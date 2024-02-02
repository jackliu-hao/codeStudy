/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import org.h2.util.StringUtils;
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
/*     */ public class MultiDimension
/*     */   implements Comparator<long[]>
/*     */ {
/*  22 */   private static final MultiDimension INSTANCE = new MultiDimension();
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
/*     */   public static MultiDimension getInstance() {
/*  35 */     return INSTANCE;
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
/*     */   public int normalize(int paramInt, double paramDouble1, double paramDouble2, double paramDouble3) {
/*  49 */     if (paramDouble1 < paramDouble2 || paramDouble1 > paramDouble3) {
/*  50 */       throw new IllegalArgumentException(paramDouble2 + "<" + paramDouble1 + "<" + paramDouble3);
/*     */     }
/*  52 */     double d = (paramDouble1 - paramDouble2) / (paramDouble3 - paramDouble2);
/*  53 */     return (int)(d * getMaxValue(paramInt));
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
/*     */   public int getMaxValue(int paramInt) {
/*  65 */     if (paramInt < 2 || paramInt > 32) {
/*  66 */       throw new IllegalArgumentException(Integer.toString(paramInt));
/*     */     }
/*  68 */     int i = getBitsPerValue(paramInt);
/*  69 */     return (int)((1L << i) - 1L);
/*     */   }
/*     */   
/*     */   private static int getBitsPerValue(int paramInt) {
/*  73 */     return Math.min(31, 64 / paramInt);
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
/*     */   public long interleave(int... paramVarArgs) {
/*  87 */     int i = paramVarArgs.length;
/*  88 */     long l1 = getMaxValue(i);
/*  89 */     int j = getBitsPerValue(i);
/*  90 */     long l2 = 0L;
/*  91 */     for (byte b = 0; b < i; b++) {
/*  92 */       long l = paramVarArgs[b];
/*  93 */       if (l < 0L || l > l1) {
/*  94 */         throw new IllegalArgumentException("0<" + l + "<" + l1);
/*     */       }
/*  96 */       for (byte b1 = 0; b1 < j; b1++) {
/*  97 */         l2 |= (l & 1L << b1) << b + (i - 1) * b1;
/*     */       }
/*     */     } 
/* 100 */     return l2;
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
/*     */   public long interleave(int paramInt1, int paramInt2) {
/* 115 */     if (paramInt1 < 0) {
/* 116 */       throw new IllegalArgumentException("0<" + paramInt1);
/*     */     }
/* 118 */     if (paramInt2 < 0) {
/* 119 */       throw new IllegalArgumentException("0<" + paramInt2);
/*     */     }
/* 121 */     long l = 0L;
/* 122 */     for (byte b = 0; b < 32; b++) {
/* 123 */       l |= (paramInt1 & 1L << b) << b;
/* 124 */       l |= (paramInt2 & 1L << b) << b + 1;
/*     */     } 
/* 126 */     return l;
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
/*     */   public int deinterleave(int paramInt1, long paramLong, int paramInt2) {
/* 138 */     int i = getBitsPerValue(paramInt1);
/* 139 */     int j = 0;
/* 140 */     for (byte b = 0; b < i; b++) {
/* 141 */       j = (int)(j | paramLong >> paramInt2 + (paramInt1 - 1) * b & 1L << b);
/*     */     }
/* 143 */     return j;
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
/*     */   public String generatePreparedQuery(String paramString1, String paramString2, String[] paramArrayOfString) {
/* 157 */     StringBuilder stringBuilder = new StringBuilder("SELECT D.* FROM ");
/* 158 */     StringUtils.quoteIdentifier(stringBuilder, paramString1)
/* 159 */       .append(" D, TABLE(_FROM_ BIGINT=?, _TO_ BIGINT=?) WHERE ");
/* 160 */     StringUtils.quoteIdentifier(stringBuilder, paramString2)
/* 161 */       .append(" BETWEEN _FROM_ AND _TO_");
/* 162 */     for (String str : paramArrayOfString) {
/* 163 */       stringBuilder.append(" AND ");
/* 164 */       StringUtils.quoteIdentifier(stringBuilder, str)
/* 165 */         .append("+1 BETWEEN ?+1 AND ?+1");
/*     */     } 
/* 167 */     return stringBuilder.toString();
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
/*     */   public ResultSet getResult(PreparedStatement paramPreparedStatement, int[] paramArrayOfint1, int[] paramArrayOfint2) throws SQLException {
/* 181 */     long[][] arrayOfLong = getMortonRanges(paramArrayOfint1, paramArrayOfint2);
/* 182 */     int i = arrayOfLong.length;
/* 183 */     Long[] arrayOfLong1 = new Long[i];
/* 184 */     Long[] arrayOfLong2 = new Long[i]; byte b1;
/* 185 */     for (b1 = 0; b1 < i; b1++) {
/* 186 */       arrayOfLong1[b1] = Long.valueOf(arrayOfLong[b1][0]);
/* 187 */       arrayOfLong2[b1] = Long.valueOf(arrayOfLong[b1][1]);
/*     */     } 
/* 189 */     paramPreparedStatement.setObject(1, arrayOfLong1);
/* 190 */     paramPreparedStatement.setObject(2, arrayOfLong2);
/* 191 */     i = paramArrayOfint1.length; byte b2;
/* 192 */     for (b1 = 0, b2 = 3; b1 < i; b1++) {
/* 193 */       paramPreparedStatement.setInt(b2++, paramArrayOfint1[b1]);
/* 194 */       paramPreparedStatement.setInt(b2++, paramArrayOfint2[b1]);
/*     */     } 
/* 196 */     return paramPreparedStatement.executeQuery();
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
/*     */   private long[][] getMortonRanges(int[] paramArrayOfint1, int[] paramArrayOfint2) {
/* 210 */     int i = paramArrayOfint1.length;
/* 211 */     if (paramArrayOfint2.length != i)
/* 212 */       throw new IllegalArgumentException(i + "=" + paramArrayOfint2.length); 
/*     */     int j;
/* 214 */     for (j = 0; j < i; j++) {
/* 215 */       if (paramArrayOfint1[j] > paramArrayOfint2[j]) {
/* 216 */         int k = paramArrayOfint1[j];
/* 217 */         paramArrayOfint1[j] = paramArrayOfint2[j];
/* 218 */         paramArrayOfint2[j] = k;
/*     */       } 
/*     */     } 
/* 221 */     j = getSize(paramArrayOfint1, paramArrayOfint2, i);
/* 222 */     ArrayList<long[]> arrayList = new ArrayList();
/* 223 */     addMortonRanges(arrayList, paramArrayOfint1, paramArrayOfint2, i, 0);
/* 224 */     combineEntries(arrayList, j);
/* 225 */     return arrayList.<long[]>toArray(new long[0][]);
/*     */   }
/*     */   
/*     */   private static int getSize(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt) {
/* 229 */     int i = 1;
/* 230 */     for (byte b = 0; b < paramInt; b++) {
/* 231 */       int j = paramArrayOfint2[b] - paramArrayOfint1[b];
/* 232 */       i *= j + 1;
/*     */     } 
/* 234 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void combineEntries(ArrayList<long[]> paramArrayList, int paramInt) {
/* 244 */     paramArrayList.sort(this);
/* 245 */     for (int i = 10; i < paramInt; i += i / 2) {
/* 246 */       int j; for (j = 0; j < paramArrayList.size() - 1; j++) {
/* 247 */         long[] arrayOfLong1 = paramArrayList.get(j);
/* 248 */         long[] arrayOfLong2 = paramArrayList.get(j + 1);
/* 249 */         if (arrayOfLong1[1] + i >= arrayOfLong2[0]) {
/* 250 */           arrayOfLong1[1] = arrayOfLong2[1];
/* 251 */           paramArrayList.remove(j + 1);
/* 252 */           j--;
/*     */         } 
/*     */       } 
/* 255 */       j = 0;
/* 256 */       for (long[] arrayOfLong : paramArrayList) {
/* 257 */         j = (int)(j + arrayOfLong[1] - arrayOfLong[0] + 1L);
/*     */       }
/* 259 */       if (j > 2 * paramInt || paramArrayList.size() < 100) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int compare(long[] paramArrayOflong1, long[] paramArrayOflong2) {
/* 267 */     return (paramArrayOflong1[0] > paramArrayOflong2[0]) ? 1 : -1;
/*     */   }
/*     */ 
/*     */   
/*     */   private void addMortonRanges(ArrayList<long[]> paramArrayList, int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt1, int paramInt2) {
/* 272 */     if (paramInt2 > 100) {
/* 273 */       throw new IllegalArgumentException(Integer.toString(paramInt2));
/*     */     }
/* 275 */     byte b1 = 0; int i = 0;
/* 276 */     long l1 = 1L;
/* 277 */     for (byte b2 = 0; b2 < paramInt1; b2++) {
/* 278 */       int j = paramArrayOfint2[b2] - paramArrayOfint1[b2];
/* 279 */       if (j < 0) {
/* 280 */         throw new IllegalArgumentException(Integer.toString(j));
/*     */       }
/* 282 */       l1 *= (j + 1);
/* 283 */       if (l1 < 0L) {
/* 284 */         throw new IllegalArgumentException(Long.toString(l1));
/*     */       }
/* 286 */       if (j > i) {
/* 287 */         i = j;
/* 288 */         b1 = b2;
/*     */       } 
/*     */     } 
/* 291 */     long l2 = interleave(paramArrayOfint1), l3 = interleave(paramArrayOfint2);
/* 292 */     if (l3 < l2) {
/* 293 */       throw new IllegalArgumentException(l3 + "<" + l2);
/*     */     }
/* 295 */     long l4 = l3 - l2 + 1L;
/* 296 */     if (l4 == l1) {
/* 297 */       long[] arrayOfLong = { l2, l3 };
/* 298 */       paramArrayList.add(arrayOfLong);
/*     */     } else {
/* 300 */       int j = findMiddle(paramArrayOfint1[b1], paramArrayOfint2[b1]);
/* 301 */       int k = paramArrayOfint2[b1];
/* 302 */       paramArrayOfint2[b1] = j;
/* 303 */       addMortonRanges(paramArrayList, paramArrayOfint1, paramArrayOfint2, paramInt1, paramInt2 + 1);
/* 304 */       paramArrayOfint2[b1] = k;
/* 305 */       k = paramArrayOfint1[b1];
/* 306 */       paramArrayOfint1[b1] = j + 1;
/* 307 */       addMortonRanges(paramArrayList, paramArrayOfint1, paramArrayOfint2, paramInt1, paramInt2 + 1);
/* 308 */       paramArrayOfint1[b1] = k;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static int roundUp(int paramInt1, int paramInt2) {
/* 313 */     return paramInt1 + paramInt2 - 1 & -paramInt2;
/*     */   }
/*     */   
/*     */   private static int findMiddle(int paramInt1, int paramInt2) {
/* 317 */     int i = paramInt2 - paramInt1 - 1;
/* 318 */     if (i == 0) {
/* 319 */       return paramInt1;
/*     */     }
/* 321 */     if (i == 1) {
/* 322 */       return paramInt1 + 1;
/*     */     }
/* 324 */     byte b = 0;
/* 325 */     while (1 << b < i) {
/* 326 */       b++;
/*     */     }
/* 328 */     b--;
/* 329 */     int j = roundUp(paramInt1 + 2, 1 << b) - 1;
/* 330 */     if (j <= paramInt1 || j >= paramInt2) {
/* 331 */       throw new IllegalArgumentException(paramInt1 + "<" + j + "<" + paramInt2);
/*     */     }
/* 333 */     return j;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\MultiDimension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */