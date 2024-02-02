package org.h2.engine;

import org.h2.command.Prepared;

public class Procedure {
   private final String name;
   private final Prepared prepared;

   public Procedure(String var1, Prepared var2) {
      this.name = var1;
      this.prepared = var2;
   }

   public String getName() {
      return this.name;
   }

   public Prepared getPrepared() {
      return this.prepared;
   }
}
