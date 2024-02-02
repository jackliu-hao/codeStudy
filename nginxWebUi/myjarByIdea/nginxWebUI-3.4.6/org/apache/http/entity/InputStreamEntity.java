package org.apache.http.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.util.Args;

public class InputStreamEntity extends AbstractHttpEntity {
   private final InputStream content;
   private final long length;

   public InputStreamEntity(InputStream inStream) {
      this(inStream, -1L);
   }

   public InputStreamEntity(InputStream inStream, long length) {
      this(inStream, length, (ContentType)null);
   }

   public InputStreamEntity(InputStream inStream, ContentType contentType) {
      this(inStream, -1L, contentType);
   }

   public InputStreamEntity(InputStream inStream, long length, ContentType contentType) {
      this.content = (InputStream)Args.notNull(inStream, "Source input stream");
      this.length = length;
      if (contentType != null) {
         this.setContentType(contentType.toString());
      }

   }

   public boolean isRepeatable() {
      return false;
   }

   public long getContentLength() {
      return this.length;
   }

   public InputStream getContent() throws IOException {
      return this.content;
   }

   public void writeTo(OutputStream outStream) throws IOException {
      Args.notNull(outStream, "Output stream");
      InputStream inStream = this.content;

      try {
         byte[] buffer = new byte[4096];
         int readLen;
         if (this.length < 0L) {
            while((readLen = inStream.read(buffer)) != -1) {
               outStream.write(buffer, 0, readLen);
            }
         } else {
            for(long remaining = this.length; remaining > 0L; remaining -= (long)readLen) {
               readLen = inStream.read(buffer, 0, (int)Math.min(4096L, remaining));
               if (readLen == -1) {
                  break;
               }

               outStream.write(buffer, 0, readLen);
            }
         }
      } finally {
         inStream.close();
      }

   }

   public boolean isStreaming() {
      return true;
   }
}
