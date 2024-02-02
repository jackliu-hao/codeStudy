/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Mixin
/*     */   extends GeneratedMessageV3
/*     */   implements MixinOrBuilder
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   public static final int NAME_FIELD_NUMBER = 1;
/*     */   private volatile Object name_;
/*     */   public static final int ROOT_FIELD_NUMBER = 2;
/*     */   private volatile Object root_;
/*     */   private byte memoizedIsInitialized;
/*     */   
/*     */   private Mixin(GeneratedMessageV3.Builder<?> builder) {
/*  81 */     super(builder);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 252 */     this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Mixin(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Mixin() { this.memoizedIsInitialized = -1; this.name_ = ""; this.root_ = ""; }
/*     */   private Mixin(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { String s; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: s = input.readStringRequireUtf8(); this.name_ = s; continue;case 18: s = input.readStringRequireUtf8(); this.root_ = s; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag))
/*     */           done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/* 255 */   public static final Descriptors.Descriptor getDescriptor() { return ApiProto.internal_static_google_protobuf_Mixin_descriptor; } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 256 */     if (isInitialized == 1) return true; 
/* 257 */     if (isInitialized == 0) return false;
/*     */     
/* 259 */     this.memoizedIsInitialized = 1;
/* 260 */     return true; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return ApiProto.internal_static_google_protobuf_Mixin_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Mixin.class, (Class)Builder.class); }
/*     */   public String getName() { Object ref = this.name_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.name_ = s; return s; }
/*     */   public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; }
/*     */   public String getRoot() { Object ref = this.root_; if (ref instanceof String)
/*     */       return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.root_ = s; return s; }
/*     */   public ByteString getRootBytes() { Object ref = this.root_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.root_ = b; return b; }  return (ByteString)ref; }
/* 266 */   public void writeTo(CodedOutputStream output) throws IOException { if (!getNameBytes().isEmpty()) {
/* 267 */       GeneratedMessageV3.writeString(output, 1, this.name_);
/*     */     }
/* 269 */     if (!getRootBytes().isEmpty()) {
/* 270 */       GeneratedMessageV3.writeString(output, 2, this.root_);
/*     */     }
/* 272 */     this.unknownFields.writeTo(output); }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/* 277 */     int size = this.memoizedSize;
/* 278 */     if (size != -1) return size;
/*     */     
/* 280 */     size = 0;
/* 281 */     if (!getNameBytes().isEmpty()) {
/* 282 */       size += GeneratedMessageV3.computeStringSize(1, this.name_);
/*     */     }
/* 284 */     if (!getRootBytes().isEmpty()) {
/* 285 */       size += GeneratedMessageV3.computeStringSize(2, this.root_);
/*     */     }
/* 287 */     size += this.unknownFields.getSerializedSize();
/* 288 */     this.memoizedSize = size;
/* 289 */     return size;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 294 */     if (obj == this) {
/* 295 */       return true;
/*     */     }
/* 297 */     if (!(obj instanceof Mixin)) {
/* 298 */       return super.equals(obj);
/*     */     }
/* 300 */     Mixin other = (Mixin)obj;
/*     */ 
/*     */     
/* 303 */     if (!getName().equals(other.getName())) return false;
/*     */     
/* 305 */     if (!getRoot().equals(other.getRoot())) return false; 
/* 306 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 307 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 312 */     if (this.memoizedHashCode != 0) {
/* 313 */       return this.memoizedHashCode;
/*     */     }
/* 315 */     int hash = 41;
/* 316 */     hash = 19 * hash + getDescriptor().hashCode();
/* 317 */     hash = 37 * hash + 1;
/* 318 */     hash = 53 * hash + getName().hashCode();
/* 319 */     hash = 37 * hash + 2;
/* 320 */     hash = 53 * hash + getRoot().hashCode();
/* 321 */     hash = 29 * hash + this.unknownFields.hashCode();
/* 322 */     this.memoizedHashCode = hash;
/* 323 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Mixin parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 329 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Mixin parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 335 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Mixin parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 340 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Mixin parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 346 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Mixin parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 350 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Mixin parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 356 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Mixin parseFrom(InputStream input) throws IOException {
/* 360 */     return 
/* 361 */       GeneratedMessageV3.<Mixin>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Mixin parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 367 */     return 
/* 368 */       GeneratedMessageV3.<Mixin>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Mixin parseDelimitedFrom(InputStream input) throws IOException {
/* 372 */     return 
/* 373 */       GeneratedMessageV3.<Mixin>parseDelimitedWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Mixin parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 379 */     return 
/* 380 */       GeneratedMessageV3.<Mixin>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Mixin parseFrom(CodedInputStream input) throws IOException {
/* 385 */     return 
/* 386 */       GeneratedMessageV3.<Mixin>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Mixin parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 392 */     return 
/* 393 */       GeneratedMessageV3.<Mixin>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public Builder newBuilderForType() {
/* 397 */     return newBuilder();
/*     */   } public static Builder newBuilder() {
/* 399 */     return DEFAULT_INSTANCE.toBuilder();
/*     */   }
/*     */   public static Builder newBuilder(Mixin prototype) {
/* 402 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*     */   }
/*     */   
/*     */   public Builder toBuilder() {
/* 406 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 407 */       .mergeFrom(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 413 */     Builder builder = new Builder(parent);
/* 414 */     return builder;
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
/*     */   public static final class Builder
/*     */     extends GeneratedMessageV3.Builder<Builder>
/*     */     implements MixinOrBuilder
/*     */   {
/*     */     private Object name_;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private Object root_;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/* 490 */       return ApiProto.internal_static_google_protobuf_Mixin_descriptor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 496 */       return ApiProto.internal_static_google_protobuf_Mixin_fieldAccessorTable
/* 497 */         .ensureFieldAccessorsInitialized((Class)Mixin.class, (Class)Builder.class);
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
/*     */     private Builder()
/*     */     {
/* 636 */       this.name_ = "";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 732 */       this.root_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.name_ = ""; this.root_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (GeneratedMessageV3.alwaysUseFieldBuilders); }
/*     */     public Builder clear() { super.clear(); this.name_ = ""; this.root_ = ""; return this; }
/*     */     public Descriptors.Descriptor getDescriptorForType() { return ApiProto.internal_static_google_protobuf_Mixin_descriptor; }
/*     */     public Mixin getDefaultInstanceForType() { return Mixin.getDefaultInstance(); }
/*     */     public Mixin build() { Mixin result = buildPartial(); if (!result.isInitialized())
/*     */         throw newUninitializedMessageException(result);  return result; }
/*     */     public Mixin buildPartial() { Mixin result = new Mixin(this); result.name_ = this.name_; result.root_ = this.root_; onBuilt(); return result; }
/*     */     public Builder clone() { return super.clone(); }
/*     */     public Builder setField(Descriptors.FieldDescriptor field, Object value) { return super.setField(field, value); }
/*     */     public Builder clearField(Descriptors.FieldDescriptor field) { return super.clearField(field); }
/*     */     public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return super.clearOneof(oneof); }
/* 743 */     public String getRoot() { Object ref = this.root_;
/* 744 */       if (!(ref instanceof String)) {
/* 745 */         ByteString bs = (ByteString)ref;
/*     */         
/* 747 */         String s = bs.toStringUtf8();
/* 748 */         this.root_ = s;
/* 749 */         return s;
/*     */       } 
/* 751 */       return (String)ref; } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return super.setRepeatedField(field, index, value); }
/*     */     public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return super.addRepeatedField(field, value); }
/*     */     public Builder mergeFrom(Message other) { if (other instanceof Mixin)
/*     */         return mergeFrom((Mixin)other);  super.mergeFrom(other); return this; }
/*     */     public Builder mergeFrom(Mixin other) { if (other == Mixin.getDefaultInstance())
/*     */         return this;  if (!other.getName().isEmpty()) {
/*     */         this.name_ = other.name_; onChanged();
/*     */       }  if (!other.getRoot().isEmpty()) {
/*     */         this.root_ = other.root_; onChanged();
/*     */       } 
/*     */       mergeUnknownFields(other.unknownFields);
/*     */       onChanged();
/*     */       return this; }
/*     */     public final boolean isInitialized() { return true; }
/* 765 */     public ByteString getRootBytes() { Object ref = this.root_;
/* 766 */       if (ref instanceof String) {
/*     */         
/* 768 */         ByteString b = ByteString.copyFromUtf8((String)ref);
/*     */         
/* 770 */         this.root_ = b;
/* 771 */         return b;
/*     */       } 
/* 773 */       return (ByteString)ref; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { Mixin parsedMessage = null; try { parsedMessage = Mixin.PARSER.parsePartialFrom(input, extensionRegistry); }
/*     */       catch (InvalidProtocolBufferException e) { parsedMessage = (Mixin)e.getUnfinishedMessage(); throw e.unwrapIOException(); }
/*     */       finally { if (parsedMessage != null)
/*     */           mergeFrom(parsedMessage);  }
/*     */        return this; }
/*     */     public String getName() { Object ref = this.name_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.name_ = s; return s; }
/*     */        return (String)ref; }
/*     */     public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) {
/*     */         ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b;
/*     */       }  return (ByteString)ref; }
/*     */     public Builder setName(String value) { if (value == null)
/*     */         throw new NullPointerException();  this.name_ = value; onChanged(); return this; }
/*     */     public Builder clearName() { this.name_ = Mixin.getDefaultInstance().getName(); onChanged(); return this; }
/*     */     public Builder setNameBytes(ByteString value) { if (value == null)
/*     */         throw new NullPointerException();  AbstractMessageLite.checkByteStringIsUtf8(value); this.name_ = value; onChanged(); return this; }
/* 788 */     public Builder setRoot(String value) { if (value == null) {
/* 789 */         throw new NullPointerException();
/*     */       }
/*     */       
/* 792 */       this.root_ = value;
/* 793 */       onChanged();
/* 794 */       return this; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder clearRoot() {
/* 807 */       this.root_ = Mixin.getDefaultInstance().getRoot();
/* 808 */       onChanged();
/* 809 */       return this;
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
/*     */     public Builder setRootBytes(ByteString value) {
/* 823 */       if (value == null) {
/* 824 */         throw new NullPointerException();
/*     */       }
/* 826 */       AbstractMessageLite.checkByteStringIsUtf8(value);
/*     */       
/* 828 */       this.root_ = value;
/* 829 */       onChanged();
/* 830 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 835 */       return super.setUnknownFields(unknownFields);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 841 */       return super.mergeUnknownFields(unknownFields);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 851 */   private static final Mixin DEFAULT_INSTANCE = new Mixin();
/*     */ 
/*     */   
/*     */   public static Mixin getDefaultInstance() {
/* 855 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ 
/*     */   
/* 859 */   private static final Parser<Mixin> PARSER = new AbstractParser<Mixin>()
/*     */     {
/*     */ 
/*     */       
/*     */       public Mixin parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */       {
/* 865 */         return new Mixin(input, extensionRegistry);
/*     */       }
/*     */     };
/*     */   
/*     */   public static Parser<Mixin> parser() {
/* 870 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<Mixin> getParserForType() {
/* 875 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Mixin getDefaultInstanceForType() {
/* 880 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Mixin.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */