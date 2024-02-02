package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Duration extends GeneratedMessageV3 implements DurationOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int SECONDS_FIELD_NUMBER = 1;
   private long seconds_;
   public static final int NANOS_FIELD_NUMBER = 2;
   private int nanos_;
   private byte memoizedIsInitialized;
   private static final Duration DEFAULT_INSTANCE = new Duration();
   private static final Parser<Duration> PARSER = new AbstractParser<Duration>() {
      public Duration parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return new Duration(input, extensionRegistry);
      }
   };

   private Duration(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
   }

   private Duration() {
      this.memoizedIsInitialized = -1;
   }

   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Duration();
   }

   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Duration(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                     this.seconds_ = input.readInt64();
                     break;
                  case 16:
                     this.nanos_ = input.readInt32();
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
      return DurationProto.internal_static_google_protobuf_Duration_descriptor;
   }

   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return DurationProto.internal_static_google_protobuf_Duration_fieldAccessorTable.ensureFieldAccessorsInitialized(Duration.class, Builder.class);
   }

   public long getSeconds() {
      return this.seconds_;
   }

   public int getNanos() {
      return this.nanos_;
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
      if (this.seconds_ != 0L) {
         output.writeInt64(1, this.seconds_);
      }

      if (this.nanos_ != 0) {
         output.writeInt32(2, this.nanos_);
      }

      this.unknownFields.writeTo(output);
   }

   public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1) {
         return size;
      } else {
         size = 0;
         if (this.seconds_ != 0L) {
            size += CodedOutputStream.computeInt64Size(1, this.seconds_);
         }

         if (this.nanos_ != 0) {
            size += CodedOutputStream.computeInt32Size(2, this.nanos_);
         }

         size += this.unknownFields.getSerializedSize();
         this.memoizedSize = size;
         return size;
      }
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Duration)) {
         return super.equals(obj);
      } else {
         Duration other = (Duration)obj;
         if (this.getSeconds() != other.getSeconds()) {
            return false;
         } else if (this.getNanos() != other.getNanos()) {
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
         hash = 53 * hash + Internal.hashLong(this.getSeconds());
         hash = 37 * hash + 2;
         hash = 53 * hash + this.getNanos();
         hash = 29 * hash + this.unknownFields.hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static Duration parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Duration)PARSER.parseFrom(data);
   }

   public static Duration parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Duration)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Duration parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Duration)PARSER.parseFrom(data);
   }

   public static Duration parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Duration)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Duration parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Duration)PARSER.parseFrom(data);
   }

   public static Duration parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Duration)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Duration parseFrom(InputStream input) throws IOException {
      return (Duration)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static Duration parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Duration)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static Duration parseDelimitedFrom(InputStream input) throws IOException {
      return (Duration)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
   }

   public static Duration parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Duration)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static Duration parseFrom(CodedInputStream input) throws IOException {
      return (Duration)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static Duration parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Duration)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Builder newBuilderForType() {
      return newBuilder();
   }

   public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Builder newBuilder(Duration prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
   }

   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
   }

   public static Duration getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Duration> parser() {
      return PARSER;
   }

   public Parser<Duration> getParserForType() {
      return PARSER;
   }

   public Duration getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   // $FF: synthetic method
   Duration(GeneratedMessageV3.Builder x0, Object x1) {
      this(x0);
   }

   // $FF: synthetic method
   Duration(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
      this(x0, x1);
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements DurationOrBuilder {
      private long seconds_;
      private int nanos_;

      public static final Descriptors.Descriptor getDescriptor() {
         return DurationProto.internal_static_google_protobuf_Duration_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return DurationProto.internal_static_google_protobuf_Duration_fieldAccessorTable.ensureFieldAccessorsInitialized(Duration.class, Builder.class);
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
         this.seconds_ = 0L;
         this.nanos_ = 0;
         return this;
      }

      public Descriptors.Descriptor getDescriptorForType() {
         return DurationProto.internal_static_google_protobuf_Duration_descriptor;
      }

      public Duration getDefaultInstanceForType() {
         return Duration.getDefaultInstance();
      }

      public Duration build() {
         Duration result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public Duration buildPartial() {
         Duration result = new Duration(this);
         result.seconds_ = this.seconds_;
         result.nanos_ = this.nanos_;
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
         if (other instanceof Duration) {
            return this.mergeFrom((Duration)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Builder mergeFrom(Duration other) {
         if (other == Duration.getDefaultInstance()) {
            return this;
         } else {
            if (other.getSeconds() != 0L) {
               this.setSeconds(other.getSeconds());
            }

            if (other.getNanos() != 0) {
               this.setNanos(other.getNanos());
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
         Duration parsedMessage = null;

         try {
            parsedMessage = (Duration)Duration.PARSER.parsePartialFrom(input, extensionRegistry);
         } catch (InvalidProtocolBufferException var8) {
            parsedMessage = (Duration)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (parsedMessage != null) {
               this.mergeFrom(parsedMessage);
            }

         }

         return this;
      }

      public long getSeconds() {
         return this.seconds_;
      }

      public Builder setSeconds(long value) {
         this.seconds_ = value;
         this.onChanged();
         return this;
      }

      public Builder clearSeconds() {
         this.seconds_ = 0L;
         this.onChanged();
         return this;
      }

      public int getNanos() {
         return this.nanos_;
      }

      public Builder setNanos(int value) {
         this.nanos_ = value;
         this.onChanged();
         return this;
      }

      public Builder clearNanos() {
         this.nanos_ = 0;
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
