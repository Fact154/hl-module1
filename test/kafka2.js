import http from 'k6/http';
import { sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { Trend, Rate } from 'k6/metrics';
import { Writer, SCHEMA_TYPE_STRING, SchemaRegistry } from 'k6/x/kafka';

// Kafka setup
const writer = new Writer({
    brokers: ["hl22.zil:9094", "hl23.zil:9094"],
    topic: "museum-topic",
});
const schemaRegistry = new SchemaRegistry();

const TARGET_VUS = 600;

const BASE_URL = 'http://10.60.3.13:30000';
const SERVICE_URL = 'http://10.60.3.13:30001';

const readHeavyLatency = new Trend('read_heavy_latency');
const balancedLatency = new Trend('balanced_latency');
const writeHeavyLatency = new Trend('write_heavy_latency');

// Метрики для времени отклика и процента неудачных запросов
const responseTime = new Trend('response_time');
const failureRate = new Rate('failure_rate');

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

function generateVisitor() {
    const ticketTypes = ['DISCOUNT', 'FULL'];
  
    return {
      fullName: `USER${randomIntBetween(10000, 99999)}`,  // Генерация уникального имени
      age: randomIntBetween(18, 90),                      // Генерация случайного возраста
      ticketType: ticketTypes[randomIntBetween(0, 1)],    // Случайный выбор между 'DISCOUNT' и 'FULL'
    };
  }
  
  function sendVisitorToKafka(visitorData) {
      const message = {
          entity: "VISITOR",
          operation: "POST",
          payload: visitorData
      };
  
      try {
          writer.produce({
              topic: "museum-topic",  // Топик для сообщений
              messages: [{
                  key: schemaRegistry.serialize({
                      data: "visitor-key",  // Ключ для сообщений
                      schemaType: SCHEMA_TYPE_STRING,
                  }),
                  value: schemaRegistry.serialize({
                      data: JSON.stringify(message),
                      schemaType: SCHEMA_TYPE_STRING,
                  }),
              }],
          });
  
      } catch (error) {
          console.error("❌ Failed to send message to Kafka:", error);
      }
  }
  
  export function mixedTraffic() {
    const scenario = scenarios[__ENV.TEST_SCENARIO];
    const random = Math.random();
    let res;
  
    if (random < scenario.read_ratio) {
      // Генерация случайного года и месяца для GET-запроса
      const year = randomIntBetween(2024, 2025);
      const month = randomIntBetween(1, 12);
  
      // Формируем запрос с параметрами года и месяца
      res = http.get(`${SERVICE_URL}/api/statistics/exhibit-rating?year=${year}&month=${month}`);
    } else {
      // Генерация случайных данных для POST-запроса через Kafka
      const visitor = generateVisitor();
      sendVisitorToKafka(visitor);
      res = { timings: { duration: 100 } }; // Default duration for Kafka operations
    }
  
    scenario.metric.add(res.timings.duration);
  
    // Запись метрик
    responseTime.add(res.timings.duration);
    failureRate.add(res.status !== 200);
  
    sleep(0.1);
  }
  
  export default function () {
    const res = http.get('http://main-service-internal:30000/core/crash');
    
    // Запись метрик
    responseTime.add(res.timings.duration);
    failureRate.add(res.status !== 200);
  
    sleep(1);
  }
  