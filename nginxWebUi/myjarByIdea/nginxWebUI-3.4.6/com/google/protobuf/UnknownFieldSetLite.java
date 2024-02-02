package com.google.protobuf;

import java.io.IOException;
import java.util.Arrays;

public final class UnknownFieldSetLite {
   private static final int MIN_CAPACITY = 8;
   private static final UnknownFieldSetLite DEFAULT_INSTANCE = new UnknownFieldSetLite(0, new int[0], new Object[0], false);
   private int count;
   private int[] tags;
   private Object[] objects;
   private int memoizedSerializedSize;
   private boolean isMutable;

   public static UnknownFieldSetLite getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   static UnknownFieldSetLite newInstance() {
      return new UnknownFieldSetLite();
   }

   static UnknownFieldSetLite mutableCopyOf(UnknownFieldSetLite first, UnknownFieldSetLite second) {
      int count = first.count + second.count;
      int[] tags = Arrays.copyOf(first.tags, count);
      System.arraycopy(second.tags, 0, tags, first.count, second.count);
      Object[] objects = Arrays.copyOf(first.objects, count);
      System.arraycopy(second.objects, 0, objects, first.count, second.count);
      return new UnknownFieldSetLite(count, tags, objects, true);
   }

   private UnknownFieldSetLite() {
      this(0, new int[8], new Object[8], true);
   }

   private UnknownFieldSetLite(int count, int[] tags, Object[] objects, boolean isMutable) {
      this.memoizedSerializedSize = -1;
      this.count = count;
      this.tags = tags;
      this.objects = objects;
      this.isMutable = isMutable;
   }

   public void makeImmutable() {
      this.isMutable = false;
   }

   void checkMutable() {
      if (!this.isMutable) {
         throw new UnsupportedOperationException();
      }
   }

   public void writeTo(CodedOutputStream var1) throws IOException {
      // $FF: Couldn't be decompiled
   }

   public void writeAsMessageSetTo(CodedOutputStream output) throws IOException {
      for(int i = 0; i < this.count; ++i) {
         int fieldNumber = WireFormat.getTagFieldNumber(this.tags[i]);
         output.writeRawMessageSetExtension(fieldNumber, (ByteString)this.objects[i]);
      }

   }

   void writeAsMessageSetTo(Writer writer) throws IOException {
      int i;
      int fieldNumber;
      if (writer.fieldOrder() == Writer.FieldOrder.DESCENDING) {
         for(i = this.count - 1; i >= 0; --i) {
            fieldNumber = WireFormat.getTagFieldNumber(this.tags[i]);
            writer.writeMessageSetItem(fieldNumber, this.objects[i]);
         }
      } else {
         for(i = 0; i < this.count; ++i) {
            fieldNumber = WireFormat.getTagFieldNumber(this.tags[i]);
            writer.writeMessageSetItem(fieldNumber, this.objects[i]);
         }
      }

   }

   public void writeTo(Writer writer) throws IOException {
      if (this.count != 0) {
         int i;
         if (writer.fieldOrder() == Writer.FieldOrder.ASCENDING) {
            for(i = 0; i < this.count; ++i) {
               writeField(this.tags[i], this.objects[i], writer);
            }
         } else {
            for(i = this.count - 1; i >= 0; --i) {
               writeField(this.tags[i], this.objects[i], writer);
            }
         }

      }
   }

   private static void writeField(int var0, Object var1, Writer var2) throws IOException {
      // $FF: Couldn't be decompiled
   }

   public int getSerializedSizeAsMessageSet() {
      int size = this.memoizedSerializedSize;
      if (size != -1) {
         return size;
      } else {
         size = 0;

         for(int i = 0; i < this.count; ++i) {
            int tag = this.tags[i];
            int fieldNumber = WireFormat.getTagFieldNumber(tag);
            size += CodedOutputStream.computeRawMessageSetExtensionSize(fieldNumber, (ByteString)this.objects[i]);
         }

         this.memoizedSerializedSize = size;
         return size;
      }
   }

   public int getSerializedSize() {
      // $FF: Couldn't be decompiled
   }

   private static boolean equals(int[] tags1, int[] tags2, int count) {
      for(int i = 0; i < count; ++i) {
         if (tags1[i] != tags2[i]) {
            return false;
         }
      }

      return true;
   }

   private static boolean equals(Object[] objects1, Object[] objects2, int count) {
      for(int i = 0; i < count; ++i) {
         if (!objects1[i].equals(objects2[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (!(obj instanceof UnknownFieldSetLite)) {
         return false;
      } else {
         UnknownFieldSetLite other = (UnknownFieldSetLite)obj;
         return this.count == other.count && equals(this.tags, other.tags, this.count) && equals(this.objects, other.objects, this.count);
      }
   }

   private static int hashCode(int[] tags, int count) {
      int hashCode = 17;

      for(int i = 0; i < count; ++i) {
         hashCode = 31 * hashCode + tags[i];
      }

      return hashCode;
   }

   private static int hashCode(Object[] objects, int count) {
      int hashCode = 17;

      for(int i = 0; i < count; ++i) {
         hashCode = 31 * hashCode + objects[i].hashCode();
      }

      return hashCode;
   }

   public int hashCode() {
      int hashCode = 17;
      hashCode = 31 * hashCode + this.count;
      hashCode = 31 * hashCode + hashCode(this.tags, this.count);
      hashCode = 31 * hashCode + hashCode(this.objects, this.count);
      return hashCode;
   }

   final void printWithIndent(StringBuilder buffer, int indent) {
      for(int i = 0; i < this.count; ++i) {
         int fieldNumber = WireFormat.getTagFieldNumber(this.tags[i]);
         MessageLiteToString.printField(buffer, indent, String.valueOf(fieldNumber), this.objects[i]);
      }

   }

   void storeField(int tag, Object value) {
      this.checkMutable();
      this.ensureCapacity();
      this.tags[this.count] = tag;
      this.objects[this.count] = value;
      ++this.count;
   }

   private void ensureCapacity() {
      if (this.count == this.tags.length) {
         int increment = this.count < 4 ? 8 : this.count >> 1;
         int newLength = this.count + increment;
         this.tags = Arrays.copyOf(this.tags, newLength);
         this.objects = Arrays.copyOf(this.objects, newLength);
      }

   }

   boolean mergeFieldFrom(int var1, CodedInputStream var2) throws IOException {
      // $FF: Couldn't be decompiled
   }

   UnknownFieldSetLite mergeVarintField(int fieldNumber, int value) {
      this.checkMutable();
      if (fieldNumber == 0) {
         throw new IllegalArgumentException("Zero is not a valid field number.");
      } else {
         this.storeField(WireFormat.makeTag(fieldNumber, 0), (long)value);
         return this;
      }
   }

   UnknownFieldSetLite mergeLengthDelimitedField(int fieldNumber, ByteString value) {
      this.checkMutable();
      if (fieldNumber == 0) {
         throw new IllegalArgumentException("Zero is not a valid field number.");
      } else {
         this.storeField(WireFormat.makeTag(fieldNumber, 2), value);
         return this;
      }
   }

   private UnknownFieldSetLite mergeFrom(CodedInputStream input) throws IOException {
      int tag;
      do {
         tag = input.readTag();
      } while(tag != 0 && this.mergeFieldFrom(tag, input));

      return this;
   }
}
