package io.undertow.server.handlers.resource;

import io.undertow.UndertowMessages;
import java.io.Closeable;
import java.io.IOException;

public interface ResourceManager extends Closeable {
   ResourceManager EMPTY_RESOURCE_MANAGER = new ResourceManager() {
      public Resource getResource(String path) {
         return null;
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
   };

   Resource getResource(String var1) throws IOException;

   boolean isResourceChangeListenerSupported();

   void registerResourceChangeListener(ResourceChangeListener var1);

   void removeResourceChangeListener(ResourceChangeListener var1);
}
