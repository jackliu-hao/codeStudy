package oshi.driver.mac.disk;

import com.sun.jna.platform.mac.SystemB;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Fsstat {
   private Fsstat() {
   }

   public static Map<String, String> queryPartitionToMountMap() {
      Map<String, String> mountPointMap = new HashMap();
      int numfs = SystemB.INSTANCE.getfsstat64((SystemB.Statfs[])null, 0, 0);
      SystemB.Statfs[] fs = new SystemB.Statfs[numfs];
      SystemB.INSTANCE.getfsstat64(fs, numfs * (new SystemB.Statfs()).size(), 16);
      SystemB.Statfs[] var3 = fs;
      int var4 = fs.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         SystemB.Statfs f = var3[var5];
         String mntFrom = (new String(f.f_mntfromname, StandardCharsets.UTF_8)).trim();
         mountPointMap.put(mntFrom.replace("/dev/", ""), (new String(f.f_mntonname, StandardCharsets.UTF_8)).trim());
      }

      return mountPointMap;
   }
}
