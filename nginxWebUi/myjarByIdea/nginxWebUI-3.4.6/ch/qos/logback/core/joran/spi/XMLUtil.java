package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.status.StatusManager;
import java.net.URL;

public class XMLUtil {
   public static final int ILL_FORMED = 1;
   public static final int UNRECOVERABLE_ERROR = 2;

   public static int checkIfWellFormed(URL url, StatusManager sm) {
      return 0;
   }
}
