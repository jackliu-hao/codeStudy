/*    */ package com.sun.mail.handlers;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import javax.activation.ActivationDataFlavor;
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
/*    */ public class image_jpeg
/*    */   extends image_gif
/*    */ {
/* 50 */   private static ActivationDataFlavor myDF = new ActivationDataFlavor(Image.class, "image/jpeg", "JPEG Image");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected ActivationDataFlavor getDF() {
/* 56 */     return myDF;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\handlers\image_jpeg.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */