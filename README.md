# Bookshelf API

A library management backend built with Spring Boot and PostgreSQL. It manages books, categories, tags and loans, supports cover image uploads, and includes a simple web page to use it.

## Requirements

- Java 17+
- PostgreSQL

## Setup

Create the database in PostgreSQL:

```sql
CREATE USER bookshelf_user WITH PASSWORD 'password123';
CREATE DATABASE bookshelf_db OWNER bookshelf_user;
```

## Run

```powershell
.\mvnw.cmd spring-boot:run
```

(On macOS/Linux: `./mvnw spring-boot:run`)

Then open:

- Web page: http://localhost:8080
- API docs (Swagger): http://localhost:8080/swagger-ui/index.html

Sample data is added automatically on the first start.
