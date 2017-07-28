package nl.esciencecenter.xenon.cli.listfiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.esciencecenter.xenon.filesystems.Path;
import nl.esciencecenter.xenon.filesystems.PathAttributes;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListFilesOutputTest {
    private ListFilesOutput listFilesOutput;

    @Before
    public void setUp() throws Exception {
        PathAttributes dir1 = mock(PathAttributes.class);
        when(dir1.getPath()).thenReturn(new Path("/start/dir1"));
        PathAttributes file1 = mock(PathAttributes.class);
        when(file1.getPath()).thenReturn(new Path("/start/file1"));
        listFilesOutput = new ListFilesOutput(new Path("/start"), Stream.of(dir1, file1));
    }

    @Test
    public void test_toString() {
        String result = listFilesOutput.toString();
        String sep = System.getProperty("line.separator");
        String expected = "dir1" + sep + "file1";
        assertEquals(expected, result);
    }

    @Test
    public void toJson() {
        Gson gson = new GsonBuilder().create();
        String result = gson.toJson(listFilesOutput);
        String expected = "{\"files\":[\"dir1\",\"file1\"]}";
        assertEquals(expected, result);
    }
}