package nl.esciencecenter.xenon.cli;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.stream.Stream;

import com.github.geowarin.junit.DockerRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
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
                "--password", "javagat"
        };
        return Stream.concat(Arrays.stream(myargs), Arrays.stream(args)).toArray(String[]::new);
    }

    @Test
    public void exec() {
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

        assertTrue("Hello World! in stdout", systemOutRule.getLog().contains("Hello World!"));
    }
}
