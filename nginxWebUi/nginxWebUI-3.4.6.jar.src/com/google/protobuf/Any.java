/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class Any
/*      */   extends GeneratedMessageV3
/*      */   implements AnyOrBuilder
/*      */ {
/*      */   private static final long serialVersionUID = 0L;
/*      */   private volatile Message cachedUnpackValue;
/*      */   public static final int TYPE_URL_FIELD_NUMBER = 1;
/*      */   private volatile Object typeUrl_;
/*      */   public static final int VALUE_FIELD_NUMBER = 2;
/*      */   private ByteString value_;
/*      */   private byte memoizedIsInitialized;
/*      */   
/*      */   private Any(GeneratedMessageV3.Builder<?> builder) {
/*   82 */     super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  334 */     this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Any(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Any(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { String s; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: s = input.readStringRequireUtf8(); this.typeUrl_ = s; continue;case 18: this.value_ = input.readBytes(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return AnyProto.internal_static_google_protobuf_Any_descriptor; } private Any() { this.memoizedIsInitialized = -1; this.typeUrl_ = ""; this.value_ = ByteString.EMPTY; }
/*      */   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return AnyProto.internal_static_google_protobuf_Any_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Any.class, (Class)Builder.class); }
/*      */   private static String getTypeUrl(String typeUrlPrefix, Descriptors.Descriptor descriptor) { return typeUrlPrefix.endsWith("/") ? (typeUrlPrefix + descriptor.getFullName()) : (typeUrlPrefix + "/" + descriptor.getFullName()); }
/*  337 */   private static String getTypeNameFromTypeUrl(String typeUrl) { int pos = typeUrl.lastIndexOf('/'); return (pos == -1) ? "" : typeUrl.substring(pos + 1); } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  338 */     if (isInitialized == 1) return true; 
/*  339 */     if (isInitialized == 0) return false;
/*      */     
/*  341 */     this.memoizedIsInitialized = 1;
/*  342 */     return true; } public static <T extends Message> Any pack(T message) { return newBuilder().setTypeUrl(getTypeUrl("type.googleapis.com", message.getDescriptorForType())).setValue(message.toByteString()).build(); } public static <T extends Message> Any pack(T message, String typeUrlPrefix) { return newBuilder().setTypeUrl(getTypeUrl(typeUrlPrefix, message.getDescriptorForType())).setValue(message.toByteString()).build(); }
/*      */   public <T extends Message> boolean is(Class<T> clazz) { Message message = Internal.<Message>getDefaultInstance(clazz); return getTypeNameFromTypeUrl(getTypeUrl()).equals(message.getDescriptorForType().getFullName()); }
/*      */   public <T extends Message> T unpack(Class<T> clazz) throws InvalidProtocolBufferException { boolean invalidClazz = false; if (this.cachedUnpackValue != null) { if (this.cachedUnpackValue.getClass() == clazz) return (T)this.cachedUnpackValue;  invalidClazz = true; }  if (invalidClazz || !is(clazz)) throw new InvalidProtocolBufferException("Type of the Any message does not match the given class.");  Message message1 = Internal.<Message>getDefaultInstance(clazz); Message message2 = message1.getParserForType().parseFrom(getValue()); this.cachedUnpackValue = message2; return (T)message2; }
/*      */   public String getTypeUrl() { Object ref = this.typeUrl_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.typeUrl_ = s; return s; }
/*      */   public ByteString getTypeUrlBytes() { Object ref = this.typeUrl_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.typeUrl_ = b; return b; }  return (ByteString)ref; }
/*      */   public ByteString getValue() { return this.value_; }
/*  348 */   public void writeTo(CodedOutputStream output) throws IOException { if (!getTypeUrlBytes().isEmpty()) {
/*  349 */       GeneratedMessageV3.writeString(output, 1, this.typeUrl_);
/*      */     }
/*  351 */     if (!this.value_.isEmpty()) {
/*  352 */       output.writeBytes(2, this.value_);
/*      */     }
/*  354 */     this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getSerializedSize() {
/*  359 */     int size = this.memoizedSize;
/*  360 */     if (size != -1) return size;
/*      */     
/*  362 */     size = 0;
/*  363 */     if (!getTypeUrlBytes().isEmpty()) {
/*  364 */       size += GeneratedMessageV3.computeStringSize(1, this.typeUrl_);
/*      */     }
/*  366 */     if (!this.value_.isEmpty()) {
/*  367 */       size += 
/*  368 */         CodedOutputStream.computeBytesSize(2, this.value_);
/*      */     }
/*  370 */     size += this.unknownFields.getSerializedSize();
/*  371 */     this.memoizedSize = size;
/*  372 */     return size;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object obj) {
/*  377 */     if (obj == this) {
/*  378 */       return true;
/*      */     }
/*  380 */     if (!(obj instanceof Any)) {
/*  381 */       return super.equals(obj);
/*      */     }
/*  383 */     Any other = (Any)obj;
/*      */ 
/*      */     
/*  386 */     if (!getTypeUrl().equals(other.getTypeUrl())) return false;
/*      */     
/*  388 */     if (!getValue().equals(other.getValue())) return false; 
/*  389 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  390 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  395 */     if (this.memoizedHashCode != 0) {
/*  396 */       return this.memoizedHashCode;
/*      */     }
/*  398 */     int hash = 41;
/*  399 */     hash = 19 * hash + getDescriptor().hashCode();
/*  400 */     hash = 37 * hash + 1;
/*  401 */     hash = 53 * hash + getTypeUrl().hashCode();
/*  402 */     hash = 37 * hash + 2;
/*  403 */     hash = 53 * hash + getValue().hashCode();
/*  404 */     hash = 29 * hash + this.unknownFields.hashCode();
/*  405 */     this.memoizedHashCode = hash;
/*  406 */     return hash;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Any parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  412 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Any parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  418 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */ 
/*      */   
/*      */   public static Any parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  423 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Any parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  429 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static Any parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  433 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Any parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  439 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static Any parseFrom(InputStream input) throws IOException {
/*  443 */     return 
/*  444 */       GeneratedMessageV3.<Any>parseWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Any parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  450 */     return 
/*  451 */       GeneratedMessageV3.<Any>parseWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static Any parseDelimitedFrom(InputStream input) throws IOException {
/*  455 */     return 
/*  456 */       GeneratedMessageV3.<Any>parseDelimitedWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Any parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  462 */     return 
/*  463 */       GeneratedMessageV3.<Any>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */ 
/*      */   
/*      */   public static Any parseFrom(CodedInputStream input) throws IOException {
/*  468 */     return 
/*  469 */       GeneratedMessageV3.<Any>parseWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Any parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  475 */     return 
/*  476 */       GeneratedMessageV3.<Any>parseWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */   
/*      */   public Builder newBuilderForType() {
/*  480 */     return newBuilder();
/*      */   } public static Builder newBuilder() {
/*  482 */     return DEFAULT_INSTANCE.toBuilder();
/*      */   }
/*      */   public static Builder newBuilder(Any prototype) {
/*  485 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */   }
/*      */   
/*      */   public Builder toBuilder() {
/*  489 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  490 */       .mergeFrom(this);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  496 */     Builder builder = new Builder(parent);
/*  497 */     return builder;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Builder
/*      */     extends GeneratedMessageV3.Builder<Builder>
/*      */     implements AnyOrBuilder
/*      */   {
/*      */     private Object typeUrl_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private ByteString value_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final Descriptors.Descriptor getDescriptor() {
/*  574 */       return AnyProto.internal_static_google_protobuf_Any_descriptor;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  580 */       return AnyProto.internal_static_google_protobuf_Any_fieldAccessorTable
/*  581 */         .ensureFieldAccessorsInitialized((Class)Any.class, (Class)Builder.class);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Builder()
/*      */     {
/*  719 */       this.typeUrl_ = "";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  925 */       this.value_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.typeUrl_ = ""; this.value_ = ByteString.EMPTY; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (GeneratedMessageV3.alwaysUseFieldBuilders); }
/*      */     public Builder clear() { super.clear(); this.typeUrl_ = ""; this.value_ = ByteString.EMPTY; return this; }
/*      */     public Descriptors.Descriptor getDescriptorForType() { return AnyProto.internal_static_google_protobuf_Any_descriptor; }
/*      */     public Any getDefaultInstanceForType() { return Any.getDefaultInstance(); }
/*      */     public Any build() { Any result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; }
/*      */     public Any buildPartial() { Any result = new Any(this); result.typeUrl_ = this.typeUrl_; result.value_ = this.value_; onBuilt(); return result; }
/*      */     public Builder clone() { return super.clone(); }
/*      */     public Builder setField(Descriptors.FieldDescriptor field, Object value) { return super.setField(field, value); }
/*      */     public Builder clearField(Descriptors.FieldDescriptor field) { return super.clearField(field); }
/*      */     public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return super.clearOneof(oneof); }
/*  935 */     public ByteString getValue() { return this.value_; } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return super.setRepeatedField(field, index, value); }
/*      */     public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return super.addRepeatedField(field, value); }
/*      */     public Builder mergeFrom(Message other) { if (other instanceof Any)
/*      */         return mergeFrom((Any)other);  super.mergeFrom(other); return this; }
/*      */     public Builder mergeFrom(Any other) { if (other == Any.getDefaultInstance())
/*      */         return this;  if (!other.getTypeUrl().isEmpty()) {
/*      */         this.typeUrl_ = other.typeUrl_; onChanged();
/*      */       }  if (other.getValue() != ByteString.EMPTY)
/*      */         setValue(other.getValue());  mergeUnknownFields(other.unknownFields);
/*      */       onChanged();
/*      */       return this; }
/*      */     public final boolean isInitialized() { return true; }
/*  947 */     public Builder setValue(ByteString value) { if (value == null) {
/*  948 */         throw new NullPointerException();
/*      */       }
/*      */       
/*  951 */       this.value_ = value;
/*  952 */       onChanged();
/*  953 */       return this; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { Any parsedMessage = null; try { parsedMessage = Any.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (Any)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null)
/*      */           mergeFrom(parsedMessage);  }
/*      */        return this; }
/*      */     public String getTypeUrl() { Object ref = this.typeUrl_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.typeUrl_ = s; return s; }
/*      */        return (String)ref; }
/*      */     public ByteString getTypeUrlBytes() { Object ref = this.typeUrl_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.typeUrl_ = b; return b; }
/*      */        return (ByteString)ref; }
/*      */     public Builder setTypeUrl(String value) { if (value == null)
/*      */         throw new NullPointerException();  this.typeUrl_ = value; onChanged(); return this; }
/*      */     public Builder clearTypeUrl() { this.typeUrl_ = Any.getDefaultInstance().getTypeUrl(); onChanged(); return this; }
/*      */     public Builder setTypeUrlBytes(ByteString value) { if (value == null)
/*      */         throw new NullPointerException();  AbstractMessageLite.checkByteStringIsUtf8(value); this.typeUrl_ = value; onChanged(); return this; }
/*  965 */     public Builder clearValue() { this.value_ = Any.getDefaultInstance().getValue();
/*  966 */       onChanged();
/*  967 */       return this; }
/*      */ 
/*      */ 
/*      */     
/*      */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  972 */       return super.setUnknownFields(unknownFields);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  978 */       return super.mergeUnknownFields(unknownFields);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  988 */   private static final Any DEFAULT_INSTANCE = new Any();
/*      */ 
/*      */   
/*      */   public static Any getDefaultInstance() {
/*  992 */     return DEFAULT_INSTANCE;
/*      */   }
/*      */ 
/*      */   
/*  996 */   private static final Parser<Any> PARSER = new AbstractParser<Any>()
/*      */     {
/*      */ 
/*      */       
/*      */       public Any parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */       {
/* 1002 */         return new Any(input, extensionRegistry);
/*      */       }
/*      */     };
/*      */   
/*      */   public static Parser<Any> parser() {
/* 1007 */     return PARSER;
/*      */   }
/*      */ 
/*      */   
/*      */   public Parser<Any> getParserForType() {
/* 1012 */     return PARSER;
/*      */   }
/*      */ 
/*      */   
/*      */   public Any getDefaultInstanceForType() {
/* 1017 */     return DEFAULT_INSTANCE;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Any.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */