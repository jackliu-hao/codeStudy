/*    */ package com.sun.jna.platform.unix;
/*    */ 
/*    */ import com.sun.jna.Library;
/*    */ import com.sun.jna.Native;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface LibC
/*    */   extends LibCAPI, Library
/*    */ {
/*    */   public static final String NAME = "c";
/* 35 */   public static final LibC INSTANCE = (LibC)Native.load("c", LibC.class);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platfor\\unix\LibC.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */