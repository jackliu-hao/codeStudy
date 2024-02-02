package cn.hutool.core.io.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

public class FileWriter extends FileWrapper {
   private static final long serialVersionUID = 1L;

   public static FileWriter create(File file, Charset charset) {
      return new FileWriter(file, charset);
   }

   public static FileWriter create(File file) {
      return new FileWriter(file);
   }

   public FileWriter(File file, Charset charset) {
      super(file, charset);
      this.checkFile();
   }

   public FileWriter(File file, String charset) {
      this(file, CharsetUtil.charset(charset));
   }

   public FileWriter(String filePath, Charset charset) {
      this(FileUtil.file(filePath), charset);
   }

   public FileWriter(String filePath, String charset) {
      this(FileUtil.file(filePath), CharsetUtil.charset(charset));
   }

   public FileWriter(File file) {
      this(file, DEFAULT_CHARSET);
   }

   public FileWriter(String filePath) {
      this(filePath, DEFAULT_CHARSET);
   }

   public File write(String content, boolean isAppend) throws IORuntimeException {
      BufferedWriter writer = null;

      try {
         writer = this.getWriter(isAppend);
         writer.write(content);
         writer.flush();
      } catch (IOException var8) {
         throw new IORuntimeException(var8);
      } finally {
         IoUtil.close(writer);
      }

      return this.file;
   }

   public File write(String content) throws IORuntimeException {
      return this.write(content, false);
   }

   public File append(String content) throws IORuntimeException {
      return this.write(content, true);
   }

   public <T> File writeLines(Iterable<T> list) throws IORuntimeException {
      return this.writeLines(list, false);
   }

   public <T> File appendLines(Iterable<T> list) throws IORuntimeException {
      return this.writeLines(list, true);
   }

   public <T> File writeLines(Iterable<T> list, boolean isAppend) throws IORuntimeException {
      return this.writeLines(list, (LineSeparator)null, isAppend);
   }

   public <T> File writeLines(Iterable<T> list, LineSeparator lineSeparator, boolean isAppend) throws IORuntimeException {
      PrintWriter writer = this.getPrintWriter(isAppend);
      Throwable var5 = null;

      try {
         boolean isFirst = true;
         Iterator var7 = list.iterator();

         while(var7.hasNext()) {
            T t = var7.next();
            if (null != t) {
               if (isFirst) {
                  isFirst = false;
                  if (isAppend && FileUtil.isNotEmpty(this.file)) {
                     this.printNewLine(writer, lineSeparator);
                  }
               } else {
                  this.printNewLine(writer, lineSeparator);
               }

               writer.print(t);
               writer.flush();
            }
         }
      } catch (Throwable var16) {
         var5 = var16;
         throw var16;
      } finally {
         if (writer != null) {
            if (var5 != null) {
               try {
                  writer.close();
               } catch (Throwable var15) {
                  var5.addSuppressed(var15);
               }
            } else {
               writer.close();
            }
         }

      }

      return this.file;
   }

   public File writeMap(Map<?, ?> map, String kvSeparator, boolean isAppend) throws IORuntimeException {
      return this.writeMap(map, (LineSeparator)null, kvSeparator, isAppend);
   }

   public File writeMap(Map<?, ?> map, LineSeparator lineSeparator, String kvSeparator, boolean isAppend) throws IORuntimeException {
      if (null == kvSeparator) {
         kvSeparator = " = ";
      }

      PrintWriter writer = this.getPrintWriter(isAppend);
      Throwable var6 = null;

      try {
         Iterator var7 = map.entrySet().iterator();

         while(var7.hasNext()) {
            Map.Entry<?, ?> entry = (Map.Entry)var7.next();
            if (null != entry) {
               writer.print(StrUtil.format("{}{}{}", new Object[]{entry.getKey(), kvSeparator, entry.getValue()}));
               this.printNewLine(writer, lineSeparator);
               writer.flush();
            }
         }
      } catch (Throwable var16) {
         var6 = var16;
         throw var16;
      } finally {
         if (writer != null) {
            if (var6 != null) {
               try {
                  writer.close();
               } catch (Throwable var15) {
                  var6.addSuppressed(var15);
               }
            } else {
               writer.close();
            }
         }

      }

      return this.file;
   }

   public File write(byte[] data, int off, int len) throws IORuntimeException {
      return this.write(data, off, len, false);
   }

   public File append(byte[] data, int off, int len) throws IORuntimeException {
      return this.write(data, off, len, true);
   }

   public File write(byte[] data, int off, int len, boolean isAppend) throws IORuntimeException {
      try {
         FileOutputStream out = new FileOutputStream(FileUtil.touch(this.file), isAppend);
         Throwable var6 = null;

         try {
            out.write(data, off, len);
            out.flush();
         } catch (Throwable var16) {
            var6 = var16;
            throw var16;
         } finally {
            if (out != null) {
               if (var6 != null) {
                  try {
                     out.close();
                  } catch (Throwable var15) {
                     var6.addSuppressed(var15);
                  }
               } else {
                  out.close();
               }
            }

         }
      } catch (IOException var18) {
         throw new IORuntimeException(var18);
      }

      return this.file;
   }

   public File writeFromStream(InputStream in) throws IORuntimeException {
      return this.writeFromStream(in, true);
   }

   public File writeFromStream(InputStream in, boolean isCloseIn) throws IORuntimeException {
      FileOutputStream out = null;

      try {
         out = new FileOutputStream(FileUtil.touch(this.file));
         IoUtil.copy((InputStream)in, (OutputStream)out);
      } catch (IOException var8) {
         throw new IORuntimeException(var8);
      } finally {
         IoUtil.close(out);
         if (isCloseIn) {
            IoUtil.close(in);
         }

      }

      return this.file;
   }

   public BufferedOutputStream getOutputStream() throws IORuntimeException {
      try {
         return new BufferedOutputStream(new FileOutputStream(FileUtil.touch(this.file)));
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public BufferedWriter getWriter(boolean isAppend) throws IORuntimeException {
      try {
         return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtil.touch(this.file), isAppend), this.charset));
      } catch (Exception var3) {
         throw new IORuntimeException(var3);
      }
   }

   public PrintWriter getPrintWriter(boolean isAppend) throws IORuntimeException {
      return new PrintWriter(this.getWriter(isAppend));
   }

   private void checkFile() throws IORuntimeException {
      Assert.notNull(this.file, "File to write content is null !");
      if (this.file.exists() && !this.file.isFile()) {
         throw new IORuntimeException("File [{}] is not a file !", new Object[]{this.file.getAbsoluteFile()});
      }
   }

   private void printNewLine(PrintWriter writer, LineSeparator lineSeparator) {
      if (null == lineSeparator) {
         writer.println();
      } else {
         writer.print(lineSeparator.getValue());
      }

   }
}
