package com.mysql.cj.x.protobuf;

import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionLite;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Mysqlx {
   public static final int CLIENT_MESSAGE_ID_FIELD_NUMBER = 100001;
   public static final GeneratedMessage.GeneratedExtension<DescriptorProtos.MessageOptions, ClientMessages.Type> clientMessageId = GeneratedMessage.newFileScopedGeneratedExtension(ClientMessages.Type.class, (Message)null);
   public static final int SERVER_MESSAGE_ID_FIELD_NUMBER = 100002;
   public static final GeneratedMessage.GeneratedExtension<DescriptorProtos.MessageOptions, ServerMessages.Type> serverMessageId = GeneratedMessage.newFileScopedGeneratedExtension(ServerMessages.Type.class, (Message)null);
   private static final Descriptors.Descriptor internal_static_Mysqlx_ClientMessages_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_ClientMessages_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_ServerMessages_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_ServerMessages_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Ok_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Ok_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Error_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Error_fieldAccessorTable;
   private static Descriptors.FileDescriptor descriptor;

   private Mysqlx() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite registry) {
      registry.add((ExtensionLite)clientMessageId);
      registry.add((ExtensionLite)serverMessageId);
   }

   public static void registerAllExtensions(ExtensionRegistry registry) {
      registerAllExtensions((ExtensionRegistryLite)registry);
   }

   public static Descriptors.FileDescriptor getDescriptor() {
      return descriptor;
   }

   static {
      String[] descriptorData = new String[]{"\n\fmysqlx.proto\u0012\u0006Mysqlx\u001a google/protobuf/descriptor.proto\"ü\u0003\n\u000eClientMessages\"é\u0003\n\u0004Type\u0012\u0018\n\u0014CON_CAPABILITIES_GET\u0010\u0001\u0012\u0018\n\u0014CON_CAPABILITIES_SET\u0010\u0002\u0012\r\n\tCON_CLOSE\u0010\u0003\u0012\u001b\n\u0017SESS_AUTHENTICATE_START\u0010\u0004\u0012\u001e\n\u001aSESS_AUTHENTICATE_CONTINUE\u0010\u0005\u0012\u000e\n\nSESS_RESET\u0010\u0006\u0012\u000e\n\nSESS_CLOSE\u0010\u0007\u0012\u0014\n\u0010SQL_STMT_EXECUTE\u0010\f\u0012\r\n\tCRUD_FIND\u0010\u0011\u0012\u000f\n\u000bCRUD_INSERT\u0010\u0012\u0012\u000f\n\u000bCRUD_UPDATE\u0010\u0013\u0012\u000f\n\u000bCRUD_DELETE\u0010\u0014\u0012\u000f\n\u000bEXPECT_OPEN\u0010\u0018\u0012\u0010\n\fEXPECT_CLOSE\u0010\u0019\u0012\u0014\n\u0010CRUD_CREATE_VIEW\u0010\u001e\u0012\u0014\n\u0010CRUD_MODIFY_VIEW\u0010\u001f\u0012\u0012\n\u000eCRUD_DROP_VIEW\u0010 \u0012\u0013\n\u000fPREPARE_PREPARE\u0010(\u0012\u0013\n\u000fPREPARE_EXECUTE\u0010)\u0012\u0016\n\u0012PREPARE_DEALLOCATE\u0010*\u0012\u000f\n\u000bCURSOR_OPEN\u0010+\u0012\u0010\n\fCURSOR_CLOSE\u0010,\u0012\u0010\n\fCURSOR_FETCH\u0010-\u0012\u000f\n\u000bCOMPRESSION\u0010.\"ó\u0002\n\u000eServerMessages\"à\u0002\n\u0004Type\u0012\u0006\n\u0002OK\u0010\u0000\u0012\t\n\u0005ERROR\u0010\u0001\u0012\u0015\n\u0011CONN_CAPABILITIES\u0010\u0002\u0012\u001e\n\u001aSESS_AUTHENTICATE_CONTINUE\u0010\u0003\u0012\u0018\n\u0014SESS_AUTHENTICATE_OK\u0010\u0004\u0012\n\n\u0006NOTICE\u0010\u000b\u0012\u001e\n\u001aRESULTSET_COLUMN_META_DATA\u0010\f\u0012\u0011\n\rRESULTSET_ROW\u0010\r\u0012\u0018\n\u0014RESULTSET_FETCH_DONE\u0010\u000e\u0012\u001d\n\u0019RESULTSET_FETCH_SUSPENDED\u0010\u000f\u0012(\n$RESULTSET_FETCH_DONE_MORE_RESULTSETS\u0010\u0010\u0012\u0017\n\u0013SQL_STMT_EXECUTE_OK\u0010\u0011\u0012(\n$RESULTSET_FETCH_DONE_MORE_OUT_PARAMS\u0010\u0012\u0012\u000f\n\u000bCOMPRESSION\u0010\u0013\"\u0017\n\u0002Ok\u0012\u000b\n\u0003msg\u0018\u0001 \u0001(\t:\u0004\u0090ê0\u0000\"\u008e\u0001\n\u0005Error\u0012/\n\bseverity\u0018\u0001 \u0001(\u000e2\u0016.Mysqlx.Error.Severity:\u0005ERROR\u0012\f\n\u0004code\u0018\u0002 \u0002(\r\u0012\u0011\n\tsql_state\u0018\u0004 \u0002(\t\u0012\u000b\n\u0003msg\u0018\u0003 \u0002(\t\" \n\bSeverity\u0012\t\n\u0005ERROR\u0010\u0000\u0012\t\n\u0005FATAL\u0010\u0001:\u0004\u0090ê0\u0001:Y\n\u0011client_message_id\u0012\u001f.google.protobuf.MessageOptions\u0018¡\u008d\u0006 \u0001(\u000e2\u001b.Mysqlx.ClientMessages.Type:Y\n\u0011server_message_id\u0012\u001f.google.protobuf.MessageOptions\u0018¢\u008d\u0006 \u0001(\u000e2\u001b.Mysqlx.ServerMessages.TypeB\u0019\n\u0017com.mysql.cj.x.protobuf"};
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{DescriptorProtos.getDescriptor()});
      internal_static_Mysqlx_ClientMessages_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(0);
      internal_static_Mysqlx_ClientMessages_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_ClientMessages_descriptor, new String[0]);
      internal_static_Mysqlx_ServerMessages_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(1);
      internal_static_Mysqlx_ServerMessages_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_ServerMessages_descriptor, new String[0]);
      internal_static_Mysqlx_Ok_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(2);
      internal_static_Mysqlx_Ok_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Ok_descriptor, new String[]{"Msg"});
      internal_static_Mysqlx_Error_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(3);
      internal_static_Mysqlx_Error_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Error_descriptor, new String[]{"Severity", "Code", "SqlState", "Msg"});
      clientMessageId.internalInit((Descriptors.FieldDescriptor)descriptor.getExtensions().get(0));
      serverMessageId.internalInit((Descriptors.FieldDescriptor)descriptor.getExtensions().get(1));
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(serverMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      DescriptorProtos.getDescriptor();
   }

   public static final class Error extends GeneratedMessageV3 implements ErrorOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int SEVERITY_FIELD_NUMBER = 1;
      private int severity_;
      public static final int CODE_FIELD_NUMBER = 2;
      private int code_;
      public static final int SQL_STATE_FIELD_NUMBER = 4;
      private volatile Object sqlState_;
      public static final int MSG_FIELD_NUMBER = 3;
      private volatile Object msg_;
      private byte memoizedIsInitialized;
      private static final Error DEFAULT_INSTANCE = new Error();
      /** @deprecated */
      @Deprecated
      public static final Parser<Error> PARSER = new AbstractParser<Error>() {
         public Error parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Error(input, extensionRegistry);
         }
      };

      private Error(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Error() {
         this.memoizedIsInitialized = -1;
         this.severity_ = 0;
         this.sqlState_ = "";
         this.msg_ = "";
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Error();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Error(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                  ByteString bs;
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 8:
                        int rawValue = input.readEnum();
                        Severity value = Mysqlx.Error.Severity.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(1, rawValue);
                        } else {
                           this.bitField0_ |= 1;
                           this.severity_ = rawValue;
                        }
                        break;
                     case 16:
                        this.bitField0_ |= 2;
                        this.code_ = input.readUInt32();
                        break;
                     case 26:
                        bs = input.readBytes();
                        this.bitField0_ |= 8;
                        this.msg_ = bs;
                        break;
                     case 34:
                        bs = input.readBytes();
                        this.bitField0_ |= 4;
                        this.sqlState_ = bs;
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
         return Mysqlx.internal_static_Mysqlx_Error_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return Mysqlx.internal_static_Mysqlx_Error_fieldAccessorTable.ensureFieldAccessorsInitialized(Error.class, Builder.class);
      }

      public boolean hasSeverity() {
         return (this.bitField0_ & 1) != 0;
      }

      public Severity getSeverity() {
         Severity result = Mysqlx.Error.Severity.valueOf(this.severity_);
         return result == null ? Mysqlx.Error.Severity.ERROR : result;
      }

      public boolean hasCode() {
         return (this.bitField0_ & 2) != 0;
      }

      public int getCode() {
         return this.code_;
      }

      public boolean hasSqlState() {
         return (this.bitField0_ & 4) != 0;
      }

      public String getSqlState() {
         Object ref = this.sqlState_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.sqlState_ = s;
            }

            return s;
         }
      }

      public ByteString getSqlStateBytes() {
         Object ref = this.sqlState_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.sqlState_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public boolean hasMsg() {
         return (this.bitField0_ & 8) != 0;
      }

      public String getMsg() {
         Object ref = this.msg_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.msg_ = s;
            }

            return s;
         }
      }

      public ByteString getMsgBytes() {
         Object ref = this.msg_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.msg_ = b;
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
         } else if (!this.hasCode()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.hasSqlState()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.hasMsg()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeEnum(1, this.severity_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeUInt32(2, this.code_);
         }

         if ((this.bitField0_ & 8) != 0) {
            GeneratedMessageV3.writeString(output, 3, this.msg_);
         }

         if ((this.bitField0_ & 4) != 0) {
            GeneratedMessageV3.writeString(output, 4, this.sqlState_);
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
               size += CodedOutputStream.computeEnumSize(1, this.severity_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeUInt32Size(2, this.code_);
            }

            if ((this.bitField0_ & 8) != 0) {
               size += GeneratedMessageV3.computeStringSize(3, this.msg_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += GeneratedMessageV3.computeStringSize(4, this.sqlState_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Error)) {
            return super.equals(obj);
         } else {
            Error other = (Error)obj;
            if (this.hasSeverity() != other.hasSeverity()) {
               return false;
            } else if (this.hasSeverity() && this.severity_ != other.severity_) {
               return false;
            } else if (this.hasCode() != other.hasCode()) {
               return false;
            } else if (this.hasCode() && this.getCode() != other.getCode()) {
               return false;
            } else if (this.hasSqlState() != other.hasSqlState()) {
               return false;
            } else if (this.hasSqlState() && !this.getSqlState().equals(other.getSqlState())) {
               return false;
            } else if (this.hasMsg() != other.hasMsg()) {
               return false;
            } else if (this.hasMsg() && !this.getMsg().equals(other.getMsg())) {
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
            if (this.hasSeverity()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.severity_;
            }

            if (this.hasCode()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getCode();
            }

            if (this.hasSqlState()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getSqlState().hashCode();
            }

            if (this.hasMsg()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getMsg().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Error parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Error)PARSER.parseFrom(data);
      }

      public static Error parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Error)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Error parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Error)PARSER.parseFrom(data);
      }

      public static Error parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Error)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Error parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Error)PARSER.parseFrom(data);
      }

      public static Error parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Error)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Error parseFrom(InputStream input) throws IOException {
         return (Error)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Error parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Error)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Error parseDelimitedFrom(InputStream input) throws IOException {
         return (Error)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Error parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Error)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Error parseFrom(CodedInputStream input) throws IOException {
         return (Error)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Error parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Error)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Error prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Error getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Error> parser() {
         return PARSER;
      }

      public Parser<Error> getParserForType() {
         return PARSER;
      }

      public Error getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Error(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Error(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ErrorOrBuilder {
         private int bitField0_;
         private int severity_;
         private int code_;
         private Object sqlState_;
         private Object msg_;

         public static final Descriptors.Descriptor getDescriptor() {
            return Mysqlx.internal_static_Mysqlx_Error_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Mysqlx.internal_static_Mysqlx_Error_fieldAccessorTable.ensureFieldAccessorsInitialized(Error.class, Builder.class);
         }

         private Builder() {
            this.severity_ = 0;
            this.sqlState_ = "";
            this.msg_ = "";
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.severity_ = 0;
            this.sqlState_ = "";
            this.msg_ = "";
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (Mysqlx.Error.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            this.severity_ = 0;
            this.bitField0_ &= -2;
            this.code_ = 0;
            this.bitField0_ &= -3;
            this.sqlState_ = "";
            this.bitField0_ &= -5;
            this.msg_ = "";
            this.bitField0_ &= -9;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return Mysqlx.internal_static_Mysqlx_Error_descriptor;
         }

         public Error getDefaultInstanceForType() {
            return Mysqlx.Error.getDefaultInstance();
         }

         public Error build() {
            Error result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Error buildPartial() {
            Error result = new Error(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.severity_ = this.severity_;
            if ((from_bitField0_ & 2) != 0) {
               result.code_ = this.code_;
               to_bitField0_ |= 2;
            }

            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 4;
            }

            result.sqlState_ = this.sqlState_;
            if ((from_bitField0_ & 8) != 0) {
               to_bitField0_ |= 8;
            }

            result.msg_ = this.msg_;
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
            if (other instanceof Error) {
               return this.mergeFrom((Error)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Error other) {
            if (other == Mysqlx.Error.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasSeverity()) {
                  this.setSeverity(other.getSeverity());
               }

               if (other.hasCode()) {
                  this.setCode(other.getCode());
               }

               if (other.hasSqlState()) {
                  this.bitField0_ |= 4;
                  this.sqlState_ = other.sqlState_;
                  this.onChanged();
               }

               if (other.hasMsg()) {
                  this.bitField0_ |= 8;
                  this.msg_ = other.msg_;
                  this.onChanged();
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasCode()) {
               return false;
            } else if (!this.hasSqlState()) {
               return false;
            } else {
               return this.hasMsg();
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Error parsedMessage = null;

            try {
               parsedMessage = (Error)Mysqlx.Error.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Error)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasSeverity() {
            return (this.bitField0_ & 1) != 0;
         }

         public Severity getSeverity() {
            Severity result = Mysqlx.Error.Severity.valueOf(this.severity_);
            return result == null ? Mysqlx.Error.Severity.ERROR : result;
         }

         public Builder setSeverity(Severity value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.severity_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearSeverity() {
            this.bitField0_ &= -2;
            this.severity_ = 0;
            this.onChanged();
            return this;
         }

         public boolean hasCode() {
            return (this.bitField0_ & 2) != 0;
         }

         public int getCode() {
            return this.code_;
         }

         public Builder setCode(int value) {
            this.bitField0_ |= 2;
            this.code_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearCode() {
            this.bitField0_ &= -3;
            this.code_ = 0;
            this.onChanged();
            return this;
         }

         public boolean hasSqlState() {
            return (this.bitField0_ & 4) != 0;
         }

         public String getSqlState() {
            Object ref = this.sqlState_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.sqlState_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getSqlStateBytes() {
            Object ref = this.sqlState_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.sqlState_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setSqlState(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.sqlState_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearSqlState() {
            this.bitField0_ &= -5;
            this.sqlState_ = Mysqlx.Error.getDefaultInstance().getSqlState();
            this.onChanged();
            return this;
         }

         public Builder setSqlStateBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.sqlState_ = value;
               this.onChanged();
               return this;
            }
         }

         public boolean hasMsg() {
            return (this.bitField0_ & 8) != 0;
         }

         public String getMsg() {
            Object ref = this.msg_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.msg_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getMsgBytes() {
            Object ref = this.msg_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.msg_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setMsg(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.msg_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearMsg() {
            this.bitField0_ &= -9;
            this.msg_ = Mysqlx.Error.getDefaultInstance().getMsg();
            this.onChanged();
            return this;
         }

         public Builder setMsgBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.msg_ = value;
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

      public static enum Severity implements ProtocolMessageEnum {
         ERROR(0),
         FATAL(1);

         public static final int ERROR_VALUE = 0;
         public static final int FATAL_VALUE = 1;
         private static final Internal.EnumLiteMap<Severity> internalValueMap = new Internal.EnumLiteMap<Severity>() {
            public Severity findValueByNumber(int number) {
               return Mysqlx.Error.Severity.forNumber(number);
            }
         };
         private static final Severity[] VALUES = values();
         private final int value;

         public final int getNumber() {
            return this.value;
         }

         /** @deprecated */
         @Deprecated
         public static Severity valueOf(int value) {
            return forNumber(value);
         }

         public static Severity forNumber(int value) {
            switch (value) {
               case 0:
                  return ERROR;
               case 1:
                  return FATAL;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<Severity> internalGetValueMap() {
            return internalValueMap;
         }

         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
         }

         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static final Descriptors.EnumDescriptor getDescriptor() {
            return (Descriptors.EnumDescriptor)Mysqlx.Error.getDescriptor().getEnumTypes().get(0);
         }

         public static Severity valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private Severity(int value) {
            this.value = value;
         }
      }
   }

   public interface ErrorOrBuilder extends MessageOrBuilder {
      boolean hasSeverity();

      Error.Severity getSeverity();

      boolean hasCode();

      int getCode();

      boolean hasSqlState();

      String getSqlState();

      ByteString getSqlStateBytes();

      boolean hasMsg();

      String getMsg();

      ByteString getMsgBytes();
   }

   public static final class Ok extends GeneratedMessageV3 implements OkOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int MSG_FIELD_NUMBER = 1;
      private volatile Object msg_;
      private byte memoizedIsInitialized;
      private static final Ok DEFAULT_INSTANCE = new Ok();
      /** @deprecated */
      @Deprecated
      public static final Parser<Ok> PARSER = new AbstractParser<Ok>() {
         public Ok parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Ok(input, extensionRegistry);
         }
      };

      private Ok(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Ok() {
         this.memoizedIsInitialized = -1;
         this.msg_ = "";
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Ok();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Ok(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.msg_ = bs;
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
         return Mysqlx.internal_static_Mysqlx_Ok_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return Mysqlx.internal_static_Mysqlx_Ok_fieldAccessorTable.ensureFieldAccessorsInitialized(Ok.class, Builder.class);
      }

      public boolean hasMsg() {
         return (this.bitField0_ & 1) != 0;
      }

      public String getMsg() {
         Object ref = this.msg_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.msg_ = s;
            }

            return s;
         }
      }

      public ByteString getMsgBytes() {
         Object ref = this.msg_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.msg_ = b;
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
         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(output, 1, this.msg_);
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
               size += GeneratedMessageV3.computeStringSize(1, this.msg_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Ok)) {
            return super.equals(obj);
         } else {
            Ok other = (Ok)obj;
            if (this.hasMsg() != other.hasMsg()) {
               return false;
            } else if (this.hasMsg() && !this.getMsg().equals(other.getMsg())) {
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
            if (this.hasMsg()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getMsg().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Ok parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Ok)PARSER.parseFrom(data);
      }

      public static Ok parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Ok)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Ok parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Ok)PARSER.parseFrom(data);
      }

      public static Ok parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Ok)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Ok parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Ok)PARSER.parseFrom(data);
      }

      public static Ok parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Ok)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Ok parseFrom(InputStream input) throws IOException {
         return (Ok)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Ok parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Ok)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Ok parseDelimitedFrom(InputStream input) throws IOException {
         return (Ok)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Ok parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Ok)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Ok parseFrom(CodedInputStream input) throws IOException {
         return (Ok)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Ok parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Ok)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Ok prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Ok getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Ok> parser() {
         return PARSER;
      }

      public Parser<Ok> getParserForType() {
         return PARSER;
      }

      public Ok getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Ok(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Ok(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements OkOrBuilder {
         private int bitField0_;
         private Object msg_;

         public static final Descriptors.Descriptor getDescriptor() {
            return Mysqlx.internal_static_Mysqlx_Ok_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Mysqlx.internal_static_Mysqlx_Ok_fieldAccessorTable.ensureFieldAccessorsInitialized(Ok.class, Builder.class);
         }

         private Builder() {
            this.msg_ = "";
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.msg_ = "";
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (Mysqlx.Ok.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            this.msg_ = "";
            this.bitField0_ &= -2;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return Mysqlx.internal_static_Mysqlx_Ok_descriptor;
         }

         public Ok getDefaultInstanceForType() {
            return Mysqlx.Ok.getDefaultInstance();
         }

         public Ok build() {
            Ok result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Ok buildPartial() {
            Ok result = new Ok(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.msg_ = this.msg_;
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
            if (other instanceof Ok) {
               return this.mergeFrom((Ok)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Ok other) {
            if (other == Mysqlx.Ok.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasMsg()) {
                  this.bitField0_ |= 1;
                  this.msg_ = other.msg_;
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
            Ok parsedMessage = null;

            try {
               parsedMessage = (Ok)Mysqlx.Ok.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Ok)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasMsg() {
            return (this.bitField0_ & 1) != 0;
         }

         public String getMsg() {
            Object ref = this.msg_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.msg_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getMsgBytes() {
            Object ref = this.msg_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.msg_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setMsg(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.msg_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearMsg() {
            this.bitField0_ &= -2;
            this.msg_ = Mysqlx.Ok.getDefaultInstance().getMsg();
            this.onChanged();
            return this;
         }

         public Builder setMsgBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.msg_ = value;
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

   public interface OkOrBuilder extends MessageOrBuilder {
      boolean hasMsg();

      String getMsg();

      ByteString getMsgBytes();
   }

   public static final class ServerMessages extends GeneratedMessageV3 implements ServerMessagesOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized;
      private static final ServerMessages DEFAULT_INSTANCE = new ServerMessages();
      /** @deprecated */
      @Deprecated
      public static final Parser<ServerMessages> PARSER = new AbstractParser<ServerMessages>() {
         public ServerMessages parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new ServerMessages(input, extensionRegistry);
         }
      };

      private ServerMessages(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private ServerMessages() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new ServerMessages();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private ServerMessages(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return Mysqlx.internal_static_Mysqlx_ServerMessages_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return Mysqlx.internal_static_Mysqlx_ServerMessages_fieldAccessorTable.ensureFieldAccessorsInitialized(ServerMessages.class, Builder.class);
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
         } else if (!(obj instanceof ServerMessages)) {
            return super.equals(obj);
         } else {
            ServerMessages other = (ServerMessages)obj;
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

      public static ServerMessages parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (ServerMessages)PARSER.parseFrom(data);
      }

      public static ServerMessages parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (ServerMessages)PARSER.parseFrom(data, extensionRegistry);
      }

      public static ServerMessages parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (ServerMessages)PARSER.parseFrom(data);
      }

      public static ServerMessages parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (ServerMessages)PARSER.parseFrom(data, extensionRegistry);
      }

      public static ServerMessages parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (ServerMessages)PARSER.parseFrom(data);
      }

      public static ServerMessages parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (ServerMessages)PARSER.parseFrom(data, extensionRegistry);
      }

      public static ServerMessages parseFrom(InputStream input) throws IOException {
         return (ServerMessages)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static ServerMessages parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (ServerMessages)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static ServerMessages parseDelimitedFrom(InputStream input) throws IOException {
         return (ServerMessages)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static ServerMessages parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (ServerMessages)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static ServerMessages parseFrom(CodedInputStream input) throws IOException {
         return (ServerMessages)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static ServerMessages parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (ServerMessages)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(ServerMessages prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static ServerMessages getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<ServerMessages> parser() {
         return PARSER;
      }

      public Parser<ServerMessages> getParserForType() {
         return PARSER;
      }

      public ServerMessages getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      ServerMessages(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      ServerMessages(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ServerMessagesOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return Mysqlx.internal_static_Mysqlx_ServerMessages_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Mysqlx.internal_static_Mysqlx_ServerMessages_fieldAccessorTable.ensureFieldAccessorsInitialized(ServerMessages.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (Mysqlx.ServerMessages.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return Mysqlx.internal_static_Mysqlx_ServerMessages_descriptor;
         }

         public ServerMessages getDefaultInstanceForType() {
            return Mysqlx.ServerMessages.getDefaultInstance();
         }

         public ServerMessages build() {
            ServerMessages result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public ServerMessages buildPartial() {
            ServerMessages result = new ServerMessages(this);
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
            if (other instanceof ServerMessages) {
               return this.mergeFrom((ServerMessages)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(ServerMessages other) {
            if (other == Mysqlx.ServerMessages.getDefaultInstance()) {
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
            ServerMessages parsedMessage = null;

            try {
               parsedMessage = (ServerMessages)Mysqlx.ServerMessages.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (ServerMessages)var8.getUnfinishedMessage();
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

      public static enum Type implements ProtocolMessageEnum {
         OK(0),
         ERROR(1),
         CONN_CAPABILITIES(2),
         SESS_AUTHENTICATE_CONTINUE(3),
         SESS_AUTHENTICATE_OK(4),
         NOTICE(11),
         RESULTSET_COLUMN_META_DATA(12),
         RESULTSET_ROW(13),
         RESULTSET_FETCH_DONE(14),
         RESULTSET_FETCH_SUSPENDED(15),
         RESULTSET_FETCH_DONE_MORE_RESULTSETS(16),
         SQL_STMT_EXECUTE_OK(17),
         RESULTSET_FETCH_DONE_MORE_OUT_PARAMS(18),
         COMPRESSION(19);

         public static final int OK_VALUE = 0;
         public static final int ERROR_VALUE = 1;
         public static final int CONN_CAPABILITIES_VALUE = 2;
         public static final int SESS_AUTHENTICATE_CONTINUE_VALUE = 3;
         public static final int SESS_AUTHENTICATE_OK_VALUE = 4;
         public static final int NOTICE_VALUE = 11;
         public static final int RESULTSET_COLUMN_META_DATA_VALUE = 12;
         public static final int RESULTSET_ROW_VALUE = 13;
         public static final int RESULTSET_FETCH_DONE_VALUE = 14;
         public static final int RESULTSET_FETCH_SUSPENDED_VALUE = 15;
         public static final int RESULTSET_FETCH_DONE_MORE_RESULTSETS_VALUE = 16;
         public static final int SQL_STMT_EXECUTE_OK_VALUE = 17;
         public static final int RESULTSET_FETCH_DONE_MORE_OUT_PARAMS_VALUE = 18;
         public static final int COMPRESSION_VALUE = 19;
         private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() {
            public Type findValueByNumber(int number) {
               return Mysqlx.ServerMessages.Type.forNumber(number);
            }
         };
         private static final Type[] VALUES = values();
         private final int value;

         public final int getNumber() {
            return this.value;
         }

         /** @deprecated */
         @Deprecated
         public static Type valueOf(int value) {
            return forNumber(value);
         }

         public static Type forNumber(int value) {
            switch (value) {
               case 0:
                  return OK;
               case 1:
                  return ERROR;
               case 2:
                  return CONN_CAPABILITIES;
               case 3:
                  return SESS_AUTHENTICATE_CONTINUE;
               case 4:
                  return SESS_AUTHENTICATE_OK;
               case 5:
               case 6:
               case 7:
               case 8:
               case 9:
               case 10:
               default:
                  return null;
               case 11:
                  return NOTICE;
               case 12:
                  return RESULTSET_COLUMN_META_DATA;
               case 13:
                  return RESULTSET_ROW;
               case 14:
                  return RESULTSET_FETCH_DONE;
               case 15:
                  return RESULTSET_FETCH_SUSPENDED;
               case 16:
                  return RESULTSET_FETCH_DONE_MORE_RESULTSETS;
               case 17:
                  return SQL_STMT_EXECUTE_OK;
               case 18:
                  return RESULTSET_FETCH_DONE_MORE_OUT_PARAMS;
               case 19:
                  return COMPRESSION;
            }
         }

         public static Internal.EnumLiteMap<Type> internalGetValueMap() {
            return internalValueMap;
         }

         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
         }

         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static final Descriptors.EnumDescriptor getDescriptor() {
            return (Descriptors.EnumDescriptor)Mysqlx.ServerMessages.getDescriptor().getEnumTypes().get(0);
         }

         public static Type valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private Type(int value) {
            this.value = value;
         }
      }
   }

   public interface ServerMessagesOrBuilder extends MessageOrBuilder {
   }

   public static final class ClientMessages extends GeneratedMessageV3 implements ClientMessagesOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized;
      private static final ClientMessages DEFAULT_INSTANCE = new ClientMessages();
      /** @deprecated */
      @Deprecated
      public static final Parser<ClientMessages> PARSER = new AbstractParser<ClientMessages>() {
         public ClientMessages parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new ClientMessages(input, extensionRegistry);
         }
      };

      private ClientMessages(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private ClientMessages() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new ClientMessages();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private ClientMessages(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return Mysqlx.internal_static_Mysqlx_ClientMessages_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return Mysqlx.internal_static_Mysqlx_ClientMessages_fieldAccessorTable.ensureFieldAccessorsInitialized(ClientMessages.class, Builder.class);
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
         } else if (!(obj instanceof ClientMessages)) {
            return super.equals(obj);
         } else {
            ClientMessages other = (ClientMessages)obj;
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

      public static ClientMessages parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (ClientMessages)PARSER.parseFrom(data);
      }

      public static ClientMessages parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (ClientMessages)PARSER.parseFrom(data, extensionRegistry);
      }

      public static ClientMessages parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (ClientMessages)PARSER.parseFrom(data);
      }

      public static ClientMessages parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (ClientMessages)PARSER.parseFrom(data, extensionRegistry);
      }

      public static ClientMessages parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (ClientMessages)PARSER.parseFrom(data);
      }

      public static ClientMessages parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (ClientMessages)PARSER.parseFrom(data, extensionRegistry);
      }

      public static ClientMessages parseFrom(InputStream input) throws IOException {
         return (ClientMessages)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static ClientMessages parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (ClientMessages)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static ClientMessages parseDelimitedFrom(InputStream input) throws IOException {
         return (ClientMessages)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static ClientMessages parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (ClientMessages)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static ClientMessages parseFrom(CodedInputStream input) throws IOException {
         return (ClientMessages)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static ClientMessages parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (ClientMessages)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(ClientMessages prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static ClientMessages getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<ClientMessages> parser() {
         return PARSER;
      }

      public Parser<ClientMessages> getParserForType() {
         return PARSER;
      }

      public ClientMessages getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      ClientMessages(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      ClientMessages(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ClientMessagesOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return Mysqlx.internal_static_Mysqlx_ClientMessages_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Mysqlx.internal_static_Mysqlx_ClientMessages_fieldAccessorTable.ensureFieldAccessorsInitialized(ClientMessages.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (Mysqlx.ClientMessages.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return Mysqlx.internal_static_Mysqlx_ClientMessages_descriptor;
         }

         public ClientMessages getDefaultInstanceForType() {
            return Mysqlx.ClientMessages.getDefaultInstance();
         }

         public ClientMessages build() {
            ClientMessages result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public ClientMessages buildPartial() {
            ClientMessages result = new ClientMessages(this);
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
            if (other instanceof ClientMessages) {
               return this.mergeFrom((ClientMessages)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(ClientMessages other) {
            if (other == Mysqlx.ClientMessages.getDefaultInstance()) {
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
            ClientMessages parsedMessage = null;

            try {
               parsedMessage = (ClientMessages)Mysqlx.ClientMessages.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (ClientMessages)var8.getUnfinishedMessage();
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

      public static enum Type implements ProtocolMessageEnum {
         CON_CAPABILITIES_GET(1),
         CON_CAPABILITIES_SET(2),
         CON_CLOSE(3),
         SESS_AUTHENTICATE_START(4),
         SESS_AUTHENTICATE_CONTINUE(5),
         SESS_RESET(6),
         SESS_CLOSE(7),
         SQL_STMT_EXECUTE(12),
         CRUD_FIND(17),
         CRUD_INSERT(18),
         CRUD_UPDATE(19),
         CRUD_DELETE(20),
         EXPECT_OPEN(24),
         EXPECT_CLOSE(25),
         CRUD_CREATE_VIEW(30),
         CRUD_MODIFY_VIEW(31),
         CRUD_DROP_VIEW(32),
         PREPARE_PREPARE(40),
         PREPARE_EXECUTE(41),
         PREPARE_DEALLOCATE(42),
         CURSOR_OPEN(43),
         CURSOR_CLOSE(44),
         CURSOR_FETCH(45),
         COMPRESSION(46);

         public static final int CON_CAPABILITIES_GET_VALUE = 1;
         public static final int CON_CAPABILITIES_SET_VALUE = 2;
         public static final int CON_CLOSE_VALUE = 3;
         public static final int SESS_AUTHENTICATE_START_VALUE = 4;
         public static final int SESS_AUTHENTICATE_CONTINUE_VALUE = 5;
         public static final int SESS_RESET_VALUE = 6;
         public static final int SESS_CLOSE_VALUE = 7;
         public static final int SQL_STMT_EXECUTE_VALUE = 12;
         public static final int CRUD_FIND_VALUE = 17;
         public static final int CRUD_INSERT_VALUE = 18;
         public static final int CRUD_UPDATE_VALUE = 19;
         public static final int CRUD_DELETE_VALUE = 20;
         public static final int EXPECT_OPEN_VALUE = 24;
         public static final int EXPECT_CLOSE_VALUE = 25;
         public static final int CRUD_CREATE_VIEW_VALUE = 30;
         public static final int CRUD_MODIFY_VIEW_VALUE = 31;
         public static final int CRUD_DROP_VIEW_VALUE = 32;
         public static final int PREPARE_PREPARE_VALUE = 40;
         public static final int PREPARE_EXECUTE_VALUE = 41;
         public static final int PREPARE_DEALLOCATE_VALUE = 42;
         public static final int CURSOR_OPEN_VALUE = 43;
         public static final int CURSOR_CLOSE_VALUE = 44;
         public static final int CURSOR_FETCH_VALUE = 45;
         public static final int COMPRESSION_VALUE = 46;
         private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() {
            public Type findValueByNumber(int number) {
               return Mysqlx.ClientMessages.Type.forNumber(number);
            }
         };
         private static final Type[] VALUES = values();
         private final int value;

         public final int getNumber() {
            return this.value;
         }

         /** @deprecated */
         @Deprecated
         public static Type valueOf(int value) {
            return forNumber(value);
         }

         public static Type forNumber(int value) {
            switch (value) {
               case 1:
                  return CON_CAPABILITIES_GET;
               case 2:
                  return CON_CAPABILITIES_SET;
               case 3:
                  return CON_CLOSE;
               case 4:
                  return SESS_AUTHENTICATE_START;
               case 5:
                  return SESS_AUTHENTICATE_CONTINUE;
               case 6:
                  return SESS_RESET;
               case 7:
                  return SESS_CLOSE;
               case 8:
               case 9:
               case 10:
               case 11:
               case 13:
               case 14:
               case 15:
               case 16:
               case 21:
               case 22:
               case 23:
               case 26:
               case 27:
               case 28:
               case 29:
               case 33:
               case 34:
               case 35:
               case 36:
               case 37:
               case 38:
               case 39:
               default:
                  return null;
               case 12:
                  return SQL_STMT_EXECUTE;
               case 17:
                  return CRUD_FIND;
               case 18:
                  return CRUD_INSERT;
               case 19:
                  return CRUD_UPDATE;
               case 20:
                  return CRUD_DELETE;
               case 24:
                  return EXPECT_OPEN;
               case 25:
                  return EXPECT_CLOSE;
               case 30:
                  return CRUD_CREATE_VIEW;
               case 31:
                  return CRUD_MODIFY_VIEW;
               case 32:
                  return CRUD_DROP_VIEW;
               case 40:
                  return PREPARE_PREPARE;
               case 41:
                  return PREPARE_EXECUTE;
               case 42:
                  return PREPARE_DEALLOCATE;
               case 43:
                  return CURSOR_OPEN;
               case 44:
                  return CURSOR_CLOSE;
               case 45:
                  return CURSOR_FETCH;
               case 46:
                  return COMPRESSION;
            }
         }

         public static Internal.EnumLiteMap<Type> internalGetValueMap() {
            return internalValueMap;
         }

         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
         }

         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static final Descriptors.EnumDescriptor getDescriptor() {
            return (Descriptors.EnumDescriptor)Mysqlx.ClientMessages.getDescriptor().getEnumTypes().get(0);
         }

         public static Type valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private Type(int value) {
            this.value = value;
         }
      }
   }

   public interface ClientMessagesOrBuilder extends MessageOrBuilder {
   }
}
