/*    */ package com.github.jaiimageio.impl.plugins.raw;
/*    */ 
/*    */ import java.util.Locale;
/*    */ import javax.imageio.ImageWriteParam;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RawImageWriteParam
/*    */   extends ImageWriteParam
/*    */ {
/*    */   public RawImageWriteParam(Locale locale) {
/* 58 */     super(locale);
/* 59 */     this.canWriteTiles = true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\raw\RawImageWriteParam.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */