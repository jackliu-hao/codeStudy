package com.mysql.cj.protocol.x;

import com.mysql.cj.protocol.ProtocolEntityFactory;

public class OkFactory implements ProtocolEntityFactory<Ok, XMessage> {
   public Ok createFromMessage(XMessage message) {
      return new Ok();
   }
}
