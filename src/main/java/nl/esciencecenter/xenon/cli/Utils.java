package nl.esciencecenter.xenon.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.esciencecenter.xenon.schedulers.JobDescription;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Helpers for Xenon.jobs based commands
 */
public class Utils {
    /** The default buffer size to use for copy operations */
    private static final int DEFAULT_BUFFER_SIZE = 16 * 1024;

    private Utils() {
        throw new IllegalAccessError("Utility class");
    }

    public static Map<String, String> parseArgumentListAsMap(List<String> input) {
        Map<String, String> output = new HashMap<>();
        if (input != null) {
            for (String prop : input) {
                String[] keyval = prop.split("=", 2);
                output.put(keyval[0], keyval[1]);
            }
        }
        return output;
    }

    public static JobDescription getJobDescription(Namespace res) {
        String executable = res.get("executable");
        List<String> args = res.getList("args");
        String queue = res.getString("queue");
        Map<String, String> envs = parseArgumentListAsMap(res.getList("envs"));
        Map<String, String> options = parseArgumentListAsMap(res.getList("options"));
        int maxTime = res.getInt("max_time");
        int nodeCount = res.getInt("node_count");
        int procsPerNode = res.getInt("procs_per_node");
        String workingDirectory = res.getString("working_directory");


        JobDescription description = new JobDescription();
        description.setExecutable(executable);
        if (args != null && !args.isEmpty()) {
            description.setArguments(args.toArray(new String[0]));
        }
        if (queue != null) {
            description.setQueueName(queue);
        }
        if (!envs.isEmpty()) {
            description.setEnvironment(envs);
        }
        if (!options.isEmpty()) {
            description.setJobOptions(options);
        }
        description.setMaxTime(maxTime);
        description.setNodeCount(nodeCount);
        description.setProcessesPerNode(procsPerNode);
        if (workingDirectory != null) {
            description.setWorkingDirectory(workingDirectory);
        }
        return description;
    }

    public static long pipe(InputStream in, OutputStream out) throws IOException {
        long bytes = 0;

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int len = in.read(buffer);
        while (len != -1) {
            bytes += len;
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }
        return bytes;
    }

    public static boolean isRelativeLocalPath(String adaptorName, String path) {
        return "local".equals(adaptorName) || "file".equals(adaptorName) && !(path.startsWith("~") || path.startsWith("/") || "-".equals(path));
    }
}
