package nl.esciencecenter.xenon.cli;

import static nl.esciencecenter.xenon.cli.Utils.getAbsolutePath;
import static org.junit.Assert.assertEquals;

import nl.esciencecenter.xenon.filesystems.Path;

import org.junit.Test;

public class UtilsTest {
    @Test(expected = IllegalAccessError.class)
    public void constructor() {
        new Utils();
    }

    @Test
    public void getAbsolutePath_remote() throws Exception {
        Path result = getAbsolutePath("sftp", "/foo");

        Path expected = new Path("/foo");
        assertEquals(expected, result);
    }

    @Test
    public void getAbsolutePath_localAbsolute() throws Exception {
        Path result = getAbsolutePath("local", "/foo");

        Path expected = new Path("/foo");
        assertEquals(expected, result);
    }

    @Test
    public void getAbsolutePath_fileAbsolute() throws Exception {
        Path result = getAbsolutePath("file", "/foo");

        Path expected = new Path("/foo");
        assertEquals(expected, result);
    }

    @Test
    public void getAbsolutePath_fileTilde() throws Exception {
        Path result = getAbsolutePath("file", "~/foo");

        Path expected = new Path("~/foo");
        assertEquals(expected, result);
    }

    @Test
    public void getAbsolutePath_filPipe() throws Exception {
        Path result = getAbsolutePath("file", "-");

        Path expected = new Path("-");
        assertEquals(expected, result);
    }

    @Test
    public void getAbsolutePath_localRelative() throws Exception {
        Path result = getAbsolutePath("local", "foo");

        Path expected = new Path(System.getProperty("user.dir") + "/foo");
        assertEquals(expected, result);
    }

    @Test
    public void getAbsolutePath_fileRelative() throws Exception {
        Path result = getAbsolutePath("file", "foo");

        Path expected = new Path(System.getProperty("user.dir") + "/foo");
        assertEquals(expected, result);
    }
}