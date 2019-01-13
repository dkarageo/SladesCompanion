package com.dkarageo.sladescompanion.vehicles.simulator;

import com.dkarageo.sladescompanion.units.Location;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ThreadLocalRandom;

public class LocationGenerator {

    public static final int VALIDITY_INTERVAL = 1000;  // 1 sec
    public static final double LATITUDE_BASE = 40.61;
    public static final double LONGITUDE_BASE = 22.92;
    public static final double ALTITUDE_BASE = 50.0;

    public static Location generateNextLocation(Location location) {
        Location newLoc = new Location();

        Date curTimestamp = GregorianCalendar.getInstance().getTime();

        newLoc.setTimestamp(curTimestamp);

        Calendar c = GregorianCalendar.getInstance();
        c.setTime(curTimestamp);
        c.add(Calendar.MILLISECOND, VALIDITY_INTERVAL);

        newLoc.setValidFrom(curTimestamp);
        newLoc.setValidTo(c.getTime());
        newLoc.setLatitude(generateNextLatitude(location));
        newLoc.setLongitude(generateNextLongitude(location));
        newLoc.setAltitude(generateNextAltitude(location));
        newLoc.setConfidence(1.0f);

        return newLoc;
    }

    private static double generateNextLatitude(Location location) {
        double latitude;

        if (location != null) latitude = location.getLatitude();
        else latitude = LATITUDE_BASE;

        latitude += ThreadLocalRandom.current().nextDouble(0, 0.000010);

        return latitude;
    }

    private static double generateNextLongitude(Location location) {
        double longitude;

        if (location != null) longitude = location.getLongitude();
        else longitude = LONGITUDE_BASE;

        longitude += ThreadLocalRandom.current().nextDouble(0, 0.000010);

        return longitude;
    }

    private static double generateNextAltitude(Location location) {
        double altitude;

        if (location != null) altitude = location.getAltitude();
        else altitude = ALTITUDE_BASE;

        altitude += ThreadLocalRandom.current().nextDouble(-0.5, 0.5);

        return altitude;
    }
}
