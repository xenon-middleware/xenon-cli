package nl.esciencecenter.xenon.cli;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.XenonException;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MainTest {

    @Test
    public void buildXenonProperties() throws Exception {
        Main main = new Main();
        Map<String, Object> attrs = new HashMap<>();
        List<String> propsIn = Arrays.asList(new String[]{"KEY1=VAL1", "KEY2=VAL2"});
        attrs.put("props", propsIn);
        Namespace ns = new Namespace(attrs);

        Map<String, String> result = main.buildXenonProperties(ns);
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

    @Test
    public void copyLocalFile() throws XenonException, ArgumentParserException {
        String[] args = {"file", "copy", "--overwrite", "README.md", "/tmp/copy-of-README.md"};
        Main main = new Main();
        main.run(args);
        File f = new File("/tmp/copy-of-README.md");
        assertTrue(f.isFile());
    }
}