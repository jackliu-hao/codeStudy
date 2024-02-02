package org.xnio.channels;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import org.jboss.logging.Logger;
import org.xnio.Buffers;
import org.xnio.IoUtils;
import org.xnio.Pooled;
import org.xnio._private.Messages;

/** @deprecated */
@Deprecated
public class FramedMessageChannel extends TranslatingSuspendableChannel<ConnectedMessageChannel, ConnectedStreamChannel> implements ConnectedMessageChannel {
   private static final Logger log = Logger.getLogger("org.xnio.channels.framed");
   private final Pooled<ByteBuffer> receiveBuffer;
   private final Pooled<ByteBuffer> transmitBuffer;
   private final Object readLock = new Object();
   private final Object writeLock = new Object();

   public FramedMessageChannel(ConnectedStreamChannel channel, ByteBuffer receiveBuffer, ByteBuffer transmitBuffer) {
      super(channel);
      this.receiveBuffer = Buffers.pooledWrapper(receiveBuffer);
      this.transmitBuffer = Buffers.pooledWrapper(transmitBuffer);
      log.tracef((String)"Created new framed message channel around %s, receive buffer %s, transmit buffer %s", (Object)channel, receiveBuffer, transmitBuffer);
   }

   public FramedMessageChannel(ConnectedStreamChannel channel, Pooled<ByteBuffer> receiveBuffer, Pooled<ByteBuffer> transmitBuffer) {
      super(channel);
      this.receiveBuffer = receiveBuffer;
      this.transmitBuffer = transmitBuffer;
      log.tracef((String)"Created new framed message channel around %s, receive buffer %s, transmit buffer %s", (Object)channel, receiveBuffer, transmitBuffer);
   }

   public int receive(ByteBuffer buffer) throws IOException {
      synchronized(this.readLock) {
         if (this.isReadShutDown()) {
            return -1;
         } else {
            ByteBuffer receiveBuffer = (ByteBuffer)this.receiveBuffer.getResource();
            ConnectedStreamChannel channel = (ConnectedStreamChannel)this.channel;

            int res;
            do {
               res = channel.read(receiveBuffer);
            } while(res > 0);

            if (receiveBuffer.position() < 4) {
               if (res == -1) {
                  receiveBuffer.clear();
               }

               log.tracef("Did not read a length");
               this.clearReadReady();
               return res;
            } else {
               receiveBuffer.flip();

               int var7;
               try {
                  int length = receiveBuffer.getInt();
                  if (length < 0 || length > receiveBuffer.capacity() - 4) {
                     Buffers.unget(receiveBuffer, 4);
                     throw Messages.msg.recvInvalidMsgLength(length);
                  }

                  if (receiveBuffer.remaining() < length) {
                     if (res == -1) {
                        receiveBuffer.clear();
                     } else {
                        Buffers.unget(receiveBuffer, 4);
                        receiveBuffer.compact();
                     }

                     log.tracef("Did not read enough bytes for a full message");
                     this.clearReadReady();
                     var7 = res;
                     return var7;
                  }

                  if (buffer.hasRemaining()) {
                     log.tracef((String)"Copying message from %s into %s", (Object)receiveBuffer, (Object)buffer);
                     Buffers.copy(buffer, Buffers.slice(receiveBuffer, length));
                  } else {
                     log.tracef((String)"Not copying message from %s into full buffer %s", (Object)receiveBuffer, (Object)buffer);
                     Buffers.skip(receiveBuffer, length);
                  }

                  receiveBuffer.compact();
                  var7 = length;
               } finally {
                  if (res != -1 && receiveBuffer.position() >= 4 && receiveBuffer.position() >= 4 + receiveBuffer.getInt(0)) {
                     this.setReadReady();
                  }

               }

               return var7;
            }
         }
      }
   }

   public long receive(ByteBuffer[] buffers) throws IOException {
      return this.receive(buffers, 0, buffers.length);
   }

