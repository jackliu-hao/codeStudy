/*    */ package cn.hutool.core.io.file;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import cn.hutool.core.map.MapUtil;
/*    */ import cn.hutool.core.util.CharsetUtil;
/*    */ import java.io.IOException;
/*    */ import java.net.URI;
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.file.FileSystem;
/*    */ import java.nio.file.FileSystems;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.Paths;
/*    */ import java.util.HashMap;
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
/*    */ public class FileSystemUtil
/*    */ {
/*    */   public static FileSystem create(String path) {
/*    */     try {
/* 34 */       return FileSystems.newFileSystem(
/* 35 */           Paths.get(path, new String[0]).toUri(), 
/* 36 */           MapUtil.of("create", "true"));
/* 37 */     } catch (IOException e) {
/* 38 */       throw new IORuntimeException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static FileSystem createZip(String path) {
/* 49 */     return createZip(path, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static FileSystem createZip(String path, Charset charset) {
/* 60 */     if (null == charset) {
/* 61 */       charset = CharsetUtil.CHARSET_UTF_8;
/*    */     }
/* 63 */     HashMap<String, String> env = new HashMap<>();
/* 64 */     env.put("create", "true");
/* 65 */     env.put("encoding", charset.name());
/*    */     
/*    */     try {
/* 68 */       return FileSystems.newFileSystem(
/* 69 */           URI.create("jar:" + Paths.get(path, new String[0]).toUri()), env);
/* 70 */     } catch (IOException e) {
/* 71 */       throw new IORuntimeException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Path getRoot(FileSystem fileSystem) {
/* 82 */     return fileSystem.getPath("/", new String[0]);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\file\FileSystemUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */