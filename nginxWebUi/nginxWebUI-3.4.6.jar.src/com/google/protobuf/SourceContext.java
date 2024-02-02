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
/*     */ public final class SourceContext
/*     */   extends GeneratedMessageV3
/*     */   implements SourceContextOrBuilder
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   public static final int FILE_NAME_FIELD_NUMBER = 1;
/*     */   private volatile Object fileName_;
/*     */   private byte memoizedIsInitialized;
/*     */   
/*     */   private SourceContext(GeneratedMessageV3.Builder<?> builder) {
/*  21 */     super(builder);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 141 */     this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new SourceContext(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private SourceContext() { this.memoizedIsInitialized = -1; this.fileName_ = ""; }
/*     */   private SourceContext(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null)
/*     */       throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { String s; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: s = input.readStringRequireUtf8(); this.fileName_ = s; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag))
/* 144 */           done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 145 */     if (isInitialized == 1) return true; 
/* 146 */     if (isInitialized == 0) return false;
/*     */     
/* 148 */     this.memoizedIsInitialized = 1;
/* 149 */     return true; } public static final Descriptors.Descriptor getDescriptor() { return SourceContextProto.internal_static_google_protobuf_SourceContext_descriptor; }
/*     */   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return SourceContextProto.internal_static_google_protobuf_SourceContext_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)SourceContext.class, (Class)Builder.class); }
/*     */   public String getFileName() { Object ref = this.fileName_; if (ref instanceof String)
/*     */       return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.fileName_ = s; return s; }
/*     */   public ByteString getFileNameBytes() { Object ref = this.fileName_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.fileName_ = b; return b; }
/*     */      return (ByteString)ref; }
/* 155 */   public void writeTo(CodedOutputStream output) throws IOException { if (!getFileNameBytes().isEmpty()) {
/* 156 */       GeneratedMessageV3.writeString(output, 1, this.fileName_);
/*     */     }
/* 158 */     this.unknownFields.writeTo(output); }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/* 163 */     int size = this.memoizedSize;
/* 164 */     if (size != -1) return size;
/*     */     
/* 166 */     size = 0;
/* 167 */     if (!getFileNameBytes().isEmpty()) {
/* 168 */       size += GeneratedMessageV3.computeStringSize(1, this.fileName_);
/*     */     }
/* 170 */     size += this.unknownFields.getSerializedSize();
/* 171 */     this.memoizedSize = size;
/* 172 */     return size;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 177 */     if (obj == this) {
/* 178 */       return true;
/*     */     }
/* 180 */     if (!(obj instanceof SourceContext)) {
/* 181 */       return super.equals(obj);
/*     */     }
/* 183 */     SourceContext other = (SourceContext)obj;
/*     */ 
/*     */     
/* 186 */     if (!getFileName().equals(other.getFileName())) return false; 
/* 187 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 188 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 193 */     if (this.memoizedHashCode != 0) {
/* 194 */       return this.memoizedHashCode;
/*     */     }
/* 196 */     int hash = 41;
/* 197 */     hash = 19 * hash + getDescriptor().hashCode();
/* 198 */     hash = 37 * hash + 1;
/* 199 */     hash = 53 * hash + getFileName().hashCode();
/* 200 */     hash = 29 * hash + this.unknownFields.hashCode();
/* 201 */     this.memoizedHashCode = hash;
/* 202 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static SourceContext parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 208 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static SourceContext parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 214 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static SourceContext parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 219 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static SourceContext parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 225 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static SourceContext parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 229 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static SourceContext parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 235 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static SourceContext parseFrom(InputStream input) throws IOException {
/* 239 */     return 
/* 240 */       GeneratedMessageV3.<SourceContext>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static SourceContext parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 246 */     return 
/* 247 */       GeneratedMessageV3.<SourceContext>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static SourceContext parseDelimitedFrom(InputStream input) throws IOException {
/* 251 */     return 
/* 252 */       GeneratedMessageV3.<SourceContext>parseDelimitedWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static SourceContext parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 258 */     return 
/* 259 */       GeneratedMessageV3.<SourceContext>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static SourceContext parseFrom(CodedInputStream input) throws IOException {
/* 264 */     return 
/* 265 */       GeneratedMessageV3.<SourceContext>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static SourceContext parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 271 */     return 
/* 272 */       GeneratedMessageV3.<SourceContext>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public Builder newBuilderForType() {
/* 276 */     return newBuilder();
/*     */   } public static Builder newBuilder() {
/* 278 */     return DEFAULT_INSTANCE.toBuilder();
/*     */   }
/*     */   public static Builder newBuilder(SourceContext prototype) {
/* 281 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*     */   }
/*     */   
/*     */   public Builder toBuilder() {
/* 285 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 286 */       .mergeFrom(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 292 */     Builder builder = new Builder(parent);
/* 293 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Builder
/*     */     extends GeneratedMessageV3.Builder<Builder>
/*     */     implements SourceContextOrBuilder
/*     */   {
/*     */     private Object fileName_;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final Descriptors.Descriptor getDescriptor() {
/* 309 */       return SourceContextProto.internal_static_google_protobuf_SourceContext_descriptor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 315 */       return SourceContextProto.internal_static_google_protobuf_SourceContext_fieldAccessorTable
/* 316 */         .ensureFieldAccessorsInitialized((Class)SourceContext.class, (Class)Builder.class);
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
/*     */     private Builder()
/*     */     {
/* 448 */       this.fileName_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.fileName_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (GeneratedMessageV3.alwaysUseFieldBuilders); }
/*     */     public Builder clear() { super.clear(); this.fileName_ = ""; return this; }
/*     */     public Descriptors.Descriptor getDescriptorForType() { return SourceContextProto.internal_static_google_protobuf_SourceContext_descriptor; }
/*     */     public SourceContext getDefaultInstanceForType() { return SourceContext.getDefaultInstance(); }
/*     */     public SourceContext build() { SourceContext result = buildPartial(); if (!result.isInitialized())
/*     */         throw newUninitializedMessageException(result);  return result; }
/*     */     public SourceContext buildPartial() { SourceContext result = new SourceContext(this); result.fileName_ = this.fileName_;
/*     */       onBuilt();
/*     */       return result; }
/*     */     public Builder clone() { return super.clone(); }
/*     */     public Builder setField(Descriptors.FieldDescriptor field, Object value) { return super.setField(field, value); }
/* 459 */     public String getFileName() { Object ref = this.fileName_;
/* 460 */       if (!(ref instanceof String)) {
/* 461 */         ByteString bs = (ByteString)ref;
/*     */         
/* 463 */         String s = bs.toStringUtf8();
/* 464 */         this.fileName_ = s;
/* 465 */         return s;
/*     */       } 
/* 467 */       return (String)ref; }
/*     */     
/*     */     public Builder clearField(Descriptors.FieldDescriptor field) {
/*     */       return super.clearField(field);
/*     */     }
/*     */     public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/*     */       return super.clearOneof(oneof);
/*     */     }
/*     */     public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/*     */       return super.setRepeatedField(field, index, value);
/*     */     }
/*     */     public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/*     */       return super.addRepeatedField(field, value);
/*     */     }
/* 481 */     public ByteString getFileNameBytes() { Object ref = this.fileName_;
/* 482 */       if (ref instanceof String) {
/*     */         
/* 484 */         ByteString b = ByteString.copyFromUtf8((String)ref);
/*     */         
/* 486 */         this.fileName_ = b;
/* 487 */         return b;
/*     */       } 
/* 489 */       return (ByteString)ref; } public Builder mergeFrom(Message other) { if (other instanceof SourceContext)
/*     */         return mergeFrom((SourceContext)other);  super.mergeFrom(other); return this; }
/*     */     public Builder mergeFrom(SourceContext other) { if (other == SourceContext.getDefaultInstance())
/*     */         return this;  if (!other.getFileName().isEmpty()) {
/*     */         this.fileName_ = other.fileName_; onChanged();
/*     */       }  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*     */     public final boolean isInitialized() { return true; }
/*     */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { SourceContext parsedMessage = null; try {
/*     */         parsedMessage = SourceContext.PARSER.parsePartialFrom(input, extensionRegistry);
/*     */       } catch (InvalidProtocolBufferException e) {
/*     */         parsedMessage = (SourceContext)e.getUnfinishedMessage(); throw e.unwrapIOException();
/*     */       } finally {
/*     */         if (parsedMessage != null)
/*     */           mergeFrom(parsedMessage); 
/*     */       }  return this; }
/* 504 */     public Builder setFileName(String value) { if (value == null) {
/* 505 */         throw new NullPointerException();
/*     */       }
/*     */       
/* 508 */       this.fileName_ = value;
/* 509 */       onChanged();
/* 510 */       return this; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder clearFileName() {
/* 523 */       this.fileName_ = SourceContext.getDefaultInstance().getFileName();
/* 524 */       onChanged();
/* 525 */       return this;
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
/*     */     public Builder setFileNameBytes(ByteString value) {
/* 539 */       if (value == null) {
/* 540 */         throw new NullPointerException();
/*     */       }
/* 542 */       AbstractMessageLite.checkByteStringIsUtf8(value);
/*     */       
/* 544 */       this.fileName_ = value;
/* 545 */       onChanged();
/* 546 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 551 */       return super.setUnknownFields(unknownFields);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 557 */       return super.mergeUnknownFields(unknownFields);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 567 */   private static final SourceContext DEFAULT_INSTANCE = new SourceContext();
/*     */ 
/*     */   
/*     */   public static SourceContext getDefaultInstance() {
/* 571 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ 
/*     */   
/* 575 */   private static final Parser<SourceContext> PARSER = new AbstractParser<SourceContext>()
/*     */     {
/*     */ 
/*     */       
/*     */       public SourceContext parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */       {
/* 581 */         return new SourceContext(input, extensionRegistry);
/*     */       }
/*     */     };
/*     */   
/*     */   public static Parser<SourceContext> parser() {
/* 586 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<SourceContext> getParserForType() {
/* 591 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public SourceContext getDefaultInstanceForType() {
/* 596 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\SourceContext.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */