package nl.esciencecenter.xenon.cli;

import java.util.ArrayList;
import java.util.List;

public class ListFilesOutput {
    public List<String> objects = new ArrayList<>();
    public List<String> files = new ArrayList<>();
    public List<String> directories = new ArrayList<>();

    @Override
    public String toString() {
        String sep = System.getProperty("line.separator");
        return String.join(sep, objects);
    }
}
