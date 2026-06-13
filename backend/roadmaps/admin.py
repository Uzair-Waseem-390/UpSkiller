from django.contrib import admin
from .models import Roadmap, Phase, Topic

class TopicInline(admin.TabularInline):
    model = Topic
    extra = 1
    fields = ['title', 'is_completed', 'order', 'completed_at']
    readonly_fields = ['completed_at']

class PhaseInline(admin.TabularInline):
    model = Phase
    extra = 1
    fields = ['name', 'order']
    inlines = [TopicInline]
    show_change_link = True

@admin.register(Roadmap)
class RoadmapAdmin(admin.ModelAdmin):
    list_display = ['id', 'user', 'target_skill', 'target_level', 'status', 'progress_percentage', 'created_at']
    list_filter = ['status', 'target_level', 'created_at']
    search_fields = ['target_skill', 'title', 'user__email']
    raw_id_fields = ['user']
    readonly_fields = ['progress_percentage', 'completed_at']
    inlines = [PhaseInline]
    
    fieldsets = (
        (None, {
            'fields': ('user', 'title', 'target_skill', 'target_level')
        }),
        ('Status', {
            'fields': ('status', 'completed_at')
        }),
        ('Timestamps', {
            'fields': ('created_at', 'updated_at')
        })
    )

@admin.register(Phase)
class PhaseAdmin(admin.ModelAdmin):
    list_display = ['id', 'roadmap', 'name', 'order', 'created_at']
    list_filter = ['created_at']
    search_fields = ['name', 'roadmap__target_skill']
    raw_id_fields = ['roadmap']
    inlines = [TopicInline]

@admin.register(Topic)
class TopicAdmin(admin.ModelAdmin):
    list_display = ['id', 'phase', 'title', 'is_completed', 'completed_at', 'order']
    list_filter = ['is_completed', 'created_at']
    search_fields = ['title', 'phase__name']
    raw_id_fields = ['phase']
    readonly_fields = ['completed_at']