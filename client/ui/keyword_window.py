from PyQt5.QtWidgets import (
    QWidget,
    QVBoxLayout,
    QLabel,
    QTableWidget,
    QTableWidgetItem,
    QPushButton,
    QMessageBox,
    QDialog,
    QHBoxLayout,
    QTextEdit,
    QScrollArea,
    QFrame,
    QApplication,
)
from PyQt5.QtGui import QPixmap
from PyQt5.QtCore import Qt
from io import BytesIO
from api.api_request import ApiClient
import requests
from config import API_BASE_URL, FILE_SERVER_URL
from ui.setting_window import SettingsWindow
from batch.post_blog import NaverBlogPoster
from ui.loading_dialog import LoadingDialog


class KeywordWindow(QWidget):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("키워드 리스트")
        self.setGeometry(100, 100, 600, 500)

        self.init_ui()
        self.load_keywords()

        # 아이디와 비밀번호 로드
        self.settings_window = SettingsWindow()
        self.naver_id, self.naver_pw = self.settings_window.load_credentials()
        if not self.naver_id or not self.naver_pw:
            QMessageBox.warning(
                self,
                "경고",
                "아이디와 비밀번호가 설정되지 않았습니다. 설정을 먼저 완료해주세요.",
            )

    def init_ui(self):
        self.layout = QVBoxLayout()

        self.label = QLabel("받은 키워드 목록")
        self.layout.addWidget(self.label)

        # 테이블 생성
        self.keyword_table = QTableWidget()
        self.keyword_table.setColumnCount(7)
        self.keyword_table.setHorizontalHeaderLabels(
            ["키워드", "지수", "상품", "사진", "리뷰", "포스팅", "실행"]
        )  # 새로운 컬럼명
        self.layout.addWidget(self.keyword_table)

        # 새로고침 버튼
        self.refresh_button = QPushButton("새로고침")
        self.refresh_button.clicked.connect(self.refresh_keywords)
        self.layout.addWidget(self.refresh_button)

        # 설정 버튼
        self.settings_button = QPushButton("설정")
        self.settings_button.clicked.connect(self.open_settings_window)
        self.layout.addWidget(self.settings_button)

        self.setLayout(self.layout)

    def refresh_keywords(self):
        print("키워드 목록을 새로고침합니다.")
        self.load_keywords()

    def load_keywords(self):
        """Spring 서버에서 키워드 리스트 받아오기"""
        try:
            url = f"{API_BASE_URL}/keywords/products"  # 서버 주소 맞게 수정
            headers = ApiClient.get_headers()

            response = requests.get(url, headers=headers)
            response.raise_for_status()

            keywords = response.json()
            print("response:", response.json())
            self.keyword_table.setRowCount(len(keywords))

            for row_idx, keyword_info in enumerate(keywords):
                keyword = keyword_info.get("keyword", "")
                score = keyword_info.get("score", "")
                valid = keyword_info.get("isValid", False)
                products = keyword_info.get("products", [])

                # 테이블에 데이터 삽입
                self.keyword_table.setItem(row_idx, 0, QTableWidgetItem(str(keyword)))
                self.keyword_table.setItem(row_idx, 1, QTableWidgetItem(str(score)))
                self.keyword_table.setItem(
                    row_idx, 5, QTableWidgetItem("등록됨" if valid else "미등록")
                )

                if products:
                    product_button = QPushButton("상품보기")
                    product_button.clicked.connect(
                        lambda _, row=row_idx, p=products: self.show_product_modal(
                            row, p
                        )
                    )
                    self.keyword_table.setCellWidget(row_idx, 2, product_button)

                # 사진등록여부 버튼
                if any(product["productImage"] for product in products):
                    image_button = QPushButton("이미지보기")
                    image_button.clicked.connect(
                        lambda _, row=row_idx, p=products: self.show_images_modal(
                            row, p
                        )
                    )
                    self.keyword_table.setCellWidget(row_idx, 3, image_button)
                # 리뷰등록여부 버튼
                if any(product["draftReviews"] for product in products):
                    review_button = QPushButton("리뷰보기")
                    review_button.clicked.connect(
                        lambda _, row=row_idx, p=products: self.show_review_modal(
                            row, p
                        )
                    )
                    # 여기서 리뷰 데이터 저장
                    review_button.setProperty("reviewData", products)
                    self.keyword_table.setCellWidget(row_idx, 4, review_button)

                # 실행 버튼
                execute_button = QPushButton("실행")
                execute_button.clicked.connect(
                    lambda _, row=row_idx: self.execute_action(row)
                )
                self.keyword_table.setCellWidget(row_idx, 6, execute_button)

        except Exception as e:
            print(f"키워드 불러오기 실패: {e}")
            QMessageBox.warning(self, "에러", "키워드 목록을 불러오는 데 실패했습니다.")

    def show_product_modal(self, row, products):
        """상품등록 버튼 클릭 시 모달을 보여주는 함수"""
        if not products:
            QMessageBox.information(self, "정보", "등록된 상품이 없습니다.")
            return
        product_name = products[0]["productName"]
        product_url = products[0].get("partnersUrl", "")  # Get the partnersUrl
        product_modal = QDialog(self)
        product_modal.setWindowTitle("상품 등록")

        layout = QVBoxLayout()
        layout.addWidget(QLabel(f"상품명: {product_name}"))
        layout.addWidget(QLabel(f"상품 URL: {product_url}"))  # Display the URL
        product_modal.setLayout(layout)
        product_modal.exec_()

    def show_images_modal(self, row, products):
        """이미지 모달"""

        loading = LoadingDialog("이미지 불러오는 중...", self)
        loading.show()
        QApplication.processEvents()

        product_images = []
        for product in products:
            product_images.extend(product.get("productImage", []))  # 단수형 주의!

        print("이미지수:", len(product_images))

        if not product_images:
            QMessageBox.information(self, "정보", "등록된 이미지가 없습니다.")
            return

        image_modal = QDialog(self)
        image_modal.setWindowTitle("🖼️ 이미지 보기")
        image_modal.resize(700, 900)

        scroll_area = QScrollArea()
        scroll_area.setWidgetResizable(True)
        scroll_content = QWidget()
        layout = QVBoxLayout(scroll_content)
        layout.setSpacing(20)
        layout.setContentsMargins(15, 15, 15, 15)

        for idx, image in enumerate(product_images):
            raw_url = image.get("imageUrl")
            if not raw_url:
                continue

            image_url = FILE_SERVER_URL + raw_url
            try:
                response = requests.get(image_url)
                response.raise_for_status()
                pixmap = QPixmap()
                pixmap.loadFromData(response.content)

                label = QLabel()
                label.setPixmap(pixmap.scaledToWidth(600, Qt.SmoothTransformation))
                label.setAlignment(Qt.AlignCenter)

                # 프레임으로 감싸기
                frame = QFrame()
                frame_layout = QVBoxLayout()
                frame_layout.addWidget(label)
                frame.setLayout(frame_layout)
                frame.setFrameShape(QFrame.StyledPanel)
                frame.setStyleSheet(
                    "QFrame { background-color: #f8f9fa; border: 1px solid #ddd; border-radius: 8px; }"
                )
                layout.addWidget(frame)

            except Exception as e:
                print(f"이미지 로드 실패: {e}")
                error_label = QLabel(f"이미지 로드 실패: {image_url}")
                error_label.setStyleSheet("color: red;")
                layout.addWidget(error_label)

        scroll_area.setWidget(scroll_content)

        modal_layout = QVBoxLayout()
        modal_layout.addWidget(scroll_area)
        image_modal.setLayout(modal_layout)
        image_modal.exec_()

        loading.close()  # 로딩창 닫음

    def show_review_modal(self, row, products):
        """리뷰등록 버튼 클릭 시 모달을 보여주는 함수"""
        draft_reviews = []
        for product in products:
            draft_reviews.extend(product.get("draftReviews", []))

        if draft_reviews:
            review_modal = QDialog(self)
            review_modal.setWindowTitle("리뷰 내용")

            layout = QVBoxLayout()
            for review in draft_reviews:
                layout.addWidget(QLabel(f"제목: {review['title']}"))
                text_edit = QTextEdit()
                text_edit.setPlainText(f"{review['content']}")
                text_edit.setReadOnly(True)  # 읽기 전용으로 설정 (선택)
                layout.addWidget(text_edit)
            review_modal.setLayout(layout)
            review_modal.exec_()

    def execute_action(self, row):
        """키워드에 대한 실행 작업을 처리"""
        loading = LoadingDialog("포스팅 작성하는 중...", self)
        loading.show()
        QApplication.processEvents()

        keyword = self.keyword_table.item(row, 0).text()
        print(f"'{keyword}'에 대한 실행 작업이 시작되었습니다.")

        self.naver_id, self.naver_pw = self.settings_window.load_credentials()
        if not self.naver_id or not self.naver_pw:
            QMessageBox.warning(
                self,
                "경고",
                "아이디와 비밀번호가 설정되지 않았습니다. 설정에서 아이디와 비밀번호를 입력해주세요.",
            )
            return

        # 데이터 가져오기
        review_button = self.keyword_table.cellWidget(row, 5)
        products = review_button.property("reviewData") if review_button else []

        review = None
        product_url = None  # Initialize the partnersUrl variable
        thumbnail_image_data = None
        image_data_list = []
        for product in products:
            draft_reviews = product.get("draftReviews", [])
            product_images = product.get("productImages", [])

            if draft_reviews:
                review = draft_reviews[0]
                product_url = product.get("partnersUrl", "")  # Get the partnersUrl
                break

            if product_images:
                for image in product_images:
                    url = image.get("imageUrl")
                    if not url:
                        continue
                    try:
                        response = requests.get(url)
                        response.raise_for_status()
                        image_data = BytesIO(
                            response.content
                        )  # 이미지 파일처럼 사용할 수 있는 객체

                        if image.get("isMain"):
                            thumbnail_image_data = image_data
                        else:
                            image_data_list.append(image_data)
                    except Exception as e:
                        print(f"이미지 다운로드 실패: {url}, 오류: {e}")
                        

        if not review:
            QMessageBox.warning(self, "경고", "등록된 리뷰가 없습니다.")
            return

        post_title = review["title"]
        post_content = review["content"]

        # 블로그 포스팅
        blog_poster = NaverBlogPoster(self.naver_id, self.naver_pw)
        blog_poster.login()
        blog_poster.create_post(
            post_title, post_content, product_url, thumbnail_image_data, image_data_list
        )  # Send partnersUrl as a parameter
        blog_poster.close()

        loading.close()

        QMessageBox.information(
            self, "실행", f"'{keyword}'에 대한 포스팅이 완료되었습니다."
        )

    def open_settings_window(self):
        """설정 창을 열기 위한 메서드"""
        self.settings_window = SettingsWindow()
        self.settings_window.show()
