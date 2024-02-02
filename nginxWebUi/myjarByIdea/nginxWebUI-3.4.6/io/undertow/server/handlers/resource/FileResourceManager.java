package io.undertow.server.handlers.resource;

import io.undertow.UndertowMessages;
import java.io.File;

public class FileResourceManager extends PathResourceManager {
   public FileResourceManager(File base) {
      this(base, 1024L, true, false, (String[])null);
   }

   public FileResourceManager(File base, long transferMinSize) {
      this(base, transferMinSize, true, false, (String[])null);
   }

   public FileResourceManager(File base, long transferMinSize, boolean caseSensitive) {
      this(base, transferMinSize, caseSensitive, false, (String[])null);
   }

   public FileResourceManager(File base, long transferMinSize, boolean followLinks, String... safePaths) {
      this(base, transferMinSize, true, followLinks, safePaths);
   }

   protected FileResourceManager(long transferMinSize, boolean caseSensitive, boolean followLinks, String... safePaths) {
      super(transferMinSize, caseSensitive, followLinks, safePaths);
   }

   public FileResourceManager(File base, long transferMinSize, boolean caseSensitive, boolean followLinks, String... safePaths) {
      super(base.toPath(), transferMinSize, caseSensitive, followLinks, safePaths);
   }

   public File getBase() {
      return new File(this.base);
   }

   public FileResourceManager setBase(File base) {
      if (base == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("base");
      } else {
         String basePath = base.getAbsolutePath();
         if (!basePath.endsWith("/")) {
            basePath = basePath + '/';
         }

         this.base = basePath;
         return this;
      }
   }
}
