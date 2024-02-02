package com.mysql.cj.protocol.a;

import com.mysql.cj.protocol.MessageReader;
import com.mysql.cj.protocol.PacketReceivedTimeHolder;
import java.io.IOException;
import java.util.Optional;

public class TimeTrackingPacketReader implements MessageReader<NativePacketHeader, NativePacketPayload>, PacketReceivedTimeHolder {
   private MessageReader<NativePacketHeader, NativePacketPayload> packetReader;
   private long lastPacketReceivedTimeMs = 0L;

   public TimeTrackingPacketReader(MessageReader<NativePacketHeader, NativePacketPayload> messageReader) {
      this.packetReader = messageReader;
   }

   public NativePacketHeader readHeader() throws IOException {
      return (NativePacketHeader)this.packetReader.readHeader();
   }

   public NativePacketHeader probeHeader() throws IOException {
      return (NativePacketHeader)this.packetReader.probeHeader();
   }

   public NativePacketPayload readMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
      NativePacketPayload buf = (NativePacketPayload)this.packetReader.readMessage(reuse, header);
      this.lastPacketReceivedTimeMs = System.currentTimeMillis();
      return buf;
   }

   public NativePacketPayload probeMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
      NativePacketPayload buf = (NativePacketPayload)this.packetReader.probeMessage(reuse, header);
      this.lastPacketReceivedTimeMs = System.currentTimeMillis();
      return buf;
   }

   public long getLastPacketReceivedTime() {
      return this.lastPacketReceivedTimeMs;
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
