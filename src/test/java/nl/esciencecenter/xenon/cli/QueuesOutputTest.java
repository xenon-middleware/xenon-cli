package nl.esciencecenter.xenon.cli;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QueuesOutputTest {
    @Test
    public void toString_empty() {
        QueuesOutput queues = new QueuesOutput(new String[]{}, null);

        String result = queues.toString();
        String expected = "Available queues: ";
        assertEquals(expected, result);
    }

    @Test
    public void toString_filled() {
        QueuesOutput queues = new QueuesOutput(new String[]{"quick", "default"}, "default");

        String result = queues.toString();
        String sep = System.getProperty("line.separator");
        String expected = "Available queues: quick, default" + sep + "Default queue: default";
        assertEquals(expected, result);
    }

    @Test
    public void test_hashCode() {
        QueuesOutput queues = new QueuesOutput(new String[]{"quick", "default"}, "default");

        assertEquals(queues.hashCode(), queues.hashCode());
    }
}