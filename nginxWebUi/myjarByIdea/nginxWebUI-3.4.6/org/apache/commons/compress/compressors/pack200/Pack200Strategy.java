package org.apache.commons.compress.compressors.pack200;

import java.io.IOException;

public enum Pack200Strategy {
   IN_MEMORY {
      StreamBridge newStreamBridge() {
         return new InMemoryCachingStreamBridge();
      }
   },
   TEMP_FILE {
      StreamBridge newStreamBridge() throws IOException {
         return new TempFileCachingStreamBridge();
      }
   };

   private Pack200Strategy() {
   }

   abstract StreamBridge newStreamBridge() throws IOException;

   // $FF: synthetic method
   Pack200Strategy(Object x2) {
      this();
   }
}
