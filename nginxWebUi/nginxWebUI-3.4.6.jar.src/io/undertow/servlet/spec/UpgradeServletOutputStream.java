/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.WriteListener;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.Channels;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.SuspendableWriteChannel;
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
/*     */ public class UpgradeServletOutputStream
/*     */   extends ServletOutputStream
/*     */ {
/*     */   private final StreamSinkChannel channel;
/*     */   private WriteListener listener;
/*     */   private final Executor ioExecutor;
/*     */   private static final int FLAG_READY = 1;
/*     */   private static final int FLAG_CLOSED = 2;
/*     */   private static final int FLAG_DELEGATE_SHUTDOWN = 4;
/*     */   private int state;
/*     */   private ByteBuffer buffer;
/*     */   
/*     */   protected UpgradeServletOutputStream(StreamSinkChannel channel, Executor ioExecutor) {
/*  64 */     this.channel = channel;
/*  65 */     this.ioExecutor = ioExecutor;
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/*  70 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/*  75 */     if (Bits.anyAreSet(this.state, 2)) {
/*  76 */       throw UndertowServletMessages.MESSAGES.streamIsClosed();
/*     */     }
/*  78 */     if (this.listener == null) {
/*  79 */       Channels.writeBlocking((WritableByteChannel)this.channel, ByteBuffer.wrap(b, off, len));
/*     */     } else {
/*  81 */       if (Bits.anyAreClear(this.state, 1)) {
/*  82 */         throw UndertowServletMessages.MESSAGES.streamNotReady();
/*     */       }
/*     */       
/*  85 */       ByteBuffer buffer = ByteBuffer.wrap(b);
/*     */       do {
/*  87 */         int res = this.channel.write(buffer);
/*  88 */         if (res == 0) {
/*     */           
/*  90 */           ByteBuffer copy = ByteBuffer.allocate(buffer.remaining());
/*  91 */           copy.put(buffer);
/*  92 */           copy.flip();
/*  93 */           this.buffer = copy;
/*  94 */           this.state &= 0xFFFFFFFE;
/*  95 */           if (Thread.currentThread() == this.channel.getIoThread()) {
/*  96 */             this.channel.resumeWrites();
/*     */           } else {
/*  98 */             this.ioExecutor.execute(new Runnable()
/*     */                 {
/*     */                   public void run() {
/* 101 */                     UpgradeServletOutputStream.this.channel.resumeWrites();
/*     */                   }
/*     */                 });
/*     */           } 
/*     */           return;
/*     */         } 
/* 107 */       } while (buffer.hasRemaining());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 113 */     write(new byte[] { (byte)b }, 0, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 118 */     if (Bits.anyAreSet(this.state, 2)) {
/* 119 */       throw UndertowServletMessages.MESSAGES.streamIsClosed();
/*     */     }
/* 121 */     if (this.listener == null) {
/* 122 */       Channels.flushBlocking((SuspendableWriteChannel)this.channel);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 128 */     this.state |= 0x2;
/* 129 */     this.state &= 0xFFFFFFFE;
/* 130 */     if (this.listener == null) {
/* 131 */       this.channel.shutdownWrites();
/* 132 */       this.state |= 0x4;
/* 133 */       Channels.flushBlocking((SuspendableWriteChannel)this.channel);
/*     */     }
/* 135 */     else if (this.buffer == null) {
/* 136 */       this.channel.shutdownWrites();
/* 137 */       this.state |= 0x4;
/* 138 */       if (!this.channel.flush()) {
/* 139 */         if (Thread.currentThread() == this.channel.getIoThread()) {
/* 140 */           this.channel.resumeWrites();
/*     */         } else {
/* 142 */           this.ioExecutor.execute(new Runnable()
/*     */               {
/*     */                 public void run() {
/* 145 */                   UpgradeServletOutputStream.this.channel.resumeWrites();
/*     */                 }
/*     */               });
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void closeBlocking() throws IOException {
/* 155 */     this.state |= 0x2;
/*     */     try {
/* 157 */       if (this.buffer != null) {
/* 158 */         Channels.writeBlocking((WritableByteChannel)this.channel, this.buffer);
/*     */       }
/* 160 */       this.channel.shutdownWrites();
/* 161 */       Channels.flushBlocking((SuspendableWriteChannel)this.channel);
/* 162 */     } catch (IOException e) {
/* 163 */       this.channel.close();
/* 164 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReady() {
/* 170 */     if (this.listener == null)
/*     */     {
/* 172 */       throw UndertowServletMessages.MESSAGES.streamNotInAsyncMode();
/*     */     }
/* 174 */     return Bits.anyAreSet(this.state, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWriteListener(WriteListener writeListener) {
/* 179 */     if (writeListener == null) {
/* 180 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNull("writeListener");
/*     */     }
/* 182 */     if (this.listener != null) {
/* 183 */       throw UndertowServletMessages.MESSAGES.listenerAlreadySet();
/*     */     }
/* 185 */     this.listener = writeListener;
/* 186 */     this.channel.getWriteSetter().set(new WriteChannelListener());
/* 187 */     this.state |= 0x1;
/* 188 */     this.ioExecutor.execute(new Runnable()
/*     */         {
/*     */           public void run() {
/* 191 */             UpgradeServletOutputStream.this.channel.resumeWrites();
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   private class WriteChannelListener
/*     */     implements ChannelListener<StreamSinkChannel> {
/*     */     private WriteChannelListener() {}
/*     */     
/*     */     public void handleEvent(StreamSinkChannel channel) {
/* 201 */       if (Bits.anyAreSet(UpgradeServletOutputStream.this.state, 4)) {
/*     */         
/*     */         try {
/*     */           
/* 205 */           channel.flush();
/*     */           return;
/* 207 */         } catch (IOException e) {
/* 208 */           handleError(channel, e);
/*     */         } 
/*     */       }
/*     */       
/* 212 */       if (UpgradeServletOutputStream.this.buffer != null)
/*     */         while (true) {
/*     */           
/*     */           try {
/* 216 */             int res = channel.write(UpgradeServletOutputStream.this.buffer);
/* 217 */             if (res == 0) {
/*     */               return;
/*     */             }
/* 220 */           } catch (IOException e) {
/* 221 */             handleError(channel, e);
/*     */           } 
/* 223 */           if (!UpgradeServletOutputStream.this.buffer.hasRemaining()) {
/* 224 */             UpgradeServletOutputStream.this.buffer = null; break;
/*     */           } 
/* 226 */         }   if (Bits.anyAreSet(UpgradeServletOutputStream.this.state, 2)) {
/*     */         try {
/* 228 */           channel.shutdownWrites();
/* 229 */           UpgradeServletOutputStream.this.state = UpgradeServletOutputStream.this.state | 0x4;
/* 230 */           channel.flush();
/* 231 */         } catch (IOException e) {
/* 232 */           handleError(channel, e);
/*     */         } 
/*     */       } else {
/*     */         
/* 236 */         UpgradeServletOutputStream.this.state = UpgradeServletOutputStream.this.state | 0x1;
/* 237 */         channel.suspendWrites();
/*     */         try {
/* 239 */           UpgradeServletOutputStream.this.listener.onWritePossible();
/* 240 */         } catch (IOException e) {
/* 241 */           UpgradeServletOutputStream.this.listener.onError(e);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     private void handleError(StreamSinkChannel channel, IOException e) {
/*     */       try {
/* 248 */         UpgradeServletOutputStream.this.listener.onError(e);
/*     */       } finally {
/* 250 */         IoUtils.safeClose((Closeable)channel);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\UpgradeServletOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */