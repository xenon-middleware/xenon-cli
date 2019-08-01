package nl.esciencecenter.xenon.cli;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.testcontainers.containers.GenericContainer;

public class SshTest {
    private static final int PORT = 22;

    @ClassRule
    public static final GenericContainer server = new GenericContainer("xenonmiddleware/ssh").withExposedPorts(PORT);

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    private static String getLocation() {
        return server.getContainerIpAddress() + ":" + server.getMappedPort(PORT);
    }

    private static String[] argsBuilder(String... args) {
        String location = getLocation();
        String[] myargs = {
                "--stacktrace",
                "scheduler",
                "ssh",
                "--location", location,
                "--username", "xenon",
                "--password", "javagat",
                "--prop", "xenon.adaptors.schedulers.ssh.strictHostKeyChecking=false"
        };
        return Stream.concat(Arrays.stream(myargs), Arrays.stream(args)).toArray(String[]::new);
    }

    @Test
    public void exec() {
        exit.expectSystemExitWithStatus(0);
        exit.checkAssertionAfterwards(() -> {
            assertTrue("Hello World! in stdout", systemOutRule.getLog().contains("Hello World!"));
        });

        String[] args= argsBuilder(
                "exec",
                "/bin/echo",
                "--",
                "-n",
                "Hello",
                "World!"
        );
        Main main = new Main();
        main.run(args);
    }

    @Test
    public void exec_exit53() {
        exit.expectSystemExitWithStatus(53);

        String[] args = argsBuilder("exec", "/bin/bash", "--", "-c", "exit 53");
        Main main = new Main();
        main.run(args);
    }
}
