package nl.esciencecenter.xenon.cli.listfiles;

import nl.esciencecenter.xenon.cli.IParser;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ListFilesParser implements IParser {
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("list")
            .setDefault("command", new ListFilesCommand())
            .help("List objects at path of location")
            .description("List objects at path of location");
        subparser.addArgument("path").help("Path").required(true);
        subparser.addArgument("--recursive").help("List directories recursively").action(Arguments.storeTrue());
        subparser.addArgument("--hidden").help("Include hidden items").action(Arguments.storeTrue());
        return subparser;
    }
}
