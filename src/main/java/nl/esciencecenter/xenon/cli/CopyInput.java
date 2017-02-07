package nl.esciencecenter.xenon.cli;

import java.util.Map;

import nl.esciencecenter.xenon.credentials.Credential;

public class CopyInput {
    public String scheme;
    public String location = null;
    public String path;
    public Credential credential = null;
    public boolean stream = false;
    public Map<String,String> properties = null;

    public CopyInput(String scheme, String location, String path, Credential credential) {
        this.scheme = scheme;
        this.location = location;
        if (path.equals("-") && (scheme.equals("file") || scheme.equals("local"))) {
            // can only stream stdin or stdout using local adaptor,
            // if not local adaptor will treat path as path, so will read/write remote file called '-'
            this.stream = true;
        }
        this.path = path;
        this.credential = credential;
    }
}
