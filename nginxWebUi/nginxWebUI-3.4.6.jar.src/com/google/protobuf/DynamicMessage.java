/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class DynamicMessage
/*     */   extends AbstractMessage
/*     */ {
/*     */   private final Descriptors.Descriptor type;
/*     */   private final FieldSet<Descriptors.FieldDescriptor> fields;
/*     */   private final Descriptors.FieldDescriptor[] oneofCases;
/*     */   private final UnknownFieldSet unknownFields;
/*  56 */   private int memoizedSize = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   DynamicMessage(Descriptors.Descriptor type, FieldSet<Descriptors.FieldDescriptor> fields, Descriptors.FieldDescriptor[] oneofCases, UnknownFieldSet unknownFields) {
/*  71 */     this.type = type;
/*  72 */     this.fields = fields;
/*  73 */     this.oneofCases = oneofCases;
/*  74 */     this.unknownFields = unknownFields;
/*     */   }
/*     */ 
/*     */   
/*     */   public static DynamicMessage getDefaultInstance(Descriptors.Descriptor type) {
/*  79 */     int oneofDeclCount = type.toProto().getOneofDeclCount();
/*  80 */     Descriptors.FieldDescriptor[] oneofCases = new Descriptors.FieldDescriptor[oneofDeclCount];
/*  81 */     return new DynamicMessage(type, 
/*     */         
/*  83 */         FieldSet.emptySet(), oneofCases, 
/*     */         
/*  85 */         UnknownFieldSet.getDefaultInstance());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DynamicMessage parseFrom(Descriptors.Descriptor type, CodedInputStream input) throws IOException {
/*  92 */     return newBuilder(type).mergeFrom(input).buildParsed();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DynamicMessage parseFrom(Descriptors.Descriptor type, CodedInputStream input, ExtensionRegistry extensionRegistry) throws IOException {
/*  99 */     return newBuilder(type).mergeFrom(input, extensionRegistry).buildParsed();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static DynamicMessage parseFrom(Descriptors.Descriptor type, ByteString data) throws InvalidProtocolBufferException {
/* 105 */     return newBuilder(type).mergeFrom(data).buildParsed();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DynamicMessage parseFrom(Descriptors.Descriptor type, ByteString data, ExtensionRegistry extensionRegistry) throws InvalidProtocolBufferException {
/* 112 */     return newBuilder(type).mergeFrom(data, extensionRegistry).buildParsed();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static DynamicMessage parseFrom(Descriptors.Descriptor type, byte[] data) throws InvalidProtocolBufferException {
/* 118 */     return newBuilder(type).mergeFrom(data).buildParsed();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DynamicMessage parseFrom(Descriptors.Descriptor type, byte[] data, ExtensionRegistry extensionRegistry) throws InvalidProtocolBufferException {
/* 125 */     return newBuilder(type).mergeFrom(data, extensionRegistry).buildParsed();
/*     */   }
/*     */ 
/*     */   
/*     */   public static DynamicMessage parseFrom(Descriptors.Descriptor type, InputStream input) throws IOException {
/* 130 */     return newBuilder(type).mergeFrom(input).buildParsed();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static DynamicMessage parseFrom(Descriptors.Descriptor type, InputStream input, ExtensionRegistry extensionRegistry) throws IOException {
/* 136 */     return newBuilder(type).mergeFrom(input, extensionRegistry).buildParsed();
/*     */   }
/*     */ 
/*     */   
/*     */   public static Builder newBuilder(Descriptors.Descriptor type) {
/* 141 */     return new Builder(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Builder newBuilder(Message prototype) {
/* 149 */     return (new Builder(prototype.getDescriptorForType())).mergeFrom(prototype);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Descriptors.Descriptor getDescriptorForType() {
/* 157 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public DynamicMessage getDefaultInstanceForType() {
/* 162 */     return getDefaultInstance(this.type);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
/* 167 */     return this.fields.getAllFields();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
/* 172 */     verifyOneofContainingType(oneof);
/* 173 */     Descriptors.FieldDescriptor field = this.oneofCases[oneof.getIndex()];
/* 174 */     if (field == null) {
/* 175 */       return false;
/*     */     }
/* 177 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
/* 182 */     verifyOneofContainingType(oneof);
/* 183 */     return this.oneofCases[oneof.getIndex()];
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasField(Descriptors.FieldDescriptor field) {
/* 188 */     verifyContainingType(field);
/* 189 */     return this.fields.hasField(field);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getField(Descriptors.FieldDescriptor field) {
/* 194 */     verifyContainingType(field);
/* 195 */     Object<?> result = (Object<?>)this.fields.getField(field);
/* 196 */     if (result == null) {
/* 197 */       if (field.isRepeated()) {
/* 198 */         result = Collections.emptyList();
/* 199 */       } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 200 */         result = (Object<?>)getDefaultInstance(field.getMessageType());
/*     */       } else {
/* 202 */         result = (Object<?>)field.getDefaultValue();
/*     */       } 
/*     */     }
/* 205 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
/* 210 */     verifyContainingType(field);
/* 211 */     return this.fields.getRepeatedFieldCount(field);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
/* 216 */     verifyContainingType(field);
/* 217 */     return this.fields.getRepeatedField(field, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public UnknownFieldSet getUnknownFields() {
/* 222 */     return this.unknownFields;
/*     */   }
/*     */ 
/*     */   
/*     */   static boolean isInitialized(Descriptors.Descriptor type, FieldSet<Descriptors.FieldDescriptor> fields) {
/* 227 */     for (Descriptors.FieldDescriptor field : type.getFields()) {
/* 228 */       if (field.isRequired() && 
/* 229 */         !fields.hasField(field)) {
/* 230 */         return false;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 236 */     return fields.isInitialized();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInitialized() {
/* 241 */     return isInitialized(this.type, this.fields);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(CodedOutputStream output) throws IOException {
/* 246 */     if (this.type.getOptions().getMessageSetWireFormat()) {
/* 247 */       this.fields.writeMessageSetTo(output);
/* 248 */       this.unknownFields.writeAsMessageSetTo(output);
/*     */     } else {
/* 250 */       this.fields.writeTo(output);
/* 251 */       this.unknownFields.writeTo(output);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/* 257 */     int size = this.memoizedSize;
/* 258 */     if (size != -1) return size;
/*     */     
/* 260 */     if (this.type.getOptions().getMessageSetWireFormat()) {
/* 261 */       size = this.fields.getMessageSetSerializedSize();
/* 262 */       size += this.unknownFields.getSerializedSizeAsMessageSet();
/*     */     } else {
/* 264 */       size = this.fields.getSerializedSize();
/* 265 */       size += this.unknownFields.getSerializedSize();
/*     */     } 
/*     */     
/* 268 */     this.memoizedSize = size;
/* 269 */     return size;
/*     */   }
/*     */ 
/*     */   
/*     */   public Builder newBuilderForType() {
/* 274 */     return new Builder(this.type);
/*     */   }
/*     */ 
/*     */   
/*     */   public Builder toBuilder() {
/* 279 */     return newBuilderForType().mergeFrom(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<DynamicMessage> getParserForType() {
/* 284 */     return new AbstractParser<DynamicMessage>()
/*     */       {
/*     */         
/*     */         public DynamicMessage parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */         {
/* 289 */           DynamicMessage.Builder builder = DynamicMessage.newBuilder(DynamicMessage.this.type);
/*     */           try {
/* 291 */             builder.mergeFrom(input, extensionRegistry);
/* 292 */           } catch (InvalidProtocolBufferException e) {
/* 293 */             throw e.setUnfinishedMessage(builder.buildPartial());
/* 294 */           } catch (IOException e) {
/* 295 */             throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(builder.buildPartial());
/*     */           } 
/* 297 */           return builder.buildPartial();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   private void verifyContainingType(Descriptors.FieldDescriptor field) {
/* 304 */     if (field.getContainingType() != this.type) {
/* 305 */       throw new IllegalArgumentException("FieldDescriptor does not match message type.");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void verifyOneofContainingType(Descriptors.OneofDescriptor oneof) {
/* 311 */     if (oneof.getContainingType() != this.type) {
/* 312 */       throw new IllegalArgumentException("OneofDescriptor does not match message type.");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class Builder
/*     */     extends AbstractMessage.Builder<Builder>
/*     */   {
/*     */     private final Descriptors.Descriptor type;
/*     */     
/*     */     private FieldSet<Descriptors.FieldDescriptor> fields;
/*     */     private final Descriptors.FieldDescriptor[] oneofCases;
/*     */     private UnknownFieldSet unknownFields;
/*     */     
/*     */     private Builder(Descriptors.Descriptor type) {
/* 327 */       this.type = type;
/* 328 */       this.fields = FieldSet.newFieldSet();
/* 329 */       this.unknownFields = UnknownFieldSet.getDefaultInstance();
/* 330 */       this.oneofCases = new Descriptors.FieldDescriptor[type.toProto().getOneofDeclCount()];
/*     */       
/* 332 */       if (type.getOptions().getMapEntry()) {
/* 333 */         populateMapEntry();
/*     */       }
/*     */     }
/*     */     
/*     */     private void populateMapEntry() {
/* 338 */       for (Descriptors.FieldDescriptor field : this.type.getFields()) {
/* 339 */         if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 340 */           this.fields.setField(field, DynamicMessage.getDefaultInstance(field.getMessageType())); continue;
/*     */         } 
/* 342 */         this.fields.setField(field, field.getDefaultValue());
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder clear() {
/* 352 */       if (this.fields.isImmutable()) {
/* 353 */         this.fields = FieldSet.newFieldSet();
/*     */       } else {
/* 355 */         this.fields.clear();
/*     */       } 
/*     */       
/* 358 */       if (this.type.getOptions().getMapEntry()) {
/* 359 */         populateMapEntry();
/*     */       }
/* 361 */       this.unknownFields = UnknownFieldSet.getDefaultInstance();
/* 362 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder mergeFrom(Message other) {
/* 367 */       if (other instanceof DynamicMessage) {
/*     */         
/* 369 */         DynamicMessage otherDynamicMessage = (DynamicMessage)other;
/* 370 */         if (otherDynamicMessage.type != this.type) {
/* 371 */           throw new IllegalArgumentException("mergeFrom(Message) can only merge messages of the same type.");
/*     */         }
/*     */         
/* 374 */         ensureIsMutable();
/* 375 */         this.fields.mergeFrom(otherDynamicMessage.fields);
/* 376 */         mergeUnknownFields(otherDynamicMessage.unknownFields);
/* 377 */         for (int i = 0; i < this.oneofCases.length; i++) {
/* 378 */           if (this.oneofCases[i] == null) {
/* 379 */             this.oneofCases[i] = otherDynamicMessage.oneofCases[i];
/*     */           }
/* 381 */           else if (otherDynamicMessage.oneofCases[i] != null && this.oneofCases[i] != otherDynamicMessage
/* 382 */             .oneofCases[i]) {
/* 383 */             this.fields.clearField(this.oneofCases[i]);
/* 384 */             this.oneofCases[i] = otherDynamicMessage.oneofCases[i];
/*     */           } 
/*     */         } 
/*     */         
/* 388 */         return this;
/*     */       } 
/* 390 */       return super.mergeFrom(other);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public DynamicMessage build() {
/* 396 */       if (!isInitialized()) {
/* 397 */         throw newUninitializedMessageException(new DynamicMessage(this.type, this.fields, 
/*     */ 
/*     */ 
/*     */               
/* 401 */               (Descriptors.FieldDescriptor[])Arrays.copyOf(this.oneofCases, this.oneofCases.length), this.unknownFields));
/*     */       }
/*     */       
/* 404 */       return buildPartial();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private DynamicMessage buildParsed() throws InvalidProtocolBufferException {
/* 412 */       if (!isInitialized()) {
/* 413 */         throw newUninitializedMessageException(new DynamicMessage(this.type, this.fields, 
/*     */ 
/*     */ 
/*     */               
/* 417 */               (Descriptors.FieldDescriptor[])Arrays.copyOf(this.oneofCases, this.oneofCases.length), this.unknownFields))
/*     */           
/* 419 */           .asInvalidProtocolBufferException();
/*     */       }
/* 421 */       return buildPartial();
/*     */     }
/*     */ 
/*     */     
/*     */     public DynamicMessage buildPartial() {
/* 426 */       this.fields.makeImmutable();
/*     */ 
/*     */       
/* 429 */       DynamicMessage result = new DynamicMessage(this.type, this.fields, Arrays.<Descriptors.FieldDescriptor>copyOf(this.oneofCases, this.oneofCases.length), this.unknownFields);
/* 430 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clone() {
/* 435 */       Builder result = new Builder(this.type);
/* 436 */       result.fields.mergeFrom(this.fields);
/* 437 */       result.mergeUnknownFields(this.unknownFields);
/* 438 */       System.arraycopy(this.oneofCases, 0, result.oneofCases, 0, this.oneofCases.length);
/* 439 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isInitialized() {
/* 444 */       return DynamicMessage.isInitialized(this.type, this.fields);
/*     */     }
/*     */ 
/*     */     
/*     */     public Descriptors.Descriptor getDescriptorForType() {
/* 449 */       return this.type;
/*     */     }
/*     */ 
/*     */     
/*     */     public DynamicMessage getDefaultInstanceForType() {
/* 454 */       return DynamicMessage.getDefaultInstance(this.type);
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
/* 459 */       return this.fields.getAllFields();
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder newBuilderForField(Descriptors.FieldDescriptor field) {
/* 464 */       verifyContainingType(field);
/*     */       
/* 466 */       if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 467 */         throw new IllegalArgumentException("newBuilderForField is only valid for fields with message type.");
/*     */       }
/*     */ 
/*     */       
/* 471 */       return new Builder(field.getMessageType());
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
/* 476 */       verifyOneofContainingType(oneof);
/* 477 */       Descriptors.FieldDescriptor field = this.oneofCases[oneof.getIndex()];
/* 478 */       if (field == null) {
/* 479 */         return false;
/*     */       }
/* 481 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
/* 486 */       verifyOneofContainingType(oneof);
/* 487 */       return this.oneofCases[oneof.getIndex()];
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 492 */       verifyOneofContainingType(oneof);
/* 493 */       Descriptors.FieldDescriptor field = this.oneofCases[oneof.getIndex()];
/* 494 */       if (field != null) {
/* 495 */         clearField(field);
/*     */       }
/* 497 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasField(Descriptors.FieldDescriptor field) {
/* 502 */       verifyContainingType(field);
/* 503 */       return this.fields.hasField(field);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getField(Descriptors.FieldDescriptor field) {
/* 508 */       verifyContainingType(field);
/* 509 */       Object<?> result = (Object<?>)this.fields.getField(field);
/* 510 */       if (result == null) {
/* 511 */         if (field.isRepeated()) {
/* 512 */           result = Collections.emptyList();
/* 513 */         } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 514 */           result = (Object<?>)DynamicMessage.getDefaultInstance(field.getMessageType());
/*     */         } else {
/* 516 */           result = (Object<?>)field.getDefaultValue();
/*     */         } 
/*     */       }
/* 519 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 524 */       verifyContainingType(field);
/* 525 */       ensureIsMutable();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 531 */       if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
/* 532 */         ensureEnumValueDescriptor(field, value);
/*     */       }
/* 534 */       Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
/* 535 */       if (oneofDescriptor != null) {
/* 536 */         int index = oneofDescriptor.getIndex();
/* 537 */         Descriptors.FieldDescriptor oldField = this.oneofCases[index];
/* 538 */         if (oldField != null && oldField != field) {
/* 539 */           this.fields.clearField(oldField);
/*     */         }
/* 541 */         this.oneofCases[index] = field;
/* 542 */       } else if (field.getFile().getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO3 && 
/* 543 */         !field.isRepeated() && field
/* 544 */         .getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE && value
/* 545 */         .equals(field.getDefaultValue())) {
/*     */         
/* 547 */         this.fields.clearField(field);
/* 548 */         return this;
/*     */       } 
/*     */       
/* 551 */       this.fields.setField(field, value);
/* 552 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder clearField(Descriptors.FieldDescriptor field) {
/* 557 */       verifyContainingType(field);
/* 558 */       ensureIsMutable();
/* 559 */       Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
/* 560 */       if (oneofDescriptor != null) {
/* 561 */         int index = oneofDescriptor.getIndex();
/* 562 */         if (this.oneofCases[index] == field) {
/* 563 */           this.oneofCases[index] = null;
/*     */         }
/*     */       } 
/* 566 */       this.fields.clearField(field);
/* 567 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
/* 572 */       verifyContainingType(field);
/* 573 */       return this.fields.getRepeatedFieldCount(field);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
/* 578 */       verifyContainingType(field);
/* 579 */       return this.fields.getRepeatedField(field, index);
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 584 */       verifyContainingType(field);
/* 585 */       ensureIsMutable();
/* 586 */       this.fields.setRepeatedField(field, index, value);
/* 587 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 592 */       verifyContainingType(field);
/* 593 */       ensureIsMutable();
/* 594 */       this.fields.addRepeatedField(field, value);
/* 595 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public UnknownFieldSet getUnknownFields() {
/* 600 */       return this.unknownFields;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 605 */       this.unknownFields = unknownFields;
/* 606 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 611 */       this
/* 612 */         .unknownFields = UnknownFieldSet.newBuilder(this.unknownFields).mergeFrom(unknownFields).build();
/* 613 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     private void verifyContainingType(Descriptors.FieldDescriptor field) {
/* 618 */       if (field.getContainingType() != this.type) {
/* 619 */         throw new IllegalArgumentException("FieldDescriptor does not match message type.");
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     private void verifyOneofContainingType(Descriptors.OneofDescriptor oneof) {
/* 625 */       if (oneof.getContainingType() != this.type) {
/* 626 */         throw new IllegalArgumentException("OneofDescriptor does not match message type.");
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     private void ensureSingularEnumValueDescriptor(Descriptors.FieldDescriptor field, Object value) {
/* 632 */       Internal.checkNotNull(value);
/* 633 */       if (!(value instanceof Descriptors.EnumValueDescriptor)) {
/* 634 */         throw new IllegalArgumentException("DynamicMessage should use EnumValueDescriptor to set Enum Value.");
/*     */       }
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
/*     */     private void ensureEnumValueDescriptor(Descriptors.FieldDescriptor field, Object value) {
/* 650 */       if (field.isRepeated()) {
/* 651 */         for (Object item : value) {
/* 652 */           ensureSingularEnumValueDescriptor(field, item);
/*     */         }
/*     */       } else {
/* 655 */         ensureSingularEnumValueDescriptor(field, value);
/*     */       } 
/*     */     }
/*     */     
/*     */     private void ensureIsMutable() {
/* 660 */       if (this.fields.isImmutable()) {
/* 661 */         this.fields = this.fields.clone();
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
/* 668 */       throw new UnsupportedOperationException("getFieldBuilder() called on a dynamic message type.");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
/* 675 */       throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a dynamic message type.");
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\DynamicMessage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */