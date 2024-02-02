package com.github.jaiimageio.stream;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStreamImpl;

public class FileChannelImageOutputStream extends ImageOutputStreamImpl {
   private static final int DEFAULT_WRITE_BUFFER_SIZE = 1048576;
   private FileChannel channel;
   private ByteBuffer byteBuffer;
   private ImageInputStream readStream = null;

   public FileChannelImageOutputStream(FileChannel channel) throws IOException {
      if (channel == null) {
         throw new IllegalArgumentException("channel == null");
      } else if (!channel.isOpen()) {
         throw new IllegalArgumentException("channel.isOpen() == false");
      } else {
         this.channel = channel;
         this.streamPos = this.flushedPos = channel.position();
         this.byteBuffer = ByteBuffer.allocateDirect(1048576);
         this.readStream = new FileChannelImageInputStream(channel);
      }
   }

   private ImageInputStream getImageInputStream() throws IOException {
      this.flushBuffer();
      this.readStream.setByteOrder(this.byteOrder);
      this.readStream.seek(this.streamPos);
      this.readStream.flushBefore(this.flushedPos);
      this.readStream.setBitOffset(this.bitOffset);
      return this.readStream;
   }

   private void flushBuffer() throws IOException {
      if (this.byteBuffer.position() != 0) {
         this.byteBuffer.limit(this.byteBuffer.position());
         this.byteBuffer.position(0);
         this.channel.write(this.byteBuffer);
         this.byteBuffer.clear();
      }

   }

   public int read() throws IOException {
      this.checkClosed();
      this.bitOffset = 0;
      ImageInputStream inputStream = this.getImageInputStream();
      ++this.streamPos;
      return inputStream.read();
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= b.length) {
         if (len == 0) {
            return 0;
         } else {
            this.checkClosed();
            this.bitOffset = 0;
            ImageInputStream inputStream = this.getImageInputStream();
            int numBytesRead = inputStream.read(b, off, len);
            this.streamPos += (long)numBytesRead;
            return numBytesRead;
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > b.length");
      }
   }

   public void write(int b) throws IOException {
      this.write(new byte[]{(byte)(b & 255)}, 0, 1);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= b.length) {
         if (len != 0) {
            this.flushBits();
            int numPut = 0;

            do {
               int numToPut = Math.min(len - numPut, this.byteBuffer.remaining());
               if (numToPut == 0) {
                  this.flushBuffer();
               } else {
                  this.byteBuffer.put(b, off + numPut, numToPut);
                  numPut += numToPut;
               }
            } while(numPut < len);

            this.streamPos += (long)len;
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > b.length");
      }
   }

   public void readFully(char[] c, int off, int len) throws IOException {
      this.getImageInputStream().readFully(c, off, len);
      this.streamPos += (long)(2 * len);
   }

   public void readFully(short[] s, int off, int len) throws IOException {
      this.getImageInputStream().readFully(s, off, len);
      this.streamPos += (long)(2 * len);
   }

   public void readFully(int[] i, int off, int len) throws IOException {
      this.getImageInputStream().readFully(i, off, len);
      this.streamPos += (long)(4 * len);
   }

   public void readFully(long[] l, int off, int len) throws IOException {
      this.getImageInputStream().readFully(l, off, len);
      this.streamPos += (long)(8 * len);
   }

   public void readFully(float[] f, int off, int len) throws IOException {
      this.getImageInputStream().readFully(f, off, len);
      this.streamPos += (long)(4 * len);
   }

   public void readFully(double[] d, int off, int len) throws IOException {
      this.getImageInputStream().readFully(d, off, len);
      this.streamPos += (long)(8 * len);
   }

   public void writeChars(char[] c, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= c.length) {
         if (len != 0) {
            this.flushBits();
            int numPut = 0;
            CharBuffer viewBuffer = this.byteBuffer.asCharBuffer();

            do {
               int numToPut = Math.min(len - numPut, viewBuffer.remaining());
               if (numToPut == 0) {
                  this.flushBuffer();
               } else {
                  viewBuffer.put(c, off + numPut, numToPut);
                  this.byteBuffer.position(this.byteBuffer.position() + 2 * numToPut);
                  numPut += numToPut;
               }
            } while(numPut < len);

            this.streamPos += (long)(2 * len);
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length");
      }
   }

