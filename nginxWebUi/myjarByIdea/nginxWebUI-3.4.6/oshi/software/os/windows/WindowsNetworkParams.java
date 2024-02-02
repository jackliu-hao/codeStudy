package oshi.software.os.windows;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.IPHlpAPI;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.ptr.IntByReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
final class WindowsNetworkParams extends AbstractNetworkParams {
   private static final Logger LOG = LoggerFactory.getLogger(WindowsNetworkParams.class);
   private static final int COMPUTER_NAME_DNS_DOMAIN_FULLY_QUALIFIED = 3;

   public String getDomainName() {
      char[] buffer = new char[256];
      IntByReference bufferSize = new IntByReference(buffer.length);
      if (!Kernel32.INSTANCE.GetComputerNameEx(3, buffer, bufferSize)) {
         LOG.error((String)"Failed to get dns domain name. Error code: {}", (Object)Kernel32.INSTANCE.GetLastError());
         return "";
      } else {
         return (new String(buffer)).trim();
      }
   }

   public String[] getDnsServers() {
      IntByReference bufferSize = new IntByReference();
      int ret = IPHlpAPI.INSTANCE.GetNetworkParams((Pointer)null, bufferSize);
      if (ret != 111) {
         LOG.error((String)"Failed to get network parameters buffer size. Error code: {}", (Object)ret);
         return new String[0];
      } else {
         Memory buffer = new Memory((long)bufferSize.getValue());
         ret = IPHlpAPI.INSTANCE.GetNetworkParams(buffer, bufferSize);
         if (ret != 0) {
            LOG.error((String)"Failed to get network parameters. Error code: {}", (Object)ret);
            return new String[0];
         } else {
            IPHlpAPI.FIXED_INFO fixedInfo = new IPHlpAPI.FIXED_INFO(buffer);
            List<String> list = new ArrayList();

            for(IPHlpAPI.IP_ADDR_STRING dns = fixedInfo.DnsServerList; dns != null; dns = ((IPHlpAPI.IP_ADDR_STRING)dns).Next) {
               String addr = new String(((IPHlpAPI.IP_ADDR_STRING)dns).IpAddress.String, StandardCharsets.US_ASCII);
               int nullPos = addr.indexOf(0);
               if (nullPos != -1) {
                  addr = addr.substring(0, nullPos);
               }

               list.add(addr);
            }

            return (String[])list.toArray(new String[0]);
         }
      }
   }

   public String getHostName() {
      return Kernel32Util.getComputerName();
   }

   public String getIpv4DefaultGateway() {
      return parseIpv4Route();
   }

   public String getIpv6DefaultGateway() {
      return parseIpv6Route();
   }

   private static String parseIpv4Route() {
      List<String> lines = ExecutingCommand.runNative("route print -4 0.0.0.0");
      Iterator var1 = lines.iterator();

      String[] fields;
      do {
         if (!var1.hasNext()) {
            return "";
         }

         String line = (String)var1.next();
         fields = ParseUtil.whitespaces.split(line.trim());
      } while(fields.length <= 2 || !"0.0.0.0".equals(fields[0]));

      return fields[2];
   }

   private static String parseIpv6Route() {
      List<String> lines = ExecutingCommand.runNative("route print -6 ::/0");
      Iterator var1 = lines.iterator();

      String[] fields;
      do {
         if (!var1.hasNext()) {
            return "";
         }

         String line = (String)var1.next();
         fields = ParseUtil.whitespaces.split(line.trim());
      } while(fields.length <= 3 || !"::/0".equals(fields[2]));

      return fields[3];
   }
}
