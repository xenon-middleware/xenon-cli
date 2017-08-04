package nl.esciencecenter.xenon.cli.rename;

import nl.esciencecenter.xenon.cli.IParser;

import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class RenameParser implements IParser {
    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("rename")
            .setDefault("command", new RenameCommand())
            .defaultHelp(true)
            .help("Copy path from location to target location")
            .description("Copy path from location to target location");
        subparser.addArgument("source").help("Source path").required(true);
        subparser.addArgument("target").help("Target path").required(true);
        return subparser;
    }
}
