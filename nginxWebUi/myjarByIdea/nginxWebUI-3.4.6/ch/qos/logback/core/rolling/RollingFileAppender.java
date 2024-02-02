package ch.qos.logback.core.rolling;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.util.ContextUtil;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class RollingFileAppender<E> extends FileAppender<E> {
   File currentlyActiveFile;
   TriggeringPolicy<E> triggeringPolicy;
   RollingPolicy rollingPolicy;
   private static String RFA_NO_TP_URL = "http://logback.qos.ch/codes.html#rfa_no_tp";
   private static String RFA_NO_RP_URL = "http://logback.qos.ch/codes.html#rfa_no_rp";
   private static String COLLISION_URL = "http://logback.qos.ch/codes.html#rfa_collision";
   private static String RFA_LATE_FILE_URL = "http://logback.qos.ch/codes.html#rfa_file_after";

   public void start() {
      if (this.triggeringPolicy == null) {
         this.addWarn("No TriggeringPolicy was set for the RollingFileAppender named " + this.getName());
         this.addWarn("For more information, please visit " + RFA_NO_TP_URL);
      } else if (!this.triggeringPolicy.isStarted()) {
         this.addWarn("TriggeringPolicy has not started. RollingFileAppender will not start");
      } else if (this.checkForCollisionsInPreviousRollingFileAppenders()) {
         this.addError("Collisions detected with FileAppender/RollingAppender instances defined earlier. Aborting.");
         this.addError("For more information, please visit " + COLLISION_WITH_EARLIER_APPENDER_URL);
      } else {
         if (!this.append) {
            this.addWarn("Append mode is mandatory for RollingFileAppender. Defaulting to append=true.");
            this.append = true;
         }

         if (this.rollingPolicy == null) {
            this.addError("No RollingPolicy was set for the RollingFileAppender named " + this.getName());
            this.addError("For more information, please visit " + RFA_NO_RP_URL);
         } else if (this.checkForFileAndPatternCollisions()) {
            this.addError("File property collides with fileNamePattern. Aborting.");
            this.addError("For more information, please visit " + COLLISION_URL);
         } else {
            if (this.isPrudent()) {
               if (this.rawFileProperty() != null) {
                  this.addWarn("Setting \"File\" property to null on account of prudent mode");
                  this.setFile((String)null);
               }

               if (this.rollingPolicy.getCompressionMode() != CompressionMode.NONE) {
                  this.addError("Compression is not supported in prudent mode. Aborting");
                  return;
               }
            }

            this.currentlyActiveFile = new File(this.getFile());
            this.addInfo("Active log file name: " + this.getFile());
            super.start();
         }
      }
   }

   private boolean checkForFileAndPatternCollisions() {
      if (this.triggeringPolicy instanceof RollingPolicyBase) {
         RollingPolicyBase base = (RollingPolicyBase)this.triggeringPolicy;
         FileNamePattern fileNamePattern = base.fileNamePattern;
         if (fileNamePattern != null && this.fileName != null) {
            String regex = fileNamePattern.toRegex();
            return this.fileName.matches(regex);
         }
      }

      return false;
   }

   private boolean checkForCollisionsInPreviousRollingFileAppenders() {
      boolean collisionResult = false;
      if (this.triggeringPolicy instanceof RollingPolicyBase) {
         RollingPolicyBase base = (RollingPolicyBase)this.triggeringPolicy;
         FileNamePattern fileNamePattern = base.fileNamePattern;
         boolean collisionsDetected = this.innerCheckForFileNamePatternCollisionInPreviousRFA(fileNamePattern);
         if (collisionsDetected) {
            collisionResult = true;
         }
      }

      return collisionResult;
   }

   private boolean innerCheckForFileNamePatternCollisionInPreviousRFA(FileNamePattern fileNamePattern) {
      boolean collisionsDetected = false;
      Map<String, FileNamePattern> map = (Map)this.context.getObject("RFA_FILENAME_PATTERN_COLLISION_MAP");
      if (map == null) {
         return collisionsDetected;
      } else {
         Iterator var4 = map.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<String, FileNamePattern> entry = (Map.Entry)var4.next();
            if (fileNamePattern.equals(entry.getValue())) {
               this.addErrorForCollision("FileNamePattern", ((FileNamePattern)entry.getValue()).toString(), (String)entry.getKey());
               collisionsDetected = true;
            }
         }

         if (this.name != null) {
            map.put(this.getName(), fileNamePattern);
         }

         return collisionsDetected;
      }
   }

   public void stop() {
      super.stop();
      if (this.rollingPolicy != null) {
         this.rollingPolicy.stop();
      }

      if (this.triggeringPolicy != null) {
         this.triggeringPolicy.stop();
      }

      Map<String, FileNamePattern> map = ContextUtil.getFilenamePatternCollisionMap(this.context);
      if (map != null && this.getName() != null) {
         map.remove(this.getName());
      }

   }

   public void setFile(String file) {
      if (file != null && (this.triggeringPolicy != null || this.rollingPolicy != null)) {
         this.addError("File property must be set before any triggeringPolicy or rollingPolicy properties");
         this.addError("For more information, please visit " + RFA_LATE_FILE_URL);
      }

      super.setFile(file);
   }

   public String getFile() {
      return this.rollingPolicy.getActiveFileName();
   }

   public void rollover() {
      this.lock.lock();

      try {
         this.closeOutputStream();
         this.attemptRollover();
         this.attemptOpenFile();
      } finally {
         this.lock.unlock();
      }

   }

   private void attemptOpenFile() {
      try {
         this.currentlyActiveFile = new File(this.rollingPolicy.getActiveFileName());
         this.openFile(this.rollingPolicy.getActiveFileName());
      } catch (IOException var2) {
         this.addError("setFile(" + this.fileName + ", false) call failed.", var2);
      }

   }

   private void attemptRollover() {
      try {
         this.rollingPolicy.rollover();
      } catch (RolloverFailure var2) {
         this.addWarn("RolloverFailure occurred. Deferring roll-over.");
         this.append = true;
      }

   }

   protected void subAppend(E event) {
      synchronized(this.triggeringPolicy) {
         if (this.triggeringPolicy.isTriggeringEvent(this.currentlyActiveFile, event)) {
            this.rollover();
         }
      }

      super.subAppend(event);
   }

   public RollingPolicy getRollingPolicy() {
      return this.rollingPolicy;
   }

   public TriggeringPolicy<E> getTriggeringPolicy() {
      return this.triggeringPolicy;
   }

   public void setRollingPolicy(RollingPolicy policy) {
      this.rollingPolicy = policy;
      if (this.rollingPolicy instanceof TriggeringPolicy) {
         this.triggeringPolicy = (TriggeringPolicy)policy;
      }

   }

   public void setTriggeringPolicy(TriggeringPolicy<E> policy) {
      this.triggeringPolicy = policy;
      if (policy instanceof RollingPolicy) {
         this.rollingPolicy = (RollingPolicy)policy;
      }

   }
}
