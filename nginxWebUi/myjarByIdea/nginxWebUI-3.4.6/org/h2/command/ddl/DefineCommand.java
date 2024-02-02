package org.h2.command.ddl;

import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.result.ResultInterface;

public abstract class DefineCommand extends Prepared {
   protected boolean transactional;

   DefineCommand(SessionLocal var1) {
      super(var1);
   }

   public boolean isReadOnly() {
      return false;
   }

   public ResultInterface queryMeta() {
      return null;
   }

   public void setTransactional(boolean var1) {
      this.transactional = var1;
   }

   public boolean isTransactional() {
      return this.transactional;
   }
}
