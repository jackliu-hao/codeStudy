package io.undertow.websockets.extensions;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.websockets.core.StreamSinkFrameChannel;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import java.io.IOException;

public interface ExtensionFunction {
  public static final int RSV1 = 4;
  
  public static final int RSV2 = 2;
  
  public static final int RSV3 = 1;
  
  boolean hasExtensionOpCode();
  
  int writeRsv(int paramInt);
  
  PooledByteBuffer transformForWrite(PooledByteBuffer paramPooledByteBuffer, StreamSinkFrameChannel paramStreamSinkFrameChannel, boolean paramBoolean) throws IOException;
  
  PooledByteBuffer transformForRead(PooledByteBuffer paramPooledByteBuffer, StreamSourceFrameChannel paramStreamSourceFrameChannel, boolean paramBoolean) throws IOException;
  
  void dispose();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\extensions\ExtensionFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */