package nl.esciencecenter.xenon.cli;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.listjobs.ListJobsOutput;
import nl.esciencecenter.xenon.cli.queues.QueuesOutput;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class SlurmTest {
    @ClassRule
    public static DockerComposeRule docker = DockerComposeRule.builder()
        .file("src/integrationTest/resources/slurm-docker-compose.yml")
        .saveLogsTo("build/dockerLogs/SlurmTest")
        .waitingForService("slurm", HealthChecks.toHaveAllPortsOpen())
        .build();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
    private Main main;

    public static String getLocation() {
        DockerPort slurm = docker.containers().container("slurm").port(22);
        return slurm.inFormat("$HOST:$EXTERNAL_PORT");
    }

    public static String[] argsBuilder(String... args) {
        String location = getLocation();
        String[] myargs = {
            "--username", "xenon",
            "--password", "javagat",
            "slurm",
            "--location", location
        };
        return Stream.concat(Arrays.stream(myargs), Arrays.stream(args)).toArray(String[]::new);
    }

    @Before
    public void setUp() throws XenonException {
        main = new Main();
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

    @Ignore("Sometimes works, but if not gives XenonException: slurm adaptor: Failed to submit interactive job. Interactive job status is DONE exit code = 0")
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
        main.run(args);

        assertEquals("Hello World!", systemOutRule.getLog());
    }

    @Test
    public void queues() throws IOException, XenonException, ArgumentParserException {
        String[] args= argsBuilder("queues");
        QueuesOutput result = (QueuesOutput) main.run(args);

        QueuesOutput expected = new QueuesOutput(new String[] {"mypartition", "otherpartition"},"mypartition");
        assertEquals(result, expected);
    }
}
