package nl.esciencecenter.xenon.cli;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import nl.esciencecenter.xenon.cli.listjobs.ListJobsOutput;
import nl.esciencecenter.xenon.cli.queues.QueuesOutput;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SlurmTest {
    private static final String ADAPTOR_NAME = "slurm";
    @ClassRule
    public static final DockerComposeRule docker = DockerComposeRule.builder()
        .file("src/integrationTest/resources/slurm-docker-compose.yml")
        .saveLogsTo("build/dockerLogs/SlurmTest")
        .waitingForService(ADAPTOR_NAME, HealthChecks.toHaveAllPortsOpen())
        .build();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
    private Main main;

    private static String getLocation() {
        DockerPort slurm = docker.containers().container(ADAPTOR_NAME).port(22);
        return slurm.inFormat("$HOST:$EXTERNAL_PORT");
    }

    private static String[] argsBuilder(String... args) {
        String location = getLocation();
        String[] myargs = {
            "--username", "xenon",
            "--password", "javagat",
            ADAPTOR_NAME,
            "--location", location,
            "--prop", "xenon.adaptors.slurm.ignore.version=true"
        };
        return Stream.concat(Arrays.stream(myargs), Arrays.stream(args)).toArray(String[]::new);
    }

    @Before
    public void setUp() {
        main = new Main();
    }

    @Test
    public void exec_stdout() {
        String[] args = argsBuilder(
            "exec",
            "/bin/echo",
            "--",
            "-n",
            "Hello",
            "World!"
        );
        main.run(args);

        String expected = "Hello World!";
        assertTrue("Stdout contains string", systemOutRule.getLog().contains(expected));
    }

    @Test
    public void queues() {
        String[] args = argsBuilder("queues");
        QueuesOutput result = (QueuesOutput) main.run(args);

        QueuesOutput expected = new QueuesOutput(new String[]{"mypartition", "otherpartition"}, "mypartition");
        assertEquals(result, expected);
    }

    @Test
    public void listjobs() {
        String[] args = argsBuilder("list");
        ListJobsOutput jobs = (ListJobsOutput) main.run(args);

        String result = jobs.toString();
        String expected = "\n";
        assertEquals(expected, result);
    }

    @Test
    public void listjobs_byqueue() {
        String[] args = argsBuilder("list", "--queue", "mypartition");
        ListJobsOutput jobs = (ListJobsOutput) main.run(args);

        String result = jobs.toString();
        String expected = "\n";
        assertEquals(expected, result);
    }
}
