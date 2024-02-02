/*      */ package com.google.protobuf;
/*      */ public final class Method extends GeneratedMessageV3 implements MethodOrBuilder {
/*      */   private static final long serialVersionUID = 0L;
/*      */   public static final int NAME_FIELD_NUMBER = 1;
/*      */   private volatile Object name_;
/*      */   public static final int REQUEST_TYPE_URL_FIELD_NUMBER = 2;
/*      */   private volatile Object requestTypeUrl_;
/*      */   public static final int REQUEST_STREAMING_FIELD_NUMBER = 3;
/*      */   private boolean requestStreaming_;
/*      */   public static final int RESPONSE_TYPE_URL_FIELD_NUMBER = 4;
/*      */   private volatile Object responseTypeUrl_;
/*      */   public static final int RESPONSE_STREAMING_FIELD_NUMBER = 5;
/*      */   private boolean responseStreaming_;
/*      */   public static final int OPTIONS_FIELD_NUMBER = 6;
/*      */   private List<Option> options_;
/*      */   public static final int SYNTAX_FIELD_NUMBER = 7;
/*      */   private int syntax_;
/*      */   private byte memoizedIsInitialized;
/*      */   
/*   20 */   private Method(GeneratedMessageV3.Builder<?> builder) { super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  381 */     this.memoizedIsInitialized = -1; } private Method() { this.memoizedIsInitialized = -1; this.name_ = ""; this.requestTypeUrl_ = ""; this.responseTypeUrl_ = ""; this.options_ = Collections.emptyList(); this.syntax_ = 0; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Method(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Method(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { String s; int rawValue, tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: s = input.readStringRequireUtf8(); this.name_ = s; continue;case 18: s = input.readStringRequireUtf8(); this.requestTypeUrl_ = s; continue;case 24: this.requestStreaming_ = input.readBool(); continue;case 34: s = input.readStringRequireUtf8(); this.responseTypeUrl_ = s; continue;case 40: this.responseStreaming_ = input.readBool(); continue;case 50: if ((mutable_bitField0_ & 0x1) == 0) { this.options_ = new ArrayList<>(); mutable_bitField0_ |= 0x1; }  this.options_.add(input.readMessage(Option.parser(), extensionRegistry)); continue;case 56: rawValue = input.readEnum(); this.syntax_ = rawValue; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x1) != 0) this.options_ = Collections.unmodifiableList(this.options_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return ApiProto.internal_static_google_protobuf_Method_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return ApiProto.internal_static_google_protobuf_Method_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Method.class, (Class)Builder.class); } public String getName() { Object ref = this.name_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.name_ = s; return s; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; } public String getRequestTypeUrl() { Object ref = this.requestTypeUrl_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.requestTypeUrl_ = s; return s; } public ByteString getRequestTypeUrlBytes() { Object ref = this.requestTypeUrl_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.requestTypeUrl_ = b; return b; }  return (ByteString)ref; } public boolean getRequestStreaming() { return this.requestStreaming_; } public String getResponseTypeUrl() { Object ref = this.responseTypeUrl_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.responseTypeUrl_ = s; return s; } public ByteString getResponseTypeUrlBytes() { Object ref = this.responseTypeUrl_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.responseTypeUrl_ = b; return b; }  return (ByteString)ref; } public boolean getResponseStreaming() { return this.responseStreaming_; } public List<Option> getOptionsList() { return this.options_; } public List<? extends OptionOrBuilder> getOptionsOrBuilderList() { return (List)this.options_; } public int getOptionsCount() { return this.options_.size(); } public Option getOptions(int index) { return this.options_.get(index); } public OptionOrBuilder getOptionsOrBuilder(int index) { return this.options_.get(index); }
/*      */   public int getSyntaxValue() { return this.syntax_; }
/*      */   public Syntax getSyntax() { Syntax result = Syntax.valueOf(this.syntax_); return (result == null) ? Syntax.UNRECOGNIZED : result; }
/*  384 */   public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  385 */     if (isInitialized == 1) return true; 
/*  386 */     if (isInitialized == 0) return false;
/*      */     
/*  388 */     this.memoizedIsInitialized = 1;
/*  389 */     return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeTo(CodedOutputStream output) throws IOException {
/*  395 */     if (!getNameBytes().isEmpty()) {
/*  396 */       GeneratedMessageV3.writeString(output, 1, this.name_);
/*      */     }
/*  398 */     if (!getRequestTypeUrlBytes().isEmpty()) {
/*  399 */       GeneratedMessageV3.writeString(output, 2, this.requestTypeUrl_);
/*      */     }
/*  401 */     if (this.requestStreaming_) {
/*  402 */       output.writeBool(3, this.requestStreaming_);
/*      */     }
/*  404 */     if (!getResponseTypeUrlBytes().isEmpty()) {
/*  405 */       GeneratedMessageV3.writeString(output, 4, this.responseTypeUrl_);
/*      */     }
/*  407 */     if (this.responseStreaming_) {
/*  408 */       output.writeBool(5, this.responseStreaming_);
/*      */     }
/*  410 */     for (int i = 0; i < this.options_.size(); i++) {
/*  411 */       output.writeMessage(6, this.options_.get(i));
/*      */     }
/*  413 */     if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
/*  414 */       output.writeEnum(7, this.syntax_);
/*      */     }
/*  416 */     this.unknownFields.writeTo(output);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getSerializedSize() {
/*  421 */     int size = this.memoizedSize;
/*  422 */     if (size != -1) return size;
/*      */     
/*  424 */     size = 0;
/*  425 */     if (!getNameBytes().isEmpty()) {
/*  426 */       size += GeneratedMessageV3.computeStringSize(1, this.name_);
/*      */     }
/*  428 */     if (!getRequestTypeUrlBytes().isEmpty()) {
/*  429 */       size += GeneratedMessageV3.computeStringSize(2, this.requestTypeUrl_);
/*      */     }
/*  431 */     if (this.requestStreaming_) {
/*  432 */       size += 
/*  433 */         CodedOutputStream.computeBoolSize(3, this.requestStreaming_);
/*      */     }
/*  435 */     if (!getResponseTypeUrlBytes().isEmpty()) {
/*  436 */       size += GeneratedMessageV3.computeStringSize(4, this.responseTypeUrl_);
/*      */     }
/*  438 */     if (this.responseStreaming_) {
/*  439 */       size += 
/*  440 */         CodedOutputStream.computeBoolSize(5, this.responseStreaming_);
/*      */     }
/*  442 */     for (int i = 0; i < this.options_.size(); i++) {
/*  443 */       size += 
/*  444 */         CodedOutputStream.computeMessageSize(6, this.options_.get(i));
/*      */     }
/*  446 */     if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
/*  447 */       size += 
/*  448 */         CodedOutputStream.computeEnumSize(7, this.syntax_);
/*      */     }
/*  450 */     size += this.unknownFields.getSerializedSize();
/*  451 */     this.memoizedSize = size;
/*  452 */     return size;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object obj) {
/*  457 */     if (obj == this) {
/*  458 */       return true;
/*      */     }
/*  460 */     if (!(obj instanceof Method)) {
/*  461 */       return super.equals(obj);
/*      */     }
/*  463 */     Method other = (Method)obj;
/*      */ 
/*      */     
/*  466 */     if (!getName().equals(other.getName())) return false;
/*      */     
/*  468 */     if (!getRequestTypeUrl().equals(other.getRequestTypeUrl())) return false; 
/*  469 */     if (getRequestStreaming() != other
/*  470 */       .getRequestStreaming()) return false;
/*      */     
/*  472 */     if (!getResponseTypeUrl().equals(other.getResponseTypeUrl())) return false; 
/*  473 */     if (getResponseStreaming() != other
/*  474 */       .getResponseStreaming()) return false;
/*      */     
/*  476 */     if (!getOptionsList().equals(other.getOptionsList())) return false; 
/*  477 */     if (this.syntax_ != other.syntax_) return false; 
/*  478 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  479 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  484 */     if (this.memoizedHashCode != 0) {
/*  485 */       return this.memoizedHashCode;
/*      */     }
/*  487 */     int hash = 41;
/*  488 */     hash = 19 * hash + getDescriptor().hashCode();
/*  489 */     hash = 37 * hash + 1;
/*  490 */     hash = 53 * hash + getName().hashCode();
/*  491 */     hash = 37 * hash + 2;
/*  492 */     hash = 53 * hash + getRequestTypeUrl().hashCode();
/*  493 */     hash = 37 * hash + 3;
/*  494 */     hash = 53 * hash + Internal.hashBoolean(
/*  495 */         getRequestStreaming());
/*  496 */     hash = 37 * hash + 4;
/*  497 */     hash = 53 * hash + getResponseTypeUrl().hashCode();
/*  498 */     hash = 37 * hash + 5;
/*  499 */     hash = 53 * hash + Internal.hashBoolean(
/*  500 */         getResponseStreaming());
/*  501 */     if (getOptionsCount() > 0) {
/*  502 */       hash = 37 * hash + 6;
/*  503 */       hash = 53 * hash + getOptionsList().hashCode();
/*      */     } 
/*  505 */     hash = 37 * hash + 7;
/*  506 */     hash = 53 * hash + this.syntax_;
/*  507 */     hash = 29 * hash + this.unknownFields.hashCode();
/*  508 */     this.memoizedHashCode = hash;
/*  509 */     return hash;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  515 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  521 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */ 
/*      */   
/*      */   public static Method parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  526 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  532 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static Method parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  536 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  542 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static Method parseFrom(InputStream input) throws IOException {
/*  546 */     return 
/*  547 */       GeneratedMessageV3.<Method>parseWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  553 */     return 
/*  554 */       GeneratedMessageV3.<Method>parseWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static Method parseDelimitedFrom(InputStream input) throws IOException {
/*  558 */     return 
/*  559 */       GeneratedMessageV3.<Method>parseDelimitedWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  565 */     return 
/*  566 */       GeneratedMessageV3.<Method>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */ 
/*      */   
/*      */   public static Method parseFrom(CodedInputStream input) throws IOException {
/*  571 */     return 
/*  572 */       GeneratedMessageV3.<Method>parseWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  578 */     return 
/*  579 */       GeneratedMessageV3.<Method>parseWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */   
/*      */   public Builder newBuilderForType() {
/*  583 */     return newBuilder();
/*      */   } public static Builder newBuilder() {
/*  585 */     return DEFAULT_INSTANCE.toBuilder();
/*      */   }
/*      */   public static Builder newBuilder(Method prototype) {
/*  588 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */   }
/*      */   
/*      */   public Builder toBuilder() {
/*  592 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  593 */       .mergeFrom(this);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  599 */     Builder builder = new Builder(parent);
/*  600 */     return builder;
/*      */   }
/*      */   
/*      */   public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MethodOrBuilder {
/*      */     private int bitField0_;
/*      */     private Object name_;
/*      */     private Object requestTypeUrl_;
/*      */     private boolean requestStreaming_;
/*      */     private Object responseTypeUrl_;
/*      */     private boolean responseStreaming_;
/*      */     private List<Option> options_;
/*      */     private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
/*      */     private int syntax_;
/*      */     
/*      */     public static final Descriptors.Descriptor getDescriptor() {
/*  615 */       return ApiProto.internal_static_google_protobuf_Method_descriptor;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  621 */       return ApiProto.internal_static_google_protobuf_Method_fieldAccessorTable
/*  622 */         .ensureFieldAccessorsInitialized((Class)Method.class, (Class)Builder.class);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*  830 */       this.name_ = "";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  926 */       this.requestTypeUrl_ = "";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1064 */       this.responseTypeUrl_ = "";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1202 */       this
/* 1203 */         .options_ = Collections.emptyList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1514 */       this.syntax_ = 0; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (GeneratedMessageV3.alwaysUseFieldBuilders) getOptionsFieldBuilder();  } public Builder clear() { super.clear(); this.name_ = ""; this.requestTypeUrl_ = ""; this.requestStreaming_ = false; this.responseTypeUrl_ = ""; this.responseStreaming_ = false; if (this.optionsBuilder_ == null) { this.options_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; } else { this.optionsBuilder_.clear(); }  this.syntax_ = 0; return this; } public Descriptors.Descriptor getDescriptorForType() { return ApiProto.internal_static_google_protobuf_Method_descriptor; } public Method getDefaultInstanceForType() { return Method.getDefaultInstance(); } public Method build() { Method result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public Method buildPartial() { Method result = new Method(this); int from_bitField0_ = this.bitField0_; result.name_ = this.name_; result.requestTypeUrl_ = this.requestTypeUrl_; result.requestStreaming_ = this.requestStreaming_; result.responseTypeUrl_ = this.responseTypeUrl_; result.responseStreaming_ = this.responseStreaming_; if (this.optionsBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0) { this.options_ = Collections.unmodifiableList(this.options_); this.bitField0_ &= 0xFFFFFFFE; }  result.options_ = this.options_; } else { result.options_ = this.optionsBuilder_.build(); }  result.syntax_ = this.syntax_; onBuilt(); return result; } public Builder clone() { return super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof Method) return mergeFrom((Method)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(Method other) { if (other == Method.getDefaultInstance()) return this;  if (!other.getName().isEmpty()) { this.name_ = other.name_; onChanged(); }  if (!other.getRequestTypeUrl().isEmpty()) { this.requestTypeUrl_ = other.requestTypeUrl_; onChanged(); }  if (other.getRequestStreaming()) setRequestStreaming(other.getRequestStreaming());  if (!other.getResponseTypeUrl().isEmpty()) { this.responseTypeUrl_ = other.responseTypeUrl_; onChanged(); }  if (other.getResponseStreaming()) setResponseStreaming(other.getResponseStreaming());  if (this.optionsBuilder_ == null) { if (!other.options_.isEmpty()) { if (this.options_.isEmpty()) { this.options_ = other.options_; this.bitField0_ &= 0xFFFFFFFE; } else { ensureOptionsIsMutable(); this.options_.addAll(other.options_); }  onChanged(); }  } else if (!other.options_.isEmpty()) { if (this.optionsBuilder_.isEmpty()) { this.optionsBuilder_.dispose(); this.optionsBuilder_ = null; this.options_ = other.options_; this.bitField0_ &= 0xFFFFFFFE; this.optionsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? getOptionsFieldBuilder() : null; } else { this.optionsBuilder_.addAllMessages(other.options_); }  }  if (other.syntax_ != 0) setSyntaxValue(other.getSyntaxValue());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.name_ = ""; this.requestTypeUrl_ = ""; this.responseTypeUrl_ = ""; this.options_ = Collections.emptyList(); this.syntax_ = 0; maybeForceBuilderInitialization(); }
/*      */     public final boolean isInitialized() { return true; }
/*      */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { Method parsedMessage = null; try { parsedMessage = Method.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (Method)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/*      */     public String getName() { Object ref = this.name_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.name_ = s; return s; }  return (String)ref; }
/*      */     public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; }
/*      */     public Builder setName(String value) { if (value == null) throw new NullPointerException();  this.name_ = value; onChanged(); return this; }
/*      */     public Builder clearName() { this.name_ = Method.getDefaultInstance().getName(); onChanged(); return this; }
/*      */     public Builder setNameBytes(ByteString value) { if (value == null) throw new NullPointerException();  AbstractMessageLite.checkByteStringIsUtf8(value); this.name_ = value; onChanged(); return this; }
/*      */     public String getRequestTypeUrl() { Object ref = this.requestTypeUrl_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.requestTypeUrl_ = s; return s; }  return (String)ref; }
/*      */     public ByteString getRequestTypeUrlBytes() { Object ref = this.requestTypeUrl_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.requestTypeUrl_ = b; return b; }  return (ByteString)ref; }
/* 1524 */     public Builder setRequestTypeUrl(String value) { if (value == null) throw new NullPointerException();  this.requestTypeUrl_ = value; onChanged(); return this; } public Builder clearRequestTypeUrl() { this.requestTypeUrl_ = Method.getDefaultInstance().getRequestTypeUrl(); onChanged(); return this; } public Builder setRequestTypeUrlBytes(ByteString value) { if (value == null) throw new NullPointerException();  AbstractMessageLite.checkByteStringIsUtf8(value); this.requestTypeUrl_ = value; onChanged(); return this; } public boolean getRequestStreaming() { return this.requestStreaming_; } public Builder setRequestStreaming(boolean value) { this.requestStreaming_ = value; onChanged(); return this; } public int getSyntaxValue() { return this.syntax_; } public Builder clearRequestStreaming() { this.requestStreaming_ = false; onChanged(); return this; } public String getResponseTypeUrl() { Object ref = this.responseTypeUrl_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.responseTypeUrl_ = s; return s; }  return (String)ref; } public ByteString getResponseTypeUrlBytes() { Object ref = this.responseTypeUrl_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.responseTypeUrl_ = b; return b; }  return (ByteString)ref; } public Builder setResponseTypeUrl(String value) { if (value == null) throw new NullPointerException();  this.responseTypeUrl_ = value; onChanged(); return this; } public Builder clearResponseTypeUrl() { this.responseTypeUrl_ = Method.getDefaultInstance().getResponseTypeUrl(); onChanged(); return this; } public Builder setResponseTypeUrlBytes(ByteString value) { if (value == null) throw new NullPointerException();  AbstractMessageLite.checkByteStringIsUtf8(value); this.responseTypeUrl_ = value; onChanged(); return this; } public boolean getResponseStreaming() { return this.responseStreaming_; } public Builder setResponseStreaming(boolean value) { this.responseStreaming_ = value; onChanged(); return this; } public Builder clearResponseStreaming() { this.responseStreaming_ = false; onChanged(); return this; } private void ensureOptionsIsMutable() { if ((this.bitField0_ & 0x1) == 0) { this.options_ = new ArrayList<>(this.options_); this.bitField0_ |= 0x1; }  } public List<Option> getOptionsList() { if (this.optionsBuilder_ == null) return Collections.unmodifiableList(this.options_);  return this.optionsBuilder_.getMessageList(); } public int getOptionsCount() { if (this.optionsBuilder_ == null) return this.options_.size();  return this.optionsBuilder_.getCount(); } public Option getOptions(int index) { if (this.optionsBuilder_ == null) return this.options_.get(index);  return this.optionsBuilder_.getMessage(index); } public Builder setOptions(int index, Option value) { if (this.optionsBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOptionsIsMutable(); this.options_.set(index, value); onChanged(); } else { this.optionsBuilder_.setMessage(index, value); }  return this; } public Builder setOptions(int index, Option.Builder builderForValue) { if (this.optionsBuilder_ == null) { ensureOptionsIsMutable(); this.options_.set(index, builderForValue.build()); onChanged(); } else { this.optionsBuilder_.setMessage(index, builderForValue.build()); }  return this; } public Builder addOptions(Option value) { if (this.optionsBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOptionsIsMutable(); this.options_.add(value); onChanged(); } else { this.optionsBuilder_.addMessage(value); }  return this; } public Builder addOptions(int index, Option value) { if (this.optionsBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOptionsIsMutable(); this.options_.add(index, value); onChanged(); } else { this.optionsBuilder_.addMessage(index, value); }  return this; } public Builder addOptions(Option.Builder builderForValue) { if (this.optionsBuilder_ == null) { ensureOptionsIsMutable(); this.options_.add(builderForValue.build()); onChanged(); } else { this.optionsBuilder_.addMessage(builderForValue.build()); }  return this; }
/*      */     public Builder addOptions(int index, Option.Builder builderForValue) { if (this.optionsBuilder_ == null) { ensureOptionsIsMutable(); this.options_.add(index, builderForValue.build()); onChanged(); } else { this.optionsBuilder_.addMessage(index, builderForValue.build()); }  return this; }
/*      */     public Builder addAllOptions(Iterable<? extends Option> values) { if (this.optionsBuilder_ == null) { ensureOptionsIsMutable(); AbstractMessageLite.Builder.addAll(values, this.options_); onChanged(); } else { this.optionsBuilder_.addAllMessages(values); }  return this; }
/*      */     public Builder clearOptions() { if (this.optionsBuilder_ == null) { this.options_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; onChanged(); } else { this.optionsBuilder_.clear(); }  return this; }
/*      */     public Builder removeOptions(int index) { if (this.optionsBuilder_ == null) { ensureOptionsIsMutable(); this.options_.remove(index); onChanged(); } else { this.optionsBuilder_.remove(index); }  return this; }
/*      */     public Option.Builder getOptionsBuilder(int index) { return getOptionsFieldBuilder().getBuilder(index); }
/*      */     public OptionOrBuilder getOptionsOrBuilder(int index) { if (this.optionsBuilder_ == null) return this.options_.get(index);  return this.optionsBuilder_.getMessageOrBuilder(index); }
/*      */     public List<? extends OptionOrBuilder> getOptionsOrBuilderList() { if (this.optionsBuilder_ != null) return this.optionsBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.options_); }
/*      */     public Option.Builder addOptionsBuilder() { return getOptionsFieldBuilder().addBuilder(Option.getDefaultInstance()); }
/*      */     public Option.Builder addOptionsBuilder(int index) { return getOptionsFieldBuilder().addBuilder(index, Option.getDefaultInstance()); }
/*      */     public List<Option.Builder> getOptionsBuilderList() { return getOptionsFieldBuilder().getBuilderList(); }
/*      */     private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> getOptionsFieldBuilder() { if (this.optionsBuilder_ == null) { this.optionsBuilder_ = new RepeatedFieldBuilderV3<>(this.options_, ((this.bitField0_ & 0x1) != 0), getParentForChildren(), isClean()); this.options_ = null; }  return this.optionsBuilder_; }
/* 1536 */     public Builder setSyntaxValue(int value) { this.syntax_ = value;
/* 1537 */       onChanged();
/* 1538 */       return this; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Syntax getSyntax() {
/* 1550 */       Syntax result = Syntax.valueOf(this.syntax_);
/* 1551 */       return (result == null) ? Syntax.UNRECOGNIZED : result;
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
/*      */     public Builder setSyntax(Syntax value) {
/* 1563 */       if (value == null) {
/* 1564 */         throw new NullPointerException();
/*      */       }
/*      */       
/* 1567 */       this.syntax_ = value.getNumber();
/* 1568 */       onChanged();
/* 1569 */       return this;
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
/*      */     public Builder clearSyntax() {
/* 1581 */       this.syntax_ = 0;
/* 1582 */       onChanged();
/* 1583 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 1588 */       return super.setUnknownFields(unknownFields);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 1594 */       return super.mergeUnknownFields(unknownFields);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1604 */   private static final Method DEFAULT_INSTANCE = new Method();
/*      */ 
/*      */   
/*      */   public static Method getDefaultInstance() {
/* 1608 */     return DEFAULT_INSTANCE;
/*      */   }
/*      */ 
/*      */   
/* 1612 */   private static final Parser<Method> PARSER = new AbstractParser<Method>()
/*      */     {
/*      */ 
/*      */       
/*      */       public Method parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */       {
/* 1618 */         return new Method(input, extensionRegistry);
/*      */       }
/*      */     };
/*      */   
/*      */   public static Parser<Method> parser() {
/* 1623 */     return PARSER;
/*      */   }
/*      */ 
/*      */   
/*      */   public Parser<Method> getParserForType() {
/* 1628 */     return PARSER;
/*      */   }
/*      */ 
/*      */   
/*      */   public Method getDefaultInstanceForType() {
/* 1633 */     return DEFAULT_INSTANCE;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Method.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */