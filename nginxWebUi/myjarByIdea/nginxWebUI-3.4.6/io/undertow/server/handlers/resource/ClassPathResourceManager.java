package io.undertow.server.handlers.resource;

import io.undertow.UndertowMessages;
import java.io.IOException;
import java.net.URL;

public class ClassPathResourceManager implements ResourceManager {
   private final ClassLoader classLoader;
   private final String prefix;

   public ClassPathResourceManager(ClassLoader loader, Package p) {
      this(loader, p.getName().replace(".", "/"));
   }

   public ClassPathResourceManager(ClassLoader classLoader, String prefix) {
      this.classLoader = classLoader;
      if (prefix.isEmpty()) {
         this.prefix = "";
      } else if (prefix.endsWith("/")) {
         this.prefix = prefix;
      } else {
         this.prefix = prefix + "/";
      }

   }

   public ClassPathResourceManager(ClassLoader classLoader) {
      this(classLoader, "");
   }

   public Resource getResource(String path) throws IOException {
      if (path == null) {
         return null;
      } else {
         String modPath = path;
         if (path.startsWith("/")) {
            modPath = path.substring(1);
         }

         String realPath = this.prefix + modPath;
         URL resource = this.classLoader.getResource(realPath);
         return resource == null ? null : new URLResource(resource, path);
      }
   }

   public boolean isResourceChangeListenerSupported() {
      return false;
   }

   public void registerResourceChangeListener(ResourceChangeListener listener) {
      throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
   }

   public void removeResourceChangeListener(ResourceChangeListener listener) {
      throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
   }

   public void close() throws IOException {
   }
}
