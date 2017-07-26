package nl.esciencecenter.xenon.cli.listfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import nl.esciencecenter.xenon.filesystems.Path;
import nl.esciencecenter.xenon.filesystems.PathAttributes;

/**
 * Listing of files and directories of a location
 */
public class ListFilesOutput {
    private List<String> objects = new ArrayList<>();
    private List<String> files = new ArrayList<>();
    private List<String> directories = new ArrayList<>();

    ListFilesOutput(Path start, Iterable<PathAttributes> items, Boolean hidden) {
        // keep track of all hidden dirs,
        // if path starts with one of the hidden dirs than path is also hidden
        // only works when parent dir is given before child
        Set<Path> hiddenDirs = new HashSet<>();
        for (PathAttributes item: items) {
            String path = start.relativize(item.getPath()).getRelativePath();
            if (!hidden && item.isDirectory() && item.isHidden()) {
                hiddenDirs.add(item.getPath());
            }
            // the current item is hidden or one of its parents is hidden
            boolean parentIsHidden = hiddenDirs.stream().anyMatch(d -> item.getPath().startsWith(d));
            boolean itemIsHidden = item.isHidden() || parentIsHidden;
            if (!itemIsHidden || hidden) {
                objects.add(path);
                if (item.isDirectory()) {
                    directories.add(path);
                } else if (item.isRegular()) {
                    files.add(path);
                }
            }
        }
    }

    @Override
    public String toString() {
        String sep = System.getProperty("line.separator");
        return String.join(sep, objects) + sep;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ListFilesOutput that = (ListFilesOutput) o;
        return Objects.equals(objects, that.objects) &&
                Objects.equals(files, that.files) &&
                Objects.equals(directories, that.directories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objects, files, directories);
    }

    public List<String> getObjects() {
        return objects;
    }
}
