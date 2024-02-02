/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class UnknownFieldSetLite
/*     */ {
/*     */   private static final int MIN_CAPACITY = 8;
/*  52 */   private static final UnknownFieldSetLite DEFAULT_INSTANCE = new UnknownFieldSetLite(0, new int[0], new Object[0], false);
/*     */   
/*     */   private int count;
/*     */   
/*     */   private int[] tags;
/*     */   
/*     */   private Object[] objects;
/*     */   
/*     */   public static UnknownFieldSetLite getDefaultInstance() {
/*  61 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   static UnknownFieldSetLite newInstance() {
/*  66 */     return new UnknownFieldSetLite();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static UnknownFieldSetLite mutableCopyOf(UnknownFieldSetLite first, UnknownFieldSetLite second) {
/*  74 */     int count = first.count + second.count;
/*  75 */     int[] tags = Arrays.copyOf(first.tags, count);
/*  76 */     System.arraycopy(second.tags, 0, tags, first.count, second.count);
/*  77 */     Object[] objects = Arrays.copyOf(first.objects, count);
/*  78 */     System.arraycopy(second.objects, 0, objects, first.count, second.count);
/*  79 */     return new UnknownFieldSetLite(count, tags, objects, true);
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
/*  92 */   private int memoizedSerializedSize = -1;
/*     */ 
/*     */   
/*     */   private boolean isMutable;
/*     */ 
/*     */   
/*     */   private UnknownFieldSetLite() {
/*  99 */     this(0, new int[8], new Object[8], true);
/*     */   }
/*     */ 
/*     */   
/*     */   private UnknownFieldSetLite(int count, int[] tags, Object[] objects, boolean isMutable) {
/* 104 */     this.count = count;
/* 105 */     this.tags = tags;
/* 106 */     this.objects = objects;
/* 107 */     this.isMutable = isMutable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void makeImmutable() {
/* 116 */     this.isMutable = false;
/*     */   }
/*     */ 
/*     */   
/*     */   void checkMutable() {
/* 121 */     if (!this.isMutable) {
/* 122 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeTo(CodedOutputStream output) throws IOException {
/* 132 */     for (int i = 0; i < this.count; i++) {
/* 133 */       int tag = this.tags[i];
/* 134 */       int fieldNumber = WireFormat.getTagFieldNumber(tag);
/* 135 */       switch (WireFormat.getTagWireType(tag)) {
/*     */         case 0:
/* 137 */           output.writeUInt64(fieldNumber, ((Long)this.objects[i]).longValue());
/*     */           break;
/*     */         case 5:
/* 140 */           output.writeFixed32(fieldNumber, ((Integer)this.objects[i]).intValue());
/*     */           break;
/*     */         case 1:
/* 143 */           output.writeFixed64(fieldNumber, ((Long)this.objects[i]).longValue());
/*     */           break;
/*     */         case 2:
/* 146 */           output.writeBytes(fieldNumber, (ByteString)this.objects[i]);
/*     */           break;
/*     */         case 3:
/* 149 */           output.writeTag(fieldNumber, 3);
/* 150 */           ((UnknownFieldSetLite)this.objects[i]).writeTo(output);
/* 151 */           output.writeTag(fieldNumber, 4);
/*     */           break;
/*     */         default:
/* 154 */           throw InvalidProtocolBufferException.invalidWireType();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeAsMessageSetTo(CodedOutputStream output) throws IOException {
/* 165 */     for (int i = 0; i < this.count; i++) {
/* 166 */       int fieldNumber = WireFormat.getTagFieldNumber(this.tags[i]);
/* 167 */       output.writeRawMessageSetExtension(fieldNumber, (ByteString)this.objects[i]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void writeAsMessageSetTo(Writer writer) throws IOException {
/* 173 */     if (writer.fieldOrder() == Writer.FieldOrder.DESCENDING) {
/*     */       
/* 175 */       for (int i = this.count - 1; i >= 0; i--) {
/* 176 */         int fieldNumber = WireFormat.getTagFieldNumber(this.tags[i]);
/* 177 */         writer.writeMessageSetItem(fieldNumber, this.objects[i]);
/*     */       } 
/*     */     } else {
/*     */       
/* 181 */       for (int i = 0; i < this.count; i++) {
/* 182 */         int fieldNumber = WireFormat.getTagFieldNumber(this.tags[i]);
/* 183 */         writer.writeMessageSetItem(fieldNumber, this.objects[i]);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(Writer writer) throws IOException {
/* 190 */     if (this.count == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 195 */     if (writer.fieldOrder() == Writer.FieldOrder.ASCENDING) {
/* 196 */       for (int i = 0; i < this.count; i++) {
/* 197 */         writeField(this.tags[i], this.objects[i], writer);
/*     */       }
/*     */     } else {
/* 200 */       for (int i = this.count - 1; i >= 0; i--) {
/* 201 */         writeField(this.tags[i], this.objects[i], writer);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void writeField(int tag, Object object, Writer writer) throws IOException {
/* 207 */     int fieldNumber = WireFormat.getTagFieldNumber(tag);
/* 208 */     switch (WireFormat.getTagWireType(tag)) {
/*     */       case 0:
/* 210 */         writer.writeInt64(fieldNumber, ((Long)object).longValue());
/*     */         return;
/*     */       case 5:
/* 213 */         writer.writeFixed32(fieldNumber, ((Integer)object).intValue());
/*     */         return;
/*     */       case 1:
/* 216 */         writer.writeFixed64(fieldNumber, ((Long)object).longValue());
/*     */         return;
/*     */       case 2:
/* 219 */         writer.writeBytes(fieldNumber, (ByteString)object);
/*     */         return;
/*     */       case 3:
/* 222 */         if (writer.fieldOrder() == Writer.FieldOrder.ASCENDING) {
/* 223 */           writer.writeStartGroup(fieldNumber);
/* 224 */           ((UnknownFieldSetLite)object).writeTo(writer);
/* 225 */           writer.writeEndGroup(fieldNumber);
/*     */         } else {
/* 227 */           writer.writeEndGroup(fieldNumber);
/* 228 */           ((UnknownFieldSetLite)object).writeTo(writer);
/* 229 */           writer.writeStartGroup(fieldNumber);
/*     */         } 
/*     */         return;
/*     */     } 
/*     */     
/* 234 */     throw new RuntimeException(InvalidProtocolBufferException.invalidWireType());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSerializedSizeAsMessageSet() {
/* 243 */     int size = this.memoizedSerializedSize;
/* 244 */     if (size != -1) {
/* 245 */       return size;
/*     */     }
/*     */     
/* 248 */     size = 0;
/* 249 */     for (int i = 0; i < this.count; i++) {
/* 250 */       int tag = this.tags[i];
/* 251 */       int fieldNumber = WireFormat.getTagFieldNumber(tag);
/* 252 */       size += 
/* 253 */         CodedOutputStream.computeRawMessageSetExtensionSize(fieldNumber, (ByteString)this.objects[i]);
/*     */     } 
/*     */     
/* 256 */     this.memoizedSerializedSize = size;
/*     */     
/* 258 */     return size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/* 267 */     int size = this.memoizedSerializedSize;
/* 268 */     if (size != -1) {
/* 269 */       return size;
/*     */     }
/*     */     
/* 272 */     size = 0;
/* 273 */     for (int i = 0; i < this.count; i++) {
/* 274 */       int tag = this.tags[i];
/* 275 */       int fieldNumber = WireFormat.getTagFieldNumber(tag);
/* 276 */       switch (WireFormat.getTagWireType(tag)) {
/*     */         case 0:
/* 278 */           size += CodedOutputStream.computeUInt64Size(fieldNumber, ((Long)this.objects[i]).longValue());
/*     */           break;
/*     */         case 5:
/* 281 */           size += CodedOutputStream.computeFixed32Size(fieldNumber, ((Integer)this.objects[i]).intValue());
/*     */           break;
/*     */         case 1:
/* 284 */           size += CodedOutputStream.computeFixed64Size(fieldNumber, ((Long)this.objects[i]).longValue());
/*     */           break;
/*     */         case 2:
/* 287 */           size += CodedOutputStream.computeBytesSize(fieldNumber, (ByteString)this.objects[i]);
/*     */           break;
/*     */         case 3:
/* 290 */           size += 
/* 291 */             CodedOutputStream.computeTagSize(fieldNumber) * 2 + ((UnknownFieldSetLite)this.objects[i])
/* 292 */             .getSerializedSize();
/*     */           break;
/*     */         default:
/* 295 */           throw new IllegalStateException(InvalidProtocolBufferException.invalidWireType());
/*     */       } 
/*     */     
/*     */     } 
/* 299 */     this.memoizedSerializedSize = size;
/*     */     
/* 301 */     return size;
/*     */   }
/*     */   
/*     */   private static boolean equals(int[] tags1, int[] tags2, int count) {
/* 305 */     for (int i = 0; i < count; i++) {
/* 306 */       if (tags1[i] != tags2[i]) {
/* 307 */         return false;
/*     */       }
/*     */     } 
/* 310 */     return true;
/*     */   }
/*     */   
/*     */   private static boolean equals(Object[] objects1, Object[] objects2, int count) {
/* 314 */     for (int i = 0; i < count; i++) {
/* 315 */       if (!objects1[i].equals(objects2[i])) {
/* 316 */         return false;
/*     */       }
/*     */     } 
/* 319 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 324 */     if (this == obj) {
/* 325 */       return true;
/*     */     }
/*     */     
/* 328 */     if (obj == null) {
/* 329 */       return false;
/*     */     }
/*     */     
/* 332 */     if (!(obj instanceof UnknownFieldSetLite)) {
/* 333 */       return false;
/*     */     }
/*     */     
/* 336 */     UnknownFieldSetLite other = (UnknownFieldSetLite)obj;
/* 337 */     if (this.count != other.count || 
/* 338 */       !equals(this.tags, other.tags, this.count) || 
/* 339 */       !equals(this.objects, other.objects, this.count)) {
/* 340 */       return false;
/*     */     }
/*     */     
/* 343 */     return true;
/*     */   }
/*     */   
/*     */   private static int hashCode(int[] tags, int count) {
/* 347 */     int hashCode = 17;
/* 348 */     for (int i = 0; i < count; i++) {
/* 349 */       hashCode = 31 * hashCode + tags[i];
/*     */     }
/* 351 */     return hashCode;
/*     */   }
/*     */   
/*     */   private static int hashCode(Object[] objects, int count) {
/* 355 */     int hashCode = 17;
/* 356 */     for (int i = 0; i < count; i++) {
/* 357 */       hashCode = 31 * hashCode + objects[i].hashCode();
/*     */     }
/* 359 */     return hashCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 364 */     int hashCode = 17;
/*     */     
/* 366 */     hashCode = 31 * hashCode + this.count;
/* 367 */     hashCode = 31 * hashCode + hashCode(this.tags, this.count);
/* 368 */     hashCode = 31 * hashCode + hashCode(this.objects, this.count);
/*     */     
/* 370 */     return hashCode;
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
/*     */   final void printWithIndent(StringBuilder buffer, int indent) {
/* 382 */     for (int i = 0; i < this.count; i++) {
/* 383 */       int fieldNumber = WireFormat.getTagFieldNumber(this.tags[i]);
/* 384 */       MessageLiteToString.printField(buffer, indent, String.valueOf(fieldNumber), this.objects[i]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void storeField(int tag, Object value) {
/* 390 */     checkMutable();
/* 391 */     ensureCapacity();
/*     */     
/* 393 */     this.tags[this.count] = tag;
/* 394 */     this.objects[this.count] = value;
/* 395 */     this.count++;
/*     */   }
/*     */ 
/*     */   
/*     */   private void ensureCapacity() {
/* 400 */     if (this.count == this.tags.length) {
/* 401 */       int increment = (this.count < 4) ? 8 : (this.count >> 1);
/* 402 */       int newLength = this.count + increment;
/*     */       
/* 404 */       this.tags = Arrays.copyOf(this.tags, newLength);
/* 405 */       this.objects = Arrays.copyOf(this.objects, newLength);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean mergeFieldFrom(int tag, CodedInputStream input) throws IOException {
/*     */     UnknownFieldSetLite subFieldSet;
/* 418 */     checkMutable();
/* 419 */     int fieldNumber = WireFormat.getTagFieldNumber(tag);
/* 420 */     switch (WireFormat.getTagWireType(tag)) {
/*     */       case 0:
/* 422 */         storeField(tag, Long.valueOf(input.readInt64()));
/* 423 */         return true;
/*     */       case 5:
/* 425 */         storeField(tag, Integer.valueOf(input.readFixed32()));
/* 426 */         return true;
/*     */       case 1:
/* 428 */         storeField(tag, Long.valueOf(input.readFixed64()));
/* 429 */         return true;
/*     */       case 2:
/* 431 */         storeField(tag, input.readBytes());
/* 432 */         return true;
/*     */       case 3:
/* 434 */         subFieldSet = new UnknownFieldSetLite();
/* 435 */         subFieldSet.mergeFrom(input);
/* 436 */         input.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
/* 437 */         storeField(tag, subFieldSet);
/* 438 */         return true;
/*     */       case 4:
/* 440 */         return false;
/*     */     } 
/* 442 */     throw InvalidProtocolBufferException.invalidWireType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   UnknownFieldSetLite mergeVarintField(int fieldNumber, int value) {
/* 453 */     checkMutable();
/* 454 */     if (fieldNumber == 0) {
/* 455 */       throw new IllegalArgumentException("Zero is not a valid field number.");
/*     */     }
/*     */     
/* 458 */     storeField(WireFormat.makeTag(fieldNumber, 0), Long.valueOf(value));
/*     */     
/* 460 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   UnknownFieldSetLite mergeLengthDelimitedField(int fieldNumber, ByteString value) {
/* 469 */     checkMutable();
/* 470 */     if (fieldNumber == 0) {
/* 471 */       throw new IllegalArgumentException("Zero is not a valid field number.");
/*     */     }
/*     */     
/* 474 */     storeField(WireFormat.makeTag(fieldNumber, 2), value);
/*     */     
/* 476 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   private UnknownFieldSetLite mergeFrom(CodedInputStream input) throws IOException {
/*     */     int tag;
/*     */     do {
/* 483 */       tag = input.readTag();
/* 484 */     } while (tag != 0 && mergeFieldFrom(tag, input));
/*     */ 
/*     */ 
/*     */     
/* 488 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\UnknownFieldSetLite.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */