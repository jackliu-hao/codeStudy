/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InvalidObjectException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.UnsupportedCharsetException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.NoSuchElementException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class ByteString
/*      */   implements Iterable<Byte>, Serializable
/*      */ {
/*      */   static final int CONCATENATE_BY_COPY_SIZE = 128;
/*      */   static final int MIN_READ_FROM_CHUNK_SIZE = 256;
/*      */   static final int MAX_READ_FROM_CHUNK_SIZE = 8192;
/*   94 */   public static final ByteString EMPTY = new LiteralByteString(Internal.EMPTY_BYTE_ARRAY);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class SystemByteArrayCopier
/*      */     implements ByteArrayCopier
/*      */   {
/*      */     private SystemByteArrayCopier() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] copyFrom(byte[] bytes, int offset, int size) {
/*  116 */       byte[] copy = new byte[size];
/*  117 */       System.arraycopy(bytes, offset, copy, 0, size);
/*  118 */       return copy;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class ArraysByteArrayCopier implements ByteArrayCopier {
/*      */     private ArraysByteArrayCopier() {}
/*      */     
/*      */     public byte[] copyFrom(byte[] bytes, int offset, int size) {
/*  126 */       return Arrays.copyOfRange(bytes, offset, offset + size);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  134 */   private static final ByteArrayCopier byteArrayCopier = Android.isOnAndroidDevice() ? new SystemByteArrayCopier() : new ArraysByteArrayCopier();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  142 */   private int hash = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int UNSIGNED_BYTE_MASK = 255;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteIterator iterator() {
/*  175 */     return new AbstractByteIterator() {
/*  176 */         private int position = 0;
/*  177 */         private final int limit = ByteString.this.size();
/*      */ 
/*      */         
/*      */         public boolean hasNext() {
/*  181 */           return (this.position < this.limit);
/*      */         }
/*      */ 
/*      */         
/*      */         public byte nextByte() {
/*  186 */           int currentPos = this.position;
/*  187 */           if (currentPos >= this.limit) {
/*  188 */             throw new NoSuchElementException();
/*      */           }
/*  190 */           this.position = currentPos + 1;
/*  191 */           return ByteString.this.internalByteAt(currentPos);
/*      */         }
/*      */       };
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
/*      */   static abstract class AbstractByteIterator
/*      */     implements ByteIterator
/*      */   {
/*      */     public final Byte next() {
/*  213 */       return Byte.valueOf(nextByte());
/*      */     }
/*      */ 
/*      */     
/*      */     public final void remove() {
/*  218 */       throw new UnsupportedOperationException();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isEmpty() {
/*  235 */     return (size() == 0);
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
/*      */   private static int toInt(byte value) {
/*  252 */     return value & 0xFF;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  262 */   private static final Comparator<ByteString> UNSIGNED_LEXICOGRAPHICAL_COMPARATOR = new Comparator<ByteString>()
/*      */     {
/*      */       public int compare(ByteString former, ByteString latter)
/*      */       {
/*  266 */         ByteString.ByteIterator formerBytes = former.iterator();
/*  267 */         ByteString.ByteIterator latterBytes = latter.iterator();
/*      */         
/*  269 */         while (formerBytes.hasNext() && latterBytes.hasNext()) {
/*      */ 
/*      */ 
/*      */           
/*  273 */           int result = Integer.compare(ByteString.toInt(formerBytes.nextByte()), ByteString.toInt(latterBytes.nextByte()));
/*  274 */           if (result != 0) {
/*  275 */             return result;
/*      */           }
/*      */         } 
/*      */         
/*  279 */         return Integer.compare(former.size(), latter.size());
/*      */       }
/*      */     };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Comparator<ByteString> unsignedLexicographicalComparator() {
/*  296 */     return UNSIGNED_LEXICOGRAPHICAL_COMPARATOR;
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
/*      */   public final ByteString substring(int beginIndex) {
/*  310 */     return substring(beginIndex, size());
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
/*      */   public final boolean startsWith(ByteString prefix) {
/*  333 */     return (size() >= prefix.size() && substring(0, prefix.size()).equals(prefix));
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
/*      */   public final boolean endsWith(ByteString suffix) {
/*  345 */     return (size() >= suffix.size() && substring(size() - suffix.size()).equals(suffix));
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
/*      */   public static ByteString copyFrom(byte[] bytes, int offset, int size) {
/*  361 */     checkRange(offset, offset + size, bytes.length);
/*  362 */     return new LiteralByteString(byteArrayCopier.copyFrom(bytes, offset, size));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteString copyFrom(byte[] bytes) {
/*  372 */     return copyFrom(bytes, 0, bytes.length);
/*      */   }
/*      */ 
/*      */   
/*      */   static ByteString wrap(ByteBuffer buffer) {
/*  377 */     if (buffer.hasArray()) {
/*  378 */       int offset = buffer.arrayOffset();
/*  379 */       return wrap(buffer.array(), offset + buffer.position(), buffer.remaining());
/*      */     } 
/*  381 */     return new NioByteString(buffer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static ByteString wrap(byte[] bytes) {
/*  391 */     return new LiteralByteString(bytes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static ByteString wrap(byte[] bytes, int offset, int length) {
/*  399 */     return new BoundedByteString(bytes, offset, length);
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
/*      */   public static ByteString copyFrom(ByteBuffer bytes, int size) {
/*  412 */     checkRange(0, size, bytes.remaining());
/*  413 */     byte[] copy = new byte[size];
/*  414 */     bytes.get(copy);
/*  415 */     return new LiteralByteString(copy);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteString copyFrom(ByteBuffer bytes) {
/*  425 */     return copyFrom(bytes, bytes.remaining());
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
/*      */   public static ByteString copyFrom(String text, String charsetName) throws UnsupportedEncodingException {
/*  439 */     return new LiteralByteString(text.getBytes(charsetName));
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
/*      */   public static ByteString copyFrom(String text, Charset charset) {
/*  451 */     return new LiteralByteString(text.getBytes(charset));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteString copyFromUtf8(String text) {
/*  462 */     return new LiteralByteString(text.getBytes(Internal.UTF_8));
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
/*      */   public static ByteString readFrom(InputStream streamToDrain) throws IOException {
/*  485 */     return readFrom(streamToDrain, 256, 8192);
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
/*      */   public static ByteString readFrom(InputStream streamToDrain, int chunkSize) throws IOException {
/*  505 */     return readFrom(streamToDrain, chunkSize, chunkSize);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteString readFrom(InputStream streamToDrain, int minChunkSize, int maxChunkSize) throws IOException {
/*  511 */     Collection<ByteString> results = new ArrayList<>();
/*      */ 
/*      */ 
/*      */     
/*  515 */     int chunkSize = minChunkSize;
/*      */     while (true) {
/*  517 */       ByteString chunk = readChunk(streamToDrain, chunkSize);
/*  518 */       if (chunk == null) {
/*      */         break;
/*      */       }
/*  521 */       results.add(chunk);
/*  522 */       chunkSize = Math.min(chunkSize * 2, maxChunkSize);
/*      */     } 
/*      */     
/*  525 */     return copyFrom(results);
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
/*      */   private static ByteString readChunk(InputStream in, int chunkSize) throws IOException {
/*  537 */     byte[] buf = new byte[chunkSize];
/*  538 */     int bytesRead = 0;
/*  539 */     while (bytesRead < chunkSize) {
/*  540 */       int count = in.read(buf, bytesRead, chunkSize - bytesRead);
/*  541 */       if (count == -1) {
/*      */         break;
/*      */       }
/*  544 */       bytesRead += count;
/*      */     } 
/*      */     
/*  547 */     if (bytesRead == 0) {
/*  548 */       return null;
/*      */     }
/*      */ 
/*      */     
/*  552 */     return copyFrom(buf, 0, bytesRead);
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
/*      */   public final ByteString concat(ByteString other) {
/*  569 */     if (Integer.MAX_VALUE - size() < other.size()) {
/*  570 */       throw new IllegalArgumentException("ByteString would be too long: " + 
/*  571 */           size() + "+" + other.size());
/*      */     }
/*      */     
/*  574 */     return RopeByteString.concatenate(this, other);
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
/*      */   public static ByteString copyFrom(Iterable<ByteString> byteStrings) {
/*      */     int size;
/*  591 */     if (!(byteStrings instanceof Collection)) {
/*  592 */       int tempSize = 0;
/*  593 */       Iterator<ByteString> iter = byteStrings.iterator();
/*  594 */       while (iter.hasNext()) {
/*  595 */         iter.next(); tempSize++;
/*  596 */       }  size = tempSize;
/*      */     } else {
/*  598 */       size = ((Collection)byteStrings).size();
/*      */     } 
/*      */     
/*  601 */     if (size == 0) {
/*  602 */       return EMPTY;
/*      */     }
/*      */     
/*  605 */     return balancedConcat(byteStrings.iterator(), size);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static ByteString balancedConcat(Iterator<ByteString> iterator, int length) {
/*      */     ByteString result;
/*  612 */     if (length < 1) {
/*  613 */       throw new IllegalArgumentException(String.format("length (%s) must be >= 1", new Object[] { Integer.valueOf(length) }));
/*      */     }
/*      */     
/*  616 */     if (length == 1) {
/*  617 */       result = iterator.next();
/*      */     } else {
/*  619 */       int halfLength = length >>> 1;
/*  620 */       ByteString left = balancedConcat(iterator, halfLength);
/*  621 */       ByteString right = balancedConcat(iterator, length - halfLength);
/*  622 */       result = left.concat(right);
/*      */     } 
/*  624 */     return result;
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
/*      */   public void copyTo(byte[] target, int offset) {
/*  641 */     copyTo(target, 0, offset, size());
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
/*      */   @Deprecated
/*      */   public final void copyTo(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
/*  657 */     checkRange(sourceOffset, sourceOffset + numberToCopy, size());
/*  658 */     checkRange(targetOffset, targetOffset + numberToCopy, target.length);
/*  659 */     if (numberToCopy > 0) {
/*  660 */       copyToInternal(target, sourceOffset, targetOffset, numberToCopy);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final byte[] toByteArray() {
/*  690 */     int size = size();
/*  691 */     if (size == 0) {
/*  692 */       return Internal.EMPTY_BYTE_ARRAY;
/*      */     }
/*  694 */     byte[] result = new byte[size];
/*  695 */     copyToInternal(result, 0, 0, size);
/*  696 */     return result;
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
/*      */   final void writeTo(OutputStream out, int sourceOffset, int numberToWrite) throws IOException {
/*  717 */     checkRange(sourceOffset, sourceOffset + numberToWrite, size());
/*  718 */     if (numberToWrite > 0) {
/*  719 */       writeToInternal(out, sourceOffset, numberToWrite);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final String toString(String charsetName) throws UnsupportedEncodingException {
/*      */     try {
/*  784 */       return toString(Charset.forName(charsetName));
/*  785 */     } catch (UnsupportedCharsetException e) {
/*  786 */       UnsupportedEncodingException exception = new UnsupportedEncodingException(charsetName);
/*  787 */       exception.initCause(e);
/*  788 */       throw exception;
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
/*      */   public final String toString(Charset charset) {
/*  800 */     return (size() == 0) ? "" : toStringInternal(charset);
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
/*      */   public final String toStringUtf8() {
/*  820 */     return toString(Internal.UTF_8);
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
/*      */   static abstract class LeafByteString
/*      */     extends ByteString
/*      */   {
/*      */     protected final int getTreeDepth() {
/*  876 */       return 0;
/*      */     }
/*      */ 
/*      */     
/*      */     protected final boolean isBalanced() {
/*  881 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     void writeToReverse(ByteOutput byteOutput) throws IOException {
/*  886 */       writeTo(byteOutput);
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
/*      */     abstract boolean equalsRange(ByteString param1ByteString, int param1Int1, int param1Int2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int hashCode() {
/*  908 */     int h = this.hash;
/*      */     
/*  910 */     if (h == 0) {
/*  911 */       int size = size();
/*  912 */       h = partialHash(size, 0, size);
/*  913 */       if (h == 0) {
/*  914 */         h = 1;
/*      */       }
/*  916 */       this.hash = h;
/*      */     } 
/*  918 */     return h;
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
/*      */   public static Output newOutput(int initialCapacity) {
/*  963 */     return new Output(initialCapacity);
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
/*      */   public static Output newOutput() {
/*  977 */     return new Output(128);
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
/*      */   public static final class Output
/*      */     extends OutputStream
/*      */   {
/*  991 */     private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
/*      */ 
/*      */     
/*      */     private final int initialCapacity;
/*      */ 
/*      */     
/*      */     private final ArrayList<ByteString> flushedBuffers;
/*      */ 
/*      */     
/*      */     private int flushedBuffersTotalBytes;
/*      */ 
/*      */     
/*      */     private byte[] buffer;
/*      */     
/*      */     private int bufferPos;
/*      */ 
/*      */     
/*      */     Output(int initialCapacity) {
/* 1009 */       if (initialCapacity < 0) {
/* 1010 */         throw new IllegalArgumentException("Buffer size < 0");
/*      */       }
/* 1012 */       this.initialCapacity = initialCapacity;
/* 1013 */       this.flushedBuffers = new ArrayList<>();
/* 1014 */       this.buffer = new byte[initialCapacity];
/*      */     }
/*      */ 
/*      */     
/*      */     public synchronized void write(int b) {
/* 1019 */       if (this.bufferPos == this.buffer.length) {
/* 1020 */         flushFullBuffer(1);
/*      */       }
/* 1022 */       this.buffer[this.bufferPos++] = (byte)b;
/*      */     }
/*      */ 
/*      */     
/*      */     public synchronized void write(byte[] b, int offset, int length) {
/* 1027 */       if (length <= this.buffer.length - this.bufferPos) {
/*      */         
/* 1029 */         System.arraycopy(b, offset, this.buffer, this.bufferPos, length);
/* 1030 */         this.bufferPos += length;
/*      */       } else {
/*      */         
/* 1033 */         int copySize = this.buffer.length - this.bufferPos;
/* 1034 */         System.arraycopy(b, offset, this.buffer, this.bufferPos, copySize);
/* 1035 */         offset += copySize;
/* 1036 */         length -= copySize;
/*      */ 
/*      */         
/* 1039 */         flushFullBuffer(length);
/* 1040 */         System.arraycopy(b, offset, this.buffer, 0, length);
/* 1041 */         this.bufferPos = length;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public synchronized ByteString toByteString() {
/* 1052 */       flushLastBuffer();
/* 1053 */       return ByteString.copyFrom(this.flushedBuffers);
/*      */     }
/*      */ 
/*      */     
/*      */     private byte[] copyArray(byte[] buffer, int length) {
/* 1058 */       byte[] result = new byte[length];
/* 1059 */       System.arraycopy(buffer, 0, result, 0, Math.min(buffer.length, length));
/* 1060 */       return result;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(OutputStream out) throws IOException {
/*      */       ByteString[] cachedFlushBuffers;
/*      */       byte[] cachedBuffer;
/*      */       int cachedBufferPos;
/* 1074 */       synchronized (this) {
/*      */ 
/*      */         
/* 1077 */         cachedFlushBuffers = this.flushedBuffers.<ByteString>toArray(new ByteString[this.flushedBuffers.size()]);
/* 1078 */         cachedBuffer = this.buffer;
/* 1079 */         cachedBufferPos = this.bufferPos;
/*      */       } 
/* 1081 */       for (ByteString byteString : cachedFlushBuffers) {
/* 1082 */         byteString.writeTo(out);
/*      */       }
/*      */       
/* 1085 */       out.write(copyArray(cachedBuffer, cachedBufferPos));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public synchronized int size() {
/* 1094 */       return this.flushedBuffersTotalBytes + this.bufferPos;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public synchronized void reset() {
/* 1102 */       this.flushedBuffers.clear();
/* 1103 */       this.flushedBuffersTotalBytes = 0;
/* 1104 */       this.bufferPos = 0;
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 1109 */       return String.format("<ByteString.Output@%s size=%d>", new Object[] {
/*      */             
/* 1111 */             Integer.toHexString(System.identityHashCode(this)), Integer.valueOf(size())
/*      */           });
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void flushFullBuffer(int minSize) {
/* 1119 */       this.flushedBuffers.add(new ByteString.LiteralByteString(this.buffer));
/* 1120 */       this.flushedBuffersTotalBytes += this.buffer.length;
/*      */ 
/*      */ 
/*      */       
/* 1124 */       int newSize = Math.max(this.initialCapacity, Math.max(minSize, this.flushedBuffersTotalBytes >>> 1));
/* 1125 */       this.buffer = new byte[newSize];
/* 1126 */       this.bufferPos = 0;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void flushLastBuffer() {
/* 1134 */       if (this.bufferPos < this.buffer.length) {
/* 1135 */         if (this.bufferPos > 0) {
/* 1136 */           byte[] bufferCopy = copyArray(this.buffer, this.bufferPos);
/* 1137 */           this.flushedBuffers.add(new ByteString.LiteralByteString(bufferCopy));
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/* 1142 */         this.flushedBuffers.add(new ByteString.LiteralByteString(this.buffer));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1148 */         this.buffer = EMPTY_BYTE_ARRAY;
/*      */       } 
/* 1150 */       this.flushedBuffersTotalBytes += this.bufferPos;
/* 1151 */       this.bufferPos = 0;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static CodedBuilder newCodedBuilder(int size) {
/* 1168 */     return new CodedBuilder(size);
/*      */   }
/*      */   
/*      */   static final class CodedBuilder
/*      */   {
/*      */     private final CodedOutputStream output;
/*      */     private final byte[] buffer;
/*      */     
/*      */     private CodedBuilder(int size) {
/* 1177 */       this.buffer = new byte[size];
/* 1178 */       this.output = CodedOutputStream.newInstance(this.buffer);
/*      */     }
/*      */     
/*      */     public ByteString build() {
/* 1182 */       this.output.checkNoSpaceLeft();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1187 */       return new ByteString.LiteralByteString(this.buffer);
/*      */     }
/*      */     
/*      */     public CodedOutputStream getCodedOutput() {
/* 1191 */       return this.output;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int peekCachedHashCode() {
/* 1221 */     return this.hash;
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
/*      */   static void checkIndex(int index, int size) {
/* 1244 */     if ((index | size - index + 1) < 0) {
/* 1245 */       if (index < 0) {
/* 1246 */         throw new ArrayIndexOutOfBoundsException("Index < 0: " + index);
/*      */       }
/* 1248 */       throw new ArrayIndexOutOfBoundsException("Index > length: " + index + ", " + size);
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
/*      */ 
/*      */   
/*      */   static int checkRange(int startIndex, int endIndex, int size) {
/* 1262 */     int length = endIndex - startIndex;
/* 1263 */     if ((startIndex | endIndex | length | size - endIndex) < 0) {
/* 1264 */       if (startIndex < 0) {
/* 1265 */         throw new IndexOutOfBoundsException("Beginning index: " + startIndex + " < 0");
/*      */       }
/* 1267 */       if (endIndex < startIndex) {
/* 1268 */         throw new IndexOutOfBoundsException("Beginning index larger than ending index: " + startIndex + ", " + endIndex);
/*      */       }
/*      */ 
/*      */       
/* 1272 */       throw new IndexOutOfBoundsException("End index: " + endIndex + " >= " + size);
/*      */     } 
/* 1274 */     return length;
/*      */   }
/*      */ 
/*      */   
/*      */   public final String toString() {
/* 1279 */     return String.format(Locale.ROOT, "<ByteString@%s size=%d contents=\"%s\">", new Object[] {
/*      */ 
/*      */           
/* 1282 */           Integer.toHexString(System.identityHashCode(this)), 
/* 1283 */           Integer.valueOf(size()), 
/* 1284 */           truncateAndEscapeForDisplay() });
/*      */   } public abstract byte byteAt(int paramInt); abstract byte internalByteAt(int paramInt); public abstract int size(); public abstract ByteString substring(int paramInt1, int paramInt2); protected abstract void copyToInternal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3); public abstract void copyTo(ByteBuffer paramByteBuffer); public abstract void writeTo(OutputStream paramOutputStream) throws IOException; abstract void writeToInternal(OutputStream paramOutputStream, int paramInt1, int paramInt2) throws IOException; abstract void writeTo(ByteOutput paramByteOutput) throws IOException; abstract void writeToReverse(ByteOutput paramByteOutput) throws IOException;
/*      */   public abstract ByteBuffer asReadOnlyByteBuffer();
/*      */   private String truncateAndEscapeForDisplay() {
/* 1288 */     int limit = 50;
/*      */     
/* 1290 */     return (size() <= 50) ? TextFormatEscaper.escapeBytes(this) : (TextFormatEscaper.escapeBytes(substring(0, 47)) + "...");
/*      */   }
/*      */   
/*      */   public abstract List<ByteBuffer> asReadOnlyByteBufferList();
/*      */   
/*      */   protected abstract String toStringInternal(Charset paramCharset);
/*      */   
/*      */   public abstract boolean isValidUtf8();
/*      */   
/*      */   protected abstract int partialIsValidUtf8(int paramInt1, int paramInt2, int paramInt3);
/*      */   
/*      */   public abstract boolean equals(Object paramObject);
/*      */   
/*      */   public abstract InputStream newInput();
/*      */   
/*      */   public abstract CodedInputStream newCodedInput();
/*      */   
/*      */   protected abstract int getTreeDepth();
/*      */   
/*      */   protected abstract boolean isBalanced();
/*      */   
/*      */   protected abstract int partialHash(int paramInt1, int paramInt2, int paramInt3);
/*      */   
/*      */   private static class LiteralByteString extends LeafByteString { LiteralByteString(byte[] bytes) {
/* 1314 */       if (bytes == null) {
/* 1315 */         throw new NullPointerException();
/*      */       }
/* 1317 */       this.bytes = bytes;
/*      */     }
/*      */ 
/*      */     
/*      */     private static final long serialVersionUID = 1L;
/*      */     protected final byte[] bytes;
/*      */     
/*      */     public byte byteAt(int index) {
/* 1325 */       return this.bytes[index];
/*      */     }
/*      */ 
/*      */     
/*      */     byte internalByteAt(int index) {
/* 1330 */       return this.bytes[index];
/*      */     }
/*      */ 
/*      */     
/*      */     public int size() {
/* 1335 */       return this.bytes.length;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final ByteString substring(int beginIndex, int endIndex) {
/* 1343 */       int length = checkRange(beginIndex, endIndex, size());
/*      */       
/* 1345 */       if (length == 0) {
/* 1346 */         return ByteString.EMPTY;
/*      */       }
/*      */       
/* 1349 */       return new ByteString.BoundedByteString(this.bytes, getOffsetIntoBytes() + beginIndex, length);
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
/*      */     protected void copyToInternal(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
/* 1361 */       System.arraycopy(this.bytes, sourceOffset, target, targetOffset, numberToCopy);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void copyTo(ByteBuffer target) {
/* 1366 */       target.put(this.bytes, getOffsetIntoBytes(), size());
/*      */     }
/*      */ 
/*      */     
/*      */     public final ByteBuffer asReadOnlyByteBuffer() {
/* 1371 */       return ByteBuffer.wrap(this.bytes, getOffsetIntoBytes(), size()).asReadOnlyBuffer();
/*      */     }
/*      */ 
/*      */     
/*      */     public final List<ByteBuffer> asReadOnlyByteBufferList() {
/* 1376 */       return Collections.singletonList(asReadOnlyByteBuffer());
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeTo(OutputStream outputStream) throws IOException {
/* 1381 */       outputStream.write(toByteArray());
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     final void writeToInternal(OutputStream outputStream, int sourceOffset, int numberToWrite) throws IOException {
/* 1387 */       outputStream.write(this.bytes, getOffsetIntoBytes() + sourceOffset, numberToWrite);
/*      */     }
/*      */ 
/*      */     
/*      */     final void writeTo(ByteOutput output) throws IOException {
/* 1392 */       output.writeLazy(this.bytes, getOffsetIntoBytes(), size());
/*      */     }
/*      */ 
/*      */     
/*      */     protected final String toStringInternal(Charset charset) {
/* 1397 */       return new String(this.bytes, getOffsetIntoBytes(), size(), charset);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final boolean isValidUtf8() {
/* 1405 */       int offset = getOffsetIntoBytes();
/* 1406 */       return Utf8.isValidUtf8(this.bytes, offset, offset + size());
/*      */     }
/*      */ 
/*      */     
/*      */     protected final int partialIsValidUtf8(int state, int offset, int length) {
/* 1411 */       int index = getOffsetIntoBytes() + offset;
/* 1412 */       return Utf8.partialIsValidUtf8(state, this.bytes, index, index + length);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final boolean equals(Object other) {
/* 1420 */       if (other == this) {
/* 1421 */         return true;
/*      */       }
/* 1423 */       if (!(other instanceof ByteString)) {
/* 1424 */         return false;
/*      */       }
/*      */       
/* 1427 */       if (size() != ((ByteString)other).size()) {
/* 1428 */         return false;
/*      */       }
/* 1430 */       if (size() == 0) {
/* 1431 */         return true;
/*      */       }
/*      */       
/* 1434 */       if (other instanceof LiteralByteString) {
/* 1435 */         LiteralByteString otherAsLiteral = (LiteralByteString)other;
/*      */ 
/*      */         
/* 1438 */         int thisHash = peekCachedHashCode();
/* 1439 */         int thatHash = otherAsLiteral.peekCachedHashCode();
/* 1440 */         if (thisHash != 0 && thatHash != 0 && thisHash != thatHash) {
/* 1441 */           return false;
/*      */         }
/*      */         
/* 1444 */         return equalsRange((LiteralByteString)other, 0, size());
/*      */       } 
/*      */       
/* 1447 */       return other.equals(this);
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
/*      */     final boolean equalsRange(ByteString other, int offset, int length) {
/* 1462 */       if (length > other.size()) {
/* 1463 */         throw new IllegalArgumentException("Length too large: " + length + size());
/*      */       }
/* 1465 */       if (offset + length > other.size()) {
/* 1466 */         throw new IllegalArgumentException("Ran off end of other: " + offset + ", " + length + ", " + other
/* 1467 */             .size());
/*      */       }
/*      */       
/* 1470 */       if (other instanceof LiteralByteString) {
/* 1471 */         LiteralByteString lbsOther = (LiteralByteString)other;
/* 1472 */         byte[] thisBytes = this.bytes;
/* 1473 */         byte[] otherBytes = lbsOther.bytes;
/* 1474 */         int thisLimit = getOffsetIntoBytes() + length;
/* 1475 */         int thisIndex = getOffsetIntoBytes();
/* 1476 */         int otherIndex = lbsOther.getOffsetIntoBytes() + offset;
/* 1477 */         for (; thisIndex < thisLimit; 
/* 1478 */           thisIndex++, otherIndex++) {
/* 1479 */           if (thisBytes[thisIndex] != otherBytes[otherIndex]) {
/* 1480 */             return false;
/*      */           }
/*      */         } 
/* 1483 */         return true;
/*      */       } 
/*      */       
/* 1486 */       return other.substring(offset, offset + length).equals(substring(0, length));
/*      */     }
/*      */ 
/*      */     
/*      */     protected final int partialHash(int h, int offset, int length) {
/* 1491 */       return Internal.partialHash(h, this.bytes, getOffsetIntoBytes() + offset, length);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final InputStream newInput() {
/* 1499 */       return new ByteArrayInputStream(this.bytes, getOffsetIntoBytes(), size());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final CodedInputStream newCodedInput() {
/* 1506 */       return CodedInputStream.newInstance(this.bytes, 
/* 1507 */           getOffsetIntoBytes(), size(), true);
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
/*      */     protected int getOffsetIntoBytes() {
/* 1519 */       return 0;
/*      */     } }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class BoundedByteString
/*      */     extends LiteralByteString
/*      */   {
/*      */     private final int bytesOffset;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final int bytesLength;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static final long serialVersionUID = 1L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     BoundedByteString(byte[] bytes, int offset, int length) {
/* 1550 */       super(bytes);
/* 1551 */       checkRange(offset, offset + length, bytes.length);
/*      */       
/* 1553 */       this.bytesOffset = offset;
/* 1554 */       this.bytesLength = length;
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
/*      */     public byte byteAt(int index) {
/* 1570 */       checkIndex(index, size());
/* 1571 */       return this.bytes[this.bytesOffset + index];
/*      */     }
/*      */ 
/*      */     
/*      */     byte internalByteAt(int index) {
/* 1576 */       return this.bytes[this.bytesOffset + index];
/*      */     }
/*      */ 
/*      */     
/*      */     public int size() {
/* 1581 */       return this.bytesLength;
/*      */     }
/*      */ 
/*      */     
/*      */     protected int getOffsetIntoBytes() {
/* 1586 */       return this.bytesOffset;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void copyToInternal(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
/* 1595 */       System.arraycopy(this.bytes, 
/* 1596 */           getOffsetIntoBytes() + sourceOffset, target, targetOffset, numberToCopy);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Object writeReplace() {
/* 1605 */       return ByteString.wrap(toByteArray());
/*      */     }
/*      */     
/*      */     private void readObject(ObjectInputStream in) throws IOException {
/* 1609 */       throw new InvalidObjectException("BoundedByteStream instances are not to be serialized directly");
/*      */     }
/*      */   }
/*      */   
/*      */   public static interface ByteIterator extends Iterator<Byte> {
/*      */     byte nextByte();
/*      */   }
/*      */   
/*      */   private static interface ByteArrayCopier {
/*      */     byte[] copyFrom(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ByteString.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */