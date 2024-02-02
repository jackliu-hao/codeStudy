/*     */ package com.sun.jna.platform;
/*     */ 
/*     */ import com.sun.jna.platform.mac.MacFileUtils;
/*     */ import com.sun.jna.platform.win32.W32FileUtils;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class FileUtils
/*     */ {
/*     */   public boolean hasTrash() {
/*  38 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract void moveToTrash(File... paramVarArgs) throws IOException;
/*     */ 
/*     */   
/*     */   private static class Holder
/*     */   {
/*     */     public static final FileUtils INSTANCE;
/*     */ 
/*     */     
/*     */     static {
/*  51 */       String os = System.getProperty("os.name");
/*  52 */       if (os.startsWith("Windows")) {
/*  53 */         INSTANCE = (FileUtils)new W32FileUtils();
/*     */       }
/*  55 */       else if (os.startsWith("Mac")) {
/*  56 */         INSTANCE = (FileUtils)new MacFileUtils();
/*     */       } else {
/*     */         
/*  59 */         INSTANCE = new FileUtils.DefaultFileUtils();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public static FileUtils getInstance() {
/*  65 */     return Holder.INSTANCE;
/*     */   }
/*     */   
/*     */   private static class DefaultFileUtils
/*     */     extends FileUtils {
/*     */     private DefaultFileUtils() {}
/*     */     
/*     */     private File getTrashDirectory() {
/*  73 */       File home = new File(System.getProperty("user.home"));
/*  74 */       File trash = new File(home, ".Trash");
/*  75 */       if (!trash.exists()) {
/*  76 */         trash = new File(home, "Trash");
/*  77 */         if (!trash.exists()) {
/*  78 */           File desktop = new File(home, "Desktop");
/*  79 */           if (desktop.exists()) {
/*  80 */             trash = new File(desktop, ".Trash");
/*  81 */             if (!trash.exists()) {
/*  82 */               trash = new File(desktop, "Trash");
/*  83 */               if (!trash.exists()) {
/*  84 */                 trash = new File(System.getProperty("fileutils.trash", "Trash"));
/*     */               }
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*  90 */       return trash;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasTrash() {
/*  95 */       return getTrashDirectory().exists();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void moveToTrash(File... files) throws IOException {
/* 103 */       File trash = getTrashDirectory();
/* 104 */       if (!trash.exists()) {
/* 105 */         throw new IOException("No trash location found (define fileutils.trash to be the path to the trash)");
/*     */       }
/* 107 */       List<File> failed = new ArrayList<File>();
/* 108 */       for (int i = 0; i < files.length; i++) {
/* 109 */         File src = files[i];
/* 110 */         File target = new File(trash, src.getName());
/* 111 */         if (!src.renameTo(target)) {
/* 112 */           failed.add(src);
/*     */         }
/*     */       } 
/* 115 */       if (failed.size() > 0)
/* 116 */         throw new IOException("The following files could not be trashed: " + failed); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\FileUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */