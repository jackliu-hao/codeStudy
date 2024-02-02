package org.h2.command.ddl;

import org.h2.engine.SessionLocal;

public class DeallocateProcedure extends DefineCommand {
   private String procedureName;

   public DeallocateProcedure(SessionLocal var1) {
      super(var1);
   }

   public long update() {
      this.session.removeProcedure(this.procedureName);
      return 0L;
   }

   public void setProcedureName(String var1) {
      this.procedureName = var1;
   }

   public int getType() {
      return 35;
   }
}
