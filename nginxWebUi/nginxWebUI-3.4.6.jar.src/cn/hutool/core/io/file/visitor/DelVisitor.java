/*    */ package cn.hutool.core.io.file.visitor;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ public class DelVisitor
/*    */   extends SimpleFileVisitor<Path>
/*    */ {
/* 18 */   public static DelVisitor INSTANCE = new DelVisitor();
/*    */ 
/*    */   
/*    */   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/* 22 */     Files.delete(file);
/* 23 */     return FileVisitResult.CONTINUE;
/*    */   }
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
/*    */   public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
/* 37 */     if (e == null) {
/* 38 */       Files.delete(dir);
/* 39 */       return FileVisitResult.CONTINUE;
/*    */     } 
/* 41 */     throw e;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\file\visitor\DelVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */