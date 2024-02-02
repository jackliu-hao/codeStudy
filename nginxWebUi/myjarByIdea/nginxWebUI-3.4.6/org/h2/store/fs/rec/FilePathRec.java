package org.h2.store.fs.rec;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FilePathWrapper;
import org.h2.store.fs.Recorder;

public class FilePathRec extends FilePathWrapper {
   private static final FilePathRec INSTANCE = new FilePathRec();
   private static Recorder recorder;
   private boolean trace;

   public static void register() {
      FilePath.register(INSTANCE);
   }

   public static void setRecorder(Recorder var0) {
      recorder = var0;
   }

   public boolean createFile() {
      this.log(2, this.name);
      return super.createFile();
   }

   public FilePath createTempFile(String var1, boolean var2) throws IOException {
      this.log(3, this.unwrap(this.name) + ":" + var1 + ":" + var2);
      return super.createTempFile(var1, var2);
   }

   public void delete() {
      this.log(4, this.name);
      super.delete();
   }

   public FileChannel open(String var1) throws IOException {
      return new FileRec(this, super.open(var1), this.name);
   }

   public OutputStream newOutputStream(boolean var1) throws IOException {
      this.log(5, this.name);
      return super.newOutputStream(var1);
   }

   public void moveTo(FilePath var1, boolean var2) {
      this.log(6, this.unwrap(this.name) + ":" + this.unwrap(var1.name));
      super.moveTo(var1, var2);
   }

   public boolean isTrace() {
      return this.trace;
   }

   public void setTrace(boolean var1) {
      this.trace = var1;
   }

   void log(int var1, String var2) {
      this.log(var1, var2, (byte[])null, 0L);
   }

   void log(int var1, String var2, byte[] var3, long var4) {
      if (recorder != null) {
         recorder.log(var1, var2, var3, var4);
      }

   }

   public String getScheme() {
      return "rec";
   }
}
