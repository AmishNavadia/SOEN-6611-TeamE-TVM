package ca.concordia.igo.service;

import ca.concordia.igo.model.Fare;
import ca.concordia.igo.model.Zone;

/**
 * Service for calculating transit fares.
 */
public class FareCalculator {
    private static final double BASE_FARE = 3.25;
    private static final double ZONE_MULTIPLIER = 1.15;

    /**
     * Calculate the fare amount based on fare details.
     */
    public double calculateAmount(Fare fare) {
        int zoneDistance = calculateZoneDistance(fare.origin(), fare.destination());
        double baseFare = BASE_FARE + (zoneDistance * BASE_FARE * ZONE_MULTIPLIER);

        switch (fare.tripType()) {
            case SINGLE:
                return baseFare;
            case RETURN:
                return baseFare * 1.8;
            case DAY_PASS:
                return 13.50;
            default:
                return baseFare;
        }
    }

    private int calculateZoneDistance(Zone origin, Zone destination) {
        int originNum = Integer.parseInt(origin.zoneId().substring(1));
        int destNum = Integer.parseInt(destination.zoneId().substring(1));
        return Math.abs(destNum - originNum);
    }
}
