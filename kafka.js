import http from 'k6/http';
import { sleep, check } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { Trend } from 'k6/metrics';
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

export const options = {
    scenarios: {
        read_heavy: {
            executor: 'constant-vus',
            vus: 10,
            duration: '1m',
            exec: 'readHeavyScenario'
        },
        balanced: {
            executor: 'constant-vus',
            vus: 10,
            duration: '1m',
            exec: 'balancedScenario'
        },
        write_heavy: {
            executor: 'constant-vus',
            vus: 10,
            duration: '1m',
            exec: 'writeHeavyScenario'
        }
    }
};

function generateVisitorMessage() {
    return JSON.stringify({
        entity: "VISITOR",
        operation: "POST",
        payload: {
            fullName: `User ${randomIntBetween(1, 100)}`,
            age: randomIntBetween(18, 80),
            ticketType: Math.random() > 0.5 ? "FULL" : "DISCOUNT"
        }
    });
}

export function readHeavyScenario() {
    if (Math.random() < 0.95) {
        // 95% read operations
        http.get(`${BASE_URL}/visitors`);
    } else {
        // 5% write operations
        writer.produce({
            key: "test-key",
            value: generateVisitorMessage()
        });
    }
    sleep(1);
}

export function balancedScenario() {
    if (Math.random() < 0.5) {
        // 50% read operations
        http.get(`${BASE_URL}/visitors`);
    } else {
        // 50% write operations
        writer.produce({
            key: "test-key",
            value: generateVisitorMessage()
        });
    }
    sleep(1);
}

export function writeHeavyScenario() {
    if (Math.random() < 0.05) {
        // 5% read operations
        http.get(`${BASE_URL}/visitors`);
    } else {
        // 95% write operations
        writer.produce({
            key: "test-key",
            value: generateVisitorMessage()
        });
    }
    sleep(1);
}

export function teardown() {
    writer.close();
}