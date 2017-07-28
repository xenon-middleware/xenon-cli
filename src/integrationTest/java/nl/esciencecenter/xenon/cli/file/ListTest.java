package nl.esciencecenter.xenon.cli.file;

import nl.esciencecenter.xenon.cli.Main;
import nl.esciencecenter.xenon.cli.listfiles.ListFilesOutput;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ListTest {
    @Rule
    public TemporaryFolder myFolder = new TemporaryFolder();

    @Test
    public void list_aDirectoryWithHiddenFiles_onlyListNonHiddenObjects() throws IOException {
        myFolder.newFile("file1");
        myFolder.newFile(".hidden1");
        File dir1 = myFolder.newFolder("dir1");
        assertTrue("Setup dir1/file3 fixture", new File(dir1, "file3").createNewFile());
        myFolder.newFolder(".hidden2");

        String path = myFolder.getRoot().getCanonicalPath();
        String[] args = {"file", "list", path};
        Main main = new Main();
        ListFilesOutput output = (ListFilesOutput) main.run(args);

        Set<String> result = new HashSet<>(output.getObjects());
        Set<String> expected = new HashSet<>(Arrays.asList("dir1", "file1"));
        assertEquals(expected, result);
    }

}
