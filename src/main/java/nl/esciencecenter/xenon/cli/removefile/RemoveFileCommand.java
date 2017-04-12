package nl.esciencecenter.xenon.cli.removefile;

import static nl.esciencecenter.xenon.util.Utils.recursiveDelete;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.files.FileSystem;
import nl.esciencecenter.xenon.files.Files;
import nl.esciencecenter.xenon.files.Path;
import nl.esciencecenter.xenon.files.RelativePath;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to remove path at location
 */
public class RemoveFileCommand extends XenonCommand {
    @Override
    public RemoveFileOutput run(Namespace res, Xenon xenon) throws XenonException {
        String scheme = res.getString("scheme");
        String location = res.getString("location");
        String path = res.getString("path");
        Files files = xenon.files();
        Credential credential = buildCredential(res, xenon);
        remove(files, scheme, location, path, credential);
        return new RemoveFileOutput(location, path);
    }

    private void remove(Files files, String scheme, String location, String pathIn, Credential credential) throws XenonException {
        FileSystem fs = files.newFileSystem(scheme, location, credential, null);

        Path path = files.newPath(fs, new RelativePath(pathIn));
        if ("local".equals(scheme) || "file".equals(scheme) && !pathIn.startsWith("/")) {
            // Path is relative to working directory, make it absolute
            RelativePath workingDirectory = new RelativePath(System.getProperty("user.dir"));
            path = files.newPath(fs, workingDirectory.resolve(pathIn));
        }
        recursiveDelete(files, path);
    }
}
