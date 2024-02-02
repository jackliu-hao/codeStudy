package io.undertow.websockets.core.function;

import io.undertow.server.protocol.framed.FrameHeaderData;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface ChannelFunction {
   void newFrame(FrameHeaderData var1);

   void afterRead(ByteBuffer var1, int var2, int var3) throws IOException;

   void beforeWrite(ByteBuffer var1, int var2, int var3) throws IOException;

   void complete() throws IOException;
}
