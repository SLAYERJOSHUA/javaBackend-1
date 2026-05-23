# Backend API Summary - Frontend Integration Guide

## System Overview

**Architecture**: Spring Boot Microservices with API Gateway  
**Java Version**: 17  
**Spring Boot**: 3.3.5  
**Spring Cloud**: 2023.0.3  
**Service Discovery**: Eureka (Service Registry)

### Service Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    API GATEWAY (Port 8080)                  │
│                 JWT Authentication & Routing                │
└──────────┬──────────────────────────────────────────────────┘
           │
    ┌──────┴──────┬──────────────┬──────────────┬──────────────┐
    │             │              │              │              │
    v             v              v              v              v
 [User       [Account      [Payment      [Transaction    [Eureka
 Service]    Service]      Service]      Service]      Discovery]
 Port 8084   Port 8081     Port 8082     Port 8083     Port 8761
```

---

## 1. API GATEWAY (Port 8080)

**Entry Point**: `http://localhost:8080`

### Authentication
- **Filter**: `JwtAuthenticationFilter` validates JWT tokens on all requests
- **Header Required**: `Authorization: Bearer <jwt_token>`
- All requests except `/api/auth/register` and `/api/auth/login` require valid JWT token

### Routing Rules

| URL Pattern | Target Service |
|------------|------------------|
| `/api/auth/**` | User Service (8084) |
| `/api/users/**` | User Service (8084) |
| `/api/chat/**` | User Service (8084) |
| `/api/sessions/**` | User Service (8084) |
| `/api/notifications/**` | User Service (8084) |
| `/api/accounts/**` | Account Service (8081) |
| `/api/payments/**` | Payment Service (8082) |
| `/api/transactions/**` | Transaction Service (8083) |

---

## 2. USER SERVICE (Port 8084)

### 2.1 Authentication Endpoints (`/api/auth`)

#### Register User
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}

Response: User object with id, createdAt, updatedAt
Status: 201 Created
```

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123",
  "deviceInfo": "Chrome on Windows",
  "ipAddress": "192.168.1.1"
}

Response:
{
  "token": "jwt_token_string",
  "refreshToken": "refresh_token_string",
  "user": { User object },
  "expiresIn": 3600
}
Status: 200 OK
```

#### Validate Token
```
POST /api/auth/validate
Content-Type: application/json

{
  "token": "jwt_token_string"
}

Response:
{
  "valid": true,
  "userId": 1,
  "username": "john_doe"
}
Status: 200 OK
```

#### Refresh Token
```
POST /api/auth/refresh
Content-Type: application/json

{
  "token": "refresh_token_string"
}

Response:
{
  "token": "new_jwt_token",
  "expiresIn": 3600
}
Status: 200 OK
```

#### Change Password
```
POST /api/auth/change-password
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "oldPassword": "old_password",
  "newPassword": "new_password"
}

Response: { "message": "Password changed successfully" }
Status: 200 OK
```

### 2.2 User Management Endpoints (`/api/users`)

#### Get All Users (with pagination)
```
GET /api/users?page=0&size=10
Authorization: Bearer <jwt_token>

Response: Array of User objects
Status: 200 OK
```

#### Search Users by Name
```
GET /api/users/search?name=john
Authorization: Bearer <jwt_token>

Response: Array of User objects
Status: 200 OK
```

#### Get Users by Role
```
GET /api/users/role/{role}
Authorization: Bearer <jwt_token>

Roles: ACCOUNT_HOLDER, CUSTOMER_SUPPORT, ADMIN

Response: Array of User objects
Status: 200 OK
```

#### Get User by ID
```
GET /api/users/{id}
Authorization: Bearer <jwt_token>

Response: User object
Status: 200 OK or 404 Not Found
```

#### Update User (Full)
```
PUT /api/users/{id}
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "username": "new_username",
  "email": "new_email@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "dateOfBirth": "1990-01-01",
  "address": "123 Main St"
}

Response: Updated User object
Status: 200 OK
```

#### Update User Profile (Partial)
```
PUT /api/users/{id}/profile
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "address": "123 Main St"
}

Response: Updated User object
Status: 200 OK
```

#### Change User Role
```
PUT /api/users/{id}/role
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "role": "ADMIN"
}

Response: Updated User object
Status: 200 OK
```

#### Activate User
```
POST /api/users/{id}/activate
Authorization: Bearer <jwt_token>

Response: Activated User object
Status: 200 OK
```

#### Deactivate User
```
DELETE /api/users/{id}
Authorization: Bearer <jwt_token>

Response: Deactivated User object
Status: 200 OK
```

### 2.3 User Model
```javascript
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "dateOfBirth": "1990-01-01",
  "address": "123 Main St",
  "role": "ACCOUNT_HOLDER", // ACCOUNT_HOLDER, CUSTOMER_SUPPORT, ADMIN
  "isActive": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### 2.4 Chat Endpoints (`/api/chat`)

#### Send Message
```
POST /api/chat/send
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "senderId": 1,
  "senderRole": "ACCOUNT_HOLDER",
  "receiverId": 2,
  "receiverRole": "CUSTOMER_SUPPORT",
  "message": "Hello, I need help",
  "chatSessionId": "session_123"
}

Response: ChatMessage object
Status: 201 Created
```

#### Get All Messages
```
GET /api/chat/messages
Authorization: Bearer <jwt_token>

Response: Array of ChatMessage objects
Status: 200 OK
```

#### Get Session Messages
```
GET /api/chat/{chatSessionId}
Authorization: Bearer <jwt_token>

Response: Array of ChatMessage objects in session
Status: 200 OK
```

#### Get Unreplied Messages
```
GET /api/chat/unreplied
Authorization: Bearer <jwt_token>

Response: Array of ChatSession objects with unreplied messages
Status: 200 OK
```

### 2.5 Chat Message Model
```javascript
{
  "id": 1,
  "senderId": 1,
  "senderRole": "ACCOUNT_HOLDER",
  "receiverId": 2,
  "receiverRole": "CUSTOMER_SUPPORT",
  "message": "Hello, I need help",
  "chatSessionId": "session_123",
  "isRead": false,
  "createdAt": "2024-01-15T10:30:00"
}
```

### 2.6 Session Endpoints (`/api/sessions`)

Manage user sessions (device logins, active sessions)

### 2.7 Notification Endpoints (`/api/notifications`)

Push notifications for account updates, transactions, messages

---

## 3. ACCOUNT SERVICE (Port 8081)

### 3.1 Account Endpoints (`/api/accounts`)

#### Create Account (Admin)
```
POST /api/accounts
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "userId": 1,
  "accountType": "SAVINGS", // SAVINGS, CHECKING, INVESTMENT
  "balance": 5000.00
}

Response: Account object
Status: 201 Created
```

#### Create Own Account (User)
```
POST /api/accounts/self
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "userId": 1,
  "accountType": "SAVINGS",
  "initialDeposit": 5000.00
}

Response: Account object
Status: 201 Created
```

#### Get All Accounts (Paginated)
```
GET /api/accounts?page=0&size=10
Authorization: Bearer <jwt_token>

Response: Page object with Account array
Status: 200 OK
```

#### Get Account by ID
```
GET /api/accounts/{id}
Authorization: Bearer <jwt_token>

Response: Account object
Status: 200 OK or 404
```

#### Update Account
```
PUT /api/accounts/{id}
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "accountType": "SAVINGS",
  "status": "ACTIVE"
}

Response: Updated Account object
Status: 200 OK
```

#### Get Accounts by User
```
GET /api/accounts/user/{userId}
Authorization: Bearer <jwt_token>

Response: Array of Account objects
Status: 200 OK
```

#### Get Active Accounts
```
GET /api/accounts/active
Authorization: Bearer <jwt_token>

Response: Array of active Account objects
Status: 200 OK
```

#### Get Active Accounts by User
```
GET /api/accounts/user/{userId}/active
Authorization: Bearer <jwt_token>

Response: Array of active Account objects for user
Status: 200 OK
```

#### Get Account Balance
```
GET /api/accounts/{id}/balance
Authorization: Bearer <jwt_token>

Response: { "balance": 5000.00 }
Status: 200 OK
```

#### Update Account Balance
```
PUT /api/accounts/{id}/balance
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "balance": 7500.00
}

Response: Updated Account object
Status: 200 OK
```

#### Generate PIN
```
POST /api/accounts/{id}/generate-pin
Authorization: Bearer <jwt_token>

