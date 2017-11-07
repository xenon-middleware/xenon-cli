package nl.esciencecenter.xenon.cli.createdir;

import static nl.esciencecenter.xenon.cli.Utils.createFileSystem;

import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.Path;

public class CreateDirectoryCommand extends XenonCommand {
    @Override
    public CreateDirectoryOutput run(Namespace res) throws XenonException {
        String pathIn = res.getString("path");
        Path dir = new Path(pathIn);
        Boolean createParent = res.getBoolean("parents");
        try (FileSystem fs = createFileSystem(res)) {
            if (createParent) {
                fs.createDirectories(dir);
            } else {
                fs.createDirectory(dir);
            }
            return new CreateDirectoryOutput(fs.getLocation(), dir);
        }
    }
}
