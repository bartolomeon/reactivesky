package pl.consileon.javips.reactivesky.web;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.consileon.javips.reactivesky.model.AircraftState;
import pl.consileon.javips.reactivesky.repository.AircraftStateRepository;
import reactor.core.publisher.Flux;

@RestController
public class ReactiveSkyController {
    
    @Autowired
    private AircraftStateRepository repository;

    @GetMapping("states")
    public Flux<AircraftState> getAllStatuses(@Param(value = "fromTime") long fromTime) {
        return repository.findAll();
    }

    @GetMapping(value = "/stream/states", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AircraftState> streamAllStatuses(@Param(value = "fromTime") long fromTime) {
        return repository.findAll();
    }
}
