/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Date;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
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
/*     */ public class Expand
/*     */ {
/*     */   private File dest;
/*     */   private File source;
/*     */   private boolean overwrite = true;
/*     */   
/*     */   public void execute() throws Exception {
/*  93 */     expandFile(this.source, this.dest);
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
/*     */   protected void expandFile(File srcF, File dir) throws Exception {
/* 105 */     ZipInputStream zis = null;
/*     */ 
/*     */     
/*     */     try {
/* 109 */       zis = new ZipInputStream(new FileInputStream(srcF));
/* 110 */       ZipEntry ze = null;
/*     */       
/* 112 */       while ((ze = zis.getNextEntry()) != null)
/*     */       {
/* 114 */         extractFile(srcF, dir, zis, ze.getName(), new Date(ze.getTime()), ze.isDirectory());
/*     */ 
/*     */ 
/*     */       
/*     */       }
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 123 */     catch (IOException ioe) {
/*     */       
/* 125 */       throw new Exception("Error while expanding " + srcF.getPath(), ioe);
/*     */     }
/*     */     finally {
/*     */       
/* 129 */       if (zis != null) {
/*     */         
/*     */         try {
/*     */           
/* 133 */           zis.close();
/*     */         }
/* 135 */         catch (IOException e) {}
/*     */       }
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
/*     */ 
/*     */   
/*     */   protected void extractFile(File srcF, File dir, InputStream compressedInputStream, String entryName, Date entryDate, boolean isDirectory) throws Exception {
/* 153 */     File f = FileUtils.resolveFile(dir, entryName);
/*     */     
/*     */     try {
/* 156 */       if (!this.overwrite && f.exists() && f.lastModified() >= entryDate.getTime()) {
/*     */         return;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 164 */       File dirF = f.getParentFile();
/* 165 */       dirF.mkdirs();
/*     */       
/* 167 */       if (isDirectory) {
/*     */         
/* 169 */         f.mkdirs();
/*     */       }
/*     */       else {
/*     */         
/* 173 */         byte[] buffer = new byte[1024];
/* 174 */         int length = 0;
/* 175 */         FileOutputStream fos = null;
/*     */         
/*     */         try {
/* 178 */           fos = new FileOutputStream(f);
/*     */ 
/*     */           
/* 181 */           while ((length = compressedInputStream.read(buffer)) >= 0)
/*     */           {
/* 183 */             fos.write(buffer, 0, length);
/*     */           }
/*     */           
/* 186 */           fos.close();
/* 187 */           fos = null;
/*     */         }
/*     */         finally {
/*     */           
/* 191 */           if (fos != null) {
/*     */             
/*     */             try {
/*     */               
/* 195 */               fos.close();
/*     */             }
/* 197 */             catch (IOException e) {}
/*     */           }
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 204 */       f.setLastModified(entryDate.getTime());
/*     */     }
/* 206 */     catch (FileNotFoundException ex) {
/*     */       
/* 208 */       throw new Exception("Can't extract file " + srcF.getPath(), ex);
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
/*     */   public void setDest(File d) {
/* 221 */     this.dest = d;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSrc(File s) {
/* 231 */     this.source = s;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOverwrite(boolean b) {
/* 240 */     this.overwrite = b;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\Expand.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */