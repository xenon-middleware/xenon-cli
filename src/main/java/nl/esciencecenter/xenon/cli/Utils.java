package nl.esciencecenter.xenon.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.esciencecenter.xenon.filesystems.Path;
import nl.esciencecenter.xenon.schedulers.JobDescription;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Helpers for Xenon.jobs based commands
 */
public class Utils {
    /** The default buffer size to use for copy operations */
    private static final int DEFAULT_BUFFER_SIZE = 16 * 1024;

    Utils() {
        throw new IllegalAccessError("Utility class");
    }

    static Map<String, String> parseArgumentListAsMap(List<String> input) {
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
        JobDescription description = new JobDescription();

        String executable = res.get("executable");
        description.setExecutable(executable);

        List<String> args = res.getList("args");
        if (args != null && !args.isEmpty()) {
            description.setArguments(args.toArray(new String[0]));
        }

        String queue = res.getString("queue");
        if (queue != null) {
            description.setQueueName(queue);
        }

        Map<String, String> envs = parseArgumentListAsMap(res.getList("envs"));
        if (!envs.isEmpty()) {
            description.setEnvironment(envs);
        }

        Map<String, String> options = parseArgumentListAsMap(res.getList("options"));
        if (!options.isEmpty()) {
            description.setJobOptions(options);
        }

        int maxTime = res.getInt("max_time");
        description.setMaxTime(maxTime);

        int nodeCount = res.getInt("node_count");
        description.setNodeCount(nodeCount);

        int procsPerNode = res.getInt("procs_per_node");
        description.setProcessesPerNode(procsPerNode);

        String workingDirectory = res.getString("working_directory");
        if (workingDirectory != null) {
            description.setWorkingDirectory(workingDirectory);
        }

        String stdin = res.getString("stdin");
        if (stdin != null) {
            description.setStdin(stdin);
        }

        String stdout = res.getString("stdout");
        if (stdout != null) {
            description.setStdout(stdout);
        }

        String stderr = res.getString("stderr");
        if (stderr != null) {
            description.setStderr(stderr);
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

    private static boolean isRelativeLocalPath(String adaptorName, String path) {
        return ("local".equals(adaptorName) || "file".equals(adaptorName)) && !(path.startsWith("~") || path.startsWith("/") || "-".equals(path));
    }

    public static Path getAbsolutePath(String adaptorName, String path)  {
        Path apath = new Path(path);
        if (isRelativeLocalPath(adaptorName, path)) {
            // Path is relative to working directory, make it absolute
            Path workingDirectory = new Path(System.getProperty("user.dir"));
            apath = workingDirectory.resolve(apath);
        }
        return apath;
    }
}
