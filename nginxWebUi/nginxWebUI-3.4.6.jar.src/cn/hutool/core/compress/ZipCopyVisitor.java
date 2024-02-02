/*    */ package cn.hutool.core.compress;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.io.IOException;
/*    */ import java.nio.file.CopyOption;
/*    */ import java.nio.file.FileAlreadyExistsException;
/*    */ import java.nio.file.FileSystem;
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
/*    */ public class ZipCopyVisitor
/*    */   extends SimpleFileVisitor<Path>
/*    */ {
/*    */   private final Path source;
/*    */   private final FileSystem fileSystem;
/*    */   private final CopyOption[] copyOptions;
/*    */   
/*    */   public ZipCopyVisitor(Path source, FileSystem fileSystem, CopyOption... copyOptions) {
/* 39 */     this.source = source;
/* 40 */     this.fileSystem = fileSystem;
/* 41 */     this.copyOptions = copyOptions;
/*    */   }
/*    */ 
/*    */   
/*    */   public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
/* 46 */     Path targetDir = resolveTarget(dir);
/* 47 */     if (StrUtil.isNotEmpty(targetDir.toString())) {
/*    */       
/*    */       try {
/* 50 */         Files.copy(dir, targetDir, this.copyOptions);
/* 51 */       } catch (FileAlreadyExistsException e) {
/* 52 */         if (false == Files.isDirectory(targetDir, new java.nio.file.LinkOption[0])) {
/* 53 */           throw e;
/*    */         }
/*    */       } 
/*    */     }
/*    */     
/* 58 */     return FileVisitResult.CONTINUE;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/* 64 */     Files.copy(file, resolveTarget(file), this.copyOptions);
/*    */     
/* 66 */     return FileVisitResult.CONTINUE;
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
/*    */ 
/*    */   
/*    */   private Path resolveTarget(Path file) {
/* 82 */     return this.fileSystem.getPath(this.source.relativize(file).toString(), new String[0]);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\compress\ZipCopyVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */