package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public class CPDouble extends CPConstantNumber {
   public CPDouble(Double value, int globalIndex) {
      super((byte)6, value, globalIndex);
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      dos.writeDouble(this.getNumber().doubleValue());
   }

   public String toString() {
      return "Double: " + this.getValue();
   }
}
