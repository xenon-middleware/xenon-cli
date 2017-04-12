package nl.esciencecenter.xenon.cli.copy;

import static nl.esciencecenter.xenon.cli.ParserHelpers.addCopyModeArguments;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class DownloadParser extends CopyParser {
    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("download")
            .setDefault("command", new DownloadCommand())
            .help("Download source file to local file")
            .description("Download source file to local file");
        subparser.addArgument("source").help("Local source path").required(true);
        subparser.addArgument("target")
            .type(Arguments.fileType().acceptSystemIn())
            .help("Target path, use '-' for stdout")
            .required(true);
        subparser.addArgument("--recursive").help("Copy directories recursively").action(Arguments.storeTrue());
        addCopyModeArguments(subparser);

        return subparser;
    }
}
