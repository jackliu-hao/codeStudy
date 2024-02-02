package org.h2.expression;

import java.util.Iterator;
import java.util.Map;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.util.ParserUtil;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

public final class FieldReference extends Operation1 {
   private final String fieldName;
   private int ordinal;

   public FieldReference(Expression var1, String var2) {
      super(var1);
      this.fieldName = var2;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return ParserUtil.quoteIdentifier(this.arg.getEnclosedSQL(var1, var2).append('.'), this.fieldName, var2);
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.arg.getValue(var1);
      return (Value)(var2 != ValueNull.INSTANCE ? ((ValueRow)var2).getList()[this.ordinal] : ValueNull.INSTANCE);
   }

   public Expression optimize(SessionLocal var1) {
      this.arg = this.arg.optimize(var1);
      TypeInfo var2 = this.arg.getType();
      if (var2.getValueType() != 41) {
         throw DbException.getInvalidExpressionTypeException("ROW", this.arg);
      } else {
         int var3 = 0;

         for(Iterator var4 = ((ExtTypeInfoRow)var2.getExtTypeInfo()).getFields().iterator(); var4.hasNext(); ++var3) {
            Map.Entry var5 = (Map.Entry)var4.next();
            if (this.fieldName.equals(var5.getKey())) {
               var2 = (TypeInfo)var5.getValue();
               this.type = var2;
               this.ordinal = var3;
               if (this.arg.isConstant()) {
                  return TypedValueExpression.get(this.getValue(var1), var2);
               }

               return this;
            }
         }

         throw DbException.get(42122, (String)this.fieldName);
      }
   }
}
