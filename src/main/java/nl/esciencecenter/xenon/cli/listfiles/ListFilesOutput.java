package nl.esciencecenter.xenon.cli.listfiles;

import nl.esciencecenter.xenon.filesystems.Path;
import nl.esciencecenter.xenon.filesystems.PathAttributes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Listing of files and directories of a location
 */
public class ListFilesOutput {
    private final List<String> files;

    public ListFilesOutput(Path start, Stream<PathAttributes> stream) {
        files = stream.map(item -> start.relativize(item.getPath()).getRelativePath()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        String sep = System.getProperty("line.separator");
        return String.join(sep, files);
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
        return Objects.equals(files, that.files);
    }

    @Override
    public int hashCode() {
        return Objects.hash(files);
    }
}
