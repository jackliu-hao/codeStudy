package cn.hutool.extra.compress.archiver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.compress.CompressException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

public class StreamArchiver implements Archiver {
   private final ArchiveOutputStream out;

   public static StreamArchiver create(Charset charset, String archiverName, File file) {
      return new StreamArchiver(charset, archiverName, file);
   }

   public static StreamArchiver create(Charset charset, String archiverName, OutputStream out) {
      return new StreamArchiver(charset, archiverName, out);
   }

   public StreamArchiver(Charset charset, String archiverName, File file) {
      this(charset, archiverName, (OutputStream)FileUtil.getOutputStream(file));
   }

   public StreamArchiver(Charset charset, String archiverName, OutputStream targetStream) {
      ArchiveStreamFactory factory = new ArchiveStreamFactory(charset.name());

      try {
         this.out = factory.createArchiveOutputStream(archiverName, targetStream);
      } catch (ArchiveException var6) {
         throw new CompressException(var6);
      }

      if (this.out instanceof TarArchiveOutputStream) {
         ((TarArchiveOutputStream)this.out).setLongFileMode(2);
      } else if (this.out instanceof ArArchiveOutputStream) {
         ((ArArchiveOutputStream)this.out).setLongFileMode(1);
      }

   }

   public StreamArchiver add(File file, String path, Filter<File> filter) throws IORuntimeException {
      try {
         this.addInternal(file, path, filter);
         return this;
      } catch (IOException var5) {
         throw new IORuntimeException(var5);
      }
   }

   public StreamArchiver finish() {
      try {
         this.out.finish();
         return this;
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public void close() {
      try {
         this.finish();
      } catch (Exception var2) {
      }

      IoUtil.close(this.out);
   }

   private void addInternal(File file, String path, Filter<File> filter) throws IOException {
      if (null == filter || filter.accept(file)) {
         ArchiveOutputStream out = this.out;
         String entryName;
         if (StrUtil.isNotEmpty(path)) {
            entryName = StrUtil.addSuffixIfNot(path, "/") + file.getName();
         } else {
            entryName = file.getName();
         }

         out.putArchiveEntry(out.createArchiveEntry(file, entryName));
         if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (ArrayUtil.isNotEmpty((Object[])files)) {
               File[] var7 = files;
               int var8 = files.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  File childFile = var7[var9];
                  this.addInternal(childFile, entryName, filter);
               }
            }
         } else {
            if (file.isFile()) {
               FileUtil.writeToStream((File)file, out);
            }

            out.closeArchiveEntry();
         }

      }
   }
}
