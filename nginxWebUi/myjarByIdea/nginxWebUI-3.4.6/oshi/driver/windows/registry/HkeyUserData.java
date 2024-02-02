package oshi.driver.windows.registry;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.OSSession;

@ThreadSafe
public final class HkeyUserData {
   private static final String DEFAULT_DEVICE = "Console";
   private static final String VOLATILE_ENV_SUBKEY = "Volatile Environment";
   private static final String CLIENTNAME = "CLIENTNAME";
   private static final String SESSIONNAME = "SESSIONNAME";
   private static final Logger LOG = LoggerFactory.getLogger(HkeyUserData.class);

   private HkeyUserData() {
   }

   public static List<OSSession> queryUserSessions() {
      List<OSSession> sessions = new ArrayList();
      String[] var1 = Advapi32Util.registryGetKeys(WinReg.HKEY_USERS);
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String sidKey = var1[var3];
         if (!sidKey.startsWith(".") && !sidKey.endsWith("_Classes")) {
            try {
               Advapi32Util.Account a = Advapi32Util.getAccountBySid(sidKey);
               String name = a.name;
               String device = "Console";
               String host = a.domain;
               long loginTime = 0L;
               String keyPath = sidKey + "\\" + "Volatile Environment";
               if (Advapi32Util.registryKeyExists(WinReg.HKEY_USERS, keyPath)) {
                  WinReg.HKEY hKey = Advapi32Util.registryGetKey(WinReg.HKEY_USERS, keyPath, 131097).getValue();
                  Advapi32Util.InfoKey info = Advapi32Util.registryQueryInfoKey(hKey, 0);
                  loginTime = info.lpftLastWriteTime.toTime();
                  String[] var14 = Advapi32Util.registryGetKeys(hKey);
                  int var15 = var14.length;

                  for(int var16 = 0; var16 < var15; ++var16) {
                     String subKey = var14[var16];
                     String subKeyPath = keyPath + "\\" + subKey;
                     String client;
                     if (Advapi32Util.registryValueExists(WinReg.HKEY_USERS, subKeyPath, "SESSIONNAME")) {
                        client = Advapi32Util.registryGetStringValue(WinReg.HKEY_USERS, subKeyPath, "SESSIONNAME");
                        if (!client.isEmpty()) {
                           device = client;
                        }
                     }

                     if (Advapi32Util.registryValueExists(WinReg.HKEY_USERS, subKeyPath, "CLIENTNAME")) {
                        client = Advapi32Util.registryGetStringValue(WinReg.HKEY_USERS, subKeyPath, "CLIENTNAME");
                        if (!client.isEmpty() && !"Console".equals(client)) {
                           host = client;
                        }
                     }
                  }

                  Advapi32Util.registryCloseKey(hKey);
               }

               sessions.add(new OSSession(name, device, loginTime, host));
            } catch (Win32Exception var20) {
               LOG.warn((String)"Error querying SID {} from registry: {}", (Object)sidKey, (Object)var20.getMessage());
            }
         }
      }

      return sessions;
   }
}
