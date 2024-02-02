package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class CPMember extends ClassFileEntry {
   List attributes;
   short flags;
   CPUTF8 name;
   transient int nameIndex;
   protected final CPUTF8 descriptor;
   transient int descriptorIndex;

   public CPMember(CPUTF8 name, CPUTF8 descriptor, long flags, List attributes) {
      this.name = name;
      this.descriptor = descriptor;
      this.flags = (short)((int)flags);
      this.attributes = attributes == null ? Collections.EMPTY_LIST : attributes;
      if (name == null || descriptor == null) {
         throw new NullPointerException("Null arguments are not allowed");
      }
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      int attributeCount = this.attributes.size();
      ClassFileEntry[] entries = new ClassFileEntry[attributeCount + 2];
      entries[0] = this.name;
      entries[1] = this.descriptor;

      for(int i = 0; i < attributeCount; ++i) {
         entries[i + 2] = (Attribute)this.attributes.get(i);
      }

      return entries;
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);
      this.nameIndex = pool.indexOf(this.name);
      this.descriptorIndex = pool.indexOf(this.descriptor);

      for(int it = 0; it < this.attributes.size(); ++it) {
         Attribute attribute = (Attribute)this.attributes.get(it);
         attribute.resolve(pool);
      }

   }

   public int hashCode() {
      int PRIME = true;
      int result = 1;
      result = 31 * result + this.attributes.hashCode();
      result = 31 * result + this.descriptor.hashCode();
      result = 31 * result + this.flags;
      result = 31 * result + this.name.hashCode();
      return result;
   }

   public String toString() {
      return "CPMember: " + this.name + "(" + this.descriptor + ")";
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CPMember other = (CPMember)obj;
         if (!this.attributes.equals(other.attributes)) {
            return false;
         } else if (!this.descriptor.equals(other.descriptor)) {
            return false;
         } else if (this.flags != other.flags) {
            return false;
         } else {
            return this.name.equals(other.name);
         }
      }
   }

   protected void doWrite(DataOutputStream dos) throws IOException {
      dos.writeShort(this.flags);
      dos.writeShort(this.nameIndex);
      dos.writeShort(this.descriptorIndex);
      int attributeCount = this.attributes.size();
      dos.writeShort(attributeCount);

      for(int i = 0; i < attributeCount; ++i) {
         Attribute attribute = (Attribute)this.attributes.get(i);
         attribute.doWrite(dos);
      }

   }
}
