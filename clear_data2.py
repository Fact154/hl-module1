import psycopg2
from psycopg2 import sql

# Параметры подключения к базе данных
DB_HOST = "localhost"  # Хост
DB_NAME = "museum"     # Имя базы данных
DB_USER = "admin"      # Имя пользователя
DB_PASSWORD = "secret" # Пароль

# Создаем подключение к базе данных
def create_connection():
    return psycopg2.connect(
        host=DB_HOST,
        database=DB_NAME,
        user=DB_USER,
        password=DB_PASSWORD
    )

# Очистка данных в таблицах и в flyway_schema_history
def clear_data():
    try:
        conn = create_connection()
        cursor = conn.cursor()

        # Очистка таблиц
        tables = ['visitor', 'exhibit', 'tour']
        for table in tables:
            cursor.execute(sql.SQL("TRUNCATE TABLE {} CASCADE;").format(sql.Identifier(table)))
            print(f"Таблица {table} очищена.")

        # Очистка таблицы flyway_schema_history
        cursor.execute("TRUNCATE TABLE flyway_schema_history;")
        print("Таблица flyway_schema_history очищена.")

        conn.commit()
        cursor.close()
        conn.close()
    except Exception as e:
        print(f"Ошибка при очистке данных: {e}")

if __name__ == "__main__":
    clear_data()
