package nl.esciencecenter.xenon.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.queues.QueuesOutput;

import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class MainTest {
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void buildXenonProperties() throws Exception {
        Map<String, Object> attrs = new HashMap<>();
        List<String> propsIn = Arrays.asList("KEY1=VAL1", "KEY2=VAL2");
        attrs.put("props", propsIn);
        Namespace ns = new Namespace(attrs);

        Set<String> allowedKeys = new HashSet<>(Arrays.asList("KEY1", "KEY2"));
        Map<String, String> result = Main.buildXenonProperties(ns, allowedKeys);
        Map<String, String> expected = new HashMap<>();
        expected.put("KEY1", "VAL1");
        expected.put("KEY2", "VAL2");
        assertEquals(expected, result);
    }

    @Test
    public void mainRootHelp() throws XenonException {
        String[] args = {"--help"};
        Main main = new Main();
        main.run(args);

        assertTrue("System out starts with 'usage: xenon'", systemOutRule.getLog().startsWith("usage: xenon"));
    }

    @Test
    public void print_defaultFormat() throws XenonException {
        QueuesOutput queues = new QueuesOutput(new String[]{"quick", "default"}, "default");
        Main main = new Main();

        main.print(queues, "default");

        String stdout = systemOutRule.getLogWithNormalizedLineSeparator();
        String expected = "Available queues: quick, default\nDefault queue: default\n";
        assertEquals(expected, stdout);
    }

    @Test
    public void print_cwljsonFormat() throws XenonException {
        QueuesOutput queues = new QueuesOutput(new String[]{"quick", "default"}, "default");
        Main main = new Main();

        main.print(queues, "cwljson");

        String stdout = systemOutRule.getLogWithNormalizedLineSeparator();
        String expected = "{\n" +
            "  \"defaultQueue\": \"default\",\n" +
            "  \"queues\": [\n" +
            "    \"quick\",\n" +
            "    \"default\"\n" +
            "  ]\n" +
            "}";
        assertEquals(expected, stdout);
    }
}