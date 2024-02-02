package org.noear.solon.core.util;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.NvMap;

public class PathUtil {
   public static final Pattern pathKeyExpr = Pattern.compile("\\{([^\\\\}]+)\\}");

   public static String mergePath(String path1, String path2) {
      if (!Utils.isEmpty(path1) && !"**".equals(path1) && !"/**".equals(path1)) {
         if (!path1.startsWith("/")) {
            path1 = "/" + path1;
         }

         int idx;
         if (Utils.isEmpty(path2)) {
            if (path1.endsWith("*")) {
               idx = path1.lastIndexOf(47) + 1;
               return idx < 1 ? "/" : path1.substring(0, idx) + path2;
            } else {
               return path1;
            }
         } else {
            if (path2.startsWith("/")) {
               path2 = path2.substring(1);
            }

            if (path1.endsWith("/")) {
               return path1 + path2;
            } else if (path1.endsWith("*")) {
               idx = path1.lastIndexOf(47) + 1;
               return idx < 1 ? path2 : path1.substring(0, idx) + path2;
            } else {
               return path1 + "/" + path2;
            }
         }
      } else {
         return path2.startsWith("/") ? path2 : "/" + path2;
      }
   }

   public static NvMap pathVarMap(String path, String expr) {
      NvMap _map = new NvMap();
      if (expr.indexOf("{") >= 0) {
         String path2 = null;

         try {
            path2 = URLDecoder.decode(path, Solon.encoding());
         } catch (Throwable var9) {
            path2 = path;
         }

         Matcher pm = pathKeyExpr.matcher(expr);
         List<String> _pks = new ArrayList();

         while(pm.find()) {
            _pks.add(pm.group(1));
         }

         if (_pks.size() > 0) {
            PathAnalyzer _pr = PathAnalyzer.get(expr);
            pm = _pr.matcher(path2);
            if (pm.find()) {
               int i = 0;

               for(int len = _pks.size(); i < len; ++i) {
                  _map.put((String)_pks.get(i), pm.group(i + 1));
               }
            }
         }
      }

      return _map;
   }
}
