import os
from flask import Flask
import json

# Flask 앱 생성
def create_app():
    app = Flask(__name__)

    # 설정 파일 로드 (절대 경로 사용)
    base_dir = os.path.dirname(os.path.abspath(__file__))  # 현재 파일의 디렉토리 경로
    config_path = os.path.join(base_dir, "../config/settings.json")  # 설정 파일 경로

    with open(config_path, "r", encoding="utf-8") as f:
        app.config.update(json.load(f))

    return app