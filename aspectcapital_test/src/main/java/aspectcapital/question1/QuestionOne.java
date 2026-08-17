package aspectcapital.question1;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Question 1 implementation - Please add your map method here
 */
public class QuestionOne {

    public static <T, R> List<R> map(Function<T, R> f, List<T> l) {
        Objects.requireNonNull(f, "Function must not be null.");
        Objects.requireNonNull(l, "List must not be null.");

        List<R> result = new ArrayList<>();
        l.forEach(item -> result.add(f.apply(item)));
        return result;
    }

}
