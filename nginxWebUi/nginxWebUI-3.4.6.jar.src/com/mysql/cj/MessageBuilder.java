package com.mysql.cj;

import java.util.List;

public interface MessageBuilder<M extends com.mysql.cj.protocol.Message> {
  M buildSqlStatement(String paramString);
  
  M buildSqlStatement(String paramString, List<Object> paramList);
  
  M buildClose();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\MessageBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */