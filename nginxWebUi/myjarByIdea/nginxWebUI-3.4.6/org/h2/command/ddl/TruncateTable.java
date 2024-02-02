package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Sequence;
import org.h2.table.Column;
import org.h2.table.Table;

public class TruncateTable extends DefineCommand {
   private Table table;
   private boolean restart;

   public TruncateTable(SessionLocal var1) {
      super(var1);
   }

   public void setTable(Table var1) {
      this.table = var1;
   }

   public void setRestart(boolean var1) {
      this.restart = var1;
   }

   public long update() {
      if (!this.table.canTruncate()) {
         throw DbException.get(90106, this.table.getTraceSQL());
      } else {
         this.session.getUser().checkTableRight(this.table, 2);
         this.table.lock(this.session, 2);
         long var1 = this.table.truncate(this.session);
         if (this.restart) {
            Column[] var3 = this.table.getColumns();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Column var6 = var3[var5];
               Sequence var7 = var6.getSequence();
               if (var7 != null) {
                  var7.modify(var7.getStartValue(), (Long)null, (Long)null, (Long)null, (Long)null, (Sequence.Cycle)null, (Long)null);
                  this.session.getDatabase().updateMeta(this.session, var7);
               }
            }
         }

         return var1;
      }
   }

   public int getType() {
      return 53;
   }
}
