package com.mysql.cj.x.protobuf;

import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class MysqlxSession {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Session_AuthenticateStart_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Session_AuthenticateStart_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Session_AuthenticateContinue_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Session_AuthenticateContinue_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Session_AuthenticateOk_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Session_AuthenticateOk_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Session_Reset_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Session_Reset_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Session_Close_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Session_Close_fieldAccessorTable;
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxSession() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite registry) {
   }

   public static void registerAllExtensions(ExtensionRegistry registry) {
      registerAllExtensions((ExtensionRegistryLite)registry);
   }

   public static Descriptors.FileDescriptor getDescriptor() {
      return descriptor;
   }

   static {
      String[] descriptorData = new String[]{"\n\u0014mysqlx_session.proto\u0012\u000eMysqlx.Session\u001a\fmysqlx.proto\"Y\n\u0011AuthenticateStart\u0012\u0011\n\tmech_name\u0018\u0001 \u0002(\t\u0012\u0011\n\tauth_data\u0018\u0002 \u0001(\f\u0012\u0018\n\u0010initial_response\u0018\u0003 \u0001(\f:\u0004\u0088ê0\u0004\"3\n\u0014AuthenticateContinue\u0012\u0011\n\tauth_data\u0018\u0001 \u0002(\f:\b\u0090ê0\u0003\u0088ê0\u0005\")\n\u000eAuthenticateOk\u0012\u0011\n\tauth_data\u0018\u0001 \u0001(\f:\u0004\u0090ê0\u0004\"'\n\u0005Reset\u0012\u0018\n\tkeep_open\u0018\u0001 \u0001(\b:\u0005false:\u0004\u0088ê0\u0006\"\r\n\u0005Close:\u0004\u0088ê0\u0007B\u0019\n\u0017com.mysql.cj.x.protobuf"};
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Mysqlx.getDescriptor()});
      internal_static_Mysqlx_Session_AuthenticateStart_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(0);
      internal_static_Mysqlx_Session_AuthenticateStart_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Session_AuthenticateStart_descriptor, new String[]{"MechName", "AuthData", "InitialResponse"});
      internal_static_Mysqlx_Session_AuthenticateContinue_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(1);
      internal_static_Mysqlx_Session_AuthenticateContinue_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Session_AuthenticateContinue_descriptor, new String[]{"AuthData"});
      internal_static_Mysqlx_Session_AuthenticateOk_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(2);
      internal_static_Mysqlx_Session_AuthenticateOk_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Session_AuthenticateOk_descriptor, new String[]{"AuthData"});
      internal_static_Mysqlx_Session_Reset_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(3);
      internal_static_Mysqlx_Session_Reset_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Session_Reset_descriptor, new String[]{"KeepOpen"});
      internal_static_Mysqlx_Session_Close_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(4);
      internal_static_Mysqlx_Session_Close_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Session_Close_descriptor, new String[0]);
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.clientMessageId);
      registry.add(Mysqlx.serverMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      Mysqlx.getDescriptor();
   }

   public static final class Close extends GeneratedMessageV3 implements CloseOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized;
      private static final Close DEFAULT_INSTANCE = new Close();
      /** @deprecated */
      @Deprecated
      public static final Parser<Close> PARSER = new AbstractParser<Close>() {
         public Close parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Close(input, extensionRegistry);
         }
      };

      private Close(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Close() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Close();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Close(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return MysqlxSession.internal_static_Mysqlx_Session_Close_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxSession.internal_static_Mysqlx_Session_Close_fieldAccessorTable.ensureFieldAccessorsInitialized(Close.class, Builder.class);
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
         } else if (!(obj instanceof Close)) {
            return super.equals(obj);
         } else {
            Close other = (Close)obj;
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

      public static Close parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Close)PARSER.parseFrom(data);
      }

      public static Close parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Close)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Close parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Close)PARSER.parseFrom(data);
      }

      public static Close parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Close)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Close parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Close)PARSER.parseFrom(data);
      }

      public static Close parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Close)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Close parseFrom(InputStream input) throws IOException {
         return (Close)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Close parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Close)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Close parseDelimitedFrom(InputStream input) throws IOException {
         return (Close)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Close parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Close)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Close parseFrom(CodedInputStream input) throws IOException {
         return (Close)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Close parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Close)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Close prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Close getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Close> parser() {
         return PARSER;
      }

      public Parser<Close> getParserForType() {
         return PARSER;
      }

      public Close getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Close(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Close(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements CloseOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxSession.internal_static_Mysqlx_Session_Close_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxSession.internal_static_Mysqlx_Session_Close_fieldAccessorTable.ensureFieldAccessorsInitialized(Close.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxSession.Close.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxSession.internal_static_Mysqlx_Session_Close_descriptor;
         }

         public Close getDefaultInstanceForType() {
            return MysqlxSession.Close.getDefaultInstance();
         }

         public Close build() {
            Close result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Close buildPartial() {
            Close result = new Close(this);
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
            if (other instanceof Close) {
               return this.mergeFrom((Close)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Close other) {
            if (other == MysqlxSession.Close.getDefaultInstance()) {
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
            Close parsedMessage = null;

            try {
               parsedMessage = (Close)MysqlxSession.Close.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Close)var8.getUnfinishedMessage();
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

   public interface CloseOrBuilder extends MessageOrBuilder {
   }

   public static final class Reset extends GeneratedMessageV3 implements ResetOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int KEEP_OPEN_FIELD_NUMBER = 1;
      private boolean keepOpen_;
      private byte memoizedIsInitialized;
      private static final Reset DEFAULT_INSTANCE = new Reset();
      /** @deprecated */
      @Deprecated
      public static final Parser<Reset> PARSER = new AbstractParser<Reset>() {
         public Reset parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Reset(input, extensionRegistry);
         }
      };

      private Reset(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Reset() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Reset();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Reset(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                     case 8:
                        this.bitField0_ |= 1;
                        this.keepOpen_ = input.readBool();
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
         return MysqlxSession.internal_static_Mysqlx_Session_Reset_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxSession.internal_static_Mysqlx_Session_Reset_fieldAccessorTable.ensureFieldAccessorsInitialized(Reset.class, Builder.class);
      }

      public boolean hasKeepOpen() {
         return (this.bitField0_ & 1) != 0;
      }

      public boolean getKeepOpen() {
         return this.keepOpen_;
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
         if ((this.bitField0_ & 1) != 0) {
            output.writeBool(1, this.keepOpen_);
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeBoolSize(1, this.keepOpen_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Reset)) {
            return super.equals(obj);
         } else {
            Reset other = (Reset)obj;
            if (this.hasKeepOpen() != other.hasKeepOpen()) {
               return false;
            } else if (this.hasKeepOpen() && this.getKeepOpen() != other.getKeepOpen()) {
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
            if (this.hasKeepOpen()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + Internal.hashBoolean(this.getKeepOpen());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Reset parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Reset)PARSER.parseFrom(data);
      }

      public static Reset parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Reset)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Reset parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Reset)PARSER.parseFrom(data);
      }

      public static Reset parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Reset)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Reset parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Reset)PARSER.parseFrom(data);
      }

      public static Reset parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Reset)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Reset parseFrom(InputStream input) throws IOException {
         return (Reset)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Reset parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Reset)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Reset parseDelimitedFrom(InputStream input) throws IOException {
         return (Reset)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Reset parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Reset)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Reset parseFrom(CodedInputStream input) throws IOException {
         return (Reset)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Reset parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Reset)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Reset prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Reset getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Reset> parser() {
         return PARSER;
      }

      public Parser<Reset> getParserForType() {
         return PARSER;
      }

      public Reset getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Reset(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Reset(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ResetOrBuilder {
         private int bitField0_;
         private boolean keepOpen_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxSession.internal_static_Mysqlx_Session_Reset_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxSession.internal_static_Mysqlx_Session_Reset_fieldAccessorTable.ensureFieldAccessorsInitialized(Reset.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxSession.Reset.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            this.keepOpen_ = false;
            this.bitField0_ &= -2;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxSession.internal_static_Mysqlx_Session_Reset_descriptor;
         }

         public Reset getDefaultInstanceForType() {
            return MysqlxSession.Reset.getDefaultInstance();
         }

         public Reset build() {
            Reset result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Reset buildPartial() {
            Reset result = new Reset(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.keepOpen_ = this.keepOpen_;
               to_bitField0_ |= 1;
            }

            result.bitField0_ = to_bitField0_;
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
            if (other instanceof Reset) {
               return this.mergeFrom((Reset)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Reset other) {
            if (other == MysqlxSession.Reset.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasKeepOpen()) {
                  this.setKeepOpen(other.getKeepOpen());
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
            Reset parsedMessage = null;

            try {
               parsedMessage = (Reset)MysqlxSession.Reset.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Reset)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasKeepOpen() {
            return (this.bitField0_ & 1) != 0;
         }

         public boolean getKeepOpen() {
            return this.keepOpen_;
         }

         public Builder setKeepOpen(boolean value) {
            this.bitField0_ |= 1;
            this.keepOpen_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearKeepOpen() {
            this.bitField0_ &= -2;
            this.keepOpen_ = false;
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

   public interface ResetOrBuilder extends MessageOrBuilder {
      boolean hasKeepOpen();

      boolean getKeepOpen();
   }

   public static final class AuthenticateOk extends GeneratedMessageV3 implements AuthenticateOkOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int AUTH_DATA_FIELD_NUMBER = 1;
      private ByteString authData_;
      private byte memoizedIsInitialized;
      private static final AuthenticateOk DEFAULT_INSTANCE = new AuthenticateOk();
      /** @deprecated */
      @Deprecated
      public static final Parser<AuthenticateOk> PARSER = new AbstractParser<AuthenticateOk>() {
         public AuthenticateOk parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new AuthenticateOk(input, extensionRegistry);
         }
      };

      private AuthenticateOk(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private AuthenticateOk() {
         this.memoizedIsInitialized = -1;
         this.authData_ = ByteString.EMPTY;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new AuthenticateOk();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private AuthenticateOk(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.bitField0_ |= 1;
                        this.authData_ = input.readBytes();
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
         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateOk_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateOk_fieldAccessorTable.ensureFieldAccessorsInitialized(AuthenticateOk.class, Builder.class);
      }

      public boolean hasAuthData() {
         return (this.bitField0_ & 1) != 0;
      }

      public ByteString getAuthData() {
         return this.authData_;
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
         if ((this.bitField0_ & 1) != 0) {
            output.writeBytes(1, this.authData_);
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeBytesSize(1, this.authData_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof AuthenticateOk)) {
            return super.equals(obj);
         } else {
            AuthenticateOk other = (AuthenticateOk)obj;
            if (this.hasAuthData() != other.hasAuthData()) {
               return false;
            } else if (this.hasAuthData() && !this.getAuthData().equals(other.getAuthData())) {
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
            if (this.hasAuthData()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getAuthData().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static AuthenticateOk parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (AuthenticateOk)PARSER.parseFrom(data);
      }

      public static AuthenticateOk parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (AuthenticateOk)PARSER.parseFrom(data, extensionRegistry);
      }

      public static AuthenticateOk parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (AuthenticateOk)PARSER.parseFrom(data);
      }

      public static AuthenticateOk parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (AuthenticateOk)PARSER.parseFrom(data, extensionRegistry);
      }

      public static AuthenticateOk parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (AuthenticateOk)PARSER.parseFrom(data);
      }

      public static AuthenticateOk parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (AuthenticateOk)PARSER.parseFrom(data, extensionRegistry);
      }

      public static AuthenticateOk parseFrom(InputStream input) throws IOException {
         return (AuthenticateOk)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static AuthenticateOk parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (AuthenticateOk)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static AuthenticateOk parseDelimitedFrom(InputStream input) throws IOException {
         return (AuthenticateOk)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static AuthenticateOk parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (AuthenticateOk)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static AuthenticateOk parseFrom(CodedInputStream input) throws IOException {
         return (AuthenticateOk)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static AuthenticateOk parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (AuthenticateOk)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(AuthenticateOk prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static AuthenticateOk getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<AuthenticateOk> parser() {
         return PARSER;
      }

      public Parser<AuthenticateOk> getParserForType() {
         return PARSER;
      }

      public AuthenticateOk getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      AuthenticateOk(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      AuthenticateOk(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements AuthenticateOkOrBuilder {
         private int bitField0_;
         private ByteString authData_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateOk_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateOk_fieldAccessorTable.ensureFieldAccessorsInitialized(AuthenticateOk.class, Builder.class);
         }

         private Builder() {
            this.authData_ = ByteString.EMPTY;
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.authData_ = ByteString.EMPTY;
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxSession.AuthenticateOk.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            this.authData_ = ByteString.EMPTY;
            this.bitField0_ &= -2;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateOk_descriptor;
         }

         public AuthenticateOk getDefaultInstanceForType() {
            return MysqlxSession.AuthenticateOk.getDefaultInstance();
         }

         public AuthenticateOk build() {
            AuthenticateOk result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public AuthenticateOk buildPartial() {
            AuthenticateOk result = new AuthenticateOk(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.authData_ = this.authData_;
            result.bitField0_ = to_bitField0_;
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
            if (other instanceof AuthenticateOk) {
               return this.mergeFrom((AuthenticateOk)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(AuthenticateOk other) {
            if (other == MysqlxSession.AuthenticateOk.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasAuthData()) {
                  this.setAuthData(other.getAuthData());
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
            AuthenticateOk parsedMessage = null;

            try {
               parsedMessage = (AuthenticateOk)MysqlxSession.AuthenticateOk.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (AuthenticateOk)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasAuthData() {
            return (this.bitField0_ & 1) != 0;
         }

         public ByteString getAuthData() {
            return this.authData_;
         }

         public Builder setAuthData(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.authData_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearAuthData() {
            this.bitField0_ &= -2;
            this.authData_ = MysqlxSession.AuthenticateOk.getDefaultInstance().getAuthData();
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

   public interface AuthenticateOkOrBuilder extends MessageOrBuilder {
      boolean hasAuthData();

      ByteString getAuthData();
   }

   public static final class AuthenticateContinue extends GeneratedMessageV3 implements AuthenticateContinueOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int AUTH_DATA_FIELD_NUMBER = 1;
      private ByteString authData_;
      private byte memoizedIsInitialized;
      private static final AuthenticateContinue DEFAULT_INSTANCE = new AuthenticateContinue();
      /** @deprecated */
      @Deprecated
      public static final Parser<AuthenticateContinue> PARSER = new AbstractParser<AuthenticateContinue>() {
         public AuthenticateContinue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new AuthenticateContinue(input, extensionRegistry);
         }
      };

      private AuthenticateContinue(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private AuthenticateContinue() {
         this.memoizedIsInitialized = -1;
         this.authData_ = ByteString.EMPTY;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new AuthenticateContinue();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private AuthenticateContinue(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.bitField0_ |= 1;
                        this.authData_ = input.readBytes();
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
         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateContinue_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateContinue_fieldAccessorTable.ensureFieldAccessorsInitialized(AuthenticateContinue.class, Builder.class);
      }

      public boolean hasAuthData() {
         return (this.bitField0_ & 1) != 0;
      }

      public ByteString getAuthData() {
         return this.authData_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasAuthData()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeBytes(1, this.authData_);
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeBytesSize(1, this.authData_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof AuthenticateContinue)) {
            return super.equals(obj);
         } else {
            AuthenticateContinue other = (AuthenticateContinue)obj;
            if (this.hasAuthData() != other.hasAuthData()) {
               return false;
            } else if (this.hasAuthData() && !this.getAuthData().equals(other.getAuthData())) {
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
            if (this.hasAuthData()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getAuthData().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static AuthenticateContinue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (AuthenticateContinue)PARSER.parseFrom(data);
      }

      public static AuthenticateContinue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (AuthenticateContinue)PARSER.parseFrom(data, extensionRegistry);
      }

      public static AuthenticateContinue parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (AuthenticateContinue)PARSER.parseFrom(data);
      }

      public static AuthenticateContinue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (AuthenticateContinue)PARSER.parseFrom(data, extensionRegistry);
      }

      public static AuthenticateContinue parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (AuthenticateContinue)PARSER.parseFrom(data);
      }

      public static AuthenticateContinue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (AuthenticateContinue)PARSER.parseFrom(data, extensionRegistry);
      }

      public static AuthenticateContinue parseFrom(InputStream input) throws IOException {
         return (AuthenticateContinue)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static AuthenticateContinue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (AuthenticateContinue)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static AuthenticateContinue parseDelimitedFrom(InputStream input) throws IOException {
         return (AuthenticateContinue)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static AuthenticateContinue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (AuthenticateContinue)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static AuthenticateContinue parseFrom(CodedInputStream input) throws IOException {
         return (AuthenticateContinue)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static AuthenticateContinue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (AuthenticateContinue)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(AuthenticateContinue prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static AuthenticateContinue getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<AuthenticateContinue> parser() {
         return PARSER;
      }

      public Parser<AuthenticateContinue> getParserForType() {
         return PARSER;
      }

      public AuthenticateContinue getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      AuthenticateContinue(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      AuthenticateContinue(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements AuthenticateContinueOrBuilder {
         private int bitField0_;
         private ByteString authData_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateContinue_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateContinue_fieldAccessorTable.ensureFieldAccessorsInitialized(AuthenticateContinue.class, Builder.class);
         }

         private Builder() {
            this.authData_ = ByteString.EMPTY;
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.authData_ = ByteString.EMPTY;
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxSession.AuthenticateContinue.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            this.authData_ = ByteString.EMPTY;
            this.bitField0_ &= -2;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateContinue_descriptor;
         }

         public AuthenticateContinue getDefaultInstanceForType() {
            return MysqlxSession.AuthenticateContinue.getDefaultInstance();
         }

         public AuthenticateContinue build() {
            AuthenticateContinue result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public AuthenticateContinue buildPartial() {
            AuthenticateContinue result = new AuthenticateContinue(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.authData_ = this.authData_;
            result.bitField0_ = to_bitField0_;
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
            if (other instanceof AuthenticateContinue) {
               return this.mergeFrom((AuthenticateContinue)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(AuthenticateContinue other) {
            if (other == MysqlxSession.AuthenticateContinue.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasAuthData()) {
                  this.setAuthData(other.getAuthData());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            return this.hasAuthData();
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            AuthenticateContinue parsedMessage = null;

            try {
               parsedMessage = (AuthenticateContinue)MysqlxSession.AuthenticateContinue.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (AuthenticateContinue)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasAuthData() {
            return (this.bitField0_ & 1) != 0;
         }

         public ByteString getAuthData() {
            return this.authData_;
         }

         public Builder setAuthData(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.authData_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearAuthData() {
            this.bitField0_ &= -2;
            this.authData_ = MysqlxSession.AuthenticateContinue.getDefaultInstance().getAuthData();
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

   public interface AuthenticateContinueOrBuilder extends MessageOrBuilder {
      boolean hasAuthData();

      ByteString getAuthData();
   }

   public static final class AuthenticateStart extends GeneratedMessageV3 implements AuthenticateStartOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int MECH_NAME_FIELD_NUMBER = 1;
      private volatile Object mechName_;
      public static final int AUTH_DATA_FIELD_NUMBER = 2;
      private ByteString authData_;
      public static final int INITIAL_RESPONSE_FIELD_NUMBER = 3;
      private ByteString initialResponse_;
      private byte memoizedIsInitialized;
      private static final AuthenticateStart DEFAULT_INSTANCE = new AuthenticateStart();
      /** @deprecated */
      @Deprecated
      public static final Parser<AuthenticateStart> PARSER = new AbstractParser<AuthenticateStart>() {
         public AuthenticateStart parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new AuthenticateStart(input, extensionRegistry);
         }
      };

      private AuthenticateStart(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private AuthenticateStart() {
         this.memoizedIsInitialized = -1;
         this.mechName_ = "";
         this.authData_ = ByteString.EMPTY;
         this.initialResponse_ = ByteString.EMPTY;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new AuthenticateStart();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private AuthenticateStart(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 1;
                        this.mechName_ = bs;
                        break;
                     case 18:
                        this.bitField0_ |= 2;
                        this.authData_ = input.readBytes();
                        break;
                     case 26:
                        this.bitField0_ |= 4;
                        this.initialResponse_ = input.readBytes();
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
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateStart_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateStart_fieldAccessorTable.ensureFieldAccessorsInitialized(AuthenticateStart.class, Builder.class);
      }

      public boolean hasMechName() {
         return (this.bitField0_ & 1) != 0;
      }

      public String getMechName() {
         Object ref = this.mechName_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.mechName_ = s;
            }

            return s;
         }
      }

      public ByteString getMechNameBytes() {
         Object ref = this.mechName_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.mechName_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public boolean hasAuthData() {
         return (this.bitField0_ & 2) != 0;
      }

      public ByteString getAuthData() {
         return this.authData_;
      }

      public boolean hasInitialResponse() {
         return (this.bitField0_ & 4) != 0;
      }

      public ByteString getInitialResponse() {
         return this.initialResponse_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasMechName()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(output, 1, this.mechName_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeBytes(2, this.authData_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeBytes(3, this.initialResponse_);
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += GeneratedMessageV3.computeStringSize(1, this.mechName_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeBytesSize(2, this.authData_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeBytesSize(3, this.initialResponse_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof AuthenticateStart)) {
            return super.equals(obj);
         } else {
            AuthenticateStart other = (AuthenticateStart)obj;
            if (this.hasMechName() != other.hasMechName()) {
               return false;
            } else if (this.hasMechName() && !this.getMechName().equals(other.getMechName())) {
               return false;
            } else if (this.hasAuthData() != other.hasAuthData()) {
               return false;
            } else if (this.hasAuthData() && !this.getAuthData().equals(other.getAuthData())) {
               return false;
            } else if (this.hasInitialResponse() != other.hasInitialResponse()) {
               return false;
            } else if (this.hasInitialResponse() && !this.getInitialResponse().equals(other.getInitialResponse())) {
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
            if (this.hasMechName()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getMechName().hashCode();
            }

            if (this.hasAuthData()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getAuthData().hashCode();
            }

            if (this.hasInitialResponse()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getInitialResponse().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static AuthenticateStart parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (AuthenticateStart)PARSER.parseFrom(data);
      }

      public static AuthenticateStart parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (AuthenticateStart)PARSER.parseFrom(data, extensionRegistry);
      }

      public static AuthenticateStart parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (AuthenticateStart)PARSER.parseFrom(data);
      }

      public static AuthenticateStart parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (AuthenticateStart)PARSER.parseFrom(data, extensionRegistry);
      }

      public static AuthenticateStart parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (AuthenticateStart)PARSER.parseFrom(data);
      }

      public static AuthenticateStart parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (AuthenticateStart)PARSER.parseFrom(data, extensionRegistry);
      }

      public static AuthenticateStart parseFrom(InputStream input) throws IOException {
         return (AuthenticateStart)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static AuthenticateStart parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (AuthenticateStart)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static AuthenticateStart parseDelimitedFrom(InputStream input) throws IOException {
         return (AuthenticateStart)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static AuthenticateStart parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (AuthenticateStart)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static AuthenticateStart parseFrom(CodedInputStream input) throws IOException {
         return (AuthenticateStart)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static AuthenticateStart parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (AuthenticateStart)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(AuthenticateStart prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static AuthenticateStart getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<AuthenticateStart> parser() {
         return PARSER;
      }

      public Parser<AuthenticateStart> getParserForType() {
         return PARSER;
      }

      public AuthenticateStart getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      AuthenticateStart(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      AuthenticateStart(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements AuthenticateStartOrBuilder {
         private int bitField0_;
         private Object mechName_;
         private ByteString authData_;
         private ByteString initialResponse_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateStart_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateStart_fieldAccessorTable.ensureFieldAccessorsInitialized(AuthenticateStart.class, Builder.class);
         }

         private Builder() {
            this.mechName_ = "";
            this.authData_ = ByteString.EMPTY;
            this.initialResponse_ = ByteString.EMPTY;
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.mechName_ = "";
            this.authData_ = ByteString.EMPTY;
            this.initialResponse_ = ByteString.EMPTY;
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxSession.AuthenticateStart.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            this.mechName_ = "";
            this.bitField0_ &= -2;
            this.authData_ = ByteString.EMPTY;
            this.bitField0_ &= -3;
            this.initialResponse_ = ByteString.EMPTY;
            this.bitField0_ &= -5;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateStart_descriptor;
         }

         public AuthenticateStart getDefaultInstanceForType() {
            return MysqlxSession.AuthenticateStart.getDefaultInstance();
         }

         public AuthenticateStart build() {
            AuthenticateStart result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public AuthenticateStart buildPartial() {
            AuthenticateStart result = new AuthenticateStart(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.mechName_ = this.mechName_;
            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.authData_ = this.authData_;
            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 4;
            }

            result.initialResponse_ = this.initialResponse_;
            result.bitField0_ = to_bitField0_;
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
            if (other instanceof AuthenticateStart) {
               return this.mergeFrom((AuthenticateStart)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(AuthenticateStart other) {
            if (other == MysqlxSession.AuthenticateStart.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasMechName()) {
                  this.bitField0_ |= 1;
                  this.mechName_ = other.mechName_;
                  this.onChanged();
               }

               if (other.hasAuthData()) {
                  this.setAuthData(other.getAuthData());
               }

               if (other.hasInitialResponse()) {
                  this.setInitialResponse(other.getInitialResponse());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            return this.hasMechName();
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            AuthenticateStart parsedMessage = null;

            try {
               parsedMessage = (AuthenticateStart)MysqlxSession.AuthenticateStart.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (AuthenticateStart)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasMechName() {
            return (this.bitField0_ & 1) != 0;
         }

         public String getMechName() {
            Object ref = this.mechName_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.mechName_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getMechNameBytes() {
            Object ref = this.mechName_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.mechName_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setMechName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.mechName_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearMechName() {
            this.bitField0_ &= -2;
            this.mechName_ = MysqlxSession.AuthenticateStart.getDefaultInstance().getMechName();
            this.onChanged();
            return this;
         }

         public Builder setMechNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.mechName_ = value;
               this.onChanged();
               return this;
            }
         }

         public boolean hasAuthData() {
            return (this.bitField0_ & 2) != 0;
         }

         public ByteString getAuthData() {
            return this.authData_;
         }

         public Builder setAuthData(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.authData_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearAuthData() {
            this.bitField0_ &= -3;
            this.authData_ = MysqlxSession.AuthenticateStart.getDefaultInstance().getAuthData();
            this.onChanged();
            return this;
         }

         public boolean hasInitialResponse() {
            return (this.bitField0_ & 4) != 0;
         }

         public ByteString getInitialResponse() {
            return this.initialResponse_;
         }

         public Builder setInitialResponse(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.initialResponse_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearInitialResponse() {
            this.bitField0_ &= -5;
            this.initialResponse_ = MysqlxSession.AuthenticateStart.getDefaultInstance().getInitialResponse();
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

   public interface AuthenticateStartOrBuilder extends MessageOrBuilder {
      boolean hasMechName();

      String getMechName();

      ByteString getMechNameBytes();

      boolean hasAuthData();

      ByteString getAuthData();

      boolean hasInitialResponse();

      ByteString getInitialResponse();
   }
}
