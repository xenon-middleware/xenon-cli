package nl.esciencecenter.xenon.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.stream.Stream;

import nl.esciencecenter.xenon.cli.listjobs.ListJobsOutput;
import nl.esciencecenter.xenon.cli.queues.QueuesOutput;
import nl.esciencecenter.xenon.cli.submit.SubmitOutput;

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
            .waitingForService(ADAPTOR_NAME, HealthChecks.toHaveAllPortsOpen())
            .build();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
    private Main main;

    private static String getLocation() {
        DockerPort slurm = docker.containers().container(ADAPTOR_NAME).port(22);
        return slurm.inFormat("ssh://$HOST:$EXTERNAL_PORT");
    }

    private static String[] argsBuilder(String... args) {
        String location = getLocation();
        String[] myargs = {
                "scheduler",
                ADAPTOR_NAME,
                "--location", location,
                "--username", "xenon",
                "--password", "javagat",
                "--prop", "xenon.adaptors.filesystems.sftp.strictHostKeyChecking=false",
                "--prop", "xenon.adaptors.filesystems.sftp.autoAddHostKey=false"
        };
        return Stream.concat(Arrays.stream(myargs), Arrays.stream(args)).toArray(String[]::new);
    }

    @Before
    public void setUp() {
        main = new Main();
    }

    @Test
    public void submitListRemoveList_long() {
        String[] submitArgs = argsBuilder(
                "submit",
                "--long",
                "/bin/sleep",
                "120"
        );
        SubmitOutput submitOutput = (SubmitOutput) main.run(submitArgs);
        assertNotNull("Job id is filled", submitOutput.jobId);

        String[] listArgs = argsBuilder("list");
        ListJobsOutput listOutput = (ListJobsOutput) main.run(listArgs);
        boolean containsId = listOutput.statuses.stream().anyMatch(c -> submitOutput.jobId.equals(c.getJobIdentifier()));
        assertTrue("List contains running job", containsId);

        String[] removeArgs = argsBuilder(
                "remove",
                submitOutput.jobId
        );
        main.run(removeArgs);
    }

    @Test
    public void submitListRemoveList() {
        String[] submitArgs = argsBuilder(
            "submit",
            "/bin/sleep",
            "120"
        );
        String jobId = (String) main.run(submitArgs);
        assertNotNull("Job id is filled", jobId);

        String[] listArgs = argsBuilder("list");
        ListJobsOutput listOutput = (ListJobsOutput) main.run(listArgs);
        boolean containsId = listOutput.statuses.stream().anyMatch(c -> jobId.equals(c.getJobIdentifier()));
        assertTrue("List contains running job", containsId);

        String[] removeArgs = argsBuilder(
            "remove",
            jobId
        );
        main.run(removeArgs);
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
        String sep = System.getProperty("line.separator");
        String expected = "Job identifier\tState\tRunning\tDone\tError\tExit code\tInformation" + sep;
        assertEquals(expected, result);
    }

    @Test
    public void listjobs_byqueue() {
        String[] args = argsBuilder("list", "--queue", "mypartition");
        ListJobsOutput jobs = (ListJobsOutput) main.run(args);

        String result = jobs.toString();
        String sep = System.getProperty("line.separator");
        String expected = "Job identifier\tState\tRunning\tDone\tError\tExit code\tInformation" + sep;
        assertEquals(expected, result);
    }
}
