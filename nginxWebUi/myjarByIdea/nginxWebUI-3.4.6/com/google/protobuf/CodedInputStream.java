package com.google.protobuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class CodedInputStream {
   private static final int DEFAULT_BUFFER_SIZE = 4096;
   private static final int DEFAULT_RECURSION_LIMIT = 100;
   private static final int DEFAULT_SIZE_LIMIT = Integer.MAX_VALUE;
   int recursionDepth;
   int recursionLimit;
   int sizeLimit;
   CodedInputStreamReader wrapper;
   private boolean shouldDiscardUnknownFields;

   public static CodedInputStream newInstance(InputStream input) {
      return newInstance(input, 4096);
   }

   public static CodedInputStream newInstance(InputStream input, int bufferSize) {
      if (bufferSize <= 0) {
         throw new IllegalArgumentException("bufferSize must be > 0");
      } else {
         return (CodedInputStream)(input == null ? newInstance(Internal.EMPTY_BYTE_ARRAY) : new StreamDecoder(input, bufferSize));
      }
   }

   public static CodedInputStream newInstance(Iterable<ByteBuffer> input) {
      return !CodedInputStream.UnsafeDirectNioDecoder.isSupported() ? newInstance((InputStream)(new IterableByteBufferInputStream(input))) : newInstance(input, false);
   }

   static CodedInputStream newInstance(Iterable<ByteBuffer> bufs, boolean bufferIsImmutable) {
      int flag = 0;
      int totalSize = 0;
      Iterator var4 = bufs.iterator();

      while(var4.hasNext()) {
         ByteBuffer buf = (ByteBuffer)var4.next();
         totalSize += buf.remaining();
         if (buf.hasArray()) {
            flag |= 1;
         } else if (buf.isDirect()) {
            flag |= 2;
         } else {
            flag |= 4;
         }
      }

      if (flag == 2) {
         return new IterableDirectByteBufferDecoder(bufs, totalSize, bufferIsImmutable);
      } else {
         return newInstance((InputStream)(new IterableByteBufferInputStream(bufs)));
      }
   }

   public static CodedInputStream newInstance(byte[] buf) {
      return newInstance(buf, 0, buf.length);
   }

   public static CodedInputStream newInstance(byte[] buf, int off, int len) {
      return newInstance(buf, off, len, false);
   }

   static CodedInputStream newInstance(byte[] buf, int off, int len, boolean bufferIsImmutable) {
      ArrayDecoder result = new ArrayDecoder(buf, off, len, bufferIsImmutable);

      try {
         result.pushLimit(len);
         return result;
      } catch (InvalidProtocolBufferException var6) {
         throw new IllegalArgumentException(var6);
      }
   }

   public static CodedInputStream newInstance(ByteBuffer buf) {
      return newInstance(buf, false);
   }

   static CodedInputStream newInstance(ByteBuffer buf, boolean bufferIsImmutable) {
      if (buf.hasArray()) {
         return newInstance(buf.array(), buf.arrayOffset() + buf.position(), buf.remaining(), bufferIsImmutable);
      } else if (buf.isDirect() && CodedInputStream.UnsafeDirectNioDecoder.isSupported()) {
         return new UnsafeDirectNioDecoder(buf, bufferIsImmutable);
      } else {
         byte[] buffer = new byte[buf.remaining()];
         buf.duplicate().get(buffer);
         return newInstance(buffer, 0, buffer.length, true);
      }
   }

   private CodedInputStream() {
      this.recursionLimit = 100;
      this.sizeLimit = Integer.MAX_VALUE;
      this.shouldDiscardUnknownFields = false;
   }

   public abstract int readTag() throws IOException;

   public abstract void checkLastTagWas(int var1) throws InvalidProtocolBufferException;

   public abstract int getLastTag();

   public abstract boolean skipField(int var1) throws IOException;

   /** @deprecated */
   @Deprecated
   public abstract boolean skipField(int var1, CodedOutputStream var2) throws IOException;

   public abstract void skipMessage() throws IOException;

   public abstract void skipMessage(CodedOutputStream var1) throws IOException;

   public abstract double readDouble() throws IOException;

   public abstract float readFloat() throws IOException;

   public abstract long readUInt64() throws IOException;

   public abstract long readInt64() throws IOException;

   public abstract int readInt32() throws IOException;

   public abstract long readFixed64() throws IOException;

   public abstract int readFixed32() throws IOException;

   public abstract boolean readBool() throws IOException;

   public abstract String readString() throws IOException;

   public abstract String readStringRequireUtf8() throws IOException;

   public abstract void readGroup(int var1, MessageLite.Builder var2, ExtensionRegistryLite var3) throws IOException;

   public abstract <T extends MessageLite> T readGroup(int var1, Parser<T> var2, ExtensionRegistryLite var3) throws IOException;

   /** @deprecated */
   @Deprecated
   public abstract void readUnknownGroup(int var1, MessageLite.Builder var2) throws IOException;

   public abstract void readMessage(MessageLite.Builder var1, ExtensionRegistryLite var2) throws IOException;

   public abstract <T extends MessageLite> T readMessage(Parser<T> var1, ExtensionRegistryLite var2) throws IOException;

   public abstract ByteString readBytes() throws IOException;

   public abstract byte[] readByteArray() throws IOException;

   public abstract ByteBuffer readByteBuffer() throws IOException;

   public abstract int readUInt32() throws IOException;

   public abstract int readEnum() throws IOException;

   public abstract int readSFixed32() throws IOException;

   public abstract long readSFixed64() throws IOException;

   public abstract int readSInt32() throws IOException;

   public abstract long readSInt64() throws IOException;

   public abstract int readRawVarint32() throws IOException;

   public abstract long readRawVarint64() throws IOException;

   abstract long readRawVarint64SlowPath() throws IOException;

   public abstract int readRawLittleEndian32() throws IOException;

   public abstract long readRawLittleEndian64() throws IOException;

   public abstract void enableAliasing(boolean var1);

   public final int setRecursionLimit(int limit) {
      if (limit < 0) {
         throw new IllegalArgumentException("Recursion limit cannot be negative: " + limit);
      } else {
         int oldLimit = this.recursionLimit;
         this.recursionLimit = limit;
         return oldLimit;
      }
   }

   public final int setSizeLimit(int limit) {
      if (limit < 0) {
         throw new IllegalArgumentException("Size limit cannot be negative: " + limit);
      } else {
         int oldLimit = this.sizeLimit;
         this.sizeLimit = limit;
         return oldLimit;
      }
   }

   final void discardUnknownFields() {
      this.shouldDiscardUnknownFields = true;
   }

   final void unsetDiscardUnknownFields() {
      this.shouldDiscardUnknownFields = false;
   }

   final boolean shouldDiscardUnknownFields() {
      return this.shouldDiscardUnknownFields;
   }

   public abstract void resetSizeCounter();

   public abstract int pushLimit(int var1) throws InvalidProtocolBufferException;

   public abstract void popLimit(int var1);

   public abstract int getBytesUntilLimit();

   public abstract boolean isAtEnd() throws IOException;

   public abstract int getTotalBytesRead();

   public abstract byte readRawByte() throws IOException;

   public abstract byte[] readRawBytes(int var1) throws IOException;

   public abstract void skipRawBytes(int var1) throws IOException;

   public static int decodeZigZag32(int n) {
      return n >>> 1 ^ -(n & 1);
   }

   public static long decodeZigZag64(long n) {
      return n >>> 1 ^ -(n & 1L);
   }

   public static int readRawVarint32(int firstByte, InputStream input) throws IOException {
      if ((firstByte & 128) == 0) {
         return firstByte;
      } else {
         int result = firstByte & 127;

         int offset;
         int b;
         for(offset = 7; offset < 32; offset += 7) {
            b = input.read();
            if (b == -1) {
               throw InvalidProtocolBufferException.truncatedMessage();
            }

            result |= (b & 127) << offset;
            if ((b & 128) == 0) {
               return result;
            }
         }

         while(offset < 64) {
            b = input.read();
            if (b == -1) {
               throw InvalidProtocolBufferException.truncatedMessage();
            }

            if ((b & 128) == 0) {
               return result;
            }

            offset += 7;
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }
   }

   static int readRawVarint32(InputStream input) throws IOException {
      int firstByte = input.read();
      if (firstByte == -1) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return readRawVarint32(firstByte, input);
      }
   }

   // $FF: synthetic method
   CodedInputStream(Object x0) {
      this();
   }

   private static final class IterableDirectByteBufferDecoder extends CodedInputStream {
      private Iterable<ByteBuffer> input;
      private Iterator<ByteBuffer> iterator;
      private ByteBuffer currentByteBuffer;
      private boolean immutable;
      private boolean enableAliasing;
      private int totalBufferSize;
      private int bufferSizeAfterCurrentLimit;
      private int currentLimit;
      private int lastTag;
      private int totalBytesRead;
      private int startOffset;
      private long currentByteBufferPos;
      private long currentByteBufferStartPos;
      private long currentAddress;
      private long currentByteBufferLimit;

      private IterableDirectByteBufferDecoder(Iterable<ByteBuffer> inputBufs, int size, boolean immutableFlag) {
         super(null);
         this.currentLimit = Integer.MAX_VALUE;
         this.totalBufferSize = size;
         this.input = inputBufs;
         this.iterator = this.input.iterator();
         this.immutable = immutableFlag;
         this.startOffset = this.totalBytesRead = 0;
         if (size == 0) {
            this.currentByteBuffer = Internal.EMPTY_BYTE_BUFFER;
            this.currentByteBufferPos = 0L;
            this.currentByteBufferStartPos = 0L;
            this.currentByteBufferLimit = 0L;
            this.currentAddress = 0L;
         } else {
            this.tryGetNextByteBuffer();
         }

      }

      private void getNextByteBuffer() throws InvalidProtocolBufferException {
         if (!this.iterator.hasNext()) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.tryGetNextByteBuffer();
         }
      }

      private void tryGetNextByteBuffer() {
         this.currentByteBuffer = (ByteBuffer)this.iterator.next();
         this.totalBytesRead += (int)(this.currentByteBufferPos - this.currentByteBufferStartPos);
         this.currentByteBufferPos = (long)this.currentByteBuffer.position();
         this.currentByteBufferStartPos = this.currentByteBufferPos;
         this.currentByteBufferLimit = (long)this.currentByteBuffer.limit();
         this.currentAddress = UnsafeUtil.addressOffset(this.currentByteBuffer);
         this.currentByteBufferPos += this.currentAddress;
         this.currentByteBufferStartPos += this.currentAddress;
         this.currentByteBufferLimit += this.currentAddress;
      }

      public int readTag() throws IOException {
         if (this.isAtEnd()) {
            this.lastTag = 0;
            return 0;
         } else {
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
               throw InvalidProtocolBufferException.invalidTag();
            } else {
               return this.lastTag;
            }
         }
      }

      public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
         if (this.lastTag != value) {
            throw InvalidProtocolBufferException.invalidEndTag();
         }
      }

      public int getLastTag() {
         return this.lastTag;
      }

      public boolean skipField(int var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public boolean skipField(int var1, CodedOutputStream var2) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void skipMessage() throws IOException {
         int tag;
         do {
            tag = this.readTag();
         } while(tag != 0 && this.skipField(tag));

      }

      public void skipMessage(CodedOutputStream output) throws IOException {
         int tag;
         do {
            tag = this.readTag();
         } while(tag != 0 && this.skipField(tag, output));

      }

      public double readDouble() throws IOException {
         return Double.longBitsToDouble(this.readRawLittleEndian64());
      }

      public float readFloat() throws IOException {
         return Float.intBitsToFloat(this.readRawLittleEndian32());
      }

      public long readUInt64() throws IOException {
         return this.readRawVarint64();
      }

      public long readInt64() throws IOException {
         return this.readRawVarint64();
      }

      public int readInt32() throws IOException {
         return this.readRawVarint32();
      }

      public long readFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      public int readFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      public boolean readBool() throws IOException {
         return this.readRawVarint64() != 0L;
      }

      public String readString() throws IOException {
         int size = this.readRawVarint32();
         byte[] bytes;
         String result;
         if (size > 0 && (long)size <= this.currentByteBufferLimit - this.currentByteBufferPos) {
            bytes = new byte[size];
            UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, (long)size);
            result = new String(bytes, Internal.UTF_8);
            this.currentByteBufferPos += (long)size;
            return result;
         } else if (size > 0 && size <= this.remaining()) {
            bytes = new byte[size];
            this.readRawBytesTo(bytes, 0, size);
            result = new String(bytes, Internal.UTF_8);
            return result;
         } else if (size == 0) {
            return "";
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      public String readStringRequireUtf8() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && (long)size <= this.currentByteBufferLimit - this.currentByteBufferPos) {
            int bufferPos = (int)(this.currentByteBufferPos - this.currentByteBufferStartPos);
            String result = Utf8.decodeUtf8(this.currentByteBuffer, bufferPos, size);
            this.currentByteBufferPos += (long)size;
            return result;
         } else if (size >= 0 && size <= this.remaining()) {
            byte[] bytes = new byte[size];
            this.readRawBytesTo(bytes, 0, size);
            return Utf8.decodeUtf8((byte[])bytes, 0, size);
         } else if (size == 0) {
            return "";
         } else if (size <= 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            ++this.recursionDepth;
            builder.mergeFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.recursionDepth;
         }
      }

      public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            ++this.recursionDepth;
            T result = (MessageLite)parser.parsePartialFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.recursionDepth;
            return result;
         }
      }

      /** @deprecated */
      @Deprecated
      public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
         this.readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
      }

      public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int oldLimit = this.pushLimit(length);
            ++this.recursionDepth;
            builder.mergeFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.recursionDepth;
            this.popLimit(oldLimit);
         }
      }

      public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int oldLimit = this.pushLimit(length);
            ++this.recursionDepth;
            T result = (MessageLite)parser.parsePartialFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.recursionDepth;
            this.popLimit(oldLimit);
            return result;
         }
      }

      public ByteString readBytes() throws IOException {
         int size = this.readRawVarint32();
         byte[] temp;
         if (size > 0 && (long)size <= this.currentByteBufferLimit - this.currentByteBufferPos) {
            if (this.immutable && this.enableAliasing) {
               int idx = (int)(this.currentByteBufferPos - this.currentAddress);
               ByteString result = ByteString.wrap(this.slice(idx, idx + size));
               this.currentByteBufferPos += (long)size;
               return result;
            } else {
               temp = new byte[size];
               UnsafeUtil.copyMemory(this.currentByteBufferPos, temp, 0L, (long)size);
               this.currentByteBufferPos += (long)size;
               return ByteString.wrap(temp);
            }
         } else if (size > 0 && size <= this.remaining()) {
            temp = new byte[size];
            this.readRawBytesTo(temp, 0, size);
            return ByteString.wrap(temp);
         } else if (size == 0) {
            return ByteString.EMPTY;
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      public byte[] readByteArray() throws IOException {
         return this.readRawBytes(this.readRawVarint32());
      }

      public ByteBuffer readByteBuffer() throws IOException {
         int size = this.readRawVarint32();
         byte[] temp;
         if (size > 0 && (long)size <= this.currentRemaining()) {
            if (!this.immutable && this.enableAliasing) {
               this.currentByteBufferPos += (long)size;
               return this.slice((int)(this.currentByteBufferPos - this.currentAddress - (long)size), (int)(this.currentByteBufferPos - this.currentAddress));
            } else {
               temp = new byte[size];
               UnsafeUtil.copyMemory(this.currentByteBufferPos, temp, 0L, (long)size);
               this.currentByteBufferPos += (long)size;
               return ByteBuffer.wrap(temp);
            }
         } else if (size > 0 && size <= this.remaining()) {
            temp = new byte[size];
            this.readRawBytesTo(temp, 0, size);
            return ByteBuffer.wrap(temp);
         } else if (size == 0) {
            return Internal.EMPTY_BYTE_BUFFER;
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      public int readUInt32() throws IOException {
         return this.readRawVarint32();
      }

      public int readEnum() throws IOException {
         return this.readRawVarint32();
      }

      public int readSFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      public long readSFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      public int readSInt32() throws IOException {
         return decodeZigZag32(this.readRawVarint32());
      }

      public long readSInt64() throws IOException {
         return decodeZigZag64(this.readRawVarint64());
      }

      public int readRawVarint32() throws IOException {
         long tempPos;
         int x;
         label47: {
            tempPos = this.currentByteBufferPos;
            if (this.currentByteBufferLimit != this.currentByteBufferPos) {
               if ((x = UnsafeUtil.getByte(tempPos++)) >= 0) {
                  ++this.currentByteBufferPos;
                  return x;
               }

               if (this.currentByteBufferLimit - this.currentByteBufferPos >= 10L) {
                  if ((x ^= UnsafeUtil.getByte(tempPos++) << 7) < 0) {
                     x ^= -128;
                     break label47;
                  }

                  if ((x ^= UnsafeUtil.getByte(tempPos++) << 14) >= 0) {
                     x ^= 16256;
                     break label47;
                  }

                  if ((x ^= UnsafeUtil.getByte(tempPos++) << 21) < 0) {
                     x ^= -2080896;
                     break label47;
                  }

                  int y = UnsafeUtil.getByte(tempPos++);
                  x ^= y << 28;
                  x ^= 266354560;
                  if (y >= 0 || UnsafeUtil.getByte(tempPos++) >= 0 || UnsafeUtil.getByte(tempPos++) >= 0 || UnsafeUtil.getByte(tempPos++) >= 0 || UnsafeUtil.getByte(tempPos++) >= 0 || UnsafeUtil.getByte(tempPos++) >= 0) {
                     break label47;
                  }
               }
            }

            return (int)this.readRawVarint64SlowPath();
         }

         this.currentByteBufferPos = tempPos;
         return x;
      }

      public long readRawVarint64() throws IOException {
         long tempPos;
         long x;
         label51: {
            tempPos = this.currentByteBufferPos;
            if (this.currentByteBufferLimit != this.currentByteBufferPos) {
               int y;
               if ((y = UnsafeUtil.getByte(tempPos++)) >= 0) {
                  ++this.currentByteBufferPos;
                  return (long)y;
               }

               if (this.currentByteBufferLimit - this.currentByteBufferPos >= 10L) {
                  if ((y ^= UnsafeUtil.getByte(tempPos++) << 7) < 0) {
                     x = (long)(y ^ -128);
                     break label51;
                  }

                  if ((y ^= UnsafeUtil.getByte(tempPos++) << 14) >= 0) {
                     x = (long)(y ^ 16256);
                     break label51;
                  }

                  if ((y ^= UnsafeUtil.getByte(tempPos++) << 21) < 0) {
                     x = (long)(y ^ -2080896);
                     break label51;
                  }

                  if ((x = (long)y ^ (long)UnsafeUtil.getByte(tempPos++) << 28) >= 0L) {
                     x ^= 266354560L;
                     break label51;
                  }

                  if ((x ^= (long)UnsafeUtil.getByte(tempPos++) << 35) < 0L) {
                     x ^= -34093383808L;
                     break label51;
                  }

                  if ((x ^= (long)UnsafeUtil.getByte(tempPos++) << 42) >= 0L) {
                     x ^= 4363953127296L;
                     break label51;
                  }

                  if ((x ^= (long)UnsafeUtil.getByte(tempPos++) << 49) < 0L) {
                     x ^= -558586000294016L;
                     break label51;
                  }

                  x ^= (long)UnsafeUtil.getByte(tempPos++) << 56;
                  x ^= 71499008037633920L;
                  if (x >= 0L || (long)UnsafeUtil.getByte(tempPos++) >= 0L) {
                     break label51;
                  }
               }
            }

            return this.readRawVarint64SlowPath();
         }

         this.currentByteBufferPos = tempPos;
         return x;
      }

      long readRawVarint64SlowPath() throws IOException {
         long result = 0L;

         for(int shift = 0; shift < 64; shift += 7) {
            byte b = this.readRawByte();
            result |= (long)(b & 127) << shift;
            if ((b & 128) == 0) {
               return result;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      public int readRawLittleEndian32() throws IOException {
         if (this.currentRemaining() >= 4L) {
            long tempPos = this.currentByteBufferPos;
            this.currentByteBufferPos += 4L;
            return UnsafeUtil.getByte(tempPos) & 255 | (UnsafeUtil.getByte(tempPos + 1L) & 255) << 8 | (UnsafeUtil.getByte(tempPos + 2L) & 255) << 16 | (UnsafeUtil.getByte(tempPos + 3L) & 255) << 24;
         } else {
            return this.readRawByte() & 255 | (this.readRawByte() & 255) << 8 | (this.readRawByte() & 255) << 16 | (this.readRawByte() & 255) << 24;
         }
      }

      public long readRawLittleEndian64() throws IOException {
         if (this.currentRemaining() >= 8L) {
            long tempPos = this.currentByteBufferPos;
            this.currentByteBufferPos += 8L;
            return (long)UnsafeUtil.getByte(tempPos) & 255L | ((long)UnsafeUtil.getByte(tempPos + 1L) & 255L) << 8 | ((long)UnsafeUtil.getByte(tempPos + 2L) & 255L) << 16 | ((long)UnsafeUtil.getByte(tempPos + 3L) & 255L) << 24 | ((long)UnsafeUtil.getByte(tempPos + 4L) & 255L) << 32 | ((long)UnsafeUtil.getByte(tempPos + 5L) & 255L) << 40 | ((long)UnsafeUtil.getByte(tempPos + 6L) & 255L) << 48 | ((long)UnsafeUtil.getByte(tempPos + 7L) & 255L) << 56;
         } else {
            return (long)this.readRawByte() & 255L | ((long)this.readRawByte() & 255L) << 8 | ((long)this.readRawByte() & 255L) << 16 | ((long)this.readRawByte() & 255L) << 24 | ((long)this.readRawByte() & 255L) << 32 | ((long)this.readRawByte() & 255L) << 40 | ((long)this.readRawByte() & 255L) << 48 | ((long)this.readRawByte() & 255L) << 56;
         }
      }

      public void enableAliasing(boolean enabled) {
         this.enableAliasing = enabled;
      }

      public void resetSizeCounter() {
         this.startOffset = (int)((long)this.totalBytesRead + this.currentByteBufferPos - this.currentByteBufferStartPos);
      }

      public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
         if (byteLimit < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            byteLimit += this.getTotalBytesRead();
            int oldLimit = this.currentLimit;
            if (byteLimit > oldLimit) {
               throw InvalidProtocolBufferException.truncatedMessage();
            } else {
               this.currentLimit = byteLimit;
               this.recomputeBufferSizeAfterLimit();
               return oldLimit;
            }
         }
      }

      private void recomputeBufferSizeAfterLimit() {
         this.totalBufferSize += this.bufferSizeAfterCurrentLimit;
         int bufferEnd = this.totalBufferSize - this.startOffset;
         if (bufferEnd > this.currentLimit) {
            this.bufferSizeAfterCurrentLimit = bufferEnd - this.currentLimit;
            this.totalBufferSize -= this.bufferSizeAfterCurrentLimit;
         } else {
            this.bufferSizeAfterCurrentLimit = 0;
         }

      }

      public void popLimit(int oldLimit) {
         this.currentLimit = oldLimit;
         this.recomputeBufferSizeAfterLimit();
      }

      public int getBytesUntilLimit() {
         return this.currentLimit == Integer.MAX_VALUE ? -1 : this.currentLimit - this.getTotalBytesRead();
      }

      public boolean isAtEnd() throws IOException {
         return (long)this.totalBytesRead + this.currentByteBufferPos - this.currentByteBufferStartPos == (long)this.totalBufferSize;
      }

      public int getTotalBytesRead() {
         return (int)((long)(this.totalBytesRead - this.startOffset) + this.currentByteBufferPos - this.currentByteBufferStartPos);
      }

      public byte readRawByte() throws IOException {
         if (this.currentRemaining() == 0L) {
            this.getNextByteBuffer();
         }

         return UnsafeUtil.getByte((long)(this.currentByteBufferPos++));
      }

      public byte[] readRawBytes(int length) throws IOException {
         byte[] bytes;
         if (length >= 0 && (long)length <= this.currentRemaining()) {
            bytes = new byte[length];
            UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, (long)length);
            this.currentByteBufferPos += (long)length;
            return bytes;
         } else if (length >= 0 && length <= this.remaining()) {
            bytes = new byte[length];
            this.readRawBytesTo(bytes, 0, length);
            return bytes;
         } else if (length <= 0) {
            if (length == 0) {
               return Internal.EMPTY_BYTE_ARRAY;
            } else {
               throw InvalidProtocolBufferException.negativeSize();
            }
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      private void readRawBytesTo(byte[] bytes, int offset, int length) throws IOException {
         if (length >= 0 && length <= this.remaining()) {
            int bytesToCopy;
            for(int l = length; l > 0; this.currentByteBufferPos += (long)bytesToCopy) {
               if (this.currentRemaining() == 0L) {
                  this.getNextByteBuffer();
               }

               bytesToCopy = Math.min(l, (int)this.currentRemaining());
               UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, (long)(length - l + offset), (long)bytesToCopy);
               l -= bytesToCopy;
            }

         } else if (length <= 0) {
            if (length != 0) {
               throw InvalidProtocolBufferException.negativeSize();
            }
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      public void skipRawBytes(int length) throws IOException {
         if (length >= 0 && (long)length <= (long)(this.totalBufferSize - this.totalBytesRead) - this.currentByteBufferPos + this.currentByteBufferStartPos) {
            int rl;
            for(int l = length; l > 0; this.currentByteBufferPos += (long)rl) {
               if (this.currentRemaining() == 0L) {
                  this.getNextByteBuffer();
               }

               rl = Math.min(l, (int)this.currentRemaining());
               l -= rl;
            }

         } else if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      private void skipRawVarint() throws IOException {
         for(int i = 0; i < 10; ++i) {
            if (this.readRawByte() >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private int remaining() {
         return (int)((long)(this.totalBufferSize - this.totalBytesRead) - this.currentByteBufferPos + this.currentByteBufferStartPos);
      }

      private long currentRemaining() {
         return this.currentByteBufferLimit - this.currentByteBufferPos;
      }

      private ByteBuffer slice(int begin, int end) throws IOException {
         int prevPos = this.currentByteBuffer.position();
         int prevLimit = this.currentByteBuffer.limit();

         ByteBuffer var5;
         try {
            this.currentByteBuffer.position(begin);
            this.currentByteBuffer.limit(end);
            var5 = this.currentByteBuffer.slice();
         } catch (IllegalArgumentException var9) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } finally {
            this.currentByteBuffer.position(prevPos);
            this.currentByteBuffer.limit(prevLimit);
         }

         return var5;
      }

      // $FF: synthetic method
      IterableDirectByteBufferDecoder(Iterable x0, int x1, boolean x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   private static final class StreamDecoder extends CodedInputStream {
      private final InputStream input;
      private final byte[] buffer;
      private int bufferSize;
      private int bufferSizeAfterLimit;
      private int pos;
      private int lastTag;
      private int totalBytesRetired;
      private int currentLimit;
      private RefillCallback refillCallback;

      private StreamDecoder(InputStream input, int bufferSize) {
         super(null);
         this.currentLimit = Integer.MAX_VALUE;
         this.refillCallback = null;
         Internal.checkNotNull(input, "input");
         this.input = input;
         this.buffer = new byte[bufferSize];
         this.bufferSize = 0;
         this.pos = 0;
         this.totalBytesRetired = 0;
      }

      public int readTag() throws IOException {
         if (this.isAtEnd()) {
            this.lastTag = 0;
            return 0;
         } else {
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
               throw InvalidProtocolBufferException.invalidTag();
            } else {
               return this.lastTag;
            }
         }
      }

      public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
         if (this.lastTag != value) {
            throw InvalidProtocolBufferException.invalidEndTag();
         }
      }

      public int getLastTag() {
         return this.lastTag;
      }

      public boolean skipField(int var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public boolean skipField(int var1, CodedOutputStream var2) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void skipMessage() throws IOException {
         int tag;
         do {
            tag = this.readTag();
         } while(tag != 0 && this.skipField(tag));

      }

      public void skipMessage(CodedOutputStream output) throws IOException {
         int tag;
         do {
            tag = this.readTag();
         } while(tag != 0 && this.skipField(tag, output));

      }

      public double readDouble() throws IOException {
         return Double.longBitsToDouble(this.readRawLittleEndian64());
      }

      public float readFloat() throws IOException {
         return Float.intBitsToFloat(this.readRawLittleEndian32());
      }

      public long readUInt64() throws IOException {
         return this.readRawVarint64();
      }

      public long readInt64() throws IOException {
         return this.readRawVarint64();
      }

      public int readInt32() throws IOException {
         return this.readRawVarint32();
      }

      public long readFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      public int readFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      public boolean readBool() throws IOException {
         return this.readRawVarint64() != 0L;
      }

      public String readString() throws IOException {
         int size = this.readRawVarint32();
         String result;
         if (size > 0 && size <= this.bufferSize - this.pos) {
            result = new String(this.buffer, this.pos, size, Internal.UTF_8);
            this.pos += size;
            return result;
         } else if (size == 0) {
            return "";
         } else if (size <= this.bufferSize) {
            this.refillBuffer(size);
            result = new String(this.buffer, this.pos, size, Internal.UTF_8);
            this.pos += size;
            return result;
         } else {
            return new String(this.readRawBytesSlowPath(size, false), Internal.UTF_8);
         }
      }

      public String readStringRequireUtf8() throws IOException {
         int size = this.readRawVarint32();
         int oldPos = this.pos;
         byte[] bytes;
         int tempPos;
         if (size <= this.bufferSize - oldPos && size > 0) {
            bytes = this.buffer;
            this.pos = oldPos + size;
            tempPos = oldPos;
         } else {
            if (size == 0) {
               return "";
            }

            if (size <= this.bufferSize) {
               this.refillBuffer(size);
               bytes = this.buffer;
               tempPos = 0;
               this.pos = tempPos + size;
            } else {
               bytes = this.readRawBytesSlowPath(size, false);
               tempPos = 0;
            }
         }

         return Utf8.decodeUtf8(bytes, tempPos, size);
      }

      public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            ++this.recursionDepth;
            builder.mergeFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.recursionDepth;
         }
      }

      public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            ++this.recursionDepth;
            T result = (MessageLite)parser.parsePartialFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.recursionDepth;
            return result;
         }
      }

      /** @deprecated */
      @Deprecated
      public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
         this.readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
      }

      public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int oldLimit = this.pushLimit(length);
            ++this.recursionDepth;
            builder.mergeFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.recursionDepth;
            this.popLimit(oldLimit);
         }
      }

      public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int oldLimit = this.pushLimit(length);
            ++this.recursionDepth;
            T result = (MessageLite)parser.parsePartialFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.recursionDepth;
            this.popLimit(oldLimit);
            return result;
         }
      }

      public ByteString readBytes() throws IOException {
         int size = this.readRawVarint32();
         if (size <= this.bufferSize - this.pos && size > 0) {
            ByteString result = ByteString.copyFrom(this.buffer, this.pos, size);
            this.pos += size;
            return result;
         } else {
            return size == 0 ? ByteString.EMPTY : this.readBytesSlowPath(size);
         }
      }

      public byte[] readByteArray() throws IOException {
         int size = this.readRawVarint32();
         if (size <= this.bufferSize - this.pos && size > 0) {
            byte[] result = Arrays.copyOfRange(this.buffer, this.pos, this.pos + size);
            this.pos += size;
            return result;
         } else {
            return this.readRawBytesSlowPath(size, false);
         }
      }

      public ByteBuffer readByteBuffer() throws IOException {
         int size = this.readRawVarint32();
         if (size <= this.bufferSize - this.pos && size > 0) {
            ByteBuffer result = ByteBuffer.wrap(Arrays.copyOfRange(this.buffer, this.pos, this.pos + size));
            this.pos += size;
            return result;
         } else {
            return size == 0 ? Internal.EMPTY_BYTE_BUFFER : ByteBuffer.wrap(this.readRawBytesSlowPath(size, true));
         }
      }

      public int readUInt32() throws IOException {
         return this.readRawVarint32();
      }

      public int readEnum() throws IOException {
         return this.readRawVarint32();
      }

      public int readSFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      public long readSFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      public int readSInt32() throws IOException {
         return decodeZigZag32(this.readRawVarint32());
      }

      public long readSInt64() throws IOException {
         return decodeZigZag64(this.readRawVarint64());
      }

      public int readRawVarint32() throws IOException {
         int tempPos;
         int x;
         label47: {
            tempPos = this.pos;
            if (this.bufferSize != tempPos) {
               byte[] buffer = this.buffer;
               if ((x = buffer[tempPos++]) >= 0) {
                  this.pos = tempPos;
                  return x;
               }

               if (this.bufferSize - tempPos >= 9) {
                  if ((x ^= buffer[tempPos++] << 7) < 0) {
                     x ^= -128;
                     break label47;
                  }

                  if ((x ^= buffer[tempPos++] << 14) >= 0) {
                     x ^= 16256;
                     break label47;
                  }

                  if ((x ^= buffer[tempPos++] << 21) < 0) {
                     x ^= -2080896;
                     break label47;
                  }

                  int y = buffer[tempPos++];
                  x ^= y << 28;
                  x ^= 266354560;
                  if (y >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0) {
                     break label47;
                  }
               }
            }

            return (int)this.readRawVarint64SlowPath();
         }

         this.pos = tempPos;
         return x;
      }

      private void skipRawVarint() throws IOException {
         if (this.bufferSize - this.pos >= 10) {
            this.skipRawVarintFastPath();
         } else {
            this.skipRawVarintSlowPath();
         }

      }

      private void skipRawVarintFastPath() throws IOException {
         for(int i = 0; i < 10; ++i) {
            if (this.buffer[this.pos++] >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private void skipRawVarintSlowPath() throws IOException {
         for(int i = 0; i < 10; ++i) {
            if (this.readRawByte() >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      public long readRawVarint64() throws IOException {
         int tempPos;
         long x;
         label51: {
            tempPos = this.pos;
            if (this.bufferSize != tempPos) {
               byte[] buffer = this.buffer;
               int y;
               if ((y = buffer[tempPos++]) >= 0) {
                  this.pos = tempPos;
                  return (long)y;
               }

               if (this.bufferSize - tempPos >= 9) {
                  if ((y ^= buffer[tempPos++] << 7) < 0) {
                     x = (long)(y ^ -128);
                     break label51;
                  }

                  if ((y ^= buffer[tempPos++] << 14) >= 0) {
                     x = (long)(y ^ 16256);
                     break label51;
                  }

                  if ((y ^= buffer[tempPos++] << 21) < 0) {
                     x = (long)(y ^ -2080896);
                     break label51;
                  }

                  if ((x = (long)y ^ (long)buffer[tempPos++] << 28) >= 0L) {
                     x ^= 266354560L;
                     break label51;
                  }

                  if ((x ^= (long)buffer[tempPos++] << 35) < 0L) {
                     x ^= -34093383808L;
                     break label51;
                  }

                  if ((x ^= (long)buffer[tempPos++] << 42) >= 0L) {
                     x ^= 4363953127296L;
                     break label51;
                  }

                  if ((x ^= (long)buffer[tempPos++] << 49) < 0L) {
                     x ^= -558586000294016L;
                     break label51;
                  }

                  x ^= (long)buffer[tempPos++] << 56;
                  x ^= 71499008037633920L;
                  if (x >= 0L || (long)buffer[tempPos++] >= 0L) {
                     break label51;
                  }
               }
            }

            return this.readRawVarint64SlowPath();
         }

         this.pos = tempPos;
         return x;
      }

      long readRawVarint64SlowPath() throws IOException {
         long result = 0L;

         for(int shift = 0; shift < 64; shift += 7) {
            byte b = this.readRawByte();
            result |= (long)(b & 127) << shift;
            if ((b & 128) == 0) {
               return result;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      public int readRawLittleEndian32() throws IOException {
         int tempPos = this.pos;
         if (this.bufferSize - tempPos < 4) {
            this.refillBuffer(4);
            tempPos = this.pos;
         }

         byte[] buffer = this.buffer;
         this.pos = tempPos + 4;
         return buffer[tempPos] & 255 | (buffer[tempPos + 1] & 255) << 8 | (buffer[tempPos + 2] & 255) << 16 | (buffer[tempPos + 3] & 255) << 24;
      }

      public long readRawLittleEndian64() throws IOException {
         int tempPos = this.pos;
         if (this.bufferSize - tempPos < 8) {
            this.refillBuffer(8);
            tempPos = this.pos;
         }

         byte[] buffer = this.buffer;
         this.pos = tempPos + 8;
         return (long)buffer[tempPos] & 255L | ((long)buffer[tempPos + 1] & 255L) << 8 | ((long)buffer[tempPos + 2] & 255L) << 16 | ((long)buffer[tempPos + 3] & 255L) << 24 | ((long)buffer[tempPos + 4] & 255L) << 32 | ((long)buffer[tempPos + 5] & 255L) << 40 | ((long)buffer[tempPos + 6] & 255L) << 48 | ((long)buffer[tempPos + 7] & 255L) << 56;
      }

      public void enableAliasing(boolean enabled) {
      }

      public void resetSizeCounter() {
         this.totalBytesRetired = -this.pos;
      }

      public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
         if (byteLimit < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            byteLimit += this.totalBytesRetired + this.pos;
            int oldLimit = this.currentLimit;
            if (byteLimit > oldLimit) {
               throw InvalidProtocolBufferException.truncatedMessage();
            } else {
               this.currentLimit = byteLimit;
               this.recomputeBufferSizeAfterLimit();
               return oldLimit;
            }
         }
      }

      private void recomputeBufferSizeAfterLimit() {
         this.bufferSize += this.bufferSizeAfterLimit;
         int bufferEnd = this.totalBytesRetired + this.bufferSize;
         if (bufferEnd > this.currentLimit) {
            this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
            this.bufferSize -= this.bufferSizeAfterLimit;
         } else {
            this.bufferSizeAfterLimit = 0;
         }

      }

      public void popLimit(int oldLimit) {
         this.currentLimit = oldLimit;
         this.recomputeBufferSizeAfterLimit();
      }

      public int getBytesUntilLimit() {
         if (this.currentLimit == Integer.MAX_VALUE) {
            return -1;
         } else {
            int currentAbsolutePosition = this.totalBytesRetired + this.pos;
            return this.currentLimit - currentAbsolutePosition;
         }
      }

      public boolean isAtEnd() throws IOException {
         return this.pos == this.bufferSize && !this.tryRefillBuffer(1);
      }

      public int getTotalBytesRead() {
         return this.totalBytesRetired + this.pos;
      }

      private void refillBuffer(int n) throws IOException {
         if (!this.tryRefillBuffer(n)) {
            if (n > this.sizeLimit - this.totalBytesRetired - this.pos) {
               throw InvalidProtocolBufferException.sizeLimitExceeded();
            } else {
               throw InvalidProtocolBufferException.truncatedMessage();
            }
         }
      }

      private boolean tryRefillBuffer(int n) throws IOException {
         if (this.pos + n <= this.bufferSize) {
            throw new IllegalStateException("refillBuffer() called when " + n + " bytes were already available in buffer");
         } else if (n > this.sizeLimit - this.totalBytesRetired - this.pos) {
            return false;
         } else if (this.totalBytesRetired + this.pos + n > this.currentLimit) {
            return false;
         } else {
            if (this.refillCallback != null) {
               this.refillCallback.onRefill();
            }

            int tempPos = this.pos;
            if (tempPos > 0) {
               if (this.bufferSize > tempPos) {
                  System.arraycopy(this.buffer, tempPos, this.buffer, 0, this.bufferSize - tempPos);
               }

               this.totalBytesRetired += tempPos;
               this.bufferSize -= tempPos;
               this.pos = 0;
            }

            int bytesRead = this.input.read(this.buffer, this.bufferSize, Math.min(this.buffer.length - this.bufferSize, this.sizeLimit - this.totalBytesRetired - this.bufferSize));
            if (bytesRead != 0 && bytesRead >= -1 && bytesRead <= this.buffer.length) {
               if (bytesRead > 0) {
                  this.bufferSize += bytesRead;
                  this.recomputeBufferSizeAfterLimit();
                  return this.bufferSize >= n ? true : this.tryRefillBuffer(n);
               } else {
                  return false;
               }
            } else {
               throw new IllegalStateException(this.input.getClass() + "#read(byte[]) returned invalid result: " + bytesRead + "\nThe InputStream implementation is buggy.");
            }
         }
      }

      public byte readRawByte() throws IOException {
         if (this.pos == this.bufferSize) {
            this.refillBuffer(1);
         }

         return this.buffer[this.pos++];
      }

      public byte[] readRawBytes(int size) throws IOException {
         int tempPos = this.pos;
         if (size <= this.bufferSize - tempPos && size > 0) {
            this.pos = tempPos + size;
            return Arrays.copyOfRange(this.buffer, tempPos, tempPos + size);
         } else {
            return this.readRawBytesSlowPath(size, false);
         }
      }

      private byte[] readRawBytesSlowPath(int size, boolean ensureNoLeakedReferences) throws IOException {
         byte[] result = this.readRawBytesSlowPathOneChunk(size);
         if (result != null) {
            return ensureNoLeakedReferences ? (byte[])result.clone() : result;
         } else {
            int originalBufferPos = this.pos;
            int bufferedBytes = this.bufferSize - this.pos;
            this.totalBytesRetired += this.bufferSize;
            this.pos = 0;
            this.bufferSize = 0;
            int sizeLeft = size - bufferedBytes;
            List<byte[]> chunks = this.readRawBytesSlowPathRemainingChunks(sizeLeft);
            byte[] bytes = new byte[size];
            System.arraycopy(this.buffer, originalBufferPos, bytes, 0, bufferedBytes);
            int tempPos = bufferedBytes;

            byte[] chunk;
            for(Iterator var10 = chunks.iterator(); var10.hasNext(); tempPos += chunk.length) {
               chunk = (byte[])var10.next();
               System.arraycopy(chunk, 0, bytes, tempPos, chunk.length);
            }

            return bytes;
         }
      }

      private byte[] readRawBytesSlowPathOneChunk(int size) throws IOException {
         if (size == 0) {
            return Internal.EMPTY_BYTE_ARRAY;
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            int currentMessageSize = this.totalBytesRetired + this.pos + size;
            if (currentMessageSize - this.sizeLimit > 0) {
               throw InvalidProtocolBufferException.sizeLimitExceeded();
            } else if (currentMessageSize > this.currentLimit) {
               this.skipRawBytes(this.currentLimit - this.totalBytesRetired - this.pos);
               throw InvalidProtocolBufferException.truncatedMessage();
            } else {
               int bufferedBytes = this.bufferSize - this.pos;
               int sizeLeft = size - bufferedBytes;
               if (sizeLeft >= 4096 && sizeLeft > this.input.available()) {
                  return null;
               } else {
                  byte[] bytes = new byte[size];
                  System.arraycopy(this.buffer, this.pos, bytes, 0, bufferedBytes);
                  this.totalBytesRetired += this.bufferSize;
                  this.pos = 0;
                  this.bufferSize = 0;

                  int n;
                  for(int tempPos = bufferedBytes; tempPos < bytes.length; tempPos += n) {
                     n = this.input.read(bytes, tempPos, size - tempPos);
                     if (n == -1) {
                        throw InvalidProtocolBufferException.truncatedMessage();
                     }

                     this.totalBytesRetired += n;
                  }

                  return bytes;
               }
            }
         }
      }

      private List<byte[]> readRawBytesSlowPathRemainingChunks(int sizeLeft) throws IOException {
         List<byte[]> chunks = new ArrayList();

         while(sizeLeft > 0) {
            byte[] chunk = new byte[Math.min(sizeLeft, 4096)];

            int n;
            for(int tempPos = 0; tempPos < chunk.length; tempPos += n) {
               n = this.input.read(chunk, tempPos, chunk.length - tempPos);
               if (n == -1) {
                  throw InvalidProtocolBufferException.truncatedMessage();
               }

               this.totalBytesRetired += n;
            }

            sizeLeft -= chunk.length;
            chunks.add(chunk);
         }

         return chunks;
      }

      private ByteString readBytesSlowPath(int size) throws IOException {
         byte[] result = this.readRawBytesSlowPathOneChunk(size);
         if (result != null) {
            return ByteString.copyFrom(result);
         } else {
            int originalBufferPos = this.pos;
            int bufferedBytes = this.bufferSize - this.pos;
            this.totalBytesRetired += this.bufferSize;
            this.pos = 0;
            this.bufferSize = 0;
            int sizeLeft = size - bufferedBytes;
            List<byte[]> chunks = this.readRawBytesSlowPathRemainingChunks(sizeLeft);
            byte[] bytes = new byte[size];
            System.arraycopy(this.buffer, originalBufferPos, bytes, 0, bufferedBytes);
            int tempPos = bufferedBytes;

            byte[] chunk;
            for(Iterator var9 = chunks.iterator(); var9.hasNext(); tempPos += chunk.length) {
               chunk = (byte[])var9.next();
               System.arraycopy(chunk, 0, bytes, tempPos, chunk.length);
            }

            return ByteString.wrap(bytes);
         }
      }

      public void skipRawBytes(int size) throws IOException {
         if (size <= this.bufferSize - this.pos && size >= 0) {
            this.pos += size;
         } else {
            this.skipRawBytesSlowPath(size);
         }

      }

      private void skipRawBytesSlowPath(int size) throws IOException {
         if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else if (this.totalBytesRetired + this.pos + size > this.currentLimit) {
            this.skipRawBytes(this.currentLimit - this.totalBytesRetired - this.pos);
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            int totalSkipped = 0;
            int tempPos;
            if (this.refillCallback == null) {
               this.totalBytesRetired += this.pos;
               totalSkipped = this.bufferSize - this.pos;
               this.bufferSize = 0;
               this.pos = 0;

               try {
                  while(totalSkipped < size) {
                     tempPos = size - totalSkipped;
                     long skipped = this.input.skip((long)tempPos);
                     if (skipped < 0L || skipped > (long)tempPos) {
                        throw new IllegalStateException(this.input.getClass() + "#skip returned invalid result: " + skipped + "\nThe InputStream implementation is buggy.");
                     }

                     if (skipped == 0L) {
                        break;
                     }

                     totalSkipped += (int)skipped;
                  }
               } finally {
                  this.totalBytesRetired += totalSkipped;
                  this.recomputeBufferSizeAfterLimit();
               }
            }

            if (totalSkipped < size) {
               tempPos = this.bufferSize - this.pos;
               this.pos = this.bufferSize;
               this.refillBuffer(1);

               while(size - tempPos > this.bufferSize) {
                  tempPos += this.bufferSize;
                  this.pos = this.bufferSize;
                  this.refillBuffer(1);
               }

               this.pos = size - tempPos;
            }

         }
      }

      // $FF: synthetic method
      StreamDecoder(InputStream x0, int x1, Object x2) {
         this(x0, x1);
      }

      private interface RefillCallback {
         void onRefill();
      }

      private class SkippedDataSink implements RefillCallback {
         private int lastPos;
         private ByteArrayOutputStream byteArrayStream;

         private SkippedDataSink() {
            this.lastPos = StreamDecoder.this.pos;
         }

         public void onRefill() {
            if (this.byteArrayStream == null) {
               this.byteArrayStream = new ByteArrayOutputStream();
            }

            this.byteArrayStream.write(StreamDecoder.this.buffer, this.lastPos, StreamDecoder.this.pos - this.lastPos);
            this.lastPos = 0;
         }

         ByteBuffer getSkippedData() {
            if (this.byteArrayStream == null) {
               return ByteBuffer.wrap(StreamDecoder.this.buffer, this.lastPos, StreamDecoder.this.pos - this.lastPos);
            } else {
               this.byteArrayStream.write(StreamDecoder.this.buffer, this.lastPos, StreamDecoder.this.pos);
               return ByteBuffer.wrap(this.byteArrayStream.toByteArray());
            }
         }
      }
   }

   private static final class UnsafeDirectNioDecoder extends CodedInputStream {
      private final ByteBuffer buffer;
      private final boolean immutable;
      private final long address;
      private long limit;
      private long pos;
      private long startPos;
      private int bufferSizeAfterLimit;
      private int lastTag;
      private boolean enableAliasing;
      private int currentLimit;

      static boolean isSupported() {
         return UnsafeUtil.hasUnsafeByteBufferOperations();
      }

      private UnsafeDirectNioDecoder(ByteBuffer buffer, boolean immutable) {
         super(null);
         this.currentLimit = Integer.MAX_VALUE;
         this.buffer = buffer;
         this.address = UnsafeUtil.addressOffset(buffer);
         this.limit = this.address + (long)buffer.limit();
         this.pos = this.address + (long)buffer.position();
         this.startPos = this.pos;
         this.immutable = immutable;
      }

      public int readTag() throws IOException {
         if (this.isAtEnd()) {
            this.lastTag = 0;
            return 0;
         } else {
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
               throw InvalidProtocolBufferException.invalidTag();
            } else {
               return this.lastTag;
            }
         }
      }

      public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
         if (this.lastTag != value) {
            throw InvalidProtocolBufferException.invalidEndTag();
         }
      }

      public int getLastTag() {
         return this.lastTag;
      }

      public boolean skipField(int var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public boolean skipField(int var1, CodedOutputStream var2) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void skipMessage() throws IOException {
         int tag;
         do {
            tag = this.readTag();
         } while(tag != 0 && this.skipField(tag));

      }

      public void skipMessage(CodedOutputStream output) throws IOException {
         int tag;
         do {
            tag = this.readTag();
         } while(tag != 0 && this.skipField(tag, output));

      }

      public double readDouble() throws IOException {
         return Double.longBitsToDouble(this.readRawLittleEndian64());
      }

      public float readFloat() throws IOException {
         return Float.intBitsToFloat(this.readRawLittleEndian32());
      }

      public long readUInt64() throws IOException {
         return this.readRawVarint64();
      }

      public long readInt64() throws IOException {
         return this.readRawVarint64();
      }

      public int readInt32() throws IOException {
         return this.readRawVarint32();
      }

      public long readFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      public int readFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      public boolean readBool() throws IOException {
         return this.readRawVarint64() != 0L;
      }

      public String readString() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.remaining()) {
            byte[] bytes = new byte[size];
            UnsafeUtil.copyMemory(this.pos, bytes, 0L, (long)size);
            String result = new String(bytes, Internal.UTF_8);
            this.pos += (long)size;
            return result;
         } else if (size == 0) {
            return "";
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      public String readStringRequireUtf8() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.remaining()) {
            int bufferPos = this.bufferPos(this.pos);
            String result = Utf8.decodeUtf8(this.buffer, bufferPos, size);
            this.pos += (long)size;
            return result;
         } else if (size == 0) {
            return "";
         } else if (size <= 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            ++this.recursionDepth;
            builder.mergeFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.recursionDepth;
         }
      }

      public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            ++this.recursionDepth;
            T result = (MessageLite)parser.parsePartialFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.recursionDepth;
            return result;
         }
      }

      /** @deprecated */
      @Deprecated
      public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
         this.readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
      }

      public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int oldLimit = this.pushLimit(length);
            ++this.recursionDepth;
            builder.mergeFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.recursionDepth;
            this.popLimit(oldLimit);
         }
      }

      public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int oldLimit = this.pushLimit(length);
            ++this.recursionDepth;
            T result = (MessageLite)parser.parsePartialFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.recursionDepth;
            this.popLimit(oldLimit);
            return result;
         }
      }

      public ByteString readBytes() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.remaining()) {
            if (this.immutable && this.enableAliasing) {
               ByteBuffer result = this.slice(this.pos, this.pos + (long)size);
               this.pos += (long)size;
               return ByteString.wrap(result);
            } else {
               byte[] bytes = new byte[size];
               UnsafeUtil.copyMemory(this.pos, bytes, 0L, (long)size);
               this.pos += (long)size;
               return ByteString.wrap(bytes);
            }
         } else if (size == 0) {
            return ByteString.EMPTY;
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      public byte[] readByteArray() throws IOException {
         return this.readRawBytes(this.readRawVarint32());
      }

      public ByteBuffer readByteBuffer() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.remaining()) {
            if (!this.immutable && this.enableAliasing) {
               ByteBuffer result = this.slice(this.pos, this.pos + (long)size);
               this.pos += (long)size;
               return result;
            } else {
               byte[] bytes = new byte[size];
               UnsafeUtil.copyMemory(this.pos, bytes, 0L, (long)size);
               this.pos += (long)size;
               return ByteBuffer.wrap(bytes);
            }
         } else if (size == 0) {
            return Internal.EMPTY_BYTE_BUFFER;
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      public int readUInt32() throws IOException {
         return this.readRawVarint32();
      }

      public int readEnum() throws IOException {
         return this.readRawVarint32();
      }

      public int readSFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      public long readSFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      public int readSInt32() throws IOException {
         return decodeZigZag32(this.readRawVarint32());
      }

      public long readSInt64() throws IOException {
         return decodeZigZag64(this.readRawVarint64());
      }

      public int readRawVarint32() throws IOException {
         long tempPos;
         int x;
         label47: {
            tempPos = this.pos;
            if (this.limit != tempPos) {
               if ((x = UnsafeUtil.getByte(tempPos++)) >= 0) {
                  this.pos = tempPos;
                  return x;
               }

               if (this.limit - tempPos >= 9L) {
                  if ((x ^= UnsafeUtil.getByte(tempPos++) << 7) < 0) {
                     x ^= -128;
                     break label47;
                  }

                  if ((x ^= UnsafeUtil.getByte(tempPos++) << 14) >= 0) {
                     x ^= 16256;
                     break label47;
                  }

                  if ((x ^= UnsafeUtil.getByte(tempPos++) << 21) < 0) {
                     x ^= -2080896;
                     break label47;
                  }

                  int y = UnsafeUtil.getByte(tempPos++);
                  x ^= y << 28;
                  x ^= 266354560;
                  if (y >= 0 || UnsafeUtil.getByte(tempPos++) >= 0 || UnsafeUtil.getByte(tempPos++) >= 0 || UnsafeUtil.getByte(tempPos++) >= 0 || UnsafeUtil.getByte(tempPos++) >= 0 || UnsafeUtil.getByte(tempPos++) >= 0) {
                     break label47;
                  }
               }
            }

            return (int)this.readRawVarint64SlowPath();
         }

         this.pos = tempPos;
         return x;
      }

      private void skipRawVarint() throws IOException {
         if (this.remaining() >= 10) {
            this.skipRawVarintFastPath();
         } else {
            this.skipRawVarintSlowPath();
         }

      }

      private void skipRawVarintFastPath() throws IOException {
         for(int i = 0; i < 10; ++i) {
            if (UnsafeUtil.getByte((long)(this.pos++)) >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private void skipRawVarintSlowPath() throws IOException {
         for(int i = 0; i < 10; ++i) {
            if (this.readRawByte() >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      public long readRawVarint64() throws IOException {
         long tempPos;
         long x;
         label51: {
            tempPos = this.pos;
            if (this.limit != tempPos) {
               int y;
               if ((y = UnsafeUtil.getByte(tempPos++)) >= 0) {
                  this.pos = tempPos;
                  return (long)y;
               }

               if (this.limit - tempPos >= 9L) {
                  if ((y ^= UnsafeUtil.getByte(tempPos++) << 7) < 0) {
                     x = (long)(y ^ -128);
                     break label51;
                  }

                  if ((y ^= UnsafeUtil.getByte(tempPos++) << 14) >= 0) {
                     x = (long)(y ^ 16256);
                     break label51;
                  }

                  if ((y ^= UnsafeUtil.getByte(tempPos++) << 21) < 0) {
                     x = (long)(y ^ -2080896);
                     break label51;
                  }

                  if ((x = (long)y ^ (long)UnsafeUtil.getByte(tempPos++) << 28) >= 0L) {
                     x ^= 266354560L;
                     break label51;
                  }

                  if ((x ^= (long)UnsafeUtil.getByte(tempPos++) << 35) < 0L) {
                     x ^= -34093383808L;
                     break label51;
                  }

                  if ((x ^= (long)UnsafeUtil.getByte(tempPos++) << 42) >= 0L) {
                     x ^= 4363953127296L;
                     break label51;
                  }

                  if ((x ^= (long)UnsafeUtil.getByte(tempPos++) << 49) < 0L) {
                     x ^= -558586000294016L;
                     break label51;
                  }

                  x ^= (long)UnsafeUtil.getByte(tempPos++) << 56;
                  x ^= 71499008037633920L;
                  if (x >= 0L || (long)UnsafeUtil.getByte(tempPos++) >= 0L) {
                     break label51;
                  }
               }
            }

            return this.readRawVarint64SlowPath();
         }

         this.pos = tempPos;
         return x;
      }

      long readRawVarint64SlowPath() throws IOException {
         long result = 0L;

         for(int shift = 0; shift < 64; shift += 7) {
            byte b = this.readRawByte();
            result |= (long)(b & 127) << shift;
            if ((b & 128) == 0) {
               return result;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      public int readRawLittleEndian32() throws IOException {
         long tempPos = this.pos;
         if (this.limit - tempPos < 4L) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.pos = tempPos + 4L;
            return UnsafeUtil.getByte(tempPos) & 255 | (UnsafeUtil.getByte(tempPos + 1L) & 255) << 8 | (UnsafeUtil.getByte(tempPos + 2L) & 255) << 16 | (UnsafeUtil.getByte(tempPos + 3L) & 255) << 24;
         }
      }

      public long readRawLittleEndian64() throws IOException {
         long tempPos = this.pos;
         if (this.limit - tempPos < 8L) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.pos = tempPos + 8L;
            return (long)UnsafeUtil.getByte(tempPos) & 255L | ((long)UnsafeUtil.getByte(tempPos + 1L) & 255L) << 8 | ((long)UnsafeUtil.getByte(tempPos + 2L) & 255L) << 16 | ((long)UnsafeUtil.getByte(tempPos + 3L) & 255L) << 24 | ((long)UnsafeUtil.getByte(tempPos + 4L) & 255L) << 32 | ((long)UnsafeUtil.getByte(tempPos + 5L) & 255L) << 40 | ((long)UnsafeUtil.getByte(tempPos + 6L) & 255L) << 48 | ((long)UnsafeUtil.getByte(tempPos + 7L) & 255L) << 56;
         }
      }

      public void enableAliasing(boolean enabled) {
         this.enableAliasing = enabled;
      }

      public void resetSizeCounter() {
         this.startPos = this.pos;
      }

      public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
         if (byteLimit < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            byteLimit += this.getTotalBytesRead();
            int oldLimit = this.currentLimit;
            if (byteLimit > oldLimit) {
               throw InvalidProtocolBufferException.truncatedMessage();
            } else {
               this.currentLimit = byteLimit;
               this.recomputeBufferSizeAfterLimit();
               return oldLimit;
            }
         }
      }

      public void popLimit(int oldLimit) {
         this.currentLimit = oldLimit;
         this.recomputeBufferSizeAfterLimit();
      }

      public int getBytesUntilLimit() {
         return this.currentLimit == Integer.MAX_VALUE ? -1 : this.currentLimit - this.getTotalBytesRead();
      }

      public boolean isAtEnd() throws IOException {
         return this.pos == this.limit;
      }

      public int getTotalBytesRead() {
         return (int)(this.pos - this.startPos);
      }

      public byte readRawByte() throws IOException {
         if (this.pos == this.limit) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            return UnsafeUtil.getByte((long)(this.pos++));
         }
      }

      public byte[] readRawBytes(int length) throws IOException {
         if (length >= 0 && length <= this.remaining()) {
            byte[] bytes = new byte[length];
            this.slice(this.pos, this.pos + (long)length).get(bytes);
            this.pos += (long)length;
            return bytes;
         } else if (length <= 0) {
            if (length == 0) {
               return Internal.EMPTY_BYTE_ARRAY;
            } else {
               throw InvalidProtocolBufferException.negativeSize();
            }
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      public void skipRawBytes(int length) throws IOException {
         if (length >= 0 && length <= this.remaining()) {
            this.pos += (long)length;
         } else if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      private void recomputeBufferSizeAfterLimit() {
         this.limit += (long)this.bufferSizeAfterLimit;
         int bufferEnd = (int)(this.limit - this.startPos);
         if (bufferEnd > this.currentLimit) {
            this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
            this.limit -= (long)this.bufferSizeAfterLimit;
         } else {
            this.bufferSizeAfterLimit = 0;
         }

      }

      private int remaining() {
         return (int)(this.limit - this.pos);
      }

      private int bufferPos(long pos) {
         return (int)(pos - this.address);
      }

      private ByteBuffer slice(long begin, long end) throws IOException {
         int prevPos = this.buffer.position();
         int prevLimit = this.buffer.limit();

         ByteBuffer var7;
         try {
            this.buffer.position(this.bufferPos(begin));
            this.buffer.limit(this.bufferPos(end));
            var7 = this.buffer.slice();
         } catch (IllegalArgumentException var11) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } finally {
            this.buffer.position(prevPos);
            this.buffer.limit(prevLimit);
         }

         return var7;
      }

      // $FF: synthetic method
      UnsafeDirectNioDecoder(ByteBuffer x0, boolean x1, Object x2) {
         this(x0, x1);
      }
   }

   private static final class ArrayDecoder extends CodedInputStream {
      private final byte[] buffer;
      private final boolean immutable;
      private int limit;
      private int bufferSizeAfterLimit;
      private int pos;
      private int startPos;
      private int lastTag;
      private boolean enableAliasing;
      private int currentLimit;

      private ArrayDecoder(byte[] buffer, int offset, int len, boolean immutable) {
         super(null);
         this.currentLimit = Integer.MAX_VALUE;
         this.buffer = buffer;
         this.limit = offset + len;
         this.pos = offset;
         this.startPos = this.pos;
         this.immutable = immutable;
      }

      public int readTag() throws IOException {
         if (this.isAtEnd()) {
            this.lastTag = 0;
            return 0;
         } else {
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
               throw InvalidProtocolBufferException.invalidTag();
            } else {
               return this.lastTag;
            }
         }
      }

      public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
         if (this.lastTag != value) {
            throw InvalidProtocolBufferException.invalidEndTag();
         }
      }

      public int getLastTag() {
         return this.lastTag;
      }

      public boolean skipField(int var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public boolean skipField(int var1, CodedOutputStream var2) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void skipMessage() throws IOException {
         int tag;
         do {
            tag = this.readTag();
         } while(tag != 0 && this.skipField(tag));

      }

      public void skipMessage(CodedOutputStream output) throws IOException {
         int tag;
         do {
            tag = this.readTag();
         } while(tag != 0 && this.skipField(tag, output));

      }

      public double readDouble() throws IOException {
         return Double.longBitsToDouble(this.readRawLittleEndian64());
      }

      public float readFloat() throws IOException {
         return Float.intBitsToFloat(this.readRawLittleEndian32());
      }

      public long readUInt64() throws IOException {
         return this.readRawVarint64();
      }

      public long readInt64() throws IOException {
         return this.readRawVarint64();
      }

      public int readInt32() throws IOException {
         return this.readRawVarint32();
      }

      public long readFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      public int readFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      public boolean readBool() throws IOException {
         return this.readRawVarint64() != 0L;
      }

      public String readString() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.limit - this.pos) {
            String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
            this.pos += size;
            return result;
         } else if (size == 0) {
            return "";
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      public String readStringRequireUtf8() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.limit - this.pos) {
            String result = Utf8.decodeUtf8(this.buffer, this.pos, size);
            this.pos += size;
            return result;
         } else if (size == 0) {
            return "";
         } else if (size <= 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            ++this.recursionDepth;
            builder.mergeFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.recursionDepth;
         }
      }

      public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            ++this.recursionDepth;
            T result = (MessageLite)parser.parsePartialFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.recursionDepth;
            return result;
         }
      }

      /** @deprecated */
      @Deprecated
      public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
         this.readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
      }

      public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int oldLimit = this.pushLimit(length);
            ++this.recursionDepth;
            builder.mergeFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.recursionDepth;
            this.popLimit(oldLimit);
         }
      }

      public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int oldLimit = this.pushLimit(length);
            ++this.recursionDepth;
            T result = (MessageLite)parser.parsePartialFrom((CodedInputStream)this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.recursionDepth;
            this.popLimit(oldLimit);
            return result;
         }
      }

      public ByteString readBytes() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.limit - this.pos) {
            ByteString result = this.immutable && this.enableAliasing ? ByteString.wrap(this.buffer, this.pos, size) : ByteString.copyFrom(this.buffer, this.pos, size);
            this.pos += size;
            return result;
         } else {
            return size == 0 ? ByteString.EMPTY : ByteString.wrap(this.readRawBytes(size));
         }
      }

      public byte[] readByteArray() throws IOException {
         int size = this.readRawVarint32();
         return this.readRawBytes(size);
      }

      public ByteBuffer readByteBuffer() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.limit - this.pos) {
            ByteBuffer result = !this.immutable && this.enableAliasing ? ByteBuffer.wrap(this.buffer, this.pos, size).slice() : ByteBuffer.wrap(Arrays.copyOfRange(this.buffer, this.pos, this.pos + size));
            this.pos += size;
            return result;
         } else if (size == 0) {
            return Internal.EMPTY_BYTE_BUFFER;
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      public int readUInt32() throws IOException {
         return this.readRawVarint32();
      }

      public int readEnum() throws IOException {
         return this.readRawVarint32();
      }

      public int readSFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      public long readSFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      public int readSInt32() throws IOException {
         return decodeZigZag32(this.readRawVarint32());
      }

      public long readSInt64() throws IOException {
         return decodeZigZag64(this.readRawVarint64());
      }

      public int readRawVarint32() throws IOException {
         int tempPos;
         int x;
         label47: {
            tempPos = this.pos;
            if (this.limit != tempPos) {
               byte[] buffer = this.buffer;
               if ((x = buffer[tempPos++]) >= 0) {
                  this.pos = tempPos;
                  return x;
               }

               if (this.limit - tempPos >= 9) {
                  if ((x ^= buffer[tempPos++] << 7) < 0) {
                     x ^= -128;
                     break label47;
                  }

                  if ((x ^= buffer[tempPos++] << 14) >= 0) {
                     x ^= 16256;
                     break label47;
                  }

                  if ((x ^= buffer[tempPos++] << 21) < 0) {
                     x ^= -2080896;
                     break label47;
                  }

                  int y = buffer[tempPos++];
                  x ^= y << 28;
                  x ^= 266354560;
                  if (y >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0) {
                     break label47;
                  }
               }
            }

            return (int)this.readRawVarint64SlowPath();
         }

         this.pos = tempPos;
         return x;
      }

      private void skipRawVarint() throws IOException {
         if (this.limit - this.pos >= 10) {
            this.skipRawVarintFastPath();
         } else {
            this.skipRawVarintSlowPath();
         }

      }

      private void skipRawVarintFastPath() throws IOException {
         for(int i = 0; i < 10; ++i) {
            if (this.buffer[this.pos++] >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private void skipRawVarintSlowPath() throws IOException {
         for(int i = 0; i < 10; ++i) {
            if (this.readRawByte() >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      public long readRawVarint64() throws IOException {
         int tempPos;
         long x;
         label51: {
            tempPos = this.pos;
            if (this.limit != tempPos) {
               byte[] buffer = this.buffer;
               int y;
               if ((y = buffer[tempPos++]) >= 0) {
                  this.pos = tempPos;
                  return (long)y;
               }

               if (this.limit - tempPos >= 9) {
                  if ((y ^= buffer[tempPos++] << 7) < 0) {
                     x = (long)(y ^ -128);
                     break label51;
                  }

                  if ((y ^= buffer[tempPos++] << 14) >= 0) {
                     x = (long)(y ^ 16256);
                     break label51;
                  }

                  if ((y ^= buffer[tempPos++] << 21) < 0) {
                     x = (long)(y ^ -2080896);
                     break label51;
                  }

                  if ((x = (long)y ^ (long)buffer[tempPos++] << 28) >= 0L) {
                     x ^= 266354560L;
                     break label51;
                  }

                  if ((x ^= (long)buffer[tempPos++] << 35) < 0L) {
                     x ^= -34093383808L;
                     break label51;
                  }

                  if ((x ^= (long)buffer[tempPos++] << 42) >= 0L) {
                     x ^= 4363953127296L;
                     break label51;
                  }

                  if ((x ^= (long)buffer[tempPos++] << 49) < 0L) {
                     x ^= -558586000294016L;
                     break label51;
                  }

                  x ^= (long)buffer[tempPos++] << 56;
                  x ^= 71499008037633920L;
                  if (x >= 0L || (long)buffer[tempPos++] >= 0L) {
                     break label51;
                  }
               }
            }

            return this.readRawVarint64SlowPath();
         }

         this.pos = tempPos;
         return x;
      }

      long readRawVarint64SlowPath() throws IOException {
         long result = 0L;

         for(int shift = 0; shift < 64; shift += 7) {
            byte b = this.readRawByte();
            result |= (long)(b & 127) << shift;
            if ((b & 128) == 0) {
               return result;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      public int readRawLittleEndian32() throws IOException {
         int tempPos = this.pos;
         if (this.limit - tempPos < 4) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            byte[] buffer = this.buffer;
            this.pos = tempPos + 4;
            return buffer[tempPos] & 255 | (buffer[tempPos + 1] & 255) << 8 | (buffer[tempPos + 2] & 255) << 16 | (buffer[tempPos + 3] & 255) << 24;
         }
      }

      public long readRawLittleEndian64() throws IOException {
         int tempPos = this.pos;
         if (this.limit - tempPos < 8) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            byte[] buffer = this.buffer;
            this.pos = tempPos + 8;
            return (long)buffer[tempPos] & 255L | ((long)buffer[tempPos + 1] & 255L) << 8 | ((long)buffer[tempPos + 2] & 255L) << 16 | ((long)buffer[tempPos + 3] & 255L) << 24 | ((long)buffer[tempPos + 4] & 255L) << 32 | ((long)buffer[tempPos + 5] & 255L) << 40 | ((long)buffer[tempPos + 6] & 255L) << 48 | ((long)buffer[tempPos + 7] & 255L) << 56;
         }
      }

      public void enableAliasing(boolean enabled) {
         this.enableAliasing = enabled;
      }

      public void resetSizeCounter() {
         this.startPos = this.pos;
      }

      public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
         if (byteLimit < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            byteLimit += this.getTotalBytesRead();
            int oldLimit = this.currentLimit;
            if (byteLimit > oldLimit) {
               throw InvalidProtocolBufferException.truncatedMessage();
            } else {
               this.currentLimit = byteLimit;
               this.recomputeBufferSizeAfterLimit();
               return oldLimit;
            }
         }
      }

      private void recomputeBufferSizeAfterLimit() {
         this.limit += this.bufferSizeAfterLimit;
         int bufferEnd = this.limit - this.startPos;
         if (bufferEnd > this.currentLimit) {
            this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
            this.limit -= this.bufferSizeAfterLimit;
         } else {
            this.bufferSizeAfterLimit = 0;
         }

      }

      public void popLimit(int oldLimit) {
         this.currentLimit = oldLimit;
         this.recomputeBufferSizeAfterLimit();
      }

      public int getBytesUntilLimit() {
         return this.currentLimit == Integer.MAX_VALUE ? -1 : this.currentLimit - this.getTotalBytesRead();
      }

      public boolean isAtEnd() throws IOException {
         return this.pos == this.limit;
      }

      public int getTotalBytesRead() {
         return this.pos - this.startPos;
      }

      public byte readRawByte() throws IOException {
         if (this.pos == this.limit) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            return this.buffer[this.pos++];
         }
      }

      public byte[] readRawBytes(int length) throws IOException {
         if (length > 0 && length <= this.limit - this.pos) {
            int tempPos = this.pos;
            this.pos += length;
            return Arrays.copyOfRange(this.buffer, tempPos, this.pos);
         } else if (length <= 0) {
            if (length == 0) {
               return Internal.EMPTY_BYTE_ARRAY;
            } else {
               throw InvalidProtocolBufferException.negativeSize();
            }
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      public void skipRawBytes(int length) throws IOException {
         if (length >= 0 && length <= this.limit - this.pos) {
            this.pos += length;
         } else if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      // $FF: synthetic method
      ArrayDecoder(byte[] x0, int x1, int x2, boolean x3, Object x4) {
         this(x0, x1, x2, x3);
      }
   }
}
