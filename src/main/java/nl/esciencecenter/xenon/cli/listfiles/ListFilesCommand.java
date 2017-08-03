package nl.esciencecenter.xenon.cli.listfiles;

import static nl.esciencecenter.xenon.cli.Utils.createFileSystem;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.filesystems.FileSystem;
import nl.esciencecenter.xenon.filesystems.Path;
import nl.esciencecenter.xenon.filesystems.PathAttributes;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to list objects at path of location
 */
public class ListFilesCommand extends XenonCommand {
    public Object run(Namespace res) throws XenonException {
        String path = res.getString("path");
        Boolean recursive = res.getBoolean("recursive");
        Boolean showhidden = res.getBoolean("hidden");
        Boolean longFormat = res.getBoolean("long");

        FileSystem fs = createFileSystem(res);
        Object listing = listObjects(fs, path, recursive, showhidden, longFormat);
        fs.close();

        return listing;
    }

    private Object listObjects(FileSystem fs, String pathIn, Boolean recursive, Boolean showhidden, Boolean longFormat) throws XenonException {
        Path start = new Path(pathIn);
        Iterable<PathAttributes> iterable = fs.list(start, recursive);

        // apply filters
        Stream<PathAttributes> stream = fsListToStream(iterable);
        if (!showhidden) {
            stream = filterHidden(stream);
        }

        // format
        if (longFormat) {
            return new ListFilesLongOutput(start, stream);
        }
        return new ListFilesOutput(start, stream);
    }

    private static Stream<PathAttributes> fsListToStream(Iterable<PathAttributes> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private static Stream<PathAttributes> filterHidden(Stream<PathAttributes> stream) {
        // keep track of all hidden dirs,
        // if path starts with one of the hidden dirs than path is also hidden
        // only works when parent dir is given before child
        Set<Path> hiddenDirs = new HashSet<>();
        return stream.filter(item -> {
            if (item.isDirectory() && item.isHidden()) {
                hiddenDirs.add(item.getPath());
            }
            // the current item is hidden or one of its parents is hidden
            boolean parentIsHidden = hiddenDirs.stream().anyMatch(d -> item.getPath().startsWith(d));
            boolean itemIsHidden = item.isHidden() || parentIsHidden;
            return !itemIsHidden;
        });
    }
}
