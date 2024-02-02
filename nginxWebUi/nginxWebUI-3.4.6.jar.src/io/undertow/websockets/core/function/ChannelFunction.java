package io.undertow.websockets.core.function;

import io.undertow.server.protocol.framed.FrameHeaderData;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface ChannelFunction {
  void newFrame(FrameHeaderData paramFrameHeaderData);
  
  void afterRead(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  void beforeWrite(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  void complete() throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\function\ChannelFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */