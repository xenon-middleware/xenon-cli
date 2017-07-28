package nl.esciencecenter.xenon.cli.listfiles;

import nl.esciencecenter.xenon.cli.IParser;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ListFilesParser implements IParser {
    public Subparser buildArgumentParser(Subparsers subparsers) {
        String sep = System.getProperty("line.separator");
        String epilog = "Listing format by default is one file/directory per line." + sep +
                sep +
                "The `--long` format is tab separated, includes a header and contains the following columns:" + sep +
                "* Type, can be '-' for regular file, 'd' for directory, 'l' for symbolic link, 'o' for other" + sep +
                "* Permissions, triple set of read, write, executable for owner, group and other" + sep +
                "* Hidden, true if file/directory is hidden" + sep +
                "* Owner, empty when not supported by adaptor" + sep +
                "* Group, empty when not supported by adaptor" + sep +
                "* Size, size of file in bytes" + sep +
                "* Last modified at, datetime in ISO8601 format" + sep +
                "* Name, path of file/directory" + sep;
        Subparser subparser = subparsers.addParser("list")
            .setDefault("command", new ListFilesCommand())
            .help("List objects in path of location")
            .description("List objects in path of location").epilog(epilog);
        subparser.addArgument("path").help("Start directory").required(true);
        subparser.addArgument("--recursive", "-r").help("List directories recursively").action(Arguments.storeTrue());
        subparser.addArgument("--hidden", "-A").help("Include hidden items").action(Arguments.storeTrue());
        subparser.addArgument("--long", "-l").help("Long listing format").action(Arguments.storeTrue());
        return subparser;
    }
}
