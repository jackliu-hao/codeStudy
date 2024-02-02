package cn.hutool.setting;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.core.util.StrUtil;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SettingUtil {
   private static final Map<String, Setting> SETTING_MAP = new ConcurrentHashMap();

   public static Setting get(String name) {
      return (Setting)SETTING_MAP.computeIfAbsent(name, (filePath) -> {
         String extName = FileNameUtil.extName(filePath);
         if (StrUtil.isEmpty(extName)) {
            filePath = filePath + "." + "setting";
         }

         return new Setting(filePath, true);
      });
   }

   public static Setting getFirstFound(String... names) {
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
}
