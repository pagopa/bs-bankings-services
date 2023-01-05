# bs-bankings-services

Template project with all the logic of check iban banking services for PA.

Runs locally on Docker.

## Requirements

* Windows 10 / MacOS
* 16 GB of RAM
* Docker (tested on 20.10.21)
* AWS credentials for *Kinesis Firestore* (must accept JSON data)

## Setup

```
# Starts the docker compose process, the APIs will be exposed on port 8080
./start.sh

# Stops the docker compose process
./stop.sh

# Cleans the created containers
./clean.sh
```

todo: complete

## Usage

The project exposes 2 sets of APIs:
* Validate Account Holder APIs
* Config APIs

By using the *Postman Collection* committed in this repo you can test the various services exposed by the application.

Note that all the PSPs (Payment Service Provider) connected to the service are mocked locally for demo purposes.

todo: complete

## Backend Structure

Here are described the contents of each backend package:

* bs-banking-services-common
* bs-banking-services-ade
* bs-banking-services-checkiban
* bs-banking-services-web

todo: complete

## Architecture

This is a pure *backend* service implemented with *Spring Boot Framework* and the *Java* programming language (JDK 8).

Furthermore, it uses *Oracle Express Edition 21* as a DB and *Zookeeper + Kafka* as a message queue to send near-realtime events to a *AWS Firestore* endpoint.

todo: complete

### Components Diagram
todo: complete
