package oshi.software.os.mac;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.DiskArbitration;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.platform.mac.SystemB;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractFileSystem;
import oshi.software.os.OSFileStore;
import oshi.util.platform.mac.SysctlUtil;

@ThreadSafe
public class MacFileSystem extends AbstractFileSystem {
   private static final Logger LOG = LoggerFactory.getLogger(MacFileSystem.class);
   private static final Pattern LOCAL_DISK = Pattern.compile("/dev/disk\\d");
   private static final int MNT_RDONLY = 1;
   private static final int MNT_SYNCHRONOUS = 2;
   private static final int MNT_NOEXEC = 4;
   private static final int MNT_NOSUID = 8;
   private static final int MNT_NODEV = 16;
   private static final int MNT_UNION = 32;
   private static final int MNT_ASYNC = 64;
   private static final int MNT_CPROTECT = 128;
   private static final int MNT_EXPORTED = 256;
   private static final int MNT_QUARANTINE = 1024;
   private static final int MNT_LOCAL = 4096;
   private static final int MNT_QUOTA = 8192;
   private static final int MNT_ROOTFS = 16384;
   private static final int MNT_DOVOLFS = 32768;
   private static final int MNT_DONTBROWSE = 1048576;
   private static final int MNT_IGNORE_OWNERSHIP = 2097152;
   private static final int MNT_AUTOMOUNTED = 4194304;
   private static final int MNT_JOURNALED = 8388608;
   private static final int MNT_NOUSERXATTR = 16777216;
   private static final int MNT_DEFWRITE = 33554432;
   private static final int MNT_MULTILABEL = 67108864;
   private static final int MNT_NOATIME = 268435456;
   private static final Map<Integer, String> OPTIONS_MAP = new HashMap();

   public List<OSFileStore> getFileStores(boolean localOnly) {
      return getFileStoreMatching((String)null, localOnly);
   }

   static List<OSFileStore> getFileStoreMatching(String nameToMatch) {
      return getFileStoreMatching(nameToMatch, false);
   }

