import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomSeed, randomIntBetween } from 'k6';

randomSeed(1234); // Устанавливаем seed для генерации случайных чисел

export var options = {
    scenarios: {
        ramping_vus: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 50 },  // Ramp-up to 50 VUs over 30 seconds
                { duration: '1m', target: 50 },   // Stay at 50 VUs for 1 minute
                { duration: '30s', target: 2 },   // Ramp-down to 2 VUs over 30 seconds
            ],
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<500'],  // 95% of requests should be below 500ms
    },
};

export default function () {
    // Определяем соотношение операций
    const ratio = Math.random();
    
    if (ratio < 0.05) {
        // 5% операций - вставка
        let payload = JSON.stringify({
            fullName: `John Doe ${__VU}-${__ITER}`,
            age: Math.floor(Math.random() * 70) + 10,
            ticketType: Math.random() > 0.5 ? 'FULL' : 'DISCOUNT'
        });
        let params = { headers: { 'Content-Type': 'application/json' } };
        let response = http.post('http://localhost:8080/visitors', payload, params);
        check(response, {
            'status is 201': (r) => r.status === 201,
        });
    } else {
        // 95% операций - чтение
        let response = http.get('http://localhost:8080/visitors');
        check(response, {
            'status is 200': (r) => r.status === 200,
        });
    }

    sleep(1);
}
