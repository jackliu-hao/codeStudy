package ch.qos.logback.core;

import ch.qos.logback.core.recovery.ResilientFileOutputStream;
import ch.qos.logback.core.util.ContextUtil;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Iterator;
import java.util.Map;

public class FileAppender<E> extends OutputStreamAppender<E> {
   public static final long DEFAULT_BUFFER_SIZE = 8192L;
   protected static String COLLISION_WITH_EARLIER_APPENDER_URL = "http://logback.qos.ch/codes.html#earlier_fa_collision";
   protected boolean append = true;
   protected String fileName = null;
   private boolean prudent = false;
   private FileSize bufferSize = new FileSize(8192L);

   public void setFile(String file) {
      if (file == null) {
         this.fileName = file;
      } else {
         this.fileName = file.trim();
      }

   }

   public boolean isAppend() {
      return this.append;
   }

   public final String rawFileProperty() {
      return this.fileName;
   }

   public String getFile() {
      return this.fileName;
   }

   public void start() {
      int errors = 0;
      if (this.getFile() != null) {
         this.addInfo("File property is set to [" + this.fileName + "]");
         if (this.prudent && !this.isAppend()) {
            this.setAppend(true);
            this.addWarn("Setting \"Append\" property to true on account of \"Prudent\" mode");
         }

         if (this.checkForFileCollisionInPreviousFileAppenders()) {
            this.addError("Collisions detected with FileAppender/RollingAppender instances defined earlier. Aborting.");
            this.addError("For more information, please visit " + COLLISION_WITH_EARLIER_APPENDER_URL);
            ++errors;
         } else {
            try {
               this.openFile(this.getFile());
            } catch (IOException var3) {
               ++errors;
               this.addError("openFile(" + this.fileName + "," + this.append + ") call failed.", var3);
            }
         }
      } else {
         ++errors;
         this.addError("\"File\" property not set for appender named [" + this.name + "].");
      }

      if (errors == 0) {
         super.start();
      }

   }

   public void stop() {
      super.stop();
      Map<String, String> map = ContextUtil.getFilenameCollisionMap(this.context);
      if (map != null && this.getName() != null) {
         map.remove(this.getName());
      }
   }

   protected boolean checkForFileCollisionInPreviousFileAppenders() {
      boolean collisionsDetected = false;
      if (this.fileName == null) {
         return false;
      } else {
         Map<String, String> map = (Map)this.context.getObject("FA_FILENAME_COLLISION_MAP");
         if (map == null) {
            return collisionsDetected;
         } else {
            Iterator var3 = map.entrySet().iterator();

            while(var3.hasNext()) {
               Map.Entry<String, String> entry = (Map.Entry)var3.next();
               if (this.fileName.equals(entry.getValue())) {
                  this.addErrorForCollision("File", (String)entry.getValue(), (String)entry.getKey());
                  collisionsDetected = true;
               }
            }

            if (this.name != null) {
               map.put(this.getName(), this.fileName);
            }

            return collisionsDetected;
         }
      }
   }

   protected void addErrorForCollision(String optionName, String optionValue, String appenderName) {
      this.addError("'" + optionName + "' option has the same value \"" + optionValue + "\" as that given for appender [" + appenderName + "] defined earlier.");
   }

   public void openFile(String file_name) throws IOException {
      this.lock.lock();

      try {
         File file = new File(file_name);
         boolean result = FileUtil.createMissingParentDirectories(file);
         if (!result) {
            this.addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");
         }

         ResilientFileOutputStream resilientFos = new ResilientFileOutputStream(file, this.append, this.bufferSize.getSize());
         resilientFos.setContext(this.context);
         this.setOutputStream(resilientFos);
      } finally {
         this.lock.unlock();
      }

   }

   public boolean isPrudent() {
      return this.prudent;
   }

   public void setPrudent(boolean prudent) {
      this.prudent = prudent;
   }

   public void setAppend(boolean append) {
      this.append = append;
   }

   public void setBufferSize(FileSize bufferSize) {
      this.addInfo("Setting bufferSize to [" + bufferSize.toString() + "]");
      this.bufferSize = bufferSize;
   }

   private void safeWrite(E event) throws IOException {
      ResilientFileOutputStream resilientFOS = (ResilientFileOutputStream)this.getOutputStream();
      FileChannel fileChannel = resilientFOS.getChannel();
      if (fileChannel != null) {
         boolean interrupted = Thread.interrupted();
         FileLock fileLock = null;

         try {
            fileLock = fileChannel.lock();
            long position = fileChannel.position();
            long size = fileChannel.size();
            if (size != position) {
               fileChannel.position(size);
            }

            super.writeOut(event);
         } catch (IOException var13) {
            resilientFOS.postIOFailure(var13);
         } finally {
            if (fileLock != null && fileLock.isValid()) {
               fileLock.release();
            }

            if (interrupted) {
               Thread.currentThread().interrupt();
            }

         }

      }
   }

   protected void writeOut(E event) throws IOException {
      if (this.prudent) {
         this.safeWrite(event);
      } else {
         super.writeOut(event);
      }

   }
}
