package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class FloatValue extends GeneratedMessageV3 implements FloatValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VALUE_FIELD_NUMBER = 1;
   private float value_;
   private byte memoizedIsInitialized;
   private static final FloatValue DEFAULT_INSTANCE = new FloatValue();
   private static final Parser<FloatValue> PARSER = new AbstractParser<FloatValue>() {
      public FloatValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return new FloatValue(input, extensionRegistry);
      }
   };

   private FloatValue(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
   }

   private FloatValue() {
      this.memoizedIsInitialized = -1;
   }

   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new FloatValue();
   }

   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private FloatValue(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                  case 13:
                     this.value_ = input.readFloat();
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
      return WrappersProto.internal_static_google_protobuf_FloatValue_descriptor;
   }

   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_FloatValue_fieldAccessorTable.ensureFieldAccessorsInitialized(FloatValue.class, Builder.class);
   }

   public float getValue() {
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
      if (this.value_ != 0.0F) {
         output.writeFloat(1, this.value_);
      }

      this.unknownFields.writeTo(output);
   }

   public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1) {
         return size;
      } else {
         size = 0;
         if (this.value_ != 0.0F) {
            size += CodedOutputStream.computeFloatSize(1, this.value_);
         }

         size += this.unknownFields.getSerializedSize();
         this.memoizedSize = size;
         return size;
      }
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof FloatValue)) {
         return super.equals(obj);
      } else {
         FloatValue other = (FloatValue)obj;
         if (Float.floatToIntBits(this.getValue()) != Float.floatToIntBits(other.getValue())) {
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
         hash = 53 * hash + Float.floatToIntBits(this.getValue());
         hash = 29 * hash + this.unknownFields.hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static FloatValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (FloatValue)PARSER.parseFrom(data);
   }

   public static FloatValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FloatValue)PARSER.parseFrom(data, extensionRegistry);
   }

   public static FloatValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (FloatValue)PARSER.parseFrom(data);
   }

   public static FloatValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FloatValue)PARSER.parseFrom(data, extensionRegistry);
   }

   public static FloatValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (FloatValue)PARSER.parseFrom(data);
   }

   public static FloatValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (FloatValue)PARSER.parseFrom(data, extensionRegistry);
   }

   public static FloatValue parseFrom(InputStream input) throws IOException {
      return (FloatValue)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static FloatValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (FloatValue)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static FloatValue parseDelimitedFrom(InputStream input) throws IOException {
      return (FloatValue)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
   }

   public static FloatValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (FloatValue)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static FloatValue parseFrom(CodedInputStream input) throws IOException {
      return (FloatValue)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static FloatValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (FloatValue)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Builder newBuilderForType() {
      return newBuilder();
   }

   public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Builder newBuilder(FloatValue prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
   }

   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
   }

   public static FloatValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static FloatValue of(float value) {
      return newBuilder().setValue(value).build();
   }

   public static Parser<FloatValue> parser() {
      return PARSER;
   }

   public Parser<FloatValue> getParserForType() {
      return PARSER;
   }

   public FloatValue getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   // $FF: synthetic method
   FloatValue(GeneratedMessageV3.Builder x0, Object x1) {
      this(x0);
   }

   // $FF: synthetic method
   FloatValue(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
      this(x0, x1);
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements FloatValueOrBuilder {
      private float value_;

      public static final Descriptors.Descriptor getDescriptor() {
         return WrappersProto.internal_static_google_protobuf_FloatValue_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return WrappersProto.internal_static_google_protobuf_FloatValue_fieldAccessorTable.ensureFieldAccessorsInitialized(FloatValue.class, Builder.class);
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
         this.value_ = 0.0F;
         return this;
      }

      public Descriptors.Descriptor getDescriptorForType() {
         return WrappersProto.internal_static_google_protobuf_FloatValue_descriptor;
      }

      public FloatValue getDefaultInstanceForType() {
         return FloatValue.getDefaultInstance();
      }

      public FloatValue build() {
         FloatValue result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public FloatValue buildPartial() {
         FloatValue result = new FloatValue(this);
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
         if (other instanceof FloatValue) {
            return this.mergeFrom((FloatValue)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Builder mergeFrom(FloatValue other) {
         if (other == FloatValue.getDefaultInstance()) {
            return this;
         } else {
            if (other.getValue() != 0.0F) {
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
         FloatValue parsedMessage = null;

         try {
            parsedMessage = (FloatValue)FloatValue.PARSER.parsePartialFrom(input, extensionRegistry);
         } catch (InvalidProtocolBufferException var8) {
            parsedMessage = (FloatValue)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (parsedMessage != null) {
               this.mergeFrom(parsedMessage);
            }

         }

         return this;
      }

      public float getValue() {
         return this.value_;
      }

      public Builder setValue(float value) {
         this.value_ = value;
         this.onChanged();
         return this;
      }

      public Builder clearValue() {
         this.value_ = 0.0F;
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
