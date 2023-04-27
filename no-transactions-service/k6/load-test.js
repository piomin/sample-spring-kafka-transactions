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

export default function () {
  writer.produce({
    messages: [
      {
        value: schemaRegistry.serialize({
          data: {
            id: 1,
            sourceAccountId: randomIntBetween(1, 100),
            targetAccountId: randomIntBetween(1, 100),
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