from django.db import models
from django.contrib.auth.models import AbstractBaseUser, PermissionsMixin, BaseUserManager
from django.core.validators import MinLengthValidator, MaxLengthValidator
from django.utils import timezone
import os

class SkillLevel(models.TextChoices):
    BEGINNER = 'beginner', 'Beginner'
    INTERMEDIATE = 'intermediate', 'Intermediate'
    ADVANCED = 'advanced', 'Advanced'

class UserManager(BaseUserManager):
    """Custom user manager for email-based authentication"""
    
    def create_user(self, email, name, password=None, **extra_fields):
        if not email:
            raise ValueError('Email address is required')
        if not name:
            raise ValueError('Name is required')
        
        email = self.normalize_email(email)
        user = self.model(email=email, name=name, **extra_fields)
        user.set_password(password)
        user.save(using=self._db)
        return user
    
    def create_superuser(self, email, name, password=None, **extra_fields):
        extra_fields.setdefault('is_staff', True)
        extra_fields.setdefault('is_superuser', True)
        extra_fields.setdefault('is_active', True)
        
        if extra_fields.get('is_staff') is not True:
            raise ValueError('Superuser must have is_staff=True.')
        if extra_fields.get('is_superuser') is not True:
            raise ValueError('Superuser must have is_superuser=True.')
        
        return self.create_user(email, name, password, **extra_fields)

def profile_picture_path(instance, filename):
    """Generate file path for profile pictures"""
    ext = filename.split('.')[-1]
    filename = f"{instance.id}_{timezone.now().timestamp()}.{ext}"
    return os.path.join('profile_pictures', filename)

class User(AbstractBaseUser, PermissionsMixin):
    """Custom User model with email as username field"""
    
    email = models.EmailField(unique=True, db_index=True)
    name = models.CharField(
        max_length=100,
        validators=[MinLengthValidator(2), MaxLengthValidator(100)]
    )
    profile_picture = models.ImageField(
        upload_to=profile_picture_path,
        null=True,
        blank=True
    )
    is_active = models.BooleanField(default=True)
    is_staff = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    
    objects = UserManager()
    
    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = ['name']
    
    class Meta:
        db_table = 'users'
        ordering = ['-created_at']
        indexes = [
            models.Index(fields=['email']),
            models.Index(fields=['-created_at']),
        ]
    
    def __str__(self):
        return self.email
    
    @property
    def full_name(self):
        return self.name
    
    def get_skills_count(self):
        return self.skills.count()

class Skill(models.Model):
    """User skills model"""
    
    user = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='skills'
    )
    skill_name = models.CharField(max_length=100, db_index=True)
    level = models.CharField(max_length=20, choices=SkillLevel.choices)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    
    class Meta:
        db_table = 'skills'
        unique_together = ['user', 'skill_name']
        ordering = ['skill_name']
        indexes = [
            models.Index(fields=['user', 'skill_name']),
            models.Index(fields=['skill_name']),
        ]
    
    def __str__(self):
        return f"{self.user.email} - {self.skill_name} ({self.level})"