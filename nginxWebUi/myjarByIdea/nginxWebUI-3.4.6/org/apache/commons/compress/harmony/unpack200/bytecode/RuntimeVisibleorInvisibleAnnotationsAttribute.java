package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RuntimeVisibleorInvisibleAnnotationsAttribute extends AnnotationsAttribute {
   private final int num_annotations;
   private final AnnotationsAttribute.Annotation[] annotations;

   public RuntimeVisibleorInvisibleAnnotationsAttribute(CPUTF8 name, AnnotationsAttribute.Annotation[] annotations) {
      super(name);
      this.num_annotations = annotations.length;
      this.annotations = annotations;
   }

   protected int getLength() {
      int length = 2;

      for(int i = 0; i < this.num_annotations; ++i) {
         length += this.annotations[i].getLength();
      }

      return length;
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);

      for(int i = 0; i < this.annotations.length; ++i) {
         this.annotations[i].resolve(pool);
      }

   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      int size = dos.size();
      dos.writeShort(this.num_annotations);

      for(int i = 0; i < this.num_annotations; ++i) {
         this.annotations[i].writeBody(dos);
      }

      if (dos.size() - size != this.getLength()) {
         throw new Error();
      }
   }

   public String toString() {
      return this.attributeName.underlyingString() + ": " + this.num_annotations + " annotations";
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      List nested = new ArrayList();
      nested.add(this.attributeName);

      for(int i = 0; i < this.annotations.length; ++i) {
         nested.addAll(this.annotations[i].getClassFileEntries());
      }

      ClassFileEntry[] nestedEntries = new ClassFileEntry[nested.size()];

      for(int i = 0; i < nestedEntries.length; ++i) {
         nestedEntries[i] = (ClassFileEntry)nested.get(i);
      }

      return nestedEntries;
   }
}
