from langgraph.graph import StateGraph, END
from django.core.exceptions import ValidationError
from .state import AgentState
from .client import GeminiClient
from .prompts import get_roadmap_generation_prompt
from .models import RoadmapOutputModel
import json
import logging

logger = logging.getLogger(__name__)

class RoadmapAgent:
    """LangGraph agent for generating personalized roadmaps"""
    
    def __init__(self):
        self.gemini_client = GeminiClient()
        self.graph = self._build_graph()
    
    def _build_graph(self):
        """Build the LangGraph workflow"""
        workflow = StateGraph(AgentState)
        
        # Add nodes
        workflow.add_node("prepare_prompt", self.prepare_prompt_node)
        workflow.add_node("call_gemini", self.call_gemini_node)
        workflow.add_node("validate_output", self.validate_output_node)
        
        # Set entry point
        workflow.set_entry_point("prepare_prompt")
        
        # Add edges
        workflow.add_edge("prepare_prompt", "call_gemini")
        workflow.add_edge("call_gemini", "validate_output")
        
        # Conditional edge from validate_output
        workflow.add_conditional_edges(
            "validate_output",
            self.should_retry,
            {
                "retry": "call_gemini",
                "end": END
            }
        )
        
        return workflow.compile()
    
    def prepare_prompt_node(self, state: AgentState) -> AgentState:
        """Prepare the prompt for AI"""
        try:
            prompt = get_roadmap_generation_prompt(
                state['user_skills'],
                state['target_skill'],
                state['target_level']
            )
            state['prompt'] = prompt
            state['error'] = None
        except Exception as e:
            state['error'] = str(e)
            logger.error(f"Error preparing prompt: {e}")
        
        return state
    
    def call_gemini_node(self, state: AgentState) -> AgentState:
        """Call Gemini API"""
        if state.get('error'):
            return state
        
        try:
            response = self.gemini_client.generate_roadmap(state['prompt'])
            state['raw_response'] = json.dumps(response)
            state['error'] = None
        except Exception as e:
            state['error'] = str(e)
            state['retry_count'] = state.get('retry_count', 0) + 1
            logger.error(f"Error calling Gemini: {e}")
        
        return state
    
    def validate_output_node(self, state: AgentState) -> AgentState:
        """Validate AI output using Pydantic"""
        if state.get('error'):
            return state
        
        try:
            # Parse raw response
            if state['raw_response']:
                response_data = json.loads(state['raw_response'])
            else:
                raise ValidationError("No response from AI")
            
            # Validate with Pydantic
            validated = RoadmapOutputModel(**response_data)
            state['validated_output'] = validated.dict()
            state['error'] = None
            
        except ValidationError as e:
            state['error'] = str(e)
            state['retry_count'] = state.get('retry_count', 0) + 1
            logger.error(f"Validation error: {e}")
        except Exception as e:
            state['error'] = f"Unexpected validation error: {str(e)}"
            state['retry_count'] = state.get('retry_count', 0) + 1
        
        return state
    
    def should_retry(self, state: AgentState) -> str:
        """Determine if we should retry on error"""
        max_retries = 1  # Only retry once
        
        if state.get('error') and state.get('retry_count', 0) <= max_retries:
            logger.info(f"Retrying roadmap generation. Retry count: {state['retry_count']}")
            return "retry"
        
        return "end"
    
    def generate_roadmap(self, user_skills, target_skill, target_level):
        """Generate roadmap using the agent"""
        initial_state = AgentState(
            user_id=0,  # Will be set by caller
            user_skills=user_skills,
            target_skill=target_skill,
            target_level=target_level,
            prompt=None,
            raw_response=None,
            validated_output=None,
            error=None,
            retry_count=0
        )
        
        # Run the graph
        final_state = self.graph.invoke(initial_state)
        
        if final_state.get('error'):
            raise ValidationError(f"Roadmap generation failed: {final_state['error']}")
        
        if not final_state.get('validated_output'):
            raise ValidationError("Failed to generate valid roadmap")
        
        return final_state['validated_output']

# Singleton instance
roadmap_agent = RoadmapAgent()