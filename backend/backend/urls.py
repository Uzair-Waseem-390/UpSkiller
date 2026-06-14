from django.contrib import admin
from django.urls import path, include
from django.conf import settings
from django.conf.urls.static import static
from rest_framework.response import Response
from rest_framework.permissions import AllowAny
from rest_framework.decorators import api_view, permission_classes

# here define the rool url function

@api_view(['GET'])
@permission_classes([AllowAny])
def root_url(request):
    return Response({
        'message': 'Welcome to UpSkiller API'
    })


urlpatterns = [
    path('', root_url),
    path('admin/', admin.site.urls),
    path('api/users/', include('users.urls')),
    path('api/roadmaps/', include('roadmaps.urls')),
]

# Serve media files in development
if settings.DEBUG:
    urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)