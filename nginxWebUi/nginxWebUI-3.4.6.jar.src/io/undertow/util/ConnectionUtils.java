/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.conduits.ConduitStreamSinkChannel;
/*     */ import org.xnio.conduits.ConduitStreamSourceChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConnectionUtils
/*     */ {
/*  41 */   private static final long MAX_DRAIN_TIME = Long.getLong("io.undertow.max-drain-time", 10000L).longValue();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void cleanClose(final StreamConnection connection, Closeable... additional) {
/*     */     try {
/*  57 */       connection.getSinkChannel().shutdownWrites();
/*  58 */       if (!connection.getSinkChannel().flush()) {
/*  59 */         connection.getSinkChannel().setWriteListener(ChannelListeners.flushingChannelListener(new ChannelListener<ConduitStreamSinkChannel>()
/*     */               {
/*     */                 public void handleEvent(ConduitStreamSinkChannel channel) {
/*  62 */                   ConnectionUtils.doDrain(connection, additional);
/*     */                 }
/*     */               }new ChannelExceptionHandler<ConduitStreamSinkChannel>()
/*     */               {
/*     */                 public void handleException(ConduitStreamSinkChannel channel, IOException exception) {
/*  67 */                   UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
/*  68 */                   IoUtils.safeClose((Closeable)connection);
/*  69 */                   IoUtils.safeClose(additional);
/*     */                 }
/*     */               }));
/*  72 */         connection.getSinkChannel().resumeWrites();
/*     */       } else {
/*  74 */         doDrain(connection, additional);
/*     */       }
/*     */     
/*  77 */     } catch (Throwable e) {
/*  78 */       if (e instanceof IOException) {
/*  79 */         UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)e);
/*     */       } else {
/*  81 */         UndertowLogger.REQUEST_IO_LOGGER.ioException(new IOException(e));
/*     */       } 
/*  83 */       IoUtils.safeClose((Closeable)connection);
/*  84 */       IoUtils.safeClose(additional);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void doDrain(final StreamConnection connection, Closeable... additional) {
/*  89 */     if (!connection.getSourceChannel().isOpen()) {
/*  90 */       IoUtils.safeClose((Closeable)connection);
/*  91 */       IoUtils.safeClose(additional);
/*     */       return;
/*     */     } 
/*  94 */     final ByteBuffer b = ByteBuffer.allocate(1);
/*     */     try {
/*  96 */       int res = connection.getSourceChannel().read(b);
/*  97 */       b.clear();
/*  98 */       if (res == 0) {
/*  99 */         final XnioExecutor.Key key = WorkerUtils.executeAfter(connection.getIoThread(), new Runnable()
/*     */             {
/*     */               public void run() {
/* 102 */                 IoUtils.safeClose((Closeable)connection);
/* 103 */                 IoUtils.safeClose(additional);
/*     */               }
/*     */             },  MAX_DRAIN_TIME, TimeUnit.MILLISECONDS);
/* 106 */         connection.getSourceChannel().setReadListener(new ChannelListener<ConduitStreamSourceChannel>()
/*     */             {
/*     */               public void handleEvent(ConduitStreamSourceChannel channel) {
/*     */                 try {
/* 110 */                   int res = channel.read(b);
/* 111 */                   if (res != 0) {
/* 112 */                     IoUtils.safeClose((Closeable)connection);
/* 113 */                     IoUtils.safeClose(additional);
/* 114 */                     key.remove();
/*     */                   } 
/* 116 */                 } catch (Exception e) {
/* 117 */                   if (e instanceof IOException) {
/* 118 */                     UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)e);
/*     */                   } else {
/* 120 */                     UndertowLogger.REQUEST_IO_LOGGER.ioException(new IOException(e));
/*     */                   } 
/* 122 */                   IoUtils.safeClose((Closeable)connection);
/* 123 */                   IoUtils.safeClose(additional);
/* 124 */                   key.remove();
/*     */                 } 
/*     */               }
/*     */             });
/* 128 */         connection.getSourceChannel().resumeReads();
/*     */       } else {
/* 130 */         IoUtils.safeClose((Closeable)connection);
/* 131 */         IoUtils.safeClose(additional);
/*     */       } 
/* 133 */     } catch (Throwable e) {
/* 134 */       if (e instanceof IOException) {
/* 135 */         UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)e);
/*     */       } else {
/* 137 */         UndertowLogger.REQUEST_IO_LOGGER.ioException(new IOException(e));
/*     */       } 
/* 139 */       IoUtils.safeClose((Closeable)connection);
/* 140 */       IoUtils.safeClose(additional);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\ConnectionUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */