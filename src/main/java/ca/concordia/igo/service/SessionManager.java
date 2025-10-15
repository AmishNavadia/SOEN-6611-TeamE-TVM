package ca.concordia.igo.service;

import ca.concordia.igo.util.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;


/**
 * Manages user session timeouts for the kiosk interface.
 * <p>
 * Kiosks need to automatically return to the home screen after
 * a period of inactivity to prevent one user from blocking the
 * machine and to protect privacy (clear any displayed info).
 * </p>
 *
 * Usage pattern:
 * 1. Call startSession() when user begins interaction
 * 2. Call resetTimer() on any user input to extend session
 * 3. The Session automatically ends after 60 seconds of inactivity
 * 4. Call endSession() to manually end the session early
 * The caller provides the timeout action (e.g., return to home screen) * as a Runnable.
 */
public class SessionManager {
    private static final int TIMEOUT_SECONDS = 300; // Inactivity timeout
    private Timeline timeoutTimer;
    private Runnable onTimeout;

    /**
     * Start a new user session with a timeout handler.
     * <p>
     * The timeout action is called when the session expires due to
     * inactivity. Typically, this would:
     * - Return to home screen
     * - Clear any displayed personal information
     * - Cancel any pending transactions
     * </p>
     * @param timeoutAction what to do when session times out
     */
    public void startSession(Runnable timeoutAction) {
        // LOG 9: Session lifecycle
        Logger.info("User session started - Timeout: " + TIMEOUT_SECONDS + " seconds");
        this.onTimeout = timeoutAction;
        resetTimer();
    }

    /**
     * Reset the inactivity timer.
     * <p>
     * Call this on any user interaction (button press, card tap, etc.)
     * to extend the session and prevent timeout.
     * </p>
     * If called multiple times rapidly (user actively using the kiosk),
     * the timer keeps resetting and the session stays alive.
     */
    public void resetTimer() {
        Logger.debug("Session timer reset - extending session");
        if (timeoutTimer != null) timeoutTimer.stop();
        timeoutTimer = new Timeline(new KeyFrame(
                Duration.seconds(TIMEOUT_SECONDS),
                e -> {
                    Logger.warn("Session timeout occurred - returning to main menu");
                    onTimeout.run();
                }
        ));
        timeoutTimer.play();
    }

    /**
     * End the current session and stop the timeout timer.
     * <p>
     * Call this when:
     * - User explicitly ends their session (clicks "Done")
     * - Transaction is completed
     * - User cancels operation
     * </p>
     * Prevents the timeout action from firing after the session
     * has already been properly concluded.
     */
    public void endSession() {
        Logger.info("User session ended");
        if (timeoutTimer != null) timeoutTimer.stop();
    }
}
