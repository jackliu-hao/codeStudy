/*      */ package com.mysql.cj.x.protobuf;
/*      */ import com.google.protobuf.AbstractMessage;
/*      */ import com.google.protobuf.AbstractMessageLite;
/*      */ import com.google.protobuf.AbstractParser;
/*      */ import com.google.protobuf.ByteString;
/*      */ import com.google.protobuf.CodedInputStream;
/*      */ import com.google.protobuf.CodedOutputStream;
/*      */ import com.google.protobuf.Descriptors;
/*      */ import com.google.protobuf.ExtensionRegistry;
/*      */ import com.google.protobuf.ExtensionRegistryLite;
/*      */ import com.google.protobuf.GeneratedMessageV3;
/*      */ import com.google.protobuf.Internal;
/*      */ import com.google.protobuf.InvalidProtocolBufferException;
/*      */ import com.google.protobuf.Message;
/*      */ import com.google.protobuf.MessageLite;
/*      */ import com.google.protobuf.MessageOrBuilder;
/*      */ import com.google.protobuf.Parser;
/*      */ import com.google.protobuf.UnknownFieldSet;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ 
/*      */ public final class MysqlxResultset {
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchDone_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchDone_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchSuspended_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchSuspended_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_ColumnMetaData_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_Row_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_Row_fieldAccessorTable;
/*      */   private static Descriptors.FileDescriptor descriptor;
/*      */   
/*      */   public static void registerAllExtensions(ExtensionRegistryLite registry) {}
/*      */   
/*      */   public static void registerAllExtensions(ExtensionRegistry registry) {
/*   43 */     registerAllExtensions((ExtensionRegistryLite)registry);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public enum ContentType_BYTES
/*      */     implements ProtocolMessageEnum
/*      */   {
/*   70 */     GEOMETRY(1),
/*      */ 
/*      */ 
/*      */     
/*   74 */     JSON(2),
/*      */ 
/*      */ 
/*      */     
/*   78 */     XML(3);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int GEOMETRY_VALUE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int JSON_VALUE = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int XML_VALUE = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  127 */     private static final Internal.EnumLiteMap<ContentType_BYTES> internalValueMap = new Internal.EnumLiteMap<ContentType_BYTES>()
/*      */       {
/*      */         public MysqlxResultset.ContentType_BYTES findValueByNumber(int number) {
/*  130 */           return MysqlxResultset.ContentType_BYTES.forNumber(number);
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  147 */     private static final ContentType_BYTES[] VALUES = values(); private final int value; public final int getNumber() { return this.value; }
/*      */     public static ContentType_BYTES forNumber(int value) { switch (value) {
/*      */         case 1:
/*      */           return GEOMETRY;
/*      */         case 2:
/*      */           return JSON;
/*      */         case 3:
/*      */           return XML;
/*      */       }  return null; }
/*      */     public static Internal.EnumLiteMap<ContentType_BYTES> internalGetValueMap() { return internalValueMap; }
/*      */     static {  }
/*      */     public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); }
/*      */     public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); }
/*      */     public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxResultset.getDescriptor().getEnumTypes().get(0); }
/*  161 */     ContentType_BYTES(int value) { this.value = value; }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public enum ContentType_DATETIME
/*      */     implements ProtocolMessageEnum
/*      */   {
/*  184 */     DATE(1),
/*      */ 
/*      */ 
/*      */     
/*  188 */     DATETIME(2);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int DATE_VALUE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int DATETIME_VALUE = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  232 */     private static final Internal.EnumLiteMap<ContentType_DATETIME> internalValueMap = new Internal.EnumLiteMap<ContentType_DATETIME>()
/*      */       {
/*      */         public MysqlxResultset.ContentType_DATETIME findValueByNumber(int number) {
/*  235 */           return MysqlxResultset.ContentType_DATETIME.forNumber(number);
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  252 */     private static final ContentType_DATETIME[] VALUES = values(); private final int value; public final int getNumber() { return this.value; }
/*      */     public static ContentType_DATETIME forNumber(int value) { switch (value) {
/*      */         case 1:
/*      */           return DATE;
/*      */         case 2:
/*      */           return DATETIME;
/*      */       } 
/*      */       return null; }
/*      */     public static Internal.EnumLiteMap<ContentType_DATETIME> internalGetValueMap() { return internalValueMap; }
/*      */     static {  }
/*      */     public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); }
/*      */     public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); }
/*      */     public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxResultset.getDescriptor().getEnumTypes().get(1); }
/*      */     ContentType_DATETIME(int value) {
/*  266 */       this.value = value;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface FetchDoneMoreOutParamsOrBuilder
/*      */     extends MessageOrBuilder {}
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class FetchDoneMoreOutParams
/*      */     extends GeneratedMessageV3
/*      */     implements FetchDoneMoreOutParamsOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */     
/*      */     private FetchDoneMoreOutParams(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/*  291 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  358 */       this.memoizedIsInitialized = -1; } private FetchDoneMoreOutParams() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new FetchDoneMoreOutParams(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private FetchDoneMoreOutParams(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/*      */     public static final Descriptors.Descriptor getDescriptor() { return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor; }
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchDoneMoreOutParams.class, Builder.class); }
/*  361 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  362 */       if (isInitialized == 1) return true; 
/*  363 */       if (isInitialized == 0) return false;
/*      */       
/*  365 */       this.memoizedIsInitialized = 1;
/*  366 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/*  372 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/*  377 */       int size = this.memoizedSize;
/*  378 */       if (size != -1) return size;
/*      */       
/*  380 */       size = 0;
/*  381 */       size += this.unknownFields.getSerializedSize();
/*  382 */       this.memoizedSize = size;
/*  383 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  388 */       if (obj == this) {
/*  389 */         return true;
/*      */       }
/*  391 */       if (!(obj instanceof FetchDoneMoreOutParams)) {
/*  392 */         return super.equals(obj);
/*      */       }
/*  394 */       FetchDoneMoreOutParams other = (FetchDoneMoreOutParams)obj;
/*      */       
/*  396 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  397 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  402 */       if (this.memoizedHashCode != 0) {
/*  403 */         return this.memoizedHashCode;
/*      */       }
/*  405 */       int hash = 41;
/*  406 */       hash = 19 * hash + getDescriptor().hashCode();
/*  407 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  408 */       this.memoizedHashCode = hash;
/*  409 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreOutParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  415 */       return (FetchDoneMoreOutParams)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreOutParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  421 */       return (FetchDoneMoreOutParams)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreOutParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  426 */       return (FetchDoneMoreOutParams)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreOutParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  432 */       return (FetchDoneMoreOutParams)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static FetchDoneMoreOutParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  436 */       return (FetchDoneMoreOutParams)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreOutParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  442 */       return (FetchDoneMoreOutParams)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static FetchDoneMoreOutParams parseFrom(InputStream input) throws IOException {
/*  446 */       return 
/*  447 */         (FetchDoneMoreOutParams)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreOutParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  453 */       return 
/*  454 */         (FetchDoneMoreOutParams)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static FetchDoneMoreOutParams parseDelimitedFrom(InputStream input) throws IOException {
/*  458 */       return 
/*  459 */         (FetchDoneMoreOutParams)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreOutParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  465 */       return 
/*  466 */         (FetchDoneMoreOutParams)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreOutParams parseFrom(CodedInputStream input) throws IOException {
/*  471 */       return 
/*  472 */         (FetchDoneMoreOutParams)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreOutParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  478 */       return 
/*  479 */         (FetchDoneMoreOutParams)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/*  483 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/*  485 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(FetchDoneMoreOutParams prototype) {
/*  488 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/*  492 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  493 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  499 */       Builder builder = new Builder(parent);
/*  500 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxResultset.FetchDoneMoreOutParamsOrBuilder
/*      */     {
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/*  516 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  522 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_fieldAccessorTable
/*  523 */           .ensureFieldAccessorsInitialized(MysqlxResultset.FetchDoneMoreOutParams.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/*  529 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/*  534 */         super(parent);
/*  535 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/*  539 */         if (MysqlxResultset.FetchDoneMoreOutParams.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/*  544 */         super.clear();
/*  545 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/*  551 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxResultset.FetchDoneMoreOutParams getDefaultInstanceForType() {
/*  556 */         return MysqlxResultset.FetchDoneMoreOutParams.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxResultset.FetchDoneMoreOutParams build() {
/*  561 */         MysqlxResultset.FetchDoneMoreOutParams result = buildPartial();
/*  562 */         if (!result.isInitialized()) {
/*  563 */           throw newUninitializedMessageException(result);
/*      */         }
/*  565 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxResultset.FetchDoneMoreOutParams buildPartial() {
/*  570 */         MysqlxResultset.FetchDoneMoreOutParams result = new MysqlxResultset.FetchDoneMoreOutParams(this);
/*  571 */         onBuilt();
/*  572 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/*  577 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/*  583 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/*  588 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/*  593 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/*  599 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/*  605 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/*  609 */         if (other instanceof MysqlxResultset.FetchDoneMoreOutParams) {
/*  610 */           return mergeFrom((MysqlxResultset.FetchDoneMoreOutParams)other);
/*      */         }
/*  612 */         super.mergeFrom(other);
/*  613 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxResultset.FetchDoneMoreOutParams other) {
/*  618 */         if (other == MysqlxResultset.FetchDoneMoreOutParams.getDefaultInstance()) return this; 
/*  619 */         mergeUnknownFields(other.unknownFields);
/*  620 */         onChanged();
/*  621 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/*  626 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  634 */         MysqlxResultset.FetchDoneMoreOutParams parsedMessage = null;
/*      */         try {
/*  636 */           parsedMessage = (MysqlxResultset.FetchDoneMoreOutParams)MysqlxResultset.FetchDoneMoreOutParams.PARSER.parsePartialFrom(input, extensionRegistry);
/*  637 */         } catch (InvalidProtocolBufferException e) {
/*  638 */           parsedMessage = (MysqlxResultset.FetchDoneMoreOutParams)e.getUnfinishedMessage();
/*  639 */           throw e.unwrapIOException();
/*      */         } finally {
/*  641 */           if (parsedMessage != null) {
/*  642 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/*  645 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  650 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  656 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  666 */     private static final FetchDoneMoreOutParams DEFAULT_INSTANCE = new FetchDoneMoreOutParams();
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreOutParams getDefaultInstance() {
/*  670 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/*  674 */     public static final Parser<FetchDoneMoreOutParams> PARSER = (Parser<FetchDoneMoreOutParams>)new AbstractParser<FetchDoneMoreOutParams>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxResultset.FetchDoneMoreOutParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/*  680 */           return new MysqlxResultset.FetchDoneMoreOutParams(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<FetchDoneMoreOutParams> parser() {
/*  685 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<FetchDoneMoreOutParams> getParserForType() {
/*  690 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public FetchDoneMoreOutParams getDefaultInstanceForType() {
/*  695 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface FetchDoneMoreResultsetsOrBuilder
/*      */     extends MessageOrBuilder {}
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class FetchDoneMoreResultsets
/*      */     extends GeneratedMessageV3
/*      */     implements FetchDoneMoreResultsetsOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */     
/*      */     private FetchDoneMoreResultsets(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/*  719 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  786 */       this.memoizedIsInitialized = -1; } private FetchDoneMoreResultsets() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new FetchDoneMoreResultsets(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private FetchDoneMoreResultsets(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/*      */     public static final Descriptors.Descriptor getDescriptor() { return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor; }
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchDoneMoreResultsets.class, Builder.class); }
/*  789 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  790 */       if (isInitialized == 1) return true; 
/*  791 */       if (isInitialized == 0) return false;
/*      */       
/*  793 */       this.memoizedIsInitialized = 1;
/*  794 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/*  800 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/*  805 */       int size = this.memoizedSize;
/*  806 */       if (size != -1) return size;
/*      */       
/*  808 */       size = 0;
/*  809 */       size += this.unknownFields.getSerializedSize();
/*  810 */       this.memoizedSize = size;
/*  811 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  816 */       if (obj == this) {
/*  817 */         return true;
/*      */       }
/*  819 */       if (!(obj instanceof FetchDoneMoreResultsets)) {
/*  820 */         return super.equals(obj);
/*      */       }
/*  822 */       FetchDoneMoreResultsets other = (FetchDoneMoreResultsets)obj;
/*      */       
/*  824 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  825 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  830 */       if (this.memoizedHashCode != 0) {
/*  831 */         return this.memoizedHashCode;
/*      */       }
/*  833 */       int hash = 41;
/*  834 */       hash = 19 * hash + getDescriptor().hashCode();
/*  835 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  836 */       this.memoizedHashCode = hash;
/*  837 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreResultsets parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  843 */       return (FetchDoneMoreResultsets)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreResultsets parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  849 */       return (FetchDoneMoreResultsets)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreResultsets parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  854 */       return (FetchDoneMoreResultsets)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreResultsets parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  860 */       return (FetchDoneMoreResultsets)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static FetchDoneMoreResultsets parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  864 */       return (FetchDoneMoreResultsets)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreResultsets parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  870 */       return (FetchDoneMoreResultsets)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static FetchDoneMoreResultsets parseFrom(InputStream input) throws IOException {
/*  874 */       return 
/*  875 */         (FetchDoneMoreResultsets)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreResultsets parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  881 */       return 
/*  882 */         (FetchDoneMoreResultsets)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static FetchDoneMoreResultsets parseDelimitedFrom(InputStream input) throws IOException {
/*  886 */       return 
/*  887 */         (FetchDoneMoreResultsets)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreResultsets parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  893 */       return 
/*  894 */         (FetchDoneMoreResultsets)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreResultsets parseFrom(CodedInputStream input) throws IOException {
/*  899 */       return 
/*  900 */         (FetchDoneMoreResultsets)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreResultsets parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  906 */       return 
/*  907 */         (FetchDoneMoreResultsets)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/*  911 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/*  913 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(FetchDoneMoreResultsets prototype) {
/*  916 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/*  920 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  921 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  927 */       Builder builder = new Builder(parent);
/*  928 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxResultset.FetchDoneMoreResultsetsOrBuilder
/*      */     {
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/*  944 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  950 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_fieldAccessorTable
/*  951 */           .ensureFieldAccessorsInitialized(MysqlxResultset.FetchDoneMoreResultsets.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/*  957 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/*  962 */         super(parent);
/*  963 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/*  967 */         if (MysqlxResultset.FetchDoneMoreResultsets.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/*  972 */         super.clear();
/*  973 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/*  979 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxResultset.FetchDoneMoreResultsets getDefaultInstanceForType() {
/*  984 */         return MysqlxResultset.FetchDoneMoreResultsets.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxResultset.FetchDoneMoreResultsets build() {
/*  989 */         MysqlxResultset.FetchDoneMoreResultsets result = buildPartial();
/*  990 */         if (!result.isInitialized()) {
/*  991 */           throw newUninitializedMessageException(result);
/*      */         }
/*  993 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxResultset.FetchDoneMoreResultsets buildPartial() {
/*  998 */         MysqlxResultset.FetchDoneMoreResultsets result = new MysqlxResultset.FetchDoneMoreResultsets(this);
/*  999 */         onBuilt();
/* 1000 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 1005 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 1011 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 1016 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 1021 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 1027 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 1033 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 1037 */         if (other instanceof MysqlxResultset.FetchDoneMoreResultsets) {
/* 1038 */           return mergeFrom((MysqlxResultset.FetchDoneMoreResultsets)other);
/*      */         }
/* 1040 */         super.mergeFrom(other);
/* 1041 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxResultset.FetchDoneMoreResultsets other) {
/* 1046 */         if (other == MysqlxResultset.FetchDoneMoreResultsets.getDefaultInstance()) return this; 
/* 1047 */         mergeUnknownFields(other.unknownFields);
/* 1048 */         onChanged();
/* 1049 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 1054 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1062 */         MysqlxResultset.FetchDoneMoreResultsets parsedMessage = null;
/*      */         try {
/* 1064 */           parsedMessage = (MysqlxResultset.FetchDoneMoreResultsets)MysqlxResultset.FetchDoneMoreResultsets.PARSER.parsePartialFrom(input, extensionRegistry);
/* 1065 */         } catch (InvalidProtocolBufferException e) {
/* 1066 */           parsedMessage = (MysqlxResultset.FetchDoneMoreResultsets)e.getUnfinishedMessage();
/* 1067 */           throw e.unwrapIOException();
/*      */         } finally {
/* 1069 */           if (parsedMessage != null) {
/* 1070 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 1073 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 1078 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 1084 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1094 */     private static final FetchDoneMoreResultsets DEFAULT_INSTANCE = new FetchDoneMoreResultsets();
/*      */ 
/*      */     
/*      */     public static FetchDoneMoreResultsets getDefaultInstance() {
/* 1098 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 1102 */     public static final Parser<FetchDoneMoreResultsets> PARSER = (Parser<FetchDoneMoreResultsets>)new AbstractParser<FetchDoneMoreResultsets>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxResultset.FetchDoneMoreResultsets parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 1108 */           return new MysqlxResultset.FetchDoneMoreResultsets(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<FetchDoneMoreResultsets> parser() {
/* 1113 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<FetchDoneMoreResultsets> getParserForType() {
/* 1118 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public FetchDoneMoreResultsets getDefaultInstanceForType() {
/* 1123 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface FetchDoneOrBuilder
/*      */     extends MessageOrBuilder {}
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class FetchDone
/*      */     extends GeneratedMessageV3
/*      */     implements FetchDoneOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */     
/*      */     private FetchDone(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 1147 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1214 */       this.memoizedIsInitialized = -1; } private FetchDone() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new FetchDone(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private FetchDone(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/*      */     public static final Descriptors.Descriptor getDescriptor() { return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_descriptor; }
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchDone.class, Builder.class); }
/* 1217 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 1218 */       if (isInitialized == 1) return true; 
/* 1219 */       if (isInitialized == 0) return false;
/*      */       
/* 1221 */       this.memoizedIsInitialized = 1;
/* 1222 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 1228 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 1233 */       int size = this.memoizedSize;
/* 1234 */       if (size != -1) return size;
/*      */       
/* 1236 */       size = 0;
/* 1237 */       size += this.unknownFields.getSerializedSize();
/* 1238 */       this.memoizedSize = size;
/* 1239 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1244 */       if (obj == this) {
/* 1245 */         return true;
/*      */       }
/* 1247 */       if (!(obj instanceof FetchDone)) {
/* 1248 */         return super.equals(obj);
/*      */       }
/* 1250 */       FetchDone other = (FetchDone)obj;
/*      */       
/* 1252 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 1253 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1258 */       if (this.memoizedHashCode != 0) {
/* 1259 */         return this.memoizedHashCode;
/*      */       }
/* 1261 */       int hash = 41;
/* 1262 */       hash = 19 * hash + getDescriptor().hashCode();
/* 1263 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 1264 */       this.memoizedHashCode = hash;
/* 1265 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDone parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 1271 */       return (FetchDone)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDone parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1277 */       return (FetchDone)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static FetchDone parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 1282 */       return (FetchDone)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDone parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1288 */       return (FetchDone)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static FetchDone parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 1292 */       return (FetchDone)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDone parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1298 */       return (FetchDone)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static FetchDone parseFrom(InputStream input) throws IOException {
/* 1302 */       return 
/* 1303 */         (FetchDone)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDone parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1309 */       return 
/* 1310 */         (FetchDone)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static FetchDone parseDelimitedFrom(InputStream input) throws IOException {
/* 1314 */       return 
/* 1315 */         (FetchDone)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDone parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1321 */       return 
/* 1322 */         (FetchDone)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static FetchDone parseFrom(CodedInputStream input) throws IOException {
/* 1327 */       return 
/* 1328 */         (FetchDone)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchDone parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1334 */       return 
/* 1335 */         (FetchDone)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 1339 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 1341 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(FetchDone prototype) {
/* 1344 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 1348 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 1349 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 1355 */       Builder builder = new Builder(parent);
/* 1356 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxResultset.FetchDoneOrBuilder
/*      */     {
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 1372 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 1378 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_fieldAccessorTable
/* 1379 */           .ensureFieldAccessorsInitialized(MysqlxResultset.FetchDone.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 1385 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 1390 */         super(parent);
/* 1391 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 1395 */         if (MysqlxResultset.FetchDone.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/* 1400 */         super.clear();
/* 1401 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 1407 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxResultset.FetchDone getDefaultInstanceForType() {
/* 1412 */         return MysqlxResultset.FetchDone.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxResultset.FetchDone build() {
/* 1417 */         MysqlxResultset.FetchDone result = buildPartial();
/* 1418 */         if (!result.isInitialized()) {
/* 1419 */           throw newUninitializedMessageException(result);
/*      */         }
/* 1421 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxResultset.FetchDone buildPartial() {
/* 1426 */         MysqlxResultset.FetchDone result = new MysqlxResultset.FetchDone(this);
/* 1427 */         onBuilt();
/* 1428 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 1433 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 1439 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 1444 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 1449 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 1455 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 1461 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 1465 */         if (other instanceof MysqlxResultset.FetchDone) {
/* 1466 */           return mergeFrom((MysqlxResultset.FetchDone)other);
/*      */         }
/* 1468 */         super.mergeFrom(other);
/* 1469 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxResultset.FetchDone other) {
/* 1474 */         if (other == MysqlxResultset.FetchDone.getDefaultInstance()) return this; 
/* 1475 */         mergeUnknownFields(other.unknownFields);
/* 1476 */         onChanged();
/* 1477 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 1482 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1490 */         MysqlxResultset.FetchDone parsedMessage = null;
/*      */         try {
/* 1492 */           parsedMessage = (MysqlxResultset.FetchDone)MysqlxResultset.FetchDone.PARSER.parsePartialFrom(input, extensionRegistry);
/* 1493 */         } catch (InvalidProtocolBufferException e) {
/* 1494 */           parsedMessage = (MysqlxResultset.FetchDone)e.getUnfinishedMessage();
/* 1495 */           throw e.unwrapIOException();
/*      */         } finally {
/* 1497 */           if (parsedMessage != null) {
/* 1498 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 1501 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 1506 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 1512 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1522 */     private static final FetchDone DEFAULT_INSTANCE = new FetchDone();
/*      */ 
/*      */     
/*      */     public static FetchDone getDefaultInstance() {
/* 1526 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 1530 */     public static final Parser<FetchDone> PARSER = (Parser<FetchDone>)new AbstractParser<FetchDone>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxResultset.FetchDone parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 1536 */           return new MysqlxResultset.FetchDone(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<FetchDone> parser() {
/* 1541 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<FetchDone> getParserForType() {
/* 1546 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public FetchDone getDefaultInstanceForType() {
/* 1551 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface FetchSuspendedOrBuilder
/*      */     extends MessageOrBuilder {}
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class FetchSuspended
/*      */     extends GeneratedMessageV3
/*      */     implements FetchSuspendedOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */     
/*      */     private FetchSuspended(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 1575 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1642 */       this.memoizedIsInitialized = -1; } private FetchSuspended() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new FetchSuspended(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private FetchSuspended(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/*      */     public static final Descriptors.Descriptor getDescriptor() { return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_descriptor; }
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_fieldAccessorTable.ensureFieldAccessorsInitialized(FetchSuspended.class, Builder.class); }
/* 1645 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 1646 */       if (isInitialized == 1) return true; 
/* 1647 */       if (isInitialized == 0) return false;
/*      */       
/* 1649 */       this.memoizedIsInitialized = 1;
/* 1650 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 1656 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 1661 */       int size = this.memoizedSize;
/* 1662 */       if (size != -1) return size;
/*      */       
/* 1664 */       size = 0;
/* 1665 */       size += this.unknownFields.getSerializedSize();
/* 1666 */       this.memoizedSize = size;
/* 1667 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1672 */       if (obj == this) {
/* 1673 */         return true;
/*      */       }
/* 1675 */       if (!(obj instanceof FetchSuspended)) {
/* 1676 */         return super.equals(obj);
/*      */       }
/* 1678 */       FetchSuspended other = (FetchSuspended)obj;
/*      */       
/* 1680 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 1681 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1686 */       if (this.memoizedHashCode != 0) {
/* 1687 */         return this.memoizedHashCode;
/*      */       }
/* 1689 */       int hash = 41;
/* 1690 */       hash = 19 * hash + getDescriptor().hashCode();
/* 1691 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 1692 */       this.memoizedHashCode = hash;
/* 1693 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchSuspended parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 1699 */       return (FetchSuspended)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchSuspended parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1705 */       return (FetchSuspended)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static FetchSuspended parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 1710 */       return (FetchSuspended)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchSuspended parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1716 */       return (FetchSuspended)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static FetchSuspended parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 1720 */       return (FetchSuspended)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchSuspended parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1726 */       return (FetchSuspended)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static FetchSuspended parseFrom(InputStream input) throws IOException {
/* 1730 */       return 
/* 1731 */         (FetchSuspended)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchSuspended parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1737 */       return 
/* 1738 */         (FetchSuspended)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static FetchSuspended parseDelimitedFrom(InputStream input) throws IOException {
/* 1742 */       return 
/* 1743 */         (FetchSuspended)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchSuspended parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1749 */       return 
/* 1750 */         (FetchSuspended)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static FetchSuspended parseFrom(CodedInputStream input) throws IOException {
/* 1755 */       return 
/* 1756 */         (FetchSuspended)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static FetchSuspended parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1762 */       return 
/* 1763 */         (FetchSuspended)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 1767 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 1769 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(FetchSuspended prototype) {
/* 1772 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 1776 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 1777 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 1783 */       Builder builder = new Builder(parent);
/* 1784 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxResultset.FetchSuspendedOrBuilder
/*      */     {
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 1800 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 1806 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_fieldAccessorTable
/* 1807 */           .ensureFieldAccessorsInitialized(MysqlxResultset.FetchSuspended.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 1813 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 1818 */         super(parent);
/* 1819 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 1823 */         if (MysqlxResultset.FetchSuspended.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/* 1828 */         super.clear();
/* 1829 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 1835 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxResultset.FetchSuspended getDefaultInstanceForType() {
/* 1840 */         return MysqlxResultset.FetchSuspended.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxResultset.FetchSuspended build() {
/* 1845 */         MysqlxResultset.FetchSuspended result = buildPartial();
/* 1846 */         if (!result.isInitialized()) {
/* 1847 */           throw newUninitializedMessageException(result);
/*      */         }
/* 1849 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxResultset.FetchSuspended buildPartial() {
/* 1854 */         MysqlxResultset.FetchSuspended result = new MysqlxResultset.FetchSuspended(this);
/* 1855 */         onBuilt();
/* 1856 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 1861 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 1867 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 1872 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 1877 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 1883 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 1889 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 1893 */         if (other instanceof MysqlxResultset.FetchSuspended) {
/* 1894 */           return mergeFrom((MysqlxResultset.FetchSuspended)other);
/*      */         }
/* 1896 */         super.mergeFrom(other);
/* 1897 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxResultset.FetchSuspended other) {
/* 1902 */         if (other == MysqlxResultset.FetchSuspended.getDefaultInstance()) return this; 
/* 1903 */         mergeUnknownFields(other.unknownFields);
/* 1904 */         onChanged();
/* 1905 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 1910 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1918 */         MysqlxResultset.FetchSuspended parsedMessage = null;
/*      */         try {
/* 1920 */           parsedMessage = (MysqlxResultset.FetchSuspended)MysqlxResultset.FetchSuspended.PARSER.parsePartialFrom(input, extensionRegistry);
/* 1921 */         } catch (InvalidProtocolBufferException e) {
/* 1922 */           parsedMessage = (MysqlxResultset.FetchSuspended)e.getUnfinishedMessage();
/* 1923 */           throw e.unwrapIOException();
/*      */         } finally {
/* 1925 */           if (parsedMessage != null) {
/* 1926 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 1929 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 1934 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 1940 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1950 */     private static final FetchSuspended DEFAULT_INSTANCE = new FetchSuspended();
/*      */ 
/*      */     
/*      */     public static FetchSuspended getDefaultInstance() {
/* 1954 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 1958 */     public static final Parser<FetchSuspended> PARSER = (Parser<FetchSuspended>)new AbstractParser<FetchSuspended>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxResultset.FetchSuspended parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 1964 */           return new MysqlxResultset.FetchSuspended(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<FetchSuspended> parser() {
/* 1969 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<FetchSuspended> getParserForType() {
/* 1974 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public FetchSuspended getDefaultInstanceForType() {
/* 1979 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface ColumnMetaDataOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasType();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxResultset.ColumnMetaData.FieldType getType();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasName();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getName();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasOriginalName();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getOriginalName();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasTable();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getTable();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasOriginalTable();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getOriginalTable();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasSchema();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getSchema();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasCatalog();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getCatalog();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasCollation();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     long getCollation();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasFractionalDigits();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getFractionalDigits();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasLength();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getLength();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasFlags();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getFlags();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasContentType();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getContentType();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class ColumnMetaData
/*      */     extends GeneratedMessageV3
/*      */     implements ColumnMetaDataOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int TYPE_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int type_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int NAME_FIELD_NUMBER = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private ByteString name_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int ORIGINAL_NAME_FIELD_NUMBER = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private ByteString originalName_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int TABLE_FIELD_NUMBER = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private ByteString table_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int ORIGINAL_TABLE_FIELD_NUMBER = 5;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private ByteString originalTable_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int SCHEMA_FIELD_NUMBER = 6;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private ByteString schema_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int CATALOG_FIELD_NUMBER = 7;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private ByteString catalog_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int COLLATION_FIELD_NUMBER = 8;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private long collation_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int FRACTIONAL_DIGITS_FIELD_NUMBER = 9;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int fractionalDigits_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int LENGTH_FIELD_NUMBER = 10;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int length_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int FLAGS_FIELD_NUMBER = 11;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int flags_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int CONTENT_TYPE_FIELD_NUMBER = 12;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int contentType_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private ColumnMetaData(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 2530 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3208 */       this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new ColumnMetaData(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private ColumnMetaData(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int rawValue; FieldType value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: rawValue = input.readEnum(); value = FieldType.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(1, rawValue); continue; }  this.bitField0_ |= 0x1; this.type_ = rawValue; continue;case 18: this.bitField0_ |= 0x2; this.name_ = input.readBytes(); continue;case 26: this.bitField0_ |= 0x4; this.originalName_ = input.readBytes(); continue;case 34: this.bitField0_ |= 0x8; this.table_ = input.readBytes(); continue;case 42: this.bitField0_ |= 0x10; this.originalTable_ = input.readBytes(); continue;case 50: this.bitField0_ |= 0x20; this.schema_ = input.readBytes(); continue;case 58: this.bitField0_ |= 0x40; this.catalog_ = input.readBytes(); continue;case 64: this.bitField0_ |= 0x80; this.collation_ = input.readUInt64(); continue;case 72: this.bitField0_ |= 0x100; this.fractionalDigits_ = input.readUInt32(); continue;case 80: this.bitField0_ |= 0x200; this.length_ = input.readUInt32(); continue;case 88: this.bitField0_ |= 0x400; this.flags_ = input.readUInt32(); continue;case 96: this.bitField0_ |= 0x800; this.contentType_ = input.readUInt32(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_fieldAccessorTable.ensureFieldAccessorsInitialized(ColumnMetaData.class, Builder.class); } public enum FieldType implements ProtocolMessageEnum { SINT(1), UINT(2), DOUBLE(5), FLOAT(6), BYTES(7), TIME(10), DATETIME(12), SET(15), ENUM(16), BIT(17), DECIMAL(18); public static final int SINT_VALUE = 1; public static final int UINT_VALUE = 2; public static final int DOUBLE_VALUE = 5; public static final int FLOAT_VALUE = 6; public static final int BYTES_VALUE = 7; public static final int TIME_VALUE = 10; public static final int DATETIME_VALUE = 12; public static final int SET_VALUE = 15; public static final int ENUM_VALUE = 16; public static final int BIT_VALUE = 17; public static final int DECIMAL_VALUE = 18; private static final Internal.EnumLiteMap<FieldType> internalValueMap = new Internal.EnumLiteMap<FieldType>() { public MysqlxResultset.ColumnMetaData.FieldType findValueByNumber(int number) { return MysqlxResultset.ColumnMetaData.FieldType.forNumber(number); } }; private static final FieldType[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static FieldType forNumber(int value) { switch (value) { case 1: return SINT;case 2: return UINT;case 5: return DOUBLE;case 6: return FLOAT;case 7: return BYTES;case 10: return TIME;case 12: return DATETIME;case 15: return SET;case 16: return ENUM;case 17: return BIT;case 18: return DECIMAL; }  return null; } public static Internal.EnumLiteMap<FieldType> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxResultset.ColumnMetaData.getDescriptor().getEnumTypes().get(0); } FieldType(int value) { this.value = value; } } public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); } public FieldType getType() { FieldType result = FieldType.valueOf(this.type_); return (result == null) ? FieldType.SINT : result; } private ColumnMetaData() { this.memoizedIsInitialized = -1; this.type_ = 1; this.name_ = ByteString.EMPTY; this.originalName_ = ByteString.EMPTY; this.table_ = ByteString.EMPTY; this.originalTable_ = ByteString.EMPTY; this.schema_ = ByteString.EMPTY; this.catalog_ = ByteString.EMPTY; }
/*      */     public boolean hasName() { return ((this.bitField0_ & 0x2) != 0); }
/*      */     public ByteString getName() { return this.name_; }
/* 3211 */     public boolean hasOriginalName() { return ((this.bitField0_ & 0x4) != 0); } public ByteString getOriginalName() { return this.originalName_; } public boolean hasTable() { return ((this.bitField0_ & 0x8) != 0); } public ByteString getTable() { return this.table_; } public boolean hasOriginalTable() { return ((this.bitField0_ & 0x10) != 0); } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 3212 */       if (isInitialized == 1) return true; 
/* 3213 */       if (isInitialized == 0) return false;
/*      */       
/* 3215 */       if (!hasType()) {
/* 3216 */         this.memoizedIsInitialized = 0;
/* 3217 */         return false;
/*      */       } 
/* 3219 */       this.memoizedIsInitialized = 1;
/* 3220 */       return true; } public ByteString getOriginalTable() { return this.originalTable_; } public boolean hasSchema() { return ((this.bitField0_ & 0x20) != 0); } public ByteString getSchema() { return this.schema_; } public boolean hasCatalog() { return ((this.bitField0_ & 0x40) != 0); } public ByteString getCatalog() { return this.catalog_; } public boolean hasCollation() { return ((this.bitField0_ & 0x80) != 0); } public long getCollation() { return this.collation_; } public boolean hasFractionalDigits() { return ((this.bitField0_ & 0x100) != 0); } public int getFractionalDigits() { return this.fractionalDigits_; } public boolean hasLength() { return ((this.bitField0_ & 0x200) != 0); }
/*      */     public int getLength() { return this.length_; }
/*      */     public boolean hasFlags() { return ((this.bitField0_ & 0x400) != 0); }
/*      */     public int getFlags() { return this.flags_; }
/*      */     public boolean hasContentType() { return ((this.bitField0_ & 0x800) != 0); }
/*      */     public int getContentType() { return this.contentType_; }
/* 3226 */     public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) {
/* 3227 */         output.writeEnum(1, this.type_);
/*      */       }
/* 3229 */       if ((this.bitField0_ & 0x2) != 0) {
/* 3230 */         output.writeBytes(2, this.name_);
/*      */       }
/* 3232 */       if ((this.bitField0_ & 0x4) != 0) {
/* 3233 */         output.writeBytes(3, this.originalName_);
/*      */       }
/* 3235 */       if ((this.bitField0_ & 0x8) != 0) {
/* 3236 */         output.writeBytes(4, this.table_);
/*      */       }
/* 3238 */       if ((this.bitField0_ & 0x10) != 0) {
/* 3239 */         output.writeBytes(5, this.originalTable_);
/*      */       }
/* 3241 */       if ((this.bitField0_ & 0x20) != 0) {
/* 3242 */         output.writeBytes(6, this.schema_);
/*      */       }
/* 3244 */       if ((this.bitField0_ & 0x40) != 0) {
/* 3245 */         output.writeBytes(7, this.catalog_);
/*      */       }
/* 3247 */       if ((this.bitField0_ & 0x80) != 0) {
/* 3248 */         output.writeUInt64(8, this.collation_);
/*      */       }
/* 3250 */       if ((this.bitField0_ & 0x100) != 0) {
/* 3251 */         output.writeUInt32(9, this.fractionalDigits_);
/*      */       }
/* 3253 */       if ((this.bitField0_ & 0x200) != 0) {
/* 3254 */         output.writeUInt32(10, this.length_);
/*      */       }
/* 3256 */       if ((this.bitField0_ & 0x400) != 0) {
/* 3257 */         output.writeUInt32(11, this.flags_);
/*      */       }
/* 3259 */       if ((this.bitField0_ & 0x800) != 0) {
/* 3260 */         output.writeUInt32(12, this.contentType_);
/*      */       }
/* 3262 */       this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 3267 */       int size = this.memoizedSize;
/* 3268 */       if (size != -1) return size;
/*      */       
/* 3270 */       size = 0;
/* 3271 */       if ((this.bitField0_ & 0x1) != 0) {
/* 3272 */         size += 
/* 3273 */           CodedOutputStream.computeEnumSize(1, this.type_);
/*      */       }
/* 3275 */       if ((this.bitField0_ & 0x2) != 0) {
/* 3276 */         size += 
/* 3277 */           CodedOutputStream.computeBytesSize(2, this.name_);
/*      */       }
/* 3279 */       if ((this.bitField0_ & 0x4) != 0) {
/* 3280 */         size += 
/* 3281 */           CodedOutputStream.computeBytesSize(3, this.originalName_);
/*      */       }
/* 3283 */       if ((this.bitField0_ & 0x8) != 0) {
/* 3284 */         size += 
/* 3285 */           CodedOutputStream.computeBytesSize(4, this.table_);
/*      */       }
/* 3287 */       if ((this.bitField0_ & 0x10) != 0) {
/* 3288 */         size += 
/* 3289 */           CodedOutputStream.computeBytesSize(5, this.originalTable_);
/*      */       }
/* 3291 */       if ((this.bitField0_ & 0x20) != 0) {
/* 3292 */         size += 
/* 3293 */           CodedOutputStream.computeBytesSize(6, this.schema_);
/*      */       }
/* 3295 */       if ((this.bitField0_ & 0x40) != 0) {
/* 3296 */         size += 
/* 3297 */           CodedOutputStream.computeBytesSize(7, this.catalog_);
/*      */       }
/* 3299 */       if ((this.bitField0_ & 0x80) != 0) {
/* 3300 */         size += 
/* 3301 */           CodedOutputStream.computeUInt64Size(8, this.collation_);
/*      */       }
/* 3303 */       if ((this.bitField0_ & 0x100) != 0) {
/* 3304 */         size += 
/* 3305 */           CodedOutputStream.computeUInt32Size(9, this.fractionalDigits_);
/*      */       }
/* 3307 */       if ((this.bitField0_ & 0x200) != 0) {
/* 3308 */         size += 
/* 3309 */           CodedOutputStream.computeUInt32Size(10, this.length_);
/*      */       }
/* 3311 */       if ((this.bitField0_ & 0x400) != 0) {
/* 3312 */         size += 
/* 3313 */           CodedOutputStream.computeUInt32Size(11, this.flags_);
/*      */       }
/* 3315 */       if ((this.bitField0_ & 0x800) != 0) {
/* 3316 */         size += 
/* 3317 */           CodedOutputStream.computeUInt32Size(12, this.contentType_);
/*      */       }
/* 3319 */       size += this.unknownFields.getSerializedSize();
/* 3320 */       this.memoizedSize = size;
/* 3321 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 3326 */       if (obj == this) {
/* 3327 */         return true;
/*      */       }
/* 3329 */       if (!(obj instanceof ColumnMetaData)) {
/* 3330 */         return super.equals(obj);
/*      */       }
/* 3332 */       ColumnMetaData other = (ColumnMetaData)obj;
/*      */       
/* 3334 */       if (hasType() != other.hasType()) return false; 
/* 3335 */       if (hasType() && 
/* 3336 */         this.type_ != other.type_) return false;
/*      */       
/* 3338 */       if (hasName() != other.hasName()) return false; 
/* 3339 */       if (hasName() && 
/*      */         
/* 3341 */         !getName().equals(other.getName())) return false;
/*      */       
/* 3343 */       if (hasOriginalName() != other.hasOriginalName()) return false; 
/* 3344 */       if (hasOriginalName() && 
/*      */         
/* 3346 */         !getOriginalName().equals(other.getOriginalName())) return false;
/*      */       
/* 3348 */       if (hasTable() != other.hasTable()) return false; 
/* 3349 */       if (hasTable() && 
/*      */         
/* 3351 */         !getTable().equals(other.getTable())) return false;
/*      */       
/* 3353 */       if (hasOriginalTable() != other.hasOriginalTable()) return false; 
/* 3354 */       if (hasOriginalTable() && 
/*      */         
/* 3356 */         !getOriginalTable().equals(other.getOriginalTable())) return false;
/*      */       
/* 3358 */       if (hasSchema() != other.hasSchema()) return false; 
/* 3359 */       if (hasSchema() && 
/*      */         
/* 3361 */         !getSchema().equals(other.getSchema())) return false;
/*      */       
/* 3363 */       if (hasCatalog() != other.hasCatalog()) return false; 
/* 3364 */       if (hasCatalog() && 
/*      */         
/* 3366 */         !getCatalog().equals(other.getCatalog())) return false;
/*      */       
/* 3368 */       if (hasCollation() != other.hasCollation()) return false; 
/* 3369 */       if (hasCollation() && 
/* 3370 */         getCollation() != other
/* 3371 */         .getCollation()) return false;
/*      */       
/* 3373 */       if (hasFractionalDigits() != other.hasFractionalDigits()) return false; 
/* 3374 */       if (hasFractionalDigits() && 
/* 3375 */         getFractionalDigits() != other
/* 3376 */         .getFractionalDigits()) return false;
/*      */       
/* 3378 */       if (hasLength() != other.hasLength()) return false; 
/* 3379 */       if (hasLength() && 
/* 3380 */         getLength() != other
/* 3381 */         .getLength()) return false;
/*      */       
/* 3383 */       if (hasFlags() != other.hasFlags()) return false; 
/* 3384 */       if (hasFlags() && 
/* 3385 */         getFlags() != other
/* 3386 */         .getFlags()) return false;
/*      */       
/* 3388 */       if (hasContentType() != other.hasContentType()) return false; 
/* 3389 */       if (hasContentType() && 
/* 3390 */         getContentType() != other
/* 3391 */         .getContentType()) return false;
/*      */       
/* 3393 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 3394 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 3399 */       if (this.memoizedHashCode != 0) {
/* 3400 */         return this.memoizedHashCode;
/*      */       }
/* 3402 */       int hash = 41;
/* 3403 */       hash = 19 * hash + getDescriptor().hashCode();
/* 3404 */       if (hasType()) {
/* 3405 */         hash = 37 * hash + 1;
/* 3406 */         hash = 53 * hash + this.type_;
/*      */       } 
/* 3408 */       if (hasName()) {
/* 3409 */         hash = 37 * hash + 2;
/* 3410 */         hash = 53 * hash + getName().hashCode();
/*      */       } 
/* 3412 */       if (hasOriginalName()) {
/* 3413 */         hash = 37 * hash + 3;
/* 3414 */         hash = 53 * hash + getOriginalName().hashCode();
/*      */       } 
/* 3416 */       if (hasTable()) {
/* 3417 */         hash = 37 * hash + 4;
/* 3418 */         hash = 53 * hash + getTable().hashCode();
/*      */       } 
/* 3420 */       if (hasOriginalTable()) {
/* 3421 */         hash = 37 * hash + 5;
/* 3422 */         hash = 53 * hash + getOriginalTable().hashCode();
/*      */       } 
/* 3424 */       if (hasSchema()) {
/* 3425 */         hash = 37 * hash + 6;
/* 3426 */         hash = 53 * hash + getSchema().hashCode();
/*      */       } 
/* 3428 */       if (hasCatalog()) {
/* 3429 */         hash = 37 * hash + 7;
/* 3430 */         hash = 53 * hash + getCatalog().hashCode();
/*      */       } 
/* 3432 */       if (hasCollation()) {
/* 3433 */         hash = 37 * hash + 8;
/* 3434 */         hash = 53 * hash + Internal.hashLong(
/* 3435 */             getCollation());
/*      */       } 
/* 3437 */       if (hasFractionalDigits()) {
/* 3438 */         hash = 37 * hash + 9;
/* 3439 */         hash = 53 * hash + getFractionalDigits();
/*      */       } 
/* 3441 */       if (hasLength()) {
/* 3442 */         hash = 37 * hash + 10;
/* 3443 */         hash = 53 * hash + getLength();
/*      */       } 
/* 3445 */       if (hasFlags()) {
/* 3446 */         hash = 37 * hash + 11;
/* 3447 */         hash = 53 * hash + getFlags();
/*      */       } 
/* 3449 */       if (hasContentType()) {
/* 3450 */         hash = 37 * hash + 12;
/* 3451 */         hash = 53 * hash + getContentType();
/*      */       } 
/* 3453 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 3454 */       this.memoizedHashCode = hash;
/* 3455 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ColumnMetaData parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 3461 */       return (ColumnMetaData)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ColumnMetaData parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 3467 */       return (ColumnMetaData)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static ColumnMetaData parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 3472 */       return (ColumnMetaData)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ColumnMetaData parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 3478 */       return (ColumnMetaData)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static ColumnMetaData parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 3482 */       return (ColumnMetaData)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ColumnMetaData parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 3488 */       return (ColumnMetaData)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static ColumnMetaData parseFrom(InputStream input) throws IOException {
/* 3492 */       return 
/* 3493 */         (ColumnMetaData)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ColumnMetaData parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3499 */       return 
/* 3500 */         (ColumnMetaData)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static ColumnMetaData parseDelimitedFrom(InputStream input) throws IOException {
/* 3504 */       return 
/* 3505 */         (ColumnMetaData)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ColumnMetaData parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3511 */       return 
/* 3512 */         (ColumnMetaData)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static ColumnMetaData parseFrom(CodedInputStream input) throws IOException {
/* 3517 */       return 
/* 3518 */         (ColumnMetaData)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ColumnMetaData parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3524 */       return 
/* 3525 */         (ColumnMetaData)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 3529 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 3531 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(ColumnMetaData prototype) {
/* 3534 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 3538 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 3539 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 3545 */       Builder builder = new Builder(parent);
/* 3546 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxResultset.ColumnMetaDataOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private int type_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private ByteString name_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private ByteString originalName_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private ByteString table_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private ByteString originalTable_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private ByteString schema_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private ByteString catalog_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private long collation_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private int fractionalDigits_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private int length_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private int flags_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private int contentType_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 3805 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 3811 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_fieldAccessorTable
/* 3812 */           .ensureFieldAccessorsInitialized(MysqlxResultset.ColumnMetaData.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder()
/*      */       {
/* 4052 */         this.type_ = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4110 */         this.name_ = ByteString.EMPTY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4166 */         this.originalName_ = ByteString.EMPTY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4222 */         this.table_ = ByteString.EMPTY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4278 */         this.originalTable_ = ByteString.EMPTY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4334 */         this.schema_ = ByteString.EMPTY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4390 */         this.catalog_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.type_ = 1; this.name_ = ByteString.EMPTY; this.originalName_ = ByteString.EMPTY; this.table_ = ByteString.EMPTY; this.originalTable_ = ByteString.EMPTY; this.schema_ = ByteString.EMPTY; this.catalog_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxResultset.ColumnMetaData.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.type_ = 1; this.bitField0_ &= 0xFFFFFFFE; this.name_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFFD; this.originalName_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFFB; this.table_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFF7; this.originalTable_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFEF; this.schema_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFDF; this.catalog_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFBF; this.collation_ = 0L; this.bitField0_ &= 0xFFFFFF7F; this.fractionalDigits_ = 0; this.bitField0_ &= 0xFFFFFEFF; this.length_ = 0; this.bitField0_ &= 0xFFFFFDFF; this.flags_ = 0; this.bitField0_ &= 0xFFFFFBFF; this.contentType_ = 0; this.bitField0_ &= 0xFFFFF7FF; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor; } public MysqlxResultset.ColumnMetaData getDefaultInstanceForType() { return MysqlxResultset.ColumnMetaData.getDefaultInstance(); } public MysqlxResultset.ColumnMetaData build() { MysqlxResultset.ColumnMetaData result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxResultset.ColumnMetaData buildPartial() { MysqlxResultset.ColumnMetaData result = new MysqlxResultset.ColumnMetaData(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.type_ = this.type_; if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.name_ = this.name_; if ((from_bitField0_ & 0x4) != 0) to_bitField0_ |= 0x4;  result.originalName_ = this.originalName_; if ((from_bitField0_ & 0x8) != 0) to_bitField0_ |= 0x8;  result.table_ = this.table_; if ((from_bitField0_ & 0x10) != 0) to_bitField0_ |= 0x10;  result.originalTable_ = this.originalTable_; if ((from_bitField0_ & 0x20) != 0) to_bitField0_ |= 0x20;  result.schema_ = this.schema_; if ((from_bitField0_ & 0x40) != 0) to_bitField0_ |= 0x40;  result.catalog_ = this.catalog_; if ((from_bitField0_ & 0x80) != 0) { result.collation_ = this.collation_; to_bitField0_ |= 0x80; }  if ((from_bitField0_ & 0x100) != 0) { result.fractionalDigits_ = this.fractionalDigits_; to_bitField0_ |= 0x100; }  if ((from_bitField0_ & 0x200) != 0) { result.length_ = this.length_; to_bitField0_ |= 0x200; }  if ((from_bitField0_ & 0x400) != 0) { result.flags_ = this.flags_; to_bitField0_ |= 0x400; }  if ((from_bitField0_ & 0x800) != 0) { result.contentType_ = this.contentType_; to_bitField0_ |= 0x800; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); }
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); }
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); }
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); }
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); }
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); }
/*      */       public Builder mergeFrom(Message other) { if (other instanceof MysqlxResultset.ColumnMetaData) return mergeFrom((MysqlxResultset.ColumnMetaData)other);  super.mergeFrom(other); return this; }
/*      */       public Builder mergeFrom(MysqlxResultset.ColumnMetaData other) { if (other == MysqlxResultset.ColumnMetaData.getDefaultInstance()) return this;  if (other.hasType()) setType(other.getType());  if (other.hasName()) setName(other.getName());  if (other.hasOriginalName()) setOriginalName(other.getOriginalName());  if (other.hasTable()) setTable(other.getTable());  if (other.hasOriginalTable()) setOriginalTable(other.getOriginalTable());  if (other.hasSchema()) setSchema(other.getSchema());  if (other.hasCatalog()) setCatalog(other.getCatalog());  if (other.hasCollation()) setCollation(other.getCollation());  if (other.hasFractionalDigits()) setFractionalDigits(other.getFractionalDigits());  if (other.hasLength()) setLength(other.getLength());  if (other.hasFlags()) setFlags(other.getFlags());  if (other.hasContentType()) setContentType(other.getContentType());  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */       public final boolean isInitialized() { if (!hasType()) return false;  return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxResultset.ColumnMetaData parsedMessage = null; try { parsedMessage = (MysqlxResultset.ColumnMetaData)MysqlxResultset.ColumnMetaData.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxResultset.ColumnMetaData)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/*      */       public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); }
/*      */       public MysqlxResultset.ColumnMetaData.FieldType getType() { MysqlxResultset.ColumnMetaData.FieldType result = MysqlxResultset.ColumnMetaData.FieldType.valueOf(this.type_); return (result == null) ? MysqlxResultset.ColumnMetaData.FieldType.SINT : result; }
/*      */       public Builder setType(MysqlxResultset.ColumnMetaData.FieldType value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.type_ = value.getNumber(); onChanged(); return this; }
/*      */       public Builder clearType() { this.bitField0_ &= 0xFFFFFFFE; this.type_ = 1; onChanged(); return this; }
/* 4404 */       public boolean hasCatalog() { return ((this.bitField0_ & 0x40) != 0); } public boolean hasName() { return ((this.bitField0_ & 0x2) != 0); } public ByteString getName() { return this.name_; } public Builder setName(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.name_ = value; onChanged(); return this; } public Builder clearName() { this.bitField0_ &= 0xFFFFFFFD; this.name_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getName(); onChanged(); return this; } public boolean hasOriginalName() { return ((this.bitField0_ & 0x4) != 0); } public ByteString getOriginalName() { return this.originalName_; }
/*      */       public Builder setOriginalName(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x4; this.originalName_ = value; onChanged(); return this; }
/*      */       public Builder clearOriginalName() { this.bitField0_ &= 0xFFFFFFFB; this.originalName_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getOriginalName(); onChanged(); return this; }
/*      */       public boolean hasTable() { return ((this.bitField0_ & 0x8) != 0); }
/*      */       public ByteString getTable() { return this.table_; }
/*      */       public Builder setTable(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x8; this.table_ = value; onChanged(); return this; }
/*      */       public Builder clearTable() { this.bitField0_ &= 0xFFFFFFF7; this.table_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getTable(); onChanged(); return this; }
/*      */       public boolean hasOriginalTable() { return ((this.bitField0_ & 0x10) != 0); }
/*      */       public ByteString getOriginalTable() { return this.originalTable_; }
/*      */       public Builder setOriginalTable(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x10; this.originalTable_ = value; onChanged(); return this; }
/*      */       public Builder clearOriginalTable() { this.bitField0_ &= 0xFFFFFFEF; this.originalTable_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getOriginalTable(); onChanged(); return this; }
/*      */       public boolean hasSchema() { return ((this.bitField0_ & 0x20) != 0); }
/*      */       public ByteString getSchema() { return this.schema_; }
/*      */       public Builder setSchema(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x20; this.schema_ = value; onChanged(); return this; }
/*      */       public Builder clearSchema() { this.bitField0_ &= 0xFFFFFFDF; this.schema_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getSchema(); onChanged(); return this; }
/* 4419 */       public ByteString getCatalog() { return this.catalog_; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setCatalog(ByteString value) {
/* 4435 */         if (value == null) {
/* 4436 */           throw new NullPointerException();
/*      */         }
/* 4438 */         this.bitField0_ |= 0x40;
/* 4439 */         this.catalog_ = value;
/* 4440 */         onChanged();
/* 4441 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearCatalog() {
/* 4456 */         this.bitField0_ &= 0xFFFFFFBF;
/* 4457 */         this.catalog_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getCatalog();
/* 4458 */         onChanged();
/* 4459 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean hasCollation() {
/* 4468 */         return ((this.bitField0_ & 0x80) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public long getCollation() {
/* 4475 */         return this.collation_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setCollation(long value) {
/* 4483 */         this.bitField0_ |= 0x80;
/* 4484 */         this.collation_ = value;
/* 4485 */         onChanged();
/* 4486 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearCollation() {
/* 4493 */         this.bitField0_ &= 0xFFFFFF7F;
/* 4494 */         this.collation_ = 0L;
/* 4495 */         onChanged();
/* 4496 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean hasFractionalDigits() {
/* 4510 */         return ((this.bitField0_ & 0x100) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int getFractionalDigits() {
/* 4522 */         return this.fractionalDigits_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setFractionalDigits(int value) {
/* 4535 */         this.bitField0_ |= 0x100;
/* 4536 */         this.fractionalDigits_ = value;
/* 4537 */         onChanged();
/* 4538 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearFractionalDigits() {
/* 4550 */         this.bitField0_ &= 0xFFFFFEFF;
/* 4551 */         this.fractionalDigits_ = 0;
/* 4552 */         onChanged();
/* 4553 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean hasLength() {
/* 4566 */         return ((this.bitField0_ & 0x200) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int getLength() {
/* 4577 */         return this.length_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setLength(int value) {
/* 4589 */         this.bitField0_ |= 0x200;
/* 4590 */         this.length_ = value;
/* 4591 */         onChanged();
/* 4592 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearLength() {
/* 4603 */         this.bitField0_ &= 0xFFFFFDFF;
/* 4604 */         this.length_ = 0;
/* 4605 */         onChanged();
/* 4606 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean hasFlags() {
/* 4634 */         return ((this.bitField0_ & 0x400) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int getFlags() {
/* 4660 */         return this.flags_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setFlags(int value) {
/* 4687 */         this.bitField0_ |= 0x400;
/* 4688 */         this.flags_ = value;
/* 4689 */         onChanged();
/* 4690 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearFlags() {
/* 4716 */         this.bitField0_ &= 0xFFFFFBFF;
/* 4717 */         this.flags_ = 0;
/* 4718 */         onChanged();
/* 4719 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean hasContentType() {
/* 4744 */         return ((this.bitField0_ & 0x800) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int getContentType() {
/* 4767 */         return this.contentType_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setContentType(int value) {
/* 4791 */         this.bitField0_ |= 0x800;
/* 4792 */         this.contentType_ = value;
/* 4793 */         onChanged();
/* 4794 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearContentType() {
/* 4817 */         this.bitField0_ &= 0xFFFFF7FF;
/* 4818 */         this.contentType_ = 0;
/* 4819 */         onChanged();
/* 4820 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 4825 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 4831 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4841 */     private static final ColumnMetaData DEFAULT_INSTANCE = new ColumnMetaData();
/*      */ 
/*      */     
/*      */     public static ColumnMetaData getDefaultInstance() {
/* 4845 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 4849 */     public static final Parser<ColumnMetaData> PARSER = (Parser<ColumnMetaData>)new AbstractParser<ColumnMetaData>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxResultset.ColumnMetaData parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 4855 */           return new MysqlxResultset.ColumnMetaData(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<ColumnMetaData> parser() {
/* 4860 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<ColumnMetaData> getParserForType() {
/* 4865 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public ColumnMetaData getDefaultInstanceForType() {
/* 4870 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface RowOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     List<ByteString> getFieldList();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getFieldCount();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getField(int param1Int);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Row
/*      */     extends GeneratedMessageV3
/*      */     implements RowOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int FIELD_FIELD_NUMBER = 1;
/*      */ 
/*      */     
/*      */     private List<ByteString> field_;
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */     
/*      */     private Row(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 4916 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 5022 */       this.memoizedIsInitialized = -1; } private Row() { this.memoizedIsInitialized = -1; this.field_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Row(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Row(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: if ((mutable_bitField0_ & 0x1) == 0) { this.field_ = new ArrayList<>(); mutable_bitField0_ |= 0x1; }  this.field_.add(input.readBytes()); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x1) != 0) this.field_ = Collections.unmodifiableList(this.field_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_fieldAccessorTable.ensureFieldAccessorsInitialized(Row.class, Builder.class); } public List<ByteString> getFieldList() { return this.field_; }
/*      */     public int getFieldCount() { return this.field_.size(); }
/*      */     public ByteString getField(int index) { return this.field_.get(index); }
/* 5025 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 5026 */       if (isInitialized == 1) return true; 
/* 5027 */       if (isInitialized == 0) return false;
/*      */       
/* 5029 */       this.memoizedIsInitialized = 1;
/* 5030 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 5036 */       for (int i = 0; i < this.field_.size(); i++) {
/* 5037 */         output.writeBytes(1, this.field_.get(i));
/*      */       }
/* 5039 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 5044 */       int size = this.memoizedSize;
/* 5045 */       if (size != -1) return size;
/*      */       
/* 5047 */       size = 0;
/*      */       
/* 5049 */       int dataSize = 0;
/* 5050 */       for (int i = 0; i < this.field_.size(); i++) {
/* 5051 */         dataSize += 
/* 5052 */           CodedOutputStream.computeBytesSizeNoTag(this.field_.get(i));
/*      */       }
/* 5054 */       size += dataSize;
/* 5055 */       size += 1 * getFieldList().size();
/*      */       
/* 5057 */       size += this.unknownFields.getSerializedSize();
/* 5058 */       this.memoizedSize = size;
/* 5059 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 5064 */       if (obj == this) {
/* 5065 */         return true;
/*      */       }
/* 5067 */       if (!(obj instanceof Row)) {
/* 5068 */         return super.equals(obj);
/*      */       }
/* 5070 */       Row other = (Row)obj;
/*      */ 
/*      */       
/* 5073 */       if (!getFieldList().equals(other.getFieldList())) return false; 
/* 5074 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 5075 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 5080 */       if (this.memoizedHashCode != 0) {
/* 5081 */         return this.memoizedHashCode;
/*      */       }
/* 5083 */       int hash = 41;
/* 5084 */       hash = 19 * hash + getDescriptor().hashCode();
/* 5085 */       if (getFieldCount() > 0) {
/* 5086 */         hash = 37 * hash + 1;
/* 5087 */         hash = 53 * hash + getFieldList().hashCode();
/*      */       } 
/* 5089 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 5090 */       this.memoizedHashCode = hash;
/* 5091 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Row parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 5097 */       return (Row)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Row parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 5103 */       return (Row)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Row parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 5108 */       return (Row)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Row parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 5114 */       return (Row)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Row parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 5118 */       return (Row)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Row parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 5124 */       return (Row)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Row parseFrom(InputStream input) throws IOException {
/* 5128 */       return 
/* 5129 */         (Row)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Row parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 5135 */       return 
/* 5136 */         (Row)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Row parseDelimitedFrom(InputStream input) throws IOException {
/* 5140 */       return 
/* 5141 */         (Row)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Row parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 5147 */       return 
/* 5148 */         (Row)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Row parseFrom(CodedInputStream input) throws IOException {
/* 5153 */       return 
/* 5154 */         (Row)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Row parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 5160 */       return 
/* 5161 */         (Row)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 5165 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 5167 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Row prototype) {
/* 5170 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 5174 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 5175 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 5181 */       Builder builder = new Builder(parent);
/* 5182 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxResultset.RowOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */ 
/*      */       
/*      */       private List<ByteString> field_;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 5203 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 5209 */         return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_fieldAccessorTable
/* 5210 */           .ensureFieldAccessorsInitialized(MysqlxResultset.Row.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder()
/*      */       {
/* 5354 */         this.field_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.field_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxResultset.Row.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.field_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_descriptor; } public MysqlxResultset.Row getDefaultInstanceForType() { return MysqlxResultset.Row.getDefaultInstance(); } public MysqlxResultset.Row build() { MysqlxResultset.Row result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxResultset.Row buildPartial() { MysqlxResultset.Row result = new MysqlxResultset.Row(this); int from_bitField0_ = this.bitField0_; if ((this.bitField0_ & 0x1) != 0) { this.field_ = Collections.unmodifiableList(this.field_); this.bitField0_ &= 0xFFFFFFFE; }  result.field_ = this.field_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxResultset.Row) return mergeFrom((MysqlxResultset.Row)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxResultset.Row other) { if (other == MysqlxResultset.Row.getDefaultInstance()) return this;  if (!other.field_.isEmpty()) { if (this.field_.isEmpty()) { this.field_ = other.field_; this.bitField0_ &= 0xFFFFFFFE; } else { ensureFieldIsMutable(); this.field_.addAll(other.field_); }  onChanged(); }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxResultset.Row parsedMessage = null; try { parsedMessage = (MysqlxResultset.Row)MysqlxResultset.Row.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxResultset.Row)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/* 5356 */       private void ensureFieldIsMutable() { if ((this.bitField0_ & 0x1) == 0) {
/* 5357 */           this.field_ = new ArrayList<>(this.field_);
/* 5358 */           this.bitField0_ |= 0x1;
/*      */         }  }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<ByteString> getFieldList() {
/* 5367 */         return ((this.bitField0_ & 0x1) != 0) ? 
/* 5368 */           Collections.<ByteString>unmodifiableList(this.field_) : this.field_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int getFieldCount() {
/* 5375 */         return this.field_.size();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public ByteString getField(int index) {
/* 5383 */         return this.field_.get(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(int index, ByteString value) {
/* 5393 */         if (value == null) {
/* 5394 */           throw new NullPointerException();
/*      */         }
/* 5396 */         ensureFieldIsMutable();
/* 5397 */         this.field_.set(index, value);
/* 5398 */         onChanged();
/* 5399 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addField(ByteString value) {
/* 5407 */         if (value == null) {
/* 5408 */           throw new NullPointerException();
/*      */         }
/* 5410 */         ensureFieldIsMutable();
/* 5411 */         this.field_.add(value);
/* 5412 */         onChanged();
/* 5413 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addAllField(Iterable<? extends ByteString> values) {
/* 5422 */         ensureFieldIsMutable();
/* 5423 */         AbstractMessageLite.Builder.addAll(values, this.field_);
/*      */         
/* 5425 */         onChanged();
/* 5426 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearField() {
/* 5433 */         this.field_ = Collections.emptyList();
/* 5434 */         this.bitField0_ &= 0xFFFFFFFE;
/* 5435 */         onChanged();
/* 5436 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 5441 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 5447 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5457 */     private static final Row DEFAULT_INSTANCE = new Row();
/*      */ 
/*      */     
/*      */     public static Row getDefaultInstance() {
/* 5461 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 5465 */     public static final Parser<Row> PARSER = (Parser<Row>)new AbstractParser<Row>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxResultset.Row parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 5471 */           return new MysqlxResultset.Row(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Row> parser() {
/* 5476 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Row> getParserForType() {
/* 5481 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Row getDefaultInstanceForType() {
/* 5486 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Descriptors.FileDescriptor getDescriptor() {
/* 5524 */     return descriptor;
/*      */   }
/*      */ 
/*      */   
/*      */   static {
/* 5529 */     String[] descriptorData = { "\n\026mysqlx_resultset.proto\022\020Mysqlx.Resultset\032\fmysqlx.proto\"\036\n\026FetchDoneMoreOutParams:\004ê0\022\"\037\n\027FetchDoneMoreResultsets:\004ê0\020\"\021\n\tFetchDone:\004ê0\016\"\026\n\016FetchSuspended:\004ê0\017\"¥\003\n\016ColumnMetaData\0228\n\004type\030\001 \002(\0162*.Mysqlx.Resultset.ColumnMetaData.FieldType\022\f\n\004name\030\002 \001(\f\022\025\n\roriginal_name\030\003 \001(\f\022\r\n\005table\030\004 \001(\f\022\026\n\016original_table\030\005 \001(\f\022\016\n\006schema\030\006 \001(\f\022\017\n\007catalog\030\007 \001(\f\022\021\n\tcollation\030\b \001(\004\022\031\n\021fractional_digits\030\t \001(\r\022\016\n\006length\030\n \001(\r\022\r\n\005flags\030\013 \001(\r\022\024\n\fcontent_type\030\f \001(\r\"\001\n\tFieldType\022\b\n\004SINT\020\001\022\b\n\004UINT\020\002\022\n\n\006DOUBLE\020\005\022\t\n\005FLOAT\020\006\022\t\n\005BYTES\020\007\022\b\n\004TIME\020\n\022\f\n\bDATETIME\020\f\022\007\n\003SET\020\017\022\b\n\004ENUM\020\020\022\007\n\003BIT\020\021\022\013\n\007DECIMAL\020\022:\004ê0\f\"\032\n\003Row\022\r\n\005field\030\001 \003(\f:\004ê0\r*4\n\021ContentType_BYTES\022\f\n\bGEOMETRY\020\001\022\b\n\004JSON\020\002\022\007\n\003XML\020\003*.\n\024ContentType_DATETIME\022\b\n\004DATE\020\001\022\f\n\bDATETIME\020\002B\031\n\027com.mysql.cj.x.protobuf" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5551 */     descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] {
/*      */           
/* 5553 */           Mysqlx.getDescriptor()
/*      */         });
/*      */     
/* 5556 */     internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor = getDescriptor().getMessageTypes().get(0);
/* 5557 */     internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor, new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5562 */     internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor = getDescriptor().getMessageTypes().get(1);
/* 5563 */     internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor, new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5568 */     internal_static_Mysqlx_Resultset_FetchDone_descriptor = getDescriptor().getMessageTypes().get(2);
/* 5569 */     internal_static_Mysqlx_Resultset_FetchDone_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_FetchDone_descriptor, new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5574 */     internal_static_Mysqlx_Resultset_FetchSuspended_descriptor = getDescriptor().getMessageTypes().get(3);
/* 5575 */     internal_static_Mysqlx_Resultset_FetchSuspended_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_FetchSuspended_descriptor, new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5580 */     internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor = getDescriptor().getMessageTypes().get(4);
/* 5581 */     internal_static_Mysqlx_Resultset_ColumnMetaData_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor, new String[] { "Type", "Name", "OriginalName", "Table", "OriginalTable", "Schema", "Catalog", "Collation", "FractionalDigits", "Length", "Flags", "ContentType" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5586 */     internal_static_Mysqlx_Resultset_Row_descriptor = getDescriptor().getMessageTypes().get(5);
/* 5587 */     internal_static_Mysqlx_Resultset_Row_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Resultset_Row_descriptor, new String[] { "Field" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5592 */     ExtensionRegistry registry = ExtensionRegistry.newInstance();
/* 5593 */     registry.add(Mysqlx.serverMessageId);
/*      */     
/* 5595 */     Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
/* 5596 */     Mysqlx.getDescriptor();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\x\protobuf\MysqlxResultset.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */