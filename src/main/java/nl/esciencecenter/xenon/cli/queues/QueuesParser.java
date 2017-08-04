package nl.esciencecenter.xenon.cli.queues;

import nl.esciencecenter.xenon.cli.IParser;

import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class QueuesParser implements IParser {
    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        return subparsers.addParser("queues")
            .setDefault("command", new QueuesCommand())
            .defaultHelp(true)
            .help("List of available queues")
            .description("List of available queues");
    }
}
