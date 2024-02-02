package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import oshi.annotation.concurrent.GuardedBy;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.Memoizer;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class Win32ProcessCached {
   private static final Supplier<Win32ProcessCached> INSTANCE = Memoizer.memoize(Win32ProcessCached::createInstance);
   @GuardedBy("commandLineCacheLock")
   private final Map<Integer, Pair<Long, String>> commandLineCache = new HashMap();
   private final ReentrantLock commandLineCacheLock = new ReentrantLock();

   private Win32ProcessCached() {
   }

   public static Win32ProcessCached getInstance() {
      return (Win32ProcessCached)INSTANCE.get();
   }

   private static Win32ProcessCached createInstance() {
      return new Win32ProcessCached();
   }

   public String getCommandLine(int processId, long startTime) {
      this.commandLineCacheLock.lock();

      try {
         Pair<Long, String> pair = (Pair)this.commandLineCache.get(processId);
         if (pair != null && startTime < (Long)pair.getA()) {
            String var15 = (String)pair.getB();
            return var15;
         } else {
            long now = System.currentTimeMillis();
            WbemcliUtil.WmiResult<Win32Process.CommandLineProperty> commandLineAllProcs = Win32Process.queryCommandLines((Set)null);
            if (this.commandLineCache.size() > commandLineAllProcs.getResultCount() * 2) {
               this.commandLineCache.clear();
            }

            String result = "";

            for(int i = 0; i < commandLineAllProcs.getResultCount(); ++i) {
               int pid = WmiUtil.getUint32(commandLineAllProcs, Win32Process.CommandLineProperty.PROCESSID, i);
               String cl = WmiUtil.getString(commandLineAllProcs, Win32Process.CommandLineProperty.COMMANDLINE, i);
               this.commandLineCache.put(pid, new Pair(now, cl));
               if (pid == processId) {
                  result = cl;
               }
            }

            String var16 = result;
            return var16;
         }
      } finally {
         this.commandLineCacheLock.unlock();
      }
   }
}
