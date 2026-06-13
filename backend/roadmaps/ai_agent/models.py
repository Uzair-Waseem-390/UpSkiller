from pydantic import BaseModel, Field, validator
from typing import List
from django.core.exceptions import ValidationError

class TopicModel(BaseModel):
    """Pydantic model for a single topic"""
    title: str = Field(..., min_length=3, max_length=300, description="Topic title")
    
    @validator('title')
    def validate_title(cls, v):
        if not v or not v.strip():
            raise ValueError('Topic title cannot be empty')
        return v.strip()

class PhaseModel(BaseModel):
    """Pydantic model for a phase containing topics"""
    name: str = Field(..., min_length=3, max_length=200, description="Phase name")
    topics: List[TopicModel] = Field(..., min_items=1, description="List of topics in this phase")
    
    @validator('name')
    def validate_name(cls, v):
        if not v or not v.strip():
            raise ValueError('Phase name cannot be empty')
        return v.strip()
    
    @validator('topics')
    def validate_topics(cls, v):
        if len(v) < 1:
            raise ValueError('Each phase must have at least one topic')
        return v

class RoadmapOutputModel(BaseModel):
    """Pydantic model for complete roadmap output from AI"""
    roadmap_title: str = Field(..., min_length=3, max_length=200, description="Title of the roadmap")
    target_level: str = Field(..., description="Target level for this roadmap")
    phases: List[PhaseModel] = Field(..., min_items=1, description="List of phases in the roadmap")
    
    @validator('roadmap_title')
    def validate_title(cls, v):
        if not v or not v.strip():
            raise ValueError('Roadmap title cannot be empty')
        return v.strip()
    
    @validator('target_level')
    def validate_target_level(cls, v):
        valid_levels = ['beginner', 'intermediate', 'advanced']
        if v.lower() not in valid_levels:
            raise ValueError(f'Target level must be one of: {", ".join(valid_levels)}')
        return v.lower()
    
    @validator('phases')
    def validate_phases(cls, v):
        if len(v) < 1:
            raise ValueError('Roadmap must have at least one phase')
        return v
    
    class Config:
        schema_extra = {
            "example": {
                "roadmap_title": "Django Mastery",
                "target_level": "advanced",
                "phases": [
                    {
                        "name": "Foundation",
                        "topics": [
                            {"title": "Django Fundamentals"},
                            {"title": "Models and Databases"}
                        ]
                    },
                    {
                        "name": "Advanced",
                        "topics": [
                            {"title": "REST Framework"},
                            {"title": "Deployment"}
                        ]
                    }
                ]
            }
        }