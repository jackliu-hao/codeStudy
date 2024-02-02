package com.sun.mail.util;

import java.io.IOException;
import javax.mail.Folder;

public class FolderClosedIOException extends IOException {
   private transient Folder folder;
   private static final long serialVersionUID = 4281122580365555735L;

   public FolderClosedIOException(Folder folder) {
      this(folder, (String)null);
   }

   public FolderClosedIOException(Folder folder, String message) {
      super(message);
      this.folder = folder;
   }

   public Folder getFolder() {
      return this.folder;
   }
}
