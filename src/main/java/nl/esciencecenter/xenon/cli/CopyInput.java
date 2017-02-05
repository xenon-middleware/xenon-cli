package nl.esciencecenter.xenon.cli;

import nl.esciencecenter.xenon.credentials.Credential;

import java.util.Map;

public class CopyInput {
    public String scheme;
    public String location = null;
    public String path;
    public Credential credential = null;
    public Map<String,String> properties = null;

    public CopyInput(String scheme, String location, String path, Credential credential) {
        this.scheme = scheme;
        this.location = location;
        this.path = path;
        this.credential = credential;
    }
}
