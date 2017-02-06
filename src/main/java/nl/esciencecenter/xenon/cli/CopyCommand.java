package nl.esciencecenter.xenon.cli;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.files.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CopyCommand extends XenonCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyCommand.class);

    protected void copy(Files files, CopyInput source, CopyInput target) throws XenonException {
        FileSystem sourceFS = files.newFileSystem(source.scheme, source.location, source.credential, source.properties);
        FileSystem targetFS = files.newFileSystem(target.scheme, target.location, target.credential, target.properties);

        Path sourcePath = files.newPath(sourceFS, new RelativePath(source.path));
        if ("local".equals(source.scheme) || "file".equals(source.scheme)) {
                if (!source.path.startsWith("/")) {
                    // Path is relative to working directory, make it absolute
                    RelativePath workingDirectory = new RelativePath(System.getProperty("user.dir"));
                    sourcePath = files.newPath(sourceFS, workingDirectory.resolve(source.path));
                }
        }
        Path targetPath = files.newPath(targetFS, new RelativePath(target.path));
        if ("local".equals(target.scheme) || "file".equals(target.scheme)) {
            if (!target.path.startsWith("/")) {
                // Path is relative to working directory, make it absolute
                RelativePath workingDirectory = new RelativePath(System.getProperty("user.dir"));
                sourcePath = files.newPath(sourceFS, workingDirectory.resolve(target.path));
            }
        }

        files.copy(sourcePath, targetPath, CopyOption.CREATE);

        files.close(sourceFS);
        files.close(targetFS);
    }
}
