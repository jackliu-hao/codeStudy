/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ final class ArrayDecoders
/*      */ {
/*      */   static final class Registers
/*      */   {
/*      */     public int int1;
/*      */     public long long1;
/*      */     public Object object1;
/*      */     public final ExtensionRegistryLite extensionRegistry;
/*      */     
/*      */     Registers() {
/*   63 */       this.extensionRegistry = ExtensionRegistryLite.getEmptyRegistry();
/*      */     }
/*      */     
/*      */     Registers(ExtensionRegistryLite extensionRegistry) {
/*   67 */       if (extensionRegistry == null) {
/*   68 */         throw new NullPointerException();
/*      */       }
/*   70 */       this.extensionRegistry = extensionRegistry;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeVarint32(byte[] data, int position, Registers registers) {
/*   79 */     int value = data[position++];
/*   80 */     if (value >= 0) {
/*   81 */       registers.int1 = value;
/*   82 */       return position;
/*      */     } 
/*   84 */     return decodeVarint32(value, data, position, registers);
/*      */   }
/*      */ 
/*      */   
/*      */   static int decodeVarint32(int firstByte, byte[] data, int position, Registers registers) {
/*   89 */     int value = firstByte & 0x7F;
/*   90 */     byte b2 = data[position++];
/*   91 */     if (b2 >= 0) {
/*   92 */       registers.int1 = value | b2 << 7;
/*   93 */       return position;
/*      */     } 
/*   95 */     value |= (b2 & Byte.MAX_VALUE) << 7;
/*      */     
/*   97 */     byte b3 = data[position++];
/*   98 */     if (b3 >= 0) {
/*   99 */       registers.int1 = value | b3 << 14;
/*  100 */       return position;
/*      */     } 
/*  102 */     value |= (b3 & Byte.MAX_VALUE) << 14;
/*      */     
/*  104 */     byte b4 = data[position++];
/*  105 */     if (b4 >= 0) {
/*  106 */       registers.int1 = value | b4 << 21;
/*  107 */       return position;
/*      */     } 
/*  109 */     value |= (b4 & Byte.MAX_VALUE) << 21;
/*      */     
/*  111 */     byte b5 = data[position++];
/*  112 */     if (b5 >= 0) {
/*  113 */       registers.int1 = value | b5 << 28;
/*  114 */       return position;
/*      */     } 
/*  116 */     value |= (b5 & Byte.MAX_VALUE) << 28;
/*      */     
/*  118 */     while (data[position++] < 0);
/*      */     
/*  120 */     registers.int1 = value;
/*  121 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeVarint64(byte[] data, int position, Registers registers) {
/*  129 */     long value = data[position++];
/*  130 */     if (value >= 0L) {
/*  131 */       registers.long1 = value;
/*  132 */       return position;
/*      */     } 
/*  134 */     return decodeVarint64(value, data, position, registers);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeVarint64(long firstByte, byte[] data, int position, Registers registers) {
/*  140 */     long value = firstByte & 0x7FL;
/*  141 */     byte next = data[position++];
/*  142 */     int shift = 7;
/*  143 */     value |= (next & Byte.MAX_VALUE) << 7L;
/*  144 */     while (next < 0) {
/*  145 */       next = data[position++];
/*  146 */       shift += 7;
/*  147 */       value |= (next & Byte.MAX_VALUE) << shift;
/*      */     } 
/*  149 */     registers.long1 = value;
/*  150 */     return position;
/*      */   }
/*      */ 
/*      */   
/*      */   static int decodeFixed32(byte[] data, int position) {
/*  155 */     return data[position] & 0xFF | (data[position + 1] & 0xFF) << 8 | (data[position + 2] & 0xFF) << 16 | (data[position + 3] & 0xFF) << 24;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static long decodeFixed64(byte[] data, int position) {
/*  163 */     return data[position] & 0xFFL | (data[position + 1] & 0xFFL) << 8L | (data[position + 2] & 0xFFL) << 16L | (data[position + 3] & 0xFFL) << 24L | (data[position + 4] & 0xFFL) << 32L | (data[position + 5] & 0xFFL) << 40L | (data[position + 6] & 0xFFL) << 48L | (data[position + 7] & 0xFFL) << 56L;
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
/*      */   static double decodeDouble(byte[] data, int position) {
/*  175 */     return Double.longBitsToDouble(decodeFixed64(data, position));
/*      */   }
/*      */ 
/*      */   
/*      */   static float decodeFloat(byte[] data, int position) {
/*  180 */     return Float.intBitsToFloat(decodeFixed32(data, position));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeString(byte[] data, int position, Registers registers) throws InvalidProtocolBufferException {
/*  186 */     position = decodeVarint32(data, position, registers);
/*  187 */     int length = registers.int1;
/*  188 */     if (length < 0)
/*  189 */       throw InvalidProtocolBufferException.negativeSize(); 
/*  190 */     if (length == 0) {
/*  191 */       registers.object1 = "";
/*  192 */       return position;
/*      */     } 
/*  194 */     registers.object1 = new String(data, position, length, Internal.UTF_8);
/*  195 */     return position + length;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeStringRequireUtf8(byte[] data, int position, Registers registers) throws InvalidProtocolBufferException {
/*  202 */     position = decodeVarint32(data, position, registers);
/*  203 */     int length = registers.int1;
/*  204 */     if (length < 0)
/*  205 */       throw InvalidProtocolBufferException.negativeSize(); 
/*  206 */     if (length == 0) {
/*  207 */       registers.object1 = "";
/*  208 */       return position;
/*      */     } 
/*  210 */     registers.object1 = Utf8.decodeUtf8(data, position, length);
/*  211 */     return position + length;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeBytes(byte[] data, int position, Registers registers) throws InvalidProtocolBufferException {
/*  218 */     position = decodeVarint32(data, position, registers);
/*  219 */     int length = registers.int1;
/*  220 */     if (length < 0)
/*  221 */       throw InvalidProtocolBufferException.negativeSize(); 
/*  222 */     if (length > data.length - position)
/*  223 */       throw InvalidProtocolBufferException.truncatedMessage(); 
/*  224 */     if (length == 0) {
/*  225 */       registers.object1 = ByteString.EMPTY;
/*  226 */       return position;
/*      */     } 
/*  228 */     registers.object1 = ByteString.copyFrom(data, position, length);
/*  229 */     return position + length;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeMessageField(Schema<Object> schema, byte[] data, int position, int limit, Registers registers) throws IOException {
/*  237 */     int length = data[position++];
/*  238 */     if (length < 0) {
/*  239 */       position = decodeVarint32(length, data, position, registers);
/*  240 */       length = registers.int1;
/*      */     } 
/*  242 */     if (length < 0 || length > limit - position) {
/*  243 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*  245 */     Object result = schema.newInstance();
/*  246 */     schema.mergeFrom(result, data, position, position + length, registers);
/*  247 */     schema.makeImmutable(result);
/*  248 */     registers.object1 = result;
/*  249 */     return position + length;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeGroupField(Schema schema, byte[] data, int position, int limit, int endGroup, Registers registers) throws IOException {
/*  259 */     MessageSchema<Object> messageSchema = (MessageSchema)schema;
/*  260 */     Object result = messageSchema.newInstance();
/*      */ 
/*      */     
/*  263 */     int endPosition = messageSchema.parseProto2Message(result, data, position, limit, endGroup, registers);
/*  264 */     messageSchema.makeImmutable(result);
/*  265 */     registers.object1 = result;
/*  266 */     return endPosition;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeVarint32List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
/*  272 */     IntArrayList output = (IntArrayList)list;
/*  273 */     position = decodeVarint32(data, position, registers);
/*  274 */     output.addInt(registers.int1);
/*  275 */     while (position < limit) {
/*  276 */       int nextPosition = decodeVarint32(data, position, registers);
/*  277 */       if (tag != registers.int1) {
/*      */         break;
/*      */       }
/*  280 */       position = decodeVarint32(data, nextPosition, registers);
/*  281 */       output.addInt(registers.int1);
/*      */     } 
/*  283 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeVarint64List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
/*  289 */     LongArrayList output = (LongArrayList)list;
/*  290 */     position = decodeVarint64(data, position, registers);
/*  291 */     output.addLong(registers.long1);
/*  292 */     while (position < limit) {
/*  293 */       int nextPosition = decodeVarint32(data, position, registers);
/*  294 */       if (tag != registers.int1) {
/*      */         break;
/*      */       }
/*  297 */       position = decodeVarint64(data, nextPosition, registers);
/*  298 */       output.addLong(registers.long1);
/*      */     } 
/*  300 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeFixed32List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
/*  306 */     IntArrayList output = (IntArrayList)list;
/*  307 */     output.addInt(decodeFixed32(data, position));
/*  308 */     position += 4;
/*  309 */     while (position < limit) {
/*  310 */       int nextPosition = decodeVarint32(data, position, registers);
/*  311 */       if (tag != registers.int1) {
/*      */         break;
/*      */       }
/*  314 */       output.addInt(decodeFixed32(data, nextPosition));
/*  315 */       position = nextPosition + 4;
/*      */     } 
/*  317 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeFixed64List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
/*  323 */     LongArrayList output = (LongArrayList)list;
/*  324 */     output.addLong(decodeFixed64(data, position));
/*  325 */     position += 8;
/*  326 */     while (position < limit) {
/*  327 */       int nextPosition = decodeVarint32(data, position, registers);
/*  328 */       if (tag != registers.int1) {
/*      */         break;
/*      */       }
/*  331 */       output.addLong(decodeFixed64(data, nextPosition));
/*  332 */       position = nextPosition + 8;
/*      */     } 
/*  334 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeFloatList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
/*  340 */     FloatArrayList output = (FloatArrayList)list;
/*  341 */     output.addFloat(decodeFloat(data, position));
/*  342 */     position += 4;
/*  343 */     while (position < limit) {
/*  344 */       int nextPosition = decodeVarint32(data, position, registers);
/*  345 */       if (tag != registers.int1) {
/*      */         break;
/*      */       }
/*  348 */       output.addFloat(decodeFloat(data, nextPosition));
/*  349 */       position = nextPosition + 4;
/*      */     } 
/*  351 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeDoubleList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
/*  357 */     DoubleArrayList output = (DoubleArrayList)list;
/*  358 */     output.addDouble(decodeDouble(data, position));
/*  359 */     position += 8;
/*  360 */     while (position < limit) {
/*  361 */       int nextPosition = decodeVarint32(data, position, registers);
/*  362 */       if (tag != registers.int1) {
/*      */         break;
/*      */       }
/*  365 */       output.addDouble(decodeDouble(data, nextPosition));
/*  366 */       position = nextPosition + 8;
/*      */     } 
/*  368 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeBoolList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
/*  374 */     BooleanArrayList output = (BooleanArrayList)list;
/*  375 */     position = decodeVarint64(data, position, registers);
/*  376 */     output.addBoolean((registers.long1 != 0L));
/*  377 */     while (position < limit) {
/*  378 */       int nextPosition = decodeVarint32(data, position, registers);
/*  379 */       if (tag != registers.int1) {
/*      */         break;
/*      */       }
/*  382 */       position = decodeVarint64(data, nextPosition, registers);
/*  383 */       output.addBoolean((registers.long1 != 0L));
/*      */     } 
/*  385 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeSInt32List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
/*  391 */     IntArrayList output = (IntArrayList)list;
/*  392 */     position = decodeVarint32(data, position, registers);
/*  393 */     output.addInt(CodedInputStream.decodeZigZag32(registers.int1));
/*  394 */     while (position < limit) {
/*  395 */       int nextPosition = decodeVarint32(data, position, registers);
/*  396 */       if (tag != registers.int1) {
/*      */         break;
/*      */       }
/*  399 */       position = decodeVarint32(data, nextPosition, registers);
/*  400 */       output.addInt(CodedInputStream.decodeZigZag32(registers.int1));
/*      */     } 
/*  402 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeSInt64List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
/*  408 */     LongArrayList output = (LongArrayList)list;
/*  409 */     position = decodeVarint64(data, position, registers);
/*  410 */     output.addLong(CodedInputStream.decodeZigZag64(registers.long1));
/*  411 */     while (position < limit) {
/*  412 */       int nextPosition = decodeVarint32(data, position, registers);
/*  413 */       if (tag != registers.int1) {
/*      */         break;
/*      */       }
/*  416 */       position = decodeVarint64(data, nextPosition, registers);
/*  417 */       output.addLong(CodedInputStream.decodeZigZag64(registers.long1));
/*      */     } 
/*  419 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodePackedVarint32List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
/*  425 */     IntArrayList output = (IntArrayList)list;
/*  426 */     position = decodeVarint32(data, position, registers);
/*  427 */     int fieldLimit = position + registers.int1;
/*  428 */     while (position < fieldLimit) {
/*  429 */       position = decodeVarint32(data, position, registers);
/*  430 */       output.addInt(registers.int1);
/*      */     } 
/*  432 */     if (position != fieldLimit) {
/*  433 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*  435 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodePackedVarint64List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
/*  441 */     LongArrayList output = (LongArrayList)list;
/*  442 */     position = decodeVarint32(data, position, registers);
/*  443 */     int fieldLimit = position + registers.int1;
/*  444 */     while (position < fieldLimit) {
/*  445 */       position = decodeVarint64(data, position, registers);
/*  446 */       output.addLong(registers.long1);
/*      */     } 
/*  448 */     if (position != fieldLimit) {
/*  449 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*  451 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodePackedFixed32List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
/*  457 */     IntArrayList output = (IntArrayList)list;
/*  458 */     position = decodeVarint32(data, position, registers);
/*  459 */     int fieldLimit = position + registers.int1;
/*  460 */     while (position < fieldLimit) {
/*  461 */       output.addInt(decodeFixed32(data, position));
/*  462 */       position += 4;
/*      */     } 
/*  464 */     if (position != fieldLimit) {
/*  465 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*  467 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodePackedFixed64List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
/*  473 */     LongArrayList output = (LongArrayList)list;
/*  474 */     position = decodeVarint32(data, position, registers);
/*  475 */     int fieldLimit = position + registers.int1;
/*  476 */     while (position < fieldLimit) {
/*  477 */       output.addLong(decodeFixed64(data, position));
/*  478 */       position += 8;
/*      */     } 
/*  480 */     if (position != fieldLimit) {
/*  481 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*  483 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodePackedFloatList(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
/*  489 */     FloatArrayList output = (FloatArrayList)list;
/*  490 */     position = decodeVarint32(data, position, registers);
/*  491 */     int fieldLimit = position + registers.int1;
/*  492 */     while (position < fieldLimit) {
/*  493 */       output.addFloat(decodeFloat(data, position));
/*  494 */       position += 4;
/*      */     } 
/*  496 */     if (position != fieldLimit) {
/*  497 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*  499 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodePackedDoubleList(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
/*  505 */     DoubleArrayList output = (DoubleArrayList)list;
/*  506 */     position = decodeVarint32(data, position, registers);
/*  507 */     int fieldLimit = position + registers.int1;
/*  508 */     while (position < fieldLimit) {
/*  509 */       output.addDouble(decodeDouble(data, position));
/*  510 */       position += 8;
/*      */     } 
/*  512 */     if (position != fieldLimit) {
/*  513 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*  515 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodePackedBoolList(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
/*  521 */     BooleanArrayList output = (BooleanArrayList)list;
/*  522 */     position = decodeVarint32(data, position, registers);
/*  523 */     int fieldLimit = position + registers.int1;
/*  524 */     while (position < fieldLimit) {
/*  525 */       position = decodeVarint64(data, position, registers);
/*  526 */       output.addBoolean((registers.long1 != 0L));
/*      */     } 
/*  528 */     if (position != fieldLimit) {
/*  529 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*  531 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodePackedSInt32List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
/*  537 */     IntArrayList output = (IntArrayList)list;
/*  538 */     position = decodeVarint32(data, position, registers);
/*  539 */     int fieldLimit = position + registers.int1;
/*  540 */     while (position < fieldLimit) {
/*  541 */       position = decodeVarint32(data, position, registers);
/*  542 */       output.addInt(CodedInputStream.decodeZigZag32(registers.int1));
/*      */     } 
/*  544 */     if (position != fieldLimit) {
/*  545 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*  547 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodePackedSInt64List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
/*  554 */     LongArrayList output = (LongArrayList)list;
/*  555 */     position = decodeVarint32(data, position, registers);
/*  556 */     int fieldLimit = position + registers.int1;
/*  557 */     while (position < fieldLimit) {
/*  558 */       position = decodeVarint64(data, position, registers);
/*  559 */       output.addLong(CodedInputStream.decodeZigZag64(registers.long1));
/*      */     } 
/*  561 */     if (position != fieldLimit) {
/*  562 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*  564 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeStringList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
/*  572 */     Internal.ProtobufList<String> output = (Internal.ProtobufList)list;
/*  573 */     position = decodeVarint32(data, position, registers);
/*  574 */     int length = registers.int1;
/*  575 */     if (length < 0)
/*  576 */       throw InvalidProtocolBufferException.negativeSize(); 
/*  577 */     if (length == 0) {
/*  578 */       output.add("");
/*      */     } else {
/*  580 */       String value = new String(data, position, length, Internal.UTF_8);
/*  581 */       output.add(value);
/*  582 */       position += length;
/*      */     } 
/*  584 */     while (position < limit) {
/*  585 */       int nextPosition = decodeVarint32(data, position, registers);
/*  586 */       if (tag != registers.int1) {
/*      */         break;
/*      */       }
/*  589 */       position = decodeVarint32(data, nextPosition, registers);
/*  590 */       int nextLength = registers.int1;
/*  591 */       if (nextLength < 0)
/*  592 */         throw InvalidProtocolBufferException.negativeSize(); 
/*  593 */       if (nextLength == 0) {
/*  594 */         output.add(""); continue;
/*      */       } 
/*  596 */       String value = new String(data, position, nextLength, Internal.UTF_8);
/*  597 */       output.add(value);
/*  598 */       position += nextLength;
/*      */     } 
/*      */     
/*  601 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeStringListRequireUtf8(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
/*  611 */     Internal.ProtobufList<String> output = (Internal.ProtobufList)list;
/*  612 */     position = decodeVarint32(data, position, registers);
/*  613 */     int length = registers.int1;
/*  614 */     if (length < 0)
/*  615 */       throw InvalidProtocolBufferException.negativeSize(); 
/*  616 */     if (length == 0) {
/*  617 */       output.add("");
/*      */     } else {
/*  619 */       if (!Utf8.isValidUtf8(data, position, position + length)) {
/*  620 */         throw InvalidProtocolBufferException.invalidUtf8();
/*      */       }
/*  622 */       String value = new String(data, position, length, Internal.UTF_8);
/*  623 */       output.add(value);
/*  624 */       position += length;
/*      */     } 
/*  626 */     while (position < limit) {
/*  627 */       int nextPosition = decodeVarint32(data, position, registers);
/*  628 */       if (tag != registers.int1) {
/*      */         break;
/*      */       }
/*  631 */       position = decodeVarint32(data, nextPosition, registers);
/*  632 */       int nextLength = registers.int1;
/*  633 */       if (nextLength < 0)
/*  634 */         throw InvalidProtocolBufferException.negativeSize(); 
/*  635 */       if (nextLength == 0) {
/*  636 */         output.add(""); continue;
/*      */       } 
/*  638 */       if (!Utf8.isValidUtf8(data, position, position + nextLength)) {
/*  639 */         throw InvalidProtocolBufferException.invalidUtf8();
/*      */       }
/*  641 */       String value = new String(data, position, nextLength, Internal.UTF_8);
/*  642 */       output.add(value);
/*  643 */       position += nextLength;
/*      */     } 
/*      */     
/*  646 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeBytesList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
/*  654 */     Internal.ProtobufList<ByteString> output = (Internal.ProtobufList)list;
/*  655 */     position = decodeVarint32(data, position, registers);
/*  656 */     int length = registers.int1;
/*  657 */     if (length < 0)
/*  658 */       throw InvalidProtocolBufferException.negativeSize(); 
/*  659 */     if (length > data.length - position)
/*  660 */       throw InvalidProtocolBufferException.truncatedMessage(); 
/*  661 */     if (length == 0) {
/*  662 */       output.add(ByteString.EMPTY);
/*      */     } else {
/*  664 */       output.add(ByteString.copyFrom(data, position, length));
/*  665 */       position += length;
/*      */     } 
/*  667 */     while (position < limit) {
/*  668 */       int nextPosition = decodeVarint32(data, position, registers);
/*  669 */       if (tag != registers.int1) {
/*      */         break;
/*      */       }
/*  672 */       position = decodeVarint32(data, nextPosition, registers);
/*  673 */       int nextLength = registers.int1;
/*  674 */       if (nextLength < 0)
/*  675 */         throw InvalidProtocolBufferException.negativeSize(); 
/*  676 */       if (nextLength > data.length - position)
/*  677 */         throw InvalidProtocolBufferException.truncatedMessage(); 
/*  678 */       if (nextLength == 0) {
/*  679 */         output.add(ByteString.EMPTY); continue;
/*      */       } 
/*  681 */       output.add(ByteString.copyFrom(data, position, nextLength));
/*  682 */       position += nextLength;
/*      */     } 
/*      */     
/*  685 */     return position;
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
/*      */   static int decodeMessageList(Schema<?> schema, int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws IOException {
/*  703 */     Internal.ProtobufList<Object> output = (Internal.ProtobufList)list;
/*  704 */     position = decodeMessageField(schema, data, position, limit, registers);
/*  705 */     output.add(registers.object1);
/*  706 */     while (position < limit) {
/*  707 */       int nextPosition = decodeVarint32(data, position, registers);
/*  708 */       if (tag != registers.int1) {
/*      */         break;
/*      */       }
/*  711 */       position = decodeMessageField(schema, data, nextPosition, limit, registers);
/*  712 */       output.add(registers.object1);
/*      */     } 
/*  714 */     return position;
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
/*      */   static int decodeGroupList(Schema schema, int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws IOException {
/*  732 */     Internal.ProtobufList<Object> output = (Internal.ProtobufList)list;
/*  733 */     int endgroup = tag & 0xFFFFFFF8 | 0x4;
/*  734 */     position = decodeGroupField(schema, data, position, limit, endgroup, registers);
/*  735 */     output.add(registers.object1);
/*  736 */     while (position < limit) {
/*  737 */       int nextPosition = decodeVarint32(data, position, registers);
/*  738 */       if (tag != registers.int1) {
/*      */         break;
/*      */       }
/*  741 */       position = decodeGroupField(schema, data, nextPosition, limit, endgroup, registers);
/*  742 */       output.add(registers.object1);
/*      */     } 
/*  744 */     return position;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeExtensionOrUnknownField(int tag, byte[] data, int position, int limit, Object message, MessageLite defaultInstance, UnknownFieldSchema<UnknownFieldSetLite, UnknownFieldSetLite> unknownFieldSchema, Registers registers) throws IOException {
/*  754 */     int number = tag >>> 3;
/*      */     
/*  756 */     GeneratedMessageLite.GeneratedExtension<MessageLite, ?> extension = registers.extensionRegistry.findLiteExtensionByNumber(defaultInstance, number);
/*  757 */     if (extension == null) {
/*  758 */       return decodeUnknownField(tag, data, position, limit, 
/*  759 */           MessageSchema.getMutableUnknownFields(message), registers);
/*      */     }
/*  761 */     ((GeneratedMessageLite.ExtendableMessage)message).ensureExtensionsAreMutable();
/*  762 */     return decodeExtension(tag, data, position, limit, (GeneratedMessageLite.ExtendableMessage<?, ?>)message, extension, unknownFieldSchema, registers);
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
/*      */   static int decodeExtension(int tag, byte[] data, int position, int limit, GeneratedMessageLite.ExtendableMessage<?, ?> message, GeneratedMessageLite.GeneratedExtension<?, ?> extension, UnknownFieldSchema<UnknownFieldSetLite, UnknownFieldSetLite> unknownFieldSchema, Registers registers) throws IOException {
/*  778 */     FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions = message.extensions;
/*  779 */     int fieldNumber = tag >>> 3;
/*  780 */     if (extension.descriptor.isRepeated() && extension.descriptor.isPacked())
/*  781 */     { DoubleArrayList doubleArrayList; FloatArrayList floatArrayList; LongArrayList longArrayList3; IntArrayList intArrayList3; LongArrayList longArrayList2; IntArrayList intArrayList2; BooleanArrayList booleanArrayList; IntArrayList intArrayList1; LongArrayList longArrayList1; IntArrayList list; UnknownFieldSetLite unknownFields; switch (extension.getLiteType())
/*      */       
/*      */       { case DOUBLE:
/*  784 */           doubleArrayList = new DoubleArrayList();
/*  785 */           position = decodePackedDoubleList(data, position, doubleArrayList, registers);
/*  786 */           extensions.setField(extension.descriptor, doubleArrayList);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  979 */           return position;case FLOAT: floatArrayList = new FloatArrayList(); position = decodePackedFloatList(data, position, floatArrayList, registers); extensions.setField(extension.descriptor, floatArrayList); return position;case INT64: case UINT64: longArrayList3 = new LongArrayList(); position = decodePackedVarint64List(data, position, longArrayList3, registers); extensions.setField(extension.descriptor, longArrayList3); return position;case INT32: case UINT32: intArrayList3 = new IntArrayList(); position = decodePackedVarint32List(data, position, intArrayList3, registers); extensions.setField(extension.descriptor, intArrayList3); return position;case FIXED64: case SFIXED64: longArrayList2 = new LongArrayList(); position = decodePackedFixed64List(data, position, longArrayList2, registers); extensions.setField(extension.descriptor, longArrayList2); return position;case FIXED32: case SFIXED32: intArrayList2 = new IntArrayList(); position = decodePackedFixed32List(data, position, intArrayList2, registers); extensions.setField(extension.descriptor, intArrayList2); return position;case BOOL: booleanArrayList = new BooleanArrayList(); position = decodePackedBoolList(data, position, booleanArrayList, registers); extensions.setField(extension.descriptor, booleanArrayList); return position;case SINT32: intArrayList1 = new IntArrayList(); position = decodePackedSInt32List(data, position, intArrayList1, registers); extensions.setField(extension.descriptor, intArrayList1); return position;case SINT64: longArrayList1 = new LongArrayList(); position = decodePackedSInt64List(data, position, longArrayList1, registers); extensions.setField(extension.descriptor, longArrayList1); return position;case ENUM: list = new IntArrayList(); position = decodePackedVarint32List(data, position, list, registers); unknownFields = message.unknownFields; if (unknownFields == UnknownFieldSetLite.getDefaultInstance()) unknownFields = null;  unknownFields = SchemaUtil.<UnknownFieldSetLite, UnknownFieldSetLite>filterUnknownEnumList(fieldNumber, list, extension.descriptor.getEnumType(), unknownFields, unknownFieldSchema); if (unknownFields != null) message.unknownFields = unknownFields;  extensions.setField(extension.descriptor, list); return position; }  throw new IllegalStateException("Type cannot be packed: " + extension.descriptor.getLiteType()); }  Object value = null; if (extension.getLiteType() == WireFormat.FieldType.ENUM) { position = decodeVarint32(data, position, registers); Object enumValue = extension.descriptor.getEnumType().findValueByNumber(registers.int1); if (enumValue == null) { UnknownFieldSetLite unknownFields = message.unknownFields; if (unknownFields == UnknownFieldSetLite.getDefaultInstance()) { unknownFields = UnknownFieldSetLite.newInstance(); message.unknownFields = unknownFields; }  SchemaUtil.storeUnknownEnum(fieldNumber, registers.int1, unknownFields, unknownFieldSchema); return position; }  value = Integer.valueOf(registers.int1); } else { int endTag; switch (extension.getLiteType()) { case DOUBLE: value = Double.valueOf(decodeDouble(data, position)); position += 8; break;case FLOAT: value = Float.valueOf(decodeFloat(data, position)); position += 4; break;case INT64: case UINT64: position = decodeVarint64(data, position, registers); value = Long.valueOf(registers.long1); break;case INT32: case UINT32: position = decodeVarint32(data, position, registers); value = Integer.valueOf(registers.int1); break;case FIXED64: case SFIXED64: value = Long.valueOf(decodeFixed64(data, position)); position += 8; break;case FIXED32: case SFIXED32: value = Integer.valueOf(decodeFixed32(data, position)); position += 4; break;case BOOL: position = decodeVarint64(data, position, registers); value = Boolean.valueOf((registers.long1 != 0L)); break;case BYTES: position = decodeBytes(data, position, registers); value = registers.object1; break;case SINT32: position = decodeVarint32(data, position, registers); value = Integer.valueOf(CodedInputStream.decodeZigZag32(registers.int1)); break;case SINT64: position = decodeVarint64(data, position, registers); value = Long.valueOf(CodedInputStream.decodeZigZag64(registers.long1)); break;case STRING: position = decodeString(data, position, registers); value = registers.object1; break;case GROUP: endTag = fieldNumber << 3 | 0x4; position = decodeGroupField(Protobuf.getInstance().schemaFor(extension.getMessageDefaultInstance().getClass()), data, position, limit, endTag, registers); value = registers.object1; break;case MESSAGE: position = decodeMessageField(Protobuf.getInstance().schemaFor(extension.getMessageDefaultInstance().getClass()), data, position, limit, registers); value = registers.object1; break;case ENUM: throw new IllegalStateException("Shouldn't reach here."); }  }  if (extension.isRepeated()) { extensions.addRepeatedField(extension.descriptor, value); } else { Object oldValue; switch (extension.getLiteType()) { case GROUP: case MESSAGE: oldValue = extensions.getField(extension.descriptor); if (oldValue != null) value = Internal.mergeMessage(oldValue, value);  break; }  extensions.setField(extension.descriptor, value); }  return position;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int decodeUnknownField(int tag, byte[] data, int position, int limit, UnknownFieldSetLite unknownFields, Registers registers) throws InvalidProtocolBufferException {
/*      */     int length;
/*      */     UnknownFieldSetLite child;
/*      */     int endGroup;
/*      */     int lastTag;
/*  991 */     if (WireFormat.getTagFieldNumber(tag) == 0) {
/*  992 */       throw InvalidProtocolBufferException.invalidTag();
/*      */     }
/*  994 */     switch (WireFormat.getTagWireType(tag)) {
/*      */       case 0:
/*  996 */         position = decodeVarint64(data, position, registers);
/*  997 */         unknownFields.storeField(tag, Long.valueOf(registers.long1));
/*  998 */         return position;
/*      */       case 5:
/* 1000 */         unknownFields.storeField(tag, Integer.valueOf(decodeFixed32(data, position)));
/* 1001 */         return position + 4;
/*      */       case 1:
/* 1003 */         unknownFields.storeField(tag, Long.valueOf(decodeFixed64(data, position)));
/* 1004 */         return position + 8;
/*      */       case 2:
/* 1006 */         position = decodeVarint32(data, position, registers);
/* 1007 */         length = registers.int1;
/* 1008 */         if (length < 0)
/* 1009 */           throw InvalidProtocolBufferException.negativeSize(); 
/* 1010 */         if (length > data.length - position)
/* 1011 */           throw InvalidProtocolBufferException.truncatedMessage(); 
/* 1012 */         if (length == 0) {
/* 1013 */           unknownFields.storeField(tag, ByteString.EMPTY);
/*      */         } else {
/* 1015 */           unknownFields.storeField(tag, ByteString.copyFrom(data, position, length));
/*      */         } 
/* 1017 */         return position + length;
/*      */       case 3:
/* 1019 */         child = UnknownFieldSetLite.newInstance();
/* 1020 */         endGroup = tag & 0xFFFFFFF8 | 0x4;
/* 1021 */         lastTag = 0;
/* 1022 */         while (position < limit) {
/* 1023 */           position = decodeVarint32(data, position, registers);
/* 1024 */           lastTag = registers.int1;
/* 1025 */           if (lastTag == endGroup) {
/*      */             break;
/*      */           }
/* 1028 */           position = decodeUnknownField(lastTag, data, position, limit, child, registers);
/*      */         } 
/* 1030 */         if (position > limit || lastTag != endGroup) {
/* 1031 */           throw InvalidProtocolBufferException.parseFailure();
/*      */         }
/* 1033 */         unknownFields.storeField(tag, child);
/* 1034 */         return position;
/*      */     } 
/* 1036 */     throw InvalidProtocolBufferException.invalidTag();
/*      */   }
/*      */ 
/*      */   
/*      */   static int skipField(int tag, byte[] data, int position, int limit, Registers registers) throws InvalidProtocolBufferException {
/*      */     int endGroup;
/*      */     int lastTag;
/* 1043 */     if (WireFormat.getTagFieldNumber(tag) == 0) {
/* 1044 */       throw InvalidProtocolBufferException.invalidTag();
/*      */     }
/* 1046 */     switch (WireFormat.getTagWireType(tag)) {
/*      */       case 0:
/* 1048 */         position = decodeVarint64(data, position, registers);
/* 1049 */         return position;
/*      */       case 5:
/* 1051 */         return position + 4;
/*      */       case 1:
/* 1053 */         return position + 8;
/*      */       case 2:
/* 1055 */         position = decodeVarint32(data, position, registers);
/* 1056 */         return position + registers.int1;
/*      */       case 3:
/* 1058 */         endGroup = tag & 0xFFFFFFF8 | 0x4;
/* 1059 */         lastTag = 0;
/* 1060 */         while (position < limit) {
/* 1061 */           position = decodeVarint32(data, position, registers);
/* 1062 */           lastTag = registers.int1;
/* 1063 */           if (lastTag == endGroup) {
/*      */             break;
/*      */           }
/* 1066 */           position = skipField(lastTag, data, position, limit, registers);
/*      */         } 
/* 1068 */         if (position > limit || lastTag != endGroup) {
/* 1069 */           throw InvalidProtocolBufferException.parseFailure();
/*      */         }
/* 1071 */         return position;
/*      */     } 
/* 1073 */     throw InvalidProtocolBufferException.invalidTag();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ArrayDecoders.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */