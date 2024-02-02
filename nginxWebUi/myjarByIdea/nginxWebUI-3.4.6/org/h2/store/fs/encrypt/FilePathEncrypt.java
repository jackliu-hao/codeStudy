package org.h2.store.fs.encrypt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FilePathWrapper;
import org.h2.store.fs.FileUtils;

public class FilePathEncrypt extends FilePathWrapper {
   private static final String SCHEME = "encrypt";

   public static void register() {
      FilePath.register(new FilePathEncrypt());
   }

   public FileChannel open(String var1) throws IOException {
      String[] var2 = this.parse(this.name);
      FileChannel var3 = FileUtils.open(var2[1], var1);
      byte[] var4 = var2[0].getBytes(StandardCharsets.UTF_8);
      return new FileEncrypt(this.name, var4, var3);
   }

   public String getScheme() {
      return "encrypt";
   }

   protected String getPrefix() {
      String[] var1 = this.parse(this.name);
      return this.getScheme() + ":" + var1[0] + ":";
   }

   public FilePath unwrap(String var1) {
      return FilePath.get(this.parse(var1)[1]);
   }

   public long size() {
      long var1 = this.getBase().size() - 4096L;
      var1 = Math.max(0L, var1);
      if ((var1 & 4095L) != 0L) {
         var1 -= 4096L;
      }

      return var1;
   }

   public OutputStream newOutputStream(boolean var1) throws IOException {
      return newFileChannelOutputStream(this.open("rw"), var1);
   }

   public InputStream newInputStream() throws IOException {
      return Channels.newInputStream(this.open("r"));
   }

   private String[] parse(String var1) {
      if (!var1.startsWith(this.getScheme())) {
         throw new IllegalArgumentException(var1 + " doesn't start with " + this.getScheme());
      } else {
         var1 = var1.substring(this.getScheme().length() + 1);
         int var2 = var1.indexOf(58);
         if (var2 < 0) {
            throw new IllegalArgumentException(var1 + " doesn't contain encryption algorithm and password");
         } else {
            String var3 = var1.substring(0, var2);
            var1 = var1.substring(var2 + 1);
            return new String[]{var3, var1};
         }
      }
   }

   public static byte[] getPasswordBytes(char[] var0) {
      int var1 = var0.length;
      byte[] var2 = new byte[var1 * 2];

      for(int var3 = 0; var3 < var1; ++var3) {
         char var4 = var0[var3];
         var2[var3 + var3] = (byte)(var4 >>> 8);
         var2[var3 + var3 + 1] = (byte)var4;
      }

      return var2;
   }
}
