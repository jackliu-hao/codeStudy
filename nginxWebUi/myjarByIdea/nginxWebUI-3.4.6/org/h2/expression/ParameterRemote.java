package org.h2.expression;

import java.io.IOException;
import org.h2.message.DbException;
import org.h2.value.Transfer;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueLob;

public class ParameterRemote implements ParameterInterface {
   private Value value;
   private final int index;
   private TypeInfo type;
   private int nullable;

   public ParameterRemote(int var1) {
      this.type = TypeInfo.TYPE_UNKNOWN;
      this.nullable = 2;
      this.index = var1;
   }

   public void setValue(Value var1, boolean var2) {
      if (var2 && this.value instanceof ValueLob) {
         ((ValueLob)this.value).remove();
      }

      this.value = var1;
   }

   public Value getParamValue() {
      return this.value;
   }

   public void checkSet() {
      if (this.value == null) {
         throw DbException.get(90012, "#" + (this.index + 1));
      }
   }

   public boolean isValueSet() {
      return this.value != null;
   }

   public TypeInfo getType() {
      return this.value == null ? this.type : this.value.getType();
   }

   public int getNullable() {
      return this.nullable;
   }

   public void readMetaData(Transfer var1) throws IOException {
      this.type = var1.readTypeInfo();
      this.nullable = var1.readInt();
   }

   public static void writeMetaData(Transfer var0, ParameterInterface var1) throws IOException {
      var0.writeTypeInfo(var1.getType()).writeInt(var1.getNullable());
   }
}