Response: { "pin": "1234" }
Status: 200 OK
```

### 3.2 Account Model
```javascript
{
  "accountId": 1,
  "accountNumber": "ACC001234567",
  "accountType": "SAVINGS", // SAVINGS, CHECKING, INVESTMENT
  "balance": 5000.00,
  "userId": 1,
  "status": "ACTIVE", // ACTIVE, INACTIVE, SUSPENDED
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### 3.3 Account Request Endpoints

#### Create Account Request (User applies for account)
```
POST /api/accounts/request
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "userId": 1,
  "accountType": "SAVINGS",
  "initialDeposit": 5000.00
}

Response: AccountRequest object
Status: 201 Created
```

#### View Account Requests
```
GET /api/accounts/requests
Authorization: Bearer <jwt_token>

Response: Array of AccountRequest objects
Status: 200 OK
```

#### Approve Request
```
POST /api/accounts/requests/{requestId}/approve
Authorization: Bearer <jwt_token>

Response: Approved AccountRequest object
Status: 200 OK
```

#### Reject Request
```
POST /api/accounts/requests/{requestId}/reject
Authorization: Bearer <jwt_token>

Response: Rejected AccountRequest object
Status: 200 OK
```

---

## 4. PAYMENT SERVICE (Port 8082)

### 4.1 Payment Endpoints (`/api/payments`)

#### Process Payment
```
POST /api/payments
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "accountId": 1,
  "userId": 1,
  "amount": 500.00,
  "paymentMethod": "CARD", // CARD, BANK_TRANSFER, WALLET
  "description": "Payment for order #123"
}

Response: Payment object
Status: 201 Created
```

#### Get Payment by ID
```
GET /api/payments/{id}
Authorization: Bearer <jwt_token>

Response: Payment object
Status: 200 OK or 404
```

#### Get All Payments (Paginated)
```
GET /api/payments?page=0&size=10
Authorization: Bearer <jwt_token>

Response: Page object with Payment array
Status: 200 OK
```

#### Update Payment
```
PUT /api/payments/{id}
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "amount": 550.00,
  "status": "COMPLETED"
}

Response: Updated Payment object
Status: 200 OK
```

#### Update Payment Status
```
PUT /api/payments/{id}/status
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "status": "COMPLETED" // PENDING, COMPLETED, FAILED, CANCELLED
}

Response: Updated Payment object
Status: 200 OK
```

#### Get Payments by User
```
GET /api/payments/user/{userId}
Authorization: Bearer <jwt_token>

Response: Array of Payment objects
Status: 200 OK
```

#### Get Payments by Account
```
GET /api/payments/account/{accountId}
Authorization: Bearer <jwt_token>

Response: Array of Payment objects
Status: 200 OK
```

#### Filter Payments
```
GET /api/payments/filter?status=COMPLETED&method=CARD
Authorization: Bearer <jwt_token>

Response: Filtered array of Payment objects
Status: 200 OK
```

### 4.2 Payment Model
```javascript
{
  "paymentId": 1,
  "paymentNumber": "PAY001234567",
  "accountId": 1,
  "userId": 1,
  "amount": 500.00,
  "paymentMethod": "CARD", // CARD, BANK_TRANSFER, WALLET
  "status": "COMPLETED", // PENDING, COMPLETED, FAILED, CANCELLED
  "description": "Payment for order #123",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

## 5. TRANSACTION SERVICE (Port 8083)

### 5.1 Transaction Endpoints (`/api/transactions`)

#### Create Transaction
```
POST /api/transactions
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "accountId": 1,
  "userId": 1,
  "transactionType": "TRANSFER", // TRANSFER, DEPOSIT, WITHDRAWAL
  "amount": 1000.00,
  "description": "Transfer to John Doe"
}

Response: Transaction object
Status: 201 Created
```

#### Get Transaction by ID
```
GET /api/transactions/{id}
Authorization: Bearer <jwt_token>

Response: Transaction object
Status: 200 OK or 404
```

#### Get All Transactions (Paginated)
```
GET /api/transactions?page=0&size=10
Authorization: Bearer <jwt_token>

Response: Page object with Transaction array
Status: 200 OK
```

#### Get Transactions by Account
```
GET /api/transactions/account/{accountId}
Authorization: Bearer <jwt_token>

Response: Array of Transaction objects
Status: 200 OK
```

#### Get Transactions by User
```
GET /api/transactions/user/{userId}
Authorization: Bearer <jwt_token>

Response: Array of Transaction objects
Status: 200 OK
```

#### Update Transaction Status
```
PUT /api/transactions/{id}/status
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "status": "COMPLETED" // PENDING, COMPLETED, FAILED, CANCELLED
}

Response: Updated Transaction object
Status: 200 OK
```

#### Filter Transactions
```
GET /api/transactions/filter?status=COMPLETED&type=TRANSFER
Authorization: Bearer <jwt_token>

