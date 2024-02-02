package org.apache.commons.compress.parallel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class FileBasedScatterGatherBackingStore implements ScatterGatherBackingStore {
   private final File target;
   private final OutputStream os;
   private boolean closed;

   public FileBasedScatterGatherBackingStore(File target) throws FileNotFoundException {
      this.target = target;

      try {
         this.os = Files.newOutputStream(target.toPath());
      } catch (FileNotFoundException var3) {
         throw var3;
      } catch (IOException var4) {
         throw new RuntimeException(var4);
      }
   }

   public InputStream getInputStream() throws IOException {
      return Files.newInputStream(this.target.toPath());
   }

   public void closeForWriting() throws IOException {
      if (!this.closed) {
         this.os.close();
         this.closed = true;
      }

   }

   public void writeOut(byte[] data, int offset, int length) throws IOException {
      this.os.write(data, offset, length);
   }

   public void close() throws IOException {
      try {
         this.closeForWriting();
      } finally {
         if (this.target.exists() && !this.target.delete()) {
            this.target.deleteOnExit();
         }

      }

   }
}
