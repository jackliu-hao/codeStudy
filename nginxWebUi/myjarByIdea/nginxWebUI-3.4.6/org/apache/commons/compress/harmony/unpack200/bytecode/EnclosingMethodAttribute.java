package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public class EnclosingMethodAttribute extends Attribute {
   private int class_index;
   private int method_index;
   private final CPClass cpClass;
   private final CPNameAndType method;
   private static CPUTF8 attributeName;

   public static void setAttributeName(CPUTF8 cpUTF8Value) {
      attributeName = cpUTF8Value;
   }

   public EnclosingMethodAttribute(CPClass cpClass, CPNameAndType method) {
      super(attributeName);
      this.cpClass = cpClass;
      this.method = method;
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      return this.method != null ? new ClassFileEntry[]{attributeName, this.cpClass, this.method} : new ClassFileEntry[]{attributeName, this.cpClass};
   }

   protected int getLength() {
      return 4;
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);
      this.cpClass.resolve(pool);
      this.class_index = pool.indexOf(this.cpClass);
      if (this.method != null) {
         this.method.resolve(pool);
         this.method_index = pool.indexOf(this.method);
      } else {
         this.method_index = 0;
      }

   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      dos.writeShort(this.class_index);
      dos.writeShort(this.method_index);
   }

   public String toString() {
      return "EnclosingMethod";
   }
}
