import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { Trend } from 'k6/metrics';

const BASE_URL = 'http://localhost:8080';

const scenarios = {
  read_heavy: {
    name: '95% read / 5% write',
    read_ratio: 0.95,
    duration: '1m',
    target: 100
  },
  balanced: {
    name: '50% read / 50% write',
    read_ratio: 0.5,
    duration: '1m',
    target: 100
  },
  write_heavy: {
    name: '5% read / 95% write',
    read_ratio: 0.05,
    duration: '1m',
    target: 100
  }
};

export const options = {
  scenarios: {
    read_heavy_test: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: scenarios.read_heavy.target },
        { duration: scenarios.read_heavy.duration, target: scenarios.read_heavy.target },
        { duration: '30s', target: 0 },
      ],
      exec: 'mixedTraffic',
      env: { TEST_SCENARIO: 'read_heavy' }
    },
    balanced_test: {
      executor: 'ramping-vus',
      startVUs: 0,
      startTime: '2m',
      stages: [
        { duration: '30s', target: scenarios.balanced.target },
        { duration: scenarios.balanced.duration, target: scenarios.balanced.target },
        { duration: '30s', target: 0 },
      ],
      exec: 'mixedTraffic',
      env: { TEST_SCENARIO: 'balanced' }
    },
    write_heavy_test: {
      executor: 'ramping-vus',
      startVUs: 0,
      startTime: '4m',
      stages: [
        { duration: '30s', target: scenarios.write_heavy.target },
        { duration: scenarios.write_heavy.duration, target: scenarios.write_heavy.target },
        { duration: '30s', target: 0 },
      ],
      exec: 'mixedTraffic',
      env: { TEST_SCENARIO: 'write_heavy' }
    }
  },
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.01'],
  }
};

export let getTrend = new Trend('get_time');
export let postTrend = new Trend('post_time');

// Функция для создания данных посетителя
function generateVisitor() {
  return {
    fullName: `User ${__VU}-${__ITER}`,
    age: Math.floor(Math.random() * 70) + 10,
    ticketType: Math.random() > 0.5 ? 'FULL' : 'DISCOUNT',
  };
}

export function mixedTraffic() {
  const scenario = scenarios[__ENV.TEST_SCENARIO];
  const random = Math.random();

  if (random < scenario.read_ratio) {
    // GET запрос на /tours
    const res = http.get(`${BASE_URL}/tours`);
    check(res, {
      'GET /visitors статус 200': (r) => r.status === 200,
    });
    getTrend.add(res.timings.duration);
  } else {
    // POST запрос на /visitors
    const visitor = generateVisitor();
    const res = http.post(
      `${BASE_URL}/visitors`,
      JSON.stringify(visitor),
      { headers: { 'Content-Type': 'application/json' } }
    );
    check(res, {
      'POST /visitors статус 200': (r) => r.status === 200,
    });
    postTrend.add(res.timings.duration);
  }

  sleep(1);
}