package nl.esciencecenter.xenon.cli.queues;

import nl.esciencecenter.xenon.cli.IParser;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class QueuesParser implements IParser {
    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("queues")
            .setDefault("command", new QueuesCommand())
            .help("List of available queues")
            .description("List of available queues");
        subparser.addArgument("--default").help("Filter on default queue").action(Arguments.storeTrue());
        return subparser;
    }
}
