package com.simitchiyski.reservationservice;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.stream.Stream;

@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @RestResource(path = "by-name")
    Collection<Reservation> findAllByReservationName(@Param("rn") final String rn);
}

interface ServiceChannels {
    @Input
    SubscribableChannel input();
}

@RefreshScope
@RestController
class MessageRestController {

    @Value("${message:Hello default}")
    private String message;

    @GetMapping("/message")
    String read() {
        return this.message;
    }
}

@EnableDiscoveryClient
@SpringBootApplication
@EnableBinding(ServiceChannels.class)
public class ReservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }

}

@Component
@RequiredArgsConstructor
class ReservationProcessor {
    private final ReservationRepository reservationRepository;

    @StreamListener("input")
    public void onNewReservationPleaseDoSomthingWithTheREquestOKThanks(String rn) {
        this.reservationRepository.save(new Reservation(rn));
    }
}

@Component
@RequiredArgsConstructor
class SampleDataCLR implements CommandLineRunner {

    private final ReservationRepository reservationRepository;

    @Override
    public void run(String... args) throws Exception {
        Stream.of("Nick", "Josh", "John", "Doe",
                "Oliver", "Andrew", "Olivia", "Viktoria")
                .map(Reservation::new)
                .forEach(this.reservationRepository::save);

        this.reservationRepository.findAll()
                .forEach(System.out::println);
    }
}

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
class Reservation {

    @Id
    @GeneratedValue
    private Long id;

    private String reservationName;

    public Reservation(final String reservationName) {
        this.reservationName = reservationName;
    }
}
