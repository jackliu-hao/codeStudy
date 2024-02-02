package com.mysql.cj;

import com.mysql.cj.protocol.Message;

public interface PreparedQuery<T extends QueryBindings<?>> extends Query {
   ParseInfo getParseInfo();

   void setParseInfo(ParseInfo var1);

   void checkNullOrEmptyQuery(String var1);

   String getOriginalSql();

   void setOriginalSql(String var1);

   int getParameterCount();

   void setParameterCount(int var1);

   T getQueryBindings();

   void setQueryBindings(T var1);

   int computeBatchSize(int var1);

   int getBatchCommandIndex();

   void setBatchCommandIndex(int var1);

   String asSql();

   String asSql(boolean var1);

   <M extends Message> M fillSendPacket();

   <M extends Message> M fillSendPacket(QueryBindings<?> var1);
}
