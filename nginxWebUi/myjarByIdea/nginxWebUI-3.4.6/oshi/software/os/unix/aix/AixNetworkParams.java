package oshi.software.os.unix.aix;

import com.sun.jna.Native;
import java.util.Iterator;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.aix.AixLibc;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
final class AixNetworkParams extends AbstractNetworkParams {
   private static final AixLibc LIBC;

   public String getHostName() {
      byte[] hostnameBuffer = new byte[256];
      return 0 != LIBC.gethostname(hostnameBuffer, hostnameBuffer.length) ? super.getHostName() : Native.toString(hostnameBuffer);
   }

   public String getIpv4DefaultGateway() {
      return getDefaultGateway("netstat -rnf inet");
   }

   public String getIpv6DefaultGateway() {
      return getDefaultGateway("netstat -rnf inet6");
   }

   private static String getDefaultGateway(String netstat) {
      Iterator var1 = ExecutingCommand.runNative(netstat).iterator();

      String[] split;
      do {
         if (!var1.hasNext()) {
            return "unknown";
         }

         String line = (String)var1.next();
         split = ParseUtil.whitespaces.split(line);
      } while(split.length <= 7 || !"default".equals(split[0]));

      return split[1];
   }

   static {
      LIBC = AixLibc.INSTANCE;
   }
}
