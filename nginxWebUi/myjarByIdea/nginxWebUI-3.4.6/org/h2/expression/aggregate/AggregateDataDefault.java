package org.h2.expression.aggregate;

import org.h2.engine.SessionLocal;
import org.h2.expression.function.BitFunction;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

final class AggregateDataDefault extends AggregateData {
   private final AggregateType aggregateType;
   private final TypeInfo dataType;
   private Value value;

   AggregateDataDefault(AggregateType var1, TypeInfo var2) {
      this.aggregateType = var1;
      this.dataType = var2;
   }

   void add(SessionLocal var1, Value var2) {
      if (var2 != ValueNull.INSTANCE) {
         ValueBoolean var3;
         switch (this.aggregateType) {
            case SUM:
               if (this.value == null) {
                  this.value = var2.convertTo(this.dataType.getValueType());
               } else {
                  var2 = var2.convertTo(this.value.getValueType());
                  this.value = this.value.add(var2);
               }
               break;
            case MIN:
               if (this.value == null || var1.compare(var2, this.value) < 0) {
                  this.value = var2;
               }
               break;
            case MAX:
               if (this.value == null || var1.compare(var2, this.value) > 0) {
                  this.value = var2;
               }
               break;
            case EVERY:
               var3 = var2.convertToBoolean();
               if (this.value == null) {
                  this.value = var3;
               } else {
                  this.value = ValueBoolean.get(this.value.getBoolean() && var3.getBoolean());
               }
               break;
            case ANY:
               var3 = var2.convertToBoolean();
               if (this.value == null) {
                  this.value = var3;
               } else {
                  this.value = ValueBoolean.get(this.value.getBoolean() || var3.getBoolean());
               }
               break;
            case BIT_AND_AGG:
            case BIT_NAND_AGG:
               if (this.value == null) {
                  this.value = var2;
               } else {
                  this.value = BitFunction.getBitwise(0, this.dataType, this.value, var2);
               }
               break;
            case BIT_OR_AGG:
            case BIT_NOR_AGG:
               if (this.value == null) {
                  this.value = var2;
               } else {
                  this.value = BitFunction.getBitwise(1, this.dataType, this.value, var2);
               }
               break;
            case BIT_XOR_AGG:
            case BIT_XNOR_AGG:
               if (this.value == null) {
                  this.value = var2;
               } else {
                  this.value = BitFunction.getBitwise(2, this.dataType, this.value, var2);
               }
               break;
            default:
               throw DbException.getInternalError("type=" + this.aggregateType);
         }

      }
   }

   Value getValue(SessionLocal var1) {
      Value var2 = this.value;
      if (var2 == null) {
         return ValueNull.INSTANCE;
      } else {
         switch (this.aggregateType) {
            case BIT_NAND_AGG:
            case BIT_NOR_AGG:
            case BIT_XNOR_AGG:
               var2 = BitFunction.getBitwise(3, this.dataType, var2, (Value)null);
            case BIT_OR_AGG:
            case BIT_XOR_AGG:
            default:
               return var2.convertTo(this.dataType);
         }
      }
   }
}
