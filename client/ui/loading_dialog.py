# loading_dialog.py
from PyQt5.QtWidgets import QDialog, QVBoxLayout, QLabel, QProgressBar
from PyQt5.QtCore import Qt, QTimer

class LoadingDialog(QDialog):
    def __init__(self, message="잠시만 기다려주세요...", parent=None):
        super().__init__(parent)
        self.setWindowTitle("로딩 중")
        self.setModal(True)
        self.setWindowFlags(self.windowFlags() | Qt.FramelessWindowHint)
        self.setStyleSheet("""
            QDialog {
                background-color: white;
                border: 1px solid #ccc;
                border-radius: 10px;
            }
            QLabel {
                font-size: 16px;
                color: #333;
            }
        """)

        layout = QVBoxLayout()
        layout.setAlignment(Qt.AlignCenter)

        self.label = QLabel(message)
        self.label.setAlignment(Qt.AlignCenter)

        self.progress = QProgressBar()
        self.progress.setRange(0, 0)  # 무한 로딩 애니메이션
        self.progress.setFixedWidth(250)

        layout.addWidget(self.label)
        layout.addWidget(self.progress)

        self.setLayout(layout)
        self.setFixedSize(300, 120)
