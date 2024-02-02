package com.mysql.cj.protocol;

public interface Message {
  byte[] getByteBuffer();
  
  int getPosition();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\Message.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */