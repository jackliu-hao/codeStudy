package com.mysql.cj.protocol.a;

import com.mysql.cj.protocol.MessageSender;
import com.mysql.cj.protocol.PacketSentTimeHolder;
import java.io.IOException;

public class TimeTrackingPacketSender implements MessageSender<NativePacketPayload>, PacketSentTimeHolder {
   private MessageSender<NativePacketPayload> packetSender;
   private long lastPacketSentTime = 0L;
   private long previousPacketSentTime = 0L;

   public TimeTrackingPacketSender(MessageSender<NativePacketPayload> packetSender) {
      this.packetSender = packetSender;
   }

   public void send(byte[] packet, int packetLen, byte packetSequence) throws IOException {
      this.packetSender.send(packet, packetLen, packetSequence);
      this.previousPacketSentTime = this.lastPacketSentTime;
      this.lastPacketSentTime = System.currentTimeMillis();
   }

   public long getLastPacketSentTime() {
      return this.lastPacketSentTime;
   }

   public long getPreviousPacketSentTime() {
      return this.previousPacketSentTime;
   }

   public MessageSender<NativePacketPayload> undecorateAll() {
      return this.packetSender.undecorateAll();
   }

   public MessageSender<NativePacketPayload> undecorate() {
      return this.packetSender;
   }
}
