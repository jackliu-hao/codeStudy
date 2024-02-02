/*     */ package io.undertow.server.protocol.http;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Protocols;
/*     */ import io.undertow.util.StatusCodes;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.AbstractStreamSinkConduit;
/*     */ import org.xnio.conduits.ConduitWritableByteChannel;
/*     */ import org.xnio.conduits.Conduits;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class HttpResponseConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*     */   private final ByteBufferPool pool;
/*     */   private final HttpServerConnection connection;
/*  57 */   private int state = 1;
/*     */   
/*  59 */   private long fiCookie = -1L;
/*     */   
/*     */   private String string;
/*     */   
/*     */   private HeaderValues headerValues;
/*     */   
/*     */   private int valueIdx;
/*     */   private int charIndex;
/*     */   private PooledByteBuffer pooledBuffer;
/*     */   private PooledByteBuffer pooledFileTransferBuffer;
/*     */   private HttpServerExchange exchange;
/*     */   private ByteBuffer[] writevBuffer;
/*     */   private boolean done = false;
/*     */   private static final int STATE_BODY = 0;
/*     */   private static final int STATE_START = 1;
/*     */   private static final int STATE_HDR_NAME = 2;
/*     */   private static final int STATE_HDR_D = 3;
/*     */   private static final int STATE_HDR_DS = 4;
/*     */   private static final int STATE_HDR_VAL = 5;
/*     */   private static final int STATE_HDR_EOL_CR = 6;
/*     */   private static final int STATE_HDR_EOL_LF = 7;
/*     */   private static final int STATE_HDR_FINAL_CR = 8;
/*     */   private static final int STATE_HDR_FINAL_LF = 9;
/*     */   private static final int STATE_BUF_FLUSH = 10;
/*     */   private static final int MASK_STATE = 15;
/*     */   private static final int FLAG_SHUTDOWN = 16;
/*     */   
/*     */   HttpResponseConduit(StreamSinkConduit next, ByteBufferPool pool, HttpServerConnection connection) {
/*  87 */     super(next);
/*  88 */     this.pool = pool;
/*  89 */     this.connection = connection;
/*     */   }
/*     */   
/*     */   HttpResponseConduit(StreamSinkConduit next, ByteBufferPool pool, HttpServerConnection connection, HttpServerExchange exchange) {
/*  93 */     super(next);
/*  94 */     this.pool = pool;
/*  95 */     this.connection = connection;
/*  96 */     this.exchange = exchange;
/*     */   }
/*     */   
/*     */   void reset(HttpServerExchange exchange) {
/* 100 */     this.exchange = exchange;
/* 101 */     this.state = 1;
/* 102 */     this.fiCookie = -1L;
/* 103 */     this.string = null;
/* 104 */     this.headerValues = null;
/* 105 */     this.valueIdx = 0;
/* 106 */     this.charIndex = 0;
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
/*     */   private int processWrite(int state, Object userData, int pos, int length) throws IOException {
/* 123 */     if (this.done || this.exchange == null) {
/* 124 */       throw new ClosedChannelException();
/*     */     }
/*     */     try {
/* 127 */       assert state != 0;
/* 128 */       if (state == 10) {
/* 129 */         ByteBuffer byteBuffer = this.pooledBuffer.getBuffer();
/*     */         while (true)
/* 131 */         { long res = 0L;
/*     */           
/* 133 */           if (userData == null || length == 0) {
/* 134 */             res = ((StreamSinkConduit)this.next).write(byteBuffer);
/* 135 */           } else if (userData instanceof ByteBuffer) {
/* 136 */             ByteBuffer[] data = this.writevBuffer;
/* 137 */             if (data == null) {
/* 138 */               data = this.writevBuffer = new ByteBuffer[2];
/*     */             }
/* 140 */             data[0] = byteBuffer;
/* 141 */             data[1] = (ByteBuffer)userData;
/* 142 */             res = ((StreamSinkConduit)this.next).write(data, 0, 2);
/*     */           } else {
/* 144 */             ByteBuffer[] data = this.writevBuffer;
/* 145 */             if (data == null || data.length < length + 1) {
/* 146 */               data = this.writevBuffer = new ByteBuffer[length + 1];
/*     */             }
/* 148 */             data[0] = byteBuffer;
/* 149 */             System.arraycopy(userData, pos, data, 1, length);
/* 150 */             res = ((StreamSinkConduit)this.next).write(data, 0, length + 1);
/*     */           } 
/* 152 */           if (res == 0L) {
/* 153 */             return 10;
/*     */           }
/* 155 */           if (!byteBuffer.hasRemaining())
/* 156 */           { bufferDone();
/* 157 */             return 0; }  } 
/* 158 */       }  if (state != 1) {
/* 159 */         return processStatefulWrite(state, userData, pos, length);
/*     */       }
/*     */ 
/*     */       
/* 163 */       Connectors.flattenCookies(this.exchange);
/*     */       
/* 165 */       if (this.pooledBuffer == null) {
/* 166 */         this.pooledBuffer = this.pool.allocate();
/*     */       }
/* 168 */       ByteBuffer buffer = this.pooledBuffer.getBuffer();
/*     */ 
/*     */       
/* 171 */       assert buffer.remaining() >= 50;
/* 172 */       Protocols.HTTP_1_1.appendTo(buffer);
/* 173 */       buffer.put((byte)32);
/* 174 */       int code = this.exchange.getStatusCode();
/* 175 */       assert 999 >= code && code >= 100;
/* 176 */       buffer.put((byte)(code / 100 + 48));
/* 177 */       buffer.put((byte)(code / 10 % 10 + 48));
/* 178 */       buffer.put((byte)(code % 10 + 48));
/* 179 */       buffer.put((byte)32);
/*     */       
/* 181 */       String string = this.exchange.getReasonPhrase();
/* 182 */       if (string == null) {
/* 183 */         string = StatusCodes.getReason(code);
/*     */       }
/* 185 */       if (string.length() > buffer.remaining()) {
/* 186 */         this.pooledBuffer.close();
/* 187 */         this.pooledBuffer = null;
/* 188 */         truncateWrites();
/* 189 */         throw UndertowMessages.MESSAGES.reasonPhraseToLargeForBuffer(string);
/*     */       } 
/* 191 */       writeString(buffer, string);
/* 192 */       buffer.put((byte)13).put((byte)10);
/*     */       
/* 194 */       int remaining = buffer.remaining();
/*     */ 
/*     */       
/* 197 */       HeaderMap headers = this.exchange.getResponseHeaders();
/* 198 */       long fiCookie = headers.fastIterateNonEmpty();
/* 199 */       while (fiCookie != -1L) {
/* 200 */         HeaderValues headerValues = headers.fiCurrent(fiCookie);
/*     */         
/* 202 */         HttpString header = headerValues.getHeaderName();
/* 203 */         int headerSize = header.length();
/* 204 */         int valueIdx = 0;
/* 205 */         while (valueIdx < headerValues.size()) {
/* 206 */           remaining -= headerSize + 2;
/*     */           
/* 208 */           if (remaining < 0) {
/* 209 */             this.fiCookie = fiCookie;
/* 210 */             this.string = string;
/* 211 */             this.headerValues = headerValues;
/* 212 */             this.valueIdx = valueIdx;
/* 213 */             this.charIndex = 0;
/* 214 */             this.state = 2;
/* 215 */             buffer.flip();
/* 216 */             return processStatefulWrite(2, userData, pos, length);
/*     */           } 
/* 218 */           header.appendTo(buffer);
/* 219 */           buffer.put((byte)58).put((byte)32);
/* 220 */           string = headerValues.get(valueIdx++);
/*     */           
/* 222 */           remaining -= string.length() + 2;
/* 223 */           if (remaining < 2) {
/* 224 */             this.fiCookie = fiCookie;
/* 225 */             this.string = string;
/* 226 */             this.headerValues = headerValues;
/* 227 */             this.valueIdx = valueIdx;
/* 228 */             this.charIndex = 0;
/* 229 */             this.state = 5;
/* 230 */             buffer.flip();
/* 231 */             return processStatefulWrite(5, userData, pos, length);
/*     */           } 
/* 233 */           writeString(buffer, string);
/* 234 */           buffer.put((byte)13).put((byte)10);
/*     */         } 
/* 236 */         fiCookie = headers.fiNextNonEmpty(fiCookie);
/*     */       } 
/* 238 */       buffer.put((byte)13).put((byte)10);
/* 239 */       buffer.flip();
/*     */       while (true)
/* 241 */       { long res = 0L;
/*     */         
/* 243 */         if (userData == null) {
/* 244 */           res = ((StreamSinkConduit)this.next).write(buffer);
/* 245 */         } else if (userData instanceof ByteBuffer) {
/* 246 */           ByteBuffer[] data = this.writevBuffer;
/* 247 */           if (data == null) {
/* 248 */             data = this.writevBuffer = new ByteBuffer[2];
/*     */           }
/* 250 */           data[0] = buffer;
/* 251 */           data[1] = (ByteBuffer)userData;
/* 252 */           res = ((StreamSinkConduit)this.next).write(data, 0, 2);
/*     */         } else {
/* 254 */           ByteBuffer[] data = this.writevBuffer;
/* 255 */           if (data == null || data.length < length + 1) {
/* 256 */             data = this.writevBuffer = new ByteBuffer[length + 1];
/*     */           }
/* 258 */           data[0] = buffer;
/* 259 */           System.arraycopy(userData, pos, data, 1, length);
/* 260 */           res = ((StreamSinkConduit)this.next).write(data, 0, length + 1);
/*     */         } 
/* 262 */         if (res == 0L) {
/* 263 */           return 10;
/*     */         }
/* 265 */         if (!buffer.hasRemaining())
/* 266 */         { bufferDone();
/* 267 */           return 0; }  } 
/* 268 */     } catch (IOException|RuntimeException|Error e) {
/*     */       
/* 270 */       if (this.pooledBuffer != null) {
/* 271 */         this.pooledBuffer.close();
/* 272 */         this.pooledBuffer = null;
/*     */       } 
/* 274 */       throw e;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void bufferDone() {
/* 279 */     if (this.exchange == null) {
/*     */       return;
/*     */     }
/* 282 */     HttpServerConnection connection = (HttpServerConnection)this.exchange.getConnection();
/* 283 */     if (connection.getExtraBytes() != null && connection.isOpen() && this.exchange.isRequestComplete()) {
/*     */       
/* 285 */       this.pooledBuffer.getBuffer().clear();
/*     */     } else {
/*     */       
/* 288 */       this.pooledBuffer.close();
/* 289 */       this.pooledBuffer = null;
/* 290 */       this.exchange = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void freeContinueResponse() {
/* 295 */     if (this.pooledBuffer != null) {
/* 296 */       this.pooledBuffer.close();
/* 297 */       this.pooledBuffer = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void writeString(ByteBuffer buffer, String string) {
/* 302 */     int length = string.length();
/* 303 */     for (int charIndex = 0; charIndex < length; charIndex++) {
/* 304 */       char c = string.charAt(charIndex);
/* 305 */       byte b = (byte)c;
/* 306 */       if (b != 13 && b != 10) {
/* 307 */         buffer.put(b);
/*     */       } else {
/* 309 */         buffer.put((byte)32);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int processStatefulWrite(int state, Object userData, int pos, int len) throws IOException {
/* 319 */     ByteBuffer buffer = this.pooledBuffer.getBuffer();
/* 320 */     long fiCookie = this.fiCookie;
/* 321 */     int valueIdx = this.valueIdx;
/* 322 */     int charIndex = this.charIndex;
/*     */     
/* 324 */     String string = this.string;
/* 325 */     HeaderValues headerValues = this.headerValues;
/*     */ 
/*     */     
/* 328 */     if (buffer.hasRemaining()) {
/*     */       do {
/* 330 */         int res = ((StreamSinkConduit)this.next).write(buffer);
/* 331 */         if (res == 0) {
/* 332 */           return state;
/*     */         }
/* 334 */       } while (buffer.hasRemaining());
/*     */     }
/* 336 */     buffer.clear();
/* 337 */     HeaderMap headers = this.exchange.getResponseHeaders(); while (true) {
/*     */       int length;
/*     */       HttpString headerName;
/* 340 */       switch (state) {
/*     */         case 2:
/* 342 */           headerName = headerValues.getHeaderName();
/* 343 */           length = headerName.length();
/* 344 */           while (charIndex < length) {
/* 345 */             if (buffer.hasRemaining()) {
/* 346 */               buffer.put(headerName.byteAt(charIndex++)); continue;
/*     */             } 
/* 348 */             buffer.flip();
/*     */             while (true) {
/* 350 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 351 */               if (res == 0) {
/* 352 */                 this.string = string;
/* 353 */                 this.headerValues = headerValues;
/* 354 */                 this.charIndex = charIndex;
/* 355 */                 this.fiCookie = fiCookie;
/* 356 */                 this.valueIdx = valueIdx;
/* 357 */                 return 2;
/*     */               } 
/* 359 */               if (!buffer.hasRemaining()) {
/* 360 */                 buffer.clear();
/*     */               }
/*     */             } 
/*     */           } 
/*     */         
/*     */         case 3:
/* 366 */           if (!buffer.hasRemaining()) {
/* 367 */             buffer.flip();
/*     */             while (true) {
/* 369 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 370 */               if (res == 0) {
/* 371 */                 this.string = string;
/* 372 */                 this.headerValues = headerValues;
/* 373 */                 this.charIndex = charIndex;
/* 374 */                 this.fiCookie = fiCookie;
/* 375 */                 this.valueIdx = valueIdx;
/* 376 */                 return 3;
/*     */               } 
/* 378 */               if (!buffer.hasRemaining())
/* 379 */               { buffer.clear(); break; } 
/*     */             } 
/* 381 */           }  buffer.put((byte)58);
/*     */ 
/*     */         
/*     */         case 4:
/* 385 */           if (!buffer.hasRemaining()) {
/* 386 */             buffer.flip();
/*     */             while (true) {
/* 388 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 389 */               if (res == 0) {
/* 390 */                 this.string = string;
/* 391 */                 this.headerValues = headerValues;
/* 392 */                 this.charIndex = charIndex;
/* 393 */                 this.fiCookie = fiCookie;
/* 394 */                 this.valueIdx = valueIdx;
/* 395 */                 return 4;
/*     */               } 
/* 397 */               if (!buffer.hasRemaining())
/* 398 */               { buffer.clear(); break; } 
/*     */             } 
/* 400 */           }  buffer.put((byte)32);
/* 401 */           string = headerValues.get(valueIdx++);
/* 402 */           charIndex = 0;
/*     */ 
/*     */         
/*     */         case 5:
/* 406 */           length = string.length();
/* 407 */           while (charIndex < length) {
/* 408 */             if (buffer.hasRemaining()) {
/* 409 */               buffer.put((byte)string.charAt(charIndex++)); continue;
/*     */             } 
/* 411 */             buffer.flip();
/*     */             while (true) {
/* 413 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 414 */               if (res == 0) {
/* 415 */                 this.string = string;
/* 416 */                 this.headerValues = headerValues;
/* 417 */                 this.charIndex = charIndex;
/* 418 */                 this.fiCookie = fiCookie;
/* 419 */                 this.valueIdx = valueIdx;
/* 420 */                 return 5;
/*     */               } 
/* 422 */               if (!buffer.hasRemaining())
/* 423 */                 buffer.clear(); 
/*     */             } 
/*     */           } 
/* 426 */           charIndex = 0;
/* 427 */           if (valueIdx == headerValues.size()) {
/* 428 */             if (!buffer.hasRemaining() && 
/* 429 */               flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
/* 430 */               return 6;
/*     */             }
/* 432 */             buffer.put((byte)13);
/* 433 */             if (!buffer.hasRemaining() && 
/* 434 */               flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
/* 435 */               return 7;
/*     */             }
/* 437 */             buffer.put((byte)10);
/* 438 */             if ((fiCookie = headers.fiNextNonEmpty(fiCookie)) != -1L) {
/* 439 */               headerValues = headers.fiCurrent(fiCookie);
/* 440 */               valueIdx = 0;
/* 441 */               state = 2;
/*     */               continue;
/*     */             } 
/* 444 */             if (!buffer.hasRemaining() && 
/* 445 */               flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
/* 446 */               return 8;
/*     */             }
/* 448 */             buffer.put((byte)13);
/* 449 */             if (!buffer.hasRemaining() && 
/* 450 */               flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
/* 451 */               return 9;
/*     */             }
/* 453 */             buffer.put((byte)10);
/* 454 */             this.fiCookie = -1L;
/* 455 */             this.valueIdx = 0;
/* 456 */             this.string = null;
/* 457 */             buffer.flip();
/*     */             
/* 459 */             if (userData == null) {
/*     */               do {
/* 461 */                 int res = ((StreamSinkConduit)this.next).write(buffer);
/* 462 */                 if (res == 0) {
/* 463 */                   return 10;
/*     */                 }
/* 465 */               } while (buffer.hasRemaining());
/* 466 */             } else if (userData instanceof ByteBuffer) {
/* 467 */               ByteBuffer[] b = { buffer, (ByteBuffer)userData };
/*     */               do {
/* 469 */                 long r = ((StreamSinkConduit)this.next).write(b, 0, b.length);
/* 470 */                 if (r == 0L && buffer.hasRemaining()) {
/* 471 */                   return 10;
/*     */                 }
/* 473 */               } while (buffer.hasRemaining());
/*     */             } else {
/* 475 */               ByteBuffer[] b = new ByteBuffer[1 + len];
/* 476 */               b[0] = buffer;
/* 477 */               System.arraycopy(userData, pos, b, 1, len);
/*     */               do {
/* 479 */                 long r = ((StreamSinkConduit)this.next).write(b, 0, b.length);
/* 480 */                 if (r == 0L && buffer.hasRemaining()) {
/* 481 */                   return 10;
/*     */                 }
/* 483 */               } while (buffer.hasRemaining());
/*     */             } 
/* 485 */             bufferDone();
/* 486 */             return 0;
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case 6:
/* 494 */           if (!buffer.hasRemaining() && 
/* 495 */             flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
/* 496 */             return 6;
/*     */           }
/* 498 */           buffer.put((byte)13);
/*     */         
/*     */         case 7:
/* 501 */           if (!buffer.hasRemaining() && 
/* 502 */             flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
/* 503 */             return 7;
/*     */           }
/* 505 */           buffer.put((byte)10);
/* 506 */           if (valueIdx < headerValues.size()) {
/* 507 */             state = 2; continue;
/*     */           } 
/* 509 */           if ((fiCookie = headers.fiNextNonEmpty(fiCookie)) != -1L) {
/* 510 */             headerValues = headers.fiCurrent(fiCookie);
/* 511 */             valueIdx = 0;
/* 512 */             state = 2;
/*     */             continue;
/*     */           } 
/*     */ 
/*     */         
/*     */         case 8:
/* 518 */           if (!buffer.hasRemaining() && 
/* 519 */             flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
/* 520 */             return 8;
/*     */           }
/* 522 */           buffer.put((byte)13);
/*     */ 
/*     */         
/*     */         case 9:
/* 526 */           if (!buffer.hasRemaining() && 
/* 527 */             flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
/* 528 */             return 9;
/*     */           }
/* 530 */           buffer.put((byte)10);
/* 531 */           this.fiCookie = -1L;
/* 532 */           this.valueIdx = 0;
/* 533 */           this.string = null;
/* 534 */           buffer.flip();
/*     */           
/* 536 */           if (userData == null) {
/*     */             do {
/* 538 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 539 */               if (res == 0) {
/* 540 */                 return 10;
/*     */               }
/* 542 */             } while (buffer.hasRemaining());
/* 543 */           } else if (userData instanceof ByteBuffer) {
/* 544 */             ByteBuffer[] b = { buffer, (ByteBuffer)userData };
/*     */             do {
/* 546 */               long r = ((StreamSinkConduit)this.next).write(b, 0, b.length);
/* 547 */               if (r == 0L && buffer.hasRemaining()) {
/* 548 */                 return 10;
/*     */               }
/* 550 */             } while (buffer.hasRemaining());
/*     */           } else {
/* 552 */             ByteBuffer[] b = new ByteBuffer[1 + len];
/* 553 */             b[0] = buffer;
/* 554 */             System.arraycopy(userData, pos, b, 1, len);
/*     */             do {
/* 556 */               long r = ((StreamSinkConduit)this.next).write(b, 0, b.length);
/* 557 */               if (r == 0L && buffer.hasRemaining()) {
/* 558 */                 return 10;
/*     */               }
/* 560 */             } while (buffer.hasRemaining());
/*     */           } 
/*     */ 
/*     */ 
/*     */         
/*     */         case 10:
/* 566 */           bufferDone();
/* 567 */           return 0;
/*     */       }  break;
/*     */     } 
/* 570 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean flushHeaderBuffer(ByteBuffer buffer, String string, HeaderValues headerValues, int charIndex, long fiCookie, int valueIdx) throws IOException {
/* 578 */     buffer.flip();
/*     */     while (true) {
/* 580 */       int res = ((StreamSinkConduit)this.next).write(buffer);
/* 581 */       if (res == 0) {
/* 582 */         this.string = string;
/* 583 */         this.headerValues = headerValues;
/* 584 */         this.charIndex = charIndex;
/* 585 */         this.fiCookie = fiCookie;
/* 586 */         this.valueIdx = valueIdx;
/* 587 */         return true;
/*     */       } 
/* 589 */       if (!buffer.hasRemaining()) {
/* 590 */         buffer.clear();
/* 591 */         return false;
/*     */       } 
/*     */     } 
/*     */   } public int write(ByteBuffer src) throws IOException {
/*     */     try {
/* 596 */       int oldState = this.state;
/* 597 */       int state = oldState & 0xF;
/* 598 */       int alreadyWritten = 0;
/* 599 */       int originalRemaining = -1;
/*     */       try {
/* 601 */         if (state != 0) {
/* 602 */           originalRemaining = src.remaining();
/* 603 */           state = processWrite(state, src, -1, -1);
/* 604 */           if (state != 0) {
/* 605 */             return 0;
/*     */           }
/* 607 */           alreadyWritten = originalRemaining - src.remaining();
/* 608 */           if (Bits.allAreSet(oldState, 16)) {
/* 609 */             ((StreamSinkConduit)this.next).terminateWrites();
/* 610 */             throw new ClosedChannelException();
/*     */           } 
/*     */         } 
/* 613 */         if (alreadyWritten != originalRemaining) {
/* 614 */           return ((StreamSinkConduit)this.next).write(src) + alreadyWritten;
/*     */         }
/* 616 */         return alreadyWritten;
/*     */       } finally {
/* 618 */         this.state = oldState & 0xFFFFFFF0 | state;
/*     */       } 
/* 620 */     } catch (IOException|RuntimeException|Error e) {
/* 621 */       IoUtils.safeClose((Closeable)this.connection);
/* 622 */       throw e;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs) throws IOException {
/* 627 */     return write(srcs, 0, srcs.length);
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 631 */     if (length == 0) {
/* 632 */       return 0L;
/*     */     }
/* 634 */     int oldVal = this.state;
/* 635 */     int state = oldVal & 0xF;
/*     */     try {
/* 637 */       if (state != 0) {
/* 638 */         long rem = Buffers.remaining((Buffer[])srcs, offset, length);
/* 639 */         state = processWrite(state, srcs, offset, length);
/*     */         
/* 641 */         long ret = rem - Buffers.remaining((Buffer[])srcs, offset, length);
/* 642 */         if (state != 0) {
/* 643 */           return ret;
/*     */         }
/* 645 */         if (Bits.allAreSet(oldVal, 16)) {
/* 646 */           ((StreamSinkConduit)this.next).terminateWrites();
/* 647 */           throw new ClosedChannelException();
/*     */         } 
/*     */         
/* 650 */         return ret;
/*     */       } 
/* 652 */       return (length == 1) ? ((StreamSinkConduit)this.next).write(srcs[offset]) : ((StreamSinkConduit)this.next).write(srcs, offset, length);
/* 653 */     } catch (IOException|RuntimeException|Error e) {
/* 654 */       IoUtils.safeClose((Closeable)this.connection);
/* 655 */       throw e;
/*     */     } finally {
/* 657 */       this.state = oldVal & 0xFFFFFFF0 | state;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*     */     try {
/* 663 */       if (this.pooledFileTransferBuffer != null) {
/*     */         try {
/* 665 */           return write(this.pooledFileTransferBuffer.getBuffer());
/* 666 */         } catch (IOException|RuntimeException|Error e) {
/* 667 */           if (this.pooledFileTransferBuffer != null) {
/* 668 */             this.pooledFileTransferBuffer.close();
/* 669 */             this.pooledFileTransferBuffer = null;
/*     */           } 
/* 671 */           throw e;
/*     */         } finally {
/* 673 */           if (this.pooledFileTransferBuffer != null && 
/* 674 */             !this.pooledFileTransferBuffer.getBuffer().hasRemaining()) {
/* 675 */             this.pooledFileTransferBuffer.close();
/* 676 */             this.pooledFileTransferBuffer = null;
/*     */           } 
/*     */         } 
/*     */       }
/* 680 */       if (this.state != 0) {
/* 681 */         PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();
/*     */         
/* 683 */         ByteBuffer buffer = pooled.getBuffer();
/*     */         try {
/* 685 */           int res = src.read(buffer);
/* 686 */           buffer.flip();
/* 687 */           if (res <= 0) {
/* 688 */             return res;
/*     */           }
/* 690 */           return write(buffer);
/*     */         } finally {
/* 692 */           if (buffer.hasRemaining()) {
/* 693 */             this.pooledFileTransferBuffer = pooled;
/*     */           } else {
/* 695 */             pooled.close();
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 700 */       return ((StreamSinkConduit)this.next).transferFrom(src, position, count);
/*     */     }
/* 702 */     catch (IOException|RuntimeException|Error e) {
/* 703 */       IoUtils.safeClose((Closeable)this.connection);
/* 704 */       throw e;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/*     */     try {
/* 710 */       if (this.state != 0) {
/* 711 */         return IoUtils.transfer((ReadableByteChannel)source, count, throughBuffer, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */       }
/* 713 */       return ((StreamSinkConduit)this.next).transferFrom(source, count, throughBuffer);
/*     */     }
/* 715 */     catch (IOException|RuntimeException|Error e) {
/* 716 */       IoUtils.safeClose((Closeable)this.connection);
/* 717 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/*     */     try {
/* 724 */       return Conduits.writeFinalBasic((StreamSinkConduit)this, src);
/* 725 */     } catch (IOException|RuntimeException|Error e) {
/* 726 */       IoUtils.safeClose((Closeable)this.connection);
/* 727 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/*     */     try {
/* 734 */       return Conduits.writeFinalBasic((StreamSinkConduit)this, srcs, offset, length);
/* 735 */     } catch (IOException|RuntimeException|Error e) {
/* 736 */       IoUtils.safeClose((Closeable)this.connection);
/* 737 */       throw e;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 742 */     int oldVal = this.state;
/* 743 */     int state = oldVal & 0xF;
/*     */     try {
/* 745 */       if (state != 0) {
/* 746 */         state = processWrite(state, null, -1, -1);
/* 747 */         if (state != 0) {
/* 748 */           return false;
/*     */         }
/* 750 */         if (Bits.allAreSet(oldVal, 16)) {
/* 751 */           ((StreamSinkConduit)this.next).terminateWrites();
/*     */         }
/*     */       } 
/*     */       
/* 755 */       return ((StreamSinkConduit)this.next).flush();
/* 756 */     } catch (IOException|RuntimeException|Error e) {
/* 757 */       IoUtils.safeClose((Closeable)this.connection);
/* 758 */       throw e;
/*     */     } finally {
/* 760 */       this.state = oldVal & 0xFFFFFFF0 | state;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateWrites() throws IOException {
/*     */     try {
/* 767 */       int oldVal = this.state;
/* 768 */       if (Bits.allAreClear(oldVal, 15)) {
/* 769 */         ((StreamSinkConduit)this.next).terminateWrites();
/*     */         return;
/*     */       } 
/* 772 */       this.state = oldVal | 0x10;
/* 773 */     } catch (IOException|RuntimeException|Error e) {
/* 774 */       IoUtils.safeClose((Closeable)this.connection);
/* 775 */       throw e;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void truncateWrites() throws IOException {
/*     */     try {
/* 781 */       ((StreamSinkConduit)this.next).truncateWrites();
/* 782 */     } catch (IOException|RuntimeException|Error e) {
/* 783 */       IoUtils.safeClose((Closeable)this.connection);
/* 784 */       throw e;
/*     */     } finally {
/* 786 */       if (this.pooledBuffer != null) {
/* 787 */         bufferDone();
/*     */       }
/* 789 */       if (this.pooledFileTransferBuffer != null) {
/* 790 */         this.pooledFileTransferBuffer.close();
/* 791 */         this.pooledFileTransferBuffer = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 797 */     return ((StreamSinkConduit)this.next).getWorker();
/*     */   }
/*     */   
/*     */   void freeBuffers() {
/* 801 */     this.done = true;
/* 802 */     if (this.pooledBuffer != null) {
/* 803 */       bufferDone();
/*     */     }
/* 805 */     if (this.pooledFileTransferBuffer != null) {
/* 806 */       this.pooledFileTransferBuffer.close();
/* 807 */       this.pooledFileTransferBuffer = null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http\HttpResponseConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */