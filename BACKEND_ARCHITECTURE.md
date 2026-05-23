# Backend Architecture and Communication

This backend is a Spring Boot microservices system. Each business area runs as its own service, Eureka provides service discovery, and the API Gateway is the single entry point for clients.

## High-Level Flow

```text
Frontend / Postman
       |
       v
API Gateway :8080
       |
       v
Eureka Service Discovery :8761
       |
       +--> Account Service :8081
       +--> Payment Service :8082
       +--> Transaction Service :8083
       +--> User Service :8084
```

Clients should call the API Gateway at `http://localhost:8080`. The gateway forwards requests to the correct microservice using service names registered in Eureka.

## Service Discovery

`springappserver` runs Eureka on port `8761`.

Each microservice has this Eureka configuration:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

When a service starts, it registers itself with Eureka. The API Gateway can then route using logical service names such as:

```text
lb://SPRINGAPPACCOUNTSERVICE
lb://SPRINGAPPPAYMENTSERVICE
lb://SPRINGAPPTRANSACTIONSERVICE
lb://SPRINGAPPUSERSERVICE
```

## API Gateway

`apigateway` runs on port `8080`.

It routes requests by URL path:

```text
/api/accounts/**       -> springappaccountservice
/api/payments/**       -> springapppaymentservice
/api/transactions/**   -> springapptransactionservice
/api/users/**          -> springappuserservice
/api/auth/**           -> springappuserservice
/api/chat/**           -> springappuserservice
/api/notifications/**  -> springappuserservice
/api/sessions/**       -> springappuserservice
```

The gateway does not contain business logic. It receives the request, finds the destination service through Eureka, and forwards the request.

## Account Service

Folder:

```text
springappaccountservice
```

Port:

```text
8081
```

Main responsibilities:

- Create accounts
- View accounts
- Update accounts and balances
- Generate, set, verify, and reset PINs
- Manage account creation requests
- Approve or reject account requests

Important models:

- `Account`
- `AccountRequest`
- `AccountType`
- `AccountStatus`
- `RequestStatus`

Main endpoints:

```text
POST /api/accounts
POST /api/accounts/self
GET  /api/accounts
GET  /api/accounts/{id}
PUT  /api/accounts/{id}
GET  /api/accounts/user/{userId}
GET  /api/accounts/{id}/balance
PUT  /api/accounts/{id}/balance
POST /api/accounts/{id}/generate-pin
POST /api/accounts/{id}/set-pin
POST /api/accounts/{id}/verify-pin
POST /api/accounts/{id}/reset-pin
POST /api/accounts/requests
GET  /api/accounts/requests/pending
POST /api/accounts/requests/{requestId}/approve
POST /api/accounts/requests/{requestId}/reject
```

## Payment Service

Folder:

```text
springapppaymentservice
```

Port:

```text
8082
```

Main responsibilities:

- Process payments
- Track payment status
- Fetch payments by id, user, or account
- Filter payments

Important models:

- `Payment`
- `PaymentMethod`
- `PaymentStatus`

Main endpoints:

```text
POST /api/payments
GET  /api/payments
GET  /api/payments/{id}
PUT  /api/payments/{id}
PUT  /api/payments/{id}/status
GET  /api/payments/user/{userId}
GET  /api/payments/account/{accountId}
GET  /api/payments/filter
```

## Transaction Service

Folder:

```text
springapptransactionservice
```

Port:

```text
8083
```

Main responsibilities:

- Record transactions
- Track transaction status
- Fetch transaction history by id, account, or user
- Filter transactions

Important models:

- `Transaction`
- `TransactionType`
- `TransactionStatus`

Main endpoints:

```text
POST /api/transactions
GET  /api/transactions
GET  /api/transactions/{id}
GET  /api/transactions/account/{accountId}
GET  /api/transactions/user/{userId}
PUT  /api/transactions/{id}/status
GET  /api/transactions/filter
```

## User Service

Folder:

```text
springappuserservice
```

Port:

```text
8084
```

Main responsibilities:

- User registration and login
- Token/session validation
- Password changes
- User management
- Chat support
- Notifications
- Session management

Important models:

- `User`
- `Role`
- `Session`
- `ChatMessage`
- `ChatSession`
- `Notification`
- `NotificationType`

Auth endpoints:

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/validate
POST /api/auth/change-password
```

User endpoints:

```text
GET    /api/users
GET    /api/users/{id}
GET    /api/users/search?name=Alice
GET    /api/users/role/{role}
PUT    /api/users/{id}
PUT    /api/users/{id}/profile
PUT    /api/users/{id}/role
POST   /api/users/{id}/activate
DELETE /api/users/{id}
```

Chat endpoints:

```text
POST /api/chat/send
GET  /api/chat/messages
GET  /api/chat/{chatSessionId}
GET  /api/chat/unreplied
```

Notification endpoints:

```text
POST /api/notifications/create
GET  /api/notifications/user/{userId}
GET  /api/notifications/user/{userId}/unread
GET  /api/notifications/user/{userId}/unread/count
PUT  /api/notifications/{notificationId}/read
PUT  /api/notifications/user/{userId}/read-all
```

Session endpoints:

```text
GET  /api/sessions/active
POST /api/sessions/{sessionId}/terminate
GET  /api/sessions/validate/{sessionId}
```

## Data Storage

Each service owns its own H2 in-memory database:

```text
Account Service      -> accountsdb
Payment Service      -> paymentsdb
Transaction Service  -> transactionsdb
User Service         -> usersdb
```

This keeps service data separated, which is the usual microservice ownership pattern.

## Internal Communication

Currently, services are independently runnable and communicate mainly through the API Gateway from the client side.

Example frontend flow:

```text
1. User logs in:
   POST /api/auth/login
   Gateway routes to User Service.

2. User creates account:
   POST /api/accounts/self
   Gateway routes to Account Service.

3. User makes payment:
   POST /api/payments
   Gateway routes to Payment Service.

4. Transaction is recorded:
   POST /api/transactions
   Gateway routes to Transaction Service.

5. Notification is created:
   POST /api/notifications/create
   Gateway routes to User Service.
```

For production-grade service-to-service communication, the next step would be adding OpenFeign/WebClient calls or asynchronous messaging between services.

## Error Handling

Services use centralized exception handlers where implemented. Validation failures return structured JSON like:

```json
{
  "timestamp": "2026-05-23T02:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": {
    "amount": "amount must be greater than 0"
  }
}
```

