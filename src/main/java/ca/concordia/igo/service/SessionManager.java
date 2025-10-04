package ca.concordia.igo.service;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class SessionManager {
    private static final int TIMEOUT_SECONDS = 30;
    private Timeline timeoutTimer;
    private Runnable onTimeout;

    public void startSession(Runnable timeoutAction) {
        this.onTimeout = timeoutAction;
        resetTimer();
    }

    public void resetTimer() {
        if (timeoutTimer != null) timeoutTimer.stop();
        timeoutTimer = new Timeline(new KeyFrame(
                Duration.seconds(TIMEOUT_SECONDS),
                e -> onTimeout.run()
        ));
        timeoutTimer.play();
    }

    public void endSession() {
        if (timeoutTimer != null) timeoutTimer.stop();
    }
}
