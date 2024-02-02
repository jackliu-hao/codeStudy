package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.Logger;
import java.util.Comparator;

public class LoggerComparator implements Comparator<Logger> {
   public int compare(Logger l1, Logger l2) {
      if (l1.getName().equals(l2.getName())) {
         return 0;
      } else if (l1.getName().equals("ROOT")) {
         return -1;
      } else {
         return l2.getName().equals("ROOT") ? 1 : l1.getName().compareTo(l2.getName());
      }
   }
}
