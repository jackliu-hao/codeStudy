package oshi.software.os.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat;
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
import oshi.util.platform.unix.solaris.KstatUtil;

@ThreadSafe
public class SolarisFileSystem extends AbstractFileSystem {
   private static final List<String> TMP_FS_PATHS = Arrays.asList("/system", "/tmp", "/dev/fd");

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
      String key = null;
      String total = null;
      String free = null;
      String command = "df -g" + (localOnly ? " -l" : "");
      Iterator var9 = ExecutingCommand.runNative(command).iterator();

      while(true) {
         String line;
         while(var9.hasNext()) {
            line = (String)var9.next();
            if (line.startsWith("/")) {
               key = ParseUtil.whitespaces.split(line)[0];
               total = null;
            } else if (line.contains("available") && line.contains("total files")) {
               total = ParseUtil.getTextBetweenStrings(line, "available", "total files").trim();
            } else if (line.contains("free files")) {
               free = ParseUtil.getTextBetweenStrings(line, "", "free files").trim();
               if (key != null && total != null) {
                  inodeFreeMap.put(key, ParseUtil.parseLongOrDefault(free, 0L));
                  inodeTotalMap.put(key, ParseUtil.parseLongOrDefault(total, 0L));
                  key = null;
               }
            }
         }

         var9 = ExecutingCommand.runNative("cat /etc/mnttab").iterator();

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
                              String[] split;
                              do {
                                 if (!var9.hasNext()) {
                                    return fsList;
                                 }

                                 line = (String)var9.next();
                                 split = ParseUtil.whitespaces.split(line);
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

            fsList.add(new SolarisOSFileStore(name, volume, name, path, options, "", "", description, type, freeSpace, usableSpace, totalSpace, inodeFreeMap.containsKey(path) ? (Long)inodeFreeMap.get(path) : 0L, inodeTotalMap.containsKey(path) ? (Long)inodeTotalMap.get(path) : 0L));
         }
      }
   }

   public long getOpenFileDescriptors() {
      KstatUtil.KstatChain kc = KstatUtil.openChain();

      long var3;
      label42: {
         try {
            LibKstat.Kstat ksp = kc.lookup((String)null, -1, "file_cache");
            if (ksp != null && kc.read(ksp)) {
               var3 = KstatUtil.dataLookupLong(ksp, "buf_inuse");
               break label42;
            }
         } catch (Throwable var6) {
            if (kc != null) {
               try {
                  kc.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (kc != null) {
            kc.close();
         }

         return 0L;
      }

      if (kc != null) {
         kc.close();
      }

      return var3;
   }

   public long getMaxFileDescriptors() {
      KstatUtil.KstatChain kc = KstatUtil.openChain();

      long var3;
      label42: {
         try {
            LibKstat.Kstat ksp = kc.lookup((String)null, -1, "file_cache");
            if (ksp != null && kc.read(ksp)) {
               var3 = KstatUtil.dataLookupLong(ksp, "buf_max");
               break label42;
            }
         } catch (Throwable var6) {
            if (kc != null) {
               try {
                  kc.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (kc != null) {
            kc.close();
         }

         return 0L;
      }

      if (kc != null) {
         kc.close();
      }

      return var3;
   }
}