   private static List<OSFileStore> getFileStoreMatching(String nameToMatch, boolean localOnly) {
      List<OSFileStore> fsList = new ArrayList();
      int numfs = SystemB.INSTANCE.getfsstat64((SystemB.Statfs[])null, 0, 0);
      if (numfs > 0) {
         DiskArbitration.DASessionRef session = DiskArbitration.INSTANCE.DASessionCreate(CoreFoundation.INSTANCE.CFAllocatorGetDefault());
         if (session == null) {
            LOG.error("Unable to open session to DiskArbitration framework.");
         } else {
            CoreFoundation.CFStringRef daVolumeNameKey = CoreFoundation.CFStringRef.createCFString("DAVolumeName");
            SystemB.Statfs[] fs = new SystemB.Statfs[numfs];
            numfs = SystemB.INSTANCE.getfsstat64(fs, numfs * (new SystemB.Statfs()).size(), 16);

            for(int f = 0; f < numfs; ++f) {
               String volume = (new String(fs[f].f_mntfromname, StandardCharsets.UTF_8)).trim();
               int flags = fs[f].f_flags;
               if ((!localOnly || (flags & 4096) != 0) && !volume.equals("devfs") && !volume.startsWith("map ")) {
                  String type = (new String(fs[f].f_fstypename, StandardCharsets.UTF_8)).trim();
                  String description = "Volume";
                  if (LOCAL_DISK.matcher(volume).matches()) {
                     description = "Local Disk";
                  } else if (volume.startsWith("localhost:") || volume.startsWith("//") || volume.startsWith("smb://") || NETWORK_FS_TYPES.contains(type)) {
                     description = "Network Drive";
                  }

                  String path = (new String(fs[f].f_mntonname, StandardCharsets.UTF_8)).trim();
                  String name = "";
                  File file = new File(path);
                  if (name.isEmpty()) {
                     name = file.getName();
                     if (name.isEmpty()) {
                        name = file.getPath();
                     }
                  }

                  if (nameToMatch == null || nameToMatch.equals(name)) {
                     StringBuilder options = new StringBuilder((1 & flags) == 0 ? "rw" : "ro");
                     String moreOptions = (String)OPTIONS_MAP.entrySet().stream().filter((e) -> {
                        return ((Integer)e.getKey() & flags) > 0;
                     }).map(Map.Entry::getValue).collect(Collectors.joining(","));
                     if (!moreOptions.isEmpty()) {
                        options.append(',').append(moreOptions);
                     }

                     String uuid = "";
                     String bsdName = volume.replace("/dev/disk", "disk");
                     if (bsdName.startsWith("disk")) {
                        DiskArbitration.DADiskRef disk = DiskArbitration.INSTANCE.DADiskCreateFromBSDName(CoreFoundation.INSTANCE.CFAllocatorGetDefault(), session, volume);
                        if (disk != null) {
                           CoreFoundation.CFDictionaryRef diskInfo = DiskArbitration.INSTANCE.DADiskCopyDescription(disk);
                           if (diskInfo != null) {
                              Pointer result = diskInfo.getValue(daVolumeNameKey);
                              CoreFoundation.CFStringRef volumePtr = new CoreFoundation.CFStringRef(result);
                              name = volumePtr.stringValue();
                              if (name == null) {
                                 name = "unknown";
                              }

                              diskInfo.release();
                           }

                           disk.release();
                        }

                        CoreFoundation.CFMutableDictionaryRef matchingDict = IOKitUtil.getBSDNameMatchingDict(bsdName);
                        if (matchingDict != null) {
                           IOKit.IOIterator fsIter = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)matchingDict);
                           if (fsIter != null) {
                              IOKit.IORegistryEntry fsEntry = fsIter.next();
                              if (fsEntry != null && fsEntry.conformsTo("IOMedia")) {
                                 uuid = fsEntry.getStringProperty("UUID");
                                 if (uuid != null) {
                                    uuid = uuid.toLowerCase();
                                 }

                                 fsEntry.release();
                              }

                              fsIter.release();
                           }
                        }
                     }

                     fsList.add(new MacOSFileStore(name, volume, name, path, options.toString(), uuid == null ? "" : uuid, "", description, type, file.getFreeSpace(), file.getUsableSpace(), file.getTotalSpace(), fs[f].f_ffree, fs[f].f_files));
                  }
               }
            }

            daVolumeNameKey.release();
            session.release();
         }
      }

      return fsList;
   }

   public long getOpenFileDescriptors() {
      return (long)SysctlUtil.sysctl("kern.num_files", 0);
   }

   public long getMaxFileDescriptors() {
      return (long)SysctlUtil.sysctl("kern.maxfiles", 0);
   }

   static {
      OPTIONS_MAP.put(2, "synchronous");
      OPTIONS_MAP.put(4, "noexec");
      OPTIONS_MAP.put(8, "nosuid");
      OPTIONS_MAP.put(16, "nodev");
      OPTIONS_MAP.put(32, "union");
      OPTIONS_MAP.put(64, "asynchronous");
      OPTIONS_MAP.put(128, "content-protection");
      OPTIONS_MAP.put(256, "exported");
      OPTIONS_MAP.put(1024, "quarantined");
      OPTIONS_MAP.put(4096, "local");
      OPTIONS_MAP.put(8192, "quotas");
      OPTIONS_MAP.put(16384, "rootfs");
      OPTIONS_MAP.put(32768, "volfs");
      OPTIONS_MAP.put(1048576, "nobrowse");
      OPTIONS_MAP.put(2097152, "noowners");
      OPTIONS_MAP.put(4194304, "automounted");
      OPTIONS_MAP.put(8388608, "journaled");
      OPTIONS_MAP.put(16777216, "nouserxattr");
      OPTIONS_MAP.put(33554432, "defwrite");
      OPTIONS_MAP.put(67108864, "multilabel");
      OPTIONS_MAP.put(268435456, "noatime");
   }
}
