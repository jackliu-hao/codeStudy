package com.mysql.cj.protocol.x;

import com.mysql.cj.protocol.ProtocolEntityFactory;

public class FetchDoneEntityFactory implements ProtocolEntityFactory<FetchDoneEntity, XMessage> {
   public FetchDoneEntity createFromMessage(XMessage message) {
      return new FetchDoneEntity();
   }
}
