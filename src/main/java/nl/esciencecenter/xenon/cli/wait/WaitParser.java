package nl.esciencecenter.xenon.cli.wait;

import nl.esciencecenter.xenon.cli.IParser;

import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class WaitParser implements IParser {
    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("wait")
            .setDefault("command", new WaitCommand())
            .defaultHelp(true)
            .help("Wait for job to complete")
            .description("Wait for job to complete");
        subparser.addArgument("--timeout").help("Maximum number of milliseconds to wait, by default waits forever").setDefault(0L).type(Long.class);
        subparser.addArgument("identifier").help("The job identifier");
        return subparser;
    }
}
