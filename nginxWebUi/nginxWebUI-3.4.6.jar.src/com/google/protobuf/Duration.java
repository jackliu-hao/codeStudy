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
/*     */ public final class Duration
/*     */   extends GeneratedMessageV3
/*     */   implements DurationOrBuilder
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   public static final int SECONDS_FIELD_NUMBER = 1;
/*     */   private long seconds_;
/*     */   public static final int NANOS_FIELD_NUMBER = 2;
/*     */   private int nanos_;
/*     */   private byte memoizedIsInitialized;
/*     */   
/*     */   private Duration(GeneratedMessageV3.Builder<?> builder) {
/*  64 */     super(builder);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 176 */     this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Duration(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Duration() { this.memoizedIsInitialized = -1; }
/*     */   private Duration(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null)
/*     */       throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.seconds_ = input.readInt64(); continue;case 16: this.nanos_ = input.readInt32(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag))
/* 179 */           done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 180 */     if (isInitialized == 1) return true; 
/* 181 */     if (isInitialized == 0) return false;
/*     */     
/* 183 */     this.memoizedIsInitialized = 1;
/* 184 */     return true; } public static final Descriptors.Descriptor getDescriptor() { return DurationProto.internal_static_google_protobuf_Duration_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return DurationProto.internal_static_google_protobuf_Duration_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Duration.class, (Class)Builder.class); }
/*     */   public long getSeconds() { return this.seconds_; }
/*     */   public int getNanos() {
/*     */     return this.nanos_;
/*     */   }
/*     */   public void writeTo(CodedOutputStream output) throws IOException {
/* 190 */     if (this.seconds_ != 0L) {
/* 191 */       output.writeInt64(1, this.seconds_);
/*     */     }
/* 193 */     if (this.nanos_ != 0) {
/* 194 */       output.writeInt32(2, this.nanos_);
/*     */     }
/* 196 */     this.unknownFields.writeTo(output);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/* 201 */     int size = this.memoizedSize;
/* 202 */     if (size != -1) return size;
/*     */     
/* 204 */     size = 0;
/* 205 */     if (this.seconds_ != 0L) {
/* 206 */       size += 
/* 207 */         CodedOutputStream.computeInt64Size(1, this.seconds_);
/*     */     }
/* 209 */     if (this.nanos_ != 0) {
/* 210 */       size += 
/* 211 */         CodedOutputStream.computeInt32Size(2, this.nanos_);
/*     */     }
/* 213 */     size += this.unknownFields.getSerializedSize();
/* 214 */     this.memoizedSize = size;
/* 215 */     return size;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 220 */     if (obj == this) {
/* 221 */       return true;
/*     */     }
/* 223 */     if (!(obj instanceof Duration)) {
/* 224 */       return super.equals(obj);
/*     */     }
/* 226 */     Duration other = (Duration)obj;
/*     */     
/* 228 */     if (getSeconds() != other
/* 229 */       .getSeconds()) return false; 
/* 230 */     if (getNanos() != other
/* 231 */       .getNanos()) return false; 
/* 232 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 233 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 238 */     if (this.memoizedHashCode != 0) {
/* 239 */       return this.memoizedHashCode;
/*     */     }
/* 241 */     int hash = 41;
/* 242 */     hash = 19 * hash + getDescriptor().hashCode();
/* 243 */     hash = 37 * hash + 1;
/* 244 */     hash = 53 * hash + Internal.hashLong(
/* 245 */         getSeconds());
/* 246 */     hash = 37 * hash + 2;
/* 247 */     hash = 53 * hash + getNanos();
/* 248 */     hash = 29 * hash + this.unknownFields.hashCode();
/* 249 */     this.memoizedHashCode = hash;
/* 250 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Duration parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 256 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Duration parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 262 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Duration parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 267 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Duration parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 273 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Duration parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 277 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Duration parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 283 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Duration parseFrom(InputStream input) throws IOException {
/* 287 */     return 
/* 288 */       GeneratedMessageV3.<Duration>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Duration parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 294 */     return 
/* 295 */       GeneratedMessageV3.<Duration>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Duration parseDelimitedFrom(InputStream input) throws IOException {
/* 299 */     return 
/* 300 */       GeneratedMessageV3.<Duration>parseDelimitedWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Duration parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 306 */     return 
/* 307 */       GeneratedMessageV3.<Duration>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Duration parseFrom(CodedInputStream input) throws IOException {
/* 312 */     return 
/* 313 */       GeneratedMessageV3.<Duration>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Duration parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 319 */     return 
/* 320 */       GeneratedMessageV3.<Duration>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public Builder newBuilderForType() {
/* 324 */     return newBuilder();
/*     */   } public static Builder newBuilder() {
/* 326 */     return DEFAULT_INSTANCE.toBuilder();
/*     */   }
/*     */   public static Builder newBuilder(Duration prototype) {
/* 329 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*     */   }
/*     */   
/*     */   public Builder toBuilder() {
/* 333 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 334 */       .mergeFrom(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 340 */     Builder builder = new Builder(parent);
/* 341 */     return builder;
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
/*     */   public static final class Builder
/*     */     extends GeneratedMessageV3.Builder<Builder>
/*     */     implements DurationOrBuilder
/*     */   {
/*     */     private long seconds_;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private int nanos_;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/* 400 */       return DurationProto.internal_static_google_protobuf_Duration_descriptor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 406 */       return DurationProto.internal_static_google_protobuf_Duration_fieldAccessorTable
/* 407 */         .ensureFieldAccessorsInitialized((Class)Duration.class, (Class)Builder.class);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private Builder() {
/* 413 */       maybeForceBuilderInitialization();
/*     */     }
/*     */ 
/*     */     
/*     */     private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 418 */       super(parent);
/* 419 */       maybeForceBuilderInitialization();
/*     */     }
/*     */     private void maybeForceBuilderInitialization() {
/* 422 */       if (GeneratedMessageV3.alwaysUseFieldBuilders);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder clear() {
/* 428 */       super.clear();
/* 429 */       this.seconds_ = 0L;
/*     */       
/* 431 */       this.nanos_ = 0;
/*     */       
/* 433 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Descriptors.Descriptor getDescriptorForType() {
/* 439 */       return DurationProto.internal_static_google_protobuf_Duration_descriptor;
/*     */     }
/*     */ 
/*     */     
/*     */     public Duration getDefaultInstanceForType() {
/* 444 */       return Duration.getDefaultInstance();
/*     */     }
/*     */ 
/*     */     
/*     */     public Duration build() {
/* 449 */       Duration result = buildPartial();
/* 450 */       if (!result.isInitialized()) {
/* 451 */         throw newUninitializedMessageException(result);
/*     */       }
/* 453 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Duration buildPartial() {
/* 458 */       Duration result = new Duration(this);
/* 459 */       result.seconds_ = this.seconds_;
/* 460 */       result.nanos_ = this.nanos_;
/* 461 */       onBuilt();
/* 462 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clone() {
/* 467 */       return super.clone();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 473 */       return super.setField(field, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearField(Descriptors.FieldDescriptor field) {
/* 478 */       return super.clearField(field);
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 483 */       return super.clearOneof(oneof);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 489 */       return super.setRepeatedField(field, index, value);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 495 */       return super.addRepeatedField(field, value);
/*     */     }
/*     */     
/*     */     public Builder mergeFrom(Message other) {
/* 499 */       if (other instanceof Duration) {
/* 500 */         return mergeFrom((Duration)other);
/*     */       }
/* 502 */       super.mergeFrom(other);
/* 503 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder mergeFrom(Duration other) {
/* 508 */       if (other == Duration.getDefaultInstance()) return this; 
/* 509 */       if (other.getSeconds() != 0L) {
/* 510 */         setSeconds(other.getSeconds());
/*     */       }
/* 512 */       if (other.getNanos() != 0) {
/* 513 */         setNanos(other.getNanos());
/*     */       }
/* 515 */       mergeUnknownFields(other.unknownFields);
/* 516 */       onChanged();
/* 517 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final boolean isInitialized() {
/* 522 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 530 */       Duration parsedMessage = null;
/*     */       try {
/* 532 */         parsedMessage = Duration.PARSER.parsePartialFrom(input, extensionRegistry);
/* 533 */       } catch (InvalidProtocolBufferException e) {
/* 534 */         parsedMessage = (Duration)e.getUnfinishedMessage();
/* 535 */         throw e.unwrapIOException();
/*     */       } finally {
/* 537 */         if (parsedMessage != null) {
/* 538 */           mergeFrom(parsedMessage);
/*     */         }
/*     */       } 
/* 541 */       return this;
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
/*     */     public long getSeconds() {
/* 556 */       return this.seconds_;
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
/*     */     public Builder setSeconds(long value) {
/* 571 */       this.seconds_ = value;
/* 572 */       onChanged();
/* 573 */       return this;
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
/*     */     public Builder clearSeconds() {
/* 587 */       this.seconds_ = 0L;
/* 588 */       onChanged();
/* 589 */       return this;
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
/*     */     public int getNanos() {
/* 607 */       return this.nanos_;
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
/*     */     public Builder setNanos(int value) {
/* 625 */       this.nanos_ = value;
/* 626 */       onChanged();
/* 627 */       return this;
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
/*     */     public Builder clearNanos() {
/* 644 */       this.nanos_ = 0;
/* 645 */       onChanged();
/* 646 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 651 */       return super.setUnknownFields(unknownFields);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 657 */       return super.mergeUnknownFields(unknownFields);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 667 */   private static final Duration DEFAULT_INSTANCE = new Duration();
/*     */ 
/*     */   
/*     */   public static Duration getDefaultInstance() {
/* 671 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ 
/*     */   
/* 675 */   private static final Parser<Duration> PARSER = new AbstractParser<Duration>()
/*     */     {
/*     */ 
/*     */       
/*     */       public Duration parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */       {
/* 681 */         return new Duration(input, extensionRegistry);
/*     */       }
/*     */     };
/*     */   
/*     */   public static Parser<Duration> parser() {
/* 686 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<Duration> getParserForType() {
/* 691 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Duration getDefaultInstanceForType() {
/* 696 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Duration.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */