package nl.esciencecenter.xenon.cli;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.files.*;

public abstract class CopyCommand extends XenonCommand {
    protected void copy(Files files, CopyInput source, CopyInput target) throws XenonException {
        FileSystem sourceFS = files.newFileSystem(source.scheme, source.location, source.credential, source.properties);
        FileSystem targetFS = files.newFileSystem(target.scheme, target.location, target.credential, target.properties);

        Path sourcePath = files.newPath(sourceFS, new RelativePath(source.path));
        Path targetPath = files.newPath(targetFS, new RelativePath(target.path));

        files.copy(sourcePath, targetPath, CopyOption.CREATE);

        files.close(sourceFS);
        files.close(targetFS);
    }
}
