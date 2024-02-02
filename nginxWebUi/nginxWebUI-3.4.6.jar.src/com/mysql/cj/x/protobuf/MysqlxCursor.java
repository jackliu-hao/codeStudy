/*      */ package com.mysql.cj.x.protobuf;
/*      */ 
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
/*      */ import com.google.protobuf.ProtocolMessageEnum;
/*      */ import com.google.protobuf.SingleFieldBuilderV3;
/*      */ import com.google.protobuf.UnknownFieldSet;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class MysqlxCursor
/*      */ {
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Cursor_Open_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Cursor_Open_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Cursor_Open_OneOfMessage_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Cursor_Fetch_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Cursor_Fetch_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Cursor_Close_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Cursor_Close_fieldAccessorTable;
/*      */   private static Descriptors.FileDescriptor descriptor;
/*      */   
/*      */   public static void registerAllExtensions(ExtensionRegistryLite registry) {}
/*      */   
/*      */   public static void registerAllExtensions(ExtensionRegistry registry) {
/*   44 */     registerAllExtensions((ExtensionRegistryLite)registry);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface OpenOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasCursorId();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getCursorId();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasStmt();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxCursor.Open.OneOfMessage getStmt();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxCursor.Open.OneOfMessageOrBuilder getStmtOrBuilder();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasFetchRows();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     long getFetchRows();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Open
/*      */     extends GeneratedMessageV3
/*      */     implements OpenOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int CURSOR_ID_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int cursorId_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int STMT_FIELD_NUMBER = 4;
/*      */ 
/*      */ 
/*      */     
/*      */     private OneOfMessage stmt_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int FETCH_ROWS_FIELD_NUMBER = 5;
/*      */ 
/*      */ 
/*      */     
/*      */     private long fetchRows_;
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Open(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/*  143 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1165 */       this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Open(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Open(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { OneOfMessage.Builder subBuilder; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.bitField0_ |= 0x1; this.cursorId_ = input.readUInt32(); continue;case 34: subBuilder = null; if ((this.bitField0_ & 0x2) != 0) subBuilder = this.stmt_.toBuilder();  this.stmt_ = (OneOfMessage)input.readMessage(OneOfMessage.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.stmt_); this.stmt_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x2; continue;case 40: this.bitField0_ |= 0x4; this.fetchRows_ = input.readUInt64(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_descriptor; } private Open() { this.memoizedIsInitialized = -1; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_fieldAccessorTable.ensureFieldAccessorsInitialized(Open.class, Builder.class); } public static final class OneOfMessage extends GeneratedMessageV3 implements OneOfMessageOrBuilder {
/*      */       private static final long serialVersionUID = 0L; private int bitField0_; public static final int TYPE_FIELD_NUMBER = 1; private int type_; public static final int PREPARE_EXECUTE_FIELD_NUMBER = 2; private MysqlxPrepare.Execute prepareExecute_; private byte memoizedIsInitialized; private OneOfMessage(GeneratedMessageV3.Builder<?> builder) { super(builder); this.memoizedIsInitialized = -1; } private OneOfMessage() { this.memoizedIsInitialized = -1; this.type_ = 0; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new OneOfMessage(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private OneOfMessage(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int rawValue; MysqlxPrepare.Execute.Builder subBuilder; Type value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: rawValue = input.readEnum(); value = Type.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(1, rawValue); continue; }  this.bitField0_ |= 0x1; this.type_ = rawValue; continue;case 18: subBuilder = null; if ((this.bitField0_ & 0x2) != 0) subBuilder = this.prepareExecute_.toBuilder();  this.prepareExecute_ = (MysqlxPrepare.Execute)input.readMessage(MysqlxPrepare.Execute.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.prepareExecute_); this.prepareExecute_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x2; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_fieldAccessorTable.ensureFieldAccessorsInitialized(OneOfMessage.class, Builder.class); } public enum Type implements ProtocolMessageEnum {
/*      */         PREPARE_EXECUTE(0); private final int value; private static final Type[] VALUES = values(); private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() { public MysqlxCursor.Open.OneOfMessage.Type findValueByNumber(int number) { return MysqlxCursor.Open.OneOfMessage.Type.forNumber(number); } }; public static final int PREPARE_EXECUTE_VALUE = 0; public final int getNumber() { return this.value; } public static Type forNumber(int value) { switch (value) { case 0: return PREPARE_EXECUTE; }  return null; } public static Internal.EnumLiteMap<Type> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxCursor.Open.OneOfMessage.getDescriptor().getEnumTypes().get(0); } Type(int value) { this.value = value; } } public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); } public Type getType() { Type result = Type.valueOf(this.type_); return (result == null) ? Type.PREPARE_EXECUTE : result; } public boolean hasPrepareExecute() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxPrepare.Execute getPrepareExecute() { return (this.prepareExecute_ == null) ? MysqlxPrepare.Execute.getDefaultInstance() : this.prepareExecute_; } public MysqlxPrepare.ExecuteOrBuilder getPrepareExecuteOrBuilder() { return (this.prepareExecute_ == null) ? MysqlxPrepare.Execute.getDefaultInstance() : this.prepareExecute_; } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized; if (isInitialized == 1) return true;  if (isInitialized == 0) return false;  if (!hasType()) { this.memoizedIsInitialized = 0; return false; }  if (hasPrepareExecute() && !getPrepareExecute().isInitialized()) { this.memoizedIsInitialized = 0; return false; }  this.memoizedIsInitialized = 1; return true; } public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) output.writeEnum(1, this.type_);  if ((this.bitField0_ & 0x2) != 0) output.writeMessage(2, (MessageLite)getPrepareExecute());  this.unknownFields.writeTo(output); } public int getSerializedSize() { int size = this.memoizedSize; if (size != -1) return size;  size = 0; if ((this.bitField0_ & 0x1) != 0) size += CodedOutputStream.computeEnumSize(1, this.type_);  if ((this.bitField0_ & 0x2) != 0) size += CodedOutputStream.computeMessageSize(2, (MessageLite)getPrepareExecute());  size += this.unknownFields.getSerializedSize(); this.memoizedSize = size; return size; } public boolean equals(Object obj) { if (obj == this) return true;  if (!(obj instanceof OneOfMessage)) return super.equals(obj);  OneOfMessage other = (OneOfMessage)obj; if (hasType() != other.hasType()) return false;  if (hasType() && this.type_ != other.type_) return false;  if (hasPrepareExecute() != other.hasPrepareExecute()) return false;  if (hasPrepareExecute() && !getPrepareExecute().equals(other.getPrepareExecute())) return false;  if (!this.unknownFields.equals(other.unknownFields)) return false;  return true; } public int hashCode() { if (this.memoizedHashCode != 0) return this.memoizedHashCode;  int hash = 41; hash = 19 * hash + getDescriptor().hashCode(); if (hasType()) { hash = 37 * hash + 1; hash = 53 * hash + this.type_; }  if (hasPrepareExecute()) { hash = 37 * hash + 2; hash = 53 * hash + getPrepareExecute().hashCode(); }  hash = 29 * hash + this.unknownFields.hashCode(); this.memoizedHashCode = hash; return hash; } public static OneOfMessage parseFrom(ByteBuffer data) throws InvalidProtocolBufferException { return (OneOfMessage)PARSER.parseFrom(data); } public static OneOfMessage parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (OneOfMessage)PARSER.parseFrom(data, extensionRegistry); } public static OneOfMessage parseFrom(ByteString data) throws InvalidProtocolBufferException { return (OneOfMessage)PARSER.parseFrom(data); } public static OneOfMessage parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (OneOfMessage)PARSER.parseFrom(data, extensionRegistry); } public static OneOfMessage parseFrom(byte[] data) throws InvalidProtocolBufferException { return (OneOfMessage)PARSER.parseFrom(data); } public static OneOfMessage parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (OneOfMessage)PARSER.parseFrom(data, extensionRegistry); } public static OneOfMessage parseFrom(InputStream input) throws IOException { return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static OneOfMessage parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public static OneOfMessage parseDelimitedFrom(InputStream input) throws IOException { return (OneOfMessage)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input); } public static OneOfMessage parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (OneOfMessage)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry); } public static OneOfMessage parseFrom(CodedInputStream input) throws IOException { return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static OneOfMessage parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public Builder newBuilderForType() { return newBuilder(); } public static Builder newBuilder() { return DEFAULT_INSTANCE.toBuilder(); } public static Builder newBuilder(OneOfMessage prototype) { return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype); } public Builder toBuilder() { return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder()).mergeFrom(this); } protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) { Builder builder = new Builder(parent); return builder; } public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCursor.Open.OneOfMessageOrBuilder {
/* 1168 */         private int bitField0_; private int type_; private MysqlxPrepare.Execute prepareExecute_; private SingleFieldBuilderV3<MysqlxPrepare.Execute, MysqlxPrepare.Execute.Builder, MysqlxPrepare.ExecuteOrBuilder> prepareExecuteBuilder_; public static final Descriptors.Descriptor getDescriptor() { return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxCursor.Open.OneOfMessage.class, Builder.class); } private Builder() { this.type_ = 0; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.type_ = 0; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxCursor.Open.OneOfMessage.alwaysUseFieldBuilders) getPrepareExecuteFieldBuilder();  } public Builder clear() { super.clear(); this.type_ = 0; this.bitField0_ &= 0xFFFFFFFE; if (this.prepareExecuteBuilder_ == null) { this.prepareExecute_ = null; } else { this.prepareExecuteBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor; } public MysqlxCursor.Open.OneOfMessage getDefaultInstanceForType() { return MysqlxCursor.Open.OneOfMessage.getDefaultInstance(); } public MysqlxCursor.Open.OneOfMessage build() { MysqlxCursor.Open.OneOfMessage result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxCursor.Open.OneOfMessage buildPartial() { MysqlxCursor.Open.OneOfMessage result = new MysqlxCursor.Open.OneOfMessage(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.type_ = this.type_; if ((from_bitField0_ & 0x2) != 0) { if (this.prepareExecuteBuilder_ == null) { result.prepareExecute_ = this.prepareExecute_; } else { result.prepareExecute_ = (MysqlxPrepare.Execute)this.prepareExecuteBuilder_.build(); }  to_bitField0_ |= 0x2; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxCursor.Open.OneOfMessage) return mergeFrom((MysqlxCursor.Open.OneOfMessage)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxCursor.Open.OneOfMessage other) { if (other == MysqlxCursor.Open.OneOfMessage.getDefaultInstance()) return this;  if (other.hasType()) setType(other.getType());  if (other.hasPrepareExecute()) mergePrepareExecute(other.getPrepareExecute());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasType()) return false;  if (hasPrepareExecute() && !getPrepareExecute().isInitialized()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxCursor.Open.OneOfMessage parsedMessage = null; try { parsedMessage = (MysqlxCursor.Open.OneOfMessage)MysqlxCursor.Open.OneOfMessage.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxCursor.Open.OneOfMessage)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxCursor.Open.OneOfMessage.Type getType() { MysqlxCursor.Open.OneOfMessage.Type result = MysqlxCursor.Open.OneOfMessage.Type.valueOf(this.type_); return (result == null) ? MysqlxCursor.Open.OneOfMessage.Type.PREPARE_EXECUTE : result; } public Builder setType(MysqlxCursor.Open.OneOfMessage.Type value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.type_ = value.getNumber(); onChanged(); return this; } public Builder clearType() { this.bitField0_ &= 0xFFFFFFFE; this.type_ = 0; onChanged(); return this; } public boolean hasPrepareExecute() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxPrepare.Execute getPrepareExecute() { if (this.prepareExecuteBuilder_ == null) return (this.prepareExecute_ == null) ? MysqlxPrepare.Execute.getDefaultInstance() : this.prepareExecute_;  return (MysqlxPrepare.Execute)this.prepareExecuteBuilder_.getMessage(); } public Builder setPrepareExecute(MysqlxPrepare.Execute value) { if (this.prepareExecuteBuilder_ == null) { if (value == null) throw new NullPointerException();  this.prepareExecute_ = value; onChanged(); } else { this.prepareExecuteBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x2; return this; } public Builder setPrepareExecute(MysqlxPrepare.Execute.Builder builderForValue) { if (this.prepareExecuteBuilder_ == null) { this.prepareExecute_ = builderForValue.build(); onChanged(); } else { this.prepareExecuteBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x2; return this; } public Builder mergePrepareExecute(MysqlxPrepare.Execute value) { if (this.prepareExecuteBuilder_ == null) { if ((this.bitField0_ & 0x2) != 0 && this.prepareExecute_ != null && this.prepareExecute_ != MysqlxPrepare.Execute.getDefaultInstance()) { this.prepareExecute_ = MysqlxPrepare.Execute.newBuilder(this.prepareExecute_).mergeFrom(value).buildPartial(); } else { this.prepareExecute_ = value; }  onChanged(); } else { this.prepareExecuteBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x2; return this; } public Builder clearPrepareExecute() { if (this.prepareExecuteBuilder_ == null) { this.prepareExecute_ = null; onChanged(); } else { this.prepareExecuteBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; return this; } public MysqlxPrepare.Execute.Builder getPrepareExecuteBuilder() { this.bitField0_ |= 0x2; onChanged(); return (MysqlxPrepare.Execute.Builder)getPrepareExecuteFieldBuilder().getBuilder(); } public MysqlxPrepare.ExecuteOrBuilder getPrepareExecuteOrBuilder() { if (this.prepareExecuteBuilder_ != null) return (MysqlxPrepare.ExecuteOrBuilder)this.prepareExecuteBuilder_.getMessageOrBuilder();  return (this.prepareExecute_ == null) ? MysqlxPrepare.Execute.getDefaultInstance() : this.prepareExecute_; } private SingleFieldBuilderV3<MysqlxPrepare.Execute, MysqlxPrepare.Execute.Builder, MysqlxPrepare.ExecuteOrBuilder> getPrepareExecuteFieldBuilder() { if (this.prepareExecuteBuilder_ == null) { this.prepareExecuteBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getPrepareExecute(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.prepareExecute_ = null; }  return this.prepareExecuteBuilder_; } public final Builder setUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.setUnknownFields(unknownFields); } public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.mergeUnknownFields(unknownFields); } } private static final OneOfMessage DEFAULT_INSTANCE = new OneOfMessage(); public static OneOfMessage getDefaultInstance() { return DEFAULT_INSTANCE; } @Deprecated public static final Parser<OneOfMessage> PARSER = (Parser<OneOfMessage>)new AbstractParser<OneOfMessage>() { public MysqlxCursor.Open.OneOfMessage parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return new MysqlxCursor.Open.OneOfMessage(input, extensionRegistry); } }; public static Parser<OneOfMessage> parser() { return PARSER; } public Parser<OneOfMessage> getParserForType() { return PARSER; } public OneOfMessage getDefaultInstanceForType() { return DEFAULT_INSTANCE; } } public static final class Builder extends GeneratedMessageV3.Builder<OneOfMessage.Builder> implements OneOfMessageOrBuilder { private int bitField0_; private int type_; private MysqlxPrepare.Execute prepareExecute_; private SingleFieldBuilderV3<MysqlxPrepare.Execute, MysqlxPrepare.Execute.Builder, MysqlxPrepare.ExecuteOrBuilder> prepareExecuteBuilder_; public static final Descriptors.Descriptor getDescriptor() { return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxCursor.Open.OneOfMessage.class, Builder.class); } private Builder() { this.type_ = 0; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.type_ = 0; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxCursor.Open.OneOfMessage.alwaysUseFieldBuilders) getPrepareExecuteFieldBuilder();  } public Builder clear() { super.clear(); this.type_ = 0; this.bitField0_ &= 0xFFFFFFFE; if (this.prepareExecuteBuilder_ == null) { this.prepareExecute_ = null; } else { this.prepareExecuteBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor; } public MysqlxCursor.Open.OneOfMessage getDefaultInstanceForType() { return MysqlxCursor.Open.OneOfMessage.getDefaultInstance(); } public MysqlxCursor.Open.OneOfMessage build() { MysqlxCursor.Open.OneOfMessage result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxCursor.Open.OneOfMessage buildPartial() { MysqlxCursor.Open.OneOfMessage result = new MysqlxCursor.Open.OneOfMessage(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.type_ = this.type_; if ((from_bitField0_ & 0x2) != 0) { if (this.prepareExecuteBuilder_ == null) { result.prepareExecute_ = this.prepareExecute_; } else { result.prepareExecute_ = (MysqlxPrepare.Execute)this.prepareExecuteBuilder_.build(); }  to_bitField0_ |= 0x2; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxCursor.Open.OneOfMessage) return mergeFrom((MysqlxCursor.Open.OneOfMessage)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxCursor.Open.OneOfMessage other) { if (other == MysqlxCursor.Open.OneOfMessage.getDefaultInstance()) return this;  if (other.hasType()) setType(other.getType());  if (other.hasPrepareExecute()) mergePrepareExecute(other.getPrepareExecute());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasType()) return false;  if (hasPrepareExecute() && !getPrepareExecute().isInitialized()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxCursor.Open.OneOfMessage parsedMessage = null; try { parsedMessage = (MysqlxCursor.Open.OneOfMessage)MysqlxCursor.Open.OneOfMessage.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxCursor.Open.OneOfMessage)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxCursor.Open.OneOfMessage.Type getType() { MysqlxCursor.Open.OneOfMessage.Type result = MysqlxCursor.Open.OneOfMessage.Type.valueOf(this.type_); return (result == null) ? MysqlxCursor.Open.OneOfMessage.Type.PREPARE_EXECUTE : result; } public Builder setType(MysqlxCursor.Open.OneOfMessage.Type value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.type_ = value.getNumber(); onChanged(); return this; } public Builder clearType() { this.bitField0_ &= 0xFFFFFFFE; this.type_ = 0; onChanged(); return this; } public boolean hasPrepareExecute() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxPrepare.Execute getPrepareExecute() { if (this.prepareExecuteBuilder_ == null) return (this.prepareExecute_ == null) ? MysqlxPrepare.Execute.getDefaultInstance() : this.prepareExecute_;  return (MysqlxPrepare.Execute)this.prepareExecuteBuilder_.getMessage(); } public Builder setPrepareExecute(MysqlxPrepare.Execute value) { if (this.prepareExecuteBuilder_ == null) { if (value == null) throw new NullPointerException();  this.prepareExecute_ = value; onChanged(); } else { this.prepareExecuteBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x2; return this; } public Builder setPrepareExecute(MysqlxPrepare.Execute.Builder builderForValue) { if (this.prepareExecuteBuilder_ == null) { this.prepareExecute_ = builderForValue.build(); onChanged(); } else { this.prepareExecuteBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x2; return this; } public Builder mergePrepareExecute(MysqlxPrepare.Execute value) { if (this.prepareExecuteBuilder_ == null) { if ((this.bitField0_ & 0x2) != 0 && this.prepareExecute_ != null && this.prepareExecute_ != MysqlxPrepare.Execute.getDefaultInstance()) { this.prepareExecute_ = MysqlxPrepare.Execute.newBuilder(this.prepareExecute_).mergeFrom(value).buildPartial(); } else { this.prepareExecute_ = value; }  onChanged(); } else { this.prepareExecuteBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x2; return this; } public Builder clearPrepareExecute() { if (this.prepareExecuteBuilder_ == null) { this.prepareExecute_ = null; onChanged(); } else { this.prepareExecuteBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; return this; } public MysqlxPrepare.Execute.Builder getPrepareExecuteBuilder() { this.bitField0_ |= 0x2; onChanged(); return (MysqlxPrepare.Execute.Builder)getPrepareExecuteFieldBuilder().getBuilder(); } public MysqlxPrepare.ExecuteOrBuilder getPrepareExecuteOrBuilder() { if (this.prepareExecuteBuilder_ != null) return (MysqlxPrepare.ExecuteOrBuilder)this.prepareExecuteBuilder_.getMessageOrBuilder();  return (this.prepareExecute_ == null) ? MysqlxPrepare.Execute.getDefaultInstance() : this.prepareExecute_; } private SingleFieldBuilderV3<MysqlxPrepare.Execute, MysqlxPrepare.Execute.Builder, MysqlxPrepare.ExecuteOrBuilder> getPrepareExecuteFieldBuilder() { if (this.prepareExecuteBuilder_ == null) { this.prepareExecuteBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getPrepareExecute(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.prepareExecute_ = null; }  return this.prepareExecuteBuilder_; } public final Builder setUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.setUnknownFields(unknownFields); } public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.mergeUnknownFields(unknownFields); } } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 1169 */       if (isInitialized == 1) return true; 
/* 1170 */       if (isInitialized == 0) return false;
/*      */       
/* 1172 */       if (!hasCursorId()) {
/* 1173 */         this.memoizedIsInitialized = 0;
/* 1174 */         return false;
/*      */       } 
/* 1176 */       if (!hasStmt()) {
/* 1177 */         this.memoizedIsInitialized = 0;
/* 1178 */         return false;
/*      */       } 
/* 1180 */       if (!getStmt().isInitialized()) {
/* 1181 */         this.memoizedIsInitialized = 0;
/* 1182 */         return false;
/*      */       } 
/* 1184 */       this.memoizedIsInitialized = 1;
/* 1185 */       return true; } public boolean hasCursorId() { return ((this.bitField0_ & 0x1) != 0); } public int getCursorId() { return this.cursorId_; }
/*      */     public boolean hasStmt() { return ((this.bitField0_ & 0x2) != 0); }
/*      */     public OneOfMessage getStmt() { return (this.stmt_ == null) ? OneOfMessage.getDefaultInstance() : this.stmt_; }
/*      */     public OneOfMessageOrBuilder getStmtOrBuilder() { return (this.stmt_ == null) ? OneOfMessage.getDefaultInstance() : this.stmt_; }
/*      */     public boolean hasFetchRows() { return ((this.bitField0_ & 0x4) != 0); }
/*      */     public long getFetchRows() { return this.fetchRows_; }
/* 1191 */     public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) {
/* 1192 */         output.writeUInt32(1, this.cursorId_);
/*      */       }
/* 1194 */       if ((this.bitField0_ & 0x2) != 0) {
/* 1195 */         output.writeMessage(4, (MessageLite)getStmt());
/*      */       }
/* 1197 */       if ((this.bitField0_ & 0x4) != 0) {
/* 1198 */         output.writeUInt64(5, this.fetchRows_);
/*      */       }
/* 1200 */       this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 1205 */       int size = this.memoizedSize;
/* 1206 */       if (size != -1) return size;
/*      */       
/* 1208 */       size = 0;
/* 1209 */       if ((this.bitField0_ & 0x1) != 0) {
/* 1210 */         size += 
/* 1211 */           CodedOutputStream.computeUInt32Size(1, this.cursorId_);
/*      */       }
/* 1213 */       if ((this.bitField0_ & 0x2) != 0) {
/* 1214 */         size += 
/* 1215 */           CodedOutputStream.computeMessageSize(4, (MessageLite)getStmt());
/*      */       }
/* 1217 */       if ((this.bitField0_ & 0x4) != 0) {
/* 1218 */         size += 
/* 1219 */           CodedOutputStream.computeUInt64Size(5, this.fetchRows_);
/*      */       }
/* 1221 */       size += this.unknownFields.getSerializedSize();
/* 1222 */       this.memoizedSize = size;
/* 1223 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1228 */       if (obj == this) {
/* 1229 */         return true;
/*      */       }
/* 1231 */       if (!(obj instanceof Open)) {
/* 1232 */         return super.equals(obj);
/*      */       }
/* 1234 */       Open other = (Open)obj;
/*      */       
/* 1236 */       if (hasCursorId() != other.hasCursorId()) return false; 
/* 1237 */       if (hasCursorId() && 
/* 1238 */         getCursorId() != other
/* 1239 */         .getCursorId()) return false;
/*      */       
/* 1241 */       if (hasStmt() != other.hasStmt()) return false; 
/* 1242 */       if (hasStmt() && 
/*      */         
/* 1244 */         !getStmt().equals(other.getStmt())) return false;
/*      */       
/* 1246 */       if (hasFetchRows() != other.hasFetchRows()) return false; 
/* 1247 */       if (hasFetchRows() && 
/* 1248 */         getFetchRows() != other
/* 1249 */         .getFetchRows()) return false;
/*      */       
/* 1251 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 1252 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1257 */       if (this.memoizedHashCode != 0) {
/* 1258 */         return this.memoizedHashCode;
/*      */       }
/* 1260 */       int hash = 41;
/* 1261 */       hash = 19 * hash + getDescriptor().hashCode();
/* 1262 */       if (hasCursorId()) {
/* 1263 */         hash = 37 * hash + 1;
/* 1264 */         hash = 53 * hash + getCursorId();
/*      */       } 
/* 1266 */       if (hasStmt()) {
/* 1267 */         hash = 37 * hash + 4;
/* 1268 */         hash = 53 * hash + getStmt().hashCode();
/*      */       } 
/* 1270 */       if (hasFetchRows()) {
/* 1271 */         hash = 37 * hash + 5;
/* 1272 */         hash = 53 * hash + Internal.hashLong(
/* 1273 */             getFetchRows());
/*      */       } 
/* 1275 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 1276 */       this.memoizedHashCode = hash;
/* 1277 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Open parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 1283 */       return (Open)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Open parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1289 */       return (Open)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Open parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 1294 */       return (Open)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Open parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1300 */       return (Open)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Open parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 1304 */       return (Open)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Open parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1310 */       return (Open)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Open parseFrom(InputStream input) throws IOException {
/* 1314 */       return 
/* 1315 */         (Open)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Open parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1321 */       return 
/* 1322 */         (Open)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Open parseDelimitedFrom(InputStream input) throws IOException {
/* 1326 */       return 
/* 1327 */         (Open)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Open parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1333 */       return 
/* 1334 */         (Open)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Open parseFrom(CodedInputStream input) throws IOException {
/* 1339 */       return 
/* 1340 */         (Open)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Open parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1346 */       return 
/* 1347 */         (Open)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 1351 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 1353 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Open prototype) {
/* 1356 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 1360 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 1361 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 1367 */       Builder builder = new Builder(parent);
/* 1368 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxCursor.OpenOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */       
/*      */       private int cursorId_;
/*      */ 
/*      */       
/*      */       private MysqlxCursor.Open.OneOfMessage stmt_;
/*      */ 
/*      */       
/*      */       private SingleFieldBuilderV3<MysqlxCursor.Open.OneOfMessage, MysqlxCursor.Open.OneOfMessage.Builder, MysqlxCursor.Open.OneOfMessageOrBuilder> stmtBuilder_;
/*      */ 
/*      */       
/*      */       private long fetchRows_;
/*      */ 
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 1394 */         return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 1400 */         return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_fieldAccessorTable
/* 1401 */           .ensureFieldAccessorsInitialized(MysqlxCursor.Open.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 1407 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 1412 */         super(parent);
/* 1413 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 1417 */         if (MysqlxCursor.Open.alwaysUseFieldBuilders) {
/* 1418 */           getStmtFieldBuilder();
/*      */         }
/*      */       }
/*      */       
/*      */       public Builder clear() {
/* 1423 */         super.clear();
/* 1424 */         this.cursorId_ = 0;
/* 1425 */         this.bitField0_ &= 0xFFFFFFFE;
/* 1426 */         if (this.stmtBuilder_ == null) {
/* 1427 */           this.stmt_ = null;
/*      */         } else {
/* 1429 */           this.stmtBuilder_.clear();
/*      */         } 
/* 1431 */         this.bitField0_ &= 0xFFFFFFFD;
/* 1432 */         this.fetchRows_ = 0L;
/* 1433 */         this.bitField0_ &= 0xFFFFFFFB;
/* 1434 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 1440 */         return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxCursor.Open getDefaultInstanceForType() {
/* 1445 */         return MysqlxCursor.Open.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxCursor.Open build() {
/* 1450 */         MysqlxCursor.Open result = buildPartial();
/* 1451 */         if (!result.isInitialized()) {
/* 1452 */           throw newUninitializedMessageException(result);
/*      */         }
/* 1454 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxCursor.Open buildPartial() {
/* 1459 */         MysqlxCursor.Open result = new MysqlxCursor.Open(this);
/* 1460 */         int from_bitField0_ = this.bitField0_;
/* 1461 */         int to_bitField0_ = 0;
/* 1462 */         if ((from_bitField0_ & 0x1) != 0) {
/* 1463 */           result.cursorId_ = this.cursorId_;
/* 1464 */           to_bitField0_ |= 0x1;
/*      */         } 
/* 1466 */         if ((from_bitField0_ & 0x2) != 0) {
/* 1467 */           if (this.stmtBuilder_ == null) {
/* 1468 */             result.stmt_ = this.stmt_;
/*      */           } else {
/* 1470 */             result.stmt_ = (MysqlxCursor.Open.OneOfMessage)this.stmtBuilder_.build();
/*      */           } 
/* 1472 */           to_bitField0_ |= 0x2;
/*      */         } 
/* 1474 */         if ((from_bitField0_ & 0x4) != 0) {
/* 1475 */           result.fetchRows_ = this.fetchRows_;
/* 1476 */           to_bitField0_ |= 0x4;
/*      */         } 
/* 1478 */         result.bitField0_ = to_bitField0_;
/* 1479 */         onBuilt();
/* 1480 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 1485 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 1491 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 1496 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 1501 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 1507 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 1513 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 1517 */         if (other instanceof MysqlxCursor.Open) {
/* 1518 */           return mergeFrom((MysqlxCursor.Open)other);
/*      */         }
/* 1520 */         super.mergeFrom(other);
/* 1521 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxCursor.Open other) {
/* 1526 */         if (other == MysqlxCursor.Open.getDefaultInstance()) return this; 
/* 1527 */         if (other.hasCursorId()) {
/* 1528 */           setCursorId(other.getCursorId());
/*      */         }
/* 1530 */         if (other.hasStmt()) {
/* 1531 */           mergeStmt(other.getStmt());
/*      */         }
/* 1533 */         if (other.hasFetchRows()) {
/* 1534 */           setFetchRows(other.getFetchRows());
/*      */         }
/* 1536 */         mergeUnknownFields(other.unknownFields);
/* 1537 */         onChanged();
/* 1538 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 1543 */         if (!hasCursorId()) {
/* 1544 */           return false;
/*      */         }
/* 1546 */         if (!hasStmt()) {
/* 1547 */           return false;
/*      */         }
/* 1549 */         if (!getStmt().isInitialized()) {
/* 1550 */           return false;
/*      */         }
/* 1552 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1560 */         MysqlxCursor.Open parsedMessage = null;
/*      */         try {
/* 1562 */           parsedMessage = (MysqlxCursor.Open)MysqlxCursor.Open.PARSER.parsePartialFrom(input, extensionRegistry);
/* 1563 */         } catch (InvalidProtocolBufferException e) {
/* 1564 */           parsedMessage = (MysqlxCursor.Open)e.getUnfinishedMessage();
/* 1565 */           throw e.unwrapIOException();
/*      */         } finally {
/* 1567 */           if (parsedMessage != null) {
/* 1568 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 1571 */         return this;
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
/*      */       public boolean hasCursorId() {
/* 1586 */         return ((this.bitField0_ & 0x1) != 0);
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
/*      */       public int getCursorId() {
/* 1598 */         return this.cursorId_;
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
/*      */       public Builder setCursorId(int value) {
/* 1611 */         this.bitField0_ |= 0x1;
/* 1612 */         this.cursorId_ = value;
/* 1613 */         onChanged();
/* 1614 */         return this;
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
/*      */       public Builder clearCursorId() {
/* 1626 */         this.bitField0_ &= 0xFFFFFFFE;
/* 1627 */         this.cursorId_ = 0;
/* 1628 */         onChanged();
/* 1629 */         return this;
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
/*      */       public boolean hasStmt() {
/* 1644 */         return ((this.bitField0_ & 0x2) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxCursor.Open.OneOfMessage getStmt() {
/* 1655 */         if (this.stmtBuilder_ == null) {
/* 1656 */           return (this.stmt_ == null) ? MysqlxCursor.Open.OneOfMessage.getDefaultInstance() : this.stmt_;
/*      */         }
/* 1658 */         return (MysqlxCursor.Open.OneOfMessage)this.stmtBuilder_.getMessage();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setStmt(MysqlxCursor.Open.OneOfMessage value) {
/* 1669 */         if (this.stmtBuilder_ == null) {
/* 1670 */           if (value == null) {
/* 1671 */             throw new NullPointerException();
/*      */           }
/* 1673 */           this.stmt_ = value;
/* 1674 */           onChanged();
/*      */         } else {
/* 1676 */           this.stmtBuilder_.setMessage((AbstractMessage)value);
/*      */         } 
/* 1678 */         this.bitField0_ |= 0x2;
/* 1679 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setStmt(MysqlxCursor.Open.OneOfMessage.Builder builderForValue) {
/* 1690 */         if (this.stmtBuilder_ == null) {
/* 1691 */           this.stmt_ = builderForValue.build();
/* 1692 */           onChanged();
/*      */         } else {
/* 1694 */           this.stmtBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 1696 */         this.bitField0_ |= 0x2;
/* 1697 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeStmt(MysqlxCursor.Open.OneOfMessage value) {
/* 1707 */         if (this.stmtBuilder_ == null) {
/* 1708 */           if ((this.bitField0_ & 0x2) != 0 && this.stmt_ != null && this.stmt_ != 
/*      */             
/* 1710 */             MysqlxCursor.Open.OneOfMessage.getDefaultInstance()) {
/* 1711 */             this
/* 1712 */               .stmt_ = MysqlxCursor.Open.OneOfMessage.newBuilder(this.stmt_).mergeFrom(value).buildPartial();
/*      */           } else {
/* 1714 */             this.stmt_ = value;
/*      */           } 
/* 1716 */           onChanged();
/*      */         } else {
/* 1718 */           this.stmtBuilder_.mergeFrom((AbstractMessage)value);
/*      */         } 
/* 1720 */         this.bitField0_ |= 0x2;
/* 1721 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearStmt() {
/* 1731 */         if (this.stmtBuilder_ == null) {
/* 1732 */           this.stmt_ = null;
/* 1733 */           onChanged();
/*      */         } else {
/* 1735 */           this.stmtBuilder_.clear();
/*      */         } 
/* 1737 */         this.bitField0_ &= 0xFFFFFFFD;
/* 1738 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxCursor.Open.OneOfMessage.Builder getStmtBuilder() {
/* 1748 */         this.bitField0_ |= 0x2;
/* 1749 */         onChanged();
/* 1750 */         return (MysqlxCursor.Open.OneOfMessage.Builder)getStmtFieldBuilder().getBuilder();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxCursor.Open.OneOfMessageOrBuilder getStmtOrBuilder() {
/* 1760 */         if (this.stmtBuilder_ != null) {
/* 1761 */           return (MysqlxCursor.Open.OneOfMessageOrBuilder)this.stmtBuilder_.getMessageOrBuilder();
/*      */         }
/* 1763 */         return (this.stmt_ == null) ? 
/* 1764 */           MysqlxCursor.Open.OneOfMessage.getDefaultInstance() : this.stmt_;
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
/*      */       private SingleFieldBuilderV3<MysqlxCursor.Open.OneOfMessage, MysqlxCursor.Open.OneOfMessage.Builder, MysqlxCursor.Open.OneOfMessageOrBuilder> getStmtFieldBuilder() {
/* 1777 */         if (this.stmtBuilder_ == null) {
/* 1778 */           this
/*      */ 
/*      */ 
/*      */             
/* 1782 */             .stmtBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getStmt(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 1783 */           this.stmt_ = null;
/*      */         } 
/* 1785 */         return this.stmtBuilder_;
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
/*      */       public boolean hasFetchRows() {
/* 1798 */         return ((this.bitField0_ & 0x4) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public long getFetchRows() {
/* 1809 */         return this.fetchRows_;
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
/*      */       public Builder setFetchRows(long value) {
/* 1821 */         this.bitField0_ |= 0x4;
/* 1822 */         this.fetchRows_ = value;
/* 1823 */         onChanged();
/* 1824 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearFetchRows() {
/* 1835 */         this.bitField0_ &= 0xFFFFFFFB;
/* 1836 */         this.fetchRows_ = 0L;
/* 1837 */         onChanged();
/* 1838 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 1843 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 1849 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1859 */     private static final Open DEFAULT_INSTANCE = new Open();
/*      */ 
/*      */     
/*      */     public static Open getDefaultInstance() {
/* 1863 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 1867 */     public static final Parser<Open> PARSER = (Parser<Open>)new AbstractParser<Open>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxCursor.Open parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 1873 */           return new MysqlxCursor.Open(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Open> parser() {
/* 1878 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Open> getParserForType() {
/* 1883 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Open getDefaultInstanceForType() {
/* 1888 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static interface OneOfMessageOrBuilder
/*      */       extends MessageOrBuilder
/*      */     {
/*      */       boolean hasType();
/*      */ 
/*      */ 
/*      */       
/*      */       MysqlxCursor.Open.OneOfMessage.Type getType();
/*      */ 
/*      */ 
/*      */       
/*      */       boolean hasPrepareExecute();
/*      */ 
/*      */ 
/*      */       
/*      */       MysqlxPrepare.Execute getPrepareExecute();
/*      */ 
/*      */ 
/*      */       
/*      */       MysqlxPrepare.ExecuteOrBuilder getPrepareExecuteOrBuilder();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface FetchOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasCursorId();
/*      */ 
/*      */ 
/*      */     
/*      */     int getCursorId();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasFetchRows();
/*      */ 
/*      */ 
/*      */     
/*      */     long getFetchRows();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Fetch
/*      */     extends GeneratedMessageV3
/*      */     implements FetchOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     private int bitField0_;
/*      */     
/*      */     public static final int CURSOR_ID_FIELD_NUMBER = 1;
/*      */     
/*      */     private int cursorId_;
/*      */     
/*      */     public static final int FETCH_ROWS_FIELD_NUMBER = 5;
/*      */     
/*      */     private long fetchRows_;
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */     
/*      */     private Fetch(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 1959 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2088 */       this.memoizedIsInitialized = -1; } private Fetch() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Fetch(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Fetch(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.bitField0_ |= 0x1; this.cursorId_ = input.readUInt32(); continue;case 40: this.bitField0_ |= 0x2; this.fetchRows_ = input.readUInt64(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCursor.internal_static_Mysqlx_Cursor_Fetch_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCursor.internal_static_Mysqlx_Cursor_Fetch_fieldAccessorTable.ensureFieldAccessorsInitialized(Fetch.class, Builder.class); } public boolean hasCursorId() { return ((this.bitField0_ & 0x1) != 0); } public int getCursorId() { return this.cursorId_; }
/*      */     public boolean hasFetchRows() { return ((this.bitField0_ & 0x2) != 0); }
/*      */     public long getFetchRows() { return this.fetchRows_; }
/* 2091 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 2092 */       if (isInitialized == 1) return true; 
/* 2093 */       if (isInitialized == 0) return false;
/*      */       
/* 2095 */       if (!hasCursorId()) {
/* 2096 */         this.memoizedIsInitialized = 0;
/* 2097 */         return false;
/*      */       } 
/* 2099 */       this.memoizedIsInitialized = 1;
/* 2100 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 2106 */       if ((this.bitField0_ & 0x1) != 0) {
/* 2107 */         output.writeUInt32(1, this.cursorId_);
/*      */       }
/* 2109 */       if ((this.bitField0_ & 0x2) != 0) {
/* 2110 */         output.writeUInt64(5, this.fetchRows_);
/*      */       }
/* 2112 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 2117 */       int size = this.memoizedSize;
/* 2118 */       if (size != -1) return size;
/*      */       
/* 2120 */       size = 0;
/* 2121 */       if ((this.bitField0_ & 0x1) != 0) {
/* 2122 */         size += 
/* 2123 */           CodedOutputStream.computeUInt32Size(1, this.cursorId_);
/*      */       }
/* 2125 */       if ((this.bitField0_ & 0x2) != 0) {
/* 2126 */         size += 
/* 2127 */           CodedOutputStream.computeUInt64Size(5, this.fetchRows_);
/*      */       }
/* 2129 */       size += this.unknownFields.getSerializedSize();
/* 2130 */       this.memoizedSize = size;
/* 2131 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 2136 */       if (obj == this) {
/* 2137 */         return true;
/*      */       }
/* 2139 */       if (!(obj instanceof Fetch)) {
/* 2140 */         return super.equals(obj);
/*      */       }
/* 2142 */       Fetch other = (Fetch)obj;
/*      */       
/* 2144 */       if (hasCursorId() != other.hasCursorId()) return false; 
/* 2145 */       if (hasCursorId() && 
/* 2146 */         getCursorId() != other
/* 2147 */         .getCursorId()) return false;
/*      */       
/* 2149 */       if (hasFetchRows() != other.hasFetchRows()) return false; 
/* 2150 */       if (hasFetchRows() && 
/* 2151 */         getFetchRows() != other
/* 2152 */         .getFetchRows()) return false;
/*      */       
/* 2154 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 2155 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 2160 */       if (this.memoizedHashCode != 0) {
/* 2161 */         return this.memoizedHashCode;
/*      */       }
/* 2163 */       int hash = 41;
/* 2164 */       hash = 19 * hash + getDescriptor().hashCode();
/* 2165 */       if (hasCursorId()) {
/* 2166 */         hash = 37 * hash + 1;
/* 2167 */         hash = 53 * hash + getCursorId();
/*      */       } 
/* 2169 */       if (hasFetchRows()) {
/* 2170 */         hash = 37 * hash + 5;
/* 2171 */         hash = 53 * hash + Internal.hashLong(
/* 2172 */             getFetchRows());
/*      */       } 
/* 2174 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 2175 */       this.memoizedHashCode = hash;
/* 2176 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Fetch parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 2182 */       return (Fetch)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Fetch parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2188 */       return (Fetch)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Fetch parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 2193 */       return (Fetch)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Fetch parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2199 */       return (Fetch)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Fetch parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 2203 */       return (Fetch)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Fetch parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2209 */       return (Fetch)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Fetch parseFrom(InputStream input) throws IOException {
/* 2213 */       return 
/* 2214 */         (Fetch)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Fetch parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2220 */       return 
/* 2221 */         (Fetch)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Fetch parseDelimitedFrom(InputStream input) throws IOException {
/* 2225 */       return 
/* 2226 */         (Fetch)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Fetch parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2232 */       return 
/* 2233 */         (Fetch)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Fetch parseFrom(CodedInputStream input) throws IOException {
/* 2238 */       return 
/* 2239 */         (Fetch)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Fetch parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2245 */       return 
/* 2246 */         (Fetch)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 2250 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 2252 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Fetch prototype) {
/* 2255 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 2259 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 2260 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 2266 */       Builder builder = new Builder(parent);
/* 2267 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxCursor.FetchOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */ 
/*      */       
/*      */       private int cursorId_;
/*      */ 
/*      */ 
/*      */       
/*      */       private long fetchRows_;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 2292 */         return MysqlxCursor.internal_static_Mysqlx_Cursor_Fetch_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 2298 */         return MysqlxCursor.internal_static_Mysqlx_Cursor_Fetch_fieldAccessorTable
/* 2299 */           .ensureFieldAccessorsInitialized(MysqlxCursor.Fetch.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 2305 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 2310 */         super(parent);
/* 2311 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 2315 */         if (MysqlxCursor.Fetch.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/* 2320 */         super.clear();
/* 2321 */         this.cursorId_ = 0;
/* 2322 */         this.bitField0_ &= 0xFFFFFFFE;
/* 2323 */         this.fetchRows_ = 0L;
/* 2324 */         this.bitField0_ &= 0xFFFFFFFD;
/* 2325 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 2331 */         return MysqlxCursor.internal_static_Mysqlx_Cursor_Fetch_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxCursor.Fetch getDefaultInstanceForType() {
/* 2336 */         return MysqlxCursor.Fetch.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxCursor.Fetch build() {
/* 2341 */         MysqlxCursor.Fetch result = buildPartial();
/* 2342 */         if (!result.isInitialized()) {
/* 2343 */           throw newUninitializedMessageException(result);
/*      */         }
/* 2345 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxCursor.Fetch buildPartial() {
/* 2350 */         MysqlxCursor.Fetch result = new MysqlxCursor.Fetch(this);
/* 2351 */         int from_bitField0_ = this.bitField0_;
/* 2352 */         int to_bitField0_ = 0;
/* 2353 */         if ((from_bitField0_ & 0x1) != 0) {
/* 2354 */           result.cursorId_ = this.cursorId_;
/* 2355 */           to_bitField0_ |= 0x1;
/*      */         } 
/* 2357 */         if ((from_bitField0_ & 0x2) != 0) {
/* 2358 */           result.fetchRows_ = this.fetchRows_;
/* 2359 */           to_bitField0_ |= 0x2;
/*      */         } 
/* 2361 */         result.bitField0_ = to_bitField0_;
/* 2362 */         onBuilt();
/* 2363 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 2368 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 2374 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 2379 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 2384 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 2390 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 2396 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 2400 */         if (other instanceof MysqlxCursor.Fetch) {
/* 2401 */           return mergeFrom((MysqlxCursor.Fetch)other);
/*      */         }
/* 2403 */         super.mergeFrom(other);
/* 2404 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxCursor.Fetch other) {
/* 2409 */         if (other == MysqlxCursor.Fetch.getDefaultInstance()) return this; 
/* 2410 */         if (other.hasCursorId()) {
/* 2411 */           setCursorId(other.getCursorId());
/*      */         }
/* 2413 */         if (other.hasFetchRows()) {
/* 2414 */           setFetchRows(other.getFetchRows());
/*      */         }
/* 2416 */         mergeUnknownFields(other.unknownFields);
/* 2417 */         onChanged();
/* 2418 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 2423 */         if (!hasCursorId()) {
/* 2424 */           return false;
/*      */         }
/* 2426 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2434 */         MysqlxCursor.Fetch parsedMessage = null;
/*      */         try {
/* 2436 */           parsedMessage = (MysqlxCursor.Fetch)MysqlxCursor.Fetch.PARSER.parsePartialFrom(input, extensionRegistry);
/* 2437 */         } catch (InvalidProtocolBufferException e) {
/* 2438 */           parsedMessage = (MysqlxCursor.Fetch)e.getUnfinishedMessage();
/* 2439 */           throw e.unwrapIOException();
/*      */         } finally {
/* 2441 */           if (parsedMessage != null) {
/* 2442 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 2445 */         return this;
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
/*      */       public boolean hasCursorId() {
/* 2459 */         return ((this.bitField0_ & 0x1) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int getCursorId() {
/* 2470 */         return this.cursorId_;
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
/*      */       public Builder setCursorId(int value) {
/* 2482 */         this.bitField0_ |= 0x1;
/* 2483 */         this.cursorId_ = value;
/* 2484 */         onChanged();
/* 2485 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearCursorId() {
/* 2496 */         this.bitField0_ &= 0xFFFFFFFE;
/* 2497 */         this.cursorId_ = 0;
/* 2498 */         onChanged();
/* 2499 */         return this;
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
/*      */       public boolean hasFetchRows() {
/* 2512 */         return ((this.bitField0_ & 0x2) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public long getFetchRows() {
/* 2523 */         return this.fetchRows_;
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
/*      */       public Builder setFetchRows(long value) {
/* 2535 */         this.bitField0_ |= 0x2;
/* 2536 */         this.fetchRows_ = value;
/* 2537 */         onChanged();
/* 2538 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearFetchRows() {
/* 2549 */         this.bitField0_ &= 0xFFFFFFFD;
/* 2550 */         this.fetchRows_ = 0L;
/* 2551 */         onChanged();
/* 2552 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 2557 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 2563 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2573 */     private static final Fetch DEFAULT_INSTANCE = new Fetch();
/*      */ 
/*      */     
/*      */     public static Fetch getDefaultInstance() {
/* 2577 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 2581 */     public static final Parser<Fetch> PARSER = (Parser<Fetch>)new AbstractParser<Fetch>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxCursor.Fetch parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 2587 */           return new MysqlxCursor.Fetch(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Fetch> parser() {
/* 2592 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Fetch> getParserForType() {
/* 2597 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Fetch getDefaultInstanceForType() {
/* 2602 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface CloseOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasCursorId();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getCursorId();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Close
/*      */     extends GeneratedMessageV3
/*      */     implements CloseOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int CURSOR_ID_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */     
/*      */     private int cursorId_;
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Close(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 2654 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2753 */       this.memoizedIsInitialized = -1; } private Close() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Close(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Close(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.bitField0_ |= 0x1; this.cursorId_ = input.readUInt32(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCursor.internal_static_Mysqlx_Cursor_Close_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCursor.internal_static_Mysqlx_Cursor_Close_fieldAccessorTable.ensureFieldAccessorsInitialized(Close.class, Builder.class); }
/*      */     public boolean hasCursorId() { return ((this.bitField0_ & 0x1) != 0); }
/*      */     public int getCursorId() { return this.cursorId_; }
/* 2756 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 2757 */       if (isInitialized == 1) return true; 
/* 2758 */       if (isInitialized == 0) return false;
/*      */       
/* 2760 */       if (!hasCursorId()) {
/* 2761 */         this.memoizedIsInitialized = 0;
/* 2762 */         return false;
/*      */       } 
/* 2764 */       this.memoizedIsInitialized = 1;
/* 2765 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 2771 */       if ((this.bitField0_ & 0x1) != 0) {
/* 2772 */         output.writeUInt32(1, this.cursorId_);
/*      */       }
/* 2774 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 2779 */       int size = this.memoizedSize;
/* 2780 */       if (size != -1) return size;
/*      */       
/* 2782 */       size = 0;
/* 2783 */       if ((this.bitField0_ & 0x1) != 0) {
/* 2784 */         size += 
/* 2785 */           CodedOutputStream.computeUInt32Size(1, this.cursorId_);
/*      */       }
/* 2787 */       size += this.unknownFields.getSerializedSize();
/* 2788 */       this.memoizedSize = size;
/* 2789 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 2794 */       if (obj == this) {
/* 2795 */         return true;
/*      */       }
/* 2797 */       if (!(obj instanceof Close)) {
/* 2798 */         return super.equals(obj);
/*      */       }
/* 2800 */       Close other = (Close)obj;
/*      */       
/* 2802 */       if (hasCursorId() != other.hasCursorId()) return false; 
/* 2803 */       if (hasCursorId() && 
/* 2804 */         getCursorId() != other
/* 2805 */         .getCursorId()) return false;
/*      */       
/* 2807 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 2808 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 2813 */       if (this.memoizedHashCode != 0) {
/* 2814 */         return this.memoizedHashCode;
/*      */       }
/* 2816 */       int hash = 41;
/* 2817 */       hash = 19 * hash + getDescriptor().hashCode();
/* 2818 */       if (hasCursorId()) {
/* 2819 */         hash = 37 * hash + 1;
/* 2820 */         hash = 53 * hash + getCursorId();
/*      */       } 
/* 2822 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 2823 */       this.memoizedHashCode = hash;
/* 2824 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 2830 */       return (Close)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2836 */       return (Close)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 2841 */       return (Close)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2847 */       return (Close)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Close parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 2851 */       return (Close)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2857 */       return (Close)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Close parseFrom(InputStream input) throws IOException {
/* 2861 */       return 
/* 2862 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2868 */       return 
/* 2869 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Close parseDelimitedFrom(InputStream input) throws IOException {
/* 2873 */       return 
/* 2874 */         (Close)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2880 */       return 
/* 2881 */         (Close)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Close parseFrom(CodedInputStream input) throws IOException {
/* 2886 */       return 
/* 2887 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2893 */       return 
/* 2894 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 2898 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 2900 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Close prototype) {
/* 2903 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 2907 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 2908 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 2914 */       Builder builder = new Builder(parent);
/* 2915 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxCursor.CloseOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private int cursorId_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 2940 */         return MysqlxCursor.internal_static_Mysqlx_Cursor_Close_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 2946 */         return MysqlxCursor.internal_static_Mysqlx_Cursor_Close_fieldAccessorTable
/* 2947 */           .ensureFieldAccessorsInitialized(MysqlxCursor.Close.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 2953 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 2958 */         super(parent);
/* 2959 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 2963 */         if (MysqlxCursor.Close.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/* 2968 */         super.clear();
/* 2969 */         this.cursorId_ = 0;
/* 2970 */         this.bitField0_ &= 0xFFFFFFFE;
/* 2971 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 2977 */         return MysqlxCursor.internal_static_Mysqlx_Cursor_Close_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxCursor.Close getDefaultInstanceForType() {
/* 2982 */         return MysqlxCursor.Close.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxCursor.Close build() {
/* 2987 */         MysqlxCursor.Close result = buildPartial();
/* 2988 */         if (!result.isInitialized()) {
/* 2989 */           throw newUninitializedMessageException(result);
/*      */         }
/* 2991 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxCursor.Close buildPartial() {
/* 2996 */         MysqlxCursor.Close result = new MysqlxCursor.Close(this);
/* 2997 */         int from_bitField0_ = this.bitField0_;
/* 2998 */         int to_bitField0_ = 0;
/* 2999 */         if ((from_bitField0_ & 0x1) != 0) {
/* 3000 */           result.cursorId_ = this.cursorId_;
/* 3001 */           to_bitField0_ |= 0x1;
/*      */         } 
/* 3003 */         result.bitField0_ = to_bitField0_;
/* 3004 */         onBuilt();
/* 3005 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 3010 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 3016 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 3021 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 3026 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 3032 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 3038 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 3042 */         if (other instanceof MysqlxCursor.Close) {
/* 3043 */           return mergeFrom((MysqlxCursor.Close)other);
/*      */         }
/* 3045 */         super.mergeFrom(other);
/* 3046 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxCursor.Close other) {
/* 3051 */         if (other == MysqlxCursor.Close.getDefaultInstance()) return this; 
/* 3052 */         if (other.hasCursorId()) {
/* 3053 */           setCursorId(other.getCursorId());
/*      */         }
/* 3055 */         mergeUnknownFields(other.unknownFields);
/* 3056 */         onChanged();
/* 3057 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 3062 */         if (!hasCursorId()) {
/* 3063 */           return false;
/*      */         }
/* 3065 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3073 */         MysqlxCursor.Close parsedMessage = null;
/*      */         try {
/* 3075 */           parsedMessage = (MysqlxCursor.Close)MysqlxCursor.Close.PARSER.parsePartialFrom(input, extensionRegistry);
/* 3076 */         } catch (InvalidProtocolBufferException e) {
/* 3077 */           parsedMessage = (MysqlxCursor.Close)e.getUnfinishedMessage();
/* 3078 */           throw e.unwrapIOException();
/*      */         } finally {
/* 3080 */           if (parsedMessage != null) {
/* 3081 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 3084 */         return this;
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
/*      */       public boolean hasCursorId() {
/* 3098 */         return ((this.bitField0_ & 0x1) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int getCursorId() {
/* 3109 */         return this.cursorId_;
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
/*      */       public Builder setCursorId(int value) {
/* 3121 */         this.bitField0_ |= 0x1;
/* 3122 */         this.cursorId_ = value;
/* 3123 */         onChanged();
/* 3124 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearCursorId() {
/* 3135 */         this.bitField0_ &= 0xFFFFFFFE;
/* 3136 */         this.cursorId_ = 0;
/* 3137 */         onChanged();
/* 3138 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 3143 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 3149 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3159 */     private static final Close DEFAULT_INSTANCE = new Close();
/*      */ 
/*      */     
/*      */     public static Close getDefaultInstance() {
/* 3163 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 3167 */     public static final Parser<Close> PARSER = (Parser<Close>)new AbstractParser<Close>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxCursor.Close parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 3173 */           return new MysqlxCursor.Close(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Close> parser() {
/* 3178 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Close> getParserForType() {
/* 3183 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Close getDefaultInstanceForType() {
/* 3188 */       return DEFAULT_INSTANCE;
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
/*      */   public static Descriptors.FileDescriptor getDescriptor() {
/* 3216 */     return descriptor;
/*      */   }
/*      */ 
/*      */   
/*      */   static {
/* 3221 */     String[] descriptorData = { "\n\023mysqlx_cursor.proto\022\rMysqlx.Cursor\032\fmysqlx.proto\032\024mysqlx_prepare.proto\"ø\001\n\004Open\022\021\n\tcursor_id\030\001 \002(\r\022.\n\004stmt\030\004 \002(\0132 .Mysqlx.Cursor.Open.OneOfMessage\022\022\n\nfetch_rows\030\005 \001(\004\032\001\n\fOneOfMessage\0223\n\004type\030\001 \002(\0162%.Mysqlx.Cursor.Open.OneOfMessage.Type\0220\n\017prepare_execute\030\002 \001(\0132\027.Mysqlx.Prepare.Execute\"\033\n\004Type\022\023\n\017PREPARE_EXECUTE\020\000:\004ê0+\"4\n\005Fetch\022\021\n\tcursor_id\030\001 \002(\r\022\022\n\nfetch_rows\030\005 \001(\004:\004ê0-\" \n\005Close\022\021\n\tcursor_id\030\001 \002(\r:\004ê0,B\031\n\027com.mysql.cj.x.protobuf" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3235 */     descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] {
/*      */           
/* 3237 */           Mysqlx.getDescriptor(), 
/* 3238 */           MysqlxPrepare.getDescriptor()
/*      */         });
/*      */     
/* 3241 */     internal_static_Mysqlx_Cursor_Open_descriptor = getDescriptor().getMessageTypes().get(0);
/* 3242 */     internal_static_Mysqlx_Cursor_Open_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Cursor_Open_descriptor, new String[] { "CursorId", "Stmt", "FetchRows" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3247 */     internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor = internal_static_Mysqlx_Cursor_Open_descriptor.getNestedTypes().get(0);
/* 3248 */     internal_static_Mysqlx_Cursor_Open_OneOfMessage_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor, new String[] { "Type", "PrepareExecute" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3253 */     internal_static_Mysqlx_Cursor_Fetch_descriptor = getDescriptor().getMessageTypes().get(1);
/* 3254 */     internal_static_Mysqlx_Cursor_Fetch_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Cursor_Fetch_descriptor, new String[] { "CursorId", "FetchRows" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3259 */     internal_static_Mysqlx_Cursor_Close_descriptor = getDescriptor().getMessageTypes().get(2);
/* 3260 */     internal_static_Mysqlx_Cursor_Close_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Cursor_Close_descriptor, new String[] { "CursorId" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3265 */     ExtensionRegistry registry = ExtensionRegistry.newInstance();
/* 3266 */     registry.add(Mysqlx.clientMessageId);
/*      */     
/* 3268 */     Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
/* 3269 */     Mysqlx.getDescriptor();
/* 3270 */     MysqlxPrepare.getDescriptor();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\x\protobuf\MysqlxCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */