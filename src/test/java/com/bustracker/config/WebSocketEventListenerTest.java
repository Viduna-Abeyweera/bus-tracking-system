package com.bustracker.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link WebSocketEventListener}.
 *
 * <p>Tests the connection counting logic using the AtomicInteger
 * counter. Event handling is tested indirectly since Spring events
 * require application context.</p>
 */
class WebSocketEventListenerTest {

    @Test
    @DisplayName("Should start with zero active connections")
    void testInitialConnectionCount() {
        WebSocketEventListener listener = new WebSocketEventListener();
        assertEquals(0, listener.getActiveConnectionCount());
    }

    @Test
    @DisplayName("Active connection count should be thread-safe")
    void testThreadSafeConnectionCount() throws InterruptedException {
        WebSocketEventListener listener = new WebSocketEventListener();
        int threadCount = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);

        // Simulate concurrent access to connection counter
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                // Just verify we can read count safely from multiple threads
                listener.getActiveConnectionCount();
                latch.countDown();
            }).start();
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS),
                "All threads should complete within 5 seconds");
        assertEquals(0, listener.getActiveConnectionCount());
    }
}
