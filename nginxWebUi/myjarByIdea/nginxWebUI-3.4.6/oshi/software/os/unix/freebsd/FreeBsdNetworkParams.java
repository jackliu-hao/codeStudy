package oshi.software.os.unix.freebsd;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.CLibrary;
import oshi.jna.platform.unix.freebsd.FreeBsdLibc;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;

@ThreadSafe
final class FreeBsdNetworkParams extends AbstractNetworkParams {
   private static final Logger LOG = LoggerFactory.getLogger(FreeBsdNetworkParams.class);
   private static final FreeBsdLibc LIBC;

   public String getDomainName() {
      CLibrary.Addrinfo hint = new CLibrary.Addrinfo();
      hint.ai_flags = 2;
      String hostname = this.getHostName();
      PointerByReference ptr = new PointerByReference();
      int res = LIBC.getaddrinfo(hostname, (String)null, hint, ptr);
      if (res > 0) {
         if (LOG.isErrorEnabled()) {
            LOG.warn((String)"Failed getaddrinfo(): {}", (Object)LIBC.gai_strerror(res));
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
      return 0 != LIBC.gethostname(hostnameBuffer, hostnameBuffer.length) ? super.getHostName() : Native.toString(hostnameBuffer);
   }

   public String getIpv4DefaultGateway() {
      return searchGateway(ExecutingCommand.runNative("route -4 get default"));
   }

   public String getIpv6DefaultGateway() {
      return searchGateway(ExecutingCommand.runNative("route -6 get default"));
   }

   static {
      LIBC = FreeBsdLibc.INSTANCE;
   }
}
