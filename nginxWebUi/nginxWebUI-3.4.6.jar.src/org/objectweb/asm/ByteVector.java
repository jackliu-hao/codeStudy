/*     */ package org.objectweb.asm;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ByteVector
/*     */ {
/*     */   byte[] data;
/*     */   int length;
/*     */   
/*     */   public ByteVector() {
/*  46 */     this.data = new byte[64];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteVector(int initialCapacity) {
/*  55 */     this.data = new byte[initialCapacity];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ByteVector(byte[] data) {
/*  64 */     this.data = data;
/*  65 */     this.length = data.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteVector putByte(int byteValue) {
/*  75 */     int currentLength = this.length;
/*  76 */     if (currentLength + 1 > this.data.length) {
/*  77 */       enlarge(1);
/*     */     }
/*  79 */     this.data[currentLength++] = (byte)byteValue;
/*  80 */     this.length = currentLength;
/*  81 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final ByteVector put11(int byteValue1, int byteValue2) {
/*  92 */     int currentLength = this.length;
/*  93 */     if (currentLength + 2 > this.data.length) {
/*  94 */       enlarge(2);
/*     */     }
/*  96 */     byte[] currentData = this.data;
/*  97 */     currentData[currentLength++] = (byte)byteValue1;
/*  98 */     currentData[currentLength++] = (byte)byteValue2;
/*  99 */     this.length = currentLength;
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteVector putShort(int shortValue) {
/* 110 */     int currentLength = this.length;
/* 111 */     if (currentLength + 2 > this.data.length) {
/* 112 */       enlarge(2);
/*     */     }
/* 114 */     byte[] currentData = this.data;
/* 115 */     currentData[currentLength++] = (byte)(shortValue >>> 8);
/* 116 */     currentData[currentLength++] = (byte)shortValue;
/* 117 */     this.length = currentLength;
/* 118 */     return this;
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
/*     */   final ByteVector put12(int byteValue, int shortValue) {
/* 130 */     int currentLength = this.length;
/* 131 */     if (currentLength + 3 > this.data.length) {
/* 132 */       enlarge(3);
/*     */     }
/* 134 */     byte[] currentData = this.data;
/* 135 */     currentData[currentLength++] = (byte)byteValue;
/* 136 */     currentData[currentLength++] = (byte)(shortValue >>> 8);
/* 137 */     currentData[currentLength++] = (byte)shortValue;
/* 138 */     this.length = currentLength;
/* 139 */     return this;
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
/*     */   final ByteVector put112(int byteValue1, int byteValue2, int shortValue) {
/* 152 */     int currentLength = this.length;
/* 153 */     if (currentLength + 4 > this.data.length) {
/* 154 */       enlarge(4);
/*     */     }
/* 156 */     byte[] currentData = this.data;
/* 157 */     currentData[currentLength++] = (byte)byteValue1;
/* 158 */     currentData[currentLength++] = (byte)byteValue2;
/* 159 */     currentData[currentLength++] = (byte)(shortValue >>> 8);
/* 160 */     currentData[currentLength++] = (byte)shortValue;
/* 161 */     this.length = currentLength;
/* 162 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteVector putInt(int intValue) {
/* 172 */     int currentLength = this.length;
/* 173 */     if (currentLength + 4 > this.data.length) {
/* 174 */       enlarge(4);
/*     */     }
/* 176 */     byte[] currentData = this.data;
/* 177 */     currentData[currentLength++] = (byte)(intValue >>> 24);
/* 178 */     currentData[currentLength++] = (byte)(intValue >>> 16);
/* 179 */     currentData[currentLength++] = (byte)(intValue >>> 8);
/* 180 */     currentData[currentLength++] = (byte)intValue;
/* 181 */     this.length = currentLength;
/* 182 */     return this;
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
/*     */   final ByteVector put122(int byteValue, int shortValue1, int shortValue2) {
/* 195 */     int currentLength = this.length;
/* 196 */     if (currentLength + 5 > this.data.length) {
/* 197 */       enlarge(5);
/*     */     }
/* 199 */     byte[] currentData = this.data;
/* 200 */     currentData[currentLength++] = (byte)byteValue;
/* 201 */     currentData[currentLength++] = (byte)(shortValue1 >>> 8);
/* 202 */     currentData[currentLength++] = (byte)shortValue1;
/* 203 */     currentData[currentLength++] = (byte)(shortValue2 >>> 8);
/* 204 */     currentData[currentLength++] = (byte)shortValue2;
/* 205 */     this.length = currentLength;
/* 206 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteVector putLong(long longValue) {
/* 216 */     int currentLength = this.length;
/* 217 */     if (currentLength + 8 > this.data.length) {
/* 218 */       enlarge(8);
/*     */     }
/* 220 */     byte[] currentData = this.data;
/* 221 */     int intValue = (int)(longValue >>> 32L);
/* 222 */     currentData[currentLength++] = (byte)(intValue >>> 24);
/* 223 */     currentData[currentLength++] = (byte)(intValue >>> 16);
/* 224 */     currentData[currentLength++] = (byte)(intValue >>> 8);
/* 225 */     currentData[currentLength++] = (byte)intValue;
/* 226 */     intValue = (int)longValue;
/* 227 */     currentData[currentLength++] = (byte)(intValue >>> 24);
/* 228 */     currentData[currentLength++] = (byte)(intValue >>> 16);
/* 229 */     currentData[currentLength++] = (byte)(intValue >>> 8);
/* 230 */     currentData[currentLength++] = (byte)intValue;
/* 231 */     this.length = currentLength;
/* 232 */     return this;
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
/*     */   public ByteVector putUTF8(String stringValue) {
/* 244 */     int charLength = stringValue.length();
/* 245 */     if (charLength > 65535) {
/* 246 */       throw new IllegalArgumentException("UTF8 string too large");
/*     */     }
/* 248 */     int currentLength = this.length;
/* 249 */     if (currentLength + 2 + charLength > this.data.length) {
/* 250 */       enlarge(2 + charLength);
/*     */     }
/* 252 */     byte[] currentData = this.data;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 257 */     currentData[currentLength++] = (byte)(charLength >>> 8);
/* 258 */     currentData[currentLength++] = (byte)charLength;
/* 259 */     for (int i = 0; i < charLength; i++) {
/* 260 */       char charValue = stringValue.charAt(i);
/* 261 */       if (charValue >= '\001' && charValue <= '') {
/* 262 */         currentData[currentLength++] = (byte)charValue;
/*     */       } else {
/* 264 */         this.length = currentLength;
/* 265 */         return encodeUtf8(stringValue, i, 65535);
/*     */       } 
/*     */     } 
/* 268 */     this.length = currentLength;
/* 269 */     return this;
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
/*     */   final ByteVector encodeUtf8(String stringValue, int offset, int maxByteLength) {
/* 285 */     int charLength = stringValue.length();
/* 286 */     int byteLength = offset;
/* 287 */     for (int i = offset; i < charLength; i++) {
/* 288 */       char charValue = stringValue.charAt(i);
/* 289 */       if (charValue >= '\001' && charValue <= '') {
/* 290 */         byteLength++;
/* 291 */       } else if (charValue <= '߿') {
/* 292 */         byteLength += 2;
/*     */       } else {
/* 294 */         byteLength += 3;
/*     */       } 
/*     */     } 
/* 297 */     if (byteLength > maxByteLength) {
/* 298 */       throw new IllegalArgumentException("UTF8 string too large");
/*     */     }
/*     */     
/* 301 */     int byteLengthOffset = this.length - offset - 2;
/* 302 */     if (byteLengthOffset >= 0) {
/* 303 */       this.data[byteLengthOffset] = (byte)(byteLength >>> 8);
/* 304 */       this.data[byteLengthOffset + 1] = (byte)byteLength;
/*     */     } 
/* 306 */     if (this.length + byteLength - offset > this.data.length) {
/* 307 */       enlarge(byteLength - offset);
/*     */     }
/* 309 */     int currentLength = this.length;
/* 310 */     for (int j = offset; j < charLength; j++) {
/* 311 */       char charValue = stringValue.charAt(j);
/* 312 */       if (charValue >= '\001' && charValue <= '') {
/* 313 */         this.data[currentLength++] = (byte)charValue;
/* 314 */       } else if (charValue <= '߿') {
/* 315 */         this.data[currentLength++] = (byte)(0xC0 | charValue >> 6 & 0x1F);
/* 316 */         this.data[currentLength++] = (byte)(0x80 | charValue & 0x3F);
/*     */       } else {
/* 318 */         this.data[currentLength++] = (byte)(0xE0 | charValue >> 12 & 0xF);
/* 319 */         this.data[currentLength++] = (byte)(0x80 | charValue >> 6 & 0x3F);
/* 320 */         this.data[currentLength++] = (byte)(0x80 | charValue & 0x3F);
/*     */       } 
/*     */     } 
/* 323 */     this.length = currentLength;
/* 324 */     return this;
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
/*     */   public ByteVector putByteArray(byte[] byteArrayValue, int byteOffset, int byteLength) {
/* 339 */     if (this.length + byteLength > this.data.length) {
/* 340 */       enlarge(byteLength);
/*     */     }
/* 342 */     if (byteArrayValue != null) {
/* 343 */       System.arraycopy(byteArrayValue, byteOffset, this.data, this.length, byteLength);
/*     */     }
/* 345 */     this.length += byteLength;
/* 346 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void enlarge(int size) {
/* 355 */     int doubleCapacity = 2 * this.data.length;
/* 356 */     int minimalCapacity = this.length + size;
/* 357 */     byte[] newData = new byte[(doubleCapacity > minimalCapacity) ? doubleCapacity : minimalCapacity];
/* 358 */     System.arraycopy(this.data, 0, newData, 0, this.length);
/* 359 */     this.data = newData;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\ByteVector.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */