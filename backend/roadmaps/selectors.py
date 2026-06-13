from django.db.models import Q, Prefetch
from .models import Roadmap, Phase, Topic, RoadmapStatus

class RoadmapSelector:
    """Selector class for fetching roadmap data"""
    
    @staticmethod
    def get_user_roadmaps(user, status=None):
        """Get all roadmaps for a user with optional status filter"""
        queryset = Roadmap.objects.filter(user=user)
        
        if status:
            queryset = queryset.filter(status=status)
        
        return queryset
    
    @staticmethod
    def get_roadmap_with_details(roadmap_id, user):
        """Get roadmap with prefetched phases and topics"""
        try:
            return Roadmap.objects.filter(id=roadmap_id, user=user).prefetch_related(
                Prefetch('phases', queryset=Phase.objects.prefetch_related('topics'))
            ).first()
        except Roadmap.DoesNotExist:
            return None
    
    @staticmethod
    def get_active_roadmaps(user):
        """Get only in-progress roadmaps"""
        return Roadmap.objects.filter(user=user, status=RoadmapStatus.IN_PROGRESS)
    
    @staticmethod
    def get_completed_roadmaps(user):
        """Get only completed roadmaps"""
        return Roadmap.objects.filter(user=user, status=RoadmapStatus.COMPLETED)

class TopicSelector:
    """Selector class for fetching topic data"""
    
    @staticmethod
    def get_user_topics(user, roadmap_id=None, completed=None):
        """Get topics for user with optional filtering"""
        queryset = Topic.objects.filter(phase__roadmap__user=user)
        
        if roadmap_id:
            queryset = queryset.filter(phase__roadmap_id=roadmap_id)
        
        if completed is not None:
            queryset = queryset.filter(is_completed=completed)
        
        return queryset
    
    @staticmethod
    def get_topic_completion_stats(user):
        """Get topic completion statistics for user"""
        from django.db.models import Count, Q
        
        return Topic.objects.filter(phase__roadmap__user=user).aggregate(
            total=Count('id'),
            completed=Count('id', filter=Q(is_completed=True)),
            pending=Count('id', filter=Q(is_completed=False))
        )