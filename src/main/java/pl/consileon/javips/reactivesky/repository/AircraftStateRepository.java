package pl.consileon.javips.reactivesky.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import pl.consileon.javips.reactivesky.model.AircraftState;

@Repository
public interface AircraftStateRepository extends ReactiveMongoRepository<AircraftState, String> {

}
