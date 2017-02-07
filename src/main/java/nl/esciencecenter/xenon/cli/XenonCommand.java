package nl.esciencecenter.xenon.cli;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.credentials.Credentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.sourceforge.argparse4j.inf.Namespace;

public abstract class XenonCommand implements ICommand {

    protected Credential buildCredential(Namespace res, Xenon xenon) throws XenonException {
        return buildCredential(res, xenon, "");
    }

    protected Credential buildCredential(Namespace res, Xenon xenon, String prefix)  throws XenonException {
        String scheme = res.getString("scheme");
        String username = res.getString(prefix + "username");
        String passwordAsString = res.getString(prefix + "password");
        String certfile = res.getString(prefix + "certfile");
        char[] password = null;
        if (passwordAsString != null) {
            password = passwordAsString.toCharArray();
        }
        Credentials credentials = xenon.credentials();
        if (certfile != null) {
            return credentials.newCertificateCredential(scheme, certfile, username, password, null);
        } else if (username != null) {
            return credentials.newPasswordCredential(scheme, username, password, null);
        } else {
            return credentials.getDefaultCredential(scheme);
        }
    }

    protected void print(Object output, String format) {
        if ("cwljson".equals(format)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.print(gson.toJson(output));
        } else {
            System.out.println(output);
        }
    }
}
