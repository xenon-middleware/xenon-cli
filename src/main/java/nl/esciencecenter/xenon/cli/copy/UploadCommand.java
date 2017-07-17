package nl.esciencecenter.xenon.cli.copy;

import static nl.esciencecenter.xenon.cli.Main.buildXenonProperties;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getAllowedFileSystemPropertyKeys;

import java.util.Map;
import java.util.Set;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.filesystems.CopyMode;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to upload a local file to target
 */
public class UploadCommand extends CopyCommand {
    @Override
    public CopyOutput run(Namespace res) throws XenonException {
        String sourcePath = res.getString("source");
        String targetScheme = res.getString("scheme");
        String targetLocation = res.getString("location");
        String targetPath = res.getString("target");
        Credential targetCredential = buildCredential(res);
        CopyMode copymode = res.get("copymode");
        Boolean recursive = res.getBoolean("recursive");
        Set<String> targetAllowedKeys = getAllowedFileSystemPropertyKeys(targetScheme);
        Map<String, String> targetProps = buildXenonProperties(res, targetAllowedKeys);

        CopyInput source = new CopyInput("file", null, sourcePath, null);
        CopyInput target = new CopyInput(targetScheme, targetLocation, targetPath, targetCredential, targetProps);

        return this.copy(source, target, recursive, copymode);
    }
}
