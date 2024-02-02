package com.mysql.cj;

import com.mysql.cj.protocol.Message;
import java.util.List;

public interface MessageBuilder<M extends Message> {
   M buildSqlStatement(String var1);

   M buildSqlStatement(String var1, List<Object> var2);

   M buildClose();
}
