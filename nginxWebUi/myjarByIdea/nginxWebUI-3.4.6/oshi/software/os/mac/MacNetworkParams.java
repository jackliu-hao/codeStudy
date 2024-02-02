package oshi.software.os.mac;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.mac.SystemB;
import oshi.jna.platform.unix.CLibrary;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
final class MacNetworkParams extends AbstractNetworkParams {
   private static final Logger LOG = LoggerFactory.getLogger(MacNetworkParams.class);
   private static final SystemB SYS;
   private static final String IPV6_ROUTE_HEADER = "Internet6:";
   private static final String DEFAULT_GATEWAY = "default";

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
      int res = SYS.getaddrinfo(hostname, (String)null, hint, ptr);
      if (res > 0) {
         if (LOG.isErrorEnabled()) {
            LOG.error((String)"Failed getaddrinfo(): {}", (Object)SYS.gai_strerror(res));
         }

         return "";
      } else {
         CLibrary.Addrinfo info = new CLibrary.Addrinfo(ptr.getValue());
         String canonname = info.ai_canonname.trim();
         SYS.freeaddrinfo(ptr.getValue());
         return canonname;
      }
   }

   public String getHostName() {
      byte[] hostnameBuffer = new byte[256];
      return 0 != SYS.gethostname(hostnameBuffer, hostnameBuffer.length) ? super.getHostName() : Native.toString(hostnameBuffer);
   }

   public String getIpv4DefaultGateway() {
      return searchGateway(ExecutingCommand.runNative("route -n get default"));
   }

   public String getIpv6DefaultGateway() {
      List<String> lines = ExecutingCommand.runNative("netstat -nr");
      boolean v6Table = false;
      Iterator var3 = lines.iterator();

      String[] fields;
      label27:
      do {
         while(var3.hasNext()) {
            String line = (String)var3.next();
            if (v6Table && line.startsWith("default")) {
               fields = ParseUtil.whitespaces.split(line);
               continue label27;
            }

            if (line.startsWith("Internet6:")) {
               v6Table = true;
            }
         }

         return "";
      } while(fields.length <= 2 || !fields[2].contains("G"));

      return fields[1].split("%")[0];
   }

   static {
      SYS = SystemB.INSTANCE;
   }
}
