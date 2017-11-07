package nl.esciencecenter.xenon.cli.rename;

import static nl.esciencecenter.xenon.cli.Utils.createFileSystem;

import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.Path;

public class RenameCommand extends XenonCommand {
    @Override
    public RenameOutput run(Namespace res) throws XenonException {
        String sourceIn = res.getString("source");
        String targetIn = res.getString("target");
        Path source = new Path(sourceIn);
        Path target = new Path(targetIn);

        try (FileSystem fs = createFileSystem(res)) {
            fs.rename(source, target);
            return new RenameOutput(fs.getLocation(), source, target);
        }
    }
}
