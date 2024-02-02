package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public class DeprecatedAttribute extends Attribute {
   private static CPUTF8 attributeName;

   public static void setAttributeName(CPUTF8 cpUTF8Value) {
      attributeName = cpUTF8Value;
   }

   public DeprecatedAttribute() {
      super(attributeName);
   }

   protected int getLength() {
      return 0;
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
   }

   public String toString() {
      return "Deprecated Attribute";
   }
}
