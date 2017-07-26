package nl.esciencecenter.xenon.cli.listfiles;

import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.Path;

import java.util.Map;
import java.util.Set;

import static nl.esciencecenter.xenon.cli.Main.buildXenonProperties;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getAllowedFileSystemPropertyKeys;
import static nl.esciencecenter.xenon.cli.Utils.createFileSystem;
import static nl.esciencecenter.xenon.cli.Utils.getAbsolutePath;

/**
 * Command to list objects at path of location
 */
public class ListFilesCommand extends XenonCommand {
    public ListFilesOutput run(Namespace res) throws XenonException {
        FileSystem fs = createFileSystem(res);
        String path = res.getString("path");
        Boolean recursive = res.getBoolean("recursive");
        Boolean hidden = res.getBoolean("hidden");
        return listObjects(fs, path, recursive, hidden);
    }

    private ListFilesOutput listObjects(FileSystem fs, String pathIn, Boolean recursive, Boolean hidden) throws XenonException {
        Path start = getAbsolutePath(fs.getAdaptorName(), pathIn);
        ListFilesOutput listing = new ListFilesOutput(start, fs.list(start, recursive), hidden);
        fs.close();
        return listing;
    }
}
