package oshi.software.os.unix.aix;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractFileSystem;
import oshi.software.os.OSFileStore;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public class AixFileSystem extends AbstractFileSystem {
   private static final List<String> TMP_FS_PATHS = Arrays.asList("/proc");

   public List<OSFileStore> getFileStores(boolean localOnly) {
      return getFileStoreMatching((String)null, localOnly);
   }

   static List<OSFileStore> getFileStoreMatching(String nameToMatch) {
      return getFileStoreMatching(nameToMatch, false);
   }

   private static List<OSFileStore> getFileStoreMatching(String nameToMatch, boolean localOnly) {
      List<OSFileStore> fsList = new ArrayList();
      Map<String, Long> inodeFreeMap = new HashMap();
      Map<String, Long> inodeTotalMap = new HashMap();
      String command = "df -i" + (localOnly ? " -l" : "");
      Iterator var6 = ExecutingCommand.runNative(command).iterator();

      String fs;
      String[] split;
      while(var6.hasNext()) {
         fs = (String)var6.next();
         if (fs.startsWith("/")) {
            split = ParseUtil.whitespaces.split(fs);
            if (split.length > 5) {
               inodeTotalMap.put(split[0], ParseUtil.parseLongOrDefault(split[1], 0L));
               inodeFreeMap.put(split[0], ParseUtil.parseLongOrDefault(split[3], 0L));
            }
         }
      }

      var6 = ExecutingCommand.runNative("mount").iterator();

      while(true) {
         String volume;
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
                              if (!var6.hasNext()) {
                                 return fsList;
                              }

                              fs = (String)var6.next();
                              split = ParseUtil.whitespaces.split("x" + fs);
                           } while(split.length <= 7);

                           volume = split[1];
                           path = split[2];
                           type = split[3];
                           options = split[4];
                        } while(localOnly && NETWORK_FS_TYPES.contains(type));
                     } while(PSEUDO_FS_TYPES.contains(type));
                  } while(path.equals("/dev"));
               } while(!path.startsWith("/"));
            } while(ParseUtil.filePathStartsWith(TMP_FS_PATHS, path) && !path.equals("/"));

            name = path.substring(path.lastIndexOf(47) + 1);
            if (name.isEmpty()) {
               name = volume.substring(volume.lastIndexOf(47) + 1);
            }
         } while(nameToMatch != null && !nameToMatch.equals(name));

         File f = new File(path);
         long totalSpace = f.getTotalSpace();
         long usableSpace = f.getUsableSpace();
         long freeSpace = f.getFreeSpace();
         String description;
         if (!volume.startsWith("/dev") && !path.equals("/")) {
            if (volume.equals("tmpfs")) {
               description = "Ram Disk";
            } else if (NETWORK_FS_TYPES.contains(type)) {
               description = "Network Disk";
            } else {
               description = "Mount Point";
            }
         } else {
            description = "Local Disk";
         }

         fsList.add(new AixOSFileStore(name, volume, name, path, options, "", "", description, type, freeSpace, usableSpace, totalSpace, (Long)inodeFreeMap.getOrDefault(volume, 0L), (Long)inodeTotalMap.getOrDefault(volume, 0L)));
      }
   }

   public long getOpenFileDescriptors() {
      boolean header = false;
      long openfiles = 0L;
      Iterator var4 = ExecutingCommand.runNative("lsof -nl").iterator();

      while(var4.hasNext()) {
         String f = (String)var4.next();
         if (!header) {
            header = f.startsWith("COMMAND");
         } else {
            ++openfiles;
         }
      }

      return openfiles;
   }

   public long getMaxFileDescriptors() {
      return ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("ulimit -n"), 0L);
   }
}
