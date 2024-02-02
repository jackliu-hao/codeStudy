package io.undertow.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public interface BufferWritableOutputStream {
   void write(ByteBuffer[] var1) throws IOException;

   void write(ByteBuffer var1) throws IOException;

   void transferFrom(FileChannel var1) throws IOException;
}
