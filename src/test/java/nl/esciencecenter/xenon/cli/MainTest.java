package nl.esciencecenter.xenon.cli;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.esciencecenter.xenon.XenonException;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Test;

public class MainTest {
    @Test
    public void buildXenonProperties() throws Exception {
        Map<String, Object> attrs = new HashMap<>();
        List<String> propsIn = Arrays.asList("KEY1=VAL1", "KEY2=VAL2");
        attrs.put("props", propsIn);
        Namespace ns = new Namespace(attrs);

        Map<String, String> result = Main.buildXenonProperties(ns);
        Map<String, String> expected = new HashMap<>();
        expected.put("KEY1", "VAL1");
        expected.put("KEY2", "VAL2");
        assertEquals(expected, result);
    }

    @Test(expected = ArgumentParserException.class)
    public void mainRootHelp() throws XenonException, ArgumentParserException {
        String[] args = {"--help"};
        Main main = new Main();
        main.run(args);
    }
}