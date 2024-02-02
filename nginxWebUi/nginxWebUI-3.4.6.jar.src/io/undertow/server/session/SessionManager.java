/*    */ package io.undertow.server.session;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.AttachmentKey;
/*    */ import java.util.Set;
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
/*    */ public interface SessionManager
/*    */ {
/* 40 */   public static final AttachmentKey<SessionManager> ATTACHMENT_KEY = AttachmentKey.create(SessionManager.class);
/*    */   
/*    */   String getDeploymentName();
/*    */   
/*    */   void start();
/*    */   
/*    */   void stop();
/*    */   
/*    */   Session createSession(HttpServerExchange paramHttpServerExchange, SessionConfig paramSessionConfig);
/*    */   
/*    */   Session getSession(HttpServerExchange paramHttpServerExchange, SessionConfig paramSessionConfig);
/*    */   
/*    */   Session getSession(String paramString);
/*    */   
/*    */   void registerSessionListener(SessionListener paramSessionListener);
/*    */   
/*    */   void removeSessionListener(SessionListener paramSessionListener);
/*    */   
/*    */   void setDefaultSessionTimeout(int paramInt);
/*    */   
/*    */   Set<String> getTransientSessions();
/*    */   
/*    */   Set<String> getActiveSessions();
/*    */   
/*    */   Set<String> getAllSessions();
/*    */   
/*    */   SessionManagerStatistics getStatistics();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\session\SessionManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */