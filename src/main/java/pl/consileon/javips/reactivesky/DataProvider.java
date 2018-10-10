package pl.consileon.javips.reactivesky;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

import pl.consileon.javips.reactivesky.model.AircraftState;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

@Configuration
public class DataProvider {

    private static final String COLLECTION_NAME = "aircraftState";
    
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;
    
    @Bean(name="aircraftStateChangeStream")
    public Flux<AircraftState> getChangeStream() {

        
        //https://github.com/spring-projects/spring-data-examples/blob/master/mongodb/change-streams/src/test/java/example/springdata/mongodb/ChangeStreamsTests.java
        
        Flux<ChangeStreamEvent<AircraftState>> changeStream 
            = mongoTemplate.changeStream(COLLECTION_NAME, ChangeStreamOptions.builder()
                    
                    .filter(newAggregation( match ( where("operationType").is("insert")))).build(),
                AircraftState.class);
        
        Flux<AircraftState> statesStream = changeStream.map( ChangeStreamEvent::getBody );
        
        
        EmitterProcessor<AircraftState> p = EmitterProcessor.create(100);
        
        //DirectProcessor<AircraftState> dp = DirectProcessor.create();
        statesStream.subscribe(p);
        
        //dp.subscribe(System.err::println);
        
        
        
        
        return statesStream;
    }
    
}
