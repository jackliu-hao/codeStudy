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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class MysqlxSql
/*      */ {
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Sql_StmtExecute_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Sql_StmtExecute_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Sql_StmtExecuteOk_fieldAccessorTable;
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
/*      */   public static interface StmtExecuteOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasNamespace();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String getNamespace();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getNamespaceBytes();
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
/*      */     ByteString getStmt();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     List<MysqlxDatatypes.Any> getArgsList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.Any getArgs(int param1Int);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getArgsCount();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int param1Int);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasCompactMetadata();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean getCompactMetadata();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class StmtExecute
/*      */     extends GeneratedMessageV3
/*      */     implements StmtExecuteOrBuilder
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
/*      */     public static final int NAMESPACE_FIELD_NUMBER = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private volatile Object namespace_;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int STMT_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private ByteString stmt_;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int ARGS_FIELD_NUMBER = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private List<MysqlxDatatypes.Any> args_;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int COMPACT_METADATA_FIELD_NUMBER = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private boolean compactMetadata_;
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private StmtExecute(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/*  187 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  451 */       this.memoizedIsInitialized = -1; } private StmtExecute() { this.memoizedIsInitialized = -1; this.namespace_ = "sql"; this.stmt_ = ByteString.EMPTY; this.args_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new StmtExecute(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private StmtExecute(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: this.bitField0_ |= 0x2; this.stmt_ = input.readBytes(); continue;case 18: if ((mutable_bitField0_ & 0x4) == 0) { this.args_ = new ArrayList<>(); mutable_bitField0_ |= 0x4; }  this.args_.add(input.readMessage(MysqlxDatatypes.Any.PARSER, extensionRegistry)); continue;case 26: bs = input.readBytes(); this.bitField0_ |= 0x1; this.namespace_ = bs; continue;case 32: this.bitField0_ |= 0x4; this.compactMetadata_ = input.readBool(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x4) != 0) this.args_ = Collections.unmodifiableList(this.args_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_fieldAccessorTable.ensureFieldAccessorsInitialized(StmtExecute.class, Builder.class); } public boolean hasNamespace() { return ((this.bitField0_ & 0x1) != 0); }
/*      */     public String getNamespace() { Object ref = this.namespace_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.namespace_ = s;  return s; }
/*      */     public ByteString getNamespaceBytes() { Object ref = this.namespace_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.namespace_ = b; return b; }  return (ByteString)ref; }
/*  454 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  455 */       if (isInitialized == 1) return true; 
/*  456 */       if (isInitialized == 0) return false;
/*      */       
/*  458 */       if (!hasStmt()) {
/*  459 */         this.memoizedIsInitialized = 0;
/*  460 */         return false;
/*      */       } 
/*  462 */       for (int i = 0; i < getArgsCount(); i++) {
/*  463 */         if (!getArgs(i).isInitialized()) {
/*  464 */           this.memoizedIsInitialized = 0;
/*  465 */           return false;
/*      */         } 
/*      */       } 
/*  468 */       this.memoizedIsInitialized = 1;
/*  469 */       return true; } public boolean hasStmt() { return ((this.bitField0_ & 0x2) != 0); } public ByteString getStmt() { return this.stmt_; } public List<MysqlxDatatypes.Any> getArgsList() { return this.args_; } public List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList() { return (List)this.args_; }
/*      */     public int getArgsCount() { return this.args_.size(); }
/*      */     public MysqlxDatatypes.Any getArgs(int index) { return this.args_.get(index); }
/*      */     public MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int index) { return this.args_.get(index); }
/*      */     public boolean hasCompactMetadata() { return ((this.bitField0_ & 0x4) != 0); }
/*      */     public boolean getCompactMetadata() { return this.compactMetadata_; }
/*  475 */     public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x2) != 0) {
/*  476 */         output.writeBytes(1, this.stmt_);
/*      */       }
/*  478 */       for (int i = 0; i < this.args_.size(); i++) {
/*  479 */         output.writeMessage(2, (MessageLite)this.args_.get(i));
/*      */       }
/*  481 */       if ((this.bitField0_ & 0x1) != 0) {
/*  482 */         GeneratedMessageV3.writeString(output, 3, this.namespace_);
/*      */       }
/*  484 */       if ((this.bitField0_ & 0x4) != 0) {
/*  485 */         output.writeBool(4, this.compactMetadata_);
/*      */       }
/*  487 */       this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/*  492 */       int size = this.memoizedSize;
/*  493 */       if (size != -1) return size;
/*      */       
/*  495 */       size = 0;
/*  496 */       if ((this.bitField0_ & 0x2) != 0) {
/*  497 */         size += 
/*  498 */           CodedOutputStream.computeBytesSize(1, this.stmt_);
/*      */       }
/*  500 */       for (int i = 0; i < this.args_.size(); i++) {
/*  501 */         size += 
/*  502 */           CodedOutputStream.computeMessageSize(2, (MessageLite)this.args_.get(i));
/*      */       }
/*  504 */       if ((this.bitField0_ & 0x1) != 0) {
/*  505 */         size += GeneratedMessageV3.computeStringSize(3, this.namespace_);
/*      */       }
/*  507 */       if ((this.bitField0_ & 0x4) != 0) {
/*  508 */         size += 
/*  509 */           CodedOutputStream.computeBoolSize(4, this.compactMetadata_);
/*      */       }
/*  511 */       size += this.unknownFields.getSerializedSize();
/*  512 */       this.memoizedSize = size;
/*  513 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  518 */       if (obj == this) {
/*  519 */         return true;
/*      */       }
/*  521 */       if (!(obj instanceof StmtExecute)) {
/*  522 */         return super.equals(obj);
/*      */       }
/*  524 */       StmtExecute other = (StmtExecute)obj;
/*      */       
/*  526 */       if (hasNamespace() != other.hasNamespace()) return false; 
/*  527 */       if (hasNamespace() && 
/*      */         
/*  529 */         !getNamespace().equals(other.getNamespace())) return false;
/*      */       
/*  531 */       if (hasStmt() != other.hasStmt()) return false; 
/*  532 */       if (hasStmt() && 
/*      */         
/*  534 */         !getStmt().equals(other.getStmt())) return false;
/*      */ 
/*      */       
/*  537 */       if (!getArgsList().equals(other.getArgsList())) return false; 
/*  538 */       if (hasCompactMetadata() != other.hasCompactMetadata()) return false; 
/*  539 */       if (hasCompactMetadata() && 
/*  540 */         getCompactMetadata() != other
/*  541 */         .getCompactMetadata()) return false;
/*      */       
/*  543 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  544 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  549 */       if (this.memoizedHashCode != 0) {
/*  550 */         return this.memoizedHashCode;
/*      */       }
/*  552 */       int hash = 41;
/*  553 */       hash = 19 * hash + getDescriptor().hashCode();
/*  554 */       if (hasNamespace()) {
/*  555 */         hash = 37 * hash + 3;
/*  556 */         hash = 53 * hash + getNamespace().hashCode();
/*      */       } 
/*  558 */       if (hasStmt()) {
/*  559 */         hash = 37 * hash + 1;
/*  560 */         hash = 53 * hash + getStmt().hashCode();
/*      */       } 
/*  562 */       if (getArgsCount() > 0) {
/*  563 */         hash = 37 * hash + 2;
/*  564 */         hash = 53 * hash + getArgsList().hashCode();
/*      */       } 
/*  566 */       if (hasCompactMetadata()) {
/*  567 */         hash = 37 * hash + 4;
/*  568 */         hash = 53 * hash + Internal.hashBoolean(
/*  569 */             getCompactMetadata());
/*      */       } 
/*  571 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  572 */       this.memoizedHashCode = hash;
/*  573 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static StmtExecute parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  579 */       return (StmtExecute)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static StmtExecute parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  585 */       return (StmtExecute)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static StmtExecute parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  590 */       return (StmtExecute)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static StmtExecute parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  596 */       return (StmtExecute)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static StmtExecute parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  600 */       return (StmtExecute)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static StmtExecute parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  606 */       return (StmtExecute)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static StmtExecute parseFrom(InputStream input) throws IOException {
/*  610 */       return 
/*  611 */         (StmtExecute)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static StmtExecute parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  617 */       return 
/*  618 */         (StmtExecute)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static StmtExecute parseDelimitedFrom(InputStream input) throws IOException {
/*  622 */       return 
/*  623 */         (StmtExecute)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static StmtExecute parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  629 */       return 
/*  630 */         (StmtExecute)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static StmtExecute parseFrom(CodedInputStream input) throws IOException {
/*  635 */       return 
/*  636 */         (StmtExecute)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static StmtExecute parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  642 */       return 
/*  643 */         (StmtExecute)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/*  647 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/*  649 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(StmtExecute prototype) {
/*  652 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/*  656 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  657 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  663 */       Builder builder = new Builder(parent);
/*  664 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements MysqlxSql.StmtExecuteOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */ 
/*      */       
/*      */       private Object namespace_;
/*      */       
/*      */       private ByteString stmt_;
/*      */       
/*      */       private List<MysqlxDatatypes.Any> args_;
/*      */       
/*      */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> argsBuilder_;
/*      */       
/*      */       private boolean compactMetadata_;
/*      */ 
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/*  688 */         return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  694 */         return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_fieldAccessorTable
/*  695 */           .ensureFieldAccessorsInitialized(MysqlxSql.StmtExecute.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/*  903 */         this.namespace_ = "sql";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1011 */         this.stmt_ = ByteString.EMPTY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1067 */         this
/* 1068 */           .args_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxSql.StmtExecute.alwaysUseFieldBuilders) getArgsFieldBuilder();  } public Builder clear() { super.clear(); this.namespace_ = "sql"; this.bitField0_ &= 0xFFFFFFFE; this.stmt_ = ByteString.EMPTY; this.bitField0_ &= 0xFFFFFFFD; if (this.argsBuilder_ == null) { this.args_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFB; } else { this.argsBuilder_.clear(); }  this.compactMetadata_ = false; this.bitField0_ &= 0xFFFFFFF7; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_descriptor; } public MysqlxSql.StmtExecute getDefaultInstanceForType() { return MysqlxSql.StmtExecute.getDefaultInstance(); } public MysqlxSql.StmtExecute build() { MysqlxSql.StmtExecute result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxSql.StmtExecute buildPartial() { MysqlxSql.StmtExecute result = new MysqlxSql.StmtExecute(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.namespace_ = this.namespace_; if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.stmt_ = this.stmt_; if (this.argsBuilder_ == null) { if ((this.bitField0_ & 0x4) != 0) { this.args_ = Collections.unmodifiableList(this.args_); this.bitField0_ &= 0xFFFFFFFB; }  result.args_ = this.args_; } else { result.args_ = this.argsBuilder_.build(); }  if ((from_bitField0_ & 0x8) != 0) { result.compactMetadata_ = this.compactMetadata_; to_bitField0_ |= 0x4; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.namespace_ = "sql"; this.stmt_ = ByteString.EMPTY; this.args_ = Collections.emptyList(); maybeForceBuilderInitialization(); }
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); }
/* 1070 */       public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxSql.StmtExecute) return mergeFrom((MysqlxSql.StmtExecute)other);  super.mergeFrom(other); return this; } private void ensureArgsIsMutable() { if ((this.bitField0_ & 0x4) == 0)
/* 1071 */         { this.args_ = new ArrayList<>(this.args_);
/* 1072 */           this.bitField0_ |= 0x4; }  } public Builder mergeFrom(MysqlxSql.StmtExecute other) { if (other == MysqlxSql.StmtExecute.getDefaultInstance())
/*      */           return this;  if (other.hasNamespace()) { this.bitField0_ |= 0x1; this.namespace_ = other.namespace_; onChanged(); }  if (other.hasStmt())
/*      */           setStmt(other.getStmt());  if (this.argsBuilder_ == null) { if (!other.args_.isEmpty()) { if (this.args_.isEmpty()) { this.args_ = other.args_; this.bitField0_ &= 0xFFFFFFFB; } else { ensureArgsIsMutable(); this.args_.addAll(other.args_); }  onChanged(); }  } else if (!other.args_.isEmpty()) { if (this.argsBuilder_.isEmpty()) { this.argsBuilder_.dispose(); this.argsBuilder_ = null; this.args_ = other.args_; this.bitField0_ &= 0xFFFFFFFB; this.argsBuilder_ = MysqlxSql.StmtExecute.alwaysUseFieldBuilders ? getArgsFieldBuilder() : null; } else { this.argsBuilder_.addAllMessages(other.args_); }  }  if (other.hasCompactMetadata())
/*      */           setCompactMetadata(other.getCompactMetadata());  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */       public final boolean isInitialized() { if (!hasStmt())
/*      */           return false;  for (int i = 0; i < getArgsCount(); i++) { if (!getArgs(i).isInitialized())
/*      */             return false;  }  return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxSql.StmtExecute parsedMessage = null; try { parsedMessage = (MysqlxSql.StmtExecute)MysqlxSql.StmtExecute.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxSql.StmtExecute)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null)
/*      */             mergeFrom(parsedMessage);  }  return this; }
/*      */       public boolean hasNamespace() { return ((this.bitField0_ & 0x1) != 0); }
/*      */       public String getNamespace() { Object ref = this.namespace_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8())
/*      */             this.namespace_ = s;  return s; }
/*      */          return (String)ref; }
/*      */       public ByteString getNamespaceBytes() { Object ref = this.namespace_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.namespace_ = b; return b; }
/*      */          return (ByteString)ref; }
/* 1087 */       public List<MysqlxDatatypes.Any> getArgsList() { if (this.argsBuilder_ == null) {
/* 1088 */           return Collections.unmodifiableList(this.args_);
/*      */         }
/* 1090 */         return this.argsBuilder_.getMessageList(); } public Builder setNamespace(String value) { if (value == null)
/*      */           throw new NullPointerException();  this.bitField0_ |= 0x1; this.namespace_ = value; onChanged(); return this; }
/*      */       public Builder clearNamespace() { this.bitField0_ &= 0xFFFFFFFE; this.namespace_ = MysqlxSql.StmtExecute.getDefaultInstance().getNamespace(); onChanged(); return this; }
/*      */       public Builder setNamespaceBytes(ByteString value) { if (value == null)
/*      */           throw new NullPointerException();  this.bitField0_ |= 0x1; this.namespace_ = value; onChanged(); return this; }
/*      */       public boolean hasStmt() { return ((this.bitField0_ & 0x2) != 0); }
/*      */       public ByteString getStmt() { return this.stmt_; }
/*      */       public Builder setStmt(ByteString value) { if (value == null)
/*      */           throw new NullPointerException();  this.bitField0_ |= 0x2; this.stmt_ = value; onChanged(); return this; }
/*      */       public Builder clearStmt() { this.bitField0_ &= 0xFFFFFFFD; this.stmt_ = MysqlxSql.StmtExecute.getDefaultInstance().getStmt(); onChanged();
/*      */         return this; }
/* 1101 */       public int getArgsCount() { if (this.argsBuilder_ == null) {
/* 1102 */           return this.args_.size();
/*      */         }
/* 1104 */         return this.argsBuilder_.getCount(); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public MysqlxDatatypes.Any getArgs(int index) {
/* 1115 */         if (this.argsBuilder_ == null) {
/* 1116 */           return this.args_.get(index);
/*      */         }
/* 1118 */         return (MysqlxDatatypes.Any)this.argsBuilder_.getMessage(index);
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
/* 1130 */         if (this.argsBuilder_ == null) {
/* 1131 */           if (value == null) {
/* 1132 */             throw new NullPointerException();
/*      */           }
/* 1134 */           ensureArgsIsMutable();
/* 1135 */           this.args_.set(index, value);
/* 1136 */           onChanged();
/*      */         } else {
/* 1138 */           this.argsBuilder_.setMessage(index, (AbstractMessage)value);
/*      */         } 
/* 1140 */         return this;
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
/* 1151 */         if (this.argsBuilder_ == null) {
/* 1152 */           ensureArgsIsMutable();
/* 1153 */           this.args_.set(index, builderForValue.build());
/* 1154 */           onChanged();
/*      */         } else {
/* 1156 */           this.argsBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 1158 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addArgs(MysqlxDatatypes.Any value) {
/* 1168 */         if (this.argsBuilder_ == null) {
/* 1169 */           if (value == null) {
/* 1170 */             throw new NullPointerException();
/*      */           }
/* 1172 */           ensureArgsIsMutable();
/* 1173 */           this.args_.add(value);
/* 1174 */           onChanged();
/*      */         } else {
/* 1176 */           this.argsBuilder_.addMessage((AbstractMessage)value);
/*      */         } 
/* 1178 */         return this;
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
/* 1189 */         if (this.argsBuilder_ == null) {
/* 1190 */           if (value == null) {
/* 1191 */             throw new NullPointerException();
/*      */           }
/* 1193 */           ensureArgsIsMutable();
/* 1194 */           this.args_.add(index, value);
/* 1195 */           onChanged();
/*      */         } else {
/* 1197 */           this.argsBuilder_.addMessage(index, (AbstractMessage)value);
/*      */         } 
/* 1199 */         return this;
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
/* 1210 */         if (this.argsBuilder_ == null) {
/* 1211 */           ensureArgsIsMutable();
/* 1212 */           this.args_.add(builderForValue.build());
/* 1213 */           onChanged();
/*      */         } else {
/* 1215 */           this.argsBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 1217 */         return this;
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
/* 1228 */         if (this.argsBuilder_ == null) {
/* 1229 */           ensureArgsIsMutable();
/* 1230 */           this.args_.add(index, builderForValue.build());
/* 1231 */           onChanged();
/*      */         } else {
/* 1233 */           this.argsBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 1235 */         return this;
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
/* 1246 */         if (this.argsBuilder_ == null) {
/* 1247 */           ensureArgsIsMutable();
/* 1248 */           AbstractMessageLite.Builder.addAll(values, this.args_);
/*      */           
/* 1250 */           onChanged();
/*      */         } else {
/* 1252 */           this.argsBuilder_.addAllMessages(values);
/*      */         } 
/* 1254 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearArgs() {
/* 1264 */         if (this.argsBuilder_ == null) {
/* 1265 */           this.args_ = Collections.emptyList();
/* 1266 */           this.bitField0_ &= 0xFFFFFFFB;
/* 1267 */           onChanged();
/*      */         } else {
/* 1269 */           this.argsBuilder_.clear();
/*      */         } 
/* 1271 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder removeArgs(int index) {
/* 1281 */         if (this.argsBuilder_ == null) {
/* 1282 */           ensureArgsIsMutable();
/* 1283 */           this.args_.remove(index);
/* 1284 */           onChanged();
/*      */         } else {
/* 1286 */           this.argsBuilder_.remove(index);
/*      */         } 
/* 1288 */         return this;
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
/* 1299 */         return (MysqlxDatatypes.Any.Builder)getArgsFieldBuilder().getBuilder(index);
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
/* 1310 */         if (this.argsBuilder_ == null)
/* 1311 */           return this.args_.get(index); 
/* 1312 */         return (MysqlxDatatypes.AnyOrBuilder)this.argsBuilder_.getMessageOrBuilder(index);
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
/* 1324 */         if (this.argsBuilder_ != null) {
/* 1325 */           return this.argsBuilder_.getMessageOrBuilderList();
/*      */         }
/* 1327 */         return Collections.unmodifiableList((List)this.args_);
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
/* 1338 */         return (MysqlxDatatypes.Any.Builder)getArgsFieldBuilder().addBuilder(
/* 1339 */             (AbstractMessage)MysqlxDatatypes.Any.getDefaultInstance());
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
/* 1350 */         return (MysqlxDatatypes.Any.Builder)getArgsFieldBuilder().addBuilder(index, 
/* 1351 */             (AbstractMessage)MysqlxDatatypes.Any.getDefaultInstance());
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
/* 1362 */         return getArgsFieldBuilder().getBuilderList();
/*      */       }
/*      */ 
/*      */       
/*      */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> getArgsFieldBuilder() {
/* 1367 */         if (this.argsBuilder_ == null) {
/* 1368 */           this
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1373 */             .argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, ((this.bitField0_ & 0x4) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 1374 */           this.args_ = null;
/*      */         } 
/* 1376 */         return this.argsBuilder_;
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
/* 1390 */         return ((this.bitField0_ & 0x8) != 0);
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
/* 1402 */         return this.compactMetadata_;
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
/* 1415 */         this.bitField0_ |= 0x8;
/* 1416 */         this.compactMetadata_ = value;
/* 1417 */         onChanged();
/* 1418 */         return this;
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
/* 1430 */         this.bitField0_ &= 0xFFFFFFF7;
/* 1431 */         this.compactMetadata_ = false;
/* 1432 */         onChanged();
/* 1433 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 1438 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 1444 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1454 */     private static final StmtExecute DEFAULT_INSTANCE = new StmtExecute();
/*      */ 
/*      */     
/*      */     public static StmtExecute getDefaultInstance() {
/* 1458 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 1462 */     public static final Parser<StmtExecute> PARSER = (Parser<StmtExecute>)new AbstractParser<StmtExecute>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxSql.StmtExecute parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 1468 */           return new MysqlxSql.StmtExecute(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<StmtExecute> parser() {
/* 1473 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<StmtExecute> getParserForType() {
/* 1478 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public StmtExecute getDefaultInstanceForType() {
/* 1483 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface StmtExecuteOkOrBuilder
/*      */     extends MessageOrBuilder {}
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class StmtExecuteOk
/*      */     extends GeneratedMessageV3
/*      */     implements StmtExecuteOkOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */     
/*      */     private StmtExecuteOk(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 1507 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1574 */       this.memoizedIsInitialized = -1; } private StmtExecuteOk() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new StmtExecuteOk(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private StmtExecuteOk(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/*      */     public static final Descriptors.Descriptor getDescriptor() { return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor; }
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_fieldAccessorTable.ensureFieldAccessorsInitialized(StmtExecuteOk.class, Builder.class); }
/* 1577 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 1578 */       if (isInitialized == 1) return true; 
/* 1579 */       if (isInitialized == 0) return false;
/*      */       
/* 1581 */       this.memoizedIsInitialized = 1;
/* 1582 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 1588 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 1593 */       int size = this.memoizedSize;
/* 1594 */       if (size != -1) return size;
/*      */       
/* 1596 */       size = 0;
/* 1597 */       size += this.unknownFields.getSerializedSize();
/* 1598 */       this.memoizedSize = size;
/* 1599 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1604 */       if (obj == this) {
/* 1605 */         return true;
/*      */       }
/* 1607 */       if (!(obj instanceof StmtExecuteOk)) {
/* 1608 */         return super.equals(obj);
/*      */       }
/* 1610 */       StmtExecuteOk other = (StmtExecuteOk)obj;
/*      */       
/* 1612 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 1613 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1618 */       if (this.memoizedHashCode != 0) {
/* 1619 */         return this.memoizedHashCode;
/*      */       }
/* 1621 */       int hash = 41;
/* 1622 */       hash = 19 * hash + getDescriptor().hashCode();
/* 1623 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 1624 */       this.memoizedHashCode = hash;
/* 1625 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static StmtExecuteOk parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 1631 */       return (StmtExecuteOk)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static StmtExecuteOk parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1637 */       return (StmtExecuteOk)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static StmtExecuteOk parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 1642 */       return (StmtExecuteOk)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static StmtExecuteOk parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1648 */       return (StmtExecuteOk)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static StmtExecuteOk parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 1652 */       return (StmtExecuteOk)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static StmtExecuteOk parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1658 */       return (StmtExecuteOk)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static StmtExecuteOk parseFrom(InputStream input) throws IOException {
/* 1662 */       return 
/* 1663 */         (StmtExecuteOk)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static StmtExecuteOk parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1669 */       return 
/* 1670 */         (StmtExecuteOk)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static StmtExecuteOk parseDelimitedFrom(InputStream input) throws IOException {
/* 1674 */       return 
/* 1675 */         (StmtExecuteOk)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static StmtExecuteOk parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1681 */       return 
/* 1682 */         (StmtExecuteOk)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static StmtExecuteOk parseFrom(CodedInputStream input) throws IOException {
/* 1687 */       return 
/* 1688 */         (StmtExecuteOk)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static StmtExecuteOk parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1694 */       return 
/* 1695 */         (StmtExecuteOk)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 1699 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 1701 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(StmtExecuteOk prototype) {
/* 1704 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 1708 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 1709 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 1715 */       Builder builder = new Builder(parent);
/* 1716 */       return builder;
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
/*      */       implements MysqlxSql.StmtExecuteOkOrBuilder
/*      */     {
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 1732 */         return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 1738 */         return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_fieldAccessorTable
/* 1739 */           .ensureFieldAccessorsInitialized(MysqlxSql.StmtExecuteOk.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 1745 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 1750 */         super(parent);
/* 1751 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 1755 */         if (MysqlxSql.StmtExecuteOk.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/* 1760 */         super.clear();
/* 1761 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 1767 */         return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxSql.StmtExecuteOk getDefaultInstanceForType() {
/* 1772 */         return MysqlxSql.StmtExecuteOk.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxSql.StmtExecuteOk build() {
/* 1777 */         MysqlxSql.StmtExecuteOk result = buildPartial();
/* 1778 */         if (!result.isInitialized()) {
/* 1779 */           throw newUninitializedMessageException(result);
/*      */         }
/* 1781 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public MysqlxSql.StmtExecuteOk buildPartial() {
/* 1786 */         MysqlxSql.StmtExecuteOk result = new MysqlxSql.StmtExecuteOk(this);
/* 1787 */         onBuilt();
/* 1788 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 1793 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 1799 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 1804 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 1809 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 1815 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 1821 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 1825 */         if (other instanceof MysqlxSql.StmtExecuteOk) {
/* 1826 */           return mergeFrom((MysqlxSql.StmtExecuteOk)other);
/*      */         }
/* 1828 */         super.mergeFrom(other);
/* 1829 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(MysqlxSql.StmtExecuteOk other) {
/* 1834 */         if (other == MysqlxSql.StmtExecuteOk.getDefaultInstance()) return this; 
/* 1835 */         mergeUnknownFields(other.unknownFields);
/* 1836 */         onChanged();
/* 1837 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 1842 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1850 */         MysqlxSql.StmtExecuteOk parsedMessage = null;
/*      */         try {
/* 1852 */           parsedMessage = (MysqlxSql.StmtExecuteOk)MysqlxSql.StmtExecuteOk.PARSER.parsePartialFrom(input, extensionRegistry);
/* 1853 */         } catch (InvalidProtocolBufferException e) {
/* 1854 */           parsedMessage = (MysqlxSql.StmtExecuteOk)e.getUnfinishedMessage();
/* 1855 */           throw e.unwrapIOException();
/*      */         } finally {
/* 1857 */           if (parsedMessage != null) {
/* 1858 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 1861 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 1866 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 1872 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1882 */     private static final StmtExecuteOk DEFAULT_INSTANCE = new StmtExecuteOk();
/*      */ 
/*      */     
/*      */     public static StmtExecuteOk getDefaultInstance() {
/* 1886 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 1890 */     public static final Parser<StmtExecuteOk> PARSER = (Parser<StmtExecuteOk>)new AbstractParser<StmtExecuteOk>()
/*      */       {
/*      */ 
/*      */         
/*      */         public MysqlxSql.StmtExecuteOk parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 1896 */           return new MysqlxSql.StmtExecuteOk(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<StmtExecuteOk> parser() {
/* 1901 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<StmtExecuteOk> getParserForType() {
/* 1906 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public StmtExecuteOk getDefaultInstanceForType() {
/* 1911 */       return DEFAULT_INSTANCE;
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
/*      */   public static Descriptors.FileDescriptor getDescriptor() {
/* 1929 */     return descriptor;
/*      */   }
/*      */ 
/*      */   
/*      */   static {
/* 1934 */     String[] descriptorData = { "\n\020mysqlx_sql.proto\022\nMysqlx.Sql\032\fmysqlx.proto\032\026mysqlx_datatypes.proto\"\n\013StmtExecute\022\026\n\tnamespace\030\003 \001(\t:\003sql\022\f\n\004stmt\030\001 \002(\f\022#\n\004args\030\002 \003(\0132\025.Mysqlx.Datatypes.Any\022\037\n\020compact_metadata\030\004 \001(\b:\005false:\004ê0\f\"\025\n\rStmtExecuteOk:\004ê0\021B\031\n\027com.mysql.cj.x.protobuf" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1944 */     descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] {
/*      */           
/* 1946 */           Mysqlx.getDescriptor(), 
/* 1947 */           MysqlxDatatypes.getDescriptor()
/*      */         });
/*      */     
/* 1950 */     internal_static_Mysqlx_Sql_StmtExecute_descriptor = getDescriptor().getMessageTypes().get(0);
/* 1951 */     internal_static_Mysqlx_Sql_StmtExecute_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Sql_StmtExecute_descriptor, new String[] { "Namespace", "Stmt", "Args", "CompactMetadata" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1956 */     internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor = getDescriptor().getMessageTypes().get(1);
/* 1957 */     internal_static_Mysqlx_Sql_StmtExecuteOk_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor, new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1962 */     ExtensionRegistry registry = ExtensionRegistry.newInstance();
/* 1963 */     registry.add(Mysqlx.clientMessageId);
/* 1964 */     registry.add(Mysqlx.serverMessageId);
/*      */     
/* 1966 */     Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
/* 1967 */     Mysqlx.getDescriptor();
/* 1968 */     MysqlxDatatypes.getDescriptor();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\x\protobuf\MysqlxSql.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */