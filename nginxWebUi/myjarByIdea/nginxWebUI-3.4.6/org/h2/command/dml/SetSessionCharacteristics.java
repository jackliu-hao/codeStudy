package org.h2.command.dml;

import org.h2.command.Prepared;
import org.h2.engine.IsolationLevel;
import org.h2.engine.SessionLocal;
import org.h2.result.ResultInterface;

public class SetSessionCharacteristics extends Prepared {
   private final IsolationLevel isolationLevel;

   public SetSessionCharacteristics(SessionLocal var1, IsolationLevel var2) {
      super(var1);
      this.isolationLevel = var2;
   }

   public boolean isTransactional() {
      return false;
   }

   public long update() {
      this.session.setIsolationLevel(this.isolationLevel);
      return 0L;
   }

   public boolean needRecompile() {
      return false;
   }

   public ResultInterface queryMeta() {
      return null;
   }

   public int getType() {
      return 67;
   }
}
