/*     */ package com.cym.utils;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
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
/*     */ public class FilePermissionUtil
/*     */ {
/*     */   public static Boolean canRead(File file) {
/*  23 */     if (file.isDirectory())
/*     */       try {
/*  25 */         File[] listFiles = file.listFiles();
/*  26 */         if (listFiles == null) {
/*  27 */           return Boolean.valueOf(false);
/*     */         }
/*  29 */         return Boolean.valueOf(true);
/*     */       }
/*  31 */       catch (Exception e) {
/*  32 */         return Boolean.valueOf(false);
/*     */       }  
/*  34 */     if (!file.exists()) {
/*  35 */       return Boolean.valueOf(false);
/*     */     }
/*  37 */     return Boolean.valueOf(checkRead(file));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean checkRead(File file) {
/*  46 */     FileReader fd = null;
/*     */     try {
/*  48 */       fd = new FileReader(file);
/*  49 */       if (fd.read() != -1);
/*     */ 
/*     */       
/*  52 */       return true;
/*  53 */     } catch (IOException e) {
/*  54 */       return false;
/*     */     } finally {
/*     */       try {
/*  57 */         fd.close();
/*  58 */       } catch (IOException e) {
/*  59 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Boolean canWrite(File file) {
/*  70 */     if (file.isDirectory()) {
/*     */       try {
/*  72 */         file = new File(file, "canWriteTestDeleteOnExit.temp");
/*  73 */         if (file.exists()) {
/*  74 */           boolean checkWrite = checkWrite(file);
/*  75 */           if (!deleteFile(file)) {
/*  76 */             file.deleteOnExit();
/*     */           }
/*  78 */           return Boolean.valueOf(checkWrite);
/*  79 */         }  if (file.createNewFile()) {
/*  80 */           if (!deleteFile(file)) {
/*  81 */             file.deleteOnExit();
/*     */           }
/*  83 */           return Boolean.valueOf(true);
/*     */         } 
/*  85 */         return Boolean.valueOf(false);
/*     */       }
/*  87 */       catch (Exception e) {
/*  88 */         return Boolean.valueOf(false);
/*     */       } 
/*     */     }
/*  91 */     return Boolean.valueOf(checkWrite(file));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean checkWrite(File file) {
/* 100 */     FileWriter fw = null;
/* 101 */     boolean delete = !file.exists();
/* 102 */     boolean result = false;
/*     */     try {
/* 104 */       fw = new FileWriter(file, true);
/* 105 */       fw.write("");
/* 106 */       fw.flush();
/* 107 */       result = true;
/* 108 */       return result;
/* 109 */     } catch (IOException e) {
/* 110 */       return false;
/*     */     } finally {
/*     */       try {
/* 113 */         fw.close();
/* 114 */       } catch (IOException e) {
/* 115 */         e.printStackTrace();
/*     */       } 
/* 117 */       if (delete && result) {
/* 118 */         deleteFile(file);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean deleteFile(File file) {
/* 129 */     return deleteFile(file, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean deleteFile(File file, boolean delDir) {
/* 139 */     if (!file.exists()) {
/* 140 */       return true;
/*     */     }
/* 142 */     if (file.isFile()) {
/* 143 */       return file.delete();
/*     */     }
/* 145 */     boolean result = true;
/* 146 */     File[] children = file.listFiles();
/* 147 */     for (int i = 0; i < children.length; i++) {
/* 148 */       result = deleteFile(children[i], delDir);
/* 149 */       if (!result) {
/* 150 */         return false;
/*     */       }
/*     */     } 
/* 153 */     if (delDir) {
/* 154 */       result = file.delete();
/*     */     }
/* 156 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\FilePermissionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */