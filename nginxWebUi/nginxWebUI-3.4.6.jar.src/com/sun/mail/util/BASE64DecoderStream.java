/*     */ package com.sun.mail.util;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BASE64DecoderStream
/*     */   extends FilterInputStream
/*     */ {
/*  57 */   private byte[] buffer = new byte[3];
/*  58 */   private int bufsize = 0;
/*  59 */   private int index = 0;
/*     */ 
/*     */ 
/*     */   
/*  63 */   private byte[] input_buffer = new byte[8190];
/*  64 */   private int input_pos = 0;
/*  65 */   private int input_len = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean ignoreErrors = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BASE64DecoderStream(InputStream in) {
/*  78 */     super(in);
/*     */     
/*  80 */     this.ignoreErrors = PropUtil.getBooleanSystemProperty("mail.mime.base64.ignoreerrors", false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BASE64DecoderStream(InputStream in, boolean ignoreErrors) {
/*  91 */     super(in);
/*  92 */     this.ignoreErrors = ignoreErrors;
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
/*     */   public int read() throws IOException {
/* 109 */     if (this.index >= this.bufsize) {
/* 110 */       this.bufsize = decode(this.buffer, 0, this.buffer.length);
/* 111 */       if (this.bufsize <= 0)
/* 112 */         return -1; 
/* 113 */       this.index = 0;
/*     */     } 
/* 115 */     return this.buffer[this.index++] & 0xFF;
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
/*     */   public int read(byte[] buf, int off, int len) throws IOException {
/* 134 */     int off0 = off;
/* 135 */     while (this.index < this.bufsize && len > 0) {
/* 136 */       buf[off++] = this.buffer[this.index++];
/* 137 */       len--;
/*     */     } 
/* 139 */     if (this.index >= this.bufsize) {
/* 140 */       this.bufsize = this.index = 0;
/*     */     }
/* 142 */     int bsize = len / 3 * 3;
/* 143 */     if (bsize > 0) {
/* 144 */       int size = decode(buf, off, bsize);
/* 145 */       off += size;
/* 146 */       len -= size;
/*     */       
/* 148 */       if (size != bsize) {
/* 149 */         if (off == off0) {
/* 150 */           return -1;
/*     */         }
/* 152 */         return off - off0;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 157 */     for (; len > 0; len--) {
/* 158 */       int c = read();
/* 159 */       if (c == -1)
/*     */         break; 
/* 161 */       buf[off++] = (byte)c;
/*     */     } 
/*     */     
/* 164 */     if (off == off0) {
/* 165 */       return -1;
/*     */     }
/* 167 */     return off - off0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/* 174 */     long skipped = 0L;
/* 175 */     while (n-- > 0L && read() >= 0)
/* 176 */       skipped++; 
/* 177 */     return skipped;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/* 185 */     return false;
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
/*     */   public int available() throws IOException {
/* 197 */     return this.in.available() * 3 / 4 + this.bufsize - this.index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 204 */   private static final char[] pem_array = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 215 */   private static final byte[] pem_convert_array = new byte[256];
/*     */   static {
/*     */     int i;
/* 218 */     for (i = 0; i < 255; i++)
/* 219 */       pem_convert_array[i] = -1; 
/* 220 */     for (i = 0; i < pem_array.length; i++) {
/* 221 */       pem_convert_array[pem_array[i]] = (byte)i;
/*     */     }
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
/*     */   private int decode(byte[] outbuf, int pos, int len) throws IOException {
/* 239 */     int pos0 = pos;
/* 240 */     while (len >= 3) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 246 */       int got = 0;
/* 247 */       int val = 0;
/* 248 */       while (got < 4) {
/* 249 */         int i = getByte();
/* 250 */         if (i == -1 || i == -2) {
/*     */           boolean atEOF;
/* 252 */           if (i == -1) {
/* 253 */             if (got == 0)
/* 254 */               return pos - pos0; 
/* 255 */             if (!this.ignoreErrors) {
/* 256 */               throw new DecodingException("BASE64Decoder: Error in encoded stream: needed 4 valid base64 characters but only got " + got + " before EOF" + recentChars());
/*     */             }
/*     */ 
/*     */ 
/*     */             
/* 261 */             atEOF = true;
/*     */           }
/*     */           else {
/*     */             
/* 265 */             if (got < 2 && !this.ignoreErrors) {
/* 266 */               throw new DecodingException("BASE64Decoder: Error in encoded stream: needed at least 2 valid base64 characters, but only got " + got + " before padding character (=)" + recentChars());
/*     */             }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 274 */             if (got == 0)
/* 275 */               return pos - pos0; 
/* 276 */             atEOF = false;
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 283 */           int size = got - 1;
/* 284 */           if (size == 0) {
/* 285 */             size = 1;
/*     */           }
/*     */           
/* 288 */           got++;
/* 289 */           val <<= 6;
/*     */           
/* 291 */           while (got < 4) {
/* 292 */             if (!atEOF) {
/*     */ 
/*     */               
/* 295 */               i = getByte();
/* 296 */               if (i == -1) {
/* 297 */                 if (!this.ignoreErrors) {
/* 298 */                   throw new DecodingException("BASE64Decoder: Error in encoded stream: hit EOF while looking for padding characters (=)" + recentChars());
/*     */                 
/*     */                 }
/*     */               
/*     */               }
/* 303 */               else if (i != -2 && 
/* 304 */                 !this.ignoreErrors) {
/* 305 */                 throw new DecodingException("BASE64Decoder: Error in encoded stream: found valid base64 character after a padding character (=)" + recentChars());
/*     */               } 
/*     */             } 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 312 */             val <<= 6;
/* 313 */             got++;
/*     */           } 
/*     */ 
/*     */           
/* 317 */           val >>= 8;
/* 318 */           if (size == 2)
/* 319 */             outbuf[pos + 1] = (byte)(val & 0xFF); 
/* 320 */           val >>= 8;
/* 321 */           outbuf[pos] = (byte)(val & 0xFF);
/*     */           
/* 323 */           pos += size;
/* 324 */           return pos - pos0;
/*     */         } 
/*     */         
/* 327 */         val <<= 6;
/* 328 */         got++;
/* 329 */         val |= i;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 334 */       outbuf[pos + 2] = (byte)(val & 0xFF);
/* 335 */       val >>= 8;
/* 336 */       outbuf[pos + 1] = (byte)(val & 0xFF);
/* 337 */       val >>= 8;
/* 338 */       outbuf[pos] = (byte)(val & 0xFF);
/* 339 */       len -= 3;
/* 340 */       pos += 3;
/*     */     } 
/* 342 */     return pos - pos0;
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
/*     */   private int getByte() throws IOException {
/*     */     while (true) {
/* 356 */       if (this.input_pos >= this.input_len) {
/*     */         try {
/* 358 */           this.input_len = this.in.read(this.input_buffer);
/* 359 */         } catch (EOFException ex) {
/* 360 */           return -1;
/*     */         } 
/* 362 */         if (this.input_len <= 0)
/* 363 */           return -1; 
/* 364 */         this.input_pos = 0;
/*     */       } 
/*     */       
/* 367 */       int c = this.input_buffer[this.input_pos++] & 0xFF;
/*     */       
/* 369 */       if (c == 61) {
/* 370 */         return -2;
/*     */       }
/* 372 */       c = pem_convert_array[c];
/*     */       
/* 374 */       if (c != -1) {
/* 375 */         return c;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String recentChars() {
/* 384 */     String errstr = "";
/* 385 */     int nc = (this.input_pos > 10) ? 10 : this.input_pos;
/* 386 */     if (nc > 0) {
/* 387 */       errstr = errstr + ", the " + nc + " most recent characters were: \"";
/*     */       
/* 389 */       for (int k = this.input_pos - nc; k < this.input_pos; k++) {
/* 390 */         char c = (char)(this.input_buffer[k] & 0xFF);
/* 391 */         switch (c) { case '\r':
/* 392 */             errstr = errstr + "\\r"; break;
/* 393 */           case '\n': errstr = errstr + "\\n"; break;
/* 394 */           case '\t': errstr = errstr + "\\t"; break;
/*     */           default:
/* 396 */             if (c >= ' ' && c < '') {
/* 397 */               errstr = errstr + c; break;
/*     */             } 
/* 399 */             errstr = errstr + "\\" + c; break; }
/*     */       
/*     */       } 
/* 402 */       errstr = errstr + "\"";
/*     */     } 
/* 404 */     return errstr;
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
/*     */   public static byte[] decode(byte[] inbuf) {
/* 417 */     int size = inbuf.length / 4 * 3;
/* 418 */     if (size == 0) {
/* 419 */       return inbuf;
/*     */     }
/* 421 */     if (inbuf[inbuf.length - 1] == 61) {
/* 422 */       size--;
/* 423 */       if (inbuf[inbuf.length - 2] == 61)
/* 424 */         size--; 
/*     */     } 
/* 426 */     byte[] outbuf = new byte[size];
/*     */     
/* 428 */     int inpos = 0, outpos = 0;
/* 429 */     size = inbuf.length;
/* 430 */     while (size > 0) {
/*     */       
/* 432 */       int osize = 3;
/* 433 */       int val = pem_convert_array[inbuf[inpos++] & 0xFF];
/* 434 */       val <<= 6;
/* 435 */       val |= pem_convert_array[inbuf[inpos++] & 0xFF];
/* 436 */       val <<= 6;
/* 437 */       if (inbuf[inpos] != 61) {
/* 438 */         val |= pem_convert_array[inbuf[inpos++] & 0xFF];
/*     */       } else {
/* 440 */         osize--;
/* 441 */       }  val <<= 6;
/* 442 */       if (inbuf[inpos] != 61) {
/* 443 */         val |= pem_convert_array[inbuf[inpos++] & 0xFF];
/*     */       } else {
/* 445 */         osize--;
/* 446 */       }  if (osize > 2)
/* 447 */         outbuf[outpos + 2] = (byte)(val & 0xFF); 
/* 448 */       val >>= 8;
/* 449 */       if (osize > 1)
/* 450 */         outbuf[outpos + 1] = (byte)(val & 0xFF); 
/* 451 */       val >>= 8;
/* 452 */       outbuf[outpos] = (byte)(val & 0xFF);
/* 453 */       outpos += osize;
/* 454 */       size -= 4;
/*     */     } 
/* 456 */     return outbuf;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\BASE64DecoderStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */