package nl.esciencecenter.xenon.cli.listjobs;

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
        subparser.addArgument("--queue").help("Only list jobs belonging to this queue");
        return subparser;
    }
}
