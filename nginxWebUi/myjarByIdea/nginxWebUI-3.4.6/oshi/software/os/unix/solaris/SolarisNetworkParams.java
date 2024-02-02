package oshi.software.os.unix.solaris;

import com.sun.jna.Native;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.solaris.SolarisLibc;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;

@ThreadSafe
final class SolarisNetworkParams extends AbstractNetworkParams {
   private static final SolarisLibc LIBC;

   public String getHostName() {
      byte[] hostnameBuffer = new byte[256];
      return 0 != LIBC.gethostname(hostnameBuffer, hostnameBuffer.length) ? super.getHostName() : Native.toString(hostnameBuffer);
   }

   public String getIpv4DefaultGateway() {
      return searchGateway(ExecutingCommand.runNative("route get -inet default"));
   }

   public String getIpv6DefaultGateway() {
      return searchGateway(ExecutingCommand.runNative("route get -inet6 default"));
   }

   static {
      LIBC = SolarisLibc.INSTANCE;
   }
}
