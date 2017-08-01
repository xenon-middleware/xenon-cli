package nl.esciencecenter.xenon.cli.file;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import nl.esciencecenter.xenon.cli.Main;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RenameTest {
    @Rule
    public TemporaryFolder myFolder = new TemporaryFolder();

    @Test
    public void rename_file() throws IOException {
        File source = myFolder.newFile("file1");
        File target = new File(myFolder.getRoot(), "file2");

        String[] args = {"filesystem", "file", "rename", source.getAbsolutePath(), target.getAbsolutePath()};
        Main main = new Main();

        main.run(args);

        assertTrue("Target exists", target.isFile());
    }
}
