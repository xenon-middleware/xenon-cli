package nl.esciencecenter.xenon.cli.createdir;

import nl.esciencecenter.xenon.cli.IParser;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class CreateDirectoryParser implements IParser {
    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("mkdir")
            .setDefault("command", new CreateDirectoryCommand())
            .help("Create directory at location")
            .description("Create directory at location");
        subparser.addArgument("path").help("Path of directory").required(true);
        subparser.addArgument("--parents", "-p").help("Create parent directories as needed").action(Arguments.storeTrue());
        return subparser;
    }
}
