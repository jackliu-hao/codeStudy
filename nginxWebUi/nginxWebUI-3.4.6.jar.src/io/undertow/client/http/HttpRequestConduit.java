/*     */ package io.undertow.client.http;
/*     */ 
/*     */ import io.undertow.client.ClientRequest;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.TruncatedResponseException;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.Iterator;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.AbstractStreamSinkConduit;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ final class HttpRequestConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*  49 */   private static final Logger log = Logger.getLogger("io.undertow.client.request");
/*     */   
/*     */   private final ByteBufferPool pool;
/*     */   
/*  53 */   private int state = 2;
/*     */   
/*     */   private Iterator<HttpString> nameIterator;
/*     */   
/*     */   private String string;
/*     */   
/*     */   private HttpString headerName;
/*     */   private Iterator<String> valueIterator;
/*     */   private int charIndex;
/*     */   private PooledByteBuffer pooledBuffer;
/*     */   private final ClientRequest request;
/*     */   private static final int STATE_BODY = 0;
/*     */   private static final int STATE_URL = 1;
/*     */   private static final int STATE_START = 2;
/*     */   private static final int STATE_HDR_NAME = 3;
/*     */   private static final int STATE_HDR_D = 4;
/*     */   private static final int STATE_HDR_DS = 5;
/*     */   private static final int STATE_HDR_VAL = 6;
/*     */   private static final int STATE_HDR_EOL_CR = 7;
/*     */   private static final int STATE_HDR_EOL_LF = 8;
/*     */   private static final int STATE_HDR_FINAL_CR = 9;
/*     */   private static final int STATE_HDR_FINAL_LF = 10;
/*     */   private static final int STATE_BUF_FLUSH = 11;
/*     */   private static final int MASK_STATE = 15;
/*     */   private static final int FLAG_SHUTDOWN = 16;
/*     */   
/*     */   HttpRequestConduit(StreamSinkConduit next, ByteBufferPool pool, ClientRequest request) {
/*  80 */     super(next);
/*  81 */     this.pool = pool;
/*  82 */     this.request = request;
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
/*     */   private int processWrite(int state, ByteBuffer userData) throws IOException {
/*  98 */     if (state == 2) {
/*  99 */       this.pooledBuffer = this.pool.allocate();
/*     */     }
/* 101 */     ClientRequest request = this.request;
/* 102 */     ByteBuffer buffer = this.pooledBuffer.getBuffer();
/*     */ 
/*     */ 
/*     */     
/* 106 */     if (state != 2 && buffer.hasRemaining()) {
/* 107 */       log.trace("Flushing remaining buffer");
/*     */       do {
/* 109 */         int res = ((StreamSinkConduit)this.next).write(buffer);
/* 110 */         if (res == 0) {
/* 111 */           return state;
/*     */         }
/* 113 */       } while (buffer.hasRemaining());
/*     */     } 
/* 115 */     buffer.clear(); while (true) {
/*     */       int length; int len; int i; HeaderMap headers;
/*     */       HeaderMap headerMap1;
/* 118 */       switch (state) {
/*     */         
/*     */         case 0:
/* 121 */           return state;
/*     */         
/*     */         case 2:
/* 124 */           log.trace("Starting request");
/* 125 */           len = request.getMethod().length() + request.getPath().length() + request.getProtocol().length() + 4;
/*     */ 
/*     */           
/* 128 */           if (len <= buffer.remaining()) {
/* 129 */             assert buffer.remaining() >= 50;
/* 130 */             request.getMethod().appendTo(buffer);
/* 131 */             buffer.put((byte)32);
/* 132 */             this.string = request.getPath();
/* 133 */             int j = this.string.length();
/* 134 */             for (this.charIndex = 0; this.charIndex < j; this.charIndex++) {
/* 135 */               buffer.put((byte)this.string.charAt(this.charIndex));
/*     */             }
/* 137 */             buffer.put((byte)32);
/* 138 */             request.getProtocol().appendTo(buffer);
/* 139 */             buffer.put((byte)13).put((byte)10);
/*     */           } else {
/* 141 */             StringBuilder sb = new StringBuilder(len);
/* 142 */             sb.append(request.getMethod().toString());
/* 143 */             sb.append(" ");
/* 144 */             sb.append(request.getPath());
/* 145 */             sb.append(" ");
/* 146 */             sb.append(request.getProtocol());
/* 147 */             sb.append("\r\n");
/* 148 */             this.string = sb.toString();
/* 149 */             this.charIndex = 0;
/* 150 */             state = 1;
/*     */             continue;
/*     */           } 
/* 153 */           headerMap1 = request.getRequestHeaders();
/* 154 */           this.nameIterator = headerMap1.getHeaderNames().iterator();
/* 155 */           if (!this.nameIterator.hasNext()) {
/* 156 */             log.trace("No request headers");
/* 157 */             buffer.put((byte)13).put((byte)10);
/* 158 */             buffer.flip();
/* 159 */             while (buffer.hasRemaining()) {
/* 160 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 161 */               if (res == 0) {
/* 162 */                 log.trace("Continuation");
/* 163 */                 return 11;
/*     */               } 
/*     */             } 
/* 166 */             this.pooledBuffer.close();
/* 167 */             this.pooledBuffer = null;
/* 168 */             log.trace("Body");
/* 169 */             return 0;
/*     */           } 
/* 171 */           this.headerName = this.nameIterator.next();
/* 172 */           this.charIndex = 0;
/*     */ 
/*     */         
/*     */         case 3:
/* 176 */           log.tracef("Processing header '%s'", this.headerName);
/* 177 */           length = this.headerName.length();
/* 178 */           while (this.charIndex < length) {
/* 179 */             if (buffer.hasRemaining()) {
/* 180 */               buffer.put(this.headerName.byteAt(this.charIndex++)); continue;
/*     */             } 
/* 182 */             log.trace("Buffer flush");
/* 183 */             buffer.flip();
/*     */             while (true) {
/* 185 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 186 */               if (res == 0) {
/* 187 */                 log.trace("Continuation");
/* 188 */                 return 3;
/*     */               } 
/* 190 */               if (!buffer.hasRemaining()) {
/* 191 */                 buffer.clear();
/*     */               }
/*     */             } 
/*     */           } 
/*     */         
/*     */         case 4:
/* 197 */           if (!buffer.hasRemaining()) {
/* 198 */             buffer.flip();
/*     */             while (true) {
/* 200 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 201 */               if (res == 0) {
/* 202 */                 log.trace("Continuation");
/* 203 */                 return 4;
/*     */               } 
/* 205 */               if (!buffer.hasRemaining())
/* 206 */               { buffer.clear(); break; } 
/*     */             } 
/* 208 */           }  buffer.put((byte)58);
/*     */ 
/*     */         
/*     */         case 5:
/* 212 */           if (!buffer.hasRemaining()) {
/* 213 */             buffer.flip();
/*     */             while (true) {
/* 215 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 216 */               if (res == 0) {
/* 217 */                 log.trace("Continuation");
/* 218 */                 return 5;
/*     */               } 
/* 220 */               if (!buffer.hasRemaining())
/* 221 */               { buffer.clear(); break; } 
/*     */             } 
/* 223 */           }  buffer.put((byte)32);
/* 224 */           if (this.valueIterator == null) {
/* 225 */             this.valueIterator = request.getRequestHeaders().get(this.headerName).iterator();
/*     */           }
/* 227 */           assert this.valueIterator.hasNext();
/* 228 */           this.string = this.valueIterator.next();
/* 229 */           this.charIndex = 0;
/*     */ 
/*     */         
/*     */         case 6:
/* 233 */           log.tracef("Processing header value '%s'", this.string);
/* 234 */           length = this.string.length();
/* 235 */           while (this.charIndex < length) {
/* 236 */             if (buffer.hasRemaining()) {
/* 237 */               buffer.put((byte)this.string.charAt(this.charIndex++)); continue;
/*     */             } 
/* 239 */             buffer.flip();
/*     */             while (true) {
/* 241 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 242 */               if (res == 0) {
/* 243 */                 log.trace("Continuation");
/* 244 */                 return 6;
/*     */               } 
/* 246 */               if (!buffer.hasRemaining())
/* 247 */                 buffer.clear(); 
/*     */             } 
/*     */           } 
/* 250 */           this.charIndex = 0;
/* 251 */           if (!this.valueIterator.hasNext()) {
/* 252 */             if (!buffer.hasRemaining()) {
/* 253 */               buffer.flip();
/*     */               while (true) {
/* 255 */                 int res = ((StreamSinkConduit)this.next).write(buffer);
/* 256 */                 if (res == 0) {
/* 257 */                   log.trace("Continuation");
/* 258 */                   return 7;
/*     */                 } 
/* 260 */                 if (!buffer.hasRemaining())
/* 261 */                 { buffer.clear(); break; } 
/*     */               } 
/* 263 */             }  buffer.put((byte)13);
/* 264 */             if (!buffer.hasRemaining()) {
/* 265 */               buffer.flip();
/*     */               while (true) {
/* 267 */                 int res = ((StreamSinkConduit)this.next).write(buffer);
/* 268 */                 if (res == 0) {
/* 269 */                   log.trace("Continuation");
/* 270 */                   return 8;
/*     */                 } 
/* 272 */                 if (!buffer.hasRemaining())
/* 273 */                 { buffer.clear(); break; } 
/*     */               } 
/* 275 */             }  buffer.put((byte)10);
/* 276 */             if (this.nameIterator.hasNext()) {
/* 277 */               this.headerName = this.nameIterator.next();
/* 278 */               this.valueIterator = null;
/* 279 */               state = 3;
/*     */               continue;
/*     */             } 
/* 282 */             if (!buffer.hasRemaining()) {
/* 283 */               buffer.flip();
/*     */               while (true) {
/* 285 */                 int res = ((StreamSinkConduit)this.next).write(buffer);
/* 286 */                 if (res == 0) {
/* 287 */                   log.trace("Continuation");
/* 288 */                   return 9;
/*     */                 } 
/* 290 */                 if (!buffer.hasRemaining())
/* 291 */                 { buffer.clear(); break; } 
/*     */               } 
/* 293 */             }  buffer.put((byte)13);
/* 294 */             if (!buffer.hasRemaining()) {
/* 295 */               buffer.flip();
/*     */               while (true) {
/* 297 */                 int res = ((StreamSinkConduit)this.next).write(buffer);
/* 298 */                 if (res == 0) {
/* 299 */                   log.trace("Continuation");
/* 300 */                   return 10;
/*     */                 } 
/* 302 */                 if (!buffer.hasRemaining())
/* 303 */                 { buffer.clear(); break; } 
/*     */               } 
/* 305 */             }  buffer.put((byte)10);
/* 306 */             this.nameIterator = null;
/* 307 */             this.valueIterator = null;
/* 308 */             this.string = null;
/* 309 */             buffer.flip();
/*     */             
/* 311 */             if (userData == null) {
/*     */               do {
/* 313 */                 int res = ((StreamSinkConduit)this.next).write(buffer);
/* 314 */                 if (res == 0) {
/* 315 */                   log.trace("Continuation");
/* 316 */                   return 11;
/*     */                 } 
/* 318 */               } while (buffer.hasRemaining());
/*     */             } else {
/* 320 */               ByteBuffer[] b = { buffer, userData };
/*     */               do {
/* 322 */                 long r = ((StreamSinkConduit)this.next).write(b, 0, b.length);
/* 323 */                 if (r == 0L && buffer.hasRemaining()) {
/* 324 */                   log.trace("Continuation");
/* 325 */                   return 11;
/*     */                 } 
/* 327 */               } while (buffer.hasRemaining());
/*     */             } 
/* 329 */             this.pooledBuffer.close();
/* 330 */             this.pooledBuffer = null;
/* 331 */             log.trace("Body");
/* 332 */             return 0;
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case 7:
/* 340 */           if (!buffer.hasRemaining()) {
/* 341 */             buffer.flip();
/*     */             while (true) {
/* 343 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 344 */               if (res == 0) {
/* 345 */                 log.trace("Continuation");
/* 346 */                 return 7;
/*     */               } 
/* 348 */               if (!buffer.hasRemaining())
/* 349 */               { buffer.clear(); break; } 
/*     */             } 
/* 351 */           }  buffer.put((byte)13);
/*     */         
/*     */         case 8:
/* 354 */           if (!buffer.hasRemaining()) {
/* 355 */             buffer.flip();
/*     */             while (true) {
/* 357 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 358 */               if (res == 0) {
/* 359 */                 log.trace("Continuation");
/* 360 */                 return 8;
/*     */               } 
/* 362 */               if (!buffer.hasRemaining())
/* 363 */               { buffer.clear(); break; } 
/*     */             } 
/* 365 */           }  buffer.put((byte)10);
/* 366 */           if (this.valueIterator != null && this.valueIterator.hasNext()) {
/* 367 */             state = 3; continue;
/*     */           } 
/* 369 */           if (this.nameIterator.hasNext()) {
/* 370 */             this.headerName = this.nameIterator.next();
/* 371 */             this.valueIterator = null;
/* 372 */             state = 3;
/*     */             continue;
/*     */           } 
/*     */ 
/*     */         
/*     */         case 9:
/* 378 */           if (!buffer.hasRemaining()) {
/* 379 */             buffer.flip();
/*     */             while (true) {
/* 381 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 382 */               if (res == 0) {
/* 383 */                 log.trace("Continuation");
/* 384 */                 return 9;
/*     */               } 
/* 386 */               if (!buffer.hasRemaining())
/* 387 */               { buffer.clear(); break; } 
/*     */             } 
/* 389 */           }  buffer.put((byte)13);
/*     */ 
/*     */         
/*     */         case 10:
/* 393 */           if (!buffer.hasRemaining()) {
/* 394 */             buffer.flip();
/*     */             while (true) {
/* 396 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 397 */               if (res == 0) {
/* 398 */                 log.trace("Continuation");
/* 399 */                 return 10;
/*     */               } 
/* 401 */               if (!buffer.hasRemaining())
/* 402 */               { buffer.clear(); break; } 
/*     */             } 
/* 404 */           }  buffer.put((byte)10);
/* 405 */           this.nameIterator = null;
/* 406 */           this.valueIterator = null;
/* 407 */           this.string = null;
/* 408 */           buffer.flip();
/*     */           
/* 410 */           if (userData == null) {
/*     */             do {
/* 412 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 413 */               if (res == 0) {
/* 414 */                 log.trace("Continuation");
/* 415 */                 return 11;
/*     */               } 
/* 417 */             } while (buffer.hasRemaining());
/*     */           } else {
/* 419 */             ByteBuffer[] b = { buffer, userData };
/*     */             do {
/* 421 */               long r = ((StreamSinkConduit)this.next).write(b, 0, b.length);
/* 422 */               if (r == 0L && buffer.hasRemaining()) {
/* 423 */                 log.trace("Continuation");
/* 424 */                 return 11;
/*     */               } 
/* 426 */             } while (buffer.hasRemaining());
/*     */           } 
/*     */ 
/*     */ 
/*     */         
/*     */         case 11:
/* 432 */           this.pooledBuffer.close();
/* 433 */           this.pooledBuffer = null;
/* 434 */           return 0;
/*     */         
/*     */         case 1:
/* 437 */           for (i = this.charIndex; i < this.string.length(); i++) {
/* 438 */             if (!buffer.hasRemaining()) {
/* 439 */               buffer.flip();
/*     */               while (true) {
/* 441 */                 int res = ((StreamSinkConduit)this.next).write(buffer);
/* 442 */                 if (res == 0) {
/* 443 */                   log.trace("Continuation");
/* 444 */                   this.charIndex = i;
/* 445 */                   this.state = 1;
/* 446 */                   return 1;
/*     */                 } 
/* 448 */                 if (!buffer.hasRemaining())
/* 449 */                 { buffer.clear(); break; } 
/*     */               } 
/* 451 */             }  buffer.put((byte)this.string.charAt(i));
/*     */           } 
/*     */           
/* 454 */           headers = request.getRequestHeaders();
/* 455 */           this.nameIterator = headers.getHeaderNames().iterator();
/* 456 */           state = 3;
/* 457 */           if (!this.nameIterator.hasNext()) {
/* 458 */             log.trace("No request headers");
/* 459 */             buffer.put((byte)13).put((byte)10);
/* 460 */             buffer.flip();
/* 461 */             while (buffer.hasRemaining()) {
/* 462 */               int res = ((StreamSinkConduit)this.next).write(buffer);
/* 463 */               if (res == 0) {
/* 464 */                 log.trace("Continuation");
/* 465 */                 return 11;
/*     */               } 
/*     */             } 
/* 468 */             this.pooledBuffer.close();
/* 469 */             this.pooledBuffer = null;
/* 470 */             log.trace("Body");
/* 471 */             return 0;
/*     */           } 
/* 473 */           this.headerName = this.nameIterator.next();
/* 474 */           this.charIndex = 0; continue;
/*     */       } 
/*     */       break;
/*     */     } 
/* 478 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 485 */     log.trace("write");
/* 486 */     int oldState = this.state;
/* 487 */     int state = oldState & 0xF;
/* 488 */     int alreadyWritten = 0;
/* 489 */     int originalRemaining = -1;
/*     */     try {
/* 491 */       if (state != 0) {
/* 492 */         originalRemaining = src.remaining();
/* 493 */         state = processWrite(state, src);
/* 494 */         if (state != 0) {
/* 495 */           return 0;
/*     */         }
/* 497 */         alreadyWritten = originalRemaining - src.remaining();
/* 498 */         if (Bits.allAreSet(oldState, 16)) {
/* 499 */           ((StreamSinkConduit)this.next).terminateWrites();
/* 500 */           throw new ClosedChannelException();
/*     */         } 
/*     */       } 
/* 503 */       if (alreadyWritten != originalRemaining) {
/* 504 */         return ((StreamSinkConduit)this.next).write(src) + alreadyWritten;
/*     */       }
/* 506 */       return alreadyWritten;
/* 507 */     } catch (IOException|RuntimeException|Error e) {
/* 508 */       this.state |= 0x10;
/* 509 */       if (this.pooledBuffer != null) {
/* 510 */         this.pooledBuffer.close();
/* 511 */         this.pooledBuffer = null;
/*     */       } 
/* 513 */       throw e;
/*     */     } finally {
/* 515 */       this.state = oldState & 0xFFFFFFF0 | state;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs) throws IOException {
/* 520 */     return write(srcs, 0, srcs.length);
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 524 */     log.trace("write");
/* 525 */     if (length == 0) {
/* 526 */       return 0L;
/*     */     }
/* 528 */     int oldVal = this.state;
/* 529 */     int state = oldVal & 0xF;
/*     */     try {
/* 531 */       if (state != 0) {
/*     */         
/* 533 */         state = processWrite(state, null);
/* 534 */         if (state != 0) {
/* 535 */           return 0L;
/*     */         }
/* 537 */         if (Bits.allAreSet(oldVal, 16)) {
/* 538 */           ((StreamSinkConduit)this.next).terminateWrites();
/* 539 */           throw new ClosedChannelException();
/*     */         } 
/*     */       } 
/* 542 */       return (length == 1) ? ((StreamSinkConduit)this.next).write(srcs[offset]) : ((StreamSinkConduit)this.next).write(srcs, offset, length);
/* 543 */     } catch (IOException|RuntimeException|Error e) {
/* 544 */       this.state |= 0x10;
/* 545 */       if (this.pooledBuffer != null) {
/* 546 */         this.pooledBuffer.close();
/* 547 */         this.pooledBuffer = null;
/*     */       } 
/* 549 */       throw e;
/*     */     } finally {
/* 551 */       this.state = oldVal & 0xFFFFFFF0 | state;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 557 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 562 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, srcs, offset, length);
/*     */   }
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 566 */     log.trace("transfer");
/* 567 */     if (count == 0L) {
/* 568 */       return 0L;
/*     */     }
/* 570 */     int oldVal = this.state;
/* 571 */     int state = oldVal & 0xF;
/*     */     try {
/* 573 */       if (state != 0) {
/* 574 */         state = processWrite(state, null);
/* 575 */         if (state != 0) {
/* 576 */           return 0L;
/*     */         }
/* 578 */         if (Bits.allAreSet(oldVal, 16)) {
/* 579 */           ((StreamSinkConduit)this.next).terminateWrites();
/* 580 */           throw new ClosedChannelException();
/*     */         } 
/*     */       } 
/* 583 */       return ((StreamSinkConduit)this.next).transferFrom(src, position, count);
/* 584 */     } catch (IOException|RuntimeException|Error e) {
/* 585 */       this.state |= 0x10;
/* 586 */       if (this.pooledBuffer != null) {
/* 587 */         this.pooledBuffer.close();
/* 588 */         this.pooledBuffer = null;
/*     */       } 
/* 590 */       throw e;
/*     */     } finally {
/* 592 */       this.state = oldVal & 0xFFFFFFF0 | state;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 597 */     log.trace("transfer");
/* 598 */     if (count == 0L) {
/* 599 */       throughBuffer.clear().limit(0);
/* 600 */       return 0L;
/*     */     } 
/* 602 */     int oldVal = this.state;
/* 603 */     int state = oldVal & 0xF;
/*     */     try {
/* 605 */       if (state != 0) {
/* 606 */         state = processWrite(state, null);
/* 607 */         if (state != 0) {
/* 608 */           return 0L;
/*     */         }
/* 610 */         if (Bits.allAreSet(oldVal, 16)) {
/* 611 */           ((StreamSinkConduit)this.next).terminateWrites();
/* 612 */           throw new ClosedChannelException();
/*     */         } 
/*     */       } 
/* 615 */       return ((StreamSinkConduit)this.next).transferFrom(source, count, throughBuffer);
/* 616 */     } catch (IOException|RuntimeException|Error e) {
/* 617 */       this.state |= 0x10;
/* 618 */       if (this.pooledBuffer != null) {
/* 619 */         this.pooledBuffer.close();
/* 620 */         this.pooledBuffer = null;
/*     */       } 
/* 622 */       throw e;
/*     */     } finally {
/* 624 */       this.state = oldVal & 0xFFFFFFF0 | state;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean flush() throws IOException {
/* 630 */     log.trace("flush");
/* 631 */     int oldVal = this.state;
/* 632 */     int state = oldVal & 0xF;
/*     */     try {
/* 634 */       if (state != 0) {
/* 635 */         state = processWrite(state, null);
/* 636 */         if (state != 0) {
/* 637 */           log.trace("Flush false because headers aren't written yet");
/* 638 */           return false;
/*     */         } 
/* 640 */         if (Bits.allAreSet(oldVal, 16)) {
/* 641 */           ((StreamSinkConduit)this.next).terminateWrites();
/*     */         }
/*     */       } 
/*     */       
/* 645 */       log.trace("Delegating flush");
/* 646 */       return ((StreamSinkConduit)this.next).flush();
/* 647 */     } catch (IOException|RuntimeException|Error e) {
/* 648 */       this.state |= 0x10;
/* 649 */       if (this.pooledBuffer != null) {
/* 650 */         this.pooledBuffer.close();
/* 651 */         this.pooledBuffer = null;
/*     */       } 
/* 653 */       throw e;
/*     */     } finally {
/* 655 */       this.state = oldVal & 0xFFFFFFF0 | state;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 661 */     log.trace("shutdown");
/* 662 */     int oldVal = this.state;
/* 663 */     if (Bits.allAreClear(oldVal, 15)) {
/* 664 */       ((StreamSinkConduit)this.next).terminateWrites();
/*     */       return;
/*     */     } 
/* 667 */     this.state = oldVal | 0x10;
/*     */   }
/*     */   
/*     */   public void truncateWrites() throws IOException {
/* 671 */     log.trace("close");
/* 672 */     int oldVal = this.state;
/* 673 */     if (Bits.allAreClear(oldVal, 15)) {
/*     */       try {
/* 675 */         ((StreamSinkConduit)this.next).truncateWrites();
/*     */       } finally {
/* 677 */         if (this.pooledBuffer != null) {
/* 678 */           this.pooledBuffer.close();
/* 679 */           this.pooledBuffer = null;
/*     */         } 
/*     */       } 
/*     */       return;
/*     */     } 
/* 684 */     this.state = oldVal & 0xFFFFFFF0 | 0x10;
/* 685 */     throw new TruncatedResponseException();
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 689 */     return ((StreamSinkConduit)this.next).getWorker();
/*     */   }
/*     */   
/*     */   public void freeBuffers() {
/* 693 */     if (this.pooledBuffer != null) {
/* 694 */       this.pooledBuffer.close();
/* 695 */       this.pooledBuffer = null;
/* 696 */       this.state = this.state & 0xFFFFFFF0 | 0x10;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\http\HttpRequestConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */