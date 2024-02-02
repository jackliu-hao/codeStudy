package oshi.driver.mac;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.mac.SystemB;
import oshi.software.os.OSSession;

@ThreadSafe
public final class Who {
   private static final SystemB SYS;

   private Who() {
   }

   public static synchronized List<OSSession> queryUtxent() {
      List<OSSession> whoList = new ArrayList();
      SYS.setutxent();

      try {
         SystemB.MacUtmpx ut;
         while((ut = SYS.getutxent()) != null) {
            if (ut.ut_type == 7 || ut.ut_type == 6) {
               String user = (new String(ut.ut_user, StandardCharsets.US_ASCII)).trim();
               String device = (new String(ut.ut_line, StandardCharsets.US_ASCII)).trim();
               String host = (new String(ut.ut_host, StandardCharsets.US_ASCII)).trim();
               long loginTime = ut.ut_tv.tv_sec.longValue() * 1000L + (long)ut.ut_tv.tv_usec / 1000L;
               if (user.isEmpty() || device.isEmpty() || loginTime < 0L || loginTime > System.currentTimeMillis()) {
                  List var7 = oshi.driver.unix.Who.queryWho();
                  return var7;
               }

               whoList.add(new OSSession(user, device, loginTime, host));
            }
         }

         return whoList;
      } finally {
         SYS.endutxent();
      }
   }

   static {
      SYS = SystemB.INSTANCE;
   }
}
