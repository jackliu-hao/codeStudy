package org.xnio;

import java.io.Closeable;

public final class Closer implements Runnable {
   private final Closeable resource;

   public Closer(Closeable resource) {
      this.resource = resource;
   }

   public void run() {
      IoUtils.safeClose(this.resource);
   }
}
