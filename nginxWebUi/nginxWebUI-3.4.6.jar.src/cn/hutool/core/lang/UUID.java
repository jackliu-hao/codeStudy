/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.util.RandomUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Serializable;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Random;
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
/*     */ public class UUID
/*     */   implements Serializable, Comparable<UUID>
/*     */ {
/*     */   private static final long serialVersionUID = -1185015143654744140L;
/*     */   private final long mostSigBits;
/*     */   private final long leastSigBits;
/*     */   
/*     */   private static class Holder
/*     */   {
/*  52 */     static final SecureRandom NUMBER_GENERATOR = RandomUtil.getSecureRandom();
/*     */   }
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
/*     */   private UUID(byte[] data) {
/*  71 */     long msb = 0L;
/*  72 */     long lsb = 0L;
/*  73 */     assert data.length == 16 : "data must be 16 bytes in length"; int i;
/*  74 */     for (i = 0; i < 8; i++) {
/*  75 */       msb = msb << 8L | (data[i] & 0xFF);
/*     */     }
/*  77 */     for (i = 8; i < 16; i++) {
/*  78 */       lsb = lsb << 8L | (data[i] & 0xFF);
/*     */     }
/*  80 */     this.mostSigBits = msb;
/*  81 */     this.leastSigBits = lsb;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UUID(long mostSigBits, long leastSigBits) {
/*  91 */     this.mostSigBits = mostSigBits;
/*  92 */     this.leastSigBits = leastSigBits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UUID fastUUID() {
/* 101 */     return randomUUID(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UUID randomUUID() {
/* 110 */     return randomUUID(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UUID randomUUID(boolean isSecure) {
/* 120 */     Random ng = isSecure ? Holder.NUMBER_GENERATOR : RandomUtil.getRandom();
/*     */     
/* 122 */     byte[] randomBytes = new byte[16];
/* 123 */     ng.nextBytes(randomBytes);
/*     */     
/* 125 */     randomBytes[6] = (byte)(randomBytes[6] & 0xF);
/* 126 */     randomBytes[6] = (byte)(randomBytes[6] | 0x40);
/* 127 */     randomBytes[8] = (byte)(randomBytes[8] & 0x3F);
/* 128 */     randomBytes[8] = (byte)(randomBytes[8] | 0x80);
/*     */     
/* 130 */     return new UUID(randomBytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UUID nameUUIDFromBytes(byte[] name) {
/*     */     MessageDigest md;
/*     */     try {
/* 142 */       md = MessageDigest.getInstance("MD5");
/* 143 */     } catch (NoSuchAlgorithmException nsae) {
/* 144 */       throw new InternalError("MD5 not supported");
/*     */     } 
/* 146 */     byte[] md5Bytes = md.digest(name);
/* 147 */     md5Bytes[6] = (byte)(md5Bytes[6] & 0xF);
/* 148 */     md5Bytes[6] = (byte)(md5Bytes[6] | 0x30);
/* 149 */     md5Bytes[8] = (byte)(md5Bytes[8] & 0x3F);
/* 150 */     md5Bytes[8] = (byte)(md5Bytes[8] | 0x80);
/* 151 */     return new UUID(md5Bytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UUID fromString(String name) {
/* 162 */     String[] components = name.split("-");
/* 163 */     if (components.length != 5) {
/* 164 */       throw new IllegalArgumentException("Invalid UUID string: " + name);
/*     */     }
/* 166 */     for (int i = 0; i < 5; i++) {
/* 167 */       components[i] = "0x" + components[i];
/*     */     }
/*     */     
/* 170 */     long mostSigBits = Long.decode(components[0]).longValue();
/* 171 */     mostSigBits <<= 16L;
/* 172 */     mostSigBits |= Long.decode(components[1]).longValue();
/* 173 */     mostSigBits <<= 16L;
/* 174 */     mostSigBits |= Long.decode(components[2]).longValue();
/*     */     
/* 176 */     long leastSigBits = Long.decode(components[3]).longValue();
/* 177 */     leastSigBits <<= 48L;
/* 178 */     leastSigBits |= Long.decode(components[4]).longValue();
/*     */     
/* 180 */     return new UUID(mostSigBits, leastSigBits);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getLeastSignificantBits() {
/* 189 */     return this.leastSigBits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMostSignificantBits() {
/* 198 */     return this.mostSigBits;
/*     */   }
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
/*     */   public int version() {
/* 216 */     return (int)(this.mostSigBits >> 12L & 0xFL);
/*     */   }
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
/*     */   public int variant() {
/* 238 */     return (int)(this.leastSigBits >>> (int)(64L - (this.leastSigBits >>> 62L)) & this.leastSigBits >> 63L);
/*     */   }
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
/*     */   public long timestamp() throws UnsupportedOperationException {
/* 256 */     checkTimeBase();
/* 257 */     return (this.mostSigBits & 0xFFFL) << 48L | (this.mostSigBits >> 16L & 0xFFFFL) << 32L | this.mostSigBits >>> 32L;
/*     */   }
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
/*     */   public int clockSequence() throws UnsupportedOperationException {
/* 274 */     checkTimeBase();
/* 275 */     return (int)((this.leastSigBits & 0x3FFF000000000000L) >>> 48L);
/*     */   }
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
/*     */   public long node() throws UnsupportedOperationException {
/* 291 */     checkTimeBase();
/* 292 */     return this.leastSigBits & 0xFFFFFFFFFFFFL;
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 321 */     return toString(false);
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString(boolean isSimple) {
/* 347 */     StringBuilder builder = StrUtil.builder(isSimple ? 32 : 36);
/*     */     
/* 349 */     builder.append(digits(this.mostSigBits >> 32L, 8));
/* 350 */     if (false == isSimple) {
/* 351 */       builder.append('-');
/*     */     }
/*     */     
/* 354 */     builder.append(digits(this.mostSigBits >> 16L, 4));
/* 355 */     if (false == isSimple) {
/* 356 */       builder.append('-');
/*     */     }
/*     */     
/* 359 */     builder.append(digits(this.mostSigBits, 4));
/* 360 */     if (false == isSimple) {
/* 361 */       builder.append('-');
/*     */     }
/*     */     
/* 364 */     builder.append(digits(this.leastSigBits >> 48L, 4));
/* 365 */     if (false == isSimple) {
/* 366 */       builder.append('-');
/*     */     }
/*     */     
/* 369 */     builder.append(digits(this.leastSigBits, 12));
/*     */     
/* 371 */     return builder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 381 */     long hilo = this.mostSigBits ^ this.leastSigBits;
/* 382 */     return (int)(hilo >> 32L) ^ (int)hilo;
/*     */   }
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
/*     */   public boolean equals(Object obj) {
/* 395 */     if (null == obj || obj.getClass() != UUID.class) {
/* 396 */       return false;
/*     */     }
/* 398 */     UUID id = (UUID)obj;
/* 399 */     return (this.mostSigBits == id.mostSigBits && this.leastSigBits == id.leastSigBits);
/*     */   }
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
/*     */   public int compareTo(UUID val) {
/* 417 */     int compare = Long.compare(this.mostSigBits, val.mostSigBits);
/* 418 */     if (0 == compare) {
/* 419 */       compare = Long.compare(this.leastSigBits, val.leastSigBits);
/*     */     }
/* 421 */     return compare;
/*     */   }
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
/*     */   private static String digits(long val, int digits) {
/* 434 */     long hi = 1L << digits * 4;
/* 435 */     return Long.toHexString(hi | val & hi - 1L).substring(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkTimeBase() {
/* 442 */     if (version() != 1)
/* 443 */       throw new UnsupportedOperationException("Not a time-based UUID"); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\UUID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */