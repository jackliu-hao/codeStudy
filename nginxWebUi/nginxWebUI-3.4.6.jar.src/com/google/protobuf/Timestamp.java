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
/*     */ public final class Timestamp
/*     */   extends GeneratedMessageV3
/*     */   implements TimestampOrBuilder
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   public static final int SECONDS_FIELD_NUMBER = 1;
/*     */   private long seconds_;
/*     */   public static final int NANOS_FIELD_NUMBER = 2;
/*     */   private int nanos_;
/*     */   private byte memoizedIsInitialized;
/*     */   
/*     */   private Timestamp(GeneratedMessageV3.Builder<?> builder) {
/*  80 */     super(builder);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 190 */     this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Timestamp(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Timestamp() { this.memoizedIsInitialized = -1; }
/*     */   private Timestamp(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null)
/*     */       throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.seconds_ = input.readInt64(); continue;case 16: this.nanos_ = input.readInt32(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag))
/* 193 */           done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 194 */     if (isInitialized == 1) return true; 
/* 195 */     if (isInitialized == 0) return false;
/*     */     
/* 197 */     this.memoizedIsInitialized = 1;
/* 198 */     return true; } public static final Descriptors.Descriptor getDescriptor() { return TimestampProto.internal_static_google_protobuf_Timestamp_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return TimestampProto.internal_static_google_protobuf_Timestamp_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Timestamp.class, (Class)Builder.class); }
/*     */   public long getSeconds() { return this.seconds_; }
/*     */   public int getNanos() {
/*     */     return this.nanos_;
/*     */   }
/*     */   public void writeTo(CodedOutputStream output) throws IOException {
/* 204 */     if (this.seconds_ != 0L) {
/* 205 */       output.writeInt64(1, this.seconds_);
/*     */     }
/* 207 */     if (this.nanos_ != 0) {
/* 208 */       output.writeInt32(2, this.nanos_);
/*     */     }
/* 210 */     this.unknownFields.writeTo(output);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/* 215 */     int size = this.memoizedSize;
/* 216 */     if (size != -1) return size;
/*     */     
/* 218 */     size = 0;
/* 219 */     if (this.seconds_ != 0L) {
/* 220 */       size += 
/* 221 */         CodedOutputStream.computeInt64Size(1, this.seconds_);
/*     */     }
/* 223 */     if (this.nanos_ != 0) {
/* 224 */       size += 
/* 225 */         CodedOutputStream.computeInt32Size(2, this.nanos_);
/*     */     }
/* 227 */     size += this.unknownFields.getSerializedSize();
/* 228 */     this.memoizedSize = size;
/* 229 */     return size;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 234 */     if (obj == this) {
/* 235 */       return true;
/*     */     }
/* 237 */     if (!(obj instanceof Timestamp)) {
/* 238 */       return super.equals(obj);
/*     */     }
/* 240 */     Timestamp other = (Timestamp)obj;
/*     */     
/* 242 */     if (getSeconds() != other
/* 243 */       .getSeconds()) return false; 
/* 244 */     if (getNanos() != other
/* 245 */       .getNanos()) return false; 
/* 246 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 247 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 252 */     if (this.memoizedHashCode != 0) {
/* 253 */       return this.memoizedHashCode;
/*     */     }
/* 255 */     int hash = 41;
/* 256 */     hash = 19 * hash + getDescriptor().hashCode();
/* 257 */     hash = 37 * hash + 1;
/* 258 */     hash = 53 * hash + Internal.hashLong(
/* 259 */         getSeconds());
/* 260 */     hash = 37 * hash + 2;
/* 261 */     hash = 53 * hash + getNanos();
/* 262 */     hash = 29 * hash + this.unknownFields.hashCode();
/* 263 */     this.memoizedHashCode = hash;
/* 264 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Timestamp parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 270 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Timestamp parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 276 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Timestamp parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 281 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Timestamp parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 287 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Timestamp parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 291 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Timestamp parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 297 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Timestamp parseFrom(InputStream input) throws IOException {
/* 301 */     return 
/* 302 */       GeneratedMessageV3.<Timestamp>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Timestamp parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 308 */     return 
/* 309 */       GeneratedMessageV3.<Timestamp>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Timestamp parseDelimitedFrom(InputStream input) throws IOException {
/* 313 */     return 
/* 314 */       GeneratedMessageV3.<Timestamp>parseDelimitedWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Timestamp parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 320 */     return 
/* 321 */       GeneratedMessageV3.<Timestamp>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Timestamp parseFrom(CodedInputStream input) throws IOException {
/* 326 */     return 
/* 327 */       GeneratedMessageV3.<Timestamp>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Timestamp parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 333 */     return 
/* 334 */       GeneratedMessageV3.<Timestamp>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public Builder newBuilderForType() {
/* 338 */     return newBuilder();
/*     */   } public static Builder newBuilder() {
/* 340 */     return DEFAULT_INSTANCE.toBuilder();
/*     */   }
/*     */   public static Builder newBuilder(Timestamp prototype) {
/* 343 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*     */   }
/*     */   
/*     */   public Builder toBuilder() {
/* 347 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 348 */       .mergeFrom(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 354 */     Builder builder = new Builder(parent);
/* 355 */     return builder;
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
/*     */     implements TimestampOrBuilder
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final Descriptors.Descriptor getDescriptor() {
/* 430 */       return TimestampProto.internal_static_google_protobuf_Timestamp_descriptor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 436 */       return TimestampProto.internal_static_google_protobuf_Timestamp_fieldAccessorTable
/* 437 */         .ensureFieldAccessorsInitialized((Class)Timestamp.class, (Class)Builder.class);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private Builder() {
/* 443 */       maybeForceBuilderInitialization();
/*     */     }
/*     */ 
/*     */     
/*     */     private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 448 */       super(parent);
/* 449 */       maybeForceBuilderInitialization();
/*     */     }
/*     */     private void maybeForceBuilderInitialization() {
/* 452 */       if (GeneratedMessageV3.alwaysUseFieldBuilders);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder clear() {
/* 458 */       super.clear();
/* 459 */       this.seconds_ = 0L;
/*     */       
/* 461 */       this.nanos_ = 0;
/*     */       
/* 463 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Descriptors.Descriptor getDescriptorForType() {
/* 469 */       return TimestampProto.internal_static_google_protobuf_Timestamp_descriptor;
/*     */     }
/*     */ 
/*     */     
/*     */     public Timestamp getDefaultInstanceForType() {
/* 474 */       return Timestamp.getDefaultInstance();
/*     */     }
/*     */ 
/*     */     
/*     */     public Timestamp build() {
/* 479 */       Timestamp result = buildPartial();
/* 480 */       if (!result.isInitialized()) {
/* 481 */         throw newUninitializedMessageException(result);
/*     */       }
/* 483 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Timestamp buildPartial() {
/* 488 */       Timestamp result = new Timestamp(this);
/* 489 */       result.seconds_ = this.seconds_;
/* 490 */       result.nanos_ = this.nanos_;
/* 491 */       onBuilt();
/* 492 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clone() {
/* 497 */       return super.clone();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 503 */       return super.setField(field, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearField(Descriptors.FieldDescriptor field) {
/* 508 */       return super.clearField(field);
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 513 */       return super.clearOneof(oneof);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 519 */       return super.setRepeatedField(field, index, value);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 525 */       return super.addRepeatedField(field, value);
/*     */     }
/*     */     
/*     */     public Builder mergeFrom(Message other) {
/* 529 */       if (other instanceof Timestamp) {
/* 530 */         return mergeFrom((Timestamp)other);
/*     */       }
/* 532 */       super.mergeFrom(other);
/* 533 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder mergeFrom(Timestamp other) {
/* 538 */       if (other == Timestamp.getDefaultInstance()) return this; 
/* 539 */       if (other.getSeconds() != 0L) {
/* 540 */         setSeconds(other.getSeconds());
/*     */       }
/* 542 */       if (other.getNanos() != 0) {
/* 543 */         setNanos(other.getNanos());
/*     */       }
/* 545 */       mergeUnknownFields(other.unknownFields);
/* 546 */       onChanged();
/* 547 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final boolean isInitialized() {
/* 552 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 560 */       Timestamp parsedMessage = null;
/*     */       try {
/* 562 */         parsedMessage = Timestamp.PARSER.parsePartialFrom(input, extensionRegistry);
/* 563 */       } catch (InvalidProtocolBufferException e) {
/* 564 */         parsedMessage = (Timestamp)e.getUnfinishedMessage();
/* 565 */         throw e.unwrapIOException();
/*     */       } finally {
/* 567 */         if (parsedMessage != null) {
/* 568 */           mergeFrom(parsedMessage);
/*     */         }
/*     */       } 
/* 571 */       return this;
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
/* 586 */       return this.seconds_;
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
/* 601 */       this.seconds_ = value;
/* 602 */       onChanged();
/* 603 */       return this;
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
/* 617 */       this.seconds_ = 0L;
/* 618 */       onChanged();
/* 619 */       return this;
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
/*     */     public int getNanos() {
/* 635 */       return this.nanos_;
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
/*     */     public Builder setNanos(int value) {
/* 651 */       this.nanos_ = value;
/* 652 */       onChanged();
/* 653 */       return this;
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
/*     */     public Builder clearNanos() {
/* 668 */       this.nanos_ = 0;
/* 669 */       onChanged();
/* 670 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 675 */       return super.setUnknownFields(unknownFields);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 681 */       return super.mergeUnknownFields(unknownFields);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 691 */   private static final Timestamp DEFAULT_INSTANCE = new Timestamp();
/*     */ 
/*     */   
/*     */   public static Timestamp getDefaultInstance() {
/* 695 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ 
/*     */   
/* 699 */   private static final Parser<Timestamp> PARSER = new AbstractParser<Timestamp>()
/*     */     {
/*     */ 
/*     */       
/*     */       public Timestamp parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */       {
/* 705 */         return new Timestamp(input, extensionRegistry);
/*     */       }
/*     */     };
/*     */   
/*     */   public static Parser<Timestamp> parser() {
/* 710 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<Timestamp> getParserForType() {
/* 715 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Timestamp getDefaultInstanceForType() {
/* 720 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Timestamp.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */