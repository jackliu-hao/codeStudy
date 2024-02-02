package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Int32Value extends GeneratedMessageV3 implements Int32ValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VALUE_FIELD_NUMBER = 1;
   private int value_;
   private byte memoizedIsInitialized;
   private static final Int32Value DEFAULT_INSTANCE = new Int32Value();
   private static final Parser<Int32Value> PARSER = new AbstractParser<Int32Value>() {
      public Int32Value parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return new Int32Value(input, extensionRegistry);
      }
   };

   private Int32Value(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
   }

   private Int32Value() {
      this.memoizedIsInitialized = -1;
   }

   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Int32Value();
   }

   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Int32Value(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                  case 8:
                     this.value_ = input.readInt32();
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
      return WrappersProto.internal_static_google_protobuf_Int32Value_descriptor;
   }

   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_Int32Value_fieldAccessorTable.ensureFieldAccessorsInitialized(Int32Value.class, Builder.class);
   }

   public int getValue() {
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
      if (this.value_ != 0) {
         output.writeInt32(1, this.value_);
      }

      this.unknownFields.writeTo(output);
   }

   public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1) {
         return size;
      } else {
         size = 0;
         if (this.value_ != 0) {
            size += CodedOutputStream.computeInt32Size(1, this.value_);
         }

         size += this.unknownFields.getSerializedSize();
         this.memoizedSize = size;
         return size;
      }
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Int32Value)) {
         return super.equals(obj);
      } else {
         Int32Value other = (Int32Value)obj;
         if (this.getValue() != other.getValue()) {
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
         hash = 53 * hash + this.getValue();
         hash = 29 * hash + this.unknownFields.hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static Int32Value parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Int32Value)PARSER.parseFrom(data);
   }

   public static Int32Value parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Int32Value)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Int32Value parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Int32Value)PARSER.parseFrom(data);
   }

   public static Int32Value parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Int32Value)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Int32Value parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Int32Value)PARSER.parseFrom(data);
   }

   public static Int32Value parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Int32Value)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Int32Value parseFrom(InputStream input) throws IOException {
      return (Int32Value)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static Int32Value parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Int32Value)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static Int32Value parseDelimitedFrom(InputStream input) throws IOException {
      return (Int32Value)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
   }

   public static Int32Value parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Int32Value)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static Int32Value parseFrom(CodedInputStream input) throws IOException {
      return (Int32Value)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static Int32Value parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Int32Value)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Builder newBuilderForType() {
      return newBuilder();
   }

   public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Builder newBuilder(Int32Value prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
   }

   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
   }

   public static Int32Value getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Int32Value of(int value) {
      return newBuilder().setValue(value).build();
   }

   public static Parser<Int32Value> parser() {
      return PARSER;
   }

   public Parser<Int32Value> getParserForType() {
      return PARSER;
   }

   public Int32Value getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   // $FF: synthetic method
   Int32Value(GeneratedMessageV3.Builder x0, Object x1) {
      this(x0);
   }

   // $FF: synthetic method
   Int32Value(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
      this(x0, x1);
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements Int32ValueOrBuilder {
      private int value_;

      public static final Descriptors.Descriptor getDescriptor() {
         return WrappersProto.internal_static_google_protobuf_Int32Value_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return WrappersProto.internal_static_google_protobuf_Int32Value_fieldAccessorTable.ensureFieldAccessorsInitialized(Int32Value.class, Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(GeneratedMessageV3.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (GeneratedMessageV3.alwaysUseFieldBuilders) {
         }

      }

      public Builder clear() {
         super.clear();
         this.value_ = 0;
         return this;
      }

      public Descriptors.Descriptor getDescriptorForType() {
         return WrappersProto.internal_static_google_protobuf_Int32Value_descriptor;
      }

      public Int32Value getDefaultInstanceForType() {
         return Int32Value.getDefaultInstance();
      }

      public Int32Value build() {
         Int32Value result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public Int32Value buildPartial() {
         Int32Value result = new Int32Value(this);
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
         if (other instanceof Int32Value) {
            return this.mergeFrom((Int32Value)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Builder mergeFrom(Int32Value other) {
         if (other == Int32Value.getDefaultInstance()) {
            return this;
         } else {
            if (other.getValue() != 0) {
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
         Int32Value parsedMessage = null;

         try {
            parsedMessage = (Int32Value)Int32Value.PARSER.parsePartialFrom(input, extensionRegistry);
         } catch (InvalidProtocolBufferException var8) {
            parsedMessage = (Int32Value)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (parsedMessage != null) {
               this.mergeFrom(parsedMessage);
            }

         }

         return this;
      }

      public int getValue() {
         return this.value_;
      }

      public Builder setValue(int value) {
         this.value_ = value;
         this.onChanged();
         return this;
      }

      public Builder clearValue() {
         this.value_ = 0;
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
