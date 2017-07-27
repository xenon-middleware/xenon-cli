package nl.esciencecenter.xenon.cli.rename;

import static nl.esciencecenter.xenon.cli.Utils.createFileSystem;
import static nl.esciencecenter.xenon.cli.Utils.getAbsolutePath;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.Path;

import net.sourceforge.argparse4j.inf.Namespace;

public class RenameCommand extends XenonCommand {
    @Override
    public RenameOutput run(Namespace res) throws XenonException {
        FileSystem fs = createFileSystem(res);
        String sourceIn = res.getString("source");
        String targetIn = res.getString("target");
        Path source = getAbsolutePath(fs.getAdaptorName(), sourceIn);
        Path target = getAbsolutePath(fs.getAdaptorName(), targetIn);

        fs.rename(source, target);

        fs.close();
        return new RenameOutput(fs.getLocation(), source, target);
    }
}
