package nl.esciencecenter.xenon.cli.listfiles;

import nl.esciencecenter.xenon.filesystems.PosixFilePermission;

import java.util.Set;

/**
 * Construct file permissions string similar to `ls -l`.
 * For example `rwxr-xr-x`.
 * File type is not included due to Xenon not supporting all Posix file types.
 */
public class PosixFilePermissionFormatter {
    private final Set<PosixFilePermission> permissions;

    PosixFilePermissionFormatter(Set<PosixFilePermission> permissions) {
        this.permissions = permissions;
    }

    private String filePermission(PosixFilePermission perm, String onBit) {
        if (permissions.contains(perm)) {
            return onBit;
        } else {
            return "-";
        }
    }

    @Override
    public String toString() {
        return filePermission(PosixFilePermission.OWNER_READ, "r") +
                filePermission(PosixFilePermission.OWNER_WRITE, "w") +
                filePermission(PosixFilePermission.OWNER_EXECUTE, "x") +
                filePermission(PosixFilePermission.GROUP_READ, "r") +
                filePermission(PosixFilePermission.GROUP_WRITE, "w") +
                filePermission(PosixFilePermission.GROUP_EXECUTE, "x") +
                filePermission(PosixFilePermission.OTHERS_READ, "r") +
                filePermission(PosixFilePermission.OWNER_WRITE, "w") +
                filePermission(PosixFilePermission.OTHERS_EXECUTE, "x");
    }
}
