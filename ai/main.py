# $ pip install fastapi
# $ pip install "uvicorn[standard]"
# 실행 명령어: python -m uvicorn main:app

from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles

from models import TaleCreateRequest, TaleApiResponse, QuizDto
from service.tale_prompt import generate_tale_prompt
from service.image_prompt import generate_final_image_prompts
from service.ollama_client import call_ollama
from service.image_generator import generate_images

app = FastAPI()

# 정적 파일 서빙: /static 경로로 이미지 접근 가능하게 함
app.mount("/static", StaticFiles(directory="static"), name="static")

@app.post("/api/tales", response_model=TaleApiResponse)
async def create_tale(request: TaleCreateRequest):
    print("Fast API 연결 성공")
    # RAG 추가시
    # 주제에 따라 외부 지식 검색
    # 검색된 정보를 포함하여 프롬프트 생성

    # 동화 생성용 프롬프트 생성
    prompt = generate_tale_prompt(request.topic, request.age)

    # Ollama 서버에 프롬프트 전송
    ollama_response = await call_ollama(prompt)

    # 데이터 추출
    title = ollama_response.get("title", "Untitled")
    character = ollama_response.get("character", {})
    contents = ollama_response.get("contents", [])
    quizzes_raw = ollama_response.get("quizzes", [])
    quizzes = [QuizDto(**q) for q in quizzes_raw]
    print(title, "동화 생성 완료")

    # 이미지 생성용 프롬프트 생성
    image_prompts = generate_final_image_prompts(contents, character)

    # 이미지 생성
    image_urls = await generate_images(image_prompts, request.style)
    print("이미지 생성 완료")

    return TaleApiResponse(
        title=title,
        contents=contents,
        imageUrls=image_urls,
        quizzes=quizzes
    )