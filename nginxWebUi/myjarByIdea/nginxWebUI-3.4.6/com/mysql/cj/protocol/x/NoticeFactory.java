package com.mysql.cj.protocol.x;

import com.mysql.cj.protocol.ProtocolEntityFactory;

public class NoticeFactory implements ProtocolEntityFactory<Notice, XMessage> {
   public Notice createFromMessage(XMessage message) {
      return Notice.getInstance(message);
   }
}
