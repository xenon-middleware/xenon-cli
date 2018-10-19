package nl.esciencecenter.xenon.cli.submit;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import nl.esciencecenter.xenon.cli.IParser;
import nl.esciencecenter.xenon.cli.ParserHelpers;

public class SubmitParser implements IParser {
    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        //   exec <executable> <args> <environment> <job options> <max time> <queue> <working directory> <std* attached to local streams>
        Subparser subparser = subparsers.addParser("submit")
            .setDefault("command", new SubmitCommand())
            .defaultHelp(true)
            .help("Submit job at location")
            .description("Submit job at location");

        ParserHelpers.addRunArguments(subparser);

        subparser.addArgument("--stdin").help("Path to file which is used as stdin for executable");
        subparser.addArgument("--stdout").help("Path to file which is used as stdout for executable, where the \"%j\" is replaced with the job identifier");
        subparser.addArgument("--stderr").help("Path to file which is used as stderr for executable, where the \"%j\" is replaced with the job identifier");

        subparser.addArgument("--long", "-l").help("Output long format").action(Arguments.storeTrue());
        subparser.addArgument("--name").help("Name of the job");
        return subparser;
    }
}
