package com.mysql.cj.protocol.x;

import com.google.protobuf.ByteString;
import com.mysql.cj.x.protobuf.Mysqlx;
import com.mysql.cj.x.protobuf.MysqlxConnection;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class CompressionSplittedOutputStream extends FilterOutputStream {
   private CompressorStreamsFactory compressorIoStreamsFactory;
   private byte[] frameHeader = new byte[5];
   private int frameHeaderBuffered = 0;
   private int frameHeaderDumped = 0;
   private int framePayloadLength = 0;
   private int framePayloadDumped = 0;
   private XMessageHeader xMessageHeader = null;
   private boolean compressionEnabled = false;
   private ByteArrayOutputStream bufferOut = null;
   private OutputStream compressorOut = null;
   private byte[] singleByte = new byte[1];
   private boolean closed = false;

   public CompressionSplittedOutputStream(OutputStream out, CompressorStreamsFactory ioStreamsFactory) {
      super(out);
      this.compressorIoStreamsFactory = ioStreamsFactory;
   }

   public void close() throws IOException {
      if (!this.closed) {
         super.close();
         this.out = null;
         this.bufferOut = null;
         if (this.compressorOut != null) {
            this.compressorOut.close();
         }

         this.compressorOut = null;
         this.closed = true;
      }

   }

   public void write(int b) throws IOException {
      this.ensureOpen();
      this.singleByte[0] = (byte)b;
      this.write(this.singleByte, 0, 1);
   }

   public void write(byte[] b) throws IOException {
      this.ensureOpen();
      this.write(b, 0, b.length);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      this.ensureOpen();
      if ((off | len | b.length - (len + off) | off + len) < 0) {
         throw new IndexOutOfBoundsException();
      } else {
         int bytesProcessed = this.peekFrameHeader(b, off, len);
         if (this.isFrameHeaderBuffered() && !this.isFrameHeaderWriteComplete()) {
            this.xMessageHeader = new XMessageHeader(this.frameHeader);
            this.framePayloadLength = this.xMessageHeader.getMessageSize();
            this.framePayloadDumped = 0;
            this.compressionEnabled = this.framePayloadLength >= 250;
            if (this.compressionEnabled) {
               this.bufferOut = new ByteArrayOutputStream();
               this.compressorOut = this.compressorIoStreamsFactory.getOutputStreamInstance(this.bufferOut);
               this.compressorOut.write(this.frameHeader, 0, 5);
            } else {
               this.out.write(this.frameHeader, 0, 5);
            }

            this.frameHeaderDumped = 5;
         }

         int bytesToDump = len - bytesProcessed;
         if (bytesToDump > 0) {
            if (this.compressionEnabled) {
               this.compressorOut.write(b, off + bytesProcessed, bytesToDump);
            } else {
               this.out.write(b, off + bytesProcessed, bytesToDump);
            }
         }

         this.framePayloadDumped += bytesToDump;
         this.finalizeWrite();
      }
   }

   private int peekFrameHeader(byte[] b, int off, int len) {
      if (this.isPayloadWriteReady()) {
         return 0;
      } else {
         int toCollect = 0;
         if (this.frameHeaderBuffered < 5) {
            toCollect = Math.min(len, 5 - this.frameHeaderBuffered);
            System.arraycopy(b, off, this.frameHeader, this.frameHeaderBuffered, toCollect);
            this.frameHeaderBuffered += toCollect;
         }

         return toCollect;
      }
   }

   private boolean isFrameHeaderBuffered() {
      return this.frameHeaderBuffered == 5;
   }

   private boolean isFrameHeaderWriteComplete() {
      return this.frameHeaderDumped == 5;
   }

   private boolean isPayloadWriteReady() {
      return this.isFrameHeaderWriteComplete() && this.framePayloadDumped < this.framePayloadLength;
   }

   private boolean isWriteComplete() {
      return this.isFrameHeaderWriteComplete() && this.framePayloadDumped >= this.framePayloadLength;
   }

   private void finalizeWrite() throws IOException {
      if (this.isWriteComplete()) {
         if (this.compressionEnabled) {
            this.compressorOut.close();
            this.compressorOut = null;
            byte[] compressedData = this.bufferOut.toByteArray();
            MysqlxConnection.Compression compressedMessage = MysqlxConnection.Compression.newBuilder().setUncompressedSize((long)(5 + this.framePayloadLength)).setClientMessages(Mysqlx.ClientMessages.Type.forNumber(this.xMessageHeader.getMessageType())).setPayload(ByteString.copyFrom(compressedData)).build();
            ByteBuffer messageHeader = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
            messageHeader.putInt(compressedMessage.getSerializedSize() + 1);
            messageHeader.put((byte)46);
            this.out.write(messageHeader.array());
            compressedMessage.writeTo(this.out);
            this.out.flush();
            this.compressionEnabled = false;
         }

         Arrays.fill(this.frameHeader, (byte)0);
         this.frameHeaderBuffered = 0;
         this.frameHeaderDumped = 0;
         this.framePayloadLength = 0;
         this.framePayloadDumped = 0;
         this.xMessageHeader = null;
      }

   }

   private void ensureOpen() throws IOException {
      if (this.closed) {
         throw new IOException("Stream closed");
      }
   }
}
