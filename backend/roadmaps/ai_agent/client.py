import google.generativeai as genai
from django.conf import settings
from django.core.exceptions import ValidationError
import json
import logging

logger = logging.getLogger(__name__)

class GeminiClient:
    """Client for interacting with Gemini API"""
    
    def __init__(self):
        self.api_key = settings.GEMINI_API_KEY
        if not self.api_key:
            raise ValidationError("GEMINI_API_KEY not found in environment variables")
        
        genai.configure(api_key=self.api_key)
        self.model = genai.GenerativeModel(settings.GEMINI_MODEL)
    
    def generate_roadmap(self, prompt):
        """Generate roadmap from prompt using Gemini"""
        try:
            # Set generation config for structured output
            generation_config = {
                "temperature": 0.3,
                "top_p": 0.95,
                "top_k": 40,
                "max_output_tokens": 4096,
            }
            
            # Generate response
            response = self.model.generate_content(
                prompt,
                generation_config=generation_config
            )
            
            if not response.text:
                raise ValidationError("AI returned empty response")
            
            # Clean response text (remove markdown if any)
            cleaned_text = self._clean_response(response.text)
            
            # Parse JSON
            roadmap_data = json.loads(cleaned_text)
            
            return roadmap_data
            
        except json.JSONDecodeError as e:
            logger.error(f"JSON parsing error: {e}, Response: {response.text}")
            raise ValidationError(f"AI response was not valid JSON: {str(e)}")
        except Exception as e:
            logger.error(f"Gemini API error: {str(e)}")
            raise ValidationError(f"Failed to generate roadmap: {str(e)}")
    
    def _clean_response(self, text):
        """Clean response text by removing markdown and extra whitespace"""
        # Remove markdown code blocks
        if text.startswith("```json"):
            text = text[7:]
        elif text.startswith("```"):
            text = text[3:]
        
        if text.endswith("```"):
            text = text[:-3]
        
        return text.strip()