package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.TriggerObject;
import org.h2.table.Table;

public class CreateTrigger extends SchemaCommand {
   private String triggerName;
   private boolean ifNotExists;
   private boolean insteadOf;
   private boolean before;
   private int typeMask;
   private boolean rowBased;
   private int queueSize = 1024;
   private boolean noWait;
   private String tableName;
   private String triggerClassName;
   private String triggerSource;
   private boolean force;
   private boolean onRollback;

   public CreateTrigger(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setInsteadOf(boolean var1) {
      this.insteadOf = var1;
   }

   public void setBefore(boolean var1) {
      this.before = var1;
   }

   public void setTriggerClassName(String var1) {
      this.triggerClassName = var1;
   }

   public void setTriggerSource(String var1) {
      this.triggerSource = var1;
   }

   public void setTypeMask(int var1) {
      this.typeMask = var1;
   }

   public void setRowBased(boolean var1) {
      this.rowBased = var1;
   }

   public void setQueueSize(int var1) {
      this.queueSize = var1;
   }

   public void setNoWait(boolean var1) {
      this.noWait = var1;
   }

   public void setTableName(String var1) {
      this.tableName = var1;
   }

   public void setTriggerName(String var1) {
      this.triggerName = var1;
   }

   public void setIfNotExists(boolean var1) {
      this.ifNotExists = var1;
   }

   public long update() {
      this.session.getUser().checkAdmin();
      Database var1 = this.session.getDatabase();
      if (this.getSchema().findTrigger(this.triggerName) != null) {
         if (this.ifNotExists) {
            return 0L;
         } else {
            throw DbException.get(90041, this.triggerName);
         }
      } else {
         if ((this.typeMask & 8) != 0) {
            if (this.rowBased) {
               throw DbException.get(90005, "SELECT + FOR EACH ROW");
            }

            if (this.onRollback) {
               throw DbException.get(90005, "SELECT + ROLLBACK");
            }
         } else if ((this.typeMask & 7) == 0) {
            if (this.onRollback) {
               throw DbException.get(90005, "(!INSERT & !UPDATE & !DELETE) + ROLLBACK");
            }

            throw DbException.getInternalError();
         }

         int var2 = this.getObjectId();
         Table var3 = this.getSchema().getTableOrView(this.session, this.tableName);
         TriggerObject var4 = new TriggerObject(this.getSchema(), var2, this.triggerName, var3);
         var4.setInsteadOf(this.insteadOf);
         var4.setBefore(this.before);
         var4.setNoWait(this.noWait);
         var4.setQueueSize(this.queueSize);
         var4.setRowBased(this.rowBased);
         var4.setTypeMask(this.typeMask);
         var4.setOnRollback(this.onRollback);
         if (this.triggerClassName != null) {
            var4.setTriggerClassName(this.triggerClassName, this.force);
         } else {
            var4.setTriggerSource(this.triggerSource, this.force);
         }

         var1.addSchemaObject(this.session, var4);
         var3.addTrigger(var4);
         return 0L;
      }
   }

   public void setForce(boolean var1) {
      this.force = var1;
   }

   public void setOnRollback(boolean var1) {
      this.onRollback = var1;
   }

   public int getType() {
      return 31;
   }
}
