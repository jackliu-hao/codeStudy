package cn.hutool.core.io.resource;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.function.Supplier;

public class UrlResource implements Resource, Serializable {
   private static final long serialVersionUID = 1L;
   protected URL url;
   private long lastModified;
   protected String name;

   public UrlResource(URI uri) {
      this(URLUtil.url(uri), (String)null);
   }

   public UrlResource(URL url) {
      this(url, (String)null);
   }

   public UrlResource(URL url, String name) {
      this.lastModified = 0L;
      this.url = url;
      if (null != url && "file".equals(url.getProtocol())) {
         this.lastModified = FileUtil.file(url).lastModified();
      }

      this.name = (String)ObjectUtil.defaultIfNull(name, (Supplier)(() -> {
         return null != url ? FileUtil.getName(url.getPath()) : null;
      }));
   }

   /** @deprecated */
   @Deprecated
   public UrlResource(File file) {
      this.lastModified = 0L;
      this.url = URLUtil.getURL(file);
   }

   public String getName() {
      return this.name;
   }

   public URL getUrl() {
      return this.url;
   }

   public InputStream getStream() throws NoResourceException {
      if (null == this.url) {
         throw new NoResourceException("Resource URL is null!");
      } else {
         return URLUtil.getStream(this.url);
      }
   }

   public boolean isModified() {
      return 0L != this.lastModified && this.lastModified != this.getFile().lastModified();
   }

   public File getFile() {
      return FileUtil.file(this.url);
   }

   public String toString() {
      return null == this.url ? "null" : this.url.toString();
   }
}
