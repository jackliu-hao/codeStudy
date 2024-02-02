package com.mysql.cj.x.protobuf;

import com.google.protobuf.AbstractMessageLite;
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
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.RepeatedFieldBuilderV3;
import com.google.protobuf.SingleFieldBuilderV3;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxConnection {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_Capability_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_Capability_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_Capabilities_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_Capabilities_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_CapabilitiesGet_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_CapabilitiesSet_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_Close_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_Close_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_Compression_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_Compression_fieldAccessorTable;
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxConnection() {
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
      String[] descriptorData = new String[]{"\n\u0017mysqlx_connection.proto\u0012\u0011Mysqlx.Connection\u001a\u0016mysqlx_datatypes.proto\u001a\fmysqlx.proto\"@\n\nCapability\u0012\f\n\u0004name\u0018\u0001 \u0002(\t\u0012$\n\u0005value\u0018\u0002 \u0002(\u000b2\u0015.Mysqlx.Datatypes.Any\"I\n\fCapabilities\u00123\n\fcapabilities\u0018\u0001 \u0003(\u000b2\u001d.Mysqlx.Connection.Capability:\u0004\u0090ê0\u0002\"\u0017\n\u000fCapabilitiesGet:\u0004\u0088ê0\u0001\"N\n\u000fCapabilitiesSet\u00125\n\fcapabilities\u0018\u0001 \u0002(\u000b2\u001f.Mysqlx.Connection.Capabilities:\u0004\u0088ê0\u0002\"\r\n\u0005Close:\u0004\u0088ê0\u0003\"¯\u0001\n\u000bCompression\u0012\u0019\n\u0011uncompressed_size\u0018\u0001 \u0001(\u0004\u00124\n\u000fserver_messages\u0018\u0002 \u0001(\u000e2\u001b.Mysqlx.ServerMessages.Type\u00124\n\u000fclient_messages\u0018\u0003 \u0001(\u000e2\u001b.Mysqlx.ClientMessages.Type\u0012\u000f\n\u0007payload\u0018\u0004 \u0002(\f:\b\u0090ê0\u0013\u0088ê0.B\u0019\n\u0017com.mysql.cj.x.protobuf"};
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{MysqlxDatatypes.getDescriptor(), Mysqlx.getDescriptor()});
      internal_static_Mysqlx_Connection_Capability_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(0);
      internal_static_Mysqlx_Connection_Capability_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Connection_Capability_descriptor, new String[]{"Name", "Value"});
      internal_static_Mysqlx_Connection_Capabilities_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(1);
      internal_static_Mysqlx_Connection_Capabilities_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Connection_Capabilities_descriptor, new String[]{"Capabilities"});
      internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(2);
      internal_static_Mysqlx_Connection_CapabilitiesGet_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor, new String[0]);
      internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(3);
      internal_static_Mysqlx_Connection_CapabilitiesSet_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor, new String[]{"Capabilities"});
      internal_static_Mysqlx_Connection_Close_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(4);
      internal_static_Mysqlx_Connection_Close_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Connection_Close_descriptor, new String[0]);
      internal_static_Mysqlx_Connection_Compression_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(5);
      internal_static_Mysqlx_Connection_Compression_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Connection_Compression_descriptor, new String[]{"UncompressedSize", "ServerMessages", "ClientMessages", "Payload"});
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.clientMessageId);
      registry.add(Mysqlx.serverMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      MysqlxDatatypes.getDescriptor();
      Mysqlx.getDescriptor();
   }

   public static final class Compression extends GeneratedMessageV3 implements CompressionOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int UNCOMPRESSED_SIZE_FIELD_NUMBER = 1;
      private long uncompressedSize_;
      public static final int SERVER_MESSAGES_FIELD_NUMBER = 2;
      private int serverMessages_;
      public static final int CLIENT_MESSAGES_FIELD_NUMBER = 3;
      private int clientMessages_;
      public static final int PAYLOAD_FIELD_NUMBER = 4;
      private ByteString payload_;
      private byte memoizedIsInitialized;
      private static final Compression DEFAULT_INSTANCE = new Compression();
      /** @deprecated */
      @Deprecated
      public static final Parser<Compression> PARSER = new AbstractParser<Compression>() {
         public Compression parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Compression(input, extensionRegistry);
         }
      };

      private Compression(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Compression() {
         this.memoizedIsInitialized = -1;
         this.serverMessages_ = 0;
         this.clientMessages_ = 1;
         this.payload_ = ByteString.EMPTY;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Compression();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Compression(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                  int rawValue;
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 8:
                        this.bitField0_ |= 1;
                        this.uncompressedSize_ = input.readUInt64();
                        break;
                     case 16:
                        rawValue = input.readEnum();
                        Mysqlx.ServerMessages.Type value = Mysqlx.ServerMessages.Type.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(2, rawValue);
                        } else {
                           this.bitField0_ |= 2;
                           this.serverMessages_ = rawValue;
                        }
                        break;
                     case 24:
                        rawValue = input.readEnum();
                        Mysqlx.ClientMessages.Type value = Mysqlx.ClientMessages.Type.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(3, rawValue);
                        } else {
                           this.bitField0_ |= 4;
                           this.clientMessages_ = rawValue;
                        }
                        break;
                     case 34:
                        this.bitField0_ |= 8;
                        this.payload_ = input.readBytes();
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw (new InvalidProtocolBufferException(var14)).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxConnection.internal_static_Mysqlx_Connection_Compression_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxConnection.internal_static_Mysqlx_Connection_Compression_fieldAccessorTable.ensureFieldAccessorsInitialized(Compression.class, Builder.class);
      }

      public boolean hasUncompressedSize() {
         return (this.bitField0_ & 1) != 0;
      }

      public long getUncompressedSize() {
         return this.uncompressedSize_;
      }

      public boolean hasServerMessages() {
         return (this.bitField0_ & 2) != 0;
      }

      public Mysqlx.ServerMessages.Type getServerMessages() {
         Mysqlx.ServerMessages.Type result = Mysqlx.ServerMessages.Type.valueOf(this.serverMessages_);
         return result == null ? Mysqlx.ServerMessages.Type.OK : result;
      }

      public boolean hasClientMessages() {
         return (this.bitField0_ & 4) != 0;
      }

      public Mysqlx.ClientMessages.Type getClientMessages() {
         Mysqlx.ClientMessages.Type result = Mysqlx.ClientMessages.Type.valueOf(this.clientMessages_);
         return result == null ? Mysqlx.ClientMessages.Type.CON_CAPABILITIES_GET : result;
      }

      public boolean hasPayload() {
         return (this.bitField0_ & 8) != 0;
      }

      public ByteString getPayload() {
         return this.payload_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasPayload()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeUInt64(1, this.uncompressedSize_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(2, this.serverMessages_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeEnum(3, this.clientMessages_);
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeBytes(4, this.payload_);
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
               size += CodedOutputStream.computeUInt64Size(1, this.uncompressedSize_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(2, this.serverMessages_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeEnumSize(3, this.clientMessages_);
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeBytesSize(4, this.payload_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Compression)) {
            return super.equals(obj);
         } else {
            Compression other = (Compression)obj;
            if (this.hasUncompressedSize() != other.hasUncompressedSize()) {
               return false;
            } else if (this.hasUncompressedSize() && this.getUncompressedSize() != other.getUncompressedSize()) {
               return false;
            } else if (this.hasServerMessages() != other.hasServerMessages()) {
               return false;
            } else if (this.hasServerMessages() && this.serverMessages_ != other.serverMessages_) {
               return false;
            } else if (this.hasClientMessages() != other.hasClientMessages()) {
               return false;
            } else if (this.hasClientMessages() && this.clientMessages_ != other.clientMessages_) {
               return false;
            } else if (this.hasPayload() != other.hasPayload()) {
               return false;
            } else if (this.hasPayload() && !this.getPayload().equals(other.getPayload())) {
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
            if (this.hasUncompressedSize()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + Internal.hashLong(this.getUncompressedSize());
            }

            if (this.hasServerMessages()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.serverMessages_;
            }

            if (this.hasClientMessages()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.clientMessages_;
            }

            if (this.hasPayload()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getPayload().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Compression parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Compression)PARSER.parseFrom(data);
      }

      public static Compression parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Compression)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Compression parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Compression)PARSER.parseFrom(data);
      }

      public static Compression parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Compression)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Compression parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Compression)PARSER.parseFrom(data);
      }

      public static Compression parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Compression)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Compression parseFrom(InputStream input) throws IOException {
         return (Compression)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Compression parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Compression)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Compression parseDelimitedFrom(InputStream input) throws IOException {
         return (Compression)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Compression parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Compression)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Compression parseFrom(CodedInputStream input) throws IOException {
         return (Compression)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Compression parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Compression)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Compression prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Compression getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Compression> parser() {
         return PARSER;
      }

      public Parser<Compression> getParserForType() {
         return PARSER;
      }

      public Compression getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Compression(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Compression(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements CompressionOrBuilder {
         private int bitField0_;
         private long uncompressedSize_;
         private int serverMessages_;
         private int clientMessages_;
         private ByteString payload_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Compression_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Compression_fieldAccessorTable.ensureFieldAccessorsInitialized(Compression.class, Builder.class);
         }

         private Builder() {
            this.serverMessages_ = 0;
            this.clientMessages_ = 1;
            this.payload_ = ByteString.EMPTY;
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.serverMessages_ = 0;
            this.clientMessages_ = 1;
            this.payload_ = ByteString.EMPTY;
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxConnection.Compression.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            this.uncompressedSize_ = 0L;
            this.bitField0_ &= -2;
            this.serverMessages_ = 0;
            this.bitField0_ &= -3;
            this.clientMessages_ = 1;
            this.bitField0_ &= -5;
            this.payload_ = ByteString.EMPTY;
            this.bitField0_ &= -9;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Compression_descriptor;
         }

         public Compression getDefaultInstanceForType() {
            return MysqlxConnection.Compression.getDefaultInstance();
         }

         public Compression build() {
            Compression result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Compression buildPartial() {
            Compression result = new Compression(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.uncompressedSize_ = this.uncompressedSize_;
               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.serverMessages_ = this.serverMessages_;
            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 4;
            }

            result.clientMessages_ = this.clientMessages_;
            if ((from_bitField0_ & 8) != 0) {
               to_bitField0_ |= 8;
            }

            result.payload_ = this.payload_;
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
            if (other instanceof Compression) {
               return this.mergeFrom((Compression)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Compression other) {
            if (other == MysqlxConnection.Compression.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasUncompressedSize()) {
                  this.setUncompressedSize(other.getUncompressedSize());
               }

               if (other.hasServerMessages()) {
                  this.setServerMessages(other.getServerMessages());
               }

               if (other.hasClientMessages()) {
                  this.setClientMessages(other.getClientMessages());
               }

               if (other.hasPayload()) {
                  this.setPayload(other.getPayload());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            return this.hasPayload();
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Compression parsedMessage = null;

            try {
               parsedMessage = (Compression)MysqlxConnection.Compression.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Compression)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasUncompressedSize() {
            return (this.bitField0_ & 1) != 0;
         }

         public long getUncompressedSize() {
            return this.uncompressedSize_;
         }

         public Builder setUncompressedSize(long value) {
            this.bitField0_ |= 1;
            this.uncompressedSize_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearUncompressedSize() {
            this.bitField0_ &= -2;
            this.uncompressedSize_ = 0L;
            this.onChanged();
            return this;
         }

         public boolean hasServerMessages() {
            return (this.bitField0_ & 2) != 0;
         }

         public Mysqlx.ServerMessages.Type getServerMessages() {
            Mysqlx.ServerMessages.Type result = Mysqlx.ServerMessages.Type.valueOf(this.serverMessages_);
            return result == null ? Mysqlx.ServerMessages.Type.OK : result;
         }

         public Builder setServerMessages(Mysqlx.ServerMessages.Type value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.serverMessages_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearServerMessages() {
            this.bitField0_ &= -3;
            this.serverMessages_ = 0;
            this.onChanged();
            return this;
         }

         public boolean hasClientMessages() {
            return (this.bitField0_ & 4) != 0;
         }

         public Mysqlx.ClientMessages.Type getClientMessages() {
            Mysqlx.ClientMessages.Type result = Mysqlx.ClientMessages.Type.valueOf(this.clientMessages_);
            return result == null ? Mysqlx.ClientMessages.Type.CON_CAPABILITIES_GET : result;
         }

         public Builder setClientMessages(Mysqlx.ClientMessages.Type value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.clientMessages_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearClientMessages() {
            this.bitField0_ &= -5;
            this.clientMessages_ = 1;
            this.onChanged();
            return this;
         }

         public boolean hasPayload() {
            return (this.bitField0_ & 8) != 0;
         }

         public ByteString getPayload() {
            return this.payload_;
         }

         public Builder setPayload(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.payload_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearPayload() {
            this.bitField0_ &= -9;
            this.payload_ = MysqlxConnection.Compression.getDefaultInstance().getPayload();
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

   public interface CompressionOrBuilder extends MessageOrBuilder {
      boolean hasUncompressedSize();

      long getUncompressedSize();

      boolean hasServerMessages();

      Mysqlx.ServerMessages.Type getServerMessages();

      boolean hasClientMessages();

      Mysqlx.ClientMessages.Type getClientMessages();

      boolean hasPayload();

      ByteString getPayload();
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
         return MysqlxConnection.internal_static_Mysqlx_Connection_Close_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxConnection.internal_static_Mysqlx_Connection_Close_fieldAccessorTable.ensureFieldAccessorsInitialized(Close.class, Builder.class);
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
            return MysqlxConnection.internal_static_Mysqlx_Connection_Close_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Close_fieldAccessorTable.ensureFieldAccessorsInitialized(Close.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxConnection.Close.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Close_descriptor;
         }

         public Close getDefaultInstanceForType() {
            return MysqlxConnection.Close.getDefaultInstance();
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
            if (other == MysqlxConnection.Close.getDefaultInstance()) {
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
               parsedMessage = (Close)MysqlxConnection.Close.PARSER.parsePartialFrom(input, extensionRegistry);
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

   public static final class CapabilitiesSet extends GeneratedMessageV3 implements CapabilitiesSetOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int CAPABILITIES_FIELD_NUMBER = 1;
      private Capabilities capabilities_;
      private byte memoizedIsInitialized;
      private static final CapabilitiesSet DEFAULT_INSTANCE = new CapabilitiesSet();
      /** @deprecated */
      @Deprecated
      public static final Parser<CapabilitiesSet> PARSER = new AbstractParser<CapabilitiesSet>() {
         public CapabilitiesSet parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new CapabilitiesSet(input, extensionRegistry);
         }
      };

      private CapabilitiesSet(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private CapabilitiesSet() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new CapabilitiesSet();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private CapabilitiesSet(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        Capabilities.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.capabilities_.toBuilder();
                        }

                        this.capabilities_ = (Capabilities)input.readMessage(MysqlxConnection.Capabilities.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.capabilities_);
                           this.capabilities_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
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
         return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesSet_fieldAccessorTable.ensureFieldAccessorsInitialized(CapabilitiesSet.class, Builder.class);
      }

      public boolean hasCapabilities() {
         return (this.bitField0_ & 1) != 0;
      }

      public Capabilities getCapabilities() {
         return this.capabilities_ == null ? MysqlxConnection.Capabilities.getDefaultInstance() : this.capabilities_;
      }

      public CapabilitiesOrBuilder getCapabilitiesOrBuilder() {
         return this.capabilities_ == null ? MysqlxConnection.Capabilities.getDefaultInstance() : this.capabilities_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCapabilities()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCapabilities().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getCapabilities());
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
               size += CodedOutputStream.computeMessageSize(1, this.getCapabilities());
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof CapabilitiesSet)) {
            return super.equals(obj);
         } else {
            CapabilitiesSet other = (CapabilitiesSet)obj;
            if (this.hasCapabilities() != other.hasCapabilities()) {
               return false;
            } else if (this.hasCapabilities() && !this.getCapabilities().equals(other.getCapabilities())) {
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
            if (this.hasCapabilities()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCapabilities().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static CapabilitiesSet parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (CapabilitiesSet)PARSER.parseFrom(data);
      }

      public static CapabilitiesSet parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (CapabilitiesSet)PARSER.parseFrom(data, extensionRegistry);
      }

      public static CapabilitiesSet parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (CapabilitiesSet)PARSER.parseFrom(data);
      }

      public static CapabilitiesSet parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (CapabilitiesSet)PARSER.parseFrom(data, extensionRegistry);
      }

      public static CapabilitiesSet parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (CapabilitiesSet)PARSER.parseFrom(data);
      }

      public static CapabilitiesSet parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (CapabilitiesSet)PARSER.parseFrom(data, extensionRegistry);
      }

      public static CapabilitiesSet parseFrom(InputStream input) throws IOException {
         return (CapabilitiesSet)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static CapabilitiesSet parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (CapabilitiesSet)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static CapabilitiesSet parseDelimitedFrom(InputStream input) throws IOException {
         return (CapabilitiesSet)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static CapabilitiesSet parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (CapabilitiesSet)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static CapabilitiesSet parseFrom(CodedInputStream input) throws IOException {
         return (CapabilitiesSet)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static CapabilitiesSet parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (CapabilitiesSet)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(CapabilitiesSet prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static CapabilitiesSet getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<CapabilitiesSet> parser() {
         return PARSER;
      }

      public Parser<CapabilitiesSet> getParserForType() {
         return PARSER;
      }

      public CapabilitiesSet getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      CapabilitiesSet(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      CapabilitiesSet(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements CapabilitiesSetOrBuilder {
         private int bitField0_;
         private Capabilities capabilities_;
         private SingleFieldBuilderV3<Capabilities, Capabilities.Builder, CapabilitiesOrBuilder> capabilitiesBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesSet_fieldAccessorTable.ensureFieldAccessorsInitialized(CapabilitiesSet.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxConnection.CapabilitiesSet.alwaysUseFieldBuilders) {
               this.getCapabilitiesFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.capabilitiesBuilder_ == null) {
               this.capabilities_ = null;
            } else {
               this.capabilitiesBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor;
         }

         public CapabilitiesSet getDefaultInstanceForType() {
            return MysqlxConnection.CapabilitiesSet.getDefaultInstance();
         }

         public CapabilitiesSet build() {
            CapabilitiesSet result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public CapabilitiesSet buildPartial() {
            CapabilitiesSet result = new CapabilitiesSet(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.capabilitiesBuilder_ == null) {
                  result.capabilities_ = this.capabilities_;
               } else {
                  result.capabilities_ = (Capabilities)this.capabilitiesBuilder_.build();
               }

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
            if (other instanceof CapabilitiesSet) {
               return this.mergeFrom((CapabilitiesSet)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(CapabilitiesSet other) {
            if (other == MysqlxConnection.CapabilitiesSet.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCapabilities()) {
                  this.mergeCapabilities(other.getCapabilities());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasCapabilities()) {
               return false;
            } else {
               return this.getCapabilities().isInitialized();
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            CapabilitiesSet parsedMessage = null;

            try {
               parsedMessage = (CapabilitiesSet)MysqlxConnection.CapabilitiesSet.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (CapabilitiesSet)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasCapabilities() {
            return (this.bitField0_ & 1) != 0;
         }

         public Capabilities getCapabilities() {
            if (this.capabilitiesBuilder_ == null) {
               return this.capabilities_ == null ? MysqlxConnection.Capabilities.getDefaultInstance() : this.capabilities_;
            } else {
               return (Capabilities)this.capabilitiesBuilder_.getMessage();
            }
         }

         public Builder setCapabilities(Capabilities value) {
            if (this.capabilitiesBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.capabilities_ = value;
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder setCapabilities(Capabilities.Builder builderForValue) {
            if (this.capabilitiesBuilder_ == null) {
               this.capabilities_ = builderForValue.build();
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder mergeCapabilities(Capabilities value) {
            if (this.capabilitiesBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.capabilities_ != null && this.capabilities_ != MysqlxConnection.Capabilities.getDefaultInstance()) {
                  this.capabilities_ = MysqlxConnection.Capabilities.newBuilder(this.capabilities_).mergeFrom(value).buildPartial();
               } else {
                  this.capabilities_ = value;
               }

               this.onChanged();
            } else {
               this.capabilitiesBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder clearCapabilities() {
            if (this.capabilitiesBuilder_ == null) {
               this.capabilities_ = null;
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public Capabilities.Builder getCapabilitiesBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return (Capabilities.Builder)this.getCapabilitiesFieldBuilder().getBuilder();
         }

         public CapabilitiesOrBuilder getCapabilitiesOrBuilder() {
            if (this.capabilitiesBuilder_ != null) {
               return (CapabilitiesOrBuilder)this.capabilitiesBuilder_.getMessageOrBuilder();
            } else {
               return this.capabilities_ == null ? MysqlxConnection.Capabilities.getDefaultInstance() : this.capabilities_;
            }
         }

         private SingleFieldBuilderV3<Capabilities, Capabilities.Builder, CapabilitiesOrBuilder> getCapabilitiesFieldBuilder() {
            if (this.capabilitiesBuilder_ == null) {
               this.capabilitiesBuilder_ = new SingleFieldBuilderV3(this.getCapabilities(), this.getParentForChildren(), this.isClean());
               this.capabilities_ = null;
            }

            return this.capabilitiesBuilder_;
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

   public interface CapabilitiesSetOrBuilder extends MessageOrBuilder {
      boolean hasCapabilities();

      Capabilities getCapabilities();

      CapabilitiesOrBuilder getCapabilitiesOrBuilder();
   }

   public static final class CapabilitiesGet extends GeneratedMessageV3 implements CapabilitiesGetOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized;
      private static final CapabilitiesGet DEFAULT_INSTANCE = new CapabilitiesGet();
      /** @deprecated */
      @Deprecated
      public static final Parser<CapabilitiesGet> PARSER = new AbstractParser<CapabilitiesGet>() {
         public CapabilitiesGet parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new CapabilitiesGet(input, extensionRegistry);
         }
      };

      private CapabilitiesGet(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private CapabilitiesGet() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new CapabilitiesGet();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private CapabilitiesGet(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesGet_fieldAccessorTable.ensureFieldAccessorsInitialized(CapabilitiesGet.class, Builder.class);
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
         } else if (!(obj instanceof CapabilitiesGet)) {
            return super.equals(obj);
         } else {
            CapabilitiesGet other = (CapabilitiesGet)obj;
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

      public static CapabilitiesGet parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (CapabilitiesGet)PARSER.parseFrom(data);
      }

      public static CapabilitiesGet parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (CapabilitiesGet)PARSER.parseFrom(data, extensionRegistry);
      }

      public static CapabilitiesGet parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (CapabilitiesGet)PARSER.parseFrom(data);
      }

      public static CapabilitiesGet parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (CapabilitiesGet)PARSER.parseFrom(data, extensionRegistry);
      }

      public static CapabilitiesGet parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (CapabilitiesGet)PARSER.parseFrom(data);
      }

      public static CapabilitiesGet parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (CapabilitiesGet)PARSER.parseFrom(data, extensionRegistry);
      }

      public static CapabilitiesGet parseFrom(InputStream input) throws IOException {
         return (CapabilitiesGet)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static CapabilitiesGet parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (CapabilitiesGet)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static CapabilitiesGet parseDelimitedFrom(InputStream input) throws IOException {
         return (CapabilitiesGet)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static CapabilitiesGet parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (CapabilitiesGet)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static CapabilitiesGet parseFrom(CodedInputStream input) throws IOException {
         return (CapabilitiesGet)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static CapabilitiesGet parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (CapabilitiesGet)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(CapabilitiesGet prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static CapabilitiesGet getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<CapabilitiesGet> parser() {
         return PARSER;
      }

      public Parser<CapabilitiesGet> getParserForType() {
         return PARSER;
      }

      public CapabilitiesGet getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      CapabilitiesGet(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      CapabilitiesGet(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements CapabilitiesGetOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesGet_fieldAccessorTable.ensureFieldAccessorsInitialized(CapabilitiesGet.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxConnection.CapabilitiesGet.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor;
         }

         public CapabilitiesGet getDefaultInstanceForType() {
            return MysqlxConnection.CapabilitiesGet.getDefaultInstance();
         }

         public CapabilitiesGet build() {
            CapabilitiesGet result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public CapabilitiesGet buildPartial() {
            CapabilitiesGet result = new CapabilitiesGet(this);
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
            if (other instanceof CapabilitiesGet) {
               return this.mergeFrom((CapabilitiesGet)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(CapabilitiesGet other) {
            if (other == MysqlxConnection.CapabilitiesGet.getDefaultInstance()) {
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
            CapabilitiesGet parsedMessage = null;

            try {
               parsedMessage = (CapabilitiesGet)MysqlxConnection.CapabilitiesGet.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (CapabilitiesGet)var8.getUnfinishedMessage();
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

   public interface CapabilitiesGetOrBuilder extends MessageOrBuilder {
   }

   public static final class Capabilities extends GeneratedMessageV3 implements CapabilitiesOrBuilder {
      private static final long serialVersionUID = 0L;
      public static final int CAPABILITIES_FIELD_NUMBER = 1;
      private List<Capability> capabilities_;
      private byte memoizedIsInitialized;
      private static final Capabilities DEFAULT_INSTANCE = new Capabilities();
      /** @deprecated */
      @Deprecated
      public static final Parser<Capabilities> PARSER = new AbstractParser<Capabilities>() {
         public Capabilities parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Capabilities(input, extensionRegistry);
         }
      };

      private Capabilities(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Capabilities() {
         this.memoizedIsInitialized = -1;
         this.capabilities_ = Collections.emptyList();
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Capabilities();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Capabilities(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        if (!(mutable_bitField0_ & true)) {
                           this.capabilities_ = new ArrayList();
                           mutable_bitField0_ |= true;
                        }

                        this.capabilities_.add(input.readMessage(MysqlxConnection.Capability.PARSER, extensionRegistry));
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
               if (mutable_bitField0_ & true) {
                  this.capabilities_ = Collections.unmodifiableList(this.capabilities_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxConnection.internal_static_Mysqlx_Connection_Capabilities_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxConnection.internal_static_Mysqlx_Connection_Capabilities_fieldAccessorTable.ensureFieldAccessorsInitialized(Capabilities.class, Builder.class);
      }

      public List<Capability> getCapabilitiesList() {
         return this.capabilities_;
      }

      public List<? extends CapabilityOrBuilder> getCapabilitiesOrBuilderList() {
         return this.capabilities_;
      }

      public int getCapabilitiesCount() {
         return this.capabilities_.size();
      }

      public Capability getCapabilities(int index) {
         return (Capability)this.capabilities_.get(index);
      }

      public CapabilityOrBuilder getCapabilitiesOrBuilder(int index) {
         return (CapabilityOrBuilder)this.capabilities_.get(index);
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            for(int i = 0; i < this.getCapabilitiesCount(); ++i) {
               if (!this.getCapabilities(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         for(int i = 0; i < this.capabilities_.size(); ++i) {
            output.writeMessage(1, (MessageLite)this.capabilities_.get(i));
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;

            for(int i = 0; i < this.capabilities_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(1, (MessageLite)this.capabilities_.get(i));
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Capabilities)) {
            return super.equals(obj);
         } else {
            Capabilities other = (Capabilities)obj;
            if (!this.getCapabilitiesList().equals(other.getCapabilitiesList())) {
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
            if (this.getCapabilitiesCount() > 0) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCapabilitiesList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Capabilities parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Capabilities)PARSER.parseFrom(data);
      }

      public static Capabilities parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Capabilities)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Capabilities parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Capabilities)PARSER.parseFrom(data);
      }

      public static Capabilities parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Capabilities)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Capabilities parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Capabilities)PARSER.parseFrom(data);
      }

      public static Capabilities parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Capabilities)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Capabilities parseFrom(InputStream input) throws IOException {
         return (Capabilities)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Capabilities parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Capabilities)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Capabilities parseDelimitedFrom(InputStream input) throws IOException {
         return (Capabilities)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Capabilities parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Capabilities)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Capabilities parseFrom(CodedInputStream input) throws IOException {
         return (Capabilities)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Capabilities parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Capabilities)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Capabilities prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Capabilities getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Capabilities> parser() {
         return PARSER;
      }

      public Parser<Capabilities> getParserForType() {
         return PARSER;
      }

      public Capabilities getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Capabilities(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Capabilities(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements CapabilitiesOrBuilder {
         private int bitField0_;
         private List<Capability> capabilities_;
         private RepeatedFieldBuilderV3<Capability, Capability.Builder, CapabilityOrBuilder> capabilitiesBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Capabilities_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Capabilities_fieldAccessorTable.ensureFieldAccessorsInitialized(Capabilities.class, Builder.class);
         }

         private Builder() {
            this.capabilities_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.capabilities_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxConnection.Capabilities.alwaysUseFieldBuilders) {
               this.getCapabilitiesFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.capabilitiesBuilder_ == null) {
               this.capabilities_ = Collections.emptyList();
               this.bitField0_ &= -2;
            } else {
               this.capabilitiesBuilder_.clear();
            }

            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Capabilities_descriptor;
         }

         public Capabilities getDefaultInstanceForType() {
            return MysqlxConnection.Capabilities.getDefaultInstance();
         }

         public Capabilities build() {
            Capabilities result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Capabilities buildPartial() {
            Capabilities result = new Capabilities(this);
            int from_bitField0_ = this.bitField0_;
            if (this.capabilitiesBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0) {
                  this.capabilities_ = Collections.unmodifiableList(this.capabilities_);
                  this.bitField0_ &= -2;
               }

               result.capabilities_ = this.capabilities_;
            } else {
               result.capabilities_ = this.capabilitiesBuilder_.build();
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
            if (other instanceof Capabilities) {
               return this.mergeFrom((Capabilities)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Capabilities other) {
            if (other == MysqlxConnection.Capabilities.getDefaultInstance()) {
               return this;
            } else {
               if (this.capabilitiesBuilder_ == null) {
                  if (!other.capabilities_.isEmpty()) {
                     if (this.capabilities_.isEmpty()) {
                        this.capabilities_ = other.capabilities_;
                        this.bitField0_ &= -2;
                     } else {
                        this.ensureCapabilitiesIsMutable();
                        this.capabilities_.addAll(other.capabilities_);
                     }

                     this.onChanged();
                  }
               } else if (!other.capabilities_.isEmpty()) {
                  if (this.capabilitiesBuilder_.isEmpty()) {
                     this.capabilitiesBuilder_.dispose();
                     this.capabilitiesBuilder_ = null;
                     this.capabilities_ = other.capabilities_;
                     this.bitField0_ &= -2;
                     this.capabilitiesBuilder_ = MysqlxConnection.Capabilities.alwaysUseFieldBuilders ? this.getCapabilitiesFieldBuilder() : null;
                  } else {
                     this.capabilitiesBuilder_.addAllMessages(other.capabilities_);
                  }
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            for(int i = 0; i < this.getCapabilitiesCount(); ++i) {
               if (!this.getCapabilities(i).isInitialized()) {
                  return false;
               }
            }

            return true;
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Capabilities parsedMessage = null;

            try {
               parsedMessage = (Capabilities)MysqlxConnection.Capabilities.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Capabilities)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         private void ensureCapabilitiesIsMutable() {
            if ((this.bitField0_ & 1) == 0) {
               this.capabilities_ = new ArrayList(this.capabilities_);
               this.bitField0_ |= 1;
            }

         }

         public List<Capability> getCapabilitiesList() {
            return this.capabilitiesBuilder_ == null ? Collections.unmodifiableList(this.capabilities_) : this.capabilitiesBuilder_.getMessageList();
         }

         public int getCapabilitiesCount() {
            return this.capabilitiesBuilder_ == null ? this.capabilities_.size() : this.capabilitiesBuilder_.getCount();
         }

         public Capability getCapabilities(int index) {
            return this.capabilitiesBuilder_ == null ? (Capability)this.capabilities_.get(index) : (Capability)this.capabilitiesBuilder_.getMessage(index);
         }

         public Builder setCapabilities(int index, Capability value) {
            if (this.capabilitiesBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureCapabilitiesIsMutable();
               this.capabilities_.set(index, value);
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setCapabilities(int index, Capability.Builder builderForValue) {
            if (this.capabilitiesBuilder_ == null) {
               this.ensureCapabilitiesIsMutable();
               this.capabilities_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addCapabilities(Capability value) {
            if (this.capabilitiesBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureCapabilitiesIsMutable();
               this.capabilities_.add(value);
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addCapabilities(int index, Capability value) {
            if (this.capabilitiesBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureCapabilitiesIsMutable();
               this.capabilities_.add(index, value);
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addCapabilities(Capability.Builder builderForValue) {
            if (this.capabilitiesBuilder_ == null) {
               this.ensureCapabilitiesIsMutable();
               this.capabilities_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addCapabilities(int index, Capability.Builder builderForValue) {
            if (this.capabilitiesBuilder_ == null) {
               this.ensureCapabilitiesIsMutable();
               this.capabilities_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllCapabilities(Iterable<? extends Capability> values) {
            if (this.capabilitiesBuilder_ == null) {
               this.ensureCapabilitiesIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.capabilities_);
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearCapabilities() {
            if (this.capabilitiesBuilder_ == null) {
               this.capabilities_ = Collections.emptyList();
               this.bitField0_ &= -2;
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.clear();
            }

            return this;
         }

         public Builder removeCapabilities(int index) {
            if (this.capabilitiesBuilder_ == null) {
               this.ensureCapabilitiesIsMutable();
               this.capabilities_.remove(index);
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.remove(index);
            }

            return this;
         }

         public Capability.Builder getCapabilitiesBuilder(int index) {
            return (Capability.Builder)this.getCapabilitiesFieldBuilder().getBuilder(index);
         }

         public CapabilityOrBuilder getCapabilitiesOrBuilder(int index) {
            return this.capabilitiesBuilder_ == null ? (CapabilityOrBuilder)this.capabilities_.get(index) : (CapabilityOrBuilder)this.capabilitiesBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends CapabilityOrBuilder> getCapabilitiesOrBuilderList() {
            return this.capabilitiesBuilder_ != null ? this.capabilitiesBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.capabilities_);
         }

         public Capability.Builder addCapabilitiesBuilder() {
            return (Capability.Builder)this.getCapabilitiesFieldBuilder().addBuilder(MysqlxConnection.Capability.getDefaultInstance());
         }

         public Capability.Builder addCapabilitiesBuilder(int index) {
            return (Capability.Builder)this.getCapabilitiesFieldBuilder().addBuilder(index, MysqlxConnection.Capability.getDefaultInstance());
         }

         public List<Capability.Builder> getCapabilitiesBuilderList() {
            return this.getCapabilitiesFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<Capability, Capability.Builder, CapabilityOrBuilder> getCapabilitiesFieldBuilder() {
            if (this.capabilitiesBuilder_ == null) {
               this.capabilitiesBuilder_ = new RepeatedFieldBuilderV3(this.capabilities_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
               this.capabilities_ = null;
            }

            return this.capabilitiesBuilder_;
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

   public interface CapabilitiesOrBuilder extends MessageOrBuilder {
      List<Capability> getCapabilitiesList();

      Capability getCapabilities(int var1);

      int getCapabilitiesCount();

      List<? extends CapabilityOrBuilder> getCapabilitiesOrBuilderList();

      CapabilityOrBuilder getCapabilitiesOrBuilder(int var1);
   }

   public static final class Capability extends GeneratedMessageV3 implements CapabilityOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private volatile Object name_;
      public static final int VALUE_FIELD_NUMBER = 2;
      private MysqlxDatatypes.Any value_;
      private byte memoizedIsInitialized;
      private static final Capability DEFAULT_INSTANCE = new Capability();
      /** @deprecated */
      @Deprecated
      public static final Parser<Capability> PARSER = new AbstractParser<Capability>() {
         public Capability parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Capability(input, extensionRegistry);
         }
      };

      private Capability(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Capability() {
         this.memoizedIsInitialized = -1;
         this.name_ = "";
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Capability();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Capability(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.name_ = bs;
                        break;
                     case 18:
                        MysqlxDatatypes.Any.Builder subBuilder = null;
                        if ((this.bitField0_ & 2) != 0) {
                           subBuilder = this.value_.toBuilder();
                        }

                        this.value_ = (MysqlxDatatypes.Any)input.readMessage(MysqlxDatatypes.Any.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.value_);
                           this.value_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 2;
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
         return MysqlxConnection.internal_static_Mysqlx_Connection_Capability_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxConnection.internal_static_Mysqlx_Connection_Capability_fieldAccessorTable.ensureFieldAccessorsInitialized(Capability.class, Builder.class);
      }

      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      public String getName() {
         Object ref = this.name_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.name_ = s;
            }

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
         return (this.bitField0_ & 2) != 0;
      }

      public MysqlxDatatypes.Any getValue() {
         return this.value_ == null ? MysqlxDatatypes.Any.getDefaultInstance() : this.value_;
      }

      public MysqlxDatatypes.AnyOrBuilder getValueOrBuilder() {
         return this.value_ == null ? MysqlxDatatypes.Any.getDefaultInstance() : this.value_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasName()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.hasValue()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getValue().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(output, 1, this.name_);
         }

         if ((this.bitField0_ & 2) != 0) {
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
            if ((this.bitField0_ & 1) != 0) {
               size += GeneratedMessageV3.computeStringSize(1, this.name_);
            }

            if ((this.bitField0_ & 2) != 0) {
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
         } else if (!(obj instanceof Capability)) {
            return super.equals(obj);
         } else {
            Capability other = (Capability)obj;
            if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
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
            if (this.hasName()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getName().hashCode();
            }

            if (this.hasValue()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getValue().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Capability parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Capability)PARSER.parseFrom(data);
      }

      public static Capability parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Capability)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Capability parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Capability)PARSER.parseFrom(data);
      }

      public static Capability parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Capability)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Capability parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Capability)PARSER.parseFrom(data);
      }

      public static Capability parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Capability)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Capability parseFrom(InputStream input) throws IOException {
         return (Capability)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Capability parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Capability)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Capability parseDelimitedFrom(InputStream input) throws IOException {
         return (Capability)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Capability parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Capability)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Capability parseFrom(CodedInputStream input) throws IOException {
         return (Capability)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Capability parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Capability)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Capability prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Capability getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Capability> parser() {
         return PARSER;
      }

      public Parser<Capability> getParserForType() {
         return PARSER;
      }

      public Capability getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Capability(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Capability(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements CapabilityOrBuilder {
         private int bitField0_;
         private Object name_;
         private MysqlxDatatypes.Any value_;
         private SingleFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> valueBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Capability_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Capability_fieldAccessorTable.ensureFieldAccessorsInitialized(Capability.class, Builder.class);
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
            if (MysqlxConnection.Capability.alwaysUseFieldBuilders) {
               this.getValueFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            this.name_ = "";
            this.bitField0_ &= -2;
            if (this.valueBuilder_ == null) {
               this.value_ = null;
            } else {
               this.valueBuilder_.clear();
            }

            this.bitField0_ &= -3;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Capability_descriptor;
         }

         public Capability getDefaultInstanceForType() {
            return MysqlxConnection.Capability.getDefaultInstance();
         }

         public Capability build() {
            Capability result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Capability buildPartial() {
            Capability result = new Capability(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.name_ = this.name_;
            if ((from_bitField0_ & 2) != 0) {
               if (this.valueBuilder_ == null) {
                  result.value_ = this.value_;
               } else {
                  result.value_ = (MysqlxDatatypes.Any)this.valueBuilder_.build();
               }

               to_bitField0_ |= 2;
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
            if (other instanceof Capability) {
               return this.mergeFrom((Capability)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Capability other) {
            if (other == MysqlxConnection.Capability.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasName()) {
                  this.bitField0_ |= 1;
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
            if (!this.hasName()) {
               return false;
            } else if (!this.hasValue()) {
               return false;
            } else {
               return this.getValue().isInitialized();
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Capability parsedMessage = null;

            try {
               parsedMessage = (Capability)MysqlxConnection.Capability.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Capability)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasName() {
            return (this.bitField0_ & 1) != 0;
         }

         public String getName() {
            Object ref = this.name_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.name_ = s;
               }

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
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearName() {
            this.bitField0_ &= -2;
            this.name_ = MysqlxConnection.Capability.getDefaultInstance().getName();
            this.onChanged();
            return this;
         }

         public Builder setNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public boolean hasValue() {
            return (this.bitField0_ & 2) != 0;
         }

         public MysqlxDatatypes.Any getValue() {
            if (this.valueBuilder_ == null) {
               return this.value_ == null ? MysqlxDatatypes.Any.getDefaultInstance() : this.value_;
            } else {
               return (MysqlxDatatypes.Any)this.valueBuilder_.getMessage();
            }
         }

         public Builder setValue(MysqlxDatatypes.Any value) {
            if (this.valueBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.value_ = value;
               this.onChanged();
            } else {
               this.valueBuilder_.setMessage(value);
            }

            this.bitField0_ |= 2;
            return this;
         }

         public Builder setValue(MysqlxDatatypes.Any.Builder builderForValue) {
            if (this.valueBuilder_ == null) {
               this.value_ = builderForValue.build();
               this.onChanged();
            } else {
               this.valueBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 2;
            return this;
         }

         public Builder mergeValue(MysqlxDatatypes.Any value) {
            if (this.valueBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0 && this.value_ != null && this.value_ != MysqlxDatatypes.Any.getDefaultInstance()) {
                  this.value_ = MysqlxDatatypes.Any.newBuilder(this.value_).mergeFrom(value).buildPartial();
               } else {
                  this.value_ = value;
               }

               this.onChanged();
            } else {
               this.valueBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 2;
            return this;
         }

         public Builder clearValue() {
            if (this.valueBuilder_ == null) {
               this.value_ = null;
               this.onChanged();
            } else {
               this.valueBuilder_.clear();
            }

            this.bitField0_ &= -3;
            return this;
         }

         public MysqlxDatatypes.Any.Builder getValueBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return (MysqlxDatatypes.Any.Builder)this.getValueFieldBuilder().getBuilder();
         }

         public MysqlxDatatypes.AnyOrBuilder getValueOrBuilder() {
            if (this.valueBuilder_ != null) {
               return (MysqlxDatatypes.AnyOrBuilder)this.valueBuilder_.getMessageOrBuilder();
            } else {
               return this.value_ == null ? MysqlxDatatypes.Any.getDefaultInstance() : this.value_;
            }
         }

         private SingleFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> getValueFieldBuilder() {
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

   public interface CapabilityOrBuilder extends MessageOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      boolean hasValue();

      MysqlxDatatypes.Any getValue();

      MysqlxDatatypes.AnyOrBuilder getValueOrBuilder();
   }
}
