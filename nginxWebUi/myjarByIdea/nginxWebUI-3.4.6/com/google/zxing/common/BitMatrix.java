package com.google.zxing.common;

import java.util.Arrays;

public final class BitMatrix implements Cloneable {
   private final int width;
   private final int height;
   private final int rowSize;
   private final int[] bits;

   public BitMatrix(int dimension) {
      this(dimension, dimension);
   }

   public BitMatrix(int width, int height) {
      if (width > 0 && height > 0) {
         this.width = width;
         this.height = height;
         this.rowSize = (width + 31) / 32;
         this.bits = new int[this.rowSize * height];
      } else {
         throw new IllegalArgumentException("Both dimensions must be greater than 0");
      }
   }

   private BitMatrix(int width, int height, int rowSize, int[] bits) {
      this.width = width;
      this.height = height;
      this.rowSize = rowSize;
      this.bits = bits;
   }

   public static BitMatrix parse(String stringRepresentation, String setString, String unsetString) {
      if (stringRepresentation == null) {
         throw new IllegalArgumentException();
      } else {
         boolean[] bits = new boolean[stringRepresentation.length()];
         int bitsPos = 0;
         int rowStartPos = 0;
         int rowLength = -1;
         int nRows = 0;
         int pos = 0;

         while(true) {
            while(pos < stringRepresentation.length()) {
               if (stringRepresentation.charAt(pos) != '\n' && stringRepresentation.charAt(pos) != '\r') {
                  if (stringRepresentation.substring(pos, pos + setString.length()).equals(setString)) {
                     pos += setString.length();
                     bits[bitsPos] = true;
                     ++bitsPos;
                  } else {
                     if (!stringRepresentation.substring(pos, pos + unsetString.length()).equals(unsetString)) {
                        throw new IllegalArgumentException("illegal character encountered: " + stringRepresentation.substring(pos));
                     }

                     pos += unsetString.length();
                     bits[bitsPos] = false;
                     ++bitsPos;
                  }
               } else {
                  if (bitsPos > rowStartPos) {
                     if (rowLength == -1) {
                        rowLength = bitsPos - rowStartPos;
                     } else if (bitsPos - rowStartPos != rowLength) {
                        throw new IllegalArgumentException("row lengths do not match");
                     }

                     rowStartPos = bitsPos;
                     ++nRows;
                  }

                  ++pos;
               }
            }

            if (bitsPos > rowStartPos) {
               if (rowLength == -1) {
                  rowLength = bitsPos - rowStartPos;
               } else if (bitsPos - rowStartPos != rowLength) {
                  throw new IllegalArgumentException("row lengths do not match");
               }

               ++nRows;
            }

            BitMatrix matrix = new BitMatrix(rowLength, nRows);

            for(int i = 0; i < bitsPos; ++i) {
               if (bits[i]) {
                  matrix.set(i % rowLength, i / rowLength);
               }
            }

            return matrix;
         }
      }
   }

   public boolean get(int x, int y) {
      int offset = y * this.rowSize + x / 32;
      return (this.bits[offset] >>> (x & 31) & 1) != 0;
   }

   public void set(int x, int y) {
      int offset = y * this.rowSize + x / 32;
      int[] var10000 = this.bits;
      var10000[offset] |= 1 << (x & 31);
   }

   public void unset(int x, int y) {
      int offset = y * this.rowSize + x / 32;
      int[] var10000 = this.bits;
      var10000[offset] &= ~(1 << (x & 31));
   }

   public void flip(int x, int y) {
      int offset = y * this.rowSize + x / 32;
      int[] var10000 = this.bits;
      var10000[offset] ^= 1 << (x & 31);
   }

   public void xor(BitMatrix mask) {
      if (this.width == mask.getWidth() && this.height == mask.getHeight() && this.rowSize == mask.getRowSize()) {
         BitArray rowArray = new BitArray(this.width / 32 + 1);

         for(int y = 0; y < this.height; ++y) {
            int offset = y * this.rowSize;
            int[] row = mask.getRow(y, rowArray).getBitArray();

            for(int x = 0; x < this.rowSize; ++x) {
               int[] var10000 = this.bits;
               var10000[offset + x] ^= row[x];
            }
         }

      } else {
         throw new IllegalArgumentException("input matrix dimensions do not match");
      }
   }

   public void clear() {
      int max = this.bits.length;

      for(int i = 0; i < max; ++i) {
         this.bits[i] = 0;
      }

   }

   public void setRegion(int left, int top, int width, int height) {
      if (top >= 0 && left >= 0) {
         if (height > 0 && width > 0) {
            int right = left + width;
            int bottom;
            if ((bottom = top + height) <= this.height && right <= this.width) {
               for(int y = top; y < bottom; ++y) {
                  int offset = y * this.rowSize;

                  for(int x = left; x < right; ++x) {
                     int[] var10000 = this.bits;
                     var10000[offset + x / 32] |= 1 << (x & 31);
                  }
               }

            } else {
               throw new IllegalArgumentException("The region must fit inside the matrix");
            }
         } else {
            throw new IllegalArgumentException("Height and width must be at least 1");
         }
      } else {
         throw new IllegalArgumentException("Left and top must be nonnegative");
      }
   }

   public BitArray getRow(int y, BitArray row) {
      if (row != null && row.getSize() >= this.width) {
         row.clear();
      } else {
         row = new BitArray(this.width);
      }

      int offset = y * this.rowSize;

      for(int x = 0; x < this.rowSize; ++x) {
         row.setBulk(x << 5, this.bits[offset + x]);
      }

      return row;
   }

   public void setRow(int y, BitArray row) {
      System.arraycopy(row.getBitArray(), 0, this.bits, y * this.rowSize, this.rowSize);
   }

   public void rotate180() {
      int width = this.getWidth();
      int height = this.getHeight();
      BitArray topRow = new BitArray(width);
      BitArray bottomRow = new BitArray(width);

      for(int i = 0; i < (height + 1) / 2; ++i) {
         topRow = this.getRow(i, topRow);
         bottomRow = this.getRow(height - 1 - i, bottomRow);
         topRow.reverse();
         bottomRow.reverse();
         this.setRow(i, bottomRow);
         this.setRow(height - 1 - i, topRow);
      }

   }

   public int[] getEnclosingRectangle() {
      int left = this.width;
      int top = this.height;
      int right = -1;
      int bottom = -1;

      for(int y = 0; y < this.height; ++y) {
         for(int x32 = 0; x32 < this.rowSize; ++x32) {
            int theBits;
            if ((theBits = this.bits[y * this.rowSize + x32]) != 0) {
               if (y < top) {
                  top = y;
               }

               if (y > bottom) {
                  bottom = y;
               }

               int bit;
               if (x32 << 5 < left) {
                  for(bit = 0; theBits << 31 - bit == 0; ++bit) {
                  }

                  if ((x32 << 5) + bit < left) {
                     left = (x32 << 5) + bit;
                  }
               }

               if ((x32 << 5) + 31 > right) {
                  for(bit = 31; theBits >>> bit == 0; --bit) {
                  }

                  if ((x32 << 5) + bit > right) {
                     right = (x32 << 5) + bit;
                  }
               }
            }
         }
      }

      if (right >= left && bottom >= top) {
         return new int[]{left, top, right - left + 1, bottom - top + 1};
      } else {
         return null;
      }
   }

   public int[] getTopLeftOnBit() {
      int bitsOffset;
      for(bitsOffset = 0; bitsOffset < this.bits.length && this.bits[bitsOffset] == 0; ++bitsOffset) {
      }

      if (bitsOffset == this.bits.length) {
         return null;
      } else {
         int y = bitsOffset / this.rowSize;
         int x = bitsOffset % this.rowSize << 5;
         int theBits = this.bits[bitsOffset];

         int bit;
         for(bit = 0; theBits << 31 - bit == 0; ++bit) {
         }

         x += bit;
         return new int[]{x, y};
      }
   }

   public int[] getBottomRightOnBit() {
      int bitsOffset;
      for(bitsOffset = this.bits.length - 1; bitsOffset >= 0 && this.bits[bitsOffset] == 0; --bitsOffset) {
      }

      if (bitsOffset < 0) {
         return null;
      } else {
         int y = bitsOffset / this.rowSize;
         int x = bitsOffset % this.rowSize << 5;
         int theBits = this.bits[bitsOffset];

         int bit;
         for(bit = 31; theBits >>> bit == 0; --bit) {
         }

         x += bit;
         return new int[]{x, y};
      }
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getRowSize() {
      return this.rowSize;
   }

   public boolean equals(Object o) {
      if (!(o instanceof BitMatrix)) {
         return false;
      } else {
         BitMatrix other = (BitMatrix)o;
         return this.width == other.width && this.height == other.height && this.rowSize == other.rowSize && Arrays.equals(this.bits, other.bits);
      }
   }

   public int hashCode() {
      return (((this.width * 31 + this.width) * 31 + this.height) * 31 + this.rowSize) * 31 + Arrays.hashCode(this.bits);
   }

   public String toString() {
      return this.toString("X ", "  ");
   }

   public String toString(String setString, String unsetString) {
      return this.buildToString(setString, unsetString, "\n");
   }

   /** @deprecated */
   @Deprecated
   public String toString(String setString, String unsetString, String lineSeparator) {
      return this.buildToString(setString, unsetString, lineSeparator);
   }

   private String buildToString(String setString, String unsetString, String lineSeparator) {
      StringBuilder result = new StringBuilder(this.height * (this.width + 1));

      for(int y = 0; y < this.height; ++y) {
         for(int x = 0; x < this.width; ++x) {
            result.append(this.get(x, y) ? setString : unsetString);
         }

         result.append(lineSeparator);
      }

      return result.toString();
   }

   public BitMatrix clone() {
      return new BitMatrix(this.width, this.height, this.rowSize, (int[])this.bits.clone());
   }
}
