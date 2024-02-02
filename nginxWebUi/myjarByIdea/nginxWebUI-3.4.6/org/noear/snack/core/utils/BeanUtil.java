package org.noear.snack.core.utils;

import java.io.Reader;
import java.sql.Clob;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.noear.snack.exception.SnackException;

public class BeanUtil {
   public static final Map<String, Class<?>> clzCached = new ConcurrentHashMap();

   public static Class<?> loadClass(String clzName) {
      try {
         Class<?> clz = (Class)clzCached.get(clzName);
         if (clz == null) {
            clz = Class.forName(clzName);
            clzCached.put(clzName, clz);
         }

         return clz;
      } catch (RuntimeException var2) {
         throw var2;
      } catch (Throwable var3) {
         throw new SnackException(var3);
      }
   }

   public static String clobToString(Clob clob) {
      Reader reader = null;
      StringBuilder buf = new StringBuilder();

      try {
         reader = clob.getCharacterStream();
         char[] chars = new char[2048];

         while(true) {
            int len = reader.read(chars, 0, chars.length);
            if (len < 0) {
               break;
            }

            buf.append(chars, 0, len);
         }
      } catch (Throwable var6) {
         throw new SnackException("read string from reader error", var6);
      }

      String text = buf.toString();
      if (reader != null) {
         try {
            reader.close();
         } catch (Throwable var5) {
            throw new SnackException("read string from reader error", var5);
         }
      }

      return text;
   }

   public static Object newInstance(Class<?> clz) {
      try {
         return clz.isInterface() ? null : clz.newInstance();
      } catch (Throwable var2) {
         throw new SnackException("create instance error, class " + clz.getName(), var2);
      }
   }
}
