# FinanceApp Angular Frontend

Angular frontend for the Spring Boot finance microservices backend.

## Ports

- Angular frontend: `http://localhost:8081`
- API Gateway: `http://localhost:8080`

## Run

```powershell
npm install
npm start
```

All API calls go through the gateway at `http://localhost:8080`.

## Project Structure

```text
src/app/core        API config, interceptors, guards, shared services
src/app/models      Typed frontend models
src/app/features    Auth, dashboard, accounts, transactions, payments, support, admin
src/app/shared      Layout, reusable pipes/directives/components
```
