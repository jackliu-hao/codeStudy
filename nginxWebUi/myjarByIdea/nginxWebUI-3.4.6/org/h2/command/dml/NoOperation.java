package org.h2.command.dml;

import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.result.ResultInterface;

public class NoOperation extends Prepared {
   public NoOperation(SessionLocal var1) {
      super(var1);
   }

   public long update() {
      return 0L;
   }

   public boolean isTransactional() {
      return true;
   }

   public boolean needRecompile() {
      return false;
   }

   public boolean isReadOnly() {
      return true;
   }

   public ResultInterface queryMeta() {
      return null;
   }

   public int getType() {
      return 63;
   }
}
