# Quick Reference - Backend API Summary

## System Architecture
- **API Gateway**: `http://localhost:8080`
- **Service Discovery**: Eureka `http://localhost:8761`
- **All requests** must include: `Authorization: Bearer <jwt_token>`

## Quick Endpoints Reference

### Authentication (`/api/auth`)
| Method | Endpoint | Purpose | Auth |
|--------|----------|---------|------|
| POST | `/register` | Create new user | ❌ |
| POST | `/login` | Login user | ❌ |
| POST | `/validate` | Check token validity | ✅ |
| POST | `/refresh` | Get new token | ✅ |
| POST | `/change-password` | Change password | ✅ |

### Users (`/api/users`)
| Method | Endpoint | Purpose | Auth |
|--------|----------|---------|------|
| GET | `/` | List users (paginated) | ✅ |
| GET | `/search?name=` | Search users | ✅ |
| GET | `/role/{role}` | Users by role | ✅ |
| GET | `/{id}` | Get user by ID | ✅ |
| PUT | `/{id}` | Update user | ✅ |
| PUT | `/{id}/profile` | Update profile | ✅ |
| PUT | `/{id}/role` | Change role | ✅ |
| POST | `/{id}/activate` | Activate user | ✅ |
| DELETE | `/{id}` | Deactivate user | ✅ |

### Accounts (`/api/accounts`)
| Method | Endpoint | Purpose | Auth |
|--------|----------|---------|------|
| POST | `/` | Create account (admin) | ✅ |
| POST | `/self` | User creates own account | ✅ |
| GET | `/` | List accounts (paginated) | ✅ |
| GET | `/{id}` | Get account by ID | ✅ |
| GET | `/user/{userId}` | User's accounts | ✅ |
| GET | `/active` | All active accounts | ✅ |
| GET | `/{id}/balance` | Get balance | ✅ |
| PUT | `/{id}` | Update account | ✅ |
| PUT | `/{id}/balance` | Update balance | ✅ |
| POST | `/{id}/generate-pin` | Generate PIN | ✅ |

### Payments (`/api/payments`)
| Method | Endpoint | Purpose | Auth |
|--------|----------|---------|------|
| POST | `/` | Process payment | ✅ |
| GET | `/` | List payments (paginated) | ✅ |
| GET | `/{id}` | Get payment by ID | ✅ |
| GET | `/user/{userId}` | User's payments | ✅ |
| GET | `/account/{accountId}` | Account payments | ✅ |
| GET | `/filter` | Filter by status/method | ✅ |
| PUT | `/{id}` | Update payment | ✅ |
| PUT | `/{id}/status` | Update status | ✅ |

### Transactions (`/api/transactions`)
| Method | Endpoint | Purpose | Auth |
|--------|----------|---------|------|
| POST | `/` | Create transaction | ✅ |
| GET | `/` | List transactions (paginated) | ✅ |
| GET | `/{id}` | Get transaction by ID | ✅ |
| GET | `/user/{userId}` | User's transactions | ✅ |
| GET | `/account/{accountId}` | Account transactions | ✅ |
| GET | `/filter` | Filter by status/type | ✅ |
| PUT | `/{id}/status` | Update status | ✅ |

### Chat (`/api/chat`)
| Method | Endpoint | Purpose | Auth |
|--------|----------|---------|------|
| POST | `/send` | Send message | ✅ |
| GET | `/messages` | All messages | ✅ |
| GET | `/{chatSessionId}` | Session messages | ✅ |
| GET | `/unreplied` | Unreplied messages | ✅ |

## Key Models

### User
```
id, username, email, password, firstName, lastName, 
phone, dateOfBirth, address, role, isActive, 
createdAt, updatedAt
```

### Account
```
accountId, accountNumber, accountType, balance, userId, 
status, createdAt, updatedAt
```

### Payment
```
paymentId, paymentNumber, accountId, userId, amount, 
paymentMethod, status, description, createdAt, updatedAt
```

### Transaction
```
transactionId, transactionNumber, accountId, userId, 
transactionType, amount, status, description, createdAt, updatedAt
```

## Enums

**User Roles**: ACCOUNT_HOLDER, CUSTOMER_SUPPORT, ADMIN  
**Account Types**: SAVINGS, CHECKING, INVESTMENT  
**Account Status**: ACTIVE, INACTIVE, SUSPENDED  
**Payment Methods**: CARD, BANK_TRANSFER, WALLET  
**Payment Status**: PENDING, COMPLETED, FAILED, CANCELLED  
**Transaction Types**: TRANSFER, DEPOSIT, WITHDRAWAL  
**Transaction Status**: PENDING, COMPLETED, FAILED, CANCELLED  

## Error Codes
- **400** - Bad Request (validation error)
- **401** - Unauthorized (invalid token)
- **403** - Forbidden (no permission)
- **404** - Not Found (resource missing)
- **500** - Server Error

## Common Request Headers
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

## Pagination
Query parameters: `page=0&size=10`  
Returns Page object with: `content[]`, `totalElements`, `totalPages`, `pageNumber`, `pageSize`

## Token Management
1. **Store** token from login response in localStorage
2. **Include** token in every request header
3. **Check** token expiration time
4. **Refresh** using refresh token when expired
5. **Validate** token anytime with `/api/auth/validate`

---

**See FRONTEND_INTEGRATION_GUIDE.md for complete documentation**
