package ca.concordia.igo.service;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionManagerTest {

    @BeforeAll
    static void initFxToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException alreadyStarted) {
            latch.countDown();
        }
        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    void startSessionExecutesTimeoutAction() throws Exception {
        SessionManager manager = new SessionManager(1);
        CountDownLatch latch = new CountDownLatch(1);

        manager.startSession(latch::countDown);

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        manager.endSession();
    }

    @Test
    void resetTimerExtendsSessionBeforeTimeout() throws Exception {
        SessionManager manager = new SessionManager(1);
        CountDownLatch latch = new CountDownLatch(1);

        manager.startSession(latch::countDown);
        Thread.sleep(600);
        manager.resetTimer();

        assertFalse(latch.await(600, TimeUnit.MILLISECONDS));
        assertTrue(latch.await(2, TimeUnit.SECONDS));
        manager.endSession();
    }
}
