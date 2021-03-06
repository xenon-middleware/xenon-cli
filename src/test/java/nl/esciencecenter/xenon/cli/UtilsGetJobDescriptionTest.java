package nl.esciencecenter.xenon.cli;

import static nl.esciencecenter.xenon.cli.Utils.getJobDescription;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Test;

import nl.esciencecenter.xenon.schedulers.JobDescription;

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
    public void tasks() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("tasks", 4);

        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setTasks(4);
        assertEquals(expected, description);
    }

    @Test
    public void tasks_per_node() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("tasks_per_node", 16);

        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setTasksPerNode(16);
        assertEquals(expected, description);
    }

    @Test
    public void cores_per_task() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("cores_per_task", 16);

        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setCoresPerTask(16);
        assertEquals(expected, description);
    }

    @Test
    public void start_per_task() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("start_per_task", true);
        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        JobDescription expected = new JobDescription();
        expected.setExecutable("sleep");
        expected.setStartPerTask();
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
        attrs.put("tasks", 1);
        attrs.put("cores_per_task", 1);
        attrs.put("start_per_task", false);
        return attrs;
    }

    @Test
    public void name() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("name", "myjobname");
        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        assertEquals("myjobname", description.getName());
    }

    @Test
    public void maxRuntime() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("max_memory", 4096);
        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        assertEquals(4096, description.getMaxMemory());
    }

    @Test
    public void schedulerArguments_zero() {
        Map<String, Object> attrs = defaultArgs();
        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        assertEquals(new ArrayList<String>() ,description.getSchedulerArguments());
    }

    @Test
    public void schedulerArguments_one() {
        Map<String, Object> attrs = defaultArgs();
        List<String> args = Collections.singletonList("--constraint=haswell");
        attrs.put("scheduler_arguments", args);
        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        assertEquals(args, description.getSchedulerArguments());
    }

    @Test
    public void tempSpace() {
        Map<String, Object> attrs = defaultArgs();
        attrs.put("temp_space", 4096); // Job requires 4Gb of temp space
        Namespace res = new Namespace(attrs);

        JobDescription description = getJobDescription(res);

        assertEquals(4096, description.getTempSpace());
    }
}
