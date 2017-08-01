package nl.esciencecenter.xenon.cli.file;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import nl.esciencecenter.xenon.cli.Main;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CreateDirectoryTest {
    @Rule
    public TemporaryFolder myfolder = new TemporaryFolder();

    @Test
    public void createDirectory_noParent() throws IOException {
        File dir = myfolder.getRoot().toPath().resolve("somedir").toFile();
        String[] args = {"filesystem", "file", "mkdir", dir.getAbsolutePath()};
        Main main = new Main();

        main.run(args);

        assertTrue("somedir dir created", dir.isDirectory());
    }

    @Test
    public void createDirectory_withParent() throws IOException {
        File dir = myfolder.getRoot().toPath().resolve("somedir").toFile();
        String[] args = {"filesystem", "file", "mkdir", "--parents", dir.getAbsolutePath()};
        Main main = new Main();

        main.run(args);

        assertTrue("somedir dir exists", dir.isDirectory());
    }

}
