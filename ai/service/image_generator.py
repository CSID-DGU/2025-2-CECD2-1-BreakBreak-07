# pip install diffusers transformers torch torchvision torchaudio pillow accelerate peft safetensors

from pathlib import Path
import uuid
from typing import List
import asyncio
import threading

from diffusers import StableDiffusionPipeline
from diffusers import DPMSolverMultistepScheduler
import torch

STYLE_LORA_MAP = {
    "illustration": "./lora/J_illustration.safetensors",
    "ghibli": "./lora/ghibli_style_offset.safetensors"
}

# 모델 로드
pipe = StableDiffusionPipeline.from_pretrained(
    "runwayml/stable-diffusion-v1-5",
    torch_dtype=torch.float32
)

# 디바이스 설정
device = "cuda" if torch.cuda.is_available() else "cpu"
pipe = pipe.to(device)

# 속도 향상을 위한 최적화
pipe.scheduler = DPMSolverMultistepScheduler.from_config(pipe.scheduler.config)
pipe.enable_attention_slicing()

# 이미지 생성 중 중복 방지를 위한 락
pipe_lock = threading.Lock()

def apply_lora(pipe, lora_path: str, lora_scale: float = 0.8):
    pipe.unfuse_lora()  # 기존 LoRA 제거
    pipe.load_lora_weights(lora_path)
    pipe.fuse_lora(lora_scale=lora_scale)

async def generate_images(prompts: List[str], style: str) -> List[str]:
    return await asyncio.to_thread(generate_images_sync, prompts, style)

def generate_images_sync(prompts: List[str], style: str) -> List[str]:
    print(f"스타일 `{style}`로 이미지 생성 중...")
    output_dir = Path("./static/images")
    output_dir.mkdir(parents=True, exist_ok=True)

    # 스타일에 맞는 LoRA 적용
    lora_path = STYLE_LORA_MAP.get(style.lower())
    if lora_path:
        apply_lora(pipe, lora_path, lora_scale=0.8)
    else:
        print(f"[경고] LoRA 파일이 '{style}'에 대해 정의되지 않음. 기본 파이프 사용.")

    with pipe_lock:
        generator = torch.Generator(device).manual_seed(0)
        images = pipe(prompts, generator, num_inference_steps=20).images

    image_urls = []
    for image in images:
        filename = f"{uuid.uuid4()}.png"
        path = output_dir / filename
        image.save(path)
        image_urls.append(f"http://localhost:8000/static/images/{filename}")

    return image_urls