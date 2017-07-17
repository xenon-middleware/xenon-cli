package nl.esciencecenter.xenon.cli.listfiles;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import nl.esciencecenter.xenon.filesystems.Path;
import nl.esciencecenter.xenon.filesystems.PathAttributes;

/**
 * Listing of files and directories of a location
 */
public class ListFilesOutput {
    private Set<String> objects = new HashSet<>();
    private Set<String> files = new HashSet<>();
    private Set<String> directories = new HashSet<>();

    public ListFilesOutput(Path start, Iterable<PathAttributes> items, Boolean hidden) {
        for (PathAttributes item: items) {
            if (!item.isHidden() || hidden) {
                String path = start.relativize(item.getPath()).toString();
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

    public void addFile(String filename) {
        files.add(filename);
        objects.add(filename);
    }

    public void addDirectory(String filename) {
        directories.add(filename);
        objects.add(filename);
    }
}
