package nl.esciencecenter.xenon.cli.listfiles;

import nl.esciencecenter.xenon.filesystems.Path;
import nl.esciencecenter.xenon.filesystems.PathAttributes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListFilesLongOutput {
    public final List<LongListItem> files;

    ListFilesLongOutput(Path start, Stream<PathAttributes> stream) {
        this.files = stream.map(o -> new LongListItem(o, start)).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListFilesLongOutput that = (ListFilesLongOutput) o;
        return Objects.equals(files, that.files);
    }

    @Override
    public int hashCode() {
        return Objects.hash(files);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String sep = System.getProperty("line.separator");
        sb.append(LongListItem.getHeader()).append(sep);
        for (LongListItem file : files) {
            sb.append(file.toString()).append(sep);
        }
        return sb.toString();
    }
}
