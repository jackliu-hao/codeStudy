package com.mysql.cj.protocol;

import java.nio.ByteBuffer;

public interface MessageHeader {
   ByteBuffer getBuffer();

   int getMessageSize();

   byte getMessageSequence();
}
