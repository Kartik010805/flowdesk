# FlowDesk — Request & Approval Management System

FlowDesk is a web-based request and approval management system built with Spring Boot and PostgreSQL. It helps organizations manage employee requests through a centralized workflow with role-based access, manager approvals, request tracking, and audit logging.

**Live Demo:** https://flowdesk-ys7i.onrender.com

## Features

### Role-Based Access

FlowDesk supports three roles:

- **Admin**
    - Manage employee and manager accounts.
    - Activate and deactivate user accounts.
    - View all submitted requests.
    - View approval workflows.
    - Review audit logs.
    - Access system-wide dashboard statistics.

- **Manager**
    - View requests submitted by assigned employees.
    - Approve or reject requests.
    - Add comments to approval decisions.
    - View manager-specific dashboard statistics.

- **Employee**
    - Submit requests.
    - View personal request history.
    - Track request statuses.
    - View request details and approval history.

### Request Management

Supported request types:

- Leave
- Expense
- Equipment
- IT Support
- Salary Revision
- Other

Request statuses include:

- Draft
- Submitted
- In Review
- Approved
- Rejected
- Cancelled

### Approval Management

- Single-manager approval workflow.
- Managers can approve or reject requests from assigned employees.
- Approval decisions and comments are recorded.
- Approval history is available on request details pages.

### Audit Logging

Important application actions are recorded, including:

- Request submissions.
- Request approvals.
- Request rejections.

Audit logs include the actor, action, details, and timestamp.

### Dashboard Statistics

Role-specific dashboards display relevant statistics.

Dashboard counts use Spring Data JPA repository count queries rather than loading all request records into application memory.

## Scalability and Performance Considerations

FlowDesk was developed with attention to database efficiency and avoiding unnecessary data loading.

The following decisions were implemented to provide a better foundation for handling growing application data.

### 1. Database-Level Aggregation

Dashboard statistics are calculated using Spring Data JPA derived count queries.

Examples include:

- `countByStatus()`
- `countByEmployee_Id()`
- `countByEmployee_Manager_Id()`
- `countByEmployee_Manager_IdAndStatusIn()`

This avoids retrieving complete request collections into application memory just to calculate dashboard totals.

**Why it matters:** Database aggregation reduces unnecessary data transfer and application memory usage as the number of requests increases.

### 2. Database Indexing

Indexes were added to frequently queried columns.

Examples include:

- Request employee ID.
- Request status.
- Approval request ID.
- Approval approver ID.
- Audit log creation timestamp.
- Audit log actor email.

**Why it matters:** Indexes can improve filtering and lookup performance for frequently accessed records.

Indexes should still be reviewed against actual query patterns and execution plans as the dataset grows.

### 3. Controlled Entity Fetching

Entity relationships use lazy fetching where appropriate.

Explicit JPA `@EntityGraph` configurations are used for queries that require related data, such as employee, department, approver, and approval step information.

**Why it matters:** Fetching only the relationships needed for a particular operation helps avoid unnecessary database loading and reduces the risk of N+1 query problems.

### 4. Filtered Repository Queries

Repository methods retrieve records based on relevant ownership and workflow conditions.

Examples include:

- Employee-specific request history.
- Manager-specific employee requests.
- Manager inbox requests filtered by request status.
- Requests ordered by creation date.

**Why it matters:** Filtering at the database level avoids loading unrelated records and performing unnecessary filtering in application memory.

### 5. Disabled Open Session in View

The application uses:

```properties
spring.jpa.open-in-view=false
```

This prevents the persistence context from remaining available throughout the entire web request.

**Why it matters:** It encourages database access to be handled deliberately within service-layer transactions instead of triggering unexpected lazy-loading queries during template rendering.

### 6. Docker Multi-Stage Build

The Dockerfile uses separate build and runtime stages:

- Maven and JDK 17 are used to compile and package the application.
- A Java 17 JRE image is used to run the packaged application.

**Why it matters:** Build dependencies are not carried into the final runtime image, reducing unnecessary runtime image contents.

### Future Scalability Improvements

Before using FlowDesk for a larger production workload, the following improvements should be considered:

- Add pagination to request and audit-log listings.
- Review query execution plans using PostgreSQL `EXPLAIN ANALYZE`.
- Add or adjust indexes based on actual query performance.
- Introduce Flyway or Liquibase for version-controlled database migrations.
- Add automated integration and performance tests.
- Review database connection-pool configuration.
- Consider caching only where repeated reads justify it.
- Monitor database performance, application memory, and response times.

