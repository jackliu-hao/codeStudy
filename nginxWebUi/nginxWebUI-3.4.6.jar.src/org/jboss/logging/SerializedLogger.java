/*    */ package org.jboss.logging;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ final class SerializedLogger
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 508779982439435831L;
/*    */   private final String name;
/*    */   
/*    */   SerializedLogger(String name) {
/* 30 */     this.name = name;
/*    */   }
/*    */   
/*    */   protected Object readResolve() {
/* 34 */     return Logger.getLogger(this.name);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\SerializedLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */