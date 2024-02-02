/*    */ package com.github.jaiimageio.impl.common;
/*    */ 
/*    */ import java.awt.image.ColorModel;
/*    */ import java.awt.image.Raster;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SingleTileRenderedImage
/*    */   extends SimpleRenderedImage
/*    */ {
/*    */   Raster ras;
/*    */   
/*    */   public SingleTileRenderedImage(Raster ras, ColorModel colorModel) {
/* 67 */     this.ras = ras;
/*    */     
/* 69 */     this.tileGridXOffset = this.minX = ras.getMinX();
/* 70 */     this.tileGridYOffset = this.minY = ras.getMinY();
/* 71 */     this.tileWidth = this.width = ras.getWidth();
/* 72 */     this.tileHeight = this.height = ras.getHeight();
/* 73 */     this.sampleModel = ras.getSampleModel();
/* 74 */     this.colorModel = colorModel;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Raster getTile(int tileX, int tileY) {
/* 81 */     if (tileX != 0 || tileY != 0) {
/* 82 */       throw new IllegalArgumentException("tileX != 0 || tileY != 0");
/*    */     }
/* 84 */     return this.ras;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\common\SingleTileRenderedImage.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */