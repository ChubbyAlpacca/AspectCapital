# Question 2 — `PriceHolder`


> You are working on a system, which receives prices from an external
> component at a frequent rate. The system needs to use the latest price
> received when required. You are tasked with implementing the component
> that will provide the latest price for an entity.
>
> The external system produces prices for several entities, interleaved
> into a single sequence of the form:
>
> ```
> EntityA: p_a1,  p_a2,  p_a3...
> EntityB: p_b1,  p_b2,  p_b3...
> Time ▶ ▶ ▶
> ```
>
> For example, if during the time taken to process price `p_a1` the prices
> `p_a2`, `p_a3` and `p_a4` arrive, then the next price the application
> should process is `p_a4` and all previous prices should be ignored.
> Prices for other entities (e.g. `p_bi`) do not affect the latest price
> for entity a, but are processed independently in the same way with
> respect to entity b.
>
> Below is the skeleton code for a component that manages prices in this
> manner. The component needs to be thread-safe (i.e. work without error
> with concurrent access to update and get prices for the same or different
> entities, since prices may be updated and accessed by multiple different
> threads). Complete the code detailed below, implementing the constructor
> and three methods which are documented.
>
> The following example shows how the component may be used (although
> single threaded) and the expected output.
>
> | Example | Output |
> |---|---|
> | `ph.putPrice("a", new BigDecimal(10));`<br>`System.out.println(ph.getPrice("a"));` | `10` |
> | `ph.putPrice("a", new BigDecimal(12));`<br>`System.out.println(ph.hasPriceChanged("a"));` | `true` |
> | `ph.putPrice("b", new BigDecimal(2));`<br>`ph.putPrice("a", new BigDecimal(11));`<br>`System.out.println(ph.getPrice("a"));` | `11` |
> | `System.out.println(ph.getPrice("a"));` | `11` |
> | `System.out.println(ph.getPrice("b"));` | `2` |
>
> The following outline is provided below:
>
> ```java
> public final class PriceHolder {
>     // TODO Write this bit
>
>     public PriceHolder() {
>         // TODO Write this bit
>     }
>
>     /** Called when a price 'p' is received for an entity 'e' */
>     public void putPrice(String e, BigDecimal p) {
>         // TODO Write this bit
>     }
>
>     /** Called to get the latest price for entity 'e' */
>     public BigDecimal getPrice(String e) {
>         // TODO Write this bit
>     }
>
>     /**
>      * Called to determine if the price for entity 'e' has
>      * changed since the last call to getPrice(e).
>      */
>     public boolean hasPriceChanged(String e) {
>         // TODO Write this bit
>     }
> }
> ```