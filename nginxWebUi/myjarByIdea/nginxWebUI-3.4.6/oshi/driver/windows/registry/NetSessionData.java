package oshi.driver.windows.registry;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Netapi32;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.OSSession;

@ThreadSafe
public final class NetSessionData {
   private static final Netapi32 NET;

   private NetSessionData() {
   }

   public static List<OSSession> queryUserSessions() {
      List<OSSession> sessions = new ArrayList();
      PointerByReference bufptr = new PointerByReference();
      IntByReference entriesread = new IntByReference();
      IntByReference totalentries = new IntByReference();
      if (0 == NET.NetSessionEnum((WString)null, (WString)null, (WString)null, 10, bufptr, -1, entriesread, totalentries, (IntByReference)null)) {
         Pointer buf = bufptr.getValue();
         Netapi32.SESSION_INFO_10 si10 = new Netapi32.SESSION_INFO_10(buf);
         if (entriesread.getValue() > 0) {
            Netapi32.SESSION_INFO_10[] sessionInfo = (Netapi32.SESSION_INFO_10[])si10.toArray(entriesread.getValue());
            Netapi32.SESSION_INFO_10[] var7 = sessionInfo;
            int var8 = sessionInfo.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Netapi32.SESSION_INFO_10 si = var7[var9];
               long logonTime = System.currentTimeMillis() - 1000L * (long)si.sesi10_time;
               sessions.add(new OSSession(si.sesi10_username, "Network session", logonTime, si.sesi10_cname));
            }
         }

         NET.NetApiBufferFree(buf);
      }

      return sessions;
   }

   static {
      NET = Netapi32.INSTANCE;
   }
}
