/*     */ package cn.hutool.core.io.file.visitor;
/*     */ 
/*     */ import cn.hutool.core.io.file.PathUtil;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.FileAlreadyExistsException;
/*     */ import java.nio.file.FileVisitResult;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.SimpleFileVisitor;
/*     */ import java.nio.file.attribute.BasicFileAttributes;
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
/*     */ public class CopyVisitor
/*     */   extends SimpleFileVisitor<Path>
/*     */ {
/*     */   private final Path source;
/*     */   private final Path target;
/*     */   private final CopyOption[] copyOptions;
/*     */   private boolean isTargetCreated;
/*     */   
/*     */   public CopyVisitor(Path source, Path target, CopyOption... copyOptions) {
/*  43 */     if (PathUtil.exists(target, false) && false == PathUtil.isDirectory(target)) {
/*  44 */       throw new IllegalArgumentException("Target must be a directory");
/*     */     }
/*  46 */     this.source = source;
/*  47 */     this.target = target;
/*  48 */     this.copyOptions = copyOptions;
/*     */   }
/*     */ 
/*     */   
/*     */   public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
/*  53 */     initTargetDir();
/*     */     
/*  55 */     Path targetDir = resolveTarget(dir);
/*     */ 
/*     */     
/*     */     try {
/*  59 */       Files.copy(dir, targetDir, this.copyOptions);
/*  60 */     } catch (FileAlreadyExistsException e) {
/*  61 */       if (false == Files.isDirectory(targetDir, new java.nio.file.LinkOption[0]))
/*     */       {
/*  63 */         throw e;
/*     */       }
/*     */     } 
/*  66 */     return FileVisitResult.CONTINUE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/*  72 */     initTargetDir();
/*     */     
/*  74 */     Files.copy(file, resolveTarget(file), this.copyOptions);
/*  75 */     return FileVisitResult.CONTINUE;
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
/*     */   private Path resolveTarget(Path file) {
/*  91 */     return this.target.resolve(this.source.relativize(file));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void initTargetDir() {
/*  98 */     if (false == this.isTargetCreated) {
/*  99 */       PathUtil.mkdir(this.target);
/* 100 */       this.isTargetCreated = true;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\file\visitor\CopyVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */