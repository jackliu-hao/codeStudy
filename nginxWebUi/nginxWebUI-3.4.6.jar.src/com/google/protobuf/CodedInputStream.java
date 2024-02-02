/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class CodedInputStream
/*      */ {
/*      */   private static final int DEFAULT_BUFFER_SIZE = 4096;
/*      */   private static final int DEFAULT_RECURSION_LIMIT = 100;
/*      */   private static final int DEFAULT_SIZE_LIMIT = 2147483647;
/*      */   int recursionDepth;
/*   70 */   int recursionLimit = 100;
/*      */ 
/*      */   
/*   73 */   int sizeLimit = Integer.MAX_VALUE;
/*      */   
/*      */   CodedInputStreamReader wrapper;
/*      */   
/*      */   private boolean shouldDiscardUnknownFields;
/*      */   
/*      */   public static CodedInputStream newInstance(InputStream input) {
/*   80 */     return newInstance(input, 4096);
/*      */   }
/*      */ 
/*      */   
/*      */   public static CodedInputStream newInstance(InputStream input, int bufferSize) {
/*   85 */     if (bufferSize <= 0) {
/*   86 */       throw new IllegalArgumentException("bufferSize must be > 0");
/*      */     }
/*   88 */     if (input == null)
/*      */     {
/*   90 */       return newInstance(Internal.EMPTY_BYTE_ARRAY);
/*      */     }
/*   92 */     return new StreamDecoder(input, bufferSize);
/*      */   }
/*      */ 
/*      */   
/*      */   public static CodedInputStream newInstance(Iterable<ByteBuffer> input) {
/*   97 */     if (!UnsafeDirectNioDecoder.isSupported()) {
/*   98 */       return newInstance(new IterableByteBufferInputStream(input));
/*      */     }
/*  100 */     return newInstance(input, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static CodedInputStream newInstance(Iterable<ByteBuffer> bufs, boolean bufferIsImmutable) {
/*  111 */     int flag = 0;
/*      */     
/*  113 */     int totalSize = 0;
/*  114 */     for (ByteBuffer buf : bufs) {
/*  115 */       totalSize += buf.remaining();
/*  116 */       if (buf.hasArray()) {
/*  117 */         flag |= 0x1; continue;
/*  118 */       }  if (buf.isDirect()) {
/*  119 */         flag |= 0x2; continue;
/*      */       } 
/*  121 */       flag |= 0x4;
/*      */     } 
/*      */     
/*  124 */     if (flag == 2) {
/*  125 */       return new IterableDirectByteBufferDecoder(bufs, totalSize, bufferIsImmutable);
/*      */     }
/*      */     
/*  128 */     return newInstance(new IterableByteBufferInputStream(bufs));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static CodedInputStream newInstance(byte[] buf) {
/*  134 */     return newInstance(buf, 0, buf.length);
/*      */   }
/*      */ 
/*      */   
/*      */   public static CodedInputStream newInstance(byte[] buf, int off, int len) {
/*  139 */     return newInstance(buf, off, len, false);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static CodedInputStream newInstance(byte[] buf, int off, int len, boolean bufferIsImmutable) {
/*  145 */     ArrayDecoder result = new ArrayDecoder(buf, off, len, bufferIsImmutable);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  152 */       result.pushLimit(len);
/*  153 */     } catch (InvalidProtocolBufferException ex) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  161 */       throw new IllegalArgumentException(ex);
/*      */     } 
/*  163 */     return result;
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
/*      */   public static CodedInputStream newInstance(ByteBuffer buf) {
/*  175 */     return newInstance(buf, false);
/*      */   }
/*      */ 
/*      */   
/*      */   static CodedInputStream newInstance(ByteBuffer buf, boolean bufferIsImmutable) {
/*  180 */     if (buf.hasArray()) {
/*  181 */       return newInstance(buf
/*  182 */           .array(), buf.arrayOffset() + buf.position(), buf.remaining(), bufferIsImmutable);
/*      */     }
/*      */     
/*  185 */     if (buf.isDirect() && UnsafeDirectNioDecoder.isSupported()) {
/*  186 */       return new UnsafeDirectNioDecoder(buf, bufferIsImmutable);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  192 */     byte[] buffer = new byte[buf.remaining()];
/*  193 */     buf.duplicate().get(buffer);
/*  194 */     return newInstance(buffer, 0, buffer.length, true);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int setRecursionLimit(int limit) {
/*  388 */     if (limit < 0) {
/*  389 */       throw new IllegalArgumentException("Recursion limit cannot be negative: " + limit);
/*      */     }
/*  391 */     int oldLimit = this.recursionLimit;
/*  392 */     this.recursionLimit = limit;
/*  393 */     return oldLimit;
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
/*      */   public final int setSizeLimit(int limit) {
/*  411 */     if (limit < 0) {
/*  412 */       throw new IllegalArgumentException("Size limit cannot be negative: " + limit);
/*      */     }
/*  414 */     int oldLimit = this.sizeLimit;
/*  415 */     this.sizeLimit = limit;
/*  416 */     return oldLimit;
/*      */   }
/*      */   private CodedInputStream() {
/*  419 */     this.shouldDiscardUnknownFields = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void discardUnknownFields() {
/*  430 */     this.shouldDiscardUnknownFields = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void unsetDiscardUnknownFields() {
/*  438 */     this.shouldDiscardUnknownFields = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final boolean shouldDiscardUnknownFields() {
/*  446 */     return this.shouldDiscardUnknownFields;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int decodeZigZag32(int n) {
/*  529 */     return n >>> 1 ^ -(n & 0x1);
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
/*      */   public static long decodeZigZag64(long n) {
/*  542 */     return n >>> 1L ^ -(n & 0x1L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int readRawVarint32(int firstByte, InputStream input) throws IOException {
/*  551 */     if ((firstByte & 0x80) == 0) {
/*  552 */       return firstByte;
/*      */     }
/*      */     
/*  555 */     int result = firstByte & 0x7F;
/*  556 */     int offset = 7;
/*  557 */     for (; offset < 32; offset += 7) {
/*  558 */       int b = input.read();
/*  559 */       if (b == -1) {
/*  560 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/*  562 */       result |= (b & 0x7F) << offset;
/*  563 */       if ((b & 0x80) == 0) {
/*  564 */         return result;
/*      */       }
/*      */     } 
/*      */     
/*  568 */     for (; offset < 64; offset += 7) {
/*  569 */       int b = input.read();
/*  570 */       if (b == -1) {
/*  571 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/*  573 */       if ((b & 0x80) == 0) {
/*  574 */         return result;
/*      */       }
/*      */     } 
/*  577 */     throw InvalidProtocolBufferException.malformedVarint();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int readRawVarint32(InputStream input) throws IOException {
/*  587 */     int firstByte = input.read();
/*  588 */     if (firstByte == -1) {
/*  589 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*  591 */     return readRawVarint32(firstByte, input);
/*      */   } public abstract int readTag() throws IOException; public abstract void checkLastTagWas(int paramInt) throws InvalidProtocolBufferException; public abstract int getLastTag(); public abstract boolean skipField(int paramInt) throws IOException; @Deprecated
/*      */   public abstract boolean skipField(int paramInt, CodedOutputStream paramCodedOutputStream) throws IOException; public abstract void skipMessage() throws IOException; public abstract void skipMessage(CodedOutputStream paramCodedOutputStream) throws IOException; public abstract double readDouble() throws IOException; public abstract float readFloat() throws IOException; public abstract long readUInt64() throws IOException; public abstract long readInt64() throws IOException; public abstract int readInt32() throws IOException; public abstract long readFixed64() throws IOException; public abstract int readFixed32() throws IOException; public abstract boolean readBool() throws IOException; public abstract String readString() throws IOException; public abstract String readStringRequireUtf8() throws IOException; public abstract void readGroup(int paramInt, MessageLite.Builder paramBuilder, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException; public abstract <T extends MessageLite> T readGroup(int paramInt, Parser<T> paramParser, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException; @Deprecated
/*      */   public abstract void readUnknownGroup(int paramInt, MessageLite.Builder paramBuilder) throws IOException; public abstract void readMessage(MessageLite.Builder paramBuilder, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException; public abstract <T extends MessageLite> T readMessage(Parser<T> paramParser, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException; public abstract ByteString readBytes() throws IOException; public abstract byte[] readByteArray() throws IOException; public abstract ByteBuffer readByteBuffer() throws IOException; public abstract int readUInt32() throws IOException; public abstract int readEnum() throws IOException; public abstract int readSFixed32() throws IOException; public abstract long readSFixed64() throws IOException; public abstract int readSInt32() throws IOException; public abstract long readSInt64() throws IOException; public abstract int readRawVarint32() throws IOException; public abstract long readRawVarint64() throws IOException; abstract long readRawVarint64SlowPath() throws IOException; public abstract int readRawLittleEndian32() throws IOException;
/*      */   public abstract long readRawLittleEndian64() throws IOException;
/*      */   public abstract void enableAliasing(boolean paramBoolean);
/*      */   public abstract void resetSizeCounter();
/*      */   public abstract int pushLimit(int paramInt) throws InvalidProtocolBufferException;
/*      */   public abstract void popLimit(int paramInt);
/*      */   public abstract int getBytesUntilLimit();
/*      */   public abstract boolean isAtEnd() throws IOException;
/*      */   public abstract int getTotalBytesRead();
/*      */   public abstract byte readRawByte() throws IOException;
/*      */   public abstract byte[] readRawBytes(int paramInt) throws IOException;
/*      */   public abstract void skipRawBytes(int paramInt) throws IOException;
/*  606 */   private static final class ArrayDecoder extends CodedInputStream { private final byte[] buffer; private final boolean immutable; private int limit; private int bufferSizeAfterLimit; private int currentLimit = Integer.MAX_VALUE; private int pos; private int startPos; private int lastTag; private boolean enableAliasing;
/*      */     
/*      */     private ArrayDecoder(byte[] buffer, int offset, int len, boolean immutable) {
/*  609 */       this.buffer = buffer;
/*  610 */       this.limit = offset + len;
/*  611 */       this.pos = offset;
/*  612 */       this.startPos = this.pos;
/*  613 */       this.immutable = immutable;
/*      */     }
/*      */ 
/*      */     
/*      */     public int readTag() throws IOException {
/*  618 */       if (isAtEnd()) {
/*  619 */         this.lastTag = 0;
/*  620 */         return 0;
/*      */       } 
/*      */       
/*  623 */       this.lastTag = readRawVarint32();
/*  624 */       if (WireFormat.getTagFieldNumber(this.lastTag) == 0)
/*      */       {
/*      */         
/*  627 */         throw InvalidProtocolBufferException.invalidTag();
/*      */       }
/*  629 */       return this.lastTag;
/*      */     }
/*      */ 
/*      */     
/*      */     public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
/*  634 */       if (this.lastTag != value) {
/*  635 */         throw InvalidProtocolBufferException.invalidEndTag();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public int getLastTag() {
/*  641 */       return this.lastTag;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean skipField(int tag) throws IOException {
/*  646 */       switch (WireFormat.getTagWireType(tag)) {
/*      */         case 0:
/*  648 */           skipRawVarint();
/*  649 */           return true;
/*      */         case 1:
/*  651 */           skipRawBytes(8);
/*  652 */           return true;
/*      */         case 2:
/*  654 */           skipRawBytes(readRawVarint32());
/*  655 */           return true;
/*      */         case 3:
/*  657 */           skipMessage();
/*  658 */           checkLastTagWas(
/*  659 */               WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
/*  660 */           return true;
/*      */         case 4:
/*  662 */           return false;
/*      */         case 5:
/*  664 */           skipRawBytes(4);
/*  665 */           return true;
/*      */       } 
/*  667 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } public boolean skipField(int tag, CodedOutputStream output) throws IOException {
/*      */       long l;
/*      */       ByteString byteString;
/*      */       int endtag;
/*      */       int value;
/*  673 */       switch (WireFormat.getTagWireType(tag)) {
/*      */         
/*      */         case 0:
/*  676 */           l = readInt64();
/*  677 */           output.writeRawVarint32(tag);
/*  678 */           output.writeUInt64NoTag(l);
/*  679 */           return true;
/*      */ 
/*      */         
/*      */         case 1:
/*  683 */           l = readRawLittleEndian64();
/*  684 */           output.writeRawVarint32(tag);
/*  685 */           output.writeFixed64NoTag(l);
/*  686 */           return true;
/*      */ 
/*      */         
/*      */         case 2:
/*  690 */           byteString = readBytes();
/*  691 */           output.writeRawVarint32(tag);
/*  692 */           output.writeBytesNoTag(byteString);
/*  693 */           return true;
/*      */ 
/*      */         
/*      */         case 3:
/*  697 */           output.writeRawVarint32(tag);
/*  698 */           skipMessage(output);
/*      */           
/*  700 */           endtag = WireFormat.makeTag(
/*  701 */               WireFormat.getTagFieldNumber(tag), 4);
/*  702 */           checkLastTagWas(endtag);
/*  703 */           output.writeRawVarint32(endtag);
/*  704 */           return true;
/*      */ 
/*      */         
/*      */         case 4:
/*  708 */           return false;
/*      */ 
/*      */         
/*      */         case 5:
/*  712 */           value = readRawLittleEndian32();
/*  713 */           output.writeRawVarint32(tag);
/*  714 */           output.writeFixed32NoTag(value);
/*  715 */           return true;
/*      */       } 
/*      */       
/*  718 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */ 
/*      */     
/*      */     public void skipMessage() throws IOException {
/*      */       int tag;
/*      */       do {
/*  725 */         tag = readTag();
/*  726 */       } while (tag != 0 && skipField(tag));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void skipMessage(CodedOutputStream output) throws IOException {
/*      */       int tag;
/*      */       do {
/*  735 */         tag = readTag();
/*  736 */       } while (tag != 0 && skipField(tag, output));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public double readDouble() throws IOException {
/*  747 */       return Double.longBitsToDouble(readRawLittleEndian64());
/*      */     }
/*      */ 
/*      */     
/*      */     public float readFloat() throws IOException {
/*  752 */       return Float.intBitsToFloat(readRawLittleEndian32());
/*      */     }
/*      */ 
/*      */     
/*      */     public long readUInt64() throws IOException {
/*  757 */       return readRawVarint64();
/*      */     }
/*      */ 
/*      */     
/*      */     public long readInt64() throws IOException {
/*  762 */       return readRawVarint64();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readInt32() throws IOException {
/*  767 */       return readRawVarint32();
/*      */     }
/*      */ 
/*      */     
/*      */     public long readFixed64() throws IOException {
/*  772 */       return readRawLittleEndian64();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readFixed32() throws IOException {
/*  777 */       return readRawLittleEndian32();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean readBool() throws IOException {
/*  782 */       return (readRawVarint64() != 0L);
/*      */     }
/*      */ 
/*      */     
/*      */     public String readString() throws IOException {
/*  787 */       int size = readRawVarint32();
/*  788 */       if (size > 0 && size <= this.limit - this.pos) {
/*      */ 
/*      */         
/*  791 */         String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
/*  792 */         this.pos += size;
/*  793 */         return result;
/*      */       } 
/*      */       
/*  796 */       if (size == 0) {
/*  797 */         return "";
/*      */       }
/*  799 */       if (size < 0) {
/*  800 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/*  802 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */ 
/*      */     
/*      */     public String readStringRequireUtf8() throws IOException {
/*  807 */       int size = readRawVarint32();
/*  808 */       if (size > 0 && size <= this.limit - this.pos) {
/*  809 */         String result = Utf8.decodeUtf8(this.buffer, this.pos, size);
/*  810 */         this.pos += size;
/*  811 */         return result;
/*      */       } 
/*      */       
/*  814 */       if (size == 0) {
/*  815 */         return "";
/*      */       }
/*  817 */       if (size <= 0) {
/*  818 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/*  820 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  829 */       if (this.recursionDepth >= this.recursionLimit) {
/*  830 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/*  832 */       this.recursionDepth++;
/*  833 */       builder.mergeFrom(this, extensionRegistry);
/*  834 */       checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
/*  835 */       this.recursionDepth--;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  845 */       if (this.recursionDepth >= this.recursionLimit) {
/*  846 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/*  848 */       this.recursionDepth++;
/*  849 */       MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
/*  850 */       checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
/*  851 */       this.recursionDepth--;
/*  852 */       return (T)messageLite;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
/*  859 */       readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  866 */       int length = readRawVarint32();
/*  867 */       if (this.recursionDepth >= this.recursionLimit) {
/*  868 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/*  870 */       int oldLimit = pushLimit(length);
/*  871 */       this.recursionDepth++;
/*  872 */       builder.mergeFrom(this, extensionRegistry);
/*  873 */       checkLastTagWas(0);
/*  874 */       this.recursionDepth--;
/*  875 */       popLimit(oldLimit);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  882 */       int length = readRawVarint32();
/*  883 */       if (this.recursionDepth >= this.recursionLimit) {
/*  884 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/*  886 */       int oldLimit = pushLimit(length);
/*  887 */       this.recursionDepth++;
/*  888 */       MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
/*  889 */       checkLastTagWas(0);
/*  890 */       this.recursionDepth--;
/*  891 */       popLimit(oldLimit);
/*  892 */       return (T)messageLite;
/*      */     }
/*      */ 
/*      */     
/*      */     public ByteString readBytes() throws IOException {
/*  897 */       int size = readRawVarint32();
/*  898 */       if (size > 0 && size <= this.limit - this.pos) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  904 */         ByteString result = (this.immutable && this.enableAliasing) ? ByteString.wrap(this.buffer, this.pos, size) : ByteString.copyFrom(this.buffer, this.pos, size);
/*  905 */         this.pos += size;
/*  906 */         return result;
/*      */       } 
/*  908 */       if (size == 0) {
/*  909 */         return ByteString.EMPTY;
/*      */       }
/*      */       
/*  912 */       return ByteString.wrap(readRawBytes(size));
/*      */     }
/*      */ 
/*      */     
/*      */     public byte[] readByteArray() throws IOException {
/*  917 */       int size = readRawVarint32();
/*  918 */       return readRawBytes(size);
/*      */     }
/*      */ 
/*      */     
/*      */     public ByteBuffer readByteBuffer() throws IOException {
/*  923 */       int size = readRawVarint32();
/*  924 */       if (size > 0 && size <= this.limit - this.pos) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  933 */         ByteBuffer result = (!this.immutable && this.enableAliasing) ? ByteBuffer.wrap(this.buffer, this.pos, size).slice() : ByteBuffer.wrap(Arrays.copyOfRange(this.buffer, this.pos, this.pos + size));
/*  934 */         this.pos += size;
/*      */         
/*  936 */         return result;
/*      */       } 
/*      */       
/*  939 */       if (size == 0) {
/*  940 */         return Internal.EMPTY_BYTE_BUFFER;
/*      */       }
/*  942 */       if (size < 0) {
/*  943 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/*  945 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readUInt32() throws IOException {
/*  950 */       return readRawVarint32();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readEnum() throws IOException {
/*  955 */       return readRawVarint32();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readSFixed32() throws IOException {
/*  960 */       return readRawLittleEndian32();
/*      */     }
/*      */ 
/*      */     
/*      */     public long readSFixed64() throws IOException {
/*  965 */       return readRawLittleEndian64();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readSInt32() throws IOException {
/*  970 */       return decodeZigZag32(readRawVarint32());
/*      */     }
/*      */ 
/*      */     
/*      */     public long readSInt64() throws IOException {
/*  975 */       return decodeZigZag64(readRawVarint64());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int readRawVarint32() throws IOException {
/*  985 */       int tempPos = this.pos;
/*      */       
/*  987 */       if (this.limit != tempPos)
/*      */       
/*      */       { 
/*      */         
/*  991 */         byte[] buffer = this.buffer;
/*      */         int x;
/*  993 */         if ((x = buffer[tempPos++]) >= 0) {
/*  994 */           this.pos = tempPos;
/*  995 */           return x;
/*  996 */         }  if (this.limit - tempPos >= 9)
/*      */         
/*  998 */         { if ((x ^= buffer[tempPos++] << 7) < 0)
/*  999 */           { x ^= 0xFFFFFF80; }
/* 1000 */           else if ((x ^= buffer[tempPos++] << 14) >= 0)
/* 1001 */           { x ^= 0x3F80; }
/* 1002 */           else if ((x ^= buffer[tempPos++] << 21) < 0)
/* 1003 */           { x ^= 0xFFE03F80; }
/*      */           else
/* 1005 */           { int y = buffer[tempPos++];
/* 1006 */             x ^= y << 28;
/* 1007 */             x ^= 0xFE03F80;
/* 1008 */             if (y < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1020 */               return (int)readRawVarint64SlowPath(); }  }  this.pos = tempPos; return x; }  }  return (int)readRawVarint64SlowPath();
/*      */     }
/*      */     
/*      */     private void skipRawVarint() throws IOException {
/* 1024 */       if (this.limit - this.pos >= 10) {
/* 1025 */         skipRawVarintFastPath();
/*      */       } else {
/* 1027 */         skipRawVarintSlowPath();
/*      */       } 
/*      */     }
/*      */     
/*      */     private void skipRawVarintFastPath() throws IOException {
/* 1032 */       for (int i = 0; i < 10; i++) {
/* 1033 */         if (this.buffer[this.pos++] >= 0) {
/*      */           return;
/*      */         }
/*      */       } 
/* 1037 */       throw InvalidProtocolBufferException.malformedVarint();
/*      */     }
/*      */     
/*      */     private void skipRawVarintSlowPath() throws IOException {
/* 1041 */       for (int i = 0; i < 10; i++) {
/* 1042 */         if (readRawByte() >= 0) {
/*      */           return;
/*      */         }
/*      */       } 
/* 1046 */       throw InvalidProtocolBufferException.malformedVarint();
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
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public long readRawVarint64() throws IOException {
/* 1064 */       int tempPos = this.pos;
/*      */       
/* 1066 */       if (this.limit != tempPos)
/*      */       
/*      */       { 
/*      */         
/* 1070 */         byte[] buffer = this.buffer;
/*      */         
/*      */         int y;
/* 1073 */         if ((y = buffer[tempPos++]) >= 0) {
/* 1074 */           this.pos = tempPos;
/* 1075 */           return y;
/* 1076 */         }  if (this.limit - tempPos >= 9)
/*      */         { long x;
/* 1078 */           if ((y ^= buffer[tempPos++] << 7) < 0)
/* 1079 */           { x = (y ^ 0xFFFFFF80); }
/* 1080 */           else if ((y ^= buffer[tempPos++] << 14) >= 0)
/* 1081 */           { x = (y ^ 0x3F80); }
/* 1082 */           else if ((y ^= buffer[tempPos++] << 21) < 0)
/* 1083 */           { x = (y ^ 0xFFE03F80); }
/* 1084 */           else if ((x = y ^ buffer[tempPos++] << 28L) >= 0L)
/* 1085 */           { x ^= 0xFE03F80L; }
/* 1086 */           else if ((x ^= buffer[tempPos++] << 35L) < 0L)
/* 1087 */           { x ^= 0xFFFFFFF80FE03F80L; }
/* 1088 */           else if ((x ^= buffer[tempPos++] << 42L) >= 0L)
/* 1089 */           { x ^= 0x3F80FE03F80L; }
/* 1090 */           else if ((x ^= buffer[tempPos++] << 49L) < 0L)
/* 1091 */           { x ^= 0xFFFE03F80FE03F80L;
/*      */ 
/*      */             
/*      */              }
/*      */           
/*      */           else
/*      */           
/*      */           { 
/*      */             
/* 1100 */             x ^= buffer[tempPos++] << 56L;
/* 1101 */             x ^= 0xFE03F80FE03F80L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1110 */             if (x < 0L && 
/* 1111 */               buffer[tempPos++] < 0L)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1119 */               return readRawVarint64SlowPath(); }  }  this.pos = tempPos; return x; }  }  return readRawVarint64SlowPath();
/*      */     }
/*      */ 
/*      */     
/*      */     long readRawVarint64SlowPath() throws IOException {
/* 1124 */       long result = 0L;
/* 1125 */       for (int shift = 0; shift < 64; shift += 7) {
/* 1126 */         byte b = readRawByte();
/* 1127 */         result |= (b & Byte.MAX_VALUE) << shift;
/* 1128 */         if ((b & 0x80) == 0) {
/* 1129 */           return result;
/*      */         }
/*      */       } 
/* 1132 */       throw InvalidProtocolBufferException.malformedVarint();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readRawLittleEndian32() throws IOException {
/* 1137 */       int tempPos = this.pos;
/*      */       
/* 1139 */       if (this.limit - tempPos < 4) {
/* 1140 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/*      */       
/* 1143 */       byte[] buffer = this.buffer;
/* 1144 */       this.pos = tempPos + 4;
/* 1145 */       return buffer[tempPos] & 0xFF | (buffer[tempPos + 1] & 0xFF) << 8 | (buffer[tempPos + 2] & 0xFF) << 16 | (buffer[tempPos + 3] & 0xFF) << 24;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public long readRawLittleEndian64() throws IOException {
/* 1153 */       int tempPos = this.pos;
/*      */       
/* 1155 */       if (this.limit - tempPos < 8) {
/* 1156 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/*      */       
/* 1159 */       byte[] buffer = this.buffer;
/* 1160 */       this.pos = tempPos + 8;
/* 1161 */       return buffer[tempPos] & 0xFFL | (buffer[tempPos + 1] & 0xFFL) << 8L | (buffer[tempPos + 2] & 0xFFL) << 16L | (buffer[tempPos + 3] & 0xFFL) << 24L | (buffer[tempPos + 4] & 0xFFL) << 32L | (buffer[tempPos + 5] & 0xFFL) << 40L | (buffer[tempPos + 6] & 0xFFL) << 48L | (buffer[tempPos + 7] & 0xFFL) << 56L;
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
/*      */     public void enableAliasing(boolean enabled) {
/* 1173 */       this.enableAliasing = enabled;
/*      */     }
/*      */ 
/*      */     
/*      */     public void resetSizeCounter() {
/* 1178 */       this.startPos = this.pos;
/*      */     }
/*      */ 
/*      */     
/*      */     public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
/* 1183 */       if (byteLimit < 0) {
/* 1184 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/* 1186 */       byteLimit += getTotalBytesRead();
/* 1187 */       int oldLimit = this.currentLimit;
/* 1188 */       if (byteLimit > oldLimit) {
/* 1189 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/* 1191 */       this.currentLimit = byteLimit;
/*      */       
/* 1193 */       recomputeBufferSizeAfterLimit();
/*      */       
/* 1195 */       return oldLimit;
/*      */     }
/*      */     
/*      */     private void recomputeBufferSizeAfterLimit() {
/* 1199 */       this.limit += this.bufferSizeAfterLimit;
/* 1200 */       int bufferEnd = this.limit - this.startPos;
/* 1201 */       if (bufferEnd > this.currentLimit) {
/*      */         
/* 1203 */         this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
/* 1204 */         this.limit -= this.bufferSizeAfterLimit;
/*      */       } else {
/* 1206 */         this.bufferSizeAfterLimit = 0;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void popLimit(int oldLimit) {
/* 1212 */       this.currentLimit = oldLimit;
/* 1213 */       recomputeBufferSizeAfterLimit();
/*      */     }
/*      */ 
/*      */     
/*      */     public int getBytesUntilLimit() {
/* 1218 */       if (this.currentLimit == Integer.MAX_VALUE) {
/* 1219 */         return -1;
/*      */       }
/*      */       
/* 1222 */       return this.currentLimit - getTotalBytesRead();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isAtEnd() throws IOException {
/* 1227 */       return (this.pos == this.limit);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getTotalBytesRead() {
/* 1232 */       return this.pos - this.startPos;
/*      */     }
/*      */ 
/*      */     
/*      */     public byte readRawByte() throws IOException {
/* 1237 */       if (this.pos == this.limit) {
/* 1238 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/* 1240 */       return this.buffer[this.pos++];
/*      */     }
/*      */ 
/*      */     
/*      */     public byte[] readRawBytes(int length) throws IOException {
/* 1245 */       if (length > 0 && length <= this.limit - this.pos) {
/* 1246 */         int tempPos = this.pos;
/* 1247 */         this.pos += length;
/* 1248 */         return Arrays.copyOfRange(this.buffer, tempPos, this.pos);
/*      */       } 
/*      */       
/* 1251 */       if (length <= 0) {
/* 1252 */         if (length == 0) {
/* 1253 */           return Internal.EMPTY_BYTE_ARRAY;
/*      */         }
/* 1255 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       } 
/*      */       
/* 1258 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */ 
/*      */     
/*      */     public void skipRawBytes(int length) throws IOException {
/* 1263 */       if (length >= 0 && length <= this.limit - this.pos) {
/*      */         
/* 1265 */         this.pos += length;
/*      */         
/*      */         return;
/*      */       } 
/* 1269 */       if (length < 0) {
/* 1270 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/* 1272 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     } }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class UnsafeDirectNioDecoder
/*      */     extends CodedInputStream
/*      */   {
/*      */     private final ByteBuffer buffer;
/*      */ 
/*      */ 
/*      */     
/*      */     private final boolean immutable;
/*      */ 
/*      */ 
/*      */     
/*      */     private final long address;
/*      */ 
/*      */ 
/*      */     
/*      */     private long limit;
/*      */ 
/*      */ 
/*      */     
/*      */     private long pos;
/*      */ 
/*      */ 
/*      */     
/*      */     private long startPos;
/*      */ 
/*      */ 
/*      */     
/*      */     private int bufferSizeAfterLimit;
/*      */ 
/*      */     
/*      */     private int lastTag;
/*      */ 
/*      */     
/*      */     private boolean enableAliasing;
/*      */ 
/*      */     
/* 1315 */     private int currentLimit = Integer.MAX_VALUE;
/*      */     
/*      */     static boolean isSupported() {
/* 1318 */       return UnsafeUtil.hasUnsafeByteBufferOperations();
/*      */     }
/*      */     
/*      */     private UnsafeDirectNioDecoder(ByteBuffer buffer, boolean immutable) {
/* 1322 */       this.buffer = buffer;
/* 1323 */       this.address = UnsafeUtil.addressOffset(buffer);
/* 1324 */       this.limit = this.address + buffer.limit();
/* 1325 */       this.pos = this.address + buffer.position();
/* 1326 */       this.startPos = this.pos;
/* 1327 */       this.immutable = immutable;
/*      */     }
/*      */ 
/*      */     
/*      */     public int readTag() throws IOException {
/* 1332 */       if (isAtEnd()) {
/* 1333 */         this.lastTag = 0;
/* 1334 */         return 0;
/*      */       } 
/*      */       
/* 1337 */       this.lastTag = readRawVarint32();
/* 1338 */       if (WireFormat.getTagFieldNumber(this.lastTag) == 0)
/*      */       {
/*      */         
/* 1341 */         throw InvalidProtocolBufferException.invalidTag();
/*      */       }
/* 1343 */       return this.lastTag;
/*      */     }
/*      */ 
/*      */     
/*      */     public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
/* 1348 */       if (this.lastTag != value) {
/* 1349 */         throw InvalidProtocolBufferException.invalidEndTag();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public int getLastTag() {
/* 1355 */       return this.lastTag;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean skipField(int tag) throws IOException {
/* 1360 */       switch (WireFormat.getTagWireType(tag)) {
/*      */         case 0:
/* 1362 */           skipRawVarint();
/* 1363 */           return true;
/*      */         case 1:
/* 1365 */           skipRawBytes(8);
/* 1366 */           return true;
/*      */         case 2:
/* 1368 */           skipRawBytes(readRawVarint32());
/* 1369 */           return true;
/*      */         case 3:
/* 1371 */           skipMessage();
/* 1372 */           checkLastTagWas(
/* 1373 */               WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
/* 1374 */           return true;
/*      */         case 4:
/* 1376 */           return false;
/*      */         case 5:
/* 1378 */           skipRawBytes(4);
/* 1379 */           return true;
/*      */       } 
/* 1381 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } public boolean skipField(int tag, CodedOutputStream output) throws IOException {
/*      */       long l;
/*      */       ByteString byteString;
/*      */       int endtag;
/*      */       int value;
/* 1387 */       switch (WireFormat.getTagWireType(tag)) {
/*      */         
/*      */         case 0:
/* 1390 */           l = readInt64();
/* 1391 */           output.writeRawVarint32(tag);
/* 1392 */           output.writeUInt64NoTag(l);
/* 1393 */           return true;
/*      */ 
/*      */         
/*      */         case 1:
/* 1397 */           l = readRawLittleEndian64();
/* 1398 */           output.writeRawVarint32(tag);
/* 1399 */           output.writeFixed64NoTag(l);
/* 1400 */           return true;
/*      */ 
/*      */         
/*      */         case 2:
/* 1404 */           byteString = readBytes();
/* 1405 */           output.writeRawVarint32(tag);
/* 1406 */           output.writeBytesNoTag(byteString);
/* 1407 */           return true;
/*      */ 
/*      */         
/*      */         case 3:
/* 1411 */           output.writeRawVarint32(tag);
/* 1412 */           skipMessage(output);
/*      */           
/* 1414 */           endtag = WireFormat.makeTag(
/* 1415 */               WireFormat.getTagFieldNumber(tag), 4);
/* 1416 */           checkLastTagWas(endtag);
/* 1417 */           output.writeRawVarint32(endtag);
/* 1418 */           return true;
/*      */ 
/*      */         
/*      */         case 4:
/* 1422 */           return false;
/*      */ 
/*      */         
/*      */         case 5:
/* 1426 */           value = readRawLittleEndian32();
/* 1427 */           output.writeRawVarint32(tag);
/* 1428 */           output.writeFixed32NoTag(value);
/* 1429 */           return true;
/*      */       } 
/*      */       
/* 1432 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */ 
/*      */     
/*      */     public void skipMessage() throws IOException {
/*      */       int tag;
/*      */       do {
/* 1439 */         tag = readTag();
/* 1440 */       } while (tag != 0 && skipField(tag));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void skipMessage(CodedOutputStream output) throws IOException {
/*      */       int tag;
/*      */       do {
/* 1449 */         tag = readTag();
/* 1450 */       } while (tag != 0 && skipField(tag, output));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public double readDouble() throws IOException {
/* 1461 */       return Double.longBitsToDouble(readRawLittleEndian64());
/*      */     }
/*      */ 
/*      */     
/*      */     public float readFloat() throws IOException {
/* 1466 */       return Float.intBitsToFloat(readRawLittleEndian32());
/*      */     }
/*      */ 
/*      */     
/*      */     public long readUInt64() throws IOException {
/* 1471 */       return readRawVarint64();
/*      */     }
/*      */ 
/*      */     
/*      */     public long readInt64() throws IOException {
/* 1476 */       return readRawVarint64();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readInt32() throws IOException {
/* 1481 */       return readRawVarint32();
/*      */     }
/*      */ 
/*      */     
/*      */     public long readFixed64() throws IOException {
/* 1486 */       return readRawLittleEndian64();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readFixed32() throws IOException {
/* 1491 */       return readRawLittleEndian32();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean readBool() throws IOException {
/* 1496 */       return (readRawVarint64() != 0L);
/*      */     }
/*      */ 
/*      */     
/*      */     public String readString() throws IOException {
/* 1501 */       int size = readRawVarint32();
/* 1502 */       if (size > 0 && size <= remaining()) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1507 */         byte[] bytes = new byte[size];
/* 1508 */         UnsafeUtil.copyMemory(this.pos, bytes, 0L, size);
/* 1509 */         String result = new String(bytes, Internal.UTF_8);
/* 1510 */         this.pos += size;
/* 1511 */         return result;
/*      */       } 
/*      */       
/* 1514 */       if (size == 0) {
/* 1515 */         return "";
/*      */       }
/* 1517 */       if (size < 0) {
/* 1518 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/* 1520 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */ 
/*      */     
/*      */     public String readStringRequireUtf8() throws IOException {
/* 1525 */       int size = readRawVarint32();
/* 1526 */       if (size > 0 && size <= remaining()) {
/* 1527 */         int bufferPos = bufferPos(this.pos);
/* 1528 */         String result = Utf8.decodeUtf8(this.buffer, bufferPos, size);
/* 1529 */         this.pos += size;
/* 1530 */         return result;
/*      */       } 
/*      */       
/* 1533 */       if (size == 0) {
/* 1534 */         return "";
/*      */       }
/* 1536 */       if (size <= 0) {
/* 1537 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/* 1539 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1548 */       if (this.recursionDepth >= this.recursionLimit) {
/* 1549 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/* 1551 */       this.recursionDepth++;
/* 1552 */       builder.mergeFrom(this, extensionRegistry);
/* 1553 */       checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
/* 1554 */       this.recursionDepth--;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1564 */       if (this.recursionDepth >= this.recursionLimit) {
/* 1565 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/* 1567 */       this.recursionDepth++;
/* 1568 */       MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
/* 1569 */       checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
/* 1570 */       this.recursionDepth--;
/* 1571 */       return (T)messageLite;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
/* 1578 */       readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1585 */       int length = readRawVarint32();
/* 1586 */       if (this.recursionDepth >= this.recursionLimit) {
/* 1587 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/* 1589 */       int oldLimit = pushLimit(length);
/* 1590 */       this.recursionDepth++;
/* 1591 */       builder.mergeFrom(this, extensionRegistry);
/* 1592 */       checkLastTagWas(0);
/* 1593 */       this.recursionDepth--;
/* 1594 */       popLimit(oldLimit);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1601 */       int length = readRawVarint32();
/* 1602 */       if (this.recursionDepth >= this.recursionLimit) {
/* 1603 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/* 1605 */       int oldLimit = pushLimit(length);
/* 1606 */       this.recursionDepth++;
/* 1607 */       MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
/* 1608 */       checkLastTagWas(0);
/* 1609 */       this.recursionDepth--;
/* 1610 */       popLimit(oldLimit);
/* 1611 */       return (T)messageLite;
/*      */     }
/*      */ 
/*      */     
/*      */     public ByteString readBytes() throws IOException {
/* 1616 */       int size = readRawVarint32();
/* 1617 */       if (size > 0 && size <= remaining()) {
/* 1618 */         if (this.immutable && this.enableAliasing) {
/* 1619 */           ByteBuffer result = slice(this.pos, this.pos + size);
/* 1620 */           this.pos += size;
/* 1621 */           return ByteString.wrap(result);
/*      */         } 
/*      */         
/* 1624 */         byte[] bytes = new byte[size];
/* 1625 */         UnsafeUtil.copyMemory(this.pos, bytes, 0L, size);
/* 1626 */         this.pos += size;
/* 1627 */         return ByteString.wrap(bytes);
/*      */       } 
/*      */ 
/*      */       
/* 1631 */       if (size == 0) {
/* 1632 */         return ByteString.EMPTY;
/*      */       }
/* 1634 */       if (size < 0) {
/* 1635 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/* 1637 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */ 
/*      */     
/*      */     public byte[] readByteArray() throws IOException {
/* 1642 */       return readRawBytes(readRawVarint32());
/*      */     }
/*      */ 
/*      */     
/*      */     public ByteBuffer readByteBuffer() throws IOException {
/* 1647 */       int size = readRawVarint32();
/* 1648 */       if (size > 0 && size <= remaining()) {
/*      */ 
/*      */ 
/*      */         
/* 1652 */         if (!this.immutable && this.enableAliasing) {
/* 1653 */           ByteBuffer result = slice(this.pos, this.pos + size);
/* 1654 */           this.pos += size;
/* 1655 */           return result;
/*      */         } 
/*      */         
/* 1658 */         byte[] bytes = new byte[size];
/* 1659 */         UnsafeUtil.copyMemory(this.pos, bytes, 0L, size);
/* 1660 */         this.pos += size;
/* 1661 */         return ByteBuffer.wrap(bytes);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1666 */       if (size == 0) {
/* 1667 */         return Internal.EMPTY_BYTE_BUFFER;
/*      */       }
/* 1669 */       if (size < 0) {
/* 1670 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/* 1672 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readUInt32() throws IOException {
/* 1677 */       return readRawVarint32();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readEnum() throws IOException {
/* 1682 */       return readRawVarint32();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readSFixed32() throws IOException {
/* 1687 */       return readRawLittleEndian32();
/*      */     }
/*      */ 
/*      */     
/*      */     public long readSFixed64() throws IOException {
/* 1692 */       return readRawLittleEndian64();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readSInt32() throws IOException {
/* 1697 */       return decodeZigZag32(readRawVarint32());
/*      */     }
/*      */ 
/*      */     
/*      */     public long readSInt64() throws IOException {
/* 1702 */       return decodeZigZag64(readRawVarint64());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int readRawVarint32() throws IOException {
/* 1712 */       long tempPos = this.pos;
/*      */       
/* 1714 */       if (this.limit != tempPos)
/*      */       { int x;
/*      */ 
/*      */ 
/*      */         
/* 1719 */         if ((x = UnsafeUtil.getByte(tempPos++)) >= 0) {
/* 1720 */           this.pos = tempPos;
/* 1721 */           return x;
/* 1722 */         }  if (this.limit - tempPos >= 9L)
/*      */         
/* 1724 */         { if ((x ^= UnsafeUtil.getByte(tempPos++) << 7) < 0)
/* 1725 */           { x ^= 0xFFFFFF80; }
/* 1726 */           else if ((x ^= UnsafeUtil.getByte(tempPos++) << 14) >= 0)
/* 1727 */           { x ^= 0x3F80; }
/* 1728 */           else if ((x ^= UnsafeUtil.getByte(tempPos++) << 21) < 0)
/* 1729 */           { x ^= 0xFFE03F80; }
/*      */           else
/* 1731 */           { int y = UnsafeUtil.getByte(tempPos++);
/* 1732 */             x ^= y << 28;
/* 1733 */             x ^= 0xFE03F80;
/* 1734 */             if (y < 0 && 
/* 1735 */               UnsafeUtil.getByte(tempPos++) < 0 && 
/* 1736 */               UnsafeUtil.getByte(tempPos++) < 0 && 
/* 1737 */               UnsafeUtil.getByte(tempPos++) < 0 && 
/* 1738 */               UnsafeUtil.getByte(tempPos++) < 0 && 
/* 1739 */               UnsafeUtil.getByte(tempPos++) < 0)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1746 */               return (int)readRawVarint64SlowPath(); }  }  this.pos = tempPos; return x; }  }  return (int)readRawVarint64SlowPath();
/*      */     }
/*      */     
/*      */     private void skipRawVarint() throws IOException {
/* 1750 */       if (remaining() >= 10) {
/* 1751 */         skipRawVarintFastPath();
/*      */       } else {
/* 1753 */         skipRawVarintSlowPath();
/*      */       } 
/*      */     }
/*      */     
/*      */     private void skipRawVarintFastPath() throws IOException {
/* 1758 */       for (int i = 0; i < 10; i++) {
/* 1759 */         if (UnsafeUtil.getByte(this.pos++) >= 0) {
/*      */           return;
/*      */         }
/*      */       } 
/* 1763 */       throw InvalidProtocolBufferException.malformedVarint();
/*      */     }
/*      */     
/*      */     private void skipRawVarintSlowPath() throws IOException {
/* 1767 */       for (int i = 0; i < 10; i++) {
/* 1768 */         if (readRawByte() >= 0) {
/*      */           return;
/*      */         }
/*      */       } 
/* 1772 */       throw InvalidProtocolBufferException.malformedVarint();
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
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public long readRawVarint64() throws IOException {
/* 1790 */       long tempPos = this.pos;
/*      */       
/* 1792 */       if (this.limit != tempPos)
/*      */       { int y;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1798 */         if ((y = UnsafeUtil.getByte(tempPos++)) >= 0) {
/* 1799 */           this.pos = tempPos;
/* 1800 */           return y;
/* 1801 */         }  if (this.limit - tempPos >= 9L)
/*      */         { long x;
/* 1803 */           if ((y ^= UnsafeUtil.getByte(tempPos++) << 7) < 0)
/* 1804 */           { x = (y ^ 0xFFFFFF80); }
/* 1805 */           else if ((y ^= UnsafeUtil.getByte(tempPos++) << 14) >= 0)
/* 1806 */           { x = (y ^ 0x3F80); }
/* 1807 */           else if ((y ^= UnsafeUtil.getByte(tempPos++) << 21) < 0)
/* 1808 */           { x = (y ^ 0xFFE03F80); }
/* 1809 */           else if ((x = y ^ UnsafeUtil.getByte(tempPos++) << 28L) >= 0L)
/* 1810 */           { x ^= 0xFE03F80L; }
/* 1811 */           else if ((x ^= UnsafeUtil.getByte(tempPos++) << 35L) < 0L)
/* 1812 */           { x ^= 0xFFFFFFF80FE03F80L; }
/* 1813 */           else if ((x ^= UnsafeUtil.getByte(tempPos++) << 42L) >= 0L)
/* 1814 */           { x ^= 0x3F80FE03F80L; }
/* 1815 */           else if ((x ^= UnsafeUtil.getByte(tempPos++) << 49L) < 0L)
/* 1816 */           { x ^= 0xFFFE03F80FE03F80L;
/*      */ 
/*      */             
/*      */              }
/*      */           
/*      */           else
/*      */           
/*      */           { 
/*      */             
/* 1825 */             x ^= UnsafeUtil.getByte(tempPos++) << 56L;
/* 1826 */             x ^= 0xFE03F80FE03F80L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1835 */             if (x < 0L && 
/* 1836 */               UnsafeUtil.getByte(tempPos++) < 0L)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1844 */               return readRawVarint64SlowPath(); }  }  this.pos = tempPos; return x; }  }  return readRawVarint64SlowPath();
/*      */     }
/*      */ 
/*      */     
/*      */     long readRawVarint64SlowPath() throws IOException {
/* 1849 */       long result = 0L;
/* 1850 */       for (int shift = 0; shift < 64; shift += 7) {
/* 1851 */         byte b = readRawByte();
/* 1852 */         result |= (b & Byte.MAX_VALUE) << shift;
/* 1853 */         if ((b & 0x80) == 0) {
/* 1854 */           return result;
/*      */         }
/*      */       } 
/* 1857 */       throw InvalidProtocolBufferException.malformedVarint();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readRawLittleEndian32() throws IOException {
/* 1862 */       long tempPos = this.pos;
/*      */       
/* 1864 */       if (this.limit - tempPos < 4L) {
/* 1865 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/*      */       
/* 1868 */       this.pos = tempPos + 4L;
/* 1869 */       return UnsafeUtil.getByte(tempPos) & 0xFF | (
/* 1870 */         UnsafeUtil.getByte(tempPos + 1L) & 0xFF) << 8 | (
/* 1871 */         UnsafeUtil.getByte(tempPos + 2L) & 0xFF) << 16 | (
/* 1872 */         UnsafeUtil.getByte(tempPos + 3L) & 0xFF) << 24;
/*      */     }
/*      */ 
/*      */     
/*      */     public long readRawLittleEndian64() throws IOException {
/* 1877 */       long tempPos = this.pos;
/*      */       
/* 1879 */       if (this.limit - tempPos < 8L) {
/* 1880 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/*      */       
/* 1883 */       this.pos = tempPos + 8L;
/* 1884 */       return UnsafeUtil.getByte(tempPos) & 0xFFL | (
/* 1885 */         UnsafeUtil.getByte(tempPos + 1L) & 0xFFL) << 8L | (
/* 1886 */         UnsafeUtil.getByte(tempPos + 2L) & 0xFFL) << 16L | (
/* 1887 */         UnsafeUtil.getByte(tempPos + 3L) & 0xFFL) << 24L | (
/* 1888 */         UnsafeUtil.getByte(tempPos + 4L) & 0xFFL) << 32L | (
/* 1889 */         UnsafeUtil.getByte(tempPos + 5L) & 0xFFL) << 40L | (
/* 1890 */         UnsafeUtil.getByte(tempPos + 6L) & 0xFFL) << 48L | (
/* 1891 */         UnsafeUtil.getByte(tempPos + 7L) & 0xFFL) << 56L;
/*      */     }
/*      */ 
/*      */     
/*      */     public void enableAliasing(boolean enabled) {
/* 1896 */       this.enableAliasing = enabled;
/*      */     }
/*      */ 
/*      */     
/*      */     public void resetSizeCounter() {
/* 1901 */       this.startPos = this.pos;
/*      */     }
/*      */ 
/*      */     
/*      */     public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
/* 1906 */       if (byteLimit < 0) {
/* 1907 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/* 1909 */       byteLimit += getTotalBytesRead();
/* 1910 */       int oldLimit = this.currentLimit;
/* 1911 */       if (byteLimit > oldLimit) {
/* 1912 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/* 1914 */       this.currentLimit = byteLimit;
/*      */       
/* 1916 */       recomputeBufferSizeAfterLimit();
/*      */       
/* 1918 */       return oldLimit;
/*      */     }
/*      */ 
/*      */     
/*      */     public void popLimit(int oldLimit) {
/* 1923 */       this.currentLimit = oldLimit;
/* 1924 */       recomputeBufferSizeAfterLimit();
/*      */     }
/*      */ 
/*      */     
/*      */     public int getBytesUntilLimit() {
/* 1929 */       if (this.currentLimit == Integer.MAX_VALUE) {
/* 1930 */         return -1;
/*      */       }
/*      */       
/* 1933 */       return this.currentLimit - getTotalBytesRead();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isAtEnd() throws IOException {
/* 1938 */       return (this.pos == this.limit);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getTotalBytesRead() {
/* 1943 */       return (int)(this.pos - this.startPos);
/*      */     }
/*      */ 
/*      */     
/*      */     public byte readRawByte() throws IOException {
/* 1948 */       if (this.pos == this.limit) {
/* 1949 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/* 1951 */       return UnsafeUtil.getByte(this.pos++);
/*      */     }
/*      */ 
/*      */     
/*      */     public byte[] readRawBytes(int length) throws IOException {
/* 1956 */       if (length >= 0 && length <= remaining()) {
/* 1957 */         byte[] bytes = new byte[length];
/* 1958 */         slice(this.pos, this.pos + length).get(bytes);
/* 1959 */         this.pos += length;
/* 1960 */         return bytes;
/*      */       } 
/*      */       
/* 1963 */       if (length <= 0) {
/* 1964 */         if (length == 0) {
/* 1965 */           return Internal.EMPTY_BYTE_ARRAY;
/*      */         }
/* 1967 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       } 
/*      */ 
/*      */       
/* 1971 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */ 
/*      */     
/*      */     public void skipRawBytes(int length) throws IOException {
/* 1976 */       if (length >= 0 && length <= remaining()) {
/*      */         
/* 1978 */         this.pos += length;
/*      */         
/*      */         return;
/*      */       } 
/* 1982 */       if (length < 0) {
/* 1983 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/* 1985 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */     
/*      */     private void recomputeBufferSizeAfterLimit() {
/* 1989 */       this.limit += this.bufferSizeAfterLimit;
/* 1990 */       int bufferEnd = (int)(this.limit - this.startPos);
/* 1991 */       if (bufferEnd > this.currentLimit) {
/*      */         
/* 1993 */         this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
/* 1994 */         this.limit -= this.bufferSizeAfterLimit;
/*      */       } else {
/* 1996 */         this.bufferSizeAfterLimit = 0;
/*      */       } 
/*      */     }
/*      */     
/*      */     private int remaining() {
/* 2001 */       return (int)(this.limit - this.pos);
/*      */     }
/*      */     
/*      */     private int bufferPos(long pos) {
/* 2005 */       return (int)(pos - this.address);
/*      */     }
/*      */     
/*      */     private ByteBuffer slice(long begin, long end) throws IOException {
/* 2009 */       int prevPos = this.buffer.position();
/* 2010 */       int prevLimit = this.buffer.limit();
/*      */       try {
/* 2012 */         this.buffer.position(bufferPos(begin));
/* 2013 */         this.buffer.limit(bufferPos(end));
/* 2014 */         return this.buffer.slice();
/* 2015 */       } catch (IllegalArgumentException e) {
/* 2016 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       } finally {
/* 2018 */         this.buffer.position(prevPos);
/* 2019 */         this.buffer.limit(prevLimit);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class StreamDecoder
/*      */     extends CodedInputStream
/*      */   {
/*      */     private final InputStream input;
/*      */ 
/*      */     
/*      */     private final byte[] buffer;
/*      */ 
/*      */     
/*      */     private int bufferSize;
/*      */ 
/*      */     
/*      */     private int bufferSizeAfterLimit;
/*      */     
/*      */     private int pos;
/*      */     
/*      */     private int lastTag;
/*      */     
/*      */     private int totalBytesRetired;
/*      */     
/* 2046 */     private int currentLimit = Integer.MAX_VALUE;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private RefillCallback refillCallback;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int readTag() throws IOException {
/* 2059 */       if (isAtEnd()) {
/* 2060 */         this.lastTag = 0;
/* 2061 */         return 0;
/*      */       } 
/*      */       
/* 2064 */       this.lastTag = readRawVarint32();
/* 2065 */       if (WireFormat.getTagFieldNumber(this.lastTag) == 0)
/*      */       {
/*      */         
/* 2068 */         throw InvalidProtocolBufferException.invalidTag();
/*      */       }
/* 2070 */       return this.lastTag;
/*      */     }
/*      */ 
/*      */     
/*      */     public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
/* 2075 */       if (this.lastTag != value) {
/* 2076 */         throw InvalidProtocolBufferException.invalidEndTag();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public int getLastTag() {
/* 2082 */       return this.lastTag;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean skipField(int tag) throws IOException {
/* 2087 */       switch (WireFormat.getTagWireType(tag)) {
/*      */         case 0:
/* 2089 */           skipRawVarint();
/* 2090 */           return true;
/*      */         case 1:
/* 2092 */           skipRawBytes(8);
/* 2093 */           return true;
/*      */         case 2:
/* 2095 */           skipRawBytes(readRawVarint32());
/* 2096 */           return true;
/*      */         case 3:
/* 2098 */           skipMessage();
/* 2099 */           checkLastTagWas(
/* 2100 */               WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
/* 2101 */           return true;
/*      */         case 4:
/* 2103 */           return false;
/*      */         case 5:
/* 2105 */           skipRawBytes(4);
/* 2106 */           return true;
/*      */       } 
/* 2108 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } public boolean skipField(int tag, CodedOutputStream output) throws IOException {
/*      */       long l;
/*      */       ByteString byteString;
/*      */       int endtag;
/*      */       int value;
/* 2114 */       switch (WireFormat.getTagWireType(tag)) {
/*      */         
/*      */         case 0:
/* 2117 */           l = readInt64();
/* 2118 */           output.writeRawVarint32(tag);
/* 2119 */           output.writeUInt64NoTag(l);
/* 2120 */           return true;
/*      */ 
/*      */         
/*      */         case 1:
/* 2124 */           l = readRawLittleEndian64();
/* 2125 */           output.writeRawVarint32(tag);
/* 2126 */           output.writeFixed64NoTag(l);
/* 2127 */           return true;
/*      */ 
/*      */         
/*      */         case 2:
/* 2131 */           byteString = readBytes();
/* 2132 */           output.writeRawVarint32(tag);
/* 2133 */           output.writeBytesNoTag(byteString);
/* 2134 */           return true;
/*      */ 
/*      */         
/*      */         case 3:
/* 2138 */           output.writeRawVarint32(tag);
/* 2139 */           skipMessage(output);
/*      */           
/* 2141 */           endtag = WireFormat.makeTag(
/* 2142 */               WireFormat.getTagFieldNumber(tag), 4);
/* 2143 */           checkLastTagWas(endtag);
/* 2144 */           output.writeRawVarint32(endtag);
/* 2145 */           return true;
/*      */ 
/*      */         
/*      */         case 4:
/* 2149 */           return false;
/*      */ 
/*      */         
/*      */         case 5:
/* 2153 */           value = readRawLittleEndian32();
/* 2154 */           output.writeRawVarint32(tag);
/* 2155 */           output.writeFixed32NoTag(value);
/* 2156 */           return true;
/*      */       } 
/*      */       
/* 2159 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */ 
/*      */     
/*      */     public void skipMessage() throws IOException {
/*      */       int tag;
/*      */       do {
/* 2166 */         tag = readTag();
/* 2167 */       } while (tag != 0 && skipField(tag));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void skipMessage(CodedOutputStream output) throws IOException {
/*      */       int tag;
/*      */       do {
/* 2176 */         tag = readTag();
/* 2177 */       } while (tag != 0 && skipField(tag, output));
/*      */     }
/*      */     
/*      */     private static interface RefillCallback {
/*      */       void onRefill();
/*      */     }
/*      */     
/*      */     private class SkippedDataSink implements RefillCallback {
/* 2185 */       private int lastPos = CodedInputStream.StreamDecoder.this.pos;
/*      */       
/*      */       private ByteArrayOutputStream byteArrayStream;
/*      */       
/*      */       public void onRefill() {
/* 2190 */         if (this.byteArrayStream == null) {
/* 2191 */           this.byteArrayStream = new ByteArrayOutputStream();
/*      */         }
/* 2193 */         this.byteArrayStream.write(CodedInputStream.StreamDecoder.this.buffer, this.lastPos, CodedInputStream.StreamDecoder.this.pos - this.lastPos);
/* 2194 */         this.lastPos = 0;
/*      */       }
/*      */ 
/*      */       
/*      */       ByteBuffer getSkippedData() {
/* 2199 */         if (this.byteArrayStream == null) {
/* 2200 */           return ByteBuffer.wrap(CodedInputStream.StreamDecoder.this.buffer, this.lastPos, CodedInputStream.StreamDecoder.this.pos - this.lastPos);
/*      */         }
/* 2202 */         this.byteArrayStream.write(CodedInputStream.StreamDecoder.this.buffer, this.lastPos, CodedInputStream.StreamDecoder.this.pos);
/* 2203 */         return ByteBuffer.wrap(this.byteArrayStream.toByteArray());
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public double readDouble() throws IOException {
/* 2213 */       return Double.longBitsToDouble(readRawLittleEndian64());
/*      */     }
/*      */ 
/*      */     
/*      */     public float readFloat() throws IOException {
/* 2218 */       return Float.intBitsToFloat(readRawLittleEndian32());
/*      */     }
/*      */ 
/*      */     
/*      */     public long readUInt64() throws IOException {
/* 2223 */       return readRawVarint64();
/*      */     }
/*      */ 
/*      */     
/*      */     public long readInt64() throws IOException {
/* 2228 */       return readRawVarint64();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readInt32() throws IOException {
/* 2233 */       return readRawVarint32();
/*      */     }
/*      */ 
/*      */     
/*      */     public long readFixed64() throws IOException {
/* 2238 */       return readRawLittleEndian64();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readFixed32() throws IOException {
/* 2243 */       return readRawLittleEndian32();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean readBool() throws IOException {
/* 2248 */       return (readRawVarint64() != 0L);
/*      */     }
/*      */ 
/*      */     
/*      */     public String readString() throws IOException {
/* 2253 */       int size = readRawVarint32();
/* 2254 */       if (size > 0 && size <= this.bufferSize - this.pos) {
/*      */ 
/*      */         
/* 2257 */         String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
/* 2258 */         this.pos += size;
/* 2259 */         return result;
/*      */       } 
/* 2261 */       if (size == 0) {
/* 2262 */         return "";
/*      */       }
/* 2264 */       if (size <= this.bufferSize) {
/* 2265 */         refillBuffer(size);
/* 2266 */         String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
/* 2267 */         this.pos += size;
/* 2268 */         return result;
/*      */       } 
/*      */       
/* 2271 */       return new String(readRawBytesSlowPath(size, false), Internal.UTF_8);
/*      */     }
/*      */     
/*      */     public String readStringRequireUtf8() throws IOException {
/*      */       byte[] bytes;
/* 2276 */       int tempPos, size = readRawVarint32();
/*      */       
/* 2278 */       int oldPos = this.pos;
/*      */       
/* 2280 */       if (size <= this.bufferSize - oldPos && size > 0)
/*      */       
/*      */       { 
/* 2283 */         bytes = this.buffer;
/* 2284 */         this.pos = oldPos + size;
/* 2285 */         tempPos = oldPos; }
/* 2286 */       else { if (size == 0)
/* 2287 */           return ""; 
/* 2288 */         if (size <= this.bufferSize) {
/* 2289 */           refillBuffer(size);
/* 2290 */           bytes = this.buffer;
/* 2291 */           tempPos = 0;
/* 2292 */           this.pos = tempPos + size;
/*      */         } else {
/*      */           
/* 2295 */           bytes = readRawBytesSlowPath(size, false);
/* 2296 */           tempPos = 0;
/*      */         }  }
/* 2298 */        return Utf8.decodeUtf8(bytes, tempPos, size);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2307 */       if (this.recursionDepth >= this.recursionLimit) {
/* 2308 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/* 2310 */       this.recursionDepth++;
/* 2311 */       builder.mergeFrom(this, extensionRegistry);
/* 2312 */       checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
/* 2313 */       this.recursionDepth--;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2323 */       if (this.recursionDepth >= this.recursionLimit) {
/* 2324 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/* 2326 */       this.recursionDepth++;
/* 2327 */       MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
/* 2328 */       checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
/* 2329 */       this.recursionDepth--;
/* 2330 */       return (T)messageLite;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
/* 2337 */       readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2344 */       int length = readRawVarint32();
/* 2345 */       if (this.recursionDepth >= this.recursionLimit) {
/* 2346 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/* 2348 */       int oldLimit = pushLimit(length);
/* 2349 */       this.recursionDepth++;
/* 2350 */       builder.mergeFrom(this, extensionRegistry);
/* 2351 */       checkLastTagWas(0);
/* 2352 */       this.recursionDepth--;
/* 2353 */       popLimit(oldLimit);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2360 */       int length = readRawVarint32();
/* 2361 */       if (this.recursionDepth >= this.recursionLimit) {
/* 2362 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/* 2364 */       int oldLimit = pushLimit(length);
/* 2365 */       this.recursionDepth++;
/* 2366 */       MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
/* 2367 */       checkLastTagWas(0);
/* 2368 */       this.recursionDepth--;
/* 2369 */       popLimit(oldLimit);
/* 2370 */       return (T)messageLite;
/*      */     }
/*      */ 
/*      */     
/*      */     public ByteString readBytes() throws IOException {
/* 2375 */       int size = readRawVarint32();
/* 2376 */       if (size <= this.bufferSize - this.pos && size > 0) {
/*      */ 
/*      */         
/* 2379 */         ByteString result = ByteString.copyFrom(this.buffer, this.pos, size);
/* 2380 */         this.pos += size;
/* 2381 */         return result;
/*      */       } 
/* 2383 */       if (size == 0) {
/* 2384 */         return ByteString.EMPTY;
/*      */       }
/* 2386 */       return readBytesSlowPath(size);
/*      */     }
/*      */ 
/*      */     
/*      */     public byte[] readByteArray() throws IOException {
/* 2391 */       int size = readRawVarint32();
/* 2392 */       if (size <= this.bufferSize - this.pos && size > 0) {
/*      */ 
/*      */         
/* 2395 */         byte[] result = Arrays.copyOfRange(this.buffer, this.pos, this.pos + size);
/* 2396 */         this.pos += size;
/* 2397 */         return result;
/*      */       } 
/*      */ 
/*      */       
/* 2401 */       return readRawBytesSlowPath(size, false);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public ByteBuffer readByteBuffer() throws IOException {
/* 2407 */       int size = readRawVarint32();
/* 2408 */       if (size <= this.bufferSize - this.pos && size > 0) {
/*      */         
/* 2410 */         ByteBuffer result = ByteBuffer.wrap(Arrays.copyOfRange(this.buffer, this.pos, this.pos + size));
/* 2411 */         this.pos += size;
/* 2412 */         return result;
/*      */       } 
/* 2414 */       if (size == 0) {
/* 2415 */         return Internal.EMPTY_BYTE_BUFFER;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2421 */       return ByteBuffer.wrap(readRawBytesSlowPath(size, true));
/*      */     }
/*      */ 
/*      */     
/*      */     public int readUInt32() throws IOException {
/* 2426 */       return readRawVarint32();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readEnum() throws IOException {
/* 2431 */       return readRawVarint32();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readSFixed32() throws IOException {
/* 2436 */       return readRawLittleEndian32();
/*      */     }
/*      */ 
/*      */     
/*      */     public long readSFixed64() throws IOException {
/* 2441 */       return readRawLittleEndian64();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readSInt32() throws IOException {
/* 2446 */       return decodeZigZag32(readRawVarint32());
/*      */     }
/*      */ 
/*      */     
/*      */     public long readSInt64() throws IOException {
/* 2451 */       return decodeZigZag64(readRawVarint64());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int readRawVarint32() throws IOException {
/* 2461 */       int tempPos = this.pos;
/*      */       
/* 2463 */       if (this.bufferSize != tempPos)
/*      */       
/*      */       { 
/*      */         
/* 2467 */         byte[] buffer = this.buffer;
/*      */         int x;
/* 2469 */         if ((x = buffer[tempPos++]) >= 0) {
/* 2470 */           this.pos = tempPos;
/* 2471 */           return x;
/* 2472 */         }  if (this.bufferSize - tempPos >= 9)
/*      */         
/* 2474 */         { if ((x ^= buffer[tempPos++] << 7) < 0)
/* 2475 */           { x ^= 0xFFFFFF80; }
/* 2476 */           else if ((x ^= buffer[tempPos++] << 14) >= 0)
/* 2477 */           { x ^= 0x3F80; }
/* 2478 */           else if ((x ^= buffer[tempPos++] << 21) < 0)
/* 2479 */           { x ^= 0xFFE03F80; }
/*      */           else
/* 2481 */           { int y = buffer[tempPos++];
/* 2482 */             x ^= y << 28;
/* 2483 */             x ^= 0xFE03F80;
/* 2484 */             if (y < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 2496 */               return (int)readRawVarint64SlowPath(); }  }  this.pos = tempPos; return x; }  }  return (int)readRawVarint64SlowPath();
/*      */     }
/*      */     
/*      */     private void skipRawVarint() throws IOException {
/* 2500 */       if (this.bufferSize - this.pos >= 10) {
/* 2501 */         skipRawVarintFastPath();
/*      */       } else {
/* 2503 */         skipRawVarintSlowPath();
/*      */       } 
/*      */     }
/*      */     
/*      */     private void skipRawVarintFastPath() throws IOException {
/* 2508 */       for (int i = 0; i < 10; i++) {
/* 2509 */         if (this.buffer[this.pos++] >= 0) {
/*      */           return;
/*      */         }
/*      */       } 
/* 2513 */       throw InvalidProtocolBufferException.malformedVarint();
/*      */     }
/*      */     
/*      */     private void skipRawVarintSlowPath() throws IOException {
/* 2517 */       for (int i = 0; i < 10; i++) {
/* 2518 */         if (readRawByte() >= 0) {
/*      */           return;
/*      */         }
/*      */       } 
/* 2522 */       throw InvalidProtocolBufferException.malformedVarint();
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
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public long readRawVarint64() throws IOException {
/* 2540 */       int tempPos = this.pos;
/*      */       
/* 2542 */       if (this.bufferSize != tempPos)
/*      */       
/*      */       { 
/*      */         
/* 2546 */         byte[] buffer = this.buffer;
/*      */         
/*      */         int y;
/* 2549 */         if ((y = buffer[tempPos++]) >= 0) {
/* 2550 */           this.pos = tempPos;
/* 2551 */           return y;
/* 2552 */         }  if (this.bufferSize - tempPos >= 9)
/*      */         { long x;
/* 2554 */           if ((y ^= buffer[tempPos++] << 7) < 0)
/* 2555 */           { x = (y ^ 0xFFFFFF80); }
/* 2556 */           else if ((y ^= buffer[tempPos++] << 14) >= 0)
/* 2557 */           { x = (y ^ 0x3F80); }
/* 2558 */           else if ((y ^= buffer[tempPos++] << 21) < 0)
/* 2559 */           { x = (y ^ 0xFFE03F80); }
/* 2560 */           else if ((x = y ^ buffer[tempPos++] << 28L) >= 0L)
/* 2561 */           { x ^= 0xFE03F80L; }
/* 2562 */           else if ((x ^= buffer[tempPos++] << 35L) < 0L)
/* 2563 */           { x ^= 0xFFFFFFF80FE03F80L; }
/* 2564 */           else if ((x ^= buffer[tempPos++] << 42L) >= 0L)
/* 2565 */           { x ^= 0x3F80FE03F80L; }
/* 2566 */           else if ((x ^= buffer[tempPos++] << 49L) < 0L)
/* 2567 */           { x ^= 0xFFFE03F80FE03F80L;
/*      */ 
/*      */             
/*      */              }
/*      */           
/*      */           else
/*      */           
/*      */           { 
/*      */             
/* 2576 */             x ^= buffer[tempPos++] << 56L;
/* 2577 */             x ^= 0xFE03F80FE03F80L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2586 */             if (x < 0L && 
/* 2587 */               buffer[tempPos++] < 0L)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 2595 */               return readRawVarint64SlowPath(); }  }  this.pos = tempPos; return x; }  }  return readRawVarint64SlowPath();
/*      */     }
/*      */ 
/*      */     
/*      */     long readRawVarint64SlowPath() throws IOException {
/* 2600 */       long result = 0L;
/* 2601 */       for (int shift = 0; shift < 64; shift += 7) {
/* 2602 */         byte b = readRawByte();
/* 2603 */         result |= (b & Byte.MAX_VALUE) << shift;
/* 2604 */         if ((b & 0x80) == 0) {
/* 2605 */           return result;
/*      */         }
/*      */       } 
/* 2608 */       throw InvalidProtocolBufferException.malformedVarint();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readRawLittleEndian32() throws IOException {
/* 2613 */       int tempPos = this.pos;
/*      */       
/* 2615 */       if (this.bufferSize - tempPos < 4) {
/* 2616 */         refillBuffer(4);
/* 2617 */         tempPos = this.pos;
/*      */       } 
/*      */       
/* 2620 */       byte[] buffer = this.buffer;
/* 2621 */       this.pos = tempPos + 4;
/* 2622 */       return buffer[tempPos] & 0xFF | (buffer[tempPos + 1] & 0xFF) << 8 | (buffer[tempPos + 2] & 0xFF) << 16 | (buffer[tempPos + 3] & 0xFF) << 24;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public long readRawLittleEndian64() throws IOException {
/* 2630 */       int tempPos = this.pos;
/*      */       
/* 2632 */       if (this.bufferSize - tempPos < 8) {
/* 2633 */         refillBuffer(8);
/* 2634 */         tempPos = this.pos;
/*      */       } 
/*      */       
/* 2637 */       byte[] buffer = this.buffer;
/* 2638 */       this.pos = tempPos + 8;
/* 2639 */       return buffer[tempPos] & 0xFFL | (buffer[tempPos + 1] & 0xFFL) << 8L | (buffer[tempPos + 2] & 0xFFL) << 16L | (buffer[tempPos + 3] & 0xFFL) << 24L | (buffer[tempPos + 4] & 0xFFL) << 32L | (buffer[tempPos + 5] & 0xFFL) << 40L | (buffer[tempPos + 6] & 0xFFL) << 48L | (buffer[tempPos + 7] & 0xFFL) << 56L;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void enableAliasing(boolean enabled) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void resetSizeCounter() {
/* 2658 */       this.totalBytesRetired = -this.pos;
/*      */     }
/*      */ 
/*      */     
/*      */     public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
/* 2663 */       if (byteLimit < 0) {
/* 2664 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/* 2666 */       byteLimit += this.totalBytesRetired + this.pos;
/* 2667 */       int oldLimit = this.currentLimit;
/* 2668 */       if (byteLimit > oldLimit) {
/* 2669 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/* 2671 */       this.currentLimit = byteLimit;
/*      */       
/* 2673 */       recomputeBufferSizeAfterLimit();
/*      */       
/* 2675 */       return oldLimit;
/*      */     }
/*      */     
/*      */     private void recomputeBufferSizeAfterLimit() {
/* 2679 */       this.bufferSize += this.bufferSizeAfterLimit;
/* 2680 */       int bufferEnd = this.totalBytesRetired + this.bufferSize;
/* 2681 */       if (bufferEnd > this.currentLimit) {
/*      */         
/* 2683 */         this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
/* 2684 */         this.bufferSize -= this.bufferSizeAfterLimit;
/*      */       } else {
/* 2686 */         this.bufferSizeAfterLimit = 0;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void popLimit(int oldLimit) {
/* 2692 */       this.currentLimit = oldLimit;
/* 2693 */       recomputeBufferSizeAfterLimit();
/*      */     }
/*      */ 
/*      */     
/*      */     public int getBytesUntilLimit() {
/* 2698 */       if (this.currentLimit == Integer.MAX_VALUE) {
/* 2699 */         return -1;
/*      */       }
/*      */       
/* 2702 */       int currentAbsolutePosition = this.totalBytesRetired + this.pos;
/* 2703 */       return this.currentLimit - currentAbsolutePosition;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isAtEnd() throws IOException {
/* 2708 */       return (this.pos == this.bufferSize && !tryRefillBuffer(1));
/*      */     }
/*      */ 
/*      */     
/*      */     public int getTotalBytesRead() {
/* 2713 */       return this.totalBytesRetired + this.pos;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private StreamDecoder(InputStream input, int bufferSize) {
/* 2720 */       this.refillCallback = null;
/*      */       Internal.checkNotNull(input, "input");
/*      */       this.input = input;
/*      */       this.buffer = new byte[bufferSize];
/*      */       this.bufferSize = 0;
/*      */       this.pos = 0;
/*      */       this.totalBytesRetired = 0;
/*      */     }
/*      */ 
/*      */     
/*      */     private void refillBuffer(int n) throws IOException {
/* 2731 */       if (!tryRefillBuffer(n)) {
/*      */ 
/*      */         
/* 2734 */         if (n > this.sizeLimit - this.totalBytesRetired - this.pos) {
/* 2735 */           throw InvalidProtocolBufferException.sizeLimitExceeded();
/*      */         }
/* 2737 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       } 
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
/*      */     private boolean tryRefillBuffer(int n) throws IOException {
/* 2751 */       if (this.pos + n <= this.bufferSize) {
/* 2752 */         throw new IllegalStateException("refillBuffer() called when " + n + " bytes were already available in buffer");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2759 */       if (n > this.sizeLimit - this.totalBytesRetired - this.pos) {
/* 2760 */         return false;
/*      */       }
/*      */ 
/*      */       
/* 2764 */       if (this.totalBytesRetired + this.pos + n > this.currentLimit)
/*      */       {
/* 2766 */         return false;
/*      */       }
/*      */       
/* 2769 */       if (this.refillCallback != null) {
/* 2770 */         this.refillCallback.onRefill();
/*      */       }
/*      */       
/* 2773 */       int tempPos = this.pos;
/* 2774 */       if (tempPos > 0) {
/* 2775 */         if (this.bufferSize > tempPos) {
/* 2776 */           System.arraycopy(this.buffer, tempPos, this.buffer, 0, this.bufferSize - tempPos);
/*      */         }
/* 2778 */         this.totalBytesRetired += tempPos;
/* 2779 */         this.bufferSize -= tempPos;
/* 2780 */         this.pos = 0;
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 2785 */       int bytesRead = this.input.read(this.buffer, this.bufferSize, 
/*      */ 
/*      */           
/* 2788 */           Math.min(this.buffer.length - this.bufferSize, this.sizeLimit - this.totalBytesRetired - this.bufferSize));
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2793 */       if (bytesRead == 0 || bytesRead < -1 || bytesRead > this.buffer.length) {
/* 2794 */         throw new IllegalStateException(this.input
/* 2795 */             .getClass() + "#read(byte[]) returned invalid result: " + bytesRead + "\nThe InputStream implementation is buggy.");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 2800 */       if (bytesRead > 0) {
/* 2801 */         this.bufferSize += bytesRead;
/* 2802 */         recomputeBufferSizeAfterLimit();
/* 2803 */         return (this.bufferSize >= n) ? true : tryRefillBuffer(n);
/*      */       } 
/*      */       
/* 2806 */       return false;
/*      */     }
/*      */ 
/*      */     
/*      */     public byte readRawByte() throws IOException {
/* 2811 */       if (this.pos == this.bufferSize) {
/* 2812 */         refillBuffer(1);
/*      */       }
/* 2814 */       return this.buffer[this.pos++];
/*      */     }
/*      */ 
/*      */     
/*      */     public byte[] readRawBytes(int size) throws IOException {
/* 2819 */       int tempPos = this.pos;
/* 2820 */       if (size <= this.bufferSize - tempPos && size > 0) {
/* 2821 */         this.pos = tempPos + size;
/* 2822 */         return Arrays.copyOfRange(this.buffer, tempPos, tempPos + size);
/*      */       } 
/*      */       
/* 2825 */       return readRawBytesSlowPath(size, false);
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
/*      */     private byte[] readRawBytesSlowPath(int size, boolean ensureNoLeakedReferences) throws IOException {
/* 2839 */       byte[] result = readRawBytesSlowPathOneChunk(size);
/* 2840 */       if (result != null) {
/* 2841 */         return ensureNoLeakedReferences ? (byte[])result.clone() : result;
/*      */       }
/*      */       
/* 2844 */       int originalBufferPos = this.pos;
/* 2845 */       int bufferedBytes = this.bufferSize - this.pos;
/*      */ 
/*      */       
/* 2848 */       this.totalBytesRetired += this.bufferSize;
/* 2849 */       this.pos = 0;
/* 2850 */       this.bufferSize = 0;
/*      */ 
/*      */       
/* 2853 */       int sizeLeft = size - bufferedBytes;
/*      */ 
/*      */ 
/*      */       
/* 2857 */       List<byte[]> chunks = readRawBytesSlowPathRemainingChunks(sizeLeft);
/*      */ 
/*      */       
/* 2860 */       byte[] bytes = new byte[size];
/*      */ 
/*      */       
/* 2863 */       System.arraycopy(this.buffer, originalBufferPos, bytes, 0, bufferedBytes);
/*      */ 
/*      */       
/* 2866 */       int tempPos = bufferedBytes;
/* 2867 */       for (byte[] chunk : chunks) {
/* 2868 */         System.arraycopy(chunk, 0, bytes, tempPos, chunk.length);
/* 2869 */         tempPos += chunk.length;
/*      */       } 
/*      */ 
/*      */       
/* 2873 */       return bytes;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private byte[] readRawBytesSlowPathOneChunk(int size) throws IOException {
/* 2883 */       if (size == 0) {
/* 2884 */         return Internal.EMPTY_BYTE_ARRAY;
/*      */       }
/* 2886 */       if (size < 0) {
/* 2887 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/*      */ 
/*      */       
/* 2891 */       int currentMessageSize = this.totalBytesRetired + this.pos + size;
/* 2892 */       if (currentMessageSize - this.sizeLimit > 0) {
/* 2893 */         throw InvalidProtocolBufferException.sizeLimitExceeded();
/*      */       }
/*      */ 
/*      */       
/* 2897 */       if (currentMessageSize > this.currentLimit) {
/*      */         
/* 2899 */         skipRawBytes(this.currentLimit - this.totalBytesRetired - this.pos);
/* 2900 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       } 
/*      */       
/* 2903 */       int bufferedBytes = this.bufferSize - this.pos;
/*      */       
/* 2905 */       int sizeLeft = size - bufferedBytes;
/*      */       
/* 2907 */       if (sizeLeft < 4096 || sizeLeft <= this.input.available()) {
/*      */ 
/*      */         
/* 2910 */         byte[] bytes = new byte[size];
/*      */ 
/*      */         
/* 2913 */         System.arraycopy(this.buffer, this.pos, bytes, 0, bufferedBytes);
/* 2914 */         this.totalBytesRetired += this.bufferSize;
/* 2915 */         this.pos = 0;
/* 2916 */         this.bufferSize = 0;
/*      */ 
/*      */         
/* 2919 */         int tempPos = bufferedBytes;
/* 2920 */         while (tempPos < bytes.length) {
/* 2921 */           int n = this.input.read(bytes, tempPos, size - tempPos);
/* 2922 */           if (n == -1) {
/* 2923 */             throw InvalidProtocolBufferException.truncatedMessage();
/*      */           }
/* 2925 */           this.totalBytesRetired += n;
/* 2926 */           tempPos += n;
/*      */         } 
/*      */         
/* 2929 */         return bytes;
/*      */       } 
/*      */       
/* 2932 */       return null;
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
/*      */ 
/*      */     
/*      */     private List<byte[]> readRawBytesSlowPathRemainingChunks(int sizeLeft) throws IOException {
/* 2948 */       List<byte[]> chunks = (List)new ArrayList<>();
/*      */       
/* 2950 */       while (sizeLeft > 0) {
/*      */         
/* 2952 */         byte[] chunk = new byte[Math.min(sizeLeft, 4096)];
/* 2953 */         int tempPos = 0;
/* 2954 */         while (tempPos < chunk.length) {
/* 2955 */           int n = this.input.read(chunk, tempPos, chunk.length - tempPos);
/* 2956 */           if (n == -1) {
/* 2957 */             throw InvalidProtocolBufferException.truncatedMessage();
/*      */           }
/* 2959 */           this.totalBytesRetired += n;
/* 2960 */           tempPos += n;
/*      */         } 
/* 2962 */         sizeLeft -= chunk.length;
/* 2963 */         chunks.add(chunk);
/*      */       } 
/*      */       
/* 2966 */       return chunks;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private ByteString readBytesSlowPath(int size) throws IOException {
/* 2974 */       byte[] result = readRawBytesSlowPathOneChunk(size);
/* 2975 */       if (result != null)
/*      */       {
/*      */         
/* 2978 */         return ByteString.copyFrom(result);
/*      */       }
/*      */       
/* 2981 */       int originalBufferPos = this.pos;
/* 2982 */       int bufferedBytes = this.bufferSize - this.pos;
/*      */ 
/*      */       
/* 2985 */       this.totalBytesRetired += this.bufferSize;
/* 2986 */       this.pos = 0;
/* 2987 */       this.bufferSize = 0;
/*      */ 
/*      */       
/* 2990 */       int sizeLeft = size - bufferedBytes;
/*      */ 
/*      */ 
/*      */       
/* 2994 */       List<byte[]> chunks = readRawBytesSlowPathRemainingChunks(sizeLeft);
/*      */ 
/*      */       
/* 2997 */       byte[] bytes = new byte[size];
/*      */ 
/*      */       
/* 3000 */       System.arraycopy(this.buffer, originalBufferPos, bytes, 0, bufferedBytes);
/*      */ 
/*      */       
/* 3003 */       int tempPos = bufferedBytes;
/* 3004 */       for (byte[] chunk : chunks) {
/* 3005 */         System.arraycopy(chunk, 0, bytes, tempPos, chunk.length);
/* 3006 */         tempPos += chunk.length;
/*      */       } 
/*      */       
/* 3009 */       return ByteString.wrap(bytes);
/*      */     }
/*      */ 
/*      */     
/*      */     public void skipRawBytes(int size) throws IOException {
/* 3014 */       if (size <= this.bufferSize - this.pos && size >= 0) {
/*      */         
/* 3016 */         this.pos += size;
/*      */       } else {
/* 3018 */         skipRawBytesSlowPath(size);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void skipRawBytesSlowPath(int size) throws IOException {
/* 3027 */       if (size < 0) {
/* 3028 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/*      */       
/* 3031 */       if (this.totalBytesRetired + this.pos + size > this.currentLimit) {
/*      */         
/* 3033 */         skipRawBytes(this.currentLimit - this.totalBytesRetired - this.pos);
/*      */         
/* 3035 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       } 
/*      */       
/* 3038 */       int totalSkipped = 0;
/* 3039 */       if (this.refillCallback == null) {
/*      */         
/* 3041 */         this.totalBytesRetired += this.pos;
/* 3042 */         totalSkipped = this.bufferSize - this.pos;
/* 3043 */         this.bufferSize = 0;
/* 3044 */         this.pos = 0;
/*      */         
/*      */         try {
/* 3047 */           while (totalSkipped < size) {
/* 3048 */             int toSkip = size - totalSkipped;
/* 3049 */             long skipped = this.input.skip(toSkip);
/* 3050 */             if (skipped < 0L || skipped > toSkip) {
/* 3051 */               throw new IllegalStateException(this.input
/* 3052 */                   .getClass() + "#skip returned invalid result: " + skipped + "\nThe InputStream implementation is buggy.");
/*      */             }
/*      */ 
/*      */             
/* 3056 */             if (skipped == 0L) {
/*      */               break;
/*      */             }
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 3063 */             totalSkipped += (int)skipped;
/*      */           } 
/*      */         } finally {
/* 3066 */           this.totalBytesRetired += totalSkipped;
/* 3067 */           recomputeBufferSizeAfterLimit();
/*      */         } 
/*      */       } 
/* 3070 */       if (totalSkipped < size) {
/*      */         
/* 3072 */         int tempPos = this.bufferSize - this.pos;
/* 3073 */         this.pos = this.bufferSize;
/*      */ 
/*      */ 
/*      */         
/* 3077 */         refillBuffer(1);
/* 3078 */         while (size - tempPos > this.bufferSize) {
/* 3079 */           tempPos += this.bufferSize;
/* 3080 */           this.pos = this.bufferSize;
/* 3081 */           refillBuffer(1);
/*      */         } 
/*      */         
/* 3084 */         this.pos = size - tempPos;
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class IterableDirectByteBufferDecoder
/*      */     extends CodedInputStream
/*      */   {
/*      */     private Iterable<ByteBuffer> input;
/*      */ 
/*      */     
/*      */     private Iterator<ByteBuffer> iterator;
/*      */ 
/*      */     
/*      */     private ByteBuffer currentByteBuffer;
/*      */ 
/*      */     
/*      */     private boolean immutable;
/*      */ 
/*      */     
/*      */     private boolean enableAliasing;
/*      */ 
/*      */     
/*      */     private int totalBufferSize;
/*      */ 
/*      */     
/*      */     private int bufferSizeAfterCurrentLimit;
/*      */ 
/*      */     
/* 3115 */     private int currentLimit = Integer.MAX_VALUE;
/*      */ 
/*      */ 
/*      */     
/*      */     private int lastTag;
/*      */ 
/*      */ 
/*      */     
/*      */     private int totalBytesRead;
/*      */ 
/*      */     
/*      */     private int startOffset;
/*      */ 
/*      */     
/*      */     private long currentByteBufferPos;
/*      */ 
/*      */     
/*      */     private long currentByteBufferStartPos;
/*      */ 
/*      */     
/*      */     private long currentAddress;
/*      */ 
/*      */     
/*      */     private long currentByteBufferLimit;
/*      */ 
/*      */ 
/*      */     
/*      */     private IterableDirectByteBufferDecoder(Iterable<ByteBuffer> inputBufs, int size, boolean immutableFlag) {
/* 3143 */       this.totalBufferSize = size;
/* 3144 */       this.input = inputBufs;
/* 3145 */       this.iterator = this.input.iterator();
/* 3146 */       this.immutable = immutableFlag;
/* 3147 */       this.startOffset = this.totalBytesRead = 0;
/* 3148 */       if (size == 0) {
/* 3149 */         this.currentByteBuffer = Internal.EMPTY_BYTE_BUFFER;
/* 3150 */         this.currentByteBufferPos = 0L;
/* 3151 */         this.currentByteBufferStartPos = 0L;
/* 3152 */         this.currentByteBufferLimit = 0L;
/* 3153 */         this.currentAddress = 0L;
/*      */       } else {
/* 3155 */         tryGetNextByteBuffer();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private void getNextByteBuffer() throws InvalidProtocolBufferException {
/* 3161 */       if (!this.iterator.hasNext()) {
/* 3162 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/* 3164 */       tryGetNextByteBuffer();
/*      */     }
/*      */     
/*      */     private void tryGetNextByteBuffer() {
/* 3168 */       this.currentByteBuffer = this.iterator.next();
/* 3169 */       this.totalBytesRead += (int)(this.currentByteBufferPos - this.currentByteBufferStartPos);
/* 3170 */       this.currentByteBufferPos = this.currentByteBuffer.position();
/* 3171 */       this.currentByteBufferStartPos = this.currentByteBufferPos;
/* 3172 */       this.currentByteBufferLimit = this.currentByteBuffer.limit();
/* 3173 */       this.currentAddress = UnsafeUtil.addressOffset(this.currentByteBuffer);
/* 3174 */       this.currentByteBufferPos += this.currentAddress;
/* 3175 */       this.currentByteBufferStartPos += this.currentAddress;
/* 3176 */       this.currentByteBufferLimit += this.currentAddress;
/*      */     }
/*      */ 
/*      */     
/*      */     public int readTag() throws IOException {
/* 3181 */       if (isAtEnd()) {
/* 3182 */         this.lastTag = 0;
/* 3183 */         return 0;
/*      */       } 
/*      */       
/* 3186 */       this.lastTag = readRawVarint32();
/* 3187 */       if (WireFormat.getTagFieldNumber(this.lastTag) == 0)
/*      */       {
/*      */         
/* 3190 */         throw InvalidProtocolBufferException.invalidTag();
/*      */       }
/* 3192 */       return this.lastTag;
/*      */     }
/*      */ 
/*      */     
/*      */     public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
/* 3197 */       if (this.lastTag != value) {
/* 3198 */         throw InvalidProtocolBufferException.invalidEndTag();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public int getLastTag() {
/* 3204 */       return this.lastTag;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean skipField(int tag) throws IOException {
/* 3209 */       switch (WireFormat.getTagWireType(tag)) {
/*      */         case 0:
/* 3211 */           skipRawVarint();
/* 3212 */           return true;
/*      */         case 1:
/* 3214 */           skipRawBytes(8);
/* 3215 */           return true;
/*      */         case 2:
/* 3217 */           skipRawBytes(readRawVarint32());
/* 3218 */           return true;
/*      */         case 3:
/* 3220 */           skipMessage();
/* 3221 */           checkLastTagWas(
/* 3222 */               WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
/* 3223 */           return true;
/*      */         case 4:
/* 3225 */           return false;
/*      */         case 5:
/* 3227 */           skipRawBytes(4);
/* 3228 */           return true;
/*      */       } 
/* 3230 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     } public boolean skipField(int tag, CodedOutputStream output) throws IOException {
/*      */       long l;
/*      */       ByteString byteString;
/*      */       int endtag;
/*      */       int value;
/* 3236 */       switch (WireFormat.getTagWireType(tag)) {
/*      */         
/*      */         case 0:
/* 3239 */           l = readInt64();
/* 3240 */           output.writeRawVarint32(tag);
/* 3241 */           output.writeUInt64NoTag(l);
/* 3242 */           return true;
/*      */ 
/*      */         
/*      */         case 1:
/* 3246 */           l = readRawLittleEndian64();
/* 3247 */           output.writeRawVarint32(tag);
/* 3248 */           output.writeFixed64NoTag(l);
/* 3249 */           return true;
/*      */ 
/*      */         
/*      */         case 2:
/* 3253 */           byteString = readBytes();
/* 3254 */           output.writeRawVarint32(tag);
/* 3255 */           output.writeBytesNoTag(byteString);
/* 3256 */           return true;
/*      */ 
/*      */         
/*      */         case 3:
/* 3260 */           output.writeRawVarint32(tag);
/* 3261 */           skipMessage(output);
/*      */           
/* 3263 */           endtag = WireFormat.makeTag(
/* 3264 */               WireFormat.getTagFieldNumber(tag), 4);
/* 3265 */           checkLastTagWas(endtag);
/* 3266 */           output.writeRawVarint32(endtag);
/* 3267 */           return true;
/*      */ 
/*      */         
/*      */         case 4:
/* 3271 */           return false;
/*      */ 
/*      */         
/*      */         case 5:
/* 3275 */           value = readRawLittleEndian32();
/* 3276 */           output.writeRawVarint32(tag);
/* 3277 */           output.writeFixed32NoTag(value);
/* 3278 */           return true;
/*      */       } 
/*      */       
/* 3281 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */ 
/*      */     
/*      */     public void skipMessage() throws IOException {
/*      */       int tag;
/*      */       do {
/* 3288 */         tag = readTag();
/* 3289 */       } while (tag != 0 && skipField(tag));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void skipMessage(CodedOutputStream output) throws IOException {
/*      */       int tag;
/*      */       do {
/* 3298 */         tag = readTag();
/* 3299 */       } while (tag != 0 && skipField(tag, output));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public double readDouble() throws IOException {
/* 3309 */       return Double.longBitsToDouble(readRawLittleEndian64());
/*      */     }
/*      */ 
/*      */     
/*      */     public float readFloat() throws IOException {
/* 3314 */       return Float.intBitsToFloat(readRawLittleEndian32());
/*      */     }
/*      */ 
/*      */     
/*      */     public long readUInt64() throws IOException {
/* 3319 */       return readRawVarint64();
/*      */     }
/*      */ 
/*      */     
/*      */     public long readInt64() throws IOException {
/* 3324 */       return readRawVarint64();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readInt32() throws IOException {
/* 3329 */       return readRawVarint32();
/*      */     }
/*      */ 
/*      */     
/*      */     public long readFixed64() throws IOException {
/* 3334 */       return readRawLittleEndian64();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readFixed32() throws IOException {
/* 3339 */       return readRawLittleEndian32();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean readBool() throws IOException {
/* 3344 */       return (readRawVarint64() != 0L);
/*      */     }
/*      */ 
/*      */     
/*      */     public String readString() throws IOException {
/* 3349 */       int size = readRawVarint32();
/* 3350 */       if (size > 0 && size <= this.currentByteBufferLimit - this.currentByteBufferPos) {
/* 3351 */         byte[] bytes = new byte[size];
/* 3352 */         UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, size);
/* 3353 */         String result = new String(bytes, Internal.UTF_8);
/* 3354 */         this.currentByteBufferPos += size;
/* 3355 */         return result;
/* 3356 */       }  if (size > 0 && size <= remaining()) {
/*      */         
/* 3358 */         byte[] bytes = new byte[size];
/* 3359 */         readRawBytesTo(bytes, 0, size);
/* 3360 */         String result = new String(bytes, Internal.UTF_8);
/* 3361 */         return result;
/*      */       } 
/*      */       
/* 3364 */       if (size == 0) {
/* 3365 */         return "";
/*      */       }
/* 3367 */       if (size < 0) {
/* 3368 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/* 3370 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */ 
/*      */     
/*      */     public String readStringRequireUtf8() throws IOException {
/* 3375 */       int size = readRawVarint32();
/* 3376 */       if (size > 0 && size <= this.currentByteBufferLimit - this.currentByteBufferPos) {
/* 3377 */         int bufferPos = (int)(this.currentByteBufferPos - this.currentByteBufferStartPos);
/* 3378 */         String result = Utf8.decodeUtf8(this.currentByteBuffer, bufferPos, size);
/* 3379 */         this.currentByteBufferPos += size;
/* 3380 */         return result;
/*      */       } 
/* 3382 */       if (size >= 0 && size <= remaining()) {
/* 3383 */         byte[] bytes = new byte[size];
/* 3384 */         readRawBytesTo(bytes, 0, size);
/* 3385 */         return Utf8.decodeUtf8(bytes, 0, size);
/*      */       } 
/*      */       
/* 3388 */       if (size == 0) {
/* 3389 */         return "";
/*      */       }
/* 3391 */       if (size <= 0) {
/* 3392 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/* 3394 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3403 */       if (this.recursionDepth >= this.recursionLimit) {
/* 3404 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/* 3406 */       this.recursionDepth++;
/* 3407 */       builder.mergeFrom(this, extensionRegistry);
/* 3408 */       checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
/* 3409 */       this.recursionDepth--;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3419 */       if (this.recursionDepth >= this.recursionLimit) {
/* 3420 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/* 3422 */       this.recursionDepth++;
/* 3423 */       MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
/* 3424 */       checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
/* 3425 */       this.recursionDepth--;
/* 3426 */       return (T)messageLite;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
/* 3433 */       readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3440 */       int length = readRawVarint32();
/* 3441 */       if (this.recursionDepth >= this.recursionLimit) {
/* 3442 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/* 3444 */       int oldLimit = pushLimit(length);
/* 3445 */       this.recursionDepth++;
/* 3446 */       builder.mergeFrom(this, extensionRegistry);
/* 3447 */       checkLastTagWas(0);
/* 3448 */       this.recursionDepth--;
/* 3449 */       popLimit(oldLimit);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 3456 */       int length = readRawVarint32();
/* 3457 */       if (this.recursionDepth >= this.recursionLimit) {
/* 3458 */         throw InvalidProtocolBufferException.recursionLimitExceeded();
/*      */       }
/* 3460 */       int oldLimit = pushLimit(length);
/* 3461 */       this.recursionDepth++;
/* 3462 */       MessageLite messageLite = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
/* 3463 */       checkLastTagWas(0);
/* 3464 */       this.recursionDepth--;
/* 3465 */       popLimit(oldLimit);
/* 3466 */       return (T)messageLite;
/*      */     }
/*      */ 
/*      */     
/*      */     public ByteString readBytes() throws IOException {
/* 3471 */       int size = readRawVarint32();
/* 3472 */       if (size > 0 && size <= this.currentByteBufferLimit - this.currentByteBufferPos) {
/* 3473 */         if (this.immutable && this.enableAliasing) {
/* 3474 */           int idx = (int)(this.currentByteBufferPos - this.currentAddress);
/* 3475 */           ByteString result = ByteString.wrap(slice(idx, idx + size));
/* 3476 */           this.currentByteBufferPos += size;
/* 3477 */           return result;
/*      */         } 
/*      */         
/* 3480 */         byte[] bytes = new byte[size];
/* 3481 */         UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, size);
/* 3482 */         this.currentByteBufferPos += size;
/* 3483 */         return ByteString.wrap(bytes);
/*      */       } 
/* 3485 */       if (size > 0 && size <= remaining()) {
/* 3486 */         byte[] temp = new byte[size];
/* 3487 */         readRawBytesTo(temp, 0, size);
/* 3488 */         return ByteString.wrap(temp);
/*      */       } 
/*      */       
/* 3491 */       if (size == 0) {
/* 3492 */         return ByteString.EMPTY;
/*      */       }
/* 3494 */       if (size < 0) {
/* 3495 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/* 3497 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */ 
/*      */     
/*      */     public byte[] readByteArray() throws IOException {
/* 3502 */       return readRawBytes(readRawVarint32());
/*      */     }
/*      */ 
/*      */     
/*      */     public ByteBuffer readByteBuffer() throws IOException {
/* 3507 */       int size = readRawVarint32();
/* 3508 */       if (size > 0 && size <= currentRemaining()) {
/* 3509 */         if (!this.immutable && this.enableAliasing) {
/* 3510 */           this.currentByteBufferPos += size;
/* 3511 */           return slice((int)(this.currentByteBufferPos - this.currentAddress - size), (int)(this.currentByteBufferPos - this.currentAddress));
/*      */         } 
/*      */ 
/*      */         
/* 3515 */         byte[] bytes = new byte[size];
/* 3516 */         UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, size);
/* 3517 */         this.currentByteBufferPos += size;
/* 3518 */         return ByteBuffer.wrap(bytes);
/*      */       } 
/* 3520 */       if (size > 0 && size <= remaining()) {
/* 3521 */         byte[] temp = new byte[size];
/* 3522 */         readRawBytesTo(temp, 0, size);
/* 3523 */         return ByteBuffer.wrap(temp);
/*      */       } 
/*      */       
/* 3526 */       if (size == 0) {
/* 3527 */         return Internal.EMPTY_BYTE_BUFFER;
/*      */       }
/* 3529 */       if (size < 0) {
/* 3530 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/* 3532 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readUInt32() throws IOException {
/* 3537 */       return readRawVarint32();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readEnum() throws IOException {
/* 3542 */       return readRawVarint32();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readSFixed32() throws IOException {
/* 3547 */       return readRawLittleEndian32();
/*      */     }
/*      */ 
/*      */     
/*      */     public long readSFixed64() throws IOException {
/* 3552 */       return readRawLittleEndian64();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readSInt32() throws IOException {
/* 3557 */       return decodeZigZag32(readRawVarint32());
/*      */     }
/*      */ 
/*      */     
/*      */     public long readSInt64() throws IOException {
/* 3562 */       return decodeZigZag64(readRawVarint64());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int readRawVarint32() throws IOException {
/* 3569 */       long tempPos = this.currentByteBufferPos;
/*      */       
/* 3571 */       if (this.currentByteBufferLimit != this.currentByteBufferPos)
/*      */       { int x;
/*      */ 
/*      */ 
/*      */         
/* 3576 */         if ((x = UnsafeUtil.getByte(tempPos++)) >= 0) {
/* 3577 */           this.currentByteBufferPos++;
/* 3578 */           return x;
/* 3579 */         }  if (this.currentByteBufferLimit - this.currentByteBufferPos >= 10L)
/*      */         
/* 3581 */         { if ((x ^= UnsafeUtil.getByte(tempPos++) << 7) < 0)
/* 3582 */           { x ^= 0xFFFFFF80; }
/* 3583 */           else if ((x ^= UnsafeUtil.getByte(tempPos++) << 14) >= 0)
/* 3584 */           { x ^= 0x3F80; }
/* 3585 */           else if ((x ^= UnsafeUtil.getByte(tempPos++) << 21) < 0)
/* 3586 */           { x ^= 0xFFE03F80; }
/*      */           else
/* 3588 */           { int y = UnsafeUtil.getByte(tempPos++);
/* 3589 */             x ^= y << 28;
/* 3590 */             x ^= 0xFE03F80;
/* 3591 */             if (y < 0 && 
/* 3592 */               UnsafeUtil.getByte(tempPos++) < 0 && 
/* 3593 */               UnsafeUtil.getByte(tempPos++) < 0 && 
/* 3594 */               UnsafeUtil.getByte(tempPos++) < 0 && 
/* 3595 */               UnsafeUtil.getByte(tempPos++) < 0 && 
/* 3596 */               UnsafeUtil.getByte(tempPos++) < 0)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 3603 */               return (int)readRawVarint64SlowPath(); }  }  this.currentByteBufferPos = tempPos; return x; }  }  return (int)readRawVarint64SlowPath();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public long readRawVarint64() throws IOException {
/* 3610 */       long tempPos = this.currentByteBufferPos;
/*      */       
/* 3612 */       if (this.currentByteBufferLimit != this.currentByteBufferPos)
/*      */       { int y;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3618 */         if ((y = UnsafeUtil.getByte(tempPos++)) >= 0) {
/* 3619 */           this.currentByteBufferPos++;
/* 3620 */           return y;
/* 3621 */         }  if (this.currentByteBufferLimit - this.currentByteBufferPos >= 10L)
/*      */         { long x;
/* 3623 */           if ((y ^= UnsafeUtil.getByte(tempPos++) << 7) < 0)
/* 3624 */           { x = (y ^ 0xFFFFFF80); }
/* 3625 */           else if ((y ^= UnsafeUtil.getByte(tempPos++) << 14) >= 0)
/* 3626 */           { x = (y ^ 0x3F80); }
/* 3627 */           else if ((y ^= UnsafeUtil.getByte(tempPos++) << 21) < 0)
/* 3628 */           { x = (y ^ 0xFFE03F80); }
/* 3629 */           else if ((x = y ^ UnsafeUtil.getByte(tempPos++) << 28L) >= 0L)
/* 3630 */           { x ^= 0xFE03F80L; }
/* 3631 */           else if ((x ^= UnsafeUtil.getByte(tempPos++) << 35L) < 0L)
/* 3632 */           { x ^= 0xFFFFFFF80FE03F80L; }
/* 3633 */           else if ((x ^= UnsafeUtil.getByte(tempPos++) << 42L) >= 0L)
/* 3634 */           { x ^= 0x3F80FE03F80L; }
/* 3635 */           else if ((x ^= UnsafeUtil.getByte(tempPos++) << 49L) < 0L)
/* 3636 */           { x ^= 0xFFFE03F80FE03F80L;
/*      */ 
/*      */             
/*      */              }
/*      */           
/*      */           else
/*      */           
/*      */           { 
/*      */             
/* 3645 */             x ^= UnsafeUtil.getByte(tempPos++) << 56L;
/* 3646 */             x ^= 0xFE03F80FE03F80L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 3655 */             if (x < 0L && 
/* 3656 */               UnsafeUtil.getByte(tempPos++) < 0L)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 3664 */               return readRawVarint64SlowPath(); }  }  this.currentByteBufferPos = tempPos; return x; }  }  return readRawVarint64SlowPath();
/*      */     }
/*      */ 
/*      */     
/*      */     long readRawVarint64SlowPath() throws IOException {
/* 3669 */       long result = 0L;
/* 3670 */       for (int shift = 0; shift < 64; shift += 7) {
/* 3671 */         byte b = readRawByte();
/* 3672 */         result |= (b & Byte.MAX_VALUE) << shift;
/* 3673 */         if ((b & 0x80) == 0) {
/* 3674 */           return result;
/*      */         }
/*      */       } 
/* 3677 */       throw InvalidProtocolBufferException.malformedVarint();
/*      */     }
/*      */ 
/*      */     
/*      */     public int readRawLittleEndian32() throws IOException {
/* 3682 */       if (currentRemaining() >= 4L) {
/* 3683 */         long tempPos = this.currentByteBufferPos;
/* 3684 */         this.currentByteBufferPos += 4L;
/* 3685 */         return UnsafeUtil.getByte(tempPos) & 0xFF | (
/* 3686 */           UnsafeUtil.getByte(tempPos + 1L) & 0xFF) << 8 | (
/* 3687 */           UnsafeUtil.getByte(tempPos + 2L) & 0xFF) << 16 | (
/* 3688 */           UnsafeUtil.getByte(tempPos + 3L) & 0xFF) << 24;
/*      */       } 
/* 3690 */       return readRawByte() & 0xFF | (
/* 3691 */         readRawByte() & 0xFF) << 8 | (
/* 3692 */         readRawByte() & 0xFF) << 16 | (
/* 3693 */         readRawByte() & 0xFF) << 24;
/*      */     }
/*      */ 
/*      */     
/*      */     public long readRawLittleEndian64() throws IOException {
/* 3698 */       if (currentRemaining() >= 8L) {
/* 3699 */         long tempPos = this.currentByteBufferPos;
/* 3700 */         this.currentByteBufferPos += 8L;
/* 3701 */         return UnsafeUtil.getByte(tempPos) & 0xFFL | (
/* 3702 */           UnsafeUtil.getByte(tempPos + 1L) & 0xFFL) << 8L | (
/* 3703 */           UnsafeUtil.getByte(tempPos + 2L) & 0xFFL) << 16L | (
/* 3704 */           UnsafeUtil.getByte(tempPos + 3L) & 0xFFL) << 24L | (
/* 3705 */           UnsafeUtil.getByte(tempPos + 4L) & 0xFFL) << 32L | (
/* 3706 */           UnsafeUtil.getByte(tempPos + 5L) & 0xFFL) << 40L | (
/* 3707 */           UnsafeUtil.getByte(tempPos + 6L) & 0xFFL) << 48L | (
/* 3708 */           UnsafeUtil.getByte(tempPos + 7L) & 0xFFL) << 56L;
/*      */       } 
/* 3710 */       return readRawByte() & 0xFFL | (
/* 3711 */         readRawByte() & 0xFFL) << 8L | (
/* 3712 */         readRawByte() & 0xFFL) << 16L | (
/* 3713 */         readRawByte() & 0xFFL) << 24L | (
/* 3714 */         readRawByte() & 0xFFL) << 32L | (
/* 3715 */         readRawByte() & 0xFFL) << 40L | (
/* 3716 */         readRawByte() & 0xFFL) << 48L | (
/* 3717 */         readRawByte() & 0xFFL) << 56L;
/*      */     }
/*      */ 
/*      */     
/*      */     public void enableAliasing(boolean enabled) {
/* 3722 */       this.enableAliasing = enabled;
/*      */     }
/*      */ 
/*      */     
/*      */     public void resetSizeCounter() {
/* 3727 */       this.startOffset = (int)(this.totalBytesRead + this.currentByteBufferPos - this.currentByteBufferStartPos);
/*      */     }
/*      */ 
/*      */     
/*      */     public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
/* 3732 */       if (byteLimit < 0) {
/* 3733 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/* 3735 */       byteLimit += getTotalBytesRead();
/* 3736 */       int oldLimit = this.currentLimit;
/* 3737 */       if (byteLimit > oldLimit) {
/* 3738 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       }
/* 3740 */       this.currentLimit = byteLimit;
/*      */       
/* 3742 */       recomputeBufferSizeAfterLimit();
/*      */       
/* 3744 */       return oldLimit;
/*      */     }
/*      */     
/*      */     private void recomputeBufferSizeAfterLimit() {
/* 3748 */       this.totalBufferSize += this.bufferSizeAfterCurrentLimit;
/* 3749 */       int bufferEnd = this.totalBufferSize - this.startOffset;
/* 3750 */       if (bufferEnd > this.currentLimit) {
/*      */         
/* 3752 */         this.bufferSizeAfterCurrentLimit = bufferEnd - this.currentLimit;
/* 3753 */         this.totalBufferSize -= this.bufferSizeAfterCurrentLimit;
/*      */       } else {
/* 3755 */         this.bufferSizeAfterCurrentLimit = 0;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void popLimit(int oldLimit) {
/* 3761 */       this.currentLimit = oldLimit;
/* 3762 */       recomputeBufferSizeAfterLimit();
/*      */     }
/*      */ 
/*      */     
/*      */     public int getBytesUntilLimit() {
/* 3767 */       if (this.currentLimit == Integer.MAX_VALUE) {
/* 3768 */         return -1;
/*      */       }
/*      */       
/* 3771 */       return this.currentLimit - getTotalBytesRead();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isAtEnd() throws IOException {
/* 3776 */       return (this.totalBytesRead + this.currentByteBufferPos - this.currentByteBufferStartPos == this.totalBufferSize);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getTotalBytesRead() {
/* 3781 */       return (int)((this.totalBytesRead - this.startOffset) + this.currentByteBufferPos - this.currentByteBufferStartPos);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte readRawByte() throws IOException {
/* 3787 */       if (currentRemaining() == 0L) {
/* 3788 */         getNextByteBuffer();
/*      */       }
/* 3790 */       return UnsafeUtil.getByte(this.currentByteBufferPos++);
/*      */     }
/*      */ 
/*      */     
/*      */     public byte[] readRawBytes(int length) throws IOException {
/* 3795 */       if (length >= 0 && length <= currentRemaining()) {
/* 3796 */         byte[] bytes = new byte[length];
/* 3797 */         UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, length);
/* 3798 */         this.currentByteBufferPos += length;
/* 3799 */         return bytes;
/*      */       } 
/* 3801 */       if (length >= 0 && length <= remaining()) {
/* 3802 */         byte[] bytes = new byte[length];
/* 3803 */         readRawBytesTo(bytes, 0, length);
/* 3804 */         return bytes;
/*      */       } 
/*      */       
/* 3807 */       if (length <= 0) {
/* 3808 */         if (length == 0) {
/* 3809 */           return Internal.EMPTY_BYTE_ARRAY;
/*      */         }
/* 3811 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       } 
/*      */ 
/*      */       
/* 3815 */       throw InvalidProtocolBufferException.truncatedMessage();
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
/*      */     private void readRawBytesTo(byte[] bytes, int offset, int length) throws IOException {
/* 3829 */       if (length >= 0 && length <= remaining()) {
/* 3830 */         int l = length;
/* 3831 */         while (l > 0) {
/* 3832 */           if (currentRemaining() == 0L) {
/* 3833 */             getNextByteBuffer();
/*      */           }
/* 3835 */           int bytesToCopy = Math.min(l, (int)currentRemaining());
/* 3836 */           UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, (length - l + offset), bytesToCopy);
/* 3837 */           l -= bytesToCopy;
/* 3838 */           this.currentByteBufferPos += bytesToCopy;
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/* 3843 */       if (length <= 0) {
/* 3844 */         if (length == 0) {
/*      */           return;
/*      */         }
/* 3847 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       } 
/*      */       
/* 3850 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */ 
/*      */     
/*      */     public void skipRawBytes(int length) throws IOException {
/* 3855 */       if (length >= 0 && length <= (this.totalBufferSize - this.totalBytesRead) - this.currentByteBufferPos + this.currentByteBufferStartPos) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3862 */         int l = length;
/* 3863 */         while (l > 0) {
/* 3864 */           if (currentRemaining() == 0L) {
/* 3865 */             getNextByteBuffer();
/*      */           }
/* 3867 */           int rl = Math.min(l, (int)currentRemaining());
/* 3868 */           l -= rl;
/* 3869 */           this.currentByteBufferPos += rl;
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/* 3874 */       if (length < 0) {
/* 3875 */         throw InvalidProtocolBufferException.negativeSize();
/*      */       }
/* 3877 */       throw InvalidProtocolBufferException.truncatedMessage();
/*      */     }
/*      */ 
/*      */     
/*      */     private void skipRawVarint() throws IOException {
/* 3882 */       for (int i = 0; i < 10; i++) {
/* 3883 */         if (readRawByte() >= 0) {
/*      */           return;
/*      */         }
/*      */       } 
/* 3887 */       throw InvalidProtocolBufferException.malformedVarint();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int remaining() {
/* 3896 */       return (int)((this.totalBufferSize - this.totalBytesRead) - this.currentByteBufferPos + this.currentByteBufferStartPos);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private long currentRemaining() {
/* 3906 */       return this.currentByteBufferLimit - this.currentByteBufferPos;
/*      */     }
/*      */     
/*      */     private ByteBuffer slice(int begin, int end) throws IOException {
/* 3910 */       int prevPos = this.currentByteBuffer.position();
/* 3911 */       int prevLimit = this.currentByteBuffer.limit();
/*      */       try {
/* 3913 */         this.currentByteBuffer.position(begin);
/* 3914 */         this.currentByteBuffer.limit(end);
/* 3915 */         return this.currentByteBuffer.slice();
/* 3916 */       } catch (IllegalArgumentException e) {
/* 3917 */         throw InvalidProtocolBufferException.truncatedMessage();
/*      */       } finally {
/* 3919 */         this.currentByteBuffer.position(prevPos);
/* 3920 */         this.currentByteBuffer.limit(prevLimit);
/*      */       } 
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\CodedInputStream.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */