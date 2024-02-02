/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ final class ExtensionSchemaLite
/*     */   extends ExtensionSchema<GeneratedMessageLite.ExtensionDescriptor>
/*     */ {
/*     */   boolean hasExtensions(MessageLite prototype) {
/*  45 */     return prototype instanceof GeneratedMessageLite.ExtendableMessage;
/*     */   }
/*     */ 
/*     */   
/*     */   FieldSet<GeneratedMessageLite.ExtensionDescriptor> getExtensions(Object message) {
/*  50 */     return ((GeneratedMessageLite.ExtendableMessage)message).extensions;
/*     */   }
/*     */ 
/*     */   
/*     */   void setExtensions(Object message, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) {
/*  55 */     ((GeneratedMessageLite.ExtendableMessage)message).extensions = extensions;
/*     */   }
/*     */ 
/*     */   
/*     */   FieldSet<GeneratedMessageLite.ExtensionDescriptor> getMutableExtensions(Object message) {
/*  60 */     return ((GeneratedMessageLite.ExtendableMessage)message).ensureExtensionsAreMutable();
/*     */   }
/*     */ 
/*     */   
/*     */   void makeImmutable(Object message) {
/*  65 */     getExtensions(message).makeImmutable();
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
/*     */   <UT, UB> UB parseExtension(Reader reader, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema) throws IOException {
/*  77 */     GeneratedMessageLite.GeneratedExtension<?, ?> extension = (GeneratedMessageLite.GeneratedExtension<?, ?>)extensionObject;
/*     */     
/*  79 */     int fieldNumber = extension.getNumber();
/*     */     
/*  81 */     if (extension.descriptor.isRepeated() && extension.descriptor.isPacked()) {
/*  82 */       List<Float> list11; List<Long> list10; List<Integer> list9; List<Long> list8; List<Integer> list7; List<Boolean> list6; List<Integer> list5; List<Long> list4; List<Integer> list3; List<Long> list2; List<Integer> list1; List<Double> list22; List<Float> list21; List<Long> list20; List<Integer> list19; List<Long> list18; List<Integer> list17; List<Boolean> list16; List<Integer> list15; List<Long> list14; List<Integer> list13; List<Long> list12; List<Integer> list; Object<Double> value = null;
/*  83 */       switch (extension.getLiteType()) {
/*     */         
/*     */         case DOUBLE:
/*  86 */           list22 = new ArrayList<>();
/*  87 */           reader.readDoubleList(list22);
/*  88 */           value = (Object<Double>)list22;
/*     */           break;
/*     */ 
/*     */         
/*     */         case FLOAT:
/*  93 */           list21 = new ArrayList<>();
/*  94 */           reader.readFloatList(list21);
/*  95 */           list11 = list21;
/*     */           break;
/*     */ 
/*     */         
/*     */         case INT64:
/* 100 */           list20 = new ArrayList<>();
/* 101 */           reader.readInt64List(list20);
/* 102 */           list10 = list20;
/*     */           break;
/*     */ 
/*     */         
/*     */         case UINT64:
/* 107 */           list20 = new ArrayList<>();
/* 108 */           reader.readUInt64List(list20);
/* 109 */           list10 = list20;
/*     */           break;
/*     */ 
/*     */         
/*     */         case INT32:
/* 114 */           list19 = new ArrayList<>();
/* 115 */           reader.readInt32List(list19);
/* 116 */           list9 = list19;
/*     */           break;
/*     */ 
/*     */         
/*     */         case FIXED64:
/* 121 */           list18 = new ArrayList<>();
/* 122 */           reader.readFixed64List(list18);
/* 123 */           list8 = list18;
/*     */           break;
/*     */ 
/*     */         
/*     */         case FIXED32:
/* 128 */           list17 = new ArrayList<>();
/* 129 */           reader.readFixed32List(list17);
/* 130 */           list7 = list17;
/*     */           break;
/*     */ 
/*     */         
/*     */         case BOOL:
/* 135 */           list16 = new ArrayList<>();
/* 136 */           reader.readBoolList(list16);
/* 137 */           list6 = list16;
/*     */           break;
/*     */ 
/*     */         
/*     */         case UINT32:
/* 142 */           list15 = new ArrayList<>();
/* 143 */           reader.readUInt32List(list15);
/* 144 */           list5 = list15;
/*     */           break;
/*     */ 
/*     */         
/*     */         case SFIXED32:
/* 149 */           list15 = new ArrayList<>();
/* 150 */           reader.readSFixed32List(list15);
/* 151 */           list5 = list15;
/*     */           break;
/*     */ 
/*     */         
/*     */         case SFIXED64:
/* 156 */           list14 = new ArrayList<>();
/* 157 */           reader.readSFixed64List(list14);
/* 158 */           list4 = list14;
/*     */           break;
/*     */ 
/*     */         
/*     */         case SINT32:
/* 163 */           list13 = new ArrayList<>();
/* 164 */           reader.readSInt32List(list13);
/* 165 */           list3 = list13;
/*     */           break;
/*     */ 
/*     */         
/*     */         case SINT64:
/* 170 */           list12 = new ArrayList<>();
/* 171 */           reader.readSInt64List(list12);
/* 172 */           list2 = list12;
/*     */           break;
/*     */ 
/*     */         
/*     */         case ENUM:
/* 177 */           list = new ArrayList<>();
/* 178 */           reader.readEnumList(list);
/*     */           
/* 180 */           unknownFields = SchemaUtil.filterUnknownEnumList(fieldNumber, list, extension.descriptor
/*     */ 
/*     */               
/* 183 */               .getEnumType(), unknownFields, unknownFieldSchema);
/*     */ 
/*     */           
/* 186 */           list1 = list;
/*     */           break;
/*     */         
/*     */         default:
/* 190 */           throw new IllegalStateException("Type cannot be packed: " + extension.descriptor
/* 191 */               .getLiteType());
/*     */       } 
/* 193 */       extensions.setField(extension.descriptor, list1);
/*     */     } else {
/* 195 */       Object value = null;
/*     */       
/* 197 */       if (extension.getLiteType() == WireFormat.FieldType.ENUM) {
/* 198 */         int number = reader.readInt32();
/* 199 */         Object enumValue = extension.descriptor.getEnumType().findValueByNumber(number);
/* 200 */         if (enumValue == null) {
/* 201 */           return SchemaUtil.storeUnknownEnum(fieldNumber, number, unknownFields, unknownFieldSchema);
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 206 */         value = Integer.valueOf(number);
/*     */       } else {
/* 208 */         switch (extension.getLiteType()) {
/*     */           case DOUBLE:
/* 210 */             value = Double.valueOf(reader.readDouble());
/*     */             break;
/*     */           case FLOAT:
/* 213 */             value = Float.valueOf(reader.readFloat());
/*     */             break;
/*     */           case INT64:
/* 216 */             value = Long.valueOf(reader.readInt64());
/*     */             break;
/*     */           case UINT64:
/* 219 */             value = Long.valueOf(reader.readUInt64());
/*     */             break;
/*     */           case INT32:
/* 222 */             value = Integer.valueOf(reader.readInt32());
/*     */             break;
/*     */           case FIXED64:
/* 225 */             value = Long.valueOf(reader.readFixed64());
/*     */             break;
/*     */           case FIXED32:
/* 228 */             value = Integer.valueOf(reader.readFixed32());
/*     */             break;
/*     */           case BOOL:
/* 231 */             value = Boolean.valueOf(reader.readBool());
/*     */             break;
/*     */           case BYTES:
/* 234 */             value = reader.readBytes();
/*     */             break;
/*     */           case UINT32:
/* 237 */             value = Integer.valueOf(reader.readUInt32());
/*     */             break;
/*     */           case SFIXED32:
/* 240 */             value = Integer.valueOf(reader.readSFixed32());
/*     */             break;
/*     */           case SFIXED64:
/* 243 */             value = Long.valueOf(reader.readSFixed64());
/*     */             break;
/*     */           case SINT32:
/* 246 */             value = Integer.valueOf(reader.readSInt32());
/*     */             break;
/*     */           case SINT64:
/* 249 */             value = Long.valueOf(reader.readSInt64());
/*     */             break;
/*     */           
/*     */           case STRING:
/* 253 */             value = reader.readString();
/*     */             break;
/*     */           
/*     */           case GROUP:
/* 257 */             value = reader.readGroup(extension
/* 258 */                 .getMessageDefaultInstance().getClass(), extensionRegistry);
/*     */             break;
/*     */ 
/*     */           
/*     */           case MESSAGE:
/* 263 */             value = reader.readMessage(extension
/* 264 */                 .getMessageDefaultInstance().getClass(), extensionRegistry);
/*     */             break;
/*     */           
/*     */           case ENUM:
/* 268 */             throw new IllegalStateException("Shouldn't reach here.");
/*     */         } 
/*     */       } 
/* 271 */       if (extension.isRepeated()) {
/* 272 */         extensions.addRepeatedField(extension.descriptor, value);
/*     */       } else {
/* 274 */         Object oldValue; switch (extension.getLiteType()) {
/*     */           case GROUP:
/*     */           case MESSAGE:
/* 277 */             oldValue = extensions.getField(extension.descriptor);
/* 278 */             if (oldValue != null) {
/* 279 */               value = Internal.mergeMessage(oldValue, value);
/*     */             }
/*     */             break;
/*     */         } 
/*     */ 
/*     */         
/* 285 */         extensions.setField(extension.descriptor, value);
/*     */       } 
/*     */     } 
/* 288 */     return unknownFields;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   int extensionNumber(Map.Entry<?, ?> extension) {
/* 294 */     GeneratedMessageLite.ExtensionDescriptor descriptor = (GeneratedMessageLite.ExtensionDescriptor)extension.getKey();
/* 295 */     return descriptor.getNumber();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void serializeExtension(Writer writer, Map.Entry<?, ?> extension) throws IOException {
/* 301 */     GeneratedMessageLite.ExtensionDescriptor descriptor = (GeneratedMessageLite.ExtensionDescriptor)extension.getKey();
/* 302 */     if (descriptor.isRepeated()) {
/* 303 */       List<?> data; switch (descriptor.getLiteType()) {
/*     */         case DOUBLE:
/* 305 */           SchemaUtil.writeDoubleList(descriptor
/* 306 */               .getNumber(), (List<Double>)extension
/* 307 */               .getValue(), writer, descriptor
/*     */               
/* 309 */               .isPacked());
/*     */           break;
/*     */         case FLOAT:
/* 312 */           SchemaUtil.writeFloatList(descriptor
/* 313 */               .getNumber(), (List<Float>)extension
/* 314 */               .getValue(), writer, descriptor
/*     */               
/* 316 */               .isPacked());
/*     */           break;
/*     */         case INT64:
/* 319 */           SchemaUtil.writeInt64List(descriptor
/* 320 */               .getNumber(), (List<Long>)extension
/* 321 */               .getValue(), writer, descriptor
/*     */               
/* 323 */               .isPacked());
/*     */           break;
/*     */         case UINT64:
/* 326 */           SchemaUtil.writeUInt64List(descriptor
/* 327 */               .getNumber(), (List<Long>)extension
/* 328 */               .getValue(), writer, descriptor
/*     */               
/* 330 */               .isPacked());
/*     */           break;
/*     */         case INT32:
/* 333 */           SchemaUtil.writeInt32List(descriptor
/* 334 */               .getNumber(), (List<Integer>)extension
/* 335 */               .getValue(), writer, descriptor
/*     */               
/* 337 */               .isPacked());
/*     */           break;
/*     */         case FIXED64:
/* 340 */           SchemaUtil.writeFixed64List(descriptor
/* 341 */               .getNumber(), (List<Long>)extension
/* 342 */               .getValue(), writer, descriptor
/*     */               
/* 344 */               .isPacked());
/*     */           break;
/*     */         case FIXED32:
/* 347 */           SchemaUtil.writeFixed32List(descriptor
/* 348 */               .getNumber(), (List<Integer>)extension
/* 349 */               .getValue(), writer, descriptor
/*     */               
/* 351 */               .isPacked());
/*     */           break;
/*     */         case BOOL:
/* 354 */           SchemaUtil.writeBoolList(descriptor
/* 355 */               .getNumber(), (List<Boolean>)extension
/* 356 */               .getValue(), writer, descriptor
/*     */               
/* 358 */               .isPacked());
/*     */           break;
/*     */         case BYTES:
/* 361 */           SchemaUtil.writeBytesList(descriptor
/* 362 */               .getNumber(), (List<ByteString>)extension.getValue(), writer);
/*     */           break;
/*     */         case UINT32:
/* 365 */           SchemaUtil.writeUInt32List(descriptor
/* 366 */               .getNumber(), (List<Integer>)extension
/* 367 */               .getValue(), writer, descriptor
/*     */               
/* 369 */               .isPacked());
/*     */           break;
/*     */         case SFIXED32:
/* 372 */           SchemaUtil.writeSFixed32List(descriptor
/* 373 */               .getNumber(), (List<Integer>)extension
/* 374 */               .getValue(), writer, descriptor
/*     */               
/* 376 */               .isPacked());
/*     */           break;
/*     */         case SFIXED64:
/* 379 */           SchemaUtil.writeSFixed64List(descriptor
/* 380 */               .getNumber(), (List<Long>)extension
/* 381 */               .getValue(), writer, descriptor
/*     */               
/* 383 */               .isPacked());
/*     */           break;
/*     */         case SINT32:
/* 386 */           SchemaUtil.writeSInt32List(descriptor
/* 387 */               .getNumber(), (List<Integer>)extension
/* 388 */               .getValue(), writer, descriptor
/*     */               
/* 390 */               .isPacked());
/*     */           break;
/*     */         case SINT64:
/* 393 */           SchemaUtil.writeSInt64List(descriptor
/* 394 */               .getNumber(), (List<Long>)extension
/* 395 */               .getValue(), writer, descriptor
/*     */               
/* 397 */               .isPacked());
/*     */           break;
/*     */         case ENUM:
/* 400 */           SchemaUtil.writeInt32List(descriptor
/* 401 */               .getNumber(), (List<Integer>)extension
/* 402 */               .getValue(), writer, descriptor
/*     */               
/* 404 */               .isPacked());
/*     */           break;
/*     */         case STRING:
/* 407 */           SchemaUtil.writeStringList(descriptor
/* 408 */               .getNumber(), (List<String>)extension.getValue(), writer);
/*     */           break;
/*     */         
/*     */         case GROUP:
/* 412 */           data = (List)extension.getValue();
/* 413 */           if (data != null && !data.isEmpty()) {
/* 414 */             SchemaUtil.writeGroupList(descriptor
/* 415 */                 .getNumber(), (List)extension
/* 416 */                 .getValue(), writer, 
/*     */                 
/* 418 */                 Protobuf.getInstance().schemaFor(data.get(0).getClass()));
/*     */           }
/*     */           break;
/*     */ 
/*     */         
/*     */         case MESSAGE:
/* 424 */           data = (List)extension.getValue();
/* 425 */           if (data != null && !data.isEmpty()) {
/* 426 */             SchemaUtil.writeMessageList(descriptor
/* 427 */                 .getNumber(), (List)extension
/* 428 */                 .getValue(), writer, 
/*     */                 
/* 430 */                 Protobuf.getInstance().schemaFor(data.get(0).getClass()));
/*     */           }
/*     */           break;
/*     */       } 
/*     */     
/*     */     } else {
/* 436 */       switch (descriptor.getLiteType()) {
/*     */         case DOUBLE:
/* 438 */           writer.writeDouble(descriptor.getNumber(), ((Double)extension.getValue()).doubleValue());
/*     */           break;
/*     */         case FLOAT:
/* 441 */           writer.writeFloat(descriptor.getNumber(), ((Float)extension.getValue()).floatValue());
/*     */           break;
/*     */         case INT64:
/* 444 */           writer.writeInt64(descriptor.getNumber(), ((Long)extension.getValue()).longValue());
/*     */           break;
/*     */         case UINT64:
/* 447 */           writer.writeUInt64(descriptor.getNumber(), ((Long)extension.getValue()).longValue());
/*     */           break;
/*     */         case INT32:
/* 450 */           writer.writeInt32(descriptor.getNumber(), ((Integer)extension.getValue()).intValue());
/*     */           break;
/*     */         case FIXED64:
/* 453 */           writer.writeFixed64(descriptor.getNumber(), ((Long)extension.getValue()).longValue());
/*     */           break;
/*     */         case FIXED32:
/* 456 */           writer.writeFixed32(descriptor.getNumber(), ((Integer)extension.getValue()).intValue());
/*     */           break;
/*     */         case BOOL:
/* 459 */           writer.writeBool(descriptor.getNumber(), ((Boolean)extension.getValue()).booleanValue());
/*     */           break;
/*     */         case BYTES:
/* 462 */           writer.writeBytes(descriptor.getNumber(), (ByteString)extension.getValue());
/*     */           break;
/*     */         case UINT32:
/* 465 */           writer.writeUInt32(descriptor.getNumber(), ((Integer)extension.getValue()).intValue());
/*     */           break;
/*     */         case SFIXED32:
/* 468 */           writer.writeSFixed32(descriptor.getNumber(), ((Integer)extension.getValue()).intValue());
/*     */           break;
/*     */         case SFIXED64:
/* 471 */           writer.writeSFixed64(descriptor.getNumber(), ((Long)extension.getValue()).longValue());
/*     */           break;
/*     */         case SINT32:
/* 474 */           writer.writeSInt32(descriptor.getNumber(), ((Integer)extension.getValue()).intValue());
/*     */           break;
/*     */         case SINT64:
/* 477 */           writer.writeSInt64(descriptor.getNumber(), ((Long)extension.getValue()).longValue());
/*     */           break;
/*     */         case ENUM:
/* 480 */           writer.writeInt32(descriptor.getNumber(), ((Integer)extension.getValue()).intValue());
/*     */           break;
/*     */         case STRING:
/* 483 */           writer.writeString(descriptor.getNumber(), (String)extension.getValue());
/*     */           break;
/*     */         case GROUP:
/* 486 */           writer.writeGroup(descriptor
/* 487 */               .getNumber(), extension
/* 488 */               .getValue(), 
/* 489 */               Protobuf.getInstance().schemaFor(extension.getValue().getClass()));
/*     */           break;
/*     */         case MESSAGE:
/* 492 */           writer.writeMessage(descriptor
/* 493 */               .getNumber(), extension
/* 494 */               .getValue(), 
/* 495 */               Protobuf.getInstance().schemaFor(extension.getValue().getClass()));
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   Object findExtensionByNumber(ExtensionRegistryLite extensionRegistry, MessageLite defaultInstance, int number) {
/* 504 */     return extensionRegistry.findLiteExtensionByNumber(defaultInstance, number);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void parseLengthPrefixedMessageSetItem(Reader reader, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) throws IOException {
/* 514 */     GeneratedMessageLite.GeneratedExtension<?, ?> extension = (GeneratedMessageLite.GeneratedExtension<?, ?>)extensionObject;
/*     */ 
/*     */     
/* 517 */     Object value = reader.readMessage(extension.getMessageDefaultInstance().getClass(), extensionRegistry);
/* 518 */     extensions.setField(extension.descriptor, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void parseMessageSetItem(ByteString data, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) throws IOException {
/* 528 */     GeneratedMessageLite.GeneratedExtension<?, ?> extension = (GeneratedMessageLite.GeneratedExtension<?, ?>)extensionObject;
/*     */     
/* 530 */     Object value = extension.getMessageDefaultInstance().newBuilderForType().buildPartial();
/*     */     
/* 532 */     Reader reader = BinaryReader.newInstance(ByteBuffer.wrap(data.toByteArray()), true);
/*     */     
/* 534 */     Protobuf.getInstance().mergeFrom(value, reader, extensionRegistry);
/* 535 */     extensions.setField(extension.descriptor, value);
/*     */     
/* 537 */     if (reader.getFieldNumber() != Integer.MAX_VALUE)
/* 538 */       throw InvalidProtocolBufferException.invalidEndTag(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ExtensionSchemaLite.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */