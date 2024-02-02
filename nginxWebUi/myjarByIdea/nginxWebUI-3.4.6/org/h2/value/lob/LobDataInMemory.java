package org.h2.value.lob;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public final class LobDataInMemory extends LobData {
   private final byte[] small;

   public LobDataInMemory(byte[] var1) {
      if (var1 == null) {
         throw new IllegalStateException();
      } else {
         this.small = var1;
      }
   }

   public InputStream getInputStream(long var1) {
      return new ByteArrayInputStream(this.small);
   }

   public byte[] getSmall() {
      return this.small;
   }

   public int getMemory() {
      return this.small.length + 127;
   }
}
