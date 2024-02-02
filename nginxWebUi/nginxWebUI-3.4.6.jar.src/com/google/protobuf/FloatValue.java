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
/*     */ public final class FloatValue
/*     */   extends GeneratedMessageV3
/*     */   implements FloatValueOrBuilder
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   public static final int VALUE_FIELD_NUMBER = 1;
/*     */   private float value_;
/*     */   private byte memoizedIsInitialized;
/*     */   
/*     */   private FloatValue(GeneratedMessageV3.Builder<?> builder) {
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
/* 107 */     this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new FloatValue(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private FloatValue() { this.memoizedIsInitialized = -1; }
/*     */   private FloatValue(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null)
/*     */       throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 13: this.value_ = input.readFloat(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag))
/* 110 */           done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 111 */     if (isInitialized == 1) return true; 
/* 112 */     if (isInitialized == 0) return false;
/*     */     
/* 114 */     this.memoizedIsInitialized = 1;
/* 115 */     return true; } public static final Descriptors.Descriptor getDescriptor() { return WrappersProto.internal_static_google_protobuf_FloatValue_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*     */     return WrappersProto.internal_static_google_protobuf_FloatValue_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)FloatValue.class, (Class)Builder.class);
/*     */   } public float getValue() {
/*     */     return this.value_;
/*     */   }
/*     */   public void writeTo(CodedOutputStream output) throws IOException {
/* 121 */     if (this.value_ != 0.0F) {
/* 122 */       output.writeFloat(1, this.value_);
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
/* 133 */     if (this.value_ != 0.0F) {
/* 134 */       size += 
/* 135 */         CodedOutputStream.computeFloatSize(1, this.value_);
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
/* 147 */     if (!(obj instanceof FloatValue)) {
/* 148 */       return super.equals(obj);
/*     */     }
/* 150 */     FloatValue other = (FloatValue)obj;
/*     */     
/* 152 */     if (Float.floatToIntBits(getValue()) != 
/* 153 */       Float.floatToIntBits(other
/* 154 */         .getValue())) return false; 
/* 155 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 156 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 161 */     if (this.memoizedHashCode != 0) {
/* 162 */       return this.memoizedHashCode;
/*     */     }
/* 164 */     int hash = 41;
/* 165 */     hash = 19 * hash + getDescriptor().hashCode();
/* 166 */     hash = 37 * hash + 1;
/* 167 */     hash = 53 * hash + Float.floatToIntBits(
/* 168 */         getValue());
/* 169 */     hash = 29 * hash + this.unknownFields.hashCode();
/* 170 */     this.memoizedHashCode = hash;
/* 171 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static FloatValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 177 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static FloatValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 183 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static FloatValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 188 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static FloatValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 194 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static FloatValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 198 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static FloatValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 204 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static FloatValue parseFrom(InputStream input) throws IOException {
/* 208 */     return 
/* 209 */       GeneratedMessageV3.<FloatValue>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static FloatValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 215 */     return 
/* 216 */       GeneratedMessageV3.<FloatValue>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static FloatValue parseDelimitedFrom(InputStream input) throws IOException {
/* 220 */     return 
/* 221 */       GeneratedMessageV3.<FloatValue>parseDelimitedWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static FloatValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 227 */     return 
/* 228 */       GeneratedMessageV3.<FloatValue>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static FloatValue parseFrom(CodedInputStream input) throws IOException {
/* 233 */     return 
/* 234 */       GeneratedMessageV3.<FloatValue>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static FloatValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 240 */     return 
/* 241 */       GeneratedMessageV3.<FloatValue>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public Builder newBuilderForType() {
/* 245 */     return newBuilder();
/*     */   } public static Builder newBuilder() {
/* 247 */     return DEFAULT_INSTANCE.toBuilder();
/*     */   }
/*     */   public static Builder newBuilder(FloatValue prototype) {
/* 250 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*     */   }
/*     */   
/*     */   public Builder toBuilder() {
/* 254 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 255 */       .mergeFrom(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 261 */     Builder builder = new Builder(parent);
/* 262 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Builder
/*     */     extends GeneratedMessageV3.Builder<Builder>
/*     */     implements FloatValueOrBuilder
/*     */   {
/*     */     private float value_;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final Descriptors.Descriptor getDescriptor() {
/* 278 */       return WrappersProto.internal_static_google_protobuf_FloatValue_descriptor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 284 */       return WrappersProto.internal_static_google_protobuf_FloatValue_fieldAccessorTable
/* 285 */         .ensureFieldAccessorsInitialized((Class)FloatValue.class, (Class)Builder.class);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private Builder() {
/* 291 */       maybeForceBuilderInitialization();
/*     */     }
/*     */ 
/*     */     
/*     */     private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 296 */       super(parent);
/* 297 */       maybeForceBuilderInitialization();
/*     */     }
/*     */     private void maybeForceBuilderInitialization() {
/* 300 */       if (GeneratedMessageV3.alwaysUseFieldBuilders);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder clear() {
/* 306 */       super.clear();
/* 307 */       this.value_ = 0.0F;
/*     */       
/* 309 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Descriptors.Descriptor getDescriptorForType() {
/* 315 */       return WrappersProto.internal_static_google_protobuf_FloatValue_descriptor;
/*     */     }
/*     */ 
/*     */     
/*     */     public FloatValue getDefaultInstanceForType() {
/* 320 */       return FloatValue.getDefaultInstance();
/*     */     }
/*     */ 
/*     */     
/*     */     public FloatValue build() {
/* 325 */       FloatValue result = buildPartial();
/* 326 */       if (!result.isInitialized()) {
/* 327 */         throw newUninitializedMessageException(result);
/*     */       }
/* 329 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public FloatValue buildPartial() {
/* 334 */       FloatValue result = new FloatValue(this);
/* 335 */       result.value_ = this.value_;
/* 336 */       onBuilt();
/* 337 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clone() {
/* 342 */       return super.clone();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 348 */       return super.setField(field, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearField(Descriptors.FieldDescriptor field) {
/* 353 */       return super.clearField(field);
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 358 */       return super.clearOneof(oneof);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 364 */       return super.setRepeatedField(field, index, value);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 370 */       return super.addRepeatedField(field, value);
/*     */     }
/*     */     
/*     */     public Builder mergeFrom(Message other) {
/* 374 */       if (other instanceof FloatValue) {
/* 375 */         return mergeFrom((FloatValue)other);
/*     */       }
/* 377 */       super.mergeFrom(other);
/* 378 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder mergeFrom(FloatValue other) {
/* 383 */       if (other == FloatValue.getDefaultInstance()) return this; 
/* 384 */       if (other.getValue() != 0.0F) {
/* 385 */         setValue(other.getValue());
/*     */       }
/* 387 */       mergeUnknownFields(other.unknownFields);
/* 388 */       onChanged();
/* 389 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final boolean isInitialized() {
/* 394 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 402 */       FloatValue parsedMessage = null;
/*     */       try {
/* 404 */         parsedMessage = FloatValue.PARSER.parsePartialFrom(input, extensionRegistry);
/* 405 */       } catch (InvalidProtocolBufferException e) {
/* 406 */         parsedMessage = (FloatValue)e.getUnfinishedMessage();
/* 407 */         throw e.unwrapIOException();
/*     */       } finally {
/* 409 */         if (parsedMessage != null) {
/* 410 */           mergeFrom(parsedMessage);
/*     */         }
/*     */       } 
/* 413 */       return this;
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
/*     */     public float getValue() {
/* 426 */       return this.value_;
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
/*     */     public Builder setValue(float value) {
/* 439 */       this.value_ = value;
/* 440 */       onChanged();
/* 441 */       return this;
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
/* 453 */       this.value_ = 0.0F;
/* 454 */       onChanged();
/* 455 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 460 */       return super.setUnknownFields(unknownFields);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 466 */       return super.mergeUnknownFields(unknownFields);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 476 */   private static final FloatValue DEFAULT_INSTANCE = new FloatValue();
/*     */ 
/*     */   
/*     */   public static FloatValue getDefaultInstance() {
/* 480 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */   
/*     */   public static FloatValue of(float value) {
/* 484 */     return newBuilder().setValue(value).build();
/*     */   }
/*     */ 
/*     */   
/* 488 */   private static final Parser<FloatValue> PARSER = new AbstractParser<FloatValue>()
/*     */     {
/*     */ 
/*     */       
/*     */       public FloatValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */       {
/* 494 */         return new FloatValue(input, extensionRegistry);
/*     */       }
/*     */     };
/*     */   
/*     */   public static Parser<FloatValue> parser() {
/* 499 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<FloatValue> getParserForType() {
/* 504 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public FloatValue getDefaultInstanceForType() {
/* 509 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\FloatValue.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */