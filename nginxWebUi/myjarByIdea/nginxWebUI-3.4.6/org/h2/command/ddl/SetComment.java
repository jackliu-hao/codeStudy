package org.h2.command.ddl;

import org.h2.engine.Comment;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

public class SetComment extends DefineCommand {
   private String schemaName;
   private String objectName;
   private boolean column;
   private String columnName;
   private int objectType;
   private Expression expr;

   public SetComment(SessionLocal var1) {
      super(var1);
   }

   public long update() {
      Database var1 = this.session.getDatabase();
      Object var2 = null;
      int var3 = 50000;
      if (this.schemaName == null) {
         this.schemaName = this.session.getCurrentSchemaName();
      }

      Schema var4;
      switch (this.objectType) {
         case 0:
            var4 = var1.getSchema(this.schemaName);
            this.session.getUser().checkSchemaOwner(var4);
            var2 = var4.getTableOrView(this.session, this.objectName);
            break;
         case 1:
            var4 = var1.getSchema(this.schemaName);
            this.session.getUser().checkSchemaOwner(var4);
            var2 = var4.getIndex(this.objectName);
            break;
         case 2:
            this.session.getUser().checkAdmin();
            this.schemaName = null;
            var2 = var1.getUser(this.objectName);
            break;
         case 3:
            var4 = var1.getSchema(this.schemaName);
            this.session.getUser().checkSchemaOwner(var4);
            var2 = var4.getSequence(this.objectName);
            break;
         case 4:
            var4 = var1.getSchema(this.schemaName);
            this.session.getUser().checkSchemaOwner(var4);
            var2 = var4.findTrigger(this.objectName);
            var3 = 90042;
            break;
         case 5:
            var4 = var1.getSchema(this.schemaName);
            this.session.getUser().checkSchemaOwner(var4);
            var2 = var4.getConstraint(this.objectName);
         case 6:
         case 8:
         default:
            break;
         case 7:
            this.session.getUser().checkAdmin();
            this.schemaName = null;
            var2 = var1.findRole(this.objectName);
            var3 = 90070;
            break;
         case 9:
            var4 = var1.getSchema(this.schemaName);
            this.session.getUser().checkSchemaOwner(var4);
            var2 = var4.findFunction(this.objectName);
            var3 = 90077;
            break;
         case 10:
            this.schemaName = null;
            var4 = var1.getSchema(this.objectName);
            this.session.getUser().checkSchemaOwner(var4);
            var2 = var4;
            break;
         case 11:
            var4 = var1.getSchema(this.schemaName);
            this.session.getUser().checkSchemaOwner(var4);
            var2 = var4.getConstant(this.objectName);
            break;
         case 12:
            var4 = var1.getSchema(this.schemaName);
            this.session.getUser().checkSchemaOwner(var4);
            var2 = var4.findDomain(this.objectName);
            var3 = 90120;
      }

      if (var2 == null) {
         throw DbException.get(var3, this.objectName);
      } else {
         String var8 = this.expr.optimize(this.session).getValue(this.session).getString();
         if (var8 != null && var8.isEmpty()) {
            var8 = null;
         }

         if (this.column) {
            Table var5 = (Table)var2;
            var5.getColumn(this.columnName).setComment(var8);
         } else {
            ((DbObject)var2).setComment(var8);
         }

         if (!this.column && this.objectType != 0 && this.objectType != 2 && this.objectType != 1 && this.objectType != 5) {
            Comment var7 = var1.findComment((DbObject)var2);
            if (var7 == null) {
               if (var8 != null) {
                  int var6 = this.getObjectId();
                  var7 = new Comment(var1, var6, (DbObject)var2);
                  var7.setCommentText(var8);
                  var1.addDatabaseObject(this.session, var7);
               }
            } else if (var8 == null) {
               var1.removeDatabaseObject(this.session, var7);
            } else {
               var7.setCommentText(var8);
               var1.updateMeta(this.session, var7);
            }
         } else {
            var1.updateMeta(this.session, (DbObject)var2);
         }

         return 0L;
      }
   }

   public void setCommentExpression(Expression var1) {
      this.expr = var1;
   }

   public void setObjectName(String var1) {
      this.objectName = var1;
   }

   public void setObjectType(int var1) {
      this.objectType = var1;
   }

   public void setColumnName(String var1) {
      this.columnName = var1;
   }

   public void setSchemaName(String var1) {
      this.schemaName = var1;
   }

   public void setColumn(boolean var1) {
      this.column = var1;
   }

   public int getType() {
      return 52;
   }
}
