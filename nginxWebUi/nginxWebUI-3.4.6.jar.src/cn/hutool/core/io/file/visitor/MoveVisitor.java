/*    */ package cn.hutool.core.io.file.visitor;
/*    */ 
/*    */ import cn.hutool.core.io.file.PathUtil;
/*    */ import java.io.IOException;
/*    */ import java.nio.file.CopyOption;
/*    */ import java.nio.file.FileAlreadyExistsException;
/*    */ import java.nio.file.FileVisitResult;
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.SimpleFileVisitor;
/*    */ import java.nio.file.attribute.BasicFileAttributes;
/*    */ import java.nio.file.attribute.FileAttribute;
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
/*    */ public class MoveVisitor
/*    */   extends SimpleFileVisitor<Path>
/*    */ {
/*    */   private final Path source;
/*    */   private final Path target;
/*    */   private boolean isTargetCreated;
/*    */   private final CopyOption[] copyOptions;
/*    */   
/*    */   public MoveVisitor(Path source, Path target, CopyOption... copyOptions) {
/* 36 */     if (PathUtil.exists(target, false) && false == PathUtil.isDirectory(target)) {
/* 37 */       throw new IllegalArgumentException("Target must be a directory");
/*    */     }
/* 39 */     this.source = source;
/* 40 */     this.target = target;
/* 41 */     this.copyOptions = copyOptions;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
/* 47 */     initTarget();
/*    */     
/* 49 */     Path targetDir = this.target.resolve(this.source.relativize(dir));
/* 50 */     if (false == Files.exists(targetDir, new java.nio.file.LinkOption[0])) {
/* 51 */       Files.createDirectories(targetDir, (FileAttribute<?>[])new FileAttribute[0]);
/* 52 */     } else if (false == Files.isDirectory(targetDir, new java.nio.file.LinkOption[0])) {
/* 53 */       throw new FileAlreadyExistsException(targetDir.toString());
/*    */     } 
/* 55 */     return FileVisitResult.CONTINUE;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/* 61 */     initTarget();
/* 62 */     Files.move(file, this.target.resolve(this.source.relativize(file)), this.copyOptions);
/* 63 */     return FileVisitResult.CONTINUE;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void initTarget() {
/* 70 */     if (false == this.isTargetCreated) {
/* 71 */       PathUtil.mkdir(this.target);
/* 72 */       this.isTargetCreated = true;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\file\visitor\MoveVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */