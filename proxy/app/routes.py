import os
from flask import Blueprint, request, jsonify, current_app
from .services import chat_default
from .services import run_crawler
from werkzeug.utils import secure_filename

bp = Blueprint("routes", __name__)

@bp.route("/chat", methods=["POST"])
def chat():
    try:
        # 요청 데이터 가져오기
        data = request.json
        print("요청 데이터:", data)  # 요청 데이터 출력

        # 필수 필드 확인
        product_name = data.get("product_name")
        keyword = data.get("keyword")

        if not product_name:
            return jsonify({"status": "error", "message": "Product name is required"}), 400
        if not keyword:
            return jsonify({"status": "error", "message": "Keyword is required"}), 400

        # 파라미터 생성
        params = {
            "product_name": product_name,
            "keyword": keyword
        }

        # 서비스 호출
        response = chat_default(params, current_app.config)

        # 응답 데이터 출력
        print("응답 데이터:", response)

        # 성공 응답
        return jsonify({
            "status": "success",
            "data": response
        }), 200

    except Exception as e:
        # 예외 처리
        print("에러 발생:", str(e))
        return jsonify({
            "status": "error",
            "message": "An unexpected error occurred",
            "details": str(e)
        }), 500
        
        bp = Blueprint("routes", __name__)

@bp.route("/crawl", methods=["POST"])
def crawl():
    try:
        # 요청 데이터 가져오기
        data = request.json
        print("요청 데이터:", data)  # 요청 데이터 출력

        # 필수 필드 확인
        url = data.get("url")
        keyword = data.get("keyword")

        if not url:
            return jsonify({"status": "error", "message": "URL is required"}), 400
        if not keyword:
            return jsonify({"status": "error", "message": "Keyword is required"}), 400

        # 이미지 저장 경로 설정
        save_dir = os.path.join(current_app.root_path, "static", "images", keyword)
        os.makedirs(save_dir, exist_ok=True)

        # 크롤링 실행
        image_io_list = run_crawler(current_app.config, url=url, keyword=keyword)

        # 이미지 저장 및 경로 생성
        image_paths = []
        for i, img_io in enumerate(image_io_list):
            filename = f"{secure_filename(keyword)}_{i}.jpg"
            file_path = os.path.join(save_dir, filename)

            with open(file_path, "wb") as f:
                img_io.seek(0)
                f.write(img_io.read())

            # 접근 가능한 URL 경로 구성 (필요시 도메인 붙이기)
            web_path = f"/static/images/{keyword}/{filename}"
            image_paths.append(web_path)


        # 성공 응답
        return jsonify({
            "status": "success",
            "data": image_paths  
        }), 200

    except Exception as e:
        # 예외 처리
        print("에러 발생:", str(e))
        return jsonify({
            "status": "error",
            "message": "An unexpected error occurred",
            "details": str(e)
        }), 500