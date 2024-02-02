package oshi.util.platform.mac;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class SysctlUtil {
   private static final Logger LOG = LoggerFactory.getLogger(SysctlUtil.class);
   private static final String SYSCTL_FAIL = "Failed syctl call: {}, Error code: {}";

   private SysctlUtil() {
   }

   public static int sysctl(String name, int def) {
      IntByReference size = new IntByReference(SystemB.INT_SIZE);
      Pointer p = new Memory((long)size.getValue());
      if (0 != SystemB.INSTANCE.sysctlbyname(name, p, size, (Pointer)null, 0)) {
         LOG.error((String)"Failed sysctl call: {}, Error code: {}", (Object)name, (Object)Native.getLastError());
         return def;
      } else {
         return p.getInt(0L);
      }
   }

   public static long sysctl(String name, long def) {
      IntByReference size = new IntByReference(SystemB.UINT64_SIZE);
      Pointer p = new Memory((long)size.getValue());
      if (0 != SystemB.INSTANCE.sysctlbyname(name, p, size, (Pointer)null, 0)) {
         LOG.error((String)"Failed syctl call: {}, Error code: {}", (Object)name, (Object)Native.getLastError());
         return def;
      } else {
         return p.getLong(0L);
      }
   }

   public static String sysctl(String name, String def) {
      IntByReference size = new IntByReference();
      if (0 != SystemB.INSTANCE.sysctlbyname(name, (Pointer)null, size, (Pointer)null, 0)) {
         LOG.error((String)"Failed syctl call: {}, Error code: {}", (Object)name, (Object)Native.getLastError());
         return def;
      } else {
         Pointer p = new Memory((long)size.getValue() + 1L);
         if (0 != SystemB.INSTANCE.sysctlbyname(name, p, size, (Pointer)null, 0)) {
            LOG.error((String)"Failed syctl call: {}, Error code: {}", (Object)name, (Object)Native.getLastError());
            return def;
         } else {
            return p.getString(0L);
         }
      }
   }

   public static boolean sysctl(String name, Structure struct) {
      if (0 != SystemB.INSTANCE.sysctlbyname(name, struct.getPointer(), new IntByReference(struct.size()), (Pointer)null, 0)) {
         LOG.error((String)"Failed syctl call: {}, Error code: {}", (Object)name, (Object)Native.getLastError());
         return false;
      } else {
         struct.read();
         return true;
      }
   }

   public static Memory sysctl(String name) {
      IntByReference size = new IntByReference();
      if (0 != SystemB.INSTANCE.sysctlbyname(name, (Pointer)null, size, (Pointer)null, 0)) {
         LOG.error((String)"Failed syctl call: {}, Error code: {}", (Object)name, (Object)Native.getLastError());
         return null;
      } else {
         Memory m = new Memory((long)size.getValue());
         if (0 != SystemB.INSTANCE.sysctlbyname(name, m, size, (Pointer)null, 0)) {
            LOG.error((String)"Failed syctl call: {}, Error code: {}", (Object)name, (Object)Native.getLastError());
            return null;
         } else {
            return m;
         }
      }
   }
}
