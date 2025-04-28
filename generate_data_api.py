import requests
from faker import Faker
import random
from datetime import datetime, timedelta
import argparse
import json
import aiohttp
import asyncio

BASE_URL = "http://localhost:8080"
fake = Faker()

def create_visitor(full_name, age, ticket_type):
    response = requests.post(
        f"{BASE_URL}/visitors",
        json={
            "fullName": full_name,
            "age": age,
            "ticketType": ticket_type
        }
    )
    if response.status_code != 200:
        print(f"Ошибка {response.status_code}: {response.text}")
    return response.json() if response.status_code == 200 else None

def create_exhibit(name, era, description):
    response = requests.post(
        f"{BASE_URL}/exhibits",
        json={
            "name": name,
            "era": era,
            "description": description
        }
    )
    if response.status_code != 200:
        print(f"Ошибка {response.status_code}: {response.text}")
    return response.json() if response.status_code == 200 else None

def create_tour(exhibit_id, visitor_id, date, guide_name):
    # Получаем данные посетителя и экспоната
    visitor_response = requests.get(f"{BASE_URL}/visitors/{visitor_id}")
    exhibit_response = requests.get(f"{BASE_URL}/exhibits/{exhibit_id}")
    
    if visitor_response.status_code != 200 or exhibit_response.status_code != 200:
        print("Ошибка при получении данных посетителя или экспоната")
        return None
        
    visitor = visitor_response.json()
    exhibit = exhibit_response.json()
    
    # Формируем данные для отправки
    tour_data = {
        "exhibit": {
            "id": exhibit_id,
            "name": exhibit["name"],
            "era": exhibit["era"],
            "description": exhibit["description"]
        },
        "visitor": {
            "id": visitor_id,
            "fullName": visitor["fullName"],
            "age": visitor["age"],
            "ticketType": visitor["ticketType"]
        },
        "date": date,
        "guideName": guide_name
    }
    
    response = requests.post(
        f"{BASE_URL}/tours",
        json=tour_data
    )
    
    if response.status_code != 200:
        print(f"Ошибка {response.status_code}: {response.text}")
    return response.json() if response.status_code == 200 else None

def insert_visitors(n):
    for _ in range(n):
        full_name = fake.name()
        age = random.randint(10, 80)
        ticket_type = random.choice(["DISCOUNT", "FULL"])
        visitor = create_visitor(full_name, age, ticket_type)
        if visitor:
            print(f"Создан посетитель: {full_name}")
        else:
            print(f"Ошибка при создании посетителя: {full_name}")

def insert_exhibits(n):
    for _ in range(n):
        name = fake.word().capitalize() + " Exhibit"
        era = random.choice(["Ancient", "Medieval", "Renaissance", "Modern"])
        description = fake.sentence()
        exhibit = create_exhibit(name, era, description)
        if exhibit:
            print(f"Создан экспонат: {name}")
        else:
            print(f"Ошибка при создании экспоната: {name}")

# def insert_tours(n):
#     # Получаем список посетителей и экспонатов
#     visitors_response = requests.get(f"{BASE_URL}/visitors")
#     exhibits_response = requests.get(f"{BASE_URL}/exhibits")
    
#     if visitors_response.status_code != 200 or exhibits_response.status_code != 200:
#         print("Ошибка при получении списка посетителей или экспонатов")
#         return
        
#     visitors = visitors_response.json()
#     exhibits = exhibits_response.json()
    
#     if not visitors or not exhibits:
#         print("Ошибка: Сначала добавьте посетителей и экспонаты!")
#         return

#     for _ in range(n):
#         exhibit = random.choice(exhibits)
#         visitor = random.choice(visitors)
#         date = fake.date_between(start_date="-1y", end_date="today").strftime("%Y-%m-%d")
#         guide_name = fake.name() if random.random() > 0.5 else None
        
#         tour = create_tour(exhibit["id"], visitor["id"], date, guide_name)
#         if tour:
#             print(f"Экскурсия успешно создана для посетителя {visitor['fullName']} к экспонату {exhibit['name']}")
#         else:
#             print(f"Ошибка при создании экскурсии")

