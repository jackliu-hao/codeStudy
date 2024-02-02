/*      */ package org.apache.commons.codec.digest;
/*      */ 
/*      */ import org.apache.commons.codec.binary.StringUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class MurmurHash3
/*      */ {
/*      */   @Deprecated
/*      */   public static final long NULL_HASHCODE = 2862933555777941757L;
/*      */   public static final int DEFAULT_SEED = 104729;
/*      */   static final int LONG_BYTES = 8;
/*      */   static final int INTEGER_BYTES = 4;
/*      */   static final int SHORT_BYTES = 2;
/*      */   private static final int C1_32 = -862048943;
/*      */   private static final int C2_32 = 461845907;
/*      */   private static final int R1_32 = 15;
/*      */   private static final int R2_32 = 13;
/*      */   private static final int M_32 = 5;
/*      */   private static final int N_32 = -430675100;
/*      */   private static final long C1 = -8663945395140668459L;
/*      */   private static final long C2 = 5545529020109919103L;
/*      */   private static final int R1 = 31;
/*      */   private static final int R2 = 27;
/*      */   private static final int R3 = 33;
/*      */   private static final int M = 5;
/*      */   private static final int N1 = 1390208809;
/*      */   private static final int N2 = 944331445;
/*      */   
/*      */   public static int hash32(long data1, long data2) {
/*  127 */     return hash32(data1, data2, 104729);
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
/*      */   public static int hash32(long data1, long data2, int seed) {
/*  149 */     int hash = seed;
/*  150 */     long r0 = Long.reverseBytes(data1);
/*  151 */     long r1 = Long.reverseBytes(data2);
/*      */     
/*  153 */     hash = mix32((int)r0, hash);
/*  154 */     hash = mix32((int)(r0 >>> 32L), hash);
/*  155 */     hash = mix32((int)r1, hash);
/*  156 */     hash = mix32((int)(r1 >>> 32L), hash);
/*      */     
/*  158 */     hash ^= 0x10;
/*  159 */     return fmix32(hash);
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
/*      */   public static int hash32(long data) {
/*  179 */     return hash32(data, 104729);
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
/*      */   public static int hash32(long data, int seed) {
/*  199 */     int hash = seed;
/*  200 */     long r0 = Long.reverseBytes(data);
/*      */     
/*  202 */     hash = mix32((int)r0, hash);
/*  203 */     hash = mix32((int)(r0 >>> 32L), hash);
/*      */     
/*  205 */     hash ^= 0x8;
/*  206 */     return fmix32(hash);
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
/*      */   @Deprecated
/*      */   public static int hash32(byte[] data) {
/*  230 */     return hash32(data, 0, data.length, 104729);
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
/*      */   @Deprecated
/*      */   public static int hash32(String data) {
/*  260 */     byte[] bytes = StringUtils.getBytesUtf8(data);
/*  261 */     return hash32(bytes, 0, bytes.length, 104729);
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
/*      */   @Deprecated
/*      */   public static int hash32(byte[] data, int length) {
/*  286 */     return hash32(data, length, 104729);
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
/*      */   @Deprecated
/*      */   public static int hash32(byte[] data, int length, int seed) {
/*  311 */     return hash32(data, 0, length, seed);
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
/*      */   @Deprecated
/*      */   public static int hash32(byte[] data, int offset, int length, int seed) {
/*  333 */     int hash = seed;
/*  334 */     int nblocks = length >> 2;
/*      */ 
/*      */     
/*  337 */     for (int i = 0; i < nblocks; i++) {
/*  338 */       int j = offset + (i << 2);
/*  339 */       int k = getLittleEndianInt(data, j);
/*  340 */       hash = mix32(k, hash);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  347 */     int index = offset + (nblocks << 2);
/*  348 */     int k1 = 0;
/*  349 */     switch (offset + length - index) {
/*      */       case 3:
/*  351 */         k1 ^= data[index + 2] << 16;
/*      */       case 2:
/*  353 */         k1 ^= data[index + 1] << 8;
/*      */       case 1:
/*  355 */         k1 ^= data[index];
/*      */ 
/*      */         
/*  358 */         k1 *= -862048943;
/*  359 */         k1 = Integer.rotateLeft(k1, 15);
/*  360 */         k1 *= 461845907;
/*  361 */         hash ^= k1;
/*      */         break;
/*      */     } 
/*  364 */     hash ^= length;
/*  365 */     return fmix32(hash);
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
/*      */   public static int hash32x86(byte[] data) {
/*  384 */     return hash32x86(data, 0, data.length, 0);
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
/*      */   public static int hash32x86(byte[] data, int offset, int length, int seed) {
/*  401 */     int hash = seed;
/*  402 */     int nblocks = length >> 2;
/*      */ 
/*      */     
/*  405 */     for (int i = 0; i < nblocks; i++) {
/*  406 */       int j = offset + (i << 2);
/*  407 */       int k = getLittleEndianInt(data, j);
/*  408 */       hash = mix32(k, hash);
/*      */     } 
/*      */ 
/*      */     
/*  412 */     int index = offset + (nblocks << 2);
/*  413 */     int k1 = 0;
/*  414 */     switch (offset + length - index) {
/*      */       case 3:
/*  416 */         k1 ^= (data[index + 2] & 0xFF) << 16;
/*      */       case 2:
/*  418 */         k1 ^= (data[index + 1] & 0xFF) << 8;
/*      */       case 1:
/*  420 */         k1 ^= data[index] & 0xFF;
/*      */ 
/*      */         
/*  423 */         k1 *= -862048943;
/*  424 */         k1 = Integer.rotateLeft(k1, 15);
/*  425 */         k1 *= 461845907;
/*  426 */         hash ^= k1;
/*      */         break;
/*      */     } 
/*  429 */     hash ^= length;
/*  430 */     return fmix32(hash);
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
/*      */   @Deprecated
/*      */   public static long hash64(long data) {
/*  464 */     long hash = 104729L;
/*  465 */     long k = Long.reverseBytes(data);
/*  466 */     int length = 8;
/*      */     
/*  468 */     k *= -8663945395140668459L;
/*  469 */     k = Long.rotateLeft(k, 31);
/*  470 */     k *= 5545529020109919103L;
/*  471 */     hash ^= k;
/*  472 */     hash = Long.rotateLeft(hash, 27) * 5L + 1390208809L;
/*      */     
/*  474 */     hash ^= 0x8L;
/*  475 */     hash = fmix64(hash);
/*  476 */     return hash;
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
/*      */   @Deprecated
/*      */   public static long hash64(int data) {
/*  510 */     long k1 = Integer.reverseBytes(data) & 0xFFFFFFFFL;
/*  511 */     int length = 4;
/*  512 */     long hash = 104729L;
/*  513 */     k1 *= -8663945395140668459L;
/*  514 */     k1 = Long.rotateLeft(k1, 31);
/*  515 */     k1 *= 5545529020109919103L;
/*  516 */     hash ^= k1;
/*      */     
/*  518 */     hash ^= 0x4L;
/*  519 */     hash = fmix64(hash);
/*  520 */     return hash;
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
/*      */   @Deprecated
/*      */   public static long hash64(short data) {
/*  554 */     long hash = 104729L;
/*  555 */     long k1 = 0L;
/*  556 */     k1 ^= (data & 0xFFL) << 8L;
/*  557 */     k1 ^= ((data & 0xFF00) >> 8) & 0xFFL;
/*  558 */     k1 *= -8663945395140668459L;
/*  559 */     k1 = Long.rotateLeft(k1, 31);
/*  560 */     k1 *= 5545529020109919103L;
/*  561 */     hash ^= k1;
/*      */ 
/*      */     
/*  564 */     hash ^= 0x2L;
/*  565 */     hash = fmix64(hash);
/*  566 */     return hash;
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
/*      */   @Deprecated
/*      */   public static long hash64(byte[] data) {
/*  598 */     return hash64(data, 0, data.length, 104729);
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
/*      */   @Deprecated
/*      */   public static long hash64(byte[] data, int offset, int length) {
/*  631 */     return hash64(data, offset, length, 104729);
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
/*      */   @Deprecated
/*      */   public static long hash64(byte[] data, int offset, int length, int seed) {
/*  667 */     long hash = seed;
/*  668 */     int nblocks = length >> 3;
/*      */ 
/*      */     
/*  671 */     for (int i = 0; i < nblocks; i++) {
/*  672 */       int j = offset + (i << 3);
/*  673 */       long k = getLittleEndianLong(data, j);
/*      */ 
/*      */       
/*  676 */       k *= -8663945395140668459L;
/*  677 */       k = Long.rotateLeft(k, 31);
/*  678 */       k *= 5545529020109919103L;
/*  679 */       hash ^= k;
/*  680 */       hash = Long.rotateLeft(hash, 27) * 5L + 1390208809L;
/*      */     } 
/*      */ 
/*      */     
/*  684 */     long k1 = 0L;
/*  685 */     int index = offset + (nblocks << 3);
/*  686 */     switch (offset + length - index) {
/*      */       case 7:
/*  688 */         k1 ^= (data[index + 6] & 0xFFL) << 48L;
/*      */       case 6:
/*  690 */         k1 ^= (data[index + 5] & 0xFFL) << 40L;
/*      */       case 5:
/*  692 */         k1 ^= (data[index + 4] & 0xFFL) << 32L;
/*      */       case 4:
/*  694 */         k1 ^= (data[index + 3] & 0xFFL) << 24L;
/*      */       case 3:
/*  696 */         k1 ^= (data[index + 2] & 0xFFL) << 16L;
/*      */       case 2:
/*  698 */         k1 ^= (data[index + 1] & 0xFFL) << 8L;
/*      */       case 1:
/*  700 */         k1 ^= data[index] & 0xFFL;
/*  701 */         k1 *= -8663945395140668459L;
/*  702 */         k1 = Long.rotateLeft(k1, 31);
/*  703 */         k1 *= 5545529020109919103L;
/*  704 */         hash ^= k1;
/*      */         break;
/*      */     } 
/*      */     
/*  708 */     hash ^= length;
/*  709 */     hash = fmix64(hash);
/*      */     
/*  711 */     return hash;
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
/*      */   public static long[] hash128(byte[] data) {
/*  732 */     return hash128(data, 0, data.length, 104729);
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
/*      */   public static long[] hash128x64(byte[] data) {
/*  751 */     return hash128x64(data, 0, data.length, 0);
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
/*      */   @Deprecated
/*      */   public static long[] hash128(String data) {
/*  780 */     byte[] bytes = StringUtils.getBytesUtf8(data);
/*  781 */     return hash128(bytes, 0, bytes.length, 104729);
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
/*      */   @Deprecated
/*      */   public static long[] hash128(byte[] data, int offset, int length, int seed) {
/*  805 */     return hash128x64(data, offset, length, seed);
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
/*      */   public static long[] hash128x64(byte[] data, int offset, int length, int seed) {
/*  823 */     return hash128x64(data, offset, length, seed & 0xFFFFFFFFL);
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
/*      */   private static long[] hash128x64(byte[] data, int offset, int length, long seed) {
/*  839 */     long h1 = seed;
/*  840 */     long h2 = seed;
/*  841 */     int nblocks = length >> 4;
/*      */ 
/*      */     
/*  844 */     for (int i = 0; i < nblocks; i++) {
/*  845 */       int j = offset + (i << 4);
/*  846 */       long l1 = getLittleEndianLong(data, j);
/*  847 */       long l2 = getLittleEndianLong(data, j + 8);
/*      */ 
/*      */       
/*  850 */       l1 *= -8663945395140668459L;
/*  851 */       l1 = Long.rotateLeft(l1, 31);
/*  852 */       l1 *= 5545529020109919103L;
/*  853 */       h1 ^= l1;
/*  854 */       h1 = Long.rotateLeft(h1, 27);
/*  855 */       h1 += h2;
/*  856 */       h1 = h1 * 5L + 1390208809L;
/*      */ 
/*      */       
/*  859 */       l2 *= 5545529020109919103L;
/*  860 */       l2 = Long.rotateLeft(l2, 33);
/*  861 */       l2 *= -8663945395140668459L;
/*  862 */       h2 ^= l2;
/*  863 */       h2 = Long.rotateLeft(h2, 31);
/*  864 */       h2 += h1;
/*  865 */       h2 = h2 * 5L + 944331445L;
/*      */     } 
/*      */ 
/*      */     
/*  869 */     long k1 = 0L;
/*  870 */     long k2 = 0L;
/*  871 */     int index = offset + (nblocks << 4);
/*  872 */     switch (offset + length - index) {
/*      */       case 15:
/*  874 */         k2 ^= (data[index + 14] & 0xFFL) << 48L;
/*      */       case 14:
/*  876 */         k2 ^= (data[index + 13] & 0xFFL) << 40L;
/*      */       case 13:
/*  878 */         k2 ^= (data[index + 12] & 0xFFL) << 32L;
/*      */       case 12:
/*  880 */         k2 ^= (data[index + 11] & 0xFFL) << 24L;
/*      */       case 11:
/*  882 */         k2 ^= (data[index + 10] & 0xFFL) << 16L;
/*      */       case 10:
/*  884 */         k2 ^= (data[index + 9] & 0xFFL) << 8L;
/*      */       case 9:
/*  886 */         k2 ^= (data[index + 8] & 0xFF);
/*  887 */         k2 *= 5545529020109919103L;
/*  888 */         k2 = Long.rotateLeft(k2, 33);
/*  889 */         k2 *= -8663945395140668459L;
/*  890 */         h2 ^= k2;
/*      */       
/*      */       case 8:
/*  893 */         k1 ^= (data[index + 7] & 0xFFL) << 56L;
/*      */       case 7:
/*  895 */         k1 ^= (data[index + 6] & 0xFFL) << 48L;
/*      */       case 6:
/*  897 */         k1 ^= (data[index + 5] & 0xFFL) << 40L;
/*      */       case 5:
/*  899 */         k1 ^= (data[index + 4] & 0xFFL) << 32L;
/*      */       case 4:
/*  901 */         k1 ^= (data[index + 3] & 0xFFL) << 24L;
/*      */       case 3:
/*  903 */         k1 ^= (data[index + 2] & 0xFFL) << 16L;
/*      */       case 2:
/*  905 */         k1 ^= (data[index + 1] & 0xFFL) << 8L;
/*      */       case 1:
/*  907 */         k1 ^= (data[index] & 0xFF);
/*  908 */         k1 *= -8663945395140668459L;
/*  909 */         k1 = Long.rotateLeft(k1, 31);
/*  910 */         k1 *= 5545529020109919103L;
/*  911 */         h1 ^= k1;
/*      */         break;
/*      */     } 
/*      */     
/*  915 */     h1 ^= length;
/*  916 */     h2 ^= length;
/*      */     
/*  918 */     h1 += h2;
/*  919 */     h2 += h1;
/*      */     
/*  921 */     h1 = fmix64(h1);
/*  922 */     h2 = fmix64(h2);
/*      */     
/*  924 */     h1 += h2;
/*  925 */     h2 += h1;
/*      */     
/*  927 */     return new long[] { h1, h2 };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static long getLittleEndianLong(byte[] data, int index) {
/*  938 */     return data[index] & 0xFFL | (data[index + 1] & 0xFFL) << 8L | (data[index + 2] & 0xFFL) << 16L | (data[index + 3] & 0xFFL) << 24L | (data[index + 4] & 0xFFL) << 32L | (data[index + 5] & 0xFFL) << 40L | (data[index + 6] & 0xFFL) << 48L | (data[index + 7] & 0xFFL) << 56L;
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
/*      */   private static int getLittleEndianInt(byte[] data, int index) {
/*  956 */     return data[index] & 0xFF | (data[index + 1] & 0xFF) << 8 | (data[index + 2] & 0xFF) << 16 | (data[index + 3] & 0xFF) << 24;
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
/*      */   private static int mix32(int k, int hash) {
/*  970 */     k *= -862048943;
/*  971 */     k = Integer.rotateLeft(k, 15);
/*  972 */     k *= 461845907;
/*  973 */     hash ^= k;
/*  974 */     return Integer.rotateLeft(hash, 13) * 5 + -430675100;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int fmix32(int hash) {
/*  984 */     hash ^= hash >>> 16;
/*  985 */     hash *= -2048144789;
/*  986 */     hash ^= hash >>> 13;
/*  987 */     hash *= -1028477387;
/*  988 */     hash ^= hash >>> 16;
/*  989 */     return hash;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static long fmix64(long hash) {
/*  999 */     hash ^= hash >>> 33L;
/* 1000 */     hash *= -49064778989728563L;
/* 1001 */     hash ^= hash >>> 33L;
/* 1002 */     hash *= -4265267296055464877L;
/* 1003 */     hash ^= hash >>> 33L;
/* 1004 */     return hash;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class IncrementalHash32x86
/*      */   {
/*      */     private static final int BLOCK_SIZE = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1022 */     private final byte[] unprocessed = new byte[3];
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int unprocessedLength;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int totalLen;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int hash;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final void start(int seed) {
/* 1043 */       this.unprocessedLength = this.totalLen = 0;
/* 1044 */       this.hash = seed;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final void add(byte[] data, int offset, int length) {
/*      */       int newOffset, newLength;
/* 1055 */       if (length <= 0) {
/*      */         return;
/*      */       }
/*      */       
/* 1059 */       this.totalLen += length;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1073 */       if (this.unprocessedLength + length - 4 < 0) {
/*      */         
/* 1075 */         System.arraycopy(data, offset, this.unprocessed, this.unprocessedLength, length);
/* 1076 */         this.unprocessedLength += length;
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*      */ 
/*      */       
/* 1083 */       if (this.unprocessedLength > 0) {
/* 1084 */         int k = -1;
/* 1085 */         switch (this.unprocessedLength) {
/*      */           case 1:
/* 1087 */             k = orBytes(this.unprocessed[0], data[offset], data[offset + 1], data[offset + 2]);
/*      */             break;
/*      */           case 2:
/* 1090 */             k = orBytes(this.unprocessed[0], this.unprocessed[1], data[offset], data[offset + 1]);
/*      */             break;
/*      */           case 3:
/* 1093 */             k = orBytes(this.unprocessed[0], this.unprocessed[1], this.unprocessed[2], data[offset]);
/*      */             break;
/*      */           default:
/* 1096 */             throw new IllegalStateException("Unprocessed length should be 1, 2, or 3: " + this.unprocessedLength);
/*      */         } 
/* 1098 */         this.hash = MurmurHash3.mix32(k, this.hash);
/*      */         
/* 1100 */         int j = 4 - this.unprocessedLength;
/* 1101 */         newOffset = offset + j;
/* 1102 */         newLength = length - j;
/*      */       } else {
/* 1104 */         newOffset = offset;
/* 1105 */         newLength = length;
/*      */       } 
/*      */ 
/*      */       
/* 1109 */       int nblocks = newLength >> 2;
/*      */       
/* 1111 */       for (int i = 0; i < nblocks; i++) {
/* 1112 */         int index = newOffset + (i << 2);
/* 1113 */         int k = MurmurHash3.getLittleEndianInt(data, index);
/* 1114 */         this.hash = MurmurHash3.mix32(k, this.hash);
/*      */       } 
/*      */ 
/*      */       
/* 1118 */       int consumed = nblocks << 2;
/* 1119 */       this.unprocessedLength = newLength - consumed;
/* 1120 */       if (this.unprocessedLength != 0) {
/* 1121 */         System.arraycopy(data, newOffset + consumed, this.unprocessed, 0, this.unprocessedLength);
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
/*      */     public final int end() {
/* 1133 */       return finalise(this.hash, this.unprocessedLength, this.unprocessed, this.totalLen);
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
/*      */     int finalise(int hash, int unprocessedLength, byte[] unprocessed, int totalLen) {
/* 1147 */       int result = hash;
/* 1148 */       int k1 = 0;
/* 1149 */       switch (unprocessedLength) {
/*      */         case 3:
/* 1151 */           k1 ^= (unprocessed[2] & 0xFF) << 16;
/*      */         case 2:
/* 1153 */           k1 ^= (unprocessed[1] & 0xFF) << 8;
/*      */         case 1:
/* 1155 */           k1 ^= unprocessed[0] & 0xFF;
/*      */ 
/*      */           
/* 1158 */           k1 *= -862048943;
/* 1159 */           k1 = Integer.rotateLeft(k1, 15);
/* 1160 */           k1 *= 461845907;
/* 1161 */           result ^= k1;
/*      */           break;
/*      */       } 
/*      */       
/* 1165 */       result ^= totalLen;
/* 1166 */       return MurmurHash3.fmix32(result);
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
/*      */     private static int orBytes(byte b1, byte b2, byte b3, byte b4) {
/* 1181 */       return b1 & 0xFF | (b2 & 0xFF) << 8 | (b3 & 0xFF) << 16 | (b4 & 0xFF) << 24;
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
/*      */   @Deprecated
/*      */   public static class IncrementalHash32
/*      */     extends IncrementalHash32x86
/*      */   {
/*      */     @Deprecated
/*      */     int finalise(int hash, int unprocessedLength, byte[] unprocessed, int totalLen) {
/* 1213 */       int result = hash;
/*      */ 
/*      */ 
/*      */       
/* 1217 */       int k1 = 0;
/* 1218 */       switch (unprocessedLength) {
/*      */         case 3:
/* 1220 */           k1 ^= unprocessed[2] << 16;
/*      */         case 2:
/* 1222 */           k1 ^= unprocessed[1] << 8;
/*      */         case 1:
/* 1224 */           k1 ^= unprocessed[0];
/*      */ 
/*      */           
/* 1227 */           k1 *= -862048943;
/* 1228 */           k1 = Integer.rotateLeft(k1, 15);
/* 1229 */           k1 *= 461845907;
/* 1230 */           result ^= k1;
/*      */           break;
/*      */       } 
/*      */       
/* 1234 */       result ^= totalLen;
/* 1235 */       return MurmurHash3.fmix32(result);
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\digest\MurmurHash3.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */