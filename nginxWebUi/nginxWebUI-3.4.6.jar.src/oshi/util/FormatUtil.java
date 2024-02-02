/*     */ package oshi.util;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class FormatUtil
/*     */ {
/*     */   private static final long KIBI = 1024L;
/*     */   private static final long MEBI = 1048576L;
/*     */   private static final long GIBI = 1073741824L;
/*     */   private static final long TEBI = 1099511627776L;
/*     */   private static final long PEBI = 1125899906842624L;
/*     */   private static final long EXBI = 1152921504606846976L;
/*     */   private static final long KILO = 1000L;
/*     */   private static final long MEGA = 1000000L;
/*     */   private static final long GIGA = 1000000000L;
/*     */   private static final long TERA = 1000000000000L;
/*     */   private static final long PETA = 1000000000000000L;
/*     */   private static final long EXA = 1000000000000000000L;
/*  65 */   private static final BigInteger TWOS_COMPLEMENT_REF = BigInteger.ONE.shiftLeft(64);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String HEX_ERROR = "0x%08X";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String formatBytes(long bytes) {
/*  85 */     if (bytes == 1L)
/*  86 */       return String.format("%d byte", new Object[] { Long.valueOf(bytes) }); 
/*  87 */     if (bytes < 1024L)
/*  88 */       return String.format("%d bytes", new Object[] { Long.valueOf(bytes) }); 
/*  89 */     if (bytes < 1048576L)
/*  90 */       return formatUnits(bytes, 1024L, "KiB"); 
/*  91 */     if (bytes < 1073741824L)
/*  92 */       return formatUnits(bytes, 1048576L, "MiB"); 
/*  93 */     if (bytes < 1099511627776L)
/*  94 */       return formatUnits(bytes, 1073741824L, "GiB"); 
/*  95 */     if (bytes < 1125899906842624L)
/*  96 */       return formatUnits(bytes, 1099511627776L, "TiB"); 
/*  97 */     if (bytes < 1152921504606846976L) {
/*  98 */       return formatUnits(bytes, 1125899906842624L, "PiB");
/*     */     }
/* 100 */     return formatUnits(bytes, 1152921504606846976L, "EiB");
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
/*     */   private static String formatUnits(long value, long prefix, String unit) {
/* 117 */     if (value % prefix == 0L) {
/* 118 */       return String.format("%d %s", new Object[] { Long.valueOf(value / prefix), unit });
/*     */     }
/* 120 */     return String.format("%.1f %s", new Object[] { Double.valueOf(value / prefix), unit });
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
/*     */   public static String formatBytesDecimal(long bytes) {
/* 133 */     if (bytes == 1L)
/* 134 */       return String.format("%d byte", new Object[] { Long.valueOf(bytes) }); 
/* 135 */     if (bytes < 1000L) {
/* 136 */       return String.format("%d bytes", new Object[] { Long.valueOf(bytes) });
/*     */     }
/* 138 */     return formatValue(bytes, "B");
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
/*     */   public static String formatHertz(long hertz) {
/* 150 */     return formatValue(hertz, "Hz");
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
/*     */   public static String formatValue(long value, String unit) {
/* 164 */     if (value < 1000L)
/* 165 */       return String.format("%d %s", new Object[] { Long.valueOf(value), unit }).trim(); 
/* 166 */     if (value < 1000000L)
/* 167 */       return formatUnits(value, 1000L, "K" + unit); 
/* 168 */     if (value < 1000000000L)
/* 169 */       return formatUnits(value, 1000000L, "M" + unit); 
/* 170 */     if (value < 1000000000000L)
/* 171 */       return formatUnits(value, 1000000000L, "G" + unit); 
/* 172 */     if (value < 1000000000000000L)
/* 173 */       return formatUnits(value, 1000000000000L, "T" + unit); 
/* 174 */     if (value < 1000000000000000000L) {
/* 175 */       return formatUnits(value, 1000000000000000L, "P" + unit);
/*     */     }
/* 177 */     return formatUnits(value, 1000000000000000000L, "E" + unit);
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
/*     */   public static String formatElapsedSecs(long secs) {
/* 189 */     long eTime = secs;
/* 190 */     long days = TimeUnit.SECONDS.toDays(eTime);
/* 191 */     eTime -= TimeUnit.DAYS.toSeconds(days);
/* 192 */     long hr = TimeUnit.SECONDS.toHours(eTime);
/* 193 */     eTime -= TimeUnit.HOURS.toSeconds(hr);
/* 194 */     long min = TimeUnit.SECONDS.toMinutes(eTime);
/* 195 */     eTime -= TimeUnit.MINUTES.toSeconds(min);
/* 196 */     long sec = eTime;
/* 197 */     return String.format("%d days, %02d:%02d:%02d", new Object[] { Long.valueOf(days), Long.valueOf(hr), Long.valueOf(min), Long.valueOf(sec) });
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
/*     */   public static float round(float d, int decimalPlace) {
/* 210 */     BigDecimal bd = (new BigDecimal(Float.toString(d))).setScale(decimalPlace, RoundingMode.HALF_UP);
/* 211 */     return bd.floatValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getUnsignedInt(int x) {
/* 222 */     return x & 0xFFFFFFFFL;
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
/*     */   public static String toUnsignedString(int i) {
/* 235 */     if (i >= 0) {
/* 236 */       return Integer.toString(i);
/*     */     }
/* 238 */     return Long.toString(getUnsignedInt(i));
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
/*     */   public static String toUnsignedString(long l) {
/* 251 */     if (l >= 0L) {
/* 252 */       return Long.toString(l);
/*     */     }
/* 254 */     return BigInteger.valueOf(l).add(TWOS_COMPLEMENT_REF).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String formatError(int errorCode) {
/* 265 */     return String.format("0x%08X", new Object[] { Integer.valueOf(errorCode) });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\FormatUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */