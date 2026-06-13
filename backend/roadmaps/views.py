from rest_framework import status, generics, permissions
from rest_framework.response import Response
from rest_framework.views import APIView
from django.core.exceptions import ValidationError
from .models import Roadmap, Topic, RoadmapStatus
from .serializers import (
    RoadmapSerializer, RoadmapCreateSerializer, 
    TopicMarkCompleteSerializer, RoadmapListSerializer
)
from .services import RoadmapService
from .selectors import RoadmapSelector, TopicSelector
from .permissions import IsRoadmapOwner, CanModifyRoadmap
from .validators import validate_topic_ownership

class RoadmapCreateView(APIView):
    """Create a new personalized roadmap"""
    permission_classes = [permissions.IsAuthenticated]
    
    def post(self, request):
        serializer = RoadmapCreateSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        
        try:
            roadmap = RoadmapService.create_roadmap(
                user=request.user,
                target_skill=serializer.validated_data['target_skill'],
                target_level=serializer.validated_data['target_level']
            )
            
            return Response({
                'roadmap': RoadmapSerializer(roadmap).data,
                'message': 'Roadmap created successfully'
            }, status=status.HTTP_201_CREATED)
            
        except ValidationError as e:
            return Response({'error': str(e)}, status=status.HTTP_400_BAD_REQUEST)
        except Exception as e:
            return Response({'error': f'Unexpected error: {str(e)}'}, 
                          status=status.HTTP_500_INTERNAL_SERVER_ERROR)

class RoadmapListView(generics.ListAPIView):
    """List all roadmaps for current user"""
    serializer_class = RoadmapListSerializer
    permission_classes = [permissions.IsAuthenticated]
    
    def get_queryset(self):
        status_filter = self.request.query_params.get('status')
        if status_filter:
            return RoadmapSelector.get_user_roadmaps(self.request.user, status_filter)
        return RoadmapSelector.get_user_roadmaps(self.request.user)

class RoadmapDetailView(generics.RetrieveAPIView):
    """Get detailed roadmap with all phases and topics"""
    serializer_class = RoadmapSerializer
    permission_classes = [permissions.IsAuthenticated, IsRoadmapOwner]
    
    def get_queryset(self):
        return Roadmap.objects.filter(user=self.request.user)

class RoadmapDeleteView(APIView):
    """Delete a roadmap entirely"""
    permission_classes = [permissions.IsAuthenticated, IsRoadmapOwner]
    
    def delete(self, request, roadmap_id):
        try:
            RoadmapService.delete_roadmap(request.user, roadmap_id)
            return Response({
                'message': 'Roadmap deleted successfully'
            }, status=status.HTTP_200_OK)
        except ValidationError as e:
            return Response({'error': str(e)}, status=status.HTTP_400_BAD_REQUEST)

class TopicMarkCompleteView(APIView):
    """Mark a topic as complete or incomplete"""
    permission_classes = [permissions.IsAuthenticated, CanModifyRoadmap]
    
    def post(self, request, topic_id):
        serializer = TopicMarkCompleteSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        
        try:
            topic = RoadmapService.mark_topic_complete(
                user=request.user,
                topic_id=topic_id,
                is_completed=serializer.validated_data['is_completed']
            )
            
            # Get updated roadmap for response
            roadmap = topic.phase.roadmap
            
            return Response({
                'topic': {
                    'id': topic.id,
                    'title': topic.title,
                    'is_completed': topic.is_completed,
                    'completed_at': topic.completed_at
                },
                'roadmap': RoadmapSerializer(roadmap).data,
                'message': f"Topic marked as {'completed' if topic.is_completed else 'incomplete'}"
            }, status=status.HTTP_200_OK)
            
        except ValidationError as e:
            return Response({'error': str(e)}, status=status.HTTP_400_BAD_REQUEST)

class RoadmapProgressView(APIView):
    """Get detailed progress for a roadmap"""
    permission_classes = [permissions.IsAuthenticated, IsRoadmapOwner]
    
    def get(self, request, roadmap_id):
        try:
            progress = RoadmapService.get_roadmap_progress(request.user, roadmap_id)
            return Response(progress, status=status.HTTP_200_OK)
        except ValidationError as e:
            return Response({'error': str(e)}, status=status.HTTP_400_BAD_REQUEST)

class ActiveRoadmapsView(generics.ListAPIView):
    """Get only active (in-progress) roadmaps"""
    serializer_class = RoadmapListSerializer
    permission_classes = [permissions.IsAuthenticated]
    
    def get_queryset(self):
        return RoadmapSelector.get_active_roadmaps(self.request.user)

class CompletedRoadmapsView(generics.ListAPIView):
    """Get only completed roadmaps"""
    serializer_class = RoadmapListSerializer
    permission_classes = [permissions.IsAuthenticated]
    
    def get_queryset(self):
        return RoadmapSelector.get_completed_roadmaps(self.request.user)