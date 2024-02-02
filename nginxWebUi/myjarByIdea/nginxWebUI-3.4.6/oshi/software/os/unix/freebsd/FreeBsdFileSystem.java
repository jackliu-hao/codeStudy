package oshi.software.os.unix.freebsd;

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
import oshi.software.os.linux.LinuxOSFileStore;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
public final class FreeBsdFileSystem extends AbstractFileSystem {
   private static final List<String> TMP_FS_PATHS = Arrays.asList("/system", "/tmp", "/dev/fd");

   public List<OSFileStore> getFileStores(boolean localOnly) {
      Map<String, String> uuidMap = new HashMap();
      String device = "";
      Iterator var4 = ExecutingCommand.runNative("geom part list").iterator();

      while(var4.hasNext()) {
         String line = (String)var4.next();
         if (line.contains("Name: ")) {
            device = line.substring(line.lastIndexOf(32) + 1);
         }

         if (!device.isEmpty()) {
            line = line.trim();
            if (line.startsWith("rawuuid:")) {
               uuidMap.put(device, line.substring(line.lastIndexOf(32) + 1));
               device = "";
            }
         }
      }

      List<OSFileStore> fsList = new ArrayList();
      Map<String, Long> inodeFreeMap = new HashMap();
      Map<String, Long> inodeTotalMap = new HashMap();
      Iterator var7 = ExecutingCommand.runNative("df -i").iterator();

      String fs;
      String[] split;
      while(var7.hasNext()) {
         fs = (String)var7.next();
         if (fs.startsWith("/")) {
            split = ParseUtil.whitespaces.split(fs);
            if (split.length > 7) {
               inodeFreeMap.put(split[0], ParseUtil.parseLongOrDefault(split[6], 0L));
               inodeTotalMap.put(split[0], (Long)inodeFreeMap.get(split[0]) + ParseUtil.parseLongOrDefault(split[5], 0L));
            }
         }
      }

      var7 = ExecutingCommand.runNative("mount -p").iterator();

      while(true) {
         String volume;
         String path;
         String type;
         String options;
         do {
            do {
               do {
                  do {
                     do {
                        do {
                           if (!var7.hasNext()) {
                              return fsList;
                           }

                           fs = (String)var7.next();
                           split = ParseUtil.whitespaces.split(fs);
                        } while(split.length < 5);

                        volume = split[0];
                        path = split[1];
                        type = split[2];
                        options = split[3];
                     } while(localOnly && NETWORK_FS_TYPES.contains(type));
                  } while(PSEUDO_FS_TYPES.contains(type));
               } while(path.equals("/dev"));
            } while(ParseUtil.filePathStartsWith(TMP_FS_PATHS, path));
         } while(volume.startsWith("rpool") && !path.equals("/"));

         String name = path.substring(path.lastIndexOf(47) + 1);
         if (name.isEmpty()) {
            name = volume.substring(volume.lastIndexOf(47) + 1);
         }

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

         String uuid = (String)uuidMap.getOrDefault(name, "");
         fsList.add(new LinuxOSFileStore(name, volume, name, path, options, uuid, "", description, type, freeSpace, usableSpace, totalSpace, inodeFreeMap.containsKey(path) ? (Long)inodeFreeMap.get(path) : 0L, inodeTotalMap.containsKey(path) ? (Long)inodeTotalMap.get(path) : 0L));
      }
   }

   public long getOpenFileDescriptors() {
      return (long)BsdSysctlUtil.sysctl("kern.openfiles", 0);
   }

   public long getMaxFileDescriptors() {
      return (long)BsdSysctlUtil.sysctl("kern.maxfiles", 0);
   }
}
