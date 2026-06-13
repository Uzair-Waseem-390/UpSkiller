from rest_framework import permissions

class IsOwnerOrReadOnly(permissions.BasePermission):
    """
    Custom permission to only allow owners of an object to edit it.
    """
    
    def has_object_permission(self, request, view, obj):
        # Read permissions are allowed to any request
        if request.method in permissions.SAFE_METHODS:
            return True
        
        # Write permissions are only allowed to the owner
        # Check if obj has 'user' attribute (for Skill) or is User instance
        if hasattr(obj, 'user'):
            return obj.user == request.user
        return obj == request.user

class IsOwner(permissions.BasePermission):
    """
    Permission to only allow owners to access the object
    """
    
    def has_object_permission(self, request, view, obj):
        if hasattr(obj, 'user'):
            return obj.user == request.user
        return obj == request.user

class CanManageSkills(permissions.BasePermission):
    """
    Permission to manage skills (owner only)
    """
    
    def has_permission(self, request, view):
        # For list/create operations, check if user is authenticated
        if request.method == 'POST':
            return request.user and request.user.is_authenticated
        return True
    
    def has_object_permission(self, request, view, obj):
        # For update/delete operations, check if skill belongs to user
        return obj.user == request.user