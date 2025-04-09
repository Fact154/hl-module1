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

async def create_tour_async(session, exhibit_id, visitor_id, date, guide_name):
    async with session.get(f"{BASE_URL}/visitors/{visitor_id}") as visitor_response:
        if visitor_response.status != 200:
            print("Ошибка при получении данных посетителя")
            return None
        visitor = await visitor_response.json()

    async with session.get(f"{BASE_URL}/exhibits/{exhibit_id}") as exhibit_response:
        if exhibit_response.status != 200:
            print("Ошибка при получении данных экспоната")
            return None
        exhibit = await exhibit_response.json()

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

    async with session.post(f"{BASE_URL}/tours", json=tour_data) as response:
        if response.status != 200:
            print(f"Ошибка {response.status}: {await response.text()}")

def insert_visitors(n):
    for _ in range(n):
        full_name = fake.name()
        age = random.randint(10, 80)
        ticket_type = random.choice(["DISCOUNT", "FULL"])
        visitor = create_visitor(full_name, age, ticket_type)


def insert_exhibits(n):
    for _ in range(n):
        name = fake.word().capitalize() + " Exhibit"
        era = random.choice(["Ancient", "Medieval", "Renaissance", "Modern"])
        description = fake.sentence()
        exhibit = create_exhibit(name, era, description)


async def create_grouped_tours_async(tours):
    async with aiohttp.ClientSession() as session:
        visitors_response = await session.get(f"{BASE_URL}/visitors")
        exhibits_response = await session.get(f"{BASE_URL}/exhibits")

        if visitors_response.status != 200 or exhibits_response.status != 200:
            print("Ошибка при получении списка посетителей или экспонатов")
            return

        visitors = await visitors_response.json()
        exhibits = await exhibits_response.json()

        if not visitors or not exhibits:
            print("Ошибка: Сначала добавьте посетителей и экспонаты!")
            return

        visitors_per_tour = len(visitors) // tours

        tasks = []
        for tour_num in range(tours):
            start_visitor_idx = tour_num * visitors_per_tour
            end_visitor_idx = start_visitor_idx + visitors_per_tour

            tour_visitors = visitors[start_visitor_idx:end_visitor_idx]

            num_exhibits = random.randint(1, len(exhibits))
            selected_exhibits = random.sample(exhibits, num_exhibits)

            print(f"\nТур {tour_num + 1}:")
            print(f"Посетители: {[v['fullName'] for v in tour_visitors]}")
            print(f"Экспонаты: {[e['name'] for e in selected_exhibits]}")

            for visitor in tour_visitors:
                for exhibit in selected_exhibits:
                    date = fake.date_between(start_date="-1y", end_date="today").strftime("%Y-%m-%d")
                    guide_name = fake.name() if random.random() > 0.5 else None

                    task = create_tour_async(session, exhibit["id"], visitor["id"], date, guide_name)
                    tasks.append(task)

        await asyncio.gather(*tasks)

def main():
    parser = argparse.ArgumentParser(description='Генерация данных для музея')
    parser.add_argument('--visitors', type=int, help='Количество посетителей для генерации')
    parser.add_argument('--exhibits', type=int, help='Количество экспонатов для генерации')
    parser.add_argument('--tours', type=int, help='Количество экскурсий  для генерации')
    
    args = parser.parse_args()
        
    if args.visitors:
        insert_visitors(args.visitors)
    if args.exhibits:
        insert_exhibits(args.exhibits)
    if args.tours:
        asyncio.run(create_grouped_tours_async(args.tours))
        
    print("Генерация данных завершена.")

if __name__ == "__main__":
    main() 