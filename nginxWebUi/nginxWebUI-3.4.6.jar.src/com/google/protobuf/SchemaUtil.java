/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class SchemaUtil
/*     */ {
/*  45 */   private static final Class<?> GENERATED_MESSAGE_CLASS = getGeneratedMessageClass();
/*     */   
/*  47 */   private static final UnknownFieldSchema<?, ?> PROTO2_UNKNOWN_FIELD_SET_SCHEMA = getUnknownFieldSetSchema(false);
/*     */   
/*  49 */   private static final UnknownFieldSchema<?, ?> PROTO3_UNKNOWN_FIELD_SET_SCHEMA = getUnknownFieldSetSchema(true);
/*  50 */   private static final UnknownFieldSchema<?, ?> UNKNOWN_FIELD_SET_LITE_SCHEMA = new UnknownFieldSetLiteSchema();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int DEFAULT_LOOK_UP_START_NUMBER = 40;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void requireGeneratedMessage(Class<?> messageType) {
/*  62 */     if (!GeneratedMessageLite.class.isAssignableFrom(messageType) && GENERATED_MESSAGE_CLASS != null && 
/*     */       
/*  64 */       !GENERATED_MESSAGE_CLASS.isAssignableFrom(messageType)) {
/*  65 */       throw new IllegalArgumentException("Message classes must extend GeneratedMessage or GeneratedMessageLite");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeDouble(int fieldNumber, double value, Writer writer) throws IOException {
/*  71 */     if (Double.compare(value, 0.0D) != 0) {
/*  72 */       writer.writeDouble(fieldNumber, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeFloat(int fieldNumber, float value, Writer writer) throws IOException {
/*  77 */     if (Float.compare(value, 0.0F) != 0) {
/*  78 */       writer.writeFloat(fieldNumber, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeInt64(int fieldNumber, long value, Writer writer) throws IOException {
/*  83 */     if (value != 0L) {
/*  84 */       writer.writeInt64(fieldNumber, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeUInt64(int fieldNumber, long value, Writer writer) throws IOException {
/*  89 */     if (value != 0L) {
/*  90 */       writer.writeUInt64(fieldNumber, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeSInt64(int fieldNumber, long value, Writer writer) throws IOException {
/*  95 */     if (value != 0L) {
/*  96 */       writer.writeSInt64(fieldNumber, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeFixed64(int fieldNumber, long value, Writer writer) throws IOException {
/* 101 */     if (value != 0L) {
/* 102 */       writer.writeFixed64(fieldNumber, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeSFixed64(int fieldNumber, long value, Writer writer) throws IOException {
/* 107 */     if (value != 0L) {
/* 108 */       writer.writeSFixed64(fieldNumber, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeInt32(int fieldNumber, int value, Writer writer) throws IOException {
/* 113 */     if (value != 0) {
/* 114 */       writer.writeInt32(fieldNumber, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeUInt32(int fieldNumber, int value, Writer writer) throws IOException {
/* 119 */     if (value != 0) {
/* 120 */       writer.writeUInt32(fieldNumber, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeSInt32(int fieldNumber, int value, Writer writer) throws IOException {
/* 125 */     if (value != 0) {
/* 126 */       writer.writeSInt32(fieldNumber, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeFixed32(int fieldNumber, int value, Writer writer) throws IOException {
/* 131 */     if (value != 0) {
/* 132 */       writer.writeFixed32(fieldNumber, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeSFixed32(int fieldNumber, int value, Writer writer) throws IOException {
/* 137 */     if (value != 0) {
/* 138 */       writer.writeSFixed32(fieldNumber, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeEnum(int fieldNumber, int value, Writer writer) throws IOException {
/* 143 */     if (value != 0) {
/* 144 */       writer.writeEnum(fieldNumber, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeBool(int fieldNumber, boolean value, Writer writer) throws IOException {
/* 149 */     if (value) {
/* 150 */       writer.writeBool(fieldNumber, true);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeString(int fieldNumber, Object value, Writer writer) throws IOException {
/* 155 */     if (value instanceof String) {
/* 156 */       writeStringInternal(fieldNumber, (String)value, writer);
/*     */     } else {
/* 158 */       writeBytes(fieldNumber, (ByteString)value, writer);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void writeStringInternal(int fieldNumber, String value, Writer writer) throws IOException {
/* 164 */     if (value != null && !value.isEmpty()) {
/* 165 */       writer.writeString(fieldNumber, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeBytes(int fieldNumber, ByteString value, Writer writer) throws IOException {
/* 171 */     if (value != null && !value.isEmpty()) {
/* 172 */       writer.writeBytes(fieldNumber, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeMessage(int fieldNumber, Object value, Writer writer) throws IOException {
/* 177 */     if (value != null) {
/* 178 */       writer.writeMessage(fieldNumber, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeDoubleList(int fieldNumber, List<Double> value, Writer writer, boolean packed) throws IOException {
/* 184 */     if (value != null && !value.isEmpty()) {
/* 185 */       writer.writeDoubleList(fieldNumber, value, packed);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeFloatList(int fieldNumber, List<Float> value, Writer writer, boolean packed) throws IOException {
/* 191 */     if (value != null && !value.isEmpty()) {
/* 192 */       writer.writeFloatList(fieldNumber, value, packed);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeInt64List(int fieldNumber, List<Long> value, Writer writer, boolean packed) throws IOException {
/* 198 */     if (value != null && !value.isEmpty()) {
/* 199 */       writer.writeInt64List(fieldNumber, value, packed);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeUInt64List(int fieldNumber, List<Long> value, Writer writer, boolean packed) throws IOException {
/* 205 */     if (value != null && !value.isEmpty()) {
/* 206 */       writer.writeUInt64List(fieldNumber, value, packed);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeSInt64List(int fieldNumber, List<Long> value, Writer writer, boolean packed) throws IOException {
/* 212 */     if (value != null && !value.isEmpty()) {
/* 213 */       writer.writeSInt64List(fieldNumber, value, packed);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeFixed64List(int fieldNumber, List<Long> value, Writer writer, boolean packed) throws IOException {
/* 219 */     if (value != null && !value.isEmpty()) {
/* 220 */       writer.writeFixed64List(fieldNumber, value, packed);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeSFixed64List(int fieldNumber, List<Long> value, Writer writer, boolean packed) throws IOException {
/* 226 */     if (value != null && !value.isEmpty()) {
/* 227 */       writer.writeSFixed64List(fieldNumber, value, packed);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeInt32List(int fieldNumber, List<Integer> value, Writer writer, boolean packed) throws IOException {
/* 233 */     if (value != null && !value.isEmpty()) {
/* 234 */       writer.writeInt32List(fieldNumber, value, packed);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeUInt32List(int fieldNumber, List<Integer> value, Writer writer, boolean packed) throws IOException {
/* 240 */     if (value != null && !value.isEmpty()) {
/* 241 */       writer.writeUInt32List(fieldNumber, value, packed);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeSInt32List(int fieldNumber, List<Integer> value, Writer writer, boolean packed) throws IOException {
/* 247 */     if (value != null && !value.isEmpty()) {
/* 248 */       writer.writeSInt32List(fieldNumber, value, packed);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeFixed32List(int fieldNumber, List<Integer> value, Writer writer, boolean packed) throws IOException {
/* 254 */     if (value != null && !value.isEmpty()) {
/* 255 */       writer.writeFixed32List(fieldNumber, value, packed);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeSFixed32List(int fieldNumber, List<Integer> value, Writer writer, boolean packed) throws IOException {
/* 261 */     if (value != null && !value.isEmpty()) {
/* 262 */       writer.writeSFixed32List(fieldNumber, value, packed);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeEnumList(int fieldNumber, List<Integer> value, Writer writer, boolean packed) throws IOException {
/* 268 */     if (value != null && !value.isEmpty()) {
/* 269 */       writer.writeEnumList(fieldNumber, value, packed);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeBoolList(int fieldNumber, List<Boolean> value, Writer writer, boolean packed) throws IOException {
/* 275 */     if (value != null && !value.isEmpty()) {
/* 276 */       writer.writeBoolList(fieldNumber, value, packed);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeStringList(int fieldNumber, List<String> value, Writer writer) throws IOException {
/* 282 */     if (value != null && !value.isEmpty()) {
/* 283 */       writer.writeStringList(fieldNumber, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeBytesList(int fieldNumber, List<ByteString> value, Writer writer) throws IOException {
/* 289 */     if (value != null && !value.isEmpty()) {
/* 290 */       writer.writeBytesList(fieldNumber, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeMessageList(int fieldNumber, List<?> value, Writer writer) throws IOException {
/* 296 */     if (value != null && !value.isEmpty()) {
/* 297 */       writer.writeMessageList(fieldNumber, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeMessageList(int fieldNumber, List<?> value, Writer writer, Schema schema) throws IOException {
/* 303 */     if (value != null && !value.isEmpty()) {
/* 304 */       writer.writeMessageList(fieldNumber, value, schema);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeLazyFieldList(int fieldNumber, List<?> value, Writer writer) throws IOException {
/* 310 */     if (value != null && !value.isEmpty()) {
/* 311 */       for (Object item : value) {
/* 312 */         ((LazyFieldLite)item).writeTo(writer, fieldNumber);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeGroupList(int fieldNumber, List<?> value, Writer writer) throws IOException {
/* 319 */     if (value != null && !value.isEmpty()) {
/* 320 */       writer.writeGroupList(fieldNumber, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeGroupList(int fieldNumber, List<?> value, Writer writer, Schema schema) throws IOException {
/* 326 */     if (value != null && !value.isEmpty()) {
/* 327 */       writer.writeGroupList(fieldNumber, value, schema);
/*     */     }
/*     */   }
/*     */   
/*     */   static int computeSizeInt64ListNoTag(List<Long> list) {
/* 332 */     int length = list.size();
/* 333 */     if (length == 0) {
/* 334 */       return 0;
/*     */     }
/*     */     
/* 337 */     int size = 0;
/*     */     
/* 339 */     if (list instanceof LongArrayList) {
/* 340 */       LongArrayList primitiveList = (LongArrayList)list;
/* 341 */       for (int i = 0; i < length; i++) {
/* 342 */         size += CodedOutputStream.computeInt64SizeNoTag(primitiveList.getLong(i));
/*     */       }
/*     */     } else {
/* 345 */       for (int i = 0; i < length; i++) {
/* 346 */         size += CodedOutputStream.computeInt64SizeNoTag(((Long)list.get(i)).longValue());
/*     */       }
/*     */     } 
/* 349 */     return size;
/*     */   }
/*     */   
/*     */   static int computeSizeInt64List(int fieldNumber, List<Long> list, boolean packed) {
/* 353 */     int length = list.size();
/* 354 */     if (length == 0) {
/* 355 */       return 0;
/*     */     }
/* 357 */     int size = computeSizeInt64ListNoTag(list);
/*     */     
/* 359 */     if (packed) {
/* 360 */       return CodedOutputStream.computeTagSize(fieldNumber) + 
/* 361 */         CodedOutputStream.computeLengthDelimitedFieldSize(size);
/*     */     }
/* 363 */     return size + list.size() * CodedOutputStream.computeTagSize(fieldNumber);
/*     */   }
/*     */ 
/*     */   
/*     */   static int computeSizeUInt64ListNoTag(List<Long> list) {
/* 368 */     int length = list.size();
/* 369 */     if (length == 0) {
/* 370 */       return 0;
/*     */     }
/*     */     
/* 373 */     int size = 0;
/*     */     
/* 375 */     if (list instanceof LongArrayList) {
/* 376 */       LongArrayList primitiveList = (LongArrayList)list;
/* 377 */       for (int i = 0; i < length; i++) {
/* 378 */         size += CodedOutputStream.computeUInt64SizeNoTag(primitiveList.getLong(i));
/*     */       }
/*     */     } else {
/* 381 */       for (int i = 0; i < length; i++) {
/* 382 */         size += CodedOutputStream.computeUInt64SizeNoTag(((Long)list.get(i)).longValue());
/*     */       }
/*     */     } 
/* 385 */     return size;
/*     */   }
/*     */   
/*     */   static int computeSizeUInt64List(int fieldNumber, List<Long> list, boolean packed) {
/* 389 */     int length = list.size();
/* 390 */     if (length == 0) {
/* 391 */       return 0;
/*     */     }
/* 393 */     int size = computeSizeUInt64ListNoTag(list);
/*     */     
/* 395 */     if (packed) {
/* 396 */       return CodedOutputStream.computeTagSize(fieldNumber) + 
/* 397 */         CodedOutputStream.computeLengthDelimitedFieldSize(size);
/*     */     }
/* 399 */     return size + length * CodedOutputStream.computeTagSize(fieldNumber);
/*     */   }
/*     */ 
/*     */   
/*     */   static int computeSizeSInt64ListNoTag(List<Long> list) {
/* 404 */     int length = list.size();
/* 405 */     if (length == 0) {
/* 406 */       return 0;
/*     */     }
/*     */     
/* 409 */     int size = 0;
/*     */     
/* 411 */     if (list instanceof LongArrayList) {
/* 412 */       LongArrayList primitiveList = (LongArrayList)list;
/* 413 */       for (int i = 0; i < length; i++) {
/* 414 */         size += CodedOutputStream.computeSInt64SizeNoTag(primitiveList.getLong(i));
/*     */       }
/*     */     } else {
/* 417 */       for (int i = 0; i < length; i++) {
/* 418 */         size += CodedOutputStream.computeSInt64SizeNoTag(((Long)list.get(i)).longValue());
/*     */       }
/*     */     } 
/* 421 */     return size;
/*     */   }
/*     */   
/*     */   static int computeSizeSInt64List(int fieldNumber, List<Long> list, boolean packed) {
/* 425 */     int length = list.size();
/* 426 */     if (length == 0) {
/* 427 */       return 0;
/*     */     }
/* 429 */     int size = computeSizeSInt64ListNoTag(list);
/*     */     
/* 431 */     if (packed) {
/* 432 */       return CodedOutputStream.computeTagSize(fieldNumber) + 
/* 433 */         CodedOutputStream.computeLengthDelimitedFieldSize(size);
/*     */     }
/* 435 */     return size + length * CodedOutputStream.computeTagSize(fieldNumber);
/*     */   }
/*     */ 
/*     */   
/*     */   static int computeSizeEnumListNoTag(List<Integer> list) {
/* 440 */     int length = list.size();
/* 441 */     if (length == 0) {
/* 442 */       return 0;
/*     */     }
/*     */     
/* 445 */     int size = 0;
/*     */     
/* 447 */     if (list instanceof IntArrayList) {
/* 448 */       IntArrayList primitiveList = (IntArrayList)list;
/* 449 */       for (int i = 0; i < length; i++) {
/* 450 */         size += CodedOutputStream.computeEnumSizeNoTag(primitiveList.getInt(i));
/*     */       }
/*     */     } else {
/* 453 */       for (int i = 0; i < length; i++) {
/* 454 */         size += CodedOutputStream.computeEnumSizeNoTag(((Integer)list.get(i)).intValue());
/*     */       }
/*     */     } 
/* 457 */     return size;
/*     */   }
/*     */   
/*     */   static int computeSizeEnumList(int fieldNumber, List<Integer> list, boolean packed) {
/* 461 */     int length = list.size();
/* 462 */     if (length == 0) {
/* 463 */       return 0;
/*     */     }
/* 465 */     int size = computeSizeEnumListNoTag(list);
/*     */     
/* 467 */     if (packed) {
/* 468 */       return CodedOutputStream.computeTagSize(fieldNumber) + 
/* 469 */         CodedOutputStream.computeLengthDelimitedFieldSize(size);
/*     */     }
/* 471 */     return size + length * CodedOutputStream.computeTagSize(fieldNumber);
/*     */   }
/*     */ 
/*     */   
/*     */   static int computeSizeInt32ListNoTag(List<Integer> list) {
/* 476 */     int length = list.size();
/* 477 */     if (length == 0) {
/* 478 */       return 0;
/*     */     }
/*     */     
/* 481 */     int size = 0;
/*     */     
/* 483 */     if (list instanceof IntArrayList) {
/* 484 */       IntArrayList primitiveList = (IntArrayList)list;
/* 485 */       for (int i = 0; i < length; i++) {
/* 486 */         size += CodedOutputStream.computeInt32SizeNoTag(primitiveList.getInt(i));
/*     */       }
/*     */     } else {
/* 489 */       for (int i = 0; i < length; i++) {
/* 490 */         size += CodedOutputStream.computeInt32SizeNoTag(((Integer)list.get(i)).intValue());
/*     */       }
/*     */     } 
/* 493 */     return size;
/*     */   }
/*     */   
/*     */   static int computeSizeInt32List(int fieldNumber, List<Integer> list, boolean packed) {
/* 497 */     int length = list.size();
/* 498 */     if (length == 0) {
/* 499 */       return 0;
/*     */     }
/* 501 */     int size = computeSizeInt32ListNoTag(list);
/*     */     
/* 503 */     if (packed) {
/* 504 */       return CodedOutputStream.computeTagSize(fieldNumber) + 
/* 505 */         CodedOutputStream.computeLengthDelimitedFieldSize(size);
/*     */     }
/* 507 */     return size + length * CodedOutputStream.computeTagSize(fieldNumber);
/*     */   }
/*     */ 
/*     */   
/*     */   static int computeSizeUInt32ListNoTag(List<Integer> list) {
/* 512 */     int length = list.size();
/* 513 */     if (length == 0) {
/* 514 */       return 0;
/*     */     }
/*     */     
/* 517 */     int size = 0;
/*     */     
/* 519 */     if (list instanceof IntArrayList) {
/* 520 */       IntArrayList primitiveList = (IntArrayList)list;
/* 521 */       for (int i = 0; i < length; i++) {
/* 522 */         size += CodedOutputStream.computeUInt32SizeNoTag(primitiveList.getInt(i));
/*     */       }
/*     */     } else {
/* 525 */       for (int i = 0; i < length; i++) {
/* 526 */         size += CodedOutputStream.computeUInt32SizeNoTag(((Integer)list.get(i)).intValue());
/*     */       }
/*     */     } 
/* 529 */     return size;
/*     */   }
/*     */   
/*     */   static int computeSizeUInt32List(int fieldNumber, List<Integer> list, boolean packed) {
/* 533 */     int length = list.size();
/* 534 */     if (length == 0) {
/* 535 */       return 0;
/*     */     }
/* 537 */     int size = computeSizeUInt32ListNoTag(list);
/*     */     
/* 539 */     if (packed) {
/* 540 */       return CodedOutputStream.computeTagSize(fieldNumber) + 
/* 541 */         CodedOutputStream.computeLengthDelimitedFieldSize(size);
/*     */     }
/* 543 */     return size + length * CodedOutputStream.computeTagSize(fieldNumber);
/*     */   }
/*     */ 
/*     */   
/*     */   static int computeSizeSInt32ListNoTag(List<Integer> list) {
/* 548 */     int length = list.size();
/* 549 */     if (length == 0) {
/* 550 */       return 0;
/*     */     }
/*     */     
/* 553 */     int size = 0;
/*     */     
/* 555 */     if (list instanceof IntArrayList) {
/* 556 */       IntArrayList primitiveList = (IntArrayList)list;
/* 557 */       for (int i = 0; i < length; i++) {
/* 558 */         size += CodedOutputStream.computeSInt32SizeNoTag(primitiveList.getInt(i));
/*     */       }
/*     */     } else {
/* 561 */       for (int i = 0; i < length; i++) {
/* 562 */         size += CodedOutputStream.computeSInt32SizeNoTag(((Integer)list.get(i)).intValue());
/*     */       }
/*     */     } 
/* 565 */     return size;
/*     */   }
/*     */   
/*     */   static int computeSizeSInt32List(int fieldNumber, List<Integer> list, boolean packed) {
/* 569 */     int length = list.size();
/* 570 */     if (length == 0) {
/* 571 */       return 0;
/*     */     }
/*     */     
/* 574 */     int size = computeSizeSInt32ListNoTag(list);
/*     */     
/* 576 */     if (packed) {
/* 577 */       return CodedOutputStream.computeTagSize(fieldNumber) + 
/* 578 */         CodedOutputStream.computeLengthDelimitedFieldSize(size);
/*     */     }
/* 580 */     return size + length * CodedOutputStream.computeTagSize(fieldNumber);
/*     */   }
/*     */ 
/*     */   
/*     */   static int computeSizeFixed32ListNoTag(List<?> list) {
/* 585 */     return list.size() * 4;
/*     */   }
/*     */   
/*     */   static int computeSizeFixed32List(int fieldNumber, List<?> list, boolean packed) {
/* 589 */     int length = list.size();
/* 590 */     if (length == 0) {
/* 591 */       return 0;
/*     */     }
/* 593 */     if (packed) {
/* 594 */       int dataSize = length * 4;
/* 595 */       return CodedOutputStream.computeTagSize(fieldNumber) + 
/* 596 */         CodedOutputStream.computeLengthDelimitedFieldSize(dataSize);
/*     */     } 
/* 598 */     return length * CodedOutputStream.computeFixed32Size(fieldNumber, 0);
/*     */   }
/*     */ 
/*     */   
/*     */   static int computeSizeFixed64ListNoTag(List<?> list) {
/* 603 */     return list.size() * 8;
/*     */   }
/*     */   
/*     */   static int computeSizeFixed64List(int fieldNumber, List<?> list, boolean packed) {
/* 607 */     int length = list.size();
/* 608 */     if (length == 0) {
/* 609 */       return 0;
/*     */     }
/* 611 */     if (packed) {
/* 612 */       int dataSize = length * 8;
/* 613 */       return CodedOutputStream.computeTagSize(fieldNumber) + 
/* 614 */         CodedOutputStream.computeLengthDelimitedFieldSize(dataSize);
/*     */     } 
/* 616 */     return length * CodedOutputStream.computeFixed64Size(fieldNumber, 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static int computeSizeBoolListNoTag(List<?> list) {
/* 622 */     return list.size();
/*     */   }
/*     */   
/*     */   static int computeSizeBoolList(int fieldNumber, List<?> list, boolean packed) {
/* 626 */     int length = list.size();
/* 627 */     if (length == 0) {
/* 628 */       return 0;
/*     */     }
/* 630 */     if (packed)
/*     */     {
/* 632 */       return CodedOutputStream.computeTagSize(fieldNumber) + 
/* 633 */         CodedOutputStream.computeLengthDelimitedFieldSize(length);
/*     */     }
/* 635 */     return length * CodedOutputStream.computeBoolSize(fieldNumber, true);
/*     */   }
/*     */ 
/*     */   
/*     */   static int computeSizeStringList(int fieldNumber, List<?> list) {
/* 640 */     int length = list.size();
/* 641 */     if (length == 0) {
/* 642 */       return 0;
/*     */     }
/* 644 */     int size = length * CodedOutputStream.computeTagSize(fieldNumber);
/* 645 */     if (list instanceof LazyStringList) {
/* 646 */       LazyStringList lazyList = (LazyStringList)list;
/* 647 */       for (int i = 0; i < length; i++) {
/* 648 */         Object value = lazyList.getRaw(i);
/* 649 */         if (value instanceof ByteString) {
/* 650 */           size += CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
/*     */         } else {
/* 652 */           size += CodedOutputStream.computeStringSizeNoTag((String)value);
/*     */         } 
/*     */       } 
/*     */     } else {
/* 656 */       for (int i = 0; i < length; i++) {
/* 657 */         Object value = list.get(i);
/* 658 */         if (value instanceof ByteString) {
/* 659 */           size += CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
/*     */         } else {
/* 661 */           size += CodedOutputStream.computeStringSizeNoTag((String)value);
/*     */         } 
/*     */       } 
/*     */     } 
/* 665 */     return size;
/*     */   }
/*     */   
/*     */   static int computeSizeMessage(int fieldNumber, Object value, Schema schema) {
/* 669 */     if (value instanceof LazyFieldLite) {
/* 670 */       return CodedOutputStream.computeLazyFieldSize(fieldNumber, (LazyFieldLite)value);
/*     */     }
/* 672 */     return CodedOutputStream.computeMessageSize(fieldNumber, (MessageLite)value, schema);
/*     */   }
/*     */ 
/*     */   
/*     */   static int computeSizeMessageList(int fieldNumber, List<?> list) {
/* 677 */     int length = list.size();
/* 678 */     if (length == 0) {
/* 679 */       return 0;
/*     */     }
/* 681 */     int size = length * CodedOutputStream.computeTagSize(fieldNumber);
/* 682 */     for (int i = 0; i < length; i++) {
/* 683 */       Object value = list.get(i);
/* 684 */       if (value instanceof LazyFieldLite) {
/* 685 */         size += CodedOutputStream.computeLazyFieldSizeNoTag((LazyFieldLite)value);
/*     */       } else {
/* 687 */         size += CodedOutputStream.computeMessageSizeNoTag((MessageLite)value);
/*     */       } 
/*     */     } 
/* 690 */     return size;
/*     */   }
/*     */   
/*     */   static int computeSizeMessageList(int fieldNumber, List<?> list, Schema schema) {
/* 694 */     int length = list.size();
/* 695 */     if (length == 0) {
/* 696 */       return 0;
/*     */     }
/* 698 */     int size = length * CodedOutputStream.computeTagSize(fieldNumber);
/* 699 */     for (int i = 0; i < length; i++) {
/* 700 */       Object value = list.get(i);
/* 701 */       if (value instanceof LazyFieldLite) {
/* 702 */         size += CodedOutputStream.computeLazyFieldSizeNoTag((LazyFieldLite)value);
/*     */       } else {
/* 704 */         size += CodedOutputStream.computeMessageSizeNoTag((MessageLite)value, schema);
/*     */       } 
/*     */     } 
/* 707 */     return size;
/*     */   }
/*     */   
/*     */   static int computeSizeByteStringList(int fieldNumber, List<ByteString> list) {
/* 711 */     int length = list.size();
/* 712 */     if (length == 0) {
/* 713 */       return 0;
/*     */     }
/* 715 */     int size = length * CodedOutputStream.computeTagSize(fieldNumber);
/* 716 */     for (int i = 0; i < list.size(); i++) {
/* 717 */       size += CodedOutputStream.computeBytesSizeNoTag(list.get(i));
/*     */     }
/* 719 */     return size;
/*     */   }
/*     */   
/*     */   static int computeSizeGroupList(int fieldNumber, List<MessageLite> list) {
/* 723 */     int length = list.size();
/* 724 */     if (length == 0) {
/* 725 */       return 0;
/*     */     }
/* 727 */     int size = 0;
/* 728 */     for (int i = 0; i < length; i++) {
/* 729 */       size += CodedOutputStream.computeGroupSize(fieldNumber, list.get(i));
/*     */     }
/* 731 */     return size;
/*     */   }
/*     */   
/*     */   static int computeSizeGroupList(int fieldNumber, List<MessageLite> list, Schema schema) {
/* 735 */     int length = list.size();
/* 736 */     if (length == 0) {
/* 737 */       return 0;
/*     */     }
/* 739 */     int size = 0;
/* 740 */     for (int i = 0; i < length; i++) {
/* 741 */       size += CodedOutputStream.computeGroupSize(fieldNumber, list.get(i), schema);
/*     */     }
/* 743 */     return size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean shouldUseTableSwitch(FieldInfo[] fields) {
/* 754 */     if (fields.length == 0) {
/* 755 */       return false;
/*     */     }
/*     */     
/* 758 */     int lo = fields[0].getFieldNumber();
/* 759 */     int hi = fields[fields.length - 1].getFieldNumber();
/* 760 */     return shouldUseTableSwitch(lo, hi, fields.length);
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
/*     */   public static boolean shouldUseTableSwitch(int lo, int hi, int numFields) {
/* 775 */     if (hi < 40) {
/* 776 */       return true;
/*     */     }
/* 778 */     long tableSpaceCost = hi - lo + 1L;
/* 779 */     long tableTimeCost = 3L;
/* 780 */     long lookupSpaceCost = 3L + 2L * numFields;
/* 781 */     long lookupTimeCost = 3L + numFields;
/* 782 */     return (tableSpaceCost + 3L * tableTimeCost <= lookupSpaceCost + 3L * lookupTimeCost);
/*     */   }
/*     */   
/*     */   public static UnknownFieldSchema<?, ?> proto2UnknownFieldSetSchema() {
/* 786 */     return PROTO2_UNKNOWN_FIELD_SET_SCHEMA;
/*     */   }
/*     */   
/*     */   public static UnknownFieldSchema<?, ?> proto3UnknownFieldSetSchema() {
/* 790 */     return PROTO3_UNKNOWN_FIELD_SET_SCHEMA;
/*     */   }
/*     */   
/*     */   public static UnknownFieldSchema<?, ?> unknownFieldSetLiteSchema() {
/* 794 */     return UNKNOWN_FIELD_SET_LITE_SCHEMA;
/*     */   }
/*     */   
/*     */   private static UnknownFieldSchema<?, ?> getUnknownFieldSetSchema(boolean proto3) {
/*     */     try {
/* 799 */       Class<?> clz = getUnknownFieldSetSchemaClass();
/* 800 */       if (clz == null) {
/* 801 */         return null;
/*     */       }
/* 803 */       return clz.getConstructor(new Class[] { boolean.class }).newInstance(new Object[] { Boolean.valueOf(proto3) });
/* 804 */     } catch (Throwable t) {
/* 805 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Class<?> getGeneratedMessageClass() {
/*     */     try {
/* 811 */       return Class.forName("com.google.protobuf.GeneratedMessageV3");
/* 812 */     } catch (Throwable e) {
/* 813 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Class<?> getUnknownFieldSetSchemaClass() {
/*     */     try {
/* 819 */       return Class.forName("com.google.protobuf.UnknownFieldSetSchema");
/* 820 */     } catch (Throwable e) {
/* 821 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static Object getMapDefaultEntry(Class<?> clazz, String name) {
/*     */     try {
/* 828 */       Class<?> holder = Class.forName(clazz.getName() + "$" + toCamelCase(name, true) + "DefaultEntryHolder");
/* 829 */       Field[] fields = holder.getDeclaredFields();
/* 830 */       if (fields.length != 1) {
/* 831 */         throw new IllegalStateException("Unable to look up map field default entry holder class for " + name + " in " + clazz
/*     */ 
/*     */ 
/*     */             
/* 835 */             .getName());
/*     */       }
/* 837 */       return UnsafeUtil.getStaticObject(fields[0]);
/* 838 */     } catch (Throwable t) {
/* 839 */       throw new RuntimeException(t);
/*     */     } 
/*     */   }
/*     */   
/*     */   static String toCamelCase(String name, boolean capNext) {
/* 844 */     StringBuilder sb = new StringBuilder();
/* 845 */     for (int i = 0; i < name.length(); i++) {
/* 846 */       char c = name.charAt(i);
/*     */       
/* 848 */       if ('a' <= c && c <= 'z') {
/* 849 */         if (capNext) {
/* 850 */           sb.append((char)(c + -32));
/*     */         } else {
/* 852 */           sb.append(c);
/*     */         } 
/* 854 */         capNext = false;
/* 855 */       } else if ('A' <= c && c <= 'Z') {
/* 856 */         if (i == 0 && !capNext) {
/*     */           
/* 858 */           sb.append((char)(c - -32));
/*     */         } else {
/* 860 */           sb.append(c);
/*     */         } 
/* 862 */         capNext = false;
/* 863 */       } else if ('0' <= c && c <= '9') {
/* 864 */         sb.append(c);
/* 865 */         capNext = true;
/*     */       } else {
/* 867 */         capNext = true;
/*     */       } 
/*     */     } 
/* 870 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   static boolean safeEquals(Object a, Object b) {
/* 875 */     return (a == b || (a != null && a.equals(b)));
/*     */   }
/*     */ 
/*     */   
/*     */   static <T> void mergeMap(MapFieldSchema mapFieldSchema, T message, T o, long offset) {
/* 880 */     Object merged = mapFieldSchema.mergeFrom(
/* 881 */         UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(o, offset));
/* 882 */     UnsafeUtil.putObject(message, offset, merged);
/*     */   }
/*     */ 
/*     */   
/*     */   static <T, FT extends FieldSet.FieldDescriptorLite<FT>> void mergeExtensions(ExtensionSchema<FT> schema, T message, T other) {
/* 887 */     FieldSet<FT> otherExtensions = schema.getExtensions(other);
/* 888 */     if (!otherExtensions.isEmpty()) {
/* 889 */       FieldSet<FT> messageExtensions = schema.getMutableExtensions(message);
/* 890 */       messageExtensions.mergeFrom(otherExtensions);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static <T, UT, UB> void mergeUnknownFields(UnknownFieldSchema<UT, UB> schema, T message, T other) {
/* 896 */     UT messageUnknowns = schema.getFromMessage(message);
/* 897 */     UT otherUnknowns = schema.getFromMessage(other);
/* 898 */     UT merged = schema.merge(messageUnknowns, otherUnknowns);
/* 899 */     schema.setToMessage(message, merged);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static <UT, UB> UB filterUnknownEnumList(int number, List<Integer> enumList, Internal.EnumLiteMap<?> enumMap, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema) {
/* 909 */     if (enumMap == null) {
/* 910 */       return unknownFields;
/*     */     }
/*     */     
/* 913 */     if (enumList instanceof java.util.RandomAccess) {
/* 914 */       int writePos = 0;
/* 915 */       int size = enumList.size();
/* 916 */       for (int readPos = 0; readPos < size; readPos++) {
/* 917 */         int enumValue = ((Integer)enumList.get(readPos)).intValue();
/* 918 */         if (enumMap.findValueByNumber(enumValue) != null) {
/* 919 */           if (readPos != writePos) {
/* 920 */             enumList.set(writePos, Integer.valueOf(enumValue));
/*     */           }
/* 922 */           writePos++;
/*     */         } else {
/* 924 */           unknownFields = storeUnknownEnum(number, enumValue, unknownFields, unknownFieldSchema);
/*     */         } 
/*     */       } 
/* 927 */       if (writePos != size) {
/* 928 */         enumList.subList(writePos, size).clear();
/*     */       }
/*     */     } else {
/* 931 */       for (Iterator<Integer> it = enumList.iterator(); it.hasNext(); ) {
/* 932 */         int enumValue = ((Integer)it.next()).intValue();
/* 933 */         if (enumMap.findValueByNumber(enumValue) == null) {
/* 934 */           unknownFields = storeUnknownEnum(number, enumValue, unknownFields, unknownFieldSchema);
/* 935 */           it.remove();
/*     */         } 
/*     */       } 
/*     */     } 
/* 939 */     return unknownFields;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static <UT, UB> UB filterUnknownEnumList(int number, List<Integer> enumList, Internal.EnumVerifier enumVerifier, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema) {
/* 949 */     if (enumVerifier == null) {
/* 950 */       return unknownFields;
/*     */     }
/*     */     
/* 953 */     if (enumList instanceof java.util.RandomAccess) {
/* 954 */       int writePos = 0;
/* 955 */       int size = enumList.size();
/* 956 */       for (int readPos = 0; readPos < size; readPos++) {
/* 957 */         int enumValue = ((Integer)enumList.get(readPos)).intValue();
/* 958 */         if (enumVerifier.isInRange(enumValue)) {
/* 959 */           if (readPos != writePos) {
/* 960 */             enumList.set(writePos, Integer.valueOf(enumValue));
/*     */           }
/* 962 */           writePos++;
/*     */         } else {
/* 964 */           unknownFields = storeUnknownEnum(number, enumValue, unknownFields, unknownFieldSchema);
/*     */         } 
/*     */       } 
/* 967 */       if (writePos != size) {
/* 968 */         enumList.subList(writePos, size).clear();
/*     */       }
/*     */     } else {
/* 971 */       for (Iterator<Integer> it = enumList.iterator(); it.hasNext(); ) {
/* 972 */         int enumValue = ((Integer)it.next()).intValue();
/* 973 */         if (!enumVerifier.isInRange(enumValue)) {
/* 974 */           unknownFields = storeUnknownEnum(number, enumValue, unknownFields, unknownFieldSchema);
/* 975 */           it.remove();
/*     */         } 
/*     */       } 
/*     */     } 
/* 979 */     return unknownFields;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static <UT, UB> UB storeUnknownEnum(int number, int enumValue, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema) {
/* 985 */     if (unknownFields == null) {
/* 986 */       unknownFields = unknownFieldSchema.newBuilder();
/*     */     }
/* 988 */     unknownFieldSchema.addVarint(unknownFields, number, enumValue);
/* 989 */     return unknownFields;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\SchemaUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */