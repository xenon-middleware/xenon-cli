package nl.esciencecenter.xenon.cli;

import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.credentials.Credential;

import static nl.esciencecenter.xenon.cli.Utils.createCredential;

public abstract class XenonCommand implements ICommand {

    protected Credential buildCredential(Namespace res) {
        return buildCredential(res,  "");
    }

    protected Credential buildCredential(Namespace res, String prefix) {
        return createCredential(res, prefix);
    }
}
