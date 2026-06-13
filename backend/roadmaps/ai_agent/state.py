from typing import TypedDict, List, Dict, Any, Optional

class AgentState(TypedDict):
    """State for the LangGraph agent"""
    user_id: int
    user_skills: List[Dict[str, str]]
    target_skill: str
    target_level: str
    prompt: Optional[str]
    raw_response: Optional[str]
    validated_output: Optional[Dict[str, Any]]
    error: Optional[str]
    retry_count: int