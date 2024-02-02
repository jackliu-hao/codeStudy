package org.h2.expression;

import java.util.Objects;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public class TypedValueExpression extends ValueExpression {
   public static final TypedValueExpression UNKNOWN;
   private final TypeInfo type;

   public static ValueExpression get(Value var0, TypeInfo var1) {
      return getImpl(var0, var1, true);
   }

   public static ValueExpression getTypedIfNull(Value var0, TypeInfo var1) {
      return getImpl(var0, var1, false);
   }

   private static ValueExpression getImpl(Value var0, TypeInfo var1, boolean var2) {
      if (var0 == ValueNull.INSTANCE) {
         switch (var1.getValueType()) {
            case 0:
               return ValueExpression.NULL;
            case 8:
               return UNKNOWN;
            default:
               return new TypedValueExpression(var0, var1);
         }
      } else {
         if (var2) {
            DataType var3 = DataType.getDataType(var1.getValueType());
            TypeInfo var4 = var0.getType();
            if (var3.supportsPrecision && var1.getPrecision() != var4.getPrecision() || var3.supportsScale && var1.getScale() != var4.getScale() || !Objects.equals(var1.getExtTypeInfo(), var4.getExtTypeInfo())) {
               return new TypedValueExpression(var0, var1);
            }
         }

         return ValueExpression.get(var0);
      }
   }

   private TypedValueExpression(Value var1, TypeInfo var2) {
      super(var1);
      this.type = var2;
   }

   public TypeInfo getType() {
      return this.type;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      if (this == UNKNOWN) {
         var1.append("UNKNOWN");
      } else {
         this.value.getSQL(var1.append("CAST("), var2 | 4).append(" AS ");
         this.type.getSQL(var1, var2).append(')');
      }

      return var1;
   }

   public boolean isNullConstant() {
      return this.value == ValueNull.INSTANCE;
   }

   static {
      UNKNOWN = new TypedValueExpression(ValueNull.INSTANCE, TypeInfo.TYPE_BOOLEAN);
   }
}
