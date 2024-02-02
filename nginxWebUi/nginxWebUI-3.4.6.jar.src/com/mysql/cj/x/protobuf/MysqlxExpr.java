/*       */ package com.mysql.cj.x.protobuf;
/*       */ import com.google.protobuf.AbstractMessage;
/*       */ import com.google.protobuf.AbstractMessageLite;
/*       */ import com.google.protobuf.ByteString;
/*       */ import com.google.protobuf.CodedInputStream;
/*       */ import com.google.protobuf.CodedOutputStream;
/*       */ import com.google.protobuf.Descriptors;
/*       */ import com.google.protobuf.ExtensionRegistryLite;
/*       */ import com.google.protobuf.GeneratedMessageV3;
/*       */ import com.google.protobuf.InvalidProtocolBufferException;
/*       */ import com.google.protobuf.Message;
/*       */ import com.google.protobuf.MessageLite;
/*       */ import com.google.protobuf.UnknownFieldSet;
/*       */ import java.io.IOException;
/*       */ import java.io.InputStream;
/*       */ import java.nio.ByteBuffer;
/*       */ import java.util.Collections;
/*       */ import java.util.List;
/*       */ 
/*       */ public final class MysqlxExpr {
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Expr_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Expr_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Identifier_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Identifier_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_DocumentPathItem_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_DocumentPathItem_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_ColumnIdentifier_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_FunctionCall_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_FunctionCall_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Operator_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Operator_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Object_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Object_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Object_ObjectField_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Object_ObjectField_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Array_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Array_fieldAccessorTable;
/*       */   private static Descriptors.FileDescriptor descriptor;
/*       */   
/*       */   public static void registerAllExtensions(ExtensionRegistryLite registry) {}
/*       */   
/*       */   public static void registerAllExtensions(ExtensionRegistry registry) {
/*    44 */     registerAllExtensions((ExtensionRegistryLite)registry);
/*       */   }
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface ExprOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasType();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Expr.Type getType();
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasIdentifier();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ColumnIdentifier getIdentifier();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ColumnIdentifierOrBuilder getIdentifierOrBuilder();
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasVariable();
/*       */ 
/*       */ 
/*       */     
/*       */     String getVariable();
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getVariableBytes();
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasLiteral();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxDatatypes.Scalar getLiteral();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxDatatypes.ScalarOrBuilder getLiteralOrBuilder();
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasFunctionCall();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.FunctionCall getFunctionCall();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.FunctionCallOrBuilder getFunctionCallOrBuilder();
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasOperator();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Operator getOperator();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.OperatorOrBuilder getOperatorOrBuilder();
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasPosition();
/*       */ 
/*       */ 
/*       */     
/*       */     int getPosition();
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasObject();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Object getObject();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ObjectOrBuilder getObjectOrBuilder();
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasArray();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Array getArray();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ArrayOrBuilder getArrayOrBuilder();
/*       */   }
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class Expr
/*       */     extends GeneratedMessageV3
/*       */     implements ExprOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */     
/*       */     public static final int TYPE_FIELD_NUMBER = 1;
/*       */ 
/*       */     
/*       */     private int type_;
/*       */ 
/*       */     
/*       */     public static final int IDENTIFIER_FIELD_NUMBER = 2;
/*       */ 
/*       */     
/*       */     private MysqlxExpr.ColumnIdentifier identifier_;
/*       */ 
/*       */     
/*       */     public static final int VARIABLE_FIELD_NUMBER = 3;
/*       */ 
/*       */     
/*       */     private volatile Object variable_;
/*       */ 
/*       */     
/*       */     public static final int LITERAL_FIELD_NUMBER = 4;
/*       */ 
/*       */     
/*       */     private MysqlxDatatypes.Scalar literal_;
/*       */     
/*       */     public static final int FUNCTION_CALL_FIELD_NUMBER = 5;
/*       */     
/*       */     private MysqlxExpr.FunctionCall functionCall_;
/*       */     
/*       */     public static final int OPERATOR_FIELD_NUMBER = 6;
/*       */     
/*       */     private MysqlxExpr.Operator operator_;
/*       */     
/*       */     public static final int POSITION_FIELD_NUMBER = 7;
/*       */     
/*       */     private int position_;
/*       */     
/*       */     public static final int OBJECT_FIELD_NUMBER = 8;
/*       */     
/*       */     private MysqlxExpr.Object object_;
/*       */     
/*       */     public static final int ARRAY_FIELD_NUMBER = 9;
/*       */     
/*       */     private MysqlxExpr.Array array_;
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */     
/*       */     private Expr(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/*   211 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*   752 */       this.memoizedIsInitialized = -1; } private Expr() { this.memoizedIsInitialized = -1; this.type_ = 1; this.variable_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Expr(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Expr(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int rawValue; MysqlxExpr.ColumnIdentifier.Builder builder4; ByteString bs; MysqlxDatatypes.Scalar.Builder builder3; MysqlxExpr.FunctionCall.Builder builder2; MysqlxExpr.Operator.Builder builder1; MysqlxExpr.Object.Builder builder; MysqlxExpr.Array.Builder subBuilder; Type value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: rawValue = input.readEnum(); value = Type.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(1, rawValue); continue; }  this.bitField0_ |= 0x1; this.type_ = rawValue; continue;case 18: builder4 = null; if ((this.bitField0_ & 0x2) != 0) builder4 = this.identifier_.toBuilder();  this.identifier_ = (MysqlxExpr.ColumnIdentifier)input.readMessage(MysqlxExpr.ColumnIdentifier.PARSER, extensionRegistry); if (builder4 != null) { builder4.mergeFrom(this.identifier_); this.identifier_ = builder4.buildPartial(); }  this.bitField0_ |= 0x2; continue;case 26: bs = input.readBytes(); this.bitField0_ |= 0x4; this.variable_ = bs; continue;case 34: builder3 = null; if ((this.bitField0_ & 0x8) != 0) builder3 = this.literal_.toBuilder();  this.literal_ = (MysqlxDatatypes.Scalar)input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry); if (builder3 != null) { builder3.mergeFrom(this.literal_); this.literal_ = builder3.buildPartial(); }  this.bitField0_ |= 0x8; continue;case 42: builder2 = null; if ((this.bitField0_ & 0x10) != 0) builder2 = this.functionCall_.toBuilder();  this.functionCall_ = (MysqlxExpr.FunctionCall)input.readMessage(MysqlxExpr.FunctionCall.PARSER, extensionRegistry); if (builder2 != null) { builder2.mergeFrom(this.functionCall_); this.functionCall_ = builder2.buildPartial(); }  this.bitField0_ |= 0x10; continue;case 50: builder1 = null; if ((this.bitField0_ & 0x20) != 0) builder1 = this.operator_.toBuilder();  this.operator_ = (MysqlxExpr.Operator)input.readMessage(MysqlxExpr.Operator.PARSER, extensionRegistry); if (builder1 != null) { builder1.mergeFrom(this.operator_); this.operator_ = builder1.buildPartial(); }  this.bitField0_ |= 0x20; continue;case 56: this.bitField0_ |= 0x40; this.position_ = input.readUInt32(); continue;case 66: builder = null; if ((this.bitField0_ & 0x80) != 0) builder = this.object_.toBuilder();  this.object_ = (MysqlxExpr.Object)input.readMessage(MysqlxExpr.Object.PARSER, extensionRegistry); if (builder != null) { builder.mergeFrom(this.object_); this.object_ = builder.buildPartial(); }  this.bitField0_ |= 0x80; continue;case 74: subBuilder = null; if ((this.bitField0_ & 0x100) != 0) subBuilder = this.array_.toBuilder();  this.array_ = (MysqlxExpr.Array)input.readMessage(MysqlxExpr.Array.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.array_); this.array_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x100; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_fieldAccessorTable.ensureFieldAccessorsInitialized(Expr.class, Builder.class); } public enum Type implements ProtocolMessageEnum {
/*       */       IDENT(1), LITERAL(2), VARIABLE(3), FUNC_CALL(4), OPERATOR(5), PLACEHOLDER(6), OBJECT(7), ARRAY(8); public static final int IDENT_VALUE = 1; public static final int LITERAL_VALUE = 2; public static final int VARIABLE_VALUE = 3; public static final int FUNC_CALL_VALUE = 4; public static final int OPERATOR_VALUE = 5; public static final int PLACEHOLDER_VALUE = 6; public static final int OBJECT_VALUE = 7; public static final int ARRAY_VALUE = 8; private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() { public MysqlxExpr.Expr.Type findValueByNumber(int number) { return MysqlxExpr.Expr.Type.forNumber(number); } }
/*       */       ; private static final Type[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Type forNumber(int value) { switch (value) { case 1: return IDENT;case 2: return LITERAL;case 3: return VARIABLE;case 4: return FUNC_CALL;case 5: return OPERATOR;case 6: return PLACEHOLDER;case 7: return OBJECT;case 8: return ARRAY; }  return null; } public static Internal.EnumLiteMap<Type> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxExpr.Expr.getDescriptor().getEnumTypes().get(0); } Type(int value) { this.value = value; } } public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); } public Type getType() { Type result = Type.valueOf(this.type_); return (result == null) ? Type.IDENT : result; } public boolean hasIdentifier() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxExpr.ColumnIdentifier getIdentifier() { return (this.identifier_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_; } public MysqlxExpr.ColumnIdentifierOrBuilder getIdentifierOrBuilder() { return (this.identifier_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_; } public boolean hasVariable() { return ((this.bitField0_ & 0x4) != 0); } public String getVariable() { Object ref = this.variable_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.variable_ = s;  return s; } public ByteString getVariableBytes() { Object ref = this.variable_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.variable_ = b; return b; }  return (ByteString)ref; } public boolean hasLiteral() { return ((this.bitField0_ & 0x8) != 0); } public MysqlxDatatypes.Scalar getLiteral() { return (this.literal_ == null) ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_; } public MysqlxDatatypes.ScalarOrBuilder getLiteralOrBuilder() { return (this.literal_ == null) ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_; } public boolean hasFunctionCall() { return ((this.bitField0_ & 0x10) != 0); } public MysqlxExpr.FunctionCall getFunctionCall() { return (this.functionCall_ == null) ? MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_; } public MysqlxExpr.FunctionCallOrBuilder getFunctionCallOrBuilder() { return (this.functionCall_ == null) ? MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_; } public boolean hasOperator() { return ((this.bitField0_ & 0x20) != 0); } public MysqlxExpr.Operator getOperator() { return (this.operator_ == null) ? MysqlxExpr.Operator.getDefaultInstance() : this.operator_; } public MysqlxExpr.OperatorOrBuilder getOperatorOrBuilder() { return (this.operator_ == null) ? MysqlxExpr.Operator.getDefaultInstance() : this.operator_; } public boolean hasPosition() { return ((this.bitField0_ & 0x40) != 0); } public int getPosition() { return this.position_; } public boolean hasObject() { return ((this.bitField0_ & 0x80) != 0); } public MysqlxExpr.Object getObject() { return (this.object_ == null) ? MysqlxExpr.Object.getDefaultInstance() : this.object_; } public MysqlxExpr.ObjectOrBuilder getObjectOrBuilder() { return (this.object_ == null) ? MysqlxExpr.Object.getDefaultInstance() : this.object_; } public boolean hasArray() { return ((this.bitField0_ & 0x100) != 0); } public MysqlxExpr.Array getArray() { return (this.array_ == null) ? MysqlxExpr.Array.getDefaultInstance() : this.array_; } public MysqlxExpr.ArrayOrBuilder getArrayOrBuilder() { return (this.array_ == null) ? MysqlxExpr.Array.getDefaultInstance() : this.array_; }
/*   755 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*   756 */       if (isInitialized == 1) return true; 
/*   757 */       if (isInitialized == 0) return false;
/*       */       
/*   759 */       if (!hasType()) {
/*   760 */         this.memoizedIsInitialized = 0;
/*   761 */         return false;
/*       */       } 
/*   763 */       if (hasIdentifier() && 
/*   764 */         !getIdentifier().isInitialized()) {
/*   765 */         this.memoizedIsInitialized = 0;
/*   766 */         return false;
/*       */       } 
/*       */       
/*   769 */       if (hasLiteral() && 
/*   770 */         !getLiteral().isInitialized()) {
/*   771 */         this.memoizedIsInitialized = 0;
/*   772 */         return false;
/*       */       } 
/*       */       
/*   775 */       if (hasFunctionCall() && 
/*   776 */         !getFunctionCall().isInitialized()) {
/*   777 */         this.memoizedIsInitialized = 0;
/*   778 */         return false;
/*       */       } 
/*       */       
/*   781 */       if (hasOperator() && 
/*   782 */         !getOperator().isInitialized()) {
/*   783 */         this.memoizedIsInitialized = 0;
/*   784 */         return false;
/*       */       } 
/*       */       
/*   787 */       if (hasObject() && 
/*   788 */         !getObject().isInitialized()) {
/*   789 */         this.memoizedIsInitialized = 0;
/*   790 */         return false;
/*       */       } 
/*       */       
/*   793 */       if (hasArray() && 
/*   794 */         !getArray().isInitialized()) {
/*   795 */         this.memoizedIsInitialized = 0;
/*   796 */         return false;
/*       */       } 
/*       */       
/*   799 */       this.memoizedIsInitialized = 1;
/*   800 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/*   806 */       if ((this.bitField0_ & 0x1) != 0) {
/*   807 */         output.writeEnum(1, this.type_);
/*       */       }
/*   809 */       if ((this.bitField0_ & 0x2) != 0) {
/*   810 */         output.writeMessage(2, (MessageLite)getIdentifier());
/*       */       }
/*   812 */       if ((this.bitField0_ & 0x4) != 0) {
/*   813 */         GeneratedMessageV3.writeString(output, 3, this.variable_);
/*       */       }
/*   815 */       if ((this.bitField0_ & 0x8) != 0) {
/*   816 */         output.writeMessage(4, (MessageLite)getLiteral());
/*       */       }
/*   818 */       if ((this.bitField0_ & 0x10) != 0) {
/*   819 */         output.writeMessage(5, (MessageLite)getFunctionCall());
/*       */       }
/*   821 */       if ((this.bitField0_ & 0x20) != 0) {
/*   822 */         output.writeMessage(6, (MessageLite)getOperator());
/*       */       }
/*   824 */       if ((this.bitField0_ & 0x40) != 0) {
/*   825 */         output.writeUInt32(7, this.position_);
/*       */       }
/*   827 */       if ((this.bitField0_ & 0x80) != 0) {
/*   828 */         output.writeMessage(8, (MessageLite)getObject());
/*       */       }
/*   830 */       if ((this.bitField0_ & 0x100) != 0) {
/*   831 */         output.writeMessage(9, (MessageLite)getArray());
/*       */       }
/*   833 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/*   838 */       int size = this.memoizedSize;
/*   839 */       if (size != -1) return size;
/*       */       
/*   841 */       size = 0;
/*   842 */       if ((this.bitField0_ & 0x1) != 0) {
/*   843 */         size += 
/*   844 */           CodedOutputStream.computeEnumSize(1, this.type_);
/*       */       }
/*   846 */       if ((this.bitField0_ & 0x2) != 0) {
/*   847 */         size += 
/*   848 */           CodedOutputStream.computeMessageSize(2, (MessageLite)getIdentifier());
/*       */       }
/*   850 */       if ((this.bitField0_ & 0x4) != 0) {
/*   851 */         size += GeneratedMessageV3.computeStringSize(3, this.variable_);
/*       */       }
/*   853 */       if ((this.bitField0_ & 0x8) != 0) {
/*   854 */         size += 
/*   855 */           CodedOutputStream.computeMessageSize(4, (MessageLite)getLiteral());
/*       */       }
/*   857 */       if ((this.bitField0_ & 0x10) != 0) {
/*   858 */         size += 
/*   859 */           CodedOutputStream.computeMessageSize(5, (MessageLite)getFunctionCall());
/*       */       }
/*   861 */       if ((this.bitField0_ & 0x20) != 0) {
/*   862 */         size += 
/*   863 */           CodedOutputStream.computeMessageSize(6, (MessageLite)getOperator());
/*       */       }
/*   865 */       if ((this.bitField0_ & 0x40) != 0) {
/*   866 */         size += 
/*   867 */           CodedOutputStream.computeUInt32Size(7, this.position_);
/*       */       }
/*   869 */       if ((this.bitField0_ & 0x80) != 0) {
/*   870 */         size += 
/*   871 */           CodedOutputStream.computeMessageSize(8, (MessageLite)getObject());
/*       */       }
/*   873 */       if ((this.bitField0_ & 0x100) != 0) {
/*   874 */         size += 
/*   875 */           CodedOutputStream.computeMessageSize(9, (MessageLite)getArray());
/*       */       }
/*   877 */       size += this.unknownFields.getSerializedSize();
/*   878 */       this.memoizedSize = size;
/*   879 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/*   884 */       if (obj == this) {
/*   885 */         return true;
/*       */       }
/*   887 */       if (!(obj instanceof Expr)) {
/*   888 */         return super.equals(obj);
/*       */       }
/*   890 */       Expr other = (Expr)obj;
/*       */       
/*   892 */       if (hasType() != other.hasType()) return false; 
/*   893 */       if (hasType() && 
/*   894 */         this.type_ != other.type_) return false;
/*       */       
/*   896 */       if (hasIdentifier() != other.hasIdentifier()) return false; 
/*   897 */       if (hasIdentifier() && 
/*       */         
/*   899 */         !getIdentifier().equals(other.getIdentifier())) return false;
/*       */       
/*   901 */       if (hasVariable() != other.hasVariable()) return false; 
/*   902 */       if (hasVariable() && 
/*       */         
/*   904 */         !getVariable().equals(other.getVariable())) return false;
/*       */       
/*   906 */       if (hasLiteral() != other.hasLiteral()) return false; 
/*   907 */       if (hasLiteral() && 
/*       */         
/*   909 */         !getLiteral().equals(other.getLiteral())) return false;
/*       */       
/*   911 */       if (hasFunctionCall() != other.hasFunctionCall()) return false; 
/*   912 */       if (hasFunctionCall() && 
/*       */         
/*   914 */         !getFunctionCall().equals(other.getFunctionCall())) return false;
/*       */       
/*   916 */       if (hasOperator() != other.hasOperator()) return false; 
/*   917 */       if (hasOperator() && 
/*       */         
/*   919 */         !getOperator().equals(other.getOperator())) return false;
/*       */       
/*   921 */       if (hasPosition() != other.hasPosition()) return false; 
/*   922 */       if (hasPosition() && 
/*   923 */         getPosition() != other
/*   924 */         .getPosition()) return false;
/*       */       
/*   926 */       if (hasObject() != other.hasObject()) return false; 
/*   927 */       if (hasObject() && 
/*       */         
/*   929 */         !getObject().equals(other.getObject())) return false;
/*       */       
/*   931 */       if (hasArray() != other.hasArray()) return false; 
/*   932 */       if (hasArray() && 
/*       */         
/*   934 */         !getArray().equals(other.getArray())) return false;
/*       */       
/*   936 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*   937 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/*   942 */       if (this.memoizedHashCode != 0) {
/*   943 */         return this.memoizedHashCode;
/*       */       }
/*   945 */       int hash = 41;
/*   946 */       hash = 19 * hash + getDescriptor().hashCode();
/*   947 */       if (hasType()) {
/*   948 */         hash = 37 * hash + 1;
/*   949 */         hash = 53 * hash + this.type_;
/*       */       } 
/*   951 */       if (hasIdentifier()) {
/*   952 */         hash = 37 * hash + 2;
/*   953 */         hash = 53 * hash + getIdentifier().hashCode();
/*       */       } 
/*   955 */       if (hasVariable()) {
/*   956 */         hash = 37 * hash + 3;
/*   957 */         hash = 53 * hash + getVariable().hashCode();
/*       */       } 
/*   959 */       if (hasLiteral()) {
/*   960 */         hash = 37 * hash + 4;
/*   961 */         hash = 53 * hash + getLiteral().hashCode();
/*       */       } 
/*   963 */       if (hasFunctionCall()) {
/*   964 */         hash = 37 * hash + 5;
/*   965 */         hash = 53 * hash + getFunctionCall().hashCode();
/*       */       } 
/*   967 */       if (hasOperator()) {
/*   968 */         hash = 37 * hash + 6;
/*   969 */         hash = 53 * hash + getOperator().hashCode();
/*       */       } 
/*   971 */       if (hasPosition()) {
/*   972 */         hash = 37 * hash + 7;
/*   973 */         hash = 53 * hash + getPosition();
/*       */       } 
/*   975 */       if (hasObject()) {
/*   976 */         hash = 37 * hash + 8;
/*   977 */         hash = 53 * hash + getObject().hashCode();
/*       */       } 
/*   979 */       if (hasArray()) {
/*   980 */         hash = 37 * hash + 9;
/*   981 */         hash = 53 * hash + getArray().hashCode();
/*       */       } 
/*   983 */       hash = 29 * hash + this.unknownFields.hashCode();
/*   984 */       this.memoizedHashCode = hash;
/*   985 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Expr parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*   991 */       return (Expr)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Expr parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*   997 */       return (Expr)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Expr parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  1002 */       return (Expr)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Expr parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  1008 */       return (Expr)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Expr parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  1012 */       return (Expr)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Expr parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  1018 */       return (Expr)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Expr parseFrom(InputStream input) throws IOException {
/*  1022 */       return 
/*  1023 */         (Expr)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Expr parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  1029 */       return 
/*  1030 */         (Expr)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Expr parseDelimitedFrom(InputStream input) throws IOException {
/*  1034 */       return 
/*  1035 */         (Expr)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Expr parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  1041 */       return 
/*  1042 */         (Expr)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Expr parseFrom(CodedInputStream input) throws IOException {
/*  1047 */       return 
/*  1048 */         (Expr)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Expr parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  1054 */       return 
/*  1055 */         (Expr)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/*  1059 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/*  1061 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(Expr prototype) {
/*  1064 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/*  1068 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  1069 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  1075 */       Builder builder = new Builder(parent);
/*  1076 */       return builder;
/*       */     }
/*       */ 
/*       */     
/*       */     public static final class Builder
/*       */       extends GeneratedMessageV3.Builder<Builder>
/*       */       implements MysqlxExpr.ExprOrBuilder
/*       */     {
/*       */       private int bitField0_;
/*       */       
/*       */       private int type_;
/*       */       
/*       */       private MysqlxExpr.ColumnIdentifier identifier_;
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxExpr.ColumnIdentifier, MysqlxExpr.ColumnIdentifier.Builder, MysqlxExpr.ColumnIdentifierOrBuilder> identifierBuilder_;
/*       */       
/*       */       private Object variable_;
/*       */       
/*       */       private MysqlxDatatypes.Scalar literal_;
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> literalBuilder_;
/*       */       private MysqlxExpr.FunctionCall functionCall_;
/*       */       private SingleFieldBuilderV3<MysqlxExpr.FunctionCall, MysqlxExpr.FunctionCall.Builder, MysqlxExpr.FunctionCallOrBuilder> functionCallBuilder_;
/*       */       private MysqlxExpr.Operator operator_;
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Operator, MysqlxExpr.Operator.Builder, MysqlxExpr.OperatorOrBuilder> operatorBuilder_;
/*       */       private int position_;
/*       */       private MysqlxExpr.Object object_;
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Object, MysqlxExpr.Object.Builder, MysqlxExpr.ObjectOrBuilder> objectBuilder_;
/*       */       private MysqlxExpr.Array array_;
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Array, MysqlxExpr.Array.Builder, MysqlxExpr.ArrayOrBuilder> arrayBuilder_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/*  1108 */         return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  1114 */         return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_fieldAccessorTable
/*  1115 */           .ensureFieldAccessorsInitialized(MysqlxExpr.Expr.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/*  1414 */         this.type_ = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/*  1576 */         this.variable_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.type_ = 1; this.variable_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxExpr.Expr.alwaysUseFieldBuilders) { getIdentifierFieldBuilder(); getLiteralFieldBuilder(); getFunctionCallFieldBuilder(); getOperatorFieldBuilder(); getObjectFieldBuilder(); getArrayFieldBuilder(); }  } public Builder clear() { super.clear(); this.type_ = 1; this.bitField0_ &= 0xFFFFFFFE; if (this.identifierBuilder_ == null) { this.identifier_ = null; } else { this.identifierBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; this.variable_ = ""; this.bitField0_ &= 0xFFFFFFFB; if (this.literalBuilder_ == null) { this.literal_ = null; } else { this.literalBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFF7; if (this.functionCallBuilder_ == null) { this.functionCall_ = null; } else { this.functionCallBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFEF; if (this.operatorBuilder_ == null) { this.operator_ = null; } else { this.operatorBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFDF; this.position_ = 0; this.bitField0_ &= 0xFFFFFFBF; if (this.objectBuilder_ == null) { this.object_ = null; } else { this.objectBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFF7F; if (this.arrayBuilder_ == null) { this.array_ = null; } else { this.arrayBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFEFF; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_descriptor; } public MysqlxExpr.Expr getDefaultInstanceForType() { return MysqlxExpr.Expr.getDefaultInstance(); } public MysqlxExpr.Expr build() { MysqlxExpr.Expr result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxExpr.Expr buildPartial() { MysqlxExpr.Expr result = new MysqlxExpr.Expr(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.type_ = this.type_; if ((from_bitField0_ & 0x2) != 0) { if (this.identifierBuilder_ == null) { result.identifier_ = this.identifier_; } else { result.identifier_ = (MysqlxExpr.ColumnIdentifier)this.identifierBuilder_.build(); }  to_bitField0_ |= 0x2; }  if ((from_bitField0_ & 0x4) != 0) to_bitField0_ |= 0x4;  result.variable_ = this.variable_; if ((from_bitField0_ & 0x8) != 0) { if (this.literalBuilder_ == null) { result.literal_ = this.literal_; } else { result.literal_ = (MysqlxDatatypes.Scalar)this.literalBuilder_.build(); }  to_bitField0_ |= 0x8; }  if ((from_bitField0_ & 0x10) != 0) { if (this.functionCallBuilder_ == null) { result.functionCall_ = this.functionCall_; } else { result.functionCall_ = (MysqlxExpr.FunctionCall)this.functionCallBuilder_.build(); }  to_bitField0_ |= 0x10; }  if ((from_bitField0_ & 0x20) != 0) { if (this.operatorBuilder_ == null) { result.operator_ = this.operator_; } else { result.operator_ = (MysqlxExpr.Operator)this.operatorBuilder_.build(); }  to_bitField0_ |= 0x20; }  if ((from_bitField0_ & 0x40) != 0) { result.position_ = this.position_; to_bitField0_ |= 0x40; }  if ((from_bitField0_ & 0x80) != 0) { if (this.objectBuilder_ == null) { result.object_ = this.object_; } else { result.object_ = (MysqlxExpr.Object)this.objectBuilder_.build(); }  to_bitField0_ |= 0x80; }  if ((from_bitField0_ & 0x100) != 0) { if (this.arrayBuilder_ == null) { result.array_ = this.array_; } else { result.array_ = (MysqlxExpr.Array)this.arrayBuilder_.build(); }  to_bitField0_ |= 0x100; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxExpr.Expr) return mergeFrom((MysqlxExpr.Expr)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxExpr.Expr other) { if (other == MysqlxExpr.Expr.getDefaultInstance()) return this;  if (other.hasType()) setType(other.getType());  if (other.hasIdentifier()) mergeIdentifier(other.getIdentifier());  if (other.hasVariable()) { this.bitField0_ |= 0x4; this.variable_ = other.variable_; onChanged(); }  if (other.hasLiteral()) mergeLiteral(other.getLiteral());  if (other.hasFunctionCall()) mergeFunctionCall(other.getFunctionCall());  if (other.hasOperator()) mergeOperator(other.getOperator());  if (other.hasPosition()) setPosition(other.getPosition());  if (other.hasObject()) mergeObject(other.getObject());  if (other.hasArray()) mergeArray(other.getArray());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasType()) return false;  if (hasIdentifier() && !getIdentifier().isInitialized()) return false;  if (hasLiteral() && !getLiteral().isInitialized()) return false;  if (hasFunctionCall() && !getFunctionCall().isInitialized()) return false;  if (hasOperator() && !getOperator().isInitialized()) return false;  if (hasObject() && !getObject().isInitialized()) return false;  if (hasArray() && !getArray().isInitialized()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxExpr.Expr parsedMessage = null; try { parsedMessage = (MysqlxExpr.Expr)MysqlxExpr.Expr.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxExpr.Expr)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxExpr.Expr.Type getType() { MysqlxExpr.Expr.Type result = MysqlxExpr.Expr.Type.valueOf(this.type_); return (result == null) ? MysqlxExpr.Expr.Type.IDENT : result; } public Builder setType(MysqlxExpr.Expr.Type value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.type_ = value.getNumber(); onChanged(); return this; } public Builder clearType() { this.bitField0_ &= 0xFFFFFFFE; this.type_ = 1; onChanged(); return this; } public boolean hasIdentifier() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxExpr.ColumnIdentifier getIdentifier() { if (this.identifierBuilder_ == null) return (this.identifier_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_;  return (MysqlxExpr.ColumnIdentifier)this.identifierBuilder_.getMessage(); } public Builder setIdentifier(MysqlxExpr.ColumnIdentifier value) { if (this.identifierBuilder_ == null) { if (value == null) throw new NullPointerException();  this.identifier_ = value; onChanged(); } else { this.identifierBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x2; return this; } public Builder setIdentifier(MysqlxExpr.ColumnIdentifier.Builder builderForValue) { if (this.identifierBuilder_ == null) { this.identifier_ = builderForValue.build(); onChanged(); } else { this.identifierBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x2; return this; }
/*       */       public Builder mergeIdentifier(MysqlxExpr.ColumnIdentifier value) { if (this.identifierBuilder_ == null) { if ((this.bitField0_ & 0x2) != 0 && this.identifier_ != null && this.identifier_ != MysqlxExpr.ColumnIdentifier.getDefaultInstance()) { this.identifier_ = MysqlxExpr.ColumnIdentifier.newBuilder(this.identifier_).mergeFrom(value).buildPartial(); } else { this.identifier_ = value; }  onChanged(); } else { this.identifierBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x2; return this; }
/*       */       public Builder clearIdentifier() { if (this.identifierBuilder_ == null) { this.identifier_ = null; onChanged(); } else { this.identifierBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; return this; }
/*       */       public MysqlxExpr.ColumnIdentifier.Builder getIdentifierBuilder() { this.bitField0_ |= 0x2; onChanged(); return (MysqlxExpr.ColumnIdentifier.Builder)getIdentifierFieldBuilder().getBuilder(); }
/*       */       public MysqlxExpr.ColumnIdentifierOrBuilder getIdentifierOrBuilder() { if (this.identifierBuilder_ != null) return (MysqlxExpr.ColumnIdentifierOrBuilder)this.identifierBuilder_.getMessageOrBuilder();  return (this.identifier_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_; }
/*       */       private SingleFieldBuilderV3<MysqlxExpr.ColumnIdentifier, MysqlxExpr.ColumnIdentifier.Builder, MysqlxExpr.ColumnIdentifierOrBuilder> getIdentifierFieldBuilder() { if (this.identifierBuilder_ == null) { this.identifierBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getIdentifier(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.identifier_ = null; }  return this.identifierBuilder_; }
/*  1582 */       public boolean hasVariable() { return ((this.bitField0_ & 0x4) != 0); }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public String getVariable() {
/*  1589 */         Object ref = this.variable_;
/*  1590 */         if (!(ref instanceof String)) {
/*  1591 */           ByteString bs = (ByteString)ref;
/*       */           
/*  1593 */           String s = bs.toStringUtf8();
/*  1594 */           if (bs.isValidUtf8()) {
/*  1595 */             this.variable_ = s;
/*       */           }
/*  1597 */           return s;
/*       */         } 
/*  1599 */         return (String)ref;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public ByteString getVariableBytes() {
/*  1608 */         Object ref = this.variable_;
/*  1609 */         if (ref instanceof String) {
/*       */           
/*  1611 */           ByteString b = ByteString.copyFromUtf8((String)ref);
/*       */           
/*  1613 */           this.variable_ = b;
/*  1614 */           return b;
/*       */         } 
/*  1616 */         return (ByteString)ref;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setVariable(String value) {
/*  1626 */         if (value == null) {
/*  1627 */           throw new NullPointerException();
/*       */         }
/*  1629 */         this.bitField0_ |= 0x4;
/*  1630 */         this.variable_ = value;
/*  1631 */         onChanged();
/*  1632 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearVariable() {
/*  1639 */         this.bitField0_ &= 0xFFFFFFFB;
/*  1640 */         this.variable_ = MysqlxExpr.Expr.getDefaultInstance().getVariable();
/*  1641 */         onChanged();
/*  1642 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setVariableBytes(ByteString value) {
/*  1651 */         if (value == null) {
/*  1652 */           throw new NullPointerException();
/*       */         }
/*  1654 */         this.bitField0_ |= 0x4;
/*  1655 */         this.variable_ = value;
/*  1656 */         onChanged();
/*  1657 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasLiteral() {
/*  1668 */         return ((this.bitField0_ & 0x8) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.Scalar getLiteral() {
/*  1675 */         if (this.literalBuilder_ == null) {
/*  1676 */           return (this.literal_ == null) ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_;
/*       */         }
/*  1678 */         return (MysqlxDatatypes.Scalar)this.literalBuilder_.getMessage();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setLiteral(MysqlxDatatypes.Scalar value) {
/*  1685 */         if (this.literalBuilder_ == null) {
/*  1686 */           if (value == null) {
/*  1687 */             throw new NullPointerException();
/*       */           }
/*  1689 */           this.literal_ = value;
/*  1690 */           onChanged();
/*       */         } else {
/*  1692 */           this.literalBuilder_.setMessage((AbstractMessage)value);
/*       */         } 
/*  1694 */         this.bitField0_ |= 0x8;
/*  1695 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setLiteral(MysqlxDatatypes.Scalar.Builder builderForValue) {
/*  1702 */         if (this.literalBuilder_ == null) {
/*  1703 */           this.literal_ = builderForValue.build();
/*  1704 */           onChanged();
/*       */         } else {
/*  1706 */           this.literalBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/*  1708 */         this.bitField0_ |= 0x8;
/*  1709 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeLiteral(MysqlxDatatypes.Scalar value) {
/*  1715 */         if (this.literalBuilder_ == null) {
/*  1716 */           if ((this.bitField0_ & 0x8) != 0 && this.literal_ != null && this.literal_ != 
/*       */             
/*  1718 */             MysqlxDatatypes.Scalar.getDefaultInstance()) {
/*  1719 */             this
/*  1720 */               .literal_ = MysqlxDatatypes.Scalar.newBuilder(this.literal_).mergeFrom(value).buildPartial();
/*       */           } else {
/*  1722 */             this.literal_ = value;
/*       */           } 
/*  1724 */           onChanged();
/*       */         } else {
/*  1726 */           this.literalBuilder_.mergeFrom((AbstractMessage)value);
/*       */         } 
/*  1728 */         this.bitField0_ |= 0x8;
/*  1729 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearLiteral() {
/*  1735 */         if (this.literalBuilder_ == null) {
/*  1736 */           this.literal_ = null;
/*  1737 */           onChanged();
/*       */         } else {
/*  1739 */           this.literalBuilder_.clear();
/*       */         } 
/*  1741 */         this.bitField0_ &= 0xFFFFFFF7;
/*  1742 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.Scalar.Builder getLiteralBuilder() {
/*  1748 */         this.bitField0_ |= 0x8;
/*  1749 */         onChanged();
/*  1750 */         return (MysqlxDatatypes.Scalar.Builder)getLiteralFieldBuilder().getBuilder();
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.ScalarOrBuilder getLiteralOrBuilder() {
/*  1756 */         if (this.literalBuilder_ != null) {
/*  1757 */           return (MysqlxDatatypes.ScalarOrBuilder)this.literalBuilder_.getMessageOrBuilder();
/*       */         }
/*  1759 */         return (this.literal_ == null) ? 
/*  1760 */           MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getLiteralFieldBuilder() {
/*  1769 */         if (this.literalBuilder_ == null) {
/*  1770 */           this
/*       */ 
/*       */ 
/*       */             
/*  1774 */             .literalBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getLiteral(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/*  1775 */           this.literal_ = null;
/*       */         } 
/*  1777 */         return this.literalBuilder_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasFunctionCall() {
/*  1788 */         return ((this.bitField0_ & 0x10) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.FunctionCall getFunctionCall() {
/*  1795 */         if (this.functionCallBuilder_ == null) {
/*  1796 */           return (this.functionCall_ == null) ? MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_;
/*       */         }
/*  1798 */         return (MysqlxExpr.FunctionCall)this.functionCallBuilder_.getMessage();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setFunctionCall(MysqlxExpr.FunctionCall value) {
/*  1805 */         if (this.functionCallBuilder_ == null) {
/*  1806 */           if (value == null) {
/*  1807 */             throw new NullPointerException();
/*       */           }
/*  1809 */           this.functionCall_ = value;
/*  1810 */           onChanged();
/*       */         } else {
/*  1812 */           this.functionCallBuilder_.setMessage((AbstractMessage)value);
/*       */         } 
/*  1814 */         this.bitField0_ |= 0x10;
/*  1815 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setFunctionCall(MysqlxExpr.FunctionCall.Builder builderForValue) {
/*  1822 */         if (this.functionCallBuilder_ == null) {
/*  1823 */           this.functionCall_ = builderForValue.build();
/*  1824 */           onChanged();
/*       */         } else {
/*  1826 */           this.functionCallBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/*  1828 */         this.bitField0_ |= 0x10;
/*  1829 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeFunctionCall(MysqlxExpr.FunctionCall value) {
/*  1835 */         if (this.functionCallBuilder_ == null) {
/*  1836 */           if ((this.bitField0_ & 0x10) != 0 && this.functionCall_ != null && this.functionCall_ != 
/*       */             
/*  1838 */             MysqlxExpr.FunctionCall.getDefaultInstance()) {
/*  1839 */             this
/*  1840 */               .functionCall_ = MysqlxExpr.FunctionCall.newBuilder(this.functionCall_).mergeFrom(value).buildPartial();
/*       */           } else {
/*  1842 */             this.functionCall_ = value;
/*       */           } 
/*  1844 */           onChanged();
/*       */         } else {
/*  1846 */           this.functionCallBuilder_.mergeFrom((AbstractMessage)value);
/*       */         } 
/*  1848 */         this.bitField0_ |= 0x10;
/*  1849 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearFunctionCall() {
/*  1855 */         if (this.functionCallBuilder_ == null) {
/*  1856 */           this.functionCall_ = null;
/*  1857 */           onChanged();
/*       */         } else {
/*  1859 */           this.functionCallBuilder_.clear();
/*       */         } 
/*  1861 */         this.bitField0_ &= 0xFFFFFFEF;
/*  1862 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.FunctionCall.Builder getFunctionCallBuilder() {
/*  1868 */         this.bitField0_ |= 0x10;
/*  1869 */         onChanged();
/*  1870 */         return (MysqlxExpr.FunctionCall.Builder)getFunctionCallFieldBuilder().getBuilder();
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.FunctionCallOrBuilder getFunctionCallOrBuilder() {
/*  1876 */         if (this.functionCallBuilder_ != null) {
/*  1877 */           return (MysqlxExpr.FunctionCallOrBuilder)this.functionCallBuilder_.getMessageOrBuilder();
/*       */         }
/*  1879 */         return (this.functionCall_ == null) ? 
/*  1880 */           MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxExpr.FunctionCall, MysqlxExpr.FunctionCall.Builder, MysqlxExpr.FunctionCallOrBuilder> getFunctionCallFieldBuilder() {
/*  1889 */         if (this.functionCallBuilder_ == null) {
/*  1890 */           this
/*       */ 
/*       */ 
/*       */             
/*  1894 */             .functionCallBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getFunctionCall(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/*  1895 */           this.functionCall_ = null;
/*       */         } 
/*  1897 */         return this.functionCallBuilder_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasOperator() {
/*  1908 */         return ((this.bitField0_ & 0x20) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Operator getOperator() {
/*  1915 */         if (this.operatorBuilder_ == null) {
/*  1916 */           return (this.operator_ == null) ? MysqlxExpr.Operator.getDefaultInstance() : this.operator_;
/*       */         }
/*  1918 */         return (MysqlxExpr.Operator)this.operatorBuilder_.getMessage();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setOperator(MysqlxExpr.Operator value) {
/*  1925 */         if (this.operatorBuilder_ == null) {
/*  1926 */           if (value == null) {
/*  1927 */             throw new NullPointerException();
/*       */           }
/*  1929 */           this.operator_ = value;
/*  1930 */           onChanged();
/*       */         } else {
/*  1932 */           this.operatorBuilder_.setMessage((AbstractMessage)value);
/*       */         } 
/*  1934 */         this.bitField0_ |= 0x20;
/*  1935 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setOperator(MysqlxExpr.Operator.Builder builderForValue) {
/*  1942 */         if (this.operatorBuilder_ == null) {
/*  1943 */           this.operator_ = builderForValue.build();
/*  1944 */           onChanged();
/*       */         } else {
/*  1946 */           this.operatorBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/*  1948 */         this.bitField0_ |= 0x20;
/*  1949 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeOperator(MysqlxExpr.Operator value) {
/*  1955 */         if (this.operatorBuilder_ == null) {
/*  1956 */           if ((this.bitField0_ & 0x20) != 0 && this.operator_ != null && this.operator_ != 
/*       */             
/*  1958 */             MysqlxExpr.Operator.getDefaultInstance()) {
/*  1959 */             this
/*  1960 */               .operator_ = MysqlxExpr.Operator.newBuilder(this.operator_).mergeFrom(value).buildPartial();
/*       */           } else {
/*  1962 */             this.operator_ = value;
/*       */           } 
/*  1964 */           onChanged();
/*       */         } else {
/*  1966 */           this.operatorBuilder_.mergeFrom((AbstractMessage)value);
/*       */         } 
/*  1968 */         this.bitField0_ |= 0x20;
/*  1969 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearOperator() {
/*  1975 */         if (this.operatorBuilder_ == null) {
/*  1976 */           this.operator_ = null;
/*  1977 */           onChanged();
/*       */         } else {
/*  1979 */           this.operatorBuilder_.clear();
/*       */         } 
/*  1981 */         this.bitField0_ &= 0xFFFFFFDF;
/*  1982 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Operator.Builder getOperatorBuilder() {
/*  1988 */         this.bitField0_ |= 0x20;
/*  1989 */         onChanged();
/*  1990 */         return (MysqlxExpr.Operator.Builder)getOperatorFieldBuilder().getBuilder();
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.OperatorOrBuilder getOperatorOrBuilder() {
/*  1996 */         if (this.operatorBuilder_ != null) {
/*  1997 */           return (MysqlxExpr.OperatorOrBuilder)this.operatorBuilder_.getMessageOrBuilder();
/*       */         }
/*  1999 */         return (this.operator_ == null) ? 
/*  2000 */           MysqlxExpr.Operator.getDefaultInstance() : this.operator_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Operator, MysqlxExpr.Operator.Builder, MysqlxExpr.OperatorOrBuilder> getOperatorFieldBuilder() {
/*  2009 */         if (this.operatorBuilder_ == null) {
/*  2010 */           this
/*       */ 
/*       */ 
/*       */             
/*  2014 */             .operatorBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getOperator(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/*  2015 */           this.operator_ = null;
/*       */         } 
/*  2017 */         return this.operatorBuilder_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasPosition() {
/*  2026 */         return ((this.bitField0_ & 0x40) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public int getPosition() {
/*  2033 */         return this.position_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setPosition(int value) {
/*  2041 */         this.bitField0_ |= 0x40;
/*  2042 */         this.position_ = value;
/*  2043 */         onChanged();
/*  2044 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearPosition() {
/*  2051 */         this.bitField0_ &= 0xFFFFFFBF;
/*  2052 */         this.position_ = 0;
/*  2053 */         onChanged();
/*  2054 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasObject() {
/*  2065 */         return ((this.bitField0_ & 0x80) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Object getObject() {
/*  2072 */         if (this.objectBuilder_ == null) {
/*  2073 */           return (this.object_ == null) ? MysqlxExpr.Object.getDefaultInstance() : this.object_;
/*       */         }
/*  2075 */         return (MysqlxExpr.Object)this.objectBuilder_.getMessage();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setObject(MysqlxExpr.Object value) {
/*  2082 */         if (this.objectBuilder_ == null) {
/*  2083 */           if (value == null) {
/*  2084 */             throw new NullPointerException();
/*       */           }
/*  2086 */           this.object_ = value;
/*  2087 */           onChanged();
/*       */         } else {
/*  2089 */           this.objectBuilder_.setMessage((AbstractMessage)value);
/*       */         } 
/*  2091 */         this.bitField0_ |= 0x80;
/*  2092 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setObject(MysqlxExpr.Object.Builder builderForValue) {
/*  2099 */         if (this.objectBuilder_ == null) {
/*  2100 */           this.object_ = builderForValue.build();
/*  2101 */           onChanged();
/*       */         } else {
/*  2103 */           this.objectBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/*  2105 */         this.bitField0_ |= 0x80;
/*  2106 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeObject(MysqlxExpr.Object value) {
/*  2112 */         if (this.objectBuilder_ == null) {
/*  2113 */           if ((this.bitField0_ & 0x80) != 0 && this.object_ != null && this.object_ != 
/*       */             
/*  2115 */             MysqlxExpr.Object.getDefaultInstance()) {
/*  2116 */             this
/*  2117 */               .object_ = MysqlxExpr.Object.newBuilder(this.object_).mergeFrom(value).buildPartial();
/*       */           } else {
/*  2119 */             this.object_ = value;
/*       */           } 
/*  2121 */           onChanged();
/*       */         } else {
/*  2123 */           this.objectBuilder_.mergeFrom((AbstractMessage)value);
/*       */         } 
/*  2125 */         this.bitField0_ |= 0x80;
/*  2126 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearObject() {
/*  2132 */         if (this.objectBuilder_ == null) {
/*  2133 */           this.object_ = null;
/*  2134 */           onChanged();
/*       */         } else {
/*  2136 */           this.objectBuilder_.clear();
/*       */         } 
/*  2138 */         this.bitField0_ &= 0xFFFFFF7F;
/*  2139 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Object.Builder getObjectBuilder() {
/*  2145 */         this.bitField0_ |= 0x80;
/*  2146 */         onChanged();
/*  2147 */         return (MysqlxExpr.Object.Builder)getObjectFieldBuilder().getBuilder();
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.ObjectOrBuilder getObjectOrBuilder() {
/*  2153 */         if (this.objectBuilder_ != null) {
/*  2154 */           return (MysqlxExpr.ObjectOrBuilder)this.objectBuilder_.getMessageOrBuilder();
/*       */         }
/*  2156 */         return (this.object_ == null) ? 
/*  2157 */           MysqlxExpr.Object.getDefaultInstance() : this.object_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Object, MysqlxExpr.Object.Builder, MysqlxExpr.ObjectOrBuilder> getObjectFieldBuilder() {
/*  2166 */         if (this.objectBuilder_ == null) {
/*  2167 */           this
/*       */ 
/*       */ 
/*       */             
/*  2171 */             .objectBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getObject(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/*  2172 */           this.object_ = null;
/*       */         } 
/*  2174 */         return this.objectBuilder_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasArray() {
/*  2185 */         return ((this.bitField0_ & 0x100) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Array getArray() {
/*  2192 */         if (this.arrayBuilder_ == null) {
/*  2193 */           return (this.array_ == null) ? MysqlxExpr.Array.getDefaultInstance() : this.array_;
/*       */         }
/*  2195 */         return (MysqlxExpr.Array)this.arrayBuilder_.getMessage();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setArray(MysqlxExpr.Array value) {
/*  2202 */         if (this.arrayBuilder_ == null) {
/*  2203 */           if (value == null) {
/*  2204 */             throw new NullPointerException();
/*       */           }
/*  2206 */           this.array_ = value;
/*  2207 */           onChanged();
/*       */         } else {
/*  2209 */           this.arrayBuilder_.setMessage((AbstractMessage)value);
/*       */         } 
/*  2211 */         this.bitField0_ |= 0x100;
/*  2212 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setArray(MysqlxExpr.Array.Builder builderForValue) {
/*  2219 */         if (this.arrayBuilder_ == null) {
/*  2220 */           this.array_ = builderForValue.build();
/*  2221 */           onChanged();
/*       */         } else {
/*  2223 */           this.arrayBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/*  2225 */         this.bitField0_ |= 0x100;
/*  2226 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeArray(MysqlxExpr.Array value) {
/*  2232 */         if (this.arrayBuilder_ == null) {
/*  2233 */           if ((this.bitField0_ & 0x100) != 0 && this.array_ != null && this.array_ != 
/*       */             
/*  2235 */             MysqlxExpr.Array.getDefaultInstance()) {
/*  2236 */             this
/*  2237 */               .array_ = MysqlxExpr.Array.newBuilder(this.array_).mergeFrom(value).buildPartial();
/*       */           } else {
/*  2239 */             this.array_ = value;
/*       */           } 
/*  2241 */           onChanged();
/*       */         } else {
/*  2243 */           this.arrayBuilder_.mergeFrom((AbstractMessage)value);
/*       */         } 
/*  2245 */         this.bitField0_ |= 0x100;
/*  2246 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearArray() {
/*  2252 */         if (this.arrayBuilder_ == null) {
/*  2253 */           this.array_ = null;
/*  2254 */           onChanged();
/*       */         } else {
/*  2256 */           this.arrayBuilder_.clear();
/*       */         } 
/*  2258 */         this.bitField0_ &= 0xFFFFFEFF;
/*  2259 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Array.Builder getArrayBuilder() {
/*  2265 */         this.bitField0_ |= 0x100;
/*  2266 */         onChanged();
/*  2267 */         return (MysqlxExpr.Array.Builder)getArrayFieldBuilder().getBuilder();
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.ArrayOrBuilder getArrayOrBuilder() {
/*  2273 */         if (this.arrayBuilder_ != null) {
/*  2274 */           return (MysqlxExpr.ArrayOrBuilder)this.arrayBuilder_.getMessageOrBuilder();
/*       */         }
/*  2276 */         return (this.array_ == null) ? 
/*  2277 */           MysqlxExpr.Array.getDefaultInstance() : this.array_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Array, MysqlxExpr.Array.Builder, MysqlxExpr.ArrayOrBuilder> getArrayFieldBuilder() {
/*  2286 */         if (this.arrayBuilder_ == null) {
/*  2287 */           this
/*       */ 
/*       */ 
/*       */             
/*  2291 */             .arrayBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getArray(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/*  2292 */           this.array_ = null;
/*       */         } 
/*  2294 */         return this.arrayBuilder_;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  2299 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  2305 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*  2315 */     private static final Expr DEFAULT_INSTANCE = new Expr();
/*       */ 
/*       */     
/*       */     public static Expr getDefaultInstance() {
/*  2319 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/*  2323 */     public static final Parser<Expr> PARSER = (Parser<Expr>)new AbstractParser<Expr>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxExpr.Expr parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/*  2329 */           return new MysqlxExpr.Expr(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<Expr> parser() {
/*  2334 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<Expr> getParserForType() {
/*  2339 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Expr getDefaultInstanceForType() {
/*  2344 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface IdentifierOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasName();
/*       */ 
/*       */ 
/*       */     
/*       */     String getName();
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getNameBytes();
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasSchemaName();
/*       */ 
/*       */ 
/*       */     
/*       */     String getSchemaName();
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getSchemaNameBytes();
/*       */   }
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class Identifier
/*       */     extends GeneratedMessageV3
/*       */     implements IdentifierOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */     
/*       */     public static final int NAME_FIELD_NUMBER = 1;
/*       */ 
/*       */     
/*       */     private volatile Object name_;
/*       */ 
/*       */     
/*       */     public static final int SCHEMA_NAME_FIELD_NUMBER = 2;
/*       */ 
/*       */     
/*       */     private volatile Object schemaName_;
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */     
/*       */     private Identifier(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/*  2407 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*  2580 */       this.memoizedIsInitialized = -1; } private Identifier() { this.memoizedIsInitialized = -1; this.name_ = ""; this.schemaName_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Identifier(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Identifier(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: bs = input.readBytes(); this.bitField0_ |= 0x1; this.name_ = bs; continue;case 18: bs = input.readBytes(); this.bitField0_ |= 0x2; this.schemaName_ = bs; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_fieldAccessorTable.ensureFieldAccessorsInitialized(Identifier.class, Builder.class); } public boolean hasName() { return ((this.bitField0_ & 0x1) != 0); } public String getName() { Object ref = this.name_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.name_ = s;  return s; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; } public boolean hasSchemaName() { return ((this.bitField0_ & 0x2) != 0); }
/*       */     public String getSchemaName() { Object ref = this.schemaName_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.schemaName_ = s;  return s; }
/*       */     public ByteString getSchemaNameBytes() { Object ref = this.schemaName_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.schemaName_ = b; return b; }  return (ByteString)ref; }
/*  2583 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  2584 */       if (isInitialized == 1) return true; 
/*  2585 */       if (isInitialized == 0) return false;
/*       */       
/*  2587 */       if (!hasName()) {
/*  2588 */         this.memoizedIsInitialized = 0;
/*  2589 */         return false;
/*       */       } 
/*  2591 */       this.memoizedIsInitialized = 1;
/*  2592 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/*  2598 */       if ((this.bitField0_ & 0x1) != 0) {
/*  2599 */         GeneratedMessageV3.writeString(output, 1, this.name_);
/*       */       }
/*  2601 */       if ((this.bitField0_ & 0x2) != 0) {
/*  2602 */         GeneratedMessageV3.writeString(output, 2, this.schemaName_);
/*       */       }
/*  2604 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/*  2609 */       int size = this.memoizedSize;
/*  2610 */       if (size != -1) return size;
/*       */       
/*  2612 */       size = 0;
/*  2613 */       if ((this.bitField0_ & 0x1) != 0) {
/*  2614 */         size += GeneratedMessageV3.computeStringSize(1, this.name_);
/*       */       }
/*  2616 */       if ((this.bitField0_ & 0x2) != 0) {
/*  2617 */         size += GeneratedMessageV3.computeStringSize(2, this.schemaName_);
/*       */       }
/*  2619 */       size += this.unknownFields.getSerializedSize();
/*  2620 */       this.memoizedSize = size;
/*  2621 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/*  2626 */       if (obj == this) {
/*  2627 */         return true;
/*       */       }
/*  2629 */       if (!(obj instanceof Identifier)) {
/*  2630 */         return super.equals(obj);
/*       */       }
/*  2632 */       Identifier other = (Identifier)obj;
/*       */       
/*  2634 */       if (hasName() != other.hasName()) return false; 
/*  2635 */       if (hasName() && 
/*       */         
/*  2637 */         !getName().equals(other.getName())) return false;
/*       */       
/*  2639 */       if (hasSchemaName() != other.hasSchemaName()) return false; 
/*  2640 */       if (hasSchemaName() && 
/*       */         
/*  2642 */         !getSchemaName().equals(other.getSchemaName())) return false;
/*       */       
/*  2644 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  2645 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/*  2650 */       if (this.memoizedHashCode != 0) {
/*  2651 */         return this.memoizedHashCode;
/*       */       }
/*  2653 */       int hash = 41;
/*  2654 */       hash = 19 * hash + getDescriptor().hashCode();
/*  2655 */       if (hasName()) {
/*  2656 */         hash = 37 * hash + 1;
/*  2657 */         hash = 53 * hash + getName().hashCode();
/*       */       } 
/*  2659 */       if (hasSchemaName()) {
/*  2660 */         hash = 37 * hash + 2;
/*  2661 */         hash = 53 * hash + getSchemaName().hashCode();
/*       */       } 
/*  2663 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  2664 */       this.memoizedHashCode = hash;
/*  2665 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Identifier parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  2671 */       return (Identifier)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Identifier parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  2677 */       return (Identifier)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Identifier parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  2682 */       return (Identifier)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Identifier parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  2688 */       return (Identifier)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Identifier parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  2692 */       return (Identifier)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Identifier parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  2698 */       return (Identifier)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Identifier parseFrom(InputStream input) throws IOException {
/*  2702 */       return 
/*  2703 */         (Identifier)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Identifier parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  2709 */       return 
/*  2710 */         (Identifier)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Identifier parseDelimitedFrom(InputStream input) throws IOException {
/*  2714 */       return 
/*  2715 */         (Identifier)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Identifier parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  2721 */       return 
/*  2722 */         (Identifier)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Identifier parseFrom(CodedInputStream input) throws IOException {
/*  2727 */       return 
/*  2728 */         (Identifier)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Identifier parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  2734 */       return 
/*  2735 */         (Identifier)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/*  2739 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/*  2741 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(Identifier prototype) {
/*  2744 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/*  2748 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  2749 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  2755 */       Builder builder = new Builder(parent);
/*  2756 */       return builder;
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final class Builder
/*       */       extends GeneratedMessageV3.Builder<Builder>
/*       */       implements MysqlxExpr.IdentifierOrBuilder
/*       */     {
/*       */       private int bitField0_;
/*       */ 
/*       */       
/*       */       private Object name_;
/*       */ 
/*       */       
/*       */       private Object schemaName_;
/*       */ 
/*       */ 
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/*  2777 */         return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  2783 */         return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_fieldAccessorTable
/*  2784 */           .ensureFieldAccessorsInitialized(MysqlxExpr.Identifier.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/*  2938 */         this.name_ = "";
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/*  3022 */         this.schemaName_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.name_ = ""; this.schemaName_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxExpr.Identifier.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.name_ = ""; this.bitField0_ &= 0xFFFFFFFE; this.schemaName_ = ""; this.bitField0_ &= 0xFFFFFFFD; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_descriptor; } public MysqlxExpr.Identifier getDefaultInstanceForType() { return MysqlxExpr.Identifier.getDefaultInstance(); } public MysqlxExpr.Identifier build() { MysqlxExpr.Identifier result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxExpr.Identifier buildPartial() { MysqlxExpr.Identifier result = new MysqlxExpr.Identifier(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.name_ = this.name_; if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.schemaName_ = this.schemaName_; result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxExpr.Identifier) return mergeFrom((MysqlxExpr.Identifier)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxExpr.Identifier other) { if (other == MysqlxExpr.Identifier.getDefaultInstance()) return this;  if (other.hasName()) { this.bitField0_ |= 0x1; this.name_ = other.name_; onChanged(); }  if (other.hasSchemaName()) { this.bitField0_ |= 0x2; this.schemaName_ = other.schemaName_; onChanged(); }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasName()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxExpr.Identifier parsedMessage = null; try { parsedMessage = (MysqlxExpr.Identifier)MysqlxExpr.Identifier.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxExpr.Identifier)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasName() { return ((this.bitField0_ & 0x1) != 0); }
/*       */       public String getName() { Object ref = this.name_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.name_ = s;  return s; }  return (String)ref; }
/*       */       public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; }
/*       */       public Builder setName(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.name_ = value; onChanged(); return this; }
/*       */       public Builder clearName() { this.bitField0_ &= 0xFFFFFFFE; this.name_ = MysqlxExpr.Identifier.getDefaultInstance().getName(); onChanged(); return this; }
/*       */       public Builder setNameBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.name_ = value; onChanged(); return this; }
/*  3028 */       public boolean hasSchemaName() { return ((this.bitField0_ & 0x2) != 0); }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public String getSchemaName() {
/*  3035 */         Object ref = this.schemaName_;
/*  3036 */         if (!(ref instanceof String)) {
/*  3037 */           ByteString bs = (ByteString)ref;
/*       */           
/*  3039 */           String s = bs.toStringUtf8();
/*  3040 */           if (bs.isValidUtf8()) {
/*  3041 */             this.schemaName_ = s;
/*       */           }
/*  3043 */           return s;
/*       */         } 
/*  3045 */         return (String)ref;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public ByteString getSchemaNameBytes() {
/*  3054 */         Object ref = this.schemaName_;
/*  3055 */         if (ref instanceof String) {
/*       */           
/*  3057 */           ByteString b = ByteString.copyFromUtf8((String)ref);
/*       */           
/*  3059 */           this.schemaName_ = b;
/*  3060 */           return b;
/*       */         } 
/*  3062 */         return (ByteString)ref;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setSchemaName(String value) {
/*  3072 */         if (value == null) {
/*  3073 */           throw new NullPointerException();
/*       */         }
/*  3075 */         this.bitField0_ |= 0x2;
/*  3076 */         this.schemaName_ = value;
/*  3077 */         onChanged();
/*  3078 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearSchemaName() {
/*  3085 */         this.bitField0_ &= 0xFFFFFFFD;
/*  3086 */         this.schemaName_ = MysqlxExpr.Identifier.getDefaultInstance().getSchemaName();
/*  3087 */         onChanged();
/*  3088 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setSchemaNameBytes(ByteString value) {
/*  3097 */         if (value == null) {
/*  3098 */           throw new NullPointerException();
/*       */         }
/*  3100 */         this.bitField0_ |= 0x2;
/*  3101 */         this.schemaName_ = value;
/*  3102 */         onChanged();
/*  3103 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  3108 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  3114 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*  3124 */     private static final Identifier DEFAULT_INSTANCE = new Identifier();
/*       */ 
/*       */     
/*       */     public static Identifier getDefaultInstance() {
/*  3128 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/*  3132 */     public static final Parser<Identifier> PARSER = (Parser<Identifier>)new AbstractParser<Identifier>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxExpr.Identifier parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/*  3138 */           return new MysqlxExpr.Identifier(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<Identifier> parser() {
/*  3143 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<Identifier> getParserForType() {
/*  3148 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Identifier getDefaultInstanceForType() {
/*  3153 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface DocumentPathItemOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasType();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.DocumentPathItem.Type getType();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasValue();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     String getValue();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getValueBytes();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasIndex();
/*       */ 
/*       */ 
/*       */     
/*       */     int getIndex();
/*       */   }
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class DocumentPathItem
/*       */     extends GeneratedMessageV3
/*       */     implements DocumentPathItemOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */     
/*       */     public static final int TYPE_FIELD_NUMBER = 1;
/*       */ 
/*       */     
/*       */     private int type_;
/*       */ 
/*       */     
/*       */     public static final int VALUE_FIELD_NUMBER = 2;
/*       */ 
/*       */     
/*       */     private volatile Object value_;
/*       */ 
/*       */     
/*       */     public static final int INDEX_FIELD_NUMBER = 3;
/*       */ 
/*       */     
/*       */     private int index_;
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */     
/*       */     private DocumentPathItem(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/*  3231 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*  3577 */       this.memoizedIsInitialized = -1; } private DocumentPathItem() { this.memoizedIsInitialized = -1; this.type_ = 1; this.value_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new DocumentPathItem(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private DocumentPathItem(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int rawValue; ByteString bs; Type value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: rawValue = input.readEnum(); value = Type.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(1, rawValue); continue; }  this.bitField0_ |= 0x1; this.type_ = rawValue; continue;case 18: bs = input.readBytes(); this.bitField0_ |= 0x2; this.value_ = bs; continue;case 24: this.bitField0_ |= 0x4; this.index_ = input.readUInt32(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_fieldAccessorTable.ensureFieldAccessorsInitialized(DocumentPathItem.class, Builder.class); } public enum Type implements ProtocolMessageEnum {
/*       */       MEMBER(1), MEMBER_ASTERISK(2), ARRAY_INDEX(3), ARRAY_INDEX_ASTERISK(4), DOUBLE_ASTERISK(5); public static final int MEMBER_VALUE = 1; public static final int MEMBER_ASTERISK_VALUE = 2; public static final int ARRAY_INDEX_VALUE = 3; public static final int ARRAY_INDEX_ASTERISK_VALUE = 4; public static final int DOUBLE_ASTERISK_VALUE = 5; private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() { public MysqlxExpr.DocumentPathItem.Type findValueByNumber(int number) { return MysqlxExpr.DocumentPathItem.Type.forNumber(number); } }
/*       */       ; private static final Type[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Type forNumber(int value) { switch (value) { case 1: return MEMBER;case 2: return MEMBER_ASTERISK;case 3: return ARRAY_INDEX;case 4: return ARRAY_INDEX_ASTERISK;case 5: return DOUBLE_ASTERISK; }  return null; } public static Internal.EnumLiteMap<Type> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxExpr.DocumentPathItem.getDescriptor().getEnumTypes().get(0); } Type(int value) { this.value = value; } } public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); } public Type getType() { Type result = Type.valueOf(this.type_); return (result == null) ? Type.MEMBER : result; } public boolean hasValue() { return ((this.bitField0_ & 0x2) != 0); } public String getValue() { Object ref = this.value_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.value_ = s;  return s; } public ByteString getValueBytes() { Object ref = this.value_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.value_ = b; return b; }  return (ByteString)ref; } public boolean hasIndex() { return ((this.bitField0_ & 0x4) != 0); } public int getIndex() { return this.index_; }
/*  3580 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  3581 */       if (isInitialized == 1) return true; 
/*  3582 */       if (isInitialized == 0) return false;
/*       */       
/*  3584 */       if (!hasType()) {
/*  3585 */         this.memoizedIsInitialized = 0;
/*  3586 */         return false;
/*       */       } 
/*  3588 */       this.memoizedIsInitialized = 1;
/*  3589 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/*  3595 */       if ((this.bitField0_ & 0x1) != 0) {
/*  3596 */         output.writeEnum(1, this.type_);
/*       */       }
/*  3598 */       if ((this.bitField0_ & 0x2) != 0) {
/*  3599 */         GeneratedMessageV3.writeString(output, 2, this.value_);
/*       */       }
/*  3601 */       if ((this.bitField0_ & 0x4) != 0) {
/*  3602 */         output.writeUInt32(3, this.index_);
/*       */       }
/*  3604 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/*  3609 */       int size = this.memoizedSize;
/*  3610 */       if (size != -1) return size;
/*       */       
/*  3612 */       size = 0;
/*  3613 */       if ((this.bitField0_ & 0x1) != 0) {
/*  3614 */         size += 
/*  3615 */           CodedOutputStream.computeEnumSize(1, this.type_);
/*       */       }
/*  3617 */       if ((this.bitField0_ & 0x2) != 0) {
/*  3618 */         size += GeneratedMessageV3.computeStringSize(2, this.value_);
/*       */       }
/*  3620 */       if ((this.bitField0_ & 0x4) != 0) {
/*  3621 */         size += 
/*  3622 */           CodedOutputStream.computeUInt32Size(3, this.index_);
/*       */       }
/*  3624 */       size += this.unknownFields.getSerializedSize();
/*  3625 */       this.memoizedSize = size;
/*  3626 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/*  3631 */       if (obj == this) {
/*  3632 */         return true;
/*       */       }
/*  3634 */       if (!(obj instanceof DocumentPathItem)) {
/*  3635 */         return super.equals(obj);
/*       */       }
/*  3637 */       DocumentPathItem other = (DocumentPathItem)obj;
/*       */       
/*  3639 */       if (hasType() != other.hasType()) return false; 
/*  3640 */       if (hasType() && 
/*  3641 */         this.type_ != other.type_) return false;
/*       */       
/*  3643 */       if (hasValue() != other.hasValue()) return false; 
/*  3644 */       if (hasValue() && 
/*       */         
/*  3646 */         !getValue().equals(other.getValue())) return false;
/*       */       
/*  3648 */       if (hasIndex() != other.hasIndex()) return false; 
/*  3649 */       if (hasIndex() && 
/*  3650 */         getIndex() != other
/*  3651 */         .getIndex()) return false;
/*       */       
/*  3653 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  3654 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/*  3659 */       if (this.memoizedHashCode != 0) {
/*  3660 */         return this.memoizedHashCode;
/*       */       }
/*  3662 */       int hash = 41;
/*  3663 */       hash = 19 * hash + getDescriptor().hashCode();
/*  3664 */       if (hasType()) {
/*  3665 */         hash = 37 * hash + 1;
/*  3666 */         hash = 53 * hash + this.type_;
/*       */       } 
/*  3668 */       if (hasValue()) {
/*  3669 */         hash = 37 * hash + 2;
/*  3670 */         hash = 53 * hash + getValue().hashCode();
/*       */       } 
/*  3672 */       if (hasIndex()) {
/*  3673 */         hash = 37 * hash + 3;
/*  3674 */         hash = 53 * hash + getIndex();
/*       */       } 
/*  3676 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  3677 */       this.memoizedHashCode = hash;
/*  3678 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static DocumentPathItem parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  3684 */       return (DocumentPathItem)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static DocumentPathItem parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  3690 */       return (DocumentPathItem)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static DocumentPathItem parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  3695 */       return (DocumentPathItem)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static DocumentPathItem parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  3701 */       return (DocumentPathItem)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static DocumentPathItem parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  3705 */       return (DocumentPathItem)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static DocumentPathItem parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  3711 */       return (DocumentPathItem)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static DocumentPathItem parseFrom(InputStream input) throws IOException {
/*  3715 */       return 
/*  3716 */         (DocumentPathItem)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static DocumentPathItem parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  3722 */       return 
/*  3723 */         (DocumentPathItem)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static DocumentPathItem parseDelimitedFrom(InputStream input) throws IOException {
/*  3727 */       return 
/*  3728 */         (DocumentPathItem)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static DocumentPathItem parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  3734 */       return 
/*  3735 */         (DocumentPathItem)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static DocumentPathItem parseFrom(CodedInputStream input) throws IOException {
/*  3740 */       return 
/*  3741 */         (DocumentPathItem)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static DocumentPathItem parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  3747 */       return 
/*  3748 */         (DocumentPathItem)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/*  3752 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/*  3754 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(DocumentPathItem prototype) {
/*  3757 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/*  3761 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  3762 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  3768 */       Builder builder = new Builder(parent);
/*  3769 */       return builder;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static final class Builder
/*       */       extends GeneratedMessageV3.Builder<Builder>
/*       */       implements MysqlxExpr.DocumentPathItemOrBuilder
/*       */     {
/*       */       private int bitField0_;
/*       */ 
/*       */       
/*       */       private int type_;
/*       */ 
/*       */       
/*       */       private Object value_;
/*       */ 
/*       */       
/*       */       private int index_;
/*       */ 
/*       */ 
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/*  3792 */         return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  3798 */         return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_fieldAccessorTable
/*  3799 */           .ensureFieldAccessorsInitialized(MysqlxExpr.DocumentPathItem.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/*  3960 */         this.type_ = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/*  4002 */         this.value_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.type_ = 1; this.value_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxExpr.DocumentPathItem.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.type_ = 1; this.bitField0_ &= 0xFFFFFFFE; this.value_ = ""; this.bitField0_ &= 0xFFFFFFFD; this.index_ = 0; this.bitField0_ &= 0xFFFFFFFB; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_descriptor; } public MysqlxExpr.DocumentPathItem getDefaultInstanceForType() { return MysqlxExpr.DocumentPathItem.getDefaultInstance(); } public MysqlxExpr.DocumentPathItem build() { MysqlxExpr.DocumentPathItem result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxExpr.DocumentPathItem buildPartial() { MysqlxExpr.DocumentPathItem result = new MysqlxExpr.DocumentPathItem(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.type_ = this.type_; if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.value_ = this.value_; if ((from_bitField0_ & 0x4) != 0) { result.index_ = this.index_; to_bitField0_ |= 0x4; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxExpr.DocumentPathItem) return mergeFrom((MysqlxExpr.DocumentPathItem)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxExpr.DocumentPathItem other) { if (other == MysqlxExpr.DocumentPathItem.getDefaultInstance()) return this;  if (other.hasType()) setType(other.getType());  if (other.hasValue()) { this.bitField0_ |= 0x2; this.value_ = other.value_; onChanged(); }  if (other.hasIndex()) setIndex(other.getIndex());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasType()) return false;  return true; }
/*       */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxExpr.DocumentPathItem parsedMessage = null; try { parsedMessage = (MysqlxExpr.DocumentPathItem)MysqlxExpr.DocumentPathItem.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxExpr.DocumentPathItem)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/*       */       public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); }
/*       */       public MysqlxExpr.DocumentPathItem.Type getType() { MysqlxExpr.DocumentPathItem.Type result = MysqlxExpr.DocumentPathItem.Type.valueOf(this.type_); return (result == null) ? MysqlxExpr.DocumentPathItem.Type.MEMBER : result; }
/*       */       public Builder setType(MysqlxExpr.DocumentPathItem.Type value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.type_ = value.getNumber(); onChanged(); return this; }
/*       */       public Builder clearType() { this.bitField0_ &= 0xFFFFFFFE; this.type_ = 1; onChanged(); return this; }
/*  4008 */       public boolean hasValue() { return ((this.bitField0_ & 0x2) != 0); }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public String getValue() {
/*  4015 */         Object ref = this.value_;
/*  4016 */         if (!(ref instanceof String)) {
/*  4017 */           ByteString bs = (ByteString)ref;
/*       */           
/*  4019 */           String s = bs.toStringUtf8();
/*  4020 */           if (bs.isValidUtf8()) {
/*  4021 */             this.value_ = s;
/*       */           }
/*  4023 */           return s;
/*       */         } 
/*  4025 */         return (String)ref;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public ByteString getValueBytes() {
/*  4034 */         Object ref = this.value_;
/*  4035 */         if (ref instanceof String) {
/*       */           
/*  4037 */           ByteString b = ByteString.copyFromUtf8((String)ref);
/*       */           
/*  4039 */           this.value_ = b;
/*  4040 */           return b;
/*       */         } 
/*  4042 */         return (ByteString)ref;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setValue(String value) {
/*  4052 */         if (value == null) {
/*  4053 */           throw new NullPointerException();
/*       */         }
/*  4055 */         this.bitField0_ |= 0x2;
/*  4056 */         this.value_ = value;
/*  4057 */         onChanged();
/*  4058 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearValue() {
/*  4065 */         this.bitField0_ &= 0xFFFFFFFD;
/*  4066 */         this.value_ = MysqlxExpr.DocumentPathItem.getDefaultInstance().getValue();
/*  4067 */         onChanged();
/*  4068 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setValueBytes(ByteString value) {
/*  4077 */         if (value == null) {
/*  4078 */           throw new NullPointerException();
/*       */         }
/*  4080 */         this.bitField0_ |= 0x2;
/*  4081 */         this.value_ = value;
/*  4082 */         onChanged();
/*  4083 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasIndex() {
/*  4096 */         return ((this.bitField0_ & 0x4) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public int getIndex() {
/*  4107 */         return this.index_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setIndex(int value) {
/*  4119 */         this.bitField0_ |= 0x4;
/*  4120 */         this.index_ = value;
/*  4121 */         onChanged();
/*  4122 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearIndex() {
/*  4133 */         this.bitField0_ &= 0xFFFFFFFB;
/*  4134 */         this.index_ = 0;
/*  4135 */         onChanged();
/*  4136 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  4141 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  4147 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*  4157 */     private static final DocumentPathItem DEFAULT_INSTANCE = new DocumentPathItem();
/*       */ 
/*       */     
/*       */     public static DocumentPathItem getDefaultInstance() {
/*  4161 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/*  4165 */     public static final Parser<DocumentPathItem> PARSER = (Parser<DocumentPathItem>)new AbstractParser<DocumentPathItem>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxExpr.DocumentPathItem parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/*  4171 */           return new MysqlxExpr.DocumentPathItem(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<DocumentPathItem> parser() {
/*  4176 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<DocumentPathItem> getParserForType() {
/*  4181 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public DocumentPathItem getDefaultInstanceForType() {
/*  4186 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface ColumnIdentifierOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     List<MysqlxExpr.DocumentPathItem> getDocumentPathList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.DocumentPathItem getDocumentPath(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getDocumentPathCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasName();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     String getName();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getNameBytes();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasTableName();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     String getTableName();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getTableNameBytes();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasSchemaName();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     String getSchemaName();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getSchemaNameBytes();
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class ColumnIdentifier
/*       */     extends GeneratedMessageV3
/*       */     implements ColumnIdentifierOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int DOCUMENT_PATH_FIELD_NUMBER = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private List<MysqlxExpr.DocumentPathItem> documentPath_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int NAME_FIELD_NUMBER = 2;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private volatile Object name_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int TABLE_NAME_FIELD_NUMBER = 3;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private volatile Object tableName_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int SCHEMA_NAME_FIELD_NUMBER = 4;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private volatile Object schemaName_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private ColumnIdentifier(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/*  4358 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*  4687 */       this.memoizedIsInitialized = -1; } private ColumnIdentifier() { this.memoizedIsInitialized = -1; this.documentPath_ = Collections.emptyList(); this.name_ = ""; this.tableName_ = ""; this.schemaName_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new ColumnIdentifier(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private ColumnIdentifier(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: if ((mutable_bitField0_ & 0x1) == 0) { this.documentPath_ = new ArrayList<>(); mutable_bitField0_ |= 0x1; }  this.documentPath_.add(input.readMessage(MysqlxExpr.DocumentPathItem.PARSER, extensionRegistry)); continue;case 18: bs = input.readBytes(); this.bitField0_ |= 0x1; this.name_ = bs; continue;case 26: bs = input.readBytes(); this.bitField0_ |= 0x2; this.tableName_ = bs; continue;case 34: bs = input.readBytes(); this.bitField0_ |= 0x4; this.schemaName_ = bs; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x1) != 0) this.documentPath_ = Collections.unmodifiableList(this.documentPath_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_fieldAccessorTable.ensureFieldAccessorsInitialized(ColumnIdentifier.class, Builder.class); } public List<MysqlxExpr.DocumentPathItem> getDocumentPathList() { return this.documentPath_; } public List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList() { return (List)this.documentPath_; } public int getDocumentPathCount() { return this.documentPath_.size(); } public MysqlxExpr.DocumentPathItem getDocumentPath(int index) { return this.documentPath_.get(index); } public MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int index) { return this.documentPath_.get(index); } public boolean hasName() { return ((this.bitField0_ & 0x1) != 0); } public String getName() { Object ref = this.name_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.name_ = s;  return s; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; } public boolean hasTableName() { return ((this.bitField0_ & 0x2) != 0); } public String getTableName() { Object ref = this.tableName_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.tableName_ = s;  return s; } public ByteString getTableNameBytes() { Object ref = this.tableName_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.tableName_ = b; return b; }  return (ByteString)ref; } public boolean hasSchemaName() { return ((this.bitField0_ & 0x4) != 0); }
/*       */     public String getSchemaName() { Object ref = this.schemaName_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.schemaName_ = s;  return s; }
/*       */     public ByteString getSchemaNameBytes() { Object ref = this.schemaName_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.schemaName_ = b; return b; }  return (ByteString)ref; }
/*  4690 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  4691 */       if (isInitialized == 1) return true; 
/*  4692 */       if (isInitialized == 0) return false;
/*       */       
/*  4694 */       for (int i = 0; i < getDocumentPathCount(); i++) {
/*  4695 */         if (!getDocumentPath(i).isInitialized()) {
/*  4696 */           this.memoizedIsInitialized = 0;
/*  4697 */           return false;
/*       */         } 
/*       */       } 
/*  4700 */       this.memoizedIsInitialized = 1;
/*  4701 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/*  4707 */       for (int i = 0; i < this.documentPath_.size(); i++) {
/*  4708 */         output.writeMessage(1, (MessageLite)this.documentPath_.get(i));
/*       */       }
/*  4710 */       if ((this.bitField0_ & 0x1) != 0) {
/*  4711 */         GeneratedMessageV3.writeString(output, 2, this.name_);
/*       */       }
/*  4713 */       if ((this.bitField0_ & 0x2) != 0) {
/*  4714 */         GeneratedMessageV3.writeString(output, 3, this.tableName_);
/*       */       }
/*  4716 */       if ((this.bitField0_ & 0x4) != 0) {
/*  4717 */         GeneratedMessageV3.writeString(output, 4, this.schemaName_);
/*       */       }
/*  4719 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/*  4724 */       int size = this.memoizedSize;
/*  4725 */       if (size != -1) return size;
/*       */       
/*  4727 */       size = 0;
/*  4728 */       for (int i = 0; i < this.documentPath_.size(); i++) {
/*  4729 */         size += 
/*  4730 */           CodedOutputStream.computeMessageSize(1, (MessageLite)this.documentPath_.get(i));
/*       */       }
/*  4732 */       if ((this.bitField0_ & 0x1) != 0) {
/*  4733 */         size += GeneratedMessageV3.computeStringSize(2, this.name_);
/*       */       }
/*  4735 */       if ((this.bitField0_ & 0x2) != 0) {
/*  4736 */         size += GeneratedMessageV3.computeStringSize(3, this.tableName_);
/*       */       }
/*  4738 */       if ((this.bitField0_ & 0x4) != 0) {
/*  4739 */         size += GeneratedMessageV3.computeStringSize(4, this.schemaName_);
/*       */       }
/*  4741 */       size += this.unknownFields.getSerializedSize();
/*  4742 */       this.memoizedSize = size;
/*  4743 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/*  4748 */       if (obj == this) {
/*  4749 */         return true;
/*       */       }
/*  4751 */       if (!(obj instanceof ColumnIdentifier)) {
/*  4752 */         return super.equals(obj);
/*       */       }
/*  4754 */       ColumnIdentifier other = (ColumnIdentifier)obj;
/*       */ 
/*       */       
/*  4757 */       if (!getDocumentPathList().equals(other.getDocumentPathList())) return false; 
/*  4758 */       if (hasName() != other.hasName()) return false; 
/*  4759 */       if (hasName() && 
/*       */         
/*  4761 */         !getName().equals(other.getName())) return false;
/*       */       
/*  4763 */       if (hasTableName() != other.hasTableName()) return false; 
/*  4764 */       if (hasTableName() && 
/*       */         
/*  4766 */         !getTableName().equals(other.getTableName())) return false;
/*       */       
/*  4768 */       if (hasSchemaName() != other.hasSchemaName()) return false; 
/*  4769 */       if (hasSchemaName() && 
/*       */         
/*  4771 */         !getSchemaName().equals(other.getSchemaName())) return false;
/*       */       
/*  4773 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  4774 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/*  4779 */       if (this.memoizedHashCode != 0) {
/*  4780 */         return this.memoizedHashCode;
/*       */       }
/*  4782 */       int hash = 41;
/*  4783 */       hash = 19 * hash + getDescriptor().hashCode();
/*  4784 */       if (getDocumentPathCount() > 0) {
/*  4785 */         hash = 37 * hash + 1;
/*  4786 */         hash = 53 * hash + getDocumentPathList().hashCode();
/*       */       } 
/*  4788 */       if (hasName()) {
/*  4789 */         hash = 37 * hash + 2;
/*  4790 */         hash = 53 * hash + getName().hashCode();
/*       */       } 
/*  4792 */       if (hasTableName()) {
/*  4793 */         hash = 37 * hash + 3;
/*  4794 */         hash = 53 * hash + getTableName().hashCode();
/*       */       } 
/*  4796 */       if (hasSchemaName()) {
/*  4797 */         hash = 37 * hash + 4;
/*  4798 */         hash = 53 * hash + getSchemaName().hashCode();
/*       */       } 
/*  4800 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  4801 */       this.memoizedHashCode = hash;
/*  4802 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static ColumnIdentifier parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  4808 */       return (ColumnIdentifier)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static ColumnIdentifier parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  4814 */       return (ColumnIdentifier)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static ColumnIdentifier parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  4819 */       return (ColumnIdentifier)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static ColumnIdentifier parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  4825 */       return (ColumnIdentifier)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static ColumnIdentifier parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  4829 */       return (ColumnIdentifier)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static ColumnIdentifier parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  4835 */       return (ColumnIdentifier)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static ColumnIdentifier parseFrom(InputStream input) throws IOException {
/*  4839 */       return 
/*  4840 */         (ColumnIdentifier)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static ColumnIdentifier parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  4846 */       return 
/*  4847 */         (ColumnIdentifier)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static ColumnIdentifier parseDelimitedFrom(InputStream input) throws IOException {
/*  4851 */       return 
/*  4852 */         (ColumnIdentifier)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static ColumnIdentifier parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  4858 */       return 
/*  4859 */         (ColumnIdentifier)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static ColumnIdentifier parseFrom(CodedInputStream input) throws IOException {
/*  4864 */       return 
/*  4865 */         (ColumnIdentifier)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static ColumnIdentifier parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  4871 */       return 
/*  4872 */         (ColumnIdentifier)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/*  4876 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/*  4878 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(ColumnIdentifier prototype) {
/*  4881 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/*  4885 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  4886 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  4892 */       Builder builder = new Builder(parent);
/*  4893 */       return builder;
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final class Builder
/*       */       extends GeneratedMessageV3.Builder<Builder>
/*       */       implements MysqlxExpr.ColumnIdentifierOrBuilder
/*       */     {
/*       */       private int bitField0_;
/*       */ 
/*       */ 
/*       */       
/*       */       private List<MysqlxExpr.DocumentPathItem> documentPath_;
/*       */ 
/*       */ 
/*       */       
/*       */       private RepeatedFieldBuilderV3<MysqlxExpr.DocumentPathItem, MysqlxExpr.DocumentPathItem.Builder, MysqlxExpr.DocumentPathItemOrBuilder> documentPathBuilder_;
/*       */ 
/*       */ 
/*       */       
/*       */       private Object name_;
/*       */ 
/*       */       
/*       */       private Object tableName_;
/*       */ 
/*       */       
/*       */       private Object schemaName_;
/*       */ 
/*       */ 
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/*  4926 */         return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  4932 */         return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_fieldAccessorTable
/*  4933 */           .ensureFieldAccessorsInitialized(MysqlxExpr.ColumnIdentifier.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/*  5142 */         this
/*  5143 */           .documentPath_ = Collections.emptyList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/*  5454 */         this.name_ = "";
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/*  5562 */         this.tableName_ = "";
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/*  5670 */         this.schemaName_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.documentPath_ = Collections.emptyList(); this.name_ = ""; this.tableName_ = ""; this.schemaName_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxExpr.ColumnIdentifier.alwaysUseFieldBuilders) getDocumentPathFieldBuilder();  } public Builder clear() { super.clear(); if (this.documentPathBuilder_ == null) { this.documentPath_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; } else { this.documentPathBuilder_.clear(); }  this.name_ = ""; this.bitField0_ &= 0xFFFFFFFD; this.tableName_ = ""; this.bitField0_ &= 0xFFFFFFFB; this.schemaName_ = ""; this.bitField0_ &= 0xFFFFFFF7; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor; } public MysqlxExpr.ColumnIdentifier getDefaultInstanceForType() { return MysqlxExpr.ColumnIdentifier.getDefaultInstance(); } public MysqlxExpr.ColumnIdentifier build() { MysqlxExpr.ColumnIdentifier result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxExpr.ColumnIdentifier buildPartial() { MysqlxExpr.ColumnIdentifier result = new MysqlxExpr.ColumnIdentifier(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if (this.documentPathBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0) { this.documentPath_ = Collections.unmodifiableList(this.documentPath_); this.bitField0_ &= 0xFFFFFFFE; }  result.documentPath_ = this.documentPath_; } else { result.documentPath_ = this.documentPathBuilder_.build(); }  if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x1;  result.name_ = this.name_; if ((from_bitField0_ & 0x4) != 0) to_bitField0_ |= 0x2;  result.tableName_ = this.tableName_; if ((from_bitField0_ & 0x8) != 0) to_bitField0_ |= 0x4;  result.schemaName_ = this.schemaName_; result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxExpr.ColumnIdentifier) return mergeFrom((MysqlxExpr.ColumnIdentifier)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxExpr.ColumnIdentifier other) { if (other == MysqlxExpr.ColumnIdentifier.getDefaultInstance()) return this;  if (this.documentPathBuilder_ == null) { if (!other.documentPath_.isEmpty()) { if (this.documentPath_.isEmpty()) { this.documentPath_ = other.documentPath_; this.bitField0_ &= 0xFFFFFFFE; } else { ensureDocumentPathIsMutable(); this.documentPath_.addAll(other.documentPath_); }  onChanged(); }  } else if (!other.documentPath_.isEmpty()) { if (this.documentPathBuilder_.isEmpty()) { this.documentPathBuilder_.dispose(); this.documentPathBuilder_ = null; this.documentPath_ = other.documentPath_; this.bitField0_ &= 0xFFFFFFFE; this.documentPathBuilder_ = MysqlxExpr.ColumnIdentifier.alwaysUseFieldBuilders ? getDocumentPathFieldBuilder() : null; } else { this.documentPathBuilder_.addAllMessages(other.documentPath_); }  }  if (other.hasName()) { this.bitField0_ |= 0x2; this.name_ = other.name_; onChanged(); }  if (other.hasTableName()) { this.bitField0_ |= 0x4; this.tableName_ = other.tableName_; onChanged(); }  if (other.hasSchemaName()) { this.bitField0_ |= 0x8; this.schemaName_ = other.schemaName_; onChanged(); }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { for (int i = 0; i < getDocumentPathCount(); i++) { if (!getDocumentPath(i).isInitialized()) return false;  }  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxExpr.ColumnIdentifier parsedMessage = null; try { parsedMessage = (MysqlxExpr.ColumnIdentifier)MysqlxExpr.ColumnIdentifier.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxExpr.ColumnIdentifier)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } private void ensureDocumentPathIsMutable() { if ((this.bitField0_ & 0x1) == 0) { this.documentPath_ = new ArrayList<>(this.documentPath_); this.bitField0_ |= 0x1; }  } public List<MysqlxExpr.DocumentPathItem> getDocumentPathList() { if (this.documentPathBuilder_ == null) return Collections.unmodifiableList(this.documentPath_);  return this.documentPathBuilder_.getMessageList(); } public int getDocumentPathCount() { if (this.documentPathBuilder_ == null) return this.documentPath_.size();  return this.documentPathBuilder_.getCount(); } public MysqlxExpr.DocumentPathItem getDocumentPath(int index) { if (this.documentPathBuilder_ == null) return this.documentPath_.get(index);  return (MysqlxExpr.DocumentPathItem)this.documentPathBuilder_.getMessage(index); } public Builder setDocumentPath(int index, MysqlxExpr.DocumentPathItem value) { if (this.documentPathBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureDocumentPathIsMutable(); this.documentPath_.set(index, value); onChanged(); } else { this.documentPathBuilder_.setMessage(index, (AbstractMessage)value); }  return this; } public Builder setDocumentPath(int index, MysqlxExpr.DocumentPathItem.Builder builderForValue) { if (this.documentPathBuilder_ == null) { ensureDocumentPathIsMutable(); this.documentPath_.set(index, builderForValue.build()); onChanged(); } else { this.documentPathBuilder_.setMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addDocumentPath(MysqlxExpr.DocumentPathItem value) { if (this.documentPathBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureDocumentPathIsMutable(); this.documentPath_.add(value); onChanged(); } else { this.documentPathBuilder_.addMessage((AbstractMessage)value); }  return this; } public Builder addDocumentPath(int index, MysqlxExpr.DocumentPathItem value) { if (this.documentPathBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureDocumentPathIsMutable(); this.documentPath_.add(index, value); onChanged(); } else { this.documentPathBuilder_.addMessage(index, (AbstractMessage)value); }  return this; } public Builder addDocumentPath(MysqlxExpr.DocumentPathItem.Builder builderForValue) { if (this.documentPathBuilder_ == null) { ensureDocumentPathIsMutable(); this.documentPath_.add(builderForValue.build()); onChanged(); } else { this.documentPathBuilder_.addMessage((AbstractMessage)builderForValue.build()); }  return this; } public Builder addDocumentPath(int index, MysqlxExpr.DocumentPathItem.Builder builderForValue) { if (this.documentPathBuilder_ == null) { ensureDocumentPathIsMutable(); this.documentPath_.add(index, builderForValue.build()); onChanged(); } else { this.documentPathBuilder_.addMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addAllDocumentPath(Iterable<? extends MysqlxExpr.DocumentPathItem> values) { if (this.documentPathBuilder_ == null) { ensureDocumentPathIsMutable(); AbstractMessageLite.Builder.addAll(values, this.documentPath_); onChanged(); } else { this.documentPathBuilder_.addAllMessages(values); }  return this; } public Builder clearDocumentPath() { if (this.documentPathBuilder_ == null) { this.documentPath_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; onChanged(); } else { this.documentPathBuilder_.clear(); }  return this; } public Builder removeDocumentPath(int index) { if (this.documentPathBuilder_ == null) { ensureDocumentPathIsMutable(); this.documentPath_.remove(index); onChanged(); } else { this.documentPathBuilder_.remove(index); }  return this; } public MysqlxExpr.DocumentPathItem.Builder getDocumentPathBuilder(int index) { return (MysqlxExpr.DocumentPathItem.Builder)getDocumentPathFieldBuilder().getBuilder(index); } public MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int index) { if (this.documentPathBuilder_ == null) return this.documentPath_.get(index);  return (MysqlxExpr.DocumentPathItemOrBuilder)this.documentPathBuilder_.getMessageOrBuilder(index); } public List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList() { if (this.documentPathBuilder_ != null) return this.documentPathBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.documentPath_); } public MysqlxExpr.DocumentPathItem.Builder addDocumentPathBuilder() { return (MysqlxExpr.DocumentPathItem.Builder)getDocumentPathFieldBuilder().addBuilder((AbstractMessage)MysqlxExpr.DocumentPathItem.getDefaultInstance()); } public MysqlxExpr.DocumentPathItem.Builder addDocumentPathBuilder(int index) { return (MysqlxExpr.DocumentPathItem.Builder)getDocumentPathFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxExpr.DocumentPathItem.getDefaultInstance()); } public List<MysqlxExpr.DocumentPathItem.Builder> getDocumentPathBuilderList() { return getDocumentPathFieldBuilder().getBuilderList(); } private RepeatedFieldBuilderV3<MysqlxExpr.DocumentPathItem, MysqlxExpr.DocumentPathItem.Builder, MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathFieldBuilder() { if (this.documentPathBuilder_ == null) { this.documentPathBuilder_ = new RepeatedFieldBuilderV3(this.documentPath_, ((this.bitField0_ & 0x1) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.documentPath_ = null; }  return this.documentPathBuilder_; } public boolean hasName() { return ((this.bitField0_ & 0x2) != 0); } public String getName() { Object ref = this.name_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.name_ = s;  return s; }  return (String)ref; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; }
/*       */       public Builder setName(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.name_ = value; onChanged(); return this; }
/*       */       public Builder clearName() { this.bitField0_ &= 0xFFFFFFFD; this.name_ = MysqlxExpr.ColumnIdentifier.getDefaultInstance().getName(); onChanged(); return this; }
/*       */       public Builder setNameBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.name_ = value; onChanged(); return this; }
/*       */       public boolean hasTableName() { return ((this.bitField0_ & 0x4) != 0); }
/*       */       public String getTableName() { Object ref = this.tableName_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.tableName_ = s;  return s; }  return (String)ref; }
/*       */       public ByteString getTableNameBytes() { Object ref = this.tableName_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.tableName_ = b; return b; }  return (ByteString)ref; }
/*       */       public Builder setTableName(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x4; this.tableName_ = value; onChanged(); return this; }
/*       */       public Builder clearTableName() { this.bitField0_ &= 0xFFFFFFFB; this.tableName_ = MysqlxExpr.ColumnIdentifier.getDefaultInstance().getTableName(); onChanged(); return this; }
/*       */       public Builder setTableNameBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x4; this.tableName_ = value; onChanged(); return this; }
/*  5680 */       public boolean hasSchemaName() { return ((this.bitField0_ & 0x8) != 0); }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public String getSchemaName() {
/*  5691 */         Object ref = this.schemaName_;
/*  5692 */         if (!(ref instanceof String)) {
/*  5693 */           ByteString bs = (ByteString)ref;
/*       */           
/*  5695 */           String s = bs.toStringUtf8();
/*  5696 */           if (bs.isValidUtf8()) {
/*  5697 */             this.schemaName_ = s;
/*       */           }
/*  5699 */           return s;
/*       */         } 
/*  5701 */         return (String)ref;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public ByteString getSchemaNameBytes() {
/*  5714 */         Object ref = this.schemaName_;
/*  5715 */         if (ref instanceof String) {
/*       */           
/*  5717 */           ByteString b = ByteString.copyFromUtf8((String)ref);
/*       */           
/*  5719 */           this.schemaName_ = b;
/*  5720 */           return b;
/*       */         } 
/*  5722 */         return (ByteString)ref;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setSchemaName(String value) {
/*  5736 */         if (value == null) {
/*  5737 */           throw new NullPointerException();
/*       */         }
/*  5739 */         this.bitField0_ |= 0x8;
/*  5740 */         this.schemaName_ = value;
/*  5741 */         onChanged();
/*  5742 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearSchemaName() {
/*  5753 */         this.bitField0_ &= 0xFFFFFFF7;
/*  5754 */         this.schemaName_ = MysqlxExpr.ColumnIdentifier.getDefaultInstance().getSchemaName();
/*  5755 */         onChanged();
/*  5756 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setSchemaNameBytes(ByteString value) {
/*  5769 */         if (value == null) {
/*  5770 */           throw new NullPointerException();
/*       */         }
/*  5772 */         this.bitField0_ |= 0x8;
/*  5773 */         this.schemaName_ = value;
/*  5774 */         onChanged();
/*  5775 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  5780 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  5786 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*  5796 */     private static final ColumnIdentifier DEFAULT_INSTANCE = new ColumnIdentifier();
/*       */ 
/*       */     
/*       */     public static ColumnIdentifier getDefaultInstance() {
/*  5800 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/*  5804 */     public static final Parser<ColumnIdentifier> PARSER = (Parser<ColumnIdentifier>)new AbstractParser<ColumnIdentifier>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxExpr.ColumnIdentifier parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/*  5810 */           return new MysqlxExpr.ColumnIdentifier(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<ColumnIdentifier> parser() {
/*  5815 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<ColumnIdentifier> getParserForType() {
/*  5820 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public ColumnIdentifier getDefaultInstanceForType() {
/*  5825 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface FunctionCallOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasName();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Identifier getName();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.IdentifierOrBuilder getNameOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<MysqlxExpr.Expr> getParamList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Expr getParam(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getParamCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ExprOrBuilder getParamOrBuilder(int param1Int);
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class FunctionCall
/*       */     extends GeneratedMessageV3
/*       */     implements FunctionCallOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int NAME_FIELD_NUMBER = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxExpr.Identifier name_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int PARAM_FIELD_NUMBER = 2;
/*       */ 
/*       */ 
/*       */     
/*       */     private List<MysqlxExpr.Expr> param_;
/*       */ 
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private FunctionCall(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/*  5924 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*  6109 */       this.memoizedIsInitialized = -1; } private FunctionCall() { this.memoizedIsInitialized = -1; this.param_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new FunctionCall(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private FunctionCall(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { MysqlxExpr.Identifier.Builder subBuilder; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: subBuilder = null; if ((this.bitField0_ & 0x1) != 0) subBuilder = this.name_.toBuilder();  this.name_ = (MysqlxExpr.Identifier)input.readMessage(MysqlxExpr.Identifier.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.name_); this.name_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x1; continue;case 18: if ((mutable_bitField0_ & 0x2) == 0) { this.param_ = new ArrayList<>(); mutable_bitField0_ |= 0x2; }  this.param_.add(input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry)); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x2) != 0) this.param_ = Collections.unmodifiableList(this.param_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_fieldAccessorTable.ensureFieldAccessorsInitialized(FunctionCall.class, Builder.class); } public boolean hasName() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxExpr.Identifier getName() { return (this.name_ == null) ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_; } public MysqlxExpr.IdentifierOrBuilder getNameOrBuilder() { return (this.name_ == null) ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_; } public List<MysqlxExpr.Expr> getParamList() { return this.param_; } public List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList() { return (List)this.param_; } public int getParamCount() { return this.param_.size(); }
/*       */     public MysqlxExpr.Expr getParam(int index) { return this.param_.get(index); }
/*       */     public MysqlxExpr.ExprOrBuilder getParamOrBuilder(int index) { return this.param_.get(index); }
/*  6112 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  6113 */       if (isInitialized == 1) return true; 
/*  6114 */       if (isInitialized == 0) return false;
/*       */       
/*  6116 */       if (!hasName()) {
/*  6117 */         this.memoizedIsInitialized = 0;
/*  6118 */         return false;
/*       */       } 
/*  6120 */       if (!getName().isInitialized()) {
/*  6121 */         this.memoizedIsInitialized = 0;
/*  6122 */         return false;
/*       */       } 
/*  6124 */       for (int i = 0; i < getParamCount(); i++) {
/*  6125 */         if (!getParam(i).isInitialized()) {
/*  6126 */           this.memoizedIsInitialized = 0;
/*  6127 */           return false;
/*       */         } 
/*       */       } 
/*  6130 */       this.memoizedIsInitialized = 1;
/*  6131 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/*  6137 */       if ((this.bitField0_ & 0x1) != 0) {
/*  6138 */         output.writeMessage(1, (MessageLite)getName());
/*       */       }
/*  6140 */       for (int i = 0; i < this.param_.size(); i++) {
/*  6141 */         output.writeMessage(2, (MessageLite)this.param_.get(i));
/*       */       }
/*  6143 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/*  6148 */       int size = this.memoizedSize;
/*  6149 */       if (size != -1) return size;
/*       */       
/*  6151 */       size = 0;
/*  6152 */       if ((this.bitField0_ & 0x1) != 0) {
/*  6153 */         size += 
/*  6154 */           CodedOutputStream.computeMessageSize(1, (MessageLite)getName());
/*       */       }
/*  6156 */       for (int i = 0; i < this.param_.size(); i++) {
/*  6157 */         size += 
/*  6158 */           CodedOutputStream.computeMessageSize(2, (MessageLite)this.param_.get(i));
/*       */       }
/*  6160 */       size += this.unknownFields.getSerializedSize();
/*  6161 */       this.memoizedSize = size;
/*  6162 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/*  6167 */       if (obj == this) {
/*  6168 */         return true;
/*       */       }
/*  6170 */       if (!(obj instanceof FunctionCall)) {
/*  6171 */         return super.equals(obj);
/*       */       }
/*  6173 */       FunctionCall other = (FunctionCall)obj;
/*       */       
/*  6175 */       if (hasName() != other.hasName()) return false; 
/*  6176 */       if (hasName() && 
/*       */         
/*  6178 */         !getName().equals(other.getName())) return false;
/*       */ 
/*       */       
/*  6181 */       if (!getParamList().equals(other.getParamList())) return false; 
/*  6182 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  6183 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/*  6188 */       if (this.memoizedHashCode != 0) {
/*  6189 */         return this.memoizedHashCode;
/*       */       }
/*  6191 */       int hash = 41;
/*  6192 */       hash = 19 * hash + getDescriptor().hashCode();
/*  6193 */       if (hasName()) {
/*  6194 */         hash = 37 * hash + 1;
/*  6195 */         hash = 53 * hash + getName().hashCode();
/*       */       } 
/*  6197 */       if (getParamCount() > 0) {
/*  6198 */         hash = 37 * hash + 2;
/*  6199 */         hash = 53 * hash + getParamList().hashCode();
/*       */       } 
/*  6201 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  6202 */       this.memoizedHashCode = hash;
/*  6203 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static FunctionCall parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  6209 */       return (FunctionCall)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static FunctionCall parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  6215 */       return (FunctionCall)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static FunctionCall parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  6220 */       return (FunctionCall)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static FunctionCall parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  6226 */       return (FunctionCall)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static FunctionCall parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  6230 */       return (FunctionCall)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static FunctionCall parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  6236 */       return (FunctionCall)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static FunctionCall parseFrom(InputStream input) throws IOException {
/*  6240 */       return 
/*  6241 */         (FunctionCall)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static FunctionCall parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  6247 */       return 
/*  6248 */         (FunctionCall)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static FunctionCall parseDelimitedFrom(InputStream input) throws IOException {
/*  6252 */       return 
/*  6253 */         (FunctionCall)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static FunctionCall parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  6259 */       return 
/*  6260 */         (FunctionCall)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static FunctionCall parseFrom(CodedInputStream input) throws IOException {
/*  6265 */       return 
/*  6266 */         (FunctionCall)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static FunctionCall parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  6272 */       return 
/*  6273 */         (FunctionCall)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/*  6277 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/*  6279 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(FunctionCall prototype) {
/*  6282 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/*  6286 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  6287 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  6293 */       Builder builder = new Builder(parent);
/*  6294 */       return builder;
/*       */     }
/*       */ 
/*       */     
/*       */     public static final class Builder
/*       */       extends GeneratedMessageV3.Builder<Builder>
/*       */       implements MysqlxExpr.FunctionCallOrBuilder
/*       */     {
/*       */       private int bitField0_;
/*       */       
/*       */       private MysqlxExpr.Identifier name_;
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Identifier, MysqlxExpr.Identifier.Builder, MysqlxExpr.IdentifierOrBuilder> nameBuilder_;
/*       */       
/*       */       private List<MysqlxExpr.Expr> param_;
/*       */       
/*       */       private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> paramBuilder_;
/*       */ 
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/*  6314 */         return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  6320 */         return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_fieldAccessorTable
/*  6321 */           .ensureFieldAccessorsInitialized(MysqlxExpr.FunctionCall.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/*  6677 */         this
/*  6678 */           .param_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.param_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxExpr.FunctionCall.alwaysUseFieldBuilders) { getNameFieldBuilder(); getParamFieldBuilder(); }  } public Builder clear() { super.clear(); if (this.nameBuilder_ == null) { this.name_ = null; } else { this.nameBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; if (this.paramBuilder_ == null) { this.param_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFD; } else { this.paramBuilder_.clear(); }  return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_descriptor; } public MysqlxExpr.FunctionCall getDefaultInstanceForType() { return MysqlxExpr.FunctionCall.getDefaultInstance(); } public MysqlxExpr.FunctionCall build() { MysqlxExpr.FunctionCall result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxExpr.FunctionCall buildPartial() { MysqlxExpr.FunctionCall result = new MysqlxExpr.FunctionCall(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { if (this.nameBuilder_ == null) { result.name_ = this.name_; } else { result.name_ = (MysqlxExpr.Identifier)this.nameBuilder_.build(); }  to_bitField0_ |= 0x1; }  if (this.paramBuilder_ == null) { if ((this.bitField0_ & 0x2) != 0) { this.param_ = Collections.unmodifiableList(this.param_); this.bitField0_ &= 0xFFFFFFFD; }  result.param_ = this.param_; } else { result.param_ = this.paramBuilder_.build(); }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxExpr.FunctionCall) return mergeFrom((MysqlxExpr.FunctionCall)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxExpr.FunctionCall other) { if (other == MysqlxExpr.FunctionCall.getDefaultInstance()) return this;  if (other.hasName()) mergeName(other.getName());  if (this.paramBuilder_ == null) { if (!other.param_.isEmpty()) { if (this.param_.isEmpty()) { this.param_ = other.param_; this.bitField0_ &= 0xFFFFFFFD; } else { ensureParamIsMutable(); this.param_.addAll(other.param_); }  onChanged(); }  } else if (!other.param_.isEmpty()) { if (this.paramBuilder_.isEmpty()) { this.paramBuilder_.dispose(); this.paramBuilder_ = null; this.param_ = other.param_; this.bitField0_ &= 0xFFFFFFFD; this.paramBuilder_ = MysqlxExpr.FunctionCall.alwaysUseFieldBuilders ? getParamFieldBuilder() : null; } else { this.paramBuilder_.addAllMessages(other.param_); }  }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasName()) return false;  if (!getName().isInitialized()) return false;  for (int i = 0; i < getParamCount(); i++) { if (!getParam(i).isInitialized()) return false;  }  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxExpr.FunctionCall parsedMessage = null; try { parsedMessage = (MysqlxExpr.FunctionCall)MysqlxExpr.FunctionCall.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxExpr.FunctionCall)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasName() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxExpr.Identifier getName() { if (this.nameBuilder_ == null) return (this.name_ == null) ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_;  return (MysqlxExpr.Identifier)this.nameBuilder_.getMessage(); } public Builder setName(MysqlxExpr.Identifier value) { if (this.nameBuilder_ == null) { if (value == null) throw new NullPointerException();  this.name_ = value; onChanged(); } else { this.nameBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; } public Builder setName(MysqlxExpr.Identifier.Builder builderForValue) { if (this.nameBuilder_ == null) { this.name_ = builderForValue.build(); onChanged(); } else { this.nameBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x1; return this; } public Builder mergeName(MysqlxExpr.Identifier value) { if (this.nameBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0 && this.name_ != null && this.name_ != MysqlxExpr.Identifier.getDefaultInstance()) { this.name_ = MysqlxExpr.Identifier.newBuilder(this.name_).mergeFrom(value).buildPartial(); } else { this.name_ = value; }  onChanged(); } else { this.nameBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; } public Builder clearName() { if (this.nameBuilder_ == null) { this.name_ = null; onChanged(); } else { this.nameBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; return this; } public MysqlxExpr.Identifier.Builder getNameBuilder() { this.bitField0_ |= 0x1; onChanged(); return (MysqlxExpr.Identifier.Builder)getNameFieldBuilder().getBuilder(); } public MysqlxExpr.IdentifierOrBuilder getNameOrBuilder() { if (this.nameBuilder_ != null) return (MysqlxExpr.IdentifierOrBuilder)this.nameBuilder_.getMessageOrBuilder();  return (this.name_ == null) ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_; }
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Identifier, MysqlxExpr.Identifier.Builder, MysqlxExpr.IdentifierOrBuilder> getNameFieldBuilder() { if (this.nameBuilder_ == null) { this.nameBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getName(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.name_ = null; }  return this.nameBuilder_; }
/*  6680 */       private void ensureParamIsMutable() { if ((this.bitField0_ & 0x2) == 0) {
/*  6681 */           this.param_ = new ArrayList<>(this.param_);
/*  6682 */           this.bitField0_ |= 0x2;
/*       */         }  }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<MysqlxExpr.Expr> getParamList() {
/*  6697 */         if (this.paramBuilder_ == null) {
/*  6698 */           return Collections.unmodifiableList(this.param_);
/*       */         }
/*  6700 */         return this.paramBuilder_.getMessageList();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public int getParamCount() {
/*  6711 */         if (this.paramBuilder_ == null) {
/*  6712 */           return this.param_.size();
/*       */         }
/*  6714 */         return this.paramBuilder_.getCount();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr getParam(int index) {
/*  6725 */         if (this.paramBuilder_ == null) {
/*  6726 */           return this.param_.get(index);
/*       */         }
/*  6728 */         return (MysqlxExpr.Expr)this.paramBuilder_.getMessage(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setParam(int index, MysqlxExpr.Expr value) {
/*  6740 */         if (this.paramBuilder_ == null) {
/*  6741 */           if (value == null) {
/*  6742 */             throw new NullPointerException();
/*       */           }
/*  6744 */           ensureParamIsMutable();
/*  6745 */           this.param_.set(index, value);
/*  6746 */           onChanged();
/*       */         } else {
/*  6748 */           this.paramBuilder_.setMessage(index, (AbstractMessage)value);
/*       */         } 
/*  6750 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setParam(int index, MysqlxExpr.Expr.Builder builderForValue) {
/*  6761 */         if (this.paramBuilder_ == null) {
/*  6762 */           ensureParamIsMutable();
/*  6763 */           this.param_.set(index, builderForValue.build());
/*  6764 */           onChanged();
/*       */         } else {
/*  6766 */           this.paramBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/*  6768 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addParam(MysqlxExpr.Expr value) {
/*  6778 */         if (this.paramBuilder_ == null) {
/*  6779 */           if (value == null) {
/*  6780 */             throw new NullPointerException();
/*       */           }
/*  6782 */           ensureParamIsMutable();
/*  6783 */           this.param_.add(value);
/*  6784 */           onChanged();
/*       */         } else {
/*  6786 */           this.paramBuilder_.addMessage((AbstractMessage)value);
/*       */         } 
/*  6788 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addParam(int index, MysqlxExpr.Expr value) {
/*  6799 */         if (this.paramBuilder_ == null) {
/*  6800 */           if (value == null) {
/*  6801 */             throw new NullPointerException();
/*       */           }
/*  6803 */           ensureParamIsMutable();
/*  6804 */           this.param_.add(index, value);
/*  6805 */           onChanged();
/*       */         } else {
/*  6807 */           this.paramBuilder_.addMessage(index, (AbstractMessage)value);
/*       */         } 
/*  6809 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addParam(MysqlxExpr.Expr.Builder builderForValue) {
/*  6820 */         if (this.paramBuilder_ == null) {
/*  6821 */           ensureParamIsMutable();
/*  6822 */           this.param_.add(builderForValue.build());
/*  6823 */           onChanged();
/*       */         } else {
/*  6825 */           this.paramBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/*  6827 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addParam(int index, MysqlxExpr.Expr.Builder builderForValue) {
/*  6838 */         if (this.paramBuilder_ == null) {
/*  6839 */           ensureParamIsMutable();
/*  6840 */           this.param_.add(index, builderForValue.build());
/*  6841 */           onChanged();
/*       */         } else {
/*  6843 */           this.paramBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/*  6845 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addAllParam(Iterable<? extends MysqlxExpr.Expr> values) {
/*  6856 */         if (this.paramBuilder_ == null) {
/*  6857 */           ensureParamIsMutable();
/*  6858 */           AbstractMessageLite.Builder.addAll(values, this.param_);
/*       */           
/*  6860 */           onChanged();
/*       */         } else {
/*  6862 */           this.paramBuilder_.addAllMessages(values);
/*       */         } 
/*  6864 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearParam() {
/*  6874 */         if (this.paramBuilder_ == null) {
/*  6875 */           this.param_ = Collections.emptyList();
/*  6876 */           this.bitField0_ &= 0xFFFFFFFD;
/*  6877 */           onChanged();
/*       */         } else {
/*  6879 */           this.paramBuilder_.clear();
/*       */         } 
/*  6881 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder removeParam(int index) {
/*  6891 */         if (this.paramBuilder_ == null) {
/*  6892 */           ensureParamIsMutable();
/*  6893 */           this.param_.remove(index);
/*  6894 */           onChanged();
/*       */         } else {
/*  6896 */           this.paramBuilder_.remove(index);
/*       */         } 
/*  6898 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr.Builder getParamBuilder(int index) {
/*  6909 */         return (MysqlxExpr.Expr.Builder)getParamFieldBuilder().getBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.ExprOrBuilder getParamOrBuilder(int index) {
/*  6920 */         if (this.paramBuilder_ == null)
/*  6921 */           return this.param_.get(index); 
/*  6922 */         return (MysqlxExpr.ExprOrBuilder)this.paramBuilder_.getMessageOrBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList() {
/*  6934 */         if (this.paramBuilder_ != null) {
/*  6935 */           return this.paramBuilder_.getMessageOrBuilderList();
/*       */         }
/*  6937 */         return Collections.unmodifiableList((List)this.param_);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr.Builder addParamBuilder() {
/*  6948 */         return (MysqlxExpr.Expr.Builder)getParamFieldBuilder().addBuilder(
/*  6949 */             (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr.Builder addParamBuilder(int index) {
/*  6960 */         return (MysqlxExpr.Expr.Builder)getParamFieldBuilder().addBuilder(index, 
/*  6961 */             (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<MysqlxExpr.Expr.Builder> getParamBuilderList() {
/*  6972 */         return getParamFieldBuilder().getBuilderList();
/*       */       }
/*       */ 
/*       */       
/*       */       private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getParamFieldBuilder() {
/*  6977 */         if (this.paramBuilder_ == null) {
/*  6978 */           this
/*       */ 
/*       */ 
/*       */ 
/*       */             
/*  6983 */             .paramBuilder_ = new RepeatedFieldBuilderV3(this.param_, ((this.bitField0_ & 0x2) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/*  6984 */           this.param_ = null;
/*       */         } 
/*  6986 */         return this.paramBuilder_;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  6991 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  6997 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*  7007 */     private static final FunctionCall DEFAULT_INSTANCE = new FunctionCall();
/*       */ 
/*       */     
/*       */     public static FunctionCall getDefaultInstance() {
/*  7011 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/*  7015 */     public static final Parser<FunctionCall> PARSER = (Parser<FunctionCall>)new AbstractParser<FunctionCall>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxExpr.FunctionCall parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/*  7021 */           return new MysqlxExpr.FunctionCall(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<FunctionCall> parser() {
/*  7026 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<FunctionCall> getParserForType() {
/*  7031 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public FunctionCall getDefaultInstanceForType() {
/*  7036 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface OperatorOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasName();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     String getName();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getNameBytes();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<MysqlxExpr.Expr> getParamList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Expr getParam(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getParamCount();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ExprOrBuilder getParamOrBuilder(int param1Int);
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class Operator
/*       */     extends GeneratedMessageV3
/*       */     implements OperatorOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int NAME_FIELD_NUMBER = 1;
/*       */ 
/*       */ 
/*       */     
/*       */     private volatile Object name_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int PARAM_FIELD_NUMBER = 2;
/*       */ 
/*       */ 
/*       */     
/*       */     private List<MysqlxExpr.Expr> param_;
/*       */ 
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private Operator(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/*  7128 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*  7329 */       this.memoizedIsInitialized = -1; } private Operator() { this.memoizedIsInitialized = -1; this.name_ = ""; this.param_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Operator(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Operator(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: bs = input.readBytes(); this.bitField0_ |= 0x1; this.name_ = bs; continue;case 18: if ((mutable_bitField0_ & 0x2) == 0) { this.param_ = new ArrayList<>(); mutable_bitField0_ |= 0x2; }  this.param_.add(input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry)); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x2) != 0) this.param_ = Collections.unmodifiableList(this.param_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_fieldAccessorTable.ensureFieldAccessorsInitialized(Operator.class, Builder.class); } public boolean hasName() { return ((this.bitField0_ & 0x1) != 0); } public String getName() { Object ref = this.name_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.name_ = s;  return s; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; } public List<MysqlxExpr.Expr> getParamList() { return this.param_; } public List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList() { return (List)this.param_; } public int getParamCount() { return this.param_.size(); }
/*       */     public MysqlxExpr.Expr getParam(int index) { return this.param_.get(index); }
/*       */     public MysqlxExpr.ExprOrBuilder getParamOrBuilder(int index) { return this.param_.get(index); }
/*  7332 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  7333 */       if (isInitialized == 1) return true; 
/*  7334 */       if (isInitialized == 0) return false;
/*       */       
/*  7336 */       if (!hasName()) {
/*  7337 */         this.memoizedIsInitialized = 0;
/*  7338 */         return false;
/*       */       } 
/*  7340 */       for (int i = 0; i < getParamCount(); i++) {
/*  7341 */         if (!getParam(i).isInitialized()) {
/*  7342 */           this.memoizedIsInitialized = 0;
/*  7343 */           return false;
/*       */         } 
/*       */       } 
/*  7346 */       this.memoizedIsInitialized = 1;
/*  7347 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/*  7353 */       if ((this.bitField0_ & 0x1) != 0) {
/*  7354 */         GeneratedMessageV3.writeString(output, 1, this.name_);
/*       */       }
/*  7356 */       for (int i = 0; i < this.param_.size(); i++) {
/*  7357 */         output.writeMessage(2, (MessageLite)this.param_.get(i));
/*       */       }
/*  7359 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/*  7364 */       int size = this.memoizedSize;
/*  7365 */       if (size != -1) return size;
/*       */       
/*  7367 */       size = 0;
/*  7368 */       if ((this.bitField0_ & 0x1) != 0) {
/*  7369 */         size += GeneratedMessageV3.computeStringSize(1, this.name_);
/*       */       }
/*  7371 */       for (int i = 0; i < this.param_.size(); i++) {
/*  7372 */         size += 
/*  7373 */           CodedOutputStream.computeMessageSize(2, (MessageLite)this.param_.get(i));
/*       */       }
/*  7375 */       size += this.unknownFields.getSerializedSize();
/*  7376 */       this.memoizedSize = size;
/*  7377 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/*  7382 */       if (obj == this) {
/*  7383 */         return true;
/*       */       }
/*  7385 */       if (!(obj instanceof Operator)) {
/*  7386 */         return super.equals(obj);
/*       */       }
/*  7388 */       Operator other = (Operator)obj;
/*       */       
/*  7390 */       if (hasName() != other.hasName()) return false; 
/*  7391 */       if (hasName() && 
/*       */         
/*  7393 */         !getName().equals(other.getName())) return false;
/*       */ 
/*       */       
/*  7396 */       if (!getParamList().equals(other.getParamList())) return false; 
/*  7397 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  7398 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/*  7403 */       if (this.memoizedHashCode != 0) {
/*  7404 */         return this.memoizedHashCode;
/*       */       }
/*  7406 */       int hash = 41;
/*  7407 */       hash = 19 * hash + getDescriptor().hashCode();
/*  7408 */       if (hasName()) {
/*  7409 */         hash = 37 * hash + 1;
/*  7410 */         hash = 53 * hash + getName().hashCode();
/*       */       } 
/*  7412 */       if (getParamCount() > 0) {
/*  7413 */         hash = 37 * hash + 2;
/*  7414 */         hash = 53 * hash + getParamList().hashCode();
/*       */       } 
/*  7416 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  7417 */       this.memoizedHashCode = hash;
/*  7418 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Operator parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  7424 */       return (Operator)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Operator parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  7430 */       return (Operator)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Operator parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  7435 */       return (Operator)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Operator parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  7441 */       return (Operator)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Operator parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  7445 */       return (Operator)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Operator parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  7451 */       return (Operator)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Operator parseFrom(InputStream input) throws IOException {
/*  7455 */       return 
/*  7456 */         (Operator)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Operator parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  7462 */       return 
/*  7463 */         (Operator)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Operator parseDelimitedFrom(InputStream input) throws IOException {
/*  7467 */       return 
/*  7468 */         (Operator)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Operator parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  7474 */       return 
/*  7475 */         (Operator)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Operator parseFrom(CodedInputStream input) throws IOException {
/*  7480 */       return 
/*  7481 */         (Operator)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Operator parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  7487 */       return 
/*  7488 */         (Operator)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/*  7492 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/*  7494 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(Operator prototype) {
/*  7497 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/*  7501 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  7502 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  7508 */       Builder builder = new Builder(parent);
/*  7509 */       return builder;
/*       */     }
/*       */     
/*       */     public static final class Builder
/*       */       extends GeneratedMessageV3.Builder<Builder> implements MysqlxExpr.OperatorOrBuilder {
/*       */       private int bitField0_;
/*       */       private Object name_;
/*       */       private List<MysqlxExpr.Expr> param_;
/*       */       private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> paramBuilder_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/*  7520 */         return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  7526 */         return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_fieldAccessorTable
/*  7527 */           .ensureFieldAccessorsInitialized(MysqlxExpr.Operator.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/*  7717 */         this.name_ = "";
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/*  7825 */         this
/*  7826 */           .param_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.name_ = ""; this.param_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxExpr.Operator.alwaysUseFieldBuilders) getParamFieldBuilder();  } public Builder clear() { super.clear(); this.name_ = ""; this.bitField0_ &= 0xFFFFFFFE; if (this.paramBuilder_ == null) { this.param_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFD; } else { this.paramBuilder_.clear(); }  return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_descriptor; } public MysqlxExpr.Operator getDefaultInstanceForType() { return MysqlxExpr.Operator.getDefaultInstance(); } public MysqlxExpr.Operator build() { MysqlxExpr.Operator result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxExpr.Operator buildPartial() { MysqlxExpr.Operator result = new MysqlxExpr.Operator(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.name_ = this.name_; if (this.paramBuilder_ == null) { if ((this.bitField0_ & 0x2) != 0) { this.param_ = Collections.unmodifiableList(this.param_); this.bitField0_ &= 0xFFFFFFFD; }  result.param_ = this.param_; } else { result.param_ = this.paramBuilder_.build(); }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxExpr.Operator) return mergeFrom((MysqlxExpr.Operator)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxExpr.Operator other) { if (other == MysqlxExpr.Operator.getDefaultInstance()) return this;  if (other.hasName()) { this.bitField0_ |= 0x1; this.name_ = other.name_; onChanged(); }  if (this.paramBuilder_ == null) { if (!other.param_.isEmpty()) { if (this.param_.isEmpty()) { this.param_ = other.param_; this.bitField0_ &= 0xFFFFFFFD; } else { ensureParamIsMutable(); this.param_.addAll(other.param_); }  onChanged(); }  } else if (!other.param_.isEmpty()) { if (this.paramBuilder_.isEmpty()) { this.paramBuilder_.dispose(); this.paramBuilder_ = null; this.param_ = other.param_; this.bitField0_ &= 0xFFFFFFFD; this.paramBuilder_ = MysqlxExpr.Operator.alwaysUseFieldBuilders ? getParamFieldBuilder() : null; } else { this.paramBuilder_.addAllMessages(other.param_); }  }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasName()) return false;  for (int i = 0; i < getParamCount(); i++) { if (!getParam(i).isInitialized()) return false;  }  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxExpr.Operator parsedMessage = null; try { parsedMessage = (MysqlxExpr.Operator)MysqlxExpr.Operator.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxExpr.Operator)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasName() { return ((this.bitField0_ & 0x1) != 0); } public String getName() { Object ref = this.name_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.name_ = s;  return s; }  return (String)ref; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; } public Builder setName(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.name_ = value; onChanged(); return this; } public Builder clearName() { this.bitField0_ &= 0xFFFFFFFE; this.name_ = MysqlxExpr.Operator.getDefaultInstance().getName(); onChanged(); return this; }
/*       */       public Builder setNameBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.name_ = value; onChanged(); return this; }
/*  7828 */       private void ensureParamIsMutable() { if ((this.bitField0_ & 0x2) == 0) {
/*  7829 */           this.param_ = new ArrayList<>(this.param_);
/*  7830 */           this.bitField0_ |= 0x2;
/*       */         }  }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<MysqlxExpr.Expr> getParamList() {
/*  7845 */         if (this.paramBuilder_ == null) {
/*  7846 */           return Collections.unmodifiableList(this.param_);
/*       */         }
/*  7848 */         return this.paramBuilder_.getMessageList();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public int getParamCount() {
/*  7859 */         if (this.paramBuilder_ == null) {
/*  7860 */           return this.param_.size();
/*       */         }
/*  7862 */         return this.paramBuilder_.getCount();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr getParam(int index) {
/*  7873 */         if (this.paramBuilder_ == null) {
/*  7874 */           return this.param_.get(index);
/*       */         }
/*  7876 */         return (MysqlxExpr.Expr)this.paramBuilder_.getMessage(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setParam(int index, MysqlxExpr.Expr value) {
/*  7888 */         if (this.paramBuilder_ == null) {
/*  7889 */           if (value == null) {
/*  7890 */             throw new NullPointerException();
/*       */           }
/*  7892 */           ensureParamIsMutable();
/*  7893 */           this.param_.set(index, value);
/*  7894 */           onChanged();
/*       */         } else {
/*  7896 */           this.paramBuilder_.setMessage(index, (AbstractMessage)value);
/*       */         } 
/*  7898 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setParam(int index, MysqlxExpr.Expr.Builder builderForValue) {
/*  7909 */         if (this.paramBuilder_ == null) {
/*  7910 */           ensureParamIsMutable();
/*  7911 */           this.param_.set(index, builderForValue.build());
/*  7912 */           onChanged();
/*       */         } else {
/*  7914 */           this.paramBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/*  7916 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addParam(MysqlxExpr.Expr value) {
/*  7926 */         if (this.paramBuilder_ == null) {
/*  7927 */           if (value == null) {
/*  7928 */             throw new NullPointerException();
/*       */           }
/*  7930 */           ensureParamIsMutable();
/*  7931 */           this.param_.add(value);
/*  7932 */           onChanged();
/*       */         } else {
/*  7934 */           this.paramBuilder_.addMessage((AbstractMessage)value);
/*       */         } 
/*  7936 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addParam(int index, MysqlxExpr.Expr value) {
/*  7947 */         if (this.paramBuilder_ == null) {
/*  7948 */           if (value == null) {
/*  7949 */             throw new NullPointerException();
/*       */           }
/*  7951 */           ensureParamIsMutable();
/*  7952 */           this.param_.add(index, value);
/*  7953 */           onChanged();
/*       */         } else {
/*  7955 */           this.paramBuilder_.addMessage(index, (AbstractMessage)value);
/*       */         } 
/*  7957 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addParam(MysqlxExpr.Expr.Builder builderForValue) {
/*  7968 */         if (this.paramBuilder_ == null) {
/*  7969 */           ensureParamIsMutable();
/*  7970 */           this.param_.add(builderForValue.build());
/*  7971 */           onChanged();
/*       */         } else {
/*  7973 */           this.paramBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/*  7975 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addParam(int index, MysqlxExpr.Expr.Builder builderForValue) {
/*  7986 */         if (this.paramBuilder_ == null) {
/*  7987 */           ensureParamIsMutable();
/*  7988 */           this.param_.add(index, builderForValue.build());
/*  7989 */           onChanged();
/*       */         } else {
/*  7991 */           this.paramBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/*  7993 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addAllParam(Iterable<? extends MysqlxExpr.Expr> values) {
/*  8004 */         if (this.paramBuilder_ == null) {
/*  8005 */           ensureParamIsMutable();
/*  8006 */           AbstractMessageLite.Builder.addAll(values, this.param_);
/*       */           
/*  8008 */           onChanged();
/*       */         } else {
/*  8010 */           this.paramBuilder_.addAllMessages(values);
/*       */         } 
/*  8012 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearParam() {
/*  8022 */         if (this.paramBuilder_ == null) {
/*  8023 */           this.param_ = Collections.emptyList();
/*  8024 */           this.bitField0_ &= 0xFFFFFFFD;
/*  8025 */           onChanged();
/*       */         } else {
/*  8027 */           this.paramBuilder_.clear();
/*       */         } 
/*  8029 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder removeParam(int index) {
/*  8039 */         if (this.paramBuilder_ == null) {
/*  8040 */           ensureParamIsMutable();
/*  8041 */           this.param_.remove(index);
/*  8042 */           onChanged();
/*       */         } else {
/*  8044 */           this.paramBuilder_.remove(index);
/*       */         } 
/*  8046 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr.Builder getParamBuilder(int index) {
/*  8057 */         return (MysqlxExpr.Expr.Builder)getParamFieldBuilder().getBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.ExprOrBuilder getParamOrBuilder(int index) {
/*  8068 */         if (this.paramBuilder_ == null)
/*  8069 */           return this.param_.get(index); 
/*  8070 */         return (MysqlxExpr.ExprOrBuilder)this.paramBuilder_.getMessageOrBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList() {
/*  8082 */         if (this.paramBuilder_ != null) {
/*  8083 */           return this.paramBuilder_.getMessageOrBuilderList();
/*       */         }
/*  8085 */         return Collections.unmodifiableList((List)this.param_);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr.Builder addParamBuilder() {
/*  8096 */         return (MysqlxExpr.Expr.Builder)getParamFieldBuilder().addBuilder(
/*  8097 */             (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr.Builder addParamBuilder(int index) {
/*  8108 */         return (MysqlxExpr.Expr.Builder)getParamFieldBuilder().addBuilder(index, 
/*  8109 */             (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<MysqlxExpr.Expr.Builder> getParamBuilderList() {
/*  8120 */         return getParamFieldBuilder().getBuilderList();
/*       */       }
/*       */ 
/*       */       
/*       */       private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getParamFieldBuilder() {
/*  8125 */         if (this.paramBuilder_ == null) {
/*  8126 */           this
/*       */ 
/*       */ 
/*       */ 
/*       */             
/*  8131 */             .paramBuilder_ = new RepeatedFieldBuilderV3(this.param_, ((this.bitField0_ & 0x2) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/*  8132 */           this.param_ = null;
/*       */         } 
/*  8134 */         return this.paramBuilder_;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  8139 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  8145 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*  8155 */     private static final Operator DEFAULT_INSTANCE = new Operator();
/*       */ 
/*       */     
/*       */     public static Operator getDefaultInstance() {
/*  8159 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/*  8163 */     public static final Parser<Operator> PARSER = (Parser<Operator>)new AbstractParser<Operator>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxExpr.Operator parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/*  8169 */           return new MysqlxExpr.Operator(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<Operator> parser() {
/*  8174 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<Operator> getParserForType() {
/*  8179 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Operator getDefaultInstanceForType() {
/*  8184 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface ObjectOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     List<MysqlxExpr.Object.ObjectField> getFldList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Object.ObjectField getFld(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getFldCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxExpr.Object.ObjectFieldOrBuilder> getFldOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Object.ObjectFieldOrBuilder getFldOrBuilder(int param1Int);
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class Object
/*       */     extends GeneratedMessageV3
/*       */     implements ObjectOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int FLD_FIELD_NUMBER = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private List<ObjectField> fld_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private Object(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/*  8252 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*  9325 */       this.memoizedIsInitialized = -1; } private Object() { this.memoizedIsInitialized = -1; this.fld_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Object(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Object(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: if ((mutable_bitField0_ & 0x1) == 0) { this.fld_ = new ArrayList<>(); mutable_bitField0_ |= 0x1; }  this.fld_.add(input.readMessage(ObjectField.PARSER, extensionRegistry)); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x1) != 0) this.fld_ = Collections.unmodifiableList(this.fld_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/*       */     public static final Descriptors.Descriptor getDescriptor() { return MysqlxExpr.internal_static_Mysqlx_Expr_Object_descriptor; }
/*       */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxExpr.internal_static_Mysqlx_Expr_Object_fieldAccessorTable.ensureFieldAccessorsInitialized(Object.class, Builder.class); }
/*  9328 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  9329 */       if (isInitialized == 1) return true; 
/*  9330 */       if (isInitialized == 0) return false;
/*       */       
/*  9332 */       for (int i = 0; i < getFldCount(); i++) {
/*  9333 */         if (!getFld(i).isInitialized()) {
/*  9334 */           this.memoizedIsInitialized = 0;
/*  9335 */           return false;
/*       */         } 
/*       */       } 
/*  9338 */       this.memoizedIsInitialized = 1;
/*  9339 */       return true; }
/*       */     public static final class ObjectField extends GeneratedMessageV3 implements ObjectFieldOrBuilder {
/*       */       private static final long serialVersionUID = 0L;
/*       */       private int bitField0_; public static final int KEY_FIELD_NUMBER = 1; private volatile Object key_; public static final int VALUE_FIELD_NUMBER = 2; private MysqlxExpr.Expr value_; private byte memoizedIsInitialized; private ObjectField(GeneratedMessageV3.Builder<?> builder) { super(builder); this.memoizedIsInitialized = -1; } private ObjectField() { this.memoizedIsInitialized = -1; this.key_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new ObjectField(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private ObjectField(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; MysqlxExpr.Expr.Builder subBuilder; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: bs = input.readBytes(); this.bitField0_ |= 0x1; this.key_ = bs; continue;case 18: subBuilder = null; if ((this.bitField0_ & 0x2) != 0) subBuilder = this.value_.toBuilder();  this.value_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.value_); this.value_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x2; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_fieldAccessorTable.ensureFieldAccessorsInitialized(ObjectField.class, Builder.class); } public boolean hasKey() { return ((this.bitField0_ & 0x1) != 0); } public String getKey() { Object ref = this.key_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.key_ = s;  return s; } public ByteString getKeyBytes() { Object ref = this.key_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.key_ = b; return b; }  return (ByteString)ref; } public boolean hasValue() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxExpr.Expr getValue() { return (this.value_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.value_; } public MysqlxExpr.ExprOrBuilder getValueOrBuilder() { return (this.value_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.value_; } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized; if (isInitialized == 1) return true;  if (isInitialized == 0) return false;  if (!hasKey()) { this.memoizedIsInitialized = 0; return false; }  if (!hasValue()) { this.memoizedIsInitialized = 0; return false; }  if (!getValue().isInitialized()) { this.memoizedIsInitialized = 0; return false; }  this.memoizedIsInitialized = 1; return true; } public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) GeneratedMessageV3.writeString(output, 1, this.key_);  if ((this.bitField0_ & 0x2) != 0) output.writeMessage(2, (MessageLite)getValue());  this.unknownFields.writeTo(output); } public int getSerializedSize() { int size = this.memoizedSize; if (size != -1) return size;  size = 0; if ((this.bitField0_ & 0x1) != 0) size += GeneratedMessageV3.computeStringSize(1, this.key_);  if ((this.bitField0_ & 0x2) != 0) size += CodedOutputStream.computeMessageSize(2, (MessageLite)getValue());  size += this.unknownFields.getSerializedSize(); this.memoizedSize = size; return size; } public boolean equals(Object obj) { if (obj == this) return true;  if (!(obj instanceof ObjectField)) return super.equals(obj);  ObjectField other = (ObjectField)obj; if (hasKey() != other.hasKey()) return false;  if (hasKey() && !getKey().equals(other.getKey())) return false;  if (hasValue() != other.hasValue()) return false;  if (hasValue() && !getValue().equals(other.getValue())) return false;  if (!this.unknownFields.equals(other.unknownFields)) return false;  return true; } public int hashCode() { if (this.memoizedHashCode != 0) return this.memoizedHashCode;  int hash = 41; hash = 19 * hash + getDescriptor().hashCode(); if (hasKey()) { hash = 37 * hash + 1; hash = 53 * hash + getKey().hashCode(); }  if (hasValue()) { hash = 37 * hash + 2; hash = 53 * hash + getValue().hashCode(); }  hash = 29 * hash + this.unknownFields.hashCode(); this.memoizedHashCode = hash; return hash; } public static ObjectField parseFrom(ByteBuffer data) throws InvalidProtocolBufferException { return (ObjectField)PARSER.parseFrom(data); } public static ObjectField parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (ObjectField)PARSER.parseFrom(data, extensionRegistry); } public static ObjectField parseFrom(ByteString data) throws InvalidProtocolBufferException { return (ObjectField)PARSER.parseFrom(data); } public static ObjectField parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (ObjectField)PARSER.parseFrom(data, extensionRegistry); } public static ObjectField parseFrom(byte[] data) throws InvalidProtocolBufferException { return (ObjectField)PARSER.parseFrom(data); } public static ObjectField parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (ObjectField)PARSER.parseFrom(data, extensionRegistry); } public static ObjectField parseFrom(InputStream input) throws IOException { return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static ObjectField parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public static ObjectField parseDelimitedFrom(InputStream input) throws IOException { return (ObjectField)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input); } public static ObjectField parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (ObjectField)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry); } public static ObjectField parseFrom(CodedInputStream input) throws IOException { return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static ObjectField parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public Builder newBuilderForType() { return newBuilder(); } public static Builder newBuilder() { return DEFAULT_INSTANCE.toBuilder(); } public static Builder newBuilder(ObjectField prototype) { return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype); } public Builder toBuilder() { return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder()).mergeFrom(this); } protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) { Builder builder = new Builder(parent); return builder; } public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxExpr.Object.ObjectFieldOrBuilder {
/*       */         private int bitField0_; private Object key_; private MysqlxExpr.Expr value_; private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> valueBuilder_; public static final Descriptors.Descriptor getDescriptor() { return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxExpr.Object.ObjectField.class, Builder.class); } private Builder() { this.key_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.key_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxExpr.Object.ObjectField.alwaysUseFieldBuilders) getValueFieldBuilder();  } public Builder clear() { super.clear(); this.key_ = ""; this.bitField0_ &= 0xFFFFFFFE; if (this.valueBuilder_ == null) { this.value_ = null; } else { this.valueBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_descriptor; } public MysqlxExpr.Object.ObjectField getDefaultInstanceForType() { return MysqlxExpr.Object.ObjectField.getDefaultInstance(); } public MysqlxExpr.Object.ObjectField build() { MysqlxExpr.Object.ObjectField result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxExpr.Object.ObjectField buildPartial() { MysqlxExpr.Object.ObjectField result = new MysqlxExpr.Object.ObjectField(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.key_ = this.key_; if ((from_bitField0_ & 0x2) != 0) { if (this.valueBuilder_ == null) { result.value_ = this.value_; } else { result.value_ = (MysqlxExpr.Expr)this.valueBuilder_.build(); }  to_bitField0_ |= 0x2; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxExpr.Object.ObjectField) return mergeFrom((MysqlxExpr.Object.ObjectField)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxExpr.Object.ObjectField other) { if (other == MysqlxExpr.Object.ObjectField.getDefaultInstance()) return this;  if (other.hasKey()) { this.bitField0_ |= 0x1; this.key_ = other.key_; onChanged(); }  if (other.hasValue()) mergeValue(other.getValue());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasKey()) return false;  if (!hasValue()) return false;  if (!getValue().isInitialized()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxExpr.Object.ObjectField parsedMessage = null; try { parsedMessage = (MysqlxExpr.Object.ObjectField)MysqlxExpr.Object.ObjectField.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxExpr.Object.ObjectField)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasKey() { return ((this.bitField0_ & 0x1) != 0); } public String getKey() { Object ref = this.key_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.key_ = s;  return s; }  return (String)ref; } public ByteString getKeyBytes() { Object ref = this.key_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.key_ = b; return b; }  return (ByteString)ref; } public Builder setKey(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.key_ = value; onChanged(); return this; } public Builder clearKey() { this.bitField0_ &= 0xFFFFFFFE; this.key_ = MysqlxExpr.Object.ObjectField.getDefaultInstance().getKey(); onChanged(); return this; } public Builder setKeyBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.key_ = value; onChanged(); return this; } public boolean hasValue() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxExpr.Expr getValue() { if (this.valueBuilder_ == null) return (this.value_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;  return (MysqlxExpr.Expr)this.valueBuilder_.getMessage(); } public Builder setValue(MysqlxExpr.Expr value) { if (this.valueBuilder_ == null) { if (value == null) throw new NullPointerException();  this.value_ = value; onChanged(); } else { this.valueBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x2; return this; } public Builder setValue(MysqlxExpr.Expr.Builder builderForValue) { if (this.valueBuilder_ == null) { this.value_ = builderForValue.build(); onChanged(); } else { this.valueBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x2; return this; } public Builder mergeValue(MysqlxExpr.Expr value) { if (this.valueBuilder_ == null) { if ((this.bitField0_ & 0x2) != 0 && this.value_ != null && this.value_ != MysqlxExpr.Expr.getDefaultInstance()) { this.value_ = MysqlxExpr.Expr.newBuilder(this.value_).mergeFrom(value).buildPartial(); } else { this.value_ = value; }  onChanged(); } else { this.valueBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x2; return this; } public Builder clearValue() { if (this.valueBuilder_ == null) { this.value_ = null; onChanged(); } else { this.valueBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; return this; } public MysqlxExpr.Expr.Builder getValueBuilder() { this.bitField0_ |= 0x2; onChanged(); return (MysqlxExpr.Expr.Builder)getValueFieldBuilder().getBuilder(); } public MysqlxExpr.ExprOrBuilder getValueOrBuilder() { if (this.valueBuilder_ != null) return (MysqlxExpr.ExprOrBuilder)this.valueBuilder_.getMessageOrBuilder();  return (this.value_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.value_; } private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getValueFieldBuilder() { if (this.valueBuilder_ == null) { this.valueBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getValue(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.value_ = null; }  return this.valueBuilder_; } public final Builder setUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.setUnknownFields(unknownFields); } public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.mergeUnknownFields(unknownFields); } } private static final ObjectField DEFAULT_INSTANCE = new ObjectField(); public static ObjectField getDefaultInstance() { return DEFAULT_INSTANCE; } @Deprecated public static final Parser<ObjectField> PARSER = (Parser<ObjectField>)new AbstractParser<ObjectField>() { public MysqlxExpr.Object.ObjectField parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return new MysqlxExpr.Object.ObjectField(input, extensionRegistry); } }
/*       */       ; public static Parser<ObjectField> parser() { return PARSER; } public Parser<ObjectField> getParserForType() { return PARSER; } public ObjectField getDefaultInstanceForType() { return DEFAULT_INSTANCE; }
/*  9345 */     } public void writeTo(CodedOutputStream output) throws IOException { for (int i = 0; i < this.fld_.size(); i++) {
/*  9346 */         output.writeMessage(1, (MessageLite)this.fld_.get(i));
/*       */       }
/*  9348 */       this.unknownFields.writeTo(output); }
/*       */     public List<ObjectField> getFldList() { return this.fld_; }
/*       */     public List<? extends ObjectFieldOrBuilder> getFldOrBuilderList() { return (List)this.fld_; }
/*       */     public int getFldCount() { return this.fld_.size(); }
/*       */     public ObjectField getFld(int index) { return this.fld_.get(index); }
/*  9353 */     public ObjectFieldOrBuilder getFldOrBuilder(int index) { return this.fld_.get(index); } public int getSerializedSize() { int size = this.memoizedSize;
/*  9354 */       if (size != -1) return size;
/*       */       
/*  9356 */       size = 0;
/*  9357 */       for (int i = 0; i < this.fld_.size(); i++) {
/*  9358 */         size += 
/*  9359 */           CodedOutputStream.computeMessageSize(1, (MessageLite)this.fld_.get(i));
/*       */       }
/*  9361 */       size += this.unknownFields.getSerializedSize();
/*  9362 */       this.memoizedSize = size;
/*  9363 */       return size; }
/*       */ 
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/*  9368 */       if (obj == this) {
/*  9369 */         return true;
/*       */       }
/*  9371 */       if (!(obj instanceof Object)) {
/*  9372 */         return super.equals(obj);
/*       */       }
/*  9374 */       Object other = (Object)obj;
/*       */ 
/*       */       
/*  9377 */       if (!getFldList().equals(other.getFldList())) return false; 
/*  9378 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  9379 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/*  9384 */       if (this.memoizedHashCode != 0) {
/*  9385 */         return this.memoizedHashCode;
/*       */       }
/*  9387 */       int hash = 41;
/*  9388 */       hash = 19 * hash + getDescriptor().hashCode();
/*  9389 */       if (getFldCount() > 0) {
/*  9390 */         hash = 37 * hash + 1;
/*  9391 */         hash = 53 * hash + getFldList().hashCode();
/*       */       } 
/*  9393 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  9394 */       this.memoizedHashCode = hash;
/*  9395 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Object parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  9401 */       return (Object)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Object parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  9407 */       return (Object)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Object parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  9412 */       return (Object)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Object parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  9418 */       return (Object)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Object parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  9422 */       return (Object)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Object parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  9428 */       return (Object)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Object parseFrom(InputStream input) throws IOException {
/*  9432 */       return 
/*  9433 */         (Object)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Object parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  9439 */       return 
/*  9440 */         (Object)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Object parseDelimitedFrom(InputStream input) throws IOException {
/*  9444 */       return 
/*  9445 */         (Object)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Object parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  9451 */       return 
/*  9452 */         (Object)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Object parseFrom(CodedInputStream input) throws IOException {
/*  9457 */       return 
/*  9458 */         (Object)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Object parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  9464 */       return 
/*  9465 */         (Object)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/*  9469 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/*  9471 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(Object prototype) {
/*  9474 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/*  9478 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  9479 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  9485 */       Builder builder = new Builder(parent);
/*  9486 */       return builder;
/*       */     }
/*       */ 
/*       */     
/*       */     public static final class Builder
/*       */       extends GeneratedMessageV3.Builder<Builder>
/*       */       implements MysqlxExpr.ObjectOrBuilder
/*       */     {
/*       */       private int bitField0_;
/*       */       
/*       */       private List<MysqlxExpr.Object.ObjectField> fld_;
/*       */       
/*       */       private RepeatedFieldBuilderV3<MysqlxExpr.Object.ObjectField, MysqlxExpr.Object.ObjectField.Builder, MysqlxExpr.Object.ObjectFieldOrBuilder> fldBuilder_;
/*       */ 
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/*  9502 */         return MysqlxExpr.internal_static_Mysqlx_Expr_Object_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  9508 */         return MysqlxExpr.internal_static_Mysqlx_Expr_Object_fieldAccessorTable
/*  9509 */           .ensureFieldAccessorsInitialized(MysqlxExpr.Object.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/*  9683 */         this
/*  9684 */           .fld_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.fld_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxExpr.Object.alwaysUseFieldBuilders) getFldFieldBuilder();  } public Builder clear() { super.clear(); if (this.fldBuilder_ == null) { this.fld_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; } else { this.fldBuilder_.clear(); }  return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxExpr.internal_static_Mysqlx_Expr_Object_descriptor; } public MysqlxExpr.Object getDefaultInstanceForType() { return MysqlxExpr.Object.getDefaultInstance(); } public MysqlxExpr.Object build() { MysqlxExpr.Object result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxExpr.Object buildPartial() { MysqlxExpr.Object result = new MysqlxExpr.Object(this); int from_bitField0_ = this.bitField0_; if (this.fldBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0) { this.fld_ = Collections.unmodifiableList(this.fld_); this.bitField0_ &= 0xFFFFFFFE; }  result.fld_ = this.fld_; } else { result.fld_ = this.fldBuilder_.build(); }  onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxExpr.Object) return mergeFrom((MysqlxExpr.Object)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxExpr.Object other) { if (other == MysqlxExpr.Object.getDefaultInstance()) return this;  if (this.fldBuilder_ == null) { if (!other.fld_.isEmpty()) { if (this.fld_.isEmpty()) { this.fld_ = other.fld_; this.bitField0_ &= 0xFFFFFFFE; } else { ensureFldIsMutable(); this.fld_.addAll(other.fld_); }  onChanged(); }  } else if (!other.fld_.isEmpty()) { if (this.fldBuilder_.isEmpty()) { this.fldBuilder_.dispose(); this.fldBuilder_ = null; this.fld_ = other.fld_; this.bitField0_ &= 0xFFFFFFFE; this.fldBuilder_ = MysqlxExpr.Object.alwaysUseFieldBuilders ? getFldFieldBuilder() : null; } else { this.fldBuilder_.addAllMessages(other.fld_); }  }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { for (int i = 0; i < getFldCount(); i++) { if (!getFld(i).isInitialized()) return false;  }  return true; }
/*       */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxExpr.Object parsedMessage = null; try { parsedMessage = (MysqlxExpr.Object)MysqlxExpr.Object.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxExpr.Object)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/*  9686 */       private void ensureFldIsMutable() { if ((this.bitField0_ & 0x1) == 0) {
/*  9687 */           this.fld_ = new ArrayList<>(this.fld_);
/*  9688 */           this.bitField0_ |= 0x1;
/*       */         }  }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<MysqlxExpr.Object.ObjectField> getFldList() {
/*  9703 */         if (this.fldBuilder_ == null) {
/*  9704 */           return Collections.unmodifiableList(this.fld_);
/*       */         }
/*  9706 */         return this.fldBuilder_.getMessageList();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public int getFldCount() {
/*  9717 */         if (this.fldBuilder_ == null) {
/*  9718 */           return this.fld_.size();
/*       */         }
/*  9720 */         return this.fldBuilder_.getCount();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Object.ObjectField getFld(int index) {
/*  9731 */         if (this.fldBuilder_ == null) {
/*  9732 */           return this.fld_.get(index);
/*       */         }
/*  9734 */         return (MysqlxExpr.Object.ObjectField)this.fldBuilder_.getMessage(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setFld(int index, MysqlxExpr.Object.ObjectField value) {
/*  9746 */         if (this.fldBuilder_ == null) {
/*  9747 */           if (value == null) {
/*  9748 */             throw new NullPointerException();
/*       */           }
/*  9750 */           ensureFldIsMutable();
/*  9751 */           this.fld_.set(index, value);
/*  9752 */           onChanged();
/*       */         } else {
/*  9754 */           this.fldBuilder_.setMessage(index, (AbstractMessage)value);
/*       */         } 
/*  9756 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setFld(int index, MysqlxExpr.Object.ObjectField.Builder builderForValue) {
/*  9767 */         if (this.fldBuilder_ == null) {
/*  9768 */           ensureFldIsMutable();
/*  9769 */           this.fld_.set(index, builderForValue.build());
/*  9770 */           onChanged();
/*       */         } else {
/*  9772 */           this.fldBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/*  9774 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addFld(MysqlxExpr.Object.ObjectField value) {
/*  9784 */         if (this.fldBuilder_ == null) {
/*  9785 */           if (value == null) {
/*  9786 */             throw new NullPointerException();
/*       */           }
/*  9788 */           ensureFldIsMutable();
/*  9789 */           this.fld_.add(value);
/*  9790 */           onChanged();
/*       */         } else {
/*  9792 */           this.fldBuilder_.addMessage((AbstractMessage)value);
/*       */         } 
/*  9794 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addFld(int index, MysqlxExpr.Object.ObjectField value) {
/*  9805 */         if (this.fldBuilder_ == null) {
/*  9806 */           if (value == null) {
/*  9807 */             throw new NullPointerException();
/*       */           }
/*  9809 */           ensureFldIsMutable();
/*  9810 */           this.fld_.add(index, value);
/*  9811 */           onChanged();
/*       */         } else {
/*  9813 */           this.fldBuilder_.addMessage(index, (AbstractMessage)value);
/*       */         } 
/*  9815 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addFld(MysqlxExpr.Object.ObjectField.Builder builderForValue) {
/*  9826 */         if (this.fldBuilder_ == null) {
/*  9827 */           ensureFldIsMutable();
/*  9828 */           this.fld_.add(builderForValue.build());
/*  9829 */           onChanged();
/*       */         } else {
/*  9831 */           this.fldBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/*  9833 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addFld(int index, MysqlxExpr.Object.ObjectField.Builder builderForValue) {
/*  9844 */         if (this.fldBuilder_ == null) {
/*  9845 */           ensureFldIsMutable();
/*  9846 */           this.fld_.add(index, builderForValue.build());
/*  9847 */           onChanged();
/*       */         } else {
/*  9849 */           this.fldBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/*  9851 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addAllFld(Iterable<? extends MysqlxExpr.Object.ObjectField> values) {
/*  9862 */         if (this.fldBuilder_ == null) {
/*  9863 */           ensureFldIsMutable();
/*  9864 */           AbstractMessageLite.Builder.addAll(values, this.fld_);
/*       */           
/*  9866 */           onChanged();
/*       */         } else {
/*  9868 */           this.fldBuilder_.addAllMessages(values);
/*       */         } 
/*  9870 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearFld() {
/*  9880 */         if (this.fldBuilder_ == null) {
/*  9881 */           this.fld_ = Collections.emptyList();
/*  9882 */           this.bitField0_ &= 0xFFFFFFFE;
/*  9883 */           onChanged();
/*       */         } else {
/*  9885 */           this.fldBuilder_.clear();
/*       */         } 
/*  9887 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder removeFld(int index) {
/*  9897 */         if (this.fldBuilder_ == null) {
/*  9898 */           ensureFldIsMutable();
/*  9899 */           this.fld_.remove(index);
/*  9900 */           onChanged();
/*       */         } else {
/*  9902 */           this.fldBuilder_.remove(index);
/*       */         } 
/*  9904 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Object.ObjectField.Builder getFldBuilder(int index) {
/*  9915 */         return (MysqlxExpr.Object.ObjectField.Builder)getFldFieldBuilder().getBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Object.ObjectFieldOrBuilder getFldOrBuilder(int index) {
/*  9926 */         if (this.fldBuilder_ == null)
/*  9927 */           return this.fld_.get(index); 
/*  9928 */         return (MysqlxExpr.Object.ObjectFieldOrBuilder)this.fldBuilder_.getMessageOrBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<? extends MysqlxExpr.Object.ObjectFieldOrBuilder> getFldOrBuilderList() {
/*  9940 */         if (this.fldBuilder_ != null) {
/*  9941 */           return this.fldBuilder_.getMessageOrBuilderList();
/*       */         }
/*  9943 */         return Collections.unmodifiableList((List)this.fld_);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Object.ObjectField.Builder addFldBuilder() {
/*  9954 */         return (MysqlxExpr.Object.ObjectField.Builder)getFldFieldBuilder().addBuilder(
/*  9955 */             (AbstractMessage)MysqlxExpr.Object.ObjectField.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Object.ObjectField.Builder addFldBuilder(int index) {
/*  9966 */         return (MysqlxExpr.Object.ObjectField.Builder)getFldFieldBuilder().addBuilder(index, 
/*  9967 */             (AbstractMessage)MysqlxExpr.Object.ObjectField.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<MysqlxExpr.Object.ObjectField.Builder> getFldBuilderList() {
/*  9978 */         return getFldFieldBuilder().getBuilderList();
/*       */       }
/*       */ 
/*       */       
/*       */       private RepeatedFieldBuilderV3<MysqlxExpr.Object.ObjectField, MysqlxExpr.Object.ObjectField.Builder, MysqlxExpr.Object.ObjectFieldOrBuilder> getFldFieldBuilder() {
/*  9983 */         if (this.fldBuilder_ == null) {
/*  9984 */           this
/*       */ 
/*       */ 
/*       */ 
/*       */             
/*  9989 */             .fldBuilder_ = new RepeatedFieldBuilderV3(this.fld_, ((this.bitField0_ & 0x1) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/*  9990 */           this.fld_ = null;
/*       */         } 
/*  9992 */         return this.fldBuilder_;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  9997 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 10003 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 10013 */     private static final Object DEFAULT_INSTANCE = new Object();
/*       */ 
/*       */     
/*       */     public static Object getDefaultInstance() {
/* 10017 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/* 10021 */     public static final Parser<Object> PARSER = (Parser<Object>)new AbstractParser<Object>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxExpr.Object parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/* 10027 */           return new MysqlxExpr.Object(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<Object> parser() {
/* 10032 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<Object> getParserForType() {
/* 10037 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Object getDefaultInstanceForType() {
/* 10042 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static interface ObjectFieldOrBuilder
/*       */       extends MessageOrBuilder
/*       */     {
/*       */       boolean hasKey();
/*       */ 
/*       */ 
/*       */       
/*       */       String getKey();
/*       */ 
/*       */ 
/*       */       
/*       */       ByteString getKeyBytes();
/*       */ 
/*       */ 
/*       */       
/*       */       boolean hasValue();
/*       */ 
/*       */ 
/*       */       
/*       */       MysqlxExpr.Expr getValue();
/*       */ 
/*       */ 
/*       */       
/*       */       MysqlxExpr.ExprOrBuilder getValueOrBuilder();
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface ArrayOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     List<MysqlxExpr.Expr> getValueList();
/*       */ 
/*       */     
/*       */     MysqlxExpr.Expr getValue(int param1Int);
/*       */ 
/*       */     
/*       */     int getValueCount();
/*       */ 
/*       */     
/*       */     List<? extends MysqlxExpr.ExprOrBuilder> getValueOrBuilderList();
/*       */ 
/*       */     
/*       */     MysqlxExpr.ExprOrBuilder getValueOrBuilder(int param1Int);
/*       */   }
/*       */ 
/*       */   
/*       */   public static final class Array
/*       */     extends GeneratedMessageV3
/*       */     implements ArrayOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */     
/*       */     public static final int VALUE_FIELD_NUMBER = 1;
/*       */     
/*       */     private List<MysqlxExpr.Expr> value_;
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */     
/*       */     private Array(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/* 10110 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/* 10246 */       this.memoizedIsInitialized = -1; } private Array() { this.memoizedIsInitialized = -1; this.value_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Array(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Array(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: if ((mutable_bitField0_ & 0x1) == 0) { this.value_ = new ArrayList<>(); mutable_bitField0_ |= 0x1; }  this.value_.add(input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry)); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x1) != 0) this.value_ = Collections.unmodifiableList(this.value_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxExpr.internal_static_Mysqlx_Expr_Array_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxExpr.internal_static_Mysqlx_Expr_Array_fieldAccessorTable.ensureFieldAccessorsInitialized(Array.class, Builder.class); } public List<MysqlxExpr.Expr> getValueList() { return this.value_; } public List<? extends MysqlxExpr.ExprOrBuilder> getValueOrBuilderList() { return (List)this.value_; } public int getValueCount() { return this.value_.size(); }
/*       */     public MysqlxExpr.Expr getValue(int index) { return this.value_.get(index); }
/*       */     public MysqlxExpr.ExprOrBuilder getValueOrBuilder(int index) { return this.value_.get(index); }
/* 10249 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 10250 */       if (isInitialized == 1) return true; 
/* 10251 */       if (isInitialized == 0) return false;
/*       */       
/* 10253 */       for (int i = 0; i < getValueCount(); i++) {
/* 10254 */         if (!getValue(i).isInitialized()) {
/* 10255 */           this.memoizedIsInitialized = 0;
/* 10256 */           return false;
/*       */         } 
/*       */       } 
/* 10259 */       this.memoizedIsInitialized = 1;
/* 10260 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/* 10266 */       for (int i = 0; i < this.value_.size(); i++) {
/* 10267 */         output.writeMessage(1, (MessageLite)this.value_.get(i));
/*       */       }
/* 10269 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/* 10274 */       int size = this.memoizedSize;
/* 10275 */       if (size != -1) return size;
/*       */       
/* 10277 */       size = 0;
/* 10278 */       for (int i = 0; i < this.value_.size(); i++) {
/* 10279 */         size += 
/* 10280 */           CodedOutputStream.computeMessageSize(1, (MessageLite)this.value_.get(i));
/*       */       }
/* 10282 */       size += this.unknownFields.getSerializedSize();
/* 10283 */       this.memoizedSize = size;
/* 10284 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/* 10289 */       if (obj == this) {
/* 10290 */         return true;
/*       */       }
/* 10292 */       if (!(obj instanceof Array)) {
/* 10293 */         return super.equals(obj);
/*       */       }
/* 10295 */       Array other = (Array)obj;
/*       */ 
/*       */       
/* 10298 */       if (!getValueList().equals(other.getValueList())) return false; 
/* 10299 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 10300 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/* 10305 */       if (this.memoizedHashCode != 0) {
/* 10306 */         return this.memoizedHashCode;
/*       */       }
/* 10308 */       int hash = 41;
/* 10309 */       hash = 19 * hash + getDescriptor().hashCode();
/* 10310 */       if (getValueCount() > 0) {
/* 10311 */         hash = 37 * hash + 1;
/* 10312 */         hash = 53 * hash + getValueList().hashCode();
/*       */       } 
/* 10314 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 10315 */       this.memoizedHashCode = hash;
/* 10316 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Array parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 10322 */       return (Array)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Array parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 10328 */       return (Array)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Array parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 10333 */       return (Array)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Array parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 10339 */       return (Array)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Array parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 10343 */       return (Array)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Array parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 10349 */       return (Array)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Array parseFrom(InputStream input) throws IOException {
/* 10353 */       return 
/* 10354 */         (Array)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Array parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 10360 */       return 
/* 10361 */         (Array)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Array parseDelimitedFrom(InputStream input) throws IOException {
/* 10365 */       return 
/* 10366 */         (Array)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Array parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 10372 */       return 
/* 10373 */         (Array)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Array parseFrom(CodedInputStream input) throws IOException {
/* 10378 */       return 
/* 10379 */         (Array)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Array parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 10385 */       return 
/* 10386 */         (Array)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/* 10390 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/* 10392 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(Array prototype) {
/* 10395 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/* 10399 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 10400 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 10406 */       Builder builder = new Builder(parent);
/* 10407 */       return builder;
/*       */     }
/*       */ 
/*       */     
/*       */     public static final class Builder
/*       */       extends GeneratedMessageV3.Builder<Builder>
/*       */       implements MysqlxExpr.ArrayOrBuilder
/*       */     {
/*       */       private int bitField0_;
/*       */       
/*       */       private List<MysqlxExpr.Expr> value_;
/*       */       
/*       */       private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> valueBuilder_;
/*       */ 
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/* 10423 */         return MysqlxExpr.internal_static_Mysqlx_Expr_Array_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 10429 */         return MysqlxExpr.internal_static_Mysqlx_Expr_Array_fieldAccessorTable
/* 10430 */           .ensureFieldAccessorsInitialized(MysqlxExpr.Array.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/* 10604 */         this
/* 10605 */           .value_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.value_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxExpr.Array.alwaysUseFieldBuilders) getValueFieldBuilder();  } public Builder clear() { super.clear(); if (this.valueBuilder_ == null) { this.value_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; } else { this.valueBuilder_.clear(); }  return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxExpr.internal_static_Mysqlx_Expr_Array_descriptor; } public MysqlxExpr.Array getDefaultInstanceForType() { return MysqlxExpr.Array.getDefaultInstance(); } public MysqlxExpr.Array build() { MysqlxExpr.Array result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxExpr.Array buildPartial() { MysqlxExpr.Array result = new MysqlxExpr.Array(this); int from_bitField0_ = this.bitField0_; if (this.valueBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0) { this.value_ = Collections.unmodifiableList(this.value_); this.bitField0_ &= 0xFFFFFFFE; }  result.value_ = this.value_; } else { result.value_ = this.valueBuilder_.build(); }  onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxExpr.Array) return mergeFrom((MysqlxExpr.Array)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxExpr.Array other) { if (other == MysqlxExpr.Array.getDefaultInstance()) return this;  if (this.valueBuilder_ == null) { if (!other.value_.isEmpty()) { if (this.value_.isEmpty()) { this.value_ = other.value_; this.bitField0_ &= 0xFFFFFFFE; } else { ensureValueIsMutable(); this.value_.addAll(other.value_); }  onChanged(); }  } else if (!other.value_.isEmpty()) { if (this.valueBuilder_.isEmpty()) { this.valueBuilder_.dispose(); this.valueBuilder_ = null; this.value_ = other.value_; this.bitField0_ &= 0xFFFFFFFE; this.valueBuilder_ = MysqlxExpr.Array.alwaysUseFieldBuilders ? getValueFieldBuilder() : null; } else { this.valueBuilder_.addAllMessages(other.value_); }  }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { for (int i = 0; i < getValueCount(); i++) { if (!getValue(i).isInitialized()) return false;  }  return true; }
/*       */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxExpr.Array parsedMessage = null; try { parsedMessage = (MysqlxExpr.Array)MysqlxExpr.Array.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxExpr.Array)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/* 10607 */       private void ensureValueIsMutable() { if ((this.bitField0_ & 0x1) == 0) {
/* 10608 */           this.value_ = new ArrayList<>(this.value_);
/* 10609 */           this.bitField0_ |= 0x1;
/*       */         }  }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<MysqlxExpr.Expr> getValueList() {
/* 10624 */         if (this.valueBuilder_ == null) {
/* 10625 */           return Collections.unmodifiableList(this.value_);
/*       */         }
/* 10627 */         return this.valueBuilder_.getMessageList();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public int getValueCount() {
/* 10638 */         if (this.valueBuilder_ == null) {
/* 10639 */           return this.value_.size();
/*       */         }
/* 10641 */         return this.valueBuilder_.getCount();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr getValue(int index) {
/* 10652 */         if (this.valueBuilder_ == null) {
/* 10653 */           return this.value_.get(index);
/*       */         }
/* 10655 */         return (MysqlxExpr.Expr)this.valueBuilder_.getMessage(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setValue(int index, MysqlxExpr.Expr value) {
/* 10667 */         if (this.valueBuilder_ == null) {
/* 10668 */           if (value == null) {
/* 10669 */             throw new NullPointerException();
/*       */           }
/* 10671 */           ensureValueIsMutable();
/* 10672 */           this.value_.set(index, value);
/* 10673 */           onChanged();
/*       */         } else {
/* 10675 */           this.valueBuilder_.setMessage(index, (AbstractMessage)value);
/*       */         } 
/* 10677 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setValue(int index, MysqlxExpr.Expr.Builder builderForValue) {
/* 10688 */         if (this.valueBuilder_ == null) {
/* 10689 */           ensureValueIsMutable();
/* 10690 */           this.value_.set(index, builderForValue.build());
/* 10691 */           onChanged();
/*       */         } else {
/* 10693 */           this.valueBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/* 10695 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addValue(MysqlxExpr.Expr value) {
/* 10705 */         if (this.valueBuilder_ == null) {
/* 10706 */           if (value == null) {
/* 10707 */             throw new NullPointerException();
/*       */           }
/* 10709 */           ensureValueIsMutable();
/* 10710 */           this.value_.add(value);
/* 10711 */           onChanged();
/*       */         } else {
/* 10713 */           this.valueBuilder_.addMessage((AbstractMessage)value);
/*       */         } 
/* 10715 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addValue(int index, MysqlxExpr.Expr value) {
/* 10726 */         if (this.valueBuilder_ == null) {
/* 10727 */           if (value == null) {
/* 10728 */             throw new NullPointerException();
/*       */           }
/* 10730 */           ensureValueIsMutable();
/* 10731 */           this.value_.add(index, value);
/* 10732 */           onChanged();
/*       */         } else {
/* 10734 */           this.valueBuilder_.addMessage(index, (AbstractMessage)value);
/*       */         } 
/* 10736 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addValue(MysqlxExpr.Expr.Builder builderForValue) {
/* 10747 */         if (this.valueBuilder_ == null) {
/* 10748 */           ensureValueIsMutable();
/* 10749 */           this.value_.add(builderForValue.build());
/* 10750 */           onChanged();
/*       */         } else {
/* 10752 */           this.valueBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/* 10754 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addValue(int index, MysqlxExpr.Expr.Builder builderForValue) {
/* 10765 */         if (this.valueBuilder_ == null) {
/* 10766 */           ensureValueIsMutable();
/* 10767 */           this.value_.add(index, builderForValue.build());
/* 10768 */           onChanged();
/*       */         } else {
/* 10770 */           this.valueBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/* 10772 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addAllValue(Iterable<? extends MysqlxExpr.Expr> values) {
/* 10783 */         if (this.valueBuilder_ == null) {
/* 10784 */           ensureValueIsMutable();
/* 10785 */           AbstractMessageLite.Builder.addAll(values, this.value_);
/*       */           
/* 10787 */           onChanged();
/*       */         } else {
/* 10789 */           this.valueBuilder_.addAllMessages(values);
/*       */         } 
/* 10791 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearValue() {
/* 10801 */         if (this.valueBuilder_ == null) {
/* 10802 */           this.value_ = Collections.emptyList();
/* 10803 */           this.bitField0_ &= 0xFFFFFFFE;
/* 10804 */           onChanged();
/*       */         } else {
/* 10806 */           this.valueBuilder_.clear();
/*       */         } 
/* 10808 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder removeValue(int index) {
/* 10818 */         if (this.valueBuilder_ == null) {
/* 10819 */           ensureValueIsMutable();
/* 10820 */           this.value_.remove(index);
/* 10821 */           onChanged();
/*       */         } else {
/* 10823 */           this.valueBuilder_.remove(index);
/*       */         } 
/* 10825 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr.Builder getValueBuilder(int index) {
/* 10836 */         return (MysqlxExpr.Expr.Builder)getValueFieldBuilder().getBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.ExprOrBuilder getValueOrBuilder(int index) {
/* 10847 */         if (this.valueBuilder_ == null)
/* 10848 */           return this.value_.get(index); 
/* 10849 */         return (MysqlxExpr.ExprOrBuilder)this.valueBuilder_.getMessageOrBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<? extends MysqlxExpr.ExprOrBuilder> getValueOrBuilderList() {
/* 10861 */         if (this.valueBuilder_ != null) {
/* 10862 */           return this.valueBuilder_.getMessageOrBuilderList();
/*       */         }
/* 10864 */         return Collections.unmodifiableList((List)this.value_);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr.Builder addValueBuilder() {
/* 10875 */         return (MysqlxExpr.Expr.Builder)getValueFieldBuilder().addBuilder(
/* 10876 */             (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr.Builder addValueBuilder(int index) {
/* 10887 */         return (MysqlxExpr.Expr.Builder)getValueFieldBuilder().addBuilder(index, 
/* 10888 */             (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<MysqlxExpr.Expr.Builder> getValueBuilderList() {
/* 10899 */         return getValueFieldBuilder().getBuilderList();
/*       */       }
/*       */ 
/*       */       
/*       */       private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getValueFieldBuilder() {
/* 10904 */         if (this.valueBuilder_ == null) {
/* 10905 */           this
/*       */ 
/*       */ 
/*       */ 
/*       */             
/* 10910 */             .valueBuilder_ = new RepeatedFieldBuilderV3(this.value_, ((this.bitField0_ & 0x1) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 10911 */           this.value_ = null;
/*       */         } 
/* 10913 */         return this.valueBuilder_;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 10918 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 10924 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 10934 */     private static final Array DEFAULT_INSTANCE = new Array();
/*       */ 
/*       */     
/*       */     public static Array getDefaultInstance() {
/* 10938 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/* 10942 */     public static final Parser<Array> PARSER = (Parser<Array>)new AbstractParser<Array>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxExpr.Array parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/* 10948 */           return new MysqlxExpr.Array(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<Array> parser() {
/* 10953 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<Array> getParserForType() {
/* 10958 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Array getDefaultInstanceForType() {
/* 10963 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static Descriptors.FileDescriptor getDescriptor() {
/* 11016 */     return descriptor;
/*       */   }
/*       */ 
/*       */   
/*       */   static {
/* 11021 */     String[] descriptorData = { "\n\021mysqlx_expr.proto\022\013Mysqlx.Expr\032\026mysqlx_datatypes.proto\"Ä\003\n\004Expr\022$\n\004type\030\001 \002(\0162\026.Mysqlx.Expr.Expr.Type\0221\n\nidentifier\030\002 \001(\0132\035.Mysqlx.Expr.ColumnIdentifier\022\020\n\bvariable\030\003 \001(\t\022)\n\007literal\030\004 \001(\0132\030.Mysqlx.Datatypes.Scalar\0220\n\rfunction_call\030\005 \001(\0132\031.Mysqlx.Expr.FunctionCall\022'\n\boperator\030\006 \001(\0132\025.Mysqlx.Expr.Operator\022\020\n\bposition\030\007 \001(\r\022#\n\006object\030\b \001(\0132\023.Mysqlx.Expr.Object\022!\n\005array\030\t \001(\0132\022.Mysqlx.Expr.Array\"q\n\004Type\022\t\n\005IDENT\020\001\022\013\n\007LITERAL\020\002\022\f\n\bVARIABLE\020\003\022\r\n\tFUNC_CALL\020\004\022\f\n\bOPERATOR\020\005\022\017\n\013PLACEHOLDER\020\006\022\n\n\006OBJECT\020\007\022\t\n\005ARRAY\020\b\"/\n\nIdentifier\022\f\n\004name\030\001 \002(\t\022\023\n\013schema_name\030\002 \001(\t\"Ë\001\n\020DocumentPathItem\0220\n\004type\030\001 \002(\0162\".Mysqlx.Expr.DocumentPathItem.Type\022\r\n\005value\030\002 \001(\t\022\r\n\005index\030\003 \001(\r\"g\n\004Type\022\n\n\006MEMBER\020\001\022\023\n\017MEMBER_ASTERISK\020\002\022\017\n\013ARRAY_INDEX\020\003\022\030\n\024ARRAY_INDEX_ASTERISK\020\004\022\023\n\017DOUBLE_ASTERISK\020\005\"\n\020ColumnIdentifier\0224\n\rdocument_path\030\001 \003(\0132\035.Mysqlx.Expr.DocumentPathItem\022\f\n\004name\030\002 \001(\t\022\022\n\ntable_name\030\003 \001(\t\022\023\n\013schema_name\030\004 \001(\t\"W\n\fFunctionCall\022%\n\004name\030\001 \002(\0132\027.Mysqlx.Expr.Identifier\022 \n\005param\030\002 \003(\0132\021.Mysqlx.Expr.Expr\":\n\bOperator\022\f\n\004name\030\001 \002(\t\022 \n\005param\030\002 \003(\0132\021.Mysqlx.Expr.Expr\"t\n\006Object\022,\n\003fld\030\001 \003(\0132\037.Mysqlx.Expr.Object.ObjectField\032<\n\013ObjectField\022\013\n\003key\030\001 \002(\t\022 \n\005value\030\002 \002(\0132\021.Mysqlx.Expr.Expr\")\n\005Array\022 \n\005value\030\001 \003(\0132\021.Mysqlx.Expr.ExprB\031\n\027com.mysql.cj.x.protobuf" };
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 11055 */     descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] {
/*       */           
/* 11057 */           MysqlxDatatypes.getDescriptor()
/*       */         });
/*       */     
/* 11060 */     internal_static_Mysqlx_Expr_Expr_descriptor = getDescriptor().getMessageTypes().get(0);
/* 11061 */     internal_static_Mysqlx_Expr_Expr_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Expr_descriptor, new String[] { "Type", "Identifier", "Variable", "Literal", "FunctionCall", "Operator", "Position", "Object", "Array" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 11066 */     internal_static_Mysqlx_Expr_Identifier_descriptor = getDescriptor().getMessageTypes().get(1);
/* 11067 */     internal_static_Mysqlx_Expr_Identifier_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Identifier_descriptor, new String[] { "Name", "SchemaName" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 11072 */     internal_static_Mysqlx_Expr_DocumentPathItem_descriptor = getDescriptor().getMessageTypes().get(2);
/* 11073 */     internal_static_Mysqlx_Expr_DocumentPathItem_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_DocumentPathItem_descriptor, new String[] { "Type", "Value", "Index" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 11078 */     internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor = getDescriptor().getMessageTypes().get(3);
/* 11079 */     internal_static_Mysqlx_Expr_ColumnIdentifier_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor, new String[] { "DocumentPath", "Name", "TableName", "SchemaName" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 11084 */     internal_static_Mysqlx_Expr_FunctionCall_descriptor = getDescriptor().getMessageTypes().get(4);
/* 11085 */     internal_static_Mysqlx_Expr_FunctionCall_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_FunctionCall_descriptor, new String[] { "Name", "Param" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 11090 */     internal_static_Mysqlx_Expr_Operator_descriptor = getDescriptor().getMessageTypes().get(5);
/* 11091 */     internal_static_Mysqlx_Expr_Operator_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Operator_descriptor, new String[] { "Name", "Param" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 11096 */     internal_static_Mysqlx_Expr_Object_descriptor = getDescriptor().getMessageTypes().get(6);
/* 11097 */     internal_static_Mysqlx_Expr_Object_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Object_descriptor, new String[] { "Fld" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 11102 */     internal_static_Mysqlx_Expr_Object_ObjectField_descriptor = internal_static_Mysqlx_Expr_Object_descriptor.getNestedTypes().get(0);
/* 11103 */     internal_static_Mysqlx_Expr_Object_ObjectField_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Object_ObjectField_descriptor, new String[] { "Key", "Value" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 11108 */     internal_static_Mysqlx_Expr_Array_descriptor = getDescriptor().getMessageTypes().get(7);
/* 11109 */     internal_static_Mysqlx_Expr_Array_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Array_descriptor, new String[] { "Value" });
/*       */ 
/*       */ 
/*       */     
/* 11113 */     MysqlxDatatypes.getDescriptor();
/*       */   }
/*       */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\x\protobuf\MysqlxExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */