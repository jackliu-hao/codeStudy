package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public class SignatureAttribute extends Attribute {
   private int signature_index;
   private final CPUTF8 signature;
   private static CPUTF8 attributeName;

   public static void setAttributeName(CPUTF8 cpUTF8Value) {
      attributeName = cpUTF8Value;
   }

   public SignatureAttribute(CPUTF8 value) {
      super(attributeName);
      this.signature = value;
   }

   protected int getLength() {
      return 2;
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      return new ClassFileEntry[]{this.getAttributeName(), this.signature};
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);
      this.signature.resolve(pool);
      this.signature_index = pool.indexOf(this.signature);
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      dos.writeShort(this.signature_index);
   }

   public String toString() {
      return "Signature: " + this.signature;
   }
}
