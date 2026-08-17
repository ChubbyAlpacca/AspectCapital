# Question 3 — `waitForNextPrice`

> Extend `PriceHolder` to provide a method that waits for a price change.
> Copy your answer to question two, add this new method and make any
> necessary modifications to your existing code.
>
> ```java
> /**
>  * Returns the next price for entity 'e'. If the price has changed since
>  * the last call to getPrice() or waitForNextPrice(), it returns
>  * immediately that price.
>  * Otherwise it blocks until the next price change for entity 'e'.
>  */
> public BigDecimal waitForNextPrice(String e) throws InterruptedException;
> ```
