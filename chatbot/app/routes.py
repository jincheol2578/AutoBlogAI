from flask import Blueprint, request, jsonify, current_app
from .services import chat_default

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