package pl.consileon.javips.reactivesky.model;

import java.time.Instant;

import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.Data;

@Data
public class AircraftState {

    private Instant fetchTime;

    /* Unique ICAO 24-bit address of the transponder in hex string representation. */
    @Indexed
    private String icao24;

    /* Callsign of the vehicle (8 chars). Can be null if no callsign has been received. */
    @Indexed
    private String callSign;

    /* Country name inferred from the ICAO 24-bit address. */
    @Indexed
    private String originCountry;

    /* Unix timestamp (seconds) for the last position update. Can be null if no position report was received by OpenSky within the past 15s. */
    private Instant timePosition;

    /* Unix timestamp (seconds) for the last update in general. This field is updated for any new, valid message received from the transponder. */
    private Instant lastContact;

    /* WGS-84 longitude, latitude in decimal degrees. Can be null. */
    @GeoSpatialIndexed(name = "index")
    private Point position;

    /* Geometric altitude in meters. Can be null. */
    private Float geoAltitude;

    /* Boolean value which indicates if the position was retrieved from a surface position report. */
    private boolean onGround;

    /* Velocity over ground in m/s. Can be null. */
    private Float velocity;

    /* True track in decimal degrees clockwise from north (north=0°). Can be null. */
    private Float trueTrack;

    /* Vertical rate in m/s. A positive value indicates that the airplane is climbing, a negative value indicates that it descends. Can be null. */
    private Float verticalRate;

    /* Barometric altitude in meters. Can be null. */
    private Float baroAltitude;

    /* The transponder code aka Squawk. Can be null. */
    private String squawk;

    /* Whether flight status indicates special purpose indicator. */
    private boolean spi;

    /* Origin of this state’s position: 0 = ADS-B, 1 = ASTERIX, 2 = MLAT */
    private Integer positionSource;
}
