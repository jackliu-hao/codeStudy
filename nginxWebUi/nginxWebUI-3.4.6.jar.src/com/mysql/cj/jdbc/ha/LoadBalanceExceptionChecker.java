package com.mysql.cj.jdbc.ha;

import java.util.Properties;

public interface LoadBalanceExceptionChecker {
  void init(Properties paramProperties);
  
  void destroy();
  
  boolean shouldExceptionTriggerFailover(Throwable paramThrowable);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\LoadBalanceExceptionChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */