package org.noear.solon.extend.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Properties;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.solon.Utils;
import org.noear.solon.core.PropsConverter;

public class PropsConverterExt extends PropsConverter {
   public <T> T convert(Properties props, T target, Class<T> targetClz, Type targetType) {
      if (target == null) {
         try {
            Constructor constructor = targetClz.getConstructor(Properties.class);
            if (constructor != null) {
               return constructor.newInstance(props);
            }
         } catch (NoSuchMethodException var6) {
         } catch (Throwable var7) {
            Throwable e = Utils.throwableUnwrap(var7);
            if (e instanceof RuntimeException) {
               throw (RuntimeException)e;
            }

            throw new RuntimeException(e);
         }

         if (targetType == null) {
            targetType = targetClz;
         }

         return ONode.loadObj(props, (Feature[])(Feature.UseSetter)).toObject((Type)targetType);
      } else {
         return ONode.loadObj(props, (Feature[])(Feature.UseSetter)).bindTo(target);
      }
   }
}
