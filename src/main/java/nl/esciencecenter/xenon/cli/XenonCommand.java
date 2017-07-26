package nl.esciencecenter.xenon.cli;

import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.credentials.CertificateCredential;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.credentials.DefaultCredential;
import nl.esciencecenter.xenon.credentials.PasswordCredential;

public abstract class XenonCommand implements ICommand {

    protected Credential buildCredential(Namespace res) {
        return buildCredential(res,  "");
    }

    protected Credential buildCredential(Namespace res, String prefix) {
        String username = res.getString(prefix + "username");
        String passwordAsString = res.getString(prefix + "password");
        String certfile = res.getString(prefix + "certfile");
        char[] password = null;
        if (passwordAsString != null) {
            password = passwordAsString.toCharArray();
        }
        if (certfile != null) {
            return new CertificateCredential(certfile, username, password);
        } else if (password != null) {
            return new PasswordCredential(username, password);
        } else if (username != null) {
            return new DefaultCredential(username);
        } else {
            return new DefaultCredential();
        }
    }
}
