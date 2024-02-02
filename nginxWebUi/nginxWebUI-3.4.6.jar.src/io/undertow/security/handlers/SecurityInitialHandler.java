/*    */ package io.undertow.security.handlers;
/*    */ 
/*    */ import io.undertow.security.api.AuthenticationMode;
/*    */ import io.undertow.security.api.SecurityContext;
/*    */ import io.undertow.security.api.SecurityContextFactory;
/*    */ import io.undertow.security.idm.IdentityManager;
/*    */ import io.undertow.security.impl.SecurityContextFactoryImpl;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
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
/*    */ public class SecurityInitialHandler
/*    */   extends AbstractSecurityContextAssociationHandler
/*    */ {
/*    */   private final AuthenticationMode authenticationMode;
/*    */   private final IdentityManager identityManager;
/*    */   private final String programaticMechName;
/*    */   private final SecurityContextFactory contextFactory;
/*    */   
/*    */   public SecurityInitialHandler(AuthenticationMode authenticationMode, IdentityManager identityManager, String programaticMechName, SecurityContextFactory contextFactory, HttpHandler next) {
/* 51 */     super(next);
/* 52 */     this.authenticationMode = authenticationMode;
/* 53 */     this.identityManager = identityManager;
/* 54 */     this.programaticMechName = programaticMechName;
/* 55 */     this.contextFactory = contextFactory;
/*    */   }
/*    */ 
/*    */   
/*    */   public SecurityInitialHandler(AuthenticationMode authenticationMode, IdentityManager identityManager, String programaticMechName, HttpHandler next) {
/* 60 */     this(authenticationMode, identityManager, programaticMechName, SecurityContextFactoryImpl.INSTANCE, next);
/*    */   }
/*    */ 
/*    */   
/*    */   public SecurityInitialHandler(AuthenticationMode authenticationMode, IdentityManager identityManager, HttpHandler next) {
/* 65 */     this(authenticationMode, identityManager, null, SecurityContextFactoryImpl.INSTANCE, next);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SecurityContext createSecurityContext(HttpServerExchange exchange) {
/* 73 */     return this.contextFactory.createSecurityContext(exchange, this.authenticationMode, this.identityManager, this.programaticMechName);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\handlers\SecurityInitialHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */