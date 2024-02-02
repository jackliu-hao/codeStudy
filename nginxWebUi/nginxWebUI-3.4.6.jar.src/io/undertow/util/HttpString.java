/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Field;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ public final class HttpString
/*     */   implements Comparable<HttpString>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final byte[] bytes;
/*     */   private final transient int hashCode;
/*     */   private final int orderInt;
/*     */   private transient String string;
/*     */   private static final Field hashCodeField;
/*     */   
/*     */   static {
/*     */     try {
/*  55 */       hashCodeField = HttpString.class.getDeclaredField("hashCode");
/*  56 */       hashCodeField.setAccessible(true);
/*  57 */     } catch (NoSuchFieldException e) {
/*  58 */       throw new NoSuchFieldError(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  65 */   public static final HttpString EMPTY = new HttpString("");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpString(byte[] bytes) {
/*  73 */     this((byte[])bytes.clone(), (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpString(byte[] bytes, int offset, int length) {
/*  84 */     this(Arrays.copyOfRange(bytes, offset, offset + length), (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpString(ByteBuffer buffer) {
/*  93 */     this(take(buffer), (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpString(String string) {
/* 103 */     this(string, 0);
/*     */   }
/*     */   
/*     */   HttpString(String string, int orderInt) {
/* 107 */     this.orderInt = orderInt;
/* 108 */     this.bytes = toByteArray(string);
/* 109 */     this.hashCode = calcHashCode(this.bytes);
/* 110 */     this.string = string;
/* 111 */     checkForNewlines();
/*     */   }
/*     */   
/*     */   private static byte[] toByteArray(String string) {
/* 115 */     int len = string.length();
/* 116 */     byte[] bytes = new byte[len];
/* 117 */     for (int i = 0; i < len; i++) {
/* 118 */       char c = string.charAt(i);
/* 119 */       if (c > 'ÿ') {
/* 120 */         throw new IllegalArgumentException("Invalid string contents " + string);
/*     */       }
/* 122 */       bytes[i] = (byte)c;
/*     */     } 
/* 124 */     return bytes;
/*     */   }
/*     */   
/*     */   private void checkForNewlines() {
/* 128 */     for (byte b : this.bytes) {
/* 129 */       if (b == 13 || b == 10) {
/* 130 */         throw UndertowMessages.MESSAGES.newlineNotSupportedInHttpString(this.string);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private HttpString(byte[] bytes, String string) {
/* 136 */     this.bytes = bytes;
/* 137 */     this.hashCode = calcHashCode(bytes);
/* 138 */     this.string = string;
/* 139 */     this.orderInt = 0;
/* 140 */     checkForNewlines();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpString tryFromString(String string) {
/* 151 */     HttpString cached = Headers.fromCache(string);
/* 152 */     if (cached != null) {
/* 153 */       return cached;
/*     */     }
/* 155 */     int len = string.length();
/* 156 */     byte[] bytes = new byte[len];
/* 157 */     for (int i = 0; i < len; i++) {
/* 158 */       char c = string.charAt(i);
/* 159 */       if (c > 'ÿ') {
/* 160 */         return null;
/*     */       }
/* 162 */       bytes[i] = (byte)c;
/*     */     } 
/* 164 */     return new HttpString(bytes, string);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int length() {
/* 173 */     return this.bytes.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte byteAt(int idx) {
/* 182 */     return this.bytes[idx];
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
/*     */   public void copyTo(int srcOffs, byte[] dst, int offs, int len) {
/* 194 */     System.arraycopy(this.bytes, srcOffs, dst, offs, len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void copyTo(byte[] dst, int offs, int len) {
/* 205 */     copyTo(0, dst, offs, len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void copyTo(byte[] dst, int offs) {
/* 215 */     copyTo(dst, offs, this.bytes.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void appendTo(ByteBuffer buffer) {
/* 224 */     buffer.put(this.bytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream output) throws IOException {
/* 234 */     output.write(this.bytes);
/*     */   }
/*     */   
/*     */   private static byte[] take(ByteBuffer buffer) {
/* 238 */     if (buffer.hasArray()) {
/*     */       
/*     */       try {
/* 241 */         return Arrays.copyOfRange(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
/*     */       } finally {
/* 243 */         buffer.position(buffer.limit());
/*     */       } 
/*     */     }
/* 246 */     byte[] bytes = new byte[buffer.remaining()];
/* 247 */     buffer.get(bytes);
/* 248 */     return bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(HttpString other) {
/* 259 */     if (this.orderInt != 0 && other.orderInt != 0) {
/* 260 */       return Integer.signum(this.orderInt - other.orderInt);
/*     */     }
/* 262 */     int len = Math.min(this.bytes.length, other.bytes.length);
/*     */     
/* 264 */     for (int i = 0; i < len; i++) {
/* 265 */       int res = Integer.signum(higher(this.bytes[i]) - higher(other.bytes[i]));
/* 266 */       if (res != 0) return res;
/*     */     
/*     */     } 
/* 269 */     return Integer.signum(this.bytes.length - other.bytes.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 279 */     return this.hashCode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 290 */     if (other == this) {
/* 291 */       return true;
/*     */     }
/* 293 */     if (!(other instanceof HttpString)) {
/* 294 */       return false;
/*     */     }
/* 296 */     HttpString otherString = (HttpString)other;
/* 297 */     if (this.orderInt > 0 && otherString.orderInt > 0)
/*     */     {
/* 299 */       return false;
/*     */     }
/* 301 */     return bytesAreEqual(this.bytes, otherString.bytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(HttpString other) {
/* 311 */     return (other == this || (other != null && bytesAreEqual(this.bytes, other.bytes)));
/*     */   }
/*     */   
/*     */   private static int calcHashCode(byte[] bytes) {
/* 315 */     int hc = 17;
/* 316 */     for (byte b : bytes) {
/* 317 */       hc = (hc << 4) + hc + higher(b);
/*     */     }
/* 319 */     return hc;
/*     */   }
/*     */   
/*     */   private static int higher(byte b) {
/* 323 */     return b & ((b >= 97 && b <= 122) ? 223 : 255);
/*     */   }
/*     */   
/*     */   private static boolean bytesAreEqual(byte[] a, byte[] b) {
/* 327 */     return (a.length == b.length && bytesAreEquivalent(a, b));
/*     */   }
/*     */   
/*     */   private static boolean bytesAreEquivalent(byte[] a, byte[] b) {
/* 331 */     assert a.length == b.length;
/* 332 */     int len = a.length;
/* 333 */     for (int i = 0; i < len; i++) {
/* 334 */       if (higher(a[i]) != higher(b[i])) {
/* 335 */         return false;
/*     */       }
/*     */     } 
/* 338 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 349 */     if (this.string == null) {
/* 350 */       this.string = new String(this.bytes, StandardCharsets.US_ASCII);
/*     */     }
/* 352 */     return this.string;
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
/* 356 */     ois.defaultReadObject();
/*     */     try {
/* 358 */       hashCodeField.setInt(this, calcHashCode(this.bytes));
/* 359 */     } catch (IllegalAccessException e) {
/* 360 */       throw new IllegalAccessError(e.getMessage());
/*     */     } 
/*     */   }
/*     */   
/*     */   static int hashCodeOf(String headerName) {
/* 365 */     byte[] bytes = toByteArray(headerName);
/* 366 */     return calcHashCode(bytes);
/*     */   }
/*     */   
/*     */   public boolean equalToString(String headerName) {
/* 370 */     if (headerName.length() != this.bytes.length) {
/* 371 */       return false;
/*     */     }
/*     */     
/* 374 */     int len = this.bytes.length;
/* 375 */     for (int i = 0; i < len; i++) {
/* 376 */       if (higher(this.bytes[i]) != higher((byte)headerName.charAt(i))) {
/* 377 */         return false;
/*     */       }
/*     */     } 
/* 380 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\HttpString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */