package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class DynamicMessage extends AbstractMessage {
   private final Descriptors.Descriptor type;
   private final FieldSet<Descriptors.FieldDescriptor> fields;
   private final Descriptors.FieldDescriptor[] oneofCases;
   private final UnknownFieldSet unknownFields;
   private int memoizedSize = -1;

   DynamicMessage(Descriptors.Descriptor type, FieldSet<Descriptors.FieldDescriptor> fields, Descriptors.FieldDescriptor[] oneofCases, UnknownFieldSet unknownFields) {
      this.type = type;
      this.fields = fields;
      this.oneofCases = oneofCases;
      this.unknownFields = unknownFields;
   }

   public static DynamicMessage getDefaultInstance(Descriptors.Descriptor type) {
      int oneofDeclCount = type.toProto().getOneofDeclCount();
      Descriptors.FieldDescriptor[] oneofCases = new Descriptors.FieldDescriptor[oneofDeclCount];
      return new DynamicMessage(type, FieldSet.emptySet(), oneofCases, UnknownFieldSet.getDefaultInstance());
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, CodedInputStream input) throws IOException {
      return ((Builder)newBuilder(type).mergeFrom((CodedInputStream)input)).buildParsed();
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, CodedInputStream input, ExtensionRegistry extensionRegistry) throws IOException {
      return ((Builder)newBuilder(type).mergeFrom(input, extensionRegistry)).buildParsed();
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, ByteString data) throws InvalidProtocolBufferException {
      return ((Builder)newBuilder(type).mergeFrom((ByteString)data)).buildParsed();
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, ByteString data, ExtensionRegistry extensionRegistry) throws InvalidProtocolBufferException {
      return ((Builder)newBuilder(type).mergeFrom(data, extensionRegistry)).buildParsed();
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, byte[] data) throws InvalidProtocolBufferException {
      return ((Builder)newBuilder(type).mergeFrom((byte[])data)).buildParsed();
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, byte[] data, ExtensionRegistry extensionRegistry) throws InvalidProtocolBufferException {
      return ((Builder)newBuilder(type).mergeFrom(data, extensionRegistry)).buildParsed();
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, InputStream input) throws IOException {
      return ((Builder)newBuilder(type).mergeFrom((InputStream)input)).buildParsed();
   }

   public static DynamicMessage parseFrom(Descriptors.Descriptor type, InputStream input, ExtensionRegistry extensionRegistry) throws IOException {
      return ((Builder)newBuilder(type).mergeFrom(input, extensionRegistry)).buildParsed();
   }

   public static Builder newBuilder(Descriptors.Descriptor type) {
      return new Builder(type);
   }

   public static Builder newBuilder(Message prototype) {
      return (new Builder(prototype.getDescriptorForType())).mergeFrom(prototype);
   }

   public Descriptors.Descriptor getDescriptorForType() {
      return this.type;
   }

   public DynamicMessage getDefaultInstanceForType() {
      return getDefaultInstance(this.type);
   }

   public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
      return this.fields.getAllFields();
   }

   public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
      this.verifyOneofContainingType(oneof);
      Descriptors.FieldDescriptor field = this.oneofCases[oneof.getIndex()];
      return field != null;
   }

   public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
      this.verifyOneofContainingType(oneof);
      return this.oneofCases[oneof.getIndex()];
   }

   public boolean hasField(Descriptors.FieldDescriptor field) {
      this.verifyContainingType(field);
      return this.fields.hasField(field);
   }

   public Object getField(Descriptors.FieldDescriptor field) {
      this.verifyContainingType(field);
      Object result = this.fields.getField(field);
      if (result == null) {
         if (field.isRepeated()) {
            result = Collections.emptyList();
         } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            result = getDefaultInstance(field.getMessageType());
         } else {
            result = field.getDefaultValue();
         }
      }

      return result;
   }

   public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
      this.verifyContainingType(field);
      return this.fields.getRepeatedFieldCount(field);
   }

   public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
      this.verifyContainingType(field);
      return this.fields.getRepeatedField(field, index);
   }

   public UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   static boolean isInitialized(Descriptors.Descriptor type, FieldSet<Descriptors.FieldDescriptor> fields) {
      Iterator var2 = type.getFields().iterator();

      Descriptors.FieldDescriptor field;
      do {
         if (!var2.hasNext()) {
            return fields.isInitialized();
         }

         field = (Descriptors.FieldDescriptor)var2.next();
      } while(!field.isRequired() || fields.hasField(field));

      return false;
   }

   public boolean isInitialized() {
      return isInitialized(this.type, this.fields);
   }

   public void writeTo(CodedOutputStream output) throws IOException {
      if (this.type.getOptions().getMessageSetWireFormat()) {
         this.fields.writeMessageSetTo(output);
         this.unknownFields.writeAsMessageSetTo(output);
      } else {
         this.fields.writeTo(output);
         this.unknownFields.writeTo(output);
      }

   }

   public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1) {
         return size;
      } else {
         if (this.type.getOptions().getMessageSetWireFormat()) {
            size = this.fields.getMessageSetSerializedSize();
            size += this.unknownFields.getSerializedSizeAsMessageSet();
         } else {
            size = this.fields.getSerializedSize();
            size += this.unknownFields.getSerializedSize();
         }

         this.memoizedSize = size;
         return size;
      }
   }

   public Builder newBuilderForType() {
      return new Builder(this.type);
   }

   public Builder toBuilder() {
      return this.newBuilderForType().mergeFrom(this);
   }

   public Parser<DynamicMessage> getParserForType() {
      return new AbstractParser<DynamicMessage>() {
         public DynamicMessage parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            Builder builder = DynamicMessage.newBuilder(DynamicMessage.this.type);

            try {
               builder.mergeFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var5) {
               throw var5.setUnfinishedMessage(builder.buildPartial());
            } catch (IOException var6) {
               throw (new InvalidProtocolBufferException(var6)).setUnfinishedMessage(builder.buildPartial());
            }

            return builder.buildPartial();
         }
      };
   }

   private void verifyContainingType(Descriptors.FieldDescriptor field) {
      if (field.getContainingType() != this.type) {
         throw new IllegalArgumentException("FieldDescriptor does not match message type.");
      }
   }

   private void verifyOneofContainingType(Descriptors.OneofDescriptor oneof) {
      if (oneof.getContainingType() != this.type) {
         throw new IllegalArgumentException("OneofDescriptor does not match message type.");
      }
   }

   public static final class Builder extends AbstractMessage.Builder<Builder> {
      private final Descriptors.Descriptor type;
      private FieldSet<Descriptors.FieldDescriptor> fields;
      private final Descriptors.FieldDescriptor[] oneofCases;
      private UnknownFieldSet unknownFields;

      private Builder(Descriptors.Descriptor type) {
         this.type = type;
         this.fields = FieldSet.newFieldSet();
         this.unknownFields = UnknownFieldSet.getDefaultInstance();
         this.oneofCases = new Descriptors.FieldDescriptor[type.toProto().getOneofDeclCount()];
         if (type.getOptions().getMapEntry()) {
            this.populateMapEntry();
         }

      }

      private void populateMapEntry() {
         Iterator var1 = this.type.getFields().iterator();

         while(var1.hasNext()) {
            Descriptors.FieldDescriptor field = (Descriptors.FieldDescriptor)var1.next();
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
               this.fields.setField(field, DynamicMessage.getDefaultInstance(field.getMessageType()));
            } else {
               this.fields.setField(field, field.getDefaultValue());
            }
         }

      }

      public Builder clear() {
         if (this.fields.isImmutable()) {
            this.fields = FieldSet.newFieldSet();
         } else {
            this.fields.clear();
         }

         if (this.type.getOptions().getMapEntry()) {
            this.populateMapEntry();
         }

         this.unknownFields = UnknownFieldSet.getDefaultInstance();
         return this;
      }

      public Builder mergeFrom(Message other) {
         if (other instanceof DynamicMessage) {
            DynamicMessage otherDynamicMessage = (DynamicMessage)other;
            if (otherDynamicMessage.type != this.type) {
               throw new IllegalArgumentException("mergeFrom(Message) can only merge messages of the same type.");
            } else {
               this.ensureIsMutable();
               this.fields.mergeFrom(otherDynamicMessage.fields);
               this.mergeUnknownFields(otherDynamicMessage.unknownFields);

               for(int i = 0; i < this.oneofCases.length; ++i) {
                  if (this.oneofCases[i] == null) {
                     this.oneofCases[i] = otherDynamicMessage.oneofCases[i];
                  } else if (otherDynamicMessage.oneofCases[i] != null && this.oneofCases[i] != otherDynamicMessage.oneofCases[i]) {
                     this.fields.clearField(this.oneofCases[i]);
                     this.oneofCases[i] = otherDynamicMessage.oneofCases[i];
                  }
               }

               return this;
            }
         } else {
            return (Builder)super.mergeFrom(other);
         }
      }

      public DynamicMessage build() {
         if (!this.isInitialized()) {
            throw newUninitializedMessageException(new DynamicMessage(this.type, this.fields, (Descriptors.FieldDescriptor[])Arrays.copyOf(this.oneofCases, this.oneofCases.length), this.unknownFields));
         } else {
            return this.buildPartial();
         }
      }

      private DynamicMessage buildParsed() throws InvalidProtocolBufferException {
         if (!this.isInitialized()) {
            throw newUninitializedMessageException(new DynamicMessage(this.type, this.fields, (Descriptors.FieldDescriptor[])Arrays.copyOf(this.oneofCases, this.oneofCases.length), this.unknownFields)).asInvalidProtocolBufferException();
         } else {
            return this.buildPartial();
         }
      }

      public DynamicMessage buildPartial() {
         this.fields.makeImmutable();
         DynamicMessage result = new DynamicMessage(this.type, this.fields, (Descriptors.FieldDescriptor[])Arrays.copyOf(this.oneofCases, this.oneofCases.length), this.unknownFields);
         return result;
      }

      public Builder clone() {
         Builder result = new Builder(this.type);
         result.fields.mergeFrom(this.fields);
         result.mergeUnknownFields(this.unknownFields);
         System.arraycopy(this.oneofCases, 0, result.oneofCases, 0, this.oneofCases.length);
         return result;
      }

      public boolean isInitialized() {
         return DynamicMessage.isInitialized(this.type, this.fields);
      }

      public Descriptors.Descriptor getDescriptorForType() {
         return this.type;
      }

      public DynamicMessage getDefaultInstanceForType() {
         return DynamicMessage.getDefaultInstance(this.type);
      }

      public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
         return this.fields.getAllFields();
      }

      public Builder newBuilderForField(Descriptors.FieldDescriptor field) {
         this.verifyContainingType(field);
         if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            throw new IllegalArgumentException("newBuilderForField is only valid for fields with message type.");
         } else {
            return new Builder(field.getMessageType());
         }
      }

      public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
         this.verifyOneofContainingType(oneof);
         Descriptors.FieldDescriptor field = this.oneofCases[oneof.getIndex()];
         return field != null;
      }

      public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
         this.verifyOneofContainingType(oneof);
         return this.oneofCases[oneof.getIndex()];
      }

      public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
         this.verifyOneofContainingType(oneof);
         Descriptors.FieldDescriptor field = this.oneofCases[oneof.getIndex()];
         if (field != null) {
            this.clearField(field);
         }

         return this;
      }

      public boolean hasField(Descriptors.FieldDescriptor field) {
         this.verifyContainingType(field);
         return this.fields.hasField(field);
      }

      public Object getField(Descriptors.FieldDescriptor field) {
         this.verifyContainingType(field);
         Object result = this.fields.getField(field);
         if (result == null) {
            if (field.isRepeated()) {
               result = Collections.emptyList();
            } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
               result = DynamicMessage.getDefaultInstance(field.getMessageType());
            } else {
               result = field.getDefaultValue();
            }
         }

         return result;
      }

      public Builder setField(Descriptors.FieldDescriptor field, Object value) {
         this.verifyContainingType(field);
         this.ensureIsMutable();
         if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
            this.ensureEnumValueDescriptor(field, value);
         }

         Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
         if (oneofDescriptor != null) {
            int index = oneofDescriptor.getIndex();
            Descriptors.FieldDescriptor oldField = this.oneofCases[index];
            if (oldField != null && oldField != field) {
               this.fields.clearField(oldField);
            }

            this.oneofCases[index] = field;
         } else if (field.getFile().getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO3 && !field.isRepeated() && field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE && value.equals(field.getDefaultValue())) {
            this.fields.clearField(field);
            return this;
         }

         this.fields.setField(field, value);
         return this;
      }

      public Builder clearField(Descriptors.FieldDescriptor field) {
         this.verifyContainingType(field);
         this.ensureIsMutable();
         Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
         if (oneofDescriptor != null) {
            int index = oneofDescriptor.getIndex();
            if (this.oneofCases[index] == field) {
               this.oneofCases[index] = null;
            }
         }

         this.fields.clearField(field);
         return this;
      }

      public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
         this.verifyContainingType(field);
         return this.fields.getRepeatedFieldCount(field);
      }

      public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
         this.verifyContainingType(field);
         return this.fields.getRepeatedField(field, index);
      }

      public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
         this.verifyContainingType(field);
         this.ensureIsMutable();
         this.fields.setRepeatedField(field, index, value);
         return this;
      }

      public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
         this.verifyContainingType(field);
         this.ensureIsMutable();
         this.fields.addRepeatedField(field, value);
         return this;
      }

      public UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      public Builder setUnknownFields(UnknownFieldSet unknownFields) {
         this.unknownFields = unknownFields;
         return this;
      }

      public Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
         this.unknownFields = UnknownFieldSet.newBuilder(this.unknownFields).mergeFrom(unknownFields).build();
         return this;
      }

      private void verifyContainingType(Descriptors.FieldDescriptor field) {
         if (field.getContainingType() != this.type) {
            throw new IllegalArgumentException("FieldDescriptor does not match message type.");
         }
      }

      private void verifyOneofContainingType(Descriptors.OneofDescriptor oneof) {
         if (oneof.getContainingType() != this.type) {
            throw new IllegalArgumentException("OneofDescriptor does not match message type.");
         }
      }

      private void ensureSingularEnumValueDescriptor(Descriptors.FieldDescriptor field, Object value) {
         Internal.checkNotNull(value);
         if (!(value instanceof Descriptors.EnumValueDescriptor)) {
            throw new IllegalArgumentException("DynamicMessage should use EnumValueDescriptor to set Enum Value.");
         }
      }

      private void ensureEnumValueDescriptor(Descriptors.FieldDescriptor field, Object value) {
         if (field.isRepeated()) {
            Iterator var3 = ((List)value).iterator();

            while(var3.hasNext()) {
               Object item = var3.next();
               this.ensureSingularEnumValueDescriptor(field, item);
            }
         } else {
            this.ensureSingularEnumValueDescriptor(field, value);
         }

      }

      private void ensureIsMutable() {
         if (this.fields.isImmutable()) {
            this.fields = this.fields.clone();
         }

      }

      public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
         throw new UnsupportedOperationException("getFieldBuilder() called on a dynamic message type.");
      }

      public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
         throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a dynamic message type.");
      }

      // $FF: synthetic method
      Builder(Descriptors.Descriptor x0, Object x1) {
         this(x0);
      }
   }
}
