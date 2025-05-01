from PyQt5.QtWidgets import QWidget, QVBoxLayout, QLabel, QLineEdit, QPushButton, QMessageBox
import os
import pickle

class SettingsWindow(QWidget):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("설정")
        self.setGeometry(100, 100, 400, 200)

        self.init_ui()

        # 아이디와 비밀번호를 불러와서 입력창에 설정
        self.load_credentials()

    def init_ui(self):
        layout = QVBoxLayout()

        self.label_id = QLabel("네이버 아이디:")
        self.id_input = QLineEdit(self)

        self.label_pw = QLabel("네이버 비밀번호:")
        self.pw_input = QLineEdit(self)
        self.pw_input.setEchoMode(QLineEdit.Password)

        # 설정 저장 버튼
        self.save_button = QPushButton("저장", self)
        self.save_button.clicked.connect(self.save_credentials)

        # 레이아웃 설정
        layout.addWidget(self.label_id)
        layout.addWidget(self.id_input)
        layout.addWidget(self.label_pw)
        layout.addWidget(self.pw_input)
        layout.addWidget(self.save_button)

        self.setLayout(layout)

    def save_credentials(self):
        """설정된 아이디와 비밀번호 저장"""
        naver_id = self.id_input.text()
        naver_pw = self.pw_input.text()

        if not naver_id or not naver_pw:
            QMessageBox.warning(self, "경고", "아이디와 비밀번호를 모두 입력해주세요.")
            return

        # 'data' 폴더가 없으면 생성
        if not os.path.exists('client/data'):
            os.makedirs('client/data')

        # 아이디와 비밀번호를 파일에 저장
        file_path = os.path.join('client', 'data', "credentials.pkl")
        print(f"파일 경로: {file_path}")
        credentials = {"naver_id": naver_id, "naver_pw": naver_pw}
        with open(file_path, "wb") as f:
            pickle.dump(credentials, f)

        # 저장 완료 팝업 창 띄우기
        msg_box = QMessageBox(self)
        msg_box.setIcon(QMessageBox.Information)
        msg_box.setWindowTitle("저장 완료")
        msg_box.setText("아이디와 비밀번호가 저장되었습니다.")
        msg_box.setStandardButtons(QMessageBox.Ok)

        # 확인 버튼 클릭 시 팝업창 닫기 및 추가 작업
        if msg_box.exec_() == QMessageBox.Ok:
            self.close()

    def load_credentials(self):
        file_path = os.path.join('client', 'data', "credentials.pkl")
        if os.path.exists(file_path):
            with open(file_path, "rb") as f:
                credentials = pickle.load(f)
                # 불러온 아이디와 비밀번호를 입력창에 채우기
                self.id_input.setText(credentials["naver_id"])
                self.pw_input.setText(credentials["naver_pw"])
                print(credentials)
                return credentials["naver_id"], credentials["naver_pw"]
        else:
            return None, None  # 파일이 없으면 None 반환
