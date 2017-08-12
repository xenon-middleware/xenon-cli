package nl.esciencecenter.xenon.cli.copy;

import java.util.Map;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.Path;

/**
 * Data required for a source or target of a copy command
 */
public class CopyInput {
    private String adaptor;
    private String location = null;
    private String path;
    private Credential credential = null;
    private boolean stream = false;
    private Map<String, String> properties = null;

    public CopyInput(String adaptor, String location, String path, Credential credential) {
        this(adaptor, location, path, credential, null);
    }

    CopyInput(String adaptor, String location, String path, Credential credential, Map<String, String> properties) {
        this.adaptor = adaptor;
        this.location = location;
        if ("-".equals(path) && ("file".equals(adaptor) || "local".equals(adaptor))) {
            // can only stream stdin or stdout using local adaptor,
            // if not local adaptor will treat path as path, so will read/write remote file called '-'
            this.stream = true;
        }
        this.path = path;
        this.credential = credential;
        this.properties = properties;
    }

    String getAdaptorName() {
        return adaptor;
    }

    public String getLocation() {
        return location;
    }

    public Credential getCredential() {
        return credential;
    }

    public boolean isStream() {
        return stream;
    }

    private Map<String, String> getProperties() {
        return properties;
    }

    public boolean isLocal() {
        return "local".equals(adaptor) || "file".equals(adaptor);
    }

    public Path getPath()  {
        return new Path(path);
    }

    public FileSystem getFileSystem() throws XenonException {
        return FileSystem.create(getAdaptorName(), getLocation(), getCredential(), getProperties());
    }
}
