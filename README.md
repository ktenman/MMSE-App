# AI-Powered MMSE Web Application

This repository contains the source code for an AI-powered web application that implements the Mini-Mental State
Examination (MMSE) for cognitive assessment. The application leverages various artificial intelligence models and
technologies to provide an enhanced user experience. It is built using Java 17 and TypeScript, with a PostgreSQL
database for data storage and Vue.js for the frontend. The project is powered by Spring Boot 3.2.2 and utilizes Docker
and Docker Compose for containerization and easy deployment.

## Features

- Integration of AI models:
    - BERT (Bidirectional Encoder Representations from Transformers): Pre-trained deep learning model for natural
      language
      processing tasks
    - GPT-3 (Generative Pre-trained Transformer 3): Large language model for text generation and understanding
    - ResNet-50: Deep learning model for image recognition and classification
- Automated scoring and validation mechanisms for the MMSE
- Containerization with Docker and Docker Compose for simplified deployment and scalability
- Backend implemented in Java 17 with Spring Boot 3.2.2 for robust and efficient server-side processing
- Frontend built with Vue.js for a responsive and interactive user interface
- PostgreSQL database for reliable data storage and retrieval
- Liquibase for database schema management and versioning
- Comprehensive testing suite:
- Unit testing to ensure individual components function as expected
- Integration testing with Testcontainers for realistic end-to-end testing

## Prerequisites

To run this application locally, you need to have the following installed:

- Docker
- Docker Compose
- Java Development Kit (JDK) 17
- Node.js v18.16.1
- npm 9.8.0
- Maven 3.2.5

## Getting Started

1. Clone the repository:
    ```
    git clone https://github.com/ktenman/MMSE-App.git
    ```

2. Navigate to the project directory:
    ```
    cd MMSE-App
    ```

3. Build and start the application using Docker Compose:
    ```
    sh start-all-containers.sh
    docker-compose up --build
    ```

4. Access the application in your web browser at http://localhost:8080.

## Testing

To run the test suite, execute the following command:

```
./mvnw verify
```

This will run both unit tests and integration tests using Testcontainers.

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a
pull request.

## Tools Used

This project made use of the following AI-assisted tools:

- Claude AI: Used for generating and refining content, providing suggestions, and assisting with the writing process.
- GitHub Copilot: Used for code generation, autocompletion, and providing code suggestions throughout the development
  process.