   public long receive(ByteBuffer[] buffers, int offs, int len) throws IOException {
      synchronized(this.readLock) {
         if (this.isReadShutDown()) {
            return -1L;
         } else {
            ByteBuffer receiveBuffer = (ByteBuffer)this.receiveBuffer.getResource();
            ConnectedStreamChannel channel = (ConnectedStreamChannel)this.channel;

            int res;
            do {
               res = channel.read(receiveBuffer);
            } while(res > 0);

            if (receiveBuffer.position() < 4) {
               if (res == -1) {
                  receiveBuffer.clear();
               }

               log.tracef("Did not read a length");
               this.clearReadReady();
               return (long)res;
            } else {
               receiveBuffer.flip();

               long var9;
               try {
                  int length = receiveBuffer.getInt();
                  if (length < 0 || length > receiveBuffer.capacity() - 4) {
                     Buffers.unget(receiveBuffer, 4);
                     throw Messages.msg.recvInvalidMsgLength(length);
                  }

                  if (receiveBuffer.remaining() >= length) {
                     if (Buffers.hasRemaining(buffers)) {
                        log.tracef("Copying message from %s into multiple buffers", (Object)receiveBuffer);
                        Buffers.copy(buffers, offs, len, Buffers.slice(receiveBuffer, length));
                     } else {
                        log.tracef("Not copying message from %s into multiple full buffers", (Object)receiveBuffer);
                        Buffers.skip(receiveBuffer, length);
                     }

                     receiveBuffer.compact();
                     var9 = (long)length;
                     return var9;
                  }

                  if (res == -1) {
                     receiveBuffer.clear();
                  } else {
                     Buffers.unget(receiveBuffer, 4);
                     receiveBuffer.compact();
                  }

                  log.tracef("Did not read enough bytes for a full message");
                  this.clearReadReady();
                  var9 = (long)res;
               } finally {
                  if (res != -1 && receiveBuffer.position() >= 4 && receiveBuffer.position() >= 4 + receiveBuffer.getInt(0)) {
                     this.setReadReady();
                  }

               }

               return var9;
            }
         }
      }
   }

   protected void shutdownReadsAction(boolean writeComplete) throws IOException {
      synchronized(this.readLock) {
         log.tracef("Shutting down reads on %s", (Object)this);

         try {
            ((ByteBuffer)this.receiveBuffer.getResource()).clear();
         } catch (Throwable var6) {
         }

         try {
            this.receiveBuffer.free();
         } catch (Throwable var5) {
         }
      }

      ((ConnectedStreamChannel)this.channel).shutdownReads();
   }

   public boolean send(ByteBuffer buffer) throws IOException {
      synchronized(this.writeLock) {
         if (this.isWriteShutDown()) {
            throw Messages.msg.writeShutDown();
         } else if (!buffer.hasRemaining()) {
            return true;
         } else {
            ByteBuffer transmitBuffer = (ByteBuffer)this.transmitBuffer.getResource();
            int remaining = buffer.remaining();
            if (remaining > transmitBuffer.capacity() - 4) {
               throw Messages.msg.txMsgTooLarge();
            } else {
               log.tracef((String)"Accepting %s into %s", (Object)buffer, (Object)transmitBuffer);
               if (transmitBuffer.remaining() < 4 + remaining && !this.doFlushBuffer()) {
                  log.tracef((String)"Insufficient room to accept %s into %s", (Object)buffer, (Object)transmitBuffer);
                  return false;
               } else {
                  transmitBuffer.putInt(remaining);
                  transmitBuffer.put(buffer);
                  log.tracef("Accepted a message into %s", (Object)transmitBuffer);
                  return true;
               }
            }
         }
      }
   }

   public boolean send(ByteBuffer[] buffers) throws IOException {
      return this.send(buffers, 0, buffers.length);
   }

