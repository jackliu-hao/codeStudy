/*    */ package cn.hutool.core.io.file;
/*    */ 
/*    */ import cn.hutool.core.io.FileUtil;
/*    */ import java.io.File;
/*    */ import java.io.Serializable;
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.StandardCharsets;
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
/*    */ public class FileWrapper
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected File file;
/*    */   protected Charset charset;
/* 24 */   public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FileWrapper(File file, Charset charset) {
/* 33 */     this.file = file;
/* 34 */     this.charset = charset;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public File getFile() {
/* 44 */     return this.file;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FileWrapper setFile(File file) {
/* 53 */     this.file = file;
/* 54 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Charset getCharset() {
/* 62 */     return this.charset;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FileWrapper setCharset(Charset charset) {
/* 71 */     this.charset = charset;
/* 72 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readableFileSize() {
/* 81 */     return FileUtil.readableFileSize(this.file.length());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\file\FileWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */