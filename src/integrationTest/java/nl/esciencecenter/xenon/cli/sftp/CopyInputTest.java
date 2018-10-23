package nl.esciencecenter.xenon.cli.sftp;

import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import nl.esciencecenter.xenon.cli.copy.CopyInput;
import nl.esciencecenter.xenon.credentials.PasswordCredential;

public class CopyInputTest {
    private static final int PORT = 22;

    @ClassRule
    public static final GenericContainer server = new GenericContainer("nlesc/xenon-ssh").withExposedPorts(PORT);

    private static String getLocation() {
        return server.getContainerIpAddress() + ":" + server.getMappedPort(PORT);
    }

    @Test
    public void isStream_sshdash_false() throws Exception {
        PasswordCredential cred = new PasswordCredential("xenon", "javagat".toCharArray());
        Map<String, String> props = new HashMap<>();
        props.put("xenon.adaptors.filesystems.sftp.strictHostKeyChecking", "false");
        CopyInput copyInput = new CopyInput("sftp", getLocation(), "-", cred, props);
        copyInput.getFileSystem().close();
        assertFalse(copyInput.isStream());
    }
}
