package nl.esciencecenter.xenon.cli;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;

public interface ICommand {
    Subparser buildArgumentParser(Subparsers subparsers);
    void run(Namespace res, Xenon xenon) throws XenonException;
}
