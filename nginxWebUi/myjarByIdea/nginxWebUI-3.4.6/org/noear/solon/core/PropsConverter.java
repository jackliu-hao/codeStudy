package org.noear.solon.core;

import java.lang.reflect.Type;
import java.util.Properties;
import org.noear.solon.Utils;
import org.noear.solon.core.wrap.ClassWrap;

public class PropsConverter {
   private static PropsConverter global;

   public static PropsConverter global() {
      return global;
   }

   public static void globalSet(PropsConverter instance) {
      if (instance != null) {
         global = instance;
      }

   }

   public <T> T convert(Properties props, T target, Class<T> targetClz, Type targetType) {
      if (target == null) {
         return ClassWrap.get(targetClz).newBy(props);
      } else {
         ClassWrap.get(target.getClass()).fill(target, props::getProperty);
         return target;
      }
   }

   static {
      PropsConverter tmp = (PropsConverter)Utils.newInstance("org.noear.solon.extend.impl.PropsConverterExt");
      if (tmp == null) {
         global = new PropsConverter();
      } else {
         global = tmp;
      }

   }
}
