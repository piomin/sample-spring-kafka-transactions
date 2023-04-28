import {
  Writer,
  SchemaRegistry,
  SCHEMA_TYPE_JSON,
} from "k6/x/kafka";
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

const writer = new Writer({
  brokers: [`${__ENV.KAFKA_URL}`],
  topic: "transactions",
});

const schemaRegistry = new SchemaRegistry();

export function setup() {
  return { index: 1 };
}

export default function (data) {
  writer.produce({
    messages: [
      {
        value: schemaRegistry.serialize({
          data: {
            id: data.index++,
            sourceAccountId: randomIntBetween(1, 1000),
            targetAccountId: randomIntBetween(1, 1000),
            amount: randomIntBetween(10, 50),
            status: "NEW"
          },
          schemaType: SCHEMA_TYPE_JSON,
        }),
      },
    ],
  });
}

export function teardown(data) {
  writer.close();
}