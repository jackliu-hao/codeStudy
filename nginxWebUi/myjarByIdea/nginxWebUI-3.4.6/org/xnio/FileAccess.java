package org.xnio;

public enum FileAccess {
   READ_ONLY(true, false),
   READ_WRITE(true, true),
   WRITE_ONLY(false, true);

   private final boolean read;
   private final boolean write;

   private FileAccess(boolean read, boolean write) {
      this.read = read;
      this.write = write;
   }

   boolean isRead() {
      return this.read;
   }

   boolean isWrite() {
      return this.write;
   }
}
