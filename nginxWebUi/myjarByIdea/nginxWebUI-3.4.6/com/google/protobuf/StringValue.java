package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class StringValue extends GeneratedMessageV3 implements StringValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VALUE_FIELD_NUMBER = 1;
   private volatile Object value_;
   private byte memoizedIsInitialized;
   private static final StringValue DEFAULT_INSTANCE = new StringValue();
   private static final Parser<StringValue> PARSER = new AbstractParser<StringValue>() {
      public StringValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return new StringValue(input, extensionRegistry);
      }
   };

   private StringValue(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
   }

   private StringValue() {
      this.memoizedIsInitialized = -1;
      this.value_ = "";
   }

   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new StringValue();
   }

   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private StringValue(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                     String s = input.readStringRequireUtf8();
                     this.value_ = s;
                     break;
                  default:
                     if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                        done = true;
                     }
               }
            }
         } catch (InvalidProtocolBufferException var11) {
            throw var11.setUnfinishedMessage(this);
         } catch (IOException var12) {
            throw (new InvalidProtocolBufferException(var12)).setUnfinishedMessage(this);
         } finally {
            this.unknownFields = unknownFields.build();
            this.makeExtensionsImmutable();
         }

      }
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return WrappersProto.internal_static_google_protobuf_StringValue_descriptor;
   }

   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_StringValue_fieldAccessorTable.ensureFieldAccessorsInitialized(StringValue.class, Builder.class);
   }

   public String getValue() {
      Object ref = this.value_;
      if (ref instanceof String) {
         return (String)ref;
      } else {
         ByteString bs = (ByteString)ref;
         String s = bs.toStringUtf8();
         this.value_ = s;
         return s;
      }
   }

   public ByteString getValueBytes() {
      Object ref = this.value_;
      if (ref instanceof String) {
         ByteString b = ByteString.copyFromUtf8((String)ref);
         this.value_ = b;
         return b;
      } else {
         return (ByteString)ref;
      }
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
      if (!this.getValueBytes().isEmpty()) {
         GeneratedMessageV3.writeString(output, 1, this.value_);
      }

      this.unknownFields.writeTo(output);
   }

   public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1) {
         return size;
      } else {
         size = 0;
         if (!this.getValueBytes().isEmpty()) {
            size += GeneratedMessageV3.computeStringSize(1, this.value_);
         }

         size += this.unknownFields.getSerializedSize();
         this.memoizedSize = size;
         return size;
      }
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof StringValue)) {
         return super.equals(obj);
      } else {
         StringValue other = (StringValue)obj;
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

   public static StringValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (StringValue)PARSER.parseFrom(data);
   }

   public static StringValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (StringValue)PARSER.parseFrom(data, extensionRegistry);
   }

   public static StringValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (StringValue)PARSER.parseFrom(data);
   }

   public static StringValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (StringValue)PARSER.parseFrom(data, extensionRegistry);
   }

   public static StringValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (StringValue)PARSER.parseFrom(data);
   }

   public static StringValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (StringValue)PARSER.parseFrom(data, extensionRegistry);
   }

   public static StringValue parseFrom(InputStream input) throws IOException {
      return (StringValue)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static StringValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (StringValue)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static StringValue parseDelimitedFrom(InputStream input) throws IOException {
      return (StringValue)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
   }

   public static StringValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (StringValue)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static StringValue parseFrom(CodedInputStream input) throws IOException {
      return (StringValue)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static StringValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (StringValue)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Builder newBuilderForType() {
      return newBuilder();
   }

   public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Builder newBuilder(StringValue prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
   }

   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
   }

   public static StringValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static StringValue of(String value) {
      return newBuilder().setValue(value).build();
   }

   public static Parser<StringValue> parser() {
      return PARSER;
   }

   public Parser<StringValue> getParserForType() {
      return PARSER;
   }

   public StringValue getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   // $FF: synthetic method
   StringValue(GeneratedMessageV3.Builder x0, Object x1) {
      this(x0);
   }

   // $FF: synthetic method
   StringValue(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
      this(x0, x1);
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements StringValueOrBuilder {
      private Object value_;

      public static final Descriptors.Descriptor getDescriptor() {
         return WrappersProto.internal_static_google_protobuf_StringValue_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return WrappersProto.internal_static_google_protobuf_StringValue_fieldAccessorTable.ensureFieldAccessorsInitialized(StringValue.class, Builder.class);
      }

      private Builder() {
         this.value_ = "";
         this.maybeForceBuilderInitialization();
      }

      private Builder(GeneratedMessageV3.BuilderParent parent) {
         super(parent);
         this.value_ = "";
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (GeneratedMessageV3.alwaysUseFieldBuilders) {
         }

      }

      public Builder clear() {
         super.clear();
         this.value_ = "";
         return this;
      }

      public Descriptors.Descriptor getDescriptorForType() {
         return WrappersProto.internal_static_google_protobuf_StringValue_descriptor;
      }

      public StringValue getDefaultInstanceForType() {
         return StringValue.getDefaultInstance();
      }

      public StringValue build() {
         StringValue result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public StringValue buildPartial() {
         StringValue result = new StringValue(this);
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
         if (other instanceof StringValue) {
            return this.mergeFrom((StringValue)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Builder mergeFrom(StringValue other) {
         if (other == StringValue.getDefaultInstance()) {
            return this;
         } else {
            if (!other.getValue().isEmpty()) {
               this.value_ = other.value_;
               this.onChanged();
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
         StringValue parsedMessage = null;

         try {
            parsedMessage = (StringValue)StringValue.PARSER.parsePartialFrom(input, extensionRegistry);
         } catch (InvalidProtocolBufferException var8) {
            parsedMessage = (StringValue)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (parsedMessage != null) {
               this.mergeFrom(parsedMessage);
            }

         }

         return this;
      }

      public String getValue() {
         Object ref = this.value_;
         if (!(ref instanceof String)) {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.value_ = s;
            return s;
         } else {
            return (String)ref;
         }
      }

      public ByteString getValueBytes() {
         Object ref = this.value_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.value_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public Builder setValue(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.value_ = value;
            this.onChanged();
            return this;
         }
      }

      public Builder clearValue() {
         this.value_ = StringValue.getDefaultInstance().getValue();
         this.onChanged();
         return this;
      }

      public Builder setValueBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.value_ = value;
            this.onChanged();
            return this;
         }
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
