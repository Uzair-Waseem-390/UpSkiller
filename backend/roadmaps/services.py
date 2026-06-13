from django.db import transaction
from django.core.exceptions import ValidationError
from django.utils import timezone
from .models import Roadmap, Phase, Topic, RoadmapStatus
from .validators import (
    validate_no_duplicate_roadmap,
    validate_skill_not_already_owned,
    validate_roadmap_ownership,
    validate_topic_ownership,
    validate_roadmap_not_completed,
    validate_max_roadmaps_per_user
)
from .selectors import RoadmapSelector
from .ai_agent.graph import roadmap_agent
from users.services import SkillService
from users.selectors import SkillSelector

class RoadmapService:
    """Service class for roadmap-related business logic"""
    
    @staticmethod
    @transaction.atomic
    def create_roadmap(user, target_skill, target_level):
        """Generate and create a new personalized roadmap"""
        
        # Validate pre-conditions
        validate_max_roadmaps_per_user(user)
        validate_no_duplicate_roadmap(user, target_skill, target_level)
        validate_skill_not_already_owned(user, target_skill, target_level)
        
        # Get user's existing skills
        user_skills = SkillSelector.get_user_skills(user).values('skill_name', 'level')
        user_skills_list = list(user_skills)
        
        # Generate roadmap using AI agent
        try:
            ai_output = roadmap_agent.generate_roadmap(
                user_skills=user_skills_list,
                target_skill=target_skill,
                target_level=target_level
            )
        except Exception as e:
            raise ValidationError(f"AI roadmap generation failed: {str(e)}")
        
        # Create roadmap
        roadmap = Roadmap.objects.create(
            user=user,
            title=ai_output['roadmap_title'],
            target_skill=target_skill,
            target_level=target_level,
            status=RoadmapStatus.IN_PROGRESS
        )
        
        # Create phases and topics
        for phase_index, phase_data in enumerate(ai_output['phases']):
            phase = Phase.objects.create(
                roadmap=roadmap,
                name=phase_data['name'],
                order=phase_index
            )
            
            for topic_index, topic_data in enumerate(phase_data['topics']):
                Topic.objects.create(
                    phase=phase,
                    title=topic_data['title'],
                    order=topic_index
                )
        
        return roadmap
    
    @staticmethod
    @transaction.atomic
    def delete_roadmap(user, roadmap_id):
        """Delete a roadmap entirely"""
        roadmap = validate_roadmap_ownership(user, roadmap_id)
        roadmap.delete()
        return True
    
    @staticmethod
    @transaction.atomic
    def mark_topic_complete(user, topic_id, is_completed):
        """Mark a topic as completed or incomplete"""
        
        topic = validate_topic_ownership(user, topic_id)
        roadmap = topic.phase.roadmap
        
        # Validate roadmap is not completed
        validate_roadmap_not_completed(roadmap)
        
        # Update topic status
        if is_completed:
            topic.mark_as_completed()
        else:
            topic.mark_as_incomplete()
        
        # Check if all topics in roadmap are completed
        all_topics = Topic.objects.filter(phase__roadmap=roadmap)
        completed_topics = all_topics.filter(is_completed=True)
        
        if all_topics.count() == completed_topics.count():
            # All topics completed - mark roadmap as completed
            roadmap.mark_as_completed()
            
            # Auto-add skill to user profile
            try:
                SkillService.create_skill(
                    user,
                    roadmap.target_skill,
                    roadmap.target_level
                )
            except Exception as e:
                # Log error but don't rollback roadmap completion
                print(f"Failed to auto-add skill: {e}")
        
        return topic
    
    @staticmethod
    def get_roadmap_progress(user, roadmap_id):
        """Get detailed progress for a roadmap"""
        roadmap = validate_roadmap_ownership(user, roadmap_id)
        
        return {
            'total_topics': roadmap.phases.aggregate(total=models.Count('topics'))['total'] or 0,
            'completed_topics': roadmap.phases.aggregate(
                completed=models.Count('topics', filter=models.Q(topics__is_completed=True))
            )['completed'] or 0,
            'progress_percentage': roadmap.progress_percentage,
            'phases_progress': [
                {
                    'phase_id': phase.id,
                    'name': phase.name,
                    'completed_topics': phase.topics.filter(is_completed=True).count(),
                    'total_topics': phase.topics.count(),
                    'percentage': phase.completion_percentage
                }
                for phase in roadmap.phases.all()
            ]
        }

# Import models for aggregation (to avoid circular import)
from django.db import models