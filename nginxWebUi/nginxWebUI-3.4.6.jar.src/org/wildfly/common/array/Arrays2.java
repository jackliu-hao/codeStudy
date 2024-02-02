/*     */ package org.wildfly.common.array;
/*     */ 
/*     */ import java.lang.reflect.Array;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Arrays2
/*     */ {
/*     */   public static boolean equals(byte[] a1, int offs1, byte[] a2, int offs2, int len) {
/*  46 */     if (offs1 < 0 || offs1 + len > a1.length) return false; 
/*  47 */     if (offs2 < 0 || offs2 + len > a2.length) return false; 
/*  48 */     for (int i = 0; i < len; i++) {
/*  49 */       if (a1[i + offs1] != a2[i + offs2]) {
/*  50 */         return false;
/*     */       }
/*     */     } 
/*  53 */     return true;
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
/*     */   public static boolean equals(byte[] a1, int offs1, byte[] a2) {
/*  66 */     return equals(a1, offs1, a2, 0, a2.length);
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
/*     */   public static boolean equals(char[] a1, int offs1, char[] a2, int offs2, int len) {
/*  81 */     if (offs1 + len > a1.length) return false; 
/*  82 */     if (offs2 + len > a2.length) return false; 
/*  83 */     for (int i = 0; i < len; i++) {
/*  84 */       if (a1[i + offs1] != a2[i + offs2]) {
/*  85 */         return false;
/*     */       }
/*     */     } 
/*  88 */     return true;
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
/*     */   public static boolean equals(char[] a1, int offs1, char[] a2) {
/* 101 */     return equals(a1, offs1, a2, 0, a2.length);
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
/*     */   public static boolean equals(char[] a1, int offs1, String a2, int offs2, int len) {
/* 116 */     if (offs1 + len > a1.length) return false; 
/* 117 */     if (offs2 + len > a2.length()) return false; 
/* 118 */     for (int i = 0; i < len; i++) {
/* 119 */       if (a1[i + offs1] != a2.charAt(i + offs2)) {
/* 120 */         return false;
/*     */       }
/*     */     } 
/* 123 */     return true;
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
/*     */   public static boolean equals(char[] a1, int offs1, String a2) {
/* 136 */     return equals(a1, offs1, a2, 0, a2.length());
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
/*     */   public static boolean equals(String a1, int offs1, char[] a2) {
/* 149 */     return equals(a2, 0, a1, offs1, a2.length);
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
/*     */   public static boolean equals(String a1, char[] a2) {
/* 161 */     return equals(a1, 0, a2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SafeVarargs
/*     */   public static <T> T[] of(T... items) {
/* 173 */     return items;
/*     */   }
/*     */   
/*     */   private static char hex(int v) {
/* 177 */     return (char)((v < 10) ? (48 + v) : (97 + v - 10));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(byte[] bytes) {
/* 187 */     StringBuilder b = new StringBuilder(bytes.length * 2);
/* 188 */     for (byte x : bytes) {
/* 189 */       b.append(hex((x & 0xF0) >> 4)).append(hex(x & 0xF));
/*     */     }
/* 191 */     return b.toString();
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
/*     */   public static int indexOf(byte[] array, int search, int offs, int len) {
/* 204 */     for (int i = 0; i < len; i++) {
/* 205 */       if (array[offs + i] == (byte)search) {
/* 206 */         return offs + i;
/*     */       }
/*     */     } 
/* 209 */     return -1;
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
/*     */   public static int indexOf(byte[] array, int search, int offs) {
/* 221 */     return indexOf(array, search, offs, array.length - offs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int indexOf(byte[] array, int search) {
/* 232 */     return indexOf(array, search, 0, array.length);
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
/*     */   public static <E> E[] createArray(Class<E> elementType, int size) {
/* 245 */     return (E[])Array.newInstance(elementType, size);
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
/*     */   public static <E> E[] compactNulls(E[] original) {
/* 257 */     int r = 0;
/*     */     
/*     */     while (true) {
/* 260 */       E item = original[r++];
/* 261 */       if (item == null) {
/*     */         break;
/*     */       }
/* 264 */       if (r == original.length)
/*     */       {
/* 266 */         return original;
/*     */       }
/*     */     } 
/*     */     
/* 270 */     int w = r - 1;
/*     */     while (true) {
/* 272 */       E e = original[r++];
/* 273 */       if (e != null) {
/* 274 */         original[w++] = e;
/*     */       }
/* 276 */       if (r == original.length) {
/* 277 */         return Arrays.copyOf(original, w);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String objectToString(Object value) {
/* 289 */     if (value == null)
/* 290 */       return "null"; 
/* 291 */     if (value instanceof Object[])
/* 292 */       return Arrays.deepToString((Object[])value); 
/* 293 */     if (value.getClass().isArray()) {
/* 294 */       StringBuilder sb = new StringBuilder();
/* 295 */       sb.append('[');
/* 296 */       for (int i = 0; i < Array.getLength(value); i++) {
/* 297 */         if (i != 0) sb.append(", "); 
/* 298 */         sb.append(String.valueOf(Array.get(value, i)));
/*     */       } 
/* 300 */       sb.append(']');
/* 301 */       return sb.toString();
/*     */     } 
/* 303 */     return value.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\array\Arrays2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */