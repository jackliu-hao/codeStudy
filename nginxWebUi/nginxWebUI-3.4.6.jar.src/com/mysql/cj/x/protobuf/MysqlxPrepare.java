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
/*      */ import com.google.protobuf.SingleFieldBuilderV3;
/*      */ import com.google.protobuf.UnknownFieldSet;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ 
/*      */ public final class MysqlxPrepare {
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Prepare_Prepare_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Prepare_Prepare_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Prepare_Execute_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Prepare_Execute_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Prepare_Deallocate_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Prepare_Deallocate_fieldAccessorTable;
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
/*      */   public static interface PrepareOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasStmtId();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getStmtId();
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
/*      */     MysqlxPrepare.Prepare.OneOfMessage getStmt();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxPrepare.Prepare.OneOfMessageOrBuilder getStmtOrBuilder();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Prepare
/*      */     extends GeneratedMessageV3
/*      */     implements PrepareOrBuilder
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
/*      */     public static final int STMT_ID_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int stmtId_;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int STMT_FIELD_NUMBER = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private OneOfMessage stmt_;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Prepare(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/*  126 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2026 */       this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Prepare(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Prepare(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { OneOfMessage.Builder subBuilder; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.bitField0_ |= 0x1; this.stmtId_ = input.readUInt32(); continue;case 18: subBuilder = null; if ((this.bitField0_ & 0x2) != 0) subBuilder = this.stmt_.toBuilder();  this.stmt_ = (OneOfMessage)input.readMessage(OneOfMessage.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.stmt_); this.stmt_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x2; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } private Prepare() { this.memoizedIsInitialized = -1; } public static final Descriptors.Descriptor getDescriptor() { return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_fieldAccessorTable.ensureFieldAccessorsInitialized(Prepare.class, Builder.class); } public static final class OneOfMessage extends GeneratedMessageV3 implements OneOfMessageOrBuilder {
/*      */       private static final long serialVersionUID = 0L; private int bitField0_; public static final int TYPE_FIELD_NUMBER = 1; private int type_; public static final int FIND_FIELD_NUMBER = 2; private MysqlxCrud.Find find_; public static final int INSERT_FIELD_NUMBER = 3; private MysqlxCrud.Insert insert_; public static final int UPDATE_FIELD_NUMBER = 4; private MysqlxCrud.Update update_; public static final int DELETE_FIELD_NUMBER = 5; private MysqlxCrud.Delete delete_; public static final int STMT_EXECUTE_FIELD_NUMBER = 6; private MysqlxSql.StmtExecute stmtExecute_; private byte memoizedIsInitialized; private OneOfMessage(GeneratedMessageV3.Builder<?> builder) { super(builder); this.memoizedIsInitialized = -1; } private OneOfMessage() { this.memoizedIsInitialized = -1; this.type_ = 0; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new OneOfMessage(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private OneOfMessage(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int rawValue; MysqlxCrud.Find.Builder builder3; MysqlxCrud.Insert.Builder builder2; MysqlxCrud.Update.Builder builder1; MysqlxCrud.Delete.Builder builder; MysqlxSql.StmtExecute.Builder subBuilder; Type value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: rawValue = input.readEnum(); value = Type.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(1, rawValue); continue; }  this.bitField0_ |= 0x1; this.type_ = rawValue; continue;case 18: builder3 = null; if ((this.bitField0_ & 0x2) != 0) builder3 = this.find_.toBuilder();  this.find_ = (MysqlxCrud.Find)input.readMessage(MysqlxCrud.Find.PARSER, extensionRegistry); if (builder3 != null) { builder3.mergeFrom(this.find_); this.find_ = builder3.buildPartial(); }  this.bitField0_ |= 0x2; continue;case 26: builder2 = null; if ((this.bitField0_ & 0x4) != 0) builder2 = this.insert_.toBuilder();  this.insert_ = (MysqlxCrud.Insert)input.readMessage(MysqlxCrud.Insert.PARSER, extensionRegistry); if (builder2 != null) { builder2.mergeFrom(this.insert_); this.insert_ = builder2.buildPartial(); }  this.bitField0_ |= 0x4; continue;case 34: builder1 = null; if ((this.bitField0_ & 0x8) != 0) builder1 = this.update_.toBuilder();  this.update_ = (MysqlxCrud.Update)input.readMessage(MysqlxCrud.Update.PARSER, extensionRegistry); if (builder1 != null) { builder1.mergeFrom(this.update_); this.update_ = builder1.buildPartial(); }  this.bitField0_ |= 0x8; continue;case 42: builder = null; if ((this.bitField0_ & 0x10) != 0) builder = this.delete_.toBuilder();  this.delete_ = (MysqlxCrud.Delete)input.readMessage(MysqlxCrud.Delete.PARSER, extensionRegistry); if (builder != null) { builder.mergeFrom(this.delete_); this.delete_ = builder.buildPartial(); }  this.bitField0_ |= 0x10; continue;case 50: subBuilder = null; if ((this.bitField0_ & 0x20) != 0) subBuilder = this.stmtExecute_.toBuilder();  this.stmtExecute_ = (MysqlxSql.StmtExecute)input.readMessage(MysqlxSql.StmtExecute.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.stmtExecute_); this.stmtExecute_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x20; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_fieldAccessorTable.ensureFieldAccessorsInitialized(OneOfMessage.class, Builder.class); } public enum Type implements ProtocolMessageEnum {
/*      */         FIND(0), INSERT(1), UPDATE(2), DELETE(4), STMT(5); public static final int FIND_VALUE = 0; public static final int INSERT_VALUE = 1; public static final int UPDATE_VALUE = 2; public static final int DELETE_VALUE = 4; public static final int STMT_VALUE = 5; private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() { public MysqlxPrepare.Prepare.OneOfMessage.Type findValueByNumber(int number) { return MysqlxPrepare.Prepare.OneOfMessage.Type.forNumber(number); } }; private static final Type[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Type forNumber(int value) { switch (value) { case 0: return FIND;case 1: return INSERT;case 2: return UPDATE;case 4: return DELETE;case 5: return STMT; }  return null; } public static Internal.EnumLiteMap<Type> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxPrepare.Prepare.OneOfMessage.getDescriptor().getEnumTypes().get(0); } Type(int value) { this.value = value; } } public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); } public Type getType() { Type result = Type.valueOf(this.type_); return (result == null) ? Type.FIND : result; } public boolean hasFind() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxCrud.Find getFind() { return (this.find_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.find_; } public MysqlxCrud.FindOrBuilder getFindOrBuilder() { return (this.find_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.find_; } public boolean hasInsert() { return ((this.bitField0_ & 0x4) != 0); } public MysqlxCrud.Insert getInsert() { return (this.insert_ == null) ? MysqlxCrud.Insert.getDefaultInstance() : this.insert_; } public MysqlxCrud.InsertOrBuilder getInsertOrBuilder() { return (this.insert_ == null) ? MysqlxCrud.Insert.getDefaultInstance() : this.insert_; } public boolean hasUpdate() { return ((this.bitField0_ & 0x8) != 0); } public MysqlxCrud.Update getUpdate() { return (this.update_ == null) ? MysqlxCrud.Update.getDefaultInstance() : this.update_; } public MysqlxCrud.UpdateOrBuilder getUpdateOrBuilder() { return (this.update_ == null) ? MysqlxCrud.Update.getDefaultInstance() : this.update_; } public boolean hasDelete() { return ((this.bitField0_ & 0x10) != 0); } public MysqlxCrud.Delete getDelete() { return (this.delete_ == null) ? MysqlxCrud.Delete.getDefaultInstance() : this.delete_; } public MysqlxCrud.DeleteOrBuilder getDeleteOrBuilder() { return (this.delete_ == null) ? MysqlxCrud.Delete.getDefaultInstance() : this.delete_; } public boolean hasStmtExecute() { return ((this.bitField0_ & 0x20) != 0); } public MysqlxSql.StmtExecute getStmtExecute() { return (this.stmtExecute_ == null) ? MysqlxSql.StmtExecute.getDefaultInstance() : this.stmtExecute_; } public MysqlxSql.StmtExecuteOrBuilder getStmtExecuteOrBuilder() { return (this.stmtExecute_ == null) ? MysqlxSql.StmtExecute.getDefaultInstance() : this.stmtExecute_; } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized; if (isInitialized == 1) return true;  if (isInitialized == 0) return false;  if (!hasType()) { this.memoizedIsInitialized = 0; return false; }  if (hasFind() && !getFind().isInitialized()) { this.memoizedIsInitialized = 0; return false; }  if (hasInsert() && !getInsert().isInitialized()) { this.memoizedIsInitialized = 0; return false; }  if (hasUpdate() && !getUpdate().isInitialized()) { this.memoizedIsInitialized = 0; return false; }  if (hasDelete() && !getDelete().isInitialized()) { this.memoizedIsInitialized = 0; return false; }  if (hasStmtExecute() && !getStmtExecute().isInitialized()) { this.memoizedIsInitialized = 0; return false; }  this.memoizedIsInitialized = 1; return true; } public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) output.writeEnum(1, this.type_);  if ((this.bitField0_ & 0x2) != 0) output.writeMessage(2, (MessageLite)getFind());  if ((this.bitField0_ & 0x4) != 0) output.writeMessage(3, (MessageLite)getInsert());  if ((this.bitField0_ & 0x8) != 0) output.writeMessage(4, (MessageLite)getUpdate());  if ((this.bitField0_ & 0x10) != 0) output.writeMessage(5, (MessageLite)getDelete());  if ((this.bitField0_ & 0x20) != 0) output.writeMessage(6, (MessageLite)getStmtExecute());  this.unknownFields.writeTo(output); } public int getSerializedSize() { int size = this.memoizedSize; if (size != -1) return size;  size = 0; if ((this.bitField0_ & 0x1) != 0) size += CodedOutputStream.computeEnumSize(1, this.type_);  if ((this.bitField0_ & 0x2) != 0) size += CodedOutputStream.computeMessageSize(2, (MessageLite)getFind());  if ((this.bitField0_ & 0x4) != 0) size += CodedOutputStream.computeMessageSize(3, (MessageLite)getInsert());  if ((this.bitField0_ & 0x8) != 0) size += CodedOutputStream.computeMessageSize(4, (MessageLite)getUpdate());  if ((this.bitField0_ & 0x10) != 0) size += CodedOutputStream.computeMessageSize(5, (MessageLite)getDelete());  if ((this.bitField0_ & 0x20) != 0) size += CodedOutputStream.computeMessageSize(6, (MessageLite)getStmtExecute());  size += this.unknownFields.getSerializedSize(); this.memoizedSize = size; return size; } public boolean equals(Object obj) { if (obj == this) return true;  if (!(obj instanceof OneOfMessage)) return super.equals(obj);  OneOfMessage other = (OneOfMessage)obj; if (hasType() != other.hasType()) return false;  if (hasType() && this.type_ != other.type_) return false;  if (hasFind() != other.hasFind()) return false;  if (hasFind() && !getFind().equals(other.getFind())) return false;  if (hasInsert() != other.hasInsert()) return false;  if (hasInsert() && !getInsert().equals(other.getInsert())) return false;  if (hasUpdate() != other.hasUpdate()) return false;  if (hasUpdate() && !getUpdate().equals(other.getUpdate())) return false;  if (hasDelete() != other.hasDelete()) return false;  if (hasDelete() && !getDelete().equals(other.getDelete())) return false;  if (hasStmtExecute() != other.hasStmtExecute()) return false;  if (hasStmtExecute() && !getStmtExecute().equals(other.getStmtExecute())) return false;  if (!this.unknownFields.equals(other.unknownFields)) return false;  return true; } public int hashCode() { if (this.memoizedHashCode != 0) return this.memoizedHashCode;  int hash = 41; hash = 19 * hash + getDescriptor().hashCode(); if (hasType()) { hash = 37 * hash + 1; hash = 53 * hash + this.type_; }  if (hasFind()) { hash = 37 * hash + 2; hash = 53 * hash + getFind().hashCode(); }  if (hasInsert()) { hash = 37 * hash + 3; hash = 53 * hash + getInsert().hashCode(); }  if (hasUpdate()) { hash = 37 * hash + 4; hash = 53 * hash + getUpdate().hashCode(); }  if (hasDelete()) { hash = 37 * hash + 5; hash = 53 * hash + getDelete().hashCode(); }  if (hasStmtExecute()) { hash = 37 * hash + 6; hash = 53 * hash + getStmtExecute().hashCode(); }  hash = 29 * hash + this.unknownFields.hashCode(); this.memoizedHashCode = hash; return hash; } public static OneOfMessage parseFrom(ByteBuffer data) throws InvalidProtocolBufferException { return (OneOfMessage)PARSER.parseFrom(data); } public static OneOfMessage parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (OneOfMessage)PARSER.parseFrom(data, extensionRegistry); } public static OneOfMessage parseFrom(ByteString data) throws InvalidProtocolBufferException { return (OneOfMessage)PARSER.parseFrom(data); } public static OneOfMessage parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (OneOfMessage)PARSER.parseFrom(data, extensionRegistry); } public static OneOfMessage parseFrom(byte[] data) throws InvalidProtocolBufferException { return (OneOfMessage)PARSER.parseFrom(data); } public static OneOfMessage parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (OneOfMessage)PARSER.parseFrom(data, extensionRegistry); } public static OneOfMessage parseFrom(InputStream input) throws IOException { return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static OneOfMessage parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public static OneOfMessage parseDelimitedFrom(InputStream input) throws IOException { return (OneOfMessage)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input); } public static OneOfMessage parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (OneOfMessage)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry); } public static OneOfMessage parseFrom(CodedInputStream input) throws IOException { return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static OneOfMessage parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (OneOfMessage)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public Builder newBuilderForType() { return newBuilder(); } public static Builder newBuilder() { return DEFAULT_INSTANCE.toBuilder(); } public static Builder newBuilder(OneOfMessage prototype) { return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype); } public Builder toBuilder() { return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder()).mergeFrom(this); } protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) { Builder builder = new Builder(parent); return builder; } public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxPrepare.Prepare.OneOfMessageOrBuilder {
/* 2029 */         private int bitField0_; private int type_; private MysqlxCrud.Find find_; private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> findBuilder_; private MysqlxCrud.Insert insert_; private SingleFieldBuilderV3<MysqlxCrud.Insert, MysqlxCrud.Insert.Builder, MysqlxCrud.InsertOrBuilder> insertBuilder_; private MysqlxCrud.Update update_; private SingleFieldBuilderV3<MysqlxCrud.Update, MysqlxCrud.Update.Builder, MysqlxCrud.UpdateOrBuilder> updateBuilder_; private MysqlxCrud.Delete delete_; private SingleFieldBuilderV3<MysqlxCrud.Delete, MysqlxCrud.Delete.Builder, MysqlxCrud.DeleteOrBuilder> deleteBuilder_; private MysqlxSql.StmtExecute stmtExecute_; private SingleFieldBuilderV3<MysqlxSql.StmtExecute, MysqlxSql.StmtExecute.Builder, MysqlxSql.StmtExecuteOrBuilder> stmtExecuteBuilder_; public static final Descriptors.Descriptor getDescriptor() { return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxPrepare.Prepare.OneOfMessage.class, Builder.class); } private Builder() { this.type_ = 0; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.type_ = 0; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxPrepare.Prepare.OneOfMessage.alwaysUseFieldBuilders) { getFindFieldBuilder(); getInsertFieldBuilder(); getUpdateFieldBuilder(); getDeleteFieldBuilder(); getStmtExecuteFieldBuilder(); }  } public Builder clear() { super.clear(); this.type_ = 0; this.bitField0_ &= 0xFFFFFFFE; if (this.findBuilder_ == null) { this.find_ = null; } else { this.findBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; if (this.insertBuilder_ == null) { this.insert_ = null; } else { this.insertBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFB; if (this.updateBuilder_ == null) { this.update_ = null; } else { this.updateBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFF7; if (this.deleteBuilder_ == null) { this.delete_ = null; } else { this.deleteBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFEF; if (this.stmtExecuteBuilder_ == null) { this.stmtExecute_ = null; } else { this.stmtExecuteBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFDF; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor; } public MysqlxPrepare.Prepare.OneOfMessage getDefaultInstanceForType() { return MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance(); } public MysqlxPrepare.Prepare.OneOfMessage build() { MysqlxPrepare.Prepare.OneOfMessage result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxPrepare.Prepare.OneOfMessage buildPartial() { MysqlxPrepare.Prepare.OneOfMessage result = new MysqlxPrepare.Prepare.OneOfMessage(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.type_ = this.type_; if ((from_bitField0_ & 0x2) != 0) { if (this.findBuilder_ == null) { result.find_ = this.find_; } else { result.find_ = (MysqlxCrud.Find)this.findBuilder_.build(); }  to_bitField0_ |= 0x2; }  if ((from_bitField0_ & 0x4) != 0) { if (this.insertBuilder_ == null) { result.insert_ = this.insert_; } else { result.insert_ = (MysqlxCrud.Insert)this.insertBuilder_.build(); }  to_bitField0_ |= 0x4; }  if ((from_bitField0_ & 0x8) != 0) { if (this.updateBuilder_ == null) { result.update_ = this.update_; } else { result.update_ = (MysqlxCrud.Update)this.updateBuilder_.build(); }  to_bitField0_ |= 0x8; }  if ((from_bitField0_ & 0x10) != 0) { if (this.deleteBuilder_ == null) { result.delete_ = this.delete_; } else { result.delete_ = (MysqlxCrud.Delete)this.deleteBuilder_.build(); }  to_bitField0_ |= 0x10; }  if ((from_bitField0_ & 0x20) != 0) { if (this.stmtExecuteBuilder_ == null) { result.stmtExecute_ = this.stmtExecute_; } else { result.stmtExecute_ = (MysqlxSql.StmtExecute)this.stmtExecuteBuilder_.build(); }  to_bitField0_ |= 0x20; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxPrepare.Prepare.OneOfMessage) return mergeFrom((MysqlxPrepare.Prepare.OneOfMessage)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxPrepare.Prepare.OneOfMessage other) { if (other == MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance()) return this;  if (other.hasType()) setType(other.getType());  if (other.hasFind()) mergeFind(other.getFind());  if (other.hasInsert()) mergeInsert(other.getInsert());  if (other.hasUpdate()) mergeUpdate(other.getUpdate());  if (other.hasDelete()) mergeDelete(other.getDelete());  if (other.hasStmtExecute()) mergeStmtExecute(other.getStmtExecute());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasType()) return false;  if (hasFind() && !getFind().isInitialized()) return false;  if (hasInsert() && !getInsert().isInitialized()) return false;  if (hasUpdate() && !getUpdate().isInitialized()) return false;  if (hasDelete() && !getDelete().isInitialized()) return false;  if (hasStmtExecute() && !getStmtExecute().isInitialized()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxPrepare.Prepare.OneOfMessage parsedMessage = null; try { parsedMessage = (MysqlxPrepare.Prepare.OneOfMessage)MysqlxPrepare.Prepare.OneOfMessage.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxPrepare.Prepare.OneOfMessage)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxPrepare.Prepare.OneOfMessage.Type getType() { MysqlxPrepare.Prepare.OneOfMessage.Type result = MysqlxPrepare.Prepare.OneOfMessage.Type.valueOf(this.type_); return (result == null) ? MysqlxPrepare.Prepare.OneOfMessage.Type.FIND : result; } public Builder setType(MysqlxPrepare.Prepare.OneOfMessage.Type value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.type_ = value.getNumber(); onChanged(); return this; } public Builder clearType() { this.bitField0_ &= 0xFFFFFFFE; this.type_ = 0; onChanged(); return this; } public boolean hasFind() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxCrud.Find getFind() { if (this.findBuilder_ == null) return (this.find_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.find_;  return (MysqlxCrud.Find)this.findBuilder_.getMessage(); } public Builder setFind(MysqlxCrud.Find value) { if (this.findBuilder_ == null) { if (value == null) throw new NullPointerException();  this.find_ = value; onChanged(); } else { this.findBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x2; return this; } public Builder setFind(MysqlxCrud.Find.Builder builderForValue) { if (this.findBuilder_ == null) { this.find_ = builderForValue.build(); onChanged(); } else { this.findBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x2; return this; } public Builder mergeFind(MysqlxCrud.Find value) { if (this.findBuilder_ == null) { if ((this.bitField0_ & 0x2) != 0 && this.find_ != null && this.find_ != MysqlxCrud.Find.getDefaultInstance()) { this.find_ = MysqlxCrud.Find.newBuilder(this.find_).mergeFrom(value).buildPartial(); } else { this.find_ = value; }  onChanged(); } else { this.findBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x2; return this; } public Builder clearFind() { if (this.findBuilder_ == null) { this.find_ = null; onChanged(); } else { this.findBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; return this; } public MysqlxCrud.Find.Builder getFindBuilder() { this.bitField0_ |= 0x2; onChanged(); return (MysqlxCrud.Find.Builder)getFindFieldBuilder().getBuilder(); } public MysqlxCrud.FindOrBuilder getFindOrBuilder() { if (this.findBuilder_ != null) return (MysqlxCrud.FindOrBuilder)this.findBuilder_.getMessageOrBuilder();  return (this.find_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.find_; } private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> getFindFieldBuilder() { if (this.findBuilder_ == null) { this.findBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getFind(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.find_ = null; }  return this.findBuilder_; } public boolean hasInsert() { return ((this.bitField0_ & 0x4) != 0); } public MysqlxCrud.Insert getInsert() { if (this.insertBuilder_ == null) return (this.insert_ == null) ? MysqlxCrud.Insert.getDefaultInstance() : this.insert_;  return (MysqlxCrud.Insert)this.insertBuilder_.getMessage(); } public Builder setInsert(MysqlxCrud.Insert value) { if (this.insertBuilder_ == null) { if (value == null) throw new NullPointerException();  this.insert_ = value; onChanged(); } else { this.insertBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x4; return this; } public Builder setInsert(MysqlxCrud.Insert.Builder builderForValue) { if (this.insertBuilder_ == null) { this.insert_ = builderForValue.build(); onChanged(); } else { this.insertBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x4; return this; } public Builder mergeInsert(MysqlxCrud.Insert value) { if (this.insertBuilder_ == null) { if ((this.bitField0_ & 0x4) != 0 && this.insert_ != null && this.insert_ != MysqlxCrud.Insert.getDefaultInstance()) { this.insert_ = MysqlxCrud.Insert.newBuilder(this.insert_).mergeFrom(value).buildPartial(); } else { this.insert_ = value; }  onChanged(); } else { this.insertBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x4; return this; } public Builder clearInsert() { if (this.insertBuilder_ == null) { this.insert_ = null; onChanged(); } else { this.insertBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFB; return this; } public MysqlxCrud.Insert.Builder getInsertBuilder() { this.bitField0_ |= 0x4; onChanged(); return (MysqlxCrud.Insert.Builder)getInsertFieldBuilder().getBuilder(); } public MysqlxCrud.InsertOrBuilder getInsertOrBuilder() { if (this.insertBuilder_ != null) return (MysqlxCrud.InsertOrBuilder)this.insertBuilder_.getMessageOrBuilder();  return (this.insert_ == null) ? MysqlxCrud.Insert.getDefaultInstance() : this.insert_; } private SingleFieldBuilderV3<MysqlxCrud.Insert, MysqlxCrud.Insert.Builder, MysqlxCrud.InsertOrBuilder> getInsertFieldBuilder() { if (this.insertBuilder_ == null) { this.insertBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getInsert(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.insert_ = null; }  return this.insertBuilder_; } public boolean hasUpdate() { return ((this.bitField0_ & 0x8) != 0); } public MysqlxCrud.Update getUpdate() { if (this.updateBuilder_ == null) return (this.update_ == null) ? MysqlxCrud.Update.getDefaultInstance() : this.update_;  return (MysqlxCrud.Update)this.updateBuilder_.getMessage(); } public Builder setUpdate(MysqlxCrud.Update value) { if (this.updateBuilder_ == null) { if (value == null) throw new NullPointerException();  this.update_ = value; onChanged(); } else { this.updateBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x8; return this; } public Builder setUpdate(MysqlxCrud.Update.Builder builderForValue) { if (this.updateBuilder_ == null) { this.update_ = builderForValue.build(); onChanged(); } else { this.updateBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x8; return this; } public Builder mergeUpdate(MysqlxCrud.Update value) { if (this.updateBuilder_ == null) { if ((this.bitField0_ & 0x8) != 0 && this.update_ != null && this.update_ != MysqlxCrud.Update.getDefaultInstance()) { this.update_ = MysqlxCrud.Update.newBuilder(this.update_).mergeFrom(value).buildPartial(); } else { this.update_ = value; }  onChanged(); } else { this.updateBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x8; return this; } public Builder clearUpdate() { if (this.updateBuilder_ == null) { this.update_ = null; onChanged(); } else { this.updateBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFF7; return this; } public MysqlxCrud.Update.Builder getUpdateBuilder() { this.bitField0_ |= 0x8; onChanged(); return (MysqlxCrud.Update.Builder)getUpdateFieldBuilder().getBuilder(); } public MysqlxCrud.UpdateOrBuilder getUpdateOrBuilder() { if (this.updateBuilder_ != null) return (MysqlxCrud.UpdateOrBuilder)this.updateBuilder_.getMessageOrBuilder();  return (this.update_ == null) ? MysqlxCrud.Update.getDefaultInstance() : this.update_; } private SingleFieldBuilderV3<MysqlxCrud.Update, MysqlxCrud.Update.Builder, MysqlxCrud.UpdateOrBuilder> getUpdateFieldBuilder() { if (this.updateBuilder_ == null) { this.updateBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getUpdate(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.update_ = null; }  return this.updateBuilder_; } public boolean hasDelete() { return ((this.bitField0_ & 0x10) != 0); } public MysqlxCrud.Delete getDelete() { if (this.deleteBuilder_ == null) return (this.delete_ == null) ? MysqlxCrud.Delete.getDefaultInstance() : this.delete_;  return (MysqlxCrud.Delete)this.deleteBuilder_.getMessage(); } public Builder setDelete(MysqlxCrud.Delete value) { if (this.deleteBuilder_ == null) { if (value == null) throw new NullPointerException();  this.delete_ = value; onChanged(); } else { this.deleteBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x10; return this; } public Builder setDelete(MysqlxCrud.Delete.Builder builderForValue) { if (this.deleteBuilder_ == null) { this.delete_ = builderForValue.build(); onChanged(); } else { this.deleteBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x10; return this; } public Builder mergeDelete(MysqlxCrud.Delete value) { if (this.deleteBuilder_ == null) { if ((this.bitField0_ & 0x10) != 0 && this.delete_ != null && this.delete_ != MysqlxCrud.Delete.getDefaultInstance()) { this.delete_ = MysqlxCrud.Delete.newBuilder(this.delete_).mergeFrom(value).buildPartial(); } else { this.delete_ = value; }  onChanged(); } else { this.deleteBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x10; return this; } public Builder clearDelete() { if (this.deleteBuilder_ == null) { this.delete_ = null; onChanged(); } else { this.deleteBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFEF; return this; } public MysqlxCrud.Delete.Builder getDeleteBuilder() { this.bitField0_ |= 0x10; onChanged(); return (MysqlxCrud.Delete.Builder)getDeleteFieldBuilder().getBuilder(); } public MysqlxCrud.DeleteOrBuilder getDeleteOrBuilder() { if (this.deleteBuilder_ != null) return (MysqlxCrud.DeleteOrBuilder)this.deleteBuilder_.getMessageOrBuilder();  return (this.delete_ == null) ? MysqlxCrud.Delete.getDefaultInstance() : this.delete_; } private SingleFieldBuilderV3<MysqlxCrud.Delete, MysqlxCrud.Delete.Builder, MysqlxCrud.DeleteOrBuilder> getDeleteFieldBuilder() { if (this.deleteBuilder_ == null) { this.deleteBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getDelete(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.delete_ = null; }  return this.deleteBuilder_; } public boolean hasStmtExecute() { return ((this.bitField0_ & 0x20) != 0); } public MysqlxSql.StmtExecute getStmtExecute() { if (this.stmtExecuteBuilder_ == null) return (this.stmtExecute_ == null) ? MysqlxSql.StmtExecute.getDefaultInstance() : this.stmtExecute_;  return (MysqlxSql.StmtExecute)this.stmtExecuteBuilder_.getMessage(); } public Builder setStmtExecute(MysqlxSql.StmtExecute value) { if (this.stmtExecuteBuilder_ == null) { if (value == null) throw new NullPointerException();  this.stmtExecute_ = value; onChanged(); } else { this.stmtExecuteBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x20; return this; } public Builder setStmtExecute(MysqlxSql.StmtExecute.Builder builderForValue) { if (this.stmtExecuteBuilder_ == null) { this.stmtExecute_ = builderForValue.build(); onChanged(); } else { this.stmtExecuteBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x20; return this; } public Builder mergeStmtExecute(MysqlxSql.StmtExecute value) { if (this.stmtExecuteBuilder_ == null) { if ((this.bitField0_ & 0x20) != 0 && this.stmtExecute_ != null && this.stmtExecute_ != MysqlxSql.StmtExecute.getDefaultInstance()) { this.stmtExecute_ = MysqlxSql.StmtExecute.newBuilder(this.stmtExecute_).mergeFrom(value).buildPartial(); } else { this.stmtExecute_ = value; }  onChanged(); } else { this.stmtExecuteBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x20; return this; } public Builder clearStmtExecute() { if (this.stmtExecuteBuilder_ == null) { this.stmtExecute_ = null; onChanged(); } else { this.stmtExecuteBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFDF; return this; } public MysqlxSql.StmtExecute.Builder getStmtExecuteBuilder() { this.bitField0_ |= 0x20; onChanged(); return (MysqlxSql.StmtExecute.Builder)getStmtExecuteFieldBuilder().getBuilder(); } public MysqlxSql.StmtExecuteOrBuilder getStmtExecuteOrBuilder() { if (this.stmtExecuteBuilder_ != null) return (MysqlxSql.StmtExecuteOrBuilder)this.stmtExecuteBuilder_.getMessageOrBuilder();  return (this.stmtExecute_ == null) ? MysqlxSql.StmtExecute.getDefaultInstance() : this.stmtExecute_; } private SingleFieldBuilderV3<MysqlxSql.StmtExecute, MysqlxSql.StmtExecute.Builder, MysqlxSql.StmtExecuteOrBuilder> getStmtExecuteFieldBuilder() { if (this.stmtExecuteBuilder_ == null) { this.stmtExecuteBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getStmtExecute(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.stmtExecute_ = null; }  return this.stmtExecuteBuilder_; } public final Builder setUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.setUnknownFields(unknownFields); } public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.mergeUnknownFields(unknownFields); } } private static final OneOfMessage DEFAULT_INSTANCE = new OneOfMessage(); public static OneOfMessage getDefaultInstance() { return DEFAULT_INSTANCE; } @Deprecated public static final Parser<OneOfMessage> PARSER = (Parser<OneOfMessage>)new AbstractParser<OneOfMessage>() { public MysqlxPrepare.Prepare.OneOfMessage parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return new MysqlxPrepare.Prepare.OneOfMessage(input, extensionRegistry); } }; public static Parser<OneOfMessage> parser() { return PARSER; } public Parser<OneOfMessage> getParserForType() { return PARSER; } public OneOfMessage getDefaultInstanceForType() { return DEFAULT_INSTANCE; } } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 2030 */       if (isInitialized == 1) return true; 
/* 2031 */       if (isInitialized == 0) return false;
/*      */       
/* 2033 */       if (!hasStmtId()) {
/* 2034 */         this.memoizedIsInitialized = 0;
/* 2035 */         return false;
/*      */       } 
/* 2037 */       if (!hasStmt()) {
/* 2038 */         this.memoizedIsInitialized = 0;
/* 2039 */         return false;
/*      */       } 
/* 2041 */       if (!getStmt().isInitialized()) {
/* 2042 */         this.memoizedIsInitialized = 0;
/* 2043 */         return false;
/*      */       } 
/* 2045 */       this.memoizedIsInitialized = 1;
/* 2046 */       return true; } public static final class Builder extends GeneratedMessageV3.Builder<OneOfMessage.Builder> implements OneOfMessageOrBuilder {
/*      */       private int bitField0_; private int type_; private MysqlxCrud.Find find_; private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> findBuilder_; private MysqlxCrud.Insert insert_; private SingleFieldBuilderV3<MysqlxCrud.Insert, MysqlxCrud.Insert.Builder, MysqlxCrud.InsertOrBuilder> insertBuilder_; private MysqlxCrud.Update update_; private SingleFieldBuilderV3<MysqlxCrud.Update, MysqlxCrud.Update.Builder, MysqlxCrud.UpdateOrBuilder> updateBuilder_; private MysqlxCrud.Delete delete_; private SingleFieldBuilderV3<MysqlxCrud.Delete, MysqlxCrud.Delete.Builder, MysqlxCrud.DeleteOrBuilder> deleteBuilder_; private MysqlxSql.StmtExecute stmtExecute_; private SingleFieldBuilderV3<MysqlxSql.StmtExecute, MysqlxSql.StmtExecute.Builder, MysqlxSql.StmtExecuteOrBuilder> stmtExecuteBuilder_; public static final Descriptors.Descriptor getDescriptor() { return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxPrepare.Prepare.OneOfMessage.class, Builder.class); } private Builder() { this.type_ = 0; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.type_ = 0; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxPrepare.Prepare.OneOfMessage.alwaysUseFieldBuilders) { getFindFieldBuilder(); getInsertFieldBuilder(); getUpdateFieldBuilder(); getDeleteFieldBuilder(); getStmtExecuteFieldBuilder(); }  } public Builder clear() { super.clear(); this.type_ = 0; this.bitField0_ &= 0xFFFFFFFE; if (this.findBuilder_ == null) { this.find_ = null; } else { this.findBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; if (this.insertBuilder_ == null) { this.insert_ = null; } else { this.insertBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFB; if (this.updateBuilder_ == null) { this.update_ = null; } else { this.updateBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFF7; if (this.deleteBuilder_ == null) { this.delete_ = null; } else { this.deleteBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFEF; if (this.stmtExecuteBuilder_ == null) { this.stmtExecute_ = null; } else { this.stmtExecuteBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFDF; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor; } public MysqlxPrepare.Prepare.OneOfMessage getDefaultInstanceForType() { return MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance(); } public MysqlxPrepare.Prepare.OneOfMessage build() { MysqlxPrepare.Prepare.OneOfMessage result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxPrepare.Prepare.OneOfMessage buildPartial() { MysqlxPrepare.Prepare.OneOfMessage result = new MysqlxPrepare.Prepare.OneOfMessage(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.type_ = this.type_; if ((from_bitField0_ & 0x2) != 0) { if (this.findBuilder_ == null) { result.find_ = this.find_; } else { result.find_ = (MysqlxCrud.Find)this.findBuilder_.build(); }  to_bitField0_ |= 0x2; }  if ((from_bitField0_ & 0x4) != 0) { if (this.insertBuilder_ == null) { result.insert_ = this.insert_; } else { result.insert_ = (MysqlxCrud.Insert)this.insertBuilder_.build(); }  to_bitField0_ |= 0x4; }  if ((from_bitField0_ & 0x8) != 0) { if (this.updateBuilder_ == null) { result.update_ = this.update_; } else { result.update_ = (MysqlxCrud.Update)this.updateBuilder_.build(); }  to_bitField0_ |= 0x8; }  if ((from_bitField0_ & 0x10) != 0) { if (this.deleteBuilder_ == null) { result.delete_ = this.delete_; } else { result.delete_ = (MysqlxCrud.Delete)this.deleteBuilder_.build(); }  to_bitField0_ |= 0x10; }  if ((from_bitField0_ & 0x20) != 0) { if (this.stmtExecuteBuilder_ == null) { result.stmtExecute_ = this.stmtExecute_; } else { result.stmtExecute_ = (MysqlxSql.StmtExecute)this.stmtExecuteBuilder_.build(); }  to_bitField0_ |= 0x20; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxPrepare.Prepare.OneOfMessage) return mergeFrom((MysqlxPrepare.Prepare.OneOfMessage)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxPrepare.Prepare.OneOfMessage other) { if (other == MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance()) return this;  if (other.hasType()) setType(other.getType());  if (other.hasFind()) mergeFind(other.getFind());  if (other.hasInsert()) mergeInsert(other.getInsert());  if (other.hasUpdate()) mergeUpdate(other.getUpdate());  if (other.hasDelete()) mergeDelete(other.getDelete());  if (other.hasStmtExecute()) mergeStmtExecute(other.getStmtExecute());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasType()) return false;  if (hasFind() && !getFind().isInitialized()) return false;  if (hasInsert() && !getInsert().isInitialized()) return false;  if (hasUpdate() && !getUpdate().isInitialized()) return false;  if (hasDelete() && !getDelete().isInitialized()) return false;  if (hasStmtExecute() && !getStmtExecute().isInitialized()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxPrepare.Prepare.OneOfMessage parsedMessage = null; try { parsedMessage = (MysqlxPrepare.Prepare.OneOfMessage)MysqlxPrepare.Prepare.OneOfMessage.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxPrepare.Prepare.OneOfMessage)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxPrepare.Prepare.OneOfMessage.Type getType() { MysqlxPrepare.Prepare.OneOfMessage.Type result = MysqlxPrepare.Prepare.OneOfMessage.Type.valueOf(this.type_); return (result == null) ? MysqlxPrepare.Prepare.OneOfMessage.Type.FIND : result; } public Builder setType(MysqlxPrepare.Prepare.OneOfMessage.Type value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.type_ = value.getNumber(); onChanged(); return this; } public Builder clearType() { this.bitField0_ &= 0xFFFFFFFE; this.type_ = 0; onChanged(); return this; } public boolean hasFind() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxCrud.Find getFind() { if (this.findBuilder_ == null) return (this.find_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.find_;  return (MysqlxCrud.Find)this.findBuilder_.getMessage(); } public Builder setFind(MysqlxCrud.Find value) { if (this.findBuilder_ == null) { if (value == null) throw new NullPointerException();  this.find_ = value; onChanged(); } else { this.findBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x2; return this; } public Builder setFind(MysqlxCrud.Find.Builder builderForValue) { if (this.findBuilder_ == null) { this.find_ = builderForValue.build(); onChanged(); } else { this.findBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x2; return this; } public Builder mergeFind(MysqlxCrud.Find value) { if (this.findBuilder_ == null) { if ((this.bitField0_ & 0x2) != 0 && this.find_ != null && this.find_ != MysqlxCrud.Find.getDefaultInstance()) { this.find_ = MysqlxCrud.Find.newBuilder(this.find_).mergeFrom(value).buildPartial(); } else { this.find_ = value; }  onChanged(); } else { this.findBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x2; return this; } public Builder clearFind() { if (this.findBuilder_ == null) { this.find_ = null; onChanged(); } else { this.findBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; return this; } public MysqlxCrud.Find.Builder getFindBuilder() { this.bitField0_ |= 0x2; onChanged(); return (MysqlxCrud.Find.Builder)getFindFieldBuilder().getBuilder(); } public MysqlxCrud.FindOrBuilder getFindOrBuilder() { if (this.findBuilder_ != null) return (MysqlxCrud.FindOrBuilder)this.findBuilder_.getMessageOrBuilder();  return (this.find_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.find_; } private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> getFindFieldBuilder() { if (this.findBuilder_ == null) { this.findBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getFind(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.find_ = null; }  return this.findBuilder_; } public boolean hasInsert() { return ((this.bitField0_ & 0x4) != 0); } public MysqlxCrud.Insert getInsert() { if (this.insertBuilder_ == null) return (this.insert_ == null) ? MysqlxCrud.Insert.getDefaultInstance() : this.insert_;  return (MysqlxCrud.Insert)this.insertBuilder_.getMessage(); } public Builder setInsert(MysqlxCrud.Insert value) { if (this.insertBuilder_ == null) { if (value == null) throw new NullPointerException();  this.insert_ = value; onChanged(); } else { this.insertBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x4; return this; } public Builder setInsert(MysqlxCrud.Insert.Builder builderForValue) { if (this.insertBuilder_ == null) { this.insert_ = builderForValue.build(); onChanged(); } else { this.insertBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x4; return this; } public Builder mergeInsert(MysqlxCrud.Insert value) { if (this.insertBuilder_ == null) { if ((this.bitField0_ & 0x4) != 0 && this.insert_ != null && this.insert_ != MysqlxCrud.Insert.getDefaultInstance()) { this.insert_ = MysqlxCrud.Insert.newBuilder(this.insert_).mergeFrom(value).buildPartial(); } else { this.insert_ = value; }  onChanged(); } else { this.insertBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x4; return this; } public Builder clearInsert() { if (this.insertBuilder_ == null) { this.insert_ = null; onChanged(); } else { this.insertBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFB; return this; } public MysqlxCrud.Insert.Builder getInsertBuilder() { this.bitField0_ |= 0x4; onChanged(); return (MysqlxCrud.Insert.Builder)getInsertFieldBuilder().getBuilder(); } public MysqlxCrud.InsertOrBuilder getInsertOrBuilder() { if (this.insertBuilder_ != null) return (MysqlxCrud.InsertOrBuilder)this.insertBuilder_.getMessageOrBuilder();  return (this.insert_ == null) ? MysqlxCrud.Insert.getDefaultInstance() : this.insert_; } private SingleFieldBuilderV3<MysqlxCrud.Insert, MysqlxCrud.Insert.Builder, MysqlxCrud.InsertOrBuilder> getInsertFieldBuilder() { if (this.insertBuilder_ == null) { this.insertBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getInsert(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.insert_ = null; }  return this.insertBuilder_; } public boolean hasUpdate() { return ((this.bitField0_ & 0x8) != 0); } public MysqlxCrud.Update getUpdate() { if (this.updateBuilder_ == null) return (this.update_ == null) ? MysqlxCrud.Update.getDefaultInstance() : this.update_;  return (MysqlxCrud.Update)this.updateBuilder_.getMessage(); } public Builder setUpdate(MysqlxCrud.Update value) { if (this.updateBuilder_ == null) { if (value == null) throw new NullPointerException();  this.update_ = value; onChanged(); } else { this.updateBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x8; return this; } public Builder setUpdate(MysqlxCrud.Update.Builder builderForValue) { if (this.updateBuilder_ == null) { this.update_ = builderForValue.build(); onChanged(); } else { this.updateBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x8; return this; } public Builder mergeUpdate(MysqlxCrud.Update value) { if (this.updateBuilder_ == null) { if ((this.bitField0_ & 0x8) != 0 && this.update_ != null && this.update_ != MysqlxCrud.Update.getDefaultInstance()) { this.update_ = MysqlxCrud.Update.newBuilder(this.update_).mergeFrom(value).buildPartial(); } else { this.update_ = value; }  onChanged(); } else { this.updateBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x8; return this; } public Builder clearUpdate() { if (this.updateBuilder_ == null) { this.update_ = null; onChanged(); } else { this.updateBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFF7; return this; } public MysqlxCrud.Update.Builder getUpdateBuilder() { this.bitField0_ |= 0x8; onChanged(); return (MysqlxCrud.Update.Builder)getUpdateFieldBuilder().getBuilder(); } public MysqlxCrud.UpdateOrBuilder getUpdateOrBuilder() { if (this.updateBuilder_ != null) return (MysqlxCrud.UpdateOrBuilder)this.updateBuilder_.getMessageOrBuilder();  return (this.update_ == null) ? MysqlxCrud.Update.getDefaultInstance() : this.update_; } private SingleFieldBuilderV3<MysqlxCrud.Update, MysqlxCrud.Update.Builder, MysqlxCrud.UpdateOrBuilder> getUpdateFieldBuilder() { if (this.updateBuilder_ == null) { this.updateBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getUpdate(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.update_ = null; }  return this.updateBuilder_; } public boolean hasDelete() { return ((this.bitField0_ & 0x10) != 0); } public MysqlxCrud.Delete getDelete() { if (this.deleteBuilder_ == null) return (this.delete_ == null) ? MysqlxCrud.Delete.getDefaultInstance() : this.delete_;  return (MysqlxCrud.Delete)this.deleteBuilder_.getMessage(); } public Builder setDelete(MysqlxCrud.Delete value) { if (this.deleteBuilder_ == null) { if (value == null) throw new NullPointerException();  this.delete_ = value; onChanged(); } else { this.deleteBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x10; return this; } public Builder setDelete(MysqlxCrud.Delete.Builder builderForValue) { if (this.deleteBuilder_ == null) { this.delete_ = builderForValue.build(); onChanged(); } else { this.deleteBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x10; return this; } public Builder mergeDelete(MysqlxCrud.Delete value) { if (this.deleteBuilder_ == null) { if ((this.bitField0_ & 0x10) != 0 && this.delete_ != null && this.delete_ != MysqlxCrud.Delete.getDefaultInstance()) { this.delete_ = MysqlxCrud.Delete.newBuilder(this.delete_).mergeFrom(value).buildPartial(); } else { this.delete_ = value; }  onChanged(); } else { this.deleteBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x10; return this; } public Builder clearDelete() { if (this.deleteBuilder_ == null) { this.delete_ = null; onChanged(); } else { this.deleteBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFEF; return this; } public MysqlxCrud.Delete.Builder getDeleteBuilder() { this.bitField0_ |= 0x10; onChanged(); return (MysqlxCrud.Delete.Builder)getDeleteFieldBuilder().getBuilder(); } public MysqlxCrud.DeleteOrBuilder getDeleteOrBuilder() { if (this.deleteBuilder_ != null) return (MysqlxCrud.DeleteOrBuilder)this.deleteBuilder_.getMessageOrBuilder();  return (this.delete_ == null) ? MysqlxCrud.Delete.getDefaultInstance() : this.delete_; } private SingleFieldBuilderV3<MysqlxCrud.Delete, MysqlxCrud.Delete.Builder, MysqlxCrud.DeleteOrBuilder> getDeleteFieldBuilder() { if (this.deleteBuilder_ == null) { this.deleteBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getDelete(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.delete_ = null; }  return this.deleteBuilder_; } public boolean hasStmtExecute() { return ((this.bitField0_ & 0x20) != 0); } public MysqlxSql.StmtExecute getStmtExecute() { if (this.stmtExecuteBuilder_ == null) return (this.stmtExecute_ == null) ? MysqlxSql.StmtExecute.getDefaultInstance() : this.stmtExecute_;  return (MysqlxSql.StmtExecute)this.stmtExecuteBuilder_.getMessage(); } public Builder setStmtExecute(MysqlxSql.StmtExecute value) { if (this.stmtExecuteBuilder_ == null) { if (value == null) throw new NullPointerException();  this.stmtExecute_ = value; onChanged(); } else { this.stmtExecuteBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x20; return this; } public Builder setStmtExecute(MysqlxSql.StmtExecute.Builder builderForValue) { if (this.stmtExecuteBuilder_ == null) { this.stmtExecute_ = builderForValue.build(); onChanged(); } else { this.stmtExecuteBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x20; return this; } public Builder mergeStmtExecute(MysqlxSql.StmtExecute value) { if (this.stmtExecuteBuilder_ == null) { if ((this.bitField0_ & 0x20) != 0 && this.stmtExecute_ != null && this.stmtExecute_ != MysqlxSql.StmtExecute.getDefaultInstance()) { this.stmtExecute_ = MysqlxSql.StmtExecute.newBuilder(this.stmtExecute_).mergeFrom(value).buildPartial(); } else { this.stmtExecute_ = value; }  onChanged(); } else { this.stmtExecuteBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x20; return this; } public Builder clearStmtExecute() { if (this.stmtExecuteBuilder_ == null) { this.stmtExecute_ = null; onChanged(); } else { this.stmtExecuteBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFDF; return this; } public MysqlxSql.StmtExecute.Builder getStmtExecuteBuilder() { this.bitField0_ |= 0x20; onChanged(); return (MysqlxSql.StmtExecute.Builder)getStmtExecuteFieldBuilder().getBuilder(); } public MysqlxSql.StmtExecuteOrBuilder getStmtExecuteOrBuilder() { if (this.stmtExecuteBuilder_ != null) return (MysqlxSql.StmtExecuteOrBuilder)this.stmtExecuteBuilder_.getMessageOrBuilder();  return (this.stmtExecute_ == null) ? MysqlxSql.StmtExecute.getDefaultInstance() : this.stmtExecute_; } private SingleFieldBuilderV3<MysqlxSql.StmtExecute, MysqlxSql.StmtExecute.Builder, MysqlxSql.StmtExecuteOrBuilder> getStmtExecuteFieldBuilder() { if (this.stmtExecuteBuilder_ == null) { this.stmtExecuteBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getStmtExecute(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.stmtExecute_ = null; }  return this.stmtExecuteBuilder_; } public final Builder setUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.setUnknownFields(unknownFields); } public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.mergeUnknownFields(unknownFields); } } public boolean hasStmtId() { return ((this.bitField0_ & 0x1) != 0); }
/*      */     public int getStmtId() { return this.stmtId_; }
/*      */     public boolean hasStmt() { return ((this.bitField0_ & 0x2) != 0); }
/*      */     public OneOfMessage getStmt() { return (this.stmt_ == null) ? OneOfMessage.getDefaultInstance() : this.stmt_; }
/*      */     public OneOfMessageOrBuilder getStmtOrBuilder() { return (this.stmt_ == null) ? OneOfMessage.getDefaultInstance() : this.stmt_; }
/* 2052 */     public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) {
/* 2053 */         output.writeUInt32(1, this.stmtId_);
/*      */       }
/* 2055 */       if ((this.bitField0_ & 0x2) != 0) {
/* 2056 */         output.writeMessage(2, (MessageLite)getStmt());
/*      */       }
/* 2058 */       this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 2063 */       int size = this.memoizedSize;
/* 2064 */       if (size != -1) return size;
/*      */       
/* 2066 */       size = 0;
/* 2067 */       if ((this.bitField0_ & 0x1) != 0) {
/* 2068 */         size += 
/* 2069 */           CodedOutputStream.computeUInt32Size(1, this.stmtId_);
/*      */       }
/* 2071 */       if ((this.bitField0_ & 0x2) != 0) {
/* 2072 */         size += 
/* 2073 */           CodedOutputStream.computeMessageSize(2, (MessageLite)getStmt());
/*      */       }
/* 2075 */       size += this.unknownFields.getSerializedSize();
/* 2076 */       this.memoizedSize = size;
/* 2077 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 2082 */       if (obj == this) {
/* 2083 */         return true;
/*      */       }
/* 2085 */       if (!(obj instanceof Prepare)) {
/* 2086 */         return super.equals(obj);
/*      */       }
/* 2088 */       Prepare other = (Prepare)obj;
/*      */       
/* 2090 */       if (hasStmtId() != other.hasStmtId()) return false; 
/* 2091 */       if (hasStmtId() && 
/* 2092 */         getStmtId() != other
/* 2093 */         .getStmtId()) return false;
/*      */       
/* 2095 */       if (hasStmt() != other.hasStmt()) return false; 
/* 2096 */       if (hasStmt() && 
/*      */         
/* 2098 */         !getStmt().equals(other.getStmt())) return false;
/*      */       
/* 2100 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 2101 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 2106 */       if (this.memoizedHashCode != 0) {
/* 2107 */         return this.memoizedHashCode;
/*      */       }
/* 2109 */       int hash = 41;
/* 2110 */       hash = 19 * hash + getDescriptor().hashCode();
/* 2111 */       if (hasStmtId()) {
/* 2112 */         hash = 37 * hash + 1;
/* 2113 */         hash = 53 * hash + getStmtId();
/*      */       } 
/* 2115 */       if (hasStmt()) {
/* 2116 */         hash = 37 * hash + 2;
/* 2117 */         hash = 53 * hash + getStmt().hashCode();
/*      */       } 
/* 2119 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 2120 */       this.memoizedHashCode = hash;
/* 2121 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Prepare parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 2127 */       return (Prepare)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Prepare parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2133 */       return (Prepare)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Prepare parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 2138 */       return (Prepare)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Prepare parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2144 */       return (Prepare)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Prepare parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 2148 */       return (Prepare)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Prepare parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2154 */       return (Prepare)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Prepare parseFrom(InputStream input) throws IOException {
/* 2158 */       return 
/* 2159 */         (Prepare)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Prepare parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2165 */       return 
/* 2166 */         (Prepare)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Prepare parseDelimitedFrom(InputStream input) throws IOException {
/* 2170 */       return 
/* 2171 */         (Prepare)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Prepare parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2177 */       return 
/* 2178 */         (Prepare)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Prepare parseFrom(CodedInputStream input) throws IOException {
/* 2183 */       return 
/* 2184 */         (Prepare)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Prepare parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2190 */       return 
/* 2191 */         (Prepare)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 2195 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 2197 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Prepare prototype) {
/* 2200 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 2204 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 2205 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 2211 */       Builder builder = new Builder(parent);
/* 2212 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxPrepare.PrepareOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */ 
/*      */       
/*      */       private int stmtId_;
/*      */ 
/*      */       
/*      */       private MysqlxPrepare.Prepare.OneOfMessage stmt_;
/*      */ 
/*      */       
/*      */       private SingleFieldBuilderV3<MysqlxPrepare.Prepare.OneOfMessage, MysqlxPrepare.Prepare.OneOfMessage.Builder, MysqlxPrepare.Prepare.OneOfMessageOrBuilder> stmtBuilder_;
/*      */ 
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 2237 */         return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 2243 */         return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_fieldAccessorTable
/* 2244 */           .ensureFieldAccessorsInitialized(MysqlxPrepare.Prepare.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 2250 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 2255 */         super(parent);
/* 2256 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 2260 */         if (MysqlxPrepare.Prepare.alwaysUseFieldBuilders) {
/* 2261 */           getStmtFieldBuilder();
/*      */         }
/*      */       }
/*      */       
/*      */       public Builder clear() {
/* 2266 */         super.clear();
/* 2267 */         this.stmtId_ = 0;
/* 2268 */         this.bitField0_ &= 0xFFFFFFFE;
/* 2269 */         if (this.stmtBuilder_ == null) {
/* 2270 */           this.stmt_ = null;
/*      */         } else {
/* 2272 */           this.stmtBuilder_.clear();
/*      */         } 
/* 2274 */         this.bitField0_ &= 0xFFFFFFFD;
/* 2275 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 2281 */         return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxPrepare.Prepare getDefaultInstanceForType() {
/* 2286 */         return MysqlxPrepare.Prepare.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxPrepare.Prepare build() {
/* 2291 */         MysqlxPrepare.Prepare result = buildPartial();
/* 2292 */         if (!result.isInitialized()) {
/* 2293 */           throw newUninitializedMessageException(result);
/*      */         }
/* 2295 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxPrepare.Prepare buildPartial() {
/* 2300 */         MysqlxPrepare.Prepare result = new MysqlxPrepare.Prepare(this);
/* 2301 */         int from_bitField0_ = this.bitField0_;
/* 2302 */         int to_bitField0_ = 0;
/* 2303 */         if ((from_bitField0_ & 0x1) != 0) {
/* 2304 */           result.stmtId_ = this.stmtId_;
/* 2305 */           to_bitField0_ |= 0x1;
/*      */         } 
/* 2307 */         if ((from_bitField0_ & 0x2) != 0) {
/* 2308 */           if (this.stmtBuilder_ == null) {
/* 2309 */             result.stmt_ = this.stmt_;
/*      */           } else {
/* 2311 */             result.stmt_ = (MysqlxPrepare.Prepare.OneOfMessage)this.stmtBuilder_.build();
/*      */           } 
/* 2313 */           to_bitField0_ |= 0x2;
/*      */         } 
/* 2315 */         result.bitField0_ = to_bitField0_;
/* 2316 */         onBuilt();
/* 2317 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 2322 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 2328 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 2333 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 2338 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 2344 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 2350 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 2354 */         if (other instanceof MysqlxPrepare.Prepare) {
/* 2355 */           return mergeFrom((MysqlxPrepare.Prepare)other);
/*      */         }
/* 2357 */         super.mergeFrom(other);
/* 2358 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxPrepare.Prepare other) {
/* 2363 */         if (other == MysqlxPrepare.Prepare.getDefaultInstance()) return this; 
/* 2364 */         if (other.hasStmtId()) {
/* 2365 */           setStmtId(other.getStmtId());
/*      */         }
/* 2367 */         if (other.hasStmt()) {
/* 2368 */           mergeStmt(other.getStmt());
/*      */         }
/* 2370 */         mergeUnknownFields(other.unknownFields);
/* 2371 */         onChanged();
/* 2372 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 2377 */         if (!hasStmtId()) {
/* 2378 */           return false;
/*      */         }
/* 2380 */         if (!hasStmt()) {
/* 2381 */           return false;
/*      */         }
/* 2383 */         if (!getStmt().isInitialized()) {
/* 2384 */           return false;
/*      */         }
/* 2386 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2394 */         MysqlxPrepare.Prepare parsedMessage = null;
/*      */         try {
/* 2396 */           parsedMessage = (MysqlxPrepare.Prepare)MysqlxPrepare.Prepare.PARSER.parsePartialFrom(input, extensionRegistry);
/* 2397 */         } catch (InvalidProtocolBufferException e) {
/* 2398 */           parsedMessage = (MysqlxPrepare.Prepare)e.getUnfinishedMessage();
/* 2399 */           throw e.unwrapIOException();
/*      */         } finally {
/* 2401 */           if (parsedMessage != null) {
/* 2402 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 2405 */         return this;
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
/*      */       public boolean hasStmtId() {
/* 2420 */         return ((this.bitField0_ & 0x1) != 0);
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
/*      */       public int getStmtId() {
/* 2432 */         return this.stmtId_;
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
/*      */       public Builder setStmtId(int value) {
/* 2445 */         this.bitField0_ |= 0x1;
/* 2446 */         this.stmtId_ = value;
/* 2447 */         onChanged();
/* 2448 */         return this;
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
/*      */       public Builder clearStmtId() {
/* 2460 */         this.bitField0_ &= 0xFFFFFFFE;
/* 2461 */         this.stmtId_ = 0;
/* 2462 */         onChanged();
/* 2463 */         return this;
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
/*      */       public boolean hasStmt() {
/* 2479 */         return ((this.bitField0_ & 0x2) != 0);
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
/*      */       public MysqlxPrepare.Prepare.OneOfMessage getStmt() {
/* 2491 */         if (this.stmtBuilder_ == null) {
/* 2492 */           return (this.stmt_ == null) ? MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance() : this.stmt_;
/*      */         }
/* 2494 */         return (MysqlxPrepare.Prepare.OneOfMessage)this.stmtBuilder_.getMessage();
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
/*      */       public Builder setStmt(MysqlxPrepare.Prepare.OneOfMessage value) {
/* 2506 */         if (this.stmtBuilder_ == null) {
/* 2507 */           if (value == null) {
/* 2508 */             throw new NullPointerException();
/*      */           }
/* 2510 */           this.stmt_ = value;
/* 2511 */           onChanged();
/*      */         } else {
/* 2513 */           this.stmtBuilder_.setMessage((AbstractMessage)value);
/*      */         } 
/* 2515 */         this.bitField0_ |= 0x2;
/* 2516 */         return this;
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
/*      */       public Builder setStmt(MysqlxPrepare.Prepare.OneOfMessage.Builder builderForValue) {
/* 2528 */         if (this.stmtBuilder_ == null) {
/* 2529 */           this.stmt_ = builderForValue.build();
/* 2530 */           onChanged();
/*      */         } else {
/* 2532 */           this.stmtBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 2534 */         this.bitField0_ |= 0x2;
/* 2535 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeStmt(MysqlxPrepare.Prepare.OneOfMessage value) {
/* 2546 */         if (this.stmtBuilder_ == null) {
/* 2547 */           if ((this.bitField0_ & 0x2) != 0 && this.stmt_ != null && this.stmt_ != 
/*      */             
/* 2549 */             MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance()) {
/* 2550 */             this
/* 2551 */               .stmt_ = MysqlxPrepare.Prepare.OneOfMessage.newBuilder(this.stmt_).mergeFrom(value).buildPartial();
/*      */           } else {
/* 2553 */             this.stmt_ = value;
/*      */           } 
/* 2555 */           onChanged();
/*      */         } else {
/* 2557 */           this.stmtBuilder_.mergeFrom((AbstractMessage)value);
/*      */         } 
/* 2559 */         this.bitField0_ |= 0x2;
/* 2560 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearStmt() {
/* 2571 */         if (this.stmtBuilder_ == null) {
/* 2572 */           this.stmt_ = null;
/* 2573 */           onChanged();
/*      */         } else {
/* 2575 */           this.stmtBuilder_.clear();
/*      */         } 
/* 2577 */         this.bitField0_ &= 0xFFFFFFFD;
/* 2578 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxPrepare.Prepare.OneOfMessage.Builder getStmtBuilder() {
/* 2589 */         this.bitField0_ |= 0x2;
/* 2590 */         onChanged();
/* 2591 */         return (MysqlxPrepare.Prepare.OneOfMessage.Builder)getStmtFieldBuilder().getBuilder();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxPrepare.Prepare.OneOfMessageOrBuilder getStmtOrBuilder() {
/* 2602 */         if (this.stmtBuilder_ != null) {
/* 2603 */           return (MysqlxPrepare.Prepare.OneOfMessageOrBuilder)this.stmtBuilder_.getMessageOrBuilder();
/*      */         }
/* 2605 */         return (this.stmt_ == null) ? 
/* 2606 */           MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance() : this.stmt_;
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
/*      */       private SingleFieldBuilderV3<MysqlxPrepare.Prepare.OneOfMessage, MysqlxPrepare.Prepare.OneOfMessage.Builder, MysqlxPrepare.Prepare.OneOfMessageOrBuilder> getStmtFieldBuilder() {
/* 2620 */         if (this.stmtBuilder_ == null) {
/* 2621 */           this
/*      */ 
/*      */ 
/*      */             
/* 2625 */             .stmtBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getStmt(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 2626 */           this.stmt_ = null;
/*      */         } 
/* 2628 */         return this.stmtBuilder_;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 2633 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 2639 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2649 */     private static final Prepare DEFAULT_INSTANCE = new Prepare();
/*      */ 
/*      */     
/*      */     public static Prepare getDefaultInstance() {
/* 2653 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 2657 */     public static final Parser<Prepare> PARSER = (Parser<Prepare>)new AbstractParser<Prepare>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxPrepare.Prepare parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 2663 */           return new MysqlxPrepare.Prepare(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Prepare> parser() {
/* 2668 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Prepare> getParserForType() {
/* 2673 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Prepare getDefaultInstanceForType() {
/* 2678 */       return DEFAULT_INSTANCE;
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
/*      */       MysqlxPrepare.Prepare.OneOfMessage.Type getType();
/*      */ 
/*      */       
/*      */       boolean hasFind();
/*      */ 
/*      */       
/*      */       MysqlxCrud.Find getFind();
/*      */ 
/*      */       
/*      */       MysqlxCrud.FindOrBuilder getFindOrBuilder();
/*      */ 
/*      */       
/*      */       boolean hasInsert();
/*      */ 
/*      */       
/*      */       MysqlxCrud.Insert getInsert();
/*      */ 
/*      */       
/*      */       MysqlxCrud.InsertOrBuilder getInsertOrBuilder();
/*      */ 
/*      */       
/*      */       boolean hasUpdate();
/*      */ 
/*      */       
/*      */       MysqlxCrud.Update getUpdate();
/*      */ 
/*      */       
/*      */       MysqlxCrud.UpdateOrBuilder getUpdateOrBuilder();
/*      */ 
/*      */       
/*      */       boolean hasDelete();
/*      */ 
/*      */       
/*      */       MysqlxCrud.Delete getDelete();
/*      */ 
/*      */       
/*      */       MysqlxCrud.DeleteOrBuilder getDeleteOrBuilder();
/*      */ 
/*      */       
/*      */       boolean hasStmtExecute();
/*      */ 
/*      */       
/*      */       MysqlxSql.StmtExecute getStmtExecute();
/*      */ 
/*      */       
/*      */       MysqlxSql.StmtExecuteOrBuilder getStmtExecuteOrBuilder();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static interface ExecuteOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasStmtId();
/*      */ 
/*      */     
/*      */     int getStmtId();
/*      */ 
/*      */     
/*      */     List<MysqlxDatatypes.Any> getArgsList();
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.Any getArgs(int param1Int);
/*      */ 
/*      */     
/*      */     int getArgsCount();
/*      */ 
/*      */     
/*      */     List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList();
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int param1Int);
/*      */ 
/*      */     
/*      */     boolean hasCompactMetadata();
/*      */ 
/*      */     
/*      */     boolean getCompactMetadata();
/*      */   }
/*      */ 
/*      */   
/*      */   public static final class Execute
/*      */     extends GeneratedMessageV3
/*      */     implements ExecuteOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     private int bitField0_;
/*      */     
/*      */     public static final int STMT_ID_FIELD_NUMBER = 1;
/*      */     
/*      */     private int stmtId_;
/*      */     
/*      */     public static final int ARGS_FIELD_NUMBER = 2;
/*      */     
/*      */     private List<MysqlxDatatypes.Any> args_;
/*      */     
/*      */     public static final int COMPACT_METADATA_FIELD_NUMBER = 3;
/*      */     
/*      */     private boolean compactMetadata_;
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */     
/*      */     private Execute(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 2796 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2995 */       this.memoizedIsInitialized = -1; } private Execute() { this.memoizedIsInitialized = -1; this.args_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Execute(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Execute(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.bitField0_ |= 0x1; this.stmtId_ = input.readUInt32(); continue;case 18: if ((mutable_bitField0_ & 0x2) == 0) { this.args_ = new ArrayList<>(); mutable_bitField0_ |= 0x2; }  this.args_.add(input.readMessage(MysqlxDatatypes.Any.PARSER, extensionRegistry)); continue;case 24: this.bitField0_ |= 0x2; this.compactMetadata_ = input.readBool(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x2) != 0) this.args_ = Collections.unmodifiableList(this.args_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxPrepare.internal_static_Mysqlx_Prepare_Execute_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxPrepare.internal_static_Mysqlx_Prepare_Execute_fieldAccessorTable.ensureFieldAccessorsInitialized(Execute.class, Builder.class); } public boolean hasStmtId() { return ((this.bitField0_ & 0x1) != 0); } public int getStmtId() { return this.stmtId_; } public List<MysqlxDatatypes.Any> getArgsList() { return this.args_; } public List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList() { return (List)this.args_; } public int getArgsCount() { return this.args_.size(); } public MysqlxDatatypes.Any getArgs(int index) { return this.args_.get(index); } public MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int index) { return this.args_.get(index); }
/*      */     public boolean hasCompactMetadata() { return ((this.bitField0_ & 0x2) != 0); }
/*      */     public boolean getCompactMetadata() { return this.compactMetadata_; }
/* 2998 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 2999 */       if (isInitialized == 1) return true; 
/* 3000 */       if (isInitialized == 0) return false;
/*      */       
/* 3002 */       if (!hasStmtId()) {
/* 3003 */         this.memoizedIsInitialized = 0;
/* 3004 */         return false;
/*      */       } 
/* 3006 */       for (int i = 0; i < getArgsCount(); i++) {
/* 3007 */         if (!getArgs(i).isInitialized()) {
/* 3008 */           this.memoizedIsInitialized = 0;
/* 3009 */           return false;
/*      */         } 
/*      */       } 
/* 3012 */       this.memoizedIsInitialized = 1;
/* 3013 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 3019 */       if ((this.bitField0_ & 0x1) != 0) {
/* 3020 */         output.writeUInt32(1, this.stmtId_);
/*      */       }
/* 3022 */       for (int i = 0; i < this.args_.size(); i++) {
/* 3023 */         output.writeMessage(2, (MessageLite)this.args_.get(i));
/*      */       }
/* 3025 */       if ((this.bitField0_ & 0x2) != 0) {
/* 3026 */         output.writeBool(3, this.compactMetadata_);
/*      */       }
/* 3028 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 3033 */       int size = this.memoizedSize;
/* 3034 */       if (size != -1) return size;
/*      */       
/* 3036 */       size = 0;
/* 3037 */       if ((this.bitField0_ & 0x1) != 0) {
/* 3038 */         size += 
/* 3039 */           CodedOutputStream.computeUInt32Size(1, this.stmtId_);
/*      */       }
/* 3041 */       for (int i = 0; i < this.args_.size(); i++) {
/* 3042 */         size += 
/* 3043 */           CodedOutputStream.computeMessageSize(2, (MessageLite)this.args_.get(i));
/*      */       }
/* 3045 */       if ((this.bitField0_ & 0x2) != 0) {
/* 3046 */         size += 
/* 3047 */           CodedOutputStream.computeBoolSize(3, this.compactMetadata_);
/*      */       }
/* 3049 */       size += this.unknownFields.getSerializedSize();
/* 3050 */       this.memoizedSize = size;
/* 3051 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 3056 */       if (obj == this) {
/* 3057 */         return true;
/*      */       }
/* 3059 */       if (!(obj instanceof Execute)) {
/* 3060 */         return super.equals(obj);
/*      */       }
/* 3062 */       Execute other = (Execute)obj;
/*      */       
/* 3064 */       if (hasStmtId() != other.hasStmtId()) return false; 
/* 3065 */       if (hasStmtId() && 
/* 3066 */         getStmtId() != other
/* 3067 */         .getStmtId()) return false;
/*      */ 
/*      */       
/* 3070 */       if (!getArgsList().equals(other.getArgsList())) return false; 
/* 3071 */       if (hasCompactMetadata() != other.hasCompactMetadata()) return false; 
/* 3072 */       if (hasCompactMetadata() && 
/* 3073 */         getCompactMetadata() != other
/* 3074 */         .getCompactMetadata()) return false;
/*      */       
/* 3076 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 3077 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 3082 */       if (this.memoizedHashCode != 0) {
/* 3083 */         return this.memoizedHashCode;
/*      */       }
/* 3085 */       int hash = 41;
/* 3086 */       hash = 19 * hash + getDescriptor().hashCode();
/* 3087 */       if (hasStmtId()) {
/* 3088 */         hash = 37 * hash + 1;
/* 3089 */         hash = 53 * hash + getStmtId();
/*      */       } 
/* 3091 */       if (getArgsCount() > 0) {
/* 3092 */         hash = 37 * hash + 2;
/* 3093 */         hash = 53 * hash + getArgsList().hashCode();
/*      */       } 
/* 3095 */       if (hasCompactMetadata()) {
/* 3096 */         hash = 37 * hash + 3;
/* 3097 */         hash = 53 * hash + Internal.hashBoolean(
/* 3098 */             getCompactMetadata());
/*      */       } 
/* 3100 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 3101 */       this.memoizedHashCode = hash;
/* 3102 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Execute parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 3108 */       return (Execute)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Execute parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 3114 */       return (Execute)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Execute parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 3119 */       return (Execute)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Execute parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 3125 */       return (Execute)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Execute parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 3129 */       return (Execute)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Execute parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 3135 */       return (Execute)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Execute parseFrom(InputStream input) throws IOException {
/* 3139 */       return 
/* 3140 */         (Execute)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Execute parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3146 */       return 
/* 3147 */         (Execute)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Execute parseDelimitedFrom(InputStream input) throws IOException {
/* 3151 */       return 
/* 3152 */         (Execute)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Execute parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3158 */       return 
/* 3159 */         (Execute)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Execute parseFrom(CodedInputStream input) throws IOException {
/* 3164 */       return 
/* 3165 */         (Execute)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Execute parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3171 */       return 
/* 3172 */         (Execute)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 3176 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 3178 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Execute prototype) {
/* 3181 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 3185 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 3186 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 3192 */       Builder builder = new Builder(parent);
/* 3193 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxPrepare.ExecuteOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */       
/*      */       private int stmtId_;
/*      */ 
/*      */       
/*      */       private List<MysqlxDatatypes.Any> args_;
/*      */ 
/*      */       
/*      */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> argsBuilder_;
/*      */ 
/*      */       
/*      */       private boolean compactMetadata_;
/*      */ 
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 3219 */         return MysqlxPrepare.internal_static_Mysqlx_Prepare_Execute_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 3225 */         return MysqlxPrepare.internal_static_Mysqlx_Prepare_Execute_fieldAccessorTable
/* 3226 */           .ensureFieldAccessorsInitialized(MysqlxPrepare.Execute.class, Builder.class);
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
/*      */ 
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
/* 3476 */         this
/* 3477 */           .args_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.args_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxPrepare.Execute.alwaysUseFieldBuilders) getArgsFieldBuilder();  } public Builder clear() { super.clear(); this.stmtId_ = 0; this.bitField0_ &= 0xFFFFFFFE; if (this.argsBuilder_ == null) { this.args_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFD; } else { this.argsBuilder_.clear(); }  this.compactMetadata_ = false; this.bitField0_ &= 0xFFFFFFFB; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxPrepare.internal_static_Mysqlx_Prepare_Execute_descriptor; } public MysqlxPrepare.Execute getDefaultInstanceForType() { return MysqlxPrepare.Execute.getDefaultInstance(); } public MysqlxPrepare.Execute build() { MysqlxPrepare.Execute result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxPrepare.Execute buildPartial() { MysqlxPrepare.Execute result = new MysqlxPrepare.Execute(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { result.stmtId_ = this.stmtId_; to_bitField0_ |= 0x1; }  if (this.argsBuilder_ == null) { if ((this.bitField0_ & 0x2) != 0) { this.args_ = Collections.unmodifiableList(this.args_); this.bitField0_ &= 0xFFFFFFFD; }  result.args_ = this.args_; } else { result.args_ = this.argsBuilder_.build(); }  if ((from_bitField0_ & 0x4) != 0) { result.compactMetadata_ = this.compactMetadata_; to_bitField0_ |= 0x2; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxPrepare.Execute) return mergeFrom((MysqlxPrepare.Execute)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxPrepare.Execute other) { if (other == MysqlxPrepare.Execute.getDefaultInstance()) return this;  if (other.hasStmtId()) setStmtId(other.getStmtId());  if (this.argsBuilder_ == null) { if (!other.args_.isEmpty()) { if (this.args_.isEmpty()) { this.args_ = other.args_; this.bitField0_ &= 0xFFFFFFFD; } else { ensureArgsIsMutable(); this.args_.addAll(other.args_); }  onChanged(); }  } else if (!other.args_.isEmpty()) { if (this.argsBuilder_.isEmpty()) { this.argsBuilder_.dispose(); this.argsBuilder_ = null; this.args_ = other.args_; this.bitField0_ &= 0xFFFFFFFD; this.argsBuilder_ = MysqlxPrepare.Execute.alwaysUseFieldBuilders ? getArgsFieldBuilder() : null; } else { this.argsBuilder_.addAllMessages(other.args_); }  }  if (other.hasCompactMetadata()) setCompactMetadata(other.getCompactMetadata());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasStmtId()) return false;  for (int i = 0; i < getArgsCount(); i++) { if (!getArgs(i).isInitialized()) return false;  }  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxPrepare.Execute parsedMessage = null; try { parsedMessage = (MysqlxPrepare.Execute)MysqlxPrepare.Execute.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxPrepare.Execute)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasStmtId() { return ((this.bitField0_ & 0x1) != 0); } public int getStmtId() { return this.stmtId_; } public Builder setStmtId(int value) { this.bitField0_ |= 0x1; this.stmtId_ = value; onChanged(); return this; }
/*      */       public Builder clearStmtId() { this.bitField0_ &= 0xFFFFFFFE; this.stmtId_ = 0; onChanged(); return this; }
/* 3479 */       private void ensureArgsIsMutable() { if ((this.bitField0_ & 0x2) == 0) {
/* 3480 */           this.args_ = new ArrayList<>(this.args_);
/* 3481 */           this.bitField0_ |= 0x2;
/*      */         }  }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<MysqlxDatatypes.Any> getArgsList() {
/* 3496 */         if (this.argsBuilder_ == null) {
/* 3497 */           return Collections.unmodifiableList(this.args_);
/*      */         }
/* 3499 */         return this.argsBuilder_.getMessageList();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int getArgsCount() {
/* 3510 */         if (this.argsBuilder_ == null) {
/* 3511 */           return this.args_.size();
/*      */         }
/* 3513 */         return this.argsBuilder_.getCount();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Any getArgs(int index) {
/* 3524 */         if (this.argsBuilder_ == null) {
/* 3525 */           return this.args_.get(index);
/*      */         }
/* 3527 */         return (MysqlxDatatypes.Any)this.argsBuilder_.getMessage(index);
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
/*      */       public Builder setArgs(int index, MysqlxDatatypes.Any value) {
/* 3539 */         if (this.argsBuilder_ == null) {
/* 3540 */           if (value == null) {
/* 3541 */             throw new NullPointerException();
/*      */           }
/* 3543 */           ensureArgsIsMutable();
/* 3544 */           this.args_.set(index, value);
/* 3545 */           onChanged();
/*      */         } else {
/* 3547 */           this.argsBuilder_.setMessage(index, (AbstractMessage)value);
/*      */         } 
/* 3549 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setArgs(int index, MysqlxDatatypes.Any.Builder builderForValue) {
/* 3560 */         if (this.argsBuilder_ == null) {
/* 3561 */           ensureArgsIsMutable();
/* 3562 */           this.args_.set(index, builderForValue.build());
/* 3563 */           onChanged();
/*      */         } else {
/* 3565 */           this.argsBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 3567 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addArgs(MysqlxDatatypes.Any value) {
/* 3577 */         if (this.argsBuilder_ == null) {
/* 3578 */           if (value == null) {
/* 3579 */             throw new NullPointerException();
/*      */           }
/* 3581 */           ensureArgsIsMutable();
/* 3582 */           this.args_.add(value);
/* 3583 */           onChanged();
/*      */         } else {
/* 3585 */           this.argsBuilder_.addMessage((AbstractMessage)value);
/*      */         } 
/* 3587 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addArgs(int index, MysqlxDatatypes.Any value) {
/* 3598 */         if (this.argsBuilder_ == null) {
/* 3599 */           if (value == null) {
/* 3600 */             throw new NullPointerException();
/*      */           }
/* 3602 */           ensureArgsIsMutable();
/* 3603 */           this.args_.add(index, value);
/* 3604 */           onChanged();
/*      */         } else {
/* 3606 */           this.argsBuilder_.addMessage(index, (AbstractMessage)value);
/*      */         } 
/* 3608 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addArgs(MysqlxDatatypes.Any.Builder builderForValue) {
/* 3619 */         if (this.argsBuilder_ == null) {
/* 3620 */           ensureArgsIsMutable();
/* 3621 */           this.args_.add(builderForValue.build());
/* 3622 */           onChanged();
/*      */         } else {
/* 3624 */           this.argsBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 3626 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addArgs(int index, MysqlxDatatypes.Any.Builder builderForValue) {
/* 3637 */         if (this.argsBuilder_ == null) {
/* 3638 */           ensureArgsIsMutable();
/* 3639 */           this.args_.add(index, builderForValue.build());
/* 3640 */           onChanged();
/*      */         } else {
/* 3642 */           this.argsBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 3644 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Any> values) {
/* 3655 */         if (this.argsBuilder_ == null) {
/* 3656 */           ensureArgsIsMutable();
/* 3657 */           AbstractMessageLite.Builder.addAll(values, this.args_);
/*      */           
/* 3659 */           onChanged();
/*      */         } else {
/* 3661 */           this.argsBuilder_.addAllMessages(values);
/*      */         } 
/* 3663 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearArgs() {
/* 3673 */         if (this.argsBuilder_ == null) {
/* 3674 */           this.args_ = Collections.emptyList();
/* 3675 */           this.bitField0_ &= 0xFFFFFFFD;
/* 3676 */           onChanged();
/*      */         } else {
/* 3678 */           this.argsBuilder_.clear();
/*      */         } 
/* 3680 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder removeArgs(int index) {
/* 3690 */         if (this.argsBuilder_ == null) {
/* 3691 */           ensureArgsIsMutable();
/* 3692 */           this.args_.remove(index);
/* 3693 */           onChanged();
/*      */         } else {
/* 3695 */           this.argsBuilder_.remove(index);
/*      */         } 
/* 3697 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Any.Builder getArgsBuilder(int index) {
/* 3708 */         return (MysqlxDatatypes.Any.Builder)getArgsFieldBuilder().getBuilder(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int index) {
/* 3719 */         if (this.argsBuilder_ == null)
/* 3720 */           return this.args_.get(index); 
/* 3721 */         return (MysqlxDatatypes.AnyOrBuilder)this.argsBuilder_.getMessageOrBuilder(index);
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
/*      */       public List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList() {
/* 3733 */         if (this.argsBuilder_ != null) {
/* 3734 */           return this.argsBuilder_.getMessageOrBuilderList();
/*      */         }
/* 3736 */         return Collections.unmodifiableList((List)this.args_);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Any.Builder addArgsBuilder() {
/* 3747 */         return (MysqlxDatatypes.Any.Builder)getArgsFieldBuilder().addBuilder(
/* 3748 */             (AbstractMessage)MysqlxDatatypes.Any.getDefaultInstance());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Any.Builder addArgsBuilder(int index) {
/* 3759 */         return (MysqlxDatatypes.Any.Builder)getArgsFieldBuilder().addBuilder(index, 
/* 3760 */             (AbstractMessage)MysqlxDatatypes.Any.getDefaultInstance());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<MysqlxDatatypes.Any.Builder> getArgsBuilderList() {
/* 3771 */         return getArgsFieldBuilder().getBuilderList();
/*      */       }
/*      */ 
/*      */       
/*      */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> getArgsFieldBuilder() {
/* 3776 */         if (this.argsBuilder_ == null) {
/* 3777 */           this
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 3782 */             .argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, ((this.bitField0_ & 0x2) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 3783 */           this.args_ = null;
/*      */         } 
/* 3785 */         return this.argsBuilder_;
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
/*      */       public boolean hasCompactMetadata() {
/* 3799 */         return ((this.bitField0_ & 0x4) != 0);
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
/*      */       public boolean getCompactMetadata() {
/* 3811 */         return this.compactMetadata_;
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
/*      */       public Builder setCompactMetadata(boolean value) {
/* 3824 */         this.bitField0_ |= 0x4;
/* 3825 */         this.compactMetadata_ = value;
/* 3826 */         onChanged();
/* 3827 */         return this;
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
/*      */       public Builder clearCompactMetadata() {
/* 3839 */         this.bitField0_ &= 0xFFFFFFFB;
/* 3840 */         this.compactMetadata_ = false;
/* 3841 */         onChanged();
/* 3842 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 3847 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 3853 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3863 */     private static final Execute DEFAULT_INSTANCE = new Execute();
/*      */ 
/*      */     
/*      */     public static Execute getDefaultInstance() {
/* 3867 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 3871 */     public static final Parser<Execute> PARSER = (Parser<Execute>)new AbstractParser<Execute>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxPrepare.Execute parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 3877 */           return new MysqlxPrepare.Execute(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Execute> parser() {
/* 3882 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Execute> getParserForType() {
/* 3887 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Execute getDefaultInstanceForType() {
/* 3892 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface DeallocateOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasStmtId();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getStmtId();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Deallocate
/*      */     extends GeneratedMessageV3
/*      */     implements DeallocateOrBuilder
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
/*      */     public static final int STMT_ID_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */     
/*      */     private int stmtId_;
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Deallocate(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 3944 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 4043 */       this.memoizedIsInitialized = -1; } private Deallocate() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Deallocate(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Deallocate(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.bitField0_ |= 0x1; this.stmtId_ = input.readUInt32(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxPrepare.internal_static_Mysqlx_Prepare_Deallocate_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxPrepare.internal_static_Mysqlx_Prepare_Deallocate_fieldAccessorTable.ensureFieldAccessorsInitialized(Deallocate.class, Builder.class); }
/*      */     public boolean hasStmtId() { return ((this.bitField0_ & 0x1) != 0); }
/*      */     public int getStmtId() { return this.stmtId_; }
/* 4046 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 4047 */       if (isInitialized == 1) return true; 
/* 4048 */       if (isInitialized == 0) return false;
/*      */       
/* 4050 */       if (!hasStmtId()) {
/* 4051 */         this.memoizedIsInitialized = 0;
/* 4052 */         return false;
/*      */       } 
/* 4054 */       this.memoizedIsInitialized = 1;
/* 4055 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 4061 */       if ((this.bitField0_ & 0x1) != 0) {
/* 4062 */         output.writeUInt32(1, this.stmtId_);
/*      */       }
/* 4064 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 4069 */       int size = this.memoizedSize;
/* 4070 */       if (size != -1) return size;
/*      */       
/* 4072 */       size = 0;
/* 4073 */       if ((this.bitField0_ & 0x1) != 0) {
/* 4074 */         size += 
/* 4075 */           CodedOutputStream.computeUInt32Size(1, this.stmtId_);
/*      */       }
/* 4077 */       size += this.unknownFields.getSerializedSize();
/* 4078 */       this.memoizedSize = size;
/* 4079 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 4084 */       if (obj == this) {
/* 4085 */         return true;
/*      */       }
/* 4087 */       if (!(obj instanceof Deallocate)) {
/* 4088 */         return super.equals(obj);
/*      */       }
/* 4090 */       Deallocate other = (Deallocate)obj;
/*      */       
/* 4092 */       if (hasStmtId() != other.hasStmtId()) return false; 
/* 4093 */       if (hasStmtId() && 
/* 4094 */         getStmtId() != other
/* 4095 */         .getStmtId()) return false;
/*      */       
/* 4097 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 4098 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 4103 */       if (this.memoizedHashCode != 0) {
/* 4104 */         return this.memoizedHashCode;
/*      */       }
/* 4106 */       int hash = 41;
/* 4107 */       hash = 19 * hash + getDescriptor().hashCode();
/* 4108 */       if (hasStmtId()) {
/* 4109 */         hash = 37 * hash + 1;
/* 4110 */         hash = 53 * hash + getStmtId();
/*      */       } 
/* 4112 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 4113 */       this.memoizedHashCode = hash;
/* 4114 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Deallocate parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 4120 */       return (Deallocate)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Deallocate parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 4126 */       return (Deallocate)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Deallocate parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 4131 */       return (Deallocate)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Deallocate parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 4137 */       return (Deallocate)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Deallocate parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 4141 */       return (Deallocate)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Deallocate parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 4147 */       return (Deallocate)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Deallocate parseFrom(InputStream input) throws IOException {
/* 4151 */       return 
/* 4152 */         (Deallocate)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Deallocate parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 4158 */       return 
/* 4159 */         (Deallocate)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Deallocate parseDelimitedFrom(InputStream input) throws IOException {
/* 4163 */       return 
/* 4164 */         (Deallocate)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Deallocate parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 4170 */       return 
/* 4171 */         (Deallocate)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Deallocate parseFrom(CodedInputStream input) throws IOException {
/* 4176 */       return 
/* 4177 */         (Deallocate)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Deallocate parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 4183 */       return 
/* 4184 */         (Deallocate)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 4188 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 4190 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Deallocate prototype) {
/* 4193 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 4197 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 4198 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 4204 */       Builder builder = new Builder(parent);
/* 4205 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxPrepare.DeallocateOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private int stmtId_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 4230 */         return MysqlxPrepare.internal_static_Mysqlx_Prepare_Deallocate_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 4236 */         return MysqlxPrepare.internal_static_Mysqlx_Prepare_Deallocate_fieldAccessorTable
/* 4237 */           .ensureFieldAccessorsInitialized(MysqlxPrepare.Deallocate.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 4243 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 4248 */         super(parent);
/* 4249 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 4253 */         if (MysqlxPrepare.Deallocate.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/* 4258 */         super.clear();
/* 4259 */         this.stmtId_ = 0;
/* 4260 */         this.bitField0_ &= 0xFFFFFFFE;
/* 4261 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 4267 */         return MysqlxPrepare.internal_static_Mysqlx_Prepare_Deallocate_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxPrepare.Deallocate getDefaultInstanceForType() {
/* 4272 */         return MysqlxPrepare.Deallocate.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxPrepare.Deallocate build() {
/* 4277 */         MysqlxPrepare.Deallocate result = buildPartial();
/* 4278 */         if (!result.isInitialized()) {
/* 4279 */           throw newUninitializedMessageException(result);
/*      */         }
/* 4281 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxPrepare.Deallocate buildPartial() {
/* 4286 */         MysqlxPrepare.Deallocate result = new MysqlxPrepare.Deallocate(this);
/* 4287 */         int from_bitField0_ = this.bitField0_;
/* 4288 */         int to_bitField0_ = 0;
/* 4289 */         if ((from_bitField0_ & 0x1) != 0) {
/* 4290 */           result.stmtId_ = this.stmtId_;
/* 4291 */           to_bitField0_ |= 0x1;
/*      */         } 
/* 4293 */         result.bitField0_ = to_bitField0_;
/* 4294 */         onBuilt();
/* 4295 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 4300 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 4306 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 4311 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 4316 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 4322 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 4328 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 4332 */         if (other instanceof MysqlxPrepare.Deallocate) {
/* 4333 */           return mergeFrom((MysqlxPrepare.Deallocate)other);
/*      */         }
/* 4335 */         super.mergeFrom(other);
/* 4336 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxPrepare.Deallocate other) {
/* 4341 */         if (other == MysqlxPrepare.Deallocate.getDefaultInstance()) return this; 
/* 4342 */         if (other.hasStmtId()) {
/* 4343 */           setStmtId(other.getStmtId());
/*      */         }
/* 4345 */         mergeUnknownFields(other.unknownFields);
/* 4346 */         onChanged();
/* 4347 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 4352 */         if (!hasStmtId()) {
/* 4353 */           return false;
/*      */         }
/* 4355 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 4363 */         MysqlxPrepare.Deallocate parsedMessage = null;
/*      */         try {
/* 4365 */           parsedMessage = (MysqlxPrepare.Deallocate)MysqlxPrepare.Deallocate.PARSER.parsePartialFrom(input, extensionRegistry);
/* 4366 */         } catch (InvalidProtocolBufferException e) {
/* 4367 */           parsedMessage = (MysqlxPrepare.Deallocate)e.getUnfinishedMessage();
/* 4368 */           throw e.unwrapIOException();
/*      */         } finally {
/* 4370 */           if (parsedMessage != null) {
/* 4371 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 4374 */         return this;
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
/*      */       public boolean hasStmtId() {
/* 4388 */         return ((this.bitField0_ & 0x1) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int getStmtId() {
/* 4399 */         return this.stmtId_;
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
/*      */       public Builder setStmtId(int value) {
/* 4411 */         this.bitField0_ |= 0x1;
/* 4412 */         this.stmtId_ = value;
/* 4413 */         onChanged();
/* 4414 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearStmtId() {
/* 4425 */         this.bitField0_ &= 0xFFFFFFFE;
/* 4426 */         this.stmtId_ = 0;
/* 4427 */         onChanged();
/* 4428 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 4433 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 4439 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4449 */     private static final Deallocate DEFAULT_INSTANCE = new Deallocate();
/*      */ 
/*      */     
/*      */     public static Deallocate getDefaultInstance() {
/* 4453 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 4457 */     public static final Parser<Deallocate> PARSER = (Parser<Deallocate>)new AbstractParser<Deallocate>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxPrepare.Deallocate parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 4463 */           return new MysqlxPrepare.Deallocate(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Deallocate> parser() {
/* 4468 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Deallocate> getParserForType() {
/* 4473 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Deallocate getDefaultInstanceForType() {
/* 4478 */       return DEFAULT_INSTANCE;
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
/* 4506 */     return descriptor;
/*      */   }
/*      */ 
/*      */   
/*      */   static {
/* 4511 */     String[] descriptorData = { "\n\024mysqlx_prepare.proto\022\016Mysqlx.Prepare\032\fmysqlx.proto\032\020mysqlx_sql.proto\032\021mysqlx_crud.proto\032\026mysqlx_datatypes.proto\"\003\n\007Prepare\022\017\n\007stmt_id\030\001 \002(\r\0222\n\004stmt\030\002 \002(\0132$.Mysqlx.Prepare.Prepare.OneOfMessage\032Æ\002\n\fOneOfMessage\0227\n\004type\030\001 \002(\0162).Mysqlx.Prepare.Prepare.OneOfMessage.Type\022\037\n\004find\030\002 \001(\0132\021.Mysqlx.Crud.Find\022#\n\006insert\030\003 \001(\0132\023.Mysqlx.Crud.Insert\022#\n\006update\030\004 \001(\0132\023.Mysqlx.Crud.Update\022#\n\006delete\030\005 \001(\0132\023.Mysqlx.Crud.Delete\022-\n\fstmt_execute\030\006 \001(\0132\027.Mysqlx.Sql.StmtExecute\">\n\004Type\022\b\n\004FIND\020\000\022\n\n\006INSERT\020\001\022\n\n\006UPDATE\020\002\022\n\n\006DELETE\020\004\022\b\n\004STMT\020\005:\004ê0(\"f\n\007Execute\022\017\n\007stmt_id\030\001 \002(\r\022#\n\004args\030\002 \003(\0132\025.Mysqlx.Datatypes.Any\022\037\n\020compact_metadata\030\003 \001(\b:\005false:\004ê0)\"#\n\nDeallocate\022\017\n\007stmt_id\030\001 \002(\r:\004ê0*B\031\n\027com.mysql.cj.x.protobuf" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4532 */     descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] {
/*      */           
/* 4534 */           Mysqlx.getDescriptor(), 
/* 4535 */           MysqlxSql.getDescriptor(), 
/* 4536 */           MysqlxCrud.getDescriptor(), 
/* 4537 */           MysqlxDatatypes.getDescriptor()
/*      */         });
/*      */     
/* 4540 */     internal_static_Mysqlx_Prepare_Prepare_descriptor = getDescriptor().getMessageTypes().get(0);
/* 4541 */     internal_static_Mysqlx_Prepare_Prepare_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Prepare_Prepare_descriptor, new String[] { "StmtId", "Stmt" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4546 */     internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor = internal_static_Mysqlx_Prepare_Prepare_descriptor.getNestedTypes().get(0);
/* 4547 */     internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor, new String[] { "Type", "Find", "Insert", "Update", "Delete", "StmtExecute" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4552 */     internal_static_Mysqlx_Prepare_Execute_descriptor = getDescriptor().getMessageTypes().get(1);
/* 4553 */     internal_static_Mysqlx_Prepare_Execute_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Prepare_Execute_descriptor, new String[] { "StmtId", "Args", "CompactMetadata" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4558 */     internal_static_Mysqlx_Prepare_Deallocate_descriptor = getDescriptor().getMessageTypes().get(2);
/* 4559 */     internal_static_Mysqlx_Prepare_Deallocate_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Prepare_Deallocate_descriptor, new String[] { "StmtId" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4564 */     ExtensionRegistry registry = ExtensionRegistry.newInstance();
/* 4565 */     registry.add(Mysqlx.clientMessageId);
/*      */     
/* 4567 */     Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
/* 4568 */     Mysqlx.getDescriptor();
/* 4569 */     MysqlxSql.getDescriptor();
/* 4570 */     MysqlxCrud.getDescriptor();
/* 4571 */     MysqlxDatatypes.getDescriptor();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\x\protobuf\MysqlxPrepare.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */