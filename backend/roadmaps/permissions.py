from rest_framework import permissions

class IsRoadmapOwner(permissions.BasePermission):
    """Permission to only allow owners of a roadmap to access it"""
    
    def has_object_permission(self, request, view, obj):
        # obj can be Roadmap, Phase, or Topic
        if hasattr(obj, 'roadmap'):
            return obj.roadmap.user == request.user
        elif hasattr(obj, 'phase'):
            return obj.phase.roadmap.user == request.user
        elif hasattr(obj, 'user'):
            return obj.user == request.user
        return obj.user == request.user

class CanModifyRoadmap(permissions.BasePermission):
    """Permission to modify roadmap (only if not completed)"""
    
    def has_object_permission(self, request, view, obj):
        # For read operations
        if request.method in permissions.SAFE_METHODS:
            if hasattr(obj, 'roadmap'):
                return obj.roadmap.user == request.user and not obj.roadmap.is_completed
            elif hasattr(obj, 'phase'):
                return obj.phase.roadmap.user == request.user and not obj.phase.roadmap.is_completed
            return obj.user == request.user and not obj.is_completed
        
        # For write operations
        if hasattr(obj, 'roadmap'):
            return obj.roadmap.user == request.user and not obj.roadmap.is_completed
        elif hasattr(obj, 'phase'):
            return obj.phase.roadmap.user == request.user and not obj.phase.roadmap.is_completed
        return obj.user == request.user and not obj.is_completed