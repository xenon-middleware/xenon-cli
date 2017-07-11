package nl.esciencecenter.xenon.cli.copy;

import static nl.esciencecenter.xenon.cli.Main.buildXenonProperties;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getAllowedXenonPropertyKeys;

import java.util.Map;
import java.util.Set;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonPropertyDescription;
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
        Set<String> sourceAllowedKeys = getAllowedXenonPropertyKeys(xenon, sourceScheme, XenonPropertyDescription.Component.FILESYSTEM);
        Map<String, String> sourceProps = buildXenonProperties(res, sourceAllowedKeys);

        CopyInput source = new CopyInput(sourceScheme, sourceLocation, sourcePath, sourceCredential, sourceProps);
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
