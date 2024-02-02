package org.apache.commons.compress.compressors.snappy;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.lz77support.AbstractLZ77CompressorInputStream;
import org.apache.commons.compress.utils.ByteUtils;

public class SnappyCompressorInputStream extends AbstractLZ77CompressorInputStream {
   private static final int TAG_MASK = 3;
   public static final int DEFAULT_BLOCK_SIZE = 32768;
   private final int size;
   private int uncompressedBytesRemaining;
   private State state;
   private boolean endReached;

   public SnappyCompressorInputStream(InputStream is) throws IOException {
      this(is, 32768);
   }

   public SnappyCompressorInputStream(InputStream is, int blockSize) throws IOException {
      super(is, blockSize);
      this.state = SnappyCompressorInputStream.State.NO_BLOCK;
      this.uncompressedBytesRemaining = this.size = (int)this.readSize();
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (len == 0) {
         return 0;
      } else if (this.endReached) {
         return -1;
      } else {
         switch (this.state) {
            case NO_BLOCK:
               this.fill();
               return this.read(b, off, len);
            case IN_LITERAL:
               int litLen = this.readLiteral(b, off, len);
               if (!this.hasMoreDataInBlock()) {
                  this.state = SnappyCompressorInputStream.State.NO_BLOCK;
               }

               return litLen > 0 ? litLen : this.read(b, off, len);
            case IN_BACK_REFERENCE:
               int backReferenceLen = this.readBackReference(b, off, len);
               if (!this.hasMoreDataInBlock()) {
                  this.state = SnappyCompressorInputStream.State.NO_BLOCK;
               }

               return backReferenceLen > 0 ? backReferenceLen : this.read(b, off, len);
            default:
               throw new IOException("Unknown stream state " + this.state);
         }
      }
   }

   private void fill() throws IOException {
      if (this.uncompressedBytesRemaining == 0) {
         this.endReached = true;
      } else {
         int b = this.readOneByte();
         if (b == -1) {
            throw new IOException("Premature end of stream reading block start");
         } else {
            int length = false;
            int offset = false;
            int length;
            int offset;
            switch (b & 3) {
               case 0:
                  length = this.readLiteralLength(b);
                  if (length < 0) {
                     throw new IOException("Illegal block with a negative literal size found");
                  }

                  this.uncompressedBytesRemaining -= length;
                  this.startLiteral((long)length);
                  this.state = SnappyCompressorInputStream.State.IN_LITERAL;
                  break;
               case 1:
                  length = 4 + (b >> 2 & 7);
                  if (length < 0) {
                     throw new IOException("Illegal block with a negative match length found");
                  }

                  this.uncompressedBytesRemaining -= length;
                  offset = (b & 224) << 3;
                  b = this.readOneByte();
                  if (b == -1) {
                     throw new IOException("Premature end of stream reading back-reference length");
                  }

                  offset |= b;

                  try {
                     this.startBackReference(offset, (long)length);
                  } catch (IllegalArgumentException var7) {
                     throw new IOException("Illegal block with bad offset found", var7);
                  }

                  this.state = SnappyCompressorInputStream.State.IN_BACK_REFERENCE;
                  break;
               case 2:
                  length = (b >> 2) + 1;
                  if (length < 0) {
                     throw new IOException("Illegal block with a negative match length found");
                  }

                  this.uncompressedBytesRemaining -= length;
                  offset = (int)ByteUtils.fromLittleEndian((ByteUtils.ByteSupplier)this.supplier, 2);

                  try {
                     this.startBackReference(offset, (long)length);
                  } catch (IllegalArgumentException var6) {
                     throw new IOException("Illegal block with bad offset found", var6);
                  }

                  this.state = SnappyCompressorInputStream.State.IN_BACK_REFERENCE;
                  break;
               case 3:
                  length = (b >> 2) + 1;
                  if (length < 0) {
                     throw new IOException("Illegal block with a negative match length found");
                  }

                  this.uncompressedBytesRemaining -= length;
                  offset = (int)ByteUtils.fromLittleEndian((ByteUtils.ByteSupplier)this.supplier, 4) & Integer.MAX_VALUE;

                  try {
                     this.startBackReference(offset, (long)length);
                  } catch (IllegalArgumentException var5) {
                     throw new IOException("Illegal block with bad offset found", var5);
                  }

                  this.state = SnappyCompressorInputStream.State.IN_BACK_REFERENCE;
            }

         }
      }
   }

   private int readLiteralLength(int b) throws IOException {
      int length;
      switch (b >> 2) {
         case 60:
            length = this.readOneByte();
            if (length == -1) {
               throw new IOException("Premature end of stream reading literal length");
            }
            break;
         case 61:
            length = (int)ByteUtils.fromLittleEndian((ByteUtils.ByteSupplier)this.supplier, 2);
            break;
         case 62:
            length = (int)ByteUtils.fromLittleEndian((ByteUtils.ByteSupplier)this.supplier, 3);
            break;
         case 63:
            length = (int)ByteUtils.fromLittleEndian((ByteUtils.ByteSupplier)this.supplier, 4);
            break;
         default:
            length = b >> 2;
      }

      return length + 1;
   }

   private long readSize() throws IOException {
      int index = 0;
      long sz = 0L;
      int b = false;

      int b;
      do {
         b = this.readOneByte();
         if (b == -1) {
            throw new IOException("Premature end of stream reading size");
         }

         sz |= (long)((b & 127) << index++ * 7);
      } while(0 != (b & 128));

      return sz;
   }

   public int getSize() {
      return this.size;
   }

   private static enum State {
      NO_BLOCK,
      IN_LITERAL,
      IN_BACK_REFERENCE;
   }
}
