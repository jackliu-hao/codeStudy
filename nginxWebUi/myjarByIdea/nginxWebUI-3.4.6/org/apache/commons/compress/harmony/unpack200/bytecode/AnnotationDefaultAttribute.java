package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnnotationDefaultAttribute extends AnnotationsAttribute {
   private final AnnotationsAttribute.ElementValue element_value;
   private static CPUTF8 attributeName;

   public static void setAttributeName(CPUTF8 cpUTF8Value) {
      attributeName = cpUTF8Value;
   }

   public AnnotationDefaultAttribute(AnnotationsAttribute.ElementValue element_value) {
      super(attributeName);
      this.element_value = element_value;
   }

   protected int getLength() {
      return this.element_value.getLength();
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      this.element_value.writeBody(dos);
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);
      this.element_value.resolve(pool);
   }

   public String toString() {
      return "AnnotationDefault: " + this.element_value;
   }

   public boolean equals(Object obj) {
      return this == obj;
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      List nested = new ArrayList();
      nested.add(attributeName);
      nested.addAll(this.element_value.getClassFileEntries());
      ClassFileEntry[] nestedEntries = new ClassFileEntry[nested.size()];

      for(int i = 0; i < nestedEntries.length; ++i) {
         nestedEntries[i] = (ClassFileEntry)nested.get(i);
      }

      return nestedEntries;
   }
}
