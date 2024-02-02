/*      */ package com.google.protobuf;
/*      */ 
/*      */ 
/*      */ public final class Type extends GeneratedMessageV3 implements TypeOrBuilder {
/*      */   private static final long serialVersionUID = 0L;
/*      */   public static final int NAME_FIELD_NUMBER = 1;
/*      */   private volatile Object name_;
/*      */   public static final int FIELDS_FIELD_NUMBER = 2;
/*      */   private List<Field> fields_;
/*      */   public static final int ONEOFS_FIELD_NUMBER = 3;
/*      */   private LazyStringList oneofs_;
/*      */   public static final int OPTIONS_FIELD_NUMBER = 4;
/*      */   private List<Option> options_;
/*      */   public static final int SOURCE_CONTEXT_FIELD_NUMBER = 5;
/*      */   private SourceContext sourceContext_;
/*      */   public static final int SYNTAX_FIELD_NUMBER = 6;
/*      */   private int syntax_;
/*      */   private byte memoizedIsInitialized;
/*      */   
/*   20 */   private Type(GeneratedMessageV3.Builder<?> builder) { super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  421 */     this.memoizedIsInitialized = -1; } private Type() { this.memoizedIsInitialized = -1; this.name_ = ""; this.fields_ = Collections.emptyList(); this.oneofs_ = LazyStringArrayList.EMPTY; this.options_ = Collections.emptyList(); this.syntax_ = 0; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Type(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Type(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { String s; SourceContext.Builder subBuilder; int rawValue, tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: s = input.readStringRequireUtf8(); this.name_ = s; continue;case 18: if ((mutable_bitField0_ & 0x1) == 0) { this.fields_ = new ArrayList<>(); mutable_bitField0_ |= 0x1; }  this.fields_.add(input.readMessage(Field.parser(), extensionRegistry)); continue;case 26: s = input.readStringRequireUtf8(); if ((mutable_bitField0_ & 0x2) == 0) { this.oneofs_ = new LazyStringArrayList(); mutable_bitField0_ |= 0x2; }  this.oneofs_.add(s); continue;case 34: if ((mutable_bitField0_ & 0x4) == 0) { this.options_ = new ArrayList<>(); mutable_bitField0_ |= 0x4; }  this.options_.add(input.readMessage(Option.parser(), extensionRegistry)); continue;case 42: subBuilder = null; if (this.sourceContext_ != null) subBuilder = this.sourceContext_.toBuilder();  this.sourceContext_ = input.<SourceContext>readMessage(SourceContext.parser(), extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.sourceContext_); this.sourceContext_ = subBuilder.buildPartial(); }  continue;case 48: rawValue = input.readEnum(); this.syntax_ = rawValue; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x1) != 0) this.fields_ = Collections.unmodifiableList(this.fields_);  if ((mutable_bitField0_ & 0x2) != 0) this.oneofs_ = this.oneofs_.getUnmodifiableView();  if ((mutable_bitField0_ & 0x4) != 0) this.options_ = Collections.unmodifiableList(this.options_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return TypeProto.internal_static_google_protobuf_Type_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return TypeProto.internal_static_google_protobuf_Type_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Type.class, (Class)Builder.class); } public String getName() { Object ref = this.name_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.name_ = s; return s; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; } public List<Field> getFieldsList() { return this.fields_; } public List<? extends FieldOrBuilder> getFieldsOrBuilderList() { return (List)this.fields_; } public int getFieldsCount() { return this.fields_.size(); } public Field getFields(int index) { return this.fields_.get(index); } public FieldOrBuilder getFieldsOrBuilder(int index) { return this.fields_.get(index); } public ProtocolStringList getOneofsList() { return this.oneofs_; } public int getOneofsCount() { return this.oneofs_.size(); } public String getOneofs(int index) { return this.oneofs_.get(index); } public ByteString getOneofsBytes(int index) { return this.oneofs_.getByteString(index); } public List<Option> getOptionsList() { return this.options_; } public List<? extends OptionOrBuilder> getOptionsOrBuilderList() { return (List)this.options_; } public int getOptionsCount() { return this.options_.size(); } public Option getOptions(int index) { return this.options_.get(index); } public OptionOrBuilder getOptionsOrBuilder(int index) { return this.options_.get(index); } public boolean hasSourceContext() { return (this.sourceContext_ != null); } public SourceContext getSourceContext() { return (this.sourceContext_ == null) ? SourceContext.getDefaultInstance() : this.sourceContext_; } public SourceContextOrBuilder getSourceContextOrBuilder() { return getSourceContext(); }
/*      */   public int getSyntaxValue() { return this.syntax_; }
/*      */   public Syntax getSyntax() { Syntax result = Syntax.valueOf(this.syntax_); return (result == null) ? Syntax.UNRECOGNIZED : result; }
/*  424 */   public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  425 */     if (isInitialized == 1) return true; 
/*  426 */     if (isInitialized == 0) return false;
/*      */     
/*  428 */     this.memoizedIsInitialized = 1;
/*  429 */     return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeTo(CodedOutputStream output) throws IOException {
/*  435 */     if (!getNameBytes().isEmpty())
/*  436 */       GeneratedMessageV3.writeString(output, 1, this.name_); 
/*      */     int i;
/*  438 */     for (i = 0; i < this.fields_.size(); i++) {
/*  439 */       output.writeMessage(2, this.fields_.get(i));
/*      */     }
/*  441 */     for (i = 0; i < this.oneofs_.size(); i++) {
/*  442 */       GeneratedMessageV3.writeString(output, 3, this.oneofs_.getRaw(i));
/*      */     }
/*  444 */     for (i = 0; i < this.options_.size(); i++) {
/*  445 */       output.writeMessage(4, this.options_.get(i));
/*      */     }
/*  447 */     if (this.sourceContext_ != null) {
/*  448 */       output.writeMessage(5, getSourceContext());
/*      */     }
/*  450 */     if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
/*  451 */       output.writeEnum(6, this.syntax_);
/*      */     }
/*  453 */     this.unknownFields.writeTo(output);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getSerializedSize() {
/*  458 */     int size = this.memoizedSize;
/*  459 */     if (size != -1) return size;
/*      */     
/*  461 */     size = 0;
/*  462 */     if (!getNameBytes().isEmpty()) {
/*  463 */       size += GeneratedMessageV3.computeStringSize(1, this.name_);
/*      */     }
/*  465 */     for (int j = 0; j < this.fields_.size(); j++) {
/*  466 */       size += 
/*  467 */         CodedOutputStream.computeMessageSize(2, this.fields_.get(j));
/*      */     }
/*      */     
/*  470 */     int dataSize = 0;
/*  471 */     for (int k = 0; k < this.oneofs_.size(); k++) {
/*  472 */       dataSize += computeStringSizeNoTag(this.oneofs_.getRaw(k));
/*      */     }
/*  474 */     size += dataSize;
/*  475 */     size += 1 * getOneofsList().size();
/*      */     
/*  477 */     for (int i = 0; i < this.options_.size(); i++) {
/*  478 */       size += 
/*  479 */         CodedOutputStream.computeMessageSize(4, this.options_.get(i));
/*      */     }
/*  481 */     if (this.sourceContext_ != null) {
/*  482 */       size += 
/*  483 */         CodedOutputStream.computeMessageSize(5, getSourceContext());
/*      */     }
/*  485 */     if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
/*  486 */       size += 
/*  487 */         CodedOutputStream.computeEnumSize(6, this.syntax_);
/*      */     }
/*  489 */     size += this.unknownFields.getSerializedSize();
/*  490 */     this.memoizedSize = size;
/*  491 */     return size;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object obj) {
/*  496 */     if (obj == this) {
/*  497 */       return true;
/*      */     }
/*  499 */     if (!(obj instanceof Type)) {
/*  500 */       return super.equals(obj);
/*      */     }
/*  502 */     Type other = (Type)obj;
/*      */ 
/*      */     
/*  505 */     if (!getName().equals(other.getName())) return false;
/*      */     
/*  507 */     if (!getFieldsList().equals(other.getFieldsList())) return false;
/*      */     
/*  509 */     if (!getOneofsList().equals(other.getOneofsList())) return false;
/*      */     
/*  511 */     if (!getOptionsList().equals(other.getOptionsList())) return false; 
/*  512 */     if (hasSourceContext() != other.hasSourceContext()) return false; 
/*  513 */     if (hasSourceContext() && 
/*      */       
/*  515 */       !getSourceContext().equals(other.getSourceContext())) return false;
/*      */     
/*  517 */     if (this.syntax_ != other.syntax_) return false; 
/*  518 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  519 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  524 */     if (this.memoizedHashCode != 0) {
/*  525 */       return this.memoizedHashCode;
/*      */     }
/*  527 */     int hash = 41;
/*  528 */     hash = 19 * hash + getDescriptor().hashCode();
/*  529 */     hash = 37 * hash + 1;
/*  530 */     hash = 53 * hash + getName().hashCode();
/*  531 */     if (getFieldsCount() > 0) {
/*  532 */       hash = 37 * hash + 2;
/*  533 */       hash = 53 * hash + getFieldsList().hashCode();
/*      */     } 
/*  535 */     if (getOneofsCount() > 0) {
/*  536 */       hash = 37 * hash + 3;
/*  537 */       hash = 53 * hash + getOneofsList().hashCode();
/*      */     } 
/*  539 */     if (getOptionsCount() > 0) {
/*  540 */       hash = 37 * hash + 4;
/*  541 */       hash = 53 * hash + getOptionsList().hashCode();
/*      */     } 
/*  543 */     if (hasSourceContext()) {
/*  544 */       hash = 37 * hash + 5;
/*  545 */       hash = 53 * hash + getSourceContext().hashCode();
/*      */     } 
/*  547 */     hash = 37 * hash + 6;
/*  548 */     hash = 53 * hash + this.syntax_;
/*  549 */     hash = 29 * hash + this.unknownFields.hashCode();
/*  550 */     this.memoizedHashCode = hash;
/*  551 */     return hash;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  557 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  563 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */ 
/*      */   
/*      */   public static Type parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  568 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  574 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static Type parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  578 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  584 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static Type parseFrom(InputStream input) throws IOException {
/*  588 */     return 
/*  589 */       GeneratedMessageV3.<Type>parseWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  595 */     return 
/*  596 */       GeneratedMessageV3.<Type>parseWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static Type parseDelimitedFrom(InputStream input) throws IOException {
/*  600 */     return 
/*  601 */       GeneratedMessageV3.<Type>parseDelimitedWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  607 */     return 
/*  608 */       GeneratedMessageV3.<Type>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */ 
/*      */   
/*      */   public static Type parseFrom(CodedInputStream input) throws IOException {
/*  613 */     return 
/*  614 */       GeneratedMessageV3.<Type>parseWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  620 */     return 
/*  621 */       GeneratedMessageV3.<Type>parseWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */   
/*      */   public Builder newBuilderForType() {
/*  625 */     return newBuilder();
/*      */   } public static Builder newBuilder() {
/*  627 */     return DEFAULT_INSTANCE.toBuilder();
/*      */   }
/*      */   public static Builder newBuilder(Type prototype) {
/*  630 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */   }
/*      */   
/*      */   public Builder toBuilder() {
/*  634 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  635 */       .mergeFrom(this);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  641 */     Builder builder = new Builder(parent);
/*  642 */     return builder;
/*      */   }
/*      */   
/*      */   public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements TypeOrBuilder { private int bitField0_;
/*      */     private Object name_;
/*      */     private List<Field> fields_;
/*      */     private RepeatedFieldBuilderV3<Field, Field.Builder, FieldOrBuilder> fieldsBuilder_;
/*      */     private LazyStringList oneofs_;
/*      */     private List<Option> options_;
/*      */     private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
/*      */     private SourceContext sourceContext_;
/*      */     private SingleFieldBuilderV3<SourceContext, SourceContext.Builder, SourceContextOrBuilder> sourceContextBuilder_;
/*      */     private int syntax_;
/*      */     
/*      */     public static final Descriptors.Descriptor getDescriptor() {
/*  657 */       return TypeProto.internal_static_google_protobuf_Type_descriptor;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  663 */       return TypeProto.internal_static_google_protobuf_Type_fieldAccessorTable
/*  664 */         .ensureFieldAccessorsInitialized((Class)Type.class, (Class)Builder.class);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*  919 */       this.name_ = "";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1015 */       this
/* 1016 */         .fields_ = Collections.emptyList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1327 */       this.oneofs_ = LazyStringArrayList.EMPTY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1473 */       this
/* 1474 */         .options_ = Collections.emptyList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1940 */       this.syntax_ = 0; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (GeneratedMessageV3.alwaysUseFieldBuilders) { getFieldsFieldBuilder(); getOptionsFieldBuilder(); }  } public Builder clear() { super.clear(); this.name_ = ""; if (this.fieldsBuilder_ == null) { this.fields_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; } else { this.fieldsBuilder_.clear(); }  this.oneofs_ = LazyStringArrayList.EMPTY; this.bitField0_ &= 0xFFFFFFFD; if (this.optionsBuilder_ == null) { this.options_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFB; } else { this.optionsBuilder_.clear(); }  if (this.sourceContextBuilder_ == null) { this.sourceContext_ = null; } else { this.sourceContext_ = null; this.sourceContextBuilder_ = null; }  this.syntax_ = 0; return this; } public Descriptors.Descriptor getDescriptorForType() { return TypeProto.internal_static_google_protobuf_Type_descriptor; } public Type getDefaultInstanceForType() { return Type.getDefaultInstance(); } public Type build() { Type result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public Type buildPartial() { Type result = new Type(this); int from_bitField0_ = this.bitField0_; result.name_ = this.name_; if (this.fieldsBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0) { this.fields_ = Collections.unmodifiableList(this.fields_); this.bitField0_ &= 0xFFFFFFFE; }  result.fields_ = this.fields_; } else { result.fields_ = this.fieldsBuilder_.build(); }  if ((this.bitField0_ & 0x2) != 0) { this.oneofs_ = this.oneofs_.getUnmodifiableView(); this.bitField0_ &= 0xFFFFFFFD; }  result.oneofs_ = this.oneofs_; if (this.optionsBuilder_ == null) { if ((this.bitField0_ & 0x4) != 0) { this.options_ = Collections.unmodifiableList(this.options_); this.bitField0_ &= 0xFFFFFFFB; }  result.options_ = this.options_; } else { result.options_ = this.optionsBuilder_.build(); }  if (this.sourceContextBuilder_ == null) { result.sourceContext_ = this.sourceContext_; } else { result.sourceContext_ = this.sourceContextBuilder_.build(); }  result.syntax_ = this.syntax_; onBuilt(); return result; } public Builder clone() { return super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof Type) return mergeFrom((Type)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(Type other) { if (other == Type.getDefaultInstance()) return this;  if (!other.getName().isEmpty()) { this.name_ = other.name_; onChanged(); }  if (this.fieldsBuilder_ == null) { if (!other.fields_.isEmpty()) { if (this.fields_.isEmpty()) { this.fields_ = other.fields_; this.bitField0_ &= 0xFFFFFFFE; } else { ensureFieldsIsMutable(); this.fields_.addAll(other.fields_); }  onChanged(); }  } else if (!other.fields_.isEmpty()) { if (this.fieldsBuilder_.isEmpty()) { this.fieldsBuilder_.dispose(); this.fieldsBuilder_ = null; this.fields_ = other.fields_; this.bitField0_ &= 0xFFFFFFFE; this.fieldsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? getFieldsFieldBuilder() : null; } else { this.fieldsBuilder_.addAllMessages(other.fields_); }  }  if (!other.oneofs_.isEmpty()) { if (this.oneofs_.isEmpty()) { this.oneofs_ = other.oneofs_; this.bitField0_ &= 0xFFFFFFFD; } else { ensureOneofsIsMutable(); this.oneofs_.addAll(other.oneofs_); }  onChanged(); }  if (this.optionsBuilder_ == null) { if (!other.options_.isEmpty()) { if (this.options_.isEmpty()) { this.options_ = other.options_; this.bitField0_ &= 0xFFFFFFFB; } else { ensureOptionsIsMutable(); this.options_.addAll(other.options_); }  onChanged(); }  } else if (!other.options_.isEmpty()) { if (this.optionsBuilder_.isEmpty()) { this.optionsBuilder_.dispose(); this.optionsBuilder_ = null; this.options_ = other.options_; this.bitField0_ &= 0xFFFFFFFB; this.optionsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? getOptionsFieldBuilder() : null; } else { this.optionsBuilder_.addAllMessages(other.options_); }  }  if (other.hasSourceContext()) mergeSourceContext(other.getSourceContext());  if (other.syntax_ != 0) setSyntaxValue(other.getSyntaxValue());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { Type parsedMessage = null; try { parsedMessage = Type.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (Type)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public String getName() { Object ref = this.name_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.name_ = s; return s; }  return (String)ref; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; } public Builder setName(String value) { if (value == null) throw new NullPointerException();  this.name_ = value; onChanged(); return this; } public Builder clearName() { this.name_ = Type.getDefaultInstance().getName(); onChanged(); return this; } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.name_ = ""; this.fields_ = Collections.emptyList(); this.oneofs_ = LazyStringArrayList.EMPTY; this.options_ = Collections.emptyList(); this.syntax_ = 0; maybeForceBuilderInitialization(); }
/*      */     public Builder setNameBytes(ByteString value) { if (value == null) throw new NullPointerException();  AbstractMessageLite.checkByteStringIsUtf8(value); this.name_ = value; onChanged(); return this; }
/*      */     private void ensureFieldsIsMutable() { if ((this.bitField0_ & 0x1) == 0) { this.fields_ = new ArrayList<>(this.fields_); this.bitField0_ |= 0x1; }  }
/*      */     public List<Field> getFieldsList() { if (this.fieldsBuilder_ == null) return Collections.unmodifiableList(this.fields_);  return this.fieldsBuilder_.getMessageList(); }
/*      */     public int getFieldsCount() { if (this.fieldsBuilder_ == null) return this.fields_.size();  return this.fieldsBuilder_.getCount(); }
/*      */     public Field getFields(int index) { if (this.fieldsBuilder_ == null) return this.fields_.get(index);  return this.fieldsBuilder_.getMessage(index); }
/*      */     public Builder setFields(int index, Field value) { if (this.fieldsBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureFieldsIsMutable(); this.fields_.set(index, value); onChanged(); } else { this.fieldsBuilder_.setMessage(index, value); }  return this; }
/*      */     public Builder setFields(int index, Field.Builder builderForValue) { if (this.fieldsBuilder_ == null) { ensureFieldsIsMutable(); this.fields_.set(index, builderForValue.build()); onChanged(); } else { this.fieldsBuilder_.setMessage(index, builderForValue.build()); }  return this; }
/*      */     public Builder addFields(Field value) { if (this.fieldsBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureFieldsIsMutable(); this.fields_.add(value); onChanged(); } else { this.fieldsBuilder_.addMessage(value); }  return this; }
/*      */     public Builder addFields(int index, Field value) { if (this.fieldsBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureFieldsIsMutable(); this.fields_.add(index, value); onChanged(); } else { this.fieldsBuilder_.addMessage(index, value); }  return this; }
/* 1950 */     public Builder addFields(Field.Builder builderForValue) { if (this.fieldsBuilder_ == null) { ensureFieldsIsMutable(); this.fields_.add(builderForValue.build()); onChanged(); } else { this.fieldsBuilder_.addMessage(builderForValue.build()); }  return this; } public Builder addFields(int index, Field.Builder builderForValue) { if (this.fieldsBuilder_ == null) { ensureFieldsIsMutable(); this.fields_.add(index, builderForValue.build()); onChanged(); } else { this.fieldsBuilder_.addMessage(index, builderForValue.build()); }  return this; } public Builder addAllFields(Iterable<? extends Field> values) { if (this.fieldsBuilder_ == null) { ensureFieldsIsMutable(); AbstractMessageLite.Builder.addAll(values, this.fields_); onChanged(); } else { this.fieldsBuilder_.addAllMessages(values); }  return this; } public Builder clearFields() { if (this.fieldsBuilder_ == null) { this.fields_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; onChanged(); } else { this.fieldsBuilder_.clear(); }  return this; } public Builder removeFields(int index) { if (this.fieldsBuilder_ == null) { ensureFieldsIsMutable(); this.fields_.remove(index); onChanged(); } else { this.fieldsBuilder_.remove(index); }  return this; } public Field.Builder getFieldsBuilder(int index) { return getFieldsFieldBuilder().getBuilder(index); } public FieldOrBuilder getFieldsOrBuilder(int index) { if (this.fieldsBuilder_ == null) return this.fields_.get(index);  return this.fieldsBuilder_.getMessageOrBuilder(index); } public List<? extends FieldOrBuilder> getFieldsOrBuilderList() { if (this.fieldsBuilder_ != null) return this.fieldsBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.fields_); } public Field.Builder addFieldsBuilder() { return getFieldsFieldBuilder().addBuilder(Field.getDefaultInstance()); } public Field.Builder addFieldsBuilder(int index) { return getFieldsFieldBuilder().addBuilder(index, Field.getDefaultInstance()); } public List<Field.Builder> getFieldsBuilderList() { return getFieldsFieldBuilder().getBuilderList(); } public int getSyntaxValue() { return this.syntax_; } private RepeatedFieldBuilderV3<Field, Field.Builder, FieldOrBuilder> getFieldsFieldBuilder() { if (this.fieldsBuilder_ == null) { this.fieldsBuilder_ = new RepeatedFieldBuilderV3<>(this.fields_, ((this.bitField0_ & 0x1) != 0), getParentForChildren(), isClean()); this.fields_ = null; }  return this.fieldsBuilder_; } private void ensureOneofsIsMutable() { if ((this.bitField0_ & 0x2) == 0) { this.oneofs_ = new LazyStringArrayList(this.oneofs_); this.bitField0_ |= 0x2; }  } public ProtocolStringList getOneofsList() { return this.oneofs_.getUnmodifiableView(); } public int getOneofsCount() { return this.oneofs_.size(); } public String getOneofs(int index) { return this.oneofs_.get(index); } public ByteString getOneofsBytes(int index) { return this.oneofs_.getByteString(index); } public Builder setOneofs(int index, String value) { if (value == null) throw new NullPointerException();  ensureOneofsIsMutable(); this.oneofs_.set(index, value); onChanged(); return this; } public Builder addOneofs(String value) { if (value == null) throw new NullPointerException();  ensureOneofsIsMutable(); this.oneofs_.add(value); onChanged(); return this; } public Builder addAllOneofs(Iterable<String> values) { ensureOneofsIsMutable(); AbstractMessageLite.Builder.addAll(values, this.oneofs_); onChanged(); return this; } public Builder clearOneofs() { this.oneofs_ = LazyStringArrayList.EMPTY; this.bitField0_ &= 0xFFFFFFFD; onChanged(); return this; } public Builder addOneofsBytes(ByteString value) { if (value == null) throw new NullPointerException();  AbstractMessageLite.checkByteStringIsUtf8(value); ensureOneofsIsMutable(); this.oneofs_.add(value); onChanged(); return this; } private void ensureOptionsIsMutable() { if ((this.bitField0_ & 0x4) == 0) { this.options_ = new ArrayList<>(this.options_); this.bitField0_ |= 0x4; }  } public List<Option> getOptionsList() { if (this.optionsBuilder_ == null) return Collections.unmodifiableList(this.options_);  return this.optionsBuilder_.getMessageList(); } public int getOptionsCount() { if (this.optionsBuilder_ == null) return this.options_.size();  return this.optionsBuilder_.getCount(); } public Option getOptions(int index) { if (this.optionsBuilder_ == null) return this.options_.get(index);  return this.optionsBuilder_.getMessage(index); } public Builder setOptions(int index, Option value) { if (this.optionsBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOptionsIsMutable(); this.options_.set(index, value); onChanged(); } else { this.optionsBuilder_.setMessage(index, value); }  return this; } public Builder setOptions(int index, Option.Builder builderForValue) { if (this.optionsBuilder_ == null) { ensureOptionsIsMutable(); this.options_.set(index, builderForValue.build()); onChanged(); } else { this.optionsBuilder_.setMessage(index, builderForValue.build()); }  return this; } public Builder addOptions(Option value) { if (this.optionsBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOptionsIsMutable(); this.options_.add(value); onChanged(); } else { this.optionsBuilder_.addMessage(value); }  return this; } public Builder addOptions(int index, Option value) { if (this.optionsBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOptionsIsMutable(); this.options_.add(index, value); onChanged(); } else { this.optionsBuilder_.addMessage(index, value); }  return this; } public Builder addOptions(Option.Builder builderForValue) { if (this.optionsBuilder_ == null) { ensureOptionsIsMutable(); this.options_.add(builderForValue.build()); onChanged(); } else { this.optionsBuilder_.addMessage(builderForValue.build()); }  return this; } public Builder addOptions(int index, Option.Builder builderForValue) { if (this.optionsBuilder_ == null) { ensureOptionsIsMutable(); this.options_.add(index, builderForValue.build()); onChanged(); } else { this.optionsBuilder_.addMessage(index, builderForValue.build()); }  return this; } public Builder addAllOptions(Iterable<? extends Option> values) { if (this.optionsBuilder_ == null) { ensureOptionsIsMutable(); AbstractMessageLite.Builder.addAll(values, this.options_); onChanged(); } else { this.optionsBuilder_.addAllMessages(values); }  return this; } public Builder clearOptions() { if (this.optionsBuilder_ == null) { this.options_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFB; onChanged(); } else { this.optionsBuilder_.clear(); }  return this; } public Builder removeOptions(int index) { if (this.optionsBuilder_ == null) { ensureOptionsIsMutable(); this.options_.remove(index); onChanged(); } else { this.optionsBuilder_.remove(index); }  return this; } public Option.Builder getOptionsBuilder(int index) { return getOptionsFieldBuilder().getBuilder(index); } public OptionOrBuilder getOptionsOrBuilder(int index) { if (this.optionsBuilder_ == null) return this.options_.get(index);  return this.optionsBuilder_.getMessageOrBuilder(index); } public List<? extends OptionOrBuilder> getOptionsOrBuilderList() { if (this.optionsBuilder_ != null) return this.optionsBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.options_); } public Option.Builder addOptionsBuilder() { return getOptionsFieldBuilder().addBuilder(Option.getDefaultInstance()); } public Option.Builder addOptionsBuilder(int index) { return getOptionsFieldBuilder().addBuilder(index, Option.getDefaultInstance()); }
/*      */     public List<Option.Builder> getOptionsBuilderList() { return getOptionsFieldBuilder().getBuilderList(); }
/*      */     private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> getOptionsFieldBuilder() { if (this.optionsBuilder_ == null) { this.optionsBuilder_ = new RepeatedFieldBuilderV3<>(this.options_, ((this.bitField0_ & 0x4) != 0), getParentForChildren(), isClean()); this.options_ = null; }  return this.optionsBuilder_; }
/*      */     public boolean hasSourceContext() { return (this.sourceContextBuilder_ != null || this.sourceContext_ != null); }
/*      */     public SourceContext getSourceContext() { if (this.sourceContextBuilder_ == null) return (this.sourceContext_ == null) ? SourceContext.getDefaultInstance() : this.sourceContext_;  return this.sourceContextBuilder_.getMessage(); }
/*      */     public Builder setSourceContext(SourceContext value) { if (this.sourceContextBuilder_ == null) { if (value == null) throw new NullPointerException();  this.sourceContext_ = value; onChanged(); } else { this.sourceContextBuilder_.setMessage(value); }  return this; }
/*      */     public Builder setSourceContext(SourceContext.Builder builderForValue) { if (this.sourceContextBuilder_ == null) { this.sourceContext_ = builderForValue.build(); onChanged(); } else { this.sourceContextBuilder_.setMessage(builderForValue.build()); }  return this; }
/*      */     public Builder mergeSourceContext(SourceContext value) { if (this.sourceContextBuilder_ == null) { if (this.sourceContext_ != null) { this.sourceContext_ = SourceContext.newBuilder(this.sourceContext_).mergeFrom(value).buildPartial(); } else { this.sourceContext_ = value; }  onChanged(); } else { this.sourceContextBuilder_.mergeFrom(value); }  return this; }
/*      */     public Builder clearSourceContext() { if (this.sourceContextBuilder_ == null) { this.sourceContext_ = null; onChanged(); } else { this.sourceContext_ = null; this.sourceContextBuilder_ = null; }  return this; }
/*      */     public SourceContext.Builder getSourceContextBuilder() { onChanged(); return getSourceContextFieldBuilder().getBuilder(); }
/*      */     public SourceContextOrBuilder getSourceContextOrBuilder() { if (this.sourceContextBuilder_ != null) return this.sourceContextBuilder_.getMessageOrBuilder();  return (this.sourceContext_ == null) ? SourceContext.getDefaultInstance() : this.sourceContext_; }
/*      */     private SingleFieldBuilderV3<SourceContext, SourceContext.Builder, SourceContextOrBuilder> getSourceContextFieldBuilder() { if (this.sourceContextBuilder_ == null) { this.sourceContextBuilder_ = new SingleFieldBuilderV3<>(getSourceContext(), getParentForChildren(), isClean()); this.sourceContext_ = null; }  return this.sourceContextBuilder_; }
/* 1962 */     public Builder setSyntaxValue(int value) { this.syntax_ = value;
/* 1963 */       onChanged();
/* 1964 */       return this; }
/*      */ 
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
/* 1976 */       Syntax result = Syntax.valueOf(this.syntax_);
/* 1977 */       return (result == null) ? Syntax.UNRECOGNIZED : result;
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
/* 1989 */       if (value == null) {
/* 1990 */         throw new NullPointerException();
/*      */       }
/*      */       
/* 1993 */       this.syntax_ = value.getNumber();
/* 1994 */       onChanged();
/* 1995 */       return this;
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
/* 2007 */       this.syntax_ = 0;
/* 2008 */       onChanged();
/* 2009 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 2014 */       return super.setUnknownFields(unknownFields);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 2020 */       return super.mergeUnknownFields(unknownFields);
/*      */     } }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2030 */   private static final Type DEFAULT_INSTANCE = new Type();
/*      */ 
/*      */   
/*      */   public static Type getDefaultInstance() {
/* 2034 */     return DEFAULT_INSTANCE;
/*      */   }
/*      */ 
/*      */   
/* 2038 */   private static final Parser<Type> PARSER = new AbstractParser<Type>()
/*      */     {
/*      */ 
/*      */       
/*      */       public Type parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */       {
/* 2044 */         return new Type(input, extensionRegistry);
/*      */       }
/*      */     };
/*      */   
/*      */   public static Parser<Type> parser() {
/* 2049 */     return PARSER;
/*      */   }
/*      */ 
/*      */   
/*      */   public Parser<Type> getParserForType() {
/* 2054 */     return PARSER;
/*      */   }
/*      */ 
/*      */   
/*      */   public Type getDefaultInstanceForType() {
/* 2059 */     return DEFAULT_INSTANCE;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Type.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */