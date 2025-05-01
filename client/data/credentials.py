import os
from cryptography.fernet import Fernet

class CredentialManager:
    def __init__(self, key_file='data/key.key', credentials_file='data/credentials.pkl'):
        self.key_file = key_file
        self.credentials_file = credentials_file

        # data 디렉토리가 없다면 생성
        if not os.path.exists('data'):
            os.makedirs('data')

        # key 파일이 없으면 새로 생성
        if not os.path.exists(self.key_file):
            self.key = Fernet.generate_key()
            with open(self.key_file, 'wb') as key_file:
                key_file.write(self.key)
        else:
            with open(self.key_file, 'rb') as key_file:
                self.key = key_file.read()

        self.cipher_suite = Fernet(self.key)

    def encrypt_data(self, data):
        """암호화"""
        encrypted_data = self.cipher_suite.encrypt(data.encode())
        return encrypted_data

    def decrypt_data(self, encrypted_data):
        """복호화"""
        decrypted_data = self.cipher_suite.decrypt(encrypted_data).decode()
        return decrypted_data

    def save_credentials(self, username, password):
        """아이디와 비밀번호 암호화하여 저장"""
        encrypted_username = self.encrypt_data(username)
        encrypted_password = self.encrypt_data(password)

        with open(self.credentials_file, 'wb') as f:
            f.write(encrypted_username + b'\n')
            f.write(encrypted_password)

    def load_credentials(self):
        """저장된 암호화된 아이디와 비밀번호 불러오기"""
        if not os.path.exists(self.credentials_file):
            return None, None

        with open(self.credentials_file, 'rb') as f:
            encrypted_username = f.readline().strip()
            encrypted_password = f.readline().strip()

        username = self.decrypt_data(encrypted_username)
        password = self.decrypt_data(encrypted_password)

        return username, password
