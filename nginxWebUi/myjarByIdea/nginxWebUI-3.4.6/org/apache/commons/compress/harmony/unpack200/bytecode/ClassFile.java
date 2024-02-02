package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public class ClassFile {
   public int major;
   public int minor;
   private final int magic = -889275714;
   public ClassConstantPool pool = new ClassConstantPool();
   public int accessFlags;
   public int thisClass;
   public int superClass;
   public int[] interfaces;
   public ClassFileEntry[] fields;
   public ClassFileEntry[] methods;
   public Attribute[] attributes;

   public void write(DataOutputStream dos) throws IOException {
      dos.writeInt(-889275714);
      dos.writeShort(this.minor);
      dos.writeShort(this.major);
      dos.writeShort(this.pool.size() + 1);

      int i;
      for(i = 1; i <= this.pool.size(); ++i) {
         ConstantPoolEntry entry;
         (entry = (ConstantPoolEntry)this.pool.get(i)).doWrite(dos);
         if (entry.getTag() == 6 || entry.getTag() == 5) {
            ++i;
         }
      }

      dos.writeShort(this.accessFlags);
      dos.writeShort(this.thisClass);
      dos.writeShort(this.superClass);
      dos.writeShort(this.interfaces.length);

      for(i = 0; i < this.interfaces.length; ++i) {
         dos.writeShort(this.interfaces[i]);
      }

      dos.writeShort(this.fields.length);

      for(i = 0; i < this.fields.length; ++i) {
         this.fields[i].write(dos);
      }

      dos.writeShort(this.methods.length);

      for(i = 0; i < this.methods.length; ++i) {
         this.methods[i].write(dos);
      }

      dos.writeShort(this.attributes.length);

      for(i = 0; i < this.attributes.length; ++i) {
         this.attributes[i].write(dos);
      }

   }
}
