# api/api_requests.py

import requests
from config import API_BASE_URL

class ApiClient:
    token = None  # JWT 토큰 저장
    session = requests.Session()  # 세션 객체 생성

    @staticmethod
    def login(user_id, user_pw):
        url = f"{API_BASE_URL}/auth/login"
        data = {
            "username": user_id,
            "password": user_pw
        }

        try:
            response = requests.post(url, json=data)
            response.raise_for_status()
            result = response.json()

            # 서버 응답이 {"token": "발급된 JWT"} 이런 형태라고 가정
            token = result.get("token")

            if token:
                ApiClient.token = token
                print(f"로그인 성공, 토큰: {token}")
                return True
            else:
                print("로그인 실패: 토큰 없음")
                return False
        except Exception as e:
            print(f"로그인 API 호출 실패: {e}")
            return False

    @staticmethod
    def get_headers():
        """Authorization 헤더를 만들어주는 메소드"""
        if ApiClient.token:
            return {
                "Authorization": f"Bearer {ApiClient.token}"
            }
        return {}
