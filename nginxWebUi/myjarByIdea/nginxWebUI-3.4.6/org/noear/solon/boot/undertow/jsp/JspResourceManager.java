package org.noear.solon.boot.undertow.jsp;

import io.undertow.UndertowMessages;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.handlers.resource.URLResource;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import org.noear.solon.core.handle.Context;

public class JspResourceManager implements ResourceManager {
   private final ClassLoader classLoader;
   private final String prefix;

   public JspResourceManager(ClassLoader classLoader, String prefix) {
      this.classLoader = classLoader;
      if (prefix.isEmpty()) {
         this.prefix = "";
      } else if (prefix.endsWith("/")) {
         this.prefix = prefix;
      } else {
         this.prefix = prefix + "/";
      }

   }

   public Resource getResource(String path) throws IOException {
      if (path != null && path.endsWith(".jsp")) {
         if (Context.current() == null) {
            return null;
         } else {
            String modPath = path;
            if (path.startsWith("/")) {
               modPath = path.substring(1);
            }

            String realPath = this.prefix + modPath;
            URL resource = null;
            if (realPath.startsWith("file:")) {
               resource = URI.create(realPath).toURL();
            } else {
               resource = this.classLoader.getResource(realPath);
            }

            return resource == null ? null : new URLResource(resource, path);
         }
      } else {
         return null;
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
