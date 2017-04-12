package nl.esciencecenter.xenon.cli.copy;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.files.CopyOption;
import nl.esciencecenter.xenon.files.Files;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to upload a local file to target
 */
public class UploadCommand extends CopyCommand {
    @Override
    public CopyOutput run(Namespace res, Xenon xenon) throws XenonException {
        String sourcePath = res.getString("source");
        String targetScheme = res.getString("scheme");
        String targetLocation = res.getString("location");
        String targetPath = res.getString("target");
        Credential targetCredential = buildCredential(res, xenon);
        CopyOption copymode = res.get("copymode");
        Boolean recursive = res.getBoolean("recursive");

        CopyInput source = new CopyInput("file", null, sourcePath, null);
        CopyInput target = new CopyInput(targetScheme, targetLocation, targetPath, targetCredential);

        Files files = xenon.files();
        this.copy(files, source, target, recursive, copymode);

        return new CopyOutput(source, target);
    }
}
