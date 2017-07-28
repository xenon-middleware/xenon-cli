package nl.esciencecenter.xenon.cli.listfiles;

import nl.esciencecenter.xenon.filesystems.AttributeNotSupportedException;
import nl.esciencecenter.xenon.filesystems.Path;
import nl.esciencecenter.xenon.filesystems.PathAttributes;
import nl.esciencecenter.xenon.filesystems.PosixFilePermission;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class LongListItem {
    public final String relativePath;
    public final boolean isRegular;
    public final boolean isDirectory;
    final Set<PosixFilePermission> permissions;
    final String owner;
    final String group;
    private final String absolutePath;
    private final long size;
    private final String createdAt;
    private final String lastAccessedAt;
    private final String lastModifiedAt;
    private final boolean isHidden;
    private final boolean isSymbolicLink;
    private final boolean isOther;

    LongListItem(PathAttributes attribs, Path start) {
        relativePath = start.relativize(attribs.getPath()).getRelativePath();
        absolutePath = attribs.getPath().getAbsolutePath();
        size = attribs.getSize();
        permissions = buildPermissions(attribs);
        owner = buildOwner(attribs);
        group = buildGroup(attribs);
        createdAt = buildISO8601(attribs.getCreationTime());
        lastAccessedAt = buildISO8601(attribs.getLastAccessTime());
        lastModifiedAt = buildISO8601(attribs.getLastModifiedTime());
        isHidden = attribs.isHidden();
        isRegular = attribs.isRegular();
        isDirectory = attribs.isDirectory();
        isSymbolicLink = attribs.isSymbolicLink();
        isOther = attribs.isOther();
    }

    static String getHeader() {
        return String.join("\t", "Type", "Permissions", "Hidden", "Owner", "Group", "Size", "Last modified at", "Name");
    }

    private String buildISO8601(long msSinceEpoch) {
        if (msSinceEpoch == 0) {
            return null;
        }
        return Instant.ofEpochMilli(msSinceEpoch).toString();
    }

    private String buildGroup(PathAttributes attribs) {
        try {
            return attribs.getGroup();
        } catch (AttributeNotSupportedException e) {
            return null;
        }
    }

    private String buildOwner(PathAttributes attribs) {
        try {
            return attribs.getOwner();
        } catch (AttributeNotSupportedException e) {
            return null;
        }
    }

    private Set<PosixFilePermission> buildPermissions(PathAttributes attribs) {
        Set<PosixFilePermission> newPermissions = new HashSet<>();
        try {
            if (attribs.getPermissions() != null) {
                newPermissions = attribs.getPermissions();
            }
        } catch (AttributeNotSupportedException e) {
            if (attribs.isExecutable()) {
                newPermissions.add(PosixFilePermission.OTHERS_EXECUTE);
            }
            if (attribs.isReadable()) {
                newPermissions.add(PosixFilePermission.OTHERS_READ);
            }
            if (attribs.isWritable()) {
                newPermissions.add(PosixFilePermission.OTHERS_WRITE);
            }
        }
        return newPermissions;
    }

    public String toString() {
        return String.join("\t", getFileType(), permissionsAsString(), String.valueOf(isHidden), owner == null ? "" : owner, group == null ? "" : group, String.valueOf(size), lastModifiedAt == null ? "" : lastModifiedAt, relativePath);
    }

    private String permissionsAsString() {
        return new PosixFilePermissionFormatter(permissions).toString();
    }

    private String getFileType() {
        if (isRegular) {
            return "-";
        } else if (isDirectory) {
            return "d";
        } else if (isSymbolicLink) {
            return "l";
        }
        // On unix other types are named pipe, socket, device file or door
        // See https://en.wikipedia.org/wiki/Unix_file_types
        // As Xenon does no do those types return o for other.
        return "o";
    }
}
