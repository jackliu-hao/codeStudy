package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EnumValue extends GeneratedMessageV3 implements EnumValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int NAME_FIELD_NUMBER = 1;
   private volatile Object name_;
   public static final int NUMBER_FIELD_NUMBER = 2;
   private int number_;
   public static final int OPTIONS_FIELD_NUMBER = 3;
   private List<Option> options_;
   private byte memoizedIsInitialized;
   private static final EnumValue DEFAULT_INSTANCE = new EnumValue();
   private static final Parser<EnumValue> PARSER = new AbstractParser<EnumValue>() {
      public EnumValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return new EnumValue(input, extensionRegistry);
      }
   };

   private EnumValue(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
   }

   private EnumValue() {
      this.memoizedIsInitialized = -1;
      this.name_ = "";
      this.options_ = Collections.emptyList();
   }

   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new EnumValue();
   }

   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private EnumValue(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
         throw new NullPointerException();
      } else {
         int mutable_bitField0_ = false;
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
                  case 16:
                     this.number_ = input.readInt32();
                     break;
                  case 26:
                     if (!(mutable_bitField0_ & true)) {
                        this.options_ = new ArrayList();
                        mutable_bitField0_ |= true;
                     }

                     this.options_.add(input.readMessage(Option.parser(), extensionRegistry));
                     break;
                  default:
                     if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                        done = true;
                     }
               }
            }
         } catch (InvalidProtocolBufferException var12) {
            throw var12.setUnfinishedMessage(this);
         } catch (IOException var13) {
            throw (new InvalidProtocolBufferException(var13)).setUnfinishedMessage(this);
         } finally {
            if (mutable_bitField0_ & true) {
               this.options_ = Collections.unmodifiableList(this.options_);
            }

            this.unknownFields = unknownFields.build();
            this.makeExtensionsImmutable();
         }

      }
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return TypeProto.internal_static_google_protobuf_EnumValue_descriptor;
   }

   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return TypeProto.internal_static_google_protobuf_EnumValue_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumValue.class, Builder.class);
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

   public int getNumber() {
      return this.number_;
   }

   public List<Option> getOptionsList() {
      return this.options_;
   }

   public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
      return this.options_;
   }

   public int getOptionsCount() {
      return this.options_.size();
   }

   public Option getOptions(int index) {
      return (Option)this.options_.get(index);
   }

   public OptionOrBuilder getOptionsOrBuilder(int index) {
      return (OptionOrBuilder)this.options_.get(index);
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

      if (this.number_ != 0) {
         output.writeInt32(2, this.number_);
      }

      for(int i = 0; i < this.options_.size(); ++i) {
         output.writeMessage(3, (MessageLite)this.options_.get(i));
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

         if (this.number_ != 0) {
            size += CodedOutputStream.computeInt32Size(2, this.number_);
         }

         for(int i = 0; i < this.options_.size(); ++i) {
            size += CodedOutputStream.computeMessageSize(3, (MessageLite)this.options_.get(i));
         }

         size += this.unknownFields.getSerializedSize();
         this.memoizedSize = size;
         return size;
      }
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof EnumValue)) {
         return super.equals(obj);
      } else {
         EnumValue other = (EnumValue)obj;
         if (!this.getName().equals(other.getName())) {
            return false;
         } else if (this.getNumber() != other.getNumber()) {
            return false;
         } else if (!this.getOptionsList().equals(other.getOptionsList())) {
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
         hash = 37 * hash + 2;
         hash = 53 * hash + this.getNumber();
         if (this.getOptionsCount() > 0) {
            hash = 37 * hash + 3;
            hash = 53 * hash + this.getOptionsList().hashCode();
         }

         hash = 29 * hash + this.unknownFields.hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static EnumValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (EnumValue)PARSER.parseFrom(data);
   }

   public static EnumValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (EnumValue)PARSER.parseFrom(data, extensionRegistry);
   }

   public static EnumValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (EnumValue)PARSER.parseFrom(data);
   }

   public static EnumValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (EnumValue)PARSER.parseFrom(data, extensionRegistry);
   }

   public static EnumValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (EnumValue)PARSER.parseFrom(data);
   }

   public static EnumValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (EnumValue)PARSER.parseFrom(data, extensionRegistry);
   }

   public static EnumValue parseFrom(InputStream input) throws IOException {
      return (EnumValue)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static EnumValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (EnumValue)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static EnumValue parseDelimitedFrom(InputStream input) throws IOException {
      return (EnumValue)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
   }

   public static EnumValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (EnumValue)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static EnumValue parseFrom(CodedInputStream input) throws IOException {
      return (EnumValue)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static EnumValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (EnumValue)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Builder newBuilderForType() {
      return newBuilder();
   }

   public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Builder newBuilder(EnumValue prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
   }

   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
   }

   public static EnumValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<EnumValue> parser() {
      return PARSER;
   }

   public Parser<EnumValue> getParserForType() {
      return PARSER;
   }

   public EnumValue getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   // $FF: synthetic method
   EnumValue(GeneratedMessageV3.Builder x0, Object x1) {
      this(x0);
   }

   // $FF: synthetic method
   EnumValue(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
      this(x0, x1);
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements EnumValueOrBuilder {
      private int bitField0_;
      private Object name_;
      private int number_;
      private List<Option> options_;
      private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return TypeProto.internal_static_google_protobuf_EnumValue_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return TypeProto.internal_static_google_protobuf_EnumValue_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumValue.class, Builder.class);
      }

      private Builder() {
         this.name_ = "";
         this.options_ = Collections.emptyList();
         this.maybeForceBuilderInitialization();
      }

      private Builder(GeneratedMessageV3.BuilderParent parent) {
         super(parent);
         this.name_ = "";
         this.options_ = Collections.emptyList();
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (GeneratedMessageV3.alwaysUseFieldBuilders) {
            this.getOptionsFieldBuilder();
         }

      }

      public Builder clear() {
         super.clear();
         this.name_ = "";
         this.number_ = 0;
         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
            this.bitField0_ &= -2;
         } else {
            this.optionsBuilder_.clear();
         }

         return this;
      }

      public Descriptors.Descriptor getDescriptorForType() {
         return TypeProto.internal_static_google_protobuf_EnumValue_descriptor;
      }

      public EnumValue getDefaultInstanceForType() {
         return EnumValue.getDefaultInstance();
      }

      public EnumValue build() {
         EnumValue result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public EnumValue buildPartial() {
         EnumValue result = new EnumValue(this);
         int from_bitField0_ = this.bitField0_;
         result.name_ = this.name_;
         result.number_ = this.number_;
         if (this.optionsBuilder_ == null) {
            if ((this.bitField0_ & 1) != 0) {
               this.options_ = Collections.unmodifiableList(this.options_);
               this.bitField0_ &= -2;
            }

            result.options_ = this.options_;
         } else {
            result.options_ = this.optionsBuilder_.build();
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
         if (other instanceof EnumValue) {
            return this.mergeFrom((EnumValue)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Builder mergeFrom(EnumValue other) {
         if (other == EnumValue.getDefaultInstance()) {
            return this;
         } else {
            if (!other.getName().isEmpty()) {
               this.name_ = other.name_;
               this.onChanged();
            }

            if (other.getNumber() != 0) {
               this.setNumber(other.getNumber());
            }

            if (this.optionsBuilder_ == null) {
               if (!other.options_.isEmpty()) {
                  if (this.options_.isEmpty()) {
                     this.options_ = other.options_;
                     this.bitField0_ &= -2;
                  } else {
                     this.ensureOptionsIsMutable();
                     this.options_.addAll(other.options_);
                  }

                  this.onChanged();
               }
            } else if (!other.options_.isEmpty()) {
               if (this.optionsBuilder_.isEmpty()) {
                  this.optionsBuilder_.dispose();
                  this.optionsBuilder_ = null;
                  this.options_ = other.options_;
                  this.bitField0_ &= -2;
                  this.optionsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? this.getOptionsFieldBuilder() : null;
               } else {
                  this.optionsBuilder_.addAllMessages(other.options_);
               }
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
         EnumValue parsedMessage = null;

         try {
            parsedMessage = (EnumValue)EnumValue.PARSER.parsePartialFrom(input, extensionRegistry);
         } catch (InvalidProtocolBufferException var8) {
            parsedMessage = (EnumValue)var8.getUnfinishedMessage();
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
         this.name_ = EnumValue.getDefaultInstance().getName();
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

      public int getNumber() {
         return this.number_;
      }

      public Builder setNumber(int value) {
         this.number_ = value;
         this.onChanged();
         return this;
      }

      public Builder clearNumber() {
         this.number_ = 0;
         this.onChanged();
         return this;
      }

      private void ensureOptionsIsMutable() {
         if ((this.bitField0_ & 1) == 0) {
            this.options_ = new ArrayList(this.options_);
            this.bitField0_ |= 1;
         }

      }

      public List<Option> getOptionsList() {
         return this.optionsBuilder_ == null ? Collections.unmodifiableList(this.options_) : this.optionsBuilder_.getMessageList();
      }

      public int getOptionsCount() {
         return this.optionsBuilder_ == null ? this.options_.size() : this.optionsBuilder_.getCount();
      }

      public Option getOptions(int index) {
         return this.optionsBuilder_ == null ? (Option)this.options_.get(index) : (Option)this.optionsBuilder_.getMessage(index);
      }

      public Builder setOptions(int index, Option value) {
         if (this.optionsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureOptionsIsMutable();
            this.options_.set(index, value);
            this.onChanged();
         } else {
            this.optionsBuilder_.setMessage(index, value);
         }

         return this;
      }

      public Builder setOptions(int index, Option.Builder builderForValue) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.set(index, builderForValue.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.setMessage(index, builderForValue.build());
         }

         return this;
      }

      public Builder addOptions(Option value) {
         if (this.optionsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureOptionsIsMutable();
            this.options_.add(value);
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(value);
         }

         return this;
      }

      public Builder addOptions(int index, Option value) {
         if (this.optionsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureOptionsIsMutable();
            this.options_.add(index, value);
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(index, value);
         }

         return this;
      }

      public Builder addOptions(Option.Builder builderForValue) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(builderForValue.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(builderForValue.build());
         }

         return this;
      }

      public Builder addOptions(int index, Option.Builder builderForValue) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(index, builderForValue.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(index, builderForValue.build());
         }

         return this;
      }

      public Builder addAllOptions(Iterable<? extends Option> values) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            AbstractMessageLite.Builder.addAll(values, this.options_);
            this.onChanged();
         } else {
            this.optionsBuilder_.addAllMessages(values);
         }

         return this;
      }

      public Builder clearOptions() {
         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
            this.bitField0_ &= -2;
            this.onChanged();
         } else {
            this.optionsBuilder_.clear();
         }

         return this;
      }

      public Builder removeOptions(int index) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.remove(index);
            this.onChanged();
         } else {
            this.optionsBuilder_.remove(index);
         }

         return this;
      }

      public Option.Builder getOptionsBuilder(int index) {
         return (Option.Builder)this.getOptionsFieldBuilder().getBuilder(index);
      }

      public OptionOrBuilder getOptionsOrBuilder(int index) {
         return this.optionsBuilder_ == null ? (OptionOrBuilder)this.options_.get(index) : (OptionOrBuilder)this.optionsBuilder_.getMessageOrBuilder(index);
      }

      public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
         return this.optionsBuilder_ != null ? this.optionsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.options_);
      }

      public Option.Builder addOptionsBuilder() {
         return (Option.Builder)this.getOptionsFieldBuilder().addBuilder(Option.getDefaultInstance());
      }

      public Option.Builder addOptionsBuilder(int index) {
         return (Option.Builder)this.getOptionsFieldBuilder().addBuilder(index, Option.getDefaultInstance());
      }

      public List<Option.Builder> getOptionsBuilderList() {
         return this.getOptionsFieldBuilder().getBuilderList();
      }

      private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> getOptionsFieldBuilder() {
         if (this.optionsBuilder_ == null) {
            this.optionsBuilder_ = new RepeatedFieldBuilderV3(this.options_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
            this.options_ = null;
         }

         return this.optionsBuilder_;
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
