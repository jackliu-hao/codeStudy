/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.charset.Charset;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
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
/*     */ public class StringWriteChannelListener
/*     */   implements ChannelListener<StreamSinkChannel>
/*     */ {
/*     */   private final ByteBuffer buffer;
/*     */   
/*     */   public StringWriteChannelListener(String string) {
/*  45 */     this(string, Charset.defaultCharset());
/*     */   }
/*     */   
/*     */   public StringWriteChannelListener(String string, Charset charset) {
/*  49 */     this.buffer = ByteBuffer.wrap(string.getBytes(charset));
/*     */   }
/*     */   
/*     */   public void setup(StreamSinkChannel channel) {
/*     */     try {
/*     */       int c;
/*     */       do {
/*  56 */         c = channel.write(this.buffer);
/*  57 */       } while (this.buffer.hasRemaining() && c > 0);
/*  58 */       if (this.buffer.hasRemaining()) {
/*  59 */         channel.getWriteSetter().set(this);
/*  60 */         channel.resumeWrites();
/*     */       } else {
/*  62 */         writeDone(channel);
/*     */       } 
/*  64 */     } catch (IOException e) {
/*  65 */       handleError(channel, e);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void handleError(StreamSinkChannel channel, IOException e) {
/*  70 */     UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/*  71 */     IoUtils.safeClose((Closeable)channel);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleEvent(StreamSinkChannel channel) {
/*     */     try {
/*     */       int c;
/*     */       do {
/*  79 */         c = channel.write(this.buffer);
/*  80 */       } while (this.buffer.hasRemaining() && c > 0);
/*  81 */       if (this.buffer.hasRemaining()) {
/*  82 */         channel.resumeWrites();
/*     */         return;
/*     */       } 
/*  85 */       writeDone(channel);
/*     */     }
/*  87 */     catch (IOException e) {
/*  88 */       handleError(channel, e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean hasRemaining() {
/*  93 */     return this.buffer.hasRemaining();
/*     */   }
/*     */   
/*     */   protected void writeDone(final StreamSinkChannel channel) {
/*     */     try {
/*  98 */       channel.shutdownWrites();
/*     */       
/* 100 */       if (!channel.flush()) {
/* 101 */         channel.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>()
/*     */               {
/*     */                 public void handleEvent(StreamSinkChannel o) {
/* 104 */                   IoUtils.safeClose((Closeable)channel);
/*     */                 }
/* 106 */               },  ChannelListeners.closingChannelExceptionHandler()));
/* 107 */         channel.resumeWrites();
/*     */       }
/*     */     
/* 110 */     } catch (IOException e) {
/* 111 */       handleError(channel, e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\StringWriteChannelListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */