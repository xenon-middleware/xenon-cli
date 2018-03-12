package nl.esciencecenter.xenon.cli;

import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.AdaptorDescription;
import nl.esciencecenter.xenon.InvalidLocationException;
import nl.esciencecenter.xenon.UnknownPropertyException;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.*;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.FileSystemAdaptorDescription;
import nl.esciencecenter.xenon.schedulers.JobDescription;
import nl.esciencecenter.xenon.schedulers.Scheduler;
import nl.esciencecenter.xenon.schedulers.SchedulerAdaptorDescription;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static nl.esciencecenter.xenon.cli.ParserHelpers.getSupportedLocationHelp;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getSupportedPropertiesHelp;

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
                int isIndex = prop.indexOf('=');
                if (isIndex > 0 && prop.length() > isIndex) {
                    // the key is not allowed to have a '=' in it,
                    // so 'k1=v1=v2' will result in key is 'k1' and val is 'v1=v2'
                    String key = prop.substring(0, isIndex);
                    String value = prop.substring(isIndex + 1);
                    output.put(key, value);
                }
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
        Boolean inheritEnv = res.getBoolean("inherit_env");
        if (inheritEnv) {
            for (Map.Entry<String, String> env : System.getenv().entrySet()) {
                if (validEnvironmentVariableName(env.getKey())) {
                    envs.putIfAbsent(env.getKey(), env.getValue());
                }
            }
        }
        if (!envs.isEmpty()) {
            description.setEnvironment(envs);
        }

        Map<String, String> options = parseArgumentListAsMap(res.getList("options"));
        if (!options.isEmpty()) {
            description.setJobOptions(options);
        }

        int maxTime = res.getInt("max_run_time");
        description.setMaxRuntime(maxTime);

        int nodeCount = res.getInt("node_count");
        description.setNodeCount(nodeCount);

        int procsPerNode = res.getInt("procs_per_node");
        description.setProcessesPerNode(procsPerNode);

        if (res.getBoolean("start_single_process")) {
            description.setStartSingleProcess(true);
        }

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

        String name = res.getString("name");
        if (name != null) {
            description.setName(name);
        }
        Integer maxMemory = res.getInt("max_memory");
        if (maxMemory != null) {
            description.setMaxMemory(maxMemory);
        }

        List<String> schedulerArgs = res.getList("scheduler_arguments");
        if (!schedulerArgs.isEmpty()) {
            description.setSchedulerArguments(schedulerArgs.toArray(new String[0]));
        }

        return description;
    }

    static boolean validEnvironmentVariableName(final String key) {
        String regex = "^[0-9A-Z_]+$";
        return Pattern.matches(regex, key) && !Character.isDigit(key.charAt(0));
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

    public static Scheduler createScheduler(Namespace res) throws XenonException {
        String adaptor = res.getString("adaptor");
        String location = res.getString("location");
        Credential credential = createCredential(res);

        Map<String, String> props = buildXenonProperties(res);
        SchedulerAdaptorDescription adaptorDescription = Scheduler.getAdaptorDescription(adaptor);
        try {
            return Scheduler.create(adaptor, location, credential, props);
        } catch (InvalidLocationException|UnknownPropertyException e) {
            rethrowWithSupported(adaptorDescription, e);
            return null;
        }
    }

    public static FileSystem createFileSystem(Namespace res) throws XenonException {
        String adaptor = res.getString("adaptor");
        String location = res.getString("location");
        Credential credential = createCredential(res);

        Map<String, String> props = buildXenonProperties(res);
        FileSystemAdaptorDescription adaptorDescription = FileSystem.getAdaptorDescription(adaptor);
        try {
            return FileSystem.create(adaptor, location, credential, props);
        } catch (InvalidLocationException|UnknownPropertyException e) {
            rethrowWithSupported(adaptorDescription, e);
            return null;
        }
    }

    static Credential createCredential(Namespace res) {
        return createCredential(res, "");
    }

    static Credential createCredential(Namespace res, String prefix) {
        String username = res.getString(prefix + "username");
        if (username == null && !"".equals(prefix)) {
            username = res.getString("username");
        }
        String passwordAsString = res.getString(prefix + "password");
        if (passwordAsString == null && !"".equals(prefix)) {
            passwordAsString = res.getString("password");
        }
        String certfile = res.getString(prefix + "certfile");
        if (certfile == null && !"".equals(prefix)) {
            certfile = res.getString("certfile");
        }
        String keytabfile = res.getString(prefix + "keytabfile");
        if (keytabfile == null && !"".equals(prefix)) {
            keytabfile = res.getString("keytabfile");
        }
        Map<String, UserCredential> vias = createViaCredentials(res, username, passwordAsString, certfile, keytabfile);
        UserCredential cred = createCredential(username, passwordAsString, certfile, keytabfile);
        if (!vias.isEmpty()) {
            CredentialMap credMap = new CredentialMap(cred);
            for (Map.Entry<String, UserCredential> entry : vias.entrySet()) {
                credMap.put(entry.getKey(), entry.getValue());
            }
            return credMap;
        }
        return cred;
    }

    private static Map<String,UserCredential> createViaCredentials(Namespace res, String defaultUsername, String defaultPasswordAsString, String defaultCertfile, String defaultKeytabfile) {
        Map<String, String> viaUsernames = parseArgumentListAsMap(res.getList("via_usernames"));
        Map<String, String> viaPasswords = parseArgumentListAsMap(res.getList("via_passwords"));
        Map<String, String> viaCertfiles = parseArgumentListAsMap(res.getList("via_certfiles"));
        Map<String, String> viaKeytabfiles = parseArgumentListAsMap(res.getList("via_keytabfiles"));
        Set<String> hosts = new HashSet<>();
        hosts.addAll(viaUsernames.keySet());
        hosts.addAll(viaPasswords.keySet());
        hosts.addAll(viaCertfiles.keySet());
        hosts.addAll(viaKeytabfiles.keySet());
        Map<String, UserCredential> creds = new HashMap<>();
        for (String host : hosts) {
            String username = viaUsernames.getOrDefault(host, defaultUsername);
            String password = viaPasswords.getOrDefault(host, defaultPasswordAsString);
            String certfile = viaCertfiles.getOrDefault(host, defaultCertfile);
            String keytabfile = viaKeytabfiles.getOrDefault(host, defaultKeytabfile);
            creds.put(host, createCredential(username, password, certfile, keytabfile));
        }
        return creds;
    }

    private static UserCredential createCredential(String username, String passwordAsString, String certfile, String keytabfile) {
        char[] password = null;
        if (passwordAsString != null) {
            password = passwordAsString.toCharArray();
        }
        if (keytabfile != null) {
            return new KeytabCredential(username, keytabfile);
        } else if (certfile != null) {
            return new CertificateCredential(username, certfile, password);
        } else if (password != null) {
            return new PasswordCredential(username, password);
        } else if (username != null) {
            return new DefaultCredential(username);
        } else {
            return new DefaultCredential();
        }
    }

    public static boolean isLocalAdaptor(AdaptorDescription adaptorDescription) {
        return adaptorDescription.getName().equals("file") || adaptorDescription.getName().equals("local");
    }

    public static Map<String,String> buildXenonProperties(Namespace res) {
        return buildXenonProperties(res.getList("props"));
    }

    public static Map<String,String> buildTargetXenonProperties(Namespace res) {
        return buildXenonProperties(res.getList("target_props"));
    }

    private static Map<String, String> buildXenonProperties(List<String> props) {
        return parseArgumentListAsMap(props).entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    static boolean supportsVia(AdaptorDescription adaptorDescription) {
        return Arrays.stream(adaptorDescription.getSupportedLocations()).anyMatch(d -> d.contains("via:"));
    }

    private static void rethrowWithSupported(AdaptorDescription adaptorDescription, XenonException e) throws XenonException {
        if (e instanceof InvalidLocationException) {
            String message = e.getMessage();
            String[] supportedLocations = adaptorDescription.getSupportedLocations();
            message += " (" + getSupportedLocationHelp(supportedLocations) + ")";
            throw new InvalidLocationException(adaptorDescription.getName(), message, e);
        } else if (e instanceof UnknownPropertyException) {
            String message = e.getMessage();
            message += " (" + getSupportedPropertiesHelp(adaptorDescription.getSupportedProperties()) + ")";
            throw new UnknownPropertyException(adaptorDescription.getName(), message, e);
        } else {
            throw e;
        }
    }
}
