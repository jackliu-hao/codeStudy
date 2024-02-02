/*     */ package cn.hutool.core.io.unit;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public final class DataSize
/*     */   implements Comparable<DataSize>
/*     */ {
/*  31 */   private static final Pattern PATTERN = Pattern.compile("^([+-]?\\d+(\\.\\d+)?)([a-zA-Z]{0,2})$");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long BYTES_PER_KB = 1024L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long BYTES_PER_MB = 1048576L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long BYTES_PER_GB = 1073741824L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long BYTES_PER_TB = 1099511627776L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final long bytes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DataSize(long bytes) {
/*  66 */     this.bytes = bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DataSize ofBytes(long bytes) {
/*  77 */     return new DataSize(bytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DataSize ofKilobytes(long kilobytes) {
/*  87 */     return new DataSize(Math.multiplyExact(kilobytes, 1024L));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DataSize ofMegabytes(long megabytes) {
/*  97 */     return new DataSize(Math.multiplyExact(megabytes, 1048576L));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DataSize ofGigabytes(long gigabytes) {
/* 107 */     return new DataSize(Math.multiplyExact(gigabytes, 1073741824L));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DataSize ofTerabytes(long terabytes) {
/* 117 */     return new DataSize(Math.multiplyExact(terabytes, 1099511627776L));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DataSize of(long amount, DataUnit unit) {
/* 128 */     if (null == unit) {
/* 129 */       unit = DataUnit.BYTES;
/*     */     }
/* 131 */     return new DataSize(Math.multiplyExact(amount, unit.size().toBytes()));
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
/*     */   public static DataSize of(BigDecimal amount, DataUnit unit) {
/* 143 */     if (null == unit) {
/* 144 */       unit = DataUnit.BYTES;
/*     */     }
/* 146 */     return new DataSize(amount.multiply(new BigDecimal(unit.size().toBytes())).longValue());
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
/*     */   public static DataSize parse(CharSequence text) {
/* 164 */     return parse(text, null);
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
/*     */   public static DataSize parse(CharSequence text, DataUnit defaultUnit) {
/* 186 */     Assert.notNull(text, "Text must not be null", new Object[0]);
/*     */     try {
/* 188 */       Matcher matcher = PATTERN.matcher(text);
/* 189 */       Assert.state(matcher.matches(), "Does not match data size pattern", new Object[0]);
/*     */       
/* 191 */       DataUnit unit = determineDataUnit(matcher.group(3), defaultUnit);
/* 192 */       return of(new BigDecimal(matcher.group(1)), unit);
/* 193 */     } catch (Exception ex) {
/* 194 */       throw new IllegalArgumentException("'" + text + "' is not a valid data size", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static DataUnit determineDataUnit(String suffix, DataUnit defaultUnit) {
/* 205 */     DataUnit defaultUnitToUse = (defaultUnit != null) ? defaultUnit : DataUnit.BYTES;
/* 206 */     return StrUtil.isNotEmpty(suffix) ? DataUnit.fromSuffix(suffix) : defaultUnitToUse;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNegative() {
/* 215 */     return (this.bytes < 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long toBytes() {
/* 224 */     return this.bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long toKilobytes() {
/* 233 */     return this.bytes / 1024L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long toMegabytes() {
/* 242 */     return this.bytes / 1048576L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long toGigabytes() {
/* 252 */     return this.bytes / 1073741824L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long toTerabytes() {
/* 261 */     return this.bytes / 1099511627776L;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(DataSize other) {
/* 266 */     return Long.compare(this.bytes, other.bytes);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 271 */     return String.format("%dB", new Object[] { Long.valueOf(this.bytes) });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 277 */     if (this == other) {
/* 278 */       return true;
/*     */     }
/* 280 */     if (other == null || getClass() != other.getClass()) {
/* 281 */       return false;
/*     */     }
/* 283 */     DataSize otherSize = (DataSize)other;
/* 284 */     return (this.bytes == otherSize.bytes);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 289 */     return Long.hashCode(this.bytes);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\i\\unit\DataSize.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */