package org.xnio.channels;

import java.io.IOException;
import org.xnio.Option;

public interface Configurable {
   Configurable EMPTY = new Configurable() {
      public boolean supportsOption(Option<?> option) {
         return false;
      }

      public <T> T getOption(Option<T> option) throws IOException {
         return null;
      }

      public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
         return null;
      }
   };

   boolean supportsOption(Option<?> var1);

   <T> T getOption(Option<T> var1) throws IOException;

   <T> T setOption(Option<T> var1, T var2) throws IllegalArgumentException, IOException;
}
