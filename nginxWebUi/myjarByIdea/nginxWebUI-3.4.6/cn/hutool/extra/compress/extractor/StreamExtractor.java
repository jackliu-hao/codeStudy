package cn.hutool.extra.compress.extractor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.compress.CompressException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

public class StreamExtractor implements Extractor {
   private final ArchiveInputStream in;

   public StreamExtractor(Charset charset, File file) {
      this(charset, (String)null, (File)file);
   }

   public StreamExtractor(Charset charset, String archiverName, File file) {
      this(charset, archiverName, (InputStream)FileUtil.getInputStream(file));
   }

   public StreamExtractor(Charset charset, InputStream in) {
      this(charset, (String)null, (InputStream)in);
   }

   public StreamExtractor(Charset charset, String archiverName, InputStream in) {
      ArchiveStreamFactory factory = new ArchiveStreamFactory(charset.name());

      try {
         InputStream in = IoUtil.toBuffered(in);
         if (StrUtil.isBlank(archiverName)) {
            this.in = factory.createArchiveInputStream(in);
         } else {
            this.in = factory.createArchiveInputStream(archiverName, in);
         }

      } catch (ArchiveException var6) {
         throw new CompressException(var6);
      }
   }

   public void extract(File targetDir, Filter<ArchiveEntry> filter) {
      try {
         this.extractInternal(targetDir, filter);
      } catch (IOException var7) {
         throw new IORuntimeException(var7);
      } finally {
         this.close();
      }

   }

   private void extractInternal(File targetDir, Filter<ArchiveEntry> filter) throws IOException {
      Assert.isTrue(null != targetDir && (!targetDir.exists() || targetDir.isDirectory()), "target must be dir.");
      ArchiveInputStream in = this.in;

      while(true) {
         ArchiveEntry entry;
         do {
            if (null == (entry = in.getNextEntry())) {
               return;
            }
         } while(null != filter && !filter.accept(entry));

         if (in.canReadEntryData(entry)) {
            File outItemFile = FileUtil.file(targetDir, entry.getName());
            if (entry.isDirectory()) {
               outItemFile.mkdirs();
            } else {
               FileUtil.writeFromStream(in, outItemFile, false);
            }
         }
      }
   }

   public void close() {
      IoUtil.close(this.in);
   }
}
