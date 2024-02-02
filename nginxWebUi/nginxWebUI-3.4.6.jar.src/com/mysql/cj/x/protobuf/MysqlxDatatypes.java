/*      */ package com.mysql.cj.x.protobuf;
/*      */ import com.google.protobuf.AbstractMessage;
/*      */ import com.google.protobuf.AbstractMessageLite;
/*      */ import com.google.protobuf.ByteString;
/*      */ import com.google.protobuf.CodedInputStream;
/*      */ import com.google.protobuf.CodedOutputStream;
/*      */ import com.google.protobuf.Descriptors;
/*      */ import com.google.protobuf.ExtensionRegistryLite;
/*      */ import com.google.protobuf.GeneratedMessageV3;
/*      */ import com.google.protobuf.Internal;
/*      */ import com.google.protobuf.InvalidProtocolBufferException;
/*      */ import com.google.protobuf.Message;
/*      */ import com.google.protobuf.MessageLite;
/*      */ import com.google.protobuf.Parser;
/*      */ import com.google.protobuf.SingleFieldBuilderV3;
/*      */ import com.google.protobuf.UnknownFieldSet;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ 
/*      */ public final class MysqlxDatatypes {
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Datatypes_Scalar_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Datatypes_Scalar_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Datatypes_Scalar_String_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Datatypes_Scalar_String_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Datatypes_Scalar_Octets_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Datatypes_Scalar_Octets_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Datatypes_Object_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Datatypes_Object_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Datatypes_Object_ObjectField_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Datatypes_Object_ObjectField_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Datatypes_Array_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Datatypes_Array_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Datatypes_Any_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Datatypes_Any_fieldAccessorTable;
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
/*      */   public static interface ScalarOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasType();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.Scalar.Type getType();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasVSignedInt();
/*      */ 
/*      */ 
/*      */     
/*      */     long getVSignedInt();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasVUnsignedInt();
/*      */ 
/*      */ 
/*      */     
/*      */     long getVUnsignedInt();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasVOctets();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.Scalar.Octets getVOctets();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.Scalar.OctetsOrBuilder getVOctetsOrBuilder();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasVDouble();
/*      */ 
/*      */ 
/*      */     
/*      */     double getVDouble();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasVFloat();
/*      */ 
/*      */ 
/*      */     
/*      */     float getVFloat();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasVBool();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean getVBool();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasVString();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.Scalar.String getVString();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.Scalar.StringOrBuilder getVStringOrBuilder();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Scalar
/*      */     extends GeneratedMessageV3
/*      */     implements ScalarOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */     
/*      */     public static final int TYPE_FIELD_NUMBER = 1;
/*      */ 
/*      */     
/*      */     private int type_;
/*      */     
/*      */     public static final int V_SIGNED_INT_FIELD_NUMBER = 2;
/*      */     
/*      */     private long vSignedInt_;
/*      */     
/*      */     public static final int V_UNSIGNED_INT_FIELD_NUMBER = 3;
/*      */     
/*      */     private long vUnsignedInt_;
/*      */     
/*      */     public static final int V_OCTETS_FIELD_NUMBER = 5;
/*      */     
/*      */     private Octets vOctets_;
/*      */     
/*      */     public static final int V_DOUBLE_FIELD_NUMBER = 6;
/*      */     
/*      */     private double vDouble_;
/*      */     
/*      */     public static final int V_FLOAT_FIELD_NUMBER = 7;
/*      */     
/*      */     private float vFloat_;
/*      */     
/*      */     public static final int V_BOOL_FIELD_NUMBER = 8;
/*      */     
/*      */     private boolean vBool_;
/*      */     
/*      */     public static final int V_STRING_FIELD_NUMBER = 9;
/*      */     
/*      */     private String vString_;
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */     
/*      */     private Scalar(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/*  172 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1886 */       this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Scalar(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Scalar(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int rawValue; Octets.Builder builder; String.Builder subBuilder; Type value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: rawValue = input.readEnum(); value = Type.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(1, rawValue); continue; }  this.bitField0_ |= 0x1; this.type_ = rawValue; continue;case 16: this.bitField0_ |= 0x2; this.vSignedInt_ = input.readSInt64(); continue;case 24: this.bitField0_ |= 0x4; this.vUnsignedInt_ = input.readUInt64(); continue;case 42: builder = null; if ((this.bitField0_ & 0x8) != 0) builder = this.vOctets_.toBuilder();  this.vOctets_ = (Octets)input.readMessage(Octets.PARSER, extensionRegistry); if (builder != null) { builder.mergeFrom(this.vOctets_); this.vOctets_ = builder.buildPartial(); }  this.bitField0_ |= 0x8; continue;case 49: this.bitField0_ |= 0x10; this.vDouble_ = input.readDouble(); continue;case 61: this.bitField0_ |= 0x20; this.vFloat_ = input.readFloat(); continue;case 64: this.bitField0_ |= 0x40; this.vBool_ = input.readBool(); continue;case 74: subBuilder = null; if ((this.bitField0_ & 0x80) != 0) subBuilder = this.vString_.toBuilder();  this.vString_ = (String)input.readMessage(String.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.vString_); this.vString_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x80; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_fieldAccessorTable.ensureFieldAccessorsInitialized(Scalar.class, Builder.class); } public enum Type implements ProtocolMessageEnum { V_SINT(1), V_UINT(2), V_NULL(3), V_OCTETS(4), V_DOUBLE(5), V_FLOAT(6), V_BOOL(7), V_STRING(8); public static final int V_SINT_VALUE = 1; public static final int V_UINT_VALUE = 2; public static final int V_NULL_VALUE = 3; public static final int V_OCTETS_VALUE = 4; public static final int V_DOUBLE_VALUE = 5; public static final int V_FLOAT_VALUE = 6; public static final int V_BOOL_VALUE = 7; public static final int V_STRING_VALUE = 8; private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() { public MysqlxDatatypes.Scalar.Type findValueByNumber(int number) { return MysqlxDatatypes.Scalar.Type.forNumber(number); } }; private static final Type[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Type forNumber(int value) { switch (value) { case 1: return V_SINT;case 2: return V_UINT;case 3: return V_NULL;case 4: return V_OCTETS;case 5: return V_DOUBLE;case 6: return V_FLOAT;case 7: return V_BOOL;case 8: return V_STRING; }  return null; } public static Internal.EnumLiteMap<Type> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxDatatypes.Scalar.getDescriptor().getEnumTypes().get(0); } Type(int value) { this.value = value; } } public static final class String extends GeneratedMessageV3 implements StringOrBuilder { private static final long serialVersionUID = 0L; private int bitField0_; public static final int VALUE_FIELD_NUMBER = 1; private ByteString value_; public static final int COLLATION_FIELD_NUMBER = 2; private long collation_; private byte memoizedIsInitialized; private String(GeneratedMessageV3.Builder<?> builder) { super(builder); this.memoizedIsInitialized = -1; } private String() { this.memoizedIsInitialized = -1; this.value_ = ByteString.EMPTY; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new String(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private String(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: this.bitField0_ |= 0x1; this.value_ = input.readBytes(); continue;case 16: this.bitField0_ |= 0x2; this.collation_ = input.readUInt64(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_String_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_String_fieldAccessorTable.ensureFieldAccessorsInitialized(String.class, Builder.class); } public boolean hasValue() { return ((this.bitField0_ & 0x1) != 0); } public ByteString getValue() { return this.value_; } public boolean hasCollation() { return ((this.bitField0_ & 0x2) != 0); } public long getCollation() { return this.collation_; } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized; if (isInitialized == 1) return true;  if (isInitialized == 0) return false;  if (!hasValue()) { this.memoizedIsInitialized = 0; return false; }  this.memoizedIsInitialized = 1; return true; } public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) output.writeBytes(1, this.value_);  if ((this.bitField0_ & 0x2) != 0) output.writeUInt64(2, this.collation_);  this.unknownFields.writeTo(output); } public int getSerializedSize() { int size = this.memoizedSize; if (size != -1) return size;  size = 0; if ((this.bitField0_ & 0x1) != 0) size += CodedOutputStream.computeBytesSize(1, this.value_);  if ((this.bitField0_ & 0x2) != 0) size += CodedOutputStream.computeUInt64Size(2, this.collation_);  size += this.unknownFields.getSerializedSize(); this.memoizedSize = size; return size; } public boolean equals(Object obj) { if (obj == this) return true;  if (!(obj instanceof String)) return super.equals(obj);  String other = (String)obj; if (hasValue() != other.hasValue()) return false;  if (hasValue() && !getValue().equals(other.getValue())) return false;  if (hasCollation() != other.hasCollation()) return false;  if (hasCollation() && getCollation() != other.getCollation()) return false;  if (!this.unknownFields.equals(other.unknownFields)) return false;  return true; } public int hashCode() { if (this.memoizedHashCode != 0) return this.memoizedHashCode;  int hash = 41; hash = 19 * hash + getDescriptor().hashCode(); if (hasValue()) { hash = 37 * hash + 1; hash = 53 * hash + getValue().hashCode(); }  if (hasCollation()) { hash = 37 * hash + 2; hash = 53 * hash + Internal.hashLong(getCollation()); }  hash = 29 * hash + this.unknownFields.hashCode(); this.memoizedHashCode = hash; return hash; } public static String parseFrom(ByteBuffer data) throws InvalidProtocolBufferException { return (String)PARSER.parseFrom(data); } public static String parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (String)PARSER.parseFrom(data, extensionRegistry); } public static String parseFrom(ByteString data) throws InvalidProtocolBufferException { return (String)PARSER.parseFrom(data); } public static String parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (String)PARSER.parseFrom(data, extensionRegistry); } public static String parseFrom(byte[] data) throws InvalidProtocolBufferException { return (String)PARSER.parseFrom(data); } public static String parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (String)PARSER.parseFrom(data, extensionRegistry); } public static String parseFrom(InputStream input) throws IOException { return (String)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static String parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (String)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public static String parseDelimitedFrom(InputStream input) throws IOException { return (String)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input); } public static String parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (String)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry); } public static String parseFrom(CodedInputStream input) throws IOException { return (String)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static String parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (String)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public Builder newBuilderForType() { return newBuilder(); } public static Builder newBuilder() { return DEFAULT_INSTANCE.toBuilder(); } public static Builder newBuilder(String prototype) { return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype); } public Builder toBuilder() { return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder()).mergeFrom(this); } protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) { Builder builder = new Builder(parent); return builder; } public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxDatatypes.Scalar.StringOrBuilder { private int bitField0_; private ByteString value_; private long collation_; public static final Descriptors.Descriptor getDescriptor() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_String_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_String_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxDatatypes.Scalar.String.class, Builder.class); } private Builder() { this.value_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.value_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxDatatypes.Scalar.String.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.value_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFFE; this.collation_ = 0L; this.bitField0_ &= 0xFFFFFFFD; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_String_descriptor; } public MysqlxDatatypes.Scalar.String getDefaultInstanceForType() { return MysqlxDatatypes.Scalar.String.getDefaultInstance(); } public MysqlxDatatypes.Scalar.String build() { MysqlxDatatypes.Scalar.String result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxDatatypes.Scalar.String buildPartial() { MysqlxDatatypes.Scalar.String result = new MysqlxDatatypes.Scalar.String(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.value_ = this.value_; if ((from_bitField0_ & 0x2) != 0) { result.collation_ = this.collation_; to_bitField0_ |= 0x2; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxDatatypes.Scalar.String) return mergeFrom((MysqlxDatatypes.Scalar.String)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxDatatypes.Scalar.String other) { if (other == MysqlxDatatypes.Scalar.String.getDefaultInstance()) return this;  if (other.hasValue()) setValue(other.getValue());  if (other.hasCollation()) setCollation(other.getCollation());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasValue()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxDatatypes.Scalar.String parsedMessage = null; try { parsedMessage = (MysqlxDatatypes.Scalar.String)MysqlxDatatypes.Scalar.String.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxDatatypes.Scalar.String)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasValue() { return ((this.bitField0_ & 0x1) != 0); } public ByteString getValue() { return this.value_; } public Builder setValue(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.value_ = value; onChanged(); return this; } public Builder clearValue() { this.bitField0_ &= 0xFFFFFFFE; this.value_ = MysqlxDatatypes.Scalar.String.getDefaultInstance().getValue(); onChanged(); return this; } public boolean hasCollation() { return ((this.bitField0_ & 0x2) != 0); } public long getCollation() { return this.collation_; } public Builder setCollation(long value) { this.bitField0_ |= 0x2; this.collation_ = value; onChanged(); return this; } public Builder clearCollation() { this.bitField0_ &= 0xFFFFFFFD; this.collation_ = 0L; onChanged(); return this; } public final Builder setUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.setUnknownFields(unknownFields); } public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.mergeUnknownFields(unknownFields); } } private static final String DEFAULT_INSTANCE = new String(); public static String getDefaultInstance() { return DEFAULT_INSTANCE; } @Deprecated public static final Parser<String> PARSER = (Parser<String>)new AbstractParser<String>() { public MysqlxDatatypes.Scalar.String parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return new MysqlxDatatypes.Scalar.String(input, extensionRegistry); } }; public static Parser<String> parser() { return PARSER; } public Parser<String> getParserForType() { return PARSER; } public String getDefaultInstanceForType() { return DEFAULT_INSTANCE; } } private Scalar() { this.memoizedIsInitialized = -1; this.type_ = 1; } public static final class Builder extends GeneratedMessageV3.Builder<String.Builder> implements StringOrBuilder {
/*      */       private int bitField0_; private ByteString value_; private long collation_; public static final Descriptors.Descriptor getDescriptor() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_String_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_String_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxDatatypes.Scalar.String.class, Builder.class); } private Builder() { this.value_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.value_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxDatatypes.Scalar.String.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.value_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFFE; this.collation_ = 0L; this.bitField0_ &= 0xFFFFFFFD; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_String_descriptor; } public MysqlxDatatypes.Scalar.String getDefaultInstanceForType() { return MysqlxDatatypes.Scalar.String.getDefaultInstance(); } public MysqlxDatatypes.Scalar.String build() { MysqlxDatatypes.Scalar.String result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxDatatypes.Scalar.String buildPartial() { MysqlxDatatypes.Scalar.String result = new MysqlxDatatypes.Scalar.String(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.value_ = this.value_; if ((from_bitField0_ & 0x2) != 0) { result.collation_ = this.collation_; to_bitField0_ |= 0x2; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxDatatypes.Scalar.String) return mergeFrom((MysqlxDatatypes.Scalar.String)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxDatatypes.Scalar.String other) { if (other == MysqlxDatatypes.Scalar.String.getDefaultInstance()) return this;  if (other.hasValue()) setValue(other.getValue());  if (other.hasCollation()) setCollation(other.getCollation());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasValue()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxDatatypes.Scalar.String parsedMessage = null; try { parsedMessage = (MysqlxDatatypes.Scalar.String)MysqlxDatatypes.Scalar.String.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxDatatypes.Scalar.String)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasValue() { return ((this.bitField0_ & 0x1) != 0); } public ByteString getValue() { return this.value_; } public Builder setValue(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.value_ = value; onChanged(); return this; } public Builder clearValue() { this.bitField0_ &= 0xFFFFFFFE; this.value_ = MysqlxDatatypes.Scalar.String.getDefaultInstance().getValue(); onChanged(); return this; } public boolean hasCollation() { return ((this.bitField0_ & 0x2) != 0); } public long getCollation() { return this.collation_; } public Builder setCollation(long value) { this.bitField0_ |= 0x2; this.collation_ = value; onChanged(); return this; } public Builder clearCollation() { this.bitField0_ &= 0xFFFFFFFD; this.collation_ = 0L; onChanged(); return this; } public final Builder setUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.setUnknownFields(unknownFields); } public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.mergeUnknownFields(unknownFields); } } public static final class Octets extends GeneratedMessageV3 implements OctetsOrBuilder {
/*      */       private static final long serialVersionUID = 0L; private int bitField0_; public static final int VALUE_FIELD_NUMBER = 1; private ByteString value_; public static final int CONTENT_TYPE_FIELD_NUMBER = 2; private int contentType_; private byte memoizedIsInitialized; private Octets(GeneratedMessageV3.Builder<?> builder) { super(builder); this.memoizedIsInitialized = -1; } private Octets() { this.memoizedIsInitialized = -1; this.value_ = ByteString.EMPTY; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Octets(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Octets(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: this.bitField0_ |= 0x1; this.value_ = input.readBytes(); continue;case 16: this.bitField0_ |= 0x2; this.contentType_ = input.readUInt32(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_Octets_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_Octets_fieldAccessorTable.ensureFieldAccessorsInitialized(Octets.class, Builder.class); } public boolean hasValue() { return ((this.bitField0_ & 0x1) != 0); } public ByteString getValue() { return this.value_; } public boolean hasContentType() { return ((this.bitField0_ & 0x2) != 0); } public int getContentType() { return this.contentType_; } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized; if (isInitialized == 1) return true;  if (isInitialized == 0) return false;  if (!hasValue()) { this.memoizedIsInitialized = 0; return false; }  this.memoizedIsInitialized = 1; return true; } public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) output.writeBytes(1, this.value_);  if ((this.bitField0_ & 0x2) != 0) output.writeUInt32(2, this.contentType_);  this.unknownFields.writeTo(output); } public int getSerializedSize() { int size = this.memoizedSize; if (size != -1) return size;  size = 0; if ((this.bitField0_ & 0x1) != 0) size += CodedOutputStream.computeBytesSize(1, this.value_);  if ((this.bitField0_ & 0x2) != 0) size += CodedOutputStream.computeUInt32Size(2, this.contentType_);  size += this.unknownFields.getSerializedSize(); this.memoizedSize = size; return size; } public boolean equals(Object obj) { if (obj == this) return true;  if (!(obj instanceof Octets)) return super.equals(obj);  Octets other = (Octets)obj; if (hasValue() != other.hasValue()) return false;  if (hasValue() && !getValue().equals(other.getValue())) return false;  if (hasContentType() != other.hasContentType()) return false;  if (hasContentType() && getContentType() != other.getContentType()) return false;  if (!this.unknownFields.equals(other.unknownFields)) return false;  return true; } public int hashCode() { if (this.memoizedHashCode != 0) return this.memoizedHashCode;  int hash = 41; hash = 19 * hash + getDescriptor().hashCode(); if (hasValue()) { hash = 37 * hash + 1; hash = 53 * hash + getValue().hashCode(); }  if (hasContentType()) { hash = 37 * hash + 2; hash = 53 * hash + getContentType(); }  hash = 29 * hash + this.unknownFields.hashCode(); this.memoizedHashCode = hash; return hash; } public static Octets parseFrom(ByteBuffer data) throws InvalidProtocolBufferException { return (Octets)PARSER.parseFrom(data); } public static Octets parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (Octets)PARSER.parseFrom(data, extensionRegistry); } public static Octets parseFrom(ByteString data) throws InvalidProtocolBufferException { return (Octets)PARSER.parseFrom(data); } public static Octets parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (Octets)PARSER.parseFrom(data, extensionRegistry); } public static Octets parseFrom(byte[] data) throws InvalidProtocolBufferException { return (Octets)PARSER.parseFrom(data); } public static Octets parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (Octets)PARSER.parseFrom(data, extensionRegistry); } public static Octets parseFrom(InputStream input) throws IOException { return (Octets)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static Octets parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (Octets)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public static Octets parseDelimitedFrom(InputStream input) throws IOException { return (Octets)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input); } public static Octets parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (Octets)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry); } public static Octets parseFrom(CodedInputStream input) throws IOException { return (Octets)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static Octets parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (Octets)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public Builder newBuilderForType() { return newBuilder(); } public static Builder newBuilder() { return DEFAULT_INSTANCE.toBuilder(); } public static Builder newBuilder(Octets prototype) { return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype); } public Builder toBuilder() { return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder()).mergeFrom(this); } protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) { Builder builder = new Builder(parent); return builder; } public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxDatatypes.Scalar.OctetsOrBuilder {
/* 1889 */         private int bitField0_; private ByteString value_; private int contentType_; public static final Descriptors.Descriptor getDescriptor() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_Octets_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_Octets_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxDatatypes.Scalar.Octets.class, Builder.class); } private Builder() { this.value_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.value_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxDatatypes.Scalar.Octets.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.value_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFFE; this.contentType_ = 0; this.bitField0_ &= 0xFFFFFFFD; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_Octets_descriptor; } public MysqlxDatatypes.Scalar.Octets getDefaultInstanceForType() { return MysqlxDatatypes.Scalar.Octets.getDefaultInstance(); } public MysqlxDatatypes.Scalar.Octets build() { MysqlxDatatypes.Scalar.Octets result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxDatatypes.Scalar.Octets buildPartial() { MysqlxDatatypes.Scalar.Octets result = new MysqlxDatatypes.Scalar.Octets(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.value_ = this.value_; if ((from_bitField0_ & 0x2) != 0) { result.contentType_ = this.contentType_; to_bitField0_ |= 0x2; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxDatatypes.Scalar.Octets) return mergeFrom((MysqlxDatatypes.Scalar.Octets)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxDatatypes.Scalar.Octets other) { if (other == MysqlxDatatypes.Scalar.Octets.getDefaultInstance()) return this;  if (other.hasValue()) setValue(other.getValue());  if (other.hasContentType()) setContentType(other.getContentType());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasValue()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxDatatypes.Scalar.Octets parsedMessage = null; try { parsedMessage = (MysqlxDatatypes.Scalar.Octets)MysqlxDatatypes.Scalar.Octets.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxDatatypes.Scalar.Octets)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasValue() { return ((this.bitField0_ & 0x1) != 0); } public ByteString getValue() { return this.value_; } public Builder setValue(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.value_ = value; onChanged(); return this; } public Builder clearValue() { this.bitField0_ &= 0xFFFFFFFE; this.value_ = MysqlxDatatypes.Scalar.Octets.getDefaultInstance().getValue(); onChanged(); return this; } public boolean hasContentType() { return ((this.bitField0_ & 0x2) != 0); } public int getContentType() { return this.contentType_; } public Builder setContentType(int value) { this.bitField0_ |= 0x2; this.contentType_ = value; onChanged(); return this; } public Builder clearContentType() { this.bitField0_ &= 0xFFFFFFFD; this.contentType_ = 0; onChanged(); return this; } public final Builder setUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.setUnknownFields(unknownFields); } public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.mergeUnknownFields(unknownFields); } } private static final Octets DEFAULT_INSTANCE = new Octets(); public static Octets getDefaultInstance() { return DEFAULT_INSTANCE; } @Deprecated public static final Parser<Octets> PARSER = (Parser<Octets>)new AbstractParser<Octets>() { public MysqlxDatatypes.Scalar.Octets parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return new MysqlxDatatypes.Scalar.Octets(input, extensionRegistry); } }; public static Parser<Octets> parser() { return PARSER; } public Parser<Octets> getParserForType() { return PARSER; } public Octets getDefaultInstanceForType() { return DEFAULT_INSTANCE; } } public static final class Builder extends GeneratedMessageV3.Builder<Octets.Builder> implements OctetsOrBuilder { private int bitField0_; private ByteString value_; private int contentType_; public static final Descriptors.Descriptor getDescriptor() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_Octets_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_Octets_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxDatatypes.Scalar.Octets.class, Builder.class); } private Builder() { this.value_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.value_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxDatatypes.Scalar.Octets.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.value_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFFE; this.contentType_ = 0; this.bitField0_ &= 0xFFFFFFFD; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_Octets_descriptor; } public MysqlxDatatypes.Scalar.Octets getDefaultInstanceForType() { return MysqlxDatatypes.Scalar.Octets.getDefaultInstance(); } public MysqlxDatatypes.Scalar.Octets build() { MysqlxDatatypes.Scalar.Octets result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxDatatypes.Scalar.Octets buildPartial() { MysqlxDatatypes.Scalar.Octets result = new MysqlxDatatypes.Scalar.Octets(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.value_ = this.value_; if ((from_bitField0_ & 0x2) != 0) { result.contentType_ = this.contentType_; to_bitField0_ |= 0x2; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxDatatypes.Scalar.Octets) return mergeFrom((MysqlxDatatypes.Scalar.Octets)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxDatatypes.Scalar.Octets other) { if (other == MysqlxDatatypes.Scalar.Octets.getDefaultInstance()) return this;  if (other.hasValue()) setValue(other.getValue());  if (other.hasContentType()) setContentType(other.getContentType());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasValue()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxDatatypes.Scalar.Octets parsedMessage = null; try { parsedMessage = (MysqlxDatatypes.Scalar.Octets)MysqlxDatatypes.Scalar.Octets.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxDatatypes.Scalar.Octets)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasValue() { return ((this.bitField0_ & 0x1) != 0); } public ByteString getValue() { return this.value_; } public Builder setValue(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.value_ = value; onChanged(); return this; } public Builder clearValue() { this.bitField0_ &= 0xFFFFFFFE; this.value_ = MysqlxDatatypes.Scalar.Octets.getDefaultInstance().getValue(); onChanged(); return this; } public boolean hasContentType() { return ((this.bitField0_ & 0x2) != 0); } public int getContentType() { return this.contentType_; } public Builder setContentType(int value) { this.bitField0_ |= 0x2; this.contentType_ = value; onChanged(); return this; } public Builder clearContentType() { this.bitField0_ &= 0xFFFFFFFD; this.contentType_ = 0; onChanged(); return this; } public final Builder setUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.setUnknownFields(unknownFields); } public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.mergeUnknownFields(unknownFields); } } public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); } public Type getType() { Type result = Type.valueOf(this.type_); return (result == null) ? Type.V_SINT : result; } public boolean hasVSignedInt() { return ((this.bitField0_ & 0x2) != 0); } public long getVSignedInt() { return this.vSignedInt_; } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 1890 */       if (isInitialized == 1) return true; 
/* 1891 */       if (isInitialized == 0) return false;
/*      */       
/* 1893 */       if (!hasType()) {
/* 1894 */         this.memoizedIsInitialized = 0;
/* 1895 */         return false;
/*      */       } 
/* 1897 */       if (hasVOctets() && 
/* 1898 */         !getVOctets().isInitialized()) {
/* 1899 */         this.memoizedIsInitialized = 0;
/* 1900 */         return false;
/*      */       } 
/*      */       
/* 1903 */       if (hasVString() && 
/* 1904 */         !getVString().isInitialized()) {
/* 1905 */         this.memoizedIsInitialized = 0;
/* 1906 */         return false;
/*      */       } 
/*      */       
/* 1909 */       this.memoizedIsInitialized = 1;
/* 1910 */       return true; } public boolean hasVUnsignedInt() { return ((this.bitField0_ & 0x4) != 0); } public long getVUnsignedInt() { return this.vUnsignedInt_; } public boolean hasVOctets() { return ((this.bitField0_ & 0x8) != 0); } public Octets getVOctets() { return (this.vOctets_ == null) ? Octets.getDefaultInstance() : this.vOctets_; } public OctetsOrBuilder getVOctetsOrBuilder() { return (this.vOctets_ == null) ? Octets.getDefaultInstance() : this.vOctets_; } public boolean hasVDouble() { return ((this.bitField0_ & 0x10) != 0); } public double getVDouble() { return this.vDouble_; } public boolean hasVFloat() { return ((this.bitField0_ & 0x20) != 0); } public float getVFloat() { return this.vFloat_; }
/*      */     public boolean hasVBool() { return ((this.bitField0_ & 0x40) != 0); }
/*      */     public boolean getVBool() { return this.vBool_; }
/*      */     public boolean hasVString() { return ((this.bitField0_ & 0x80) != 0); }
/*      */     public String getVString() { return (this.vString_ == null) ? String.getDefaultInstance() : this.vString_; }
/*      */     public StringOrBuilder getVStringOrBuilder() { return (this.vString_ == null) ? String.getDefaultInstance() : this.vString_; }
/* 1916 */     public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) {
/* 1917 */         output.writeEnum(1, this.type_);
/*      */       }
/* 1919 */       if ((this.bitField0_ & 0x2) != 0) {
/* 1920 */         output.writeSInt64(2, this.vSignedInt_);
/*      */       }
/* 1922 */       if ((this.bitField0_ & 0x4) != 0) {
/* 1923 */         output.writeUInt64(3, this.vUnsignedInt_);
/*      */       }
/* 1925 */       if ((this.bitField0_ & 0x8) != 0) {
/* 1926 */         output.writeMessage(5, (MessageLite)getVOctets());
/*      */       }
/* 1928 */       if ((this.bitField0_ & 0x10) != 0) {
/* 1929 */         output.writeDouble(6, this.vDouble_);
/*      */       }
/* 1931 */       if ((this.bitField0_ & 0x20) != 0) {
/* 1932 */         output.writeFloat(7, this.vFloat_);
/*      */       }
/* 1934 */       if ((this.bitField0_ & 0x40) != 0) {
/* 1935 */         output.writeBool(8, this.vBool_);
/*      */       }
/* 1937 */       if ((this.bitField0_ & 0x80) != 0) {
/* 1938 */         output.writeMessage(9, (MessageLite)getVString());
/*      */       }
/* 1940 */       this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 1945 */       int size = this.memoizedSize;
/* 1946 */       if (size != -1) return size;
/*      */       
/* 1948 */       size = 0;
/* 1949 */       if ((this.bitField0_ & 0x1) != 0) {
/* 1950 */         size += 
/* 1951 */           CodedOutputStream.computeEnumSize(1, this.type_);
/*      */       }
/* 1953 */       if ((this.bitField0_ & 0x2) != 0) {
/* 1954 */         size += 
/* 1955 */           CodedOutputStream.computeSInt64Size(2, this.vSignedInt_);
/*      */       }
/* 1957 */       if ((this.bitField0_ & 0x4) != 0) {
/* 1958 */         size += 
/* 1959 */           CodedOutputStream.computeUInt64Size(3, this.vUnsignedInt_);
/*      */       }
/* 1961 */       if ((this.bitField0_ & 0x8) != 0) {
/* 1962 */         size += 
/* 1963 */           CodedOutputStream.computeMessageSize(5, (MessageLite)getVOctets());
/*      */       }
/* 1965 */       if ((this.bitField0_ & 0x10) != 0) {
/* 1966 */         size += 
/* 1967 */           CodedOutputStream.computeDoubleSize(6, this.vDouble_);
/*      */       }
/* 1969 */       if ((this.bitField0_ & 0x20) != 0) {
/* 1970 */         size += 
/* 1971 */           CodedOutputStream.computeFloatSize(7, this.vFloat_);
/*      */       }
/* 1973 */       if ((this.bitField0_ & 0x40) != 0) {
/* 1974 */         size += 
/* 1975 */           CodedOutputStream.computeBoolSize(8, this.vBool_);
/*      */       }
/* 1977 */       if ((this.bitField0_ & 0x80) != 0) {
/* 1978 */         size += 
/* 1979 */           CodedOutputStream.computeMessageSize(9, (MessageLite)getVString());
/*      */       }
/* 1981 */       size += this.unknownFields.getSerializedSize();
/* 1982 */       this.memoizedSize = size;
/* 1983 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1988 */       if (obj == this) {
/* 1989 */         return true;
/*      */       }
/* 1991 */       if (!(obj instanceof Scalar)) {
/* 1992 */         return super.equals(obj);
/*      */       }
/* 1994 */       Scalar other = (Scalar)obj;
/*      */       
/* 1996 */       if (hasType() != other.hasType()) return false; 
/* 1997 */       if (hasType() && 
/* 1998 */         this.type_ != other.type_) return false;
/*      */       
/* 2000 */       if (hasVSignedInt() != other.hasVSignedInt()) return false; 
/* 2001 */       if (hasVSignedInt() && 
/* 2002 */         getVSignedInt() != other
/* 2003 */         .getVSignedInt()) return false;
/*      */       
/* 2005 */       if (hasVUnsignedInt() != other.hasVUnsignedInt()) return false; 
/* 2006 */       if (hasVUnsignedInt() && 
/* 2007 */         getVUnsignedInt() != other
/* 2008 */         .getVUnsignedInt()) return false;
/*      */       
/* 2010 */       if (hasVOctets() != other.hasVOctets()) return false; 
/* 2011 */       if (hasVOctets() && 
/*      */         
/* 2013 */         !getVOctets().equals(other.getVOctets())) return false;
/*      */       
/* 2015 */       if (hasVDouble() != other.hasVDouble()) return false; 
/* 2016 */       if (hasVDouble() && 
/* 2017 */         Double.doubleToLongBits(getVDouble()) != 
/* 2018 */         Double.doubleToLongBits(other
/* 2019 */           .getVDouble())) return false;
/*      */       
/* 2021 */       if (hasVFloat() != other.hasVFloat()) return false; 
/* 2022 */       if (hasVFloat() && 
/* 2023 */         Float.floatToIntBits(getVFloat()) != 
/* 2024 */         Float.floatToIntBits(other
/* 2025 */           .getVFloat())) return false;
/*      */       
/* 2027 */       if (hasVBool() != other.hasVBool()) return false; 
/* 2028 */       if (hasVBool() && 
/* 2029 */         getVBool() != other
/* 2030 */         .getVBool()) return false;
/*      */       
/* 2032 */       if (hasVString() != other.hasVString()) return false; 
/* 2033 */       if (hasVString() && 
/*      */         
/* 2035 */         !getVString().equals(other.getVString())) return false;
/*      */       
/* 2037 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 2038 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 2043 */       if (this.memoizedHashCode != 0) {
/* 2044 */         return this.memoizedHashCode;
/*      */       }
/* 2046 */       int hash = 41;
/* 2047 */       hash = 19 * hash + getDescriptor().hashCode();
/* 2048 */       if (hasType()) {
/* 2049 */         hash = 37 * hash + 1;
/* 2050 */         hash = 53 * hash + this.type_;
/*      */       } 
/* 2052 */       if (hasVSignedInt()) {
/* 2053 */         hash = 37 * hash + 2;
/* 2054 */         hash = 53 * hash + Internal.hashLong(
/* 2055 */             getVSignedInt());
/*      */       } 
/* 2057 */       if (hasVUnsignedInt()) {
/* 2058 */         hash = 37 * hash + 3;
/* 2059 */         hash = 53 * hash + Internal.hashLong(
/* 2060 */             getVUnsignedInt());
/*      */       } 
/* 2062 */       if (hasVOctets()) {
/* 2063 */         hash = 37 * hash + 5;
/* 2064 */         hash = 53 * hash + getVOctets().hashCode();
/*      */       } 
/* 2066 */       if (hasVDouble()) {
/* 2067 */         hash = 37 * hash + 6;
/* 2068 */         hash = 53 * hash + Internal.hashLong(
/* 2069 */             Double.doubleToLongBits(getVDouble()));
/*      */       } 
/* 2071 */       if (hasVFloat()) {
/* 2072 */         hash = 37 * hash + 7;
/* 2073 */         hash = 53 * hash + Float.floatToIntBits(
/* 2074 */             getVFloat());
/*      */       } 
/* 2076 */       if (hasVBool()) {
/* 2077 */         hash = 37 * hash + 8;
/* 2078 */         hash = 53 * hash + Internal.hashBoolean(
/* 2079 */             getVBool());
/*      */       } 
/* 2081 */       if (hasVString()) {
/* 2082 */         hash = 37 * hash + 9;
/* 2083 */         hash = 53 * hash + getVString().hashCode();
/*      */       } 
/* 2085 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 2086 */       this.memoizedHashCode = hash;
/* 2087 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Scalar parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 2093 */       return (Scalar)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Scalar parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2099 */       return (Scalar)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Scalar parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 2104 */       return (Scalar)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Scalar parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2110 */       return (Scalar)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Scalar parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 2114 */       return (Scalar)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Scalar parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2120 */       return (Scalar)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Scalar parseFrom(InputStream input) throws IOException {
/* 2124 */       return 
/* 2125 */         (Scalar)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Scalar parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2131 */       return 
/* 2132 */         (Scalar)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Scalar parseDelimitedFrom(InputStream input) throws IOException {
/* 2136 */       return 
/* 2137 */         (Scalar)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Scalar parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2143 */       return 
/* 2144 */         (Scalar)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Scalar parseFrom(CodedInputStream input) throws IOException {
/* 2149 */       return 
/* 2150 */         (Scalar)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Scalar parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2156 */       return 
/* 2157 */         (Scalar)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 2161 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 2163 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Scalar prototype) {
/* 2166 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 2170 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 2171 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 2177 */       Builder builder = new Builder(parent);
/* 2178 */       return builder;
/*      */     }
/*      */     public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxDatatypes.ScalarOrBuilder { private int bitField0_;
/*      */       private int type_;
/*      */       private long vSignedInt_;
/*      */       private long vUnsignedInt_;
/*      */       private MysqlxDatatypes.Scalar.Octets vOctets_;
/*      */       private SingleFieldBuilderV3<MysqlxDatatypes.Scalar.Octets, MysqlxDatatypes.Scalar.Octets.Builder, MysqlxDatatypes.Scalar.OctetsOrBuilder> vOctetsBuilder_;
/*      */       private double vDouble_;
/*      */       private float vFloat_;
/*      */       private boolean vBool_;
/*      */       private MysqlxDatatypes.Scalar.String vString_;
/*      */       private SingleFieldBuilderV3<MysqlxDatatypes.Scalar.String, MysqlxDatatypes.Scalar.String.Builder, MysqlxDatatypes.Scalar.StringOrBuilder> vStringBuilder_;
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 2193 */         return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 2199 */         return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_fieldAccessorTable
/* 2200 */           .ensureFieldAccessorsInitialized(MysqlxDatatypes.Scalar.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/* 2432 */         this.type_ = 1; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.type_ = 1; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxDatatypes.Scalar.alwaysUseFieldBuilders) { getVOctetsFieldBuilder(); getVStringFieldBuilder(); }  } public Builder clear() { super.clear(); this.type_ = 1; this.bitField0_ &= 0xFFFFFFFE; this.vSignedInt_ = 0L; this.bitField0_ &= 0xFFFFFFFD; this.vUnsignedInt_ = 0L; this.bitField0_ &= 0xFFFFFFFB; if (this.vOctetsBuilder_ == null) { this.vOctets_ = null; } else { this.vOctetsBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFF7; this.vDouble_ = 0.0D; this.bitField0_ &= 0xFFFFFFEF; this.vFloat_ = 0.0F; this.bitField0_ &= 0xFFFFFFDF; this.vBool_ = false; this.bitField0_ &= 0xFFFFFFBF; if (this.vStringBuilder_ == null) { this.vString_ = null; } else { this.vStringBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFF7F; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Scalar_descriptor; } public MysqlxDatatypes.Scalar getDefaultInstanceForType() { return MysqlxDatatypes.Scalar.getDefaultInstance(); } public MysqlxDatatypes.Scalar build() { MysqlxDatatypes.Scalar result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxDatatypes.Scalar buildPartial() { MysqlxDatatypes.Scalar result = new MysqlxDatatypes.Scalar(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.type_ = this.type_; if ((from_bitField0_ & 0x2) != 0) { result.vSignedInt_ = this.vSignedInt_; to_bitField0_ |= 0x2; }  if ((from_bitField0_ & 0x4) != 0) { result.vUnsignedInt_ = this.vUnsignedInt_; to_bitField0_ |= 0x4; }  if ((from_bitField0_ & 0x8) != 0) { if (this.vOctetsBuilder_ == null) { result.vOctets_ = this.vOctets_; } else { result.vOctets_ = (MysqlxDatatypes.Scalar.Octets)this.vOctetsBuilder_.build(); }  to_bitField0_ |= 0x8; }  if ((from_bitField0_ & 0x10) != 0) { result.vDouble_ = this.vDouble_; to_bitField0_ |= 0x10; }  if ((from_bitField0_ & 0x20) != 0) { result.vFloat_ = this.vFloat_; to_bitField0_ |= 0x20; }  if ((from_bitField0_ & 0x40) != 0) { result.vBool_ = this.vBool_; to_bitField0_ |= 0x40; }  if ((from_bitField0_ & 0x80) != 0) { if (this.vStringBuilder_ == null) { result.vString_ = this.vString_; } else { result.vString_ = (MysqlxDatatypes.Scalar.String)this.vStringBuilder_.build(); }  to_bitField0_ |= 0x80; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); }
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); }
/*      */       public Builder mergeFrom(Message other) { if (other instanceof MysqlxDatatypes.Scalar) return mergeFrom((MysqlxDatatypes.Scalar)other);  super.mergeFrom(other); return this; }
/*      */       public Builder mergeFrom(MysqlxDatatypes.Scalar other) { if (other == MysqlxDatatypes.Scalar.getDefaultInstance()) return this;  if (other.hasType()) setType(other.getType());  if (other.hasVSignedInt()) setVSignedInt(other.getVSignedInt());  if (other.hasVUnsignedInt()) setVUnsignedInt(other.getVUnsignedInt());  if (other.hasVOctets()) mergeVOctets(other.getVOctets());  if (other.hasVDouble()) setVDouble(other.getVDouble());  if (other.hasVFloat()) setVFloat(other.getVFloat());  if (other.hasVBool()) setVBool(other.getVBool());  if (other.hasVString()) mergeVString(other.getVString());  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */       public final boolean isInitialized() { if (!hasType()) return false;  if (hasVOctets() && !getVOctets().isInitialized()) return false;  if (hasVString() && !getVString().isInitialized()) return false;  return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxDatatypes.Scalar parsedMessage = null; try { parsedMessage = (MysqlxDatatypes.Scalar)MysqlxDatatypes.Scalar.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxDatatypes.Scalar)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/* 2438 */       public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Scalar.Type getType() {
/* 2446 */         MysqlxDatatypes.Scalar.Type result = MysqlxDatatypes.Scalar.Type.valueOf(this.type_);
/* 2447 */         return (result == null) ? MysqlxDatatypes.Scalar.Type.V_SINT : result;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setType(MysqlxDatatypes.Scalar.Type value) {
/* 2455 */         if (value == null) {
/* 2456 */           throw new NullPointerException();
/*      */         }
/* 2458 */         this.bitField0_ |= 0x1;
/* 2459 */         this.type_ = value.getNumber();
/* 2460 */         onChanged();
/* 2461 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearType() {
/* 2468 */         this.bitField0_ &= 0xFFFFFFFE;
/* 2469 */         this.type_ = 1;
/* 2470 */         onChanged();
/* 2471 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean hasVSignedInt() {
/* 2480 */         return ((this.bitField0_ & 0x2) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public long getVSignedInt() {
/* 2487 */         return this.vSignedInt_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setVSignedInt(long value) {
/* 2495 */         this.bitField0_ |= 0x2;
/* 2496 */         this.vSignedInt_ = value;
/* 2497 */         onChanged();
/* 2498 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearVSignedInt() {
/* 2505 */         this.bitField0_ &= 0xFFFFFFFD;
/* 2506 */         this.vSignedInt_ = 0L;
/* 2507 */         onChanged();
/* 2508 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean hasVUnsignedInt() {
/* 2517 */         return ((this.bitField0_ & 0x4) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public long getVUnsignedInt() {
/* 2524 */         return this.vUnsignedInt_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setVUnsignedInt(long value) {
/* 2532 */         this.bitField0_ |= 0x4;
/* 2533 */         this.vUnsignedInt_ = value;
/* 2534 */         onChanged();
/* 2535 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearVUnsignedInt() {
/* 2542 */         this.bitField0_ &= 0xFFFFFFFB;
/* 2543 */         this.vUnsignedInt_ = 0L;
/* 2544 */         onChanged();
/* 2545 */         return this;
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
/*      */       public boolean hasVOctets() {
/* 2560 */         return ((this.bitField0_ & 0x8) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Scalar.Octets getVOctets() {
/* 2571 */         if (this.vOctetsBuilder_ == null) {
/* 2572 */           return (this.vOctets_ == null) ? MysqlxDatatypes.Scalar.Octets.getDefaultInstance() : this.vOctets_;
/*      */         }
/* 2574 */         return (MysqlxDatatypes.Scalar.Octets)this.vOctetsBuilder_.getMessage();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setVOctets(MysqlxDatatypes.Scalar.Octets value) {
/* 2585 */         if (this.vOctetsBuilder_ == null) {
/* 2586 */           if (value == null) {
/* 2587 */             throw new NullPointerException();
/*      */           }
/* 2589 */           this.vOctets_ = value;
/* 2590 */           onChanged();
/*      */         } else {
/* 2592 */           this.vOctetsBuilder_.setMessage((AbstractMessage)value);
/*      */         } 
/* 2594 */         this.bitField0_ |= 0x8;
/* 2595 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setVOctets(MysqlxDatatypes.Scalar.Octets.Builder builderForValue) {
/* 2606 */         if (this.vOctetsBuilder_ == null) {
/* 2607 */           this.vOctets_ = builderForValue.build();
/* 2608 */           onChanged();
/*      */         } else {
/* 2610 */           this.vOctetsBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 2612 */         this.bitField0_ |= 0x8;
/* 2613 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeVOctets(MysqlxDatatypes.Scalar.Octets value) {
/* 2623 */         if (this.vOctetsBuilder_ == null) {
/* 2624 */           if ((this.bitField0_ & 0x8) != 0 && this.vOctets_ != null && this.vOctets_ != 
/*      */             
/* 2626 */             MysqlxDatatypes.Scalar.Octets.getDefaultInstance()) {
/* 2627 */             this
/* 2628 */               .vOctets_ = MysqlxDatatypes.Scalar.Octets.newBuilder(this.vOctets_).mergeFrom(value).buildPartial();
/*      */           } else {
/* 2630 */             this.vOctets_ = value;
/*      */           } 
/* 2632 */           onChanged();
/*      */         } else {
/* 2634 */           this.vOctetsBuilder_.mergeFrom((AbstractMessage)value);
/*      */         } 
/* 2636 */         this.bitField0_ |= 0x8;
/* 2637 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearVOctets() {
/* 2647 */         if (this.vOctetsBuilder_ == null) {
/* 2648 */           this.vOctets_ = null;
/* 2649 */           onChanged();
/*      */         } else {
/* 2651 */           this.vOctetsBuilder_.clear();
/*      */         } 
/* 2653 */         this.bitField0_ &= 0xFFFFFFF7;
/* 2654 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Scalar.Octets.Builder getVOctetsBuilder() {
/* 2664 */         this.bitField0_ |= 0x8;
/* 2665 */         onChanged();
/* 2666 */         return (MysqlxDatatypes.Scalar.Octets.Builder)getVOctetsFieldBuilder().getBuilder();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Scalar.OctetsOrBuilder getVOctetsOrBuilder() {
/* 2676 */         if (this.vOctetsBuilder_ != null) {
/* 2677 */           return (MysqlxDatatypes.Scalar.OctetsOrBuilder)this.vOctetsBuilder_.getMessageOrBuilder();
/*      */         }
/* 2679 */         return (this.vOctets_ == null) ? 
/* 2680 */           MysqlxDatatypes.Scalar.Octets.getDefaultInstance() : this.vOctets_;
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
/*      */       private SingleFieldBuilderV3<MysqlxDatatypes.Scalar.Octets, MysqlxDatatypes.Scalar.Octets.Builder, MysqlxDatatypes.Scalar.OctetsOrBuilder> getVOctetsFieldBuilder() {
/* 2693 */         if (this.vOctetsBuilder_ == null) {
/* 2694 */           this
/*      */ 
/*      */ 
/*      */             
/* 2698 */             .vOctetsBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getVOctets(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 2699 */           this.vOctets_ = null;
/*      */         } 
/* 2701 */         return this.vOctetsBuilder_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean hasVDouble() {
/* 2710 */         return ((this.bitField0_ & 0x10) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public double getVDouble() {
/* 2717 */         return this.vDouble_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setVDouble(double value) {
/* 2725 */         this.bitField0_ |= 0x10;
/* 2726 */         this.vDouble_ = value;
/* 2727 */         onChanged();
/* 2728 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearVDouble() {
/* 2735 */         this.bitField0_ &= 0xFFFFFFEF;
/* 2736 */         this.vDouble_ = 0.0D;
/* 2737 */         onChanged();
/* 2738 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean hasVFloat() {
/* 2747 */         return ((this.bitField0_ & 0x20) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public float getVFloat() {
/* 2754 */         return this.vFloat_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setVFloat(float value) {
/* 2762 */         this.bitField0_ |= 0x20;
/* 2763 */         this.vFloat_ = value;
/* 2764 */         onChanged();
/* 2765 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearVFloat() {
/* 2772 */         this.bitField0_ &= 0xFFFFFFDF;
/* 2773 */         this.vFloat_ = 0.0F;
/* 2774 */         onChanged();
/* 2775 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean hasVBool() {
/* 2784 */         return ((this.bitField0_ & 0x40) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean getVBool() {
/* 2791 */         return this.vBool_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setVBool(boolean value) {
/* 2799 */         this.bitField0_ |= 0x40;
/* 2800 */         this.vBool_ = value;
/* 2801 */         onChanged();
/* 2802 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearVBool() {
/* 2809 */         this.bitField0_ &= 0xFFFFFFBF;
/* 2810 */         this.vBool_ = false;
/* 2811 */         onChanged();
/* 2812 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean hasVString() {
/* 2823 */         return ((this.bitField0_ & 0x80) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Scalar.String getVString() {
/* 2830 */         if (this.vStringBuilder_ == null) {
/* 2831 */           return (this.vString_ == null) ? MysqlxDatatypes.Scalar.String.getDefaultInstance() : this.vString_;
/*      */         }
/* 2833 */         return (MysqlxDatatypes.Scalar.String)this.vStringBuilder_.getMessage();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setVString(MysqlxDatatypes.Scalar.String value) {
/* 2840 */         if (this.vStringBuilder_ == null) {
/* 2841 */           if (value == null) {
/* 2842 */             throw new NullPointerException();
/*      */           }
/* 2844 */           this.vString_ = value;
/* 2845 */           onChanged();
/*      */         } else {
/* 2847 */           this.vStringBuilder_.setMessage((AbstractMessage)value);
/*      */         } 
/* 2849 */         this.bitField0_ |= 0x80;
/* 2850 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setVString(MysqlxDatatypes.Scalar.String.Builder builderForValue) {
/* 2857 */         if (this.vStringBuilder_ == null) {
/* 2858 */           this.vString_ = builderForValue.build();
/* 2859 */           onChanged();
/*      */         } else {
/* 2861 */           this.vStringBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 2863 */         this.bitField0_ |= 0x80;
/* 2864 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeVString(MysqlxDatatypes.Scalar.String value) {
/* 2870 */         if (this.vStringBuilder_ == null) {
/* 2871 */           if ((this.bitField0_ & 0x80) != 0 && this.vString_ != null && this.vString_ != 
/*      */             
/* 2873 */             MysqlxDatatypes.Scalar.String.getDefaultInstance()) {
/* 2874 */             this
/* 2875 */               .vString_ = MysqlxDatatypes.Scalar.String.newBuilder(this.vString_).mergeFrom(value).buildPartial();
/*      */           } else {
/* 2877 */             this.vString_ = value;
/*      */           } 
/* 2879 */           onChanged();
/*      */         } else {
/* 2881 */           this.vStringBuilder_.mergeFrom((AbstractMessage)value);
/*      */         } 
/* 2883 */         this.bitField0_ |= 0x80;
/* 2884 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearVString() {
/* 2890 */         if (this.vStringBuilder_ == null) {
/* 2891 */           this.vString_ = null;
/* 2892 */           onChanged();
/*      */         } else {
/* 2894 */           this.vStringBuilder_.clear();
/*      */         } 
/* 2896 */         this.bitField0_ &= 0xFFFFFF7F;
/* 2897 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Scalar.String.Builder getVStringBuilder() {
/* 2903 */         this.bitField0_ |= 0x80;
/* 2904 */         onChanged();
/* 2905 */         return (MysqlxDatatypes.Scalar.String.Builder)getVStringFieldBuilder().getBuilder();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Scalar.StringOrBuilder getVStringOrBuilder() {
/* 2911 */         if (this.vStringBuilder_ != null) {
/* 2912 */           return (MysqlxDatatypes.Scalar.StringOrBuilder)this.vStringBuilder_.getMessageOrBuilder();
/*      */         }
/* 2914 */         return (this.vString_ == null) ? 
/* 2915 */           MysqlxDatatypes.Scalar.String.getDefaultInstance() : this.vString_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private SingleFieldBuilderV3<MysqlxDatatypes.Scalar.String, MysqlxDatatypes.Scalar.String.Builder, MysqlxDatatypes.Scalar.StringOrBuilder> getVStringFieldBuilder() {
/* 2924 */         if (this.vStringBuilder_ == null) {
/* 2925 */           this
/*      */ 
/*      */ 
/*      */             
/* 2929 */             .vStringBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getVString(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 2930 */           this.vString_ = null;
/*      */         } 
/* 2932 */         return this.vStringBuilder_;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 2937 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 2943 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       } }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2953 */     private static final Scalar DEFAULT_INSTANCE = new Scalar();
/*      */ 
/*      */     
/*      */     public static Scalar getDefaultInstance() {
/* 2957 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 2961 */     public static final Parser<Scalar> PARSER = (Parser<Scalar>)new AbstractParser<Scalar>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxDatatypes.Scalar parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 2967 */           return new MysqlxDatatypes.Scalar(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Scalar> parser() {
/* 2972 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Scalar> getParserForType() {
/* 2977 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Scalar getDefaultInstanceForType() {
/* 2982 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     public static interface OctetsOrBuilder
/*      */       extends MessageOrBuilder {
/*      */       boolean hasValue();
/*      */       
/*      */       ByteString getValue();
/*      */       
/*      */       boolean hasContentType();
/*      */       
/*      */       int getContentType();
/*      */     }
/*      */     
/*      */     public static interface StringOrBuilder
/*      */       extends MessageOrBuilder {
/*      */       boolean hasValue();
/*      */       
/*      */       ByteString getValue();
/*      */       
/*      */       boolean hasCollation();
/*      */       
/*      */       long getCollation();
/*      */     }
/*      */   }
/*      */   
/*      */   public static interface ObjectOrBuilder
/*      */     extends MessageOrBuilder {
/*      */     List<MysqlxDatatypes.Object.ObjectField> getFldList();
/*      */     
/*      */     MysqlxDatatypes.Object.ObjectField getFld(int param1Int);
/*      */     
/*      */     int getFldCount();
/*      */     
/*      */     List<? extends MysqlxDatatypes.Object.ObjectFieldOrBuilder> getFldOrBuilderList();
/*      */     
/*      */     MysqlxDatatypes.Object.ObjectFieldOrBuilder getFldOrBuilder(int param1Int);
/*      */   }
/*      */   
/*      */   public static final class Object
/*      */     extends GeneratedMessageV3
/*      */     implements ObjectOrBuilder {
/*      */     private static final long serialVersionUID = 0L;
/*      */     public static final int FLD_FIELD_NUMBER = 1;
/*      */     private List<ObjectField> fld_;
/*      */     private byte memoizedIsInitialized;
/*      */     
/*      */     private Object(GeneratedMessageV3.Builder<?> builder) {
/* 3030 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3975 */       this.memoizedIsInitialized = -1; } private Object() { this.memoizedIsInitialized = -1; this.fld_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Object(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Object(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: if ((mutable_bitField0_ & 0x1) == 0) { this.fld_ = new ArrayList<>(); mutable_bitField0_ |= 0x1; }  this.fld_.add(input.readMessage(ObjectField.PARSER, extensionRegistry)); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x1) != 0) this.fld_ = Collections.unmodifiableList(this.fld_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Object_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Object_fieldAccessorTable.ensureFieldAccessorsInitialized(Object.class, Builder.class); } public static final class ObjectField extends GeneratedMessageV3 implements ObjectFieldOrBuilder {
/*      */       private static final long serialVersionUID = 0L; private int bitField0_; public static final int KEY_FIELD_NUMBER = 1; private volatile Object key_; public static final int VALUE_FIELD_NUMBER = 2; private MysqlxDatatypes.Any value_; private byte memoizedIsInitialized; private ObjectField(GeneratedMessageV3.Builder<?> builder) { super(builder); this.memoizedIsInitialized = -1; } private ObjectField() { this.memoizedIsInitialized = -1; this.key_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new ObjectField(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private ObjectField(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; MysqlxDatatypes.Any.Builder subBuilder; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: bs = input.readBytes(); this.bitField0_ |= 0x1; this.key_ = bs; continue;case 18: subBuilder = null; if ((this.bitField0_ & 0x2) != 0) subBuilder = this.value_.toBuilder();  this.value_ = (MysqlxDatatypes.Any)input.readMessage(MysqlxDatatypes.Any.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.value_); this.value_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x2; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Object_ObjectField_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Object_ObjectField_fieldAccessorTable.ensureFieldAccessorsInitialized(ObjectField.class, Builder.class); } public boolean hasKey() { return ((this.bitField0_ & 0x1) != 0); } public String getKey() { Object ref = this.key_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.key_ = s;  return s; } public ByteString getKeyBytes() { Object ref = this.key_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.key_ = b; return b; }  return (ByteString)ref; } public boolean hasValue() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxDatatypes.Any getValue() { return (this.value_ == null) ? MysqlxDatatypes.Any.getDefaultInstance() : this.value_; } public MysqlxDatatypes.AnyOrBuilder getValueOrBuilder() { return (this.value_ == null) ? MysqlxDatatypes.Any.getDefaultInstance() : this.value_; } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized; if (isInitialized == 1) return true;  if (isInitialized == 0) return false;  if (!hasKey()) { this.memoizedIsInitialized = 0; return false; }  if (!hasValue()) { this.memoizedIsInitialized = 0; return false; }  if (!getValue().isInitialized()) { this.memoizedIsInitialized = 0; return false; }  this.memoizedIsInitialized = 1; return true; } public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) GeneratedMessageV3.writeString(output, 1, this.key_);  if ((this.bitField0_ & 0x2) != 0) output.writeMessage(2, (MessageLite)getValue());  this.unknownFields.writeTo(output); } public int getSerializedSize() { int size = this.memoizedSize; if (size != -1) return size;  size = 0; if ((this.bitField0_ & 0x1) != 0) size += GeneratedMessageV3.computeStringSize(1, this.key_);  if ((this.bitField0_ & 0x2) != 0) size += CodedOutputStream.computeMessageSize(2, (MessageLite)getValue());  size += this.unknownFields.getSerializedSize(); this.memoizedSize = size; return size; } public boolean equals(Object obj) { if (obj == this) return true;  if (!(obj instanceof ObjectField)) return super.equals(obj);  ObjectField other = (ObjectField)obj; if (hasKey() != other.hasKey()) return false;  if (hasKey() && !getKey().equals(other.getKey())) return false;  if (hasValue() != other.hasValue()) return false;  if (hasValue() && !getValue().equals(other.getValue())) return false;  if (!this.unknownFields.equals(other.unknownFields)) return false;  return true; } public int hashCode() { if (this.memoizedHashCode != 0) return this.memoizedHashCode;  int hash = 41; hash = 19 * hash + getDescriptor().hashCode(); if (hasKey()) { hash = 37 * hash + 1; hash = 53 * hash + getKey().hashCode(); }  if (hasValue()) { hash = 37 * hash + 2; hash = 53 * hash + getValue().hashCode(); }  hash = 29 * hash + this.unknownFields.hashCode(); this.memoizedHashCode = hash; return hash; } public static ObjectField parseFrom(ByteBuffer data) throws InvalidProtocolBufferException { return (ObjectField)PARSER.parseFrom(data); } public static ObjectField parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (ObjectField)PARSER.parseFrom(data, extensionRegistry); } public static ObjectField parseFrom(ByteString data) throws InvalidProtocolBufferException { return (ObjectField)PARSER.parseFrom(data); } public static ObjectField parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (ObjectField)PARSER.parseFrom(data, extensionRegistry); } public static ObjectField parseFrom(byte[] data) throws InvalidProtocolBufferException { return (ObjectField)PARSER.parseFrom(data); } public static ObjectField parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (ObjectField)PARSER.parseFrom(data, extensionRegistry); } public static ObjectField parseFrom(InputStream input) throws IOException { return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static ObjectField parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public static ObjectField parseDelimitedFrom(InputStream input) throws IOException { return (ObjectField)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input); } public static ObjectField parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (ObjectField)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry); } public static ObjectField parseFrom(CodedInputStream input) throws IOException { return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static ObjectField parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public Builder newBuilderForType() { return newBuilder(); } public static Builder newBuilder() { return DEFAULT_INSTANCE.toBuilder(); } public static Builder newBuilder(ObjectField prototype) { return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype); } public Builder toBuilder() { return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder()).mergeFrom(this); } protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) { Builder builder = new Builder(parent); return builder; } public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxDatatypes.Object.ObjectFieldOrBuilder {
/*      */         private int bitField0_; private Object key_; private MysqlxDatatypes.Any value_; private SingleFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> valueBuilder_; public static final Descriptors.Descriptor getDescriptor() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Object_ObjectField_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Object_ObjectField_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxDatatypes.Object.ObjectField.class, Builder.class); } private Builder() { this.key_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.key_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxDatatypes.Object.ObjectField.alwaysUseFieldBuilders) getValueFieldBuilder();  } public Builder clear() { super.clear(); this.key_ = ""; this.bitField0_ &= 0xFFFFFFFE; if (this.valueBuilder_ == null) { this.value_ = null; } else { this.valueBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Object_ObjectField_descriptor; } public MysqlxDatatypes.Object.ObjectField getDefaultInstanceForType() { return MysqlxDatatypes.Object.ObjectField.getDefaultInstance(); } public MysqlxDatatypes.Object.ObjectField build() { MysqlxDatatypes.Object.ObjectField result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxDatatypes.Object.ObjectField buildPartial() { MysqlxDatatypes.Object.ObjectField result = new MysqlxDatatypes.Object.ObjectField(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.key_ = this.key_; if ((from_bitField0_ & 0x2) != 0) { if (this.valueBuilder_ == null) { result.value_ = this.value_; } else { result.value_ = (MysqlxDatatypes.Any)this.valueBuilder_.build(); }  to_bitField0_ |= 0x2; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxDatatypes.Object.ObjectField) return mergeFrom((MysqlxDatatypes.Object.ObjectField)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxDatatypes.Object.ObjectField other) { if (other == MysqlxDatatypes.Object.ObjectField.getDefaultInstance()) return this;  if (other.hasKey()) { this.bitField0_ |= 0x1; this.key_ = other.key_; onChanged(); }  if (other.hasValue()) mergeValue(other.getValue());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasKey()) return false;  if (!hasValue()) return false;  if (!getValue().isInitialized()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxDatatypes.Object.ObjectField parsedMessage = null; try { parsedMessage = (MysqlxDatatypes.Object.ObjectField)MysqlxDatatypes.Object.ObjectField.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxDatatypes.Object.ObjectField)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasKey() { return ((this.bitField0_ & 0x1) != 0); } public String getKey() { Object ref = this.key_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.key_ = s;  return s; }  return (String)ref; } public ByteString getKeyBytes() { Object ref = this.key_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.key_ = b; return b; }  return (ByteString)ref; } public Builder setKey(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.key_ = value; onChanged(); return this; } public Builder clearKey() { this.bitField0_ &= 0xFFFFFFFE; this.key_ = MysqlxDatatypes.Object.ObjectField.getDefaultInstance().getKey(); onChanged(); return this; } public Builder setKeyBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.key_ = value; onChanged(); return this; } public boolean hasValue() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxDatatypes.Any getValue() { if (this.valueBuilder_ == null) return (this.value_ == null) ? MysqlxDatatypes.Any.getDefaultInstance() : this.value_;  return (MysqlxDatatypes.Any)this.valueBuilder_.getMessage(); } public Builder setValue(MysqlxDatatypes.Any value) { if (this.valueBuilder_ == null) { if (value == null) throw new NullPointerException();  this.value_ = value; onChanged(); } else { this.valueBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x2; return this; } public Builder setValue(MysqlxDatatypes.Any.Builder builderForValue) { if (this.valueBuilder_ == null) { this.value_ = builderForValue.build(); onChanged(); } else { this.valueBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x2; return this; } public Builder mergeValue(MysqlxDatatypes.Any value) { if (this.valueBuilder_ == null) { if ((this.bitField0_ & 0x2) != 0 && this.value_ != null && this.value_ != MysqlxDatatypes.Any.getDefaultInstance()) { this.value_ = MysqlxDatatypes.Any.newBuilder(this.value_).mergeFrom(value).buildPartial(); } else { this.value_ = value; }  onChanged(); } else { this.valueBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x2; return this; } public Builder clearValue() { if (this.valueBuilder_ == null) { this.value_ = null; onChanged(); } else { this.valueBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; return this; } public MysqlxDatatypes.Any.Builder getValueBuilder() { this.bitField0_ |= 0x2; onChanged(); return (MysqlxDatatypes.Any.Builder)getValueFieldBuilder().getBuilder(); } public MysqlxDatatypes.AnyOrBuilder getValueOrBuilder() { if (this.valueBuilder_ != null) return (MysqlxDatatypes.AnyOrBuilder)this.valueBuilder_.getMessageOrBuilder();  return (this.value_ == null) ? MysqlxDatatypes.Any.getDefaultInstance() : this.value_; } private SingleFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> getValueFieldBuilder() { if (this.valueBuilder_ == null) { this.valueBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getValue(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.value_ = null; }  return this.valueBuilder_; } public final Builder setUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.setUnknownFields(unknownFields); } public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.mergeUnknownFields(unknownFields); } } private static final ObjectField DEFAULT_INSTANCE = new ObjectField(); public static ObjectField getDefaultInstance() { return DEFAULT_INSTANCE; } @Deprecated public static final Parser<ObjectField> PARSER = (Parser<ObjectField>)new AbstractParser<ObjectField>() { public MysqlxDatatypes.Object.ObjectField parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return new MysqlxDatatypes.Object.ObjectField(input, extensionRegistry); } }
/* 3978 */       ; public static Parser<ObjectField> parser() { return PARSER; } public Parser<ObjectField> getParserForType() { return PARSER; } public ObjectField getDefaultInstanceForType() { return DEFAULT_INSTANCE; } } public List<ObjectField> getFldList() { return this.fld_; } public List<? extends ObjectFieldOrBuilder> getFldOrBuilderList() { return (List)this.fld_; } public int getFldCount() { return this.fld_.size(); } public ObjectField getFld(int index) { return this.fld_.get(index); } public ObjectFieldOrBuilder getFldOrBuilder(int index) { return this.fld_.get(index); } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 3979 */       if (isInitialized == 1) return true; 
/* 3980 */       if (isInitialized == 0) return false;
/*      */       
/* 3982 */       for (int i = 0; i < getFldCount(); i++) {
/* 3983 */         if (!getFld(i).isInitialized()) {
/* 3984 */           this.memoizedIsInitialized = 0;
/* 3985 */           return false;
/*      */         } 
/*      */       } 
/* 3988 */       this.memoizedIsInitialized = 1;
/* 3989 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 3995 */       for (int i = 0; i < this.fld_.size(); i++) {
/* 3996 */         output.writeMessage(1, (MessageLite)this.fld_.get(i));
/*      */       }
/* 3998 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 4003 */       int size = this.memoizedSize;
/* 4004 */       if (size != -1) return size;
/*      */       
/* 4006 */       size = 0;
/* 4007 */       for (int i = 0; i < this.fld_.size(); i++) {
/* 4008 */         size += 
/* 4009 */           CodedOutputStream.computeMessageSize(1, (MessageLite)this.fld_.get(i));
/*      */       }
/* 4011 */       size += this.unknownFields.getSerializedSize();
/* 4012 */       this.memoizedSize = size;
/* 4013 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 4018 */       if (obj == this) {
/* 4019 */         return true;
/*      */       }
/* 4021 */       if (!(obj instanceof Object)) {
/* 4022 */         return super.equals(obj);
/*      */       }
/* 4024 */       Object other = (Object)obj;
/*      */ 
/*      */       
/* 4027 */       if (!getFldList().equals(other.getFldList())) return false; 
/* 4028 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 4029 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 4034 */       if (this.memoizedHashCode != 0) {
/* 4035 */         return this.memoizedHashCode;
/*      */       }
/* 4037 */       int hash = 41;
/* 4038 */       hash = 19 * hash + getDescriptor().hashCode();
/* 4039 */       if (getFldCount() > 0) {
/* 4040 */         hash = 37 * hash + 1;
/* 4041 */         hash = 53 * hash + getFldList().hashCode();
/*      */       } 
/* 4043 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 4044 */       this.memoizedHashCode = hash;
/* 4045 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Object parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 4051 */       return (Object)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Object parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 4057 */       return (Object)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Object parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 4062 */       return (Object)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Object parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 4068 */       return (Object)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Object parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 4072 */       return (Object)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Object parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 4078 */       return (Object)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Object parseFrom(InputStream input) throws IOException {
/* 4082 */       return 
/* 4083 */         (Object)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Object parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 4089 */       return 
/* 4090 */         (Object)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Object parseDelimitedFrom(InputStream input) throws IOException {
/* 4094 */       return 
/* 4095 */         (Object)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Object parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 4101 */       return 
/* 4102 */         (Object)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Object parseFrom(CodedInputStream input) throws IOException {
/* 4107 */       return 
/* 4108 */         (Object)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Object parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 4114 */       return 
/* 4115 */         (Object)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 4119 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 4121 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Object prototype) {
/* 4124 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 4128 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 4129 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 4135 */       Builder builder = new Builder(parent);
/* 4136 */       return builder;
/*      */     }
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxDatatypes.ObjectOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */       
/*      */       private List<MysqlxDatatypes.Object.ObjectField> fld_;
/*      */       
/*      */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Object.ObjectField, MysqlxDatatypes.Object.ObjectField.Builder, MysqlxDatatypes.Object.ObjectFieldOrBuilder> fldBuilder_;
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 4152 */         return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Object_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 4158 */         return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Object_fieldAccessorTable
/* 4159 */           .ensureFieldAccessorsInitialized(MysqlxDatatypes.Object.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/* 4333 */         this
/* 4334 */           .fld_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.fld_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxDatatypes.Object.alwaysUseFieldBuilders) getFldFieldBuilder();  } public Builder clear() { super.clear(); if (this.fldBuilder_ == null) { this.fld_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; } else { this.fldBuilder_.clear(); }  return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Object_descriptor; } public MysqlxDatatypes.Object getDefaultInstanceForType() { return MysqlxDatatypes.Object.getDefaultInstance(); } public MysqlxDatatypes.Object build() { MysqlxDatatypes.Object result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxDatatypes.Object buildPartial() { MysqlxDatatypes.Object result = new MysqlxDatatypes.Object(this); int from_bitField0_ = this.bitField0_; if (this.fldBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0) { this.fld_ = Collections.unmodifiableList(this.fld_); this.bitField0_ &= 0xFFFFFFFE; }  result.fld_ = this.fld_; } else { result.fld_ = this.fldBuilder_.build(); }  onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxDatatypes.Object) return mergeFrom((MysqlxDatatypes.Object)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxDatatypes.Object other) { if (other == MysqlxDatatypes.Object.getDefaultInstance()) return this;  if (this.fldBuilder_ == null) { if (!other.fld_.isEmpty()) { if (this.fld_.isEmpty()) { this.fld_ = other.fld_; this.bitField0_ &= 0xFFFFFFFE; } else { ensureFldIsMutable(); this.fld_.addAll(other.fld_); }  onChanged(); }  } else if (!other.fld_.isEmpty()) { if (this.fldBuilder_.isEmpty()) { this.fldBuilder_.dispose(); this.fldBuilder_ = null; this.fld_ = other.fld_; this.bitField0_ &= 0xFFFFFFFE; this.fldBuilder_ = MysqlxDatatypes.Object.alwaysUseFieldBuilders ? getFldFieldBuilder() : null; } else { this.fldBuilder_.addAllMessages(other.fld_); }  }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { for (int i = 0; i < getFldCount(); i++) { if (!getFld(i).isInitialized()) return false;  }  return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxDatatypes.Object parsedMessage = null; try { parsedMessage = (MysqlxDatatypes.Object)MysqlxDatatypes.Object.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxDatatypes.Object)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/* 4336 */       private void ensureFldIsMutable() { if ((this.bitField0_ & 0x1) == 0) {
/* 4337 */           this.fld_ = new ArrayList<>(this.fld_);
/* 4338 */           this.bitField0_ |= 0x1;
/*      */         }  }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<MysqlxDatatypes.Object.ObjectField> getFldList() {
/* 4349 */         if (this.fldBuilder_ == null) {
/* 4350 */           return Collections.unmodifiableList(this.fld_);
/*      */         }
/* 4352 */         return this.fldBuilder_.getMessageList();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int getFldCount() {
/* 4359 */         if (this.fldBuilder_ == null) {
/* 4360 */           return this.fld_.size();
/*      */         }
/* 4362 */         return this.fldBuilder_.getCount();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Object.ObjectField getFld(int index) {
/* 4369 */         if (this.fldBuilder_ == null) {
/* 4370 */           return this.fld_.get(index);
/*      */         }
/* 4372 */         return (MysqlxDatatypes.Object.ObjectField)this.fldBuilder_.getMessage(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setFld(int index, MysqlxDatatypes.Object.ObjectField value) {
/* 4380 */         if (this.fldBuilder_ == null) {
/* 4381 */           if (value == null) {
/* 4382 */             throw new NullPointerException();
/*      */           }
/* 4384 */           ensureFldIsMutable();
/* 4385 */           this.fld_.set(index, value);
/* 4386 */           onChanged();
/*      */         } else {
/* 4388 */           this.fldBuilder_.setMessage(index, (AbstractMessage)value);
/*      */         } 
/* 4390 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setFld(int index, MysqlxDatatypes.Object.ObjectField.Builder builderForValue) {
/* 4397 */         if (this.fldBuilder_ == null) {
/* 4398 */           ensureFldIsMutable();
/* 4399 */           this.fld_.set(index, builderForValue.build());
/* 4400 */           onChanged();
/*      */         } else {
/* 4402 */           this.fldBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 4404 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addFld(MysqlxDatatypes.Object.ObjectField value) {
/* 4410 */         if (this.fldBuilder_ == null) {
/* 4411 */           if (value == null) {
/* 4412 */             throw new NullPointerException();
/*      */           }
/* 4414 */           ensureFldIsMutable();
/* 4415 */           this.fld_.add(value);
/* 4416 */           onChanged();
/*      */         } else {
/* 4418 */           this.fldBuilder_.addMessage((AbstractMessage)value);
/*      */         } 
/* 4420 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addFld(int index, MysqlxDatatypes.Object.ObjectField value) {
/* 4427 */         if (this.fldBuilder_ == null) {
/* 4428 */           if (value == null) {
/* 4429 */             throw new NullPointerException();
/*      */           }
/* 4431 */           ensureFldIsMutable();
/* 4432 */           this.fld_.add(index, value);
/* 4433 */           onChanged();
/*      */         } else {
/* 4435 */           this.fldBuilder_.addMessage(index, (AbstractMessage)value);
/*      */         } 
/* 4437 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addFld(MysqlxDatatypes.Object.ObjectField.Builder builderForValue) {
/* 4444 */         if (this.fldBuilder_ == null) {
/* 4445 */           ensureFldIsMutable();
/* 4446 */           this.fld_.add(builderForValue.build());
/* 4447 */           onChanged();
/*      */         } else {
/* 4449 */           this.fldBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 4451 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addFld(int index, MysqlxDatatypes.Object.ObjectField.Builder builderForValue) {
/* 4458 */         if (this.fldBuilder_ == null) {
/* 4459 */           ensureFldIsMutable();
/* 4460 */           this.fld_.add(index, builderForValue.build());
/* 4461 */           onChanged();
/*      */         } else {
/* 4463 */           this.fldBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 4465 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addAllFld(Iterable<? extends MysqlxDatatypes.Object.ObjectField> values) {
/* 4472 */         if (this.fldBuilder_ == null) {
/* 4473 */           ensureFldIsMutable();
/* 4474 */           AbstractMessageLite.Builder.addAll(values, this.fld_);
/*      */           
/* 4476 */           onChanged();
/*      */         } else {
/* 4478 */           this.fldBuilder_.addAllMessages(values);
/*      */         } 
/* 4480 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearFld() {
/* 4486 */         if (this.fldBuilder_ == null) {
/* 4487 */           this.fld_ = Collections.emptyList();
/* 4488 */           this.bitField0_ &= 0xFFFFFFFE;
/* 4489 */           onChanged();
/*      */         } else {
/* 4491 */           this.fldBuilder_.clear();
/*      */         } 
/* 4493 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder removeFld(int index) {
/* 4499 */         if (this.fldBuilder_ == null) {
/* 4500 */           ensureFldIsMutable();
/* 4501 */           this.fld_.remove(index);
/* 4502 */           onChanged();
/*      */         } else {
/* 4504 */           this.fldBuilder_.remove(index);
/*      */         } 
/* 4506 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Object.ObjectField.Builder getFldBuilder(int index) {
/* 4513 */         return (MysqlxDatatypes.Object.ObjectField.Builder)getFldFieldBuilder().getBuilder(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Object.ObjectFieldOrBuilder getFldOrBuilder(int index) {
/* 4520 */         if (this.fldBuilder_ == null)
/* 4521 */           return this.fld_.get(index); 
/* 4522 */         return (MysqlxDatatypes.Object.ObjectFieldOrBuilder)this.fldBuilder_.getMessageOrBuilder(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<? extends MysqlxDatatypes.Object.ObjectFieldOrBuilder> getFldOrBuilderList() {
/* 4530 */         if (this.fldBuilder_ != null) {
/* 4531 */           return this.fldBuilder_.getMessageOrBuilderList();
/*      */         }
/* 4533 */         return Collections.unmodifiableList((List)this.fld_);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Object.ObjectField.Builder addFldBuilder() {
/* 4540 */         return (MysqlxDatatypes.Object.ObjectField.Builder)getFldFieldBuilder().addBuilder(
/* 4541 */             (AbstractMessage)MysqlxDatatypes.Object.ObjectField.getDefaultInstance());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Object.ObjectField.Builder addFldBuilder(int index) {
/* 4548 */         return (MysqlxDatatypes.Object.ObjectField.Builder)getFldFieldBuilder().addBuilder(index, 
/* 4549 */             (AbstractMessage)MysqlxDatatypes.Object.ObjectField.getDefaultInstance());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<MysqlxDatatypes.Object.ObjectField.Builder> getFldBuilderList() {
/* 4556 */         return getFldFieldBuilder().getBuilderList();
/*      */       }
/*      */ 
/*      */       
/*      */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Object.ObjectField, MysqlxDatatypes.Object.ObjectField.Builder, MysqlxDatatypes.Object.ObjectFieldOrBuilder> getFldFieldBuilder() {
/* 4561 */         if (this.fldBuilder_ == null) {
/* 4562 */           this
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 4567 */             .fldBuilder_ = new RepeatedFieldBuilderV3(this.fld_, ((this.bitField0_ & 0x1) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 4568 */           this.fld_ = null;
/*      */         } 
/* 4570 */         return this.fldBuilder_;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 4575 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 4581 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4591 */     private static final Object DEFAULT_INSTANCE = new Object();
/*      */ 
/*      */     
/*      */     public static Object getDefaultInstance() {
/* 4595 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 4599 */     public static final Parser<Object> PARSER = (Parser<Object>)new AbstractParser<Object>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxDatatypes.Object parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 4605 */           return new MysqlxDatatypes.Object(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Object> parser() {
/* 4610 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Object> getParserForType() {
/* 4615 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Object getDefaultInstanceForType() {
/* 4620 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */ 
/*      */     
/*      */     public static interface ObjectFieldOrBuilder
/*      */       extends MessageOrBuilder
/*      */     {
/*      */       boolean hasKey();
/*      */ 
/*      */       
/*      */       String getKey();
/*      */ 
/*      */       
/*      */       ByteString getKeyBytes();
/*      */       
/*      */       boolean hasValue();
/*      */       
/*      */       MysqlxDatatypes.Any getValue();
/*      */       
/*      */       MysqlxDatatypes.AnyOrBuilder getValueOrBuilder();
/*      */     }
/*      */   }
/*      */   
/*      */   public static interface ArrayOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     List<MysqlxDatatypes.Any> getValueList();
/*      */     
/*      */     MysqlxDatatypes.Any getValue(int param1Int);
/*      */     
/*      */     int getValueCount();
/*      */     
/*      */     List<? extends MysqlxDatatypes.AnyOrBuilder> getValueOrBuilderList();
/*      */     
/*      */     MysqlxDatatypes.AnyOrBuilder getValueOrBuilder(int param1Int);
/*      */   }
/*      */   
/*      */   public static final class Array
/*      */     extends GeneratedMessageV3
/*      */     implements ArrayOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */     public static final int VALUE_FIELD_NUMBER = 1;
/*      */     private List<MysqlxDatatypes.Any> value_;
/*      */     private byte memoizedIsInitialized;
/*      */     
/*      */     private Array(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 4668 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 4784 */       this.memoizedIsInitialized = -1; } private Array() { this.memoizedIsInitialized = -1; this.value_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Array(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Array(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: if ((mutable_bitField0_ & 0x1) == 0) { this.value_ = new ArrayList<>(); mutable_bitField0_ |= 0x1; }  this.value_.add(input.readMessage(MysqlxDatatypes.Any.PARSER, extensionRegistry)); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x1) != 0) this.value_ = Collections.unmodifiableList(this.value_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Array_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Array_fieldAccessorTable.ensureFieldAccessorsInitialized(Array.class, Builder.class); } public List<MysqlxDatatypes.Any> getValueList() { return this.value_; } public List<? extends MysqlxDatatypes.AnyOrBuilder> getValueOrBuilderList() { return (List)this.value_; } public int getValueCount() { return this.value_.size(); }
/*      */     public MysqlxDatatypes.Any getValue(int index) { return this.value_.get(index); }
/*      */     public MysqlxDatatypes.AnyOrBuilder getValueOrBuilder(int index) { return this.value_.get(index); }
/* 4787 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 4788 */       if (isInitialized == 1) return true; 
/* 4789 */       if (isInitialized == 0) return false;
/*      */       
/* 4791 */       for (int i = 0; i < getValueCount(); i++) {
/* 4792 */         if (!getValue(i).isInitialized()) {
/* 4793 */           this.memoizedIsInitialized = 0;
/* 4794 */           return false;
/*      */         } 
/*      */       } 
/* 4797 */       this.memoizedIsInitialized = 1;
/* 4798 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 4804 */       for (int i = 0; i < this.value_.size(); i++) {
/* 4805 */         output.writeMessage(1, (MessageLite)this.value_.get(i));
/*      */       }
/* 4807 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 4812 */       int size = this.memoizedSize;
/* 4813 */       if (size != -1) return size;
/*      */       
/* 4815 */       size = 0;
/* 4816 */       for (int i = 0; i < this.value_.size(); i++) {
/* 4817 */         size += 
/* 4818 */           CodedOutputStream.computeMessageSize(1, (MessageLite)this.value_.get(i));
/*      */       }
/* 4820 */       size += this.unknownFields.getSerializedSize();
/* 4821 */       this.memoizedSize = size;
/* 4822 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 4827 */       if (obj == this) {
/* 4828 */         return true;
/*      */       }
/* 4830 */       if (!(obj instanceof Array)) {
/* 4831 */         return super.equals(obj);
/*      */       }
/* 4833 */       Array other = (Array)obj;
/*      */ 
/*      */       
/* 4836 */       if (!getValueList().equals(other.getValueList())) return false; 
/* 4837 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 4838 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 4843 */       if (this.memoizedHashCode != 0) {
/* 4844 */         return this.memoizedHashCode;
/*      */       }
/* 4846 */       int hash = 41;
/* 4847 */       hash = 19 * hash + getDescriptor().hashCode();
/* 4848 */       if (getValueCount() > 0) {
/* 4849 */         hash = 37 * hash + 1;
/* 4850 */         hash = 53 * hash + getValueList().hashCode();
/*      */       } 
/* 4852 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 4853 */       this.memoizedHashCode = hash;
/* 4854 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Array parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 4860 */       return (Array)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Array parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 4866 */       return (Array)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Array parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 4871 */       return (Array)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Array parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 4877 */       return (Array)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Array parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 4881 */       return (Array)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Array parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 4887 */       return (Array)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Array parseFrom(InputStream input) throws IOException {
/* 4891 */       return 
/* 4892 */         (Array)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Array parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 4898 */       return 
/* 4899 */         (Array)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Array parseDelimitedFrom(InputStream input) throws IOException {
/* 4903 */       return 
/* 4904 */         (Array)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Array parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 4910 */       return 
/* 4911 */         (Array)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Array parseFrom(CodedInputStream input) throws IOException {
/* 4916 */       return 
/* 4917 */         (Array)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Array parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 4923 */       return 
/* 4924 */         (Array)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 4928 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 4930 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Array prototype) {
/* 4933 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 4937 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 4938 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 4944 */       Builder builder = new Builder(parent);
/* 4945 */       return builder;
/*      */     }
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxDatatypes.ArrayOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */       
/*      */       private List<MysqlxDatatypes.Any> value_;
/*      */       
/*      */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> valueBuilder_;
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 4961 */         return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Array_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 4967 */         return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Array_fieldAccessorTable
/* 4968 */           .ensureFieldAccessorsInitialized(MysqlxDatatypes.Array.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/* 5142 */         this
/* 5143 */           .value_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.value_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxDatatypes.Array.alwaysUseFieldBuilders) getValueFieldBuilder();  } public Builder clear() { super.clear(); if (this.valueBuilder_ == null) { this.value_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; } else { this.valueBuilder_.clear(); }  return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Array_descriptor; } public MysqlxDatatypes.Array getDefaultInstanceForType() { return MysqlxDatatypes.Array.getDefaultInstance(); } public MysqlxDatatypes.Array build() { MysqlxDatatypes.Array result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxDatatypes.Array buildPartial() { MysqlxDatatypes.Array result = new MysqlxDatatypes.Array(this); int from_bitField0_ = this.bitField0_; if (this.valueBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0) { this.value_ = Collections.unmodifiableList(this.value_); this.bitField0_ &= 0xFFFFFFFE; }  result.value_ = this.value_; } else { result.value_ = this.valueBuilder_.build(); }  onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxDatatypes.Array) return mergeFrom((MysqlxDatatypes.Array)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxDatatypes.Array other) { if (other == MysqlxDatatypes.Array.getDefaultInstance()) return this;  if (this.valueBuilder_ == null) { if (!other.value_.isEmpty()) { if (this.value_.isEmpty()) { this.value_ = other.value_; this.bitField0_ &= 0xFFFFFFFE; } else { ensureValueIsMutable(); this.value_.addAll(other.value_); }  onChanged(); }  } else if (!other.value_.isEmpty()) { if (this.valueBuilder_.isEmpty()) { this.valueBuilder_.dispose(); this.valueBuilder_ = null; this.value_ = other.value_; this.bitField0_ &= 0xFFFFFFFE; this.valueBuilder_ = MysqlxDatatypes.Array.alwaysUseFieldBuilders ? getValueFieldBuilder() : null; } else { this.valueBuilder_.addAllMessages(other.value_); }  }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { for (int i = 0; i < getValueCount(); i++) { if (!getValue(i).isInitialized()) return false;  }  return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxDatatypes.Array parsedMessage = null; try { parsedMessage = (MysqlxDatatypes.Array)MysqlxDatatypes.Array.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxDatatypes.Array)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/* 5145 */       private void ensureValueIsMutable() { if ((this.bitField0_ & 0x1) == 0) {
/* 5146 */           this.value_ = new ArrayList<>(this.value_);
/* 5147 */           this.bitField0_ |= 0x1;
/*      */         }  }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<MysqlxDatatypes.Any> getValueList() {
/* 5158 */         if (this.valueBuilder_ == null) {
/* 5159 */           return Collections.unmodifiableList(this.value_);
/*      */         }
/* 5161 */         return this.valueBuilder_.getMessageList();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int getValueCount() {
/* 5168 */         if (this.valueBuilder_ == null) {
/* 5169 */           return this.value_.size();
/*      */         }
/* 5171 */         return this.valueBuilder_.getCount();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Any getValue(int index) {
/* 5178 */         if (this.valueBuilder_ == null) {
/* 5179 */           return this.value_.get(index);
/*      */         }
/* 5181 */         return (MysqlxDatatypes.Any)this.valueBuilder_.getMessage(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setValue(int index, MysqlxDatatypes.Any value) {
/* 5189 */         if (this.valueBuilder_ == null) {
/* 5190 */           if (value == null) {
/* 5191 */             throw new NullPointerException();
/*      */           }
/* 5193 */           ensureValueIsMutable();
/* 5194 */           this.value_.set(index, value);
/* 5195 */           onChanged();
/*      */         } else {
/* 5197 */           this.valueBuilder_.setMessage(index, (AbstractMessage)value);
/*      */         } 
/* 5199 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setValue(int index, MysqlxDatatypes.Any.Builder builderForValue) {
/* 5206 */         if (this.valueBuilder_ == null) {
/* 5207 */           ensureValueIsMutable();
/* 5208 */           this.value_.set(index, builderForValue.build());
/* 5209 */           onChanged();
/*      */         } else {
/* 5211 */           this.valueBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 5213 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addValue(MysqlxDatatypes.Any value) {
/* 5219 */         if (this.valueBuilder_ == null) {
/* 5220 */           if (value == null) {
/* 5221 */             throw new NullPointerException();
/*      */           }
/* 5223 */           ensureValueIsMutable();
/* 5224 */           this.value_.add(value);
/* 5225 */           onChanged();
/*      */         } else {
/* 5227 */           this.valueBuilder_.addMessage((AbstractMessage)value);
/*      */         } 
/* 5229 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addValue(int index, MysqlxDatatypes.Any value) {
/* 5236 */         if (this.valueBuilder_ == null) {
/* 5237 */           if (value == null) {
/* 5238 */             throw new NullPointerException();
/*      */           }
/* 5240 */           ensureValueIsMutable();
/* 5241 */           this.value_.add(index, value);
/* 5242 */           onChanged();
/*      */         } else {
/* 5244 */           this.valueBuilder_.addMessage(index, (AbstractMessage)value);
/*      */         } 
/* 5246 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addValue(MysqlxDatatypes.Any.Builder builderForValue) {
/* 5253 */         if (this.valueBuilder_ == null) {
/* 5254 */           ensureValueIsMutable();
/* 5255 */           this.value_.add(builderForValue.build());
/* 5256 */           onChanged();
/*      */         } else {
/* 5258 */           this.valueBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 5260 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addValue(int index, MysqlxDatatypes.Any.Builder builderForValue) {
/* 5267 */         if (this.valueBuilder_ == null) {
/* 5268 */           ensureValueIsMutable();
/* 5269 */           this.value_.add(index, builderForValue.build());
/* 5270 */           onChanged();
/*      */         } else {
/* 5272 */           this.valueBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 5274 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addAllValue(Iterable<? extends MysqlxDatatypes.Any> values) {
/* 5281 */         if (this.valueBuilder_ == null) {
/* 5282 */           ensureValueIsMutable();
/* 5283 */           AbstractMessageLite.Builder.addAll(values, this.value_);
/*      */           
/* 5285 */           onChanged();
/*      */         } else {
/* 5287 */           this.valueBuilder_.addAllMessages(values);
/*      */         } 
/* 5289 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearValue() {
/* 5295 */         if (this.valueBuilder_ == null) {
/* 5296 */           this.value_ = Collections.emptyList();
/* 5297 */           this.bitField0_ &= 0xFFFFFFFE;
/* 5298 */           onChanged();
/*      */         } else {
/* 5300 */           this.valueBuilder_.clear();
/*      */         } 
/* 5302 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder removeValue(int index) {
/* 5308 */         if (this.valueBuilder_ == null) {
/* 5309 */           ensureValueIsMutable();
/* 5310 */           this.value_.remove(index);
/* 5311 */           onChanged();
/*      */         } else {
/* 5313 */           this.valueBuilder_.remove(index);
/*      */         } 
/* 5315 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Any.Builder getValueBuilder(int index) {
/* 5322 */         return (MysqlxDatatypes.Any.Builder)getValueFieldBuilder().getBuilder(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.AnyOrBuilder getValueOrBuilder(int index) {
/* 5329 */         if (this.valueBuilder_ == null)
/* 5330 */           return this.value_.get(index); 
/* 5331 */         return (MysqlxDatatypes.AnyOrBuilder)this.valueBuilder_.getMessageOrBuilder(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<? extends MysqlxDatatypes.AnyOrBuilder> getValueOrBuilderList() {
/* 5339 */         if (this.valueBuilder_ != null) {
/* 5340 */           return this.valueBuilder_.getMessageOrBuilderList();
/*      */         }
/* 5342 */         return Collections.unmodifiableList((List)this.value_);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Any.Builder addValueBuilder() {
/* 5349 */         return (MysqlxDatatypes.Any.Builder)getValueFieldBuilder().addBuilder(
/* 5350 */             (AbstractMessage)MysqlxDatatypes.Any.getDefaultInstance());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Any.Builder addValueBuilder(int index) {
/* 5357 */         return (MysqlxDatatypes.Any.Builder)getValueFieldBuilder().addBuilder(index, 
/* 5358 */             (AbstractMessage)MysqlxDatatypes.Any.getDefaultInstance());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<MysqlxDatatypes.Any.Builder> getValueBuilderList() {
/* 5365 */         return getValueFieldBuilder().getBuilderList();
/*      */       }
/*      */ 
/*      */       
/*      */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> getValueFieldBuilder() {
/* 5370 */         if (this.valueBuilder_ == null) {
/* 5371 */           this
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 5376 */             .valueBuilder_ = new RepeatedFieldBuilderV3(this.value_, ((this.bitField0_ & 0x1) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 5377 */           this.value_ = null;
/*      */         } 
/* 5379 */         return this.valueBuilder_;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 5384 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 5390 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5400 */     private static final Array DEFAULT_INSTANCE = new Array();
/*      */ 
/*      */     
/*      */     public static Array getDefaultInstance() {
/* 5404 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 5408 */     public static final Parser<Array> PARSER = (Parser<Array>)new AbstractParser<Array>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxDatatypes.Array parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 5414 */           return new MysqlxDatatypes.Array(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Array> parser() {
/* 5419 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Array> getParserForType() {
/* 5424 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Array getDefaultInstanceForType() {
/* 5429 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface AnyOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasType();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.Any.Type getType();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasScalar();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.Scalar getScalar();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.ScalarOrBuilder getScalarOrBuilder();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasObj();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.Object getObj();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.ObjectOrBuilder getObjOrBuilder();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasArray();
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.Array getArray();
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.ArrayOrBuilder getArrayOrBuilder();
/*      */   }
/*      */ 
/*      */   
/*      */   public static final class Any
/*      */     extends GeneratedMessageV3
/*      */     implements AnyOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     private int bitField0_;
/*      */     
/*      */     public static final int TYPE_FIELD_NUMBER = 1;
/*      */     
/*      */     private int type_;
/*      */     
/*      */     public static final int SCALAR_FIELD_NUMBER = 2;
/*      */     
/*      */     private MysqlxDatatypes.Scalar scalar_;
/*      */     
/*      */     public static final int OBJ_FIELD_NUMBER = 3;
/*      */     
/*      */     private MysqlxDatatypes.Object obj_;
/*      */     
/*      */     public static final int ARRAY_FIELD_NUMBER = 4;
/*      */     
/*      */     private MysqlxDatatypes.Array array_;
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */     
/*      */     private Any(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 5509 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 5823 */       this.memoizedIsInitialized = -1; } private Any() { this.memoizedIsInitialized = -1; this.type_ = 1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Any(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Any(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int rawValue; MysqlxDatatypes.Scalar.Builder builder1; MysqlxDatatypes.Object.Builder builder; MysqlxDatatypes.Array.Builder subBuilder; Type value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: rawValue = input.readEnum(); value = Type.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(1, rawValue); continue; }  this.bitField0_ |= 0x1; this.type_ = rawValue; continue;case 18: builder1 = null; if ((this.bitField0_ & 0x2) != 0) builder1 = this.scalar_.toBuilder();  this.scalar_ = (MysqlxDatatypes.Scalar)input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry); if (builder1 != null) { builder1.mergeFrom(this.scalar_); this.scalar_ = builder1.buildPartial(); }  this.bitField0_ |= 0x2; continue;case 26: builder = null; if ((this.bitField0_ & 0x4) != 0) builder = this.obj_.toBuilder();  this.obj_ = (MysqlxDatatypes.Object)input.readMessage(MysqlxDatatypes.Object.PARSER, extensionRegistry); if (builder != null) { builder.mergeFrom(this.obj_); this.obj_ = builder.buildPartial(); }  this.bitField0_ |= 0x4; continue;case 34: subBuilder = null; if ((this.bitField0_ & 0x8) != 0) subBuilder = this.array_.toBuilder();  this.array_ = (MysqlxDatatypes.Array)input.readMessage(MysqlxDatatypes.Array.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.array_); this.array_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x8; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Any_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Any_fieldAccessorTable.ensureFieldAccessorsInitialized(Any.class, Builder.class); } public enum Type implements ProtocolMessageEnum {
/*      */       SCALAR(1), OBJECT(2), ARRAY(3); public static final int SCALAR_VALUE = 1; public static final int OBJECT_VALUE = 2; public static final int ARRAY_VALUE = 3; private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() { public MysqlxDatatypes.Any.Type findValueByNumber(int number) { return MysqlxDatatypes.Any.Type.forNumber(number); } }
/*      */       ; private static final Type[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Type forNumber(int value) { switch (value) { case 1: return SCALAR;case 2: return OBJECT;case 3: return ARRAY; }  return null; } public static Internal.EnumLiteMap<Type> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxDatatypes.Any.getDescriptor().getEnumTypes().get(0); } Type(int value) { this.value = value; } } public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); } public Type getType() { Type result = Type.valueOf(this.type_); return (result == null) ? Type.SCALAR : result; } public boolean hasScalar() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxDatatypes.Scalar getScalar() { return (this.scalar_ == null) ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.scalar_; } public MysqlxDatatypes.ScalarOrBuilder getScalarOrBuilder() { return (this.scalar_ == null) ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.scalar_; } public boolean hasObj() { return ((this.bitField0_ & 0x4) != 0); } public MysqlxDatatypes.Object getObj() { return (this.obj_ == null) ? MysqlxDatatypes.Object.getDefaultInstance() : this.obj_; } public MysqlxDatatypes.ObjectOrBuilder getObjOrBuilder() { return (this.obj_ == null) ? MysqlxDatatypes.Object.getDefaultInstance() : this.obj_; } public boolean hasArray() { return ((this.bitField0_ & 0x8) != 0); } public MysqlxDatatypes.Array getArray() { return (this.array_ == null) ? MysqlxDatatypes.Array.getDefaultInstance() : this.array_; } public MysqlxDatatypes.ArrayOrBuilder getArrayOrBuilder() { return (this.array_ == null) ? MysqlxDatatypes.Array.getDefaultInstance() : this.array_; }
/* 5826 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 5827 */       if (isInitialized == 1) return true; 
/* 5828 */       if (isInitialized == 0) return false;
/*      */       
/* 5830 */       if (!hasType()) {
/* 5831 */         this.memoizedIsInitialized = 0;
/* 5832 */         return false;
/*      */       } 
/* 5834 */       if (hasScalar() && 
/* 5835 */         !getScalar().isInitialized()) {
/* 5836 */         this.memoizedIsInitialized = 0;
/* 5837 */         return false;
/*      */       } 
/*      */       
/* 5840 */       if (hasObj() && 
/* 5841 */         !getObj().isInitialized()) {
/* 5842 */         this.memoizedIsInitialized = 0;
/* 5843 */         return false;
/*      */       } 
/*      */       
/* 5846 */       if (hasArray() && 
/* 5847 */         !getArray().isInitialized()) {
/* 5848 */         this.memoizedIsInitialized = 0;
/* 5849 */         return false;
/*      */       } 
/*      */       
/* 5852 */       this.memoizedIsInitialized = 1;
/* 5853 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 5859 */       if ((this.bitField0_ & 0x1) != 0) {
/* 5860 */         output.writeEnum(1, this.type_);
/*      */       }
/* 5862 */       if ((this.bitField0_ & 0x2) != 0) {
/* 5863 */         output.writeMessage(2, (MessageLite)getScalar());
/*      */       }
/* 5865 */       if ((this.bitField0_ & 0x4) != 0) {
/* 5866 */         output.writeMessage(3, (MessageLite)getObj());
/*      */       }
/* 5868 */       if ((this.bitField0_ & 0x8) != 0) {
/* 5869 */         output.writeMessage(4, (MessageLite)getArray());
/*      */       }
/* 5871 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 5876 */       int size = this.memoizedSize;
/* 5877 */       if (size != -1) return size;
/*      */       
/* 5879 */       size = 0;
/* 5880 */       if ((this.bitField0_ & 0x1) != 0) {
/* 5881 */         size += 
/* 5882 */           CodedOutputStream.computeEnumSize(1, this.type_);
/*      */       }
/* 5884 */       if ((this.bitField0_ & 0x2) != 0) {
/* 5885 */         size += 
/* 5886 */           CodedOutputStream.computeMessageSize(2, (MessageLite)getScalar());
/*      */       }
/* 5888 */       if ((this.bitField0_ & 0x4) != 0) {
/* 5889 */         size += 
/* 5890 */           CodedOutputStream.computeMessageSize(3, (MessageLite)getObj());
/*      */       }
/* 5892 */       if ((this.bitField0_ & 0x8) != 0) {
/* 5893 */         size += 
/* 5894 */           CodedOutputStream.computeMessageSize(4, (MessageLite)getArray());
/*      */       }
/* 5896 */       size += this.unknownFields.getSerializedSize();
/* 5897 */       this.memoizedSize = size;
/* 5898 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 5903 */       if (obj == this) {
/* 5904 */         return true;
/*      */       }
/* 5906 */       if (!(obj instanceof Any)) {
/* 5907 */         return super.equals(obj);
/*      */       }
/* 5909 */       Any other = (Any)obj;
/*      */       
/* 5911 */       if (hasType() != other.hasType()) return false; 
/* 5912 */       if (hasType() && 
/* 5913 */         this.type_ != other.type_) return false;
/*      */       
/* 5915 */       if (hasScalar() != other.hasScalar()) return false; 
/* 5916 */       if (hasScalar() && 
/*      */         
/* 5918 */         !getScalar().equals(other.getScalar())) return false;
/*      */       
/* 5920 */       if (hasObj() != other.hasObj()) return false; 
/* 5921 */       if (hasObj() && 
/*      */         
/* 5923 */         !getObj().equals(other.getObj())) return false;
/*      */       
/* 5925 */       if (hasArray() != other.hasArray()) return false; 
/* 5926 */       if (hasArray() && 
/*      */         
/* 5928 */         !getArray().equals(other.getArray())) return false;
/*      */       
/* 5930 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 5931 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 5936 */       if (this.memoizedHashCode != 0) {
/* 5937 */         return this.memoizedHashCode;
/*      */       }
/* 5939 */       int hash = 41;
/* 5940 */       hash = 19 * hash + getDescriptor().hashCode();
/* 5941 */       if (hasType()) {
/* 5942 */         hash = 37 * hash + 1;
/* 5943 */         hash = 53 * hash + this.type_;
/*      */       } 
/* 5945 */       if (hasScalar()) {
/* 5946 */         hash = 37 * hash + 2;
/* 5947 */         hash = 53 * hash + getScalar().hashCode();
/*      */       } 
/* 5949 */       if (hasObj()) {
/* 5950 */         hash = 37 * hash + 3;
/* 5951 */         hash = 53 * hash + getObj().hashCode();
/*      */       } 
/* 5953 */       if (hasArray()) {
/* 5954 */         hash = 37 * hash + 4;
/* 5955 */         hash = 53 * hash + getArray().hashCode();
/*      */       } 
/* 5957 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 5958 */       this.memoizedHashCode = hash;
/* 5959 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Any parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 5965 */       return (Any)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Any parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 5971 */       return (Any)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Any parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 5976 */       return (Any)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Any parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 5982 */       return (Any)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Any parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 5986 */       return (Any)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Any parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 5992 */       return (Any)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Any parseFrom(InputStream input) throws IOException {
/* 5996 */       return 
/* 5997 */         (Any)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Any parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 6003 */       return 
/* 6004 */         (Any)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Any parseDelimitedFrom(InputStream input) throws IOException {
/* 6008 */       return 
/* 6009 */         (Any)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Any parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 6015 */       return 
/* 6016 */         (Any)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Any parseFrom(CodedInputStream input) throws IOException {
/* 6021 */       return 
/* 6022 */         (Any)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Any parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 6028 */       return 
/* 6029 */         (Any)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 6033 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 6035 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Any prototype) {
/* 6038 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 6042 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 6043 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 6049 */       Builder builder = new Builder(parent);
/* 6050 */       return builder;
/*      */     }
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxDatatypes.AnyOrBuilder {
/*      */       private int bitField0_;
/*      */       private int type_;
/*      */       private MysqlxDatatypes.Scalar scalar_;
/*      */       private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> scalarBuilder_;
/*      */       private MysqlxDatatypes.Object obj_;
/*      */       private SingleFieldBuilderV3<MysqlxDatatypes.Object, MysqlxDatatypes.Object.Builder, MysqlxDatatypes.ObjectOrBuilder> objBuilder_;
/*      */       private MysqlxDatatypes.Array array_;
/*      */       private SingleFieldBuilderV3<MysqlxDatatypes.Array, MysqlxDatatypes.Array.Builder, MysqlxDatatypes.ArrayOrBuilder> arrayBuilder_;
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 6066 */         return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Any_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 6072 */         return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Any_fieldAccessorTable
/* 6073 */           .ensureFieldAccessorsInitialized(MysqlxDatatypes.Any.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/* 6283 */         this.type_ = 1; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.type_ = 1; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxDatatypes.Any.alwaysUseFieldBuilders) { getScalarFieldBuilder(); getObjFieldBuilder(); getArrayFieldBuilder(); }  } public Builder clear() { super.clear(); this.type_ = 1; this.bitField0_ &= 0xFFFFFFFE; if (this.scalarBuilder_ == null) { this.scalar_ = null; } else { this.scalarBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; if (this.objBuilder_ == null) { this.obj_ = null; } else { this.objBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFB; if (this.arrayBuilder_ == null) { this.array_ = null; } else { this.arrayBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFF7; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxDatatypes.internal_static_Mysqlx_Datatypes_Any_descriptor; } public MysqlxDatatypes.Any getDefaultInstanceForType() { return MysqlxDatatypes.Any.getDefaultInstance(); } public MysqlxDatatypes.Any build() { MysqlxDatatypes.Any result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxDatatypes.Any buildPartial() { MysqlxDatatypes.Any result = new MysqlxDatatypes.Any(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.type_ = this.type_; if ((from_bitField0_ & 0x2) != 0) { if (this.scalarBuilder_ == null) { result.scalar_ = this.scalar_; } else { result.scalar_ = (MysqlxDatatypes.Scalar)this.scalarBuilder_.build(); }  to_bitField0_ |= 0x2; }  if ((from_bitField0_ & 0x4) != 0) { if (this.objBuilder_ == null) { result.obj_ = this.obj_; } else { result.obj_ = (MysqlxDatatypes.Object)this.objBuilder_.build(); }  to_bitField0_ |= 0x4; }  if ((from_bitField0_ & 0x8) != 0) { if (this.arrayBuilder_ == null) { result.array_ = this.array_; } else { result.array_ = (MysqlxDatatypes.Array)this.arrayBuilder_.build(); }  to_bitField0_ |= 0x8; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); }
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); }
/*      */       public Builder mergeFrom(Message other) { if (other instanceof MysqlxDatatypes.Any) return mergeFrom((MysqlxDatatypes.Any)other);  super.mergeFrom(other); return this; }
/*      */       public Builder mergeFrom(MysqlxDatatypes.Any other) { if (other == MysqlxDatatypes.Any.getDefaultInstance()) return this;  if (other.hasType()) setType(other.getType());  if (other.hasScalar()) mergeScalar(other.getScalar());  if (other.hasObj()) mergeObj(other.getObj());  if (other.hasArray()) mergeArray(other.getArray());  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */       public final boolean isInitialized() { if (!hasType()) return false;  if (hasScalar() && !getScalar().isInitialized()) return false;  if (hasObj() && !getObj().isInitialized()) return false;  if (hasArray() && !getArray().isInitialized()) return false;  return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxDatatypes.Any parsedMessage = null; try { parsedMessage = (MysqlxDatatypes.Any)MysqlxDatatypes.Any.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxDatatypes.Any)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/* 6289 */       public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Any.Type getType() {
/* 6297 */         MysqlxDatatypes.Any.Type result = MysqlxDatatypes.Any.Type.valueOf(this.type_);
/* 6298 */         return (result == null) ? MysqlxDatatypes.Any.Type.SCALAR : result;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setType(MysqlxDatatypes.Any.Type value) {
/* 6306 */         if (value == null) {
/* 6307 */           throw new NullPointerException();
/*      */         }
/* 6309 */         this.bitField0_ |= 0x1;
/* 6310 */         this.type_ = value.getNumber();
/* 6311 */         onChanged();
/* 6312 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearType() {
/* 6319 */         this.bitField0_ &= 0xFFFFFFFE;
/* 6320 */         this.type_ = 1;
/* 6321 */         onChanged();
/* 6322 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean hasScalar() {
/* 6333 */         return ((this.bitField0_ & 0x2) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Scalar getScalar() {
/* 6340 */         if (this.scalarBuilder_ == null) {
/* 6341 */           return (this.scalar_ == null) ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.scalar_;
/*      */         }
/* 6343 */         return (MysqlxDatatypes.Scalar)this.scalarBuilder_.getMessage();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setScalar(MysqlxDatatypes.Scalar value) {
/* 6350 */         if (this.scalarBuilder_ == null) {
/* 6351 */           if (value == null) {
/* 6352 */             throw new NullPointerException();
/*      */           }
/* 6354 */           this.scalar_ = value;
/* 6355 */           onChanged();
/*      */         } else {
/* 6357 */           this.scalarBuilder_.setMessage((AbstractMessage)value);
/*      */         } 
/* 6359 */         this.bitField0_ |= 0x2;
/* 6360 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setScalar(MysqlxDatatypes.Scalar.Builder builderForValue) {
/* 6367 */         if (this.scalarBuilder_ == null) {
/* 6368 */           this.scalar_ = builderForValue.build();
/* 6369 */           onChanged();
/*      */         } else {
/* 6371 */           this.scalarBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 6373 */         this.bitField0_ |= 0x2;
/* 6374 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeScalar(MysqlxDatatypes.Scalar value) {
/* 6380 */         if (this.scalarBuilder_ == null) {
/* 6381 */           if ((this.bitField0_ & 0x2) != 0 && this.scalar_ != null && this.scalar_ != 
/*      */             
/* 6383 */             MysqlxDatatypes.Scalar.getDefaultInstance()) {
/* 6384 */             this
/* 6385 */               .scalar_ = MysqlxDatatypes.Scalar.newBuilder(this.scalar_).mergeFrom(value).buildPartial();
/*      */           } else {
/* 6387 */             this.scalar_ = value;
/*      */           } 
/* 6389 */           onChanged();
/*      */         } else {
/* 6391 */           this.scalarBuilder_.mergeFrom((AbstractMessage)value);
/*      */         } 
/* 6393 */         this.bitField0_ |= 0x2;
/* 6394 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearScalar() {
/* 6400 */         if (this.scalarBuilder_ == null) {
/* 6401 */           this.scalar_ = null;
/* 6402 */           onChanged();
/*      */         } else {
/* 6404 */           this.scalarBuilder_.clear();
/*      */         } 
/* 6406 */         this.bitField0_ &= 0xFFFFFFFD;
/* 6407 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Scalar.Builder getScalarBuilder() {
/* 6413 */         this.bitField0_ |= 0x2;
/* 6414 */         onChanged();
/* 6415 */         return (MysqlxDatatypes.Scalar.Builder)getScalarFieldBuilder().getBuilder();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.ScalarOrBuilder getScalarOrBuilder() {
/* 6421 */         if (this.scalarBuilder_ != null) {
/* 6422 */           return (MysqlxDatatypes.ScalarOrBuilder)this.scalarBuilder_.getMessageOrBuilder();
/*      */         }
/* 6424 */         return (this.scalar_ == null) ? 
/* 6425 */           MysqlxDatatypes.Scalar.getDefaultInstance() : this.scalar_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getScalarFieldBuilder() {
/* 6434 */         if (this.scalarBuilder_ == null) {
/* 6435 */           this
/*      */ 
/*      */ 
/*      */             
/* 6439 */             .scalarBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getScalar(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 6440 */           this.scalar_ = null;
/*      */         } 
/* 6442 */         return this.scalarBuilder_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean hasObj() {
/* 6453 */         return ((this.bitField0_ & 0x4) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Object getObj() {
/* 6460 */         if (this.objBuilder_ == null) {
/* 6461 */           return (this.obj_ == null) ? MysqlxDatatypes.Object.getDefaultInstance() : this.obj_;
/*      */         }
/* 6463 */         return (MysqlxDatatypes.Object)this.objBuilder_.getMessage();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setObj(MysqlxDatatypes.Object value) {
/* 6470 */         if (this.objBuilder_ == null) {
/* 6471 */           if (value == null) {
/* 6472 */             throw new NullPointerException();
/*      */           }
/* 6474 */           this.obj_ = value;
/* 6475 */           onChanged();
/*      */         } else {
/* 6477 */           this.objBuilder_.setMessage((AbstractMessage)value);
/*      */         } 
/* 6479 */         this.bitField0_ |= 0x4;
/* 6480 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setObj(MysqlxDatatypes.Object.Builder builderForValue) {
/* 6487 */         if (this.objBuilder_ == null) {
/* 6488 */           this.obj_ = builderForValue.build();
/* 6489 */           onChanged();
/*      */         } else {
/* 6491 */           this.objBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 6493 */         this.bitField0_ |= 0x4;
/* 6494 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeObj(MysqlxDatatypes.Object value) {
/* 6500 */         if (this.objBuilder_ == null) {
/* 6501 */           if ((this.bitField0_ & 0x4) != 0 && this.obj_ != null && this.obj_ != 
/*      */             
/* 6503 */             MysqlxDatatypes.Object.getDefaultInstance()) {
/* 6504 */             this
/* 6505 */               .obj_ = MysqlxDatatypes.Object.newBuilder(this.obj_).mergeFrom(value).buildPartial();
/*      */           } else {
/* 6507 */             this.obj_ = value;
/*      */           } 
/* 6509 */           onChanged();
/*      */         } else {
/* 6511 */           this.objBuilder_.mergeFrom((AbstractMessage)value);
/*      */         } 
/* 6513 */         this.bitField0_ |= 0x4;
/* 6514 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearObj() {
/* 6520 */         if (this.objBuilder_ == null) {
/* 6521 */           this.obj_ = null;
/* 6522 */           onChanged();
/*      */         } else {
/* 6524 */           this.objBuilder_.clear();
/*      */         } 
/* 6526 */         this.bitField0_ &= 0xFFFFFFFB;
/* 6527 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Object.Builder getObjBuilder() {
/* 6533 */         this.bitField0_ |= 0x4;
/* 6534 */         onChanged();
/* 6535 */         return (MysqlxDatatypes.Object.Builder)getObjFieldBuilder().getBuilder();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.ObjectOrBuilder getObjOrBuilder() {
/* 6541 */         if (this.objBuilder_ != null) {
/* 6542 */           return (MysqlxDatatypes.ObjectOrBuilder)this.objBuilder_.getMessageOrBuilder();
/*      */         }
/* 6544 */         return (this.obj_ == null) ? 
/* 6545 */           MysqlxDatatypes.Object.getDefaultInstance() : this.obj_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private SingleFieldBuilderV3<MysqlxDatatypes.Object, MysqlxDatatypes.Object.Builder, MysqlxDatatypes.ObjectOrBuilder> getObjFieldBuilder() {
/* 6554 */         if (this.objBuilder_ == null) {
/* 6555 */           this
/*      */ 
/*      */ 
/*      */             
/* 6559 */             .objBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getObj(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 6560 */           this.obj_ = null;
/*      */         } 
/* 6562 */         return this.objBuilder_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean hasArray() {
/* 6573 */         return ((this.bitField0_ & 0x8) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Array getArray() {
/* 6580 */         if (this.arrayBuilder_ == null) {
/* 6581 */           return (this.array_ == null) ? MysqlxDatatypes.Array.getDefaultInstance() : this.array_;
/*      */         }
/* 6583 */         return (MysqlxDatatypes.Array)this.arrayBuilder_.getMessage();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setArray(MysqlxDatatypes.Array value) {
/* 6590 */         if (this.arrayBuilder_ == null) {
/* 6591 */           if (value == null) {
/* 6592 */             throw new NullPointerException();
/*      */           }
/* 6594 */           this.array_ = value;
/* 6595 */           onChanged();
/*      */         } else {
/* 6597 */           this.arrayBuilder_.setMessage((AbstractMessage)value);
/*      */         } 
/* 6599 */         this.bitField0_ |= 0x8;
/* 6600 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setArray(MysqlxDatatypes.Array.Builder builderForValue) {
/* 6607 */         if (this.arrayBuilder_ == null) {
/* 6608 */           this.array_ = builderForValue.build();
/* 6609 */           onChanged();
/*      */         } else {
/* 6611 */           this.arrayBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 6613 */         this.bitField0_ |= 0x8;
/* 6614 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeArray(MysqlxDatatypes.Array value) {
/* 6620 */         if (this.arrayBuilder_ == null) {
/* 6621 */           if ((this.bitField0_ & 0x8) != 0 && this.array_ != null && this.array_ != 
/*      */             
/* 6623 */             MysqlxDatatypes.Array.getDefaultInstance()) {
/* 6624 */             this
/* 6625 */               .array_ = MysqlxDatatypes.Array.newBuilder(this.array_).mergeFrom(value).buildPartial();
/*      */           } else {
/* 6627 */             this.array_ = value;
/*      */           } 
/* 6629 */           onChanged();
/*      */         } else {
/* 6631 */           this.arrayBuilder_.mergeFrom((AbstractMessage)value);
/*      */         } 
/* 6633 */         this.bitField0_ |= 0x8;
/* 6634 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearArray() {
/* 6640 */         if (this.arrayBuilder_ == null) {
/* 6641 */           this.array_ = null;
/* 6642 */           onChanged();
/*      */         } else {
/* 6644 */           this.arrayBuilder_.clear();
/*      */         } 
/* 6646 */         this.bitField0_ &= 0xFFFFFFF7;
/* 6647 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Array.Builder getArrayBuilder() {
/* 6653 */         this.bitField0_ |= 0x8;
/* 6654 */         onChanged();
/* 6655 */         return (MysqlxDatatypes.Array.Builder)getArrayFieldBuilder().getBuilder();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.ArrayOrBuilder getArrayOrBuilder() {
/* 6661 */         if (this.arrayBuilder_ != null) {
/* 6662 */           return (MysqlxDatatypes.ArrayOrBuilder)this.arrayBuilder_.getMessageOrBuilder();
/*      */         }
/* 6664 */         return (this.array_ == null) ? 
/* 6665 */           MysqlxDatatypes.Array.getDefaultInstance() : this.array_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private SingleFieldBuilderV3<MysqlxDatatypes.Array, MysqlxDatatypes.Array.Builder, MysqlxDatatypes.ArrayOrBuilder> getArrayFieldBuilder() {
/* 6674 */         if (this.arrayBuilder_ == null) {
/* 6675 */           this
/*      */ 
/*      */ 
/*      */             
/* 6679 */             .arrayBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getArray(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 6680 */           this.array_ = null;
/*      */         } 
/* 6682 */         return this.arrayBuilder_;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 6687 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 6693 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6703 */     private static final Any DEFAULT_INSTANCE = new Any();
/*      */ 
/*      */     
/*      */     public static Any getDefaultInstance() {
/* 6707 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 6711 */     public static final Parser<Any> PARSER = (Parser<Any>)new AbstractParser<Any>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxDatatypes.Any parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 6717 */           return new MysqlxDatatypes.Any(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Any> parser() {
/* 6722 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Any> getParserForType() {
/* 6727 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Any getDefaultInstanceForType() {
/* 6732 */       return DEFAULT_INSTANCE;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Descriptors.FileDescriptor getDescriptor() {
/* 6775 */     return descriptor;
/*      */   }
/*      */ 
/*      */   
/*      */   static {
/* 6780 */     String[] descriptorData = { "\n\026mysqlx_datatypes.proto\022\020Mysqlx.Datatypes\"Æ\003\n\006Scalar\022+\n\004type\030\001 \002(\0162\035.Mysqlx.Datatypes.Scalar.Type\022\024\n\fv_signed_int\030\002 \001(\022\022\026\n\016v_unsigned_int\030\003 \001(\004\0221\n\bv_octets\030\005 \001(\0132\037.Mysqlx.Datatypes.Scalar.Octets\022\020\n\bv_double\030\006 \001(\001\022\017\n\007v_float\030\007 \001(\002\022\016\n\006v_bool\030\b \001(\b\0221\n\bv_string\030\t \001(\0132\037.Mysqlx.Datatypes.Scalar.String\032*\n\006String\022\r\n\005value\030\001 \002(\f\022\021\n\tcollation\030\002 \001(\004\032-\n\006Octets\022\r\n\005value\030\001 \002(\f\022\024\n\fcontent_type\030\002 \001(\r\"m\n\004Type\022\n\n\006V_SINT\020\001\022\n\n\006V_UINT\020\002\022\n\n\006V_NULL\020\003\022\f\n\bV_OCTETS\020\004\022\f\n\bV_DOUBLE\020\005\022\013\n\007V_FLOAT\020\006\022\n\n\006V_BOOL\020\007\022\f\n\bV_STRING\020\b\"}\n\006Object\0221\n\003fld\030\001 \003(\0132$.Mysqlx.Datatypes.Object.ObjectField\032@\n\013ObjectField\022\013\n\003key\030\001 \002(\t\022$\n\005value\030\002 \002(\0132\025.Mysqlx.Datatypes.Any\"-\n\005Array\022$\n\005value\030\001 \003(\0132\025.Mysqlx.Datatypes.Any\"Ó\001\n\003Any\022(\n\004type\030\001 \002(\0162\032.Mysqlx.Datatypes.Any.Type\022(\n\006scalar\030\002 \001(\0132\030.Mysqlx.Datatypes.Scalar\022%\n\003obj\030\003 \001(\0132\030.Mysqlx.Datatypes.Object\022&\n\005array\030\004 \001(\0132\027.Mysqlx.Datatypes.Array\")\n\004Type\022\n\n\006SCALAR\020\001\022\n\n\006OBJECT\020\002\022\t\n\005ARRAY\020\003B\031\n\027com.mysql.cj.x.protobuf" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6806 */     descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
/*      */ 
/*      */ 
/*      */     
/* 6810 */     internal_static_Mysqlx_Datatypes_Scalar_descriptor = getDescriptor().getMessageTypes().get(0);
/* 6811 */     internal_static_Mysqlx_Datatypes_Scalar_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Datatypes_Scalar_descriptor, new String[] { "Type", "VSignedInt", "VUnsignedInt", "VOctets", "VDouble", "VFloat", "VBool", "VString" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6816 */     internal_static_Mysqlx_Datatypes_Scalar_String_descriptor = internal_static_Mysqlx_Datatypes_Scalar_descriptor.getNestedTypes().get(0);
/* 6817 */     internal_static_Mysqlx_Datatypes_Scalar_String_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Datatypes_Scalar_String_descriptor, new String[] { "Value", "Collation" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6822 */     internal_static_Mysqlx_Datatypes_Scalar_Octets_descriptor = internal_static_Mysqlx_Datatypes_Scalar_descriptor.getNestedTypes().get(1);
/* 6823 */     internal_static_Mysqlx_Datatypes_Scalar_Octets_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Datatypes_Scalar_Octets_descriptor, new String[] { "Value", "ContentType" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6828 */     internal_static_Mysqlx_Datatypes_Object_descriptor = getDescriptor().getMessageTypes().get(1);
/* 6829 */     internal_static_Mysqlx_Datatypes_Object_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Datatypes_Object_descriptor, new String[] { "Fld" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6834 */     internal_static_Mysqlx_Datatypes_Object_ObjectField_descriptor = internal_static_Mysqlx_Datatypes_Object_descriptor.getNestedTypes().get(0);
/* 6835 */     internal_static_Mysqlx_Datatypes_Object_ObjectField_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Datatypes_Object_ObjectField_descriptor, new String[] { "Key", "Value" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6840 */     internal_static_Mysqlx_Datatypes_Array_descriptor = getDescriptor().getMessageTypes().get(2);
/* 6841 */     internal_static_Mysqlx_Datatypes_Array_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Datatypes_Array_descriptor, new String[] { "Value" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 6846 */     internal_static_Mysqlx_Datatypes_Any_descriptor = getDescriptor().getMessageTypes().get(3);
/* 6847 */     internal_static_Mysqlx_Datatypes_Any_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Datatypes_Any_descriptor, new String[] { "Type", "Scalar", "Obj", "Array" });
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\x\protobuf\MysqlxDatatypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */