package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewAttribute extends BCIRenumberedAttribute {
   private final List lengths = new ArrayList();
   private final List body = new ArrayList();
   private ClassConstantPool pool;
   private final int layoutIndex;

   public NewAttribute(CPUTF8 attributeName, int layoutIndex) {
      super(attributeName);
      this.layoutIndex = layoutIndex;
   }

   public int getLayoutIndex() {
      return this.layoutIndex;
   }

   protected int getLength() {
      int length = 0;

      for(int iter = 0; iter < this.lengths.size(); ++iter) {
         length += (Integer)this.lengths.get(iter);
      }

      return length;
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      for(int i = 0; i < this.lengths.size(); ++i) {
         int length = (Integer)this.lengths.get(i);
         Object obj = this.body.get(i);
         long value = 0L;
         if (obj instanceof Long) {
            value = (Long)obj;
         } else if (obj instanceof ClassFileEntry) {
            value = (long)this.pool.indexOf((ClassFileEntry)obj);
         } else if (obj instanceof BCValue) {
            value = (long)((BCValue)obj).actualValue;
         }

         if (length == 1) {
            dos.writeByte((int)value);
         } else if (length == 2) {
            dos.writeShort((int)value);
         } else if (length == 4) {
            dos.writeInt((int)value);
         } else if (length == 8) {
            dos.writeLong(value);
         }
      }

   }

   public String toString() {
      return this.attributeName.underlyingString();
   }

   public void addInteger(int length, long value) {
      this.lengths.add(length);
      this.body.add(value);
   }

   public void addBCOffset(int length, int value) {
      this.lengths.add(length);
      this.body.add(new BCOffset(value));
   }

   public void addBCIndex(int length, int value) {
      this.lengths.add(length);
      this.body.add(new BCIndex(value));
   }

   public void addBCLength(int length, int value) {
      this.lengths.add(length);
      this.body.add(new BCLength(value));
   }

   public void addToBody(int length, Object value) {
      this.lengths.add(length);
      this.body.add(value);
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);

      for(int iter = 0; iter < this.body.size(); ++iter) {
         Object element = this.body.get(iter);
         if (element instanceof ClassFileEntry) {
            ((ClassFileEntry)element).resolve(pool);
         }
      }

      this.pool = pool;
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      int total = 1;

      for(int iter = 0; iter < this.body.size(); ++iter) {
         Object element = this.body.get(iter);
         if (element instanceof ClassFileEntry) {
            ++total;
         }
      }

      ClassFileEntry[] nested = new ClassFileEntry[total];
      nested[0] = this.getAttributeName();
      int i = 1;

      for(int iter = 0; iter < this.body.size(); ++iter) {
         Object element = this.body.get(iter);
         if (element instanceof ClassFileEntry) {
            nested[i] = (ClassFileEntry)element;
            ++i;
         }
      }

      return nested;
   }

   protected int[] getStartPCs() {
      return null;
   }

   public void renumber(List byteCodeOffsets) {
      if (!this.renumbered) {
         Object previous = null;

         Object obj;
         for(Iterator iter = this.body.iterator(); iter.hasNext(); previous = obj) {
            obj = iter.next();
            if (obj instanceof BCIndex) {
               BCIndex bcIndex = (BCIndex)obj;
               bcIndex.setActualValue((Integer)byteCodeOffsets.get(bcIndex.index));
            } else if (obj instanceof BCOffset) {
               BCOffset bcOffset = (BCOffset)obj;
               int index;
               if (previous instanceof BCIndex) {
                  index = ((BCIndex)previous).index + bcOffset.offset;
                  bcOffset.setIndex(index);
                  bcOffset.setActualValue((Integer)byteCodeOffsets.get(index));
               } else if (previous instanceof BCOffset) {
                  index = ((BCOffset)previous).index + bcOffset.offset;
                  bcOffset.setIndex(index);
                  bcOffset.setActualValue((Integer)byteCodeOffsets.get(index));
               } else {
                  bcOffset.setActualValue((Integer)byteCodeOffsets.get(bcOffset.offset));
               }
            } else if (obj instanceof BCLength) {
               BCLength bcLength = (BCLength)obj;
               BCIndex prevIndex = (BCIndex)previous;
               int index = prevIndex.index + bcLength.length;
               int actualLength = (Integer)byteCodeOffsets.get(index) - prevIndex.actualValue;
               bcLength.setActualValue(actualLength);
            }
         }

         this.renumbered = true;
      }

   }

   private abstract static class BCValue {
      int actualValue;

      private BCValue() {
      }

      public void setActualValue(int value) {
         this.actualValue = value;
      }

      // $FF: synthetic method
      BCValue(Object x0) {
         this();
      }
   }

   private static class BCLength extends BCValue {
      private final int length;

      public BCLength(int length) {
         super(null);
         this.length = length;
      }
   }

   private static class BCIndex extends BCValue {
      private final int index;

      public BCIndex(int index) {
         super(null);
         this.index = index;
      }
   }

   private static class BCOffset extends BCValue {
      private final int offset;
      private int index;

      public BCOffset(int offset) {
         super(null);
         this.offset = offset;
      }

      public void setIndex(int index) {
         this.index = index;
      }
   }
}
