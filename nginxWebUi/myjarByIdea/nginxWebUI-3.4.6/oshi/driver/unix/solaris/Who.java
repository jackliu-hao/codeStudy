package oshi.driver.unix.solaris;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.solaris.SolarisLibc;
import oshi.software.os.OSSession;

@ThreadSafe
public final class Who {
   private static final SolarisLibc LIBC;

   private Who() {
   }

   public static synchronized List<OSSession> queryUtxent() {
      List<OSSession> whoList = new ArrayList();
      LIBC.setutxent();

      SolarisLibc.SolarisUtmpx ut;
      try {
         while((ut = LIBC.getutxent()) != null) {
            if (ut.ut_type == 7 || ut.ut_type == 6) {
               String user = (new String(ut.ut_user, StandardCharsets.US_ASCII)).trim();
               if (!"LOGIN".equals(user)) {
                  String device = (new String(ut.ut_line, StandardCharsets.US_ASCII)).trim();
                  String host = (new String(ut.ut_host, StandardCharsets.US_ASCII)).trim();
                  long loginTime = ut.ut_tv.tv_sec.longValue() * 1000L + ut.ut_tv.tv_usec.longValue() / 1000L;
                  if (user.isEmpty() || device.isEmpty() || loginTime < 0L || loginTime > System.currentTimeMillis()) {
                     List var7 = oshi.driver.unix.Who.queryWho();
                     return var7;
                  }

                  whoList.add(new OSSession(user, device, loginTime, host));
               }
            }
         }
      } finally {
         LIBC.endutxent();
      }

      return whoList;
   }

   static {
      LIBC = SolarisLibc.INSTANCE;
   }
}
