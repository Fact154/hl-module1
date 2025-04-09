import psycopg2
from faker import Faker
import random
from datetime import datetime, timedelta

# Подключение к БД
conn = psycopg2.connect(
    dbname="museum",
    user="admin",
    password="secret",
    host="localhost",
    port="5432"
)
cursor = conn.cursor()

fake = Faker()

# Функция для заполнения таблицы Visitor
def insert_visitors(n):
    visitors = []
    for _ in range(n):
        full_name = fake.name()
        age = random.randint(10, 80)
        ticket_type = random.choice(["DISCOUNT", "FULL"])
        visitors.append((full_name, age, ticket_type))

    cursor.executemany("INSERT INTO visitor (full_name, age, ticket_type) VALUES (%s, %s, %s)", visitors)
    conn.commit()
    print(f"Добавлено {n} посетителей")

# Функция для заполнения таблицы Exhibit
def insert_exhibits(n):
    exhibits = []
    for _ in range(n):
        name = fake.word().capitalize() + " Exhibit"
        era = random.choice(["Ancient", "Medieval", "Renaissance", "Modern"])
        description = fake.sentence()
        exhibits.append((name, era, description))

    cursor.executemany("INSERT INTO exhibit (name, era, description) VALUES (%s, %s, %s)", exhibits)
    conn.commit()
    print(f"Добавлено {n} экспонатов")

# Функция для заполнения таблицы Tour
def insert_tours(n):
    cursor.execute("SELECT id FROM visitor")
    visitor_ids = [row[0] for row in cursor.fetchall()]

    cursor.execute("SELECT id FROM exhibit")
    exhibit_ids = [row[0] for row in cursor.fetchall()]

    if not visitor_ids or not exhibit_ids:
        print("Ошибка: Сначала добавьте посетителей и экспонаты!")
        return

    tours = []
    for _ in range(n):
        exhibit_id = random.choice(exhibit_ids)
        visitor_id = random.choice(visitor_ids)
        date = fake.date_between(start_date="-1y", end_date="today")
        guide_name = fake.name() if random.random() > 0.5 else None
        tours.append((exhibit_id, visitor_id, date, guide_name))

    cursor.executemany("INSERT INTO tour (exhibit_id, visitor_id, date, guide_name) VALUES (%s, %s, %s, %s)", tours)
    conn.commit()
    print(f"Добавлено {n} экскурсий")

# Заполняем таблицы
insert_visitors(10)   # 10 посетителей
insert_exhibits(5)    # 5 экспонатов
insert_tours(15)      # 15 экскурсий

# Закрываем соединение
cursor.close()
conn.close()
print("Генерация данных завершена.")
