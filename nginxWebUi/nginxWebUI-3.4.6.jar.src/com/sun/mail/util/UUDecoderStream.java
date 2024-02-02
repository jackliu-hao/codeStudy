/*     */ package com.sun.mail.util;
/*     */ 
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
/*     */ 
/*     */ public class UUDecoderStream
/*     */   extends FilterInputStream
/*     */ {
/*     */   private String name;
/*     */   private int mode;
/*  59 */   private byte[] buffer = new byte[45];
/*  60 */   private int bufsize = 0;
/*  61 */   private int index = 0;
/*     */ 
/*     */   
/*     */   private boolean gotPrefix = false;
/*     */ 
/*     */   
/*     */   private boolean gotEnd = false;
/*     */ 
/*     */   
/*     */   private LineInputStream lin;
/*     */   
/*     */   private boolean ignoreErrors;
/*     */   
/*     */   private boolean ignoreMissingBeginEnd;
/*     */   
/*     */   private String readAhead;
/*     */ 
/*     */   
/*     */   public UUDecoderStream(InputStream in) {
/*  80 */     super(in);
/*  81 */     this.lin = new LineInputStream(in);
/*     */     
/*  83 */     this.ignoreErrors = PropUtil.getBooleanSystemProperty("mail.mime.uudecode.ignoreerrors", false);
/*     */ 
/*     */     
/*  86 */     this.ignoreMissingBeginEnd = PropUtil.getBooleanSystemProperty("mail.mime.uudecode.ignoremissingbeginend", false);
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
/*     */   public UUDecoderStream(InputStream in, boolean ignoreErrors, boolean ignoreMissingBeginEnd) {
/*  98 */     super(in);
/*  99 */     this.lin = new LineInputStream(in);
/* 100 */     this.ignoreErrors = ignoreErrors;
/* 101 */     this.ignoreMissingBeginEnd = ignoreMissingBeginEnd;
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
/* 118 */     if (this.index >= this.bufsize) {
/* 119 */       readPrefix();
/* 120 */       if (!decode())
/* 121 */         return -1; 
/* 122 */       this.index = 0;
/*     */     } 
/* 124 */     return this.buffer[this.index++] & 0xFF;
/*     */   }
/*     */   
/*     */   public int read(byte[] buf, int off, int len) throws IOException {
/*     */     int i;
/* 129 */     for (i = 0; i < len; i++) {
/* 130 */       int c; if ((c = read()) == -1) {
/* 131 */         if (i == 0)
/* 132 */           i = -1; 
/*     */         break;
/*     */       } 
/* 135 */       buf[off + i] = (byte)c;
/*     */     } 
/* 137 */     return i;
/*     */   }
/*     */   
/*     */   public boolean markSupported() {
/* 141 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 147 */     return this.in.available() * 3 / 4 + this.bufsize - this.index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() throws IOException {
/* 158 */     readPrefix();
/* 159 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMode() throws IOException {
/* 170 */     readPrefix();
/* 171 */     return this.mode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readPrefix() throws IOException {
/* 180 */     if (this.gotPrefix) {
/*     */       return;
/*     */     }
/* 183 */     this.mode = 438;
/* 184 */     this.name = "encoder.buf";
/*     */ 
/*     */     
/*     */     while (true) {
/* 188 */       String line = this.lin.readLine();
/* 189 */       if (line == null) {
/* 190 */         if (!this.ignoreMissingBeginEnd) {
/* 191 */           throw new DecodingException("UUDecoder: Missing begin");
/*     */         }
/* 193 */         this.gotPrefix = true;
/* 194 */         this.gotEnd = true;
/*     */         break;
/*     */       } 
/* 197 */       if (line.regionMatches(false, 0, "begin", 0, 5)) {
/*     */         try {
/* 199 */           this.mode = Integer.parseInt(line.substring(6, 9));
/* 200 */         } catch (NumberFormatException ex) {
/* 201 */           if (!this.ignoreErrors) {
/* 202 */             throw new DecodingException("UUDecoder: Error in mode: " + ex.toString());
/*     */           }
/*     */         } 
/* 205 */         if (line.length() > 10) {
/* 206 */           this.name = line.substring(10);
/*     */         }
/* 208 */         else if (!this.ignoreErrors) {
/* 209 */           throw new DecodingException("UUDecoder: Missing name: " + line);
/*     */         } 
/*     */         
/* 212 */         this.gotPrefix = true; break;
/*     */       } 
/* 214 */       if (this.ignoreMissingBeginEnd && line.length() != 0) {
/* 215 */         int count = line.charAt(0);
/* 216 */         count = count - 32 & 0x3F;
/* 217 */         int need = (count * 8 + 5) / 6;
/* 218 */         if (need == 0 || line.length() >= need + 1) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 225 */           this.readAhead = line;
/* 226 */           this.gotPrefix = true;
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean decode() throws IOException {
/*     */     String line;
/* 235 */     if (this.gotEnd)
/* 236 */       return false; 
/* 237 */     this.bufsize = 0;
/* 238 */     int count = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     while (true) {
/* 245 */       if (this.readAhead != null) {
/* 246 */         line = this.readAhead;
/* 247 */         this.readAhead = null;
/*     */       } else {
/* 249 */         line = this.lin.readLine();
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 256 */       if (line == null) {
/* 257 */         if (!this.ignoreMissingBeginEnd) {
/* 258 */           throw new DecodingException("UUDecoder: Missing end at EOF");
/*     */         }
/* 260 */         this.gotEnd = true;
/* 261 */         return false;
/*     */       } 
/* 263 */       if (line.equals("end")) {
/* 264 */         this.gotEnd = true;
/* 265 */         return false;
/*     */       } 
/* 267 */       if (line.length() == 0)
/*     */         continue; 
/* 269 */       count = line.charAt(0);
/* 270 */       if (count < 32) {
/* 271 */         if (!this.ignoreErrors) {
/* 272 */           throw new DecodingException("UUDecoder: Buffer format error");
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         continue;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 283 */       count = count - 32 & 0x3F;
/*     */       
/* 285 */       if (count == 0) {
/* 286 */         line = this.lin.readLine();
/* 287 */         if ((line == null || !line.equals("end")) && 
/* 288 */           !this.ignoreMissingBeginEnd) {
/* 289 */           throw new DecodingException("UUDecoder: Missing End after count 0 line");
/*     */         }
/*     */         
/* 292 */         this.gotEnd = true;
/* 293 */         return false;
/*     */       } 
/*     */       
/* 296 */       int need = (count * 8 + 5) / 6;
/*     */       
/* 298 */       if (line.length() < need + 1) {
/* 299 */         if (!this.ignoreErrors) {
/* 300 */           throw new DecodingException("UUDecoder: Short buffer error");
/*     */         }
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/*     */       break;
/*     */     } 
/*     */     
/* 309 */     int i = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 317 */     while (this.bufsize < count) {
/*     */       
/* 319 */       byte a = (byte)(line.charAt(i++) - 32 & 0x3F);
/* 320 */       byte b = (byte)(line.charAt(i++) - 32 & 0x3F);
/* 321 */       this.buffer[this.bufsize++] = (byte)(a << 2 & 0xFC | b >>> 4 & 0x3);
/*     */       
/* 323 */       if (this.bufsize < count) {
/* 324 */         a = b;
/* 325 */         b = (byte)(line.charAt(i++) - 32 & 0x3F);
/* 326 */         this.buffer[this.bufsize++] = (byte)(a << 4 & 0xF0 | b >>> 2 & 0xF);
/*     */       } 
/*     */ 
/*     */       
/* 330 */       if (this.bufsize < count) {
/* 331 */         a = b;
/* 332 */         b = (byte)(line.charAt(i++) - 32 & 0x3F);
/* 333 */         this.buffer[this.bufsize++] = (byte)(a << 6 & 0xC0 | b & 0x3F);
/*     */       } 
/*     */     } 
/* 336 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\UUDecoderStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */