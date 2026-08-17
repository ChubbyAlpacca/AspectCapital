package aspectcapital.question2;

import java.math.BigDecimal;
import java.util.Objects;

public class PriceRecord {

    private volatile BigDecimal currentPrice;
    private volatile BigDecimal lastSeenPrice;
    private volatile int updateVersion;


    public int getUpdateVersion() {
        return updateVersion;
    }

    public synchronized void incrementUpdateVersion() {
        updateVersion++;
    }

    public PriceRecord(BigDecimal lastSeenPrice, BigDecimal currentPrice) {
        this.lastSeenPrice = lastSeenPrice;
        this.currentPrice = currentPrice;
    }

    public synchronized void updatePrice(BigDecimal price) {
        this.currentPrice = price;
        incrementUpdateVersion();
        notifyAll();
    }

    public synchronized BigDecimal readPrice() {
        this.lastSeenPrice = this.currentPrice;
        return this.currentPrice;
    }

    public synchronized boolean hasChanged() {
        return !Objects.equals(currentPrice, lastSeenPrice);
    }

    public synchronized BigDecimal waitForUpdate(int knownVersion) throws InterruptedException {
        while (updateVersion == knownVersion) {
            wait();
        }
        lastSeenPrice = currentPrice;
        return currentPrice;
    }
}
