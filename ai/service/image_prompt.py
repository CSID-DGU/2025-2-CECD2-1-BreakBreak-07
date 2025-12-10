def refine_image_prompt(raw_prompt: str, character: dict) -> str:
    character_context = ""
    if "name" in character and "description" in character:
        character_context = f"{character['name']} who is {character['description']}"

    if character_context:
        return (
            f"A detailed illustration of the scene: \"{raw_prompt}\". "
            f"Featuring {character_context}. "
            f"Children's book style, warm atmosphere"
        )
    else:
        return (
            f"A detailed illustration of the scene: \"{raw_prompt}\". "
            f"Children's book style, warm atmosphere"
        )



def generate_final_image_prompts(raw_prompts: list[str], character: dict) -> list[str]:
    return [refine_image_prompt(p, character) for p in raw_prompts]
