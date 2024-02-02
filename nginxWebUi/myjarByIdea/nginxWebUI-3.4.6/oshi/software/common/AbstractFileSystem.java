package oshi.software.common;

import java.util.Arrays;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

@ThreadSafe
public abstract class AbstractFileSystem implements FileSystem {
   protected static final List<String> NETWORK_FS_TYPES = Arrays.asList("afs", "cifs", "smbfs", "sshfs", "ncpfs", "ncp", "nfs", "nfs4", "gfs", "gds2", "glusterfs");
   protected static final List<String> PSEUDO_FS_TYPES = Arrays.asList("anon_inodefs", "autofs", "bdev", "binfmt_misc", "bpf", "cgroup", "cgroup2", "configfs", "cpuset", "dax", "debugfs", "devpts", "devtmpfs", "drm", "ecryptfs", "efivarfs", "fuse", "fusectl", "hugetlbfs", "inotifyfs", "mqueue", "nfsd", "overlay", "proc", "pstore", "rootfs", "rpc_pipefs", "securityfs", "selinuxfs", "sunrpc", "sysfs", "systemd-1", "tracefs", "usbfs", "procfs", "devfs", "ctfs", "fdescfs", "objfs", "mntfs", "sharefs", "lofs");

   public List<OSFileStore> getFileStores() {
      return this.getFileStores(false);
   }
}
