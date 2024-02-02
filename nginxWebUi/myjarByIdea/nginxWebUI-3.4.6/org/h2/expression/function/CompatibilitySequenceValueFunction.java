package org.h2.expression.function;

import org.h2.command.Parser;
import org.h2.command.Prepared;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public final class CompatibilitySequenceValueFunction extends Function1_2 {
   private final boolean current;

   public CompatibilitySequenceValueFunction(Expression var1, Expression var2, boolean var3) {
      super(var1, var2);
      this.current = var3;
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3) {
      String var4;
      String var5;
      if (var3 == null) {
         Parser var6 = new Parser(var1);
         String var7 = var2.getString();
         Expression var8 = var6.parseExpression(var7);
         if (!(var8 instanceof ExpressionColumn)) {
            throw DbException.getSyntaxError(var7, 1);
         }

         ExpressionColumn var9 = (ExpressionColumn)var8;
         var4 = var9.getOriginalTableAliasName();
         if (var4 == null) {
            var4 = var1.getCurrentSchemaName();
            var5 = var7;
         } else {
            var5 = var9.getColumnName(var1, -1);
         }
      } else {
         var4 = var2.getString();
         var5 = var3.getString();
      }

      Database var10 = var1.getDatabase();
      Schema var11 = var10.findSchema(var4);
      if (var11 == null) {
         var4 = StringUtils.toUpperEnglish(var4);
         var11 = var10.getSchema(var4);
      }

      Sequence var12 = var11.findSequence(var5);
      if (var12 == null) {
         var5 = StringUtils.toUpperEnglish(var5);
         var12 = var11.getSequence(var5);
      }

      return (this.current ? var1.getCurrentValueFor(var12) : var1.getNextValueFor(var12, (Prepared)null)).convertTo(this.type);
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      if (this.right != null) {
         this.right = this.right.optimize(var1);
      }

      this.type = var1.getMode().decimalSequences ? TypeInfo.TYPE_NUMERIC_BIGINT : TypeInfo.TYPE_BIGINT;
      return this;
   }

   public boolean isEverything(ExpressionVisitor var1) {
      switch (var1.getType()) {
         case 0:
         case 2:
         case 8:
            return false;
         case 5:
            if (!this.current) {
               return false;
            }
         case 1:
         case 3:
         case 4:
         case 6:
         case 7:
         default:
            return super.isEverything(var1);
      }
   }

   public String getName() {
      return this.current ? "CURRVAL" : "NEXTVAL";
   }
}
