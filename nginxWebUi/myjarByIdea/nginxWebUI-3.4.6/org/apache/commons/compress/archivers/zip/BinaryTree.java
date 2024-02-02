package org.apache.commons.compress.archivers.zip;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.commons.compress.utils.IOUtils;

class BinaryTree {
   private static final int UNDEFINED = -1;
   private static final int NODE = -2;
   private final int[] tree;

   public BinaryTree(int depth) {
      if (depth >= 0 && depth <= 30) {
         this.tree = new int[(int)((1L << depth + 1) - 1L)];
         Arrays.fill(this.tree, -1);
      } else {
         throw new IllegalArgumentException("depth must be bigger than 0 and not bigger than 30 but is " + depth);
      }
   }

   public void addLeaf(int node, int path, int depth, int value) {
      if (depth == 0) {
         if (this.tree[node] != -1) {
            throw new IllegalArgumentException("Tree value at index " + node + " has already been assigned (" + this.tree[node] + ")");
         }

         this.tree[node] = value;
      } else {
         this.tree[node] = -2;
         int nextChild = 2 * node + 1 + (path & 1);
         this.addLeaf(nextChild, path >>> 1, depth - 1, value);
      }

   }

   public int read(BitStream stream) throws IOException {
      int currentIndex = 0;

      while(true) {
         int bit = stream.nextBit();
         if (bit == -1) {
            return -1;
         }

         int childIndex = 2 * currentIndex + 1 + bit;
         int value = this.tree[childIndex];
         if (value != -2) {
            if (value != -1) {
               return value;
            }

            throw new IOException("The child " + bit + " of node at index " + currentIndex + " is not defined");
         }

         currentIndex = childIndex;
      }
   }

   static BinaryTree decode(InputStream inputStream, int totalNumberOfValues) throws IOException {
      if (totalNumberOfValues < 0) {
         throw new IllegalArgumentException("totalNumberOfValues must be bigger than 0, is " + totalNumberOfValues);
      } else {
         int size = inputStream.read() + 1;
         if (size == 0) {
            throw new IOException("Cannot read the size of the encoded tree, unexpected end of stream");
         } else {
            byte[] encodedTree = IOUtils.readRange(inputStream, size);
            if (encodedTree.length != size) {
               throw new EOFException();
            } else {
               int maxLength = 0;
               int[] originalBitLengths = new int[totalNumberOfValues];
               int pos = 0;
               byte[] var7 = encodedTree;
               int var8 = encodedTree.length;

               int c;
               int code;
               int codeIncrement;
               int lastBitLength;
               for(c = 0; c < var8; ++c) {
                  byte b = var7[c];
                  code = ((b & 240) >> 4) + 1;
                  if (pos + code > totalNumberOfValues) {
                     throw new IOException("Number of values exceeds given total number of values");
                  }

                  codeIncrement = (b & 15) + 1;

                  for(lastBitLength = 0; lastBitLength < code; ++lastBitLength) {
                     originalBitLengths[pos++] = codeIncrement;
                  }

                  maxLength = Math.max(maxLength, codeIncrement);
               }

               int oBitLengths = originalBitLengths.length;
               int[] permutation = new int[oBitLengths];

               for(c = 0; c < permutation.length; permutation[c] = c++) {
               }

               c = 0;
               int[] sortedBitLengths = new int[oBitLengths];

               for(code = 0; code < oBitLengths; ++code) {
                  for(codeIncrement = 0; codeIncrement < oBitLengths; ++codeIncrement) {
                     if (originalBitLengths[codeIncrement] == code) {
                        sortedBitLengths[c] = code;
                        permutation[c] = codeIncrement;
                        ++c;
                     }
                  }
               }

               code = 0;
               codeIncrement = 0;
               lastBitLength = 0;
               int[] codes = new int[totalNumberOfValues];

               for(int i = totalNumberOfValues - 1; i >= 0; --i) {
                  code += codeIncrement;
                  if (sortedBitLengths[i] != lastBitLength) {
                     lastBitLength = sortedBitLengths[i];
                     codeIncrement = 1 << 16 - lastBitLength;
                  }

                  codes[permutation[i]] = code;
               }

               BinaryTree tree = new BinaryTree(maxLength);

               for(int k = 0; k < codes.length; ++k) {
                  int bitLength = originalBitLengths[k];
                  if (bitLength > 0) {
                     tree.addLeaf(0, Integer.reverse(codes[k] << 16), bitLength, k);
                  }
               }

               return tree;
            }
         }
      }
   }
}
