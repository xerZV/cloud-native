# cloud-native
Spring Boot, Spring Cloud and Cloud Foundry

# Setup
 - Java 8 or newer
 - Apache Maven 3.1 or newer
 - IDE installed
 - [Spring Boot CLI](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-cli.html) (check with `spring version`)
 - [Spring Cloud CLI](https://cloud.spring.io/spring-cloud-cli/reference/html/) (when you have spring boot cli run the following command `spring install org.springframework.cloud:spring-cloud-cli:2.2.1.RELEASE` in your spring boot cli /bin folder and then verify `spring cloud --version`)
 - [Cloud Foundry CLI](https://github.com/cloudfoundry/cli#installers-and-compressed-binaries) (install it and verify `cf --version`)
 
 
# How to run with spring cloud CLI

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

# How to run locally only with IDE

**Start RabbitMQ:**
 - Go to `./bin` folder and execute the `rabbitmsq.sh`. It will start a docker image with RabbitMQ instance running on the default port

**Run these applications as spring boot applications in order like this:**
 1. config-service 
 2. eureka-service
 3. hystrix-dashboard
 4. auth-service
 5. reservation-service
 6. reservation-client
 
Meanwhile you can run dataflow as executing this command in `/dataflow-service` folder:
```shell script
java -jar spring-cloud-dataflow-server-2.5.1.RELEASE.jar
java -jar spring-cloud-skipper-server-2.4.1.RELEASE.jar
```

And you can access DataFlow dashboard at `http://localhost:9393/dashboard`

Let's say we want to monitor a directory 
and for each file that appears in that 
we want to take each line and send the line to the rabbitmq:
 1. After that you can click on the button `+Add Application(s)`
 2. Click `Bulk import application coordinates from an HTTP URI location.`
 3. Then select from below `Stream Apps (RabbitMQ/Maven)`
 4. Click `Streams`
 5. Click `Create stream`
 6. I have created an empty directory in my Desktop called `dataflow`. You can create a dir somewhere and make sure to point its location in the command below
 7. Paste this in the text area `GetFilesFromADir: file --directory=C:\\Users\\your-user\\Desktop\\dataflow --filename-pattern=*.txt --mode=lines | toUpperCase: transform --expression=payload.toUpperCase() > :reservations`
 8. Click `Create Stream(s)` and give it a proper name.
 9. Deploy the stream. 
 10. Create a txt file in the directory and write some names each on a new line.
 11. Verify HTTP GET `http://localhost:9999/reservations/names` (with Authentication)
 
Also you can use cURL or Postman collection.


