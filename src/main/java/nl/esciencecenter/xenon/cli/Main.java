package nl.esciencecenter.xenon.cli;

import static nl.esciencecenter.xenon.cli.JobsUtils.parseArgumentListAsMap;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getAllowedXenonPropertyKeys;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getSupportedLocationHelp;

import java.util.*;
import java.util.stream.Collectors;

import nl.esciencecenter.xenon.AdaptorDescription;
import nl.esciencecenter.xenon.AdaptorStatus;
import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonFactory;
import nl.esciencecenter.xenon.XenonPropertyDescription;
import nl.esciencecenter.xenon.cli.copy.CopyParser;
import nl.esciencecenter.xenon.cli.copy.DownloadParser;
import nl.esciencecenter.xenon.cli.copy.UploadParser;
import nl.esciencecenter.xenon.cli.exec.ExecParser;
import nl.esciencecenter.xenon.cli.listfiles.ListFilesParser;
import nl.esciencecenter.xenon.cli.listjobs.ListJobsParser;
import nl.esciencecenter.xenon.cli.queues.QueuesParser;
import nl.esciencecenter.xenon.cli.removefile.RemoveFileParser;
import nl.esciencecenter.xenon.cli.removejob.RemoveJobParser;
import nl.esciencecenter.xenon.cli.submit.SubmitParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.FileSystemAdaptorDescription;
import nl.esciencecenter.xenon.schedulers.Scheduler;
import nl.esciencecenter.xenon.schedulers.SchedulerAdaptorDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse arguments and runs sub-commands.
 */
public class Main {
    // TODO make filesSchemes+JOBS_SCHEMES dynamic after https://github.com/NLeSC/Xenon/issues/400 is fixed
    private static final List<String> ONLINE_SCHEMES = getOnlineNames();

