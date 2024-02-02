/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Field;
/*      */ import java.util.Arrays;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import sun.misc.Unsafe;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ final class MessageSchema<T>
/*      */   implements Schema<T>
/*      */ {
/*      */   private static final int INTS_PER_FIELD = 3;
/*      */   private static final int OFFSET_BITS = 20;
/*      */   private static final int OFFSET_MASK = 1048575;
/*      */   private static final int FIELD_TYPE_MASK = 267386880;
/*      */   private static final int REQUIRED_MASK = 268435456;
/*      */   private static final int ENFORCE_UTF8_MASK = 536870912;
/*   92 */   private static final int[] EMPTY_INT_ARRAY = new int[0];
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final int ONEOF_TYPE_OFFSET = 51;
/*      */ 
/*      */ 
/*      */   
/*  101 */   private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int[] buffer;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Object[] objects;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int minFieldNumber;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int maxFieldNumber;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final MessageLite defaultInstance;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean hasExtensions;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean lite;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean proto3;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean useCachedSizeField;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int[] intArray;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int checkInitializedCount;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int repeatedFieldOffsetStart;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final NewInstanceSchema newInstanceSchema;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final ListFieldSchema listFieldSchema;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final UnknownFieldSchema<?, ?> unknownFieldSchema;
/*      */ 
/*      */ 
/*      */   
/*      */   private final ExtensionSchema<?> extensionSchema;
/*      */ 
/*      */ 
/*      */   
/*      */   private final MapFieldSchema mapFieldSchema;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private MessageSchema(int[] buffer, Object[] objects, int minFieldNumber, int maxFieldNumber, MessageLite defaultInstance, boolean proto3, boolean useCachedSizeField, int[] intArray, int checkInitialized, int mapFieldPositions, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
/*  190 */     this.buffer = buffer;
/*  191 */     this.objects = objects;
/*  192 */     this.minFieldNumber = minFieldNumber;
/*  193 */     this.maxFieldNumber = maxFieldNumber;
/*      */     
/*  195 */     this.lite = defaultInstance instanceof GeneratedMessageLite;
/*  196 */     this.proto3 = proto3;
/*  197 */     this.hasExtensions = (extensionSchema != null && extensionSchema.hasExtensions(defaultInstance));
/*  198 */     this.useCachedSizeField = useCachedSizeField;
/*      */     
/*  200 */     this.intArray = intArray;
/*  201 */     this.checkInitializedCount = checkInitialized;
/*  202 */     this.repeatedFieldOffsetStart = mapFieldPositions;
/*      */     
/*  204 */     this.newInstanceSchema = newInstanceSchema;
/*  205 */     this.listFieldSchema = listFieldSchema;
/*  206 */     this.unknownFieldSchema = unknownFieldSchema;
/*  207 */     this.extensionSchema = extensionSchema;
/*  208 */     this.defaultInstance = defaultInstance;
/*  209 */     this.mapFieldSchema = mapFieldSchema;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static <T> MessageSchema<T> newSchema(Class<T> messageClass, MessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
/*  220 */     if (messageInfo instanceof RawMessageInfo) {
/*  221 */       return newSchemaForRawMessageInfo((RawMessageInfo)messageInfo, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  230 */     return newSchemaForMessageInfo((StructuralMessageInfo)messageInfo, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static <T> MessageSchema<T> newSchemaForRawMessageInfo(RawMessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
/*      */     int oneofCount, minFieldNumber, maxFieldNumber, numEntries, mapFieldCount, checkInitialized, intArray[], objectsPosition;
/*  247 */     boolean isProto3 = (messageInfo.getSyntax() == ProtoSyntax.PROTO3);
/*      */     
/*  249 */     String info = messageInfo.getStringInfo();
/*  250 */     int length = info.length();
/*  251 */     int i = 0;
/*      */     
/*  253 */     int next = info.charAt(i++);
/*  254 */     if (next >= 55296) {
/*  255 */       int result = next & 0x1FFF;
/*  256 */       int shift = 13;
/*  257 */       while ((next = info.charAt(i++)) >= 55296) {
/*  258 */         result |= (next & 0x1FFF) << shift;
/*  259 */         shift += 13;
/*      */       } 
/*  261 */       next = result | next << shift;
/*      */     } 
/*  263 */     int flags = next;
/*      */     
/*  265 */     next = info.charAt(i++);
/*  266 */     if (next >= 55296) {
/*  267 */       int result = next & 0x1FFF;
/*  268 */       int shift = 13;
/*  269 */       while ((next = info.charAt(i++)) >= 55296) {
/*  270 */         result |= (next & 0x1FFF) << shift;
/*  271 */         shift += 13;
/*      */       } 
/*  273 */       next = result | next << shift;
/*      */     } 
/*  275 */     int fieldCount = next;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  287 */     if (fieldCount == 0) {
/*  288 */       oneofCount = 0;
/*  289 */       int hasBitsCount = 0;
/*  290 */       minFieldNumber = 0;
/*  291 */       maxFieldNumber = 0;
/*  292 */       numEntries = 0;
/*  293 */       mapFieldCount = 0;
/*  294 */       int repeatedFieldCount = 0;
/*  295 */       checkInitialized = 0;
/*  296 */       intArray = EMPTY_INT_ARRAY;
/*  297 */       objectsPosition = 0;
/*      */     } else {
/*  299 */       next = info.charAt(i++);
/*  300 */       if (next >= 55296) {
/*  301 */         int result = next & 0x1FFF;
/*  302 */         int shift = 13;
/*  303 */         while ((next = info.charAt(i++)) >= 55296) {
/*  304 */           result |= (next & 0x1FFF) << shift;
/*  305 */           shift += 13;
/*      */         } 
/*  307 */         next = result | next << shift;
/*      */       } 
/*  309 */       oneofCount = next;
/*      */       
/*  311 */       next = info.charAt(i++);
/*  312 */       if (next >= 55296) {
/*  313 */         int result = next & 0x1FFF;
/*  314 */         int shift = 13;
/*  315 */         while ((next = info.charAt(i++)) >= 55296) {
/*  316 */           result |= (next & 0x1FFF) << shift;
/*  317 */           shift += 13;
/*      */         } 
/*  319 */         next = result | next << shift;
/*      */       } 
/*  321 */       int hasBitsCount = next;
/*      */       
/*  323 */       next = info.charAt(i++);
/*  324 */       if (next >= 55296) {
/*  325 */         int result = next & 0x1FFF;
/*  326 */         int shift = 13;
/*  327 */         while ((next = info.charAt(i++)) >= 55296) {
/*  328 */           result |= (next & 0x1FFF) << shift;
/*  329 */           shift += 13;
/*      */         } 
/*  331 */         next = result | next << shift;
/*      */       } 
/*  333 */       minFieldNumber = next;
/*      */       
/*  335 */       next = info.charAt(i++);
/*  336 */       if (next >= 55296) {
/*  337 */         int result = next & 0x1FFF;
/*  338 */         int shift = 13;
/*  339 */         while ((next = info.charAt(i++)) >= 55296) {
/*  340 */           result |= (next & 0x1FFF) << shift;
/*  341 */           shift += 13;
/*      */         } 
/*  343 */         next = result | next << shift;
/*      */       } 
/*  345 */       maxFieldNumber = next;
/*      */       
/*  347 */       next = info.charAt(i++);
/*  348 */       if (next >= 55296) {
/*  349 */         int result = next & 0x1FFF;
/*  350 */         int shift = 13;
/*  351 */         while ((next = info.charAt(i++)) >= 55296) {
/*  352 */           result |= (next & 0x1FFF) << shift;
/*  353 */           shift += 13;
/*      */         } 
/*  355 */         next = result | next << shift;
/*      */       } 
/*  357 */       numEntries = next;
/*      */       
/*  359 */       next = info.charAt(i++);
/*  360 */       if (next >= 55296) {
/*  361 */         int result = next & 0x1FFF;
/*  362 */         int shift = 13;
/*  363 */         while ((next = info.charAt(i++)) >= 55296) {
/*  364 */           result |= (next & 0x1FFF) << shift;
/*  365 */           shift += 13;
/*      */         } 
/*  367 */         next = result | next << shift;
/*      */       } 
/*  369 */       mapFieldCount = next;
/*      */       
/*  371 */       next = info.charAt(i++);
/*  372 */       if (next >= 55296) {
/*  373 */         int result = next & 0x1FFF;
/*  374 */         int shift = 13;
/*  375 */         while ((next = info.charAt(i++)) >= 55296) {
/*  376 */           result |= (next & 0x1FFF) << shift;
/*  377 */           shift += 13;
/*      */         } 
/*  379 */         next = result | next << shift;
/*      */       } 
/*  381 */       int repeatedFieldCount = next;
/*      */       
/*  383 */       next = info.charAt(i++);
/*  384 */       if (next >= 55296) {
/*  385 */         int result = next & 0x1FFF;
/*  386 */         int shift = 13;
/*  387 */         while ((next = info.charAt(i++)) >= 55296) {
/*  388 */           result |= (next & 0x1FFF) << shift;
/*  389 */           shift += 13;
/*      */         } 
/*  391 */         next = result | next << shift;
/*      */       } 
/*  393 */       checkInitialized = next;
/*  394 */       intArray = new int[checkInitialized + mapFieldCount + repeatedFieldCount];
/*      */       
/*  396 */       objectsPosition = oneofCount * 2 + hasBitsCount;
/*      */     } 
/*      */     
/*  399 */     Unsafe unsafe = UNSAFE;
/*  400 */     Object[] messageInfoObjects = messageInfo.getObjects();
/*  401 */     int checkInitializedPosition = 0;
/*  402 */     Class<?> messageClass = messageInfo.getDefaultInstance().getClass();
/*  403 */     int[] buffer = new int[numEntries * 3];
/*  404 */     Object[] objects = new Object[numEntries * 2];
/*      */     
/*  406 */     int mapFieldIndex = checkInitialized;
/*  407 */     int repeatedFieldIndex = checkInitialized + mapFieldCount;
/*      */     
/*  409 */     int bufferIndex = 0;
/*  410 */     while (i < length) {
/*      */       int fieldOffset, presenceMaskShift, presenceFieldOffset;
/*      */ 
/*      */ 
/*      */       
/*  415 */       next = info.charAt(i++);
/*  416 */       if (next >= 55296) {
/*  417 */         int result = next & 0x1FFF;
/*  418 */         int shift = 13;
/*  419 */         while ((next = info.charAt(i++)) >= 55296) {
/*  420 */           result |= (next & 0x1FFF) << shift;
/*  421 */           shift += 13;
/*      */         } 
/*  423 */         next = result | next << shift;
/*      */       } 
/*  425 */       int fieldNumber = next;
/*      */       
/*  427 */       next = info.charAt(i++);
/*  428 */       if (next >= 55296) {
/*  429 */         int result = next & 0x1FFF;
/*  430 */         int shift = 13;
/*  431 */         while ((next = info.charAt(i++)) >= 55296) {
/*  432 */           result |= (next & 0x1FFF) << shift;
/*  433 */           shift += 13;
/*      */         } 
/*  435 */         next = result | next << shift;
/*      */       } 
/*  437 */       int fieldTypeWithExtraBits = next;
/*  438 */       int fieldType = fieldTypeWithExtraBits & 0xFF;
/*      */       
/*  440 */       if ((fieldTypeWithExtraBits & 0x400) != 0) {
/*  441 */         intArray[checkInitializedPosition++] = bufferIndex;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  449 */       if (fieldType >= 51) {
/*  450 */         Field oneofField, oneofCaseField; next = info.charAt(i++);
/*  451 */         if (next >= 55296) {
/*  452 */           int result = next & 0x1FFF;
/*  453 */           int shift = 13;
/*  454 */           while ((next = info.charAt(i++)) >= 55296) {
/*  455 */             result |= (next & 0x1FFF) << shift;
/*  456 */             shift += 13;
/*      */           } 
/*  458 */           next = result | next << shift;
/*      */         } 
/*  460 */         int oneofIndex = next;
/*      */         
/*  462 */         int oneofFieldType = fieldType - 51;
/*  463 */         if (oneofFieldType == 9 || oneofFieldType == 17) {
/*      */           
/*  465 */           objects[bufferIndex / 3 * 2 + 1] = messageInfoObjects[objectsPosition++];
/*  466 */         } else if (oneofFieldType == 12) {
/*      */           
/*  468 */           if ((flags & 0x1) == 1) {
/*  469 */             objects[bufferIndex / 3 * 2 + 1] = messageInfoObjects[objectsPosition++];
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/*  474 */         int index = oneofIndex * 2;
/*  475 */         Object o = messageInfoObjects[index];
/*  476 */         if (o instanceof Field) {
/*  477 */           oneofField = (Field)o;
/*      */         } else {
/*  479 */           oneofField = reflectField(messageClass, (String)o);
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  484 */           messageInfoObjects[index] = oneofField;
/*      */         } 
/*      */         
/*  487 */         fieldOffset = (int)unsafe.objectFieldOffset(oneofField);
/*      */ 
/*      */         
/*  490 */         index++;
/*  491 */         o = messageInfoObjects[index];
/*  492 */         if (o instanceof Field) {
/*  493 */           oneofCaseField = (Field)o;
/*      */         } else {
/*  495 */           oneofCaseField = reflectField(messageClass, (String)o);
/*  496 */           messageInfoObjects[index] = oneofCaseField;
/*      */         } 
/*      */         
/*  499 */         presenceFieldOffset = (int)unsafe.objectFieldOffset(oneofCaseField);
/*  500 */         presenceMaskShift = 0;
/*      */       } else {
/*  502 */         Field field = reflectField(messageClass, (String)messageInfoObjects[objectsPosition++]);
/*  503 */         if (fieldType == 9 || fieldType == 17) {
/*  504 */           objects[bufferIndex / 3 * 2 + 1] = field.getType();
/*  505 */         } else if (fieldType == 27 || fieldType == 49) {
/*      */           
/*  507 */           objects[bufferIndex / 3 * 2 + 1] = messageInfoObjects[objectsPosition++];
/*  508 */         } else if (fieldType == 12 || fieldType == 30 || fieldType == 44) {
/*      */ 
/*      */           
/*  511 */           if ((flags & 0x1) == 1) {
/*  512 */             objects[bufferIndex / 3 * 2 + 1] = messageInfoObjects[objectsPosition++];
/*      */           }
/*  514 */         } else if (fieldType == 50) {
/*  515 */           intArray[mapFieldIndex++] = bufferIndex;
/*  516 */           objects[bufferIndex / 3 * 2] = messageInfoObjects[objectsPosition++];
/*  517 */           if ((fieldTypeWithExtraBits & 0x800) != 0) {
/*  518 */             objects[bufferIndex / 3 * 2 + 1] = messageInfoObjects[objectsPosition++];
/*      */           }
/*      */         } 
/*      */         
/*  522 */         fieldOffset = (int)unsafe.objectFieldOffset(field);
/*  523 */         if ((flags & 0x1) == 1 && fieldType <= 17) {
/*  524 */           Field hasBitsField; next = info.charAt(i++);
/*  525 */           if (next >= 55296) {
/*  526 */             int result = next & 0x1FFF;
/*  527 */             int shift = 13;
/*  528 */             while ((next = info.charAt(i++)) >= 55296) {
/*  529 */               result |= (next & 0x1FFF) << shift;
/*  530 */               shift += 13;
/*      */             } 
/*  532 */             next = result | next << shift;
/*      */           } 
/*  534 */           int hasBitsIndex = next;
/*      */ 
/*      */           
/*  537 */           int index = oneofCount * 2 + hasBitsIndex / 32;
/*  538 */           Object o = messageInfoObjects[index];
/*  539 */           if (o instanceof Field) {
/*  540 */             hasBitsField = (Field)o;
/*      */           } else {
/*  542 */             hasBitsField = reflectField(messageClass, (String)o);
/*  543 */             messageInfoObjects[index] = hasBitsField;
/*      */           } 
/*      */           
/*  546 */           presenceFieldOffset = (int)unsafe.objectFieldOffset(hasBitsField);
/*  547 */           presenceMaskShift = hasBitsIndex % 32;
/*      */         } else {
/*  549 */           presenceFieldOffset = 0;
/*  550 */           presenceMaskShift = 0;
/*      */         } 
/*      */         
/*  553 */         if (fieldType >= 18 && fieldType <= 49)
/*      */         {
/*      */           
/*  556 */           intArray[repeatedFieldIndex++] = fieldOffset;
/*      */         }
/*      */       } 
/*      */       
/*  560 */       buffer[bufferIndex++] = fieldNumber;
/*  561 */       buffer[bufferIndex++] = (((fieldTypeWithExtraBits & 0x200) != 0) ? true : false) | (((fieldTypeWithExtraBits & 0x100) != 0) ? true : false) | fieldType << 20 | fieldOffset;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  566 */       buffer[bufferIndex++] = presenceMaskShift << 20 | presenceFieldOffset;
/*      */     } 
/*      */     
/*  569 */     return new MessageSchema<>(buffer, objects, minFieldNumber, maxFieldNumber, messageInfo
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  574 */         .getDefaultInstance(), isProto3, false, intArray, checkInitialized, checkInitialized + mapFieldCount, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Field reflectField(Class<?> messageClass, String fieldName) {
/*      */     try {
/*  589 */       return messageClass.getDeclaredField(fieldName);
/*  590 */     } catch (NoSuchFieldException e) {
/*      */ 
/*      */       
/*  593 */       Field[] fields = messageClass.getDeclaredFields();
/*  594 */       for (Field field : fields) {
/*  595 */         if (fieldName.equals(field.getName())) {
/*  596 */           return field;
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  602 */       throw new RuntimeException("Field " + fieldName + " for " + messageClass
/*      */ 
/*      */ 
/*      */           
/*  606 */           .getName() + " not found. Known fields are " + 
/*      */           
/*  608 */           Arrays.toString(fields));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static <T> MessageSchema<T> newSchemaForMessageInfo(StructuralMessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
/*      */     int minFieldNumber, maxFieldNumber;
/*  619 */     boolean isProto3 = (messageInfo.getSyntax() == ProtoSyntax.PROTO3);
/*  620 */     FieldInfo[] fis = messageInfo.getFields();
/*      */ 
/*      */     
/*  623 */     if (fis.length == 0) {
/*  624 */       minFieldNumber = 0;
/*  625 */       maxFieldNumber = 0;
/*      */     } else {
/*  627 */       minFieldNumber = fis[0].getFieldNumber();
/*  628 */       maxFieldNumber = fis[fis.length - 1].getFieldNumber();
/*      */     } 
/*      */     
/*  631 */     int numEntries = fis.length;
/*      */     
/*  633 */     int[] buffer = new int[numEntries * 3];
/*  634 */     Object[] objects = new Object[numEntries * 2];
/*      */     
/*  636 */     int mapFieldCount = 0;
/*  637 */     int repeatedFieldCount = 0;
/*  638 */     for (FieldInfo fi : fis) {
/*  639 */       if (fi.getType() == FieldType.MAP) {
/*  640 */         mapFieldCount++;
/*  641 */       } else if (fi.getType().id() >= 18 && fi.getType().id() <= 49) {
/*      */ 
/*      */         
/*  644 */         repeatedFieldCount++;
/*      */       } 
/*      */     } 
/*  647 */     int[] mapFieldPositions = (mapFieldCount > 0) ? new int[mapFieldCount] : null;
/*  648 */     int[] repeatedFieldOffsets = (repeatedFieldCount > 0) ? new int[repeatedFieldCount] : null;
/*  649 */     mapFieldCount = 0;
/*  650 */     repeatedFieldCount = 0;
/*      */     
/*  652 */     int[] checkInitialized = messageInfo.getCheckInitialized();
/*  653 */     if (checkInitialized == null) {
/*  654 */       checkInitialized = EMPTY_INT_ARRAY;
/*      */     }
/*  656 */     int checkInitializedIndex = 0;
/*      */     
/*  658 */     int fieldIndex = 0;
/*  659 */     for (int bufferIndex = 0; fieldIndex < fis.length; bufferIndex += 3) {
/*  660 */       FieldInfo fi = fis[fieldIndex];
/*  661 */       int fieldNumber = fi.getFieldNumber();
/*      */ 
/*      */ 
/*      */       
/*  665 */       storeFieldData(fi, buffer, bufferIndex, isProto3, objects);
/*      */ 
/*      */       
/*  668 */       if (checkInitializedIndex < checkInitialized.length && checkInitialized[checkInitializedIndex] == fieldNumber)
/*      */       {
/*  670 */         checkInitialized[checkInitializedIndex++] = bufferIndex;
/*      */       }
/*      */       
/*  673 */       if (fi.getType() == FieldType.MAP) {
/*  674 */         mapFieldPositions[mapFieldCount++] = bufferIndex;
/*  675 */       } else if (fi.getType().id() >= 18 && fi.getType().id() <= 49) {
/*      */ 
/*      */         
/*  678 */         repeatedFieldOffsets[repeatedFieldCount++] = 
/*  679 */           (int)UnsafeUtil.objectFieldOffset(fi.getField());
/*      */       } 
/*      */       
/*  682 */       fieldIndex++;
/*      */     } 
/*      */     
/*  685 */     if (mapFieldPositions == null) {
/*  686 */       mapFieldPositions = EMPTY_INT_ARRAY;
/*      */     }
/*  688 */     if (repeatedFieldOffsets == null) {
/*  689 */       repeatedFieldOffsets = EMPTY_INT_ARRAY;
/*      */     }
/*  691 */     int[] combined = new int[checkInitialized.length + mapFieldPositions.length + repeatedFieldOffsets.length];
/*      */     
/*  693 */     System.arraycopy(checkInitialized, 0, combined, 0, checkInitialized.length);
/*  694 */     System.arraycopy(mapFieldPositions, 0, combined, checkInitialized.length, mapFieldPositions.length);
/*      */     
/*  696 */     System.arraycopy(repeatedFieldOffsets, 0, combined, checkInitialized.length + mapFieldPositions.length, repeatedFieldOffsets.length);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  703 */     return new MessageSchema<>(buffer, objects, minFieldNumber, maxFieldNumber, messageInfo
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  708 */         .getDefaultInstance(), isProto3, true, combined, checkInitialized.length, checkInitialized.length + mapFieldPositions.length, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void storeFieldData(FieldInfo fi, int[] buffer, int bufferIndex, boolean proto3, Object[] objects) {
/*      */     int fieldOffset, typeId, presenceMaskShift, presenceFieldOffset;
/*  728 */     OneofInfo oneof = fi.getOneof();
/*  729 */     if (oneof != null) {
/*  730 */       typeId = fi.getType().id() + 51;
/*  731 */       fieldOffset = (int)UnsafeUtil.objectFieldOffset(oneof.getValueField());
/*  732 */       presenceFieldOffset = (int)UnsafeUtil.objectFieldOffset(oneof.getCaseField());
/*  733 */       presenceMaskShift = 0;
/*      */     } else {
/*  735 */       FieldType type = fi.getType();
/*  736 */       fieldOffset = (int)UnsafeUtil.objectFieldOffset(fi.getField());
/*  737 */       typeId = type.id();
/*  738 */       if (!proto3 && !type.isList() && !type.isMap()) {
/*  739 */         presenceFieldOffset = (int)UnsafeUtil.objectFieldOffset(fi.getPresenceField());
/*  740 */         presenceMaskShift = Integer.numberOfTrailingZeros(fi.getPresenceMask());
/*      */       }
/*  742 */       else if (fi.getCachedSizeField() == null) {
/*  743 */         presenceFieldOffset = 0;
/*  744 */         presenceMaskShift = 0;
/*      */       } else {
/*  746 */         presenceFieldOffset = (int)UnsafeUtil.objectFieldOffset(fi.getCachedSizeField());
/*  747 */         presenceMaskShift = 0;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  752 */     buffer[bufferIndex] = fi.getFieldNumber();
/*  753 */     buffer[bufferIndex + 1] = (
/*  754 */       fi.isEnforceUtf8() ? true : false) | (
/*  755 */       fi.isRequired() ? true : false) | typeId << 20 | fieldOffset;
/*      */ 
/*      */     
/*  758 */     buffer[bufferIndex + 2] = presenceMaskShift << 20 | presenceFieldOffset;
/*      */     
/*  760 */     Object<?> messageFieldClass = (Object<?>)fi.getMessageFieldClass();
/*  761 */     if (fi.getMapDefaultEntry() != null) {
/*  762 */       objects[bufferIndex / 3 * 2] = fi.getMapDefaultEntry();
/*  763 */       if (messageFieldClass != null) {
/*  764 */         objects[bufferIndex / 3 * 2 + 1] = messageFieldClass;
/*  765 */       } else if (fi.getEnumVerifier() != null) {
/*  766 */         objects[bufferIndex / 3 * 2 + 1] = fi.getEnumVerifier();
/*      */       }
/*      */     
/*  769 */     } else if (messageFieldClass != null) {
/*  770 */       objects[bufferIndex / 3 * 2 + 1] = messageFieldClass;
/*  771 */     } else if (fi.getEnumVerifier() != null) {
/*  772 */       objects[bufferIndex / 3 * 2 + 1] = fi.getEnumVerifier();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public T newInstance() {
/*  780 */     return (T)this.newInstanceSchema.newInstance(this.defaultInstance);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(T message, T other) {
/*  785 */     int bufferLength = this.buffer.length;
/*  786 */     for (int pos = 0; pos < bufferLength; pos += 3) {
/*  787 */       if (!equals(message, other, pos)) {
/*  788 */         return false;
/*      */       }
/*      */     } 
/*      */     
/*  792 */     Object messageUnknown = this.unknownFieldSchema.getFromMessage(message);
/*  793 */     Object otherUnknown = this.unknownFieldSchema.getFromMessage(other);
/*  794 */     if (!messageUnknown.equals(otherUnknown)) {
/*  795 */       return false;
/*      */     }
/*      */     
/*  798 */     if (this.hasExtensions) {
/*  799 */       FieldSet<?> messageExtensions = this.extensionSchema.getExtensions(message);
/*  800 */       FieldSet<?> otherExtensions = this.extensionSchema.getExtensions(other);
/*  801 */       return messageExtensions.equals(otherExtensions);
/*      */     } 
/*  803 */     return true;
/*      */   }
/*      */   
/*      */   private boolean equals(T message, T other, int pos) {
/*  807 */     int typeAndOffset = typeAndOffsetAt(pos);
/*  808 */     long offset = offset(typeAndOffset);
/*      */     
/*  810 */     switch (type(typeAndOffset)) {
/*      */       case 0:
/*  812 */         return (arePresentForEquals(message, other, pos) && 
/*  813 */           Double.doubleToLongBits(UnsafeUtil.getDouble(message, offset)) == 
/*  814 */           Double.doubleToLongBits(UnsafeUtil.getDouble(other, offset)));
/*      */       case 1:
/*  816 */         return (arePresentForEquals(message, other, pos) && 
/*  817 */           Float.floatToIntBits(UnsafeUtil.getFloat(message, offset)) == 
/*  818 */           Float.floatToIntBits(UnsafeUtil.getFloat(other, offset)));
/*      */       case 2:
/*  820 */         return (arePresentForEquals(message, other, pos) && 
/*  821 */           UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset));
/*      */       case 3:
/*  823 */         return (arePresentForEquals(message, other, pos) && 
/*  824 */           UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset));
/*      */       case 4:
/*  826 */         return (arePresentForEquals(message, other, pos) && 
/*  827 */           UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset));
/*      */       case 5:
/*  829 */         return (arePresentForEquals(message, other, pos) && 
/*  830 */           UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset));
/*      */       case 6:
/*  832 */         return (arePresentForEquals(message, other, pos) && 
/*  833 */           UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset));
/*      */       case 7:
/*  835 */         return (arePresentForEquals(message, other, pos) && 
/*  836 */           UnsafeUtil.getBoolean(message, offset) == UnsafeUtil.getBoolean(other, offset));
/*      */       case 8:
/*  838 */         return (arePresentForEquals(message, other, pos) && 
/*  839 */           SchemaUtil.safeEquals(
/*  840 */             UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset)));
/*      */       case 9:
/*  842 */         return (arePresentForEquals(message, other, pos) && 
/*  843 */           SchemaUtil.safeEquals(
/*  844 */             UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset)));
/*      */       case 10:
/*  846 */         return (arePresentForEquals(message, other, pos) && 
/*  847 */           SchemaUtil.safeEquals(
/*  848 */             UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset)));
/*      */       case 11:
/*  850 */         return (arePresentForEquals(message, other, pos) && 
/*  851 */           UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset));
/*      */       case 12:
/*  853 */         return (arePresentForEquals(message, other, pos) && 
/*  854 */           UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset));
/*      */       case 13:
/*  856 */         return (arePresentForEquals(message, other, pos) && 
/*  857 */           UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset));
/*      */       case 14:
/*  859 */         return (arePresentForEquals(message, other, pos) && 
/*  860 */           UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset));
/*      */       case 15:
/*  862 */         return (arePresentForEquals(message, other, pos) && 
/*  863 */           UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset));
/*      */       case 16:
/*  865 */         return (arePresentForEquals(message, other, pos) && 
/*  866 */           UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset));
/*      */       case 17:
/*  868 */         return (arePresentForEquals(message, other, pos) && 
/*  869 */           SchemaUtil.safeEquals(
/*  870 */             UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset)));
/*      */       
/*      */       case 18:
/*      */       case 19:
/*      */       case 20:
/*      */       case 21:
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*      */       case 28:
/*      */       case 29:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/*      */       case 35:
/*      */       case 36:
/*      */       case 37:
/*      */       case 38:
/*      */       case 39:
/*      */       case 40:
/*      */       case 41:
/*      */       case 42:
/*      */       case 43:
/*      */       case 44:
/*      */       case 45:
/*      */       case 46:
/*      */       case 47:
/*      */       case 48:
/*      */       case 49:
/*  904 */         return SchemaUtil.safeEquals(
/*  905 */             UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
/*      */       case 50:
/*  907 */         return SchemaUtil.safeEquals(
/*  908 */             UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/*      */       case 58:
/*      */       case 59:
/*      */       case 60:
/*      */       case 61:
/*      */       case 62:
/*      */       case 63:
/*      */       case 64:
/*      */       case 65:
/*      */       case 66:
/*      */       case 67:
/*      */       case 68:
/*  927 */         return (isOneofCaseEqual(message, other, pos) && 
/*  928 */           SchemaUtil.safeEquals(
/*  929 */             UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset)));
/*      */     } 
/*      */     
/*  932 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int hashCode(T message) {
/*  938 */     int hashCode = 0;
/*  939 */     int bufferLength = this.buffer.length;
/*  940 */     for (int pos = 0; pos < bufferLength; pos += 3) {
/*  941 */       int protoHash; Object submessage; int typeAndOffset = typeAndOffsetAt(pos);
/*  942 */       int entryNumber = numberAt(pos);
/*      */       
/*  944 */       long offset = offset(typeAndOffset);
/*      */       
/*  946 */       switch (type(typeAndOffset)) {
/*      */ 
/*      */         
/*      */         case 0:
/*  950 */           hashCode = hashCode * 53 + Internal.hashLong(
/*  951 */               Double.doubleToLongBits(UnsafeUtil.getDouble(message, offset)));
/*      */           break;
/*      */         case 1:
/*  954 */           hashCode = hashCode * 53 + Float.floatToIntBits(UnsafeUtil.getFloat(message, offset));
/*      */           break;
/*      */         case 2:
/*  957 */           hashCode = hashCode * 53 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
/*      */           break;
/*      */         case 3:
/*  960 */           hashCode = hashCode * 53 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
/*      */           break;
/*      */         case 4:
/*  963 */           hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
/*      */           break;
/*      */         case 5:
/*  966 */           hashCode = hashCode * 53 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
/*      */           break;
/*      */         case 6:
/*  969 */           hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
/*      */           break;
/*      */         case 7:
/*  972 */           hashCode = hashCode * 53 + Internal.hashBoolean(UnsafeUtil.getBoolean(message, offset));
/*      */           break;
/*      */         case 8:
/*  975 */           hashCode = hashCode * 53 + ((String)UnsafeUtil.getObject(message, offset)).hashCode();
/*      */           break;
/*      */         
/*      */         case 9:
/*  979 */           protoHash = 37;
/*  980 */           submessage = UnsafeUtil.getObject(message, offset);
/*  981 */           if (submessage != null) {
/*  982 */             protoHash = submessage.hashCode();
/*      */           }
/*  984 */           hashCode = 53 * hashCode + protoHash;
/*      */           break;
/*      */         
/*      */         case 10:
/*  988 */           hashCode = hashCode * 53 + UnsafeUtil.getObject(message, offset).hashCode();
/*      */           break;
/*      */         case 11:
/*  991 */           hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
/*      */           break;
/*      */         case 12:
/*  994 */           hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
/*      */           break;
/*      */         case 13:
/*  997 */           hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
/*      */           break;
/*      */         case 14:
/* 1000 */           hashCode = hashCode * 53 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
/*      */           break;
/*      */         case 15:
/* 1003 */           hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
/*      */           break;
/*      */         case 16:
/* 1006 */           hashCode = hashCode * 53 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
/*      */           break;
/*      */ 
/*      */         
/*      */         case 17:
/* 1011 */           protoHash = 37;
/* 1012 */           submessage = UnsafeUtil.getObject(message, offset);
/* 1013 */           if (submessage != null) {
/* 1014 */             protoHash = submessage.hashCode();
/*      */           }
/* 1016 */           hashCode = 53 * hashCode + protoHash;
/*      */           break;
/*      */         
/*      */         case 18:
/*      */         case 19:
/*      */         case 20:
/*      */         case 21:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 26:
/*      */         case 27:
/*      */         case 28:
/*      */         case 29:
/*      */         case 30:
/*      */         case 31:
/*      */         case 32:
/*      */         case 33:
/*      */         case 34:
/*      */         case 35:
/*      */         case 36:
/*      */         case 37:
/*      */         case 38:
/*      */         case 39:
/*      */         case 40:
/*      */         case 41:
/*      */         case 42:
/*      */         case 43:
/*      */         case 44:
/*      */         case 45:
/*      */         case 46:
/*      */         case 47:
/*      */         case 48:
/*      */         case 49:
/* 1051 */           hashCode = hashCode * 53 + UnsafeUtil.getObject(message, offset).hashCode();
/*      */           break;
/*      */         case 50:
/* 1054 */           hashCode = hashCode * 53 + UnsafeUtil.getObject(message, offset).hashCode();
/*      */           break;
/*      */         case 51:
/* 1057 */           if (isOneofPresent(message, entryNumber, pos))
/*      */           {
/*      */             
/* 1060 */             hashCode = hashCode * 53 + Internal.hashLong(Double.doubleToLongBits(oneofDoubleAt(message, offset)));
/*      */           }
/*      */           break;
/*      */         case 52:
/* 1064 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1065 */             hashCode = hashCode * 53 + Float.floatToIntBits(oneofFloatAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 53:
/* 1069 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1070 */             hashCode = hashCode * 53 + Internal.hashLong(oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 54:
/* 1074 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1075 */             hashCode = hashCode * 53 + Internal.hashLong(oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 55:
/* 1079 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1080 */             hashCode = hashCode * 53 + oneofIntAt(message, offset);
/*      */           }
/*      */           break;
/*      */         case 56:
/* 1084 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1085 */             hashCode = hashCode * 53 + Internal.hashLong(oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 57:
/* 1089 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1090 */             hashCode = hashCode * 53 + oneofIntAt(message, offset);
/*      */           }
/*      */           break;
/*      */         case 58:
/* 1094 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1095 */             hashCode = hashCode * 53 + Internal.hashBoolean(oneofBooleanAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 59:
/* 1099 */           if (isOneofPresent(message, entryNumber, pos))
/*      */           {
/* 1101 */             hashCode = hashCode * 53 + ((String)UnsafeUtil.getObject(message, offset)).hashCode();
/*      */           }
/*      */           break;
/*      */         case 60:
/* 1105 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1106 */             Object object = UnsafeUtil.getObject(message, offset);
/* 1107 */             hashCode = 53 * hashCode + object.hashCode();
/*      */           } 
/*      */           break;
/*      */         case 61:
/* 1111 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1112 */             hashCode = hashCode * 53 + UnsafeUtil.getObject(message, offset).hashCode();
/*      */           }
/*      */           break;
/*      */         case 62:
/* 1116 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1117 */             hashCode = hashCode * 53 + oneofIntAt(message, offset);
/*      */           }
/*      */           break;
/*      */         case 63:
/* 1121 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1122 */             hashCode = hashCode * 53 + oneofIntAt(message, offset);
/*      */           }
/*      */           break;
/*      */         case 64:
/* 1126 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1127 */             hashCode = hashCode * 53 + oneofIntAt(message, offset);
/*      */           }
/*      */           break;
/*      */         case 65:
/* 1131 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1132 */             hashCode = hashCode * 53 + Internal.hashLong(oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 66:
/* 1136 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1137 */             hashCode = hashCode * 53 + oneofIntAt(message, offset);
/*      */           }
/*      */           break;
/*      */         case 67:
/* 1141 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1142 */             hashCode = hashCode * 53 + Internal.hashLong(oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 68:
/* 1146 */           if (isOneofPresent(message, entryNumber, pos)) {
/* 1147 */             Object object = UnsafeUtil.getObject(message, offset);
/* 1148 */             hashCode = 53 * hashCode + object.hashCode();
/*      */           } 
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     } 
/* 1157 */     hashCode = hashCode * 53 + this.unknownFieldSchema.getFromMessage(message).hashCode();
/*      */     
/* 1159 */     if (this.hasExtensions) {
/* 1160 */       hashCode = hashCode * 53 + this.extensionSchema.getExtensions(message).hashCode();
/*      */     }
/*      */     
/* 1163 */     return hashCode;
/*      */   }
/*      */ 
/*      */   
/*      */   public void mergeFrom(T message, T other) {
/* 1168 */     if (other == null) {
/* 1169 */       throw new NullPointerException();
/*      */     }
/* 1171 */     for (int i = 0; i < this.buffer.length; i += 3)
/*      */     {
/* 1173 */       mergeSingleField(message, other, i);
/*      */     }
/*      */     
/* 1176 */     SchemaUtil.mergeUnknownFields(this.unknownFieldSchema, message, other);
/*      */     
/* 1178 */     if (this.hasExtensions) {
/* 1179 */       SchemaUtil.mergeExtensions(this.extensionSchema, message, other);
/*      */     }
/*      */   }
/*      */   
/*      */   private void mergeSingleField(T message, T other, int pos) {
/* 1184 */     int typeAndOffset = typeAndOffsetAt(pos);
/* 1185 */     long offset = offset(typeAndOffset);
/* 1186 */     int number = numberAt(pos);
/*      */     
/* 1188 */     switch (type(typeAndOffset)) {
/*      */       case 0:
/* 1190 */         if (isFieldPresent(other, pos)) {
/* 1191 */           UnsafeUtil.putDouble(message, offset, UnsafeUtil.getDouble(other, offset));
/* 1192 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 1:
/* 1196 */         if (isFieldPresent(other, pos)) {
/* 1197 */           UnsafeUtil.putFloat(message, offset, UnsafeUtil.getFloat(other, offset));
/* 1198 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 2:
/* 1202 */         if (isFieldPresent(other, pos)) {
/* 1203 */           UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
/* 1204 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 3:
/* 1208 */         if (isFieldPresent(other, pos)) {
/* 1209 */           UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
/* 1210 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 4:
/* 1214 */         if (isFieldPresent(other, pos)) {
/* 1215 */           UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
/* 1216 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 5:
/* 1220 */         if (isFieldPresent(other, pos)) {
/* 1221 */           UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
/* 1222 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 6:
/* 1226 */         if (isFieldPresent(other, pos)) {
/* 1227 */           UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
/* 1228 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 7:
/* 1232 */         if (isFieldPresent(other, pos)) {
/* 1233 */           UnsafeUtil.putBoolean(message, offset, UnsafeUtil.getBoolean(other, offset));
/* 1234 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 8:
/* 1238 */         if (isFieldPresent(other, pos)) {
/* 1239 */           UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
/* 1240 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 9:
/* 1244 */         mergeMessage(message, other, pos);
/*      */         break;
/*      */       case 10:
/* 1247 */         if (isFieldPresent(other, pos)) {
/* 1248 */           UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
/* 1249 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 11:
/* 1253 */         if (isFieldPresent(other, pos)) {
/* 1254 */           UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
/* 1255 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 12:
/* 1259 */         if (isFieldPresent(other, pos)) {
/* 1260 */           UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
/* 1261 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 13:
/* 1265 */         if (isFieldPresent(other, pos)) {
/* 1266 */           UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
/* 1267 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 14:
/* 1271 */         if (isFieldPresent(other, pos)) {
/* 1272 */           UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
/* 1273 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 15:
/* 1277 */         if (isFieldPresent(other, pos)) {
/* 1278 */           UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
/* 1279 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 16:
/* 1283 */         if (isFieldPresent(other, pos)) {
/* 1284 */           UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
/* 1285 */           setFieldPresent(message, pos);
/*      */         } 
/*      */         break;
/*      */       case 17:
/* 1289 */         mergeMessage(message, other, pos);
/*      */         break;
/*      */       case 18:
/*      */       case 19:
/*      */       case 20:
/*      */       case 21:
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*      */       case 28:
/*      */       case 29:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/*      */       case 35:
/*      */       case 36:
/*      */       case 37:
/*      */       case 38:
/*      */       case 39:
/*      */       case 40:
/*      */       case 41:
/*      */       case 42:
/*      */       case 43:
/*      */       case 44:
/*      */       case 45:
/*      */       case 46:
/*      */       case 47:
/*      */       case 48:
/*      */       case 49:
/* 1323 */         this.listFieldSchema.mergeListsAt(message, other, offset);
/*      */         break;
/*      */       case 50:
/* 1326 */         SchemaUtil.mergeMap(this.mapFieldSchema, message, other, offset);
/*      */         break;
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/*      */       case 58:
/*      */       case 59:
/* 1337 */         if (isOneofPresent(other, number, pos)) {
/* 1338 */           UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
/* 1339 */           setOneofPresent(message, number, pos);
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 60:
/* 1344 */         mergeOneofMessage(message, other, pos);
/*      */         break;
/*      */       case 61:
/*      */       case 62:
/*      */       case 63:
/*      */       case 64:
/*      */       case 65:
/*      */       case 66:
/*      */       case 67:
/* 1353 */         if (isOneofPresent(other, number, pos)) {
/* 1354 */           UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
/* 1355 */           setOneofPresent(message, number, pos);
/*      */         } 
/*      */         break;
/*      */       case 68:
/* 1359 */         mergeOneofMessage(message, other, pos);
/*      */         break;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void mergeMessage(T message, T other, int pos) {
/* 1367 */     int typeAndOffset = typeAndOffsetAt(pos);
/* 1368 */     long offset = offset(typeAndOffset);
/*      */     
/* 1370 */     if (!isFieldPresent(other, pos)) {
/*      */       return;
/*      */     }
/*      */     
/* 1374 */     Object mine = UnsafeUtil.getObject(message, offset);
/* 1375 */     Object theirs = UnsafeUtil.getObject(other, offset);
/* 1376 */     if (mine != null && theirs != null) {
/* 1377 */       Object merged = Internal.mergeMessage(mine, theirs);
/* 1378 */       UnsafeUtil.putObject(message, offset, merged);
/* 1379 */       setFieldPresent(message, pos);
/* 1380 */     } else if (theirs != null) {
/* 1381 */       UnsafeUtil.putObject(message, offset, theirs);
/* 1382 */       setFieldPresent(message, pos);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void mergeOneofMessage(T message, T other, int pos) {
/* 1387 */     int typeAndOffset = typeAndOffsetAt(pos);
/* 1388 */     int number = numberAt(pos);
/* 1389 */     long offset = offset(typeAndOffset);
/*      */     
/* 1391 */     if (!isOneofPresent(other, number, pos)) {
/*      */       return;
/*      */     }
/*      */     
/* 1395 */     Object mine = UnsafeUtil.getObject(message, offset);
/* 1396 */     Object theirs = UnsafeUtil.getObject(other, offset);
/* 1397 */     if (mine != null && theirs != null) {
/* 1398 */       Object merged = Internal.mergeMessage(mine, theirs);
/* 1399 */       UnsafeUtil.putObject(message, offset, merged);
/* 1400 */       setOneofPresent(message, number, pos);
/* 1401 */     } else if (theirs != null) {
/* 1402 */       UnsafeUtil.putObject(message, offset, theirs);
/* 1403 */       setOneofPresent(message, number, pos);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public int getSerializedSize(T message) {
/* 1409 */     return this.proto3 ? getSerializedSizeProto3(message) : getSerializedSizeProto2(message);
/*      */   }
/*      */ 
/*      */   
/*      */   private int getSerializedSizeProto2(T message) {
/* 1414 */     int size = 0;
/*      */     
/* 1416 */     Unsafe unsafe = UNSAFE;
/* 1417 */     int currentPresenceFieldOffset = -1;
/* 1418 */     int currentPresenceField = 0;
/* 1419 */     for (int i = 0; i < this.buffer.length; i += 3) {
/* 1420 */       int fieldSize, typeAndOffset = typeAndOffsetAt(i);
/* 1421 */       int number = numberAt(i);
/*      */       
/* 1423 */       int fieldType = type(typeAndOffset);
/* 1424 */       int presenceMaskAndOffset = 0;
/* 1425 */       int presenceMask = 0;
/* 1426 */       if (fieldType <= 17) {
/* 1427 */         presenceMaskAndOffset = this.buffer[i + 2];
/* 1428 */         int presenceFieldOffset = presenceMaskAndOffset & 0xFFFFF;
/* 1429 */         presenceMask = 1 << presenceMaskAndOffset >>> 20;
/* 1430 */         if (presenceFieldOffset != currentPresenceFieldOffset) {
/* 1431 */           currentPresenceFieldOffset = presenceFieldOffset;
/* 1432 */           currentPresenceField = unsafe.getInt(message, presenceFieldOffset);
/*      */         } 
/* 1434 */       } else if (this.useCachedSizeField && fieldType >= FieldType.DOUBLE_LIST_PACKED
/* 1435 */         .id() && fieldType <= FieldType.SINT64_LIST_PACKED
/* 1436 */         .id()) {
/* 1437 */         presenceMaskAndOffset = this.buffer[i + 2] & 0xFFFFF;
/*      */       } 
/*      */       
/* 1440 */       long offset = offset(typeAndOffset);
/*      */       
/* 1442 */       switch (fieldType) {
/*      */         case 0:
/* 1444 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1445 */             size += CodedOutputStream.computeDoubleSize(number, 0.0D);
/*      */           }
/*      */           break;
/*      */         case 1:
/* 1449 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1450 */             size += CodedOutputStream.computeFloatSize(number, 0.0F);
/*      */           }
/*      */           break;
/*      */         case 2:
/* 1454 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1455 */             size += CodedOutputStream.computeInt64Size(number, unsafe.getLong(message, offset));
/*      */           }
/*      */           break;
/*      */         case 3:
/* 1459 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1460 */             size += CodedOutputStream.computeUInt64Size(number, unsafe.getLong(message, offset));
/*      */           }
/*      */           break;
/*      */         case 4:
/* 1464 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1465 */             size += CodedOutputStream.computeInt32Size(number, unsafe.getInt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 5:
/* 1469 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1470 */             size += CodedOutputStream.computeFixed64Size(number, 0L);
/*      */           }
/*      */           break;
/*      */         case 6:
/* 1474 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1475 */             size += CodedOutputStream.computeFixed32Size(number, 0);
/*      */           }
/*      */           break;
/*      */         case 7:
/* 1479 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1480 */             size += CodedOutputStream.computeBoolSize(number, true);
/*      */           }
/*      */           break;
/*      */         case 8:
/* 1484 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1485 */             Object value = unsafe.getObject(message, offset);
/* 1486 */             if (value instanceof ByteString) {
/* 1487 */               size += CodedOutputStream.computeBytesSize(number, (ByteString)value); break;
/*      */             } 
/* 1489 */             size += CodedOutputStream.computeStringSize(number, (String)value);
/*      */           } 
/*      */           break;
/*      */         
/*      */         case 9:
/* 1494 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1495 */             Object value = unsafe.getObject(message, offset);
/* 1496 */             size += SchemaUtil.computeSizeMessage(number, value, getMessageFieldSchema(i));
/*      */           } 
/*      */           break;
/*      */         case 10:
/* 1500 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1501 */             ByteString value = (ByteString)unsafe.getObject(message, offset);
/* 1502 */             size += CodedOutputStream.computeBytesSize(number, value);
/*      */           } 
/*      */           break;
/*      */         case 11:
/* 1506 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1507 */             size += CodedOutputStream.computeUInt32Size(number, unsafe.getInt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 12:
/* 1511 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1512 */             size += CodedOutputStream.computeEnumSize(number, unsafe.getInt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 13:
/* 1516 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1517 */             size += CodedOutputStream.computeSFixed32Size(number, 0);
/*      */           }
/*      */           break;
/*      */         case 14:
/* 1521 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1522 */             size += CodedOutputStream.computeSFixed64Size(number, 0L);
/*      */           }
/*      */           break;
/*      */         case 15:
/* 1526 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1527 */             size += CodedOutputStream.computeSInt32Size(number, unsafe.getInt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 16:
/* 1531 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1532 */             size += CodedOutputStream.computeSInt64Size(number, unsafe.getLong(message, offset));
/*      */           }
/*      */           break;
/*      */         case 17:
/* 1536 */           if ((currentPresenceField & presenceMask) != 0) {
/* 1537 */             size += 
/* 1538 */               CodedOutputStream.computeGroupSize(number, (MessageLite)unsafe
/*      */                 
/* 1540 */                 .getObject(message, offset), 
/* 1541 */                 getMessageFieldSchema(i));
/*      */           }
/*      */           break;
/*      */         case 18:
/* 1545 */           size += 
/* 1546 */             SchemaUtil.computeSizeFixed64List(number, (List)unsafe
/* 1547 */               .getObject(message, offset), false);
/*      */           break;
/*      */         case 19:
/* 1550 */           size += 
/* 1551 */             SchemaUtil.computeSizeFixed32List(number, (List)unsafe
/* 1552 */               .getObject(message, offset), false);
/*      */           break;
/*      */         case 20:
/* 1555 */           size += 
/* 1556 */             SchemaUtil.computeSizeInt64List(number, (List<Long>)unsafe
/* 1557 */               .getObject(message, offset), false);
/*      */           break;
/*      */         case 21:
/* 1560 */           size += 
/* 1561 */             SchemaUtil.computeSizeUInt64List(number, (List<Long>)unsafe
/* 1562 */               .getObject(message, offset), false);
/*      */           break;
/*      */         case 22:
/* 1565 */           size += 
/* 1566 */             SchemaUtil.computeSizeInt32List(number, (List<Integer>)unsafe
/* 1567 */               .getObject(message, offset), false);
/*      */           break;
/*      */         case 23:
/* 1570 */           size += 
/* 1571 */             SchemaUtil.computeSizeFixed64List(number, (List)unsafe
/* 1572 */               .getObject(message, offset), false);
/*      */           break;
/*      */         case 24:
/* 1575 */           size += 
/* 1576 */             SchemaUtil.computeSizeFixed32List(number, (List)unsafe
/* 1577 */               .getObject(message, offset), false);
/*      */           break;
/*      */         case 25:
/* 1580 */           size += 
/* 1581 */             SchemaUtil.computeSizeBoolList(number, (List)unsafe
/* 1582 */               .getObject(message, offset), false);
/*      */           break;
/*      */         case 26:
/* 1585 */           size += 
/* 1586 */             SchemaUtil.computeSizeStringList(number, (List)unsafe.getObject(message, offset));
/*      */           break;
/*      */         case 27:
/* 1589 */           size += 
/* 1590 */             SchemaUtil.computeSizeMessageList(number, (List)unsafe
/* 1591 */               .getObject(message, offset), getMessageFieldSchema(i));
/*      */           break;
/*      */         case 28:
/* 1594 */           size += 
/* 1595 */             SchemaUtil.computeSizeByteStringList(number, (List<ByteString>)unsafe
/* 1596 */               .getObject(message, offset));
/*      */           break;
/*      */         case 29:
/* 1599 */           size += 
/* 1600 */             SchemaUtil.computeSizeUInt32List(number, (List<Integer>)unsafe
/* 1601 */               .getObject(message, offset), false);
/*      */           break;
/*      */         case 30:
/* 1604 */           size += 
/* 1605 */             SchemaUtil.computeSizeEnumList(number, (List<Integer>)unsafe
/* 1606 */               .getObject(message, offset), false);
/*      */           break;
/*      */         case 31:
/* 1609 */           size += 
/* 1610 */             SchemaUtil.computeSizeFixed32List(number, (List)unsafe
/* 1611 */               .getObject(message, offset), false);
/*      */           break;
/*      */         case 32:
/* 1614 */           size += 
/* 1615 */             SchemaUtil.computeSizeFixed64List(number, (List)unsafe
/* 1616 */               .getObject(message, offset), false);
/*      */           break;
/*      */         case 33:
/* 1619 */           size += 
/* 1620 */             SchemaUtil.computeSizeSInt32List(number, (List<Integer>)unsafe
/* 1621 */               .getObject(message, offset), false);
/*      */           break;
/*      */         case 34:
/* 1624 */           size += 
/* 1625 */             SchemaUtil.computeSizeSInt64List(number, (List<Long>)unsafe
/* 1626 */               .getObject(message, offset), false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 35:
/* 1631 */           fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List)unsafe
/* 1632 */               .getObject(message, offset));
/* 1633 */           if (fieldSize > 0) {
/* 1634 */             if (this.useCachedSizeField) {
/* 1635 */               unsafe.putInt(message, presenceMaskAndOffset, fieldSize);
/*      */             }
/* 1637 */             size += 
/* 1638 */               CodedOutputStream.computeTagSize(number) + 
/* 1639 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 36:
/* 1647 */           fieldSize = SchemaUtil.computeSizeFixed32ListNoTag((List)unsafe
/* 1648 */               .getObject(message, offset));
/* 1649 */           if (fieldSize > 0) {
/* 1650 */             if (this.useCachedSizeField) {
/* 1651 */               unsafe.putInt(message, presenceMaskAndOffset, fieldSize);
/*      */             }
/* 1653 */             size += 
/* 1654 */               CodedOutputStream.computeTagSize(number) + 
/* 1655 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 37:
/* 1663 */           fieldSize = SchemaUtil.computeSizeInt64ListNoTag((List<Long>)unsafe
/* 1664 */               .getObject(message, offset));
/* 1665 */           if (fieldSize > 0) {
/* 1666 */             if (this.useCachedSizeField) {
/* 1667 */               unsafe.putInt(message, presenceMaskAndOffset, fieldSize);
/*      */             }
/* 1669 */             size += 
/* 1670 */               CodedOutputStream.computeTagSize(number) + 
/* 1671 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 38:
/* 1679 */           fieldSize = SchemaUtil.computeSizeUInt64ListNoTag((List<Long>)unsafe
/* 1680 */               .getObject(message, offset));
/* 1681 */           if (fieldSize > 0) {
/* 1682 */             if (this.useCachedSizeField) {
/* 1683 */               unsafe.putInt(message, presenceMaskAndOffset, fieldSize);
/*      */             }
/* 1685 */             size += 
/* 1686 */               CodedOutputStream.computeTagSize(number) + 
/* 1687 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 39:
/* 1695 */           fieldSize = SchemaUtil.computeSizeInt32ListNoTag((List<Integer>)unsafe
/* 1696 */               .getObject(message, offset));
/* 1697 */           if (fieldSize > 0) {
/* 1698 */             if (this.useCachedSizeField) {
/* 1699 */               unsafe.putInt(message, presenceMaskAndOffset, fieldSize);
/*      */             }
/* 1701 */             size += 
/* 1702 */               CodedOutputStream.computeTagSize(number) + 
/* 1703 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 40:
/* 1711 */           fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List)unsafe
/* 1712 */               .getObject(message, offset));
/* 1713 */           if (fieldSize > 0) {
/* 1714 */             if (this.useCachedSizeField) {
/* 1715 */               unsafe.putInt(message, presenceMaskAndOffset, fieldSize);
/*      */             }
/* 1717 */             size += 
/* 1718 */               CodedOutputStream.computeTagSize(number) + 
/* 1719 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 41:
/* 1727 */           fieldSize = SchemaUtil.computeSizeFixed32ListNoTag((List)unsafe
/* 1728 */               .getObject(message, offset));
/* 1729 */           if (fieldSize > 0) {
/* 1730 */             if (this.useCachedSizeField) {
/* 1731 */               unsafe.putInt(message, presenceMaskAndOffset, fieldSize);
/*      */             }
/* 1733 */             size += 
/* 1734 */               CodedOutputStream.computeTagSize(number) + 
/* 1735 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 42:
/* 1743 */           fieldSize = SchemaUtil.computeSizeBoolListNoTag((List)unsafe
/* 1744 */               .getObject(message, offset));
/* 1745 */           if (fieldSize > 0) {
/* 1746 */             if (this.useCachedSizeField) {
/* 1747 */               unsafe.putInt(message, presenceMaskAndOffset, fieldSize);
/*      */             }
/* 1749 */             size += 
/* 1750 */               CodedOutputStream.computeTagSize(number) + 
/* 1751 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 43:
/* 1759 */           fieldSize = SchemaUtil.computeSizeUInt32ListNoTag((List<Integer>)unsafe
/* 1760 */               .getObject(message, offset));
/* 1761 */           if (fieldSize > 0) {
/* 1762 */             if (this.useCachedSizeField) {
/* 1763 */               unsafe.putInt(message, presenceMaskAndOffset, fieldSize);
/*      */             }
/* 1765 */             size += 
/* 1766 */               CodedOutputStream.computeTagSize(number) + 
/* 1767 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 44:
/* 1775 */           fieldSize = SchemaUtil.computeSizeEnumListNoTag((List<Integer>)unsafe
/* 1776 */               .getObject(message, offset));
/* 1777 */           if (fieldSize > 0) {
/* 1778 */             if (this.useCachedSizeField) {
/* 1779 */               unsafe.putInt(message, presenceMaskAndOffset, fieldSize);
/*      */             }
/* 1781 */             size += 
/* 1782 */               CodedOutputStream.computeTagSize(number) + 
/* 1783 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 45:
/* 1791 */           fieldSize = SchemaUtil.computeSizeFixed32ListNoTag((List)unsafe
/* 1792 */               .getObject(message, offset));
/* 1793 */           if (fieldSize > 0) {
/* 1794 */             if (this.useCachedSizeField) {
/* 1795 */               unsafe.putInt(message, presenceMaskAndOffset, fieldSize);
/*      */             }
/* 1797 */             size += 
/* 1798 */               CodedOutputStream.computeTagSize(number) + 
/* 1799 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 46:
/* 1807 */           fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List)unsafe
/* 1808 */               .getObject(message, offset));
/* 1809 */           if (fieldSize > 0) {
/* 1810 */             if (this.useCachedSizeField) {
/* 1811 */               unsafe.putInt(message, presenceMaskAndOffset, fieldSize);
/*      */             }
/* 1813 */             size += 
/* 1814 */               CodedOutputStream.computeTagSize(number) + 
/* 1815 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 47:
/* 1823 */           fieldSize = SchemaUtil.computeSizeSInt32ListNoTag((List<Integer>)unsafe
/* 1824 */               .getObject(message, offset));
/* 1825 */           if (fieldSize > 0) {
/* 1826 */             if (this.useCachedSizeField) {
/* 1827 */               unsafe.putInt(message, presenceMaskAndOffset, fieldSize);
/*      */             }
/* 1829 */             size += 
/* 1830 */               CodedOutputStream.computeTagSize(number) + 
/* 1831 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 48:
/* 1839 */           fieldSize = SchemaUtil.computeSizeSInt64ListNoTag((List<Long>)unsafe
/* 1840 */               .getObject(message, offset));
/* 1841 */           if (fieldSize > 0) {
/* 1842 */             if (this.useCachedSizeField) {
/* 1843 */               unsafe.putInt(message, presenceMaskAndOffset, fieldSize);
/*      */             }
/* 1845 */             size += 
/* 1846 */               CodedOutputStream.computeTagSize(number) + 
/* 1847 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */         
/*      */         case 49:
/* 1853 */           size += 
/* 1854 */             SchemaUtil.computeSizeGroupList(number, (List<MessageLite>)unsafe
/*      */               
/* 1856 */               .getObject(message, offset), 
/* 1857 */               getMessageFieldSchema(i));
/*      */           break;
/*      */         
/*      */         case 50:
/* 1861 */           size += this.mapFieldSchema
/* 1862 */             .getSerializedSize(number, unsafe
/* 1863 */               .getObject(message, offset), getMapFieldDefaultEntry(i));
/*      */           break;
/*      */         case 51:
/* 1866 */           if (isOneofPresent(message, number, i)) {
/* 1867 */             size += CodedOutputStream.computeDoubleSize(number, 0.0D);
/*      */           }
/*      */           break;
/*      */         case 52:
/* 1871 */           if (isOneofPresent(message, number, i)) {
/* 1872 */             size += CodedOutputStream.computeFloatSize(number, 0.0F);
/*      */           }
/*      */           break;
/*      */         case 53:
/* 1876 */           if (isOneofPresent(message, number, i)) {
/* 1877 */             size += CodedOutputStream.computeInt64Size(number, oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 54:
/* 1881 */           if (isOneofPresent(message, number, i)) {
/* 1882 */             size += CodedOutputStream.computeUInt64Size(number, oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 55:
/* 1886 */           if (isOneofPresent(message, number, i)) {
/* 1887 */             size += CodedOutputStream.computeInt32Size(number, oneofIntAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 56:
/* 1891 */           if (isOneofPresent(message, number, i)) {
/* 1892 */             size += CodedOutputStream.computeFixed64Size(number, 0L);
/*      */           }
/*      */           break;
/*      */         case 57:
/* 1896 */           if (isOneofPresent(message, number, i)) {
/* 1897 */             size += CodedOutputStream.computeFixed32Size(number, 0);
/*      */           }
/*      */           break;
/*      */         case 58:
/* 1901 */           if (isOneofPresent(message, number, i)) {
/* 1902 */             size += CodedOutputStream.computeBoolSize(number, true);
/*      */           }
/*      */           break;
/*      */         case 59:
/* 1906 */           if (isOneofPresent(message, number, i)) {
/* 1907 */             Object value = unsafe.getObject(message, offset);
/* 1908 */             if (value instanceof ByteString) {
/* 1909 */               size += CodedOutputStream.computeBytesSize(number, (ByteString)value); break;
/*      */             } 
/* 1911 */             size += CodedOutputStream.computeStringSize(number, (String)value);
/*      */           } 
/*      */           break;
/*      */         
/*      */         case 60:
/* 1916 */           if (isOneofPresent(message, number, i)) {
/* 1917 */             Object value = unsafe.getObject(message, offset);
/* 1918 */             size += SchemaUtil.computeSizeMessage(number, value, getMessageFieldSchema(i));
/*      */           } 
/*      */           break;
/*      */         case 61:
/* 1922 */           if (isOneofPresent(message, number, i)) {
/* 1923 */             size += 
/* 1924 */               CodedOutputStream.computeBytesSize(number, (ByteString)unsafe
/* 1925 */                 .getObject(message, offset));
/*      */           }
/*      */           break;
/*      */         case 62:
/* 1929 */           if (isOneofPresent(message, number, i)) {
/* 1930 */             size += CodedOutputStream.computeUInt32Size(number, oneofIntAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 63:
/* 1934 */           if (isOneofPresent(message, number, i)) {
/* 1935 */             size += CodedOutputStream.computeEnumSize(number, oneofIntAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 64:
/* 1939 */           if (isOneofPresent(message, number, i)) {
/* 1940 */             size += CodedOutputStream.computeSFixed32Size(number, 0);
/*      */           }
/*      */           break;
/*      */         case 65:
/* 1944 */           if (isOneofPresent(message, number, i)) {
/* 1945 */             size += CodedOutputStream.computeSFixed64Size(number, 0L);
/*      */           }
/*      */           break;
/*      */         case 66:
/* 1949 */           if (isOneofPresent(message, number, i)) {
/* 1950 */             size += CodedOutputStream.computeSInt32Size(number, oneofIntAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 67:
/* 1954 */           if (isOneofPresent(message, number, i)) {
/* 1955 */             size += CodedOutputStream.computeSInt64Size(number, oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 68:
/* 1959 */           if (isOneofPresent(message, number, i)) {
/* 1960 */             size += 
/* 1961 */               CodedOutputStream.computeGroupSize(number, (MessageLite)unsafe
/*      */                 
/* 1963 */                 .getObject(message, offset), 
/* 1964 */                 getMessageFieldSchema(i));
/*      */           }
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */     
/*      */     } 
/* 1972 */     size += getUnknownFieldsSerializedSize(this.unknownFieldSchema, message);
/*      */     
/* 1974 */     if (this.hasExtensions) {
/* 1975 */       size += this.extensionSchema.getExtensions(message).getSerializedSize();
/*      */     }
/*      */     
/* 1978 */     return size;
/*      */   }
/*      */   
/*      */   private int getSerializedSizeProto3(T message) {
/* 1982 */     Unsafe unsafe = UNSAFE;
/* 1983 */     int size = 0;
/* 1984 */     for (int i = 0; i < this.buffer.length; i += 3) {
/* 1985 */       int fieldSize, typeAndOffset = typeAndOffsetAt(i);
/* 1986 */       int fieldType = type(typeAndOffset);
/* 1987 */       int number = numberAt(i);
/*      */       
/* 1989 */       long offset = offset(typeAndOffset);
/*      */ 
/*      */       
/* 1992 */       int cachedSizeOffset = (fieldType >= FieldType.DOUBLE_LIST_PACKED.id() && fieldType <= FieldType.SINT64_LIST_PACKED.id()) ? (this.buffer[i + 2] & 0xFFFFF) : 0;
/*      */ 
/*      */ 
/*      */       
/* 1996 */       switch (fieldType) {
/*      */         case 0:
/* 1998 */           if (isFieldPresent(message, i)) {
/* 1999 */             size += CodedOutputStream.computeDoubleSize(number, 0.0D);
/*      */           }
/*      */           break;
/*      */         case 1:
/* 2003 */           if (isFieldPresent(message, i)) {
/* 2004 */             size += CodedOutputStream.computeFloatSize(number, 0.0F);
/*      */           }
/*      */           break;
/*      */         case 2:
/* 2008 */           if (isFieldPresent(message, i)) {
/* 2009 */             size += CodedOutputStream.computeInt64Size(number, UnsafeUtil.getLong(message, offset));
/*      */           }
/*      */           break;
/*      */         case 3:
/* 2013 */           if (isFieldPresent(message, i)) {
/* 2014 */             size += 
/* 2015 */               CodedOutputStream.computeUInt64Size(number, UnsafeUtil.getLong(message, offset));
/*      */           }
/*      */           break;
/*      */         case 4:
/* 2019 */           if (isFieldPresent(message, i)) {
/* 2020 */             size += CodedOutputStream.computeInt32Size(number, UnsafeUtil.getInt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 5:
/* 2024 */           if (isFieldPresent(message, i)) {
/* 2025 */             size += CodedOutputStream.computeFixed64Size(number, 0L);
/*      */           }
/*      */           break;
/*      */         case 6:
/* 2029 */           if (isFieldPresent(message, i)) {
/* 2030 */             size += CodedOutputStream.computeFixed32Size(number, 0);
/*      */           }
/*      */           break;
/*      */         case 7:
/* 2034 */           if (isFieldPresent(message, i)) {
/* 2035 */             size += CodedOutputStream.computeBoolSize(number, true);
/*      */           }
/*      */           break;
/*      */         case 8:
/* 2039 */           if (isFieldPresent(message, i)) {
/* 2040 */             Object value = UnsafeUtil.getObject(message, offset);
/* 2041 */             if (value instanceof ByteString) {
/* 2042 */               size += CodedOutputStream.computeBytesSize(number, (ByteString)value); break;
/*      */             } 
/* 2044 */             size += CodedOutputStream.computeStringSize(number, (String)value);
/*      */           } 
/*      */           break;
/*      */         
/*      */         case 9:
/* 2049 */           if (isFieldPresent(message, i)) {
/* 2050 */             Object value = UnsafeUtil.getObject(message, offset);
/* 2051 */             size += SchemaUtil.computeSizeMessage(number, value, getMessageFieldSchema(i));
/*      */           } 
/*      */           break;
/*      */         case 10:
/* 2055 */           if (isFieldPresent(message, i)) {
/* 2056 */             ByteString value = (ByteString)UnsafeUtil.getObject(message, offset);
/* 2057 */             size += CodedOutputStream.computeBytesSize(number, value);
/*      */           } 
/*      */           break;
/*      */         case 11:
/* 2061 */           if (isFieldPresent(message, i)) {
/* 2062 */             size += CodedOutputStream.computeUInt32Size(number, UnsafeUtil.getInt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 12:
/* 2066 */           if (isFieldPresent(message, i)) {
/* 2067 */             size += CodedOutputStream.computeEnumSize(number, UnsafeUtil.getInt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 13:
/* 2071 */           if (isFieldPresent(message, i)) {
/* 2072 */             size += CodedOutputStream.computeSFixed32Size(number, 0);
/*      */           }
/*      */           break;
/*      */         case 14:
/* 2076 */           if (isFieldPresent(message, i)) {
/* 2077 */             size += CodedOutputStream.computeSFixed64Size(number, 0L);
/*      */           }
/*      */           break;
/*      */         case 15:
/* 2081 */           if (isFieldPresent(message, i)) {
/* 2082 */             size += CodedOutputStream.computeSInt32Size(number, UnsafeUtil.getInt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 16:
/* 2086 */           if (isFieldPresent(message, i)) {
/* 2087 */             size += 
/* 2088 */               CodedOutputStream.computeSInt64Size(number, UnsafeUtil.getLong(message, offset));
/*      */           }
/*      */           break;
/*      */         case 17:
/* 2092 */           if (isFieldPresent(message, i)) {
/* 2093 */             size += 
/* 2094 */               CodedOutputStream.computeGroupSize(number, 
/*      */                 
/* 2096 */                 (MessageLite)UnsafeUtil.getObject(message, offset), 
/* 2097 */                 getMessageFieldSchema(i));
/*      */           }
/*      */           break;
/*      */         case 18:
/* 2101 */           size += SchemaUtil.computeSizeFixed64List(number, listAt(message, offset), false);
/*      */           break;
/*      */         case 19:
/* 2104 */           size += SchemaUtil.computeSizeFixed32List(number, listAt(message, offset), false);
/*      */           break;
/*      */         case 20:
/* 2107 */           size += 
/* 2108 */             SchemaUtil.computeSizeInt64List(number, (List)listAt(message, offset), false);
/*      */           break;
/*      */         case 21:
/* 2111 */           size += 
/* 2112 */             SchemaUtil.computeSizeUInt64List(number, (List)listAt(message, offset), false);
/*      */           break;
/*      */         case 22:
/* 2115 */           size += 
/* 2116 */             SchemaUtil.computeSizeInt32List(number, 
/* 2117 */               (List)listAt(message, offset), false);
/*      */           break;
/*      */         case 23:
/* 2120 */           size += SchemaUtil.computeSizeFixed64List(number, listAt(message, offset), false);
/*      */           break;
/*      */         case 24:
/* 2123 */           size += SchemaUtil.computeSizeFixed32List(number, listAt(message, offset), false);
/*      */           break;
/*      */         case 25:
/* 2126 */           size += SchemaUtil.computeSizeBoolList(number, listAt(message, offset), false);
/*      */           break;
/*      */         case 26:
/* 2129 */           size += SchemaUtil.computeSizeStringList(number, listAt(message, offset));
/*      */           break;
/*      */         case 27:
/* 2132 */           size += 
/* 2133 */             SchemaUtil.computeSizeMessageList(number, 
/* 2134 */               listAt(message, offset), getMessageFieldSchema(i));
/*      */           break;
/*      */         case 28:
/* 2137 */           size += 
/* 2138 */             SchemaUtil.computeSizeByteStringList(number, 
/* 2139 */               (List)listAt(message, offset));
/*      */           break;
/*      */         case 29:
/* 2142 */           size += 
/* 2143 */             SchemaUtil.computeSizeUInt32List(number, 
/* 2144 */               (List)listAt(message, offset), false);
/*      */           break;
/*      */         case 30:
/* 2147 */           size += 
/* 2148 */             SchemaUtil.computeSizeEnumList(number, 
/* 2149 */               (List)listAt(message, offset), false);
/*      */           break;
/*      */         case 31:
/* 2152 */           size += SchemaUtil.computeSizeFixed32List(number, listAt(message, offset), false);
/*      */           break;
/*      */         case 32:
/* 2155 */           size += SchemaUtil.computeSizeFixed64List(number, listAt(message, offset), false);
/*      */           break;
/*      */         case 33:
/* 2158 */           size += 
/* 2159 */             SchemaUtil.computeSizeSInt32List(number, 
/* 2160 */               (List)listAt(message, offset), false);
/*      */           break;
/*      */         case 34:
/* 2163 */           size += 
/* 2164 */             SchemaUtil.computeSizeSInt64List(number, (List)listAt(message, offset), false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 35:
/* 2169 */           fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List)unsafe
/* 2170 */               .getObject(message, offset));
/* 2171 */           if (fieldSize > 0) {
/* 2172 */             if (this.useCachedSizeField) {
/* 2173 */               unsafe.putInt(message, cachedSizeOffset, fieldSize);
/*      */             }
/* 2175 */             size += 
/* 2176 */               CodedOutputStream.computeTagSize(number) + 
/* 2177 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 36:
/* 2185 */           fieldSize = SchemaUtil.computeSizeFixed32ListNoTag((List)unsafe
/* 2186 */               .getObject(message, offset));
/* 2187 */           if (fieldSize > 0) {
/* 2188 */             if (this.useCachedSizeField) {
/* 2189 */               unsafe.putInt(message, cachedSizeOffset, fieldSize);
/*      */             }
/* 2191 */             size += 
/* 2192 */               CodedOutputStream.computeTagSize(number) + 
/* 2193 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 37:
/* 2201 */           fieldSize = SchemaUtil.computeSizeInt64ListNoTag((List<Long>)unsafe
/* 2202 */               .getObject(message, offset));
/* 2203 */           if (fieldSize > 0) {
/* 2204 */             if (this.useCachedSizeField) {
/* 2205 */               unsafe.putInt(message, cachedSizeOffset, fieldSize);
/*      */             }
/* 2207 */             size += 
/* 2208 */               CodedOutputStream.computeTagSize(number) + 
/* 2209 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 38:
/* 2217 */           fieldSize = SchemaUtil.computeSizeUInt64ListNoTag((List<Long>)unsafe
/* 2218 */               .getObject(message, offset));
/* 2219 */           if (fieldSize > 0) {
/* 2220 */             if (this.useCachedSizeField) {
/* 2221 */               unsafe.putInt(message, cachedSizeOffset, fieldSize);
/*      */             }
/* 2223 */             size += 
/* 2224 */               CodedOutputStream.computeTagSize(number) + 
/* 2225 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 39:
/* 2233 */           fieldSize = SchemaUtil.computeSizeInt32ListNoTag((List<Integer>)unsafe
/* 2234 */               .getObject(message, offset));
/* 2235 */           if (fieldSize > 0) {
/* 2236 */             if (this.useCachedSizeField) {
/* 2237 */               unsafe.putInt(message, cachedSizeOffset, fieldSize);
/*      */             }
/* 2239 */             size += 
/* 2240 */               CodedOutputStream.computeTagSize(number) + 
/* 2241 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 40:
/* 2249 */           fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List)unsafe
/* 2250 */               .getObject(message, offset));
/* 2251 */           if (fieldSize > 0) {
/* 2252 */             if (this.useCachedSizeField) {
/* 2253 */               unsafe.putInt(message, cachedSizeOffset, fieldSize);
/*      */             }
/* 2255 */             size += 
/* 2256 */               CodedOutputStream.computeTagSize(number) + 
/* 2257 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 41:
/* 2265 */           fieldSize = SchemaUtil.computeSizeFixed32ListNoTag((List)unsafe
/* 2266 */               .getObject(message, offset));
/* 2267 */           if (fieldSize > 0) {
/* 2268 */             if (this.useCachedSizeField) {
/* 2269 */               unsafe.putInt(message, cachedSizeOffset, fieldSize);
/*      */             }
/* 2271 */             size += 
/* 2272 */               CodedOutputStream.computeTagSize(number) + 
/* 2273 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 42:
/* 2281 */           fieldSize = SchemaUtil.computeSizeBoolListNoTag((List)unsafe
/* 2282 */               .getObject(message, offset));
/* 2283 */           if (fieldSize > 0) {
/* 2284 */             if (this.useCachedSizeField) {
/* 2285 */               unsafe.putInt(message, cachedSizeOffset, fieldSize);
/*      */             }
/* 2287 */             size += 
/* 2288 */               CodedOutputStream.computeTagSize(number) + 
/* 2289 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 43:
/* 2297 */           fieldSize = SchemaUtil.computeSizeUInt32ListNoTag((List<Integer>)unsafe
/* 2298 */               .getObject(message, offset));
/* 2299 */           if (fieldSize > 0) {
/* 2300 */             if (this.useCachedSizeField) {
/* 2301 */               unsafe.putInt(message, cachedSizeOffset, fieldSize);
/*      */             }
/* 2303 */             size += 
/* 2304 */               CodedOutputStream.computeTagSize(number) + 
/* 2305 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 44:
/* 2313 */           fieldSize = SchemaUtil.computeSizeEnumListNoTag((List<Integer>)unsafe
/* 2314 */               .getObject(message, offset));
/* 2315 */           if (fieldSize > 0) {
/* 2316 */             if (this.useCachedSizeField) {
/* 2317 */               unsafe.putInt(message, cachedSizeOffset, fieldSize);
/*      */             }
/* 2319 */             size += 
/* 2320 */               CodedOutputStream.computeTagSize(number) + 
/* 2321 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 45:
/* 2329 */           fieldSize = SchemaUtil.computeSizeFixed32ListNoTag((List)unsafe
/* 2330 */               .getObject(message, offset));
/* 2331 */           if (fieldSize > 0) {
/* 2332 */             if (this.useCachedSizeField) {
/* 2333 */               unsafe.putInt(message, cachedSizeOffset, fieldSize);
/*      */             }
/* 2335 */             size += 
/* 2336 */               CodedOutputStream.computeTagSize(number) + 
/* 2337 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 46:
/* 2345 */           fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List)unsafe
/* 2346 */               .getObject(message, offset));
/* 2347 */           if (fieldSize > 0) {
/* 2348 */             if (this.useCachedSizeField) {
/* 2349 */               unsafe.putInt(message, cachedSizeOffset, fieldSize);
/*      */             }
/* 2351 */             size += 
/* 2352 */               CodedOutputStream.computeTagSize(number) + 
/* 2353 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 47:
/* 2361 */           fieldSize = SchemaUtil.computeSizeSInt32ListNoTag((List<Integer>)unsafe
/* 2362 */               .getObject(message, offset));
/* 2363 */           if (fieldSize > 0) {
/* 2364 */             if (this.useCachedSizeField) {
/* 2365 */               unsafe.putInt(message, cachedSizeOffset, fieldSize);
/*      */             }
/* 2367 */             size += 
/* 2368 */               CodedOutputStream.computeTagSize(number) + 
/* 2369 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 48:
/* 2377 */           fieldSize = SchemaUtil.computeSizeSInt64ListNoTag((List<Long>)unsafe
/* 2378 */               .getObject(message, offset));
/* 2379 */           if (fieldSize > 0) {
/* 2380 */             if (this.useCachedSizeField) {
/* 2381 */               unsafe.putInt(message, cachedSizeOffset, fieldSize);
/*      */             }
/* 2383 */             size += 
/* 2384 */               CodedOutputStream.computeTagSize(number) + 
/* 2385 */               CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
/*      */           } 
/*      */           break;
/*      */ 
/*      */         
/*      */         case 49:
/* 2391 */           size += 
/* 2392 */             SchemaUtil.computeSizeGroupList(number, 
/* 2393 */               (List)listAt(message, offset), getMessageFieldSchema(i));
/*      */           break;
/*      */         
/*      */         case 50:
/* 2397 */           size += this.mapFieldSchema
/* 2398 */             .getSerializedSize(number, 
/* 2399 */               UnsafeUtil.getObject(message, offset), getMapFieldDefaultEntry(i));
/*      */           break;
/*      */         case 51:
/* 2402 */           if (isOneofPresent(message, number, i)) {
/* 2403 */             size += CodedOutputStream.computeDoubleSize(number, 0.0D);
/*      */           }
/*      */           break;
/*      */         case 52:
/* 2407 */           if (isOneofPresent(message, number, i)) {
/* 2408 */             size += CodedOutputStream.computeFloatSize(number, 0.0F);
/*      */           }
/*      */           break;
/*      */         case 53:
/* 2412 */           if (isOneofPresent(message, number, i)) {
/* 2413 */             size += CodedOutputStream.computeInt64Size(number, oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 54:
/* 2417 */           if (isOneofPresent(message, number, i)) {
/* 2418 */             size += CodedOutputStream.computeUInt64Size(number, oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 55:
/* 2422 */           if (isOneofPresent(message, number, i)) {
/* 2423 */             size += CodedOutputStream.computeInt32Size(number, oneofIntAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 56:
/* 2427 */           if (isOneofPresent(message, number, i)) {
/* 2428 */             size += CodedOutputStream.computeFixed64Size(number, 0L);
/*      */           }
/*      */           break;
/*      */         case 57:
/* 2432 */           if (isOneofPresent(message, number, i)) {
/* 2433 */             size += CodedOutputStream.computeFixed32Size(number, 0);
/*      */           }
/*      */           break;
/*      */         case 58:
/* 2437 */           if (isOneofPresent(message, number, i)) {
/* 2438 */             size += CodedOutputStream.computeBoolSize(number, true);
/*      */           }
/*      */           break;
/*      */         case 59:
/* 2442 */           if (isOneofPresent(message, number, i)) {
/* 2443 */             Object value = UnsafeUtil.getObject(message, offset);
/* 2444 */             if (value instanceof ByteString) {
/* 2445 */               size += CodedOutputStream.computeBytesSize(number, (ByteString)value); break;
/*      */             } 
/* 2447 */             size += CodedOutputStream.computeStringSize(number, (String)value);
/*      */           } 
/*      */           break;
/*      */         
/*      */         case 60:
/* 2452 */           if (isOneofPresent(message, number, i)) {
/* 2453 */             Object value = UnsafeUtil.getObject(message, offset);
/* 2454 */             size += SchemaUtil.computeSizeMessage(number, value, getMessageFieldSchema(i));
/*      */           } 
/*      */           break;
/*      */         case 61:
/* 2458 */           if (isOneofPresent(message, number, i)) {
/* 2459 */             size += 
/* 2460 */               CodedOutputStream.computeBytesSize(number, 
/* 2461 */                 (ByteString)UnsafeUtil.getObject(message, offset));
/*      */           }
/*      */           break;
/*      */         case 62:
/* 2465 */           if (isOneofPresent(message, number, i)) {
/* 2466 */             size += CodedOutputStream.computeUInt32Size(number, oneofIntAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 63:
/* 2470 */           if (isOneofPresent(message, number, i)) {
/* 2471 */             size += CodedOutputStream.computeEnumSize(number, oneofIntAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 64:
/* 2475 */           if (isOneofPresent(message, number, i)) {
/* 2476 */             size += CodedOutputStream.computeSFixed32Size(number, 0);
/*      */           }
/*      */           break;
/*      */         case 65:
/* 2480 */           if (isOneofPresent(message, number, i)) {
/* 2481 */             size += CodedOutputStream.computeSFixed64Size(number, 0L);
/*      */           }
/*      */           break;
/*      */         case 66:
/* 2485 */           if (isOneofPresent(message, number, i)) {
/* 2486 */             size += CodedOutputStream.computeSInt32Size(number, oneofIntAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 67:
/* 2490 */           if (isOneofPresent(message, number, i)) {
/* 2491 */             size += CodedOutputStream.computeSInt64Size(number, oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 68:
/* 2495 */           if (isOneofPresent(message, number, i)) {
/* 2496 */             size += 
/* 2497 */               CodedOutputStream.computeGroupSize(number, 
/*      */                 
/* 2499 */                 (MessageLite)UnsafeUtil.getObject(message, offset), 
/* 2500 */                 getMessageFieldSchema(i));
/*      */           }
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */     
/*      */     } 
/* 2508 */     size += getUnknownFieldsSerializedSize(this.unknownFieldSchema, message);
/*      */     
/* 2510 */     return size;
/*      */   }
/*      */ 
/*      */   
/*      */   private <UT, UB> int getUnknownFieldsSerializedSize(UnknownFieldSchema<UT, UB> schema, T message) {
/* 2515 */     UT unknowns = schema.getFromMessage(message);
/* 2516 */     return schema.getSerializedSize(unknowns);
/*      */   }
/*      */   
/*      */   private static List<?> listAt(Object message, long offset) {
/* 2520 */     return (List)UnsafeUtil.getObject(message, offset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeTo(T message, Writer writer) throws IOException {
/* 2529 */     if (writer.fieldOrder() == Writer.FieldOrder.DESCENDING) {
/* 2530 */       writeFieldsInDescendingOrder(message, writer);
/*      */     }
/* 2532 */     else if (this.proto3) {
/* 2533 */       writeFieldsInAscendingOrderProto3(message, writer);
/*      */     } else {
/* 2535 */       writeFieldsInAscendingOrderProto2(message, writer);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeFieldsInAscendingOrderProto2(T message, Writer writer) throws IOException {
/* 2542 */     Iterator<? extends Map.Entry<?, ?>> extensionIterator = null;
/* 2543 */     Map.Entry<?, ?> nextExtension = null;
/* 2544 */     if (this.hasExtensions) {
/* 2545 */       FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
/* 2546 */       if (!extensions.isEmpty()) {
/* 2547 */         extensionIterator = extensions.iterator();
/* 2548 */         nextExtension = extensionIterator.next();
/*      */       } 
/*      */     } 
/* 2551 */     int currentPresenceFieldOffset = -1;
/* 2552 */     int currentPresenceField = 0;
/* 2553 */     int bufferLength = this.buffer.length;
/* 2554 */     Unsafe unsafe = UNSAFE;
/* 2555 */     for (int pos = 0; pos < bufferLength; pos += 3) {
/* 2556 */       int typeAndOffset = typeAndOffsetAt(pos);
/* 2557 */       int number = numberAt(pos);
/* 2558 */       int fieldType = type(typeAndOffset);
/*      */       
/* 2560 */       int presenceMaskAndOffset = 0;
/* 2561 */       int presenceMask = 0;
/* 2562 */       if (!this.proto3 && fieldType <= 17) {
/* 2563 */         presenceMaskAndOffset = this.buffer[pos + 2];
/* 2564 */         int presenceFieldOffset = presenceMaskAndOffset & 0xFFFFF;
/* 2565 */         if (presenceFieldOffset != currentPresenceFieldOffset) {
/* 2566 */           currentPresenceFieldOffset = presenceFieldOffset;
/* 2567 */           currentPresenceField = unsafe.getInt(message, presenceFieldOffset);
/*      */         } 
/* 2569 */         presenceMask = 1 << presenceMaskAndOffset >>> 20;
/*      */       } 
/*      */ 
/*      */       
/* 2573 */       while (nextExtension != null && this.extensionSchema.extensionNumber(nextExtension) <= number) {
/* 2574 */         this.extensionSchema.serializeExtension(writer, nextExtension);
/* 2575 */         nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
/*      */       } 
/* 2577 */       long offset = offset(typeAndOffset);
/*      */       
/* 2579 */       switch (fieldType) {
/*      */         case 0:
/* 2581 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2582 */             writer.writeDouble(number, doubleAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 1:
/* 2586 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2587 */             writer.writeFloat(number, floatAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 2:
/* 2591 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2592 */             writer.writeInt64(number, unsafe.getLong(message, offset));
/*      */           }
/*      */           break;
/*      */         case 3:
/* 2596 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2597 */             writer.writeUInt64(number, unsafe.getLong(message, offset));
/*      */           }
/*      */           break;
/*      */         case 4:
/* 2601 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2602 */             writer.writeInt32(number, unsafe.getInt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 5:
/* 2606 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2607 */             writer.writeFixed64(number, unsafe.getLong(message, offset));
/*      */           }
/*      */           break;
/*      */         case 6:
/* 2611 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2612 */             writer.writeFixed32(number, unsafe.getInt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 7:
/* 2616 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2617 */             writer.writeBool(number, booleanAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 8:
/* 2621 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2622 */             writeString(number, unsafe.getObject(message, offset), writer);
/*      */           }
/*      */           break;
/*      */         case 9:
/* 2626 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2627 */             Object value = unsafe.getObject(message, offset);
/* 2628 */             writer.writeMessage(number, value, getMessageFieldSchema(pos));
/*      */           } 
/*      */           break;
/*      */         case 10:
/* 2632 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2633 */             writer.writeBytes(number, (ByteString)unsafe.getObject(message, offset));
/*      */           }
/*      */           break;
/*      */         case 11:
/* 2637 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2638 */             writer.writeUInt32(number, unsafe.getInt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 12:
/* 2642 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2643 */             writer.writeEnum(number, unsafe.getInt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 13:
/* 2647 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2648 */             writer.writeSFixed32(number, unsafe.getInt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 14:
/* 2652 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2653 */             writer.writeSFixed64(number, unsafe.getLong(message, offset));
/*      */           }
/*      */           break;
/*      */         case 15:
/* 2657 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2658 */             writer.writeSInt32(number, unsafe.getInt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 16:
/* 2662 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2663 */             writer.writeSInt64(number, unsafe.getLong(message, offset));
/*      */           }
/*      */           break;
/*      */         case 17:
/* 2667 */           if ((currentPresenceField & presenceMask) != 0) {
/* 2668 */             writer.writeGroup(number, unsafe
/* 2669 */                 .getObject(message, offset), getMessageFieldSchema(pos));
/*      */           }
/*      */           break;
/*      */         case 18:
/* 2673 */           SchemaUtil.writeDoubleList(
/* 2674 */               numberAt(pos), (List<Double>)unsafe.getObject(message, offset), writer, false);
/*      */           break;
/*      */         case 19:
/* 2677 */           SchemaUtil.writeFloatList(
/* 2678 */               numberAt(pos), (List<Float>)unsafe.getObject(message, offset), writer, false);
/*      */           break;
/*      */         case 20:
/* 2681 */           SchemaUtil.writeInt64List(
/* 2682 */               numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, false);
/*      */           break;
/*      */         case 21:
/* 2685 */           SchemaUtil.writeUInt64List(
/* 2686 */               numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, false);
/*      */           break;
/*      */         case 22:
/* 2689 */           SchemaUtil.writeInt32List(
/* 2690 */               numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, false);
/*      */           break;
/*      */         case 23:
/* 2693 */           SchemaUtil.writeFixed64List(
/* 2694 */               numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, false);
/*      */           break;
/*      */         case 24:
/* 2697 */           SchemaUtil.writeFixed32List(
/* 2698 */               numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, false);
/*      */           break;
/*      */         case 25:
/* 2701 */           SchemaUtil.writeBoolList(
/* 2702 */               numberAt(pos), (List<Boolean>)unsafe.getObject(message, offset), writer, false);
/*      */           break;
/*      */         case 26:
/* 2705 */           SchemaUtil.writeStringList(
/* 2706 */               numberAt(pos), (List<String>)unsafe.getObject(message, offset), writer);
/*      */           break;
/*      */         case 27:
/* 2709 */           SchemaUtil.writeMessageList(
/* 2710 */               numberAt(pos), (List)unsafe
/* 2711 */               .getObject(message, offset), writer, 
/*      */               
/* 2713 */               getMessageFieldSchema(pos));
/*      */           break;
/*      */         case 28:
/* 2716 */           SchemaUtil.writeBytesList(
/* 2717 */               numberAt(pos), (List<ByteString>)unsafe.getObject(message, offset), writer);
/*      */           break;
/*      */         case 29:
/* 2720 */           SchemaUtil.writeUInt32List(
/* 2721 */               numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, false);
/*      */           break;
/*      */         case 30:
/* 2724 */           SchemaUtil.writeEnumList(
/* 2725 */               numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, false);
/*      */           break;
/*      */         case 31:
/* 2728 */           SchemaUtil.writeSFixed32List(
/* 2729 */               numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, false);
/*      */           break;
/*      */         case 32:
/* 2732 */           SchemaUtil.writeSFixed64List(
/* 2733 */               numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, false);
/*      */           break;
/*      */         case 33:
/* 2736 */           SchemaUtil.writeSInt32List(
/* 2737 */               numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, false);
/*      */           break;
/*      */         case 34:
/* 2740 */           SchemaUtil.writeSInt64List(
/* 2741 */               numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, false);
/*      */           break;
/*      */         
/*      */         case 35:
/* 2745 */           SchemaUtil.writeDoubleList(
/* 2746 */               numberAt(pos), (List<Double>)unsafe.getObject(message, offset), writer, true);
/*      */           break;
/*      */         case 36:
/* 2749 */           SchemaUtil.writeFloatList(
/* 2750 */               numberAt(pos), (List<Float>)unsafe.getObject(message, offset), writer, true);
/*      */           break;
/*      */         case 37:
/* 2753 */           SchemaUtil.writeInt64List(
/* 2754 */               numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, true);
/*      */           break;
/*      */         case 38:
/* 2757 */           SchemaUtil.writeUInt64List(
/* 2758 */               numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, true);
/*      */           break;
/*      */         case 39:
/* 2761 */           SchemaUtil.writeInt32List(
/* 2762 */               numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, true);
/*      */           break;
/*      */         case 40:
/* 2765 */           SchemaUtil.writeFixed64List(
/* 2766 */               numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, true);
/*      */           break;
/*      */         case 41:
/* 2769 */           SchemaUtil.writeFixed32List(
/* 2770 */               numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, true);
/*      */           break;
/*      */         
/*      */         case 42:
/* 2774 */           SchemaUtil.writeBoolList(
/* 2775 */               numberAt(pos), (List<Boolean>)unsafe.getObject(message, offset), writer, true);
/*      */           break;
/*      */         case 43:
/* 2778 */           SchemaUtil.writeUInt32List(
/* 2779 */               numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, true);
/*      */           break;
/*      */         case 44:
/* 2782 */           SchemaUtil.writeEnumList(
/* 2783 */               numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, true);
/*      */           break;
/*      */         case 45:
/* 2786 */           SchemaUtil.writeSFixed32List(
/* 2787 */               numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, true);
/*      */           break;
/*      */         case 46:
/* 2790 */           SchemaUtil.writeSFixed64List(
/* 2791 */               numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, true);
/*      */           break;
/*      */         case 47:
/* 2794 */           SchemaUtil.writeSInt32List(
/* 2795 */               numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, true);
/*      */           break;
/*      */         case 48:
/* 2798 */           SchemaUtil.writeSInt64List(
/* 2799 */               numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, true);
/*      */           break;
/*      */         case 49:
/* 2802 */           SchemaUtil.writeGroupList(
/* 2803 */               numberAt(pos), (List)unsafe
/* 2804 */               .getObject(message, offset), writer, 
/*      */               
/* 2806 */               getMessageFieldSchema(pos));
/*      */           break;
/*      */         
/*      */         case 50:
/* 2810 */           writeMapHelper(writer, number, unsafe.getObject(message, offset), pos);
/*      */           break;
/*      */         case 51:
/* 2813 */           if (isOneofPresent(message, number, pos)) {
/* 2814 */             writer.writeDouble(number, oneofDoubleAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 52:
/* 2818 */           if (isOneofPresent(message, number, pos)) {
/* 2819 */             writer.writeFloat(number, oneofFloatAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 53:
/* 2823 */           if (isOneofPresent(message, number, pos)) {
/* 2824 */             writer.writeInt64(number, oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 54:
/* 2828 */           if (isOneofPresent(message, number, pos)) {
/* 2829 */             writer.writeUInt64(number, oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 55:
/* 2833 */           if (isOneofPresent(message, number, pos)) {
/* 2834 */             writer.writeInt32(number, oneofIntAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 56:
/* 2838 */           if (isOneofPresent(message, number, pos)) {
/* 2839 */             writer.writeFixed64(number, oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 57:
/* 2843 */           if (isOneofPresent(message, number, pos)) {
/* 2844 */             writer.writeFixed32(number, oneofIntAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 58:
/* 2848 */           if (isOneofPresent(message, number, pos)) {
/* 2849 */             writer.writeBool(number, oneofBooleanAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 59:
/* 2853 */           if (isOneofPresent(message, number, pos)) {
/* 2854 */             writeString(number, unsafe.getObject(message, offset), writer);
/*      */           }
/*      */           break;
/*      */         case 60:
/* 2858 */           if (isOneofPresent(message, number, pos)) {
/* 2859 */             Object value = unsafe.getObject(message, offset);
/* 2860 */             writer.writeMessage(number, value, getMessageFieldSchema(pos));
/*      */           } 
/*      */           break;
/*      */         case 61:
/* 2864 */           if (isOneofPresent(message, number, pos)) {
/* 2865 */             writer.writeBytes(number, (ByteString)unsafe.getObject(message, offset));
/*      */           }
/*      */           break;
/*      */         case 62:
/* 2869 */           if (isOneofPresent(message, number, pos)) {
/* 2870 */             writer.writeUInt32(number, oneofIntAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 63:
/* 2874 */           if (isOneofPresent(message, number, pos)) {
/* 2875 */             writer.writeEnum(number, oneofIntAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 64:
/* 2879 */           if (isOneofPresent(message, number, pos)) {
/* 2880 */             writer.writeSFixed32(number, oneofIntAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 65:
/* 2884 */           if (isOneofPresent(message, number, pos)) {
/* 2885 */             writer.writeSFixed64(number, oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 66:
/* 2889 */           if (isOneofPresent(message, number, pos)) {
/* 2890 */             writer.writeSInt32(number, oneofIntAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 67:
/* 2894 */           if (isOneofPresent(message, number, pos)) {
/* 2895 */             writer.writeSInt64(number, oneofLongAt(message, offset));
/*      */           }
/*      */           break;
/*      */         case 68:
/* 2899 */           if (isOneofPresent(message, number, pos)) {
/* 2900 */             writer.writeGroup(number, unsafe
/* 2901 */                 .getObject(message, offset), getMessageFieldSchema(pos));
/*      */           }
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */     
/*      */     } 
/* 2909 */     while (nextExtension != null) {
/* 2910 */       this.extensionSchema.serializeExtension(writer, nextExtension);
/* 2911 */       nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
/*      */     } 
/* 2913 */     writeUnknownInMessageTo(this.unknownFieldSchema, message, writer);
/*      */   }
/*      */ 
/*      */   
/*      */   private void writeFieldsInAscendingOrderProto3(T message, Writer writer) throws IOException {
/* 2918 */     Iterator<? extends Map.Entry<?, ?>> extensionIterator = null;
/* 2919 */     Map.Entry<?, ?> nextExtension = null;
/* 2920 */     if (this.hasExtensions) {
/* 2921 */       FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
/* 2922 */       if (!extensions.isEmpty()) {
/* 2923 */         extensionIterator = extensions.iterator();
/* 2924 */         nextExtension = extensionIterator.next();
/*      */       } 
/*      */     } 
/*      */     
/* 2928 */     int bufferLength = this.buffer.length;
/* 2929 */     for (int pos = 0; pos < bufferLength; pos += 3) {
/* 2930 */       int typeAndOffset = typeAndOffsetAt(pos);
/* 2931 */       int number = numberAt(pos);
/*      */ 
/*      */       
/* 2934 */       while (nextExtension != null && this.extensionSchema.extensionNumber(nextExtension) <= number) {
/* 2935 */         this.extensionSchema.serializeExtension(writer, nextExtension);
/* 2936 */         nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
/*      */       } 
/*      */       
/* 2939 */       switch (type(typeAndOffset)) {
/*      */         case 0:
/* 2941 */           if (isFieldPresent(message, pos)) {
/* 2942 */             writer.writeDouble(number, doubleAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 1:
/* 2946 */           if (isFieldPresent(message, pos)) {
/* 2947 */             writer.writeFloat(number, floatAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 2:
/* 2951 */           if (isFieldPresent(message, pos)) {
/* 2952 */             writer.writeInt64(number, longAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 3:
/* 2956 */           if (isFieldPresent(message, pos)) {
/* 2957 */             writer.writeUInt64(number, longAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 4:
/* 2961 */           if (isFieldPresent(message, pos)) {
/* 2962 */             writer.writeInt32(number, intAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 5:
/* 2966 */           if (isFieldPresent(message, pos)) {
/* 2967 */             writer.writeFixed64(number, longAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 6:
/* 2971 */           if (isFieldPresent(message, pos)) {
/* 2972 */             writer.writeFixed32(number, intAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 7:
/* 2976 */           if (isFieldPresent(message, pos)) {
/* 2977 */             writer.writeBool(number, booleanAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 8:
/* 2981 */           if (isFieldPresent(message, pos)) {
/* 2982 */             writeString(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
/*      */           }
/*      */           break;
/*      */         case 9:
/* 2986 */           if (isFieldPresent(message, pos)) {
/* 2987 */             Object value = UnsafeUtil.getObject(message, offset(typeAndOffset));
/* 2988 */             writer.writeMessage(number, value, getMessageFieldSchema(pos));
/*      */           } 
/*      */           break;
/*      */         case 10:
/* 2992 */           if (isFieldPresent(message, pos)) {
/* 2993 */             writer.writeBytes(number, 
/* 2994 */                 (ByteString)UnsafeUtil.getObject(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 11:
/* 2998 */           if (isFieldPresent(message, pos)) {
/* 2999 */             writer.writeUInt32(number, intAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 12:
/* 3003 */           if (isFieldPresent(message, pos)) {
/* 3004 */             writer.writeEnum(number, intAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 13:
/* 3008 */           if (isFieldPresent(message, pos)) {
/* 3009 */             writer.writeSFixed32(number, intAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 14:
/* 3013 */           if (isFieldPresent(message, pos)) {
/* 3014 */             writer.writeSFixed64(number, longAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 15:
/* 3018 */           if (isFieldPresent(message, pos)) {
/* 3019 */             writer.writeSInt32(number, intAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 16:
/* 3023 */           if (isFieldPresent(message, pos)) {
/* 3024 */             writer.writeSInt64(number, longAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 17:
/* 3028 */           if (isFieldPresent(message, pos)) {
/* 3029 */             writer.writeGroup(number, 
/*      */                 
/* 3031 */                 UnsafeUtil.getObject(message, offset(typeAndOffset)), 
/* 3032 */                 getMessageFieldSchema(pos));
/*      */           }
/*      */           break;
/*      */         case 18:
/* 3036 */           SchemaUtil.writeDoubleList(
/* 3037 */               numberAt(pos), 
/* 3038 */               (List<Double>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 19:
/* 3043 */           SchemaUtil.writeFloatList(
/* 3044 */               numberAt(pos), 
/* 3045 */               (List<Float>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 20:
/* 3050 */           SchemaUtil.writeInt64List(
/* 3051 */               numberAt(pos), 
/* 3052 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 21:
/* 3057 */           SchemaUtil.writeUInt64List(
/* 3058 */               numberAt(pos), 
/* 3059 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 22:
/* 3064 */           SchemaUtil.writeInt32List(
/* 3065 */               numberAt(pos), 
/* 3066 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 23:
/* 3071 */           SchemaUtil.writeFixed64List(
/* 3072 */               numberAt(pos), 
/* 3073 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 24:
/* 3078 */           SchemaUtil.writeFixed32List(
/* 3079 */               numberAt(pos), 
/* 3080 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 25:
/* 3085 */           SchemaUtil.writeBoolList(
/* 3086 */               numberAt(pos), 
/* 3087 */               (List<Boolean>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 26:
/* 3092 */           SchemaUtil.writeStringList(
/* 3093 */               numberAt(pos), 
/* 3094 */               (List<String>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
/*      */           break;
/*      */         
/*      */         case 27:
/* 3098 */           SchemaUtil.writeMessageList(
/* 3099 */               numberAt(pos), 
/* 3100 */               (List)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, 
/*      */               
/* 3102 */               getMessageFieldSchema(pos));
/*      */           break;
/*      */         case 28:
/* 3105 */           SchemaUtil.writeBytesList(
/* 3106 */               numberAt(pos), 
/* 3107 */               (List<ByteString>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
/*      */           break;
/*      */         
/*      */         case 29:
/* 3111 */           SchemaUtil.writeUInt32List(
/* 3112 */               numberAt(pos), 
/* 3113 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 30:
/* 3118 */           SchemaUtil.writeEnumList(
/* 3119 */               numberAt(pos), 
/* 3120 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 31:
/* 3125 */           SchemaUtil.writeSFixed32List(
/* 3126 */               numberAt(pos), 
/* 3127 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 32:
/* 3132 */           SchemaUtil.writeSFixed64List(
/* 3133 */               numberAt(pos), 
/* 3134 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 33:
/* 3139 */           SchemaUtil.writeSInt32List(
/* 3140 */               numberAt(pos), 
/* 3141 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 34:
/* 3146 */           SchemaUtil.writeSInt64List(
/* 3147 */               numberAt(pos), 
/* 3148 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case 35:
/* 3154 */           SchemaUtil.writeDoubleList(
/* 3155 */               numberAt(pos), 
/* 3156 */               (List<Double>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 36:
/* 3161 */           SchemaUtil.writeFloatList(
/* 3162 */               numberAt(pos), 
/* 3163 */               (List<Float>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 37:
/* 3168 */           SchemaUtil.writeInt64List(
/* 3169 */               numberAt(pos), 
/* 3170 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 38:
/* 3175 */           SchemaUtil.writeUInt64List(
/* 3176 */               numberAt(pos), 
/* 3177 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 39:
/* 3182 */           SchemaUtil.writeInt32List(
/* 3183 */               numberAt(pos), 
/* 3184 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 40:
/* 3189 */           SchemaUtil.writeFixed64List(
/* 3190 */               numberAt(pos), 
/* 3191 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 41:
/* 3196 */           SchemaUtil.writeFixed32List(
/* 3197 */               numberAt(pos), 
/* 3198 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case 42:
/* 3204 */           SchemaUtil.writeBoolList(
/* 3205 */               numberAt(pos), 
/* 3206 */               (List<Boolean>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 43:
/* 3211 */           SchemaUtil.writeUInt32List(
/* 3212 */               numberAt(pos), 
/* 3213 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 44:
/* 3218 */           SchemaUtil.writeEnumList(
/* 3219 */               numberAt(pos), 
/* 3220 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 45:
/* 3225 */           SchemaUtil.writeSFixed32List(
/* 3226 */               numberAt(pos), 
/* 3227 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 46:
/* 3232 */           SchemaUtil.writeSFixed64List(
/* 3233 */               numberAt(pos), 
/* 3234 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 47:
/* 3239 */           SchemaUtil.writeSInt32List(
/* 3240 */               numberAt(pos), 
/* 3241 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 48:
/* 3246 */           SchemaUtil.writeSInt64List(
/* 3247 */               numberAt(pos), 
/* 3248 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 49:
/* 3253 */           SchemaUtil.writeGroupList(
/* 3254 */               numberAt(pos), 
/* 3255 */               (List)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, 
/*      */               
/* 3257 */               getMessageFieldSchema(pos));
/*      */           break;
/*      */         
/*      */         case 50:
/* 3261 */           writeMapHelper(writer, number, UnsafeUtil.getObject(message, offset(typeAndOffset)), pos);
/*      */           break;
/*      */         case 51:
/* 3264 */           if (isOneofPresent(message, number, pos)) {
/* 3265 */             writer.writeDouble(number, oneofDoubleAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 52:
/* 3269 */           if (isOneofPresent(message, number, pos)) {
/* 3270 */             writer.writeFloat(number, oneofFloatAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 53:
/* 3274 */           if (isOneofPresent(message, number, pos)) {
/* 3275 */             writer.writeInt64(number, oneofLongAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 54:
/* 3279 */           if (isOneofPresent(message, number, pos)) {
/* 3280 */             writer.writeUInt64(number, oneofLongAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 55:
/* 3284 */           if (isOneofPresent(message, number, pos)) {
/* 3285 */             writer.writeInt32(number, oneofIntAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 56:
/* 3289 */           if (isOneofPresent(message, number, pos)) {
/* 3290 */             writer.writeFixed64(number, oneofLongAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 57:
/* 3294 */           if (isOneofPresent(message, number, pos)) {
/* 3295 */             writer.writeFixed32(number, oneofIntAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 58:
/* 3299 */           if (isOneofPresent(message, number, pos)) {
/* 3300 */             writer.writeBool(number, oneofBooleanAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 59:
/* 3304 */           if (isOneofPresent(message, number, pos)) {
/* 3305 */             writeString(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
/*      */           }
/*      */           break;
/*      */         case 60:
/* 3309 */           if (isOneofPresent(message, number, pos)) {
/* 3310 */             Object value = UnsafeUtil.getObject(message, offset(typeAndOffset));
/* 3311 */             writer.writeMessage(number, value, getMessageFieldSchema(pos));
/*      */           } 
/*      */           break;
/*      */         case 61:
/* 3315 */           if (isOneofPresent(message, number, pos)) {
/* 3316 */             writer.writeBytes(number, 
/* 3317 */                 (ByteString)UnsafeUtil.getObject(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 62:
/* 3321 */           if (isOneofPresent(message, number, pos)) {
/* 3322 */             writer.writeUInt32(number, oneofIntAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 63:
/* 3326 */           if (isOneofPresent(message, number, pos)) {
/* 3327 */             writer.writeEnum(number, oneofIntAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 64:
/* 3331 */           if (isOneofPresent(message, number, pos)) {
/* 3332 */             writer.writeSFixed32(number, oneofIntAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 65:
/* 3336 */           if (isOneofPresent(message, number, pos)) {
/* 3337 */             writer.writeSFixed64(number, oneofLongAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 66:
/* 3341 */           if (isOneofPresent(message, number, pos)) {
/* 3342 */             writer.writeSInt32(number, oneofIntAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 67:
/* 3346 */           if (isOneofPresent(message, number, pos)) {
/* 3347 */             writer.writeSInt64(number, oneofLongAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 68:
/* 3351 */           if (isOneofPresent(message, number, pos)) {
/* 3352 */             writer.writeGroup(number, 
/*      */                 
/* 3354 */                 UnsafeUtil.getObject(message, offset(typeAndOffset)), 
/* 3355 */                 getMessageFieldSchema(pos));
/*      */           }
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */     
/*      */     } 
/* 3363 */     while (nextExtension != null) {
/* 3364 */       this.extensionSchema.serializeExtension(writer, nextExtension);
/* 3365 */       nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
/*      */     } 
/* 3367 */     writeUnknownInMessageTo(this.unknownFieldSchema, message, writer);
/*      */   }
/*      */ 
/*      */   
/*      */   private void writeFieldsInDescendingOrder(T message, Writer writer) throws IOException {
/* 3372 */     writeUnknownInMessageTo(this.unknownFieldSchema, message, writer);
/*      */     
/* 3374 */     Iterator<? extends Map.Entry<?, ?>> extensionIterator = null;
/* 3375 */     Map.Entry<?, ?> nextExtension = null;
/* 3376 */     if (this.hasExtensions) {
/* 3377 */       FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
/* 3378 */       if (!extensions.isEmpty()) {
/* 3379 */         extensionIterator = extensions.descendingIterator();
/* 3380 */         nextExtension = extensionIterator.next();
/*      */       } 
/*      */     } 
/*      */     
/* 3384 */     for (int pos = this.buffer.length - 3; pos >= 0; pos -= 3) {
/* 3385 */       int typeAndOffset = typeAndOffsetAt(pos);
/* 3386 */       int number = numberAt(pos);
/*      */ 
/*      */       
/* 3389 */       while (nextExtension != null && this.extensionSchema.extensionNumber(nextExtension) > number) {
/* 3390 */         this.extensionSchema.serializeExtension(writer, nextExtension);
/* 3391 */         nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
/*      */       } 
/*      */       
/* 3394 */       switch (type(typeAndOffset)) {
/*      */         case 0:
/* 3396 */           if (isFieldPresent(message, pos)) {
/* 3397 */             writer.writeDouble(number, doubleAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 1:
/* 3401 */           if (isFieldPresent(message, pos)) {
/* 3402 */             writer.writeFloat(number, floatAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 2:
/* 3406 */           if (isFieldPresent(message, pos)) {
/* 3407 */             writer.writeInt64(number, longAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 3:
/* 3411 */           if (isFieldPresent(message, pos)) {
/* 3412 */             writer.writeUInt64(number, longAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 4:
/* 3416 */           if (isFieldPresent(message, pos)) {
/* 3417 */             writer.writeInt32(number, intAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 5:
/* 3421 */           if (isFieldPresent(message, pos)) {
/* 3422 */             writer.writeFixed64(number, longAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 6:
/* 3426 */           if (isFieldPresent(message, pos)) {
/* 3427 */             writer.writeFixed32(number, intAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 7:
/* 3431 */           if (isFieldPresent(message, pos)) {
/* 3432 */             writer.writeBool(number, booleanAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 8:
/* 3436 */           if (isFieldPresent(message, pos)) {
/* 3437 */             writeString(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
/*      */           }
/*      */           break;
/*      */         case 9:
/* 3441 */           if (isFieldPresent(message, pos)) {
/* 3442 */             Object value = UnsafeUtil.getObject(message, offset(typeAndOffset));
/* 3443 */             writer.writeMessage(number, value, getMessageFieldSchema(pos));
/*      */           } 
/*      */           break;
/*      */         case 10:
/* 3447 */           if (isFieldPresent(message, pos)) {
/* 3448 */             writer.writeBytes(number, 
/* 3449 */                 (ByteString)UnsafeUtil.getObject(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 11:
/* 3453 */           if (isFieldPresent(message, pos)) {
/* 3454 */             writer.writeUInt32(number, intAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 12:
/* 3458 */           if (isFieldPresent(message, pos)) {
/* 3459 */             writer.writeEnum(number, intAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 13:
/* 3463 */           if (isFieldPresent(message, pos)) {
/* 3464 */             writer.writeSFixed32(number, intAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 14:
/* 3468 */           if (isFieldPresent(message, pos)) {
/* 3469 */             writer.writeSFixed64(number, longAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 15:
/* 3473 */           if (isFieldPresent(message, pos)) {
/* 3474 */             writer.writeSInt32(number, intAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 16:
/* 3478 */           if (isFieldPresent(message, pos)) {
/* 3479 */             writer.writeSInt64(number, longAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 17:
/* 3483 */           if (isFieldPresent(message, pos)) {
/* 3484 */             writer.writeGroup(number, 
/*      */                 
/* 3486 */                 UnsafeUtil.getObject(message, offset(typeAndOffset)), 
/* 3487 */                 getMessageFieldSchema(pos));
/*      */           }
/*      */           break;
/*      */         case 18:
/* 3491 */           SchemaUtil.writeDoubleList(
/* 3492 */               numberAt(pos), 
/* 3493 */               (List<Double>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 19:
/* 3498 */           SchemaUtil.writeFloatList(
/* 3499 */               numberAt(pos), 
/* 3500 */               (List<Float>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 20:
/* 3505 */           SchemaUtil.writeInt64List(
/* 3506 */               numberAt(pos), 
/* 3507 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 21:
/* 3512 */           SchemaUtil.writeUInt64List(
/* 3513 */               numberAt(pos), 
/* 3514 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 22:
/* 3519 */           SchemaUtil.writeInt32List(
/* 3520 */               numberAt(pos), 
/* 3521 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 23:
/* 3526 */           SchemaUtil.writeFixed64List(
/* 3527 */               numberAt(pos), 
/* 3528 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 24:
/* 3533 */           SchemaUtil.writeFixed32List(
/* 3534 */               numberAt(pos), 
/* 3535 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 25:
/* 3540 */           SchemaUtil.writeBoolList(
/* 3541 */               numberAt(pos), 
/* 3542 */               (List<Boolean>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 26:
/* 3547 */           SchemaUtil.writeStringList(
/* 3548 */               numberAt(pos), 
/* 3549 */               (List<String>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
/*      */           break;
/*      */         
/*      */         case 27:
/* 3553 */           SchemaUtil.writeMessageList(
/* 3554 */               numberAt(pos), 
/* 3555 */               (List)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, 
/*      */               
/* 3557 */               getMessageFieldSchema(pos));
/*      */           break;
/*      */         case 28:
/* 3560 */           SchemaUtil.writeBytesList(
/* 3561 */               numberAt(pos), 
/* 3562 */               (List<ByteString>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
/*      */           break;
/*      */         
/*      */         case 29:
/* 3566 */           SchemaUtil.writeUInt32List(
/* 3567 */               numberAt(pos), 
/* 3568 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 30:
/* 3573 */           SchemaUtil.writeEnumList(
/* 3574 */               numberAt(pos), 
/* 3575 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 31:
/* 3580 */           SchemaUtil.writeSFixed32List(
/* 3581 */               numberAt(pos), 
/* 3582 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 32:
/* 3587 */           SchemaUtil.writeSFixed64List(
/* 3588 */               numberAt(pos), 
/* 3589 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 33:
/* 3594 */           SchemaUtil.writeSInt32List(
/* 3595 */               numberAt(pos), 
/* 3596 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 34:
/* 3601 */           SchemaUtil.writeSInt64List(
/* 3602 */               numberAt(pos), 
/* 3603 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 35:
/* 3608 */           SchemaUtil.writeDoubleList(
/* 3609 */               numberAt(pos), 
/* 3610 */               (List<Double>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 36:
/* 3615 */           SchemaUtil.writeFloatList(
/* 3616 */               numberAt(pos), 
/* 3617 */               (List<Float>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 37:
/* 3622 */           SchemaUtil.writeInt64List(
/* 3623 */               numberAt(pos), 
/* 3624 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 38:
/* 3629 */           SchemaUtil.writeUInt64List(
/* 3630 */               numberAt(pos), 
/* 3631 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 39:
/* 3636 */           SchemaUtil.writeInt32List(
/* 3637 */               numberAt(pos), 
/* 3638 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 40:
/* 3643 */           SchemaUtil.writeFixed64List(
/* 3644 */               numberAt(pos), 
/* 3645 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 41:
/* 3650 */           SchemaUtil.writeFixed32List(
/* 3651 */               numberAt(pos), 
/* 3652 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case 42:
/* 3658 */           SchemaUtil.writeBoolList(
/* 3659 */               numberAt(pos), 
/* 3660 */               (List<Boolean>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 43:
/* 3665 */           SchemaUtil.writeUInt32List(
/* 3666 */               numberAt(pos), 
/* 3667 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 44:
/* 3672 */           SchemaUtil.writeEnumList(
/* 3673 */               numberAt(pos), 
/* 3674 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 45:
/* 3679 */           SchemaUtil.writeSFixed32List(
/* 3680 */               numberAt(pos), 
/* 3681 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 46:
/* 3686 */           SchemaUtil.writeSFixed64List(
/* 3687 */               numberAt(pos), 
/* 3688 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 47:
/* 3693 */           SchemaUtil.writeSInt32List(
/* 3694 */               numberAt(pos), 
/* 3695 */               (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 48:
/* 3700 */           SchemaUtil.writeSInt64List(
/* 3701 */               numberAt(pos), 
/* 3702 */               (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 49:
/* 3707 */           SchemaUtil.writeGroupList(
/* 3708 */               numberAt(pos), 
/* 3709 */               (List)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, 
/*      */               
/* 3711 */               getMessageFieldSchema(pos));
/*      */           break;
/*      */         
/*      */         case 50:
/* 3715 */           writeMapHelper(writer, number, UnsafeUtil.getObject(message, offset(typeAndOffset)), pos);
/*      */           break;
/*      */         case 51:
/* 3718 */           if (isOneofPresent(message, number, pos)) {
/* 3719 */             writer.writeDouble(number, oneofDoubleAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 52:
/* 3723 */           if (isOneofPresent(message, number, pos)) {
/* 3724 */             writer.writeFloat(number, oneofFloatAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 53:
/* 3728 */           if (isOneofPresent(message, number, pos)) {
/* 3729 */             writer.writeInt64(number, oneofLongAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 54:
/* 3733 */           if (isOneofPresent(message, number, pos)) {
/* 3734 */             writer.writeUInt64(number, oneofLongAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 55:
/* 3738 */           if (isOneofPresent(message, number, pos)) {
/* 3739 */             writer.writeInt32(number, oneofIntAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 56:
/* 3743 */           if (isOneofPresent(message, number, pos)) {
/* 3744 */             writer.writeFixed64(number, oneofLongAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 57:
/* 3748 */           if (isOneofPresent(message, number, pos)) {
/* 3749 */             writer.writeFixed32(number, oneofIntAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 58:
/* 3753 */           if (isOneofPresent(message, number, pos)) {
/* 3754 */             writer.writeBool(number, oneofBooleanAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 59:
/* 3758 */           if (isOneofPresent(message, number, pos)) {
/* 3759 */             writeString(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
/*      */           }
/*      */           break;
/*      */         case 60:
/* 3763 */           if (isOneofPresent(message, number, pos)) {
/* 3764 */             Object value = UnsafeUtil.getObject(message, offset(typeAndOffset));
/* 3765 */             writer.writeMessage(number, value, getMessageFieldSchema(pos));
/*      */           } 
/*      */           break;
/*      */         case 61:
/* 3769 */           if (isOneofPresent(message, number, pos)) {
/* 3770 */             writer.writeBytes(number, 
/* 3771 */                 (ByteString)UnsafeUtil.getObject(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 62:
/* 3775 */           if (isOneofPresent(message, number, pos)) {
/* 3776 */             writer.writeUInt32(number, oneofIntAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 63:
/* 3780 */           if (isOneofPresent(message, number, pos)) {
/* 3781 */             writer.writeEnum(number, oneofIntAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 64:
/* 3785 */           if (isOneofPresent(message, number, pos)) {
/* 3786 */             writer.writeSFixed32(number, oneofIntAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 65:
/* 3790 */           if (isOneofPresent(message, number, pos)) {
/* 3791 */             writer.writeSFixed64(number, oneofLongAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 66:
/* 3795 */           if (isOneofPresent(message, number, pos)) {
/* 3796 */             writer.writeSInt32(number, oneofIntAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 67:
/* 3800 */           if (isOneofPresent(message, number, pos)) {
/* 3801 */             writer.writeSInt64(number, oneofLongAt(message, offset(typeAndOffset)));
/*      */           }
/*      */           break;
/*      */         case 68:
/* 3805 */           if (isOneofPresent(message, number, pos)) {
/* 3806 */             writer.writeGroup(number, 
/*      */                 
/* 3808 */                 UnsafeUtil.getObject(message, offset(typeAndOffset)), 
/* 3809 */                 getMessageFieldSchema(pos));
/*      */           }
/*      */           break;
/*      */       } 
/*      */ 
/*      */     
/*      */     } 
/* 3816 */     while (nextExtension != null) {
/* 3817 */       this.extensionSchema.serializeExtension(writer, nextExtension);
/* 3818 */       nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private <K, V> void writeMapHelper(Writer writer, int number, Object mapField, int pos) throws IOException {
/* 3825 */     if (mapField != null) {
/* 3826 */       writer.writeMap(number, this.mapFieldSchema
/*      */           
/* 3828 */           .forMapMetadata(getMapFieldDefaultEntry(pos)), this.mapFieldSchema
/* 3829 */           .forMapData(mapField));
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private <UT, UB> void writeUnknownInMessageTo(UnknownFieldSchema<UT, UB> schema, T message, Writer writer) throws IOException {
/* 3835 */     schema.writeTo(schema.getFromMessage(message), writer);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void mergeFrom(T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3841 */     if (extensionRegistry == null) {
/* 3842 */       throw new NullPointerException();
/*      */     }
/* 3844 */     mergeFromHelper(this.unknownFieldSchema, this.extensionSchema, message, reader, extensionRegistry);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private <UT, UB, ET extends FieldSet.FieldDescriptorLite<ET>> void mergeFromHelper(UnknownFieldSchema<UT, UB> unknownFieldSchema, ExtensionSchema<ET> extensionSchema, T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3858 */     UB unknownFields = null;
/* 3859 */     FieldSet<ET> extensions = null;
/*      */     try {
/*      */       while (true) {
/* 3862 */         int number = reader.getFieldNumber();
/* 3863 */         int pos = positionForFieldNumber(number);
/* 3864 */         if (pos < 0) {
/* 3865 */           if (number == Integer.MAX_VALUE) {
/*      */             return;
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 3872 */           Object extension = !this.hasExtensions ? null : extensionSchema.findExtensionByNumber(extensionRegistry, this.defaultInstance, number);
/*      */           
/* 3874 */           if (extension != null) {
/* 3875 */             if (extensions == null) {
/* 3876 */               extensions = extensionSchema.getMutableExtensions(message);
/*      */             }
/*      */             
/* 3879 */             unknownFields = extensionSchema.parseExtension(reader, extension, extensionRegistry, extensions, unknownFields, unknownFieldSchema);
/*      */ 
/*      */ 
/*      */             
/*      */             continue;
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/* 3888 */           if (unknownFieldSchema.shouldDiscardUnknownFields(reader)) {
/* 3889 */             if (reader.skipField()) {
/*      */               continue;
/*      */             }
/*      */           } else {
/* 3893 */             if (unknownFields == null) {
/* 3894 */               unknownFields = unknownFieldSchema.getBuilderFromMessage(message);
/*      */             }
/*      */             
/* 3897 */             if (unknownFieldSchema.mergeOneFieldFrom(unknownFields, reader)) {
/*      */               continue;
/*      */             }
/*      */           } 
/*      */           
/*      */           return;
/*      */         } 
/* 3904 */         int typeAndOffset = typeAndOffsetAt(pos); try {
/*      */           int j; List<Integer> enumList; int enumValue;
/*      */           Internal.EnumVerifier enumVerifier;
/* 3907 */           switch (type(typeAndOffset)) {
/*      */             case 0:
/* 3909 */               UnsafeUtil.putDouble(message, offset(typeAndOffset), reader.readDouble());
/* 3910 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */             case 1:
/* 3913 */               UnsafeUtil.putFloat(message, offset(typeAndOffset), reader.readFloat());
/* 3914 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */             case 2:
/* 3917 */               UnsafeUtil.putLong(message, offset(typeAndOffset), reader.readInt64());
/* 3918 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */             case 3:
/* 3921 */               UnsafeUtil.putLong(message, offset(typeAndOffset), reader.readUInt64());
/* 3922 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */             case 4:
/* 3925 */               UnsafeUtil.putInt(message, offset(typeAndOffset), reader.readInt32());
/* 3926 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */             case 5:
/* 3929 */               UnsafeUtil.putLong(message, offset(typeAndOffset), reader.readFixed64());
/* 3930 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */             case 6:
/* 3933 */               UnsafeUtil.putInt(message, offset(typeAndOffset), reader.readFixed32());
/* 3934 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */             case 7:
/* 3937 */               UnsafeUtil.putBoolean(message, offset(typeAndOffset), reader.readBool());
/* 3938 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */             case 8:
/* 3941 */               readString(message, typeAndOffset, reader);
/* 3942 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */             
/*      */             case 9:
/* 3946 */               if (isFieldPresent(message, pos)) {
/*      */                 
/* 3948 */                 Object mergedResult = Internal.mergeMessage(
/* 3949 */                     UnsafeUtil.getObject(message, offset(typeAndOffset)), reader
/* 3950 */                     .readMessageBySchemaWithCheck(
/* 3951 */                       getMessageFieldSchema(pos), extensionRegistry));
/* 3952 */                 UnsafeUtil.putObject(message, offset(typeAndOffset), mergedResult); continue;
/*      */               } 
/* 3954 */               UnsafeUtil.putObject(message, 
/*      */                   
/* 3956 */                   offset(typeAndOffset), reader
/* 3957 */                   .readMessageBySchemaWithCheck(
/* 3958 */                     getMessageFieldSchema(pos), extensionRegistry));
/* 3959 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */ 
/*      */             
/*      */             case 10:
/* 3964 */               UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readBytes());
/* 3965 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */             case 11:
/* 3968 */               UnsafeUtil.putInt(message, offset(typeAndOffset), reader.readUInt32());
/* 3969 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */             
/*      */             case 12:
/* 3973 */               j = reader.readEnum();
/* 3974 */               enumVerifier = getEnumFieldVerifier(pos);
/* 3975 */               if (enumVerifier == null || enumVerifier.isInRange(j)) {
/* 3976 */                 UnsafeUtil.putInt(message, offset(typeAndOffset), j);
/* 3977 */                 setFieldPresent(message, pos);
/*      */                 continue;
/*      */               } 
/* 3980 */               unknownFields = SchemaUtil.storeUnknownEnum(number, j, unknownFields, unknownFieldSchema);
/*      */               continue;
/*      */ 
/*      */ 
/*      */             
/*      */             case 13:
/* 3986 */               UnsafeUtil.putInt(message, offset(typeAndOffset), reader.readSFixed32());
/* 3987 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */             case 14:
/* 3990 */               UnsafeUtil.putLong(message, offset(typeAndOffset), reader.readSFixed64());
/* 3991 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */             case 15:
/* 3994 */               UnsafeUtil.putInt(message, offset(typeAndOffset), reader.readSInt32());
/* 3995 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */             case 16:
/* 3998 */               UnsafeUtil.putLong(message, offset(typeAndOffset), reader.readSInt64());
/* 3999 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */             
/*      */             case 17:
/* 4003 */               if (isFieldPresent(message, pos)) {
/*      */                 
/* 4005 */                 Object mergedResult = Internal.mergeMessage(
/* 4006 */                     UnsafeUtil.getObject(message, offset(typeAndOffset)), reader
/* 4007 */                     .readGroupBySchemaWithCheck(
/* 4008 */                       getMessageFieldSchema(pos), extensionRegistry));
/* 4009 */                 UnsafeUtil.putObject(message, offset(typeAndOffset), mergedResult); continue;
/*      */               } 
/* 4011 */               UnsafeUtil.putObject(message, 
/*      */                   
/* 4013 */                   offset(typeAndOffset), reader
/* 4014 */                   .readGroupBySchemaWithCheck(
/* 4015 */                     getMessageFieldSchema(pos), extensionRegistry));
/* 4016 */               setFieldPresent(message, pos);
/*      */               continue;
/*      */ 
/*      */             
/*      */             case 18:
/* 4021 */               reader.readDoubleList(this.listFieldSchema
/* 4022 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 19:
/* 4025 */               reader.readFloatList(this.listFieldSchema
/* 4026 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 20:
/* 4029 */               reader.readInt64List(this.listFieldSchema
/* 4030 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 21:
/* 4033 */               reader.readUInt64List(this.listFieldSchema
/* 4034 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 22:
/* 4037 */               reader.readInt32List(this.listFieldSchema
/* 4038 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 23:
/* 4041 */               reader.readFixed64List(this.listFieldSchema
/* 4042 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 24:
/* 4045 */               reader.readFixed32List(this.listFieldSchema
/* 4046 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 25:
/* 4049 */               reader.readBoolList(this.listFieldSchema
/* 4050 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 26:
/* 4053 */               readStringList(message, typeAndOffset, reader);
/*      */               continue;
/*      */             
/*      */             case 27:
/* 4057 */               readMessageList(message, typeAndOffset, reader, 
/*      */ 
/*      */ 
/*      */                   
/* 4061 */                   getMessageFieldSchema(pos), extensionRegistry);
/*      */               continue;
/*      */ 
/*      */             
/*      */             case 28:
/* 4066 */               reader.readBytesList(this.listFieldSchema
/* 4067 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 29:
/* 4070 */               reader.readUInt32List(this.listFieldSchema
/* 4071 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */ 
/*      */             
/*      */             case 30:
/* 4076 */               enumList = this.listFieldSchema.mutableListAt(message, offset(typeAndOffset));
/* 4077 */               reader.readEnumList(enumList);
/*      */               
/* 4079 */               unknownFields = SchemaUtil.filterUnknownEnumList(number, enumList, 
/*      */ 
/*      */                   
/* 4082 */                   getEnumFieldVerifier(pos), unknownFields, unknownFieldSchema);
/*      */               continue;
/*      */ 
/*      */ 
/*      */             
/*      */             case 31:
/* 4088 */               reader.readSFixed32List(this.listFieldSchema
/* 4089 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 32:
/* 4092 */               reader.readSFixed64List(this.listFieldSchema
/* 4093 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 33:
/* 4096 */               reader.readSInt32List(this.listFieldSchema
/* 4097 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 34:
/* 4100 */               reader.readSInt64List(this.listFieldSchema
/* 4101 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 35:
/* 4104 */               reader.readDoubleList(this.listFieldSchema
/* 4105 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 36:
/* 4108 */               reader.readFloatList(this.listFieldSchema
/* 4109 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 37:
/* 4112 */               reader.readInt64List(this.listFieldSchema
/* 4113 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 38:
/* 4116 */               reader.readUInt64List(this.listFieldSchema
/* 4117 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 39:
/* 4120 */               reader.readInt32List(this.listFieldSchema
/* 4121 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 40:
/* 4124 */               reader.readFixed64List(this.listFieldSchema
/* 4125 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 41:
/* 4128 */               reader.readFixed32List(this.listFieldSchema
/* 4129 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 42:
/* 4132 */               reader.readBoolList(this.listFieldSchema
/* 4133 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 43:
/* 4136 */               reader.readUInt32List(this.listFieldSchema
/* 4137 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */ 
/*      */             
/*      */             case 44:
/* 4142 */               enumList = this.listFieldSchema.mutableListAt(message, offset(typeAndOffset));
/* 4143 */               reader.readEnumList(enumList);
/*      */               
/* 4145 */               unknownFields = SchemaUtil.filterUnknownEnumList(number, enumList, 
/*      */ 
/*      */                   
/* 4148 */                   getEnumFieldVerifier(pos), unknownFields, unknownFieldSchema);
/*      */               continue;
/*      */ 
/*      */ 
/*      */             
/*      */             case 45:
/* 4154 */               reader.readSFixed32List(this.listFieldSchema
/* 4155 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 46:
/* 4158 */               reader.readSFixed64List(this.listFieldSchema
/* 4159 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 47:
/* 4162 */               reader.readSInt32List(this.listFieldSchema
/* 4163 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             case 48:
/* 4166 */               reader.readSInt64List(this.listFieldSchema
/* 4167 */                   .mutableListAt(message, offset(typeAndOffset)));
/*      */               continue;
/*      */             
/*      */             case 49:
/* 4171 */               readGroupList(message, 
/*      */                   
/* 4173 */                   offset(typeAndOffset), reader, 
/*      */                   
/* 4175 */                   getMessageFieldSchema(pos), extensionRegistry);
/*      */               continue;
/*      */ 
/*      */             
/*      */             case 50:
/* 4180 */               mergeMap(message, pos, getMapFieldDefaultEntry(pos), extensionRegistry, reader);
/*      */               continue;
/*      */             case 51:
/* 4183 */               UnsafeUtil.putObject(message, 
/* 4184 */                   offset(typeAndOffset), Double.valueOf(reader.readDouble()));
/* 4185 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             case 52:
/* 4188 */               UnsafeUtil.putObject(message, 
/* 4189 */                   offset(typeAndOffset), Float.valueOf(reader.readFloat()));
/* 4190 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             case 53:
/* 4193 */               UnsafeUtil.putObject(message, 
/* 4194 */                   offset(typeAndOffset), Long.valueOf(reader.readInt64()));
/* 4195 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             case 54:
/* 4198 */               UnsafeUtil.putObject(message, 
/* 4199 */                   offset(typeAndOffset), Long.valueOf(reader.readUInt64()));
/* 4200 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             case 55:
/* 4203 */               UnsafeUtil.putObject(message, 
/* 4204 */                   offset(typeAndOffset), Integer.valueOf(reader.readInt32()));
/* 4205 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             case 56:
/* 4208 */               UnsafeUtil.putObject(message, 
/* 4209 */                   offset(typeAndOffset), Long.valueOf(reader.readFixed64()));
/* 4210 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             case 57:
/* 4213 */               UnsafeUtil.putObject(message, 
/* 4214 */                   offset(typeAndOffset), Integer.valueOf(reader.readFixed32()));
/* 4215 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             case 58:
/* 4218 */               UnsafeUtil.putObject(message, 
/* 4219 */                   offset(typeAndOffset), Boolean.valueOf(reader.readBool()));
/* 4220 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             case 59:
/* 4223 */               readString(message, typeAndOffset, reader);
/* 4224 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             case 60:
/* 4227 */               if (isOneofPresent(message, number, pos)) {
/*      */                 
/* 4229 */                 Object mergedResult = Internal.mergeMessage(
/* 4230 */                     UnsafeUtil.getObject(message, offset(typeAndOffset)), reader
/* 4231 */                     .readMessageBySchemaWithCheck(
/* 4232 */                       getMessageFieldSchema(pos), extensionRegistry));
/* 4233 */                 UnsafeUtil.putObject(message, offset(typeAndOffset), mergedResult);
/*      */               } else {
/* 4235 */                 UnsafeUtil.putObject(message, 
/*      */                     
/* 4237 */                     offset(typeAndOffset), reader
/* 4238 */                     .readMessageBySchemaWithCheck(
/* 4239 */                       getMessageFieldSchema(pos), extensionRegistry));
/* 4240 */                 setFieldPresent(message, pos);
/*      */               } 
/* 4242 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             case 61:
/* 4245 */               UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readBytes());
/* 4246 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             case 62:
/* 4249 */               UnsafeUtil.putObject(message, 
/* 4250 */                   offset(typeAndOffset), Integer.valueOf(reader.readUInt32()));
/* 4251 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             
/*      */             case 63:
/* 4255 */               enumValue = reader.readEnum();
/* 4256 */               enumVerifier = getEnumFieldVerifier(pos);
/* 4257 */               if (enumVerifier == null || enumVerifier.isInRange(enumValue)) {
/* 4258 */                 UnsafeUtil.putObject(message, offset(typeAndOffset), Integer.valueOf(enumValue));
/* 4259 */                 setOneofPresent(message, number, pos);
/*      */                 continue;
/*      */               } 
/* 4262 */               unknownFields = SchemaUtil.storeUnknownEnum(number, enumValue, unknownFields, unknownFieldSchema);
/*      */               continue;
/*      */ 
/*      */ 
/*      */             
/*      */             case 64:
/* 4268 */               UnsafeUtil.putObject(message, 
/* 4269 */                   offset(typeAndOffset), Integer.valueOf(reader.readSFixed32()));
/* 4270 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             case 65:
/* 4273 */               UnsafeUtil.putObject(message, 
/* 4274 */                   offset(typeAndOffset), Long.valueOf(reader.readSFixed64()));
/* 4275 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             case 66:
/* 4278 */               UnsafeUtil.putObject(message, 
/* 4279 */                   offset(typeAndOffset), Integer.valueOf(reader.readSInt32()));
/* 4280 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             case 67:
/* 4283 */               UnsafeUtil.putObject(message, 
/* 4284 */                   offset(typeAndOffset), Long.valueOf(reader.readSInt64()));
/* 4285 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */             case 68:
/* 4288 */               UnsafeUtil.putObject(message, 
/*      */                   
/* 4290 */                   offset(typeAndOffset), reader
/* 4291 */                   .readGroupBySchemaWithCheck(getMessageFieldSchema(pos), extensionRegistry));
/* 4292 */               setOneofPresent(message, number, pos);
/*      */               continue;
/*      */           } 
/*      */           
/* 4296 */           if (unknownFields == null) {
/* 4297 */             unknownFields = unknownFieldSchema.newBuilder();
/*      */           }
/* 4299 */           if (!unknownFieldSchema.mergeOneFieldFrom(unknownFields, reader))
/*      */           {
/*      */             return;
/*      */           }
/*      */         }
/* 4304 */         catch (InvalidWireTypeException e) {
/*      */ 
/*      */           
/* 4307 */           if (unknownFieldSchema.shouldDiscardUnknownFields(reader)) {
/* 4308 */             if (!reader.skipField())
/*      */               return; 
/*      */             continue;
/*      */           } 
/* 4312 */           if (unknownFields == null) {
/* 4313 */             unknownFields = unknownFieldSchema.getBuilderFromMessage(message);
/*      */           }
/* 4315 */           if (!unknownFieldSchema.mergeOneFieldFrom(unknownFields, reader)) {
/*      */             break;
/*      */           }
/*      */         } 
/*      */       } 
/*      */       return;
/*      */     } finally {
/* 4322 */       for (int i = this.checkInitializedCount; i < this.repeatedFieldOffsetStart; i++)
/*      */       {
/* 4324 */         unknownFields = filterMapUnknownEnumValues(message, this.intArray[i], unknownFields, unknownFieldSchema);
/*      */       }
/* 4326 */       if (unknownFields != null) {
/* 4327 */         unknownFieldSchema.setBuilderToMessage(message, unknownFields);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   static UnknownFieldSetLite getMutableUnknownFields(Object message) {
/* 4334 */     UnknownFieldSetLite unknownFields = ((GeneratedMessageLite)message).unknownFields;
/* 4335 */     if (unknownFields == UnknownFieldSetLite.getDefaultInstance()) {
/* 4336 */       unknownFields = UnknownFieldSetLite.newInstance();
/* 4337 */       ((GeneratedMessageLite)message).unknownFields = unknownFields;
/*      */     } 
/* 4339 */     return unknownFields;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int decodeMapEntryValue(byte[] data, int position, int limit, WireFormat.FieldType fieldType, Class<?> messageType, ArrayDecoders.Registers registers) throws IOException {
/* 4351 */     switch (fieldType) {
/*      */       case BOOL:
/* 4353 */         position = ArrayDecoders.decodeVarint64(data, position, registers);
/* 4354 */         registers.object1 = Boolean.valueOf((registers.long1 != 0L));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4407 */         return position;case BYTES: position = ArrayDecoders.decodeBytes(data, position, registers); return position;case DOUBLE: registers.object1 = Double.valueOf(ArrayDecoders.decodeDouble(data, position)); position += 8; return position;case FIXED32: case SFIXED32: registers.object1 = Integer.valueOf(ArrayDecoders.decodeFixed32(data, position)); position += 4; return position;case FIXED64: case SFIXED64: registers.object1 = Long.valueOf(ArrayDecoders.decodeFixed64(data, position)); position += 8; return position;case FLOAT: registers.object1 = Float.valueOf(ArrayDecoders.decodeFloat(data, position)); position += 4; return position;case ENUM: case INT32: case UINT32: position = ArrayDecoders.decodeVarint32(data, position, registers); registers.object1 = Integer.valueOf(registers.int1); return position;case INT64: case UINT64: position = ArrayDecoders.decodeVarint64(data, position, registers); registers.object1 = Long.valueOf(registers.long1); return position;case MESSAGE: position = ArrayDecoders.decodeMessageField(Protobuf.getInstance().schemaFor(messageType), data, position, limit, registers); return position;case SINT32: position = ArrayDecoders.decodeVarint32(data, position, registers); registers.object1 = Integer.valueOf(CodedInputStream.decodeZigZag32(registers.int1)); return position;case SINT64: position = ArrayDecoders.decodeVarint64(data, position, registers); registers.object1 = Long.valueOf(CodedInputStream.decodeZigZag64(registers.long1)); return position;case STRING: position = ArrayDecoders.decodeStringRequireUtf8(data, position, registers); return position;
/*      */     } 
/*      */     throw new RuntimeException("unsupported field type.");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private <K, V> int decodeMapEntry(byte[] data, int position, int limit, MapEntryLite.Metadata<K, V> metadata, Map<K, V> target, ArrayDecoders.Registers registers) throws IOException {
/* 4419 */     position = ArrayDecoders.decodeVarint32(data, position, registers);
/* 4420 */     int length = registers.int1;
/* 4421 */     if (length < 0 || length > limit - position) {
/* 4422 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/* 4424 */     int end = position + length;
/* 4425 */     K key = metadata.defaultKey;
/* 4426 */     V value = metadata.defaultValue;
/* 4427 */     while (position < end) {
/* 4428 */       int tag = data[position++];
/* 4429 */       if (tag < 0) {
/* 4430 */         position = ArrayDecoders.decodeVarint32(tag, data, position, registers);
/* 4431 */         tag = registers.int1;
/*      */       } 
/* 4433 */       int fieldNumber = tag >>> 3;
/* 4434 */       int wireType = tag & 0x7;
/* 4435 */       switch (fieldNumber) {
/*      */         case 1:
/* 4437 */           if (wireType == metadata.keyType.getWireType()) {
/*      */             
/* 4439 */             position = decodeMapEntryValue(data, position, limit, metadata.keyType, null, registers);
/* 4440 */             key = (K)registers.object1;
/*      */             continue;
/*      */           } 
/*      */           break;
/*      */         case 2:
/* 4445 */           if (wireType == metadata.valueType.getWireType()) {
/*      */             
/* 4447 */             position = decodeMapEntryValue(data, position, limit, metadata.valueType, metadata.defaultValue
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 4452 */                 .getClass(), registers);
/*      */             
/* 4454 */             value = (V)registers.object1;
/*      */             continue;
/*      */           } 
/*      */           break;
/*      */       } 
/*      */ 
/*      */       
/* 4461 */       position = ArrayDecoders.skipField(tag, data, position, limit, registers);
/*      */     } 
/* 4463 */     if (position != end) {
/* 4464 */       throw InvalidProtocolBufferException.parseFailure();
/*      */     }
/* 4466 */     target.put(key, value);
/* 4467 */     return end;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int parseRepeatedField(T message, byte[] data, int position, int limit, int tag, int number, int wireType, int bufferPosition, long typeAndOffset, int fieldType, long fieldOffset, ArrayDecoders.Registers registers) throws IOException {
/*      */     UnknownFieldSetLite unknownFields;
/* 4485 */     Internal.ProtobufList<?> list = (Internal.ProtobufList)UNSAFE.getObject(message, fieldOffset);
/* 4486 */     if (!list.isModifiable()) {
/* 4487 */       int size = list.size();
/*      */       
/* 4489 */       list = list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*      */       
/* 4491 */       UNSAFE.putObject(message, fieldOffset, list);
/*      */     } 
/* 4493 */     switch (fieldType) {
/*      */       case 18:
/*      */       case 35:
/* 4496 */         if (wireType == 2) {
/* 4497 */           position = ArrayDecoders.decodePackedDoubleList(data, position, list, registers); break;
/* 4498 */         }  if (wireType == 1) {
/* 4499 */           position = ArrayDecoders.decodeDoubleList(tag, data, position, limit, list, registers);
/*      */         }
/*      */         break;
/*      */       case 19:
/*      */       case 36:
/* 4504 */         if (wireType == 2) {
/* 4505 */           position = ArrayDecoders.decodePackedFloatList(data, position, list, registers); break;
/* 4506 */         }  if (wireType == 5) {
/* 4507 */           position = ArrayDecoders.decodeFloatList(tag, data, position, limit, list, registers);
/*      */         }
/*      */         break;
/*      */       case 20:
/*      */       case 21:
/*      */       case 37:
/*      */       case 38:
/* 4514 */         if (wireType == 2) {
/* 4515 */           position = ArrayDecoders.decodePackedVarint64List(data, position, list, registers); break;
/* 4516 */         }  if (wireType == 0) {
/* 4517 */           position = ArrayDecoders.decodeVarint64List(tag, data, position, limit, list, registers);
/*      */         }
/*      */         break;
/*      */       case 22:
/*      */       case 29:
/*      */       case 39:
/*      */       case 43:
/* 4524 */         if (wireType == 2) {
/* 4525 */           position = ArrayDecoders.decodePackedVarint32List(data, position, list, registers); break;
/* 4526 */         }  if (wireType == 0) {
/* 4527 */           position = ArrayDecoders.decodeVarint32List(tag, data, position, limit, list, registers);
/*      */         }
/*      */         break;
/*      */       case 23:
/*      */       case 32:
/*      */       case 40:
/*      */       case 46:
/* 4534 */         if (wireType == 2) {
/* 4535 */           position = ArrayDecoders.decodePackedFixed64List(data, position, list, registers); break;
/* 4536 */         }  if (wireType == 1) {
/* 4537 */           position = ArrayDecoders.decodeFixed64List(tag, data, position, limit, list, registers);
/*      */         }
/*      */         break;
/*      */       case 24:
/*      */       case 31:
/*      */       case 41:
/*      */       case 45:
/* 4544 */         if (wireType == 2) {
/* 4545 */           position = ArrayDecoders.decodePackedFixed32List(data, position, list, registers); break;
/* 4546 */         }  if (wireType == 5) {
/* 4547 */           position = ArrayDecoders.decodeFixed32List(tag, data, position, limit, list, registers);
/*      */         }
/*      */         break;
/*      */       case 25:
/*      */       case 42:
/* 4552 */         if (wireType == 2) {
/* 4553 */           position = ArrayDecoders.decodePackedBoolList(data, position, list, registers); break;
/* 4554 */         }  if (wireType == 0) {
/* 4555 */           position = ArrayDecoders.decodeBoolList(tag, data, position, limit, list, registers);
/*      */         }
/*      */         break;
/*      */       case 26:
/* 4559 */         if (wireType == 2) {
/* 4560 */           if ((typeAndOffset & 0x20000000L) == 0L) {
/* 4561 */             position = ArrayDecoders.decodeStringList(tag, data, position, limit, list, registers); break;
/*      */           } 
/* 4563 */           position = ArrayDecoders.decodeStringListRequireUtf8(tag, data, position, limit, list, registers);
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 27:
/* 4568 */         if (wireType == 2)
/*      */         {
/* 4570 */           position = ArrayDecoders.decodeMessageList(
/* 4571 */               getMessageFieldSchema(bufferPosition), tag, data, position, limit, list, registers);
/*      */         }
/*      */         break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 28:
/* 4581 */         if (wireType == 2) {
/* 4582 */           position = ArrayDecoders.decodeBytesList(tag, data, position, limit, list, registers);
/*      */         }
/*      */         break;
/*      */       case 30:
/*      */       case 44:
/* 4587 */         if (wireType == 2) {
/* 4588 */           position = ArrayDecoders.decodePackedVarint32List(data, position, list, registers);
/* 4589 */         } else if (wireType == 0) {
/* 4590 */           position = ArrayDecoders.decodeVarint32List(tag, data, position, limit, list, registers);
/*      */         } else {
/*      */           break;
/*      */         } 
/* 4594 */         unknownFields = ((GeneratedMessageLite)message).unknownFields;
/* 4595 */         if (unknownFields == UnknownFieldSetLite.getDefaultInstance())
/*      */         {
/*      */ 
/*      */ 
/*      */           
/* 4600 */           unknownFields = null;
/*      */         }
/*      */         
/* 4603 */         unknownFields = (UnknownFieldSetLite)SchemaUtil.filterUnknownEnumList(number, (List)list, 
/*      */ 
/*      */             
/* 4606 */             getEnumFieldVerifier(bufferPosition), unknownFields, this.unknownFieldSchema);
/*      */ 
/*      */         
/* 4609 */         if (unknownFields != null) {
/* 4610 */           ((GeneratedMessageLite)message).unknownFields = unknownFields;
/*      */         }
/*      */         break;
/*      */       case 33:
/*      */       case 47:
/* 4615 */         if (wireType == 2) {
/* 4616 */           position = ArrayDecoders.decodePackedSInt32List(data, position, list, registers); break;
/* 4617 */         }  if (wireType == 0) {
/* 4618 */           position = ArrayDecoders.decodeSInt32List(tag, data, position, limit, list, registers);
/*      */         }
/*      */         break;
/*      */       case 34:
/*      */       case 48:
/* 4623 */         if (wireType == 2) {
/* 4624 */           position = ArrayDecoders.decodePackedSInt64List(data, position, list, registers); break;
/* 4625 */         }  if (wireType == 0) {
/* 4626 */           position = ArrayDecoders.decodeSInt64List(tag, data, position, limit, list, registers);
/*      */         }
/*      */         break;
/*      */       case 49:
/* 4630 */         if (wireType == 3)
/*      */         {
/* 4632 */           position = ArrayDecoders.decodeGroupList(
/* 4633 */               getMessageFieldSchema(bufferPosition), tag, data, position, limit, list, registers);
/*      */         }
/*      */         break;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4645 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private <K, V> int parseMapField(T message, byte[] data, int position, int limit, int bufferPosition, long fieldOffset, ArrayDecoders.Registers registers) throws IOException {
/* 4657 */     Unsafe unsafe = UNSAFE;
/* 4658 */     Object mapDefaultEntry = getMapFieldDefaultEntry(bufferPosition);
/* 4659 */     Object mapField = unsafe.getObject(message, fieldOffset);
/* 4660 */     if (this.mapFieldSchema.isImmutable(mapField)) {
/* 4661 */       Object oldMapField = mapField;
/* 4662 */       mapField = this.mapFieldSchema.newMapField(mapDefaultEntry);
/* 4663 */       this.mapFieldSchema.mergeFrom(mapField, oldMapField);
/* 4664 */       unsafe.putObject(message, fieldOffset, mapField);
/*      */     } 
/* 4666 */     return decodeMapEntry(data, position, limit, this.mapFieldSchema
/*      */ 
/*      */ 
/*      */         
/* 4670 */         .forMapMetadata(mapDefaultEntry), this.mapFieldSchema
/* 4671 */         .forMutableMapData(mapField), registers);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int parseOneofField(T message, byte[] data, int position, int limit, int tag, int number, int wireType, int typeAndOffset, int fieldType, long fieldOffset, int bufferPosition, ArrayDecoders.Registers registers) throws IOException {
/* 4689 */     Unsafe unsafe = UNSAFE;
/* 4690 */     long oneofCaseOffset = (this.buffer[bufferPosition + 2] & 0xFFFFF);
/* 4691 */     switch (fieldType) {
/*      */       case 51:
/* 4693 */         if (wireType == 1) {
/* 4694 */           unsafe.putObject(message, fieldOffset, Double.valueOf(ArrayDecoders.decodeDouble(data, position)));
/* 4695 */           position += 8;
/* 4696 */           unsafe.putInt(message, oneofCaseOffset, number);
/*      */         } 
/*      */         break;
/*      */       case 52:
/* 4700 */         if (wireType == 5) {
/* 4701 */           unsafe.putObject(message, fieldOffset, Float.valueOf(ArrayDecoders.decodeFloat(data, position)));
/* 4702 */           position += 4;
/* 4703 */           unsafe.putInt(message, oneofCaseOffset, number);
/*      */         } 
/*      */         break;
/*      */       case 53:
/*      */       case 54:
/* 4708 */         if (wireType == 0) {
/* 4709 */           position = ArrayDecoders.decodeVarint64(data, position, registers);
/* 4710 */           unsafe.putObject(message, fieldOffset, Long.valueOf(registers.long1));
/* 4711 */           unsafe.putInt(message, oneofCaseOffset, number);
/*      */         } 
/*      */         break;
/*      */       case 55:
/*      */       case 62:
/* 4716 */         if (wireType == 0) {
/* 4717 */           position = ArrayDecoders.decodeVarint32(data, position, registers);
/* 4718 */           unsafe.putObject(message, fieldOffset, Integer.valueOf(registers.int1));
/* 4719 */           unsafe.putInt(message, oneofCaseOffset, number);
/*      */         } 
/*      */         break;
/*      */       case 56:
/*      */       case 65:
/* 4724 */         if (wireType == 1) {
/* 4725 */           unsafe.putObject(message, fieldOffset, Long.valueOf(ArrayDecoders.decodeFixed64(data, position)));
/* 4726 */           position += 8;
/* 4727 */           unsafe.putInt(message, oneofCaseOffset, number);
/*      */         } 
/*      */         break;
/*      */       case 57:
/*      */       case 64:
/* 4732 */         if (wireType == 5) {
/* 4733 */           unsafe.putObject(message, fieldOffset, Integer.valueOf(ArrayDecoders.decodeFixed32(data, position)));
/* 4734 */           position += 4;
/* 4735 */           unsafe.putInt(message, oneofCaseOffset, number);
/*      */         } 
/*      */         break;
/*      */       case 58:
/* 4739 */         if (wireType == 0) {
/* 4740 */           position = ArrayDecoders.decodeVarint64(data, position, registers);
/* 4741 */           unsafe.putObject(message, fieldOffset, Boolean.valueOf((registers.long1 != 0L)));
/* 4742 */           unsafe.putInt(message, oneofCaseOffset, number);
/*      */         } 
/*      */         break;
/*      */       case 59:
/* 4746 */         if (wireType == 2) {
/* 4747 */           position = ArrayDecoders.decodeVarint32(data, position, registers);
/* 4748 */           int length = registers.int1;
/* 4749 */           if (length == 0) {
/* 4750 */             unsafe.putObject(message, fieldOffset, "");
/*      */           } else {
/* 4752 */             if ((typeAndOffset & 0x20000000) != 0 && 
/* 4753 */               !Utf8.isValidUtf8(data, position, position + length)) {
/* 4754 */               throw InvalidProtocolBufferException.invalidUtf8();
/*      */             }
/* 4756 */             String value = new String(data, position, length, Internal.UTF_8);
/* 4757 */             unsafe.putObject(message, fieldOffset, value);
/* 4758 */             position += length;
/*      */           } 
/* 4760 */           unsafe.putInt(message, oneofCaseOffset, number);
/*      */         } 
/*      */         break;
/*      */       case 60:
/* 4764 */         if (wireType == 2) {
/*      */           
/* 4766 */           position = ArrayDecoders.decodeMessageField(
/* 4767 */               getMessageFieldSchema(bufferPosition), data, position, limit, registers);
/*      */ 
/*      */           
/* 4770 */           Object oldValue = (unsafe.getInt(message, oneofCaseOffset) == number) ? unsafe.getObject(message, fieldOffset) : null;
/*      */           
/* 4772 */           if (oldValue == null) {
/* 4773 */             unsafe.putObject(message, fieldOffset, registers.object1);
/*      */           } else {
/* 4775 */             unsafe.putObject(message, fieldOffset, 
/* 4776 */                 Internal.mergeMessage(oldValue, registers.object1));
/*      */           } 
/* 4778 */           unsafe.putInt(message, oneofCaseOffset, number);
/*      */         } 
/*      */         break;
/*      */       case 61:
/* 4782 */         if (wireType == 2) {
/* 4783 */           position = ArrayDecoders.decodeBytes(data, position, registers);
/* 4784 */           unsafe.putObject(message, fieldOffset, registers.object1);
/* 4785 */           unsafe.putInt(message, oneofCaseOffset, number);
/*      */         } 
/*      */         break;
/*      */       case 63:
/* 4789 */         if (wireType == 0) {
/* 4790 */           position = ArrayDecoders.decodeVarint32(data, position, registers);
/* 4791 */           int enumValue = registers.int1;
/* 4792 */           Internal.EnumVerifier enumVerifier = getEnumFieldVerifier(bufferPosition);
/* 4793 */           if (enumVerifier == null || enumVerifier.isInRange(enumValue)) {
/* 4794 */             unsafe.putObject(message, fieldOffset, Integer.valueOf(enumValue));
/* 4795 */             unsafe.putInt(message, oneofCaseOffset, number);
/*      */             break;
/*      */           } 
/* 4798 */           getMutableUnknownFields(message).storeField(tag, Long.valueOf(enumValue));
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 66:
/* 4803 */         if (wireType == 0) {
/* 4804 */           position = ArrayDecoders.decodeVarint32(data, position, registers);
/* 4805 */           unsafe.putObject(message, fieldOffset, Integer.valueOf(CodedInputStream.decodeZigZag32(registers.int1)));
/* 4806 */           unsafe.putInt(message, oneofCaseOffset, number);
/*      */         } 
/*      */         break;
/*      */       case 67:
/* 4810 */         if (wireType == 0) {
/* 4811 */           position = ArrayDecoders.decodeVarint64(data, position, registers);
/* 4812 */           unsafe.putObject(message, fieldOffset, Long.valueOf(CodedInputStream.decodeZigZag64(registers.long1)));
/* 4813 */           unsafe.putInt(message, oneofCaseOffset, number);
/*      */         } 
/*      */         break;
/*      */       case 68:
/* 4817 */         if (wireType == 3) {
/* 4818 */           int endTag = tag & 0xFFFFFFF8 | 0x4;
/*      */           
/* 4820 */           position = ArrayDecoders.decodeGroupField(
/* 4821 */               getMessageFieldSchema(bufferPosition), data, position, limit, endTag, registers);
/*      */ 
/*      */           
/* 4824 */           Object oldValue = (unsafe.getInt(message, oneofCaseOffset) == number) ? unsafe.getObject(message, fieldOffset) : null;
/*      */           
/* 4826 */           if (oldValue == null) {
/* 4827 */             unsafe.putObject(message, fieldOffset, registers.object1);
/*      */           } else {
/* 4829 */             unsafe.putObject(message, fieldOffset, 
/* 4830 */                 Internal.mergeMessage(oldValue, registers.object1));
/*      */           } 
/* 4832 */           unsafe.putInt(message, oneofCaseOffset, number);
/*      */         } 
/*      */         break;
/*      */     } 
/*      */ 
/*      */     
/* 4838 */     return position;
/*      */   }
/*      */   
/*      */   private Schema getMessageFieldSchema(int pos) {
/* 4842 */     int index = pos / 3 * 2;
/* 4843 */     Schema<?> schema = (Schema)this.objects[index];
/* 4844 */     if (schema != null) {
/* 4845 */       return schema;
/*      */     }
/* 4847 */     schema = Protobuf.getInstance().schemaFor((Class)this.objects[index + 1]);
/* 4848 */     this.objects[index] = schema;
/* 4849 */     return schema;
/*      */   }
/*      */   
/*      */   private Object getMapFieldDefaultEntry(int pos) {
/* 4853 */     return this.objects[pos / 3 * 2];
/*      */   }
/*      */   
/*      */   private Internal.EnumVerifier getEnumFieldVerifier(int pos) {
/* 4857 */     return (Internal.EnumVerifier)this.objects[pos / 3 * 2 + 1];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int parseProto2Message(T message, byte[] data, int position, int limit, int endGroup, ArrayDecoders.Registers registers) throws IOException {
/* 4869 */     Unsafe unsafe = UNSAFE;
/* 4870 */     int currentPresenceFieldOffset = -1;
/* 4871 */     int currentPresenceField = 0;
/* 4872 */     int tag = 0;
/* 4873 */     int oldNumber = -1;
/* 4874 */     int pos = 0;
/* 4875 */     while (position < limit) {
/* 4876 */       tag = data[position++];
/* 4877 */       if (tag < 0) {
/* 4878 */         position = ArrayDecoders.decodeVarint32(tag, data, position, registers);
/* 4879 */         tag = registers.int1;
/*      */       } 
/* 4881 */       int number = tag >>> 3;
/* 4882 */       int wireType = tag & 0x7;
/* 4883 */       if (number > oldNumber) {
/* 4884 */         pos = positionForFieldNumber(number, pos / 3);
/*      */       } else {
/* 4886 */         pos = positionForFieldNumber(number);
/*      */       } 
/* 4888 */       oldNumber = number;
/* 4889 */       if (pos == -1) {
/*      */         
/* 4891 */         pos = 0;
/*      */       } else {
/* 4893 */         int typeAndOffset = this.buffer[pos + 1];
/* 4894 */         int fieldType = type(typeAndOffset);
/* 4895 */         long fieldOffset = offset(typeAndOffset);
/* 4896 */         if (fieldType <= 17) {
/*      */           
/* 4898 */           int presenceMaskAndOffset = this.buffer[pos + 2];
/* 4899 */           int presenceMask = 1 << presenceMaskAndOffset >>> 20;
/* 4900 */           int presenceFieldOffset = presenceMaskAndOffset & 0xFFFFF;
/*      */ 
/*      */           
/* 4903 */           if (presenceFieldOffset != currentPresenceFieldOffset) {
/* 4904 */             if (currentPresenceFieldOffset != -1) {
/* 4905 */               unsafe.putInt(message, currentPresenceFieldOffset, currentPresenceField);
/*      */             }
/* 4907 */             currentPresenceFieldOffset = presenceFieldOffset;
/* 4908 */             currentPresenceField = unsafe.getInt(message, presenceFieldOffset);
/*      */           } 
/* 4910 */           switch (fieldType) {
/*      */             case 0:
/* 4912 */               if (wireType == 1) {
/* 4913 */                 UnsafeUtil.putDouble(message, fieldOffset, ArrayDecoders.decodeDouble(data, position));
/* 4914 */                 position += 8;
/* 4915 */                 currentPresenceField |= presenceMask;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 1:
/* 4920 */               if (wireType == 5) {
/* 4921 */                 UnsafeUtil.putFloat(message, fieldOffset, ArrayDecoders.decodeFloat(data, position));
/* 4922 */                 position += 4;
/* 4923 */                 currentPresenceField |= presenceMask;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 2:
/*      */             case 3:
/* 4929 */               if (wireType == 0) {
/* 4930 */                 position = ArrayDecoders.decodeVarint64(data, position, registers);
/* 4931 */                 unsafe.putLong(message, fieldOffset, registers.long1);
/* 4932 */                 currentPresenceField |= presenceMask;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 4:
/*      */             case 11:
/* 4938 */               if (wireType == 0) {
/* 4939 */                 position = ArrayDecoders.decodeVarint32(data, position, registers);
/* 4940 */                 unsafe.putInt(message, fieldOffset, registers.int1);
/* 4941 */                 currentPresenceField |= presenceMask;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 5:
/*      */             case 14:
/* 4947 */               if (wireType == 1) {
/* 4948 */                 unsafe.putLong(message, fieldOffset, ArrayDecoders.decodeFixed64(data, position));
/* 4949 */                 position += 8;
/* 4950 */                 currentPresenceField |= presenceMask;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 6:
/*      */             case 13:
/* 4956 */               if (wireType == 5) {
/* 4957 */                 unsafe.putInt(message, fieldOffset, ArrayDecoders.decodeFixed32(data, position));
/* 4958 */                 position += 4;
/* 4959 */                 currentPresenceField |= presenceMask;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 7:
/* 4964 */               if (wireType == 0) {
/* 4965 */                 position = ArrayDecoders.decodeVarint64(data, position, registers);
/* 4966 */                 UnsafeUtil.putBoolean(message, fieldOffset, (registers.long1 != 0L));
/* 4967 */                 currentPresenceField |= presenceMask;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 8:
/* 4972 */               if (wireType == 2) {
/* 4973 */                 if ((typeAndOffset & 0x20000000) == 0) {
/* 4974 */                   position = ArrayDecoders.decodeString(data, position, registers);
/*      */                 } else {
/* 4976 */                   position = ArrayDecoders.decodeStringRequireUtf8(data, position, registers);
/*      */                 } 
/* 4978 */                 unsafe.putObject(message, fieldOffset, registers.object1);
/* 4979 */                 currentPresenceField |= presenceMask;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 9:
/* 4984 */               if (wireType == 2) {
/*      */                 
/* 4986 */                 position = ArrayDecoders.decodeMessageField(
/* 4987 */                     getMessageFieldSchema(pos), data, position, limit, registers);
/* 4988 */                 if ((currentPresenceField & presenceMask) == 0) {
/* 4989 */                   unsafe.putObject(message, fieldOffset, registers.object1);
/*      */                 } else {
/* 4991 */                   unsafe.putObject(message, fieldOffset, 
/*      */ 
/*      */                       
/* 4994 */                       Internal.mergeMessage(unsafe
/* 4995 */                         .getObject(message, fieldOffset), registers.object1));
/*      */                 } 
/* 4997 */                 currentPresenceField |= presenceMask;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 10:
/* 5002 */               if (wireType == 2) {
/* 5003 */                 position = ArrayDecoders.decodeBytes(data, position, registers);
/* 5004 */                 unsafe.putObject(message, fieldOffset, registers.object1);
/* 5005 */                 currentPresenceField |= presenceMask;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 12:
/* 5010 */               if (wireType == 0) {
/* 5011 */                 position = ArrayDecoders.decodeVarint32(data, position, registers);
/* 5012 */                 int enumValue = registers.int1;
/* 5013 */                 Internal.EnumVerifier enumVerifier = getEnumFieldVerifier(pos);
/* 5014 */                 if (enumVerifier == null || enumVerifier.isInRange(enumValue)) {
/* 5015 */                   unsafe.putInt(message, fieldOffset, enumValue);
/* 5016 */                   currentPresenceField |= presenceMask;
/*      */                   continue;
/*      */                 } 
/* 5019 */                 getMutableUnknownFields(message).storeField(tag, Long.valueOf(enumValue));
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             
/*      */             case 15:
/* 5025 */               if (wireType == 0) {
/* 5026 */                 position = ArrayDecoders.decodeVarint32(data, position, registers);
/* 5027 */                 unsafe.putInt(message, fieldOffset, 
/* 5028 */                     CodedInputStream.decodeZigZag32(registers.int1));
/* 5029 */                 currentPresenceField |= presenceMask;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 16:
/* 5034 */               if (wireType == 0) {
/* 5035 */                 position = ArrayDecoders.decodeVarint64(data, position, registers);
/* 5036 */                 unsafe.putLong(message, fieldOffset, 
/* 5037 */                     CodedInputStream.decodeZigZag64(registers.long1));
/*      */                 
/* 5039 */                 currentPresenceField |= presenceMask;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 17:
/* 5044 */               if (wireType == 3) {
/* 5045 */                 int endTag = number << 3 | 0x4;
/*      */                 
/* 5047 */                 position = ArrayDecoders.decodeGroupField(
/* 5048 */                     getMessageFieldSchema(pos), data, position, limit, endTag, registers);
/* 5049 */                 if ((currentPresenceField & presenceMask) == 0) {
/* 5050 */                   unsafe.putObject(message, fieldOffset, registers.object1);
/*      */                 } else {
/* 5052 */                   unsafe.putObject(message, fieldOffset, 
/*      */ 
/*      */                       
/* 5055 */                       Internal.mergeMessage(unsafe
/* 5056 */                         .getObject(message, fieldOffset), registers.object1));
/*      */                 } 
/*      */                 
/* 5059 */                 currentPresenceField |= presenceMask;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */           } 
/*      */ 
/*      */         
/* 5066 */         } else if (fieldType == 27) {
/*      */           
/* 5068 */           if (wireType == 2) {
/* 5069 */             Internal.ProtobufList<?> list = (Internal.ProtobufList)unsafe.getObject(message, fieldOffset);
/* 5070 */             if (!list.isModifiable()) {
/* 5071 */               int size = list.size();
/*      */               
/* 5073 */               list = list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*      */               
/* 5075 */               unsafe.putObject(message, fieldOffset, list);
/*      */             } 
/*      */             
/* 5078 */             position = ArrayDecoders.decodeMessageList(
/* 5079 */                 getMessageFieldSchema(pos), tag, data, position, limit, list, registers);
/*      */             continue;
/*      */           } 
/* 5082 */         } else if (fieldType <= 49) {
/*      */           
/* 5084 */           int oldPosition = position;
/*      */           
/* 5086 */           position = parseRepeatedField(message, data, position, limit, tag, number, wireType, pos, typeAndOffset, fieldType, fieldOffset, registers);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 5099 */           if (position != oldPosition) {
/*      */             continue;
/*      */           }
/* 5102 */         } else if (fieldType == 50) {
/* 5103 */           if (wireType == 2) {
/* 5104 */             int oldPosition = position;
/* 5105 */             position = parseMapField(message, data, position, limit, pos, fieldOffset, registers);
/* 5106 */             if (position != oldPosition) {
/*      */               continue;
/*      */             }
/*      */           } 
/*      */         } else {
/* 5111 */           int oldPosition = position;
/*      */           
/* 5113 */           position = parseOneofField(message, data, position, limit, tag, number, wireType, typeAndOffset, fieldType, fieldOffset, pos, registers);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 5126 */           if (position != oldPosition) {
/*      */             continue;
/*      */           }
/*      */         } 
/*      */       } 
/* 5131 */       if (tag == endGroup && endGroup != 0) {
/*      */         break;
/*      */       }
/*      */       
/* 5135 */       if (this.hasExtensions && registers.extensionRegistry != 
/* 5136 */         ExtensionRegistryLite.getEmptyRegistry()) {
/* 5137 */         position = ArrayDecoders.decodeExtensionOrUnknownField(tag, data, position, limit, message, this.defaultInstance, (UnknownFieldSchema)this.unknownFieldSchema, registers);
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 5142 */       position = ArrayDecoders.decodeUnknownField(tag, data, position, limit, 
/* 5143 */           getMutableUnknownFields(message), registers);
/*      */     } 
/*      */     
/* 5146 */     if (currentPresenceFieldOffset != -1) {
/* 5147 */       unsafe.putInt(message, currentPresenceFieldOffset, currentPresenceField);
/*      */     }
/* 5149 */     UnknownFieldSetLite unknownFields = null;
/* 5150 */     for (int i = this.checkInitializedCount; i < this.repeatedFieldOffsetStart; i++)
/*      */     {
/* 5152 */       unknownFields = (UnknownFieldSetLite)filterMapUnknownEnumValues(message, this.intArray[i], unknownFields, this.unknownFieldSchema);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5158 */     if (unknownFields != null) {
/* 5159 */       this.unknownFieldSchema
/* 5160 */         .setBuilderToMessage(message, unknownFields);
/*      */     }
/* 5162 */     if (endGroup == 0) {
/* 5163 */       if (position != limit) {
/* 5164 */         throw InvalidProtocolBufferException.parseFailure();
/*      */       }
/*      */     }
/* 5167 */     else if (position > limit || tag != endGroup) {
/* 5168 */       throw InvalidProtocolBufferException.parseFailure();
/*      */     } 
/*      */     
/* 5171 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private int parseProto3Message(T message, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException {
/* 5177 */     Unsafe unsafe = UNSAFE;
/* 5178 */     int tag = 0;
/* 5179 */     int oldNumber = -1;
/* 5180 */     int pos = 0;
/* 5181 */     while (position < limit) {
/* 5182 */       tag = data[position++];
/* 5183 */       if (tag < 0) {
/* 5184 */         position = ArrayDecoders.decodeVarint32(tag, data, position, registers);
/* 5185 */         tag = registers.int1;
/*      */       } 
/* 5187 */       int number = tag >>> 3;
/* 5188 */       int wireType = tag & 0x7;
/* 5189 */       if (number > oldNumber) {
/* 5190 */         pos = positionForFieldNumber(number, pos / 3);
/*      */       } else {
/* 5192 */         pos = positionForFieldNumber(number);
/*      */       } 
/* 5194 */       oldNumber = number;
/* 5195 */       if (pos == -1) {
/*      */         
/* 5197 */         pos = 0;
/*      */       } else {
/* 5199 */         int typeAndOffset = this.buffer[pos + 1];
/* 5200 */         int fieldType = type(typeAndOffset);
/* 5201 */         long fieldOffset = offset(typeAndOffset);
/* 5202 */         if (fieldType <= 17) {
/* 5203 */           switch (fieldType) {
/*      */             case 0:
/* 5205 */               if (wireType == 1) {
/* 5206 */                 UnsafeUtil.putDouble(message, fieldOffset, ArrayDecoders.decodeDouble(data, position));
/* 5207 */                 position += 8;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 1:
/* 5212 */               if (wireType == 5) {
/* 5213 */                 UnsafeUtil.putFloat(message, fieldOffset, ArrayDecoders.decodeFloat(data, position));
/* 5214 */                 position += 4;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 2:
/*      */             case 3:
/* 5220 */               if (wireType == 0) {
/* 5221 */                 position = ArrayDecoders.decodeVarint64(data, position, registers);
/* 5222 */                 unsafe.putLong(message, fieldOffset, registers.long1);
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 4:
/*      */             case 11:
/* 5228 */               if (wireType == 0) {
/* 5229 */                 position = ArrayDecoders.decodeVarint32(data, position, registers);
/* 5230 */                 unsafe.putInt(message, fieldOffset, registers.int1);
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 5:
/*      */             case 14:
/* 5236 */               if (wireType == 1) {
/* 5237 */                 unsafe.putLong(message, fieldOffset, ArrayDecoders.decodeFixed64(data, position));
/* 5238 */                 position += 8;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 6:
/*      */             case 13:
/* 5244 */               if (wireType == 5) {
/* 5245 */                 unsafe.putInt(message, fieldOffset, ArrayDecoders.decodeFixed32(data, position));
/* 5246 */                 position += 4;
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 7:
/* 5251 */               if (wireType == 0) {
/* 5252 */                 position = ArrayDecoders.decodeVarint64(data, position, registers);
/* 5253 */                 UnsafeUtil.putBoolean(message, fieldOffset, (registers.long1 != 0L));
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 8:
/* 5258 */               if (wireType == 2) {
/* 5259 */                 if ((typeAndOffset & 0x20000000) == 0) {
/* 5260 */                   position = ArrayDecoders.decodeString(data, position, registers);
/*      */                 } else {
/* 5262 */                   position = ArrayDecoders.decodeStringRequireUtf8(data, position, registers);
/*      */                 } 
/* 5264 */                 unsafe.putObject(message, fieldOffset, registers.object1);
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 9:
/* 5269 */               if (wireType == 2) {
/*      */                 
/* 5271 */                 position = ArrayDecoders.decodeMessageField(
/* 5272 */                     getMessageFieldSchema(pos), data, position, limit, registers);
/* 5273 */                 Object oldValue = unsafe.getObject(message, fieldOffset);
/* 5274 */                 if (oldValue == null) {
/* 5275 */                   unsafe.putObject(message, fieldOffset, registers.object1); continue;
/*      */                 } 
/* 5277 */                 unsafe.putObject(message, fieldOffset, 
/* 5278 */                     Internal.mergeMessage(oldValue, registers.object1));
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             
/*      */             case 10:
/* 5284 */               if (wireType == 2) {
/* 5285 */                 position = ArrayDecoders.decodeBytes(data, position, registers);
/* 5286 */                 unsafe.putObject(message, fieldOffset, registers.object1);
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 12:
/* 5291 */               if (wireType == 0) {
/* 5292 */                 position = ArrayDecoders.decodeVarint32(data, position, registers);
/* 5293 */                 unsafe.putInt(message, fieldOffset, registers.int1);
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 15:
/* 5298 */               if (wireType == 0) {
/* 5299 */                 position = ArrayDecoders.decodeVarint32(data, position, registers);
/* 5300 */                 unsafe.putInt(message, fieldOffset, 
/* 5301 */                     CodedInputStream.decodeZigZag32(registers.int1));
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */             case 16:
/* 5306 */               if (wireType == 0) {
/* 5307 */                 position = ArrayDecoders.decodeVarint64(data, position, registers);
/* 5308 */                 unsafe.putLong(message, fieldOffset, 
/* 5309 */                     CodedInputStream.decodeZigZag64(registers.long1));
/*      */                 continue;
/*      */               } 
/*      */               break;
/*      */           } 
/*      */ 
/*      */         
/* 5316 */         } else if (fieldType == 27) {
/*      */           
/* 5318 */           if (wireType == 2) {
/* 5319 */             Internal.ProtobufList<?> list = (Internal.ProtobufList)unsafe.getObject(message, fieldOffset);
/* 5320 */             if (!list.isModifiable()) {
/* 5321 */               int size = list.size();
/*      */               
/* 5323 */               list = list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*      */               
/* 5325 */               unsafe.putObject(message, fieldOffset, list);
/*      */             } 
/*      */             
/* 5328 */             position = ArrayDecoders.decodeMessageList(
/* 5329 */                 getMessageFieldSchema(pos), tag, data, position, limit, list, registers);
/*      */             continue;
/*      */           } 
/* 5332 */         } else if (fieldType <= 49) {
/*      */           
/* 5334 */           int oldPosition = position;
/*      */           
/* 5336 */           position = parseRepeatedField(message, data, position, limit, tag, number, wireType, pos, typeAndOffset, fieldType, fieldOffset, registers);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 5349 */           if (position != oldPosition) {
/*      */             continue;
/*      */           }
/* 5352 */         } else if (fieldType == 50) {
/* 5353 */           if (wireType == 2) {
/* 5354 */             int oldPosition = position;
/* 5355 */             position = parseMapField(message, data, position, limit, pos, fieldOffset, registers);
/* 5356 */             if (position != oldPosition) {
/*      */               continue;
/*      */             }
/*      */           } 
/*      */         } else {
/* 5361 */           int oldPosition = position;
/*      */           
/* 5363 */           position = parseOneofField(message, data, position, limit, tag, number, wireType, typeAndOffset, fieldType, fieldOffset, pos, registers);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 5376 */           if (position != oldPosition) {
/*      */             continue;
/*      */           }
/*      */         } 
/*      */       } 
/* 5381 */       position = ArrayDecoders.decodeUnknownField(tag, data, position, limit, 
/* 5382 */           getMutableUnknownFields(message), registers);
/*      */     } 
/* 5384 */     if (position != limit) {
/* 5385 */       throw InvalidProtocolBufferException.parseFailure();
/*      */     }
/* 5387 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void mergeFrom(T message, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException {
/* 5393 */     if (this.proto3) {
/* 5394 */       parseProto3Message(message, data, position, limit, registers);
/*      */     } else {
/* 5396 */       parseProto2Message(message, data, position, limit, 0, registers);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void makeImmutable(T message) {
/* 5403 */     for (int i = this.checkInitializedCount; i < this.repeatedFieldOffsetStart; i++) {
/* 5404 */       long offset = offset(typeAndOffsetAt(this.intArray[i]));
/* 5405 */       Object mapField = UnsafeUtil.getObject(message, offset);
/* 5406 */       if (mapField != null)
/*      */       {
/*      */         
/* 5409 */         UnsafeUtil.putObject(message, offset, this.mapFieldSchema.toImmutable(mapField)); } 
/*      */     } 
/* 5411 */     int length = this.intArray.length;
/* 5412 */     for (int j = this.repeatedFieldOffsetStart; j < length; j++) {
/* 5413 */       this.listFieldSchema.makeImmutableListAt(message, this.intArray[j]);
/*      */     }
/* 5415 */     this.unknownFieldSchema.makeImmutable(message);
/* 5416 */     if (this.hasExtensions) {
/* 5417 */       this.extensionSchema.makeImmutable(message);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final <K, V> void mergeMap(Object message, int pos, Object mapDefaultEntry, ExtensionRegistryLite extensionRegistry, Reader reader) throws IOException {
/* 5429 */     long offset = offset(typeAndOffsetAt(pos));
/* 5430 */     Object mapField = UnsafeUtil.getObject(message, offset);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 5435 */     if (mapField == null) {
/* 5436 */       mapField = this.mapFieldSchema.newMapField(mapDefaultEntry);
/* 5437 */       UnsafeUtil.putObject(message, offset, mapField);
/* 5438 */     } else if (this.mapFieldSchema.isImmutable(mapField)) {
/* 5439 */       Object oldMapField = mapField;
/* 5440 */       mapField = this.mapFieldSchema.newMapField(mapDefaultEntry);
/* 5441 */       this.mapFieldSchema.mergeFrom(mapField, oldMapField);
/* 5442 */       UnsafeUtil.putObject(message, offset, mapField);
/*      */     } 
/* 5444 */     reader.readMap(this.mapFieldSchema
/* 5445 */         .forMutableMapData(mapField), this.mapFieldSchema
/* 5446 */         .forMapMetadata(mapDefaultEntry), extensionRegistry);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final <UT, UB> UB filterMapUnknownEnumValues(Object message, int pos, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema) {
/* 5452 */     int fieldNumber = numberAt(pos);
/* 5453 */     long offset = offset(typeAndOffsetAt(pos));
/* 5454 */     Object mapField = UnsafeUtil.getObject(message, offset);
/* 5455 */     if (mapField == null) {
/* 5456 */       return unknownFields;
/*      */     }
/* 5458 */     Internal.EnumVerifier enumVerifier = getEnumFieldVerifier(pos);
/* 5459 */     if (enumVerifier == null) {
/* 5460 */       return unknownFields;
/*      */     }
/* 5462 */     Map<?, ?> mapData = this.mapFieldSchema.forMutableMapData(mapField);
/*      */ 
/*      */     
/* 5465 */     unknownFields = filterUnknownEnumMap(pos, fieldNumber, mapData, enumVerifier, unknownFields, unknownFieldSchema);
/*      */     
/* 5467 */     return unknownFields;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final <K, V, UT, UB> UB filterUnknownEnumMap(int pos, int number, Map<K, V> mapData, Internal.EnumVerifier enumVerifier, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema) {
/* 5479 */     MapEntryLite.Metadata<K, V> metadata = (MapEntryLite.Metadata)this.mapFieldSchema.forMapMetadata(getMapFieldDefaultEntry(pos));
/* 5480 */     for (Iterator<Map.Entry<K, V>> it = mapData.entrySet().iterator(); it.hasNext(); ) {
/* 5481 */       Map.Entry<K, V> entry = it.next();
/* 5482 */       if (!enumVerifier.isInRange(((Integer)entry.getValue()).intValue())) {
/* 5483 */         if (unknownFields == null) {
/* 5484 */           unknownFields = unknownFieldSchema.newBuilder();
/*      */         }
/*      */         
/* 5487 */         int entrySize = MapEntryLite.computeSerializedSize(metadata, entry.getKey(), entry.getValue());
/* 5488 */         ByteString.CodedBuilder codedBuilder = ByteString.newCodedBuilder(entrySize);
/* 5489 */         CodedOutputStream codedOutput = codedBuilder.getCodedOutput();
/*      */         try {
/* 5491 */           MapEntryLite.writeTo(codedOutput, metadata, entry.getKey(), entry.getValue());
/* 5492 */         } catch (IOException e) {
/*      */           
/* 5494 */           throw new RuntimeException(e);
/*      */         } 
/* 5496 */         unknownFieldSchema.addLengthDelimited(unknownFields, number, codedBuilder.build());
/* 5497 */         it.remove();
/*      */       } 
/*      */     } 
/* 5500 */     return unknownFields;
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean isInitialized(T message) {
/* 5505 */     int currentPresenceFieldOffset = -1;
/* 5506 */     int currentPresenceField = 0;
/* 5507 */     for (int i = 0; i < this.checkInitializedCount; i++) {
/* 5508 */       int pos = this.intArray[i];
/* 5509 */       int number = numberAt(pos);
/*      */       
/* 5511 */       int typeAndOffset = typeAndOffsetAt(pos);
/*      */       
/* 5513 */       int presenceMaskAndOffset = 0;
/* 5514 */       int presenceMask = 0;
/* 5515 */       if (!this.proto3) {
/* 5516 */         presenceMaskAndOffset = this.buffer[pos + 2];
/* 5517 */         int presenceFieldOffset = presenceMaskAndOffset & 0xFFFFF;
/* 5518 */         presenceMask = 1 << presenceMaskAndOffset >>> 20;
/* 5519 */         if (presenceFieldOffset != currentPresenceFieldOffset) {
/* 5520 */           currentPresenceFieldOffset = presenceFieldOffset;
/* 5521 */           currentPresenceField = UNSAFE.getInt(message, presenceFieldOffset);
/*      */         } 
/*      */       } 
/*      */       
/* 5525 */       if (isRequired(typeAndOffset) && 
/* 5526 */         !isFieldPresent(message, pos, currentPresenceField, presenceMask)) {
/* 5527 */         return false;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 5534 */       switch (type(typeAndOffset)) {
/*      */         case 9:
/*      */         case 17:
/* 5537 */           if (isFieldPresent(message, pos, currentPresenceField, presenceMask) && 
/* 5538 */             !isInitialized(message, typeAndOffset, getMessageFieldSchema(pos))) {
/* 5539 */             return false;
/*      */           }
/*      */           break;
/*      */         case 27:
/*      */         case 49:
/* 5544 */           if (!isListInitialized(message, typeAndOffset, pos)) {
/* 5545 */             return false;
/*      */           }
/*      */           break;
/*      */         case 60:
/*      */         case 68:
/* 5550 */           if (isOneofPresent(message, number, pos) && 
/* 5551 */             !isInitialized(message, typeAndOffset, getMessageFieldSchema(pos))) {
/* 5552 */             return false;
/*      */           }
/*      */           break;
/*      */         case 50:
/* 5556 */           if (!isMapInitialized(message, typeAndOffset, pos)) {
/* 5557 */             return false;
/*      */           }
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */     
/*      */     } 
/* 5565 */     if (this.hasExtensions && 
/* 5566 */       !this.extensionSchema.getExtensions(message).isInitialized()) {
/* 5567 */       return false;
/*      */     }
/*      */ 
/*      */     
/* 5571 */     return true;
/*      */   }
/*      */   
/*      */   private static boolean isInitialized(Object message, int typeAndOffset, Schema<Object> schema) {
/* 5575 */     Object nested = UnsafeUtil.getObject(message, offset(typeAndOffset));
/* 5576 */     return schema.isInitialized(nested);
/*      */   }
/*      */ 
/*      */   
/*      */   private <N> boolean isListInitialized(Object message, int typeAndOffset, int pos) {
/* 5581 */     List<N> list = (List<N>)UnsafeUtil.getObject(message, offset(typeAndOffset));
/* 5582 */     if (list.isEmpty()) {
/* 5583 */       return true;
/*      */     }
/*      */     
/* 5586 */     Schema<N> schema = getMessageFieldSchema(pos);
/* 5587 */     for (int i = 0; i < list.size(); i++) {
/* 5588 */       N nested = list.get(i);
/* 5589 */       if (!schema.isInitialized(nested)) {
/* 5590 */         return false;
/*      */       }
/*      */     } 
/* 5593 */     return true;
/*      */   }
/*      */   
/*      */   private boolean isMapInitialized(T message, int typeAndOffset, int pos) {
/* 5597 */     Map<?, ?> map = this.mapFieldSchema.forMapData(UnsafeUtil.getObject(message, offset(typeAndOffset)));
/* 5598 */     if (map.isEmpty()) {
/* 5599 */       return true;
/*      */     }
/* 5601 */     Object mapDefaultEntry = getMapFieldDefaultEntry(pos);
/* 5602 */     MapEntryLite.Metadata<?, ?> metadata = this.mapFieldSchema.forMapMetadata(mapDefaultEntry);
/* 5603 */     if (metadata.valueType.getJavaType() != WireFormat.JavaType.MESSAGE) {
/* 5604 */       return true;
/*      */     }
/*      */     
/* 5607 */     Schema<?> schema = null;
/* 5608 */     for (Object nested : map.values()) {
/* 5609 */       if (schema == null) {
/* 5610 */         schema = Protobuf.getInstance().schemaFor(nested.getClass());
/*      */       }
/* 5612 */       if (!schema.isInitialized(nested)) {
/* 5613 */         return false;
/*      */       }
/*      */     } 
/* 5616 */     return true;
/*      */   }
/*      */   
/*      */   private void writeString(int fieldNumber, Object value, Writer writer) throws IOException {
/* 5620 */     if (value instanceof String) {
/* 5621 */       writer.writeString(fieldNumber, (String)value);
/*      */     } else {
/* 5623 */       writer.writeBytes(fieldNumber, (ByteString)value);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void readString(Object message, int typeAndOffset, Reader reader) throws IOException {
/* 5628 */     if (isEnforceUtf8(typeAndOffset)) {
/*      */       
/* 5630 */       UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readStringRequireUtf8());
/* 5631 */     } else if (this.lite) {
/*      */ 
/*      */       
/* 5634 */       UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readString());
/*      */     }
/*      */     else {
/*      */       
/* 5638 */       UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readBytes());
/*      */     } 
/*      */   }
/*      */   
/*      */   private void readStringList(Object message, int typeAndOffset, Reader reader) throws IOException {
/* 5643 */     if (isEnforceUtf8(typeAndOffset)) {
/* 5644 */       reader.readStringListRequireUtf8(this.listFieldSchema
/* 5645 */           .mutableListAt(message, offset(typeAndOffset)));
/*      */     } else {
/* 5647 */       reader.readStringList(this.listFieldSchema.mutableListAt(message, offset(typeAndOffset)));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private <E> void readMessageList(Object message, int typeAndOffset, Reader reader, Schema<E> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 5658 */     long offset = offset(typeAndOffset);
/* 5659 */     reader.readMessageList(this.listFieldSchema
/* 5660 */         .mutableListAt(message, offset), schema, extensionRegistry);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private <E> void readGroupList(Object message, long offset, Reader reader, Schema<E> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 5670 */     reader.readGroupList(this.listFieldSchema
/* 5671 */         .mutableListAt(message, offset), schema, extensionRegistry);
/*      */   }
/*      */   
/*      */   private int numberAt(int pos) {
/* 5675 */     return this.buffer[pos];
/*      */   }
/*      */   
/*      */   private int typeAndOffsetAt(int pos) {
/* 5679 */     return this.buffer[pos + 1];
/*      */   }
/*      */   
/*      */   private int presenceMaskAndOffsetAt(int pos) {
/* 5683 */     return this.buffer[pos + 2];
/*      */   }
/*      */   
/*      */   private static int type(int value) {
/* 5687 */     return (value & 0xFF00000) >>> 20;
/*      */   }
/*      */   
/*      */   private static boolean isRequired(int value) {
/* 5691 */     return ((value & 0x10000000) != 0);
/*      */   }
/*      */   
/*      */   private static boolean isEnforceUtf8(int value) {
/* 5695 */     return ((value & 0x20000000) != 0);
/*      */   }
/*      */   
/*      */   private static long offset(int value) {
/* 5699 */     return (value & 0xFFFFF);
/*      */   }
/*      */   
/*      */   private static <T> double doubleAt(T message, long offset) {
/* 5703 */     return UnsafeUtil.getDouble(message, offset);
/*      */   }
/*      */   
/*      */   private static <T> float floatAt(T message, long offset) {
/* 5707 */     return UnsafeUtil.getFloat(message, offset);
/*      */   }
/*      */   
/*      */   private static <T> int intAt(T message, long offset) {
/* 5711 */     return UnsafeUtil.getInt(message, offset);
/*      */   }
/*      */   
/*      */   private static <T> long longAt(T message, long offset) {
/* 5715 */     return UnsafeUtil.getLong(message, offset);
/*      */   }
/*      */   
/*      */   private static <T> boolean booleanAt(T message, long offset) {
/* 5719 */     return UnsafeUtil.getBoolean(message, offset);
/*      */   }
/*      */   
/*      */   private static <T> double oneofDoubleAt(T message, long offset) {
/* 5723 */     return ((Double)UnsafeUtil.getObject(message, offset)).doubleValue();
/*      */   }
/*      */   
/*      */   private static <T> float oneofFloatAt(T message, long offset) {
/* 5727 */     return ((Float)UnsafeUtil.getObject(message, offset)).floatValue();
/*      */   }
/*      */   
/*      */   private static <T> int oneofIntAt(T message, long offset) {
/* 5731 */     return ((Integer)UnsafeUtil.getObject(message, offset)).intValue();
/*      */   }
/*      */   
/*      */   private static <T> long oneofLongAt(T message, long offset) {
/* 5735 */     return ((Long)UnsafeUtil.getObject(message, offset)).longValue();
/*      */   }
/*      */   
/*      */   private static <T> boolean oneofBooleanAt(T message, long offset) {
/* 5739 */     return ((Boolean)UnsafeUtil.getObject(message, offset)).booleanValue();
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean arePresentForEquals(T message, T other, int pos) {
/* 5744 */     return (isFieldPresent(message, pos) == isFieldPresent(other, pos));
/*      */   }
/*      */   
/*      */   private boolean isFieldPresent(T message, int pos, int presenceField, int presenceMask) {
/* 5748 */     if (this.proto3) {
/* 5749 */       return isFieldPresent(message, pos);
/*      */     }
/* 5751 */     return ((presenceField & presenceMask) != 0);
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isFieldPresent(T message, int pos) {
/* 5756 */     if (this.proto3) {
/* 5757 */       Object value; int typeAndOffset = typeAndOffsetAt(pos);
/* 5758 */       long offset = offset(typeAndOffset);
/* 5759 */       switch (type(typeAndOffset)) {
/*      */         case 0:
/* 5761 */           return (UnsafeUtil.getDouble(message, offset) != 0.0D);
/*      */         case 1:
/* 5763 */           return (UnsafeUtil.getFloat(message, offset) != 0.0F);
/*      */         case 2:
/* 5765 */           return (UnsafeUtil.getLong(message, offset) != 0L);
/*      */         case 3:
/* 5767 */           return (UnsafeUtil.getLong(message, offset) != 0L);
/*      */         case 4:
/* 5769 */           return (UnsafeUtil.getInt(message, offset) != 0);
/*      */         case 5:
/* 5771 */           return (UnsafeUtil.getLong(message, offset) != 0L);
/*      */         case 6:
/* 5773 */           return (UnsafeUtil.getInt(message, offset) != 0);
/*      */         case 7:
/* 5775 */           return UnsafeUtil.getBoolean(message, offset);
/*      */         case 8:
/* 5777 */           value = UnsafeUtil.getObject(message, offset);
/* 5778 */           if (value instanceof String)
/* 5779 */             return !((String)value).isEmpty(); 
/* 5780 */           if (value instanceof ByteString) {
/* 5781 */             return !ByteString.EMPTY.equals(value);
/*      */           }
/* 5783 */           throw new IllegalArgumentException();
/*      */         
/*      */         case 9:
/* 5786 */           return (UnsafeUtil.getObject(message, offset) != null);
/*      */         case 10:
/* 5788 */           return !ByteString.EMPTY.equals(UnsafeUtil.getObject(message, offset));
/*      */         case 11:
/* 5790 */           return (UnsafeUtil.getInt(message, offset) != 0);
/*      */         case 12:
/* 5792 */           return (UnsafeUtil.getInt(message, offset) != 0);
/*      */         case 13:
/* 5794 */           return (UnsafeUtil.getInt(message, offset) != 0);
/*      */         case 14:
/* 5796 */           return (UnsafeUtil.getLong(message, offset) != 0L);
/*      */         case 15:
/* 5798 */           return (UnsafeUtil.getInt(message, offset) != 0);
/*      */         case 16:
/* 5800 */           return (UnsafeUtil.getLong(message, offset) != 0L);
/*      */         case 17:
/* 5802 */           return (UnsafeUtil.getObject(message, offset) != null);
/*      */       } 
/* 5804 */       throw new IllegalArgumentException();
/*      */     } 
/*      */     
/* 5807 */     int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
/* 5808 */     int presenceMask = 1 << presenceMaskAndOffset >>> 20;
/* 5809 */     return ((UnsafeUtil.getInt(message, (presenceMaskAndOffset & 0xFFFFF)) & presenceMask) != 0);
/*      */   }
/*      */ 
/*      */   
/*      */   private void setFieldPresent(T message, int pos) {
/* 5814 */     if (this.proto3) {
/*      */       return;
/*      */     }
/*      */     
/* 5818 */     int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
/* 5819 */     int presenceMask = 1 << presenceMaskAndOffset >>> 20;
/* 5820 */     long presenceFieldOffset = (presenceMaskAndOffset & 0xFFFFF);
/* 5821 */     UnsafeUtil.putInt(message, presenceFieldOffset, 
/*      */ 
/*      */         
/* 5824 */         UnsafeUtil.getInt(message, presenceFieldOffset) | presenceMask);
/*      */   }
/*      */   
/*      */   private boolean isOneofPresent(T message, int fieldNumber, int pos) {
/* 5828 */     int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
/* 5829 */     return (UnsafeUtil.getInt(message, (presenceMaskAndOffset & 0xFFFFF)) == fieldNumber);
/*      */   }
/*      */   
/*      */   private boolean isOneofCaseEqual(T message, T other, int pos) {
/* 5833 */     int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
/* 5834 */     return 
/* 5835 */       (UnsafeUtil.getInt(message, (presenceMaskAndOffset & 0xFFFFF)) == UnsafeUtil.getInt(other, (presenceMaskAndOffset & 0xFFFFF)));
/*      */   }
/*      */   
/*      */   private void setOneofPresent(T message, int fieldNumber, int pos) {
/* 5839 */     int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
/* 5840 */     UnsafeUtil.putInt(message, (presenceMaskAndOffset & 0xFFFFF), fieldNumber);
/*      */   }
/*      */   
/*      */   private int positionForFieldNumber(int number) {
/* 5844 */     if (number >= this.minFieldNumber && number <= this.maxFieldNumber) {
/* 5845 */       return slowPositionForFieldNumber(number, 0);
/*      */     }
/* 5847 */     return -1;
/*      */   }
/*      */   
/*      */   private int positionForFieldNumber(int number, int min) {
/* 5851 */     if (number >= this.minFieldNumber && number <= this.maxFieldNumber) {
/* 5852 */       return slowPositionForFieldNumber(number, min);
/*      */     }
/* 5854 */     return -1;
/*      */   }
/*      */   
/*      */   private int slowPositionForFieldNumber(int number, int min) {
/* 5858 */     int max = this.buffer.length / 3 - 1;
/* 5859 */     while (min <= max) {
/*      */       
/* 5861 */       int mid = max + min >>> 1;
/* 5862 */       int pos = mid * 3;
/* 5863 */       int midFieldNumber = numberAt(pos);
/* 5864 */       if (number == midFieldNumber)
/*      */       {
/* 5866 */         return pos;
/*      */       }
/* 5868 */       if (number < midFieldNumber) {
/*      */         
/* 5870 */         max = mid - 1;
/*      */         continue;
/*      */       } 
/* 5873 */       min = mid + 1;
/*      */     } 
/*      */     
/* 5876 */     return -1;
/*      */   }
/*      */   
/*      */   int getSchemaSize() {
/* 5880 */     return this.buffer.length * 3;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MessageSchema.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */