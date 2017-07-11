package nl.esciencecenter.xenon.cli.copy;

import static nl.esciencecenter.xenon.cli.Main.buildXenonProperties;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getAllowedXenonPropertyKeys;

import java.util.Map;
import java.util.Set;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonPropertyDescription;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.files.CopyOption;
import nl.esciencecenter.xenon.files.FileSystem;
import nl.esciencecenter.xenon.files.Files;
import nl.esciencecenter.xenon.files.NoSuchCopyException;
import nl.esciencecenter.xenon.files.Path;
import nl.esciencecenter.xenon.files.RelativePath;
import nl.esciencecenter.xenon.util.Utils;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Copy source file/directory to target location command
 */
public class CopyCommand extends XenonCommand {
    protected void copy(Files files, CopyInput source, CopyInput target, Boolean recursive, CopyOption copymode) throws XenonException {
        if (recursive && source.isStream()) {
            throw new NoSuchCopyException(source.getScheme(), "Unable to do recursive copy from stdin");
        }
        if (recursive && target.isStream()) {
            throw new NoSuchCopyException(target.getScheme(), "Unable to do recursive copy to stdout");
        }

        FileSystem sourceFS = files.newFileSystem(source.getScheme(), source.getLocation(), source.getCredential(), source.getProperties());
        FileSystem targetFS = files.newFileSystem(target.getScheme(), target.getLocation(), target.getCredential(), target.getProperties());

        Path sourcePath = getAbsolutePath(files, source, sourceFS);
        Path targetPath = getAbsolutePath(files, target, targetFS);

        copy(files, source, target, recursive, copymode, sourcePath, targetPath);
        files.close(sourceFS);
        files.close(targetFS);
    }

    private Path getAbsolutePath(Files files, CopyInput source, FileSystem sourceFS) throws XenonException {
        Path sourcePath = files.newPath(sourceFS, new RelativePath(source.getPath()));
        if ("local".equals(source.getScheme()) || "file".equals(source.getScheme()) && !(source.getPath().startsWith("/") || "-".equals(source.getPath()))) {
            // Path is relative to working directory, make it absolute
            RelativePath workingDirectory = new RelativePath(System.getProperty("user.dir"));
            sourcePath = files.newPath(sourceFS, workingDirectory.resolve(source.getPath()));
        }
        return sourcePath;
    }

    private void copy(Files files, CopyInput source, CopyInput target, Boolean recursive, CopyOption copymode, Path sourcePath, Path targetPath) throws XenonException {
        if (source.isStream() && !target.isStream()) {
            // copy from stdin
            if (copymode.occursIn(CopyOption.REPLACE)) {
                Utils.copy(files, System.in, targetPath, true);
            } else {
                Utils.copy(files, System.in, targetPath, false);
            }
        } else if (!source.isStream() && target.isStream()) {
            // copy to stdout
            Utils.copy(files, sourcePath, System.out);
        } else if (recursive) {
            Utils.recursiveCopy(files, sourcePath, targetPath, copymode);
        } else {
            files.copy(sourcePath, targetPath, copymode);
        }
    }

    @Override
    public CopyOutput run(Namespace res, Xenon xenon) throws XenonException {
        String scheme = res.getString("scheme");
        String sourceLocation = res.getString("location");
        String sourcePath = res.getString("source_path");
        Credential sourceCredential = buildCredential(res, xenon);
        String targetLocation = res.getString("target_location");
        String targetPath = res.getString("target_path");
        Credential targetCredential = buildCredential(res, xenon, "target_");
        CopyOption copymode = res.get("copymode");
        Boolean recursive = res.getBoolean("recursive");
        Set<String> allowedKeys = getAllowedXenonPropertyKeys(xenon, scheme, XenonPropertyDescription.Component.FILESYSTEM);
        Map<String, String> props = buildXenonProperties(res, allowedKeys);

        CopyInput source = new CopyInput(scheme, sourceLocation, sourcePath, sourceCredential, props);
        CopyInput target = new CopyInput(scheme, targetLocation, targetPath, targetCredential, props);

        Files files = xenon.files();
        this.copy(files, source, target, recursive, copymode);

        if (target.isStream()) {
            return null;
        } else {
            return new CopyOutput(source, target);
        }
    }
}
