from django.core.exceptions import ValidationError
from django.conf import settings
from .models import Roadmap, TargetLevel

def validate_no_duplicate_roadmap(user, target_skill, target_level):
    """Validate that user doesn't have in-progress roadmap for same skill and level"""
    
    existing_roadmap = Roadmap.objects.filter(
        user=user,
        target_skill__iexact=target_skill,
        target_level=target_level,
        status='in_progress'
    ).exists()
    
    if existing_roadmap:
        raise ValidationError(
            f"You already have an in-progress roadmap for '{target_skill}' at {target_level} level. "
            "Please complete or delete it before creating a new one."
        )
    
    return True

def validate_skill_not_already_owned(user, target_skill, target_level):
    """Validate that user doesn't already have this skill at same or higher level"""
    
    # Check if user already has this skill at the same or higher level
    existing_skill = user.skills.filter(
        skill_name__iexact=target_skill
    ).first()
    
    if existing_skill:
        levels = ['beginner', 'intermediate', 'advanced']
        current_level_index = levels.index(existing_skill.level)
        target_level_index = levels.index(target_level)
        
        if target_level_index <= current_level_index:
            raise ValidationError(
                f"You already have '{target_skill}' at {existing_skill.level} level. "
                f"Cannot create roadmap for {target_level} level as it's not higher than your current level."
            )
    
    return True

def validate_roadmap_ownership(user, roadmap_id):
    """Validate that roadmap belongs to user"""
    try:
        roadmap = Roadmap.objects.get(id=roadmap_id, user=user)
        return roadmap
    except Roadmap.DoesNotExist:
        raise ValidationError("Roadmap not found or you don't have permission to access it")

def validate_topic_ownership(user, topic_id):
    """Validate that topic belongs to user's roadmap"""
    from .models import Topic
    
    try:
        topic = Topic.objects.select_related('phase__roadmap').get(
            id=topic_id,
            phase__roadmap__user=user
        )
        return topic
    except Topic.DoesNotExist:
        raise ValidationError("Topic not found or you don't have permission to access it")

def validate_roadmap_not_completed(roadmap):
    """Validate that roadmap is not completed"""
    if roadmap.is_completed:
        raise ValidationError("Cannot modify a completed roadmap")

def validate_max_roadmaps_per_user(user):
    """Validate user hasn't exceeded maximum roadmaps limit"""
    active_roadmaps_count = Roadmap.objects.filter(
        user=user,
        status='in_progress'
    ).count()
    
    if active_roadmaps_count >= settings.MAX_ROADMAPS_PER_USER:
        raise ValidationError(
            f"You have reached the maximum limit of {settings.MAX_ROADMAPS_PER_USER} active roadmaps. "
            "Please complete or delete some existing roadmaps before creating new ones."
        )
    
    return True