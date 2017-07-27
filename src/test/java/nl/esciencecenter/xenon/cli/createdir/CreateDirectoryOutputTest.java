package nl.esciencecenter.xenon.cli.createdir;

import static org.junit.Assert.assertEquals;

import nl.esciencecenter.xenon.filesystems.Path;

import org.junit.Test;

public class CreateDirectoryOutputTest {
    @Test
    public void test_toString() throws Exception {
        CreateDirectoryOutput obj = new CreateDirectoryOutput("file", new Path("/some/path"));

        String out = obj.toString();

        String expected = "Created directory '/some/path' in location 'file'";
        assertEquals(expected, out);
    }

}