package io.undertow.io;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public interface Sender {
   void send(ByteBuffer var1, IoCallback var2);

   void send(ByteBuffer[] var1, IoCallback var2);

   void send(ByteBuffer var1);

   void send(ByteBuffer[] var1);

   void send(String var1, IoCallback var2);

   void send(String var1, Charset var2, IoCallback var3);

   void send(String var1);

   void send(String var1, Charset var2);

   void transferFrom(FileChannel var1, IoCallback var2);

   void close(IoCallback var1);

   void close();
}
