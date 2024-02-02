package org.noear.solon.core;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;

public class ResourceScanner {
   public Set<String> scan(ClassLoader classLoader, String path, Predicate<String> filter) {
      Set<String> urls = new LinkedHashSet();
      if (classLoader == null) {
         return urls;
      } else {
         try {
            Enumeration<URL> roots = Utils.getResources(classLoader, path);

            while(roots.hasMoreElements()) {
               this.scanDo((URL)roots.nextElement(), path, filter, urls);
            }
         } catch (IOException var6) {
            EventBus.push(var6);
         }

         return urls;
      }
   }

   protected void scanDo(URL url, String path, Predicate<String> filter, Set<String> urls) throws IOException {
      if ("file".equals(url.getProtocol())) {
         String fp = URLDecoder.decode(url.getFile(), Solon.encoding());
         this.doScanByFile(new File(fp), path, filter, urls);
      } else if ("jar".equals(url.getProtocol())) {
         JarFile jar = ((JarURLConnection)url.openConnection()).getJarFile();
         this.doScanByJar(jar, path, filter, urls);
      }

   }

   protected void doScanByFile(File dir, String path, Predicate<String> filter, Set<String> urls) {
      if (dir.exists() && dir.isDirectory()) {
         File[] dirfiles = dir.listFiles((fx) -> {
            return fx.isDirectory() || filter.test(fx.getName());
         });
         if (dirfiles != null) {
            File[] var6 = dirfiles;
            int var7 = dirfiles.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               File f = var6[var8];
               String p2 = path + "/" + f.getName();
               if (f.isDirectory()) {
                  this.doScanByFile(f, p2, filter, urls);
               } else if (p2.startsWith("/")) {
                  urls.add(p2.substring(1));
               } else {
                  urls.add(p2);
               }
            }
         }

      }
   }

   protected void doScanByJar(JarFile jar, String path, Predicate<String> filter, Set<String> urls) {
      Enumeration<JarEntry> entry = jar.entries();

      while(entry.hasMoreElements()) {
         JarEntry e = (JarEntry)entry.nextElement();
         String n = e.getName();
         if (n.charAt(0) == '/') {
            n = n.substring(1);
         }

         if (!e.isDirectory() && n.startsWith(path) && filter.test(n)) {
            if (n.startsWith("/")) {
               urls.add(n.substring(1));
            } else {
               urls.add(n);
            }
         }
      }

   }
}
