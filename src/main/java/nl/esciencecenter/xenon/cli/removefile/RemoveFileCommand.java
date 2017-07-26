package nl.esciencecenter.xenon.cli.removefile;

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
 * Command to remove path at location
 */
public class RemoveFileCommand extends XenonCommand {
    @Override
    public RemoveFileOutput run(Namespace res) throws XenonException {
        String adaptor = res.getString("adaptor");
        String location = res.getString("location");
        String path = res.getString("path");
        Boolean recursive = res.getBoolean("recursive");
        Credential credential = buildCredential(res);
        Set<String> allowedKeys = getAllowedFileSystemPropertyKeys(adaptor);
        Map<String, String> props = buildXenonProperties(res, allowedKeys);
        remove(adaptor, location, path, credential, props, recursive);
        return new RemoveFileOutput(location, path);
    }

    private void remove(String adaptor, String location, String pathIn, Credential credential, Map<String, String> props, boolean recursive) throws XenonException {
        FileSystem fs = FileSystem.create(adaptor, location, credential, props);

        Path path = new Path(pathIn);
        if ("local".equals(adaptor) || "file".equals(adaptor) && !pathIn.startsWith("/")) {
            // Path is relative to working directory, make it absolute
            Path workingDirectory = new Path(System.getProperty("user.dir"));
            path = workingDirectory.resolve(pathIn);
        }
        fs.delete(path, recursive);

        fs.close();
    }
}
