package nl.esciencecenter.xenon.cli;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import nl.esciencecenter.xenon.XenonException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class LocalTest {
    @Rule
    public TemporaryFolder myfolder = new TemporaryFolder();

    @Test
    public void exec_touchFile_fileExists() throws IOException, XenonException, ArgumentParserException {
        File file1 = myfolder.newFile("file1");
        String[] args = {"local", "exec", "/bin/touch", file1.getAbsolutePath()};
        Main main = new Main();
        main.run(args);

        assertTrue(file1.exists());
    }
}
