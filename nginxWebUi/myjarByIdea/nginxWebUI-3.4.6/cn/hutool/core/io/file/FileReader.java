package cn.hutool.core.io.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.LineHandler;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileReader extends FileWrapper {
   private static final long serialVersionUID = 1L;

   public static FileReader create(File file, Charset charset) {
      return new FileReader(file, charset);
   }

   public static FileReader create(File file) {
      return new FileReader(file);
   }

   public FileReader(File file, Charset charset) {
      super(file, charset);
      this.checkFile();
   }

   public FileReader(File file, String charset) {
      this(file, CharsetUtil.charset(charset));
   }

   public FileReader(String filePath, Charset charset) {
      this(FileUtil.file(filePath), charset);
   }

   public FileReader(String filePath, String charset) {
      this(FileUtil.file(filePath), CharsetUtil.charset(charset));
   }

   public FileReader(File file) {
      this(file, DEFAULT_CHARSET);
   }

   public FileReader(String filePath) {
      this(filePath, DEFAULT_CHARSET);
   }

   public byte[] readBytes() throws IORuntimeException {
      long len = this.file.length();
      if (len >= 2147483647L) {
         throw new IORuntimeException("File is larger then max array size");
      } else {
         byte[] bytes = new byte[(int)len];
         FileInputStream in = null;

         try {
            in = new FileInputStream(this.file);
            int readLength = in.read(bytes);
            if ((long)readLength < len) {
               throw new IOException(StrUtil.format("File length is [{}] but read [{}]!", new Object[]{len, readLength}));
            }
         } catch (Exception var10) {
            throw new IORuntimeException(var10);
         } finally {
            IoUtil.close(in);
         }

         return bytes;
      }
   }

   public String readString() throws IORuntimeException {
      return new String(this.readBytes(), this.charset);
   }

   public <T extends Collection<String>> T readLines(T collection) throws IORuntimeException {
      BufferedReader reader = null;

      try {
         reader = FileUtil.getReader(this.file, this.charset);

         while(true) {
            String line = reader.readLine();
            if (line == null) {
               Collection var4 = collection;
               return var4;
            }

            collection.add(line);
         }
      } catch (IOException var8) {
         throw new IORuntimeException(var8);
      } finally {
         IoUtil.close(reader);
      }
   }

   public void readLines(LineHandler lineHandler) throws IORuntimeException {
      BufferedReader reader = null;

      try {
         reader = FileUtil.getReader(this.file, this.charset);
         IoUtil.readLines(reader, (LineHandler)lineHandler);
      } finally {
         IoUtil.close(reader);
      }

   }

   public List<String> readLines() throws IORuntimeException {
      return (List)this.readLines((Collection)(new ArrayList()));
   }

   public <T> T read(ReaderHandler<T> readerHandler) throws IORuntimeException {
      BufferedReader reader = null;

      Object result;
      try {
         reader = FileUtil.getReader(this.file, this.charset);
         result = readerHandler.handle(reader);
      } catch (IOException var8) {
         throw new IORuntimeException(var8);
      } finally {
         IoUtil.close(reader);
      }

      return result;
   }

   public BufferedReader getReader() throws IORuntimeException {
      return IoUtil.getReader(this.getInputStream(), (Charset)this.charset);
   }

   public BufferedInputStream getInputStream() throws IORuntimeException {
      try {
         return new BufferedInputStream(new FileInputStream(this.file));
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public long writeToStream(OutputStream out) throws IORuntimeException {
      return this.writeToStream(out, false);
   }

   public long writeToStream(OutputStream out, boolean isCloseOut) throws IORuntimeException {
      Throwable var5;
      try {
         FileInputStream in = new FileInputStream(this.file);
         Throwable var4 = null;

         try {
            var5 = IoUtil.copy((InputStream)in, (OutputStream)out);
         } catch (Throwable var24) {
            var5 = var24;
            var4 = var24;
            throw var24;
         } finally {
            if (in != null) {
               if (var4 != null) {
                  try {
                     in.close();
                  } catch (Throwable var23) {
                     var4.addSuppressed(var23);
                  }
               } else {
                  in.close();
               }
            }

         }
      } catch (IOException var26) {
         throw new IORuntimeException(var26);
      } finally {
         if (isCloseOut) {
            IoUtil.close(out);
         }

      }

      return (long)var5;
   }

   private void checkFile() throws IORuntimeException {
      if (!this.file.exists()) {
         throw new IORuntimeException("File not exist: " + this.file);
      } else if (!this.file.isFile()) {
         throw new IORuntimeException("Not a file:" + this.file);
      }
   }

   public interface ReaderHandler<T> {
      T handle(BufferedReader var1) throws IOException;
   }
}
