package cn.hutool.extra.compress;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.compress.archiver.Archiver;
import cn.hutool.extra.compress.archiver.SevenZArchiver;
import cn.hutool.extra.compress.archiver.StreamArchiver;
import cn.hutool.extra.compress.extractor.Extractor;
import cn.hutool.extra.compress.extractor.SevenZExtractor;
import cn.hutool.extra.compress.extractor.StreamExtractor;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.commons.compress.archivers.StreamingNotSupportedException;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class CompressUtil {
   public static CompressorOutputStream getOut(String compressorName, OutputStream out) {
      try {
         return (new CompressorStreamFactory()).createCompressorOutputStream(compressorName, out);
      } catch (CompressorException var3) {
         throw new CompressException(var3);
      }
   }

   public static CompressorInputStream getIn(String compressorName, InputStream in) {
      in = IoUtil.toMarkSupportStream(in);

      try {
         if (StrUtil.isBlank(compressorName)) {
            compressorName = CompressorStreamFactory.detect(in);
         }

         return (new CompressorStreamFactory()).createCompressorInputStream(compressorName, in);
      } catch (CompressorException var3) {
         throw new CompressException(var3);
      }
   }

   public static Archiver createArchiver(Charset charset, String archiverName, File file) {
      return (Archiver)("7z".equalsIgnoreCase(archiverName) ? new SevenZArchiver(file) : StreamArchiver.create(charset, archiverName, file));
   }

   public static Archiver createArchiver(Charset charset, String archiverName, OutputStream out) {
      return (Archiver)("7z".equalsIgnoreCase(archiverName) ? new SevenZArchiver(out) : StreamArchiver.create(charset, archiverName, out));
   }

   public static Extractor createExtractor(Charset charset, File file) {
      return createExtractor(charset, (String)null, (File)file);
   }

   public static Extractor createExtractor(Charset charset, String archiverName, File file) {
      if ("7z".equalsIgnoreCase(archiverName)) {
         return new SevenZExtractor(file);
      } else {
         try {
            return new StreamExtractor(charset, archiverName, file);
         } catch (CompressException var5) {
            Throwable cause = var5.getCause();
            if (cause instanceof StreamingNotSupportedException && cause.getMessage().contains("7z")) {
               return new SevenZExtractor(file);
            } else {
               throw var5;
            }
         }
      }
   }

   public static Extractor createExtractor(Charset charset, InputStream in) {
      return createExtractor(charset, (String)null, (InputStream)in);
   }

   public static Extractor createExtractor(Charset charset, String archiverName, InputStream in) {
      if ("7z".equalsIgnoreCase(archiverName)) {
         return new SevenZExtractor(in);
      } else {
         try {
            return new StreamExtractor(charset, archiverName, in);
         } catch (CompressException var5) {
            Throwable cause = var5.getCause();
            if (cause instanceof StreamingNotSupportedException && cause.getMessage().contains("7z")) {
               return new SevenZExtractor(in);
            } else {
               throw var5;
            }
         }
      }
   }
}
