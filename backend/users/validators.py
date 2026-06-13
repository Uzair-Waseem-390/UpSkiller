from django.core.exceptions import ValidationError
from django.conf import settings
from PIL import Image
import os

def validate_profile_picture(value):
    """Validate profile picture size and format"""
    
    # Check file size
    if value.size > settings.MAX_PROFILE_PICTURE_SIZE:
        raise ValidationError(
            f"Profile picture size must be less than {settings.MAX_PROFILE_PICTURE_SIZE / (1024 * 1024)}MB"
        )
    
    # Check content type
    content_type = value.content_type
    if content_type not in settings.ALLOWED_IMAGE_TYPES:
        raise ValidationError(
            f"Invalid image type. Allowed types: {', '.join(settings.ALLOWED_IMAGE_TYPES)}"
        )
    
    # Validate image using PIL
    try:
        img = Image.open(value)
        img.verify()
    except Exception:
        raise ValidationError("Invalid image file")
    
    return value

def validate_skill_uniqueness(user, skill_name, exclude_id=None):
    """Validate that user doesn't already have this skill"""
    from .models import Skill
    
    queryset = Skill.objects.filter(user=user, skill_name__iexact=skill_name)
    if exclude_id:
        queryset = queryset.exclude(id=exclude_id)
    
    if queryset.exists():
        raise ValidationError(f"You already have a skill named '{skill_name}'")
    
    return True

def validate_skill_ownership(user, skill_id):
    """Validate that skill belongs to the user"""
    from .models import Skill
    
    try:
        skill = Skill.objects.get(id=skill_id, user=user)
        return skill
    except Skill.DoesNotExist:
        raise ValidationError("Skill not found or you don't have permission to access it")