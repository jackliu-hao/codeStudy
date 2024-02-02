/*      */ package org.xnio;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InterruptedIOException;
/*      */ import java.io.OutputStream;
/*      */ import java.nio.Buffer;
/*      */ import java.nio.BufferOverflowException;
/*      */ import java.nio.BufferUnderflowException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.IntBuffer;
/*      */ import java.nio.LongBuffer;
/*      */ import java.nio.ReadOnlyBufferException;
/*      */ import java.nio.ShortBuffer;
/*      */ import java.nio.charset.CharsetDecoder;
/*      */ import java.nio.charset.CoderResult;
/*      */ import java.util.Arrays;
/*      */ import java.util.Random;
/*      */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*      */ import org.wildfly.common.ref.CleanerReference;
/*      */ import org.wildfly.common.ref.Reaper;
/*      */ import org.wildfly.common.ref.Reference;
/*      */ import org.xnio._private.Messages;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class Buffers
/*      */ {
/*      */   public static <T extends Buffer> T flip(T buffer) {
/*   65 */     buffer.flip();
/*   66 */     return buffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Buffer> T clear(T buffer) {
/*   78 */     buffer.clear();
/*   79 */     return buffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Buffer> T limit(T buffer, int limit) {
/*   92 */     buffer.limit(limit);
/*   93 */     return buffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Buffer> T mark(T buffer) {
/*  105 */     buffer.mark();
/*  106 */     return buffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Buffer> T position(T buffer, int position) {
/*  119 */     buffer.position(position);
/*  120 */     return buffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Buffer> T reset(T buffer) {
/*  132 */     buffer.reset();
/*  133 */     return buffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Buffer> T rewind(T buffer) {
/*  145 */     buffer.rewind();
/*  146 */     return buffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteBuffer slice(ByteBuffer buffer, int sliceSize) {
/*  158 */     int oldRem = buffer.remaining();
/*  159 */     if (sliceSize > oldRem || sliceSize < -oldRem) {
/*  160 */       throw Messages.msg.bufferUnderflow();
/*      */     }
/*  162 */     int oldPos = buffer.position();
/*  163 */     int oldLim = buffer.limit();
/*  164 */     if (sliceSize < 0) {
/*      */       
/*  166 */       buffer.limit(oldLim + sliceSize);
/*      */       try {
/*  168 */         return buffer.slice();
/*      */       } finally {
/*  170 */         buffer.limit(oldLim);
/*  171 */         buffer.position(oldLim + sliceSize);
/*      */       } 
/*      */     } 
/*      */     
/*  175 */     buffer.limit(oldPos + sliceSize);
/*      */     try {
/*  177 */       return buffer.slice();
/*      */     } finally {
/*  179 */       buffer.limit(oldLim);
/*  180 */       buffer.position(oldPos + sliceSize);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteBuffer copy(ByteBuffer buffer, int count, BufferAllocator<ByteBuffer> allocator) {
/*  194 */     int oldRem = buffer.remaining();
/*  195 */     if (count > oldRem || count < -oldRem) {
/*  196 */       throw Messages.msg.bufferUnderflow();
/*      */     }
/*  198 */     int oldPos = buffer.position();
/*  199 */     int oldLim = buffer.limit();
/*  200 */     if (count < 0) {
/*      */       
/*  202 */       ByteBuffer byteBuffer = allocator.allocate(-count);
/*  203 */       buffer.position(oldLim + count);
/*      */       try {
/*  205 */         byteBuffer.put(buffer);
/*  206 */         return byteBuffer;
/*      */       } finally {
/*  208 */         buffer.limit(oldLim);
/*  209 */         buffer.position(oldLim + count);
/*      */       } 
/*      */     } 
/*      */     
/*  213 */     ByteBuffer target = allocator.allocate(count);
/*  214 */     buffer.limit(oldPos + count);
/*      */     try {
/*  216 */       target.put(buffer);
/*  217 */       return target;
/*      */     } finally {
/*  219 */       buffer.limit(oldLim);
/*  220 */       buffer.position(oldPos + count);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int copy(ByteBuffer destination, ByteBuffer source) {
/*  233 */     int sr = source.remaining();
/*  234 */     int dr = destination.remaining();
/*  235 */     if (dr >= sr) {
/*  236 */       destination.put(source);
/*  237 */       return sr;
/*      */     } 
/*  239 */     destination.put(slice(source, dr));
/*  240 */     return dr;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int copy(ByteBuffer[] destinations, int offset, int length, ByteBuffer source) {
/*  254 */     int t = 0;
/*  255 */     for (int i = 0; i < length; i++) {
/*  256 */       ByteBuffer buffer = destinations[i + offset];
/*  257 */       int rem = buffer.remaining();
/*  258 */       if (rem != 0)
/*      */       {
/*  260 */         if (rem < source.remaining()) {
/*  261 */           buffer.put(slice(source, rem));
/*  262 */           t += rem;
/*      */         } else {
/*  264 */           t += source.remaining();
/*  265 */           buffer.put(source);
/*  266 */           return t;
/*      */         }  } 
/*      */     } 
/*  269 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int copy(ByteBuffer destination, ByteBuffer[] sources, int offset, int length) {
/*  282 */     int t = 0;
/*  283 */     for (int i = 0; i < length; i++) {
/*  284 */       ByteBuffer buffer = sources[i + offset];
/*  285 */       int rem = buffer.remaining();
/*  286 */       if (rem != 0) {
/*      */         
/*  288 */         if (rem > destination.remaining()) {
/*  289 */           t += destination.remaining();
/*  290 */           destination.put(slice(buffer, destination.remaining()));
/*  291 */           return t;
/*      */         } 
/*  293 */         destination.put(buffer);
/*  294 */         t += rem;
/*      */       } 
/*      */     } 
/*  297 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long copy(ByteBuffer[] destinations, int destOffset, int destLength, ByteBuffer[] sources, int srcOffset, int srcLength) {
/*  312 */     long t = 0L;
/*  313 */     int s = 0, d = 0;
/*  314 */     if (destLength == 0 || srcLength == 0) {
/*  315 */       return 0L;
/*      */     }
/*  317 */     ByteBuffer source = sources[srcOffset];
/*  318 */     ByteBuffer dest = destinations[destOffset];
/*  319 */     while (s < srcLength && d < destLength) {
/*  320 */       source = sources[srcOffset + s];
/*  321 */       dest = destinations[destOffset + d];
/*  322 */       int sr = source.remaining();
/*  323 */       int dr = dest.remaining();
/*  324 */       if (sr < dr) {
/*  325 */         dest.put(source);
/*  326 */         s++;
/*  327 */         t += sr; continue;
/*  328 */       }  if (sr > dr) {
/*  329 */         dest.put(slice(source, dr));
/*  330 */         d++;
/*  331 */         t += dr; continue;
/*      */       } 
/*  333 */       dest.put(source);
/*  334 */       s++;
/*  335 */       d++;
/*  336 */       t += sr;
/*      */     } 
/*      */     
/*  339 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int copy(int count, ByteBuffer destination, ByteBuffer source) {
/*  352 */     int cnt = (count >= 0) ? Math.min(Math.min(count, source.remaining()), destination.remaining()) : Math.max(Math.max(count, -source.remaining()), -destination.remaining());
/*  353 */     ByteBuffer copy = slice(source, cnt);
/*  354 */     destination.put(copy);
/*  355 */     return copy.position();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int copy(int count, ByteBuffer[] destinations, int offset, int length, ByteBuffer source) {
/*  369 */     if (source.remaining() > count) {
/*  370 */       int oldLimit = source.limit();
/*  371 */       if (count < 0)
/*      */       {
/*  373 */         throw Messages.msg.copyNegative();
/*      */       }
/*      */       try {
/*  376 */         source.limit(source.position() + count);
/*  377 */         return copy(destinations, offset, length, source);
/*      */       } finally {
/*  379 */         source.limit(oldLimit);
/*      */       } 
/*      */     } 
/*      */     
/*  383 */     return copy(destinations, offset, length, source);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int copy(int count, ByteBuffer destination, ByteBuffer[] sources, int offset, int length) {
/*  398 */     if (destination.remaining() > count) {
/*  399 */       if (count < 0)
/*      */       {
/*  401 */         throw Messages.msg.copyNegative();
/*      */       }
/*  403 */       int oldLimit = destination.limit();
/*      */       try {
/*  405 */         destination.limit(destination.position() + Math.min(count, destination.remaining()));
/*  406 */         return copy(destination, sources, offset, length);
/*      */       } finally {
/*  408 */         destination.limit(oldLimit);
/*      */       } 
/*      */     } 
/*      */     
/*  412 */     return copy(destination, sources, offset, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long copy(long count, ByteBuffer[] destinations, int destOffset, int destLength, ByteBuffer[] sources, int srcOffset, int srcLength) {
/*  429 */     long t = 0L;
/*  430 */     int s = 0, d = 0;
/*  431 */     if (count < 0L)
/*      */     {
/*  433 */       throw Messages.msg.copyNegative();
/*      */     }
/*  435 */     if (destLength == 0 || srcLength == 0 || count == 0L) {
/*  436 */       return 0L;
/*      */     }
/*  438 */     while (s < srcLength && d < destLength) {
/*  439 */       ByteBuffer source = sources[srcOffset + s];
/*  440 */       ByteBuffer dest = destinations[destOffset + d];
/*  441 */       int sr = source.remaining();
/*  442 */       int dr = (int)Math.min(count, dest.remaining());
/*  443 */       if (sr < dr) {
/*  444 */         dest.put(source);
/*  445 */         s++;
/*  446 */         t += sr;
/*  447 */         count -= sr; continue;
/*  448 */       }  if (sr > dr) {
/*  449 */         dest.put(slice(source, dr));
/*  450 */         d++;
/*  451 */         t += dr;
/*  452 */         count -= dr; continue;
/*      */       } 
/*  454 */       dest.put(source);
/*  455 */       s++;
/*  456 */       d++;
/*  457 */       t += sr;
/*  458 */       count -= sr;
/*      */     } 
/*      */     
/*  461 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteBuffer fill(ByteBuffer buffer, int value, int count) {
/*  473 */     if (count > buffer.remaining()) {
/*  474 */       throw Messages.msg.bufferUnderflow();
/*      */     }
/*  476 */     if (buffer.hasArray()) {
/*  477 */       int offs = buffer.arrayOffset();
/*  478 */       Arrays.fill(buffer.array(), offs + buffer.position(), offs + buffer.limit(), (byte)value);
/*  479 */       skip(buffer, count);
/*      */     } else {
/*  481 */       for (int i = count; i > 0; i--) {
/*  482 */         buffer.put((byte)value);
/*      */       }
/*      */     } 
/*  485 */     return buffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static CharBuffer slice(CharBuffer buffer, int sliceSize) {
/*  497 */     if (sliceSize > buffer.remaining() || sliceSize < -buffer.remaining()) {
/*  498 */       throw Messages.msg.bufferUnderflow();
/*      */     }
/*  500 */     int oldPos = buffer.position();
/*  501 */     int oldLim = buffer.limit();
/*  502 */     if (sliceSize < 0) {
/*      */       
/*  504 */       buffer.limit(oldLim + sliceSize);
/*      */       try {
/*  506 */         return buffer.slice();
/*      */       } finally {
/*  508 */         buffer.limit(oldLim);
/*  509 */         buffer.position(oldLim + sliceSize);
/*      */       } 
/*      */     } 
/*      */     
/*  513 */     buffer.limit(oldPos + sliceSize);
/*      */     try {
/*  515 */       return buffer.slice();
/*      */     } finally {
/*  517 */       buffer.limit(oldLim);
/*  518 */       buffer.position(oldPos + sliceSize);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static CharBuffer fill(CharBuffer buffer, int value, int count) {
/*  532 */     if (count > buffer.remaining()) {
/*  533 */       throw Messages.msg.bufferUnderflow();
/*      */     }
/*  535 */     if (buffer.hasArray()) {
/*  536 */       int offs = buffer.arrayOffset();
/*  537 */       Arrays.fill(buffer.array(), offs + buffer.position(), offs + buffer.limit(), (char)value);
/*  538 */       skip(buffer, count);
/*      */     } else {
/*  540 */       for (int i = count; i > 0; i--) {
/*  541 */         buffer.put((char)value);
/*      */       }
/*      */     } 
/*  544 */     return buffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ShortBuffer slice(ShortBuffer buffer, int sliceSize) {
/*  556 */     if (sliceSize > buffer.remaining() || sliceSize < -buffer.remaining()) {
/*  557 */       throw Messages.msg.bufferUnderflow();
/*      */     }
/*  559 */     int oldPos = buffer.position();
/*  560 */     int oldLim = buffer.limit();
/*  561 */     if (sliceSize < 0) {
/*      */       
/*  563 */       buffer.limit(oldLim + sliceSize);
/*      */       try {
/*  565 */         return buffer.slice();
/*      */       } finally {
/*  567 */         buffer.limit(oldLim);
/*  568 */         buffer.position(oldLim + sliceSize);
/*      */       } 
/*      */     } 
/*      */     
/*  572 */     buffer.limit(oldPos + sliceSize);
/*      */     try {
/*  574 */       return buffer.slice();
/*      */     } finally {
/*  576 */       buffer.limit(oldLim);
/*  577 */       buffer.position(oldPos + sliceSize);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ShortBuffer fill(ShortBuffer buffer, int value, int count) {
/*  591 */     if (count > buffer.remaining()) {
/*  592 */       throw Messages.msg.bufferUnderflow();
/*      */     }
/*  594 */     if (buffer.hasArray()) {
/*  595 */       int offs = buffer.arrayOffset();
/*  596 */       Arrays.fill(buffer.array(), offs + buffer.position(), offs + buffer.limit(), (short)value);
/*  597 */       skip(buffer, count);
/*      */     } else {
/*  599 */       for (int i = count; i > 0; i--) {
/*  600 */         buffer.put((short)value);
/*      */       }
/*      */     } 
/*  603 */     return buffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static IntBuffer slice(IntBuffer buffer, int sliceSize) {
/*  615 */     if (sliceSize > buffer.remaining() || sliceSize < -buffer.remaining()) {
/*  616 */       throw Messages.msg.bufferUnderflow();
/*      */     }
/*  618 */     int oldPos = buffer.position();
/*  619 */     int oldLim = buffer.limit();
/*  620 */     if (sliceSize < 0) {
/*      */       
/*  622 */       buffer.limit(oldLim + sliceSize);
/*      */       try {
/*  624 */         return buffer.slice();
/*      */       } finally {
/*  626 */         buffer.limit(oldLim);
/*  627 */         buffer.position(oldLim + sliceSize);
/*      */       } 
/*      */     } 
/*      */     
/*  631 */     buffer.limit(oldPos + sliceSize);
/*      */     try {
/*  633 */       return buffer.slice();
/*      */     } finally {
/*  635 */       buffer.limit(oldLim);
/*  636 */       buffer.position(oldPos + sliceSize);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static IntBuffer fill(IntBuffer buffer, int value, int count) {
/*  650 */     if (count > buffer.remaining()) {
/*  651 */       throw Messages.msg.bufferUnderflow();
/*      */     }
/*  653 */     if (buffer.hasArray()) {
/*  654 */       int offs = buffer.arrayOffset();
/*  655 */       Arrays.fill(buffer.array(), offs + buffer.position(), offs + buffer.limit(), value);
/*  656 */       skip(buffer, count);
/*      */     } else {
/*  658 */       for (int i = count; i > 0; i--) {
/*  659 */         buffer.put(value);
/*      */       }
/*      */     } 
/*  662 */     return buffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static LongBuffer slice(LongBuffer buffer, int sliceSize) {
/*  674 */     if (sliceSize > buffer.remaining() || sliceSize < -buffer.remaining()) {
/*  675 */       throw Messages.msg.bufferUnderflow();
/*      */     }
/*  677 */     int oldPos = buffer.position();
/*  678 */     int oldLim = buffer.limit();
/*  679 */     if (sliceSize < 0) {
/*      */       
/*  681 */       buffer.limit(oldLim + sliceSize);
/*      */       try {
/*  683 */         return buffer.slice();
/*      */       } finally {
/*  685 */         buffer.limit(oldLim);
/*  686 */         buffer.position(oldLim + sliceSize);
/*      */       } 
/*      */     } 
/*      */     
/*  690 */     buffer.limit(oldPos + sliceSize);
/*      */     try {
/*  692 */       return buffer.slice();
/*      */     } finally {
/*  694 */       buffer.limit(oldLim);
/*  695 */       buffer.position(oldPos + sliceSize);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static LongBuffer fill(LongBuffer buffer, long value, int count) {
/*  709 */     if (count > buffer.remaining()) {
/*  710 */       throw Messages.msg.bufferUnderflow();
/*      */     }
/*  712 */     if (buffer.hasArray()) {
/*  713 */       int offs = buffer.arrayOffset();
/*  714 */       Arrays.fill(buffer.array(), offs + buffer.position(), offs + buffer.limit(), value);
/*  715 */       skip(buffer, count);
/*      */     } else {
/*  717 */       for (int i = count; i > 0; i--) {
/*  718 */         buffer.put(value);
/*      */       }
/*      */     } 
/*  721 */     return buffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Buffer> T skip(T buffer, int cnt) throws BufferUnderflowException {
/*  735 */     if (cnt < 0) {
/*  736 */       throw Messages.msg.parameterOutOfRange("cnt");
/*      */     }
/*  738 */     if (cnt > buffer.remaining()) {
/*  739 */       throw Messages.msg.bufferUnderflow();
/*      */     }
/*  741 */     buffer.position(buffer.position() + cnt);
/*  742 */     return buffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int trySkip(Buffer buffer, int cnt) {
/*  754 */     if (cnt < 0) {
/*  755 */       throw Messages.msg.parameterOutOfRange("cnt");
/*      */     }
/*  757 */     int rem = buffer.remaining();
/*  758 */     if (cnt > rem) {
/*  759 */       cnt = rem;
/*      */     }
/*  761 */     buffer.position(buffer.position() + cnt);
/*  762 */     return cnt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long trySkip(Buffer[] buffers, int offs, int len, long cnt) {
/*  776 */     if (cnt < 0L) {
/*  777 */       throw Messages.msg.parameterOutOfRange("cnt");
/*      */     }
/*  779 */     if (len < 0) {
/*  780 */       throw Messages.msg.parameterOutOfRange("len");
/*      */     }
/*  782 */     if (offs < 0) {
/*  783 */       throw Messages.msg.parameterOutOfRange("offs");
/*      */     }
/*  785 */     if (offs > buffers.length) {
/*  786 */       throw Messages.msg.parameterOutOfRange("offs");
/*      */     }
/*  788 */     if (offs + len > buffers.length) {
/*  789 */       throw Messages.msg.parameterOutOfRange("offs");
/*      */     }
/*  791 */     long c = 0L;
/*  792 */     for (int i = 0; i < len; i++) {
/*  793 */       Buffer buffer = buffers[offs + i];
/*  794 */       int rem = buffer.remaining();
/*  795 */       if (rem < cnt) {
/*  796 */         buffer.position(buffer.position() + rem);
/*  797 */         cnt -= rem;
/*  798 */         c += rem;
/*      */       } else {
/*  800 */         buffer.position(buffer.position() + (int)cnt);
/*  801 */         return c + cnt;
/*      */       } 
/*      */     } 
/*  804 */     return c;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Buffer> T unget(T buffer, int cnt) {
/*  817 */     if (cnt < 0) {
/*  818 */       throw Messages.msg.parameterOutOfRange("cnt");
/*      */     }
/*  820 */     if (cnt > buffer.position()) {
/*  821 */       throw Messages.msg.bufferUnderflow();
/*      */     }
/*  823 */     buffer.position(buffer.position() - cnt);
/*  824 */     return buffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] take(ByteBuffer buffer, int cnt) {
/*  835 */     if (cnt < 0) {
/*  836 */       throw Messages.msg.parameterOutOfRange("cnt");
/*      */     }
/*  838 */     if (buffer.hasArray()) {
/*  839 */       int pos = buffer.position();
/*  840 */       int lim = buffer.limit();
/*  841 */       if (lim - pos < cnt) {
/*  842 */         throw new BufferUnderflowException();
/*      */       }
/*  844 */       byte[] array = buffer.array();
/*  845 */       int offset = buffer.arrayOffset();
/*  846 */       buffer.position(pos + cnt);
/*  847 */       int start = offset + pos;
/*  848 */       return Arrays.copyOfRange(array, start, start + cnt);
/*      */     } 
/*  850 */     byte[] bytes = new byte[cnt];
/*  851 */     buffer.get(bytes);
/*  852 */     return bytes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static char[] take(CharBuffer buffer, int cnt) {
/*  863 */     if (cnt < 0) {
/*  864 */       throw Messages.msg.parameterOutOfRange("cnt");
/*      */     }
/*  866 */     if (buffer.hasArray()) {
/*  867 */       int pos = buffer.position();
/*  868 */       int lim = buffer.limit();
/*  869 */       if (lim - pos < cnt) {
/*  870 */         throw new BufferUnderflowException();
/*      */       }
/*  872 */       char[] array = buffer.array();
/*  873 */       int offset = buffer.arrayOffset();
/*  874 */       buffer.position(pos + cnt);
/*  875 */       int start = offset + pos;
/*  876 */       return Arrays.copyOfRange(array, start, start + cnt);
/*      */     } 
/*  878 */     char[] chars = new char[cnt];
/*  879 */     buffer.get(chars);
/*  880 */     return chars;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static short[] take(ShortBuffer buffer, int cnt) {
/*  891 */     if (cnt < 0) {
/*  892 */       throw Messages.msg.parameterOutOfRange("cnt");
/*      */     }
/*  894 */     if (buffer.hasArray()) {
/*  895 */       int pos = buffer.position();
/*  896 */       int lim = buffer.limit();
/*  897 */       if (lim - pos < cnt) {
/*  898 */         throw new BufferUnderflowException();
/*      */       }
/*  900 */       short[] array = buffer.array();
/*  901 */       int offset = buffer.arrayOffset();
/*  902 */       buffer.position(pos + cnt);
/*  903 */       int start = offset + pos;
/*  904 */       return Arrays.copyOfRange(array, start, start + cnt);
/*      */     } 
/*  906 */     short[] shorts = new short[cnt];
/*  907 */     buffer.get(shorts);
/*  908 */     return shorts;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int[] take(IntBuffer buffer, int cnt) {
/*  919 */     if (cnt < 0) {
/*  920 */       throw Messages.msg.parameterOutOfRange("cnt");
/*      */     }
/*  922 */     if (buffer.hasArray()) {
/*  923 */       int pos = buffer.position();
/*  924 */       int lim = buffer.limit();
/*  925 */       if (lim - pos < cnt) {
/*  926 */         throw new BufferUnderflowException();
/*      */       }
/*  928 */       int[] array = buffer.array();
/*  929 */       int offset = buffer.arrayOffset();
/*  930 */       buffer.position(pos + cnt);
/*  931 */       int start = offset + pos;
/*  932 */       return Arrays.copyOfRange(array, start, start + cnt);
/*      */     } 
/*  934 */     int[] ints = new int[cnt];
/*  935 */     buffer.get(ints);
/*  936 */     return ints;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long[] take(LongBuffer buffer, int cnt) {
/*  947 */     if (cnt < 0) {
/*  948 */       throw Messages.msg.parameterOutOfRange("cnt");
/*      */     }
/*  950 */     if (buffer.hasArray()) {
/*  951 */       int pos = buffer.position();
/*  952 */       int lim = buffer.limit();
/*  953 */       if (lim - pos < cnt) {
/*  954 */         throw new BufferUnderflowException();
/*      */       }
/*  956 */       long[] array = buffer.array();
/*  957 */       int offset = buffer.arrayOffset();
/*  958 */       buffer.position(pos + cnt);
/*  959 */       int start = offset + pos;
/*  960 */       return Arrays.copyOfRange(array, start, start + cnt);
/*      */     } 
/*  962 */     long[] longs = new long[cnt];
/*  963 */     buffer.get(longs);
/*  964 */     return longs;
/*      */   }
/*      */   
/*  967 */   private static final byte[] NO_BYTES = new byte[0];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] take(ByteBuffer buffer) {
/*  976 */     int remaining = buffer.remaining();
/*  977 */     if (remaining == 0) return NO_BYTES; 
/*  978 */     if (buffer.hasArray()) {
/*  979 */       int pos = buffer.position();
/*  980 */       int lim = buffer.limit();
/*  981 */       byte[] array = buffer.array();
/*  982 */       int offset = buffer.arrayOffset();
/*  983 */       buffer.position(lim);
/*  984 */       return Arrays.copyOfRange(array, offset + pos, offset + lim);
/*      */     } 
/*  986 */     byte[] bytes = new byte[remaining];
/*  987 */     buffer.get(bytes);
/*  988 */     return bytes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] take(ByteBuffer[] buffers, int offs, int len) {
/* 1000 */     if (len == 1) return take(buffers[offs]); 
/* 1001 */     long remaining = remaining((Buffer[])buffers, offs, len);
/* 1002 */     if (remaining == 0L) return NO_BYTES; 
/* 1003 */     if (remaining > 2147483647L) throw new OutOfMemoryError("Array too large"); 
/* 1004 */     byte[] bytes = new byte[(int)remaining];
/* 1005 */     int o = 0;
/*      */ 
/*      */     
/* 1008 */     for (int i = 0; i < len; i++) {
/* 1009 */       ByteBuffer buffer = buffers[i + offs];
/* 1010 */       int rem = buffer.remaining();
/* 1011 */       buffer.get(bytes, o, rem);
/* 1012 */       o += rem;
/*      */     } 
/* 1014 */     return bytes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static char[] take(CharBuffer buffer) {
/* 1024 */     char[] chars = new char[buffer.remaining()];
/* 1025 */     buffer.get(chars);
/* 1026 */     return chars;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static short[] take(ShortBuffer buffer) {
/* 1036 */     short[] shorts = new short[buffer.remaining()];
/* 1037 */     buffer.get(shorts);
/* 1038 */     return shorts;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int[] take(IntBuffer buffer) {
/* 1048 */     int[] ints = new int[buffer.remaining()];
/* 1049 */     buffer.get(ints);
/* 1050 */     return ints;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long[] take(LongBuffer buffer) {
/* 1060 */     long[] longs = new long[buffer.remaining()];
/* 1061 */     buffer.get(longs);
/* 1062 */     return longs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object createDumper(final ByteBuffer buffer, final int indent, final int columns) {
/* 1076 */     if (columns <= 0) {
/* 1077 */       throw Messages.msg.parameterOutOfRange("columns");
/*      */     }
/* 1079 */     if (indent < 0) {
/* 1080 */       throw Messages.msg.parameterOutOfRange("indent");
/*      */     }
/* 1082 */     return new Object() {
/*      */         public String toString() {
/* 1084 */           StringBuilder b = new StringBuilder();
/*      */           try {
/* 1086 */             Buffers.dump(buffer, b, indent, columns);
/* 1087 */           } catch (IOException iOException) {}
/*      */ 
/*      */           
/* 1090 */           return b.toString();
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void dump(ByteBuffer buffer, Appendable dest, int indent, int columns) throws IOException {
/* 1105 */     if (columns <= 0) {
/* 1106 */       throw Messages.msg.parameterOutOfRange("columns");
/*      */     }
/* 1108 */     if (indent < 0) {
/* 1109 */       throw Messages.msg.parameterOutOfRange("indent");
/*      */     }
/* 1111 */     int pos = buffer.position();
/* 1112 */     int remaining = buffer.remaining();
/* 1113 */     int rowLength = 8 << columns - 1;
/* 1114 */     int n = Math.max(Integer.toString(buffer.remaining(), 16).length(), 4); int idx;
/* 1115 */     for (idx = 0; idx < remaining; idx += rowLength) {
/*      */       
/* 1117 */       for (int i = 0; i < indent; i++) {
/* 1118 */         dest.append(' ');
/*      */       }
/* 1120 */       String s = Integer.toString(idx, 16);
/* 1121 */       for (int j = n - s.length(); j > 0; j--) {
/* 1122 */         dest.append('0');
/*      */       }
/* 1124 */       dest.append(s);
/* 1125 */       dest.append(" - ");
/* 1126 */       appendHexRow(buffer, dest, pos + idx, columns);
/* 1127 */       appendTextRow(buffer, dest, pos + idx, columns);
/* 1128 */       dest.append('\n');
/*      */     } 
/*      */   }
/*      */   
/*      */   private static void appendHexRow(ByteBuffer buffer, Appendable dest, int startPos, int columns) throws IOException {
/* 1133 */     int limit = buffer.limit();
/* 1134 */     int pos = startPos;
/* 1135 */     for (int c = 0; c < columns; c++) {
/* 1136 */       for (int i = 0; i < 8; i++) {
/* 1137 */         if (pos >= limit) {
/* 1138 */           dest.append("  ");
/*      */         } else {
/* 1140 */           int v = buffer.get(pos++) & 0xFF;
/* 1141 */           String hexVal = Integer.toString(v, 16);
/* 1142 */           if (v < 16) {
/* 1143 */             dest.append('0');
/*      */           }
/* 1145 */           dest.append(hexVal);
/*      */         } 
/* 1147 */         dest.append(' ');
/*      */       } 
/* 1149 */       dest.append(' ');
/* 1150 */       dest.append(' ');
/*      */     } 
/*      */   }
/*      */   
/*      */   private static void appendTextRow(ByteBuffer buffer, Appendable dest, int startPos, int columns) throws IOException {
/* 1155 */     int limit = buffer.limit();
/* 1156 */     int pos = startPos;
/* 1157 */     dest.append('[');
/* 1158 */     dest.append(' ');
/* 1159 */     for (int c = 0; c < columns; c++) {
/* 1160 */       for (int i = 0; i < 8; i++) {
/* 1161 */         if (pos >= limit) {
/* 1162 */           dest.append(' ');
/*      */         } else {
/* 1164 */           char v = (char)(buffer.get(pos++) & 0xFF);
/* 1165 */           if (Character.isISOControl(v)) {
/* 1166 */             dest.append('.');
/*      */           } else {
/* 1168 */             dest.append(v);
/*      */           } 
/*      */         } 
/*      */       } 
/* 1172 */       dest.append(' ');
/*      */     } 
/* 1174 */     dest.append(']');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object createDumper(final CharBuffer buffer, final int indent, final int columns) {
/* 1188 */     if (columns <= 0) {
/* 1189 */       throw Messages.msg.parameterOutOfRange("columns");
/*      */     }
/* 1191 */     if (indent < 0) {
/* 1192 */       throw Messages.msg.parameterOutOfRange("indent");
/*      */     }
/* 1194 */     return new Object() {
/*      */         public String toString() {
/* 1196 */           StringBuilder b = new StringBuilder();
/*      */           try {
/* 1198 */             Buffers.dump(buffer, b, indent, columns);
/* 1199 */           } catch (IOException iOException) {}
/*      */ 
/*      */           
/* 1202 */           return b.toString();
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void dump(CharBuffer buffer, Appendable dest, int indent, int columns) throws IOException {
/* 1217 */     if (columns <= 0) {
/* 1218 */       throw Messages.msg.parameterOutOfRange("columns");
/*      */     }
/* 1220 */     if (indent < 0) {
/* 1221 */       throw Messages.msg.parameterOutOfRange("indent");
/*      */     }
/* 1223 */     int pos = buffer.position();
/* 1224 */     int remaining = buffer.remaining();
/* 1225 */     int rowLength = 8 << columns - 1;
/* 1226 */     int n = Math.max(Integer.toString(buffer.remaining(), 16).length(), 4); int idx;
/* 1227 */     for (idx = 0; idx < remaining; idx += rowLength) {
/*      */       
/* 1229 */       for (int i = 0; i < indent; i++) {
/* 1230 */         dest.append(' ');
/*      */       }
/* 1232 */       String s = Integer.toString(idx, 16);
/* 1233 */       for (int j = n - s.length(); j > 0; j--) {
/* 1234 */         dest.append('0');
/*      */       }
/* 1236 */       dest.append(s);
/* 1237 */       dest.append(" - ");
/* 1238 */       appendHexRow(buffer, dest, pos + idx, columns);
/* 1239 */       appendTextRow(buffer, dest, pos + idx, columns);
/* 1240 */       dest.append('\n');
/*      */     } 
/*      */   }
/*      */   
/*      */   private static void appendHexRow(CharBuffer buffer, Appendable dest, int startPos, int columns) throws IOException {
/* 1245 */     int limit = buffer.limit();
/* 1246 */     int pos = startPos;
/* 1247 */     for (int c = 0; c < columns; c++) {
/* 1248 */       for (int i = 0; i < 8; i++) {
/* 1249 */         if (pos >= limit) {
/* 1250 */           dest.append("  ");
/*      */         } else {
/* 1252 */           char v = buffer.get(pos++);
/* 1253 */           String hexVal = Integer.toString(v, 16);
/* 1254 */           dest.append("0000".substring(hexVal.length()));
/* 1255 */           dest.append(hexVal);
/*      */         } 
/* 1257 */         dest.append(' ');
/*      */       } 
/* 1259 */       dest.append(' ');
/* 1260 */       dest.append(' ');
/*      */     } 
/*      */   }
/*      */   
/*      */   private static void appendTextRow(CharBuffer buffer, Appendable dest, int startPos, int columns) throws IOException {
/* 1265 */     int limit = buffer.limit();
/* 1266 */     int pos = startPos;
/* 1267 */     dest.append('[');
/* 1268 */     dest.append(' ');
/* 1269 */     for (int c = 0; c < columns; c++) {
/* 1270 */       for (int i = 0; i < 8; i++) {
/* 1271 */         if (pos >= limit) {
/* 1272 */           dest.append(' ');
/*      */         } else {
/* 1274 */           char v = buffer.get(pos++);
/* 1275 */           if (Character.isISOControl(v) || Character.isHighSurrogate(v) || Character.isLowSurrogate(v)) {
/* 1276 */             dest.append('.');
/*      */           } else {
/* 1278 */             dest.append(v);
/*      */           } 
/*      */         } 
/*      */       } 
/* 1282 */       dest.append(' ');
/*      */     } 
/* 1284 */     dest.append(']');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1290 */   public static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocate(0);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1295 */   public static final Pooled<ByteBuffer> EMPTY_POOLED_BYTE_BUFFER = emptyPooledByteBuffer();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasRemaining(Buffer[] buffers, int offs, int len) {
/* 1306 */     for (int i = 0; i < len; i++) {
/* 1307 */       if (buffers[i + offs].hasRemaining()) {
/* 1308 */         return true;
/*      */       }
/*      */     } 
/* 1311 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasRemaining(Buffer[] buffers) {
/* 1321 */     return hasRemaining(buffers, 0, buffers.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long remaining(Buffer[] buffers, int offs, int len) {
/* 1333 */     long t = 0L;
/* 1334 */     for (int i = 0; i < len; i++) {
/* 1335 */       t += buffers[i + offs].remaining();
/*      */     }
/* 1337 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long remaining(Buffer[] buffers) {
/* 1347 */     return remaining(buffers, 0, buffers.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteBuffer putModifiedUtf8(ByteBuffer dest, String orig) throws BufferOverflowException {
/* 1360 */     char[] chars = orig.toCharArray();
/* 1361 */     for (char c : chars) {
/* 1362 */       if (c > '\000' && c <= '') {
/* 1363 */         dest.put((byte)c);
/* 1364 */       } else if (c <= '߿') {
/* 1365 */         dest.put((byte)(0xC0 | 0x1F & c >> 6));
/* 1366 */         dest.put((byte)(0x80 | 0x3F & c));
/*      */       } else {
/* 1368 */         dest.put((byte)(0xE0 | 0xF & c >> 12));
/* 1369 */         dest.put((byte)(0x80 | 0x3F & c >> 6));
/* 1370 */         dest.put((byte)(0x80 | 0x3F & c));
/*      */       } 
/*      */     } 
/* 1373 */     return dest;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getModifiedUtf8Z(ByteBuffer src) throws BufferUnderflowException {
/* 1384 */     StringBuilder builder = new StringBuilder();
/*      */     while (true) {
/* 1386 */       int ch = readUTFChar(src);
/* 1387 */       if (ch == -1) {
/* 1388 */         return builder.toString();
/*      */       }
/* 1390 */       builder.append((char)ch);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getModifiedUtf8(ByteBuffer src) throws BufferUnderflowException {
/* 1402 */     StringBuilder builder = new StringBuilder();
/* 1403 */     while (src.hasRemaining()) {
/* 1404 */       int ch = readUTFChar(src);
/* 1405 */       if (ch == -1) {
/* 1406 */         builder.append(false); continue;
/*      */       } 
/* 1408 */       builder.append((char)ch);
/*      */     } 
/*      */     
/* 1411 */     return builder.toString();
/*      */   }
/*      */   
/*      */   private static int readUTFChar(ByteBuffer src) throws BufferUnderflowException {
/* 1415 */     int a = src.get() & 0xFF;
/* 1416 */     if (a == 0)
/* 1417 */       return -1; 
/* 1418 */     if (a < 128)
/* 1419 */       return (char)a; 
/* 1420 */     if (a < 192)
/* 1421 */       return 63; 
/* 1422 */     if (a < 224) {
/* 1423 */       int b = src.get() & 0xFF;
/* 1424 */       if ((b & 0xC0) != 128) {
/* 1425 */         return 63;
/*      */       }
/* 1427 */       return (a & 0x1F) << 6 | b & 0x3F;
/* 1428 */     }  if (a < 240) {
/* 1429 */       int b = src.get() & 0xFF;
/* 1430 */       if ((b & 0xC0) != 128) {
/* 1431 */         return 63;
/*      */       }
/* 1433 */       int c = src.get() & 0xFF;
/* 1434 */       if ((c & 0xC0) != 128) {
/* 1435 */         return 63;
/*      */       }
/* 1437 */       return (a & 0xF) << 12 | (b & 0x3F) << 6 | c & 0x3F;
/*      */     } 
/* 1439 */     return 63;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean readAsciiZ(ByteBuffer src, StringBuilder builder) {
/* 1454 */     return readAsciiZ(src, builder, '?');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean readAsciiZ(ByteBuffer src, StringBuilder builder, char replacement) {
/*      */     while (true) {
/* 1471 */       if (!src.hasRemaining()) {
/* 1472 */         return false;
/*      */       }
/* 1474 */       byte b = src.get();
/* 1475 */       if (b == 0) {
/* 1476 */         return true;
/*      */       }
/* 1478 */       builder.append((b < 0) ? replacement : (char)b);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean readAsciiLine(ByteBuffer src, StringBuilder builder) {
/* 1494 */     return readAsciiLine(src, builder, '?', '\n');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean readAsciiLine(ByteBuffer src, StringBuilder builder, char replacement) {
/* 1510 */     return readAsciiLine(src, builder, replacement, '\n');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean readAsciiLine(ByteBuffer src, StringBuilder builder, char replacement, char delimiter) {
/*      */     while (true) {
/* 1529 */       if (!src.hasRemaining()) {
/* 1530 */         return false;
/*      */       }
/* 1532 */       byte b = src.get();
/* 1533 */       builder.append((b < 0) ? replacement : (char)b);
/* 1534 */       if (b == delimiter) {
/* 1535 */         return true;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void readAscii(ByteBuffer src, StringBuilder builder) {
/* 1549 */     readAscii(src, builder, '?');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void readAscii(ByteBuffer src, StringBuilder builder, char replacement) {
/*      */     while (true) {
/* 1563 */       if (!src.hasRemaining()) {
/*      */         return;
/*      */       }
/* 1566 */       byte b = src.get();
/* 1567 */       builder.append((b < 0) ? replacement : (char)b);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void readAscii(ByteBuffer src, StringBuilder builder, int limit, char replacement) {
/* 1582 */     while (limit > 0) {
/* 1583 */       if (!src.hasRemaining()) {
/*      */         return;
/*      */       }
/* 1586 */       byte b = src.get();
/* 1587 */       builder.append((b < 0) ? replacement : (char)b);
/* 1588 */       limit--;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean readLatin1Z(ByteBuffer src, StringBuilder builder) {
/*      */     while (true) {
/* 1604 */       if (!src.hasRemaining()) {
/* 1605 */         return false;
/*      */       }
/* 1607 */       byte b = src.get();
/* 1608 */       if (b == 0) {
/* 1609 */         return true;
/*      */       }
/* 1611 */       builder.append((char)(b & 0xFF));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean readLatin1Line(ByteBuffer src, StringBuilder builder) {
/*      */     while (true) {
/* 1627 */       if (!src.hasRemaining()) {
/* 1628 */         return false;
/*      */       }
/* 1630 */       byte b = src.get();
/* 1631 */       builder.append((char)(b & 0xFF));
/* 1632 */       if (b == 10) {
/* 1633 */         return true;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean readLatin1Line(ByteBuffer src, StringBuilder builder, char delimiter) {
/*      */     while (true) {
/* 1651 */       if (!src.hasRemaining()) {
/* 1652 */         return false;
/*      */       }
/* 1654 */       byte b = src.get();
/* 1655 */       builder.append((char)(b & 0xFF));
/* 1656 */       if (b == delimiter) {
/* 1657 */         return true;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void readLatin1(ByteBuffer src, StringBuilder builder) {
/*      */     while (true) {
/* 1671 */       if (!src.hasRemaining()) {
/*      */         return;
/*      */       }
/* 1674 */       byte b = src.get();
/* 1675 */       builder.append((char)(b & 0xFF));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean readModifiedUtf8Z(ByteBuffer src, StringBuilder builder) {
/* 1691 */     return readModifiedUtf8Z(src, builder, '?');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean readModifiedUtf8Z(ByteBuffer src, StringBuilder builder, char replacement) {
/*      */     while (true) {
/* 1708 */       if (!src.hasRemaining()) {
/* 1709 */         return false;
/*      */       }
/* 1711 */       int a = src.get() & 0xFF;
/* 1712 */       if (a == 0)
/* 1713 */         return true; 
/* 1714 */       if (a < 128) {
/* 1715 */         builder.append((char)a); continue;
/* 1716 */       }  if (a < 192) {
/* 1717 */         builder.append(replacement); continue;
/* 1718 */       }  if (a < 224) {
/* 1719 */         if (src.hasRemaining()) {
/* 1720 */           int b = src.get() & 0xFF;
/* 1721 */           if ((b & 0xC0) != 128) {
/* 1722 */             builder.append(replacement); continue;
/*      */           } 
/* 1724 */           builder.append((char)((a & 0x1F) << 6 | b & 0x3F));
/*      */           continue;
/*      */         } 
/* 1727 */         unget(src, 1);
/* 1728 */         return false;
/*      */       } 
/* 1730 */       if (a < 240) {
/* 1731 */         if (src.hasRemaining()) {
/* 1732 */           int b = src.get() & 0xFF;
/* 1733 */           if ((b & 0xC0) != 128) {
/* 1734 */             builder.append(replacement); continue;
/*      */           } 
/* 1736 */           if (src.hasRemaining()) {
/* 1737 */             int c = src.get() & 0xFF;
/* 1738 */             if ((c & 0xC0) != 128) {
/* 1739 */               builder.append(replacement); continue;
/*      */             } 
/* 1741 */             builder.append((char)((a & 0xF) << 12 | (b & 0x3F) << 6 | c & 0x3F));
/*      */             continue;
/*      */           } 
/* 1744 */           unget(src, 2);
/* 1745 */           return false;
/*      */         } 
/*      */ 
/*      */         
/* 1749 */         unget(src, 1);
/* 1750 */         return false;
/*      */       } 
/*      */       
/* 1753 */       builder.append(replacement);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean readModifiedUtf8Line(ByteBuffer src, StringBuilder builder) {
/* 1770 */     return readModifiedUtf8Line(src, builder, '?');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean readModifiedUtf8Line(ByteBuffer src, StringBuilder builder, char replacement) {
/* 1786 */     return readModifiedUtf8Line(src, builder, replacement, '\n');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean readModifiedUtf8Line(ByteBuffer src, StringBuilder builder, char replacement, char delimiter) {
/*      */     while (true) {
/* 1804 */       if (!src.hasRemaining()) {
/* 1805 */         return false;
/*      */       }
/* 1807 */       int a = src.get() & 0xFF;
/* 1808 */       if (a < 128) {
/* 1809 */         builder.append((char)a);
/* 1810 */         if (a == delimiter)
/* 1811 */           return true;  continue;
/*      */       } 
/* 1813 */       if (a < 192) {
/* 1814 */         builder.append(replacement); continue;
/* 1815 */       }  if (a < 224) {
/* 1816 */         if (src.hasRemaining()) {
/* 1817 */           int b = src.get() & 0xFF;
/* 1818 */           if ((b & 0xC0) != 128) {
/* 1819 */             builder.append(replacement); continue;
/*      */           } 
/* 1821 */           char ch = (char)((a & 0x1F) << 6 | b & 0x3F);
/* 1822 */           builder.append(ch);
/* 1823 */           if (ch == delimiter) {
/* 1824 */             return true;
/*      */           }
/*      */           continue;
/*      */         } 
/* 1828 */         unget(src, 1);
/* 1829 */         return false;
/*      */       } 
/* 1831 */       if (a < 240) {
/* 1832 */         if (src.hasRemaining()) {
/* 1833 */           int b = src.get() & 0xFF;
/* 1834 */           if ((b & 0xC0) != 128) {
/* 1835 */             builder.append(replacement); continue;
/*      */           } 
/* 1837 */           if (src.hasRemaining()) {
/* 1838 */             int c = src.get() & 0xFF;
/* 1839 */             if ((c & 0xC0) != 128) {
/* 1840 */               builder.append(replacement); continue;
/*      */             } 
/* 1842 */             char ch = (char)((a & 0xF) << 12 | (b & 0x3F) << 6 | c & 0x3F);
/* 1843 */             builder.append(ch);
/* 1844 */             if (ch == delimiter) {
/* 1845 */               return true;
/*      */             }
/*      */             continue;
/*      */           } 
/* 1849 */           unget(src, 2);
/* 1850 */           return false;
/*      */         } 
/*      */ 
/*      */         
/* 1854 */         unget(src, 1);
/* 1855 */         return false;
/*      */       } 
/*      */       
/* 1858 */       builder.append(replacement);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean readLine(ByteBuffer src, StringBuilder builder, CharsetDecoder decoder) {
/* 1878 */     return readLine(src, builder, decoder, '\n');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean readLine(ByteBuffer src, StringBuilder builder, CharsetDecoder decoder, char delimiter) {
/* 1897 */     CharBuffer oneChar = CharBuffer.allocate(1);
/*      */     while (true) {
/* 1899 */       CoderResult coderResult = decoder.decode(src, oneChar, false);
/* 1900 */       if (coderResult.isUnderflow()) {
/* 1901 */         if (oneChar.hasRemaining()) {
/* 1902 */           return false;
/*      */         }
/* 1904 */       } else if (oneChar.hasRemaining()) {
/* 1905 */         throw new IllegalStateException();
/*      */       } 
/* 1907 */       char ch = oneChar.get(0);
/* 1908 */       builder.append(ch);
/* 1909 */       if (ch == delimiter) {
/* 1910 */         return true;
/*      */       }
/* 1912 */       oneChar.clear();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <B extends Buffer> Pooled<B> pooledWrapper(final B buffer) {
/* 1925 */     return new Pooled<B>() {
/* 1926 */         private volatile B buf = (B)buffer;
/*      */         
/*      */         public void discard() {
/* 1929 */           this.buf = null;
/*      */         }
/*      */         
/*      */         public void free() {
/* 1933 */           this.buf = null;
/*      */         }
/*      */         
/*      */         public B getResource() throws IllegalStateException {
/* 1937 */           B buffer = this.buf;
/* 1938 */           if (buffer == null) {
/* 1939 */             throw new IllegalStateException();
/*      */           }
/* 1941 */           return buffer;
/*      */         }
/*      */         
/*      */         public void close() {
/* 1945 */           free();
/*      */         }
/*      */         
/*      */         public String toString() {
/* 1949 */           return "Pooled wrapper around " + buffer;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Pooled<ByteBuffer> globalPooledWrapper(final ByteBuffer buffer) {
/* 1962 */     return new Pooled<ByteBuffer>() {
/* 1963 */         private volatile ByteBuffer buf = buffer;
/*      */         
/*      */         public void discard() {
/* 1966 */           ByteBuffer oldBuf = this.buf;
/* 1967 */           if (oldBuf == null)
/* 1968 */             return;  final ByteBuffer buf = oldBuf.duplicate();
/* 1969 */           new CleanerReference(this.buf, null, new Reaper<ByteBuffer, Void>()
/*      */               {
/*      */                 public void reap(Reference<ByteBuffer, Void> reference) {
/* 1972 */                   ByteBufferPool.free(buf);
/*      */                 }
/*      */               });
/* 1975 */           this.buf = null;
/*      */         }
/*      */         
/*      */         public void free() {
/* 1979 */           ByteBuffer oldBuf = this.buf;
/* 1980 */           if (oldBuf == null)
/* 1981 */             return;  ByteBufferPool.free(oldBuf);
/* 1982 */           this.buf = null;
/*      */         }
/*      */         
/*      */         public ByteBuffer getResource() throws IllegalStateException {
/* 1986 */           ByteBuffer buffer = this.buf;
/* 1987 */           if (buffer == null) {
/* 1988 */             throw new IllegalStateException();
/*      */           }
/* 1990 */           return buffer;
/*      */         }
/*      */         
/*      */         public void close() {
/* 1994 */           free();
/*      */         }
/*      */         
/*      */         public String toString() {
/* 1998 */           return "Globally pooled wrapper around " + buffer;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Pooled<ByteBuffer> emptyPooledByteBuffer() {
/* 2010 */     return new Pooled<ByteBuffer>()
/*      */       {
/*      */         public void discard() {}
/*      */ 
/*      */         
/*      */         public void free() {}
/*      */         
/*      */         public ByteBuffer getResource() throws IllegalStateException {
/* 2018 */           return Buffers.EMPTY_BYTE_BUFFER;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         public void close() {}
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferAllocator<ByteBuffer> sliceAllocator(final ByteBuffer buffer) {
/* 2034 */     return new BufferAllocator<ByteBuffer>() {
/*      */         public ByteBuffer allocate(int size) throws IllegalArgumentException {
/* 2036 */           return Buffers.slice(buffer, size);
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <B extends Buffer> Pool<B> allocatedBufferPool(final BufferAllocator<B> allocator, final int size) {
/* 2050 */     return new Pool<B>() {
/*      */         public Pooled<B> allocate() {
/* 2052 */           return (Pooled)Buffers.pooledWrapper(allocator.allocate(size));
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Pool<ByteBuffer> secureBufferPool(Pool<ByteBuffer> delegate) {
/* 2064 */     return new SecureByteBufferPool(delegate);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isSecureBufferPool(Pool<?> pool) {
/* 2075 */     return pool instanceof SecureByteBufferPool;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void zero(ByteBuffer buffer) {
/* 2085 */     buffer.clear();
/* 2086 */     while (buffer.remaining() >= 8) {
/* 2087 */       buffer.putLong(0L);
/*      */     }
/* 2089 */     while (buffer.hasRemaining()) {
/* 2090 */       buffer.put((byte)0);
/*      */     }
/* 2092 */     buffer.clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void zero(CharBuffer buffer) {
/* 2102 */     buffer.clear();
/* 2103 */     while (buffer.remaining() >= 32) {
/* 2104 */       buffer.put("\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000");
/*      */     }
/* 2106 */     while (buffer.hasRemaining()) {
/* 2107 */       buffer.put(false);
/*      */     }
/* 2109 */     buffer.clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isDirect(Buffer... buffers) throws IllegalArgumentException {
/* 2120 */     return isDirect(buffers, 0, buffers.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isDirect(Buffer[] buffers, int offset, int length) {
/* 2131 */     boolean foundDirect = false;
/* 2132 */     boolean foundHeap = false;
/* 2133 */     for (int i = 0; i < length; i++) {
/* 2134 */       Buffer buffer = buffers[i + offset];
/* 2135 */       if (buffer == null) {
/* 2136 */         throw Messages.msg.nullParameter("buffer");
/*      */       }
/* 2138 */       if (buffer.isDirect()) {
/* 2139 */         if (foundHeap) {
/* 2140 */           throw Messages.msg.mixedDirectAndHeap();
/*      */         }
/* 2142 */         foundDirect = true;
/*      */       } else {
/* 2144 */         if (foundDirect) {
/* 2145 */           throw Messages.msg.mixedDirectAndHeap();
/*      */         }
/* 2147 */         foundHeap = true;
/*      */       } 
/*      */     } 
/* 2150 */     return foundDirect;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void assertWritable(Buffer[] buffers, int offs, int len) throws ReadOnlyBufferException {
/* 2162 */     for (int i = 0; i < len; i++) {
/* 2163 */       if (buffers[i + offs].isReadOnly()) {
/* 2164 */         throw Messages.msg.readOnlyBuffer();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void assertWritable(Buffer... buffers) throws ReadOnlyBufferException {
/* 2176 */     assertWritable(buffers, 0, buffers.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void addRandom(ByteBuffer target, Random random, int count) {
/* 2187 */     byte[] bytes = new byte[count];
/* 2188 */     random.nextBytes(bytes);
/* 2189 */     target.put(bytes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void addRandom(ByteBuffer target, int count) {
/* 2199 */     addRandom(target, IoUtils.getThreadLocalRandom(), count);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void addRandom(ByteBuffer target, Random random) {
/* 2209 */     if (target.remaining() == 0) {
/*      */       return;
/*      */     }
/* 2212 */     addRandom(target, random, random.nextInt(target.remaining()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void addRandom(ByteBuffer target) {
/* 2221 */     addRandom(target, IoUtils.getThreadLocalRandom());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int fillFromStream(ByteBuffer target, InputStream source) throws IOException {
/* 2234 */     int res, remaining = target.remaining();
/* 2235 */     if (remaining == 0) {
/* 2236 */       return 0;
/*      */     }
/* 2238 */     int p = target.position();
/* 2239 */     if (target.hasArray()) {
/*      */       int i;
/*      */       
/*      */       try {
/* 2243 */         i = source.read(target.array(), p + target.arrayOffset(), remaining);
/* 2244 */       } catch (InterruptedIOException e) {
/* 2245 */         target.position(p + e.bytesTransferred);
/* 2246 */         throw e;
/*      */       } 
/* 2248 */       if (i > 0) {
/* 2249 */         target.position(p + i);
/*      */       }
/* 2251 */       return i;
/*      */     } 
/* 2253 */     byte[] tmp = new byte[remaining];
/*      */     
/*      */     try {
/* 2256 */       res = source.read(tmp);
/* 2257 */     } catch (InterruptedIOException e) {
/* 2258 */       int n = e.bytesTransferred;
/* 2259 */       target.put(tmp, 0, n);
/* 2260 */       target.position(p + n);
/* 2261 */       throw e;
/*      */     } 
/* 2263 */     if (res > 0) {
/* 2264 */       target.put(tmp, 0, res);
/*      */     }
/* 2266 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String debugString(ByteBuffer buffer) {
/* 2278 */     StringBuilder b = new StringBuilder();
/* 2279 */     b.append("1 buffer of ").append(buffer.remaining()).append(" bytes");
/* 2280 */     return b.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String debugString(ByteBuffer[] buffers, int offs, int len) {
/* 2292 */     StringBuilder b = new StringBuilder();
/* 2293 */     b.append(len).append(" buffer(s)");
/* 2294 */     if (len > 0) {
/* 2295 */       b.append(" of ").append(remaining((Buffer[])buffers, offs, len)).append(" bytes");
/*      */     }
/* 2297 */     return b.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void emptyToStream(OutputStream target, ByteBuffer source) throws IOException {
/* 2309 */     int remaining = source.remaining();
/* 2310 */     if (remaining == 0) {
/*      */       return;
/*      */     }
/* 2313 */     int p = source.position();
/* 2314 */     if (source.hasArray()) {
/*      */       
/*      */       try {
/* 2317 */         target.write(source.array(), p + source.arrayOffset(), remaining);
/* 2318 */       } catch (InterruptedIOException e) {
/* 2319 */         source.position(p + e.bytesTransferred);
/* 2320 */         throw e;
/*      */       } 
/* 2322 */       source.position(source.limit());
/*      */       return;
/*      */     } 
/* 2325 */     byte[] tmp = take(source);
/*      */     try {
/* 2327 */       target.write(tmp);
/* 2328 */     } catch (InterruptedIOException e) {
/* 2329 */       source.position(p + e.bytesTransferred);
/* 2330 */       throw e;
/* 2331 */     } catch (IOException e) {
/* 2332 */       source.position(p);
/* 2333 */       throw e;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private static class SecureByteBufferPool
/*      */     implements Pool<ByteBuffer>
/*      */   {
/*      */     private final Pool<ByteBuffer> delegate;
/*      */     
/*      */     SecureByteBufferPool(Pool<ByteBuffer> delegate) {
/* 2344 */       this.delegate = delegate;
/*      */     }
/*      */     
/*      */     public Pooled<ByteBuffer> allocate() {
/* 2348 */       return new Buffers.SecurePooledByteBuffer(this.delegate.allocate());
/*      */     }
/*      */   }
/*      */   
/*      */   private static class SecurePooledByteBuffer
/*      */     implements Pooled<ByteBuffer> {
/* 2354 */     private static final AtomicIntegerFieldUpdater<SecurePooledByteBuffer> freedUpdater = AtomicIntegerFieldUpdater.newUpdater(SecurePooledByteBuffer.class, "freed");
/*      */     
/*      */     private final Pooled<ByteBuffer> allocated;
/*      */     
/*      */     private volatile int freed;
/*      */     
/*      */     SecurePooledByteBuffer(Pooled<ByteBuffer> allocated) {
/* 2361 */       this.allocated = allocated;
/*      */     }
/*      */     
/*      */     public void discard() {
/* 2365 */       if (freedUpdater.compareAndSet(this, 0, 1)) {
/* 2366 */         Buffers.zero(this.allocated.getResource());
/* 2367 */         this.allocated.discard();
/*      */       } 
/*      */     }
/*      */     
/*      */     public void free() {
/* 2372 */       if (freedUpdater.compareAndSet(this, 0, 1)) {
/* 2373 */         Buffers.zero(this.allocated.getResource());
/* 2374 */         this.allocated.free();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public ByteBuffer getResource() throws IllegalStateException {
/* 2380 */       return this.allocated.getResource();
/*      */     }
/*      */     
/*      */     public void close() {
/* 2384 */       free();
/*      */     }
/*      */     
/*      */     public String toString() {
/* 2388 */       return "Secure wrapper around " + this.allocated;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\Buffers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */