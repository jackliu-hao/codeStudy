package org.xnio.nio;

import org.xnio.Xnio;
import org.xnio.XnioProvider;

public final class NioXnioProvider implements XnioProvider {
   private static final Xnio INSTANCE = new NioXnio();

   public Xnio getInstance() {
      return INSTANCE;
   }

   public String getName() {
      return INSTANCE.getName();
   }
}
