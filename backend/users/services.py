from django.core.exceptions import ValidationError
from django.db import transaction
from rest_framework_simplejwt.tokens import RefreshToken
from .models import User, Skill, SkillLevel
from .validators import validate_skill_uniqueness, validate_skill_ownership
from .selectors import UserSelector, SkillSelector

class UserService:
    """Service class for user-related business logic"""
    
    @staticmethod
    @transaction.atomic
    def create_user(email, name, password):
        """Create a new user"""
        from .serializers import RegisterSerializer
        
        serializer = RegisterSerializer(data={
            'email': email,
            'name': name,
            'password': password,
            'password_confirm': password
        })
        serializer.is_valid(raise_exception=True)
        return serializer.save()
    
    @staticmethod
    def update_user_profile(user, data):
        """Update user profile"""
        from .serializers import UserProfileUpdateSerializer
        
        serializer = UserProfileUpdateSerializer(instance=user, data=data, partial=True)
        serializer.is_valid(raise_exception=True)
        return serializer.save()
    
    @staticmethod
    def delete_user_account(user):
        """Soft delete user account"""
        user.is_active = False
        user.save()
        return True
    
    @staticmethod
    def get_tokens_for_user(user):
        """Generate JWT tokens for user"""
        refresh = RefreshToken.for_user(user)
        return {
            'refresh': str(refresh),
            'access': str(refresh.access_token),
        }
    
    @staticmethod
    def logout_user(refresh_token):
        """Blacklist refresh token for logout"""
        try:
            token = RefreshToken(refresh_token)
            token.blacklist()
            return True
        except Exception:
            return False

class SkillService:
    """Service class for skill-related business logic"""
    
    @staticmethod
    @transaction.atomic
    def create_skill(user, skill_name, level):
        """Create a new skill for user"""
        from .serializers import SkillCreateUpdateSerializer
        
        # Validate uniqueness
        validate_skill_uniqueness(user, skill_name)
        
        # Validate level
        if level not in [choice[0] for choice in SkillLevel.choices]:
            raise ValidationError(f"Invalid level. Choose from: {', '.join([choice[0] for choice in SkillLevel.choices])}")
        
        serializer = SkillCreateUpdateSerializer(data={
            'skill_name': skill_name,
            'level': level
        })
        serializer.is_valid(raise_exception=True)
        
        skill = Skill.objects.create(
            user=user,
            skill_name=serializer.validated_data['skill_name'].title(),
            level=serializer.validated_data['level']
        )
        return skill
    
    @staticmethod
    @transaction.atomic
    def update_skill(user, skill_id, skill_name=None, level=None):
        """Update an existing skill"""
        skill = validate_skill_ownership(user, skill_id)
        
        if skill_name:
            skill_name = skill_name.strip().title()
            # Check uniqueness excluding current skill
            validate_skill_uniqueness(user, skill_name, exclude_id=skill_id)
            skill.skill_name = skill_name
        
        if level:
            if level not in [choice[0] for choice in SkillLevel.choices]:
                raise ValidationError(f"Invalid level. Choose from: {', '.join([choice[0] for choice in SkillLevel.choices])}")
            skill.level = level
        
        skill.save()
        return skill
    
    @staticmethod
    @transaction.atomic
    def delete_skill(user, skill_id):
        """Delete a skill"""
        skill = validate_skill_ownership(user, skill_id)
        skill.delete()
        return True
    
    @staticmethod
    def bulk_create_skills(user, skills_data):
        """Create multiple skills at once"""
        created_skills = []
        errors = []
        
        for skill_data in skills_data:
            try:
                skill = SkillService.create_skill(
                    user,
                    skill_data.get('skill_name'),
                    skill_data.get('level')
                )
                created_skills.append(skill)
            except ValidationError as e:
                errors.append({
                    'skill_name': skill_data.get('skill_name'),
                    'error': str(e)
                })
        
        return {
            'created': created_skills,
            'errors': errors
        }