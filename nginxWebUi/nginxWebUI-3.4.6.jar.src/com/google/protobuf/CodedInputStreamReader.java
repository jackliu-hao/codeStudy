/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.IOException;
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
/*      */ final class CodedInputStreamReader
/*      */   implements Reader
/*      */ {
/*      */   private static final int FIXED32_MULTIPLE_MASK = 3;
/*      */   private static final int FIXED64_MULTIPLE_MASK = 7;
/*      */   private static final int NEXT_TAG_UNSET = 0;
/*      */   private final CodedInputStream input;
/*      */   private int tag;
/*      */   private int endGroupTag;
/*   56 */   private int nextTag = 0;
/*      */   
/*      */   public static CodedInputStreamReader forCodedInput(CodedInputStream input) {
/*   59 */     if (input.wrapper != null) {
/*   60 */       return input.wrapper;
/*      */     }
/*   62 */     return new CodedInputStreamReader(input);
/*      */   }
/*      */   
/*      */   private CodedInputStreamReader(CodedInputStream input) {
/*   66 */     this.input = Internal.<CodedInputStream>checkNotNull(input, "input");
/*   67 */     this.input.wrapper = this;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean shouldDiscardUnknownFields() {
/*   72 */     return this.input.shouldDiscardUnknownFields();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getFieldNumber() throws IOException {
/*   77 */     if (this.nextTag != 0) {
/*   78 */       this.tag = this.nextTag;
/*   79 */       this.nextTag = 0;
/*      */     } else {
/*   81 */       this.tag = this.input.readTag();
/*      */     } 
/*   83 */     if (this.tag == 0 || this.tag == this.endGroupTag) {
/*   84 */       return Integer.MAX_VALUE;
/*      */     }
/*   86 */     return WireFormat.getTagFieldNumber(this.tag);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getTag() {
/*   91 */     return this.tag;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean skipField() throws IOException {
/*   96 */     if (this.input.isAtEnd() || this.tag == this.endGroupTag) {
/*   97 */       return false;
/*      */     }
/*   99 */     return this.input.skipField(this.tag);
/*      */   }
/*      */   
/*      */   private void requireWireType(int requiredWireType) throws IOException {
/*  103 */     if (WireFormat.getTagWireType(this.tag) != requiredWireType) {
/*  104 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public double readDouble() throws IOException {
/*  110 */     requireWireType(1);
/*  111 */     return this.input.readDouble();
/*      */   }
/*      */ 
/*      */   
/*      */   public float readFloat() throws IOException {
/*  116 */     requireWireType(5);
/*  117 */     return this.input.readFloat();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readUInt64() throws IOException {
/*  122 */     requireWireType(0);
/*  123 */     return this.input.readUInt64();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readInt64() throws IOException {
/*  128 */     requireWireType(0);
/*  129 */     return this.input.readInt64();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readInt32() throws IOException {
/*  134 */     requireWireType(0);
/*  135 */     return this.input.readInt32();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readFixed64() throws IOException {
/*  140 */     requireWireType(1);
/*  141 */     return this.input.readFixed64();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readFixed32() throws IOException {
/*  146 */     requireWireType(5);
/*  147 */     return this.input.readFixed32();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean readBool() throws IOException {
/*  152 */     requireWireType(0);
/*  153 */     return this.input.readBool();
/*      */   }
/*      */ 
/*      */   
/*      */   public String readString() throws IOException {
/*  158 */     requireWireType(2);
/*  159 */     return this.input.readString();
/*      */   }
/*      */ 
/*      */   
/*      */   public String readStringRequireUtf8() throws IOException {
/*  164 */     requireWireType(2);
/*  165 */     return this.input.readStringRequireUtf8();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readMessage(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  172 */     requireWireType(2);
/*  173 */     return readMessage(Protobuf.getInstance().schemaFor(clazz), extensionRegistry);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readMessageBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  180 */     requireWireType(2);
/*  181 */     return readMessage(schema, extensionRegistry);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readGroup(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  188 */     requireWireType(3);
/*  189 */     return readGroup(Protobuf.getInstance().schemaFor(clazz), extensionRegistry);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readGroupBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  196 */     requireWireType(3);
/*  197 */     return readGroup(schema, extensionRegistry);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private <T> T readMessage(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  203 */     int size = this.input.readUInt32();
/*  204 */     if (this.input.recursionDepth >= this.input.recursionLimit) {
/*  205 */       throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */     }
/*      */ 
/*      */     
/*  209 */     int prevLimit = this.input.pushLimit(size);
/*      */     
/*  211 */     T message = schema.newInstance();
/*  212 */     this.input.recursionDepth++;
/*  213 */     schema.mergeFrom(message, this, extensionRegistry);
/*  214 */     schema.makeImmutable(message);
/*  215 */     this.input.checkLastTagWas(0);
/*  216 */     this.input.recursionDepth--;
/*      */     
/*  218 */     this.input.popLimit(prevLimit);
/*  219 */     return message;
/*      */   }
/*      */ 
/*      */   
/*      */   private <T> T readGroup(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  224 */     int prevEndGroupTag = this.endGroupTag;
/*  225 */     this.endGroupTag = WireFormat.makeTag(WireFormat.getTagFieldNumber(this.tag), 4);
/*      */ 
/*      */     
/*      */     try {
/*  229 */       T message = schema.newInstance();
/*  230 */       schema.mergeFrom(message, this, extensionRegistry);
/*  231 */       schema.makeImmutable(message);
/*      */       
/*  233 */       if (this.tag != this.endGroupTag) {
/*  234 */         throw InvalidProtocolBufferException.parseFailure();
/*      */       }
/*  236 */       return message;
/*      */     } finally {
/*      */       
/*  239 */       this.endGroupTag = prevEndGroupTag;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteString readBytes() throws IOException {
/*  245 */     requireWireType(2);
/*  246 */     return this.input.readBytes();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUInt32() throws IOException {
/*  251 */     requireWireType(0);
/*  252 */     return this.input.readUInt32();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readEnum() throws IOException {
/*  257 */     requireWireType(0);
/*  258 */     return this.input.readEnum();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readSFixed32() throws IOException {
/*  263 */     requireWireType(5);
/*  264 */     return this.input.readSFixed32();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readSFixed64() throws IOException {
/*  269 */     requireWireType(1);
/*  270 */     return this.input.readSFixed64();
/*      */   }
/*      */ 
/*      */   
/*      */   public int readSInt32() throws IOException {
/*  275 */     requireWireType(0);
/*  276 */     return this.input.readSInt32();
/*      */   }
/*      */ 
/*      */   
/*      */   public long readSInt64() throws IOException {
/*  281 */     requireWireType(0);
/*  282 */     return this.input.readSInt64();
/*      */   } public void readDoubleList(List<Double> target) throws IOException {
/*      */     int bytes;
/*      */     int endPos;
/*      */     int nextTag;
/*  287 */     if (target instanceof DoubleArrayList) {
/*  288 */       int i, j, k; DoubleArrayList plist = (DoubleArrayList)target;
/*  289 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  291 */           i = this.input.readUInt32();
/*  292 */           verifyPackedFixed64Length(i);
/*  293 */           j = this.input.getTotalBytesRead() + i;
/*      */           do {
/*  295 */             plist.addDouble(this.input.readDouble());
/*  296 */           } while (this.input.getTotalBytesRead() < j);
/*      */           return;
/*      */         case 1:
/*      */           do {
/*  300 */             plist.addDouble(this.input.readDouble());
/*  301 */             if (this.input.isAtEnd()) {
/*      */               return;
/*      */             }
/*  304 */             k = this.input.readTag();
/*  305 */           } while (k == this.tag);
/*      */           
/*  307 */           this.nextTag = k;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  312 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } 
/*      */     
/*  315 */     switch (WireFormat.getTagWireType(this.tag)) {
/*      */       case 2:
/*  317 */         bytes = this.input.readUInt32();
/*  318 */         verifyPackedFixed64Length(bytes);
/*  319 */         endPos = this.input.getTotalBytesRead() + bytes;
/*      */         do {
/*  321 */           target.add(Double.valueOf(this.input.readDouble()));
/*  322 */         } while (this.input.getTotalBytesRead() < endPos);
/*      */         return;
/*      */       case 1:
/*      */         do {
/*  326 */           target.add(Double.valueOf(this.input.readDouble()));
/*  327 */           if (this.input.isAtEnd()) {
/*      */             return;
/*      */           }
/*  330 */           nextTag = this.input.readTag();
/*  331 */         } while (nextTag == this.tag);
/*      */         
/*  333 */         this.nextTag = nextTag;
/*      */         return;
/*      */     } 
/*      */ 
/*      */     
/*  338 */     throw InvalidProtocolBufferException.invalidWireType();
/*      */   }
/*      */   
/*      */   public void readFloatList(List<Float> target) throws IOException {
/*      */     int bytes;
/*      */     int endPos;
/*      */     int nextTag;
/*  345 */     if (target instanceof FloatArrayList) {
/*  346 */       int i, j, k; FloatArrayList plist = (FloatArrayList)target;
/*  347 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  349 */           i = this.input.readUInt32();
/*  350 */           verifyPackedFixed32Length(i);
/*  351 */           j = this.input.getTotalBytesRead() + i;
/*      */           do {
/*  353 */             plist.addFloat(this.input.readFloat());
/*  354 */           } while (this.input.getTotalBytesRead() < j);
/*      */           return;
/*      */         case 5:
/*      */           do {
/*  358 */             plist.addFloat(this.input.readFloat());
/*  359 */             if (this.input.isAtEnd()) {
/*      */               return;
/*      */             }
/*  362 */             k = this.input.readTag();
/*  363 */           } while (k == this.tag);
/*      */           
/*  365 */           this.nextTag = k;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  370 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } 
/*      */     
/*  373 */     switch (WireFormat.getTagWireType(this.tag)) {
/*      */       case 2:
/*  375 */         bytes = this.input.readUInt32();
/*  376 */         verifyPackedFixed32Length(bytes);
/*  377 */         endPos = this.input.getTotalBytesRead() + bytes;
/*      */         do {
/*  379 */           target.add(Float.valueOf(this.input.readFloat()));
/*  380 */         } while (this.input.getTotalBytesRead() < endPos);
/*      */         return;
/*      */       case 5:
/*      */         do {
/*  384 */           target.add(Float.valueOf(this.input.readFloat()));
/*  385 */           if (this.input.isAtEnd()) {
/*      */             return;
/*      */           }
/*  388 */           nextTag = this.input.readTag();
/*  389 */         } while (nextTag == this.tag);
/*      */         
/*  391 */         this.nextTag = nextTag;
/*      */         return;
/*      */     } 
/*      */ 
/*      */     
/*  396 */     throw InvalidProtocolBufferException.invalidWireType();
/*      */   }
/*      */   
/*      */   public void readUInt64List(List<Long> target) throws IOException {
/*      */     int bytes;
/*      */     int endPos;
/*      */     int nextTag;
/*  403 */     if (target instanceof LongArrayList) {
/*  404 */       int i, j, k; LongArrayList plist = (LongArrayList)target;
/*  405 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  407 */           i = this.input.readUInt32();
/*  408 */           j = this.input.getTotalBytesRead() + i;
/*      */           while (true) {
/*  410 */             plist.addLong(this.input.readUInt64());
/*  411 */             if (this.input.getTotalBytesRead() >= j) {
/*  412 */               requirePosition(j); return;
/*      */             } 
/*      */           } 
/*      */         case 0:
/*  416 */           do { plist.addLong(this.input.readUInt64());
/*  417 */             if (this.input.isAtEnd()) {
/*      */               return;
/*      */             }
/*  420 */             k = this.input.readTag(); }
/*  421 */           while (k == this.tag);
/*      */           
/*  423 */           this.nextTag = k;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  428 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } 
/*      */     
/*  431 */     switch (WireFormat.getTagWireType(this.tag)) {
/*      */       case 2:
/*  433 */         bytes = this.input.readUInt32();
/*  434 */         endPos = this.input.getTotalBytesRead() + bytes;
/*      */         while (true) {
/*  436 */           target.add(Long.valueOf(this.input.readUInt64()));
/*  437 */           if (this.input.getTotalBytesRead() >= endPos) {
/*  438 */             requirePosition(endPos); return;
/*      */           } 
/*      */         } 
/*      */       case 0:
/*  442 */         do { target.add(Long.valueOf(this.input.readUInt64()));
/*  443 */           if (this.input.isAtEnd()) {
/*      */             return;
/*      */           }
/*  446 */           nextTag = this.input.readTag(); }
/*  447 */         while (nextTag == this.tag);
/*      */         
/*  449 */         this.nextTag = nextTag;
/*      */         return;
/*      */     } 
/*      */ 
/*      */     
/*  454 */     throw InvalidProtocolBufferException.invalidWireType();
/*      */   }
/*      */   
/*      */   public void readInt64List(List<Long> target) throws IOException {
/*      */     int bytes;
/*      */     int endPos;
/*      */     int nextTag;
/*  461 */     if (target instanceof LongArrayList) {
/*  462 */       int i, j, k; LongArrayList plist = (LongArrayList)target;
/*  463 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  465 */           i = this.input.readUInt32();
/*  466 */           j = this.input.getTotalBytesRead() + i;
/*      */           while (true) {
/*  468 */             plist.addLong(this.input.readInt64());
/*  469 */             if (this.input.getTotalBytesRead() >= j) {
/*  470 */               requirePosition(j); return;
/*      */             } 
/*      */           } 
/*      */         case 0:
/*  474 */           do { plist.addLong(this.input.readInt64());
/*  475 */             if (this.input.isAtEnd()) {
/*      */               return;
/*      */             }
/*  478 */             k = this.input.readTag(); }
/*  479 */           while (k == this.tag);
/*      */           
/*  481 */           this.nextTag = k;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  486 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } 
/*      */     
/*  489 */     switch (WireFormat.getTagWireType(this.tag)) {
/*      */       case 2:
/*  491 */         bytes = this.input.readUInt32();
/*  492 */         endPos = this.input.getTotalBytesRead() + bytes;
/*      */         while (true) {
/*  494 */           target.add(Long.valueOf(this.input.readInt64()));
/*  495 */           if (this.input.getTotalBytesRead() >= endPos) {
/*  496 */             requirePosition(endPos); return;
/*      */           } 
/*      */         } 
/*      */       case 0:
/*  500 */         do { target.add(Long.valueOf(this.input.readInt64()));
/*  501 */           if (this.input.isAtEnd()) {
/*      */             return;
/*      */           }
/*  504 */           nextTag = this.input.readTag(); }
/*  505 */         while (nextTag == this.tag);
/*      */         
/*  507 */         this.nextTag = nextTag;
/*      */         return;
/*      */     } 
/*      */ 
/*      */     
/*  512 */     throw InvalidProtocolBufferException.invalidWireType();
/*      */   }
/*      */   
/*      */   public void readInt32List(List<Integer> target) throws IOException {
/*      */     int bytes;
/*      */     int endPos;
/*      */     int nextTag;
/*  519 */     if (target instanceof IntArrayList) {
/*  520 */       int i, j, k; IntArrayList plist = (IntArrayList)target;
/*  521 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  523 */           i = this.input.readUInt32();
/*  524 */           j = this.input.getTotalBytesRead() + i;
/*      */           while (true) {
/*  526 */             plist.addInt(this.input.readInt32());
/*  527 */             if (this.input.getTotalBytesRead() >= j) {
/*  528 */               requirePosition(j); return;
/*      */             } 
/*      */           } 
/*      */         case 0:
/*  532 */           do { plist.addInt(this.input.readInt32());
/*  533 */             if (this.input.isAtEnd()) {
/*      */               return;
/*      */             }
/*  536 */             k = this.input.readTag(); }
/*  537 */           while (k == this.tag);
/*      */           
/*  539 */           this.nextTag = k;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  544 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } 
/*      */     
/*  547 */     switch (WireFormat.getTagWireType(this.tag)) {
/*      */       case 2:
/*  549 */         bytes = this.input.readUInt32();
/*  550 */         endPos = this.input.getTotalBytesRead() + bytes;
/*      */         while (true) {
/*  552 */           target.add(Integer.valueOf(this.input.readInt32()));
/*  553 */           if (this.input.getTotalBytesRead() >= endPos) {
/*  554 */             requirePosition(endPos); return;
/*      */           } 
/*      */         } 
/*      */       case 0:
/*  558 */         do { target.add(Integer.valueOf(this.input.readInt32()));
/*  559 */           if (this.input.isAtEnd()) {
/*      */             return;
/*      */           }
/*  562 */           nextTag = this.input.readTag(); }
/*  563 */         while (nextTag == this.tag);
/*      */         
/*  565 */         this.nextTag = nextTag;
/*      */         return;
/*      */     } 
/*      */ 
/*      */     
/*  570 */     throw InvalidProtocolBufferException.invalidWireType();
/*      */   }
/*      */   
/*      */   public void readFixed64List(List<Long> target) throws IOException {
/*      */     int bytes;
/*      */     int endPos;
/*      */     int nextTag;
/*  577 */     if (target instanceof LongArrayList) {
/*  578 */       int i, j, k; LongArrayList plist = (LongArrayList)target;
/*  579 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  581 */           i = this.input.readUInt32();
/*  582 */           verifyPackedFixed64Length(i);
/*  583 */           j = this.input.getTotalBytesRead() + i;
/*      */           do {
/*  585 */             plist.addLong(this.input.readFixed64());
/*  586 */           } while (this.input.getTotalBytesRead() < j);
/*      */           return;
/*      */         case 1:
/*      */           do {
/*  590 */             plist.addLong(this.input.readFixed64());
/*  591 */             if (this.input.isAtEnd()) {
/*      */               return;
/*      */             }
/*  594 */             k = this.input.readTag();
/*  595 */           } while (k == this.tag);
/*      */           
/*  597 */           this.nextTag = k;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  602 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } 
/*      */     
/*  605 */     switch (WireFormat.getTagWireType(this.tag)) {
/*      */       case 2:
/*  607 */         bytes = this.input.readUInt32();
/*  608 */         verifyPackedFixed64Length(bytes);
/*  609 */         endPos = this.input.getTotalBytesRead() + bytes;
/*      */         do {
/*  611 */           target.add(Long.valueOf(this.input.readFixed64()));
/*  612 */         } while (this.input.getTotalBytesRead() < endPos);
/*      */         return;
/*      */       case 1:
/*      */         do {
/*  616 */           target.add(Long.valueOf(this.input.readFixed64()));
/*  617 */           if (this.input.isAtEnd()) {
/*      */             return;
/*      */           }
/*  620 */           nextTag = this.input.readTag();
/*  621 */         } while (nextTag == this.tag);
/*      */         
/*  623 */         this.nextTag = nextTag;
/*      */         return;
/*      */     } 
/*      */ 
/*      */     
/*  628 */     throw InvalidProtocolBufferException.invalidWireType();
/*      */   }
/*      */   
/*      */   public void readFixed32List(List<Integer> target) throws IOException {
/*      */     int bytes;
/*      */     int endPos;
/*      */     int nextTag;
/*  635 */     if (target instanceof IntArrayList) {
/*  636 */       int i, j, k; IntArrayList plist = (IntArrayList)target;
/*  637 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  639 */           i = this.input.readUInt32();
/*  640 */           verifyPackedFixed32Length(i);
/*  641 */           j = this.input.getTotalBytesRead() + i;
/*      */           do {
/*  643 */             plist.addInt(this.input.readFixed32());
/*  644 */           } while (this.input.getTotalBytesRead() < j);
/*      */           return;
/*      */         case 5:
/*      */           do {
/*  648 */             plist.addInt(this.input.readFixed32());
/*  649 */             if (this.input.isAtEnd()) {
/*      */               return;
/*      */             }
/*  652 */             k = this.input.readTag();
/*  653 */           } while (k == this.tag);
/*      */           
/*  655 */           this.nextTag = k;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  660 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } 
/*      */     
/*  663 */     switch (WireFormat.getTagWireType(this.tag)) {
/*      */       case 2:
/*  665 */         bytes = this.input.readUInt32();
/*  666 */         verifyPackedFixed32Length(bytes);
/*  667 */         endPos = this.input.getTotalBytesRead() + bytes;
/*      */         do {
/*  669 */           target.add(Integer.valueOf(this.input.readFixed32()));
/*  670 */         } while (this.input.getTotalBytesRead() < endPos);
/*      */         return;
/*      */       case 5:
/*      */         do {
/*  674 */           target.add(Integer.valueOf(this.input.readFixed32()));
/*  675 */           if (this.input.isAtEnd()) {
/*      */             return;
/*      */           }
/*  678 */           nextTag = this.input.readTag();
/*  679 */         } while (nextTag == this.tag);
/*      */         
/*  681 */         this.nextTag = nextTag;
/*      */         return;
/*      */     } 
/*      */ 
/*      */     
/*  686 */     throw InvalidProtocolBufferException.invalidWireType();
/*      */   }
/*      */   
/*      */   public void readBoolList(List<Boolean> target) throws IOException {
/*      */     int bytes;
/*      */     int endPos;
/*      */     int nextTag;
/*  693 */     if (target instanceof BooleanArrayList) {
/*  694 */       int i, j, k; BooleanArrayList plist = (BooleanArrayList)target;
/*  695 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  697 */           i = this.input.readUInt32();
/*  698 */           j = this.input.getTotalBytesRead() + i;
/*      */           while (true) {
/*  700 */             plist.addBoolean(this.input.readBool());
/*  701 */             if (this.input.getTotalBytesRead() >= j) {
/*  702 */               requirePosition(j); return;
/*      */             } 
/*      */           } 
/*      */         case 0:
/*  706 */           do { plist.addBoolean(this.input.readBool());
/*  707 */             if (this.input.isAtEnd()) {
/*      */               return;
/*      */             }
/*  710 */             k = this.input.readTag(); }
/*  711 */           while (k == this.tag);
/*      */           
/*  713 */           this.nextTag = k;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  718 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } 
/*      */     
/*  721 */     switch (WireFormat.getTagWireType(this.tag)) {
/*      */       case 2:
/*  723 */         bytes = this.input.readUInt32();
/*  724 */         endPos = this.input.getTotalBytesRead() + bytes;
/*      */         while (true) {
/*  726 */           target.add(Boolean.valueOf(this.input.readBool()));
/*  727 */           if (this.input.getTotalBytesRead() >= endPos) {
/*  728 */             requirePosition(endPos); return;
/*      */           } 
/*      */         } 
/*      */       case 0:
/*  732 */         do { target.add(Boolean.valueOf(this.input.readBool()));
/*  733 */           if (this.input.isAtEnd()) {
/*      */             return;
/*      */           }
/*  736 */           nextTag = this.input.readTag(); }
/*  737 */         while (nextTag == this.tag);
/*      */         
/*  739 */         this.nextTag = nextTag;
/*      */         return;
/*      */     } 
/*      */ 
/*      */     
/*  744 */     throw InvalidProtocolBufferException.invalidWireType();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void readStringList(List<String> target) throws IOException {
/*  751 */     readStringListInternal(target, false);
/*      */   }
/*      */ 
/*      */   
/*      */   public void readStringListRequireUtf8(List<String> target) throws IOException {
/*  756 */     readStringListInternal(target, true);
/*      */   }
/*      */   public void readStringListInternal(List<String> target, boolean requireUtf8) throws IOException {
/*      */     int nextTag;
/*  760 */     if (WireFormat.getTagWireType(this.tag) != 2) {
/*  761 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */     
/*  764 */     if (target instanceof LazyStringList && !requireUtf8) {
/*  765 */       int i; LazyStringList lazyList = (LazyStringList)target;
/*      */       do {
/*  767 */         lazyList.add(readBytes());
/*  768 */         if (this.input.isAtEnd()) {
/*      */           return;
/*      */         }
/*  771 */         i = this.input.readTag();
/*  772 */       } while (i == this.tag);
/*      */       
/*  774 */       this.nextTag = i;
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*      */     do {
/*  780 */       target.add(requireUtf8 ? readStringRequireUtf8() : readString());
/*  781 */       if (this.input.isAtEnd()) {
/*      */         return;
/*      */       }
/*  784 */       nextTag = this.input.readTag();
/*  785 */     } while (nextTag == this.tag);
/*      */     
/*  787 */     this.nextTag = nextTag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> void readMessageList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  798 */     Schema<T> schema = Protobuf.getInstance().schemaFor(targetType);
/*  799 */     readMessageList(target, schema, extensionRegistry);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> void readMessageList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
/*      */     int nextTag;
/*  806 */     if (WireFormat.getTagWireType(this.tag) != 2) {
/*  807 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*  809 */     int listTag = this.tag;
/*      */     do {
/*  811 */       target.add(readMessage(schema, extensionRegistry));
/*  812 */       if (this.input.isAtEnd() || this.nextTag != 0) {
/*      */         return;
/*      */       }
/*  815 */       nextTag = this.input.readTag();
/*  816 */     } while (nextTag == listTag);
/*      */     
/*  818 */     this.nextTag = nextTag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> void readGroupList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  828 */     Schema<T> schema = Protobuf.getInstance().schemaFor(targetType);
/*  829 */     readGroupList(target, schema, extensionRegistry);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> void readGroupList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
/*      */     int nextTag;
/*  836 */     if (WireFormat.getTagWireType(this.tag) != 3) {
/*  837 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*  839 */     int listTag = this.tag;
/*      */     do {
/*  841 */       target.add(readGroup(schema, extensionRegistry));
/*  842 */       if (this.input.isAtEnd() || this.nextTag != 0) {
/*      */         return;
/*      */       }
/*  845 */       nextTag = this.input.readTag();
/*  846 */     } while (nextTag == listTag);
/*      */     
/*  848 */     this.nextTag = nextTag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void readBytesList(List<ByteString> target) throws IOException {
/*      */     int nextTag;
/*  856 */     if (WireFormat.getTagWireType(this.tag) != 2) {
/*  857 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */     
/*      */     do {
/*  861 */       target.add(readBytes());
/*  862 */       if (this.input.isAtEnd()) {
/*      */         return;
/*      */       }
/*  865 */       nextTag = this.input.readTag();
/*  866 */     } while (nextTag == this.tag);
/*      */     
/*  868 */     this.nextTag = nextTag;
/*      */   }
/*      */ 
/*      */   
/*      */   public void readUInt32List(List<Integer> target) throws IOException {
/*      */     int bytes;
/*      */     int endPos;
/*      */     int nextTag;
/*  876 */     if (target instanceof IntArrayList) {
/*  877 */       int i, j, k; IntArrayList plist = (IntArrayList)target;
/*  878 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  880 */           i = this.input.readUInt32();
/*  881 */           j = this.input.getTotalBytesRead() + i;
/*      */           while (true) {
/*  883 */             plist.addInt(this.input.readUInt32());
/*  884 */             if (this.input.getTotalBytesRead() >= j) {
/*  885 */               requirePosition(j); return;
/*      */             } 
/*      */           } 
/*      */         case 0:
/*  889 */           do { plist.addInt(this.input.readUInt32());
/*  890 */             if (this.input.isAtEnd()) {
/*      */               return;
/*      */             }
/*  893 */             k = this.input.readTag(); }
/*  894 */           while (k == this.tag);
/*      */           
/*  896 */           this.nextTag = k;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  901 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } 
/*      */     
/*  904 */     switch (WireFormat.getTagWireType(this.tag)) {
/*      */       case 2:
/*  906 */         bytes = this.input.readUInt32();
/*  907 */         endPos = this.input.getTotalBytesRead() + bytes;
/*      */         while (true) {
/*  909 */           target.add(Integer.valueOf(this.input.readUInt32()));
/*  910 */           if (this.input.getTotalBytesRead() >= endPos) {
/*  911 */             requirePosition(endPos); return;
/*      */           } 
/*      */         } 
/*      */       case 0:
/*  915 */         do { target.add(Integer.valueOf(this.input.readUInt32()));
/*  916 */           if (this.input.isAtEnd()) {
/*      */             return;
/*      */           }
/*  919 */           nextTag = this.input.readTag(); }
/*  920 */         while (nextTag == this.tag);
/*      */         
/*  922 */         this.nextTag = nextTag;
/*      */         return;
/*      */     } 
/*      */ 
/*      */     
/*  927 */     throw InvalidProtocolBufferException.invalidWireType();
/*      */   }
/*      */   
/*      */   public void readEnumList(List<Integer> target) throws IOException {
/*      */     int bytes;
/*      */     int endPos;
/*      */     int nextTag;
/*  934 */     if (target instanceof IntArrayList) {
/*  935 */       int i, j, k; IntArrayList plist = (IntArrayList)target;
/*  936 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  938 */           i = this.input.readUInt32();
/*  939 */           j = this.input.getTotalBytesRead() + i;
/*      */           while (true) {
/*  941 */             plist.addInt(this.input.readEnum());
/*  942 */             if (this.input.getTotalBytesRead() >= j) {
/*  943 */               requirePosition(j); return;
/*      */             } 
/*      */           } 
/*      */         case 0:
/*  947 */           do { plist.addInt(this.input.readEnum());
/*  948 */             if (this.input.isAtEnd()) {
/*      */               return;
/*      */             }
/*  951 */             k = this.input.readTag(); }
/*  952 */           while (k == this.tag);
/*      */           
/*  954 */           this.nextTag = k;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  959 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } 
/*      */     
/*  962 */     switch (WireFormat.getTagWireType(this.tag)) {
/*      */       case 2:
/*  964 */         bytes = this.input.readUInt32();
/*  965 */         endPos = this.input.getTotalBytesRead() + bytes;
/*      */         while (true) {
/*  967 */           target.add(Integer.valueOf(this.input.readEnum()));
/*  968 */           if (this.input.getTotalBytesRead() >= endPos) {
/*  969 */             requirePosition(endPos); return;
/*      */           } 
/*      */         } 
/*      */       case 0:
/*  973 */         do { target.add(Integer.valueOf(this.input.readEnum()));
/*  974 */           if (this.input.isAtEnd()) {
/*      */             return;
/*      */           }
/*  977 */           nextTag = this.input.readTag(); }
/*  978 */         while (nextTag == this.tag);
/*      */         
/*  980 */         this.nextTag = nextTag;
/*      */         return;
/*      */     } 
/*      */ 
/*      */     
/*  985 */     throw InvalidProtocolBufferException.invalidWireType();
/*      */   }
/*      */   
/*      */   public void readSFixed32List(List<Integer> target) throws IOException {
/*      */     int bytes;
/*      */     int endPos;
/*      */     int nextTag;
/*  992 */     if (target instanceof IntArrayList) {
/*  993 */       int i, j, k; IntArrayList plist = (IntArrayList)target;
/*  994 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/*  996 */           i = this.input.readUInt32();
/*  997 */           verifyPackedFixed32Length(i);
/*  998 */           j = this.input.getTotalBytesRead() + i;
/*      */           do {
/* 1000 */             plist.addInt(this.input.readSFixed32());
/* 1001 */           } while (this.input.getTotalBytesRead() < j);
/*      */           return;
/*      */         case 5:
/*      */           do {
/* 1005 */             plist.addInt(this.input.readSFixed32());
/* 1006 */             if (this.input.isAtEnd()) {
/*      */               return;
/*      */             }
/* 1009 */             k = this.input.readTag();
/* 1010 */           } while (k == this.tag);
/*      */           
/* 1012 */           this.nextTag = k;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/* 1017 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } 
/*      */     
/* 1020 */     switch (WireFormat.getTagWireType(this.tag)) {
/*      */       case 2:
/* 1022 */         bytes = this.input.readUInt32();
/* 1023 */         verifyPackedFixed32Length(bytes);
/* 1024 */         endPos = this.input.getTotalBytesRead() + bytes;
/*      */         do {
/* 1026 */           target.add(Integer.valueOf(this.input.readSFixed32()));
/* 1027 */         } while (this.input.getTotalBytesRead() < endPos);
/*      */         return;
/*      */       case 5:
/*      */         do {
/* 1031 */           target.add(Integer.valueOf(this.input.readSFixed32()));
/* 1032 */           if (this.input.isAtEnd()) {
/*      */             return;
/*      */           }
/* 1035 */           nextTag = this.input.readTag();
/* 1036 */         } while (nextTag == this.tag);
/*      */         
/* 1038 */         this.nextTag = nextTag;
/*      */         return;
/*      */     } 
/*      */ 
/*      */     
/* 1043 */     throw InvalidProtocolBufferException.invalidWireType();
/*      */   }
/*      */   
/*      */   public void readSFixed64List(List<Long> target) throws IOException {
/*      */     int bytes;
/*      */     int endPos;
/*      */     int nextTag;
/* 1050 */     if (target instanceof LongArrayList) {
/* 1051 */       int i, j, k; LongArrayList plist = (LongArrayList)target;
/* 1052 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/* 1054 */           i = this.input.readUInt32();
/* 1055 */           verifyPackedFixed64Length(i);
/* 1056 */           j = this.input.getTotalBytesRead() + i;
/*      */           do {
/* 1058 */             plist.addLong(this.input.readSFixed64());
/* 1059 */           } while (this.input.getTotalBytesRead() < j);
/*      */           return;
/*      */         case 1:
/*      */           do {
/* 1063 */             plist.addLong(this.input.readSFixed64());
/* 1064 */             if (this.input.isAtEnd()) {
/*      */               return;
/*      */             }
/* 1067 */             k = this.input.readTag();
/* 1068 */           } while (k == this.tag);
/*      */           
/* 1070 */           this.nextTag = k;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/* 1075 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } 
/*      */     
/* 1078 */     switch (WireFormat.getTagWireType(this.tag)) {
/*      */       case 2:
/* 1080 */         bytes = this.input.readUInt32();
/* 1081 */         verifyPackedFixed64Length(bytes);
/* 1082 */         endPos = this.input.getTotalBytesRead() + bytes;
/*      */         do {
/* 1084 */           target.add(Long.valueOf(this.input.readSFixed64()));
/* 1085 */         } while (this.input.getTotalBytesRead() < endPos);
/*      */         return;
/*      */       case 1:
/*      */         do {
/* 1089 */           target.add(Long.valueOf(this.input.readSFixed64()));
/* 1090 */           if (this.input.isAtEnd()) {
/*      */             return;
/*      */           }
/* 1093 */           nextTag = this.input.readTag();
/* 1094 */         } while (nextTag == this.tag);
/*      */         
/* 1096 */         this.nextTag = nextTag;
/*      */         return;
/*      */     } 
/*      */ 
/*      */     
/* 1101 */     throw InvalidProtocolBufferException.invalidWireType();
/*      */   }
/*      */   
/*      */   public void readSInt32List(List<Integer> target) throws IOException {
/*      */     int bytes;
/*      */     int endPos;
/*      */     int nextTag;
/* 1108 */     if (target instanceof IntArrayList) {
/* 1109 */       int i, j, k; IntArrayList plist = (IntArrayList)target;
/* 1110 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/* 1112 */           i = this.input.readUInt32();
/* 1113 */           j = this.input.getTotalBytesRead() + i;
/*      */           while (true) {
/* 1115 */             plist.addInt(this.input.readSInt32());
/* 1116 */             if (this.input.getTotalBytesRead() >= j) {
/* 1117 */               requirePosition(j); return;
/*      */             } 
/*      */           } 
/*      */         case 0:
/* 1121 */           do { plist.addInt(this.input.readSInt32());
/* 1122 */             if (this.input.isAtEnd()) {
/*      */               return;
/*      */             }
/* 1125 */             k = this.input.readTag(); }
/* 1126 */           while (k == this.tag);
/*      */           
/* 1128 */           this.nextTag = k;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/* 1133 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } 
/*      */     
/* 1136 */     switch (WireFormat.getTagWireType(this.tag)) {
/*      */       case 2:
/* 1138 */         bytes = this.input.readUInt32();
/* 1139 */         endPos = this.input.getTotalBytesRead() + bytes;
/*      */         while (true) {
/* 1141 */           target.add(Integer.valueOf(this.input.readSInt32()));
/* 1142 */           if (this.input.getTotalBytesRead() >= endPos) {
/* 1143 */             requirePosition(endPos); return;
/*      */           } 
/*      */         } 
/*      */       case 0:
/* 1147 */         do { target.add(Integer.valueOf(this.input.readSInt32()));
/* 1148 */           if (this.input.isAtEnd()) {
/*      */             return;
/*      */           }
/* 1151 */           nextTag = this.input.readTag(); }
/* 1152 */         while (nextTag == this.tag);
/*      */         
/* 1154 */         this.nextTag = nextTag;
/*      */         return;
/*      */     } 
/*      */ 
/*      */     
/* 1159 */     throw InvalidProtocolBufferException.invalidWireType();
/*      */   }
/*      */   
/*      */   public void readSInt64List(List<Long> target) throws IOException {
/*      */     int bytes;
/*      */     int endPos;
/*      */     int nextTag;
/* 1166 */     if (target instanceof LongArrayList) {
/* 1167 */       int i, j, k; LongArrayList plist = (LongArrayList)target;
/* 1168 */       switch (WireFormat.getTagWireType(this.tag)) {
/*      */         case 2:
/* 1170 */           i = this.input.readUInt32();
/* 1171 */           j = this.input.getTotalBytesRead() + i;
/*      */           while (true) {
/* 1173 */             plist.addLong(this.input.readSInt64());
/* 1174 */             if (this.input.getTotalBytesRead() >= j) {
/* 1175 */               requirePosition(j); return;
/*      */             } 
/*      */           } 
/*      */         case 0:
/* 1179 */           do { plist.addLong(this.input.readSInt64());
/* 1180 */             if (this.input.isAtEnd()) {
/*      */               return;
/*      */             }
/* 1183 */             k = this.input.readTag(); }
/* 1184 */           while (k == this.tag);
/*      */           
/* 1186 */           this.nextTag = k;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/* 1191 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } 
/*      */     
/* 1194 */     switch (WireFormat.getTagWireType(this.tag)) {
/*      */       case 2:
/* 1196 */         bytes = this.input.readUInt32();
/* 1197 */         endPos = this.input.getTotalBytesRead() + bytes;
/*      */         while (true) {
/* 1199 */           target.add(Long.valueOf(this.input.readSInt64()));
/* 1200 */           if (this.input.getTotalBytesRead() >= endPos) {
/* 1201 */             requirePosition(endPos); return;
/*      */           } 
/*      */         } 
/*      */       case 0:
/* 1205 */         do { target.add(Long.valueOf(this.input.readSInt64()));
/* 1206 */           if (this.input.isAtEnd()) {
/*      */             return;
/*      */           }
/* 1209 */           nextTag = this.input.readTag(); }
/* 1210 */         while (nextTag == this.tag);
/*      */         
/* 1212 */         this.nextTag = nextTag;
/*      */         return;
/*      */     } 
/*      */ 
/*      */     
/* 1217 */     throw InvalidProtocolBufferException.invalidWireType();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void verifyPackedFixed64Length(int bytes) throws IOException {
/* 1223 */     if ((bytes & 0x7) != 0)
/*      */     {
/* 1225 */       throw InvalidProtocolBufferException.parseFailure();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <K, V> void readMap(Map<K, V> target, MapEntryLite.Metadata<K, V> metadata, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1236 */     requireWireType(2);
/* 1237 */     int size = this.input.readUInt32();
/* 1238 */     int prevLimit = this.input.pushLimit(size);
/* 1239 */     K key = metadata.defaultKey;
/* 1240 */     V value = metadata.defaultValue;
/*      */     try {
/*      */       while (true) {
/* 1243 */         int number = getFieldNumber();
/* 1244 */         if (number == Integer.MAX_VALUE || this.input.isAtEnd()) {
/*      */           break;
/*      */         }
/*      */         try {
/* 1248 */           switch (number) {
/*      */             case 1:
/* 1250 */               key = (K)readField(metadata.keyType, null, null);
/*      */               continue;
/*      */ 
/*      */             
/*      */             case 2:
/* 1255 */               value = (V)readField(metadata.valueType, metadata.defaultValue
/* 1256 */                   .getClass(), extensionRegistry);
/*      */               continue;
/*      */           } 
/* 1259 */           if (!skipField()) {
/* 1260 */             throw new InvalidProtocolBufferException("Unable to parse map entry.");
/*      */           
/*      */           }
/*      */         }
/* 1264 */         catch (InvalidWireTypeException ignore) {
/*      */           
/* 1266 */           if (!skipField()) {
/* 1267 */             throw new InvalidProtocolBufferException("Unable to parse map entry.");
/*      */           }
/*      */         } 
/*      */       } 
/* 1271 */       target.put(key, value);
/*      */     } finally {
/*      */       
/* 1274 */       this.input.popLimit(prevLimit);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private Object readField(WireFormat.FieldType fieldType, Class<?> messageType, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1281 */     switch (fieldType) {
/*      */       case BOOL:
/* 1283 */         return Boolean.valueOf(readBool());
/*      */       case BYTES:
/* 1285 */         return readBytes();
/*      */       case DOUBLE:
/* 1287 */         return Double.valueOf(readDouble());
/*      */       case ENUM:
/* 1289 */         return Integer.valueOf(readEnum());
/*      */       case FIXED32:
/* 1291 */         return Integer.valueOf(readFixed32());
/*      */       case FIXED64:
/* 1293 */         return Long.valueOf(readFixed64());
/*      */       case FLOAT:
/* 1295 */         return Float.valueOf(readFloat());
/*      */       case INT32:
/* 1297 */         return Integer.valueOf(readInt32());
/*      */       case INT64:
/* 1299 */         return Long.valueOf(readInt64());
/*      */       case MESSAGE:
/* 1301 */         return readMessage(messageType, extensionRegistry);
/*      */       case SFIXED32:
/* 1303 */         return Integer.valueOf(readSFixed32());
/*      */       case SFIXED64:
/* 1305 */         return Long.valueOf(readSFixed64());
/*      */       case SINT32:
/* 1307 */         return Integer.valueOf(readSInt32());
/*      */       case SINT64:
/* 1309 */         return Long.valueOf(readSInt64());
/*      */       case STRING:
/* 1311 */         return readStringRequireUtf8();
/*      */       case UINT32:
/* 1313 */         return Integer.valueOf(readUInt32());
/*      */       case UINT64:
/* 1315 */         return Long.valueOf(readUInt64());
/*      */     } 
/* 1317 */     throw new RuntimeException("unsupported field type.");
/*      */   }
/*      */ 
/*      */   
/*      */   private void verifyPackedFixed32Length(int bytes) throws IOException {
/* 1322 */     if ((bytes & 0x3) != 0)
/*      */     {
/* 1324 */       throw InvalidProtocolBufferException.parseFailure();
/*      */     }
/*      */   }
/*      */   
/*      */   private void requirePosition(int expectedPosition) throws IOException {
/* 1329 */     if (this.input.getTotalBytesRead() != expectedPosition)
/* 1330 */       throw InvalidProtocolBufferException.truncatedMessage(); 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\CodedInputStreamReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */