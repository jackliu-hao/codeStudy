package org.h2.expression;

import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.schema.Sequence;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public final class SequenceValue extends Operation0 {
   private final Sequence sequence;
   private final boolean current;
   private final Prepared prepared;

   public SequenceValue(Sequence var1, Prepared var2) {
      this.sequence = var1;
      this.current = false;
      this.prepared = var2;
   }

   public SequenceValue(Sequence var1) {
      this.sequence = var1;
      this.current = true;
      this.prepared = null;
   }

   public Value getValue(SessionLocal var1) {
      return this.current ? var1.getCurrentValueFor(this.sequence) : var1.getNextValueFor(this.sequence, this.prepared);
   }

   public TypeInfo getType() {
      return this.sequence.getDataType();
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      var1.append(this.current ? "CURRENT" : "NEXT").append(" VALUE FOR ");
      return this.sequence.getSQL(var1, var2);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      switch (var1.getType()) {
         case 0:
         case 2:
         case 8:
            return false;
         case 1:
         case 3:
         case 6:
         default:
            return true;
         case 4:
            var1.addDataModificationId(this.sequence.getModificationId());
            return true;
         case 5:
            return this.current;
         case 7:
            var1.addDependency(this.sequence);
            return true;
      }
   }

   public int getCost() {
      return 1;
   }
}
