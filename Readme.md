```markdown
# Leave Management System - Backend

A backend system for managing employee leave requests, built with **Java 24**, **Spring Boot**, **JWT Authentication**, and **Microsoft Azure OAuth2**. It is easily deployable via **Docker**.


## Features

- Secure authentication using **JWT** and **Azure OAuth2**
- Role-based access (Admin, Manager, Employee)
- APIs for leave requests (CRUD + approval flow)
- Validation with **Jakarta Bean Validation**
- Integrated with **MySQL 8+**
- Docker-ready for seamless deployment
- Clean, RESTful API design



## Docker Hub

You can find the Docker image at:  
**devrob123/leave-ms-backend**

To pull the latest version, use:

```bash
docker pull devrob123/leave-ms-backend
```

---

## Requirements

- **Java 24**
- **Docker** & **Docker Compose**
- **MySQL 8+**
- **Maven** (for package management)
- **IntelliJ IDEA** (Recommended IDE)
- Azure App Registration (for OAuth2)

---

## Project Structure

```bash
leave-ms-backend/
├── src/main/java/com/robert/leave_ms_bn/...
├── Dockerfile
├── .env
├── application.yml
├── pom.xml
└── README.md
```

---

## Environment Variables

Create a `.env` file at the root:

```env
# MySQL Configuration
MYSQL_DATABASE=leave_db
MYSQL_USER=
MYSQL_PASSWORD=
MYSQL_HOST=localhost
MYSQL_PORT=3306

# JWT Secret
JWT_SECRET=your-very-secure-jwt-secret

# Azure OAuth2 Configuration
AZURE_CLIENT_ID=your-azure-client-id
AZURE_CLIENT_SECRET=your-azure-client-secret
AZURE_TENANT_ID=your-azure-tenant-id
AZURE_REDIRECT_URI=http://localhost:8080/login/oauth2/code/azure
```

> **Tip:** Use [dotenv-spring-boot](https://github.com/cdimascio/dotenv-spring-boot) to load `.env` variables.

---

## Docker Build & Run

### Build Locally

```bash
docker build -t devrob123/leave-ms-backend .
```

### Run the Container

```bash
docker run -d -p 8080:8080 --env-file .env devrob123/leave-ms-backend
```

---

## API Usage

You can test the API using tools like **Postman** or **cURL**.

### Authentication Request

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
}'
```

### Authenticated Request (JWT Required)

```bash
curl -H "Authorization: Bearer <your-token>" \
  http://localhost:8080/api/leave-requests
```

---

## Azure OAuth2 Setup

1. Register your app on the [Azure Portal](https://portal.azure.com)
2. Set the redirect URI to:

```
http://localhost:8080/login/oauth2/code/azure
```

3. Add the following configuration to `application.yml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          azure:
            client-id: ${AZURE_CLIENT_ID}
            client-secret: ${AZURE_CLIENT_SECRET}
            redirect-uri: ${AZURE_REDIRECT_URI}
            scope: openid, profile, email
        provider:
          azure:
            issuer-uri: https://login.microsoftonline.com/${AZURE_TENANT_ID}/v2.0
```

---

## Health Check

- **Spring Boot Health Check**:  
  [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

- **OAuth2 Authentication**:  
  [http://localhost:8080/oauth2/authorization/azure](http://localhost:8080/oauth2/authorization/azure)

---

## Docker Commands

```bash
# Run the container with live logs
docker run -it --env-file .env -p 8080:8080 devrob123/leave-ms-backend

# Stop all running containers
docker stop $(docker ps -aq)
```

---

## License

This project is licensed under the **MIT License**.

---

## Contributing

Feel free to fork the repository, create pull requests, or open issues for bug fixes or feature requests.

---

## Contact

For support, reach out to:  
**Email**: robertwilly668@gmail.com  
**GitHub Issues**: [Open an Issue](https://github.com/niroberto/leave-ms-backend/issues)
```

---

### Key Updates:

1. **IDE**: Added **IntelliJ IDEA** as the recommended IDE.
2. **Package Management**: Clearly mentioned the use of **Maven** for package management.
3. **Docker and Azure** setups remain the same, with all instructions cleaned up.






