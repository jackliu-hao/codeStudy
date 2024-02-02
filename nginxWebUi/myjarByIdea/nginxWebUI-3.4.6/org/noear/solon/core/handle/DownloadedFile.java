package org.noear.solon.core.handle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.noear.solon.Utils;

public class DownloadedFile {
   public String contentType;
   public InputStream content;
   public String name;

   public DownloadedFile() {
   }

   public DownloadedFile(String contentType, InputStream content, String name) {
      this.contentType = contentType;
      this.content = content;
      this.name = name;
   }

   public DownloadedFile(String contentType, byte[] content, String name) {
      this.contentType = contentType;
      this.content = new ByteArrayInputStream(content);
      this.name = name;
   }

   public void transferTo(File file) throws IOException {
      FileOutputStream stream = new FileOutputStream(file);
      Throwable var3 = null;

      try {
         Utils.transferTo(this.content, stream);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (stream != null) {
            if (var3 != null) {
               try {
                  stream.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               stream.close();
            }
         }

      }

   }

   public void transferTo(OutputStream stream) throws IOException {
      Utils.transferTo(this.content, stream);
   }
}
