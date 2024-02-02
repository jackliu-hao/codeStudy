/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
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
/*     */ final class MessageSetSchema<T>
/*     */   implements Schema<T>
/*     */ {
/*     */   private final MessageLite defaultInstance;
/*     */   private final UnknownFieldSchema<?, ?> unknownFieldSchema;
/*     */   private final boolean hasExtensions;
/*     */   private final ExtensionSchema<?> extensionSchema;
/*     */   
/*     */   private MessageSetSchema(UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MessageLite defaultInstance) {
/*  48 */     this.unknownFieldSchema = unknownFieldSchema;
/*  49 */     this.hasExtensions = extensionSchema.hasExtensions(defaultInstance);
/*  50 */     this.extensionSchema = extensionSchema;
/*  51 */     this.defaultInstance = defaultInstance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static <T> MessageSetSchema<T> newSchema(UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MessageLite defaultInstance) {
/*  58 */     return new MessageSetSchema<>(unknownFieldSchema, extensionSchema, defaultInstance);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public T newInstance() {
/*  64 */     return (T)this.defaultInstance.newBuilderForType().buildPartial();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(T message, T other) {
/*  69 */     Object messageUnknown = this.unknownFieldSchema.getFromMessage(message);
/*  70 */     Object otherUnknown = this.unknownFieldSchema.getFromMessage(other);
/*  71 */     if (!messageUnknown.equals(otherUnknown)) {
/*  72 */       return false;
/*     */     }
/*  74 */     if (this.hasExtensions) {
/*  75 */       FieldSet<?> messageExtensions = this.extensionSchema.getExtensions(message);
/*  76 */       FieldSet<?> otherExtensions = this.extensionSchema.getExtensions(other);
/*  77 */       return messageExtensions.equals(otherExtensions);
/*     */     } 
/*  79 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode(T message) {
/*  84 */     int hashCode = this.unknownFieldSchema.getFromMessage(message).hashCode();
/*  85 */     if (this.hasExtensions) {
/*  86 */       FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
/*  87 */       hashCode = hashCode * 53 + extensions.hashCode();
/*     */     } 
/*  89 */     return hashCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public void mergeFrom(T message, T other) {
/*  94 */     SchemaUtil.mergeUnknownFields(this.unknownFieldSchema, message, other);
/*  95 */     if (this.hasExtensions) {
/*  96 */       SchemaUtil.mergeExtensions(this.extensionSchema, message, other);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeTo(T message, Writer writer) throws IOException {
/* 103 */     FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
/* 104 */     Iterator<?> iterator = extensions.iterator();
/* 105 */     while (iterator.hasNext()) {
/* 106 */       Map.Entry<?, ?> extension = (Map.Entry<?, ?>)iterator.next();
/* 107 */       FieldSet.FieldDescriptorLite<?> fd = (FieldSet.FieldDescriptorLite)extension.getKey();
/* 108 */       if (fd.getLiteJavaType() != WireFormat.JavaType.MESSAGE || fd.isRepeated() || fd.isPacked()) {
/* 109 */         throw new IllegalStateException("Found invalid MessageSet item.");
/*     */       }
/* 111 */       if (extension instanceof LazyField.LazyEntry) {
/* 112 */         writer.writeMessageSetItem(fd
/* 113 */             .getNumber(), ((LazyField.LazyEntry)extension).getField().toByteString()); continue;
/*     */       } 
/* 115 */       writer.writeMessageSetItem(fd.getNumber(), extension.getValue());
/*     */     } 
/*     */     
/* 118 */     writeUnknownFieldsHelper(this.unknownFieldSchema, message, writer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private <UT, UB> void writeUnknownFieldsHelper(UnknownFieldSchema<UT, UB> unknownFieldSchema, T message, Writer writer) throws IOException {
/* 127 */     unknownFieldSchema.writeAsMessageSetTo(unknownFieldSchema.getFromMessage(message), writer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void mergeFrom(T message, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException {
/* 135 */     UnknownFieldSetLite unknownFields = ((GeneratedMessageLite)message).unknownFields;
/* 136 */     if (unknownFields == UnknownFieldSetLite.getDefaultInstance()) {
/* 137 */       unknownFields = UnknownFieldSetLite.newInstance();
/* 138 */       ((GeneratedMessageLite)message).unknownFields = unknownFields;
/*     */     } 
/*     */     
/* 141 */     FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions = ((GeneratedMessageLite.ExtendableMessage)message).ensureExtensionsAreMutable();
/* 142 */     GeneratedMessageLite.GeneratedExtension<?, ?> extension = null;
/* 143 */     while (position < limit) {
/* 144 */       position = ArrayDecoders.decodeVarint32(data, position, registers);
/* 145 */       int startTag = registers.int1;
/* 146 */       if (startTag != WireFormat.MESSAGE_SET_ITEM_TAG) {
/* 147 */         if (WireFormat.getTagWireType(startTag) == 2) {
/*     */           
/* 149 */           extension = (GeneratedMessageLite.GeneratedExtension<?, ?>)this.extensionSchema.findExtensionByNumber(registers.extensionRegistry, this.defaultInstance, 
/*     */               
/* 151 */               WireFormat.getTagFieldNumber(startTag));
/* 152 */           if (extension != null) {
/*     */             
/* 154 */             position = ArrayDecoders.decodeMessageField(
/* 155 */                 Protobuf.getInstance().schemaFor(extension
/* 156 */                   .getMessageDefaultInstance().getClass()), data, position, limit, registers);
/*     */             
/* 158 */             extensions.setField(extension.descriptor, registers.object1);
/*     */             continue;
/*     */           } 
/* 161 */           position = ArrayDecoders.decodeUnknownField(startTag, data, position, limit, unknownFields, registers);
/*     */           
/*     */           continue;
/*     */         } 
/* 165 */         position = ArrayDecoders.skipField(startTag, data, position, limit, registers);
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 170 */       int typeId = 0;
/* 171 */       ByteString rawBytes = null;
/*     */       
/* 173 */       while (position < limit) {
/* 174 */         position = ArrayDecoders.decodeVarint32(data, position, registers);
/* 175 */         int tag = registers.int1;
/* 176 */         int number = WireFormat.getTagFieldNumber(tag);
/* 177 */         int wireType = WireFormat.getTagWireType(tag);
/* 178 */         switch (number) {
/*     */           case 2:
/* 180 */             if (wireType == 0) {
/* 181 */               position = ArrayDecoders.decodeVarint32(data, position, registers);
/* 182 */               typeId = registers.int1;
/*     */ 
/*     */               
/* 185 */               extension = (GeneratedMessageLite.GeneratedExtension<?, ?>)this.extensionSchema.findExtensionByNumber(registers.extensionRegistry, this.defaultInstance, typeId);
/*     */               continue;
/*     */             } 
/*     */             break;
/*     */           case 3:
/* 190 */             if (extension != null) {
/* 191 */               position = ArrayDecoders.decodeMessageField(
/* 192 */                   Protobuf.getInstance().schemaFor(extension
/* 193 */                     .getMessageDefaultInstance().getClass()), data, position, limit, registers);
/*     */               
/* 195 */               extensions.setField(extension.descriptor, registers.object1);
/*     */               continue;
/*     */             } 
/* 198 */             if (wireType == 2) {
/* 199 */               position = ArrayDecoders.decodeBytes(data, position, registers);
/* 200 */               rawBytes = (ByteString)registers.object1;
/*     */               continue;
/*     */             } 
/*     */             break;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 208 */         if (tag == WireFormat.MESSAGE_SET_ITEM_END_TAG) {
/*     */           break;
/*     */         }
/* 211 */         position = ArrayDecoders.skipField(tag, data, position, limit, registers);
/*     */       } 
/*     */       
/* 214 */       if (rawBytes != null) {
/* 215 */         unknownFields.storeField(
/* 216 */             WireFormat.makeTag(typeId, 2), rawBytes);
/*     */       }
/*     */     } 
/* 219 */     if (position != limit) {
/* 220 */       throw InvalidProtocolBufferException.parseFailure();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void mergeFrom(T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 227 */     mergeFromHelper(this.unknownFieldSchema, this.extensionSchema, message, reader, extensionRegistry);
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
/*     */   private <UT, UB, ET extends FieldSet.FieldDescriptorLite<ET>> void mergeFromHelper(UnknownFieldSchema<UT, UB> unknownFieldSchema, ExtensionSchema<ET> extensionSchema, T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 242 */     UB unknownFields = unknownFieldSchema.getBuilderFromMessage(message);
/* 243 */     FieldSet<ET> extensions = extensionSchema.getMutableExtensions(message);
/*     */     try {
/*     */       while (true) {
/* 246 */         int number = reader.getFieldNumber();
/* 247 */         if (number == Integer.MAX_VALUE) {
/*     */           return;
/*     */         }
/* 250 */         if (parseMessageSetItemOrUnknownField(reader, extensionRegistry, extensionSchema, extensions, unknownFieldSchema, unknownFields)) {
/*     */           continue;
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*     */         break;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/*     */       return;
/*     */     } finally {
/* 263 */       unknownFieldSchema.setBuilderToMessage(message, unknownFields);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void makeImmutable(T message) {
/* 269 */     this.unknownFieldSchema.makeImmutable(message);
/* 270 */     this.extensionSchema.makeImmutable(message);
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
/*     */   private <UT, UB, ET extends FieldSet.FieldDescriptorLite<ET>> boolean parseMessageSetItemOrUnknownField(Reader reader, ExtensionRegistryLite extensionRegistry, ExtensionSchema<ET> extensionSchema, FieldSet<ET> extensions, UnknownFieldSchema<UT, UB> unknownFieldSchema, UB unknownFields) throws IOException {
/* 282 */     int startTag = reader.getTag();
/* 283 */     if (startTag != WireFormat.MESSAGE_SET_ITEM_TAG) {
/* 284 */       if (WireFormat.getTagWireType(startTag) == 2) {
/*     */         
/* 286 */         Object object = extensionSchema.findExtensionByNumber(extensionRegistry, this.defaultInstance, 
/* 287 */             WireFormat.getTagFieldNumber(startTag));
/* 288 */         if (object != null) {
/* 289 */           extensionSchema.parseLengthPrefixedMessageSetItem(reader, object, extensionRegistry, extensions);
/*     */           
/* 291 */           return true;
/*     */         } 
/* 293 */         return unknownFieldSchema.mergeOneFieldFrom(unknownFields, reader);
/*     */       } 
/*     */       
/* 296 */       return reader.skipField();
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
/* 316 */     int typeId = 0;
/* 317 */     ByteString rawBytes = null;
/* 318 */     Object extension = null;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     while (true) {
/* 324 */       int number = reader.getFieldNumber();
/* 325 */       if (number == Integer.MAX_VALUE) {
/*     */         break;
/*     */       }
/*     */       
/* 329 */       int tag = reader.getTag();
/* 330 */       if (tag == WireFormat.MESSAGE_SET_TYPE_ID_TAG) {
/* 331 */         typeId = reader.readUInt32();
/*     */         
/* 333 */         extension = extensionSchema.findExtensionByNumber(extensionRegistry, this.defaultInstance, typeId); continue;
/*     */       } 
/* 335 */       if (tag == WireFormat.MESSAGE_SET_MESSAGE_TAG) {
/* 336 */         if (extension != null) {
/* 337 */           extensionSchema.parseLengthPrefixedMessageSetItem(reader, extension, extensionRegistry, extensions);
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 342 */         rawBytes = reader.readBytes();
/*     */         continue;
/*     */       } 
/* 345 */       if (!reader.skipField()) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 351 */     if (reader.getTag() != WireFormat.MESSAGE_SET_ITEM_END_TAG) {
/* 352 */       throw InvalidProtocolBufferException.invalidEndTag();
/*     */     }
/*     */ 
/*     */     
/* 356 */     if (rawBytes != null) {
/* 357 */       if (extension != null) {
/*     */ 
/*     */         
/* 360 */         extensionSchema.parseMessageSetItem(rawBytes, extension, extensionRegistry, extensions);
/*     */       } else {
/* 362 */         unknownFieldSchema.addLengthDelimited(unknownFields, typeId, rawBytes);
/*     */       } 
/*     */     }
/* 365 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isInitialized(T message) {
/* 370 */     FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
/* 371 */     return extensions.isInitialized();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSerializedSize(T message) {
/* 376 */     int size = 0;
/*     */     
/* 378 */     size += getUnknownFieldsSerializedSize(this.unknownFieldSchema, message);
/*     */     
/* 380 */     if (this.hasExtensions) {
/* 381 */       size += this.extensionSchema.getExtensions(message).getMessageSetSerializedSize();
/*     */     }
/*     */     
/* 384 */     return size;
/*     */   }
/*     */ 
/*     */   
/*     */   private <UT, UB> int getUnknownFieldsSerializedSize(UnknownFieldSchema<UT, UB> schema, T message) {
/* 389 */     UT unknowns = schema.getFromMessage(message);
/* 390 */     return schema.getSerializedSizeAsMessageSet(unknowns);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MessageSetSchema.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */