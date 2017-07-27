package nl.esciencecenter.xenon.cli.rename;

import static org.junit.Assert.assertEquals;

import nl.esciencecenter.xenon.filesystems.Path;

import org.junit.Test;

public class RenameOutputTest {
    @Test
    public void test_toString() throws Exception {
        RenameOutput obj = new RenameOutput("file", new Path("/some/source"), new Path("/some/target"));

        String out = obj.toString();

        String expected = "Renamed '/some/source' to '/some/target' on location 'file'";
        assertEquals(expected, out);
    }
}