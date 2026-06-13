from rest_framework import serializers
from django.contrib.auth.password_validation import validate_password
from django.core.validators import MinLengthValidator, MaxLengthValidator
from .models import User, Skill, SkillLevel
from .validators import validate_profile_picture

class UserSerializer(serializers.ModelSerializer):
    """Base user serializer"""
    
    class Meta:
        model = User
        fields = ['id', 'email', 'name', 'profile_picture', 'created_at', 'updated_at']
        read_only_fields = ['id', 'created_at', 'updated_at']

class UserProfileUpdateSerializer(serializers.ModelSerializer):
    """Serializer for updating user profile"""
    
    name = serializers.CharField(
        max_length=100,
        validators=[MinLengthValidator(2), MaxLengthValidator(100)]
    )
    profile_picture = serializers.ImageField(
        validators=[validate_profile_picture],
        required=False
    )
    
    class Meta:
        model = User
        fields = ['name', 'profile_picture']
    
    def update(self, instance, validated_data):
        instance.name = validated_data.get('name', instance.name)
        if 'profile_picture' in validated_data:
            # Delete old profile picture if exists
            if instance.profile_picture:
                instance.profile_picture.delete(save=False)
            instance.profile_picture = validated_data['profile_picture']
        instance.save()
        return instance

class RegisterSerializer(serializers.ModelSerializer):
    """User registration serializer"""
    
    password = serializers.CharField(
        write_only=True,
        required=True,
        validators=[validate_password]
    )
    password_confirm = serializers.CharField(write_only=True, required=True)
    
    class Meta:
        model = User
        fields = ['email', 'name', 'password', 'password_confirm']
    
    def validate(self, attrs):
        if attrs['password'] != attrs['password_confirm']:
            raise serializers.ValidationError({"password": "Password fields didn't match."})
        return attrs
    
    def create(self, validated_data):
        validated_data.pop('password_confirm')
        user = User.objects.create_user(
            email=validated_data['email'],
            name=validated_data['name'],
            password=validated_data['password']
        )
        return user

class SkillSerializer(serializers.ModelSerializer):
    """Skill serializer"""
    
    level_display = serializers.CharField(source='get_level_display', read_only=True)
    
    class Meta:
        model = Skill
        fields = ['id', 'skill_name', 'level', 'level_display', 'created_at', 'updated_at']
        read_only_fields = ['id', 'created_at', 'updated_at']
    
    def validate_skill_name(self, value):
        value = value.strip().title()
        if len(value) < 2:
            raise serializers.ValidationError("Skill name must be at least 2 characters long")
        if len(value) > 100:
            raise serializers.ValidationError("Skill name must be less than 100 characters")
        return value
    
    def validate_level(self, value):
        if value not in [choice[0] for choice in SkillLevel.choices]:
            raise serializers.ValidationError(f"Invalid level. Choose from: {', '.join([choice[0] for choice in SkillLevel.choices])}")
        return value

class SkillCreateUpdateSerializer(serializers.ModelSerializer):
    """Serializer for creating and updating skills"""
    
    skill_name = serializers.CharField(max_length=100)
    level = serializers.ChoiceField(choices=SkillLevel.choices)
    
    class Meta:
        model = Skill
        fields = ['skill_name', 'level']
    
    def validate_skill_name(self, value):
        value = value.strip().title()
        if len(value) < 2:
            raise serializers.ValidationError("Skill name must be at least 2 characters long")
        return value