package org.h2.security;

import java.security.MessageDigest;
import java.util.Arrays;
import org.h2.util.Bits;

public final class SHA3 extends MessageDigest {
   private static final long[] ROUND_CONSTANTS;
   private final int digestLength;
   private final int rate;
   private long state00;
   private long state01;
   private long state02;
   private long state03;
   private long state04;
   private long state05;
   private long state06;
   private long state07;
   private long state08;
   private long state09;
   private long state10;
   private long state11;
   private long state12;
   private long state13;
   private long state14;
   private long state15;
   private long state16;
   private long state17;
   private long state18;
   private long state19;
   private long state20;
   private long state21;
   private long state22;
   private long state23;
   private long state24;
   private final byte[] buf;
   private int bufcnt;

   public static SHA3 getSha3_224() {
      return new SHA3("SHA3-224", 28);
   }

   public static SHA3 getSha3_256() {
      return new SHA3("SHA3-256", 32);
   }

   public static SHA3 getSha3_384() {
      return new SHA3("SHA3-384", 48);
   }

   public static SHA3 getSha3_512() {
      return new SHA3("SHA3-512", 64);
   }

   private SHA3(String var1, int var2) {
      super(var1);
      this.digestLength = var2;
      this.buf = new byte[this.rate = 200 - var2 * 2];
   }

   protected byte[] engineDigest() {
      this.buf[this.bufcnt] = 6;
      Arrays.fill(this.buf, this.bufcnt + 1, this.rate, (byte)0);
      byte[] var10000 = this.buf;
      int var10001 = this.rate - 1;
      var10000[var10001] = (byte)(var10000[var10001] | 128);
      this.absorbQueue();
      byte[] var1 = new byte[this.digestLength];
      switch (this.digestLength) {
         case 28:
            Bits.writeIntLE(var1, 24, (int)this.state03);
            break;
         case 64:
            Bits.writeLongLE(var1, 56, this.state07);
            Bits.writeLongLE(var1, 48, this.state06);
         case 48:
            Bits.writeLongLE(var1, 40, this.state05);
            Bits.writeLongLE(var1, 32, this.state04);
         case 32:
            Bits.writeLongLE(var1, 24, this.state03);
      }

      Bits.writeLongLE(var1, 16, this.state02);
      Bits.writeLongLE(var1, 8, this.state01);
      Bits.writeLongLE(var1, 0, this.state00);
      this.engineReset();
      return var1;
   }

   protected int engineGetDigestLength() {
      return this.digestLength;
   }

   protected void engineReset() {
      this.state24 = this.state23 = this.state22 = this.state21 = this.state20 = this.state19 = this.state18 = this.state17 = this.state16 = this.state15 = this.state14 = this.state13 = this.state12 = this.state11 = this.state10 = this.state09 = this.state08 = this.state07 = this.state06 = this.state05 = this.state04 = this.state03 = this.state02 = this.state01 = this.state00 = 0L;
      Arrays.fill(this.buf, (byte)0);
      this.bufcnt = 0;
   }

   protected void engineUpdate(byte var1) {
      this.buf[this.bufcnt++] = var1;
      if (this.bufcnt == this.rate) {
         this.absorbQueue();
      }

   }

   protected void engineUpdate(byte[] var1, int var2, int var3) {
      while(var3 > 0) {
         if (this.bufcnt == 0 && var3 >= this.rate) {
            while(true) {
               this.absorb(var1, var2);
               var2 += this.rate;
               var3 -= this.rate;
               if (var3 < this.rate) {
                  break;
               }
            }
         } else {
            int var4 = Math.min(var3, this.rate - this.bufcnt);
            System.arraycopy(var1, var2, this.buf, this.bufcnt, var4);
            this.bufcnt += var4;
            var2 += var4;
            var3 -= var4;
            if (this.bufcnt == this.rate) {
               this.absorbQueue();
            }
         }
      }

   }

   private void absorbQueue() {
      this.absorb(this.buf, 0);
      this.bufcnt = 0;
   }

