# Odds Based Game

A simple odds-based game built with Kotlin and Spring Boot.

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) installed and running.
- (Optional) JDK 21 and Gradle installed locally for development and testing.

## Building and Running with Docker

### 1. Build the Docker Image

From the project root, run:

```bash
docker build -t game .
```

### 2. Run the Docker Container

Once the image is built, run:

```bash
docker run -p 8080:8080 game
```

The application will start and listen on port 8080. You can access it at [http://localhost:8080](http://localhost:8080).

## Running Locally

If you prefer to run the application without Docker:

### 1. Build the Application

From the project root, use Gradle to build:

```bash
./gradlew clean build
```

### 2. Run the Application

Run the generated jar file:

```bash
java -jar build/libs/game-0.0.1-SNAPSHOT.jar
```

The application will start and be accessible at [http://localhost:8080](http://localhost:8080).

## Running Unit Tests

To run all unit tests, execute:

```bash
./gradlew test
```

After the tests finish, you can review the results in the generated report at:
`build/reports/tests/test/index.html`.


## Example JSON payloads

You can find example JSON payloads for testing all the API endpoints [here](SAMPLE_JSON_PAYLOADS.md).
