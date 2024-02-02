package org.noear.solon.core.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.noear.solon.annotation.Inject;

public class IndexBuilder {
   private static final Map<String, Integer> map = new HashMap();
   private static final ArrayList<String> classStack = new ArrayList();

   public static int buildIndex(Class<?> clazz) {
      return buildIndex(clazz, true);
   }

   private static int buildIndex(Class<?> clazz, Boolean stackTop) {
      if (stackTop) {
         classStack.clear();
         if (isLoopRelate(clazz, clazz.getName())) {
            String link = "";

            for(int i = 0; i < classStack.size(); ++i) {
               link = link + (String)classStack.get(i);
               if (i != classStack.size() - 1) {
                  link = link + " -> ";
               }
            }

            throw new IllegalStateException("Dependency loops are not supported: " + link);
         }
      }

      if (map.get(clazz.getName()) != null) {
         return (Integer)map.get(clazz.getName());
      } else {
         List<Class<?>> clazzList = findRelateClass(clazz);
         if (clazzList.size() == 0) {
            map.put(clazz.getName(), 0);
            return 0;
         } else {
            Integer maxIndex = null;
            Iterator var4 = clazzList.iterator();

            while(var4.hasNext()) {
               Class<?> clazzRelate = (Class)var4.next();
               Integer index = buildIndex(clazzRelate, false);
               if (maxIndex == null) {
                  maxIndex = index;
               } else if (maxIndex < index) {
                  maxIndex = index;
               }
            }

            map.put(clazz.getName(), maxIndex + 1);
            return maxIndex + 1;
         }
      }
   }

   private static List<Class<?>> findRelateClass(Class<?> clazz) {
      List<Class<?>> clazzList = new ArrayList();
      Field[] fields = clazz.getDeclaredFields();
      Field[] var3 = fields;
      int var4 = fields.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Field field = var3[var5];
         if (field.isAnnotationPresent(Inject.class)) {
            Inject inject = (Inject)field.getAnnotation(Inject.class);
            if (!inject.value().contains("${") && !clazz.equals(field.getType())) {
               clazzList.add(field.getType());
            }
         }
      }

      return clazzList;
   }

   private static boolean isLoopRelate(Class<?> clazz, String topName) {
      classStack.add(clazz.getName());
      List<Class<?>> clazzList = findRelateClass(clazz);
      Iterator var3 = clazzList.iterator();

      Class clazzRelate;
      do {
         if (!var3.hasNext()) {
            var3 = clazzList.iterator();

            do {
               if (!var3.hasNext()) {
                  classStack.remove(clazz.getName());
                  return false;
               }

               clazzRelate = (Class)var3.next();
            } while(!isLoopRelate(clazzRelate, topName));

            return true;
         }

         clazzRelate = (Class)var3.next();
      } while(!clazzRelate.getName().equals(topName));

      classStack.add(clazzRelate.getName());
      return true;
   }
}
