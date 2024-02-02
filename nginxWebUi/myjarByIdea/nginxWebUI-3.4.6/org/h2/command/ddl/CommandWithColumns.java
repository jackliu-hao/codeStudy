package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.table.Column;
import org.h2.table.IndexColumn;

public abstract class CommandWithColumns extends SchemaCommand {
   private ArrayList<DefineCommand> constraintCommands;
   private AlterTableAddConstraint primaryKey;

   protected CommandWithColumns(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public abstract void addColumn(Column var1);

   public void addConstraintCommand(DefineCommand var1) {
      if (!(var1 instanceof CreateIndex)) {
         AlterTableAddConstraint var2 = (AlterTableAddConstraint)var1;
         if (var2.getType() == 6 && this.setPrimaryKey(var2)) {
            return;
         }
      }

      this.getConstraintCommands().add(var1);
   }

   protected void changePrimaryKeysToNotNull(ArrayList<Column> var1) {
      if (this.primaryKey != null) {
         IndexColumn[] var2 = this.primaryKey.getIndexColumns();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Column var4 = (Column)var3.next();
            IndexColumn[] var5 = var2;
            int var6 = var2.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               IndexColumn var8 = var5[var7];
               if (var4.getName().equals(var8.columnName)) {
                  var4.setNullable(false);
               }
            }
         }
      }

   }

   protected void createConstraints() {
      if (this.constraintCommands != null) {
         Iterator var1 = this.constraintCommands.iterator();

         while(var1.hasNext()) {
            DefineCommand var2 = (DefineCommand)var1.next();
            var2.setTransactional(this.transactional);
            var2.update();
         }
      }

   }

   protected ArrayList<Sequence> generateSequences(ArrayList<Column> var1, boolean var2) {
      ArrayList var3 = new ArrayList(var1 == null ? 0 : var1.size());
      if (var1 != null) {
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            Column var5 = (Column)var4.next();
            if (var5.hasIdentityOptions()) {
               int var6 = this.session.getDatabase().allocateObjectId();
               var5.initializeSequence(this.session, this.getSchema(), var6, var2);
               if (!"''".equals(this.session.getDatabase().getCluster())) {
                  throw DbException.getUnsupportedException("CLUSTERING && identity columns");
               }
            }

            Sequence var7 = var5.getSequence();
            if (var7 != null) {
               var3.add(var7);
            }
         }
      }

      return var3;
   }

   private ArrayList<DefineCommand> getConstraintCommands() {
      if (this.constraintCommands == null) {
         this.constraintCommands = new ArrayList();
      }

      return this.constraintCommands;
   }

   private boolean setPrimaryKey(AlterTableAddConstraint var1) {
      if (this.primaryKey != null) {
         IndexColumn[] var2 = this.primaryKey.getIndexColumns();
         IndexColumn[] var3 = var1.getIndexColumns();
         int var4 = var3.length;
         if (var4 != var2.length) {
            throw DbException.get(90017);
         }

         for(int var5 = 0; var5 < var4; ++var5) {
            if (!var3[var5].columnName.equals(var2[var5].columnName)) {
               throw DbException.get(90017);
            }
         }

         if (this.primaryKey.getConstraintName() != null) {
            return true;
         }

         this.constraintCommands.remove(this.primaryKey);
      }

      this.primaryKey = var1;
      return false;
   }

   public AlterTableAddConstraint getPrimaryKey() {
      return this.primaryKey;
   }
}
