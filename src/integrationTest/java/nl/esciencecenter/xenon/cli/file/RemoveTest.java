package nl.esciencecenter.xenon.cli.file;

import static junit.framework.TestCase.assertFalse;

import java.io.File;
import java.io.IOException;

import nl.esciencecenter.xenon.cli.Main;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RemoveTest {
    @Rule
    public TemporaryFolder myFolder = new TemporaryFolder();

    @Test
    public void removeFile_touchedFile_fileShouldNotExist() throws IOException {
        File file1 = myFolder.newFile("file1");

        String[] args = {"filesystem", "file", "remove", file1.getAbsolutePath()};
        Main main = new Main();
        main.run(args);

        assertFalse(file1.exists());
    }
}
