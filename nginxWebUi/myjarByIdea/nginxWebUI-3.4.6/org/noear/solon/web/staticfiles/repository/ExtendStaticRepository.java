package org.noear.solon.web.staticfiles.repository;

import java.io.File;
import java.net.URL;
import org.noear.solon.core.ExtendLoader;
import org.noear.solon.web.staticfiles.StaticRepository;

public class ExtendStaticRepository implements StaticRepository {
   String location;

   public ExtendStaticRepository() {
      String path = ExtendLoader.path();
      if (path == null) {
         throw new RuntimeException("No extension directory exists");
      } else {
         this.location = path + "static";
      }
   }

   public URL find(String path) throws Exception {
      File file = new File(this.location, path);
      return file.exists() ? file.toURI().toURL() : null;
   }
}
