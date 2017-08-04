package nl.esciencecenter.xenon.cli.copy;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import static nl.esciencecenter.xenon.cli.ParserHelpers.addCopyModeArguments;

public class DownloadParser extends CopyParser {
    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("download")
            .setDefault("command", new DownloadCommand())
            .defaultHelp(true)
            .help("Download source file to local file")
            .description("Download source file to local file");
        subparser.addArgument("source").help("Source path at location").required(true);
        subparser.addArgument("target")
            .type(Arguments.fileType().acceptSystemIn())
            .help("Target path, use '-' for stdout")
            .required(true);
        subparser.addArgument("--recursive").help("Copy directories recursively").action(Arguments.storeTrue());
        addCopyModeArguments(subparser);

        return subparser;
    }
}
