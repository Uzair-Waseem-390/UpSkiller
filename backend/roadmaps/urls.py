from django.urls import path
from .views import (
    RoadmapCreateView, RoadmapListView, RoadmapDetailView,
    RoadmapDeleteView, TopicMarkCompleteView, RoadmapProgressView,
    ActiveRoadmapsView, CompletedRoadmapsView
)

urlpatterns = [
    # Roadmap endpoints
    path('create/', RoadmapCreateView.as_view(), name='roadmap_create'),
    path('list/', RoadmapListView.as_view(), name='roadmap_list'),
    path('active/', ActiveRoadmapsView.as_view(), name='active_roadmaps'),
    path('completed/', CompletedRoadmapsView.as_view(), name='completed_roadmaps'),
    path('<int:pk>/', RoadmapDetailView.as_view(), name='roadmap_detail'),
    path('<int:roadmap_id>/delete/', RoadmapDeleteView.as_view(), name='roadmap_delete'),
    path('<int:roadmap_id>/progress/', RoadmapProgressView.as_view(), name='roadmap_progress'),
    
    # Topic endpoints
    path('topic/<int:topic_id>/complete/', TopicMarkCompleteView.as_view(), name='topic_complete'),
]