package oshi.driver.unix;

import java.util.Iterator;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class NetStatTcp {
   private NetStatTcp() {
   }

   public static Pair<Long, Long> queryTcpnetstat() {
      long tcp4 = 0L;
      long tcp6 = 0L;
      List<String> activeConns = ExecutingCommand.runNative("netstat -n -p tcp");
      Iterator var5 = activeConns.iterator();

      while(var5.hasNext()) {
         String s = (String)var5.next();
         if (s.endsWith("ESTABLISHED")) {
            if (s.startsWith("tcp4")) {
               ++tcp4;
            } else if (s.startsWith("tcp6")) {
               ++tcp6;
            }
         }
      }

      return new Pair(tcp4, tcp6);
   }
}
