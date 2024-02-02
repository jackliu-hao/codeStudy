/*    */ package com.sun.activation.viewers;
/*    */ 
/*    */ import java.awt.Canvas;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Image;
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
/*    */ public class ImageViewerCanvas
/*    */   extends Canvas
/*    */ {
/* 34 */   private Image canvas_image = null;
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
/*    */   public void setImage(Image new_image) {
/* 49 */     this.canvas_image = new_image;
/* 50 */     invalidate();
/* 51 */     repaint();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Dimension getPreferredSize() {
/* 59 */     Dimension d = null;
/*    */     
/* 61 */     if (this.canvas_image == null) {
/*    */       
/* 63 */       d = new Dimension(200, 200);
/*    */     } else {
/*    */       
/* 66 */       d = new Dimension(this.canvas_image.getWidth(this), this.canvas_image.getHeight(this));
/*    */     } 
/*    */     
/* 69 */     return d;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void paint(Graphics g) {
/* 77 */     if (this.canvas_image != null)
/* 78 */       g.drawImage(this.canvas_image, 0, 0, this); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\activation\viewers\ImageViewerCanvas.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */