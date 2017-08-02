package nl.esciencecenter.xenon.cli;

import static nl.esciencecenter.xenon.adaptors.shared.local.LocalUtil.isWindows;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getSupportedLocationHelp;
import static nl.esciencecenter.xenon.cli.Utils.parseArgumentListAsMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import nl.esciencecenter.xenon.AdaptorDescription;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonPropertyDescription;
import nl.esciencecenter.xenon.cli.copy.CopyParser;
import nl.esciencecenter.xenon.cli.copy.DownloadParser;
import nl.esciencecenter.xenon.cli.copy.UploadParser;
import nl.esciencecenter.xenon.cli.createdir.CreateDirectoryParser;
import nl.esciencecenter.xenon.cli.exec.ExecParser;
import nl.esciencecenter.xenon.cli.listfiles.ListFilesParser;
import nl.esciencecenter.xenon.cli.listjobs.ListJobsParser;
import nl.esciencecenter.xenon.cli.queues.QueuesParser;
import nl.esciencecenter.xenon.cli.removefile.RemoveFileParser;
import nl.esciencecenter.xenon.cli.removejob.RemoveJobParser;
import nl.esciencecenter.xenon.cli.rename.RenameParser;
import nl.esciencecenter.xenon.cli.submit.SubmitParser;
import nl.esciencecenter.xenon.cli.wait.WaitParser;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.FileSystemAdaptorDescription;
import nl.esciencecenter.xenon.schedulers.Scheduler;
import nl.esciencecenter.xenon.schedulers.SchedulerAdaptorDescription;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
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
import org.slf4j.LoggerFactory;

/**
 * Parse arguments and runs sub-commands.
 */
public class Main {
    private static final String PROGRAM_NAME = "xenon";
    private static final String PROGRAM_VERSION = "2.0.0";
    private final ArgumentParser parser;
    private Namespace res = new Namespace(new HashMap<>());

    public static void main(String[] args) {
        Main main = new Main();
        Object output = main.run(args);
        main.print(output);
    }

