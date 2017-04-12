package nl.esciencecenter.xenon.cli;

import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.jobs.JobDescription;
import org.junit.Test;

import java.util.*;

import static nl.esciencecenter.xenon.cli.JobsUtils.getJobDescription;
import static org.junit.Assert.*;

public class ParserHelpersTest {
    @Test
    public void getJobDescriptione() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("executable", "/bin/true");
        map.put("args", null);
        map.put("queue", null);
        map.put("envs", new ArrayList<String>());
        map.put("options", new ArrayList<String>());
        map.put("max_time", 1);
        map.put("node_count", 1);
        map.put("procs_per_node", 1);
        map.put("working_directory", "/tmp");

        Namespace res = new Namespace(map);
        JobDescription result = getJobDescription(res);

        assertNull(result.getQueueName());
    }

    @Test
    public void getJobDescription_withQueue() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("executable", "/bin/true");
        map.put("args", null);
        map.put("queue", "gpu");
        map.put("envs", new ArrayList<String>());
        map.put("options", new ArrayList<String>());
        map.put("max_time", 1);
        map.put("node_count", 1);
        map.put("procs_per_node", 1);
        map.put("working_directory", "/tmp");

        Namespace res = new Namespace(map);
        JobDescription result = getJobDescription(res);

        assertEquals("gpu", result.getQueueName());
    }

    @Test
    public void getJobDescriptione_withEnvs() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("executable", "/bin/true");
        map.put("args", null);
        map.put("queue", null);
        List<String> envs = Arrays.asList("KEY1=VAL1");
        map.put("envs", envs);
        map.put("options", new ArrayList<String>());
        map.put("max_time", 1);
        map.put("node_count", 1);
        map.put("procs_per_node", 1);
        map.put("working_directory", "/tmp");

        Namespace res = new Namespace(map);
        JobDescription result = getJobDescription(res);

        HashMap<String, String> expected = new HashMap<>();
        expected.put("KEY1", "VAL1");
        assertEquals(expected, result.getEnvironment());
    }

    @Test
    public void getJobDescriptione_withOptions() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("executable", "/bin/true");
        map.put("args", null);
        map.put("queue", null);
        map.put("envs", new ArrayList<String>());
        List<String> options = Arrays.asList("KEY1=VAL1");
        map.put("options", options);
        map.put("max_time", 1);
        map.put("node_count", 1);
        map.put("procs_per_node", 1);
        map.put("working_directory", "/tmp");

        Namespace res = new Namespace(map);
        JobDescription result = getJobDescription(res);

        HashMap<String, String> expected = new HashMap<>();
        expected.put("KEY1", "VAL1");
        assertEquals(expected, result.getJobOptions());
    }
}