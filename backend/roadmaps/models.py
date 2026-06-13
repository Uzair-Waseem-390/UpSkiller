from django.db import models
from django.conf import settings
from django.core.validators import MinLengthValidator, MaxLengthValidator
from django.utils import timezone

class RoadmapStatus(models.TextChoices):
    IN_PROGRESS = 'in_progress', 'In Progress'
    COMPLETED = 'completed', 'Completed'

class TargetLevel(models.TextChoices):
    BEGINNER = 'beginner', 'Beginner'
    INTERMEDIATE = 'intermediate', 'Intermediate'
    ADVANCED = 'advanced', 'Advanced'

class Roadmap(models.Model):
    """Roadmap model for storing generated learning paths"""
    
    user = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name='roadmaps'
    )
    title = models.CharField(max_length=200, validators=[MinLengthValidator(3)])
    target_skill = models.CharField(max_length=100, db_index=True)
    target_level = models.CharField(max_length=20, choices=TargetLevel.choices)
    status = models.CharField(
        max_length=20,
        choices=RoadmapStatus.choices,
        default=RoadmapStatus.IN_PROGRESS
    )
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    completed_at = models.DateTimeField(null=True, blank=True)
    
    class Meta:
        db_table = 'roadmaps'
        ordering = ['-created_at']
        indexes = [
            models.Index(fields=['user', 'status']),
            models.Index(fields=['user', 'target_skill', 'target_level']),
            models.Index(fields=['-created_at']),
        ]
        unique_together = ['user', 'target_skill', 'target_level', 'status']
    
    def __str__(self):
        return f"{self.user.email} - {self.target_skill} ({self.target_level})"
    
    @property
    def is_completed(self):
        return self.status == RoadmapStatus.COMPLETED
    
    @property
    def progress_percentage(self):
        total_topics = self.phases.aggregate(
            total=models.Count('topics')
        )['total'] or 0
        
        completed_topics = self.phases.aggregate(
            completed=models.Count('topics', filter=models.Q(topics__is_completed=True))
        )['completed'] or 0
        
        if total_topics == 0:
            return 0
        return int((completed_topics / total_topics) * 100)
    
    def mark_as_completed(self):
        """Mark roadmap as completed and set completion timestamp"""
        if not self.is_completed:
            self.status = RoadmapStatus.COMPLETED
            self.completed_at = timezone.now()
            self.save(update_fields=['status', 'completed_at', 'updated_at'])
            return True
        return False

class Phase(models.Model):
    """Phase model for grouping topics in a roadmap"""
    
    roadmap = models.ForeignKey(
        Roadmap,
        on_delete=models.CASCADE,
        related_name='phases'
    )
    name = models.CharField(max_length=200)
    order = models.PositiveIntegerField(default=0)
    created_at = models.DateTimeField(auto_now_add=True)
    
    class Meta:
        db_table = 'phases'
        ordering = ['order', 'created_at']
        unique_together = ['roadmap', 'order']
        indexes = [
            models.Index(fields=['roadmap', 'order']),
        ]
    
    def __str__(self):
        return f"{self.roadmap.title} - {self.name}"
    
    @property
    def completion_percentage(self):
        total_topics = self.topics.count()
        if total_topics == 0:
            return 0
        completed_topics = self.topics.filter(is_completed=True).count()
        return int((completed_topics / total_topics) * 100)
    
    @property
    def is_completed(self):
        return self.topics.filter(is_completed=False).count() == 0

class Topic(models.Model):
    """Topic model for individual learning items"""
    
    phase = models.ForeignKey(
        Phase,
        on_delete=models.CASCADE,
        related_name='topics'
    )
    title = models.CharField(max_length=300)
    is_completed = models.BooleanField(default=False)
    order = models.PositiveIntegerField(default=0)
    completed_at = models.DateTimeField(null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    
    class Meta:
        db_table = 'topics'
        ordering = ['order', 'created_at']
        unique_together = ['phase', 'order']
        indexes = [
            models.Index(fields=['phase', 'is_completed']),
            models.Index(fields=['is_completed', '-completed_at']),
        ]
    
    def __str__(self):
        return f"{self.phase.name} - {self.title}"
    
    def mark_as_completed(self):
        """Mark topic as completed with timestamp"""
        if not self.is_completed:
            self.is_completed = True
            self.completed_at = timezone.now()
            self.save(update_fields=['is_completed', 'completed_at', 'updated_at'])
            return True
        return False
    
    def mark_as_incomplete(self):
        """Mark topic as incomplete"""
        if self.is_completed:
            self.is_completed = False
            self.completed_at = None
            self.save(update_fields=['is_completed', 'completed_at', 'updated_at'])
            return True
        return False