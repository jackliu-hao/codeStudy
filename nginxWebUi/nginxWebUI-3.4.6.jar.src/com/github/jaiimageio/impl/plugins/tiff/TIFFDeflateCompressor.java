/*    */ package com.github.jaiimageio.impl.plugins.tiff;
/*    */ 
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
/*    */ public class TIFFDeflateCompressor
/*    */   extends TIFFDeflater
/*    */ {
/*    */   public TIFFDeflateCompressor(ImageWriteParam param, int predictor) {
/* 56 */     super("Deflate", 32946, param, predictor);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFDeflateCompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */