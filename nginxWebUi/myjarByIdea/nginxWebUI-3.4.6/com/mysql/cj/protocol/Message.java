package com.mysql.cj.protocol;

public interface Message {
   byte[] getByteBuffer();

   int getPosition();
}
