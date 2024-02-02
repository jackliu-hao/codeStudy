package com.mysql.cj;

public interface PreparedQuery<T extends QueryBindings<?>> extends Query {
  ParseInfo getParseInfo();
  
  void setParseInfo(ParseInfo paramParseInfo);
  
  void checkNullOrEmptyQuery(String paramString);
  
  String getOriginalSql();
  
  void setOriginalSql(String paramString);
  
  int getParameterCount();
  
  void setParameterCount(int paramInt);
  
  T getQueryBindings();
  
  void setQueryBindings(T paramT);
  
  int computeBatchSize(int paramInt);
  
  int getBatchCommandIndex();
  
  void setBatchCommandIndex(int paramInt);
  
  String asSql();
  
  String asSql(boolean paramBoolean);
  
  <M extends com.mysql.cj.protocol.Message> M fillSendPacket();
  
  <M extends com.mysql.cj.protocol.Message> M fillSendPacket(QueryBindings<?> paramQueryBindings);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\PreparedQuery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */