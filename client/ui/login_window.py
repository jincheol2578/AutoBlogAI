# ui/login_window.py

from PyQt5.QtWidgets import QWidget, QLabel, QLineEdit, QPushButton, QVBoxLayout, QHBoxLayout, QMessageBox
from PyQt5.QtCore import Qt
from api.api_request import ApiClient
from ui.keyword_window import KeywordWindow

class LoginWindow(QWidget):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("네이버 블로그 자동화 - 로그인")
        self.setGeometry(100, 100, 400, 250)  # 창 크기 키움
        self.setStyleSheet(self.get_styles())  # 스타일 시트 적용
        self.init_ui()

    def init_ui(self):
        # 아이디 레이블과 입력 필드
        self.label_id = QLabel("아이디")
        self.input_id = QLineEdit()
        self.input_id.setPlaceholderText("아이디를 입력하세요.")  # placeholder 추가

        # 비밀번호 레이블과 입력 필드
        self.label_pw = QLabel("비밀번호")
        self.input_pw = QLineEdit()
        self.input_pw.setEchoMode(QLineEdit.Password)
        self.input_pw.setPlaceholderText("비밀번호를 입력하세요.")  # placeholder 추가

        # 로그인 버튼 크기 조정
        self.button_login = QPushButton("로그인")
        self.button_login.setFixedHeight(40)  # 버튼 높이 설정
        self.button_login.clicked.connect(self.handle_login)

        # 레이아웃 설정
        layout = QVBoxLayout()
        layout.addWidget(self.label_id)
        layout.addWidget(self.input_id)
        layout.addWidget(self.label_pw)
        layout.addWidget(self.input_pw)
        layout.addWidget(self.button_login)

        self.setLayout(layout)

    def handle_login(self):
        user_id = self.input_id.text()
        user_pw = self.input_pw.text()

        if not user_id or not user_pw:
            QMessageBox.warning(self, "입력 오류", "아이디와 비밀번호를 모두 입력하세요.")
            return

        # API로 로그인 요청
        login_success = ApiClient.login(user_id, user_pw)

        if login_success:
            QMessageBox.information(self, "로그인 성공", "로그인에 성공했습니다.")
            self.open_keyword_window()
        else:
            QMessageBox.warning(self, "로그인 실패", "아이디 또는 비밀번호를 확인하세요.")

    def open_keyword_window(self):
        self.keyword_window = KeywordWindow()
        self.keyword_window.show()
        self.close()

    def get_styles(self):
        # 부트스트랩 스타일을 QSS로 흉내 내기
        return """
            QWidget {
                font-family: 'Arial', sans-serif;
                background-color: #f8f9fa;
            }

            QLabel {
                font-size: 14px;
                font-weight: bold;
                color: #495057;
            }

            QLineEdit {
                padding: 10px;
                border: 1px solid #ccc;
                border-radius: 5px;
                font-size: 14px;
                margin-bottom: 10px;
            }

            QLineEdit:focus {
                border-color: #28a745;
                box-shadow: 0 0 5px rgba(40, 167, 69, 0.5);
            }

            QPushButton {
                background-color: #28a745;
                color: white;
                border: none;
                border-radius: 5px;
                font-size: 16px;
                padding: 10px;
                font-weight: bold;
            }

            QPushButton:hover {
                background-color: #218838;
            }

            QPushButton:pressed {
                background-color: #1e7e34;
            }

            QWidget {
                padding: 20px;
            }
        """
