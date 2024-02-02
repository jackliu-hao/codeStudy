package com.mysql.cj.protocol;

import java.nio.ByteBuffer;

public interface MessageHeader {
  ByteBuffer getBuffer();
  
  int getMessageSize();
  
  byte getMessageSequence();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\MessageHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */