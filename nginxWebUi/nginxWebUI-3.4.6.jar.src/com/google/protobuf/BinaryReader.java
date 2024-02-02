/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ abstract class BinaryReader
/*      */   implements Reader
/*      */ {
/*      */   private static final int FIXED32_MULTIPLE_MASK = 3;
/*      */   private static final int FIXED64_MULTIPLE_MASK = 7;
/*      */   
/*      */   public static BinaryReader newInstance(ByteBuffer buffer, boolean bufferIsImmutable) {
/*   68 */     if (buffer.hasArray())
/*      */     {
/*   70 */       return new SafeHeapReader(buffer, bufferIsImmutable);
/*      */     }
/*      */     
/*   73 */     throw new IllegalArgumentException("Direct buffers not yet supported");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private BinaryReader() {}
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean shouldDiscardUnknownFields() {
/*   84 */     return false;
/*      */   }
/*      */   
/*      */   public abstract int getTotalBytesRead();
/*      */   
/*      */   private static final class SafeHeapReader
/*      */     extends BinaryReader
/*      */   {
/*      */     private final boolean bufferIsImmutable;
/*      */     private final byte[] buffer;
/*      */     private int pos;
/*      */     private final int initialPos;
/*      */     private int limit;
/*      */     private int tag;
/*      */     private int endGroupTag;
/*      */     
/*      */     public SafeHeapReader(ByteBuffer bytebuf, boolean bufferIsImmutable) {
/*  101 */       this.bufferIsImmutable = bufferIsImmutable;
/*  102 */       this.buffer = bytebuf.array();
/*  103 */       this.initialPos = this.pos = bytebuf.arrayOffset() + bytebuf.position();
/*  104 */       this.limit = bytebuf.arrayOffset() + bytebuf.limit();
/*      */     }
/*      */     
/*      */     private boolean isAtEnd() {
/*  108 */       return (this.pos == this.limit);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getTotalBytesRead() {
/*  113 */       return this.pos - this.initialPos;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getFieldNumber() throws IOException {
/*  118 */       if (isAtEnd()) {
/*  119 */         return Integer.MAX_VALUE;
/*      */       }
/*  121 */       this.tag = readVarint32();
/*  122 */       if (this.tag == this.endGroupTag) {
/*  123 */         return Integer.MAX_VALUE;
/*      */       }
/*  125 */       return WireFormat.getTagFieldNumber(this.tag);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getTag() {
/*  130 */       return this.tag;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean skipField() throws IOException {
/*  135 */       if (isAtEnd() || this.tag == this.endGroupTag) {
/*  136 */         return false;
/*      */       }
/*      */       
/*  139 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 0:
/*  141 */           skipVarint();
/*  142 */           return true;
/*      */         case 1:
/*  144 */           skipBytes(8);
/*  145 */           return true;
/*      */         case 2:
/*  147 */           skipBytes(readVarint32());
/*  148 */           return true;
/*      */         case 5:
/*  150 */           skipBytes(4);
/*  151 */           return true;
/*      */         case 3:
/*  153 */           skipGroup();
/*  154 */           return true;
/*      */       } 
/*  156 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public double readDouble() throws IOException {
/*  162 */       requireWireType(1);
/*  163 */       return Double.longBitsToDouble(readLittleEndian64());
/*      */     }
/*      */ 
/*      */     
/*      */     public float readFloat() throws IOException {
/*  168 */       requireWireType(5);
/*  169 */       return Float.intBitsToFloat(readLittleEndian32());
/*      */     }
/*      */ 
/*      */     
/*      */     public long readUInt64() throws IOException {
/*  174 */       requireWireType(0);
/*  175 */       return readVarint64();
/*      */     }
/*      */ 
/*      */     
/*      */     public long readInt64() throws IOException {
/*  180 */       requireWireType(0);
/*  181 */       return readVarint64();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readInt32() throws IOException {
/*  186 */       requireWireType(0);
/*  187 */       return readVarint32();
/*      */     }
/*      */ 
/*      */     
/*      */     public long readFixed64() throws IOException {
/*  192 */       requireWireType(1);
/*  193 */       return readLittleEndian64();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readFixed32() throws IOException {
/*  198 */       requireWireType(5);
/*  199 */       return readLittleEndian32();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean readBool() throws IOException {
/*  204 */       requireWireType(0);
/*  205 */       return (readVarint32() != 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public String readString() throws IOException {
/*  210 */       return readStringInternal(false);
/*      */     }
/*      */ 
/*      */     
/*      */     public String readStringRequireUtf8() throws IOException {
/*  215 */       return readStringInternal(true);
/*      */     }
/*      */     
/*      */     public String readStringInternal(boolean requireUtf8) throws IOException {
/*  219 */       requireWireType(2);
/*  220 */       int size = readVarint32();
/*  221 */       if (size == 0) {
/*  222 */         return "";
/*      */       }
/*      */       
/*  225 */       requireBytes(size);
/*  226 */       if (requireUtf8 && !Utf8.isValidUtf8(this.buffer, this.pos, this.pos + size)) {
/*  227 */         throw InvalidProtocolBufferException.invalidUtf8();
/*      */       }
/*  229 */       String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
/*  230 */       this.pos += size;
/*  231 */       return result;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public <T> T readMessage(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  237 */       requireWireType(2);
/*  238 */       return readMessage(Protobuf.getInstance().schemaFor(clazz), extensionRegistry);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public <T> T readMessageBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  244 */       requireWireType(2);
/*  245 */       return readMessage(schema, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     private <T> T readMessage(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  250 */       int size = readVarint32();
/*  251 */       requireBytes(size);
/*      */ 
/*      */       
/*  254 */       int prevLimit = this.limit;
/*  255 */       int newLimit = this.pos + size;
/*  256 */       this.limit = newLimit;
/*      */ 
/*      */       
/*      */       try {
/*  260 */         T message = schema.newInstance();
/*  261 */         schema.mergeFrom(message, this, extensionRegistry);
/*  262 */         schema.makeImmutable(message);
/*      */         
/*  264 */         if (this.pos != newLimit) {
/*  265 */           throw InvalidProtocolBufferException.parseFailure();
/*      */         }
/*  267 */         return message;
/*      */       } finally {
/*      */         
/*  270 */         this.limit = prevLimit;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public <T> T readGroup(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  277 */       requireWireType(3);
/*  278 */       return readGroup(Protobuf.getInstance().schemaFor(clazz), extensionRegistry);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public <T> T readGroupBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  284 */       requireWireType(3);
/*  285 */       return readGroup(schema, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     private <T> T readGroup(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  290 */       int prevEndGroupTag = this.endGroupTag;
/*  291 */       this.endGroupTag = WireFormat.makeTag(WireFormat.getTagFieldNumber(this.tag), 4);
/*      */ 
/*      */       
/*      */       try {
/*  295 */         T message = schema.newInstance();
/*  296 */         schema.mergeFrom(message, this, extensionRegistry);
/*  297 */         schema.makeImmutable(message);
/*      */         
/*  299 */         if (this.tag != this.endGroupTag) {
/*  300 */           throw InvalidProtocolBufferException.parseFailure();
/*      */         }
/*  302 */         return message;
/*      */       } finally {
/*      */         
/*  305 */         this.endGroupTag = prevEndGroupTag;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public ByteString readBytes() throws IOException {
/*  311 */       requireWireType(2);
/*  312 */       int size = readVarint32();
/*  313 */       if (size == 0) {
/*  314 */         return ByteString.EMPTY;
/*      */       }
/*      */       
/*  317 */       requireBytes(size);
/*      */ 
/*      */ 
/*      */       
/*  321 */       ByteString bytes = this.bufferIsImmutable ? ByteString.wrap(this.buffer, this.pos, size) : ByteString.copyFrom(this.buffer, this.pos, size);
/*  322 */       this.pos += size;
/*  323 */       return bytes;
/*      */     }
/*      */ 
/*      */     
/*      */     public int readUInt32() throws IOException {
/*  328 */       requireWireType(0);
/*  329 */       return readVarint32();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readEnum() throws IOException {
/*  334 */       requireWireType(0);
/*  335 */       return readVarint32();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readSFixed32() throws IOException {
/*  340 */       requireWireType(5);
/*  341 */       return readLittleEndian32();
/*      */     }
/*      */ 
/*      */     
/*      */     public long readSFixed64() throws IOException {
/*  346 */       requireWireType(1);
/*  347 */       return readLittleEndian64();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readSInt32() throws IOException {
/*  352 */       requireWireType(0);
/*  353 */       return CodedInputStream.decodeZigZag32(readVarint32());
/*      */     }
/*      */ 
/*      */     
/*      */     public long readSInt64() throws IOException {
/*  358 */       requireWireType(0);
/*  359 */       return CodedInputStream.decodeZigZag64(readVarint64()); } public void readDoubleList(List<Double> target) throws IOException {
/*      */       int bytes;
/*      */       int fieldEndPos;
/*      */       int prevPos;
/*      */       int nextTag;
/*  364 */       if (target instanceof DoubleArrayList) {
/*  365 */         int i, j, k, m; DoubleArrayList plist = (DoubleArrayList)target;
/*  366 */         switch (WireFormat.getTagWireType(this.tag)) {
/*      */           case 2:
/*  368 */             i = readVarint32();
/*  369 */             verifyPackedFixed64Length(i);
/*  370 */             j = this.pos + i;
/*  371 */             while (this.pos < j) {
/*  372 */               plist.addDouble(Double.longBitsToDouble(readLittleEndian64_NoCheck()));
/*      */             }
/*      */             return;
/*      */           case 1:
/*      */             do {
/*  377 */               plist.addDouble(readDouble());
/*      */               
/*  379 */               if (isAtEnd()) {
/*      */                 return;
/*      */               }
/*  382 */               k = this.pos;
/*  383 */               m = readVarint32();
/*  384 */             } while (m == this.tag);
/*      */ 
/*      */             
/*  387 */             this.pos = k;
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/*  392 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       } 
/*      */       
/*  395 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  397 */           bytes = readVarint32();
/*  398 */           verifyPackedFixed64Length(bytes);
/*  399 */           fieldEndPos = this.pos + bytes;
/*  400 */           while (this.pos < fieldEndPos) {
/*  401 */             target.add(Double.valueOf(Double.longBitsToDouble(readLittleEndian64_NoCheck())));
/*      */           }
/*      */           return;
/*      */         case 1:
/*      */           do {
/*  406 */             target.add(Double.valueOf(readDouble()));
/*      */             
/*  408 */             if (isAtEnd()) {
/*      */               return;
/*      */             }
/*  411 */             prevPos = this.pos;
/*  412 */             nextTag = readVarint32();
/*  413 */           } while (nextTag == this.tag);
/*      */ 
/*      */           
/*  416 */           this.pos = prevPos;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  421 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */     public void readFloatList(List<Float> target) throws IOException {
/*      */       int bytes;
/*      */       int fieldEndPos;
/*      */       int prevPos;
/*      */       int nextTag;
/*  428 */       if (target instanceof FloatArrayList) {
/*  429 */         int i, j, k, m; FloatArrayList plist = (FloatArrayList)target;
/*  430 */         switch (WireFormat.getTagWireType(this.tag)) {
/*      */           case 2:
/*  432 */             i = readVarint32();
/*  433 */             verifyPackedFixed32Length(i);
/*  434 */             j = this.pos + i;
/*  435 */             while (this.pos < j) {
/*  436 */               plist.addFloat(Float.intBitsToFloat(readLittleEndian32_NoCheck()));
/*      */             }
/*      */             return;
/*      */           case 5:
/*      */             do {
/*  441 */               plist.addFloat(readFloat());
/*      */               
/*  443 */               if (isAtEnd()) {
/*      */                 return;
/*      */               }
/*  446 */               k = this.pos;
/*  447 */               m = readVarint32();
/*  448 */             } while (m == this.tag);
/*      */ 
/*      */             
/*  451 */             this.pos = k;
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/*  456 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       } 
/*      */       
/*  459 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  461 */           bytes = readVarint32();
/*  462 */           verifyPackedFixed32Length(bytes);
/*  463 */           fieldEndPos = this.pos + bytes;
/*  464 */           while (this.pos < fieldEndPos) {
/*  465 */             target.add(Float.valueOf(Float.intBitsToFloat(readLittleEndian32_NoCheck())));
/*      */           }
/*      */           return;
/*      */         case 5:
/*      */           do {
/*  470 */             target.add(Float.valueOf(readFloat()));
/*      */             
/*  472 */             if (isAtEnd()) {
/*      */               return;
/*      */             }
/*  475 */             prevPos = this.pos;
/*  476 */             nextTag = readVarint32();
/*  477 */           } while (nextTag == this.tag);
/*      */ 
/*      */           
/*  480 */           this.pos = prevPos;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  485 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */     public void readUInt64List(List<Long> target) throws IOException {
/*      */       int bytes;
/*      */       int fieldEndPos;
/*      */       int prevPos;
/*      */       int nextTag;
/*  492 */       if (target instanceof LongArrayList) {
/*  493 */         int i, j, k, m; LongArrayList plist = (LongArrayList)target;
/*  494 */         switch (WireFormat.getTagWireType(this.tag)) {
/*      */           case 2:
/*  496 */             i = readVarint32();
/*  497 */             j = this.pos + i;
/*  498 */             while (this.pos < j) {
/*  499 */               plist.addLong(readVarint64());
/*      */             }
/*  501 */             requirePosition(j);
/*      */             return;
/*      */           case 0:
/*      */             do {
/*  505 */               plist.addLong(readUInt64());
/*      */               
/*  507 */               if (isAtEnd()) {
/*      */                 return;
/*      */               }
/*  510 */               k = this.pos;
/*  511 */               m = readVarint32();
/*  512 */             } while (m == this.tag);
/*      */ 
/*      */             
/*  515 */             this.pos = k;
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/*  520 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       } 
/*      */       
/*  523 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  525 */           bytes = readVarint32();
/*  526 */           fieldEndPos = this.pos + bytes;
/*  527 */           while (this.pos < fieldEndPos) {
/*  528 */             target.add(Long.valueOf(readVarint64()));
/*      */           }
/*  530 */           requirePosition(fieldEndPos);
/*      */           return;
/*      */         case 0:
/*      */           do {
/*  534 */             target.add(Long.valueOf(readUInt64()));
/*      */             
/*  536 */             if (isAtEnd()) {
/*      */               return;
/*      */             }
/*  539 */             prevPos = this.pos;
/*  540 */             nextTag = readVarint32();
/*  541 */           } while (nextTag == this.tag);
/*      */ 
/*      */           
/*  544 */           this.pos = prevPos;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  549 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */     public void readInt64List(List<Long> target) throws IOException {
/*      */       int bytes;
/*      */       int fieldEndPos;
/*      */       int prevPos;
/*      */       int nextTag;
/*  556 */       if (target instanceof LongArrayList) {
/*  557 */         int i, j, k, m; LongArrayList plist = (LongArrayList)target;
/*  558 */         switch (WireFormat.getTagWireType(this.tag)) {
/*      */           case 2:
/*  560 */             i = readVarint32();
/*  561 */             j = this.pos + i;
/*  562 */             while (this.pos < j) {
/*  563 */               plist.addLong(readVarint64());
/*      */             }
/*  565 */             requirePosition(j);
/*      */             return;
/*      */           case 0:
/*      */             do {
/*  569 */               plist.addLong(readInt64());
/*      */               
/*  571 */               if (isAtEnd()) {
/*      */                 return;
/*      */               }
/*  574 */               k = this.pos;
/*  575 */               m = readVarint32();
/*  576 */             } while (m == this.tag);
/*      */ 
/*      */             
/*  579 */             this.pos = k;
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/*  584 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       } 
/*      */       
/*  587 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  589 */           bytes = readVarint32();
/*  590 */           fieldEndPos = this.pos + bytes;
/*  591 */           while (this.pos < fieldEndPos) {
/*  592 */             target.add(Long.valueOf(readVarint64()));
/*      */           }
/*  594 */           requirePosition(fieldEndPos);
/*      */           return;
/*      */         case 0:
/*      */           do {
/*  598 */             target.add(Long.valueOf(readInt64()));
/*      */             
/*  600 */             if (isAtEnd()) {
/*      */               return;
/*      */             }
/*  603 */             prevPos = this.pos;
/*  604 */             nextTag = readVarint32();
/*  605 */           } while (nextTag == this.tag);
/*      */ 
/*      */           
/*  608 */           this.pos = prevPos;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  613 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */     public void readInt32List(List<Integer> target) throws IOException {
/*      */       int bytes;
/*      */       int fieldEndPos;
/*      */       int prevPos;
/*      */       int nextTag;
/*  620 */       if (target instanceof IntArrayList) {
/*  621 */         int i, j, k, m; IntArrayList plist = (IntArrayList)target;
/*  622 */         switch (WireFormat.getTagWireType(this.tag)) {
/*      */           case 2:
/*  624 */             i = readVarint32();
/*  625 */             j = this.pos + i;
/*  626 */             while (this.pos < j) {
/*  627 */               plist.addInt(readVarint32());
/*      */             }
/*  629 */             requirePosition(j);
/*      */             return;
/*      */           case 0:
/*      */             do {
/*  633 */               plist.addInt(readInt32());
/*      */               
/*  635 */               if (isAtEnd()) {
/*      */                 return;
/*      */               }
/*  638 */               k = this.pos;
/*  639 */               m = readVarint32();
/*  640 */             } while (m == this.tag);
/*      */ 
/*      */             
/*  643 */             this.pos = k;
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/*  648 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       } 
/*      */       
/*  651 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  653 */           bytes = readVarint32();
/*  654 */           fieldEndPos = this.pos + bytes;
/*  655 */           while (this.pos < fieldEndPos) {
/*  656 */             target.add(Integer.valueOf(readVarint32()));
/*      */           }
/*  658 */           requirePosition(fieldEndPos);
/*      */           return;
/*      */         case 0:
/*      */           do {
/*  662 */             target.add(Integer.valueOf(readInt32()));
/*      */             
/*  664 */             if (isAtEnd()) {
/*      */               return;
/*      */             }
/*  667 */             prevPos = this.pos;
/*  668 */             nextTag = readVarint32();
/*  669 */           } while (nextTag == this.tag);
/*      */ 
/*      */           
/*  672 */           this.pos = prevPos;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  677 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */     public void readFixed64List(List<Long> target) throws IOException {
/*      */       int bytes;
/*      */       int fieldEndPos;
/*      */       int prevPos;
/*      */       int nextTag;
/*  684 */       if (target instanceof LongArrayList) {
/*  685 */         int i, j, k, m; LongArrayList plist = (LongArrayList)target;
/*  686 */         switch (WireFormat.getTagWireType(this.tag)) {
/*      */           case 2:
/*  688 */             i = readVarint32();
/*  689 */             verifyPackedFixed64Length(i);
/*  690 */             j = this.pos + i;
/*  691 */             while (this.pos < j) {
/*  692 */               plist.addLong(readLittleEndian64_NoCheck());
/*      */             }
/*      */             return;
/*      */           case 1:
/*      */             do {
/*  697 */               plist.addLong(readFixed64());
/*      */               
/*  699 */               if (isAtEnd()) {
/*      */                 return;
/*      */               }
/*  702 */               k = this.pos;
/*  703 */               m = readVarint32();
/*  704 */             } while (m == this.tag);
/*      */ 
/*      */             
/*  707 */             this.pos = k;
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/*  712 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       } 
/*      */       
/*  715 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  717 */           bytes = readVarint32();
/*  718 */           verifyPackedFixed64Length(bytes);
/*  719 */           fieldEndPos = this.pos + bytes;
/*  720 */           while (this.pos < fieldEndPos) {
/*  721 */             target.add(Long.valueOf(readLittleEndian64_NoCheck()));
/*      */           }
/*      */           return;
/*      */         case 1:
/*      */           do {
/*  726 */             target.add(Long.valueOf(readFixed64()));
/*      */             
/*  728 */             if (isAtEnd()) {
/*      */               return;
/*      */             }
/*  731 */             prevPos = this.pos;
/*  732 */             nextTag = readVarint32();
/*  733 */           } while (nextTag == this.tag);
/*      */ 
/*      */           
/*  736 */           this.pos = prevPos;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  741 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */     public void readFixed32List(List<Integer> target) throws IOException {
/*      */       int bytes;
/*      */       int fieldEndPos;
/*      */       int prevPos;
/*      */       int nextTag;
/*  748 */       if (target instanceof IntArrayList) {
/*  749 */         int i, j, k, m; IntArrayList plist = (IntArrayList)target;
/*  750 */         switch (WireFormat.getTagWireType(this.tag)) {
/*      */           case 2:
/*  752 */             i = readVarint32();
/*  753 */             verifyPackedFixed32Length(i);
/*  754 */             j = this.pos + i;
/*  755 */             while (this.pos < j) {
/*  756 */               plist.addInt(readLittleEndian32_NoCheck());
/*      */             }
/*      */             return;
/*      */           case 5:
/*      */             do {
/*  761 */               plist.addInt(readFixed32());
/*      */               
/*  763 */               if (isAtEnd()) {
/*      */                 return;
/*      */               }
/*  766 */               k = this.pos;
/*  767 */               m = readVarint32();
/*  768 */             } while (m == this.tag);
/*      */ 
/*      */             
/*  771 */             this.pos = k;
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/*  776 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       } 
/*      */       
/*  779 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  781 */           bytes = readVarint32();
/*  782 */           verifyPackedFixed32Length(bytes);
/*  783 */           fieldEndPos = this.pos + bytes;
/*  784 */           while (this.pos < fieldEndPos) {
/*  785 */             target.add(Integer.valueOf(readLittleEndian32_NoCheck()));
/*      */           }
/*      */           return;
/*      */         case 5:
/*      */           do {
/*  790 */             target.add(Integer.valueOf(readFixed32()));
/*      */             
/*  792 */             if (isAtEnd()) {
/*      */               return;
/*      */             }
/*  795 */             prevPos = this.pos;
/*  796 */             nextTag = readVarint32();
/*  797 */           } while (nextTag == this.tag);
/*      */ 
/*      */           
/*  800 */           this.pos = prevPos;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  805 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */     public void readBoolList(List<Boolean> target) throws IOException {
/*      */       int bytes;
/*      */       int fieldEndPos;
/*      */       int prevPos;
/*      */       int nextTag;
/*  812 */       if (target instanceof BooleanArrayList) {
/*  813 */         int i, j, k, m; BooleanArrayList plist = (BooleanArrayList)target;
/*  814 */         switch (WireFormat.getTagWireType(this.tag)) {
/*      */           case 2:
/*  816 */             i = readVarint32();
/*  817 */             j = this.pos + i;
/*  818 */             while (this.pos < j) {
/*  819 */               plist.addBoolean((readVarint32() != 0));
/*      */             }
/*  821 */             requirePosition(j);
/*      */             return;
/*      */           case 0:
/*      */             do {
/*  825 */               plist.addBoolean(readBool());
/*      */               
/*  827 */               if (isAtEnd()) {
/*      */                 return;
/*      */               }
/*  830 */               k = this.pos;
/*  831 */               m = readVarint32();
/*  832 */             } while (m == this.tag);
/*      */ 
/*      */             
/*  835 */             this.pos = k;
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/*  840 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       } 
/*      */       
/*  843 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  845 */           bytes = readVarint32();
/*  846 */           fieldEndPos = this.pos + bytes;
/*  847 */           while (this.pos < fieldEndPos) {
/*  848 */             target.add(Boolean.valueOf((readVarint32() != 0)));
/*      */           }
/*  850 */           requirePosition(fieldEndPos);
/*      */           return;
/*      */         case 0:
/*      */           do {
/*  854 */             target.add(Boolean.valueOf(readBool()));
/*      */             
/*  856 */             if (isAtEnd()) {
/*      */               return;
/*      */             }
/*  859 */             prevPos = this.pos;
/*  860 */             nextTag = readVarint32();
/*  861 */           } while (nextTag == this.tag);
/*      */ 
/*      */           
/*  864 */           this.pos = prevPos;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  869 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void readStringList(List<String> target) throws IOException {
/*  876 */       readStringListInternal(target, false);
/*      */     }
/*      */ 
/*      */     
/*      */     public void readStringListRequireUtf8(List<String> target) throws IOException {
/*  881 */       readStringListInternal(target, true);
/*      */     }
/*      */     
/*      */     public void readStringListInternal(List<String> target, boolean requireUtf8) throws IOException {
/*      */       int prevPos, nextTag;
/*  886 */       if (WireFormat.getTagWireType(this.tag) != 2) {
/*  887 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       }
/*      */       
/*  890 */       if (target instanceof LazyStringList && !requireUtf8) {
/*  891 */         int i, j; LazyStringList lazyList = (LazyStringList)target;
/*      */         do {
/*  893 */           lazyList.add(readBytes());
/*      */           
/*  895 */           if (isAtEnd()) {
/*      */             return;
/*      */           }
/*  898 */           i = this.pos;
/*  899 */           j = readVarint32();
/*  900 */         } while (j == this.tag);
/*      */ 
/*      */         
/*  903 */         this.pos = i;
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/*      */       do {
/*  909 */         target.add(readStringInternal(requireUtf8));
/*      */         
/*  911 */         if (isAtEnd()) {
/*      */           return;
/*      */         }
/*  914 */         prevPos = this.pos;
/*  915 */         nextTag = readVarint32();
/*  916 */       } while (nextTag == this.tag);
/*      */ 
/*      */       
/*  919 */       this.pos = prevPos;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T> void readMessageList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  930 */       Schema<T> schema = Protobuf.getInstance().schemaFor(targetType);
/*  931 */       readMessageList(target, schema, extensionRegistry);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public <T> void readMessageList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
/*      */       int prevPos, nextTag;
/*  938 */       if (WireFormat.getTagWireType(this.tag) != 2) {
/*  939 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       }
/*  941 */       int listTag = this.tag;
/*      */       do {
/*  943 */         target.add(readMessage(schema, extensionRegistry));
/*      */         
/*  945 */         if (isAtEnd()) {
/*      */           return;
/*      */         }
/*  948 */         prevPos = this.pos;
/*  949 */         nextTag = readVarint32();
/*  950 */       } while (nextTag == listTag);
/*      */ 
/*      */       
/*  953 */       this.pos = prevPos;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T> void readGroupList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  963 */       Schema<T> schema = Protobuf.getInstance().schemaFor(targetType);
/*  964 */       readGroupList(target, schema, extensionRegistry);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public <T> void readGroupList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
/*      */       int prevPos, nextTag;
/*  971 */       if (WireFormat.getTagWireType(this.tag) != 3) {
/*  972 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       }
/*  974 */       int listTag = this.tag;
/*      */       do {
/*  976 */         target.add(readGroup(schema, extensionRegistry));
/*      */         
/*  978 */         if (isAtEnd()) {
/*      */           return;
/*      */         }
/*  981 */         prevPos = this.pos;
/*  982 */         nextTag = readVarint32();
/*  983 */       } while (nextTag == listTag);
/*      */ 
/*      */       
/*  986 */       this.pos = prevPos;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void readBytesList(List<ByteString> target) throws IOException {
/*      */       int prevPos, nextTag;
/*  994 */       if (WireFormat.getTagWireType(this.tag) != 2) {
/*  995 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       }
/*      */       
/*      */       do {
/*  999 */         target.add(readBytes());
/*      */         
/* 1001 */         if (isAtEnd()) {
/*      */           return;
/*      */         }
/* 1004 */         prevPos = this.pos;
/* 1005 */         nextTag = readVarint32();
/* 1006 */       } while (nextTag == this.tag);
/*      */ 
/*      */       
/* 1009 */       this.pos = prevPos;
/*      */     }
/*      */     
/*      */     public void readUInt32List(List<Integer> target) throws IOException {
/*      */       int bytes;
/*      */       int fieldEndPos;
/*      */       int prevPos;
/*      */       int nextTag;
/* 1017 */       if (target instanceof IntArrayList) {
/* 1018 */         int i, j, k, m; IntArrayList plist = (IntArrayList)target;
/* 1019 */         switch (WireFormat.getTagWireType(this.tag)) {
/*      */           case 2:
/* 1021 */             i = readVarint32();
/* 1022 */             j = this.pos + i;
/* 1023 */             while (this.pos < j) {
/* 1024 */               plist.addInt(readVarint32());
/*      */             }
/*      */             return;
/*      */           case 0:
/*      */             do {
/* 1029 */               plist.addInt(readUInt32());
/*      */               
/* 1031 */               if (isAtEnd()) {
/*      */                 return;
/*      */               }
/* 1034 */               k = this.pos;
/* 1035 */               m = readVarint32();
/* 1036 */             } while (m == this.tag);
/*      */ 
/*      */             
/* 1039 */             this.pos = k;
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/* 1044 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       } 
/*      */       
/* 1047 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/* 1049 */           bytes = readVarint32();
/* 1050 */           fieldEndPos = this.pos + bytes;
/* 1051 */           while (this.pos < fieldEndPos) {
/* 1052 */             target.add(Integer.valueOf(readVarint32()));
/*      */           }
/*      */           return;
/*      */         case 0:
/*      */           do {
/* 1057 */             target.add(Integer.valueOf(readUInt32()));
/*      */             
/* 1059 */             if (isAtEnd()) {
/*      */               return;
/*      */             }
/* 1062 */             prevPos = this.pos;
/* 1063 */             nextTag = readVarint32();
/* 1064 */           } while (nextTag == this.tag);
/*      */ 
/*      */           
/* 1067 */           this.pos = prevPos;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/* 1072 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */     public void readEnumList(List<Integer> target) throws IOException {
/*      */       int bytes;
/*      */       int fieldEndPos;
/*      */       int prevPos;
/*      */       int nextTag;
/* 1079 */       if (target instanceof IntArrayList) {
/* 1080 */         int i, j, k, m; IntArrayList plist = (IntArrayList)target;
/* 1081 */         switch (WireFormat.getTagWireType(this.tag)) {
/*      */           case 2:
/* 1083 */             i = readVarint32();
/* 1084 */             j = this.pos + i;
/* 1085 */             while (this.pos < j) {
/* 1086 */               plist.addInt(readVarint32());
/*      */             }
/*      */             return;
/*      */           case 0:
/*      */             do {
/* 1091 */               plist.addInt(readEnum());
/*      */               
/* 1093 */               if (isAtEnd()) {
/*      */                 return;
/*      */               }
/* 1096 */               k = this.pos;
/* 1097 */               m = readVarint32();
/* 1098 */             } while (m == this.tag);
/*      */ 
/*      */             
/* 1101 */             this.pos = k;
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/* 1106 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       } 
/*      */       
/* 1109 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/* 1111 */           bytes = readVarint32();
/* 1112 */           fieldEndPos = this.pos + bytes;
/* 1113 */           while (this.pos < fieldEndPos) {
/* 1114 */             target.add(Integer.valueOf(readVarint32()));
/*      */           }
/*      */           return;
/*      */         case 0:
/*      */           do {
/* 1119 */             target.add(Integer.valueOf(readEnum()));
/*      */             
/* 1121 */             if (isAtEnd()) {
/*      */               return;
/*      */             }
/* 1124 */             prevPos = this.pos;
/* 1125 */             nextTag = readVarint32();
/* 1126 */           } while (nextTag == this.tag);
/*      */ 
/*      */           
/* 1129 */           this.pos = prevPos;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/* 1134 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */     public void readSFixed32List(List<Integer> target) throws IOException {
/*      */       int bytes;
/*      */       int fieldEndPos;
/*      */       int prevPos;
/*      */       int nextTag;
/* 1141 */       if (target instanceof IntArrayList) {
/* 1142 */         int i, j, k, m; IntArrayList plist = (IntArrayList)target;
/* 1143 */         switch (WireFormat.getTagWireType(this.tag)) {
/*      */           case 2:
/* 1145 */             i = readVarint32();
/* 1146 */             verifyPackedFixed32Length(i);
/* 1147 */             j = this.pos + i;
/* 1148 */             while (this.pos < j) {
/* 1149 */               plist.addInt(readLittleEndian32_NoCheck());
/*      */             }
/*      */             return;
/*      */           case 5:
/*      */             do {
/* 1154 */               plist.addInt(readSFixed32());
/*      */               
/* 1156 */               if (isAtEnd()) {
/*      */                 return;
/*      */               }
/* 1159 */               k = this.pos;
/* 1160 */               m = readVarint32();
/* 1161 */             } while (m == this.tag);
/*      */ 
/*      */             
/* 1164 */             this.pos = k;
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/* 1169 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       } 
/*      */       
/* 1172 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/* 1174 */           bytes = readVarint32();
/* 1175 */           verifyPackedFixed32Length(bytes);
/* 1176 */           fieldEndPos = this.pos + bytes;
/* 1177 */           while (this.pos < fieldEndPos) {
/* 1178 */             target.add(Integer.valueOf(readLittleEndian32_NoCheck()));
/*      */           }
/*      */           return;
/*      */         case 5:
/*      */           do {
/* 1183 */             target.add(Integer.valueOf(readSFixed32()));
/*      */             
/* 1185 */             if (isAtEnd()) {
/*      */               return;
/*      */             }
/* 1188 */             prevPos = this.pos;
/* 1189 */             nextTag = readVarint32();
/* 1190 */           } while (nextTag == this.tag);
/*      */ 
/*      */           
/* 1193 */           this.pos = prevPos;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/* 1198 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */     public void readSFixed64List(List<Long> target) throws IOException {
/*      */       int bytes;
/*      */       int fieldEndPos;
/*      */       int prevPos;
/*      */       int nextTag;
/* 1205 */       if (target instanceof LongArrayList) {
/* 1206 */         int i, j, k, m; LongArrayList plist = (LongArrayList)target;
/* 1207 */         switch (WireFormat.getTagWireType(this.tag)) {
/*      */           case 2:
/* 1209 */             i = readVarint32();
/* 1210 */             verifyPackedFixed64Length(i);
/* 1211 */             j = this.pos + i;
/* 1212 */             while (this.pos < j) {
/* 1213 */               plist.addLong(readLittleEndian64_NoCheck());
/*      */             }
/*      */             return;
/*      */           case 1:
/*      */             do {
/* 1218 */               plist.addLong(readSFixed64());
/*      */               
/* 1220 */               if (isAtEnd()) {
/*      */                 return;
/*      */               }
/* 1223 */               k = this.pos;
/* 1224 */               m = readVarint32();
/* 1225 */             } while (m == this.tag);
/*      */ 
/*      */             
/* 1228 */             this.pos = k;
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/* 1233 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       } 
/*      */       
/* 1236 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/* 1238 */           bytes = readVarint32();
/* 1239 */           verifyPackedFixed64Length(bytes);
/* 1240 */           fieldEndPos = this.pos + bytes;
/* 1241 */           while (this.pos < fieldEndPos) {
/* 1242 */             target.add(Long.valueOf(readLittleEndian64_NoCheck()));
/*      */           }
/*      */           return;
/*      */         case 1:
/*      */           do {
/* 1247 */             target.add(Long.valueOf(readSFixed64()));
/*      */             
/* 1249 */             if (isAtEnd()) {
/*      */               return;
/*      */             }
/* 1252 */             prevPos = this.pos;
/* 1253 */             nextTag = readVarint32();
/* 1254 */           } while (nextTag == this.tag);
/*      */ 
/*      */           
/* 1257 */           this.pos = prevPos;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/* 1262 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */     public void readSInt32List(List<Integer> target) throws IOException {
/*      */       int bytes;
/*      */       int fieldEndPos;
/*      */       int prevPos;
/*      */       int nextTag;
/* 1269 */       if (target instanceof IntArrayList) {
/* 1270 */         int i, j, k, m; IntArrayList plist = (IntArrayList)target;
/* 1271 */         switch (WireFormat.getTagWireType(this.tag)) {
/*      */           case 2:
/* 1273 */             i = readVarint32();
/* 1274 */             j = this.pos + i;
/* 1275 */             while (this.pos < j) {
/* 1276 */               plist.addInt(CodedInputStream.decodeZigZag32(readVarint32()));
/*      */             }
/*      */             return;
/*      */           case 0:
/*      */             do {
/* 1281 */               plist.addInt(readSInt32());
/*      */               
/* 1283 */               if (isAtEnd()) {
/*      */                 return;
/*      */               }
/* 1286 */               k = this.pos;
/* 1287 */               m = readVarint32();
/* 1288 */             } while (m == this.tag);
/*      */ 
/*      */             
/* 1291 */             this.pos = k;
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/* 1296 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       } 
/*      */       
/* 1299 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/* 1301 */           bytes = readVarint32();
/* 1302 */           fieldEndPos = this.pos + bytes;
/* 1303 */           while (this.pos < fieldEndPos) {
/* 1304 */             target.add(Integer.valueOf(CodedInputStream.decodeZigZag32(readVarint32())));
/*      */           }
/*      */           return;
/*      */         case 0:
/*      */           do {
/* 1309 */             target.add(Integer.valueOf(readSInt32()));
/*      */             
/* 1311 */             if (isAtEnd()) {
/*      */               return;
/*      */             }
/* 1314 */             prevPos = this.pos;
/* 1315 */             nextTag = readVarint32();
/* 1316 */           } while (nextTag == this.tag);
/*      */ 
/*      */           
/* 1319 */           this.pos = prevPos;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/* 1324 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */     public void readSInt64List(List<Long> target) throws IOException {
/*      */       int bytes;
/*      */       int fieldEndPos;
/*      */       int prevPos;
/*      */       int nextTag;
/* 1331 */       if (target instanceof LongArrayList) {
/* 1332 */         int i, j, k, m; LongArrayList plist = (LongArrayList)target;
/* 1333 */         switch (WireFormat.getTagWireType(this.tag)) {
/*      */           case 2:
/* 1335 */             i = readVarint32();
/* 1336 */             j = this.pos + i;
/* 1337 */             while (this.pos < j) {
/* 1338 */               plist.addLong(CodedInputStream.decodeZigZag64(readVarint64()));
/*      */             }
/*      */             return;
/*      */           case 0:
/*      */             do {
/* 1343 */               plist.addLong(readSInt64());
/*      */               
/* 1345 */               if (isAtEnd()) {
/*      */                 return;
/*      */               }
/* 1348 */               k = this.pos;
/* 1349 */               m = readVarint32();
/* 1350 */             } while (m == this.tag);
/*      */ 
/*      */             
/* 1353 */             this.pos = k;
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/* 1358 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       } 
/*      */       
/* 1361 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/* 1363 */           bytes = readVarint32();
/* 1364 */           fieldEndPos = this.pos + bytes;
/* 1365 */           while (this.pos < fieldEndPos) {
/* 1366 */             target.add(Long.valueOf(CodedInputStream.decodeZigZag64(readVarint64())));
/*      */           }
/*      */           return;
/*      */         case 0:
/*      */           do {
/* 1371 */             target.add(Long.valueOf(readSInt64()));
/*      */             
/* 1373 */             if (isAtEnd()) {
/*      */               return;
/*      */             }
/* 1376 */             prevPos = this.pos;
/* 1377 */             nextTag = readVarint32();
/* 1378 */           } while (nextTag == this.tag);
/*      */ 
/*      */           
/* 1381 */           this.pos = prevPos;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/* 1386 */       throw InvalidProtocolBufferException.invalidWireType();
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
/*      */     public <K, V> void readMap(Map<K, V> target, MapEntryLite.Metadata<K, V> metadata, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1398 */       requireWireType(2);
/* 1399 */       int size = readVarint32();
/* 1400 */       requireBytes(size);
/*      */ 
/*      */       
/* 1403 */       int prevLimit = this.limit;
/* 1404 */       int newLimit = this.pos + size;
/* 1405 */       this.limit = newLimit;
/*      */       
/*      */       try {
/* 1408 */         K key = metadata.defaultKey;
/* 1409 */         V value = metadata.defaultValue;
/*      */         while (true) {
/* 1411 */           int number = getFieldNumber();
/* 1412 */           if (number == Integer.MAX_VALUE) {
/*      */             break;
/*      */           }
/*      */           try {
/* 1416 */             switch (number) {
/*      */               case 1:
/* 1418 */                 key = (K)readField(metadata.keyType, null, null);
/*      */                 continue;
/*      */ 
/*      */               
/*      */               case 2:
/* 1423 */                 value = (V)readField(metadata.valueType, metadata.defaultValue
/*      */                     
/* 1425 */                     .getClass(), extensionRegistry);
/*      */                 continue;
/*      */             } 
/*      */             
/* 1429 */             if (!skipField()) {
/* 1430 */               throw new InvalidProtocolBufferException("Unable to parse map entry.");
/*      */             
/*      */             }
/*      */           }
/* 1434 */           catch (InvalidWireTypeException ignore) {
/*      */             
/* 1436 */             if (!skipField()) {
/* 1437 */               throw new InvalidProtocolBufferException("Unable to parse map entry.");
/*      */             }
/*      */           } 
/*      */         } 
/* 1441 */         target.put(key, value);
/*      */       } finally {
/*      */         
/* 1444 */         this.limit = prevLimit;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Object readField(WireFormat.FieldType fieldType, Class<?> messageType, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1453 */       switch (fieldType) {
/*      */         case BOOL:
/* 1455 */           return Boolean.valueOf(readBool());
/*      */         case BYTES:
/* 1457 */           return readBytes();
/*      */         case DOUBLE:
/* 1459 */           return Double.valueOf(readDouble());
/*      */         case ENUM:
/* 1461 */           return Integer.valueOf(readEnum());
/*      */         case FIXED32:
/* 1463 */           return Integer.valueOf(readFixed32());
/*      */         case FIXED64:
/* 1465 */           return Long.valueOf(readFixed64());
/*      */         case FLOAT:
/* 1467 */           return Float.valueOf(readFloat());
/*      */         case INT32:
/* 1469 */           return Integer.valueOf(readInt32());
/*      */         case INT64:
/* 1471 */           return Long.valueOf(readInt64());
/*      */         case MESSAGE:
/* 1473 */           return readMessage(messageType, extensionRegistry);
/*      */         case SFIXED32:
/* 1475 */           return Integer.valueOf(readSFixed32());
/*      */         case SFIXED64:
/* 1477 */           return Long.valueOf(readSFixed64());
/*      */         case SINT32:
/* 1479 */           return Integer.valueOf(readSInt32());
/*      */         case SINT64:
/* 1481 */           return Long.valueOf(readSInt64());
/*      */         case STRING:
/* 1483 */           return readStringRequireUtf8();
/*      */         case UINT32:
/* 1485 */           return Integer.valueOf(readUInt32());
/*      */         case UINT64:
/* 1487 */           return Long.valueOf(readUInt64());
/*      */       } 
/* 1489 */       throw new RuntimeException("unsupported field type.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int readVarint32() throws IOException {
/* 1496 */       int i = this.pos;
/*      */       
/* 1498 */       if (this.limit == this.pos) {
/* 1499 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/*      */       
/*      */       int x;
/* 1503 */       if ((x = this.buffer[i++]) >= 0) {
/* 1504 */         this.pos = i;
/* 1505 */         return x;
/* 1506 */       }  if (this.limit - i < 9)
/* 1507 */         return (int)readVarint64SlowPath(); 
/* 1508 */       if ((x ^= this.buffer[i++] << 7) < 0) {
/* 1509 */         x ^= 0xFFFFFF80;
/* 1510 */       } else if ((x ^= this.buffer[i++] << 14) >= 0) {
/* 1511 */         x ^= 0x3F80;
/* 1512 */       } else if ((x ^= this.buffer[i++] << 21) < 0) {
/* 1513 */         x ^= 0xFFE03F80;
/*      */       } else {
/* 1515 */         int y = this.buffer[i++];
/* 1516 */         x ^= y << 28;
/* 1517 */         x ^= 0xFE03F80;
/* 1518 */         if (y < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1524 */           throw InvalidProtocolBufferException.malformedVarint();
/*      */         }
/*      */       } 
/* 1527 */       this.pos = i;
/* 1528 */       return x;
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
/*      */     public long readVarint64() throws IOException {
/*      */       long x;
/* 1543 */       int i = this.pos;
/*      */       
/* 1545 */       if (this.limit == i) {
/* 1546 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/*      */       
/* 1549 */       byte[] buffer = this.buffer;
/*      */       
/*      */       int y;
/* 1552 */       if ((y = buffer[i++]) >= 0) {
/* 1553 */         this.pos = i;
/* 1554 */         return y;
/* 1555 */       }  if (this.limit - i < 9)
/* 1556 */         return readVarint64SlowPath(); 
/* 1557 */       if ((y ^= buffer[i++] << 7) < 0) {
/* 1558 */         x = (y ^ 0xFFFFFF80);
/* 1559 */       } else if ((y ^= buffer[i++] << 14) >= 0) {
/* 1560 */         x = (y ^ 0x3F80);
/* 1561 */       } else if ((y ^= buffer[i++] << 21) < 0) {
/* 1562 */         x = (y ^ 0xFFE03F80);
/* 1563 */       } else if ((x = y ^ buffer[i++] << 28L) >= 0L) {
/* 1564 */         x ^= 0xFE03F80L;
/* 1565 */       } else if ((x ^= buffer[i++] << 35L) < 0L) {
/* 1566 */         x ^= 0xFFFFFFF80FE03F80L;
/* 1567 */       } else if ((x ^= buffer[i++] << 42L) >= 0L) {
/* 1568 */         x ^= 0x3F80FE03F80L;
/* 1569 */       } else if ((x ^= buffer[i++] << 49L) < 0L) {
/* 1570 */         x ^= 0xFFFE03F80FE03F80L;
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */ 
/*      */         
/* 1579 */         x ^= buffer[i++] << 56L;
/* 1580 */         x ^= 0xFE03F80FE03F80L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1589 */         if (x < 0L && 
/* 1590 */           buffer[i++] < 0L) {
/* 1591 */           throw InvalidProtocolBufferException.malformedVarint();
/*      */         }
/*      */       } 
/*      */       
/* 1595 */       this.pos = i;
/* 1596 */       return x;
/*      */     }
/*      */     
/*      */     private long readVarint64SlowPath() throws IOException {
/* 1600 */       long result = 0L;
/* 1601 */       for (int shift = 0; shift < 64; shift += 7) {
/* 1602 */         byte b = readByte();
/* 1603 */         result |= (b & Byte.MAX_VALUE) << shift;
/* 1604 */         if ((b & 0x80) == 0) {
/* 1605 */           return result;
/*      */         }
/*      */       } 
/* 1608 */       throw InvalidProtocolBufferException.malformedVarint();
/*      */     }
/*      */     
/*      */     private byte readByte() throws IOException {
/* 1612 */       if (this.pos == this.limit) {
/* 1613 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/* 1615 */       return this.buffer[this.pos++];
/*      */     }
/*      */     
/*      */     private int readLittleEndian32() throws IOException {
/* 1619 */       requireBytes(4);
/* 1620 */       return readLittleEndian32_NoCheck();
/*      */     }
/*      */     
/*      */     private long readLittleEndian64() throws IOException {
/* 1624 */       requireBytes(8);
/* 1625 */       return readLittleEndian64_NoCheck();
/*      */     }
/*      */     
/*      */     private int readLittleEndian32_NoCheck() {
/* 1629 */       int p = this.pos;
/* 1630 */       byte[] buffer = this.buffer;
/* 1631 */       this.pos = p + 4;
/* 1632 */       return buffer[p] & 0xFF | (buffer[p + 1] & 0xFF) << 8 | (buffer[p + 2] & 0xFF) << 16 | (buffer[p + 3] & 0xFF) << 24;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private long readLittleEndian64_NoCheck() {
/* 1639 */       int p = this.pos;
/* 1640 */       byte[] buffer = this.buffer;
/* 1641 */       this.pos = p + 8;
/* 1642 */       return buffer[p] & 0xFFL | (buffer[p + 1] & 0xFFL) << 8L | (buffer[p + 2] & 0xFFL) << 16L | (buffer[p + 3] & 0xFFL) << 24L | (buffer[p + 4] & 0xFFL) << 32L | (buffer[p + 5] & 0xFFL) << 40L | (buffer[p + 6] & 0xFFL) << 48L | (buffer[p + 7] & 0xFFL) << 56L;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void skipVarint() throws IOException {
/* 1653 */       if (this.limit - this.pos >= 10) {
/* 1654 */         byte[] buffer = this.buffer;
/* 1655 */         int p = this.pos;
/* 1656 */         for (int i = 0; i < 10; i++) {
/* 1657 */           if (buffer[p++] >= 0) {
/* 1658 */             this.pos = p;
/*      */             return;
/*      */           } 
/*      */         } 
/*      */       } 
/* 1663 */       skipVarintSlowPath();
/*      */     }
/*      */     
/*      */     private void skipVarintSlowPath() throws IOException {
/* 1667 */       for (int i = 0; i < 10; i++) {
/* 1668 */         if (readByte() >= 0) {
/*      */           return;
/*      */         }
/*      */       } 
/* 1672 */       throw InvalidProtocolBufferException.malformedVarint();
/*      */     }
/*      */     
/*      */     private void skipBytes(int size) throws IOException {
/* 1676 */       requireBytes(size);
/*      */       
/* 1678 */       this.pos += size;
/*      */     }
/*      */     
/*      */     private void skipGroup() throws IOException {
/* 1682 */       int prevEndGroupTag = this.endGroupTag;
/* 1683 */       this.endGroupTag = WireFormat.makeTag(WireFormat.getTagFieldNumber(this.tag), 4); do {
/*      */       
/* 1685 */       } while (getFieldNumber() != Integer.MAX_VALUE && skipField());
/*      */ 
/*      */ 
/*      */       
/* 1689 */       if (this.tag != this.endGroupTag) {
/* 1690 */         throw InvalidProtocolBufferException.parseFailure();
/*      */       }
/* 1692 */       this.endGroupTag = prevEndGroupTag;
/*      */     }
/*      */     
/*      */     private void requireBytes(int size) throws IOException {
/* 1696 */       if (size < 0 || size > this.limit - this.pos) {
/* 1697 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/*      */     }
/*      */     
/*      */     private void requireWireType(int requiredWireType) throws IOException {
/* 1702 */       if (WireFormat.getTagWireType(this.tag) != requiredWireType) {
/* 1703 */         throw InvalidProtocolBufferException.invalidWireType();
/*      */       }
/*      */     }
/*      */     
/*      */     private void verifyPackedFixed64Length(int bytes) throws IOException {
/* 1708 */       requireBytes(bytes);
/* 1709 */       if ((bytes & 0x7) != 0)
/*      */       {
/* 1711 */         throw InvalidProtocolBufferException.parseFailure();
/*      */       }
/*      */     }
/*      */     
/*      */     private void verifyPackedFixed32Length(int bytes) throws IOException {
/* 1716 */       requireBytes(bytes);
/* 1717 */       if ((bytes & 0x3) != 0)
/*      */       {
/* 1719 */         throw InvalidProtocolBufferException.parseFailure();
/*      */       }
/*      */     }
/*      */     
/*      */     private void requirePosition(int expectedPosition) throws IOException {
/* 1724 */       if (this.pos != expectedPosition)
/* 1725 */         throw InvalidProtocolBufferException.truncatedMessage(); 
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\BinaryReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */