import http from 'k6/http';
import { sleep, check } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { Trend, Rate } from 'k6/metrics';
import { Writer } from 'k6/x/kafka';

// Kafka setup
const writer = new Writer({
    brokers: ["hl22.zil:9094", "hl23.zil:9094"],
    topic: "museum-topic"
});

const BASE_URL = 'http://10.60.3.13:8080';
const SERVICE_URL = 'http://10.60.3.13:8081';

const readHeavyLatency = new Trend('read_heavy_latency');
const balancedLatency = new Trend('balanced_latency');
const writeHeavyLatency = new Trend('write_heavy_latency');
const responseTime = new Trend('response_time');
const failureRate = new Rate('failure_rate');

const scenarios = {
  read_heavy: {
    name: '95% read / 5% write',
    read_ratio: 0.95,
    duration: '1m',
    target: 150,
    metric: readHeavyLatency
  },
  balanced: {
    name: '50% read / 50% write',
    read_ratio: 0.5,
    duration: '1m',
    target: 150,
    metric: balancedLatency
  },
  write_heavy: {
    name: '5% read / 95% write',
    read_ratio: 0.05,
    duration: '1m',
    target: 150,
    metric: writeHeavyLatency
  },
  circuit_breaker: {
    name: 'Circuit Breaker Test',
    duration: '5m',
    target: 100,
    metric: responseTime
  }
};

export const options = {
  scenarios: {
    read_heavy_test: {
      executor: 'ramping-vus',
      startVUs: 150,
      stages: [
        { duration: scenarios.read_heavy.duration, target: scenarios.read_heavy.target },
      ],
      exec: 'mixedTraffic',
      env: { TEST_SCENARIO: 'read_heavy' }
    },
    balanced_test: {
      executor: 'ramping-vus',
      startVUs: 150,
      startTime: '1m',
      stages: [
        { duration: scenarios.balanced.duration, target: scenarios.balanced.target },
      ],
      exec: 'mixedTraffic',
      env: { TEST_SCENARIO: 'balanced' }
    },
    write_heavy_test: {
      executor: 'ramping-vus',
      startVUs: 150,
      startTime: '2m',
      stages: [
        { duration: scenarios.write_heavy.duration, target: scenarios.write_heavy.target },
      ],
      exec: 'mixedTraffic',
      env: { TEST_SCENARIO: 'write_heavy' }
    },
    circuit_breaker_test: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        { duration: '1m', target: 100 }, // Увеличение до 100 пользователей
        { duration: '3m', target: 100 }, // Поддержание нагрузки
        { duration: '1m', target: 0 },   // Снижение нагрузки
      ],
    },
  },
  thresholds: {
    'read_heavy_latency': ['avg<500'],
    'balanced_latency': ['avg<500'],
    'write_heavy_latency': ['avg<500'],
    'response_time': ['p(95)<500'], // 95% запросов должны быть быстрее 500 мс
    'failure_rate': ['rate<0.1'],   // Менее 10% запросов должны завершаться неудачей
  }
};

// Генерация случайного "визитора"
function generateVisitor() {
  return {
    fullName: `User ${__VU}-${__ITER}`,
    age: Math.floor(Math.random() * 70) + 10,
    ticketType: Math.random() > 0.5 ? 'FULL' : 'DISCOUNT',
  };
}

function sendToKafka(visitorData) {
    const message = {
        entity: "VISITOR",
        operation: "POST",
        payload: {
            fullName: `User ${__VU}-${__ITER}`,
            age: randomIntBetween(18, 80),
            ticketType: Math.random() > 0.5 ? 'FULL' : 'DISCOUNT'
        }
    };

    try {
        writer.produce({
            key: `test-key-${__VU}-${__ITER}`,
            value: JSON.stringify(message)
        });
    } catch (error) {
        console.error("❌ Ошибка при отправке сообщения в Kafka:", error);
    }
}

export function mixedTraffic() {
  const scenario = scenarios[__ENV.TEST_SCENARIO];
  const random = Math.random();
  let res;

  if (random < scenario.read_ratio) {
    // GET-запрос
    const year = randomIntBetween(2024, 2025);
    const month = String(randomIntBetween(1, 12)).padStart(2, '0');
    res = http.get(`${BASE_URL}/api/statistics/exhibit-rating?year=${year}&month=${month}`);
    check(res, { 'GET статус 200': (r) => r.status === 200 }); // Проверка статуса ответа
  } else {
    // Генерация посетителя и отправка в Kafka
    const visitor = generateVisitor();
    sendToKafka(visitor);
    res = { timings: { duration: 100 } }; // Моделирование задержки для записи
  }

  scenario.metric.add(res.timings.duration);

  sleep(0.1);
}

export function teardown() {
  writer.close();
}

export default function() {
  const start = Date.now();
  const response = http.get(`${SERVICE_URL}/tours`);
  
  // Записываем время отклика
  responseTime.add(Date.now() - start);
  
  // Записываем процент неудачных запросов
  failureRate.add(response.status !== 200);
  
  sleep(1);
}