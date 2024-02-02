package cn.hutool.setting.dialect;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.core.util.StrUtil;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PropsUtil {
   private static final Map<String, Props> propsMap = new ConcurrentHashMap();

   public static Props get(String name) {
      return (Props)propsMap.computeIfAbsent(name, (filePath) -> {
         String extName = FileUtil.extName(filePath);
         if (StrUtil.isEmpty(extName)) {
            filePath = filePath + "." + "properties";
         }

         return new Props(filePath);
      });
   }

   public static Props getFirstFound(String... names) {
      String[] var1 = names;
      int var2 = names.length;
      int var3 = 0;

      while(var3 < var2) {
         String name = var1[var3];

         try {
            return get(name);
         } catch (NoResourceException var6) {
            ++var3;
         }
      }

      return null;
   }

   public static Props getSystemProps() {
      return new Props(System.getProperties());
   }
}
