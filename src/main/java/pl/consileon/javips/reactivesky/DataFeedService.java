package pl.consileon.javips.reactivesky;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import pl.consileon.javips.reactivesky.model.AircraftState;
import pl.consileon.javips.reactivesky.openskyapi.OpenSkyService;
import pl.consileon.javips.reactivesky.repository.AircraftStateRepository;
import reactor.core.publisher.Flux;

@Service
public class DataFeedService {

    private final Logger logger = LoggerFactory.getLogger( DataFeedService.class );

    @Autowired
    private AircraftStateRepository aircraftStateRepository;

    @Autowired
    private OpenSkyService openSkyService;

    //@Scheduled(fixedRate = 10000)
    public void retrieveData() {

        Flux<AircraftState> statesStream = openSkyService //
                .retrieveStates().log(); //

        aircraftStateRepository.insert( statesStream ).blockLast();

    }

}
