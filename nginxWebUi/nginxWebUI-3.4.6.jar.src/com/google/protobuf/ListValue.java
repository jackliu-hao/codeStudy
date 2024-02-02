/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ public final class ListValue
/*     */   extends GeneratedMessageV3
/*     */   implements ListValueOrBuilder
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   public static final int VALUES_FIELD_NUMBER = 1;
/*     */   private List<Value> values_;
/*     */   private byte memoizedIsInitialized;
/*     */   
/*     */   private ListValue(GeneratedMessageV3.Builder<?> builder) {
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
/* 157 */     this.memoizedIsInitialized = -1; } private ListValue() { this.memoizedIsInitialized = -1; this.values_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new ListValue(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private ListValue(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: if ((mutable_bitField0_ & 0x1) == 0) { this.values_ = new ArrayList<>(); mutable_bitField0_ |= 0x1; }  this.values_.add(input.readMessage(Value.parser(), extensionRegistry)); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x1) != 0) this.values_ = Collections.unmodifiableList(this.values_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  }
/*     */   public static final Descriptors.Descriptor getDescriptor() { return StructProto.internal_static_google_protobuf_ListValue_descriptor; }
/*     */   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return StructProto.internal_static_google_protobuf_ListValue_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)ListValue.class, (Class)Builder.class); }
/* 160 */   public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 161 */     if (isInitialized == 1) return true; 
/* 162 */     if (isInitialized == 0) return false;
/*     */     
/* 164 */     this.memoizedIsInitialized = 1;
/* 165 */     return true; } public List<Value> getValuesList() { return this.values_; }
/*     */   public List<? extends ValueOrBuilder> getValuesOrBuilderList() { return (List)this.values_; }
/*     */   public int getValuesCount() { return this.values_.size(); }
/*     */   public Value getValues(int index) { return this.values_.get(index); }
/*     */   public ValueOrBuilder getValuesOrBuilder(int index) { return this.values_.get(index); }
/*     */   public void writeTo(CodedOutputStream output) throws IOException {
/* 171 */     for (int i = 0; i < this.values_.size(); i++) {
/* 172 */       output.writeMessage(1, this.values_.get(i));
/*     */     }
/* 174 */     this.unknownFields.writeTo(output);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/* 179 */     int size = this.memoizedSize;
/* 180 */     if (size != -1) return size;
/*     */     
/* 182 */     size = 0;
/* 183 */     for (int i = 0; i < this.values_.size(); i++) {
/* 184 */       size += 
/* 185 */         CodedOutputStream.computeMessageSize(1, this.values_.get(i));
/*     */     }
/* 187 */     size += this.unknownFields.getSerializedSize();
/* 188 */     this.memoizedSize = size;
/* 189 */     return size;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 194 */     if (obj == this) {
/* 195 */       return true;
/*     */     }
/* 197 */     if (!(obj instanceof ListValue)) {
/* 198 */       return super.equals(obj);
/*     */     }
/* 200 */     ListValue other = (ListValue)obj;
/*     */ 
/*     */     
/* 203 */     if (!getValuesList().equals(other.getValuesList())) return false; 
/* 204 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 205 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 210 */     if (this.memoizedHashCode != 0) {
/* 211 */       return this.memoizedHashCode;
/*     */     }
/* 213 */     int hash = 41;
/* 214 */     hash = 19 * hash + getDescriptor().hashCode();
/* 215 */     if (getValuesCount() > 0) {
/* 216 */       hash = 37 * hash + 1;
/* 217 */       hash = 53 * hash + getValuesList().hashCode();
/*     */     } 
/* 219 */     hash = 29 * hash + this.unknownFields.hashCode();
/* 220 */     this.memoizedHashCode = hash;
/* 221 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static ListValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 227 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static ListValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 233 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ListValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 238 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static ListValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 244 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static ListValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 248 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static ListValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 254 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static ListValue parseFrom(InputStream input) throws IOException {
/* 258 */     return 
/* 259 */       GeneratedMessageV3.<ListValue>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static ListValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 265 */     return 
/* 266 */       GeneratedMessageV3.<ListValue>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static ListValue parseDelimitedFrom(InputStream input) throws IOException {
/* 270 */     return 
/* 271 */       GeneratedMessageV3.<ListValue>parseDelimitedWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static ListValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 277 */     return 
/* 278 */       GeneratedMessageV3.<ListValue>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ListValue parseFrom(CodedInputStream input) throws IOException {
/* 283 */     return 
/* 284 */       GeneratedMessageV3.<ListValue>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static ListValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 290 */     return 
/* 291 */       GeneratedMessageV3.<ListValue>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public Builder newBuilderForType() {
/* 295 */     return newBuilder();
/*     */   } public static Builder newBuilder() {
/* 297 */     return DEFAULT_INSTANCE.toBuilder();
/*     */   }
/*     */   public static Builder newBuilder(ListValue prototype) {
/* 300 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*     */   }
/*     */   
/*     */   public Builder toBuilder() {
/* 304 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 305 */       .mergeFrom(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 311 */     Builder builder = new Builder(parent);
/* 312 */     return builder;
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class Builder
/*     */     extends GeneratedMessageV3.Builder<Builder>
/*     */     implements ListValueOrBuilder
/*     */   {
/*     */     private int bitField0_;
/*     */     
/*     */     private List<Value> values_;
/*     */     
/*     */     private RepeatedFieldBuilderV3<Value, Value.Builder, ValueOrBuilder> valuesBuilder_;
/*     */ 
/*     */     
/*     */     public static final Descriptors.Descriptor getDescriptor() {
/* 328 */       return StructProto.internal_static_google_protobuf_ListValue_descriptor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 334 */       return StructProto.internal_static_google_protobuf_ListValue_fieldAccessorTable
/* 335 */         .ensureFieldAccessorsInitialized((Class)ListValue.class, (Class)Builder.class);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/* 504 */       this
/* 505 */         .values_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.values_ = Collections.emptyList(); maybeForceBuilderInitialization(); }
/*     */     private void maybeForceBuilderInitialization() { if (GeneratedMessageV3.alwaysUseFieldBuilders) getValuesFieldBuilder();  }
/* 507 */     public Builder clear() { super.clear(); if (this.valuesBuilder_ == null) { this.values_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; } else { this.valuesBuilder_.clear(); }  return this; } public Descriptors.Descriptor getDescriptorForType() { return StructProto.internal_static_google_protobuf_ListValue_descriptor; } public ListValue getDefaultInstanceForType() { return ListValue.getDefaultInstance(); } private void ensureValuesIsMutable() { if ((this.bitField0_ & 0x1) == 0) {
/* 508 */         this.values_ = new ArrayList<>(this.values_);
/* 509 */         this.bitField0_ |= 0x1;
/*     */       }  }
/*     */     public ListValue build() { ListValue result = buildPartial(); if (!result.isInitialized())
/*     */         throw newUninitializedMessageException(result);  return result; }
/*     */     public ListValue buildPartial() { ListValue result = new ListValue(this); int from_bitField0_ = this.bitField0_; if (this.valuesBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0) {
/*     */           this.values_ = Collections.unmodifiableList(this.values_); this.bitField0_ &= 0xFFFFFFFE;
/*     */         }  result.values_ = this.values_; }
/*     */       else
/*     */       { result.values_ = this.valuesBuilder_.build(); }
/*     */        onBuilt(); return result; }
/*     */     public Builder clone() { return super.clone(); }
/*     */     public Builder setField(Descriptors.FieldDescriptor field, Object value) { return super.setField(field, value); }
/*     */     public Builder clearField(Descriptors.FieldDescriptor field) { return super.clearField(field); }
/*     */     public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return super.clearOneof(oneof); }
/*     */     public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return super.setRepeatedField(field, index, value); }
/* 524 */     public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return super.addRepeatedField(field, value); } public List<Value> getValuesList() { if (this.valuesBuilder_ == null) {
/* 525 */         return Collections.unmodifiableList(this.values_);
/*     */       }
/* 527 */       return this.valuesBuilder_.getMessageList(); } public Builder mergeFrom(Message other) { if (other instanceof ListValue)
/*     */         return mergeFrom((ListValue)other);  super.mergeFrom(other); return this; }
/*     */     public Builder mergeFrom(ListValue other) { if (other == ListValue.getDefaultInstance())
/*     */         return this;  if (this.valuesBuilder_ == null) { if (!other.values_.isEmpty()) { if (this.values_.isEmpty()) { this.values_ = other.values_; this.bitField0_ &= 0xFFFFFFFE; } else { ensureValuesIsMutable(); this.values_.addAll(other.values_); }  onChanged(); }  } else if (!other.values_.isEmpty()) { if (this.valuesBuilder_.isEmpty()) { this.valuesBuilder_.dispose(); this.valuesBuilder_ = null; this.values_ = other.values_; this.bitField0_ &= 0xFFFFFFFE; this.valuesBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? getValuesFieldBuilder() : null; } else { this.valuesBuilder_.addAllMessages(other.values_); }  }
/*     */        mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*     */     public final boolean isInitialized() { return true; }
/*     */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { ListValue parsedMessage = null; try { parsedMessage = ListValue.PARSER.parsePartialFrom(input, extensionRegistry); }
/*     */       catch (InvalidProtocolBufferException e) { parsedMessage = (ListValue)e.getUnfinishedMessage(); throw e.unwrapIOException(); }
/*     */       finally { if (parsedMessage != null)
/*     */           mergeFrom(parsedMessage);  }
/*     */        return this; }
/* 538 */     public int getValuesCount() { if (this.valuesBuilder_ == null) {
/* 539 */         return this.values_.size();
/*     */       }
/* 541 */       return this.valuesBuilder_.getCount(); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value getValues(int index) {
/* 552 */       if (this.valuesBuilder_ == null) {
/* 553 */         return this.values_.get(index);
/*     */       }
/* 555 */       return this.valuesBuilder_.getMessage(index);
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
/*     */     public Builder setValues(int index, Value value) {
/* 567 */       if (this.valuesBuilder_ == null) {
/* 568 */         if (value == null) {
/* 569 */           throw new NullPointerException();
/*     */         }
/* 571 */         ensureValuesIsMutable();
/* 572 */         this.values_.set(index, value);
/* 573 */         onChanged();
/*     */       } else {
/* 575 */         this.valuesBuilder_.setMessage(index, value);
/*     */       } 
/* 577 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setValues(int index, Value.Builder builderForValue) {
/* 588 */       if (this.valuesBuilder_ == null) {
/* 589 */         ensureValuesIsMutable();
/* 590 */         this.values_.set(index, builderForValue.build());
/* 591 */         onChanged();
/*     */       } else {
/* 593 */         this.valuesBuilder_.setMessage(index, builderForValue.build());
/*     */       } 
/* 595 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addValues(Value value) {
/* 605 */       if (this.valuesBuilder_ == null) {
/* 606 */         if (value == null) {
/* 607 */           throw new NullPointerException();
/*     */         }
/* 609 */         ensureValuesIsMutable();
/* 610 */         this.values_.add(value);
/* 611 */         onChanged();
/*     */       } else {
/* 613 */         this.valuesBuilder_.addMessage(value);
/*     */       } 
/* 615 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addValues(int index, Value value) {
/* 626 */       if (this.valuesBuilder_ == null) {
/* 627 */         if (value == null) {
/* 628 */           throw new NullPointerException();
/*     */         }
/* 630 */         ensureValuesIsMutable();
/* 631 */         this.values_.add(index, value);
/* 632 */         onChanged();
/*     */       } else {
/* 634 */         this.valuesBuilder_.addMessage(index, value);
/*     */       } 
/* 636 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addValues(Value.Builder builderForValue) {
/* 647 */       if (this.valuesBuilder_ == null) {
/* 648 */         ensureValuesIsMutable();
/* 649 */         this.values_.add(builderForValue.build());
/* 650 */         onChanged();
/*     */       } else {
/* 652 */         this.valuesBuilder_.addMessage(builderForValue.build());
/*     */       } 
/* 654 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addValues(int index, Value.Builder builderForValue) {
/* 665 */       if (this.valuesBuilder_ == null) {
/* 666 */         ensureValuesIsMutable();
/* 667 */         this.values_.add(index, builderForValue.build());
/* 668 */         onChanged();
/*     */       } else {
/* 670 */         this.valuesBuilder_.addMessage(index, builderForValue.build());
/*     */       } 
/* 672 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addAllValues(Iterable<? extends Value> values) {
/* 683 */       if (this.valuesBuilder_ == null) {
/* 684 */         ensureValuesIsMutable();
/* 685 */         AbstractMessageLite.Builder.addAll(values, this.values_);
/*     */         
/* 687 */         onChanged();
/*     */       } else {
/* 689 */         this.valuesBuilder_.addAllMessages(values);
/*     */       } 
/* 691 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder clearValues() {
/* 701 */       if (this.valuesBuilder_ == null) {
/* 702 */         this.values_ = Collections.emptyList();
/* 703 */         this.bitField0_ &= 0xFFFFFFFE;
/* 704 */         onChanged();
/*     */       } else {
/* 706 */         this.valuesBuilder_.clear();
/*     */       } 
/* 708 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder removeValues(int index) {
/* 718 */       if (this.valuesBuilder_ == null) {
/* 719 */         ensureValuesIsMutable();
/* 720 */         this.values_.remove(index);
/* 721 */         onChanged();
/*     */       } else {
/* 723 */         this.valuesBuilder_.remove(index);
/*     */       } 
/* 725 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value.Builder getValuesBuilder(int index) {
/* 736 */       return getValuesFieldBuilder().getBuilder(index);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ValueOrBuilder getValuesOrBuilder(int index) {
/* 747 */       if (this.valuesBuilder_ == null)
/* 748 */         return this.values_.get(index); 
/* 749 */       return this.valuesBuilder_.getMessageOrBuilder(index);
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
/*     */     public List<? extends ValueOrBuilder> getValuesOrBuilderList() {
/* 761 */       if (this.valuesBuilder_ != null) {
/* 762 */         return this.valuesBuilder_.getMessageOrBuilderList();
/*     */       }
/* 764 */       return Collections.unmodifiableList((List)this.values_);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value.Builder addValuesBuilder() {
/* 775 */       return getValuesFieldBuilder().addBuilder(
/* 776 */           Value.getDefaultInstance());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value.Builder addValuesBuilder(int index) {
/* 787 */       return getValuesFieldBuilder().addBuilder(index, 
/* 788 */           Value.getDefaultInstance());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public List<Value.Builder> getValuesBuilderList() {
/* 799 */       return getValuesFieldBuilder().getBuilderList();
/*     */     }
/*     */ 
/*     */     
/*     */     private RepeatedFieldBuilderV3<Value, Value.Builder, ValueOrBuilder> getValuesFieldBuilder() {
/* 804 */       if (this.valuesBuilder_ == null) {
/* 805 */         this
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 810 */           .valuesBuilder_ = new RepeatedFieldBuilderV3<>(this.values_, ((this.bitField0_ & 0x1) != 0), getParentForChildren(), isClean());
/* 811 */         this.values_ = null;
/*     */       } 
/* 813 */       return this.valuesBuilder_;
/*     */     }
/*     */ 
/*     */     
/*     */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 818 */       return super.setUnknownFields(unknownFields);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 824 */       return super.mergeUnknownFields(unknownFields);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 834 */   private static final ListValue DEFAULT_INSTANCE = new ListValue();
/*     */ 
/*     */   
/*     */   public static ListValue getDefaultInstance() {
/* 838 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ 
/*     */   
/* 842 */   private static final Parser<ListValue> PARSER = new AbstractParser<ListValue>()
/*     */     {
/*     */ 
/*     */       
/*     */       public ListValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */       {
/* 848 */         return new ListValue(input, extensionRegistry);
/*     */       }
/*     */     };
/*     */   
/*     */   public static Parser<ListValue> parser() {
/* 853 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<ListValue> getParserForType() {
/* 858 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public ListValue getDefaultInstanceForType() {
/* 863 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ListValue.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */