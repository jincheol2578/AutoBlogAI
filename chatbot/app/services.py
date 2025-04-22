import requests
import json
import re  # 정규식을 사용하기 위해 추가

def chat_default(params, config):
    # params에서 product_name과 keyword를 추출
    product_name = params.get("product_name")
    keyword = params.get("keyword")

    # 예외 처리: product_name 또는 keyword가 없을 경우
    if not product_name:
        raise ValueError("상품명이 누락되었습니다. 'product_name'을 전달해주세요.")
    if not keyword:
        raise ValueError("키워드가 누락되었습니다. 'keyword'를 전달해주세요.")

    # message 생성
    message = f"내용: {product_name} 상품에 대한 리뷰를 작성해줘.\n메인 키워드: {keyword}"

    headers = {
        'Authorization': f'Bearer {config["OPENROUTER_API_KEY"]}',
        'Content-Type': 'application/json'
    }

    payload = {
        "model": "deepseek/deepseek-chat:free",
        "messages": [
            {"role": "system", "content": config["BLOG_GUIDE"]},
            {"role": "user", "content": message}
        ],
        "stream": False,
        "provider": {
            'sort' : 'throughput'
        }
    }

    try:
        response = requests.post(config["OPENROUTER_API_URL"], headers=headers, json=payload)
        if response.status_code != 200:
            return {"error": response.text}

        result = response.json()
        print("result: " + json.dumps(result, ensure_ascii=False, indent=4))  # JSON 형식으로 출력
        full_content = result['choices'][0]['message']['content']

        # 정규식을 사용하여 @특수문자@로 감싸진 부분 추출
        title_match = re.search(r"@(.+?)@", full_content)
        title = title_match.group(1) if title_match else "No Title"

        # title을 제외한 나머지 내용을 content로 저장
        content = re.sub(r"@.+?@", "", full_content).strip()

        return {
            "title": title,
            "content": content
        }
    except Exception as e:
        return {"error": str(e)}