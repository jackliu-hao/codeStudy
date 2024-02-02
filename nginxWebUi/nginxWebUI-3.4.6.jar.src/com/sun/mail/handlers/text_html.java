/*    */ package com.sun.mail.handlers;
/*    */ 
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
/*    */ 
/*    */ public class text_html
/*    */   extends text_plain
/*    */ {
/* 50 */   private static ActivationDataFlavor myDF = new ActivationDataFlavor(String.class, "text/html", "HTML String");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected ActivationDataFlavor getDF() {
/* 56 */     return myDF;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\handlers\text_html.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */