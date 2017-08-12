package nl.esciencecenter.xenon.cli.removefile;

import static nl.esciencecenter.xenon.cli.Utils.createFileSystem;

import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.Path;

/**
 * Command to remove path at location
 */
public class RemoveFileCommand extends XenonCommand {
    @Override
    public RemoveFileOutput run(Namespace res) throws XenonException {
        FileSystem fs = createFileSystem(res);
        String path = res.getString("path");
        Boolean recursive = res.getBoolean("recursive");
        remove(fs, path, recursive);
        return new RemoveFileOutput(fs.getLocation(), path);
    }

    private void remove(FileSystem fs, String pathIn, Boolean recursive) throws XenonException {
        Path path = new Path(pathIn);
        fs.delete(path, recursive);

        fs.close();
    }
}
