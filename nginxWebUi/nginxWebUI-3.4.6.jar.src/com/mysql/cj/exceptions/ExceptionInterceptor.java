package com.mysql.cj.exceptions;

import com.mysql.cj.log.Log;
import java.util.Properties;

public interface ExceptionInterceptor {
  ExceptionInterceptor init(Properties paramProperties, Log paramLog);
  
  void destroy();
  
  Exception interceptException(Exception paramException);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\ExceptionInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */