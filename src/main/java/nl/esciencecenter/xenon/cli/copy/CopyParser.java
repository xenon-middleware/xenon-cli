package nl.esciencecenter.xenon.cli.copy;

import static nl.esciencecenter.xenon.cli.ParserHelpers.addCopyModeArguments;
import static nl.esciencecenter.xenon.cli.ParserHelpers.addTargetCredentialArguments;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getSupportedLocationHelp;
import static nl.esciencecenter.xenon.utils.LocalFileSystemUtils.isWindows;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import nl.esciencecenter.xenon.cli.IParser;
import nl.esciencecenter.xenon.cli.ParserHelpers;
import nl.esciencecenter.xenon.cli.Utils;
import nl.esciencecenter.xenon.filesystems.FileSystemAdaptorDescription;

public class CopyParser implements IParser {
    private FileSystemAdaptorDescription adaptorDescription;

    public Subparser buildArgumentParser(Subparsers subparsers) {
        boolean isLocal = Utils.isLocalAdaptor(adaptorDescription);

        Subparser subparser = subparsers.addParser("copy")
                .setDefault("command", new CopyCommand())
                .defaultHelp(true)
                .help("Copy path from location to target location")
                .description("Copy path from location to target location");
        Argument sourcePath = subparser.addArgument("source-path").required(true);
        if (isLocal) {
            sourcePath
                    .help("Source path, use '-' for stdin")
                    .type(Arguments.fileType().acceptSystemIn());
        } else {
            sourcePath.help("Source path");
        }
        Argument targetPath = subparser.addArgument("target-path").required(true);
        if (isLocal) {
            targetPath.help("Target path, use '-' for stdout");
        } else {
            targetPath.help("Target path");
        }
        String sep = System.getProperty("line.separator");
        ArgumentGroup targetparser = subparser.addArgumentGroup("target")
                .description("If location of target path ('target_path') is not the same as --location, then set --target-* arguments. " + sep + sep +
                        "The target location always makes use of the " + adaptorDescription.getName() + " adaptor. " +
                        "To copy between the file adaptor (aka local files) and " + adaptorDescription.getName() + " adaptor use xenon upload or xenon download commands. " +
                        "To copy between different adaptors use the local filesystem as intermediate storage and a sequence of xenon download, upload and remove commands.");
        if (!isLocal || isWindows()) {
            String supportedLocationHelp = getSupportedLocationHelp(adaptorDescription.getSupportedLocations());
            Argument targetLocation = targetparser.addArgument("--target-location")
                    .help("Target location, " +
                            supportedLocationHelp +
                            sep + "(default: --location value)");
            if (isLocal) {
                targetLocation.setDefault("c:");
            }
        }
        if (!isLocal) {
            addTargetCredentialArguments(targetparser);
        }
        addTargetArgumentProp(adaptorDescription, targetparser);

        subparser.addArgument("--recursive").help("Copy directories recursively").action(Arguments.storeTrue());
        addCopyModeArguments(subparser);
        return subparser;
    }

    private void addTargetArgumentProp(FileSystemAdaptorDescription adaptorDescription, ArgumentGroup subparser) {
        if (adaptorDescription.getSupportedProperties().length > 0) {
            String sep = System.getProperty("line.separator");
            subparser.addArgument("--target-prop")
                    .action(Arguments.append())
                    .metavar("KEY=VALUE")
                    .help("Adaptor properties for target location, can be given multiple times, " +
                            ParserHelpers.getSupportedPropertiesHelp(adaptorDescription.getSupportedProperties()) +
                            sep + "(default: --prop value)")
                    .dest("props");
        }
    }

    public CopyParser setAdaptorDescription(FileSystemAdaptorDescription adaptorDescription) {
        this.adaptorDescription = adaptorDescription;
        return this;
    }
}
