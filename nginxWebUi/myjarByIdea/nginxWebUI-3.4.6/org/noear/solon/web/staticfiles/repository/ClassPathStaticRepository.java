package org.noear.solon.web.staticfiles.repository;

import java.io.File;
import java.net.URI;
import java.net.URL;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.web.staticfiles.StaticRepository;

public class ClassPathStaticRepository implements StaticRepository {
   String location;
   String locationDebug;
   ClassLoader classLoader;

   public ClassPathStaticRepository(String location) {
      this(JarClassLoader.global(), location);
   }

   public ClassPathStaticRepository(ClassLoader classLoader, String location) {
      this.classLoader = classLoader;
      this.setLocation(location);
   }

   protected void setLocation(String location) {
      if (location != null) {
         if (location.endsWith("/")) {
            location = location.substring(0, location.length() - 1);
         }

         if (location.startsWith("/")) {
            location = location.substring(1);
         }

         this.location = location;
         if (Solon.cfg().isDebugMode()) {
            URL rooturi = Utils.getResource(this.classLoader, "/");
            if (rooturi != null) {
               String rootdir = rooturi.toString().replace("target/classes/", "");
               if (rootdir.startsWith("file:")) {
                  this.locationDebug = rootdir + "src/main/resources/" + location;
               }
            }
         }

      }
   }

   public URL find(String path) throws Exception {
      if (this.locationDebug != null) {
         URI uri = URI.create(this.locationDebug + path);
         File file = new File(uri);
         if (file.exists()) {
            return uri.toURL();
         }
      }

      return Utils.getResource(this.classLoader, this.location + path);
   }
}
