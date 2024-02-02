package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Mixin extends GeneratedMessageV3 implements MixinOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int NAME_FIELD_NUMBER = 1;
   private volatile Object name_;
   public static final int ROOT_FIELD_NUMBER = 2;
   private volatile Object root_;
   private byte memoizedIsInitialized;
   private static final Mixin DEFAULT_INSTANCE = new Mixin();
   private static final Parser<Mixin> PARSER = new AbstractParser<Mixin>() {
      public Mixin parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return new Mixin(input, extensionRegistry);
      }
   };

   private Mixin(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
   }

   private Mixin() {
      this.memoizedIsInitialized = -1;
      this.name_ = "";
      this.root_ = "";
   }

   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Mixin();
   }

   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Mixin(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
         throw new NullPointerException();
      } else {
         UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

         try {
            boolean done = false;

            while(!done) {
               int tag = input.readTag();
               String s;
               switch (tag) {
                  case 0:
                     done = true;
                     break;
                  case 10:
                     s = input.readStringRequireUtf8();
                     this.name_ = s;
                     break;
                  case 18:
                     s = input.readStringRequireUtf8();
                     this.root_ = s;
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
      return ApiProto.internal_static_google_protobuf_Mixin_descriptor;
   }

   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return ApiProto.internal_static_google_protobuf_Mixin_fieldAccessorTable.ensureFieldAccessorsInitialized(Mixin.class, Builder.class);
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

   public String getRoot() {
      Object ref = this.root_;
      if (ref instanceof String) {
         return (String)ref;
      } else {
         ByteString bs = (ByteString)ref;
         String s = bs.toStringUtf8();
         this.root_ = s;
         return s;
      }
   }

   public ByteString getRootBytes() {
      Object ref = this.root_;
      if (ref instanceof String) {
         ByteString b = ByteString.copyFromUtf8((String)ref);
         this.root_ = b;
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
      if (!this.getNameBytes().isEmpty()) {
         GeneratedMessageV3.writeString(output, 1, this.name_);
      }

      if (!this.getRootBytes().isEmpty()) {
         GeneratedMessageV3.writeString(output, 2, this.root_);
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

         if (!this.getRootBytes().isEmpty()) {
            size += GeneratedMessageV3.computeStringSize(2, this.root_);
         }

         size += this.unknownFields.getSerializedSize();
         this.memoizedSize = size;
         return size;
      }
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Mixin)) {
         return super.equals(obj);
      } else {
         Mixin other = (Mixin)obj;
         if (!this.getName().equals(other.getName())) {
            return false;
         } else if (!this.getRoot().equals(other.getRoot())) {
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
         hash = 53 * hash + this.getRoot().hashCode();
         hash = 29 * hash + this.unknownFields.hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static Mixin parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Mixin)PARSER.parseFrom(data);
   }

   public static Mixin parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Mixin)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Mixin parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Mixin)PARSER.parseFrom(data);
   }

   public static Mixin parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Mixin)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Mixin parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Mixin)PARSER.parseFrom(data);
   }

   public static Mixin parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Mixin)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Mixin parseFrom(InputStream input) throws IOException {
      return (Mixin)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static Mixin parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Mixin)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static Mixin parseDelimitedFrom(InputStream input) throws IOException {
      return (Mixin)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
   }

   public static Mixin parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Mixin)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static Mixin parseFrom(CodedInputStream input) throws IOException {
      return (Mixin)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static Mixin parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Mixin)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Builder newBuilderForType() {
      return newBuilder();
   }

   public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Builder newBuilder(Mixin prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
   }

   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
   }

   public static Mixin getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Mixin> parser() {
      return PARSER;
   }

   public Parser<Mixin> getParserForType() {
      return PARSER;
   }

   public Mixin getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   // $FF: synthetic method
   Mixin(GeneratedMessageV3.Builder x0, Object x1) {
      this(x0);
   }

   // $FF: synthetic method
   Mixin(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
      this(x0, x1);
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MixinOrBuilder {
      private Object name_;
      private Object root_;

      public static final Descriptors.Descriptor getDescriptor() {
         return ApiProto.internal_static_google_protobuf_Mixin_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return ApiProto.internal_static_google_protobuf_Mixin_fieldAccessorTable.ensureFieldAccessorsInitialized(Mixin.class, Builder.class);
      }

      private Builder() {
         this.name_ = "";
         this.root_ = "";
         this.maybeForceBuilderInitialization();
      }

      private Builder(GeneratedMessageV3.BuilderParent parent) {
         super(parent);
         this.name_ = "";
         this.root_ = "";
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (GeneratedMessageV3.alwaysUseFieldBuilders) {
         }

      }

      public Builder clear() {
         super.clear();
         this.name_ = "";
         this.root_ = "";
         return this;
      }

      public Descriptors.Descriptor getDescriptorForType() {
         return ApiProto.internal_static_google_protobuf_Mixin_descriptor;
      }

      public Mixin getDefaultInstanceForType() {
         return Mixin.getDefaultInstance();
      }

      public Mixin build() {
         Mixin result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public Mixin buildPartial() {
         Mixin result = new Mixin(this);
         result.name_ = this.name_;
         result.root_ = this.root_;
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
         if (other instanceof Mixin) {
            return this.mergeFrom((Mixin)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Builder mergeFrom(Mixin other) {
         if (other == Mixin.getDefaultInstance()) {
            return this;
         } else {
            if (!other.getName().isEmpty()) {
               this.name_ = other.name_;
               this.onChanged();
            }

            if (!other.getRoot().isEmpty()) {
               this.root_ = other.root_;
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
         Mixin parsedMessage = null;

         try {
            parsedMessage = (Mixin)Mixin.PARSER.parsePartialFrom(input, extensionRegistry);
         } catch (InvalidProtocolBufferException var8) {
            parsedMessage = (Mixin)var8.getUnfinishedMessage();
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
         this.name_ = Mixin.getDefaultInstance().getName();
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

      public String getRoot() {
         Object ref = this.root_;
         if (!(ref instanceof String)) {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.root_ = s;
            return s;
         } else {
            return (String)ref;
         }
      }

      public ByteString getRootBytes() {
         Object ref = this.root_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.root_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public Builder setRoot(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.root_ = value;
            this.onChanged();
            return this;
         }
      }

      public Builder clearRoot() {
         this.root_ = Mixin.getDefaultInstance().getRoot();
         this.onChanged();
         return this;
      }

      public Builder setRootBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.root_ = value;
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
