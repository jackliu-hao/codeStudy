package io.undertow.server.handlers.accesslog;

import io.undertow.UndertowLogger;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class DefaultAccessLogReceiver implements AccessLogReceiver, Runnable, Closeable {
   private static final String DEFAULT_LOG_SUFFIX = "log";
   private final Executor logWriteExecutor;
   private final Deque<String> pendingMessages;
   private volatile int state;
   private static final AtomicIntegerFieldUpdater<DefaultAccessLogReceiver> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(DefaultAccessLogReceiver.class, "state");
   private long changeOverPoint;
   private String currentDateString;
   private boolean forceLogRotation;
   private final Path outputDirectory;
   private final Path defaultLogFile;
   private final String logBaseName;
   private final String logNameSuffix;
   private BufferedWriter writer;
   private volatile boolean closed;
   private boolean initialRun;
   private final boolean rotate;
   private final LogFileHeaderGenerator fileHeaderGenerator;

   public DefaultAccessLogReceiver(Executor logWriteExecutor, File outputDirectory, String logBaseName) {
      this(logWriteExecutor, (Path)outputDirectory.toPath(), logBaseName, (String)null);
   }

   public DefaultAccessLogReceiver(Executor logWriteExecutor, File outputDirectory, String logBaseName, String logNameSuffix) {
      this(logWriteExecutor, outputDirectory.toPath(), logBaseName, logNameSuffix, true);
   }

   public DefaultAccessLogReceiver(Executor logWriteExecutor, File outputDirectory, String logBaseName, String logNameSuffix, boolean rotate) {
      this(logWriteExecutor, outputDirectory.toPath(), logBaseName, logNameSuffix, rotate);
   }

   public DefaultAccessLogReceiver(Executor logWriteExecutor, Path outputDirectory, String logBaseName) {
      this(logWriteExecutor, (Path)outputDirectory, logBaseName, (String)null);
   }

   public DefaultAccessLogReceiver(Executor logWriteExecutor, Path outputDirectory, String logBaseName, String logNameSuffix) {
      this(logWriteExecutor, outputDirectory, logBaseName, logNameSuffix, true);
   }

   public DefaultAccessLogReceiver(Executor logWriteExecutor, Path outputDirectory, String logBaseName, String logNameSuffix, boolean rotate) {
      this(logWriteExecutor, outputDirectory, logBaseName, logNameSuffix, rotate, (LogFileHeaderGenerator)null);
   }

   private DefaultAccessLogReceiver(Executor logWriteExecutor, Path outputDirectory, String logBaseName, String logNameSuffix, boolean rotate, LogFileHeaderGenerator fileHeader) {
      this.state = 0;
      this.writer = null;
      this.closed = false;
      this.initialRun = true;
      this.logWriteExecutor = logWriteExecutor;
      this.outputDirectory = outputDirectory;
      this.logBaseName = logBaseName;
      this.rotate = rotate;
      this.fileHeaderGenerator = fileHeader;
      this.logNameSuffix = logNameSuffix != null ? logNameSuffix : "log";
      this.pendingMessages = new ConcurrentLinkedDeque();
      this.defaultLogFile = outputDirectory.resolve(logBaseName + this.logNameSuffix);
      this.calculateChangeOverPoint();
   }

   private void calculateChangeOverPoint() {
      Calendar calendar = Calendar.getInstance();
      calendar.set(13, 0);
      calendar.set(12, 0);
      calendar.set(11, 0);
      calendar.add(5, 1);
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
      this.currentDateString = df.format(new Date());
      if (Files.exists(this.defaultLogFile, new LinkOption[0])) {
         try {
            this.currentDateString = df.format(new Date(Files.getLastModifiedTime(this.defaultLogFile).toMillis()));
         } catch (IOException var4) {
         }
      }

      this.changeOverPoint = calendar.getTimeInMillis();
   }

   public void logMessage(String message) {
      this.pendingMessages.add(message);
      int state = stateUpdater.get(this);
      if (state == 0 && stateUpdater.compareAndSet(this, 0, 1)) {
         this.logWriteExecutor.execute(this);
      }

   }

   public void run() {
      if (stateUpdater.compareAndSet(this, 1, 2)) {
         if (this.forceLogRotation) {
            this.doRotate();
         } else if (this.initialRun && Files.exists(this.defaultLogFile, new LinkOption[0])) {
            long lm = 0L;

            try {
               lm = Files.getLastModifiedTime(this.defaultLogFile).toMillis();
            } catch (IOException var28) {
               UndertowLogger.ROOT_LOGGER.errorRotatingAccessLog(var28);
            }

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(this.changeOverPoint);
            c.add(5, -1);
            if (lm <= c.getTimeInMillis()) {
               this.doRotate();
            }
         }

         this.initialRun = false;
         List<String> messages = new ArrayList();

         for(int i = 0; i < 1000; ++i) {
            String msg = (String)this.pendingMessages.poll();
            if (msg == null) {
               break;
            }

            messages.add(msg);
         }

         try {
            if (!messages.isEmpty()) {
               this.writeMessage(messages);
            }
         } finally {
            stateUpdater.set(this, 3);
            if ((!this.pendingMessages.isEmpty() || this.forceLogRotation) && stateUpdater.compareAndSet(this, 3, 1)) {
               this.logWriteExecutor.execute(this);
            }

            if (stateUpdater.compareAndSet(this, 3, 0) && this.closed && stateUpdater.compareAndSet(this, 0, 3)) {
               try {
                  if (this.writer != null) {
                     this.writer.flush();
                     this.writer.close();
                     this.writer = null;
                  }
               } catch (IOException var26) {
                  UndertowLogger.ROOT_LOGGER.errorWritingAccessLog(var26);
               } finally {
                  stateUpdater.set(this, 0);
               }
            }

         }

      }
   }

   void awaitWrittenForTest() throws InterruptedException {
      while(!this.pendingMessages.isEmpty() || this.forceLogRotation) {
         Thread.sleep(10L);
      }

      while(this.state != 0) {
         Thread.sleep(10L);
      }

   }

   private void writeMessage(List<String> messages) {
      if (System.currentTimeMillis() > this.changeOverPoint) {
         this.doRotate();
      }

      try {
         if (this.writer == null) {
            this.writer = Files.newBufferedWriter(this.defaultLogFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            if (Files.size(this.defaultLogFile) == 0L && this.fileHeaderGenerator != null) {
               String header = this.fileHeaderGenerator.generateHeader();
               if (header != null) {
                  this.writer.write(header);
                  this.writer.newLine();
                  this.writer.flush();
               }
            }
         }

         Iterator var5 = messages.iterator();

         while(var5.hasNext()) {
            String message = (String)var5.next();
            this.writer.write(message);
            this.writer.newLine();
         }

         this.writer.flush();
      } catch (IOException var4) {
         UndertowLogger.ROOT_LOGGER.errorWritingAccessLog(var4);
      }

   }

   private void doRotate() {
      this.forceLogRotation = false;
      if (this.rotate) {
         try {
            if (this.writer != null) {
               this.writer.flush();
               this.writer.close();
               this.writer = null;
            }

            if (!Files.exists(this.defaultLogFile, new LinkOption[0])) {
               return;
            }

            Path newFile = this.outputDirectory.resolve(this.logBaseName + this.currentDateString + "." + this.logNameSuffix);

            for(int count = 0; Files.exists(newFile, new LinkOption[0]); newFile = this.outputDirectory.resolve(this.logBaseName + this.currentDateString + "-" + count + "." + this.logNameSuffix)) {
               ++count;
            }

            Files.move(this.defaultLogFile, newFile);
         } catch (IOException var6) {
            UndertowLogger.ROOT_LOGGER.errorRotatingAccessLog(var6);
         } finally {
            this.calculateChangeOverPoint();
         }

      }
   }

   public void rotate() {
      this.forceLogRotation = true;
      if (stateUpdater.compareAndSet(this, 0, 1)) {
         this.logWriteExecutor.execute(this);
      }

   }

   public void close() throws IOException {
      this.closed = true;
      if (stateUpdater.compareAndSet(this, 0, 1)) {
         this.logWriteExecutor.execute(this);
      }

   }

   public static Builder builder() {
      return new Builder();
   }

   // $FF: synthetic method
   DefaultAccessLogReceiver(Executor x0, Path x1, String x2, String x3, boolean x4, LogFileHeaderGenerator x5, Object x6) {
      this(x0, x1, x2, x3, x4, x5);
   }

   public static class Builder {
      private Executor logWriteExecutor;
      private Path outputDirectory;
      private String logBaseName;
      private String logNameSuffix;
      private boolean rotate;
      private LogFileHeaderGenerator logFileHeaderGenerator;

      public Executor getLogWriteExecutor() {
         return this.logWriteExecutor;
      }

      public Builder setLogWriteExecutor(Executor logWriteExecutor) {
         this.logWriteExecutor = logWriteExecutor;
         return this;
      }

      public Path getOutputDirectory() {
         return this.outputDirectory;
      }

      public Builder setOutputDirectory(Path outputDirectory) {
         this.outputDirectory = outputDirectory;
         return this;
      }

      public String getLogBaseName() {
         return this.logBaseName;
      }

      public Builder setLogBaseName(String logBaseName) {
         this.logBaseName = logBaseName;
         return this;
      }

      public String getLogNameSuffix() {
         return this.logNameSuffix;
      }

      public Builder setLogNameSuffix(String logNameSuffix) {
         this.logNameSuffix = logNameSuffix;
         return this;
      }

      public boolean isRotate() {
         return this.rotate;
      }

      public Builder setRotate(boolean rotate) {
         this.rotate = rotate;
         return this;
      }

      public LogFileHeaderGenerator getLogFileHeaderGenerator() {
         return this.logFileHeaderGenerator;
      }

      public Builder setLogFileHeaderGenerator(LogFileHeaderGenerator logFileHeaderGenerator) {
         this.logFileHeaderGenerator = logFileHeaderGenerator;
         return this;
      }

      public DefaultAccessLogReceiver build() {
         return new DefaultAccessLogReceiver(this.logWriteExecutor, this.outputDirectory, this.logBaseName, this.logNameSuffix, this.rotate, this.logFileHeaderGenerator);
      }
   }
}
