def generate_tale_prompt(topic: str, age: int) -> str:
    return (
        f"You are an API that only returns pure JSON.\n"
        f"Create a children's story for a {age}-year-old.\n"
        f"The theme is '{topic}'.\n"
        f"The entire story must consist of exactly 10 sentences. These sentences will be grouped into 5 parts.\n"
        f"Each of these 5 parts will form a single string element in the 'contents' array, containing exactly 2 sentences.\n"
        f"Each sentence within a part must be clearly separated by a period ('.').\n"
        f"In the 'character', describe the main character with physical details (e.g., color, size, unique features) that would be helpful for drawing them consistently across scenes.\n"
        "Generate exactly 4 quiz questions based on the story.\n"
        "Each quiz must have exactly 4 answer choices, all of which are strings.\n"
        "In the 'explanation' field, explain why the correct answer is correct.\n"
        "Respond only in this JSON format:\n"
        "{\n"
        '  "title": "string",\n'
        '  "character": [\n'
        '    {\n'
        '      "name": "string",\n'
        '      "description": "string"\n'
        '    }\n'
        '  ],\n'
        '  "contents": ["string", "string", "string", "string", "string"],\n'
        '  "quizzes": [\n'
        "    {\n"
        '      "questionNumber": 1,\n'
        '      "question": "string",\n'
        '      "choices": ["string", "string", "string", "string"],\n'
        '      "answerIndex": int,\n'
        '      "explanation": "string (explain why the answer is correct)"\n'
        "    },\n"
        "    {\n"
        '      "questionNumber": 2,\n'
        '      "question": "string",\n'
        '      "choices": ["string", "string", "string", "string"],\n'
        '      "answerIndex": int,\n'
        '      "explanation": "string (explain why the answer is correct)"\n'
        "    },\n"
        "    {\n"
        '      "questionNumber": 3,\n'
        '      "question": "string",\n'
        '      "choices": ["string", "string", "string", "string"],\n'
        '      "answerIndex": int,\n'
        '      "explanation": "string (explain why the answer is correct)"\n'
        "    },\n"
        "    {\n"
        '      "questionNumber": 4,\n'
        '      "question": "string",\n'
        '      "choices": ["string", "string", "string", "string"],\n'
        '      "answerIndex": int,\n'
        '      "explanation": "string (explain why the answer is correct)"\n'
        "    }\n"
        "  ]\n"
        "}\n"
        "Do not include any markdown, explanation, or formatting outside of the JSON."
    )