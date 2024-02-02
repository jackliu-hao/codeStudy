/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ final class CodedOutputStreamWriter
/*     */   implements Writer
/*     */ {
/*     */   private final CodedOutputStream output;
/*     */   
/*     */   public static CodedOutputStreamWriter forCodedOutput(CodedOutputStream output) {
/*  47 */     if (output.wrapper != null) {
/*  48 */       return output.wrapper;
/*     */     }
/*  50 */     return new CodedOutputStreamWriter(output);
/*     */   }
/*     */   
/*     */   private CodedOutputStreamWriter(CodedOutputStream output) {
/*  54 */     this.output = Internal.<CodedOutputStream>checkNotNull(output, "output");
/*  55 */     this.output.wrapper = this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Writer.FieldOrder fieldOrder() {
/*  60 */     return Writer.FieldOrder.ASCENDING;
/*     */   }
/*     */   
/*     */   public int getTotalBytesWritten() {
/*  64 */     return this.output.getTotalBytesWritten();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeSFixed32(int fieldNumber, int value) throws IOException {
/*  69 */     this.output.writeSFixed32(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeInt64(int fieldNumber, long value) throws IOException {
/*  74 */     this.output.writeInt64(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeSFixed64(int fieldNumber, long value) throws IOException {
/*  79 */     this.output.writeSFixed64(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeFloat(int fieldNumber, float value) throws IOException {
/*  84 */     this.output.writeFloat(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeDouble(int fieldNumber, double value) throws IOException {
/*  89 */     this.output.writeDouble(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeEnum(int fieldNumber, int value) throws IOException {
/*  94 */     this.output.writeEnum(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeUInt64(int fieldNumber, long value) throws IOException {
/*  99 */     this.output.writeUInt64(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeInt32(int fieldNumber, int value) throws IOException {
/* 104 */     this.output.writeInt32(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeFixed64(int fieldNumber, long value) throws IOException {
/* 109 */     this.output.writeFixed64(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeFixed32(int fieldNumber, int value) throws IOException {
/* 114 */     this.output.writeFixed32(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeBool(int fieldNumber, boolean value) throws IOException {
/* 119 */     this.output.writeBool(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeString(int fieldNumber, String value) throws IOException {
/* 124 */     this.output.writeString(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeBytes(int fieldNumber, ByteString value) throws IOException {
/* 129 */     this.output.writeBytes(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeUInt32(int fieldNumber, int value) throws IOException {
/* 134 */     this.output.writeUInt32(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeSInt32(int fieldNumber, int value) throws IOException {
/* 139 */     this.output.writeSInt32(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeSInt64(int fieldNumber, long value) throws IOException {
/* 144 */     this.output.writeSInt64(fieldNumber, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeMessage(int fieldNumber, Object value) throws IOException {
/* 149 */     this.output.writeMessage(fieldNumber, (MessageLite)value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeMessage(int fieldNumber, Object value, Schema schema) throws IOException {
/* 154 */     this.output.writeMessage(fieldNumber, (MessageLite)value, schema);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeGroup(int fieldNumber, Object value) throws IOException {
/* 159 */     this.output.writeGroup(fieldNumber, (MessageLite)value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeGroup(int fieldNumber, Object value, Schema schema) throws IOException {
/* 164 */     this.output.writeGroup(fieldNumber, (MessageLite)value, schema);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeStartGroup(int fieldNumber) throws IOException {
/* 169 */     this.output.writeTag(fieldNumber, 3);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeEndGroup(int fieldNumber) throws IOException {
/* 174 */     this.output.writeTag(fieldNumber, 4);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void writeMessageSetItem(int fieldNumber, Object value) throws IOException {
/* 179 */     if (value instanceof ByteString) {
/* 180 */       this.output.writeRawMessageSetExtension(fieldNumber, (ByteString)value);
/*     */     } else {
/* 182 */       this.output.writeMessageSetExtension(fieldNumber, (MessageLite)value);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeInt32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
/* 189 */     if (packed) {
/* 190 */       this.output.writeTag(fieldNumber, 2);
/*     */ 
/*     */       
/* 193 */       int dataSize = 0; int i;
/* 194 */       for (i = 0; i < value.size(); i++) {
/* 195 */         dataSize += CodedOutputStream.computeInt32SizeNoTag(((Integer)value.get(i)).intValue());
/*     */       }
/* 197 */       this.output.writeUInt32NoTag(dataSize);
/*     */ 
/*     */       
/* 200 */       for (i = 0; i < value.size(); i++) {
/* 201 */         this.output.writeInt32NoTag(((Integer)value.get(i)).intValue());
/*     */       }
/*     */     } else {
/* 204 */       for (int i = 0; i < value.size(); i++) {
/* 205 */         this.output.writeInt32(fieldNumber, ((Integer)value.get(i)).intValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeFixed32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
/* 213 */     if (packed) {
/* 214 */       this.output.writeTag(fieldNumber, 2);
/*     */ 
/*     */       
/* 217 */       int dataSize = 0; int i;
/* 218 */       for (i = 0; i < value.size(); i++) {
/* 219 */         dataSize += CodedOutputStream.computeFixed32SizeNoTag(((Integer)value.get(i)).intValue());
/*     */       }
/* 221 */       this.output.writeUInt32NoTag(dataSize);
/*     */ 
/*     */       
/* 224 */       for (i = 0; i < value.size(); i++) {
/* 225 */         this.output.writeFixed32NoTag(((Integer)value.get(i)).intValue());
/*     */       }
/*     */     } else {
/* 228 */       for (int i = 0; i < value.size(); i++) {
/* 229 */         this.output.writeFixed32(fieldNumber, ((Integer)value.get(i)).intValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeInt64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
/* 236 */     if (packed) {
/* 237 */       this.output.writeTag(fieldNumber, 2);
/*     */ 
/*     */       
/* 240 */       int dataSize = 0; int i;
/* 241 */       for (i = 0; i < value.size(); i++) {
/* 242 */         dataSize += CodedOutputStream.computeInt64SizeNoTag(((Long)value.get(i)).longValue());
/*     */       }
/* 244 */       this.output.writeUInt32NoTag(dataSize);
/*     */ 
/*     */       
/* 247 */       for (i = 0; i < value.size(); i++) {
/* 248 */         this.output.writeInt64NoTag(((Long)value.get(i)).longValue());
/*     */       }
/*     */     } else {
/* 251 */       for (int i = 0; i < value.size(); i++) {
/* 252 */         this.output.writeInt64(fieldNumber, ((Long)value.get(i)).longValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeUInt64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
/* 260 */     if (packed) {
/* 261 */       this.output.writeTag(fieldNumber, 2);
/*     */ 
/*     */       
/* 264 */       int dataSize = 0; int i;
/* 265 */       for (i = 0; i < value.size(); i++) {
/* 266 */         dataSize += CodedOutputStream.computeUInt64SizeNoTag(((Long)value.get(i)).longValue());
/*     */       }
/* 268 */       this.output.writeUInt32NoTag(dataSize);
/*     */ 
/*     */       
/* 271 */       for (i = 0; i < value.size(); i++) {
/* 272 */         this.output.writeUInt64NoTag(((Long)value.get(i)).longValue());
/*     */       }
/*     */     } else {
/* 275 */       for (int i = 0; i < value.size(); i++) {
/* 276 */         this.output.writeUInt64(fieldNumber, ((Long)value.get(i)).longValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeFixed64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
/* 284 */     if (packed) {
/* 285 */       this.output.writeTag(fieldNumber, 2);
/*     */ 
/*     */       
/* 288 */       int dataSize = 0; int i;
/* 289 */       for (i = 0; i < value.size(); i++) {
/* 290 */         dataSize += CodedOutputStream.computeFixed64SizeNoTag(((Long)value.get(i)).longValue());
/*     */       }
/* 292 */       this.output.writeUInt32NoTag(dataSize);
/*     */ 
/*     */       
/* 295 */       for (i = 0; i < value.size(); i++) {
/* 296 */         this.output.writeFixed64NoTag(((Long)value.get(i)).longValue());
/*     */       }
/*     */     } else {
/* 299 */       for (int i = 0; i < value.size(); i++) {
/* 300 */         this.output.writeFixed64(fieldNumber, ((Long)value.get(i)).longValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeFloatList(int fieldNumber, List<Float> value, boolean packed) throws IOException {
/* 308 */     if (packed) {
/* 309 */       this.output.writeTag(fieldNumber, 2);
/*     */ 
/*     */       
/* 312 */       int dataSize = 0; int i;
/* 313 */       for (i = 0; i < value.size(); i++) {
/* 314 */         dataSize += CodedOutputStream.computeFloatSizeNoTag(((Float)value.get(i)).floatValue());
/*     */       }
/* 316 */       this.output.writeUInt32NoTag(dataSize);
/*     */ 
/*     */       
/* 319 */       for (i = 0; i < value.size(); i++) {
/* 320 */         this.output.writeFloatNoTag(((Float)value.get(i)).floatValue());
/*     */       }
/*     */     } else {
/* 323 */       for (int i = 0; i < value.size(); i++) {
/* 324 */         this.output.writeFloat(fieldNumber, ((Float)value.get(i)).floatValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeDoubleList(int fieldNumber, List<Double> value, boolean packed) throws IOException {
/* 332 */     if (packed) {
/* 333 */       this.output.writeTag(fieldNumber, 2);
/*     */ 
/*     */       
/* 336 */       int dataSize = 0; int i;
/* 337 */       for (i = 0; i < value.size(); i++) {
/* 338 */         dataSize += CodedOutputStream.computeDoubleSizeNoTag(((Double)value.get(i)).doubleValue());
/*     */       }
/* 340 */       this.output.writeUInt32NoTag(dataSize);
/*     */ 
/*     */       
/* 343 */       for (i = 0; i < value.size(); i++) {
/* 344 */         this.output.writeDoubleNoTag(((Double)value.get(i)).doubleValue());
/*     */       }
/*     */     } else {
/* 347 */       for (int i = 0; i < value.size(); i++) {
/* 348 */         this.output.writeDouble(fieldNumber, ((Double)value.get(i)).doubleValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeEnumList(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
/* 356 */     if (packed) {
/* 357 */       this.output.writeTag(fieldNumber, 2);
/*     */ 
/*     */       
/* 360 */       int dataSize = 0; int i;
/* 361 */       for (i = 0; i < value.size(); i++) {
/* 362 */         dataSize += CodedOutputStream.computeEnumSizeNoTag(((Integer)value.get(i)).intValue());
/*     */       }
/* 364 */       this.output.writeUInt32NoTag(dataSize);
/*     */ 
/*     */       
/* 367 */       for (i = 0; i < value.size(); i++) {
/* 368 */         this.output.writeEnumNoTag(((Integer)value.get(i)).intValue());
/*     */       }
/*     */     } else {
/* 371 */       for (int i = 0; i < value.size(); i++) {
/* 372 */         this.output.writeEnum(fieldNumber, ((Integer)value.get(i)).intValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeBoolList(int fieldNumber, List<Boolean> value, boolean packed) throws IOException {
/* 380 */     if (packed) {
/* 381 */       this.output.writeTag(fieldNumber, 2);
/*     */ 
/*     */       
/* 384 */       int dataSize = 0; int i;
/* 385 */       for (i = 0; i < value.size(); i++) {
/* 386 */         dataSize += CodedOutputStream.computeBoolSizeNoTag(((Boolean)value.get(i)).booleanValue());
/*     */       }
/* 388 */       this.output.writeUInt32NoTag(dataSize);
/*     */ 
/*     */       
/* 391 */       for (i = 0; i < value.size(); i++) {
/* 392 */         this.output.writeBoolNoTag(((Boolean)value.get(i)).booleanValue());
/*     */       }
/*     */     } else {
/* 395 */       for (int i = 0; i < value.size(); i++) {
/* 396 */         this.output.writeBool(fieldNumber, ((Boolean)value.get(i)).booleanValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeStringList(int fieldNumber, List<String> value) throws IOException {
/* 403 */     if (value instanceof LazyStringList) {
/* 404 */       LazyStringList lazyList = (LazyStringList)value;
/* 405 */       for (int i = 0; i < value.size(); i++) {
/* 406 */         writeLazyString(fieldNumber, lazyList.getRaw(i));
/*     */       }
/*     */     } else {
/* 409 */       for (int i = 0; i < value.size(); i++) {
/* 410 */         this.output.writeString(fieldNumber, value.get(i));
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeLazyString(int fieldNumber, Object value) throws IOException {
/* 416 */     if (value instanceof String) {
/* 417 */       this.output.writeString(fieldNumber, (String)value);
/*     */     } else {
/* 419 */       this.output.writeBytes(fieldNumber, (ByteString)value);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeBytesList(int fieldNumber, List<ByteString> value) throws IOException {
/* 425 */     for (int i = 0; i < value.size(); i++) {
/* 426 */       this.output.writeBytes(fieldNumber, value.get(i));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeUInt32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
/* 433 */     if (packed) {
/* 434 */       this.output.writeTag(fieldNumber, 2);
/*     */ 
/*     */       
/* 437 */       int dataSize = 0; int i;
/* 438 */       for (i = 0; i < value.size(); i++) {
/* 439 */         dataSize += CodedOutputStream.computeUInt32SizeNoTag(((Integer)value.get(i)).intValue());
/*     */       }
/* 441 */       this.output.writeUInt32NoTag(dataSize);
/*     */ 
/*     */       
/* 444 */       for (i = 0; i < value.size(); i++) {
/* 445 */         this.output.writeUInt32NoTag(((Integer)value.get(i)).intValue());
/*     */       }
/*     */     } else {
/* 448 */       for (int i = 0; i < value.size(); i++) {
/* 449 */         this.output.writeUInt32(fieldNumber, ((Integer)value.get(i)).intValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeSFixed32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
/* 457 */     if (packed) {
/* 458 */       this.output.writeTag(fieldNumber, 2);
/*     */ 
/*     */       
/* 461 */       int dataSize = 0; int i;
/* 462 */       for (i = 0; i < value.size(); i++) {
/* 463 */         dataSize += CodedOutputStream.computeSFixed32SizeNoTag(((Integer)value.get(i)).intValue());
/*     */       }
/* 465 */       this.output.writeUInt32NoTag(dataSize);
/*     */ 
/*     */       
/* 468 */       for (i = 0; i < value.size(); i++) {
/* 469 */         this.output.writeSFixed32NoTag(((Integer)value.get(i)).intValue());
/*     */       }
/*     */     } else {
/* 472 */       for (int i = 0; i < value.size(); i++) {
/* 473 */         this.output.writeSFixed32(fieldNumber, ((Integer)value.get(i)).intValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeSFixed64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
/* 481 */     if (packed) {
/* 482 */       this.output.writeTag(fieldNumber, 2);
/*     */ 
/*     */       
/* 485 */       int dataSize = 0; int i;
/* 486 */       for (i = 0; i < value.size(); i++) {
/* 487 */         dataSize += CodedOutputStream.computeSFixed64SizeNoTag(((Long)value.get(i)).longValue());
/*     */       }
/* 489 */       this.output.writeUInt32NoTag(dataSize);
/*     */ 
/*     */       
/* 492 */       for (i = 0; i < value.size(); i++) {
/* 493 */         this.output.writeSFixed64NoTag(((Long)value.get(i)).longValue());
/*     */       }
/*     */     } else {
/* 496 */       for (int i = 0; i < value.size(); i++) {
/* 497 */         this.output.writeSFixed64(fieldNumber, ((Long)value.get(i)).longValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeSInt32List(int fieldNumber, List<Integer> value, boolean packed) throws IOException {
/* 505 */     if (packed) {
/* 506 */       this.output.writeTag(fieldNumber, 2);
/*     */ 
/*     */       
/* 509 */       int dataSize = 0; int i;
/* 510 */       for (i = 0; i < value.size(); i++) {
/* 511 */         dataSize += CodedOutputStream.computeSInt32SizeNoTag(((Integer)value.get(i)).intValue());
/*     */       }
/* 513 */       this.output.writeUInt32NoTag(dataSize);
/*     */ 
/*     */       
/* 516 */       for (i = 0; i < value.size(); i++) {
/* 517 */         this.output.writeSInt32NoTag(((Integer)value.get(i)).intValue());
/*     */       }
/*     */     } else {
/* 520 */       for (int i = 0; i < value.size(); i++) {
/* 521 */         this.output.writeSInt32(fieldNumber, ((Integer)value.get(i)).intValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeSInt64List(int fieldNumber, List<Long> value, boolean packed) throws IOException {
/* 529 */     if (packed) {
/* 530 */       this.output.writeTag(fieldNumber, 2);
/*     */ 
/*     */       
/* 533 */       int dataSize = 0; int i;
/* 534 */       for (i = 0; i < value.size(); i++) {
/* 535 */         dataSize += CodedOutputStream.computeSInt64SizeNoTag(((Long)value.get(i)).longValue());
/*     */       }
/* 537 */       this.output.writeUInt32NoTag(dataSize);
/*     */ 
/*     */       
/* 540 */       for (i = 0; i < value.size(); i++) {
/* 541 */         this.output.writeSInt64NoTag(((Long)value.get(i)).longValue());
/*     */       }
/*     */     } else {
/* 544 */       for (int i = 0; i < value.size(); i++) {
/* 545 */         this.output.writeSInt64(fieldNumber, ((Long)value.get(i)).longValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeMessageList(int fieldNumber, List<?> value) throws IOException {
/* 552 */     for (int i = 0; i < value.size(); i++) {
/* 553 */       writeMessage(fieldNumber, value.get(i));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeMessageList(int fieldNumber, List<?> value, Schema schema) throws IOException {
/* 559 */     for (int i = 0; i < value.size(); i++) {
/* 560 */       writeMessage(fieldNumber, value.get(i), schema);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeGroupList(int fieldNumber, List<?> value) throws IOException {
/* 566 */     for (int i = 0; i < value.size(); i++) {
/* 567 */       writeGroup(fieldNumber, value.get(i));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeGroupList(int fieldNumber, List<?> value, Schema schema) throws IOException {
/* 573 */     for (int i = 0; i < value.size(); i++) {
/* 574 */       writeGroup(fieldNumber, value.get(i), schema);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <K, V> void writeMap(int fieldNumber, MapEntryLite.Metadata<K, V> metadata, Map<K, V> map) throws IOException {
/* 581 */     if (this.output.isSerializationDeterministic()) {
/* 582 */       writeDeterministicMap(fieldNumber, metadata, map);
/*     */       return;
/*     */     } 
/* 585 */     for (Map.Entry<K, V> entry : map.entrySet()) {
/* 586 */       this.output.writeTag(fieldNumber, 2);
/* 587 */       this.output.writeUInt32NoTag(
/* 588 */           MapEntryLite.computeSerializedSize(metadata, entry.getKey(), entry.getValue()));
/* 589 */       MapEntryLite.writeTo(this.output, metadata, entry.getKey(), entry.getValue());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private <K, V> void writeDeterministicMap(int fieldNumber, MapEntryLite.Metadata<K, V> metadata, Map<K, V> map) throws IOException {
/*     */     V value;
/* 596 */     switch (metadata.keyType) {
/*     */       
/*     */       case BOOL:
/* 599 */         if ((value = map.get(Boolean.FALSE)) != null) {
/* 600 */           writeDeterministicBooleanMapEntry(fieldNumber, false, value, (MapEntryLite.Metadata)metadata);
/*     */         }
/*     */         
/* 603 */         if ((value = map.get(Boolean.TRUE)) != null) {
/* 604 */           writeDeterministicBooleanMapEntry(fieldNumber, true, value, (MapEntryLite.Metadata)metadata);
/*     */         }
/*     */         return;
/*     */       
/*     */       case FIXED32:
/*     */       case INT32:
/*     */       case SFIXED32:
/*     */       case SINT32:
/*     */       case UINT32:
/* 613 */         writeDeterministicIntegerMap(fieldNumber, (MapEntryLite.Metadata)metadata, (Map)map);
/*     */         return;
/*     */       
/*     */       case FIXED64:
/*     */       case INT64:
/*     */       case SFIXED64:
/*     */       case SINT64:
/*     */       case UINT64:
/* 621 */         writeDeterministicLongMap(fieldNumber, (MapEntryLite.Metadata)metadata, (Map)map);
/*     */         return;
/*     */       
/*     */       case STRING:
/* 625 */         writeDeterministicStringMap(fieldNumber, (MapEntryLite.Metadata)metadata, (Map)map);
/*     */         return;
/*     */     } 
/*     */     
/* 629 */     throw new IllegalArgumentException("does not support key type: " + metadata.keyType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private <V> void writeDeterministicBooleanMapEntry(int fieldNumber, boolean key, V value, MapEntryLite.Metadata<Boolean, V> metadata) throws IOException {
/* 636 */     this.output.writeTag(fieldNumber, 2);
/* 637 */     this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, Boolean.valueOf(key), value));
/* 638 */     MapEntryLite.writeTo(this.output, metadata, Boolean.valueOf(key), value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private <V> void writeDeterministicIntegerMap(int fieldNumber, MapEntryLite.Metadata<Integer, V> metadata, Map<Integer, V> map) throws IOException {
/* 644 */     int[] keys = new int[map.size()];
/* 645 */     int index = 0;
/* 646 */     for (Iterator<Integer> iterator = map.keySet().iterator(); iterator.hasNext(); ) { int k = ((Integer)iterator.next()).intValue();
/* 647 */       keys[index++] = k; }
/*     */     
/* 649 */     Arrays.sort(keys);
/* 650 */     for (int key : keys) {
/* 651 */       V value = map.get(Integer.valueOf(key));
/* 652 */       this.output.writeTag(fieldNumber, 2);
/* 653 */       this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, Integer.valueOf(key), value));
/* 654 */       MapEntryLite.writeTo(this.output, metadata, Integer.valueOf(key), value);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private <V> void writeDeterministicLongMap(int fieldNumber, MapEntryLite.Metadata<Long, V> metadata, Map<Long, V> map) throws IOException {
/* 661 */     long[] keys = new long[map.size()];
/* 662 */     int index = 0;
/* 663 */     for (Iterator<Long> iterator = map.keySet().iterator(); iterator.hasNext(); ) { long k = ((Long)iterator.next()).longValue();
/* 664 */       keys[index++] = k; }
/*     */     
/* 666 */     Arrays.sort(keys);
/* 667 */     for (long key : keys) {
/* 668 */       V value = map.get(Long.valueOf(key));
/* 669 */       this.output.writeTag(fieldNumber, 2);
/* 670 */       this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, Long.valueOf(key), value));
/* 671 */       MapEntryLite.writeTo(this.output, metadata, Long.valueOf(key), value);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private <V> void writeDeterministicStringMap(int fieldNumber, MapEntryLite.Metadata<String, V> metadata, Map<String, V> map) throws IOException {
/* 678 */     String[] keys = new String[map.size()];
/* 679 */     int index = 0;
/* 680 */     for (String k : map.keySet()) {
/* 681 */       keys[index++] = k;
/*     */     }
/* 683 */     Arrays.sort((Object[])keys);
/* 684 */     for (String key : keys) {
/* 685 */       V value = map.get(key);
/* 686 */       this.output.writeTag(fieldNumber, 2);
/* 687 */       this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(metadata, key, value));
/* 688 */       MapEntryLite.writeTo(this.output, metadata, key, value);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\CodedOutputStreamWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */