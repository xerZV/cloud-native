package com.simitchiyski.reservationclient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.hateoas.CollectionModel;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.MessageChannel;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@FeignClient("reservation-service")
interface ReservationReader {

    @RequestMapping(method = RequestMethod.GET, value = "/reservations")
    CollectionModel<Reservation> getReservations();
}

interface ClientChannels {
    @Output
    MessageChannel output();
}

@MessagingGateway
interface ReservationWriter {
    @Gateway(requestChannel = "output")
    void write(String rn);
}

@EnableZuulProxy
@EnableFeignClients
@EnableCircuitBreaker
@EnableResourceServer
@EnableDiscoveryClient
@SpringBootApplication
@IntegrationComponentScan
@EnableBinding(ClientChannels.class)
public class ReservationClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationClientApplication.class, args);
    }

}

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
class Reservation {
    private String reservationName;
}

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
class ReservationAdapterApiRestController {

    private final ReservationReader reservationReader;
    private final ReservationWriter reservationWriter;

    public Collection<String> fallback() {
        return new ArrayList<>();
    }

    @PostMapping
    public void write(@RequestBody Reservation r) {
        this.reservationWriter.write(r.getReservationName());
    }

    @GetMapping("/names")
    @HystrixCommand(fallbackMethod = "fallback")
    public Collection<String> names() {
        return this.reservationReader.getReservations()
                .getContent()
                .stream()
                .map(Reservation::getReservationName)
                .collect(Collectors.toList());
    }
}
