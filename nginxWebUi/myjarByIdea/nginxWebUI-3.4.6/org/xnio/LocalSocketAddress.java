package org.xnio;

import java.net.SocketAddress;
import org.xnio._private.Messages;

public final class LocalSocketAddress extends SocketAddress {
   private static final long serialVersionUID = -596342428809783686L;
   private final String name;

   public LocalSocketAddress(String name) {
      if (name == null) {
         throw Messages.msg.nullParameter("name");
      } else {
         this.name = name;
      }
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return this.getName();
   }
}
