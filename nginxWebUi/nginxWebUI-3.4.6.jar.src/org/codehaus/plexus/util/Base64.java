/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Base64
/*     */ {
/*     */   static final int CHUNK_SIZE = 76;
/*  52 */   static final byte[] CHUNK_SEPARATOR = "\r\n".getBytes();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int BASELENGTH = 255;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int LOOKUPLENGTH = 64;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int EIGHTBIT = 8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int SIXTEENBIT = 16;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int TWENTYFOURBITGROUP = 24;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int FOURBYTE = 4;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int SIGN = -128;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final byte PAD = 61;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 104 */   private static byte[] base64Alphabet = new byte[255];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 119 */   private static byte[] lookUpBase64Alphabet = new byte[64];
/*     */   
/*     */   static {
/*     */     int i;
/* 123 */     for (i = 0; i < 255; i++) {
/* 124 */       base64Alphabet[i] = -1;
/*     */     }
/* 126 */     for (i = 90; i >= 65; i--) {
/* 127 */       base64Alphabet[i] = (byte)(i - 65);
/*     */     }
/* 129 */     for (i = 122; i >= 97; i--) {
/* 130 */       base64Alphabet[i] = (byte)(i - 97 + 26);
/*     */     }
/* 132 */     for (i = 57; i >= 48; i--) {
/* 133 */       base64Alphabet[i] = (byte)(i - 48 + 52);
/*     */     }
/*     */     
/* 136 */     base64Alphabet[43] = 62;
/* 137 */     base64Alphabet[47] = 63;
/*     */     
/* 139 */     for (i = 0; i <= 25; i++) {
/* 140 */       lookUpBase64Alphabet[i] = (byte)(65 + i);
/*     */     }
/*     */     int j;
/* 143 */     for (i = 26, j = 0; i <= 51; i++, j++) {
/* 144 */       lookUpBase64Alphabet[i] = (byte)(97 + j);
/*     */     }
/*     */     
/* 147 */     for (i = 52, j = 0; i <= 61; i++, j++) {
/* 148 */       lookUpBase64Alphabet[i] = (byte)(48 + j);
/*     */     }
/*     */     
/* 151 */     lookUpBase64Alphabet[62] = 43;
/* 152 */     lookUpBase64Alphabet[63] = 47;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isBase64(byte octect) {
/* 162 */     if (octect == 61)
/* 163 */       return true; 
/* 164 */     if (octect < 0 || base64Alphabet[octect] == -1) {
/* 165 */       return false;
/*     */     }
/* 167 */     return true;
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
/*     */   public static boolean isArrayByteBase64(byte[] arrayOctect) {
/* 181 */     arrayOctect = discardWhitespace(arrayOctect);
/*     */     
/* 183 */     int length = arrayOctect.length;
/* 184 */     if (length == 0)
/*     */     {
/*     */       
/* 187 */       return true;
/*     */     }
/* 189 */     for (int i = 0; i < length; i++) {
/* 190 */       if (!isBase64(arrayOctect[i])) {
/* 191 */         return false;
/*     */       }
/*     */     } 
/* 194 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] encodeBase64(byte[] binaryData) {
/* 205 */     return encodeBase64(binaryData, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] encodeBase64Chunked(byte[] binaryData) {
/* 216 */     return encodeBase64(binaryData, true);
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
/*     */   public byte[] decode(byte[] pArray) {
/* 228 */     return decodeBase64(pArray);
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
/*     */   public static byte[] encodeBase64(byte[] binaryData, boolean isChunked) {
/* 241 */     int lengthDataBits = binaryData.length * 8;
/* 242 */     int fewerThan24bits = lengthDataBits % 24;
/* 243 */     int numberTriplets = lengthDataBits / 24;
/* 244 */     byte[] encodedData = null;
/* 245 */     int encodedDataLength = 0;
/* 246 */     int nbrChunks = 0;
/*     */     
/* 248 */     if (fewerThan24bits != 0) {
/*     */       
/* 250 */       encodedDataLength = (numberTriplets + 1) * 4;
/*     */     } else {
/*     */       
/* 253 */       encodedDataLength = numberTriplets * 4;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 259 */     if (isChunked) {
/*     */       
/* 261 */       nbrChunks = (CHUNK_SEPARATOR.length == 0) ? 0 : (int)Math.ceil((encodedDataLength / 76.0F));
/*     */       
/* 263 */       encodedDataLength += nbrChunks * CHUNK_SEPARATOR.length;
/*     */     } 
/*     */     
/* 266 */     encodedData = new byte[encodedDataLength];
/*     */     
/* 268 */     byte k = 0, l = 0, b1 = 0, b2 = 0, b3 = 0;
/*     */     
/* 270 */     int encodedIndex = 0;
/* 271 */     int dataIndex = 0;
/* 272 */     int i = 0;
/* 273 */     int nextSeparatorIndex = 76;
/* 274 */     int chunksSoFar = 0;
/*     */ 
/*     */     
/* 277 */     for (i = 0; i < numberTriplets; i++) {
/* 278 */       dataIndex = i * 3;
/* 279 */       b1 = binaryData[dataIndex];
/* 280 */       b2 = binaryData[dataIndex + 1];
/* 281 */       b3 = binaryData[dataIndex + 2];
/*     */ 
/*     */ 
/*     */       
/* 285 */       l = (byte)(b2 & 0xF);
/* 286 */       k = (byte)(b1 & 0x3);
/*     */       
/* 288 */       byte val1 = ((b1 & Byte.MIN_VALUE) == 0) ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xC0);
/*     */       
/* 290 */       byte val2 = ((b2 & Byte.MIN_VALUE) == 0) ? (byte)(b2 >> 4) : (byte)(b2 >> 4 ^ 0xF0);
/*     */       
/* 292 */       byte val3 = ((b3 & Byte.MIN_VALUE) == 0) ? (byte)(b3 >> 6) : (byte)(b3 >> 6 ^ 0xFC);
/*     */ 
/*     */       
/* 295 */       encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
/*     */ 
/*     */ 
/*     */       
/* 299 */       encodedData[encodedIndex + 1] = lookUpBase64Alphabet[val2 | k << 4];
/*     */       
/* 301 */       encodedData[encodedIndex + 2] = lookUpBase64Alphabet[l << 2 | val3];
/*     */       
/* 303 */       encodedData[encodedIndex + 3] = lookUpBase64Alphabet[b3 & 0x3F];
/*     */       
/* 305 */       encodedIndex += 4;
/*     */ 
/*     */       
/* 308 */       if (isChunked)
/*     */       {
/* 310 */         if (encodedIndex == nextSeparatorIndex) {
/* 311 */           System.arraycopy(CHUNK_SEPARATOR, 0, encodedData, encodedIndex, CHUNK_SEPARATOR.length);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 317 */           chunksSoFar++;
/* 318 */           nextSeparatorIndex = 76 * (chunksSoFar + 1) + chunksSoFar * CHUNK_SEPARATOR.length;
/*     */ 
/*     */           
/* 321 */           encodedIndex += CHUNK_SEPARATOR.length;
/*     */         } 
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 327 */     dataIndex = i * 3;
/*     */     
/* 329 */     if (fewerThan24bits == 8) {
/* 330 */       b1 = binaryData[dataIndex];
/* 331 */       k = (byte)(b1 & 0x3);
/*     */ 
/*     */       
/* 334 */       byte val1 = ((b1 & Byte.MIN_VALUE) == 0) ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xC0);
/*     */       
/* 336 */       encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
/* 337 */       encodedData[encodedIndex + 1] = lookUpBase64Alphabet[k << 4];
/* 338 */       encodedData[encodedIndex + 2] = 61;
/* 339 */       encodedData[encodedIndex + 3] = 61;
/* 340 */     } else if (fewerThan24bits == 16) {
/*     */       
/* 342 */       b1 = binaryData[dataIndex];
/* 343 */       b2 = binaryData[dataIndex + 1];
/* 344 */       l = (byte)(b2 & 0xF);
/* 345 */       k = (byte)(b1 & 0x3);
/*     */       
/* 347 */       byte val1 = ((b1 & Byte.MIN_VALUE) == 0) ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xC0);
/*     */       
/* 349 */       byte val2 = ((b2 & Byte.MIN_VALUE) == 0) ? (byte)(b2 >> 4) : (byte)(b2 >> 4 ^ 0xF0);
/*     */ 
/*     */       
/* 352 */       encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
/* 353 */       encodedData[encodedIndex + 1] = lookUpBase64Alphabet[val2 | k << 4];
/*     */       
/* 355 */       encodedData[encodedIndex + 2] = lookUpBase64Alphabet[l << 2];
/* 356 */       encodedData[encodedIndex + 3] = 61;
/*     */     } 
/*     */     
/* 359 */     if (isChunked)
/*     */     {
/* 361 */       if (chunksSoFar < nbrChunks) {
/* 362 */         System.arraycopy(CHUNK_SEPARATOR, 0, encodedData, encodedDataLength - CHUNK_SEPARATOR.length, CHUNK_SEPARATOR.length);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 371 */     return encodedData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] decodeBase64(byte[] base64Data) {
/* 382 */     base64Data = discardNonBase64(base64Data);
/*     */ 
/*     */     
/* 385 */     if (base64Data.length == 0) {
/* 386 */       return new byte[0];
/*     */     }
/*     */     
/* 389 */     int numberQuadruple = base64Data.length / 4;
/* 390 */     byte[] decodedData = null;
/* 391 */     byte b1 = 0, b2 = 0, b3 = 0, b4 = 0, marker0 = 0, marker1 = 0;
/*     */ 
/*     */ 
/*     */     
/* 395 */     int encodedIndex = 0;
/* 396 */     int dataIndex = 0;
/*     */ 
/*     */     
/* 399 */     int lastData = base64Data.length;
/*     */     
/* 401 */     while (base64Data[lastData - 1] == 61) {
/* 402 */       if (--lastData == 0) {
/* 403 */         return new byte[0];
/*     */       }
/*     */     } 
/* 406 */     decodedData = new byte[lastData - numberQuadruple];
/*     */ 
/*     */     
/* 409 */     for (int i = 0; i < numberQuadruple; i++) {
/* 410 */       dataIndex = i * 4;
/* 411 */       marker0 = base64Data[dataIndex + 2];
/* 412 */       marker1 = base64Data[dataIndex + 3];
/*     */       
/* 414 */       b1 = base64Alphabet[base64Data[dataIndex]];
/* 415 */       b2 = base64Alphabet[base64Data[dataIndex + 1]];
/*     */       
/* 417 */       if (marker0 != 61 && marker1 != 61) {
/*     */         
/* 419 */         b3 = base64Alphabet[marker0];
/* 420 */         b4 = base64Alphabet[marker1];
/*     */         
/* 422 */         decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
/* 423 */         decodedData[encodedIndex + 1] = (byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
/*     */         
/* 425 */         decodedData[encodedIndex + 2] = (byte)(b3 << 6 | b4);
/* 426 */       } else if (marker0 == 61) {
/*     */         
/* 428 */         decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
/* 429 */       } else if (marker1 == 61) {
/*     */         
/* 431 */         b3 = base64Alphabet[marker0];
/*     */         
/* 433 */         decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
/* 434 */         decodedData[encodedIndex + 1] = (byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
/*     */       } 
/*     */       
/* 437 */       encodedIndex += 3;
/*     */     } 
/* 439 */     return decodedData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static byte[] discardWhitespace(byte[] data) {
/* 450 */     byte[] groomedData = new byte[data.length];
/* 451 */     int bytesCopied = 0;
/*     */     
/* 453 */     for (int i = 0; i < data.length; i++) {
/* 454 */       switch (data[i]) {
/*     */         case 9:
/*     */         case 10:
/*     */         case 13:
/*     */         case 32:
/*     */           break;
/*     */         default:
/* 461 */           groomedData[bytesCopied++] = data[i];
/*     */           break;
/*     */       } 
/*     */     } 
/* 465 */     byte[] packedData = new byte[bytesCopied];
/*     */     
/* 467 */     System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);
/*     */     
/* 469 */     return packedData;
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
/*     */   static byte[] discardNonBase64(byte[] data) {
/* 482 */     byte[] groomedData = new byte[data.length];
/* 483 */     int bytesCopied = 0;
/*     */     
/* 485 */     for (int i = 0; i < data.length; i++) {
/* 486 */       if (isBase64(data[i])) {
/* 487 */         groomedData[bytesCopied++] = data[i];
/*     */       }
/*     */     } 
/*     */     
/* 491 */     byte[] packedData = new byte[bytesCopied];
/*     */     
/* 493 */     System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);
/*     */     
/* 495 */     return packedData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] encode(byte[] pArray) {
/* 506 */     return encodeBase64(pArray, false);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\Base64.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */