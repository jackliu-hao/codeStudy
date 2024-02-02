/*     */ package io.undertow.websockets.core;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.util.Transfer;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
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
/*     */ public final class WebSocketUtils
/*     */ {
/*     */   private static final String EMPTY = "";
/*     */   
/*     */   public static ByteBuffer fromUtf8String(CharSequence utfString) {
/*  55 */     if (utfString == null || utfString.length() == 0) {
/*  56 */       return Buffers.EMPTY_BYTE_BUFFER;
/*     */     }
/*  58 */     return ByteBuffer.wrap(utfString.toString().getBytes(StandardCharsets.UTF_8));
/*     */   }
/*     */ 
/*     */   
/*     */   public static String toUtf8String(ByteBuffer buffer) {
/*  63 */     if (!buffer.hasRemaining()) {
/*  64 */       return "";
/*     */     }
/*  66 */     if (buffer.hasArray()) {
/*  67 */       return new String(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining(), StandardCharsets.UTF_8);
/*     */     }
/*  69 */     byte[] content = new byte[buffer.remaining()];
/*  70 */     buffer.get(content);
/*  71 */     return new String(content, StandardCharsets.UTF_8);
/*     */   }
/*     */ 
/*     */   
/*     */   public static String toUtf8String(ByteBuffer... buffers) {
/*  76 */     int size = 0;
/*  77 */     for (ByteBuffer buf : buffers) {
/*  78 */       size += buf.remaining();
/*     */     }
/*  80 */     if (size == 0) {
/*  81 */       return "";
/*     */     }
/*     */     
/*  84 */     int index = 0;
/*  85 */     byte[] bytes = new byte[size];
/*  86 */     for (ByteBuffer buf : buffers) {
/*  87 */       if (buf.hasArray()) {
/*  88 */         int len = buf.remaining();
/*  89 */         System.arraycopy(buf.array(), buf.arrayOffset() + buf.position(), bytes, index, len);
/*  90 */         index += len;
/*     */       } else {
/*  92 */         int len = buf.remaining();
/*  93 */         buf.get(bytes, index, len);
/*  94 */         index += len;
/*     */       } 
/*     */     } 
/*  97 */     return new String(bytes, StandardCharsets.UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long transfer(ReadableByteChannel source, long count, ByteBuffer throughBuffer, WritableByteChannel sink) throws IOException {
/* 104 */     long total = 0L;
/* 105 */     while (total < count) {
/* 106 */       throughBuffer.clear();
/* 107 */       if (count - total < throughBuffer.remaining()) {
/* 108 */         throughBuffer.limit((int)(count - total));
/*     */       }
/*     */       
/*     */       try {
/* 112 */         long res = source.read(throughBuffer);
/* 113 */         if (res <= 0L) {
/* 114 */           return (total == 0L) ? res : total;
/*     */         }
/*     */       } finally {
/* 117 */         throughBuffer.flip();
/*     */       } 
/*     */       
/* 120 */       while (throughBuffer.hasRemaining()) {
/* 121 */         long res = sink.write(throughBuffer);
/* 122 */         if (res <= 0L) {
/* 123 */           return total;
/*     */         }
/* 125 */         total += res;
/*     */       } 
/*     */     } 
/* 128 */     return total;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void echoFrame(final WebSocketChannel channel, StreamSourceFrameChannel ws) throws IOException {
/*     */     final WebSocketFrameType type;
/* 137 */     switch (ws.getType()) {
/*     */       
/*     */       case PONG:
/* 140 */         ws.close();
/*     */         return;
/*     */       
/*     */       case PING:
/* 144 */         type = WebSocketFrameType.PONG;
/*     */         break;
/*     */       default:
/* 147 */         type = ws.getType();
/*     */         break;
/*     */     } 
/* 150 */     StreamSinkFrameChannel sink = channel.send(type);
/* 151 */     sink.setRsv(ws.getRsv());
/* 152 */     initiateTransfer(ws, sink, new ChannelListener<StreamSourceFrameChannel>()
/*     */         {
/*     */           public void handleEvent(StreamSourceFrameChannel streamSourceFrameChannel) {
/* 155 */             IoUtils.safeClose((Closeable)streamSourceFrameChannel);
/*     */           }
/*     */         },  new ChannelListener<StreamSinkFrameChannel>()
/*     */         {
/*     */           public void handleEvent(StreamSinkFrameChannel streamSinkFrameChannel) {
/*     */             try {
/* 161 */               streamSinkFrameChannel.shutdownWrites();
/* 162 */             } catch (IOException e) {
/* 163 */               UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 164 */               IoUtils.safeClose(new Closeable[] { (Closeable)streamSinkFrameChannel, (Closeable)this.val$channel }, );
/*     */               return;
/*     */             } 
/*     */             try {
/* 168 */               if (!streamSinkFrameChannel.flush()) {
/* 169 */                 streamSinkFrameChannel.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkFrameChannel>()
/*     */                       {
/*     */                         public void handleEvent(StreamSinkFrameChannel streamSinkFrameChannel)
/*     */                         {
/* 173 */                           streamSinkFrameChannel.getWriteSetter().set(null);
/* 174 */                           IoUtils.safeClose((Closeable)streamSinkFrameChannel);
/* 175 */                           if (type == WebSocketFrameType.CLOSE) {
/* 176 */                             IoUtils.safeClose((Closeable)channel);
/*     */                           }
/*     */                         }
/*     */                       },  new ChannelExceptionHandler<StreamSinkFrameChannel>()
/*     */                       {
/*     */                         public void handleException(StreamSinkFrameChannel streamSinkFrameChannel, IOException e)
/*     */                         {
/* 183 */                           UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 184 */                           IoUtils.safeClose(new Closeable[] { (Closeable)streamSinkFrameChannel, (Closeable)this.this$0.val$channel });
/*     */                         }
/*     */                       }));
/*     */ 
/*     */                 
/* 189 */                 streamSinkFrameChannel.resumeWrites();
/*     */               } else {
/* 191 */                 if (type == WebSocketFrameType.CLOSE) {
/* 192 */                   IoUtils.safeClose((Closeable)channel);
/*     */                 }
/* 194 */                 streamSinkFrameChannel.getWriteSetter().set(null);
/* 195 */                 IoUtils.safeClose((Closeable)streamSinkFrameChannel);
/*     */               } 
/* 197 */             } catch (IOException e) {
/* 198 */               UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 199 */               IoUtils.safeClose(new Closeable[] { (Closeable)streamSinkFrameChannel, (Closeable)this.val$channel });
/*     */             } 
/*     */           }
/*     */         }new ChannelExceptionHandler<StreamSourceFrameChannel>()
/*     */         {
/*     */           public void handleException(StreamSourceFrameChannel streamSourceFrameChannel, IOException e)
/*     */           {
/* 206 */             UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 207 */             IoUtils.safeClose(new Closeable[] { (Closeable)streamSourceFrameChannel, (Closeable)this.val$channel }, );
/*     */           }
/*     */         },  new ChannelExceptionHandler<StreamSinkFrameChannel>()
/*     */         {
/*     */           public void handleException(StreamSinkFrameChannel streamSinkFrameChannel, IOException e) {
/* 212 */             UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 213 */             IoUtils.safeClose(new Closeable[] { (Closeable)streamSinkFrameChannel, (Closeable)this.val$channel }, );
/*     */           }
/* 215 */         },  channel.getBufferPool());
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
/*     */   @Deprecated
/*     */   public static <I extends StreamSourceChannel, O extends StreamSinkChannel> void initiateTransfer(I source, O sink, ChannelListener<? super I> sourceListener, ChannelListener<? super O> sinkListener, ChannelExceptionHandler<? super I> readExceptionHandler, ChannelExceptionHandler<? super O> writeExceptionHandler, ByteBufferPool pool) {
/* 234 */     Transfer.initiateTransfer((StreamSourceChannel)source, (StreamSinkChannel)sink, sourceListener, sinkListener, readExceptionHandler, writeExceptionHandler, pool);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\WebSocketUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */