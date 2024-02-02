package com.mysql.cj.jdbc.exceptions;

import com.mysql.cj.Messages;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.protocol.PacketReceivedTimeHolder;
import com.mysql.cj.protocol.PacketSentTimeHolder;

public class ConnectionFeatureNotAvailableException extends CommunicationsException {
   private static final long serialVersionUID = 8315412078945570018L;

   public ConnectionFeatureNotAvailableException(JdbcConnection conn, PacketSentTimeHolder packetSentTimeHolder, Exception underlyingException) {
      super(conn, packetSentTimeHolder, (PacketReceivedTimeHolder)null, underlyingException);
   }

   public ConnectionFeatureNotAvailableException(String message, Throwable underlyingException) {
      super(message, underlyingException);
   }

   public String getMessage() {
      return Messages.getString("ConnectionFeatureNotAvailableException.0");
   }

   public String getSQLState() {
      return "01S00";
   }
}
