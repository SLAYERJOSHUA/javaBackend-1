# Microservices Project

Initial project structure based on the requested folders.

## Services

- `angularapp` - Angular frontend
- `apigateway` - Spring Cloud Gateway, port `8080`
- `springappserver` - Eureka discovery server, port `8761`
- `springappaccountservice` - Account service, port `8081`
- `springapppaymentservice` - Payment service, port `8082`
- `springapptransactionservice` - Transaction service, port `8083`
- `springappuserservice` - User service, port `8084`
- `junit` - Shared Java test notes/resources

## Run Java Services

From the root:

```powershell
mvn clean package
```

Run each service:

```powershell
mvn -pl springappserver spring-boot:run
mvn -pl springappaccountservice spring-boot:run
mvn -pl springapppaymentservice spring-boot:run
mvn -pl springapptransactionservice spring-boot:run
mvn -pl springappuserservice spring-boot:run
mvn -pl apigateway spring-boot:run
```

## Run Angular

```powershell
cd angularapp
npm install
npm start
```

No browser test runner scaffold is included.
