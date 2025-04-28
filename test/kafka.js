import http from 'k6/http';
import { sleep, check } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { Trend } from 'k6/metrics';
import { Writer, SCHEMA_TYPE_STRING, SchemaRegistry } from 'k6/x/kafka';

// Kafka setup
const writer = new Writer({
    brokers: ["hl22.zil:9094", "hl23.zil:9094"],
    topic: "museum-topic",  // <-- Изменено здесь
});
const schemaRegistry = new SchemaRegistry();

const BASE_URL = 'http://10.60.3.13:8080';
const SERVICE_URL = 'http://10.60.3.13:8081';

const readHeavyLatency = new Trend('read_heavy_latency');
const balancedLatency = new Trend('balanced_latency');
const writeHeavyLatency = new Trend('write_heavy_latency');

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
    }
  },
  thresholds: {
    'read_heavy_latency': ['avg<500'],
    'balanced_latency': ['avg<500'],
    'write_heavy_latency': ['avg<500']
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
        payload: visitorData
    };

    try {
        writer.produce({
            topic: "museum-topic",  // <-- И здесь изменено
            messages: [{
                key: schemaRegistry.serialize({
                    data: "test-key",
                    schemaType: SCHEMA_TYPE_STRING,
                }),
                value: schemaRegistry.serialize({
                    data: JSON.stringify(message),
                    schemaType: SCHEMA_TYPE_STRING,
                }),
            }],
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