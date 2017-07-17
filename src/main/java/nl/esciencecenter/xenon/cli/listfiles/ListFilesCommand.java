package nl.esciencecenter.xenon.cli.listfiles;

import static nl.esciencecenter.xenon.cli.Main.buildXenonProperties;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getAllowedFileSystemPropertyKeys;

import java.util.Map;
import java.util.Set;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.Path;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to list objects at path of location
 */
public class ListFilesCommand extends XenonCommand {

    private ListFilesOutput listObjects(String scheme, String location, String pathIn, Credential credential, Boolean recursive, Boolean hidden, Map<String, String> props) throws XenonException {
        FileSystem fs = FileSystem.create(scheme, location, credential, props);
        Path start = new Path(pathIn);
        ListFilesOutput listing = new ListFilesOutput(start, fs.list(start, recursive), hidden);
        fs.close();
        return listing;
    }

    public ListFilesOutput run(Namespace res) throws XenonException {
        String scheme = res.getString("scheme");
        String location = res.getString("location");
        String path = res.getString("path");
        Boolean recursive = res.getBoolean("recursive");
        Boolean hidden = res.getBoolean("hidden");
        Credential credential = buildCredential(res);
        Set<String> allowedKeys = getAllowedFileSystemPropertyKeys(scheme);
        Map<String, String> props = buildXenonProperties(res, allowedKeys);
        return listObjects(scheme, location, path, credential, recursive, hidden, props);
    }
}
