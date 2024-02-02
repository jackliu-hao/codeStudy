/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import java.io.File;
/*    */ import java.net.URI;
/*    */ import java.net.URL;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class URIConverter
/*    */   extends AbstractConverter<URI>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected URI convertInternal(Object value) {
/*    */     try {
/* 20 */       if (value instanceof File) {
/* 21 */         return ((File)value).toURI();
/*    */       }
/*    */       
/* 24 */       if (value instanceof URL) {
/* 25 */         return ((URL)value).toURI();
/*    */       }
/* 27 */       return new URI(convertToStr(value));
/* 28 */     } catch (Exception exception) {
/*    */ 
/*    */       
/* 31 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\URIConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */