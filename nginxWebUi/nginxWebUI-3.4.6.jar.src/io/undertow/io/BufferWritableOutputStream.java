package io.undertow.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public interface BufferWritableOutputStream {
  void write(ByteBuffer[] paramArrayOfByteBuffer) throws IOException;
  
  void write(ByteBuffer paramByteBuffer) throws IOException;
  
  void transferFrom(FileChannel paramFileChannel) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\io\BufferWritableOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */