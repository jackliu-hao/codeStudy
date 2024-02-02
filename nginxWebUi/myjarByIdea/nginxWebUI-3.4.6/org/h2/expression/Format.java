package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueJson;

public final class Format extends Operation1 {
   private final FormatEnum format;

   public Format(Expression var1, FormatEnum var2) {
      super(var1);
      this.format = var2;
   }

   public Value getValue(SessionLocal var1) {
      return this.getValue(this.arg.getValue(var1));
   }

   public Value getValue(Value var1) {
      switch (var1.getValueType()) {
         case 0:
            return ValueJson.NULL;
         case 1:
         case 2:
         case 3:
         case 4:
            return ValueJson.fromJson(var1.getString());
         default:
            return var1.convertTo(TypeInfo.TYPE_JSON);
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.arg = this.arg.optimize(var1);
      if (this.arg.isConstant()) {
         return ValueExpression.get(this.getValue(var1));
      } else if (this.arg instanceof Format && this.format == ((Format)this.arg).format) {
         return this.arg;
      } else {
         this.type = TypeInfo.TYPE_JSON;
         return this;
      }
   }

   public boolean isIdentity() {
      return this.arg.isIdentity();
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return this.arg.getSQL(var1, var2, 0).append(" FORMAT ").append(this.format.name());
   }

   public int getNullable() {
      return this.arg.getNullable();
   }

   public String getTableName() {
      return this.arg.getTableName();
   }

   public String getColumnName(SessionLocal var1, int var2) {
      return this.arg.getColumnName(var1, var2);
   }

   public static enum FormatEnum {
      JSON;
   }
}
