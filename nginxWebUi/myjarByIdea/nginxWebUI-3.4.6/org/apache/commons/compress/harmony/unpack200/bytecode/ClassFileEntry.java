package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ClassFileEntry {
   protected static final ClassFileEntry[] NONE = new ClassFileEntry[0];
   private boolean resolved;

   protected abstract void doWrite(DataOutputStream var1) throws IOException;

   public abstract boolean equals(Object var1);

   protected ClassFileEntry[] getNestedClassFileEntries() {
      return NONE;
   }

   public abstract int hashCode();

   protected void resolve(ClassConstantPool pool) {
      this.resolved = true;
   }

   protected int objectHashCode() {
      return super.hashCode();
   }

   public abstract String toString();

   public final void write(DataOutputStream dos) throws IOException {
      if (!this.resolved) {
         throw new IllegalStateException("Entry has not been resolved");
      } else {
         this.doWrite(dos);
      }
   }
}
