package org.noear.solon.aspect;

import java.lang.reflect.InvocationHandler;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.util.ScanUtil;

public class AspectUtil {
   private static Set<Class<?>> tryAttachCached = new HashSet();

   public static boolean binding(BeanWrap bw, String name, boolean typed) {
      if (bw.proxy() instanceof AspectUtil) {
         return false;
      } else {
         bw.proxySet(BeanProxy.global);
         bw.context().beanRegister(bw, name, typed);
         return true;
      }
   }

   public static boolean binding(BeanWrap bw) {
      return binding(bw, "", false);
   }

   public static void attach(Class<?> clz, InvocationHandler handler) {
      if (!clz.isAnnotation() && !clz.isInterface() && !clz.isEnum() && !clz.isPrimitive()) {
         if (!tryAttachCached.contains(clz)) {
            tryAttachCached.add(clz);
            Aop.wrapAndPut(clz).proxySet(new BeanProxy(handler));
         }
      }
   }

   public static void attachByScan(String basePackage, InvocationHandler handler) {
      attachByScan(JarClassLoader.global(), basePackage, (Predicate)null, handler);
   }

   public static void attachByScan(String basePackage, Predicate<String> filter, InvocationHandler handler) {
      attachByScan(JarClassLoader.global(), basePackage, filter, handler);
   }

   public static void attachByScan(ClassLoader classLoader, String basePackage, Predicate<String> filter, InvocationHandler handler) {
      if (!Utils.isEmpty(basePackage)) {
         if (classLoader != null) {
            if (filter == null) {
               filter = (s) -> {
                  return true;
               };
            }

            String dir = basePackage.replace('.', '/');
            ScanUtil.scan(classLoader, dir, (n) -> {
               return n.endsWith(".class");
            }).stream().sorted(Comparator.comparing((s) -> {
               return s.length();
            })).filter(filter).forEach((name) -> {
               String className = name.substring(0, name.length() - 6);
               Class<?> clz = Utils.loadClass(classLoader, className.replace("/", "."));
               if (clz != null) {
                  attach(clz, handler);
               }

            });
         }
      }
   }
}
