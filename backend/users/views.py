from rest_framework import status, generics, permissions
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework_simplejwt.views import TokenObtainPairView
from rest_framework_simplejwt.tokens import RefreshToken
from django.contrib.auth import authenticate
from django.core.exceptions import ValidationError
from .models import User, Skill
from .serializers import (
    UserSerializer, RegisterSerializer, SkillSerializer,
    SkillCreateUpdateSerializer, UserProfileUpdateSerializer
)
from .permissions import IsOwner, CanManageSkills
from .services import UserService, SkillService
from .selectors import UserSelector, SkillSelector

class RegisterView(APIView):
    """User registration endpoint"""
    permission_classes = [permissions.AllowAny]
    
    def post(self, request):
        serializer = RegisterSerializer(data=request.data)
        if serializer.is_valid():
            user = serializer.save()
            tokens = UserService.get_tokens_for_user(user)
            
            return Response({
                'user': UserSerializer(user).data,
                'tokens': tokens,
                'message': 'Registration successful. Please add your skills.'
            }, status=status.HTTP_201_CREATED)
        
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class LoginView(TokenObtainPairView):
    """User login endpoint"""
    permission_classes = [permissions.AllowAny]
    
    def post(self, request, *args, **kwargs):
        email = request.data.get('email')
        password = request.data.get('password')
        
        if not email or not password:
            return Response({
                'error': 'Please provide both email and password'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        user = authenticate(request, username=email, password=password)
        
        if user is None:
            return Response({
                'error': 'Invalid credentials'
            }, status=status.HTTP_401_UNAUTHORIZED)
        
        if not user.is_active:
            return Response({
                'error': 'Account is deactivated'
            }, status=status.HTTP_401_UNAUTHORIZED)
        
        tokens = UserService.get_tokens_for_user(user)
        
        return Response({
            'user': UserSerializer(user).data,
            'tokens': tokens,
            'message': 'Login successful'
        }, status=status.HTTP_200_OK)

class LogoutView(APIView):
    """User logout endpoint - blacklists refresh token"""
    permission_classes = [permissions.IsAuthenticated]
    
    def post(self, request):
        try:
            refresh_token = request.data.get('refresh_token')
            if not refresh_token:
                return Response({
                    'error': 'Refresh token is required'
                }, status=status.HTTP_400_BAD_REQUEST)
            
            UserService.logout_user(refresh_token)
            return Response({
                'message': 'Successfully logged out'
            }, status=status.HTTP_200_OK)
        except Exception as e:
            return Response({
                'error': str(e)
            }, status=status.HTTP_400_BAD_REQUEST)

class RefreshTokenView(APIView):
    """Refresh access token endpoint"""
    permission_classes = [permissions.AllowAny]
    
    def post(self, request):
        refresh_token = request.data.get('refresh_token')
        if not refresh_token:
            return Response({
                'error': 'Refresh token is required'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        try:
            refresh = RefreshToken(refresh_token)
            return Response({
                'access': str(refresh.access_token),
                'refresh': str(refresh)
            }, status=status.HTTP_200_OK)
        except Exception as e:
            return Response({
                'error': 'Invalid or expired refresh token'
            }, status=status.HTTP_401_UNAUTHORIZED)

class ProfileView(generics.RetrieveUpdateAPIView):
    """View and update user profile"""
    serializer_class = UserSerializer
    permission_classes = [permissions.IsAuthenticated, IsOwner]
    
    def get_object(self):
        return self.request.user
    
    def get_serializer_class(self):
        if self.request.method in ['PUT', 'PATCH']:
            return UserProfileUpdateSerializer
        return UserSerializer
    
    def update(self, request, *args, **kwargs):
        partial = kwargs.pop('partial', False)
        instance = self.get_object()
        
        try:
            user = UserService.update_user_profile(instance, request.data)
            return Response({
                'user': UserSerializer(user).data,
                'message': 'Profile updated successfully'
            }, status=status.HTTP_200_OK)
        except ValidationError as e:
            return Response({'error': str(e)}, status=status.HTTP_400_BAD_REQUEST)

class DeleteAccountView(APIView):
    """Delete user account (soft delete)"""
    permission_classes = [permissions.IsAuthenticated]
    
    def delete(self, request):
        UserService.delete_user_account(request.user)
        return Response({
            'message': 'Account deleted successfully'
        }, status=status.HTTP_200_OK)

class SkillListView(generics.ListCreateAPIView):
    """List all skills for current user or create new skill"""
    serializer_class = SkillSerializer
    permission_classes = [permissions.IsAuthenticated, CanManageSkills]
    
    def get_queryset(self):
        return SkillSelector.get_user_skills(self.request.user)
    
    def get_serializer_class(self):
        if self.request.method == 'POST':
            return SkillCreateUpdateSerializer
        return SkillSerializer
    
    def perform_create(self, serializer):
        try:
            skill = SkillService.create_skill(
                self.request.user,
                serializer.validated_data['skill_name'],
                serializer.validated_data['level']
            )
            return skill
        except ValidationError as e:
            from rest_framework.exceptions import ValidationError as DRFValidationError
            raise DRFValidationError({'error': str(e)})
    
    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        
        try:
            skill = SkillService.create_skill(
                request.user,
                serializer.validated_data['skill_name'],
                serializer.validated_data['level']
            )
            return Response({
                'skill': SkillSerializer(skill).data,
                'message': 'Skill added successfully'
            }, status=status.HTTP_201_CREATED)
        except ValidationError as e:
            return Response({'error': str(e)}, status=status.HTTP_400_BAD_REQUEST)

class SkillDetailView(generics.RetrieveUpdateDestroyAPIView):
    """Retrieve, update or delete a specific skill"""
    serializer_class = SkillSerializer
    permission_classes = [permissions.IsAuthenticated, CanManageSkills]
    
    def get_queryset(self):
        return Skill.objects.filter(user=self.request.user)
    
    def get_serializer_class(self):
        if self.request.method in ['PUT', 'PATCH']:
            return SkillCreateUpdateSerializer
        return SkillSerializer
    
    def update(self, request, *args, **kwargs):
        partial = kwargs.pop('partial', False)
        skill = self.get_object()
        
        try:
            updated_skill = SkillService.update_skill(
                request.user,
                skill.id,
                skill_name=request.data.get('skill_name'),
                level=request.data.get('level')
            )
            return Response({
                'skill': SkillSerializer(updated_skill).data,
                'message': 'Skill updated successfully'
            }, status=status.HTTP_200_OK)
        except ValidationError as e:
            return Response({'error': str(e)}, status=status.HTTP_400_BAD_REQUEST)
    
    def destroy(self, request, *args, **kwargs):
        skill = self.get_object()
        SkillService.delete_skill(request.user, skill.id)
        return Response({
            'message': 'Skill deleted successfully'
        }, status=status.HTTP_200_OK)

class BulkSkillCreateView(APIView):
    """Create multiple skills at once"""
    permission_classes = [permissions.IsAuthenticated]
    
    def post(self, request):
        skills_data = request.data.get('skills', [])
        
        if not skills_data:
            return Response({
                'error': 'Please provide skills data'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        result = SkillService.bulk_create_skills(request.user, skills_data)
        
        response_data = {
            'created': SkillSerializer(result['created'], many=True).data,
            'message': f"{len(result['created'])} skills created successfully"
        }
        
        if result['errors']:
            response_data['errors'] = result['errors']
        
        return Response(response_data, status=status.HTTP_201_CREATED)

class SkillStatsView(APIView):
    """Get skill statistics for current user"""
    permission_classes = [permissions.IsAuthenticated]
    
    def get(self, request):
        stats = SkillSelector.get_skill_count_by_level(request.user)
        total_skills = SkillSelector.get_user_skills(request.user).count()
        
        return Response({
            'total_skills': total_skills,
            'by_level': stats,
            'levels': {
                'beginner': next((item['count'] for item in stats if item['level'] == 'beginner'), 0),
                'intermediate': next((item['count'] for item in stats if item['level'] == 'intermediate'), 0),
                'advanced': next((item['count'] for item in stats if item['level'] == 'advanced'), 0),
            }
        }, status=status.HTTP_200_OK)