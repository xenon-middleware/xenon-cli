package nl.esciencecenter.xenon.cli;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Subparser;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonPropertyDescription;
import nl.esciencecenter.xenon.filesystems.CopyMode;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.schedulers.JobDescription;
import nl.esciencecenter.xenon.schedulers.Scheduler;

/**
 * Utilities to construct argument parser
 */
public class ParserHelpers {
    private ParserHelpers() {
        throw new IllegalAccessError("Utility class");
    }

    static void addCredentialArguments(ArgumentParser parser) {
        parser.addArgument("--username").help("Username").setDefault(System.getProperty("user.name"));
        parser.addArgument("--password").help("Password or passphrase");
        parser.addArgument("--certfile").help("Certificate private key file");
    }

    public static void addTargetCredentialArguments(ArgumentGroup parser) {
        parser.addArgument("--target-username").help("Username for target location (default: --username value)");
        parser.addArgument("--target-password").help("Password or passphrase for target location (default: --password value)");
        parser.addArgument("--target-certfile").help("Certificate private key file for target location (default: --certfile value)");
    }

    public static MutuallyExclusiveGroup addCopyModeArguments(ArgumentParser parser) {
        MutuallyExclusiveGroup group = parser.addMutuallyExclusiveGroup("optional copy mode arguments");
        group.addArgument("--replace")
            .help("If a file already exists at the target location, replace that file with the file from the source location")
            .type(CopyMode.class)
            .action(Arguments.storeConst())
            .dest("copymode")
            .setConst(CopyMode.REPLACE)
            .setDefault(CopyMode.CREATE);
        group.addArgument("--ignore")
            .help("If a file already exists at the target location, skip copying of that file")
            .type(CopyMode.class)
            .action(Arguments.storeConst())
            .dest("copymode")
            .setConst(CopyMode.IGNORE)
            .setDefault(CopyMode.CREATE);
        return group;
    }

    public static String getSupportedLocationHelp(String[] supportedLocations) {
        List<String> helps = Arrays.stream(supportedLocations).map(location -> "- " + location).collect(Collectors.toList());
        helps.add(0, "supported locations:");
        String sep = System.getProperty("line.separator");
        return String.join(sep, helps);
    }

    static String getAdaptorPropertyHelp(XenonPropertyDescription property) {
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
            .help("Path at location where executable should be executed. If location is local system, default value is the current working directory. If location is remote, default value is remote system's entry path");
    }

    public static Set<String> getAllowedFileSystemPropertyKeys(String adaptor) throws XenonException {
        return Arrays.stream(FileSystem.getAdaptorDescription(adaptor).getSupportedProperties()).map(XenonPropertyDescription::getName).collect(Collectors.toSet());
    }

    public static Set<String> getAllowedSchedulerPropertyKeys(String adaptor) throws XenonException {
        return Arrays.stream(Scheduler.getAdaptorDescription(adaptor).getSupportedProperties()).map(XenonPropertyDescription::getName).collect(Collectors.toSet());
    }

    public static String getSupportedPropertiesHelp(XenonPropertyDescription[] descriptions) {
        String sep = System.getProperty("line.separator");
        List<String> helps = Arrays.stream(descriptions).map(ParserHelpers::getAdaptorPropertyHelp).collect(Collectors.toList());
        helps.add(0, "supported properties:");
        return String.join(sep, helps);
    }
}
