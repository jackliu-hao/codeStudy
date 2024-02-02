package com.google.protobuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class FieldSet<T extends FieldDescriptorLite<T>> {
   private static final int DEFAULT_FIELD_MAP_ARRAY_SIZE = 16;
   private final SmallSortedMap<T, Object> fields;
   private boolean isImmutable;
   private boolean hasLazyField;
   private static final FieldSet DEFAULT_INSTANCE = new FieldSet(true);

   private FieldSet() {
      this.fields = SmallSortedMap.newFieldMap(16);
   }

   private FieldSet(boolean dummy) {
      this(SmallSortedMap.newFieldMap(0));
      this.makeImmutable();
   }

   private FieldSet(SmallSortedMap<T, Object> fields) {
      this.fields = fields;
      this.makeImmutable();
   }

   public static <T extends FieldDescriptorLite<T>> FieldSet<T> newFieldSet() {
      return new FieldSet();
   }

   public static <T extends FieldDescriptorLite<T>> FieldSet<T> emptySet() {
      return DEFAULT_INSTANCE;
   }

   public static <T extends FieldDescriptorLite<T>> Builder<T> newBuilder() {
      return new Builder();
   }

   boolean isEmpty() {
      return this.fields.isEmpty();
   }

   public void makeImmutable() {
      if (!this.isImmutable) {
         this.fields.makeImmutable();
         this.isImmutable = true;
      }
   }

   public boolean isImmutable() {
      return this.isImmutable;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof FieldSet)) {
         return false;
      } else {
         FieldSet<?> other = (FieldSet)o;
         return this.fields.equals(other.fields);
      }
   }

   public int hashCode() {
      return this.fields.hashCode();
   }

   public FieldSet<T> clone() {
      FieldSet<T> clone = newFieldSet();

      Map.Entry entry;
      for(int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
         entry = this.fields.getArrayEntryAt(i);
         clone.setField((FieldDescriptorLite)entry.getKey(), entry.getValue());
      }

      Iterator var4 = this.fields.getOverflowEntries().iterator();

      while(var4.hasNext()) {
         entry = (Map.Entry)var4.next();
         clone.setField((FieldDescriptorLite)entry.getKey(), entry.getValue());
      }

      clone.hasLazyField = this.hasLazyField;
      return clone;
   }

   public void clear() {
      this.fields.clear();
      this.hasLazyField = false;
   }

   public Map<T, Object> getAllFields() {
      if (this.hasLazyField) {
         SmallSortedMap<T, Object> result = cloneAllFieldsMap(this.fields, false);
         if (this.fields.isImmutable()) {
            result.makeImmutable();
         }

         return result;
      } else {
         return (Map)(this.fields.isImmutable() ? this.fields : Collections.unmodifiableMap(this.fields));
      }
   }

   private static <T extends FieldDescriptorLite<T>> SmallSortedMap<T, Object> cloneAllFieldsMap(SmallSortedMap<T, Object> fields, boolean copyList) {
      SmallSortedMap<T, Object> result = SmallSortedMap.newFieldMap(16);

      for(int i = 0; i < fields.getNumArrayEntries(); ++i) {
         cloneFieldEntry(result, fields.getArrayEntryAt(i), copyList);
      }

      Iterator var5 = fields.getOverflowEntries().iterator();

      while(var5.hasNext()) {
         Map.Entry<T, Object> entry = (Map.Entry)var5.next();
         cloneFieldEntry(result, entry, copyList);
      }

      return result;
   }

   private static <T extends FieldDescriptorLite<T>> void cloneFieldEntry(Map<T, Object> map, Map.Entry<T, Object> entry, boolean copyList) {
      T key = (FieldDescriptorLite)entry.getKey();
      Object value = entry.getValue();
      if (value instanceof LazyField) {
         map.put(key, ((LazyField)value).getValue());
      } else if (copyList && value instanceof List) {
         map.put(key, new ArrayList((List)value));
      } else {
         map.put(key, value);
      }

   }

   public Iterator<Map.Entry<T, Object>> iterator() {
      return (Iterator)(this.hasLazyField ? new LazyField.LazyIterator(this.fields.entrySet().iterator()) : this.fields.entrySet().iterator());
   }

   Iterator<Map.Entry<T, Object>> descendingIterator() {
      return (Iterator)(this.hasLazyField ? new LazyField.LazyIterator(this.fields.descendingEntrySet().iterator()) : this.fields.descendingEntrySet().iterator());
   }

   public boolean hasField(T descriptor) {
      if (descriptor.isRepeated()) {
         throw new IllegalArgumentException("hasField() can only be called on non-repeated fields.");
      } else {
         return this.fields.get(descriptor) != null;
      }
   }

   public Object getField(T descriptor) {
      Object o = this.fields.get(descriptor);
      return o instanceof LazyField ? ((LazyField)o).getValue() : o;
   }

   public void setField(T descriptor, Object value) {
      if (descriptor.isRepeated()) {
         if (!(value instanceof List)) {
            throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
         }

         List newList = new ArrayList();
         newList.addAll((List)value);
         Iterator var4 = newList.iterator();

         while(var4.hasNext()) {
            Object element = var4.next();
            this.verifyType(descriptor.getLiteType(), element);
         }

         value = newList;
      } else {
         this.verifyType(descriptor.getLiteType(), value);
      }

      if (value instanceof LazyField) {
         this.hasLazyField = true;
      }

      this.fields.put((Comparable)descriptor, value);
   }

   public void clearField(T descriptor) {
      this.fields.remove(descriptor);
      if (this.fields.isEmpty()) {
         this.hasLazyField = false;
      }

   }

   public int getRepeatedFieldCount(T descriptor) {
      if (!descriptor.isRepeated()) {
         throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
      } else {
         Object value = this.getField(descriptor);
         return value == null ? 0 : ((List)value).size();
      }
   }

   public Object getRepeatedField(T descriptor, int index) {
      if (!descriptor.isRepeated()) {
         throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
      } else {
         Object value = this.getField(descriptor);
         if (value == null) {
            throw new IndexOutOfBoundsException();
         } else {
            return ((List)value).get(index);
         }
      }
   }

   public void setRepeatedField(T descriptor, int index, Object value) {
      if (!descriptor.isRepeated()) {
         throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
      } else {
         Object list = this.getField(descriptor);
         if (list == null) {
            throw new IndexOutOfBoundsException();
         } else {
            this.verifyType(descriptor.getLiteType(), value);
            ((List)list).set(index, value);
         }
      }
   }

   public void addRepeatedField(T descriptor, Object value) {
      if (!descriptor.isRepeated()) {
         throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
      } else {
         this.verifyType(descriptor.getLiteType(), value);
         Object existingValue = this.getField(descriptor);
         Object list;
         if (existingValue == null) {
            list = new ArrayList();
            this.fields.put((Comparable)descriptor, list);
         } else {
            list = (List)existingValue;
         }

         ((List)list).add(value);
      }
   }

   private void verifyType(WireFormat.FieldType type, Object value) {
      if (!isValidType(type, value)) {
         throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
      }
   }

   private static boolean isValidType(WireFormat.FieldType type, Object value) {
      Internal.checkNotNull(value);
      switch (type.getJavaType()) {
         case INT:
            return value instanceof Integer;
         case LONG:
            return value instanceof Long;
         case FLOAT:
            return value instanceof Float;
         case DOUBLE:
            return value instanceof Double;
         case BOOLEAN:
            return value instanceof Boolean;
         case STRING:
            return value instanceof String;
         case BYTE_STRING:
            return value instanceof ByteString || value instanceof byte[];
         case ENUM:
            return value instanceof Integer || value instanceof Internal.EnumLite;
         case MESSAGE:
            return value instanceof MessageLite || value instanceof LazyField;
         default:
            return false;
      }
   }

   public boolean isInitialized() {
      for(int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
         if (!isInitialized(this.fields.getArrayEntryAt(i))) {
            return false;
         }
      }

      Iterator var3 = this.fields.getOverflowEntries().iterator();

      Map.Entry entry;
      do {
         if (!var3.hasNext()) {
            return true;
         }

         entry = (Map.Entry)var3.next();
      } while(isInitialized(entry));

      return false;
   }

   private static <T extends FieldDescriptorLite<T>> boolean isInitialized(Map.Entry<T, Object> entry) {
      T descriptor = (FieldDescriptorLite)entry.getKey();
      if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
         if (descriptor.isRepeated()) {
            Iterator var2 = ((List)entry.getValue()).iterator();

            while(var2.hasNext()) {
               MessageLite element = (MessageLite)var2.next();
               if (!element.isInitialized()) {
                  return false;
               }
            }
         } else {
            Object value = entry.getValue();
            if (!(value instanceof MessageLite)) {
               if (value instanceof LazyField) {
                  return true;
               }

               throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
            }

            if (!((MessageLite)value).isInitialized()) {
               return false;
            }
         }
      }

      return true;
   }

   static int getWireFormatForFieldType(WireFormat.FieldType type, boolean isPacked) {
      return isPacked ? 2 : type.getWireType();
   }

   public void mergeFrom(FieldSet<T> other) {
      for(int i = 0; i < other.fields.getNumArrayEntries(); ++i) {
         this.mergeFromField(other.fields.getArrayEntryAt(i));
      }

      Iterator var4 = other.fields.getOverflowEntries().iterator();

      while(var4.hasNext()) {
         Map.Entry<T, Object> entry = (Map.Entry)var4.next();
         this.mergeFromField(entry);
      }

   }

   private static Object cloneIfMutable(Object value) {
      if (value instanceof byte[]) {
         byte[] bytes = (byte[])((byte[])value);
         byte[] copy = new byte[bytes.length];
         System.arraycopy(bytes, 0, copy, 0, bytes.length);
         return copy;
      } else {
         return value;
      }
   }

   private void mergeFromField(Map.Entry<T, Object> entry) {
      T descriptor = (FieldDescriptorLite)entry.getKey();
      Object otherValue = entry.getValue();
      if (otherValue instanceof LazyField) {
         otherValue = ((LazyField)otherValue).getValue();
      }

      Object value;
      if (descriptor.isRepeated()) {
         value = this.getField(descriptor);
         if (value == null) {
            value = new ArrayList();
         }

         Iterator var5 = ((List)otherValue).iterator();

         while(var5.hasNext()) {
            Object element = var5.next();
            ((List)value).add(cloneIfMutable(element));
         }

         this.fields.put((Comparable)descriptor, value);
      } else if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
         value = this.getField(descriptor);
         if (value == null) {
            this.fields.put((Comparable)descriptor, cloneIfMutable(otherValue));
         } else {
            Object value = descriptor.internalMergeFrom(((MessageLite)value).toBuilder(), (MessageLite)otherValue).build();
            this.fields.put((Comparable)descriptor, value);
         }
      } else {
         this.fields.put((Comparable)descriptor, cloneIfMutable(otherValue));
      }

   }

   public static Object readPrimitiveField(CodedInputStream input, WireFormat.FieldType type, boolean checkUtf8) throws IOException {
      return checkUtf8 ? WireFormat.readPrimitiveField(input, type, WireFormat.Utf8Validation.STRICT) : WireFormat.readPrimitiveField(input, type, WireFormat.Utf8Validation.LOOSE);
   }

   public void writeTo(CodedOutputStream output) throws IOException {
      Map.Entry entry;
      for(int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
         entry = this.fields.getArrayEntryAt(i);
         writeField((FieldDescriptorLite)entry.getKey(), entry.getValue(), output);
      }

      Iterator var4 = this.fields.getOverflowEntries().iterator();

      while(var4.hasNext()) {
         entry = (Map.Entry)var4.next();
         writeField((FieldDescriptorLite)entry.getKey(), entry.getValue(), output);
      }

   }

   public void writeMessageSetTo(CodedOutputStream output) throws IOException {
      for(int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
         this.writeMessageSetTo(this.fields.getArrayEntryAt(i), output);
      }

      Iterator var4 = this.fields.getOverflowEntries().iterator();

      while(var4.hasNext()) {
         Map.Entry<T, Object> entry = (Map.Entry)var4.next();
         this.writeMessageSetTo(entry, output);
      }

   }

   private void writeMessageSetTo(Map.Entry<T, Object> entry, CodedOutputStream output) throws IOException {
      T descriptor = (FieldDescriptorLite)entry.getKey();
      if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !descriptor.isRepeated() && !descriptor.isPacked()) {
         Object value = entry.getValue();
         if (value instanceof LazyField) {
            value = ((LazyField)value).getValue();
         }

         output.writeMessageSetExtension(((FieldDescriptorLite)entry.getKey()).getNumber(), (MessageLite)value);
      } else {
         writeField(descriptor, entry.getValue(), output);
      }

   }

   static void writeElement(CodedOutputStream output, WireFormat.FieldType type, int number, Object value) throws IOException {
      if (type == WireFormat.FieldType.GROUP) {
         output.writeGroup(number, (MessageLite)value);
      } else {
         output.writeTag(number, getWireFormatForFieldType(type, false));
         writeElementNoTag(output, type, value);
      }

   }

   static void writeElementNoTag(CodedOutputStream output, WireFormat.FieldType type, Object value) throws IOException {
      switch (type) {
         case DOUBLE:
            output.writeDoubleNoTag((Double)value);
            break;
         case FLOAT:
            output.writeFloatNoTag((Float)value);
            break;
         case INT64:
            output.writeInt64NoTag((Long)value);
            break;
         case UINT64:
            output.writeUInt64NoTag((Long)value);
            break;
         case INT32:
            output.writeInt32NoTag((Integer)value);
            break;
         case FIXED64:
            output.writeFixed64NoTag((Long)value);
            break;
         case FIXED32:
            output.writeFixed32NoTag((Integer)value);
            break;
         case BOOL:
            output.writeBoolNoTag((Boolean)value);
            break;
         case GROUP:
            output.writeGroupNoTag((MessageLite)value);
            break;
         case MESSAGE:
            output.writeMessageNoTag((MessageLite)value);
            break;
         case STRING:
            if (value instanceof ByteString) {
               output.writeBytesNoTag((ByteString)value);
            } else {
               output.writeStringNoTag((String)value);
            }
            break;
         case BYTES:
            if (value instanceof ByteString) {
               output.writeBytesNoTag((ByteString)value);
            } else {
               output.writeByteArrayNoTag((byte[])((byte[])value));
            }
            break;
         case UINT32:
            output.writeUInt32NoTag((Integer)value);
            break;
         case SFIXED32:
            output.writeSFixed32NoTag((Integer)value);
            break;
         case SFIXED64:
            output.writeSFixed64NoTag((Long)value);
            break;
         case SINT32:
            output.writeSInt32NoTag((Integer)value);
            break;
         case SINT64:
            output.writeSInt64NoTag((Long)value);
            break;
         case ENUM:
            if (value instanceof Internal.EnumLite) {
               output.writeEnumNoTag(((Internal.EnumLite)value).getNumber());
            } else {
               output.writeEnumNoTag((Integer)value);
            }
      }

   }

   public static void writeField(FieldDescriptorLite<?> descriptor, Object value, CodedOutputStream output) throws IOException {
      WireFormat.FieldType type = descriptor.getLiteType();
      int number = descriptor.getNumber();
      if (descriptor.isRepeated()) {
         List<?> valueList = (List)value;
         if (descriptor.isPacked()) {
            output.writeTag(number, 2);
            int dataSize = 0;

            Iterator var7;
            Object element;
            for(var7 = valueList.iterator(); var7.hasNext(); dataSize += computeElementSizeNoTag(type, element)) {
               element = var7.next();
            }

            output.writeRawVarint32(dataSize);
            var7 = valueList.iterator();

            while(var7.hasNext()) {
               element = var7.next();
               writeElementNoTag(output, type, element);
            }
         } else {
            Iterator var9 = valueList.iterator();

            while(var9.hasNext()) {
               Object element = var9.next();
               writeElement(output, type, number, element);
            }
         }
      } else if (value instanceof LazyField) {
         writeElement(output, type, number, ((LazyField)value).getValue());
      } else {
         writeElement(output, type, number, value);
      }

   }

   public int getSerializedSize() {
      int size = 0;

      Map.Entry entry;
      for(int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
         entry = this.fields.getArrayEntryAt(i);
         size += computeFieldSize((FieldDescriptorLite)entry.getKey(), entry.getValue());
      }

      for(Iterator var4 = this.fields.getOverflowEntries().iterator(); var4.hasNext(); size += computeFieldSize((FieldDescriptorLite)entry.getKey(), entry.getValue())) {
         entry = (Map.Entry)var4.next();
      }

      return size;
   }

   public int getMessageSetSerializedSize() {
      int size = 0;

      for(int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
         size += this.getMessageSetSerializedSize(this.fields.getArrayEntryAt(i));
      }

      Map.Entry entry;
      for(Iterator var4 = this.fields.getOverflowEntries().iterator(); var4.hasNext(); size += this.getMessageSetSerializedSize(entry)) {
         entry = (Map.Entry)var4.next();
      }

      return size;
   }

   private int getMessageSetSerializedSize(Map.Entry<T, Object> entry) {
      T descriptor = (FieldDescriptorLite)entry.getKey();
      Object value = entry.getValue();
      if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !descriptor.isRepeated() && !descriptor.isPacked()) {
         return value instanceof LazyField ? CodedOutputStream.computeLazyFieldMessageSetExtensionSize(((FieldDescriptorLite)entry.getKey()).getNumber(), (LazyField)value) : CodedOutputStream.computeMessageSetExtensionSize(((FieldDescriptorLite)entry.getKey()).getNumber(), (MessageLite)value);
      } else {
         return computeFieldSize(descriptor, value);
      }
   }

   static int computeElementSize(WireFormat.FieldType type, int number, Object value) {
      int tagSize = CodedOutputStream.computeTagSize(number);
      if (type == WireFormat.FieldType.GROUP) {
         tagSize *= 2;
      }

      return tagSize + computeElementSizeNoTag(type, value);
   }

   static int computeElementSizeNoTag(WireFormat.FieldType type, Object value) {
      switch (type) {
         case DOUBLE:
            return CodedOutputStream.computeDoubleSizeNoTag((Double)value);
         case FLOAT:
            return CodedOutputStream.computeFloatSizeNoTag((Float)value);
         case INT64:
            return CodedOutputStream.computeInt64SizeNoTag((Long)value);
         case UINT64:
            return CodedOutputStream.computeUInt64SizeNoTag((Long)value);
         case INT32:
            return CodedOutputStream.computeInt32SizeNoTag((Integer)value);
         case FIXED64:
            return CodedOutputStream.computeFixed64SizeNoTag((Long)value);
         case FIXED32:
            return CodedOutputStream.computeFixed32SizeNoTag((Integer)value);
         case BOOL:
            return CodedOutputStream.computeBoolSizeNoTag((Boolean)value);
         case GROUP:
            return CodedOutputStream.computeGroupSizeNoTag((MessageLite)value);
         case MESSAGE:
            if (value instanceof LazyField) {
               return CodedOutputStream.computeLazyFieldSizeNoTag((LazyField)value);
            }

            return CodedOutputStream.computeMessageSizeNoTag((MessageLite)value);
         case STRING:
            if (value instanceof ByteString) {
               return CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
            }

            return CodedOutputStream.computeStringSizeNoTag((String)value);
         case BYTES:
            if (value instanceof ByteString) {
               return CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
            }

            return CodedOutputStream.computeByteArraySizeNoTag((byte[])((byte[])value));
         case UINT32:
            return CodedOutputStream.computeUInt32SizeNoTag((Integer)value);
         case SFIXED32:
            return CodedOutputStream.computeSFixed32SizeNoTag((Integer)value);
         case SFIXED64:
            return CodedOutputStream.computeSFixed64SizeNoTag((Long)value);
         case SINT32:
            return CodedOutputStream.computeSInt32SizeNoTag((Integer)value);
         case SINT64:
            return CodedOutputStream.computeSInt64SizeNoTag((Long)value);
         case ENUM:
            if (value instanceof Internal.EnumLite) {
               return CodedOutputStream.computeEnumSizeNoTag(((Internal.EnumLite)value).getNumber());
            }

            return CodedOutputStream.computeEnumSizeNoTag((Integer)value);
         default:
            throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
      }
   }

   public static int computeFieldSize(FieldDescriptorLite<?> descriptor, Object value) {
      WireFormat.FieldType type = descriptor.getLiteType();
      int number = descriptor.getNumber();
      if (!descriptor.isRepeated()) {
         return computeElementSize(type, number, value);
      } else {
         int size;
         Iterator var5;
         Object element;
         if (descriptor.isPacked()) {
            size = 0;

            for(var5 = ((List)value).iterator(); var5.hasNext(); size += computeElementSizeNoTag(type, element)) {
               element = var5.next();
            }

            return size + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeRawVarint32Size(size);
         } else {
            size = 0;

            for(var5 = ((List)value).iterator(); var5.hasNext(); size += computeElementSize(type, number, element)) {
               element = var5.next();
            }

            return size;
         }
      }
   }

   // $FF: synthetic method
   FieldSet(SmallSortedMap x0, Object x1) {
      this(x0);
   }

   static final class Builder<T extends FieldDescriptorLite<T>> {
      private SmallSortedMap<T, Object> fields;
      private boolean hasLazyField;
      private boolean isMutable;
      private boolean hasNestedBuilders;

      private Builder() {
         this(SmallSortedMap.newFieldMap(16));
      }

      private Builder(SmallSortedMap<T, Object> fields) {
         this.fields = fields;
         this.isMutable = true;
      }

      public FieldSet<T> build() {
         if (this.fields.isEmpty()) {
            return FieldSet.emptySet();
         } else {
            this.isMutable = false;
            SmallSortedMap<T, Object> fieldsForBuild = this.fields;
            if (this.hasNestedBuilders) {
               fieldsForBuild = FieldSet.cloneAllFieldsMap(this.fields, false);
               replaceBuilders(fieldsForBuild);
            }

            FieldSet<T> fieldSet = new FieldSet(fieldsForBuild);
            fieldSet.hasLazyField = this.hasLazyField;
            return fieldSet;
         }
      }

      private static <T extends FieldDescriptorLite<T>> void replaceBuilders(SmallSortedMap<T, Object> fieldMap) {
         for(int i = 0; i < fieldMap.getNumArrayEntries(); ++i) {
            replaceBuilders(fieldMap.getArrayEntryAt(i));
         }

         Iterator var3 = fieldMap.getOverflowEntries().iterator();

         while(var3.hasNext()) {
            Map.Entry<T, Object> entry = (Map.Entry)var3.next();
            replaceBuilders(entry);
         }

      }

      private static <T extends FieldDescriptorLite<T>> void replaceBuilders(Map.Entry<T, Object> entry) {
         entry.setValue(replaceBuilders((FieldDescriptorLite)entry.getKey(), entry.getValue()));
      }

      private static <T extends FieldDescriptorLite<T>> Object replaceBuilders(T descriptor, Object value) {
         if (value == null) {
            return value;
         } else if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
            if (descriptor.isRepeated()) {
               if (!(value instanceof List)) {
                  throw new IllegalStateException("Repeated field should contains a List but actually contains type: " + value.getClass());
               } else {
                  List<Object> list = (List)value;

                  for(int i = 0; i < ((List)list).size(); ++i) {
                     Object oldElement = ((List)list).get(i);
                     Object newElement = replaceBuilder(oldElement);
                     if (newElement != oldElement) {
                        if (list == value) {
                           list = new ArrayList((Collection)list);
                        }

                        ((List)list).set(i, newElement);
                     }
                  }

                  return list;
               }
            } else {
               return replaceBuilder(value);
            }
         } else {
            return value;
         }
      }

      private static Object replaceBuilder(Object value) {
         return value instanceof MessageLite.Builder ? ((MessageLite.Builder)value).build() : value;
      }

      public static <T extends FieldDescriptorLite<T>> Builder<T> fromFieldSet(FieldSet<T> fieldSet) {
         Builder<T> builder = new Builder(FieldSet.cloneAllFieldsMap(fieldSet.fields, true));
         builder.hasLazyField = fieldSet.hasLazyField;
         return builder;
      }

      public Map<T, Object> getAllFields() {
         if (this.hasLazyField) {
            SmallSortedMap<T, Object> result = FieldSet.cloneAllFieldsMap(this.fields, false);
            if (this.fields.isImmutable()) {
               result.makeImmutable();
            } else {
               replaceBuilders(result);
            }

            return result;
         } else {
            return (Map)(this.fields.isImmutable() ? this.fields : Collections.unmodifiableMap(this.fields));
         }
      }

      public boolean hasField(T descriptor) {
         if (descriptor.isRepeated()) {
            throw new IllegalArgumentException("hasField() can only be called on non-repeated fields.");
         } else {
            return this.fields.get(descriptor) != null;
         }
      }

      public Object getField(T descriptor) {
         Object value = this.getFieldAllowBuilders(descriptor);
         return replaceBuilders(descriptor, value);
      }

      Object getFieldAllowBuilders(T descriptor) {
         Object o = this.fields.get(descriptor);
         return o instanceof LazyField ? ((LazyField)o).getValue() : o;
      }

      private void ensureIsMutable() {
         if (!this.isMutable) {
            this.fields = FieldSet.cloneAllFieldsMap(this.fields, true);
            this.isMutable = true;
         }

      }

      public void setField(T descriptor, Object value) {
         this.ensureIsMutable();
         if (descriptor.isRepeated()) {
            if (!(value instanceof List)) {
               throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
            }

            List newList = new ArrayList();
            newList.addAll((List)value);

            Object element;
            for(Iterator var4 = newList.iterator(); var4.hasNext(); this.hasNestedBuilders = this.hasNestedBuilders || element instanceof MessageLite.Builder) {
               element = var4.next();
               verifyType(descriptor.getLiteType(), element);
            }

            value = newList;
         } else {
            verifyType(descriptor.getLiteType(), value);
         }

         if (value instanceof LazyField) {
            this.hasLazyField = true;
         }

         this.hasNestedBuilders = this.hasNestedBuilders || value instanceof MessageLite.Builder;
         this.fields.put((Comparable)descriptor, value);
      }

      public void clearField(T descriptor) {
         this.ensureIsMutable();
         this.fields.remove(descriptor);
         if (this.fields.isEmpty()) {
            this.hasLazyField = false;
         }

      }

      public int getRepeatedFieldCount(T descriptor) {
         if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
         } else {
            Object value = this.getField(descriptor);
            return value == null ? 0 : ((List)value).size();
         }
      }

      public Object getRepeatedField(T descriptor, int index) {
         if (this.hasNestedBuilders) {
            this.ensureIsMutable();
         }

         Object value = this.getRepeatedFieldAllowBuilders(descriptor, index);
         return replaceBuilder(value);
      }

      Object getRepeatedFieldAllowBuilders(T descriptor, int index) {
         if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
         } else {
            Object value = this.getFieldAllowBuilders(descriptor);
            if (value == null) {
               throw new IndexOutOfBoundsException();
            } else {
               return ((List)value).get(index);
            }
         }
      }

      public void setRepeatedField(T descriptor, int index, Object value) {
         this.ensureIsMutable();
         if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
         } else {
            this.hasNestedBuilders = this.hasNestedBuilders || value instanceof MessageLite.Builder;
            Object list = this.getField(descriptor);
            if (list == null) {
               throw new IndexOutOfBoundsException();
            } else {
               verifyType(descriptor.getLiteType(), value);
               ((List)list).set(index, value);
            }
         }
      }

      public void addRepeatedField(T descriptor, Object value) {
         this.ensureIsMutable();
         if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
         } else {
            this.hasNestedBuilders = this.hasNestedBuilders || value instanceof MessageLite.Builder;
            verifyType(descriptor.getLiteType(), value);
            Object existingValue = this.getField(descriptor);
            Object list;
            if (existingValue == null) {
               list = new ArrayList();
               this.fields.put((Comparable)descriptor, list);
            } else {
               list = (List)existingValue;
            }

            ((List)list).add(value);
         }
      }

      private static void verifyType(WireFormat.FieldType type, Object value) {
         if (!FieldSet.isValidType(type, value)) {
            if (type.getJavaType() != WireFormat.JavaType.MESSAGE || !(value instanceof MessageLite.Builder)) {
               throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
            }
         }
      }

      public boolean isInitialized() {
         for(int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
            if (!FieldSet.isInitialized(this.fields.getArrayEntryAt(i))) {
               return false;
            }
         }

         Iterator var3 = this.fields.getOverflowEntries().iterator();

         Map.Entry entry;
         do {
            if (!var3.hasNext()) {
               return true;
            }

            entry = (Map.Entry)var3.next();
         } while(FieldSet.isInitialized(entry));

         return false;
      }

      public void mergeFrom(FieldSet<T> other) {
         this.ensureIsMutable();

         for(int i = 0; i < other.fields.getNumArrayEntries(); ++i) {
            this.mergeFromField(other.fields.getArrayEntryAt(i));
         }

         Iterator var4 = other.fields.getOverflowEntries().iterator();

         while(var4.hasNext()) {
            Map.Entry<T, Object> entry = (Map.Entry)var4.next();
            this.mergeFromField(entry);
         }

      }

      private void mergeFromField(Map.Entry<T, Object> entry) {
         T descriptor = (FieldDescriptorLite)entry.getKey();
         Object otherValue = entry.getValue();
         if (otherValue instanceof LazyField) {
            otherValue = ((LazyField)otherValue).getValue();
         }

         Object value;
         if (descriptor.isRepeated()) {
            value = this.getField(descriptor);
            if (value == null) {
               value = new ArrayList();
            }

            Iterator var5 = ((List)otherValue).iterator();

            while(var5.hasNext()) {
               Object element = var5.next();
               ((List)value).add(FieldSet.cloneIfMutable(element));
            }

            this.fields.put((Comparable)descriptor, value);
         } else if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
            value = this.getField(descriptor);
            if (value == null) {
               this.fields.put((Comparable)descriptor, FieldSet.cloneIfMutable(otherValue));
            } else if (value instanceof MessageLite.Builder) {
               descriptor.internalMergeFrom((MessageLite.Builder)value, (MessageLite)otherValue);
            } else {
               Object value = descriptor.internalMergeFrom(((MessageLite)value).toBuilder(), (MessageLite)otherValue).build();
               this.fields.put((Comparable)descriptor, value);
            }
         } else {
            this.fields.put((Comparable)descriptor, FieldSet.cloneIfMutable(otherValue));
         }

      }

      // $FF: synthetic method
      Builder(Object x0) {
         this();
      }
   }

   public interface FieldDescriptorLite<T extends FieldDescriptorLite<T>> extends Comparable<T> {
      int getNumber();

      WireFormat.FieldType getLiteType();

      WireFormat.JavaType getLiteJavaType();

      boolean isRepeated();

      boolean isPacked();

      Internal.EnumLiteMap<?> getEnumType();

      MessageLite.Builder internalMergeFrom(MessageLite.Builder var1, MessageLite var2);
   }
}
