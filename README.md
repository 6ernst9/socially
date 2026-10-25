# Socially

A full-stack social media application built with **React, TypeScript, and Spring Boot**, organized into separate backend services for accounts, connections, content, and messaging.

## Overview

Socially is a modular web application project exploring the architecture of a social platform. Its frontend communicates with a collection of backend services responsible for different parts of the application.

The project combines a modern React frontend with Java-based backend services, reactive programming, database integration, and real-time communication technologies.

## Architecture

The repository is organized into the following modules:

| Module | Responsibility |
|---|---|
| `social-media-front` | React and TypeScript frontend |
| `api_gateway` | API gateway and backend request entry point |
| `account` | Account-related functionality and PostgreSQL integration |
| `connection` | Connection-related functionality |
| `content` | Content-related functionality and MongoDB integration |
| `messages` | Messaging-related functionality |

## Tech Stack

### Frontend
- React 18
- TypeScript
- Redux Toolkit and Redux Persist
- React Router
- Axios
- Bootstrap and React Bootstrap
- Firebase
- STOMP and SockJS client libraries

### Backend
- Java 17
- Spring Boot
- Spring Web and WebFlux
- Spring Data R2DBC
- Spring Security Crypto
- Spring Boot Actuator
- Project Reactor
- Lombok

### Databases and communication
- PostgreSQL
- MongoDB
- STOMP-based messaging support
- Reactive APIs and asynchronous processing

## Project Structure

```text
socially/
├── account/
├── api_gateway/
├── connection/
├── content/
├── messages/
└── social-media-front/
```

Each backend module has its own Maven configuration, while the frontend has a separate Node.js package configuration.

## Getting Started

### Prerequisites

Install the following tools before running the application:

- Java 17 or compatible JDK
- Node.js and npm
- Maven, or the included Maven Wrapper
- PostgreSQL
- MongoDB

Additional services or credentials may be required depending on the application configuration.

### 1. Clone the repository

```bash
git clone https://github.com/6ernst9/socially.git
cd socially
```

### 2. Configure the backend

Review the configuration files in each backend module and configure the required database connections, service URLs, credentials, and other environment-specific settings.

Ensure PostgreSQL and MongoDB are running where required.

### 3. Start the backend services

From each backend module directory, use the Maven Wrapper to build and run the corresponding service.

On Windows:

```cmd
mvnw.cmd spring-boot:run
```

On macOS or Linux:

```bash
./mvnw spring-boot:run
```

Run the command from the relevant module directory. Start the services in the order required by their configuration.

### 4. Start the frontend

Open a terminal in the frontend directory:

```bash
cd social-media-front
npm install
npm start
```

The React development server will start using the configured development environment.

Make sure the backend endpoints and any required Firebase configuration match your local environment.

## Development

The project is split into independent frontend and backend modules, allowing each part to be developed and maintained separately.

- **Frontend:** `social-media-front`
- **Backend services:** individual Java/Spring Boot modules
- **Build tooling:** npm and Maven
- **Configuration:** module-specific application settings

## Future Improvements

Potential areas for further development include automated testing, API documentation, containerized development environments, and streamlined service orchestration.

---

*Built as a full-stack software development project.*
