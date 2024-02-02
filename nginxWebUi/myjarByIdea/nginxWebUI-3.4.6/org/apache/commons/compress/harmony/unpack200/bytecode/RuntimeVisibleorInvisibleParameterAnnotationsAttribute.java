package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RuntimeVisibleorInvisibleParameterAnnotationsAttribute extends AnnotationsAttribute {
   private final int num_parameters;
   private final ParameterAnnotation[] parameter_annotations;

   public RuntimeVisibleorInvisibleParameterAnnotationsAttribute(CPUTF8 name, ParameterAnnotation[] parameter_annotations) {
      super(name);
      this.num_parameters = parameter_annotations.length;
      this.parameter_annotations = parameter_annotations;
   }

   protected int getLength() {
      int length = 1;

      for(int i = 0; i < this.num_parameters; ++i) {
         length += this.parameter_annotations[i].getLength();
      }

      return length;
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);

      for(int i = 0; i < this.parameter_annotations.length; ++i) {
         this.parameter_annotations[i].resolve(pool);
      }

   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      dos.writeByte(this.num_parameters);

      for(int i = 0; i < this.num_parameters; ++i) {
         this.parameter_annotations[i].writeBody(dos);
      }

   }

   public String toString() {
      return this.attributeName.underlyingString() + ": " + this.num_parameters + " parameter annotations";
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      List nested = new ArrayList();
      nested.add(this.attributeName);

      for(int i = 0; i < this.parameter_annotations.length; ++i) {
         nested.addAll(this.parameter_annotations[i].getClassFileEntries());
      }

      ClassFileEntry[] nestedEntries = new ClassFileEntry[nested.size()];

      for(int i = 0; i < nestedEntries.length; ++i) {
         nestedEntries[i] = (ClassFileEntry)nested.get(i);
      }

      return nestedEntries;
   }

   public static class ParameterAnnotation {
      private final AnnotationsAttribute.Annotation[] annotations;
      private final int num_annotations;

      public ParameterAnnotation(AnnotationsAttribute.Annotation[] annotations) {
         this.num_annotations = annotations.length;
         this.annotations = annotations;
      }

      public void writeBody(DataOutputStream dos) throws IOException {
         dos.writeShort(this.num_annotations);

         for(int i = 0; i < this.annotations.length; ++i) {
            this.annotations[i].writeBody(dos);
         }

      }

      public void resolve(ClassConstantPool pool) {
         for(int i = 0; i < this.annotations.length; ++i) {
            this.annotations[i].resolve(pool);
         }

      }

      public int getLength() {
         int length = 2;

         for(int i = 0; i < this.annotations.length; ++i) {
            length += this.annotations[i].getLength();
         }

         return length;
      }

      public List getClassFileEntries() {
         List nested = new ArrayList();

         for(int i = 0; i < this.annotations.length; ++i) {
            nested.addAll(this.annotations[i].getClassFileEntries());
         }

         return nested;
      }
   }
}
