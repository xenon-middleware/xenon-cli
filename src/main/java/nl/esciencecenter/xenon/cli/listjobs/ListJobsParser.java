package nl.esciencecenter.xenon.cli.listjobs;

import net.sourceforge.argparse4j.impl.Arguments;
import nl.esciencecenter.xenon.cli.IParser;

import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ListJobsParser implements IParser {
    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("list")
            .setDefault("command", new ListJobsCommand())
            .help("List jobs of scheduler")
            .description("List jobs of scheduler");
        subparser.addArgument("--queue").help("Only list jobs belonging to this queue").action(Arguments.append());
        subparser.addArgument("--identifier").help("Only list jobs with this identifier").action(Arguments.append());
        return subparser;
    }
}
