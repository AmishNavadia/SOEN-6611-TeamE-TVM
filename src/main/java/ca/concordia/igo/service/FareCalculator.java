package ca.concordia.igo.service;

import ca.concordia.igo.model.Fare;
import ca.concordia.igo.model.Zone;

/**
 * Service for calculating transit fares based on zones and trip types.
 * <p>
 * Fare calculation uses a distance-based pricing model:
 * - Base fare starts at $3.25
 * - Additional charge per zone traveled (15% multiplier)
 * - Return trips are discounted (1.8x instead of 2x single fare)
 * - Day passes have a flat rate regardless of zones
 * - Monthly passes have a flat rate for unlimited travel
 * </p>
 * This is a stateless service - thread-safe and can be reused.
 */
public class FareCalculator {

    private static final double BASE_FARE = 3.25;
    private static final double ZONE_MULTIPLIER = 1.15;  // 15% per zone

    /**
     * Calculate the fare amount for a given trip.
     * <p>
     * Calculation logic:
     * - SINGLE: Base fare and zone distance charges
     * - RETURN: Single fare * 1.8 (10% discount vs. buying two singles)
     * - DAY_PASS: Flat $13.50 regardless of zones
     * - MONTHLY_PASS: Flat $150.00 for unlimited monthly travel
     * </p>
     *
     * @param fare the fare details including origin, destination, and trip type
     * @return calculated fare amount in dollars
     */
    public double calculateAmount(Fare fare) {
        int zoneDistance = calculateZoneDistance(fare.origin(), fare.destination());
        double baseFare = BASE_FARE + (zoneDistance * BASE_FARE * ZONE_MULTIPLIER);

        switch (fare.tripType()) {
            case SINGLE:
                return baseFare;
            case RETURN:
                return baseFare * 1.8; // 10% discount for return
            case DAY_PASS:
                return 13.50;  // Flat rate for unlimited daily travel
            case MONTHLY_PASS:
                return 150.00; // Flat rate for monthly pass
            default:
                return baseFare;
        }
    }

    /**
     * Calculates the number of zones traveled.
     * <p>
     * Zone distance is the absolute difference between zone numbers.
     * For example, Z1 to Z3 = 2 zones, Z3 to Z1 = 2 zones.
     * </p>
     * Assumes zone IDs are in format "Z#" where # is the zone number.
     *
     * @param origin starting zone
     * @param destination ending zone
     * @return number of zones traveled (0 if same zone)
     */
    private int calculateZoneDistance(Zone origin, Zone destination) {
        int originNum = Integer.parseInt(origin.zoneId().substring(1));
        int destNum = Integer.parseInt(destination.zoneId().substring(1));
        return Math.abs(destNum - originNum);
    }
}
