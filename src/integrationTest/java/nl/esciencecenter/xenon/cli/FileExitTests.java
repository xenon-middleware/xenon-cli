package nl.esciencecenter.xenon.cli;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

/**
 * Tests using file adaptor, which cause System.exit
 *
 * Separated from rest of tests, because normal asserts are not reached when an exit occurs
 * must perform asserts in checkAssertionAfterwards callback
 */
public class FileExitTests {
    @Rule
    public TemporaryFolder myfolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void list_aFile_exit1() throws IOException {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            String expected = "file adaptor: Path is not a directory";
            String log = systemErrRule.getLog();
            assertTrue(expected, log.contains(expected));
        });

        File file1 = myfolder.newFile("file1");

        String path = file1.getAbsolutePath();
        String[] args = {"filesystem", "file", "list", path};
        Main main = new Main();
        main.run(args);
    }

    @Test
    public void list_nonExistingPath_exit1() throws IOException {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            String expected = "file adaptor: Path is not a directory";
            assertTrue(expected, systemErrRule.getLog().contains(expected));
        });
        File file1 = myfolder.newFile("idontexist");

        String[] args = {"filesystem", "file", "list", file1.getAbsolutePath()};
        Main main = new Main();
        main.run(args);
    }

    @Test
    public void list_nonExistingPathWithStacktrace_exit1() throws IOException {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            String expected = "at nl.esciencecenter.xenon.filesystems";
            String log = systemErrRule.getLog();
            assertTrue(expected, log.contains(expected));
        });
        File file1 = myfolder.newFile("idontexist");

        String[] args = {"--stacktrace", "filesystem", "file", "list", file1.getAbsolutePath()};
        Main main = new Main();
        main.run(args);
    }

    @Test
    public void copy_targetExists_throwsExecption() throws IOException {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            String expected = "file adaptor: Destination path already exists";
            assertTrue(expected, systemErrRule.getLog().contains(expected));
        });

        File sourceFile = myfolder.newFile("source.txt");
        File targetFile = myfolder.newFile("target.txt");

        String[] args = {"filesystem", "file", "copy", sourceFile.getAbsolutePath(), targetFile.getAbsolutePath()};
        Main main = new Main();
        main.run(args);
    }

    @Test
    public void copy_RecursiveStdin_throwsException() {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            String expected = "file adaptor: Unable to do recursive copy from stdin";
            assertTrue(expected, systemErrRule.getLog().contains(expected));
        });
        String[] args = {"filesystem", "file", "copy", "--recursive", "-", myfolder.getRoot().getAbsolutePath()};
        Main main = new Main();
        main.run(args);
    }

    @Test
    public void copyFile_RecursiveStdout_throwsException() {
        exit.checkAssertionAfterwards(() -> {
            String expected = "file adaptor: Unable to do recursive copy to stdout";
            assertTrue(expected, systemErrRule.getLog().contains(expected));
        });
        exit.expectSystemExitWithStatus(1);
        String[] args = {"filesystem", "file", "copy", "--recursive", myfolder.getRoot().getAbsolutePath(), "-"};
        Main main = new Main();
        main.run(args);
    }

    @Test
    public void createDirectory_withoutParent_NoSuchPathException() throws IOException {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            String log = systemErrRule.getLog();
            assertTrue("NoSuchPathException message", log.contains("file adaptor: Path does not exist:"));
            assertTrue("Parent dir", log.contains("somedir"));
        });
        File dir = myfolder.getRoot().toPath().resolve("somedir").resolve("otherdir").toFile();
        String[] args = {"filesystem", "file", "mkdir", dir.getAbsolutePath()};
        Main main = new Main();

        main.run(args);
    }

    @Test
    public void rename_targetAlreadyExists_PathAlreadyExistsException() throws IOException {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            String log = systemErrRule.getLog();
            assertTrue("PathAlreadyExistsException message", log.contains("file adaptor: Path already exists:"));
            assertTrue("Target", log.contains("file2"));
        });
        File source = myfolder.newFile("file1");
        File target = myfolder.newFile("file2");

        String[] args = {"filesystem", "file", "rename", source.getAbsolutePath(), target.getAbsolutePath()};
        Main main = new Main();

        main.run(args);
    }
}
