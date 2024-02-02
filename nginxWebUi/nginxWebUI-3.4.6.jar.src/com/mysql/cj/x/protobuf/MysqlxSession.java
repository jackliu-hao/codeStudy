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
/*      */ import com.google.protobuf.UnknownFieldSet;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class MysqlxSession
/*      */ {
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Session_AuthenticateStart_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Session_AuthenticateStart_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Session_AuthenticateContinue_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Session_AuthenticateContinue_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Session_AuthenticateOk_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Session_AuthenticateOk_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Session_Reset_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Session_Reset_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Session_Close_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Session_Close_fieldAccessorTable;
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
/*      */   public static interface AuthenticateStartOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasMechName();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String getMechName();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getMechNameBytes();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasAuthData();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getAuthData();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasInitialResponse();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getInitialResponse();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class AuthenticateStart
/*      */     extends GeneratedMessageV3
/*      */     implements AuthenticateStartOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int MECH_NAME_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */     
/*      */     private volatile Object mechName_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int AUTH_DATA_FIELD_NUMBER = 2;
/*      */ 
/*      */ 
/*      */     
/*      */     private ByteString authData_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int INITIAL_RESPONSE_FIELD_NUMBER = 3;
/*      */ 
/*      */ 
/*      */     
/*      */     private ByteString initialResponse_;
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private AuthenticateStart(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/*  134 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  329 */       this.memoizedIsInitialized = -1; } private AuthenticateStart() { this.memoizedIsInitialized = -1; this.mechName_ = ""; this.authData_ = ByteString.EMPTY; this.initialResponse_ = ByteString.EMPTY; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new AuthenticateStart(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private AuthenticateStart(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: bs = input.readBytes(); this.bitField0_ |= 0x1; this.mechName_ = bs; continue;case 18: this.bitField0_ |= 0x2; this.authData_ = input.readBytes(); continue;case 26: this.bitField0_ |= 0x4; this.initialResponse_ = input.readBytes(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateStart_descriptor; }
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateStart_fieldAccessorTable.ensureFieldAccessorsInitialized(AuthenticateStart.class, Builder.class); }
/*      */     public boolean hasMechName() { return ((this.bitField0_ & 0x1) != 0); }
/*  332 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  333 */       if (isInitialized == 1) return true; 
/*  334 */       if (isInitialized == 0) return false;
/*      */       
/*  336 */       if (!hasMechName()) {
/*  337 */         this.memoizedIsInitialized = 0;
/*  338 */         return false;
/*      */       } 
/*  340 */       this.memoizedIsInitialized = 1;
/*  341 */       return true; } public String getMechName() { Object ref = this.mechName_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.mechName_ = s;  return s; }
/*      */     public ByteString getMechNameBytes() { Object ref = this.mechName_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.mechName_ = b; return b; }  return (ByteString)ref; }
/*      */     public boolean hasAuthData() { return ((this.bitField0_ & 0x2) != 0); }
/*      */     public ByteString getAuthData() { return this.authData_; }
/*      */     public boolean hasInitialResponse() { return ((this.bitField0_ & 0x4) != 0); }
/*      */     public ByteString getInitialResponse() { return this.initialResponse_; }
/*  347 */     public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) {
/*  348 */         GeneratedMessageV3.writeString(output, 1, this.mechName_);
/*      */       }
/*  350 */       if ((this.bitField0_ & 0x2) != 0) {
/*  351 */         output.writeBytes(2, this.authData_);
/*      */       }
/*  353 */       if ((this.bitField0_ & 0x4) != 0) {
/*  354 */         output.writeBytes(3, this.initialResponse_);
/*      */       }
/*  356 */       this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/*  361 */       int size = this.memoizedSize;
/*  362 */       if (size != -1) return size;
/*      */       
/*  364 */       size = 0;
/*  365 */       if ((this.bitField0_ & 0x1) != 0) {
/*  366 */         size += GeneratedMessageV3.computeStringSize(1, this.mechName_);
/*      */       }
/*  368 */       if ((this.bitField0_ & 0x2) != 0) {
/*  369 */         size += 
/*  370 */           CodedOutputStream.computeBytesSize(2, this.authData_);
/*      */       }
/*  372 */       if ((this.bitField0_ & 0x4) != 0) {
/*  373 */         size += 
/*  374 */           CodedOutputStream.computeBytesSize(3, this.initialResponse_);
/*      */       }
/*  376 */       size += this.unknownFields.getSerializedSize();
/*  377 */       this.memoizedSize = size;
/*  378 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  383 */       if (obj == this) {
/*  384 */         return true;
/*      */       }
/*  386 */       if (!(obj instanceof AuthenticateStart)) {
/*  387 */         return super.equals(obj);
/*      */       }
/*  389 */       AuthenticateStart other = (AuthenticateStart)obj;
/*      */       
/*  391 */       if (hasMechName() != other.hasMechName()) return false; 
/*  392 */       if (hasMechName() && 
/*      */         
/*  394 */         !getMechName().equals(other.getMechName())) return false;
/*      */       
/*  396 */       if (hasAuthData() != other.hasAuthData()) return false; 
/*  397 */       if (hasAuthData() && 
/*      */         
/*  399 */         !getAuthData().equals(other.getAuthData())) return false;
/*      */       
/*  401 */       if (hasInitialResponse() != other.hasInitialResponse()) return false; 
/*  402 */       if (hasInitialResponse() && 
/*      */         
/*  404 */         !getInitialResponse().equals(other.getInitialResponse())) return false;
/*      */       
/*  406 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  407 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  412 */       if (this.memoizedHashCode != 0) {
/*  413 */         return this.memoizedHashCode;
/*      */       }
/*  415 */       int hash = 41;
/*  416 */       hash = 19 * hash + getDescriptor().hashCode();
/*  417 */       if (hasMechName()) {
/*  418 */         hash = 37 * hash + 1;
/*  419 */         hash = 53 * hash + getMechName().hashCode();
/*      */       } 
/*  421 */       if (hasAuthData()) {
/*  422 */         hash = 37 * hash + 2;
/*  423 */         hash = 53 * hash + getAuthData().hashCode();
/*      */       } 
/*  425 */       if (hasInitialResponse()) {
/*  426 */         hash = 37 * hash + 3;
/*  427 */         hash = 53 * hash + getInitialResponse().hashCode();
/*      */       } 
/*  429 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  430 */       this.memoizedHashCode = hash;
/*  431 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateStart parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  437 */       return (AuthenticateStart)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateStart parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  443 */       return (AuthenticateStart)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static AuthenticateStart parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  448 */       return (AuthenticateStart)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateStart parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  454 */       return (AuthenticateStart)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static AuthenticateStart parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  458 */       return (AuthenticateStart)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateStart parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  464 */       return (AuthenticateStart)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static AuthenticateStart parseFrom(InputStream input) throws IOException {
/*  468 */       return 
/*  469 */         (AuthenticateStart)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateStart parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  475 */       return 
/*  476 */         (AuthenticateStart)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static AuthenticateStart parseDelimitedFrom(InputStream input) throws IOException {
/*  480 */       return 
/*  481 */         (AuthenticateStart)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateStart parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  487 */       return 
/*  488 */         (AuthenticateStart)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static AuthenticateStart parseFrom(CodedInputStream input) throws IOException {
/*  493 */       return 
/*  494 */         (AuthenticateStart)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateStart parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  500 */       return 
/*  501 */         (AuthenticateStart)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/*  505 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/*  507 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(AuthenticateStart prototype) {
/*  510 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/*  514 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  515 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  521 */       Builder builder = new Builder(parent);
/*  522 */       return builder;
/*      */     }
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxSession.AuthenticateStartOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */       
/*      */       private Object mechName_;
/*      */       
/*      */       private ByteString authData_;
/*      */       
/*      */       private ByteString initialResponse_;
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/*  540 */         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateStart_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  546 */         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateStart_fieldAccessorTable
/*  547 */           .ensureFieldAccessorsInitialized(MysqlxSession.AuthenticateStart.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/*  708 */         this.mechName_ = "";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  816 */         this.authData_ = ByteString.EMPTY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  872 */         this.initialResponse_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxSession.AuthenticateStart.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.mechName_ = ""; this.bitField0_ &= 0xFFFFFFFE; this.authData_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFFD; this.initialResponse_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFFB; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateStart_descriptor; } public MysqlxSession.AuthenticateStart getDefaultInstanceForType() { return MysqlxSession.AuthenticateStart.getDefaultInstance(); } public MysqlxSession.AuthenticateStart build() { MysqlxSession.AuthenticateStart result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxSession.AuthenticateStart buildPartial() { MysqlxSession.AuthenticateStart result = new MysqlxSession.AuthenticateStart(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.mechName_ = this.mechName_; if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.authData_ = this.authData_; if ((from_bitField0_ & 0x4) != 0) to_bitField0_ |= 0x4;  result.initialResponse_ = this.initialResponse_; result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.mechName_ = ""; this.authData_ = ByteString.EMPTY; this.initialResponse_ = ByteString.EMPTY;
/*      */         maybeForceBuilderInitialization(); }
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); }
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); }
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); }
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); }
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); }
/*      */       public Builder mergeFrom(Message other) { if (other instanceof MysqlxSession.AuthenticateStart)
/*      */           return mergeFrom((MysqlxSession.AuthenticateStart)other); 
/*      */         super.mergeFrom(other);
/*  882 */         return this; } public boolean hasInitialResponse() { return ((this.bitField0_ & 0x4) != 0); } public Builder mergeFrom(MysqlxSession.AuthenticateStart other) { if (other == MysqlxSession.AuthenticateStart.getDefaultInstance()) return this;  if (other.hasMechName()) { this.bitField0_ |= 0x1; this.mechName_ = other.mechName_; onChanged(); }  if (other.hasAuthData())
/*      */           setAuthData(other.getAuthData());  if (other.hasInitialResponse())
/*      */           setInitialResponse(other.getInitialResponse());  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */       public final boolean isInitialized() { if (!hasMechName())
/*      */           return false;  return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxSession.AuthenticateStart parsedMessage = null; try { parsedMessage = (MysqlxSession.AuthenticateStart)MysqlxSession.AuthenticateStart.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxSession.AuthenticateStart)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null)
/*      */             mergeFrom(parsedMessage);  }  return this; }
/*      */       public boolean hasMechName() { return ((this.bitField0_ & 0x1) != 0); }
/*      */       public String getMechName() { Object ref = this.mechName_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8())
/*      */             this.mechName_ = s;  return s; }  return (String)ref; }
/*      */       public ByteString getMechNameBytes() { Object ref = this.mechName_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.mechName_ = b; return b; }  return (ByteString)ref; }
/*  893 */       public ByteString getInitialResponse() { return this.initialResponse_; } public Builder setMechName(String value) { if (value == null)
/*      */           throw new NullPointerException();  this.bitField0_ |= 0x1; this.mechName_ = value; onChanged(); return this; }
/*      */       public Builder clearMechName() { this.bitField0_ &= 0xFFFFFFFE; this.mechName_ = MysqlxSession.AuthenticateStart.getDefaultInstance().getMechName(); onChanged(); return this; }
/*      */       public Builder setMechNameBytes(ByteString value) { if (value == null)
/*      */           throw new NullPointerException();  this.bitField0_ |= 0x1; this.mechName_ = value; onChanged(); return this; }
/*      */       public boolean hasAuthData() { return ((this.bitField0_ & 0x2) != 0); }
/*      */       public ByteString getAuthData() { return this.authData_; }
/*      */       public Builder setAuthData(ByteString value) { if (value == null)
/*      */           throw new NullPointerException();  this.bitField0_ |= 0x2; this.authData_ = value; onChanged(); return this; }
/*      */       public Builder clearAuthData() { this.bitField0_ &= 0xFFFFFFFD; this.authData_ = MysqlxSession.AuthenticateStart.getDefaultInstance().getAuthData();
/*      */         onChanged();
/*      */         return this; }
/*  905 */       public Builder setInitialResponse(ByteString value) { if (value == null) {
/*  906 */           throw new NullPointerException();
/*      */         }
/*  908 */         this.bitField0_ |= 0x4;
/*  909 */         this.initialResponse_ = value;
/*  910 */         onChanged();
/*  911 */         return this; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearInitialResponse() {
/*  922 */         this.bitField0_ &= 0xFFFFFFFB;
/*  923 */         this.initialResponse_ = MysqlxSession.AuthenticateStart.getDefaultInstance().getInitialResponse();
/*  924 */         onChanged();
/*  925 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  930 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  936 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  946 */     private static final AuthenticateStart DEFAULT_INSTANCE = new AuthenticateStart();
/*      */ 
/*      */     
/*      */     public static AuthenticateStart getDefaultInstance() {
/*  950 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/*  954 */     public static final Parser<AuthenticateStart> PARSER = (Parser<AuthenticateStart>)new AbstractParser<AuthenticateStart>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxSession.AuthenticateStart parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/*  960 */           return new MysqlxSession.AuthenticateStart(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<AuthenticateStart> parser() {
/*  965 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<AuthenticateStart> getParserForType() {
/*  970 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public AuthenticateStart getDefaultInstanceForType() {
/*  975 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface AuthenticateContinueOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasAuthData();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getAuthData();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class AuthenticateContinue
/*      */     extends GeneratedMessageV3
/*      */     implements AuthenticateContinueOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int AUTH_DATA_FIELD_NUMBER = 1;
/*      */ 
/*      */     
/*      */     private ByteString authData_;
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */     
/*      */     private AuthenticateContinue(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 1020 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1120 */       this.memoizedIsInitialized = -1; } private AuthenticateContinue() { this.memoizedIsInitialized = -1; this.authData_ = ByteString.EMPTY; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new AuthenticateContinue(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private AuthenticateContinue(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: this.bitField0_ |= 0x1; this.authData_ = input.readBytes(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateContinue_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateContinue_fieldAccessorTable.ensureFieldAccessorsInitialized(AuthenticateContinue.class, Builder.class); }
/*      */     public boolean hasAuthData() { return ((this.bitField0_ & 0x1) != 0); }
/*      */     public ByteString getAuthData() { return this.authData_; }
/* 1123 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 1124 */       if (isInitialized == 1) return true; 
/* 1125 */       if (isInitialized == 0) return false;
/*      */       
/* 1127 */       if (!hasAuthData()) {
/* 1128 */         this.memoizedIsInitialized = 0;
/* 1129 */         return false;
/*      */       } 
/* 1131 */       this.memoizedIsInitialized = 1;
/* 1132 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 1138 */       if ((this.bitField0_ & 0x1) != 0) {
/* 1139 */         output.writeBytes(1, this.authData_);
/*      */       }
/* 1141 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 1146 */       int size = this.memoizedSize;
/* 1147 */       if (size != -1) return size;
/*      */       
/* 1149 */       size = 0;
/* 1150 */       if ((this.bitField0_ & 0x1) != 0) {
/* 1151 */         size += 
/* 1152 */           CodedOutputStream.computeBytesSize(1, this.authData_);
/*      */       }
/* 1154 */       size += this.unknownFields.getSerializedSize();
/* 1155 */       this.memoizedSize = size;
/* 1156 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1161 */       if (obj == this) {
/* 1162 */         return true;
/*      */       }
/* 1164 */       if (!(obj instanceof AuthenticateContinue)) {
/* 1165 */         return super.equals(obj);
/*      */       }
/* 1167 */       AuthenticateContinue other = (AuthenticateContinue)obj;
/*      */       
/* 1169 */       if (hasAuthData() != other.hasAuthData()) return false; 
/* 1170 */       if (hasAuthData() && 
/*      */         
/* 1172 */         !getAuthData().equals(other.getAuthData())) return false;
/*      */       
/* 1174 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 1175 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1180 */       if (this.memoizedHashCode != 0) {
/* 1181 */         return this.memoizedHashCode;
/*      */       }
/* 1183 */       int hash = 41;
/* 1184 */       hash = 19 * hash + getDescriptor().hashCode();
/* 1185 */       if (hasAuthData()) {
/* 1186 */         hash = 37 * hash + 1;
/* 1187 */         hash = 53 * hash + getAuthData().hashCode();
/*      */       } 
/* 1189 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 1190 */       this.memoizedHashCode = hash;
/* 1191 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateContinue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 1197 */       return (AuthenticateContinue)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateContinue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1203 */       return (AuthenticateContinue)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static AuthenticateContinue parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 1208 */       return (AuthenticateContinue)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateContinue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1214 */       return (AuthenticateContinue)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static AuthenticateContinue parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 1218 */       return (AuthenticateContinue)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateContinue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1224 */       return (AuthenticateContinue)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static AuthenticateContinue parseFrom(InputStream input) throws IOException {
/* 1228 */       return 
/* 1229 */         (AuthenticateContinue)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateContinue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1235 */       return 
/* 1236 */         (AuthenticateContinue)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static AuthenticateContinue parseDelimitedFrom(InputStream input) throws IOException {
/* 1240 */       return 
/* 1241 */         (AuthenticateContinue)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateContinue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1247 */       return 
/* 1248 */         (AuthenticateContinue)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static AuthenticateContinue parseFrom(CodedInputStream input) throws IOException {
/* 1253 */       return 
/* 1254 */         (AuthenticateContinue)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateContinue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1260 */       return 
/* 1261 */         (AuthenticateContinue)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 1265 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 1267 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(AuthenticateContinue prototype) {
/* 1270 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 1274 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 1275 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 1281 */       Builder builder = new Builder(parent);
/* 1282 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxSession.AuthenticateContinueOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */       
/*      */       private ByteString authData_;
/*      */ 
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 1300 */         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateContinue_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 1306 */         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateContinue_fieldAccessorTable
/* 1307 */           .ensureFieldAccessorsInitialized(MysqlxSession.AuthenticateContinue.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/* 1448 */         this.authData_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.authData_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxSession.AuthenticateContinue.alwaysUseFieldBuilders); }
/*      */       public Builder clear() { super.clear(); this.authData_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFFE; return this; }
/*      */       public Descriptors.Descriptor getDescriptorForType() { return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateContinue_descriptor; }
/*      */       public MysqlxSession.AuthenticateContinue getDefaultInstanceForType() { return MysqlxSession.AuthenticateContinue.getDefaultInstance(); }
/*      */       public MysqlxSession.AuthenticateContinue build() { MysqlxSession.AuthenticateContinue result = buildPartial(); if (!result.isInitialized())
/*      */           throw newUninitializedMessageException(result);  return result; }
/*      */       public MysqlxSession.AuthenticateContinue buildPartial() { MysqlxSession.AuthenticateContinue result = new MysqlxSession.AuthenticateContinue(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0)
/*      */           to_bitField0_ |= 0x1;  result.authData_ = this.authData_; result.bitField0_ = to_bitField0_; onBuilt(); return result; }
/*      */       public Builder clone() { return (Builder)super.clone(); }
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); }
/* 1458 */       public boolean hasAuthData() { return ((this.bitField0_ & 0x1) != 0); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); }
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); }
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); }
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); }
/*      */       public Builder mergeFrom(Message other) { if (other instanceof MysqlxSession.AuthenticateContinue) return mergeFrom((MysqlxSession.AuthenticateContinue)other);  super.mergeFrom(other); return this; }
/*      */       public Builder mergeFrom(MysqlxSession.AuthenticateContinue other) { if (other == MysqlxSession.AuthenticateContinue.getDefaultInstance()) return this;  if (other.hasAuthData())
/*      */           setAuthData(other.getAuthData());  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */       public final boolean isInitialized() { if (!hasAuthData())
/*      */           return false;  return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxSession.AuthenticateContinue parsedMessage = null; try { parsedMessage = (MysqlxSession.AuthenticateContinue)MysqlxSession.AuthenticateContinue.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxSession.AuthenticateContinue)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null)
/*      */             mergeFrom(parsedMessage);  }  return this; }
/* 1469 */       public ByteString getAuthData() { return this.authData_; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setAuthData(ByteString value) {
/* 1481 */         if (value == null) {
/* 1482 */           throw new NullPointerException();
/*      */         }
/* 1484 */         this.bitField0_ |= 0x1;
/* 1485 */         this.authData_ = value;
/* 1486 */         onChanged();
/* 1487 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearAuthData() {
/* 1498 */         this.bitField0_ &= 0xFFFFFFFE;
/* 1499 */         this.authData_ = MysqlxSession.AuthenticateContinue.getDefaultInstance().getAuthData();
/* 1500 */         onChanged();
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
/* 1522 */     private static final AuthenticateContinue DEFAULT_INSTANCE = new AuthenticateContinue();
/*      */ 
/*      */     
/*      */     public static AuthenticateContinue getDefaultInstance() {
/* 1526 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 1530 */     public static final Parser<AuthenticateContinue> PARSER = (Parser<AuthenticateContinue>)new AbstractParser<AuthenticateContinue>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxSession.AuthenticateContinue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 1536 */           return new MysqlxSession.AuthenticateContinue(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<AuthenticateContinue> parser() {
/* 1541 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<AuthenticateContinue> getParserForType() {
/* 1546 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public AuthenticateContinue getDefaultInstanceForType() {
/* 1551 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface AuthenticateOkOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasAuthData();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getAuthData();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class AuthenticateOk
/*      */     extends GeneratedMessageV3
/*      */     implements AuthenticateOkOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */     
/*      */     public static final int AUTH_DATA_FIELD_NUMBER = 1;
/*      */ 
/*      */     
/*      */     private ByteString authData_;
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */     
/*      */     private AuthenticateOk(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 1594 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1694 */       this.memoizedIsInitialized = -1; } private AuthenticateOk() { this.memoizedIsInitialized = -1; this.authData_ = ByteString.EMPTY; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new AuthenticateOk(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private AuthenticateOk(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: this.bitField0_ |= 0x1; this.authData_ = input.readBytes(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateOk_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateOk_fieldAccessorTable.ensureFieldAccessorsInitialized(AuthenticateOk.class, Builder.class); }
/*      */     public boolean hasAuthData() { return ((this.bitField0_ & 0x1) != 0); }
/*      */     public ByteString getAuthData() { return this.authData_; }
/* 1697 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 1698 */       if (isInitialized == 1) return true; 
/* 1699 */       if (isInitialized == 0) return false;
/*      */       
/* 1701 */       this.memoizedIsInitialized = 1;
/* 1702 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 1708 */       if ((this.bitField0_ & 0x1) != 0) {
/* 1709 */         output.writeBytes(1, this.authData_);
/*      */       }
/* 1711 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 1716 */       int size = this.memoizedSize;
/* 1717 */       if (size != -1) return size;
/*      */       
/* 1719 */       size = 0;
/* 1720 */       if ((this.bitField0_ & 0x1) != 0) {
/* 1721 */         size += 
/* 1722 */           CodedOutputStream.computeBytesSize(1, this.authData_);
/*      */       }
/* 1724 */       size += this.unknownFields.getSerializedSize();
/* 1725 */       this.memoizedSize = size;
/* 1726 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1731 */       if (obj == this) {
/* 1732 */         return true;
/*      */       }
/* 1734 */       if (!(obj instanceof AuthenticateOk)) {
/* 1735 */         return super.equals(obj);
/*      */       }
/* 1737 */       AuthenticateOk other = (AuthenticateOk)obj;
/*      */       
/* 1739 */       if (hasAuthData() != other.hasAuthData()) return false; 
/* 1740 */       if (hasAuthData() && 
/*      */         
/* 1742 */         !getAuthData().equals(other.getAuthData())) return false;
/*      */       
/* 1744 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 1745 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1750 */       if (this.memoizedHashCode != 0) {
/* 1751 */         return this.memoizedHashCode;
/*      */       }
/* 1753 */       int hash = 41;
/* 1754 */       hash = 19 * hash + getDescriptor().hashCode();
/* 1755 */       if (hasAuthData()) {
/* 1756 */         hash = 37 * hash + 1;
/* 1757 */         hash = 53 * hash + getAuthData().hashCode();
/*      */       } 
/* 1759 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 1760 */       this.memoizedHashCode = hash;
/* 1761 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateOk parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 1767 */       return (AuthenticateOk)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateOk parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1773 */       return (AuthenticateOk)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static AuthenticateOk parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 1778 */       return (AuthenticateOk)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateOk parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1784 */       return (AuthenticateOk)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static AuthenticateOk parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 1788 */       return (AuthenticateOk)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateOk parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1794 */       return (AuthenticateOk)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static AuthenticateOk parseFrom(InputStream input) throws IOException {
/* 1798 */       return 
/* 1799 */         (AuthenticateOk)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateOk parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1805 */       return 
/* 1806 */         (AuthenticateOk)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static AuthenticateOk parseDelimitedFrom(InputStream input) throws IOException {
/* 1810 */       return 
/* 1811 */         (AuthenticateOk)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateOk parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1817 */       return 
/* 1818 */         (AuthenticateOk)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static AuthenticateOk parseFrom(CodedInputStream input) throws IOException {
/* 1823 */       return 
/* 1824 */         (AuthenticateOk)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static AuthenticateOk parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1830 */       return 
/* 1831 */         (AuthenticateOk)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 1835 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 1837 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(AuthenticateOk prototype) {
/* 1840 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 1844 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 1845 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 1851 */       Builder builder = new Builder(parent);
/* 1852 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxSession.AuthenticateOkOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */       
/*      */       private ByteString authData_;
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 1868 */         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateOk_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 1874 */         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateOk_fieldAccessorTable
/* 1875 */           .ensureFieldAccessorsInitialized(MysqlxSession.AuthenticateOk.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/* 2013 */         this.authData_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.authData_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxSession.AuthenticateOk.alwaysUseFieldBuilders); }
/*      */       public Builder clear() { super.clear(); this.authData_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFFE; return this; }
/*      */       public Descriptors.Descriptor getDescriptorForType() { return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateOk_descriptor; }
/*      */       public MysqlxSession.AuthenticateOk getDefaultInstanceForType() { return MysqlxSession.AuthenticateOk.getDefaultInstance(); }
/*      */       public MysqlxSession.AuthenticateOk build() { MysqlxSession.AuthenticateOk result = buildPartial(); if (!result.isInitialized())
/*      */           throw newUninitializedMessageException(result);  return result; }
/*      */       public MysqlxSession.AuthenticateOk buildPartial() { MysqlxSession.AuthenticateOk result = new MysqlxSession.AuthenticateOk(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0)
/*      */           to_bitField0_ |= 0x1;  result.authData_ = this.authData_; result.bitField0_ = to_bitField0_; onBuilt(); return result; }
/*      */       public Builder clone() { return (Builder)super.clone(); }
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); }
/* 2023 */       public boolean hasAuthData() { return ((this.bitField0_ & 0x1) != 0); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); }
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); }
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); }
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); }
/*      */       public Builder mergeFrom(Message other) { if (other instanceof MysqlxSession.AuthenticateOk) return mergeFrom((MysqlxSession.AuthenticateOk)other);  super.mergeFrom(other); return this; }
/*      */       public Builder mergeFrom(MysqlxSession.AuthenticateOk other) { if (other == MysqlxSession.AuthenticateOk.getDefaultInstance())
/*      */           return this;  if (other.hasAuthData())
/*      */           setAuthData(other.getAuthData());  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */       public final boolean isInitialized() { return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxSession.AuthenticateOk parsedMessage = null; try { parsedMessage = (MysqlxSession.AuthenticateOk)MysqlxSession.AuthenticateOk.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxSession.AuthenticateOk)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null)
/*      */             mergeFrom(parsedMessage);  }  return this; }
/* 2034 */       public ByteString getAuthData() { return this.authData_; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setAuthData(ByteString value) {
/* 2046 */         if (value == null) {
/* 2047 */           throw new NullPointerException();
/*      */         }
/* 2049 */         this.bitField0_ |= 0x1;
/* 2050 */         this.authData_ = value;
/* 2051 */         onChanged();
/* 2052 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearAuthData() {
/* 2063 */         this.bitField0_ &= 0xFFFFFFFE;
/* 2064 */         this.authData_ = MysqlxSession.AuthenticateOk.getDefaultInstance().getAuthData();
/* 2065 */         onChanged();
/* 2066 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 2071 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 2077 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2087 */     private static final AuthenticateOk DEFAULT_INSTANCE = new AuthenticateOk();
/*      */ 
/*      */     
/*      */     public static AuthenticateOk getDefaultInstance() {
/* 2091 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 2095 */     public static final Parser<AuthenticateOk> PARSER = (Parser<AuthenticateOk>)new AbstractParser<AuthenticateOk>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxSession.AuthenticateOk parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 2101 */           return new MysqlxSession.AuthenticateOk(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<AuthenticateOk> parser() {
/* 2106 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<AuthenticateOk> getParserForType() {
/* 2111 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public AuthenticateOk getDefaultInstanceForType() {
/* 2116 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface ResetOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasKeepOpen();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean getKeepOpen();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Reset
/*      */     extends GeneratedMessageV3
/*      */     implements ResetOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int KEEP_OPEN_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */     
/*      */     private boolean keepOpen_;
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */     
/*      */     private Reset(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 2162 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2263 */       this.memoizedIsInitialized = -1; } private Reset() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Reset(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Reset(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.bitField0_ |= 0x1; this.keepOpen_ = input.readBool(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxSession.internal_static_Mysqlx_Session_Reset_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxSession.internal_static_Mysqlx_Session_Reset_fieldAccessorTable.ensureFieldAccessorsInitialized(Reset.class, Builder.class); }
/*      */     public boolean hasKeepOpen() { return ((this.bitField0_ & 0x1) != 0); }
/*      */     public boolean getKeepOpen() { return this.keepOpen_; }
/* 2266 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 2267 */       if (isInitialized == 1) return true; 
/* 2268 */       if (isInitialized == 0) return false;
/*      */       
/* 2270 */       this.memoizedIsInitialized = 1;
/* 2271 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 2277 */       if ((this.bitField0_ & 0x1) != 0) {
/* 2278 */         output.writeBool(1, this.keepOpen_);
/*      */       }
/* 2280 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 2285 */       int size = this.memoizedSize;
/* 2286 */       if (size != -1) return size;
/*      */       
/* 2288 */       size = 0;
/* 2289 */       if ((this.bitField0_ & 0x1) != 0) {
/* 2290 */         size += 
/* 2291 */           CodedOutputStream.computeBoolSize(1, this.keepOpen_);
/*      */       }
/* 2293 */       size += this.unknownFields.getSerializedSize();
/* 2294 */       this.memoizedSize = size;
/* 2295 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 2300 */       if (obj == this) {
/* 2301 */         return true;
/*      */       }
/* 2303 */       if (!(obj instanceof Reset)) {
/* 2304 */         return super.equals(obj);
/*      */       }
/* 2306 */       Reset other = (Reset)obj;
/*      */       
/* 2308 */       if (hasKeepOpen() != other.hasKeepOpen()) return false; 
/* 2309 */       if (hasKeepOpen() && 
/* 2310 */         getKeepOpen() != other
/* 2311 */         .getKeepOpen()) return false;
/*      */       
/* 2313 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 2314 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 2319 */       if (this.memoizedHashCode != 0) {
/* 2320 */         return this.memoizedHashCode;
/*      */       }
/* 2322 */       int hash = 41;
/* 2323 */       hash = 19 * hash + getDescriptor().hashCode();
/* 2324 */       if (hasKeepOpen()) {
/* 2325 */         hash = 37 * hash + 1;
/* 2326 */         hash = 53 * hash + Internal.hashBoolean(
/* 2327 */             getKeepOpen());
/*      */       } 
/* 2329 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 2330 */       this.memoizedHashCode = hash;
/* 2331 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Reset parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 2337 */       return (Reset)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Reset parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2343 */       return (Reset)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Reset parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 2348 */       return (Reset)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Reset parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2354 */       return (Reset)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Reset parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 2358 */       return (Reset)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Reset parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2364 */       return (Reset)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Reset parseFrom(InputStream input) throws IOException {
/* 2368 */       return 
/* 2369 */         (Reset)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Reset parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2375 */       return 
/* 2376 */         (Reset)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Reset parseDelimitedFrom(InputStream input) throws IOException {
/* 2380 */       return 
/* 2381 */         (Reset)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Reset parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2387 */       return 
/* 2388 */         (Reset)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Reset parseFrom(CodedInputStream input) throws IOException {
/* 2393 */       return 
/* 2394 */         (Reset)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Reset parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2400 */       return 
/* 2401 */         (Reset)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 2405 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 2407 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Reset prototype) {
/* 2410 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 2414 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 2415 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 2421 */       Builder builder = new Builder(parent);
/* 2422 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxSession.ResetOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */       
/*      */       private boolean keepOpen_;
/*      */ 
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 2439 */         return MysqlxSession.internal_static_Mysqlx_Session_Reset_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 2445 */         return MysqlxSession.internal_static_Mysqlx_Session_Reset_fieldAccessorTable
/* 2446 */           .ensureFieldAccessorsInitialized(MysqlxSession.Reset.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 2452 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 2457 */         super(parent);
/* 2458 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 2462 */         if (MysqlxSession.Reset.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/* 2467 */         super.clear();
/* 2468 */         this.keepOpen_ = false;
/* 2469 */         this.bitField0_ &= 0xFFFFFFFE;
/* 2470 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 2476 */         return MysqlxSession.internal_static_Mysqlx_Session_Reset_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxSession.Reset getDefaultInstanceForType() {
/* 2481 */         return MysqlxSession.Reset.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxSession.Reset build() {
/* 2486 */         MysqlxSession.Reset result = buildPartial();
/* 2487 */         if (!result.isInitialized()) {
/* 2488 */           throw newUninitializedMessageException(result);
/*      */         }
/* 2490 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxSession.Reset buildPartial() {
/* 2495 */         MysqlxSession.Reset result = new MysqlxSession.Reset(this);
/* 2496 */         int from_bitField0_ = this.bitField0_;
/* 2497 */         int to_bitField0_ = 0;
/* 2498 */         if ((from_bitField0_ & 0x1) != 0) {
/* 2499 */           result.keepOpen_ = this.keepOpen_;
/* 2500 */           to_bitField0_ |= 0x1;
/*      */         } 
/* 2502 */         result.bitField0_ = to_bitField0_;
/* 2503 */         onBuilt();
/* 2504 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 2509 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 2515 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 2520 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 2525 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 2531 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 2537 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 2541 */         if (other instanceof MysqlxSession.Reset) {
/* 2542 */           return mergeFrom((MysqlxSession.Reset)other);
/*      */         }
/* 2544 */         super.mergeFrom(other);
/* 2545 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxSession.Reset other) {
/* 2550 */         if (other == MysqlxSession.Reset.getDefaultInstance()) return this; 
/* 2551 */         if (other.hasKeepOpen()) {
/* 2552 */           setKeepOpen(other.getKeepOpen());
/*      */         }
/* 2554 */         mergeUnknownFields(other.unknownFields);
/* 2555 */         onChanged();
/* 2556 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 2561 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2569 */         MysqlxSession.Reset parsedMessage = null;
/*      */         try {
/* 2571 */           parsedMessage = (MysqlxSession.Reset)MysqlxSession.Reset.PARSER.parsePartialFrom(input, extensionRegistry);
/* 2572 */         } catch (InvalidProtocolBufferException e) {
/* 2573 */           parsedMessage = (MysqlxSession.Reset)e.getUnfinishedMessage();
/* 2574 */           throw e.unwrapIOException();
/*      */         } finally {
/* 2576 */           if (parsedMessage != null) {
/* 2577 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 2580 */         return this;
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
/*      */       public boolean hasKeepOpen() {
/* 2595 */         return ((this.bitField0_ & 0x1) != 0);
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
/*      */       public boolean getKeepOpen() {
/* 2607 */         return this.keepOpen_;
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
/*      */       public Builder setKeepOpen(boolean value) {
/* 2620 */         this.bitField0_ |= 0x1;
/* 2621 */         this.keepOpen_ = value;
/* 2622 */         onChanged();
/* 2623 */         return this;
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
/*      */       public Builder clearKeepOpen() {
/* 2635 */         this.bitField0_ &= 0xFFFFFFFE;
/* 2636 */         this.keepOpen_ = false;
/* 2637 */         onChanged();
/* 2638 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 2643 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 2649 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2659 */     private static final Reset DEFAULT_INSTANCE = new Reset();
/*      */ 
/*      */     
/*      */     public static Reset getDefaultInstance() {
/* 2663 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 2667 */     public static final Parser<Reset> PARSER = (Parser<Reset>)new AbstractParser<Reset>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxSession.Reset parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 2673 */           return new MysqlxSession.Reset(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Reset> parser() {
/* 2678 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Reset> getParserForType() {
/* 2683 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Reset getDefaultInstanceForType() {
/* 2688 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface CloseOrBuilder
/*      */     extends MessageOrBuilder {}
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
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */     
/*      */     private Close(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 2713 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2780 */       this.memoizedIsInitialized = -1; } private Close() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Close(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Close(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/*      */     public static final Descriptors.Descriptor getDescriptor() { return MysqlxSession.internal_static_Mysqlx_Session_Close_descriptor; }
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxSession.internal_static_Mysqlx_Session_Close_fieldAccessorTable.ensureFieldAccessorsInitialized(Close.class, Builder.class); }
/* 2783 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 2784 */       if (isInitialized == 1) return true; 
/* 2785 */       if (isInitialized == 0) return false;
/*      */       
/* 2787 */       this.memoizedIsInitialized = 1;
/* 2788 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 2794 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 2799 */       int size = this.memoizedSize;
/* 2800 */       if (size != -1) return size;
/*      */       
/* 2802 */       size = 0;
/* 2803 */       size += this.unknownFields.getSerializedSize();
/* 2804 */       this.memoizedSize = size;
/* 2805 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 2810 */       if (obj == this) {
/* 2811 */         return true;
/*      */       }
/* 2813 */       if (!(obj instanceof Close)) {
/* 2814 */         return super.equals(obj);
/*      */       }
/* 2816 */       Close other = (Close)obj;
/*      */       
/* 2818 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 2819 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 2824 */       if (this.memoizedHashCode != 0) {
/* 2825 */         return this.memoizedHashCode;
/*      */       }
/* 2827 */       int hash = 41;
/* 2828 */       hash = 19 * hash + getDescriptor().hashCode();
/* 2829 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 2830 */       this.memoizedHashCode = hash;
/* 2831 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 2837 */       return (Close)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2843 */       return (Close)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 2848 */       return (Close)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2854 */       return (Close)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Close parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 2858 */       return (Close)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2864 */       return (Close)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Close parseFrom(InputStream input) throws IOException {
/* 2868 */       return 
/* 2869 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2875 */       return 
/* 2876 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Close parseDelimitedFrom(InputStream input) throws IOException {
/* 2880 */       return 
/* 2881 */         (Close)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2887 */       return 
/* 2888 */         (Close)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Close parseFrom(CodedInputStream input) throws IOException {
/* 2893 */       return 
/* 2894 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2900 */       return 
/* 2901 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 2905 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 2907 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Close prototype) {
/* 2910 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 2914 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 2915 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 2921 */       Builder builder = new Builder(parent);
/* 2922 */       return builder;
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
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxSession.CloseOrBuilder
/*      */     {
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 2939 */         return MysqlxSession.internal_static_Mysqlx_Session_Close_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 2945 */         return MysqlxSession.internal_static_Mysqlx_Session_Close_fieldAccessorTable
/* 2946 */           .ensureFieldAccessorsInitialized(MysqlxSession.Close.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 2952 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 2957 */         super(parent);
/* 2958 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 2962 */         if (MysqlxSession.Close.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/* 2967 */         super.clear();
/* 2968 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 2974 */         return MysqlxSession.internal_static_Mysqlx_Session_Close_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxSession.Close getDefaultInstanceForType() {
/* 2979 */         return MysqlxSession.Close.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxSession.Close build() {
/* 2984 */         MysqlxSession.Close result = buildPartial();
/* 2985 */         if (!result.isInitialized()) {
/* 2986 */           throw newUninitializedMessageException(result);
/*      */         }
/* 2988 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxSession.Close buildPartial() {
/* 2993 */         MysqlxSession.Close result = new MysqlxSession.Close(this);
/* 2994 */         onBuilt();
/* 2995 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 3000 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 3006 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 3011 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 3016 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 3022 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 3028 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 3032 */         if (other instanceof MysqlxSession.Close) {
/* 3033 */           return mergeFrom((MysqlxSession.Close)other);
/*      */         }
/* 3035 */         super.mergeFrom(other);
/* 3036 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxSession.Close other) {
/* 3041 */         if (other == MysqlxSession.Close.getDefaultInstance()) return this; 
/* 3042 */         mergeUnknownFields(other.unknownFields);
/* 3043 */         onChanged();
/* 3044 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 3049 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3057 */         MysqlxSession.Close parsedMessage = null;
/*      */         try {
/* 3059 */           parsedMessage = (MysqlxSession.Close)MysqlxSession.Close.PARSER.parsePartialFrom(input, extensionRegistry);
/* 3060 */         } catch (InvalidProtocolBufferException e) {
/* 3061 */           parsedMessage = (MysqlxSession.Close)e.getUnfinishedMessage();
/* 3062 */           throw e.unwrapIOException();
/*      */         } finally {
/* 3064 */           if (parsedMessage != null) {
/* 3065 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 3068 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 3073 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 3079 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3089 */     private static final Close DEFAULT_INSTANCE = new Close();
/*      */ 
/*      */     
/*      */     public static Close getDefaultInstance() {
/* 3093 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 3097 */     public static final Parser<Close> PARSER = (Parser<Close>)new AbstractParser<Close>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxSession.Close parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 3103 */           return new MysqlxSession.Close(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Close> parser() {
/* 3108 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Close> getParserForType() {
/* 3113 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Close getDefaultInstanceForType() {
/* 3118 */       return DEFAULT_INSTANCE;
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
/*      */   public static Descriptors.FileDescriptor getDescriptor() {
/* 3151 */     return descriptor;
/*      */   }
/*      */ 
/*      */   
/*      */   static {
/* 3156 */     String[] descriptorData = { "\n\024mysqlx_session.proto\022\016Mysqlx.Session\032\fmysqlx.proto\"Y\n\021AuthenticateStart\022\021\n\tmech_name\030\001 \002(\t\022\021\n\tauth_data\030\002 \001(\f\022\030\n\020initial_response\030\003 \001(\f:\004ê0\004\"3\n\024AuthenticateContinue\022\021\n\tauth_data\030\001 \002(\f:\bê0\003ê0\005\")\n\016AuthenticateOk\022\021\n\tauth_data\030\001 \001(\f:\004ê0\004\"'\n\005Reset\022\030\n\tkeep_open\030\001 \001(\b:\005false:\004ê0\006\"\r\n\005Close:\004ê0\007B\031\n\027com.mysql.cj.x.protobuf" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3168 */     descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] {
/*      */           
/* 3170 */           Mysqlx.getDescriptor()
/*      */         });
/*      */     
/* 3173 */     internal_static_Mysqlx_Session_AuthenticateStart_descriptor = getDescriptor().getMessageTypes().get(0);
/* 3174 */     internal_static_Mysqlx_Session_AuthenticateStart_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Session_AuthenticateStart_descriptor, new String[] { "MechName", "AuthData", "InitialResponse" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3179 */     internal_static_Mysqlx_Session_AuthenticateContinue_descriptor = getDescriptor().getMessageTypes().get(1);
/* 3180 */     internal_static_Mysqlx_Session_AuthenticateContinue_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Session_AuthenticateContinue_descriptor, new String[] { "AuthData" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3185 */     internal_static_Mysqlx_Session_AuthenticateOk_descriptor = getDescriptor().getMessageTypes().get(2);
/* 3186 */     internal_static_Mysqlx_Session_AuthenticateOk_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Session_AuthenticateOk_descriptor, new String[] { "AuthData" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3191 */     internal_static_Mysqlx_Session_Reset_descriptor = getDescriptor().getMessageTypes().get(3);
/* 3192 */     internal_static_Mysqlx_Session_Reset_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Session_Reset_descriptor, new String[] { "KeepOpen" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3197 */     internal_static_Mysqlx_Session_Close_descriptor = getDescriptor().getMessageTypes().get(4);
/* 3198 */     internal_static_Mysqlx_Session_Close_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Session_Close_descriptor, new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3203 */     ExtensionRegistry registry = ExtensionRegistry.newInstance();
/* 3204 */     registry.add(Mysqlx.clientMessageId);
/* 3205 */     registry.add(Mysqlx.serverMessageId);
/*      */     
/* 3207 */     Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
/* 3208 */     Mysqlx.getDescriptor();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\x\protobuf\MysqlxSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */