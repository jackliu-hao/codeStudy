package com.mysql.cj.exceptions;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.protocol.PacketReceivedTimeHolder;
import com.mysql.cj.protocol.PacketSentTimeHolder;
import com.mysql.cj.protocol.ServerSession;

public class CJConnectionFeatureNotAvailableException extends CJCommunicationsException {
   private static final long serialVersionUID = -4129847384681995107L;

   public CJConnectionFeatureNotAvailableException() {
   }

   public CJConnectionFeatureNotAvailableException(PropertySet propertySet, ServerSession serverSession, PacketSentTimeHolder packetSentTimeHolder, Exception underlyingException) {
      super((Throwable)underlyingException);
      this.init(propertySet, serverSession, packetSentTimeHolder, (PacketReceivedTimeHolder)null);
   }

   public String getMessage() {
      return Messages.getString("ConnectionFeatureNotAvailableException.0");
   }
}
