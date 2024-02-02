package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.schema.Constant;
import org.h2.schema.Schema;
import org.h2.value.Value;

public class CreateConstant extends SchemaOwnerCommand {
   private String constantName;
   private Expression expression;
   private boolean ifNotExists;

   public CreateConstant(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setIfNotExists(boolean var1) {
      this.ifNotExists = var1;
   }

   long update(Schema var1) {
      Database var2 = this.session.getDatabase();
      if (var1.findConstant(this.constantName) != null) {
         if (this.ifNotExists) {
            return 0L;
         } else {
            throw DbException.get(90114, this.constantName);
         }
      } else {
         int var3 = this.getObjectId();
         Constant var4 = new Constant(var1, var3, this.constantName);
         this.expression = this.expression.optimize(this.session);
         Value var5 = this.expression.getValue(this.session);
         var4.setValue(var5);
         var2.addSchemaObject(this.session, var4);
         return 0L;
      }
   }

   public void setConstantName(String var1) {
      this.constantName = var1;
   }

   public void setExpression(Expression var1) {
      this.expression = var1;
   }

   public int getType() {
      return 23;
   }
}
