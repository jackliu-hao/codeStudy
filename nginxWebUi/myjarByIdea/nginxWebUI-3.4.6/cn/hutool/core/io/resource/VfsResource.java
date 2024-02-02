package cn.hutool.core.io.resource;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ReflectUtil;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;

public class VfsResource implements Resource {
   private static final String VFS3_PKG = "org.jboss.vfs.";
   private static final Method VIRTUAL_FILE_METHOD_EXISTS;
   private static final Method VIRTUAL_FILE_METHOD_GET_INPUT_STREAM;
   private static final Method VIRTUAL_FILE_METHOD_GET_SIZE;
   private static final Method VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED;
   private static final Method VIRTUAL_FILE_METHOD_TO_URL;
   private static final Method VIRTUAL_FILE_METHOD_GET_NAME;
   private final Object virtualFile;
   private final long lastModified;

   public VfsResource(Object resource) {
      Assert.notNull(resource, "VirtualFile must not be null");
      this.virtualFile = resource;
      this.lastModified = this.getLastModified();
   }

   public boolean exists() {
      return (Boolean)ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_EXISTS);
   }

   public String getName() {
      return (String)ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_GET_NAME);
   }

   public URL getUrl() {
      return (URL)ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_TO_URL);
   }

   public InputStream getStream() {
      return (InputStream)ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_GET_INPUT_STREAM);
   }

   public boolean isModified() {
      return this.lastModified != this.getLastModified();
   }

   public long getLastModified() {
      return (Long)ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED);
   }

   public long size() {
      return (Long)ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_GET_SIZE);
   }

   static {
      Class<?> virtualFile = ClassLoaderUtil.loadClass("org.jboss.vfs.VirtualFile");

      try {
         VIRTUAL_FILE_METHOD_EXISTS = virtualFile.getMethod("exists");
         VIRTUAL_FILE_METHOD_GET_INPUT_STREAM = virtualFile.getMethod("openStream");
         VIRTUAL_FILE_METHOD_GET_SIZE = virtualFile.getMethod("getSize");
         VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED = virtualFile.getMethod("getLastModified");
         VIRTUAL_FILE_METHOD_TO_URL = virtualFile.getMethod("toURL");
         VIRTUAL_FILE_METHOD_GET_NAME = virtualFile.getMethod("getName");
      } catch (NoSuchMethodException var2) {
         throw new IllegalStateException("Could not detect JBoss VFS infrastructure", var2);
      }
   }
}
