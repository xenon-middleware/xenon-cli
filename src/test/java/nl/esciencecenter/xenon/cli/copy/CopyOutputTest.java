package nl.esciencecenter.xenon.cli.copy;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.DefaultCredential;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CopyOutputTest {
    private CopyOutput copyOutput;

    @Before
    public void setUp() throws Exception {
        CopyInput source = new CopyInput("file", "/", "/source", new DefaultCredential());
        CopyInput target = new CopyInput("file", "/", "/target", new DefaultCredential());
        copyOutput = new CopyOutput(source, target, 100L);
    }

    @After
    public void tearDown() throws XenonException {
        copyOutput.getSource().getFileSystem().close();
        copyOutput.getTarget().getFileSystem().close();
    }

    @Test
    public void test_toString() throws Exception {
        String output = copyOutput.toString();

        String expected = "Copied '/source' from location '/' to '/target' at location '/', 100 bytes copied";
        assertEquals(expected, output);
    }

    @Test
    public void toJson() {
        Gson gson = new GsonBuilder().create();
        String result = gson.toJson(copyOutput);
        String currentUser = new DefaultCredential().getUsername();
        String expected = "{\"target\":{\"adaptor\":\"file\",\"location\":\"/\",\"path\":\"/target\",\"credential\":{\"username\":\"" + currentUser + "\"},\"stream\":false},\"source\":{\"adaptor\":\"file\",\"location\":\"/\",\"path\":\"/source\",\"credential\":{\"username\":\"" + currentUser + "\"},\"stream\":false},\"bytesCopied\":100}";
        assertEquals(expected, result);
    }

    @Test
    public void test_toString_sourcelocationnull() throws Exception {
        CopyInput source = new CopyInput("file", null, "/source", new DefaultCredential());
        CopyInput target = new CopyInput("file", "/", "/target", new DefaultCredential());
        copyOutput = new CopyOutput(source, target, 100L);
        String output = copyOutput.toString();

        String expected = "Copied '/source' from location 'file' to '/target' at location '/', 100 bytes copied";
        assertEquals(expected, output);
    }

    @Test
    public void test_toString_targetlocationnull() throws Exception {
        CopyInput source = new CopyInput("file", "/", "/source", new DefaultCredential());
        CopyInput target = new CopyInput("file", null, "/target", new DefaultCredential());
        copyOutput = new CopyOutput(source, target, 100L);
        String output = copyOutput.toString();

        String expected = "Copied '/source' from location '/' to '/target' at location 'file', 100 bytes copied";
        assertEquals(expected, output);
    }

}