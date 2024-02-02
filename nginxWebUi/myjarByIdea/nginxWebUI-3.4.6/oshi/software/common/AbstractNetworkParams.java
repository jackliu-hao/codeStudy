package oshi.software.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.NetworkParams;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;

@ThreadSafe
public abstract class AbstractNetworkParams implements NetworkParams {
   private static final Logger LOG = LoggerFactory.getLogger(AbstractNetworkParams.class);
   private static final String NAMESERVER = "nameserver";

   public String getDomainName() {
      try {
         return InetAddress.getLocalHost().getCanonicalHostName();
      } catch (UnknownHostException var2) {
         LOG.error((String)"Unknown host exception when getting address of local host: {}", (Object)var2.getMessage());
         return "";
      }
   }

   public String getHostName() {
      try {
         String hn = InetAddress.getLocalHost().getHostName();
         int dot = hn.indexOf(46);
         return dot == -1 ? hn : hn.substring(0, dot);
      } catch (UnknownHostException var3) {
         LOG.error((String)"Unknown host exception when getting address of local host: {}", (Object)var3.getMessage());
         return "";
      }
   }

   public String[] getDnsServers() {
      List<String> resolv = FileUtil.readFile("/etc/resolv.conf");
      String key = "nameserver";
      int maxNameServer = 3;
      List<String> servers = new ArrayList();

      for(int i = 0; i < resolv.size() && servers.size() < maxNameServer; ++i) {
         String line = (String)resolv.get(i);
         if (line.startsWith(key)) {
            String value = line.substring(key.length()).replaceFirst("^[ \t]+", "");
            if (value.length() != 0 && value.charAt(0) != '#' && value.charAt(0) != ';') {
               String val = value.split("[ \t#;]", 2)[0];
               servers.add(val);
            }
         }
      }

      return (String[])servers.toArray(new String[0]);
   }

   protected static String searchGateway(List<String> lines) {
      Iterator var1 = lines.iterator();

      String leftTrimmed;
      do {
         if (!var1.hasNext()) {
            return "";
         }

         String line = (String)var1.next();
         leftTrimmed = line.replaceFirst("^\\s+", "");
      } while(!leftTrimmed.startsWith("gateway:"));

      String[] split = ParseUtil.whitespaces.split(leftTrimmed);
      if (split.length < 2) {
         return "";
      } else {
         return split[1].split("%")[0];
      }
   }

   public String toString() {
      return String.format("Host name: %s, Domain name: %s, DNS servers: %s, IPv4 Gateway: %s, IPv6 Gateway: %s", this.getHostName(), this.getDomainName(), Arrays.toString(this.getDnsServers()), this.getIpv4DefaultGateway(), this.getIpv6DefaultGateway());
   }
}
