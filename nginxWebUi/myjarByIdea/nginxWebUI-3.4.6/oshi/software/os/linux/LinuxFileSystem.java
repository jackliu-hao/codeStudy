package oshi.software.os.linux;

import com.sun.jna.Native;
import com.sun.jna.platform.linux.LibC;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractFileSystem;
import oshi.software.os.OSFileStore;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public class LinuxFileSystem extends AbstractFileSystem {
   private static final Logger LOG = LoggerFactory.getLogger(LinuxFileSystem.class);
   private static final String UNICODE_SPACE = "\\040";
   private static final List<String> TMP_FS_PATHS;

   public List<OSFileStore> getFileStores(boolean localOnly) {
      Map<String, String> uuidMap = new HashMap();
      File uuidDir = new File("/dev/disk/by-uuid");
      if (uuidDir.listFiles() != null) {
         File[] var4 = uuidDir.listFiles();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            File uuid = var4[var6];

            try {
               uuidMap.put(uuid.getCanonicalPath(), uuid.getName().toLowerCase());
            } catch (IOException var9) {
               LOG.error((String)"Couldn't get canonical path for {}. {}", (Object)uuid.getName(), (Object)var9.getMessage());
            }
         }
      }

      return getFileStoreMatching((String)null, uuidMap, localOnly);
   }

   static List<OSFileStore> getFileStoreMatching(String nameToMatch, Map<String, String> uuidMap) {
      return getFileStoreMatching(nameToMatch, uuidMap, false);
   }

   private static List<OSFileStore> getFileStoreMatching(String nameToMatch, Map<String, String> uuidMap, boolean localOnly) {
      List<OSFileStore> fsList = new ArrayList();
      List<String> mounts = FileUtil.readFile(ProcPath.MOUNTS);
      Iterator var5 = mounts.iterator();

      while(true) {
         String[] split;
         String path;
         String type;
         String options;
         String name;
         do {
            do {
               do {
                  do {
                     do {
                        do {
                           do {
                              if (!var5.hasNext()) {
                                 return fsList;
                              }

                              String mount = (String)var5.next();
                              split = mount.split(" ");
                           } while(split.length < 6);

                           path = split[1].replace("\\040", " ");
                           type = split[2];
                        } while(localOnly && NETWORK_FS_TYPES.contains(type));
                     } while(PSEUDO_FS_TYPES.contains(type));
                  } while(path.equals("/dev"));
               } while(ParseUtil.filePathStartsWith(TMP_FS_PATHS, path));
            } while(path.endsWith("/shm"));

            options = split[3];
            name = split[0].replace("\\040", " ");
            if (path.equals("/")) {
               name = "/";
            }
         } while(nameToMatch != null && !nameToMatch.equals(name));

         String volume = split[0].replace("\\040", " ");
         String uuid = uuidMap != null ? (String)uuidMap.getOrDefault(split[0], "") : "";
         String description;
         if (volume.startsWith("/dev")) {
            description = "Local Disk";
         } else if (volume.equals("tmpfs")) {
            description = "Ram Disk";
         } else if (NETWORK_FS_TYPES.contains(type)) {
            description = "Network Disk";
         } else {
            description = "Mount Point";
         }

         String logicalVolume = "";
         String volumeMapperDirectory = "/dev/mapper/";
         Path link = Paths.get(volume);
         if (link.toFile().exists() && Files.isSymbolicLink(link)) {
            try {
               Path slink = Files.readSymbolicLink(link);
               Path full = Paths.get(volumeMapperDirectory + slink.toString());
               if (full.toFile().exists()) {
                  logicalVolume = full.normalize().toString();
               }
            } catch (IOException var31) {
               LOG.warn((String)"Couldn't access symbolic path  {}. {}", (Object)link, (Object)var31.getMessage());
            }
         }

         long totalInodes = 0L;
         long freeInodes = 0L;
         long totalSpace = 0L;
         long usableSpace = 0L;
         long freeSpace = 0L;

         try {
            LibC.Statvfs vfsStat = new LibC.Statvfs();
            if (0 == LibC.INSTANCE.statvfs(path, vfsStat)) {
               totalInodes = vfsStat.f_files.longValue();
               freeInodes = vfsStat.f_ffree.longValue();
               totalSpace = vfsStat.f_blocks.longValue() * vfsStat.f_frsize.longValue();
               usableSpace = vfsStat.f_bavail.longValue() * vfsStat.f_frsize.longValue();
               freeSpace = vfsStat.f_bfree.longValue() * vfsStat.f_frsize.longValue();
            } else {
               File tmpFile = new File(path);
               totalSpace = tmpFile.getTotalSpace();
               usableSpace = tmpFile.getUsableSpace();
               freeSpace = tmpFile.getFreeSpace();
               LOG.warn((String)"Failed to get information to use statvfs. path: {}, Error code: {}", (Object)path, (Object)Native.getLastError());
            }
         } catch (NoClassDefFoundError | UnsatisfiedLinkError var30) {
            LOG.error((String)"Failed to get file counts from statvfs. {}", (Object)var30.getMessage());
         }

         fsList.add(new LinuxOSFileStore(name, volume, name, path, options, uuid, logicalVolume, description, type, freeSpace, usableSpace, totalSpace, freeInodes, totalInodes));
      }
   }

   public long getOpenFileDescriptors() {
      return getFileDescriptors(0);
   }

   public long getMaxFileDescriptors() {
      return getFileDescriptors(2);
   }

   private static long getFileDescriptors(int index) {
      String filename = ProcPath.SYS_FS_FILE_NR;
      if (index >= 0 && index <= 2) {
         List<String> osDescriptors = FileUtil.readFile(filename);
         if (!osDescriptors.isEmpty()) {
            String[] splittedLine = ((String)osDescriptors.get(0)).split("\\D+");
            return ParseUtil.parseLongOrDefault(splittedLine[index], 0L);
         } else {
            return 0L;
         }
      } else {
         throw new IllegalArgumentException("Index must be between 0 and 2.");
      }
   }

   static {
      TMP_FS_PATHS = Arrays.asList("/run", "/sys", "/proc", ProcPath.PROC);
   }
}
