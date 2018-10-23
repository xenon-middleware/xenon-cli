package nl.esciencecenter.xenon.cli;


import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.GenericContainer;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.credentials.PasswordCredential;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.Path;

public class SftpTest {
    private static final int PORT = 22;

    @ClassRule
    public static final GenericContainer server = new GenericContainer("nlesc/xenon-ssh").withExposedPorts(PORT);


    @Rule
    public TemporaryFolder myfolder = new TemporaryFolder();

    private static String getLocation() {
        return server.getContainerIpAddress() + ":" + server.getMappedPort(PORT);
    }

    private static String[] argsBuilder(String... args) {
        String location = getLocation();
        String[] myargs = {
            "filesystem",
            "sftp",
            "--location", location,
            "--username", "xenon",
            "--password", "javagat",
            "--prop", "xenon.adaptors.filesystems.sftp.strictHostKeyChecking=false"
        };
        return Stream.concat(Arrays.stream(myargs), Arrays.stream(args)).toArray(String[]::new);
    }

    @Test
    public void upload() throws IOException, XenonException {
        File sourceFile = myfolder.newFile("source.txt");
        sourceFile.createNewFile();

        String targetPath = "/tmp/uploaded.txt";
        String[] args = argsBuilder(
                "upload",
                sourceFile.getAbsolutePath(),
                targetPath
        );
        Main main = new Main();
        main.run(args);

        assertRemoteFileExists(targetPath);
    }

    private void assertRemoteFileExists(String targetPath) throws XenonException {
        Credential cred = new PasswordCredential("xenon", "javagat".toCharArray());
        FileSystem fs = null;
        try {
            fs = FileSystem.create("sftp", getLocation(), cred);
            Path path = new Path(targetPath);
            assertTrue(fs.exists(path));
        } finally {
            if (fs != null) {
                fs.close();
            }
        }
    }

    @Test
    public void download() {
        String sourcePath = "/home/xenon/filesystem-test-fixture/links/file0";
        File targetFile = new File(myfolder.getRoot(), "downloaded.txt");
        String[] args = argsBuilder("download", sourcePath, targetFile.getAbsolutePath());
        Main main = new Main();
        main.run(args);

        assertTrue(targetFile.exists());
    }

    @Test
    public void copy() throws XenonException {
        String sourcePath = "/home/xenon/filesystem-test-fixture/links/file0";
        String targetPath = "/tmp/copied.txt";

        String[] args = argsBuilder("copy", sourcePath, targetPath);
        Main main = new Main();
        main.run(args);

        assertRemoteFileExists(targetPath);
    }
}
