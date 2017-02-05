package nl.esciencecenter.xenon.cli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.AdaptorStatus;
import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonFactory;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.credentials.Credentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class XenonCommand implements ICommand {

    protected Credential buildCredential(Namespace res, Xenon xenon) throws XenonException {
        String scheme = res.getString("scheme");
        String username = res.getString("username");
        String passwordAsString = res.getString("password");
        String certfile = res.getString("certfile");
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

    protected void print(Object output, Boolean json) {
        if (json) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.print(gson.toJson(output));
        } else {
            System.out.println(output);
        }
    }


    protected List<String> getSupportedLocations() {
        List<String> locations = new ArrayList<>();
        try {
            Xenon xenon = XenonFactory.newXenon(null);
            AdaptorStatus[] adaptors = xenon.getAdaptorStatuses();
            Arrays.stream(adaptors).forEach((adaptor) -> Collections.addAll(locations, adaptor.getSupportedLocations()));
            XenonFactory.endXenon(xenon);
        } catch (XenonException e) {
            System.err.print(e);
        }
        return locations;
    }

    protected String getSupportedLocationHelp() {
        List<String> helps = getSupportedLocations().stream().distinct().map((location) -> "- " + location).collect(Collectors.toList());
        helps.add(0, "Supported locations:");
        String sep = System.getProperty("line.separator");
        return String.join(sep, helps);
    }
}
