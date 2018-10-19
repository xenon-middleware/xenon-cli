package nl.esciencecenter.xenon.cli;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.stream.Stream;

import com.github.geowarin.junit.DockerRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class SshTest {
    private static final String PORT = "22/tcp";

    @ClassRule
    public static final DockerRule server = DockerRule.builder()
            .image("nlesc/xenon-ssh")
            .ports("22")
            .waitForPort(PORT)
            .build();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    private static String getLocation() {
        return server.getDockerHost() + ":" + server.getHostPort(PORT);
    }

    private static String[] argsBuilder(String... args) {
        String location = getLocation();
        String[] myargs = {
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
