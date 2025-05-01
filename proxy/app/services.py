import requests
import json
import re  # 정규식을 사용하기 위해 추가
import os
import time
import random
from PIL import Image, ImageDraw, ImageFont, ImageOps
from io import BytesIO
from selenium.webdriver import Remote, ChromeOptions as Options
from selenium.webdriver.chromium.remote_connection import (
    ChromiumRemoteConnection as Connection,
)
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


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
    message = (
        f"내용: {product_name} 상품에 대한 리뷰를 작성해줘.\n메인 키워드: {keyword}"
    )

    headers = {
        "Authorization": f'Bearer {config["OPENROUTER_API_KEY"]}',
        "Content-Type": "application/json",
    }

    payload = {
        "model": "google/gemini-2.0-flash-exp:free",
        # "model": "deepseek/deepseek-chat:free",
        "messages": [
            {"role": "system", "content": config["BLOG_GUIDE"]},
            {"role": "user", "content": message},
        ],
        "stream": False,
        "provider": {"sort": "throughput"},
    }

    try:
        response = requests.post(
            config["OPENROUTER_API_URL"], headers=headers, json=payload
        )
        if response.status_code != 200:
            return {"error": response.text}

        result = response.json()
        print(
            "result: " + json.dumps(result, ensure_ascii=False, indent=4)
        )  # JSON 형식으로 출력
        full_content = result["choices"][0]["message"]["content"]

        # 정규식을 사용하여 @특수문자@로 감싸진 부분 추출
        title_match = re.search(r"@(.+?)@", full_content)
        title = title_match.group(1) if title_match else "No Title"

        # title을 제외한 나머지 내용을 content로 저장
        content = re.sub(r"@.+?@", "", full_content).strip()

        return {"title": title, "content": content}
    except Exception as e:
        return {"error": str(e)}

TARGET_URL = os.environ.get(
    "TARGET_URL",
    default="https://www.naver.com",
)


def get_random_color():
    # 랜덤 RGB 색상 생성
    return tuple(random.randint(0, 255) for _ in range(3))

# 이미지 저장 및 변환 함수
def transform_and_return_image(image_bytes, index, keyword=None):
    image = Image.open(BytesIO(image_bytes))

    # RGBA 또는 P 모드일 경우 RGB로 변환
    if image.mode in ("RGBA", "P"):
        image = image.convert("RGB")

    # 이미지 크기 확인 및 자르기
    image_width, image_height = image.size
    print(f"Original Image Size: {image_width}x{image_height}")

    if image_height > 1400:
        print("Image height is greater than 1400px. Cropping the image...")
        image = image.crop((0, 0, image_width, 1400))

    # 첫 번째 이미지에 썸네일 만들기
    if index == 0 and keyword:
        image = create_thumbnail(image, keyword)

    border_color = get_random_color()
    image = ImageOps.expand(image, border=15, fill=border_color)

    # 이미지를 메모리에서 BytesIO 객체로 변환하여 반환
    image_io = BytesIO()
    image.save(image_io, format="JPEG")
    image_io.seek(0)
    return image_io


