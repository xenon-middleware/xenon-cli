package nl.esciencecenter.xenon.cli.createdir;

import static nl.esciencecenter.xenon.cli.Utils.createFileSystem;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.Path;

import net.sourceforge.argparse4j.inf.Namespace;

public class CreateDirectoryCommand extends XenonCommand {
    @Override
    public CreateDirectoryOutput run(Namespace res) throws XenonException {
        FileSystem fs = createFileSystem(res);
        String pathIn = res.getString("path");
        Path dir = new Path(pathIn);
        Boolean createParent = res.getBoolean("parents");
        if (createParent) {
            fs.createDirectories(dir);
        } else {
            fs.createDirectory(dir);
        }
        fs.close();
        return new CreateDirectoryOutput(fs.getLocation(), dir);
    }
}
