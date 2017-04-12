package nl.esciencecenter.xenon.cli.exec;

import nl.esciencecenter.xenon.cli.IParser;
import nl.esciencecenter.xenon.cli.ParserHelpers;

import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ExecParser implements IParser {
    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        //   exec <executable> <args> <environment> <job options> <max time> <queue> <working directory> <std* attached to local streams>
        Subparser subparser = subparsers.addParser("exec")
            .setDefault("command", new ExecCommand())
            .help("Execute job at location")
            .description("Execute job at location");

        ParserHelpers.addRunArguments(subparser);

        subparser.addArgument("--wait-timeout")
            .type(Long.class)
            .help("Time to wait for job completion (ms)")
            .setDefault(0L);
        return subparser;
    }

}
