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
/*     */ public final class BytesValue
/*     */   extends GeneratedMessageV3
/*     */   implements BytesValueOrBuilder
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   public static final int VALUE_FIELD_NUMBER = 1;
/*     */   private ByteString value_;
/*     */   private byte memoizedIsInitialized;
/*     */   
/*     */   private BytesValue(GeneratedMessageV3.Builder<?> builder) {
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
/* 108 */     this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new BytesValue(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private BytesValue() { this.memoizedIsInitialized = -1; this.value_ = ByteString.EMPTY; }
/*     */   private BytesValue(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null)
/*     */       throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: this.value_ = input.readBytes(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag))
/* 111 */           done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 112 */     if (isInitialized == 1) return true; 
/* 113 */     if (isInitialized == 0) return false;
/*     */     
/* 115 */     this.memoizedIsInitialized = 1;
/* 116 */     return true; } public static final Descriptors.Descriptor getDescriptor() { return WrappersProto.internal_static_google_protobuf_BytesValue_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*     */     return WrappersProto.internal_static_google_protobuf_BytesValue_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)BytesValue.class, (Class)Builder.class);
/*     */   } public ByteString getValue() {
/*     */     return this.value_;
/*     */   }
/*     */   public void writeTo(CodedOutputStream output) throws IOException {
/* 122 */     if (!this.value_.isEmpty()) {
/* 123 */       output.writeBytes(1, this.value_);
/*     */     }
/* 125 */     this.unknownFields.writeTo(output);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/* 130 */     int size = this.memoizedSize;
/* 131 */     if (size != -1) return size;
/*     */     
/* 133 */     size = 0;
/* 134 */     if (!this.value_.isEmpty()) {
/* 135 */       size += 
/* 136 */         CodedOutputStream.computeBytesSize(1, this.value_);
/*     */     }
/* 138 */     size += this.unknownFields.getSerializedSize();
/* 139 */     this.memoizedSize = size;
/* 140 */     return size;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 145 */     if (obj == this) {
/* 146 */       return true;
/*     */     }
/* 148 */     if (!(obj instanceof BytesValue)) {
/* 149 */       return super.equals(obj);
/*     */     }
/* 151 */     BytesValue other = (BytesValue)obj;
/*     */ 
/*     */     
/* 154 */     if (!getValue().equals(other.getValue())) return false; 
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
/* 167 */     hash = 53 * hash + getValue().hashCode();
/* 168 */     hash = 29 * hash + this.unknownFields.hashCode();
/* 169 */     this.memoizedHashCode = hash;
/* 170 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static BytesValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 176 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static BytesValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 182 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static BytesValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 187 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static BytesValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 193 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static BytesValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 197 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static BytesValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 203 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static BytesValue parseFrom(InputStream input) throws IOException {
/* 207 */     return 
/* 208 */       GeneratedMessageV3.<BytesValue>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static BytesValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 214 */     return 
/* 215 */       GeneratedMessageV3.<BytesValue>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static BytesValue parseDelimitedFrom(InputStream input) throws IOException {
/* 219 */     return 
/* 220 */       GeneratedMessageV3.<BytesValue>parseDelimitedWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static BytesValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 226 */     return 
/* 227 */       GeneratedMessageV3.<BytesValue>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static BytesValue parseFrom(CodedInputStream input) throws IOException {
/* 232 */     return 
/* 233 */       GeneratedMessageV3.<BytesValue>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static BytesValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 239 */     return 
/* 240 */       GeneratedMessageV3.<BytesValue>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public Builder newBuilderForType() {
/* 244 */     return newBuilder();
/*     */   } public static Builder newBuilder() {
/* 246 */     return DEFAULT_INSTANCE.toBuilder();
/*     */   }
/*     */   public static Builder newBuilder(BytesValue prototype) {
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
/*     */     implements BytesValueOrBuilder
/*     */   {
/*     */     private ByteString value_;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final Descriptors.Descriptor getDescriptor() {
/* 277 */       return WrappersProto.internal_static_google_protobuf_BytesValue_descriptor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 283 */       return WrappersProto.internal_static_google_protobuf_BytesValue_fieldAccessorTable
/* 284 */         .ensureFieldAccessorsInitialized((Class)BytesValue.class, (Class)Builder.class);
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
/*     */     private Builder()
/*     */     {
/* 415 */       this.value_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.value_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (GeneratedMessageV3.alwaysUseFieldBuilders); }
/*     */     public Builder clear() { super.clear(); this.value_ = ByteString.EMPTY; return this; }
/*     */     public Descriptors.Descriptor getDescriptorForType() { return WrappersProto.internal_static_google_protobuf_BytesValue_descriptor; }
/*     */     public BytesValue getDefaultInstanceForType() { return BytesValue.getDefaultInstance(); }
/*     */     public BytesValue build() { BytesValue result = buildPartial(); if (!result.isInitialized())
/*     */         throw newUninitializedMessageException(result);  return result; }
/*     */     public BytesValue buildPartial() { BytesValue result = new BytesValue(this); result.value_ = this.value_; onBuilt();
/*     */       return result; }
/*     */     public Builder clone() { return super.clone(); }
/*     */     public Builder setField(Descriptors.FieldDescriptor field, Object value) { return super.setField(field, value); }
/* 425 */     public ByteString getValue() { return this.value_; }
/*     */      public Builder clearField(Descriptors.FieldDescriptor field) {
/*     */       return super.clearField(field);
/*     */     } public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/*     */       return super.clearOneof(oneof);
/*     */     }
/*     */     public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/*     */       return super.setRepeatedField(field, index, value);
/*     */     }
/*     */     public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/*     */       return super.addRepeatedField(field, value);
/*     */     }
/* 437 */     public Builder setValue(ByteString value) { if (value == null) {
/* 438 */         throw new NullPointerException();
/*     */       }
/*     */       
/* 441 */       this.value_ = value;
/* 442 */       onChanged();
/* 443 */       return this; } public Builder mergeFrom(Message other) { if (other instanceof BytesValue)
/*     */         return mergeFrom((BytesValue)other);  super.mergeFrom(other); return this; }
/*     */     public Builder mergeFrom(BytesValue other) { if (other == BytesValue.getDefaultInstance())
/*     */         return this;  if (other.getValue() != ByteString.EMPTY)
/*     */         setValue(other.getValue());  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*     */     public final boolean isInitialized() { return true; }
/*     */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { BytesValue parsedMessage = null; try { parsedMessage = BytesValue.PARSER.parsePartialFrom(input, extensionRegistry); }
/*     */       catch (InvalidProtocolBufferException e) { parsedMessage = (BytesValue)e.getUnfinishedMessage(); throw e.unwrapIOException(); }
/*     */       finally
/*     */       { if (parsedMessage != null)
/*     */           mergeFrom(parsedMessage);  }
/*     */        return this; }
/* 455 */     public Builder clearValue() { this.value_ = BytesValue.getDefaultInstance().getValue();
/* 456 */       onChanged();
/* 457 */       return this; }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 462 */       return super.setUnknownFields(unknownFields);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 468 */       return super.mergeUnknownFields(unknownFields);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 478 */   private static final BytesValue DEFAULT_INSTANCE = new BytesValue();
/*     */ 
/*     */   
/*     */   public static BytesValue getDefaultInstance() {
/* 482 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */   
/*     */   public static BytesValue of(ByteString value) {
/* 486 */     return newBuilder().setValue(value).build();
/*     */   }
/*     */ 
/*     */   
/* 490 */   private static final Parser<BytesValue> PARSER = new AbstractParser<BytesValue>()
/*     */     {
/*     */ 
/*     */       
/*     */       public BytesValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */       {
/* 496 */         return new BytesValue(input, extensionRegistry);
/*     */       }
/*     */     };
/*     */   
/*     */   public static Parser<BytesValue> parser() {
/* 501 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<BytesValue> getParserForType() {
/* 506 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public BytesValue getDefaultInstanceForType() {
/* 511 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\BytesValue.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */