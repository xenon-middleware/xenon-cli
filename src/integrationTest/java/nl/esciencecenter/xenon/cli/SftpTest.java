package nl.esciencecenter.xenon.cli;


import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.credentials.PasswordCredential;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.Path;

import com.github.geowarin.junit.DockerRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SftpTest {
    private static final String PORT = "22/tcp";

    @ClassRule
    public static final DockerRule server = DockerRule.builder()
            .image("nlesc/xenon-ssh")
            .ports("22")
            .waitForPort(PORT)
            .build();

    @Rule
    public TemporaryFolder myfolder = new TemporaryFolder();

    private static String getLocation() {
        return server.getDockerHost() + ":" + server.getHostPort(PORT);
    }

    private static String[] argsBuilder(String... args) {
        String location = getLocation();
        String[] myargs = {
                "--username", "xenon",
                "--password", "javagat",
                "sftp",
                "--location", location
        };
        return Stream.concat(Arrays.stream(myargs), Arrays.stream(args)).toArray(String[]::new);
    }

    @Test
    public void upload() throws IOException, XenonException {
        File sourceFile = myfolder.newFile("source.txt");
        sourceFile.createNewFile();

        String targetPath = "/tmp/target.txt";
        String[] args= argsBuilder(
                "upload",
                sourceFile.getAbsolutePath(),
                targetPath
        );
        Main main = new Main();
        main.run(args);

        Credential cred = new PasswordCredential("xenon", "javagat".toCharArray());
        FileSystem fs = FileSystem.create("sftp", getLocation(), cred);
        Path path = new Path(targetPath);
        assertTrue(fs.exists(path));
        fs.close();
    }

    @Test
    public void download() throws XenonException {
        String sourcePath = "/home/xenon/filesystem-test-fixture/links/file0";
        File targetFile = new File(myfolder.getRoot(), "target.txt");
        String[] args = argsBuilder("download", sourcePath, targetFile.getAbsolutePath());
        Main main = new Main();
        main.run(args);

        assertTrue(targetFile.exists());
    }
}
