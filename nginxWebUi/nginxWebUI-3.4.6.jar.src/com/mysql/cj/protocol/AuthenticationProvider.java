package com.mysql.cj.protocol;

import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.ExceptionInterceptor;

public interface AuthenticationProvider<M extends Message> {
  void init(Protocol<M> paramProtocol, PropertySet paramPropertySet, ExceptionInterceptor paramExceptionInterceptor);
  
  void connect(String paramString1, String paramString2, String paramString3);
  
  void changeUser(String paramString1, String paramString2, String paramString3);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\AuthenticationProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */