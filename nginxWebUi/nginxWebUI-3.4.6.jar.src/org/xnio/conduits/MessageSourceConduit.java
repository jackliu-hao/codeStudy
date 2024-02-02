package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface MessageSourceConduit extends SourceConduit {
  int receive(ByteBuffer paramByteBuffer) throws IOException;
  
  long receive(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\MessageSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */