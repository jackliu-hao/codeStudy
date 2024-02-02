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
/*      */ import com.google.protobuf.InvalidProtocolBufferException;
/*      */ import com.google.protobuf.Message;
/*      */ import com.google.protobuf.MessageLite;
/*      */ import com.google.protobuf.MessageOrBuilder;
/*      */ import com.google.protobuf.Parser;
/*      */ import com.google.protobuf.RepeatedFieldBuilderV3;
/*      */ import com.google.protobuf.SingleFieldBuilderV3;
/*      */ import com.google.protobuf.UnknownFieldSet;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ 
/*      */ public final class MysqlxConnection {
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_Capability_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_Capability_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_Capabilities_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_Capabilities_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_CapabilitiesGet_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_CapabilitiesSet_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_Close_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_Close_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_Compression_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_Compression_fieldAccessorTable;
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
/*      */   public static interface CapabilityOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasName();
/*      */ 
/*      */ 
/*      */     
/*      */     String getName();
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getNameBytes();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasValue();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.Any getValue();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.AnyOrBuilder getValueOrBuilder();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Capability
/*      */     extends GeneratedMessageV3
/*      */     implements CapabilityOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */     
/*      */     public static final int NAME_FIELD_NUMBER = 1;
/*      */     
/*      */     private volatile Object name_;
/*      */     
/*      */     public static final int VALUE_FIELD_NUMBER = 2;
/*      */     
/*      */     private MysqlxDatatypes.Any value_;
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */     
/*      */     private Capability(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/*   99 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  256 */       this.memoizedIsInitialized = -1; } private Capability() { this.memoizedIsInitialized = -1; this.name_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Capability(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Capability(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; MysqlxDatatypes.Any.Builder subBuilder; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: bs = input.readBytes(); this.bitField0_ |= 0x1; this.name_ = bs; continue;case 18: subBuilder = null; if ((this.bitField0_ & 0x2) != 0) subBuilder = this.value_.toBuilder();  this.value_ = (MysqlxDatatypes.Any)input.readMessage(MysqlxDatatypes.Any.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.value_); this.value_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x2; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/*      */     public static final Descriptors.Descriptor getDescriptor() { return MysqlxConnection.internal_static_Mysqlx_Connection_Capability_descriptor; }
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxConnection.internal_static_Mysqlx_Connection_Capability_fieldAccessorTable.ensureFieldAccessorsInitialized(Capability.class, Builder.class); }
/*  259 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  260 */       if (isInitialized == 1) return true; 
/*  261 */       if (isInitialized == 0) return false;
/*      */       
/*  263 */       if (!hasName()) {
/*  264 */         this.memoizedIsInitialized = 0;
/*  265 */         return false;
/*      */       } 
/*  267 */       if (!hasValue()) {
/*  268 */         this.memoizedIsInitialized = 0;
/*  269 */         return false;
/*      */       } 
/*  271 */       if (!getValue().isInitialized()) {
/*  272 */         this.memoizedIsInitialized = 0;
/*  273 */         return false;
/*      */       } 
/*  275 */       this.memoizedIsInitialized = 1;
/*  276 */       return true; } public boolean hasName() { return ((this.bitField0_ & 0x1) != 0); }
/*      */     public String getName() { Object ref = this.name_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.name_ = s;  return s; }
/*      */     public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; }
/*      */     public boolean hasValue() { return ((this.bitField0_ & 0x2) != 0); }
/*      */     public MysqlxDatatypes.Any getValue() { return (this.value_ == null) ? MysqlxDatatypes.Any.getDefaultInstance() : this.value_; }
/*      */     public MysqlxDatatypes.AnyOrBuilder getValueOrBuilder() { return (this.value_ == null) ? MysqlxDatatypes.Any.getDefaultInstance() : this.value_; }
/*  282 */     public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) {
/*  283 */         GeneratedMessageV3.writeString(output, 1, this.name_);
/*      */       }
/*  285 */       if ((this.bitField0_ & 0x2) != 0) {
/*  286 */         output.writeMessage(2, (MessageLite)getValue());
/*      */       }
/*  288 */       this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/*  293 */       int size = this.memoizedSize;
/*  294 */       if (size != -1) return size;
/*      */       
/*  296 */       size = 0;
/*  297 */       if ((this.bitField0_ & 0x1) != 0) {
/*  298 */         size += GeneratedMessageV3.computeStringSize(1, this.name_);
/*      */       }
/*  300 */       if ((this.bitField0_ & 0x2) != 0) {
/*  301 */         size += 
/*  302 */           CodedOutputStream.computeMessageSize(2, (MessageLite)getValue());
/*      */       }
/*  304 */       size += this.unknownFields.getSerializedSize();
/*  305 */       this.memoizedSize = size;
/*  306 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  311 */       if (obj == this) {
/*  312 */         return true;
/*      */       }
/*  314 */       if (!(obj instanceof Capability)) {
/*  315 */         return super.equals(obj);
/*      */       }
/*  317 */       Capability other = (Capability)obj;
/*      */       
/*  319 */       if (hasName() != other.hasName()) return false; 
/*  320 */       if (hasName() && 
/*      */         
/*  322 */         !getName().equals(other.getName())) return false;
/*      */       
/*  324 */       if (hasValue() != other.hasValue()) return false; 
/*  325 */       if (hasValue() && 
/*      */         
/*  327 */         !getValue().equals(other.getValue())) return false;
/*      */       
/*  329 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  330 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  335 */       if (this.memoizedHashCode != 0) {
/*  336 */         return this.memoizedHashCode;
/*      */       }
/*  338 */       int hash = 41;
/*  339 */       hash = 19 * hash + getDescriptor().hashCode();
/*  340 */       if (hasName()) {
/*  341 */         hash = 37 * hash + 1;
/*  342 */         hash = 53 * hash + getName().hashCode();
/*      */       } 
/*  344 */       if (hasValue()) {
/*  345 */         hash = 37 * hash + 2;
/*  346 */         hash = 53 * hash + getValue().hashCode();
/*      */       } 
/*  348 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  349 */       this.memoizedHashCode = hash;
/*  350 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Capability parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  356 */       return (Capability)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Capability parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  362 */       return (Capability)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Capability parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  367 */       return (Capability)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Capability parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  373 */       return (Capability)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Capability parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  377 */       return (Capability)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Capability parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  383 */       return (Capability)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Capability parseFrom(InputStream input) throws IOException {
/*  387 */       return 
/*  388 */         (Capability)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Capability parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  394 */       return 
/*  395 */         (Capability)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Capability parseDelimitedFrom(InputStream input) throws IOException {
/*  399 */       return 
/*  400 */         (Capability)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Capability parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  406 */       return 
/*  407 */         (Capability)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Capability parseFrom(CodedInputStream input) throws IOException {
/*  412 */       return 
/*  413 */         (Capability)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Capability parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  419 */       return 
/*  420 */         (Capability)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/*  424 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/*  426 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Capability prototype) {
/*  429 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/*  433 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  434 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  440 */       Builder builder = new Builder(parent);
/*  441 */       return builder;
/*      */     }
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxConnection.CapabilityOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */       
/*      */       private Object name_;
/*      */       
/*      */       private MysqlxDatatypes.Any value_;
/*      */       
/*      */       private SingleFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> valueBuilder_;
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/*  458 */         return MysqlxConnection.internal_static_Mysqlx_Connection_Capability_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  464 */         return MysqlxConnection.internal_static_Mysqlx_Connection_Capability_fieldAccessorTable
/*  465 */           .ensureFieldAccessorsInitialized(MysqlxConnection.Capability.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/*  632 */         this.name_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.name_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxConnection.Capability.alwaysUseFieldBuilders) getValueFieldBuilder();  } public Builder clear() { super.clear(); this.name_ = ""; this.bitField0_ &= 0xFFFFFFFE; if (this.valueBuilder_ == null) { this.value_ = null; } else { this.valueBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFD; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxConnection.internal_static_Mysqlx_Connection_Capability_descriptor; }
/*      */       public MysqlxConnection.Capability getDefaultInstanceForType() { return MysqlxConnection.Capability.getDefaultInstance(); }
/*      */       public MysqlxConnection.Capability build() { MysqlxConnection.Capability result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; }
/*      */       public MysqlxConnection.Capability buildPartial() { MysqlxConnection.Capability result = new MysqlxConnection.Capability(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.name_ = this.name_; if ((from_bitField0_ & 0x2) != 0) { if (this.valueBuilder_ == null) { result.value_ = this.value_; } else { result.value_ = (MysqlxDatatypes.Any)this.valueBuilder_.build(); }  to_bitField0_ |= 0x2; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; }
/*      */       public Builder clone() { return (Builder)super.clone(); }
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); }
/*  638 */       public boolean hasName() { return ((this.bitField0_ & 0x1) != 0); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); }
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); }
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); }
/*      */       public Builder mergeFrom(Message other) { if (other instanceof MysqlxConnection.Capability) return mergeFrom((MysqlxConnection.Capability)other);  super.mergeFrom(other); return this; }
/*      */       public Builder mergeFrom(MysqlxConnection.Capability other) { if (other == MysqlxConnection.Capability.getDefaultInstance()) return this;  if (other.hasName()) { this.bitField0_ |= 0x1; this.name_ = other.name_; onChanged(); }  if (other.hasValue()) mergeValue(other.getValue());  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */       public final boolean isInitialized() { if (!hasName()) return false;  if (!hasValue()) return false;  if (!getValue().isInitialized()) return false;  return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxConnection.Capability parsedMessage = null; try { parsedMessage = (MysqlxConnection.Capability)MysqlxConnection.Capability.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxConnection.Capability)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/*  645 */       public String getName() { Object ref = this.name_;
/*  646 */         if (!(ref instanceof String)) {
/*  647 */           ByteString bs = (ByteString)ref;
/*      */           
/*  649 */           String s = bs.toStringUtf8();
/*  650 */           if (bs.isValidUtf8()) {
/*  651 */             this.name_ = s;
/*      */           }
/*  653 */           return s;
/*      */         } 
/*  655 */         return (String)ref; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public ByteString getNameBytes() {
/*  664 */         Object ref = this.name_;
/*  665 */         if (ref instanceof String) {
/*      */           
/*  667 */           ByteString b = ByteString.copyFromUtf8((String)ref);
/*      */           
/*  669 */           this.name_ = b;
/*  670 */           return b;
/*      */         } 
/*  672 */         return (ByteString)ref;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setName(String value) {
/*  682 */         if (value == null) {
/*  683 */           throw new NullPointerException();
/*      */         }
/*  685 */         this.bitField0_ |= 0x1;
/*  686 */         this.name_ = value;
/*  687 */         onChanged();
/*  688 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearName() {
/*  695 */         this.bitField0_ &= 0xFFFFFFFE;
/*  696 */         this.name_ = MysqlxConnection.Capability.getDefaultInstance().getName();
/*  697 */         onChanged();
/*  698 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setNameBytes(ByteString value) {
/*  707 */         if (value == null) {
/*  708 */           throw new NullPointerException();
/*      */         }
/*  710 */         this.bitField0_ |= 0x1;
/*  711 */         this.name_ = value;
/*  712 */         onChanged();
/*  713 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean hasValue() {
/*  724 */         return ((this.bitField0_ & 0x2) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Any getValue() {
/*  731 */         if (this.valueBuilder_ == null) {
/*  732 */           return (this.value_ == null) ? MysqlxDatatypes.Any.getDefaultInstance() : this.value_;
/*      */         }
/*  734 */         return (MysqlxDatatypes.Any)this.valueBuilder_.getMessage();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setValue(MysqlxDatatypes.Any value) {
/*  741 */         if (this.valueBuilder_ == null) {
/*  742 */           if (value == null) {
/*  743 */             throw new NullPointerException();
/*      */           }
/*  745 */           this.value_ = value;
/*  746 */           onChanged();
/*      */         } else {
/*  748 */           this.valueBuilder_.setMessage((AbstractMessage)value);
/*      */         } 
/*  750 */         this.bitField0_ |= 0x2;
/*  751 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setValue(MysqlxDatatypes.Any.Builder builderForValue) {
/*  758 */         if (this.valueBuilder_ == null) {
/*  759 */           this.value_ = builderForValue.build();
/*  760 */           onChanged();
/*      */         } else {
/*  762 */           this.valueBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/*  764 */         this.bitField0_ |= 0x2;
/*  765 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeValue(MysqlxDatatypes.Any value) {
/*  771 */         if (this.valueBuilder_ == null) {
/*  772 */           if ((this.bitField0_ & 0x2) != 0 && this.value_ != null && this.value_ != 
/*      */             
/*  774 */             MysqlxDatatypes.Any.getDefaultInstance()) {
/*  775 */             this
/*  776 */               .value_ = MysqlxDatatypes.Any.newBuilder(this.value_).mergeFrom(value).buildPartial();
/*      */           } else {
/*  778 */             this.value_ = value;
/*      */           } 
/*  780 */           onChanged();
/*      */         } else {
/*  782 */           this.valueBuilder_.mergeFrom((AbstractMessage)value);
/*      */         } 
/*  784 */         this.bitField0_ |= 0x2;
/*  785 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearValue() {
/*  791 */         if (this.valueBuilder_ == null) {
/*  792 */           this.value_ = null;
/*  793 */           onChanged();
/*      */         } else {
/*  795 */           this.valueBuilder_.clear();
/*      */         } 
/*  797 */         this.bitField0_ &= 0xFFFFFFFD;
/*  798 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Any.Builder getValueBuilder() {
/*  804 */         this.bitField0_ |= 0x2;
/*  805 */         onChanged();
/*  806 */         return (MysqlxDatatypes.Any.Builder)getValueFieldBuilder().getBuilder();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.AnyOrBuilder getValueOrBuilder() {
/*  812 */         if (this.valueBuilder_ != null) {
/*  813 */           return (MysqlxDatatypes.AnyOrBuilder)this.valueBuilder_.getMessageOrBuilder();
/*      */         }
/*  815 */         return (this.value_ == null) ? 
/*  816 */           MysqlxDatatypes.Any.getDefaultInstance() : this.value_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private SingleFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> getValueFieldBuilder() {
/*  825 */         if (this.valueBuilder_ == null) {
/*  826 */           this
/*      */ 
/*      */ 
/*      */             
/*  830 */             .valueBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getValue(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/*  831 */           this.value_ = null;
/*      */         } 
/*  833 */         return this.valueBuilder_;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  838 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  844 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  854 */     private static final Capability DEFAULT_INSTANCE = new Capability();
/*      */ 
/*      */     
/*      */     public static Capability getDefaultInstance() {
/*  858 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/*  862 */     public static final Parser<Capability> PARSER = (Parser<Capability>)new AbstractParser<Capability>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxConnection.Capability parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/*  868 */           return new MysqlxConnection.Capability(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Capability> parser() {
/*  873 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Capability> getParserForType() {
/*  878 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Capability getDefaultInstanceForType() {
/*  883 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface CapabilitiesOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     List<MysqlxConnection.Capability> getCapabilitiesList();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxConnection.Capability getCapabilities(int param1Int);
/*      */ 
/*      */ 
/*      */     
/*      */     int getCapabilitiesCount();
/*      */ 
/*      */ 
/*      */     
/*      */     List<? extends MysqlxConnection.CapabilityOrBuilder> getCapabilitiesOrBuilderList();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxConnection.CapabilityOrBuilder getCapabilitiesOrBuilder(int param1Int);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Capabilities
/*      */     extends GeneratedMessageV3
/*      */     implements CapabilitiesOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     
/*      */     public static final int CAPABILITIES_FIELD_NUMBER = 1;
/*      */ 
/*      */     
/*      */     private List<MysqlxConnection.Capability> capabilities_;
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */     
/*      */     private Capabilities(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/*  932 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1048 */       this.memoizedIsInitialized = -1; } private Capabilities() { this.memoizedIsInitialized = -1; this.capabilities_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Capabilities(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Capabilities(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: if ((mutable_bitField0_ & 0x1) == 0) { this.capabilities_ = new ArrayList<>(); mutable_bitField0_ |= 0x1; }  this.capabilities_.add(input.readMessage(MysqlxConnection.Capability.PARSER, extensionRegistry)); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x1) != 0) this.capabilities_ = Collections.unmodifiableList(this.capabilities_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxConnection.internal_static_Mysqlx_Connection_Capabilities_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxConnection.internal_static_Mysqlx_Connection_Capabilities_fieldAccessorTable.ensureFieldAccessorsInitialized(Capabilities.class, Builder.class); } public List<MysqlxConnection.Capability> getCapabilitiesList() { return this.capabilities_; } public List<? extends MysqlxConnection.CapabilityOrBuilder> getCapabilitiesOrBuilderList() { return (List)this.capabilities_; } public int getCapabilitiesCount() { return this.capabilities_.size(); }
/*      */     public MysqlxConnection.Capability getCapabilities(int index) { return this.capabilities_.get(index); }
/*      */     public MysqlxConnection.CapabilityOrBuilder getCapabilitiesOrBuilder(int index) { return this.capabilities_.get(index); }
/* 1051 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 1052 */       if (isInitialized == 1) return true; 
/* 1053 */       if (isInitialized == 0) return false;
/*      */       
/* 1055 */       for (int i = 0; i < getCapabilitiesCount(); i++) {
/* 1056 */         if (!getCapabilities(i).isInitialized()) {
/* 1057 */           this.memoizedIsInitialized = 0;
/* 1058 */           return false;
/*      */         } 
/*      */       } 
/* 1061 */       this.memoizedIsInitialized = 1;
/* 1062 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 1068 */       for (int i = 0; i < this.capabilities_.size(); i++) {
/* 1069 */         output.writeMessage(1, (MessageLite)this.capabilities_.get(i));
/*      */       }
/* 1071 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 1076 */       int size = this.memoizedSize;
/* 1077 */       if (size != -1) return size;
/*      */       
/* 1079 */       size = 0;
/* 1080 */       for (int i = 0; i < this.capabilities_.size(); i++) {
/* 1081 */         size += 
/* 1082 */           CodedOutputStream.computeMessageSize(1, (MessageLite)this.capabilities_.get(i));
/*      */       }
/* 1084 */       size += this.unknownFields.getSerializedSize();
/* 1085 */       this.memoizedSize = size;
/* 1086 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1091 */       if (obj == this) {
/* 1092 */         return true;
/*      */       }
/* 1094 */       if (!(obj instanceof Capabilities)) {
/* 1095 */         return super.equals(obj);
/*      */       }
/* 1097 */       Capabilities other = (Capabilities)obj;
/*      */ 
/*      */       
/* 1100 */       if (!getCapabilitiesList().equals(other.getCapabilitiesList())) return false; 
/* 1101 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 1102 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1107 */       if (this.memoizedHashCode != 0) {
/* 1108 */         return this.memoizedHashCode;
/*      */       }
/* 1110 */       int hash = 41;
/* 1111 */       hash = 19 * hash + getDescriptor().hashCode();
/* 1112 */       if (getCapabilitiesCount() > 0) {
/* 1113 */         hash = 37 * hash + 1;
/* 1114 */         hash = 53 * hash + getCapabilitiesList().hashCode();
/*      */       } 
/* 1116 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 1117 */       this.memoizedHashCode = hash;
/* 1118 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Capabilities parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 1124 */       return (Capabilities)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Capabilities parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1130 */       return (Capabilities)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Capabilities parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 1135 */       return (Capabilities)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Capabilities parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1141 */       return (Capabilities)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Capabilities parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 1145 */       return (Capabilities)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Capabilities parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1151 */       return (Capabilities)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Capabilities parseFrom(InputStream input) throws IOException {
/* 1155 */       return 
/* 1156 */         (Capabilities)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Capabilities parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1162 */       return 
/* 1163 */         (Capabilities)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Capabilities parseDelimitedFrom(InputStream input) throws IOException {
/* 1167 */       return 
/* 1168 */         (Capabilities)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Capabilities parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1174 */       return 
/* 1175 */         (Capabilities)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Capabilities parseFrom(CodedInputStream input) throws IOException {
/* 1180 */       return 
/* 1181 */         (Capabilities)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Capabilities parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1187 */       return 
/* 1188 */         (Capabilities)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 1192 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 1194 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Capabilities prototype) {
/* 1197 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 1201 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 1202 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 1208 */       Builder builder = new Builder(parent);
/* 1209 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxConnection.CapabilitiesOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */       
/*      */       private List<MysqlxConnection.Capability> capabilities_;
/*      */       
/*      */       private RepeatedFieldBuilderV3<MysqlxConnection.Capability, MysqlxConnection.Capability.Builder, MysqlxConnection.CapabilityOrBuilder> capabilitiesBuilder_;
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 1226 */         return MysqlxConnection.internal_static_Mysqlx_Connection_Capabilities_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 1232 */         return MysqlxConnection.internal_static_Mysqlx_Connection_Capabilities_fieldAccessorTable
/* 1233 */           .ensureFieldAccessorsInitialized(MysqlxConnection.Capabilities.class, Builder.class);
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
/* 1407 */         this
/* 1408 */           .capabilities_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.capabilities_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxConnection.Capabilities.alwaysUseFieldBuilders) getCapabilitiesFieldBuilder();  } public Builder clear() { super.clear(); if (this.capabilitiesBuilder_ == null) { this.capabilities_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; } else { this.capabilitiesBuilder_.clear(); }  return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxConnection.internal_static_Mysqlx_Connection_Capabilities_descriptor; } public MysqlxConnection.Capabilities getDefaultInstanceForType() { return MysqlxConnection.Capabilities.getDefaultInstance(); } public MysqlxConnection.Capabilities build() { MysqlxConnection.Capabilities result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxConnection.Capabilities buildPartial() { MysqlxConnection.Capabilities result = new MysqlxConnection.Capabilities(this); int from_bitField0_ = this.bitField0_; if (this.capabilitiesBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0) { this.capabilities_ = Collections.unmodifiableList(this.capabilities_); this.bitField0_ &= 0xFFFFFFFE; }  result.capabilities_ = this.capabilities_; } else { result.capabilities_ = this.capabilitiesBuilder_.build(); }  onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); }
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); }
/* 1410 */       private void ensureCapabilitiesIsMutable() { if ((this.bitField0_ & 0x1) == 0)
/* 1411 */         { this.capabilities_ = new ArrayList<>(this.capabilities_);
/* 1412 */           this.bitField0_ |= 0x1; }  } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); }
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); }
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); }
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); }
/*      */       public Builder mergeFrom(Message other) { if (other instanceof MysqlxConnection.Capabilities) return mergeFrom((MysqlxConnection.Capabilities)other);  super.mergeFrom(other); return this; }
/*      */       public Builder mergeFrom(MysqlxConnection.Capabilities other) { if (other == MysqlxConnection.Capabilities.getDefaultInstance())
/*      */           return this;  if (this.capabilitiesBuilder_ == null) { if (!other.capabilities_.isEmpty()) { if (this.capabilities_.isEmpty()) { this.capabilities_ = other.capabilities_; this.bitField0_ &= 0xFFFFFFFE; } else { ensureCapabilitiesIsMutable(); this.capabilities_.addAll(other.capabilities_); }  onChanged(); }  } else if (!other.capabilities_.isEmpty()) { if (this.capabilitiesBuilder_.isEmpty()) { this.capabilitiesBuilder_.dispose(); this.capabilitiesBuilder_ = null; this.capabilities_ = other.capabilities_; this.bitField0_ &= 0xFFFFFFFE; this.capabilitiesBuilder_ = MysqlxConnection.Capabilities.alwaysUseFieldBuilders ? getCapabilitiesFieldBuilder() : null; } else { this.capabilitiesBuilder_.addAllMessages(other.capabilities_); }  }  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */       public final boolean isInitialized() { for (int i = 0; i < getCapabilitiesCount(); i++) { if (!getCapabilities(i).isInitialized())
/*      */             return false;  }  return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxConnection.Capabilities parsedMessage = null; try { parsedMessage = (MysqlxConnection.Capabilities)MysqlxConnection.Capabilities.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxConnection.Capabilities)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null)
/*      */             mergeFrom(parsedMessage);  }  return this; }
/* 1423 */       public List<MysqlxConnection.Capability> getCapabilitiesList() { if (this.capabilitiesBuilder_ == null) {
/* 1424 */           return Collections.unmodifiableList(this.capabilities_);
/*      */         }
/* 1426 */         return this.capabilitiesBuilder_.getMessageList(); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int getCapabilitiesCount() {
/* 1433 */         if (this.capabilitiesBuilder_ == null) {
/* 1434 */           return this.capabilities_.size();
/*      */         }
/* 1436 */         return this.capabilitiesBuilder_.getCount();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxConnection.Capability getCapabilities(int index) {
/* 1443 */         if (this.capabilitiesBuilder_ == null) {
/* 1444 */           return this.capabilities_.get(index);
/*      */         }
/* 1446 */         return (MysqlxConnection.Capability)this.capabilitiesBuilder_.getMessage(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setCapabilities(int index, MysqlxConnection.Capability value) {
/* 1454 */         if (this.capabilitiesBuilder_ == null) {
/* 1455 */           if (value == null) {
/* 1456 */             throw new NullPointerException();
/*      */           }
/* 1458 */           ensureCapabilitiesIsMutable();
/* 1459 */           this.capabilities_.set(index, value);
/* 1460 */           onChanged();
/*      */         } else {
/* 1462 */           this.capabilitiesBuilder_.setMessage(index, (AbstractMessage)value);
/*      */         } 
/* 1464 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setCapabilities(int index, MysqlxConnection.Capability.Builder builderForValue) {
/* 1471 */         if (this.capabilitiesBuilder_ == null) {
/* 1472 */           ensureCapabilitiesIsMutable();
/* 1473 */           this.capabilities_.set(index, builderForValue.build());
/* 1474 */           onChanged();
/*      */         } else {
/* 1476 */           this.capabilitiesBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 1478 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addCapabilities(MysqlxConnection.Capability value) {
/* 1484 */         if (this.capabilitiesBuilder_ == null) {
/* 1485 */           if (value == null) {
/* 1486 */             throw new NullPointerException();
/*      */           }
/* 1488 */           ensureCapabilitiesIsMutable();
/* 1489 */           this.capabilities_.add(value);
/* 1490 */           onChanged();
/*      */         } else {
/* 1492 */           this.capabilitiesBuilder_.addMessage((AbstractMessage)value);
/*      */         } 
/* 1494 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addCapabilities(int index, MysqlxConnection.Capability value) {
/* 1501 */         if (this.capabilitiesBuilder_ == null) {
/* 1502 */           if (value == null) {
/* 1503 */             throw new NullPointerException();
/*      */           }
/* 1505 */           ensureCapabilitiesIsMutable();
/* 1506 */           this.capabilities_.add(index, value);
/* 1507 */           onChanged();
/*      */         } else {
/* 1509 */           this.capabilitiesBuilder_.addMessage(index, (AbstractMessage)value);
/*      */         } 
/* 1511 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addCapabilities(MysqlxConnection.Capability.Builder builderForValue) {
/* 1518 */         if (this.capabilitiesBuilder_ == null) {
/* 1519 */           ensureCapabilitiesIsMutable();
/* 1520 */           this.capabilities_.add(builderForValue.build());
/* 1521 */           onChanged();
/*      */         } else {
/* 1523 */           this.capabilitiesBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 1525 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addCapabilities(int index, MysqlxConnection.Capability.Builder builderForValue) {
/* 1532 */         if (this.capabilitiesBuilder_ == null) {
/* 1533 */           ensureCapabilitiesIsMutable();
/* 1534 */           this.capabilities_.add(index, builderForValue.build());
/* 1535 */           onChanged();
/*      */         } else {
/* 1537 */           this.capabilitiesBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 1539 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addAllCapabilities(Iterable<? extends MysqlxConnection.Capability> values) {
/* 1546 */         if (this.capabilitiesBuilder_ == null) {
/* 1547 */           ensureCapabilitiesIsMutable();
/* 1548 */           AbstractMessageLite.Builder.addAll(values, this.capabilities_);
/*      */           
/* 1550 */           onChanged();
/*      */         } else {
/* 1552 */           this.capabilitiesBuilder_.addAllMessages(values);
/*      */         } 
/* 1554 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearCapabilities() {
/* 1560 */         if (this.capabilitiesBuilder_ == null) {
/* 1561 */           this.capabilities_ = Collections.emptyList();
/* 1562 */           this.bitField0_ &= 0xFFFFFFFE;
/* 1563 */           onChanged();
/*      */         } else {
/* 1565 */           this.capabilitiesBuilder_.clear();
/*      */         } 
/* 1567 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder removeCapabilities(int index) {
/* 1573 */         if (this.capabilitiesBuilder_ == null) {
/* 1574 */           ensureCapabilitiesIsMutable();
/* 1575 */           this.capabilities_.remove(index);
/* 1576 */           onChanged();
/*      */         } else {
/* 1578 */           this.capabilitiesBuilder_.remove(index);
/*      */         } 
/* 1580 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxConnection.Capability.Builder getCapabilitiesBuilder(int index) {
/* 1587 */         return (MysqlxConnection.Capability.Builder)getCapabilitiesFieldBuilder().getBuilder(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxConnection.CapabilityOrBuilder getCapabilitiesOrBuilder(int index) {
/* 1594 */         if (this.capabilitiesBuilder_ == null)
/* 1595 */           return this.capabilities_.get(index); 
/* 1596 */         return (MysqlxConnection.CapabilityOrBuilder)this.capabilitiesBuilder_.getMessageOrBuilder(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<? extends MysqlxConnection.CapabilityOrBuilder> getCapabilitiesOrBuilderList() {
/* 1604 */         if (this.capabilitiesBuilder_ != null) {
/* 1605 */           return this.capabilitiesBuilder_.getMessageOrBuilderList();
/*      */         }
/* 1607 */         return Collections.unmodifiableList((List)this.capabilities_);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxConnection.Capability.Builder addCapabilitiesBuilder() {
/* 1614 */         return (MysqlxConnection.Capability.Builder)getCapabilitiesFieldBuilder().addBuilder(
/* 1615 */             (AbstractMessage)MysqlxConnection.Capability.getDefaultInstance());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxConnection.Capability.Builder addCapabilitiesBuilder(int index) {
/* 1622 */         return (MysqlxConnection.Capability.Builder)getCapabilitiesFieldBuilder().addBuilder(index, 
/* 1623 */             (AbstractMessage)MysqlxConnection.Capability.getDefaultInstance());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<MysqlxConnection.Capability.Builder> getCapabilitiesBuilderList() {
/* 1630 */         return getCapabilitiesFieldBuilder().getBuilderList();
/*      */       }
/*      */ 
/*      */       
/*      */       private RepeatedFieldBuilderV3<MysqlxConnection.Capability, MysqlxConnection.Capability.Builder, MysqlxConnection.CapabilityOrBuilder> getCapabilitiesFieldBuilder() {
/* 1635 */         if (this.capabilitiesBuilder_ == null) {
/* 1636 */           this
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1641 */             .capabilitiesBuilder_ = new RepeatedFieldBuilderV3(this.capabilities_, ((this.bitField0_ & 0x1) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 1642 */           this.capabilities_ = null;
/*      */         } 
/* 1644 */         return this.capabilitiesBuilder_;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 1649 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 1655 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1665 */     private static final Capabilities DEFAULT_INSTANCE = new Capabilities();
/*      */ 
/*      */     
/*      */     public static Capabilities getDefaultInstance() {
/* 1669 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 1673 */     public static final Parser<Capabilities> PARSER = (Parser<Capabilities>)new AbstractParser<Capabilities>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxConnection.Capabilities parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 1679 */           return new MysqlxConnection.Capabilities(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Capabilities> parser() {
/* 1684 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Capabilities> getParserForType() {
/* 1689 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Capabilities getDefaultInstanceForType() {
/* 1694 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface CapabilitiesGetOrBuilder
/*      */     extends MessageOrBuilder {}
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class CapabilitiesGet
/*      */     extends GeneratedMessageV3
/*      */     implements CapabilitiesGetOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */     
/*      */     private CapabilitiesGet(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 1719 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1786 */       this.memoizedIsInitialized = -1; } private CapabilitiesGet() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new CapabilitiesGet(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private CapabilitiesGet(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/*      */     public static final Descriptors.Descriptor getDescriptor() { return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor; }
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesGet_fieldAccessorTable.ensureFieldAccessorsInitialized(CapabilitiesGet.class, Builder.class); }
/* 1789 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 1790 */       if (isInitialized == 1) return true; 
/* 1791 */       if (isInitialized == 0) return false;
/*      */       
/* 1793 */       this.memoizedIsInitialized = 1;
/* 1794 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 1800 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 1805 */       int size = this.memoizedSize;
/* 1806 */       if (size != -1) return size;
/*      */       
/* 1808 */       size = 0;
/* 1809 */       size += this.unknownFields.getSerializedSize();
/* 1810 */       this.memoizedSize = size;
/* 1811 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1816 */       if (obj == this) {
/* 1817 */         return true;
/*      */       }
/* 1819 */       if (!(obj instanceof CapabilitiesGet)) {
/* 1820 */         return super.equals(obj);
/*      */       }
/* 1822 */       CapabilitiesGet other = (CapabilitiesGet)obj;
/*      */       
/* 1824 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 1825 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1830 */       if (this.memoizedHashCode != 0) {
/* 1831 */         return this.memoizedHashCode;
/*      */       }
/* 1833 */       int hash = 41;
/* 1834 */       hash = 19 * hash + getDescriptor().hashCode();
/* 1835 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 1836 */       this.memoizedHashCode = hash;
/* 1837 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CapabilitiesGet parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 1843 */       return (CapabilitiesGet)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CapabilitiesGet parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1849 */       return (CapabilitiesGet)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static CapabilitiesGet parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 1854 */       return (CapabilitiesGet)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CapabilitiesGet parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1860 */       return (CapabilitiesGet)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static CapabilitiesGet parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 1864 */       return (CapabilitiesGet)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CapabilitiesGet parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1870 */       return (CapabilitiesGet)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static CapabilitiesGet parseFrom(InputStream input) throws IOException {
/* 1874 */       return 
/* 1875 */         (CapabilitiesGet)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CapabilitiesGet parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1881 */       return 
/* 1882 */         (CapabilitiesGet)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static CapabilitiesGet parseDelimitedFrom(InputStream input) throws IOException {
/* 1886 */       return 
/* 1887 */         (CapabilitiesGet)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CapabilitiesGet parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1893 */       return 
/* 1894 */         (CapabilitiesGet)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static CapabilitiesGet parseFrom(CodedInputStream input) throws IOException {
/* 1899 */       return 
/* 1900 */         (CapabilitiesGet)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CapabilitiesGet parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1906 */       return 
/* 1907 */         (CapabilitiesGet)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 1911 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 1913 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(CapabilitiesGet prototype) {
/* 1916 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 1920 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 1921 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 1927 */       Builder builder = new Builder(parent);
/* 1928 */       return builder;
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
/*      */       implements MysqlxConnection.CapabilitiesGetOrBuilder
/*      */     {
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 1945 */         return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 1951 */         return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesGet_fieldAccessorTable
/* 1952 */           .ensureFieldAccessorsInitialized(MysqlxConnection.CapabilitiesGet.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 1958 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 1963 */         super(parent);
/* 1964 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 1968 */         if (MysqlxConnection.CapabilitiesGet.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/* 1973 */         super.clear();
/* 1974 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 1980 */         return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxConnection.CapabilitiesGet getDefaultInstanceForType() {
/* 1985 */         return MysqlxConnection.CapabilitiesGet.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxConnection.CapabilitiesGet build() {
/* 1990 */         MysqlxConnection.CapabilitiesGet result = buildPartial();
/* 1991 */         if (!result.isInitialized()) {
/* 1992 */           throw newUninitializedMessageException(result);
/*      */         }
/* 1994 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxConnection.CapabilitiesGet buildPartial() {
/* 1999 */         MysqlxConnection.CapabilitiesGet result = new MysqlxConnection.CapabilitiesGet(this);
/* 2000 */         onBuilt();
/* 2001 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 2006 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 2012 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 2017 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 2022 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 2028 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 2034 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 2038 */         if (other instanceof MysqlxConnection.CapabilitiesGet) {
/* 2039 */           return mergeFrom((MysqlxConnection.CapabilitiesGet)other);
/*      */         }
/* 2041 */         super.mergeFrom(other);
/* 2042 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxConnection.CapabilitiesGet other) {
/* 2047 */         if (other == MysqlxConnection.CapabilitiesGet.getDefaultInstance()) return this; 
/* 2048 */         mergeUnknownFields(other.unknownFields);
/* 2049 */         onChanged();
/* 2050 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 2055 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2063 */         MysqlxConnection.CapabilitiesGet parsedMessage = null;
/*      */         try {
/* 2065 */           parsedMessage = (MysqlxConnection.CapabilitiesGet)MysqlxConnection.CapabilitiesGet.PARSER.parsePartialFrom(input, extensionRegistry);
/* 2066 */         } catch (InvalidProtocolBufferException e) {
/* 2067 */           parsedMessage = (MysqlxConnection.CapabilitiesGet)e.getUnfinishedMessage();
/* 2068 */           throw e.unwrapIOException();
/*      */         } finally {
/* 2070 */           if (parsedMessage != null) {
/* 2071 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 2074 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 2079 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 2085 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2095 */     private static final CapabilitiesGet DEFAULT_INSTANCE = new CapabilitiesGet();
/*      */ 
/*      */     
/*      */     public static CapabilitiesGet getDefaultInstance() {
/* 2099 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 2103 */     public static final Parser<CapabilitiesGet> PARSER = (Parser<CapabilitiesGet>)new AbstractParser<CapabilitiesGet>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxConnection.CapabilitiesGet parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 2109 */           return new MysqlxConnection.CapabilitiesGet(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<CapabilitiesGet> parser() {
/* 2114 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<CapabilitiesGet> getParserForType() {
/* 2119 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public CapabilitiesGet getDefaultInstanceForType() {
/* 2124 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface CapabilitiesSetOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasCapabilities();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxConnection.Capabilities getCapabilities();
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxConnection.CapabilitiesOrBuilder getCapabilitiesOrBuilder();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class CapabilitiesSet
/*      */     extends GeneratedMessageV3
/*      */     implements CapabilitiesSetOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */     
/*      */     public static final int CAPABILITIES_FIELD_NUMBER = 1;
/*      */ 
/*      */     
/*      */     private MysqlxConnection.Capabilities capabilities_;
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */     
/*      */     private CapabilitiesSet(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 2168 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2273 */       this.memoizedIsInitialized = -1; } private CapabilitiesSet() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new CapabilitiesSet(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private CapabilitiesSet(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { MysqlxConnection.Capabilities.Builder subBuilder; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: subBuilder = null; if ((this.bitField0_ & 0x1) != 0) subBuilder = this.capabilities_.toBuilder();  this.capabilities_ = (MysqlxConnection.Capabilities)input.readMessage(MysqlxConnection.Capabilities.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.capabilities_); this.capabilities_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x1; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesSet_fieldAccessorTable.ensureFieldAccessorsInitialized(CapabilitiesSet.class, Builder.class); } public boolean hasCapabilities() { return ((this.bitField0_ & 0x1) != 0); }
/*      */     public MysqlxConnection.Capabilities getCapabilities() { return (this.capabilities_ == null) ? MysqlxConnection.Capabilities.getDefaultInstance() : this.capabilities_; }
/*      */     public MysqlxConnection.CapabilitiesOrBuilder getCapabilitiesOrBuilder() { return (this.capabilities_ == null) ? MysqlxConnection.Capabilities.getDefaultInstance() : this.capabilities_; }
/* 2276 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 2277 */       if (isInitialized == 1) return true; 
/* 2278 */       if (isInitialized == 0) return false;
/*      */       
/* 2280 */       if (!hasCapabilities()) {
/* 2281 */         this.memoizedIsInitialized = 0;
/* 2282 */         return false;
/*      */       } 
/* 2284 */       if (!getCapabilities().isInitialized()) {
/* 2285 */         this.memoizedIsInitialized = 0;
/* 2286 */         return false;
/*      */       } 
/* 2288 */       this.memoizedIsInitialized = 1;
/* 2289 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 2295 */       if ((this.bitField0_ & 0x1) != 0) {
/* 2296 */         output.writeMessage(1, (MessageLite)getCapabilities());
/*      */       }
/* 2298 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 2303 */       int size = this.memoizedSize;
/* 2304 */       if (size != -1) return size;
/*      */       
/* 2306 */       size = 0;
/* 2307 */       if ((this.bitField0_ & 0x1) != 0) {
/* 2308 */         size += 
/* 2309 */           CodedOutputStream.computeMessageSize(1, (MessageLite)getCapabilities());
/*      */       }
/* 2311 */       size += this.unknownFields.getSerializedSize();
/* 2312 */       this.memoizedSize = size;
/* 2313 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 2318 */       if (obj == this) {
/* 2319 */         return true;
/*      */       }
/* 2321 */       if (!(obj instanceof CapabilitiesSet)) {
/* 2322 */         return super.equals(obj);
/*      */       }
/* 2324 */       CapabilitiesSet other = (CapabilitiesSet)obj;
/*      */       
/* 2326 */       if (hasCapabilities() != other.hasCapabilities()) return false; 
/* 2327 */       if (hasCapabilities() && 
/*      */         
/* 2329 */         !getCapabilities().equals(other.getCapabilities())) return false;
/*      */       
/* 2331 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 2332 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 2337 */       if (this.memoizedHashCode != 0) {
/* 2338 */         return this.memoizedHashCode;
/*      */       }
/* 2340 */       int hash = 41;
/* 2341 */       hash = 19 * hash + getDescriptor().hashCode();
/* 2342 */       if (hasCapabilities()) {
/* 2343 */         hash = 37 * hash + 1;
/* 2344 */         hash = 53 * hash + getCapabilities().hashCode();
/*      */       } 
/* 2346 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 2347 */       this.memoizedHashCode = hash;
/* 2348 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CapabilitiesSet parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 2354 */       return (CapabilitiesSet)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CapabilitiesSet parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2360 */       return (CapabilitiesSet)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static CapabilitiesSet parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 2365 */       return (CapabilitiesSet)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CapabilitiesSet parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2371 */       return (CapabilitiesSet)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static CapabilitiesSet parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 2375 */       return (CapabilitiesSet)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CapabilitiesSet parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2381 */       return (CapabilitiesSet)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static CapabilitiesSet parseFrom(InputStream input) throws IOException {
/* 2385 */       return 
/* 2386 */         (CapabilitiesSet)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CapabilitiesSet parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2392 */       return 
/* 2393 */         (CapabilitiesSet)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static CapabilitiesSet parseDelimitedFrom(InputStream input) throws IOException {
/* 2397 */       return 
/* 2398 */         (CapabilitiesSet)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CapabilitiesSet parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2404 */       return 
/* 2405 */         (CapabilitiesSet)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static CapabilitiesSet parseFrom(CodedInputStream input) throws IOException {
/* 2410 */       return 
/* 2411 */         (CapabilitiesSet)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CapabilitiesSet parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2417 */       return 
/* 2418 */         (CapabilitiesSet)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 2422 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 2424 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(CapabilitiesSet prototype) {
/* 2427 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 2431 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 2432 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 2438 */       Builder builder = new Builder(parent);
/* 2439 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxConnection.CapabilitiesSetOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */       
/*      */       private MysqlxConnection.Capabilities capabilities_;
/*      */ 
/*      */       
/*      */       private SingleFieldBuilderV3<MysqlxConnection.Capabilities, MysqlxConnection.Capabilities.Builder, MysqlxConnection.CapabilitiesOrBuilder> capabilitiesBuilder_;
/*      */ 
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 2460 */         return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 2466 */         return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesSet_fieldAccessorTable
/* 2467 */           .ensureFieldAccessorsInitialized(MysqlxConnection.CapabilitiesSet.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 2473 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 2478 */         super(parent);
/* 2479 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 2483 */         if (MysqlxConnection.CapabilitiesSet.alwaysUseFieldBuilders) {
/* 2484 */           getCapabilitiesFieldBuilder();
/*      */         }
/*      */       }
/*      */       
/*      */       public Builder clear() {
/* 2489 */         super.clear();
/* 2490 */         if (this.capabilitiesBuilder_ == null) {
/* 2491 */           this.capabilities_ = null;
/*      */         } else {
/* 2493 */           this.capabilitiesBuilder_.clear();
/*      */         } 
/* 2495 */         this.bitField0_ &= 0xFFFFFFFE;
/* 2496 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 2502 */         return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxConnection.CapabilitiesSet getDefaultInstanceForType() {
/* 2507 */         return MysqlxConnection.CapabilitiesSet.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxConnection.CapabilitiesSet build() {
/* 2512 */         MysqlxConnection.CapabilitiesSet result = buildPartial();
/* 2513 */         if (!result.isInitialized()) {
/* 2514 */           throw newUninitializedMessageException(result);
/*      */         }
/* 2516 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxConnection.CapabilitiesSet buildPartial() {
/* 2521 */         MysqlxConnection.CapabilitiesSet result = new MysqlxConnection.CapabilitiesSet(this);
/* 2522 */         int from_bitField0_ = this.bitField0_;
/* 2523 */         int to_bitField0_ = 0;
/* 2524 */         if ((from_bitField0_ & 0x1) != 0) {
/* 2525 */           if (this.capabilitiesBuilder_ == null) {
/* 2526 */             result.capabilities_ = this.capabilities_;
/*      */           } else {
/* 2528 */             result.capabilities_ = (MysqlxConnection.Capabilities)this.capabilitiesBuilder_.build();
/*      */           } 
/* 2530 */           to_bitField0_ |= 0x1;
/*      */         } 
/* 2532 */         result.bitField0_ = to_bitField0_;
/* 2533 */         onBuilt();
/* 2534 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 2539 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 2545 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 2550 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 2555 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 2561 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 2567 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 2571 */         if (other instanceof MysqlxConnection.CapabilitiesSet) {
/* 2572 */           return mergeFrom((MysqlxConnection.CapabilitiesSet)other);
/*      */         }
/* 2574 */         super.mergeFrom(other);
/* 2575 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxConnection.CapabilitiesSet other) {
/* 2580 */         if (other == MysqlxConnection.CapabilitiesSet.getDefaultInstance()) return this; 
/* 2581 */         if (other.hasCapabilities()) {
/* 2582 */           mergeCapabilities(other.getCapabilities());
/*      */         }
/* 2584 */         mergeUnknownFields(other.unknownFields);
/* 2585 */         onChanged();
/* 2586 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 2591 */         if (!hasCapabilities()) {
/* 2592 */           return false;
/*      */         }
/* 2594 */         if (!getCapabilities().isInitialized()) {
/* 2595 */           return false;
/*      */         }
/* 2597 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2605 */         MysqlxConnection.CapabilitiesSet parsedMessage = null;
/*      */         try {
/* 2607 */           parsedMessage = (MysqlxConnection.CapabilitiesSet)MysqlxConnection.CapabilitiesSet.PARSER.parsePartialFrom(input, extensionRegistry);
/* 2608 */         } catch (InvalidProtocolBufferException e) {
/* 2609 */           parsedMessage = (MysqlxConnection.CapabilitiesSet)e.getUnfinishedMessage();
/* 2610 */           throw e.unwrapIOException();
/*      */         } finally {
/* 2612 */           if (parsedMessage != null) {
/* 2613 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 2616 */         return this;
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
/*      */       public boolean hasCapabilities() {
/* 2628 */         return ((this.bitField0_ & 0x1) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxConnection.Capabilities getCapabilities() {
/* 2635 */         if (this.capabilitiesBuilder_ == null) {
/* 2636 */           return (this.capabilities_ == null) ? MysqlxConnection.Capabilities.getDefaultInstance() : this.capabilities_;
/*      */         }
/* 2638 */         return (MysqlxConnection.Capabilities)this.capabilitiesBuilder_.getMessage();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setCapabilities(MysqlxConnection.Capabilities value) {
/* 2645 */         if (this.capabilitiesBuilder_ == null) {
/* 2646 */           if (value == null) {
/* 2647 */             throw new NullPointerException();
/*      */           }
/* 2649 */           this.capabilities_ = value;
/* 2650 */           onChanged();
/*      */         } else {
/* 2652 */           this.capabilitiesBuilder_.setMessage((AbstractMessage)value);
/*      */         } 
/* 2654 */         this.bitField0_ |= 0x1;
/* 2655 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setCapabilities(MysqlxConnection.Capabilities.Builder builderForValue) {
/* 2662 */         if (this.capabilitiesBuilder_ == null) {
/* 2663 */           this.capabilities_ = builderForValue.build();
/* 2664 */           onChanged();
/*      */         } else {
/* 2666 */           this.capabilitiesBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 2668 */         this.bitField0_ |= 0x1;
/* 2669 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeCapabilities(MysqlxConnection.Capabilities value) {
/* 2675 */         if (this.capabilitiesBuilder_ == null) {
/* 2676 */           if ((this.bitField0_ & 0x1) != 0 && this.capabilities_ != null && this.capabilities_ != 
/*      */             
/* 2678 */             MysqlxConnection.Capabilities.getDefaultInstance()) {
/* 2679 */             this
/* 2680 */               .capabilities_ = MysqlxConnection.Capabilities.newBuilder(this.capabilities_).mergeFrom(value).buildPartial();
/*      */           } else {
/* 2682 */             this.capabilities_ = value;
/*      */           } 
/* 2684 */           onChanged();
/*      */         } else {
/* 2686 */           this.capabilitiesBuilder_.mergeFrom((AbstractMessage)value);
/*      */         } 
/* 2688 */         this.bitField0_ |= 0x1;
/* 2689 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearCapabilities() {
/* 2695 */         if (this.capabilitiesBuilder_ == null) {
/* 2696 */           this.capabilities_ = null;
/* 2697 */           onChanged();
/*      */         } else {
/* 2699 */           this.capabilitiesBuilder_.clear();
/*      */         } 
/* 2701 */         this.bitField0_ &= 0xFFFFFFFE;
/* 2702 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxConnection.Capabilities.Builder getCapabilitiesBuilder() {
/* 2708 */         this.bitField0_ |= 0x1;
/* 2709 */         onChanged();
/* 2710 */         return (MysqlxConnection.Capabilities.Builder)getCapabilitiesFieldBuilder().getBuilder();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxConnection.CapabilitiesOrBuilder getCapabilitiesOrBuilder() {
/* 2716 */         if (this.capabilitiesBuilder_ != null) {
/* 2717 */           return (MysqlxConnection.CapabilitiesOrBuilder)this.capabilitiesBuilder_.getMessageOrBuilder();
/*      */         }
/* 2719 */         return (this.capabilities_ == null) ? 
/* 2720 */           MysqlxConnection.Capabilities.getDefaultInstance() : this.capabilities_;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private SingleFieldBuilderV3<MysqlxConnection.Capabilities, MysqlxConnection.Capabilities.Builder, MysqlxConnection.CapabilitiesOrBuilder> getCapabilitiesFieldBuilder() {
/* 2729 */         if (this.capabilitiesBuilder_ == null) {
/* 2730 */           this
/*      */ 
/*      */ 
/*      */             
/* 2734 */             .capabilitiesBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCapabilities(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 2735 */           this.capabilities_ = null;
/*      */         } 
/* 2737 */         return this.capabilitiesBuilder_;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 2742 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 2748 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2758 */     private static final CapabilitiesSet DEFAULT_INSTANCE = new CapabilitiesSet();
/*      */ 
/*      */     
/*      */     public static CapabilitiesSet getDefaultInstance() {
/* 2762 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 2766 */     public static final Parser<CapabilitiesSet> PARSER = (Parser<CapabilitiesSet>)new AbstractParser<CapabilitiesSet>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxConnection.CapabilitiesSet parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 2772 */           return new MysqlxConnection.CapabilitiesSet(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<CapabilitiesSet> parser() {
/* 2777 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<CapabilitiesSet> getParserForType() {
/* 2782 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public CapabilitiesSet getDefaultInstanceForType() {
/* 2787 */       return DEFAULT_INSTANCE;
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
/* 2813 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2880 */       this.memoizedIsInitialized = -1; } private Close() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Close(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Close(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/*      */     public static final Descriptors.Descriptor getDescriptor() { return MysqlxConnection.internal_static_Mysqlx_Connection_Close_descriptor; }
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxConnection.internal_static_Mysqlx_Connection_Close_fieldAccessorTable.ensureFieldAccessorsInitialized(Close.class, Builder.class); }
/* 2883 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 2884 */       if (isInitialized == 1) return true; 
/* 2885 */       if (isInitialized == 0) return false;
/*      */       
/* 2887 */       this.memoizedIsInitialized = 1;
/* 2888 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 2894 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 2899 */       int size = this.memoizedSize;
/* 2900 */       if (size != -1) return size;
/*      */       
/* 2902 */       size = 0;
/* 2903 */       size += this.unknownFields.getSerializedSize();
/* 2904 */       this.memoizedSize = size;
/* 2905 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 2910 */       if (obj == this) {
/* 2911 */         return true;
/*      */       }
/* 2913 */       if (!(obj instanceof Close)) {
/* 2914 */         return super.equals(obj);
/*      */       }
/* 2916 */       Close other = (Close)obj;
/*      */       
/* 2918 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 2919 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 2924 */       if (this.memoizedHashCode != 0) {
/* 2925 */         return this.memoizedHashCode;
/*      */       }
/* 2927 */       int hash = 41;
/* 2928 */       hash = 19 * hash + getDescriptor().hashCode();
/* 2929 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 2930 */       this.memoizedHashCode = hash;
/* 2931 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 2937 */       return (Close)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2943 */       return (Close)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 2948 */       return (Close)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2954 */       return (Close)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Close parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 2958 */       return (Close)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2964 */       return (Close)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Close parseFrom(InputStream input) throws IOException {
/* 2968 */       return 
/* 2969 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2975 */       return 
/* 2976 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Close parseDelimitedFrom(InputStream input) throws IOException {
/* 2980 */       return 
/* 2981 */         (Close)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2987 */       return 
/* 2988 */         (Close)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Close parseFrom(CodedInputStream input) throws IOException {
/* 2993 */       return 
/* 2994 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Close parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3000 */       return 
/* 3001 */         (Close)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 3005 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 3007 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Close prototype) {
/* 3010 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 3014 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 3015 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 3021 */       Builder builder = new Builder(parent);
/* 3022 */       return builder;
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
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxConnection.CloseOrBuilder
/*      */     {
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 3040 */         return MysqlxConnection.internal_static_Mysqlx_Connection_Close_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 3046 */         return MysqlxConnection.internal_static_Mysqlx_Connection_Close_fieldAccessorTable
/* 3047 */           .ensureFieldAccessorsInitialized(MysqlxConnection.Close.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 3053 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 3058 */         super(parent);
/* 3059 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 3063 */         if (MysqlxConnection.Close.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/* 3068 */         super.clear();
/* 3069 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 3075 */         return MysqlxConnection.internal_static_Mysqlx_Connection_Close_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxConnection.Close getDefaultInstanceForType() {
/* 3080 */         return MysqlxConnection.Close.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxConnection.Close build() {
/* 3085 */         MysqlxConnection.Close result = buildPartial();
/* 3086 */         if (!result.isInitialized()) {
/* 3087 */           throw newUninitializedMessageException(result);
/*      */         }
/* 3089 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxConnection.Close buildPartial() {
/* 3094 */         MysqlxConnection.Close result = new MysqlxConnection.Close(this);
/* 3095 */         onBuilt();
/* 3096 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 3101 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 3107 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 3112 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 3117 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 3123 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 3129 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 3133 */         if (other instanceof MysqlxConnection.Close) {
/* 3134 */           return mergeFrom((MysqlxConnection.Close)other);
/*      */         }
/* 3136 */         super.mergeFrom(other);
/* 3137 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxConnection.Close other) {
/* 3142 */         if (other == MysqlxConnection.Close.getDefaultInstance()) return this; 
/* 3143 */         mergeUnknownFields(other.unknownFields);
/* 3144 */         onChanged();
/* 3145 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 3150 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3158 */         MysqlxConnection.Close parsedMessage = null;
/*      */         try {
/* 3160 */           parsedMessage = (MysqlxConnection.Close)MysqlxConnection.Close.PARSER.parsePartialFrom(input, extensionRegistry);
/* 3161 */         } catch (InvalidProtocolBufferException e) {
/* 3162 */           parsedMessage = (MysqlxConnection.Close)e.getUnfinishedMessage();
/* 3163 */           throw e.unwrapIOException();
/*      */         } finally {
/* 3165 */           if (parsedMessage != null) {
/* 3166 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 3169 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 3174 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 3180 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3190 */     private static final Close DEFAULT_INSTANCE = new Close();
/*      */ 
/*      */     
/*      */     public static Close getDefaultInstance() {
/* 3194 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 3198 */     public static final Parser<Close> PARSER = (Parser<Close>)new AbstractParser<Close>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxConnection.Close parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 3204 */           return new MysqlxConnection.Close(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Close> parser() {
/* 3209 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Close> getParserForType() {
/* 3214 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Close getDefaultInstanceForType() {
/* 3219 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface CompressionOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasUncompressedSize();
/*      */ 
/*      */     
/*      */     long getUncompressedSize();
/*      */ 
/*      */     
/*      */     boolean hasServerMessages();
/*      */ 
/*      */     
/*      */     Mysqlx.ServerMessages.Type getServerMessages();
/*      */ 
/*      */     
/*      */     boolean hasClientMessages();
/*      */ 
/*      */     
/*      */     Mysqlx.ClientMessages.Type getClientMessages();
/*      */ 
/*      */     
/*      */     boolean hasPayload();
/*      */ 
/*      */     
/*      */     ByteString getPayload();
/*      */   }
/*      */ 
/*      */   
/*      */   public static final class Compression
/*      */     extends GeneratedMessageV3
/*      */     implements CompressionOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */     
/*      */     private int bitField0_;
/*      */     
/*      */     public static final int UNCOMPRESSED_SIZE_FIELD_NUMBER = 1;
/*      */     
/*      */     private long uncompressedSize_;
/*      */     
/*      */     public static final int SERVER_MESSAGES_FIELD_NUMBER = 2;
/*      */     
/*      */     private int serverMessages_;
/*      */     
/*      */     public static final int CLIENT_MESSAGES_FIELD_NUMBER = 3;
/*      */     
/*      */     private int clientMessages_;
/*      */     
/*      */     public static final int PAYLOAD_FIELD_NUMBER = 4;
/*      */     
/*      */     private ByteString payload_;
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */     
/*      */     private Compression(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 3282 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3460 */       this.memoizedIsInitialized = -1; } private Compression() { this.memoizedIsInitialized = -1; this.serverMessages_ = 0; this.clientMessages_ = 1; this.payload_ = ByteString.EMPTY; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Compression(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Compression(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int rawValue; Mysqlx.ServerMessages.Type type; Mysqlx.ClientMessages.Type value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.bitField0_ |= 0x1; this.uncompressedSize_ = input.readUInt64(); continue;case 16: rawValue = input.readEnum(); type = Mysqlx.ServerMessages.Type.valueOf(rawValue); if (type == null) { unknownFields.mergeVarintField(2, rawValue); continue; }  this.bitField0_ |= 0x2; this.serverMessages_ = rawValue; continue;case 24: rawValue = input.readEnum(); value = Mysqlx.ClientMessages.Type.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(3, rawValue); continue; }  this.bitField0_ |= 0x4; this.clientMessages_ = rawValue; continue;case 34: this.bitField0_ |= 0x8; this.payload_ = input.readBytes(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxConnection.internal_static_Mysqlx_Connection_Compression_descriptor; }
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxConnection.internal_static_Mysqlx_Connection_Compression_fieldAccessorTable.ensureFieldAccessorsInitialized(Compression.class, Builder.class); }
/*      */     public boolean hasUncompressedSize() { return ((this.bitField0_ & 0x1) != 0); }
/* 3463 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 3464 */       if (isInitialized == 1) return true; 
/* 3465 */       if (isInitialized == 0) return false;
/*      */       
/* 3467 */       if (!hasPayload()) {
/* 3468 */         this.memoizedIsInitialized = 0;
/* 3469 */         return false;
/*      */       } 
/* 3471 */       this.memoizedIsInitialized = 1;
/* 3472 */       return true; } public long getUncompressedSize() { return this.uncompressedSize_; } public boolean hasServerMessages() { return ((this.bitField0_ & 0x2) != 0); }
/*      */     public Mysqlx.ServerMessages.Type getServerMessages() { Mysqlx.ServerMessages.Type result = Mysqlx.ServerMessages.Type.valueOf(this.serverMessages_); return (result == null) ? Mysqlx.ServerMessages.Type.OK : result; }
/*      */     public boolean hasClientMessages() { return ((this.bitField0_ & 0x4) != 0); }
/*      */     public Mysqlx.ClientMessages.Type getClientMessages() { Mysqlx.ClientMessages.Type result = Mysqlx.ClientMessages.Type.valueOf(this.clientMessages_); return (result == null) ? Mysqlx.ClientMessages.Type.CON_CAPABILITIES_GET : result; }
/*      */     public boolean hasPayload() { return ((this.bitField0_ & 0x8) != 0); }
/*      */     public ByteString getPayload() { return this.payload_; }
/* 3478 */     public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) {
/* 3479 */         output.writeUInt64(1, this.uncompressedSize_);
/*      */       }
/* 3481 */       if ((this.bitField0_ & 0x2) != 0) {
/* 3482 */         output.writeEnum(2, this.serverMessages_);
/*      */       }
/* 3484 */       if ((this.bitField0_ & 0x4) != 0) {
/* 3485 */         output.writeEnum(3, this.clientMessages_);
/*      */       }
/* 3487 */       if ((this.bitField0_ & 0x8) != 0) {
/* 3488 */         output.writeBytes(4, this.payload_);
/*      */       }
/* 3490 */       this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 3495 */       int size = this.memoizedSize;
/* 3496 */       if (size != -1) return size;
/*      */       
/* 3498 */       size = 0;
/* 3499 */       if ((this.bitField0_ & 0x1) != 0) {
/* 3500 */         size += 
/* 3501 */           CodedOutputStream.computeUInt64Size(1, this.uncompressedSize_);
/*      */       }
/* 3503 */       if ((this.bitField0_ & 0x2) != 0) {
/* 3504 */         size += 
/* 3505 */           CodedOutputStream.computeEnumSize(2, this.serverMessages_);
/*      */       }
/* 3507 */       if ((this.bitField0_ & 0x4) != 0) {
/* 3508 */         size += 
/* 3509 */           CodedOutputStream.computeEnumSize(3, this.clientMessages_);
/*      */       }
/* 3511 */       if ((this.bitField0_ & 0x8) != 0) {
/* 3512 */         size += 
/* 3513 */           CodedOutputStream.computeBytesSize(4, this.payload_);
/*      */       }
/* 3515 */       size += this.unknownFields.getSerializedSize();
/* 3516 */       this.memoizedSize = size;
/* 3517 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 3522 */       if (obj == this) {
/* 3523 */         return true;
/*      */       }
/* 3525 */       if (!(obj instanceof Compression)) {
/* 3526 */         return super.equals(obj);
/*      */       }
/* 3528 */       Compression other = (Compression)obj;
/*      */       
/* 3530 */       if (hasUncompressedSize() != other.hasUncompressedSize()) return false; 
/* 3531 */       if (hasUncompressedSize() && 
/* 3532 */         getUncompressedSize() != other
/* 3533 */         .getUncompressedSize()) return false;
/*      */       
/* 3535 */       if (hasServerMessages() != other.hasServerMessages()) return false; 
/* 3536 */       if (hasServerMessages() && 
/* 3537 */         this.serverMessages_ != other.serverMessages_) return false;
/*      */       
/* 3539 */       if (hasClientMessages() != other.hasClientMessages()) return false; 
/* 3540 */       if (hasClientMessages() && 
/* 3541 */         this.clientMessages_ != other.clientMessages_) return false;
/*      */       
/* 3543 */       if (hasPayload() != other.hasPayload()) return false; 
/* 3544 */       if (hasPayload() && 
/*      */         
/* 3546 */         !getPayload().equals(other.getPayload())) return false;
/*      */       
/* 3548 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 3549 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 3554 */       if (this.memoizedHashCode != 0) {
/* 3555 */         return this.memoizedHashCode;
/*      */       }
/* 3557 */       int hash = 41;
/* 3558 */       hash = 19 * hash + getDescriptor().hashCode();
/* 3559 */       if (hasUncompressedSize()) {
/* 3560 */         hash = 37 * hash + 1;
/* 3561 */         hash = 53 * hash + Internal.hashLong(
/* 3562 */             getUncompressedSize());
/*      */       } 
/* 3564 */       if (hasServerMessages()) {
/* 3565 */         hash = 37 * hash + 2;
/* 3566 */         hash = 53 * hash + this.serverMessages_;
/*      */       } 
/* 3568 */       if (hasClientMessages()) {
/* 3569 */         hash = 37 * hash + 3;
/* 3570 */         hash = 53 * hash + this.clientMessages_;
/*      */       } 
/* 3572 */       if (hasPayload()) {
/* 3573 */         hash = 37 * hash + 4;
/* 3574 */         hash = 53 * hash + getPayload().hashCode();
/*      */       } 
/* 3576 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 3577 */       this.memoizedHashCode = hash;
/* 3578 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Compression parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 3584 */       return (Compression)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Compression parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 3590 */       return (Compression)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Compression parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 3595 */       return (Compression)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Compression parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 3601 */       return (Compression)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Compression parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 3605 */       return (Compression)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Compression parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 3611 */       return (Compression)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Compression parseFrom(InputStream input) throws IOException {
/* 3615 */       return 
/* 3616 */         (Compression)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Compression parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3622 */       return 
/* 3623 */         (Compression)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Compression parseDelimitedFrom(InputStream input) throws IOException {
/* 3627 */       return 
/* 3628 */         (Compression)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Compression parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3634 */       return 
/* 3635 */         (Compression)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Compression parseFrom(CodedInputStream input) throws IOException {
/* 3640 */       return 
/* 3641 */         (Compression)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Compression parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3647 */       return 
/* 3648 */         (Compression)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 3652 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 3654 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Compression prototype) {
/* 3657 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 3661 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 3662 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 3668 */       Builder builder = new Builder(parent);
/* 3669 */       return builder;
/*      */     }
/*      */     
/*      */     public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxConnection.CompressionOrBuilder {
/*      */       private int bitField0_;
/*      */       private long uncompressedSize_;
/*      */       private int serverMessages_;
/*      */       private int clientMessages_;
/*      */       private ByteString payload_;
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 3680 */         return MysqlxConnection.internal_static_Mysqlx_Connection_Compression_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 3686 */         return MysqlxConnection.internal_static_Mysqlx_Connection_Compression_fieldAccessorTable
/* 3687 */           .ensureFieldAccessorsInitialized(MysqlxConnection.Compression.class, Builder.class);
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
/* 3892 */         this.serverMessages_ = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3934 */         this.clientMessages_ = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3976 */         this.payload_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxConnection.Compression.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.uncompressedSize_ = 0L; this.bitField0_ &= 0xFFFFFFFE; this.serverMessages_ = 0; this.bitField0_ &= 0xFFFFFFFD; this.clientMessages_ = 1; this.bitField0_ &= 0xFFFFFFFB; this.payload_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFF7; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxConnection.internal_static_Mysqlx_Connection_Compression_descriptor; } public MysqlxConnection.Compression getDefaultInstanceForType() { return MysqlxConnection.Compression.getDefaultInstance(); } public MysqlxConnection.Compression build() { MysqlxConnection.Compression result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxConnection.Compression buildPartial() { MysqlxConnection.Compression result = new MysqlxConnection.Compression(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { result.uncompressedSize_ = this.uncompressedSize_; to_bitField0_ |= 0x1; }  if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.serverMessages_ = this.serverMessages_; if ((from_bitField0_ & 0x4) != 0) to_bitField0_ |= 0x4;  result.clientMessages_ = this.clientMessages_; if ((from_bitField0_ & 0x8) != 0) to_bitField0_ |= 0x8;  result.payload_ = this.payload_; result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.serverMessages_ = 0; this.clientMessages_ = 1; this.payload_ = ByteString.EMPTY; maybeForceBuilderInitialization(); }
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); }
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); }
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); }
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); }
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); }
/* 3982 */       public Builder mergeFrom(Message other) { if (other instanceof MysqlxConnection.Compression) return mergeFrom((MysqlxConnection.Compression)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxConnection.Compression other) { if (other == MysqlxConnection.Compression.getDefaultInstance()) return this;  if (other.hasUncompressedSize()) setUncompressedSize(other.getUncompressedSize());  if (other.hasServerMessages()) setServerMessages(other.getServerMessages());  if (other.hasClientMessages()) setClientMessages(other.getClientMessages());  if (other.hasPayload()) setPayload(other.getPayload());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public boolean hasPayload() { return ((this.bitField0_ & 0x8) != 0); } public final boolean isInitialized() { if (!hasPayload()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxConnection.Compression parsedMessage = null; try { parsedMessage = (MysqlxConnection.Compression)MysqlxConnection.Compression.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxConnection.Compression)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasUncompressedSize() { return ((this.bitField0_ & 0x1) != 0); } public long getUncompressedSize() { return this.uncompressedSize_; } public Builder setUncompressedSize(long value) { this.bitField0_ |= 0x1; this.uncompressedSize_ = value; onChanged(); return this; } public Builder clearUncompressedSize() { this.bitField0_ &= 0xFFFFFFFE; this.uncompressedSize_ = 0L; onChanged(); return this; } public boolean hasServerMessages() { return ((this.bitField0_ & 0x2) != 0); } public Mysqlx.ServerMessages.Type getServerMessages() { Mysqlx.ServerMessages.Type result = Mysqlx.ServerMessages.Type.valueOf(this.serverMessages_); return (result == null) ? Mysqlx.ServerMessages.Type.OK : result; }
/*      */       public Builder setServerMessages(Mysqlx.ServerMessages.Type value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.serverMessages_ = value.getNumber(); onChanged(); return this; }
/*      */       public Builder clearServerMessages() { this.bitField0_ &= 0xFFFFFFFD; this.serverMessages_ = 0; onChanged(); return this; }
/*      */       public boolean hasClientMessages() { return ((this.bitField0_ & 0x4) != 0); }
/*      */       public Mysqlx.ClientMessages.Type getClientMessages() { Mysqlx.ClientMessages.Type result = Mysqlx.ClientMessages.Type.valueOf(this.clientMessages_); return (result == null) ? Mysqlx.ClientMessages.Type.CON_CAPABILITIES_GET : result; }
/*      */       public Builder setClientMessages(Mysqlx.ClientMessages.Type value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x4; this.clientMessages_ = value.getNumber(); onChanged(); return this; }
/*      */       public Builder clearClientMessages() { this.bitField0_ &= 0xFFFFFFFB; this.clientMessages_ = 1; onChanged(); return this; }
/* 3989 */       public ByteString getPayload() { return this.payload_; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setPayload(ByteString value) {
/* 3997 */         if (value == null) {
/* 3998 */           throw new NullPointerException();
/*      */         }
/* 4000 */         this.bitField0_ |= 0x8;
/* 4001 */         this.payload_ = value;
/* 4002 */         onChanged();
/* 4003 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearPayload() {
/* 4010 */         this.bitField0_ &= 0xFFFFFFF7;
/* 4011 */         this.payload_ = MysqlxConnection.Compression.getDefaultInstance().getPayload();
/* 4012 */         onChanged();
/* 4013 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 4018 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 4024 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4034 */     private static final Compression DEFAULT_INSTANCE = new Compression();
/*      */ 
/*      */     
/*      */     public static Compression getDefaultInstance() {
/* 4038 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 4042 */     public static final Parser<Compression> PARSER = (Parser<Compression>)new AbstractParser<Compression>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxConnection.Compression parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 4048 */           return new MysqlxConnection.Compression(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Compression> parser() {
/* 4053 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Compression> getParserForType() {
/* 4058 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Compression getDefaultInstanceForType() {
/* 4063 */       return DEFAULT_INSTANCE;
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
/* 4101 */     return descriptor;
/*      */   }
/*      */ 
/*      */   
/*      */   static {
/* 4106 */     String[] descriptorData = { "\n\027mysqlx_connection.proto\022\021Mysqlx.Connection\032\026mysqlx_datatypes.proto\032\fmysqlx.proto\"@\n\nCapability\022\f\n\004name\030\001 \002(\t\022$\n\005value\030\002 \002(\0132\025.Mysqlx.Datatypes.Any\"I\n\fCapabilities\0223\n\fcapabilities\030\001 \003(\0132\035.Mysqlx.Connection.Capability:\004ê0\002\"\027\n\017CapabilitiesGet:\004ê0\001\"N\n\017CapabilitiesSet\0225\n\fcapabilities\030\001 \002(\0132\037.Mysqlx.Connection.Capabilities:\004ê0\002\"\r\n\005Close:\004ê0\003\"¯\001\n\013Compression\022\031\n\021uncompressed_size\030\001 \001(\004\0224\n\017server_messages\030\002 \001(\0162\033.Mysqlx.ServerMessages.Type\0224\n\017client_messages\030\003 \001(\0162\033.Mysqlx.ClientMessages.Type\022\017\n\007payload\030\004 \002(\f:\bê0\023ê0.B\031\n\027com.mysql.cj.x.protobuf" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4123 */     descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] {
/*      */           
/* 4125 */           MysqlxDatatypes.getDescriptor(), 
/* 4126 */           Mysqlx.getDescriptor()
/*      */         });
/*      */     
/* 4129 */     internal_static_Mysqlx_Connection_Capability_descriptor = getDescriptor().getMessageTypes().get(0);
/* 4130 */     internal_static_Mysqlx_Connection_Capability_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Connection_Capability_descriptor, new String[] { "Name", "Value" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4135 */     internal_static_Mysqlx_Connection_Capabilities_descriptor = getDescriptor().getMessageTypes().get(1);
/* 4136 */     internal_static_Mysqlx_Connection_Capabilities_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Connection_Capabilities_descriptor, new String[] { "Capabilities" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4141 */     internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor = getDescriptor().getMessageTypes().get(2);
/* 4142 */     internal_static_Mysqlx_Connection_CapabilitiesGet_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor, new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4147 */     internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor = getDescriptor().getMessageTypes().get(3);
/* 4148 */     internal_static_Mysqlx_Connection_CapabilitiesSet_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor, new String[] { "Capabilities" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4153 */     internal_static_Mysqlx_Connection_Close_descriptor = getDescriptor().getMessageTypes().get(4);
/* 4154 */     internal_static_Mysqlx_Connection_Close_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Connection_Close_descriptor, new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4159 */     internal_static_Mysqlx_Connection_Compression_descriptor = getDescriptor().getMessageTypes().get(5);
/* 4160 */     internal_static_Mysqlx_Connection_Compression_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Connection_Compression_descriptor, new String[] { "UncompressedSize", "ServerMessages", "ClientMessages", "Payload" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4165 */     ExtensionRegistry registry = ExtensionRegistry.newInstance();
/* 4166 */     registry.add(Mysqlx.clientMessageId);
/* 4167 */     registry.add(Mysqlx.serverMessageId);
/*      */     
/* 4169 */     Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
/* 4170 */     MysqlxDatatypes.getDescriptor();
/* 4171 */     Mysqlx.getDescriptor();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\x\protobuf\MysqlxConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */