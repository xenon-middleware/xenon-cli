package nl.esciencecenter.xenon.cli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.esciencecenter.xenon.jobs.JobDescription;

import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

public class ParserHelpers {
    public static ArgumentGroup addCredentialArguments(ArgumentParser parser) {
        return addCredentialArguments(parser, "");
    }

    public static ArgumentGroup addCredentialArguments(ArgumentParser parser, String prefix) {
        ArgumentGroup credGroup = parser.addArgumentGroup("optional credential arguments");
        credGroup.addArgument("--" + prefix + "username").help("Username");
        credGroup.addArgument("--" + prefix + "password").help("Password or passphrase");
        credGroup.addArgument("--" + prefix + "certfile").help("Certificate file");
        return credGroup;
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
        if (!args.isEmpty()) {
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
}
