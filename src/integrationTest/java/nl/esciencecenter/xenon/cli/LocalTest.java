package nl.esciencecenter.xenon.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.junit.rules.TemporaryFolder;

public class LocalTest {
    @Rule
    public TemporaryFolder myfolder = new TemporaryFolder();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Rule
    public final TextFromStandardInputStream systemInMock
            = emptyStandardInputStream();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void exec_touchFile_fileExists() throws IOException {
        File file1 = myfolder.newFile("file1");
        exit.expectSystemExitWithStatus(0);
        exit.checkAssertionAfterwards(() -> {
            assertTrue(file1.exists());
        });

        String[] args = {"scheduler", "local", "exec", "/bin/touch", file1.getAbsolutePath()};
        Main main = new Main();
        main.run(args);
    }

    @Test
    public void exec_exit53() {
        exit.expectSystemExitWithStatus(53);

        String[] args = {"scheduler", "local", "exec", "/bin/bash", "--", "-c", "exit 53"};
        Main main = new Main();
        main.run(args);
    }

    @Test
    public void exec_wcstdin2stout() {
        exit.expectSystemExitWithStatus(0);
        exit.checkAssertionAfterwards(() -> {
            String out = systemOutRule.getLogWithNormalizedLineSeparator();
            String expected = "      2       3      19\n";
            assertEquals(expected, out);
        });

        systemInMock.provideLines("Hello world!", "line2");
        String[] args = {"--stacktrace", "scheduler", "local", "exec", "wc"};
        Main main = new Main();
        main.run(args);
    }
}
