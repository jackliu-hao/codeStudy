/*    */ package io.undertow.util;
/*    */ 
/*    */ import io.undertow.security.api.AuthenticationMechanism;
/*    */ import io.undertow.security.api.AuthenticationMechanismFactory;
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
/*    */ public class ImmediateAuthenticationMechanismFactory
/*    */   implements AuthenticationMechanismFactory
/*    */ {
/*    */   private final AuthenticationMechanism authenticationMechanism;
/*    */   
/*    */   public ImmediateAuthenticationMechanismFactory(AuthenticationMechanism authenticationMechanism) {
/* 37 */     this.authenticationMechanism = authenticationMechanism;
/*    */   }
/*    */ 
/*    */   
/*    */   public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
/* 42 */     return this.authenticationMechanism;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\ImmediateAuthenticationMechanismFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */