package nl.esciencecenter.xenon.cli.copy;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.files.CopyOption;
import nl.esciencecenter.xenon.files.Files;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to download source file to local file
 */
public class DownloadCommand extends CopyCommand {
    @Override
    public CopyOutput run(Namespace res, Xenon xenon) throws XenonException {
        String sourceScheme = res.getString("scheme");
        String sourceLocation = res.getString("location");
        String sourcePath = res.getString("source");
        Credential sourceCredential = buildCredential(res, xenon);
        String targetPath = res.getString("target");
        CopyOption copymode = res.get("copymode");
        Boolean recursive = res.getBoolean("recursive");

        CopyInput source = new CopyInput(sourceScheme, sourceLocation, sourcePath, sourceCredential);
        CopyInput target = new CopyInput("file", null, targetPath, null);

        Files files = xenon.files();
        this.copy(files, source, target, recursive, copymode);

        if (target.isStream()) {
            return null;
        } else {
            return new CopyOutput(source, target);
        }
    }
}
