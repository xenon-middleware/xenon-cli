package nl.esciencecenter.xenon.cli.listfiles;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import nl.esciencecenter.xenon.filesystems.Path;
import nl.esciencecenter.xenon.filesystems.PathAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;

public class ListFilesOutputTest {
    private ListFilesOutput listFilesOutput;

    @Before
    public void setUp() throws Exception {
        PathAttributes dir1 = mock(PathAttributes.class);
        when(dir1.isDirectory()).thenReturn(true);
        when(dir1.getPath()).thenReturn(new Path("/start/dir1"));
        PathAttributes file1 = mock(PathAttributes.class);
        when(file1.isRegular()).thenReturn(true);
        when(file1.getPath()).thenReturn(new Path("/start/file1"));
        listFilesOutput = new ListFilesOutput(new Path("/start"), Arrays.asList(dir1, file1), false);
    }

    @Test
    public void test_toString() {
        String result = listFilesOutput.toString();
        String sep = System.getProperty("line.separator");
        String expected = "dir1" + sep + "file1" + sep;
        assertEquals(expected, result);
    }

    @Test
    public void toJson() {
        Gson gson = new GsonBuilder().create();
        String result = gson.toJson(listFilesOutput);
        String expected = "{\"objects\":[\"dir1\",\"file1\"],\"files\":[\"file1\"],\"directories\":[\"dir1\"]}";
        assertEquals(expected, result);
    }
}