package nl.esciencecenter.xenon.cli.listfiles;

import nl.esciencecenter.xenon.filesystems.AttributeNotSupportedException;
import nl.esciencecenter.xenon.filesystems.Path;
import nl.esciencecenter.xenon.filesystems.PathAttributes;
import nl.esciencecenter.xenon.filesystems.PosixFilePermission;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LongListItemTest {

    @Test
    public void construct_notSupported_nulls() throws AttributeNotSupportedException {
        Path start = new Path("/start");
        PathAttributes attribs = mock(PathAttributes.class);
        when(attribs.getPath()).thenReturn(new Path("/start/file1"));
        when(attribs.getOwner()).thenThrow(new AttributeNotSupportedException("file", "owner not supported"));
        when(attribs.getGroup()).thenThrow(new AttributeNotSupportedException("file", "group not supported"));
        when(attribs.getPermissions()).thenThrow(new AttributeNotSupportedException("file", "permissions not supported"));

        LongListItem item = new LongListItem(attribs, start);

        assertNull(item.owner);
        assertNull(item.group);
        Set<PosixFilePermission> expected = new HashSet<>();
        assertEquals("Empty permissions", expected, item.permissions);
    }

    @Test
    public void construct_permissionsNotSupportedButExecutableWriteableReadable_otherPermissionsSet() throws AttributeNotSupportedException {
        Path start = new Path("/start");
        PathAttributes attribs = mock(PathAttributes.class);
        when(attribs.getPath()).thenReturn(new Path("/start/file1"));
        when(attribs.isExecutable()).thenReturn(true);
        when(attribs.isWritable()).thenReturn(true);
        when(attribs.isReadable()).thenReturn(true);
        when(attribs.getPermissions()).thenThrow(new AttributeNotSupportedException("file", "permissions not supported"));

        LongListItem item = new LongListItem(attribs, start);

        assertNull(item.owner);
        assertNull(item.group);
        Set<PosixFilePermission> expected = new HashSet<>();
        expected.add(PosixFilePermission.OTHERS_EXECUTE);
        expected.add(PosixFilePermission.OTHERS_WRITE);
        expected.add(PosixFilePermission.OTHERS_READ);
        assertEquals("------rwx permissions", expected, item.permissions);
    }

    @Test
    public void test_toString_symbolicLink() throws AttributeNotSupportedException {
        Path start = new Path("/start");
        PathAttributes attribs = mock(PathAttributes.class);
        when(attribs.getPath()).thenReturn(new Path("/start/file1"));
        when(attribs.getLastModifiedTime()).thenReturn(0L);
        when(attribs.isSymbolicLink()).thenReturn(true);
        when(attribs.getOwner()).thenThrow(new AttributeNotSupportedException("file", "owner not supported"));
        when(attribs.getGroup()).thenThrow(new AttributeNotSupportedException("file", "group not supported"));
        when(attribs.getPermissions()).thenThrow(new AttributeNotSupportedException("file", "permissions not supported"));

        LongListItem item = new LongListItem(attribs, start);

        String expected = "l\t---------\tfalse\t\t\t0\t\tfile1";
        assertEquals(expected, item.toString());
    }

    @Test
    public void test_toString_other() throws AttributeNotSupportedException {
        Path start = new Path("/start");
        PathAttributes attribs = mock(PathAttributes.class);
        when(attribs.getPath()).thenReturn(new Path("/start/file1"));
        when(attribs.getLastModifiedTime()).thenReturn(0L);
        when(attribs.isOther()).thenReturn(true);
        when(attribs.getOwner()).thenThrow(new AttributeNotSupportedException("file", "owner not supported"));
        when(attribs.getGroup()).thenThrow(new AttributeNotSupportedException("file", "group not supported"));
        when(attribs.getPermissions()).thenThrow(new AttributeNotSupportedException("file", "permissions not supported"));

        LongListItem item = new LongListItem(attribs, start);

        String expected = "o\t---------\tfalse\t\t\t0\t\tfile1";
        assertEquals(expected, item.toString());
    }
}