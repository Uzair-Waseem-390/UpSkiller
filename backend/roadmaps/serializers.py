from rest_framework import serializers
from .models import Roadmap, Phase, Topic, RoadmapStatus, TargetLevel

class TopicSerializer(serializers.ModelSerializer):
    """Serializer for topics"""
    
    class Meta:
        model = Topic
        fields = ['id', 'title', 'is_completed', 'completed_at', 'order', 'created_at', 'updated_at']
        read_only_fields = ['id', 'completed_at', 'created_at', 'updated_at']

class PhaseSerializer(serializers.ModelSerializer):
    """Serializer for phases with nested topics"""
    
    topics = TopicSerializer(many=True, read_only=True)
    completion_percentage = serializers.IntegerField(read_only=True)
    is_completed = serializers.BooleanField(read_only=True)
    
    class Meta:
        model = Phase
        fields = ['id', 'name', 'order', 'topics', 'completion_percentage', 'is_completed', 'created_at']
        read_only_fields = ['id', 'created_at']

class RoadmapSerializer(serializers.ModelSerializer):
    """Serializer for roadmaps with nested phases"""
    
    phases = PhaseSerializer(many=True, read_only=True)
    progress_percentage = serializers.IntegerField(read_only=True)
    is_completed = serializers.BooleanField(read_only=True)
    
    class Meta:
        model = Roadmap
        fields = [
            'id', 'title', 'target_skill', 'target_level', 'status',
            'phases', 'progress_percentage', 'is_completed',
            'created_at', 'updated_at', 'completed_at'
        ]
        read_only_fields = ['id', 'created_at', 'updated_at', 'completed_at']

class RoadmapCreateSerializer(serializers.Serializer):
    """Serializer for creating a new roadmap"""
    
    target_skill = serializers.CharField(max_length=100, min_length=2)
    target_level = serializers.ChoiceField(choices=TargetLevel.choices)
    
    def validate_target_skill(self, value):
        return value.strip().title()

class TopicMarkCompleteSerializer(serializers.Serializer):
    """Serializer for marking topic as complete/incomplete"""
    
    is_completed = serializers.BooleanField(required=True)

class RoadmapListSerializer(serializers.ModelSerializer):
    """Simplified serializer for listing roadmaps"""
    
    progress_percentage = serializers.IntegerField(read_only=True)
    
    class Meta:
        model = Roadmap
        fields = ['id', 'title', 'target_skill', 'target_level', 'status', 'progress_percentage', 'created_at']