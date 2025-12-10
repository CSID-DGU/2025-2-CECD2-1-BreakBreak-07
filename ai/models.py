from pydantic import BaseModel
from typing import List, Optional


class TaleCreateRequest(BaseModel):
    topic: str
    style: str
    age: int


class QuizDto(BaseModel):
    questionNumber: int
    question: str
    choices: List[str]
    answerIndex: int
    explanation: Optional[str] = None


class TaleApiResponse(BaseModel):
    title: str
    contents: List[str]
    imageUrls: List[str]
    quizzes: Optional[List[QuizDto]] = []