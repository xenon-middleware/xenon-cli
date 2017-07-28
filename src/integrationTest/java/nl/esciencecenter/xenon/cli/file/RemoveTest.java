package nl.esciencecenter.xenon.cli.file;

import nl.esciencecenter.xenon.cli.Main;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertFalse;

public class RemoveTest {
    @Rule
    public TemporaryFolder myFolder = new TemporaryFolder();

    @Test
    public void removeFile_touchedFile_fileShouldNotExist() throws IOException {
        File file1 = myFolder.newFile("file1");

        String[] args = {"file", "remove", file1.getAbsolutePath()};
        Main main = new Main();
        main.run(args);

        assertFalse(file1.exists());
    }
}
