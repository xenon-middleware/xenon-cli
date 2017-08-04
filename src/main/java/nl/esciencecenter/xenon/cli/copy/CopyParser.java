package nl.esciencecenter.xenon.cli.copy;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import nl.esciencecenter.xenon.cli.IParser;
import nl.esciencecenter.xenon.cli.ParserHelpers;

import static nl.esciencecenter.xenon.adaptors.shared.local.LocalUtil.isWindows;
import static nl.esciencecenter.xenon.cli.ParserHelpers.addCopyModeArguments;

public class CopyParser implements IParser {
    public Subparser buildArgumentParser(Subparsers subparsers, String supportedLocationHelp, Boolean isLocal) {
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
        if (!isLocal || isWindows()) {
            Argument targetLocation = subparser.addArgument("target-location").help("Target location, " + supportedLocationHelp);
            if (isLocal) {
                targetLocation.nargs("?");
            }
        }
        Argument targetPath = subparser.addArgument("target-path").required(true);
        if (isLocal) {
            targetPath.help("Target path, use '-' for stdout");
        } else {
            targetPath.help("Target path");
        }
        if (!isLocal) {
            ParserHelpers.addCredentialArguments(subparser, "target-");
        }
        subparser.addArgument("--recursive").help("Copy directories recursively").action(Arguments.storeTrue());
        addCopyModeArguments(subparser);
        return subparser;
    }

    public Subparser buildArgumentParser(Subparsers subparsers) {
        return buildArgumentParser(subparsers, "", false);
    }
}