# 썸네일에 텍스트 추가하는 함수
def create_thumbnail(image, keyword):
    image = image.convert("RGBA")  # 투명도 지원을 위한 RGBA 모드 전환
    overlay = Image.new("RGBA", image.size, (0, 0, 0, 0))  # 투명 레이어
    draw = ImageDraw.Draw(overlay)

    width, height = image.size
    font_path = os.path.join(os.path.dirname(__file__), "static", "font", "NotoSansKR-Bold.ttf")

    try:
        font = ImageFont.truetype(font_path, 40)
    except IOError:
        font = ImageFont.load_default()

    text = f"{keyword} 핫딜중!!\n바로가기"
    lines = text.split("\n")
    line_sizes = [draw.textbbox((0, 0), line, font=font) for line in lines]
    line_heights = [bbox[3] - bbox[1] for bbox in line_sizes]
    line_widths = [bbox[2] - bbox[0] for bbox in line_sizes]

    total_height = sum(line_heights) + (len(lines) - 1) * 10
    max_width = max(line_widths)

    padding = 25
    box_x = (width - max_width) / 2 - padding
    box_y = height - 170
    box_width = max_width + padding * 2
    box_height = total_height + padding * 2
    radius = 50  # radius 강하게

    # 반투명 회색 박스 (RGBA 값: 회색 + 알파)
    draw.rounded_rectangle(
        [(box_x, box_y), (box_x + box_width, box_y + box_height)],
        radius=radius,
        fill=(10, 105, 214, 200),  # 회색 + 반투명
        outline=(255, 255, 255, 255),     # 검정 테두리
        width=13
    )

    # 텍스트 다시 그림
    current_y = box_y + padding
    for i, line in enumerate(lines):
        line_width = line_widths[i]
        x = (width - line_width) / 2
        draw.text((x, current_y), line, font=font, fill=(255, 255, 255, 255))
        current_y += line_heights[i] + 10

    # 원본 이미지에 overlay 합성
    result = Image.alpha_composite(image, overlay).convert("RGB")  # JPEG 저장 위해 RGB로 다시 변환
    return result



def random_delay():
    time.sleep(random.uniform(1, 3))


def run_crawler(config, url=TARGET_URL, keyword=None):
    session = "-" + str(random.randint(10000, 99999)) + ":"
    auth = config["PROXY_USERNAME"] + session + config["PROXY_PASSWORD"]
    
    print("Connecting to Browser...")
    
    server_addr = f"https://{auth}@brd.superproxy.io:9515"
    connection = Connection(server_addr, "goog", "chrome")
    options = Options()
    options.add_argument(
        "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36"
    )
    options.add_argument("--disable-blink-features=AutomationControlled")
    options.add_experimental_option("excludeSwitches", ["enable-automation"])
    options.add_experimental_option("useAutomationExtension", False)
    driver = Remote(connection, options=options)

    try:
        print(f"Connected! Navigating to {url}...")
        driver.get(url)
        print("Navigated! Scraping page content...")

        time.sleep(5)  # 페이지 로딩 대기

        # 주요 제품 이미지 찾기
        main_thumbs = WebDriverWait(driver, 5).until(
            EC.presence_of_all_elements_located((By.CSS_SELECTOR, ".prod-image__item"))
        )
        saved_image_files = []

        for i, img in enumerate(main_thumbs):
            if len(saved_image_files) >= 7:  # 최대 7장까지 다운로드
                break
            try:
                img.click()
                random_delay()
                img_tag = WebDriverWait(driver, 5).until(
                    EC.presence_of_element_located(
                        (By.CSS_SELECTOR, ".prod-image__detail")
                    )
                )
                img_url = img_tag.get_attribute("src")
                if img_url:
                    img_bytes = requests.get(img_url).content
                    image_file = transform_and_return_image(img_bytes, i, keyword)
                    saved_image_files.append(image_file)
            except Exception as e:
                print(f"🔥 Error occurred (main image): {e}")

        # 7장 미만으로 이미지가 저장되면, 제품 상세 이미지에서 추가로 다운로드
        if len(saved_image_files) < 7:
            additional_images = driver.find_elements(
                By.CSS_SELECTOR, ".product-detail-content img"
            )
            for img in additional_images:
                if len(saved_image_files) >= 7:
                    break
                try:
                    img_url = img.get_attribute("src")
                    if img_url:
                        img_bytes = requests.get(img_url).content
                        image_file = transform_and_return_image(
                            img_bytes, len(saved_image_files), keyword
                        )
                        saved_image_files.append(image_file)
                    random_delay()
                except Exception as e:
                    print(f"🔥 Error occurred (detail image): {e}")

        print(f"Saved {len(saved_image_files)} images.")
        return saved_image_files

    finally:
        driver.quit()
