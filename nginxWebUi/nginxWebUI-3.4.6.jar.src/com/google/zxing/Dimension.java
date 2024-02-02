/*    */ package com.google.zxing;
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
/*    */ public final class Dimension
/*    */ {
/*    */   private final int width;
/*    */   private final int height;
/*    */   
/*    */   public Dimension(int width, int height) {
/* 28 */     if (width < 0 || height < 0) {
/* 29 */       throw new IllegalArgumentException();
/*    */     }
/* 31 */     this.width = width;
/* 32 */     this.height = height;
/*    */   }
/*    */   
/*    */   public int getWidth() {
/* 36 */     return this.width;
/*    */   }
/*    */   
/*    */   public int getHeight() {
/* 40 */     return this.height;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 45 */     if (other instanceof Dimension) {
/* 46 */       Dimension d = (Dimension)other;
/* 47 */       return (this.width == d.width && this.height == d.height);
/*    */     } 
/* 49 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 54 */     return this.width * 32713 + this.height;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 59 */     return this.width + "x" + this.height;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\Dimension.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */