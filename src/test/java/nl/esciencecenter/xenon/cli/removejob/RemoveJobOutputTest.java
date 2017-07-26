package nl.esciencecenter.xenon.cli.removejob;

import org.junit.Test;

import static org.junit.Assert.*;

public class RemoveJobOutputTest {
    @Test
    public void test_toString() throws Exception {
        RemoveJobOutput output = new RemoveJobOutput("/", "1");

        String result = output.toString();

        String expected = "Removed job with identifier '1' from location '/'";
        assertEquals(expected, result);
    }
}