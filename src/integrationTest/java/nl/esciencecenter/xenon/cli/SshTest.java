package nl.esciencecenter.xenon.cli;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import nl.esciencecenter.xenon.XenonException;

import com.github.geowarin.junit.DockerRule;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class SshTest {
    public static String PORT = "22/tcp";

    @ClassRule
    public static DockerRule server = DockerRule.builder()
            .image("nlesc/xenon-ssh")
            .ports("22")
            .waitForPort(PORT)
            .build();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    public static String getLocation() {
        return server.getDockerHost() + ":" + server.getHostPort(PORT);
    }

    public static String[] argsBuilder(String... args) {
        String location = getLocation();
        String[] myargs = {
                "--username", "xenon",
                "--password", "javagat",
                "ssh",
                "--location", location
        };
        return Stream.concat(Arrays.stream(myargs), Arrays.stream(args)).toArray(String[]::new);
    }

    @Test
    public void exec() throws IOException, XenonException, ArgumentParserException {
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

        assertEquals("Hello World!", systemOutRule.getLog());
    }
}
