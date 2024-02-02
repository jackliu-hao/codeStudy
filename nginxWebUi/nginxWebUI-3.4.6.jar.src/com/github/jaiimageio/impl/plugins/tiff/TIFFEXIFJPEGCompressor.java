/*    */ package com.github.jaiimageio.impl.plugins.tiff;
/*    */ 
/*    */ import javax.imageio.ImageWriteParam;
/*    */ import javax.imageio.metadata.IIOMetadata;
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
/*    */ public class TIFFEXIFJPEGCompressor
/*    */   extends TIFFBaseJPEGCompressor
/*    */ {
/*    */   public TIFFEXIFJPEGCompressor(ImageWriteParam param) {
/* 57 */     super("EXIF JPEG", 6, false, param);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setMetadata(IIOMetadata metadata) {
/* 65 */     super.setMetadata(metadata);
/*    */ 
/*    */     
/* 68 */     initJPEGWriter(false, true);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFEXIFJPEGCompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */