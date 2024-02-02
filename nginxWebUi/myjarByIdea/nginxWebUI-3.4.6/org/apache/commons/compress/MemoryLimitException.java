package org.apache.commons.compress;

import java.io.IOException;

public class MemoryLimitException extends IOException {
   private static final long serialVersionUID = 1L;
   private final long memoryNeededInKb;
   private final int memoryLimitInKb;

   public MemoryLimitException(long memoryNeededInKb, int memoryLimitInKb) {
      super(buildMessage(memoryNeededInKb, memoryLimitInKb));
      this.memoryNeededInKb = memoryNeededInKb;
      this.memoryLimitInKb = memoryLimitInKb;
   }

   public MemoryLimitException(long memoryNeededInKb, int memoryLimitInKb, Exception e) {
      super(buildMessage(memoryNeededInKb, memoryLimitInKb), e);
      this.memoryNeededInKb = memoryNeededInKb;
      this.memoryLimitInKb = memoryLimitInKb;
   }

   public long getMemoryNeededInKb() {
      return this.memoryNeededInKb;
   }

   public int getMemoryLimitInKb() {
      return this.memoryLimitInKb;
   }

   private static String buildMessage(long memoryNeededInKb, int memoryLimitInKb) {
      return memoryNeededInKb + " kb of memory would be needed; limit was " + memoryLimitInKb + " kb. If the file is not corrupt, consider increasing the memory limit.";
   }
}
