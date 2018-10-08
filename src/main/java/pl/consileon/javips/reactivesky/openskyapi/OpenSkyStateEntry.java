package pl.consileon.javips.reactivesky.openskyapi;

import java.time.Instant;

public class OpenSkyStateEntry {

    public enum StateField {
        ICAO24, //
        CALLSIGN, //
        ORIGIN_COUNTRY, //
        TIME_POSITION, //
        LAST_CONTACT, //
        LONGITUDE, //
        LATITUDE, //
        GEO_ALTITUDE, //
        ON_GROUND, //
        VELOCITY, //
        TRUE_TRACK, // 
        VERTICAL_RATE, //
        SENSORS, //
        BARO_ALTITUDE, //
        SQUAWK, //
        SPI, //
        POSITION_SOURCE //
    }

    private String[] record;

    public OpenSkyStateEntry( String[] record ) {
        this.record = record;
    }

    public String getFieldValue( StateField stateField ) {
        String value = record[stateField.ordinal()];
        return value;
    }

    public Float getFieldValueAsFloat( StateField stateField ) {
        String valAsStr = getFieldValue( stateField );
        return valAsStr != null ? Float.valueOf( valAsStr ) : null;
    }

    public Integer getFieldValueAsInt( StateField stateField ) {
        String valAsStr = getFieldValue( stateField );
        return valAsStr != null ? Integer.valueOf( valAsStr ) : null;
    }

    public Instant getFieldValueAsInstant( StateField stateField ) {
        String valAsStr = getFieldValue( stateField );
        return valAsStr != null ? Instant.ofEpochSecond( Long.valueOf( valAsStr ) ) : null;
    }

    public boolean getFieldValueAsBoolean( StateField stateField ) {
        return Boolean.valueOf( getFieldValue( stateField ) );
    }

}