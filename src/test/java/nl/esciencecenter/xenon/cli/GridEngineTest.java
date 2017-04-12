package nl.esciencecenter.xenon.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.listjobs.ListJobsOutput;
import nl.esciencecenter.xenon.cli.queues.QueuesOutput;
import nl.esciencecenter.xenon.cli.submit.SubmitOutput;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class GridEngineTest {
    @ClassRule
    public static DockerComposeRule docker = DockerComposeRule.builder()
            .file("src/test/resources/sge-docker-compose.yml")
            .saveLogsTo("build/dockerLogs/SgeTest")
            .waitingForService("sge", HealthChecks.toHaveAllPortsOpen())
            .build();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
    private Main main;

    public static String getLocation() {
        DockerPort sge = docker.containers().container("sge").port(22);
        return sge.inFormat("$HOST:$EXTERNAL_PORT");
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

    @Before
    public void setUp() throws XenonException {
        main = new Main();
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

        ListJobsOutput expected = new ListJobsOutput(getLocation(), null, new ArrayList<>());
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

        // clean up
        main.run(argsBuilder("remove", output.jobId));
    }
}
