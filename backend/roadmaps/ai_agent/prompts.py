def get_roadmap_generation_prompt(user_skills, target_skill, target_level):
    """Generate prompt for AI to create personalized roadmap"""
    
    # Format user skills
    skills_text = ""
    if user_skills:
        skills_text = "Existing Skills:\n"
        for skill in user_skills:
            skills_text += f"- {skill['skill_name']} ({skill['level']})\n"
    else:
        skills_text = "No existing skills provided.\n"
    
    prompt = f"""You are an expert learning path creator. Create a personalized learning roadmap for {target_level} level {target_skill}.

{skills_text}

Requirements:
1. Skip topics the user already knows based on their existing skills
2. Create a logical progression from basics to {target_level} level
3. Each phase should have 3-7 topics
4. Total topics should be 10-25 depending on complexity
5. Topics should be specific and actionable
6. Do NOT include any introductory text, explanations, or conversational responses
7. Return ONLY valid JSON matching the required schema

Output Format (strict JSON):
{{
    "roadmap_title": "string (title for this roadmap)",
    "target_level": "{target_level}",
    "phases": [
        {{
            "name": "Phase Name",
            "topics": [
                {{"title": "Topic Title"}},
                {{"title": "Topic Title"}}
            ]
        }}
    ]
}}

Important: 
- Do NOT add any text before or after the JSON
- Do NOT include markdown formatting
- Do NOT explain your reasoning
- Return ONLY the JSON object"""
    
    return prompt