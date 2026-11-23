# Customer Order Processing Service



Spring Boot REST service for creating, retrieving, listing and updating orders. Data is stored in H2 via Spring Data JPA. Optionally publishes an `ORDER_CREATED` event to Kafka.



## Prerequisites

- Java 21

- Maven 3.8+



## Build

```bash

mvn clean package

```

Run tests only: `mvn test` (7 integration tests, 11 unit tests).



## Run

```bash

mvn spring-boot:run

# or

java -jar target/order-service-1.0.0.jar

```

The service listens on `http://localhost:8080`. The H2 console is at `/h2-console` (JDBC URL `jdbc:h2:mem:ordersdb`, user `sa`, empty password).



### Enabling Kafka (optional)

Kafka is off by default so the service runs with no broker. To enable:

```bash

mvn spring-boot:run -Dspring-boot.run.arguments="--app.kafka.enabled=true --spring.kafka.bootstrap-servers=localhost:9092"

```

On order creation this message is sent to topic `order-events` (configurable via `app.kafka.topic`), keyed by order id:

```json

{"orderId":"1","event":"ORDER_CREATED"}

```



## API



| Method | Path | Description |

|--------|------|-------------|

| POST | `/orders` | Create order (201) |

| GET | `/orders/{id}` | Retrieve order (200 / 404) |

| PUT | `/orders/{id}/status` | Update status (200 / 400 / 404) |

| GET | `/orders` | List all orders |



Statuses: `CREATED`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`.



### Create order

```bash

curl -X POST localhost:8080/orders -H "Content-Type: application/json" \

  -d '{"customerId":"12345","product":"Laptop","quantity":1,"price":1200}'

```

Response (201):

```json

{"id":1,"customerId":"12345","product":"Laptop","quantity":1,"price":1200,

 "status":"CREATED","createdAt":"2026-11-21T17:00:00Z","updatedAt":null}

```



### Retrieve order

```bash

curl localhost:8080/orders/1

```



### Update status

```bash

curl -X PUT localhost:8080/orders/1/status -H "Content-Type: application/json" \

  -d '{"status":"SHIPPED"}'

```


### List orders

```bash

curl localhost:8080/orders

```



### Error responses

Validation failure (400):

```json

{"timestamp":"...","status":400,"message":"Validation failed",

 "errors":["customerId is required","price must be greater than 0","quantity must be greater than 0"]}

```

Not found (404):

```json

{"timestamp":"...","status":404,"message":"Order not found with id: 99"}

```



## Assumptions

- Order id is a database-generated number; the Kafka event sends it as a string.

- New orders always start as `CREATED`; any status can be set to any other (no transition rules).

- `product` is also required, in addition to the validations specified.

- Price is the unit price, stored as `BigDecimal` with 2 decimals.

- H2 is in-memory, so data is lost on restart.

- Kafka is optional and disabled by default. A publish failure is logged and does not fail order creation.

- No authentication or pagination (not in scope).

 
