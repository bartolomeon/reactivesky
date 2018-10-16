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

import pl.consileon.javips.reactivesky.model.AircraftState;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

/**
 * @author bartek
 *
 */
@Configuration
public class AircraftDataProvider {

    private static final String COLLECTION_NAME = "aircraftState";

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    /**
     * Singleton providing a stream of updates for aircrafts data. Multiple clients can subscribe since it is backed by an EmmiterProcessor.
     * 
     * @see 
     *  <a href="https://github.com/spring-projects/spring-data-examples/blob/master/mongodb/change-streams/src/test/java/example/springdata/mongodb/ChangeStreamsTests.java">ChangeStreamsTests.java </a>
     * 
     * @return stream of recently received aircrafts' data
     */
    @Bean(name = "aircraftStateChangeStream")
    public Flux<AircraftState> getChangeStream() {

        Flux<ChangeStreamEvent<AircraftState>> changeStream = mongoTemplate.changeStream( COLLECTION_NAME, ChangeStreamOptions.builder()

                .filter( newAggregation( match( where( "operationType" ).is( "insert" ) ) ) ).build(), AircraftState.class );

        Flux<AircraftState> statesStream = changeStream.map( ChangeStreamEvent::getBody );

        EmitterProcessor<AircraftState> p = EmitterProcessor.create();

        statesStream.subscribe( p );

        return statesStream;
    }

}
