import sys
from PyQt5.QtWidgets import QApplication, QMainWindow, QWidget, QVBoxLayout, QLabel
from PyQt5.QtCore import QFile, QTextStream
from ui.login_window import LoginWindow
sys.path.append('C:/Git/AutoBlogAI')

def load_stylesheet(file_path):
    """QSS 파일을 불러오는 함수"""
    file = QFile(file_path)
    file.open(QFile.ReadOnly | QFile.Text)
    stream = QTextStream(file)
    return stream.readAll()

if __name__ == "__main__":
    app = QApplication(sys.argv)

    # QSS 파일을 읽고 애플리케이션에 적용
    stylesheet = load_stylesheet("/res/styles.qss")  # QSS 파일 경로
    app.setStyleSheet(stylesheet)

    # 로그인 창 열기
    window = LoginWindow()
    window.show()

    sys.exit(app.exec_())
