package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.EnvUtil;
import ch.qos.logback.core.util.FileUtil;
import java.io.File;

public class RenameUtil extends ContextAwareBase {
   static String RENAMING_ERROR_URL = "http://logback.qos.ch/codes.html#renamingError";

   public void rename(String src, String target) throws RolloverFailure {
      if (src.equals(target)) {
         this.addWarn("Source and target files are the same [" + src + "]. Skipping.");
      } else {
         File srcFile = new File(src);
         if (srcFile.exists()) {
            File targetFile = new File(target);
            this.createMissingTargetDirsIfNecessary(targetFile);
            this.addInfo("Renaming file [" + srcFile + "] to [" + targetFile + "]");
            boolean result = srcFile.renameTo(targetFile);
            if (!result) {
               this.addWarn("Failed to rename file [" + srcFile + "] as [" + targetFile + "].");
               Boolean areOnDifferentVolumes = this.areOnDifferentVolumes(srcFile, targetFile);
               if (Boolean.TRUE.equals(areOnDifferentVolumes)) {
                  this.addWarn("Detected different file systems for source [" + src + "] and target [" + target + "]. Attempting rename by copying.");
                  this.renameByCopying(src, target);
                  return;
               }

               this.addWarn("Please consider leaving the [file] option of " + RollingFileAppender.class.getSimpleName() + " empty.");
               this.addWarn("See also " + RENAMING_ERROR_URL);
            }

         } else {
            throw new RolloverFailure("File [" + src + "] does not exist.");
         }
      }
   }

   Boolean areOnDifferentVolumes(File srcFile, File targetFile) throws RolloverFailure {
      if (!EnvUtil.isJDK7OrHigher()) {
         return false;
      } else {
         File parentOfTarget = targetFile.getAbsoluteFile().getParentFile();
         if (parentOfTarget == null) {
            this.addWarn("Parent of target file [" + targetFile + "] is null");
            return null;
         } else if (!parentOfTarget.exists()) {
            this.addWarn("Parent of target file [" + targetFile + "] does not exist");
            return null;
         } else {
            try {
               boolean onSameFileStore = FileStoreUtil.areOnSameFileStore(srcFile, parentOfTarget);
               return !onSameFileStore;
            } catch (RolloverFailure var5) {
               this.addWarn("Error while checking file store equality", var5);
               return null;
            }
         }
      }
   }

   public void renameByCopying(String src, String target) throws RolloverFailure {
      FileUtil fileUtil = new FileUtil(this.getContext());
      fileUtil.copy(src, target);
      File srcFile = new File(src);
      if (!srcFile.delete()) {
         this.addWarn("Could not delete " + src);
      }

   }

   void createMissingTargetDirsIfNecessary(File toFile) throws RolloverFailure {
      boolean result = FileUtil.createMissingParentDirectories(toFile);
      if (!result) {
         throw new RolloverFailure("Failed to create parent directories for [" + toFile.getAbsolutePath() + "]");
      }
   }

   public String toString() {
      return "c.q.l.co.rolling.helper.RenameUtil";
   }
}
