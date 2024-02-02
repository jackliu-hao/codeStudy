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
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.SingleFieldBuilderV3;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class MysqlxCursor {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Cursor_Open_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Cursor_Open_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Cursor_Open_OneOfMessage_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Cursor_Fetch_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Cursor_Fetch_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Cursor_Close_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Cursor_Close_fieldAccessorTable;
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxCursor() {
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
      String[] descriptorData = new String[]{"\n\u0013mysqlx_cursor.proto\u0012\rMysqlx.Cursor\u001a\fmysqlx.proto\u001a\u0014mysqlx_prepare.proto\"ø\u0001\n\u0004Open\u0012\u0011\n\tcursor_id\u0018\u0001 \u0002(\r\u0012.\n\u0004stmt\u0018\u0004 \u0002(\u000b2 .Mysqlx.Cursor.Open.OneOfMessage\u0012\u0012\n\nfetch_rows\u0018\u0005 \u0001(\u0004\u001a\u0092\u0001\n\fOneOfMessage\u00123\n\u0004type\u0018\u0001 \u0002(\u000e2%.Mysqlx.Cursor.Open.OneOfMessage.Type\u00120\n\u000fprepare_execute\u0018\u0002 \u0001(\u000b2\u0017.Mysqlx.Prepare.Execute\"\u001b\n\u0004Type\u0012\u0013\n\u000fPREPARE_EXECUTE\u0010\u0000:\u0004\u0088ê0+\"4\n\u0005Fetch\u0012\u0011\n\tcursor_id\u0018\u0001 \u0002(\r\u0012\u0012\n\nfetch_rows\u0018\u0005 \u0001(\u0004:\u0004\u0088ê0-\" \n\u0005Close\u0012\u0011\n\tcursor_id\u0018\u0001 \u0002(\r:\u0004\u0088ê0,B\u0019\n\u0017com.mysql.cj.x.protobuf"};
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Mysqlx.getDescriptor(), MysqlxPrepare.getDescriptor()});
      internal_static_Mysqlx_Cursor_Open_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(0);
      internal_static_Mysqlx_Cursor_Open_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Cursor_Open_descriptor, new String[]{"CursorId", "Stmt", "FetchRows"});
      internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor = (Descriptors.Descriptor)internal_static_Mysqlx_Cursor_Open_descriptor.getNestedTypes().get(0);
      internal_static_Mysqlx_Cursor_Open_OneOfMessage_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor, new String[]{"Type", "PrepareExecute"});
      internal_static_Mysqlx_Cursor_Fetch_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(1);
      internal_static_Mysqlx_Cursor_Fetch_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Cursor_Fetch_descriptor, new String[]{"CursorId", "FetchRows"});
      internal_static_Mysqlx_Cursor_Close_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(2);
      internal_static_Mysqlx_Cursor_Close_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Cursor_Close_descriptor, new String[]{"CursorId"});
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.clientMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      Mysqlx.getDescriptor();
      MysqlxPrepare.getDescriptor();
   }

   public static final class Close extends GeneratedMessageV3 implements CloseOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int CURSOR_ID_FIELD_NUMBER = 1;
      private int cursorId_;
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
                        this.cursorId_ = input.readUInt32();
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
         return MysqlxCursor.internal_static_Mysqlx_Cursor_Close_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCursor.internal_static_Mysqlx_Cursor_Close_fieldAccessorTable.ensureFieldAccessorsInitialized(Close.class, Builder.class);
      }

      public boolean hasCursorId() {
         return (this.bitField0_ & 1) != 0;
      }

      public int getCursorId() {
         return this.cursorId_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCursorId()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeUInt32(1, this.cursorId_);
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
               size += CodedOutputStream.computeUInt32Size(1, this.cursorId_);
            }

            size += this.unknownFields.getSerializedSize();
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
            if (this.hasCursorId() != other.hasCursorId()) {
               return false;
            } else if (this.hasCursorId() && this.getCursorId() != other.getCursorId()) {
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
            if (this.hasCursorId()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCursorId();
            }

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
         private int bitField0_;
         private int cursorId_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Close_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Close_fieldAccessorTable.ensureFieldAccessorsInitialized(Close.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCursor.Close.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            this.cursorId_ = 0;
            this.bitField0_ &= -2;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Close_descriptor;
         }

         public Close getDefaultInstanceForType() {
            return MysqlxCursor.Close.getDefaultInstance();
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
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.cursorId_ = this.cursorId_;
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
            if (other instanceof Close) {
               return this.mergeFrom((Close)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Close other) {
            if (other == MysqlxCursor.Close.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCursorId()) {
                  this.setCursorId(other.getCursorId());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            return this.hasCursorId();
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Close parsedMessage = null;

            try {
               parsedMessage = (Close)MysqlxCursor.Close.PARSER.parsePartialFrom(input, extensionRegistry);
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

         public boolean hasCursorId() {
            return (this.bitField0_ & 1) != 0;
         }

         public int getCursorId() {
            return this.cursorId_;
         }

         public Builder setCursorId(int value) {
            this.bitField0_ |= 1;
            this.cursorId_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearCursorId() {
            this.bitField0_ &= -2;
            this.cursorId_ = 0;
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

   public interface CloseOrBuilder extends MessageOrBuilder {
      boolean hasCursorId();

      int getCursorId();
   }

   public static final class Fetch extends GeneratedMessageV3 implements FetchOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int CURSOR_ID_FIELD_NUMBER = 1;
      private int cursorId_;
      public static final int FETCH_ROWS_FIELD_NUMBER = 5;
      private long fetchRows_;
      private byte memoizedIsInitialized;
      private static final Fetch DEFAULT_INSTANCE = new Fetch();
      /** @deprecated */
      @Deprecated
      public static final Parser<Fetch> PARSER = new AbstractParser<Fetch>() {
         public Fetch parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Fetch(input, extensionRegistry);
         }
      };

      private Fetch(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Fetch() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Fetch();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Fetch(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.cursorId_ = input.readUInt32();
                        break;
                     case 40:
                        this.bitField0_ |= 2;
                        this.fetchRows_ = input.readUInt64();
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
         return MysqlxCursor.internal_static_Mysqlx_Cursor_Fetch_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCursor.internal_static_Mysqlx_Cursor_Fetch_fieldAccessorTable.ensureFieldAccessorsInitialized(Fetch.class, Builder.class);
      }

      public boolean hasCursorId() {
         return (this.bitField0_ & 1) != 0;
      }

      public int getCursorId() {
         return this.cursorId_;
      }

      public boolean hasFetchRows() {
         return (this.bitField0_ & 2) != 0;
      }

      public long getFetchRows() {
         return this.fetchRows_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCursorId()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeUInt32(1, this.cursorId_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeUInt64(5, this.fetchRows_);
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
               size += CodedOutputStream.computeUInt32Size(1, this.cursorId_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeUInt64Size(5, this.fetchRows_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Fetch)) {
            return super.equals(obj);
         } else {
            Fetch other = (Fetch)obj;
            if (this.hasCursorId() != other.hasCursorId()) {
               return false;
            } else if (this.hasCursorId() && this.getCursorId() != other.getCursorId()) {
               return false;
            } else if (this.hasFetchRows() != other.hasFetchRows()) {
               return false;
            } else if (this.hasFetchRows() && this.getFetchRows() != other.getFetchRows()) {
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
            if (this.hasCursorId()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCursorId();
            }

            if (this.hasFetchRows()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + Internal.hashLong(this.getFetchRows());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Fetch parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Fetch)PARSER.parseFrom(data);
      }

      public static Fetch parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Fetch)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Fetch parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Fetch)PARSER.parseFrom(data);
      }

      public static Fetch parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Fetch)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Fetch parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Fetch)PARSER.parseFrom(data);
      }

      public static Fetch parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Fetch)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Fetch parseFrom(InputStream input) throws IOException {
         return (Fetch)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Fetch parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Fetch)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Fetch parseDelimitedFrom(InputStream input) throws IOException {
         return (Fetch)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Fetch parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Fetch)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Fetch parseFrom(CodedInputStream input) throws IOException {
         return (Fetch)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Fetch parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Fetch)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Fetch prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Fetch getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Fetch> parser() {
         return PARSER;
      }

      public Parser<Fetch> getParserForType() {
         return PARSER;
      }

      public Fetch getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Fetch(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Fetch(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements FetchOrBuilder {
         private int bitField0_;
         private int cursorId_;
         private long fetchRows_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Fetch_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Fetch_fieldAccessorTable.ensureFieldAccessorsInitialized(Fetch.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCursor.Fetch.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            this.cursorId_ = 0;
            this.bitField0_ &= -2;
            this.fetchRows_ = 0L;
            this.bitField0_ &= -3;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Fetch_descriptor;
         }

         public Fetch getDefaultInstanceForType() {
            return MysqlxCursor.Fetch.getDefaultInstance();
         }

         public Fetch build() {
            Fetch result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Fetch buildPartial() {
            Fetch result = new Fetch(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.cursorId_ = this.cursorId_;
               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               result.fetchRows_ = this.fetchRows_;
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
            if (other instanceof Fetch) {
               return this.mergeFrom((Fetch)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Fetch other) {
            if (other == MysqlxCursor.Fetch.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCursorId()) {
                  this.setCursorId(other.getCursorId());
               }

               if (other.hasFetchRows()) {
                  this.setFetchRows(other.getFetchRows());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            return this.hasCursorId();
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Fetch parsedMessage = null;

            try {
               parsedMessage = (Fetch)MysqlxCursor.Fetch.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Fetch)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasCursorId() {
            return (this.bitField0_ & 1) != 0;
         }

         public int getCursorId() {
            return this.cursorId_;
         }

         public Builder setCursorId(int value) {
            this.bitField0_ |= 1;
            this.cursorId_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearCursorId() {
            this.bitField0_ &= -2;
            this.cursorId_ = 0;
            this.onChanged();
            return this;
         }

         public boolean hasFetchRows() {
            return (this.bitField0_ & 2) != 0;
         }

         public long getFetchRows() {
            return this.fetchRows_;
         }

         public Builder setFetchRows(long value) {
            this.bitField0_ |= 2;
            this.fetchRows_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearFetchRows() {
            this.bitField0_ &= -3;
            this.fetchRows_ = 0L;
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

   public interface FetchOrBuilder extends MessageOrBuilder {
      boolean hasCursorId();

      int getCursorId();

      boolean hasFetchRows();

      long getFetchRows();
   }

   public static final class Open extends GeneratedMessageV3 implements OpenOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int CURSOR_ID_FIELD_NUMBER = 1;
      private int cursorId_;
      public static final int STMT_FIELD_NUMBER = 4;
      private OneOfMessage stmt_;
      public static final int FETCH_ROWS_FIELD_NUMBER = 5;
      private long fetchRows_;
      private byte memoizedIsInitialized;
      private static final Open DEFAULT_INSTANCE = new Open();
      /** @deprecated */
      @Deprecated
      public static final Parser<Open> PARSER = new AbstractParser<Open>() {
         public Open parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Open(input, extensionRegistry);
         }
      };

      private Open(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Open() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Open();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Open(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.cursorId_ = input.readUInt32();
                        break;
                     case 34:
                        OneOfMessage.Builder subBuilder = null;
                        if ((this.bitField0_ & 2) != 0) {
                           subBuilder = this.stmt_.toBuilder();
                        }

                        this.stmt_ = (OneOfMessage)input.readMessage(MysqlxCursor.Open.OneOfMessage.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.stmt_);
                           this.stmt_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 2;
                        break;
                     case 40:
                        this.bitField0_ |= 4;
                        this.fetchRows_ = input.readUInt64();
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
         return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_fieldAccessorTable.ensureFieldAccessorsInitialized(Open.class, Builder.class);
      }

      public boolean hasCursorId() {
         return (this.bitField0_ & 1) != 0;
      }

      public int getCursorId() {
         return this.cursorId_;
      }

      public boolean hasStmt() {
         return (this.bitField0_ & 2) != 0;
      }

      public OneOfMessage getStmt() {
         return this.stmt_ == null ? MysqlxCursor.Open.OneOfMessage.getDefaultInstance() : this.stmt_;
      }

      public OneOfMessageOrBuilder getStmtOrBuilder() {
         return this.stmt_ == null ? MysqlxCursor.Open.OneOfMessage.getDefaultInstance() : this.stmt_;
      }

      public boolean hasFetchRows() {
         return (this.bitField0_ & 4) != 0;
      }

      public long getFetchRows() {
         return this.fetchRows_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCursorId()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.hasStmt()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getStmt().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeUInt32(1, this.cursorId_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeMessage(4, this.getStmt());
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeUInt64(5, this.fetchRows_);
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
               size += CodedOutputStream.computeUInt32Size(1, this.cursorId_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeMessageSize(4, this.getStmt());
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeUInt64Size(5, this.fetchRows_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Open)) {
            return super.equals(obj);
         } else {
            Open other = (Open)obj;
            if (this.hasCursorId() != other.hasCursorId()) {
               return false;
            } else if (this.hasCursorId() && this.getCursorId() != other.getCursorId()) {
               return false;
            } else if (this.hasStmt() != other.hasStmt()) {
               return false;
            } else if (this.hasStmt() && !this.getStmt().equals(other.getStmt())) {
               return false;
            } else if (this.hasFetchRows() != other.hasFetchRows()) {
               return false;
            } else if (this.hasFetchRows() && this.getFetchRows() != other.getFetchRows()) {
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
            if (this.hasCursorId()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCursorId();
            }

            if (this.hasStmt()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getStmt().hashCode();
            }

            if (this.hasFetchRows()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + Internal.hashLong(this.getFetchRows());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Open parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Open)PARSER.parseFrom(data);
      }

      public static Open parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Open)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Open parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Open)PARSER.parseFrom(data);
      }

      public static Open parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Open)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Open parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Open)PARSER.parseFrom(data);
      }

      public static Open parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Open)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Open parseFrom(InputStream input) throws IOException {
         return (Open)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Open parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Open)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Open parseDelimitedFrom(InputStream input) throws IOException {
         return (Open)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Open parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Open)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Open parseFrom(CodedInputStream input) throws IOException {
         return (Open)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Open parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Open)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Open prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Open getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Open> parser() {
         return PARSER;
      }

      public Parser<Open> getParserForType() {
         return PARSER;
      }

      public Open getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Open(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Open(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements OpenOrBuilder {
         private int bitField0_;
         private int cursorId_;
         private OneOfMessage stmt_;
         private SingleFieldBuilderV3<OneOfMessage, OneOfMessage.Builder, OneOfMessageOrBuilder> stmtBuilder_;
         private long fetchRows_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_fieldAccessorTable.ensureFieldAccessorsInitialized(Open.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCursor.Open.alwaysUseFieldBuilders) {
               this.getStmtFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            this.cursorId_ = 0;
            this.bitField0_ &= -2;
            if (this.stmtBuilder_ == null) {
               this.stmt_ = null;
            } else {
               this.stmtBuilder_.clear();
            }

            this.bitField0_ &= -3;
            this.fetchRows_ = 0L;
            this.bitField0_ &= -5;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_descriptor;
         }

         public Open getDefaultInstanceForType() {
            return MysqlxCursor.Open.getDefaultInstance();
         }

         public Open build() {
            Open result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Open buildPartial() {
            Open result = new Open(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.cursorId_ = this.cursorId_;
               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               if (this.stmtBuilder_ == null) {
                  result.stmt_ = this.stmt_;
               } else {
                  result.stmt_ = (OneOfMessage)this.stmtBuilder_.build();
               }

               to_bitField0_ |= 2;
            }

            if ((from_bitField0_ & 4) != 0) {
               result.fetchRows_ = this.fetchRows_;
               to_bitField0_ |= 4;
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
            if (other instanceof Open) {
               return this.mergeFrom((Open)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Open other) {
            if (other == MysqlxCursor.Open.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCursorId()) {
                  this.setCursorId(other.getCursorId());
               }

               if (other.hasStmt()) {
                  this.mergeStmt(other.getStmt());
               }

               if (other.hasFetchRows()) {
                  this.setFetchRows(other.getFetchRows());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasCursorId()) {
               return false;
            } else if (!this.hasStmt()) {
               return false;
            } else {
               return this.getStmt().isInitialized();
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Open parsedMessage = null;

            try {
               parsedMessage = (Open)MysqlxCursor.Open.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Open)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasCursorId() {
            return (this.bitField0_ & 1) != 0;
         }

         public int getCursorId() {
            return this.cursorId_;
         }

         public Builder setCursorId(int value) {
            this.bitField0_ |= 1;
            this.cursorId_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearCursorId() {
            this.bitField0_ &= -2;
            this.cursorId_ = 0;
            this.onChanged();
            return this;
         }

         public boolean hasStmt() {
            return (this.bitField0_ & 2) != 0;
         }

         public OneOfMessage getStmt() {
            if (this.stmtBuilder_ == null) {
               return this.stmt_ == null ? MysqlxCursor.Open.OneOfMessage.getDefaultInstance() : this.stmt_;
            } else {
               return (OneOfMessage)this.stmtBuilder_.getMessage();
            }
         }

         public Builder setStmt(OneOfMessage value) {
            if (this.stmtBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.stmt_ = value;
               this.onChanged();
            } else {
               this.stmtBuilder_.setMessage(value);
            }

            this.bitField0_ |= 2;
            return this;
         }

         public Builder setStmt(OneOfMessage.Builder builderForValue) {
            if (this.stmtBuilder_ == null) {
               this.stmt_ = builderForValue.build();
               this.onChanged();
            } else {
               this.stmtBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 2;
            return this;
         }

         public Builder mergeStmt(OneOfMessage value) {
            if (this.stmtBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0 && this.stmt_ != null && this.stmt_ != MysqlxCursor.Open.OneOfMessage.getDefaultInstance()) {
                  this.stmt_ = MysqlxCursor.Open.OneOfMessage.newBuilder(this.stmt_).mergeFrom(value).buildPartial();
               } else {
                  this.stmt_ = value;
               }

               this.onChanged();
            } else {
               this.stmtBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 2;
            return this;
         }

         public Builder clearStmt() {
            if (this.stmtBuilder_ == null) {
               this.stmt_ = null;
               this.onChanged();
            } else {
               this.stmtBuilder_.clear();
            }

            this.bitField0_ &= -3;
            return this;
         }

         public OneOfMessage.Builder getStmtBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return (OneOfMessage.Builder)this.getStmtFieldBuilder().getBuilder();
         }

         public OneOfMessageOrBuilder getStmtOrBuilder() {
            if (this.stmtBuilder_ != null) {
               return (OneOfMessageOrBuilder)this.stmtBuilder_.getMessageOrBuilder();
            } else {
               return this.stmt_ == null ? MysqlxCursor.Open.OneOfMessage.getDefaultInstance() : this.stmt_;
            }
         }

         private SingleFieldBuilderV3<OneOfMessage, OneOfMessage.Builder, OneOfMessageOrBuilder> getStmtFieldBuilder() {
            if (this.stmtBuilder_ == null) {
               this.stmtBuilder_ = new SingleFieldBuilderV3(this.getStmt(), this.getParentForChildren(), this.isClean());
               this.stmt_ = null;
            }

            return this.stmtBuilder_;
         }

         public boolean hasFetchRows() {
            return (this.bitField0_ & 4) != 0;
         }

         public long getFetchRows() {
            return this.fetchRows_;
         }

         public Builder setFetchRows(long value) {
            this.bitField0_ |= 4;
            this.fetchRows_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearFetchRows() {
            this.bitField0_ &= -5;
            this.fetchRows_ = 0L;
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

      public static final class OneOfMessage extends GeneratedMessageV3 implements OneOfMessageOrBuilder {
         private static final long serialVersionUID = 0L;
         private int bitField0_;
         public static final int TYPE_FIELD_NUMBER = 1;
         private int type_;
         public static final int PREPARE_EXECUTE_FIELD_NUMBER = 2;
         private MysqlxPrepare.Execute prepareExecute_;
         private byte memoizedIsInitialized;
         private static final OneOfMessage DEFAULT_INSTANCE = new OneOfMessage();
         /** @deprecated */
         @Deprecated
         public static final Parser<OneOfMessage> PARSER = new AbstractParser<OneOfMessage>() {
            public OneOfMessage parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
               return new OneOfMessage(input, extensionRegistry);
            }
         };

         private OneOfMessage(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
         }

         private OneOfMessage() {
            this.memoizedIsInitialized = -1;
            this.type_ = 0;
         }

         protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new OneOfMessage();
         }

         public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
         }

         private OneOfMessage(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                           int rawValue = input.readEnum();
                           Type value = MysqlxCursor.Open.OneOfMessage.Type.valueOf(rawValue);
                           if (value == null) {
                              unknownFields.mergeVarintField(1, rawValue);
                           } else {
                              this.bitField0_ |= 1;
                              this.type_ = rawValue;
                           }
                           break;
                        case 18:
                           MysqlxPrepare.Execute.Builder subBuilder = null;
                           if ((this.bitField0_ & 2) != 0) {
                              subBuilder = this.prepareExecute_.toBuilder();
                           }

                           this.prepareExecute_ = (MysqlxPrepare.Execute)input.readMessage(MysqlxPrepare.Execute.PARSER, extensionRegistry);
                           if (subBuilder != null) {
                              subBuilder.mergeFrom(this.prepareExecute_);
                              this.prepareExecute_ = subBuilder.buildPartial();
                           }

                           this.bitField0_ |= 2;
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
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_fieldAccessorTable.ensureFieldAccessorsInitialized(OneOfMessage.class, Builder.class);
         }

         public boolean hasType() {
            return (this.bitField0_ & 1) != 0;
         }

         public Type getType() {
            Type result = MysqlxCursor.Open.OneOfMessage.Type.valueOf(this.type_);
            return result == null ? MysqlxCursor.Open.OneOfMessage.Type.PREPARE_EXECUTE : result;
         }

         public boolean hasPrepareExecute() {
            return (this.bitField0_ & 2) != 0;
         }

         public MysqlxPrepare.Execute getPrepareExecute() {
            return this.prepareExecute_ == null ? MysqlxPrepare.Execute.getDefaultInstance() : this.prepareExecute_;
         }

         public MysqlxPrepare.ExecuteOrBuilder getPrepareExecuteOrBuilder() {
            return this.prepareExecute_ == null ? MysqlxPrepare.Execute.getDefaultInstance() : this.prepareExecute_;
         }

         public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
               return true;
            } else if (isInitialized == 0) {
               return false;
            } else if (!this.hasType()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else if (this.hasPrepareExecute() && !this.getPrepareExecute().isInitialized()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else {
               this.memoizedIsInitialized = 1;
               return true;
            }
         }

         public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
               output.writeEnum(1, this.type_);
            }

            if ((this.bitField0_ & 2) != 0) {
               output.writeMessage(2, this.getPrepareExecute());
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
                  size += CodedOutputStream.computeEnumSize(1, this.type_);
               }

               if ((this.bitField0_ & 2) != 0) {
                  size += CodedOutputStream.computeMessageSize(2, this.getPrepareExecute());
               }

               size += this.unknownFields.getSerializedSize();
               this.memoizedSize = size;
               return size;
            }
         }

         public boolean equals(Object obj) {
            if (obj == this) {
               return true;
            } else if (!(obj instanceof OneOfMessage)) {
               return super.equals(obj);
            } else {
               OneOfMessage other = (OneOfMessage)obj;
               if (this.hasType() != other.hasType()) {
                  return false;
               } else if (this.hasType() && this.type_ != other.type_) {
                  return false;
               } else if (this.hasPrepareExecute() != other.hasPrepareExecute()) {
                  return false;
               } else if (this.hasPrepareExecute() && !this.getPrepareExecute().equals(other.getPrepareExecute())) {
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
               if (this.hasType()) {
                  hash = 37 * hash + 1;
                  hash = 53 * hash + this.type_;
               }

               if (this.hasPrepareExecute()) {
                  hash = 37 * hash + 2;
                  hash = 53 * hash + this.getPrepareExecute().hashCode();
               }

               hash = 29 * hash + this.unknownFields.hashCode();
               this.memoizedHashCode = hash;
               return hash;
            }
         }

         public static OneOfMessage parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (OneOfMessage)PARSER.parseFrom(data);
         }

         public static OneOfMessage parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (OneOfMessage)PARSER.parseFrom(data, extensionRegistry);
         }

         public static OneOfMessage parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (OneOfMessage)PARSER.parseFrom(data);
         }

         public static OneOfMessage parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (OneOfMessage)PARSER.parseFrom(data, extensionRegistry);
         }

         public static OneOfMessage parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (OneOfMessage)PARSER.parseFrom(data);
         }

         public static OneOfMessage parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (OneOfMessage)PARSER.parseFrom(data, extensionRegistry);
         }

         public static OneOfMessage parseFrom(InputStream input) throws IOException {
            return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static OneOfMessage parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public static OneOfMessage parseDelimitedFrom(InputStream input) throws IOException {
            return (OneOfMessage)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
         }

         public static OneOfMessage parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (OneOfMessage)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
         }

         public static OneOfMessage parseFrom(CodedInputStream input) throws IOException {
            return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static OneOfMessage parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public Builder newBuilderForType() {
            return newBuilder();
         }

         public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
         }

         public static Builder newBuilder(OneOfMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
         }

         public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
         }

         protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
         }

         public static OneOfMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<OneOfMessage> parser() {
            return PARSER;
         }

         public Parser<OneOfMessage> getParserForType() {
            return PARSER;
         }

         public OneOfMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
         }

         // $FF: synthetic method
         OneOfMessage(GeneratedMessageV3.Builder x0, Object x1) {
            this(x0);
         }

         // $FF: synthetic method
         OneOfMessage(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
            this(x0, x1);
         }

         public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements OneOfMessageOrBuilder {
            private int bitField0_;
            private int type_;
            private MysqlxPrepare.Execute prepareExecute_;
            private SingleFieldBuilderV3<MysqlxPrepare.Execute, MysqlxPrepare.Execute.Builder, MysqlxPrepare.ExecuteOrBuilder> prepareExecuteBuilder_;

            public static final Descriptors.Descriptor getDescriptor() {
               return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
               return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_fieldAccessorTable.ensureFieldAccessorsInitialized(OneOfMessage.class, Builder.class);
            }

            private Builder() {
               this.type_ = 0;
               this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
               super(parent);
               this.type_ = 0;
               this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
               if (MysqlxCursor.Open.OneOfMessage.alwaysUseFieldBuilders) {
                  this.getPrepareExecuteFieldBuilder();
               }

            }

            public Builder clear() {
               super.clear();
               this.type_ = 0;
               this.bitField0_ &= -2;
               if (this.prepareExecuteBuilder_ == null) {
                  this.prepareExecute_ = null;
               } else {
                  this.prepareExecuteBuilder_.clear();
               }

               this.bitField0_ &= -3;
               return this;
            }

            public Descriptors.Descriptor getDescriptorForType() {
               return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor;
            }

            public OneOfMessage getDefaultInstanceForType() {
               return MysqlxCursor.Open.OneOfMessage.getDefaultInstance();
            }

            public OneOfMessage build() {
               OneOfMessage result = this.buildPartial();
               if (!result.isInitialized()) {
                  throw newUninitializedMessageException(result);
               } else {
                  return result;
               }
            }

            public OneOfMessage buildPartial() {
               OneOfMessage result = new OneOfMessage(this);
               int from_bitField0_ = this.bitField0_;
               int to_bitField0_ = 0;
               if ((from_bitField0_ & 1) != 0) {
                  to_bitField0_ |= 1;
               }

               result.type_ = this.type_;
               if ((from_bitField0_ & 2) != 0) {
                  if (this.prepareExecuteBuilder_ == null) {
                     result.prepareExecute_ = this.prepareExecute_;
                  } else {
                     result.prepareExecute_ = (MysqlxPrepare.Execute)this.prepareExecuteBuilder_.build();
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
               if (other instanceof OneOfMessage) {
                  return this.mergeFrom((OneOfMessage)other);
               } else {
                  super.mergeFrom(other);
                  return this;
               }
            }

            public Builder mergeFrom(OneOfMessage other) {
               if (other == MysqlxCursor.Open.OneOfMessage.getDefaultInstance()) {
                  return this;
               } else {
                  if (other.hasType()) {
                     this.setType(other.getType());
                  }

                  if (other.hasPrepareExecute()) {
                     this.mergePrepareExecute(other.getPrepareExecute());
                  }

                  this.mergeUnknownFields(other.unknownFields);
                  this.onChanged();
                  return this;
               }
            }

            public final boolean isInitialized() {
               if (!this.hasType()) {
                  return false;
               } else {
                  return !this.hasPrepareExecute() || this.getPrepareExecute().isInitialized();
               }
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
               OneOfMessage parsedMessage = null;

               try {
                  parsedMessage = (OneOfMessage)MysqlxCursor.Open.OneOfMessage.PARSER.parsePartialFrom(input, extensionRegistry);
               } catch (InvalidProtocolBufferException var8) {
                  parsedMessage = (OneOfMessage)var8.getUnfinishedMessage();
                  throw var8.unwrapIOException();
               } finally {
                  if (parsedMessage != null) {
                     this.mergeFrom(parsedMessage);
                  }

               }

               return this;
            }

            public boolean hasType() {
               return (this.bitField0_ & 1) != 0;
            }

            public Type getType() {
               Type result = MysqlxCursor.Open.OneOfMessage.Type.valueOf(this.type_);
               return result == null ? MysqlxCursor.Open.OneOfMessage.Type.PREPARE_EXECUTE : result;
            }

            public Builder setType(Type value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 1;
                  this.type_ = value.getNumber();
                  this.onChanged();
                  return this;
               }
            }

            public Builder clearType() {
               this.bitField0_ &= -2;
               this.type_ = 0;
               this.onChanged();
               return this;
            }

            public boolean hasPrepareExecute() {
               return (this.bitField0_ & 2) != 0;
            }

            public MysqlxPrepare.Execute getPrepareExecute() {
               if (this.prepareExecuteBuilder_ == null) {
                  return this.prepareExecute_ == null ? MysqlxPrepare.Execute.getDefaultInstance() : this.prepareExecute_;
               } else {
                  return (MysqlxPrepare.Execute)this.prepareExecuteBuilder_.getMessage();
               }
            }

            public Builder setPrepareExecute(MysqlxPrepare.Execute value) {
               if (this.prepareExecuteBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.prepareExecute_ = value;
                  this.onChanged();
               } else {
                  this.prepareExecuteBuilder_.setMessage(value);
               }

               this.bitField0_ |= 2;
               return this;
            }

            public Builder setPrepareExecute(MysqlxPrepare.Execute.Builder builderForValue) {
               if (this.prepareExecuteBuilder_ == null) {
                  this.prepareExecute_ = builderForValue.build();
                  this.onChanged();
               } else {
                  this.prepareExecuteBuilder_.setMessage(builderForValue.build());
               }

               this.bitField0_ |= 2;
               return this;
            }

            public Builder mergePrepareExecute(MysqlxPrepare.Execute value) {
               if (this.prepareExecuteBuilder_ == null) {
                  if ((this.bitField0_ & 2) != 0 && this.prepareExecute_ != null && this.prepareExecute_ != MysqlxPrepare.Execute.getDefaultInstance()) {
                     this.prepareExecute_ = MysqlxPrepare.Execute.newBuilder(this.prepareExecute_).mergeFrom(value).buildPartial();
                  } else {
                     this.prepareExecute_ = value;
                  }

                  this.onChanged();
               } else {
                  this.prepareExecuteBuilder_.mergeFrom(value);
               }

               this.bitField0_ |= 2;
               return this;
            }

            public Builder clearPrepareExecute() {
               if (this.prepareExecuteBuilder_ == null) {
                  this.prepareExecute_ = null;
                  this.onChanged();
               } else {
                  this.prepareExecuteBuilder_.clear();
               }

               this.bitField0_ &= -3;
               return this;
            }

            public MysqlxPrepare.Execute.Builder getPrepareExecuteBuilder() {
               this.bitField0_ |= 2;
               this.onChanged();
               return (MysqlxPrepare.Execute.Builder)this.getPrepareExecuteFieldBuilder().getBuilder();
            }

            public MysqlxPrepare.ExecuteOrBuilder getPrepareExecuteOrBuilder() {
               if (this.prepareExecuteBuilder_ != null) {
                  return (MysqlxPrepare.ExecuteOrBuilder)this.prepareExecuteBuilder_.getMessageOrBuilder();
               } else {
                  return this.prepareExecute_ == null ? MysqlxPrepare.Execute.getDefaultInstance() : this.prepareExecute_;
               }
            }

            private SingleFieldBuilderV3<MysqlxPrepare.Execute, MysqlxPrepare.Execute.Builder, MysqlxPrepare.ExecuteOrBuilder> getPrepareExecuteFieldBuilder() {
               if (this.prepareExecuteBuilder_ == null) {
                  this.prepareExecuteBuilder_ = new SingleFieldBuilderV3(this.getPrepareExecute(), this.getParentForChildren(), this.isClean());
                  this.prepareExecute_ = null;
               }

               return this.prepareExecuteBuilder_;
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
            PREPARE_EXECUTE(0);

            public static final int PREPARE_EXECUTE_VALUE = 0;
            private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() {
               public Type findValueByNumber(int number) {
                  return MysqlxCursor.Open.OneOfMessage.Type.forNumber(number);
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
                     return PREPARE_EXECUTE;
                  default:
                     return null;
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
               return (Descriptors.EnumDescriptor)MysqlxCursor.Open.OneOfMessage.getDescriptor().getEnumTypes().get(0);
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

      public interface OneOfMessageOrBuilder extends MessageOrBuilder {
         boolean hasType();

         OneOfMessage.Type getType();

         boolean hasPrepareExecute();

         MysqlxPrepare.Execute getPrepareExecute();

         MysqlxPrepare.ExecuteOrBuilder getPrepareExecuteOrBuilder();
      }
   }

   public interface OpenOrBuilder extends MessageOrBuilder {
      boolean hasCursorId();

      int getCursorId();

      boolean hasStmt();

      Open.OneOfMessage getStmt();

      Open.OneOfMessageOrBuilder getStmtOrBuilder();

      boolean hasFetchRows();

      long getFetchRows();
   }
}
