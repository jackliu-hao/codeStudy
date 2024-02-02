package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public class CPLong extends CPConstantNumber {
   public CPLong(Long value, int globalIndex) {
      super((byte)5, value, globalIndex);
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      dos.writeLong(this.getNumber().longValue());
   }

   public String toString() {
      return "Long: " + this.getValue();
   }
}
