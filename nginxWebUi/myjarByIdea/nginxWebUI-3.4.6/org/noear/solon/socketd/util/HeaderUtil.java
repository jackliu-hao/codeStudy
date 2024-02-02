package org.noear.solon.socketd.util;

import java.util.Map;
import org.noear.solon.Utils;
import org.noear.solon.core.NvMap;

public class HeaderUtil {
   public static String encodeHeaderMap(Map<String, String> headers) {
      StringBuilder header = new StringBuilder();
      if (headers != null) {
         headers.forEach((k, v) -> {
            header.append(k).append("=").append(v).append("&");
         });
         if (header.length() > 0) {
            header.setLength(header.length() - 1);
         }
      }

      return header.toString();
   }

   public static Map<String, String> decodeHeaderMap(String header) {
      NvMap headerMap = new NvMap();
      if (Utils.isNotEmpty(header)) {
         String[] ss = header.split("&");
         String[] var3 = ss;
         int var4 = ss.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String s = var3[var5];
            String[] kv = s.split("=");
            if (kv.length == 2) {
               headerMap.put(kv[0], kv[1]);
            }
         }
      }

      return headerMap;
   }
}
