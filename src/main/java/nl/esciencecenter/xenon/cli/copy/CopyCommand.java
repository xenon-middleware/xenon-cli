package nl.esciencecenter.xenon.cli.copy;

import static nl.esciencecenter.xenon.cli.Utils.buildTargetXenonProperties;
import static nl.esciencecenter.xenon.cli.Utils.buildXenonProperties;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getAllowedFileSystemPropertyKeys;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonRuntimeException;
import nl.esciencecenter.xenon.cli.Utils;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.filesystems.CopyMode;
import nl.esciencecenter.xenon.filesystems.CopyStatus;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.NoSuchCopyException;
import nl.esciencecenter.xenon.filesystems.Path;

/**
 * Copy source file/directory to target location command
 */
public class CopyCommand extends XenonCommand {
    /**
     * Default timeout for copy operation is a week
     */
    private static final long DEFAULT_COPY_TIMEOUT = 1000 * 60 * 60 * 24 * 7L;

    protected CopyOutput copy(CopyInput source, CopyInput target, Boolean recursive, CopyMode copymode) throws XenonException {
        if (recursive && source.isStream()) {
            throw new NoSuchCopyException(source.getAdaptorName(), "Unable to do recursive copy from stdin");
        }
        if (recursive && target.isStream()) {
            throw new NoSuchCopyException(target.getAdaptorName(), "Unable to do recursive copy to stdout");
        }

        Path sourcePath = source.getPath();
        Path targetPath = target.getPath();
        try (
            FileSystem sourceFS = source.getFileSystem();
            FileSystem targetFS = target.getFileSystem()
            ) {
            long bytesCopied;
            if (source.isStream() && !target.isStream()) {
                bytesCopied = copyFromSystemIn(targetPath, targetFS, copymode, recursive);
            } else if (!source.isStream() && target.isStream()) {
                bytesCopied = copyToSystemOut(sourcePath, sourceFS);
            } else {
                bytesCopied = copy(sourcePath, sourceFS, targetPath, targetFS, recursive, copymode).bytesCopied();
            }
            return new CopyOutput(source, target, bytesCopied);
        }
    }

    private CopyStatus copy(Path sourcePath, FileSystem sourceFS, Path targetPath, FileSystem targetFS, Boolean recursive, CopyMode copymode) throws XenonException {
        String copyId = sourceFS.copy(sourcePath, targetFS, targetPath, copymode, recursive);
        CopyStatus status = sourceFS.waitUntilDone(copyId, DEFAULT_COPY_TIMEOUT);
        if (status.hasException()) {
            Throwable e = status.getException();
            if (e != null) {
                throw (XenonException) e;
            }
        }
        return status;
    }

    private long copyToSystemOut(Path sourcePath, FileSystem sourceFS) throws XenonException {
        InputStream in = sourceFS.readFromFile(sourcePath);
        OutputStream out = System.out;
        try {
            return Utils.pipe(in, out);
        } catch (IOException e) {
            throw new XenonRuntimeException("file", e.getMessage(), e);
        }
    }

    private long copyFromSystemIn(Path targetPath, FileSystem targetFS, CopyMode copymode, Boolean recursive) throws XenonException {
        if (CopyMode.REPLACE.equals(copymode) && targetFS.exists(targetPath)) {
            targetFS.delete(targetPath, recursive);
        }
        InputStream in = System.in;
        OutputStream out = targetFS.writeToFile(targetPath);
        try {
            return Utils.pipe(in, out);
        } catch (IOException e) {
            throw new XenonRuntimeException("file", e.getMessage(), e);
        }
    }

    @Override
    public CopyOutput run(Namespace res) throws XenonException {
        String adaptor = res.getString("adaptor");
        String sourceLocation = res.getString("location");
        String sourcePath = res.getString("source_path");
        Credential sourceCredential = buildCredential(res);
        String targetLocation = res.getString("target_location");
        if (targetLocation == null) {
            targetLocation = sourceLocation;
        }
        String targetPath = res.getString("target_path");
        Credential targetCredential = buildCredential(res, "target_");
        CopyMode copymode = res.get("copymode");
        Boolean recursive = res.getBoolean("recursive");

        Set<String> allowedKeys = getAllowedFileSystemPropertyKeys(adaptor);
        Map<String, String> sourceProps = buildXenonProperties(res, allowedKeys);
        Map<String, String> targetProps = buildTargetXenonProperties(res, allowedKeys);
        if (targetProps.isEmpty() && !sourceProps.isEmpty()) {
            targetProps = sourceProps;
        }

        CopyInput source = new CopyInput(adaptor, sourceLocation, sourcePath, sourceCredential, sourceProps);
        CopyInput target = new CopyInput(adaptor, targetLocation, targetPath, targetCredential, targetProps);

        CopyOutput result = this.copy(source, target, recursive, copymode);

        if (target.isStream()) {
            return null;
        } else {
            return result;
        }
    }
}