def create_grouped_tours(tours):
    # Получаем список посетителей и экспонатов
    visitors_response = requests.get(f"{BASE_URL}/visitors")
    exhibits_response = requests.get(f"{BASE_URL}/exhibits")
    
    if visitors_response.status_code != 200 or exhibits_response.status_code != 200:
        print("Ошибка при получении списка посетителей или экспонатов")
        return
        
    visitors = visitors_response.json()
    exhibits = exhibits_response.json()
    
    if not visitors or not exhibits:
        print("Ошибка: Сначала добавьте посетителей и экспонаты!")
        return

    # Вычисляем количество посетителей и экспонатов в каждом туре
    visitors_per_tour = len(visitors) // tours
    exhibits_per_tour = len(exhibits) // tours
    
    # Создаем туры
    for tour_num in range(tours):
        # Вычисляем индексы для текущего тура
        start_visitor_idx = tour_num * visitors_per_tour
        end_visitor_idx = start_visitor_idx + visitors_per_tour
        
        start_exhibit_idx = tour_num * exhibits_per_tour
        end_exhibit_idx = start_exhibit_idx + exhibits_per_tour
        
        # Получаем посетителей и экспонаты для текущего тура
        tour_visitors = visitors[start_visitor_idx:end_visitor_idx]
        tour_exhibits = exhibits[start_exhibit_idx:end_exhibit_idx]
        
        print(f"\nТур {tour_num + 1}:")
        print(f"Посетители: {[v['fullName'] for v in tour_visitors]}")
        print(f"Экспонаты: {[e['name'] for e in tour_exhibits]}")
        
        # Создаем экскурсии для каждой пары посетитель-экспонат в туре
        for visitor in tour_visitors:
            for exhibit in tour_exhibits:
                date = fake.date_between(start_date="-1y", end_date="today").strftime("%Y-%m-%d")
                guide_name = fake.name() if random.random() > 0.5 else None
                
                create_tour(exhibit["id"], visitor["id"], date, guide_name)

async def create_async_tours():
    async with aiohttp.ClientSession() as session:
        # Получаем первых 10 посетителей
        async with session.get(f"{BASE_URL}/visitors") as response:
            if response.status != 200:
                print("Ошибка при получении списка посетителей")
                return
            visitors = await response.json()
            first_10_visitors = visitors[:10]

        # Получаем первых 10 экспонатов
        async with session.get(f"{BASE_URL}/exhibits") as response:
            if response.status != 200:
                print("Ошибка при получении списка экспонатов")
                return
            exhibits = await response.json()
            first_10_exhibits = exhibits[:10]

        if not first_10_visitors or not first_10_exhibits:
            print("Ошибка: Недостаточно посетителей или экспонатов!")
            return

        # Создаем туры для всех пар посетитель-экспонат
        tasks = []
        for visitor in first_10_visitors:
            for exhibit in first_10_exhibits:
                date = fake.date_between(start_date="-1y", end_date="today").strftime("%Y-%m-%d")
                guide_name = fake.name() if random.random() > 0.5 else None
                tasks.append(
                    create_tour_async(session, exhibit["id"], visitor["id"], date, guide_name)
                )

        results = await asyncio.gather(*tasks, return_exceptions=True)
        for result in results:
            if isinstance(result, Exception):
                print(f"Ошибка при создании тура: {result}")
            elif result:
                print(f"Тур создан: {result}")

async def create_tour_async(session, exhibit_id, visitor_id, date, guide_name):
    # Получаем данные посетителя и экспоната
    async with session.get(f"{BASE_URL}/visitors/{visitor_id}") as visitor_response:
        if visitor_response.status != 200:
            raise Exception(f"Ошибка при получении данных посетителя: {visitor_response.status}")
        visitor = await visitor_response.json()

    async with session.get(f"{BASE_URL}/exhibits/{exhibit_id}") as exhibit_response:
        if exhibit_response.status != 200:
            raise Exception(f"Ошибка при получении данных экспоната: {exhibit_response.status}")
        exhibit = await exhibit_response.json()

    # Формируем данные для отправки
    tour_data = {
        "exhibit": {
            "id": exhibit_id,
            "name": exhibit["name"],
            "era": exhibit["era"],
            "description": exhibit["description"]
        },
        "visitor": {
            "id": visitor_id,
            "fullName": visitor["fullName"],
            "age": visitor["age"],
            "ticketType": visitor["ticketType"]
        },
        "date": date,
        "guideName": guide_name
    }

    # Отправляем POST-запрос для создания тура
    async with session.post(f"{BASE_URL}/tours", json=tour_data) as response:
        if response.status != 200:
            raise Exception(f"Ошибка {response.status}: {await response.text()}")
        return await response.json()

def main():
    parser = argparse.ArgumentParser(description='Генерация данных для музея')
    parser.add_argument('--visitors', type=int, help='Количество посетителей для генерации')
    parser.add_argument('--exhibits', type=int, help='Количество экспонатов для генерации')
    parser.add_argument('--tours', type=int, help='Количество экскурсий для генерации')
    parser.add_argument('--async-tours', action='store_true', help='Создать асинхронные туры для первых 10 посетителей и экспонатов')
    
    args = parser.parse_args()
        
    if args.visitors:
        insert_visitors(args.visitors)
    if args.exhibits:
        insert_exhibits(args.exhibits)
    if args.tours:
        create_grouped_tours(args.tours)
    if args.async_tours:
        asyncio.run(create_async_tours())
        
    print("Генерация данных завершена.")

if __name__ == "__main__":
    main() 