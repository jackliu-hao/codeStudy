package org.noear.solon.core;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.noear.solon.Utils;

public class JarClassLoader extends URLClassLoader {
   private static JarClassLoader global;
   private Map<URL, JarURLConnection> cachedMap;

   public static JarClassLoader global() {
      return global;
   }

   public static void globalSet(JarClassLoader instance) {
      if (instance != null) {
         global = instance;
      }

   }

   public static JarClassLoader loadJar(URL url) {
      JarClassLoader loader = new JarClassLoader();
      loader.addJar(url);
      return loader;
   }

   public static JarClassLoader loadJar(File fileOrDir) {
      JarClassLoader loader = new JarClassLoader();
      loader.addJar(fileOrDir);
      return loader;
   }

   public JarClassLoader() {
      this(Utils.getClassLoader());
   }

   public JarClassLoader(ClassLoader parent) {
      super(new URL[0], parent);
      this.cachedMap = new HashMap();
   }

   public JarClassLoader(URL[] urls, ClassLoader parent) {
      super(urls, parent);
      this.cachedMap = new HashMap();
   }

   public void addJar(URL url) {
      this.addJar(url, true);
   }

   public void addJar(File file) {
      try {
         this.addJar(file.toURI().toURL(), true);
      } catch (MalformedURLException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void addJar(URL url, boolean useCaches) {
      try {
         URLConnection uc = url.openConnection();
         if (uc instanceof JarURLConnection) {
            JarURLConnection juc = (JarURLConnection)uc;
            juc.setUseCaches(useCaches);
            juc.getManifest();
            this.cachedMap.put(url, juc);
         }
      } catch (Throwable var5) {
         System.err.println("Failed to cache plugin JAR file: " + url.toExternalForm());
      }

      this.addURL(url);
   }

   public void removeJar(URL url) {
      JarURLConnection jarURL = (JarURLConnection)this.cachedMap.get(url);

      try {
         if (jarURL != null) {
            jarURL.getJarFile().close();
            this.cachedMap.remove(url);
         }
      } catch (Throwable var4) {
         System.err.println("Failed to unload JAR file\n" + var4);
      }

   }

   public void removeJar(File file) {
      try {
         this.removeJar(file.toURI().toURL());
      } catch (MalformedURLException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void close() throws IOException {
      super.close();
      Iterator var1 = this.cachedMap.values().iterator();

      while(var1.hasNext()) {
         JarURLConnection jarURL = (JarURLConnection)var1.next();
         jarURL.getJarFile().close();
      }

      this.cachedMap.clear();
   }

   public Class<?> loadClass(String clzName) throws ClassNotFoundException {
      return super.loadClass(clzName);
   }

   public static void bindingThread() {
      Thread.currentThread().setContextClassLoader(global());
   }

   public Enumeration<URL> getResources(String name) throws IOException {
      Enumeration<URL> urls = super.getResources(name);
      if (urls == null || !urls.hasMoreElements()) {
         urls = ClassLoader.getSystemResources(name);
      }

      return urls;
   }

   public URL getResource(String name) {
      URL url = super.getResource(name);
      if (url == null) {
         url = JarClassLoader.class.getResource(name);
      }

      return url;
   }

   static {
      JarClassLoader tmp = (JarClassLoader)Utils.newInstance("org.noear.solon.extend.impl.JarClassLoaderEx");
      if (tmp == null) {
         global = new JarClassLoader();
      } else {
         global = tmp;
      }

   }
}
