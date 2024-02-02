package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public class CPInteger extends CPConstantNumber {
   public CPInteger(Integer value, int globalIndex) {
      super((byte)3, value, globalIndex);
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      dos.writeInt(this.getNumber().intValue());
   }

   public String toString() {
      return "Integer: " + this.getValue();
   }
}
