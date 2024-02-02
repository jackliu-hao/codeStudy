package org.h2.message;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.h2.api.ErrorCode;
import org.h2.jdbc.JdbcException;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;

public class TraceSystem implements TraceWriter {
   public static final int PARENT = -1;
   public static final int OFF = 0;
   public static final int ERROR = 1;
   public static final int INFO = 2;
   public static final int DEBUG = 3;
   public static final int ADAPTER = 4;
   public static final int DEFAULT_TRACE_LEVEL_SYSTEM_OUT = 0;
   public static final int DEFAULT_TRACE_LEVEL_FILE = 1;
   private static final int DEFAULT_MAX_FILE_SIZE = 67108864;
   private static final int CHECK_SIZE_EACH_WRITES = 4096;
   private int levelSystemOut = 0;
   private int levelFile = 1;
   private int levelMax;
   private int maxFileSize = 67108864;
   private String fileName;
   private final AtomicReferenceArray<Trace> traces;
   private SimpleDateFormat dateFormat;
   private Writer fileWriter;
   private PrintWriter printWriter;
   private int checkSize;
   private boolean closed;
   private boolean writingErrorLogged;
   private TraceWriter writer;
   private PrintStream sysOut;

   public TraceSystem(String var1) {
      this.traces = new AtomicReferenceArray(Trace.MODULE_NAMES.length);
      this.checkSize = -1;
      this.writer = this;
      this.sysOut = System.out;
      this.fileName = var1;
      this.updateLevel();
   }

   private void updateLevel() {
      this.levelMax = Math.max(this.levelSystemOut, this.levelFile);
   }

   public void setSysOut(PrintStream var1) {
      this.sysOut = var1;
   }

   public Trace getTrace(int var1) {
      Trace var2 = (Trace)this.traces.get(var1);
      if (var2 == null) {
         var2 = new Trace(this.writer, var1);
         if (!this.traces.compareAndSet(var1, (Object)null, var2)) {
            var2 = (Trace)this.traces.get(var1);
         }
      }

      return var2;
   }

   public Trace getTrace(String var1) {
      return new Trace(this.writer, var1);
   }

   public boolean isEnabled(int var1) {
      if (this.levelMax == 4) {
         return this.writer.isEnabled(var1);
      } else {
         return var1 <= this.levelMax;
      }
   }

   public void setFileName(String var1) {
      this.fileName = var1;
   }

   public void setMaxFileSize(int var1) {
      this.maxFileSize = var1;
   }

   public void setLevelSystemOut(int var1) {
      this.levelSystemOut = var1;
      this.updateLevel();
   }

   public void setLevelFile(int var1) {
      if (var1 == 4) {
         String var2 = "org.h2.message.TraceWriterAdapter";

         try {
            this.writer = (TraceWriter)Class.forName(var2).getDeclaredConstructor().newInstance();
         } catch (Throwable var5) {
            DbException var3 = DbException.get(90086, var5, var2);
            this.write(1, 2, var2, var3);
            return;
         }

         String var6 = this.fileName;
         if (var6 != null) {
            if (var6.endsWith(".trace.db")) {
               var6 = var6.substring(0, var6.length() - ".trace.db".length());
            }

            int var4 = Math.max(var6.lastIndexOf(47), var6.lastIndexOf(92));
            if (var4 >= 0) {
               var6 = var6.substring(var4 + 1);
            }

            this.writer.setName(var6);
         }
      }

      this.levelFile = var1;
      this.updateLevel();
   }

   public int getLevelFile() {
      return this.levelFile;
   }

   private synchronized String format(String var1, String var2) {
      if (this.dateFormat == null) {
         this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
      }

      return this.dateFormat.format(System.currentTimeMillis()) + var1 + ": " + var2;
   }

   public void write(int var1, int var2, String var3, Throwable var4) {
      this.write(var1, Trace.MODULE_NAMES[var2], var3, var4);
   }

   public void write(int var1, String var2, String var3, Throwable var4) {
      if (var1 <= this.levelSystemOut || var1 > this.levelMax) {
         this.sysOut.println(this.format(var2, var3));
         if (var4 != null && this.levelSystemOut == 3) {
            var4.printStackTrace(this.sysOut);
         }
      }

      if (this.fileName != null && var1 <= this.levelFile) {
         this.writeFile(this.format(var2, var3), var4);
      }

   }

   private synchronized void writeFile(String var1, Throwable var2) {
      try {
         this.checkSize = (this.checkSize + 1) % 4096;
         if (this.checkSize == 0) {
            this.closeWriter();
            if (this.maxFileSize > 0 && FileUtils.size(this.fileName) > (long)this.maxFileSize) {
               String var3 = this.fileName + ".old";
               FileUtils.delete(var3);
               FileUtils.move(this.fileName, var3);
            }
         }

         if (!this.openWriter()) {
            return;
         }

         this.printWriter.println(var1);
         if (var2 != null) {
            if (this.levelFile == 1 && var2 instanceof JdbcException) {
               JdbcException var6 = (JdbcException)var2;
               int var4 = var6.getErrorCode();
               if (ErrorCode.isCommon(var4)) {
                  this.printWriter.println(var2);
               } else {
                  var2.printStackTrace(this.printWriter);
               }
            } else {
               var2.printStackTrace(this.printWriter);
            }
         }

         this.printWriter.flush();
         if (this.closed) {
            this.closeWriter();
         }
      } catch (Exception var5) {
         this.logWritingError(var5);
      }

   }

   private void logWritingError(Exception var1) {
      if (!this.writingErrorLogged) {
         this.writingErrorLogged = true;
         DbException var2 = DbException.get(90034, var1, this.fileName, var1.toString());
         this.fileName = null;
         this.sysOut.println(var2);
         var2.printStackTrace();
      }
   }

   private boolean openWriter() {
      if (this.printWriter == null) {
         try {
            FileUtils.createDirectories(FileUtils.getParent(this.fileName));
            if (FileUtils.exists(this.fileName) && !FileUtils.canWrite(this.fileName)) {
               return false;
            }

            this.fileWriter = IOUtils.getBufferedWriter(FileUtils.newOutputStream(this.fileName, true));
            this.printWriter = new PrintWriter(this.fileWriter, true);
         } catch (Exception var2) {
            this.logWritingError(var2);
            return false;
         }
      }

      return true;
   }

   private synchronized void closeWriter() {
      if (this.printWriter != null) {
         this.printWriter.flush();
         this.printWriter.close();
         this.printWriter = null;
      }

      if (this.fileWriter != null) {
         try {
            this.fileWriter.close();
         } catch (IOException var2) {
         }

         this.fileWriter = null;
      }

   }

   public void close() {
      this.closeWriter();
      this.closed = true;
   }

   public void setName(String var1) {
   }
}
