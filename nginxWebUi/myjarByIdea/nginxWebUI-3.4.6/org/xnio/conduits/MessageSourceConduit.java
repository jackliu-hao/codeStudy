package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface MessageSourceConduit extends SourceConduit {
   int receive(ByteBuffer var1) throws IOException;

   long receive(ByteBuffer[] var1, int var2, int var3) throws IOException;
}
