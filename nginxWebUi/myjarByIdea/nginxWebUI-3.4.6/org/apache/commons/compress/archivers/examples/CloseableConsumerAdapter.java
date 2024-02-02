package org.apache.commons.compress.archivers.examples;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;

final class CloseableConsumerAdapter implements Closeable {
   private final CloseableConsumer consumer;
   private Closeable closeable;

   CloseableConsumerAdapter(CloseableConsumer consumer) {
      this.consumer = (CloseableConsumer)Objects.requireNonNull(consumer, "consumer");
   }

   <C extends Closeable> C track(C closeable) {
      this.closeable = closeable;
      return closeable;
   }

   public void close() throws IOException {
      if (this.closeable != null) {
         this.consumer.accept(this.closeable);
      }

   }
}
