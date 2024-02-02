/*     */ package com.mysql.cj.sasl;
/*     */ 
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ import javax.security.auth.callback.Callback;
/*     */ import javax.security.auth.callback.CallbackHandler;
/*     */ import javax.security.auth.callback.NameCallback;
/*     */ import javax.security.auth.callback.PasswordCallback;
/*     */ import javax.security.sasl.SaslClient;
/*     */ import javax.security.sasl.SaslClientFactory;
/*     */ import javax.security.sasl.SaslException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ScramShaSaslClientFactory
/*     */   implements SaslClientFactory
/*     */ {
/*  50 */   private static final String[] SUPPORTED_MECHANISMS = new String[] { "MYSQLCJ-SCRAM-SHA-1", "MYSQLCJ-SCRAM-SHA-256" };
/*     */ 
/*     */ 
/*     */   
/*     */   public SaslClient createSaslClient(String[] mechanisms, String authorizationId, String protocol, String serverName, Map<String, ?> props, CallbackHandler cbh) throws SaslException {
/*  55 */     for (String mech : mechanisms) {
/*  56 */       if (mech.equals("MYSQLCJ-SCRAM-SHA-1")) {
/*  57 */         return new ScramSha1SaslClient(authorizationId, getUsername(mech, authorizationId, cbh), getPassword(mech, cbh));
/*     */       }
/*  59 */       if (mech.equals("MYSQLCJ-SCRAM-SHA-256")) {
/*  60 */         return new ScramSha256SaslClient(authorizationId, getUsername(mech, authorizationId, cbh), getPassword(mech, cbh));
/*     */       }
/*     */     } 
/*  63 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getMechanismNames(Map<String, ?> props) {
/*  68 */     return (String[])SUPPORTED_MECHANISMS.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getUsername(String prefix, String authorizationId, CallbackHandler cbh) throws SaslException {
/*  86 */     if (cbh == null) {
/*  87 */       throw new SaslException("Callback handler required to get username.");
/*     */     }
/*     */     
/*     */     try {
/*  91 */       String prompt = prefix + " authentication id:";
/*  92 */       NameCallback ncb = StringUtils.isNullOrEmpty(authorizationId) ? new NameCallback(prompt) : new NameCallback(prompt, authorizationId);
/*  93 */       cbh.handle(new Callback[] { ncb });
/*     */       
/*  95 */       String userName = ncb.getName();
/*  96 */       return userName;
/*  97 */     } catch (IOException|javax.security.auth.callback.UnsupportedCallbackException e) {
/*  98 */       throw new SaslException("Cannot get username", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getPassword(String prefix, CallbackHandler cbh) throws SaslException {
/* 115 */     if (cbh == null) {
/* 116 */       throw new SaslException("Callback handler required to get password.");
/*     */     }
/*     */     
/*     */     try {
/* 120 */       String prompt = prefix + " password:";
/* 121 */       PasswordCallback pcb = new PasswordCallback(prompt, false);
/* 122 */       cbh.handle(new Callback[] { pcb });
/*     */       
/* 124 */       String password = new String(pcb.getPassword());
/* 125 */       pcb.clearPassword();
/* 126 */       return password;
/* 127 */     } catch (IOException|javax.security.auth.callback.UnsupportedCallbackException e) {
/* 128 */       throw new SaslException("Cannot get password", e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\sasl\ScramShaSaslClientFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */