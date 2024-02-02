/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import java.io.File;
/*    */ import java.net.URI;
/*    */ import java.net.URL;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.Paths;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PathConverter
/*    */   extends AbstractConverter<Path>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected Path convertInternal(Object value) {
/*    */     try {
/* 22 */       if (value instanceof URI) {
/* 23 */         return Paths.get((URI)value);
/*    */       }
/*    */       
/* 26 */       if (value instanceof URL) {
/* 27 */         return Paths.get(((URL)value).toURI());
/*    */       }
/*    */       
/* 30 */       if (value instanceof File) {
/* 31 */         return ((File)value).toPath();
/*    */       }
/*    */       
/* 34 */       return Paths.get(convertToStr(value), new String[0]);
/* 35 */     } catch (Exception exception) {
/*    */ 
/*    */       
/* 38 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\PathConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */