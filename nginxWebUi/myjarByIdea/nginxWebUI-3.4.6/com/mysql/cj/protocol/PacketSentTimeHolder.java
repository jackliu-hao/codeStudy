package com.mysql.cj.protocol;

public interface PacketSentTimeHolder {
   default long getLastPacketSentTime() {
      return 0L;
   }

   default long getPreviousPacketSentTime() {
      return 0L;
   }
}
