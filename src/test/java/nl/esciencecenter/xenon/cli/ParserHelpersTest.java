package nl.esciencecenter.xenon.cli;

import net.sourceforge.argparse4j.ArgumentParserBuilder;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import nl.esciencecenter.xenon.credentials.CertificateCredential;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.credentials.DefaultCredential;
import nl.esciencecenter.xenon.credentials.PasswordCredential;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static nl.esciencecenter.xenon.cli.ParserHelpers.addCredentialArguments;
import static nl.esciencecenter.xenon.cli.ParserHelpers.addTargetCredentialArguments;
import static nl.esciencecenter.xenon.cli.ParserHelpers.addViaCredentialArguments;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class ParserHelpersTest {
    private ArgumentParser parser;
    private ArgumentGroup targetparser;

    @Before
    public void setUp() {
        parser = ArgumentParsers.newFor(BuildConfig.NAME).build();
        targetparser = parser.addArgumentGroup("target");
    }

    private class DummyCredential implements Credential {

    }

    @Test
    public void addCredentialArguments_dummy_none() {
        HashSet<Class> supportedCredentials = new HashSet<>(Collections.singletonList(DummyCredential.class));

        addCredentialArguments(parser, supportedCredentials);

        String usage = parser.formatUsage();
        assertThat(usage, allOf(
                not(containsString("--username")),
                not(containsString("--password")),
                not(containsString("--certfile"))
        ));
    }

    @Test
    public void addCredentialArguments_default_username() {
        HashSet<Class> supportedCredentials = new HashSet<>(Collections.singletonList(DefaultCredential.class));

        addCredentialArguments(parser, supportedCredentials);

        String usage = parser.formatUsage();
        assertThat(usage, allOf(
                containsString("--username"),
                not(containsString("--password")),
                not(containsString("--certfile"))
        ));
    }

    @Test
    public void addCredentialArguments_password_usernamepassword() {
        HashSet<Class> supportedCredentials = new HashSet<>(Collections.singletonList(PasswordCredential.class));

        addCredentialArguments(parser, supportedCredentials);

        String usage = parser.formatUsage();
        assertThat(usage, allOf(
                containsString("--username"),
                containsString("--password"),
                not(containsString("--certfile"))
        ));
    }

    @Test
    public void addCredentialArguments_cert_usernamepasswordcert() {
        HashSet<Class> supportedCredentials = new HashSet<>(Collections.singletonList(CertificateCredential.class));

        addCredentialArguments(parser, supportedCredentials);

        String usage = parser.formatUsage();
        assertThat(usage, allOf(
                containsString("--username"),
                containsString("--password"),
                containsString("--certfile")
        ));
    }

    @Test
    public void addViaCredentialArguments_dummy_none() {
        HashSet<Class> supportedCredentials = new HashSet<>(Collections.singletonList(DummyCredential.class));

        addViaCredentialArguments(parser, supportedCredentials);

        String usage = parser.formatUsage();
        assertThat(usage, not(containsString("--via-username")));
        assertThat(usage, not(containsString("--via-password")));
        assertThat(usage, not(containsString("--via-certfile")));
    }

    @Test
    public void addViaCredentialArguments_default_username() {
        HashSet<Class> supportedCredentials = new HashSet<>(Collections.singletonList(DefaultCredential.class));

        addViaCredentialArguments(parser, supportedCredentials);

        String usage = parser.formatUsage();
        assertThat(usage, containsString("--via-username"));
        assertThat(usage, not(containsString("--via-password")));
        assertThat(usage, not(containsString("--via-certfile")));
    }

    @Test
    public void addViaCredentialArguments_password_usernamepassword() {
        HashSet<Class> supportedCredentials = new HashSet<>(Collections.singletonList(PasswordCredential.class));

        addViaCredentialArguments(parser, supportedCredentials);

        String usage = parser.formatUsage();
        assertThat(usage, containsString("--via-username"));
        assertThat(usage, containsString("--via-password"));
        assertThat(usage, not(containsString("--via-certfile")));
    }

    @Test
    public void addViaCredentialArguments_cert_usernamepasswordcert() {
        HashSet<Class> supportedCredentials = new HashSet<>(Collections.singletonList(CertificateCredential.class));

        addViaCredentialArguments(parser, supportedCredentials);

        String usage = parser.formatUsage();
        assertThat(usage, containsString("--via-username"));
        assertThat(usage, containsString("--via-password"));
        assertThat(usage, containsString("--via-certfile"));
    }

    @Test
    public void addTargetCredentialArguments_dummy_none() {
        HashSet<Class> supportedCredentials = new HashSet<>(Collections.singletonList(DummyCredential.class));

        addTargetCredentialArguments(targetparser, supportedCredentials);

        String usage = parser.formatUsage();
        assertThat(usage, not(containsString("--target-username")));
        assertThat(usage, not(containsString("--target-password")));
        assertThat(usage, not(containsString("--target-certfile")));
    }

    @Test
    public void addTargetCredentialArguments_default_username() {
        HashSet<Class> supportedCredentials = new HashSet<>(Collections.singletonList(DefaultCredential.class));

        addTargetCredentialArguments(targetparser, supportedCredentials);

        String usage = parser.formatUsage();
        assertThat(usage, containsString("--target-username"));
        assertThat(usage, not(containsString("--target-password")));
        assertThat(usage, not(containsString("--target-certfile")));
    }

    @Test
    public void addTargetCredentialArguments_password_usernamepassword() {
        HashSet<Class> supportedCredentials = new HashSet<>(Collections.singletonList(PasswordCredential.class));

        addTargetCredentialArguments(targetparser, supportedCredentials);

        String usage = parser.formatUsage();
        assertThat(usage, containsString("--target-username"));
        assertThat(usage, containsString("--target-password"));
        assertThat(usage, not(containsString("--target-certfile")));
    }

    @Test
    public void addTargetCredentialArguments_cert_usernamepasswordcert() {
        HashSet<Class> supportedCredentials = new HashSet<>(Collections.singletonList(CertificateCredential.class));

        addTargetCredentialArguments(targetparser, supportedCredentials);

        String usage = parser.formatUsage();
        assertThat(usage, containsString("--target-username"));
        assertThat(usage, containsString("--target-password"));
        assertThat(usage, containsString("--target-certfile"));
    }
}