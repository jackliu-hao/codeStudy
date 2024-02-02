package org.noear.solon.data.util;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.FieldWrap;

public class InvKeys {
   public static String buildByInv(Invocation inv) {
      Method method = inv.method().getMethod();
      StringBuilder keyB = new StringBuilder();
      keyB.append(method.getDeclaringClass().getName()).append(":");
      keyB.append(method.getName()).append(":");
      inv.argsAsMap().forEach((k, v) -> {
         keyB.append(k).append("_").append(v);
      });
      return Utils.md5(keyB.toString());
   }

   public static String buildByTmlAndInv(String tml, Invocation inv) {
      return buildByTmlAndInv(tml, inv, (Object)null);
   }

   public static String buildByTmlAndInv(String tml, Invocation inv, Object rst) {
      if (tml.indexOf("$") < 0) {
         return tml;
      } else {
         Map map = inv.argsAsMap();
         String str2 = tml;
         Pattern pattern = Pattern.compile("\\$\\{(\\w*\\.?\\w+)\\}");
         Matcher m = pattern.matcher(tml);

         while(m.find()) {
            String mark = m.group(0);
            String name = m.group(1);
            if (map.containsKey(name)) {
               String val = String.valueOf(map.get(name));
               str2 = str2.replace(mark, val);
            } else {
               if (!name.contains(".")) {
                  throw new IllegalArgumentException("Missing cache tag parameter: " + name);
               }

               String fieldKey = null;
               String fieldVal = null;
               String[] valTmp;
               Object obj;
               if (name.startsWith(".")) {
                  obj = rst;
                  fieldKey = name.substring(1);
               } else {
                  valTmp = name.split("\\.");
                  obj = map.get(valTmp[0]);
                  fieldKey = valTmp[1];
               }

               if (obj != null) {
                  valTmp = null;
                  Object valTmp;
                  if (obj instanceof Map) {
                     valTmp = ((Map)obj).get(fieldKey);
                  } else {
                     FieldWrap fw = ClassWrap.get(obj.getClass()).getFieldWrap(fieldKey);
                     if (fw == null) {
                        throw new IllegalArgumentException("Missing cache tag parameter (result field): " + name);
                     }

                     try {
                        valTmp = fw.getValue(obj);
                     } catch (ReflectiveOperationException var15) {
                        throw new RuntimeException(var15);
                     }
                  }

                  if (valTmp != null) {
                     fieldVal = valTmp.toString();
                  }
               }

               if (fieldVal == null) {
                  fieldVal = "null";
               }

               str2 = str2.replace(mark, fieldVal);
            }
         }

         return str2;
      }
   }
}
