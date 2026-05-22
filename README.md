# Sales Management API

REST API for a sales management system, built with Spring Boot 3, PostgreSQL, Redis and JWT authentication.

## Tech Stack

- **Java 17** + **Spring Boot 3.5.6**
- **PostgreSQL 15** (persistence, with Flyway migrations)
- **Redis 7** (caching)
- **Spring Security** + **JWT** (authentication)
- **springdoc-openapi** (Swagger UI)

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) and Docker Compose — the easiest way to run everything.
- For running locally without Docker: **JDK 17** and running PostgreSQL + Redis instances. The Maven Wrapper (`./mvnw`) is included, so a local Maven install is not required.

## Configuration

Copy the example environment file. The defaults already work for local Docker — you don't need to change anything to get started.

```bash
cp .env.example .env
```

| Variable        | Description                          | Default (dev)            |
| --------------- | ------------------------------------ | ------------------------ |
| `SPRING_PROFILE`| Active Spring profile (`dev`/`prod`) | `dev`                    |
| `DB_HOST`       | PostgreSQL host                      | `postgres`               |
| `REDIS_HOST`    | Redis host                           | `redis`                  |
| `JWT_SECRET`    | Base64 secret used to sign JWTs      | provided (change in prod)|

## Running with Docker (recommended)

This starts the API, PostgreSQL and Redis together. Migrations run automatically on startup.

```bash
docker compose up --build
```

When everything is ready, the app prints a banner with the URLs and demo accounts.

- API base URL: **http://localhost:8080/api**
- Swagger UI: **http://localhost:8080/api/swagger-ui.html**

Exposed ports on the host:

| Service    | Host port |
| ---------- | --------- |
| API        | `8080`    |
| PostgreSQL | `5434`    |
| Redis      | `6380`    |

To stop and remove the containers:

```bash
docker compose down
```

Add `-v` to also drop the database volume (wipes all data):

```bash
docker compose down -v
```

## Running locally (without Docker)

You'll need PostgreSQL and Redis running and reachable. The `dev` profile defaults to a database named `sales_db` (user `sales_user` / password `sales_pass`) on `localhost:5432` and Redis on `localhost:6379`.

```bash
# start the app
./mvnw spring-boot:run

# or build a jar and run it
./mvnw clean package
java -jar target/sales-api-0.0.1-SNAPSHOT.jar
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

To run the tests:

```bash
./mvnw test
```

## Demo accounts (dev profile only)

When running with the `dev` profile, two accounts are seeded automatically:

| Role     | Email                  | Password      |
| -------- | ---------------------- | ------------- |
| Admin    | `admin@sales.local`    | `admin123`    |
| Vendedor | `vendedor@sales.local` | `vendedor123` |

Authenticate via `POST /api/auth/login` to obtain a JWT, then send it as `Authorization: Bearer <token>` on subsequent requests.

## API Overview

All routes are served under the `/api` context path:

- `/api/auth` — login / authentication
- `/api/users` — user management
- `/api/products` — products
- `/api/customers` — customers
- `/api/sales` — sales
- `/api/reports` — reports
- `/api/audit-logs` — audit logs

Explore and try them through the Swagger UI at `/api/swagger-ui.html`.

## License

See [LICENSE](LICENSE).
