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
/*      */ import com.google.protobuf.RepeatedFieldBuilderV3;
/*      */ import com.google.protobuf.UnknownFieldSet;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ 
/*      */ 
/*      */ public final class MysqlxExpect
/*      */ {
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Expect_Open_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expect_Open_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Expect_Open_Condition_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expect_Open_Condition_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Expect_Close_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expect_Close_fieldAccessorTable;
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
/*      */   public static interface OpenOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasOp();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxExpect.Open.CtxOperation getOp();
/*      */ 
/*      */ 
/*      */     
/*      */     List<MysqlxExpect.Open.Condition> getCondList();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxExpect.Open.Condition getCond(int param1Int);
/*      */ 
/*      */ 
/*      */     
/*      */     int getCondCount();
/*      */ 
/*      */ 
/*      */     
/*      */     List<? extends MysqlxExpect.Open.ConditionOrBuilder> getCondOrBuilderList();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxExpect.Open.ConditionOrBuilder getCondOrBuilder(int param1Int);
/*      */   }
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
/*      */     private int bitField0_;
/*      */ 
/*      */     
/*      */     public static final int OP_FIELD_NUMBER = 1;
/*      */ 
/*      */     
/*      */     private int op_;
/*      */     
/*      */     public static final int COND_FIELD_NUMBER = 2;
/*      */     
/*      */     private List<Condition> cond_;
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */     
/*      */     private Open(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/*  104 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1340 */       this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Open(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Open(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int rawValue; CtxOperation value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: rawValue = input.readEnum(); value = CtxOperation.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(1, rawValue); continue; }  this.bitField0_ |= 0x1; this.op_ = rawValue; continue;case 18: if ((mutable_bitField0_ & 0x2) == 0) { this.cond_ = new ArrayList<>(); mutable_bitField0_ |= 0x2; }  this.cond_.add(input.readMessage(Condition.PARSER, extensionRegistry)); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x2) != 0) this.cond_ = Collections.unmodifiableList(this.cond_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxExpect.internal_static_Mysqlx_Expect_Open_descriptor; } private Open() { this.memoizedIsInitialized = -1; this.op_ = 0; this.cond_ = Collections.emptyList(); } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxExpect.internal_static_Mysqlx_Expect_Open_fieldAccessorTable.ensureFieldAccessorsInitialized(Open.class, Builder.class); } public enum CtxOperation implements ProtocolMessageEnum {
/*      */       EXPECT_CTX_COPY_PREV(0), EXPECT_CTX_EMPTY(1); public static final int EXPECT_CTX_COPY_PREV_VALUE = 0; public static final int EXPECT_CTX_EMPTY_VALUE = 1; private static final Internal.EnumLiteMap<CtxOperation> internalValueMap = new Internal.EnumLiteMap<CtxOperation>() { public MysqlxExpect.Open.CtxOperation findValueByNumber(int number) { return MysqlxExpect.Open.CtxOperation.forNumber(number); } }; private static final CtxOperation[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static CtxOperation forNumber(int value) { switch (value) { case 0: return EXPECT_CTX_COPY_PREV;case 1: return EXPECT_CTX_EMPTY; }  return null; } public static Internal.EnumLiteMap<CtxOperation> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxExpect.Open.getDescriptor().getEnumTypes().get(0); } CtxOperation(int value) { this.value = value; } } public static final class Condition extends GeneratedMessageV3 implements ConditionOrBuilder { private static final long serialVersionUID = 0L; private int bitField0_; public static final int CONDITION_KEY_FIELD_NUMBER = 1; private int conditionKey_; public static final int CONDITION_VALUE_FIELD_NUMBER = 2; private ByteString conditionValue_; public static final int OP_FIELD_NUMBER = 3; private int op_; private byte memoizedIsInitialized; private Condition(GeneratedMessageV3.Builder<?> builder) { super(builder); this.memoizedIsInitialized = -1; } private Condition() { this.memoizedIsInitialized = -1; this.conditionValue_ = ByteString.EMPTY; this.op_ = 0; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Condition(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Condition(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int rawValue; ConditionOperation value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.bitField0_ |= 0x1; this.conditionKey_ = input.readUInt32(); continue;case 18: this.bitField0_ |= 0x2; this.conditionValue_ = input.readBytes(); continue;case 24: rawValue = input.readEnum(); value = ConditionOperation.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(3, rawValue); continue; }  this.bitField0_ |= 0x4; this.op_ = rawValue; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_fieldAccessorTable.ensureFieldAccessorsInitialized(Condition.class, Builder.class); } public enum Key implements ProtocolMessageEnum {
/*      */         EXPECT_NO_ERROR(1), EXPECT_FIELD_EXIST(2), EXPECT_DOCID_GENERATED(3); public static final int EXPECT_NO_ERROR_VALUE = 1; public static final int EXPECT_FIELD_EXIST_VALUE = 2; public static final int EXPECT_DOCID_GENERATED_VALUE = 3; private static final Internal.EnumLiteMap<Key> internalValueMap = new Internal.EnumLiteMap<Key>() { public MysqlxExpect.Open.Condition.Key findValueByNumber(int number) { return MysqlxExpect.Open.Condition.Key.forNumber(number); } }; private static final Key[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Key forNumber(int value) { switch (value) { case 1: return EXPECT_NO_ERROR;case 2: return EXPECT_FIELD_EXIST;case 3: return EXPECT_DOCID_GENERATED; }  return null; } public static Internal.EnumLiteMap<Key> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxExpect.Open.Condition.getDescriptor().getEnumTypes().get(0); } Key(int value) { this.value = value; } } public enum ConditionOperation implements ProtocolMessageEnum {
/* 1343 */         EXPECT_OP_SET(0), EXPECT_OP_UNSET(1); public static final int EXPECT_OP_SET_VALUE = 0; public static final int EXPECT_OP_UNSET_VALUE = 1; private static final Internal.EnumLiteMap<ConditionOperation> internalValueMap = new Internal.EnumLiteMap<ConditionOperation>() { public MysqlxExpect.Open.Condition.ConditionOperation findValueByNumber(int number) { return MysqlxExpect.Open.Condition.ConditionOperation.forNumber(number); } }; private static final ConditionOperation[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static ConditionOperation forNumber(int value) { switch (value) { case 0: return EXPECT_OP_SET;case 1: return EXPECT_OP_UNSET; }  return null; } public static Internal.EnumLiteMap<ConditionOperation> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxExpect.Open.Condition.getDescriptor().getEnumTypes().get(1); } ConditionOperation(int value) { this.value = value; } } public boolean hasConditionKey() { return ((this.bitField0_ & 0x1) != 0); } public int getConditionKey() { return this.conditionKey_; } public boolean hasConditionValue() { return ((this.bitField0_ & 0x2) != 0); } public ByteString getConditionValue() { return this.conditionValue_; } public boolean hasOp() { return ((this.bitField0_ & 0x4) != 0); } public ConditionOperation getOp() { ConditionOperation result = ConditionOperation.valueOf(this.op_); return (result == null) ? ConditionOperation.EXPECT_OP_SET : result; } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized; if (isInitialized == 1) return true;  if (isInitialized == 0) return false;  if (!hasConditionKey()) { this.memoizedIsInitialized = 0; return false; }  this.memoizedIsInitialized = 1; return true; } public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) output.writeUInt32(1, this.conditionKey_);  if ((this.bitField0_ & 0x2) != 0) output.writeBytes(2, this.conditionValue_);  if ((this.bitField0_ & 0x4) != 0) output.writeEnum(3, this.op_);  this.unknownFields.writeTo(output); } public int getSerializedSize() { int size = this.memoizedSize; if (size != -1) return size;  size = 0; if ((this.bitField0_ & 0x1) != 0) size += CodedOutputStream.computeUInt32Size(1, this.conditionKey_);  if ((this.bitField0_ & 0x2) != 0) size += CodedOutputStream.computeBytesSize(2, this.conditionValue_);  if ((this.bitField0_ & 0x4) != 0) size += CodedOutputStream.computeEnumSize(3, this.op_);  size += this.unknownFields.getSerializedSize(); this.memoizedSize = size; return size; } public boolean equals(Object obj) { if (obj == this) return true;  if (!(obj instanceof Condition)) return super.equals(obj);  Condition other = (Condition)obj; if (hasConditionKey() != other.hasConditionKey()) return false;  if (hasConditionKey() && getConditionKey() != other.getConditionKey()) return false;  if (hasConditionValue() != other.hasConditionValue()) return false;  if (hasConditionValue() && !getConditionValue().equals(other.getConditionValue())) return false;  if (hasOp() != other.hasOp()) return false;  if (hasOp() && this.op_ != other.op_) return false;  if (!this.unknownFields.equals(other.unknownFields)) return false;  return true; } public int hashCode() { if (this.memoizedHashCode != 0) return this.memoizedHashCode;  int hash = 41; hash = 19 * hash + getDescriptor().hashCode(); if (hasConditionKey()) { hash = 37 * hash + 1; hash = 53 * hash + getConditionKey(); }  if (hasConditionValue()) { hash = 37 * hash + 2; hash = 53 * hash + getConditionValue().hashCode(); }  if (hasOp()) { hash = 37 * hash + 3; hash = 53 * hash + this.op_; }  hash = 29 * hash + this.unknownFields.hashCode(); this.memoizedHashCode = hash; return hash; } public static Condition parseFrom(ByteBuffer data) throws InvalidProtocolBufferException { return (Condition)PARSER.parseFrom(data); } public static Condition parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (Condition)PARSER.parseFrom(data, extensionRegistry); } public static Condition parseFrom(ByteString data) throws InvalidProtocolBufferException { return (Condition)PARSER.parseFrom(data); } public static Condition parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (Condition)PARSER.parseFrom(data, extensionRegistry); } public static Condition parseFrom(byte[] data) throws InvalidProtocolBufferException { return (Condition)PARSER.parseFrom(data); } public static Condition parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (Condition)PARSER.parseFrom(data, extensionRegistry); } public static Condition parseFrom(InputStream input) throws IOException { return (Condition)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static Condition parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (Condition)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public static Condition parseDelimitedFrom(InputStream input) throws IOException { return (Condition)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input); } public static Condition parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (Condition)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry); } public static Condition parseFrom(CodedInputStream input) throws IOException { return (Condition)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static Condition parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (Condition)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public Builder newBuilderForType() { return newBuilder(); } public static Builder newBuilder() { return DEFAULT_INSTANCE.toBuilder(); } public static Builder newBuilder(Condition prototype) { return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype); } public Builder toBuilder() { return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder()).mergeFrom(this); } protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) { Builder builder = new Builder(parent); return builder; } public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxExpect.Open.ConditionOrBuilder { private int bitField0_; private int conditionKey_; private ByteString conditionValue_; private int op_; public static final Descriptors.Descriptor getDescriptor() { return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxExpect.Open.Condition.class, Builder.class); } private Builder() { this.conditionValue_ = ByteString.EMPTY; this.op_ = 0; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.conditionValue_ = ByteString.EMPTY; this.op_ = 0; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxExpect.Open.Condition.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.conditionKey_ = 0; this.bitField0_ &= 0xFFFFFFFE; this.conditionValue_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFFD; this.op_ = 0; this.bitField0_ &= 0xFFFFFFFB; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_descriptor; } public MysqlxExpect.Open.Condition getDefaultInstanceForType() { return MysqlxExpect.Open.Condition.getDefaultInstance(); } public MysqlxExpect.Open.Condition build() { MysqlxExpect.Open.Condition result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxExpect.Open.Condition buildPartial() { MysqlxExpect.Open.Condition result = new MysqlxExpect.Open.Condition(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { result.conditionKey_ = this.conditionKey_; to_bitField0_ |= 0x1; }  if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.conditionValue_ = this.conditionValue_; if ((from_bitField0_ & 0x4) != 0) to_bitField0_ |= 0x4;  result.op_ = this.op_; result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxExpect.Open.Condition) return mergeFrom((MysqlxExpect.Open.Condition)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxExpect.Open.Condition other) { if (other == MysqlxExpect.Open.Condition.getDefaultInstance()) return this;  if (other.hasConditionKey()) setConditionKey(other.getConditionKey());  if (other.hasConditionValue()) setConditionValue(other.getConditionValue());  if (other.hasOp()) setOp(other.getOp());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasConditionKey()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxExpect.Open.Condition parsedMessage = null; try { parsedMessage = (MysqlxExpect.Open.Condition)MysqlxExpect.Open.Condition.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxExpect.Open.Condition)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasConditionKey() { return ((this.bitField0_ & 0x1) != 0); } public int getConditionKey() { return this.conditionKey_; } public Builder setConditionKey(int value) { this.bitField0_ |= 0x1; this.conditionKey_ = value; onChanged(); return this; } public Builder clearConditionKey() { this.bitField0_ &= 0xFFFFFFFE; this.conditionKey_ = 0; onChanged(); return this; } public boolean hasConditionValue() { return ((this.bitField0_ & 0x2) != 0); } public ByteString getConditionValue() { return this.conditionValue_; } public Builder setConditionValue(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.conditionValue_ = value; onChanged(); return this; } public Builder clearConditionValue() { this.bitField0_ &= 0xFFFFFFFD; this.conditionValue_ = MysqlxExpect.Open.Condition.getDefaultInstance().getConditionValue(); onChanged(); return this; } public boolean hasOp() { return ((this.bitField0_ & 0x4) != 0); } public MysqlxExpect.Open.Condition.ConditionOperation getOp() { MysqlxExpect.Open.Condition.ConditionOperation result = MysqlxExpect.Open.Condition.ConditionOperation.valueOf(this.op_); return (result == null) ? MysqlxExpect.Open.Condition.ConditionOperation.EXPECT_OP_SET : result; } public Builder setOp(MysqlxExpect.Open.Condition.ConditionOperation value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x4; this.op_ = value.getNumber(); onChanged(); return this; } public Builder clearOp() { this.bitField0_ &= 0xFFFFFFFB; this.op_ = 0; onChanged(); return this; } public final Builder setUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.setUnknownFields(unknownFields); } public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.mergeUnknownFields(unknownFields); } } private static final Condition DEFAULT_INSTANCE = new Condition(); public static Condition getDefaultInstance() { return DEFAULT_INSTANCE; } @Deprecated public static final Parser<Condition> PARSER = (Parser<Condition>)new AbstractParser<Condition>() { public MysqlxExpect.Open.Condition parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return new MysqlxExpect.Open.Condition(input, extensionRegistry); } }; public static Parser<Condition> parser() { return PARSER; } public Parser<Condition> getParserForType() { return PARSER; } public Condition getDefaultInstanceForType() { return DEFAULT_INSTANCE; } } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 1344 */       if (isInitialized == 1) return true; 
/* 1345 */       if (isInitialized == 0) return false;
/*      */       
/* 1347 */       for (int i = 0; i < getCondCount(); i++) {
/* 1348 */         if (!getCond(i).isInitialized()) {
/* 1349 */           this.memoizedIsInitialized = 0;
/* 1350 */           return false;
/*      */         } 
/*      */       } 
/* 1353 */       this.memoizedIsInitialized = 1;
/* 1354 */       return true; } public boolean hasOp() { return ((this.bitField0_ & 0x1) != 0); } public CtxOperation getOp() { CtxOperation result = CtxOperation.valueOf(this.op_); return (result == null) ? CtxOperation.EXPECT_CTX_COPY_PREV : result; }
/*      */     public List<Condition> getCondList() { return this.cond_; }
/*      */     public List<? extends ConditionOrBuilder> getCondOrBuilderList() { return (List)this.cond_; }
/*      */     public int getCondCount() { return this.cond_.size(); }
/*      */     public Condition getCond(int index) { return this.cond_.get(index); }
/*      */     public ConditionOrBuilder getCondOrBuilder(int index) { return this.cond_.get(index); }
/* 1360 */     public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) {
/* 1361 */         output.writeEnum(1, this.op_);
/*      */       }
/* 1363 */       for (int i = 0; i < this.cond_.size(); i++) {
/* 1364 */         output.writeMessage(2, (MessageLite)this.cond_.get(i));
/*      */       }
/* 1366 */       this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 1371 */       int size = this.memoizedSize;
/* 1372 */       if (size != -1) return size;
/*      */       
/* 1374 */       size = 0;
/* 1375 */       if ((this.bitField0_ & 0x1) != 0) {
/* 1376 */         size += 
/* 1377 */           CodedOutputStream.computeEnumSize(1, this.op_);
/*      */       }
/* 1379 */       for (int i = 0; i < this.cond_.size(); i++) {
/* 1380 */         size += 
/* 1381 */           CodedOutputStream.computeMessageSize(2, (MessageLite)this.cond_.get(i));
/*      */       }
/* 1383 */       size += this.unknownFields.getSerializedSize();
/* 1384 */       this.memoizedSize = size;
/* 1385 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1390 */       if (obj == this) {
/* 1391 */         return true;
/*      */       }
/* 1393 */       if (!(obj instanceof Open)) {
/* 1394 */         return super.equals(obj);
/*      */       }
/* 1396 */       Open other = (Open)obj;
/*      */       
/* 1398 */       if (hasOp() != other.hasOp()) return false; 
/* 1399 */       if (hasOp() && 
/* 1400 */         this.op_ != other.op_) return false;
/*      */ 
/*      */       
/* 1403 */       if (!getCondList().equals(other.getCondList())) return false; 
/* 1404 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 1405 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1410 */       if (this.memoizedHashCode != 0) {
/* 1411 */         return this.memoizedHashCode;
/*      */       }
/* 1413 */       int hash = 41;
/* 1414 */       hash = 19 * hash + getDescriptor().hashCode();
/* 1415 */       if (hasOp()) {
/* 1416 */         hash = 37 * hash + 1;
/* 1417 */         hash = 53 * hash + this.op_;
/*      */       } 
/* 1419 */       if (getCondCount() > 0) {
/* 1420 */         hash = 37 * hash + 2;
/* 1421 */         hash = 53 * hash + getCondList().hashCode();
/*      */       } 
/* 1423 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 1424 */       this.memoizedHashCode = hash;
/* 1425 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Open parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 1431 */       return (Open)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Open parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1437 */       return (Open)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Open parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 1442 */       return (Open)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Open parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1448 */       return (Open)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Open parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 1452 */       return (Open)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Open parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1458 */       return (Open)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Open parseFrom(InputStream input) throws IOException {
/* 1462 */       return 
/* 1463 */         (Open)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Open parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1469 */       return 
/* 1470 */         (Open)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Open parseDelimitedFrom(InputStream input) throws IOException {
/* 1474 */       return 
/* 1475 */         (Open)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Open parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1481 */       return 
/* 1482 */         (Open)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Open parseFrom(CodedInputStream input) throws IOException {
/* 1487 */       return 
/* 1488 */         (Open)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Open parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1494 */       return 
/* 1495 */         (Open)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 1499 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 1501 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Open prototype) {
/* 1504 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 1508 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 1509 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 1515 */       Builder builder = new Builder(parent);
/* 1516 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxExpect.OpenOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */       
/*      */       private int op_;
/*      */       
/*      */       private List<MysqlxExpect.Open.Condition> cond_;
/*      */       
/*      */       private RepeatedFieldBuilderV3<MysqlxExpect.Open.Condition, MysqlxExpect.Open.Condition.Builder, MysqlxExpect.Open.ConditionOrBuilder> condBuilder_;
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 1536 */         return MysqlxExpect.internal_static_Mysqlx_Expect_Open_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 1542 */         return MysqlxExpect.internal_static_Mysqlx_Expect_Open_fieldAccessorTable
/* 1543 */           .ensureFieldAccessorsInitialized(MysqlxExpect.Open.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/* 1728 */         this.op_ = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1770 */         this
/* 1771 */           .cond_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.op_ = 0; this.cond_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxExpect.Open.alwaysUseFieldBuilders) getCondFieldBuilder();  } public Builder clear() { super.clear(); this.op_ = 0; this.bitField0_ &= 0xFFFFFFFE; if (this.condBuilder_ == null) { this.cond_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFD; } else { this.condBuilder_.clear(); }  return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxExpect.internal_static_Mysqlx_Expect_Open_descriptor; } public MysqlxExpect.Open getDefaultInstanceForType() { return MysqlxExpect.Open.getDefaultInstance(); } public MysqlxExpect.Open build() { MysqlxExpect.Open result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxExpect.Open buildPartial() { MysqlxExpect.Open result = new MysqlxExpect.Open(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.op_ = this.op_; if (this.condBuilder_ == null) { if ((this.bitField0_ & 0x2) != 0) { this.cond_ = Collections.unmodifiableList(this.cond_); this.bitField0_ &= 0xFFFFFFFD; }  result.cond_ = this.cond_; } else { result.cond_ = this.condBuilder_.build(); }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxExpect.Open) return mergeFrom((MysqlxExpect.Open)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxExpect.Open other) { if (other == MysqlxExpect.Open.getDefaultInstance()) return this;  if (other.hasOp()) setOp(other.getOp());  if (this.condBuilder_ == null) { if (!other.cond_.isEmpty()) { if (this.cond_.isEmpty()) { this.cond_ = other.cond_; this.bitField0_ &= 0xFFFFFFFD; } else { ensureCondIsMutable(); this.cond_.addAll(other.cond_); }  onChanged(); }  } else if (!other.cond_.isEmpty()) { if (this.condBuilder_.isEmpty()) { this.condBuilder_.dispose(); this.condBuilder_ = null; this.cond_ = other.cond_; this.bitField0_ &= 0xFFFFFFFD; this.condBuilder_ = MysqlxExpect.Open.alwaysUseFieldBuilders ? getCondFieldBuilder() : null; } else { this.condBuilder_.addAllMessages(other.cond_); }  }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { for (int i = 0; i < getCondCount(); i++) { if (!getCond(i).isInitialized()) return false;  }  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxExpect.Open parsedMessage = null; try { parsedMessage = (MysqlxExpect.Open)MysqlxExpect.Open.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxExpect.Open)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasOp() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxExpect.Open.CtxOperation getOp() { MysqlxExpect.Open.CtxOperation result = MysqlxExpect.Open.CtxOperation.valueOf(this.op_); return (result == null) ? MysqlxExpect.Open.CtxOperation.EXPECT_CTX_COPY_PREV : result; } public Builder setOp(MysqlxExpect.Open.CtxOperation value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.op_ = value.getNumber(); onChanged(); return this; }
/*      */       public Builder clearOp() { this.bitField0_ &= 0xFFFFFFFE; this.op_ = 0; onChanged(); return this; }
/* 1773 */       private void ensureCondIsMutable() { if ((this.bitField0_ & 0x2) == 0) {
/* 1774 */           this.cond_ = new ArrayList<>(this.cond_);
/* 1775 */           this.bitField0_ |= 0x2;
/*      */         }  }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<MysqlxExpect.Open.Condition> getCondList() {
/* 1786 */         if (this.condBuilder_ == null) {
/* 1787 */           return Collections.unmodifiableList(this.cond_);
/*      */         }
/* 1789 */         return this.condBuilder_.getMessageList();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int getCondCount() {
/* 1796 */         if (this.condBuilder_ == null) {
/* 1797 */           return this.cond_.size();
/*      */         }
/* 1799 */         return this.condBuilder_.getCount();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxExpect.Open.Condition getCond(int index) {
/* 1806 */         if (this.condBuilder_ == null) {
/* 1807 */           return this.cond_.get(index);
/*      */         }
/* 1809 */         return (MysqlxExpect.Open.Condition)this.condBuilder_.getMessage(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setCond(int index, MysqlxExpect.Open.Condition value) {
/* 1817 */         if (this.condBuilder_ == null) {
/* 1818 */           if (value == null) {
/* 1819 */             throw new NullPointerException();
/*      */           }
/* 1821 */           ensureCondIsMutable();
/* 1822 */           this.cond_.set(index, value);
/* 1823 */           onChanged();
/*      */         } else {
/* 1825 */           this.condBuilder_.setMessage(index, (AbstractMessage)value);
/*      */         } 
/* 1827 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setCond(int index, MysqlxExpect.Open.Condition.Builder builderForValue) {
/* 1834 */         if (this.condBuilder_ == null) {
/* 1835 */           ensureCondIsMutable();
/* 1836 */           this.cond_.set(index, builderForValue.build());
/* 1837 */           onChanged();
/*      */         } else {
/* 1839 */           this.condBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 1841 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addCond(MysqlxExpect.Open.Condition value) {
/* 1847 */         if (this.condBuilder_ == null) {
/* 1848 */           if (value == null) {
/* 1849 */             throw new NullPointerException();
/*      */           }
/* 1851 */           ensureCondIsMutable();
/* 1852 */           this.cond_.add(value);
/* 1853 */           onChanged();
/*      */         } else {
/* 1855 */           this.condBuilder_.addMessage((AbstractMessage)value);
/*      */         } 
/* 1857 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addCond(int index, MysqlxExpect.Open.Condition value) {
/* 1864 */         if (this.condBuilder_ == null) {
/* 1865 */           if (value == null) {
/* 1866 */             throw new NullPointerException();
/*      */           }
/* 1868 */           ensureCondIsMutable();
/* 1869 */           this.cond_.add(index, value);
/* 1870 */           onChanged();
/*      */         } else {
/* 1872 */           this.condBuilder_.addMessage(index, (AbstractMessage)value);
/*      */         } 
/* 1874 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addCond(MysqlxExpect.Open.Condition.Builder builderForValue) {
/* 1881 */         if (this.condBuilder_ == null) {
/* 1882 */           ensureCondIsMutable();
/* 1883 */           this.cond_.add(builderForValue.build());
/* 1884 */           onChanged();
/*      */         } else {
/* 1886 */           this.condBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 1888 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addCond(int index, MysqlxExpect.Open.Condition.Builder builderForValue) {
/* 1895 */         if (this.condBuilder_ == null) {
/* 1896 */           ensureCondIsMutable();
/* 1897 */           this.cond_.add(index, builderForValue.build());
/* 1898 */           onChanged();
/*      */         } else {
/* 1900 */           this.condBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 1902 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addAllCond(Iterable<? extends MysqlxExpect.Open.Condition> values) {
/* 1909 */         if (this.condBuilder_ == null) {
/* 1910 */           ensureCondIsMutable();
/* 1911 */           AbstractMessageLite.Builder.addAll(values, this.cond_);
/*      */           
/* 1913 */           onChanged();
/*      */         } else {
/* 1915 */           this.condBuilder_.addAllMessages(values);
/*      */         } 
/* 1917 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearCond() {
/* 1923 */         if (this.condBuilder_ == null) {
/* 1924 */           this.cond_ = Collections.emptyList();
/* 1925 */           this.bitField0_ &= 0xFFFFFFFD;
/* 1926 */           onChanged();
/*      */         } else {
/* 1928 */           this.condBuilder_.clear();
/*      */         } 
/* 1930 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder removeCond(int index) {
/* 1936 */         if (this.condBuilder_ == null) {
/* 1937 */           ensureCondIsMutable();
/* 1938 */           this.cond_.remove(index);
/* 1939 */           onChanged();
/*      */         } else {
/* 1941 */           this.condBuilder_.remove(index);
/*      */         } 
/* 1943 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxExpect.Open.Condition.Builder getCondBuilder(int index) {
/* 1950 */         return (MysqlxExpect.Open.Condition.Builder)getCondFieldBuilder().getBuilder(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxExpect.Open.ConditionOrBuilder getCondOrBuilder(int index) {
/* 1957 */         if (this.condBuilder_ == null)
/* 1958 */           return this.cond_.get(index); 
/* 1959 */         return (MysqlxExpect.Open.ConditionOrBuilder)this.condBuilder_.getMessageOrBuilder(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<? extends MysqlxExpect.Open.ConditionOrBuilder> getCondOrBuilderList() {
/* 1967 */         if (this.condBuilder_ != null) {
/* 1968 */           return this.condBuilder_.getMessageOrBuilderList();
/*      */         }
/* 1970 */         return Collections.unmodifiableList((List)this.cond_);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxExpect.Open.Condition.Builder addCondBuilder() {
/* 1977 */         return (MysqlxExpect.Open.Condition.Builder)getCondFieldBuilder().addBuilder(
/* 1978 */             (AbstractMessage)MysqlxExpect.Open.Condition.getDefaultInstance());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxExpect.Open.Condition.Builder addCondBuilder(int index) {
/* 1985 */         return (MysqlxExpect.Open.Condition.Builder)getCondFieldBuilder().addBuilder(index, 
/* 1986 */             (AbstractMessage)MysqlxExpect.Open.Condition.getDefaultInstance());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<MysqlxExpect.Open.Condition.Builder> getCondBuilderList() {
/* 1993 */         return getCondFieldBuilder().getBuilderList();
/*      */       }
/*      */ 
/*      */       
/*      */       private RepeatedFieldBuilderV3<MysqlxExpect.Open.Condition, MysqlxExpect.Open.Condition.Builder, MysqlxExpect.Open.ConditionOrBuilder> getCondFieldBuilder() {
/* 1998 */         if (this.condBuilder_ == null) {
/* 1999 */           this
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2004 */             .condBuilder_ = new RepeatedFieldBuilderV3(this.cond_, ((this.bitField0_ & 0x2) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 2005 */           this.cond_ = null;
/*      */         } 
/* 2007 */         return this.condBuilder_;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 2012 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 2018 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2028 */     private static final Open DEFAULT_INSTANCE = new Open();
/*      */ 
/*      */     
/*      */     public static Open getDefaultInstance() {
/* 2032 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 2036 */     public static final Parser<Open> PARSER = (Parser<Open>)new AbstractParser<Open>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxExpect.Open parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 2042 */           return new MysqlxExpect.Open(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Open> parser() {
/* 2047 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Open> getParserForType() {
/* 2052 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Open getDefaultInstanceForType() {
/* 2057 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     public static interface ConditionOrBuilder
/*      */       extends MessageOrBuilder {
/*      */       boolean hasConditionKey();
/*      */       
/*      */       int getConditionKey();
/*      */       
/*      */       boolean hasConditionValue();
/*      */       
/*      */       ByteString getConditionValue();
/*      */       
/*      */       boolean hasOp();
/*      */       
/*      */       MysqlxExpect.Open.Condition.ConditionOperation getOp();
/*      */     }
/*      */   }
/*      */   
/*      */   public static interface CloseOrBuilder
/*      */     extends MessageOrBuilder {}
/*      */   
/*      */   public static final class Close extends GeneratedMessageV3 implements CloseOrBuilder {
/*      */     private static final long serialVersionUID = 0L;
/*      */     private byte memoizedIsInitialized;
/*      */     
/*      */     private Close(GeneratedMessageV3.Builder<?> builder) {
/* 2084 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2151 */       this.memoizedIsInitialized = -1; } private Close() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Close(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Close(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/*      */     public static final Descriptors.Descriptor getDescriptor() { return MysqlxExpect.internal_static_Mysqlx_Expect_Close_descriptor; }
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxExpect.internal_static_Mysqlx_Expect_Close_fieldAccessorTable.ensureFieldAccessorsInitialized(Close.class, Builder.class); }
/* 2154 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 2155 */       if (isInitialized == 1) return true; 
/* 2156 */       if (isInitialized == 0) return false;
/*      */       
/* 2158 */       this.memoizedIsInitialized = 1;
/* 2159 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 2165 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 2170 */       int size = this.memoizedSize;
/* 2171 */       if (size != -1) return size;
/*      */       
/* 2173 */       size = 0;
/* 2174 */       size += this.unknownFields.getSerializedSize();
/* 2175 */       this.memoizedSize = size;
/* 2176 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 2181 */       if (obj == this) {
/* 2182 */         return true;
/*      */       }
/* 2184 */       if (!(obj instanceof Close)) {
/* 2185 */         return super.equals(obj);
/*      */       }
/* 2187 */       Close other = (Close)obj;
/*      */       
/* 2189 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 2190 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 2195 */       if (this.memoizedHashCode != 0) {
/* 2196 */         return this.memoizedHashCode;
/*      */       }
/* 2198 */       int hash = 41;
/* 2199 */       hash = 19 * hash + getDescriptor().hashCode();
/* 2200 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 2201 */       this.memoizedHashCode = hash;
/* 2202 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 2208 */       return (Close)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2214 */       return (Close)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 2219 */       return (Close)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2225 */       return (Close)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Close parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 2229 */       return (Close)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2235 */       return (Close)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Close parseFrom(InputStream input) throws IOException {
/* 2239 */       return 
/* 2240 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2246 */       return 
/* 2247 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Close parseDelimitedFrom(InputStream input) throws IOException {
/* 2251 */       return 
/* 2252 */         (Close)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2258 */       return 
/* 2259 */         (Close)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Close parseFrom(CodedInputStream input) throws IOException {
/* 2264 */       return 
/* 2265 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2271 */       return 
/* 2272 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 2276 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 2278 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Close prototype) {
/* 2281 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 2285 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 2286 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 2292 */       Builder builder = new Builder(parent);
/* 2293 */       return builder;
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
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxExpect.CloseOrBuilder
/*      */     {
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 2312 */         return MysqlxExpect.internal_static_Mysqlx_Expect_Close_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 2318 */         return MysqlxExpect.internal_static_Mysqlx_Expect_Close_fieldAccessorTable
/* 2319 */           .ensureFieldAccessorsInitialized(MysqlxExpect.Close.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 2325 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 2330 */         super(parent);
/* 2331 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 2335 */         if (MysqlxExpect.Close.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/* 2340 */         super.clear();
/* 2341 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 2347 */         return MysqlxExpect.internal_static_Mysqlx_Expect_Close_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxExpect.Close getDefaultInstanceForType() {
/* 2352 */         return MysqlxExpect.Close.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxExpect.Close build() {
/* 2357 */         MysqlxExpect.Close result = buildPartial();
/* 2358 */         if (!result.isInitialized()) {
/* 2359 */           throw newUninitializedMessageException(result);
/*      */         }
/* 2361 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxExpect.Close buildPartial() {
/* 2366 */         MysqlxExpect.Close result = new MysqlxExpect.Close(this);
/* 2367 */         onBuilt();
/* 2368 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 2373 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 2379 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 2384 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 2389 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 2395 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 2401 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 2405 */         if (other instanceof MysqlxExpect.Close) {
/* 2406 */           return mergeFrom((MysqlxExpect.Close)other);
/*      */         }
/* 2408 */         super.mergeFrom(other);
/* 2409 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxExpect.Close other) {
/* 2414 */         if (other == MysqlxExpect.Close.getDefaultInstance()) return this; 
/* 2415 */         mergeUnknownFields(other.unknownFields);
/* 2416 */         onChanged();
/* 2417 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 2422 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2430 */         MysqlxExpect.Close parsedMessage = null;
/*      */         try {
/* 2432 */           parsedMessage = (MysqlxExpect.Close)MysqlxExpect.Close.PARSER.parsePartialFrom(input, extensionRegistry);
/* 2433 */         } catch (InvalidProtocolBufferException e) {
/* 2434 */           parsedMessage = (MysqlxExpect.Close)e.getUnfinishedMessage();
/* 2435 */           throw e.unwrapIOException();
/*      */         } finally {
/* 2437 */           if (parsedMessage != null) {
/* 2438 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 2441 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 2446 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 2452 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2462 */     private static final Close DEFAULT_INSTANCE = new Close();
/*      */ 
/*      */     
/*      */     public static Close getDefaultInstance() {
/* 2466 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 2470 */     public static final Parser<Close> PARSER = (Parser<Close>)new AbstractParser<Close>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxExpect.Close parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 2476 */           return new MysqlxExpect.Close(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Close> parser() {
/* 2481 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Close> getParserForType() {
/* 2486 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Close getDefaultInstanceForType() {
/* 2491 */       return DEFAULT_INSTANCE;
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
/*      */   public static Descriptors.FileDescriptor getDescriptor() {
/* 2514 */     return descriptor;
/*      */   }
/*      */ 
/*      */   
/*      */   static {
/* 2519 */     String[] descriptorData = { "\n\023mysqlx_expect.proto\022\rMysqlx.Expect\032\fmysqlx.proto\"Ö\003\n\004Open\022B\n\002op\030\001 \001(\0162 .Mysqlx.Expect.Open.CtxOperation:\024EXPECT_CTX_COPY_PREV\022+\n\004cond\030\002 \003(\0132\035.Mysqlx.Expect.Open.Condition\032\002\n\tCondition\022\025\n\rcondition_key\030\001 \002(\r\022\027\n\017condition_value\030\002 \001(\f\022K\n\002op\030\003 \001(\01620.Mysqlx.Expect.Open.Condition.ConditionOperation:\rEXPECT_OP_SET\"N\n\003Key\022\023\n\017EXPECT_NO_ERROR\020\001\022\026\n\022EXPECT_FIELD_EXIST\020\002\022\032\n\026EXPECT_DOCID_GENERATED\020\003\"<\n\022ConditionOperation\022\021\n\rEXPECT_OP_SET\020\000\022\023\n\017EXPECT_OP_UNSET\020\001\">\n\fCtxOperation\022\030\n\024EXPECT_CTX_COPY_PREV\020\000\022\024\n\020EXPECT_CTX_EMPTY\020\001:\004ê0\030\"\r\n\005Close:\004ê0\031B\031\n\027com.mysql.cj.x.protobuf" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2537 */     descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] {
/*      */           
/* 2539 */           Mysqlx.getDescriptor()
/*      */         });
/*      */     
/* 2542 */     internal_static_Mysqlx_Expect_Open_descriptor = getDescriptor().getMessageTypes().get(0);
/* 2543 */     internal_static_Mysqlx_Expect_Open_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expect_Open_descriptor, new String[] { "Op", "Cond" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2548 */     internal_static_Mysqlx_Expect_Open_Condition_descriptor = internal_static_Mysqlx_Expect_Open_descriptor.getNestedTypes().get(0);
/* 2549 */     internal_static_Mysqlx_Expect_Open_Condition_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expect_Open_Condition_descriptor, new String[] { "ConditionKey", "ConditionValue", "Op" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2554 */     internal_static_Mysqlx_Expect_Close_descriptor = getDescriptor().getMessageTypes().get(1);
/* 2555 */     internal_static_Mysqlx_Expect_Close_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expect_Close_descriptor, new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2560 */     ExtensionRegistry registry = ExtensionRegistry.newInstance();
/* 2561 */     registry.add(Mysqlx.clientMessageId);
/*      */     
/* 2563 */     Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
/* 2564 */     Mysqlx.getDescriptor();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\x\protobuf\MysqlxExpect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */