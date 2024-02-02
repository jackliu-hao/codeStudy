/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MultipartParser
/*     */ {
/*     */   public static final byte HTAB = 9;
/*     */   public static final byte CR = 13;
/*     */   public static final byte LF = 10;
/*     */   public static final byte SP = 32;
/*     */   public static final byte DASH = 45;
/*  66 */   private static final byte[] BOUNDARY_PREFIX = new byte[] { 13, 10, 45, 45 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ParseState beginParse(ByteBufferPool bufferPool, PartHandler handler, byte[] boundary, String requestCharset) {
/*  80 */     byte[] boundaryToken = new byte[boundary.length + BOUNDARY_PREFIX.length];
/*  81 */     System.arraycopy(BOUNDARY_PREFIX, 0, boundaryToken, 0, BOUNDARY_PREFIX.length);
/*  82 */     System.arraycopy(boundary, 0, boundaryToken, BOUNDARY_PREFIX.length, boundary.length);
/*  83 */     return new ParseState(bufferPool, handler, requestCharset, boundaryToken);
/*     */   }
/*     */   
/*     */   public static interface PartHandler {
/*     */     void beginPart(HeaderMap param1HeaderMap);
/*     */     
/*     */     void data(ByteBuffer param1ByteBuffer) throws IOException;
/*     */     
/*     */     void endPart(); }
/*     */   
/*     */   public static class ParseState {
/*     */     private final ByteBufferPool bufferPool;
/*     */     private final MultipartParser.PartHandler partHandler;
/*  96 */     private int state = 0; private String requestCharset; private final byte[] boundary;
/*  97 */     private int subState = Integer.MAX_VALUE;
/*  98 */     private ByteArrayOutputStream currentString = null;
/*  99 */     private String currentHeaderName = null;
/*     */     
/*     */     private HeaderMap headers;
/*     */     private MultipartParser.Encoding encodingHandler;
/*     */     
/*     */     public ParseState(ByteBufferPool bufferPool, MultipartParser.PartHandler partHandler, String requestCharset, byte[] boundary) {
/* 105 */       this.bufferPool = bufferPool;
/* 106 */       this.partHandler = partHandler;
/* 107 */       this.requestCharset = requestCharset;
/* 108 */       this.boundary = boundary;
/*     */     }
/*     */     
/*     */     public void setCharacterEncoding(String encoding) {
/* 112 */       this.requestCharset = encoding;
/*     */     }
/*     */     
/*     */     public void parse(ByteBuffer buffer) throws IOException {
/* 116 */       while (buffer.hasRemaining()) {
/* 117 */         switch (this.state) {
/*     */           case 0:
/* 119 */             preamble(buffer);
/*     */             continue;
/*     */           
/*     */           case 1:
/* 123 */             headerName(buffer);
/*     */             continue;
/*     */           
/*     */           case 2:
/* 127 */             headerValue(buffer);
/*     */             continue;
/*     */           
/*     */           case 3:
/* 131 */             entity(buffer);
/*     */             continue;
/*     */           
/*     */           case -1:
/*     */             return;
/*     */         } 
/*     */         
/* 138 */         throw new IllegalStateException("" + this.state);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private void preamble(ByteBuffer buffer) {
/* 145 */       while (buffer.hasRemaining()) {
/* 146 */         byte b = buffer.get();
/* 147 */         if (this.subState >= 0) {
/*     */           
/* 149 */           if (this.subState == Integer.MAX_VALUE) {
/* 150 */             if (this.boundary[2] == b) {
/* 151 */               this.subState = 2;
/*     */             } else {
/* 153 */               this.subState = 0;
/*     */             } 
/*     */           }
/* 156 */           if (b == this.boundary[this.subState]) {
/* 157 */             this.subState++;
/* 158 */             if (this.subState == this.boundary.length)
/* 159 */               this.subState = -1;  continue;
/*     */           } 
/* 161 */           if (b == this.boundary[0]) {
/* 162 */             this.subState = 1; continue;
/*     */           } 
/* 164 */           this.subState = 0; continue;
/*     */         } 
/* 166 */         if (this.subState == -1) {
/* 167 */           if (b == 13)
/* 168 */             this.subState = -2;  continue;
/*     */         } 
/* 170 */         if (this.subState == -2) {
/* 171 */           if (b == 10) {
/* 172 */             this.subState = 0;
/* 173 */             this.state = 1;
/* 174 */             this.headers = new HeaderMap();
/*     */             return;
/*     */           } 
/* 177 */           this.subState = -1;
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private void headerName(ByteBuffer buffer) throws MalformedMessageException, UnsupportedEncodingException {
/* 184 */       while (buffer.hasRemaining()) {
/* 185 */         byte b = buffer.get();
/* 186 */         if (b == 58) {
/* 187 */           if (this.currentString == null || this.subState != 0) {
/* 188 */             throw new MalformedMessageException();
/*     */           }
/* 190 */           this.currentHeaderName = new String(this.currentString.toByteArray(), this.requestCharset);
/* 191 */           this.currentString.reset();
/* 192 */           this.subState = 0;
/* 193 */           this.state = 2;
/*     */           return;
/*     */         } 
/* 196 */         if (b == 13) {
/* 197 */           if (this.currentString != null) {
/* 198 */             throw new MalformedMessageException();
/*     */           }
/* 200 */           this.subState = 1; continue;
/*     */         } 
/* 202 */         if (b == 10) {
/* 203 */           if (this.currentString != null || this.subState != 1) {
/* 204 */             throw new MalformedMessageException();
/*     */           }
/* 206 */           this.state = 3;
/* 207 */           this.subState = 0;
/* 208 */           this.partHandler.beginPart(this.headers);
/*     */           
/* 210 */           String encoding = this.headers.getFirst(Headers.CONTENT_TRANSFER_ENCODING);
/* 211 */           if (encoding == null) {
/* 212 */             this.encodingHandler = new MultipartParser.IdentityEncoding();
/* 213 */           } else if (encoding.equalsIgnoreCase("base64")) {
/* 214 */             this.encodingHandler = new MultipartParser.Base64Encoding(this.bufferPool);
/* 215 */           } else if (encoding.equalsIgnoreCase("quoted-printable")) {
/* 216 */             this.encodingHandler = new MultipartParser.QuotedPrintableEncoding(this.bufferPool);
/*     */           } else {
/* 218 */             this.encodingHandler = new MultipartParser.IdentityEncoding();
/*     */           } 
/* 220 */           this.headers = null;
/*     */           
/*     */           return;
/*     */         } 
/* 224 */         if (this.subState != 0)
/* 225 */           throw new MalformedMessageException(); 
/* 226 */         if (this.currentString == null) {
/* 227 */           this.currentString = new ByteArrayOutputStream();
/*     */         }
/* 229 */         this.currentString.write(b);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private void headerValue(ByteBuffer buffer) throws MalformedMessageException, UnsupportedEncodingException {
/* 235 */       while (buffer.hasRemaining()) {
/* 236 */         byte b = buffer.get();
/* 237 */         if (this.subState == 2) {
/* 238 */           if (b == 13) {
/* 239 */             this.headers.put(new HttpString(this.currentHeaderName.trim()), (new String(this.currentString.toByteArray(), this.requestCharset)).trim());
/*     */             
/* 241 */             this.state = 1;
/* 242 */             this.subState = 1;
/* 243 */             this.currentString = null; return;
/*     */           } 
/* 245 */           if (b == 32 || b == 9) {
/* 246 */             this.currentString.write(b);
/* 247 */             this.subState = 0; continue;
/*     */           } 
/* 249 */           this.headers.put(new HttpString(this.currentHeaderName.trim()), (new String(this.currentString.toByteArray(), this.requestCharset)).trim());
/*     */           
/* 251 */           this.state = 1;
/* 252 */           this.subState = 0;
/*     */           
/* 254 */           this.currentString = new ByteArrayOutputStream();
/* 255 */           this.currentString.write(b);
/*     */           return;
/*     */         } 
/* 258 */         if (b == 13) {
/* 259 */           this.subState = 1; continue;
/* 260 */         }  if (b == 10) {
/* 261 */           if (this.subState != 1) {
/* 262 */             throw new MalformedMessageException();
/*     */           }
/* 264 */           this.subState = 2; continue;
/*     */         } 
/* 266 */         if (this.subState != 0) {
/* 267 */           throw new MalformedMessageException();
/*     */         }
/* 269 */         this.currentString.write(b);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private void entity(ByteBuffer buffer) throws IOException {
/* 275 */       int startingSubState = this.subState;
/* 276 */       int pos = buffer.position();
/* 277 */       while (buffer.hasRemaining()) {
/* 278 */         byte b = buffer.get();
/* 279 */         if (this.subState >= 0) {
/* 280 */           if (b == this.boundary[this.subState]) {
/*     */             
/* 282 */             this.subState++;
/* 283 */             if (this.subState == this.boundary.length) {
/* 284 */               startingSubState = 0;
/*     */               
/* 286 */               ByteBuffer byteBuffer = buffer.duplicate();
/* 287 */               byteBuffer.position(pos);
/*     */               
/* 289 */               byteBuffer.limit(Math.max(buffer.position() - this.boundary.length, 0));
/* 290 */               this.encodingHandler.handle(this.partHandler, byteBuffer);
/* 291 */               this.partHandler.endPart();
/* 292 */               this.subState = -1;
/*     */             }  continue;
/* 294 */           }  if (b == this.boundary[0]) {
/*     */ 
/*     */             
/* 297 */             if (startingSubState > 0) {
/* 298 */               this.encodingHandler.handle(this.partHandler, ByteBuffer.wrap(this.boundary, 0, startingSubState));
/* 299 */               startingSubState = 0;
/*     */             } 
/* 301 */             this.subState = 1;
/*     */             
/*     */             continue;
/*     */           } 
/* 305 */           if (startingSubState > 0) {
/* 306 */             this.encodingHandler.handle(this.partHandler, ByteBuffer.wrap(this.boundary, 0, startingSubState));
/* 307 */             startingSubState = 0;
/*     */           } 
/* 309 */           this.subState = 0; continue;
/*     */         } 
/* 311 */         if (this.subState == -1) {
/* 312 */           if (b == 13) {
/* 313 */             this.subState = -2; continue;
/* 314 */           }  if (b == 45)
/* 315 */             this.subState = -3;  continue;
/*     */         } 
/* 317 */         if (this.subState == -2) {
/* 318 */           if (b == 10) {
/*     */             
/* 320 */             this.subState = 0;
/* 321 */             this.state = 1;
/* 322 */             this.headers = new HeaderMap(); return;
/*     */           } 
/* 324 */           if (b == 45) {
/* 325 */             this.subState = -3; continue;
/*     */           } 
/* 327 */           this.subState = -1; continue;
/*     */         } 
/* 329 */         if (this.subState == -3) {
/* 330 */           if (b == 45) {
/* 331 */             this.state = -1;
/*     */             return;
/*     */           } 
/* 334 */           this.subState = -1;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 339 */       ByteBuffer retBuffer = buffer.duplicate();
/* 340 */       retBuffer.position(pos);
/* 341 */       if (this.subState == 0) {
/*     */         
/* 343 */         this.encodingHandler.handle(this.partHandler, retBuffer);
/* 344 */       } else if (retBuffer.remaining() > this.subState && this.subState > 0) {
/*     */         
/* 346 */         retBuffer.limit(retBuffer.limit() - this.subState);
/* 347 */         this.encodingHandler.handle(this.partHandler, retBuffer);
/*     */       } 
/*     */     }
/*     */     
/*     */     public boolean isComplete() {
/* 352 */       return (this.state == -1);
/*     */     }
/*     */   }
/*     */   
/*     */   private static interface Encoding {
/*     */     void handle(MultipartParser.PartHandler param1PartHandler, ByteBuffer param1ByteBuffer) throws IOException;
/*     */   }
/*     */   
/*     */   private static class IdentityEncoding
/*     */     implements Encoding {
/*     */     private IdentityEncoding() {}
/*     */     
/*     */     public void handle(MultipartParser.PartHandler handler, ByteBuffer rawData) throws IOException {
/* 365 */       handler.data(rawData);
/* 366 */       rawData.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Base64Encoding
/*     */     implements Encoding {
/* 372 */     private final FlexBase64.Decoder decoder = FlexBase64.createDecoder();
/*     */     
/*     */     private final ByteBufferPool bufferPool;
/*     */     
/*     */     private Base64Encoding(ByteBufferPool bufferPool) {
/* 377 */       this.bufferPool = bufferPool;
/*     */     }
/*     */ 
/*     */     
/*     */     public void handle(MultipartParser.PartHandler handler, ByteBuffer rawData) throws IOException {
/* 382 */       PooledByteBuffer resource = this.bufferPool.allocate();
/* 383 */       ByteBuffer buf = resource.getBuffer();
/*     */       try {
/*     */         do {
/* 386 */           buf.clear();
/*     */           try {
/* 388 */             this.decoder.decode(rawData, buf);
/* 389 */           } catch (IOException e) {
/* 390 */             throw new RuntimeException(e);
/*     */           } 
/* 392 */           buf.flip();
/* 393 */           handler.data(buf);
/* 394 */         } while (rawData.hasRemaining());
/*     */       } finally {
/* 396 */         resource.close();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static class QuotedPrintableEncoding
/*     */     implements Encoding {
/*     */     private final ByteBufferPool bufferPool;
/*     */     boolean equalsSeen;
/*     */     byte firstCharacter;
/*     */     
/*     */     private QuotedPrintableEncoding(ByteBufferPool bufferPool) {
/* 408 */       this.bufferPool = bufferPool;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void handle(MultipartParser.PartHandler handler, ByteBuffer rawData) throws IOException {
/* 414 */       boolean equalsSeen = this.equalsSeen;
/* 415 */       byte firstCharacter = this.firstCharacter;
/* 416 */       PooledByteBuffer resource = this.bufferPool.allocate();
/* 417 */       ByteBuffer buf = resource.getBuffer();
/*     */       try {
/* 419 */         while (rawData.hasRemaining()) {
/* 420 */           byte b = rawData.get();
/* 421 */           if (equalsSeen) {
/* 422 */             if (firstCharacter == 0) {
/* 423 */               if (b == 10 || b == 13) {
/*     */ 
/*     */                 
/* 426 */                 equalsSeen = false; continue;
/*     */               } 
/* 428 */               firstCharacter = b;
/*     */               continue;
/*     */             } 
/* 431 */             int result = Character.digit((char)firstCharacter, 16);
/* 432 */             result <<= 4;
/* 433 */             result += Character.digit((char)b, 16);
/* 434 */             buf.put((byte)result);
/* 435 */             equalsSeen = false;
/* 436 */             firstCharacter = 0; continue;
/*     */           } 
/* 438 */           if (b == 61) {
/* 439 */             equalsSeen = true; continue;
/*     */           } 
/* 441 */           buf.put(b);
/* 442 */           if (!buf.hasRemaining()) {
/* 443 */             buf.flip();
/* 444 */             handler.data(buf);
/* 445 */             buf.clear();
/*     */           } 
/*     */         } 
/*     */         
/* 449 */         buf.flip();
/* 450 */         handler.data(buf);
/*     */       } finally {
/* 452 */         resource.close();
/* 453 */         this.equalsSeen = equalsSeen;
/* 454 */         this.firstCharacter = firstCharacter;
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\MultipartParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */