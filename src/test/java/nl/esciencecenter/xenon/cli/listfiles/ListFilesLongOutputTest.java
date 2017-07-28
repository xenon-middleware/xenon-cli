package nl.esciencecenter.xenon.cli.listfiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.esciencecenter.xenon.adaptors.filesystems.PathAttributesImplementation;
import nl.esciencecenter.xenon.filesystems.Path;
import nl.esciencecenter.xenon.filesystems.PosixFilePermission;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ListFilesLongOutputTest {
    private String sep = System.getProperty("line.separator");

    @Test
    public void test_toString() throws Exception {
        ListFilesLongOutput listing = getListingSample();

        String result = listing.toString();

        String expected = "Type\tPermissions\tHidden\tOwner\tGroup\tSize\tLast modified at\tName" + sep +
                "-\tr--------\tfalse\txenon\tusers\t42\t2017-07-28T09:24:33Z\tfile1" + sep;
        assertEquals(expected, result);
    }
    @Test
    public void toJson() {
        ListFilesLongOutput listing = getListingSample();
        Gson gson = new GsonBuilder().create();
        String result = gson.toJson(listing);
        String expected = "{\"files\":[{\"relativePath\":\"file1\",\"isRegular\":true,\"isDirectory\":false,\"permissions\":[\"OWNER_READ\"],\"owner\":\"xenon\",\"group\":\"users\",\"absolutePath\":\"/start/file1\",\"size\":42,\"lastModifiedAt\":\"2017-07-28T09:24:33Z\",\"isHidden\":false,\"isSymbolicLink\":false,\"isOther\":false}]}";
        assertEquals(expected, result);
    }

    private ListFilesLongOutput getListingSample() {
        PathAttributesImplementation file1 = new PathAttributesImplementation();
        file1.setPath(new Path("/start/file1"));
        file1.setRegular(true);
        file1.setSize(42L);
        file1.setLastModifiedTime(1501233873000L);
        file1.setCreationTime(0L); // mock no support for creation time
        file1.setLastAccessTime(0L); // mock no support for last access time
        file1.setOwner("xenon");
        file1.setGroup("users");
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);
        file1.setPermissions(perms);
        return new ListFilesLongOutput(new Path("/start"), Stream.of(file1));
    }
}