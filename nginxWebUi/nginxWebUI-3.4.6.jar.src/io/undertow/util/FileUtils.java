/*    */ package io.undertow.util;
/*    */ 
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.nio.file.FileVisitResult;
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.SimpleFileVisitor;
/*    */ import java.nio.file.attribute.BasicFileAttributes;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FileUtils
/*    */ {
/*    */   public static String readFile(Class<?> testClass, String fileName) {
/* 42 */     URL res = testClass.getResource(fileName);
/* 43 */     return readFile(res);
/*    */   }
/*    */   
/*    */   public static String readFile(URL url) {
/*    */     try {
/* 48 */       return readFile(url.openStream());
/* 49 */     } catch (IOException e) {
/* 50 */       throw new RuntimeException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String readFile(InputStream file) {
/* 58 */     try (BufferedInputStream stream = new BufferedInputStream(file)) {
/* 59 */       byte[] buff = new byte[1024];
/* 60 */       StringBuilder builder = new StringBuilder();
/*    */       int read;
/* 62 */       while ((read = stream.read(buff)) != -1) {
/* 63 */         builder.append(new String(buff, 0, read, StandardCharsets.UTF_8));
/*    */       }
/* 65 */       return builder.toString();
/* 66 */     } catch (IOException e) {
/* 67 */       throw new RuntimeException(e);
/*    */     } 
/*    */   }
/*    */   
/*    */   public static void deleteRecursive(Path directory) throws IOException {
/* 72 */     if (!Files.isDirectory(directory, new java.nio.file.LinkOption[0])) {
/*    */       return;
/*    */     }
/* 75 */     Files.walkFileTree(directory, new SimpleFileVisitor<Path>()
/*    */         {
/*    */           public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/*    */             try {
/* 79 */               Files.delete(file);
/* 80 */             } catch (IOException iOException) {}
/*    */ 
/*    */             
/* 83 */             return FileVisitResult.CONTINUE;
/*    */           }
/*    */ 
/*    */           
/*    */           public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
/*    */             try {
/* 89 */               Files.delete(dir);
/* 90 */             } catch (IOException iOException) {}
/*    */ 
/*    */             
/* 93 */             return FileVisitResult.CONTINUE;
/*    */           }
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\FileUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */