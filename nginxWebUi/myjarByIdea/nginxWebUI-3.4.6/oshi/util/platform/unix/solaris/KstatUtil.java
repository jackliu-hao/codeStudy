package oshi.util.platform.unix.solaris;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.solaris.LibKstat;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FormatUtil;
import oshi.util.Util;

@ThreadSafe
public final class KstatUtil {
   private static final Logger LOG = LoggerFactory.getLogger(KstatUtil.class);
   private static final LibKstat KS;
   private static final LibKstat.KstatCtl KC;
   private static final ReentrantLock CHAIN;

   private KstatUtil() {
   }

   public static KstatChain openChain() {
      return new KstatChain();
   }

   public static String dataLookupString(LibKstat.Kstat ksp, String name) {
      if (ksp.ks_type != 1 && ksp.ks_type != 4) {
         throw new IllegalArgumentException("Not a kstat_named or kstat_timer kstat.");
      } else {
         Pointer p = KS.kstat_data_lookup(ksp, name);
         if (p == null) {
            LOG.error((String)"Failed lo lookup kstat value for key {}", (Object)name);
            return "";
         } else {
            LibKstat.KstatNamed data = new LibKstat.KstatNamed(p);
            switch (data.data_type) {
               case 0:
                  return (new String(data.value.charc, StandardCharsets.UTF_8)).trim();
               case 1:
                  return Integer.toString(data.value.i32);
               case 2:
                  return FormatUtil.toUnsignedString(data.value.ui32);
               case 3:
                  return Long.toString(data.value.i64);
               case 4:
                  return FormatUtil.toUnsignedString(data.value.ui64);
               case 5:
               case 6:
               case 7:
               case 8:
               default:
                  LOG.error((String)"Unimplemented kstat data type {}", (Object)data.data_type);
                  return "";
               case 9:
                  return data.value.str.addr.getString(0L);
            }
         }
      }
   }

   public static long dataLookupLong(LibKstat.Kstat ksp, String name) {
      if (ksp.ks_type != 1 && ksp.ks_type != 4) {
         throw new IllegalArgumentException("Not a kstat_named or kstat_timer kstat.");
      } else {
         Pointer p = KS.kstat_data_lookup(ksp, name);
         if (p == null) {
            if (LOG.isErrorEnabled()) {
               LOG.error("Failed lo lookup kstat value on {}:{}:{} for key {}", (new String(ksp.ks_module, StandardCharsets.US_ASCII)).trim(), ksp.ks_instance, (new String(ksp.ks_name, StandardCharsets.US_ASCII)).trim(), name);
            }

            return 0L;
         } else {
            LibKstat.KstatNamed data = new LibKstat.KstatNamed(p);
            switch (data.data_type) {
               case 1:
                  return (long)data.value.i32;
               case 2:
                  return FormatUtil.getUnsignedInt(data.value.ui32);
               case 3:
                  return data.value.i64;
               case 4:
                  return data.value.ui64;
               default:
                  LOG.error((String)"Unimplemented or non-numeric kstat data type {}", (Object)data.data_type);
                  return 0L;
            }
         }
      }
   }

   static {
      KS = LibKstat.INSTANCE;
      KC = KS.kstat_open();
      CHAIN = new ReentrantLock();
   }

   public static final class KstatChain implements AutoCloseable {
      private KstatChain() {
         KstatUtil.CHAIN.lock();
         this.update();
      }

      public boolean read(LibKstat.Kstat ksp) {
         int retry = 0;

         while(true) {
            if (0 <= KstatUtil.KS.kstat_read(KstatUtil.KC, ksp, (Pointer)null)) {
               return true;
            }

            if (11 != Native.getLastError()) {
               break;
            }

            ++retry;
            if (5 <= retry) {
               break;
            }

            Util.sleep((long)(8 << retry));
         }

         if (KstatUtil.LOG.isErrorEnabled()) {
            KstatUtil.LOG.error("Failed to read kstat {}:{}:{}", (new String(ksp.ks_module, StandardCharsets.US_ASCII)).trim(), ksp.ks_instance, (new String(ksp.ks_name, StandardCharsets.US_ASCII)).trim());
         }

         return false;
      }

      public LibKstat.Kstat lookup(String module, int instance, String name) {
         return KstatUtil.KS.kstat_lookup(KstatUtil.KC, module, instance, name);
      }

      public List<LibKstat.Kstat> lookupAll(String module, int instance, String name) {
         List<LibKstat.Kstat> kstats = new ArrayList();

         for(LibKstat.Kstat ksp = KstatUtil.KS.kstat_lookup(KstatUtil.KC, module, instance, name); ksp != null; ksp = ksp.next()) {
            if ((module == null || module.equals((new String(ksp.ks_module, StandardCharsets.US_ASCII)).trim())) && (instance < 0 || instance == ksp.ks_instance) && (name == null || name.equals((new String(ksp.ks_name, StandardCharsets.US_ASCII)).trim()))) {
               kstats.add(ksp);
            }
         }

         return kstats;
      }

      public int update() {
         return KstatUtil.KS.kstat_chain_update(KstatUtil.KC);
      }

      public void close() {
         KstatUtil.CHAIN.unlock();
      }

      // $FF: synthetic method
      KstatChain(Object x0) {
         this();
      }
   }
}
