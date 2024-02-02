/*    */ package org.noear.solon.core.handle;
/*    */ 
/*    */ import java.util.Collection;
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
/*    */ public interface SessionState
/*    */ {
/*    */   default void sessionRefresh() {}
/*    */   
/*    */   default void sessionPublish() {}
/*    */   
/*    */   default boolean replaceable() {
/* 30 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   String sessionId();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   String sessionChangeId();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   Collection<String> sessionKeys();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   Object sessionGet(String paramString);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void sessionSet(String paramString, Object paramObject);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void sessionRemove(String paramString);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void sessionClear();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void sessionReset();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default String sessionToken() {
/* 78 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\SessionState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */