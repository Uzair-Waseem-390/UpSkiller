from django.urls import path
from .views import (
    RegisterView, LoginView, LogoutView, RefreshTokenView,
    ProfileView, DeleteAccountView, SkillListView, SkillDetailView,
    BulkSkillCreateView, SkillStatsView
)

urlpatterns = [
    # Authentication endpoints
    path('auth/register/', RegisterView.as_view(), name='register'),
    path('auth/login/', LoginView.as_view(), name='login'),
    path('auth/logout/', LogoutView.as_view(), name='logout'),
    path('auth/refresh/', RefreshTokenView.as_view(), name='token_refresh'),
    
    # Profile endpoints
    path('profile/', ProfileView.as_view(), name='profile'),
    path('profile/delete/', DeleteAccountView.as_view(), name='delete_account'),
    
    # Skill endpoints
    path('skills/', SkillListView.as_view(), name='skill_list'),
    path('skills/bulk/', BulkSkillCreateView.as_view(), name='bulk_skill_create'),
    path('skills/stats/', SkillStatsView.as_view(), name='skill_stats'),
    path('skills/<int:pk>/', SkillDetailView.as_view(), name='skill_detail'),
]