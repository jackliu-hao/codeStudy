package org.noear.solon.core;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.PrintUtil;

public class ExtendLoader {
   private static final ExtendLoader instance = new ExtendLoader();
   private static String path;

   public static String path() {
      return path;
   }

   public static List<ClassLoader> load(String extend) {
      return load(extend, false);
   }

   public static List<ClassLoader> load(String extend, boolean autoMake) {
      return load(extend, autoMake, (Predicate)null);
   }

   public static List<ClassLoader> load(String extend, boolean autoMake, Predicate<String> filter) {
      List<ClassLoader> loaders = new ArrayList();
      loaders.add(JarClassLoader.global());
      if (Utils.isNotEmpty(extend)) {
         if (extend.startsWith("!")) {
            extend = extend.substring(1);
            autoMake = true;
         }

         extend = Utils.buildExt(extend, autoMake);
         if (extend != null) {
            path = extend;
            PrintUtil.blueln("Extend: " + path);
            instance.loadFile(loaders, new File(path), filter);
         }
      }

      return loaders;
   }

   public static boolean loadJar(List<ClassLoader> loaders, File file) {
      try {
         if (!Solon.app().enableJarIsolation() && !file.getName().startsWith("!")) {
            JarClassLoader.global().addJar(file);
         } else {
            loaders.add(JarClassLoader.loadJar(file));
         }

         return true;
      } catch (Throwable var3) {
         EventBus.push(var3);
         return false;
      }
   }

   public static boolean loadJar(File file) {
      try {
         JarClassLoader.global().addJar(file);
         return true;
      } catch (Throwable var2) {
         EventBus.push(var2);
         return false;
      }
   }

   public static boolean unloadJar(File file) {
      try {
         JarClassLoader.global().removeJar(file);
         return true;
      } catch (Throwable var2) {
         EventBus.push(var2);
         return false;
      }
   }

   private ExtendLoader() {
   }

   private void loadFile(List<ClassLoader> loaders, File file, Predicate<String> filter) {
      if (file.exists()) {
         if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            File[] var5 = tmps;
            int var6 = tmps.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               File tmp = var5[var7];
               this.loadFileDo(loaders, tmp, filter);
            }
         } else {
            this.loadFileDo(loaders, file, filter);
         }

      }
   }

   private void loadFileDo(List<ClassLoader> loaders, File file, Predicate<String> filter) {
      if (file.isFile()) {
         String path = file.getAbsolutePath();
         if (filter != null && !filter.test(path)) {
            return;
         }

         try {
            if (path.endsWith(".jar") || path.endsWith(".zip")) {
               loadJar(loaders, file);
               return;
            }

            if (path.endsWith(".properties")) {
               Solon.cfg().loadAdd((URL)file.toURI().toURL());
               PrintUtil.blueln("loaded: " + path);
               return;
            }

            if (path.endsWith(".yml")) {
               if (!PropsLoader.global().isSupport(path)) {
                  throw new IllegalStateException("Do not support the *.yml");
               }

               Solon.cfg().loadAdd((URL)file.toURI().toURL());
               PrintUtil.blueln("loaded: " + path);
               return;
            }
         } catch (Throwable var6) {
            EventBus.push(var6);
         }
      }

   }
}
