package com.mysql.cj.protocol.a;

import com.mysql.cj.Messages;
import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.MessageReader;
import com.mysql.cj.util.StringUtils;
import java.io.IOException;
import java.util.Optional;

public class TracingPacketReader implements MessageReader<NativePacketHeader, NativePacketPayload> {
   private static final int MAX_PACKET_DUMP_LENGTH = 1024;
   private MessageReader<NativePacketHeader, NativePacketPayload> packetReader;
   private Log log;

   public TracingPacketReader(MessageReader<NativePacketHeader, NativePacketPayload> packetReader, Log log) {
      this.packetReader = packetReader;
      this.log = log;
   }

   public NativePacketHeader readHeader() throws IOException {
      return this.traceHeader((NativePacketHeader)this.packetReader.readHeader());
   }

   public NativePacketHeader probeHeader() throws IOException {
      return this.traceHeader((NativePacketHeader)this.packetReader.probeHeader());
   }

   private NativePacketHeader traceHeader(NativePacketHeader hdr) throws IOException {
      StringBuilder traceMessageBuf = new StringBuilder();
      traceMessageBuf.append(Messages.getString("PacketReader.3"));
      traceMessageBuf.append(hdr.getMessageSize());
      traceMessageBuf.append(Messages.getString("PacketReader.4"));
      traceMessageBuf.append(StringUtils.dumpAsHex(hdr.getBuffer().array(), 4));
      this.log.logTrace(traceMessageBuf.toString());
      return hdr;
   }

   public NativePacketPayload readMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
      return this.traceMessage((NativePacketPayload)this.packetReader.readMessage(reuse, header), header.getMessageSize(), reuse.isPresent());
   }

   public NativePacketPayload probeMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
      return this.traceMessage((NativePacketPayload)this.packetReader.probeMessage(reuse, header), header.getMessageSize(), reuse.isPresent());
   }

   private NativePacketPayload traceMessage(NativePacketPayload buf, int packetLength, boolean reuse) throws IOException {
      StringBuilder traceMessageBuf = new StringBuilder();
      traceMessageBuf.append(Messages.getString(reuse ? "PacketReader.5" : "PacketReader.6"));
      traceMessageBuf.append(StringUtils.dumpAsHex(buf.getByteBuffer(), packetLength < 1024 ? packetLength : 1024));
      if (packetLength > 1024) {
         traceMessageBuf.append(Messages.getString("PacketReader.7"));
         traceMessageBuf.append(1024);
         traceMessageBuf.append(Messages.getString("PacketReader.8"));
      }

      this.log.logTrace(traceMessageBuf.toString());
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
