/*    */ package org.antlr.v4.runtime.misc;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TestRig
/*    */ {
/*    */   public static void main(String[] args) {
/*    */     try {
/* 14 */       Class<?> testRigClass = Class.forName("org.antlr.v4.gui.TestRig");
/* 15 */       System.err.println("Warning: TestRig moved to org.antlr.v4.gui.TestRig; calling automatically");
/*    */       try {
/* 17 */         Method mainMethod = testRigClass.getMethod("main", new Class[] { String[].class });
/* 18 */         mainMethod.invoke(null, new Object[] { args });
/*    */       }
/* 20 */       catch (Exception nsme) {
/* 21 */         System.err.println("Problems calling org.antlr.v4.gui.TestRig.main(args)");
/*    */       }
/*    */     
/* 24 */     } catch (ClassNotFoundException cnfe) {
/* 25 */       System.err.println("Use of TestRig now requires the use of the tool jar, antlr-4.X-complete.jar");
/* 26 */       System.err.println("Maven users need group ID org.antlr and artifact ID antlr4");
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\TestRig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */