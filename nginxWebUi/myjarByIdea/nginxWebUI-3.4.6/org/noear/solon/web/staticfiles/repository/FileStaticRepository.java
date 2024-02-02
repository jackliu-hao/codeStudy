package org.noear.solon.web.staticfiles.repository;

import java.io.File;
import java.net.URL;
import org.noear.solon.web.staticfiles.StaticRepository;

public class FileStaticRepository implements StaticRepository {
   String location;

   public FileStaticRepository(String location) {
      this.setLocation(location);
   }

   protected void setLocation(String location) {
      if (location != null) {
         this.location = location;
      }
   }

   public URL find(String path) throws Exception {
      File file = new File(this.location, path);
      return file.exists() ? file.toURI().toURL() : null;
   }
}
