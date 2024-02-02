package com.mysql.cj.protocol;

public interface Warning {
  int getLevel();
  
  long getCode();
  
  String getMessage();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\Warning.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */