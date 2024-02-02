/*    */ package io.undertow.security.api;
/*    */ 
/*    */ import io.undertow.security.idm.IdentityManager;
/*    */ import io.undertow.server.handlers.form.FormParserFactory;
/*    */ import java.util.Map;
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
/*    */ public interface AuthenticationMechanismFactory
/*    */ {
/*    */   public static final String REALM = "realm";
/*    */   public static final String LOGIN_PAGE = "login_page";
/*    */   public static final String ERROR_PAGE = "error_page";
/*    */   public static final String CONTEXT_PATH = "context_path";
/*    */   public static final String DEFAULT_PAGE = "default_page";
/*    */   public static final String OVERRIDE_INITIAL = "override_initial";
/*    */   
/*    */   @Deprecated
/*    */   default AuthenticationMechanism create(String mechanismName, FormParserFactory formParserFactory, Map<String, String> properties) {
/* 53 */     return null;
/*    */   }
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
/*    */   default AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
/* 66 */     return create(mechanismName, formParserFactory, properties);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\api\AuthenticationMechanismFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */