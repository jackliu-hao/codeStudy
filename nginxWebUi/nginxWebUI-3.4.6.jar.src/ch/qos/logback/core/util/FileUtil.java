/*     */ package ch.qos.logback.core.util;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.rolling.RolloverFailure;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileUtil
/*     */   extends ContextAwareBase
/*     */ {
/*     */   static final int BUF_SIZE = 32768;
/*     */   
/*     */   public FileUtil(Context context) {
/*  28 */     setContext(context);
/*     */   }
/*     */   
/*     */   public static URL fileToURL(File file) {
/*     */     try {
/*  33 */       return file.toURI().toURL();
/*  34 */     } catch (MalformedURLException e) {
/*  35 */       throw new RuntimeException("Unexpected exception on file [" + file + "]", e);
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
/*     */   public static boolean createMissingParentDirectories(File file) {
/*  49 */     File parent = file.getParentFile();
/*  50 */     if (parent == null)
/*     */     {
/*     */       
/*  53 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  58 */     parent.mkdirs();
/*  59 */     return parent.exists();
/*     */   }
/*     */   
/*     */   public String resourceAsString(ClassLoader classLoader, String resourceName) {
/*  63 */     URL url = classLoader.getResource(resourceName);
/*  64 */     if (url == null) {
/*  65 */       addError("Failed to find resource [" + resourceName + "]");
/*  66 */       return null;
/*     */     } 
/*     */     
/*  69 */     InputStreamReader isr = null;
/*     */     try {
/*  71 */       URLConnection urlConnection = url.openConnection();
/*  72 */       urlConnection.setUseCaches(false);
/*  73 */       isr = new InputStreamReader(urlConnection.getInputStream());
/*  74 */       char[] buf = new char[128];
/*  75 */       StringBuilder builder = new StringBuilder();
/*  76 */       int count = -1;
/*  77 */       while ((count = isr.read(buf, 0, buf.length)) != -1) {
/*  78 */         builder.append(buf, 0, count);
/*     */       }
/*  80 */       return builder.toString();
/*  81 */     } catch (IOException e) {
/*  82 */       addError("Failed to open " + resourceName, e);
/*     */     } finally {
/*  84 */       if (isr != null) {
/*     */         try {
/*  86 */           isr.close();
/*  87 */         } catch (IOException iOException) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  92 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void copy(String src, String destination) throws RolloverFailure {
/*  98 */     BufferedInputStream bis = null;
/*  99 */     BufferedOutputStream bos = null;
/*     */     try {
/* 101 */       bis = new BufferedInputStream(new FileInputStream(src));
/* 102 */       bos = new BufferedOutputStream(new FileOutputStream(destination));
/* 103 */       byte[] inbuf = new byte[32768];
/*     */       
/*     */       int n;
/* 106 */       while ((n = bis.read(inbuf)) != -1) {
/* 107 */         bos.write(inbuf, 0, n);
/*     */       }
/*     */       
/* 110 */       bis.close();
/* 111 */       bis = null;
/* 112 */       bos.close();
/* 113 */       bos = null;
/* 114 */     } catch (IOException ioe) {
/* 115 */       String msg = "Failed to copy [" + src + "] to [" + destination + "]";
/* 116 */       addError(msg, ioe);
/* 117 */       throw new RolloverFailure(msg);
/*     */     } finally {
/* 119 */       if (bis != null) {
/*     */         try {
/* 121 */           bis.close();
/* 122 */         } catch (IOException iOException) {}
/*     */       }
/*     */ 
/*     */       
/* 126 */       if (bos != null)
/*     */         try {
/* 128 */           bos.close();
/* 129 */         } catch (IOException iOException) {} 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\FileUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */