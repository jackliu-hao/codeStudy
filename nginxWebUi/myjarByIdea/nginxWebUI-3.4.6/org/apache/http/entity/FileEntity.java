package org.apache.http.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.util.Args;

public class FileEntity extends AbstractHttpEntity implements Cloneable {
   protected final File file;

   /** @deprecated */
   @Deprecated
   public FileEntity(File file, String contentType) {
      this.file = (File)Args.notNull(file, "File");
      this.setContentType(contentType);
   }

   public FileEntity(File file, ContentType contentType) {
      this.file = (File)Args.notNull(file, "File");
      if (contentType != null) {
         this.setContentType(contentType.toString());
      }

   }

   public FileEntity(File file) {
      this.file = (File)Args.notNull(file, "File");
   }

   public boolean isRepeatable() {
      return true;
   }

   public long getContentLength() {
      return this.file.length();
   }

   public InputStream getContent() throws IOException {
      return new FileInputStream(this.file);
   }

   public void writeTo(OutputStream outStream) throws IOException {
      Args.notNull(outStream, "Output stream");
      InputStream inStream = new FileInputStream(this.file);

      try {
         byte[] tmp = new byte[4096];

         int l;
         while((l = inStream.read(tmp)) != -1) {
            outStream.write(tmp, 0, l);
         }

         outStream.flush();
      } finally {
         inStream.close();
      }
   }

   public boolean isStreaming() {
      return false;
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
