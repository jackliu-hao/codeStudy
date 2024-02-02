/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ 
/*      */ public final class EnumValue extends GeneratedMessageV3 implements EnumValueOrBuilder {
/*      */   private static final long serialVersionUID = 0L;
/*      */   public static final int NAME_FIELD_NUMBER = 1;
/*      */   private volatile Object name_;
/*      */   public static final int NUMBER_FIELD_NUMBER = 2;
/*      */   private int number_;
/*      */   public static final int OPTIONS_FIELD_NUMBER = 3;
/*      */   private List<Option> options_;
/*      */   private byte memoizedIsInitialized;
/*      */   
/*   20 */   private EnumValue(GeneratedMessageV3.Builder<?> builder) { super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  226 */     this.memoizedIsInitialized = -1; } private EnumValue() { this.memoizedIsInitialized = -1; this.name_ = ""; this.options_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new EnumValue(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private EnumValue(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { String s; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: s = input.readStringRequireUtf8(); this.name_ = s; continue;case 16: this.number_ = input.readInt32(); continue;case 26: if ((mutable_bitField0_ & 0x1) == 0) { this.options_ = new ArrayList<>(); mutable_bitField0_ |= 0x1; }  this.options_.add(input.readMessage(Option.parser(), extensionRegistry)); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x1) != 0) this.options_ = Collections.unmodifiableList(this.options_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return TypeProto.internal_static_google_protobuf_EnumValue_descriptor; }
/*      */   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return TypeProto.internal_static_google_protobuf_EnumValue_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)EnumValue.class, (Class)Builder.class); }
/*      */   public String getName() { Object ref = this.name_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.name_ = s; return s; }
/*  229 */   public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  230 */     if (isInitialized == 1) return true; 
/*  231 */     if (isInitialized == 0) return false;
/*      */     
/*  233 */     this.memoizedIsInitialized = 1;
/*  234 */     return true; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; } public int getNumber() { return this.number_; }
/*      */   public List<Option> getOptionsList() { return this.options_; }
/*      */   public List<? extends OptionOrBuilder> getOptionsOrBuilderList() { return (List)this.options_; }
/*      */   public int getOptionsCount() { return this.options_.size(); }
/*      */   public Option getOptions(int index) { return this.options_.get(index); }
/*      */   public OptionOrBuilder getOptionsOrBuilder(int index) { return this.options_.get(index); }
/*  240 */   public void writeTo(CodedOutputStream output) throws IOException { if (!getNameBytes().isEmpty()) {
/*  241 */       GeneratedMessageV3.writeString(output, 1, this.name_);
/*      */     }
/*  243 */     if (this.number_ != 0) {
/*  244 */       output.writeInt32(2, this.number_);
/*      */     }
/*  246 */     for (int i = 0; i < this.options_.size(); i++) {
/*  247 */       output.writeMessage(3, this.options_.get(i));
/*      */     }
/*  249 */     this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getSerializedSize() {
/*  254 */     int size = this.memoizedSize;
/*  255 */     if (size != -1) return size;
/*      */     
/*  257 */     size = 0;
/*  258 */     if (!getNameBytes().isEmpty()) {
/*  259 */       size += GeneratedMessageV3.computeStringSize(1, this.name_);
/*      */     }
/*  261 */     if (this.number_ != 0) {
/*  262 */       size += 
/*  263 */         CodedOutputStream.computeInt32Size(2, this.number_);
/*      */     }
/*  265 */     for (int i = 0; i < this.options_.size(); i++) {
/*  266 */       size += 
/*  267 */         CodedOutputStream.computeMessageSize(3, this.options_.get(i));
/*      */     }
/*  269 */     size += this.unknownFields.getSerializedSize();
/*  270 */     this.memoizedSize = size;
/*  271 */     return size;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object obj) {
/*  276 */     if (obj == this) {
/*  277 */       return true;
/*      */     }
/*  279 */     if (!(obj instanceof EnumValue)) {
/*  280 */       return super.equals(obj);
/*      */     }
/*  282 */     EnumValue other = (EnumValue)obj;
/*      */ 
/*      */     
/*  285 */     if (!getName().equals(other.getName())) return false; 
/*  286 */     if (getNumber() != other
/*  287 */       .getNumber()) return false;
/*      */     
/*  289 */     if (!getOptionsList().equals(other.getOptionsList())) return false; 
/*  290 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  291 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  296 */     if (this.memoizedHashCode != 0) {
/*  297 */       return this.memoizedHashCode;
/*      */     }
/*  299 */     int hash = 41;
/*  300 */     hash = 19 * hash + getDescriptor().hashCode();
/*  301 */     hash = 37 * hash + 1;
/*  302 */     hash = 53 * hash + getName().hashCode();
/*  303 */     hash = 37 * hash + 2;
/*  304 */     hash = 53 * hash + getNumber();
/*  305 */     if (getOptionsCount() > 0) {
/*  306 */       hash = 37 * hash + 3;
/*  307 */       hash = 53 * hash + getOptionsList().hashCode();
/*      */     } 
/*  309 */     hash = 29 * hash + this.unknownFields.hashCode();
/*  310 */     this.memoizedHashCode = hash;
/*  311 */     return hash;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static EnumValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  317 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static EnumValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  323 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */ 
/*      */   
/*      */   public static EnumValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  328 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static EnumValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  334 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static EnumValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  338 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static EnumValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  344 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static EnumValue parseFrom(InputStream input) throws IOException {
/*  348 */     return 
/*  349 */       GeneratedMessageV3.<EnumValue>parseWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static EnumValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  355 */     return 
/*  356 */       GeneratedMessageV3.<EnumValue>parseWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static EnumValue parseDelimitedFrom(InputStream input) throws IOException {
/*  360 */     return 
/*  361 */       GeneratedMessageV3.<EnumValue>parseDelimitedWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static EnumValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  367 */     return 
/*  368 */       GeneratedMessageV3.<EnumValue>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */ 
/*      */   
/*      */   public static EnumValue parseFrom(CodedInputStream input) throws IOException {
/*  373 */     return 
/*  374 */       GeneratedMessageV3.<EnumValue>parseWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static EnumValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  380 */     return 
/*  381 */       GeneratedMessageV3.<EnumValue>parseWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */   
/*      */   public Builder newBuilderForType() {
/*  385 */     return newBuilder();
/*      */   } public static Builder newBuilder() {
/*  387 */     return DEFAULT_INSTANCE.toBuilder();
/*      */   }
/*      */   public static Builder newBuilder(EnumValue prototype) {
/*  390 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */   }
/*      */   
/*      */   public Builder toBuilder() {
/*  394 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  395 */       .mergeFrom(this);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  401 */     Builder builder = new Builder(parent);
/*  402 */     return builder;
/*      */   }
/*      */ 
/*      */   
/*      */   public static final class Builder
/*      */     extends GeneratedMessageV3.Builder<Builder>
/*      */     implements EnumValueOrBuilder
/*      */   {
/*      */     private int bitField0_;
/*      */     private Object name_;
/*      */     private int number_;
/*      */     private List<Option> options_;
/*      */     private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
/*      */     
/*      */     public static final Descriptors.Descriptor getDescriptor() {
/*  417 */       return TypeProto.internal_static_google_protobuf_EnumValue_descriptor;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  423 */       return TypeProto.internal_static_google_protobuf_EnumValue_fieldAccessorTable
/*  424 */         .ensureFieldAccessorsInitialized((Class)EnumValue.class, (Class)Builder.class);
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
/*      */     private Builder()
/*      */     {
/*  606 */       this.name_ = "";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  744 */       this
/*  745 */         .options_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (GeneratedMessageV3.alwaysUseFieldBuilders) getOptionsFieldBuilder();  } public Builder clear() { super.clear(); this.name_ = ""; this.number_ = 0; if (this.optionsBuilder_ == null) { this.options_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; } else { this.optionsBuilder_.clear(); }  return this; } public Descriptors.Descriptor getDescriptorForType() { return TypeProto.internal_static_google_protobuf_EnumValue_descriptor; } public EnumValue getDefaultInstanceForType() { return EnumValue.getDefaultInstance(); } public EnumValue build() { EnumValue result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public EnumValue buildPartial() { EnumValue result = new EnumValue(this); int from_bitField0_ = this.bitField0_; result.name_ = this.name_; result.number_ = this.number_; if (this.optionsBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0) { this.options_ = Collections.unmodifiableList(this.options_); this.bitField0_ &= 0xFFFFFFFE; }  result.options_ = this.options_; } else { result.options_ = this.optionsBuilder_.build(); }  onBuilt(); return result; } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.name_ = ""; this.options_ = Collections.emptyList(); maybeForceBuilderInitialization(); }
/*      */     public Builder clone() { return super.clone(); }
/*  747 */     public Builder setField(Descriptors.FieldDescriptor field, Object value) { return super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return super.addRepeatedField(field, value); } private void ensureOptionsIsMutable() { if ((this.bitField0_ & 0x1) == 0)
/*  748 */       { this.options_ = new ArrayList<>(this.options_);
/*  749 */         this.bitField0_ |= 0x1; }  } public Builder mergeFrom(Message other) { if (other instanceof EnumValue)
/*      */         return mergeFrom((EnumValue)other);  super.mergeFrom(other); return this; }
/*      */     public Builder mergeFrom(EnumValue other) { if (other == EnumValue.getDefaultInstance())
/*      */         return this;  if (!other.getName().isEmpty()) { this.name_ = other.name_; onChanged(); }  if (other.getNumber() != 0)
/*      */         setNumber(other.getNumber());  if (this.optionsBuilder_ == null) { if (!other.options_.isEmpty()) { if (this.options_.isEmpty()) { this.options_ = other.options_; this.bitField0_ &= 0xFFFFFFFE; } else { ensureOptionsIsMutable(); this.options_.addAll(other.options_); }  onChanged(); }  } else if (!other.options_.isEmpty()) { if (this.optionsBuilder_.isEmpty()) { this.optionsBuilder_.dispose(); this.optionsBuilder_ = null; this.options_ = other.options_; this.bitField0_ &= 0xFFFFFFFE; this.optionsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? getOptionsFieldBuilder() : null; } else { this.optionsBuilder_.addAllMessages(other.options_); }  }  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */     public final boolean isInitialized() { return true; }
/*      */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { EnumValue parsedMessage = null; try { parsedMessage = EnumValue.PARSER.parsePartialFrom(input, extensionRegistry); }
/*      */       catch (InvalidProtocolBufferException e) { parsedMessage = (EnumValue)e.getUnfinishedMessage(); throw e.unwrapIOException(); }
/*      */       finally { if (parsedMessage != null)
/*      */           mergeFrom(parsedMessage);  }
/*      */        return this; }
/*      */     public String getName() { Object ref = this.name_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.name_ = s; return s; }
/*      */        return (String)ref; }
/*      */     public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }
/*      */        return (ByteString)ref; }
/*  764 */     public List<Option> getOptionsList() { if (this.optionsBuilder_ == null) {
/*  765 */         return Collections.unmodifiableList(this.options_);
/*      */       }
/*  767 */       return this.optionsBuilder_.getMessageList(); } public Builder setName(String value) { if (value == null)
/*      */         throw new NullPointerException();  this.name_ = value; onChanged(); return this; }
/*      */     public Builder clearName() { this.name_ = EnumValue.getDefaultInstance().getName(); onChanged(); return this; }
/*      */     public Builder setNameBytes(ByteString value) { if (value == null)
/*      */         throw new NullPointerException();  AbstractMessageLite.checkByteStringIsUtf8(value); this.name_ = value; onChanged(); return this; }
/*      */     public int getNumber() { return this.number_; }
/*      */     public Builder setNumber(int value) { this.number_ = value; onChanged();
/*      */       return this; }
/*      */     public Builder clearNumber() { this.number_ = 0;
/*      */       onChanged();
/*      */       return this; }
/*  778 */     public int getOptionsCount() { if (this.optionsBuilder_ == null) {
/*  779 */         return this.options_.size();
/*      */       }
/*  781 */       return this.optionsBuilder_.getCount(); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Option getOptions(int index) {
/*  792 */       if (this.optionsBuilder_ == null) {
/*  793 */         return this.options_.get(index);
/*      */       }
/*  795 */       return this.optionsBuilder_.getMessage(index);
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
/*      */     public Builder setOptions(int index, Option value) {
/*  807 */       if (this.optionsBuilder_ == null) {
/*  808 */         if (value == null) {
/*  809 */           throw new NullPointerException();
/*      */         }
/*  811 */         ensureOptionsIsMutable();
/*  812 */         this.options_.set(index, value);
/*  813 */         onChanged();
/*      */       } else {
/*  815 */         this.optionsBuilder_.setMessage(index, value);
/*      */       } 
/*  817 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setOptions(int index, Option.Builder builderForValue) {
/*  828 */       if (this.optionsBuilder_ == null) {
/*  829 */         ensureOptionsIsMutable();
/*  830 */         this.options_.set(index, builderForValue.build());
/*  831 */         onChanged();
/*      */       } else {
/*  833 */         this.optionsBuilder_.setMessage(index, builderForValue.build());
/*      */       } 
/*  835 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder addOptions(Option value) {
/*  845 */       if (this.optionsBuilder_ == null) {
/*  846 */         if (value == null) {
/*  847 */           throw new NullPointerException();
/*      */         }
/*  849 */         ensureOptionsIsMutable();
/*  850 */         this.options_.add(value);
/*  851 */         onChanged();
/*      */       } else {
/*  853 */         this.optionsBuilder_.addMessage(value);
/*      */       } 
/*  855 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder addOptions(int index, Option value) {
/*  866 */       if (this.optionsBuilder_ == null) {
/*  867 */         if (value == null) {
/*  868 */           throw new NullPointerException();
/*      */         }
/*  870 */         ensureOptionsIsMutable();
/*  871 */         this.options_.add(index, value);
/*  872 */         onChanged();
/*      */       } else {
/*  874 */         this.optionsBuilder_.addMessage(index, value);
/*      */       } 
/*  876 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder addOptions(Option.Builder builderForValue) {
/*  887 */       if (this.optionsBuilder_ == null) {
/*  888 */         ensureOptionsIsMutable();
/*  889 */         this.options_.add(builderForValue.build());
/*  890 */         onChanged();
/*      */       } else {
/*  892 */         this.optionsBuilder_.addMessage(builderForValue.build());
/*      */       } 
/*  894 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder addOptions(int index, Option.Builder builderForValue) {
/*  905 */       if (this.optionsBuilder_ == null) {
/*  906 */         ensureOptionsIsMutable();
/*  907 */         this.options_.add(index, builderForValue.build());
/*  908 */         onChanged();
/*      */       } else {
/*  910 */         this.optionsBuilder_.addMessage(index, builderForValue.build());
/*      */       } 
/*  912 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder addAllOptions(Iterable<? extends Option> values) {
/*  923 */       if (this.optionsBuilder_ == null) {
/*  924 */         ensureOptionsIsMutable();
/*  925 */         AbstractMessageLite.Builder.addAll(values, this.options_);
/*      */         
/*  927 */         onChanged();
/*      */       } else {
/*  929 */         this.optionsBuilder_.addAllMessages(values);
/*      */       } 
/*  931 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder clearOptions() {
/*  941 */       if (this.optionsBuilder_ == null) {
/*  942 */         this.options_ = Collections.emptyList();
/*  943 */         this.bitField0_ &= 0xFFFFFFFE;
/*  944 */         onChanged();
/*      */       } else {
/*  946 */         this.optionsBuilder_.clear();
/*      */       } 
/*  948 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder removeOptions(int index) {
/*  958 */       if (this.optionsBuilder_ == null) {
/*  959 */         ensureOptionsIsMutable();
/*  960 */         this.options_.remove(index);
/*  961 */         onChanged();
/*      */       } else {
/*  963 */         this.optionsBuilder_.remove(index);
/*      */       } 
/*  965 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Option.Builder getOptionsBuilder(int index) {
/*  976 */       return getOptionsFieldBuilder().getBuilder(index);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public OptionOrBuilder getOptionsOrBuilder(int index) {
/*  987 */       if (this.optionsBuilder_ == null)
/*  988 */         return this.options_.get(index); 
/*  989 */       return this.optionsBuilder_.getMessageOrBuilder(index);
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
/*      */     public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
/* 1001 */       if (this.optionsBuilder_ != null) {
/* 1002 */         return this.optionsBuilder_.getMessageOrBuilderList();
/*      */       }
/* 1004 */       return Collections.unmodifiableList((List)this.options_);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Option.Builder addOptionsBuilder() {
/* 1015 */       return getOptionsFieldBuilder().addBuilder(
/* 1016 */           Option.getDefaultInstance());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Option.Builder addOptionsBuilder(int index) {
/* 1027 */       return getOptionsFieldBuilder().addBuilder(index, 
/* 1028 */           Option.getDefaultInstance());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public List<Option.Builder> getOptionsBuilderList() {
/* 1039 */       return getOptionsFieldBuilder().getBuilderList();
/*      */     }
/*      */ 
/*      */     
/*      */     private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> getOptionsFieldBuilder() {
/* 1044 */       if (this.optionsBuilder_ == null) {
/* 1045 */         this
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1050 */           .optionsBuilder_ = new RepeatedFieldBuilderV3<>(this.options_, ((this.bitField0_ & 0x1) != 0), getParentForChildren(), isClean());
/* 1051 */         this.options_ = null;
/*      */       } 
/* 1053 */       return this.optionsBuilder_;
/*      */     }
/*      */ 
/*      */     
/*      */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 1058 */       return super.setUnknownFields(unknownFields);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 1064 */       return super.mergeUnknownFields(unknownFields);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1074 */   private static final EnumValue DEFAULT_INSTANCE = new EnumValue();
/*      */ 
/*      */   
/*      */   public static EnumValue getDefaultInstance() {
/* 1078 */     return DEFAULT_INSTANCE;
/*      */   }
/*      */ 
/*      */   
/* 1082 */   private static final Parser<EnumValue> PARSER = new AbstractParser<EnumValue>()
/*      */     {
/*      */ 
/*      */       
/*      */       public EnumValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */       {
/* 1088 */         return new EnumValue(input, extensionRegistry);
/*      */       }
/*      */     };
/*      */   
/*      */   public static Parser<EnumValue> parser() {
/* 1093 */     return PARSER;
/*      */   }
/*      */ 
/*      */   
/*      */   public Parser<EnumValue> getParserForType() {
/* 1098 */     return PARSER;
/*      */   }
/*      */ 
/*      */   
/*      */   public EnumValue getDefaultInstanceForType() {
/* 1103 */     return DEFAULT_INSTANCE;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\EnumValue.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */