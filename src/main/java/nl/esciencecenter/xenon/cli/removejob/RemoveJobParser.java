package nl.esciencecenter.xenon.cli.removejob;

import nl.esciencecenter.xenon.cli.IParser;

import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class RemoveJobParser implements IParser {
    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("remove")
            .setDefault("command", new RemoveJobCommand())
            .defaultHelp(true)
            .help("Remove job from scheduler")
            .description("Remove job from scheduler");
        subparser.addArgument("job-identifier").help("Job identifier").required(true);
        return subparser;
    }
}
