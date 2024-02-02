package org.noear.solon.core.handle;

import java.io.InputStream;

public class UploadedFile extends DownloadedFile {
   public long contentSize;
   public String extension;

   public UploadedFile() {
   }

   public UploadedFile(String contentType, InputStream content, String name) {
      super(contentType, content, name);
   }

   public UploadedFile(String contentType, long contentSize, InputStream content, String name, String extension) {
      super(contentType, content, name);
      this.contentSize = contentSize;
      this.extension = extension;
   }

   public boolean isEmpty() {
      return this.contentSize == 0L;
   }
}
