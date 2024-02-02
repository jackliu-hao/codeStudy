package com.mysql.cj.protocol.x;

import com.google.protobuf.MessageLite;
import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.CJPacketTooBigException;
import com.mysql.cj.protocol.MessageSender;
import com.mysql.cj.protocol.PacketSentTimeHolder;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;

public class SyncMessageSender implements MessageSender<XMessage>, PacketSentTimeHolder {
   static final int HEADER_LEN = 5;
   private OutputStream outputStream;
   private long lastPacketSentTime = 0L;
   private long previousPacketSentTime = 0L;
   private int maxAllowedPacket = -1;
   Object waitingAsyncOperationMonitor = new Object();

   public SyncMessageSender(OutputStream os) {
      this.outputStream = os;
   }

   public void send(XMessage message) {
      synchronized(this.waitingAsyncOperationMonitor) {
         MessageLite msg = message.getMessage();

         try {
            int type = MessageConstants.getTypeForMessageClass(msg.getClass());
            int size = 1 + msg.getSerializedSize();
            if (this.maxAllowedPacket > 0 && size > this.maxAllowedPacket) {
               throw new CJPacketTooBigException(Messages.getString("PacketTooBigException.1", new Object[]{size, this.maxAllowedPacket}));
            }

            byte[] sizeHeader = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(size).array();
            this.outputStream.write(sizeHeader);
            this.outputStream.write(type);
            msg.writeTo(this.outputStream);
            this.outputStream.flush();
            this.previousPacketSentTime = this.lastPacketSentTime;
            this.lastPacketSentTime = System.currentTimeMillis();
         } catch (IOException var8) {
            throw new CJCommunicationsException("Unable to write message", var8);
         }

      }
   }

   public CompletableFuture<?> send(XMessage message, CompletableFuture<?> future, Runnable callback) {
      synchronized(this.waitingAsyncOperationMonitor) {
         CompletionHandler<Long, Void> resultHandler = new ErrorToFutureCompletionHandler(future, callback);
         MessageLite msg = message.getMessage();

         try {
            this.send(message);
            long result = (long)(5 + msg.getSerializedSize());
            resultHandler.completed(result, (Object)null);
         } catch (Throwable var10) {
            resultHandler.failed(var10, (Object)null);
         }

         return future;
      }
   }

   public long getLastPacketSentTime() {
      return this.lastPacketSentTime;
   }

   public long getPreviousPacketSentTime() {
      return this.previousPacketSentTime;
   }

   public void setMaxAllowedPacket(int maxAllowedPacket) {
      this.maxAllowedPacket = maxAllowedPacket;
   }
}
