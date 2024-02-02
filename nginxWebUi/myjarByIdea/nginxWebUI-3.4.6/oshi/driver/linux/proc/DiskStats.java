package oshi.driver.linux.proc;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public final class DiskStats {
   private DiskStats() {
   }

   public static Map<String, Map<IoStat, Long>> getDiskStats() {
      Map<String, Map<IoStat, Long>> diskStatMap = new HashMap();
      IoStat[] enumArray = (IoStat[])IoStat.class.getEnumConstants();
      List<String> diskStats = FileUtil.readFile(ProcPath.DISKSTATS);
      Iterator var3 = diskStats.iterator();

      while(var3.hasNext()) {
         String stat = (String)var3.next();
         String[] split = ParseUtil.whitespaces.split(stat.trim());
         Map<IoStat, Long> statMap = new EnumMap(IoStat.class);
         String name = null;

         for(int i = 0; i < enumArray.length && i < split.length; ++i) {
            if (enumArray[i] == DiskStats.IoStat.NAME) {
               name = split[i];
            } else {
               statMap.put(enumArray[i], ParseUtil.parseLongOrDefault(split[i], 0L));
            }
         }

         if (name != null) {
            diskStatMap.put(name, statMap);
         }
      }

      return diskStatMap;
   }

   public static enum IoStat {
      MAJOR,
      MINOR,
      NAME,
      READS,
      READS_MERGED,
      READS_SECTOR,
      READS_MS,
      WRITES,
      WRITES_MERGED,
      WRITES_SECTOR,
      WRITES_MS,
      IO_QUEUE_LENGTH,
      IO_MS,
      IO_MS_WEIGHTED,
      DISCARDS,
      DISCARDS_MERGED,
      DISCARDS_SECTOR,
      DISCARDS_MS,
      FLUSHES,
      FLUSHES_MS;
   }
}
