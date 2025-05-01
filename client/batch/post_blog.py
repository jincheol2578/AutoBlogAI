import time
import random
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.action_chains import ActionChains
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import pyautogui
import pyperclip
from selenium.common.exceptions import NoSuchElementException
from selenium.common.exceptions import StaleElementReferenceException
from selenium.common.exceptions import TimeoutException


class NaverBlogPoster:
    def __init__(self, naver_id, naver_pw):
        self.naver_id = naver_id
        self.naver_pw = naver_pw

        # 웹드라이버 설정
        self.driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()))
        self.driver.implicitly_wait(10)

    def random_delay(self):
        delay = random.uniform(1.5, 3)
        time.sleep(delay)

    def login(self):
        """네이버 로그인"""
        self.driver.get(
            "https://nid.naver.com/nidlogin.login?mode=form&url=https://www.naver.com/"
        )
        self.random_delay()
        # 아이디와 비밀번호 입력
        id_input = self.driver.find_element(By.ID, "id")
        pw_input = self.driver.find_element(By.ID, "pw")

        try:
            # switch_on span 요소 찾기
            switch_element = WebDriverWait(self.driver, 5).until(
                EC.presence_of_element_located(
                    (By.CSS_SELECTOR, 'span.switch_on[aria-checked="true"]')
                )
            )

            # switch_element가 존재하고, aria-checked="true" 상태라면 클릭
            if switch_element:
                print("Switch is ON, clicking...")
                switch_element.click()
            else:
                print("Switch is already OFF or not found.")

        except Exception as e:
            print(f"Error: {e}")

        id_input.click()
        time.sleep(0.5)
        pyperclip.copy(self.naver_id)
        pyautogui.hotkey("ctrl", "v")
        time.sleep(0.5)
        pw_input.click()
        time.sleep(0.5)
        pyperclip.copy(self.naver_pw)
        pyautogui.hotkey("ctrl", "v")
        time.sleep(0.5)

        # 로그인 버튼 클릭
        login_button = self.driver.find_element(By.ID, "log.login")
        login_button.click()
        time.sleep(1)

    def create_post(self, title, content, url, thumbnailImage, images):
        """블로그 포스트 작성"""

        def safe_click(element):
            self.random_delay()
            element.click()

        def paste_text(element, text):
            element.click()
            pyperclip.copy(text)
            time.sleep(0.1)
            pyautogui.hotkey("ctrl", "shift", "v")
            time.sleep(1)

        def update_span_text(span_id, new_text, key):
            if key == "(인용구1)":
                selector = f"#{span_id} > i"
            elif key == "(인용구3)":
                selector = f"#{span_id} > b"
            else:
                selector = f"#{span_id}"
            target = WebDriverWait(self.driver, 5).until(
                EC.presence_of_element_located((By.CSS_SELECTOR, selector))
            )
            target.click()
            actions = ActionChains(self.driver)
            actions.send_keys(Keys.HOME).perform()
            print("home실행")
            time.sleep(0.5)
            for _ in range(6):
                actions = ActionChains(self.driver)
                actions.send_keys(Keys.DELETE).perform()
                print("delete실행")
                time.sleep(0.1)

        quotation_map = {
            "(인용구1)": "se-quotation-default-toolbar-button",
            "(인용구2)": "se-quotation-quotation_line-toolbar-button",
            "(인용구3)": "se-quotation-quotation_bubble-toolbar-button",
            "(인용구4)": "se-quotation-quotation_underline-toolbar-button",
            "(인용구5)": "se-quotation-quotation_corner-toolbar-button",
        }

        # 블로그 메인 이동
        self.driver.get("https://blog.naver.com/MyBlog.naver")
        self.driver.switch_to.frame("mainFrame")

        # 글쓰기 버튼 클릭
        WebDriverWait(self.driver, 5).until(
            EC.presence_of_element_located((By.CLASS_NAME, "_checkBlock"))
        ).click()

        # '취소' 버튼 처리
        try:
            cancel_button = self.driver.find_element(
                By.CLASS_NAME, "se-popup-button-cancel"
            )
            safe_click(cancel_button)
            print("이어쓰기 취소")

            guide_button = self.driver.find_element(
                By.CLASS_NAME, "se-help-panel-close-button"
            )
            safe_click(guide_button)
            print("가이드 창 닫기")
        except NoSuchElementException:
            print("취소 버튼 없음, 계속 진행.")

        # 제목, 본문 입력
        paste_text(self.driver.find_element(By.CLASS_NAME, "se-title-text"), title)
        paste_text(self.driver.find_element(By.CLASS_NAME, "se-section-text"), content)

        # 인용구 스타일 적용
        previous_spans = None
        while True:
            time.sleep(2)
            spans = self.driver.find_elements(By.CSS_SELECTOR, ".__se-node")

            if spans == previous_spans:
                print("변경된 span 없음, 종료.")
                break

            print(f"새로운 span {len(spans)}개 발견")

            for span in spans:
                try:
                    span_text = span.text
                    span_id = span.get_attribute("id")
                    print(f"span: {span_text} (id={span_id})")

                    for key, class_name in quotation_map.items():
                        if key in span_text:
                            print(f"'{key}' 포함된 span 발견")

                            # 텍스트에서 인용구 번호 제거
                            clean_text = span_text.replace(key, "")

                            # 텍스트 선택
                            actions = ActionChains(self.driver)
                            actions.move_to_element(span).click().key_down(Keys.SHIFT)
                            for _ in range(2):
                                actions.send_keys(Keys.ARROW_RIGHT)
                            actions.key_up(Keys.SHIFT).perform()

                            # 인용구 버튼 누르기
                            WebDriverWait(self.driver, 5).until(
                                EC.presence_of_element_located(
                                    (By.CLASS_NAME, "se-to-quotation-toolbar-button")
                                )
                            ).click()

                            # 인용구 스타일 선택
                            safe_click(
                                self.driver.find_element(By.CLASS_NAME, class_name)
                            )

                            # span 텍스트 수정
                            update_span_text(span_id, clean_text, key)
                            break  # 해당 span 작업 완료되면 바로 다음 span 이동

                except StaleElementReferenceException:
                    print("StaleElementReferenceException 발생, span 다시 조회")
                    break

                except TimeoutException:
                    print("TimeoutException 발생, 다음 span으로 이동")
                    continue

            previous_spans = spans
        print("인용구 추가 완료")
        print("이미지 작성 시작")
        self.add_image()
        self.add_thumbnail(url)
        print("블로그 포스트 작성 완료 ✅")

        self.close()

    def add_image(self):
        target_text = "(이미지)"
        previous_spans = None  # 이전 span을 추적할 변수 초기화

        while True:
            try:
                spans = self.driver.find_elements(By.CSS_SELECTOR, ".__se-node")
                print(f"찾은 span 요소의 개수: {len(spans)}")  # 스팬 요소 개수 확인

                # 현재 span 목록을 이전 span 목록과 비교
                if spans == previous_spans:
                    print("이전에 처리한 span 목록과 동일하므로 종료합니다.")
                    break  # 같은 span이 반복되면 종료

                # 이전 span 목록 업데이트
                previous_spans = spans.copy()

                for span in spans:
                    span_text = span.text
                    span_id = span.get_attribute("id")
                    print(f"현재 span 텍스트: {span_text}")  # 각 span의 텍스트 확인

                    if target_text in span_text:  # 이미지 추가가 필요한 부분
                        print("이미지 추가가 필요한 부분을 찾았습니다.")

                        clean_text = span_text.replace(
                            target_text, ""
                        )  # 텍스트에서 (Image) 제거
                        print(
                            f"정리된 텍스트: {clean_text}"
                        )  # (Image) 제거된 텍스트 확인

                        # 텍스트 선택
                        actions = ActionChains(self.driver)
                        actions.move_to_element(span).click()
                        actions.send_keys(Keys.END).perform()
                        print("텍스트 선택 및 끝으로 이동 완료.")
                        time.sleep(1)

                        # 이미지 추가 버튼 클릭
                        image_button = WebDriverWait(self.driver, 5).until(
                            EC.presence_of_element_located(
                                (By.CLASS_NAME, "se-image-toolbar-button")
                            )
                        )
                        image_button.click()
                        print("이미지 추가 버튼 클릭 완료.")
                        time.sleep(1.5)

                        # 숨겨진 파일 업로드 input[type="file"] 요소 찾기
                        file_input = WebDriverWait(self.driver, 5).until(
                            EC.presence_of_element_located((By.ID, "hidden-file"))
                        )
                        print("파일 업로드 input 요소 찾음.")

                        # 업로드할 이미지 파일 경로 (이미지 경로를 지정)
                        file_path = "C:\\Users\\Admin\\Pictures\\Screenshots\\스크린샷 2025-04-26 133601.png"
                        print(f"업로드할 파일 경로: {file_path}")  # 파일 경로 확인

                        # 파일 경로 입력
                        file_input.send_keys(file_path)
                        print("파일 경로 입력 완료.")
                        time.sleep(1)
                        pyautogui.hotkey("esc")  # 업로드 창 닫기 (ESC)
                        time.sleep(2)
                        actions = ActionChains(self.driver)
                        actions.send_keys(Keys.ARROW_RIGHT).perform()

                        # 텍스트 업데이트
                        target = WebDriverWait(self.driver, 5).until(
                            EC.presence_of_element_located((By.ID, span_id))
                        )
                        self.driver.execute_script(
                            "arguments[0].scrollIntoView({block: 'center'});", target
                        )
                        target.click()
                        actions = ActionChains(self.driver)
                        actions.send_keys(Keys.HOME).perform()
                        time.sleep(1)
                        actions = ActionChains(self.driver)
                        actions.send_keys(Keys.DELETE * 5).perform()
                        # self.driver.execute_script("arguments[0].innerText = arguments[1];", target, clean_text)
                        print(
                            f"이미지 추가 후 텍스트 업데이트: {clean_text}"
                        )  # 텍스트 업데이트 확인

                        print("이미지 추가 완료!")
                        break  # 한 번 이미지를 추가하고 나면 다음 반복

            except StaleElementReferenceException:
                print("StaleElementReferenceException 발생, span 다시 조회")
                break  # 오류 발생 시 span을 다시 조회하고 종료

            except TimeoutException:
                print("TimeoutException 발생, 다음 span으로 이동")
                continue  # 타임아웃이 발생하면 다음 span으로 넘어감

    def add_thumbnail(self, url):
        target_text = "(링크)"
        previous_spans = None  # 이전 span을 추적할 변수 초기화

        while True:
            try:
                spans = self.driver.find_elements(By.CSS_SELECTOR, ".__se-node")
                print(f"찾은 span 요소의 개수: {len(spans)}")  # 스팬 요소 개수 확인

                # 현재 span 목록을 이전 span 목록과 비교
                if spans == previous_spans:
                    print("이전에 처리한 span 목록과 동일하므로 종료합니다.")
                    break  # 같은 span이 반복되면 종료

                # 이전 span 목록 업데이트
                previous_spans = spans.copy()

                for span in spans:
                    span_text = span.text
                    span_id = span.get_attribute("id")
                    print(f"현재 span 텍스트: {span_text}")  # 각 span의 텍스트 확인

                    if target_text in span_text:  # 링크 부분 찾기
                        print("썸네일 추가가 필요한 부분을 찾았습니다.")

                        clean_text = span_text.replace(
                            target_text, ""
                        )  # 텍스트에서 (링크) 제거
                        print(
                            f"정리된 텍스트: {clean_text}"
                        )  # (링크) 제거된 텍스트 확인

                        # 텍스트 선택
                        actions = ActionChains(self.driver)
                        actions.move_to_element(span).click()
                        actions.send_keys(Keys.END).perform()
                        print("텍스트 선택 및 끝으로 이동 완료.")
                        time.sleep(2)

                        # 썸네일 추가 버튼 클릭 (썸네일 삽입 버튼을 클릭)
                        thumbnail_button = WebDriverWait(self.driver, 5).until(
                            EC.presence_of_element_located(
                                (By.CLASS_NAME, "se-image-toolbar-button")
                            )
                        )
                        thumbnail_button.click()  # 썸네일 추가 버튼 클릭
                        print("썸네일 추가 버튼 클릭 완료.")
                        time.sleep(2)

                        # 썸네일 이미지 업로드 위한 파일 업로드 input[type="file"] 요소 찾기
                        file_input = WebDriverWait(self.driver, 5).until(
                            EC.presence_of_element_located((By.ID, "hidden-file"))
                        )
                        print("파일 업로드 input 요소 찾음.")

                        # 업로드할 썸네일 이미지 경로 (썸네일 이미지 경로를 지정)
                        thumbnail_path = "C:\\Users\\Admin\\Pictures\\Screenshots\\스크린샷 2025-04-26 133621.png"
                        print(
                            f"업로드할 썸네일 파일 경로: {thumbnail_path}"
                        )  # 파일 경로 확인

                        # 파일 경로 입력
                        file_input.send_keys(thumbnail_path)
                        print("썸네일 파일 경로 입력 완료.")
                        time.sleep(1)
                        pyautogui.hotkey("esc")
                        time.sleep(1)

                        actions = ActionChains(self.driver)
                        actions.send_keys(Keys.ARROW_RIGHT).perform()
                        time.sleep(1)
                        # 텍스트 업데이트
                        target = WebDriverWait(self.driver, 5).until(
                            EC.presence_of_element_located((By.ID, span_id))
                        )
                        target.click()
                        actions = ActionChains(self.driver)
                        actions.send_keys(Keys.HOME).perform()
                        time.sleep(1)
                        actions = ActionChains(self.driver)
                        actions.send_keys(Keys.DELETE * 4).perform()
                        print(
                            f"썸네일 추가 후 텍스트 업데이트: {clean_text}"
                        )  # 텍스트 업데이트 확인

                        print("썸네일 추가 완료!")
                        break  # 한 번 썸네일을 추가하고 나면 다음 반복

            except StaleElementReferenceException:
                print("StaleElementReferenceException 발생, span 다시 조회")
                break  # 오류 발생 시 span을 다시 조회하고 종료

            except TimeoutException:
                print("TimeoutException 발생, 다음 span으로 이동")
                continue  # 타임아웃이 발생하면 다음 span으로 넘어감\

        link_images = WebDriverWait(self.driver, 5).until(
            EC.presence_of_all_elements_located((By.CSS_SELECTOR, ".se-image-resource"))
        )
        link_images[0].click()
        self.random_delay()
        try:
            link_button = WebDriverWait(self.driver, 5).until(
                EC.presence_of_element_located(
                    (By.CSS_SELECTOR, ".se-link-toolbar-button")
                )
            )
            link_button.click()
        except StaleElementReferenceException:
            print("StaleElementReferenceException 발생, 링크 버튼을 다시 찾습니다.")
            link_button = WebDriverWait(self.driver, 5).until(
                EC.presence_of_element_located(
                    (By.CSS_SELECTOR, ".se-link-toolbar-button")
                )
            )
            link_button.click()
        self.random_delay()
        link_input = WebDriverWait(self.driver, 5).until(
            EC.presence_of_element_located(
                (By.CSS_SELECTOR, ".se-custom-layer-link-input")
            )
        )
        link_input.send_keys(url)

        link_apply_button = WebDriverWait(self.driver, 5).until(
            EC.presence_of_element_located(
                (By.CSS_SELECTOR, ".se-custom-layer-link-apply-button")
            )
        )
        link_apply_button.click()

    def close(self):
        """브라우저 종료"""
        # self.driver.quit()
