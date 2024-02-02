/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.ByteOrder;
/*      */ import java.util.ArrayDeque;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Queue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ abstract class BinaryWriter
/*      */   extends ByteOutput
/*      */   implements Writer
/*      */ {
/*      */   public static final int DEFAULT_CHUNK_SIZE = 4096;
/*      */   private final BufferAllocator alloc;
/*      */   private final int chunkSize;
/*   75 */   final ArrayDeque<AllocatedBuffer> buffers = new ArrayDeque<>(4);
/*      */   
/*      */   int totalDoneBytes;
/*      */   
/*      */   private static final int MAP_KEY_NUMBER = 1;
/*      */   private static final int MAP_VALUE_NUMBER = 2;
/*      */   
/*      */   public static BinaryWriter newHeapInstance(BufferAllocator alloc) {
/*   83 */     return newHeapInstance(alloc, 4096);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BinaryWriter newHeapInstance(BufferAllocator alloc, int chunkSize) {
/*   91 */     return isUnsafeHeapSupported() ? 
/*   92 */       newUnsafeHeapInstance(alloc, chunkSize) : 
/*   93 */       newSafeHeapInstance(alloc, chunkSize);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BinaryWriter newDirectInstance(BufferAllocator alloc) {
/*  101 */     return newDirectInstance(alloc, 4096);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BinaryWriter newDirectInstance(BufferAllocator alloc, int chunkSize) {
/*  109 */     return isUnsafeDirectSupported() ? 
/*  110 */       newUnsafeDirectInstance(alloc, chunkSize) : 
/*  111 */       newSafeDirectInstance(alloc, chunkSize);
/*      */   }
/*      */   
/*      */   static boolean isUnsafeHeapSupported() {
/*  115 */     return UnsafeHeapWriter.isSupported();
/*      */   }
/*      */   
/*      */   static boolean isUnsafeDirectSupported() {
/*  119 */     return UnsafeDirectWriter.isSupported();
/*      */   }
/*      */   
/*      */   static BinaryWriter newSafeHeapInstance(BufferAllocator alloc, int chunkSize) {
/*  123 */     return new SafeHeapWriter(alloc, chunkSize);
/*      */   }
/*      */   
/*      */   static BinaryWriter newUnsafeHeapInstance(BufferAllocator alloc, int chunkSize) {
/*  127 */     if (!isUnsafeHeapSupported()) {
/*  128 */       throw new UnsupportedOperationException("Unsafe operations not supported");
/*      */     }
/*  130 */     return new UnsafeHeapWriter(alloc, chunkSize);
/*      */   }
/*      */   
/*      */   static BinaryWriter newSafeDirectInstance(BufferAllocator alloc, int chunkSize) {
/*  134 */     return new SafeDirectWriter(alloc, chunkSize);
/*      */   }
/*      */   
/*      */   static BinaryWriter newUnsafeDirectInstance(BufferAllocator alloc, int chunkSize) {
/*  138 */     if (!isUnsafeDirectSupported()) {
/*  139 */       throw new UnsupportedOperationException("Unsafe operations not supported");
/*      */     }
/*  141 */     return new UnsafeDirectWriter(alloc, chunkSize);
/*      */   }
/*      */ 
/*      */   
/*      */   private BinaryWriter(BufferAllocator alloc, int chunkSize) {
/*  146 */     if (chunkSize <= 0) {
/*  147 */       throw new IllegalArgumentException("chunkSize must be > 0");
/*      */     }
/*  149 */     this.alloc = Internal.<BufferAllocator>checkNotNull(alloc, "alloc");
/*  150 */     this.chunkSize = chunkSize;
/*      */   }
/*      */ 
/*      */   
/*      */   public final Writer.FieldOrder fieldOrder() {
/*  155 */     return Writer.FieldOrder.DESCENDING;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Queue<AllocatedBuffer> complete() {
/*  166 */     finishCurrentBuffer();
/*  167 */     return this.buffers;
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeSFixed32(int fieldNumber, int value) throws IOException {
/*  172 */     writeFixed32(fieldNumber, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeInt64(int fieldNumber, long value) throws IOException {
/*  177 */     writeUInt64(fieldNumber, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeSFixed64(int fieldNumber, long value) throws IOException {
/*  182 */     writeFixed64(fieldNumber, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeFloat(int fieldNumber, float value) throws IOException {
/*  187 */     writeFixed32(fieldNumber, Float.floatToRawIntBits(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeDouble(int fieldNumber, double value) throws IOException {
/*  192 */     writeFixed64(fieldNumber, Double.doubleToRawLongBits(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeEnum(int fieldNumber, int value) throws IOException {
/*  197 */     writeInt32(fieldNumber, value);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeInt32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
/*  203 */     if (list instanceof IntArrayList) {
/*  204 */       writeInt32List_Internal(fieldNumber, (IntArrayList)list, packed);
/*      */     } else {
/*  206 */       writeInt32List_Internal(fieldNumber, list, packed);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeInt32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
/*  212 */     if (packed) {
/*  213 */       requireSpace(10 + list.size() * 10);
/*  214 */       int prevBytes = getTotalBytesWritten();
/*  215 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  216 */         writeInt32(((Integer)list.get(i)).intValue());
/*      */       }
/*  218 */       int length = getTotalBytesWritten() - prevBytes;
/*  219 */       writeVarint32(length);
/*  220 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  222 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  223 */         writeInt32(fieldNumber, ((Integer)list.get(i)).intValue());
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeInt32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
/*  230 */     if (packed) {
/*  231 */       requireSpace(10 + list.size() * 10);
/*  232 */       int prevBytes = getTotalBytesWritten();
/*  233 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  234 */         writeInt32(list.getInt(i));
/*      */       }
/*  236 */       int length = getTotalBytesWritten() - prevBytes;
/*  237 */       writeVarint32(length);
/*  238 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  240 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  241 */         writeInt32(fieldNumber, list.getInt(i));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeFixed32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
/*  249 */     if (list instanceof IntArrayList) {
/*  250 */       writeFixed32List_Internal(fieldNumber, (IntArrayList)list, packed);
/*      */     } else {
/*  252 */       writeFixed32List_Internal(fieldNumber, list, packed);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeFixed32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
/*  258 */     if (packed) {
/*  259 */       requireSpace(10 + list.size() * 4);
/*  260 */       int prevBytes = getTotalBytesWritten();
/*  261 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  262 */         writeFixed32(((Integer)list.get(i)).intValue());
/*      */       }
/*  264 */       int length = getTotalBytesWritten() - prevBytes;
/*  265 */       writeVarint32(length);
/*  266 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  268 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  269 */         writeFixed32(fieldNumber, ((Integer)list.get(i)).intValue());
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeFixed32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
/*  276 */     if (packed) {
/*  277 */       requireSpace(10 + list.size() * 4);
/*  278 */       int prevBytes = getTotalBytesWritten();
/*  279 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  280 */         writeFixed32(list.getInt(i));
/*      */       }
/*  282 */       int length = getTotalBytesWritten() - prevBytes;
/*  283 */       writeVarint32(length);
/*  284 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  286 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  287 */         writeFixed32(fieldNumber, list.getInt(i));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeInt64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
/*  295 */     writeUInt64List(fieldNumber, list, packed);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeUInt64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
/*  301 */     if (list instanceof LongArrayList) {
/*  302 */       writeUInt64List_Internal(fieldNumber, (LongArrayList)list, packed);
/*      */     } else {
/*  304 */       writeUInt64List_Internal(fieldNumber, list, packed);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeUInt64List_Internal(int fieldNumber, List<Long> list, boolean packed) throws IOException {
/*  310 */     if (packed) {
/*  311 */       requireSpace(10 + list.size() * 10);
/*  312 */       int prevBytes = getTotalBytesWritten();
/*  313 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  314 */         writeVarint64(((Long)list.get(i)).longValue());
/*      */       }
/*  316 */       int length = getTotalBytesWritten() - prevBytes;
/*  317 */       writeVarint32(length);
/*  318 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  320 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  321 */         writeUInt64(fieldNumber, ((Long)list.get(i)).longValue());
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeUInt64List_Internal(int fieldNumber, LongArrayList list, boolean packed) throws IOException {
/*  328 */     if (packed) {
/*  329 */       requireSpace(10 + list.size() * 10);
/*  330 */       int prevBytes = getTotalBytesWritten();
/*  331 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  332 */         writeVarint64(list.getLong(i));
/*      */       }
/*  334 */       int length = getTotalBytesWritten() - prevBytes;
/*  335 */       writeVarint32(length);
/*  336 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  338 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  339 */         writeUInt64(fieldNumber, list.getLong(i));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeFixed64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
/*  347 */     if (list instanceof LongArrayList) {
/*  348 */       writeFixed64List_Internal(fieldNumber, (LongArrayList)list, packed);
/*      */     } else {
/*  350 */       writeFixed64List_Internal(fieldNumber, list, packed);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeFixed64List_Internal(int fieldNumber, List<Long> list, boolean packed) throws IOException {
/*  356 */     if (packed) {
/*  357 */       requireSpace(10 + list.size() * 8);
/*  358 */       int prevBytes = getTotalBytesWritten();
/*  359 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  360 */         writeFixed64(((Long)list.get(i)).longValue());
/*      */       }
/*  362 */       int length = getTotalBytesWritten() - prevBytes;
/*  363 */       writeVarint32(length);
/*  364 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  366 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  367 */         writeFixed64(fieldNumber, ((Long)list.get(i)).longValue());
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeFixed64List_Internal(int fieldNumber, LongArrayList list, boolean packed) throws IOException {
/*  374 */     if (packed) {
/*  375 */       requireSpace(10 + list.size() * 8);
/*  376 */       int prevBytes = getTotalBytesWritten();
/*  377 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  378 */         writeFixed64(list.getLong(i));
/*      */       }
/*  380 */       int length = getTotalBytesWritten() - prevBytes;
/*  381 */       writeVarint32(length);
/*  382 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  384 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  385 */         writeFixed64(fieldNumber, list.getLong(i));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeFloatList(int fieldNumber, List<Float> list, boolean packed) throws IOException {
/*  393 */     if (list instanceof FloatArrayList) {
/*  394 */       writeFloatList_Internal(fieldNumber, (FloatArrayList)list, packed);
/*      */     } else {
/*  396 */       writeFloatList_Internal(fieldNumber, list, packed);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeFloatList_Internal(int fieldNumber, List<Float> list, boolean packed) throws IOException {
/*  402 */     if (packed) {
/*  403 */       requireSpace(10 + list.size() * 4);
/*  404 */       int prevBytes = getTotalBytesWritten();
/*  405 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  406 */         writeFixed32(Float.floatToRawIntBits(((Float)list.get(i)).floatValue()));
/*      */       }
/*  408 */       int length = getTotalBytesWritten() - prevBytes;
/*  409 */       writeVarint32(length);
/*  410 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  412 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  413 */         writeFloat(fieldNumber, ((Float)list.get(i)).floatValue());
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeFloatList_Internal(int fieldNumber, FloatArrayList list, boolean packed) throws IOException {
/*  420 */     if (packed) {
/*  421 */       requireSpace(10 + list.size() * 4);
/*  422 */       int prevBytes = getTotalBytesWritten();
/*  423 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  424 */         writeFixed32(Float.floatToRawIntBits(list.getFloat(i)));
/*      */       }
/*  426 */       int length = getTotalBytesWritten() - prevBytes;
/*  427 */       writeVarint32(length);
/*  428 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  430 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  431 */         writeFloat(fieldNumber, list.getFloat(i));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeDoubleList(int fieldNumber, List<Double> list, boolean packed) throws IOException {
/*  439 */     if (list instanceof DoubleArrayList) {
/*  440 */       writeDoubleList_Internal(fieldNumber, (DoubleArrayList)list, packed);
/*      */     } else {
/*  442 */       writeDoubleList_Internal(fieldNumber, list, packed);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeDoubleList_Internal(int fieldNumber, List<Double> list, boolean packed) throws IOException {
/*  448 */     if (packed) {
/*  449 */       requireSpace(10 + list.size() * 8);
/*  450 */       int prevBytes = getTotalBytesWritten();
/*  451 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  452 */         writeFixed64(Double.doubleToRawLongBits(((Double)list.get(i)).doubleValue()));
/*      */       }
/*  454 */       int length = getTotalBytesWritten() - prevBytes;
/*  455 */       writeVarint32(length);
/*  456 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  458 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  459 */         writeDouble(fieldNumber, ((Double)list.get(i)).doubleValue());
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeDoubleList_Internal(int fieldNumber, DoubleArrayList list, boolean packed) throws IOException {
/*  466 */     if (packed) {
/*  467 */       requireSpace(10 + list.size() * 8);
/*  468 */       int prevBytes = getTotalBytesWritten();
/*  469 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  470 */         writeFixed64(Double.doubleToRawLongBits(list.getDouble(i)));
/*      */       }
/*  472 */       int length = getTotalBytesWritten() - prevBytes;
/*  473 */       writeVarint32(length);
/*  474 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  476 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  477 */         writeDouble(fieldNumber, list.getDouble(i));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeEnumList(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
/*  485 */     writeInt32List(fieldNumber, list, packed);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeBoolList(int fieldNumber, List<Boolean> list, boolean packed) throws IOException {
/*  491 */     if (list instanceof BooleanArrayList) {
/*  492 */       writeBoolList_Internal(fieldNumber, (BooleanArrayList)list, packed);
/*      */     } else {
/*  494 */       writeBoolList_Internal(fieldNumber, list, packed);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeBoolList_Internal(int fieldNumber, List<Boolean> list, boolean packed) throws IOException {
/*  500 */     if (packed) {
/*  501 */       requireSpace(10 + list.size());
/*  502 */       int prevBytes = getTotalBytesWritten();
/*  503 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  504 */         writeBool(((Boolean)list.get(i)).booleanValue());
/*      */       }
/*  506 */       int length = getTotalBytesWritten() - prevBytes;
/*  507 */       writeVarint32(length);
/*  508 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  510 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  511 */         writeBool(fieldNumber, ((Boolean)list.get(i)).booleanValue());
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeBoolList_Internal(int fieldNumber, BooleanArrayList list, boolean packed) throws IOException {
/*  518 */     if (packed) {
/*  519 */       requireSpace(10 + list.size());
/*  520 */       int prevBytes = getTotalBytesWritten();
/*  521 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  522 */         writeBool(list.getBoolean(i));
/*      */       }
/*  524 */       int length = getTotalBytesWritten() - prevBytes;
/*  525 */       writeVarint32(length);
/*  526 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  528 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  529 */         writeBool(fieldNumber, list.getBoolean(i));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeStringList(int fieldNumber, List<String> list) throws IOException {
/*  536 */     if (list instanceof LazyStringList) {
/*  537 */       LazyStringList lazyList = (LazyStringList)list;
/*  538 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  539 */         writeLazyString(fieldNumber, lazyList.getRaw(i));
/*      */       }
/*      */     } else {
/*  542 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  543 */         writeString(fieldNumber, list.get(i));
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private void writeLazyString(int fieldNumber, Object value) throws IOException {
/*  549 */     if (value instanceof String) {
/*  550 */       writeString(fieldNumber, (String)value);
/*      */     } else {
/*  552 */       writeBytes(fieldNumber, (ByteString)value);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeBytesList(int fieldNumber, List<ByteString> list) throws IOException {
/*  558 */     for (int i = list.size() - 1; i >= 0; i--) {
/*  559 */       writeBytes(fieldNumber, list.get(i));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeUInt32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
/*  566 */     if (list instanceof IntArrayList) {
/*  567 */       writeUInt32List_Internal(fieldNumber, (IntArrayList)list, packed);
/*      */     } else {
/*  569 */       writeUInt32List_Internal(fieldNumber, list, packed);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeUInt32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
/*  575 */     if (packed) {
/*  576 */       requireSpace(10 + list.size() * 5);
/*  577 */       int prevBytes = getTotalBytesWritten();
/*  578 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  579 */         writeVarint32(((Integer)list.get(i)).intValue());
/*      */       }
/*  581 */       int length = getTotalBytesWritten() - prevBytes;
/*  582 */       writeVarint32(length);
/*  583 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  585 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  586 */         writeUInt32(fieldNumber, ((Integer)list.get(i)).intValue());
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeUInt32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
/*  593 */     if (packed) {
/*  594 */       requireSpace(10 + list.size() * 5);
/*  595 */       int prevBytes = getTotalBytesWritten();
/*  596 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  597 */         writeVarint32(list.getInt(i));
/*      */       }
/*  599 */       int length = getTotalBytesWritten() - prevBytes;
/*  600 */       writeVarint32(length);
/*  601 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  603 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  604 */         writeUInt32(fieldNumber, list.getInt(i));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeSFixed32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
/*  612 */     writeFixed32List(fieldNumber, list, packed);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeSFixed64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
/*  618 */     writeFixed64List(fieldNumber, list, packed);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeSInt32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
/*  624 */     if (list instanceof IntArrayList) {
/*  625 */       writeSInt32List_Internal(fieldNumber, (IntArrayList)list, packed);
/*      */     } else {
/*  627 */       writeSInt32List_Internal(fieldNumber, list, packed);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeSInt32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
/*  633 */     if (packed) {
/*  634 */       requireSpace(10 + list.size() * 5);
/*  635 */       int prevBytes = getTotalBytesWritten();
/*  636 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  637 */         writeSInt32(((Integer)list.get(i)).intValue());
/*      */       }
/*  639 */       int length = getTotalBytesWritten() - prevBytes;
/*  640 */       writeVarint32(length);
/*  641 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  643 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  644 */         writeSInt32(fieldNumber, ((Integer)list.get(i)).intValue());
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeSInt32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
/*  651 */     if (packed) {
/*  652 */       requireSpace(10 + list.size() * 5);
/*  653 */       int prevBytes = getTotalBytesWritten();
/*  654 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  655 */         writeSInt32(list.getInt(i));
/*      */       }
/*  657 */       int length = getTotalBytesWritten() - prevBytes;
/*  658 */       writeVarint32(length);
/*  659 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  661 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  662 */         writeSInt32(fieldNumber, list.getInt(i));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeSInt64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
/*  670 */     if (list instanceof LongArrayList) {
/*  671 */       writeSInt64List_Internal(fieldNumber, (LongArrayList)list, packed);
/*      */     } else {
/*  673 */       writeSInt64List_Internal(fieldNumber, list, packed);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <K, V> void writeMap(int fieldNumber, MapEntryLite.Metadata<K, V> metadata, Map<K, V> map) throws IOException {
/*  684 */     for (Map.Entry<K, V> entry : map.entrySet()) {
/*  685 */       int prevBytes = getTotalBytesWritten();
/*  686 */       writeMapEntryField(this, 2, metadata.valueType, entry.getValue());
/*  687 */       writeMapEntryField(this, 1, metadata.keyType, entry.getKey());
/*  688 */       int length = getTotalBytesWritten() - prevBytes;
/*  689 */       writeVarint32(length);
/*  690 */       writeTag(fieldNumber, 2);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static final void writeMapEntryField(Writer writer, int fieldNumber, WireFormat.FieldType fieldType, Object object) throws IOException {
/*  697 */     switch (fieldType) {
/*      */       case BOOL:
/*  699 */         writer.writeBool(fieldNumber, ((Boolean)object).booleanValue());
/*      */         return;
/*      */       case FIXED32:
/*  702 */         writer.writeFixed32(fieldNumber, ((Integer)object).intValue());
/*      */         return;
/*      */       case FIXED64:
/*  705 */         writer.writeFixed64(fieldNumber, ((Long)object).longValue());
/*      */         return;
/*      */       case INT32:
/*  708 */         writer.writeInt32(fieldNumber, ((Integer)object).intValue());
/*      */         return;
/*      */       case INT64:
/*  711 */         writer.writeInt64(fieldNumber, ((Long)object).longValue());
/*      */         return;
/*      */       case SFIXED32:
/*  714 */         writer.writeSFixed32(fieldNumber, ((Integer)object).intValue());
/*      */         return;
/*      */       case SFIXED64:
/*  717 */         writer.writeSFixed64(fieldNumber, ((Long)object).longValue());
/*      */         return;
/*      */       case SINT32:
/*  720 */         writer.writeSInt32(fieldNumber, ((Integer)object).intValue());
/*      */         return;
/*      */       case SINT64:
/*  723 */         writer.writeSInt64(fieldNumber, ((Long)object).longValue());
/*      */         return;
/*      */       case STRING:
/*  726 */         writer.writeString(fieldNumber, (String)object);
/*      */         return;
/*      */       case UINT32:
/*  729 */         writer.writeUInt32(fieldNumber, ((Integer)object).intValue());
/*      */         return;
/*      */       case UINT64:
/*  732 */         writer.writeUInt64(fieldNumber, ((Long)object).longValue());
/*      */         return;
/*      */       case FLOAT:
/*  735 */         writer.writeFloat(fieldNumber, ((Float)object).floatValue());
/*      */         return;
/*      */       case DOUBLE:
/*  738 */         writer.writeDouble(fieldNumber, ((Double)object).doubleValue());
/*      */         return;
/*      */       case MESSAGE:
/*  741 */         writer.writeMessage(fieldNumber, object);
/*      */         return;
/*      */       case BYTES:
/*  744 */         writer.writeBytes(fieldNumber, (ByteString)object);
/*      */         return;
/*      */       case ENUM:
/*  747 */         if (object instanceof Internal.EnumLite) {
/*  748 */           writer.writeEnum(fieldNumber, ((Internal.EnumLite)object).getNumber());
/*  749 */         } else if (object instanceof Integer) {
/*  750 */           writer.writeEnum(fieldNumber, ((Integer)object).intValue());
/*      */         } else {
/*  752 */           throw new IllegalArgumentException("Unexpected type for enum in map.");
/*      */         } 
/*      */         return;
/*      */     } 
/*  756 */     throw new IllegalArgumentException("Unsupported map value type for: " + fieldType);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void writeSInt64List_Internal(int fieldNumber, List<Long> list, boolean packed) throws IOException {
/*  762 */     if (packed) {
/*  763 */       requireSpace(10 + list.size() * 10);
/*  764 */       int prevBytes = getTotalBytesWritten();
/*  765 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  766 */         writeSInt64(((Long)list.get(i)).longValue());
/*      */       }
/*  768 */       int length = getTotalBytesWritten() - prevBytes;
/*  769 */       writeVarint32(length);
/*  770 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  772 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  773 */         writeSInt64(fieldNumber, ((Long)list.get(i)).longValue());
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void writeSInt64List_Internal(int fieldNumber, LongArrayList list, boolean packed) throws IOException {
/*  780 */     if (packed) {
/*  781 */       requireSpace(10 + list.size() * 10);
/*  782 */       int prevBytes = getTotalBytesWritten();
/*  783 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  784 */         writeSInt64(list.getLong(i));
/*      */       }
/*  786 */       int length = getTotalBytesWritten() - prevBytes;
/*  787 */       writeVarint32(length);
/*  788 */       writeTag(fieldNumber, 2);
/*      */     } else {
/*  790 */       for (int i = list.size() - 1; i >= 0; i--) {
/*  791 */         writeSInt64(fieldNumber, list.getLong(i));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeMessageList(int fieldNumber, List<?> list) throws IOException {
/*  798 */     for (int i = list.size() - 1; i >= 0; i--) {
/*  799 */       writeMessage(fieldNumber, list.get(i));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeMessageList(int fieldNumber, List<?> list, Schema schema) throws IOException {
/*  806 */     for (int i = list.size() - 1; i >= 0; i--) {
/*  807 */       writeMessage(fieldNumber, list.get(i), schema);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeGroupList(int fieldNumber, List<?> list) throws IOException {
/*  813 */     for (int i = list.size() - 1; i >= 0; i--) {
/*  814 */       writeGroup(fieldNumber, list.get(i));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeGroupList(int fieldNumber, List<?> list, Schema schema) throws IOException {
/*  821 */     for (int i = list.size() - 1; i >= 0; i--) {
/*  822 */       writeGroup(fieldNumber, list.get(i), schema);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeMessageSetItem(int fieldNumber, Object value) throws IOException {
/*  828 */     writeTag(1, 4);
/*  829 */     if (value instanceof ByteString) {
/*  830 */       writeBytes(3, (ByteString)value);
/*      */     } else {
/*  832 */       writeMessage(3, value);
/*      */     } 
/*  834 */     writeUInt32(2, fieldNumber);
/*  835 */     writeTag(1, 3);
/*      */   }
/*      */   
/*      */   final AllocatedBuffer newHeapBuffer() {
/*  839 */     return this.alloc.allocateHeapBuffer(this.chunkSize);
/*      */   }
/*      */   
/*      */   final AllocatedBuffer newHeapBuffer(int capacity) {
/*  843 */     return this.alloc.allocateHeapBuffer(Math.max(capacity, this.chunkSize));
/*      */   }
/*      */   
/*      */   final AllocatedBuffer newDirectBuffer() {
/*  847 */     return this.alloc.allocateDirectBuffer(this.chunkSize);
/*      */   }
/*      */   
/*      */   final AllocatedBuffer newDirectBuffer(int capacity) {
/*  851 */     return this.alloc.allocateDirectBuffer(Math.max(capacity, this.chunkSize));
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static byte computeUInt64SizeNoTag(long value) {
/*  890 */     if ((value & 0xFFFFFFFFFFFFFF80L) == 0L)
/*      */     {
/*  892 */       return 1;
/*      */     }
/*  894 */     if (value < 0L)
/*      */     {
/*  896 */       return 10;
/*      */     }
/*      */     
/*  899 */     byte n = 2;
/*  900 */     if ((value & 0xFFFFFFF800000000L) != 0L) {
/*      */       
/*  902 */       n = (byte)(n + 4);
/*  903 */       value >>>= 28L;
/*      */     } 
/*  905 */     if ((value & 0xFFFFFFFFFFE00000L) != 0L) {
/*      */       
/*  907 */       n = (byte)(n + 2);
/*  908 */       value >>>= 14L;
/*      */     } 
/*  910 */     if ((value & 0xFFFFFFFFFFFFC000L) != 0L)
/*      */     {
/*  912 */       n = (byte)(n + 1);
/*      */     }
/*  914 */     return n;
/*      */   } public abstract int getTotalBytesWritten(); abstract void requireSpace(int paramInt); abstract void finishCurrentBuffer();
/*      */   abstract void writeTag(int paramInt1, int paramInt2);
/*      */   abstract void writeVarint32(int paramInt);
/*      */   abstract void writeInt32(int paramInt);
/*      */   abstract void writeSInt32(int paramInt);
/*      */   abstract void writeFixed32(int paramInt);
/*      */   abstract void writeVarint64(long paramLong);
/*      */   abstract void writeSInt64(long paramLong);
/*      */   abstract void writeFixed64(long paramLong);
/*      */   abstract void writeBool(boolean paramBoolean);
/*      */   abstract void writeString(String paramString);
/*      */   private static final class SafeHeapWriter extends BinaryWriter { private AllocatedBuffer allocatedBuffer; private byte[] buffer; private int offset;
/*      */     SafeHeapWriter(BufferAllocator alloc, int chunkSize) {
/*  928 */       super(alloc, chunkSize);
/*  929 */       nextBuffer();
/*      */     }
/*      */     private int limit; private int offsetMinusOne; private int limitMinusOne; private int pos;
/*      */     
/*      */     void finishCurrentBuffer() {
/*  934 */       if (this.allocatedBuffer != null) {
/*  935 */         this.totalDoneBytes += bytesWrittenToCurrentBuffer();
/*  936 */         this.allocatedBuffer.position(this.pos - this.allocatedBuffer.arrayOffset() + 1);
/*  937 */         this.allocatedBuffer = null;
/*  938 */         this.pos = 0;
/*  939 */         this.limitMinusOne = 0;
/*      */       } 
/*      */     }
/*      */     
/*      */     private void nextBuffer() {
/*  944 */       nextBuffer(newHeapBuffer());
/*      */     }
/*      */     
/*      */     private void nextBuffer(int capacity) {
/*  948 */       nextBuffer(newHeapBuffer(capacity));
/*      */     }
/*      */     
/*      */     private void nextBuffer(AllocatedBuffer allocatedBuffer) {
/*  952 */       if (!allocatedBuffer.hasArray()) {
/*  953 */         throw new RuntimeException("Allocator returned non-heap buffer");
/*      */       }
/*      */       
/*  956 */       finishCurrentBuffer();
/*      */       
/*  958 */       this.buffers.addFirst(allocatedBuffer);
/*      */       
/*  960 */       this.allocatedBuffer = allocatedBuffer;
/*  961 */       this.buffer = allocatedBuffer.array();
/*  962 */       int arrayOffset = allocatedBuffer.arrayOffset();
/*  963 */       this.limit = arrayOffset + allocatedBuffer.limit();
/*  964 */       this.offset = arrayOffset + allocatedBuffer.position();
/*  965 */       this.offsetMinusOne = this.offset - 1;
/*  966 */       this.limitMinusOne = this.limit - 1;
/*  967 */       this.pos = this.limitMinusOne;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getTotalBytesWritten() {
/*  972 */       return this.totalDoneBytes + bytesWrittenToCurrentBuffer();
/*      */     }
/*      */     
/*      */     int bytesWrittenToCurrentBuffer() {
/*  976 */       return this.limitMinusOne - this.pos;
/*      */     }
/*      */     
/*      */     int spaceLeft() {
/*  980 */       return this.pos - this.offsetMinusOne;
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt32(int fieldNumber, int value) throws IOException {
/*  985 */       requireSpace(10);
/*  986 */       writeVarint32(value);
/*  987 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeInt32(int fieldNumber, int value) throws IOException {
/*  992 */       requireSpace(15);
/*  993 */       writeInt32(value);
/*  994 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeSInt32(int fieldNumber, int value) throws IOException {
/*  999 */       requireSpace(10);
/* 1000 */       writeSInt32(value);
/* 1001 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed32(int fieldNumber, int value) throws IOException {
/* 1006 */       requireSpace(9);
/* 1007 */       writeFixed32(value);
/* 1008 */       writeTag(fieldNumber, 5);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt64(int fieldNumber, long value) throws IOException {
/* 1013 */       requireSpace(15);
/* 1014 */       writeVarint64(value);
/* 1015 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeSInt64(int fieldNumber, long value) throws IOException {
/* 1020 */       requireSpace(15);
/* 1021 */       writeSInt64(value);
/* 1022 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed64(int fieldNumber, long value) throws IOException {
/* 1027 */       requireSpace(13);
/* 1028 */       writeFixed64(value);
/* 1029 */       writeTag(fieldNumber, 1);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBool(int fieldNumber, boolean value) throws IOException {
/* 1034 */       requireSpace(6);
/* 1035 */       write((byte)(value ? 1 : 0));
/* 1036 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeString(int fieldNumber, String value) throws IOException {
/* 1041 */       int prevBytes = getTotalBytesWritten();
/* 1042 */       writeString(value);
/* 1043 */       int length = getTotalBytesWritten() - prevBytes;
/* 1044 */       requireSpace(10);
/* 1045 */       writeVarint32(length);
/* 1046 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBytes(int fieldNumber, ByteString value) throws IOException {
/*      */       try {
/* 1052 */         value.writeToReverse(this);
/* 1053 */       } catch (IOException e) {
/*      */         
/* 1055 */         throw new RuntimeException(e);
/*      */       } 
/*      */       
/* 1058 */       requireSpace(10);
/* 1059 */       writeVarint32(value.size());
/* 1060 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessage(int fieldNumber, Object value) throws IOException {
/* 1065 */       int prevBytes = getTotalBytesWritten();
/* 1066 */       Protobuf.getInstance().writeTo(value, this);
/* 1067 */       int length = getTotalBytesWritten() - prevBytes;
/* 1068 */       requireSpace(10);
/* 1069 */       writeVarint32(length);
/* 1070 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessage(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
/* 1075 */       int prevBytes = getTotalBytesWritten();
/* 1076 */       schema.writeTo(value, this);
/* 1077 */       int length = getTotalBytesWritten() - prevBytes;
/* 1078 */       requireSpace(10);
/* 1079 */       writeVarint32(length);
/* 1080 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeGroup(int fieldNumber, Object value) throws IOException {
/* 1085 */       writeTag(fieldNumber, 4);
/* 1086 */       Protobuf.getInstance().writeTo(value, this);
/* 1087 */       writeTag(fieldNumber, 3);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeGroup(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
/* 1092 */       writeTag(fieldNumber, 4);
/* 1093 */       schema.writeTo(value, this);
/* 1094 */       writeTag(fieldNumber, 3);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeStartGroup(int fieldNumber) {
/* 1099 */       writeTag(fieldNumber, 3);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeEndGroup(int fieldNumber) {
/* 1104 */       writeTag(fieldNumber, 4);
/*      */     }
/*      */ 
/*      */     
/*      */     void writeInt32(int value) {
/* 1109 */       if (value >= 0) {
/* 1110 */         writeVarint32(value);
/*      */       } else {
/* 1112 */         writeVarint64(value);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     void writeSInt32(int value) {
/* 1118 */       writeVarint32(CodedOutputStream.encodeZigZag32(value));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeSInt64(long value) {
/* 1123 */       writeVarint64(CodedOutputStream.encodeZigZag64(value));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeBool(boolean value) {
/* 1128 */       write((byte)(value ? 1 : 0));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeTag(int fieldNumber, int wireType) {
/* 1133 */       writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeVarint32(int value) {
/* 1138 */       if ((value & 0xFFFFFF80) == 0) {
/* 1139 */         writeVarint32OneByte(value);
/* 1140 */       } else if ((value & 0xFFFFC000) == 0) {
/* 1141 */         writeVarint32TwoBytes(value);
/* 1142 */       } else if ((value & 0xFFE00000) == 0) {
/* 1143 */         writeVarint32ThreeBytes(value);
/* 1144 */       } else if ((value & 0xF0000000) == 0) {
/* 1145 */         writeVarint32FourBytes(value);
/*      */       } else {
/* 1147 */         writeVarint32FiveBytes(value);
/*      */       } 
/*      */     }
/*      */     
/*      */     private void writeVarint32OneByte(int value) {
/* 1152 */       this.buffer[this.pos--] = (byte)value;
/*      */     }
/*      */     
/*      */     private void writeVarint32TwoBytes(int value) {
/* 1156 */       this.buffer[this.pos--] = (byte)(value >>> 7);
/* 1157 */       this.buffer[this.pos--] = (byte)(value & 0x7F | 0x80);
/*      */     }
/*      */     
/*      */     private void writeVarint32ThreeBytes(int value) {
/* 1161 */       this.buffer[this.pos--] = (byte)(value >>> 14);
/* 1162 */       this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7F | 0x80);
/* 1163 */       this.buffer[this.pos--] = (byte)(value & 0x7F | 0x80);
/*      */     }
/*      */     
/*      */     private void writeVarint32FourBytes(int value) {
/* 1167 */       this.buffer[this.pos--] = (byte)(value >>> 21);
/* 1168 */       this.buffer[this.pos--] = (byte)(value >>> 14 & 0x7F | 0x80);
/* 1169 */       this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7F | 0x80);
/* 1170 */       this.buffer[this.pos--] = (byte)(value & 0x7F | 0x80);
/*      */     }
/*      */     
/*      */     private void writeVarint32FiveBytes(int value) {
/* 1174 */       this.buffer[this.pos--] = (byte)(value >>> 28);
/* 1175 */       this.buffer[this.pos--] = (byte)(value >>> 21 & 0x7F | 0x80);
/* 1176 */       this.buffer[this.pos--] = (byte)(value >>> 14 & 0x7F | 0x80);
/* 1177 */       this.buffer[this.pos--] = (byte)(value >>> 7 & 0x7F | 0x80);
/* 1178 */       this.buffer[this.pos--] = (byte)(value & 0x7F | 0x80);
/*      */     }
/*      */ 
/*      */     
/*      */     void writeVarint64(long value) {
/* 1183 */       switch (BinaryWriter.computeUInt64SizeNoTag(value)) {
/*      */         case 1:
/* 1185 */           writeVarint64OneByte(value);
/*      */           break;
/*      */         case 2:
/* 1188 */           writeVarint64TwoBytes(value);
/*      */           break;
/*      */         case 3:
/* 1191 */           writeVarint64ThreeBytes(value);
/*      */           break;
/*      */         case 4:
/* 1194 */           writeVarint64FourBytes(value);
/*      */           break;
/*      */         case 5:
/* 1197 */           writeVarint64FiveBytes(value);
/*      */           break;
/*      */         case 6:
/* 1200 */           writeVarint64SixBytes(value);
/*      */           break;
/*      */         case 7:
/* 1203 */           writeVarint64SevenBytes(value);
/*      */           break;
/*      */         case 8:
/* 1206 */           writeVarint64EightBytes(value);
/*      */           break;
/*      */         case 9:
/* 1209 */           writeVarint64NineBytes(value);
/*      */           break;
/*      */         case 10:
/* 1212 */           writeVarint64TenBytes(value);
/*      */           break;
/*      */       } 
/*      */     }
/*      */     
/*      */     private void writeVarint64OneByte(long value) {
/* 1218 */       this.buffer[this.pos--] = (byte)(int)value;
/*      */     }
/*      */     
/*      */     private void writeVarint64TwoBytes(long value) {
/* 1222 */       this.buffer[this.pos--] = (byte)(int)(value >>> 7L);
/* 1223 */       this.buffer[this.pos--] = (byte)((int)value & 0x7F | 0x80);
/*      */     }
/*      */     
/*      */     private void writeVarint64ThreeBytes(long value) {
/* 1227 */       this.buffer[this.pos--] = (byte)((int)value >>> 14);
/* 1228 */       this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
/* 1229 */       this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
/*      */     }
/*      */     
/*      */     private void writeVarint64FourBytes(long value) {
/* 1233 */       this.buffer[this.pos--] = (byte)(int)(value >>> 21L);
/* 1234 */       this.buffer[this.pos--] = (byte)(int)(value >>> 14L & 0x7FL | 0x80L);
/* 1235 */       this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
/* 1236 */       this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
/*      */     }
/*      */     
/*      */     private void writeVarint64FiveBytes(long value) {
/* 1240 */       this.buffer[this.pos--] = (byte)(int)(value >>> 28L);
/* 1241 */       this.buffer[this.pos--] = (byte)(int)(value >>> 21L & 0x7FL | 0x80L);
/* 1242 */       this.buffer[this.pos--] = (byte)(int)(value >>> 14L & 0x7FL | 0x80L);
/* 1243 */       this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
/* 1244 */       this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
/*      */     }
/*      */     
/*      */     private void writeVarint64SixBytes(long value) {
/* 1248 */       this.buffer[this.pos--] = (byte)(int)(value >>> 35L);
/* 1249 */       this.buffer[this.pos--] = (byte)(int)(value >>> 28L & 0x7FL | 0x80L);
/* 1250 */       this.buffer[this.pos--] = (byte)(int)(value >>> 21L & 0x7FL | 0x80L);
/* 1251 */       this.buffer[this.pos--] = (byte)(int)(value >>> 14L & 0x7FL | 0x80L);
/* 1252 */       this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
/* 1253 */       this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
/*      */     }
/*      */     
/*      */     private void writeVarint64SevenBytes(long value) {
/* 1257 */       this.buffer[this.pos--] = (byte)(int)(value >>> 42L);
/* 1258 */       this.buffer[this.pos--] = (byte)(int)(value >>> 35L & 0x7FL | 0x80L);
/* 1259 */       this.buffer[this.pos--] = (byte)(int)(value >>> 28L & 0x7FL | 0x80L);
/* 1260 */       this.buffer[this.pos--] = (byte)(int)(value >>> 21L & 0x7FL | 0x80L);
/* 1261 */       this.buffer[this.pos--] = (byte)(int)(value >>> 14L & 0x7FL | 0x80L);
/* 1262 */       this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
/* 1263 */       this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
/*      */     }
/*      */     
/*      */     private void writeVarint64EightBytes(long value) {
/* 1267 */       this.buffer[this.pos--] = (byte)(int)(value >>> 49L);
/* 1268 */       this.buffer[this.pos--] = (byte)(int)(value >>> 42L & 0x7FL | 0x80L);
/* 1269 */       this.buffer[this.pos--] = (byte)(int)(value >>> 35L & 0x7FL | 0x80L);
/* 1270 */       this.buffer[this.pos--] = (byte)(int)(value >>> 28L & 0x7FL | 0x80L);
/* 1271 */       this.buffer[this.pos--] = (byte)(int)(value >>> 21L & 0x7FL | 0x80L);
/* 1272 */       this.buffer[this.pos--] = (byte)(int)(value >>> 14L & 0x7FL | 0x80L);
/* 1273 */       this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
/* 1274 */       this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
/*      */     }
/*      */     
/*      */     private void writeVarint64NineBytes(long value) {
/* 1278 */       this.buffer[this.pos--] = (byte)(int)(value >>> 56L);
/* 1279 */       this.buffer[this.pos--] = (byte)(int)(value >>> 49L & 0x7FL | 0x80L);
/* 1280 */       this.buffer[this.pos--] = (byte)(int)(value >>> 42L & 0x7FL | 0x80L);
/* 1281 */       this.buffer[this.pos--] = (byte)(int)(value >>> 35L & 0x7FL | 0x80L);
/* 1282 */       this.buffer[this.pos--] = (byte)(int)(value >>> 28L & 0x7FL | 0x80L);
/* 1283 */       this.buffer[this.pos--] = (byte)(int)(value >>> 21L & 0x7FL | 0x80L);
/* 1284 */       this.buffer[this.pos--] = (byte)(int)(value >>> 14L & 0x7FL | 0x80L);
/* 1285 */       this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
/* 1286 */       this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
/*      */     }
/*      */     
/*      */     private void writeVarint64TenBytes(long value) {
/* 1290 */       this.buffer[this.pos--] = (byte)(int)(value >>> 63L);
/* 1291 */       this.buffer[this.pos--] = (byte)(int)(value >>> 56L & 0x7FL | 0x80L);
/* 1292 */       this.buffer[this.pos--] = (byte)(int)(value >>> 49L & 0x7FL | 0x80L);
/* 1293 */       this.buffer[this.pos--] = (byte)(int)(value >>> 42L & 0x7FL | 0x80L);
/* 1294 */       this.buffer[this.pos--] = (byte)(int)(value >>> 35L & 0x7FL | 0x80L);
/* 1295 */       this.buffer[this.pos--] = (byte)(int)(value >>> 28L & 0x7FL | 0x80L);
/* 1296 */       this.buffer[this.pos--] = (byte)(int)(value >>> 21L & 0x7FL | 0x80L);
/* 1297 */       this.buffer[this.pos--] = (byte)(int)(value >>> 14L & 0x7FL | 0x80L);
/* 1298 */       this.buffer[this.pos--] = (byte)(int)(value >>> 7L & 0x7FL | 0x80L);
/* 1299 */       this.buffer[this.pos--] = (byte)(int)(value & 0x7FL | 0x80L);
/*      */     }
/*      */ 
/*      */     
/*      */     void writeFixed32(int value) {
/* 1304 */       this.buffer[this.pos--] = (byte)(value >> 24 & 0xFF);
/* 1305 */       this.buffer[this.pos--] = (byte)(value >> 16 & 0xFF);
/* 1306 */       this.buffer[this.pos--] = (byte)(value >> 8 & 0xFF);
/* 1307 */       this.buffer[this.pos--] = (byte)(value & 0xFF);
/*      */     }
/*      */ 
/*      */     
/*      */     void writeFixed64(long value) {
/* 1312 */       this.buffer[this.pos--] = (byte)((int)(value >> 56L) & 0xFF);
/* 1313 */       this.buffer[this.pos--] = (byte)((int)(value >> 48L) & 0xFF);
/* 1314 */       this.buffer[this.pos--] = (byte)((int)(value >> 40L) & 0xFF);
/* 1315 */       this.buffer[this.pos--] = (byte)((int)(value >> 32L) & 0xFF);
/* 1316 */       this.buffer[this.pos--] = (byte)((int)(value >> 24L) & 0xFF);
/* 1317 */       this.buffer[this.pos--] = (byte)((int)(value >> 16L) & 0xFF);
/* 1318 */       this.buffer[this.pos--] = (byte)((int)(value >> 8L) & 0xFF);
/* 1319 */       this.buffer[this.pos--] = (byte)((int)value & 0xFF);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     void writeString(String in) {
/* 1325 */       requireSpace(in.length());
/*      */ 
/*      */       
/* 1328 */       int i = in.length() - 1;
/*      */       
/* 1330 */       this.pos -= i;
/*      */       
/*      */       char c;
/* 1333 */       for (; i >= 0 && (c = in.charAt(i)) < ''; i--) {
/* 1334 */         this.buffer[this.pos + i] = (byte)c;
/*      */       }
/* 1336 */       if (i == -1) {
/*      */         
/* 1338 */         this.pos--;
/*      */         return;
/*      */       } 
/* 1341 */       this.pos += i;
/* 1342 */       for (; i >= 0; i--) {
/* 1343 */         c = in.charAt(i);
/* 1344 */         if (c < '' && this.pos > this.offsetMinusOne) {
/* 1345 */           this.buffer[this.pos--] = (byte)c;
/* 1346 */         } else if (c < 'ࠀ' && this.pos > this.offset) {
/* 1347 */           this.buffer[this.pos--] = (byte)(0x80 | 0x3F & c);
/* 1348 */           this.buffer[this.pos--] = (byte)(0x3C0 | c >>> 6);
/* 1349 */         } else if ((c < '?' || '?' < c) && this.pos > this.offset + 1) {
/*      */ 
/*      */           
/* 1352 */           this.buffer[this.pos--] = (byte)(0x80 | 0x3F & c);
/* 1353 */           this.buffer[this.pos--] = (byte)(0x80 | 0x3F & c >>> 6);
/* 1354 */           this.buffer[this.pos--] = (byte)(0x1E0 | c >>> 12);
/* 1355 */         } else if (this.pos > this.offset + 2) {
/*      */ 
/*      */           
/* 1358 */           char high = Character.MIN_VALUE;
/* 1359 */           if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c)) {
/* 1360 */             throw new Utf8.UnpairedSurrogateException(i - 1, i);
/*      */           }
/* 1362 */           i--;
/* 1363 */           int codePoint = Character.toCodePoint(high, c);
/* 1364 */           this.buffer[this.pos--] = (byte)(0x80 | 0x3F & codePoint);
/* 1365 */           this.buffer[this.pos--] = (byte)(0x80 | 0x3F & codePoint >>> 6);
/* 1366 */           this.buffer[this.pos--] = (byte)(0x80 | 0x3F & codePoint >>> 12);
/* 1367 */           this.buffer[this.pos--] = (byte)(0xF0 | codePoint >>> 18);
/*      */         } else {
/*      */           
/* 1370 */           requireSpace(i);
/* 1371 */           i++;
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(byte value) {
/* 1378 */       this.buffer[this.pos--] = value;
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(byte[] value, int offset, int length) {
/* 1383 */       if (spaceLeft() < length) {
/* 1384 */         nextBuffer(length);
/*      */       }
/*      */       
/* 1387 */       this.pos -= length;
/* 1388 */       System.arraycopy(value, offset, this.buffer, this.pos + 1, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(byte[] value, int offset, int length) {
/* 1393 */       if (spaceLeft() < length) {
/*      */ 
/*      */         
/* 1396 */         this.totalDoneBytes += length;
/* 1397 */         this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
/*      */ 
/*      */ 
/*      */         
/* 1401 */         nextBuffer();
/*      */         
/*      */         return;
/*      */       } 
/* 1405 */       this.pos -= length;
/* 1406 */       System.arraycopy(value, offset, this.buffer, this.pos + 1, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(ByteBuffer value) {
/* 1411 */       int length = value.remaining();
/* 1412 */       if (spaceLeft() < length) {
/* 1413 */         nextBuffer(length);
/*      */       }
/*      */       
/* 1416 */       this.pos -= length;
/* 1417 */       value.get(this.buffer, this.pos + 1, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(ByteBuffer value) {
/* 1422 */       int length = value.remaining();
/* 1423 */       if (spaceLeft() < length) {
/*      */ 
/*      */         
/* 1426 */         this.totalDoneBytes += length;
/* 1427 */         this.buffers.addFirst(AllocatedBuffer.wrap(value));
/*      */ 
/*      */ 
/*      */         
/* 1431 */         nextBuffer();
/*      */       } 
/*      */       
/* 1434 */       this.pos -= length;
/* 1435 */       value.get(this.buffer, this.pos + 1, length);
/*      */     }
/*      */ 
/*      */     
/*      */     void requireSpace(int size) {
/* 1440 */       if (spaceLeft() < size)
/* 1441 */         nextBuffer(size); 
/*      */     } }
/*      */ 
/*      */   
/*      */   private static final class UnsafeHeapWriter
/*      */     extends BinaryWriter
/*      */   {
/*      */     private AllocatedBuffer allocatedBuffer;
/*      */     private byte[] buffer;
/*      */     private long offset;
/*      */     private long limit;
/*      */     private long offsetMinusOne;
/*      */     private long limitMinusOne;
/*      */     private long pos;
/*      */     
/*      */     UnsafeHeapWriter(BufferAllocator alloc, int chunkSize) {
/* 1457 */       super(alloc, chunkSize);
/* 1458 */       nextBuffer();
/*      */     }
/*      */ 
/*      */     
/*      */     static boolean isSupported() {
/* 1463 */       return UnsafeUtil.hasUnsafeArrayOperations();
/*      */     }
/*      */ 
/*      */     
/*      */     void finishCurrentBuffer() {
/* 1468 */       if (this.allocatedBuffer != null) {
/* 1469 */         this.totalDoneBytes += bytesWrittenToCurrentBuffer();
/* 1470 */         this.allocatedBuffer.position(arrayPos() - this.allocatedBuffer.arrayOffset() + 1);
/* 1471 */         this.allocatedBuffer = null;
/* 1472 */         this.pos = 0L;
/* 1473 */         this.limitMinusOne = 0L;
/*      */       } 
/*      */     }
/*      */     
/*      */     private int arrayPos() {
/* 1478 */       return (int)this.pos;
/*      */     }
/*      */     
/*      */     private void nextBuffer() {
/* 1482 */       nextBuffer(newHeapBuffer());
/*      */     }
/*      */     
/*      */     private void nextBuffer(int capacity) {
/* 1486 */       nextBuffer(newHeapBuffer(capacity));
/*      */     }
/*      */     
/*      */     private void nextBuffer(AllocatedBuffer allocatedBuffer) {
/* 1490 */       if (!allocatedBuffer.hasArray()) {
/* 1491 */         throw new RuntimeException("Allocator returned non-heap buffer");
/*      */       }
/*      */       
/* 1494 */       finishCurrentBuffer();
/* 1495 */       this.buffers.addFirst(allocatedBuffer);
/*      */       
/* 1497 */       this.allocatedBuffer = allocatedBuffer;
/* 1498 */       this.buffer = allocatedBuffer.array();
/* 1499 */       int arrayOffset = allocatedBuffer.arrayOffset();
/* 1500 */       this.limit = (arrayOffset + allocatedBuffer.limit());
/* 1501 */       this.offset = (arrayOffset + allocatedBuffer.position());
/* 1502 */       this.offsetMinusOne = this.offset - 1L;
/* 1503 */       this.limitMinusOne = this.limit - 1L;
/* 1504 */       this.pos = this.limitMinusOne;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getTotalBytesWritten() {
/* 1509 */       return this.totalDoneBytes + bytesWrittenToCurrentBuffer();
/*      */     }
/*      */     
/*      */     int bytesWrittenToCurrentBuffer() {
/* 1513 */       return (int)(this.limitMinusOne - this.pos);
/*      */     }
/*      */     
/*      */     int spaceLeft() {
/* 1517 */       return (int)(this.pos - this.offsetMinusOne);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt32(int fieldNumber, int value) {
/* 1522 */       requireSpace(10);
/* 1523 */       writeVarint32(value);
/* 1524 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeInt32(int fieldNumber, int value) {
/* 1529 */       requireSpace(15);
/* 1530 */       writeInt32(value);
/* 1531 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeSInt32(int fieldNumber, int value) {
/* 1536 */       requireSpace(10);
/* 1537 */       writeSInt32(value);
/* 1538 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed32(int fieldNumber, int value) {
/* 1543 */       requireSpace(9);
/* 1544 */       writeFixed32(value);
/* 1545 */       writeTag(fieldNumber, 5);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt64(int fieldNumber, long value) {
/* 1550 */       requireSpace(15);
/* 1551 */       writeVarint64(value);
/* 1552 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeSInt64(int fieldNumber, long value) {
/* 1557 */       requireSpace(15);
/* 1558 */       writeSInt64(value);
/* 1559 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed64(int fieldNumber, long value) {
/* 1564 */       requireSpace(13);
/* 1565 */       writeFixed64(value);
/* 1566 */       writeTag(fieldNumber, 1);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBool(int fieldNumber, boolean value) {
/* 1571 */       requireSpace(6);
/* 1572 */       write((byte)(value ? 1 : 0));
/* 1573 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeString(int fieldNumber, String value) {
/* 1578 */       int prevBytes = getTotalBytesWritten();
/* 1579 */       writeString(value);
/* 1580 */       int length = getTotalBytesWritten() - prevBytes;
/* 1581 */       requireSpace(10);
/* 1582 */       writeVarint32(length);
/* 1583 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBytes(int fieldNumber, ByteString value) {
/*      */       try {
/* 1589 */         value.writeToReverse(this);
/* 1590 */       } catch (IOException e) {
/*      */         
/* 1592 */         throw new RuntimeException(e);
/*      */       } 
/*      */       
/* 1595 */       requireSpace(10);
/* 1596 */       writeVarint32(value.size());
/* 1597 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessage(int fieldNumber, Object value) throws IOException {
/* 1602 */       int prevBytes = getTotalBytesWritten();
/* 1603 */       Protobuf.getInstance().writeTo(value, this);
/* 1604 */       int length = getTotalBytesWritten() - prevBytes;
/* 1605 */       requireSpace(10);
/* 1606 */       writeVarint32(length);
/* 1607 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessage(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
/* 1612 */       int prevBytes = getTotalBytesWritten();
/* 1613 */       schema.writeTo(value, this);
/* 1614 */       int length = getTotalBytesWritten() - prevBytes;
/* 1615 */       requireSpace(10);
/* 1616 */       writeVarint32(length);
/* 1617 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeGroup(int fieldNumber, Object value) throws IOException {
/* 1622 */       writeTag(fieldNumber, 4);
/* 1623 */       Protobuf.getInstance().writeTo(value, this);
/* 1624 */       writeTag(fieldNumber, 3);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeGroup(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
/* 1629 */       writeTag(fieldNumber, 4);
/* 1630 */       schema.writeTo(value, this);
/* 1631 */       writeTag(fieldNumber, 3);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeStartGroup(int fieldNumber) {
/* 1636 */       writeTag(fieldNumber, 3);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeEndGroup(int fieldNumber) {
/* 1641 */       writeTag(fieldNumber, 4);
/*      */     }
/*      */ 
/*      */     
/*      */     void writeInt32(int value) {
/* 1646 */       if (value >= 0) {
/* 1647 */         writeVarint32(value);
/*      */       } else {
/* 1649 */         writeVarint64(value);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     void writeSInt32(int value) {
/* 1655 */       writeVarint32(CodedOutputStream.encodeZigZag32(value));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeSInt64(long value) {
/* 1660 */       writeVarint64(CodedOutputStream.encodeZigZag64(value));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeBool(boolean value) {
/* 1665 */       write((byte)(value ? 1 : 0));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeTag(int fieldNumber, int wireType) {
/* 1670 */       writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeVarint32(int value) {
/* 1675 */       if ((value & 0xFFFFFF80) == 0) {
/* 1676 */         writeVarint32OneByte(value);
/* 1677 */       } else if ((value & 0xFFFFC000) == 0) {
/* 1678 */         writeVarint32TwoBytes(value);
/* 1679 */       } else if ((value & 0xFFE00000) == 0) {
/* 1680 */         writeVarint32ThreeBytes(value);
/* 1681 */       } else if ((value & 0xF0000000) == 0) {
/* 1682 */         writeVarint32FourBytes(value);
/*      */       } else {
/* 1684 */         writeVarint32FiveBytes(value);
/*      */       } 
/*      */     }
/*      */     
/*      */     private void writeVarint32OneByte(int value) {
/* 1689 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)value);
/*      */     }
/*      */     
/*      */     private void writeVarint32TwoBytes(int value) {
/* 1693 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7));
/* 1694 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7F | 0x80));
/*      */     }
/*      */     
/*      */     private void writeVarint32ThreeBytes(int value) {
/* 1698 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14));
/* 1699 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
/* 1700 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7F | 0x80));
/*      */     }
/*      */     
/*      */     private void writeVarint32FourBytes(int value) {
/* 1704 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 21));
/* 1705 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14 & 0x7F | 0x80));
/* 1706 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
/* 1707 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7F | 0x80));
/*      */     }
/*      */     
/*      */     private void writeVarint32FiveBytes(int value) {
/* 1711 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 28));
/* 1712 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 21 & 0x7F | 0x80));
/* 1713 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 14 & 0x7F | 0x80));
/* 1714 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
/* 1715 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0x7F | 0x80));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeVarint64(long value) {
/* 1720 */       switch (BinaryWriter.computeUInt64SizeNoTag(value)) {
/*      */         case 1:
/* 1722 */           writeVarint64OneByte(value);
/*      */           break;
/*      */         case 2:
/* 1725 */           writeVarint64TwoBytes(value);
/*      */           break;
/*      */         case 3:
/* 1728 */           writeVarint64ThreeBytes(value);
/*      */           break;
/*      */         case 4:
/* 1731 */           writeVarint64FourBytes(value);
/*      */           break;
/*      */         case 5:
/* 1734 */           writeVarint64FiveBytes(value);
/*      */           break;
/*      */         case 6:
/* 1737 */           writeVarint64SixBytes(value);
/*      */           break;
/*      */         case 7:
/* 1740 */           writeVarint64SevenBytes(value);
/*      */           break;
/*      */         case 8:
/* 1743 */           writeVarint64EightBytes(value);
/*      */           break;
/*      */         case 9:
/* 1746 */           writeVarint64NineBytes(value);
/*      */           break;
/*      */         case 10:
/* 1749 */           writeVarint64TenBytes(value);
/*      */           break;
/*      */       } 
/*      */     }
/*      */     
/*      */     private void writeVarint64OneByte(long value) {
/* 1755 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)value);
/*      */     }
/*      */     
/*      */     private void writeVarint64TwoBytes(long value) {
/* 1759 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L));
/* 1760 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)value & 0x7F | 0x80));
/*      */     }
/*      */     
/*      */     private void writeVarint64ThreeBytes(long value) {
/* 1764 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)value >>> 14));
/* 1765 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 1766 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */     
/*      */     private void writeVarint64FourBytes(long value) {
/* 1770 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 21L));
/* 1771 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
/* 1772 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 1773 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */     
/*      */     private void writeVarint64FiveBytes(long value) {
/* 1777 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 28L));
/* 1778 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
/* 1779 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
/* 1780 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 1781 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */     
/*      */     private void writeVarint64SixBytes(long value) {
/* 1785 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 35L));
/* 1786 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
/* 1787 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
/* 1788 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
/* 1789 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 1790 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */     
/*      */     private void writeVarint64SevenBytes(long value) {
/* 1794 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 42L));
/* 1795 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
/* 1796 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
/* 1797 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
/* 1798 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
/* 1799 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 1800 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */     
/*      */     private void writeVarint64EightBytes(long value) {
/* 1804 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 49L));
/* 1805 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 42L & 0x7FL | 0x80L));
/* 1806 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
/* 1807 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
/* 1808 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
/* 1809 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
/* 1810 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 1811 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */     
/*      */     private void writeVarint64NineBytes(long value) {
/* 1815 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 56L));
/* 1816 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 49L & 0x7FL | 0x80L));
/* 1817 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 42L & 0x7FL | 0x80L));
/* 1818 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
/* 1819 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
/* 1820 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
/* 1821 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
/* 1822 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 1823 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */     
/*      */     private void writeVarint64TenBytes(long value) {
/* 1827 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 63L));
/* 1828 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 56L & 0x7FL | 0x80L));
/* 1829 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 49L & 0x7FL | 0x80L));
/* 1830 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 42L & 0x7FL | 0x80L));
/* 1831 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
/* 1832 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
/* 1833 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
/* 1834 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
/* 1835 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 1836 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeFixed32(int value) {
/* 1841 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 24 & 0xFF));
/* 1842 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 16 & 0xFF));
/* 1843 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value >> 8 & 0xFF));
/* 1844 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(value & 0xFF));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeFixed64(long value) {
/* 1849 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(value >> 56L) & 0xFF));
/* 1850 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(value >> 48L) & 0xFF));
/* 1851 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(value >> 40L) & 0xFF));
/* 1852 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(value >> 32L) & 0xFF));
/* 1853 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(value >> 24L) & 0xFF));
/* 1854 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(value >> 16L) & 0xFF));
/* 1855 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(value >> 8L) & 0xFF));
/* 1856 */       UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)value & 0xFF));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     void writeString(String in) {
/* 1862 */       requireSpace(in.length());
/*      */ 
/*      */       
/* 1865 */       int i = in.length() - 1;
/*      */ 
/*      */       
/*      */       char c;
/*      */       
/* 1870 */       for (; i >= 0 && (c = in.charAt(i)) < ''; i--) {
/* 1871 */         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)c);
/*      */       }
/* 1873 */       if (i == -1) {
/*      */         return;
/*      */       }
/*      */       
/* 1877 */       for (; i >= 0; i--) {
/* 1878 */         c = in.charAt(i);
/*      */         
/* 1880 */         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)c);
/*      */         
/* 1882 */         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & c));
/* 1883 */         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x3C0 | c >>> 6));
/*      */ 
/*      */ 
/*      */         
/* 1887 */         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & c));
/* 1888 */         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & c >>> 6));
/* 1889 */         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x1E0 | c >>> 12));
/*      */ 
/*      */         
/*      */         char high;
/*      */         
/* 1894 */         if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c)) {
/* 1895 */           throw new Utf8.UnpairedSurrogateException(i - 1, i);
/*      */         }
/* 1897 */         i--;
/* 1898 */         int codePoint = Character.toCodePoint(high, c);
/* 1899 */         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & codePoint));
/* 1900 */         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 6));
/* 1901 */         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 12));
/* 1902 */         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(0xF0 | codePoint >>> 18));
/*      */ 
/*      */         
/* 1905 */         requireSpace(i);
/* 1906 */         i++;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void write(byte value) {
/* 1913 */       UnsafeUtil.putByte(this.buffer, this.pos--, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(byte[] value, int offset, int length) {
/* 1918 */       if (offset < 0 || offset + length > value.length) {
/* 1919 */         throw new ArrayIndexOutOfBoundsException(
/* 1920 */             String.format("value.length=%d, offset=%d, length=%d", new Object[] { Integer.valueOf(value.length), Integer.valueOf(offset), Integer.valueOf(length) }));
/*      */       }
/* 1922 */       requireSpace(length);
/*      */       
/* 1924 */       this.pos -= length;
/* 1925 */       System.arraycopy(value, offset, this.buffer, arrayPos() + 1, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(byte[] value, int offset, int length) {
/* 1930 */       if (offset < 0 || offset + length > value.length) {
/* 1931 */         throw new ArrayIndexOutOfBoundsException(
/* 1932 */             String.format("value.length=%d, offset=%d, length=%d", new Object[] { Integer.valueOf(value.length), Integer.valueOf(offset), Integer.valueOf(length) }));
/*      */       }
/* 1934 */       if (spaceLeft() < length) {
/*      */ 
/*      */         
/* 1937 */         this.totalDoneBytes += length;
/* 1938 */         this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
/*      */ 
/*      */ 
/*      */         
/* 1942 */         nextBuffer();
/*      */         
/*      */         return;
/*      */       } 
/* 1946 */       this.pos -= length;
/* 1947 */       System.arraycopy(value, offset, this.buffer, arrayPos() + 1, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(ByteBuffer value) {
/* 1952 */       int length = value.remaining();
/* 1953 */       requireSpace(length);
/*      */       
/* 1955 */       this.pos -= length;
/* 1956 */       value.get(this.buffer, arrayPos() + 1, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(ByteBuffer value) {
/* 1961 */       int length = value.remaining();
/* 1962 */       if (spaceLeft() < length) {
/*      */ 
/*      */         
/* 1965 */         this.totalDoneBytes += length;
/* 1966 */         this.buffers.addFirst(AllocatedBuffer.wrap(value));
/*      */ 
/*      */ 
/*      */         
/* 1970 */         nextBuffer();
/*      */       } 
/*      */       
/* 1973 */       this.pos -= length;
/* 1974 */       value.get(this.buffer, arrayPos() + 1, length);
/*      */     }
/*      */ 
/*      */     
/*      */     void requireSpace(int size) {
/* 1979 */       if (spaceLeft() < size)
/* 1980 */         nextBuffer(size); 
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class SafeDirectWriter
/*      */     extends BinaryWriter
/*      */   {
/*      */     private ByteBuffer buffer;
/*      */     private int limitMinusOne;
/*      */     private int pos;
/*      */     
/*      */     SafeDirectWriter(BufferAllocator alloc, int chunkSize) {
/* 1992 */       super(alloc, chunkSize);
/* 1993 */       nextBuffer();
/*      */     }
/*      */     
/*      */     private void nextBuffer() {
/* 1997 */       nextBuffer(newDirectBuffer());
/*      */     }
/*      */     
/*      */     private void nextBuffer(int capacity) {
/* 2001 */       nextBuffer(newDirectBuffer(capacity));
/*      */     }
/*      */     
/*      */     private void nextBuffer(AllocatedBuffer allocatedBuffer) {
/* 2005 */       if (!allocatedBuffer.hasNioBuffer()) {
/* 2006 */         throw new RuntimeException("Allocated buffer does not have NIO buffer");
/*      */       }
/* 2008 */       ByteBuffer nioBuffer = allocatedBuffer.nioBuffer();
/* 2009 */       if (!nioBuffer.isDirect()) {
/* 2010 */         throw new RuntimeException("Allocator returned non-direct buffer");
/*      */       }
/*      */       
/* 2013 */       finishCurrentBuffer();
/* 2014 */       this.buffers.addFirst(allocatedBuffer);
/*      */       
/* 2016 */       this.buffer = nioBuffer;
/* 2017 */       this.buffer.limit(this.buffer.capacity());
/* 2018 */       this.buffer.position(0);
/*      */       
/* 2020 */       this.buffer.order(ByteOrder.LITTLE_ENDIAN);
/*      */       
/* 2022 */       this.limitMinusOne = this.buffer.limit() - 1;
/* 2023 */       this.pos = this.limitMinusOne;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getTotalBytesWritten() {
/* 2028 */       return this.totalDoneBytes + bytesWrittenToCurrentBuffer();
/*      */     }
/*      */     
/*      */     private int bytesWrittenToCurrentBuffer() {
/* 2032 */       return this.limitMinusOne - this.pos;
/*      */     }
/*      */     
/*      */     private int spaceLeft() {
/* 2036 */       return this.pos + 1;
/*      */     }
/*      */ 
/*      */     
/*      */     void finishCurrentBuffer() {
/* 2041 */       if (this.buffer != null) {
/* 2042 */         this.totalDoneBytes += bytesWrittenToCurrentBuffer();
/*      */         
/* 2044 */         this.buffer.position(this.pos + 1);
/* 2045 */         this.buffer = null;
/* 2046 */         this.pos = 0;
/* 2047 */         this.limitMinusOne = 0;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt32(int fieldNumber, int value) {
/* 2053 */       requireSpace(10);
/* 2054 */       writeVarint32(value);
/* 2055 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeInt32(int fieldNumber, int value) {
/* 2060 */       requireSpace(15);
/* 2061 */       writeInt32(value);
/* 2062 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeSInt32(int fieldNumber, int value) {
/* 2067 */       requireSpace(10);
/* 2068 */       writeSInt32(value);
/* 2069 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed32(int fieldNumber, int value) {
/* 2074 */       requireSpace(9);
/* 2075 */       writeFixed32(value);
/* 2076 */       writeTag(fieldNumber, 5);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt64(int fieldNumber, long value) {
/* 2081 */       requireSpace(15);
/* 2082 */       writeVarint64(value);
/* 2083 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeSInt64(int fieldNumber, long value) {
/* 2088 */       requireSpace(15);
/* 2089 */       writeSInt64(value);
/* 2090 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed64(int fieldNumber, long value) {
/* 2095 */       requireSpace(13);
/* 2096 */       writeFixed64(value);
/* 2097 */       writeTag(fieldNumber, 1);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBool(int fieldNumber, boolean value) {
/* 2102 */       requireSpace(6);
/* 2103 */       write((byte)(value ? 1 : 0));
/* 2104 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeString(int fieldNumber, String value) {
/* 2109 */       int prevBytes = getTotalBytesWritten();
/* 2110 */       writeString(value);
/* 2111 */       int length = getTotalBytesWritten() - prevBytes;
/* 2112 */       requireSpace(10);
/* 2113 */       writeVarint32(length);
/* 2114 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBytes(int fieldNumber, ByteString value) {
/*      */       try {
/* 2120 */         value.writeToReverse(this);
/* 2121 */       } catch (IOException e) {
/*      */         
/* 2123 */         throw new RuntimeException(e);
/*      */       } 
/*      */       
/* 2126 */       requireSpace(10);
/* 2127 */       writeVarint32(value.size());
/* 2128 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessage(int fieldNumber, Object value) throws IOException {
/* 2133 */       int prevBytes = getTotalBytesWritten();
/* 2134 */       Protobuf.getInstance().writeTo(value, this);
/* 2135 */       int length = getTotalBytesWritten() - prevBytes;
/* 2136 */       requireSpace(10);
/* 2137 */       writeVarint32(length);
/* 2138 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessage(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
/* 2143 */       int prevBytes = getTotalBytesWritten();
/* 2144 */       schema.writeTo(value, this);
/* 2145 */       int length = getTotalBytesWritten() - prevBytes;
/* 2146 */       requireSpace(10);
/* 2147 */       writeVarint32(length);
/* 2148 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeGroup(int fieldNumber, Object value) throws IOException {
/* 2153 */       writeTag(fieldNumber, 4);
/* 2154 */       Protobuf.getInstance().writeTo(value, this);
/* 2155 */       writeTag(fieldNumber, 3);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeGroup(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
/* 2160 */       writeTag(fieldNumber, 4);
/* 2161 */       schema.writeTo(value, this);
/* 2162 */       writeTag(fieldNumber, 3);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeStartGroup(int fieldNumber) {
/* 2167 */       writeTag(fieldNumber, 3);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeEndGroup(int fieldNumber) {
/* 2172 */       writeTag(fieldNumber, 4);
/*      */     }
/*      */ 
/*      */     
/*      */     void writeInt32(int value) {
/* 2177 */       if (value >= 0) {
/* 2178 */         writeVarint32(value);
/*      */       } else {
/* 2180 */         writeVarint64(value);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     void writeSInt32(int value) {
/* 2186 */       writeVarint32(CodedOutputStream.encodeZigZag32(value));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeSInt64(long value) {
/* 2191 */       writeVarint64(CodedOutputStream.encodeZigZag64(value));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeBool(boolean value) {
/* 2196 */       write((byte)(value ? 1 : 0));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeTag(int fieldNumber, int wireType) {
/* 2201 */       writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeVarint32(int value) {
/* 2206 */       if ((value & 0xFFFFFF80) == 0) {
/* 2207 */         writeVarint32OneByte(value);
/* 2208 */       } else if ((value & 0xFFFFC000) == 0) {
/* 2209 */         writeVarint32TwoBytes(value);
/* 2210 */       } else if ((value & 0xFFE00000) == 0) {
/* 2211 */         writeVarint32ThreeBytes(value);
/* 2212 */       } else if ((value & 0xF0000000) == 0) {
/* 2213 */         writeVarint32FourBytes(value);
/*      */       } else {
/* 2215 */         writeVarint32FiveBytes(value);
/*      */       } 
/*      */     }
/*      */     
/*      */     private void writeVarint32OneByte(int value) {
/* 2220 */       this.buffer.put(this.pos--, (byte)value);
/*      */     }
/*      */ 
/*      */     
/*      */     private void writeVarint32TwoBytes(int value) {
/* 2225 */       this.pos -= 2;
/* 2226 */       this.buffer.putShort(this.pos + 1, (short)((value & 0x3F80) << 1 | value & 0x7F | 0x80));
/*      */     }
/*      */ 
/*      */     
/*      */     private void writeVarint32ThreeBytes(int value) {
/* 2231 */       this.pos -= 3;
/* 2232 */       this.buffer.putInt(this.pos, (value & 0x1FC000) << 10 | (value & 0x3F80 | 0x4000) << 9 | (value & 0x7F | 0x80) << 8);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void writeVarint32FourBytes(int value) {
/* 2241 */       this.pos -= 4;
/* 2242 */       this.buffer.putInt(this.pos + 1, (value & 0xFE00000) << 3 | (value & 0x1FC000 | 0x200000) << 2 | (value & 0x3F80 | 0x4000) << 1 | value & 0x7F | 0x80);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void writeVarint32FiveBytes(int value) {
/* 2252 */       this.buffer.put(this.pos--, (byte)(value >>> 28));
/* 2253 */       this.pos -= 4;
/* 2254 */       this.buffer.putInt(this.pos + 1, (value >>> 21 & 0x7F | 0x80) << 24 | (value >>> 14 & 0x7F | 0x80) << 16 | (value >>> 7 & 0x7F | 0x80) << 8 | value & 0x7F | 0x80);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void writeVarint64(long value) {
/* 2264 */       switch (BinaryWriter.computeUInt64SizeNoTag(value)) {
/*      */         case 1:
/* 2266 */           writeVarint64OneByte(value);
/*      */           break;
/*      */         case 2:
/* 2269 */           writeVarint64TwoBytes(value);
/*      */           break;
/*      */         case 3:
/* 2272 */           writeVarint64ThreeBytes(value);
/*      */           break;
/*      */         case 4:
/* 2275 */           writeVarint64FourBytes(value);
/*      */           break;
/*      */         case 5:
/* 2278 */           writeVarint64FiveBytes(value);
/*      */           break;
/*      */         case 6:
/* 2281 */           writeVarint64SixBytes(value);
/*      */           break;
/*      */         case 7:
/* 2284 */           writeVarint64SevenBytes(value);
/*      */           break;
/*      */         case 8:
/* 2287 */           writeVarint64EightBytes(value);
/*      */           break;
/*      */         case 9:
/* 2290 */           writeVarint64NineBytes(value);
/*      */           break;
/*      */         case 10:
/* 2293 */           writeVarint64TenBytes(value);
/*      */           break;
/*      */       } 
/*      */     }
/*      */     
/*      */     private void writeVarint64OneByte(long value) {
/* 2299 */       writeVarint32OneByte((int)value);
/*      */     }
/*      */     
/*      */     private void writeVarint64TwoBytes(long value) {
/* 2303 */       writeVarint32TwoBytes((int)value);
/*      */     }
/*      */     
/*      */     private void writeVarint64ThreeBytes(long value) {
/* 2307 */       writeVarint32ThreeBytes((int)value);
/*      */     }
/*      */     
/*      */     private void writeVarint64FourBytes(long value) {
/* 2311 */       writeVarint32FourBytes((int)value);
/*      */     }
/*      */ 
/*      */     
/*      */     private void writeVarint64FiveBytes(long value) {
/* 2316 */       this.pos -= 5;
/* 2317 */       this.buffer.putLong(this.pos - 2, (value & 0x7F0000000L) << 28L | (value & 0xFE00000L | 0x10000000L) << 27L | (value & 0x1FC000L | 0x200000L) << 26L | (value & 0x3F80L | 0x4000L) << 25L | (value & 0x7FL | 0x80L) << 24L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void writeVarint64SixBytes(long value) {
/* 2328 */       this.pos -= 6;
/* 2329 */       this.buffer.putLong(this.pos - 1, (value & 0x3F800000000L) << 21L | (value & 0x7F0000000L | 0x800000000L) << 20L | (value & 0xFE00000L | 0x10000000L) << 19L | (value & 0x1FC000L | 0x200000L) << 18L | (value & 0x3F80L | 0x4000L) << 17L | (value & 0x7FL | 0x80L) << 16L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void writeVarint64SevenBytes(long value) {
/* 2341 */       this.pos -= 7;
/* 2342 */       this.buffer.putLong(this.pos, (value & 0x1FC0000000000L) << 14L | (value & 0x3F800000000L | 0x40000000000L) << 13L | (value & 0x7F0000000L | 0x800000000L) << 12L | (value & 0xFE00000L | 0x10000000L) << 11L | (value & 0x1FC000L | 0x200000L) << 10L | (value & 0x3F80L | 0x4000L) << 9L | (value & 0x7FL | 0x80L) << 8L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void writeVarint64EightBytes(long value) {
/* 2355 */       this.pos -= 8;
/* 2356 */       this.buffer.putLong(this.pos + 1, (value & 0xFE000000000000L) << 7L | (value & 0x1FC0000000000L | 0x2000000000000L) << 6L | (value & 0x3F800000000L | 0x40000000000L) << 5L | (value & 0x7F0000000L | 0x800000000L) << 4L | (value & 0xFE00000L | 0x10000000L) << 3L | (value & 0x1FC000L | 0x200000L) << 2L | (value & 0x3F80L | 0x4000L) << 1L | value & 0x7FL | 0x80L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void writeVarint64EightBytesWithSign(long value) {
/* 2370 */       this.pos -= 8;
/* 2371 */       this.buffer.putLong(this.pos + 1, (value & 0xFE000000000000L | 0x100000000000000L) << 7L | (value & 0x1FC0000000000L | 0x2000000000000L) << 6L | (value & 0x3F800000000L | 0x40000000000L) << 5L | (value & 0x7F0000000L | 0x800000000L) << 4L | (value & 0xFE00000L | 0x10000000L) << 3L | (value & 0x1FC000L | 0x200000L) << 2L | (value & 0x3F80L | 0x4000L) << 1L | value & 0x7FL | 0x80L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void writeVarint64NineBytes(long value) {
/* 2384 */       this.buffer.put(this.pos--, (byte)(int)(value >>> 56L));
/* 2385 */       writeVarint64EightBytesWithSign(value & 0xFFFFFFFFFFFFFFL);
/*      */     }
/*      */     
/*      */     private void writeVarint64TenBytes(long value) {
/* 2389 */       this.buffer.put(this.pos--, (byte)(int)(value >>> 63L));
/* 2390 */       this.buffer.put(this.pos--, (byte)(int)(value >>> 56L & 0x7FL | 0x80L));
/* 2391 */       writeVarint64EightBytesWithSign(value & 0xFFFFFFFFFFFFFFL);
/*      */     }
/*      */ 
/*      */     
/*      */     void writeFixed32(int value) {
/* 2396 */       this.pos -= 4;
/* 2397 */       this.buffer.putInt(this.pos + 1, value);
/*      */     }
/*      */ 
/*      */     
/*      */     void writeFixed64(long value) {
/* 2402 */       this.pos -= 8;
/* 2403 */       this.buffer.putLong(this.pos + 1, value);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     void writeString(String in) {
/* 2409 */       requireSpace(in.length());
/*      */ 
/*      */       
/* 2412 */       int i = in.length() - 1;
/* 2413 */       this.pos -= i;
/*      */       
/*      */       char c;
/* 2416 */       for (; i >= 0 && (c = in.charAt(i)) < ''; i--) {
/* 2417 */         this.buffer.put(this.pos + i, (byte)c);
/*      */       }
/* 2419 */       if (i == -1) {
/*      */         
/* 2421 */         this.pos--;
/*      */         return;
/*      */       } 
/* 2424 */       this.pos += i;
/* 2425 */       for (; i >= 0; i--) {
/* 2426 */         c = in.charAt(i);
/* 2427 */         if (c < '' && this.pos >= 0) {
/* 2428 */           this.buffer.put(this.pos--, (byte)c);
/* 2429 */         } else if (c < 'ࠀ' && this.pos > 0) {
/* 2430 */           this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & c));
/* 2431 */           this.buffer.put(this.pos--, (byte)(0x3C0 | c >>> 6));
/* 2432 */         } else if ((c < '?' || '?' < c) && this.pos > 1) {
/*      */           
/* 2434 */           this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & c));
/* 2435 */           this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & c >>> 6));
/* 2436 */           this.buffer.put(this.pos--, (byte)(0x1E0 | c >>> 12));
/* 2437 */         } else if (this.pos > 2) {
/*      */ 
/*      */           
/* 2440 */           char high = Character.MIN_VALUE;
/* 2441 */           if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c)) {
/* 2442 */             throw new Utf8.UnpairedSurrogateException(i - 1, i);
/*      */           }
/* 2444 */           i--;
/* 2445 */           int codePoint = Character.toCodePoint(high, c);
/* 2446 */           this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & codePoint));
/* 2447 */           this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 6));
/* 2448 */           this.buffer.put(this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 12));
/* 2449 */           this.buffer.put(this.pos--, (byte)(0xF0 | codePoint >>> 18));
/*      */         } else {
/*      */           
/* 2452 */           requireSpace(i);
/* 2453 */           i++;
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(byte value) {
/* 2460 */       this.buffer.put(this.pos--, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(byte[] value, int offset, int length) {
/* 2465 */       if (spaceLeft() < length) {
/* 2466 */         nextBuffer(length);
/*      */       }
/*      */       
/* 2469 */       this.pos -= length;
/* 2470 */       this.buffer.position(this.pos + 1);
/* 2471 */       this.buffer.put(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(byte[] value, int offset, int length) {
/* 2476 */       if (spaceLeft() < length) {
/*      */ 
/*      */         
/* 2479 */         this.totalDoneBytes += length;
/* 2480 */         this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
/*      */ 
/*      */ 
/*      */         
/* 2484 */         nextBuffer();
/*      */         
/*      */         return;
/*      */       } 
/* 2488 */       this.pos -= length;
/* 2489 */       this.buffer.position(this.pos + 1);
/* 2490 */       this.buffer.put(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(ByteBuffer value) {
/* 2495 */       int length = value.remaining();
/* 2496 */       if (spaceLeft() < length) {
/* 2497 */         nextBuffer(length);
/*      */       }
/*      */       
/* 2500 */       this.pos -= length;
/* 2501 */       this.buffer.position(this.pos + 1);
/* 2502 */       this.buffer.put(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(ByteBuffer value) {
/* 2507 */       int length = value.remaining();
/* 2508 */       if (spaceLeft() < length) {
/*      */ 
/*      */         
/* 2511 */         this.totalDoneBytes += length;
/* 2512 */         this.buffers.addFirst(AllocatedBuffer.wrap(value));
/*      */ 
/*      */ 
/*      */         
/* 2516 */         nextBuffer();
/*      */         
/*      */         return;
/*      */       } 
/* 2520 */       this.pos -= length;
/* 2521 */       this.buffer.position(this.pos + 1);
/* 2522 */       this.buffer.put(value);
/*      */     }
/*      */ 
/*      */     
/*      */     void requireSpace(int size) {
/* 2527 */       if (spaceLeft() < size)
/* 2528 */         nextBuffer(size); 
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class UnsafeDirectWriter
/*      */     extends BinaryWriter
/*      */   {
/*      */     private ByteBuffer buffer;
/*      */     private long bufferOffset;
/*      */     private long limitMinusOne;
/*      */     private long pos;
/*      */     
/*      */     UnsafeDirectWriter(BufferAllocator alloc, int chunkSize) {
/* 2541 */       super(alloc, chunkSize);
/* 2542 */       nextBuffer();
/*      */     }
/*      */ 
/*      */     
/*      */     private static boolean isSupported() {
/* 2547 */       return UnsafeUtil.hasUnsafeByteBufferOperations();
/*      */     }
/*      */     
/*      */     private void nextBuffer() {
/* 2551 */       nextBuffer(newDirectBuffer());
/*      */     }
/*      */     
/*      */     private void nextBuffer(int capacity) {
/* 2555 */       nextBuffer(newDirectBuffer(capacity));
/*      */     }
/*      */     
/*      */     private void nextBuffer(AllocatedBuffer allocatedBuffer) {
/* 2559 */       if (!allocatedBuffer.hasNioBuffer()) {
/* 2560 */         throw new RuntimeException("Allocated buffer does not have NIO buffer");
/*      */       }
/* 2562 */       ByteBuffer nioBuffer = allocatedBuffer.nioBuffer();
/* 2563 */       if (!nioBuffer.isDirect()) {
/* 2564 */         throw new RuntimeException("Allocator returned non-direct buffer");
/*      */       }
/*      */       
/* 2567 */       finishCurrentBuffer();
/* 2568 */       this.buffers.addFirst(allocatedBuffer);
/*      */       
/* 2570 */       this.buffer = nioBuffer;
/* 2571 */       this.buffer.limit(this.buffer.capacity());
/* 2572 */       this.buffer.position(0);
/*      */       
/* 2574 */       this.bufferOffset = UnsafeUtil.addressOffset(this.buffer);
/* 2575 */       this.limitMinusOne = this.bufferOffset + (this.buffer.limit() - 1);
/* 2576 */       this.pos = this.limitMinusOne;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getTotalBytesWritten() {
/* 2581 */       return this.totalDoneBytes + bytesWrittenToCurrentBuffer();
/*      */     }
/*      */     
/*      */     private int bytesWrittenToCurrentBuffer() {
/* 2585 */       return (int)(this.limitMinusOne - this.pos);
/*      */     }
/*      */     
/*      */     private int spaceLeft() {
/* 2589 */       return bufferPos() + 1;
/*      */     }
/*      */ 
/*      */     
/*      */     void finishCurrentBuffer() {
/* 2594 */       if (this.buffer != null) {
/* 2595 */         this.totalDoneBytes += bytesWrittenToCurrentBuffer();
/*      */         
/* 2597 */         this.buffer.position(bufferPos() + 1);
/* 2598 */         this.buffer = null;
/* 2599 */         this.pos = 0L;
/* 2600 */         this.limitMinusOne = 0L;
/*      */       } 
/*      */     }
/*      */     
/*      */     private int bufferPos() {
/* 2605 */       return (int)(this.pos - this.bufferOffset);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt32(int fieldNumber, int value) {
/* 2610 */       requireSpace(10);
/* 2611 */       writeVarint32(value);
/* 2612 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeInt32(int fieldNumber, int value) {
/* 2617 */       requireSpace(15);
/* 2618 */       writeInt32(value);
/* 2619 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeSInt32(int fieldNumber, int value) {
/* 2624 */       requireSpace(10);
/* 2625 */       writeSInt32(value);
/* 2626 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed32(int fieldNumber, int value) {
/* 2631 */       requireSpace(9);
/* 2632 */       writeFixed32(value);
/* 2633 */       writeTag(fieldNumber, 5);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt64(int fieldNumber, long value) {
/* 2638 */       requireSpace(15);
/* 2639 */       writeVarint64(value);
/* 2640 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeSInt64(int fieldNumber, long value) {
/* 2645 */       requireSpace(15);
/* 2646 */       writeSInt64(value);
/* 2647 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed64(int fieldNumber, long value) {
/* 2652 */       requireSpace(13);
/* 2653 */       writeFixed64(value);
/* 2654 */       writeTag(fieldNumber, 1);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBool(int fieldNumber, boolean value) {
/* 2659 */       requireSpace(6);
/* 2660 */       write((byte)(value ? 1 : 0));
/* 2661 */       writeTag(fieldNumber, 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeString(int fieldNumber, String value) {
/* 2666 */       int prevBytes = getTotalBytesWritten();
/* 2667 */       writeString(value);
/* 2668 */       int length = getTotalBytesWritten() - prevBytes;
/* 2669 */       requireSpace(10);
/* 2670 */       writeVarint32(length);
/* 2671 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBytes(int fieldNumber, ByteString value) {
/*      */       try {
/* 2677 */         value.writeToReverse(this);
/* 2678 */       } catch (IOException e) {
/*      */         
/* 2680 */         throw new RuntimeException(e);
/*      */       } 
/*      */       
/* 2683 */       requireSpace(10);
/* 2684 */       writeVarint32(value.size());
/* 2685 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessage(int fieldNumber, Object value) throws IOException {
/* 2690 */       int prevBytes = getTotalBytesWritten();
/* 2691 */       Protobuf.getInstance().writeTo(value, this);
/* 2692 */       int length = getTotalBytesWritten() - prevBytes;
/* 2693 */       requireSpace(10);
/* 2694 */       writeVarint32(length);
/* 2695 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessage(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
/* 2700 */       int prevBytes = getTotalBytesWritten();
/* 2701 */       schema.writeTo(value, this);
/* 2702 */       int length = getTotalBytesWritten() - prevBytes;
/* 2703 */       requireSpace(10);
/* 2704 */       writeVarint32(length);
/* 2705 */       writeTag(fieldNumber, 2);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeGroup(int fieldNumber, Object value) throws IOException {
/* 2710 */       writeTag(fieldNumber, 4);
/* 2711 */       Protobuf.getInstance().writeTo(value, this);
/* 2712 */       writeTag(fieldNumber, 3);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeGroup(int fieldNumber, Object value, Schema<Object> schema) throws IOException {
/* 2717 */       writeTag(fieldNumber, 4);
/* 2718 */       schema.writeTo(value, this);
/* 2719 */       writeTag(fieldNumber, 3);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeStartGroup(int fieldNumber) {
/* 2724 */       writeTag(fieldNumber, 3);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeEndGroup(int fieldNumber) {
/* 2729 */       writeTag(fieldNumber, 4);
/*      */     }
/*      */ 
/*      */     
/*      */     void writeInt32(int value) {
/* 2734 */       if (value >= 0) {
/* 2735 */         writeVarint32(value);
/*      */       } else {
/* 2737 */         writeVarint64(value);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     void writeSInt32(int value) {
/* 2743 */       writeVarint32(CodedOutputStream.encodeZigZag32(value));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeSInt64(long value) {
/* 2748 */       writeVarint64(CodedOutputStream.encodeZigZag64(value));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeBool(boolean value) {
/* 2753 */       write((byte)(value ? 1 : 0));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeTag(int fieldNumber, int wireType) {
/* 2758 */       writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeVarint32(int value) {
/* 2763 */       if ((value & 0xFFFFFF80) == 0) {
/* 2764 */         writeVarint32OneByte(value);
/* 2765 */       } else if ((value & 0xFFFFC000) == 0) {
/* 2766 */         writeVarint32TwoBytes(value);
/* 2767 */       } else if ((value & 0xFFE00000) == 0) {
/* 2768 */         writeVarint32ThreeBytes(value);
/* 2769 */       } else if ((value & 0xF0000000) == 0) {
/* 2770 */         writeVarint32FourBytes(value);
/*      */       } else {
/* 2772 */         writeVarint32FiveBytes(value);
/*      */       } 
/*      */     }
/*      */     
/*      */     private void writeVarint32OneByte(int value) {
/* 2777 */       UnsafeUtil.putByte(this.pos--, (byte)value);
/*      */     }
/*      */     
/*      */     private void writeVarint32TwoBytes(int value) {
/* 2781 */       UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7));
/* 2782 */       UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7F | 0x80));
/*      */     }
/*      */     
/*      */     private void writeVarint32ThreeBytes(int value) {
/* 2786 */       UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14));
/* 2787 */       UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
/* 2788 */       UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7F | 0x80));
/*      */     }
/*      */     
/*      */     private void writeVarint32FourBytes(int value) {
/* 2792 */       UnsafeUtil.putByte(this.pos--, (byte)(value >>> 21));
/* 2793 */       UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14 & 0x7F | 0x80));
/* 2794 */       UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
/* 2795 */       UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7F | 0x80));
/*      */     }
/*      */     
/*      */     private void writeVarint32FiveBytes(int value) {
/* 2799 */       UnsafeUtil.putByte(this.pos--, (byte)(value >>> 28));
/* 2800 */       UnsafeUtil.putByte(this.pos--, (byte)(value >>> 21 & 0x7F | 0x80));
/* 2801 */       UnsafeUtil.putByte(this.pos--, (byte)(value >>> 14 & 0x7F | 0x80));
/* 2802 */       UnsafeUtil.putByte(this.pos--, (byte)(value >>> 7 & 0x7F | 0x80));
/* 2803 */       UnsafeUtil.putByte(this.pos--, (byte)(value & 0x7F | 0x80));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeVarint64(long value) {
/* 2808 */       switch (BinaryWriter.computeUInt64SizeNoTag(value)) {
/*      */         case 1:
/* 2810 */           writeVarint64OneByte(value);
/*      */           break;
/*      */         case 2:
/* 2813 */           writeVarint64TwoBytes(value);
/*      */           break;
/*      */         case 3:
/* 2816 */           writeVarint64ThreeBytes(value);
/*      */           break;
/*      */         case 4:
/* 2819 */           writeVarint64FourBytes(value);
/*      */           break;
/*      */         case 5:
/* 2822 */           writeVarint64FiveBytes(value);
/*      */           break;
/*      */         case 6:
/* 2825 */           writeVarint64SixBytes(value);
/*      */           break;
/*      */         case 7:
/* 2828 */           writeVarint64SevenBytes(value);
/*      */           break;
/*      */         case 8:
/* 2831 */           writeVarint64EightBytes(value);
/*      */           break;
/*      */         case 9:
/* 2834 */           writeVarint64NineBytes(value);
/*      */           break;
/*      */         case 10:
/* 2837 */           writeVarint64TenBytes(value);
/*      */           break;
/*      */       } 
/*      */     }
/*      */     
/*      */     private void writeVarint64OneByte(long value) {
/* 2843 */       UnsafeUtil.putByte(this.pos--, (byte)(int)value);
/*      */     }
/*      */     
/*      */     private void writeVarint64TwoBytes(long value) {
/* 2847 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L));
/* 2848 */       UnsafeUtil.putByte(this.pos--, (byte)((int)value & 0x7F | 0x80));
/*      */     }
/*      */     
/*      */     private void writeVarint64ThreeBytes(long value) {
/* 2852 */       UnsafeUtil.putByte(this.pos--, (byte)((int)value >>> 14));
/* 2853 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 2854 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */     
/*      */     private void writeVarint64FourBytes(long value) {
/* 2858 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 21L));
/* 2859 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
/* 2860 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 2861 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */     
/*      */     private void writeVarint64FiveBytes(long value) {
/* 2865 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 28L));
/* 2866 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
/* 2867 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
/* 2868 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 2869 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */     
/*      */     private void writeVarint64SixBytes(long value) {
/* 2873 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 35L));
/* 2874 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
/* 2875 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
/* 2876 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
/* 2877 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 2878 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */     
/*      */     private void writeVarint64SevenBytes(long value) {
/* 2882 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 42L));
/* 2883 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
/* 2884 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
/* 2885 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
/* 2886 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
/* 2887 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 2888 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */     
/*      */     private void writeVarint64EightBytes(long value) {
/* 2892 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 49L));
/* 2893 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 42L & 0x7FL | 0x80L));
/* 2894 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
/* 2895 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
/* 2896 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
/* 2897 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
/* 2898 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 2899 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */     
/*      */     private void writeVarint64NineBytes(long value) {
/* 2903 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 56L));
/* 2904 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 49L & 0x7FL | 0x80L));
/* 2905 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 42L & 0x7FL | 0x80L));
/* 2906 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
/* 2907 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
/* 2908 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
/* 2909 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
/* 2910 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 2911 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */     
/*      */     private void writeVarint64TenBytes(long value) {
/* 2915 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 63L));
/* 2916 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 56L & 0x7FL | 0x80L));
/* 2917 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 49L & 0x7FL | 0x80L));
/* 2918 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 42L & 0x7FL | 0x80L));
/* 2919 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 35L & 0x7FL | 0x80L));
/* 2920 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 28L & 0x7FL | 0x80L));
/* 2921 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 21L & 0x7FL | 0x80L));
/* 2922 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 14L & 0x7FL | 0x80L));
/* 2923 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value >>> 7L & 0x7FL | 0x80L));
/* 2924 */       UnsafeUtil.putByte(this.pos--, (byte)(int)(value & 0x7FL | 0x80L));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeFixed32(int value) {
/* 2929 */       UnsafeUtil.putByte(this.pos--, (byte)(value >> 24 & 0xFF));
/* 2930 */       UnsafeUtil.putByte(this.pos--, (byte)(value >> 16 & 0xFF));
/* 2931 */       UnsafeUtil.putByte(this.pos--, (byte)(value >> 8 & 0xFF));
/* 2932 */       UnsafeUtil.putByte(this.pos--, (byte)(value & 0xFF));
/*      */     }
/*      */ 
/*      */     
/*      */     void writeFixed64(long value) {
/* 2937 */       UnsafeUtil.putByte(this.pos--, (byte)((int)(value >> 56L) & 0xFF));
/* 2938 */       UnsafeUtil.putByte(this.pos--, (byte)((int)(value >> 48L) & 0xFF));
/* 2939 */       UnsafeUtil.putByte(this.pos--, (byte)((int)(value >> 40L) & 0xFF));
/* 2940 */       UnsafeUtil.putByte(this.pos--, (byte)((int)(value >> 32L) & 0xFF));
/* 2941 */       UnsafeUtil.putByte(this.pos--, (byte)((int)(value >> 24L) & 0xFF));
/* 2942 */       UnsafeUtil.putByte(this.pos--, (byte)((int)(value >> 16L) & 0xFF));
/* 2943 */       UnsafeUtil.putByte(this.pos--, (byte)((int)(value >> 8L) & 0xFF));
/* 2944 */       UnsafeUtil.putByte(this.pos--, (byte)((int)value & 0xFF));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     void writeString(String in) {
/* 2950 */       requireSpace(in.length());
/*      */ 
/*      */       
/* 2953 */       int i = in.length() - 1;
/*      */       
/*      */       char c;
/* 2956 */       for (; i >= 0 && (c = in.charAt(i)) < ''; i--) {
/* 2957 */         UnsafeUtil.putByte(this.pos--, (byte)c);
/*      */       }
/* 2959 */       if (i == -1) {
/*      */         return;
/*      */       }
/*      */       
/* 2963 */       for (; i >= 0; i--) {
/* 2964 */         c = in.charAt(i);
/*      */         
/* 2966 */         UnsafeUtil.putByte(this.pos--, (byte)c);
/*      */         
/* 2968 */         UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & c));
/* 2969 */         UnsafeUtil.putByte(this.pos--, (byte)(0x3C0 | c >>> 6));
/*      */ 
/*      */ 
/*      */         
/* 2973 */         UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & c));
/* 2974 */         UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & c >>> 6));
/* 2975 */         UnsafeUtil.putByte(this.pos--, (byte)(0x1E0 | c >>> 12));
/*      */ 
/*      */         
/*      */         char high;
/*      */         
/* 2980 */         if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c)) {
/* 2981 */           throw new Utf8.UnpairedSurrogateException(i - 1, i);
/*      */         }
/* 2983 */         i--;
/* 2984 */         int codePoint = Character.toCodePoint(high, c);
/* 2985 */         UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & codePoint));
/* 2986 */         UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 6));
/* 2987 */         UnsafeUtil.putByte(this.pos--, (byte)(0x80 | 0x3F & codePoint >>> 12));
/* 2988 */         UnsafeUtil.putByte(this.pos--, (byte)(0xF0 | codePoint >>> 18));
/*      */ 
/*      */         
/* 2991 */         requireSpace(i);
/* 2992 */         i++;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void write(byte value) {
/* 2999 */       UnsafeUtil.putByte(this.pos--, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(byte[] value, int offset, int length) {
/* 3004 */       if (spaceLeft() < length) {
/* 3005 */         nextBuffer(length);
/*      */       }
/*      */       
/* 3008 */       this.pos -= length;
/* 3009 */       this.buffer.position(bufferPos() + 1);
/* 3010 */       this.buffer.put(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(byte[] value, int offset, int length) {
/* 3015 */       if (spaceLeft() < length) {
/*      */ 
/*      */         
/* 3018 */         this.totalDoneBytes += length;
/* 3019 */         this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
/*      */ 
/*      */ 
/*      */         
/* 3023 */         nextBuffer();
/*      */         
/*      */         return;
/*      */       } 
/* 3027 */       this.pos -= length;
/* 3028 */       this.buffer.position(bufferPos() + 1);
/* 3029 */       this.buffer.put(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(ByteBuffer value) {
/* 3034 */       int length = value.remaining();
/* 3035 */       if (spaceLeft() < length) {
/* 3036 */         nextBuffer(length);
/*      */       }
/*      */       
/* 3039 */       this.pos -= length;
/* 3040 */       this.buffer.position(bufferPos() + 1);
/* 3041 */       this.buffer.put(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(ByteBuffer value) {
/* 3046 */       int length = value.remaining();
/* 3047 */       if (spaceLeft() < length) {
/*      */ 
/*      */         
/* 3050 */         this.totalDoneBytes += length;
/* 3051 */         this.buffers.addFirst(AllocatedBuffer.wrap(value));
/*      */ 
/*      */ 
/*      */         
/* 3055 */         nextBuffer();
/*      */         
/*      */         return;
/*      */       } 
/* 3059 */       this.pos -= length;
/* 3060 */       this.buffer.position(bufferPos() + 1);
/* 3061 */       this.buffer.put(value);
/*      */     }
/*      */ 
/*      */     
/*      */     void requireSpace(int size) {
/* 3066 */       if (spaceLeft() < size)
/* 3067 */         nextBuffer(size); 
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\BinaryWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */