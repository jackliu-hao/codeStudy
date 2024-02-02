/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Option
/*     */   extends GeneratedMessageV3
/*     */   implements OptionOrBuilder
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   public static final int NAME_FIELD_NUMBER = 1;
/*     */   private volatile Object name_;
/*     */   public static final int VALUE_FIELD_NUMBER = 2;
/*     */   private Any value_;
/*     */   private byte memoizedIsInitialized;
/*     */   
/*     */   private Option(GeneratedMessageV3.Builder<?> builder) {
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 202 */     this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Option(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Option(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { String s; Any.Builder subBuilder; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: s = input.readStringRequireUtf8(); this.name_ = s; continue;case 18: subBuilder = null; if (this.value_ != null) subBuilder = this.value_.toBuilder();  this.value_ = input.<Any>readMessage(Any.parser(), extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.value_); this.value_ = subBuilder.buildPartial(); }  continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } private Option() { this.memoizedIsInitialized = -1;
/*     */     this.name_ = ""; }
/*     */   public static final Descriptors.Descriptor getDescriptor() { return TypeProto.internal_static_google_protobuf_Option_descriptor; }
/* 205 */   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return TypeProto.internal_static_google_protobuf_Option_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Option.class, (Class)Builder.class); } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 206 */     if (isInitialized == 1) return true; 
/* 207 */     if (isInitialized == 0) return false;
/*     */     
/* 209 */     this.memoizedIsInitialized = 1;
/* 210 */     return true; } public String getName() { Object ref = this.name_; if (ref instanceof String)
/*     */       return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.name_ = s; return s; }
/*     */   public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; }
/*     */   public boolean hasValue() { return (this.value_ != null); }
/*     */   public Any getValue() { return (this.value_ == null) ? Any.getDefaultInstance() : this.value_; }
/*     */   public AnyOrBuilder getValueOrBuilder() { return getValue(); }
/* 216 */   public void writeTo(CodedOutputStream output) throws IOException { if (!getNameBytes().isEmpty()) {
/* 217 */       GeneratedMessageV3.writeString(output, 1, this.name_);
/*     */     }
/* 219 */     if (this.value_ != null) {
/* 220 */       output.writeMessage(2, getValue());
/*     */     }
/* 222 */     this.unknownFields.writeTo(output); }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/* 227 */     int size = this.memoizedSize;
/* 228 */     if (size != -1) return size;
/*     */     
/* 230 */     size = 0;
/* 231 */     if (!getNameBytes().isEmpty()) {
/* 232 */       size += GeneratedMessageV3.computeStringSize(1, this.name_);
/*     */     }
/* 234 */     if (this.value_ != null) {
/* 235 */       size += 
/* 236 */         CodedOutputStream.computeMessageSize(2, getValue());
/*     */     }
/* 238 */     size += this.unknownFields.getSerializedSize();
/* 239 */     this.memoizedSize = size;
/* 240 */     return size;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 245 */     if (obj == this) {
/* 246 */       return true;
/*     */     }
/* 248 */     if (!(obj instanceof Option)) {
/* 249 */       return super.equals(obj);
/*     */     }
/* 251 */     Option other = (Option)obj;
/*     */ 
/*     */     
/* 254 */     if (!getName().equals(other.getName())) return false; 
/* 255 */     if (hasValue() != other.hasValue()) return false; 
/* 256 */     if (hasValue() && 
/*     */       
/* 258 */       !getValue().equals(other.getValue())) return false;
/*     */     
/* 260 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 261 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 266 */     if (this.memoizedHashCode != 0) {
/* 267 */       return this.memoizedHashCode;
/*     */     }
/* 269 */     int hash = 41;
/* 270 */     hash = 19 * hash + getDescriptor().hashCode();
/* 271 */     hash = 37 * hash + 1;
/* 272 */     hash = 53 * hash + getName().hashCode();
/* 273 */     if (hasValue()) {
/* 274 */       hash = 37 * hash + 2;
/* 275 */       hash = 53 * hash + getValue().hashCode();
/*     */     } 
/* 277 */     hash = 29 * hash + this.unknownFields.hashCode();
/* 278 */     this.memoizedHashCode = hash;
/* 279 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Option parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 285 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Option parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 291 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Option parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 296 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Option parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 302 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Option parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 306 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Option parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 312 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Option parseFrom(InputStream input) throws IOException {
/* 316 */     return 
/* 317 */       GeneratedMessageV3.<Option>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Option parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 323 */     return 
/* 324 */       GeneratedMessageV3.<Option>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Option parseDelimitedFrom(InputStream input) throws IOException {
/* 328 */     return 
/* 329 */       GeneratedMessageV3.<Option>parseDelimitedWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Option parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 335 */     return 
/* 336 */       GeneratedMessageV3.<Option>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Option parseFrom(CodedInputStream input) throws IOException {
/* 341 */     return 
/* 342 */       GeneratedMessageV3.<Option>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Option parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 348 */     return 
/* 349 */       GeneratedMessageV3.<Option>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public Builder newBuilderForType() {
/* 353 */     return newBuilder();
/*     */   } public static Builder newBuilder() {
/* 355 */     return DEFAULT_INSTANCE.toBuilder();
/*     */   }
/*     */   public static Builder newBuilder(Option prototype) {
/* 358 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*     */   }
/*     */   
/*     */   public Builder toBuilder() {
/* 362 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 363 */       .mergeFrom(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 369 */     Builder builder = new Builder(parent);
/* 370 */     return builder;
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class Builder
/*     */     extends GeneratedMessageV3.Builder<Builder>
/*     */     implements OptionOrBuilder
/*     */   {
/*     */     private Object name_;
/*     */     
/*     */     private Any value_;
/*     */     
/*     */     private SingleFieldBuilderV3<Any, Any.Builder, AnyOrBuilder> valueBuilder_;
/*     */ 
/*     */     
/*     */     public static final Descriptors.Descriptor getDescriptor() {
/* 386 */       return TypeProto.internal_static_google_protobuf_Option_descriptor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 392 */       return TypeProto.internal_static_google_protobuf_Option_fieldAccessorTable
/* 393 */         .ensureFieldAccessorsInitialized((Class)Option.class, (Class)Builder.class);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/* 539 */       this.name_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.name_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (GeneratedMessageV3.alwaysUseFieldBuilders); }
/*     */     public Builder clear() { super.clear(); this.name_ = ""; if (this.valueBuilder_ == null) { this.value_ = null; }
/*     */       else { this.value_ = null; this.valueBuilder_ = null; }
/*     */        return this; }
/*     */     public Descriptors.Descriptor getDescriptorForType() { return TypeProto.internal_static_google_protobuf_Option_descriptor; }
/*     */     public Option getDefaultInstanceForType() { return Option.getDefaultInstance(); }
/*     */     public Option build() { Option result = buildPartial(); if (!result.isInitialized())
/*     */         throw newUninitializedMessageException(result);  return result; }
/*     */     public Option buildPartial() { Option result = new Option(this); result.name_ = this.name_; if (this.valueBuilder_ == null) { result.value_ = this.value_; }
/*     */       else { result.value_ = this.valueBuilder_.build(); }
/*     */        onBuilt(); return result; }
/*     */     public Builder clone() { return super.clone(); }
/*     */     public Builder setField(Descriptors.FieldDescriptor field, Object value) { return super.setField(field, value); }
/* 552 */     public String getName() { Object ref = this.name_;
/* 553 */       if (!(ref instanceof String)) {
/* 554 */         ByteString bs = (ByteString)ref;
/*     */         
/* 556 */         String s = bs.toStringUtf8();
/* 557 */         this.name_ = s;
/* 558 */         return s;
/*     */       } 
/* 560 */       return (String)ref; } public Builder clearField(Descriptors.FieldDescriptor field) { return super.clearField(field); }
/*     */     public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return super.clearOneof(oneof); }
/*     */     public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return super.setRepeatedField(field, index, value); }
/*     */     public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return super.addRepeatedField(field, value); }
/*     */     public Builder mergeFrom(Message other) { if (other instanceof Option)
/*     */         return mergeFrom((Option)other);  super.mergeFrom(other); return this; }
/*     */     public Builder mergeFrom(Option other) { if (other == Option.getDefaultInstance())
/*     */         return this;  if (!other.getName().isEmpty()) { this.name_ = other.name_; onChanged(); }
/*     */        if (other.hasValue())
/*     */         mergeValue(other.getValue());  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*     */     public final boolean isInitialized() { return true; }
/*     */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { Option parsedMessage = null; try { parsedMessage = Option.PARSER.parsePartialFrom(input, extensionRegistry); }
/*     */       catch (InvalidProtocolBufferException e) { parsedMessage = (Option)e.getUnfinishedMessage(); throw e.unwrapIOException(); }
/*     */       finally { if (parsedMessage != null)
/*     */           mergeFrom(parsedMessage);  }
/*     */        return this; }
/* 576 */     public ByteString getNameBytes() { Object ref = this.name_;
/* 577 */       if (ref instanceof String) {
/*     */         
/* 579 */         ByteString b = ByteString.copyFromUtf8((String)ref);
/*     */         
/* 581 */         this.name_ = b;
/* 582 */         return b;
/*     */       } 
/* 584 */       return (ByteString)ref; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setName(String value) {
/* 601 */       if (value == null) {
/* 602 */         throw new NullPointerException();
/*     */       }
/*     */       
/* 605 */       this.name_ = value;
/* 606 */       onChanged();
/* 607 */       return this;
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
/*     */     public Builder clearName() {
/* 622 */       this.name_ = Option.getDefaultInstance().getName();
/* 623 */       onChanged();
/* 624 */       return this;
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
/*     */     public Builder setNameBytes(ByteString value) {
/* 640 */       if (value == null) {
/* 641 */         throw new NullPointerException();
/*     */       }
/* 643 */       AbstractMessageLite.checkByteStringIsUtf8(value);
/*     */       
/* 645 */       this.name_ = value;
/* 646 */       onChanged();
/* 647 */       return this;
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
/*     */     public boolean hasValue() {
/* 665 */       return (this.valueBuilder_ != null || this.value_ != null);
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
/*     */     public Any getValue() {
/* 679 */       if (this.valueBuilder_ == null) {
/* 680 */         return (this.value_ == null) ? Any.getDefaultInstance() : this.value_;
/*     */       }
/* 682 */       return this.valueBuilder_.getMessage();
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
/*     */     public Builder setValue(Any value) {
/* 696 */       if (this.valueBuilder_ == null) {
/* 697 */         if (value == null) {
/* 698 */           throw new NullPointerException();
/*     */         }
/* 700 */         this.value_ = value;
/* 701 */         onChanged();
/*     */       } else {
/* 703 */         this.valueBuilder_.setMessage(value);
/*     */       } 
/*     */       
/* 706 */       return this;
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
/*     */     public Builder setValue(Any.Builder builderForValue) {
/* 720 */       if (this.valueBuilder_ == null) {
/* 721 */         this.value_ = builderForValue.build();
/* 722 */         onChanged();
/*     */       } else {
/* 724 */         this.valueBuilder_.setMessage(builderForValue.build());
/*     */       } 
/*     */       
/* 727 */       return this;
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
/*     */     public Builder mergeValue(Any value) {
/* 740 */       if (this.valueBuilder_ == null) {
/* 741 */         if (this.value_ != null) {
/* 742 */           this
/* 743 */             .value_ = Any.newBuilder(this.value_).mergeFrom(value).buildPartial();
/*     */         } else {
/* 745 */           this.value_ = value;
/*     */         } 
/* 747 */         onChanged();
/*     */       } else {
/* 749 */         this.valueBuilder_.mergeFrom(value);
/*     */       } 
/*     */       
/* 752 */       return this;
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
/*     */     public Builder clearValue() {
/* 765 */       if (this.valueBuilder_ == null) {
/* 766 */         this.value_ = null;
/* 767 */         onChanged();
/*     */       } else {
/* 769 */         this.value_ = null;
/* 770 */         this.valueBuilder_ = null;
/*     */       } 
/*     */       
/* 773 */       return this;
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
/*     */     public Any.Builder getValueBuilder() {
/* 787 */       onChanged();
/* 788 */       return getValueFieldBuilder().getBuilder();
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
/*     */     public AnyOrBuilder getValueOrBuilder() {
/* 801 */       if (this.valueBuilder_ != null) {
/* 802 */         return this.valueBuilder_.getMessageOrBuilder();
/*     */       }
/* 804 */       return (this.value_ == null) ? 
/* 805 */         Any.getDefaultInstance() : this.value_;
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
/*     */     private SingleFieldBuilderV3<Any, Any.Builder, AnyOrBuilder> getValueFieldBuilder() {
/* 821 */       if (this.valueBuilder_ == null) {
/* 822 */         this
/*     */ 
/*     */ 
/*     */           
/* 826 */           .valueBuilder_ = new SingleFieldBuilderV3<>(getValue(), getParentForChildren(), isClean());
/* 827 */         this.value_ = null;
/*     */       } 
/* 829 */       return this.valueBuilder_;
/*     */     }
/*     */ 
/*     */     
/*     */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 834 */       return super.setUnknownFields(unknownFields);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 840 */       return super.mergeUnknownFields(unknownFields);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 850 */   private static final Option DEFAULT_INSTANCE = new Option();
/*     */ 
/*     */   
/*     */   public static Option getDefaultInstance() {
/* 854 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ 
/*     */   
/* 858 */   private static final Parser<Option> PARSER = new AbstractParser<Option>()
/*     */     {
/*     */ 
/*     */       
/*     */       public Option parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */       {
/* 864 */         return new Option(input, extensionRegistry);
/*     */       }
/*     */     };
/*     */   
/*     */   public static Parser<Option> parser() {
/* 869 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<Option> getParserForType() {
/* 874 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Option getDefaultInstanceForType() {
/* 879 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Option.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */