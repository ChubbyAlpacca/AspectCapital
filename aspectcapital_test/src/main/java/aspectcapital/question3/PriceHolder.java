package aspectcapital.question3;

import java.math.BigDecimal;

/**
 * Question 3 implementation template
 */
public final class PriceHolder {

    public PriceHolder() {

    }

    /**
     * Called when a price ‘p’ is received for an entity ‘e’
     */
    public void putPrice(String e, BigDecimal p) {

    }

    /**
     * Called to get the latest price for entity ‘e’
     */
    public BigDecimal getPrice(String e) {

        return null;
    }

    /**
     * Called to determine if the price for entity ‘e’ has
     * changed since the last call to getPrice(e).
     */
    public boolean hasPriceChanged(String e) {

        return false;
    }


    /**
     * Returns the next price for entity ‘e’. If the price has changed since the last
     * call to getPrice() or waitForNextPrice(), it returns immediately that price.
     * Otherwise it blocks until the next price change for entity ‘e’.
     */
    public BigDecimal waitForNextPrice(String e) throws InterruptedException {

        //copied to Question 2 priceHolder, as requested on HackerRank.

        return null;
    }

}
