package nl.esciencecenter.xenon.cli;


import com.github.geowarin.junit.DockerRule;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonFactory;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.files.FileSystem;
import nl.esciencecenter.xenon.files.Files;
import nl.esciencecenter.xenon.files.Path;
import nl.esciencecenter.xenon.files.RelativePath;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public class SftpTest {
    public static String PORT = "22/tcp";

    @ClassRule
    public static DockerRule server = DockerRule.builder()
            .image("nlesc/xenon-ssh")
            .ports("22")
            .waitForPort(PORT)
            .build();

    @Rule
    public TemporaryFolder myfolder = new TemporaryFolder();

    public static String getLocation() {
        return server.getDockerHost() + ":" + server.getHostPort(PORT);
    }

    public static String[] argsBuilder(String... args) {
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
    public void upload() throws IOException, XenonException, ArgumentParserException {
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

        Xenon xenon = null;
        try {
            xenon = XenonFactory.newXenon(null);
            Credential cred = xenon.credentials().newPasswordCredential("sftp", "xenon", "javagat".toCharArray(), null);
            Files files = xenon.files();
            FileSystem fs = files.newFileSystem("sftp", getLocation(), cred, null);
            Path path = files.newPath(fs, new RelativePath(targetPath));
            assertTrue(files.exists(path));
        } finally {
            XenonFactory.endXenon(xenon);
        }
    }

    @Test
    public void download() throws XenonException, ArgumentParserException {
        String sourcePath = "/home/xenon/filesystem-test-fixture/links/file0";
        File targetFile = new File(myfolder.getRoot(), "target.txt");
        String[] args = argsBuilder("download", sourcePath, targetFile.getAbsolutePath());
        Main main = new Main();
        main.run(args);

        assertTrue(targetFile.exists());
    }
}