Response: Filtered array of Transaction objects
Status: 200 OK
```

### 5.2 Transaction Model
```javascript
{
  "transactionId": 1,
  "transactionNumber": "TXN001234567",
  "accountId": 1,
  "userId": 1,
  "transactionType": "TRANSFER", // TRANSFER, DEPOSIT, WITHDRAWAL
  "amount": 1000.00,
  "status": "COMPLETED", // PENDING, COMPLETED, FAILED, CANCELLED
  "description": "Transfer to John Doe",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

## 6. Authentication & Security

### JWT Token Structure
```
Header: Authorization: Bearer <jwt_token>
```

### Token Flow
1. **Register/Login** → Receive JWT token + Refresh Token
2. **Use JWT token** in all subsequent requests
3. **Token expires** → Use refresh token to get new JWT
4. **Validate token** → POST `/api/auth/validate` to check validity

### Error Responses

#### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token"
}
```

#### 403 Forbidden
```json
{
  "error": "Forbidden",
  "message": "You don't have permission to access this resource"
}
```

#### 404 Not Found
```json
{
  "error": "Not Found",
  "message": "Resource not found"
}
```

#### 400 Bad Request
```json
{
  "error": "Bad Request",
  "message": "Validation failed",
  "details": {
    "field": "error message"
  }
}
```

---

## 7. Data Validation Rules

### User
- **username**: 3-50 characters, unique, required
- **email**: valid email format, unique, required
- **password**: min 6 characters, required
- **firstName**: required
- **lastName**: required
- **phone**: optional
- **dateOfBirth**: optional
- **address**: optional

### Account
- **accountNumber**: auto-generated, unique
- **userId**: required
- **accountType**: required (SAVINGS, CHECKING, INVESTMENT)
- **balance**: min 0.00, precision 15,2
- **status**: ACTIVE, INACTIVE, SUSPENDED

### Payment
- **accountId**: required
- **userId**: required
- **amount**: min 0.01, precision 15,2
- **paymentMethod**: required (CARD, BANK_TRANSFER, WALLET)
- **status**: PENDING, COMPLETED, FAILED, CANCELLED

### Transaction
- **accountId**: required
- **transactionType**: required (TRANSFER, DEPOSIT, WITHDRAWAL)
- **amount**: min 0.01, precision 15,2
- **status**: PENDING, COMPLETED, FAILED, CANCELLED

---

## 8. Frontend Implementation Checklist

### Authentication Page
- [ ] Register form (username, email, password, firstName, lastName)
- [ ] Login form (username, password)
- [ ] Token storage (localStorage/sessionStorage)
- [ ] Token refresh logic
- [ ] Logout functionality

### Dashboard
- [ ] User profile display
- [ ] Account list with pagination
- [ ] Account balance display
- [ ] Recent transactions
- [ ] Recent payments

### Account Management
- [ ] Create account form
- [ ] View account details
- [ ] Update account
- [ ] View account requests (if applicable)
- [ ] Account balance management

### Transactions
- [ ] Create transaction form
- [ ] Filter/search transactions
- [ ] Transaction history
- [ ] Transaction status updates
- [ ] Download/export transaction report

### Payments
- [ ] Process payment form
- [ ] Payment history with pagination
- [ ] Filter by status and method
- [ ] Payment status tracking

### Chat/Support
- [ ] Chat interface
- [ ] Send messages
- [ ] View chat history
- [ ] Real-time message updates (WebSocket)
- [ ] Mark as read

### Error Handling
- [ ] Token expiration handling
- [ ] Network error handling
- [ ] Validation error display
- [ ] Global error modal/toast

---

## 9. Example Frontend Requests (JavaScript/Fetch)

### Register
```javascript
const register = async (user) => {
  const response = await fetch('http://localhost:8080/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(user)
  });
  return response.json();
};
```

### Login
```javascript
const login = async (username, password) => {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  const data = await response.json();
  localStorage.setItem('token', data.token);
  return data;
};
```

### Authenticated Request
```javascript
const getAccounts = async (page = 0, size = 10) => {
  const token = localStorage.getItem('token');
  const response = await fetch(
    `http://localhost:8080/api/accounts?page=${page}&size=${size}`,
    {
      method: 'GET',
      headers: { 'Authorization': `Bearer ${token}` }
    }
  );
  return response.json();
};
```

### Token Refresh
```javascript
const refreshToken = async () => {
  const refreshToken = localStorage.getItem('refreshToken');
  const response = await fetch('http://localhost:8080/api/auth/refresh', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ token: refreshToken })
  });
  const data = await response.json();
  localStorage.setItem('token', data.token);
  return data.token;
};
```

---

## 10. Base URL for All Requests

```
http://localhost:8080
```

All API calls go through the API Gateway on port 8080, never directly to the microservices.

---

## 11. Common Enums

### User Roles
- `ACCOUNT_HOLDER`
- `CUSTOMER_SUPPORT`
- `ADMIN`

### Account Types
- `SAVINGS`
- `CHECKING`
- `INVESTMENT`

### Account Status
- `ACTIVE`
- `INACTIVE`
- `SUSPENDED`

### Payment Method
- `CARD`
- `BANK_TRANSFER`
- `WALLET`

### Payment/Transaction Status
- `PENDING`
- `COMPLETED`
- `FAILED`
- `CANCELLED`

### Transaction Types
- `TRANSFER`
- `DEPOSIT`
- `WITHDRAWAL`

---

## 12. Useful Tools for Testing

- **Postman**: Import the API endpoints and test them
- **cURL**: Command-line testing
- **Thunder Client**: VS Code extension
- **REST Client**: VS Code extension

---

**Last Updated**: January 2024  
**Backend Version**: 0.0.1-SNAPSHOT
