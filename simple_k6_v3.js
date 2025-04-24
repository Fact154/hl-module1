import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { Trend } from 'k6/metrics';

const BASE_URL_GET = 'http://localhost:8081';
const BASE_URL_POST = 'http://localhost:8080';

// Константы для настройки тестов
const TARGET_VUS = 400;

// Метрики
const readHeavyLatency = new Trend('read_heavy_latency');
const balancedLatency = new Trend('balanced_latency');
const writeHeavyLatency = new Trend('write_heavy_latency');
const postTrend = new Trend('post_latency'); // для POST-запросов отдельно

// Сценарии нагрузки
const scenarios = {
  read_heavy: {
    name: '95% read / 5% write',
    read_ratio: 0.95,
    duration: '1m',
    target: TARGET_VUS,
    metric: readHeavyLatency
  },
  balanced: {
    name: '50% read / 50% write',
    read_ratio: 0.5,
    duration: '1m',
    target: TARGET_VUS,
    metric: balancedLatency
  },
  write_heavy: {
    name: '5% read / 95% write',
    read_ratio: 0.05,
    duration: '1m',
    target: TARGET_VUS,
    metric: writeHeavyLatency
  }
};

export const options = {
  scenarios: {
    read_heavy_test: {
      executor: 'ramping-vus',
      startVUs: TARGET_VUS,
      stages: [
        { duration: scenarios.read_heavy.duration, target: scenarios.read_heavy.target },
      ],
      exec: 'mixedTraffic',
      env: { TEST_SCENARIO: 'read_heavy' }
    },
    balanced_test: {
      executor: 'ramping-vus',
      startVUs: TARGET_VUS,
      startTime: '1m',
      stages: [
        { duration: scenarios.balanced.duration, target: scenarios.balanced.target },
      ],
      exec: 'mixedTraffic',
      env: { TEST_SCENARIO: 'balanced' }
    },
    write_heavy_test: {
      executor: 'ramping-vus',
      startVUs: TARGET_VUS,
      startTime: '2m',
      stages: [
        { duration: scenarios.write_heavy.duration, target: scenarios.write_heavy.target },
      ],
      exec: 'mixedTraffic',
      env: { TEST_SCENARIO: 'write_heavy' }
    }
  },
  thresholds: {
    'read_heavy_latency': ['avg<500'],
    'balanced_latency': ['avg<500'],
    'write_heavy_latency': ['avg<500'],
    'post_latency': ['avg<500']
  }
};

// Генерация случайного "визитора"
function generateVisitor() {
  const names = ['Anna', 'Boris', 'Clara', 'David', 'Eva', 'Felix'];
  return {
    name: names[randomIntBetween(0, names.length - 1)],
    age: randomIntBetween(18, 80),
    ticketType: ['adult', 'child', 'senior'][randomIntBetween(0, 2)]
  };
}

// Главная функция для имитации трафика
export function mixedTraffic() {
  const scenario = scenarios[__ENV.TEST_SCENARIO];
  const random = Math.random();
  let res;

  if (random < scenario.read_ratio) {
    // GET-запрос
    const year = randomIntBetween(2024, 2025);
    const month = String(randomIntBetween(1, 12)).padStart(2, '0');
    res = http.get(`${BASE_URL_GET}/api/statistics/exhibit-rating?year=${year}&month=${month}`);
    check(res, { 'GET статус 200': (r) => r.status === 200 });
  } else {
    // POST-запрос
    const visitor = generateVisitor();
    res = http.post(
      `${BASE_URL_POST}/visitors`,
      JSON.stringify(visitor),
      { headers: { 'Content-Type': 'application/json' } }
    );
    check(res, { 'POST /visitors статус 200': (r) => r.status === 200 });
    postTrend.add(res.timings.duration);
  }

  // Добавляем в общую метрику
  scenario.metric.add(res.timings.duration);

  sleep(0.1);
}
