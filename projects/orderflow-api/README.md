# Orderflow API

An order management API demo built with Java 21 and Spring Boot. It demonstrates request validation, immutable domain records, concurrency-safe storage, explicit state transitions, REST conventions, actuator health, and integration tests.

## Why this project belongs in a software engineering portfolio

- Models a real business workflow instead of a CRUD-only endpoint.
- Enforces valid order transitions (`CREATED -> CONFIRMED -> FULFILLED`).
- Calculates totals from line items and rejects invalid input at the API boundary.
- Uses a clean controller/service/domain split that can move to PostgreSQL without changing the API.

## Run locally

Requires JDK 21 and Gradle 8.10.2. Run commands from this project directory.

```bash
gradle bootRun
```

Create an order:

```bash
curl -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"customerEmail":"engineer@example.com","items":[{"sku":"keyboard","quantity":2,"unitPrice":49.95}]}'
```

Explore `GET /api/orders`, `GET /api/orders/{id}`, and `POST /api/orders/{id}/transitions/CONFIRMED`. Health is available at `/actuator/health`.

Run `gradle test` to verify totals, validation, missing orders, filtering, cancellation, and terminal state rules.

With Docker installed:

```bash
docker build -t orderflow-api .
docker run --rm -p 127.0.0.1:8080:8080 orderflow-api
```

The image builds and tests the application before packaging it.

## Design and limits

`OrderController` validates HTTP requests and delegates to `OrderService`. Updates use `ConcurrentHashMap.compute` so each order's state transition is atomic within a single process. Amounts use `BigDecimal`, and lists in returned orders are immutable.

This is an unauthenticated, single-process demo: data resets on restart, prices are supplied by the caller, and there is no payment integration, JWT authentication, database adapter, or pagination. Production use would require server-side catalog pricing and durable transactions. This repository does not claim those features are implemented.

## Stack

Java 21 · Spring Boot 3 · Spring MVC · Bean Validation · JUnit 5 · MockMvc · Docker

The sample uses an in-memory repository so it runs with zero infrastructure. A natural next step is a PostgreSQL adapter and an outbox publisher for downstream fulfillment events.
