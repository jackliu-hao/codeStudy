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
/*     */ public final class Int64Value
/*     */   extends GeneratedMessageV3
/*     */   implements Int64ValueOrBuilder
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   public static final int VALUE_FIELD_NUMBER = 1;
/*     */   private long value_;
/*     */   private byte memoizedIsInitialized;
/*     */   
/*     */   private Int64Value(GeneratedMessageV3.Builder<?> builder) {
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
/* 107 */     this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Int64Value(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Int64Value() { this.memoizedIsInitialized = -1; }
/*     */   private Int64Value(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null)
/*     */       throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.value_ = input.readInt64(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag))
/* 110 */           done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 111 */     if (isInitialized == 1) return true; 
/* 112 */     if (isInitialized == 0) return false;
/*     */     
/* 114 */     this.memoizedIsInitialized = 1;
/* 115 */     return true; } public static final Descriptors.Descriptor getDescriptor() { return WrappersProto.internal_static_google_protobuf_Int64Value_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*     */     return WrappersProto.internal_static_google_protobuf_Int64Value_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Int64Value.class, (Class)Builder.class);
/*     */   } public long getValue() {
/*     */     return this.value_;
/*     */   }
/*     */   public void writeTo(CodedOutputStream output) throws IOException {
/* 121 */     if (this.value_ != 0L) {
/* 122 */       output.writeInt64(1, this.value_);
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
/* 133 */     if (this.value_ != 0L) {
/* 134 */       size += 
/* 135 */         CodedOutputStream.computeInt64Size(1, this.value_);
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
/* 147 */     if (!(obj instanceof Int64Value)) {
/* 148 */       return super.equals(obj);
/*     */     }
/* 150 */     Int64Value other = (Int64Value)obj;
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
/* 166 */     hash = 53 * hash + Internal.hashLong(
/* 167 */         getValue());
/* 168 */     hash = 29 * hash + this.unknownFields.hashCode();
/* 169 */     this.memoizedHashCode = hash;
/* 170 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Int64Value parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 176 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Int64Value parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 182 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Int64Value parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 187 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Int64Value parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 193 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Int64Value parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 197 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Int64Value parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 203 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Int64Value parseFrom(InputStream input) throws IOException {
/* 207 */     return 
/* 208 */       GeneratedMessageV3.<Int64Value>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Int64Value parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 214 */     return 
/* 215 */       GeneratedMessageV3.<Int64Value>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Int64Value parseDelimitedFrom(InputStream input) throws IOException {
/* 219 */     return 
/* 220 */       GeneratedMessageV3.<Int64Value>parseDelimitedWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Int64Value parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 226 */     return 
/* 227 */       GeneratedMessageV3.<Int64Value>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Int64Value parseFrom(CodedInputStream input) throws IOException {
/* 232 */     return 
/* 233 */       GeneratedMessageV3.<Int64Value>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Int64Value parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 239 */     return 
/* 240 */       GeneratedMessageV3.<Int64Value>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public Builder newBuilderForType() {
/* 244 */     return newBuilder();
/*     */   } public static Builder newBuilder() {
/* 246 */     return DEFAULT_INSTANCE.toBuilder();
/*     */   }
/*     */   public static Builder newBuilder(Int64Value prototype) {
/* 249 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*     */   }
/*     */   
/*     */   public Builder toBuilder() {
/* 253 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 254 */       .mergeFrom(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 260 */     Builder builder = new Builder(parent);
/* 261 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Builder
/*     */     extends GeneratedMessageV3.Builder<Builder>
/*     */     implements Int64ValueOrBuilder
/*     */   {
/*     */     private long value_;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final Descriptors.Descriptor getDescriptor() {
/* 277 */       return WrappersProto.internal_static_google_protobuf_Int64Value_descriptor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 283 */       return WrappersProto.internal_static_google_protobuf_Int64Value_fieldAccessorTable
/* 284 */         .ensureFieldAccessorsInitialized((Class)Int64Value.class, (Class)Builder.class);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private Builder() {
/* 290 */       maybeForceBuilderInitialization();
/*     */     }
/*     */ 
/*     */     
/*     */     private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 295 */       super(parent);
/* 296 */       maybeForceBuilderInitialization();
/*     */     }
/*     */     private void maybeForceBuilderInitialization() {
/* 299 */       if (GeneratedMessageV3.alwaysUseFieldBuilders);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder clear() {
/* 305 */       super.clear();
/* 306 */       this.value_ = 0L;
/*     */       
/* 308 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Descriptors.Descriptor getDescriptorForType() {
/* 314 */       return WrappersProto.internal_static_google_protobuf_Int64Value_descriptor;
/*     */     }
/*     */ 
/*     */     
/*     */     public Int64Value getDefaultInstanceForType() {
/* 319 */       return Int64Value.getDefaultInstance();
/*     */     }
/*     */ 
/*     */     
/*     */     public Int64Value build() {
/* 324 */       Int64Value result = buildPartial();
/* 325 */       if (!result.isInitialized()) {
/* 326 */         throw newUninitializedMessageException(result);
/*     */       }
/* 328 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Int64Value buildPartial() {
/* 333 */       Int64Value result = new Int64Value(this);
/* 334 */       result.value_ = this.value_;
/* 335 */       onBuilt();
/* 336 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clone() {
/* 341 */       return super.clone();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 347 */       return super.setField(field, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearField(Descriptors.FieldDescriptor field) {
/* 352 */       return super.clearField(field);
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 357 */       return super.clearOneof(oneof);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 363 */       return super.setRepeatedField(field, index, value);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 369 */       return super.addRepeatedField(field, value);
/*     */     }
/*     */     
/*     */     public Builder mergeFrom(Message other) {
/* 373 */       if (other instanceof Int64Value) {
/* 374 */         return mergeFrom((Int64Value)other);
/*     */       }
/* 376 */       super.mergeFrom(other);
/* 377 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder mergeFrom(Int64Value other) {
/* 382 */       if (other == Int64Value.getDefaultInstance()) return this; 
/* 383 */       if (other.getValue() != 0L) {
/* 384 */         setValue(other.getValue());
/*     */       }
/* 386 */       mergeUnknownFields(other.unknownFields);
/* 387 */       onChanged();
/* 388 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final boolean isInitialized() {
/* 393 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 401 */       Int64Value parsedMessage = null;
/*     */       try {
/* 403 */         parsedMessage = Int64Value.PARSER.parsePartialFrom(input, extensionRegistry);
/* 404 */       } catch (InvalidProtocolBufferException e) {
/* 405 */         parsedMessage = (Int64Value)e.getUnfinishedMessage();
/* 406 */         throw e.unwrapIOException();
/*     */       } finally {
/* 408 */         if (parsedMessage != null) {
/* 409 */           mergeFrom(parsedMessage);
/*     */         }
/*     */       } 
/* 412 */       return this;
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
/*     */     public long getValue() {
/* 425 */       return this.value_;
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
/*     */     public Builder setValue(long value) {
/* 438 */       this.value_ = value;
/* 439 */       onChanged();
/* 440 */       return this;
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
/* 452 */       this.value_ = 0L;
/* 453 */       onChanged();
/* 454 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 459 */       return super.setUnknownFields(unknownFields);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 465 */       return super.mergeUnknownFields(unknownFields);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 475 */   private static final Int64Value DEFAULT_INSTANCE = new Int64Value();
/*     */ 
/*     */   
/*     */   public static Int64Value getDefaultInstance() {
/* 479 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */   
/*     */   public static Int64Value of(long value) {
/* 483 */     return newBuilder().setValue(value).build();
/*     */   }
/*     */ 
/*     */   
/* 487 */   private static final Parser<Int64Value> PARSER = new AbstractParser<Int64Value>()
/*     */     {
/*     */ 
/*     */       
/*     */       public Int64Value parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */       {
/* 493 */         return new Int64Value(input, extensionRegistry);
/*     */       }
/*     */     };
/*     */   
/*     */   public static Parser<Int64Value> parser() {
/* 498 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<Int64Value> getParserForType() {
/* 503 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Int64Value getDefaultInstanceForType() {
/* 508 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Int64Value.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */