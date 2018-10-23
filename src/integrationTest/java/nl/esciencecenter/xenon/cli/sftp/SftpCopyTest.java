package nl.esciencecenter.xenon.cli.sftp;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.Main;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.credentials.PasswordCredential;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.Path;

public class SftpCopyTest {
    private static final int PORT = 22;

    @ClassRule
    public static final GenericContainer serverA = new GenericContainer("nlesc/xenon-ssh").withExposedPorts(PORT);
    @ClassRule
    public static final GenericContainer serverB = new GenericContainer("nlesc/xenon-ssh").withExposedPorts(PORT);

    private static String getLocationA() {
        return serverA.getContainerIpAddress() + ":" + serverA.getMappedPort(PORT);
    }

    private static String getLocationB() {
        return serverB.getContainerIpAddress() + ":" + serverB.getMappedPort(PORT);
    }

    @Test
    public void copy_targetUsingSourceCreds() throws XenonException {
        String sourcePath = "/home/xenon/filesystem-test-fixture/links/file0";
        String targetPath = "/tmp/target-with-source-creds.txt";

        String[] args = new String[] {
                "filesystem",
                "sftp",
                "--location", getLocationA(),
                "--username", "xenon",
                "--password", "javagat",
                "--prop", "xenon.adaptors.filesystems.sftp.strictHostKeyChecking=false",
                "copy",
                "--target-location", getLocationB(),
                "--target-prop", "xenon.adaptors.filesystems.sftp.strictHostKeyChecking=false",
                sourcePath,
                targetPath
        };
        Main main = new Main();
        main.run(args);

        // Check file has been copied with Xenon to locationB
        Map<String, String> props = new HashMap<>();
        props.put("xenon.adaptors.filesystems.sftp.strictHostKeyChecking", "false");
        Credential cred = new PasswordCredential("xenon", "javagat".toCharArray());
        FileSystem fs = null;
        try {
            fs = FileSystem.create("sftp", getLocationB(), cred, props);
            Path path = new Path(targetPath);
            assertTrue(fs.exists(path));
        } finally {
            if (fs != null) {
                fs.close();
            }
        }
    }

    @Test
    public void copy_targetWithOwnCreds() throws XenonException {
        String sourcePath = "/home/xenon/filesystem-test-fixture/links/file0";
        String targetPath = "/tmp/target-with-own-creds.txt";

        String[] args = new String[] {
                "filesystem",
                "sftp",
                "--location", getLocationA(),
                "--username", "xenon",
                "--password", "javagat",
                "--prop", "xenon.adaptors.filesystems.sftp.strictHostKeyChecking=false",
                "copy",
                "--target-location", getLocationB(),
                "--target-username", "xenon",
                "--target-password", "javagat",
                "--target-prop", "xenon.adaptors.filesystems.sftp.strictHostKeyChecking=false",
                sourcePath,
                targetPath
        };
        Main main = new Main();
        main.run(args);

        // Check file has been copied with Xenon to locationB
        Credential cred = new PasswordCredential("xenon", "javagat".toCharArray());
        Map<String, String> props = new HashMap<>();
        props.put("xenon.adaptors.filesystems.sftp.strictHostKeyChecking", "false");
        FileSystem fs = null;
        try {
            fs = FileSystem.create("sftp", getLocationB(), cred, props);
            Path path = new Path(targetPath);
            assertTrue(fs.exists(path));
        } finally {
            if (fs != null) {
                fs.close();
            }
        }
    }
}
