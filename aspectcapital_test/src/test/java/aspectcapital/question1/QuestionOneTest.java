package aspectcapital.question1;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Question 1 usage - Please provide example usage of your map method here
 */
public class QuestionOneTest {


    @Test
    public void examplePlusOneUsingMapFunction() throws Exception {
        List<Integer> input = List.of(1, 2, 3);
        List<Integer> result = QuestionOne.map(x -> x + 1, input);
        assertEquals(List.of(2, 3, 4), result);
        assertEquals(List.of(1, 2, 3), input);
    }

    @Test
    public void worksWithTypeChangingFunction() {
        List<Integer> input = List.of(1, 2, 3);
        List<String> result = QuestionOne.map(x -> "val:" + x, input);
        assertEquals(List.of("val:1", "val:2", "val:3"), result);
    }

    @Test
    public void handlesEmptyList() {
        List<Integer> input = List.of();
        List<Integer> result = QuestionOne.map(x -> x + 1, input);
        assertTrue(result.isEmpty());
        assertEquals(input, Collections.emptyList());
    }

    @Test
    public void doesNotMutateInputList() {
        List<Integer> input = List.of(1, 2, 3);
        QuestionOne.map(x -> x + 1, input);
        assertEquals(List.of(1, 2, 3), input);
    }

    @Test
    public void throwsWhenFunctionIsNull() {
        try {
            QuestionOne.map(null, List.of(1, 2, 3));
            fail("Expected NullPointerException was not thrown");
        } catch (NullPointerException e) {
            assertEquals("Function must not be null.", e.getMessage());
        }
    }

    @Test
    public void throwsWhenListIsNull() {
        try {
            QuestionOne.map(x -> x + 1, (List<Integer>) null);
            fail("Expected NullPointerException was not thrown");
        } catch (NullPointerException e) {
            assertEquals("List must not be null.", e.getMessage());
        }
    }

}