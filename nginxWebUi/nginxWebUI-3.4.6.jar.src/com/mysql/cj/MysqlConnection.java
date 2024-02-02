package com.mysql.cj;

import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.protocol.ServerSessionStateController;
import java.util.Properties;

public interface MysqlConnection {
  PropertySet getPropertySet();
  
  void createNewIO(boolean paramBoolean);
  
  long getId();
  
  Properties getProperties();
  
  Object getConnectionMutex();
  
  Session getSession();
  
  String getURL();
  
  String getUser();
  
  ExceptionInterceptor getExceptionInterceptor();
  
  void checkClosed();
  
  void normalClose();
  
  void cleanup(Throwable paramThrowable);
  
  ServerSessionStateController getServerSessionStateController();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\MysqlConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */