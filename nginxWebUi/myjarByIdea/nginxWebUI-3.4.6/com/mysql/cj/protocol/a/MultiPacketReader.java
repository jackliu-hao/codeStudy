package com.mysql.cj.protocol.a;

import com.mysql.cj.Messages;
import com.mysql.cj.protocol.MessageReader;
import java.io.IOException;
import java.util.Optional;

public class MultiPacketReader implements MessageReader<NativePacketHeader, NativePacketPayload> {
   private MessageReader<NativePacketHeader, NativePacketPayload> packetReader;

   public MultiPacketReader(MessageReader<NativePacketHeader, NativePacketPayload> packetReader) {
      this.packetReader = packetReader;
   }

   public NativePacketHeader readHeader() throws IOException {
      return (NativePacketHeader)this.packetReader.readHeader();
   }

   public NativePacketHeader probeHeader() throws IOException {
      return (NativePacketHeader)this.packetReader.probeHeader();
   }

   public NativePacketPayload readMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
      int packetLength = header.getMessageSize();
      NativePacketPayload buf = (NativePacketPayload)this.packetReader.readMessage(reuse, header);
      if (packetLength == 16777215) {
         buf.setPosition(16777215);
         NativePacketPayload multiPacket = null;
         int multiPacketLength = true;
         byte multiPacketSeq = this.getMessageSequence();

         int multiPacketLength;
         do {
            NativePacketHeader hdr = this.readHeader();
            multiPacketLength = hdr.getMessageSize();
            if (multiPacket == null) {
               multiPacket = new NativePacketPayload(multiPacketLength);
            }

            ++multiPacketSeq;
            if (multiPacketSeq != hdr.getMessageSequence()) {
               throw new IOException(Messages.getString("PacketReader.10"));
            }

            this.packetReader.readMessage(Optional.of(multiPacket), hdr);
            buf.writeBytes((NativeConstants.StringLengthDataType)NativeConstants.StringLengthDataType.STRING_FIXED, multiPacket.getByteBuffer(), 0, multiPacketLength);
         } while(multiPacketLength == 16777215);

         buf.setPosition(0);
      }

      return buf;
   }

   public NativePacketPayload probeMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
      int packetLength = header.getMessageSize();
      NativePacketPayload buf = (NativePacketPayload)this.packetReader.probeMessage(reuse, header);
      if (packetLength == 16777215) {
         buf.setPosition(16777215);
         NativePacketPayload multiPacket = null;
         int multiPacketLength = true;
         byte multiPacketSeq = this.getMessageSequence();

         int multiPacketLength;
         do {
            NativePacketHeader hdr = this.readHeader();
            multiPacketLength = hdr.getMessageSize();
            if (multiPacket == null) {
               multiPacket = new NativePacketPayload(multiPacketLength);
            }

            ++multiPacketSeq;
            if (multiPacketSeq != hdr.getMessageSequence()) {
               throw new IOException(Messages.getString("PacketReader.10"));
            }

            this.packetReader.probeMessage(Optional.of(multiPacket), hdr);
            buf.writeBytes((NativeConstants.StringLengthDataType)NativeConstants.StringLengthDataType.STRING_FIXED, multiPacket.getByteBuffer(), 0, multiPacketLength);
         } while(multiPacketLength == 16777215);

         buf.setPosition(0);
      }

      return buf;
   }

   public byte getMessageSequence() {
      return this.packetReader.getMessageSequence();
   }

   public void resetMessageSequence() {
      this.packetReader.resetMessageSequence();
   }

   public MessageReader<NativePacketHeader, NativePacketPayload> undecorateAll() {
      return this.packetReader.undecorateAll();
   }

   public MessageReader<NativePacketHeader, NativePacketPayload> undecorate() {
      return this.packetReader;
   }
}
