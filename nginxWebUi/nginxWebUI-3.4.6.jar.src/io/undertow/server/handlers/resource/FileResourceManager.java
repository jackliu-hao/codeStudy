/*    */ package io.undertow.server.handlers.resource;
/*    */ 
/*    */ import io.undertow.UndertowMessages;
/*    */ import java.io.File;
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
/*    */ public class FileResourceManager
/*    */   extends PathResourceManager
/*    */ {
/*    */   public FileResourceManager(File base) {
/* 31 */     this(base, 1024L, true, false, null);
/*    */   }
/*    */   public FileResourceManager(File base, long transferMinSize) {
/* 34 */     this(base, transferMinSize, true, false, null);
/*    */   }
/*    */   
/*    */   public FileResourceManager(File base, long transferMinSize, boolean caseSensitive) {
/* 38 */     this(base, transferMinSize, caseSensitive, false, null);
/*    */   }
/*    */   
/*    */   public FileResourceManager(File base, long transferMinSize, boolean followLinks, String... safePaths) {
/* 42 */     this(base, transferMinSize, true, followLinks, safePaths);
/*    */   }
/*    */   
/*    */   protected FileResourceManager(long transferMinSize, boolean caseSensitive, boolean followLinks, String... safePaths) {
/* 46 */     super(transferMinSize, caseSensitive, followLinks, safePaths);
/*    */   }
/*    */   
/*    */   public FileResourceManager(File base, long transferMinSize, boolean caseSensitive, boolean followLinks, String... safePaths) {
/* 50 */     super(base.toPath(), transferMinSize, caseSensitive, followLinks, safePaths);
/*    */   }
/*    */   
/*    */   public File getBase() {
/* 54 */     return new File(this.base);
/*    */   }
/*    */   
/*    */   public FileResourceManager setBase(File base) {
/* 58 */     if (base == null) {
/* 59 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("base");
/*    */     }
/* 61 */     String basePath = base.getAbsolutePath();
/* 62 */     if (!basePath.endsWith("/")) {
/* 63 */       basePath = basePath + '/';
/*    */     }
/* 65 */     this.base = basePath;
/* 66 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\FileResourceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */