/*    */ package org.xnio;
/*    */ 
/*    */ import java.security.PrivilegedAction;
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
/*    */ public final class ReadPropertyAction
/*    */   implements PrivilegedAction<String>
/*    */ {
/*    */   private final String propertyName;
/*    */   private final String defaultValue;
/*    */   
/*    */   public ReadPropertyAction(String propertyName, String defaultValue) {
/* 39 */     this.propertyName = propertyName;
/* 40 */     this.defaultValue = defaultValue;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String run() {
/* 49 */     return System.getProperty(this.propertyName, this.defaultValue);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ReadPropertyAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */