package nl.esciencecenter.xenon.cli;

import com.github.geowarin.junit.DockerRule;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import nl.esciencecenter.xenon.XenonException;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore("TODO test jobs commands which require batch scheduler")
public class GridEngineTest {
    public static String SSH_PORT = "22/tcp";
    public static String GE_PORT = "6444/tcp";

    private Main main = new Main();

    @ClassRule
    public static DockerRule server = DockerRule.builder()
            .image("nlesc/xenon-gridengine")
            .ports("22", "6444")
            .waitForPort(GE_PORT, 100000)
            .build();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    public GridEngineTest() throws XenonException {
    }

    public static String getLocation() {
        return server.getDockerHost() + ":" + server.getHostPort(SSH_PORT);
    }

    public static String[] argsBuilder(String... args) {
        String location = getLocation();
        String[] myargs = {
                "--username", "xenon",
                "--password", "javagat",
                "ge",
                "--location", location
        };
        return Stream.concat(Arrays.stream(myargs), Arrays.stream(args)).toArray(String[]::new);
    }

    @Test
    public void queues() throws IOException, XenonException, ArgumentParserException {
        String[] args= argsBuilder(
                "queues"
        );
        QueuesOutput output = (QueuesOutput) main.run(args);

        QueuesOutput expected = new QueuesOutput(new String[]{"default", "slow"}, null);
        assertEquals(expected, output);
    }

    @Test
    public void list_nojobs_emptylist() throws IOException, XenonException, ArgumentParserException {
        String[] args= argsBuilder(
                "list"
        );
        ListJobsOutput output = (ListJobsOutput) main.run(args);

        ListJobsOutput expected = new ListJobsOutput(getLocation(), null, new ArrayList<String>());
        assertEquals(expected, output);
    }

    @Test(expected = XenonException.class)
    public void remove_nojobs_throwsException() throws IOException, XenonException, ArgumentParserException {
        String[] args= argsBuilder(
                "remove",
                "1234"
        );
        main.run(args);
    }

    @Test
    public void submit() throws IOException, XenonException, ArgumentParserException {
        String[] args= argsBuilder(
                "submit",
                "--stdout", "stdout1.txt",
                "--stderr", "stderr1.txt",
                "/bin/echo",
                "--",
                "-n",
                "Hello",
                "World!"
        );
        SubmitOutput output = (SubmitOutput) main.run(args);

        assertNotNull(output.jobId);
    }
}
