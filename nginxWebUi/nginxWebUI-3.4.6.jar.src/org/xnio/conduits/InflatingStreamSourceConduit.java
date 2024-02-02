/*     */ package org.xnio.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Inflater;
/*     */ import org.xnio._private.Messages;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class InflatingStreamSourceConduit
/*     */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*     */   implements StreamSourceConduit
/*     */ {
/*     */   private final Inflater inflater;
/*     */   private final ByteBuffer buffer;
/*     */   
/*     */   public InflatingStreamSourceConduit(StreamSourceConduit next, Inflater inflater) {
/*  48 */     super(next);
/*  49 */     this.inflater = inflater;
/*  50 */     this.buffer = ByteBuffer.allocate(16384);
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*  54 */     return target.transferFrom(new ConduitReadableByteChannel(this), position, count);
/*     */   }
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*  58 */     return Conduits.transfer(this, count, throughBuffer, (WritableByteChannel)target);
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/*  62 */     int res, remaining = dst.remaining();
/*  63 */     int position = dst.position();
/*  64 */     Inflater inflater = this.inflater;
/*     */     
/*  66 */     if (dst.hasArray()) {
/*     */       
/*  68 */       byte[] array = dst.array();
/*  69 */       int off = dst.arrayOffset();
/*     */       while (true) {
/*     */         try {
/*  72 */           res = inflater.inflate(array, off + position, remaining);
/*  73 */         } catch (DataFormatException e) {
/*  74 */           throw new IOException(e);
/*     */         } 
/*  76 */         if (res > 0) {
/*  77 */           dst.position(position + res);
/*  78 */           return res;
/*     */         } 
/*  80 */         if (inflater.needsDictionary()) {
/*  81 */           throw Messages.msg.inflaterNeedsDictionary();
/*     */         }
/*  83 */         ByteBuffer buffer = this.buffer;
/*  84 */         buffer.clear();
/*  85 */         res = this.next.read(buffer);
/*  86 */         if (res > 0) {
/*  87 */           inflater.setInput(buffer.array(), buffer.arrayOffset(), res); continue;
/*     */         }  break;
/*  89 */       }  return res;
/*     */     } 
/*     */ 
/*     */     
/*  93 */     byte[] space = new byte[remaining];
/*     */     while (true) {
/*     */       try {
/*  96 */         res = inflater.inflate(space);
/*  97 */       } catch (DataFormatException e) {
/*  98 */         throw new IOException(e);
/*     */       } 
/* 100 */       if (res > 0) {
/* 101 */         dst.put(space, 0, res);
/* 102 */         return res;
/*     */       } 
/* 104 */       if (inflater.needsDictionary()) {
/* 105 */         throw Messages.msg.inflaterNeedsDictionary();
/*     */       }
/* 107 */       ByteBuffer buffer = this.buffer;
/* 108 */       buffer.clear();
/* 109 */       res = this.next.read(buffer);
/* 110 */       if (res > 0) {
/* 111 */         inflater.setInput(buffer.array(), buffer.arrayOffset(), res); continue;
/*     */       }  break;
/* 113 */     }  return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts) throws IOException {
/* 120 */     return read(dsts, 0, dsts.length);
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 124 */     for (int i = 0; i < length; i++) {
/* 125 */       ByteBuffer buffer = dsts[i + offset];
/* 126 */       if (buffer.hasRemaining()) {
/* 127 */         return read(buffer);
/*     */       }
/*     */     } 
/* 130 */     return 0L;
/*     */   }
/*     */   
/*     */   public void terminateReads() throws IOException {
/* 134 */     this.inflater.end();
/* 135 */     this.next.terminateReads();
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 139 */     if (!this.inflater.needsInput()) {
/*     */       return;
/*     */     }
/* 142 */     this.next.awaitReadable();
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 146 */     if (!this.inflater.needsInput()) {
/*     */       return;
/*     */     }
/* 149 */     this.next.awaitReadable(time, timeUnit);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\InflatingStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */