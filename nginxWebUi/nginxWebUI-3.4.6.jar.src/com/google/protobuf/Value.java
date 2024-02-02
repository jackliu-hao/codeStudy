/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ 
/*      */ 
/*      */ public final class Value
/*      */   extends GeneratedMessageV3
/*      */   implements ValueOrBuilder
/*      */ {
/*      */   private static final long serialVersionUID = 0L;
/*      */   private int kindCase_;
/*      */   private Object kind_;
/*      */   public static final int NULL_VALUE_FIELD_NUMBER = 1;
/*      */   public static final int NUMBER_VALUE_FIELD_NUMBER = 2;
/*      */   public static final int STRING_VALUE_FIELD_NUMBER = 3;
/*      */   public static final int BOOL_VALUE_FIELD_NUMBER = 4;
/*      */   public static final int STRUCT_VALUE_FIELD_NUMBER = 5;
/*      */   public static final int LIST_VALUE_FIELD_NUMBER = 6;
/*      */   private byte memoizedIsInitialized;
/*      */   
/*      */   private Value(GeneratedMessageV3.Builder<?> builder) {
/*   24 */     super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  141 */     this.kindCase_ = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  388 */     this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Value(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Value(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int rawValue; String s; Struct.Builder builder; ListValue.Builder subBuilder; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: rawValue = input.readEnum(); this.kindCase_ = 1; this.kind_ = Integer.valueOf(rawValue); continue;case 17: this.kindCase_ = 2; this.kind_ = Double.valueOf(input.readDouble()); continue;case 26: s = input.readStringRequireUtf8(); this.kindCase_ = 3; this.kind_ = s; continue;case 32: this.kindCase_ = 4; this.kind_ = Boolean.valueOf(input.readBool()); continue;case 42: builder = null; if (this.kindCase_ == 5) builder = ((Struct)this.kind_).toBuilder();  this.kind_ = input.readMessage(Struct.parser(), extensionRegistry); if (builder != null) { builder.mergeFrom((Struct)this.kind_); this.kind_ = builder.buildPartial(); }  this.kindCase_ = 5; continue;case 50: subBuilder = null; if (this.kindCase_ == 6) subBuilder = ((ListValue)this.kind_).toBuilder();  this.kind_ = input.readMessage(ListValue.parser(), extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom((ListValue)this.kind_); this.kind_ = subBuilder.buildPartial(); }  this.kindCase_ = 6; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return StructProto.internal_static_google_protobuf_Value_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return StructProto.internal_static_google_protobuf_Value_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Value.class, (Class)Builder.class); } private Value() { this.kindCase_ = 0; this.memoizedIsInitialized = -1; }
/*      */   public enum KindCase implements Internal.EnumLite, AbstractMessageLite.InternalOneOfEnum {
/*      */     NULL_VALUE(1), NUMBER_VALUE(2), STRING_VALUE(3), BOOL_VALUE(4), STRUCT_VALUE(5), LIST_VALUE(6), KIND_NOT_SET(0);
/*  391 */     private final int value; KindCase(int value) { this.value = value; } public static KindCase forNumber(int value) { switch (value) { case 1: return NULL_VALUE;case 2: return NUMBER_VALUE;case 3: return STRING_VALUE;case 4: return BOOL_VALUE;case 5: return STRUCT_VALUE;case 6: return LIST_VALUE;case 0: return KIND_NOT_SET; }  return null; } public int getNumber() { return this.value; } } public KindCase getKindCase() { return KindCase.forNumber(this.kindCase_); } public int getNullValueValue() { if (this.kindCase_ == 1) return ((Integer)this.kind_).intValue();  return 0; } public NullValue getNullValue() { if (this.kindCase_ == 1) { NullValue result = NullValue.valueOf(((Integer)this.kind_).intValue()); return (result == null) ? NullValue.UNRECOGNIZED : result; }  return NullValue.NULL_VALUE; } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  392 */     if (isInitialized == 1) return true; 
/*  393 */     if (isInitialized == 0) return false;
/*      */     
/*  395 */     this.memoizedIsInitialized = 1;
/*  396 */     return true; } public double getNumberValue() { if (this.kindCase_ == 2) return ((Double)this.kind_).doubleValue();  return 0.0D; } public String getStringValue() { Object ref = ""; if (this.kindCase_ == 3) ref = this.kind_;  if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (this.kindCase_ == 3) this.kind_ = s;  return s; } public ByteString getStringValueBytes() { Object ref = ""; if (this.kindCase_ == 3) ref = this.kind_;  if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); if (this.kindCase_ == 3) this.kind_ = b;  return b; }  return (ByteString)ref; } public boolean getBoolValue() { if (this.kindCase_ == 4) return ((Boolean)this.kind_).booleanValue();  return false; } public boolean hasStructValue() { return (this.kindCase_ == 5); }
/*      */   public Struct getStructValue() { if (this.kindCase_ == 5) return (Struct)this.kind_;  return Struct.getDefaultInstance(); }
/*      */   public StructOrBuilder getStructValueOrBuilder() { if (this.kindCase_ == 5) return (Struct)this.kind_;  return Struct.getDefaultInstance(); }
/*      */   public boolean hasListValue() { return (this.kindCase_ == 6); }
/*      */   public ListValue getListValue() { if (this.kindCase_ == 6) return (ListValue)this.kind_;  return ListValue.getDefaultInstance(); }
/*      */   public ListValueOrBuilder getListValueOrBuilder() { if (this.kindCase_ == 6) return (ListValue)this.kind_;  return ListValue.getDefaultInstance(); }
/*  402 */   public void writeTo(CodedOutputStream output) throws IOException { if (this.kindCase_ == 1) {
/*  403 */       output.writeEnum(1, ((Integer)this.kind_).intValue());
/*      */     }
/*  405 */     if (this.kindCase_ == 2) {
/*  406 */       output.writeDouble(2, ((Double)this.kind_)
/*  407 */           .doubleValue());
/*      */     }
/*  409 */     if (this.kindCase_ == 3) {
/*  410 */       GeneratedMessageV3.writeString(output, 3, this.kind_);
/*      */     }
/*  412 */     if (this.kindCase_ == 4) {
/*  413 */       output.writeBool(4, ((Boolean)this.kind_)
/*  414 */           .booleanValue());
/*      */     }
/*  416 */     if (this.kindCase_ == 5) {
/*  417 */       output.writeMessage(5, (Struct)this.kind_);
/*      */     }
/*  419 */     if (this.kindCase_ == 6) {
/*  420 */       output.writeMessage(6, (ListValue)this.kind_);
/*      */     }
/*  422 */     this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getSerializedSize() {
/*  427 */     int size = this.memoizedSize;
/*  428 */     if (size != -1) return size;
/*      */     
/*  430 */     size = 0;
/*  431 */     if (this.kindCase_ == 1) {
/*  432 */       size += 
/*  433 */         CodedOutputStream.computeEnumSize(1, ((Integer)this.kind_).intValue());
/*      */     }
/*  435 */     if (this.kindCase_ == 2) {
/*  436 */       size += 
/*  437 */         CodedOutputStream.computeDoubleSize(2, ((Double)this.kind_)
/*  438 */           .doubleValue());
/*      */     }
/*  440 */     if (this.kindCase_ == 3) {
/*  441 */       size += GeneratedMessageV3.computeStringSize(3, this.kind_);
/*      */     }
/*  443 */     if (this.kindCase_ == 4) {
/*  444 */       size += 
/*  445 */         CodedOutputStream.computeBoolSize(4, ((Boolean)this.kind_)
/*  446 */           .booleanValue());
/*      */     }
/*  448 */     if (this.kindCase_ == 5) {
/*  449 */       size += 
/*  450 */         CodedOutputStream.computeMessageSize(5, (Struct)this.kind_);
/*      */     }
/*  452 */     if (this.kindCase_ == 6) {
/*  453 */       size += 
/*  454 */         CodedOutputStream.computeMessageSize(6, (ListValue)this.kind_);
/*      */     }
/*  456 */     size += this.unknownFields.getSerializedSize();
/*  457 */     this.memoizedSize = size;
/*  458 */     return size;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object obj) {
/*  463 */     if (obj == this) {
/*  464 */       return true;
/*      */     }
/*  466 */     if (!(obj instanceof Value)) {
/*  467 */       return super.equals(obj);
/*      */     }
/*  469 */     Value other = (Value)obj;
/*      */     
/*  471 */     if (!getKindCase().equals(other.getKindCase())) return false; 
/*  472 */     switch (this.kindCase_) {
/*      */       case 1:
/*  474 */         if (getNullValueValue() != other
/*  475 */           .getNullValueValue()) return false; 
/*      */         break;
/*      */       case 2:
/*  478 */         if (Double.doubleToLongBits(getNumberValue()) != 
/*  479 */           Double.doubleToLongBits(other
/*  480 */             .getNumberValue())) return false;
/*      */         
/*      */         break;
/*      */       case 3:
/*  484 */         if (!getStringValue().equals(other.getStringValue())) return false; 
/*      */         break;
/*      */       case 4:
/*  487 */         if (getBoolValue() != other
/*  488 */           .getBoolValue()) return false;
/*      */         
/*      */         break;
/*      */       case 5:
/*  492 */         if (!getStructValue().equals(other.getStructValue())) return false;
/*      */         
/*      */         break;
/*      */       case 6:
/*  496 */         if (!getListValue().equals(other.getListValue())) return false;
/*      */         
/*      */         break;
/*      */     } 
/*      */     
/*  501 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  502 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  507 */     if (this.memoizedHashCode != 0) {
/*  508 */       return this.memoizedHashCode;
/*      */     }
/*  510 */     int hash = 41;
/*  511 */     hash = 19 * hash + getDescriptor().hashCode();
/*  512 */     switch (this.kindCase_) {
/*      */       case 1:
/*  514 */         hash = 37 * hash + 1;
/*  515 */         hash = 53 * hash + getNullValueValue();
/*      */         break;
/*      */       case 2:
/*  518 */         hash = 37 * hash + 2;
/*  519 */         hash = 53 * hash + Internal.hashLong(
/*  520 */             Double.doubleToLongBits(getNumberValue()));
/*      */         break;
/*      */       case 3:
/*  523 */         hash = 37 * hash + 3;
/*  524 */         hash = 53 * hash + getStringValue().hashCode();
/*      */         break;
/*      */       case 4:
/*  527 */         hash = 37 * hash + 4;
/*  528 */         hash = 53 * hash + Internal.hashBoolean(
/*  529 */             getBoolValue());
/*      */         break;
/*      */       case 5:
/*  532 */         hash = 37 * hash + 5;
/*  533 */         hash = 53 * hash + getStructValue().hashCode();
/*      */         break;
/*      */       case 6:
/*  536 */         hash = 37 * hash + 6;
/*  537 */         hash = 53 * hash + getListValue().hashCode();
/*      */         break;
/*      */     } 
/*      */ 
/*      */     
/*  542 */     hash = 29 * hash + this.unknownFields.hashCode();
/*  543 */     this.memoizedHashCode = hash;
/*  544 */     return hash;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Value parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  550 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Value parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  556 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */ 
/*      */   
/*      */   public static Value parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  561 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Value parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  567 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static Value parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  571 */     return PARSER.parseFrom(data);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Value parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  577 */     return PARSER.parseFrom(data, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static Value parseFrom(InputStream input) throws IOException {
/*  581 */     return 
/*  582 */       GeneratedMessageV3.<Value>parseWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Value parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  588 */     return 
/*  589 */       GeneratedMessageV3.<Value>parseWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */   
/*      */   public static Value parseDelimitedFrom(InputStream input) throws IOException {
/*  593 */     return 
/*  594 */       GeneratedMessageV3.<Value>parseDelimitedWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Value parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  600 */     return 
/*  601 */       GeneratedMessageV3.<Value>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */ 
/*      */   
/*      */   public static Value parseFrom(CodedInputStream input) throws IOException {
/*  606 */     return 
/*  607 */       GeneratedMessageV3.<Value>parseWithIOException(PARSER, input);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Value parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  613 */     return 
/*  614 */       GeneratedMessageV3.<Value>parseWithIOException(PARSER, input, extensionRegistry);
/*      */   }
/*      */   
/*      */   public Builder newBuilderForType() {
/*  618 */     return newBuilder();
/*      */   } public static Builder newBuilder() {
/*  620 */     return DEFAULT_INSTANCE.toBuilder();
/*      */   }
/*      */   public static Builder newBuilder(Value prototype) {
/*  623 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */   }
/*      */   
/*      */   public Builder toBuilder() {
/*  627 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  628 */       .mergeFrom(this);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  634 */     Builder builder = new Builder(parent);
/*  635 */     return builder;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Builder
/*      */     extends GeneratedMessageV3.Builder<Builder>
/*      */     implements ValueOrBuilder
/*      */   {
/*      */     private int kindCase_;
/*      */     
/*      */     private Object kind_;
/*      */     
/*      */     private SingleFieldBuilderV3<Struct, Struct.Builder, StructOrBuilder> structValueBuilder_;
/*      */     
/*      */     private SingleFieldBuilderV3<ListValue, ListValue.Builder, ListValueOrBuilder> listValueBuilder_;
/*      */ 
/*      */     
/*      */     public static final Descriptors.Descriptor getDescriptor() {
/*  654 */       return StructProto.internal_static_google_protobuf_Value_descriptor;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  660 */       return StructProto.internal_static_google_protobuf_Value_fieldAccessorTable
/*  661 */         .ensureFieldAccessorsInitialized((Class)Value.class, (Class)Builder.class);
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
/*      */     private Builder()
/*      */     {
/*  845 */       this.kindCase_ = 0; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.kindCase_ = 0; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (GeneratedMessageV3.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.kindCase_ = 0; this.kind_ = null; return this; } public Descriptors.Descriptor getDescriptorForType() { return StructProto.internal_static_google_protobuf_Value_descriptor; } public Value getDefaultInstanceForType() { return Value.getDefaultInstance(); } public Value build() { Value result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public Value buildPartial() { Value result = new Value(this); if (this.kindCase_ == 1) result.kind_ = this.kind_;  if (this.kindCase_ == 2) result.kind_ = this.kind_;  if (this.kindCase_ == 3) result.kind_ = this.kind_;  if (this.kindCase_ == 4) result.kind_ = this.kind_;  if (this.kindCase_ == 5) if (this.structValueBuilder_ == null) { result.kind_ = this.kind_; } else { result.kind_ = this.structValueBuilder_.build(); }   if (this.kindCase_ == 6) if (this.listValueBuilder_ == null) { result.kind_ = this.kind_; } else { result.kind_ = this.listValueBuilder_.build(); }   result.kindCase_ = this.kindCase_; onBuilt(); return result; } public Builder clone() { return super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof Value) return mergeFrom((Value)other);  super.mergeFrom(other); return this; }
/*      */     public Builder mergeFrom(Value other) { if (other == Value.getDefaultInstance()) return this;  switch (other.getKindCase()) { case NULL_VALUE: setNullValueValue(other.getNullValueValue()); break;case NUMBER_VALUE: setNumberValue(other.getNumberValue()); break;case STRING_VALUE: this.kindCase_ = 3; this.kind_ = other.kind_; onChanged(); break;case BOOL_VALUE: setBoolValue(other.getBoolValue()); break;case STRUCT_VALUE: mergeStructValue(other.getStructValue()); break;case LIST_VALUE: mergeListValue(other.getListValue()); break; }  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */     public final boolean isInitialized() { return true; }
/*      */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { Value parsedMessage = null; try { parsedMessage = Value.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (Value)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/*  849 */     public Value.KindCase getKindCase() { return Value.KindCase.forNumber(this.kindCase_); }
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder clearKind() {
/*  854 */       this.kindCase_ = 0;
/*  855 */       this.kind_ = null;
/*  856 */       onChanged();
/*  857 */       return this;
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
/*      */     public int getNullValueValue() {
/*  870 */       if (this.kindCase_ == 1) {
/*  871 */         return ((Integer)this.kind_).intValue();
/*      */       }
/*  873 */       return 0;
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
/*      */     public Builder setNullValueValue(int value) {
/*  885 */       this.kindCase_ = 1;
/*  886 */       this.kind_ = Integer.valueOf(value);
/*  887 */       onChanged();
/*  888 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public NullValue getNullValue() {
/*  899 */       if (this.kindCase_ == 1) {
/*      */         
/*  901 */         NullValue result = NullValue.valueOf(((Integer)this.kind_)
/*  902 */             .intValue());
/*  903 */         return (result == null) ? NullValue.UNRECOGNIZED : result;
/*      */       } 
/*  905 */       return NullValue.NULL_VALUE;
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
/*      */     public Builder setNullValue(NullValue value) {
/*  917 */       if (value == null) {
/*  918 */         throw new NullPointerException();
/*      */       }
/*  920 */       this.kindCase_ = 1;
/*  921 */       this.kind_ = Integer.valueOf(value.getNumber());
/*  922 */       onChanged();
/*  923 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder clearNullValue() {
/*  934 */       if (this.kindCase_ == 1) {
/*  935 */         this.kindCase_ = 0;
/*  936 */         this.kind_ = null;
/*  937 */         onChanged();
/*      */       } 
/*  939 */       return this;
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
/*      */     public double getNumberValue() {
/*  951 */       if (this.kindCase_ == 2) {
/*  952 */         return ((Double)this.kind_).doubleValue();
/*      */       }
/*  954 */       return 0.0D;
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
/*      */     public Builder setNumberValue(double value) {
/*  966 */       this.kindCase_ = 2;
/*  967 */       this.kind_ = Double.valueOf(value);
/*  968 */       onChanged();
/*  969 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder clearNumberValue() {
/*  980 */       if (this.kindCase_ == 2) {
/*  981 */         this.kindCase_ = 0;
/*  982 */         this.kind_ = null;
/*  983 */         onChanged();
/*      */       } 
/*  985 */       return this;
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
/*      */     public String getStringValue() {
/*  997 */       Object ref = "";
/*  998 */       if (this.kindCase_ == 3) {
/*  999 */         ref = this.kind_;
/*      */       }
/* 1001 */       if (!(ref instanceof String)) {
/* 1002 */         ByteString bs = (ByteString)ref;
/*      */         
/* 1004 */         String s = bs.toStringUtf8();
/* 1005 */         if (this.kindCase_ == 3) {
/* 1006 */           this.kind_ = s;
/*      */         }
/* 1008 */         return s;
/*      */       } 
/* 1010 */       return (String)ref;
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
/*      */     public ByteString getStringValueBytes() {
/* 1023 */       Object ref = "";
/* 1024 */       if (this.kindCase_ == 3) {
/* 1025 */         ref = this.kind_;
/*      */       }
/* 1027 */       if (ref instanceof String) {
/*      */         
/* 1029 */         ByteString b = ByteString.copyFromUtf8((String)ref);
/*      */         
/* 1031 */         if (this.kindCase_ == 3) {
/* 1032 */           this.kind_ = b;
/*      */         }
/* 1034 */         return b;
/*      */       } 
/* 1036 */       return (ByteString)ref;
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
/*      */     public Builder setStringValue(String value) {
/* 1050 */       if (value == null) {
/* 1051 */         throw new NullPointerException();
/*      */       }
/* 1053 */       this.kindCase_ = 3;
/* 1054 */       this.kind_ = value;
/* 1055 */       onChanged();
/* 1056 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder clearStringValue() {
/* 1067 */       if (this.kindCase_ == 3) {
/* 1068 */         this.kindCase_ = 0;
/* 1069 */         this.kind_ = null;
/* 1070 */         onChanged();
/*      */       } 
/* 1072 */       return this;
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
/*      */     public Builder setStringValueBytes(ByteString value) {
/* 1085 */       if (value == null) {
/* 1086 */         throw new NullPointerException();
/*      */       }
/* 1088 */       AbstractMessageLite.checkByteStringIsUtf8(value);
/* 1089 */       this.kindCase_ = 3;
/* 1090 */       this.kind_ = value;
/* 1091 */       onChanged();
/* 1092 */       return this;
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
/*      */     public boolean getBoolValue() {
/* 1104 */       if (this.kindCase_ == 4) {
/* 1105 */         return ((Boolean)this.kind_).booleanValue();
/*      */       }
/* 1107 */       return false;
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
/*      */     public Builder setBoolValue(boolean value) {
/* 1119 */       this.kindCase_ = 4;
/* 1120 */       this.kind_ = Boolean.valueOf(value);
/* 1121 */       onChanged();
/* 1122 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder clearBoolValue() {
/* 1133 */       if (this.kindCase_ == 4) {
/* 1134 */         this.kindCase_ = 0;
/* 1135 */         this.kind_ = null;
/* 1136 */         onChanged();
/*      */       } 
/* 1138 */       return this;
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
/*      */     public boolean hasStructValue() {
/* 1152 */       return (this.kindCase_ == 5);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Struct getStructValue() {
/* 1163 */       if (this.structValueBuilder_ == null) {
/* 1164 */         if (this.kindCase_ == 5) {
/* 1165 */           return (Struct)this.kind_;
/*      */         }
/* 1167 */         return Struct.getDefaultInstance();
/*      */       } 
/* 1169 */       if (this.kindCase_ == 5) {
/* 1170 */         return this.structValueBuilder_.getMessage();
/*      */       }
/* 1172 */       return Struct.getDefaultInstance();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setStructValue(Struct value) {
/* 1183 */       if (this.structValueBuilder_ == null) {
/* 1184 */         if (value == null) {
/* 1185 */           throw new NullPointerException();
/*      */         }
/* 1187 */         this.kind_ = value;
/* 1188 */         onChanged();
/*      */       } else {
/* 1190 */         this.structValueBuilder_.setMessage(value);
/*      */       } 
/* 1192 */       this.kindCase_ = 5;
/* 1193 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setStructValue(Struct.Builder builderForValue) {
/* 1204 */       if (this.structValueBuilder_ == null) {
/* 1205 */         this.kind_ = builderForValue.build();
/* 1206 */         onChanged();
/*      */       } else {
/* 1208 */         this.structValueBuilder_.setMessage(builderForValue.build());
/*      */       } 
/* 1210 */       this.kindCase_ = 5;
/* 1211 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder mergeStructValue(Struct value) {
/* 1221 */       if (this.structValueBuilder_ == null) {
/* 1222 */         if (this.kindCase_ == 5 && this.kind_ != 
/* 1223 */           Struct.getDefaultInstance()) {
/* 1224 */           this
/* 1225 */             .kind_ = Struct.newBuilder((Struct)this.kind_).mergeFrom(value).buildPartial();
/*      */         } else {
/* 1227 */           this.kind_ = value;
/*      */         } 
/* 1229 */         onChanged();
/*      */       } else {
/* 1231 */         if (this.kindCase_ == 5) {
/* 1232 */           this.structValueBuilder_.mergeFrom(value);
/*      */         }
/* 1234 */         this.structValueBuilder_.setMessage(value);
/*      */       } 
/* 1236 */       this.kindCase_ = 5;
/* 1237 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder clearStructValue() {
/* 1247 */       if (this.structValueBuilder_ == null) {
/* 1248 */         if (this.kindCase_ == 5) {
/* 1249 */           this.kindCase_ = 0;
/* 1250 */           this.kind_ = null;
/* 1251 */           onChanged();
/*      */         } 
/*      */       } else {
/* 1254 */         if (this.kindCase_ == 5) {
/* 1255 */           this.kindCase_ = 0;
/* 1256 */           this.kind_ = null;
/*      */         } 
/* 1258 */         this.structValueBuilder_.clear();
/*      */       } 
/* 1260 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Struct.Builder getStructValueBuilder() {
/* 1270 */       return getStructValueFieldBuilder().getBuilder();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public StructOrBuilder getStructValueOrBuilder() {
/* 1280 */       if (this.kindCase_ == 5 && this.structValueBuilder_ != null) {
/* 1281 */         return this.structValueBuilder_.getMessageOrBuilder();
/*      */       }
/* 1283 */       if (this.kindCase_ == 5) {
/* 1284 */         return (Struct)this.kind_;
/*      */       }
/* 1286 */       return Struct.getDefaultInstance();
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
/*      */     private SingleFieldBuilderV3<Struct, Struct.Builder, StructOrBuilder> getStructValueFieldBuilder() {
/* 1299 */       if (this.structValueBuilder_ == null) {
/* 1300 */         if (this.kindCase_ != 5) {
/* 1301 */           this.kind_ = Struct.getDefaultInstance();
/*      */         }
/* 1303 */         this
/*      */ 
/*      */ 
/*      */           
/* 1307 */           .structValueBuilder_ = new SingleFieldBuilderV3<>((Struct)this.kind_, getParentForChildren(), isClean());
/* 1308 */         this.kind_ = null;
/*      */       } 
/* 1310 */       this.kindCase_ = 5;
/* 1311 */       onChanged();
/* 1312 */       return this.structValueBuilder_;
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
/*      */     public boolean hasListValue() {
/* 1326 */       return (this.kindCase_ == 6);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ListValue getListValue() {
/* 1337 */       if (this.listValueBuilder_ == null) {
/* 1338 */         if (this.kindCase_ == 6) {
/* 1339 */           return (ListValue)this.kind_;
/*      */         }
/* 1341 */         return ListValue.getDefaultInstance();
/*      */       } 
/* 1343 */       if (this.kindCase_ == 6) {
/* 1344 */         return this.listValueBuilder_.getMessage();
/*      */       }
/* 1346 */       return ListValue.getDefaultInstance();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setListValue(ListValue value) {
/* 1357 */       if (this.listValueBuilder_ == null) {
/* 1358 */         if (value == null) {
/* 1359 */           throw new NullPointerException();
/*      */         }
/* 1361 */         this.kind_ = value;
/* 1362 */         onChanged();
/*      */       } else {
/* 1364 */         this.listValueBuilder_.setMessage(value);
/*      */       } 
/* 1366 */       this.kindCase_ = 6;
/* 1367 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder setListValue(ListValue.Builder builderForValue) {
/* 1378 */       if (this.listValueBuilder_ == null) {
/* 1379 */         this.kind_ = builderForValue.build();
/* 1380 */         onChanged();
/*      */       } else {
/* 1382 */         this.listValueBuilder_.setMessage(builderForValue.build());
/*      */       } 
/* 1384 */       this.kindCase_ = 6;
/* 1385 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder mergeListValue(ListValue value) {
/* 1395 */       if (this.listValueBuilder_ == null) {
/* 1396 */         if (this.kindCase_ == 6 && this.kind_ != 
/* 1397 */           ListValue.getDefaultInstance()) {
/* 1398 */           this
/* 1399 */             .kind_ = ListValue.newBuilder((ListValue)this.kind_).mergeFrom(value).buildPartial();
/*      */         } else {
/* 1401 */           this.kind_ = value;
/*      */         } 
/* 1403 */         onChanged();
/*      */       } else {
/* 1405 */         if (this.kindCase_ == 6) {
/* 1406 */           this.listValueBuilder_.mergeFrom(value);
/*      */         }
/* 1408 */         this.listValueBuilder_.setMessage(value);
/*      */       } 
/* 1410 */       this.kindCase_ = 6;
/* 1411 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder clearListValue() {
/* 1421 */       if (this.listValueBuilder_ == null) {
/* 1422 */         if (this.kindCase_ == 6) {
/* 1423 */           this.kindCase_ = 0;
/* 1424 */           this.kind_ = null;
/* 1425 */           onChanged();
/*      */         } 
/*      */       } else {
/* 1428 */         if (this.kindCase_ == 6) {
/* 1429 */           this.kindCase_ = 0;
/* 1430 */           this.kind_ = null;
/*      */         } 
/* 1432 */         this.listValueBuilder_.clear();
/*      */       } 
/* 1434 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ListValue.Builder getListValueBuilder() {
/* 1444 */       return getListValueFieldBuilder().getBuilder();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ListValueOrBuilder getListValueOrBuilder() {
/* 1454 */       if (this.kindCase_ == 6 && this.listValueBuilder_ != null) {
/* 1455 */         return this.listValueBuilder_.getMessageOrBuilder();
/*      */       }
/* 1457 */       if (this.kindCase_ == 6) {
/* 1458 */         return (ListValue)this.kind_;
/*      */       }
/* 1460 */       return ListValue.getDefaultInstance();
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
/*      */     private SingleFieldBuilderV3<ListValue, ListValue.Builder, ListValueOrBuilder> getListValueFieldBuilder() {
/* 1473 */       if (this.listValueBuilder_ == null) {
/* 1474 */         if (this.kindCase_ != 6) {
/* 1475 */           this.kind_ = ListValue.getDefaultInstance();
/*      */         }
/* 1477 */         this
/*      */ 
/*      */ 
/*      */           
/* 1481 */           .listValueBuilder_ = new SingleFieldBuilderV3<>((ListValue)this.kind_, getParentForChildren(), isClean());
/* 1482 */         this.kind_ = null;
/*      */       } 
/* 1484 */       this.kindCase_ = 6;
/* 1485 */       onChanged();
/* 1486 */       return this.listValueBuilder_;
/*      */     }
/*      */ 
/*      */     
/*      */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 1491 */       return super.setUnknownFields(unknownFields);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 1497 */       return super.mergeUnknownFields(unknownFields);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1507 */   private static final Value DEFAULT_INSTANCE = new Value();
/*      */ 
/*      */   
/*      */   public static Value getDefaultInstance() {
/* 1511 */     return DEFAULT_INSTANCE;
/*      */   }
/*      */ 
/*      */   
/* 1515 */   private static final Parser<Value> PARSER = new AbstractParser<Value>()
/*      */     {
/*      */ 
/*      */       
/*      */       public Value parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */       {
/* 1521 */         return new Value(input, extensionRegistry);
/*      */       }
/*      */     };
/*      */   
/*      */   public static Parser<Value> parser() {
/* 1526 */     return PARSER;
/*      */   }
/*      */ 
/*      */   
/*      */   public Parser<Value> getParserForType() {
/* 1531 */     return PARSER;
/*      */   }
/*      */ 
/*      */   
/*      */   public Value getDefaultInstanceForType() {
/* 1536 */     return DEFAULT_INSTANCE;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Value.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */