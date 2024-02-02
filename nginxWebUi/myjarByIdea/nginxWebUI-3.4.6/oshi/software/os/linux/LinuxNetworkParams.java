package oshi.software.os.linux;

import com.sun.jna.Native;
import com.sun.jna.platform.linux.LibC;
import com.sun.jna.ptr.PointerByReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.linux.LinuxLibc;
import oshi.jna.platform.unix.CLibrary;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
final class LinuxNetworkParams extends AbstractNetworkParams {
   private static final Logger LOG = LoggerFactory.getLogger(LinuxNetworkParams.class);
   private static final LinuxLibc LIBC;
   private static final String IPV4_DEFAULT_DEST = "0.0.0.0";
   private static final String IPV6_DEFAULT_DEST = "::/0";

   public String getDomainName() {
      CLibrary.Addrinfo hint = new CLibrary.Addrinfo();
      hint.ai_flags = 2;
      String hostname = "";

      try {
         hostname = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException var7) {
         LOG.error((String)"Unknown host exception when getting address of local host: {}", (Object)var7.getMessage());
         return "";
      }

      PointerByReference ptr = new PointerByReference();
      int res = LIBC.getaddrinfo(hostname, (String)null, hint, ptr);
      if (res > 0) {
         if (LOG.isErrorEnabled()) {
            LOG.error((String)"Failed getaddrinfo(): {}", (Object)LIBC.gai_strerror(res));
         }

         return "";
      } else {
         CLibrary.Addrinfo info = new CLibrary.Addrinfo(ptr.getValue());
         String canonname = info.ai_canonname.trim();
         LIBC.freeaddrinfo(ptr.getValue());
         return canonname;
      }
   }

   public String getHostName() {
      byte[] hostnameBuffer = new byte[256];
      return 0 != LibC.INSTANCE.gethostname(hostnameBuffer, hostnameBuffer.length) ? super.getHostName() : Native.toString(hostnameBuffer);
   }

   public String getIpv4DefaultGateway() {
      List<String> routes = ExecutingCommand.runNative("route -A inet -n");
      if (routes.size() <= 2) {
         return "";
      } else {
         String gateway = "";
         int minMetric = Integer.MAX_VALUE;

         for(int i = 2; i < routes.size(); ++i) {
            String[] fields = ParseUtil.whitespaces.split((CharSequence)routes.get(i));
            if (fields.length > 4 && fields[0].equals("0.0.0.0")) {
               boolean isGateway = fields[3].indexOf(71) != -1;
               int metric = ParseUtil.parseIntOrDefault(fields[4], Integer.MAX_VALUE);
               if (isGateway && metric < minMetric) {
                  minMetric = metric;
                  gateway = fields[1];
               }
            }
         }

         return gateway;
      }
   }

   public String getIpv6DefaultGateway() {
      List<String> routes = ExecutingCommand.runNative("route -A inet6 -n");
      if (routes.size() <= 2) {
         return "";
      } else {
         String gateway = "";
         int minMetric = Integer.MAX_VALUE;

         for(int i = 2; i < routes.size(); ++i) {
            String[] fields = ParseUtil.whitespaces.split((CharSequence)routes.get(i));
            if (fields.length > 3 && fields[0].equals("::/0")) {
               boolean isGateway = fields[2].indexOf(71) != -1;
               int metric = ParseUtil.parseIntOrDefault(fields[3], Integer.MAX_VALUE);
               if (isGateway && metric < minMetric) {
                  minMetric = metric;
                  gateway = fields[1];
               }
            }
         }

         return gateway;
      }
   }

   static {
      LIBC = LinuxLibc.INSTANCE;
   }
}
