import pandas as pd
import matplotlib.pyplot as plt

# Загрузка данных из CSV
csv_file = "results.csv"  # Укажите путь к вашему файлу
columns = ["metric_name", "timestamp", "metric_value"]
df = pd.read_csv(csv_file, usecols=columns)

# Фильтрация данных по времени ответа (http_req_duration) и количеству пользователей (vus)
duration_df = df[df["metric_name"] == "http_req_duration"].copy()
users_df = df[df["metric_name"] == "vus"].copy()

# Преобразование временной метки в формат времени
duration_df.loc[:, "timestamp"] = pd.to_datetime(duration_df["timestamp"], unit="s")
users_df.loc[:, "timestamp"] = pd.to_datetime(users_df["timestamp"], unit="s")

# Объединение данных по временной метке
merged_df = pd.merge(duration_df, users_df, on="timestamp", suffixes=("_duration", "_users"))

# Построение графика
plt.figure(figsize=(10, 5))
plt.scatter( merged_df["metric_value_users"], merged_df["metric_value_duration"], alpha=0.5, label="Пользователи vs Время ответа")
plt.ylabel("Время ответа (сек)")
plt.xlabel("Количество пользователей")
plt.title("Зависимость количества пользователей от времени ответа")
plt.legend()
plt.grid()
plt.show()

