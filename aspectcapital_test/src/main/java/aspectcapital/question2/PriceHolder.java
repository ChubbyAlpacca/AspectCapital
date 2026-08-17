package aspectcapital.question2;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Question 2 implementation template
 */
public final class PriceHolder {

    public PriceHolder() {
    }

    ConcurrentHashMap<String, PriceRecord> priceMap = new ConcurrentHashMap<>();


    /**
     * Called when a price ‘p’ is received for an entity ‘e’
     */
    public void putPrice(String e, BigDecimal p) {
        System.out.println("inside putPrice");
        PriceRecord priceRecord = priceMap.get(e);
        if (priceRecord == null) {
            priceMap.put(e, new PriceRecord(null, p));
        } else {
            priceRecord.updatePrice(p);
        }
    }

    /**
     * Called to get the latest price for entity ‘e’
     */
    public BigDecimal getPrice(String e) {
        System.out.println("inside getPrice");

        PriceRecord priceRecord = priceMap.get(e);
        Objects.requireNonNull(priceRecord, "Price record must not be null.");
        return priceRecord.readPrice();
    }

    /**
     * Called to determine if the price for entity ‘e’ has
     * changed since the last call to getPrice(e).
     */
    public boolean hasPriceChanged(String e) {
        System.out.println("inside hasPriceChanged");

        PriceRecord priceRecord = priceMap.get(e);
        Objects.requireNonNull(priceRecord, "Price record must not be null.");
        return priceRecord.hasChanged();
    }

    public BigDecimal waitForNextPrice(String e) throws InterruptedException {
        System.out.println("inside waitForNextPrice");

        PriceRecord priceRecord = priceMap.get(e);
        Objects.requireNonNull(priceRecord, "Price record must not be null.");
        return priceRecord.waitForUpdate(priceRecord.getUpdateVersion());
    }
}
