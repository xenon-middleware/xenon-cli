package nl.esciencecenter.xenon.cli;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonPropertyDescription;
import nl.esciencecenter.xenon.filesystems.CopyMode;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.schedulers.JobDescription;
import nl.esciencecenter.xenon.schedulers.Scheduler;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Subparser;

/**
 * Utilities to construct argument parser
 */
public class ParserHelpers {
    private ParserHelpers() {
        throw new IllegalAccessError("Utility class");
    }

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

    public static MutuallyExclusiveGroup addCopyModeArguments(ArgumentParser parser) {
        MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup("optional copy mode arguments");
        group.addArgument("--overwrite")
            .help("Overwrite existing files at target location")
            .type(CopyMode.class)
            .action(Arguments.storeConst())
            .dest("copymode")
            .setConst(CopyMode.REPLACE)
            .setDefault(CopyMode.CREATE);
        group.addArgument("--ignore")
            .help("Ignore existing files at target location")
            .type(CopyMode.class)
            .action(Arguments.storeConst())
            .dest("copymode")
            .setConst(CopyMode.IGNORE)
            .setDefault(CopyMode.CREATE);
        return group;
    }

    public static String getSupportedLocationHelp(String[] supportedLocations) {
        List<String> helps = Arrays.stream(supportedLocations).map(location -> "- " + location).collect(Collectors.toList());
        helps.add(0, "Supported locations:");
        String sep = System.getProperty("line.separator");
        return String.join(sep, helps);
    }

    public static String getAdaptorPropertyHelp(XenonPropertyDescription property) {
        return "- " + property.getName() + "=" + property.getDefaultValue() + " ("+ property.getDescription() + ", type:" + property.getType() + ") ";
    }

    public static void addRunArguments(Subparser subparser) {
        subparser.addArgument("executable").help("Executable to schedule for execution").required(true);
        subparser.addArgument("args")
            .help("Arguments for executable, prepend ' -- ' when arguments start with '-'")
            .nargs("*");

        subparser.addArgument("--queue").help("Schedule job in this queue");
        subparser.addArgument("--env")
            .help("Environment variable of the executable")
            .metavar("KEY=VAL")
            .action(Arguments.append())
            .dest("envs");
        subparser.addArgument("--option")
            .help("Option for job")
            .metavar("KEY=VAL")
            .action(Arguments.append())
            .dest("options");
        subparser.addArgument("--max-time").help("Maximum job time (in minutes)").type(Integer.class).setDefault(JobDescription.DEFAULT_MAX_RUN_TIME);
        subparser.addArgument("--node-count").type(Integer.class).help("Number of nodes to reserve").setDefault(1);
        subparser.addArgument("--procs-per-node").type(Integer.class).help("Number of processes started on each node").setDefault(1);
        subparser.addArgument("--working-directory")
            .help("Path at location where executable should be executed. If not given will local working directory or when remove will use home directory");
    }

    public static Set<String> getAllowedFileSystemPropertyKeys(String adaptor) throws XenonException {
        return Arrays.stream(FileSystem.getAdaptorDescription(adaptor).getSupportedProperties()).map(XenonPropertyDescription::getName).collect(Collectors.toSet());
    }

    public static Set<String> getAllowedSchedulerPropertyKeys(String adaptor) throws XenonException {
        return Arrays.stream(Scheduler.getAdaptorDescription(adaptor).getSupportedProperties()).map(XenonPropertyDescription::getName).collect(Collectors.toSet());
    }
}
