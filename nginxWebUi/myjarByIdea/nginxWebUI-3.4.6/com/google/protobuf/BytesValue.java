package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class BytesValue extends GeneratedMessageV3 implements BytesValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VALUE_FIELD_NUMBER = 1;
   private ByteString value_;
   private byte memoizedIsInitialized;
   private static final BytesValue DEFAULT_INSTANCE = new BytesValue();
   private static final Parser<BytesValue> PARSER = new AbstractParser<BytesValue>() {
      public BytesValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return new BytesValue(input, extensionRegistry);
      }
   };

   private BytesValue(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
   }

   private BytesValue() {
      this.memoizedIsInitialized = -1;
      this.value_ = ByteString.EMPTY;
   }

   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new BytesValue();
   }

   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private BytesValue(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
         throw new NullPointerException();
      } else {
         UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

         try {
            boolean done = false;

            while(!done) {
               int tag = input.readTag();
               switch (tag) {
                  case 0:
                     done = true;
                     break;
                  case 10:
                     this.value_ = input.readBytes();
                     break;
                  default:
                     if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                        done = true;
                     }
               }
            }
         } catch (InvalidProtocolBufferException var10) {
            throw var10.setUnfinishedMessage(this);
         } catch (IOException var11) {
            throw (new InvalidProtocolBufferException(var11)).setUnfinishedMessage(this);
         } finally {
            this.unknownFields = unknownFields.build();
            this.makeExtensionsImmutable();
         }

      }
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return WrappersProto.internal_static_google_protobuf_BytesValue_descriptor;
   }

   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_BytesValue_fieldAccessorTable.ensureFieldAccessorsInitialized(BytesValue.class, Builder.class);
   }

   public ByteString getValue() {
      return this.value_;
   }

   public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1) {
         return true;
      } else if (isInitialized == 0) {
         return false;
      } else {
         this.memoizedIsInitialized = 1;
         return true;
      }
   }

   public void writeTo(CodedOutputStream output) throws IOException {
      if (!this.value_.isEmpty()) {
         output.writeBytes(1, this.value_);
      }

      this.unknownFields.writeTo(output);
   }

   public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1) {
         return size;
      } else {
         size = 0;
         if (!this.value_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(1, this.value_);
         }

         size += this.unknownFields.getSerializedSize();
         this.memoizedSize = size;
         return size;
      }
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof BytesValue)) {
         return super.equals(obj);
      } else {
         BytesValue other = (BytesValue)obj;
         if (!this.getValue().equals(other.getValue())) {
            return false;
         } else {
            return this.unknownFields.equals(other.unknownFields);
         }
      }
   }

   public int hashCode() {
      if (this.memoizedHashCode != 0) {
         return this.memoizedHashCode;
      } else {
         int hash = 41;
         hash = 19 * hash + getDescriptor().hashCode();
         hash = 37 * hash + 1;
         hash = 53 * hash + this.getValue().hashCode();
         hash = 29 * hash + this.unknownFields.hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static BytesValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (BytesValue)PARSER.parseFrom(data);
   }

   public static BytesValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (BytesValue)PARSER.parseFrom(data, extensionRegistry);
   }

   public static BytesValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (BytesValue)PARSER.parseFrom(data);
   }

   public static BytesValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (BytesValue)PARSER.parseFrom(data, extensionRegistry);
   }

   public static BytesValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (BytesValue)PARSER.parseFrom(data);
   }

   public static BytesValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (BytesValue)PARSER.parseFrom(data, extensionRegistry);
   }

   public static BytesValue parseFrom(InputStream input) throws IOException {
      return (BytesValue)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static BytesValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (BytesValue)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static BytesValue parseDelimitedFrom(InputStream input) throws IOException {
      return (BytesValue)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
   }

   public static BytesValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (BytesValue)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static BytesValue parseFrom(CodedInputStream input) throws IOException {
      return (BytesValue)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static BytesValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (BytesValue)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Builder newBuilderForType() {
      return newBuilder();
   }

   public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Builder newBuilder(BytesValue prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
   }

   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
   }

   public static BytesValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static BytesValue of(ByteString value) {
      return newBuilder().setValue(value).build();
   }

   public static Parser<BytesValue> parser() {
      return PARSER;
   }

   public Parser<BytesValue> getParserForType() {
      return PARSER;
   }

   public BytesValue getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   // $FF: synthetic method
   BytesValue(GeneratedMessageV3.Builder x0, Object x1) {
      this(x0);
   }

   // $FF: synthetic method
   BytesValue(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
      this(x0, x1);
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements BytesValueOrBuilder {
      private ByteString value_;

      public static final Descriptors.Descriptor getDescriptor() {
         return WrappersProto.internal_static_google_protobuf_BytesValue_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return WrappersProto.internal_static_google_protobuf_BytesValue_fieldAccessorTable.ensureFieldAccessorsInitialized(BytesValue.class, Builder.class);
      }

      private Builder() {
         this.value_ = ByteString.EMPTY;
         this.maybeForceBuilderInitialization();
      }

      private Builder(GeneratedMessageV3.BuilderParent parent) {
         super(parent);
         this.value_ = ByteString.EMPTY;
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (GeneratedMessageV3.alwaysUseFieldBuilders) {
         }

      }

      public Builder clear() {
         super.clear();
         this.value_ = ByteString.EMPTY;
         return this;
      }

      public Descriptors.Descriptor getDescriptorForType() {
         return WrappersProto.internal_static_google_protobuf_BytesValue_descriptor;
      }

      public BytesValue getDefaultInstanceForType() {
         return BytesValue.getDefaultInstance();
      }

      public BytesValue build() {
         BytesValue result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public BytesValue buildPartial() {
         BytesValue result = new BytesValue(this);
         result.value_ = this.value_;
         this.onBuilt();
         return result;
      }

      public Builder clone() {
         return (Builder)super.clone();
      }

      public Builder setField(Descriptors.FieldDescriptor field, Object value) {
         return (Builder)super.setField(field, value);
      }

      public Builder clearField(Descriptors.FieldDescriptor field) {
         return (Builder)super.clearField(field);
      }

      public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
         return (Builder)super.clearOneof(oneof);
      }

      public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
         return (Builder)super.setRepeatedField(field, index, value);
      }

      public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
         return (Builder)super.addRepeatedField(field, value);
      }

      public Builder mergeFrom(Message other) {
         if (other instanceof BytesValue) {
            return this.mergeFrom((BytesValue)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Builder mergeFrom(BytesValue other) {
         if (other == BytesValue.getDefaultInstance()) {
            return this;
         } else {
            if (other.getValue() != ByteString.EMPTY) {
               this.setValue(other.getValue());
            }

            this.mergeUnknownFields(other.unknownFields);
            this.onChanged();
            return this;
         }
      }

      public final boolean isInitialized() {
         return true;
      }

      public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         BytesValue parsedMessage = null;

         try {
            parsedMessage = (BytesValue)BytesValue.PARSER.parsePartialFrom(input, extensionRegistry);
         } catch (InvalidProtocolBufferException var8) {
            parsedMessage = (BytesValue)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (parsedMessage != null) {
               this.mergeFrom(parsedMessage);
            }

         }

         return this;
      }

      public ByteString getValue() {
         return this.value_;
      }

      public Builder setValue(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.value_ = value;
            this.onChanged();
            return this;
         }
      }

      public Builder clearValue() {
         this.value_ = BytesValue.getDefaultInstance().getValue();
         this.onChanged();
         return this;
      }

      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
         return (Builder)super.setUnknownFields(unknownFields);
      }

      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
         return (Builder)super.mergeUnknownFields(unknownFields);
      }

      // $FF: synthetic method
      Builder(Object x0) {
         this();
      }

      // $FF: synthetic method
      Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
         this(x0);
      }
   }
}
