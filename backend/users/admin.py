from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from django.utils.translation import gettext_lazy as _
from .models import User, Skill

class SkillInline(admin.TabularInline):
    model = Skill
    extra = 1
    fields = ['skill_name', 'level']
    show_change_link = True

@admin.register(User)
class UserAdmin(BaseUserAdmin):
    list_display = ['id', 'email', 'name', 'is_active', 'is_staff', 'created_at']
    list_filter = ['is_active', 'is_staff', 'created_at']
    search_fields = ['email', 'name']
    ordering = ['-created_at']
    
    fieldsets = (
        (None, {'fields': ('email', 'password')}),
        (_('Personal info'), {'fields': ('name', 'profile_picture')}),
        (_('Permissions'), {'fields': ('is_active', 'is_staff', 'is_superuser', 'groups', 'user_permissions')}),
        (_('Important dates'), {'fields': ('last_login', 'created_at', 'updated_at')}),
    )
    
    add_fieldsets = (
        (None, {
            'classes': ('wide',),
            'fields': ('email', 'name', 'password1', 'password2'),
        }),
    )
    
    readonly_fields = ['created_at', 'updated_at']
    inlines = [SkillInline]

@admin.register(Skill)
class SkillAdmin(admin.ModelAdmin):
    list_display = ['id', 'user', 'skill_name', 'level', 'created_at']
    list_filter = ['level', 'created_at']
    search_fields = ['skill_name', 'user__email', 'user__name']
    ordering = ['-created_at']
    raw_id_fields = ['user']