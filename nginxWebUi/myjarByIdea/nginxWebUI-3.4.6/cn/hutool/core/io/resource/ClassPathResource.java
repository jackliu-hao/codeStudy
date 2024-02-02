package cn.hutool.core.io.resource;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import java.net.URL;
import java.util.function.Supplier;

public class ClassPathResource extends UrlResource {
   private static final long serialVersionUID = 1L;
   private final String path;
   private final ClassLoader classLoader;
   private final Class<?> clazz;

   public ClassPathResource(String path) {
      this(path, (ClassLoader)null, (Class)null);
   }

   public ClassPathResource(String path, ClassLoader classLoader) {
      this(path, classLoader, (Class)null);
   }

   public ClassPathResource(String path, Class<?> clazz) {
      this(path, (ClassLoader)null, clazz);
   }

   public ClassPathResource(String pathBaseClassLoader, ClassLoader classLoader, Class<?> clazz) {
      super((URL)null);
      Assert.notNull(pathBaseClassLoader, "Path must not be null");
      String path = this.normalizePath(pathBaseClassLoader);
      this.path = path;
      this.name = StrUtil.isBlank(path) ? null : FileUtil.getName(path);
      this.classLoader = (ClassLoader)ObjectUtil.defaultIfNull(classLoader, (Supplier)(ClassUtil::getClassLoader));
      this.clazz = clazz;
      this.initUrl();
   }

   public final String getPath() {
      return this.path;
   }

   public final String getAbsolutePath() {
      return FileUtil.isAbsolutePath(this.path) ? this.path : FileUtil.normalize(URLUtil.getDecodedPath(this.url));
   }

   public final ClassLoader getClassLoader() {
      return this.classLoader;
   }

   private void initUrl() {
      if (null != this.clazz) {
         super.url = this.clazz.getResource(this.path);
      } else if (null != this.classLoader) {
         super.url = this.classLoader.getResource(this.path);
      } else {
         super.url = ClassLoader.getSystemResource(this.path);
      }

      if (null == super.url) {
         throw new NoResourceException("Resource of path [{}] not exist!", new Object[]{this.path});
      }
   }

   public String toString() {
      return null == this.path ? super.toString() : "classpath:" + this.path;
   }

   private String normalizePath(String path) {
      path = FileUtil.normalize(path);
      path = StrUtil.removePrefix(path, "/");
      Assert.isFalse(FileUtil.isAbsolutePath(path), "Path [{}] must be a relative path !", path);
      return path;
   }
}
