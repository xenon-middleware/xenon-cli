package nl.esciencecenter.xenon.cli;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import nl.esciencecenter.xenon.XenonException;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class SlurmTest {
    @ClassRule
    public static DockerComposeRule docker = DockerComposeRule.builder()
        .file("src/test/resources/slurm-docker-compose.yml")
        .saveLogsTo("build/dockerLogs/SlurmTest")
        .waitingForService("slurm", HealthChecks.toHaveAllPortsOpen())
//        .addClusterWait(new ClusterWait(serviceHealthCheck("slurm", HealthChecks.toHaveAllPortsOpen()), Duration.standardMinutes(5)))
        .build();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    public static String getLocation() {
        DockerPort slurm = docker.containers().container("slurm").port(22);
        System.err.println(slurm.inFormat("$HOST:$EXTERNAL_PORT"));
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
