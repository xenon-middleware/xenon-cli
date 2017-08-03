package nl.esciencecenter.xenon.cli;

import static nl.esciencecenter.xenon.cli.Utils.createCredential;
import static nl.esciencecenter.xenon.cli.Utils.getAbsolutePath;
import static org.junit.Assert.assertEquals;

import java.util.Hashtable;
import java.util.Map;

import nl.esciencecenter.xenon.credentials.CertificateCredential;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.credentials.DefaultCredential;
import nl.esciencecenter.xenon.credentials.PasswordCredential;
import nl.esciencecenter.xenon.filesystems.Path;

import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Test;

public class UtilsTest {
    @Test(expected = IllegalAccessError.class)
    public void constructor() {
        new Utils();
    }

    @Test
    public void getAbsolutePath_remote() throws Exception {
        Path result = getAbsolutePath("sftp", "/foo");

        Path expected = new Path("/foo");
        assertEquals(expected, result);
    }

    @Test
    public void getAbsolutePath_localAbsolute() throws Exception {
        Path result = getAbsolutePath("local", "/foo");

        Path expected = new Path("/foo");
        assertEquals(expected, result);
    }

    @Test
    public void getAbsolutePath_fileAbsolute() throws Exception {
        Path result = getAbsolutePath("file", "/foo");

        Path expected = new Path("/foo");
        assertEquals(expected, result);
    }

    @Test
    public void getAbsolutePath_fileTilde() throws Exception {
        Path result = getAbsolutePath("file", "~/foo");

        Path expected = new Path("~/foo");
        assertEquals(expected, result);
    }

    @Test
    public void getAbsolutePath_filPipe() throws Exception {
        Path result = getAbsolutePath("file", "-");

        Path expected = new Path("-");
        assertEquals(expected, result);
    }

    @Test
    public void getAbsolutePath_localRelative() throws Exception {
        Path result = getAbsolutePath("local", "foo");

        Path expected = new Path(System.getProperty("user.dir") + "/foo");
        assertEquals(expected, result);
    }

    @Test
    public void getAbsolutePath_fileRelative() throws Exception {
        Path result = getAbsolutePath("file", "foo");

        Path expected = new Path(System.getProperty("user.dir") + "/foo");
        assertEquals(expected, result);
    }

    @Test
    public void createCredential_default() {
        Map<String, Object> attrs = new Hashtable<>();
        Namespace res = new Namespace(attrs);

        Credential result = createCredential(res);

        Credential expected = new DefaultCredential();
        assertEquals(expected, result);
    }

    @Test
    public void createCredential_username() {
        Map<String, Object> attrs = new Hashtable<>();
        attrs.put("username", "someone");
        Namespace res = new Namespace(attrs);

        Credential result = createCredential(res);

        Credential expected = new DefaultCredential("someone");
        assertEquals(expected, result);
    }

    @Test
    public void createCredential_password() {
        Map<String, Object> attrs = new Hashtable<>();
        attrs.put("username", "someone");
        attrs.put("password", "mypassword");
        Namespace res = new Namespace(attrs);

        Credential result = createCredential(res);

        Credential expected = new PasswordCredential("someone", "mypassword".toCharArray());
        assertEquals(expected, result);
    }

    @Test
    public void createCredential_certfile() {
        Map<String, Object> attrs = new Hashtable<>();
        attrs.put("username", "someone");
        attrs.put("certfile", "/home/someone/.ssh/id_rsa");
        Namespace res = new Namespace(attrs);

        Credential result = createCredential(res);

        Credential expected = new CertificateCredential("someone", "/home/someone/.ssh/id_rsa", null);
        assertEquals(expected, result);
    }

    @Test
    public void createCredential_certfilePassphrase() {
        Map<String, Object> attrs = new Hashtable<>();
        attrs.put("username", "someone");
        attrs.put("certfile", "/home/someone/.ssh/id_rsa");
        attrs.put("password", "mypassphrase");
        Namespace res = new Namespace(attrs);

        Credential result = createCredential(res);

        Credential expected = new CertificateCredential("someone", "/home/someone/.ssh/id_rsa", "mypassphrase".toCharArray());
        assertEquals(expected, result);
    }
}