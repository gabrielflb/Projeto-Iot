import requests
import os
import time
import random
from dotenv import load_dotenv

print("Debug: O script sensores.py foi iniciado.")
load_dotenv()

BASE_URL = os.getenv("BASE_URL", "http://localhost:8080")
EMAIL = os.getenv("EMAIL", "")
SENHA = os.getenv("SENHA", "")

def get_jwt_token():
    login_url = f"{BASE_URL}/login"
    credentials = {"email": EMAIL, "password": SENHA}
    try:
        response = requests.post(login_url, json=credentials)
        response.raise_for_status()  
        print("Token JWT obtido com sucesso!")
        return response.json().get("acessToken")
    except requests.exceptions.RequestException as e:
        print(f"Erro ao obter o token JWT: {e}")
        return None

def simulate_device(resource_id, token):
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json",
        "Email": EMAIL
    }
    resource_url = f"{BASE_URL}/api/resources/{resource_id}/release"

    print(f"Iniciando simulação para o Recurso ID: {resource_id}")

    while True:
        try:
            print(f"Enviando requisição para liberar o recurso {resource_id}...")
            response = requests.post(resource_url, headers=headers)

            if response.status_code == 200:
                print(f"Recurso {resource_id} liberado com sucesso.")
            else:
                print(f"Erro ao liberar o recurso {resource_id}: {response.status_code} - {response.text}")

        except requests.exceptions.RequestException as e:
            print(f"Erro na requisição para o recurso {resource_id}: {e}")

        time.sleep(30)

if __name__ == "__main__":
    print("Iniciando simulador de sensor...")
    
    if not EMAIL or not SENHA:
        print("ERRO: As variáveis de ambiente EMAIL e SENHA não foram encontradas.")
        print("Verifique se o seu arquivo .env está no mesmo diretório e configurado corretamente.")
    else:
        jwt_token = get_jwt_token()
        if jwt_token:
            resource_to_simulate = random.choice([1, 2])
            simulate_device(resource_to_simulate, jwt_token)
        else:
            print("Não foi possível iniciar a simulação por falha na autenticação.")