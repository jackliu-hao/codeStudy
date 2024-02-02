/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
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
/*     */ final class ExtensionSchemaFull
/*     */   extends ExtensionSchema<Descriptors.FieldDescriptor>
/*     */ {
/*  45 */   private static final long EXTENSION_FIELD_OFFSET = getExtensionsFieldOffset();
/*     */   
/*     */   private static <T> long getExtensionsFieldOffset() {
/*     */     try {
/*  49 */       Field field = GeneratedMessageV3.ExtendableMessage.class.getDeclaredField("extensions");
/*  50 */       return UnsafeUtil.objectFieldOffset(field);
/*  51 */     } catch (Throwable e) {
/*  52 */       throw new IllegalStateException("Unable to lookup extension field offset");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   boolean hasExtensions(MessageLite prototype) {
/*  58 */     return prototype instanceof GeneratedMessageV3.ExtendableMessage;
/*     */   }
/*     */ 
/*     */   
/*     */   public FieldSet<Descriptors.FieldDescriptor> getExtensions(Object message) {
/*  63 */     return (FieldSet<Descriptors.FieldDescriptor>)UnsafeUtil.getObject(message, EXTENSION_FIELD_OFFSET);
/*     */   }
/*     */ 
/*     */   
/*     */   void setExtensions(Object message, FieldSet<Descriptors.FieldDescriptor> extensions) {
/*  68 */     UnsafeUtil.putObject(message, EXTENSION_FIELD_OFFSET, extensions);
/*     */   }
/*     */ 
/*     */   
/*     */   FieldSet<Descriptors.FieldDescriptor> getMutableExtensions(Object message) {
/*  73 */     FieldSet<Descriptors.FieldDescriptor> extensions = getExtensions(message);
/*  74 */     if (extensions.isImmutable()) {
/*  75 */       extensions = extensions.clone();
/*  76 */       setExtensions(message, extensions);
/*     */     } 
/*  78 */     return extensions;
/*     */   }
/*     */ 
/*     */   
/*     */   void makeImmutable(Object message) {
/*  83 */     getExtensions(message).makeImmutable();
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
/*     */   <UT, UB> UB parseExtension(Reader reader, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<Descriptors.FieldDescriptor> extensions, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema) throws IOException {
/*  95 */     ExtensionRegistry.ExtensionInfo extension = (ExtensionRegistry.ExtensionInfo)extensionObject;
/*  96 */     int fieldNumber = extension.descriptor.getNumber();
/*     */     
/*  98 */     if (extension.descriptor.isRepeated() && extension.descriptor.isPacked()) {
/*  99 */       List<Float> list11; List<Long> list10; List<Integer> list9; List<Long> list8; List<Integer> list7; List<Boolean> list6; List<Integer> list5; List<Long> list4; List<Integer> list3; List<Long> list2; List<Descriptors.EnumValueDescriptor> list1; List<Double> list22; List<Float> list21; List<Long> list20; List<Integer> list19; List<Long> list18; List<Integer> list17; List<Boolean> list16; List<Integer> list15; List<Long> list14; List<Integer> list13; List<Long> list12; List<Integer> list; List<Descriptors.EnumValueDescriptor> enumList; Iterator<Integer> iterator; Object<Double> value = null;
/* 100 */       switch (extension.descriptor.getLiteType()) {
/*     */         
/*     */         case DOUBLE:
/* 103 */           list22 = new ArrayList<>();
/* 104 */           reader.readDoubleList(list22);
/* 105 */           value = (Object<Double>)list22;
/*     */           break;
/*     */ 
/*     */         
/*     */         case FLOAT:
/* 110 */           list21 = new ArrayList<>();
/* 111 */           reader.readFloatList(list21);
/* 112 */           list11 = list21;
/*     */           break;
/*     */ 
/*     */         
/*     */         case INT64:
/* 117 */           list20 = new ArrayList<>();
/* 118 */           reader.readInt64List(list20);
/* 119 */           list10 = list20;
/*     */           break;
/*     */ 
/*     */         
/*     */         case UINT64:
/* 124 */           list20 = new ArrayList<>();
/* 125 */           reader.readUInt64List(list20);
/* 126 */           list10 = list20;
/*     */           break;
/*     */ 
/*     */         
/*     */         case INT32:
/* 131 */           list19 = new ArrayList<>();
/* 132 */           reader.readInt32List(list19);
/* 133 */           list9 = list19;
/*     */           break;
/*     */ 
/*     */         
/*     */         case FIXED64:
/* 138 */           list18 = new ArrayList<>();
/* 139 */           reader.readFixed64List(list18);
/* 140 */           list8 = list18;
/*     */           break;
/*     */ 
/*     */         
/*     */         case FIXED32:
/* 145 */           list17 = new ArrayList<>();
/* 146 */           reader.readFixed32List(list17);
/* 147 */           list7 = list17;
/*     */           break;
/*     */ 
/*     */         
/*     */         case BOOL:
/* 152 */           list16 = new ArrayList<>();
/* 153 */           reader.readBoolList(list16);
/* 154 */           list6 = list16;
/*     */           break;
/*     */ 
/*     */         
/*     */         case UINT32:
/* 159 */           list15 = new ArrayList<>();
/* 160 */           reader.readUInt32List(list15);
/* 161 */           list5 = list15;
/*     */           break;
/*     */ 
/*     */         
/*     */         case SFIXED32:
/* 166 */           list15 = new ArrayList<>();
/* 167 */           reader.readSFixed32List(list15);
/* 168 */           list5 = list15;
/*     */           break;
/*     */ 
/*     */         
/*     */         case SFIXED64:
/* 173 */           list14 = new ArrayList<>();
/* 174 */           reader.readSFixed64List(list14);
/* 175 */           list4 = list14;
/*     */           break;
/*     */ 
/*     */         
/*     */         case SINT32:
/* 180 */           list13 = new ArrayList<>();
/* 181 */           reader.readSInt32List(list13);
/* 182 */           list3 = list13;
/*     */           break;
/*     */ 
/*     */         
/*     */         case SINT64:
/* 187 */           list12 = new ArrayList<>();
/* 188 */           reader.readSInt64List(list12);
/* 189 */           list2 = list12;
/*     */           break;
/*     */ 
/*     */         
/*     */         case ENUM:
/* 194 */           list = new ArrayList<>();
/* 195 */           reader.readEnumList(list);
/* 196 */           enumList = new ArrayList<>();
/* 197 */           for (iterator = list.iterator(); iterator.hasNext(); ) { int number = ((Integer)iterator.next()).intValue();
/*     */             
/* 199 */             Descriptors.EnumValueDescriptor enumDescriptor = extension.descriptor.getEnumType().findValueByNumber(number);
/* 200 */             if (enumDescriptor != null) {
/* 201 */               enumList.add(enumDescriptor);
/*     */               continue;
/*     */             } 
/* 204 */             unknownFields = SchemaUtil.storeUnknownEnum(fieldNumber, number, unknownFields, unknownFieldSchema); }
/*     */ 
/*     */ 
/*     */           
/* 208 */           list1 = enumList;
/*     */           break;
/*     */         
/*     */         default:
/* 212 */           throw new IllegalStateException("Type cannot be packed: " + extension.descriptor
/* 213 */               .getLiteType());
/*     */       } 
/* 215 */       extensions.setField(extension.descriptor, list1);
/*     */     } else {
/* 217 */       Object value = null;
/*     */       
/* 219 */       if (extension.descriptor.getLiteType() == WireFormat.FieldType.ENUM) {
/* 220 */         int number = reader.readInt32();
/* 221 */         Object enumValue = extension.descriptor.getEnumType().findValueByNumber(number);
/* 222 */         if (enumValue == null) {
/* 223 */           return SchemaUtil.storeUnknownEnum(fieldNumber, number, unknownFields, unknownFieldSchema);
/*     */         }
/*     */         
/* 226 */         value = enumValue;
/*     */       } else {
/* 228 */         switch (extension.descriptor.getLiteType()) {
/*     */           case DOUBLE:
/* 230 */             value = Double.valueOf(reader.readDouble());
/*     */             break;
/*     */           case FLOAT:
/* 233 */             value = Float.valueOf(reader.readFloat());
/*     */             break;
/*     */           case INT64:
/* 236 */             value = Long.valueOf(reader.readInt64());
/*     */             break;
/*     */           case UINT64:
/* 239 */             value = Long.valueOf(reader.readUInt64());
/*     */             break;
/*     */           case INT32:
/* 242 */             value = Integer.valueOf(reader.readInt32());
/*     */             break;
/*     */           case FIXED64:
/* 245 */             value = Long.valueOf(reader.readFixed64());
/*     */             break;
/*     */           case FIXED32:
/* 248 */             value = Integer.valueOf(reader.readFixed32());
/*     */             break;
/*     */           case BOOL:
/* 251 */             value = Boolean.valueOf(reader.readBool());
/*     */             break;
/*     */           case BYTES:
/* 254 */             value = reader.readBytes();
/*     */             break;
/*     */           case UINT32:
/* 257 */             value = Integer.valueOf(reader.readUInt32());
/*     */             break;
/*     */           case SFIXED32:
/* 260 */             value = Integer.valueOf(reader.readSFixed32());
/*     */             break;
/*     */           case SFIXED64:
/* 263 */             value = Long.valueOf(reader.readSFixed64());
/*     */             break;
/*     */           case SINT32:
/* 266 */             value = Integer.valueOf(reader.readSInt32());
/*     */             break;
/*     */           case SINT64:
/* 269 */             value = Long.valueOf(reader.readSInt64());
/*     */             break;
/*     */           
/*     */           case STRING:
/* 273 */             value = reader.readString();
/*     */             break;
/*     */           case GROUP:
/* 276 */             value = reader.readGroup(extension.defaultInstance.getClass(), extensionRegistry);
/*     */             break;
/*     */           
/*     */           case MESSAGE:
/* 280 */             value = reader.readMessage(extension.defaultInstance.getClass(), extensionRegistry);
/*     */             break;
/*     */           
/*     */           case ENUM:
/* 284 */             throw new IllegalStateException("Shouldn't reach here.");
/*     */         } 
/*     */       } 
/* 287 */       if (extension.descriptor.isRepeated()) {
/* 288 */         extensions.addRepeatedField(extension.descriptor, value);
/*     */       } else {
/* 290 */         Object oldValue; switch (extension.descriptor.getLiteType()) {
/*     */           case GROUP:
/*     */           case MESSAGE:
/* 293 */             oldValue = extensions.getField(extension.descriptor);
/* 294 */             if (oldValue != null) {
/* 295 */               value = Internal.mergeMessage(oldValue, value);
/*     */             }
/*     */             break;
/*     */         } 
/*     */ 
/*     */         
/* 301 */         extensions.setField(extension.descriptor, value);
/*     */       } 
/*     */     } 
/* 304 */     return unknownFields;
/*     */   }
/*     */ 
/*     */   
/*     */   int extensionNumber(Map.Entry<?, ?> extension) {
/* 309 */     Descriptors.FieldDescriptor descriptor = (Descriptors.FieldDescriptor)extension.getKey();
/* 310 */     return descriptor.getNumber();
/*     */   }
/*     */ 
/*     */   
/*     */   void serializeExtension(Writer writer, Map.Entry<?, ?> extension) throws IOException {
/* 315 */     Descriptors.FieldDescriptor descriptor = (Descriptors.FieldDescriptor)extension.getKey();
/* 316 */     if (descriptor.isRepeated()) {
/* 317 */       List<Descriptors.EnumValueDescriptor> enumList; List<Integer> list; switch (descriptor.getLiteType()) {
/*     */         case DOUBLE:
/* 319 */           SchemaUtil.writeDoubleList(descriptor
/* 320 */               .getNumber(), (List<Double>)extension
/* 321 */               .getValue(), writer, descriptor
/*     */               
/* 323 */               .isPacked());
/*     */           break;
/*     */         case FLOAT:
/* 326 */           SchemaUtil.writeFloatList(descriptor
/* 327 */               .getNumber(), (List<Float>)extension
/* 328 */               .getValue(), writer, descriptor
/*     */               
/* 330 */               .isPacked());
/*     */           break;
/*     */         case INT64:
/* 333 */           SchemaUtil.writeInt64List(descriptor
/* 334 */               .getNumber(), (List<Long>)extension
/* 335 */               .getValue(), writer, descriptor
/*     */               
/* 337 */               .isPacked());
/*     */           break;
/*     */         case UINT64:
/* 340 */           SchemaUtil.writeUInt64List(descriptor
/* 341 */               .getNumber(), (List<Long>)extension
/* 342 */               .getValue(), writer, descriptor
/*     */               
/* 344 */               .isPacked());
/*     */           break;
/*     */         case INT32:
/* 347 */           SchemaUtil.writeInt32List(descriptor
/* 348 */               .getNumber(), (List<Integer>)extension
/* 349 */               .getValue(), writer, descriptor
/*     */               
/* 351 */               .isPacked());
/*     */           break;
/*     */         case FIXED64:
/* 354 */           SchemaUtil.writeFixed64List(descriptor
/* 355 */               .getNumber(), (List<Long>)extension
/* 356 */               .getValue(), writer, descriptor
/*     */               
/* 358 */               .isPacked());
/*     */           break;
/*     */         case FIXED32:
/* 361 */           SchemaUtil.writeFixed32List(descriptor
/* 362 */               .getNumber(), (List<Integer>)extension
/* 363 */               .getValue(), writer, descriptor
/*     */               
/* 365 */               .isPacked());
/*     */           break;
/*     */         case BOOL:
/* 368 */           SchemaUtil.writeBoolList(descriptor
/* 369 */               .getNumber(), (List<Boolean>)extension
/* 370 */               .getValue(), writer, descriptor
/*     */               
/* 372 */               .isPacked());
/*     */           break;
/*     */         case BYTES:
/* 375 */           SchemaUtil.writeBytesList(descriptor
/* 376 */               .getNumber(), (List<ByteString>)extension.getValue(), writer);
/*     */           break;
/*     */         case UINT32:
/* 379 */           SchemaUtil.writeUInt32List(descriptor
/* 380 */               .getNumber(), (List<Integer>)extension
/* 381 */               .getValue(), writer, descriptor
/*     */               
/* 383 */               .isPacked());
/*     */           break;
/*     */         case SFIXED32:
/* 386 */           SchemaUtil.writeSFixed32List(descriptor
/* 387 */               .getNumber(), (List<Integer>)extension
/* 388 */               .getValue(), writer, descriptor
/*     */               
/* 390 */               .isPacked());
/*     */           break;
/*     */         case SFIXED64:
/* 393 */           SchemaUtil.writeSFixed64List(descriptor
/* 394 */               .getNumber(), (List<Long>)extension
/* 395 */               .getValue(), writer, descriptor
/*     */               
/* 397 */               .isPacked());
/*     */           break;
/*     */         case SINT32:
/* 400 */           SchemaUtil.writeSInt32List(descriptor
/* 401 */               .getNumber(), (List<Integer>)extension
/* 402 */               .getValue(), writer, descriptor
/*     */               
/* 404 */               .isPacked());
/*     */           break;
/*     */         case SINT64:
/* 407 */           SchemaUtil.writeSInt64List(descriptor
/* 408 */               .getNumber(), (List<Long>)extension
/* 409 */               .getValue(), writer, descriptor
/*     */               
/* 411 */               .isPacked());
/*     */           break;
/*     */         
/*     */         case ENUM:
/* 415 */           enumList = (List<Descriptors.EnumValueDescriptor>)extension.getValue();
/* 416 */           list = new ArrayList<>();
/* 417 */           for (Descriptors.EnumValueDescriptor d : enumList) {
/* 418 */             list.add(Integer.valueOf(d.getNumber()));
/*     */           }
/* 420 */           SchemaUtil.writeInt32List(descriptor.getNumber(), list, writer, descriptor.isPacked());
/*     */           break;
/*     */         
/*     */         case STRING:
/* 424 */           SchemaUtil.writeStringList(descriptor
/* 425 */               .getNumber(), (List<String>)extension.getValue(), writer);
/*     */           break;
/*     */         case GROUP:
/* 428 */           SchemaUtil.writeGroupList(descriptor.getNumber(), (List)extension.getValue(), writer);
/*     */           break;
/*     */         case MESSAGE:
/* 431 */           SchemaUtil.writeMessageList(descriptor
/* 432 */               .getNumber(), (List)extension.getValue(), writer);
/*     */           break;
/*     */       } 
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
/* 480 */           writer.writeInt32(descriptor
/* 481 */               .getNumber(), ((Descriptors.EnumValueDescriptor)extension.getValue()).getNumber());
/*     */           break;
/*     */         case STRING:
/* 484 */           writer.writeString(descriptor.getNumber(), (String)extension.getValue());
/*     */           break;
/*     */         case GROUP:
/* 487 */           writer.writeGroup(descriptor.getNumber(), extension.getValue());
/*     */           break;
/*     */         case MESSAGE:
/* 490 */           writer.writeMessage(descriptor.getNumber(), extension.getValue());
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   Object findExtensionByNumber(ExtensionRegistryLite extensionRegistry, MessageLite defaultInstance, int number) {
/* 499 */     return ((ExtensionRegistry)extensionRegistry)
/* 500 */       .findExtensionByNumber(((Message)defaultInstance).getDescriptorForType(), number);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void parseLengthPrefixedMessageSetItem(Reader reader, Object extension, ExtensionRegistryLite extensionRegistry, FieldSet<Descriptors.FieldDescriptor> extensions) throws IOException {
/* 510 */     ExtensionRegistry.ExtensionInfo extensionInfo = (ExtensionRegistry.ExtensionInfo)extension;
/*     */     
/* 512 */     if (ExtensionRegistryLite.isEagerlyParseMessageSets()) {
/*     */       
/* 514 */       Object value = reader.readMessage(extensionInfo.defaultInstance.getClass(), extensionRegistry);
/* 515 */       extensions.setField(extensionInfo.descriptor, value);
/*     */     } else {
/* 517 */       extensions.setField(extensionInfo.descriptor, new LazyField(extensionInfo.defaultInstance, extensionRegistry, reader
/*     */             
/* 519 */             .readBytes()));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void parseMessageSetItem(ByteString data, Object extension, ExtensionRegistryLite extensionRegistry, FieldSet<Descriptors.FieldDescriptor> extensions) throws IOException {
/* 530 */     ExtensionRegistry.ExtensionInfo extensionInfo = (ExtensionRegistry.ExtensionInfo)extension;
/* 531 */     Object value = extensionInfo.defaultInstance.newBuilderForType().buildPartial();
/*     */     
/* 533 */     if (ExtensionRegistryLite.isEagerlyParseMessageSets()) {
/* 534 */       Reader reader = BinaryReader.newInstance(ByteBuffer.wrap(data.toByteArray()), true);
/* 535 */       Protobuf.getInstance().mergeFrom(value, reader, extensionRegistry);
/* 536 */       extensions.setField(extensionInfo.descriptor, value);
/*     */       
/* 538 */       if (reader.getFieldNumber() != Integer.MAX_VALUE) {
/* 539 */         throw InvalidProtocolBufferException.invalidEndTag();
/*     */       }
/*     */     } else {
/* 542 */       extensions.setField(extensionInfo.descriptor, new LazyField(extensionInfo.defaultInstance, extensionRegistry, data));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ExtensionSchemaFull.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */