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
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxSql {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Sql_StmtExecute_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Sql_StmtExecute_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Sql_StmtExecuteOk_fieldAccessorTable;
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxSql() {
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
      String[] descriptorData = new String[]{"\n\u0010mysqlx_sql.proto\u0012\nMysqlx.Sql\u001a\fmysqlx.proto\u001a\u0016mysqlx_datatypes.proto\"\u007f\n\u000bStmtExecute\u0012\u0016\n\tnamespace\u0018\u0003 \u0001(\t:\u0003sql\u0012\f\n\u0004stmt\u0018\u0001 \u0002(\f\u0012#\n\u0004args\u0018\u0002 \u0003(\u000b2\u0015.Mysqlx.Datatypes.Any\u0012\u001f\n\u0010compact_metadata\u0018\u0004 \u0001(\b:\u0005false:\u0004\u0088ê0\f\"\u0015\n\rStmtExecuteOk:\u0004\u0090ê0\u0011B\u0019\n\u0017com.mysql.cj.x.protobuf"};
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Mysqlx.getDescriptor(), MysqlxDatatypes.getDescriptor()});
      internal_static_Mysqlx_Sql_StmtExecute_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(0);
      internal_static_Mysqlx_Sql_StmtExecute_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Sql_StmtExecute_descriptor, new String[]{"Namespace", "Stmt", "Args", "CompactMetadata"});
      internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(1);
      internal_static_Mysqlx_Sql_StmtExecuteOk_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor, new String[0]);
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.clientMessageId);
      registry.add(Mysqlx.serverMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      Mysqlx.getDescriptor();
      MysqlxDatatypes.getDescriptor();
   }

   public static final class StmtExecuteOk extends GeneratedMessageV3 implements StmtExecuteOkOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized;
      private static final StmtExecuteOk DEFAULT_INSTANCE = new StmtExecuteOk();
      /** @deprecated */
      @Deprecated
      public static final Parser<StmtExecuteOk> PARSER = new AbstractParser<StmtExecuteOk>() {
         public StmtExecuteOk parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new StmtExecuteOk(input, extensionRegistry);
         }
      };

      private StmtExecuteOk(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private StmtExecuteOk() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new StmtExecuteOk();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private StmtExecuteOk(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_fieldAccessorTable.ensureFieldAccessorsInitialized(StmtExecuteOk.class, Builder.class);
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
         } else if (!(obj instanceof StmtExecuteOk)) {
            return super.equals(obj);
         } else {
            StmtExecuteOk other = (StmtExecuteOk)obj;
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

      public static StmtExecuteOk parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (StmtExecuteOk)PARSER.parseFrom(data);
      }

      public static StmtExecuteOk parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (StmtExecuteOk)PARSER.parseFrom(data, extensionRegistry);
      }

      public static StmtExecuteOk parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (StmtExecuteOk)PARSER.parseFrom(data);
      }

      public static StmtExecuteOk parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (StmtExecuteOk)PARSER.parseFrom(data, extensionRegistry);
      }

      public static StmtExecuteOk parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (StmtExecuteOk)PARSER.parseFrom(data);
      }

      public static StmtExecuteOk parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (StmtExecuteOk)PARSER.parseFrom(data, extensionRegistry);
      }

      public static StmtExecuteOk parseFrom(InputStream input) throws IOException {
         return (StmtExecuteOk)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static StmtExecuteOk parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (StmtExecuteOk)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static StmtExecuteOk parseDelimitedFrom(InputStream input) throws IOException {
         return (StmtExecuteOk)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static StmtExecuteOk parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (StmtExecuteOk)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static StmtExecuteOk parseFrom(CodedInputStream input) throws IOException {
         return (StmtExecuteOk)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static StmtExecuteOk parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (StmtExecuteOk)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(StmtExecuteOk prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static StmtExecuteOk getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<StmtExecuteOk> parser() {
         return PARSER;
      }

      public Parser<StmtExecuteOk> getParserForType() {
         return PARSER;
      }

      public StmtExecuteOk getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      StmtExecuteOk(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      StmtExecuteOk(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements StmtExecuteOkOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_fieldAccessorTable.ensureFieldAccessorsInitialized(StmtExecuteOk.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxSql.StmtExecuteOk.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor;
         }

         public StmtExecuteOk getDefaultInstanceForType() {
            return MysqlxSql.StmtExecuteOk.getDefaultInstance();
         }

         public StmtExecuteOk build() {
            StmtExecuteOk result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public StmtExecuteOk buildPartial() {
            StmtExecuteOk result = new StmtExecuteOk(this);
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
            if (other instanceof StmtExecuteOk) {
               return this.mergeFrom((StmtExecuteOk)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(StmtExecuteOk other) {
            if (other == MysqlxSql.StmtExecuteOk.getDefaultInstance()) {
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
            StmtExecuteOk parsedMessage = null;

            try {
               parsedMessage = (StmtExecuteOk)MysqlxSql.StmtExecuteOk.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (StmtExecuteOk)var8.getUnfinishedMessage();
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

   public interface StmtExecuteOkOrBuilder extends MessageOrBuilder {
   }

   public static final class StmtExecute extends GeneratedMessageV3 implements StmtExecuteOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int NAMESPACE_FIELD_NUMBER = 3;
      private volatile Object namespace_;
      public static final int STMT_FIELD_NUMBER = 1;
      private ByteString stmt_;
      public static final int ARGS_FIELD_NUMBER = 2;
      private List<MysqlxDatatypes.Any> args_;
      public static final int COMPACT_METADATA_FIELD_NUMBER = 4;
      private boolean compactMetadata_;
      private byte memoizedIsInitialized;
      private static final StmtExecute DEFAULT_INSTANCE = new StmtExecute();
      /** @deprecated */
      @Deprecated
      public static final Parser<StmtExecute> PARSER = new AbstractParser<StmtExecute>() {
         public StmtExecute parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new StmtExecute(input, extensionRegistry);
         }
      };

      private StmtExecute(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private StmtExecute() {
         this.memoizedIsInitialized = -1;
         this.namespace_ = "sql";
         this.stmt_ = ByteString.EMPTY;
         this.args_ = Collections.emptyList();
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new StmtExecute();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private StmtExecute(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
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
                        this.bitField0_ |= 2;
                        this.stmt_ = input.readBytes();
                        break;
                     case 18:
                        if ((mutable_bitField0_ & 4) == 0) {
                           this.args_ = new ArrayList();
                           mutable_bitField0_ |= 4;
                        }

                        this.args_.add(input.readMessage(MysqlxDatatypes.Any.PARSER, extensionRegistry));
                        break;
                     case 26:
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 1;
                        this.namespace_ = bs;
                        break;
                     case 32:
                        this.bitField0_ |= 4;
                        this.compactMetadata_ = input.readBool();
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
               if ((mutable_bitField0_ & 4) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_fieldAccessorTable.ensureFieldAccessorsInitialized(StmtExecute.class, Builder.class);
      }

      public boolean hasNamespace() {
         return (this.bitField0_ & 1) != 0;
      }

      public String getNamespace() {
         Object ref = this.namespace_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.namespace_ = s;
            }

            return s;
         }
      }

      public ByteString getNamespaceBytes() {
         Object ref = this.namespace_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.namespace_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public boolean hasStmt() {
         return (this.bitField0_ & 2) != 0;
      }

      public ByteString getStmt() {
         return this.stmt_;
      }

      public List<MysqlxDatatypes.Any> getArgsList() {
         return this.args_;
      }

      public List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList() {
         return this.args_;
      }

      public int getArgsCount() {
         return this.args_.size();
      }

      public MysqlxDatatypes.Any getArgs(int index) {
         return (MysqlxDatatypes.Any)this.args_.get(index);
      }

      public MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int index) {
         return (MysqlxDatatypes.AnyOrBuilder)this.args_.get(index);
      }

      public boolean hasCompactMetadata() {
         return (this.bitField0_ & 4) != 0;
      }

      public boolean getCompactMetadata() {
         return this.compactMetadata_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasStmt()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            for(int i = 0; i < this.getArgsCount(); ++i) {
               if (!this.getArgs(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 2) != 0) {
            output.writeBytes(1, this.stmt_);
         }

         for(int i = 0; i < this.args_.size(); ++i) {
            output.writeMessage(2, (MessageLite)this.args_.get(i));
         }

         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(output, 3, this.namespace_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeBool(4, this.compactMetadata_);
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeBytesSize(1, this.stmt_);
            }

            for(int i = 0; i < this.args_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(2, (MessageLite)this.args_.get(i));
            }

            if ((this.bitField0_ & 1) != 0) {
               size += GeneratedMessageV3.computeStringSize(3, this.namespace_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeBoolSize(4, this.compactMetadata_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof StmtExecute)) {
            return super.equals(obj);
         } else {
            StmtExecute other = (StmtExecute)obj;
            if (this.hasNamespace() != other.hasNamespace()) {
               return false;
            } else if (this.hasNamespace() && !this.getNamespace().equals(other.getNamespace())) {
               return false;
            } else if (this.hasStmt() != other.hasStmt()) {
               return false;
            } else if (this.hasStmt() && !this.getStmt().equals(other.getStmt())) {
               return false;
            } else if (!this.getArgsList().equals(other.getArgsList())) {
               return false;
            } else if (this.hasCompactMetadata() != other.hasCompactMetadata()) {
               return false;
            } else if (this.hasCompactMetadata() && this.getCompactMetadata() != other.getCompactMetadata()) {
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
            if (this.hasNamespace()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getNamespace().hashCode();
            }

            if (this.hasStmt()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getStmt().hashCode();
            }

            if (this.getArgsCount() > 0) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getArgsList().hashCode();
            }

            if (this.hasCompactMetadata()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + Internal.hashBoolean(this.getCompactMetadata());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static StmtExecute parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (StmtExecute)PARSER.parseFrom(data);
      }

      public static StmtExecute parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (StmtExecute)PARSER.parseFrom(data, extensionRegistry);
      }

      public static StmtExecute parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (StmtExecute)PARSER.parseFrom(data);
      }

      public static StmtExecute parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (StmtExecute)PARSER.parseFrom(data, extensionRegistry);
      }

      public static StmtExecute parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (StmtExecute)PARSER.parseFrom(data);
      }

      public static StmtExecute parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (StmtExecute)PARSER.parseFrom(data, extensionRegistry);
      }

      public static StmtExecute parseFrom(InputStream input) throws IOException {
         return (StmtExecute)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static StmtExecute parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (StmtExecute)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static StmtExecute parseDelimitedFrom(InputStream input) throws IOException {
         return (StmtExecute)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static StmtExecute parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (StmtExecute)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static StmtExecute parseFrom(CodedInputStream input) throws IOException {
         return (StmtExecute)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static StmtExecute parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (StmtExecute)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(StmtExecute prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static StmtExecute getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<StmtExecute> parser() {
         return PARSER;
      }

      public Parser<StmtExecute> getParserForType() {
         return PARSER;
      }

      public StmtExecute getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      StmtExecute(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      StmtExecute(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements StmtExecuteOrBuilder {
         private int bitField0_;
         private Object namespace_;
         private ByteString stmt_;
         private List<MysqlxDatatypes.Any> args_;
         private RepeatedFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> argsBuilder_;
         private boolean compactMetadata_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_fieldAccessorTable.ensureFieldAccessorsInitialized(StmtExecute.class, Builder.class);
         }

         private Builder() {
            this.namespace_ = "sql";
            this.stmt_ = ByteString.EMPTY;
            this.args_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.namespace_ = "sql";
            this.stmt_ = ByteString.EMPTY;
            this.args_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxSql.StmtExecute.alwaysUseFieldBuilders) {
               this.getArgsFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            this.namespace_ = "sql";
            this.bitField0_ &= -2;
            this.stmt_ = ByteString.EMPTY;
            this.bitField0_ &= -3;
            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -5;
            } else {
               this.argsBuilder_.clear();
            }

            this.compactMetadata_ = false;
            this.bitField0_ &= -9;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_descriptor;
         }

         public StmtExecute getDefaultInstanceForType() {
            return MysqlxSql.StmtExecute.getDefaultInstance();
         }

         public StmtExecute build() {
            StmtExecute result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public StmtExecute buildPartial() {
            StmtExecute result = new StmtExecute(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.namespace_ = this.namespace_;
            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.stmt_ = this.stmt_;
            if (this.argsBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
                  this.bitField0_ &= -5;
               }

               result.args_ = this.args_;
            } else {
               result.args_ = this.argsBuilder_.build();
            }

            if ((from_bitField0_ & 8) != 0) {
               result.compactMetadata_ = this.compactMetadata_;
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
            if (other instanceof StmtExecute) {
               return this.mergeFrom((StmtExecute)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(StmtExecute other) {
            if (other == MysqlxSql.StmtExecute.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasNamespace()) {
                  this.bitField0_ |= 1;
                  this.namespace_ = other.namespace_;
                  this.onChanged();
               }

               if (other.hasStmt()) {
                  this.setStmt(other.getStmt());
               }

               if (this.argsBuilder_ == null) {
                  if (!other.args_.isEmpty()) {
                     if (this.args_.isEmpty()) {
                        this.args_ = other.args_;
                        this.bitField0_ &= -5;
                     } else {
                        this.ensureArgsIsMutable();
                        this.args_.addAll(other.args_);
                     }

                     this.onChanged();
                  }
               } else if (!other.args_.isEmpty()) {
                  if (this.argsBuilder_.isEmpty()) {
                     this.argsBuilder_.dispose();
                     this.argsBuilder_ = null;
                     this.args_ = other.args_;
                     this.bitField0_ &= -5;
                     this.argsBuilder_ = MysqlxSql.StmtExecute.alwaysUseFieldBuilders ? this.getArgsFieldBuilder() : null;
                  } else {
                     this.argsBuilder_.addAllMessages(other.args_);
                  }
               }

               if (other.hasCompactMetadata()) {
                  this.setCompactMetadata(other.getCompactMetadata());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasStmt()) {
               return false;
            } else {
               for(int i = 0; i < this.getArgsCount(); ++i) {
                  if (!this.getArgs(i).isInitialized()) {
                     return false;
                  }
               }

               return true;
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            StmtExecute parsedMessage = null;

            try {
               parsedMessage = (StmtExecute)MysqlxSql.StmtExecute.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (StmtExecute)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasNamespace() {
            return (this.bitField0_ & 1) != 0;
         }

         public String getNamespace() {
            Object ref = this.namespace_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.namespace_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getNamespaceBytes() {
            Object ref = this.namespace_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.namespace_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setNamespace(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.namespace_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearNamespace() {
            this.bitField0_ &= -2;
            this.namespace_ = MysqlxSql.StmtExecute.getDefaultInstance().getNamespace();
            this.onChanged();
            return this;
         }

         public Builder setNamespaceBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.namespace_ = value;
               this.onChanged();
               return this;
            }
         }

         public boolean hasStmt() {
            return (this.bitField0_ & 2) != 0;
         }

         public ByteString getStmt() {
            return this.stmt_;
         }

         public Builder setStmt(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.stmt_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearStmt() {
            this.bitField0_ &= -3;
            this.stmt_ = MysqlxSql.StmtExecute.getDefaultInstance().getStmt();
            this.onChanged();
            return this;
         }

         private void ensureArgsIsMutable() {
            if ((this.bitField0_ & 4) == 0) {
               this.args_ = new ArrayList(this.args_);
               this.bitField0_ |= 4;
            }

         }

         public List<MysqlxDatatypes.Any> getArgsList() {
            return this.argsBuilder_ == null ? Collections.unmodifiableList(this.args_) : this.argsBuilder_.getMessageList();
         }

         public int getArgsCount() {
            return this.argsBuilder_ == null ? this.args_.size() : this.argsBuilder_.getCount();
         }

         public MysqlxDatatypes.Any getArgs(int index) {
            return this.argsBuilder_ == null ? (MysqlxDatatypes.Any)this.args_.get(index) : (MysqlxDatatypes.Any)this.argsBuilder_.getMessage(index);
         }

         public Builder setArgs(int index, MysqlxDatatypes.Any value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.set(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setArgs(int index, MysqlxDatatypes.Any.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addArgs(MysqlxDatatypes.Any value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addArgs(int index, MysqlxDatatypes.Any value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addArgs(MysqlxDatatypes.Any.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addArgs(int index, MysqlxDatatypes.Any.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Any> values) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.args_);
               this.onChanged();
            } else {
               this.argsBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearArgs() {
            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -5;
               this.onChanged();
            } else {
               this.argsBuilder_.clear();
            }

            return this;
         }

         public Builder removeArgs(int index) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.remove(index);
               this.onChanged();
            } else {
               this.argsBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxDatatypes.Any.Builder getArgsBuilder(int index) {
            return (MysqlxDatatypes.Any.Builder)this.getArgsFieldBuilder().getBuilder(index);
         }

         public MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int index) {
            return this.argsBuilder_ == null ? (MysqlxDatatypes.AnyOrBuilder)this.args_.get(index) : (MysqlxDatatypes.AnyOrBuilder)this.argsBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList() {
            return this.argsBuilder_ != null ? this.argsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.args_);
         }

         public MysqlxDatatypes.Any.Builder addArgsBuilder() {
            return (MysqlxDatatypes.Any.Builder)this.getArgsFieldBuilder().addBuilder(MysqlxDatatypes.Any.getDefaultInstance());
         }

         public MysqlxDatatypes.Any.Builder addArgsBuilder(int index) {
            return (MysqlxDatatypes.Any.Builder)this.getArgsFieldBuilder().addBuilder(index, MysqlxDatatypes.Any.getDefaultInstance());
         }

         public List<MysqlxDatatypes.Any.Builder> getArgsBuilderList() {
            return this.getArgsFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> getArgsFieldBuilder() {
            if (this.argsBuilder_ == null) {
               this.argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, (this.bitField0_ & 4) != 0, this.getParentForChildren(), this.isClean());
               this.args_ = null;
            }

            return this.argsBuilder_;
         }

         public boolean hasCompactMetadata() {
            return (this.bitField0_ & 8) != 0;
         }

         public boolean getCompactMetadata() {
            return this.compactMetadata_;
         }

         public Builder setCompactMetadata(boolean value) {
            this.bitField0_ |= 8;
            this.compactMetadata_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearCompactMetadata() {
            this.bitField0_ &= -9;
            this.compactMetadata_ = false;
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

   public interface StmtExecuteOrBuilder extends MessageOrBuilder {
      boolean hasNamespace();

      String getNamespace();

      ByteString getNamespaceBytes();

      boolean hasStmt();

      ByteString getStmt();

      List<MysqlxDatatypes.Any> getArgsList();

      MysqlxDatatypes.Any getArgs(int var1);

      int getArgsCount();

      List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList();

      MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int var1);

      boolean hasCompactMetadata();

      boolean getCompactMetadata();
   }
}
