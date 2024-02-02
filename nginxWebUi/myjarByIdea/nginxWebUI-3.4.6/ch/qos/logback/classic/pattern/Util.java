package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ClassPackagingData;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Marker;

public class Util {
   static Map<String, ClassPackagingData> cache = new HashMap();

   public static boolean match(Marker marker, Marker[] markerArray) {
      if (markerArray == null) {
         throw new IllegalArgumentException("markerArray should not be null");
      } else {
         int size = markerArray.length;

         for(int i = 0; i < size; ++i) {
            if (marker.contains(markerArray[i])) {
               return true;
            }
         }

         return false;
      }
   }
}
