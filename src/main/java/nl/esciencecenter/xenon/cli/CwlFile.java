package nl.esciencecenter.xenon.cli;

import com.google.gson.annotations.SerializedName;

public class CwlFile {
    @SerializedName("class")
    public String clazz = "File";
    public String path;

    public CwlFile(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }
}
