package com.mysql.cj.protocol.x;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.x.protobuf.Mysqlx;
import com.mysql.cj.x.protobuf.MysqlxConnection;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CompressionSplittedInputStream extends FilterInputStream {
   private CompressorStreamsFactory compressorIoStreamsFactory;
   private byte[] frameHeader = new byte[5];
   private int frameHeaderConsumed = 0;
   private int framePayloadLength = 0;
   private int framePayloadConsumed = 0;
   private XMessageHeader xMessageHeader;
   private InputStream compressorIn = null;
   private byte[] singleByte = new byte[1];
   private boolean closed = false;

   public CompressionSplittedInputStream(InputStream in, CompressorStreamsFactory streamsFactory) {
      super(in);
      this.compressorIoStreamsFactory = streamsFactory;
   }

   public int available() throws IOException {
      this.ensureOpen();
      return this.compressorIn != null ? this.compressorIn.available() : (this.frameHeaderConsumed > 0 ? 5 - this.frameHeaderConsumed : 0) + this.in.available();
   }

   public void close() throws IOException {
      if (!this.closed) {
         super.close();
         this.in = null;
         if (this.compressorIn != null) {
            this.compressorIn.close();
         }

         this.compressorIn = null;
         this.closed = true;
      }

   }

   public int read() throws IOException {
      this.ensureOpen();
      int read = this.read(this.singleByte, 0, 1);
      return read >= 0 ? this.singleByte[0] & 255 : read;
   }

   public int read(byte[] b) throws IOException {
      this.ensureOpen();
      return this.read(b, 0, b.length);
   }

   public int read(byte[] b, int off, int len) throws IOException {
      this.ensureOpen();
      if (len <= 0) {
         return 0;
      } else {
         this.peekNextFrame();

         int headerBytesRead;
         try {
            if (this.isCompressedDataAvailable()) {
               headerBytesRead = this.readFully(this.compressorIn, b, off, len);
               if (this.isCompressedDataReadComplete()) {
                  this.compressorIn.close();
                  this.compressorIn = null;
               }

               return headerBytesRead;
            }
         } catch (IOException var6) {
            throw var6;
         }

         headerBytesRead = 0;
         int payloadBytesRead;
         if (!this.isFrameHeaderFullyConsumed()) {
            payloadBytesRead = Math.min(len, 5 - this.frameHeaderConsumed);
            System.arraycopy(this.frameHeader, this.frameHeaderConsumed, b, off, payloadBytesRead);
            off += payloadBytesRead;
            len -= payloadBytesRead;
            this.frameHeaderConsumed += payloadBytesRead;
            headerBytesRead = payloadBytesRead;
         }

         payloadBytesRead = this.readFully(b, off, len);
         this.framePayloadConsumed += payloadBytesRead;
         return headerBytesRead + payloadBytesRead;
      }
   }

   private void peekNextFrame() throws IOException {
      if (!this.isDataAvailable()) {
         this.readFully(this.frameHeader, 0, 5);
         this.xMessageHeader = new XMessageHeader(this.frameHeader);
         this.framePayloadLength = this.xMessageHeader.getMessageSize();
         this.frameHeaderConsumed = 0;
         this.framePayloadConsumed = 0;
         if (this.isCompressedFrame()) {
            MysqlxConnection.Compression compressedMessage = this.parseCompressedMessage();
            this.compressorIn = new ConfinedInputStream(this.compressorIoStreamsFactory.getInputStreamInstance(new ByteArrayInputStream(compressedMessage.getPayload().toByteArray())), (int)compressedMessage.getUncompressedSize());
            this.frameHeaderConsumed = 5;
            this.framePayloadConsumed = this.framePayloadLength;
         }

      }
   }

   private boolean isCompressedFrame() {
      return Mysqlx.ServerMessages.Type.forNumber(this.xMessageHeader.getMessageType()) == Mysqlx.ServerMessages.Type.COMPRESSION;
   }

   private MysqlxConnection.Compression parseCompressedMessage() {
      Parser<MysqlxConnection.Compression> parser = (Parser)MessageConstants.MESSAGE_CLASS_TO_PARSER.get(MessageConstants.MESSAGE_TYPE_TO_CLASS.get(19));
      byte[] packet = new byte[this.xMessageHeader.getMessageSize()];

      try {
         this.readFully(packet);
      } catch (IOException var5) {
         throw (CJCommunicationsException)ExceptionFactory.createException((Class)CJCommunicationsException.class, (String)Messages.getString("Protocol.Compression.Streams.0"), (Throwable)var5);
      }

      try {
         return (MysqlxConnection.Compression)parser.parseFrom(packet);
      } catch (InvalidProtocolBufferException var4) {
         throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)Messages.getString("Protocol.Compression.Streams.1"), (Throwable)var4);
      }
   }

   private boolean isDataAvailable() throws IOException {
      return this.isCompressedDataAvailable() || this.frameHeaderConsumed > 0 && this.frameHeaderConsumed < 5 || this.isFrameHeaderFullyConsumed() && this.framePayloadConsumed < this.framePayloadLength;
   }

   private boolean isCompressedDataAvailable() throws IOException {
      return this.compressorIn != null && this.compressorIn.available() > 0;
   }

   private boolean isCompressedDataReadComplete() throws IOException {
      return this.compressorIn != null && this.compressorIn.available() == 0;
   }

   boolean isFrameHeaderFullyConsumed() {
      return this.frameHeaderConsumed == 5;
   }

   public int readFully(byte[] b) throws IOException {
      return this.readFully(b, 0, b.length);
   }

   private final int readFully(byte[] b, int off, int len) throws IOException {
      return this.readFully(this.in, b, off, len);
   }

   private final int readFully(InputStream inStream, byte[] b, int off, int len) throws IOException {
      if (len < 0) {
         throw new IndexOutOfBoundsException();
      } else {
         int total;
         int count;
         for(total = 0; total < len; total += count) {
            count = inStream.read(b, off + total, len - total);
            if (count < 0) {
               throw new EOFException();
            }
         }

         return total;
      }
   }

   private void ensureOpen() throws IOException {
      if (this.closed) {
         throw new IOException("Stream closed");
      }
   }
}
