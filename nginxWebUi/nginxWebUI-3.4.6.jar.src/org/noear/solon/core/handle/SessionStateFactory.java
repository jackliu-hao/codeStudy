/*    */ package org.noear.solon.core.handle;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface SessionStateFactory
/*    */ {
/*    */   default int priority() {
/* 13 */     return 0;
/*    */   }
/*    */   
/*    */   SessionState create(Context paramContext);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\SessionStateFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */