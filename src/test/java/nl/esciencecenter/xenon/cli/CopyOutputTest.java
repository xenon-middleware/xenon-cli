package nl.esciencecenter.xenon.cli;

import org.junit.Test;

import static org.junit.Assert.*;

public class CopyOutputTest {
    @Test
    public void test_toString() throws Exception {
        CopyInput source = new CopyInput("local", "/", "/source", null);
        CopyInput target = new CopyInput("sftp", "localhost", "/target", null);
        String output = new CopyOutput(source, target).toString();

        String expected = "Copied '/source' from location '/' to  '/target' to location 'localhost'";
        assertEquals(expected, output);
    }

}