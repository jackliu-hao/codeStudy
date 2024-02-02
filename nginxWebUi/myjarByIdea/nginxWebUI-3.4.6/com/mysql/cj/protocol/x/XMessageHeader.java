package com.mysql.cj.protocol.x;

import com.mysql.cj.protocol.MessageHeader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class XMessageHeader implements MessageHeader {
   public static final int MESSAGE_SIZE_LENGTH = 4;
   public static final int MESSAGE_TYPE_LENGTH = 1;
   public static final int HEADER_LENGTH = 5;
   private ByteBuffer headerBuf;
   private int messageType = -1;
   private int messageSize = -1;

   public XMessageHeader() {
      this.headerBuf = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
   }

   public XMessageHeader(byte[] buf) {
      this.headerBuf = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
   }

   private void parseBuffer() {
      if (this.messageSize == -1) {
         this.headerBuf.position(0);
         this.messageSize = this.headerBuf.getInt() - 1;
         this.messageType = this.headerBuf.get();
      }

   }

   public ByteBuffer getBuffer() {
      return this.headerBuf;
   }

   public int getMessageSize() {
      this.parseBuffer();
      return this.messageSize;
   }

   public byte getMessageSequence() {
      return 0;
   }

   public int getMessageType() {
      this.parseBuffer();
      return this.messageType;
   }
}
