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
/*     */ public final class UInt32Value
/*     */   extends GeneratedMessageV3
/*     */   implements UInt32ValueOrBuilder
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   public static final int VALUE_FIELD_NUMBER = 1;
/*     */   private int value_;
/*     */   private byte memoizedIsInitialized;
/*     */   
/*     */   private UInt32Value(GeneratedMessageV3.Builder<?> builder) {
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
/* 107 */     this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new UInt32Value(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private UInt32Value() { this.memoizedIsInitialized = -1; }
/*     */   private UInt32Value(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null)
/*     */       throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.value_ = input.readUInt32(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag))
/* 110 */           done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 111 */     if (isInitialized == 1) return true; 
/* 112 */     if (isInitialized == 0) return false;
/*     */     
/* 114 */     this.memoizedIsInitialized = 1;
/* 115 */     return true; } public static final Descriptors.Descriptor getDescriptor() { return WrappersProto.internal_static_google_protobuf_UInt32Value_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*     */     return WrappersProto.internal_static_google_protobuf_UInt32Value_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)UInt32Value.class, (Class)Builder.class);
/*     */   } public int getValue() {
/*     */     return this.value_;
/*     */   }
/*     */   public void writeTo(CodedOutputStream output) throws IOException {
/* 121 */     if (this.value_ != 0) {
/* 122 */       output.writeUInt32(1, this.value_);
/*     */     }
/* 124 */     this.unknownFields.writeTo(output);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/* 129 */     int size = this.memoizedSize;
/* 130 */     if (size != -1) return size;
/*     */     
/* 132 */     size = 0;
/* 133 */     if (this.value_ != 0) {
/* 134 */       size += 
/* 135 */         CodedOutputStream.computeUInt32Size(1, this.value_);
/*     */     }
/* 137 */     size += this.unknownFields.getSerializedSize();
/* 138 */     this.memoizedSize = size;
/* 139 */     return size;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 144 */     if (obj == this) {
/* 145 */       return true;
/*     */     }
/* 147 */     if (!(obj instanceof UInt32Value)) {
/* 148 */       return super.equals(obj);
/*     */     }
/* 150 */     UInt32Value other = (UInt32Value)obj;
/*     */     
/* 152 */     if (getValue() != other
/* 153 */       .getValue()) return false; 
/* 154 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 155 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 160 */     if (this.memoizedHashCode != 0) {
/* 161 */       return this.memoizedHashCode;
/*     */     }
/* 163 */     int hash = 41;
/* 164 */     hash = 19 * hash + getDescriptor().hashCode();
/* 165 */     hash = 37 * hash + 1;
/* 166 */     hash = 53 * hash + getValue();
/* 167 */     hash = 29 * hash + this.unknownFields.hashCode();
/* 168 */     this.memoizedHashCode = hash;
/* 169 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static UInt32Value parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 175 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static UInt32Value parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 181 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static UInt32Value parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 186 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static UInt32Value parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 192 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static UInt32Value parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 196 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static UInt32Value parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 202 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static UInt32Value parseFrom(InputStream input) throws IOException {
/* 206 */     return 
/* 207 */       GeneratedMessageV3.<UInt32Value>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static UInt32Value parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 213 */     return 
/* 214 */       GeneratedMessageV3.<UInt32Value>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static UInt32Value parseDelimitedFrom(InputStream input) throws IOException {
/* 218 */     return 
/* 219 */       GeneratedMessageV3.<UInt32Value>parseDelimitedWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static UInt32Value parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 225 */     return 
/* 226 */       GeneratedMessageV3.<UInt32Value>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static UInt32Value parseFrom(CodedInputStream input) throws IOException {
/* 231 */     return 
/* 232 */       GeneratedMessageV3.<UInt32Value>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static UInt32Value parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 238 */     return 
/* 239 */       GeneratedMessageV3.<UInt32Value>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public Builder newBuilderForType() {
/* 243 */     return newBuilder();
/*     */   } public static Builder newBuilder() {
/* 245 */     return DEFAULT_INSTANCE.toBuilder();
/*     */   }
/*     */   public static Builder newBuilder(UInt32Value prototype) {
/* 248 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*     */   }
/*     */   
/*     */   public Builder toBuilder() {
/* 252 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 253 */       .mergeFrom(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 259 */     Builder builder = new Builder(parent);
/* 260 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Builder
/*     */     extends GeneratedMessageV3.Builder<Builder>
/*     */     implements UInt32ValueOrBuilder
/*     */   {
/*     */     private int value_;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final Descriptors.Descriptor getDescriptor() {
/* 276 */       return WrappersProto.internal_static_google_protobuf_UInt32Value_descriptor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 282 */       return WrappersProto.internal_static_google_protobuf_UInt32Value_fieldAccessorTable
/* 283 */         .ensureFieldAccessorsInitialized((Class)UInt32Value.class, (Class)Builder.class);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private Builder() {
/* 289 */       maybeForceBuilderInitialization();
/*     */     }
/*     */ 
/*     */     
/*     */     private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 294 */       super(parent);
/* 295 */       maybeForceBuilderInitialization();
/*     */     }
/*     */     private void maybeForceBuilderInitialization() {
/* 298 */       if (GeneratedMessageV3.alwaysUseFieldBuilders);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder clear() {
/* 304 */       super.clear();
/* 305 */       this.value_ = 0;
/*     */       
/* 307 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Descriptors.Descriptor getDescriptorForType() {
/* 313 */       return WrappersProto.internal_static_google_protobuf_UInt32Value_descriptor;
/*     */     }
/*     */ 
/*     */     
/*     */     public UInt32Value getDefaultInstanceForType() {
/* 318 */       return UInt32Value.getDefaultInstance();
/*     */     }
/*     */ 
/*     */     
/*     */     public UInt32Value build() {
/* 323 */       UInt32Value result = buildPartial();
/* 324 */       if (!result.isInitialized()) {
/* 325 */         throw newUninitializedMessageException(result);
/*     */       }
/* 327 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public UInt32Value buildPartial() {
/* 332 */       UInt32Value result = new UInt32Value(this);
/* 333 */       result.value_ = this.value_;
/* 334 */       onBuilt();
/* 335 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clone() {
/* 340 */       return super.clone();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 346 */       return super.setField(field, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearField(Descriptors.FieldDescriptor field) {
/* 351 */       return super.clearField(field);
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 356 */       return super.clearOneof(oneof);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 362 */       return super.setRepeatedField(field, index, value);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 368 */       return super.addRepeatedField(field, value);
/*     */     }
/*     */     
/*     */     public Builder mergeFrom(Message other) {
/* 372 */       if (other instanceof UInt32Value) {
/* 373 */         return mergeFrom((UInt32Value)other);
/*     */       }
/* 375 */       super.mergeFrom(other);
/* 376 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder mergeFrom(UInt32Value other) {
/* 381 */       if (other == UInt32Value.getDefaultInstance()) return this; 
/* 382 */       if (other.getValue() != 0) {
/* 383 */         setValue(other.getValue());
/*     */       }
/* 385 */       mergeUnknownFields(other.unknownFields);
/* 386 */       onChanged();
/* 387 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final boolean isInitialized() {
/* 392 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 400 */       UInt32Value parsedMessage = null;
/*     */       try {
/* 402 */         parsedMessage = UInt32Value.PARSER.parsePartialFrom(input, extensionRegistry);
/* 403 */       } catch (InvalidProtocolBufferException e) {
/* 404 */         parsedMessage = (UInt32Value)e.getUnfinishedMessage();
/* 405 */         throw e.unwrapIOException();
/*     */       } finally {
/* 407 */         if (parsedMessage != null) {
/* 408 */           mergeFrom(parsedMessage);
/*     */         }
/*     */       } 
/* 411 */       return this;
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
/*     */     public int getValue() {
/* 424 */       return this.value_;
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
/*     */     public Builder setValue(int value) {
/* 437 */       this.value_ = value;
/* 438 */       onChanged();
/* 439 */       return this;
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
/*     */     public Builder clearValue() {
/* 451 */       this.value_ = 0;
/* 452 */       onChanged();
/* 453 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 458 */       return super.setUnknownFields(unknownFields);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 464 */       return super.mergeUnknownFields(unknownFields);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 474 */   private static final UInt32Value DEFAULT_INSTANCE = new UInt32Value();
/*     */ 
/*     */   
/*     */   public static UInt32Value getDefaultInstance() {
/* 478 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */   
/*     */   public static UInt32Value of(int value) {
/* 482 */     return newBuilder().setValue(value).build();
/*     */   }
/*     */ 
/*     */   
/* 486 */   private static final Parser<UInt32Value> PARSER = new AbstractParser<UInt32Value>()
/*     */     {
/*     */ 
/*     */       
/*     */       public UInt32Value parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */       {
/* 492 */         return new UInt32Value(input, extensionRegistry);
/*     */       }
/*     */     };
/*     */   
/*     */   public static Parser<UInt32Value> parser() {
/* 497 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<UInt32Value> getParserForType() {
/* 502 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public UInt32Value getDefaultInstanceForType() {
/* 507 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\UInt32Value.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */