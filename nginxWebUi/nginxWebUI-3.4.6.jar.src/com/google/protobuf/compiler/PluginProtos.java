/*      */ package com.google.protobuf.compiler;public final class PluginProtos { private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_Version_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_compiler_Version_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable;
/*      */   private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor;
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable;
/*      */   private static Descriptors.FileDescriptor descriptor;
/*      */   
/*      */   public static void registerAllExtensions(ExtensionRegistryLite registry) {}
/*      */   
/*      */   public static void registerAllExtensions(ExtensionRegistry registry) {
/*   14 */     registerAllExtensions((ExtensionRegistryLite)registry);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface VersionOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasMajor();
/*      */ 
/*      */ 
/*      */     
/*      */     int getMajor();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasMinor();
/*      */ 
/*      */ 
/*      */     
/*      */     int getMinor();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasPatch();
/*      */ 
/*      */ 
/*      */     
/*      */     int getPatch();
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasSuffix();
/*      */ 
/*      */ 
/*      */     
/*      */     String getSuffix();
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getSuffixBytes();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Version
/*      */     extends GeneratedMessageV3
/*      */     implements VersionOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */     
/*      */     public static final int MAJOR_FIELD_NUMBER = 1;
/*      */ 
/*      */     
/*      */     private int major_;
/*      */ 
/*      */     
/*      */     public static final int MINOR_FIELD_NUMBER = 2;
/*      */ 
/*      */     
/*      */     private int minor_;
/*      */ 
/*      */     
/*      */     public static final int PATCH_FIELD_NUMBER = 3;
/*      */ 
/*      */     
/*      */     private int patch_;
/*      */ 
/*      */     
/*      */     public static final int SUFFIX_FIELD_NUMBER = 4;
/*      */ 
/*      */     
/*      */     private volatile Object suffix_;
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */     
/*      */     private Version(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/*  100 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  302 */       this.memoizedIsInitialized = -1; } private Version() { this.memoizedIsInitialized = -1; this.suffix_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Version(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Version(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.bitField0_ |= 0x1; this.major_ = input.readInt32(); continue;case 16: this.bitField0_ |= 0x2; this.minor_ = input.readInt32(); continue;case 24: this.bitField0_ |= 0x4; this.patch_ = input.readInt32(); continue;case 34: bs = input.readBytes(); this.bitField0_ |= 0x8; this.suffix_ = bs; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return PluginProtos.internal_static_google_protobuf_compiler_Version_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return PluginProtos.internal_static_google_protobuf_compiler_Version_fieldAccessorTable.ensureFieldAccessorsInitialized(Version.class, Builder.class); } public boolean hasMajor() { return ((this.bitField0_ & 0x1) != 0); } public int getMajor() { return this.major_; } public boolean hasMinor() { return ((this.bitField0_ & 0x2) != 0); } public int getMinor() { return this.minor_; } public boolean hasPatch() { return ((this.bitField0_ & 0x4) != 0); } public int getPatch() { return this.patch_; } public boolean hasSuffix() { return ((this.bitField0_ & 0x8) != 0); }
/*      */     public String getSuffix() { Object ref = this.suffix_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.suffix_ = s;  return s; }
/*      */     public ByteString getSuffixBytes() { Object ref = this.suffix_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.suffix_ = b; return b; }  return (ByteString)ref; }
/*  305 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  306 */       if (isInitialized == 1) return true; 
/*  307 */       if (isInitialized == 0) return false;
/*      */       
/*  309 */       this.memoizedIsInitialized = 1;
/*  310 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/*  316 */       if ((this.bitField0_ & 0x1) != 0) {
/*  317 */         output.writeInt32(1, this.major_);
/*      */       }
/*  319 */       if ((this.bitField0_ & 0x2) != 0) {
/*  320 */         output.writeInt32(2, this.minor_);
/*      */       }
/*  322 */       if ((this.bitField0_ & 0x4) != 0) {
/*  323 */         output.writeInt32(3, this.patch_);
/*      */       }
/*  325 */       if ((this.bitField0_ & 0x8) != 0) {
/*  326 */         GeneratedMessageV3.writeString(output, 4, this.suffix_);
/*      */       }
/*  328 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/*  333 */       int size = this.memoizedSize;
/*  334 */       if (size != -1) return size;
/*      */       
/*  336 */       size = 0;
/*  337 */       if ((this.bitField0_ & 0x1) != 0) {
/*  338 */         size += 
/*  339 */           CodedOutputStream.computeInt32Size(1, this.major_);
/*      */       }
/*  341 */       if ((this.bitField0_ & 0x2) != 0) {
/*  342 */         size += 
/*  343 */           CodedOutputStream.computeInt32Size(2, this.minor_);
/*      */       }
/*  345 */       if ((this.bitField0_ & 0x4) != 0) {
/*  346 */         size += 
/*  347 */           CodedOutputStream.computeInt32Size(3, this.patch_);
/*      */       }
/*  349 */       if ((this.bitField0_ & 0x8) != 0) {
/*  350 */         size += GeneratedMessageV3.computeStringSize(4, this.suffix_);
/*      */       }
/*  352 */       size += this.unknownFields.getSerializedSize();
/*  353 */       this.memoizedSize = size;
/*  354 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  359 */       if (obj == this) {
/*  360 */         return true;
/*      */       }
/*  362 */       if (!(obj instanceof Version)) {
/*  363 */         return super.equals(obj);
/*      */       }
/*  365 */       Version other = (Version)obj;
/*      */       
/*  367 */       if (hasMajor() != other.hasMajor()) return false; 
/*  368 */       if (hasMajor() && 
/*  369 */         getMajor() != other
/*  370 */         .getMajor()) return false;
/*      */       
/*  372 */       if (hasMinor() != other.hasMinor()) return false; 
/*  373 */       if (hasMinor() && 
/*  374 */         getMinor() != other
/*  375 */         .getMinor()) return false;
/*      */       
/*  377 */       if (hasPatch() != other.hasPatch()) return false; 
/*  378 */       if (hasPatch() && 
/*  379 */         getPatch() != other
/*  380 */         .getPatch()) return false;
/*      */       
/*  382 */       if (hasSuffix() != other.hasSuffix()) return false; 
/*  383 */       if (hasSuffix() && 
/*      */         
/*  385 */         !getSuffix().equals(other.getSuffix())) return false;
/*      */       
/*  387 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  388 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  393 */       if (this.memoizedHashCode != 0) {
/*  394 */         return this.memoizedHashCode;
/*      */       }
/*  396 */       int hash = 41;
/*  397 */       hash = 19 * hash + getDescriptor().hashCode();
/*  398 */       if (hasMajor()) {
/*  399 */         hash = 37 * hash + 1;
/*  400 */         hash = 53 * hash + getMajor();
/*      */       } 
/*  402 */       if (hasMinor()) {
/*  403 */         hash = 37 * hash + 2;
/*  404 */         hash = 53 * hash + getMinor();
/*      */       } 
/*  406 */       if (hasPatch()) {
/*  407 */         hash = 37 * hash + 3;
/*  408 */         hash = 53 * hash + getPatch();
/*      */       } 
/*  410 */       if (hasSuffix()) {
/*  411 */         hash = 37 * hash + 4;
/*  412 */         hash = 53 * hash + getSuffix().hashCode();
/*      */       } 
/*  414 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  415 */       this.memoizedHashCode = hash;
/*  416 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Version parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  422 */       return (Version)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Version parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  428 */       return (Version)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Version parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  433 */       return (Version)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Version parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  439 */       return (Version)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Version parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  443 */       return (Version)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Version parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  449 */       return (Version)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Version parseFrom(InputStream input) throws IOException {
/*  453 */       return 
/*  454 */         (Version)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Version parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  460 */       return 
/*  461 */         (Version)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Version parseDelimitedFrom(InputStream input) throws IOException {
/*  465 */       return 
/*  466 */         (Version)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Version parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  472 */       return 
/*  473 */         (Version)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Version parseFrom(CodedInputStream input) throws IOException {
/*  478 */       return 
/*  479 */         (Version)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Version parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  485 */       return 
/*  486 */         (Version)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/*  490 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/*  492 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Version prototype) {
/*  495 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/*  499 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  500 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  506 */       Builder builder = new Builder(parent);
/*  507 */       return builder;
/*      */     }
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements PluginProtos.VersionOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */       private int major_;
/*      */       private int minor_;
/*      */       private int patch_;
/*      */       private Object suffix_;
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/*  522 */         return PluginProtos.internal_static_google_protobuf_compiler_Version_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  528 */         return PluginProtos.internal_static_google_protobuf_compiler_Version_fieldAccessorTable
/*  529 */           .ensureFieldAccessorsInitialized(PluginProtos.Version.class, Builder.class);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*  807 */         this.suffix_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.suffix_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (PluginProtos.Version.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.major_ = 0; this.bitField0_ &= 0xFFFFFFFE; this.minor_ = 0; this.bitField0_ &= 0xFFFFFFFD; this.patch_ = 0; this.bitField0_ &= 0xFFFFFFFB; this.suffix_ = ""; this.bitField0_ &= 0xFFFFFFF7; return this; } public Descriptors.Descriptor getDescriptorForType() { return PluginProtos.internal_static_google_protobuf_compiler_Version_descriptor; } public PluginProtos.Version getDefaultInstanceForType() { return PluginProtos.Version.getDefaultInstance(); } public PluginProtos.Version build() { PluginProtos.Version result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public PluginProtos.Version buildPartial() { PluginProtos.Version result = new PluginProtos.Version(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { result.major_ = this.major_; to_bitField0_ |= 0x1; }  if ((from_bitField0_ & 0x2) != 0) { result.minor_ = this.minor_; to_bitField0_ |= 0x2; }  if ((from_bitField0_ & 0x4) != 0) { result.patch_ = this.patch_; to_bitField0_ |= 0x4; }  if ((from_bitField0_ & 0x8) != 0) to_bitField0_ |= 0x8;  result.suffix_ = this.suffix_; result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof PluginProtos.Version) return mergeFrom((PluginProtos.Version)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(PluginProtos.Version other) { if (other == PluginProtos.Version.getDefaultInstance()) return this;  if (other.hasMajor()) setMajor(other.getMajor());  if (other.hasMinor()) setMinor(other.getMinor());  if (other.hasPatch()) setPatch(other.getPatch());  if (other.hasSuffix()) { this.bitField0_ |= 0x8; this.suffix_ = other.suffix_; onChanged(); }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { PluginProtos.Version parsedMessage = null; try { parsedMessage = (PluginProtos.Version)PluginProtos.Version.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (PluginProtos.Version)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasMajor() { return ((this.bitField0_ & 0x1) != 0); } public int getMajor() { return this.major_; }
/*      */       public Builder setMajor(int value) { this.bitField0_ |= 0x1; this.major_ = value; onChanged(); return this; }
/*      */       public Builder clearMajor() { this.bitField0_ &= 0xFFFFFFFE; this.major_ = 0; onChanged(); return this; }
/*      */       public boolean hasMinor() { return ((this.bitField0_ & 0x2) != 0); }
/*      */       public int getMinor() { return this.minor_; }
/*      */       public Builder setMinor(int value) { this.bitField0_ |= 0x2; this.minor_ = value; onChanged(); return this; }
/*      */       public Builder clearMinor() { this.bitField0_ &= 0xFFFFFFFD; this.minor_ = 0; onChanged(); return this; }
/*      */       public boolean hasPatch() { return ((this.bitField0_ & 0x4) != 0); }
/*      */       public int getPatch() { return this.patch_; }
/*      */       public Builder setPatch(int value) { this.bitField0_ |= 0x4; this.patch_ = value; onChanged(); return this; }
/*      */       public Builder clearPatch() { this.bitField0_ &= 0xFFFFFFFB; this.patch_ = 0; onChanged(); return this; }
/*  818 */       public boolean hasSuffix() { return ((this.bitField0_ & 0x8) != 0); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public String getSuffix() {
/*  830 */         Object ref = this.suffix_;
/*  831 */         if (!(ref instanceof String)) {
/*  832 */           ByteString bs = (ByteString)ref;
/*      */           
/*  834 */           String s = bs.toStringUtf8();
/*  835 */           if (bs.isValidUtf8()) {
/*  836 */             this.suffix_ = s;
/*      */           }
/*  838 */           return s;
/*      */         } 
/*  840 */         return (String)ref;
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
/*      */       public ByteString getSuffixBytes() {
/*  854 */         Object ref = this.suffix_;
/*  855 */         if (ref instanceof String) {
/*      */           
/*  857 */           ByteString b = ByteString.copyFromUtf8((String)ref);
/*      */           
/*  859 */           this.suffix_ = b;
/*  860 */           return b;
/*      */         } 
/*  862 */         return (ByteString)ref;
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
/*      */       public Builder setSuffix(String value) {
/*  877 */         if (value == null) {
/*  878 */           throw new NullPointerException();
/*      */         }
/*  880 */         this.bitField0_ |= 0x8;
/*  881 */         this.suffix_ = value;
/*  882 */         onChanged();
/*  883 */         return this;
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
/*      */       public Builder clearSuffix() {
/*  895 */         this.bitField0_ &= 0xFFFFFFF7;
/*  896 */         this.suffix_ = PluginProtos.Version.getDefaultInstance().getSuffix();
/*  897 */         onChanged();
/*  898 */         return this;
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
/*      */       public Builder setSuffixBytes(ByteString value) {
/*  912 */         if (value == null) {
/*  913 */           throw new NullPointerException();
/*      */         }
/*  915 */         this.bitField0_ |= 0x8;
/*  916 */         this.suffix_ = value;
/*  917 */         onChanged();
/*  918 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  923 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  929 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  939 */     private static final Version DEFAULT_INSTANCE = new Version();
/*      */ 
/*      */     
/*      */     public static Version getDefaultInstance() {
/*  943 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/*  947 */     public static final Parser<Version> PARSER = (Parser<Version>)new AbstractParser<Version>()
/*      */       {
/*      */ 
/*      */         
/*      */         public PluginProtos.Version parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/*  953 */           return new PluginProtos.Version(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Version> parser() {
/*  958 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Version> getParserForType() {
/*  963 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Version getDefaultInstanceForType() {
/*  968 */       return DEFAULT_INSTANCE;
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
/*      */   public static interface CodeGeneratorRequestOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     List<String> getFileToGenerateList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getFileToGenerateCount();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String getFileToGenerate(int param1Int);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getFileToGenerateBytes(int param1Int);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasParameter();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String getParameter();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getParameterBytes();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     List<DescriptorProtos.FileDescriptorProto> getProtoFileList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     DescriptorProtos.FileDescriptorProto getProtoFile(int param1Int);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getProtoFileCount();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileOrBuilderList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     DescriptorProtos.FileDescriptorProtoOrBuilder getProtoFileOrBuilder(int param1Int);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean hasCompilerVersion();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     PluginProtos.Version getCompilerVersion();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     PluginProtos.VersionOrBuilder getCompilerVersionOrBuilder();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class CodeGeneratorRequest
/*      */     extends GeneratedMessageV3
/*      */     implements CodeGeneratorRequestOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int FILE_TO_GENERATE_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private LazyStringList fileToGenerate_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int PARAMETER_FIELD_NUMBER = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private volatile Object parameter_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int PROTO_FILE_FIELD_NUMBER = 15;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private List<DescriptorProtos.FileDescriptorProto> protoFile_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int COMPILER_VERSION_FIELD_NUMBER = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private PluginProtos.Version compilerVersion_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private CodeGeneratorRequest(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 1195 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1571 */       this.memoizedIsInitialized = -1; } private CodeGeneratorRequest() { this.memoizedIsInitialized = -1; this.fileToGenerate_ = LazyStringArrayList.EMPTY; this.parameter_ = ""; this.protoFile_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new CodeGeneratorRequest(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private CodeGeneratorRequest(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; PluginProtos.Version.Builder subBuilder; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: bs = input.readBytes(); if ((mutable_bitField0_ & 0x1) == 0) { this.fileToGenerate_ = (LazyStringList)new LazyStringArrayList(); mutable_bitField0_ |= 0x1; }  this.fileToGenerate_.add(bs); continue;case 18: bs = input.readBytes(); this.bitField0_ |= 0x1; this.parameter_ = bs; continue;case 26: subBuilder = null; if ((this.bitField0_ & 0x2) != 0) subBuilder = this.compilerVersion_.toBuilder();  this.compilerVersion_ = (PluginProtos.Version)input.readMessage(PluginProtos.Version.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.compilerVersion_); this.compilerVersion_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x2; continue;case 122: if ((mutable_bitField0_ & 0x4) == 0) { this.protoFile_ = new ArrayList<>(); mutable_bitField0_ |= 0x4; }  this.protoFile_.add(input.readMessage(DescriptorProtos.FileDescriptorProto.PARSER, extensionRegistry)); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x1) != 0) this.fileToGenerate_ = this.fileToGenerate_.getUnmodifiableView();  if ((mutable_bitField0_ & 0x4) != 0) this.protoFile_ = Collections.unmodifiableList(this.protoFile_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable.ensureFieldAccessorsInitialized(CodeGeneratorRequest.class, Builder.class); } public ProtocolStringList getFileToGenerateList() { return (ProtocolStringList)this.fileToGenerate_; } public int getFileToGenerateCount() { return this.fileToGenerate_.size(); } public String getFileToGenerate(int index) { return (String)this.fileToGenerate_.get(index); } public ByteString getFileToGenerateBytes(int index) { return this.fileToGenerate_.getByteString(index); } public boolean hasParameter() { return ((this.bitField0_ & 0x1) != 0); } public String getParameter() { Object ref = this.parameter_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.parameter_ = s;  return s; } public ByteString getParameterBytes() { Object ref = this.parameter_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.parameter_ = b; return b; }  return (ByteString)ref; } public List<DescriptorProtos.FileDescriptorProto> getProtoFileList() { return this.protoFile_; } public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileOrBuilderList() { return (List)this.protoFile_; } public int getProtoFileCount() { return this.protoFile_.size(); } public DescriptorProtos.FileDescriptorProto getProtoFile(int index) { return this.protoFile_.get(index); } public DescriptorProtos.FileDescriptorProtoOrBuilder getProtoFileOrBuilder(int index) { return (DescriptorProtos.FileDescriptorProtoOrBuilder)this.protoFile_.get(index); } public boolean hasCompilerVersion() { return ((this.bitField0_ & 0x2) != 0); }
/*      */     public PluginProtos.Version getCompilerVersion() { return (this.compilerVersion_ == null) ? PluginProtos.Version.getDefaultInstance() : this.compilerVersion_; }
/*      */     public PluginProtos.VersionOrBuilder getCompilerVersionOrBuilder() { return (this.compilerVersion_ == null) ? PluginProtos.Version.getDefaultInstance() : this.compilerVersion_; }
/* 1574 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 1575 */       if (isInitialized == 1) return true; 
/* 1576 */       if (isInitialized == 0) return false;
/*      */       
/* 1578 */       for (int i = 0; i < getProtoFileCount(); i++) {
/* 1579 */         if (!getProtoFile(i).isInitialized()) {
/* 1580 */           this.memoizedIsInitialized = 0;
/* 1581 */           return false;
/*      */         } 
/*      */       } 
/* 1584 */       this.memoizedIsInitialized = 1;
/* 1585 */       return true; }
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/*      */       int i;
/* 1591 */       for (i = 0; i < this.fileToGenerate_.size(); i++) {
/* 1592 */         GeneratedMessageV3.writeString(output, 1, this.fileToGenerate_.getRaw(i));
/*      */       }
/* 1594 */       if ((this.bitField0_ & 0x1) != 0) {
/* 1595 */         GeneratedMessageV3.writeString(output, 2, this.parameter_);
/*      */       }
/* 1597 */       if ((this.bitField0_ & 0x2) != 0) {
/* 1598 */         output.writeMessage(3, (MessageLite)getCompilerVersion());
/*      */       }
/* 1600 */       for (i = 0; i < this.protoFile_.size(); i++) {
/* 1601 */         output.writeMessage(15, (MessageLite)this.protoFile_.get(i));
/*      */       }
/* 1603 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 1608 */       int size = this.memoizedSize;
/* 1609 */       if (size != -1) return size;
/*      */       
/* 1611 */       size = 0;
/*      */       
/* 1613 */       int dataSize = 0;
/* 1614 */       for (int j = 0; j < this.fileToGenerate_.size(); j++) {
/* 1615 */         dataSize += computeStringSizeNoTag(this.fileToGenerate_.getRaw(j));
/*      */       }
/* 1617 */       size += dataSize;
/* 1618 */       size += 1 * getFileToGenerateList().size();
/*      */       
/* 1620 */       if ((this.bitField0_ & 0x1) != 0) {
/* 1621 */         size += GeneratedMessageV3.computeStringSize(2, this.parameter_);
/*      */       }
/* 1623 */       if ((this.bitField0_ & 0x2) != 0) {
/* 1624 */         size += 
/* 1625 */           CodedOutputStream.computeMessageSize(3, (MessageLite)getCompilerVersion());
/*      */       }
/* 1627 */       for (int i = 0; i < this.protoFile_.size(); i++) {
/* 1628 */         size += 
/* 1629 */           CodedOutputStream.computeMessageSize(15, (MessageLite)this.protoFile_.get(i));
/*      */       }
/* 1631 */       size += this.unknownFields.getSerializedSize();
/* 1632 */       this.memoizedSize = size;
/* 1633 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1638 */       if (obj == this) {
/* 1639 */         return true;
/*      */       }
/* 1641 */       if (!(obj instanceof CodeGeneratorRequest)) {
/* 1642 */         return super.equals(obj);
/*      */       }
/* 1644 */       CodeGeneratorRequest other = (CodeGeneratorRequest)obj;
/*      */ 
/*      */       
/* 1647 */       if (!getFileToGenerateList().equals(other.getFileToGenerateList())) return false; 
/* 1648 */       if (hasParameter() != other.hasParameter()) return false; 
/* 1649 */       if (hasParameter() && 
/*      */         
/* 1651 */         !getParameter().equals(other.getParameter())) return false;
/*      */ 
/*      */       
/* 1654 */       if (!getProtoFileList().equals(other.getProtoFileList())) return false; 
/* 1655 */       if (hasCompilerVersion() != other.hasCompilerVersion()) return false; 
/* 1656 */       if (hasCompilerVersion() && 
/*      */         
/* 1658 */         !getCompilerVersion().equals(other.getCompilerVersion())) return false;
/*      */       
/* 1660 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 1661 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1666 */       if (this.memoizedHashCode != 0) {
/* 1667 */         return this.memoizedHashCode;
/*      */       }
/* 1669 */       int hash = 41;
/* 1670 */       hash = 19 * hash + getDescriptor().hashCode();
/* 1671 */       if (getFileToGenerateCount() > 0) {
/* 1672 */         hash = 37 * hash + 1;
/* 1673 */         hash = 53 * hash + getFileToGenerateList().hashCode();
/*      */       } 
/* 1675 */       if (hasParameter()) {
/* 1676 */         hash = 37 * hash + 2;
/* 1677 */         hash = 53 * hash + getParameter().hashCode();
/*      */       } 
/* 1679 */       if (getProtoFileCount() > 0) {
/* 1680 */         hash = 37 * hash + 15;
/* 1681 */         hash = 53 * hash + getProtoFileList().hashCode();
/*      */       } 
/* 1683 */       if (hasCompilerVersion()) {
/* 1684 */         hash = 37 * hash + 3;
/* 1685 */         hash = 53 * hash + getCompilerVersion().hashCode();
/*      */       } 
/* 1687 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 1688 */       this.memoizedHashCode = hash;
/* 1689 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CodeGeneratorRequest parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 1695 */       return (CodeGeneratorRequest)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CodeGeneratorRequest parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1701 */       return (CodeGeneratorRequest)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static CodeGeneratorRequest parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 1706 */       return (CodeGeneratorRequest)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CodeGeneratorRequest parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1712 */       return (CodeGeneratorRequest)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static CodeGeneratorRequest parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 1716 */       return (CodeGeneratorRequest)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CodeGeneratorRequest parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1722 */       return (CodeGeneratorRequest)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static CodeGeneratorRequest parseFrom(InputStream input) throws IOException {
/* 1726 */       return 
/* 1727 */         (CodeGeneratorRequest)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CodeGeneratorRequest parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1733 */       return 
/* 1734 */         (CodeGeneratorRequest)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static CodeGeneratorRequest parseDelimitedFrom(InputStream input) throws IOException {
/* 1738 */       return 
/* 1739 */         (CodeGeneratorRequest)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CodeGeneratorRequest parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1745 */       return 
/* 1746 */         (CodeGeneratorRequest)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static CodeGeneratorRequest parseFrom(CodedInputStream input) throws IOException {
/* 1751 */       return 
/* 1752 */         (CodeGeneratorRequest)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CodeGeneratorRequest parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1758 */       return 
/* 1759 */         (CodeGeneratorRequest)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 1763 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 1765 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(CodeGeneratorRequest prototype) {
/* 1768 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 1772 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 1773 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 1779 */       Builder builder = new Builder(parent);
/* 1780 */       return builder;
/*      */     }
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements PluginProtos.CodeGeneratorRequestOrBuilder {
/*      */       private int bitField0_;
/*      */       private LazyStringList fileToGenerate_;
/*      */       private Object parameter_;
/*      */       private List<DescriptorProtos.FileDescriptorProto> protoFile_;
/*      */       private RepeatedFieldBuilderV3<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder, DescriptorProtos.FileDescriptorProtoOrBuilder> protoFileBuilder_;
/*      */       private PluginProtos.Version compilerVersion_;
/*      */       private SingleFieldBuilderV3<PluginProtos.Version, PluginProtos.Version.Builder, PluginProtos.VersionOrBuilder> compilerVersionBuilder_;
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 1795 */         return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 1801 */         return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable
/* 1802 */           .ensureFieldAccessorsInitialized(PluginProtos.CodeGeneratorRequest.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/* 2024 */         this.fileToGenerate_ = LazyStringArrayList.EMPTY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2187 */         this.parameter_ = "";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2295 */         this
/* 2296 */           .protoFile_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.fileToGenerate_ = LazyStringArrayList.EMPTY; this.parameter_ = ""; this.protoFile_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (PluginProtos.CodeGeneratorRequest.alwaysUseFieldBuilders) { getProtoFileFieldBuilder(); getCompilerVersionFieldBuilder(); }  } public Builder clear() { super.clear(); this.fileToGenerate_ = LazyStringArrayList.EMPTY; this.bitField0_ &= 0xFFFFFFFE; this.parameter_ = ""; this.bitField0_ &= 0xFFFFFFFD; if (this.protoFileBuilder_ == null) { this.protoFile_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFB; } else { this.protoFileBuilder_.clear(); }  if (this.compilerVersionBuilder_ == null) { this.compilerVersion_ = null; } else { this.compilerVersionBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFF7; return this; } public Descriptors.Descriptor getDescriptorForType() { return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor; } public PluginProtos.CodeGeneratorRequest getDefaultInstanceForType() { return PluginProtos.CodeGeneratorRequest.getDefaultInstance(); } public PluginProtos.CodeGeneratorRequest build() { PluginProtos.CodeGeneratorRequest result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public PluginProtos.CodeGeneratorRequest buildPartial() { PluginProtos.CodeGeneratorRequest result = new PluginProtos.CodeGeneratorRequest(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((this.bitField0_ & 0x1) != 0) { this.fileToGenerate_ = this.fileToGenerate_.getUnmodifiableView(); this.bitField0_ &= 0xFFFFFFFE; }  result.fileToGenerate_ = this.fileToGenerate_; if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x1;  result.parameter_ = this.parameter_; if (this.protoFileBuilder_ == null) { if ((this.bitField0_ & 0x4) != 0) { this.protoFile_ = Collections.unmodifiableList(this.protoFile_); this.bitField0_ &= 0xFFFFFFFB; }  result.protoFile_ = this.protoFile_; } else { result.protoFile_ = this.protoFileBuilder_.build(); }  if ((from_bitField0_ & 0x8) != 0) { if (this.compilerVersionBuilder_ == null) { result.compilerVersion_ = this.compilerVersion_; } else { result.compilerVersion_ = (PluginProtos.Version)this.compilerVersionBuilder_.build(); }  to_bitField0_ |= 0x2; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof PluginProtos.CodeGeneratorRequest) return mergeFrom((PluginProtos.CodeGeneratorRequest)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(PluginProtos.CodeGeneratorRequest other) { if (other == PluginProtos.CodeGeneratorRequest.getDefaultInstance()) return this;  if (!other.fileToGenerate_.isEmpty()) { if (this.fileToGenerate_.isEmpty()) { this.fileToGenerate_ = other.fileToGenerate_; this.bitField0_ &= 0xFFFFFFFE; } else { ensureFileToGenerateIsMutable(); this.fileToGenerate_.addAll((Collection)other.fileToGenerate_); }  onChanged(); }  if (other.hasParameter()) { this.bitField0_ |= 0x2; this.parameter_ = other.parameter_; onChanged(); }  if (this.protoFileBuilder_ == null) { if (!other.protoFile_.isEmpty()) { if (this.protoFile_.isEmpty()) { this.protoFile_ = other.protoFile_; this.bitField0_ &= 0xFFFFFFFB; } else { ensureProtoFileIsMutable(); this.protoFile_.addAll(other.protoFile_); }  onChanged(); }  } else if (!other.protoFile_.isEmpty()) { if (this.protoFileBuilder_.isEmpty()) { this.protoFileBuilder_.dispose(); this.protoFileBuilder_ = null; this.protoFile_ = other.protoFile_; this.bitField0_ &= 0xFFFFFFFB; this.protoFileBuilder_ = PluginProtos.CodeGeneratorRequest.alwaysUseFieldBuilders ? getProtoFileFieldBuilder() : null; } else { this.protoFileBuilder_.addAllMessages(other.protoFile_); }  }  if (other.hasCompilerVersion()) mergeCompilerVersion(other.getCompilerVersion());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { for (int i = 0; i < getProtoFileCount(); i++) { if (!getProtoFile(i).isInitialized()) return false;  }  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { PluginProtos.CodeGeneratorRequest parsedMessage = null; try { parsedMessage = (PluginProtos.CodeGeneratorRequest)PluginProtos.CodeGeneratorRequest.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (PluginProtos.CodeGeneratorRequest)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } private void ensureFileToGenerateIsMutable() { if ((this.bitField0_ & 0x1) == 0) { this.fileToGenerate_ = (LazyStringList)new LazyStringArrayList(this.fileToGenerate_); this.bitField0_ |= 0x1; }  } public ProtocolStringList getFileToGenerateList() { return (ProtocolStringList)this.fileToGenerate_.getUnmodifiableView(); } public int getFileToGenerateCount() { return this.fileToGenerate_.size(); } public String getFileToGenerate(int index) { return (String)this.fileToGenerate_.get(index); } public ByteString getFileToGenerateBytes(int index) { return this.fileToGenerate_.getByteString(index); } public Builder setFileToGenerate(int index, String value) { if (value == null) throw new NullPointerException();  ensureFileToGenerateIsMutable(); this.fileToGenerate_.set(index, value); onChanged(); return this; } public Builder addFileToGenerate(String value) { if (value == null) throw new NullPointerException();  ensureFileToGenerateIsMutable(); this.fileToGenerate_.add(value); onChanged(); return this; } public Builder addAllFileToGenerate(Iterable<String> values) { ensureFileToGenerateIsMutable(); AbstractMessageLite.Builder.addAll(values, (List)this.fileToGenerate_); onChanged(); return this; } public Builder clearFileToGenerate() { this.fileToGenerate_ = LazyStringArrayList.EMPTY; this.bitField0_ &= 0xFFFFFFFE; onChanged(); return this; } public Builder addFileToGenerateBytes(ByteString value) { if (value == null) throw new NullPointerException();  ensureFileToGenerateIsMutable(); this.fileToGenerate_.add(value); onChanged(); return this; } public boolean hasParameter() { return ((this.bitField0_ & 0x2) != 0); } public String getParameter() { Object ref = this.parameter_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.parameter_ = s;  return s; }  return (String)ref; } public ByteString getParameterBytes() { Object ref = this.parameter_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.parameter_ = b; return b; }  return (ByteString)ref; } public Builder setParameter(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.parameter_ = value; onChanged(); return this; } public Builder clearParameter() { this.bitField0_ &= 0xFFFFFFFD; this.parameter_ = PluginProtos.CodeGeneratorRequest.getDefaultInstance().getParameter(); onChanged(); return this; }
/*      */       public Builder setParameterBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.parameter_ = value; onChanged(); return this; }
/* 2298 */       private void ensureProtoFileIsMutable() { if ((this.bitField0_ & 0x4) == 0) {
/* 2299 */           this.protoFile_ = new ArrayList<>(this.protoFile_);
/* 2300 */           this.bitField0_ |= 0x4;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<DescriptorProtos.FileDescriptorProto> getProtoFileList() {
/* 2326 */         if (this.protoFileBuilder_ == null) {
/* 2327 */           return Collections.unmodifiableList(this.protoFile_);
/*      */         }
/* 2329 */         return this.protoFileBuilder_.getMessageList();
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
/*      */       public int getProtoFileCount() {
/* 2351 */         if (this.protoFileBuilder_ == null) {
/* 2352 */           return this.protoFile_.size();
/*      */         }
/* 2354 */         return this.protoFileBuilder_.getCount();
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
/*      */       public DescriptorProtos.FileDescriptorProto getProtoFile(int index) {
/* 2376 */         if (this.protoFileBuilder_ == null) {
/* 2377 */           return this.protoFile_.get(index);
/*      */         }
/* 2379 */         return (DescriptorProtos.FileDescriptorProto)this.protoFileBuilder_.getMessage(index);
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
/*      */       public Builder setProtoFile(int index, DescriptorProtos.FileDescriptorProto value) {
/* 2402 */         if (this.protoFileBuilder_ == null) {
/* 2403 */           if (value == null) {
/* 2404 */             throw new NullPointerException();
/*      */           }
/* 2406 */           ensureProtoFileIsMutable();
/* 2407 */           this.protoFile_.set(index, value);
/* 2408 */           onChanged();
/*      */         } else {
/* 2410 */           this.protoFileBuilder_.setMessage(index, (AbstractMessage)value);
/*      */         } 
/* 2412 */         return this;
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
/*      */       public Builder setProtoFile(int index, DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
/* 2434 */         if (this.protoFileBuilder_ == null) {
/* 2435 */           ensureProtoFileIsMutable();
/* 2436 */           this.protoFile_.set(index, builderForValue.build());
/* 2437 */           onChanged();
/*      */         } else {
/* 2439 */           this.protoFileBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 2441 */         return this;
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
/*      */       public Builder addProtoFile(DescriptorProtos.FileDescriptorProto value) {
/* 2462 */         if (this.protoFileBuilder_ == null) {
/* 2463 */           if (value == null) {
/* 2464 */             throw new NullPointerException();
/*      */           }
/* 2466 */           ensureProtoFileIsMutable();
/* 2467 */           this.protoFile_.add(value);
/* 2468 */           onChanged();
/*      */         } else {
/* 2470 */           this.protoFileBuilder_.addMessage((AbstractMessage)value);
/*      */         } 
/* 2472 */         return this;
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
/*      */       public Builder addProtoFile(int index, DescriptorProtos.FileDescriptorProto value) {
/* 2494 */         if (this.protoFileBuilder_ == null) {
/* 2495 */           if (value == null) {
/* 2496 */             throw new NullPointerException();
/*      */           }
/* 2498 */           ensureProtoFileIsMutable();
/* 2499 */           this.protoFile_.add(index, value);
/* 2500 */           onChanged();
/*      */         } else {
/* 2502 */           this.protoFileBuilder_.addMessage(index, (AbstractMessage)value);
/*      */         } 
/* 2504 */         return this;
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
/*      */       public Builder addProtoFile(DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
/* 2526 */         if (this.protoFileBuilder_ == null) {
/* 2527 */           ensureProtoFileIsMutable();
/* 2528 */           this.protoFile_.add(builderForValue.build());
/* 2529 */           onChanged();
/*      */         } else {
/* 2531 */           this.protoFileBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 2533 */         return this;
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
/*      */       public Builder addProtoFile(int index, DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
/* 2555 */         if (this.protoFileBuilder_ == null) {
/* 2556 */           ensureProtoFileIsMutable();
/* 2557 */           this.protoFile_.add(index, builderForValue.build());
/* 2558 */           onChanged();
/*      */         } else {
/* 2560 */           this.protoFileBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 2562 */         return this;
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
/*      */       public Builder addAllProtoFile(Iterable<? extends DescriptorProtos.FileDescriptorProto> values) {
/* 2584 */         if (this.protoFileBuilder_ == null) {
/* 2585 */           ensureProtoFileIsMutable();
/* 2586 */           AbstractMessageLite.Builder.addAll(values, this.protoFile_);
/*      */           
/* 2588 */           onChanged();
/*      */         } else {
/* 2590 */           this.protoFileBuilder_.addAllMessages(values);
/*      */         } 
/* 2592 */         return this;
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
/*      */       public Builder clearProtoFile() {
/* 2613 */         if (this.protoFileBuilder_ == null) {
/* 2614 */           this.protoFile_ = Collections.emptyList();
/* 2615 */           this.bitField0_ &= 0xFFFFFFFB;
/* 2616 */           onChanged();
/*      */         } else {
/* 2618 */           this.protoFileBuilder_.clear();
/*      */         } 
/* 2620 */         return this;
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
/*      */       public Builder removeProtoFile(int index) {
/* 2641 */         if (this.protoFileBuilder_ == null) {
/* 2642 */           ensureProtoFileIsMutable();
/* 2643 */           this.protoFile_.remove(index);
/* 2644 */           onChanged();
/*      */         } else {
/* 2646 */           this.protoFileBuilder_.remove(index);
/*      */         } 
/* 2648 */         return this;
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
/*      */       public DescriptorProtos.FileDescriptorProto.Builder getProtoFileBuilder(int index) {
/* 2670 */         return (DescriptorProtos.FileDescriptorProto.Builder)getProtoFileFieldBuilder().getBuilder(index);
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
/*      */       public DescriptorProtos.FileDescriptorProtoOrBuilder getProtoFileOrBuilder(int index) {
/* 2692 */         if (this.protoFileBuilder_ == null)
/* 2693 */           return (DescriptorProtos.FileDescriptorProtoOrBuilder)this.protoFile_.get(index); 
/* 2694 */         return (DescriptorProtos.FileDescriptorProtoOrBuilder)this.protoFileBuilder_.getMessageOrBuilder(index);
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
/*      */       public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileOrBuilderList() {
/* 2717 */         if (this.protoFileBuilder_ != null) {
/* 2718 */           return this.protoFileBuilder_.getMessageOrBuilderList();
/*      */         }
/* 2720 */         return (List)Collections.unmodifiableList(this.protoFile_);
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
/*      */       public DescriptorProtos.FileDescriptorProto.Builder addProtoFileBuilder() {
/* 2742 */         return (DescriptorProtos.FileDescriptorProto.Builder)getProtoFileFieldBuilder().addBuilder(
/* 2743 */             (AbstractMessage)DescriptorProtos.FileDescriptorProto.getDefaultInstance());
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
/*      */       public DescriptorProtos.FileDescriptorProto.Builder addProtoFileBuilder(int index) {
/* 2765 */         return (DescriptorProtos.FileDescriptorProto.Builder)getProtoFileFieldBuilder().addBuilder(index, 
/* 2766 */             (AbstractMessage)DescriptorProtos.FileDescriptorProto.getDefaultInstance());
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
/*      */       public List<DescriptorProtos.FileDescriptorProto.Builder> getProtoFileBuilderList() {
/* 2788 */         return getProtoFileFieldBuilder().getBuilderList();
/*      */       }
/*      */ 
/*      */       
/*      */       private RepeatedFieldBuilderV3<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder, DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileFieldBuilder() {
/* 2793 */         if (this.protoFileBuilder_ == null) {
/* 2794 */           this
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2799 */             .protoFileBuilder_ = new RepeatedFieldBuilderV3(this.protoFile_, ((this.bitField0_ & 0x4) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 2800 */           this.protoFile_ = null;
/*      */         } 
/* 2802 */         return this.protoFileBuilder_;
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
/*      */       public boolean hasCompilerVersion() {
/* 2817 */         return ((this.bitField0_ & 0x8) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public PluginProtos.Version getCompilerVersion() {
/* 2828 */         if (this.compilerVersionBuilder_ == null) {
/* 2829 */           return (this.compilerVersion_ == null) ? PluginProtos.Version.getDefaultInstance() : this.compilerVersion_;
/*      */         }
/* 2831 */         return (PluginProtos.Version)this.compilerVersionBuilder_.getMessage();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setCompilerVersion(PluginProtos.Version value) {
/* 2842 */         if (this.compilerVersionBuilder_ == null) {
/* 2843 */           if (value == null) {
/* 2844 */             throw new NullPointerException();
/*      */           }
/* 2846 */           this.compilerVersion_ = value;
/* 2847 */           onChanged();
/*      */         } else {
/* 2849 */           this.compilerVersionBuilder_.setMessage((AbstractMessage)value);
/*      */         } 
/* 2851 */         this.bitField0_ |= 0x8;
/* 2852 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setCompilerVersion(PluginProtos.Version.Builder builderForValue) {
/* 2863 */         if (this.compilerVersionBuilder_ == null) {
/* 2864 */           this.compilerVersion_ = builderForValue.build();
/* 2865 */           onChanged();
/*      */         } else {
/* 2867 */           this.compilerVersionBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 2869 */         this.bitField0_ |= 0x8;
/* 2870 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeCompilerVersion(PluginProtos.Version value) {
/* 2880 */         if (this.compilerVersionBuilder_ == null) {
/* 2881 */           if ((this.bitField0_ & 0x8) != 0 && this.compilerVersion_ != null && this.compilerVersion_ != 
/*      */             
/* 2883 */             PluginProtos.Version.getDefaultInstance()) {
/* 2884 */             this
/* 2885 */               .compilerVersion_ = PluginProtos.Version.newBuilder(this.compilerVersion_).mergeFrom(value).buildPartial();
/*      */           } else {
/* 2887 */             this.compilerVersion_ = value;
/*      */           } 
/* 2889 */           onChanged();
/*      */         } else {
/* 2891 */           this.compilerVersionBuilder_.mergeFrom((AbstractMessage)value);
/*      */         } 
/* 2893 */         this.bitField0_ |= 0x8;
/* 2894 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearCompilerVersion() {
/* 2904 */         if (this.compilerVersionBuilder_ == null) {
/* 2905 */           this.compilerVersion_ = null;
/* 2906 */           onChanged();
/*      */         } else {
/* 2908 */           this.compilerVersionBuilder_.clear();
/*      */         } 
/* 2910 */         this.bitField0_ &= 0xFFFFFFF7;
/* 2911 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public PluginProtos.Version.Builder getCompilerVersionBuilder() {
/* 2921 */         this.bitField0_ |= 0x8;
/* 2922 */         onChanged();
/* 2923 */         return (PluginProtos.Version.Builder)getCompilerVersionFieldBuilder().getBuilder();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public PluginProtos.VersionOrBuilder getCompilerVersionOrBuilder() {
/* 2933 */         if (this.compilerVersionBuilder_ != null) {
/* 2934 */           return (PluginProtos.VersionOrBuilder)this.compilerVersionBuilder_.getMessageOrBuilder();
/*      */         }
/* 2936 */         return (this.compilerVersion_ == null) ? 
/* 2937 */           PluginProtos.Version.getDefaultInstance() : this.compilerVersion_;
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
/*      */       private SingleFieldBuilderV3<PluginProtos.Version, PluginProtos.Version.Builder, PluginProtos.VersionOrBuilder> getCompilerVersionFieldBuilder() {
/* 2950 */         if (this.compilerVersionBuilder_ == null) {
/* 2951 */           this
/*      */ 
/*      */ 
/*      */             
/* 2955 */             .compilerVersionBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCompilerVersion(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 2956 */           this.compilerVersion_ = null;
/*      */         } 
/* 2958 */         return this.compilerVersionBuilder_;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 2963 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 2969 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2979 */     private static final CodeGeneratorRequest DEFAULT_INSTANCE = new CodeGeneratorRequest();
/*      */ 
/*      */     
/*      */     public static CodeGeneratorRequest getDefaultInstance() {
/* 2983 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 2987 */     public static final Parser<CodeGeneratorRequest> PARSER = (Parser<CodeGeneratorRequest>)new AbstractParser<CodeGeneratorRequest>()
/*      */       {
/*      */ 
/*      */         
/*      */         public PluginProtos.CodeGeneratorRequest parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 2993 */           return new PluginProtos.CodeGeneratorRequest(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<CodeGeneratorRequest> parser() {
/* 2998 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<CodeGeneratorRequest> getParserForType() {
/* 3003 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public CodeGeneratorRequest getDefaultInstanceForType() {
/* 3008 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface CodeGeneratorResponseOrBuilder
/*      */     extends MessageOrBuilder
/*      */   {
/*      */     boolean hasError();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String getError();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ByteString getErrorBytes();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     List<PluginProtos.CodeGeneratorResponse.File> getFileList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     PluginProtos.CodeGeneratorResponse.File getFile(int param1Int);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int getFileCount();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     List<? extends PluginProtos.CodeGeneratorResponse.FileOrBuilder> getFileOrBuilderList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     PluginProtos.CodeGeneratorResponse.FileOrBuilder getFileOrBuilder(int param1Int);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class CodeGeneratorResponse
/*      */     extends GeneratedMessageV3
/*      */     implements CodeGeneratorResponseOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int ERROR_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */     
/*      */     private volatile Object error_;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int FILE_FIELD_NUMBER = 15;
/*      */ 
/*      */ 
/*      */     
/*      */     private List<File> file_;
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private CodeGeneratorResponse(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 3102 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 4906 */       this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new CodeGeneratorResponse(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private CodeGeneratorResponse(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: bs = input.readBytes(); this.bitField0_ |= 0x1; this.error_ = bs; continue;case 122: if ((mutable_bitField0_ & 0x2) == 0) { this.file_ = new ArrayList<>(); mutable_bitField0_ |= 0x2; }  this.file_.add(input.readMessage(File.PARSER, extensionRegistry)); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x2) != 0) this.file_ = Collections.unmodifiableList(this.file_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor; } private CodeGeneratorResponse() { this.memoizedIsInitialized = -1; this.error_ = ""; this.file_ = Collections.emptyList(); } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable.ensureFieldAccessorsInitialized(CodeGeneratorResponse.class, Builder.class); } public static final class File extends GeneratedMessageV3 implements FileOrBuilder {
/*      */       private static final long serialVersionUID = 0L; private int bitField0_; public static final int NAME_FIELD_NUMBER = 1; private volatile Object name_; public static final int INSERTION_POINT_FIELD_NUMBER = 2; private volatile Object insertionPoint_; public static final int CONTENT_FIELD_NUMBER = 15; private volatile Object content_; private byte memoizedIsInitialized; private File(GeneratedMessageV3.Builder<?> builder) { super(builder); this.memoizedIsInitialized = -1; } private File() { this.memoizedIsInitialized = -1; this.name_ = ""; this.insertionPoint_ = ""; this.content_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new File(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private File(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: bs = input.readBytes(); this.bitField0_ |= 0x1; this.name_ = bs; continue;case 18: bs = input.readBytes(); this.bitField0_ |= 0x2; this.insertionPoint_ = bs; continue;case 122: bs = input.readBytes(); this.bitField0_ |= 0x4; this.content_ = bs; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable.ensureFieldAccessorsInitialized(File.class, Builder.class); } public boolean hasName() { return ((this.bitField0_ & 0x1) != 0); } public String getName() { Object ref = this.name_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.name_ = s;  return s; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; } public boolean hasInsertionPoint() { return ((this.bitField0_ & 0x2) != 0); } public String getInsertionPoint() { Object ref = this.insertionPoint_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.insertionPoint_ = s;  return s; } public ByteString getInsertionPointBytes() { Object ref = this.insertionPoint_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.insertionPoint_ = b; return b; }  return (ByteString)ref; } public boolean hasContent() { return ((this.bitField0_ & 0x4) != 0); } public String getContent() { Object ref = this.content_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.content_ = s;  return s; } public ByteString getContentBytes() { Object ref = this.content_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.content_ = b; return b; }  return (ByteString)ref; } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized; if (isInitialized == 1) return true;  if (isInitialized == 0) return false;  this.memoizedIsInitialized = 1; return true; } public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) GeneratedMessageV3.writeString(output, 1, this.name_);  if ((this.bitField0_ & 0x2) != 0) GeneratedMessageV3.writeString(output, 2, this.insertionPoint_);  if ((this.bitField0_ & 0x4) != 0) GeneratedMessageV3.writeString(output, 15, this.content_);  this.unknownFields.writeTo(output); } public int getSerializedSize() { int size = this.memoizedSize; if (size != -1) return size;  size = 0; if ((this.bitField0_ & 0x1) != 0) size += GeneratedMessageV3.computeStringSize(1, this.name_);  if ((this.bitField0_ & 0x2) != 0) size += GeneratedMessageV3.computeStringSize(2, this.insertionPoint_);  if ((this.bitField0_ & 0x4) != 0) size += GeneratedMessageV3.computeStringSize(15, this.content_);  size += this.unknownFields.getSerializedSize(); this.memoizedSize = size; return size; } public boolean equals(Object obj) { if (obj == this) return true;  if (!(obj instanceof File)) return super.equals(obj);  File other = (File)obj; if (hasName() != other.hasName()) return false;  if (hasName() && !getName().equals(other.getName())) return false;  if (hasInsertionPoint() != other.hasInsertionPoint()) return false;  if (hasInsertionPoint() && !getInsertionPoint().equals(other.getInsertionPoint())) return false;  if (hasContent() != other.hasContent()) return false;  if (hasContent() && !getContent().equals(other.getContent())) return false;  if (!this.unknownFields.equals(other.unknownFields)) return false;  return true; } public int hashCode() { if (this.memoizedHashCode != 0) return this.memoizedHashCode;  int hash = 41; hash = 19 * hash + getDescriptor().hashCode(); if (hasName()) { hash = 37 * hash + 1; hash = 53 * hash + getName().hashCode(); }  if (hasInsertionPoint()) { hash = 37 * hash + 2; hash = 53 * hash + getInsertionPoint().hashCode(); }  if (hasContent()) { hash = 37 * hash + 15; hash = 53 * hash + getContent().hashCode(); }  hash = 29 * hash + this.unknownFields.hashCode(); this.memoizedHashCode = hash; return hash; } public static File parseFrom(ByteBuffer data) throws InvalidProtocolBufferException { return (File)PARSER.parseFrom(data); } public static File parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (File)PARSER.parseFrom(data, extensionRegistry); } public static File parseFrom(ByteString data) throws InvalidProtocolBufferException { return (File)PARSER.parseFrom(data); } public static File parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (File)PARSER.parseFrom(data, extensionRegistry); } public static File parseFrom(byte[] data) throws InvalidProtocolBufferException { return (File)PARSER.parseFrom(data); } public static File parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (File)PARSER.parseFrom(data, extensionRegistry); } public static File parseFrom(InputStream input) throws IOException { return (File)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static File parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (File)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public static File parseDelimitedFrom(InputStream input) throws IOException { return (File)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input); } public static File parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (File)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry); } public static File parseFrom(CodedInputStream input) throws IOException { return (File)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static File parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (File)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public Builder newBuilderForType() { return newBuilder(); } public static Builder newBuilder() { return DEFAULT_INSTANCE.toBuilder(); } public static Builder newBuilder(File prototype) { return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype); } public Builder toBuilder() { return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder()).mergeFrom(this); } protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) { Builder builder = new Builder(parent); return builder; } public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements PluginProtos.CodeGeneratorResponse.FileOrBuilder {
/*      */         private int bitField0_; private Object name_; private Object insertionPoint_; private Object content_; public static final Descriptors.Descriptor getDescriptor() { return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable.ensureFieldAccessorsInitialized(PluginProtos.CodeGeneratorResponse.File.class, Builder.class); } private Builder() { this.name_ = ""; this.insertionPoint_ = ""; this.content_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.name_ = ""; this.insertionPoint_ = ""; this.content_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (PluginProtos.CodeGeneratorResponse.File.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.name_ = ""; this.bitField0_ &= 0xFFFFFFFE; this.insertionPoint_ = ""; this.bitField0_ &= 0xFFFFFFFD; this.content_ = ""; this.bitField0_ &= 0xFFFFFFFB; return this; } public Descriptors.Descriptor getDescriptorForType() { return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor; } public PluginProtos.CodeGeneratorResponse.File getDefaultInstanceForType() { return PluginProtos.CodeGeneratorResponse.File.getDefaultInstance(); } public PluginProtos.CodeGeneratorResponse.File build() { PluginProtos.CodeGeneratorResponse.File result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public PluginProtos.CodeGeneratorResponse.File buildPartial() { PluginProtos.CodeGeneratorResponse.File result = new PluginProtos.CodeGeneratorResponse.File(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.name_ = this.name_; if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.insertionPoint_ = this.insertionPoint_; if ((from_bitField0_ & 0x4) != 0) to_bitField0_ |= 0x4;  result.content_ = this.content_; result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof PluginProtos.CodeGeneratorResponse.File) return mergeFrom((PluginProtos.CodeGeneratorResponse.File)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(PluginProtos.CodeGeneratorResponse.File other) { if (other == PluginProtos.CodeGeneratorResponse.File.getDefaultInstance()) return this;  if (other.hasName()) { this.bitField0_ |= 0x1; this.name_ = other.name_; onChanged(); }  if (other.hasInsertionPoint()) { this.bitField0_ |= 0x2; this.insertionPoint_ = other.insertionPoint_; onChanged(); }  if (other.hasContent()) { this.bitField0_ |= 0x4; this.content_ = other.content_; onChanged(); }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { PluginProtos.CodeGeneratorResponse.File parsedMessage = null; try { parsedMessage = (PluginProtos.CodeGeneratorResponse.File)PluginProtos.CodeGeneratorResponse.File.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (PluginProtos.CodeGeneratorResponse.File)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasName() { return ((this.bitField0_ & 0x1) != 0); } public String getName() { Object ref = this.name_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.name_ = s;  return s; }  return (String)ref; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; } public Builder setName(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.name_ = value; onChanged(); return this; } public Builder clearName() { this.bitField0_ &= 0xFFFFFFFE; this.name_ = PluginProtos.CodeGeneratorResponse.File.getDefaultInstance().getName(); onChanged(); return this; } public Builder setNameBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.name_ = value; onChanged(); return this; } public boolean hasInsertionPoint() { return ((this.bitField0_ & 0x2) != 0); } public String getInsertionPoint() { Object ref = this.insertionPoint_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.insertionPoint_ = s;  return s; }  return (String)ref; } public ByteString getInsertionPointBytes() { Object ref = this.insertionPoint_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.insertionPoint_ = b; return b; }  return (ByteString)ref; } public Builder setInsertionPoint(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.insertionPoint_ = value; onChanged(); return this; } public Builder clearInsertionPoint() { this.bitField0_ &= 0xFFFFFFFD; this.insertionPoint_ = PluginProtos.CodeGeneratorResponse.File.getDefaultInstance().getInsertionPoint(); onChanged(); return this; } public Builder setInsertionPointBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.insertionPoint_ = value; onChanged(); return this; } public boolean hasContent() { return ((this.bitField0_ & 0x4) != 0); } public String getContent() { Object ref = this.content_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.content_ = s;  return s; }  return (String)ref; } public ByteString getContentBytes() { Object ref = this.content_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.content_ = b; return b; }  return (ByteString)ref; } public Builder setContent(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x4; this.content_ = value; onChanged(); return this; } public Builder clearContent() { this.bitField0_ &= 0xFFFFFFFB; this.content_ = PluginProtos.CodeGeneratorResponse.File.getDefaultInstance().getContent(); onChanged(); return this; } public Builder setContentBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x4; this.content_ = value; onChanged(); return this; } public final Builder setUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.setUnknownFields(unknownFields); } public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.mergeUnknownFields(unknownFields); } } private static final File DEFAULT_INSTANCE = new File(); public static File getDefaultInstance() { return DEFAULT_INSTANCE; } @Deprecated public static final Parser<File> PARSER = (Parser<File>)new AbstractParser<File>() { public PluginProtos.CodeGeneratorResponse.File parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return new PluginProtos.CodeGeneratorResponse.File(input, extensionRegistry); } }
/* 4909 */       ; public static Parser<File> parser() { return PARSER; } public Parser<File> getParserForType() { return PARSER; } public File getDefaultInstanceForType() { return DEFAULT_INSTANCE; } } public boolean hasError() { return ((this.bitField0_ & 0x1) != 0); } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 4910 */       if (isInitialized == 1) return true; 
/* 4911 */       if (isInitialized == 0) return false;
/*      */       
/* 4913 */       this.memoizedIsInitialized = 1;
/* 4914 */       return true; } public String getError() { Object ref = this.error_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.error_ = s;  return s; } public ByteString getErrorBytes() { Object ref = this.error_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.error_ = b; return b; }  return (ByteString)ref; }
/*      */     public List<File> getFileList() { return this.file_; }
/*      */     public List<? extends FileOrBuilder> getFileOrBuilderList() { return (List)this.file_; }
/*      */     public int getFileCount() { return this.file_.size(); }
/*      */     public File getFile(int index) { return this.file_.get(index); }
/*      */     public FileOrBuilder getFileOrBuilder(int index) { return this.file_.get(index); }
/* 4920 */     public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) {
/* 4921 */         GeneratedMessageV3.writeString(output, 1, this.error_);
/*      */       }
/* 4923 */       for (int i = 0; i < this.file_.size(); i++) {
/* 4924 */         output.writeMessage(15, (MessageLite)this.file_.get(i));
/*      */       }
/* 4926 */       this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 4931 */       int size = this.memoizedSize;
/* 4932 */       if (size != -1) return size;
/*      */       
/* 4934 */       size = 0;
/* 4935 */       if ((this.bitField0_ & 0x1) != 0) {
/* 4936 */         size += GeneratedMessageV3.computeStringSize(1, this.error_);
/*      */       }
/* 4938 */       for (int i = 0; i < this.file_.size(); i++) {
/* 4939 */         size += 
/* 4940 */           CodedOutputStream.computeMessageSize(15, (MessageLite)this.file_.get(i));
/*      */       }
/* 4942 */       size += this.unknownFields.getSerializedSize();
/* 4943 */       this.memoizedSize = size;
/* 4944 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 4949 */       if (obj == this) {
/* 4950 */         return true;
/*      */       }
/* 4952 */       if (!(obj instanceof CodeGeneratorResponse)) {
/* 4953 */         return super.equals(obj);
/*      */       }
/* 4955 */       CodeGeneratorResponse other = (CodeGeneratorResponse)obj;
/*      */       
/* 4957 */       if (hasError() != other.hasError()) return false; 
/* 4958 */       if (hasError() && 
/*      */         
/* 4960 */         !getError().equals(other.getError())) return false;
/*      */ 
/*      */       
/* 4963 */       if (!getFileList().equals(other.getFileList())) return false; 
/* 4964 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 4965 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 4970 */       if (this.memoizedHashCode != 0) {
/* 4971 */         return this.memoizedHashCode;
/*      */       }
/* 4973 */       int hash = 41;
/* 4974 */       hash = 19 * hash + getDescriptor().hashCode();
/* 4975 */       if (hasError()) {
/* 4976 */         hash = 37 * hash + 1;
/* 4977 */         hash = 53 * hash + getError().hashCode();
/*      */       } 
/* 4979 */       if (getFileCount() > 0) {
/* 4980 */         hash = 37 * hash + 15;
/* 4981 */         hash = 53 * hash + getFileList().hashCode();
/*      */       } 
/* 4983 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 4984 */       this.memoizedHashCode = hash;
/* 4985 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CodeGeneratorResponse parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 4991 */       return (CodeGeneratorResponse)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CodeGeneratorResponse parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 4997 */       return (CodeGeneratorResponse)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static CodeGeneratorResponse parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 5002 */       return (CodeGeneratorResponse)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CodeGeneratorResponse parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 5008 */       return (CodeGeneratorResponse)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static CodeGeneratorResponse parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 5012 */       return (CodeGeneratorResponse)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CodeGeneratorResponse parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 5018 */       return (CodeGeneratorResponse)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static CodeGeneratorResponse parseFrom(InputStream input) throws IOException {
/* 5022 */       return 
/* 5023 */         (CodeGeneratorResponse)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CodeGeneratorResponse parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 5029 */       return 
/* 5030 */         (CodeGeneratorResponse)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static CodeGeneratorResponse parseDelimitedFrom(InputStream input) throws IOException {
/* 5034 */       return 
/* 5035 */         (CodeGeneratorResponse)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CodeGeneratorResponse parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 5041 */       return 
/* 5042 */         (CodeGeneratorResponse)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static CodeGeneratorResponse parseFrom(CodedInputStream input) throws IOException {
/* 5047 */       return 
/* 5048 */         (CodeGeneratorResponse)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static CodeGeneratorResponse parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 5054 */       return 
/* 5055 */         (CodeGeneratorResponse)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 5059 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 5061 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(CodeGeneratorResponse prototype) {
/* 5064 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 5068 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 5069 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 5075 */       Builder builder = new Builder(parent);
/* 5076 */       return builder;
/*      */     }
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements PluginProtos.CodeGeneratorResponseOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */       
/*      */       private Object error_;
/*      */       private List<PluginProtos.CodeGeneratorResponse.File> file_;
/*      */       private RepeatedFieldBuilderV3<PluginProtos.CodeGeneratorResponse.File, PluginProtos.CodeGeneratorResponse.File.Builder, PluginProtos.CodeGeneratorResponse.FileOrBuilder> fileBuilder_;
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 5091 */         return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 5097 */         return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable
/* 5098 */           .ensureFieldAccessorsInitialized(PluginProtos.CodeGeneratorResponse.class, Builder.class);
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
/*      */       private Builder()
/*      */       {
/* 5280 */         this.error_ = "";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 5424 */         this
/* 5425 */           .file_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.error_ = ""; this.file_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (PluginProtos.CodeGeneratorResponse.alwaysUseFieldBuilders) getFileFieldBuilder();  } public Builder clear() { super.clear(); this.error_ = ""; this.bitField0_ &= 0xFFFFFFFE; if (this.fileBuilder_ == null) { this.file_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFD; } else { this.fileBuilder_.clear(); }  return this; } public Descriptors.Descriptor getDescriptorForType() { return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor; } public PluginProtos.CodeGeneratorResponse getDefaultInstanceForType() { return PluginProtos.CodeGeneratorResponse.getDefaultInstance(); } public PluginProtos.CodeGeneratorResponse build() { PluginProtos.CodeGeneratorResponse result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public PluginProtos.CodeGeneratorResponse buildPartial() { PluginProtos.CodeGeneratorResponse result = new PluginProtos.CodeGeneratorResponse(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.error_ = this.error_; if (this.fileBuilder_ == null) { if ((this.bitField0_ & 0x2) != 0) { this.file_ = Collections.unmodifiableList(this.file_); this.bitField0_ &= 0xFFFFFFFD; }  result.file_ = this.file_; } else { result.file_ = this.fileBuilder_.build(); }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof PluginProtos.CodeGeneratorResponse) return mergeFrom((PluginProtos.CodeGeneratorResponse)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(PluginProtos.CodeGeneratorResponse other) { if (other == PluginProtos.CodeGeneratorResponse.getDefaultInstance()) return this;  if (other.hasError()) { this.bitField0_ |= 0x1; this.error_ = other.error_; onChanged(); }  if (this.fileBuilder_ == null) { if (!other.file_.isEmpty()) { if (this.file_.isEmpty()) { this.file_ = other.file_; this.bitField0_ &= 0xFFFFFFFD; } else { ensureFileIsMutable(); this.file_.addAll(other.file_); }  onChanged(); }  } else if (!other.file_.isEmpty()) { if (this.fileBuilder_.isEmpty()) { this.fileBuilder_.dispose(); this.fileBuilder_ = null; this.file_ = other.file_; this.bitField0_ &= 0xFFFFFFFD; this.fileBuilder_ = PluginProtos.CodeGeneratorResponse.alwaysUseFieldBuilders ? getFileFieldBuilder() : null; } else { this.fileBuilder_.addAllMessages(other.file_); }  }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { PluginProtos.CodeGeneratorResponse parsedMessage = null; try { parsedMessage = (PluginProtos.CodeGeneratorResponse)PluginProtos.CodeGeneratorResponse.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (PluginProtos.CodeGeneratorResponse)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasError() { return ((this.bitField0_ & 0x1) != 0); } public String getError() { Object ref = this.error_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.error_ = s;  return s; }  return (String)ref; } public ByteString getErrorBytes() { Object ref = this.error_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.error_ = b; return b; }  return (ByteString)ref; } public Builder setError(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.error_ = value; onChanged(); return this; } public Builder clearError() { this.bitField0_ &= 0xFFFFFFFE; this.error_ = PluginProtos.CodeGeneratorResponse.getDefaultInstance().getError(); onChanged(); return this; }
/*      */       public Builder setErrorBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.error_ = value; onChanged(); return this; }
/* 5427 */       private void ensureFileIsMutable() { if ((this.bitField0_ & 0x2) == 0) {
/* 5428 */           this.file_ = new ArrayList<>(this.file_);
/* 5429 */           this.bitField0_ |= 0x2;
/*      */         }  }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<PluginProtos.CodeGeneratorResponse.File> getFileList() {
/* 5440 */         if (this.fileBuilder_ == null) {
/* 5441 */           return Collections.unmodifiableList(this.file_);
/*      */         }
/* 5443 */         return this.fileBuilder_.getMessageList();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int getFileCount() {
/* 5450 */         if (this.fileBuilder_ == null) {
/* 5451 */           return this.file_.size();
/*      */         }
/* 5453 */         return this.fileBuilder_.getCount();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public PluginProtos.CodeGeneratorResponse.File getFile(int index) {
/* 5460 */         if (this.fileBuilder_ == null) {
/* 5461 */           return this.file_.get(index);
/*      */         }
/* 5463 */         return (PluginProtos.CodeGeneratorResponse.File)this.fileBuilder_.getMessage(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setFile(int index, PluginProtos.CodeGeneratorResponse.File value) {
/* 5471 */         if (this.fileBuilder_ == null) {
/* 5472 */           if (value == null) {
/* 5473 */             throw new NullPointerException();
/*      */           }
/* 5475 */           ensureFileIsMutable();
/* 5476 */           this.file_.set(index, value);
/* 5477 */           onChanged();
/*      */         } else {
/* 5479 */           this.fileBuilder_.setMessage(index, (AbstractMessage)value);
/*      */         } 
/* 5481 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setFile(int index, PluginProtos.CodeGeneratorResponse.File.Builder builderForValue) {
/* 5488 */         if (this.fileBuilder_ == null) {
/* 5489 */           ensureFileIsMutable();
/* 5490 */           this.file_.set(index, builderForValue.build());
/* 5491 */           onChanged();
/*      */         } else {
/* 5493 */           this.fileBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 5495 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addFile(PluginProtos.CodeGeneratorResponse.File value) {
/* 5501 */         if (this.fileBuilder_ == null) {
/* 5502 */           if (value == null) {
/* 5503 */             throw new NullPointerException();
/*      */           }
/* 5505 */           ensureFileIsMutable();
/* 5506 */           this.file_.add(value);
/* 5507 */           onChanged();
/*      */         } else {
/* 5509 */           this.fileBuilder_.addMessage((AbstractMessage)value);
/*      */         } 
/* 5511 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addFile(int index, PluginProtos.CodeGeneratorResponse.File value) {
/* 5518 */         if (this.fileBuilder_ == null) {
/* 5519 */           if (value == null) {
/* 5520 */             throw new NullPointerException();
/*      */           }
/* 5522 */           ensureFileIsMutable();
/* 5523 */           this.file_.add(index, value);
/* 5524 */           onChanged();
/*      */         } else {
/* 5526 */           this.fileBuilder_.addMessage(index, (AbstractMessage)value);
/*      */         } 
/* 5528 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addFile(PluginProtos.CodeGeneratorResponse.File.Builder builderForValue) {
/* 5535 */         if (this.fileBuilder_ == null) {
/* 5536 */           ensureFileIsMutable();
/* 5537 */           this.file_.add(builderForValue.build());
/* 5538 */           onChanged();
/*      */         } else {
/* 5540 */           this.fileBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*      */         } 
/* 5542 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addFile(int index, PluginProtos.CodeGeneratorResponse.File.Builder builderForValue) {
/* 5549 */         if (this.fileBuilder_ == null) {
/* 5550 */           ensureFileIsMutable();
/* 5551 */           this.file_.add(index, builderForValue.build());
/* 5552 */           onChanged();
/*      */         } else {
/* 5554 */           this.fileBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*      */         } 
/* 5556 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addAllFile(Iterable<? extends PluginProtos.CodeGeneratorResponse.File> values) {
/* 5563 */         if (this.fileBuilder_ == null) {
/* 5564 */           ensureFileIsMutable();
/* 5565 */           AbstractMessageLite.Builder.addAll(values, this.file_);
/*      */           
/* 5567 */           onChanged();
/*      */         } else {
/* 5569 */           this.fileBuilder_.addAllMessages(values);
/*      */         } 
/* 5571 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearFile() {
/* 5577 */         if (this.fileBuilder_ == null) {
/* 5578 */           this.file_ = Collections.emptyList();
/* 5579 */           this.bitField0_ &= 0xFFFFFFFD;
/* 5580 */           onChanged();
/*      */         } else {
/* 5582 */           this.fileBuilder_.clear();
/*      */         } 
/* 5584 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder removeFile(int index) {
/* 5590 */         if (this.fileBuilder_ == null) {
/* 5591 */           ensureFileIsMutable();
/* 5592 */           this.file_.remove(index);
/* 5593 */           onChanged();
/*      */         } else {
/* 5595 */           this.fileBuilder_.remove(index);
/*      */         } 
/* 5597 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public PluginProtos.CodeGeneratorResponse.File.Builder getFileBuilder(int index) {
/* 5604 */         return (PluginProtos.CodeGeneratorResponse.File.Builder)getFileFieldBuilder().getBuilder(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public PluginProtos.CodeGeneratorResponse.FileOrBuilder getFileOrBuilder(int index) {
/* 5611 */         if (this.fileBuilder_ == null)
/* 5612 */           return this.file_.get(index); 
/* 5613 */         return (PluginProtos.CodeGeneratorResponse.FileOrBuilder)this.fileBuilder_.getMessageOrBuilder(index);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<? extends PluginProtos.CodeGeneratorResponse.FileOrBuilder> getFileOrBuilderList() {
/* 5621 */         if (this.fileBuilder_ != null) {
/* 5622 */           return this.fileBuilder_.getMessageOrBuilderList();
/*      */         }
/* 5624 */         return Collections.unmodifiableList((List)this.file_);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public PluginProtos.CodeGeneratorResponse.File.Builder addFileBuilder() {
/* 5631 */         return (PluginProtos.CodeGeneratorResponse.File.Builder)getFileFieldBuilder().addBuilder(
/* 5632 */             (AbstractMessage)PluginProtos.CodeGeneratorResponse.File.getDefaultInstance());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public PluginProtos.CodeGeneratorResponse.File.Builder addFileBuilder(int index) {
/* 5639 */         return (PluginProtos.CodeGeneratorResponse.File.Builder)getFileFieldBuilder().addBuilder(index, 
/* 5640 */             (AbstractMessage)PluginProtos.CodeGeneratorResponse.File.getDefaultInstance());
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public List<PluginProtos.CodeGeneratorResponse.File.Builder> getFileBuilderList() {
/* 5647 */         return getFileFieldBuilder().getBuilderList();
/*      */       }
/*      */ 
/*      */       
/*      */       private RepeatedFieldBuilderV3<PluginProtos.CodeGeneratorResponse.File, PluginProtos.CodeGeneratorResponse.File.Builder, PluginProtos.CodeGeneratorResponse.FileOrBuilder> getFileFieldBuilder() {
/* 5652 */         if (this.fileBuilder_ == null) {
/* 5653 */           this
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 5658 */             .fileBuilder_ = new RepeatedFieldBuilderV3(this.file_, ((this.bitField0_ & 0x2) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 5659 */           this.file_ = null;
/*      */         } 
/* 5661 */         return this.fileBuilder_;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 5666 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 5672 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5682 */     private static final CodeGeneratorResponse DEFAULT_INSTANCE = new CodeGeneratorResponse();
/*      */ 
/*      */     
/*      */     public static CodeGeneratorResponse getDefaultInstance() {
/* 5686 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 5690 */     public static final Parser<CodeGeneratorResponse> PARSER = (Parser<CodeGeneratorResponse>)new AbstractParser<CodeGeneratorResponse>()
/*      */       {
/*      */ 
/*      */         
/*      */         public PluginProtos.CodeGeneratorResponse parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 5696 */           return new PluginProtos.CodeGeneratorResponse(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<CodeGeneratorResponse> parser() {
/* 5701 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<CodeGeneratorResponse> getParserForType() {
/* 5706 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public CodeGeneratorResponse getDefaultInstanceForType() {
/* 5711 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */ 
/*      */     
/*      */     public static interface FileOrBuilder
/*      */       extends MessageOrBuilder
/*      */     {
/*      */       boolean hasName();
/*      */       
/*      */       String getName();
/*      */       
/*      */       ByteString getNameBytes();
/*      */       
/*      */       boolean hasInsertionPoint();
/*      */       
/*      */       String getInsertionPoint();
/*      */       
/*      */       ByteString getInsertionPointBytes();
/*      */       
/*      */       boolean hasContent();
/*      */       
/*      */       String getContent();
/*      */       
/*      */       ByteString getContentBytes();
/*      */     }
/*      */   }
/*      */   
/*      */   public static Descriptors.FileDescriptor getDescriptor() {
/* 5739 */     return descriptor;
/*      */   }
/*      */ 
/*      */   
/*      */   static {
/* 5744 */     String[] descriptorData = { "\n%google/protobuf/compiler/plugin.proto\022\030google.protobuf.compiler\032 google/protobuf/descriptor.proto\"F\n\007Version\022\r\n\005major\030\001 \001(\005\022\r\n\005minor\030\002 \001(\005\022\r\n\005patch\030\003 \001(\005\022\016\n\006suffix\030\004 \001(\t\"º\001\n\024CodeGeneratorRequest\022\030\n\020file_to_generate\030\001 \003(\t\022\021\n\tparameter\030\002 \001(\t\0228\n\nproto_file\030\017 \003(\0132$.google.protobuf.FileDescriptorProto\022;\n\020compiler_version\030\003 \001(\0132!.google.protobuf.compiler.Version\"ª\001\n\025CodeGeneratorResponse\022\r\n\005error\030\001 \001(\t\022B\n\004file\030\017 \003(\01324.google.protobuf.compiler.CodeGeneratorResponse.File\032>\n\004File\022\f\n\004name\030\001 \001(\t\022\027\n\017insertion_point\030\002 \001(\t\022\017\n\007content\030\017 \001(\tBg\n\034com.google.protobuf.compilerB\fPluginProtosZ9github.com/golang/protobuf/protoc-gen-go/plugin;plugin_go" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5763 */     descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] {
/*      */           
/* 5765 */           DescriptorProtos.getDescriptor()
/*      */         });
/*      */     
/* 5768 */     internal_static_google_protobuf_compiler_Version_descriptor = getDescriptor().getMessageTypes().get(0);
/* 5769 */     internal_static_google_protobuf_compiler_Version_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_compiler_Version_descriptor, new String[] { "Major", "Minor", "Patch", "Suffix" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5774 */     internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor = getDescriptor().getMessageTypes().get(1);
/* 5775 */     internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor, new String[] { "FileToGenerate", "Parameter", "ProtoFile", "CompilerVersion" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5780 */     internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor = getDescriptor().getMessageTypes().get(2);
/* 5781 */     internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor, new String[] { "Error", "File" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5786 */     internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor = internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor.getNestedTypes().get(0);
/* 5787 */     internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor, new String[] { "Name", "InsertionPoint", "Content" });
/*      */ 
/*      */ 
/*      */     
/* 5791 */     DescriptorProtos.getDescriptor();
/*      */   } }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\compiler\PluginProtos.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */