package com.mysql.cj.protocol.x;

import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.x.protobuf.MysqlxResultset;

public class XProtocolRowFactory implements ProtocolEntityFactory<XProtocolRow, XMessage> {
   public XProtocolRow createFromMessage(XMessage message) {
      return new XProtocolRow((MysqlxResultset.Row)MysqlxResultset.Row.class.cast(message.getMessage()));
   }
}
