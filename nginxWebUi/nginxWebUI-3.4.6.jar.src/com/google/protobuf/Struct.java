/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Struct
/*     */   extends GeneratedMessageV3
/*     */   implements StructOrBuilder
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   public static final int FIELDS_FIELD_NUMBER = 1;
/*     */   private MapField<String, Value> fields_;
/*     */   private byte memoizedIsInitialized;
/*     */   
/*     */   private Struct(GeneratedMessageV3.Builder<?> builder) {
/*  26 */     super(builder);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 211 */     this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Struct(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Struct(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { MapEntry<String, Value> fields__; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: if ((mutable_bitField0_ & 0x1) == 0) { this.fields_ = MapField.newMapField(FieldsDefaultEntryHolder.defaultEntry); mutable_bitField0_ |= 0x1; }  fields__ = input.<MapEntry<String, Value>>readMessage(FieldsDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry); this.fields_.getMutableMap().put(fields__.getKey(), fields__.getValue()); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return StructProto.internal_static_google_protobuf_Struct_descriptor; } private Struct() { this.memoizedIsInitialized = -1; }
/*     */   protected MapField internalGetMapField(int number) { switch (number) { case 1: return internalGetFields(); }  throw new RuntimeException("Invalid map field number: " + number); }
/*     */   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return StructProto.internal_static_google_protobuf_Struct_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Struct.class, (Class)Builder.class); } private static final class FieldsDefaultEntryHolder {
/* 214 */     static final MapEntry<String, Value> defaultEntry = MapEntry.newDefaultInstance(StructProto.internal_static_google_protobuf_Struct_FieldsEntry_descriptor, WireFormat.FieldType.STRING, "", WireFormat.FieldType.MESSAGE, Value.getDefaultInstance()); } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 215 */     if (isInitialized == 1) return true; 
/* 216 */     if (isInitialized == 0) return false;
/*     */     
/* 218 */     this.memoizedIsInitialized = 1;
/* 219 */     return true; } private MapField<String, Value> internalGetFields() { if (this.fields_ == null) return MapField.emptyMapField(FieldsDefaultEntryHolder.defaultEntry);  return this.fields_; }
/*     */   public int getFieldsCount() { return internalGetFields().getMap().size(); }
/*     */   public boolean containsFields(String key) { if (key == null) throw new NullPointerException();  return internalGetFields().getMap().containsKey(key); }
/*     */   @Deprecated public Map<String, Value> getFields() { return getFieldsMap(); }
/*     */   public Map<String, Value> getFieldsMap() { return internalGetFields().getMap(); }
/*     */   public Value getFieldsOrDefault(String key, Value defaultValue) { if (key == null) throw new NullPointerException();  Map<String, Value> map = internalGetFields().getMap(); return map.containsKey(key) ? map.get(key) : defaultValue; }
/*     */   public Value getFieldsOrThrow(String key) { if (key == null) throw new NullPointerException();  Map<String, Value> map = internalGetFields().getMap(); if (!map.containsKey(key)) throw new IllegalArgumentException();  return map.get(key); }
/* 226 */   public void writeTo(CodedOutputStream output) throws IOException { GeneratedMessageV3.serializeStringMapTo(output, 
/*     */         
/* 228 */         internalGetFields(), FieldsDefaultEntryHolder.defaultEntry, 1);
/*     */ 
/*     */     
/* 231 */     this.unknownFields.writeTo(output); }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/* 236 */     int size = this.memoizedSize;
/* 237 */     if (size != -1) return size;
/*     */     
/* 239 */     size = 0;
/*     */     
/* 241 */     for (Map.Entry<String, Value> entry : (Iterable<Map.Entry<String, Value>>)internalGetFields().getMap().entrySet()) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 246 */       MapEntry<String, Value> fields__ = FieldsDefaultEntryHolder.defaultEntry.newBuilderForType().setKey(entry.getKey()).setValue(entry.getValue()).build();
/* 247 */       size += 
/* 248 */         CodedOutputStream.computeMessageSize(1, fields__);
/*     */     } 
/* 250 */     size += this.unknownFields.getSerializedSize();
/* 251 */     this.memoizedSize = size;
/* 252 */     return size;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 257 */     if (obj == this) {
/* 258 */       return true;
/*     */     }
/* 260 */     if (!(obj instanceof Struct)) {
/* 261 */       return super.equals(obj);
/*     */     }
/* 263 */     Struct other = (Struct)obj;
/*     */     
/* 265 */     if (!internalGetFields().equals(other
/* 266 */         .internalGetFields())) return false; 
/* 267 */     if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 268 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 273 */     if (this.memoizedHashCode != 0) {
/* 274 */       return this.memoizedHashCode;
/*     */     }
/* 276 */     int hash = 41;
/* 277 */     hash = 19 * hash + getDescriptor().hashCode();
/* 278 */     if (!internalGetFields().getMap().isEmpty()) {
/* 279 */       hash = 37 * hash + 1;
/* 280 */       hash = 53 * hash + internalGetFields().hashCode();
/*     */     } 
/* 282 */     hash = 29 * hash + this.unknownFields.hashCode();
/* 283 */     this.memoizedHashCode = hash;
/* 284 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Struct parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 290 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Struct parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 296 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Struct parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 301 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Struct parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 307 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Struct parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 311 */     return PARSER.parseFrom(data);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Struct parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 317 */     return PARSER.parseFrom(data, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Struct parseFrom(InputStream input) throws IOException {
/* 321 */     return 
/* 322 */       GeneratedMessageV3.<Struct>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Struct parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 328 */     return 
/* 329 */       GeneratedMessageV3.<Struct>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public static Struct parseDelimitedFrom(InputStream input) throws IOException {
/* 333 */     return 
/* 334 */       GeneratedMessageV3.<Struct>parseDelimitedWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Struct parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 340 */     return 
/* 341 */       GeneratedMessageV3.<Struct>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Struct parseFrom(CodedInputStream input) throws IOException {
/* 346 */     return 
/* 347 */       GeneratedMessageV3.<Struct>parseWithIOException(PARSER, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Struct parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 353 */     return 
/* 354 */       GeneratedMessageV3.<Struct>parseWithIOException(PARSER, input, extensionRegistry);
/*     */   }
/*     */   
/*     */   public Builder newBuilderForType() {
/* 358 */     return newBuilder();
/*     */   } public static Builder newBuilder() {
/* 360 */     return DEFAULT_INSTANCE.toBuilder();
/*     */   }
/*     */   public static Builder newBuilder(Struct prototype) {
/* 363 */     return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*     */   }
/*     */   
/*     */   public Builder toBuilder() {
/* 367 */     return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 368 */       .mergeFrom(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 374 */     Builder builder = new Builder(parent);
/* 375 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Builder
/*     */     extends GeneratedMessageV3.Builder<Builder>
/*     */     implements StructOrBuilder
/*     */   {
/*     */     private int bitField0_;
/*     */ 
/*     */ 
/*     */     
/*     */     private MapField<String, Value> fields_;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final Descriptors.Descriptor getDescriptor() {
/* 396 */       return StructProto.internal_static_google_protobuf_Struct_descriptor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected MapField internalGetMapField(int number) {
/* 402 */       switch (number) {
/*     */         case 1:
/* 404 */           return internalGetFields();
/*     */       } 
/* 406 */       throw new RuntimeException("Invalid map field number: " + number);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected MapField internalGetMutableMapField(int number) {
/* 413 */       switch (number) {
/*     */         case 1:
/* 415 */           return internalGetMutableFields();
/*     */       } 
/* 417 */       throw new RuntimeException("Invalid map field number: " + number);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 424 */       return StructProto.internal_static_google_protobuf_Struct_fieldAccessorTable
/* 425 */         .ensureFieldAccessorsInitialized((Class)Struct.class, (Class)Builder.class);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private Builder() {
/* 431 */       maybeForceBuilderInitialization();
/*     */     }
/*     */ 
/*     */     
/*     */     private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 436 */       super(parent);
/* 437 */       maybeForceBuilderInitialization();
/*     */     }
/*     */     private void maybeForceBuilderInitialization() {
/* 440 */       if (GeneratedMessageV3.alwaysUseFieldBuilders);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder clear() {
/* 446 */       super.clear();
/* 447 */       internalGetMutableFields().clear();
/* 448 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Descriptors.Descriptor getDescriptorForType() {
/* 454 */       return StructProto.internal_static_google_protobuf_Struct_descriptor;
/*     */     }
/*     */ 
/*     */     
/*     */     public Struct getDefaultInstanceForType() {
/* 459 */       return Struct.getDefaultInstance();
/*     */     }
/*     */ 
/*     */     
/*     */     public Struct build() {
/* 464 */       Struct result = buildPartial();
/* 465 */       if (!result.isInitialized()) {
/* 466 */         throw newUninitializedMessageException(result);
/*     */       }
/* 468 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Struct buildPartial() {
/* 473 */       Struct result = new Struct(this);
/* 474 */       int from_bitField0_ = this.bitField0_;
/* 475 */       result.fields_ = internalGetFields();
/* 476 */       result.fields_.makeImmutable();
/* 477 */       onBuilt();
/* 478 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clone() {
/* 483 */       return super.clone();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 489 */       return super.setField(field, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearField(Descriptors.FieldDescriptor field) {
/* 494 */       return super.clearField(field);
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 499 */       return super.clearOneof(oneof);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 505 */       return super.setRepeatedField(field, index, value);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 511 */       return super.addRepeatedField(field, value);
/*     */     }
/*     */     
/*     */     public Builder mergeFrom(Message other) {
/* 515 */       if (other instanceof Struct) {
/* 516 */         return mergeFrom((Struct)other);
/*     */       }
/* 518 */       super.mergeFrom(other);
/* 519 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder mergeFrom(Struct other) {
/* 524 */       if (other == Struct.getDefaultInstance()) return this; 
/* 525 */       internalGetMutableFields().mergeFrom(other
/* 526 */           .internalGetFields());
/* 527 */       mergeUnknownFields(other.unknownFields);
/* 528 */       onChanged();
/* 529 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final boolean isInitialized() {
/* 534 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 542 */       Struct parsedMessage = null;
/*     */       try {
/* 544 */         parsedMessage = Struct.PARSER.parsePartialFrom(input, extensionRegistry);
/* 545 */       } catch (InvalidProtocolBufferException e) {
/* 546 */         parsedMessage = (Struct)e.getUnfinishedMessage();
/* 547 */         throw e.unwrapIOException();
/*     */       } finally {
/* 549 */         if (parsedMessage != null) {
/* 550 */           mergeFrom(parsedMessage);
/*     */         }
/*     */       } 
/* 553 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private MapField<String, Value> internalGetFields() {
/* 561 */       if (this.fields_ == null) {
/* 562 */         return MapField.emptyMapField(Struct.FieldsDefaultEntryHolder.defaultEntry);
/*     */       }
/*     */       
/* 565 */       return this.fields_;
/*     */     }
/*     */     
/*     */     private MapField<String, Value> internalGetMutableFields() {
/* 569 */       onChanged();
/* 570 */       if (this.fields_ == null) {
/* 571 */         this.fields_ = MapField.newMapField(Struct.FieldsDefaultEntryHolder.defaultEntry);
/*     */       }
/*     */       
/* 574 */       if (!this.fields_.isMutable()) {
/* 575 */         this.fields_ = this.fields_.copy();
/*     */       }
/* 577 */       return this.fields_;
/*     */     }
/*     */     
/*     */     public int getFieldsCount() {
/* 581 */       return internalGetFields().getMap().size();
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
/*     */     public boolean containsFields(String key) {
/* 593 */       if (key == null) throw new NullPointerException(); 
/* 594 */       return internalGetFields().getMap().containsKey(key);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Map<String, Value> getFields() {
/* 601 */       return getFieldsMap();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Map<String, Value> getFieldsMap() {
/* 612 */       return internalGetFields().getMap();
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
/*     */     public Value getFieldsOrDefault(String key, Value defaultValue) {
/* 625 */       if (key == null) throw new NullPointerException();
/*     */       
/* 627 */       Map<String, Value> map = internalGetFields().getMap();
/* 628 */       return map.containsKey(key) ? map.get(key) : defaultValue;
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
/*     */     public Value getFieldsOrThrow(String key) {
/* 640 */       if (key == null) throw new NullPointerException();
/*     */       
/* 642 */       Map<String, Value> map = internalGetFields().getMap();
/* 643 */       if (!map.containsKey(key)) {
/* 644 */         throw new IllegalArgumentException();
/*     */       }
/* 646 */       return map.get(key);
/*     */     }
/*     */     
/*     */     public Builder clearFields() {
/* 650 */       internalGetMutableFields().getMutableMap()
/* 651 */         .clear();
/* 652 */       return this;
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
/*     */     public Builder removeFields(String key) {
/* 664 */       if (key == null) throw new NullPointerException(); 
/* 665 */       internalGetMutableFields().getMutableMap()
/* 666 */         .remove(key);
/* 667 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Map<String, Value> getMutableFields() {
/* 675 */       return internalGetMutableFields().getMutableMap();
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
/*     */     public Builder putFields(String key, Value value) {
/* 687 */       if (key == null) throw new NullPointerException(); 
/* 688 */       if (value == null) throw new NullPointerException(); 
/* 689 */       internalGetMutableFields().getMutableMap()
/* 690 */         .put(key, value);
/* 691 */       return this;
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
/*     */     public Builder putAllFields(Map<String, Value> values) {
/* 703 */       internalGetMutableFields().getMutableMap()
/* 704 */         .putAll(values);
/* 705 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 710 */       return super.setUnknownFields(unknownFields);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 716 */       return super.mergeUnknownFields(unknownFields);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 726 */   private static final Struct DEFAULT_INSTANCE = new Struct();
/*     */ 
/*     */   
/*     */   public static Struct getDefaultInstance() {
/* 730 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ 
/*     */   
/* 734 */   private static final Parser<Struct> PARSER = new AbstractParser<Struct>()
/*     */     {
/*     */ 
/*     */       
/*     */       public Struct parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */       {
/* 740 */         return new Struct(input, extensionRegistry);
/*     */       }
/*     */     };
/*     */   
/*     */   public static Parser<Struct> parser() {
/* 745 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<Struct> getParserForType() {
/* 750 */     return PARSER;
/*     */   }
/*     */ 
/*     */   
/*     */   public Struct getDefaultInstanceForType() {
/* 755 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Struct.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */