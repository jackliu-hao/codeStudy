package cn.hutool;

import cn.hutool.core.lang.ConsoleTable;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import java.util.Iterator;
import java.util.Set;

public class Hutool {
   public static final String AUTHOR = "Looly";

   private Hutool() {
   }

   public static Set<Class<?>> getAllUtils() {
      return ClassUtil.scanPackage("cn.hutool", (clazz) -> {
         return !clazz.isInterface() && StrUtil.endWith(clazz.getSimpleName(), "Util");
      });
   }

   public static void printAllUtils() {
      Set<Class<?>> allUtils = getAllUtils();
      ConsoleTable consoleTable = ConsoleTable.create().addHeader("工具类名", "所在包");
      Iterator var2 = allUtils.iterator();

      while(var2.hasNext()) {
         Class<?> clazz = (Class)var2.next();
         consoleTable.addBody(clazz.getSimpleName(), clazz.getPackage().getName());
      }

      consoleTable.print();
   }
}
