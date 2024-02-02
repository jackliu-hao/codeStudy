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
/*     */ public final class Empty
/*     */   extends GeneratedMessageV3
/*     */   implements EmptyOrBuilder
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   private byte memoizedIsInitialized;
/*     */   
/*     */   private Empty(GeneratedMessageV3.Builder<?> builder) {
/*  26 */     super(builder);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  93 */     this.memoizedIsInitialized = -1; } private Empty() { this.memoizedIsInitialized = -1; }
/*     */   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Empty(); }
/*     */   public final UnknownFieldSet getUnknownFields() { return this.unknownFields; }
/*  96 */   public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  97 */     if (isInitialized == 1) return true; 
/*  98 */     if (isInitialized == 0) return false;
/*     */     
/* 100 */     this.memoizedIsInitialized = 1;
/* 101 */     return true; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeTo(CodedOutputStream output) throws IOException {
/* 107 */     this.unknownFields.writeTo(output);
/*     */   }
/*     */   private Empty(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null)
/*     */       throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag))
/*     */           done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/* 112 */   public static final Descriptors.Descriptor getDescriptor() { return EmptyProto.internal_static_google_protobuf_Empty_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return EmptyProto.internal_static_google_protobuf_Empty_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Empty.class, (Class)Builder.class); } public int getSerializedSize() { int size = this.memoizedSize;
/* 113 */     if (size != -1) return size;
/*     */     
/* 115 */     size = 0;
/* 116 */     size += this.unknownFields.getSerializedSize();
/* 117 */     this.memoizedSize = size;
/* 118 */     return size; }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 123 */     if (obj == this) {
/* 124 */       return true;
/*     */     }
/* 126 */     if (!(obj instanceof Empty)) {
/* 127 */       return super.equals(obj);
/*     */     }
/* 129 */     Empty other = (Empty)obj;
/*     */     
/* 131 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 132 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 137 */     if (this.memoizedHashCode != 0) {
/* 138 */       return this.memoizedHashCode;
/*     */     }
/* 140 */     int hash = 41;
/* 141 */     hash = 19 * hash + getDescriptor().hashCode();
/* 142 */     hash = 29 * hash + this.unknownFields.hashCode();
/* 143 */     this.memoizedHashCode = hash;
/* 144 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Empty parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 150 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Empty parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 156 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Empty parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 161 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Empty parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 167 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Empty parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 171 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Empty parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 177 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Empty parseFrom(InputStream input) throws IOException {
/* 181 */     return 
/* 182 */       GeneratedMessageV3.<Empty>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Empty parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 188 */     return 
/* 189 */       GeneratedMessageV3.<Empty>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Empty parseDelimitedFrom(InputStream input) throws IOException {
/* 193 */     return 
/* 194 */       GeneratedMessageV3.<Empty>parseDelimitedWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Empty parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 200 */     return 
/* 201 */       GeneratedMessageV3.<Empty>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Empty parseFrom(CodedInputStream input) throws IOException {
/* 206 */     return 
/* 207 */       GeneratedMessageV3.<Empty>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Empty parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 213 */     return 
/* 214 */       GeneratedMessageV3.<Empty>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public Builder newBuilderForType() {
/* 218 */     return newBuilder();
/*     */   } public static Builder newBuilder() {
/* 220 */     return DEFAULT_INSTANCE.toBuilder();
/*     */   }
/*     */   public static Builder newBuilder(Empty prototype) {
/* 223 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*     */   }
/*     */   
/*     */   public Builder toBuilder() {
/* 227 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 228 */       .mergeFrom(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 234 */     Builder builder = new Builder(parent);
/* 235 */     return builder;
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
/*     */   public static final class Builder
/*     */     extends GeneratedMessageV3.Builder<Builder>
/*     */     implements EmptyOrBuilder
/*     */   {
/*     */     public static final Descriptors.Descriptor getDescriptor() {
/* 256 */       return EmptyProto.internal_static_google_protobuf_Empty_descriptor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 262 */       return EmptyProto.internal_static_google_protobuf_Empty_fieldAccessorTable
/* 263 */         .ensureFieldAccessorsInitialized((Class)Empty.class, (Class)Builder.class);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private Builder() {
/* 269 */       maybeForceBuilderInitialization();
/*     */     }
/*     */ 
/*     */     
/*     */     private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 274 */       super(parent);
/* 275 */       maybeForceBuilderInitialization();
/*     */     }
/*     */     private void maybeForceBuilderInitialization() {
/* 278 */       if (GeneratedMessageV3.alwaysUseFieldBuilders);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder clear() {
/* 284 */       super.clear();
/* 285 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Descriptors.Descriptor getDescriptorForType() {
/* 291 */       return EmptyProto.internal_static_google_protobuf_Empty_descriptor;
/*     */     }
/*     */ 
/*     */     
/*     */     public Empty getDefaultInstanceForType() {
/* 296 */       return Empty.getDefaultInstance();
/*     */     }
/*     */ 
/*     */     
/*     */     public Empty build() {
/* 301 */       Empty result = buildPartial();
/* 302 */       if (!result.isInitialized()) {
/* 303 */         throw newUninitializedMessageException(result);
/*     */       }
/* 305 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Empty buildPartial() {
/* 310 */       Empty result = new Empty(this);
/* 311 */       onBuilt();
/* 312 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clone() {
/* 317 */       return super.clone();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 323 */       return super.setField(field, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearField(Descriptors.FieldDescriptor field) {
/* 328 */       return super.clearField(field);
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 333 */       return super.clearOneof(oneof);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 339 */       return super.setRepeatedField(field, index, value);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 345 */       return super.addRepeatedField(field, value);
/*     */     }
/*     */     
/*     */     public Builder mergeFrom(Message other) {
/* 349 */       if (other instanceof Empty) {
/* 350 */         return mergeFrom((Empty)other);
/*     */       }
/* 352 */       super.mergeFrom(other);
/* 353 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder mergeFrom(Empty other) {
/* 358 */       if (other == Empty.getDefaultInstance()) return this; 
/* 359 */       mergeUnknownFields(other.unknownFields);
/* 360 */       onChanged();
/* 361 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final boolean isInitialized() {
/* 366 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 374 */       Empty parsedMessage = null;
/*     */       try {
/* 376 */         parsedMessage = Empty.PARSER.parsePartialFrom(input, extensionRegistry);
/* 377 */       } catch (InvalidProtocolBufferException e) {
/* 378 */         parsedMessage = (Empty)e.getUnfinishedMessage();
/* 379 */         throw e.unwrapIOException();
/*     */       } finally {
/* 381 */         if (parsedMessage != null) {
/* 382 */           mergeFrom(parsedMessage);
/*     */         }
/*     */       } 
/* 385 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 390 */       return super.setUnknownFields(unknownFields);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 396 */       return super.mergeUnknownFields(unknownFields);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 406 */   private static final Empty DEFAULT_INSTANCE = new Empty();
/*     */ 
/*     */   
/*     */   public static Empty getDefaultInstance() {
/* 410 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ 
/*     */   
/* 414 */   private static final Parser<Empty> PARSER = new AbstractParser<Empty>()
/*     */     {
/*     */ 
/*     */       
/*     */       public Empty parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */       {
/* 420 */         return new Empty(input, extensionRegistry);
/*     */       }
/*     */     };
/*     */   
/*     */   public static Parser<Empty> parser() {
/* 425 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<Empty> getParserForType() {
/* 430 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Empty getDefaultInstanceForType() {
/* 435 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Empty.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */