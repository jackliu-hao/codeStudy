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
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxResultset {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchDone_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchDone_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchSuspended_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchSuspended_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_ColumnMetaData_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_Row_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_Row_fieldAccessorTable;
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxResultset() {
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
      String[] descriptorData = new String[]{"\n\u0016mysqlx_resultset.proto\u0012\u0010Mysqlx.Resultset\u001a\fmysqlx.proto\"\u001e\n\u0016FetchDoneMoreOutParams:\u0004\u0090ê0\u0012\"\u001f\n\u0017FetchDoneMoreResultsets:\u0004\u0090ê0\u0010\"\u0011\n\tFetchDone:\u0004\u0090ê0\u000e\"\u0016\n\u000eFetchSuspended:\u0004\u0090ê0\u000f\"¥\u0003\n\u000eColumnMetaData\u00128\n\u0004type\u0018\u0001 \u0002(\u000e2*.Mysqlx.Resultset.ColumnMetaData.FieldType\u0012\f\n\u0004name\u0018\u0002 \u0001(\f\u0012\u0015\n\roriginal_name\u0018\u0003 \u0001(\f\u0012\r\n\u0005table\u0018\u0004 \u0001(\f\u0012\u0016\n\u000eoriginal_table\u0018\u0005 \u0001(\f\u0012\u000e\n\u0006schema\u0018\u0006 \u0001(\f\u0012\u000f\n\u0007catalog\u0018\u0007 \u0001(\f\u0012\u0011\n\tcollation\u0018\b \u0001(\u0004\u0012\u0019\n\u0011fractional_digits\u0018\t \u0001(\r\u0012\u000e\n\u0006length\u0018\n \u0001(\r\u0012\r\n\u0005flags\u0018\u000b \u0001(\r\u0012\u0014\n\fcontent_type\u0018\f \u0001(\r\"\u0082\u0001\n\tFieldType\u0012\b\n\u0004SINT\u0010\u0001\u0012\b\n\u0004UINT\u0010\u0002\u0012\n\n\u0006DOUBLE\u0010\u0005\u0012\t\n\u0005FLOAT\u0010\u0006\u0012\t\n\u0005BYTES\u0010\u0007\u0012\b\n\u0004TIME\u0010\n\u0012\f\n\bDATETIME\u0010\f\u0012\u0007\n\u0003SET\u0010\u000f\u0012\b\n\u0004ENUM\u0010\u0010\u0012\u0007\n\u0003BIT\u0010\u0011\u0012\u000b\n\u0007DECIMAL\u0010\u0012:\u0004\u0090ê0\f\"\u001a\n\u0003Row\u0012\r\n\u0005field\u0018\u0001 \u0003(\f:\u0004\u0090ê0\r*4\n\u0011ContentType_BYTES\u0012\f\n\bGEOMETRY\u0010\u0001\u0012\b\n\u0004JSON\u0010\u0002\u0012\u0007\n\u0003XML\u0010\u0003*.\n\u0014ContentType_DATETIME\u0012\b\n\u0004DATE\u0010\u0001\u0012\f\n\bDATETIME\u0010\u0002B\u0019\n\u0017com.mysql.cj.x.protobuf"};
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Mysqlx.getDescriptor()});
      internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(0);
      internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor, new String[0]);
      internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(1);
      internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor, new String[0]);
      internal_static_Mysqlx_Resultset_FetchDone_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(2);
      internal_static_Mysqlx_Resultset_FetchDone_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_FetchDone_descriptor, new String[0]);
      internal_static_Mysqlx_Resultset_FetchSuspended_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(3);
      internal_static_Mysqlx_Resultset_FetchSuspended_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_FetchSuspended_descriptor, new String[0]);
      internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(4);
      internal_static_Mysqlx_Resultset_ColumnMetaData_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor, new String[]{"Type", "Name", "OriginalName", "Table", "OriginalTable", "Schema", "Catalog", "Collation", "FractionalDigits", "Length", "Flags", "ContentType"});
      internal_static_Mysqlx_Resultset_Row_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(5);
      internal_static_Mysqlx_Resultset_Row_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_Row_descriptor, new String[]{"Field"});
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.serverMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      Mysqlx.getDescriptor();
   }

   public static final class Row extends GeneratedMessageV3 implements RowOrBuilder {
      private static final long serialVersionUID = 0L;
      public static final int FIELD_FIELD_NUMBER = 1;
      private List<ByteString> field_;
      private byte memoizedIsInitialized;
      private static final Row DEFAULT_INSTANCE = new Row();
      /** @deprecated */
      @Deprecated
      public static final Parser<Row> PARSER = new AbstractParser<Row>() {
         public Row parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Row(input, extensionRegistry);
         }
      };

      private Row(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Row() {
         this.memoizedIsInitialized = -1;
         this.field_ = Collections.emptyList();
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Row();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Row(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                           this.field_ = new ArrayList();
                           mutable_bitField0_ |= true;
                        }

                        this.field_.add(input.readBytes());
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
                  this.field_ = Collections.unmodifiableList(this.field_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_fieldAccessorTable.ensureFieldAccessorsInitialized(Row.class, Builder.class);
      }

      public List<ByteString> getFieldList() {
         return this.field_;
      }

      public int getFieldCount() {
         return this.field_.size();
      }

      public ByteString getField(int index) {
         return (ByteString)this.field_.get(index);
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
         for(int i = 0; i < this.field_.size(); ++i) {
            output.writeBytes(1, (ByteString)this.field_.get(i));
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            int size = 0;
            int dataSize = 0;

            for(int i = 0; i < this.field_.size(); ++i) {
               dataSize += CodedOutputStream.computeBytesSizeNoTag((ByteString)this.field_.get(i));
            }

            size = size + dataSize;
            size += 1 * this.getFieldList().size();
            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Row)) {
            return super.equals(obj);
         } else {
            Row other = (Row)obj;
            if (!this.getFieldList().equals(other.getFieldList())) {
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
            if (this.getFieldCount() > 0) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getFieldList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Row parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Row)PARSER.parseFrom(data);
      }

      public static Row parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Row)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Row parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Row)PARSER.parseFrom(data);
      }

      public static Row parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Row)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Row parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Row)PARSER.parseFrom(data);
      }

      public static Row parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Row)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Row parseFrom(InputStream input) throws IOException {
         return (Row)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Row parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Row)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Row parseDelimitedFrom(InputStream input) throws IOException {
         return (Row)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Row parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Row)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Row parseFrom(CodedInputStream input) throws IOException {
         return (Row)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Row parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Row)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Row prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Row getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Row> parser() {
         return PARSER;
      }

      public Parser<Row> getParserForType() {
         return PARSER;
      }

      public Row getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Row(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Row(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements RowOrBuilder {
         private int bitField0_;
         private List<ByteString> field_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_fieldAccessorTable.ensureFieldAccessorsInitialized(Row.class, Builder.class);
         }

         private Builder() {
            this.field_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.field_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxResultset.Row.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            this.field_ = Collections.emptyList();
            this.bitField0_ &= -2;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_descriptor;
         }

         public Row getDefaultInstanceForType() {
            return MysqlxResultset.Row.getDefaultInstance();
         }

         public Row build() {
            Row result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Row buildPartial() {
            Row result = new Row(this);
            int from_bitField0_ = this.bitField0_;
            if ((this.bitField0_ & 1) != 0) {
               this.field_ = Collections.unmodifiableList(this.field_);
               this.bitField0_ &= -2;
            }

            result.field_ = this.field_;
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
            if (other instanceof Row) {
               return this.mergeFrom((Row)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Row other) {
            if (other == MysqlxResultset.Row.getDefaultInstance()) {
               return this;
            } else {
               if (!other.field_.isEmpty()) {
                  if (this.field_.isEmpty()) {
                     this.field_ = other.field_;
                     this.bitField0_ &= -2;
                  } else {
                     this.ensureFieldIsMutable();
                     this.field_.addAll(other.field_);
                  }

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
            Row parsedMessage = null;

            try {
               parsedMessage = (Row)MysqlxResultset.Row.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Row)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         private void ensureFieldIsMutable() {
            if ((this.bitField0_ & 1) == 0) {
               this.field_ = new ArrayList(this.field_);
               this.bitField0_ |= 1;
            }

         }

         public List<ByteString> getFieldList() {
            return (this.bitField0_ & 1) != 0 ? Collections.unmodifiableList(this.field_) : this.field_;
         }

         public int getFieldCount() {
            return this.field_.size();
         }

         public ByteString getField(int index) {
            return (ByteString)this.field_.get(index);
         }

         public Builder setField(int index, ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureFieldIsMutable();
               this.field_.set(index, value);
               this.onChanged();
               return this;
            }
         }

         public Builder addField(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureFieldIsMutable();
               this.field_.add(value);
               this.onChanged();
               return this;
            }
         }

         public Builder addAllField(Iterable<? extends ByteString> values) {
            this.ensureFieldIsMutable();
            AbstractMessageLite.Builder.addAll(values, this.field_);
            this.onChanged();
            return this;
         }

         public Builder clearField() {
            this.field_ = Collections.emptyList();
            this.bitField0_ &= -2;
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

   public interface RowOrBuilder extends MessageOrBuilder {
      List<ByteString> getFieldList();

      int getFieldCount();

      ByteString getField(int var1);
   }

   public static final class ColumnMetaData extends GeneratedMessageV3 implements ColumnMetaDataOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int TYPE_FIELD_NUMBER = 1;
      private int type_;
      public static final int NAME_FIELD_NUMBER = 2;
      private ByteString name_;
      public static final int ORIGINAL_NAME_FIELD_NUMBER = 3;
      private ByteString originalName_;
      public static final int TABLE_FIELD_NUMBER = 4;
      private ByteString table_;
      public static final int ORIGINAL_TABLE_FIELD_NUMBER = 5;
      private ByteString originalTable_;
      public static final int SCHEMA_FIELD_NUMBER = 6;
      private ByteString schema_;
      public static final int CATALOG_FIELD_NUMBER = 7;
      private ByteString catalog_;
      public static final int COLLATION_FIELD_NUMBER = 8;
      private long collation_;
      public static final int FRACTIONAL_DIGITS_FIELD_NUMBER = 9;
      private int fractionalDigits_;
      public static final int LENGTH_FIELD_NUMBER = 10;
      private int length_;
      public static final int FLAGS_FIELD_NUMBER = 11;
      private int flags_;
      public static final int CONTENT_TYPE_FIELD_NUMBER = 12;
      private int contentType_;
      private byte memoizedIsInitialized;
      private static final ColumnMetaData DEFAULT_INSTANCE = new ColumnMetaData();
      /** @deprecated */
      @Deprecated
      public static final Parser<ColumnMetaData> PARSER = new AbstractParser<ColumnMetaData>() {
         public ColumnMetaData parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new ColumnMetaData(input, extensionRegistry);
         }
      };

      private ColumnMetaData(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private ColumnMetaData() {
         this.memoizedIsInitialized = -1;
         this.type_ = 1;
         this.name_ = ByteString.EMPTY;
         this.originalName_ = ByteString.EMPTY;
         this.table_ = ByteString.EMPTY;
         this.originalTable_ = ByteString.EMPTY;
         this.schema_ = ByteString.EMPTY;
         this.catalog_ = ByteString.EMPTY;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new ColumnMetaData();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private ColumnMetaData(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        FieldType value = MysqlxResultset.ColumnMetaData.FieldType.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(1, rawValue);
                        } else {
                           this.bitField0_ |= 1;
                           this.type_ = rawValue;
                        }
                        break;
                     case 18:
                        this.bitField0_ |= 2;
                        this.name_ = input.readBytes();
                        break;
                     case 26:
                        this.bitField0_ |= 4;
                        this.originalName_ = input.readBytes();
                        break;
                     case 34:
                        this.bitField0_ |= 8;
                        this.table_ = input.readBytes();
                        break;
                     case 42:
                        this.bitField0_ |= 16;
                        this.originalTable_ = input.readBytes();
                        break;
                     case 50:
                        this.bitField0_ |= 32;
                        this.schema_ = input.readBytes();
                        break;
                     case 58:
                        this.bitField0_ |= 64;
                        this.catalog_ = input.readBytes();
                        break;
                     case 64:
                        this.bitField0_ |= 128;
                        this.collation_ = input.readUInt64();
                        break;
                     case 72:
                        this.bitField0_ |= 256;
                        this.fractionalDigits_ = input.readUInt32();
                        break;
                     case 80:
                        this.bitField0_ |= 512;
                        this.length_ = input.readUInt32();
                        break;
                     case 88:
                        this.bitField0_ |= 1024;
                        this.flags_ = input.readUInt32();
                        break;
                     case 96:
                        this.bitField0_ |= 2048;
                        this.contentType_ = input.readUInt32();
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
         return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_fieldAccessorTable.ensureFieldAccessorsInitialized(ColumnMetaData.class, Builder.class);
      }

      public boolean hasType() {
         return (this.bitField0_ & 1) != 0;
      }

      public FieldType getType() {
         FieldType result = MysqlxResultset.ColumnMetaData.FieldType.valueOf(this.type_);
         return result == null ? MysqlxResultset.ColumnMetaData.FieldType.SINT : result;
      }

      public boolean hasName() {
         return (this.bitField0_ & 2) != 0;
      }

      public ByteString getName() {
         return this.name_;
      }

      public boolean hasOriginalName() {
         return (this.bitField0_ & 4) != 0;
      }

      public ByteString getOriginalName() {
         return this.originalName_;
      }

      public boolean hasTable() {
         return (this.bitField0_ & 8) != 0;
      }

      public ByteString getTable() {
         return this.table_;
      }

      public boolean hasOriginalTable() {
         return (this.bitField0_ & 16) != 0;
      }

      public ByteString getOriginalTable() {
         return this.originalTable_;
      }

      public boolean hasSchema() {
         return (this.bitField0_ & 32) != 0;
      }

      public ByteString getSchema() {
         return this.schema_;
      }

      public boolean hasCatalog() {
         return (this.bitField0_ & 64) != 0;
      }

      public ByteString getCatalog() {
         return this.catalog_;
      }

      public boolean hasCollation() {
         return (this.bitField0_ & 128) != 0;
      }

      public long getCollation() {
         return this.collation_;
      }

      public boolean hasFractionalDigits() {
         return (this.bitField0_ & 256) != 0;
      }

      public int getFractionalDigits() {
         return this.fractionalDigits_;
      }

      public boolean hasLength() {
         return (this.bitField0_ & 512) != 0;
      }

      public int getLength() {
         return this.length_;
      }

      public boolean hasFlags() {
         return (this.bitField0_ & 1024) != 0;
      }

      public int getFlags() {
         return this.flags_;
      }

      public boolean hasContentType() {
         return (this.bitField0_ & 2048) != 0;
      }

      public int getContentType() {
         return this.contentType_;
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
            output.writeBytes(2, this.name_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeBytes(3, this.originalName_);
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeBytes(4, this.table_);
         }

         if ((this.bitField0_ & 16) != 0) {
            output.writeBytes(5, this.originalTable_);
         }

         if ((this.bitField0_ & 32) != 0) {
            output.writeBytes(6, this.schema_);
         }

         if ((this.bitField0_ & 64) != 0) {
            output.writeBytes(7, this.catalog_);
         }

         if ((this.bitField0_ & 128) != 0) {
            output.writeUInt64(8, this.collation_);
         }

         if ((this.bitField0_ & 256) != 0) {
            output.writeUInt32(9, this.fractionalDigits_);
         }

         if ((this.bitField0_ & 512) != 0) {
            output.writeUInt32(10, this.length_);
         }

         if ((this.bitField0_ & 1024) != 0) {
            output.writeUInt32(11, this.flags_);
         }

         if ((this.bitField0_ & 2048) != 0) {
            output.writeUInt32(12, this.contentType_);
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
               size += CodedOutputStream.computeBytesSize(2, this.name_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeBytesSize(3, this.originalName_);
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeBytesSize(4, this.table_);
            }

            if ((this.bitField0_ & 16) != 0) {
               size += CodedOutputStream.computeBytesSize(5, this.originalTable_);
            }

            if ((this.bitField0_ & 32) != 0) {
               size += CodedOutputStream.computeBytesSize(6, this.schema_);
            }

            if ((this.bitField0_ & 64) != 0) {
               size += CodedOutputStream.computeBytesSize(7, this.catalog_);
            }

            if ((this.bitField0_ & 128) != 0) {
               size += CodedOutputStream.computeUInt64Size(8, this.collation_);
            }

            if ((this.bitField0_ & 256) != 0) {
               size += CodedOutputStream.computeUInt32Size(9, this.fractionalDigits_);
            }

            if ((this.bitField0_ & 512) != 0) {
               size += CodedOutputStream.computeUInt32Size(10, this.length_);
            }

            if ((this.bitField0_ & 1024) != 0) {
               size += CodedOutputStream.computeUInt32Size(11, this.flags_);
            }

            if ((this.bitField0_ & 2048) != 0) {
               size += CodedOutputStream.computeUInt32Size(12, this.contentType_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof ColumnMetaData)) {
            return super.equals(obj);
         } else {
            ColumnMetaData other = (ColumnMetaData)obj;
            if (this.hasType() != other.hasType()) {
               return false;
            } else if (this.hasType() && this.type_ != other.type_) {
               return false;
            } else if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
               return false;
            } else if (this.hasOriginalName() != other.hasOriginalName()) {
               return false;
            } else if (this.hasOriginalName() && !this.getOriginalName().equals(other.getOriginalName())) {
               return false;
            } else if (this.hasTable() != other.hasTable()) {
               return false;
            } else if (this.hasTable() && !this.getTable().equals(other.getTable())) {
               return false;
            } else if (this.hasOriginalTable() != other.hasOriginalTable()) {
               return false;
            } else if (this.hasOriginalTable() && !this.getOriginalTable().equals(other.getOriginalTable())) {
               return false;
            } else if (this.hasSchema() != other.hasSchema()) {
               return false;
            } else if (this.hasSchema() && !this.getSchema().equals(other.getSchema())) {
               return false;
            } else if (this.hasCatalog() != other.hasCatalog()) {
               return false;
            } else if (this.hasCatalog() && !this.getCatalog().equals(other.getCatalog())) {
               return false;
            } else if (this.hasCollation() != other.hasCollation()) {
               return false;
            } else if (this.hasCollation() && this.getCollation() != other.getCollation()) {
               return false;
            } else if (this.hasFractionalDigits() != other.hasFractionalDigits()) {
               return false;
            } else if (this.hasFractionalDigits() && this.getFractionalDigits() != other.getFractionalDigits()) {
               return false;
            } else if (this.hasLength() != other.hasLength()) {
               return false;
            } else if (this.hasLength() && this.getLength() != other.getLength()) {
               return false;
            } else if (this.hasFlags() != other.hasFlags()) {
               return false;
            } else if (this.hasFlags() && this.getFlags() != other.getFlags()) {
               return false;
            } else if (this.hasContentType() != other.hasContentType()) {
               return false;
            } else if (this.hasContentType() && this.getContentType() != other.getContentType()) {
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

            if (this.hasName()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getName().hashCode();
            }

            if (this.hasOriginalName()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getOriginalName().hashCode();
            }

            if (this.hasTable()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getTable().hashCode();
            }

            if (this.hasOriginalTable()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.getOriginalTable().hashCode();
            }

            if (this.hasSchema()) {
               hash = 37 * hash + 6;
               hash = 53 * hash + this.getSchema().hashCode();
            }

            if (this.hasCatalog()) {
               hash = 37 * hash + 7;
               hash = 53 * hash + this.getCatalog().hashCode();
            }

            if (this.hasCollation()) {
               hash = 37 * hash + 8;
               hash = 53 * hash + Internal.hashLong(this.getCollation());
            }

            if (this.hasFractionalDigits()) {
               hash = 37 * hash + 9;
               hash = 53 * hash + this.getFractionalDigits();
            }

            if (this.hasLength()) {
               hash = 37 * hash + 10;
               hash = 53 * hash + this.getLength();
            }

            if (this.hasFlags()) {
               hash = 37 * hash + 11;
               hash = 53 * hash + this.getFlags();
            }

            if (this.hasContentType()) {
               hash = 37 * hash + 12;
               hash = 53 * hash + this.getContentType();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static ColumnMetaData parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (ColumnMetaData)PARSER.parseFrom(data);
      }

      public static ColumnMetaData parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (ColumnMetaData)PARSER.parseFrom(data, extensionRegistry);
      }

      public static ColumnMetaData parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (ColumnMetaData)PARSER.parseFrom(data);
      }

      public static ColumnMetaData parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (ColumnMetaData)PARSER.parseFrom(data, extensionRegistry);
      }

      public static ColumnMetaData parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (ColumnMetaData)PARSER.parseFrom(data);
      }

      public static ColumnMetaData parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (ColumnMetaData)PARSER.parseFrom(data, extensionRegistry);
      }

      public static ColumnMetaData parseFrom(InputStream input) throws IOException {
         return (ColumnMetaData)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static ColumnMetaData parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (ColumnMetaData)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static ColumnMetaData parseDelimitedFrom(InputStream input) throws IOException {
         return (ColumnMetaData)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static ColumnMetaData parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (ColumnMetaData)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static ColumnMetaData parseFrom(CodedInputStream input) throws IOException {
         return (ColumnMetaData)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static ColumnMetaData parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (ColumnMetaData)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(ColumnMetaData prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static ColumnMetaData getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<ColumnMetaData> parser() {
         return PARSER;
      }

      public Parser<ColumnMetaData> getParserForType() {
         return PARSER;
      }

      public ColumnMetaData getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      ColumnMetaData(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      ColumnMetaData(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ColumnMetaDataOrBuilder {
         private int bitField0_;
         private int type_;
         private ByteString name_;
         private ByteString originalName_;
         private ByteString table_;
         private ByteString originalTable_;
         private ByteString schema_;
         private ByteString catalog_;
         private long collation_;
         private int fractionalDigits_;
         private int length_;
         private int flags_;
         private int contentType_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_fieldAccessorTable.ensureFieldAccessorsInitialized(ColumnMetaData.class, Builder.class);
         }

         private Builder() {
            this.type_ = 1;
            this.name_ = ByteString.EMPTY;
            this.originalName_ = ByteString.EMPTY;
            this.table_ = ByteString.EMPTY;
            this.originalTable_ = ByteString.EMPTY;
            this.schema_ = ByteString.EMPTY;
            this.catalog_ = ByteString.EMPTY;
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.type_ = 1;
            this.name_ = ByteString.EMPTY;
            this.originalName_ = ByteString.EMPTY;
            this.table_ = ByteString.EMPTY;
            this.originalTable_ = ByteString.EMPTY;
            this.schema_ = ByteString.EMPTY;
            this.catalog_ = ByteString.EMPTY;
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxResultset.ColumnMetaData.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            this.type_ = 1;
            this.bitField0_ &= -2;
            this.name_ = ByteString.EMPTY;
            this.bitField0_ &= -3;
            this.originalName_ = ByteString.EMPTY;
            this.bitField0_ &= -5;
            this.table_ = ByteString.EMPTY;
            this.bitField0_ &= -9;
            this.originalTable_ = ByteString.EMPTY;
            this.bitField0_ &= -17;
            this.schema_ = ByteString.EMPTY;
            this.bitField0_ &= -33;
            this.catalog_ = ByteString.EMPTY;
            this.bitField0_ &= -65;
            this.collation_ = 0L;
            this.bitField0_ &= -129;
            this.fractionalDigits_ = 0;
            this.bitField0_ &= -257;
            this.length_ = 0;
            this.bitField0_ &= -513;
            this.flags_ = 0;
            this.bitField0_ &= -1025;
            this.contentType_ = 0;
            this.bitField0_ &= -2049;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor;
         }

         public ColumnMetaData getDefaultInstanceForType() {
            return MysqlxResultset.ColumnMetaData.getDefaultInstance();
         }

         public ColumnMetaData build() {
            ColumnMetaData result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public ColumnMetaData buildPartial() {
            ColumnMetaData result = new ColumnMetaData(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.type_ = this.type_;
            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.name_ = this.name_;
            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 4;
            }

            result.originalName_ = this.originalName_;
            if ((from_bitField0_ & 8) != 0) {
               to_bitField0_ |= 8;
            }

            result.table_ = this.table_;
            if ((from_bitField0_ & 16) != 0) {
               to_bitField0_ |= 16;
            }

            result.originalTable_ = this.originalTable_;
            if ((from_bitField0_ & 32) != 0) {
               to_bitField0_ |= 32;
            }

            result.schema_ = this.schema_;
            if ((from_bitField0_ & 64) != 0) {
               to_bitField0_ |= 64;
            }

            result.catalog_ = this.catalog_;
            if ((from_bitField0_ & 128) != 0) {
               result.collation_ = this.collation_;
               to_bitField0_ |= 128;
            }

            if ((from_bitField0_ & 256) != 0) {
               result.fractionalDigits_ = this.fractionalDigits_;
               to_bitField0_ |= 256;
            }

            if ((from_bitField0_ & 512) != 0) {
               result.length_ = this.length_;
               to_bitField0_ |= 512;
            }

            if ((from_bitField0_ & 1024) != 0) {
               result.flags_ = this.flags_;
               to_bitField0_ |= 1024;
            }

            if ((from_bitField0_ & 2048) != 0) {
               result.contentType_ = this.contentType_;
               to_bitField0_ |= 2048;
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
            if (other instanceof ColumnMetaData) {
               return this.mergeFrom((ColumnMetaData)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(ColumnMetaData other) {
            if (other == MysqlxResultset.ColumnMetaData.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasType()) {
                  this.setType(other.getType());
               }

               if (other.hasName()) {
                  this.setName(other.getName());
               }

               if (other.hasOriginalName()) {
                  this.setOriginalName(other.getOriginalName());
               }

               if (other.hasTable()) {
                  this.setTable(other.getTable());
               }

               if (other.hasOriginalTable()) {
                  this.setOriginalTable(other.getOriginalTable());
               }

               if (other.hasSchema()) {
                  this.setSchema(other.getSchema());
               }

               if (other.hasCatalog()) {
                  this.setCatalog(other.getCatalog());
               }

               if (other.hasCollation()) {
                  this.setCollation(other.getCollation());
               }

               if (other.hasFractionalDigits()) {
                  this.setFractionalDigits(other.getFractionalDigits());
               }

               if (other.hasLength()) {
                  this.setLength(other.getLength());
               }

               if (other.hasFlags()) {
                  this.setFlags(other.getFlags());
               }

               if (other.hasContentType()) {
                  this.setContentType(other.getContentType());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            return this.hasType();
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            ColumnMetaData parsedMessage = null;

            try {
               parsedMessage = (ColumnMetaData)MysqlxResultset.ColumnMetaData.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (ColumnMetaData)var8.getUnfinishedMessage();
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

         public FieldType getType() {
            FieldType result = MysqlxResultset.ColumnMetaData.FieldType.valueOf(this.type_);
            return result == null ? MysqlxResultset.ColumnMetaData.FieldType.SINT : result;
         }

         public Builder setType(FieldType value) {
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
            this.type_ = 1;
            this.onChanged();
            return this;
         }

         public boolean hasName() {
            return (this.bitField0_ & 2) != 0;
         }

         public ByteString getName() {
            return this.name_;
         }

         public Builder setName(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearName() {
            this.bitField0_ &= -3;
            this.name_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getName();
            this.onChanged();
            return this;
         }

         public boolean hasOriginalName() {
            return (this.bitField0_ & 4) != 0;
         }

         public ByteString getOriginalName() {
            return this.originalName_;
         }

         public Builder setOriginalName(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.originalName_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearOriginalName() {
            this.bitField0_ &= -5;
            this.originalName_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getOriginalName();
            this.onChanged();
            return this;
         }

         public boolean hasTable() {
            return (this.bitField0_ & 8) != 0;
         }

         public ByteString getTable() {
            return this.table_;
         }

         public Builder setTable(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.table_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearTable() {
            this.bitField0_ &= -9;
            this.table_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getTable();
            this.onChanged();
            return this;
         }

         public boolean hasOriginalTable() {
            return (this.bitField0_ & 16) != 0;
         }

         public ByteString getOriginalTable() {
            return this.originalTable_;
         }

         public Builder setOriginalTable(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 16;
               this.originalTable_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearOriginalTable() {
            this.bitField0_ &= -17;
            this.originalTable_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getOriginalTable();
            this.onChanged();
            return this;
         }

         public boolean hasSchema() {
            return (this.bitField0_ & 32) != 0;
         }

         public ByteString getSchema() {
            return this.schema_;
         }

         public Builder setSchema(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 32;
               this.schema_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearSchema() {
            this.bitField0_ &= -33;
            this.schema_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getSchema();
            this.onChanged();
            return this;
         }

         public boolean hasCatalog() {
            return (this.bitField0_ & 64) != 0;
         }

         public ByteString getCatalog() {
            return this.catalog_;
         }

         public Builder setCatalog(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 64;
               this.catalog_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearCatalog() {
            this.bitField0_ &= -65;
            this.catalog_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getCatalog();
            this.onChanged();
            return this;
         }

         public boolean hasCollation() {
            return (this.bitField0_ & 128) != 0;
         }

         public long getCollation() {
            return this.collation_;
         }

         public Builder setCollation(long value) {
            this.bitField0_ |= 128;
            this.collation_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearCollation() {
            this.bitField0_ &= -129;
            this.collation_ = 0L;
            this.onChanged();
            return this;
         }

         public boolean hasFractionalDigits() {
            return (this.bitField0_ & 256) != 0;
         }

         public int getFractionalDigits() {
            return this.fractionalDigits_;
         }

         public Builder setFractionalDigits(int value) {
            this.bitField0_ |= 256;
            this.fractionalDigits_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearFractionalDigits() {
            this.bitField0_ &= -257;
            this.fractionalDigits_ = 0;
            this.onChanged();
            return this;
         }

         public boolean hasLength() {
            return (this.bitField0_ & 512) != 0;
         }

         public int getLength() {
            return this.length_;
         }

         public Builder setLength(int value) {
            this.bitField0_ |= 512;
            this.length_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearLength() {
            this.bitField0_ &= -513;
            this.length_ = 0;
            this.onChanged();
            return this;
         }

         public boolean hasFlags() {
            return (this.bitField0_ & 1024) != 0;
         }

         public int getFlags() {
            return this.flags_;
         }

         public Builder setFlags(int value) {
            this.bitField0_ |= 1024;
            this.flags_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearFlags() {
            this.bitField0_ &= -1025;
            this.flags_ = 0;
            this.onChanged();
            return this;
         }

         public boolean hasContentType() {
            return (this.bitField0_ & 2048) != 0;
         }

         public int getContentType() {
            return this.contentType_;
         }

         public Builder setContentType(int value) {
            this.bitField0_ |= 2048;
            this.contentType_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearContentType() {
            this.bitField0_ &= -2049;
            this.contentType_ = 0;
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

      public static enum FieldType implements ProtocolMessageEnum {
         SINT(1),
         UINT(2),
         DOUBLE(5),
         FLOAT(6),
         BYTES(7),
         TIME(10),
         DATETIME(12),
         SET(15),
         ENUM(16),
         BIT(17),
         DECIMAL(18);

         public static final int SINT_VALUE = 1;
         public static final int UINT_VALUE = 2;
         public static final int DOUBLE_VALUE = 5;
         public static final int FLOAT_VALUE = 6;
         public static final int BYTES_VALUE = 7;
         public static final int TIME_VALUE = 10;
         public static final int DATETIME_VALUE = 12;
         public static final int SET_VALUE = 15;
         public static final int ENUM_VALUE = 16;
         public static final int BIT_VALUE = 17;
         public static final int DECIMAL_VALUE = 18;
         private static final Internal.EnumLiteMap<FieldType> internalValueMap = new Internal.EnumLiteMap<FieldType>() {
            public FieldType findValueByNumber(int number) {
               return MysqlxResultset.ColumnMetaData.FieldType.forNumber(number);
            }
         };
         private static final FieldType[] VALUES = values();
         private final int value;

         public final int getNumber() {
            return this.value;
         }

         /** @deprecated */
         @Deprecated
         public static FieldType valueOf(int value) {
            return forNumber(value);
         }

         public static FieldType forNumber(int value) {
            switch (value) {
               case 1:
                  return SINT;
               case 2:
                  return UINT;
               case 3:
               case 4:
               case 8:
               case 9:
               case 11:
               case 13:
               case 14:
               default:
                  return null;
               case 5:
                  return DOUBLE;
               case 6:
                  return FLOAT;
               case 7:
                  return BYTES;
               case 10:
                  return TIME;
               case 12:
                  return DATETIME;
               case 15:
                  return SET;
               case 16:
                  return ENUM;
               case 17:
                  return BIT;
               case 18:
                  return DECIMAL;
            }
         }

         public static Internal.EnumLiteMap<FieldType> internalGetValueMap() {
            return internalValueMap;
         }

         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
         }

         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static final Descriptors.EnumDescriptor getDescriptor() {
            return (Descriptors.EnumDescriptor)MysqlxResultset.ColumnMetaData.getDescriptor().getEnumTypes().get(0);
         }

         public static FieldType valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private FieldType(int value) {
            this.value = value;
         }
      }
   }

   public interface ColumnMetaDataOrBuilder extends MessageOrBuilder {
      boolean hasType();

      ColumnMetaData.FieldType getType();

      boolean hasName();

      ByteString getName();

      boolean hasOriginalName();

      ByteString getOriginalName();

      boolean hasTable();

      ByteString getTable();

      boolean hasOriginalTable();

      ByteString getOriginalTable();

      boolean hasSchema();

      ByteString getSchema();

      boolean hasCatalog();

      ByteString getCatalog();

      boolean hasCollation();

      long getCollation();

      boolean hasFractionalDigits();

      int getFractionalDigits();

      boolean hasLength();

      int getLength();

      boolean hasFlags();

      int getFlags();

      boolean hasContentType();

      int getContentType();
   }

   public static final class FetchSuspended extends GeneratedMessageV3 implements FetchSuspendedOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized;
      private static final FetchSuspended DEFAULT_INSTANCE = new FetchSuspended();
      /** @deprecated */
      @Deprecated
      public static final Parser<FetchSuspended> PARSER = new AbstractParser<FetchSuspended>() {
         public FetchSuspended parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new FetchSuspended(input, extensionRegistry);
         }
      };

      private FetchSuspended(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private FetchSuspended() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new FetchSuspended();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private FetchSuspended(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchSuspended.class, Builder.class);
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
         } else if (!(obj instanceof FetchSuspended)) {
            return super.equals(obj);
         } else {
            FetchSuspended other = (FetchSuspended)obj;
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

      public static FetchSuspended parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (FetchSuspended)PARSER.parseFrom(data);
      }

      public static FetchSuspended parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (FetchSuspended)PARSER.parseFrom(data, extensionRegistry);
      }

      public static FetchSuspended parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (FetchSuspended)PARSER.parseFrom(data);
      }

      public static FetchSuspended parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (FetchSuspended)PARSER.parseFrom(data, extensionRegistry);
      }

      public static FetchSuspended parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (FetchSuspended)PARSER.parseFrom(data);
      }

      public static FetchSuspended parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (FetchSuspended)PARSER.parseFrom(data, extensionRegistry);
      }

      public static FetchSuspended parseFrom(InputStream input) throws IOException {
         return (FetchSuspended)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static FetchSuspended parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (FetchSuspended)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static FetchSuspended parseDelimitedFrom(InputStream input) throws IOException {
         return (FetchSuspended)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static FetchSuspended parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (FetchSuspended)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static FetchSuspended parseFrom(CodedInputStream input) throws IOException {
         return (FetchSuspended)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static FetchSuspended parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (FetchSuspended)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(FetchSuspended prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static FetchSuspended getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<FetchSuspended> parser() {
         return PARSER;
      }

      public Parser<FetchSuspended> getParserForType() {
         return PARSER;
      }

      public FetchSuspended getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      FetchSuspended(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      FetchSuspended(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements FetchSuspendedOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchSuspended.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxResultset.FetchSuspended.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_descriptor;
         }

         public FetchSuspended getDefaultInstanceForType() {
            return MysqlxResultset.FetchSuspended.getDefaultInstance();
         }

         public FetchSuspended build() {
            FetchSuspended result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public FetchSuspended buildPartial() {
            FetchSuspended result = new FetchSuspended(this);
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
            if (other instanceof FetchSuspended) {
               return this.mergeFrom((FetchSuspended)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(FetchSuspended other) {
            if (other == MysqlxResultset.FetchSuspended.getDefaultInstance()) {
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
            FetchSuspended parsedMessage = null;

            try {
               parsedMessage = (FetchSuspended)MysqlxResultset.FetchSuspended.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (FetchSuspended)var8.getUnfinishedMessage();
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

   public interface FetchSuspendedOrBuilder extends MessageOrBuilder {
   }

   public static final class FetchDone extends GeneratedMessageV3 implements FetchDoneOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized;
      private static final FetchDone DEFAULT_INSTANCE = new FetchDone();
      /** @deprecated */
      @Deprecated
      public static final Parser<FetchDone> PARSER = new AbstractParser<FetchDone>() {
         public FetchDone parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new FetchDone(input, extensionRegistry);
         }
      };

      private FetchDone(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private FetchDone() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new FetchDone();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private FetchDone(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchDone.class, Builder.class);
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
         } else if (!(obj instanceof FetchDone)) {
            return super.equals(obj);
         } else {
            FetchDone other = (FetchDone)obj;
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

      public static FetchDone parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (FetchDone)PARSER.parseFrom(data);
      }

      public static FetchDone parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (FetchDone)PARSER.parseFrom(data, extensionRegistry);
      }

      public static FetchDone parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (FetchDone)PARSER.parseFrom(data);
      }

      public static FetchDone parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (FetchDone)PARSER.parseFrom(data, extensionRegistry);
      }

      public static FetchDone parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (FetchDone)PARSER.parseFrom(data);
      }

      public static FetchDone parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (FetchDone)PARSER.parseFrom(data, extensionRegistry);
      }

      public static FetchDone parseFrom(InputStream input) throws IOException {
         return (FetchDone)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static FetchDone parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (FetchDone)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static FetchDone parseDelimitedFrom(InputStream input) throws IOException {
         return (FetchDone)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static FetchDone parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (FetchDone)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static FetchDone parseFrom(CodedInputStream input) throws IOException {
         return (FetchDone)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static FetchDone parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (FetchDone)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(FetchDone prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static FetchDone getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<FetchDone> parser() {
         return PARSER;
      }

      public Parser<FetchDone> getParserForType() {
         return PARSER;
      }

      public FetchDone getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      FetchDone(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      FetchDone(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements FetchDoneOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchDone.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxResultset.FetchDone.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_descriptor;
         }

         public FetchDone getDefaultInstanceForType() {
            return MysqlxResultset.FetchDone.getDefaultInstance();
         }

         public FetchDone build() {
            FetchDone result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public FetchDone buildPartial() {
            FetchDone result = new FetchDone(this);
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
            if (other instanceof FetchDone) {
               return this.mergeFrom((FetchDone)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(FetchDone other) {
            if (other == MysqlxResultset.FetchDone.getDefaultInstance()) {
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
            FetchDone parsedMessage = null;

            try {
               parsedMessage = (FetchDone)MysqlxResultset.FetchDone.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (FetchDone)var8.getUnfinishedMessage();
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

   public interface FetchDoneOrBuilder extends MessageOrBuilder {
   }

   public static final class FetchDoneMoreResultsets extends GeneratedMessageV3 implements FetchDoneMoreResultsetsOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized;
      private static final FetchDoneMoreResultsets DEFAULT_INSTANCE = new FetchDoneMoreResultsets();
      /** @deprecated */
      @Deprecated
      public static final Parser<FetchDoneMoreResultsets> PARSER = new AbstractParser<FetchDoneMoreResultsets>() {
         public FetchDoneMoreResultsets parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new FetchDoneMoreResultsets(input, extensionRegistry);
         }
      };

      private FetchDoneMoreResultsets(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private FetchDoneMoreResultsets() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new FetchDoneMoreResultsets();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private FetchDoneMoreResultsets(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchDoneMoreResultsets.class, Builder.class);
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
         } else if (!(obj instanceof FetchDoneMoreResultsets)) {
            return super.equals(obj);
         } else {
            FetchDoneMoreResultsets other = (FetchDoneMoreResultsets)obj;
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

      public static FetchDoneMoreResultsets parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (FetchDoneMoreResultsets)PARSER.parseFrom(data);
      }

      public static FetchDoneMoreResultsets parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (FetchDoneMoreResultsets)PARSER.parseFrom(data, extensionRegistry);
      }

      public static FetchDoneMoreResultsets parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (FetchDoneMoreResultsets)PARSER.parseFrom(data);
      }

      public static FetchDoneMoreResultsets parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (FetchDoneMoreResultsets)PARSER.parseFrom(data, extensionRegistry);
      }

      public static FetchDoneMoreResultsets parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (FetchDoneMoreResultsets)PARSER.parseFrom(data);
      }

      public static FetchDoneMoreResultsets parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (FetchDoneMoreResultsets)PARSER.parseFrom(data, extensionRegistry);
      }

      public static FetchDoneMoreResultsets parseFrom(InputStream input) throws IOException {
         return (FetchDoneMoreResultsets)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static FetchDoneMoreResultsets parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (FetchDoneMoreResultsets)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static FetchDoneMoreResultsets parseDelimitedFrom(InputStream input) throws IOException {
         return (FetchDoneMoreResultsets)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static FetchDoneMoreResultsets parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (FetchDoneMoreResultsets)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static FetchDoneMoreResultsets parseFrom(CodedInputStream input) throws IOException {
         return (FetchDoneMoreResultsets)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static FetchDoneMoreResultsets parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (FetchDoneMoreResultsets)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(FetchDoneMoreResultsets prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static FetchDoneMoreResultsets getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<FetchDoneMoreResultsets> parser() {
         return PARSER;
      }

      public Parser<FetchDoneMoreResultsets> getParserForType() {
         return PARSER;
      }

      public FetchDoneMoreResultsets getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      FetchDoneMoreResultsets(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      FetchDoneMoreResultsets(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements FetchDoneMoreResultsetsOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchDoneMoreResultsets.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxResultset.FetchDoneMoreResultsets.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor;
         }

         public FetchDoneMoreResultsets getDefaultInstanceForType() {
            return MysqlxResultset.FetchDoneMoreResultsets.getDefaultInstance();
         }

         public FetchDoneMoreResultsets build() {
            FetchDoneMoreResultsets result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public FetchDoneMoreResultsets buildPartial() {
            FetchDoneMoreResultsets result = new FetchDoneMoreResultsets(this);
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
            if (other instanceof FetchDoneMoreResultsets) {
               return this.mergeFrom((FetchDoneMoreResultsets)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(FetchDoneMoreResultsets other) {
            if (other == MysqlxResultset.FetchDoneMoreResultsets.getDefaultInstance()) {
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
            FetchDoneMoreResultsets parsedMessage = null;

            try {
               parsedMessage = (FetchDoneMoreResultsets)MysqlxResultset.FetchDoneMoreResultsets.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (FetchDoneMoreResultsets)var8.getUnfinishedMessage();
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

   public interface FetchDoneMoreResultsetsOrBuilder extends MessageOrBuilder {
   }

   public static final class FetchDoneMoreOutParams extends GeneratedMessageV3 implements FetchDoneMoreOutParamsOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized;
      private static final FetchDoneMoreOutParams DEFAULT_INSTANCE = new FetchDoneMoreOutParams();
      /** @deprecated */
      @Deprecated
      public static final Parser<FetchDoneMoreOutParams> PARSER = new AbstractParser<FetchDoneMoreOutParams>() {
         public FetchDoneMoreOutParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new FetchDoneMoreOutParams(input, extensionRegistry);
         }
      };

      private FetchDoneMoreOutParams(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private FetchDoneMoreOutParams() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new FetchDoneMoreOutParams();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private FetchDoneMoreOutParams(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchDoneMoreOutParams.class, Builder.class);
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
         } else if (!(obj instanceof FetchDoneMoreOutParams)) {
            return super.equals(obj);
         } else {
            FetchDoneMoreOutParams other = (FetchDoneMoreOutParams)obj;
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

      public static FetchDoneMoreOutParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (FetchDoneMoreOutParams)PARSER.parseFrom(data);
      }

      public static FetchDoneMoreOutParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (FetchDoneMoreOutParams)PARSER.parseFrom(data, extensionRegistry);
      }

      public static FetchDoneMoreOutParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (FetchDoneMoreOutParams)PARSER.parseFrom(data);
      }

      public static FetchDoneMoreOutParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (FetchDoneMoreOutParams)PARSER.parseFrom(data, extensionRegistry);
      }

      public static FetchDoneMoreOutParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (FetchDoneMoreOutParams)PARSER.parseFrom(data);
      }

      public static FetchDoneMoreOutParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (FetchDoneMoreOutParams)PARSER.parseFrom(data, extensionRegistry);
      }

      public static FetchDoneMoreOutParams parseFrom(InputStream input) throws IOException {
         return (FetchDoneMoreOutParams)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static FetchDoneMoreOutParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (FetchDoneMoreOutParams)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static FetchDoneMoreOutParams parseDelimitedFrom(InputStream input) throws IOException {
         return (FetchDoneMoreOutParams)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static FetchDoneMoreOutParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (FetchDoneMoreOutParams)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static FetchDoneMoreOutParams parseFrom(CodedInputStream input) throws IOException {
         return (FetchDoneMoreOutParams)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static FetchDoneMoreOutParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (FetchDoneMoreOutParams)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(FetchDoneMoreOutParams prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static FetchDoneMoreOutParams getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<FetchDoneMoreOutParams> parser() {
         return PARSER;
      }

      public Parser<FetchDoneMoreOutParams> getParserForType() {
         return PARSER;
      }

      public FetchDoneMoreOutParams getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      FetchDoneMoreOutParams(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      FetchDoneMoreOutParams(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements FetchDoneMoreOutParamsOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchDoneMoreOutParams.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxResultset.FetchDoneMoreOutParams.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor;
         }

         public FetchDoneMoreOutParams getDefaultInstanceForType() {
            return MysqlxResultset.FetchDoneMoreOutParams.getDefaultInstance();
         }

         public FetchDoneMoreOutParams build() {
            FetchDoneMoreOutParams result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public FetchDoneMoreOutParams buildPartial() {
            FetchDoneMoreOutParams result = new FetchDoneMoreOutParams(this);
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
            if (other instanceof FetchDoneMoreOutParams) {
               return this.mergeFrom((FetchDoneMoreOutParams)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(FetchDoneMoreOutParams other) {
            if (other == MysqlxResultset.FetchDoneMoreOutParams.getDefaultInstance()) {
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
            FetchDoneMoreOutParams parsedMessage = null;

            try {
               parsedMessage = (FetchDoneMoreOutParams)MysqlxResultset.FetchDoneMoreOutParams.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (FetchDoneMoreOutParams)var8.getUnfinishedMessage();
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

   public interface FetchDoneMoreOutParamsOrBuilder extends MessageOrBuilder {
   }

   public static enum ContentType_DATETIME implements ProtocolMessageEnum {
      DATE(1),
      DATETIME(2);

      public static final int DATE_VALUE = 1;
      public static final int DATETIME_VALUE = 2;
      private static final Internal.EnumLiteMap<ContentType_DATETIME> internalValueMap = new Internal.EnumLiteMap<ContentType_DATETIME>() {
         public ContentType_DATETIME findValueByNumber(int number) {
            return MysqlxResultset.ContentType_DATETIME.forNumber(number);
         }
      };
      private static final ContentType_DATETIME[] VALUES = values();
      private final int value;

      public final int getNumber() {
         return this.value;
      }

      /** @deprecated */
      @Deprecated
      public static ContentType_DATETIME valueOf(int value) {
         return forNumber(value);
      }

      public static ContentType_DATETIME forNumber(int value) {
         switch (value) {
            case 1:
               return DATE;
            case 2:
               return DATETIME;
            default:
               return null;
         }
      }

      public static Internal.EnumLiteMap<ContentType_DATETIME> internalGetValueMap() {
         return internalValueMap;
      }

      public final Descriptors.EnumValueDescriptor getValueDescriptor() {
         return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
      }

      public final Descriptors.EnumDescriptor getDescriptorForType() {
         return getDescriptor();
      }

      public static final Descriptors.EnumDescriptor getDescriptor() {
         return (Descriptors.EnumDescriptor)MysqlxResultset.getDescriptor().getEnumTypes().get(1);
      }

      public static ContentType_DATETIME valueOf(Descriptors.EnumValueDescriptor desc) {
         if (desc.getType() != getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
         } else {
            return VALUES[desc.getIndex()];
         }
      }

      private ContentType_DATETIME(int value) {
         this.value = value;
      }
   }

   public static enum ContentType_BYTES implements ProtocolMessageEnum {
      GEOMETRY(1),
      JSON(2),
      XML(3);

      public static final int GEOMETRY_VALUE = 1;
      public static final int JSON_VALUE = 2;
      public static final int XML_VALUE = 3;
      private static final Internal.EnumLiteMap<ContentType_BYTES> internalValueMap = new Internal.EnumLiteMap<ContentType_BYTES>() {
         public ContentType_BYTES findValueByNumber(int number) {
            return MysqlxResultset.ContentType_BYTES.forNumber(number);
         }
      };
      private static final ContentType_BYTES[] VALUES = values();
      private final int value;

      public final int getNumber() {
         return this.value;
      }

      /** @deprecated */
      @Deprecated
      public static ContentType_BYTES valueOf(int value) {
         return forNumber(value);
      }

      public static ContentType_BYTES forNumber(int value) {
         switch (value) {
            case 1:
               return GEOMETRY;
            case 2:
               return JSON;
            case 3:
               return XML;
            default:
               return null;
         }
      }

      public static Internal.EnumLiteMap<ContentType_BYTES> internalGetValueMap() {
         return internalValueMap;
      }

      public final Descriptors.EnumValueDescriptor getValueDescriptor() {
         return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
      }

      public final Descriptors.EnumDescriptor getDescriptorForType() {
         return getDescriptor();
      }

      public static final Descriptors.EnumDescriptor getDescriptor() {
         return (Descriptors.EnumDescriptor)MysqlxResultset.getDescriptor().getEnumTypes().get(0);
      }

      public static ContentType_BYTES valueOf(Descriptors.EnumValueDescriptor desc) {
         if (desc.getType() != getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
         } else {
            return VALUES[desc.getIndex()];
         }
      }

      private ContentType_BYTES(int value) {
         this.value = value;
      }
   }
}
