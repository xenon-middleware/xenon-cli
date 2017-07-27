package nl.esciencecenter.xenon.cli.listfiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        String expected = "dir1" + sep + "file1";
        assertEquals(expected, result);
    }

    @Test
    public void toJson() {
        Gson gson = new GsonBuilder().create();
        String result = gson.toJson(listFilesOutput);
        String expected = "{\"objects\":[\"dir1\",\"file1\"],\"files\":[\"file1\"],\"directories\":[\"dir1\"]}";
        assertEquals(expected, result);
    }

    @Test
    public void getObjects_hiddenFile_invisible() {
        PathAttributes file1 = mock(PathAttributes.class);
        when(file1.isRegular()).thenReturn(true);
        when(file1.isHidden()).thenReturn(true);
        when(file1.getPath()).thenReturn(new Path("/start/.file1"));
        listFilesOutput = new ListFilesOutput(new Path("/start"), Collections.singletonList(file1), false);

        List<String> objects = listFilesOutput.getObjects();

        assertTrue("Hidden files not shown", objects.isEmpty());
    }

    @Test
    public void getObjects_hiddenFile_visible() {
        PathAttributes file1 = mock(PathAttributes.class);
        when(file1.isRegular()).thenReturn(true);
        when(file1.isHidden()).thenReturn(true);
        when(file1.getPath()).thenReturn(new Path("/start/.file1"));
        listFilesOutput = new ListFilesOutput(new Path("/start"), Collections.singletonList(file1), true);

        List<String> objects = listFilesOutput.getObjects();

        assertEquals("Hidden files shown", Collections.singletonList(".file1"), objects);
    }

    @Test
    public void getObjects_hiddenNestedFile_invisible() {
        PathAttributes dir1 = mock(PathAttributes.class);
        when(dir1.isDirectory()).thenReturn(true);
        when(dir1.getPath()).thenReturn(new Path("/start/dir1"));
        PathAttributes file1 = mock(PathAttributes.class);
        when(file1.isRegular()).thenReturn(true);
        when(file1.isHidden()).thenReturn(true);
        when(file1.getPath()).thenReturn(new Path("/start/dir1/.file1"));
        listFilesOutput = new ListFilesOutput(new Path("/start"), Arrays.asList(dir1, file1), false);

        List<String> objects = listFilesOutput.getObjects();

        List<String> expected = Collections.singletonList("dir1");
        assertEquals("Hidden files not shown", expected, objects);
    }

    @Test
    public void getObjects_hiddenNestedFile_visible() {
        PathAttributes dir1 = mock(PathAttributes.class);
        when(dir1.isDirectory()).thenReturn(true);
        when(dir1.getPath()).thenReturn(new Path("/start/dir1"));
        PathAttributes file1 = mock(PathAttributes.class);
        when(file1.isRegular()).thenReturn(true);
        when(file1.isHidden()).thenReturn(true);
        when(file1.getPath()).thenReturn(new Path("/start/dir1/.file1"));
        listFilesOutput = new ListFilesOutput(new Path("/start"), Arrays.asList(dir1, file1), true);

        List<String> objects = listFilesOutput.getObjects();

        List<String> expected = Arrays.asList("dir1", "dir1/.file1");
        assertEquals("Hidden files not shown", expected, objects);
    }

    @Test
    public void getObjects_hiddenDirNestedFile_invisible() {
        PathAttributes dir1 = mock(PathAttributes.class);
        when(dir1.isDirectory()).thenReturn(true);
        when(dir1.isHidden()).thenReturn(true);
        when(dir1.getPath()).thenReturn(new Path("/start/.dir1"));
        PathAttributes file1 = mock(PathAttributes.class);
        when(file1.isRegular()).thenReturn(true);
        when(file1.isHidden()).thenReturn(true);
        when(file1.getPath()).thenReturn(new Path("/start/.dir1/file1"));
        listFilesOutput = new ListFilesOutput(new Path("/start"), Arrays.asList(dir1, file1), false);

        List<String> objects = listFilesOutput.getObjects();

        List<String> expected = Collections.emptyList();
        assertEquals("Hidden files not shown", expected, objects);
    }


    @Test
    public void getObjects_hiddenDirNestedFile_visible() {
        PathAttributes dir1 = mock(PathAttributes.class);
        when(dir1.isDirectory()).thenReturn(true);
        when(dir1.isHidden()).thenReturn(true);
        when(dir1.getPath()).thenReturn(new Path("/start/.dir1"));
        PathAttributes file1 = mock(PathAttributes.class);
        when(file1.isRegular()).thenReturn(true);
        when(file1.isHidden()).thenReturn(true);
        when(file1.getPath()).thenReturn(new Path("/start/.dir1/file1"));
        listFilesOutput = new ListFilesOutput(new Path("/start"), Arrays.asList(dir1, file1), true);

        List<String> objects = listFilesOutput.getObjects();

        List<String> expected = Arrays.asList(".dir1", ".dir1/file1");
        assertEquals("Hidden files not shown", expected, objects);
    }


}