These improvements have not all been implemented; they are identified as future engineering work rather than existing capabilities.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 4.2.0 | Backend framework |
| Spring Security | Authentication and authorization |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| PostgreSQL | Relational database |
| Thymeleaf | Server-side templates |
| Bootstrap | Frontend styling |
| Maven | Build management |
| Docker | Containerization |
| Render | Cloud deployment |

## Application Architecture

The application follows a layered architecture:

- **Controller Layer:** Handles HTTP requests and responses.
- **Service Layer:** Contains business logic and workflow rules.
- **Repository Layer:** Handles database operations using Spring Data JPA.
- **Entity Layer:** Defines persistent domain models.
- **Configuration Layer:** Configures security and application initialization.
- **Template Layer:** Contains Thymeleaf HTML views.

## Project Structure

```text
src/
└── main/
    ├── java/com/example/demo/
    │   ├── config/
    │   ├── controller/
    │   ├── dto/
    │   ├── entity/
    │   ├── enums/
    │   ├── repository/
    │   ├── service/
    │   └── DemoApplication.java
    │
    └── resources/
        ├── static/
        ├── templates/
        └── application.properties

Dockerfile
.dockerignore
pom.xml
README.md
```

## Local Setup

### Prerequisites

Install:

- JDK 17
- PostgreSQL
- Git

### 1. Clone the repository

```bash
git clone https://github.com/Kartik010805/flowdesk.git
cd flowdesk
```

### 2. Create the PostgreSQL database

```sql
CREATE DATABASE flowdesk_db;
```

### 3. Configure environment variables

Required variables:

| Variable | Description |
|---|---|
| `DB_URL` | JDBC database connection URL |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `ADMIN_PASSWORD` | Initial administrator password |

For Windows PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/flowdesk_db"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_database_password"
$env:ADMIN_PASSWORD="your_strong_admin_password"
```

Do not commit actual credentials to GitHub.

### 4. Run the application

```powershell
.\mvnw.cmd spring-boot:run
```

Alternatively, build the application:

```powershell
.\mvnw.cmd clean package
```

The application runs at:

http://localhost:8080

Login page:

http://localhost:8080/login

Initial administrator email:

`admin@flowdesk.com`

The administrator password is the value configured through `ADMIN_PASSWORD`.

## Docker

FlowDesk includes a multi-stage Dockerfile.

### Build the Docker image

```bash
docker build -t flowdesk .
```

### Run the container

```bash
docker run -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://host.docker.internal:5432/flowdesk_db" \
  -e DB_USERNAME="postgres" \
  -e DB_PASSWORD="your_database_password" \
  -e ADMIN_PASSWORD="your_strong_admin_password" \
  flowdesk
```

The example uses `host.docker.internal` to connect to PostgreSQL running on the host machine from Docker Desktop.

## Deployment

The application is deployed on Render using Docker and a managed PostgreSQL database.

Deployment overview:

1. Source code is maintained in GitHub.
2. Render connects to the GitHub repository.
3. Render builds the Docker image using the project's Dockerfile.
4. The application starts inside a Docker container.
5. Spring Boot connects to Render PostgreSQL using environment variables.

### Render Environment Variables

Configure these variables in the Render web service:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `ADMIN_PASSWORD`

Use the PostgreSQL internal hostname when constructing the JDBC URL:

```text
jdbc:postgresql://HOSTNAME:5432/DATABASE_NAME
```

Render supplies the application port through the `PORT` environment variable.

**Live Application:** https://flowdesk-ys7i.onrender.com

## Database Initialization

On application startup, the initializer creates the initial administrator account, default departments, and default manager approval steps when they do not already exist.

The application uses:

```properties
spring.jpa.hibernate.ddl-auto=update
```

This is convenient for development and demonstration. For production systems, database schema changes should be managed through versioned migration tools such as Flyway or Liquibase.

## Testing and Verification

The application was manually tested locally and after deployment.

Verified scenarios include:

- Administrator authentication and dashboard access.
- Manager and employee account creation.
- User activation and deactivation.
- Employee request submission.
- Manager approval and rejection.
- Request details and approval history.
- Audit log generation.
- Role-based access restrictions.
- Unauthorized request access prevention.
- Dashboard statistics.
- PostgreSQL connectivity in the deployed environment.

These are manual verification results, not a claim of comprehensive automated test coverage.

## Current Limitations

- The approval process currently supports a single manager approval level.
- Dashboard aggregate queries are optimized at the repository level, but additional performance testing is needed as data grows.
- Hibernate automatic schema updates should be replaced with database migrations for production.
- Automated integration and unit test coverage can be expanded.
- Pagination and indexing should be reviewed as request and audit data increase.
- Render's free-tier resources have availability and performance limitations and are intended for demonstration rather than production workloads.

## Author

**Kartik**

GitHub: https://github.com/Kartik010805