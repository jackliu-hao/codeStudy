/*     */ package org.slf4j.helpers;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public final class MessageFormatter
/*     */ {
/*     */   static final char DELIM_START = '{';
/*     */   static final char DELIM_STOP = '}';
/*     */   static final String DELIM_STR = "{}";
/*     */   private static final char ESCAPE_CHAR = '\\';
/*     */   
/*     */   public static final FormattingTuple format(String messagePattern, Object arg) {
/* 124 */     return arrayFormat(messagePattern, new Object[] { arg });
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
/*     */   public static final FormattingTuple format(String messagePattern, Object arg1, Object arg2) {
/* 151 */     return arrayFormat(messagePattern, new Object[] { arg1, arg2 });
/*     */   }
/*     */ 
/*     */   
/*     */   public static final FormattingTuple arrayFormat(String messagePattern, Object[] argArray) {
/* 156 */     Throwable throwableCandidate = getThrowableCandidate(argArray);
/* 157 */     Object[] args = argArray;
/* 158 */     if (throwableCandidate != null) {
/* 159 */       args = trimmedCopy(argArray);
/*     */     }
/* 161 */     return arrayFormat(messagePattern, args, throwableCandidate);
/*     */   }
/*     */ 
/*     */   
/*     */   public static final FormattingTuple arrayFormat(String messagePattern, Object[] argArray, Throwable throwable) {
/* 166 */     if (messagePattern == null) {
/* 167 */       return new FormattingTuple(null, argArray, throwable);
/*     */     }
/*     */     
/* 170 */     if (argArray == null) {
/* 171 */       return new FormattingTuple(messagePattern);
/*     */     }
/*     */     
/* 174 */     int i = 0;
/*     */ 
/*     */     
/* 177 */     StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);
/*     */ 
/*     */     
/* 180 */     for (int L = 0; L < argArray.length; L++) {
/*     */       
/* 182 */       int j = messagePattern.indexOf("{}", i);
/*     */       
/* 184 */       if (j == -1) {
/*     */         
/* 186 */         if (i == 0) {
/* 187 */           return new FormattingTuple(messagePattern, argArray, throwable);
/*     */         }
/*     */         
/* 190 */         sbuf.append(messagePattern, i, messagePattern.length());
/* 191 */         return new FormattingTuple(sbuf.toString(), argArray, throwable);
/*     */       } 
/*     */       
/* 194 */       if (isEscapedDelimeter(messagePattern, j)) {
/* 195 */         if (!isDoubleEscaped(messagePattern, j)) {
/* 196 */           L--;
/* 197 */           sbuf.append(messagePattern, i, j - 1);
/* 198 */           sbuf.append('{');
/* 199 */           i = j + 1;
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 204 */           sbuf.append(messagePattern, i, j - 1);
/* 205 */           deeplyAppendParameter(sbuf, argArray[L], (Map)new HashMap<Object, Object>());
/* 206 */           i = j + 2;
/*     */         } 
/*     */       } else {
/*     */         
/* 210 */         sbuf.append(messagePattern, i, j);
/* 211 */         deeplyAppendParameter(sbuf, argArray[L], (Map)new HashMap<Object, Object>());
/* 212 */         i = j + 2;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 217 */     sbuf.append(messagePattern, i, messagePattern.length());
/* 218 */     return new FormattingTuple(sbuf.toString(), argArray, throwable);
/*     */   }
/*     */ 
/*     */   
/*     */   static final boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {
/* 223 */     if (delimeterStartIndex == 0) {
/* 224 */       return false;
/*     */     }
/* 226 */     char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
/* 227 */     if (potentialEscape == '\\') {
/* 228 */       return true;
/*     */     }
/* 230 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   static final boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
/* 235 */     if (delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == '\\') {
/* 236 */       return true;
/*     */     }
/* 238 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void deeplyAppendParameter(StringBuilder sbuf, Object o, Map<Object[], Object> seenMap) {
/* 244 */     if (o == null) {
/* 245 */       sbuf.append("null");
/*     */       return;
/*     */     } 
/* 248 */     if (!o.getClass().isArray()) {
/* 249 */       safeObjectAppend(sbuf, o);
/*     */ 
/*     */     
/*     */     }
/* 253 */     else if (o instanceof boolean[]) {
/* 254 */       booleanArrayAppend(sbuf, (boolean[])o);
/* 255 */     } else if (o instanceof byte[]) {
/* 256 */       byteArrayAppend(sbuf, (byte[])o);
/* 257 */     } else if (o instanceof char[]) {
/* 258 */       charArrayAppend(sbuf, (char[])o);
/* 259 */     } else if (o instanceof short[]) {
/* 260 */       shortArrayAppend(sbuf, (short[])o);
/* 261 */     } else if (o instanceof int[]) {
/* 262 */       intArrayAppend(sbuf, (int[])o);
/* 263 */     } else if (o instanceof long[]) {
/* 264 */       longArrayAppend(sbuf, (long[])o);
/* 265 */     } else if (o instanceof float[]) {
/* 266 */       floatArrayAppend(sbuf, (float[])o);
/* 267 */     } else if (o instanceof double[]) {
/* 268 */       doubleArrayAppend(sbuf, (double[])o);
/*     */     } else {
/* 270 */       objectArrayAppend(sbuf, (Object[])o, seenMap);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void safeObjectAppend(StringBuilder sbuf, Object o) {
/*     */     try {
/* 277 */       String oAsString = o.toString();
/* 278 */       sbuf.append(oAsString);
/* 279 */     } catch (Throwable t) {
/* 280 */       Util.report("SLF4J: Failed toString() invocation on an object of type [" + o.getClass().getName() + "]", t);
/* 281 */       sbuf.append("[FAILED toString()]");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void objectArrayAppend(StringBuilder sbuf, Object[] a, Map<Object[], Object> seenMap) {
/* 287 */     sbuf.append('[');
/* 288 */     if (!seenMap.containsKey(a)) {
/* 289 */       seenMap.put(a, null);
/* 290 */       int len = a.length;
/* 291 */       for (int i = 0; i < len; i++) {
/* 292 */         deeplyAppendParameter(sbuf, a[i], seenMap);
/* 293 */         if (i != len - 1) {
/* 294 */           sbuf.append(", ");
/*     */         }
/*     */       } 
/* 297 */       seenMap.remove(a);
/*     */     } else {
/* 299 */       sbuf.append("...");
/*     */     } 
/* 301 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
/* 305 */     sbuf.append('[');
/* 306 */     int len = a.length;
/* 307 */     for (int i = 0; i < len; i++) {
/* 308 */       sbuf.append(a[i]);
/* 309 */       if (i != len - 1)
/* 310 */         sbuf.append(", "); 
/*     */     } 
/* 312 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void byteArrayAppend(StringBuilder sbuf, byte[] a) {
/* 316 */     sbuf.append('[');
/* 317 */     int len = a.length;
/* 318 */     for (int i = 0; i < len; i++) {
/* 319 */       sbuf.append(a[i]);
/* 320 */       if (i != len - 1)
/* 321 */         sbuf.append(", "); 
/*     */     } 
/* 323 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void charArrayAppend(StringBuilder sbuf, char[] a) {
/* 327 */     sbuf.append('[');
/* 328 */     int len = a.length;
/* 329 */     for (int i = 0; i < len; i++) {
/* 330 */       sbuf.append(a[i]);
/* 331 */       if (i != len - 1)
/* 332 */         sbuf.append(", "); 
/*     */     } 
/* 334 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void shortArrayAppend(StringBuilder sbuf, short[] a) {
/* 338 */     sbuf.append('[');
/* 339 */     int len = a.length;
/* 340 */     for (int i = 0; i < len; i++) {
/* 341 */       sbuf.append(a[i]);
/* 342 */       if (i != len - 1)
/* 343 */         sbuf.append(", "); 
/*     */     } 
/* 345 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void intArrayAppend(StringBuilder sbuf, int[] a) {
/* 349 */     sbuf.append('[');
/* 350 */     int len = a.length;
/* 351 */     for (int i = 0; i < len; i++) {
/* 352 */       sbuf.append(a[i]);
/* 353 */       if (i != len - 1)
/* 354 */         sbuf.append(", "); 
/*     */     } 
/* 356 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void longArrayAppend(StringBuilder sbuf, long[] a) {
/* 360 */     sbuf.append('[');
/* 361 */     int len = a.length;
/* 362 */     for (int i = 0; i < len; i++) {
/* 363 */       sbuf.append(a[i]);
/* 364 */       if (i != len - 1)
/* 365 */         sbuf.append(", "); 
/*     */     } 
/* 367 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
/* 371 */     sbuf.append('[');
/* 372 */     int len = a.length;
/* 373 */     for (int i = 0; i < len; i++) {
/* 374 */       sbuf.append(a[i]);
/* 375 */       if (i != len - 1)
/* 376 */         sbuf.append(", "); 
/*     */     } 
/* 378 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
/* 382 */     sbuf.append('[');
/* 383 */     int len = a.length;
/* 384 */     for (int i = 0; i < len; i++) {
/* 385 */       sbuf.append(a[i]);
/* 386 */       if (i != len - 1)
/* 387 */         sbuf.append(", "); 
/*     */     } 
/* 389 */     sbuf.append(']');
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
/*     */   public static Throwable getThrowableCandidate(Object[] argArray) {
/* 401 */     if (argArray == null || argArray.length == 0) {
/* 402 */       return null;
/*     */     }
/*     */     
/* 405 */     Object lastEntry = argArray[argArray.length - 1];
/* 406 */     if (lastEntry instanceof Throwable) {
/* 407 */       return (Throwable)lastEntry;
/*     */     }
/*     */     
/* 410 */     return null;
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
/*     */   public static Object[] trimmedCopy(Object[] argArray) {
/* 422 */     if (argArray == null || argArray.length == 0) {
/* 423 */       throw new IllegalStateException("non-sensical empty or null argument array");
/*     */     }
/*     */     
/* 426 */     int trimmedLen = argArray.length - 1;
/*     */     
/* 428 */     Object[] trimmed = new Object[trimmedLen];
/*     */     
/* 430 */     if (trimmedLen > 0) {
/* 431 */       System.arraycopy(argArray, 0, trimmed, 0, trimmedLen);
/*     */     }
/*     */     
/* 434 */     return trimmed;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\slf4j\helpers\MessageFormatter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */