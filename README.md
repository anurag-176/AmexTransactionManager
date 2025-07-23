# Amex Transaction Manager

This is a microservices-based system that manages users, accounts, and transactions with inter-service communication via Kafka.
For convenience, all service are added to one repository.
---

# Agenda
**1. Services** – Overview of the three microservices in the system

**2. ER Diagram** – Visual schema of entities and their relationships

**3. Kafka Communication** – How services communicate asynchronously

**4. Sample cURL Commands** – Example requests and expected responses

**5. Running the Application** – Step-by-step setup and execution instructions



# Services

1. **User Service**
    - Create and manage users.
    - Responds to validation and data fetch requests via Kafka.

2. **Account Service**
    - Handles account creation and links users to accounts.
    - Validates user existence via Kafka before account creation.

3. **Transaction Service**
    - Creates and retrieves financial transactions linked to accounts.
    - Uses Kafka to fetch user information via AccountService.

---

# ER Diagram
```
┌────────────┐         ┌──────────────┐         ┌────────────────────┐
│   User     │         │   Account    │         │    Transaction     │
├────────────┤         ├──────────────┤         ├────────────────────┤
│ id         │◄────────┤ userId       │         │ id                 │
│ name       │         │ id           │◄────────┤ accountId          │
│ email      │         │ balance      │         │ amount             │
│ deleted    │         │ currency     │         │ type (CREDIT/DEBIT)│
└────────────┘         │ deleted      │         │ description        │
                       │ createdAt    │         │ createdAt          │
                       └──────────────┘         └────────────────────┘
```
- A `User` can have multiple `Accounts`
- An `Account` can have multiple `Transactions`

---

# Kafka Communication

### AccountService ⇆ UserService
- **Topic**: `user-validation-request` / `user-validation-response`
- **Use Case**: Account creation requires validation that the user exists.

### TransactionService ⇆ AccountService ⇆ UserService
- **Topic Chain**:  
  `account-to-user-request` → AccountService  
  → `fetch-user-by-account-id` → UserService  
  → replies to dynamic topic like `user-data-response-xyz`

---


# Sample cURL Commands

## Create User
### cURL
```bash
curl --location 'http://localhost:8081/users' \
--header 'Content-Type: application/json' \
--data-raw '{
        "name": "John Doe",
        "email": "john.doe@example.com"
      }'
```
### Response
```json
{
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "deleted": false
}
```


## Create Account
### cURL
```bash
curl --location 'http://localhost:8082/accounts' \
--header 'Content-Type: application/json' \
--data '{
        "userId": 1,
        "balance": 5000,
        "currency": "INR"
      }'
```
### Response
```json
{
  "id": 1,
  "userId": 1,
  "balance": 5000,
  "currency": "INR",
  "deleted": false
}
```


## Create Transaction
### cURL
```bash
curl --location 'http://localhost:8083/transactions' \
--header 'Content-Type: application/json' \
--data '{
    "accountId": 1,
    "amount": 12000.00,
    "type": "CREDIT",
    "description": "Salary"
}'
```
### Response
```json
{
  "id": 3,
  "accountId": 1,
  "amount": 12000.00,
  "type": "CREDIT",
  "description": "Salary",
  "createdAt": "2025-07-23T17:25:11.107631"
}
```


## Fetch Transaction
### cURL
```bash
curl --location 'http://localhost:8083/transactions/by-account?accountId=1&from=2025-07-01&to=2025-07-31'
```
### Response
```json
{
  "transactionList": [
    {
      "id": null,
      "accountId": null,
      "amount": 125.00,
      "type": "DEBIT",
      "description": null,
      "createdAt": "2025-07-23T17:24:53.741098"
    },
    {
      "id": null,
      "accountId": null,
      "amount": 12.00,
      "type": "DEBIT",
      "description": null,
      "createdAt": "2025-07-23T17:24:58.59264"
    },
    {
      "id": null,
      "accountId": null,
      "amount": 12000.00,
      "type": "CREDIT",
      "description": null,
      "createdAt": "2025-07-23T17:25:11.107631"
    }
  ],
  "userData": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "deleted": false,
    "createdAt": "2025-07-23T16:57:18.752794",
    "updatedAt": "2025-07-23T16:57:18.752794"
  }
}
```

#  Running the Application

## 0. Install Docker
```bash
brew install --cask docker
brew install --cask docker-compose
```

## 1. Install & Run Kafka using Docker
Make sure you have Docker installed. Then in the root directory (amex/).
Run the command, to start the fakfa service
```bash
docker compose up -d
```

## 2. Build & Run Services
### Run UserService
```bash
cd UserService
./mvnw spring-boot:run
```

### Run AccountService
```bash
cd AccountService
./mvnw spring-boot:run
```

### Run TransactionService
```bash
cd TransactionService
./mvnw spring-boot:run
```