/*     */ package io.undertow.websockets.core;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Pooled;
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
/*     */ public abstract class AbstractReceiveListener
/*     */   implements ChannelListener<WebSocketChannel>
/*     */ {
/*     */   public void handleEvent(WebSocketChannel channel) {
/*     */     try {
/*  38 */       StreamSourceFrameChannel result = (StreamSourceFrameChannel)channel.receive();
/*  39 */       if (result == null)
/*     */         return; 
/*  41 */       if (result.getType() == WebSocketFrameType.BINARY) {
/*  42 */         onBinary(channel, result);
/*  43 */       } else if (result.getType() == WebSocketFrameType.TEXT) {
/*  44 */         onText(channel, result);
/*  45 */       } else if (result.getType() == WebSocketFrameType.PONG) {
/*  46 */         onPong(channel, result);
/*  47 */       } else if (result.getType() == WebSocketFrameType.PING) {
/*  48 */         onPing(channel, result);
/*  49 */       } else if (result.getType() == WebSocketFrameType.CLOSE) {
/*  50 */         onClose(channel, result);
/*     */       } 
/*  52 */     } catch (IOException e) {
/*  53 */       onError(channel, e);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void onPing(WebSocketChannel webSocketChannel, StreamSourceFrameChannel channel) throws IOException {
/*  58 */     bufferFullMessage(channel);
/*     */   }
/*     */   
/*     */   protected void onClose(WebSocketChannel webSocketChannel, StreamSourceFrameChannel channel) throws IOException {
/*  62 */     bufferFullMessage(channel);
/*     */   }
/*     */   
/*     */   protected void onPong(WebSocketChannel webSocketChannel, StreamSourceFrameChannel messageChannel) throws IOException {
/*  66 */     bufferFullMessage(messageChannel);
/*     */   }
/*     */   
/*     */   protected void onText(WebSocketChannel webSocketChannel, StreamSourceFrameChannel messageChannel) throws IOException {
/*  70 */     bufferFullMessage(messageChannel);
/*     */   }
/*     */   
/*     */   protected void onBinary(WebSocketChannel webSocketChannel, StreamSourceFrameChannel messageChannel) throws IOException {
/*  74 */     bufferFullMessage(messageChannel);
/*     */   }
/*     */   
/*     */   protected void onError(WebSocketChannel channel, Throwable error) {
/*  78 */     IoUtils.safeClose((Closeable)channel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void bufferFullMessage(StreamSourceFrameChannel messageChannel) {
/*  89 */     if (messageChannel.getType() == WebSocketFrameType.TEXT) {
/*  90 */       readBufferedText(messageChannel, new BufferedTextMessage(getMaxTextBufferSize(), true));
/*  91 */     } else if (messageChannel.getType() == WebSocketFrameType.BINARY) {
/*  92 */       readBufferedBinary(messageChannel, false, new BufferedBinaryMessage(getMaxBinaryBufferSize(), true));
/*  93 */     } else if (messageChannel.getType() == WebSocketFrameType.PONG) {
/*  94 */       readBufferedBinary(messageChannel, true, new BufferedBinaryMessage(getMaxPongBufferSize(), true));
/*  95 */     } else if (messageChannel.getType() == WebSocketFrameType.PING) {
/*  96 */       readBufferedBinary(messageChannel, true, new BufferedBinaryMessage(getMaxPingBufferSize(), true));
/*  97 */     } else if (messageChannel.getType() == WebSocketFrameType.CLOSE) {
/*  98 */       readBufferedBinary(messageChannel, true, new BufferedBinaryMessage(getMaxCloseBufferSize(), true));
/*     */     } 
/*     */   }
/*     */   
/*     */   protected long getMaxBinaryBufferSize() {
/* 103 */     return -1L;
/*     */   }
/*     */   
/*     */   protected long getMaxPongBufferSize() {
/* 107 */     return -1L;
/*     */   }
/*     */   
/*     */   protected long getMaxCloseBufferSize() {
/* 111 */     return -1L;
/*     */   }
/*     */   
/*     */   protected long getMaxPingBufferSize() {
/* 115 */     return -1L;
/*     */   }
/*     */   
/*     */   protected long getMaxTextBufferSize() {
/* 119 */     return -1L;
/*     */   }
/*     */ 
/*     */   
/*     */   private void readBufferedBinary(final StreamSourceFrameChannel messageChannel, final boolean controlFrame, final BufferedBinaryMessage buffer) {
/* 124 */     buffer.read(messageChannel, new WebSocketCallback<BufferedBinaryMessage>()
/*     */         {
/*     */           public void complete(WebSocketChannel channel, BufferedBinaryMessage context) {
/*     */             try {
/* 128 */               WebSocketFrameType type = messageChannel.getType();
/* 129 */               if (!controlFrame) {
/* 130 */                 AbstractReceiveListener.this.onFullBinaryMessage(channel, buffer);
/* 131 */               } else if (type == WebSocketFrameType.PONG) {
/* 132 */                 AbstractReceiveListener.this.onFullPongMessage(channel, buffer);
/* 133 */               } else if (type == WebSocketFrameType.PING) {
/* 134 */                 AbstractReceiveListener.this.onFullPingMessage(channel, buffer);
/* 135 */               } else if (type == WebSocketFrameType.CLOSE) {
/* 136 */                 AbstractReceiveListener.this.onFullCloseMessage(channel, buffer);
/*     */               } 
/* 138 */             } catch (IOException e) {
/* 139 */               AbstractReceiveListener.this.onError(channel, e);
/*     */             } 
/*     */           }
/*     */ 
/*     */           
/*     */           public void onError(WebSocketChannel channel, BufferedBinaryMessage context, Throwable throwable) {
/* 145 */             context.getData().close();
/* 146 */             AbstractReceiveListener.this.onError(channel, throwable);
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   private void readBufferedText(StreamSourceFrameChannel messageChannel, final BufferedTextMessage textMessage) {
/* 152 */     textMessage.read(messageChannel, new WebSocketCallback<BufferedTextMessage>()
/*     */         {
/*     */           public void complete(WebSocketChannel channel, BufferedTextMessage context) {
/*     */             try {
/* 156 */               AbstractReceiveListener.this.onFullTextMessage(channel, textMessage);
/* 157 */             } catch (IOException e) {
/* 158 */               AbstractReceiveListener.this.onError(channel, e);
/*     */             } 
/*     */           }
/*     */ 
/*     */           
/*     */           public void onError(WebSocketChannel channel, BufferedTextMessage context, Throwable throwable) {
/* 164 */             AbstractReceiveListener.this.onError(channel, throwable);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {}
/*     */   
/*     */   protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
/* 173 */     message.getData().free();
/*     */   }
/*     */   
/*     */   protected void onFullPingMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
/* 177 */     Pooled<ByteBuffer[]> data = message.getData();
/* 178 */     WebSockets.sendPong((ByteBuffer[])data.getResource(), channel, new FreeDataCallback(data));
/*     */   }
/*     */   
/*     */   protected void onFullPongMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
/* 182 */     message.getData().free();
/*     */   }
/*     */   
/*     */   protected void onFullCloseMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
/* 186 */     Pooled<ByteBuffer[]> data = message.getData();
/*     */     try {
/* 188 */       CloseMessage cm = new CloseMessage((ByteBuffer[])data.getResource());
/* 189 */       onCloseMessage(cm, channel);
/* 190 */       if (!channel.isCloseFrameSent()) {
/* 191 */         WebSockets.sendClose(cm, channel, (WebSocketCallback<Void>)null);
/*     */       }
/*     */     } finally {
/* 194 */       data.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void onCloseMessage(CloseMessage cm, WebSocketChannel channel) {}
/*     */   
/*     */   private static class FreeDataCallback
/*     */     implements WebSocketCallback<Void> {
/*     */     private final Pooled<ByteBuffer[]> data;
/*     */     
/*     */     FreeDataCallback(Pooled<ByteBuffer[]> data) {
/* 205 */       this.data = data;
/*     */     }
/*     */ 
/*     */     
/*     */     public void complete(WebSocketChannel channel, Void context) {
/* 210 */       this.data.close();
/*     */     }
/*     */ 
/*     */     
/*     */     public void onError(WebSocketChannel channel, Void context, Throwable throwable) {
/* 215 */       this.data.close();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\AbstractReceiveListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */