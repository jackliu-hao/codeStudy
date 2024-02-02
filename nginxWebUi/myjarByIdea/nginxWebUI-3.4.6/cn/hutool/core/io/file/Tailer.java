package cn.hutool.core.io.file;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.LineHandler;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Tailer implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final LineHandler CONSOLE_HANDLER = new ConsoleLineHandler();
   private final Charset charset;
   private final LineHandler lineHandler;
   private final int initReadLine;
   private final long period;
   private final RandomAccessFile randomAccessFile;
   private final ScheduledExecutorService executorService;

   public Tailer(File file, LineHandler lineHandler) {
      this(file, lineHandler, 0);
   }

   public Tailer(File file, LineHandler lineHandler, int initReadLine) {
      this(file, CharsetUtil.CHARSET_UTF_8, lineHandler, initReadLine, DateUnit.SECOND.getMillis());
   }

   public Tailer(File file, Charset charset, LineHandler lineHandler) {
      this(file, charset, lineHandler, 0, DateUnit.SECOND.getMillis());
   }

   public Tailer(File file, Charset charset, LineHandler lineHandler, int initReadLine, long period) {
      checkFile(file);
      this.charset = charset;
      this.lineHandler = lineHandler;
      this.period = period;
      this.initReadLine = initReadLine;
      this.randomAccessFile = FileUtil.createRandomAccessFile(file, FileMode.r);
      this.executorService = Executors.newSingleThreadScheduledExecutor();
   }

   public void start() {
      this.start(false);
   }

   public void start(boolean async) {
      try {
         this.readTail();
      } catch (IOException var7) {
         throw new IORuntimeException(var7);
      }

      LineReadWatcher lineReadWatcher = new LineReadWatcher(this.randomAccessFile, this.charset, this.lineHandler);
      ScheduledFuture<?> scheduledFuture = this.executorService.scheduleAtFixedRate(lineReadWatcher, 0L, this.period, TimeUnit.MILLISECONDS);
      if (!async) {
         try {
            scheduledFuture.get();
         } catch (ExecutionException var5) {
            throw new UtilException(var5);
         } catch (InterruptedException var6) {
         }
      }

   }

   public void stop() {
      this.executorService.shutdown();
   }

   private void readTail() throws IOException {
      long len = this.randomAccessFile.length();
      if (this.initReadLine > 0) {
         Stack<String> stack = new Stack();
         long start = this.randomAccessFile.getFilePointer();
         long nextEnd = len - 1L < 0L ? 0L : len - 1L;
         this.randomAccessFile.seek(nextEnd);
         int currentLine = 0;

         while(nextEnd > start && currentLine <= this.initReadLine) {
            int c = this.randomAccessFile.read();
            String line;
            if (c == 10 || c == 13) {
               line = FileUtil.readLine(this.randomAccessFile, this.charset);
               if (null != line) {
                  stack.push(line);
               }

               ++currentLine;
               --nextEnd;
            }

            --nextEnd;
            this.randomAccessFile.seek(nextEnd);
            if (nextEnd == 0L) {
               line = FileUtil.readLine(this.randomAccessFile, this.charset);
               if (null != line) {
                  stack.push(line);
               }
               break;
            }
         }

         while(!stack.isEmpty()) {
            this.lineHandler.handle((String)stack.pop());
         }
      }

      try {
         this.randomAccessFile.seek(len);
      } catch (IOException var11) {
         throw new IORuntimeException(var11);
      }
   }

   private static void checkFile(File file) {
      if (!file.exists()) {
         throw new UtilException("File [{}] not exist !", new Object[]{file.getAbsolutePath()});
      } else if (!file.isFile()) {
         throw new UtilException("Path [{}] is not a file !", new Object[]{file.getAbsolutePath()});
      }
   }

   public static class ConsoleLineHandler implements LineHandler {
      public void handle(String line) {
         Console.log(line);
      }
   }
}