    public static Map<String,String> buildXenonProperties(Namespace res, Set<String> allowedKeys) {
        return parseArgumentListAsMap(res.getList("props")).entrySet().stream()
            .filter(p -> allowedKeys.contains(p.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Namespace getRes() {
        return res;
    }

    public Main() {
        parser = buildArgumentParser();
    }

    public Object run(String[] args) {
        try {
            res = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(2);
        }
        configureLogger();
        ICommand subCommand = res.get("command");
        return run(subCommand);
    }

    private void configureLogger() {
        Integer verboseness = res.getInt("verbose");
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        if (verboseness == 1) {
            root.setLevel(Level.WARN);
        } else if (verboseness == 2) {
            root.setLevel(Level.INFO);
        } else if (verboseness == 3) {
            root.setLevel(Level.DEBUG);
        } else if (verboseness > 3) {
            root.setLevel(Level.TRACE);
        } else {
            root.setLevel(Level.ERROR);
        }
        // ?do we need a --quiet arg to set level to off
    }

    public Object run(ICommand subCommand) {
        try {
            return subCommand.run(res);
        } catch (XenonException e) {
            handleError(e);
            return null;
        }
    }

    private void handleError(XenonException e) {
        if (res.getBoolean("stacktrace")) {
            e.printStackTrace();
        } else {
            System.err.println(e.getMessage());
        }
        System.exit(1);
    }

    private void print(Object output) {
        Boolean jsonFormat = res.getBoolean("json");
        print(output, jsonFormat);
    }

    void print(Object output, Boolean jsonFormat) {
        if (output == null) {
            return;
        }
        if (jsonFormat) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.print(gson.toJson(output));
        } else {
            System.out.println(output);
        }
    }

    public ArgumentParser buildArgumentParser() {
        ArgumentParser newParser = ArgumentParsers.newArgumentParser(PROGRAM_NAME, true, "-", "@")
                .defaultHelp(true)
                .description("Operations on filesystems and schedulers with Xenon")
                .version(PROGRAM_VERSION);
        newParser.addArgument("--version").action(Arguments.version()).help("Prints version and exists");
        newParser.addArgument("--json").help("Output in JSON format").action(Arguments.storeTrue());
        newParser.addArgument("--stacktrace").help("Print out the stacktrace for all exceptions").action(Arguments.storeTrue());
        newParser.addArgument("--verbose", "-v").help("Repeat for more verbose logging").action(Arguments.count());

        Subparsers subparsers = newParser.addSubparsers();
        String filesystemHelp = "Filesystem represent a (possibly remote) file system that can be used to access data.";
        Subparser filesystemAdaptorParser = subparsers.addParser("filesystem")
            .help(filesystemHelp).description(filesystemHelp);
        addAdaptorSubParsers(filesystemAdaptorParser, FileSystem.getAdaptorDescriptions());
        String schedulerHelp = "Scheduler represents a (possibly remote) scheduler that can be used to submit jobs and retrieve queue information.";
        Subparser schedulerAdaptorParser = subparsers.addParser("scheduler")
            .help(schedulerHelp).description(schedulerHelp);
        addAdaptorSubParsers(schedulerAdaptorParser, Scheduler.getAdaptorDescriptions());

        return newParser;
    }

    private void addAdaptorSubParsers(Subparser parser, AdaptorDescription[] adaptorDescriptionArray) {
        Subparsers subparsers = parser.addSubparsers().title("adaptor");
        List<AdaptorDescription> adaptorDescriptions = new ArrayList<>();
        Collections.addAll(adaptorDescriptions, adaptorDescriptionArray);
        adaptorDescriptions.sort(Comparator.comparing(AdaptorDescription::getName));
        for (AdaptorDescription adaptorDescription : adaptorDescriptions) {
            adaptorSubCommands(subparsers, adaptorDescription);
        }
    }

    private void adaptorSubCommands(Subparsers subparsers, AdaptorDescription adaptorDescription) {
        Subparser adaptorParser = addSubCommandAdaptor(subparsers, adaptorDescription);
        String supportedLocationHelp = addArgumentLocation(adaptorDescription, adaptorParser);
        if (!isLocalAdaptor(adaptorDescription)) {
            ParserHelpers.addCredentialArguments(adaptorParser);
        }
        addArgumentProp(adaptorDescription, adaptorParser);
        Subparsers commandsParser = adaptorParser.addSubparsers().title("commands");
        if (adaptorDescription instanceof FileSystemAdaptorDescription) {
            filesystemSubCommands((FileSystemAdaptorDescription) adaptorDescription, supportedLocationHelp, commandsParser);
        } else if (adaptorDescription instanceof SchedulerAdaptorDescription) {
            schedulerSubCommands((SchedulerAdaptorDescription) adaptorDescription, commandsParser);
        }
    }

    private void schedulerSubCommands(SchedulerAdaptorDescription adaptorDescription, Subparsers commandsParser) {
        // exec
        new ExecParser().buildArgumentParser(commandsParser);
        if (!adaptorDescription.isEmbedded()) {
            // submit
            new SubmitParser().buildArgumentParser(commandsParser);
            // list
            new ListJobsParser().buildArgumentParser(commandsParser);
            // remove
            new RemoveJobParser().buildArgumentParser(commandsParser);
            // wait
            new WaitParser().buildArgumentParser(commandsParser);
        }
        // queues
        new QueuesParser().buildArgumentParser(commandsParser);
    }

    private void filesystemSubCommands(FileSystemAdaptorDescription adaptorDescription, String supportedLocationHelp, Subparsers commandsParser) {
        // copy
        boolean isLocal = isLocalAdaptor(adaptorDescription);
        new CopyParser().buildArgumentParser(commandsParser, supportedLocationHelp, isLocal);
        if (!isLocal) {
            // upload
            new UploadParser().buildArgumentParser(commandsParser);
            // download
            new DownloadParser().buildArgumentParser(commandsParser);
        }
        // list
        new ListFilesParser().buildArgumentParser(commandsParser);
        // createdir
        new CreateDirectoryParser().buildArgumentParser(commandsParser);
        // remove
        new RemoveFileParser().buildArgumentParser(commandsParser);
        // rename
        new RenameParser().buildArgumentParser(commandsParser);
    }

    private Subparser addSubCommandAdaptor(Subparsers subparsers, AdaptorDescription adaptorDescription) {
        String help = adaptorDescription.getDescription();
        return subparsers.addParser(adaptorDescription.getName())
                        .help(help)
                        .description(help)
                        .setDefault("adaptor", adaptorDescription.getName());
    }

    private String addArgumentLocation(AdaptorDescription adaptorDescription, Subparser adaptorParser) {
        String supportedLocationHelp = getSupportedLocationHelp(adaptorDescription.getSupportedLocations());
        boolean isLocal = isLocalAdaptor(adaptorDescription);
        if (!isLocal || isWindows()) {
            Argument locationArgument = adaptorParser.addArgument("--location").help("Location, " + supportedLocationHelp);
            boolean locationCanBeNull = Arrays.stream(adaptorDescription.getSupportedLocations()).anyMatch(l -> l.equals("(null)"));
            if (!locationCanBeNull) {
                locationArgument.required(true);
            }
        }
        return supportedLocationHelp;
    }

    private boolean isLocalAdaptor(AdaptorDescription adaptorDescription) {
        return adaptorDescription.getName().equals("file") || adaptorDescription.getName().equals("local");
    }

    private void addArgumentProp(AdaptorDescription adaptorDescription, Subparser adaptorParser) {
        if (adaptorDescription.getSupportedProperties().length > 0) {
            adaptorParser.addArgument("--prop")
                .action(Arguments.append())
                .metavar("KEY=VALUE")
                .help("Supported adaptor properties, " + getSupportedPropertiesHelp(adaptorDescription.getSupportedProperties()))
                .dest("props");
        }
    }

    private String getSupportedPropertiesHelp(XenonPropertyDescription[] descriptions) {
        String sep = System.getProperty("line.separator");
        List<String> helps = Arrays.stream(descriptions).map(ParserHelpers::getAdaptorPropertyHelp).collect(Collectors.toList());
        helps.add(0, "Supported properties:");
        return String.join(sep, helps);
    }
}
