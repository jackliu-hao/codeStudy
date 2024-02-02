/*    */ package cn.hutool.poi.word;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum PicType
/*    */ {
/* 12 */   EMF(2),
/* 13 */   WMF(3),
/* 14 */   PICT(4),
/* 15 */   JPEG(5),
/* 16 */   PNG(6),
/* 17 */   DIB(7),
/* 18 */   GIF(8),
/* 19 */   TIFF(9),
/* 20 */   EPS(10),
/* 21 */   WPG(12);
/*    */ 
/*    */   
/*    */   private final int value;
/*    */ 
/*    */ 
/*    */   
/*    */   PicType(int value) {
/* 29 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getValue() {
/* 40 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\word\PicType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */