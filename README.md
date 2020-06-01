# cloud-native
Spring Boot, Spring Cloud and Cloud Foundry

# Setup
 - Java 8 or newer
 - Apache Maven 3.1 or newer
 - IDE installed
 - [Spring Boot CLI](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-cli.html) (check with `spring version`)
 - [Spring Cloud CLI](https://cloud.spring.io/spring-cloud-cli/reference/html/) (when you have spring boot cli run the following command `spring install org.springframework.cloud:spring-cloud-cli:2.2.1.RELEASE` in your spring boot cli /bin folder and then verify `spring cloud --version`)
 - [Cloud Foundry CLI](https://github.com/cloudfoundry/cli#installers-and-compressed-binaries) (install it and verify `cf --version`)
 
 
# How to run

## 1. Start Eureka

Go to `./config-server` and run `spring cloud eureka`. That will start eureka server for you.

## 2. Start configServer, hystrixdashboard and zipking

Go to `./config-server` and run `spring cloud configserver hystrixdashboard zipkin`.

## 3. Start RabbitMQ

Go to `./bin` folder and execute the `rabbitmsq.sh`. It will start a docker image with RabbitMQ instance running on the default port

## 4. Run the services

**Open the repo folder in our IDE and run one by one each service.**
 - Auth service
 - Reservation service
 - Reservation client
 
**Verify they are working** 
Open Eureka dashboard at `http:localhost:8761` and check everything is up.

## 5. Test the services

You can load the postman collection `CloudNative.postman_collection.json` or just execute the cURL commands 
but be sure to change the bearer token with your obtained token.

First you are going to need to obtain an authentication token.
Execute `[AuthService] Obtain token` in Postman or this cURL 
```
curl --location --request POST 'http://localhost:9191/uaa/oauth/token' \
--header 'Authorization: Basic aHRtbDU6c2VjcmV0' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=nick' \
--data-urlencode 'password=pass'
```

Next you can fetch all the reservations by making HTTP GET `[ReservationServiceProxy] Get reservation names` in Postman or 
```
curl --location --request GET 'http://localhost:9999/reservations/names' \
--header 'Authorization: Bearer 21ab48a0-0f42-4b77-847f-757364d3e570' \
```

For more request please check the Postman collection.

 


