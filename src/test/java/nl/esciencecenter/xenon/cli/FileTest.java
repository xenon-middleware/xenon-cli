package nl.esciencecenter.xenon.cli;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import nl.esciencecenter.xenon.XenonException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileTest {
    @Rule
    public TemporaryFolder myfolder = new TemporaryFolder();

    @Test
    public void copyFile() throws XenonException, ArgumentParserException, IOException {
        File sourceFile = myfolder.newFile("source.txt");
        sourceFile.createNewFile();
        File targetFile = new File(myfolder.getRoot(), "target.txt");

        String[] args = {"file", "copy", sourceFile.getAbsolutePath(), targetFile.getAbsolutePath()};
        Main main = new Main();
        main.run(args);

        assertTrue(targetFile.isFile());
    }


    @Test
    public void copyFile_recursiveFile() throws XenonException, ArgumentParserException, IOException {
        File sourceFile = myfolder.newFile("source.txt");
        sourceFile.createNewFile();
        File targetFile = new File(myfolder.getRoot(), "target.txt");

        String[] args = {"file", "copy", "--recursive", sourceFile.getAbsolutePath(), targetFile.getAbsolutePath()};
        Main main = new Main();
        main.run(args);

        assertTrue(targetFile.isFile());
    }

    @Test(expected = XenonException.class)
    public void copyFile_targetExists_throwsExecption() throws XenonException, ArgumentParserException, IOException {
        File sourceFile = myfolder.newFile("source.txt");
        sourceFile.createNewFile();
        File targetFile = myfolder.newFile("target.txt");
        targetFile.createNewFile();

        String[] args = {"file", "copy", sourceFile.getAbsolutePath(), targetFile.getAbsolutePath()};
        Main main = new Main();
        main.run(args);
    }

    @Test
    public void copyFile_fromStdin() throws XenonException, ArgumentParserException, IOException {
        Path targetFile = Paths.get(myfolder.getRoot().getAbsolutePath(), "target.txt");
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

    @Test(expected = XenonException.class)
    public void copyFile_RecursiveStdin_throwsExecption() throws XenonException, ArgumentParserException {
        String[] args = {"file", "copy", "--recursive", "-", myfolder.getRoot().getAbsolutePath()};
        Main main = new Main();
        main.run(args);
    }

    @Test(expected = XenonException.class)
    public void copyFile_RecursiveStdout_throwsExecption() throws XenonException, ArgumentParserException {
        String[] args = {"file", "copy", "--recursive", myfolder.getRoot().getAbsolutePath(), "-"};
        Main main = new Main();
        main.run(args);
    }

    @Test
    public void listFile_aDirectory() throws IOException, XenonException, ArgumentParserException {
        myfolder.newFile("file1").createNewFile();
        myfolder.newFile(".hidden1").createNewFile();
        File dir1 = myfolder.newFolder("dir1");
        dir1.mkdirs();
        new File(dir1, "file3").createNewFile();
        File hdir2 = myfolder.newFolder(".hidden2");
        hdir2.mkdirs();

        String path = myfolder.getRoot().getCanonicalPath();
        String[] args = {"file", "list", path};
        Main main = new Main();
        ListFilesOutput output = (ListFilesOutput) main.run(args);

        ListFilesOutput expected = new ListFilesOutput();
        expected.objects.add("file1");
        expected.objects.add("dir1");
        expected.directories.add("dir1");
        expected.files.add("file1");
        assertEquals(expected, output);
    }

    @Test
    public void listFile_aFile() throws IOException, XenonException, ArgumentParserException {
        File file1 = myfolder.newFile("file1");
        file1.createNewFile();

        String path = file1.getAbsolutePath();
        String[] args = {"file", "list", path};
        Main main = new Main();
        ListFilesOutput output = (ListFilesOutput) main.run(args);

        ListFilesOutput expected = new ListFilesOutput();
        expected.objects.add("file1");
        expected.files.add("file1");
        assertEquals(expected, output);
    }

    @Test
    public void removeFile_touchedFile_fileShouldNotExist() throws IOException, XenonException, ArgumentParserException {
        File file1 = myfolder.newFile("file1");
        file1.createNewFile();

        String[] args = {"file", "remove", file1.getAbsolutePath()};
        Main main = new Main();
        main.run(args);

        assertFalse(file1.exists());
    }

}
