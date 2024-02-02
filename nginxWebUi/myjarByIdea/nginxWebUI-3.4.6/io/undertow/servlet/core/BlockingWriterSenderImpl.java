package io.undertow.servlet.core;

import io.undertow.UndertowMessages;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import javax.servlet.DispatcherType;
import org.xnio.IoUtils;

public class BlockingWriterSenderImpl implements Sender {
   public static final int BUFFER_SIZE = 128;
   private final CharsetDecoder charsetDecoder;
   private final HttpServerExchange exchange;
   private final PrintWriter writer;
   private FileChannel pendingFile;
   private volatile Thread inCall;
   private volatile Thread sendThread;
   private String next;
   private IoCallback queuedCallback;

   public BlockingWriterSenderImpl(HttpServerExchange exchange, PrintWriter writer, String charset) {
      this.exchange = exchange;
      this.writer = writer;
      this.charsetDecoder = Charset.forName(charset).newDecoder();
   }

   public void send(ByteBuffer buffer, IoCallback callback) {
      this.sendThread = Thread.currentThread();
      if (this.inCall == Thread.currentThread()) {
         this.queue(new ByteBuffer[]{buffer}, callback);
      } else {
         if (this.writeBuffer(buffer, callback)) {
            this.invokeOnComplete(callback);
         }

      }
   }

   public void send(ByteBuffer[] buffer, IoCallback callback) {
      this.sendThread = Thread.currentThread();
      if (this.inCall == Thread.currentThread()) {
         this.queue(buffer, callback);
      } else {
         ByteBuffer[] var3 = buffer;
         int var4 = buffer.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ByteBuffer b = var3[var5];
            if (!this.writeBuffer(b, callback)) {
               return;
            }
         }

         this.invokeOnComplete(callback);
      }
   }

   public void send(String data, IoCallback callback) {
      this.sendThread = Thread.currentThread();
      if (this.inCall == Thread.currentThread()) {
         this.queue(data, callback);
      } else {
         this.writer.write(data);
         this.invokeOnComplete(callback);
      }
   }

   public void send(ByteBuffer buffer) {
      this.send(buffer, IoCallback.END_EXCHANGE);
   }

   public void send(ByteBuffer[] buffer) {
      this.send(buffer, IoCallback.END_EXCHANGE);
   }

   public void send(String data, Charset charset, IoCallback callback) {
      this.sendThread = Thread.currentThread();
      if (this.inCall == Thread.currentThread()) {
         this.queue(new ByteBuffer[]{ByteBuffer.wrap(data.getBytes(charset))}, callback);
      } else {
         this.writer.write(data);
         this.invokeOnComplete(callback);
      }
   }

   public void send(String data) {
      this.send(data, IoCallback.END_EXCHANGE);
   }

   public void send(String data, Charset charset) {
      this.send(data, charset, IoCallback.END_EXCHANGE);
   }

   public void transferFrom(FileChannel source, IoCallback callback) {
      this.sendThread = Thread.currentThread();
      if (this.inCall == Thread.currentThread()) {
         this.queue(source, callback);
      } else {
         this.performTransfer(source, callback);
      }
   }

   private void performTransfer(FileChannel source, IoCallback callback) {
      ByteBuffer buffer = ByteBuffer.allocate(128);

      try {
         long pos = source.position();
         long size = source.size();

         while(size - pos > 0L) {
            int ret = source.read(buffer);
            if (ret <= 0) {
               break;
            }

            pos += (long)ret;
            buffer.flip();
            if (!this.writeBuffer(buffer, callback)) {
               return;
            }

            buffer.clear();
         }

         if (pos != size) {
            throw new EOFException("Unexpected EOF reading file");
         }
      } catch (IOException var9) {
         callback.onException(this.exchange, this, var9);
      }

      this.invokeOnComplete(callback);
   }

   public void close(IoCallback callback) {
      this.writer.close();
      if (this.writer.checkError()) {
         callback.onException(this.exchange, this, new IOException());
      } else {
         this.invokeOnComplete(callback);
      }

   }

   public void close() {
      if (((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getDispatcherType() != DispatcherType.INCLUDE) {
         IoUtils.safeClose((Closeable)this.writer);
      }

      this.writer.checkError();
   }

   private boolean writeBuffer(ByteBuffer buffer, IoCallback callback) {
      StringBuilder builder = new StringBuilder();

      try {
         builder.append(this.charsetDecoder.decode(buffer));
      } catch (CharacterCodingException var5) {
         callback.onException(this.exchange, this, var5);
         return false;
      }

      String data = builder.toString();
      this.writer.write(data);
      return true;
   }

   private void invokeOnComplete(IoCallback callback) {
      this.sendThread = null;
      this.inCall = Thread.currentThread();

      try {
         callback.onComplete(this.exchange, this);
      } finally {
         this.inCall = null;
      }

      if (Thread.currentThread() == this.sendThread) {
         while(this.next != null) {
            String next = this.next;
            IoCallback queuedCallback = this.queuedCallback;
            this.next = null;
            this.queuedCallback = null;
            this.writer.write(next);
            if (this.writer.checkError()) {
               queuedCallback.onException(this.exchange, this, new IOException());
            } else {
               this.sendThread = null;
               this.inCall = Thread.currentThread();

               try {
                  queuedCallback.onComplete(this.exchange, this);
               } finally {
                  this.inCall = null;
               }

               if (Thread.currentThread() != this.sendThread) {
                  return;
               }
            }
         }

      }
   }

   private void queue(ByteBuffer[] byteBuffers, IoCallback ioCallback) {
      if (this.next == null && this.pendingFile == null) {
         StringBuilder builder = new StringBuilder();
         ByteBuffer[] var4 = byteBuffers;
         int var5 = byteBuffers.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            ByteBuffer buffer = var4[var6];

            try {
               builder.append(this.charsetDecoder.decode(buffer));
            } catch (CharacterCodingException var9) {
               ioCallback.onException(this.exchange, this, var9);
               return;
            }
         }

         this.next = builder.toString();
         this.queuedCallback = ioCallback;
      } else {
         throw UndertowMessages.MESSAGES.dataAlreadyQueued();
      }
   }

   private void queue(String data, IoCallback callback) {
      if (this.next == null && this.pendingFile == null) {
         this.next = data;
         this.queuedCallback = callback;
      } else {
         throw UndertowMessages.MESSAGES.dataAlreadyQueued();
      }
   }

   private void queue(FileChannel data, IoCallback callback) {
      if (this.next == null && this.pendingFile == null) {
         this.pendingFile = data;
         this.queuedCallback = callback;
      } else {
         throw UndertowMessages.MESSAGES.dataAlreadyQueued();
      }
   }
}
