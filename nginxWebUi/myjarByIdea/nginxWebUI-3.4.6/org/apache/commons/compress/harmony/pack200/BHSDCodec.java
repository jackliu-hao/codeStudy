package org.apache.commons.compress.harmony.pack200;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class BHSDCodec extends Codec {
   private final int b;
   private final int d;
   private final int h;
   private final int l;
   private final int s;
   private long cardinality;
   private final long smallest;
   private final long largest;
   private final long[] powers;

   public BHSDCodec(int b, int h) {
      this(b, h, 0, 0);
   }

   public BHSDCodec(int b, int h, int s) {
      this(b, h, s, 0);
   }

   public BHSDCodec(int b, int h, int s, int d) {
      if (b >= 1 && b <= 5) {
         if (h >= 1 && h <= 256) {
            if (s >= 0 && s <= 2) {
               if (d >= 0 && d <= 1) {
                  if (b == 1 && h != 256) {
                     throw new IllegalArgumentException("b=1 -> h=256");
                  } else if (h == 256 && b == 5) {
                     throw new IllegalArgumentException("h=256 -> b!=5");
                  } else {
                     this.b = b;
                     this.h = h;
                     this.s = s;
                     this.d = d;
                     this.l = 256 - h;
                     if (h == 1) {
                        this.cardinality = (long)(b * 255 + 1);
                     } else {
                        this.cardinality = (long)((double)((long)((double)this.l * (1.0 - Math.pow((double)h, (double)b)) / (double)(1 - h))) + Math.pow((double)h, (double)b));
                     }

                     this.smallest = this.calculateSmallest();
                     this.largest = this.calculateLargest();
                     this.powers = new long[b];

                     for(int c = 0; c < b; ++c) {
                        this.powers[c] = (long)Math.pow((double)h, (double)c);
                     }

                  }
               } else {
                  throw new IllegalArgumentException("0<=d<=1");
               }
            } else {
               throw new IllegalArgumentException("0<=s<=2");
            }
         } else {
            throw new IllegalArgumentException("1<=h<=256");
         }
      } else {
         throw new IllegalArgumentException("1<=b<=5");
      }
   }

   public long cardinality() {
      return this.cardinality;
   }

   public int decode(InputStream in) throws IOException, Pack200Exception {
      if (this.d != 0) {
         throw new Pack200Exception("Delta encoding used without passing in last value; this is a coding error");
      } else {
         return this.decode(in, 0L);
      }
   }

   public int decode(InputStream in, long last) throws IOException, Pack200Exception {
      int n = 0;
      long z = 0L;
      long x = 0L;

      do {
         x = (long)in.read();
         ++this.lastBandLength;
         z += x * this.powers[n];
         ++n;
      } while(x >= (long)this.l && n < this.b);

      if (x == -1L) {
         throw new EOFException("End of stream reached whilst decoding");
      } else {
         if (this.isSigned()) {
            int u = (1 << this.s) - 1;
            if ((z & (long)u) == (long)u) {
               z = ~(z >>> this.s);
            } else {
               z -= z >>> this.s;
            }
         }

         if (this.isDelta()) {
            z += last;
         }

         return (int)z;
      }
   }

   public int[] decodeInts(int n, InputStream in) throws IOException, Pack200Exception {
      int[] band = super.decodeInts(n, in);
      if (this.isDelta()) {
         for(int i = 0; i < band.length; ++i) {
            while((long)band[i] > this.largest) {
               band[i] = (int)((long)band[i] - this.cardinality);
            }

            while((long)band[i] < this.smallest) {
               band[i] = (int)((long)band[i] + this.cardinality);
            }
         }
      }

      return band;
   }

   public int[] decodeInts(int n, InputStream in, int firstValue) throws IOException, Pack200Exception {
      int[] band = super.decodeInts(n, in, firstValue);
      if (this.isDelta()) {
         for(int i = 0; i < band.length; ++i) {
            while((long)band[i] > this.largest) {
               band[i] = (int)((long)band[i] - this.cardinality);
            }

            while((long)band[i] < this.smallest) {
               band[i] = (int)((long)band[i] + this.cardinality);
            }
         }
      }

      return band;
   }

   public boolean encodes(long value) {
      return value >= this.smallest && value <= this.largest;
   }

   public byte[] encode(int value, int last) throws Pack200Exception {
      if (!this.encodes((long)value)) {
         throw new Pack200Exception("The codec " + this.toString() + " does not encode the value " + value);
      } else {
         long z = (long)value;
         if (this.isDelta()) {
            z -= (long)last;
         }

         if (this.isSigned()) {
            if (z < -2147483648L) {
               z += 4294967296L;
            } else if (z > 2147483647L) {
               z -= 4294967296L;
            }

            if (z < 0L) {
               z = (-z << this.s) - 1L;
            } else if (this.s == 1) {
               z <<= this.s;
            } else {
               z += (z - z % 3L) / 3L;
            }
         } else if (z < 0L) {
            if (this.cardinality < 4294967296L) {
               z += this.cardinality;
            } else {
               z += 4294967296L;
            }
         }

         if (z < 0L) {
            throw new Pack200Exception("unable to encode");
         } else {
            List byteList = new ArrayList();

            for(int n = 0; n < this.b; ++n) {
               long byteN;
               if (z < (long)this.l) {
                  byteN = z;
               } else {
                  for(byteN = z % (long)this.h; byteN < (long)this.l; byteN += (long)this.h) {
                  }
               }

               byteList.add((byte)((int)byteN));
               if (byteN < (long)this.l) {
                  break;
               }

               z -= byteN;
               z /= (long)this.h;
            }

            byte[] bytes = new byte[byteList.size()];

            for(int i = 0; i < bytes.length; ++i) {
               bytes[i] = (Byte)byteList.get(i);
            }

            return bytes;
         }
      }
   }

   public byte[] encode(int value) throws Pack200Exception {
      return this.encode(value, 0);
   }

   public boolean isDelta() {
      return this.d != 0;
   }

   public boolean isSigned() {
      return this.s != 0;
   }

   public long largest() {
      return this.largest;
   }

   private long calculateLargest() {
      if (this.d == 1) {
         BHSDCodec bh0 = new BHSDCodec(this.b, this.h);
         return bh0.largest();
      } else {
         long result;
         if (this.s == 0) {
            result = this.cardinality() - 1L;
         } else if (this.s == 1) {
            result = this.cardinality() / 2L - 1L;
         } else {
            if (this.s != 2) {
               throw new Error("Unknown s value");
            }

            result = 3L * this.cardinality() / 4L - 1L;
         }

         return Math.min((this.s == 0 ? 4294967294L : 2147483647L) - 1L, result);
      }
   }

   public long smallest() {
      return this.smallest;
   }

   private long calculateSmallest() {
      long result;
      if (this.d != 1 && this.isSigned()) {
         result = Math.max(-2147483648L, -this.cardinality() / (long)(1 << this.s));
      } else if (this.cardinality >= 4294967296L) {
         result = -2147483648L;
      } else {
         result = 0L;
      }

      return result;
   }

   public String toString() {
      StringBuffer buffer = new StringBuffer(11);
      buffer.append('(');
      buffer.append(this.b);
      buffer.append(',');
      buffer.append(this.h);
      if (this.s != 0 || this.d != 0) {
         buffer.append(',');
         buffer.append(this.s);
      }

      if (this.d != 0) {
         buffer.append(',');
         buffer.append(this.d);
      }

      buffer.append(')');
      return buffer.toString();
   }

   public int getB() {
      return this.b;
   }

   public int getH() {
      return this.h;
   }

   public int getS() {
      return this.s;
   }

   public int getL() {
      return this.l;
   }

   public boolean equals(Object o) {
      if (!(o instanceof BHSDCodec)) {
         return false;
      } else {
         BHSDCodec codec = (BHSDCodec)o;
         return codec.b == this.b && codec.h == this.h && codec.s == this.s && codec.d == this.d;
      }
   }

   public int hashCode() {
      return ((this.b * 37 + this.h) * 37 + this.s) * 37 + this.d;
   }
}