   public void writeShorts(short[] s, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= s.length) {
         if (len != 0) {
            this.flushBits();
            int numPut = 0;
            ShortBuffer viewBuffer = this.byteBuffer.asShortBuffer();

            do {
               int numToPut = Math.min(len - numPut, viewBuffer.remaining());
               if (numToPut == 0) {
                  this.flushBuffer();
               } else {
                  viewBuffer.put(s, off + numPut, numToPut);
                  this.byteBuffer.position(this.byteBuffer.position() + 2 * numToPut);
                  numPut += numToPut;
               }
            } while(numPut < len);

            this.streamPos += (long)(2 * len);
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length");
      }
   }

   public void writeInts(int[] i, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= i.length) {
         if (len != 0) {
            this.flushBits();
            int numPut = 0;
            IntBuffer viewBuffer = this.byteBuffer.asIntBuffer();

            do {
               int numToPut = Math.min(len - numPut, viewBuffer.remaining());
               if (numToPut == 0) {
                  this.flushBuffer();
               } else {
                  viewBuffer.put(i, off + numPut, numToPut);
                  this.byteBuffer.position(this.byteBuffer.position() + 4 * numToPut);
                  numPut += numToPut;
               }
            } while(numPut < len);

            this.streamPos += (long)(4 * len);
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length");
      }
   }

   public void writeLongs(long[] l, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= l.length) {
         if (len != 0) {
            this.flushBits();
            int numPut = 0;
            LongBuffer viewBuffer = this.byteBuffer.asLongBuffer();

            do {
               int numToPut = Math.min(len - numPut, viewBuffer.remaining());
               if (numToPut == 0) {
                  this.flushBuffer();
               } else {
                  viewBuffer.put(l, off + numPut, numToPut);
                  this.byteBuffer.position(this.byteBuffer.position() + 8 * numToPut);
                  numPut += numToPut;
               }
            } while(numPut < len);

            this.streamPos += (long)(8 * len);
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length");
      }
   }

   public void writeFloats(float[] f, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= f.length) {
         if (len != 0) {
            this.flushBits();
            int numPut = 0;
            FloatBuffer viewBuffer = this.byteBuffer.asFloatBuffer();

            do {
               int numToPut = Math.min(len - numPut, viewBuffer.remaining());
               if (numToPut == 0) {
                  this.flushBuffer();
               } else {
                  viewBuffer.put(f, off + numPut, numToPut);
                  this.byteBuffer.position(this.byteBuffer.position() + 4 * numToPut);
                  numPut += numToPut;
               }
            } while(numPut < len);

            this.streamPos += (long)(4 * len);
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > f.length");
      }
   }

   public void writeDoubles(double[] d, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= d.length) {
         if (len != 0) {
            this.flushBits();
            int numPut = 0;
            DoubleBuffer viewBuffer = this.byteBuffer.asDoubleBuffer();

            do {
               int numToPut = Math.min(len - numPut, viewBuffer.remaining());
               if (numToPut == 0) {
                  this.flushBuffer();
               } else {
                  viewBuffer.put(d, off + numPut, numToPut);
                  this.byteBuffer.position(this.byteBuffer.position() + 8 * numToPut);
                  numPut += numToPut;
               }
            } while(numPut < len);

            this.streamPos += (long)(8 * len);
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > d.length");
      }
   }

   public void close() throws IOException {
      this.flushBuffer();
      this.readStream.close();
      this.readStream = null;
      this.channel = null;
      this.byteBuffer = null;
      super.close();
   }

   public long length() {
      long length = -1L;

      try {
         length = this.channel.size();
      } catch (IOException var4) {
      }

      return length;
   }

   public void seek(long pos) throws IOException {
      super.seek(pos);
      this.flushBuffer();
      this.channel.position(pos);
   }

   public void setByteOrder(ByteOrder networkByteOrder) {
      super.setByteOrder(networkByteOrder);
      this.byteBuffer.order(networkByteOrder);
   }
}
