package com.mysql.cj.protocol.x;

import com.mysql.cj.protocol.ProtocolEntityFactory;

public class StatementExecuteOkFactory implements ProtocolEntityFactory<StatementExecuteOk, XMessage> {
   public StatementExecuteOk createFromMessage(XMessage message) {
      return new StatementExecuteOk();
   }
}
