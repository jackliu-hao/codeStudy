package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractMessage extends AbstractMessageLite implements Message {
   protected int memoizedSize = -1;

   public boolean isInitialized() {
      return MessageReflection.isInitialized(this);
   }

   protected Message.Builder newBuilderForType(BuilderParent parent) {
      throw new UnsupportedOperationException("Nested builder is not supported for this type.");
   }

   public List<String> findInitializationErrors() {
      return MessageReflection.findMissingFields(this);
   }

   public String getInitializationErrorString() {
      return MessageReflection.delimitWithCommas(this.findInitializationErrors());
   }

   public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
      throw new UnsupportedOperationException("hasOneof() is not implemented.");
   }

   public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
      throw new UnsupportedOperationException("getOneofFieldDescriptor() is not implemented.");
   }

   public final String toString() {
      return TextFormat.printer().printToString((MessageOrBuilder)this);
   }

   public void writeTo(CodedOutputStream output) throws IOException {
      MessageReflection.writeMessageTo(this, this.getAllFields(), output, false);
   }

   int getMemoizedSerializedSize() {
      return this.memoizedSize;
   }

   void setMemoizedSerializedSize(int size) {
      this.memoizedSize = size;
   }

   public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1) {
         return size;
      } else {
         this.memoizedSize = MessageReflection.getSerializedSize(this, this.getAllFields());
         return this.memoizedSize;
      }
   }

   public boolean equals(Object other) {
      if (other == this) {
         return true;
      } else if (!(other instanceof Message)) {
         return false;
      } else {
         Message otherMessage = (Message)other;
         if (this.getDescriptorForType() != otherMessage.getDescriptorForType()) {
            return false;
         } else {
            return compareFields(this.getAllFields(), otherMessage.getAllFields()) && this.getUnknownFields().equals(otherMessage.getUnknownFields());
         }
      }
   }

   public int hashCode() {
      int hash = this.memoizedHashCode;
      if (hash == 0) {
         int hash = 41;
         hash = 19 * hash + this.getDescriptorForType().hashCode();
         hash = hashFields(hash, this.getAllFields());
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
      }

      return hash;
   }

   private static ByteString toByteString(Object value) {
      return value instanceof byte[] ? ByteString.copyFrom((byte[])((byte[])value)) : (ByteString)value;
   }

   private static boolean compareBytes(Object a, Object b) {
      return a instanceof byte[] && b instanceof byte[] ? Arrays.equals((byte[])((byte[])a), (byte[])((byte[])b)) : toByteString(a).equals(toByteString(b));
   }

   private static Map convertMapEntryListToMap(List list) {
      if (list.isEmpty()) {
         return Collections.emptyMap();
      } else {
         Map result = new HashMap();
         Iterator iterator = list.iterator();
         Message entry = (Message)iterator.next();
         Descriptors.Descriptor descriptor = entry.getDescriptorForType();
         Descriptors.FieldDescriptor key = descriptor.findFieldByName("key");
         Descriptors.FieldDescriptor value = descriptor.findFieldByName("value");
         Object fieldValue = entry.getField(value);
         if (fieldValue instanceof Descriptors.EnumValueDescriptor) {
            fieldValue = ((Descriptors.EnumValueDescriptor)fieldValue).getNumber();
         }

         result.put(entry.getField(key), fieldValue);

         for(; iterator.hasNext(); result.put(entry.getField(key), fieldValue)) {
            entry = (Message)iterator.next();
            fieldValue = entry.getField(value);
            if (fieldValue instanceof Descriptors.EnumValueDescriptor) {
               fieldValue = ((Descriptors.EnumValueDescriptor)fieldValue).getNumber();
            }
         }

         return result;
      }
   }

   private static boolean compareMapField(Object a, Object b) {
      Map ma = convertMapEntryListToMap((List)a);
      Map mb = convertMapEntryListToMap((List)b);
      return MapFieldLite.equals(ma, mb);
   }

   static boolean compareFields(Map<Descriptors.FieldDescriptor, Object> a, Map<Descriptors.FieldDescriptor, Object> b) {
      if (a.size() != b.size()) {
         return false;
      } else {
         Iterator var2 = a.keySet().iterator();

         while(true) {
            while(true) {
               while(var2.hasNext()) {
                  Descriptors.FieldDescriptor descriptor = (Descriptors.FieldDescriptor)var2.next();
                  if (!b.containsKey(descriptor)) {
                     return false;
                  }

                  Object value1 = a.get(descriptor);
                  Object value2 = b.get(descriptor);
                  if (descriptor.getType() == Descriptors.FieldDescriptor.Type.BYTES) {
                     if (descriptor.isRepeated()) {
                        List list1 = (List)value1;
                        List list2 = (List)value2;
                        if (list1.size() != list2.size()) {
                           return false;
                        }

                        for(int i = 0; i < list1.size(); ++i) {
                           if (!compareBytes(list1.get(i), list2.get(i))) {
                              return false;
                           }
                        }
                     } else if (!compareBytes(value1, value2)) {
                        return false;
                     }
                  } else if (descriptor.isMapField()) {
                     if (!compareMapField(value1, value2)) {
                        return false;
                     }
                  } else if (!value1.equals(value2)) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   private static int hashMapField(Object value) {
      return MapFieldLite.calculateHashCodeForMap(convertMapEntryListToMap((List)value));
   }

   protected static int hashFields(int hash, Map<Descriptors.FieldDescriptor, Object> map) {
      Iterator var2 = map.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<Descriptors.FieldDescriptor, Object> entry = (Map.Entry)var2.next();
         Descriptors.FieldDescriptor field = (Descriptors.FieldDescriptor)entry.getKey();
         Object value = entry.getValue();
         hash = 37 * hash + field.getNumber();
         if (field.isMapField()) {
            hash = 53 * hash + hashMapField(value);
         } else if (field.getType() != Descriptors.FieldDescriptor.Type.ENUM) {
            hash = 53 * hash + value.hashCode();
         } else if (field.isRepeated()) {
            List<? extends Internal.EnumLite> list = (List)value;
            hash = 53 * hash + Internal.hashEnumList(list);
         } else {
            hash = 53 * hash + Internal.hashEnum((Internal.EnumLite)value);
         }
      }

      return hash;
   }

   UninitializedMessageException newUninitializedMessageException() {
      return AbstractMessage.Builder.newUninitializedMessageException(this);
   }

   /** @deprecated */
   @Deprecated
   protected static int hashLong(long n) {
      return (int)(n ^ n >>> 32);
   }

   /** @deprecated */
   @Deprecated
   protected static int hashBoolean(boolean b) {
      return b ? 1231 : 1237;
   }

   /** @deprecated */
   @Deprecated
   protected static int hashEnum(Internal.EnumLite e) {
      return e.getNumber();
   }

   /** @deprecated */
   @Deprecated
   protected static int hashEnumList(List<? extends Internal.EnumLite> list) {
      int hash = 1;

      Internal.EnumLite e;
      for(Iterator var2 = list.iterator(); var2.hasNext(); hash = 31 * hash + hashEnum(e)) {
         e = (Internal.EnumLite)var2.next();
      }

      return hash;
   }

   public abstract static class Builder<BuilderType extends Builder<BuilderType>> extends AbstractMessageLite.Builder implements Message.Builder {
      public BuilderType clone() {
         throw new UnsupportedOperationException("clone() should be implemented in subclasses.");
      }

      public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
         throw new UnsupportedOperationException("hasOneof() is not implemented.");
      }

      public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
         throw new UnsupportedOperationException("getOneofFieldDescriptor() is not implemented.");
      }

      public BuilderType clearOneof(Descriptors.OneofDescriptor oneof) {
         throw new UnsupportedOperationException("clearOneof() is not implemented.");
      }

      public BuilderType clear() {
         Iterator var1 = this.getAllFields().entrySet().iterator();

         while(var1.hasNext()) {
            Map.Entry<Descriptors.FieldDescriptor, Object> entry = (Map.Entry)var1.next();
            this.clearField((Descriptors.FieldDescriptor)entry.getKey());
         }

         return this;
      }

      public List<String> findInitializationErrors() {
         return MessageReflection.findMissingFields(this);
      }

      public String getInitializationErrorString() {
         return MessageReflection.delimitWithCommas(this.findInitializationErrors());
      }

      protected BuilderType internalMergeFrom(AbstractMessageLite other) {
         return this.mergeFrom((Message)other);
      }

      public BuilderType mergeFrom(Message other) {
         return this.mergeFrom(other, other.getAllFields());
      }

      BuilderType mergeFrom(Message other, Map<Descriptors.FieldDescriptor, Object> allFields) {
         if (other.getDescriptorForType() != this.getDescriptorForType()) {
            throw new IllegalArgumentException("mergeFrom(Message) can only merge messages of the same type.");
         } else {
            Iterator var3 = allFields.entrySet().iterator();

            while(true) {
               while(var3.hasNext()) {
                  Map.Entry<Descriptors.FieldDescriptor, Object> entry = (Map.Entry)var3.next();
                  Descriptors.FieldDescriptor field = (Descriptors.FieldDescriptor)entry.getKey();
                  if (field.isRepeated()) {
                     Iterator var8 = ((List)entry.getValue()).iterator();

                     while(var8.hasNext()) {
                        Object element = var8.next();
                        this.addRepeatedField(field, element);
                     }
                  } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                     Message existingValue = (Message)this.getField(field);
                     if (existingValue == existingValue.getDefaultInstanceForType()) {
                        this.setField(field, entry.getValue());
                     } else {
                        this.setField(field, existingValue.newBuilderForType().mergeFrom(existingValue).mergeFrom((Message)entry.getValue()).build());
                     }
                  } else {
                     this.setField(field, entry.getValue());
                  }
               }

               this.mergeUnknownFields(other.getUnknownFields());
               return this;
            }
         }
      }

      public BuilderType mergeFrom(CodedInputStream input) throws IOException {
         return this.mergeFrom((CodedInputStream)input, (ExtensionRegistryLite)ExtensionRegistry.getEmptyRegistry());
      }

      public BuilderType mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         boolean discardUnknown = input.shouldDiscardUnknownFields();
         UnknownFieldSet.Builder unknownFields = discardUnknown ? null : UnknownFieldSet.newBuilder(this.getUnknownFields());

         int tag;
         MessageReflection.BuilderAdapter builderAdapter;
         do {
            tag = input.readTag();
            if (tag == 0) {
               break;
            }

            builderAdapter = new MessageReflection.BuilderAdapter(this);
         } while(MessageReflection.mergeFieldFrom(input, unknownFields, extensionRegistry, this.getDescriptorForType(), builderAdapter, tag));

         if (unknownFields != null) {
            this.setUnknownFields(unknownFields.build());
         }

         return this;
      }

      public BuilderType mergeUnknownFields(UnknownFieldSet unknownFields) {
         this.setUnknownFields(UnknownFieldSet.newBuilder(this.getUnknownFields()).mergeFrom(unknownFields).build());
         return this;
      }

      public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
         throw new UnsupportedOperationException("getFieldBuilder() called on an unsupported message type.");
      }

      public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
         throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on an unsupported message type.");
      }

      public String toString() {
         return TextFormat.printer().printToString((MessageOrBuilder)this);
      }

      protected static UninitializedMessageException newUninitializedMessageException(Message message) {
         return new UninitializedMessageException(MessageReflection.findMissingFields(message));
      }

      void markClean() {
         throw new IllegalStateException("Should be overridden by subclasses.");
      }

      void dispose() {
         throw new IllegalStateException("Should be overridden by subclasses.");
      }

      public BuilderType mergeFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Builder)super.mergeFrom(data);
      }

      public BuilderType mergeFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Builder)super.mergeFrom(data, extensionRegistry);
      }

      public BuilderType mergeFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Builder)super.mergeFrom(data);
      }

      public BuilderType mergeFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
         return (Builder)super.mergeFrom(data, off, len);
      }

      public BuilderType mergeFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Builder)super.mergeFrom(data, extensionRegistry);
      }

      public BuilderType mergeFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Builder)super.mergeFrom(data, off, len, extensionRegistry);
      }

      public BuilderType mergeFrom(InputStream input) throws IOException {
         return (Builder)super.mergeFrom(input);
      }

      public BuilderType mergeFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Builder)super.mergeFrom(input, extensionRegistry);
      }

      public boolean mergeDelimitedFrom(InputStream input) throws IOException {
         return super.mergeDelimitedFrom(input);
      }

      public boolean mergeDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return super.mergeDelimitedFrom(input, extensionRegistry);
      }
   }

   protected interface BuilderParent {
      void markDirty();
   }
}
