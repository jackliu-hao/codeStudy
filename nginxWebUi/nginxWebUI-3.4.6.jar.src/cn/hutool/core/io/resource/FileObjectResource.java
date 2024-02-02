/*    */ package cn.hutool.core.io.resource;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
/*    */ import java.nio.charset.Charset;
/*    */ import javax.tools.FileObject;
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
/*    */ public class FileObjectResource
/*    */   implements Resource
/*    */ {
/*    */   private final FileObject fileObject;
/*    */   
/*    */   public FileObjectResource(FileObject fileObject) {
/* 30 */     this.fileObject = fileObject;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FileObject getFileObject() {
/* 39 */     return this.fileObject;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 44 */     return this.fileObject.getName();
/*    */   }
/*    */ 
/*    */   
/*    */   public URL getUrl() {
/*    */     try {
/* 50 */       return this.fileObject.toUri().toURL();
/* 51 */     } catch (MalformedURLException e) {
/* 52 */       return null;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public InputStream getStream() {
/*    */     try {
/* 59 */       return this.fileObject.openInputStream();
/* 60 */     } catch (IOException e) {
/* 61 */       throw new IORuntimeException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public BufferedReader getReader(Charset charset) {
/*    */     try {
/* 68 */       return IoUtil.getReader(this.fileObject.openReader(false));
/* 69 */     } catch (IOException e) {
/* 70 */       throw new IORuntimeException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\resource\FileObjectResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */