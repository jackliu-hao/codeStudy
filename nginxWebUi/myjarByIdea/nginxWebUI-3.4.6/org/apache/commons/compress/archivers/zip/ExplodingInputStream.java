package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.utils.CloseShieldFilterInputStream;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.InputStreamStatistics;

class ExplodingInputStream extends InputStream implements InputStreamStatistics {
   private final InputStream in;
   private BitStream bits;
   private final int dictionarySize;
   private final int numberOfTrees;
   private final int minimumMatchLength;
   private BinaryTree literalTree;
   private BinaryTree lengthTree;
   private BinaryTree distanceTree;
   private final CircularBuffer buffer = new CircularBuffer(32768);
   private long uncompressedCount;
   private long treeSizes;

   public ExplodingInputStream(int dictionarySize, int numberOfTrees, InputStream in) {
      if (dictionarySize != 4096 && dictionarySize != 8192) {
         throw new IllegalArgumentException("The dictionary size must be 4096 or 8192");
      } else if (numberOfTrees != 2 && numberOfTrees != 3) {
         throw new IllegalArgumentException("The number of trees must be 2 or 3");
      } else {
         this.dictionarySize = dictionarySize;
         this.numberOfTrees = numberOfTrees;
         this.minimumMatchLength = numberOfTrees;
         this.in = in;
      }
   }

   private void init() throws IOException {
      if (this.bits == null) {
         CountingInputStream i = new CountingInputStream(new CloseShieldFilterInputStream(this.in));
         Throwable var2 = null;

         try {
            if (this.numberOfTrees == 3) {
               this.literalTree = BinaryTree.decode(i, 256);
            }

            this.lengthTree = BinaryTree.decode(i, 64);
            this.distanceTree = BinaryTree.decode(i, 64);
            this.treeSizes += i.getBytesRead();
         } catch (Throwable var11) {
            var2 = var11;
            throw var11;
         } finally {
            if (i != null) {
               if (var2 != null) {
                  try {
                     i.close();
                  } catch (Throwable var10) {
                     var2.addSuppressed(var10);
                  }
               } else {
                  i.close();
               }
            }

         }

         this.bits = new BitStream(this.in);
      }

   }

   public int read() throws IOException {
      if (!this.buffer.available()) {
         try {
            this.fillBuffer();
         } catch (IllegalArgumentException var2) {
            throw new IOException("bad IMPLODE stream", var2);
         }
      }

      int ret = this.buffer.get();
      if (ret > -1) {
         ++this.uncompressedCount;
      }

      return ret;
   }

   public long getCompressedCount() {
      return this.bits.getBytesRead() + this.treeSizes;
   }

   public long getUncompressedCount() {
      return this.uncompressedCount;
   }

   public void close() throws IOException {
      this.in.close();
   }

   private void fillBuffer() throws IOException {
      this.init();
      int bit = this.bits.nextBit();
      if (bit != -1) {
         int literal;
         if (bit == 1) {
            if (this.literalTree != null) {
               literal = this.literalTree.read(this.bits);
            } else {
               literal = this.bits.nextByte();
            }

            if (literal == -1) {
               return;
            }

            this.buffer.put(literal);
         } else {
            literal = this.dictionarySize == 4096 ? 6 : 7;
            int distanceLow = (int)this.bits.nextBits(literal);
            int distanceHigh = this.distanceTree.read(this.bits);
            if (distanceHigh == -1 && distanceLow <= 0) {
               return;
            }

            int distance = distanceHigh << literal | distanceLow;
            int length = this.lengthTree.read(this.bits);
            if (length == 63) {
               long nextByte = this.bits.nextBits(8);
               if (nextByte == -1L) {
                  return;
               }

               length = (int)((long)length + nextByte);
            }

            length += this.minimumMatchLength;
            this.buffer.copy(distance + 1, length);
         }

      }
   }
}
