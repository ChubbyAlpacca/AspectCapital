package aspectcapital.question2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class PriceHolderTest {

    private PriceHolder priceHolder;

    @BeforeEach
    void setUp() {
        priceHolder = new PriceHolder();
    }

    @Test
    void putPriceThenGetPriceReturnsLatestPrice() {
        priceHolder.putPrice("a", new BigDecimal("10"));
        assertEquals(new BigDecimal("10"), priceHolder.getPrice("a"));

        priceHolder.putPrice("a", new BigDecimal("12"));
        assertEquals(new BigDecimal("12"), priceHolder.getPrice("a"));
    }

    @Test
    void matchesSpecExampleSequence() {
        priceHolder.putPrice("a", new BigDecimal("10"));
        assertEquals(new BigDecimal("10"), priceHolder.getPrice("a"));

        priceHolder.putPrice("a", new BigDecimal("12"));
        assertTrue(priceHolder.hasPriceChanged("a"));

        priceHolder.putPrice("b", new BigDecimal("2"));
        priceHolder.putPrice("a", new BigDecimal("11"));

        assertEquals(new BigDecimal("11"), priceHolder.getPrice("a"));
        assertEquals(new BigDecimal("11"), priceHolder.getPrice("a"));
        assertEquals(new BigDecimal("2"), priceHolder.getPrice("b"));
    }

    @Test
    void hasPriceChangedIsFalseAfterGetPriceSeesSameValue() {
        priceHolder.putPrice("a", new BigDecimal("10"));
        priceHolder.getPrice("a");

        assertFalse(priceHolder.hasPriceChanged("a"));
    }

    @Test
    void hasPriceChangedIsTrueAfterNewPriceArrives() {
        priceHolder.putPrice("a", new BigDecimal("10"));
        priceHolder.getPrice("a");

        priceHolder.putPrice("a", new BigDecimal("15"));

        assertTrue(priceHolder.hasPriceChanged("a"));
    }

    @Test
    void differentEntitiesAreIndependent() {
        priceHolder.putPrice("a", new BigDecimal("100"));
        priceHolder.putPrice("b", new BigDecimal("200"));

        assertEquals(new BigDecimal("100"), priceHolder.getPrice("a"));
        assertEquals(new BigDecimal("200"), priceHolder.getPrice("b"));
    }

    @Test
    void getPriceThrowsForUnknownEntity() {
        assertThrows(NullPointerException.class, () -> priceHolder.getPrice("unknown"));
    }

    @Test
    void hasPriceChangedThrowsForUnknownEntity() {
        assertThrows(NullPointerException.class, () -> priceHolder.hasPriceChanged("unknown"));
    }

    // ---------- waitForNextPrice tests ----------

    @Test
    void waitForNextPriceBlocksUntilNewPriceArrives() throws Exception {

        priceHolder.putPrice("a", new BigDecimal("10"));

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Future<BigDecimal> future = executor.submit(() -> priceHolder.waitForNextPrice("a"));

            // It should still be waiting because no new price has arrived.
            assertFalse(future.isDone(), "waitForNextPrice should block until a new price arrives");

            // Give the waiting thread a little time to enter wait().
            Thread.sleep(100);

            assertFalse(future.isDone(), "waitForNextPrice returned before price update");

            // Now publish a new price.
            priceHolder.putPrice("a", new BigDecimal("15"));

            // The waiting thread should now be released.
            assertEquals(new BigDecimal("15"), future.get(1, TimeUnit.SECONDS));

        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void waitForNextPriceReturnsNewPriceNotOldPrice() throws Exception {

        priceHolder.putPrice("a", new BigDecimal("10"));

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Future<BigDecimal> future = executor.submit(() -> priceHolder.waitForNextPrice("a"));

            // Make sure the waiter has had a chance to start.
            Thread.sleep(100);

            priceHolder.putPrice("a", new BigDecimal("25"));

            assertEquals(new BigDecimal("25"), future.get(1, TimeUnit.SECONDS));

        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void waitForNextPriceThrowsForUnknownEntity() {
        assertThrows(NullPointerException.class, () -> priceHolder.waitForNextPrice("unknown"));
    }

    @Test
    void waitForNextPriceCanBeInterrupted() throws Exception {

        priceHolder.putPrice("a", new BigDecimal("10"));

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Future<BigDecimal> future = executor.submit(() -> priceHolder.waitForNextPrice("a"));

            Thread.sleep(100);

            future.cancel(true);

            assertThrows(CancellationException.class, future::get);

        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void multipleThreadsCanWaitForNextPrice() throws Exception {

        priceHolder.putPrice("a", new BigDecimal("10"));

        int threadCount = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            List<Future<BigDecimal>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> priceHolder.waitForNextPrice("a")));
            }

            // Give all threads time to reach waitForNextPrice().
            Thread.sleep(200);

            // All threads should still be waiting.
            for (Future<BigDecimal> future : futures) {
                assertFalse(future.isDone(), "Thread returned before price update");
            }

            // Publish the new price.
            priceHolder.putPrice("a", new BigDecimal("20"));

            // All waiting threads should receive the new price.
            for (Future<BigDecimal> future : futures) {
                assertEquals(new BigDecimal("20"), future.get(1, TimeUnit.SECONDS));
            }

        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void waitForNextPriceWaitsForEachNewUpdate() throws Exception {

        priceHolder.putPrice("a", new BigDecimal("10"));

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Future<BigDecimal> first = executor.submit(() -> priceHolder.waitForNextPrice("a"));

            Thread.sleep(100);

            priceHolder.putPrice("a", new BigDecimal("15"));

            assertEquals(new BigDecimal("15"), first.get(1, TimeUnit.SECONDS));

            // Start waiting for another price.
            Future<BigDecimal> second = executor.submit(() -> priceHolder.waitForNextPrice("a"));

            Thread.sleep(100);

            assertFalse(second.isDone());

            priceHolder.putPrice("a", new BigDecimal("20"));

            assertEquals(new BigDecimal("20"), second.get(1, TimeUnit.SECONDS));

        } finally {
            executor.shutdownNow();
        }
    }

    // ---------- Multithreaded tests ----------

    @Test
    void concurrentPutsForSameEntityDoNotCorruptState() throws InterruptedException {

        int threadCount = 20;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int value = i;

            executor.submit(() -> {
                try {
                    priceHolder.putPrice("a", new BigDecimal(value));
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));

        executor.shutdown();

        BigDecimal result = priceHolder.getPrice("a");

        assertNotNull(result);

        assertTrue(result.intValue() >= 0 && result.intValue() < threadCount);
    }

    @Test
    void concurrentPutsForDifferentEntitiesDoNotInterfere() throws InterruptedException {

        int entityCount = 50;

        ExecutorService executor = Executors.newFixedThreadPool(10);

        CountDownLatch latch = new CountDownLatch(entityCount);

        for (int i = 0; i < entityCount; i++) {
            String entity = "entity-" + i;
            BigDecimal price = new BigDecimal(i);

            executor.submit(() -> {
                try {
                    priceHolder.putPrice(entity, price);
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));

        executor.shutdown();

        for (int i = 0; i < entityCount; i++) {
            assertEquals(new BigDecimal(i), priceHolder.getPrice("entity-" + i));
        }
    }

    @Test
    void concurrentGetPriceNeverThrowsOrReturnsNullOnceEntityExists() throws InterruptedException {

        priceHolder.putPrice("a", new BigDecimal("1"));

        int threadCount = 30;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger failures = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    BigDecimal price = priceHolder.getPrice("a");

                    if (price == null) {
                        failures.incrementAndGet();
                    }

                } catch (Exception e) {
                    failures.incrementAndGet();

                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));

        executor.shutdown();

        assertEquals(0, failures.get());
    }

    @Test
    void concurrentMixedPutsAndGetsDoNotThrow() throws InterruptedException {

        priceHolder.putPrice("a", new BigDecimal("0"));

        int threadCount = 40;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger failures = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int value = i;

            executor.submit(() -> {
                try {
                    if (value % 2 == 0) {
                        priceHolder.putPrice("a", new BigDecimal(value));
                    } else {
                        priceHolder.getPrice("a");
                        priceHolder.hasPriceChanged("a");
                    }

                } catch (Exception e) {
                    failures.incrementAndGet();

                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));

        executor.shutdown();

        assertEquals(0, failures.get());
    }
}
