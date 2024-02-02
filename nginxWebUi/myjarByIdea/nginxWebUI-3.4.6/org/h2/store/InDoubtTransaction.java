package org.h2.store;

import org.h2.message.DbException;

public interface InDoubtTransaction {
   int IN_DOUBT = 0;
   int COMMIT = 1;
   int ROLLBACK = 2;

   void setState(int var1);

   int getState();

   default String getStateDescription() {
      int var1 = this.getState();
      switch (var1) {
         case 0:
            return "IN_DOUBT";
         case 1:
            return "COMMIT";
         case 2:
            return "ROLLBACK";
         default:
            throw DbException.getInternalError("state=" + var1);
      }
   }

   String getTransactionName();
}