    // can not tell which adaptors are local, so hardcoded them
    private static final List<String> LOCAL_SCHEMES = Arrays.asList("file", "local");
    // can not tell which adaptors are local, so hardcoded them
    private static final List<String> SSH_SCHEMES = Arrays.asList("ssh", "sftp");
    private static final String PROGRAM_NAME = "xenon";
    private static final String PROGRAM_VERSION = "2.0.0";
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private final ArgumentParser parser;
    private Namespace res = new Namespace(new HashMap<>());

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        Object output = main.run(args);
        main.print(output);
    }

    public static Map<String,String> buildXenonProperties(Namespace res, Set<String> allowedKeys) {
        return parseArgumentListAsMap(res.getList("props")).entrySet().stream()
            .filter(p -> allowedKeys.contains(p.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static List<String> getOnlineNames() {
        // TODO can not tell which filesystems are online, so hardcoded them for now
        List<String> names = Arrays.asList("file", "sftp");
        try {
            Arrays.stream(Scheduler.getAdaptorDescriptions())
                    .filter(SchedulerAdaptorDescription::isOnline)
                    .map(SchedulerAdaptorDescription::getName)
                    .forEach(names::add);
        } catch (XenonException e) {
            e.printStackTrace();
        }
        return names;
    }

    public ArgumentParser getParser() {
        return parser;
    }

    public Namespace getRes() {
        return res;
    }

    public Main() throws XenonException {
        parser = buildArgumentParser();
    }

    public Object run(String[] args) throws XenonException {
        try {
            res = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            return null;
        }
        ICommand subCommand = res.get("command");
        return run(subCommand);
    }

    public Object run(ICommand subCommand) throws XenonException {
        return subCommand.run(res);
    }

    private void print(Object output) {
        String format = res.getString("format");
        print(output, format);
    }

    public void print(Object output, String format) {
        if ("cwljson".equals(format)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.print(gson.toJson(output));
        } else if (output != null) {
            System.out.println(output);
        }
    }

    public ArgumentParser buildArgumentParser() {
        ArgumentParser newParser = ArgumentParsers.newArgumentParser(PROGRAM_NAME, true, "-", "@")
                .defaultHelp(true)
                .description("Files and Jobs operations with Xenon")
                .version(PROGRAM_VERSION);
        newParser.addArgument("--version").action(Arguments.version());
        newParser.addArgument("--format").choices("cwljson").help("Output in JSON format");
        addSchemeSubParsers(newParser);
        ParserHelpers.addCredentialArguments(newParser);
        return newParser;
    }

    private void addSchemeSubParsers(ArgumentParser parser) {
        Subparsers subparsers = parser.addSubparsers().title("scheme");
        List<AdaptorDescription> adaptorDescriptions = new ArrayList<>();
        try {
            Collections.addAll(adaptorDescriptions, Scheduler.getAdaptorDescriptions());
            Collections.addAll(adaptorDescriptions, FileSystem.getAdaptorDescriptions());
        } catch (XenonException e ) {
            LOGGER.info("Failed to getAdaptorDescriptions", e);
        }
        adaptorDescriptions.sort(Comparator.comparing(AdaptorDescription::getName));
        for (AdaptorDescription adaptorDescription : adaptorDescriptions) {
            adaptorSubCommands(subparsers, adaptorDescription);
        }
    }

    private void adaptorSubCommands(Subparsers subparsers, AdaptorDescription adaptorDescription) {
        Subparser schemeParser = addSubCommandScheme(subparsers, adaptorDescription);
        String supportedLocationHelp = addArgumentLocation(adaptorDescription, schemeParser);
        addArgumentProp(adaptorDescription, schemeParser);
        Subparsers commandsParser = schemeParser.addSubparsers().title("commands");
        if (adaptorDescription instanceof FileSystemAdaptorDescription) {
            filesSubCommands(adaptorDescription, supportedLocationHelp, commandsParser);
        } else if (adaptorDescription instanceof SchedulerAdaptorDescription) {
            jobsSubCommands(adaptorDescription, commandsParser);
        }
    }

    private void jobsSubCommands(String scheme, Subparsers commandsParser) {
        // exec
        new ExecParser().buildArgumentParser(commandsParser);
        if (!ONLINE_SCHEMES.contains(scheme)) {
            // submit
            new SubmitParser().buildArgumentParser(commandsParser);
            // list
            new ListJobsParser().buildArgumentParser(commandsParser);
            // remove
            new RemoveJobParser().buildArgumentParser(commandsParser);
            // queues
            new QueuesParser().buildArgumentParser(commandsParser);
        }
    }

    private void filesSubCommands(String scheme, String supportedLocationHelp, Subparsers commandsParser) {
        // copy
        new CopyParser().buildArgumentParser(commandsParser, supportedLocationHelp, LOCAL_SCHEMES.contains(scheme));
        if (!LOCAL_SCHEMES.contains(scheme)) {
            // upload
            new UploadParser().buildArgumentParser(commandsParser);
            // download
            new DownloadParser().buildArgumentParser(commandsParser);
        }
        // list
        new ListFilesParser().buildArgumentParser(commandsParser);
        // remove
        new RemoveFileParser().buildArgumentParser(commandsParser);
    }

    private Subparser addSubCommandScheme(Subparsers subparsers, AdaptorDescription adaptorDescription) {
        return subparsers.addParser(adaptorDescription.getName())
                        .help(adaptorDescription.getDescription())
                        .description(adaptorDescription.getDescription())
                        .setDefault("scheme", adaptorDescription.getName());
    }

    private String addArgumentLocation(AdaptorDescription adaptorDescription, Subparser schemeParser) {
        String supportedLocationHelp = getSupportedLocationHelp(adaptorDescription.getSupportedLocations());
        Argument locationArgument = schemeParser.addArgument("--location").help("Location, " + supportedLocationHelp);
        if (SSH_SCHEMES.contains(adaptorDescription.getName())) {
            locationArgument.required(true);
        }
        return supportedLocationHelp;
    }

    private void addArgumentProp(AdaptorDescription adaptorDescription, Subparser schemeParser) {
        schemeParser.addArgument("--prop")
            .action(Arguments.append())
            .metavar("KEY=VALUE")
            .help("Xenon adaptor properties, " + getSupportedPropertiesHelp(adaptorDescription.getSupportedProperties()))
            .dest("props");
    }

    private String getSupportedPropertiesHelp(XenonPropertyDescription[] descriptions) {
        String sep = System.getProperty("line.separator");
        List<String> helps = Arrays.stream(descriptions).map(ParserHelpers::getAdaptorPropertyHelp).collect(Collectors.toList());
        helps.add(0, "Supported properties:");
        return String.join(sep, helps);
    }

    private AdaptorStatus[] getAdaptorStatuses() {
        AdaptorStatus[] adaptors = {};
        try {
            Xenon xenon = XenonFactory.newXenon(null);
            adaptors = xenon.getAdaptorStatuses();
            XenonFactory.endXenon(xenon);
            return adaptors;
        } catch (XenonException e) {
            LOGGER.info("Failed to getAdaptorStatuses", e);
        }
        return adaptors;
    }
}
