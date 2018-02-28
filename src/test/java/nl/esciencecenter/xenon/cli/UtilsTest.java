package nl.esciencecenter.xenon.cli;

import static nl.esciencecenter.xenon.cli.Utils.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.InvalidLocationException;
import nl.esciencecenter.xenon.UnknownAdaptorException;
import nl.esciencecenter.xenon.UnknownPropertyException;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.CertificateCredential;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.credentials.CredentialMap;
import nl.esciencecenter.xenon.credentials.DefaultCredential;
import nl.esciencecenter.xenon.credentials.PasswordCredential;
import nl.esciencecenter.xenon.schedulers.Scheduler;
import nl.esciencecenter.xenon.schedulers.SchedulerAdaptorDescription;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UtilsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test(expected = IllegalAccessError.class)
    public void constructor() {
        new Utils();
    }

    @Test
    public void parseArgumentListAsMap_empty_empty() {
        List<String> list = new ArrayList<>();

        Map<String, String> map = parseArgumentListAsMap(list);

        assertTrue(map.isEmpty());
    }

    @Test
    public void parseArgumentListAsMap_noEqualSign_empty() {
        List<String> list = Collections.singletonList("something");

        Map<String, String> map = parseArgumentListAsMap(list);

        assertTrue(map.isEmpty());
    }


    @Test
    public void parseArgumentListAsMap_singleEntry() {
        List<String> list = Collections.singletonList("key1=val1");

        Map<String, String> map = parseArgumentListAsMap(list);

        Map<String, String> expected = new HashMap<>();
        expected.put("key1", "val1");
        assertEquals(expected, map);
    }

    @Test
    public void parseArgumentListAsMap_twoEntriesWithSameKeys_lastEntryWins() {
        List<String> list = Arrays.asList("key1=val1", "key1=val2");

        Map<String, String> map = parseArgumentListAsMap(list);

        Map<String, String> expected = new HashMap<>();
        expected.put("key1", "val2");
        assertEquals(expected, map);
    }

    @Test
    public void parseArgumentListAsMap_twoEntriesWithDifferentKeys() {
        List<String> list = Arrays.asList("key1=val1", "key2=val2");

        Map<String, String> map = parseArgumentListAsMap(list);

        Map<String, String> expected = new HashMap<>();
        expected.put("key1", "val1");
        expected.put("key2", "val2");
        assertEquals(expected, map);
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
    public void createCredential_usernameAndPassword() {
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

    @Test
    public void createCredential_viaUsername_credentialMapWithDefaultCred() {
        Map<String, Object> attrs = new Hashtable<>();
        attrs.put("via_usernames", Collections.singletonList("somehost=someone"));
        Namespace res = new Namespace(attrs);

        Credential result = createCredential(res);

        CredentialMap expected = new CredentialMap(new DefaultCredential());
        expected.put("somehost", new DefaultCredential("someone"));
        assertEquals(expected, result);
    }

    @Test
    public void createCredential_viaUsernames_credentialMapWithDefaultCred() {
        Map<String, Object> attrs = new Hashtable<>();
        attrs.put("via_usernames", Arrays.asList("somehost=someone", "otherhost=otherone"));
        Namespace res = new Namespace(attrs);

        Credential result = createCredential(res);

        CredentialMap expected = new CredentialMap(new DefaultCredential());
        expected.put("somehost", new DefaultCredential("someone"));
        expected.put("otherhost", new DefaultCredential("otherone"));
        assertEquals(expected, result);
    }

    @Test
    public void createCredential_UsernameAndViaPassword_credentialMap() {
        Map<String, Object> attrs = new Hashtable<>();
        attrs.put("username", "otherone");
        attrs.put("via_usernames", Collections.singletonList("somehost=someone"));
        Namespace res = new Namespace(attrs);

        Credential result = createCredential(res);

        CredentialMap expected = new CredentialMap(new DefaultCredential("otherone"));
        expected.put("somehost", new DefaultCredential("someone"));
        assertEquals(expected, result);
    }

    @Test
    public void createCredential_UsernameAndViaUsernameAndViaPasswordForOtherHost() {
        Map<String, Object> attrs = new Hashtable<>();
        attrs.put("username", "otherone");
        attrs.put("via_usernames", Collections.singletonList("somehost=someone"));
        attrs.put("via_passwords", Collections.singletonList("otherhost=somepw"));
        Namespace res = new Namespace(attrs);

        Credential result = createCredential(res);

        CredentialMap expected = new CredentialMap(new DefaultCredential("otherone"));
        expected.put("somehost", new DefaultCredential("someone"));
        expected.put("otherhost", new PasswordCredential("otherone", "somepw".toCharArray()));
        assertEquals(expected, result);
    }

    @Test
    public void createCredential_UsernameAndViaUsernameAndViaPasswordForSameHost() {
        Map<String, Object> attrs = new Hashtable<>();
        attrs.put("username", "otherone");
        attrs.put("via_usernames", Collections.singletonList("somehost=someone"));
        attrs.put("via_passwords", Collections.singletonList("somehost=somepw"));
        Namespace res = new Namespace(attrs);

        Credential result = createCredential(res);

        CredentialMap expected = new CredentialMap(new DefaultCredential("otherone"));
        expected.put("somehost", new PasswordCredential("someone", "somepw".toCharArray()));
        assertEquals(expected, result);
    }

    @Test
    public void createCredential_UsernameAndViaPassword_credentialMapWithPassword() {
        Map<String, Object> attrs = new Hashtable<>();
        attrs.put("username", "someone");
        attrs.put("via_passwords", Collections.singletonList("somehost=somepw"));
        Namespace res = new Namespace(attrs);

        Credential result = createCredential(res);

        CredentialMap expected = new CredentialMap(new DefaultCredential("someone"));
        expected.put("somehost", new PasswordCredential("someone", "somepw".toCharArray()));
        assertEquals(expected, result);
    }

    @Test
    public void createCredential_UsernameAndViaCertfile_credentialMapWithCert() {
        Map<String, Object> attrs = new Hashtable<>();
        attrs.put("username", "someone");
        attrs.put("via_certfiles", Collections.singletonList("somehost=/home/someone/.ssh/somehost.key"));
        Namespace res = new Namespace(attrs);

        Credential result = createCredential(res);

        CredentialMap expected = new CredentialMap(new DefaultCredential("someone"));
        expected.put("somehost", new CertificateCredential("someone", "/home/someone/.ssh/somehost.key", null));
        assertEquals(expected, result);
    }

    @Test
    public void createCredential_UsernameAndViaUsernameAndViaCertfile_credentialMapWithCert() {
        Map<String, Object> attrs = new Hashtable<>();
        attrs.put("username", "otherone");
        attrs.put("via_usernames", Collections.singletonList("somehost=someone"));
        attrs.put("via_certfiles", Collections.singletonList("somehost=/home/someone/.ssh/somehost.key"));
        Namespace res = new Namespace(attrs);

        Credential result = createCredential(res);

        CredentialMap expected = new CredentialMap(new DefaultCredential("otherone"));
        expected.put("somehost", new CertificateCredential("someone", "/home/someone/.ssh/somehost.key", null));
        assertEquals(expected, result);
    }

    @Test
    public void createCredential_UsernameAndViaUsernameAndViaCertfileAndViaPassword_credentialMapWithCert() {
        Map<String, Object> attrs = new Hashtable<>();
        attrs.put("username", "otherone");
        attrs.put("via_usernames", Collections.singletonList("somehost=someone"));
        attrs.put("via_certfiles", Collections.singletonList("somehost=/home/someone/.ssh/somehost.key"));
        attrs.put("via_passwords", Collections.singletonList("somehost=somepw"));
        Namespace res = new Namespace(attrs);

        Credential result = createCredential(res);

        CredentialMap expected = new CredentialMap(new DefaultCredential("otherone"));
        expected.put("somehost", new CertificateCredential("someone", "/home/someone/.ssh/somehost.key", "somepw".toCharArray()));
        assertEquals(expected, result);
    }

    @Test
    public void supportsVia_localAdaptor_false() throws UnknownAdaptorException {
        SchedulerAdaptorDescription description = Scheduler.getAdaptorDescription("local");

        assertFalse(supportsVia(description));
    }

    @Test
    public void supportsVia_sshAdaptor_true() throws UnknownAdaptorException {
        SchedulerAdaptorDescription description = Scheduler.getAdaptorDescription("ssh");

        assertTrue(supportsVia(description));
    }

    @Test
    public void validEnvironmentVariableName_path() {
        assertTrue(validEnvironmentVariableName("PATH"));
    }

    @Test
    public void validEnvironmentVariableName_javaHome() {
        assertTrue(validEnvironmentVariableName("JAVA_HOME"));
    }

    @Test
    public void validEnvironmentVariableName_foo1() {
        assertTrue(validEnvironmentVariableName("FOO1"));
    }

    @Test
    public void validEnvironmentVariableName_1foo() {
        assertFalse(validEnvironmentVariableName("1FOO"));
    }

    @Test
    public void validEnvironmentVariableName_bashFunction() {
        assertFalse(validEnvironmentVariableName("BASH_FUNC_module()"));
    }

    @Test
    public void validEnvironmentVariableName_bashFunctionShellshock() {
        assertFalse(validEnvironmentVariableName("BASH_FUNC_module%%"));
    }

    @Test
    public void createScheduler_InvalidLocationException_supportedLocations() throws XenonException {
        thrown.expect(InvalidLocationException.class);
        thrown.expectMessage(containsString("supported"));

        Map<String, Object> attrs = new HashMap<>();
        attrs.put("adaptor", "slurm");
        attrs.put("location", "foobar"); // should start with local:// or ssh://
        Namespace res = new Namespace(attrs);

        createScheduler(res);
    }

    @Test
    public void createFileSystem_InvalidLocationException_supportedLocations() throws XenonException {
        thrown.expect(InvalidLocationException.class);
        thrown.expectMessage(containsString("supported"));

        Map<String, Object> attrs = new HashMap<>();
        attrs.put("adaptor", "sftp");
        attrs.put("location", " ");
        Namespace res = new Namespace(attrs);

        createFileSystem(res);
    }

    @Test
    public void createScheduler_UnknownPropertyException_supportedProps() throws XenonException {
        thrown.expect(UnknownPropertyException.class);
        thrown.expectMessage(containsString("supported"));

        Map<String, Object> attrs = new HashMap<>();
        attrs.put("adaptor", "local");
        attrs.put("props", Collections.singletonList("foo=bar"));
        Namespace res = new Namespace(attrs);

        createScheduler(res);
    }

    @Test
    public void createFileSystem_UnknownPropertyException_supportedProps() throws XenonException {
        thrown.expect(UnknownPropertyException.class);
        thrown.expectMessage(containsString("supported"));

        Map<String, Object> attrs = new HashMap<>();
        attrs.put("adaptor", "file");
        attrs.put("props", Collections.singletonList("foo=bar"));
        Namespace res = new Namespace(attrs);

        createFileSystem(res);
    }

}