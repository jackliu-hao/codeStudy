package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Empty extends GeneratedMessageV3 implements EmptyOrBuilder {
   private static final long serialVersionUID = 0L;
   private byte memoizedIsInitialized;
   private static final Empty DEFAULT_INSTANCE = new Empty();
   private static final Parser<Empty> PARSER = new AbstractParser<Empty>() {
      public Empty parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return new Empty(input, extensionRegistry);
      }
   };

   private Empty(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
   }

   private Empty() {
      this.memoizedIsInitialized = -1;
   }

   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Empty();
   }

   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Empty(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
      return EmptyProto.internal_static_google_protobuf_Empty_descriptor;
   }

   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return EmptyProto.internal_static_google_protobuf_Empty_fieldAccessorTable.ensureFieldAccessorsInitialized(Empty.class, Builder.class);
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
      this.unknownFields.writeTo(output);
   }

   public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1) {
         return size;
      } else {
         int size = 0;
         size = size + this.unknownFields.getSerializedSize();
         this.memoizedSize = size;
         return size;
      }
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Empty)) {
         return super.equals(obj);
      } else {
         Empty other = (Empty)obj;
         return this.unknownFields.equals(other.unknownFields);
      }
   }

   public int hashCode() {
      if (this.memoizedHashCode != 0) {
         return this.memoizedHashCode;
      } else {
         int hash = 41;
         hash = 19 * hash + getDescriptor().hashCode();
         hash = 29 * hash + this.unknownFields.hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static Empty parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Empty)PARSER.parseFrom(data);
   }

   public static Empty parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Empty)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Empty parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Empty)PARSER.parseFrom(data);
   }

   public static Empty parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Empty)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Empty parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Empty)PARSER.parseFrom(data);
   }

   public static Empty parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Empty)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Empty parseFrom(InputStream input) throws IOException {
      return (Empty)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static Empty parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Empty)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static Empty parseDelimitedFrom(InputStream input) throws IOException {
      return (Empty)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
   }

   public static Empty parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Empty)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static Empty parseFrom(CodedInputStream input) throws IOException {
      return (Empty)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static Empty parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Empty)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Builder newBuilderForType() {
      return newBuilder();
   }

   public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Builder newBuilder(Empty prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
   }

   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
   }

   public static Empty getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Empty> parser() {
      return PARSER;
   }

   public Parser<Empty> getParserForType() {
      return PARSER;
   }

   public Empty getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   // $FF: synthetic method
   Empty(GeneratedMessageV3.Builder x0, Object x1) {
      this(x0);
   }

   // $FF: synthetic method
   Empty(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
      this(x0, x1);
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements EmptyOrBuilder {
      public static final Descriptors.Descriptor getDescriptor() {
         return EmptyProto.internal_static_google_protobuf_Empty_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return EmptyProto.internal_static_google_protobuf_Empty_fieldAccessorTable.ensureFieldAccessorsInitialized(Empty.class, Builder.class);
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
         return this;
      }

      public Descriptors.Descriptor getDescriptorForType() {
         return EmptyProto.internal_static_google_protobuf_Empty_descriptor;
      }

      public Empty getDefaultInstanceForType() {
         return Empty.getDefaultInstance();
      }

      public Empty build() {
         Empty result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public Empty buildPartial() {
         Empty result = new Empty(this);
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
         if (other instanceof Empty) {
            return this.mergeFrom((Empty)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Builder mergeFrom(Empty other) {
         if (other == Empty.getDefaultInstance()) {
            return this;
         } else {
            this.mergeUnknownFields(other.unknownFields);
            this.onChanged();
            return this;
         }
      }

      public final boolean isInitialized() {
         return true;
      }

      public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         Empty parsedMessage = null;

         try {
            parsedMessage = (Empty)Empty.PARSER.parsePartialFrom(input, extensionRegistry);
         } catch (InvalidProtocolBufferException var8) {
            parsedMessage = (Empty)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (parsedMessage != null) {
               this.mergeFrom(parsedMessage);
            }

         }

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
