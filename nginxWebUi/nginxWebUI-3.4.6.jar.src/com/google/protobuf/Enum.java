/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ 
/*      */ public final class Enum extends GeneratedMessageV3 implements EnumOrBuilder {
/*      */   private static final long serialVersionUID = 0L;
/*      */   public static final int NAME_FIELD_NUMBER = 1;
/*      */   private volatile Object name_;
/*      */   public static final int ENUMVALUE_FIELD_NUMBER = 2;
/*      */   private List<EnumValue> enumvalue_;
/*      */   public static final int OPTIONS_FIELD_NUMBER = 3;
/*      */   private List<Option> options_;
/*      */   public static final int SOURCE_CONTEXT_FIELD_NUMBER = 4;
/*      */   private SourceContext sourceContext_;
/*      */   public static final int SYNTAX_FIELD_NUMBER = 5;
/*      */   private int syntax_;
/*      */   private byte memoizedIsInitialized;
/*      */   
/*   20 */   private Enum(GeneratedMessageV3.Builder<?> builder) { super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  357 */     this.memoizedIsInitialized = -1; } private Enum() { this.memoizedIsInitialized = -1; this.name_ = ""; this.enumvalue_ = Collections.emptyList(); this.options_ = Collections.emptyList(); this.syntax_ = 0; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Enum(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Enum(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { String s; SourceContext.Builder subBuilder; int rawValue, tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: s = input.readStringRequireUtf8(); this.name_ = s; continue;case 18: if ((mutable_bitField0_ & 0x1) == 0) { this.enumvalue_ = new ArrayList<>(); mutable_bitField0_ |= 0x1; }  this.enumvalue_.add(input.readMessage(EnumValue.parser(), extensionRegistry)); continue;case 26: if ((mutable_bitField0_ & 0x2) == 0) { this.options_ = new ArrayList<>(); mutable_bitField0_ |= 0x2; }  this.options_.add(input.readMessage(Option.parser(), extensionRegistry)); continue;case 34: subBuilder = null; if (this.sourceContext_ != null) subBuilder = this.sourceContext_.toBuilder();  this.sourceContext_ = input.<SourceContext>readMessage(SourceContext.parser(), extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.sourceContext_); this.sourceContext_ = subBuilder.buildPartial(); }  continue;case 40: rawValue = input.readEnum(); this.syntax_ = rawValue; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x1) != 0) this.enumvalue_ = Collections.unmodifiableList(this.enumvalue_);  if ((mutable_bitField0_ & 0x2) != 0) this.options_ = Collections.unmodifiableList(this.options_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return TypeProto.internal_static_google_protobuf_Enum_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return TypeProto.internal_static_google_protobuf_Enum_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Enum.class, (Class)Builder.class); } public String getName() { Object ref = this.name_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.name_ = s; return s; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; } public List<EnumValue> getEnumvalueList() { return this.enumvalue_; } public List<? extends EnumValueOrBuilder> getEnumvalueOrBuilderList() { return (List)this.enumvalue_; } public int getEnumvalueCount() { return this.enumvalue_.size(); } public EnumValue getEnumvalue(int index) { return this.enumvalue_.get(index); } public EnumValueOrBuilder getEnumvalueOrBuilder(int index) { return this.enumvalue_.get(index); } public List<Option> getOptionsList() { return this.options_; } public List<? extends OptionOrBuilder> getOptionsOrBuilderList() { return (List)this.options_; } public int getOptionsCount() { return this.options_.size(); } public Option getOptions(int index) { return this.options_.get(index); } public OptionOrBuilder getOptionsOrBuilder(int index) { return this.options_.get(index); } public boolean hasSourceContext() { return (this.sourceContext_ != null); } public SourceContext getSourceContext() { return (this.sourceContext_ == null) ? SourceContext.getDefaultInstance() : this.sourceContext_; } public SourceContextOrBuilder getSourceContextOrBuilder() { return getSourceContext(); }
/*      */   public int getSyntaxValue() { return this.syntax_; }
/*      */   public Syntax getSyntax() { Syntax result = Syntax.valueOf(this.syntax_); return (result == null) ? Syntax.UNRECOGNIZED : result; }
/*  360 */   public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  361 */     if (isInitialized == 1) return true; 
/*  362 */     if (isInitialized == 0) return false;
/*      */     
/*  364 */     this.memoizedIsInitialized = 1;
/*  365 */     return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeTo(CodedOutputStream output) throws IOException {
/*  371 */     if (!getNameBytes().isEmpty())
/*  372 */       GeneratedMessageV3.writeString(output, 1, this.name_); 
/*      */     int i;
/*  374 */     for (i = 0; i < this.enumvalue_.size(); i++) {
/*  375 */       output.writeMessage(2, this.enumvalue_.get(i));
/*      */     }
/*  377 */     for (i = 0; i < this.options_.size(); i++) {
/*  378 */       output.writeMessage(3, this.options_.get(i));
/*      */     }
/*  380 */     if (this.sourceContext_ != null) {
/*  381 */       output.writeMessage(4, getSourceContext());
/*      */     }
/*  383 */     if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
/*  384 */       output.writeEnum(5, this.syntax_);
/*      */     }
/*  386 */     this.unknownFields.writeTo(output);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getSerializedSize() {
/*  391 */     int size = this.memoizedSize;
/*  392 */     if (size != -1) return size;
/*      */     
/*  394 */     size = 0;
/*  395 */     if (!getNameBytes().isEmpty())
/*  396 */       size += GeneratedMessageV3.computeStringSize(1, this.name_); 
/*      */     int i;
/*  398 */     for (i = 0; i < this.enumvalue_.size(); i++) {
/*  399 */       size += 
/*  400 */         CodedOutputStream.computeMessageSize(2, this.enumvalue_.get(i));
/*      */     }
/*  402 */     for (i = 0; i < this.options_.size(); i++) {
/*  403 */       size += 
/*  404 */         CodedOutputStream.computeMessageSize(3, this.options_.get(i));
/*      */     }
/*  406 */     if (this.sourceContext_ != null) {
/*  407 */       size += 
/*  408 */         CodedOutputStream.computeMessageSize(4, getSourceContext());
/*      */     }
/*  410 */     if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
/*  411 */       size += 
/*  412 */         CodedOutputStream.computeEnumSize(5, this.syntax_);
/*      */     }
/*  414 */     size += this.unknownFields.getSerializedSize();
/*  415 */     this.memoizedSize = size;
/*  416 */     return size;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object obj) {
/*  421 */     if (obj == this) {
/*  422 */       return true;
/*      */     }
/*  424 */     if (!(obj instanceof Enum)) {
/*  425 */       return super.equals(obj);
/*      */     }
/*  427 */     Enum other = (Enum)obj;
/*      */ 
/*      */     
/*  430 */     if (!getName().equals(other.getName())) return false;
/*      */     
/*  432 */     if (!getEnumvalueList().equals(other.getEnumvalueList())) return false;
/*      */     
/*  434 */     if (!getOptionsList().equals(other.getOptionsList())) return false; 
/*  435 */     if (hasSourceContext() != other.hasSourceContext()) return false; 
/*  436 */     if (hasSourceContext() && 
/*      */       
/*  438 */       !getSourceContext().equals(other.getSourceContext())) return false;
/*      */     
/*  440 */     if (this.syntax_ != other.syntax_) return false; 
/*  441 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  442 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  447 */     if (this.memoizedHashCode != 0) {
/*  448 */       return this.memoizedHashCode;
/*      */     }
/*  450 */     int hash = 41;
/*  451 */     hash = 19 * hash + getDescriptor().hashCode();
/*  452 */     hash = 37 * hash + 1;
/*  453 */     hash = 53 * hash + getName().hashCode();
/*  454 */     if (getEnumvalueCount() > 0) {
/*  455 */       hash = 37 * hash + 2;
/*  456 */       hash = 53 * hash + getEnumvalueList().hashCode();
/*      */     } 
/*  458 */     if (getOptionsCount() > 0) {
/*  459 */       hash = 37 * hash + 3;
/*  460 */       hash = 53 * hash + getOptionsList().hashCode();
/*      */     } 
/*  462 */     if (hasSourceContext()) {
/*  463 */       hash = 37 * hash + 4;
/*  464 */       hash = 53 * hash + getSourceContext().hashCode();
/*      */     } 
/*  466 */     hash = 37 * hash + 5;
/*  467 */     hash = 53 * hash + this.syntax_;
/*  468 */     hash = 29 * hash + this.unknownFields.hashCode();
/*  469 */     this.memoizedHashCode = hash;
/*  470 */     return hash;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Enum parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  476 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Enum parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  482 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */ 
/*      */   
/*      */   public static Enum parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  487 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Enum parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  493 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static Enum parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  497 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Enum parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  503 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static Enum parseFrom(InputStream input) throws IOException {
/*  507 */     return 
/*  508 */       GeneratedMessageV3.<Enum>parseWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Enum parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  514 */     return 
/*  515 */       GeneratedMessageV3.<Enum>parseWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static Enum parseDelimitedFrom(InputStream input) throws IOException {
/*  519 */     return 
/*  520 */       GeneratedMessageV3.<Enum>parseDelimitedWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Enum parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  526 */     return 
/*  527 */       GeneratedMessageV3.<Enum>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */ 
/*      */   
/*      */   public static Enum parseFrom(CodedInputStream input) throws IOException {
/*  532 */     return 
/*  533 */       GeneratedMessageV3.<Enum>parseWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Enum parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  539 */     return 
/*  540 */       GeneratedMessageV3.<Enum>parseWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */   
/*      */   public Builder newBuilderForType() {
/*  544 */     return newBuilder();
/*      */   } public static Builder newBuilder() {
/*  546 */     return DEFAULT_INSTANCE.toBuilder();
/*      */   }
/*      */   public static Builder newBuilder(Enum prototype) {
/*  549 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */   }
/*      */   
/*      */   public Builder toBuilder() {
/*  553 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  554 */       .mergeFrom(this);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  560 */     Builder builder = new Builder(parent);
/*  561 */     return builder;
/*      */   }
/*      */   
/*      */   public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements EnumOrBuilder {
/*      */     private int bitField0_;
/*      */     private Object name_;
/*      */     private List<EnumValue> enumvalue_;
/*      */     private RepeatedFieldBuilderV3<EnumValue, EnumValue.Builder, EnumValueOrBuilder> enumvalueBuilder_;
/*      */     private List<Option> options_;
/*      */     private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
/*      */     private SourceContext sourceContext_;
/*      */     private SingleFieldBuilderV3<SourceContext, SourceContext.Builder, SourceContextOrBuilder> sourceContextBuilder_;
/*      */     private int syntax_;
/*      */     
/*      */     public static final Descriptors.Descriptor getDescriptor() {
/*  576 */       return TypeProto.internal_static_google_protobuf_Enum_descriptor;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  582 */       return TypeProto.internal_static_google_protobuf_Enum_fieldAccessorTable
/*  583 */         .ensureFieldAccessorsInitialized((Class)Enum.class, (Class)Builder.class);
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
/*      */     private Builder()
/*      */     {
/*  821 */       this.name_ = "";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  917 */       this
/*  918 */         .enumvalue_ = Collections.emptyList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1229 */       this
/* 1230 */         .options_ = Collections.emptyList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1696 */       this.syntax_ = 0; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (GeneratedMessageV3.alwaysUseFieldBuilders) { getEnumvalueFieldBuilder(); getOptionsFieldBuilder(); }  } public Builder clear() { super.clear(); this.name_ = ""; if (this.enumvalueBuilder_ == null) { this.enumvalue_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; } else { this.enumvalueBuilder_.clear(); }  if (this.optionsBuilder_ == null) { this.options_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFD; } else { this.optionsBuilder_.clear(); }  if (this.sourceContextBuilder_ == null) { this.sourceContext_ = null; } else { this.sourceContext_ = null; this.sourceContextBuilder_ = null; }  this.syntax_ = 0; return this; } public Descriptors.Descriptor getDescriptorForType() { return TypeProto.internal_static_google_protobuf_Enum_descriptor; } public Enum getDefaultInstanceForType() { return Enum.getDefaultInstance(); } public Enum build() { Enum result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public Enum buildPartial() { Enum result = new Enum(this); int from_bitField0_ = this.bitField0_; result.name_ = this.name_; if (this.enumvalueBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0) { this.enumvalue_ = Collections.unmodifiableList(this.enumvalue_); this.bitField0_ &= 0xFFFFFFFE; }  result.enumvalue_ = this.enumvalue_; } else { result.enumvalue_ = this.enumvalueBuilder_.build(); }  if (this.optionsBuilder_ == null) { if ((this.bitField0_ & 0x2) != 0) { this.options_ = Collections.unmodifiableList(this.options_); this.bitField0_ &= 0xFFFFFFFD; }  result.options_ = this.options_; } else { result.options_ = this.optionsBuilder_.build(); }  if (this.sourceContextBuilder_ == null) { result.sourceContext_ = this.sourceContext_; } else { result.sourceContext_ = this.sourceContextBuilder_.build(); }  result.syntax_ = this.syntax_; onBuilt(); return result; } public Builder clone() { return super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof Enum) return mergeFrom((Enum)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(Enum other) { if (other == Enum.getDefaultInstance()) return this;  if (!other.getName().isEmpty()) { this.name_ = other.name_; onChanged(); }  if (this.enumvalueBuilder_ == null) { if (!other.enumvalue_.isEmpty()) { if (this.enumvalue_.isEmpty()) { this.enumvalue_ = other.enumvalue_; this.bitField0_ &= 0xFFFFFFFE; } else { ensureEnumvalueIsMutable(); this.enumvalue_.addAll(other.enumvalue_); }  onChanged(); }  } else if (!other.enumvalue_.isEmpty()) { if (this.enumvalueBuilder_.isEmpty()) { this.enumvalueBuilder_.dispose(); this.enumvalueBuilder_ = null; this.enumvalue_ = other.enumvalue_; this.bitField0_ &= 0xFFFFFFFE; this.enumvalueBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? getEnumvalueFieldBuilder() : null; } else { this.enumvalueBuilder_.addAllMessages(other.enumvalue_); }  }  if (this.optionsBuilder_ == null) { if (!other.options_.isEmpty()) { if (this.options_.isEmpty()) { this.options_ = other.options_; this.bitField0_ &= 0xFFFFFFFD; } else { ensureOptionsIsMutable(); this.options_.addAll(other.options_); }  onChanged(); }  } else if (!other.options_.isEmpty()) { if (this.optionsBuilder_.isEmpty()) { this.optionsBuilder_.dispose(); this.optionsBuilder_ = null; this.options_ = other.options_; this.bitField0_ &= 0xFFFFFFFD; this.optionsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? getOptionsFieldBuilder() : null; } else { this.optionsBuilder_.addAllMessages(other.options_); }  }  if (other.hasSourceContext()) mergeSourceContext(other.getSourceContext());  if (other.syntax_ != 0) setSyntaxValue(other.getSyntaxValue());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { Enum parsedMessage = null; try { parsedMessage = Enum.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (Enum)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public String getName() { Object ref = this.name_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); this.name_ = s; return s; }  return (String)ref; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.name_ = ""; this.enumvalue_ = Collections.emptyList(); this.options_ = Collections.emptyList(); this.syntax_ = 0; maybeForceBuilderInitialization(); }
/*      */     public Builder setName(String value) { if (value == null) throw new NullPointerException();  this.name_ = value; onChanged(); return this; }
/*      */     public Builder clearName() { this.name_ = Enum.getDefaultInstance().getName(); onChanged(); return this; }
/*      */     public Builder setNameBytes(ByteString value) { if (value == null) throw new NullPointerException();  AbstractMessageLite.checkByteStringIsUtf8(value); this.name_ = value; onChanged(); return this; }
/*      */     private void ensureEnumvalueIsMutable() { if ((this.bitField0_ & 0x1) == 0) { this.enumvalue_ = new ArrayList<>(this.enumvalue_); this.bitField0_ |= 0x1; }  }
/*      */     public List<EnumValue> getEnumvalueList() { if (this.enumvalueBuilder_ == null) return Collections.unmodifiableList(this.enumvalue_);  return this.enumvalueBuilder_.getMessageList(); }
/*      */     public int getEnumvalueCount() { if (this.enumvalueBuilder_ == null) return this.enumvalue_.size();  return this.enumvalueBuilder_.getCount(); }
/*      */     public EnumValue getEnumvalue(int index) { if (this.enumvalueBuilder_ == null) return this.enumvalue_.get(index);  return this.enumvalueBuilder_.getMessage(index); }
/*      */     public Builder setEnumvalue(int index, EnumValue value) { if (this.enumvalueBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureEnumvalueIsMutable(); this.enumvalue_.set(index, value); onChanged(); } else { this.enumvalueBuilder_.setMessage(index, value); }  return this; }
/*      */     public Builder setEnumvalue(int index, EnumValue.Builder builderForValue) { if (this.enumvalueBuilder_ == null) { ensureEnumvalueIsMutable(); this.enumvalue_.set(index, builderForValue.build()); onChanged(); } else { this.enumvalueBuilder_.setMessage(index, builderForValue.build()); }  return this; }
/* 1706 */     public Builder addEnumvalue(EnumValue value) { if (this.enumvalueBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureEnumvalueIsMutable(); this.enumvalue_.add(value); onChanged(); } else { this.enumvalueBuilder_.addMessage(value); }  return this; } public Builder addEnumvalue(int index, EnumValue value) { if (this.enumvalueBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureEnumvalueIsMutable(); this.enumvalue_.add(index, value); onChanged(); } else { this.enumvalueBuilder_.addMessage(index, value); }  return this; } public Builder addEnumvalue(EnumValue.Builder builderForValue) { if (this.enumvalueBuilder_ == null) { ensureEnumvalueIsMutable(); this.enumvalue_.add(builderForValue.build()); onChanged(); } else { this.enumvalueBuilder_.addMessage(builderForValue.build()); }  return this; } public Builder addEnumvalue(int index, EnumValue.Builder builderForValue) { if (this.enumvalueBuilder_ == null) { ensureEnumvalueIsMutable(); this.enumvalue_.add(index, builderForValue.build()); onChanged(); } else { this.enumvalueBuilder_.addMessage(index, builderForValue.build()); }  return this; } public Builder addAllEnumvalue(Iterable<? extends EnumValue> values) { if (this.enumvalueBuilder_ == null) { ensureEnumvalueIsMutable(); AbstractMessageLite.Builder.addAll(values, this.enumvalue_); onChanged(); } else { this.enumvalueBuilder_.addAllMessages(values); }  return this; } public Builder clearEnumvalue() { if (this.enumvalueBuilder_ == null) { this.enumvalue_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; onChanged(); } else { this.enumvalueBuilder_.clear(); }  return this; } public Builder removeEnumvalue(int index) { if (this.enumvalueBuilder_ == null) { ensureEnumvalueIsMutable(); this.enumvalue_.remove(index); onChanged(); } else { this.enumvalueBuilder_.remove(index); }  return this; } public EnumValue.Builder getEnumvalueBuilder(int index) { return getEnumvalueFieldBuilder().getBuilder(index); } public int getSyntaxValue() { return this.syntax_; } public EnumValueOrBuilder getEnumvalueOrBuilder(int index) { if (this.enumvalueBuilder_ == null) return this.enumvalue_.get(index);  return this.enumvalueBuilder_.getMessageOrBuilder(index); } public List<? extends EnumValueOrBuilder> getEnumvalueOrBuilderList() { if (this.enumvalueBuilder_ != null) return this.enumvalueBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.enumvalue_); } public EnumValue.Builder addEnumvalueBuilder() { return getEnumvalueFieldBuilder().addBuilder(EnumValue.getDefaultInstance()); } public EnumValue.Builder addEnumvalueBuilder(int index) { return getEnumvalueFieldBuilder().addBuilder(index, EnumValue.getDefaultInstance()); } public List<EnumValue.Builder> getEnumvalueBuilderList() { return getEnumvalueFieldBuilder().getBuilderList(); } private RepeatedFieldBuilderV3<EnumValue, EnumValue.Builder, EnumValueOrBuilder> getEnumvalueFieldBuilder() { if (this.enumvalueBuilder_ == null) { this.enumvalueBuilder_ = new RepeatedFieldBuilderV3<>(this.enumvalue_, ((this.bitField0_ & 0x1) != 0), getParentForChildren(), isClean()); this.enumvalue_ = null; }  return this.enumvalueBuilder_; } private void ensureOptionsIsMutable() { if ((this.bitField0_ & 0x2) == 0) { this.options_ = new ArrayList<>(this.options_); this.bitField0_ |= 0x2; }  } public List<Option> getOptionsList() { if (this.optionsBuilder_ == null) return Collections.unmodifiableList(this.options_);  return this.optionsBuilder_.getMessageList(); } public int getOptionsCount() { if (this.optionsBuilder_ == null) return this.options_.size();  return this.optionsBuilder_.getCount(); } public Option getOptions(int index) { if (this.optionsBuilder_ == null) return this.options_.get(index);  return this.optionsBuilder_.getMessage(index); } public Builder setOptions(int index, Option value) { if (this.optionsBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOptionsIsMutable(); this.options_.set(index, value); onChanged(); } else { this.optionsBuilder_.setMessage(index, value); }  return this; } public Builder setOptions(int index, Option.Builder builderForValue) { if (this.optionsBuilder_ == null) { ensureOptionsIsMutable(); this.options_.set(index, builderForValue.build()); onChanged(); } else { this.optionsBuilder_.setMessage(index, builderForValue.build()); }  return this; } public Builder addOptions(Option value) { if (this.optionsBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOptionsIsMutable(); this.options_.add(value); onChanged(); } else { this.optionsBuilder_.addMessage(value); }  return this; } public Builder addOptions(int index, Option value) { if (this.optionsBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOptionsIsMutable(); this.options_.add(index, value); onChanged(); } else { this.optionsBuilder_.addMessage(index, value); }  return this; } public Builder addOptions(Option.Builder builderForValue) { if (this.optionsBuilder_ == null) { ensureOptionsIsMutable(); this.options_.add(builderForValue.build()); onChanged(); } else { this.optionsBuilder_.addMessage(builderForValue.build()); }  return this; } public Builder addOptions(int index, Option.Builder builderForValue) { if (this.optionsBuilder_ == null) { ensureOptionsIsMutable(); this.options_.add(index, builderForValue.build()); onChanged(); } else { this.optionsBuilder_.addMessage(index, builderForValue.build()); }  return this; } public Builder addAllOptions(Iterable<? extends Option> values) { if (this.optionsBuilder_ == null) { ensureOptionsIsMutable(); AbstractMessageLite.Builder.addAll(values, this.options_); onChanged(); } else { this.optionsBuilder_.addAllMessages(values); }  return this; } public Builder clearOptions() { if (this.optionsBuilder_ == null) { this.options_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFD; onChanged(); } else { this.optionsBuilder_.clear(); }  return this; } public Builder removeOptions(int index) { if (this.optionsBuilder_ == null) { ensureOptionsIsMutable(); this.options_.remove(index); onChanged(); } else { this.optionsBuilder_.remove(index); }  return this; } public Option.Builder getOptionsBuilder(int index) { return getOptionsFieldBuilder().getBuilder(index); } public OptionOrBuilder getOptionsOrBuilder(int index) { if (this.optionsBuilder_ == null) return this.options_.get(index);  return this.optionsBuilder_.getMessageOrBuilder(index); } public List<? extends OptionOrBuilder> getOptionsOrBuilderList() { if (this.optionsBuilder_ != null) return this.optionsBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.options_); } public Option.Builder addOptionsBuilder() { return getOptionsFieldBuilder().addBuilder(Option.getDefaultInstance()); } public Option.Builder addOptionsBuilder(int index) { return getOptionsFieldBuilder().addBuilder(index, Option.getDefaultInstance()); }
/*      */     public List<Option.Builder> getOptionsBuilderList() { return getOptionsFieldBuilder().getBuilderList(); }
/*      */     private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> getOptionsFieldBuilder() { if (this.optionsBuilder_ == null) { this.optionsBuilder_ = new RepeatedFieldBuilderV3<>(this.options_, ((this.bitField0_ & 0x2) != 0), getParentForChildren(), isClean()); this.options_ = null; }  return this.optionsBuilder_; }
/*      */     public boolean hasSourceContext() { return (this.sourceContextBuilder_ != null || this.sourceContext_ != null); }
/*      */     public SourceContext getSourceContext() { if (this.sourceContextBuilder_ == null) return (this.sourceContext_ == null) ? SourceContext.getDefaultInstance() : this.sourceContext_;  return this.sourceContextBuilder_.getMessage(); }
/*      */     public Builder setSourceContext(SourceContext value) { if (this.sourceContextBuilder_ == null) { if (value == null) throw new NullPointerException();  this.sourceContext_ = value; onChanged(); } else { this.sourceContextBuilder_.setMessage(value); }  return this; }
/*      */     public Builder setSourceContext(SourceContext.Builder builderForValue) { if (this.sourceContextBuilder_ == null) { this.sourceContext_ = builderForValue.build(); onChanged(); } else { this.sourceContextBuilder_.setMessage(builderForValue.build()); }  return this; }
/*      */     public Builder mergeSourceContext(SourceContext value) { if (this.sourceContextBuilder_ == null) { if (this.sourceContext_ != null) { this.sourceContext_ = SourceContext.newBuilder(this.sourceContext_).mergeFrom(value).buildPartial(); } else { this.sourceContext_ = value; }  onChanged(); } else { this.sourceContextBuilder_.mergeFrom(value); }  return this; }
/*      */     public Builder clearSourceContext() { if (this.sourceContextBuilder_ == null) { this.sourceContext_ = null; onChanged(); } else { this.sourceContext_ = null; this.sourceContextBuilder_ = null; }  return this; }
/*      */     public SourceContext.Builder getSourceContextBuilder() { onChanged(); return getSourceContextFieldBuilder().getBuilder(); }
/*      */     public SourceContextOrBuilder getSourceContextOrBuilder() { if (this.sourceContextBuilder_ != null) return this.sourceContextBuilder_.getMessageOrBuilder();  return (this.sourceContext_ == null) ? SourceContext.getDefaultInstance() : this.sourceContext_; }
/*      */     private SingleFieldBuilderV3<SourceContext, SourceContext.Builder, SourceContextOrBuilder> getSourceContextFieldBuilder() { if (this.sourceContextBuilder_ == null) { this.sourceContextBuilder_ = new SingleFieldBuilderV3<>(getSourceContext(), getParentForChildren(), isClean()); this.sourceContext_ = null; }  return this.sourceContextBuilder_; }
/* 1718 */     public Builder setSyntaxValue(int value) { this.syntax_ = value;
/* 1719 */       onChanged();
/* 1720 */       return this; }
/*      */ 
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
/* 1732 */       Syntax result = Syntax.valueOf(this.syntax_);
/* 1733 */       return (result == null) ? Syntax.UNRECOGNIZED : result;
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
/* 1745 */       if (value == null) {
/* 1746 */         throw new NullPointerException();
/*      */       }
/*      */       
/* 1749 */       this.syntax_ = value.getNumber();
/* 1750 */       onChanged();
/* 1751 */       return this;
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
/* 1763 */       this.syntax_ = 0;
/* 1764 */       onChanged();
/* 1765 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 1770 */       return super.setUnknownFields(unknownFields);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 1776 */       return super.mergeUnknownFields(unknownFields);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1786 */   private static final Enum DEFAULT_INSTANCE = new Enum();
/*      */ 
/*      */   
/*      */   public static Enum getDefaultInstance() {
/* 1790 */     return DEFAULT_INSTANCE;
/*      */   }
/*      */ 
/*      */   
/* 1794 */   private static final Parser<Enum> PARSER = new AbstractParser<Enum>()
/*      */     {
/*      */ 
/*      */       
/*      */       public Enum parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */       {
/* 1800 */         return new Enum(input, extensionRegistry);
/*      */       }
/*      */     };
/*      */   
/*      */   public static Parser<Enum> parser() {
/* 1805 */     return PARSER;
/*      */   }
/*      */ 
/*      */   
/*      */   public Parser<Enum> getParserForType() {
/* 1810 */     return PARSER;
/*      */   }
/*      */ 
/*      */   
/*      */   public Enum getDefaultInstanceForType() {
/* 1815 */     return DEFAULT_INSTANCE;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Enum.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */