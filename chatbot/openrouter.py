import requests  # HTTP 요청을 보내기 위해 필요한 requests 라이브러리 임포트
import json  # JSON 데이터 처리 위해 json 라이브러리 임포트

# OpenRouter API 키와 URL 설정
OPENROUTER_API_KEY = "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"
OPENROUTER_API_URL = "https://openrouter.ai/api/v1/chat/completions"

# 블로그 스타일 가이드 설정: 
BLOG_GUIDE = (
    "너는 상품 리뷰 전문 네이버 블로거이자 친근한 아줌마 말투로 상품을 리뷰한다."
    "사용자가 제시한 질문으로 상품 리뷰를 작성한다. "
    "단 메인 키워드는 본문에 꼭 6번만 사용한다"
    "구성은 제목 서론 본문으로 작성하고, 이모지도 자연스럽게 활용한다. "
    "본문 내에 이미지가 들어갈 자리를 (이미지 자리) 로만 표시한다 이미지는 총 6장이 들어간다."
    "markdown 형식을 쓰지 않는다"
    "본문 내용과 관련된 자주 쓰이는 형태소를 30번 이상 사용한다" 
    "형태소 예시: 제품 사용 LG 삼성" 
    "목차를 꼭 나눠서 작성한다. 소제목으로 나눠서 길고 자세하게 쓴다. 소제목에 메인 키워드를 포함해선 안된다."
    "소제목 앞에는 (인용구) 를 붙인다"
    "본 제목은 메인키워드가 가장 앞으로 오고 그 뒤에 많이 사용된 형태소들을 조합해서 작성한다" 
    "본 제목에 내용이 아닌 메인키워드가 들어가고 markdown, 특수문자, 이모지는 사용금지"
    "본제목 예시: 메인키워드 제품 사용 리뷰 핫딜중"
    "글 분량은 공백제외 한글 3000자 이상 작성한다"
    "금칙어 구매, 가격, 만족"
)

# ✅ 일반 응답 (디폴트 모드)
def chat_default(message):
    headers = {
        'Authorization': f'Bearer {OPENROUTER_API_KEY}',  # API 키를 인증 헤더에 포함
        'Content-Type': 'application/json'  # 요청 본문 타입을 JSON 형식으로 설정
    }

    payload = {
        "model": "deepseek/deepseek-chat:free",  # 사용할 모델 설정
        "messages": [
            {"role": "system", "content": BLOG_GUIDE},  # 시스템 메시지로 블로그 스타일 가이드 전달
            {"role": "user", "content": message}  # 사용자 메시지 전달
        ],
        "stream": False,  # 응답을 스트리밍 모드가 아닌 일반 모드로 설정
        "max_tokens": 2048 # 최대 토큰 수 설정 (옵션)
    }

    # POST 요청을 OpenRouter API로 보냄
    print('응답 생성 중 . . .')
    try:
        response = requests.post(OPENROUTER_API_URL, headers=headers, json=payload)
        print(f"📡 응답 코드: {response.status_code}")  # 응답 상태 코드 출력
        
        # 응답이 실패했을 경우, 실패한 응답 객체를 출력
        if response.status_code != 200:
            print(f"❌ 요청 실패: {response.text}")  # 실패 이유 출력
            print(f"❌ 응답 객체: {response}")  # 응답 객체 자체 출력
            return
        
        # 응답이 성공적일 경우
        result = response.json()  # JSON 형식의 응답 내용 파싱
        content = result['choices'][0]['message']['content']  # 챗봇의 응답 내용 추출
        usage = result.get("usage", {})  # 사용량 정보 추출

        # 챗봇 응답 내용과 사용량 출력
        print("\n💬 답변:")
        print(content)

        print("\n📊 사용량:")
        print(f" - 프롬프트 토큰: {usage.get('prompt_tokens')}")
        print(f" - 응답 토큰: {usage.get('completion_tokens')}")
        print(f" - 전체 토큰: {usage.get('total_tokens')}")

    except Exception as e:
        # 요청 중 예외가 발생한 경우
        print(f"❌ 예외 발생: {str(e)}")

# ✅ 스트리밍 응답
def chat_streaming(message):
    headers = {
        'Authorization': f'Bearer {OPENROUTER_API_KEY}',
        'Content-Type': 'application/json'
    }

    payload = {
        "model": "deepseek/deepseek-chat:free",
        "messages": [
            {"role": "system", "content": BLOG_GUIDE},
            {"role": "user", "content": message}
        ],
        "stream": True
    }

    # 전체 응답을 누적할 변수
    full_response = ""

    try:
        print("💬 스트리밍 시작:\n")
        with requests.post(OPENROUTER_API_URL, headers=headers, json=payload, stream=True) as r:
            if r.status_code == 200:
                for line in r.iter_lines():
                    if line:
                        decoded = line.decode('utf-8').strip()
                        if decoded.startswith("data: "):
                            try:
                                data = json.loads(decoded[6:])
                                delta = data["choices"][0].get("delta", {}).get("content", "")
                                # 스트리밍된 부분을 full_response에 추가
                                full_response += delta
                                print(delta, end="", flush=True)  # 실시간으로 스트리밍된 내용 출력
                            except json.JSONDecodeError:
                                continue
    except Exception as e:
        # 요청 중 예외가 발생한 경우
        print(f"❌ 스트리밍 모드 예외 발생: {str(e)}")
    
    # 스트리밍이 끝난 후 전체 응답 출력
    #print(f"\n💬 정리된 전체 응답 : {full_response}")    
    print("스트리밍 모드에서는 총 토큰 사용량이 제공되지 않습니다.")

# ▶ 테스트 실행
if __name__ == "__main__":
    product_name = "레노버 Legion Tab Y700 2세대"
    keyword = "y7002세대"

    # f-string을 사용하여 변수 삽입
    message = f"내용: {product_name} 상품에 대한 리뷰를 작성해줘. \n메인 키워드: {keyword}"

    print(f"📝 요청 메시지: {message}")  # 요청 메시지 출력

    # 테스트 메시지 설정
    # chat_default(message)  # 일반 응답 모드 실행
    chat_streaming(message)  # 스트리밍 응답 모드 실행
