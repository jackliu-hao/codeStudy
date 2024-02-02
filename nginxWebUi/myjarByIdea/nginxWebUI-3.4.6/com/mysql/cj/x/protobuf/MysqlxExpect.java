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
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RepeatedFieldBuilderV3;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxExpect {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expect_Open_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expect_Open_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expect_Open_Condition_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expect_Open_Condition_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expect_Close_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expect_Close_fieldAccessorTable;
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxExpect() {
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
      String[] descriptorData = new String[]{"\n\u0013mysqlx_expect.proto\u0012\rMysqlx.Expect\u001a\fmysqlx.proto\"Ö\u0003\n\u0004Open\u0012B\n\u0002op\u0018\u0001 \u0001(\u000e2 .Mysqlx.Expect.Open.CtxOperation:\u0014EXPECT_CTX_COPY_PREV\u0012+\n\u0004cond\u0018\u0002 \u0003(\u000b2\u001d.Mysqlx.Expect.Open.Condition\u001a\u0096\u0002\n\tCondition\u0012\u0015\n\rcondition_key\u0018\u0001 \u0002(\r\u0012\u0017\n\u000fcondition_value\u0018\u0002 \u0001(\f\u0012K\n\u0002op\u0018\u0003 \u0001(\u000e20.Mysqlx.Expect.Open.Condition.ConditionOperation:\rEXPECT_OP_SET\"N\n\u0003Key\u0012\u0013\n\u000fEXPECT_NO_ERROR\u0010\u0001\u0012\u0016\n\u0012EXPECT_FIELD_EXIST\u0010\u0002\u0012\u001a\n\u0016EXPECT_DOCID_GENERATED\u0010\u0003\"<\n\u0012ConditionOperation\u0012\u0011\n\rEXPECT_OP_SET\u0010\u0000\u0012\u0013\n\u000fEXPECT_OP_UNSET\u0010\u0001\">\n\fCtxOperation\u0012\u0018\n\u0014EXPECT_CTX_COPY_PREV\u0010\u0000\u0012\u0014\n\u0010EXPECT_CTX_EMPTY\u0010\u0001:\u0004\u0088ê0\u0018\"\r\n\u0005Close:\u0004\u0088ê0\u0019B\u0019\n\u0017com.mysql.cj.x.protobuf"};
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Mysqlx.getDescriptor()});
      internal_static_Mysqlx_Expect_Open_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(0);
      internal_static_Mysqlx_Expect_Open_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expect_Open_descriptor, new String[]{"Op", "Cond"});
      internal_static_Mysqlx_Expect_Open_Condition_descriptor = (Descriptors.Descriptor)internal_static_Mysqlx_Expect_Open_descriptor.getNestedTypes().get(0);
      internal_static_Mysqlx_Expect_Open_Condition_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expect_Open_Condition_descriptor, new String[]{"ConditionKey", "ConditionValue", "Op"});
      internal_static_Mysqlx_Expect_Close_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(1);
      internal_static_Mysqlx_Expect_Close_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expect_Close_descriptor, new String[0]);
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.clientMessageId);
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
         return MysqlxExpect.internal_static_Mysqlx_Expect_Close_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpect.internal_static_Mysqlx_Expect_Close_fieldAccessorTable.ensureFieldAccessorsInitialized(Close.class, Builder.class);
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
            return MysqlxExpect.internal_static_Mysqlx_Expect_Close_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpect.internal_static_Mysqlx_Expect_Close_fieldAccessorTable.ensureFieldAccessorsInitialized(Close.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpect.Close.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpect.internal_static_Mysqlx_Expect_Close_descriptor;
         }

         public Close getDefaultInstanceForType() {
            return MysqlxExpect.Close.getDefaultInstance();
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
            if (other == MysqlxExpect.Close.getDefaultInstance()) {
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
               parsedMessage = (Close)MysqlxExpect.Close.PARSER.parsePartialFrom(input, extensionRegistry);
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

   public static final class Open extends GeneratedMessageV3 implements OpenOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int OP_FIELD_NUMBER = 1;
      private int op_;
      public static final int COND_FIELD_NUMBER = 2;
      private List<Condition> cond_;
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
         this.op_ = 0;
         this.cond_ = Collections.emptyList();
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
                     case 8:
                        int rawValue = input.readEnum();
                        CtxOperation value = MysqlxExpect.Open.CtxOperation.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(1, rawValue);
                        } else {
                           this.bitField0_ |= 1;
                           this.op_ = rawValue;
                        }
                        break;
                     case 18:
                        if ((mutable_bitField0_ & 2) == 0) {
                           this.cond_ = new ArrayList();
                           mutable_bitField0_ |= 2;
                        }

                        this.cond_.add(input.readMessage(MysqlxExpect.Open.Condition.PARSER, extensionRegistry));
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
               if ((mutable_bitField0_ & 2) != 0) {
                  this.cond_ = Collections.unmodifiableList(this.cond_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpect.internal_static_Mysqlx_Expect_Open_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpect.internal_static_Mysqlx_Expect_Open_fieldAccessorTable.ensureFieldAccessorsInitialized(Open.class, Builder.class);
      }

      public boolean hasOp() {
         return (this.bitField0_ & 1) != 0;
      }

      public CtxOperation getOp() {
         CtxOperation result = MysqlxExpect.Open.CtxOperation.valueOf(this.op_);
         return result == null ? MysqlxExpect.Open.CtxOperation.EXPECT_CTX_COPY_PREV : result;
      }

      public List<Condition> getCondList() {
         return this.cond_;
      }

      public List<? extends ConditionOrBuilder> getCondOrBuilderList() {
         return this.cond_;
      }

      public int getCondCount() {
         return this.cond_.size();
      }

      public Condition getCond(int index) {
         return (Condition)this.cond_.get(index);
      }

      public ConditionOrBuilder getCondOrBuilder(int index) {
         return (ConditionOrBuilder)this.cond_.get(index);
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            for(int i = 0; i < this.getCondCount(); ++i) {
               if (!this.getCond(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeEnum(1, this.op_);
         }

         for(int i = 0; i < this.cond_.size(); ++i) {
            output.writeMessage(2, (MessageLite)this.cond_.get(i));
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
               size += CodedOutputStream.computeEnumSize(1, this.op_);
            }

            for(int i = 0; i < this.cond_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(2, (MessageLite)this.cond_.get(i));
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
            if (this.hasOp() != other.hasOp()) {
               return false;
            } else if (this.hasOp() && this.op_ != other.op_) {
               return false;
            } else if (!this.getCondList().equals(other.getCondList())) {
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
            if (this.hasOp()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.op_;
            }

            if (this.getCondCount() > 0) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getCondList().hashCode();
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
         private int op_;
         private List<Condition> cond_;
         private RepeatedFieldBuilderV3<Condition, Condition.Builder, ConditionOrBuilder> condBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpect.internal_static_Mysqlx_Expect_Open_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpect.internal_static_Mysqlx_Expect_Open_fieldAccessorTable.ensureFieldAccessorsInitialized(Open.class, Builder.class);
         }

         private Builder() {
            this.op_ = 0;
            this.cond_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.op_ = 0;
            this.cond_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpect.Open.alwaysUseFieldBuilders) {
               this.getCondFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            this.op_ = 0;
            this.bitField0_ &= -2;
            if (this.condBuilder_ == null) {
               this.cond_ = Collections.emptyList();
               this.bitField0_ &= -3;
            } else {
               this.condBuilder_.clear();
            }

            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpect.internal_static_Mysqlx_Expect_Open_descriptor;
         }

         public Open getDefaultInstanceForType() {
            return MysqlxExpect.Open.getDefaultInstance();
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
               to_bitField0_ |= 1;
            }

            result.op_ = this.op_;
            if (this.condBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0) {
                  this.cond_ = Collections.unmodifiableList(this.cond_);
                  this.bitField0_ &= -3;
               }

               result.cond_ = this.cond_;
            } else {
               result.cond_ = this.condBuilder_.build();
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
            if (other == MysqlxExpect.Open.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasOp()) {
                  this.setOp(other.getOp());
               }

               if (this.condBuilder_ == null) {
                  if (!other.cond_.isEmpty()) {
                     if (this.cond_.isEmpty()) {
                        this.cond_ = other.cond_;
                        this.bitField0_ &= -3;
                     } else {
                        this.ensureCondIsMutable();
                        this.cond_.addAll(other.cond_);
                     }

                     this.onChanged();
                  }
               } else if (!other.cond_.isEmpty()) {
                  if (this.condBuilder_.isEmpty()) {
                     this.condBuilder_.dispose();
                     this.condBuilder_ = null;
                     this.cond_ = other.cond_;
                     this.bitField0_ &= -3;
                     this.condBuilder_ = MysqlxExpect.Open.alwaysUseFieldBuilders ? this.getCondFieldBuilder() : null;
                  } else {
                     this.condBuilder_.addAllMessages(other.cond_);
                  }
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            for(int i = 0; i < this.getCondCount(); ++i) {
               if (!this.getCond(i).isInitialized()) {
                  return false;
               }
            }

            return true;
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Open parsedMessage = null;

            try {
               parsedMessage = (Open)MysqlxExpect.Open.PARSER.parsePartialFrom(input, extensionRegistry);
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

         public boolean hasOp() {
            return (this.bitField0_ & 1) != 0;
         }

         public CtxOperation getOp() {
            CtxOperation result = MysqlxExpect.Open.CtxOperation.valueOf(this.op_);
            return result == null ? MysqlxExpect.Open.CtxOperation.EXPECT_CTX_COPY_PREV : result;
         }

         public Builder setOp(CtxOperation value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.op_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearOp() {
            this.bitField0_ &= -2;
            this.op_ = 0;
            this.onChanged();
            return this;
         }

         private void ensureCondIsMutable() {
            if ((this.bitField0_ & 2) == 0) {
               this.cond_ = new ArrayList(this.cond_);
               this.bitField0_ |= 2;
            }

         }

         public List<Condition> getCondList() {
            return this.condBuilder_ == null ? Collections.unmodifiableList(this.cond_) : this.condBuilder_.getMessageList();
         }

         public int getCondCount() {
            return this.condBuilder_ == null ? this.cond_.size() : this.condBuilder_.getCount();
         }

         public Condition getCond(int index) {
            return this.condBuilder_ == null ? (Condition)this.cond_.get(index) : (Condition)this.condBuilder_.getMessage(index);
         }

         public Builder setCond(int index, Condition value) {
            if (this.condBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureCondIsMutable();
               this.cond_.set(index, value);
               this.onChanged();
            } else {
               this.condBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setCond(int index, Condition.Builder builderForValue) {
            if (this.condBuilder_ == null) {
               this.ensureCondIsMutable();
               this.cond_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.condBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addCond(Condition value) {
            if (this.condBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureCondIsMutable();
               this.cond_.add(value);
               this.onChanged();
            } else {
               this.condBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addCond(int index, Condition value) {
            if (this.condBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureCondIsMutable();
               this.cond_.add(index, value);
               this.onChanged();
            } else {
               this.condBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addCond(Condition.Builder builderForValue) {
            if (this.condBuilder_ == null) {
               this.ensureCondIsMutable();
               this.cond_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.condBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addCond(int index, Condition.Builder builderForValue) {
            if (this.condBuilder_ == null) {
               this.ensureCondIsMutable();
               this.cond_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.condBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllCond(Iterable<? extends Condition> values) {
            if (this.condBuilder_ == null) {
               this.ensureCondIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.cond_);
               this.onChanged();
            } else {
               this.condBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearCond() {
            if (this.condBuilder_ == null) {
               this.cond_ = Collections.emptyList();
               this.bitField0_ &= -3;
               this.onChanged();
            } else {
               this.condBuilder_.clear();
            }

            return this;
         }

         public Builder removeCond(int index) {
            if (this.condBuilder_ == null) {
               this.ensureCondIsMutable();
               this.cond_.remove(index);
               this.onChanged();
            } else {
               this.condBuilder_.remove(index);
            }

            return this;
         }

         public Condition.Builder getCondBuilder(int index) {
            return (Condition.Builder)this.getCondFieldBuilder().getBuilder(index);
         }

         public ConditionOrBuilder getCondOrBuilder(int index) {
            return this.condBuilder_ == null ? (ConditionOrBuilder)this.cond_.get(index) : (ConditionOrBuilder)this.condBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends ConditionOrBuilder> getCondOrBuilderList() {
            return this.condBuilder_ != null ? this.condBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.cond_);
         }

         public Condition.Builder addCondBuilder() {
            return (Condition.Builder)this.getCondFieldBuilder().addBuilder(MysqlxExpect.Open.Condition.getDefaultInstance());
         }

         public Condition.Builder addCondBuilder(int index) {
            return (Condition.Builder)this.getCondFieldBuilder().addBuilder(index, MysqlxExpect.Open.Condition.getDefaultInstance());
         }

         public List<Condition.Builder> getCondBuilderList() {
            return this.getCondFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<Condition, Condition.Builder, ConditionOrBuilder> getCondFieldBuilder() {
            if (this.condBuilder_ == null) {
               this.condBuilder_ = new RepeatedFieldBuilderV3(this.cond_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
               this.cond_ = null;
            }

            return this.condBuilder_;
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

      public static final class Condition extends GeneratedMessageV3 implements ConditionOrBuilder {
         private static final long serialVersionUID = 0L;
         private int bitField0_;
         public static final int CONDITION_KEY_FIELD_NUMBER = 1;
         private int conditionKey_;
         public static final int CONDITION_VALUE_FIELD_NUMBER = 2;
         private ByteString conditionValue_;
         public static final int OP_FIELD_NUMBER = 3;
         private int op_;
         private byte memoizedIsInitialized;
         private static final Condition DEFAULT_INSTANCE = new Condition();
         /** @deprecated */
         @Deprecated
         public static final Parser<Condition> PARSER = new AbstractParser<Condition>() {
            public Condition parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
               return new Condition(input, extensionRegistry);
            }
         };

         private Condition(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
         }

         private Condition() {
            this.memoizedIsInitialized = -1;
            this.conditionValue_ = ByteString.EMPTY;
            this.op_ = 0;
         }

         protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new Condition();
         }

         public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
         }

         private Condition(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                           this.conditionKey_ = input.readUInt32();
                           break;
                        case 18:
                           this.bitField0_ |= 2;
                           this.conditionValue_ = input.readBytes();
                           break;
                        case 24:
                           int rawValue = input.readEnum();
                           ConditionOperation value = MysqlxExpect.Open.Condition.ConditionOperation.valueOf(rawValue);
                           if (value == null) {
                              unknownFields.mergeVarintField(3, rawValue);
                           } else {
                              this.bitField0_ |= 4;
                              this.op_ = rawValue;
                           }
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
            return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_fieldAccessorTable.ensureFieldAccessorsInitialized(Condition.class, Builder.class);
         }

         public boolean hasConditionKey() {
            return (this.bitField0_ & 1) != 0;
         }

         public int getConditionKey() {
            return this.conditionKey_;
         }

         public boolean hasConditionValue() {
            return (this.bitField0_ & 2) != 0;
         }

         public ByteString getConditionValue() {
            return this.conditionValue_;
         }

         public boolean hasOp() {
            return (this.bitField0_ & 4) != 0;
         }

         public ConditionOperation getOp() {
            ConditionOperation result = MysqlxExpect.Open.Condition.ConditionOperation.valueOf(this.op_);
            return result == null ? MysqlxExpect.Open.Condition.ConditionOperation.EXPECT_OP_SET : result;
         }

         public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
               return true;
            } else if (isInitialized == 0) {
               return false;
            } else if (!this.hasConditionKey()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else {
               this.memoizedIsInitialized = 1;
               return true;
            }
         }

         public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
               output.writeUInt32(1, this.conditionKey_);
            }

            if ((this.bitField0_ & 2) != 0) {
               output.writeBytes(2, this.conditionValue_);
            }

            if ((this.bitField0_ & 4) != 0) {
               output.writeEnum(3, this.op_);
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
                  size += CodedOutputStream.computeUInt32Size(1, this.conditionKey_);
               }

               if ((this.bitField0_ & 2) != 0) {
                  size += CodedOutputStream.computeBytesSize(2, this.conditionValue_);
               }

               if ((this.bitField0_ & 4) != 0) {
                  size += CodedOutputStream.computeEnumSize(3, this.op_);
               }

               size += this.unknownFields.getSerializedSize();
               this.memoizedSize = size;
               return size;
            }
         }

         public boolean equals(Object obj) {
            if (obj == this) {
               return true;
            } else if (!(obj instanceof Condition)) {
               return super.equals(obj);
            } else {
               Condition other = (Condition)obj;
               if (this.hasConditionKey() != other.hasConditionKey()) {
                  return false;
               } else if (this.hasConditionKey() && this.getConditionKey() != other.getConditionKey()) {
                  return false;
               } else if (this.hasConditionValue() != other.hasConditionValue()) {
                  return false;
               } else if (this.hasConditionValue() && !this.getConditionValue().equals(other.getConditionValue())) {
                  return false;
               } else if (this.hasOp() != other.hasOp()) {
                  return false;
               } else if (this.hasOp() && this.op_ != other.op_) {
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
               if (this.hasConditionKey()) {
                  hash = 37 * hash + 1;
                  hash = 53 * hash + this.getConditionKey();
               }

               if (this.hasConditionValue()) {
                  hash = 37 * hash + 2;
                  hash = 53 * hash + this.getConditionValue().hashCode();
               }

               if (this.hasOp()) {
                  hash = 37 * hash + 3;
                  hash = 53 * hash + this.op_;
               }

               hash = 29 * hash + this.unknownFields.hashCode();
               this.memoizedHashCode = hash;
               return hash;
            }
         }

         public static Condition parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (Condition)PARSER.parseFrom(data);
         }

         public static Condition parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Condition)PARSER.parseFrom(data, extensionRegistry);
         }

         public static Condition parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (Condition)PARSER.parseFrom(data);
         }

         public static Condition parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Condition)PARSER.parseFrom(data, extensionRegistry);
         }

         public static Condition parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (Condition)PARSER.parseFrom(data);
         }

         public static Condition parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Condition)PARSER.parseFrom(data, extensionRegistry);
         }

         public static Condition parseFrom(InputStream input) throws IOException {
            return (Condition)GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static Condition parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Condition)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public static Condition parseDelimitedFrom(InputStream input) throws IOException {
            return (Condition)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
         }

         public static Condition parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Condition)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
         }

         public static Condition parseFrom(CodedInputStream input) throws IOException {
            return (Condition)GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static Condition parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Condition)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public Builder newBuilderForType() {
            return newBuilder();
         }

         public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
         }

         public static Builder newBuilder(Condition prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
         }

         public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
         }

         protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
         }

         public static Condition getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<Condition> parser() {
            return PARSER;
         }

         public Parser<Condition> getParserForType() {
            return PARSER;
         }

         public Condition getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
         }

         // $FF: synthetic method
         Condition(GeneratedMessageV3.Builder x0, Object x1) {
            this(x0);
         }

         // $FF: synthetic method
         Condition(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
            this(x0, x1);
         }

         public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ConditionOrBuilder {
            private int bitField0_;
            private int conditionKey_;
            private ByteString conditionValue_;
            private int op_;

            public static final Descriptors.Descriptor getDescriptor() {
               return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_descriptor;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
               return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_fieldAccessorTable.ensureFieldAccessorsInitialized(Condition.class, Builder.class);
            }

            private Builder() {
               this.conditionValue_ = ByteString.EMPTY;
               this.op_ = 0;
               this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
               super(parent);
               this.conditionValue_ = ByteString.EMPTY;
               this.op_ = 0;
               this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
               if (MysqlxExpect.Open.Condition.alwaysUseFieldBuilders) {
               }

            }

            public Builder clear() {
               super.clear();
               this.conditionKey_ = 0;
               this.bitField0_ &= -2;
               this.conditionValue_ = ByteString.EMPTY;
               this.bitField0_ &= -3;
               this.op_ = 0;
               this.bitField0_ &= -5;
               return this;
            }

            public Descriptors.Descriptor getDescriptorForType() {
               return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_descriptor;
            }

            public Condition getDefaultInstanceForType() {
               return MysqlxExpect.Open.Condition.getDefaultInstance();
            }

            public Condition build() {
               Condition result = this.buildPartial();
               if (!result.isInitialized()) {
                  throw newUninitializedMessageException(result);
               } else {
                  return result;
               }
            }

            public Condition buildPartial() {
               Condition result = new Condition(this);
               int from_bitField0_ = this.bitField0_;
               int to_bitField0_ = 0;
               if ((from_bitField0_ & 1) != 0) {
                  result.conditionKey_ = this.conditionKey_;
                  to_bitField0_ |= 1;
               }

               if ((from_bitField0_ & 2) != 0) {
                  to_bitField0_ |= 2;
               }

               result.conditionValue_ = this.conditionValue_;
               if ((from_bitField0_ & 4) != 0) {
                  to_bitField0_ |= 4;
               }

               result.op_ = this.op_;
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
               if (other instanceof Condition) {
                  return this.mergeFrom((Condition)other);
               } else {
                  super.mergeFrom(other);
                  return this;
               }
            }

            public Builder mergeFrom(Condition other) {
               if (other == MysqlxExpect.Open.Condition.getDefaultInstance()) {
                  return this;
               } else {
                  if (other.hasConditionKey()) {
                     this.setConditionKey(other.getConditionKey());
                  }

                  if (other.hasConditionValue()) {
                     this.setConditionValue(other.getConditionValue());
                  }

                  if (other.hasOp()) {
                     this.setOp(other.getOp());
                  }

                  this.mergeUnknownFields(other.unknownFields);
                  this.onChanged();
                  return this;
               }
            }

            public final boolean isInitialized() {
               return this.hasConditionKey();
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
               Condition parsedMessage = null;

               try {
                  parsedMessage = (Condition)MysqlxExpect.Open.Condition.PARSER.parsePartialFrom(input, extensionRegistry);
               } catch (InvalidProtocolBufferException var8) {
                  parsedMessage = (Condition)var8.getUnfinishedMessage();
                  throw var8.unwrapIOException();
               } finally {
                  if (parsedMessage != null) {
                     this.mergeFrom(parsedMessage);
                  }

               }

               return this;
            }

            public boolean hasConditionKey() {
               return (this.bitField0_ & 1) != 0;
            }

            public int getConditionKey() {
               return this.conditionKey_;
            }

            public Builder setConditionKey(int value) {
               this.bitField0_ |= 1;
               this.conditionKey_ = value;
               this.onChanged();
               return this;
            }

            public Builder clearConditionKey() {
               this.bitField0_ &= -2;
               this.conditionKey_ = 0;
               this.onChanged();
               return this;
            }

            public boolean hasConditionValue() {
               return (this.bitField0_ & 2) != 0;
            }

            public ByteString getConditionValue() {
               return this.conditionValue_;
            }

            public Builder setConditionValue(ByteString value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 2;
                  this.conditionValue_ = value;
                  this.onChanged();
                  return this;
               }
            }

            public Builder clearConditionValue() {
               this.bitField0_ &= -3;
               this.conditionValue_ = MysqlxExpect.Open.Condition.getDefaultInstance().getConditionValue();
               this.onChanged();
               return this;
            }

            public boolean hasOp() {
               return (this.bitField0_ & 4) != 0;
            }

            public ConditionOperation getOp() {
               ConditionOperation result = MysqlxExpect.Open.Condition.ConditionOperation.valueOf(this.op_);
               return result == null ? MysqlxExpect.Open.Condition.ConditionOperation.EXPECT_OP_SET : result;
            }

            public Builder setOp(ConditionOperation value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 4;
                  this.op_ = value.getNumber();
                  this.onChanged();
                  return this;
               }
            }

            public Builder clearOp() {
               this.bitField0_ &= -5;
               this.op_ = 0;
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

         public static enum ConditionOperation implements ProtocolMessageEnum {
            EXPECT_OP_SET(0),
            EXPECT_OP_UNSET(1);

            public static final int EXPECT_OP_SET_VALUE = 0;
            public static final int EXPECT_OP_UNSET_VALUE = 1;
            private static final Internal.EnumLiteMap<ConditionOperation> internalValueMap = new Internal.EnumLiteMap<ConditionOperation>() {
               public ConditionOperation findValueByNumber(int number) {
                  return MysqlxExpect.Open.Condition.ConditionOperation.forNumber(number);
               }
            };
            private static final ConditionOperation[] VALUES = values();
            private final int value;

            public final int getNumber() {
               return this.value;
            }

            /** @deprecated */
            @Deprecated
            public static ConditionOperation valueOf(int value) {
               return forNumber(value);
            }

            public static ConditionOperation forNumber(int value) {
               switch (value) {
                  case 0:
                     return EXPECT_OP_SET;
                  case 1:
                     return EXPECT_OP_UNSET;
                  default:
                     return null;
               }
            }

            public static Internal.EnumLiteMap<ConditionOperation> internalGetValueMap() {
               return internalValueMap;
            }

            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
               return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
            }

            public final Descriptors.EnumDescriptor getDescriptorForType() {
               return getDescriptor();
            }

            public static final Descriptors.EnumDescriptor getDescriptor() {
               return (Descriptors.EnumDescriptor)MysqlxExpect.Open.Condition.getDescriptor().getEnumTypes().get(1);
            }

            public static ConditionOperation valueOf(Descriptors.EnumValueDescriptor desc) {
               if (desc.getType() != getDescriptor()) {
                  throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
               } else {
                  return VALUES[desc.getIndex()];
               }
            }

            private ConditionOperation(int value) {
               this.value = value;
            }
         }

         public static enum Key implements ProtocolMessageEnum {
            EXPECT_NO_ERROR(1),
            EXPECT_FIELD_EXIST(2),
            EXPECT_DOCID_GENERATED(3);

            public static final int EXPECT_NO_ERROR_VALUE = 1;
            public static final int EXPECT_FIELD_EXIST_VALUE = 2;
            public static final int EXPECT_DOCID_GENERATED_VALUE = 3;
            private static final Internal.EnumLiteMap<Key> internalValueMap = new Internal.EnumLiteMap<Key>() {
               public Key findValueByNumber(int number) {
                  return MysqlxExpect.Open.Condition.Key.forNumber(number);
               }
            };
            private static final Key[] VALUES = values();
            private final int value;

            public final int getNumber() {
               return this.value;
            }

            /** @deprecated */
            @Deprecated
            public static Key valueOf(int value) {
               return forNumber(value);
            }

            public static Key forNumber(int value) {
               switch (value) {
                  case 1:
                     return EXPECT_NO_ERROR;
                  case 2:
                     return EXPECT_FIELD_EXIST;
                  case 3:
                     return EXPECT_DOCID_GENERATED;
                  default:
                     return null;
               }
            }

            public static Internal.EnumLiteMap<Key> internalGetValueMap() {
               return internalValueMap;
            }

            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
               return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
            }

            public final Descriptors.EnumDescriptor getDescriptorForType() {
               return getDescriptor();
            }

            public static final Descriptors.EnumDescriptor getDescriptor() {
               return (Descriptors.EnumDescriptor)MysqlxExpect.Open.Condition.getDescriptor().getEnumTypes().get(0);
            }

            public static Key valueOf(Descriptors.EnumValueDescriptor desc) {
               if (desc.getType() != getDescriptor()) {
                  throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
               } else {
                  return VALUES[desc.getIndex()];
               }
            }

            private Key(int value) {
               this.value = value;
            }
         }
      }

      public interface ConditionOrBuilder extends MessageOrBuilder {
         boolean hasConditionKey();

         int getConditionKey();

         boolean hasConditionValue();

         ByteString getConditionValue();

         boolean hasOp();

         Condition.ConditionOperation getOp();
      }

      public static enum CtxOperation implements ProtocolMessageEnum {
         EXPECT_CTX_COPY_PREV(0),
         EXPECT_CTX_EMPTY(1);

         public static final int EXPECT_CTX_COPY_PREV_VALUE = 0;
         public static final int EXPECT_CTX_EMPTY_VALUE = 1;
         private static final Internal.EnumLiteMap<CtxOperation> internalValueMap = new Internal.EnumLiteMap<CtxOperation>() {
            public CtxOperation findValueByNumber(int number) {
               return MysqlxExpect.Open.CtxOperation.forNumber(number);
            }
         };
         private static final CtxOperation[] VALUES = values();
         private final int value;

         public final int getNumber() {
            return this.value;
         }

         /** @deprecated */
         @Deprecated
         public static CtxOperation valueOf(int value) {
            return forNumber(value);
         }

         public static CtxOperation forNumber(int value) {
            switch (value) {
               case 0:
                  return EXPECT_CTX_COPY_PREV;
               case 1:
                  return EXPECT_CTX_EMPTY;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<CtxOperation> internalGetValueMap() {
            return internalValueMap;
         }

         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
         }

         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static final Descriptors.EnumDescriptor getDescriptor() {
            return (Descriptors.EnumDescriptor)MysqlxExpect.Open.getDescriptor().getEnumTypes().get(0);
         }

         public static CtxOperation valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private CtxOperation(int value) {
            this.value = value;
         }
      }
   }

   public interface OpenOrBuilder extends MessageOrBuilder {
      boolean hasOp();

      Open.CtxOperation getOp();

      List<Open.Condition> getCondList();

      Open.Condition getCond(int var1);

      int getCondCount();

      List<? extends Open.ConditionOrBuilder> getCondOrBuilderList();

      Open.ConditionOrBuilder getCondOrBuilder(int var1);
   }
}
