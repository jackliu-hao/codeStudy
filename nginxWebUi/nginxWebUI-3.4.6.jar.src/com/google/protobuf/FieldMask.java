/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class FieldMask
/*     */   extends GeneratedMessageV3
/*     */   implements FieldMaskOrBuilder
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   public static final int PATHS_FIELD_NUMBER = 1;
/*     */   private LazyStringList paths_;
/*     */   private byte memoizedIsInitialized;
/*     */   
/*     */   private FieldMask(GeneratedMessageV3.Builder<?> builder) {
/* 170 */     super(builder);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 302 */     this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new FieldMask(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private FieldMask() { this.memoizedIsInitialized = -1; this.paths_ = LazyStringArrayList.EMPTY; }
/*     */   private FieldMask(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { String s; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: s = input.readStringRequireUtf8(); if ((mutable_bitField0_ & 0x1) == 0) { this.paths_ = new LazyStringArrayList(); mutable_bitField0_ |= 0x1; }  this.paths_.add(s); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x1) != 0)
/*     */         this.paths_ = this.paths_.getUnmodifiableView();  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/* 305 */   public static final Descriptors.Descriptor getDescriptor() { return FieldMaskProto.internal_static_google_protobuf_FieldMask_descriptor; } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 306 */     if (isInitialized == 1) return true; 
/* 307 */     if (isInitialized == 0) return false;
/*     */     
/* 309 */     this.memoizedIsInitialized = 1;
/* 310 */     return true; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return FieldMaskProto.internal_static_google_protobuf_FieldMask_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)FieldMask.class, (Class)Builder.class); }
/*     */   public ProtocolStringList getPathsList() { return this.paths_; }
/*     */   public int getPathsCount() { return this.paths_.size(); }
/*     */   public String getPaths(int index) { return this.paths_.get(index); }
/*     */   public ByteString getPathsBytes(int index) { return this.paths_.getByteString(index); }
/*     */   public void writeTo(CodedOutputStream output) throws IOException {
/* 316 */     for (int i = 0; i < this.paths_.size(); i++) {
/* 317 */       GeneratedMessageV3.writeString(output, 1, this.paths_.getRaw(i));
/*     */     }
/* 319 */     this.unknownFields.writeTo(output);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/* 324 */     int size = this.memoizedSize;
/* 325 */     if (size != -1) return size;
/*     */     
/* 327 */     size = 0;
/*     */     
/* 329 */     int dataSize = 0;
/* 330 */     for (int i = 0; i < this.paths_.size(); i++) {
/* 331 */       dataSize += computeStringSizeNoTag(this.paths_.getRaw(i));
/*     */     }
/* 333 */     size += dataSize;
/* 334 */     size += 1 * getPathsList().size();
/*     */     
/* 336 */     size += this.unknownFields.getSerializedSize();
/* 337 */     this.memoizedSize = size;
/* 338 */     return size;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 343 */     if (obj == this) {
/* 344 */       return true;
/*     */     }
/* 346 */     if (!(obj instanceof FieldMask)) {
/* 347 */       return super.equals(obj);
/*     */     }
/* 349 */     FieldMask other = (FieldMask)obj;
/*     */ 
/*     */     
/* 352 */     if (!getPathsList().equals(other.getPathsList())) return false; 
/* 353 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 354 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 359 */     if (this.memoizedHashCode != 0) {
/* 360 */       return this.memoizedHashCode;
/*     */     }
/* 362 */     int hash = 41;
/* 363 */     hash = 19 * hash + getDescriptor().hashCode();
/* 364 */     if (getPathsCount() > 0) {
/* 365 */       hash = 37 * hash + 1;
/* 366 */       hash = 53 * hash + getPathsList().hashCode();
/*     */     } 
/* 368 */     hash = 29 * hash + this.unknownFields.hashCode();
/* 369 */     this.memoizedHashCode = hash;
/* 370 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static FieldMask parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 376 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static FieldMask parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 382 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static FieldMask parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 387 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static FieldMask parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 393 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static FieldMask parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 397 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static FieldMask parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 403 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static FieldMask parseFrom(InputStream input) throws IOException {
/* 407 */     return 
/* 408 */       GeneratedMessageV3.<FieldMask>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static FieldMask parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 414 */     return 
/* 415 */       GeneratedMessageV3.<FieldMask>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static FieldMask parseDelimitedFrom(InputStream input) throws IOException {
/* 419 */     return 
/* 420 */       GeneratedMessageV3.<FieldMask>parseDelimitedWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static FieldMask parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 426 */     return 
/* 427 */       GeneratedMessageV3.<FieldMask>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static FieldMask parseFrom(CodedInputStream input) throws IOException {
/* 432 */     return 
/* 433 */       GeneratedMessageV3.<FieldMask>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static FieldMask parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 439 */     return 
/* 440 */       GeneratedMessageV3.<FieldMask>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public Builder newBuilderForType() {
/* 444 */     return newBuilder();
/*     */   } public static Builder newBuilder() {
/* 446 */     return DEFAULT_INSTANCE.toBuilder();
/*     */   }
/*     */   public static Builder newBuilder(FieldMask prototype) {
/* 449 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*     */   }
/*     */   
/*     */   public Builder toBuilder() {
/* 453 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 454 */       .mergeFrom(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 460 */     Builder builder = new Builder(parent);
/* 461 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Builder
/*     */     extends GeneratedMessageV3.Builder<Builder>
/*     */     implements FieldMaskOrBuilder
/*     */   {
/*     */     private int bitField0_;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private LazyStringList paths_;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final Descriptors.Descriptor getDescriptor() {
/* 626 */       return FieldMaskProto.internal_static_google_protobuf_FieldMask_descriptor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 632 */       return FieldMaskProto.internal_static_google_protobuf_FieldMask_fieldAccessorTable
/* 633 */         .ensureFieldAccessorsInitialized((Class)FieldMask.class, (Class)Builder.class);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private Builder()
/*     */     {
/* 777 */       this.paths_ = LazyStringArrayList.EMPTY; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.paths_ = LazyStringArrayList.EMPTY; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (GeneratedMessageV3.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.paths_ = LazyStringArrayList.EMPTY; this.bitField0_ &= 0xFFFFFFFE; return this; } public Descriptors.Descriptor getDescriptorForType() { return FieldMaskProto.internal_static_google_protobuf_FieldMask_descriptor; } public FieldMask getDefaultInstanceForType() { return FieldMask.getDefaultInstance(); } public FieldMask build() { FieldMask result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public FieldMask buildPartial() { FieldMask result = new FieldMask(this); int from_bitField0_ = this.bitField0_; if ((this.bitField0_ & 0x1) != 0) { this.paths_ = this.paths_.getUnmodifiableView(); this.bitField0_ &= 0xFFFFFFFE; }  result.paths_ = this.paths_; onBuilt(); return result; } public Builder clone() { return super.clone(); }
/*     */     public Builder setField(Descriptors.FieldDescriptor field, Object value) { return super.setField(field, value); }
/* 779 */     private void ensurePathsIsMutable() { if ((this.bitField0_ & 0x1) == 0)
/* 780 */       { this.paths_ = new LazyStringArrayList(this.paths_);
/* 781 */         this.bitField0_ |= 0x1; }  } public Builder clearField(Descriptors.FieldDescriptor field) { return super.clearField(field); }
/*     */     public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return super.clearOneof(oneof); }
/*     */     public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return super.setRepeatedField(field, index, value); }
/*     */     public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return super.addRepeatedField(field, value); }
/*     */     public Builder mergeFrom(Message other) { if (other instanceof FieldMask)
/*     */         return mergeFrom((FieldMask)other);  super.mergeFrom(other); return this; }
/*     */     public Builder mergeFrom(FieldMask other) { if (other == FieldMask.getDefaultInstance())
/*     */         return this;  if (!other.paths_.isEmpty()) { if (this.paths_.isEmpty()) { this.paths_ = other.paths_; this.bitField0_ &= 0xFFFFFFFE; } else { ensurePathsIsMutable(); this.paths_.addAll(other.paths_); }  onChanged(); }  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*     */     public final boolean isInitialized() { return true; }
/*     */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { FieldMask parsedMessage = null; try { parsedMessage = FieldMask.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (FieldMask)e.getUnfinishedMessage(); throw e.unwrapIOException(); }
/*     */       finally { if (parsedMessage != null)
/*     */           mergeFrom(parsedMessage);  }
/*     */        return this; }
/* 794 */     public ProtocolStringList getPathsList() { return this.paths_.getUnmodifiableView(); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getPathsCount() {
/* 805 */       return this.paths_.size();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getPaths(int index) {
/* 817 */       return this.paths_.get(index);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ByteString getPathsBytes(int index) {
/* 830 */       return this.paths_.getByteString(index);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setPaths(int index, String value) {
/* 844 */       if (value == null) {
/* 845 */         throw new NullPointerException();
/*     */       }
/* 847 */       ensurePathsIsMutable();
/* 848 */       this.paths_.set(index, value);
/* 849 */       onChanged();
/* 850 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addPaths(String value) {
/* 863 */       if (value == null) {
/* 864 */         throw new NullPointerException();
/*     */       }
/* 866 */       ensurePathsIsMutable();
/* 867 */       this.paths_.add(value);
/* 868 */       onChanged();
/* 869 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addAllPaths(Iterable<String> values) {
/* 882 */       ensurePathsIsMutable();
/* 883 */       AbstractMessageLite.Builder.addAll(values, this.paths_);
/*     */       
/* 885 */       onChanged();
/* 886 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder clearPaths() {
/* 897 */       this.paths_ = LazyStringArrayList.EMPTY;
/* 898 */       this.bitField0_ &= 0xFFFFFFFE;
/* 899 */       onChanged();
/* 900 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addPathsBytes(ByteString value) {
/* 913 */       if (value == null) {
/* 914 */         throw new NullPointerException();
/*     */       }
/* 916 */       AbstractMessageLite.checkByteStringIsUtf8(value);
/* 917 */       ensurePathsIsMutable();
/* 918 */       this.paths_.add(value);
/* 919 */       onChanged();
/* 920 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 925 */       return super.setUnknownFields(unknownFields);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 931 */       return super.mergeUnknownFields(unknownFields);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 941 */   private static final FieldMask DEFAULT_INSTANCE = new FieldMask();
/*     */ 
/*     */   
/*     */   public static FieldMask getDefaultInstance() {
/* 945 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ 
/*     */   
/* 949 */   private static final Parser<FieldMask> PARSER = new AbstractParser<FieldMask>()
/*     */     {
/*     */ 
/*     */       
/*     */       public FieldMask parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */       {
/* 955 */         return new FieldMask(input, extensionRegistry);
/*     */       }
/*     */     };
/*     */   
/*     */   public static Parser<FieldMask> parser() {
/* 960 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<FieldMask> getParserForType() {
/* 965 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public FieldMask getDefaultInstanceForType() {
/* 970 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\FieldMask.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */