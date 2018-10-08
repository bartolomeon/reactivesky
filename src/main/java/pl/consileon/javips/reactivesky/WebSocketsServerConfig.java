package pl.consileon.javips.reactivesky;

import java.time.Duration;
import java.util.Collections;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.consileon.javips.reactivesky.model.AircraftState;
import pl.consileon.javips.reactivesky.repository.AircraftStateRepository;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class WebSocketsServerConfig {

    private final Logger logger = LoggerFactory.getLogger( WebSocketsServerConfig.class );

    @Autowired
    private AircraftStateRepository repository;

    @Bean
    WebSocketHandlerAdapter socketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    private static ObjectMapper mapper = new ObjectMapper();

    private String convertToJson( AircraftState obj ) {
        try {
            return mapper.writeValueAsString( obj );
        }
        catch ( JsonProcessingException e ) {
            throw new RuntimeException( e );
        }
    }

    @Bean
    public WebSocketHandler getAllStatesHandler() {

        return session -> session.send( //
                repository.findAll().take( 500 ) //
                        .map( this::convertToJson ) //
                        .map( session::textMessage ) //
                        .onTerminateDetach() //
                        .doOnSubscribe( sub -> logger.info( session.getId() + " OPEN." ) ) )//
                .and( //
                        session.receive() //
                                .map( WebSocketMessage::getPayloadAsText ) //
                                .doOnNext( System.out::println ) //
                                .doFinally( sig -> logger.info( session.getId() + " CLOSE." ) //
                                ) //
                );
    }

    @Bean
    HandlerMapping simpleUrlHandlerMapping() {
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setUrlMap( Collections.singletonMap( "/ws/states", getAllStatesHandler() ) );
        simpleUrlHandlerMapping.setOrder( Ordered.HIGHEST_PRECEDENCE );
        return simpleUrlHandlerMapping;
    }

}
