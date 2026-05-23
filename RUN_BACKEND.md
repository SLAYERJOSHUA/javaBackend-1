# Run the Microservices Backend

This guide starts the complete Spring Boot backend from a Git Bash terminal: Eureka server, API Gateway, and all backend microservices.

## Prerequisites

- Java 17 or newer
- Maven 3.9 or newer
- Ports available:
  - `8761` - Eureka server
  - `8080` - API Gateway
  - `8081` - Account service
  - `8082` - Payment service
  - `8083` - Transaction service
  - `8084` - User/Auth/Chat/Notification service

## Build Everything

Run from the project root:

```bash
mvn clean package -DskipTests
```

## Start Services

Open separate Git Bash terminals from the project root and run in this order.

Project root example:

```bash
cd "/c/Users/lenovo/Desktop/java project"
```

### 1. Eureka Server

```bash
mvn -pl springappserver spring-boot:run
```

Eureka dashboard:

```text
http://localhost:8761
```

### 2. Account Service

```bash
mvn -pl springappaccountservice spring-boot:run
```

Direct service URL:

```text
http://localhost:8081
```

### 3. Payment Service

```bash
mvn -pl springapppaymentservice spring-boot:run
```

Direct service URL:

```text
http://localhost:8082
```

### 4. Transaction Service

```bash
mvn -pl springapptransactionservice spring-boot:run
```

Direct service URL:

```text
http://localhost:8083
```

### 5. User Service

```bash
mvn -pl springappuserservice spring-boot:run
```

Direct service URL:

```text
http://localhost:8084
```

### 6. API Gateway

```bash
mvn -pl apigateway spring-boot:run
```

Gateway URL:

```text
http://localhost:8080
```

## Test Through API Gateway

Use `http://localhost:8080` for frontend/Postman calls.

Examples:

```bash
curl http://localhost:8080/api/accounts
curl http://localhost:8080/api/payments
curl http://localhost:8080/api/transactions
curl http://localhost:8080/api/users
```

## Optional: Start Services in Background

From one Git Bash terminal, you can start all services in the background and write logs to `logs/`.

```bash
mkdir -p logs

mvn -pl springappserver spring-boot:run > logs/eureka.log 2>&1 &
sleep 15

mvn -pl springappaccountservice spring-boot:run > logs/account.log 2>&1 &
mvn -pl springapppaymentservice spring-boot:run > logs/payment.log 2>&1 &
mvn -pl springapptransactionservice spring-boot:run > logs/transaction.log 2>&1 &
mvn -pl springappuserservice spring-boot:run > logs/user.log 2>&1 &
sleep 20

mvn -pl apigateway spring-boot:run > logs/gateway.log 2>&1 &
```

View logs:

```bash
tail -f logs/gateway.log
```

Stop background services:

```bash
pkill -f "spring-boot:run"
```

## Health Checks

```text
http://localhost:8080/actuator/health
http://localhost:8081/actuator/health
http://localhost:8082/actuator/health
http://localhost:8083/actuator/health
http://localhost:8084/actuator/health
```

## H2 Database Consoles

Each service uses an in-memory H2 database for local development.

```text
http://localhost:8081/h2-console
http://localhost:8082/h2-console
http://localhost:8083/h2-console
http://localhost:8084/h2-console
```

JDBC URLs:

```text
jdbc:h2:mem:accountsdb
jdbc:h2:mem:paymentsdb
jdbc:h2:mem:transactionsdb
jdbc:h2:mem:usersdb
```

Credentials:

```text
username: sa
password:
```

## Stop Services

Press `Ctrl + C` in each terminal.
