package org.xnio;

import java.io.File;

public class FileChangeEvent {
   private final File file;
   private final Type type;

   public FileChangeEvent(File file, Type type) {
      this.file = file;
      this.type = type;
   }

   public File getFile() {
      return this.file;
   }

   public Type getType() {
      return this.type;
   }

   public static enum Type {
      ADDED,
      REMOVED,
      MODIFIED;
   }
}
