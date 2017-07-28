package nl.esciencecenter.xenon.cli.file;

import nl.esciencecenter.xenon.cli.Main;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CopyTest {
    @Rule
    public TemporaryFolder myFolder = new TemporaryFolder();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void copy_file() throws IOException {
        File sourceFile = myFolder.newFile("source.txt");
        File targetFile = new File(myFolder.getRoot(), "target.txt");

        String[] args = {"file", "copy", sourceFile.getAbsolutePath(), targetFile.getAbsolutePath()};
        Main main = new Main();
        main.run(args);

        assertTrue(targetFile.isFile());
    }

    @Test
    public void copy_recursiveFile() throws IOException {
        File sourceFile = myFolder.newFile("source.txt");
        File targetFile = new File(myFolder.getRoot(), "target.txt");

        String[] args = {"file", "copy", "--recursive", sourceFile.getAbsolutePath(), targetFile.getAbsolutePath()};
        Main main = new Main();
        main.run(args);

        assertTrue(targetFile.isFile());
    }

    @Test
    public void copy_dir() throws IOException {
        File sourceDir = myFolder.newFolder("source");
        File targetDir = new File(myFolder.getRoot(), "target");

        String[] args = {"file", "copy", "--recursive", sourceDir.getAbsolutePath(), targetDir.getAbsolutePath()};
        Main main = new Main();
        main.run(args);

        assertTrue(targetDir.isDirectory());
    }

    @Test
    public void copy_recursiveDir() throws IOException {
        File sourceDir = myFolder.newFolder("source");
        assertTrue("Setup source/file2 fixture",new File(sourceDir, "file1").createNewFile());
        File sourceDirDir = myFolder.newFolder("source", "dep1");
        assertTrue("Setup source/file2 fixture",new File(sourceDirDir, "file2").createNewFile());
        File targetDir = new File(myFolder.getRoot(), "target");

        String[] args = {"file", "copy", "--recursive", sourceDir.getAbsolutePath(), targetDir.getAbsolutePath()};
        Main main = new Main();
        main.run(args);

        assertTrue(targetDir.isDirectory());
        assertTrue(new File(targetDir, "file1").isFile());
        File targetDirDir = new File(targetDir, "dep1");
        assertTrue(targetDirDir.isDirectory());
        assertTrue(new File(targetDirDir, "file2").isFile());
    }

    @Test
    public void copy_fromStdin() throws IOException {
        Path targetFile = Paths.get(myFolder.getRoot().getAbsolutePath(), "target.txt");
        InputStream oldIn = System.in;
        String sourceContent = "my content";
        InputStream sourceIn = new ByteArrayInputStream(sourceContent.getBytes());
        System.setIn(sourceIn);

        try {
            String[] args = {"file", "copy", "-", targetFile.toString()};
            Main main = new Main();
            main.run(args);

            String targetContent = String.join("", Files.lines(targetFile).collect(Collectors.toList()));
            assertEquals(sourceContent, targetContent);
        } finally {
            System.setIn(oldIn);
        }
    }

    @Test
    public void copy_toStdout() throws IOException {
        File sourceFile = myFolder.newFile("source.txt");
        String message = "Hello World!\n";
        Files.write(sourceFile.toPath(), message.getBytes());

        String[] args = {"file", "copy", sourceFile.getAbsolutePath(), "-"};
        Main main = new Main();
        main.run(args);

        assertEquals(message, systemOutRule.getLogWithNormalizedLineSeparator());
    }
}
