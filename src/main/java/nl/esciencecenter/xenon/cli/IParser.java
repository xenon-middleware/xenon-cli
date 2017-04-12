package nl.esciencecenter.xenon.cli;

import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public interface IParser {
    Subparser buildArgumentParser(Subparsers subparsers);
}
