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
/*     */ public final class StringValue
/*     */   extends GeneratedMessageV3
/*     */   implements StringValueOrBuilder
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   public static final int VALUE_FIELD_NUMBER = 1;
/*     */   private volatile Object value_;
/*     */   private byte memoizedIsInitialized;
/*     */   
/*     */   private StringValue(GeneratedMessageV3.Builder<?> builder) {
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
/* 139 */     this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new StringValue(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private StringValue() { this.memoizedIsInitialized = -1; this.value_ = ""; }
/*     */   private StringValue(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null)
/*     */       throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { String s; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: s = input.readStringRequireUtf8(); this.value_ = s; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag))
/* 142 */           done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 143 */     if (isInitialized == 1) return true; 
/* 144 */     if (isInitialized == 0) return false;
/*     */     
/* 146 */     this.memoizedIsInitialized = 1;
/* 147 */     return true; } public static final Descriptors.Descriptor getDescriptor() { return WrappersProto.internal_static_google_protobuf_StringValue_descriptor; }
/*     */   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return WrappersProto.internal_static_google_protobuf_StringValue_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)StringValue.class, (Class)Builder.class); }
/*     */   public String getValue() { Object ref = this.value_; if (ref instanceof String)
/*     */       return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.value_ = s; return s; }
/*     */   public ByteString getValueBytes() { Object ref = this.value_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.value_ = b; return b; }
/*     */      return (ByteString)ref; }
/* 153 */   public void writeTo(CodedOutputStream output) throws IOException { if (!getValueBytes().isEmpty()) {
/* 154 */       GeneratedMessageV3.writeString(output, 1, this.value_);
/*     */     }
/* 156 */     this.unknownFields.writeTo(output); }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/* 161 */     int size = this.memoizedSize;
/* 162 */     if (size != -1) return size;
/*     */     
/* 164 */     size = 0;
/* 165 */     if (!getValueBytes().isEmpty()) {
/* 166 */       size += GeneratedMessageV3.computeStringSize(1, this.value_);
/*     */     }
/* 168 */     size += this.unknownFields.getSerializedSize();
/* 169 */     this.memoizedSize = size;
/* 170 */     return size;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 175 */     if (obj == this) {
/* 176 */       return true;
/*     */     }
/* 178 */     if (!(obj instanceof StringValue)) {
/* 179 */       return super.equals(obj);
/*     */     }
/* 181 */     StringValue other = (StringValue)obj;
/*     */ 
/*     */     
/* 184 */     if (!getValue().equals(other.getValue())) return false; 
/* 185 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 186 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 191 */     if (this.memoizedHashCode != 0) {
/* 192 */       return this.memoizedHashCode;
/*     */     }
/* 194 */     int hash = 41;
/* 195 */     hash = 19 * hash + getDescriptor().hashCode();
/* 196 */     hash = 37 * hash + 1;
/* 197 */     hash = 53 * hash + getValue().hashCode();
/* 198 */     hash = 29 * hash + this.unknownFields.hashCode();
/* 199 */     this.memoizedHashCode = hash;
/* 200 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 206 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 212 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static StringValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 217 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 223 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static StringValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 227 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 233 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static StringValue parseFrom(InputStream input) throws IOException {
/* 237 */     return 
/* 238 */       GeneratedMessageV3.<StringValue>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 244 */     return 
/* 245 */       GeneratedMessageV3.<StringValue>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static StringValue parseDelimitedFrom(InputStream input) throws IOException {
/* 249 */     return 
/* 250 */       GeneratedMessageV3.<StringValue>parseDelimitedWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 256 */     return 
/* 257 */       GeneratedMessageV3.<StringValue>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static StringValue parseFrom(CodedInputStream input) throws IOException {
/* 262 */     return 
/* 263 */       GeneratedMessageV3.<StringValue>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 269 */     return 
/* 270 */       GeneratedMessageV3.<StringValue>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public Builder newBuilderForType() {
/* 274 */     return newBuilder();
/*     */   } public static Builder newBuilder() {
/* 276 */     return DEFAULT_INSTANCE.toBuilder();
/*     */   }
/*     */   public static Builder newBuilder(StringValue prototype) {
/* 279 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*     */   }
/*     */   
/*     */   public Builder toBuilder() {
/* 283 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 284 */       .mergeFrom(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 290 */     Builder builder = new Builder(parent);
/* 291 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Builder
/*     */     extends GeneratedMessageV3.Builder<Builder>
/*     */     implements StringValueOrBuilder
/*     */   {
/*     */     private Object value_;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final Descriptors.Descriptor getDescriptor() {
/* 307 */       return WrappersProto.internal_static_google_protobuf_StringValue_descriptor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 313 */       return WrappersProto.internal_static_google_protobuf_StringValue_fieldAccessorTable
/* 314 */         .ensureFieldAccessorsInitialized((Class)StringValue.class, (Class)Builder.class);
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
/* 446 */       this.value_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.value_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (GeneratedMessageV3.alwaysUseFieldBuilders); }
/*     */     public Builder clear() { super.clear(); this.value_ = ""; return this; }
/*     */     public Descriptors.Descriptor getDescriptorForType() { return WrappersProto.internal_static_google_protobuf_StringValue_descriptor; }
/*     */     public StringValue getDefaultInstanceForType() { return StringValue.getDefaultInstance(); }
/*     */     public StringValue build() { StringValue result = buildPartial(); if (!result.isInitialized())
/*     */         throw newUninitializedMessageException(result);  return result; }
/*     */     public StringValue buildPartial() { StringValue result = new StringValue(this); result.value_ = this.value_; onBuilt();
/*     */       return result; }
/*     */     public Builder clone() { return super.clone(); }
/*     */     public Builder setField(Descriptors.FieldDescriptor field, Object value) { return super.setField(field, value); }
/* 456 */     public String getValue() { Object ref = this.value_;
/* 457 */       if (!(ref instanceof String)) {
/* 458 */         ByteString bs = (ByteString)ref;
/*     */         
/* 460 */         String s = bs.toStringUtf8();
/* 461 */         this.value_ = s;
/* 462 */         return s;
/*     */       } 
/* 464 */       return (String)ref; }
/*     */      public Builder clearField(Descriptors.FieldDescriptor field) {
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
/* 477 */     public ByteString getValueBytes() { Object ref = this.value_;
/* 478 */       if (ref instanceof String) {
/*     */         
/* 480 */         ByteString b = ByteString.copyFromUtf8((String)ref);
/*     */         
/* 482 */         this.value_ = b;
/* 483 */         return b;
/*     */       } 
/* 485 */       return (ByteString)ref; } public Builder mergeFrom(Message other) { if (other instanceof StringValue)
/*     */         return mergeFrom((StringValue)other);  super.mergeFrom(other); return this; }
/*     */     public Builder mergeFrom(StringValue other) { if (other == StringValue.getDefaultInstance())
/*     */         return this;  if (!other.getValue().isEmpty()) { this.value_ = other.value_; onChanged(); }
/*     */        mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*     */     public final boolean isInitialized() { return true; }
/*     */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { StringValue parsedMessage = null; try {
/*     */         parsedMessage = StringValue.PARSER.parsePartialFrom(input, extensionRegistry);
/*     */       } catch (InvalidProtocolBufferException e) {
/*     */         parsedMessage = (StringValue)e.getUnfinishedMessage(); throw e.unwrapIOException();
/*     */       } finally {
/*     */         if (parsedMessage != null)
/*     */           mergeFrom(parsedMessage); 
/*     */       }  return this; }
/* 499 */     public Builder setValue(String value) { if (value == null) {
/* 500 */         throw new NullPointerException();
/*     */       }
/*     */       
/* 503 */       this.value_ = value;
/* 504 */       onChanged();
/* 505 */       return this; }
/*     */ 
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
/* 517 */       this.value_ = StringValue.getDefaultInstance().getValue();
/* 518 */       onChanged();
/* 519 */       return this;
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
/*     */     public Builder setValueBytes(ByteString value) {
/* 532 */       if (value == null) {
/* 533 */         throw new NullPointerException();
/*     */       }
/* 535 */       AbstractMessageLite.checkByteStringIsUtf8(value);
/*     */       
/* 537 */       this.value_ = value;
/* 538 */       onChanged();
/* 539 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 544 */       return super.setUnknownFields(unknownFields);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 550 */       return super.mergeUnknownFields(unknownFields);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 560 */   private static final StringValue DEFAULT_INSTANCE = new StringValue();
/*     */ 
/*     */   
/*     */   public static StringValue getDefaultInstance() {
/* 564 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */   
/*     */   public static StringValue of(String value) {
/* 568 */     return newBuilder().setValue(value).build();
/*     */   }
/*     */ 
/*     */   
/* 572 */   private static final Parser<StringValue> PARSER = new AbstractParser<StringValue>()
/*     */     {
/*     */ 
/*     */       
/*     */       public StringValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */       {
/* 578 */         return new StringValue(input, extensionRegistry);
/*     */       }
/*     */     };
/*     */   
/*     */   public static Parser<StringValue> parser() {
/* 583 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<StringValue> getParserForType() {
/* 588 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringValue getDefaultInstanceForType() {
/* 593 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\StringValue.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */