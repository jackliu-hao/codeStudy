/*     */ package ch.qos.logback.core.rolling.helper;
/*     */ 
/*     */ import ch.qos.logback.core.rolling.RollingFileAppender;
/*     */ import ch.qos.logback.core.rolling.RolloverFailure;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.util.EnvUtil;
/*     */ import ch.qos.logback.core.util.FileUtil;
/*     */ import java.io.File;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RenameUtil
/*     */   extends ContextAwareBase
/*     */ {
/*  32 */   static String RENAMING_ERROR_URL = "http://logback.qos.ch/codes.html#renamingError";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rename(String src, String target) throws RolloverFailure {
/*  44 */     if (src.equals(target)) {
/*  45 */       addWarn("Source and target files are the same [" + src + "]. Skipping.");
/*     */       return;
/*     */     } 
/*  48 */     File srcFile = new File(src);
/*     */     
/*  50 */     if (srcFile.exists()) {
/*  51 */       File targetFile = new File(target);
/*  52 */       createMissingTargetDirsIfNecessary(targetFile);
/*     */       
/*  54 */       addInfo("Renaming file [" + srcFile + "] to [" + targetFile + "]");
/*     */       
/*  56 */       boolean result = srcFile.renameTo(targetFile);
/*     */       
/*  58 */       if (!result) {
/*  59 */         addWarn("Failed to rename file [" + srcFile + "] as [" + targetFile + "].");
/*  60 */         Boolean areOnDifferentVolumes = areOnDifferentVolumes(srcFile, targetFile);
/*  61 */         if (Boolean.TRUE.equals(areOnDifferentVolumes)) {
/*  62 */           addWarn("Detected different file systems for source [" + src + "] and target [" + target + "]. Attempting rename by copying.");
/*  63 */           renameByCopying(src, target);
/*     */           return;
/*     */         } 
/*  66 */         addWarn("Please consider leaving the [file] option of " + RollingFileAppender.class.getSimpleName() + " empty.");
/*  67 */         addWarn("See also " + RENAMING_ERROR_URL);
/*     */       } 
/*     */     } else {
/*     */       
/*  71 */       throw new RolloverFailure("File [" + src + "] does not exist.");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Boolean areOnDifferentVolumes(File srcFile, File targetFile) throws RolloverFailure {
/*  86 */     if (!EnvUtil.isJDK7OrHigher()) {
/*  87 */       return Boolean.valueOf(false);
/*     */     }
/*     */     
/*  90 */     File parentOfTarget = targetFile.getAbsoluteFile().getParentFile();
/*     */     
/*  92 */     if (parentOfTarget == null) {
/*  93 */       addWarn("Parent of target file [" + targetFile + "] is null");
/*  94 */       return null;
/*     */     } 
/*  96 */     if (!parentOfTarget.exists()) {
/*  97 */       addWarn("Parent of target file [" + targetFile + "] does not exist");
/*  98 */       return null;
/*     */     } 
/*     */     
/*     */     try {
/* 102 */       boolean onSameFileStore = FileStoreUtil.areOnSameFileStore(srcFile, parentOfTarget);
/* 103 */       return Boolean.valueOf(!onSameFileStore);
/* 104 */     } catch (RolloverFailure rf) {
/* 105 */       addWarn("Error while checking file store equality", (Throwable)rf);
/* 106 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void renameByCopying(String src, String target) throws RolloverFailure {
/* 112 */     FileUtil fileUtil = new FileUtil(getContext());
/* 113 */     fileUtil.copy(src, target);
/*     */     
/* 115 */     File srcFile = new File(src);
/* 116 */     if (!srcFile.delete()) {
/* 117 */       addWarn("Could not delete " + src);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   void createMissingTargetDirsIfNecessary(File toFile) throws RolloverFailure {
/* 123 */     boolean result = FileUtil.createMissingParentDirectories(toFile);
/* 124 */     if (!result) {
/* 125 */       throw new RolloverFailure("Failed to create parent directories for [" + toFile.getAbsolutePath() + "]");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 131 */     return "c.q.l.co.rolling.helper.RenameUtil";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\rolling\helper\RenameUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */