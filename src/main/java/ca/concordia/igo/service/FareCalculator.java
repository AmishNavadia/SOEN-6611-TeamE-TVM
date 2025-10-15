package ca.concordia.igo.service;

import ca.concordia.igo.model.Fare;
import ca.concordia.igo.model.Zone;
import ca.concordia.igo.util.Logger;

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
        // LOG 4: Fare calculation
        Logger.info("Fare calculation started - " + fare.toString());

        int zoneDistance = calculateZoneDistance(fare.origin(), fare.destination());
        double baseFare = BASE_FARE + (zoneDistance * BASE_FARE * ZONE_MULTIPLIER);

        Logger.debug("Base fare calculated - Zones: " + zoneDistance +
                ", Base: $" + baseFare);
        double finalAmount;
        switch (fare.tripType()) {
            case SINGLE:
                finalAmount = baseFare;
                Logger.debug("Single trip fare: $" + finalAmount);
                break;
            case RETURN:
                finalAmount = baseFare * 1.8;
                Logger.debug("Return trip fare (10% discount): $" + finalAmount);
                break;
            case DAY_PASS:
                finalAmount = 13.50;
                Logger.debug("Day pass flat rate: $" + finalAmount);
                break;
            case MONTHLY_PASS:
                finalAmount = 150.00;
                Logger.debug("Monthly pass flat rate: $" + finalAmount);
                break;
            default:
                finalAmount = baseFare;
                Logger.debug("Default fare: $" + finalAmount);
        }

        Logger.info("Fare calculation completed - " + fare.toString() +
                " = $" + finalAmount);
        return finalAmount;
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
