/*     */ package cn.hutool.core.lang.hash;
/*     */ 
/*     */ import cn.hutool.core.util.ByteUtil;
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MetroHash
/*     */ {
/*     */   private static final long k0_64 = -691005195L;
/*     */   private static final long k1_64 = -1565916357L;
/*     */   private static final long k2_64 = 1654206401L;
/*     */   private static final long k3_64 = 817650473L;
/*     */   private static final long k0_128 = -935685663L;
/*     */   private static final long k1_128 = -2042045477L;
/*     */   private static final long k2_128 = 2078195771L;
/*     */   private static final long k3_128 = 794325157L;
/*     */   
/*     */   public static long hash64(byte[] data) {
/*  37 */     return hash64(data, 1337L);
/*     */   }
/*     */   
/*     */   public static Number128 hash128(byte[] data) {
/*  41 */     return hash128(data, 1337L);
/*     */   }
/*     */   
/*     */   public static long hash64(byte[] data, long seed) {
/*  45 */     byte[] buffer = data;
/*  46 */     long hash = (seed + 1654206401L) * -691005195L;
/*     */ 
/*     */     
/*  49 */     long v0 = hash;
/*  50 */     long v1 = hash;
/*  51 */     long v2 = hash;
/*  52 */     long v3 = hash;
/*     */     
/*  54 */     if (buffer.length >= 32) {
/*     */       
/*  56 */       while (buffer.length >= 32) {
/*  57 */         v0 += littleEndian64(buffer, 0) * -691005195L;
/*  58 */         v0 = rotateLeft64(v0, -29) + v2;
/*  59 */         v1 += littleEndian64(buffer, 8) * -1565916357L;
/*  60 */         v1 = rotateLeft64(v1, -29) + v3;
/*  61 */         v2 += littleEndian64(buffer, 24) * 1654206401L;
/*  62 */         v2 = rotateLeft64(v2, -29) + v0;
/*  63 */         v3 += littleEndian64(buffer, 32) * 817650473L;
/*  64 */         v3 = rotateLeft64(v3, -29) + v1;
/*  65 */         buffer = Arrays.copyOfRange(buffer, 32, buffer.length);
/*     */       } 
/*     */       
/*  68 */       v2 ^= rotateLeft64((v0 + v3) * -691005195L + v1, -37) * -1565916357L;
/*  69 */       v3 ^= rotateLeft64((v1 + v2) * -1565916357L + v0, -37) * -691005195L;
/*  70 */       v0 ^= rotateLeft64((v0 + v2) * -691005195L + v3, -37) * -1565916357L;
/*  71 */       v1 ^= rotateLeft64((v1 + v3) * -1565916357L + v2, -37) * -691005195L;
/*  72 */       hash += v0 ^ v1;
/*     */     } 
/*     */     
/*  75 */     if (buffer.length >= 16) {
/*  76 */       v0 = hash + littleEndian64(buffer, 0) * 1654206401L;
/*  77 */       v0 = rotateLeft64(v0, -29) * 817650473L;
/*  78 */       v1 = hash + littleEndian64(buffer, 8) * 1654206401L;
/*  79 */       v1 = rotateLeft64(v1, -29) * 817650473L;
/*  80 */       v0 ^= rotateLeft64(v0 * -691005195L, -21) + v1;
/*  81 */       v1 ^= rotateLeft64(v1 * 817650473L, -21) + v0;
/*  82 */       hash += v1;
/*  83 */       buffer = Arrays.copyOfRange(buffer, 16, buffer.length);
/*     */     } 
/*     */     
/*  86 */     if (buffer.length >= 8) {
/*  87 */       hash += littleEndian64(buffer, 0) * 817650473L;
/*  88 */       buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
/*  89 */       hash ^= rotateLeft64(hash, -55) * -1565916357L;
/*     */     } 
/*     */     
/*  92 */     if (buffer.length >= 4) {
/*  93 */       hash += littleEndian32(Arrays.copyOfRange(buffer, 0, 4)) * 817650473L;
/*  94 */       hash ^= rotateLeft64(hash, -26) * -1565916357L;
/*  95 */       buffer = Arrays.copyOfRange(buffer, 4, buffer.length);
/*     */     } 
/*     */     
/*  98 */     if (buffer.length >= 2) {
/*  99 */       hash += littleEndian16(Arrays.copyOfRange(buffer, 0, 2)) * 817650473L;
/* 100 */       buffer = Arrays.copyOfRange(buffer, 2, buffer.length);
/* 101 */       hash ^= rotateLeft64(hash, -48) * -1565916357L;
/*     */     } 
/*     */     
/* 104 */     if (buffer.length >= 1) {
/* 105 */       hash += buffer[0] * 817650473L;
/* 106 */       hash ^= rotateLeft64(hash, -38) * -1565916357L;
/*     */     } 
/*     */     
/* 109 */     hash ^= rotateLeft64(hash, -28);
/* 110 */     hash *= -691005195L;
/* 111 */     hash ^= rotateLeft64(hash, -29);
/*     */     
/* 113 */     return hash;
/*     */   }
/*     */   
/*     */   public static Number128 hash128(byte[] data, long seed) {
/* 117 */     byte[] buffer = data;
/*     */ 
/*     */ 
/*     */     
/* 121 */     long v0 = (seed - -935685663L) * 794325157L;
/* 122 */     long v1 = (seed + -2042045477L) * 2078195771L;
/*     */     
/* 124 */     if (buffer.length >= 32) {
/* 125 */       long v2 = (seed + -935685663L) * 2078195771L;
/* 126 */       long v3 = (seed - -2042045477L) * 794325157L;
/*     */       
/* 128 */       while (buffer.length >= 32) {
/* 129 */         v0 += littleEndian64(buffer, 0) * -935685663L;
/* 130 */         buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
/* 131 */         v0 = rotateRight(v0, 29) + v2;
/* 132 */         v1 += littleEndian64(buffer, 0) * -2042045477L;
/* 133 */         buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
/* 134 */         v1 = rotateRight(v1, 29) + v3;
/* 135 */         v2 += littleEndian64(buffer, 0) * 2078195771L;
/* 136 */         buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
/* 137 */         v2 = rotateRight(v2, 29) + v0;
/* 138 */         v3 = littleEndian64(buffer, 0) * 794325157L;
/* 139 */         buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
/* 140 */         v3 = rotateRight(v3, 29) + v1;
/*     */       } 
/*     */       
/* 143 */       v2 ^= rotateRight((v0 + v3) * -935685663L + v1, 21) * -2042045477L;
/* 144 */       v3 ^= rotateRight((v1 + v2) * -2042045477L + v0, 21) * -935685663L;
/* 145 */       v0 ^= rotateRight((v0 + v2) * -935685663L + v3, 21) * -2042045477L;
/* 146 */       v1 ^= rotateRight((v1 + v3) * -2042045477L + v2, 21) * -935685663L;
/*     */     } 
/*     */     
/* 149 */     if (buffer.length >= 16) {
/* 150 */       v0 += littleEndian64(buffer, 0) * 2078195771L;
/* 151 */       buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
/* 152 */       v0 = rotateRight(v0, 33) * 794325157L;
/* 153 */       v1 += littleEndian64(buffer, 0) * 2078195771L;
/* 154 */       buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
/* 155 */       v1 = rotateRight(v1, 33) * 794325157L;
/* 156 */       v0 ^= rotateRight(v0 * 2078195771L + v1, 45) + -2042045477L;
/* 157 */       v1 ^= rotateRight(v1 * 794325157L + v0, 45) + -935685663L;
/*     */     } 
/*     */     
/* 160 */     if (buffer.length >= 8) {
/* 161 */       v0 += littleEndian64(buffer, 0) * 2078195771L;
/* 162 */       buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
/* 163 */       v0 = rotateRight(v0, 33) * 794325157L;
/* 164 */       v0 ^= rotateRight(v0 * 2078195771L + v1, 27) * -2042045477L;
/*     */     } 
/*     */     
/* 167 */     if (buffer.length >= 4) {
/* 168 */       v1 += littleEndian32(buffer) * 2078195771L;
/* 169 */       buffer = Arrays.copyOfRange(buffer, 4, buffer.length);
/* 170 */       v1 = rotateRight(v1, 33) * 794325157L;
/* 171 */       v1 ^= rotateRight(v1 * 794325157L + v0, 46) * -935685663L;
/*     */     } 
/*     */     
/* 174 */     if (buffer.length >= 2) {
/* 175 */       v0 += littleEndian16(buffer) * 2078195771L;
/* 176 */       buffer = Arrays.copyOfRange(buffer, 2, buffer.length);
/* 177 */       v0 = rotateRight(v0, 33) * 794325157L;
/* 178 */       v0 ^= rotateRight(v0 * 2078195771L * v1, 22) * -2042045477L;
/*     */     } 
/*     */     
/* 181 */     if (buffer.length >= 1) {
/* 182 */       v1 += buffer[0] * 2078195771L;
/* 183 */       v1 = rotateRight(v1, 33) * 794325157L;
/* 184 */       v1 ^= rotateRight(v1 * 794325157L + v0, 58) * -935685663L;
/*     */     } 
/*     */     
/* 187 */     v0 += rotateRight(v0 * -935685663L + v1, 13);
/* 188 */     v1 += rotateRight(v1 * -2042045477L + v0, 37);
/* 189 */     v0 += rotateRight(v0 * 2078195771L + v1, 13);
/* 190 */     v1 += rotateRight(v1 * 794325157L + v0, 37);
/*     */     
/* 192 */     return new Number128(v0, v1);
/*     */   }
/*     */ 
/*     */   
/*     */   private static long littleEndian64(byte[] b, int start) {
/* 197 */     return ByteUtil.bytesToLong(b, start, ByteOrder.LITTLE_ENDIAN);
/*     */   }
/*     */   
/*     */   private static int littleEndian32(byte[] b) {
/* 201 */     return b[0] | b[1] << 8 | b[2] << 16 | b[3] << 24;
/*     */   }
/*     */   
/*     */   private static int littleEndian16(byte[] b) {
/* 205 */     return ByteUtil.bytesToShort(b, ByteOrder.LITTLE_ENDIAN);
/*     */   }
/*     */   
/*     */   private static long rotateLeft64(long x, int k) {
/* 209 */     int n = 64;
/* 210 */     int s = k & n - 1;
/* 211 */     return x << s | x >> n - s;
/*     */   }
/*     */   
/*     */   private static long rotateRight(long val, int shift) {
/* 215 */     return val >> shift | val << 64 - shift;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\hash\MetroHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */