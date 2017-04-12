package nl.esciencecenter.xenon.cli.listfiles;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Listing of files and directories of a location
 */
public class ListFilesOutput {
    private Set<String> objects = new HashSet<>();
    private Set<String> files = new HashSet<>();
    private Set<String> directories = new HashSet<>();

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
