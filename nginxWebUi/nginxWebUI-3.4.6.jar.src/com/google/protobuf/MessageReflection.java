/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class MessageReflection
/*     */ {
/*     */   static void writeMessageTo(Message message, Map<Descriptors.FieldDescriptor, Object> fields, CodedOutputStream output, boolean alwaysWriteRequiredFields) throws IOException {
/*  54 */     boolean isMessageSet = message.getDescriptorForType().getOptions().getMessageSetWireFormat();
/*  55 */     if (alwaysWriteRequiredFields) {
/*  56 */       fields = new TreeMap<>(fields);
/*  57 */       for (Descriptors.FieldDescriptor field : message.getDescriptorForType().getFields()) {
/*  58 */         if (field.isRequired() && !fields.containsKey(field)) {
/*  59 */           fields.put(field, message.getField(field));
/*     */         }
/*     */       } 
/*     */     } 
/*  63 */     for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : fields.entrySet()) {
/*  64 */       Descriptors.FieldDescriptor field = entry.getKey();
/*  65 */       Object value = entry.getValue();
/*  66 */       if (isMessageSet && field
/*  67 */         .isExtension() && field
/*  68 */         .getType() == Descriptors.FieldDescriptor.Type.MESSAGE && 
/*  69 */         !field.isRepeated()) {
/*  70 */         output.writeMessageSetExtension(field.getNumber(), (Message)value); continue;
/*     */       } 
/*  72 */       FieldSet.writeField(field, value, output);
/*     */     } 
/*     */ 
/*     */     
/*  76 */     UnknownFieldSet unknownFields = message.getUnknownFields();
/*  77 */     if (isMessageSet) {
/*  78 */       unknownFields.writeAsMessageSetTo(output);
/*     */     } else {
/*  80 */       unknownFields.writeTo(output);
/*     */     } 
/*     */   }
/*     */   
/*     */   static int getSerializedSize(Message message, Map<Descriptors.FieldDescriptor, Object> fields) {
/*  85 */     int size = 0;
/*     */     
/*  87 */     boolean isMessageSet = message.getDescriptorForType().getOptions().getMessageSetWireFormat();
/*     */     
/*  89 */     for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : fields.entrySet()) {
/*  90 */       Descriptors.FieldDescriptor field = entry.getKey();
/*  91 */       Object value = entry.getValue();
/*  92 */       if (isMessageSet && field
/*  93 */         .isExtension() && field
/*  94 */         .getType() == Descriptors.FieldDescriptor.Type.MESSAGE && 
/*  95 */         !field.isRepeated()) {
/*  96 */         size += 
/*  97 */           CodedOutputStream.computeMessageSetExtensionSize(field.getNumber(), (Message)value); continue;
/*     */       } 
/*  99 */       size += FieldSet.computeFieldSize(field, value);
/*     */     } 
/*     */ 
/*     */     
/* 103 */     UnknownFieldSet unknownFields = message.getUnknownFields();
/* 104 */     if (isMessageSet) {
/* 105 */       size += unknownFields.getSerializedSizeAsMessageSet();
/*     */     } else {
/* 107 */       size += unknownFields.getSerializedSize();
/*     */     } 
/* 109 */     return size;
/*     */   }
/*     */   
/*     */   static String delimitWithCommas(List<String> parts) {
/* 113 */     StringBuilder result = new StringBuilder();
/* 114 */     for (String part : parts) {
/* 115 */       if (result.length() > 0) {
/* 116 */         result.append(", ");
/*     */       }
/* 118 */       result.append(part);
/*     */     } 
/* 120 */     return result.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean isInitialized(MessageOrBuilder message) {
/* 126 */     for (Descriptors.FieldDescriptor field : message.getDescriptorForType().getFields()) {
/* 127 */       if (field.isRequired() && 
/* 128 */         !message.hasField(field)) {
/* 129 */         return false;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 136 */     for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : message.getAllFields().entrySet()) {
/* 137 */       Descriptors.FieldDescriptor field = entry.getKey();
/* 138 */       if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 139 */         if (field.isRepeated()) {
/* 140 */           for (Message element : entry.getValue()) {
/* 141 */             if (!element.isInitialized())
/* 142 */               return false; 
/*     */           } 
/*     */           continue;
/*     */         } 
/* 146 */         if (!((Message)entry.getValue()).isInitialized()) {
/* 147 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 153 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String subMessagePrefix(String prefix, Descriptors.FieldDescriptor field, int index) {
/* 158 */     StringBuilder result = new StringBuilder(prefix);
/* 159 */     if (field.isExtension()) {
/* 160 */       result.append('(').append(field.getFullName()).append(')');
/*     */     } else {
/* 162 */       result.append(field.getName());
/*     */     } 
/* 164 */     if (index != -1) {
/* 165 */       result.append('[').append(index).append(']');
/*     */     }
/* 167 */     result.append('.');
/* 168 */     return result.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   private static void findMissingFields(MessageOrBuilder message, String prefix, List<String> results) {
/* 173 */     for (Descriptors.FieldDescriptor field : message.getDescriptorForType().getFields()) {
/* 174 */       if (field.isRequired() && !message.hasField(field)) {
/* 175 */         results.add(prefix + field.getName());
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 180 */     for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : message.getAllFields().entrySet()) {
/* 181 */       Descriptors.FieldDescriptor field = entry.getKey();
/* 182 */       Object value = entry.getValue();
/*     */       
/* 184 */       if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 185 */         if (field.isRepeated()) {
/* 186 */           int i = 0;
/* 187 */           for (Object element : value)
/* 188 */             findMissingFields((MessageOrBuilder)element, 
/* 189 */                 subMessagePrefix(prefix, field, i++), results); 
/*     */           continue;
/*     */         } 
/* 192 */         if (message.hasField(field)) {
/* 193 */           findMissingFields((MessageOrBuilder)value, 
/* 194 */               subMessagePrefix(prefix, field, -1), results);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static List<String> findMissingFields(MessageOrBuilder message) {
/* 206 */     List<String> results = new ArrayList<>();
/* 207 */     findMissingFields(message, "", results);
/* 208 */     return results;
/*     */   }
/*     */   
/*     */   public enum ContainerType
/*     */   {
/* 213 */     MESSAGE,
/* 214 */     EXTENSION_SET; } static interface MergeTarget { Descriptors.Descriptor getDescriptorForType(); ContainerType getContainerType(); ExtensionRegistry.ExtensionInfo findExtensionByName(ExtensionRegistry param1ExtensionRegistry, String param1String); ExtensionRegistry.ExtensionInfo findExtensionByNumber(ExtensionRegistry param1ExtensionRegistry, Descriptors.Descriptor param1Descriptor, int param1Int); Object getField(Descriptors.FieldDescriptor param1FieldDescriptor); boolean hasField(Descriptors.FieldDescriptor param1FieldDescriptor); MergeTarget setField(Descriptors.FieldDescriptor param1FieldDescriptor, Object param1Object); MergeTarget clearField(Descriptors.FieldDescriptor param1FieldDescriptor); MergeTarget setRepeatedField(Descriptors.FieldDescriptor param1FieldDescriptor, int param1Int, Object param1Object); MergeTarget addRepeatedField(Descriptors.FieldDescriptor param1FieldDescriptor, Object param1Object); boolean hasOneof(Descriptors.OneofDescriptor param1OneofDescriptor); MergeTarget clearOneof(Descriptors.OneofDescriptor param1OneofDescriptor); Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor param1OneofDescriptor); Object parseGroup(CodedInputStream param1CodedInputStream, ExtensionRegistryLite param1ExtensionRegistryLite, Descriptors.FieldDescriptor param1FieldDescriptor, Message param1Message) throws IOException; Object parseMessage(CodedInputStream param1CodedInputStream, ExtensionRegistryLite param1ExtensionRegistryLite, Descriptors.FieldDescriptor param1FieldDescriptor, Message param1Message) throws IOException; Object parseMessageFromBytes(ByteString param1ByteString, ExtensionRegistryLite param1ExtensionRegistryLite, Descriptors.FieldDescriptor param1FieldDescriptor, Message param1Message) throws IOException; WireFormat.Utf8Validation getUtf8Validation(Descriptors.FieldDescriptor param1FieldDescriptor); MergeTarget newMergeTargetForField(Descriptors.FieldDescriptor param1FieldDescriptor, Message param1Message); MergeTarget newEmptyTargetForField(Descriptors.FieldDescriptor param1FieldDescriptor, Message param1Message); Object finish(); public enum ContainerType { MESSAGE, EXTENSION_SET; }
/*     */      }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class BuilderAdapter
/*     */     implements MergeTarget
/*     */   {
/*     */     private final Message.Builder builder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Descriptors.Descriptor getDescriptorForType() {
/* 355 */       return this.builder.getDescriptorForType();
/*     */     }
/*     */     
/*     */     public BuilderAdapter(Message.Builder builder) {
/* 359 */       this.builder = builder;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getField(Descriptors.FieldDescriptor field) {
/* 364 */       return this.builder.getField(field);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasField(Descriptors.FieldDescriptor field) {
/* 369 */       return this.builder.hasField(field);
/*     */     }
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget setField(Descriptors.FieldDescriptor field, Object value) {
/* 374 */       this.builder.setField(field, value);
/* 375 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget clearField(Descriptors.FieldDescriptor field) {
/* 380 */       this.builder.clearField(field);
/* 381 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 387 */       this.builder.setRepeatedField(field, index, value);
/* 388 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 393 */       this.builder.addRepeatedField(field, value);
/* 394 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
/* 399 */       return this.builder.hasOneof(oneof);
/*     */     }
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget clearOneof(Descriptors.OneofDescriptor oneof) {
/* 404 */       this.builder.clearOneof(oneof);
/* 405 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
/* 410 */       return this.builder.getOneofFieldDescriptor(oneof);
/*     */     }
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget.ContainerType getContainerType() {
/* 415 */       return MessageReflection.MergeTarget.ContainerType.MESSAGE;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ExtensionRegistry.ExtensionInfo findExtensionByName(ExtensionRegistry registry, String name) {
/* 421 */       return registry.findImmutableExtensionByName(name);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ExtensionRegistry.ExtensionInfo findExtensionByNumber(ExtensionRegistry registry, Descriptors.Descriptor containingType, int fieldNumber) {
/* 427 */       return registry.findImmutableExtensionByNumber(containingType, fieldNumber);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object parseGroup(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
/*     */       Message.Builder subBuilder;
/* 439 */       if (defaultInstance != null) {
/* 440 */         subBuilder = defaultInstance.newBuilderForType();
/*     */       } else {
/* 442 */         subBuilder = this.builder.newBuilderForField(field);
/*     */       } 
/* 444 */       if (!field.isRepeated()) {
/* 445 */         Message originalMessage = (Message)getField(field);
/* 446 */         if (originalMessage != null) {
/* 447 */           subBuilder.mergeFrom(originalMessage);
/*     */         }
/*     */       } 
/* 450 */       input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
/* 451 */       return subBuilder.buildPartial();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object parseMessage(CodedInputStream input, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
/*     */       Message.Builder subBuilder;
/* 463 */       if (defaultInstance != null) {
/* 464 */         subBuilder = defaultInstance.newBuilderForType();
/*     */       } else {
/* 466 */         subBuilder = this.builder.newBuilderForField(field);
/*     */       } 
/* 468 */       if (!field.isRepeated()) {
/* 469 */         Message originalMessage = (Message)getField(field);
/* 470 */         if (originalMessage != null) {
/* 471 */           subBuilder.mergeFrom(originalMessage);
/*     */         }
/*     */       } 
/* 474 */       input.readMessage(subBuilder, extensionRegistry);
/* 475 */       return subBuilder.buildPartial();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object parseMessageFromBytes(ByteString bytes, ExtensionRegistryLite extensionRegistry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
/*     */       Message.Builder subBuilder;
/* 487 */       if (defaultInstance != null) {
/* 488 */         subBuilder = defaultInstance.newBuilderForType();
/*     */       } else {
/* 490 */         subBuilder = this.builder.newBuilderForField(field);
/*     */       } 
/* 492 */       if (!field.isRepeated()) {
/* 493 */         Message originalMessage = (Message)getField(field);
/* 494 */         if (originalMessage != null) {
/* 495 */           subBuilder.mergeFrom(originalMessage);
/*     */         }
/*     */       } 
/* 498 */       subBuilder.mergeFrom(bytes, extensionRegistry);
/* 499 */       return subBuilder.buildPartial();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget newMergeTargetForField(Descriptors.FieldDescriptor field, Message defaultInstance) {
/*     */       Message.Builder subBuilder;
/* 506 */       if (defaultInstance != null) {
/* 507 */         subBuilder = defaultInstance.newBuilderForType();
/*     */       } else {
/* 509 */         subBuilder = this.builder.newBuilderForField(field);
/*     */       } 
/* 511 */       if (!field.isRepeated()) {
/* 512 */         Message originalMessage = (Message)getField(field);
/* 513 */         if (originalMessage != null) {
/* 514 */           subBuilder.mergeFrom(originalMessage);
/*     */         }
/*     */       } 
/* 517 */       return new BuilderAdapter(subBuilder);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget newEmptyTargetForField(Descriptors.FieldDescriptor field, Message defaultInstance) {
/*     */       Message.Builder subBuilder;
/* 524 */       if (defaultInstance != null) {
/* 525 */         subBuilder = defaultInstance.newBuilderForType();
/*     */       } else {
/* 527 */         subBuilder = this.builder.newBuilderForField(field);
/*     */       } 
/* 529 */       return new BuilderAdapter(subBuilder);
/*     */     }
/*     */ 
/*     */     
/*     */     public WireFormat.Utf8Validation getUtf8Validation(Descriptors.FieldDescriptor descriptor) {
/* 534 */       if (descriptor.needsUtf8Check()) {
/* 535 */         return WireFormat.Utf8Validation.STRICT;
/*     */       }
/*     */       
/* 538 */       if (!descriptor.isRepeated() && this.builder instanceof GeneratedMessage.Builder) {
/* 539 */         return WireFormat.Utf8Validation.LAZY;
/*     */       }
/* 541 */       return WireFormat.Utf8Validation.LOOSE;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object finish() {
/* 546 */       return this.builder.buildPartial();
/*     */     }
/*     */   }
/*     */   
/*     */   static class ExtensionAdapter
/*     */     implements MergeTarget
/*     */   {
/*     */     private final FieldSet<Descriptors.FieldDescriptor> extensions;
/*     */     
/*     */     ExtensionAdapter(FieldSet<Descriptors.FieldDescriptor> extensions) {
/* 556 */       this.extensions = extensions;
/*     */     }
/*     */ 
/*     */     
/*     */     public Descriptors.Descriptor getDescriptorForType() {
/* 561 */       throw new UnsupportedOperationException("getDescriptorForType() called on FieldSet object");
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getField(Descriptors.FieldDescriptor field) {
/* 566 */       return this.extensions.getField(field);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasField(Descriptors.FieldDescriptor field) {
/* 571 */       return this.extensions.hasField(field);
/*     */     }
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget setField(Descriptors.FieldDescriptor field, Object value) {
/* 576 */       this.extensions.setField(field, value);
/* 577 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget clearField(Descriptors.FieldDescriptor field) {
/* 582 */       this.extensions.clearField(field);
/* 583 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 589 */       this.extensions.setRepeatedField(field, index, value);
/* 590 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 595 */       this.extensions.addRepeatedField(field, value);
/* 596 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
/* 601 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget clearOneof(Descriptors.OneofDescriptor oneof) {
/* 607 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
/* 612 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget.ContainerType getContainerType() {
/* 617 */       return MessageReflection.MergeTarget.ContainerType.EXTENSION_SET;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ExtensionRegistry.ExtensionInfo findExtensionByName(ExtensionRegistry registry, String name) {
/* 623 */       return registry.findImmutableExtensionByName(name);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ExtensionRegistry.ExtensionInfo findExtensionByNumber(ExtensionRegistry registry, Descriptors.Descriptor containingType, int fieldNumber) {
/* 629 */       return registry.findImmutableExtensionByNumber(containingType, fieldNumber);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object parseGroup(CodedInputStream input, ExtensionRegistryLite registry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
/* 639 */       Message.Builder subBuilder = defaultInstance.newBuilderForType();
/* 640 */       if (!field.isRepeated()) {
/* 641 */         Message originalMessage = (Message)getField(field);
/* 642 */         if (originalMessage != null) {
/* 643 */           subBuilder.mergeFrom(originalMessage);
/*     */         }
/*     */       } 
/* 646 */       input.readGroup(field.getNumber(), subBuilder, registry);
/* 647 */       return subBuilder.buildPartial();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object parseMessage(CodedInputStream input, ExtensionRegistryLite registry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
/* 657 */       Message.Builder subBuilder = defaultInstance.newBuilderForType();
/* 658 */       if (!field.isRepeated()) {
/* 659 */         Message originalMessage = (Message)getField(field);
/* 660 */         if (originalMessage != null) {
/* 661 */           subBuilder.mergeFrom(originalMessage);
/*     */         }
/*     */       } 
/* 664 */       input.readMessage(subBuilder, registry);
/* 665 */       return subBuilder.buildPartial();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object parseMessageFromBytes(ByteString bytes, ExtensionRegistryLite registry, Descriptors.FieldDescriptor field, Message defaultInstance) throws IOException {
/* 675 */       Message.Builder subBuilder = defaultInstance.newBuilderForType();
/* 676 */       if (!field.isRepeated()) {
/* 677 */         Message originalMessage = (Message)getField(field);
/* 678 */         if (originalMessage != null) {
/* 679 */           subBuilder.mergeFrom(originalMessage);
/*     */         }
/*     */       } 
/* 682 */       subBuilder.mergeFrom(bytes, registry);
/* 683 */       return subBuilder.buildPartial();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget newMergeTargetForField(Descriptors.FieldDescriptor descriptor, Message defaultInstance) {
/* 689 */       throw new UnsupportedOperationException("newMergeTargetForField() called on FieldSet object");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public MessageReflection.MergeTarget newEmptyTargetForField(Descriptors.FieldDescriptor descriptor, Message defaultInstance) {
/* 695 */       throw new UnsupportedOperationException("newEmptyTargetForField() called on FieldSet object");
/*     */     }
/*     */ 
/*     */     
/*     */     public WireFormat.Utf8Validation getUtf8Validation(Descriptors.FieldDescriptor descriptor) {
/* 700 */       if (descriptor.needsUtf8Check()) {
/* 701 */         return WireFormat.Utf8Validation.STRICT;
/*     */       }
/*     */       
/* 704 */       return WireFormat.Utf8Validation.LOOSE;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object finish() {
/* 709 */       throw new UnsupportedOperationException("finish() called on FieldSet object");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean mergeFieldFrom(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, Descriptors.Descriptor type, MergeTarget target, int tag) throws IOException {
/*     */     Descriptors.FieldDescriptor field;
/* 732 */     if (type.getOptions().getMessageSetWireFormat() && tag == WireFormat.MESSAGE_SET_ITEM_TAG) {
/* 733 */       mergeMessageSetExtensionFromCodedStream(input, unknownFields, extensionRegistry, type, target);
/*     */       
/* 735 */       return true;
/*     */     } 
/*     */     
/* 738 */     int wireType = WireFormat.getTagWireType(tag);
/* 739 */     int fieldNumber = WireFormat.getTagFieldNumber(tag);
/*     */ 
/*     */     
/* 742 */     Message defaultInstance = null;
/*     */     
/* 744 */     if (type.isExtensionNumber(fieldNumber)) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 750 */       if (extensionRegistry instanceof ExtensionRegistry) {
/*     */         
/* 752 */         ExtensionRegistry.ExtensionInfo extension = target.findExtensionByNumber((ExtensionRegistry)extensionRegistry, type, fieldNumber);
/* 753 */         if (extension == null) {
/* 754 */           field = null;
/*     */         } else {
/* 756 */           field = extension.descriptor;
/* 757 */           defaultInstance = extension.defaultInstance;
/* 758 */           if (defaultInstance == null && field
/* 759 */             .getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 760 */             throw new IllegalStateException("Message-typed extension lacked default instance: " + field
/* 761 */                 .getFullName());
/*     */           }
/*     */         } 
/*     */       } else {
/* 765 */         field = null;
/*     */       } 
/* 767 */     } else if (target.getContainerType() == MergeTarget.ContainerType.MESSAGE) {
/* 768 */       field = type.findFieldByNumber(fieldNumber);
/*     */     } else {
/* 770 */       field = null;
/*     */     } 
/*     */     
/* 773 */     boolean unknown = false;
/* 774 */     boolean packed = false;
/* 775 */     if (field == null) {
/* 776 */       unknown = true;
/* 777 */     } else if (wireType == 
/* 778 */       FieldSet.getWireFormatForFieldType(field.getLiteType(), false)) {
/* 779 */       packed = false;
/* 780 */     } else if (field.isPackable() && wireType == 
/*     */       
/* 782 */       FieldSet.getWireFormatForFieldType(field.getLiteType(), true)) {
/* 783 */       packed = true;
/*     */     } else {
/* 785 */       unknown = true;
/*     */     } 
/*     */     
/* 788 */     if (unknown) {
/* 789 */       if (unknownFields != null) {
/* 790 */         return unknownFields.mergeFieldFrom(tag, input);
/*     */       }
/* 792 */       return input.skipField(tag);
/*     */     } 
/*     */ 
/*     */     
/* 796 */     if (packed) {
/* 797 */       int length = input.readRawVarint32();
/* 798 */       int limit = input.pushLimit(length);
/* 799 */       if (field.getLiteType() == WireFormat.FieldType.ENUM) {
/* 800 */         while (input.getBytesUntilLimit() > 0) {
/* 801 */           int rawValue = input.readEnum();
/* 802 */           if (field.getFile().supportsUnknownEnumValue()) {
/* 803 */             target.addRepeatedField(field, field
/* 804 */                 .getEnumType().findValueByNumberCreatingIfUnknown(rawValue)); continue;
/*     */           } 
/* 806 */           Object value = field.getEnumType().findValueByNumber(rawValue);
/*     */ 
/*     */           
/* 809 */           if (value == null) {
/* 810 */             if (unknownFields != null)
/* 811 */               unknownFields.mergeVarintField(fieldNumber, rawValue); 
/*     */             continue;
/*     */           } 
/* 814 */           target.addRepeatedField(field, value);
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 819 */         while (input.getBytesUntilLimit() > 0) {
/*     */           
/* 821 */           Object value = WireFormat.readPrimitiveField(input, field
/* 822 */               .getLiteType(), target.getUtf8Validation(field));
/* 823 */           target.addRepeatedField(field, value);
/*     */         } 
/*     */       } 
/* 826 */       input.popLimit(limit);
/*     */     } else {
/*     */       Object value; int rawValue;
/* 829 */       switch (field.getType()) {
/*     */         
/*     */         case GROUP:
/* 832 */           value = target.parseGroup(input, extensionRegistry, field, defaultInstance);
/*     */           break;
/*     */ 
/*     */         
/*     */         case MESSAGE:
/* 837 */           value = target.parseMessage(input, extensionRegistry, field, defaultInstance);
/*     */           break;
/*     */         
/*     */         case ENUM:
/* 841 */           rawValue = input.readEnum();
/* 842 */           if (field.getFile().supportsUnknownEnumValue()) {
/* 843 */             value = field.getEnumType().findValueByNumberCreatingIfUnknown(rawValue); break;
/*     */           } 
/* 845 */           value = field.getEnumType().findValueByNumber(rawValue);
/*     */ 
/*     */           
/* 848 */           if (value == null) {
/* 849 */             if (unknownFields != null) {
/* 850 */               unknownFields.mergeVarintField(fieldNumber, rawValue);
/*     */             }
/* 852 */             return true;
/*     */           } 
/*     */           break;
/*     */ 
/*     */         
/*     */         default:
/* 858 */           value = WireFormat.readPrimitiveField(input, field
/* 859 */               .getLiteType(), target.getUtf8Validation(field));
/*     */           break;
/*     */       } 
/*     */       
/* 863 */       if (field.isRepeated()) {
/* 864 */         target.addRepeatedField(field, value);
/*     */       } else {
/* 866 */         target.setField(field, value);
/*     */       } 
/*     */     } 
/*     */     
/* 870 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void mergeMessageSetExtensionFromCodedStream(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, Descriptors.Descriptor type, MergeTarget target) throws IOException {
/* 898 */     int typeId = 0;
/* 899 */     ByteString rawBytes = null;
/* 900 */     ExtensionRegistry.ExtensionInfo extension = null;
/*     */ 
/*     */ 
/*     */     
/*     */     while (true) {
/* 905 */       int tag = input.readTag();
/* 906 */       if (tag == 0) {
/*     */         break;
/*     */       }
/*     */       
/* 910 */       if (tag == WireFormat.MESSAGE_SET_TYPE_ID_TAG) {
/* 911 */         typeId = input.readUInt32();
/* 912 */         if (typeId != 0)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 918 */           if (extensionRegistry instanceof ExtensionRegistry)
/*     */           {
/* 920 */             extension = target.findExtensionByNumber((ExtensionRegistry)extensionRegistry, type, typeId); } 
/*     */         }
/*     */         continue;
/*     */       } 
/* 924 */       if (tag == WireFormat.MESSAGE_SET_MESSAGE_TAG) {
/* 925 */         if (typeId != 0 && 
/* 926 */           extension != null && ExtensionRegistryLite.isEagerlyParseMessageSets()) {
/*     */ 
/*     */           
/* 929 */           eagerlyMergeMessageSetExtension(input, extension, extensionRegistry, target);
/* 930 */           rawBytes = null;
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 935 */         rawBytes = input.readBytes();
/*     */         continue;
/*     */       } 
/* 938 */       if (!input.skipField(tag)) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */     
/* 943 */     input.checkLastTagWas(WireFormat.MESSAGE_SET_ITEM_END_TAG);
/*     */ 
/*     */     
/* 946 */     if (rawBytes != null && typeId != 0) {
/* 947 */       if (extension != null) {
/* 948 */         mergeMessageSetExtensionFromBytes(rawBytes, extension, extensionRegistry, target);
/*     */       }
/* 950 */       else if (rawBytes != null && unknownFields != null) {
/* 951 */         unknownFields.mergeField(typeId, 
/* 952 */             UnknownFieldSet.Field.newBuilder().addLengthDelimited(rawBytes).build());
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void mergeMessageSetExtensionFromBytes(ByteString rawBytes, ExtensionRegistry.ExtensionInfo extension, ExtensionRegistryLite extensionRegistry, MergeTarget target) throws IOException {
/* 965 */     Descriptors.FieldDescriptor field = extension.descriptor;
/* 966 */     boolean hasOriginalValue = target.hasField(field);
/*     */     
/* 968 */     if (hasOriginalValue || ExtensionRegistryLite.isEagerlyParseMessageSets()) {
/*     */ 
/*     */       
/* 971 */       Object value = target.parseMessageFromBytes(rawBytes, extensionRegistry, field, extension.defaultInstance);
/*     */       
/* 973 */       target.setField(field, value);
/*     */     } else {
/*     */       
/* 976 */       LazyField lazyField = new LazyField(extension.defaultInstance, extensionRegistry, rawBytes);
/* 977 */       target.setField(field, lazyField);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void eagerlyMergeMessageSetExtension(CodedInputStream input, ExtensionRegistry.ExtensionInfo extension, ExtensionRegistryLite extensionRegistry, MergeTarget target) throws IOException {
/* 987 */     Descriptors.FieldDescriptor field = extension.descriptor;
/* 988 */     Object value = target.parseMessage(input, extensionRegistry, field, extension.defaultInstance);
/* 989 */     target.setField(field, value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MessageReflection.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */