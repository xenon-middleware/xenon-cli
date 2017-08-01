package nl.esciencecenter.xenon.cli.file;

import static nl.esciencecenter.xenon.cli.Main.main;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import nl.esciencecenter.xenon.adaptors.filesystems.PathAttributesImplementation;
import nl.esciencecenter.xenon.cli.listfiles.ListFilesLongOutput;
import nl.esciencecenter.xenon.cli.listfiles.ListFilesOutput;
import nl.esciencecenter.xenon.cli.listfiles.LongListItem;
import nl.esciencecenter.xenon.filesystems.Path;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

public class ListTest {
    @Rule
    public TemporaryFolder myFolder = new TemporaryFolder();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
    private String start;

    @Before
    public void setUp() throws IOException {
        start = myFolder.getRoot().getCanonicalPath();

        myFolder.newFile("file1");
        myFolder.newFile(".hidden2");
        File dir3 = myFolder.newFolder("dir3");
        assertTrue("Setup dir3/file4 fixture", new File(dir3, "file4").createNewFile());
        assertTrue("Setup dir3/.file5 fixture", new File(dir3, ".file5").createNewFile());
        File dir6 = myFolder.newFolder(".hidden6");
        assertTrue("Setup .hidden6/file4 fixture", new File(dir6, "file4").createNewFile());
        assertTrue("Setup .hidden6/.file5 fixture", new File(dir6, ".file5").createNewFile());
        assertTrue("Setup dir3/dir7 fixture", new File(dir3, "dir7").mkdir());
    }

    private void runFileList(String expectedOutput, String ...args) {
        String[] myArgs = new String[3 + args.length];
        myArgs[0] = "filesystem";
        myArgs[1] = "file";
        myArgs[2] = "list";
        System.arraycopy(args, 0, myArgs, 3, args.length);

        main(myArgs);

        String output = systemOutRule.getLogWithNormalizedLineSeparator();
        assertEquals(expectedOutput, output);
    }

    @Test
    public void list_default() throws IOException {
        runFileList( "dir3\nfile1\n",  start);
    }

    @Test
    public void list_emptyStart() throws IOException {
        String myStart = myFolder.newFolder("emptyDir").getAbsolutePath();
        runFileList("\n", myStart);
    }

    @Test
    public void list_showHidden() {
        runFileList(".hidden2\n.hidden6\ndir3\nfile1\n", "--hidden", start);
    }

    @Test
    public void list_recursive() {
        runFileList("dir3\n" +
                "dir3/dir7\n" +
                "dir3/file4\n" +
                "file1\n", "--recursive", start);
    }

    @Test
    public void list_recursiveAndShowHidden() {
        String expected = ".hidden2\n" +
                ".hidden6\n" +
                ".hidden6/.file5\n" +
                ".hidden6/file4\n" +
                "dir3\n" +
                "dir3/.file5\n" +
                "dir3/dir7\n" +
                "dir3/file4\n" +
                "file1\n";
        runFileList(expected, "--recursive", "--hidden", start);
    }

    @Test
    public void list_longFormat() {
        String[] args = new String[] {"filesystem", "file", "list", "--long", start};
        main(args);

        String output = systemOutRule.getLogWithNormalizedLineSeparator();
        assertTrue("Has header", output.startsWith("Type\tPermissions\tHidden\tOwner\tGroup\tSize\tLast modified at\tName"));
        assertEquals("Number of lines", 3, output.split("\n").length);
    }


    @Test
    public void list_jsonFormat() {
        String[] args = new String[] {"--json", "filesystem", "file", "list", start};

        main(args);

        // convert stdout to object via json deserialization
        String output = systemOutRule.getLogWithNormalizedLineSeparator();
        Gson gson = new Gson();
        ListFilesOutput result = gson.fromJson(output, ListFilesOutput.class);

        PathAttributesImplementation file1 = new PathAttributesImplementation();
        file1.setPath(new Path("/start/file1"));
        PathAttributesImplementation dir3 = new PathAttributesImplementation();
        dir3.setPath(new Path("/start/dir3"));
        ListFilesOutput expected = new ListFilesOutput(new Path("/start"), Stream.of(dir3, file1));
        assertEquals(expected, result);
    }

    @Test
    public void list_jsonLongFormat() {
        String[] args = new String[] {"--json", "filesystem", "file", "list", "--long", start};
        main(args);

        // convert stdout to object via json deserialization
        String output = systemOutRule.getLogWithNormalizedLineSeparator();
        Gson gson = new Gson();
        ListFilesLongOutput result = gson.fromJson(output, ListFilesLongOutput.class);

        assertEquals("Count", 2, result.files.size());
        LongListItem dir3 = result.files.get(0);
        assertEquals("first item is dir3", "dir3", dir3.relativePath);
        assertTrue("first item is dir", dir3.isDirectory);
        LongListItem file1 = result.files.get(1);
        assertEquals("second item is file1", "file1", file1.relativePath);
        assertTrue("second item is file", file1.isRegular);
    }
}
