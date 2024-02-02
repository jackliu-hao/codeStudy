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
/*      */ import com.google.protobuf.ProtocolMessageEnum;
/*      */ import com.google.protobuf.UnknownFieldSet;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ 
/*      */ public final class MysqlxNotice {
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_Frame_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_Frame_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_Warning_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_Warning_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_SessionVariableChanged_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_SessionStateChanged_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_SessionStateChanged_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_GroupReplicationStateChanged_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_ServerHello_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_ServerHello_fieldAccessorTable;
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
/*      */   public static interface FrameOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasType();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getType();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasScope();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxNotice.Frame.Scope getScope();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasPayload();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getPayload();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Frame
/*      */     extends GeneratedMessageV3
/*      */     implements FrameOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int TYPE_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */     
/*      */     private int type_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int SCOPE_FIELD_NUMBER = 2;
/*      */ 
/*      */ 
/*      */     
/*      */     private int scope_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int PAYLOAD_FIELD_NUMBER = 3;
/*      */ 
/*      */ 
/*      */     
/*      */     private ByteString payload_;
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Frame(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/*  130 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  527 */       this.memoizedIsInitialized = -1; } private Frame() { this.memoizedIsInitialized = -1; this.scope_ = 1; this.payload_ = ByteString.EMPTY; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Frame(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Frame(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int rawValue; Scope value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.bitField0_ |= 0x1; this.type_ = input.readUInt32(); continue;case 16: rawValue = input.readEnum(); value = Scope.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(2, rawValue); continue; }  this.bitField0_ |= 0x2; this.scope_ = rawValue; continue;case 26: this.bitField0_ |= 0x4; this.payload_ = input.readBytes(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxNotice.internal_static_Mysqlx_Notice_Frame_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxNotice.internal_static_Mysqlx_Notice_Frame_fieldAccessorTable.ensureFieldAccessorsInitialized(Frame.class, Builder.class); } public enum Scope implements ProtocolMessageEnum {
/*      */       GLOBAL(1), LOCAL(2); public static final int GLOBAL_VALUE = 1; public static final int LOCAL_VALUE = 2; private static final Internal.EnumLiteMap<Scope> internalValueMap = new Internal.EnumLiteMap<Scope>() { public MysqlxNotice.Frame.Scope findValueByNumber(int number) { return MysqlxNotice.Frame.Scope.forNumber(number); } }
/*      */       ; private static final Scope[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Scope forNumber(int value) { switch (value) { case 1: return GLOBAL;case 2: return LOCAL; }  return null; } public static Internal.EnumLiteMap<Scope> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxNotice.Frame.getDescriptor().getEnumTypes().get(0); } Scope(int value) { this.value = value; } }
/*  530 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  531 */       if (isInitialized == 1) return true; 
/*  532 */       if (isInitialized == 0) return false;
/*      */       
/*  534 */       if (!hasType()) {
/*  535 */         this.memoizedIsInitialized = 0;
/*  536 */         return false;
/*      */       } 
/*  538 */       this.memoizedIsInitialized = 1;
/*  539 */       return true; } public enum Type implements ProtocolMessageEnum {
/*      */       WARNING(1), SESSION_VARIABLE_CHANGED(2), SESSION_STATE_CHANGED(3), GROUP_REPLICATION_STATE_CHANGED(4), SERVER_HELLO(5); public static final int WARNING_VALUE = 1; public static final int SESSION_VARIABLE_CHANGED_VALUE = 2; public static final int SESSION_STATE_CHANGED_VALUE = 3; public static final int GROUP_REPLICATION_STATE_CHANGED_VALUE = 4; public static final int SERVER_HELLO_VALUE = 5; private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() { public MysqlxNotice.Frame.Type findValueByNumber(int number) { return MysqlxNotice.Frame.Type.forNumber(number); } }
/*      */       ; private static final Type[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Type forNumber(int value) { switch (value) { case 1: return WARNING;case 2: return SESSION_VARIABLE_CHANGED;case 3: return SESSION_STATE_CHANGED;case 4: return GROUP_REPLICATION_STATE_CHANGED;case 5: return SERVER_HELLO; }  return null; } public static Internal.EnumLiteMap<Type> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxNotice.Frame.getDescriptor().getEnumTypes().get(1); } Type(int value) { this.value = value; } } public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); } public int getType() { return this.type_; } public boolean hasScope() { return ((this.bitField0_ & 0x2) != 0); }
/*      */     public Scope getScope() { Scope result = Scope.valueOf(this.scope_); return (result == null) ? Scope.GLOBAL : result; }
/*      */     public boolean hasPayload() { return ((this.bitField0_ & 0x4) != 0); }
/*      */     public ByteString getPayload() { return this.payload_; }
/*  545 */     public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) {
/*  546 */         output.writeUInt32(1, this.type_);
/*      */       }
/*  548 */       if ((this.bitField0_ & 0x2) != 0) {
/*  549 */         output.writeEnum(2, this.scope_);
/*      */       }
/*  551 */       if ((this.bitField0_ & 0x4) != 0) {
/*  552 */         output.writeBytes(3, this.payload_);
/*      */       }
/*  554 */       this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/*  559 */       int size = this.memoizedSize;
/*  560 */       if (size != -1) return size;
/*      */       
/*  562 */       size = 0;
/*  563 */       if ((this.bitField0_ & 0x1) != 0) {
/*  564 */         size += 
/*  565 */           CodedOutputStream.computeUInt32Size(1, this.type_);
/*      */       }
/*  567 */       if ((this.bitField0_ & 0x2) != 0) {
/*  568 */         size += 
/*  569 */           CodedOutputStream.computeEnumSize(2, this.scope_);
/*      */       }
/*  571 */       if ((this.bitField0_ & 0x4) != 0) {
/*  572 */         size += 
/*  573 */           CodedOutputStream.computeBytesSize(3, this.payload_);
/*      */       }
/*  575 */       size += this.unknownFields.getSerializedSize();
/*  576 */       this.memoizedSize = size;
/*  577 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  582 */       if (obj == this) {
/*  583 */         return true;
/*      */       }
/*  585 */       if (!(obj instanceof Frame)) {
/*  586 */         return super.equals(obj);
/*      */       }
/*  588 */       Frame other = (Frame)obj;
/*      */       
/*  590 */       if (hasType() != other.hasType()) return false; 
/*  591 */       if (hasType() && 
/*  592 */         getType() != other
/*  593 */         .getType()) return false;
/*      */       
/*  595 */       if (hasScope() != other.hasScope()) return false; 
/*  596 */       if (hasScope() && 
/*  597 */         this.scope_ != other.scope_) return false;
/*      */       
/*  599 */       if (hasPayload() != other.hasPayload()) return false; 
/*  600 */       if (hasPayload() && 
/*      */         
/*  602 */         !getPayload().equals(other.getPayload())) return false;
/*      */       
/*  604 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  605 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  610 */       if (this.memoizedHashCode != 0) {
/*  611 */         return this.memoizedHashCode;
/*      */       }
/*  613 */       int hash = 41;
/*  614 */       hash = 19 * hash + getDescriptor().hashCode();
/*  615 */       if (hasType()) {
/*  616 */         hash = 37 * hash + 1;
/*  617 */         hash = 53 * hash + getType();
/*      */       } 
/*  619 */       if (hasScope()) {
/*  620 */         hash = 37 * hash + 2;
/*  621 */         hash = 53 * hash + this.scope_;
/*      */       } 
/*  623 */       if (hasPayload()) {
/*  624 */         hash = 37 * hash + 3;
/*  625 */         hash = 53 * hash + getPayload().hashCode();
/*      */       } 
/*  627 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  628 */       this.memoizedHashCode = hash;
/*  629 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Frame parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  635 */       return (Frame)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Frame parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  641 */       return (Frame)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Frame parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  646 */       return (Frame)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Frame parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  652 */       return (Frame)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Frame parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  656 */       return (Frame)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Frame parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  662 */       return (Frame)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Frame parseFrom(InputStream input) throws IOException {
/*  666 */       return 
/*  667 */         (Frame)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Frame parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  673 */       return 
/*  674 */         (Frame)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Frame parseDelimitedFrom(InputStream input) throws IOException {
/*  678 */       return 
/*  679 */         (Frame)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Frame parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  685 */       return 
/*  686 */         (Frame)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Frame parseFrom(CodedInputStream input) throws IOException {
/*  691 */       return 
/*  692 */         (Frame)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Frame parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  698 */       return 
/*  699 */         (Frame)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/*  703 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/*  705 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Frame prototype) {
/*  708 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/*  712 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  713 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  719 */       Builder builder = new Builder(parent);
/*  720 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxNotice.FrameOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */       
/*      */       private int type_;
/*      */ 
/*      */       
/*      */       private int scope_;
/*      */ 
/*      */       
/*      */       private ByteString payload_;
/*      */ 
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/*  743 */         return MysqlxNotice.internal_static_Mysqlx_Notice_Frame_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  749 */         return MysqlxNotice.internal_static_Mysqlx_Notice_Frame_fieldAccessorTable
/*  750 */           .ensureFieldAccessorsInitialized(MysqlxNotice.Frame.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/*  962 */         this.scope_ = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1020 */         this.payload_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.scope_ = 1; this.payload_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxNotice.Frame.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.type_ = 0; this.bitField0_ &= 0xFFFFFFFE; this.scope_ = 1; this.bitField0_ &= 0xFFFFFFFD; this.payload_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFFB; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxNotice.internal_static_Mysqlx_Notice_Frame_descriptor; } public MysqlxNotice.Frame getDefaultInstanceForType() { return MysqlxNotice.Frame.getDefaultInstance(); } public MysqlxNotice.Frame build() { MysqlxNotice.Frame result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxNotice.Frame buildPartial() { MysqlxNotice.Frame result = new MysqlxNotice.Frame(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { result.type_ = this.type_; to_bitField0_ |= 0x1; }  if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.scope_ = this.scope_; if ((from_bitField0_ & 0x4) != 0) to_bitField0_ |= 0x4;  result.payload_ = this.payload_; result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxNotice.Frame) return mergeFrom((MysqlxNotice.Frame)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxNotice.Frame other) { if (other == MysqlxNotice.Frame.getDefaultInstance()) return this;  if (other.hasType()) setType(other.getType());  if (other.hasScope()) setScope(other.getScope());  if (other.hasPayload()) setPayload(other.getPayload());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasType()) return false;  return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxNotice.Frame parsedMessage = null; try { parsedMessage = (MysqlxNotice.Frame)MysqlxNotice.Frame.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxNotice.Frame)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/*      */       public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); }
/*      */       public int getType() { return this.type_; }
/*      */       public Builder setType(int value) { this.bitField0_ |= 0x1; this.type_ = value; onChanged(); return this; }
/*      */       public Builder clearType() { this.bitField0_ &= 0xFFFFFFFE; this.type_ = 0; onChanged(); return this; }
/*      */       public boolean hasScope() { return ((this.bitField0_ & 0x2) != 0); }
/*      */       public MysqlxNotice.Frame.Scope getScope() { MysqlxNotice.Frame.Scope result = MysqlxNotice.Frame.Scope.valueOf(this.scope_); return (result == null) ? MysqlxNotice.Frame.Scope.GLOBAL : result; }
/*      */       public Builder setScope(MysqlxNotice.Frame.Scope value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.scope_ = value.getNumber(); onChanged(); return this; }
/*      */       public Builder clearScope() { this.bitField0_ &= 0xFFFFFFFD; this.scope_ = 1; onChanged(); return this; }
/* 1030 */       public boolean hasPayload() { return ((this.bitField0_ & 0x4) != 0); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public ByteString getPayload() {
/* 1041 */         return this.payload_;
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
/*      */       public Builder setPayload(ByteString value) {
/* 1053 */         if (value == null) {
/* 1054 */           throw new NullPointerException();
/*      */         }
/* 1056 */         this.bitField0_ |= 0x4;
/* 1057 */         this.payload_ = value;
/* 1058 */         onChanged();
/* 1059 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearPayload() {
/* 1070 */         this.bitField0_ &= 0xFFFFFFFB;
/* 1071 */         this.payload_ = MysqlxNotice.Frame.getDefaultInstance().getPayload();
/* 1072 */         onChanged();
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
/* 1094 */     private static final Frame DEFAULT_INSTANCE = new Frame();
/*      */ 
/*      */     
/*      */     public static Frame getDefaultInstance() {
/* 1098 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 1102 */     public static final Parser<Frame> PARSER = (Parser<Frame>)new AbstractParser<Frame>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxNotice.Frame parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 1108 */           return new MysqlxNotice.Frame(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Frame> parser() {
/* 1113 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Frame> getParserForType() {
/* 1118 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Frame getDefaultInstanceForType() {
/* 1123 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface WarningOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasLevel();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxNotice.Warning.Level getLevel();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasCode();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getCode();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasMsg();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String getMsg();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getMsgBytes();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Warning
/*      */     extends GeneratedMessageV3
/*      */     implements WarningOrBuilder
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
/*      */     public static final int LEVEL_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int level_;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int CODE_FIELD_NUMBER = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int code_;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int MSG_FIELD_NUMBER = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private volatile Object msg_;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Warning(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 1228 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1536 */       this.memoizedIsInitialized = -1; } private Warning() { this.memoizedIsInitialized = -1; this.level_ = 2; this.msg_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Warning(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Warning(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int rawValue; ByteString bs; Level value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: rawValue = input.readEnum(); value = Level.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(1, rawValue); continue; }  this.bitField0_ |= 0x1; this.level_ = rawValue; continue;case 16: this.bitField0_ |= 0x2; this.code_ = input.readUInt32(); continue;case 26: bs = input.readBytes(); this.bitField0_ |= 0x4; this.msg_ = bs; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxNotice.internal_static_Mysqlx_Notice_Warning_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxNotice.internal_static_Mysqlx_Notice_Warning_fieldAccessorTable.ensureFieldAccessorsInitialized(Warning.class, Builder.class); } public enum Level implements ProtocolMessageEnum {
/*      */       NOTE(1), WARNING(2), ERROR(3); public static final int NOTE_VALUE = 1; public static final int WARNING_VALUE = 2; public static final int ERROR_VALUE = 3; private static final Internal.EnumLiteMap<Level> internalValueMap = new Internal.EnumLiteMap<Level>() { public MysqlxNotice.Warning.Level findValueByNumber(int number) { return MysqlxNotice.Warning.Level.forNumber(number); } }
/*      */       ; private static final Level[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Level forNumber(int value) { switch (value) { case 1: return NOTE;case 2: return WARNING;case 3: return ERROR; }  return null; } public static Internal.EnumLiteMap<Level> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxNotice.Warning.getDescriptor().getEnumTypes().get(0); } Level(int value) { this.value = value; } }
/* 1539 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 1540 */       if (isInitialized == 1) return true; 
/* 1541 */       if (isInitialized == 0) return false;
/*      */       
/* 1543 */       if (!hasCode()) {
/* 1544 */         this.memoizedIsInitialized = 0;
/* 1545 */         return false;
/*      */       } 
/* 1547 */       if (!hasMsg()) {
/* 1548 */         this.memoizedIsInitialized = 0;
/* 1549 */         return false;
/*      */       } 
/* 1551 */       this.memoizedIsInitialized = 1;
/* 1552 */       return true; } public boolean hasLevel() { return ((this.bitField0_ & 0x1) != 0); } public Level getLevel() { Level result = Level.valueOf(this.level_); return (result == null) ? Level.WARNING : result; }
/*      */     public boolean hasCode() { return ((this.bitField0_ & 0x2) != 0); }
/*      */     public int getCode() { return this.code_; }
/*      */     public boolean hasMsg() { return ((this.bitField0_ & 0x4) != 0); }
/*      */     public String getMsg() { Object ref = this.msg_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.msg_ = s;  return s; }
/*      */     public ByteString getMsgBytes() { Object ref = this.msg_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.msg_ = b; return b; }  return (ByteString)ref; }
/* 1558 */     public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) {
/* 1559 */         output.writeEnum(1, this.level_);
/*      */       }
/* 1561 */       if ((this.bitField0_ & 0x2) != 0) {
/* 1562 */         output.writeUInt32(2, this.code_);
/*      */       }
/* 1564 */       if ((this.bitField0_ & 0x4) != 0) {
/* 1565 */         GeneratedMessageV3.writeString(output, 3, this.msg_);
/*      */       }
/* 1567 */       this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 1572 */       int size = this.memoizedSize;
/* 1573 */       if (size != -1) return size;
/*      */       
/* 1575 */       size = 0;
/* 1576 */       if ((this.bitField0_ & 0x1) != 0) {
/* 1577 */         size += 
/* 1578 */           CodedOutputStream.computeEnumSize(1, this.level_);
/*      */       }
/* 1580 */       if ((this.bitField0_ & 0x2) != 0) {
/* 1581 */         size += 
/* 1582 */           CodedOutputStream.computeUInt32Size(2, this.code_);
/*      */       }
/* 1584 */       if ((this.bitField0_ & 0x4) != 0) {
/* 1585 */         size += GeneratedMessageV3.computeStringSize(3, this.msg_);
/*      */       }
/* 1587 */       size += this.unknownFields.getSerializedSize();
/* 1588 */       this.memoizedSize = size;
/* 1589 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1594 */       if (obj == this) {
/* 1595 */         return true;
/*      */       }
/* 1597 */       if (!(obj instanceof Warning)) {
/* 1598 */         return super.equals(obj);
/*      */       }
/* 1600 */       Warning other = (Warning)obj;
/*      */       
/* 1602 */       if (hasLevel() != other.hasLevel()) return false; 
/* 1603 */       if (hasLevel() && 
/* 1604 */         this.level_ != other.level_) return false;
/*      */       
/* 1606 */       if (hasCode() != other.hasCode()) return false; 
/* 1607 */       if (hasCode() && 
/* 1608 */         getCode() != other
/* 1609 */         .getCode()) return false;
/*      */       
/* 1611 */       if (hasMsg() != other.hasMsg()) return false; 
/* 1612 */       if (hasMsg() && 
/*      */         
/* 1614 */         !getMsg().equals(other.getMsg())) return false;
/*      */       
/* 1616 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 1617 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1622 */       if (this.memoizedHashCode != 0) {
/* 1623 */         return this.memoizedHashCode;
/*      */       }
/* 1625 */       int hash = 41;
/* 1626 */       hash = 19 * hash + getDescriptor().hashCode();
/* 1627 */       if (hasLevel()) {
/* 1628 */         hash = 37 * hash + 1;
/* 1629 */         hash = 53 * hash + this.level_;
/*      */       } 
/* 1631 */       if (hasCode()) {
/* 1632 */         hash = 37 * hash + 2;
/* 1633 */         hash = 53 * hash + getCode();
/*      */       } 
/* 1635 */       if (hasMsg()) {
/* 1636 */         hash = 37 * hash + 3;
/* 1637 */         hash = 53 * hash + getMsg().hashCode();
/*      */       } 
/* 1639 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 1640 */       this.memoizedHashCode = hash;
/* 1641 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Warning parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 1647 */       return (Warning)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Warning parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1653 */       return (Warning)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Warning parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 1658 */       return (Warning)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Warning parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1664 */       return (Warning)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Warning parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 1668 */       return (Warning)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Warning parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1674 */       return (Warning)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Warning parseFrom(InputStream input) throws IOException {
/* 1678 */       return 
/* 1679 */         (Warning)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Warning parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1685 */       return 
/* 1686 */         (Warning)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Warning parseDelimitedFrom(InputStream input) throws IOException {
/* 1690 */       return 
/* 1691 */         (Warning)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Warning parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1697 */       return 
/* 1698 */         (Warning)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Warning parseFrom(CodedInputStream input) throws IOException {
/* 1703 */       return 
/* 1704 */         (Warning)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Warning parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1710 */       return 
/* 1711 */         (Warning)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 1715 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 1717 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Warning prototype) {
/* 1720 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 1724 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 1725 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 1731 */       Builder builder = new Builder(parent);
/* 1732 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxNotice.WarningOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private int level_;
/*      */ 
/*      */ 
/*      */       
/*      */       private int code_;
/*      */ 
/*      */ 
/*      */       
/*      */       private Object msg_;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 1762 */         return MysqlxNotice.internal_static_Mysqlx_Notice_Warning_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 1768 */         return MysqlxNotice.internal_static_Mysqlx_Notice_Warning_fieldAccessorTable
/* 1769 */           .ensureFieldAccessorsInitialized(MysqlxNotice.Warning.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/* 1933 */         this.level_ = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2044 */         this.msg_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.level_ = 2; this.msg_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxNotice.Warning.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.level_ = 2; this.bitField0_ &= 0xFFFFFFFE; this.code_ = 0; this.bitField0_ &= 0xFFFFFFFD; this.msg_ = ""; this.bitField0_ &= 0xFFFFFFFB; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxNotice.internal_static_Mysqlx_Notice_Warning_descriptor; }
/*      */       public MysqlxNotice.Warning getDefaultInstanceForType() { return MysqlxNotice.Warning.getDefaultInstance(); }
/*      */       public MysqlxNotice.Warning build() { MysqlxNotice.Warning result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; }
/*      */       public MysqlxNotice.Warning buildPartial() { MysqlxNotice.Warning result = new MysqlxNotice.Warning(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.level_ = this.level_; if ((from_bitField0_ & 0x2) != 0) { result.code_ = this.code_; to_bitField0_ |= 0x2; }  if ((from_bitField0_ & 0x4) != 0) to_bitField0_ |= 0x4;  result.msg_ = this.msg_; result.bitField0_ = to_bitField0_; onBuilt(); return result; }
/*      */       public Builder clone() { return (Builder)super.clone(); }
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); }
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); }
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); }
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); }
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); }
/* 2054 */       public boolean hasMsg() { return ((this.bitField0_ & 0x4) != 0); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxNotice.Warning) return mergeFrom((MysqlxNotice.Warning)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxNotice.Warning other) { if (other == MysqlxNotice.Warning.getDefaultInstance()) return this;  if (other.hasLevel()) setLevel(other.getLevel());  if (other.hasCode()) setCode(other.getCode());  if (other.hasMsg()) { this.bitField0_ |= 0x4; this.msg_ = other.msg_; onChanged(); }  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */       public final boolean isInitialized() { if (!hasCode()) return false;  if (!hasMsg()) return false;  return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxNotice.Warning parsedMessage = null; try { parsedMessage = (MysqlxNotice.Warning)MysqlxNotice.Warning.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxNotice.Warning)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/*      */       public boolean hasLevel() { return ((this.bitField0_ & 0x1) != 0); }
/*      */       public MysqlxNotice.Warning.Level getLevel() { MysqlxNotice.Warning.Level result = MysqlxNotice.Warning.Level.valueOf(this.level_); return (result == null) ? MysqlxNotice.Warning.Level.WARNING : result; }
/*      */       public Builder setLevel(MysqlxNotice.Warning.Level value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.level_ = value.getNumber(); onChanged(); return this; }
/*      */       public Builder clearLevel() { this.bitField0_ &= 0xFFFFFFFE; this.level_ = 2; onChanged(); return this; }
/*      */       public boolean hasCode() { return ((this.bitField0_ & 0x2) != 0); }
/*      */       public int getCode() { return this.code_; }
/*      */       public Builder setCode(int value) { this.bitField0_ |= 0x2; this.code_ = value; onChanged(); return this; }
/*      */       public Builder clearCode() { this.bitField0_ &= 0xFFFFFFFD; this.code_ = 0; onChanged(); return this; }
/* 2065 */       public String getMsg() { Object ref = this.msg_;
/* 2066 */         if (!(ref instanceof String)) {
/* 2067 */           ByteString bs = (ByteString)ref;
/*      */           
/* 2069 */           String s = bs.toStringUtf8();
/* 2070 */           if (bs.isValidUtf8()) {
/* 2071 */             this.msg_ = s;
/*      */           }
/* 2073 */           return s;
/*      */         } 
/* 2075 */         return (String)ref; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public ByteString getMsgBytes() {
/* 2088 */         Object ref = this.msg_;
/* 2089 */         if (ref instanceof String) {
/*      */           
/* 2091 */           ByteString b = ByteString.copyFromUtf8((String)ref);
/*      */           
/* 2093 */           this.msg_ = b;
/* 2094 */           return b;
/*      */         } 
/* 2096 */         return (ByteString)ref;
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
/*      */       public Builder setMsg(String value) {
/* 2110 */         if (value == null) {
/* 2111 */           throw new NullPointerException();
/*      */         }
/* 2113 */         this.bitField0_ |= 0x4;
/* 2114 */         this.msg_ = value;
/* 2115 */         onChanged();
/* 2116 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearMsg() {
/* 2127 */         this.bitField0_ &= 0xFFFFFFFB;
/* 2128 */         this.msg_ = MysqlxNotice.Warning.getDefaultInstance().getMsg();
/* 2129 */         onChanged();
/* 2130 */         return this;
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
/*      */       public Builder setMsgBytes(ByteString value) {
/* 2143 */         if (value == null) {
/* 2144 */           throw new NullPointerException();
/*      */         }
/* 2146 */         this.bitField0_ |= 0x4;
/* 2147 */         this.msg_ = value;
/* 2148 */         onChanged();
/* 2149 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 2154 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 2160 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2170 */     private static final Warning DEFAULT_INSTANCE = new Warning();
/*      */ 
/*      */     
/*      */     public static Warning getDefaultInstance() {
/* 2174 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 2178 */     public static final Parser<Warning> PARSER = (Parser<Warning>)new AbstractParser<Warning>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxNotice.Warning parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 2184 */           return new MysqlxNotice.Warning(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Warning> parser() {
/* 2189 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Warning> getParserForType() {
/* 2194 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Warning getDefaultInstanceForType() {
/* 2199 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface SessionVariableChangedOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasParam();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String getParam();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getParamBytes();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasValue();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.Scalar getValue();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class SessionVariableChanged
/*      */     extends GeneratedMessageV3
/*      */     implements SessionVariableChangedOrBuilder
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
/*      */     public static final int PARAM_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private volatile Object param_;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int VALUE_FIELD_NUMBER = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private MysqlxDatatypes.Scalar value_;
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private SessionVariableChanged(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 2287 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2468 */       this.memoizedIsInitialized = -1; } private SessionVariableChanged() { this.memoizedIsInitialized = -1; this.param_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new SessionVariableChanged(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private SessionVariableChanged(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; MysqlxDatatypes.Scalar.Builder subBuilder; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: bs = input.readBytes(); this.bitField0_ |= 0x1; this.param_ = bs; continue;case 18: subBuilder = null; if ((this.bitField0_ & 0x2) != 0) subBuilder = this.value_.toBuilder();  this.value_ = (MysqlxDatatypes.Scalar)input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.value_); this.value_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x2; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxNotice.internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxNotice.internal_static_Mysqlx_Notice_SessionVariableChanged_fieldAccessorTable.ensureFieldAccessorsInitialized(SessionVariableChanged.class, Builder.class); } public boolean hasParam() { return ((this.bitField0_ & 0x1) != 0); } public String getParam() { Object ref = this.param_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.param_ = s;  return s; } public ByteString getParamBytes() { Object ref = this.param_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.param_ = b; return b; }  return (ByteString)ref; } public boolean hasValue() { return ((this.bitField0_ & 0x2) != 0); }
/*      */     public MysqlxDatatypes.Scalar getValue() { return (this.value_ == null) ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_; }
/*      */     public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder() { return (this.value_ == null) ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_; }
/* 2471 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 2472 */       if (isInitialized == 1) return true; 
/* 2473 */       if (isInitialized == 0) return false;
/*      */       
/* 2475 */       if (!hasParam()) {
/* 2476 */         this.memoizedIsInitialized = 0;
/* 2477 */         return false;
/*      */       } 
/* 2479 */       if (hasValue() && 
/* 2480 */         !getValue().isInitialized()) {
/* 2481 */         this.memoizedIsInitialized = 0;
/* 2482 */         return false;
/*      */       } 
/*      */       
/* 2485 */       this.memoizedIsInitialized = 1;
/* 2486 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 2492 */       if ((this.bitField0_ & 0x1) != 0) {
/* 2493 */         GeneratedMessageV3.writeString(output, 1, this.param_);
/*      */       }
/* 2495 */       if ((this.bitField0_ & 0x2) != 0) {
/* 2496 */         output.writeMessage(2, (MessageLite)getValue());
/*      */       }
/* 2498 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 2503 */       int size = this.memoizedSize;
/* 2504 */       if (size != -1) return size;
/*      */       
/* 2506 */       size = 0;
/* 2507 */       if ((this.bitField0_ & 0x1) != 0) {
/* 2508 */         size += GeneratedMessageV3.computeStringSize(1, this.param_);
/*      */       }
/* 2510 */       if ((this.bitField0_ & 0x2) != 0) {
/* 2511 */         size += 
/* 2512 */           CodedOutputStream.computeMessageSize(2, (MessageLite)getValue());
/*      */       }
/* 2514 */       size += this.unknownFields.getSerializedSize();
/* 2515 */       this.memoizedSize = size;
/* 2516 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 2521 */       if (obj == this) {
/* 2522 */         return true;
/*      */       }
/* 2524 */       if (!(obj instanceof SessionVariableChanged)) {
/* 2525 */         return super.equals(obj);
/*      */       }
/* 2527 */       SessionVariableChanged other = (SessionVariableChanged)obj;
/*      */       
/* 2529 */       if (hasParam() != other.hasParam()) return false; 
/* 2530 */       if (hasParam() && 
/*      */         
/* 2532 */         !getParam().equals(other.getParam())) return false;
/*      */       
/* 2534 */       if (hasValue() != other.hasValue()) return false; 
/* 2535 */       if (hasValue() && 
/*      */         
/* 2537 */         !getValue().equals(other.getValue())) return false;
/*      */       
/* 2539 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 2540 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 2545 */       if (this.memoizedHashCode != 0) {
/* 2546 */         return this.memoizedHashCode;
/*      */       }
/* 2548 */       int hash = 41;
/* 2549 */       hash = 19 * hash + getDescriptor().hashCode();
/* 2550 */       if (hasParam()) {
/* 2551 */         hash = 37 * hash + 1;
/* 2552 */         hash = 53 * hash + getParam().hashCode();
/*      */       } 
/* 2554 */       if (hasValue()) {
/* 2555 */         hash = 37 * hash + 2;
/* 2556 */         hash = 53 * hash + getValue().hashCode();
/*      */       } 
/* 2558 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 2559 */       this.memoizedHashCode = hash;
/* 2560 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static SessionVariableChanged parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 2566 */       return (SessionVariableChanged)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static SessionVariableChanged parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2572 */       return (SessionVariableChanged)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static SessionVariableChanged parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 2577 */       return (SessionVariableChanged)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static SessionVariableChanged parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2583 */       return (SessionVariableChanged)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static SessionVariableChanged parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 2587 */       return (SessionVariableChanged)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static SessionVariableChanged parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2593 */       return (SessionVariableChanged)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static SessionVariableChanged parseFrom(InputStream input) throws IOException {
/* 2597 */       return 
/* 2598 */         (SessionVariableChanged)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static SessionVariableChanged parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2604 */       return 
/* 2605 */         (SessionVariableChanged)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static SessionVariableChanged parseDelimitedFrom(InputStream input) throws IOException {
/* 2609 */       return 
/* 2610 */         (SessionVariableChanged)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static SessionVariableChanged parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2616 */       return 
/* 2617 */         (SessionVariableChanged)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static SessionVariableChanged parseFrom(CodedInputStream input) throws IOException {
/* 2622 */       return 
/* 2623 */         (SessionVariableChanged)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static SessionVariableChanged parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2629 */       return 
/* 2630 */         (SessionVariableChanged)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 2634 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 2636 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(SessionVariableChanged prototype) {
/* 2639 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 2643 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 2644 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 2650 */       Builder builder = new Builder(parent);
/* 2651 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxNotice.SessionVariableChangedOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */       
/*      */       private Object param_;
/*      */ 
/*      */       
/*      */       private MysqlxDatatypes.Scalar value_;
/*      */ 
/*      */       
/*      */       private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> valueBuilder_;
/*      */ 
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 2675 */         return MysqlxNotice.internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 2681 */         return MysqlxNotice.internal_static_Mysqlx_Notice_SessionVariableChanged_fieldAccessorTable
/* 2682 */           .ensureFieldAccessorsInitialized(MysqlxNotice.SessionVariableChanged.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/* 2848 */         this.param_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.param_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxNotice.SessionVariableChanged.alwaysUseFieldBuilders) getValueFieldBuilder();  } public Builder clear() { super.clear(); this.param_ = ""; this.bitField0_ &= 0xFFFFFFFE; if (this.valueBuilder_ == null) { this.value_ = null; } else { this.valueBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxNotice.internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor; } public MysqlxNotice.SessionVariableChanged getDefaultInstanceForType() { return MysqlxNotice.SessionVariableChanged.getDefaultInstance(); } public MysqlxNotice.SessionVariableChanged build() { MysqlxNotice.SessionVariableChanged result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxNotice.SessionVariableChanged buildPartial() { MysqlxNotice.SessionVariableChanged result = new MysqlxNotice.SessionVariableChanged(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.param_ = this.param_; if ((from_bitField0_ & 0x2) != 0) { if (this.valueBuilder_ == null) { result.value_ = this.value_; } else { result.value_ = (MysqlxDatatypes.Scalar)this.valueBuilder_.build(); }  to_bitField0_ |= 0x2; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); }
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); }
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); }
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); }
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); }
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); }
/*      */       public Builder mergeFrom(Message other) { if (other instanceof MysqlxNotice.SessionVariableChanged) return mergeFrom((MysqlxNotice.SessionVariableChanged)other);  super.mergeFrom(other); return this; }
/*      */       public Builder mergeFrom(MysqlxNotice.SessionVariableChanged other) { if (other == MysqlxNotice.SessionVariableChanged.getDefaultInstance()) return this;  if (other.hasParam()) { this.bitField0_ |= 0x1; this.param_ = other.param_; onChanged(); }  if (other.hasValue()) mergeValue(other.getValue());  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */       public final boolean isInitialized() { if (!hasParam()) return false;  if (hasValue() && !getValue().isInitialized()) return false;  return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxNotice.SessionVariableChanged parsedMessage = null; try { parsedMessage = (MysqlxNotice.SessionVariableChanged)MysqlxNotice.SessionVariableChanged.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxNotice.SessionVariableChanged)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/* 2858 */       public boolean hasParam() { return ((this.bitField0_ & 0x1) != 0); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public String getParam() {
/* 2869 */         Object ref = this.param_;
/* 2870 */         if (!(ref instanceof String)) {
/* 2871 */           ByteString bs = (ByteString)ref;
/*      */           
/* 2873 */           String s = bs.toStringUtf8();
/* 2874 */           if (bs.isValidUtf8()) {
/* 2875 */             this.param_ = s;
/*      */           }
/* 2877 */           return s;
/*      */         } 
/* 2879 */         return (String)ref;
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
/*      */       public ByteString getParamBytes() {
/* 2892 */         Object ref = this.param_;
/* 2893 */         if (ref instanceof String) {
/*      */           
/* 2895 */           ByteString b = ByteString.copyFromUtf8((String)ref);
/*      */           
/* 2897 */           this.param_ = b;
/* 2898 */           return b;
/*      */         } 
/* 2900 */         return (ByteString)ref;
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
/*      */       public Builder setParam(String value) {
/* 2914 */         if (value == null) {
/* 2915 */           throw new NullPointerException();
/*      */         }
/* 2917 */         this.bitField0_ |= 0x1;
/* 2918 */         this.param_ = value;
/* 2919 */         onChanged();
/* 2920 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearParam() {
/* 2931 */         this.bitField0_ &= 0xFFFFFFFE;
/* 2932 */         this.param_ = MysqlxNotice.SessionVariableChanged.getDefaultInstance().getParam();
/* 2933 */         onChanged();
/* 2934 */         return this;
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
/*      */       public Builder setParamBytes(ByteString value) {
/* 2947 */         if (value == null) {
/* 2948 */           throw new NullPointerException();
/*      */         }
/* 2950 */         this.bitField0_ |= 0x1;
/* 2951 */         this.param_ = value;
/* 2952 */         onChanged();
/* 2953 */         return this;
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
/*      */       public boolean hasValue() {
/* 2968 */         return ((this.bitField0_ & 0x2) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Scalar getValue() {
/* 2979 */         if (this.valueBuilder_ == null) {
/* 2980 */           return (this.value_ == null) ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_;
/*      */         }
/* 2982 */         return (MysqlxDatatypes.Scalar)this.valueBuilder_.getMessage();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setValue(MysqlxDatatypes.Scalar value) {
/* 2993 */         if (this.valueBuilder_ == null) {
/* 2994 */           if (value == null) {
/* 2995 */             throw new NullPointerException();
/*      */           }
/* 2997 */           this.value_ = value;
/* 2998 */           onChanged();
/*      */         } else {
/* 3000 */           this.valueBuilder_.setMessage((AbstractMessage)value);
/*      */         } 
/* 3002 */         this.bitField0_ |= 0x2;
/* 3003 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setValue(MysqlxDatatypes.Scalar.Builder builderForValue) {
/* 3014 */         if (this.valueBuilder_ == null) {
/* 3015 */           this.value_ = builderForValue.build();
/* 3016 */           onChanged();
/*      */         } else {
/* 3018 */           this.valueBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 3020 */         this.bitField0_ |= 0x2;
/* 3021 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeValue(MysqlxDatatypes.Scalar value) {
/* 3031 */         if (this.valueBuilder_ == null) {
/* 3032 */           if ((this.bitField0_ & 0x2) != 0 && this.value_ != null && this.value_ != 
/*      */             
/* 3034 */             MysqlxDatatypes.Scalar.getDefaultInstance()) {
/* 3035 */             this
/* 3036 */               .value_ = MysqlxDatatypes.Scalar.newBuilder(this.value_).mergeFrom(value).buildPartial();
/*      */           } else {
/* 3038 */             this.value_ = value;
/*      */           } 
/* 3040 */           onChanged();
/*      */         } else {
/* 3042 */           this.valueBuilder_.mergeFrom((AbstractMessage)value);
/*      */         } 
/* 3044 */         this.bitField0_ |= 0x2;
/* 3045 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearValue() {
/* 3055 */         if (this.valueBuilder_ == null) {
/* 3056 */           this.value_ = null;
/* 3057 */           onChanged();
/*      */         } else {
/* 3059 */           this.valueBuilder_.clear();
/*      */         } 
/* 3061 */         this.bitField0_ &= 0xFFFFFFFD;
/* 3062 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Scalar.Builder getValueBuilder() {
/* 3072 */         this.bitField0_ |= 0x2;
/* 3073 */         onChanged();
/* 3074 */         return (MysqlxDatatypes.Scalar.Builder)getValueFieldBuilder().getBuilder();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder() {
/* 3084 */         if (this.valueBuilder_ != null) {
/* 3085 */           return (MysqlxDatatypes.ScalarOrBuilder)this.valueBuilder_.getMessageOrBuilder();
/*      */         }
/* 3087 */         return (this.value_ == null) ? 
/* 3088 */           MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_;
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
/*      */       private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getValueFieldBuilder() {
/* 3101 */         if (this.valueBuilder_ == null) {
/* 3102 */           this
/*      */ 
/*      */ 
/*      */             
/* 3106 */             .valueBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getValue(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 3107 */           this.value_ = null;
/*      */         } 
/* 3109 */         return this.valueBuilder_;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 3114 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 3120 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3130 */     private static final SessionVariableChanged DEFAULT_INSTANCE = new SessionVariableChanged();
/*      */ 
/*      */     
/*      */     public static SessionVariableChanged getDefaultInstance() {
/* 3134 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 3138 */     public static final Parser<SessionVariableChanged> PARSER = (Parser<SessionVariableChanged>)new AbstractParser<SessionVariableChanged>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxNotice.SessionVariableChanged parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 3144 */           return new MysqlxNotice.SessionVariableChanged(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<SessionVariableChanged> parser() {
/* 3149 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<SessionVariableChanged> getParserForType() {
/* 3154 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public SessionVariableChanged getDefaultInstanceForType() {
/* 3159 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface SessionStateChangedOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasParam();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxNotice.SessionStateChanged.Parameter getParam();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     List<MysqlxDatatypes.Scalar> getValueList();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.Scalar getValue(int param1Int);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getValueCount();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     List<? extends MysqlxDatatypes.ScalarOrBuilder> getValueOrBuilderList();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder(int param1Int);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class SessionStateChanged
/*      */     extends GeneratedMessageV3
/*      */     implements SessionStateChangedOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int PARAM_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */     
/*      */     private int param_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int VALUE_FIELD_NUMBER = 2;
/*      */ 
/*      */ 
/*      */     
/*      */     private List<MysqlxDatatypes.Scalar> value_;
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private SessionStateChanged(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 3241 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3603 */       this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new SessionStateChanged(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private SessionStateChanged(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int rawValue; Parameter value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: rawValue = input.readEnum(); value = Parameter.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(1, rawValue); continue; }  this.bitField0_ |= 0x1; this.param_ = rawValue; continue;case 18: if ((mutable_bitField0_ & 0x2) == 0) { this.value_ = new ArrayList<>(); mutable_bitField0_ |= 0x2; }  this.value_.add(input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry)); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x2) != 0) this.value_ = Collections.unmodifiableList(this.value_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } private SessionStateChanged() { this.memoizedIsInitialized = -1; this.param_ = 1; this.value_ = Collections.emptyList(); } public static final Descriptors.Descriptor getDescriptor() { return MysqlxNotice.internal_static_Mysqlx_Notice_SessionStateChanged_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxNotice.internal_static_Mysqlx_Notice_SessionStateChanged_fieldAccessorTable.ensureFieldAccessorsInitialized(SessionStateChanged.class, Builder.class); } public enum Parameter implements ProtocolMessageEnum {
/*      */       CURRENT_SCHEMA(1), ACCOUNT_EXPIRED(2), GENERATED_INSERT_ID(3), ROWS_AFFECTED(4), ROWS_FOUND(5), ROWS_MATCHED(6), TRX_COMMITTED(7), TRX_ROLLEDBACK(9), PRODUCED_MESSAGE(10), CLIENT_ID_ASSIGNED(11), GENERATED_DOCUMENT_IDS(12); public static final int CURRENT_SCHEMA_VALUE = 1; public static final int ACCOUNT_EXPIRED_VALUE = 2; public static final int GENERATED_INSERT_ID_VALUE = 3; public static final int ROWS_AFFECTED_VALUE = 4; public static final int ROWS_FOUND_VALUE = 5; public static final int ROWS_MATCHED_VALUE = 6; public static final int TRX_COMMITTED_VALUE = 7; public static final int TRX_ROLLEDBACK_VALUE = 9; public static final int PRODUCED_MESSAGE_VALUE = 10; public static final int CLIENT_ID_ASSIGNED_VALUE = 11; public static final int GENERATED_DOCUMENT_IDS_VALUE = 12; private static final Internal.EnumLiteMap<Parameter> internalValueMap = new Internal.EnumLiteMap<Parameter>() { public MysqlxNotice.SessionStateChanged.Parameter findValueByNumber(int number) { return MysqlxNotice.SessionStateChanged.Parameter.forNumber(number); } }
/*      */       ; private static final Parameter[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Parameter forNumber(int value) { switch (value) { case 1: return CURRENT_SCHEMA;case 2: return ACCOUNT_EXPIRED;case 3: return GENERATED_INSERT_ID;case 4: return ROWS_AFFECTED;case 5: return ROWS_FOUND;case 6: return ROWS_MATCHED;case 7: return TRX_COMMITTED;case 9: return TRX_ROLLEDBACK;case 10: return PRODUCED_MESSAGE;case 11: return CLIENT_ID_ASSIGNED;case 12: return GENERATED_DOCUMENT_IDS; }  return null; } public static Internal.EnumLiteMap<Parameter> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxNotice.SessionStateChanged.getDescriptor().getEnumTypes().get(0); } Parameter(int value) { this.value = value; }
/* 3606 */     } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 3607 */       if (isInitialized == 1) return true; 
/* 3608 */       if (isInitialized == 0) return false;
/*      */       
/* 3610 */       if (!hasParam()) {
/* 3611 */         this.memoizedIsInitialized = 0;
/* 3612 */         return false;
/*      */       } 
/* 3614 */       for (int i = 0; i < getValueCount(); i++) {
/* 3615 */         if (!getValue(i).isInitialized()) {
/* 3616 */           this.memoizedIsInitialized = 0;
/* 3617 */           return false;
/*      */         } 
/*      */       } 
/* 3620 */       this.memoizedIsInitialized = 1;
/* 3621 */       return true; } public boolean hasParam() { return ((this.bitField0_ & 0x1) != 0); } public Parameter getParam() { Parameter result = Parameter.valueOf(this.param_); return (result == null) ? Parameter.CURRENT_SCHEMA : result; }
/*      */     public List<MysqlxDatatypes.Scalar> getValueList() { return this.value_; }
/*      */     public List<? extends MysqlxDatatypes.ScalarOrBuilder> getValueOrBuilderList() { return (List)this.value_; }
/*      */     public int getValueCount() { return this.value_.size(); }
/*      */     public MysqlxDatatypes.Scalar getValue(int index) { return this.value_.get(index); }
/*      */     public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder(int index) { return this.value_.get(index); }
/* 3627 */     public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) {
/* 3628 */         output.writeEnum(1, this.param_);
/*      */       }
/* 3630 */       for (int i = 0; i < this.value_.size(); i++) {
/* 3631 */         output.writeMessage(2, (MessageLite)this.value_.get(i));
/*      */       }
/* 3633 */       this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 3638 */       int size = this.memoizedSize;
/* 3639 */       if (size != -1) return size;
/*      */       
/* 3641 */       size = 0;
/* 3642 */       if ((this.bitField0_ & 0x1) != 0) {
/* 3643 */         size += 
/* 3644 */           CodedOutputStream.computeEnumSize(1, this.param_);
/*      */       }
/* 3646 */       for (int i = 0; i < this.value_.size(); i++) {
/* 3647 */         size += 
/* 3648 */           CodedOutputStream.computeMessageSize(2, (MessageLite)this.value_.get(i));
/*      */       }
/* 3650 */       size += this.unknownFields.getSerializedSize();
/* 3651 */       this.memoizedSize = size;
/* 3652 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 3657 */       if (obj == this) {
/* 3658 */         return true;
/*      */       }
/* 3660 */       if (!(obj instanceof SessionStateChanged)) {
/* 3661 */         return super.equals(obj);
/*      */       }
/* 3663 */       SessionStateChanged other = (SessionStateChanged)obj;
/*      */       
/* 3665 */       if (hasParam() != other.hasParam()) return false; 
/* 3666 */       if (hasParam() && 
/* 3667 */         this.param_ != other.param_) return false;
/*      */ 
/*      */       
/* 3670 */       if (!getValueList().equals(other.getValueList())) return false; 
/* 3671 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 3672 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 3677 */       if (this.memoizedHashCode != 0) {
/* 3678 */         return this.memoizedHashCode;
/*      */       }
/* 3680 */       int hash = 41;
/* 3681 */       hash = 19 * hash + getDescriptor().hashCode();
/* 3682 */       if (hasParam()) {
/* 3683 */         hash = 37 * hash + 1;
/* 3684 */         hash = 53 * hash + this.param_;
/*      */       } 
/* 3686 */       if (getValueCount() > 0) {
/* 3687 */         hash = 37 * hash + 2;
/* 3688 */         hash = 53 * hash + getValueList().hashCode();
/*      */       } 
/* 3690 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 3691 */       this.memoizedHashCode = hash;
/* 3692 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static SessionStateChanged parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 3698 */       return (SessionStateChanged)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static SessionStateChanged parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 3704 */       return (SessionStateChanged)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static SessionStateChanged parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 3709 */       return (SessionStateChanged)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static SessionStateChanged parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 3715 */       return (SessionStateChanged)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static SessionStateChanged parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 3719 */       return (SessionStateChanged)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static SessionStateChanged parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 3725 */       return (SessionStateChanged)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static SessionStateChanged parseFrom(InputStream input) throws IOException {
/* 3729 */       return 
/* 3730 */         (SessionStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static SessionStateChanged parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3736 */       return 
/* 3737 */         (SessionStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static SessionStateChanged parseDelimitedFrom(InputStream input) throws IOException {
/* 3741 */       return 
/* 3742 */         (SessionStateChanged)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static SessionStateChanged parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3748 */       return 
/* 3749 */         (SessionStateChanged)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static SessionStateChanged parseFrom(CodedInputStream input) throws IOException {
/* 3754 */       return 
/* 3755 */         (SessionStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static SessionStateChanged parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3761 */       return 
/* 3762 */         (SessionStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 3766 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 3768 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(SessionStateChanged prototype) {
/* 3771 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 3775 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 3776 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 3782 */       Builder builder = new Builder(parent);
/* 3783 */       return builder;
/*      */     }
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder> implements MysqlxNotice.SessionStateChangedOrBuilder {
/*      */       private int bitField0_;
/*      */       private int param_;
/*      */       private List<MysqlxDatatypes.Scalar> value_;
/*      */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> valueBuilder_;
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 3794 */         return MysqlxNotice.internal_static_Mysqlx_Notice_SessionStateChanged_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 3800 */         return MysqlxNotice.internal_static_Mysqlx_Notice_SessionStateChanged_fieldAccessorTable
/* 3801 */           .ensureFieldAccessorsInitialized(MysqlxNotice.SessionStateChanged.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/* 3989 */         this.param_ = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4047 */         this
/* 4048 */           .value_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.param_ = 1; this.value_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxNotice.SessionStateChanged.alwaysUseFieldBuilders) getValueFieldBuilder();  } public Builder clear() { super.clear(); this.param_ = 1; this.bitField0_ &= 0xFFFFFFFE; if (this.valueBuilder_ == null) { this.value_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFD; } else { this.valueBuilder_.clear(); }  return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxNotice.internal_static_Mysqlx_Notice_SessionStateChanged_descriptor; } public MysqlxNotice.SessionStateChanged getDefaultInstanceForType() { return MysqlxNotice.SessionStateChanged.getDefaultInstance(); } public MysqlxNotice.SessionStateChanged build() { MysqlxNotice.SessionStateChanged result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxNotice.SessionStateChanged buildPartial() { MysqlxNotice.SessionStateChanged result = new MysqlxNotice.SessionStateChanged(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.param_ = this.param_; if (this.valueBuilder_ == null) { if ((this.bitField0_ & 0x2) != 0) { this.value_ = Collections.unmodifiableList(this.value_); this.bitField0_ &= 0xFFFFFFFD; }  result.value_ = this.value_; } else { result.value_ = this.valueBuilder_.build(); }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); }
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); }
/* 4050 */       private void ensureValueIsMutable() { if ((this.bitField0_ & 0x2) == 0)
/* 4051 */         { this.value_ = new ArrayList<>(this.value_);
/* 4052 */           this.bitField0_ |= 0x2; }  } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); }
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); }
/*      */       public Builder mergeFrom(Message other) { if (other instanceof MysqlxNotice.SessionStateChanged) return mergeFrom((MysqlxNotice.SessionStateChanged)other);  super.mergeFrom(other); return this; }
/*      */       public Builder mergeFrom(MysqlxNotice.SessionStateChanged other) { if (other == MysqlxNotice.SessionStateChanged.getDefaultInstance()) return this;  if (other.hasParam())
/*      */           setParam(other.getParam());  if (this.valueBuilder_ == null) { if (!other.value_.isEmpty()) { if (this.value_.isEmpty()) { this.value_ = other.value_; this.bitField0_ &= 0xFFFFFFFD; } else { ensureValueIsMutable(); this.value_.addAll(other.value_); }  onChanged(); }  } else if (!other.value_.isEmpty()) { if (this.valueBuilder_.isEmpty()) { this.valueBuilder_.dispose(); this.valueBuilder_ = null; this.value_ = other.value_; this.bitField0_ &= 0xFFFFFFFD; this.valueBuilder_ = MysqlxNotice.SessionStateChanged.alwaysUseFieldBuilders ? getValueFieldBuilder() : null; } else { this.valueBuilder_.addAllMessages(other.value_); }  }  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */       public final boolean isInitialized() { if (!hasParam())
/*      */           return false;  for (int i = 0; i < getValueCount(); i++) { if (!getValue(i).isInitialized())
/*      */             return false;  }  return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxNotice.SessionStateChanged parsedMessage = null; try { parsedMessage = (MysqlxNotice.SessionStateChanged)MysqlxNotice.SessionStateChanged.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxNotice.SessionStateChanged)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null)
/*      */             mergeFrom(parsedMessage);  }  return this; }
/*      */       public boolean hasParam() { return ((this.bitField0_ & 0x1) != 0); }
/*      */       public MysqlxNotice.SessionStateChanged.Parameter getParam() { MysqlxNotice.SessionStateChanged.Parameter result = MysqlxNotice.SessionStateChanged.Parameter.valueOf(this.param_); return (result == null) ? MysqlxNotice.SessionStateChanged.Parameter.CURRENT_SCHEMA : result; }
/*      */       public Builder setParam(MysqlxNotice.SessionStateChanged.Parameter value) { if (value == null)
/*      */           throw new NullPointerException();  this.bitField0_ |= 0x1; this.param_ = value.getNumber(); onChanged(); return this; }
/*      */       public Builder clearParam() { this.bitField0_ &= 0xFFFFFFFE; this.param_ = 1; onChanged(); return this; }
/* 4067 */       public List<MysqlxDatatypes.Scalar> getValueList() { if (this.valueBuilder_ == null) {
/* 4068 */           return Collections.unmodifiableList(this.value_);
/*      */         }
/* 4070 */         return this.valueBuilder_.getMessageList(); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int getValueCount() {
/* 4081 */         if (this.valueBuilder_ == null) {
/* 4082 */           return this.value_.size();
/*      */         }
/* 4084 */         return this.valueBuilder_.getCount();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Scalar getValue(int index) {
/* 4095 */         if (this.valueBuilder_ == null) {
/* 4096 */           return this.value_.get(index);
/*      */         }
/* 4098 */         return (MysqlxDatatypes.Scalar)this.valueBuilder_.getMessage(index);
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
/*      */       public Builder setValue(int index, MysqlxDatatypes.Scalar value) {
/* 4110 */         if (this.valueBuilder_ == null) {
/* 4111 */           if (value == null) {
/* 4112 */             throw new NullPointerException();
/*      */           }
/* 4114 */           ensureValueIsMutable();
/* 4115 */           this.value_.set(index, value);
/* 4116 */           onChanged();
/*      */         } else {
/* 4118 */           this.valueBuilder_.setMessage(index, (AbstractMessage)value);
/*      */         } 
/* 4120 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setValue(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
/* 4131 */         if (this.valueBuilder_ == null) {
/* 4132 */           ensureValueIsMutable();
/* 4133 */           this.value_.set(index, builderForValue.build());
/* 4134 */           onChanged();
/*      */         } else {
/* 4136 */           this.valueBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 4138 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addValue(MysqlxDatatypes.Scalar value) {
/* 4148 */         if (this.valueBuilder_ == null) {
/* 4149 */           if (value == null) {
/* 4150 */             throw new NullPointerException();
/*      */           }
/* 4152 */           ensureValueIsMutable();
/* 4153 */           this.value_.add(value);
/* 4154 */           onChanged();
/*      */         } else {
/* 4156 */           this.valueBuilder_.addMessage((AbstractMessage)value);
/*      */         } 
/* 4158 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addValue(int index, MysqlxDatatypes.Scalar value) {
/* 4169 */         if (this.valueBuilder_ == null) {
/* 4170 */           if (value == null) {
/* 4171 */             throw new NullPointerException();
/*      */           }
/* 4173 */           ensureValueIsMutable();
/* 4174 */           this.value_.add(index, value);
/* 4175 */           onChanged();
/*      */         } else {
/* 4177 */           this.valueBuilder_.addMessage(index, (AbstractMessage)value);
/*      */         } 
/* 4179 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addValue(MysqlxDatatypes.Scalar.Builder builderForValue) {
/* 4190 */         if (this.valueBuilder_ == null) {
/* 4191 */           ensureValueIsMutable();
/* 4192 */           this.value_.add(builderForValue.build());
/* 4193 */           onChanged();
/*      */         } else {
/* 4195 */           this.valueBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 4197 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addValue(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
/* 4208 */         if (this.valueBuilder_ == null) {
/* 4209 */           ensureValueIsMutable();
/* 4210 */           this.value_.add(index, builderForValue.build());
/* 4211 */           onChanged();
/*      */         } else {
/* 4213 */           this.valueBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 4215 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addAllValue(Iterable<? extends MysqlxDatatypes.Scalar> values) {
/* 4226 */         if (this.valueBuilder_ == null) {
/* 4227 */           ensureValueIsMutable();
/* 4228 */           AbstractMessageLite.Builder.addAll(values, this.value_);
/*      */           
/* 4230 */           onChanged();
/*      */         } else {
/* 4232 */           this.valueBuilder_.addAllMessages(values);
/*      */         } 
/* 4234 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearValue() {
/* 4244 */         if (this.valueBuilder_ == null) {
/* 4245 */           this.value_ = Collections.emptyList();
/* 4246 */           this.bitField0_ &= 0xFFFFFFFD;
/* 4247 */           onChanged();
/*      */         } else {
/* 4249 */           this.valueBuilder_.clear();
/*      */         } 
/* 4251 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder removeValue(int index) {
/* 4261 */         if (this.valueBuilder_ == null) {
/* 4262 */           ensureValueIsMutable();
/* 4263 */           this.value_.remove(index);
/* 4264 */           onChanged();
/*      */         } else {
/* 4266 */           this.valueBuilder_.remove(index);
/*      */         } 
/* 4268 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Scalar.Builder getValueBuilder(int index) {
/* 4279 */         return (MysqlxDatatypes.Scalar.Builder)getValueFieldBuilder().getBuilder(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder(int index) {
/* 4290 */         if (this.valueBuilder_ == null)
/* 4291 */           return this.value_.get(index); 
/* 4292 */         return (MysqlxDatatypes.ScalarOrBuilder)this.valueBuilder_.getMessageOrBuilder(index);
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
/*      */       public List<? extends MysqlxDatatypes.ScalarOrBuilder> getValueOrBuilderList() {
/* 4304 */         if (this.valueBuilder_ != null) {
/* 4305 */           return this.valueBuilder_.getMessageOrBuilderList();
/*      */         }
/* 4307 */         return Collections.unmodifiableList((List)this.value_);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Scalar.Builder addValueBuilder() {
/* 4318 */         return (MysqlxDatatypes.Scalar.Builder)getValueFieldBuilder().addBuilder(
/* 4319 */             (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Scalar.Builder addValueBuilder(int index) {
/* 4330 */         return (MysqlxDatatypes.Scalar.Builder)getValueFieldBuilder().addBuilder(index, 
/* 4331 */             (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<MysqlxDatatypes.Scalar.Builder> getValueBuilderList() {
/* 4342 */         return getValueFieldBuilder().getBuilderList();
/*      */       }
/*      */ 
/*      */       
/*      */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getValueFieldBuilder() {
/* 4347 */         if (this.valueBuilder_ == null) {
/* 4348 */           this
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 4353 */             .valueBuilder_ = new RepeatedFieldBuilderV3(this.value_, ((this.bitField0_ & 0x2) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 4354 */           this.value_ = null;
/*      */         } 
/* 4356 */         return this.valueBuilder_;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 4361 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 4367 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4377 */     private static final SessionStateChanged DEFAULT_INSTANCE = new SessionStateChanged();
/*      */ 
/*      */     
/*      */     public static SessionStateChanged getDefaultInstance() {
/* 4381 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 4385 */     public static final Parser<SessionStateChanged> PARSER = (Parser<SessionStateChanged>)new AbstractParser<SessionStateChanged>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxNotice.SessionStateChanged parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 4391 */           return new MysqlxNotice.SessionStateChanged(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<SessionStateChanged> parser() {
/* 4396 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<SessionStateChanged> getParserForType() {
/* 4401 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public SessionStateChanged getDefaultInstanceForType() {
/* 4406 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface GroupReplicationStateChangedOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasType();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getType();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasViewId();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String getViewId();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getViewIdBytes();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class GroupReplicationStateChanged
/*      */     extends GeneratedMessageV3
/*      */     implements GroupReplicationStateChangedOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int TYPE_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */     
/*      */     private int type_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int VIEW_ID_FIELD_NUMBER = 2;
/*      */ 
/*      */ 
/*      */     
/*      */     private volatile Object viewId_;
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private GroupReplicationStateChanged(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 4482 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 4759 */       this.memoizedIsInitialized = -1; } private GroupReplicationStateChanged() { this.memoizedIsInitialized = -1; this.viewId_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new GroupReplicationStateChanged(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private GroupReplicationStateChanged(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.bitField0_ |= 0x1; this.type_ = input.readUInt32(); continue;case 18: bs = input.readBytes(); this.bitField0_ |= 0x2; this.viewId_ = bs; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxNotice.internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxNotice.internal_static_Mysqlx_Notice_GroupReplicationStateChanged_fieldAccessorTable.ensureFieldAccessorsInitialized(GroupReplicationStateChanged.class, Builder.class); } public enum Type implements ProtocolMessageEnum {
/*      */       MEMBERSHIP_QUORUM_LOSS(1), MEMBERSHIP_VIEW_CHANGE(2), MEMBER_ROLE_CHANGE(3), MEMBER_STATE_CHANGE(4); public static final int MEMBERSHIP_QUORUM_LOSS_VALUE = 1; public static final int MEMBERSHIP_VIEW_CHANGE_VALUE = 2; public static final int MEMBER_ROLE_CHANGE_VALUE = 3; public static final int MEMBER_STATE_CHANGE_VALUE = 4; private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() { public MysqlxNotice.GroupReplicationStateChanged.Type findValueByNumber(int number) { return MysqlxNotice.GroupReplicationStateChanged.Type.forNumber(number); } }
/*      */       ; private static final Type[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Type forNumber(int value) { switch (value) { case 1: return MEMBERSHIP_QUORUM_LOSS;case 2: return MEMBERSHIP_VIEW_CHANGE;case 3: return MEMBER_ROLE_CHANGE;case 4: return MEMBER_STATE_CHANGE; }  return null; } public static Internal.EnumLiteMap<Type> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxNotice.GroupReplicationStateChanged.getDescriptor().getEnumTypes().get(0); } Type(int value) { this.value = value; } } public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); } public int getType() { return this.type_; } public boolean hasViewId() { return ((this.bitField0_ & 0x2) != 0); } public String getViewId() { Object ref = this.viewId_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.viewId_ = s;  return s; } public ByteString getViewIdBytes() { Object ref = this.viewId_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.viewId_ = b; return b; }  return (ByteString)ref; }
/* 4762 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 4763 */       if (isInitialized == 1) return true; 
/* 4764 */       if (isInitialized == 0) return false;
/*      */       
/* 4766 */       if (!hasType()) {
/* 4767 */         this.memoizedIsInitialized = 0;
/* 4768 */         return false;
/*      */       } 
/* 4770 */       this.memoizedIsInitialized = 1;
/* 4771 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 4777 */       if ((this.bitField0_ & 0x1) != 0) {
/* 4778 */         output.writeUInt32(1, this.type_);
/*      */       }
/* 4780 */       if ((this.bitField0_ & 0x2) != 0) {
/* 4781 */         GeneratedMessageV3.writeString(output, 2, this.viewId_);
/*      */       }
/* 4783 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 4788 */       int size = this.memoizedSize;
/* 4789 */       if (size != -1) return size;
/*      */       
/* 4791 */       size = 0;
/* 4792 */       if ((this.bitField0_ & 0x1) != 0) {
/* 4793 */         size += 
/* 4794 */           CodedOutputStream.computeUInt32Size(1, this.type_);
/*      */       }
/* 4796 */       if ((this.bitField0_ & 0x2) != 0) {
/* 4797 */         size += GeneratedMessageV3.computeStringSize(2, this.viewId_);
/*      */       }
/* 4799 */       size += this.unknownFields.getSerializedSize();
/* 4800 */       this.memoizedSize = size;
/* 4801 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 4806 */       if (obj == this) {
/* 4807 */         return true;
/*      */       }
/* 4809 */       if (!(obj instanceof GroupReplicationStateChanged)) {
/* 4810 */         return super.equals(obj);
/*      */       }
/* 4812 */       GroupReplicationStateChanged other = (GroupReplicationStateChanged)obj;
/*      */       
/* 4814 */       if (hasType() != other.hasType()) return false; 
/* 4815 */       if (hasType() && 
/* 4816 */         getType() != other
/* 4817 */         .getType()) return false;
/*      */       
/* 4819 */       if (hasViewId() != other.hasViewId()) return false; 
/* 4820 */       if (hasViewId() && 
/*      */         
/* 4822 */         !getViewId().equals(other.getViewId())) return false;
/*      */       
/* 4824 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 4825 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 4830 */       if (this.memoizedHashCode != 0) {
/* 4831 */         return this.memoizedHashCode;
/*      */       }
/* 4833 */       int hash = 41;
/* 4834 */       hash = 19 * hash + getDescriptor().hashCode();
/* 4835 */       if (hasType()) {
/* 4836 */         hash = 37 * hash + 1;
/* 4837 */         hash = 53 * hash + getType();
/*      */       } 
/* 4839 */       if (hasViewId()) {
/* 4840 */         hash = 37 * hash + 2;
/* 4841 */         hash = 53 * hash + getViewId().hashCode();
/*      */       } 
/* 4843 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 4844 */       this.memoizedHashCode = hash;
/* 4845 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static GroupReplicationStateChanged parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 4851 */       return (GroupReplicationStateChanged)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static GroupReplicationStateChanged parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 4857 */       return (GroupReplicationStateChanged)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static GroupReplicationStateChanged parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 4862 */       return (GroupReplicationStateChanged)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static GroupReplicationStateChanged parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 4868 */       return (GroupReplicationStateChanged)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static GroupReplicationStateChanged parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 4872 */       return (GroupReplicationStateChanged)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static GroupReplicationStateChanged parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 4878 */       return (GroupReplicationStateChanged)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static GroupReplicationStateChanged parseFrom(InputStream input) throws IOException {
/* 4882 */       return 
/* 4883 */         (GroupReplicationStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static GroupReplicationStateChanged parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 4889 */       return 
/* 4890 */         (GroupReplicationStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static GroupReplicationStateChanged parseDelimitedFrom(InputStream input) throws IOException {
/* 4894 */       return 
/* 4895 */         (GroupReplicationStateChanged)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static GroupReplicationStateChanged parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 4901 */       return 
/* 4902 */         (GroupReplicationStateChanged)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static GroupReplicationStateChanged parseFrom(CodedInputStream input) throws IOException {
/* 4907 */       return 
/* 4908 */         (GroupReplicationStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static GroupReplicationStateChanged parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 4914 */       return 
/* 4915 */         (GroupReplicationStateChanged)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 4919 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 4921 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(GroupReplicationStateChanged prototype) {
/* 4924 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 4928 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 4929 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 4935 */       Builder builder = new Builder(parent);
/* 4936 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxNotice.GroupReplicationStateChangedOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */       
/*      */       private int type_;
/*      */ 
/*      */       
/*      */       private Object viewId_;
/*      */ 
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 4956 */         return MysqlxNotice.internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 4962 */         return MysqlxNotice.internal_static_Mysqlx_Notice_GroupReplicationStateChanged_fieldAccessorTable
/* 4963 */           .ensureFieldAccessorsInitialized(MysqlxNotice.GroupReplicationStateChanged.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/* 5168 */         this.viewId_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.viewId_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxNotice.GroupReplicationStateChanged.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.type_ = 0; this.bitField0_ &= 0xFFFFFFFE; this.viewId_ = ""; this.bitField0_ &= 0xFFFFFFFD; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxNotice.internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor; } public MysqlxNotice.GroupReplicationStateChanged getDefaultInstanceForType() { return MysqlxNotice.GroupReplicationStateChanged.getDefaultInstance(); } public MysqlxNotice.GroupReplicationStateChanged build() { MysqlxNotice.GroupReplicationStateChanged result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxNotice.GroupReplicationStateChanged buildPartial() { MysqlxNotice.GroupReplicationStateChanged result = new MysqlxNotice.GroupReplicationStateChanged(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { result.type_ = this.type_; to_bitField0_ |= 0x1; }  if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.viewId_ = this.viewId_; result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); }
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); }
/*      */       public Builder mergeFrom(Message other) { if (other instanceof MysqlxNotice.GroupReplicationStateChanged) return mergeFrom((MysqlxNotice.GroupReplicationStateChanged)other);  super.mergeFrom(other); return this; }
/*      */       public Builder mergeFrom(MysqlxNotice.GroupReplicationStateChanged other) { if (other == MysqlxNotice.GroupReplicationStateChanged.getDefaultInstance()) return this;  if (other.hasType()) setType(other.getType());  if (other.hasViewId()) { this.bitField0_ |= 0x2; this.viewId_ = other.viewId_; onChanged(); }  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */       public final boolean isInitialized() { if (!hasType()) return false;  return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxNotice.GroupReplicationStateChanged parsedMessage = null; try { parsedMessage = (MysqlxNotice.GroupReplicationStateChanged)MysqlxNotice.GroupReplicationStateChanged.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxNotice.GroupReplicationStateChanged)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/*      */       public boolean hasType() { return ((this.bitField0_ & 0x1) != 0); }
/*      */       public int getType() { return this.type_; }
/*      */       public Builder setType(int value) { this.bitField0_ |= 0x1; this.type_ = value; onChanged(); return this; }
/*      */       public Builder clearType() { this.bitField0_ &= 0xFFFFFFFE; this.type_ = 0; onChanged(); return this; }
/* 5178 */       public boolean hasViewId() { return ((this.bitField0_ & 0x2) != 0); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public String getViewId() {
/* 5189 */         Object ref = this.viewId_;
/* 5190 */         if (!(ref instanceof String)) {
/* 5191 */           ByteString bs = (ByteString)ref;
/*      */           
/* 5193 */           String s = bs.toStringUtf8();
/* 5194 */           if (bs.isValidUtf8()) {
/* 5195 */             this.viewId_ = s;
/*      */           }
/* 5197 */           return s;
/*      */         } 
/* 5199 */         return (String)ref;
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
/*      */       public ByteString getViewIdBytes() {
/* 5212 */         Object ref = this.viewId_;
/* 5213 */         if (ref instanceof String) {
/*      */           
/* 5215 */           ByteString b = ByteString.copyFromUtf8((String)ref);
/*      */           
/* 5217 */           this.viewId_ = b;
/* 5218 */           return b;
/*      */         } 
/* 5220 */         return (ByteString)ref;
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
/*      */       public Builder setViewId(String value) {
/* 5234 */         if (value == null) {
/* 5235 */           throw new NullPointerException();
/*      */         }
/* 5237 */         this.bitField0_ |= 0x2;
/* 5238 */         this.viewId_ = value;
/* 5239 */         onChanged();
/* 5240 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearViewId() {
/* 5251 */         this.bitField0_ &= 0xFFFFFFFD;
/* 5252 */         this.viewId_ = MysqlxNotice.GroupReplicationStateChanged.getDefaultInstance().getViewId();
/* 5253 */         onChanged();
/* 5254 */         return this;
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
/*      */       public Builder setViewIdBytes(ByteString value) {
/* 5267 */         if (value == null) {
/* 5268 */           throw new NullPointerException();
/*      */         }
/* 5270 */         this.bitField0_ |= 0x2;
/* 5271 */         this.viewId_ = value;
/* 5272 */         onChanged();
/* 5273 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 5278 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 5284 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5294 */     private static final GroupReplicationStateChanged DEFAULT_INSTANCE = new GroupReplicationStateChanged();
/*      */ 
/*      */     
/*      */     public static GroupReplicationStateChanged getDefaultInstance() {
/* 5298 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 5302 */     public static final Parser<GroupReplicationStateChanged> PARSER = (Parser<GroupReplicationStateChanged>)new AbstractParser<GroupReplicationStateChanged>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxNotice.GroupReplicationStateChanged parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 5308 */           return new MysqlxNotice.GroupReplicationStateChanged(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<GroupReplicationStateChanged> parser() {
/* 5313 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<GroupReplicationStateChanged> getParserForType() {
/* 5318 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public GroupReplicationStateChanged getDefaultInstanceForType() {
/* 5323 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface ServerHelloOrBuilder
/*      */     extends MessageOrBuilder {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class ServerHello
/*      */     extends GeneratedMessageV3
/*      */     implements ServerHelloOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private ServerHello(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 5351 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 5418 */       this.memoizedIsInitialized = -1; } private ServerHello() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new ServerHello(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private ServerHello(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/*      */     public static final Descriptors.Descriptor getDescriptor() { return MysqlxNotice.internal_static_Mysqlx_Notice_ServerHello_descriptor; }
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxNotice.internal_static_Mysqlx_Notice_ServerHello_fieldAccessorTable.ensureFieldAccessorsInitialized(ServerHello.class, Builder.class); }
/* 5421 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 5422 */       if (isInitialized == 1) return true; 
/* 5423 */       if (isInitialized == 0) return false;
/*      */       
/* 5425 */       this.memoizedIsInitialized = 1;
/* 5426 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 5432 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 5437 */       int size = this.memoizedSize;
/* 5438 */       if (size != -1) return size;
/*      */       
/* 5440 */       size = 0;
/* 5441 */       size += this.unknownFields.getSerializedSize();
/* 5442 */       this.memoizedSize = size;
/* 5443 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 5448 */       if (obj == this) {
/* 5449 */         return true;
/*      */       }
/* 5451 */       if (!(obj instanceof ServerHello)) {
/* 5452 */         return super.equals(obj);
/*      */       }
/* 5454 */       ServerHello other = (ServerHello)obj;
/*      */       
/* 5456 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 5457 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 5462 */       if (this.memoizedHashCode != 0) {
/* 5463 */         return this.memoizedHashCode;
/*      */       }
/* 5465 */       int hash = 41;
/* 5466 */       hash = 19 * hash + getDescriptor().hashCode();
/* 5467 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 5468 */       this.memoizedHashCode = hash;
/* 5469 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ServerHello parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 5475 */       return (ServerHello)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ServerHello parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 5481 */       return (ServerHello)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static ServerHello parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 5486 */       return (ServerHello)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ServerHello parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 5492 */       return (ServerHello)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static ServerHello parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 5496 */       return (ServerHello)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ServerHello parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 5502 */       return (ServerHello)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static ServerHello parseFrom(InputStream input) throws IOException {
/* 5506 */       return 
/* 5507 */         (ServerHello)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ServerHello parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 5513 */       return 
/* 5514 */         (ServerHello)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static ServerHello parseDelimitedFrom(InputStream input) throws IOException {
/* 5518 */       return 
/* 5519 */         (ServerHello)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ServerHello parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 5525 */       return 
/* 5526 */         (ServerHello)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static ServerHello parseFrom(CodedInputStream input) throws IOException {
/* 5531 */       return 
/* 5532 */         (ServerHello)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ServerHello parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 5538 */       return 
/* 5539 */         (ServerHello)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 5543 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 5545 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(ServerHello prototype) {
/* 5548 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 5552 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 5553 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 5559 */       Builder builder = new Builder(parent);
/* 5560 */       return builder;
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
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxNotice.ServerHelloOrBuilder
/*      */     {
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 5580 */         return MysqlxNotice.internal_static_Mysqlx_Notice_ServerHello_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 5586 */         return MysqlxNotice.internal_static_Mysqlx_Notice_ServerHello_fieldAccessorTable
/* 5587 */           .ensureFieldAccessorsInitialized(MysqlxNotice.ServerHello.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 5593 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 5598 */         super(parent);
/* 5599 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 5603 */         if (MysqlxNotice.ServerHello.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/* 5608 */         super.clear();
/* 5609 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 5615 */         return MysqlxNotice.internal_static_Mysqlx_Notice_ServerHello_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxNotice.ServerHello getDefaultInstanceForType() {
/* 5620 */         return MysqlxNotice.ServerHello.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxNotice.ServerHello build() {
/* 5625 */         MysqlxNotice.ServerHello result = buildPartial();
/* 5626 */         if (!result.isInitialized()) {
/* 5627 */           throw newUninitializedMessageException(result);
/*      */         }
/* 5629 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxNotice.ServerHello buildPartial() {
/* 5634 */         MysqlxNotice.ServerHello result = new MysqlxNotice.ServerHello(this);
/* 5635 */         onBuilt();
/* 5636 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 5641 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 5647 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 5652 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 5657 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 5663 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 5669 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 5673 */         if (other instanceof MysqlxNotice.ServerHello) {
/* 5674 */           return mergeFrom((MysqlxNotice.ServerHello)other);
/*      */         }
/* 5676 */         super.mergeFrom(other);
/* 5677 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxNotice.ServerHello other) {
/* 5682 */         if (other == MysqlxNotice.ServerHello.getDefaultInstance()) return this; 
/* 5683 */         mergeUnknownFields(other.unknownFields);
/* 5684 */         onChanged();
/* 5685 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 5690 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 5698 */         MysqlxNotice.ServerHello parsedMessage = null;
/*      */         try {
/* 5700 */           parsedMessage = (MysqlxNotice.ServerHello)MysqlxNotice.ServerHello.PARSER.parsePartialFrom(input, extensionRegistry);
/* 5701 */         } catch (InvalidProtocolBufferException e) {
/* 5702 */           parsedMessage = (MysqlxNotice.ServerHello)e.getUnfinishedMessage();
/* 5703 */           throw e.unwrapIOException();
/*      */         } finally {
/* 5705 */           if (parsedMessage != null) {
/* 5706 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 5709 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 5714 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 5720 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5730 */     private static final ServerHello DEFAULT_INSTANCE = new ServerHello();
/*      */ 
/*      */     
/*      */     public static ServerHello getDefaultInstance() {
/* 5734 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 5738 */     public static final Parser<ServerHello> PARSER = (Parser<ServerHello>)new AbstractParser<ServerHello>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxNotice.ServerHello parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 5744 */           return new MysqlxNotice.ServerHello(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<ServerHello> parser() {
/* 5749 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<ServerHello> getParserForType() {
/* 5754 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public ServerHello getDefaultInstanceForType() {
/* 5759 */       return DEFAULT_INSTANCE;
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
/* 5797 */     return descriptor;
/*      */   }
/*      */ 
/*      */   
/*      */   static {
/* 5802 */     String[] descriptorData = { "\n\023mysqlx_notice.proto\022\rMysqlx.Notice\032\fmysqlx.proto\032\026mysqlx_datatypes.proto\"\002\n\005Frame\022\f\n\004type\030\001 \002(\r\0221\n\005scope\030\002 \001(\0162\032.Mysqlx.Notice.Frame.Scope:\006GLOBAL\022\017\n\007payload\030\003 \001(\f\"\036\n\005Scope\022\n\n\006GLOBAL\020\001\022\t\n\005LOCAL\020\002\"\001\n\004Type\022\013\n\007WARNING\020\001\022\034\n\030SESSION_VARIABLE_CHANGED\020\002\022\031\n\025SESSION_STATE_CHANGED\020\003\022#\n\037GROUP_REPLICATION_STATE_CHANGED\020\004\022\020\n\fSERVER_HELLO\020\005:\004ê0\013\"\001\n\007Warning\0224\n\005level\030\001 \001(\0162\034.Mysqlx.Notice.Warning.Level:\007WARNING\022\f\n\004code\030\002 \002(\r\022\013\n\003msg\030\003 \002(\t\")\n\005Level\022\b\n\004NOTE\020\001\022\013\n\007WARNING\020\002\022\t\n\005ERROR\020\003\"P\n\026SessionVariableChanged\022\r\n\005param\030\001 \002(\t\022'\n\005value\030\002 \001(\0132\030.Mysqlx.Datatypes.Scalar\"ñ\002\n\023SessionStateChanged\022;\n\005param\030\001 \002(\0162,.Mysqlx.Notice.SessionStateChanged.Parameter\022'\n\005value\030\002 \003(\0132\030.Mysqlx.Datatypes.Scalar\"ó\001\n\tParameter\022\022\n\016CURRENT_SCHEMA\020\001\022\023\n\017ACCOUNT_EXPIRED\020\002\022\027\n\023GENERATED_INSERT_ID\020\003\022\021\n\rROWS_AFFECTED\020\004\022\016\n\nROWS_FOUND\020\005\022\020\n\fROWS_MATCHED\020\006\022\021\n\rTRX_COMMITTED\020\007\022\022\n\016TRX_ROLLEDBACK\020\t\022\024\n\020PRODUCED_MESSAGE\020\n\022\026\n\022CLIENT_ID_ASSIGNED\020\013\022\032\n\026GENERATED_DOCUMENT_IDS\020\f\"®\001\n\034GroupReplicationStateChanged\022\f\n\004type\030\001 \002(\r\022\017\n\007view_id\030\002 \001(\t\"o\n\004Type\022\032\n\026MEMBERSHIP_QUORUM_LOSS\020\001\022\032\n\026MEMBERSHIP_VIEW_CHANGE\020\002\022\026\n\022MEMBER_ROLE_CHANGE\020\003\022\027\n\023MEMBER_STATE_CHANGE\020\004\"\r\n\013ServerHelloB\031\n\027com.mysql.cj.x.protobuf" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5834 */     descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] {
/*      */           
/* 5836 */           Mysqlx.getDescriptor(), 
/* 5837 */           MysqlxDatatypes.getDescriptor()
/*      */         });
/*      */     
/* 5840 */     internal_static_Mysqlx_Notice_Frame_descriptor = getDescriptor().getMessageTypes().get(0);
/* 5841 */     internal_static_Mysqlx_Notice_Frame_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_Frame_descriptor, new String[] { "Type", "Scope", "Payload" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5846 */     internal_static_Mysqlx_Notice_Warning_descriptor = getDescriptor().getMessageTypes().get(1);
/* 5847 */     internal_static_Mysqlx_Notice_Warning_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_Warning_descriptor, new String[] { "Level", "Code", "Msg" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5852 */     internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor = getDescriptor().getMessageTypes().get(2);
/* 5853 */     internal_static_Mysqlx_Notice_SessionVariableChanged_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor, new String[] { "Param", "Value" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5858 */     internal_static_Mysqlx_Notice_SessionStateChanged_descriptor = getDescriptor().getMessageTypes().get(3);
/* 5859 */     internal_static_Mysqlx_Notice_SessionStateChanged_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_SessionStateChanged_descriptor, new String[] { "Param", "Value" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5864 */     internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor = getDescriptor().getMessageTypes().get(4);
/* 5865 */     internal_static_Mysqlx_Notice_GroupReplicationStateChanged_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor, new String[] { "Type", "ViewId" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5870 */     internal_static_Mysqlx_Notice_ServerHello_descriptor = getDescriptor().getMessageTypes().get(5);
/* 5871 */     internal_static_Mysqlx_Notice_ServerHello_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_ServerHello_descriptor, new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5876 */     ExtensionRegistry registry = ExtensionRegistry.newInstance();
/* 5877 */     registry.add(Mysqlx.serverMessageId);
/*      */     
/* 5879 */     Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
/* 5880 */     Mysqlx.getDescriptor();
/* 5881 */     MysqlxDatatypes.getDescriptor();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\x\protobuf\MysqlxNotice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */