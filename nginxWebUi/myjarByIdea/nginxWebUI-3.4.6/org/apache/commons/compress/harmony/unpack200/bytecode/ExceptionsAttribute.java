package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ExceptionsAttribute extends Attribute {
   private static CPUTF8 attributeName;
   private transient int[] exceptionIndexes;
   private final CPClass[] exceptions;

   private static int hashCode(Object[] array) {
      int prime = true;
      if (array == null) {
         return 0;
      } else {
         int result = 1;

         for(int index = 0; index < array.length; ++index) {
            result = 31 * result + (array[index] == null ? 0 : array[index].hashCode());
         }

         return result;
      }
   }

   public ExceptionsAttribute(CPClass[] exceptions) {
      super(attributeName);
      this.exceptions = exceptions;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!super.equals(obj)) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         ExceptionsAttribute other = (ExceptionsAttribute)obj;
         return Arrays.equals(this.exceptions, other.exceptions);
      }
   }

   protected int getLength() {
      return 2 + 2 * this.exceptions.length;
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      ClassFileEntry[] result = new ClassFileEntry[this.exceptions.length + 1];

      for(int i = 0; i < this.exceptions.length; ++i) {
         result[i] = this.exceptions[i];
      }

      result[this.exceptions.length] = this.getAttributeName();
      return result;
   }

   public int hashCode() {
      int prime = true;
      int result = super.hashCode();
      result = 31 * result + hashCode(this.exceptions);
      return result;
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);
      this.exceptionIndexes = new int[this.exceptions.length];

      for(int i = 0; i < this.exceptions.length; ++i) {
         this.exceptions[i].resolve(pool);
         this.exceptionIndexes[i] = pool.indexOf(this.exceptions[i]);
      }

   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("Exceptions: ");

      for(int i = 0; i < this.exceptions.length; ++i) {
         sb.append(this.exceptions[i]);
         sb.append(' ');
      }

      return sb.toString();
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      dos.writeShort(this.exceptionIndexes.length);

      for(int i = 0; i < this.exceptionIndexes.length; ++i) {
         dos.writeShort(this.exceptionIndexes[i]);
      }

   }

   public static void setAttributeName(CPUTF8 cpUTF8Value) {
      attributeName = cpUTF8Value;
   }
}
