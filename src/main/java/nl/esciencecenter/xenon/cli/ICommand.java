package nl.esciencecenter.xenon.cli;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;

import net.sourceforge.argparse4j.inf.Namespace;

public interface ICommand {
    Object run(Namespace res, Xenon xenon) throws XenonException;
}
