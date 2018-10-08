package pl.consileon.javips.reactivesky.openskyapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import pl.consileon.javips.reactivesky.model.AircraftState;
import pl.consileon.javips.reactivesky.openskyapi.OpenSkyStateEntry.StateField;
import reactor.core.publisher.Flux;

@Service
public class OpenSkyService {

    private final String OPENSKY_BASE_URL = "https://opensky-network.org/api";

    Logger logger = LoggerFactory.getLogger( OpenSkyService.class );

    public Flux<AircraftState> retrieveStates() {
        logger.debug( "Calling OpenSky API..." );

        WebClient webClient = WebClient.builder().baseUrl( OPENSKY_BASE_URL ).defaultHeader( HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE ).build();

        ResponseSpec responseSpec = webClient.get().uri( "/states/all" ).retrieve();

        Flux<AircraftState> entitiesStream = responseSpec.bodyToMono( OpenSkyResponse.class ) // Mono<List<String[]>>
                .map( OpenSkyResponse::getStates ) // Mono<List<String[]>> 
                .flatMapMany( Flux::fromIterable ) // Flux<String[]>
                .map( OpenSkyStateEntry::new ) // Flux<OpenSkyStateEntry>
                .map( this::convertToEntity );

        return entitiesStream;
    }

    private AircraftState convertToEntity( OpenSkyStateEntry stateEntry ) {

        AircraftState entity = new AircraftState();

        entity.setIcao24( stateEntry.getFieldValue( StateField.ICAO24 ) );
        entity.setCallSign( stateEntry.getFieldValue( StateField.CALLSIGN ) );
        entity.setOriginCountry( stateEntry.getFieldValue( StateField.ORIGIN_COUNTRY ) );
        
        entity.setTimePosition( stateEntry.getFieldValueAsInstant( StateField.TIME_POSITION ) );
        entity.setLastContact( stateEntry.getFieldValueAsInstant( StateField.LAST_CONTACT ) );

        Float lat = stateEntry.getFieldValueAsFloat( StateField.LATITUDE );
        Float lon = stateEntry.getFieldValueAsFloat( StateField.LONGITUDE );
        Point position = lat != null && lon != null ? new Point( lon, lat ) : null;
        entity.setPosition( position );

        entity.setGeoAltitude( stateEntry.getFieldValueAsFloat( StateField.GEO_ALTITUDE ) );
        entity.setOnGround( stateEntry.getFieldValueAsBoolean( StateField.ON_GROUND ) );
        entity.setVelocity( stateEntry.getFieldValueAsFloat( StateField.VELOCITY ) );
        entity.setTrueTrack( stateEntry.getFieldValueAsFloat( StateField.TRUE_TRACK ) );
        entity.setVerticalRate( stateEntry.getFieldValueAsFloat( StateField.VERTICAL_RATE ) );
        entity.setBaroAltitude( stateEntry.getFieldValueAsFloat( StateField.BARO_ALTITUDE ) );
        entity.setSquawk( stateEntry.getFieldValue( StateField.SQUAWK ) );
        entity.setSpi( stateEntry.getFieldValueAsBoolean( StateField.SPI ) );
        entity.setPositionSource( stateEntry.getFieldValueAsInt( StateField.POSITION_SOURCE ) );

        return entity;
    }
}
