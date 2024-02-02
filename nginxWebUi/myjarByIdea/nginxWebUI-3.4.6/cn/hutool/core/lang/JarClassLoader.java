package cn.hutool.core.lang;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.URLUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;

public class JarClassLoader extends URLClassLoader {
   public static JarClassLoader load(File dir) {
      JarClassLoader loader = new JarClassLoader();
      loader.addJar(dir);
      loader.addURL(dir);
      return loader;
   }

   public static JarClassLoader loadJar(File jarFile) {
      JarClassLoader loader = new JarClassLoader();
      loader.addJar(jarFile);
      return loader;
   }

   public static void loadJar(URLClassLoader loader, File jarFile) throws UtilException {
      try {
         Method method = ClassUtil.getDeclaredMethod(URLClassLoader.class, "addURL", URL.class);
         if (null != method) {
            method.setAccessible(true);
            List<File> jars = loopJar(jarFile);
            Iterator var4 = jars.iterator();

            while(var4.hasNext()) {
               File jar = (File)var4.next();
               ReflectUtil.invoke(loader, (Method)method, jar.toURI().toURL());
            }
         }

      } catch (IOException var6) {
         throw new UtilException(var6);
      }
   }

   public static URLClassLoader loadJarToSystemClassLoader(File jarFile) {
      URLClassLoader urlClassLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
      loadJar(urlClassLoader, jarFile);
      return urlClassLoader;
   }

   public JarClassLoader() {
      this(new URL[0]);
   }

   public JarClassLoader(URL[] urls) {
      super(urls, ClassUtil.getClassLoader());
   }

   public JarClassLoader(URL[] urls, ClassLoader classLoader) {
      super(urls, classLoader);
   }

   public JarClassLoader addJar(File jarFileOrDir) {
      if (isJarFile(jarFileOrDir)) {
         return this.addURL(jarFileOrDir);
      } else {
         List<File> jars = loopJar(jarFileOrDir);
         Iterator var3 = jars.iterator();

         while(var3.hasNext()) {
            File jar = (File)var3.next();
            this.addURL(jar);
         }

         return this;
      }
   }

   public void addURL(URL url) {
      super.addURL(url);
   }

   public JarClassLoader addURL(File dir) {
      super.addURL(URLUtil.getURL(dir));
      return this;
   }

   private static List<File> loopJar(File file) {
      return FileUtil.loopFiles(file, JarClassLoader::isJarFile);
   }

   private static boolean isJarFile(File file) {
      return !FileUtil.isFile(file) ? false : file.getPath().toLowerCase().endsWith(".jar");
   }
}
