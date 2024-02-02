package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.table.Column;

public class AlterSequence extends SchemaOwnerCommand {
   private boolean ifExists;
   private Column column;
   private Boolean always;
   private String sequenceName;
   private Sequence sequence;
   private SequenceOptions options;

   public AlterSequence(SessionLocal var1, Schema var2) {
      super(var1, var2);
      this.transactional = true;
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public void setSequenceName(String var1) {
      this.sequenceName = var1;
   }

   public void setOptions(SequenceOptions var1) {
      this.options = var1;
   }

   public boolean isTransactional() {
      return true;
   }

   public void setColumn(Column var1, Boolean var2) {
      this.column = var1;
      this.always = var2;
      this.sequence = var1.getSequence();
      if (this.sequence == null && !this.ifExists) {
         throw DbException.get(90036, var1.getTraceSQL());
      }
   }

   long update(Schema var1) {
      if (this.sequence == null) {
         this.sequence = var1.findSequence(this.sequenceName);
         if (this.sequence == null) {
            if (!this.ifExists) {
               throw DbException.get(90036, this.sequenceName);
            }

            return 0L;
         }
      }

      if (this.column != null) {
         this.session.getUser().checkTableRight(this.column.getTable(), 32);
      }

      this.options.setDataType(this.sequence.getDataType());
      Long var2 = this.options.getStartValue(this.session);
      this.sequence.modify(this.options.getRestartValue(this.session, var2 != null ? var2 : this.sequence.getStartValue()), var2, this.options.getMinValue(this.sequence, this.session), this.options.getMaxValue(this.sequence, this.session), this.options.getIncrement(this.session), this.options.getCycle(), this.options.getCacheSize(this.session));
      this.sequence.flush(this.session);
      if (this.column != null && this.always != null) {
         this.column.setSequence(this.sequence, this.always);
         this.session.getDatabase().updateMeta(this.session, this.column.getTable());
      }

      return 0L;
   }

   public int getType() {
      return 54;
   }
}
