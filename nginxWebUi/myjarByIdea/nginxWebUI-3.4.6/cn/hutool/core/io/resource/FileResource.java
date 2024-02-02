package cn.hutool.core.io.resource;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;
import java.util.function.Supplier;

public class FileResource implements Resource, Serializable {
   private static final long serialVersionUID = 1L;
   private final File file;
   private final long lastModified;
   private final String name;

   public FileResource(String path) {
      this(FileUtil.file(path));
   }

   public FileResource(Path path) {
      this(path.toFile());
   }

   public FileResource(File file) {
      this(file, (String)null);
   }

   public FileResource(File file, String fileName) {
      Assert.notNull(file, "File must be not null !");
      this.file = file;
      this.lastModified = file.lastModified();
      file.getClass();
      this.name = (String)ObjectUtil.defaultIfNull(fileName, (Supplier)(file::getName));
   }

   public String getName() {
      return this.name;
   }

   public URL getUrl() {
      return URLUtil.getURL(this.file);
   }

   public InputStream getStream() throws NoResourceException {
      return FileUtil.getInputStream(this.file);
   }

   public File getFile() {
      return this.file;
   }

   public boolean isModified() {
      return this.lastModified != this.file.lastModified();
   }

   public String toString() {
      return this.file.toString();
   }
}
