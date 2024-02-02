package org.apache.commons.compress.archivers.examples;

import java.io.Closeable;
import java.io.IOException;

public interface CloseableConsumer {
   CloseableConsumer CLOSING_CONSUMER = Closeable::close;
   CloseableConsumer NULL_CONSUMER = (c) -> {
   };

   void accept(Closeable var1) throws IOException;
}