   public boolean send(ByteBuffer[] buffers, int offs, int len) throws IOException {
      synchronized(this.writeLock) {
         if (this.isWriteShutDown()) {
            throw Messages.msg.writeShutDown();
         } else if (!Buffers.hasRemaining(buffers, offs, len)) {
            return true;
         } else {
            ByteBuffer transmitBuffer = (ByteBuffer)this.transmitBuffer.getResource();
            long remaining = Buffers.remaining(buffers, offs, len);
            if (remaining > (long)transmitBuffer.capacity() - 4L) {
               throw Messages.msg.txMsgTooLarge();
            } else {
               log.tracef("Accepting multiple buffers into %s", (Object)transmitBuffer);
               if ((long)transmitBuffer.remaining() < 4L + remaining && !this.doFlushBuffer()) {
                  log.tracef("Insufficient room to accept multiple buffers into %s", (Object)transmitBuffer);
                  return false;
               } else {
                  transmitBuffer.putInt((int)remaining);
                  Buffers.copy(transmitBuffer, buffers, offs, len);
                  log.tracef("Accepted a message into %s", (Object)transmitBuffer);
                  return true;
               }
            }
         }
      }
   }

   public boolean sendFinal(ByteBuffer buffer) throws IOException {
      if (this.send(buffer)) {
         this.shutdownWrites();
         return true;
      } else {
         return false;
      }
   }

   public boolean sendFinal(ByteBuffer[] buffers) throws IOException {
      if (this.send(buffers)) {
         this.shutdownWrites();
         return true;
      } else {
         return false;
      }
   }

   public boolean sendFinal(ByteBuffer[] buffers, int offs, int len) throws IOException {
      if (this.send(buffers, offs, len)) {
         this.shutdownWrites();
         return true;
      } else {
         return false;
      }
   }

   protected boolean flushAction(boolean shutDown) throws IOException {
      synchronized(this.writeLock) {
         return this.doFlushBuffer() && ((ConnectedStreamChannel)this.channel).flush();
      }
   }

   protected void shutdownWritesComplete(boolean readShutDown) throws IOException {
      synchronized(this.writeLock) {
         log.tracef("Finished shutting down writes on %s", (Object)this);

         try {
            this.transmitBuffer.free();
         } catch (Throwable var5) {
         }
      }

      ((ConnectedStreamChannel)this.channel).shutdownWrites();
   }

   private boolean doFlushBuffer() throws IOException {
      assert Thread.holdsLock(this.writeLock);

      ByteBuffer buffer = (ByteBuffer)this.transmitBuffer.getResource();
      buffer.flip();

      try {
         while(true) {
            if (buffer.hasRemaining()) {
               int res = ((ConnectedStreamChannel)this.channel).write(buffer);
               if (res != 0) {
                  continue;
               }

               log.tracef("Did not fully flush %s", (Object)this);
               boolean var3 = false;
               return var3;
            }

            log.tracef("Fully flushed %s", (Object)this);
            boolean var2 = true;
            return var2;
         }
      } finally {
         buffer.compact();
      }
   }

   private boolean doFlush() throws IOException {
      return this.doFlushBuffer() && ((ConnectedStreamChannel)this.channel).flush();
   }

   protected void closeAction(boolean readShutDown, boolean writeShutDown) throws IOException {
      boolean error = false;
      if (!writeShutDown) {
         synchronized(this.writeLock) {
            try {
               if (!this.doFlush()) {
                  error = true;
               }
            } catch (Throwable var18) {
               error = true;
            }

            try {
               this.transmitBuffer.free();
            } catch (Throwable var17) {
            }
         }
      }

      if (!readShutDown) {
         synchronized(this.readLock) {
            try {
               this.receiveBuffer.free();
            } catch (Throwable var15) {
            }
         }
      }

      try {
         if (error) {
            throw Messages.msg.unflushedData();
         }

         ((ConnectedStreamChannel)this.channel).close();
      } finally {
         IoUtils.safeClose((Closeable)this.channel);
      }

   }

   public SocketAddress getPeerAddress() {
      return ((ConnectedStreamChannel)this.channel).getPeerAddress();
   }

   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
      return ((ConnectedStreamChannel)this.channel).getPeerAddress(type);
   }

   public SocketAddress getLocalAddress() {
      return ((ConnectedStreamChannel)this.channel).getLocalAddress();
   }

   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
      return ((ConnectedStreamChannel)this.channel).getLocalAddress(type);
   }

   public ConnectedStreamChannel getChannel() {
      return (ConnectedStreamChannel)this.channel;
   }
}
