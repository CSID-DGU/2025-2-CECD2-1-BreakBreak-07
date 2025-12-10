# pip install ollama

import ollama
import asyncio
import json
import re

# 응답 본문에서 JSON 형식 문자열을 추출하는 함수
def extract_json_from_response(text: str) -> str:
    match = re.search(r'\{.*\}', text, re.DOTALL)
    if not match:
        raise ValueError("본문에서 JSON 블럭을 찾지 못했습니다.")
    
    json_str = match.group(0)
    
    # 잘못된 숫자+쌍따옴표 패턴 정리
    for i in range(1, 5):
        json_str = json_str.replace(f'{i}"', str(i))
    
    # 불필요한 ",", 패턴 정리
    json_str = re.sub(r'",",', '",', json_str)

    return json_str

# Ollama 모델을 비동기로 호출하는 함수
async def call_ollama(prompt: str, model: str = 'llama3') -> dict:
    print("동화 생성 중...")
    def sync_chat():
        return ollama.chat(model=model, messages=[{
            'role': 'user',
            'content': prompt,
        }])

    # 동기 함수를 비동기 스레드로 실행
    response = await asyncio.to_thread(sync_chat)

    print(response["message"]["content"])

    # 응답에서 JSON 부분만 추출 후 파싱해서 딕셔너리로 반환
    try:
        json_str = extract_json_from_response(response["message"]["content"])
        return json.loads(json_str)
    except Exception as e:
        raise ValueError(f"JSON 파싱 실패: {e}")