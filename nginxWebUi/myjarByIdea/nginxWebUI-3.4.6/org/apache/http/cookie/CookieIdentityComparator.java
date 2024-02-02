package org.apache.http.cookie;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class CookieIdentityComparator implements Serializable, Comparator<Cookie> {
   private static final long serialVersionUID = 4466565437490631532L;

   public int compare(Cookie c1, Cookie c2) {
      int res = c1.getName().compareTo(c2.getName());
      String p1;
      String d2;
      if (res == 0) {
         p1 = c1.getDomain();
         if (p1 == null) {
            p1 = "";
         } else if (p1.indexOf(46) == -1) {
            p1 = p1 + ".local";
         }

         d2 = c2.getDomain();
         if (d2 == null) {
            d2 = "";
         } else if (d2.indexOf(46) == -1) {
            d2 = d2 + ".local";
         }

         res = p1.compareToIgnoreCase(d2);
      }

      if (res == 0) {
         p1 = c1.getPath();
         if (p1 == null) {
            p1 = "/";
         }

         d2 = c2.getPath();
         if (d2 == null) {
            d2 = "/";
         }

         res = p1.compareTo(d2);
      }

      return res;
   }
}
