package nl.esciencecenter.xenon.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.stream.Stream;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.queues.QueuesOutput;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

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
    public void setUp() throws XenonException {
        main = new Main();
    }

    @Test
    public void exec() throws XenonException {
        String[] args= argsBuilder(
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
    public void queues() throws XenonException {
        String[] args= argsBuilder("queues");
        QueuesOutput result = (QueuesOutput) main.run(args);

        QueuesOutput expected = new QueuesOutput(new String[] {"mypartition", "otherpartition"},"mypartition");
        assertEquals(result, expected);
    }
}
