package nl.esciencecenter.xenon.cli.copy;

import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.filesystems.CopyMode;

import java.util.Map;

import static nl.esciencecenter.xenon.cli.Utils.buildXenonProperties;

/**
 * Command to upload a local file to target
 */
public class UploadCommand extends CopyCommand {
    @Override
    public CopyOutput run(Namespace res) throws XenonException {
        String sourcePath = res.getString("source");
        String targetAdaptorName = res.getString("adaptor");
        String targetLocation = res.getString("location");
        String targetPath = res.getString("target");
        Credential targetCredential = buildCredential(res);
        CopyMode copymode = res.get("copymode");
        Boolean recursive = res.getBoolean("recursive");
        Map<String, String> targetProps = buildXenonProperties(res);

        CopyInput source = new CopyInput("file", null, sourcePath, null);
        CopyInput target = new CopyInput(targetAdaptorName, targetLocation, targetPath, targetCredential, targetProps);

        return this.copy(source, target, recursive, copymode);
    }
}