   private void absorb(byte[] var1, int var2) {
      switch (this.digestLength) {
         case 28:
            this.state17 ^= Bits.readLongLE(var1, var2 + 136);
         case 32:
            this.state13 ^= Bits.readLongLE(var1, var2 + 104);
            this.state14 ^= Bits.readLongLE(var1, var2 + 112);
            this.state15 ^= Bits.readLongLE(var1, var2 + 120);
            this.state16 ^= Bits.readLongLE(var1, var2 + 128);
         case 48:
            this.state09 ^= Bits.readLongLE(var1, var2 + 72);
            this.state10 ^= Bits.readLongLE(var1, var2 + 80);
            this.state11 ^= Bits.readLongLE(var1, var2 + 88);
            this.state12 ^= Bits.readLongLE(var1, var2 + 96);
         default:
            this.state00 ^= Bits.readLongLE(var1, var2);
            this.state01 ^= Bits.readLongLE(var1, var2 + 8);
            this.state02 ^= Bits.readLongLE(var1, var2 + 16);
            this.state03 ^= Bits.readLongLE(var1, var2 + 24);
            this.state04 ^= Bits.readLongLE(var1, var2 + 32);
            this.state05 ^= Bits.readLongLE(var1, var2 + 40);
            this.state06 ^= Bits.readLongLE(var1, var2 + 48);
            this.state07 ^= Bits.readLongLE(var1, var2 + 56);
            this.state08 ^= Bits.readLongLE(var1, var2 + 64);

            for(int var3 = 0; var3 < 24; ++var3) {
               long var4 = this.state00 ^ this.state05 ^ this.state10 ^ this.state15 ^ this.state20;
               long var6 = this.state01 ^ this.state06 ^ this.state11 ^ this.state16 ^ this.state21;
               long var8 = this.state02 ^ this.state07 ^ this.state12 ^ this.state17 ^ this.state22;
               long var10 = this.state03 ^ this.state08 ^ this.state13 ^ this.state18 ^ this.state23;
               long var12 = this.state04 ^ this.state09 ^ this.state14 ^ this.state19 ^ this.state24;
               long var14 = (var6 << 1 | var6 >>> 63) ^ var12;
               this.state00 ^= var14;
               this.state05 ^= var14;
               this.state10 ^= var14;
               this.state15 ^= var14;
               this.state20 ^= var14;
               var14 = (var8 << 1 | var8 >>> 63) ^ var4;
               this.state01 ^= var14;
               this.state06 ^= var14;
               this.state11 ^= var14;
               this.state16 ^= var14;
               this.state21 ^= var14;
               var14 = (var10 << 1 | var10 >>> 63) ^ var6;
               this.state02 ^= var14;
               this.state07 ^= var14;
               this.state12 ^= var14;
               this.state17 ^= var14;
               this.state22 ^= var14;
               var14 = (var12 << 1 | var12 >>> 63) ^ var8;
               this.state03 ^= var14;
               this.state08 ^= var14;
               this.state13 ^= var14;
               this.state18 ^= var14;
               this.state23 ^= var14;
               var14 = (var4 << 1 | var4 >>> 63) ^ var10;
               this.state04 ^= var14;
               this.state09 ^= var14;
               this.state14 ^= var14;
               this.state19 ^= var14;
               this.state24 ^= var14;
               long var16 = this.state00;
               long var18 = this.state06 << 44 | this.state06 >>> 20;
               long var20 = this.state12 << 43 | this.state12 >>> 21;
               long var22 = this.state18 << 21 | this.state18 >>> 43;
               long var24 = this.state24 << 14 | this.state24 >>> 50;
               long var26 = this.state03 << 28 | this.state03 >>> 36;
               long var28 = this.state09 << 20 | this.state09 >>> 44;
               long var30 = this.state10 << 3 | this.state10 >>> 61;
               long var32 = this.state16 << 45 | this.state16 >>> 19;
               long var34 = this.state22 << 61 | this.state22 >>> 3;
               long var36 = this.state01 << 1 | this.state01 >>> 63;
               long var38 = this.state07 << 6 | this.state07 >>> 58;
               long var40 = this.state13 << 25 | this.state13 >>> 39;
               long var42 = this.state19 << 8 | this.state19 >>> 56;
               long var44 = this.state20 << 18 | this.state20 >>> 46;
               long var46 = this.state04 << 27 | this.state04 >>> 37;
               long var48 = this.state05 << 36 | this.state05 >>> 28;
               long var50 = this.state11 << 10 | this.state11 >>> 54;
               long var52 = this.state17 << 15 | this.state17 >>> 49;
               long var54 = this.state23 << 56 | this.state23 >>> 8;
               long var56 = this.state02 << 62 | this.state02 >>> 2;
               long var58 = this.state08 << 55 | this.state08 >>> 9;
               long var60 = this.state14 << 39 | this.state14 >>> 25;
               long var62 = this.state15 << 41 | this.state15 >>> 23;
               long var64 = this.state21 << 2 | this.state21 >>> 62;
               this.state00 = var16 ^ ~var18 & var20 ^ ROUND_CONSTANTS[var3];
               this.state01 = var18 ^ ~var20 & var22;
               this.state02 = var20 ^ ~var22 & var24;
               this.state03 = var22 ^ ~var24 & var16;
               this.state04 = var24 ^ ~var16 & var18;
               this.state05 = var26 ^ ~var28 & var30;
               this.state06 = var28 ^ ~var30 & var32;
               this.state07 = var30 ^ ~var32 & var34;
               this.state08 = var32 ^ ~var34 & var26;
               this.state09 = var34 ^ ~var26 & var28;
               this.state10 = var36 ^ ~var38 & var40;
               this.state11 = var38 ^ ~var40 & var42;
               this.state12 = var40 ^ ~var42 & var44;
               this.state13 = var42 ^ ~var44 & var36;
               this.state14 = var44 ^ ~var36 & var38;
               this.state15 = var46 ^ ~var48 & var50;
               this.state16 = var48 ^ ~var50 & var52;
               this.state17 = var50 ^ ~var52 & var54;
               this.state18 = var52 ^ ~var54 & var46;
               this.state19 = var54 ^ ~var46 & var48;
               this.state20 = var56 ^ ~var58 & var60;
               this.state21 = var58 ^ ~var60 & var62;
               this.state22 = var60 ^ ~var62 & var64;
               this.state23 = var62 ^ ~var64 & var56;
               this.state24 = var64 ^ ~var56 & var58;
            }

      }
   }

   static {
      long[] var0 = new long[24];
      byte var1 = 1;

      for(int var2 = 0; var2 < 24; ++var2) {
         var0[var2] = 0L;

         for(int var3 = 0; var3 < 7; ++var3) {
            byte var4 = var1;
            var1 = (byte)(var1 < 0 ? var1 << 1 ^ 113 : var1 << 1);
            if ((var4 & 1) != 0) {
               var0[var2] ^= 1L << (1 << var3) - 1;
            }
         }
      }

      ROUND_CONSTANTS = var0;
   }
}
