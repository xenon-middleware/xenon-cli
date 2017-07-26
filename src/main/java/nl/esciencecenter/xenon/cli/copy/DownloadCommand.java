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
 * Command to download source file to local file
 */
public class DownloadCommand extends CopyCommand {
    @Override
    public CopyOutput run(Namespace res) throws XenonException {
        String sourceAdaptorName = res.getString("adaptor");
        String sourceLocation = res.getString("location");
        String sourcePath = res.getString("source");
        Credential sourceCredential = buildCredential(res);
        String targetPath = res.getString("target");
        CopyMode copymode = res.get("copymode");
        Boolean recursive = res.getBoolean("recursive");
        Set<String> sourceAllowedKeys = getAllowedFileSystemPropertyKeys(sourceAdaptorName);
        Map<String, String> sourceProps = buildXenonProperties(res, sourceAllowedKeys);

        CopyInput source = new CopyInput(sourceAdaptorName, sourceLocation, sourcePath, sourceCredential, sourceProps);
        CopyInput target = new CopyInput("file", null, targetPath, null);

        CopyOutput result = this.copy(source, target, recursive, copymode);

        if (target.isStream()) {
            return null;
        } else {
            return result;
        }
    }
}
