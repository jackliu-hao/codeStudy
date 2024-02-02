package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class CPRef extends ConstantPoolEntry {
   CPClass className;
   transient int classNameIndex;
   protected CPNameAndType nameAndType;
   transient int nameAndTypeIndex;
   protected String cachedToString;

   public CPRef(byte type, CPClass className, CPNameAndType descriptor, int globalIndex) {
      super(type, globalIndex);
      this.className = className;
      this.nameAndType = descriptor;
      if (descriptor == null || className == null) {
         throw new NullPointerException("Null arguments are not allowed");
      }
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else if (this.hashCode() != obj.hashCode()) {
         return false;
      } else {
         CPRef other = (CPRef)obj;
         if (!this.className.equals(other.className)) {
            return false;
         } else {
            return this.nameAndType.equals(other.nameAndType);
         }
      }
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      ClassFileEntry[] entries = new ClassFileEntry[]{this.className, this.nameAndType};
      return entries;
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);
      this.nameAndTypeIndex = pool.indexOf(this.nameAndType);
      this.classNameIndex = pool.indexOf(this.className);
   }

   public String toString() {
      if (this.cachedToString == null) {
         String type;
         if (this.getTag() == 9) {
            type = "FieldRef";
         } else if (this.getTag() == 10) {
            type = "MethoddRef";
         } else if (this.getTag() == 11) {
            type = "InterfaceMethodRef";
         } else {
            type = "unknown";
         }

         this.cachedToString = type + ": " + this.className + "#" + this.nameAndType;
      }

      return this.cachedToString;
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      dos.writeShort(this.classNameIndex);
      dos.writeShort(this.nameAndTypeIndex);
   }
}
