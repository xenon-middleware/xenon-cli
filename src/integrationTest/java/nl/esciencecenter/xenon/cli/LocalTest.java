package nl.esciencecenter.xenon.cli;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LocalTest {
    @Rule
    public TemporaryFolder myfolder = new TemporaryFolder();

    @Test
    public void exec_touchFile_fileExists() throws IOException {
        File file1 = myfolder.newFile("file1");
        String[] args = {"local", "exec", "/bin/touch", file1.getAbsolutePath()};
        Main main = new Main();
        main.run(args);

        assertTrue(file1.exists());
    }
}
