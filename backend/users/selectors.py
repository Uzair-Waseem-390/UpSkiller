from django.db.models import Q
from .models import User, Skill

class UserSelector:
    """Selector class for fetching user data"""
    
    @staticmethod
    def get_user_by_id(user_id):
        """Get user by ID"""
        try:
            return User.objects.get(id=user_id, is_active=True)
        except User.DoesNotExist:
            return None
    
    @staticmethod
    def get_user_by_email(email):
        """Get user by email"""
        try:
            return User.objects.get(email__iexact=email, is_active=True)
        except User.DoesNotExist:
            return None

class SkillSelector:
    """Selector class for fetching skills data"""
    
    @staticmethod
    def get_user_skills(user, skill_name=None):
        """Get all skills for a user with optional filtering"""
        queryset = Skill.objects.filter(user=user)
        if skill_name:
            queryset = queryset.filter(skill_name__icontains=skill_name)
        return queryset
    
    @staticmethod
    def get_user_skill_by_id(user, skill_id):
        """Get specific skill by ID"""
        try:
            return Skill.objects.get(id=skill_id, user=user)
        except Skill.DoesNotExist:
            return None
    
    @staticmethod
    def get_skill_count_by_level(user):
        """Get count of skills grouped by level"""
        from django.db.models import Count
        
        return Skill.objects.filter(user=user).values('level').annotate(
            count=Count('id')
        )