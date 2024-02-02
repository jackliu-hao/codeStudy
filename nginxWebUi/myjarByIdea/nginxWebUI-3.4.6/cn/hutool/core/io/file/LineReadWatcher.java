package cn.hutool.core.io.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.LineHandler;
import cn.hutool.core.io.watch.SimpleWatcher;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

public class LineReadWatcher extends SimpleWatcher implements Runnable {
   private final RandomAccessFile randomAccessFile;
   private final Charset charset;
   private final LineHandler lineHandler;

   public LineReadWatcher(RandomAccessFile randomAccessFile, Charset charset, LineHandler lineHandler) {
      this.randomAccessFile = randomAccessFile;
      this.charset = charset;
      this.lineHandler = lineHandler;
   }

   public void run() {
      this.onModify((WatchEvent)null, (Path)null);
   }

   public void onModify(WatchEvent<?> event, Path currentPath) {
      RandomAccessFile randomAccessFile = this.randomAccessFile;
      Charset charset = this.charset;
      LineHandler lineHandler = this.lineHandler;

      try {
         long currentLength = randomAccessFile.length();
         long position = randomAccessFile.getFilePointer();
         if (position != currentLength) {
            if (currentLength < position) {
               randomAccessFile.seek(currentLength);
            } else {
               FileUtil.readLines(randomAccessFile, charset, lineHandler);
               randomAccessFile.seek(currentLength);
            }
         }
      } catch (IOException var10) {
         throw new IORuntimeException(var10);
      }
   }
}
