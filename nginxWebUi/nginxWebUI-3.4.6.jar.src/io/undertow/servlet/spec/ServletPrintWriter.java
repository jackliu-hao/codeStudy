/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ import java.util.Locale;
/*     */ import javax.servlet.DispatcherType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServletPrintWriter
/*     */ {
/*  41 */   private static final char[] EMPTY_CHAR = new char[0];
/*     */   
/*     */   private final ServletOutputStreamImpl outputStream;
/*     */   private final String charset;
/*     */   private CharsetEncoder charsetEncoder;
/*     */   private boolean error = false;
/*     */   private boolean closed = false;
/*     */   private char[] underflow;
/*     */   
/*     */   public ServletPrintWriter(ServletOutputStreamImpl outputStream, String charset) throws UnsupportedEncodingException {
/*  51 */     this.charset = charset;
/*  52 */     this.outputStream = outputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  57 */     if (!charset.equalsIgnoreCase("utf-8") && 
/*  58 */       !charset.equalsIgnoreCase("iso-8859-1")) {
/*  59 */       createEncoder();
/*     */     }
/*     */   }
/*     */   
/*     */   private void createEncoder() {
/*  64 */     this.charsetEncoder = Charset.forName(this.charset).newEncoder();
/*     */     
/*  66 */     this.charsetEncoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
/*  67 */     this.charsetEncoder.onMalformedInput(CodingErrorAction.REPLACE);
/*     */   }
/*     */   
/*     */   public void flush() {
/*     */     try {
/*  72 */       this.outputStream.flush();
/*  73 */     } catch (IOException e) {
/*  74 */       this.error = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*  80 */     if (this.outputStream.getServletRequestContext().getOriginalRequest().getDispatcherType() == DispatcherType.INCLUDE) {
/*     */       return;
/*     */     }
/*  83 */     if (this.closed) {
/*     */       return;
/*     */     }
/*  86 */     this.closed = true; try {
/*     */       CharBuffer buffer;
/*  88 */       boolean done = false;
/*     */       
/*  90 */       if (this.underflow == null) {
/*  91 */         buffer = CharBuffer.wrap(EMPTY_CHAR);
/*     */       } else {
/*  93 */         buffer = CharBuffer.wrap(this.underflow);
/*  94 */         this.underflow = null;
/*     */       } 
/*  96 */       if (this.charsetEncoder != null) {
/*     */         do {
/*  98 */           ByteBuffer out = this.outputStream.underlyingBuffer();
/*  99 */           if (out == null) {
/*     */             
/* 101 */             this.error = true;
/*     */             return;
/*     */           } 
/* 104 */           CoderResult result = this.charsetEncoder.encode(buffer, out, true);
/* 105 */           if (result.isOverflow()) {
/* 106 */             this.outputStream.flushInternal();
/* 107 */             if (out.remaining() == 0) {
/* 108 */               this.outputStream.close();
/* 109 */               this.error = true;
/*     */               return;
/*     */             } 
/*     */           } else {
/* 113 */             done = true;
/*     */           } 
/* 115 */         } while (!done);
/*     */       }
/* 117 */       this.outputStream.close();
/* 118 */     } catch (IOException e) {
/* 119 */       this.error = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean checkError() {
/* 124 */     flush();
/* 125 */     return this.error;
/*     */   }
/*     */   
/*     */   public void write(CharBuffer input) {
/* 129 */     ByteBuffer buffer = this.outputStream.underlyingBuffer();
/* 130 */     if (buffer == null) {
/*     */       
/* 132 */       this.error = true; return;
/*     */     } 
/*     */     try {
/*     */       CharBuffer cb;
/* 136 */       if (!buffer.hasRemaining()) {
/* 137 */         this.outputStream.flushInternal();
/* 138 */         if (!buffer.hasRemaining()) {
/* 139 */           this.error = true;
/*     */           
/*     */           return;
/*     */         } 
/*     */       } 
/* 144 */       if (this.charsetEncoder == null) {
/* 145 */         createEncoder();
/*     */       }
/*     */       
/* 148 */       if (this.underflow == null) {
/* 149 */         cb = input;
/*     */       } else {
/* 151 */         char[] newArray = new char[this.underflow.length + input.remaining()];
/* 152 */         System.arraycopy(this.underflow, 0, newArray, 0, this.underflow.length);
/* 153 */         input.get(newArray, this.underflow.length, input.remaining());
/* 154 */         cb = CharBuffer.wrap(newArray);
/* 155 */         this.underflow = null;
/*     */       } 
/* 157 */       int last = -1;
/* 158 */       while (cb.hasRemaining()) {
/* 159 */         int remaining = buffer.remaining();
/* 160 */         CoderResult result = this.charsetEncoder.encode(cb, buffer, false);
/* 161 */         this.outputStream.updateWritten((remaining - buffer.remaining()));
/* 162 */         if (result.isOverflow() || !buffer.hasRemaining()) {
/* 163 */           this.outputStream.flushInternal();
/* 164 */           if (!buffer.hasRemaining()) {
/* 165 */             this.error = true;
/*     */             return;
/*     */           } 
/*     */         } 
/* 169 */         if (result.isUnderflow()) {
/* 170 */           this.underflow = new char[cb.remaining()];
/* 171 */           cb.get(this.underflow);
/*     */           return;
/*     */         } 
/* 174 */         if (result.isError()) {
/* 175 */           this.error = true;
/*     */           return;
/*     */         } 
/* 178 */         if (result.isUnmappable()) {
/*     */           
/* 180 */           this.error = true;
/*     */           return;
/*     */         } 
/* 183 */         if (last == cb.remaining()) {
/* 184 */           this.underflow = new char[cb.remaining()];
/* 185 */           cb.get(this.underflow);
/*     */           return;
/*     */         } 
/* 188 */         last = cb.remaining();
/*     */       } 
/* 190 */     } catch (IOException e) {
/* 191 */       this.error = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void write(int c) {
/* 196 */     write(Character.toString((char)c));
/*     */   }
/*     */   
/*     */   public void write(char[] buf, int off, int len) {
/* 200 */     if (this.charsetEncoder == null) {
/*     */       try {
/* 202 */         ByteBuffer buffer = this.outputStream.underlyingBuffer();
/* 203 */         if (buffer == null) {
/*     */           
/* 205 */           this.error = true;
/*     */           
/*     */           return;
/*     */         } 
/* 209 */         int remaining = buffer.remaining();
/* 210 */         boolean ok = true;
/*     */ 
/*     */         
/* 213 */         int end = off + len;
/* 214 */         int i = off;
/* 215 */         int flushPos = i + remaining;
/* 216 */         while (ok && i < end) {
/* 217 */           int realEnd = Math.min(end, flushPos);
/* 218 */           for (; i < realEnd; i++) {
/* 219 */             char c = buf[i];
/* 220 */             if (c > '') {
/* 221 */               ok = false;
/*     */               break;
/*     */             } 
/* 224 */             buffer.put((byte)c);
/*     */           } 
/*     */           
/* 227 */           if (i == flushPos) {
/* 228 */             this.outputStream.flushInternal();
/* 229 */             flushPos = i + buffer.remaining();
/*     */           } 
/*     */         } 
/* 232 */         this.outputStream.updateWritten((remaining - buffer.remaining()));
/* 233 */         if (ok) {
/*     */           return;
/*     */         }
/* 236 */         CharBuffer charBuffer = CharBuffer.wrap(buf, i, len - i - off);
/* 237 */         write(charBuffer);
/*     */         return;
/* 239 */       } catch (IOException e) {
/* 240 */         this.error = false;
/*     */         
/*     */         return;
/*     */       } 
/*     */     }
/* 245 */     CharBuffer cb = CharBuffer.wrap(buf, off, len);
/* 246 */     write(cb);
/*     */   }
/*     */   
/*     */   public void write(char[] buf) {
/* 250 */     write(buf, 0, buf.length);
/*     */   }
/*     */   
/*     */   public void write(String s, int off, int len) {
/* 254 */     if (this.charsetEncoder == null) {
/*     */       try {
/* 256 */         ByteBuffer buffer = this.outputStream.underlyingBuffer();
/* 257 */         if (buffer == null) {
/*     */           
/* 259 */           this.error = true;
/*     */           
/*     */           return;
/*     */         } 
/* 263 */         int remaining = buffer.remaining();
/* 264 */         boolean ok = true;
/*     */ 
/*     */         
/* 267 */         int end = off + len;
/* 268 */         int i = off;
/* 269 */         int fpos = i + remaining;
/* 270 */         for (; i < end; i++) {
/* 271 */           if (i == fpos) {
/* 272 */             this.outputStream.flushInternal();
/* 273 */             fpos = i + buffer.remaining();
/*     */           } 
/* 275 */           char c = s.charAt(i);
/* 276 */           if (c > '') {
/* 277 */             ok = false;
/*     */             break;
/*     */           } 
/* 280 */           buffer.put((byte)c);
/*     */         } 
/* 282 */         this.outputStream.updateWritten((remaining - buffer.remaining()));
/* 283 */         if (ok) {
/*     */           return;
/*     */         }
/*     */         
/* 287 */         CharBuffer charBuffer = CharBuffer.wrap(s.toCharArray(), i, len - i - off);
/* 288 */         write(charBuffer);
/*     */         return;
/* 290 */       } catch (IOException e) {
/* 291 */         this.error = false;
/*     */         
/*     */         return;
/*     */       } 
/*     */     }
/* 296 */     CharBuffer cb = CharBuffer.wrap(s, off, off + len);
/* 297 */     write(cb);
/*     */   }
/*     */   
/*     */   public void write(String s) {
/* 301 */     write(s, 0, s.length());
/*     */   }
/*     */   
/*     */   public void print(boolean b) {
/* 305 */     write(Boolean.toString(b));
/*     */   }
/*     */   
/*     */   public void print(char c) {
/* 309 */     write(Character.toString(c));
/*     */   }
/*     */   
/*     */   public void print(int i) {
/* 313 */     write(Integer.toString(i));
/*     */   }
/*     */   
/*     */   public void print(long l) {
/* 317 */     write(Long.toString(l));
/*     */   }
/*     */   
/*     */   public void print(float f) {
/* 321 */     write(Float.toString(f));
/*     */   }
/*     */   
/*     */   public void print(double d) {
/* 325 */     write(Double.toString(d));
/*     */   }
/*     */   
/*     */   public void print(char[] s) {
/* 329 */     write(CharBuffer.wrap(s));
/*     */   }
/*     */   
/*     */   public void print(String s) {
/* 333 */     write((s == null) ? "null" : s);
/*     */   }
/*     */   
/*     */   public void print(Object obj) {
/* 337 */     write((obj == null) ? "null" : obj.toString());
/*     */   }
/*     */   
/*     */   public void println() {
/* 341 */     print("\r\n");
/*     */   }
/*     */   
/*     */   public void println(boolean b) {
/* 345 */     print(b);
/* 346 */     println();
/*     */   }
/*     */   
/*     */   public void println(char c) {
/* 350 */     print(c);
/* 351 */     println();
/*     */   }
/*     */   
/*     */   public void println(int i) {
/* 355 */     print(i);
/* 356 */     println();
/*     */   }
/*     */   
/*     */   public void println(long l) {
/* 360 */     print(l);
/* 361 */     println();
/*     */   }
/*     */   
/*     */   public void println(float f) {
/* 365 */     print(f);
/* 366 */     println();
/*     */   }
/*     */   
/*     */   public void println(double d) {
/* 370 */     print(d);
/* 371 */     println();
/*     */   }
/*     */   
/*     */   public void println(char[] s) {
/* 375 */     print(s);
/* 376 */     println();
/*     */   }
/*     */   
/*     */   public void println(String s) {
/* 380 */     print(s);
/* 381 */     println();
/*     */   }
/*     */   
/*     */   public void println(Object obj) {
/* 385 */     print(obj);
/* 386 */     println();
/*     */   }
/*     */   
/*     */   public void printf(String format, Object... args) {
/* 390 */     print(String.format(format, args));
/*     */   }
/*     */   
/*     */   public void printf(Locale l, String format, Object... args) {
/* 394 */     print(String.format(l, format, args));
/*     */   }
/*     */ 
/*     */   
/*     */   public void format(String format, Object... args) {
/* 399 */     printf(format, args);
/*     */   }
/*     */   
/*     */   public void format(Locale l, String format, Object... args) {
/* 403 */     printf(l, format, args);
/*     */   }
/*     */   
/*     */   public void append(CharSequence csq) {
/* 407 */     if (csq == null) {
/* 408 */       write("null");
/*     */     } else {
/* 410 */       write(csq.toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void append(CharSequence csq, int start, int end) {
/* 415 */     CharSequence cs = (csq == null) ? "null" : csq;
/* 416 */     write(cs.subSequence(start, end).toString());
/*     */   }
/*     */   
/*     */   public void append(char c) {
/* 420 */     write(c);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\ServletPrintWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */