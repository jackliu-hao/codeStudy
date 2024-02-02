package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ConstantPoolEntry extends ClassFileEntry {
   public static final byte CP_Class = 7;
   public static final byte CP_Double = 6;
   public static final byte CP_Fieldref = 9;
   public static final byte CP_Float = 4;
   public static final byte CP_Integer = 3;
   public static final byte CP_InterfaceMethodref = 11;
   public static final byte CP_Long = 5;
   public static final byte CP_Methodref = 10;
   public static final byte CP_NameAndType = 12;
   public static final byte CP_String = 8;
   public static final byte CP_UTF8 = 1;
   byte tag;
   protected int globalIndex;

   ConstantPoolEntry(byte tag, int globalIndex) {
      this.tag = tag;
      this.globalIndex = globalIndex;
   }

   public abstract boolean equals(Object var1);

   public byte getTag() {
      return this.tag;
   }

   public abstract int hashCode();

   public void doWrite(DataOutputStream dos) throws IOException {
      dos.writeByte(this.tag);
      this.writeBody(dos);
   }

   protected abstract void writeBody(DataOutputStream var1) throws IOException;

   public int getGlobalIndex() {
      return this.globalIndex;
   }
}
