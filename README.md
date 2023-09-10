**Project Title:** Mini Bank

**Project Description:**

The "Mini Bank" project is a Spring Boot back-end application that provides a simplified platform for individual and corporate clients to register and perform fund transfers among themselves. This mini bank offers a secure and efficient experience for managing accounts, recording client information, and conducting financial transactions.

**Key Features:**

1. **Client Registration:**
   - The system allows clients to register by providing personal information such as name, CPF (individual) or CNPJ (corporate), address, contact phone number, and banking details.

2. **Account Management:**
   - Each client will have an associated account containing balance information and transaction history.
   - The system automatically generates a unique account number for each client.

3. **Fund Transfers:**
   - Clients can perform money transfers between their own accounts or to the accounts of other clients registered in the system.
   - Transfers can be made by specifying the destination account number and the transfer amount.
   - It is necessary to check the account balance before initiating a transfer to ensure that the client has sufficient funds.

4. **Balance Inquiry and Statement:** (WORK IN PROGRESS)
   - Clients can check the current balance of their accounts and view the transaction history, including dates, amounts, and descriptions of operations.

5. **Security and Authentication:**
   - To ensure transaction security, the system requires client authentication. This can be done through credentials such as a username and password or through two-factor authentication, such as a code sent via SMS.

6. **Notifications and Alerts:** (WORK IN PROGRESS)
   - The system can send notifications via email or SMS to inform clients about activities in their accounts, such as successful transfers, insufficient balance, or other relevant events.

7. **Reports and Statistics:** (WORK IN PROGRESS)
   - System administrators can generate reports and statistics to analyze the performance of the mini bank, including transaction volume, revenue, expenses, and other relevant metrics.

**Technologies Used:**

- Spring Boot: For the development of the back-end application.
- Spring Boot Security - Implementation of robust security measures to protect client data.
- Spring Data JPA
- SpringDoc OpenAPI 3
- JWT - Authentication by JWT Token.
- Database: For storing client information, accounts, and transactions.

## **Technologies Used:**

 - **SOLID**: SOLID principles were followed to create more modular and flexible code, making it easier to maintain and evolve the system.

- **DRY (Don't Repeat Yourself)**: Code duplication was avoided, promoting code reuse and efficient code organization.

- **RESTful API**: For communication between the back-end application and potential front-end clients.

- **Queries with Spring Data JPA**: Spring Data JPA queries were used to interact with the database efficiently and elegantly.

- **Dependency Injection**: Spring Boot manages dependency injection, allowing for more decoupled and testable code.

- **Error Response Handling**: Mechanisms for error handling were implemented, providing clear and meaningful responses in case of failures. (WORK IN PROGRESS)

- **Automatic Swagger Documentation with OpenAPI 3**: API documentation is automatically generated using SpringDoc OpenAPI 3, making it easy to understand and use the API. (WORK IN PROGRESS)

- **Unit Testing**: Utilized for testing individual components and functions to ensure code reliability and correctness.

- **Email and SMS Framework**: For sending notifications and alerts to clients. (TODO)



**Final Objective:**

The ultimate goal of the "Mini Bank" project is to provide a service back-end for performing basic banking operations, such as registration, transfers, and balance inquiries, while ensuring the security and reliability of financial transactions. This mini bank can serve as a foundation for future expansions and improvements as needed.

## Getting Started

To run the project, you can use Maven. Navigate to the project root directory and execute the following command:

```shell
mvn spring-boot:run