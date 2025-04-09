import requests

BASE_URL = "http://localhost:8080"

def clear_db_via_api():
    try:
        # Очищаем tours
        response = requests.get(f"{BASE_URL}/tours")
        if response.status_code == 200:
            tours = response.json()
            for tour in tours:
                requests.delete(f"{BASE_URL}/tours/{tour['id']}")
        print(f"[API] Таблица tours очищена")
        
        # Очищаем exhibits
        response = requests.get(f"{BASE_URL}/exhibits")
        if response.status_code == 200:
            exhibits = response.json()
            for exhibit in exhibits:
                requests.delete(f"{BASE_URL}/exhibits/{exhibit['id']}")
        print(f"[API] Таблица exhibits очищена")
        
        # Очищаем visitors
        response = requests.get(f"{BASE_URL}/visitors")
        if response.status_code == 200:
            visitors = response.json()
            for visitor in visitors:
                requests.delete(f"{BASE_URL}/visitors/{visitor['id']}")
        print(f"[API] Таблица visitors очищена")
        
    except Exception as e:
        print(f"Ошибка при очистке базы данных: {e}")

if __name__ == "__main__":
    clear_db_via_api()