package io.undertow.io;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public interface Sender {
  void send(ByteBuffer paramByteBuffer, IoCallback paramIoCallback);
  
  void send(ByteBuffer[] paramArrayOfByteBuffer, IoCallback paramIoCallback);
  
  void send(ByteBuffer paramByteBuffer);
  
  void send(ByteBuffer[] paramArrayOfByteBuffer);
  
  void send(String paramString, IoCallback paramIoCallback);
  
  void send(String paramString, Charset paramCharset, IoCallback paramIoCallback);
  
  void send(String paramString);
  
  void send(String paramString, Charset paramCharset);
  
  void transferFrom(FileChannel paramFileChannel, IoCallback paramIoCallback);
  
  void close(IoCallback paramIoCallback);
  
  void close();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\io\Sender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */