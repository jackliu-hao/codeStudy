package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Option extends GeneratedMessageV3 implements OptionOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int NAME_FIELD_NUMBER = 1;
   private volatile Object name_;
   public static final int VALUE_FIELD_NUMBER = 2;
   private Any value_;
   private byte memoizedIsInitialized;
   private static final Option DEFAULT_INSTANCE = new Option();
   private static final Parser<Option> PARSER = new AbstractParser<Option>() {
      public Option parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return new Option(input, extensionRegistry);
      }
   };

   private Option(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
   }

   private Option() {
      this.memoizedIsInitialized = -1;
      this.name_ = "";
   }

   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Option();
   }

   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Option(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                     this.name_ = s;
                     break;
                  case 18:
                     Any.Builder subBuilder = null;
                     if (this.value_ != null) {
                        subBuilder = this.value_.toBuilder();
                     }

                     this.value_ = (Any)input.readMessage(Any.parser(), extensionRegistry);
                     if (subBuilder != null) {
                        subBuilder.mergeFrom(this.value_);
                        this.value_ = subBuilder.buildPartial();
                     }
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
      return TypeProto.internal_static_google_protobuf_Option_descriptor;
   }

   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return TypeProto.internal_static_google_protobuf_Option_fieldAccessorTable.ensureFieldAccessorsInitialized(Option.class, Builder.class);
   }

   public String getName() {
      Object ref = this.name_;
      if (ref instanceof String) {
         return (String)ref;
      } else {
         ByteString bs = (ByteString)ref;
         String s = bs.toStringUtf8();
         this.name_ = s;
         return s;
      }
   }

   public ByteString getNameBytes() {
      Object ref = this.name_;
      if (ref instanceof String) {
         ByteString b = ByteString.copyFromUtf8((String)ref);
         this.name_ = b;
         return b;
      } else {
         return (ByteString)ref;
      }
   }

   public boolean hasValue() {
      return this.value_ != null;
   }

   public Any getValue() {
      return this.value_ == null ? Any.getDefaultInstance() : this.value_;
   }

   public AnyOrBuilder getValueOrBuilder() {
      return this.getValue();
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
      if (!this.getNameBytes().isEmpty()) {
         GeneratedMessageV3.writeString(output, 1, this.name_);
      }

      if (this.value_ != null) {
         output.writeMessage(2, this.getValue());
      }

      this.unknownFields.writeTo(output);
   }

   public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1) {
         return size;
      } else {
         size = 0;
         if (!this.getNameBytes().isEmpty()) {
            size += GeneratedMessageV3.computeStringSize(1, this.name_);
         }

         if (this.value_ != null) {
            size += CodedOutputStream.computeMessageSize(2, this.getValue());
         }

         size += this.unknownFields.getSerializedSize();
         this.memoizedSize = size;
         return size;
      }
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Option)) {
         return super.equals(obj);
      } else {
         Option other = (Option)obj;
         if (!this.getName().equals(other.getName())) {
            return false;
         } else if (this.hasValue() != other.hasValue()) {
            return false;
         } else if (this.hasValue() && !this.getValue().equals(other.getValue())) {
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
         hash = 53 * hash + this.getName().hashCode();
         if (this.hasValue()) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getValue().hashCode();
         }

         hash = 29 * hash + this.unknownFields.hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static Option parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Option)PARSER.parseFrom(data);
   }

   public static Option parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Option)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Option parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Option)PARSER.parseFrom(data);
   }

   public static Option parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Option)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Option parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Option)PARSER.parseFrom(data);
   }

   public static Option parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Option)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Option parseFrom(InputStream input) throws IOException {
      return (Option)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static Option parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Option)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static Option parseDelimitedFrom(InputStream input) throws IOException {
      return (Option)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
   }

   public static Option parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Option)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static Option parseFrom(CodedInputStream input) throws IOException {
      return (Option)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static Option parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Option)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Builder newBuilderForType() {
      return newBuilder();
   }

   public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Builder newBuilder(Option prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
   }

   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
   }

   public static Option getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Option> parser() {
      return PARSER;
   }

   public Parser<Option> getParserForType() {
      return PARSER;
   }

   public Option getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   // $FF: synthetic method
   Option(GeneratedMessageV3.Builder x0, Object x1) {
      this(x0);
   }

   // $FF: synthetic method
   Option(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
      this(x0, x1);
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements OptionOrBuilder {
      private Object name_;
      private Any value_;
      private SingleFieldBuilderV3<Any, Any.Builder, AnyOrBuilder> valueBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return TypeProto.internal_static_google_protobuf_Option_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return TypeProto.internal_static_google_protobuf_Option_fieldAccessorTable.ensureFieldAccessorsInitialized(Option.class, Builder.class);
      }

      private Builder() {
         this.name_ = "";
         this.maybeForceBuilderInitialization();
      }

      private Builder(GeneratedMessageV3.BuilderParent parent) {
         super(parent);
         this.name_ = "";
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (GeneratedMessageV3.alwaysUseFieldBuilders) {
         }

      }

      public Builder clear() {
         super.clear();
         this.name_ = "";
         if (this.valueBuilder_ == null) {
            this.value_ = null;
         } else {
            this.value_ = null;
            this.valueBuilder_ = null;
         }

         return this;
      }

      public Descriptors.Descriptor getDescriptorForType() {
         return TypeProto.internal_static_google_protobuf_Option_descriptor;
      }

      public Option getDefaultInstanceForType() {
         return Option.getDefaultInstance();
      }

      public Option build() {
         Option result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public Option buildPartial() {
         Option result = new Option(this);
         result.name_ = this.name_;
         if (this.valueBuilder_ == null) {
            result.value_ = this.value_;
         } else {
            result.value_ = (Any)this.valueBuilder_.build();
         }

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
         if (other instanceof Option) {
            return this.mergeFrom((Option)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Builder mergeFrom(Option other) {
         if (other == Option.getDefaultInstance()) {
            return this;
         } else {
            if (!other.getName().isEmpty()) {
               this.name_ = other.name_;
               this.onChanged();
            }

            if (other.hasValue()) {
               this.mergeValue(other.getValue());
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
         Option parsedMessage = null;

         try {
            parsedMessage = (Option)Option.PARSER.parsePartialFrom(input, extensionRegistry);
         } catch (InvalidProtocolBufferException var8) {
            parsedMessage = (Option)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (parsedMessage != null) {
               this.mergeFrom(parsedMessage);
            }

         }

         return this;
      }

      public String getName() {
         Object ref = this.name_;
         if (!(ref instanceof String)) {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.name_ = s;
            return s;
         } else {
            return (String)ref;
         }
      }

      public ByteString getNameBytes() {
         Object ref = this.name_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.name_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public Builder setName(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.name_ = value;
            this.onChanged();
            return this;
         }
      }

      public Builder clearName() {
         this.name_ = Option.getDefaultInstance().getName();
         this.onChanged();
         return this;
      }

      public Builder setNameBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.name_ = value;
            this.onChanged();
            return this;
         }
      }

      public boolean hasValue() {
         return this.valueBuilder_ != null || this.value_ != null;
      }

      public Any getValue() {
         if (this.valueBuilder_ == null) {
            return this.value_ == null ? Any.getDefaultInstance() : this.value_;
         } else {
            return (Any)this.valueBuilder_.getMessage();
         }
      }

      public Builder setValue(Any value) {
         if (this.valueBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.value_ = value;
            this.onChanged();
         } else {
            this.valueBuilder_.setMessage(value);
         }

         return this;
      }

      public Builder setValue(Any.Builder builderForValue) {
         if (this.valueBuilder_ == null) {
            this.value_ = builderForValue.build();
            this.onChanged();
         } else {
            this.valueBuilder_.setMessage(builderForValue.build());
         }

         return this;
      }

      public Builder mergeValue(Any value) {
         if (this.valueBuilder_ == null) {
            if (this.value_ != null) {
               this.value_ = Any.newBuilder(this.value_).mergeFrom(value).buildPartial();
            } else {
               this.value_ = value;
            }

            this.onChanged();
         } else {
            this.valueBuilder_.mergeFrom(value);
         }

         return this;
      }

      public Builder clearValue() {
         if (this.valueBuilder_ == null) {
            this.value_ = null;
            this.onChanged();
         } else {
            this.value_ = null;
            this.valueBuilder_ = null;
         }

         return this;
      }

      public Any.Builder getValueBuilder() {
         this.onChanged();
         return (Any.Builder)this.getValueFieldBuilder().getBuilder();
      }

      public AnyOrBuilder getValueOrBuilder() {
         if (this.valueBuilder_ != null) {
            return (AnyOrBuilder)this.valueBuilder_.getMessageOrBuilder();
         } else {
            return this.value_ == null ? Any.getDefaultInstance() : this.value_;
         }
      }

      private SingleFieldBuilderV3<Any, Any.Builder, AnyOrBuilder> getValueFieldBuilder() {
         if (this.valueBuilder_ == null) {
            this.valueBuilder_ = new SingleFieldBuilderV3(this.getValue(), this.getParentForChildren(), this.isClean());
            this.value_ = null;
         }

         return this.valueBuilder_;
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
