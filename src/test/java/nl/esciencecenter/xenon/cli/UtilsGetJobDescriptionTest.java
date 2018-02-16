package nl.esciencecenter.xenon.cli;

import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.schedulers.JobDescription;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nl.esciencecenter.xenon.cli.Utils.getJobDescription;
import static org.junit.Assert.assertEquals;

public class UtilsGetJobDescriptionTest {
    @Test
    public void defaults() {
        Map<String, Object> attrs = defaultArgs();
        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        assertEquals(expected, description);
    }

    @Test
    public void one_args() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("args", Collections.singletonList("600"));
        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setArguments("600");
        assertEquals(expected, description);
    }

    @Test
    public void some_queue() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("queue", "veryfast");
        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setQueueName("veryfast");
        assertEquals(expected, description);
    }

    @Test
    public void some_envs() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("envs", Collections.singletonList("SOMEVAR=somevalue"));
        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setEnvironment(Collections.singletonMap("SOMEVAR", "somevalue"));
        assertEquals(expected, description);
    }

    @Test
    public void options() {
        Map<String, Object> attrs = defaultArgs();
        List<String> options = Collections.singletonList("KEY1=VAL1");
        attrs.put("options", options);
        Namespace res = new Namespace(attrs);
        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setJobOptions(Collections.singletonMap("KEY1", "VAL1"));
        assertEquals(expected, description);
    }

    @Test
    public void max_run_time() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("max_run_time", 60);

        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setMaxRuntime(60);
        assertEquals(expected, description);
    }

    @Test
    public void node_count() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("node_count", 4);

        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setNodeCount(4);
        assertEquals(expected, description);
    }

    @Test
    public void procs_per_node() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("procs_per_node", 16);

        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setProcessesPerNode(16);
        assertEquals(expected, description);
    }

    @Test
    public void start_single_process() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("start_single_process", true);
        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setStartSingleProcess(true);
        assertEquals(expected, description);
    }

    @Test
    public void working_directory() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("working_directory", "/data");
        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setWorkingDirectory("/data");
        assertEquals(expected, description);
    }
    @Test
    public void stdin() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("stdin", "input.txt");
        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setStdin("input.txt");
        assertEquals(expected, description);
    }

    @Test
    public void stdout() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("stdout", "output.txt");
        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setStdout("output.txt");
        assertEquals(expected, description);
    }

    @Test
    public void stderr() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("stderr", "err.log");
        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setStderr("err.log");
        assertEquals(expected, description);
    }

    private Map<String, Object> defaultArgs() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("executable", "sleep");
        attrs.put("inherit_env", false);
        attrs.put("max_run_time", JobDescription.DEFAULT_MAX_RUN_TIME_IN_MINUTES);
        attrs.put("node_count", 1);
        attrs.put("procs_per_node", 1);
        attrs.put("start_single_process", false);
        return attrs;
    }
}
